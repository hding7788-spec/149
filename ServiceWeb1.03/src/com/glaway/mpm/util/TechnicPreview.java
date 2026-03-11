package com.glaway.mpm.util;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ext.casc.sop.constants.SopConstants;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;

import com.glaway.mpm.constants.Constants;
import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.qmIntf.technics.TechnicsPreview;

public class TechnicPreview {

	private static String localCodeBase = PropertiesUtil.getLocalCodeBase();
	private static String basePath = new PropertiesUtil(PropertiesConfigs.TEMP_PATH_CONFIG_PATH)
			.getProperty("PUBLISH_FILE");


	/**
	 * 获取发布地址
	 *
	 * @author lbzhang
	 * @date 2012-11-10下午01:31:08
	 * @param oid
	 * @return
	 */
	public String preview(String oid, String type) {
		String url = "";
		try {
			Object obj = ReferenceFactory.getObjectbyOid(oid);
			GLLogger.debug("publish obj===>" + obj.getClass());
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				String path = localCodeBase + File.separator + "temp" + File.separator + "publish";
				File file = new File(path);
				if(!file.exists()){
					file.mkdirs();
				}
				String fileName = getPartAttachment(part, path, null, type);
				GLLogger.debug("fileName===>" + fileName);
				GLLogger.debug("path======>" + path);
				url = TechnicsPreview.preview(path + File.separator + fileName, path);
				GLLogger.debug("url====11>" + url);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return url;
	}

	public String preview(String oid, String technicName, String type) {
		String url = "";
		try {
			Object obj = ReferenceFactory.getObjectbyOid(oid);
			GLLogger.debug("publish obj===>" + obj.getClass());
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				String path = localCodeBase + File.separator + "temp" + File.separator + "publish";
				File file = new File(path);
				if(!file.exists()){
					file.mkdirs();
				}
				String fileName = getPartAttachment(part, path, technicName, type);
				GLLogger.debug("fileName===>" + fileName);
				GLLogger.debug("path======>" + path);
				url = TechnicsPreview.preview(path + File.separator + fileName, path);
				GLLogger.debug("url====11>" + url);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return url;
	}

	public String preview(String oid) {
		String url = "";
		try {
			Object obj = Util.getObjectByOid(WTDocument.class, oid);
			GLLogger.debug("publish obj===>" + obj.getClass());
			if (obj instanceof WTDocument) {
				WTDocument doc = (WTDocument) obj;
				String path = localCodeBase + File.separator + "temp" + File.separator + "publish";
				File file = new File(path);
				if(!file.exists()){
					file.mkdirs();
				}
				String fileName = getPartAttachment(doc, path);
				String fileNameSub = fileName.replaceAll(".zip", "");
				GLLogger.debug("fileName===>" + fileName);
				GLLogger.debug("path======>" + path);
				String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
				if (docType.contains(SopConstants.SOP_TYPE_SOPDOC)) {
					url = TechnicsPreview.previewSop(path + File.separator + fileName, path + File.separator + fileNameSub);
				}else{
					url = TechnicsPreview.preview(path + File.separator + fileName, path + File.separator + fileNameSub);
				}
				GLLogger.debug("url====11>" + url);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}

	public String fillTime(String oid, String technicName, String type) {
		String url = "";
		try {
			Object obj = ReferenceFactory.getObjectbyOid(oid);
			GLLogger.debug("publish obj===>" + obj.getClass());
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				String path = localCodeBase + File.separator + "temp" + File.separator + "publish";
				File file = new File(path);
				if(!file.exists()){
					file.mkdirs();
				}
				String fileName = getPartAttachment(part, path, technicName, type);
				GLLogger.debug("fileName===>" + fileName);
				GLLogger.debug("path======>" + path);
				url = TechnicsPreview.preview(path + File.separator + fileName, path);//定额发布
				GLLogger.debug("url====11>" + url);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return url;
	}

	public String fillTime(WTDocument doc) {
		String url = "";
		String path = localCodeBase + File.separator + "temp" + File.separator + "publish";
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		String fileName = getPartAttachment(doc, path);
		GLLogger.debug("fileName===>" + fileName);
		GLLogger.debug("path======>" + path);
		url = TechnicsPreview.preview(path + File.separator + fileName, path);
		GLLogger.debug("url====11>" + url);
		return url;
	}

	/**
	 * 获取零件的附件
	 *
	 * @author lbzhang
	 * @date 2012-11-8下午07:15:28
	 * @param part
	 * @param path
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String getPartAttachment(WTPart part, String path, String technicName, String type) {
		String fileName = "";
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			WTDocument doc = null;
			if (type.equals(Constants.normalProcess)) {
				List<WTDocument> list = ProcessPlanHelper.getProcessZipDoc(part, null, "",Constants.normalProcess);
				if (list.size() != 0) {
					doc = list.get(0);
				}
			} else if (type.equals(Constants.reworkProcess)) {
				List<WTDocument> list = ProcessPlanHelper.getProcessZipDoc(part, technicName, "",Constants.reworkProcess);
				if (list.size() != 0) {
					doc = list.get(0);
				}
			} else {
				List<WTDocument> list = ProcessPlanHelper.getProcessZipDoc(part, technicName, "",Constants.tempProcess);
				if (list.size() != 0) {
					doc = list.get(0);
				}
			}

			if (doc == null) {
				GLLogger.debug("the task of doc ====>" + doc);
				return "";
			}
			GLLogger.debug("doc>>>>>" + doc.getNumber() + "   " + doc.getName() + "  "
					+ doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());
			FormatContentHolder holder = (FormatContentHolder) ContentHelper.service.getContents(doc);
			ApplicationData currdata = (ApplicationData) ContentHelper.service.getPrimary(holder);

			is = ContentServerHelper.service.findContentStream(currdata);
			fileName = currdata.getFileName();
			GLLogger.debug("the part Attachment file name:" + fileName);
			fos = new FileOutputStream(new File(path + File.separator + fileName));
			int i = 0;
			byte abyte[] = new byte[8192];
			while ((i = is.read(abyte, 0, abyte.length)) >= 0) {
				fos.write(abyte, 0, i);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return fileName;
	}

	/**
	 * 获取工艺文档zip包
	 * @author lbzhang
	 * @date  2013-7-18
	 * @param part
	 * @param technicName
	 * @param type
	 * @return
	 * @throws WTRuntimeException
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 *
	 */
	public static WTDocument getZipDoc(WTPart part, String technicName, String type) throws WTRuntimeException, WTPropertyVetoException, WTException {
		WTDocument doc = null;
		if (type.equals(Constants.normalProcess)) {
			List<WTDocument> list = ProcessPlanHelper.getProcessZipDoc(part, null, "",Constants.normalProcess);
			if (list.size() != 0) {
				doc = list.get(0);
			}
		} else if (type.equals(Constants.reworkProcess)) {
			List<WTDocument> list = ProcessPlanHelper.getProcessZipDoc(part, technicName, "",Constants.reworkProcess);
			if (list.size() != 0) {
				doc = list.get(0);
			}
		} else {
			List<WTDocument> list = ProcessPlanHelper.getProcessZipDoc(part, technicName, "",Constants.tempProcess);
			if (list.size() != 0) {
				doc = list.get(0);
			}
		}

		if (doc == null) {
			GLLogger.debug("the task of doc ====>" + doc);
			return null;
		}

		return doc;

	}

	/**
	 * 获取零件的附件
	 *
	 * @author lbzhang
	 * @date 2013-7-10
	 * @param doc
	 * @param path
	 * @return
	 *
	 */
	@SuppressWarnings("deprecation")
	public String getPartAttachment(WTDocument doc, String path) {
		String fileName = "";
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			if (doc == null) {
				GLLogger.debug("the task of doc===>" + doc);
				return "";
			}
			GLLogger.debug("doc>>>>>" + doc.getNumber() + "   " + doc.getName() + "  "
					+ doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());
			FormatContentHolder holder = (FormatContentHolder) ContentHelper.service.getContents(doc);
			ApplicationData currdata = (ApplicationData) ContentHelper.service.getPrimary(holder);

			is = ContentServerHelper.service.findContentStream(currdata);
			fileName = currdata.getFileName();
			GLLogger.debug("the part Attachment file name:" + fileName);
			fos = new FileOutputStream(new File(path + File.separator + fileName));
			int i = 0;
			byte abyte[] = new byte[8192];
			while ((i = is.read(abyte, 0, abyte.length)) >= 0) {
				fos.write(abyte, 0, i);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return fileName;
	}

	public static void main(String args[]) {
		RemoteMethodServer server = RemoteMethodServer.getDefault();
		server.setUserName("wcadmin");
		server.setPassword("wcadmin");
		String path = localCodeBase + basePath;
		System.out.println("part====>" + path);
	}
}
