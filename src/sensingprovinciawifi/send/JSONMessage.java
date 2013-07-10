package sensingprovinciawifi.send;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.HashMap;

import sensingprovinciawifi.connect.Connect;

import com.google.gson.Gson;


public class JSONMessage implements Message {

	private HashMap<String,Integer> data;
	private static HttpURLConnection connectionToDb;
	
	public JSONMessage(HashMap<String,Integer> d) throws IOException 
	{
		data=d;
		connectionToDb=Connect.connectToServer();
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
			System.out.println(input);
		
		in.close();
	}

}
