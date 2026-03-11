package ext.casc.cadsign.wcserver;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.Vector;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;

/** 
 * <p>
 * Description:
 * </p>
 * 
 * @author: Zhong
 * @time: May 19, 2010 2:44:32 PM
 * @version 1.0
 */

public class CADSignHelper implements RemoteAccess{ 

	private static final String CLASSNAME = CADSignHelper.class.getName();
	
	static final String PROPERTIES = "ext.casc.cadsign.wcserver.cadSign";
	
	/**
	 * 筛选出主内容或者附件为指定格式文件的文档 2:55:45 PM
	 * 
	 * @param docList
	 *            要筛选的文档对象集合
	 * @param fileType
	 *            文件后缀名如"txt"
	 * @param isPrimary
	 *            是否筛选主内容 true筛选主内容，false则根据附件筛选
	 * @return
	 */
	public static List filterWTDocument(List<WTDocument> docList,
			String fileType, boolean isPrimary) {
		List newDocList = new ArrayList();
		for (Iterator it = docList.iterator(); it.hasNext();) {
			WTDocument doc = (WTDocument) it.next();
			try {
				if (isPrimary) { // 筛选主内容
					ContentItem item = (ContentItem) ContentHelper.service
							.getPrimary(doc);
					if (item instanceof ApplicationData) {
						ApplicationData priData = (ApplicationData) item;
						if (priData.getFileName().toLowerCase().endsWith(
								"." + fileType.toLowerCase())) {
							newDocList.add(doc);
						}
					}
				} else {// 筛选附件,只判断第一个附件类型
					ContentHolder contentHolder = ContentHelper.service
							.getContents((ContentHolder) doc);
					Vector apps = ContentHelper
							.getApplicationData(contentHolder);
					if (apps != null && apps.size() > 0) {
						ApplicationData appData = (ApplicationData) apps
								.elementAt(0);
						if (appData.getFileName().toLowerCase().endsWith(
								"." + fileType.toLowerCase())) {
							newDocList.add(doc);
						}
					}
				}
			} catch (WTException e) {
				e.printStackTrace();
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		System.out.println("筛选文档完毕,筛选依据：主内容或附件后缀为"+fileType+",是否根据主内容筛选：" + isPrimary + "。");
		return newDocList;
	}

	/**
	 * 设置对象的生命周期状态 4:03:38 PM
	 * 
	 * @param doc
	 * @param toState
	 */
	public static void updateLifeCycle(WTDocument doc, String toState) {
		WTUser current = null;
		try {
			current = (WTUser) SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setAdministrator();
			LifeCycleManaged lcm = (LifeCycleManaged) doc;
			LifeCycleHelper.service.setLifeCycleState(lcm, State
					.toState(toState));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				SessionHelper.manager.setPrincipal(current
						.getAuthenticationName());
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
		String stateDisplay = State.toState(toState).getDisplay(
				Locale.SIMPLIFIED_CHINESE);
		System.out.println(CLASSNAME + ".()updateLifeCycle,设置文档-" + doc.getNumber() + "-的生命周期状态为-" + stateDisplay);
	}

	/**
	 * ok 更新文件的主内容
	 * 
	 * @param file
	 * @param doc
	 * @param isChangeFileName
	 *            是否改变主内容名 true为改变,false为不改变
	 * @return
	 */
	public static WTDocument uploadDocPriFile(File file, WTDocument doc,
			boolean isChangeFileName) {
		FileInputStream in = null;
		Transaction ts = null;
		try {
			ContentItem contentHolder = ContentHelper.service.getPrimary(doc);
			ApplicationData applicationdataPrimary = null;
			String oldFileName = "";
			if (contentHolder != null) {
				applicationdataPrimary = (ApplicationData) contentHolder;
				oldFileName = applicationdataPrimary.getFileName();
				System.out.println(CLASSNAME
						+ "-uploadPrimaryFile(),--------------更新前的的主文件名： "
						+ applicationdataPrimary.getFileName());
			}
			ts = new Transaction();
			ts.start();
			if (applicationdataPrimary != null) {
				ContentServerHelper.service.deleteContent(doc,
						applicationdataPrimary);
			}
			// 上载新的主文件
			in = new FileInputStream(file);
			ApplicationData tempAppData = ApplicationData
					.newApplicationData(doc);
			ContentRoleType contentRole = ContentRoleType.PRIMARY;
			if (!isChangeFileName) {
				tempAppData.setFileName(oldFileName); // 不更新文件名
			} else {
				tempAppData.setFileName(file.getName()); // 更新文件名
			}
			tempAppData.setUploadedFromPath(file.getParent());
			tempAppData.setRole(contentRole);
			tempAppData = ContentServerHelper.service.updatePrimary(doc,
					tempAppData, in);
			wt.content.FormatContentHolder result = ContentServerHelper.service
					.updateHolderFormat(doc);
			doc = (WTDocument) result;
			PersistenceServerHelper.manager.update(doc);
			doc = (WTDocument) PersistenceHelper.manager.refresh(doc);
			ts.commit();
			in.close();
			System.out.println("uploadPrimaryFile(),-------------更新主内容成功！");
		} catch (Exception e) {
			System.out.println("uploadPrimaryFile(),更新文件主内容出错,信息："
					+ e.getMessage());
			e.printStackTrace();
			ts.rollback();
		} finally {
			if (in != null) {
				try {
					in.close();
					if(file.exists()){
						file.delete();
					} 
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return doc;
	}
	
	
	/**
	 * 下载文档对象的主内容到指定的文件夹
	 * 
	 * @param wtdocument 文档对象
	 * @param tempDir 要存放的文件夹
	 * @return 下载下来的主内容位置tempDir+ "/" + 文档编号 +"/" + 主内容文件名
	 */
	public static String downloadDocPriFile(WTDocument wtdocument,
			String tempDir) {
		try {
			if (!tempDir.endsWith("/") && !tempDir.endsWith("\\")) {
				tempDir = tempDir + File.separator;
			}
			String downloadDirectoryStr = tempDir; //tempDir + wtdocument.getNumber();
			File file = new File(downloadDirectoryStr);
			if (!file.exists()) {
				file.mkdirs(); 
			}
			String contentFileName = "";
			ContentItem item = (ContentItem) ContentHelper.service
					.getPrimary(wtdocument);
			if (item instanceof ApplicationData) {
				ApplicationData appData = (ApplicationData) item;
				contentFileName = appData.getFileName();
				downloadDirectoryStr = downloadDirectoryStr + contentFileName;
				ContentServerHelper.service.writeContentStream(appData,downloadDirectoryStr);
			} 
			return downloadDirectoryStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * ok 给文档上传附件 6:16:35 PM
	 * 
	 * @param wtdocument
	 * @param filePathVector
	 *            附件文件路径的集合
	 * @param deleteOld
	 *            是否删除原来的附件
	 * @throws Exception
	 */
	public static void uploadDocAppFile(WTDocument wtdocument,
			Vector filePathVector, Boolean deleteOld) throws Exception {

		ContentHolder contentHolder = ContentHelper.service
				.getContents((ContentHolder) wtdocument);
		Vector apps = ContentHelper.getApplicationData(contentHolder);
		if (deleteOld) {
			if (apps != null && apps.size() > 0) {
				for (int i = 0; i < apps.size(); i++) {
					ApplicationData applicationdata = (ApplicationData) apps.elementAt(i);
					ContentServerHelper.service.deleteContent(contentHolder,applicationdata);
					contentHolder = (ContentHolder) PersistenceHelper.manager.refresh(contentHolder);
				}
			}
		}
		ContentHolder holder = contentHolder;// (ContentHolder) wtdocument;
		if (filePathVector == null || filePathVector.size() == 0){
			System.out.println("没有找到需要上传的附件！");
			return;
		}
		for (int j = 0; j < filePathVector.size(); j++) {
			String filePath = (String) filePathVector.get(j);
			File attFile = new File(filePath);
			if (attFile.exists()) {
				ApplicationData app_data = ApplicationData
						.newApplicationData(holder);
				app_data.setFileName(filePath);
				app_data.setUploadedFromPath(attFile.getAbsolutePath());
				app_data.setRole(ContentRoleType.SECONDARY);
				app_data.setFileSize(attFile.length());
				holder = (ContentHolder) PersistenceHelper.manager.refresh(holder);
				if (attFile.exists()) {
					app_data = ContentServerHelper.service.updateContent(holder, app_data, attFile.getPath());
				}
				// 删除临时文件
				if (attFile.exists()) {
					attFile.delete();
				}
			} else { 
				System.out.println("不存在文件 ....." + filePath);
			}
		}
	}
	
	/**
	 * 测试正确 下载文档对象的第一个附件内容到指定的文件夹
	 * @param wtdocument 文档对象           
	 * @param tempDir 要存放的文件夹    
	 * @return 下载下来的第一个附件位置tempDir+ "/" + 文档编号 +"/" + 文件名
	 */
	public static String downloadDocAppFile(WTDocument wtdocument,String tempDir) {
		try {
			if (!tempDir.endsWith("/") && !tempDir.endsWith("\\")) {
				tempDir = tempDir + File.separator;
			}
			String downloadDirectoryStr = tempDir + wtdocument.getNumber();
			File file = new File(downloadDirectoryStr);
			if (!file.exists()) {
				file.mkdirs();
			}
			wt.content.ContentHolder contentHolder = ContentHelper.service
					.getContents((ContentHolder) wtdocument);
			Vector apps = ContentHelper.getApplicationData(contentHolder);
			if (apps.size() > 0) {
				ApplicationData applicationdata = (ApplicationData) apps
						.elementAt(0);
				String appFileName = applicationdata.getFileName();
				System.out.println("appFileName=" + appFileName);
				InputStream inputstream = ContentServerHelper.service
						.findContentStream(applicationdata);
				String fileAbsolutePath = "";
				fileAbsolutePath = downloadDirectoryStr + File.separator+ appFileName;
				FileOutputStream tout = new FileOutputStream(fileAbsolutePath);
				byte abyte1[] = new byte[2048];
				int j;
				while ((j = inputstream.read(abyte1, 0, abyte1.length)) >= 0)
					tout.write(abyte1, 0, j);
				tout.close();
				return fileAbsolutePath;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Persistable getPersistableByOid(String oid){
		Persistable p = null;
		try {
			p = new ReferenceFactory().getReference(oid).getObject();
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return p;
	}
	
	public static String getPersistableOid(Persistable p){
		String oid = null;
		try {
			 oid = new ReferenceFactory()
					.getReferenceString(ObjectReference
							.newObjectReference((p.getPersistInfo()
									.getObjectIdentifier())));
		} catch (WTException e) {
			e.printStackTrace();
		}
		return oid;
	}
	
	public static boolean cadsignEnabled()
	{
		try{
			String cadSignEnabled = CADSignHelper.getValueProperties("cadsign.enable");
			System.out.println("cadSignEnabled="+cadSignEnabled);
			if(cadSignEnabled==null||cadSignEnabled.trim().length()==0||!cadSignEnabled.equalsIgnoreCase("true"))
				return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}		
		return true;
	}
    /**
     * 根据给定的key从properties文件读取信息
     * 10:06:21 AM
     * @param key
     * @param propertiefile
     * @return
     * @throws UnsupportedEncodingException
     * @throws WTException
     */
    public static String getValueProperties(String key) throws Exception {
        String strinfo = null;
        try{            
            PropertyResourceBundle prBundle = (PropertyResourceBundle)PropertyResourceBundle.getBundle(PROPERTIES);
            byte[] temp =null;
            temp = key.getBytes("GB2312");
            key=new String(temp,"ISO-8859-1");
            temp = prBundle.getString(key).getBytes("ISO-8859-1");
            strinfo=new String(temp,"GB2312");
        }catch(java.util.MissingResourceException mre){
        	mre.printStackTrace();
        }catch(UnsupportedEncodingException uee){ 
        	uee.printStackTrace(); 
        }
        if(strinfo == null){
        	throw new Exception("得到空值,请检查,资源文件：" + PROPERTIES+".proerties" + ",key:" + key);
        }
        return strinfo;        
    }

	/**
	 * 测试正确 获得文档的最新大版本的最新小版本
	 * 
	 * @param String
	 *            documentNumber
	 * 
	 * return type WTDocument
	 * @throws WTException 
	 */
	public static WTDocument getLatestVersionWTDocument(String documentNumber) throws WTException{
		WTDocument thedoc = null;
		try{
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition temp = new SearchCondition(WTDocument.class,
					WTDocument.NUMBER, SearchCondition.EQUAL, documentNumber
							.toUpperCase());
			qs.appendSearchCondition(temp);
			qs.appendAnd();
			SearchCondition latest = VersionControlHelper.getSearchCondition(
					WTDocument.class, true);
			qs.appendSearchCondition(latest);
			// qr里存储的是每个大版本的最新小版本,按顺序存放如：A.2,B.1,C.4
			// 如果要取得C.4，则需要while循环到最后
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				thedoc = (WTDocument) qr.nextElement(); 
			}
		}catch(WTException e){
			e.printStackTrace();
		}
		if(thedoc == null){
			throw new WTException("无法获取编号为  " + documentNumber + "  的文档对象"); 
		}
		return thedoc;
	}
}
