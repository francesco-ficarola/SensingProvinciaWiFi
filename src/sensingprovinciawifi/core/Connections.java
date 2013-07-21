package sensingprovinciawifi.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

public class Connections {
	
	private static Logger logger = Logger.getLogger(Connections.class);
	
	public static boolean connectToWifi() throws IOException 
	{
		//setting up a connection to captiveportal.provinciawifi.it
		
        URL captive = null;
        URLConnection connected = null;
		try {
			//captive = new URL("https://captiveportal.provinciawifi.it:8081/login");
			captive = new URL("https://captiveportal.provinciawifi.it:8001/");
			connected = captive.openConnection();
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
		}
		
		connected.setDoOutput(true);
		//if the site allows outputstream OK, otherwise throw an exception
		OutputStreamWriter out = new OutputStreamWriter(connected.getOutputStream());
		
		//writing the necessary data for the login
		String data = URLEncoder.encode("auth_user", "UTF-8") + "=" + URLEncoder.encode("3402528296", "UTF-8");
		data += "&" + URLEncoder.encode("auth_pass", "UTF-8") + "=" + URLEncoder.encode("IlCapitano89", "UTF-8");
		data += "&" + URLEncoder.encode("rediurl", "UTF-8") + "=" + 
			URLEncoder.encode("https://wasp.provinciawifi.it/captiveportal/?q=spot/0&redirect=http%3A%2F%2Fwww.provincia.roma.it%2F", "UTF-8");
		
		//sending the login
		out.write(data);
        out.close();
		
        BufferedReader in = new BufferedReader(new InputStreamReader(connected.getInputStream()));

        //checking the result
        String input;
        while ((input = in.readLine()) != null)
        {
        	if(input.contains("Success"))
        	{
        		logger.info("Connected to Wifi.");
        		in.close();
        		return true;
        	}
        }
        
        return false;
	}
	
	public static HttpURLConnection connectionToDb; 
	
	public static HttpURLConnection connectToServer() throws IOException 
	{
		URL db= null;
        connectionToDb = null;
        
        try {
        	db = new URL("http://sensingprovinciawifi.appspot.com/server");
			 connectionToDb = (HttpURLConnection) db.openConnection();
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
		}
        
      //if the site allows outputstream ok, otherwise it throws an exception
        connectionToDb.setDoOutput(true);
        
        logger.info("Connected to the server.");
        
        return connectionToDb;
	}
}
