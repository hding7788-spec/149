package com.glaway.mpm.qmIntf.viewPanel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.tomcat.util.http.fileupload.util.Streams;

public class FileDownLoad {

	public static String ANTHORIZATION  = "d2NhZG1pbjp3Y2FkbWlu";
										   
	private static PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
	
	/**
	 * 文档下载
	 * @param fileName 文档名称
	 * @param url      文档连接
	 * @param tofolder 下载到的目录
	 * @throws Exception
	 */
	public void downLoadDocument(String fileName,String url,String tofolder)throws Exception{
		InputStream is=serviceRequest(ANTHORIZATION,url);
		File downLoadFile=new File(tofolder+"\\"+fileName);
		FileOutputStream fos=new FileOutputStream(downLoadFile);
		Streams.copy(is, fos, true);
	}
	
	/**
	 * 
	 * @param anthorization
	 * @param url
	 * @return
	 */
	public static InputStream serviceRequest(String anthorization, String url) {
		InputStream is = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient(cm);
			BasicHttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet(url);
			httpGet.addHeader("Authorization", "Basic " + anthorization);
			HttpResponse response = httpclient.execute(httpGet, localContext);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return is;
	}

}
