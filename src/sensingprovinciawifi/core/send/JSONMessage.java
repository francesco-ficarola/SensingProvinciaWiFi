package sensingprovinciawifi.core.send;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.Map;

import org.apache.log4j.Logger;

import sensingprovinciawifi.core.Connections;

import com.google.gson.Gson;


public class JSONMessage implements Message {
	
	private Logger logger = Logger.getLogger(getClass());

	private Map<String, Object> data;
	private HttpURLConnection connectionToDb;
	private Gson gson;
	
	public JSONMessage(Map<String, Object> values) throws IOException 
	{
		this.gson = new Gson();
		this.data = values;
		this.connectionToDb = Connections.connectToServer();
	}
	
	@Override
	public void send() throws Exception 
	{
		connectionToDb.setRequestProperty("Content-Type", "application/json");
		
		String json = gson.toJson(data);
		
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
