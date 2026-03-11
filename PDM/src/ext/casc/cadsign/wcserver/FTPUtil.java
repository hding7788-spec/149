package ext.casc.cadsign.wcserver;

import java.io.File;
import java.io.IOException;

import wt.method.RemoteAccess;
import wt.util.WTException;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPMessageCollector;
import com.enterprisedt.net.ftp.FTPTransferType;
  

public class FTPUtil implements RemoteAccess{
	
	public static void main(String[] args) throws Exception{
	/*  //上传本地文件到ftp的a文件夹下，并命名为"2.xml"
		uploadToFTP("127.0.0.1","wcadmin","wcadmin","D:\\3b.xml","a\\2.xml"); 
		//下载ftp的"a\\2.xml"到本地硬盘"D:\\3b.xml"
		downloadFromFTP("a\\2.xml","D:\\4b.xml");
		//移动ftp的a\\2.xml到b文件夹，并命名为f.xml,同时a\\2.xml消失,如果在同一文件夹，则为重命名
		renameFTPFile("127.0.0.1","wcadmin","wcadmin","a\\2.xml","b\\f.xml");
		//删除ftp的b文件夹下的"f.xml"
		deleteFTPFile("127.0.0.1","wcadmin","wcadmin","b\\f.xml");
	*/
		String folderList = listFolder("");
		//System.out.println(folderList.length());
		char [] ch = folderList.toCharArray();
		for(int i = 0 ; i < ch.length ; i++){
			System.out.println(ch[i]+""); 
		} 
		ftpToSign("D:\\b.zip","c.zip");
	}
	
	 /******************默认ftp端口******************/
	 private static final int FTPPORT = 21;
	 /******************默认ftp地址******************/
	 private static final String FTPSERVER = "192.168.142.1";
	 /******************默认ftp登录账号******************/
	 private static final String USERNAME = "wcadmin";
	 /******************默认ftp登录密码******************/
	 private static final String PASSWORD = "wcadmin";
	 /******************默认ftp地址******************/
	 private static final String CLASSNAME = FTPUtil.class.getName();
	  
	 
	 public static String ftpToSign(String localFile,String ftpPath) throws Exception{
		 String ftpFileName = null;
		 String server = null;
		 try{
			 FTPClient ftp = connectSign();
			 /** put(源，目的)这里的路径可以用相对路径或绝对路径 */ 
			 ftp.put(localFile,ftpPath);
			 ftp.quit();
			 ftpFileName = ftpPath;
		 }catch(Exception e){
			 e.printStackTrace();
		 }if(ftpFileName == null){
			 throw new Exception("上传本地文件到签字服务器的ftp失败,本地文件地址:" + localFile);
		 }
		 return ftpFileName;
	 }

	 public static String ftpToWindchill(String localFile,String ftpPath) throws Exception{
		 String ftpFileName = null;
		 String server = null;
		 try{
			 FTPClient ftp = connectSign();
			 /** put(源，目的)这里的路径可以用相对路径或绝对路径 */ 
			 ftp.put(localFile,ftpPath);
			 ftp.quit();
			 ftpFileName = ftpPath;
		 }catch(Exception e){
			 e.printStackTrace();
		 }if(ftpFileName == null){
			 throw new Exception("上传本地文件到Windchill服务器的ftp失败,本地文件地址:" + localFile);
		 }
		 return ftpFileName;
	 }
	 
	/**
	 * 
	 * 上午11:45:21
	 * @param ftpserver ftp地址如127.0.0.1
	 * @param user 登录的用户名
	 * @param password 登录的密码
	 * @param localFilePath 本地文件位置
	 * @param serverFilePath 要上传上去的位置
	 * @return
	 */
	public static boolean uploadToFTP(String ftpserver, String user,
			String password, String localFilePath, String serverFilePath){
		
		try {
			FTPClient ftp = connect(ftpserver,FTPPORT,user,password);
			/** put(源，目的)这里的路径可以用相对路径或绝对路径 */
			ftp.put(localFilePath, serverFilePath);
			ftp.quit();
			return true;
		} catch (NumberFormatException e) { 
			System.out.println(CLASSNAME + "uploadToFTP(),NumberFormatException, message=");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(CLASSNAME + "uploadToFTP(),IOException, message=");
			e.printStackTrace();
		} catch (FTPException e) {
			System.out.println(CLASSNAME + "uploadToFTP(),FTPException, message=");
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 下载ftp上的文件到本地
	 * 2:41:26 PM
	 * @param serverFilePath 服务器上的文件路径
	 * @param localFilePath 本地路径
	 * @return
	 */
	public static boolean downloadFromFTP(String serverFilePath,String localFilePath){
		File file = new File(localFilePath);
		if(!file.exists()){
			file.mkdirs();
		}else{
			ZipFileUtil.deleteFiles(localFilePath); 
			file.mkdirs();
		}
		try {
			FTPClient ftp = connect(FTPSERVER,FTPPORT,USERNAME,PASSWORD);
			/** put(源，目的)这里的路径可以用相对路径或绝对路径 */
			ftp.get(localFilePath,serverFilePath); 
			ftp.quit();
			return true;
		} catch (NumberFormatException e) { 
			System.out.println(CLASSNAME + "downloadFromFTP(),NumberFormatException, message=");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(CLASSNAME + "downloadFromFTP(),IOException, message=");
			e.printStackTrace();
		} catch (FTPException e) { 
			System.out.println(CLASSNAME + "downloadFromFTP(),FTPException, message=");
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * 创建ftp连接
	 * 2:42:16 PM
	 * @param ftpserver 服务地址
	 * @param port      端口
	 * @param user      用户名
	 * @param password  密码
	 * @return
	 * @throws IOException
	 * @throws FTPException
	 */
	private static FTPClient connect(String ftpserver,int port,String user,String password) throws IOException, FTPException{
		
		FTPClient ftp = new FTPClient();
		ftp.setRemoteHost(ftpserver);
		ftp.setRemotePort(port);
		ftp.setControlEncoding("GB2312");
		FTPMessageCollector listener = new FTPMessageCollector();
		ftp.setMessageListener(listener);
		ftp.connect();
		ftp.login(user, password);
		
		/** 设置连接模式 */
		ftp.setConnectMode(FTPConnectMode.ACTIVE);
		/** 设置传送模式 为二进制模式 */ 
		ftp.setType(FTPTransferType.BINARY);
		return ftp;
	}
	
	public static FTPClient connectSign() throws Exception{
		String signFtpServer = CADSignHelper.getValueProperties("sign.ftp.ip");
		int signFtpPort = Integer.parseInt(CADSignHelper.getValueProperties("sign.ftp.port")); 
		String signFtpUser = CADSignHelper.getValueProperties("sign.ftp.user");
		String signFtpPassword = CADSignHelper.getValueProperties("sign.ftp.password");
		 
		FTPClient ftp = new FTPClient();
		ftp.setRemoteHost(signFtpServer);
		ftp.setRemotePort(signFtpPort);
		ftp.setControlEncoding("GB2312");
		FTPMessageCollector listener = new FTPMessageCollector();
		ftp.setMessageListener(listener);
		ftp.connect();
		ftp.login(signFtpUser, signFtpPassword);
		
		/** 设置连接模式 */
		ftp.setConnectMode(FTPConnectMode.ACTIVE);
		/** 设置传送模式 为二进制模式 */ 
		ftp.setType(FTPTransferType.BINARY);
		return ftp;
	}
	
	
	public static FTPClient connectWindchill() throws Exception{
		String signFtpServer = CADSignHelper.getValueProperties("windchill.ftp.ip");
		int signFtpPort = Integer.parseInt(CADSignHelper.getValueProperties("windchill.ftp.port")); 
		String signFtpUser = CADSignHelper.getValueProperties("windchill.ftp.user");
		String signFtpPassword = CADSignHelper.getValueProperties("windchill.ftp.password");
		 
		FTPClient ftp = new FTPClient();
		ftp.setRemoteHost(signFtpServer);
		ftp.setRemotePort(signFtpPort);
		ftp.setControlEncoding("GB2312");
		FTPMessageCollector listener = new FTPMessageCollector();
		ftp.setMessageListener(listener);
		ftp.connect();
		ftp.login(signFtpUser, signFtpPassword);
		
		/** 设置连接模式 */
		ftp.setConnectMode(FTPConnectMode.ACTIVE);
		/** 设置传送模式 为二进制模式 */ 
		ftp.setType(FTPTransferType.BINARY);
		return ftp;
	}
	
	public static boolean renameFTPFile(String ftpserver, String user,
			String password, String oldName, String newName){
		
		try {
			FTPClient ftp = connect(ftpserver,FTPPORT,user,password);
			/** put(源，目的)这里的路径可以用相对路径或绝对路径 */
			ftp.rename(oldName,newName);//ftp.rename("a\\1.xml", "a\\2.xml")------>修改ftp服务器上a目录下的1.xml，将其名字修改为2.xml
			ftp.quit();
			return true;
		} catch (NumberFormatException e) { 
			System.out.println(CLASSNAME + "renameFTPFile(),NumberFormatException, message=");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(CLASSNAME + "renameFTPFile(),IOException, message=");
			e.printStackTrace();
		} catch (FTPException e) { 
			System.out.println(CLASSNAME + "renameFTPFile(),FTPException, message=");
			e.printStackTrace();
		}
		return false;
	}
	
	
	public static boolean deleteFTPFile(String ftpserver, String user,
			String password, String FTPFilePath){
		
		int FTPPORT = 21; 
		try {
			FTPClient ftp = connect(ftpserver,FTPPORT,user,password);
			ftp.delete(FTPFilePath); //ftp.delete("a\\1.xml");--->删除ftp服务器上a目录下的1.xml，需要ftp服务器设置相应的权限
			ftp.quit();
			return true;
		} catch (NumberFormatException e) { 
			System.out.println(CLASSNAME + "deleteFTPFile(),NumberFormatException, message=");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(CLASSNAME + "deleteFTPFile(),IOException, message=");
			e.printStackTrace();
		} catch (FTPException e) { 
			System.out.println(CLASSNAME + "deleteFTPFile(),FTPException, message=");
			e.printStackTrace();
		}
		return false;
	}
	
	public static String listFolder(String rootFolder){
		if(rootFolder == null){
			rootFolder= "";
		}
		try {
			FTPClient ftp = connect(FTPSERVER,FTPPORT,USERNAME,PASSWORD);
			return ftp.list(rootFolder);
		} catch (IOException e) {
			System.out.println(CLASSNAME + "deleteFTPFile(),NumberFormatException, message=");
			e.printStackTrace();
		} catch (FTPException e) {
			System.out.println(CLASSNAME + "deleteFTPFile(),NumberFormatException, message=");
			e.printStackTrace();
		} 
		return "";
	} 
}