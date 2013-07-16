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
	
	private boolean wifi;
	
	public PcsProxy(boolean wifi) {
		this.wifi = wifi;
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
				logger.debug("PCS Raw Data Packet: " + Arrays.toString(rawDataPacket));
				
				
				/**
				 * uint8_t (C) = short (java), uint16_t (C) = int (java), uint32_t (C) = long (java)
				 * Java needs double-sized primitives because they are all signed.
				 */
				
				/** First 16th bytes - Reader data */
				int eCrc = Functions.byteArraytoInt(new byte[] {0, 0, rawDataPacket[0], rawDataPacket[1]});
				short eProto = Functions.byteArraytoShort(new byte[] {0, rawDataPacket[2]});
				short eInterface = Functions.byteArraytoShort(new byte[] {0, rawDataPacket[3]});
				int eReaderID = Functions.byteArraytoInt(new byte[] {0, 0, rawDataPacket[4], rawDataPacket[5]});
				int eSize = Functions.byteArraytoInt(new byte[] {0, 0, rawDataPacket[6], rawDataPacket[7]});
				long eSequence = Functions.byteArraytoLong(new byte[] {0, 0, 0, 0, rawDataPacket[8], rawDataPacket[9], rawDataPacket[10], rawDataPacket[11]});
				long eTimestamp = Functions.byteArraytoLong(new byte[] {0, 0, 0, 0, rawDataPacket[12], rawDataPacket[13], rawDataPacket[14], rawDataPacket[15]});
				logger.info("Reader fields: " + eCrc + ", " + eProto + ", " + eInterface + ", " + eReaderID + ", " + eSize + ", " + eSequence + ", " + eTimestamp);
				
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
				 */ 
				short proto = Functions.byteArraytoShort(new byte[] {0, decryptedPayload[0]});
				long time = Functions.byteArraytoLong(new byte[] {0, 0, 0, 0, decryptedPayload[1], decryptedPayload[2], decryptedPayload[3], decryptedPayload[4]});
				long seq = Functions.byteArraytoLong(new byte[] {0, 0, 0, 0, decryptedPayload[5], decryptedPayload[6], decryptedPayload[7], decryptedPayload[8]});
				int from = Functions.byteArraytoInt(new byte[] {0, 0, decryptedPayload[9], decryptedPayload[10]});
				int data = Functions.byteArraytoInt(new byte[] {0, 0, decryptedPayload[11], decryptedPayload[12]});
				short prop = Functions.byteArraytoShort(new byte[] {0, decryptedPayload[13]});
				int crc = Functions.byteArraytoInt(new byte[] {0, 0, decryptedPayload[14], decryptedPayload[15]});
				
				byte[] payloadWithoutCrc = Arrays.copyOfRange(decryptedPayload, 0, 14);
				if(Functions.crc16(payloadWithoutCrc) == crc) {
					logger.info("Payload fields: " + proto + ", " + time + ", " + Long.toHexString(seq).toUpperCase() + ", " + Integer.toHexString(from).toUpperCase() + ", " + data + ", " + prop + ", " + crc + "\n");
					
					if(wifi) {
						while(!WifiConnection.connectToWifi());
					}
					d.put(from, String.valueOf(time));
					
				} else{
					logger.warn("Rejecting packet from" + eReaderID + "on CRC.\n");
				}
				
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}
