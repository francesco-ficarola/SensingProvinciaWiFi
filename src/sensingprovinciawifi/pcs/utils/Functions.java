package sensingprovinciawifi.pcs.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Functions {
	public static byte[] hexToByteArray(String s) {
	    int j = 0;
	    int k = 0;
	    int cSize = s.length();
	    byte[] newByteArray = new byte[cSize/2];

	    while (k < cSize/2) {
	        String tempString = s.substring(j, j+2);

	        int intValue = Integer.parseInt(tempString, 16);
	        if (intValue >= 128) {
	            int diff = intValue - 128;
	            intValue = -(128-diff);
	        }
	        String st = Integer.toString(intValue);
	        newByteArray[k] = Byte.valueOf(st);
	        j = j+2;
	        k++;
	    }

	    return newByteArray;
	}
	
	public static short byteArraytoShort(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		//buffer.order(ByteOrder.BIG_ENDIAN); // it depends on the platform
		return buffer.getShort();
	}
	
	public static int byteArraytoInt(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		//buffer.order(ByteOrder.BIG_ENDIAN); // it depends on the platform
		return buffer.getInt();
	}
}
