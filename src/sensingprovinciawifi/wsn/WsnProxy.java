package sensingprovinciawifi.wsn;

import java.io.*;

import org.apache.log4j.Logger;

import sensingprovinciawifi.core.Forward;
import sensingprovinciawifi.core.WifiConnection;
import sensingprovinciawifi.wsn.receive.Data;
import sensingprovinciawifi.wsn.receive.ReceiveData;


public class WsnProxy implements Runnable {
	
	private static Logger logger = Logger.getLogger(WsnProxy.class);

	private String[] param;
	private static boolean state;
	
	public WsnProxy(String[] args)
	{
		state=false;
		param=args;
		new Thread(this).start();
	}

	@Override
	public void run() {
		
		while(true)
		{
			try {
				while(!WifiConnection.connectToWifi());
				if(!state)
				{
					state=true;
					Data d= new Data();
					new ReceiveData(d, param);
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
