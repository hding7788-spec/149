package com.glaway.mpm.dwg2pdf;

import java.beans.PropertyVetoException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.fc.ObjectIdentifier;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.org.UserNotFoundException;
import wt.org.WTPrincipal;
import wt.pom.ObjectIsStaleException;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.pom.UniquenessException;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.services.ManagerException;
import wt.services.StandardManager;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.wvs.server.util.Util;

import ext.glaway.dwg2pdf.client.Dwg2PdfClient;
//import ext.sast.center.util.RestMessageQueue;

public class StandardDwgService extends StandardManager implements DwgService, Serializable {

	private static final long serialVersionUID = 1L;
	public static final String DWG_PDF_REP_NAME = "DWG";
	public static final String DWG_PDF_REP_DESC = "DWG->PDF表示法";
	private static final Logger log;

	static String CLASSNAME = StandardDwgService.class.getName();
	private static final String DWG_WORKER_QUEUE_NAME = "DwgWorkerQueue";
	private ProcessingQueue dwgWorkerQueue;
	public static final String DWG_PDF_FILE_FLODER;

	static {
		try {
			DWG_PDF_FILE_FLODER = LoadConfig.getInstance().getDwgFolder();
			log = LogR.getLogger(StandardDwgService.class.getName());
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static StandardDwgService newStandardDwgService() throws WTException {
		StandardDwgService standardService = new StandardDwgService();
		standardService.initialize();
		return standardService;
	}

	protected void performStartupProcess() throws ManagerException {
		log.info("DWG Worker Starting...");
		System.out.println("DWG Worker Starting...");
		SessionContext sessioncontext = SessionContext.newContext();
		try {
			try {
				SessionHelper.manager.setAdministrator();
			} catch (UserNotFoundException usernotfoundexception) {
				log.info("DWG Worker: failed to set Administrator (ok if installation)");
				return;
			}

			log.info("MQ Worker Starting...");
			//RestMessageQueue.startRestQueue();
			log.info("MQ Worker Start OK!");

			dwgWorkerQueue = QueueHelper.manager.getQueue(DWG_WORKER_QUEUE_NAME);
			if (dwgWorkerQueue == null)
				dwgWorkerQueue = createQueue(DWG_WORKER_QUEUE_NAME, 300);

			log.info("DWG Worker Start OK!");
		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw new ManagerException(this,
					"Couldn't initialize dwg worker service.");
		} finally {
			SessionContext.setContext(sessioncontext);
		}
	}

	protected void performShutdownProcess() {
		log.info("DWG Worker Service Stop.");
	}

	private ProcessingQueue createQueue(String s, int i) throws WTException {
		ProcessingQueue processingqueue = null;
		try {
			processingqueue = QueueHelper.manager.createQueue(s);
			QueueHelper.manager.setInterval(processingqueue, i);
			processingqueue = (ProcessingQueue) PersistenceHelper.manager.save(processingqueue);
		} catch (ObjectIsStaleException objectisstaleexception) {
			processingqueue = QueueHelper.manager.getQueue(s);
			log.error("createQueue exception", objectisstaleexception);
		} catch (UniquenessException uniquenessexception) {
			processingqueue = QueueHelper.manager.getQueue(s);
			log.error("createQueue exception", uniquenessexception);
		} catch (PersistenceException persistenceexception) {
			processingqueue = QueueHelper.manager.getQueue(s);
			log.error("createQueue exception", persistenceexception);
		}
		return processingqueue;
	}

	public boolean sendToDwgWorkerQueue(WTDocument doc) throws WTException {
		WTPrincipal wtprincipal=SessionHelper.manager.getAdministrator();
		if (wtprincipal == null) {
			log.info("DwgWorker:sendToDwgWorkerQueue: failed to set Administrator (ok if installation)");
			return false;
		}
        WTPrincipal wtprincipal1 = SessionContext.setEffectivePrincipal(wtprincipal);

		String uName = SessionHelper.manager.getPrincipal().getName();
    	try {
    		String oi=PersistenceHelper.getObjectIdentifier(doc).getStringValue();
    		Class aclass[] = { String.class ,String.class};
    		Object aobj[] = { oi , uName};
    		log.info("dwgWorkerQueue.addEntry: dwgWorker start");
    		dwgWorkerQueue.addEntry(wtprincipal, "dwgWorker", CLASSNAME, aclass, aobj);
    		log.info("dwgWorkerQueue.addEntry: dwgWorker end");
    	} catch (WTException exception) {
    		if (exception instanceof WTException) throw (WTException)exception;
    		else throw new WTException(exception);
    	} finally {
    		SessionContext.setEffectivePrincipal(wtprincipal1);

    	}
    	return true;
	}

	public static boolean dwgWorker(String oid, String userName) throws WTException, PropertyVetoException {
		log.info("-----dwgWorker------oid--"+oid+"-----user---"+userName);
		WTObject obj = (WTObject) PersistenceHelper.manager.refresh(ObjectIdentifier.newObjectIdentifier(oid));
		ContentHolder contentHolder = ContentHelper.service.getContents((ContentHolder) obj);
		ApplicationData ad = getPrimaryApplicationData(contentHolder);
		if(ad == null) {
			log.info("-----dwgWorker------主文件空");
			return false;
		}
		InputStream is = null;
		String fileName = null;
		String fName = null;
		InputStream pdfIs = null;
		try {
			is = ContentServerHelper.service.findContentStream(ad);
			if (is == null) {
				log.info("主文件空！");
				throw new WTException("主文件空！");
			}
			fileName = ad.getFileName();
			log.info("----fileName---"+fileName);
			String extention = Util.getExtension(fileName);
			log.info("----extention---"+extention);
			if (!extention.equalsIgnoreCase("dwg")) {
				return false;
			}
			fName = fileName.substring(0, fileName.length() - 4);
			sendFile(is, userName, fileName);
			String transfName = transPdf(userName, fileName);
			log.info("----transfName---"+transfName);
			if (transfName != null) {
				File f = new File(DWG_PDF_FILE_FLODER + userName + File.separator);
				if (!f.exists()) {
					f.mkdirs();
				}
				File file = new File(f, fName + ".pdf");
				log.info("----file---"+file);
				log.info("----file.exists()---"+file.exists());
				if (file.exists()) {
					pdfIs = new FileInputStream(file);
					Transaction trans = new Transaction();
					trans.start();
					try {
						WTDocumentUtil.uploadAttach((WTDocument) obj, fName + ".pdf", IOUtils.toByteArray(pdfIs));
						// RepUtils.saveFileRep(obj, file);
						trans.commit();
					} catch (Exception e) {
						trans.rollback();
						throw new WTException("上传附件、保存表示法异常");
					} finally {
						trans = null;
					}

				}

			}
		} catch (MalformedURLException e) {
			log.error("URL异常", e);
			throw new WTException("URL异常");
		} catch (RemoteException e) {
			log.error("DWG->PDF远程服务器异常", e);
			throw new WTException("DWG->PDF远程服务器异常");
		} catch (NotBoundException e) {
			log.error("DWG->PDF远程服务器没有绑定", e);
			throw new WTException("DWG->PDF远程服务器没有绑定");
		} catch (IOException e) {
			log.error("DWG->PDF远程服务器IO异常", e);
			throw new WTException("DWG->PDF远程服务器IO异常");
		} catch (Exception e) {
			log.error("DWG->PDF IO异常", e);
			throw new WTException("DWG->PDF IO异常");
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				log.error("DWG->PDF 关闭流IO异常", e);
				throw new WTException("DWG->PDF 关闭流IO异常");
			}
			try {
				if (pdfIs != null)
					pdfIs.close();
			} catch (IOException e) {
				log.error("DWG->PDF 关闭流IO异常", e);
				throw new WTException("DWG->PDF 关闭流IO异常");
			}
		}
		rmFile(userName, fName);

		return true;

	}

	/**
	 * 发送文件
	 *
	 * @param is
	 * @param userName
	 * @param fileName
	 * @throws IOException
	 * @throws Exception
	 */
	private static void sendFile(InputStream is, String userName,
			String fileName) throws IOException {
		File f = new File(DWG_PDF_FILE_FLODER + userName + File.separator);
		System.out.println("--------f--"+f);
		if (!f.exists()) {
			f.mkdirs();
		}
		File file = new File(f, fileName);
		byte[] buf = new byte[1024];
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			int len = 0;
			while ((len = is.read(buf)) >= 0) {
				bos.write(buf, 0, len);
			}
		} finally {
			if (bos != null)
				bos.close();
			if (fos != null)
				fos.close();
		}

	}

	/**
	 * dwg->pdf转化
	 *
	 * @param userName
	 * @param fileName
	 * @throws NotBoundException
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws Exception
	 */
	private static String transPdf(String userName, String fileName)
			throws MalformedURLException, RemoteException, NotBoundException {
		String fName = Dwg2PdfClient.getInstants().parsePdf(userName, fileName);
		return fName;
	}

	/**
	 * 删除文件
	 *
	 * @param userName
	 * @param fileName
	 */
	private static void rmFile(String userName, String fileName) {
		File f = new File(DWG_PDF_FILE_FLODER + userName);
		if (!f.exists()) {
			f.mkdir();
		}
		File dwgFile = new File(f, fileName + ".dwg");
		if (dwgFile.exists() && dwgFile.isFile()) {
			dwgFile.delete();
		}
		File pdfFile = new File(f, fileName + ".pdf");
		if (pdfFile.exists() && pdfFile.isFile()) {
			pdfFile.delete();
		}

	}

	/**
	 * 获取主文件
	 *
	 * @param holder
	 * @return
	 * @throws WTException
	 */
	private static ApplicationData getPrimaryApplicationData(ContentHolder holder) throws WTException {
		ContentItem contentitem = null;
		FormatContentHolder formatcontentholder = null;
		try {
			formatcontentholder = (FormatContentHolder) ContentHelper.service.getContents((FormatContentHolder) holder);
			contentitem = ContentHelper.getPrimary(formatcontentholder);
		} catch (java.beans.PropertyVetoException propertyvetoexception) {
			log.error("", propertyvetoexception);
		}
		if (contentitem instanceof ApplicationData) {
			ApplicationData applicationdata = (ApplicationData) contentitem;
			return applicationdata;
		}
		return null;
	}

}
