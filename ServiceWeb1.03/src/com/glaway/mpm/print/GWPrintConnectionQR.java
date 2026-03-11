package com.glaway.mpm.print;

import com.glaway.mpm.model.PrintFileBean;
import com.glaway.mpm.print.util.PrintUtil;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.WTDocumentUtil;
import ext.casc.util.DBConn;
import org.tempuri.Service1Locator;
import org.tempuri.Service1Soap;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.WTObject;
import wt.iba.value.IBAHolder;
import wt.util.WTProperties;

import javax.xml.rpc.ServiceException;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GWPrintConnectionQR {
	/**
	 * 正常打印分发交互条码系统
	* @author zhuhao
	* @date 2018-5-23
	* @param list
	* @param map
	 * @throws ServiceException
	 * @throws RemoteException
	 */
	public static void connectionQR(List<WTObject> list, Map<WTObject, String> map) throws RemoteException, ServiceException{
		DBConn conn = null;
		try {
			conn = new DBConn();
		    for(WTObject wtObject : list){
		    	if(wtObject instanceof WTChangeOrder2){
		    		WTChangeOrder2 changeOrder2 = (WTChangeOrder2)wtObject;
		    		String number = changeOrder2.getNumber();//编号
		    		String name = changeOrder2.getName();//名称
		    		IBAHelper ibaHelper = new IBAHelper((IBAHolder) changeOrder2.getContainer());
		    		String value = ibaHelper.getIBAValue("XHLX");
		    		if(value == null){
		    			value = "";
		    		}
		    		String XHLX = PrintUtil.getModelType(value);//型号类型
		    		IBAHelper docIBA = new IBAHelper(changeOrder2);
		    		String secret = docIBA.getIBAValue("SECRET");//密级
		    		if(secret == null || "null".equals(secret)){
		    			secret = "";
		    		}
		    		String version = "";//版本
		    		String id = map.get(changeOrder2);
		    		StringBuffer sb = new StringBuffer();
		    		sb.append("SELECT * FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID = '");
		    		sb.append(id);
		    		sb.append("'");
		    		ResultSet rs = conn.executeQuery(sb.toString());
		    		int count = 0;
		    		while(rs.next()){
		    			String index = rs.getString("DISTRIBUTEQUANTITY");
		    			count = count + Integer.valueOf(index);
		    		}
		    		if(count == 0){
		    			continue;
		    		}
		    		String barCodeList = getQRInfo(number, name, XHLX, secret, version, String.valueOf(count));
		    		if(barCodeList != null && !"".equals(barCodeList)){
		    			downloadBarCoder(barCodeList);//下载条码
		    			List<String> uuidList = queryBarCode(id);//查询条码标对应id
		    			String result = updateBarCoder(uuidList, barCodeList);//更新条码表
		    			System.out.println("-----" + result + "-----");
		    		}
		    	}else{
		    		WTDocument doc = (WTDocument)wtObject;
		    		String number = doc.getNumber();//编号
		    		String name = doc.getName();//名称
		    		IBAHelper ibaHelper = new IBAHelper((IBAHolder) doc.getContainer());
		    		String value = ibaHelper.getIBAValue("XHLX");
		    		if(value == null){
		    			value = "";
		    		}
		    		String XHLX = PrintUtil.getModelType(value);//型号类型
		    		IBAHelper docIBA = new IBAHelper(doc);
		    		String secret = docIBA.getIBAValue("SECRET");//密级
		    		if(secret == null || "null".equals(secret)){
		    			secret = "";
		    		}
		    		String version = doc.getVersionIdentifier().getValue()+"."+doc.getIterationIdentifier().getValue();//版本
		    		String id = map.get(doc);
		    		StringBuffer sb = new StringBuffer();
		    		sb.append("SELECT * FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID = '");
		    		sb.append(id);
		    		sb.append("'");
		    		ResultSet rs = conn.executeQuery(sb.toString());
		    		int count = 0;
		    		while(rs.next()){
		    			String index = rs.getString("DISTRIBUTEQUANTITY");
		    			count = count + Integer.valueOf(index);
		    		}
		    		if(count == 0){
		    			continue;
		    		}
		    		String barCodeList = getQRInfo(number, name, XHLX, secret, version, String.valueOf(count));
		    		if(barCodeList != null && !"".equals(barCodeList)){
		    			downloadBarCoder(barCodeList);//下载条码
		    			List<String> uuidList = queryBarCode(id);//查询条码标对应id
		    			String result = updateBarCoder(uuidList, barCodeList);//更新条码表
		    			System.out.println("-----" + result + "-----");
		    		}
		    	}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void connectionQR(WTObject wtObject, String id) throws RemoteException, ServiceException{
		DBConn conn = null;
		try {
			conn = new DBConn();
			if(wtObject instanceof WTChangeOrder2){
				WTChangeOrder2 changeOrder2 = (WTChangeOrder2)wtObject;
				String number = changeOrder2.getNumber();//编号
				String name = changeOrder2.getName();//名称
				IBAHelper ibaHelper = new IBAHelper((IBAHolder) changeOrder2.getContainer());
				String value = ibaHelper.getIBAValue("XHLX");
				if(value == null){
					value = "";
				}
				String XHLX = PrintUtil.getModelType(value);//型号类型
				IBAHelper docIBA = new IBAHelper(changeOrder2);
				String secret = docIBA.getIBAValue("SECRET");//密级
				if(secret == null || "null".equals(secret)){
					secret = "";
				}
				String version = "";//版本
				StringBuffer sb = new StringBuffer();
				sb.append("SELECT * FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID = '");
				sb.append(id);
				sb.append("'");
				ResultSet rs = conn.executeQuery(sb.toString());
				int count = 0;
				while(rs.next()){
					String index = rs.getString("DISTRIBUTEQUANTITY");
					count = count + Integer.valueOf(index);
				}
				if(count == 0){
					return;
				}
				String barCodeList = getQRInfo(number, name, XHLX, secret, version, String.valueOf(count));
				if(barCodeList != null && !"".equals(barCodeList)){
					downloadBarCoder(barCodeList);//下载条码
					List<String> uuidList = queryBarCode(id);//查询条码标对应id
					String result = updateBarCoder(uuidList, barCodeList);//更新条码表
					System.out.println("-----" + result + "-----");
				}
			}else{
				WTDocument doc = (WTDocument)wtObject;
				String number = doc.getNumber();//编号
				String name = doc.getName();//名称
				IBAHelper ibaHelper = new IBAHelper((IBAHolder) doc.getContainer());
				String value = ibaHelper.getIBAValue("XHLX");
				if(value == null){
					value = "";
				}
				String XHLX = PrintUtil.getModelType(value);//型号类型
				IBAHelper docIBA = new IBAHelper(doc);
				String secret = docIBA.getIBAValue("SECRET");//密级
				if(secret == null || "null".equals(secret)){
					secret = "";
				}
				String version = doc.getVersionIdentifier().getValue()+"."+doc.getIterationIdentifier().getValue();//版本
				StringBuffer sb = new StringBuffer();
				sb.append("SELECT * FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID = '");
				sb.append(id);
				sb.append("'");
				ResultSet rs = conn.executeQuery(sb.toString());
				int count = 0;
				while(rs.next()){
					String index = rs.getString("DISTRIBUTEQUANTITY");
					count = count + Integer.valueOf(index);
				}
				if(count == 0){
					return;
				}
				String barCodeList = getQRInfo(number, name, XHLX, secret, version, String.valueOf(count));
				if(barCodeList != null && !"".equals(barCodeList)){
					downloadBarCoder(barCodeList);//下载条码
					List<String> uuidList = queryBarCode(id);//查询条码标对应id
					String result = updateBarCoder(uuidList, barCodeList);//更新条码表
					System.out.println("-----" + result + "-----");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 更新条码到条码表
	* @author zhuhao
	* @date 2018-4-11
	* @param uuidList
	* @param barCodeList
	 * @return
	 */
	private static String updateBarCoder(List<String> uuidList, String barCodeList) {
		List<String> list = new ArrayList<String>();
		if(barCodeList.contains("|")){
			list = java.util.Arrays.asList(barCodeList.split("\\|"));
		}else{
			list.add(barCodeList);
		}
		if(uuidList.size() != list.size()){
			return "条码更新失败";
		}
		DBConn conn = null;
		try {
			conn = new DBConn();
		    for(int i = 0; i < uuidList.size(); i++){
				StringBuffer sb = new StringBuffer();
				sb.append("UPDATE GWPRINTBARCODE SET BARCODE = '" + list.get(i) + "' WHERE GWKEYID = '" + uuidList.get(i) +"'");
				conn.executeUpdate(sb.toString());
				conn.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return "条码更新成功";
	}

	/**
	 * 查询文件占用条码标id
	* @author zhuhao
	* @date 2018-4-11
	* @param id
	* @return
	 */
	private static List<String> queryBarCode(String id) {
		List<String> list = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT GWKEYID FROM GWPRINTBARCODE WHERE APPLYRECORDID IN ");
		sb.append("(SELECT GWKEYID FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID IN");
		sb.append("(SELECT GWKEYID FROM GWPRINTAPPLYRECORD WHERE GWKEYID ='"+id+"'))");
		DBConn conn = null;
		try {
			conn = new DBConn();
		    ResultSet rs = conn.executeQuery(sb.toString());
		    while(rs.next()){
		    	list.add(rs.getString("GWKEYID"));
		    }
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	private static String getQRInfo(String number, String name, String xHLX, String secret, String version, String count) throws ServiceException, RemoteException{
		Service1Locator locator = new Service1Locator();
		//调用条码接口
		String barCodeList = "";

		Service1Soap soap = locator.getService1Soap();
		barCodeList = soap.getDABH_PDM(number, name, xHLX, secret, version, count);
		System.out.println("info:-----" + barCodeList + "-----");
		if(barCodeList.startsWith("1:")){
			barCodeList = barCodeList.substring(2, barCodeList.length());
		}

		return barCodeList;
	}

	private static void downloadBarCoder(String barCodeList){
		List<String> list = new ArrayList<String>();
		if(barCodeList.contains("|")){
			list = java.util.Arrays.asList(barCodeList.split("\\|"));
		}else{
			list.add(barCodeList);
		}
		WTProperties wtProperties = null;
		try {
			wtProperties = WTProperties.getLocalProperties();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String codebasePath = wtProperties.getProperty("wt.codebase.location");
		String path = codebasePath + File.separator +"printApply";
		Service1Locator locator = new Service1Locator();
		for(String imgName : list){
			byte[] result = null;
			try{
				Service1Soap soap = locator.getService1Soap();
				result = soap.getQR_PDM(imgName);
				if(result != null){
					System.out.println("-----下载条码成功------");
				}
			}catch(ServiceException e){
				e.printStackTrace();
			}catch(RemoteException e){
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(imgName.contains("/")){
				imgName = imgName.replace("/", "~");
			}
			String file = path + File.separator + imgName + ".jpg";
			PrintUtil.writeBytes(file,result);
		}

	}

	/**
	 * 补打交互条码系统
	* @author zhuhao
	* @date 2018-5-25
	* @param ids
	 * @throws ServiceException
	 * @throws RemoteException
	 */
	public static void connectionQRForOffSet(String ids) throws RemoteException, ServiceException {
		DBConn conn =null;
		try {
			conn = new DBConn();

			List<String> idList = java.util.Arrays.asList(ids.split(","));
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM GWPRINTAPPLYRECORD WHERE GWKEYID IN ");
            sb.append("(SELECT APPLYRECORDID FROM GWPRINTDISTRIBUTERECORD WHERE GWKEYID IN ");
            sb.append("(SELECT APPLYRECORDID FROM GWPRINTBARCODE WHERE GWKEYID = '" + idList.get(0) + "'))");
            ResultSet rs = conn.executeQuery(sb.toString());
            String number1 = "";
            String version1 = "";
            while(rs.next()){
            	number1 = rs.getString("TECHNICSNUMBER");
            	version1 = rs.getString("VERSION");
            }
            List<WTDocument> docs = WTDocumentUtil.getAllDocumentByNumber(number1);
	    	WTDocument doc = null;
	    	precise : for(WTDocument doc0 : docs){
	    		String docVersion = doc0.getVersionIdentifier().getValue()+"."+doc0.getIterationIdentifier().getValue();
	    		if(docVersion.equals(version1)){
	    			doc = doc0;
	    			break precise;
	    		}
	    	}
	    	if(doc == null){
	    		System.out.println("-----条码系统所传文档不存在-----");
	    		return;
	    	}
	    	String number = doc.getNumber();//编号
	    	String name = doc.getName();//名称
	    	IBAHelper ibaHelper = new IBAHelper((IBAHolder) doc.getContainer());
			String value = ibaHelper.getIBAValue("XHLX");
			if(value == null){
				value = "";
			}
			String XHLX = PrintUtil.getModelType(value);//型号类型
			IBAHelper docIBA = new IBAHelper(doc);
			String secret = docIBA.getIBAValue("SECRET");//密级
			if(secret == null || "null".equals(secret)){
				secret = "";
			}
			String version = doc.getVersionIdentifier().getValue()+"."+doc.getIterationIdentifier().getValue();//版本
			String count = String.valueOf(idList.size());
			String barCodeList = getQRInfo(number, name, XHLX, secret, version, count);
			if(barCodeList != null && !"".equals(barCodeList)){
				downloadBarCoder(barCodeList);//下载条码
				String result = updateBarCoder(idList, barCodeList);//更新条码表
				System.out.println("-----" + result + "-----");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 外来文件交互条码系统
	* @author zhuhao
	* @date 2018-6-14
	* @param list
	* @param map
	 * @throws ServiceException
	 * @throws RemoteException
	 */
	public static void connectionQRForOutFile(List<String> list, Map<String, PrintFileBean> map) throws RemoteException, ServiceException {
		DBConn conn =null;
		try {
			conn = new DBConn();
			for(String id : list){
				PrintFileBean bean = map.get(id);
				String number = bean.getFileNumber();
				String name = bean.getFileName();
				String XHLX = bean.getXhlx();
				String secret = bean.getSecret();
				String version = bean.getVersion();
				StringBuffer sb = new StringBuffer();
				sb.append("SELECT * FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID = '");
				sb.append(id);
				sb.append("'");
				ResultSet rs = conn.executeQuery(sb.toString());
				int count = 0;
				while(rs.next()){
					String index = rs.getString("DISTRIBUTEQUANTITY");
					count = count + Integer.valueOf(index);
				}
				if(count == 0){
					continue;
				}
				String barCodeList = getQRInfo(number, name, XHLX, secret, version, String.valueOf(count));
				if(barCodeList != null && !"".equals(barCodeList)){
					downloadBarCoder(barCodeList);//下载条码
					List<String> uuidList = queryBarCode(id);//查询条码标对应id
					String result = updateBarCoder(uuidList, barCodeList);//更新条码表
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
