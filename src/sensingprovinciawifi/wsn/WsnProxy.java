package sensingprovinciawifi.wsn;

import java.io.*;

import org.apache.log4j.Logger;

import sensingprovinciawifi.core.Forward;
import sensingprovinciawifi.core.WifiConnection;
import sensingprovinciawifi.wsn.receive.Data;
import sensingprovinciawifi.wsn.receive.ReceiveData;


public class WsnProxy implements Runnable {
	
	private static Logger logger = Logger.getLogger(WsnProxy.class);
	private static boolean state;
	
	private boolean wifi;
	private String source;
	
	public WsnProxy(boolean wifi, String source)
	{
		this.wifi = wifi;
		this.source = source;
		state=false;
		new Thread(this).start();
	}

	@Override
	public void run() {
		
		while(true)
		{
			try {
				
				if(wifi) {
					while(!WifiConnection.connectToWifi());
				}
				
				if(!state)
				{
					state=true;
					Data d= new Data();
					new ReceiveData(d, source);
					new Forward(d);
				}
				
				Thread.sleep(6000);		//every 6 seconds check the connectivity
				
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			
		}
	}
	
}
