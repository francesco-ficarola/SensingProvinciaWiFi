package sensingprovinciawifi.wsn.send;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.HashMap;

import org.apache.log4j.Logger;

import sensingprovinciawifi.core.WifiConnection;

import com.google.gson.Gson;


public class JSONMessage implements Message {
	
	private Logger logger = Logger.getLogger(getClass());

	private HashMap<String,Integer> data;
	private static HttpURLConnection connectionToDb;
	
	public JSONMessage(HashMap<String,Integer> d) throws IOException 
	{
		data=d;
		connectionToDb=WifiConnection.connectToServer();
	}
	
	@Override
	public void send() throws Exception 
	{
		connectionToDb.setRequestProperty("Content-Type", "application/json");
		
		String json = new Gson().toJson(data);
		
		OutputStreamWriter forward = new OutputStreamWriter(connectionToDb.getOutputStream());
		forward.write(json);
		forward.close();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(connectionToDb.getInputStream()));
		String input="";
		
        while ((input = in.readLine()) != null)
			logger.info(input);
		
		in.close();
	}

}
