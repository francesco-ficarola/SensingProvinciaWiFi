package sensingprovinciawifi.core.send;
import java.util.Map;

import org.apache.log4j.Logger;

import sensingprovinciawifi.core.Data;


public class Forward implements Runnable {

	private static Logger logger = Logger.getLogger(Forward.class);
	
	private Data data;
	
	public Forward(Data d) 
	{
		this.data = d;
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
			
			Map<String, Object> values = data.get();
			
			if(values.isEmpty()) continue;
			
			try {

				logger.info("Forwarding...");
				Message msg = (Message) new JSONMessage(values);
				msg.send();
				
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}
