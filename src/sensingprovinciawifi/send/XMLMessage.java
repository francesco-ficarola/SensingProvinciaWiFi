package sensingprovinciawifi.send;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Iterator;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sensingprovinciawifi.connect.Connect;

public class XMLMessage implements Message {

	private static HashMap<String,Integer> data;
	private static HttpURLConnection connectionToDb;
	private static File f=new File("xmldata.xml");
	
	public XMLMessage(HashMap<String,Integer> d) throws IOException
	{
		data=d;
		connectionToDb= Connect.connectToServer();
		createxml();
	}
	
	private static void createxml()
	{
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		
		try {
			builder = dbfactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			e.printStackTrace();
		}
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(f);
		
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
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
			System.out.println(input);
		
		in.close();
	}

	
}
