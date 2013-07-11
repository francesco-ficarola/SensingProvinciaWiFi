package sensingprovinciawifi.pcs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

import org.apache.log4j.Logger;

import sensingprovinciawifi.core.WifiConnection;
import sensingprovinciawifi.pcs.utils.PcsConstants;

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
		while(true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				serverSocket.receive(receivePacket);
				byte[] data = receivePacket.getData();
				logger.info(Arrays.toString(data));
				
				//TODO: byte array parsing
				
				//TODO: sending data
				while(!WifiConnection.connectToWifi());
				
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}
