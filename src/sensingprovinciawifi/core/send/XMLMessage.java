package sensingprovinciawifi.core.send;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sensingprovinciawifi.core.WifiConnection;

public class XMLMessage implements Message {

	private Logger logger = Logger.getLogger(getClass());
	
	private Map<String,Integer> data;
	private HttpURLConnection connectionToDb;
	private File f;
	
	public XMLMessage(Map<String, Integer>  values) throws IOException
	{
		this.f = new File("xmldata.xml");
		this.data = values;
		this.connectionToDb = WifiConnection.connectToServer();
		createxml();
	}
	
	private void createxml()
	{
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		
		try {
			builder = dbfactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error(e.getMessage(), e);
		}
		
		Document document = builder.newDocument();
		Element rootnode = document.createElement("senseddata");
		document.appendChild(rootnode);
		
		Iterator<String> key= data.keySet().iterator();
		while(key.hasNext())
		{
			String str= key.next();
			
			Element datanode = document.createElement("data");
			rootnode.appendChild(datanode);
			
			Element timenode = document.createElement("time");
			timenode.appendChild(document.createTextNode(str));
			datanode.appendChild(timenode);
			
			Element valuenode= document.createElement("value");
			valuenode.appendChild(document.createTextNode(data.get(str).toString()));
			datanode.appendChild(valuenode);
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			logger.error(e.getMessage(), e);
		}
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(f);
		
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void send() throws Exception 
	{
		connectionToDb.setRequestProperty("Content-Type", "application/xml");

		OutputStream forward = connectionToDb.getOutputStream();
		
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		FileReader fileReader = new FileReader(f);
		StreamSource source = new StreamSource(fileReader);
		StreamResult result = new StreamResult(forward);
		transformer.transform(source, result);

		forward.flush();
		forward.close();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(connectionToDb.getInputStream()));
		String input="";
		
        while ((input = in.readLine()) != null)
			logger.info(input);
		
		in.close();
	}

	
}
