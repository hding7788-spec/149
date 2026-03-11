package com.glaway.mpm.util;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.ixb.clientAccess.StandardIXBService;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.viewmarkup.Viewable;
import wt.wvs.VisualizationHelperFactory;

import com.ptc.wvs.common.ui.PublishResult;
import com.ptc.wvs.common.util.WVSProperties;
import com.ptc.wvs.server.publish.Publish;
import com.ptc.wvs.server.ui.RepHelper;
import com.ptc.wvs.server.util.PublishUtils;

public class RepUtils {
	public static final String DWG_PDF_REP_NAME = "GUAZAI";
	public static final String DWG_PDF_REP_DESC = "GUAZAI->PDF挂载";

	/**
	 * �����ļ�����ʾ��
	 *
	 * @param obj
	 * @param file
	 * @return
	 * @throws WTException
	 * @throws IOException
	 */
	public static boolean saveFileRep(WTObject obj, File file) throws WTException, IOException {
		boolean flag = false;

		SessionContext sessioncontext = SessionContext.newContext();

		SessionHelper.manager.setAdministrator();

		String oid = obj.getPersistInfo().getObjectIdentifier()
				.getStringValue();

		String fileName = null;

		if (obj instanceof WTDocument) {
			WTDocument doc = (WTDocument) obj;
			fileName = doc.getName();
		} else if (obj instanceof EPMDocument) {
			EPMDocument epm = (EPMDocument) obj;
			fileName = epm.getName();
		}

		File tmpfile = StandardIXBService.getSaveFileOnServer();
		if (!tmpfile.exists()) {
			tmpfile.mkdir();
		}
		File oFile = new File(tmpfile, fileName + ".pdf");

		FileUtils.copyFile(file, oFile);

		boolean isDwg = RepHelper.doesRepNameExist(oid, DWG_PDF_REP_NAME);
		if (isDwg) {
			Persistable localPersistable = PublishUtils.getObjectFromRef(oid);
			QueryResult localQueryResult = PublishUtils.getRepresentations(localPersistable);
			while (localQueryResult.hasMoreElements()) {
				Representation localRepresentation = (Representation) localQueryResult.nextElement();
				if (localRepresentation.getName().equals(DWG_PDF_REP_NAME)) {
					if (localRepresentation.getDefaultRepresentation()) {
						if (obj instanceof WTDocument) {
							WTDocument doc = (WTDocument) obj;
							boolean bool = SessionServerHelper.manager.setAccessEnforced(false);
							try {
								RepresentationHelper.service.setDefaultRepresentation(doc, localRepresentation, false);
							} catch (PropertyVetoException e) {
								e.printStackTrace();
							} finally {
								SessionServerHelper.manager.setAccessEnforced(bool);
							}
						} else if (obj instanceof EPMDocument) {
							EPMDocument epm = (EPMDocument) obj;
							boolean bool = SessionServerHelper.manager.setAccessEnforced(false);
							try {
								RepresentationHelper.service.setDefaultRepresentation(epm, localRepresentation, false);
							} catch (PropertyVetoException e) {
								e.printStackTrace();
							} finally {
								SessionServerHelper.manager.setAccessEnforced(bool);
							}
						}
					} else {
						RepresentationHelper.service.deleteRepresentation(localRepresentation);
					}
				}
			}
		}

		flag = RepHelper.loadRepresentation(tmpfile.getCanonicalPath(), oid,
				true, DWG_PDF_REP_NAME, DWG_PDF_REP_DESC, false,
				VisualizationHelperFactory.HELPER.isThumbnailEnabled(), false);

		tmpfile.delete();

		SessionContext.setContext(sessioncontext);
		return flag;
	}

	public static boolean saveFileRep2(WTObject obj, InputStream inputStream,String fileName)
			throws WTException, IOException {
		boolean flag = false;

		SessionContext sessioncontext = SessionContext.newContext();

		SessionHelper.manager.setAdministrator();

		String oid = obj.getPersistInfo().getObjectIdentifier().getStringValue();

		File tmpfile = StandardIXBService.getSaveFileOnServer();
		if (!tmpfile.exists()) {
			tmpfile.mkdir();
		}
		File oFile = new File(tmpfile, fileName);

		FileUtils.copyInputStreamToFile(inputStream, oFile);

		boolean isDwg = RepHelper.doesRepNameExist(oid, DWG_PDF_REP_NAME);
		if (isDwg) {
			Persistable localPersistable = PublishUtils.getObjectFromRef(oid);
			QueryResult localQueryResult = PublishUtils.getRepresentations(localPersistable);
			while (localQueryResult.hasMoreElements()) {
				Representation localRepresentation = (Representation) localQueryResult.nextElement();
				if (localRepresentation.getName().equals(DWG_PDF_REP_NAME)) {
					if (localRepresentation.getDefaultRepresentation()) {
						if (obj instanceof WTDocument) {
							WTDocument doc = (WTDocument) obj;
							boolean bool = SessionServerHelper.manager.setAccessEnforced(false);
							try {
								RepresentationHelper.service.setDefaultRepresentation(doc, localRepresentation, false);
							} catch (PropertyVetoException e) {
								e.printStackTrace();
							} finally {
								SessionServerHelper.manager.setAccessEnforced(bool);
							}
						} else if (obj instanceof EPMDocument) {
							EPMDocument epm = (EPMDocument) obj;
							boolean bool = SessionServerHelper.manager.setAccessEnforced(false);
							try {
								RepresentationHelper.service.setDefaultRepresentation(epm, localRepresentation, false);
							} catch (PropertyVetoException e) {
								e.printStackTrace();
							} finally {
								SessionServerHelper.manager.setAccessEnforced(bool);
							}
						}
					} else {
						RepresentationHelper.service.deleteRepresentation(localRepresentation);
					}
				}
			}
		}

		flag = RepHelper.loadRepresentation(tmpfile.getCanonicalPath(), oid,
				true, DWG_PDF_REP_NAME, DWG_PDF_REP_DESC, false,
				VisualizationHelperFactory.HELPER.isThumbnailEnabled(), false);

		//tmpfile.delete();
		FileUtil.deleteFile(tmpfile);

		SessionContext.setContext(sessioncontext);
		return flag;
	}

	/**
	 * �����ļ�����ʾ��
	 *
	 * @param obj
	 * @param file
	 * @return
	 * @throws WTException
	 * @throws IOException
	 */
	public static boolean saveSignFileRep(WTObject obj, File file) throws WTException, IOException {
		boolean flag = false;

		SessionContext sessioncontext = SessionContext.newContext();

		SessionHelper.manager.setAdministrator();

		String oid = obj.getPersistInfo().getObjectIdentifier().getStringValue();

		String fileName = null;

		if (obj instanceof WTDocument) {
			WTDocument doc = (WTDocument) obj;
			fileName = doc.getName();
		} else if (obj instanceof EPMDocument) {
			EPMDocument epm = (EPMDocument) obj;
			fileName = epm.getName();
		}

		File tmpfile = StandardIXBService.getSaveFileOnServer();
		if (!tmpfile.exists()) {
			tmpfile.mkdir();
		}
		File oFile = new File(tmpfile, fileName + ".pdf");

		FileUtils.copyFile(file, oFile);

		boolean isDwg = RepHelper.doesRepNameExist(oid, "SignPdf");
		if (isDwg) {
			Persistable localPersistable = PublishUtils.getObjectFromRef(oid);
			QueryResult localQueryResult = PublishUtils.getRepresentations(localPersistable);
			while (localQueryResult.hasMoreElements()) {
				Representation localRepresentation = (Representation) localQueryResult.nextElement();
				if (localRepresentation.getName().equals("SignPdf")) {
					if (localRepresentation.getDefaultRepresentation()) {

					} else {
						RepresentationHelper.service.deleteRepresentation(localRepresentation);
					}
				} else if (localRepresentation.getName().endsWith(".pdf")) {
					if (obj instanceof WTDocument) {
						WTDocument doc = (WTDocument) obj;
						boolean bool = SessionServerHelper.manager.setAccessEnforced(false);
						try {
							RepresentationHelper.service
									.setDefaultRepresentation(doc,
											localRepresentation, false);
						} catch (PropertyVetoException e) {
							e.printStackTrace();
						} finally {
							SessionServerHelper.manager.setAccessEnforced(bool);
						}
					} else if (obj instanceof EPMDocument) {
						EPMDocument epm = (EPMDocument) obj;
						boolean bool = SessionServerHelper.manager.setAccessEnforced(false);
						try {
							RepresentationHelper.service.setDefaultRepresentation(epm, localRepresentation, false);
						} catch (PropertyVetoException e) {
							e.printStackTrace();
						} finally {
							SessionServerHelper.manager.setAccessEnforced(bool);
						}
					}
				}
			}
		}

		flag = RepHelper.loadRepresentation(tmpfile.getCanonicalPath(), oid,
				true, "SignPdf", "SignPdf", true,
				VisualizationHelperFactory.HELPER.isThumbnailEnabled(), false);

		tmpfile.delete();

		SessionContext.setContext(sessioncontext);
		return flag;
	}

	public static void publish(Viewable viewable, File file) throws IOException, WTException {
		String PUBLISHTEMPUPLOADDIR = null;
		WVSProperties wvsProperties = new WVSProperties();
		PUBLISHTEMPUPLOADDIR = wvsProperties.getPublishTempUploadDir();
		if (viewable == null || file == null) {
			return;
		}
		String fileName = file.getName();
		ReferenceFactory rf = new ReferenceFactory();
		String objRef = rf.getReferenceString(viewable);
		// String extension = Util.getExtension(filePath).toLowerCase();
		File pubTempUploadDir = new File(PUBLISHTEMPUPLOADDIR);
		if (!pubTempUploadDir.exists()) {
			pubTempUploadDir.mkdirs();
		}
		File tempFile = File.createTempFile(fileName + "_Sign", ".pdf", pubTempUploadDir);
		String tempFileName = tempFile.getName();
		FileOutputStream fos = new FileOutputStream(tempFile);
		byte[] arrayOfByte = new byte[1024];
		FileInputStream localMPInputStream = new FileInputStream(file);
		long l1 = 0L;
		int l = 0;
		while ((l = localMPInputStream.read(arrayOfByte, 0, 1024)) > 0) {
			l1 += l;
			fos.write(arrayOfByte, 0, l);
		}
		fos.close();

		if (l1 == 0L) {
			((File) tempFile).delete();
			return;
		}

		// PublishResult publishResult = Publish.doPublish(false, true, objRef,
		// null, (String)null,
		// true, fileName+"_Sign.pdf", "", 0, "<wvsoptions>tempfile=" +
		// tempFileName
		// + ",origfile=" + fileName
		// + ",remotehost=127.0.0.1,thumbnail=true", 1);
		PublishResult publishResult = Publish.doPublish(false, true, objRef,
				null, (String) null, true, fileName + "_Sign.pdf", "", 0, null,
				1);
	}
}
