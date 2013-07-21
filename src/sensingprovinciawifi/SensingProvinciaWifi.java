package sensingprovinciawifi;

import java.io.IOException;

import org.apache.log4j.Logger;

import sensingprovinciawifi.core.Connections;
import sensingprovinciawifi.pcs.PcsProxy;
import sensingprovinciawifi.wsn.WsnProxy;

public class SensingProvinciaWifi {

	private static Logger logger = Logger.getLogger(SensingProvinciaWifi.class);
	private static boolean wifi = false;
	private static String source = "serial@/dev/ttyUSB0:telos";
	
	public static void main(String[] args) {
		
		if(args.length > 0) {
			parseArgs(args);
		}
		
		if(wifi) {
			logger.info("Connection to captiveportal.provinciawifi.it ...");
			try {
				Connections.connectToWifi();
				logger.info("Connection established!");
			} catch (IOException e) {
				logger.error("Connection failed: " + e.getMessage(), e);
			}
		}
		
		logger.info("WSN Proxy initialization ...");
		new WsnProxy(wifi, source);
		
		logger.info("PCS Proxy initialization ...");
		new PcsProxy(wifi);
		
	}

	private static void parseArgs(String[] args) {
		for(int i=0; i<args.length; i++) {
			
			if(args[i].equals("-wifi")) {
				wifi = true;
			}
			
			else
			
			if(args[i].equals("-comm")) {
				if(args.length > i+1 && args[i+1].matches("\\.+")) {
					source = args[i+1];
				}
			}
			
			else
			
			if(args[i].equals("-h") || args[i].equals("--help")) {
				usage();
				System.exit(0);
			}
			
			else
			
			if(args[i].matches(".+")) {
				;
			}
			
			else {
				usage();
				System.exit(0);
			}
		}
	}
	
	
	private static void usage() {
		System.out.println("\nTARGET SPECIFICATION:");
		System.out.println("-h or --help: Getting this help.");
		System.out.println("-wifi: If you need to connect to the ProvinciaWiFi AP.");
		System.out.println("-comm <source>: The TelosB serial port [default: serial@/dev/ttyUSB0:telos].\n");
	}

}
