package per.yrj.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

public class StreamUtils {
	
	/**
	 * 用于从输入流中读取文件
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static String readStream(InputStream inputStream) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		byte[] buf = new byte[1024];
		int len = 0;
		while((len = inputStream.read(buf)) != -1){
			out.write(buf, 0, len);
		}
		String result = out.toString();
		inputStream.close();
		out.close();
		return result;
	}
}
