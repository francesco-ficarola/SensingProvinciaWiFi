package sensingprovinciawifi.pcs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
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
				logger.info(Arrays.toString(rawDataPacket));
				
				// First 16th bytes - Reader data
				short eCrc = Functions.byteArraytoShort(new byte[] {rawDataPacket[0], rawDataPacket[1]});
				byte eProto = rawDataPacket[2];
				byte eInterface = rawDataPacket[3];
				short eReader_id = Functions.byteArraytoShort(new byte[] {rawDataPacket[4], rawDataPacket[5]});
				short eSize = Functions.byteArraytoShort(new byte[] {rawDataPacket[6], rawDataPacket[7]});
				int eSequence = Functions.byteArraytoInt(new byte[] {rawDataPacket[8], rawDataPacket[9], rawDataPacket[10], rawDataPacket[11]});
				int eTimestamp = Functions.byteArraytoInt(new byte[] {rawDataPacket[12], rawDataPacket[13], rawDataPacket[14], rawDataPacket[15]});
				logger.info(eCrc + ", " + eProto + ", " + eInterface + ", " + eReader_id + ", " + eSize + ", " + eSequence + ", " + eTimestamp);
				
				// Second 16th bytes - Payload encrypted by XXTEA
				byte[] encryptedPayload = Arrays.copyOfRange(rawDataPacket, 16, rawDataPacket.length);
				
//				ByteBuffer byteBuffer = ByteBuffer.allocate(PcsConstants.XXTEA_KEY.length * 4);
//				IntBuffer intBuffer = byteBuffer.asIntBuffer();
//				int[] intKey = new int[4];
//				for(int i=0; i<4; i++) {
//					intKey[i] = (int)PcsConstants.XXTEA_KEY[i];
//				}
//				
//				intBuffer.put(intKey);
//				byte[] key = byteBuffer.array();
//				byte[] decryptedPayload = XXTEA.decrypt(encryptedPayload, key);
				
				byte[] decryptedPayload = encryptedPayload;
				logger.info(Arrays.toString(decryptedPayload));
				
				byte proto = decryptedPayload[0];
				int time = Functions.byteArraytoInt(new byte[] {decryptedPayload[1], decryptedPayload[2], decryptedPayload[3], decryptedPayload[4]});
				int seq = Functions.byteArraytoInt(new byte[] {decryptedPayload[5], decryptedPayload[6], decryptedPayload[7], decryptedPayload[8]});
				short from = Functions.byteArraytoShort(new byte[] {decryptedPayload[9], decryptedPayload[10]});
				short data = Functions.byteArraytoShort(new byte[] {decryptedPayload[11], decryptedPayload[12]});
				byte prop = decryptedPayload[13];
				short crc = Functions.byteArraytoShort(new byte[] {decryptedPayload[14], decryptedPayload[15]});
				logger.info(proto + ", " + time + ", " + seq + ", " + from + ", " + data + ", " + prop + ", " + crc);
				
//				while(!WifiConnection.connectToWifi());
				
				d.put(seq, String.valueOf(time));
				
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}
