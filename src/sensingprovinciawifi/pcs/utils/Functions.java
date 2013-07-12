package sensingprovinciawifi.pcs.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Functions {
	
	public static short byteArraytoShort(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
//		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.order(ByteOrder.BIG_ENDIAN); // it depends on the platform
		return buffer.getShort();
	}
	
	public static int byteArraytoInt(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
//		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.order(ByteOrder.BIG_ENDIAN); // it depends on the platform
		return buffer.getInt();
	}
	
	public static long byteArraytoLong(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
//		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.order(ByteOrder.BIG_ENDIAN); // it depends on the platform
		return buffer.getLong();
	}
	
}
