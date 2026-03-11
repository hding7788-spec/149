package ext.casc.download;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.part.WTPartReferenceLink;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representation;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.vc.Iterated;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.config.LatestConfigSpec;

import com.glaway.mpm.util.Constant;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.wvs.server.util.PublishUtils;
import com.ptc.wvs.server.util.RepUpdateUtils;

import ext.casc.cadsign.wcserver.ZipFileUtil;
import ext.casc.integrate.util.ZipUtil;
import ext.casc.util.IBAHelper;
import ext.casc.util.WCUtil;

public class DownloadFormProcessor {

	private String tempPath = PropertiesUtil.getTempPath();

	public String packetsProcessor(String oid) throws Exception {
		System.out.println("----离线数据下载----------------start-------------");
		Object object = new ReferenceFactory().getReference(oid).getObject();
		String floderName = "";
		String pbomFloder = "";
		String ebomFloder = "";
		if (object instanceof WTPart) {
			WTPart pbomPart = (WTPart) object;
			String partPhaseCode = getString(IBAHelper.getIBAStringValue(pbomPart, "PHASE_CODE"));// PBOM阶段标记
			String partBatch = getString(IBAHelper.getIBAStringValue(pbomPart, "BATCH"));// PBOM批次
			String partState = getString(pbomPart.getState().toString());// PBOM状态
			String partCindex = getString(IBAHelper.getIBAStringValue(pbomPart, "CINDEX"));// 图号
			String partVersion = getString(VersionControlHelper.getVersionIdentifier((Versioned) pbomPart).getValue()); // PBOM版本
			System.out.println("阶段标记：" + partPhaseCode + "\r\n" + "批次:" + partBatch + "\r\n" + "状态:" + partState + "\r\n" + "图号" + partCindex + "\r\n" + "版本:" + partVersion);
			Date date = new Date();
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			String downloadTime = format.format(date); // 下载时间
			floderName = partCindex + "_" + partVersion + "_" + partState + "_" + partPhaseCode + "_" + partBatch + "_" + downloadTime;
			pbomFloder = "PBOM文档";
			ebomFloder = "EBOM文档";
			// 参考文档
			// QueryResult result = PersistenceHelper.manager.navigate(part,
			// WTPartReferenceLink.REFERENCES_ROLE,
			// WTPartReferenceLink.class,true);
			// while(result.hasMoreElements()){
			// WTDocumentMaster master = (WTDocumentMaster)result.nextElement();
			// }
			/**
			 * 收集PBOM的相关数据 1.结构化工艺：web预览、电子签名pdf文件 2.非结构化工艺：电子签名pdf文件
			 */
			QueryResult qr = wt.part.WTPartHelper.service.getDescribedByWTDocuments(pbomPart, true);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTDocument) {
					WTDocument document = (WTDocument) obj;
					// 取文档的最新受控对象、且版本批次与PBOM相同
					WTDocument doc = filterDocument(document, partPhaseCode, partBatch);
					if (doc != null) {
						String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
						String docNumber = doc.getNumber();
						// PBOM存放路径
						String pbomPath = tempPath + File.separator + floderName + File.separator + pbomFloder + File.separator + docNumber;
						// pbomPath = new String(pbomPath.getBytes("utf-8"),
						// "iso-8859-1");
						// 结构化工艺
						if (docType.contains("casc.sast.149.PROCESS_PLAN")) {
							// 结构化工艺取web预览，以及PDF签名文件
							downloadMainContent(doc, pbomPath);// 下载主内容
							downloadPrintPdf(doc, pbomPath);// 下载电子签名
						} else {
							// 非结构化工艺（是否存在特殊？待测试）
							downloadPrintPdf(doc, pbomPath);// 非结构化工艺下载电子签名PDF
						}
					}
				}
			}
			/**
			 * 收集EBOM相关数据 1.技术文件的电子签名pdf文件 2.图样的电子签名pdf文件 3.模型的可视化文件（PVZ）
			 */
			String partNumber = pbomPart.getNumber();
			WTPart ebomPart = WTPartUtil.getPartByNumberAndView(partNumber, Constant.EBOM_VIEW);
			// 获取ebom的参考文档,即技术文件
			List<WTPartReferenceLink> referenceLinkList = getPartReferenceLinksByDoc(ebomPart);
			WTPartReferenceLink wtPartReferenceLink = null;
			for (int i = 0; i < referenceLinkList.size(); i++) {
				wtPartReferenceLink = referenceLinkList.get(i);
				WTDocumentMaster master = (WTDocumentMaster) wtPartReferenceLink.getRoleBObject();
				WTDocument document = WCUtil.getDocumentByNumber(master.getNumber());
				// 取文档的最新受控对象
				WTDocument doc = filterDocument(document, partPhaseCode, partBatch);
				String ebomPath = tempPath + File.separator + floderName + File.separator + ebomFloder;
				// ebomPath = new String(ebomPath.getBytes("utf-8"),
				// "iso-8859-1");
				// 下载技术文件pdf签名文件
				if (doc != null) {
					downloadPrintPdf(document, ebomPath);
				}
			}
			// 获取ebom二维图样
			EPMDocument epmDoc = WTPartUtil.get2DEPMDocumentByPart(ebomPart);
			String epmDocPath = tempPath + File.separator + floderName + File.separator + ebomFloder;
			File filePath = new File(epmDocPath);
			if(!filePath.exists()){
				filePath.mkdirs();
			}
			if (epmDoc != null) {
				downloadPrintPdf(epmDoc, epmDocPath);
			}
			// 获取ebom模型的可视化（PVZ）
			downloadPvzFile(ebomPart, epmDocPath);
		}
		System.out.println("----离线数据下载----------------end-------------");
		String zipFilePath = tempPath + File.separator + floderName;
		ZipFileUtil.zipFile2(zipFilePath, zipFilePath + ".zip");
		System.out.println("--------------数据打包完成------------");
		return zipFilePath + ".zip";
	}

	/**
	 * 下载电子签名
	 *
	 * @param doc
	 * @param downloadPath
	 */
	public static void downloadPrintPdf(WTObject obj, String downloadPath) {
		String fileName = "";
		FileOutputStream fos = null;
		ContentHolder holder = null;
		try {
			if (obj instanceof WTDocument) {
				WTDocument doc = (WTDocument) obj;
				holder = ContentHelper.service.getContents(doc);
			} else if (obj instanceof EPMDocument) {
				EPMDocument epmDoc = (EPMDocument) obj;
				holder = ContentHelper.service.getContents(epmDoc);
			}
			Vector apps = ContentHelper.getApplicationData(holder);
			for (Enumeration e = apps.elements(); e.hasMoreElements();) {
				ApplicationData contentItem = (ApplicationData) e.nextElement();
				String applicationdataRole = contentItem.getRole().toString();
				if (!"SECONDARY".equalsIgnoreCase(contentItem.getRole().toString()))
					continue;// 不是附件

				if (contentItem.getFileName().startsWith("Print_")) {
					byte[] bytes = WTDocumentUtil.applicationDataToByte(contentItem);
					fileName = contentItem.getFileName();
					fos = new FileOutputStream(downloadPath + File.separator + fileName);
					fos.write(bytes);
					fos.flush();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(fos != null){
					fos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 下载主内容
	 *
	 * @param doc
	 * @param downloadPath
	 */
	public static void downloadMainContent(WTDocument doc, String downloadPath) {
		byte[] bytes = null;
		ApplicationData data;
		try {
			data = WTDocumentUtil.getPrimaryByDocument(doc);
			bytes = WTDocumentUtil.applicationDataToByte(data);
			String fileName = data.getFileName();
			if (fileName.toLowerCase().endsWith(".zip")) {
				fileName = fileName.substring(0, fileName.length() - 4);
			}
			File dir = new File(downloadPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			ZipUtil.unZip(bytes, downloadPath);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}

	private static List<WTPartReferenceLink> getPartReferenceLinksByDoc(WTPart part) throws WTException {
		List<WTPartReferenceLink> list = new ArrayList<WTPartReferenceLink>();
		QuerySpec qSpec = new QuerySpec(WTPartReferenceLink.class);
		int[] index = { 0 };
		long longId = PersistenceHelper.getObjectIdentifier(part).getId();
		SearchCondition sCondition = new SearchCondition(WTPartReferenceLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, longId);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		WTPartReferenceLink link = null;
		while (qResult.hasMoreElements()) {
			link = (WTPartReferenceLink) qResult.nextElement();
			list.add(link);
		}
		return list;
	}

	public static String getString(String str) {
		if (str == null) {
			str = "";
		}
		return str;
	}

	private static WTDocument filterDocument(WTDocument document, String partPhaseCode, String partBatch) {
		// 取文档的最新受控对象
		WTDocument doc = null;
		try {
			QueryResult allIterations = VersionControlHelper.service.allVersionsFrom((Versioned) document);
			if (allIterations != null) {
				while (allIterations.hasMoreElements()) {
					doc = (WTDocument) allIterations.nextElement();
					String state2 = doc.getState().toString();
					String v1 = VersionControlHelper.getVersionIdentifier((Versioned) doc).getValue();
					String v2 = VersionControlHelper.getIterationIdentifier((Iterated) doc).getValue();
					System.out.println(doc.getNumber() + "," + state2 + "," + v1 + "," + v2);
					if (state2.equals("APPROVED")) {
						break;
					} else {
						doc = null;
					}
				}
			}
			if (doc != null) {
				String docPhaseCode = getString(IBAHelper.getIBAStringValue(doc, "PHASE_CODE"));// 文档阶段
				String docBatch = getString(IBAHelper.getIBAStringValue(doc, "BATCH"));// 文档批次
				// 文档版本号、批次号与PBOM版本批次号相同
				if (docPhaseCode.equals(partPhaseCode) && docBatch.equals(partBatch)) {
					return doc;
				} else {
					doc = null;
				}
			}
		} catch (PersistenceException e) {
			e.printStackTrace();
		} catch (VersionControlException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return doc;
	}

	public static void downloadPvzFile(Persistable per, String path) throws IOException {
		FileOutputStream fos = null;
		InputStream is = null;
		try {
			QueryResult qrRep = PublishUtils.getRepresentations(per);
			byte[] buf = new byte[2048];
			while (qrRep.hasMoreElements()) {
				Object object = qrRep.nextElement();
				Representation representation = (Representation) object;
				representation = (Representation) ContentHelper.service.getContents(representation);
				Vector vector1 = ContentHelper.getContentList(representation);
				for (int l = 0; l < vector1.size(); l++) {
					ContentItem contentitem = (ContentItem) vector1.elementAt(l);
					if (!(contentitem instanceof ApplicationData)) {
						continue;
					}
					ApplicationData data = (ApplicationData) contentitem;
					if (data.getRole() != ContentRoleType.PRODUCT_VIEW_ED) {
						continue;
					}
					ApplicationData data3 = RepUpdateUtils.processDeferredUpdateRepresentation(data, representation);
					if (data3 != null) {
						representation = (Representation) ContentHelper.service.getContents(representation);
						vector1 = ContentHelper.getContentList(representation);
					}
					break;
				}

				for (int j1 = 0; j1 < vector1.size(); j1++) {
					ContentItem contentitem1 = (ContentItem) vector1.elementAt(j1);
					if (!(contentitem1 instanceof ApplicationData)) {
						continue;
					}
					ApplicationData applicationdata1 = (ApplicationData) contentitem1;
					String tempFileName = applicationdata1.getFileName();
					if (tempFileName.endsWith(".pvs") || tempFileName.endsWith(".pvz")) {
						is = ContentServerHelper.service.findContentStream(applicationdata1);
						File file = new File(path + File.separator + tempFileName);

						fos = new FileOutputStream(file);
						int j = 0;
						while ((j = is.read(buf, 0, buf.length)) >= 0) {
							fos.write(buf, 0, j);
						}
					}

				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				fos.close();
			}
			if (is != null) {
				is.close();
			}
		}
	}

}
