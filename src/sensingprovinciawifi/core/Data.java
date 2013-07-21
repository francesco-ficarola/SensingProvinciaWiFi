package sensingprovinciawifi.core;
import java.util.HashMap;
import java.util.Map;


public class Data {

	private Map<String, Object> data;

	public Data()
	{
		data = new HashMap<String, Object>();
	}
	
	synchronized public void put(String time, Object value)
	{
		data.put(time, value);
	}
	
	synchronized public Map<String, Object> get()
	{
		Map<String, Object> ret = new HashMap<String, Object>(data);
		data.clear();
		return ret;
	}
}
