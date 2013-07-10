package sensingprovinciawifi.send;
import java.util.HashMap;

import sensingprovinciawifi.receive.Data;


public class Forward implements Runnable {

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
				e.printStackTrace();
			}
			
			HashMap<String,Integer> values= data.get();
			
			if(values.isEmpty()) continue;
			
			try {

				Message msg= (Message) new POSTMessage(values);
				
				msg.send();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
