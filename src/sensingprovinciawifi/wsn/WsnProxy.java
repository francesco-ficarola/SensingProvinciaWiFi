package sensingprovinciawifi.wsn;

import java.io.*;

import org.apache.log4j.Logger;

import sensingprovinciawifi.core.Forward;
import sensingprovinciawifi.core.WifiConnection;
import sensingprovinciawifi.wsn.receive.Data;
import sensingprovinciawifi.wsn.receive.ReceiveData;


public class WsnProxy implements Runnable {
	
	private static Logger logger = Logger.getLogger(WsnProxy.class);
	
	private boolean wifi;
	private String source;
	
	public WsnProxy(boolean wifi, String source)
	{
		this.wifi = wifi;
		this.source = source;
		new Thread(this).start();
	}

	@Override
	public void run() {
		
		Data d = new Data();
		new ReceiveData(d, source);
		new Forward(d);
		
		while(true)
		{
			try {
				
				Thread.sleep(6000);		//every 6 seconds check the connectivity
				
				if(wifi) {
					while(!WifiConnection.connectToWifi());
				}
				
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			
		}
	}
	
}
