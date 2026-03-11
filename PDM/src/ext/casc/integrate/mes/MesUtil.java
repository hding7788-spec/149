package ext.casc.integrate.mes;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
import wt.epm.build.EPMBuildRule;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.part.WTPartReferenceLink;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;

import com.glaway.mpm.util.GLLogger;
import com.ptc.wvs.server.util.PublishUtils;
import com.ptc.wvs.server.util.RepUpdateUtils;
import com.ptc.wvs.server.util.Util;

import ext.casc.util.IBAHelper;
import ext.casc.util.WCUtil;

import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import com.ptc.windchill.enterprise.part.mvc.builders.RelatedCaddynamicDocumentTableBuilder;

public class MesUtil {

	private static String wt_temp;

	static {
		try {
			WTProperties pro = WTProperties.getLocalProperties();
			wt_temp = pro.getProperty("wt.temp");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过对象获取默认表示法的PVS相关文件
	 *
	 * @param Persistable
	 * @return
	 * @throws IOException
	 */
	public static String getPvsFile(Persistable per, String number) throws IOException {
		String fileName = "";
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
					if (tempFileName.endsWith(".pvs") || tempFileName.endsWith(".prt")) {
						fileName = tempFileName;
					}

					if(number.contains(".")) {
						number = number.substring(0, number.lastIndexOf('.'));
					}
					String fileDir = wt_temp + File.separator + "IXBExpImp" + File.separator + number + File.separator + number;
					File dir = new File(fileDir);
                	if(!dir.exists()) {
                		dir.mkdirs();
                	}

					String path = fileDir + File.separator + new String(tempFileName.getBytes(), Charset.forName("GB2312"));

					is = ContentServerHelper.service.findContentStream(applicationdata1);
					File file = new File(path);

					fos = new FileOutputStream(file);
					int j = 0;
					while ((j = is.read(buf, 0, buf.length)) >= 0) {
						fos.write(buf, 0, j);
					}
					fos.close();
					// ContentServerHelper.service.writeContentStream(applicationdata1, path);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fos != null) {
				fos.close();
			}
			if(is != null) {
				is.close();
			}
		}
		return fileName;
	}

	public static MesFileBean getEpmDrwDocumentByPart2(WTPart part,String partNumber) throws Exception  {
		String path = wt_temp + File.separator + "IXBExpImp" + File.separator + partNumber + File.separator;
		QueryResult qResult = PartDocServiceCommand.getAssociatedCADDocuments(part);
		while (qResult.hasMoreElements()) {
			Object object = qResult.nextElement();
			if (object instanceof EPMDocument) {
				EPMDocument epmDocument = (EPMDocument) object;
				String docType = epmDocument.getDocType().toString();
				if("CADDRAWING".equals(docType)) {
					Representation representation = RepresentationHelper.service.getDefaultRepresentation(epmDocument);
			    	if (representation != null) {
			            representation = (Representation) ContentHelper.service.getContents(representation);
			            Vector vector1 = ContentHelper.getContentList(representation);
			            for (int l = 0; l < vector1.size(); l++) {
			                ContentItem contentitem = (ContentItem) vector1.elementAt(l);
			                if (contentitem instanceof ApplicationData) {
			                    ApplicationData data = (ApplicationData) contentitem;
			                    String filename = data.getFileName();
			                    String extention = Util.getExtension(filename);
			                    if (extention.equalsIgnoreCase("PDF")) {
			                    	String number = epmDocument.getNumber();
			            			if(number.contains(".")) {
			            				number = number.substring(0, number.lastIndexOf('.'));
			            			}
			                    	String fileDir = path + number;
			                    	File dir = new File(fileDir);
			                    	if(!dir.exists()) {
			                    		dir.mkdirs();
			                    	}

			                    	String filePath = fileDir + File.separator + filename;
			                    	downloadAttachPdf(data,filePath);

			                    	MesFileBean bean = new MesFileBean();
			                    	bean.setNumber(epmDocument.getNumber());
			                    	bean.setName(epmDocument.getName());
			                    	bean.setVersion(epmDocument.getVersionIdentifier().getValue()+"."+epmDocument.getIterationIdentifier().getValue());
			                    	bean.setFilePath(filename);

			                    	return bean;
			                    }
			                }
			            }
			    	}
				}
			}
		}
		return null;
	}

	/**
	 * 通过零件和零件的视图，获取最新视图版本的零件对象
	 *
	 * @param partNumber
	 * @param viewOid
	 * @return
	 * @throws WTException
	 *
	 */
	@SuppressWarnings("deprecation")
	public static WTPart getLatestPartByNumberAndView(String partNumber, long viewOid) throws WTException {
		QuerySpec qs = new QuerySpec(WTPart.class);
		qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, partNumber),
				new int[] { 0 });

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, viewOid),
				new int[] { 0 });

		qs.setAdvancedQueryEnabled(true);

		QueryResult qr = PersistenceHelper.manager.find(qs);
		LatestConfigSpec lc = new LatestConfigSpec();
		qr = lc.process(qr);
		if (qr.hasMoreElements()) {
			WTPart temp = (WTPart) qr.nextElement();
			GLLogger.debug(temp.getName() + "  " + temp.getViewName() + "  " + temp.getVersionIdentifier().getValue()
					+ "." + temp.getIterationIdentifier().getValue());
			return temp;
		} else {
			return null;
		}
	}

	/**
	 * 根据视图的名称，获取零件的视图
	 *
	 * @param viewName
	 * @return
	 * @throws WTException
	 *
	 */
	@SuppressWarnings("deprecation")
	public static View getViewByName(String viewName) throws WTException {
		View view = null;
		int[] index = { 0 };
		QuerySpec qs = new QuerySpec(View.class);
		qs.appendWhere(new SearchCondition(View.class, View.NAME, SearchCondition.EQUAL, viewName, false), index);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			view = (View) qr.nextElement();
		}
		return view;
	}

	public static MesFileBean getEpmDrwDocumentByPart(WTPart part) throws Exception {
		String path = wt_temp + File.separator + "IXBExpImp" + File.separator + part.getNumber() + File.separator;
		QueryResult qResult = getEPMBuildLinksRoles(part);
		if (qResult.hasMoreElements()) {
			Object object = qResult.nextElement();
			if (object instanceof EPMBuildRule) {
				EPMBuildRule rule = (EPMBuildRule) object;
				Object ruleA = rule.getRoleAObject();
				if (ruleA instanceof EPMDocument) {
					EPMDocument epmDocument = (EPMDocument) ruleA;
					// 获得2维子件
					Set<EPMDocument> set2 = WCUtil.get2DesignDocs(epmDocument);
					Iterator<EPMDocument> iterator = set2.iterator();
					EPMDocument drwDoc = null;
					while (iterator.hasNext()) {
						drwDoc = (EPMDocument) iterator.next();
						ContentHolder contentHolder = ContentHelper.service.getContents((ContentHolder) drwDoc);
		                Vector apps = ContentHelper.getApplicationData(contentHolder);
		                for (int j = 0; j < apps.size(); j++) {
		                    ApplicationData data = (ApplicationData) apps.elementAt(j);
		                    String fileName = data.getFileName();
		                    System.out.println("---------fileName---"+fileName);
		                    if(fileName.endsWith("pdf")) {
		                    	String fileDir = path + drwDoc.getNumber();
		                    	File dir = new File(fileDir);
		                    	if(!dir.exists()) {
		                    		dir.mkdirs();
		                    	}

		                    	String filePath = fileDir + File.separator + fileName;
		                    	downloadAttachPdf(data,filePath);

		                    	MesFileBean bean = new MesFileBean();
		                    	bean.setNumber(drwDoc.getNumber());
		                    	bean.setName(drwDoc.getName());
		                    	bean.setVersion(drwDoc.getVersionIdentifier().getValue()+"."+drwDoc.getIterationIdentifier().getValue());
		                    	bean.setFilePath(fileName);
		                    	return bean;
		                    }
		                }
					}
				}
			}
		}
		return null;
	}

    /**
     * 通过指定的部件查找与其相关的所有EPMBuildLinksRule对象。
     * EPMBuildLinksRule对象是连接CAD文档和部件的link。
     *
     * @param part
     *            部件
     * @return EPMBuildLinksRule对象集合
     * @throws WTException
     */
    private static QueryResult getEPMBuildLinksRoles(WTPart part) throws WTException {
        QuerySpec qSpec = new QuerySpec(EPMBuildRule.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(part).getId();
        SearchCondition scCondition = new SearchCondition(EPMBuildRule.class, "roleBObjectRef.key.branchId", SearchCondition.EQUAL,
                longId);
        qSpec.appendWhere(scCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        return qResult;
    }

	public static List<WTDocument> getRelateDocByPart(WTPart part) throws WTException {
		List<WTDocument> list = new ArrayList<WTDocument>();
        QueryResult qResult = WTPartHelper.service.getDescribedByWTDocuments(part, false);
        WTPartDescribeLink wtPartDescribeLink = null;
        WTDocument doc = null;
        while (qResult.hasMoreElements()) {
            Object object = qResult.nextElement();
            wtPartDescribeLink = (WTPartDescribeLink) object;
            doc = (WTDocument)wtPartDescribeLink.getRoleBObject();
            String type = IBAHelper.getSoftType(doc);
        	if (!type.contains("PROCESSPLAN")) {
        		list.add(doc);
        	}

        }

        List<WTPartReferenceLink> list2 = getPartReferenceLinksByDoc(part);
        WTPartReferenceLink wtPartReferenceLink = null;
        for (int i = 0; i < list2.size(); i++) {
            wtPartReferenceLink = list2.get(i);
            WTDocumentMaster master = (WTDocumentMaster)wtPartReferenceLink.getRoleBObject();
            doc = WCUtil.getDocumentByNumber(master.getNumber());
            String type = IBAHelper.getSoftType(doc);
        	if (!type.contains("PROCESSPLAN")) {
        		list.add(doc);
        	}
        }

        return list;
	}

    /**
     * 获取文档的相关部件
     *
     * @param doc
     * @return List<WTPartDescribeLink> 相关部件的link的集合
     * @throws WTException
     */
    private static List<WTPartReferenceLink> getPartReferenceLinksByDoc(WTPart part) throws WTException {
        List<WTPartReferenceLink> list = new ArrayList<WTPartReferenceLink>();
        QuerySpec qSpec = new QuerySpec(WTPartReferenceLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(part).getId();
        SearchCondition sCondition = new SearchCondition(WTPartReferenceLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL,
                longId);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        WTPartReferenceLink link = null;
        while (qResult.hasMoreElements()) {
            link = (WTPartReferenceLink) qResult.nextElement();
            list.add(link);
        }
        return list;
    }

    public static MesFileBean getPdfFileForDoc(WTDocument doc,String partNumber) throws WTException, PropertyVetoException {
    	String path = wt_temp + File.separator + "IXBExpImp" + File.separator + partNumber + File.separator;
    	//如果是DWG，则取其附件
    	ContentItem item = (ContentItem) ContentHelper.service.getPrimary((WTDocument) doc);
        if (item != null && item instanceof ApplicationData) {
            if (((ApplicationData) item).getFileName().toUpperCase().endsWith(".DWG")){
            	ContentHolder contentHolder = ContentHelper.service.getContents((ContentHolder) doc);
                Vector apps = ContentHelper.getApplicationData(contentHolder);
                for (int j = 0; j < apps.size(); j++) {
                    ApplicationData data = (ApplicationData) apps.elementAt(j);
                    String fileName = data.getFileName();
                    if(fileName.endsWith("pdf")) {
                    	String fileDir = path + doc.getNumber();
                    	File dir = new File(fileDir);
                    	if(!dir.exists()) {
                    		dir.mkdirs();
                    	}

                    	String filePath = fileDir + File.separator + fileName;
                    	downloadAttachPdf(data,filePath);

                    	MesFileBean bean = new MesFileBean();
                    	bean.setNumber(doc.getNumber());
                    	bean.setName(doc.getName());
                    	bean.setVersion(doc.getVersionIdentifier().getValue()+"."+doc.getIterationIdentifier().getValue());
                    	bean.setFilePath(fileName);
                    	return bean;
                    }
                }
            }
        }

    	Representation representation = RepresentationHelper.service.getDefaultRepresentation(doc);
    	if (representation != null) {
            representation = (Representation) ContentHelper.service.getContents(representation);
            Vector vector1 = ContentHelper.getContentList(representation);
            for (int l = 0; l < vector1.size(); l++) {
                ContentItem contentitem = (ContentItem) vector1.elementAt(l);
                if (contentitem instanceof ApplicationData) {
                    ApplicationData data = (ApplicationData) contentitem;
                    String filename = data.getFileName();
                    String extention = Util.getExtension(filename);
                    if (extention.equalsIgnoreCase("PDF")) {
                    	String fileDir = path + doc.getNumber();
                    	File dir = new File(fileDir);
                    	if(!dir.exists()) {
                    		dir.mkdirs();
                    	}

                    	String filePath = fileDir + File.separator + filename;
                    	downloadAttachPdf(data,filePath);

                    	MesFileBean bean = new MesFileBean();
                    	bean.setNumber(doc.getNumber());
                    	bean.setName(doc.getName());
                    	bean.setVersion(doc.getVersionIdentifier().getValue()+"."+doc.getIterationIdentifier().getValue());
                    	bean.setFilePath(filename);

                    	return bean;
                    }
                }
            }
    	}

    	return null;
    }

	public static void downloadAttachPdf(ApplicationData data,String path) {
		FileOutputStream fos = null;
		InputStream is = null;
		try {
			is = ContentServerHelper.service.findContentStream(data);
			File file = new File(path);
			byte[] buf = new byte[2048];
			fos = new FileOutputStream(file);
			int j = 0;
			while ((j = is.read(buf, 0, buf.length)) >= 0) {
				fos.write(buf, 0, j);
			}
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
