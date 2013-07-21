package sensingprovinciawifi.core.send;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import sensingprovinciawifi.core.Connections;

public class POSTMessage implements Message {
	
	private Logger logger = Logger.getLogger(getClass());

	private HttpURLConnection connectionToDb;
	private Map<String, Object> data;	
	
	public POSTMessage(Map<String, Object> values) throws IOException {
		this.data = values;
	}
	
	@Override
	public void send() throws Exception 
	{
		Set<String> time= data.keySet();
		Collection<Object> value= data.values();
		
		Iterator<String> t = time.iterator();
		Iterator<Object> v = value.iterator();
		
		while(t.hasNext())
		{
			connectionToDb=Connections.connectToServer();
			OutputStreamWriter forward = new OutputStreamWriter(connectionToDb.getOutputStream());
			
			String send=URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(t.next(), "UTF-8");
			send += "&" + URLEncoder.encode("value", "UTF-8") + "=" + URLEncoder.encode(v.next().toString(), "UTF-8");
			
			forward.write(send);
			forward.close();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connectionToDb.getInputStream()));
			String input="";
			
	        while ((input = in.readLine()) != null)
	        	logger.info(input);
			
			in.close();
		}
	}

}
