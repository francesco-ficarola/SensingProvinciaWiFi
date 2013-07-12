package sensingprovinciawifi.core;
import java.util.HashMap;

import org.apache.log4j.Logger;

import sensingprovinciawifi.wsn.receive.Data;
import sensingprovinciawifi.wsn.send.JSONMessage;
import sensingprovinciawifi.wsn.send.Message;


public class Forward implements Runnable {

	private static Logger logger = Logger.getLogger(Forward.class);
	
	private static Data data;
	
	public Forward(Data d) 
	{
		data=d;
		new Thread(this).start();
	}

	@Override
	public void run()
	{
		while(true)
		{
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			
			HashMap<String,Integer> values= data.get();
			
			if(values.isEmpty()) continue;
			
			try {

				Message msg= (Message) new JSONMessage(values);
				
				msg.send();
				
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}
