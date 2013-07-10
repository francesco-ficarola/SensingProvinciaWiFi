package sensingprovinciawifi;

import java.io.IOException;

import org.apache.log4j.Logger;

import sensingprovinciawifi.core.WifiConnection;
import sensingprovinciawifi.pcs.PcsProxy;
import sensingprovinciawifi.wsn.WsnProxy;

public class SensingProvinciaWifi {

	private static Logger logger = Logger.getLogger(SensingProvinciaWifi.class);
	
	public static void main(String[] args) {
		
		logger.info("Connection to captiveportal.provinciawifi.it ...");
		try {
			WifiConnection.connectToWifi();
			logger.info("Connection established!");
		} catch (IOException e) {
			logger.error("Connection failed: " + e.getMessage(), e);
		}
		
		logger.info("WSN Proxy initialization ...");
		new WsnProxy(args);
		
		logger.info("PCS Proxy initialization ...");
		new PcsProxy();
		
		//System.out.println("Connected to wifi");
		//create an interface listening the default port instantiated by MOTECOM
	}

}
