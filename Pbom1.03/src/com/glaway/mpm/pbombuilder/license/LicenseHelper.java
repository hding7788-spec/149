package com.glaway.mpm.pbombuilder.license;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class LicenseHelper {
	public static final int EXPIRY = 0;
	public static final int LICENSE_NOT_EXISTS = 1;
	public static final int WRONG_HARDWARE = 2;
	public static final int REG_FAIL = 3;
	public static final int REG_SUCCESS = 4;
	
	
	public static void main(String[] args)
	{
		LicenseHelper helper = new LicenseHelper();
		helper.checkLicense("C:\\Users\\cding\\Documents");
	}
	public int checkLicense(String path) {

		try {
			// 读取license文件
			String licenseFilePath = path + "\\license"; 
			File file = new File(licenseFilePath);
			if(!file.exists())
			{
				System.out.println("License 不存在，请申请License！");
				return LICENSE_NOT_EXISTS;
			}
			String strLicese = readLicenseFile(licenseFilePath);
			byte[] btLicense = Base64.altBase64ToByteArray(strLicese);

			// 读取私钥
//			String currentPaht = System.getProperty("user.dir");//getCurrentPath();
//			String strPrivateKey = RSATool.readFile(path	+ "\\licenseprivatekey.key");
			
			
			InputStream is=LicenseHelper.this.getClass().getResourceAsStream("/licenseprivatekey.key");   
			        BufferedReader br=new BufferedReader(new InputStreamReader(is));  
			        String strPrivateKey = "";  
			        String str = "";
			        while((str = br.readLine()) != null) {
			        	strPrivateKey = strPrivateKey + str;;  
			    }  

			// System.out.println(strPrivateKey.equals(temp));
			byte[] btPrivateKey = Base64.altBase64ToByteArray(strPrivateKey);

			// 使用私钥对密文进行解密 返回解密后的数据
			byte[] btLicenseTxt = RSATool.decryptByRSA(btPrivateKey, btLicense);

			String strLicenseUtf8 = new String(btLicenseTxt, "utf-8");
			
			Map licenseValueMap = RSATool.parseLienseContent(strLicenseUtf8);
			
			DateFormat df = new SimpleDateFormat(RSATool.DATE_FORMAT);
			Calendar calendar = Calendar.getInstance();
			Date today = calendar.getTime();
			Date expiryDate = df.parse((String)licenseValueMap.get(RSATool.EXPIRY));
			if(today.getTime() > expiryDate.getTime())
			{
				System.out.println("产品过期！请申请License！");
				return EXPIRY;
			}
			String cpuSN = SystemInfor.getCPUSerial();
			//String hdSN = SystemInfor.getHDSerial();
//			String mac = SystemInfor.getMACAddress();
			
			String lcpuSN = (String)licenseValueMap.get("cpu");
//			String lhdSN = (String)licenseValueMap.get("hd");
//			String lmac = (String)licenseValueMap.get("mac");
			
			if(!lcpuSN.equalsIgnoreCase(cpuSN)
					//|| !lhdSN.equalsIgnoreCase(hdSN)
//					|| !lmac.equalsIgnoreCase(mac)
					)
			{
				System.out.println("未注册的硬件！");
				return WRONG_HARDWARE;
			}
//			 System.out.println(strLicenseUtf8);
		} catch (Exception e) {
			System.out.println("注册失败！");
			e.printStackTrace();
		}
		return REG_SUCCESS;
	}
	
	/*
	 * 获取当前的工作目录
	 */
	private static String getCurrentPath()
	{
		String path = System.getProperty("java.class.path");
		int firstIndex = 0;//path.lastIndexOf(System.getProperty("path.separator")) + 1;
		int lastIndex = path.indexOf(System.getProperty("path.separator")) + 1; 
		path = path.substring(firstIndex, lastIndex);
		path = path.substring(0, path.lastIndexOf(File.separator));
		return path;
	}

	/*
	 * 获取License文件
	 */
	private static String readLicenseFile(String licenseFilePath) {
		Reader reader;
		try {
			File licenseFile = new File(licenseFilePath);
			reader = new InputStreamReader(new FileInputStream(licenseFile));
			BufferedReader bufferReader = new BufferedReader(reader);
			StringBuffer sb = new StringBuffer();
			String rLine = "";
			while ((rLine = bufferReader.readLine()) != null) {
				sb.append(rLine);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
