package sensingprovinciawifi.wsn.receive;
import java.util.HashMap;


public class Data {

	public HashMap<String,Integer> data;

	public Data()
	{
		data = new HashMap<String,Integer>();
	}
	
	synchronized public void put(int value,String time)
	{
		data.put(time, value);
	}
	
	@SuppressWarnings("unchecked")
	synchronized public HashMap<String, Integer> get()
	{
		HashMap<String,Integer> ret = (HashMap<String, Integer>) data.clone();
		data.clear();
		return ret;
		
	}
}
