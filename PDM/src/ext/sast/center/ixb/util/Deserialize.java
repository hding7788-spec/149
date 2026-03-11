package ext.sast.center.ixb.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 序列化和反序列化HashMap对象
 * @version 1.0
 * @date 2012-08-21
 * @author Esthan
 *
 */
public class Deserialize {

	/**
	 * 序列化HashMap
	 * @param targetMap
	 * @return
	 */
	public static String serializeMap(Map<String, String> targetMap){
		String result="";
		try{
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
	        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);  
	        objectOutputStream.writeObject(targetMap);    
	        result = byteArrayOutputStream.toString("ISO-8859-1");  
	        result = java.net.URLEncoder.encode(result, "UTF-8");  
	        objectOutputStream.close();  
	        byteArrayOutputStream.close();  
		}catch(IOException ex){
			ex.printStackTrace();
		}
		return result;
	}
	
	public static String serializeObjectMap(Map<String, Object> targetMap){
		String result="";
		try{
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
	        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);  
	        objectOutputStream.writeObject(targetMap);    
	        result = byteArrayOutputStream.toString("ISO-8859-1");  
	        result = java.net.URLEncoder.encode(result, "UTF-8");  
	        objectOutputStream.close();  
	        byteArrayOutputStream.close();  
		}catch(IOException ex){
			ex.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 反序列化HashMap字符串
	 * @param targetStr
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map deserializeMap(String targetStr){
		Map targetMap=new HashMap();
		try{
			String tempStr=java.net.URLDecoder.decode(targetStr, "UTF-8");
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(tempStr.getBytes("ISO-8859-1")); 
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			targetMap = (Map)objectInputStream.readObject();  
			
		    objectInputStream.close();  
		    byteArrayInputStream.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}catch(ClassNotFoundException ex){
			ex.printStackTrace();
		}
		
		return targetMap;
	}
}
