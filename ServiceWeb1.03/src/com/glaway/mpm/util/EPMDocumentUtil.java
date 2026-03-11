package com.glaway.mpm.util;

import java.beans.PropertyVetoException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMFamily;
import wt.epm.structure.EPMContainedIn;
import wt.epm.structure.EPMMemberLink;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.util.FileUtil;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.config.LatestConfigSpec;
import wt.viewmarkup.DerivedImage;
import wt.viewmarkup.ViewMarkUpHelper;
import wt.viewmarkup.Viewable;
import wt.viewmarkup.WTMarkUp;

import com.ptc.wvs.server.util.ETB;
import com.ptc.wvs.server.util.PublishUtils;
import com.ptc.wvs.server.util.Util;

public class EPMDocumentUtil implements RemoteAccess {
	static int index[] = { 0 };
	private static final String CLASSNAME = EPMDocumentUtil.class.getName();

	/**
	 * 获取图档的下皆结构link
	 *
	 * @author qianlong
	 * @date 2012-11-13
	 * @param epmDocument
	 * @return
	 * @throws WTException
	 *
	 */
	public static List<EPMMemberLink> getEPMMemberLink(EPMDocument epmDocument) throws WTException {
		List<EPMMemberLink> list = new ArrayList<EPMMemberLink>();

		if (epmDocument != null) {
			QueryResult qr = PersistenceHelper.manager.navigate(epmDocument, EPMMemberLink.USES_ROLE,
					EPMMemberLink.class, false);
			// QueryResult qr =
			// EPMStructureHelper.service.navigateUses(epmDocument,
			// new QuerySpec(),false);
			while (qr.hasMoreElements()) {
				list.add((EPMMemberLink) qr.nextElement());
			}
		}
		return list;
	}

	/**
	 * 获取图档的下皆结构图档
	 *
	 * @author qianlong
	 * @date 2012-11-13
	 * @param epmDocument
	 * @return
	 * @throws WTException
	 * @throws PersistenceException
	 *
	 */
	public static List<EPMDocument> getChildEPMDocument(EPMDocument epmDocument) throws PersistenceException,
			WTException {
		List<EPMDocument> list = new ArrayList<EPMDocument>();

		if (epmDocument != null) {
			QueryResult qr = PersistenceHelper.manager.navigate(epmDocument, EPMMemberLink.USES_ROLE,
					EPMMemberLink.class, true);
			// QueryResult qr =
			// EPMStructureHelper.service.navigateUses(epmDocument, new
			// QuerySpec(),true);
			while (qr.hasMoreElements()) {
				EPMDocumentMaster epmDocumentMaster = (EPMDocumentMaster) qr.nextElement();
				list.add(getLatestEPMDocumentByMaster(epmDocumentMaster));
			}
		}
		return list;
	}

	/**
	 * 获取整套图档
	 *
	 * @author qianlong
	 * @date 2013-7-19
	 * @param parent
	 * @param vector
	 * @throws WTException
	 */
	public static void getAllChildreanEPMDocument(EPMDocument parent, List<EPMDocument> vector) throws WTException {
		List<EPMDocument> childrenVector = getChildEPMDocument(parent);
		for (EPMDocument child : childrenVector) {
			vector.add(child);
			getAllChildreanEPMDocument(child, vector);
		}
	}

	/**
	 * 根据Master获取最新版本最新版序的图档
	 *
	 * @author qianlong
	 * @date 2012-11-13
	 * @param epmDocumentMaster
	 * @return
	 * @throws WTException
	 * @throws PersistenceException
	 *
	 */
	public static EPMDocument getLatestEPMDocumentByMaster(EPMDocumentMaster epmDocumentMaster)
			throws PersistenceException, WTException {
		EPMDocument epmDocument = null;

		if (epmDocumentMaster != null) {
			QueryResult qr = VersionControlHelper.service.allVersionsOf(epmDocumentMaster);
			if (qr.hasMoreElements()) {
				epmDocument = (EPMDocument) qr.nextElement();
			}
		}
		return epmDocument;

	}

	/*
	 * 根据图档编号获取图档的Master
	 */
	public static EPMDocumentMaster getEPMDocumentMasterByNumber(String epmDocumentNumber) throws WTException {
		EPMDocumentMaster epmDocumentMaster = null;

		QuerySpec qs = new QuerySpec(EPMDocumentMaster.class);
		qs.appendWhere(new SearchCondition(EPMDocumentMaster.class, EPMDocumentMaster.NUMBER, SearchCondition.EQUAL,
				epmDocumentNumber), index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		if (qr.hasMoreElements()) {
			epmDocumentMaster = (EPMDocumentMaster) qr.nextElement();
		}
		return epmDocumentMaster;

	}

	/*
	 * 根据图档编号获取图档
	 */
	public static EPMDocument getEPMDocumentByNumber(String number) throws WTException {
		EPMDocument epmDocument = null;

		QuerySpec qs = new QuerySpec(EPMDocument.class);
		qs
				.appendWhere(new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, SearchCondition.EQUAL, number),
						index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		if (qr.hasMoreElements()) {
			epmDocument = (EPMDocument) qr.nextElement();
		}
		return epmDocument;
	}

	/**
	 * 根据图档编号获取最新版本最新版序的图档
	 *
	 * @author qianlong
	 * @date 2012-11-13
	 * @param epmDocumentNumber
	 * @return
	 * @throws WTException
	 *
	 */
	public static EPMDocument getLatestEPMDocumentByNumber(String epmDocumentNumber) throws WTException {
		EPMDocument epmDocument = null;

		QuerySpec qSpec = new QuerySpec(EPMDocument.class);
		qSpec.appendWhere(new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, SearchCondition.EQUAL,
				epmDocumentNumber), index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		qResult = new LatestConfigSpec().process(qResult);
		while (qResult.hasMoreElements()) {
			epmDocument = (EPMDocument) qResult.nextElement();
		}
		return epmDocument;
	}

	/**
	 * 根据图档获取FamilyTable中相关的所有的图档
	 *
	 * @author qianlong
	 * @date 2013-2-5
	 * @param epmDocument
	 * @throws WTException
	 *
	 */
	public static List<EPMDocument> getAllFamilyTableEPMDocument(EPMDocument epmDocument) throws WTException {
		List<EPMDocument> list = new ArrayList<EPMDocument>();
		EPMFamily family = EPMFamily.getEPMFamily(epmDocument);
		if (family != null) {
			List<EPMContainedIn> links = family.getCompatibleLinks();
			for (EPMContainedIn link : links) {
				list.add((EPMDocument) link.getRoleAObject());
			}
		} else {
			list.add(epmDocument);
		}
		return list;
	}

	/**
	 * 获取相关的中间模型图档
	 *
	 * @author qianlong
	 * @date 2013-2-5
	 * @param epmDocument
	 * @throws WTException
	 *
	 */
	public static List<EPMDocument> getMiddleModelEPMDocument(String number) throws WTException {
		List<EPMDocument> list = new ArrayList<EPMDocument>();

		QuerySpec qs = new QuerySpec(EPMDocument.class);
//		qs.appendWhere(new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, SearchCondition.LIKE, number
//				+ Constants.middleModelNameContains + "%", false), index);
		qs.appendWhere(new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, SearchCondition.LIKE, number + "%", false), index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		while (qr.hasMoreElements()) {
			list.add((EPMDocument) qr.nextElement());
		}
		return list;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-11-13
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws IOException
	 *
	 */
	public static void getPrimaryAndMarkUpToZip(EPMDocument document, String path) {

		InputStream is = null;
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(new FileOutputStream(new File(path)));
			FormatContentHolder epmDocHolder = (FormatContentHolder) ContentHelper.service.getContents(document);
			ApplicationData data = (ApplicationData) ContentHelper.service.getPrimary(epmDocHolder);

			if (data != null) {
				is = ContentServerHelper.service.findContentStream(data);
				zos.putNextEntry(new ZipEntry(document.getCADName()));
				int i = 0;
				byte abyte[] = new byte[8192];
				while ((i = is.read(abyte, 0, abyte.length)) >= 0) {
					zos.write(abyte, 0, i);
				}
				zos.closeEntry();
				QueryResult qrRep = PublishUtils.getRepresentations(document);
				while (qrRep.hasMoreElements()) {
					Object obj = qrRep.nextElement();
					if (obj instanceof Representation) {
						if (obj instanceof Viewable) {
							QueryResult result = ViewMarkUpHelper.service.getMarkUps((Viewable) obj);
							addMarkupsToZip((Viewable) obj, result, zos, "etb.etb", "utf-8", true);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != zos) {
				try {
					zos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-11-14
	 * @param paramViewable
	 * @param paramQueryResult
	 * @param paramZipOutputStream
	 * @param paramString1
	 * @param paramString2
	 * @param paramBoolean
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws IOException
	 *
	 */
	private static void addMarkupsToZip(Viewable paramViewable, QueryResult paramQueryResult,
			ZipOutputStream paramZipOutputStream, String paramString1, String paramString2, boolean paramBoolean)
			throws WTException, PropertyVetoException, IOException {
		byte[] arrayOfByte = new byte[1024];

		InputStream localInputStream = null;
		ContentItem localContentItem = null;
		ApplicationData localApplicationData = null;

		StringBuffer localStringBuffer1 = new StringBuffer(300);
		StringBuffer localStringBuffer2 = new StringBuffer(300);

		while (paramQueryResult.hasMoreElements()) {
			WTMarkUp localWTMarkUp = (WTMarkUp) paramQueryResult.nextElement();

			String str2 = localWTMarkUp.getAdditionalInfo();
			ETB localETB = new ETB(str2);
			String str3 = localETB.getWcFile();
			String str4 = str3;
			int j = str3.indexOf("!>");
			if ((j >= 0) && (j < str3.length() - 3)) {
				str4 = str3.substring(j + 2);
				str2 = Util.SandR(str2, str3, str4);
			}

			String str5 = localETB.getTargetWcFile();
			j = str5.indexOf("!>");
			if ((j >= 0) && (j < str5.length() - 3)) {
				str2 = Util.SandR(str2, str5, str5.substring(j + 2));
			}
			localStringBuffer1.append(str2).append("\n");

			if ((localWTMarkUp.getDescription() != null) && (localWTMarkUp.getDescription().length() > 0)) {
				localStringBuffer2.append(
						localETB.getName() + "@@" + localETB.getTag() + "=" + localWTMarkUp.getDescription()).append(
						"\n");
			}

			localWTMarkUp = (WTMarkUp) ContentHelper.service.getContents(localWTMarkUp);
			Vector localVector = ContentHelper.getContentListAll(localWTMarkUp);
			for (int k = 0; k < localVector.size(); k++) {
				localContentItem = (ContentItem) localVector.elementAt(k);
				if (localContentItem instanceof ApplicationData) {
					localApplicationData = (ApplicationData) localContentItem;
					String str1;
					if (localApplicationData.getRole() == ContentRoleType.THUMBNAIL) {
						str1 = FileUtil.setExtension(str4, "gif");
					} else {
						str1 = str4;
					}

					localInputStream = ContentServerHelper.service.findContentStream(localApplicationData);
					try {
						paramZipOutputStream.putNextEntry(new ZipEntry(str1));
						int i;
						while ((i = localInputStream.read(arrayOfByte, 0, 1024)) > 0) {
							paramZipOutputStream.write(arrayOfByte, 0, i);
						}
						paramZipOutputStream.closeEntry();
					} catch (ZipException localZipException) {
					}
				}
			}
		}
		if (localStringBuffer1 != null) {
			paramZipOutputStream.putNextEntry(new ZipEntry(paramString1));
			if ((paramString2 != null) && (paramString2.length() > 0)) {
				paramZipOutputStream.write(localStringBuffer1.toString().getBytes(paramString2));
			} else {
				paramZipOutputStream.write(localStringBuffer1.toString().getBytes());
			}
			paramZipOutputStream.closeEntry();
		}

		if ((localStringBuffer2 != null) && (localStringBuffer2.length() > 0)) {
			paramZipOutputStream.putNextEntry(new ZipEntry("mkdescr.dcr"));
			if ((paramString2 != null) && (paramString2.length() > 0)) {
				paramZipOutputStream.write(localStringBuffer2.toString().getBytes(paramString2));
			} else {
				paramZipOutputStream.write(localStringBuffer2.toString().getBytes());
			}
			paramZipOutputStream.closeEntry();
		}
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-12-1
	 * @param markupResult
	 * @param etbFileName
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws IOException
	 *
	 */
	public static void getMarkUpInputStream(QueryResult markupResult, String etbFileName) throws WTException,
			PropertyVetoException, IOException {
		Map<String, InputStream> map = new HashMap<String, InputStream>();
		InputStream localInputStream = null;
		ContentItem localContentItem = null;
		ApplicationData localApplicationData = null;

		StringBuffer localStringBuffer1 = new StringBuffer(300);
		StringBuffer localStringBuffer2 = new StringBuffer(300);

		while (markupResult.hasMoreElements()) {
			WTMarkUp localWTMarkUp = (WTMarkUp) markupResult.nextElement();

			String str2 = localWTMarkUp.getAdditionalInfo();
			ETB localETB = new ETB(str2);
			String str3 = localETB.getWcFile();
			String str4 = str3;
			int j = str3.indexOf("!>");
			if ((j >= 0) && (j < str3.length() - 3)) {
				str4 = str3.substring(j + 2);
				str2 = Util.SandR(str2, str3, str4);
			}

			String str5 = localETB.getTargetWcFile();
			j = str5.indexOf("!>");
			if ((j >= 0) && (j < str5.length() - 3)) {
				str2 = Util.SandR(str2, str5, str5.substring(j + 2));
			}
			localStringBuffer1.append(str2).append("\n");

			if ((localWTMarkUp.getDescription() != null) && (localWTMarkUp.getDescription().length() > 0)) {
				localStringBuffer2.append(
						localETB.getName() + "@@" + localETB.getTag() + "=" + localWTMarkUp.getDescription()).append(
						"\n");
			}

			localWTMarkUp = (WTMarkUp) ContentHelper.service.getContents(localWTMarkUp);
			Vector localVector = ContentHelper.getContentListAll(localWTMarkUp);
			for (int k = 0; k < localVector.size(); k++) {
				localContentItem = (ContentItem) localVector.elementAt(k);
				if (localContentItem instanceof ApplicationData) {
					localApplicationData = (ApplicationData) localContentItem;
					String str1 = "";
					if (localApplicationData.getRole() == ContentRoleType.THUMBNAIL) {
						str1 = FileUtil.setExtension(str4, "gif");
					} else {
						str1 = str4;
					}

					localInputStream = ContentServerHelper.service.findContentStream(localApplicationData);
					map.put(str1, localInputStream);
				}
			}
		}
		if (localStringBuffer1 != null) {
			localInputStream = new ByteArrayInputStream(localStringBuffer1.toString().getBytes());
			map.put(etbFileName, localInputStream);
		}

		if ((localStringBuffer2 != null) && (localStringBuffer2.length() > 0)) {
			localInputStream = new ByteArrayInputStream(localStringBuffer2.toString().getBytes());
			map.put("mkdescr.dcr", localInputStream);
		}
	}

	/**
	 * 获取指定注释的图形，除简图
	 *
	 * @author qianlong
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @date 2012-12-8
	 *
	 */
	public static Map<String, byte[]> getMarkupImgNoSmallImg(QueryResult markupResult, String etbFileName,
			List<String> markupNameList, boolean needFilterMarkupName) throws WTException, PropertyVetoException {
		GLLogger.debug(CLASSNAME, "-markupResult-" + markupResult + "-etbFileName-" + etbFileName + "-markupNameList-"
				+ markupNameList);
		Map<String, byte[]> map = new HashMap<String, byte[]>();
		StringBuffer stringBuffer = new StringBuffer(300);
		if (markupNameList == null || markupNameList.size() == 0) {
			if (needFilterMarkupName) {
				return map;
			}
		}
		while (markupResult.hasMoreElements()) {
			WTMarkUp markup = (WTMarkUp) markupResult.nextElement();
			if (needFilterMarkupName && !markupNameList.contains(markup.getName())) {
				continue;
			}
			String str2 = markup.getAdditionalInfo();
			GLLogger.debug(CLASSNAME, "-str2-" + str2);
			ETB localETB = new ETB(str2);
			String str3 = localETB.getWcFile();
			GLLogger.debug(CLASSNAME, "-str3-" + str3);
			String str4 = str3;
			GLLogger.debug(CLASSNAME, "-str4-" + str4);
			int j = str3.indexOf("!>");
			if ((j >= 0) && (j < str3.length() - 3)) {
				str4 = str3.substring(j + 2);
				str2 = Util.SandR(str2, str3, str4);
			}
			GLLogger.debug(CLASSNAME, "-str4-" + str4);
			GLLogger.debug(CLASSNAME, "-str2-" + str2);
			String str5 = localETB.getTargetWcFile();
			GLLogger.debug(CLASSNAME, "-str5-" + str5);
			j = str5.indexOf("!>");
			if ((j >= 0) && (j < str5.length() - 3)) {
				str2 = Util.SandR(str2, str5, str5.substring(j + 2));
			}
			GLLogger.debug(CLASSNAME, "-str2-" + str2);
			stringBuffer.append(str2).append("\n");

			markup = (WTMarkUp) ContentHelper.service.getContents(markup);
			Vector localVector = ContentHelper.getContentListAll(markup);
			for (int i = 0; i < localVector.size(); i++) {
				ContentItem contentItem = (ContentItem) localVector.elementAt(i);
				if (contentItem instanceof ApplicationData) {
					ApplicationData data = (ApplicationData) contentItem;
					GLLogger.debug(CLASSNAME, "-data role-" + data.getFileName() + "--" + data.getRole());
					if (data.getRole() != ContentRoleType.THUMBNAIL) {
						byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
						map.put(str4, bytes);
					}
				}
			}
		}
		if (stringBuffer != null && stringBuffer.length() != 0) {
			GLLogger.debug(CLASSNAME, "-stringBuffer.toString-" + stringBuffer.toString());
			map.put(etbFileName, stringBuffer.toString().getBytes());
		}
		GLLogger.debug(CLASSNAME, "-map-" + map);
		return map;
	}

	/**
	 * 获取注释的简图
	 *
	 * @author qianlong
	 * @throws PropertyVetoException
	 * @throws WTException
	 * @throws
	 * @date 2012-12-1
	 *
	 */
	public static byte[] getMarkupByRole(WTMarkUp markup, ContentRoleType roleType) throws WTException,
			PropertyVetoException {
		// .gif THUMBNAIL
		// .ast PRIMARY
		GLLogger.debug(CLASSNAME, "-markup-" + markup);
		byte[] bytes = null;
		markup = (WTMarkUp) ContentHelper.service.getContents(markup);
		Vector vector = ContentHelper.getContentListAll(markup);
		for (int i = 0; i < vector.size(); i++) {
			ContentItem contentItem = (ContentItem) vector.elementAt(i);
			if (contentItem instanceof ApplicationData) {
				ApplicationData data = (ApplicationData) contentItem;
				GLLogger.debug(CLASSNAME, "-data role-" + data.getFileName() + "--" + data.getRole());
				if (data.getRole().equals(roleType)) {
					bytes = WTDocumentUtil.applicationDataToByte(data);
				}
			}
		}

		return bytes;
	}

	/**
	 * 根据可视化文档的类型和后缀名查找相应的可视化文件
	 *
	 * @author qianlong
	 * @date 2013-7-19
	 * @param representation
	 * @param endWithMap
	 * @return
	 * @throws WTException
	 */
	public static Map<String, byte[]> getRepByRoleAndEndWith(Representation representation,
			Map<ContentRoleType, String> endWithMap) throws WTException {
		// _small.jpg THUMBNAIL_SMAIL
		// .jpg THUMBNAIL
		// .pvt THUMBNAIL3D
		// .log .ol SECONDARY
		// .pvm PRODUCT_VIEW_EDM
		// .PVS PRODUCT_VIEW_ED
		// .pvp PRODUCT_VIEW_EDP
		GLLogger.debug(CLASSNAME, "-representation-" + representation + "-endWithMap-" + endWithMap);
		Map<String, byte[]> map = new HashMap<String, byte[]>();
		byte[] bytes = null;
		Vector vector = ContentHelper.getContentListAll(representation);
		for (int i = 0; vector != null && i < vector.size(); i++) {
			if (vector.get(i) instanceof ApplicationData) {
				ApplicationData data = (ApplicationData) vector.get(i);
				GLLogger.debug(CLASSNAME, "-data role-" + data.getFileName() + "--" + data.getRole());
				if (endWithMap == null || endWithMap.size() == 0) {
					bytes = WTDocumentUtil.applicationDataToByte(data);
					map.put(data.getFileName(), bytes);
					continue;
				}
				if (!endWithMap.containsKey(data.getRole())) {
					continue;
				}
				String endWithStr = endWithMap.get(data.getRole());
				if (endWithStr != null && !endWithStr.equals("")) {
					if (data.getFileName().toLowerCase().endsWith(endWithStr.toLowerCase())) {
						bytes = WTDocumentUtil.applicationDataToByte(data);
						map.put(data.getFileName(), bytes);
					}
				} else {
					bytes = WTDocumentUtil.applicationDataToByte(data);
					map.put(data.getFileName(), bytes);
				}
			}
		}
		GLLogger.debug(CLASSNAME, "-map-" + map);
		return map;
	}

	/**
	 * 获取零件的可视化图
	 *
	 * @author qianlong
	 * @date 2013-7-16
	 * @param part
	 * @throws WTException
	 */
	public static QueryResult getRepresentations(WTPart part) throws WTException {
		QueryResult queryResult = null;
		if (null != part) {
			queryResult = PublishUtils.getRepresentations(part);
			if (null == queryResult || queryResult.size() == 0) {
				EPMDocument epmDocument = WTPartUtil.get3DEPMDocumentByPart(part);
				if (null != epmDocument) {
					queryResult = PublishUtils.getRepresentations(epmDocument);
				}
			}
		}
		return queryResult;
	}

	/**
	 * 获取默认的可视化图档，如果没有默认的就任意获取一个
	 *
	 * @author qianlong
	 * @date 2013-12-3
	 * @return
	 * @throws WTException
	 */
	public static List<Object> getDefaultRepresentation(Representable representable) throws WTException {
		List<Object> list = new ArrayList<Object>();
		DerivedImage derivedImage = null;
		List<Viewable> viewableList = new ArrayList<Viewable>();
		Representation defaultRepresentation = RepresentationHelper.service.getDefaultRepresentation(representable);
		GLLogger.debug(CLASSNAME, "-defaultRepresentation-" + defaultRepresentation);
		if (null != defaultRepresentation && defaultRepresentation instanceof DerivedImage) {
			derivedImage = (DerivedImage) defaultRepresentation;
		}
		QueryResult repResult = PublishUtils.getRepresentations(representable);
		while (null != repResult && repResult.hasMoreElements()) {
			Object object = repResult.nextElement();
			if (object instanceof Representation) {
				if (object instanceof DerivedImage && derivedImage == null) {
					derivedImage = (DerivedImage) object;
				}
				if (object instanceof Viewable) {
					viewableList.add((Viewable) object);
				}
			}
		}
		list.add(derivedImage);
		list.add(viewableList);
		GLLogger.debug(CLASSNAME, "-list-" + list);
		return list;
	}

	/**
	 * 获取零件的可视化图
	 *
	 * @author qianlong
	 * @date 2013-7-16
	 * @param part
	 * @throws WTException
	 */
	public static Representable getHasRepPersistable(WTPart part) throws WTException {
		Representable representable = null;
		if (null != part) {
			QueryResult repResult = PublishUtils.getRepresentations(part);
			if (null == repResult || repResult.size() == 0) {
				representable = WTPartUtil.get3DEPMDocumentByPart(part);
			} else {
				representable = part;
			}
		}
		return representable;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2013-12-16
	 * @param representable
	 * @param nameMap
	 * @param isPrimary
	 * @throws WTException
	 * @throws PropertyVetoException
	 */
	public static void getCADAndMarkupName(Representable representable, List<List<Object>> nameList, boolean isPrimary)
			throws WTException, PropertyVetoException {
		GLLogger.debug(CLASSNAME, "-representable-" + representable + "-nameList-" + nameList + "-isPrimary-"
				+ isPrimary);
		DerivedImage derivedImage = (DerivedImage) getDefaultRepresentation(representable).get(0);
		if (null == derivedImage) {
			return;
		}
		List<Object> list = new ArrayList<Object>();
		Representation representation = (Representation) ContentHelper.service.getContents(derivedImage);
		Map<ContentRoleType, String> endWithMap = new HashMap<ContentRoleType, String>();
		endWithMap.put(ContentRoleType.THUMBNAIL, ".jpg");
		endWithMap.put(ContentRoleType.PRODUCT_VIEW_ED, ".pvs");
		Map<String, byte[]> repMap = EPMDocumentUtil.getRepByRoleAndEndWith(representation, endWithMap);
		String pvsName = "";
		byte[] strByte = {};
		for (String str : repMap.keySet()) {
			if (str.endsWith(".pvs")) {
				pvsName = str.replace(".pvs", "");
			} else if (str.endsWith(".jpg")) {
				strByte = repMap.get(str);
			}
		}
		if (!"".equalsIgnoreCase(pvsName)) {
			list.add(pvsName + ".jpg");
			list.add(strByte);
			list.add(com.glaway.mpm.util.Util.getStringOid(representable));
			list.add(isPrimary);
			String version = "";
			if (representable instanceof Versioned) {
				Versioned versioned = (Versioned) representable;
				version = versioned.getVersionIdentifier().getValue() + "."
						+ versioned.getIterationIdentifier().getValue();
			}
			list.add(version);
			String name = "";
			if (representable instanceof EPMDocument) {
				name = ((EPMDocument) representable).getName();
			}
			list.add(name);
			nameList.add(list);
		}
	}

	/**
	 * 获取相关的中间模型图档
	 *
	 * @author qianlong
	 * @date 2013-2-5
	 * @param epmDocument
	 * @throws WTException
	 *
	 */
	public static QueryResult getEPMDocumentLikeNumber(String number) throws WTException {

		QuerySpec qs = new QuerySpec(EPMDocument.class);
		qs.appendWhere(new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, SearchCondition.LIKE, number, false),
				index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		return qr;
	}

	/**
	 *
	 * @param epm3d
	 * @return
	 * @throws WTException
	 */
	public static List<EPMDocument> getReferenceEPMDoc(EPMDocument epm3d) throws WTException {
		List<EPMDocument> childList = new ArrayList<EPMDocument>();
		QueryResult queryResult = PersistenceHelper.manager.navigate(epm3d.getMaster(), EPMReferenceLink.REFERENCED_BY_ROLE, EPMReferenceLink.class, false);
		EPMReferenceLink refLink = null;
		EPMDocument epm = null;
		while (queryResult.hasMoreElements()) {
			refLink = (EPMReferenceLink) queryResult.nextElement();
			epm = refLink.getReferencedBy();
			childList.add(epm);
		}
		return childList;
	}

	public static void main(String[] args) throws RemoteException, InvocationTargetException, WTRuntimeException,
			WTException {
		// RemoteMethodServer server = RemoteMethodServer.getDefault();
		// server.setUserName("wcadmin");
		// server.setPassword("wcadmin");
		// EPMDocument document = (EPMDocument)
		// ReferenceFactory.getObjectbyOid("OR:wt.epm.EPMDocument:192716");
		// server.invoke("getPrimaryAmdMarkUpToZip",
		// EPMDocumentUtil.class.getName(), null, new Class[] {
		// EPMDocument.class, String.class },
		// new Object[] { document, "C:\\Users\\Administrator\\Desktop/xxx.zip"
		// });

		List<String> list = new ArrayList<String>();
		list.add("adad");
		list.add("阿达");
		System.out.println(list.contains("adad"));
		System.out.println(list.contains("阿达"));

	}
}
