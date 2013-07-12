package sensingprovinciawifi.pcs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.log4j.Logger;

import sensingprovinciawifi.core.Forward;
import sensingprovinciawifi.core.WifiConnection;
import sensingprovinciawifi.pcs.utils.Functions;
import sensingprovinciawifi.pcs.utils.PcsConstants;
import sensingprovinciawifi.pcs.utils.XXTEA;
import sensingprovinciawifi.wsn.receive.Data;

public class PcsProxy implements Runnable {
	
	private static Logger logger = Logger.getLogger(PcsProxy.class);
	
	public PcsProxy() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		
		boolean initialized = false;
		DatagramSocket serverSocket = null;
		
		while(!initialized) {
			try {
				serverSocket = new DatagramSocket(PcsConstants.UDP_SERVER_PORT);
				initialized = true;
				logger.info("PCS UDP Server has been initialized.");
			} catch (SocketException e) {
				logger.error("PCS UDP Server has not been initialized: " + e.getMessage(), e);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		}
		
		byte[] receiveData = new byte[PcsConstants.PCS_PACKET_SIZE];
		Data d = new Data();
		new Forward(d);
		
		while(true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				serverSocket.receive(receivePacket);
				byte[] rawDataPacket = receivePacket.getData();
				logger.info("Raw Packet: " + Arrays.toString(rawDataPacket));
				
				/** First 16th bytes - Reader data */
				short eCrc = Functions.byteArraytoShort(new byte[] {rawDataPacket[0], rawDataPacket[1]});
				byte eProto = rawDataPacket[2];
				byte eInterface = rawDataPacket[3];
				short eReader_id = Functions.byteArraytoShort(new byte[] {rawDataPacket[4], rawDataPacket[5]});
				short eSize = Functions.byteArraytoShort(new byte[] {rawDataPacket[6], rawDataPacket[7]});
				int eSequence = Functions.byteArraytoInt(new byte[] {rawDataPacket[8], rawDataPacket[9], rawDataPacket[10], rawDataPacket[11]});
				int eTimestamp = Functions.byteArraytoInt(new byte[] {rawDataPacket[12], rawDataPacket[13], rawDataPacket[14], rawDataPacket[15]});
				logger.debug("Reader fields: " + eCrc + ", " + eProto + ", " + eInterface + ", " + eReader_id + ", " + eSize + ", " + eSequence + ", " + eTimestamp);
				
				/** Second 16th bytes - Payload encrypted by XXTEA */
				byte[] encryptedPayload = Arrays.copyOfRange(rawDataPacket, 16, rawDataPacket.length);
				logger.info("Encrypted Payload: " + Arrays.toString(encryptedPayload));
				ByteBuffer byteBuffer = ByteBuffer.allocate(PcsConstants.XXTEA_KEY.length * 4);
				for(int i=0; i<4; i++) {
					byteBuffer.putInt(PcsConstants.XXTEA_KEY[i]);
				}
				byte[] key = byteBuffer.array();
				byte[] decryptedPayload = XXTEA.decrypt(encryptedPayload, key);				
				logger.info("Decrypted payload: " + Arrays.toString(decryptedPayload));
				
				
				/**
				 * DTN Message:
				 * uint8_t proto;
				 * uint32_t time;
				 * uint32_t seq;
				 * uint16_t from;
				 * uint16_t data;
				 * uint8_t prop;
				 * uint16_t crc;
				 * 
				 * uint8_t (C) = short (java), uint16_t (C) = int (java), uint32_t (C) = long (java)
				 * Java needs double-sized primitives because they are all signed.
				 */ 
				
				short proto = Functions.byteArraytoShort(new byte[] {0, decryptedPayload[0]});
				long time = Functions.byteArraytoLong(new byte[] {0, 0, 0, 0, decryptedPayload[1], decryptedPayload[2], decryptedPayload[3], decryptedPayload[4]});
				long seq = Functions.byteArraytoLong(new byte[] {0, 0, 0, 0, decryptedPayload[5], decryptedPayload[6], decryptedPayload[7], decryptedPayload[8]});
				int from = Functions.byteArraytoInt(new byte[] {0, 0, decryptedPayload[9], decryptedPayload[10]});
				int data = Functions.byteArraytoInt(new byte[] {0, 0, decryptedPayload[11], decryptedPayload[12]});
				short prop = Functions.byteArraytoShort(new byte[] {0, decryptedPayload[13]});
				int crc = Functions.byteArraytoInt(new byte[] {0, 0, decryptedPayload[14], decryptedPayload[15]});
				logger.info("Payload fields: " + proto + ", " + time + ", " + seq + ", " + from + ", " + data + ", " + prop + ", " + crc);
				
				while(!WifiConnection.connectToWifi());
				d.put(data, String.valueOf(time));
				
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}
