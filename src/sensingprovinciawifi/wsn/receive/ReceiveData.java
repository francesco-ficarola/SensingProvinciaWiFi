package sensingprovinciawifi.wsn.receive;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import sensingprovinciawifi.core.Data;


import net.tinyos.packet.*;
import net.tinyos.util.*;

public class ReceiveData implements Runnable {

	private static Logger logger = Logger.getLogger(ReceiveData.class);

	private PacketSource reader;
	private Data data;
	private String source;
	private Calendar gc;
	private SimpleDateFormat sdf;

	public ReceiveData(Data d, String source)
	{
		this.source = source;
		this.data = d;
		this.gc = GregorianCalendar.getInstance();
		this.sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		new Thread(this).start();
	}

	private void USBconnection(String source)
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
	}

	private void receive()
	{
		try {
			reader.open(PrintStreamMessenger.err);
			logger.info("Connection established with "+ reader.getName() +".");

			for (;;) 
			{
				byte[] packet = reader.readPacket();
				long timeMilli = System.currentTimeMillis();
				logger.info("WSN Raw Data Packet: " + Arrays.toString(packet));

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

				if(packet.length > 8) {			//check correct structure of the message
					if(packet.length >= 10)		//check first argument of the payload
					{
						int value = 256 * (int) packet[8] + (int) packet[9];
						gc.setTimeInMillis(timeMilli);
						data.put(sdf.format(gc.getTime()), value);
					}
				}
			}
		}
		catch (IOException e) {
			logger.error("Error on " + reader.getName() + ": " + e);
		}
	}

	@Override
	public void run() {
		USBconnection(source);
		receive();
	}
}

