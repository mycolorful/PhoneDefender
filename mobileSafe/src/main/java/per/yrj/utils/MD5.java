package per.yrj.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	public static String toMD5(String psd) {
		StringBuffer sb = new StringBuffer();
		try {
			MessageDigest msgDigest = MessageDigest.getInstance("MD5");
			byte[] digest = msgDigest.digest(psd.getBytes());
			
			for(byte b : digest){
				int i = b&0xff;
				String hexString = Integer.toHexString(i);
				if(hexString.length() < 2){
					hexString = "0" + hexString;
				}
				sb.append(hexString);
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 获取到文件的MD5(病毒特征码)
	 * @param sourceDir
	 * @return
	 * 主动防御
	 */
	public static String getFileMd5(String sourceDir) {

		File file = new File(sourceDir);

		try {
			FileInputStream fis = new FileInputStream(file);

			byte[] buffer =  new byte[1024];

			int len = -1;
			//获取到数字摘要
			MessageDigest messageDigest = MessageDigest.getInstance("md5");

			while((len = fis.read(buffer))!= -1){

				messageDigest.update(buffer, 0, len);

			}
			byte[] result = messageDigest.digest();

			StringBuffer sb = new StringBuffer();

			for(byte b : result){
				int number = b&0xff; // 加盐 +1 ;
				String hex = Integer.toHexString(number);
				if(hex.length()==1){
					sb.append("0"+hex);
				}else{
					sb.append(hex);
				}
			}
			return sb.toString();


		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
