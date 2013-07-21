package sensingprovinciawifi.core;
import java.util.HashMap;
import java.util.Map;


public class Data {

	private Map<String, Integer> data;

	public Data()
	{
		data = new HashMap<String, Integer>();
	}
	
	synchronized public void put(String time, int value)
	{
		data.put(time, value);
	}
	
	synchronized public Map<String, Integer> get()
	{
		Map<String,Integer> ret = new HashMap<String, Integer>(data);
		data.clear();
		return ret;
	}
}
