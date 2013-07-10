package sensingprovinciawifi.send;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import sensingprovinciawifi.connect.Connect;

public class POSTMessage implements Message {

	private static HttpURLConnection connectionToDb;
	private static HashMap<String,Integer> data;	
	
	public POSTMessage(HashMap<String,Integer> d) throws IOException {data=d;}
	
	@Override
	public void send() throws Exception 
	{
		Set<String> time= data.keySet();
		Collection<Integer> value= data.values();
		
		Iterator<String> t= time.iterator();
		Iterator<Integer> v= value.iterator();
		
		while(t.hasNext())
		{
			connectionToDb=Connect.connectToServer();
			OutputStreamWriter forward = new OutputStreamWriter(connectionToDb.getOutputStream());
			
			String send=URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(t.next(), "UTF-8");
			send += "&" + URLEncoder.encode("value", "UTF-8") + "=" + URLEncoder.encode(v.next().toString(), "UTF-8");
			
			forward.write(send);
			forward.close();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connectionToDb.getInputStream()));
			String input="";
			
	        while ((input = in.readLine()) != null)
				System.out.println(input);
			
			in.close();
		}
	}

}
