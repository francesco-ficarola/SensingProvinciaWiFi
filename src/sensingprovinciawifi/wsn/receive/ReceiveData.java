package sensingprovinciawifi.wsn.receive;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import net.tinyos.packet.*;
import net.tinyos.util.*;

public class ReceiveData implements Runnable {

	private static Logger logger = Logger.getLogger(ReceiveData.class);
	
	private static PacketSource reader;
	private static Data data;
	private String[] param;
	
	public ReceiveData(Data d, String[] args)
	{
		data=d;
		param=args;
		new Thread(this).start();
	}
	
	public static void USBconnection(String source)
	{
		if (source == null) 
			reader = BuildSource.makePacketSource();
		else 
			reader = BuildSource.makePacketSource(source);
		
		if (reader == null) 
		{
		    logger.error("Invalid packet source (check your MOTECOM environment variable)");
		    System.exit(0);
		}
		else
			logger.error("Connection established with "+ reader.getName() +".");
	}
	
	public static void receive()
	{
		try {
			  reader.open(PrintStreamMessenger.err);
			  for (;;) 
			  {
			    byte[] packet = reader.readPacket();
			    
			    /* * * * * * * * * * * * * * * * * * * * * * * * */
				/* 												 */
				/*  arr[1]-arr[2] 	destination					 */
				/*  arr[3]-arr[4] 	link source (default 00 01)	 */
				/*  arr[5]			length						 */
				/*  arr[6]			group ID (default 22)		 */
				/*  arr[7]			handler ID (default 06)		 */
				/*  											 */
				/*  then payload.								 */
				/* 												 */
				/* * * * * * * * * * * * * * * * * * * * * * * * */
			    
			    if(packet.length > 8)			//check correct structure of the message
					if(packet.length >=10)		//check first argument of the payload
					{
						GregorianCalendar gc = new GregorianCalendar();
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					    int value= 256* (int) packet[8] + (int) packet[9];
					    data.put(value,sdf.format(gc.getTime()));
					}
			  }
			}
			catch (IOException e) {
				logger.error("Error on " + reader.getName() + ": " + e);
			}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		USBconnection(param[0]);
		receive();
		
	}
}

