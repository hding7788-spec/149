package com.glaway.mpm.util;

import com.glaway.mpm.constants.ProcessPlanConstants;
import com.ptc.core.query.common.QueryException;
import ext.casc.fileprint.FilePrintUtil;
import ext.casc.util.IBAHelper;
import wt.change2.WTChangeRequest2;
import wt.content.*;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.enterprise.RevisionControlled;
import wt.fc.*;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.method.RemoteAccess;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

import java.beans.PropertyVetoException;
import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class WTDocumentUtil implements RemoteAccess {
	private static int index[] = { 0 };
	private static final String CLASSNAME = WTDocumentUtil.class.getName();
	private static int reviewNum = 0;

	/**
	 * 根据文件编号获取文件
	 *
	 * @author qianlong
	 * @date 2012-10-23
	 * @param type
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 *
	 */
	public static WTDocument getDocumentByNumber(String number) throws WTException, RemoteException {
		WTDocument document = null;
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number), index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		if (qr.hasMoreElements()) {
			document = (WTDocument) qr.nextElement();
		}
		return document;
	}

	public static InputStream applicationDataToInputStream(ApplicationData data) throws WTException {
		return ContentServerHelper.service.findContentStream(data);
	}
	/**
	 * 根据文件类型获取文件
	 *
	 * @author qianlong
	 * @date 2012-10-23
	 * @param type
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 *
	 */
	public static List<WTDocument> getDocumentByType(String type) throws WTException, RemoteException {
		List<WTDocument> list = new ArrayList<WTDocument>();
		QuerySpec qs = new QuerySpec(WTDocument.class);
		TypeUtil.getTypeQuery(WTDocument.class, type, qs);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		while (qr.hasMoreElements()) {
			WTDocument document = (WTDocument) qr.nextElement();
			list.add(document);
		}
		return list;
	}

	/**
	 * 根据文件类型、和当前用户获取文件
	 *
	 * @author qianlong
	 * @date 2012-10-23
	 * @param type
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 *
	 */
	public static List<WTDocument> getDocumentByTypeAndUser(String type, long user) throws WTException, RemoteException {
		List<WTDocument> list = new ArrayList<WTDocument>();
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTDocument.class, "iterationInfo.creator.key.id", SearchCondition.EQUAL,
				user), index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		while (qr.hasMoreElements()) {
			WTDocument document = (WTDocument) qr.nextElement();
			list.add(document);
		}
		return list;
	}

	/**
	 * 根据master获取最新的文件
	 *
	 * @author qianlong
	 * @date 2012-10-23
	 * @param documentMaster
	 * @return
	 *
	 */
	public static WTDocument getLatestDocumentByMaster(WTDocumentMaster documentMaster) {
		WTDocument document = null;
		try {
			if (documentMaster != null) {
				QueryResult qr = VersionControlHelper.service.allVersionsOf(documentMaster);
				if (qr.hasMoreElements()) {
					document = (WTDocument) qr.nextElement();
				}
			}
		} catch (PersistenceException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return document;
	}

	/**
	 * 通过编号获取最新的文件
	 *
	 * @author qianlong
	 * @date 2012-10-23
	 * @param documentNumber
	 * @return
	 * @throws WTException
	 *
	 */
	public static WTDocument getLatestDocumentByNumber(String documentNumber) throws WTException {
		WTDocument document = null;
		QuerySpec qSpec = new QuerySpec(WTDocument.class);
		qSpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL,
				documentNumber), index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		qResult = new LatestConfigSpec().process(qResult);
		while (qResult.hasMoreElements()) {
			document = (WTDocument) qResult.nextElement();
		}
		return document;
	}

	/**
	 * 通过编号获取最新的文件
	 *
	 * @author qianlong
	 * @date 2012-10-23
	 * @param documentNumber
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 *
	 */
	public static QueryResult getDocumentByLikeNumberType(String documentNumber, String typeName) throws WTException,
			RemoteException {
		QuerySpec qSpec = new QuerySpec(WTDocument.class);
		qSpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.LIKE,
				documentNumber, false), index);
		qSpec.appendAnd();
		TypeUtil.getTypeQuery(WTDocument.class, typeName, qSpec);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		qResult = new LatestConfigSpec().process(qResult);
		return qResult;
	}

	/**
	 * 通过工装申请卡编号或者名称获取工装申请卡
	 *
	 * @author qianlong
	 * @date 2012-10-23
	 * @param documentNumber
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 *
	 */
	public static QueryResult getDocumentByLikeNumberOrNameType(String param, String typeName) throws WTException,
			RemoteException {
		QuerySpec qSpec = new QuerySpec(WTDocument.class);
		qSpec.appendOpenParen();
		qSpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.LIKE, param, false),
				index);
		qSpec.appendOr();
		qSpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, param, false),
				index);
		qSpec.appendCloseParen();
		qSpec.appendAnd();
		TypeUtil.getTypeQuery(WTDocument.class, typeName, qSpec);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		qResult = new LatestConfigSpec().process(qResult);
		return qResult;
	}

	/**
	 * 通过编号或者名称获取文档对象
	 *
	 * @author LongXiuChuan
	 * @date 2014-5-28
	 * @param documentNumber
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static QueryResult getDocumentByLikeNumberAndNameType(String number, String name, String typeName) throws WTException,
			RemoteException, WTPropertyVetoException {
		QuerySpec qSpec = new QuerySpec(WTDocument.class);
		qSpec.setAdvancedQueryEnabled(true);
		qSpec.appendOpenParen();

		ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "."
				+ PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		SubSelectExpression subSelectExpression = getStringIBAQuery2("PPNUMBER", number);
		qSpec.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
		qSpec.appendAnd();

//		qSpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.LIKE, number, false),
//				index);
//		qSpec.appendAnd();

		qSpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, name, false),
				index);
		qSpec.appendCloseParen();
		if(typeName != null){
			qSpec.appendAnd();
			TypeUtil.getTypeQuery(WTDocument.class, typeName, qSpec);
		}
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		qResult = new LatestConfigSpec().process(qResult);
		return qResult;
	}

	/**
	 * 通过编号获取master
	 *
	 * @author qianlong
	 * @date 2012-10-23
	 * @param documentNumber
	 * @return
	 * @throws WTException
	 *
	 */
	public static WTDocumentMaster getWTDocumentMasterByNumber(String documentNumber) throws WTException {
		WTDocumentMaster documentMaster = null;

		QuerySpec qs = new QuerySpec(WTDocumentMaster.class);
		qs.appendWhere(new SearchCondition(WTDocumentMaster.class, WTDocumentMaster.NUMBER, SearchCondition.EQUAL,
				documentNumber), index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		if (qr.hasMoreElements()) {
			documentMaster = (WTDocumentMaster) qr.nextElement();
		}

		return documentMaster;
	}

	/**
	 * 通过名称获取最新版本的文档
	 *
	 * @author fly
	 * @date 2013-5-6
	 * @param docName
	 * @return
	 *
	 */
	public static WTDocument getLastWTDocumentByName(String docName) {
		WTDocument doc = null;
		try {
			WTDocumentMaster master = WTDocumentUtil.getWTDocumentMasterByName(docName);
			if(master != null){
				QueryResult qr = VersionControlHelper.service.allVersionsOf(master);
				if (qr.hasMoreElements()) {
					doc = (WTDocument) qr.nextElement();
					GLLogger.debug("doc =" + doc.getName() + " " + doc.getIterationDisplayIdentifier());
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 通过名称获取master
	 *
	 * @author
	 * @date 2012-10-23
	 * @param documentNumber
	 * @return 返回第一次搜索到的文档，如果想获取多个名称相同的master 请使用其他的方法
	 * @throws WTException
	 *
	 */
	public static WTDocumentMaster getWTDocumentMasterByName(String docName) throws WTException {
		WTDocumentMaster documentMaster = null;

		QuerySpec qs = new QuerySpec(WTDocumentMaster.class);
		qs.appendWhere(new SearchCondition(WTDocumentMaster.class, WTDocumentMaster.NAME, SearchCondition.EQUAL,
				docName), index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		if (qr.hasMoreElements()) {
			documentMaster = (WTDocumentMaster) qr.nextElement();
		}

		return documentMaster;
	}

	/**
	 * 获取文件的主体档
	 *
	 * @author qianlong
	 * @date 2012-10-23
	 * @param doc
	 * @return
	 * @throws PropertyVetoException
	 * @throws WTException
	 * @throws WTException
	 * @throws PropertyVetoException
	 *
	 */
	@SuppressWarnings("deprecation")

	public static ApplicationData getPrimaryByDocument(WTDocument doc) throws WTException, PropertyVetoException {

		return (ApplicationData) ContentHelper.service.getPrimary(doc);
	}

	/**
	 * 获取变更请求的辅件
	 *
	 * @author qianlong
	 * @date 2012-10-23
	 * @param doc
	 * @return
	 * @throws PropertyVetoException
	 * @throws WTException
	 * @throws WTException
	 * @throws PropertyVetoException
	 *
	 */
	public static QueryResult getSecondaryByChangeRequest(WTChangeRequest2 changeRequest) throws WTException,
			PropertyVetoException {
		QueryResult result = ContentHelper.service.getContentsByRole(changeRequest, ContentRoleType.SECONDARY);
		return result;
	}

	/**
	 * 把applicationData转化成字节数组
	 *
	 * @author qianlong
	 * @date 2012-11-9
	 * @param data
	 * @return
	 * @throws WTException
	 *
	 */
	public static byte[] applicationDataToByte(ApplicationData data) throws WTException {

		InputStream inputStream = ContentServerHelper.service.findContentStream(data);
		return FileUtil.fileToBytes(inputStream);
	}

	/**
	 * 设置文件的主体档
	 *
	 * @author qianlong
	 * @date 2013-7-24
	 * @param setPrimaryDoc
	 * @param copyPrimaryDoc
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public static WTDocument setPrimaryForDocument(WTDocument setPrimaryDoc, WTDocument copyPrimaryDoc)
			throws WTException, PropertyVetoException, FileNotFoundException, IOException {
		ApplicationData copyAppData = (ApplicationData) ContentHelper.service.getPrimary(copyPrimaryDoc);
		if(copyAppData != null){
			ApplicationData appData = (ApplicationData) ContentHelper.service.getPrimary(setPrimaryDoc);
			if (appData != null) {
				PersistenceHelper.manager.delete(appData);
				PersistenceServerHelper.manager.update(setPrimaryDoc);
			}
			appData = ApplicationData.newApplicationData(setPrimaryDoc);
			// 主物件和附件
			// ContentRoleType.SECONDARY 表示 附件
			// ContentRoleType.PRIMARY 表示主物件
			appData.setRole(ContentRoleType.PRIMARY);
			appData.setFileName(copyAppData.getFileName());

			appData = ContentServerHelper.service.updateContent((ContentHolder) setPrimaryDoc, appData,
					ContentServerHelper.service.findContentStream(copyAppData)); // 更新内容
			PersistenceServerHelper.manager.update(setPrimaryDoc);
			setPrimaryDoc = (WTDocument) ContentServerHelper.service
					.updateHolderFormat((FormatContentHolder) setPrimaryDoc); // 更新格式
		}
		return setPrimaryDoc;
	}

	/**
	 * 设置文件的主体档
	 *
	 * @author qianlong
	 * @date 2012-11-9
	 * @param document
	 * @param fileName
	 * @param bytes
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws IOException
	 * @throws FileNotFoundException
	 *
	 */
	@SuppressWarnings("deprecation")
	public static WTDocument setPrimaryForDocument(WTDocument document, String fileName, byte[] bytes)
			throws WTException, PropertyVetoException, FileNotFoundException, IOException {
		Transaction trans = new Transaction();
		trans.start();
		ByteArrayInputStream inputStream = null;
		document = (WTDocument) PersistenceHelper.manager.refresh(document);
		ApplicationData appData = (ApplicationData) ContentHelper.service.getPrimary(document);
		if (appData != null) {
			GLLogger.debug(CLASSNAME, "--appData-" + appData.getFileName());

			PersistenceHelper.manager.delete(appData);
			PersistenceServerHelper.manager.update(document);
		}
		appData = ApplicationData.newApplicationData(document);

		// 主物件和附件
		// ContentRoleType.SECONDARY 表示 附件
		// ContentRoleType.PRIMARY 表示主物件
		appData.setRole(ContentRoleType.PRIMARY);
		// 设置文件名称
		appData.setFileName(fileName);

		inputStream = new ByteArrayInputStream(bytes);

		appData = ContentServerHelper.service.updateContent((ContentHolder) document, appData, inputStream); // 更新内容
		PersistenceServerHelper.manager.update(document);
		document = (WTDocument) ContentServerHelper.service.updateHolderFormat((FormatContentHolder) document); // 更新格式
		if (null != inputStream) {
			inputStream.close();
		}
		trans.commit();

		return document;
	}

	/**
	 * 设置文件的主体档
	 *
	 * @author qianlong
	 * @date 2012-11-23
	 * @param document
	 * @param fileName
	 * @param inputStream
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws FileNotFoundException
	 * @throws IOException
	 *
	 */
	@SuppressWarnings("deprecation")
	public static WTDocument setPrimaryForDocument(WTDocument document, String fileName, InputStream inputStream)
			throws WTException, PropertyVetoException, FileNotFoundException, IOException {
		Transaction trans = new Transaction();
		trans.start();
		document = (WTDocument) PersistenceHelper.manager.refresh(document);
		ApplicationData appData = (ApplicationData) ContentHelper.service.getPrimary(document);
		if (appData != null) {
			GLLogger.debug(CLASSNAME, "--appData-" + appData.getFileName());

			PersistenceHelper.manager.delete(appData);
			PersistenceServerHelper.manager.update(document);
		}
		appData = ApplicationData.newApplicationData(document);

		// 主物件和附件
		// ContentRoleType.SECONDARY 表示 附件
		// ContentRoleType.PRIMARY 表示主物件
		appData.setRole(ContentRoleType.PRIMARY);
		// 设置文件名称
		appData.setFileName(fileName);

		appData = ContentServerHelper.service.updateContent((ContentHolder) document, appData, inputStream); // 更新内容
		PersistenceServerHelper.manager.update(document);
		document = (WTDocument) ContentServerHelper.service.updateHolderFormat((FormatContentHolder) document); // 更新格式
		if (null != inputStream) {
			inputStream.close();
		}
		trans.commit();

		return document;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-11-15
	 * @param docName
	 * @param container
	 * @return
	 *
	 */
	public static WTDocument getWrokproceduceDoc(String docName, WTContainer container) {
		WTDocument document = null;

		QuerySpec qSpec;
		try {
			qSpec = new QuerySpec(WTDocument.class);
			qSpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.EQUAL, docName),
					index);
			qSpec.appendAnd();
			qSpec.appendWhere(new SearchCondition(WTDocument.class, "containerReference.key.id", SearchCondition.EQUAL,
					Util.getLongOid(container)), index);
			QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
			qResult = new LatestConfigSpec().process(qResult);
			LatestConfigSpec lcSpec = new LatestConfigSpec();
			qResult = lcSpec.process(qResult);
			while (qResult.hasMoreElements()) {
				document = (WTDocument) qResult.nextElement();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		return document;
	}

	/**
	 * 创建文件
	 *
	 * @author qianlong
	 * @date 2012-11-15
	 * @param name
	 * @param container
	 * @param folderPath
	 * @param type
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @throws RemoteException
	 *
	 */
	@SuppressWarnings("deprecation")
	public static WTDocument createDocument(String name, WTContainer container, String folderPath, String type)
			throws WTPropertyVetoException, WTException, RemoteException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		Transaction transaction = new Transaction();
		transaction.start();
		WTDocument document = WTDocument.newWTDocument();
		document.setName(name);
		document.setContainer(container);
		Folder folder = FolderUtil.getFolder(folderPath, WTContainerRef.newWTContainerRef(container));
		FolderHelper.assignFolder(document, folder);
		TypeDefinitionReference typeRef = TypedUtilityServiceHelper.service.getTypeDefinitionReference(type);
		document.setTypeDefinitionReference(typeRef);
		document = (WTDocument) PersistenceHelper.manager.save(document);
		transaction.commit();
		SessionServerHelper.manager.setAccessEnforced(flag);
		return document;
	}

	/**
	 * 创建文件
	 *
	 * @author qianlong
	 * @date 2012-11-15
	 * @param name
	 * @param container
	 * @param folderPath
	 * @param type
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @throws RemoteException
	 *
	 */
	@SuppressWarnings("deprecation")
	public static void setDocType(WTDocument doc) throws RemoteException, WTException, WTPropertyVetoException{
		TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference("casc.sast.149.PRINTRECOVERFORM");
		doc.setTypeDefinitionReference(tdr);
	}

	@SuppressWarnings("deprecation")
	public static WTDocument createDocument(String number, String name, WTContainer container, String folderPath,
			String type) throws WTPropertyVetoException, WTException, RemoteException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		Transaction transaction = new Transaction();
		transaction.start();
		WTDocument document = WTDocument.newWTDocument();
		if(number != null && !"".equals(number)) {
			document.setNumber(number);
		}
		document.setName(name);
		document.setContainer(container);
		Folder folder = FolderUtil.getFolder(folderPath, WTContainerRef.newWTContainerRef(container));
		if (folder == null) {
            try {
                folder = FolderHelper.service.saveFolderPath(folderPath,  WTContainerRef.newWTContainerRef(container));
            } catch (Exception e) {
                // TODO: handle exception
                folder = null;
            }
        }
		FolderHelper.assignFolder(document, folder);
		TypeDefinitionReference typeRef = TypedUtilityServiceHelper.service.getTypeDefinitionReference(type);
		document.setTypeDefinitionReference(typeRef);
		document = (WTDocument) PersistenceHelper.manager.save(document);
		transaction.commit();
		SessionServerHelper.manager.setAccessEnforced(flag);
		return document;
	}

	/**
	 * 创建文件
	 *
	 * @author qianlong
	 * @date 2012-11-23
	 * @param name
	 * @param container
	 * @param folderPath
	 * @param type
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @throws RemoteException
	 *
	 */
	@SuppressWarnings("deprecation")
	public static WTDocument createDocument(String name, WTContainer container, Folder folder, String type)
			throws WTPropertyVetoException, WTException, RemoteException {
		Transaction transaction = new Transaction();
		transaction.start();
		WTDocument document = WTDocument.newWTDocument();
		document.setName(name);
		document.setContainer(container);
		WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
		document.setDescription("由"+currentUser.getFullName()+"创建");
		FolderHelper.assignFolder(document, folder);
		TypeDefinitionReference typeRef = TypedUtilityServiceHelper.service.getTypeDefinitionReference(type);
		document.setTypeDefinitionReference(typeRef);
		document = (WTDocument) PersistenceHelper.manager.save(document);
		transaction.commit();
		return document;
	}

	/**
	 * 通过文件名称和类型获取文件
	 *
	 * @author qianlong
	 * @date 2012-11-15
	 * @param name
	 * @param location
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 *
	 */
	public static List<WTDocument> getDocumnetByNameAndType(String name, String type) throws WTException,
			WTPropertyVetoException, RemoteException {
		GLLogger.debug(CLASSNAME, "----name-" + name + "--type--" + type);
		List<WTDocument> list = new ArrayList<WTDocument>();
		QuerySpec qs = new QuerySpec(WTDocument.class);
		TypeUtil.getTypeQuery(WTDocument.class, type, qs);
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.EQUAL, name, true),
						index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) qs);
		queryResult = new LatestConfigSpec().process(queryResult);
		while (queryResult.hasMoreElements()) {
			WTDocument document = (WTDocument) queryResult.nextElement();
			list.add(document);
		}
		return list;
	}

	/**
	 * 通过文件名称和软属性获取文件
	 *
	 * @author qianlong
	 * @date 2012-11-15
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static WTDocument getDocumnetByNameAndIBA(String name, String ibaName, String ibaValue) throws WTException,
			WTPropertyVetoException, RemoteException {
		WTDocument document = null;
		QuerySpec querySpec = new QuerySpec(WTDocument.class);
		querySpec.setAdvancedQueryEnabled(true);
		ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "."
				+ PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		querySpec.appendWhere(
				new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.EQUAL, name, true), index);
		// 通过对象的软属性查询

		querySpec.appendAnd();
		querySpec.appendOpenParen();
		SubSelectExpression subSelectExpression = getStringIBAQuery(ibaName, ibaValue);
		querySpec.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
		querySpec.appendCloseParen();
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		if (queryResult.hasMoreElements()) {
			document = (WTDocument) queryResult.nextElement();
		}
		return document;
	}

	/**
	 * 构建子查询语句
	 *
	 * @author lbzhang
	 * @date 2012-8-28
	 * @param ibaName
	 * @param ibaValue
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 */
	public static SubSelectExpression getStringIBAQuery(String ibaName, String ibaValue) throws WTException,
			WTPropertyVetoException, RemoteException {
		// 获取IBA属性定义
		AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
		if (addv == null)
			throw new IBADefinitionException("No IBA Definition: " + ibaName);
		long ibaDefId = addv.getObjectID().getId();
		QuerySpec qs = new QuerySpec();
		int idx = qs.appendClassList(StringValue.class, false);
		qs
				.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx },
						false);
		qs.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL,
				ibaDefId), new int[] { idx });
		qs.appendAnd();
		qs.appendWhere(
				new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.EQUAL, ibaValue, true),
				new int[] { idx });
		return new SubSelectExpression(qs);
	}

	/**
	 * 构建子查询语句
	 *
	 * @author lbzhang
	 * @date 2012-8-28
	 * @param ibaName
	 * @param ibaValue
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 */
	public static SubSelectExpression getStringIBAQuery2(String ibaName, String ibaValue) throws WTException,
			WTPropertyVetoException, RemoteException {
		// 获取IBA属性定义
		AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
		if (addv == null)
			throw new IBADefinitionException("No IBA Definition: " + ibaName);
		long ibaDefId = addv.getObjectID().getId();
		QuerySpec qs = new QuerySpec();
		int idx = qs.appendClassList(StringValue.class, false);
		qs
				.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx },
						false);
		qs.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL,
				ibaDefId), new int[] { idx });
		qs.appendAnd();
		qs.appendWhere(
				new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.LIKE, ibaValue, true),
				new int[] { idx });
		return new SubSelectExpression(qs);
	}

	/**
	 * 下载文件主题档到系统的temp资料夹下，并返回文件路径
	 *
	 * @author lbzhang
	 * @date 2012-11-30上午11:47:13
	 * @param doc
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public static String downloadDocumentPrimaryToTemp(WTDocument doc) throws WTException, PropertyVetoException,
			FileNotFoundException, IOException {
		FormatContentHolder holder = (FormatContentHolder) ContentHelper.service.getContents(doc);
		ApplicationData currdata = (ApplicationData) ContentHelper.service.getPrimary(holder);
		InputStream is = ContentServerHelper.service.findContentStream(currdata);
		String appFileName = currdata.getFileName();
		String currentTime = "" + System.currentTimeMillis();
		File file = new File(Util.getTempPath() + File.separatorChar + currentTime);
		if (!file.exists()) {
			file.mkdir();
		}
		FileOutputStream fos = new FileOutputStream(new File(Util.getTempPath() + File.separatorChar + currentTime
				+ File.separatorChar + appFileName));
		int i = 0;
		byte abyte[] = new byte[8192];
		while ((i = is.read(abyte, 0, abyte.length)) >= 0) {
			fos.write(abyte, 0, i);
		}
		is.close();
		fos.close();
		return Util.getTempPath() + File.separatorChar + currentTime + File.separatorChar + appFileName;
	}

	/**
	 * 通过container 和文件类型获取文件
	 *
	 * @author qianlong
	 * @date 2012-12-7
	 * @param container
	 * @throws WTException
	 * @throws RemoteException
	 *
	 */
	public static List<WTDocument> getDocumentByTypeAndConatiner(WTContainer container, String type)
			throws RemoteException, WTException {
		GLLogger.debug(CLASSNAME, "----container-" + container + "--type--" + type);
		List<WTDocument> list = new ArrayList<WTDocument>();
		TypeDefinitionReference typeDefinitionReference = ClientTypedUtility.getTypeDefinitionReference(type);
		GLLogger.debug(CLASSNAME, "----typeDefinitionReference-" + typeDefinitionReference);
		long containerid = Util.getLongOid(container);
		QuerySpec qs = new QuerySpec(WTDocument.class);
		TypeUtil.getTypeQuery(WTDocument.class, type, qs);
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTDocument.class, "containerReference.key.id", SearchCondition.EQUAL,
				containerid), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) qs);
		queryResult = new LatestConfigSpec().process(queryResult);
		while (queryResult.hasMoreElements()) {
			list.add((WTDocument) queryResult.nextElement());
		}
		GLLogger.debug(CLASSNAME, "----list-" + list);
		return list;
	}

	/**
	 * 下载文件primary到指定的目录,并且返回文件名称
	 *
	 * @author qianlong
	 * @date 2012-12-7
	 * @param doc
	 * @return
	 * @throws WTException
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws PropertyVetoException
	 * @throws FileNotFoundException
	 * @throws IOException
	 *
	 */
	public static String downloadDocumentPrimaryToTemp(WTDocument doc, String tempPath) throws WTException,
			PropertyVetoException {
		ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
		if (data == null) {
			return null;
		}
		InputStream is = ContentServerHelper.service.findContentStream(data);
		String appFileName = data.getFileName();
		File file = new File(tempPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(tempPath + File.separatorChar + appFileName));
			int i = 0;
			byte abyte[] = new byte[8192];
			while ((i = is.read(abyte, 0, abyte.length)) >= 0) {
				fos.write(abyte, 0, i);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != is) {
					is.close();
				}
				if (null != fos) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return appFileName;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-11-15
	 * @param args
	 *
	 */
	public static void main(String[] args) {
		// RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		// methodServer.setUserName("wcadmin");
		// methodServer.setPassword("wcadmin");
		// try {
		// methodServer.invoke("test", WTDocumentUtil.class.getCanonicalName(),
		// null, null, null);
		// } catch (RemoteException e) {
		// e.printStackTrace();
		// } catch (InvocationTargetException e) {
		// e.printStackTrace();
		// }
		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// System.out.println(baos.toByteArray());

		// System.out.println("sss/ss".lastIndexOf("/"));
		// System.out.println("sss\\ss".lastIndexOf(""));
		// System.out.println("sss\\ss".lastIndexOf("\\"));
		// System.out.println(File.separator);

		File file = new File("c:/ss/dd");
		System.out.println(file.exists());
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 获取文档
	 *
	 * @author fly
	 * @date 2013-5-28
	 * @param docs
	 * @param name
	 * @return
	 *
	 */
	public static WTDocument getDocumentByName(List<WTDocument> docs, String name) {
		WTDocument doc = null;
		for (int i = 0; i < docs.size(); i++) {
			if (docs.get(i).getName().equals(name)) {
				return docs.get(i);
			}
		}
		return doc;
	}


	public static List<WTDocument> getAllDocumentByNumber(String number) throws WTException{
		List<WTDocument> documentlist = new ArrayList<WTDocument>();
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number), index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			WTDocument document = (WTDocument) qr.nextElement();
			documentlist.add(document);
		}
		return documentlist;


	}

	/**
	 * 设置零件关联工艺压缩文档状态(普通工艺)
	 *
	 * @author lbzhang
	 * @date 2013-6-5
	 * @param part
	 * @param state
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws WTRuntimeException
	 *
	 */
	public static void setWTDocumentStateOfWTPart(WTPart part, String state) throws WTException, WTRuntimeException,
			WTPropertyVetoException {
//		WTPart pPart = part;
//		if (pPart == null) {
//			return;
//		}
//
//		WTDocument doc = null;
//		List<WTDocument> list = ProcessPlanHelper.getProcessZipDoc(pPart, null, Constants.normalProcess);
//		if (list.size() != 0) {
//			doc = list.get(0);
//		}
//		if (doc == null) {
//			return;
//		}
//		GLLogger.debug("state===>>>" + state);
//		GLLogger.debug("doc=====>" + doc.getName() + "    " + doc.getNumber() + "   "
//				+ doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());
//		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
//		LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) doc, State.toState(state));
//		SessionServerHelper.manager.setAccessEnforced(flag);
//		GLLogger.debug("doc state ====>" + doc.getLifeCycleState().toString());
	}

	/**
	 * 设置零件关联返工工艺压缩文档状态(返工工艺)
	 *
	 * @author lbzhang
	 * @date 2013-6-21
	 * @param part
	 * @param state
	 * @param technicName
	 * @throws WTException
	 * @throws WTRuntimeException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static void setWTDocumentStateOfWTPart(WTPart part, String state, String technicName) throws WTException,
			WTRuntimeException, WTPropertyVetoException {
//		WTPart pPart = part;
//		if (pPart == null) {
//			return;
//		}
//		WTDocument doc = null;
//		List<WTDocument> list = ProcessPlanHelper.getProcessZipDoc(pPart, technicName, null);
//		if (list.size() != 0) {
//			doc = list.get(0);
//		}
//		if (doc == null) {
//			return;
//		}
//		GLLogger.debug("state===>>>" + state);
//		GLLogger.debug("doc=====>" + doc.getName() + "    " + doc.getNumber() + "   "
//				+ doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());
//		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
//		LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) doc, State.toState(state));
//		SessionServerHelper.manager.setAccessEnforced(flag);
//		GLLogger.debug("doc state ====>" + doc.getLifeCycleState().toString());
//		GLLogger.debug("technicName=============>" + technicName);
	}

	public static WTDocument uploadAttachForDocument(WTDocument document, String fileName, byte[] bytes)
			throws WTException, PropertyVetoException, FileNotFoundException, IOException {
		Transaction trans = new Transaction();
		trans.start();
		ByteArrayInputStream inputStream = null;
		document = (WTDocument) PersistenceHelper.manager.refresh(document);
		ApplicationData appData = null;
		QueryResult qr = ContentHelper.service.getContentsByRole(document, ContentRoleType.SECONDARY);
		while(qr.hasMoreElements()){
			appData = (ApplicationData)qr.nextElement();
			String filename = appData.getFileName();
			if("PDFPreview.pdf".equals(filename)){
				PersistenceHelper.manager.delete(appData);
				PersistenceServerHelper.manager.update(document);
			}
		}
		appData = ApplicationData.newApplicationData(document);
		// 主物件和附件
		// ContentRoleType.SECONDARY 表示 附件
		// ContentRoleType.PRIMARY 表示主物件
		appData.setRole(ContentRoleType.SECONDARY);
		// 设置文件名称
		appData.setFileName(fileName);

		inputStream = new ByteArrayInputStream(bytes);
		InputStream is = new ByteArrayInputStream(bytes);

		//设置默认表示法
		RepUtils.saveFileRep2(document, inputStream, fileName);

		appData = ContentServerHelper.service.updateContent((ContentHolder) document, appData, is,true); // 更新内容

		PersistenceServerHelper.manager.update(document);
		document = (WTDocument) ContentServerHelper.service.updateHolderFormat((FormatContentHolder) document); // 更新格式
		if (null != inputStream) {
			inputStream.close();
		}
		if (null != is) {
			is.close();
		}
		trans.commit();

		return document;
	}

	public static WTDocument uploadPrintAttachForDocument(WTDocument document, String fileName, byte[] bytes)
			throws WTException, PropertyVetoException, FileNotFoundException, IOException {
		Transaction trans = new Transaction();
		trans.start();
		ByteArrayInputStream inputStream = null;
		document = (WTDocument) PersistenceHelper.manager.refresh(document);
		ApplicationData appData = null;
		QueryResult qr = ContentHelper.service.getContentsByRole(document, ContentRoleType.SECONDARY);
		while(qr.hasMoreElements()){
			appData = (ApplicationData)qr.nextElement();
			String filename = appData.getFileName();
			if((filename.startsWith("Print") || filename.startsWith("PRINT")) && filename.endsWith("pdf")){
				PersistenceHelper.manager.delete(appData);
				PersistenceServerHelper.manager.update(document);
			}
		}
		appData = ApplicationData.newApplicationData(document);
		// 主物件和附件
		// ContentRoleType.SECONDARY 表示 附件
		// ContentRoleType.PRIMARY 表示主物件
		appData.setRole(ContentRoleType.SECONDARY);
		// 设置文件名称
		appData.setFileName(fileName);

		inputStream = new ByteArrayInputStream(bytes);
		InputStream is = new ByteArrayInputStream(bytes);

		//设置默认表示法
		RepUtils.saveFileRep2(document, inputStream, fileName);

		appData = ContentServerHelper.service.updateContent((ContentHolder) document, appData, is,true); // 更新内容

		PersistenceServerHelper.manager.update(document);
		document = (WTDocument) ContentServerHelper.service.updateHolderFormat((FormatContentHolder) document); // 更新格式
		if (null != inputStream) {
			inputStream.close();
		}
		if (null != is) {
			is.close();
		}
		trans.commit();

		return document;
	}

	@SuppressWarnings("deprecation")
	public static boolean deleteAttachForSOP(WTDocument document) throws ObjectNoLongerExistsException, WTException, RemoteException
			{
		Transaction trans = new Transaction();
		trans.start();
		document = (WTDocument) PersistenceHelper.manager.refresh(document);
		ApplicationData appData = null;
		QueryResult qr = ContentHelper.service.getContentsByRole(document, ContentRoleType.SECONDARY);
		String PRINTKEY = "PRINT";
		String primaryfileName = FilePrintUtil.getPrimaryFileName(document);
		String printFileName = FilePrintUtil.getQualityFileName(document, primaryfileName, PRINTKEY);
		RevisionControlled rc = (RevisionControlled) document;
		String doctype = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(rc);
		String secret = IBAHelper.getIBAStringValue(rc, "SECRET");
		printFileName = printFileName.replaceAll("/", "_");
		if (doctype.contains("casc.sast.149.PROCESS_PLAN") && (secret == null || "".equals(secret) || "null".equals(secret) || "公开".equals(secret))) {
			printFileName = com.ptc.wvs.server.util.Util.removeExtension(printFileName) + "（专用）.pdf";
		} else {
			printFileName = com.ptc.wvs.server.util.Util.removeExtension(printFileName) + ".pdf";
		}
		while(qr.hasMoreElements()){
			appData = (ApplicationData)qr.nextElement();
			String filename = appData.getFileName();
			if(!"PDFPreview.pdf".equals(filename) && !filename.startsWith("Print") && !filename.startsWith("PRINT")){
					PersistenceHelper.manager.delete(appData);
					PersistenceServerHelper.manager.update(document);
				}
			}
		trans.commit();

		return true;
	}

	@SuppressWarnings("deprecation")
	public static boolean deletePDFAttach(WTDocument document) throws ObjectNoLongerExistsException, WTException, RemoteException
			{
		Transaction trans = new Transaction();
		trans.start();
		document = (WTDocument) PersistenceHelper.manager.refresh(document);
		ApplicationData appData = null;
		QueryResult qr = ContentHelper.service.getContentsByRole(document, ContentRoleType.SECONDARY);
		String PRINTKEY = "PRINT";
		String primaryfileName = FilePrintUtil.getPrimaryFileName(document);
		String printFileName = FilePrintUtil.getQualityFileName(document, primaryfileName, PRINTKEY);
		RevisionControlled rc = (RevisionControlled) document;
		String doctype = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(rc);
		String secret = IBAHelper.getIBAStringValue(rc, "SECRET");
		printFileName = printFileName.replaceAll("/", "_");
		if (doctype.contains("casc.sast.149.PROCESS_PLAN") && (secret == null || "".equals(secret) || "null".equals(secret) || "公开".equals(secret))) {
			printFileName = com.ptc.wvs.server.util.Util.removeExtension(printFileName) + "（专用）.pdf";
		} else {
			printFileName = com.ptc.wvs.server.util.Util.removeExtension(printFileName) + ".pdf";
		}
		while(qr.hasMoreElements()){
			appData = (ApplicationData)qr.nextElement();
			String filename = appData.getFileName();
			if((filename.startsWith("Print") || filename.startsWith("PRINT")) && filename.endsWith("pdf")){
				PersistenceHelper.manager.delete(appData);
				PersistenceServerHelper.manager.update(document);
			}
			}
		trans.commit();

		return true;
	}



	@SuppressWarnings("deprecation")
	public static WTDocument uploadAttachForSOP(WTDocument document, String fileName, byte[] bytes)
			throws WTException, PropertyVetoException, FileNotFoundException, IOException {
		Transaction trans = new Transaction();
		trans.start();
		WTPrincipal currentuser = null;
		try{
			WTPrincipal admin = SessionHelper.manager.getAdministrator();
	        currentuser = SessionContext.setEffectivePrincipal(admin);
			ByteArrayInputStream inputStream = null;
			document = (WTDocument) PersistenceHelper.manager.refresh(document);
			ApplicationData appData = null;
			appData = ApplicationData.newApplicationData(document);
			// 主物件和附件
			// ContentRoleType.SECONDARY 表示 附件
			// ContentRoleType.PRIMARY 表示主物件
			appData.setRole(ContentRoleType.SECONDARY);
			// 设置文件名称
			appData.setFileName(fileName);

			inputStream = new ByteArrayInputStream(bytes);
			InputStream is = new ByteArrayInputStream(bytes);

			//设置默认表示法
			RepUtils.saveFileRep2(document, inputStream, fileName);

			appData = ContentServerHelper.service.updateContent((ContentHolder) document, appData, is,true); // 更新内容

			PersistenceServerHelper.manager.update(document);
			document = (WTDocument) ContentServerHelper.service.updateHolderFormat((FormatContentHolder) document); // 更新格式
			if (null != inputStream) {
				inputStream.close();
			}
			if (null != is) {
				is.close();
			}
			trans.commit();
		}finally{
			SessionContext.setEffectivePrincipal(currentuser);
		}
		return document;
	}



	public static WTDocument uploadAttach(WTDocument document, String fileName, byte[] bytes)
			throws WTException, PropertyVetoException, FileNotFoundException, IOException {

		ByteArrayInputStream inputStream = null;
		document = (WTDocument) PersistenceHelper.manager.refresh(document);
		ApplicationData appData = null;
		QueryResult qr = ContentHelper.service.getContentsByRole(document, ContentRoleType.SECONDARY);
		while(qr.hasMoreElements()){
			appData = (ApplicationData)qr.nextElement();
			PersistenceHelper.manager.delete(appData);
			PersistenceServerHelper.manager.update(document);
		}
		appData = ApplicationData.newApplicationData(document);
		// 主物件和附件
		// ContentRoleType.SECONDARY 表示 附件
		// ContentRoleType.PRIMARY 表示主物件
		appData.setRole(ContentRoleType.SECONDARY);
		// 设置文件名称
		appData.setFileName(fileName);

		inputStream = new ByteArrayInputStream(bytes);

		appData = ContentServerHelper.service.updateContent((ContentHolder) document, appData, inputStream,true); // 更新内容

		PersistenceServerHelper.manager.update(document);
		document = (WTDocument) ContentServerHelper.service.updateHolderFormat((FormatContentHolder) document); // 更新格式
		if (null != inputStream) {
			inputStream.close();
		}
		return document;
	}

	public static List<ApplicationData> getAttachFromDocument(
			WTDocument document) throws WTException {
		List<ApplicationData> adList = new ArrayList<ApplicationData>();
		// document = (WTDocument) PersistenceHelper.manager.refresh(document);
		ApplicationData appData = null;
		QueryResult qr = ContentHelper.service.getContentsByRole(document,
				ContentRoleType.SECONDARY);
		while (qr.hasMoreElements()) {
			appData = (ApplicationData) qr.nextElement();
			adList.add(appData);
		}

		return adList;
	}

	public static List<ApplicationData> getFuJianFromDocument(
			WTDocument document) throws WTException {
		List<ApplicationData> adList = new ArrayList<ApplicationData>();
		// document = (WTDocument) PersistenceHelper.manager.refresh(document);
		ApplicationData appData = null;
		QueryResult qr = ContentHelper.service.getContentsByRole(document,
				ContentRoleType.PRIMARY);
		while (qr.hasMoreElements()) {
			appData = (ApplicationData) qr.nextElement();
			adList.add(appData);
		}

		return adList;
	}

	public static WTPart getLatestDescribesWTPartsByDocument(WTDocument doc) throws WTException {
		WTPart part = null;
		if(doc != null) {
			QueryResult qr = WTPartHelper.service.getDescribesWTParts(doc);
			if(qr != null && qr.hasMoreElements()) {
				LatestConfigSpec lcs = new LatestConfigSpec();
				qr = lcs.process(qr);
				part = (WTPart)qr.nextElement();
			}
		}
		System.out.println("------getLatestDescribesWTPartsByDocument---part--"+part);
		return part;
	}
	public static WTDocument getDocumentByNumberAndVersion(String partNumber,String version)
			throws WTException {

		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, partNumber),
				new int[] { 0 });

		qs.setAdvancedQueryEnabled(true);
		qs = new LatestConfigSpec().appendSearchCriteria(qs);
		QueryResult qr = PersistenceHelper.manager.find(qs);
//		LatestConfigSpec lc = new LatestConfigSpec();
//		qr = lc.process(qr);
		while (qr.hasMoreElements()) {
			WTDocument temp = (WTDocument) qr.nextElement();
			if (temp.getIterationDisplayIdentifier().toString().startsWith(version)) {
				return temp;
			}
		}
		return null;
	}

	/**
	 * 根据编号和版本查询指定版本文档,精确到小版本
	* @author zhuhao
	* @date 2018-5-17
	* @param docNumber
	* @param version
	* @return
	* @throws WTException
	 */
	public static WTDocument getDocumentByNumberAndAllVersion(String docNumber,String version)throws WTException {
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, docNumber),
				new int[] { 0 });
		if(version == null || "".equals(version)){
			return null;
		}
		if(version.contains(".")){
			qs.appendAnd();
			String[] versionStr = version.split("\\.");
			qs.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", versionStr[0]),index);
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTDocument.class, "iterationInfo.identifier.iterationId", "=", versionStr[1]),index);
			qs.setAdvancedQueryEnabled(true);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lc = new LatestConfigSpec();
			qr = lc.process(qr);
			if (qr.hasMoreElements()) {
				WTDocument temp = (WTDocument) qr.nextElement();
				return temp;
			}
		}
		return null;
	}
	/**
	 *根据编号和版本查询文档最新版本
	* @author jyx
	* @date 2018-4-11
	* @param number
	* @param version
	* @return
	* @throws WTException
	 */
	public static WTDocument getWTDocumentByNumberAndVersion(String number,String version)
			throws WTException {
		WTDocument document = null;
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number), index);
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTDocument.class,	"state.state", SearchCondition.EQUAL, ProcessPlanConstants.LIFECYCLE_EN_APPROVED), index);
		//根据版本查询
		if(!version.equals("")){
			qs.appendAnd();
			if(version.length() ==1){
				qs.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", version),index);
			}else{
				String[] versionStr = version.split("\\.");
				qs.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", versionStr[0]),index);
				if(versionStr.length==2){
					qs.appendAnd();
					qs.appendWhere(new SearchCondition(WTDocument.class, "iterationInfo.identifier.iterationId", "=", versionStr[1]),index);
				}
			}
		}
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		if (qr.hasMoreElements()) {
			document = (WTDocument) qr.nextElement();
		}
		return document;
	}

	/**
	 *根据编号和版本号查询文档最新版本
	 * @author cjh
	 * @date 2022-12-02
	 * @param number
	 * @param version
	 * @return
	 * @throws WTException
	 */
	public static WTDocument getLaestWTDocumentByNumberAndVersion(String number,String version)
			throws WTException {
		WTDocument document = null;
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number), index);
		//根据版本查询
		if(!version.equals("")){
			qs.appendAnd();
			if(version.length() ==1){
				qs.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", version),index);
			}else{
				String[] versionStr = version.split("\\.");
				qs.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", versionStr[0]),index);
				if(versionStr.length==2){
					qs.appendAnd();
					qs.appendWhere(new SearchCondition(WTDocument.class, "iterationInfo.identifier.iterationId", "=", versionStr[1]),index);
				}
			}
		}
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		if (qr.hasMoreElements()) {
			document = (WTDocument) qr.nextElement();
		}
		return document;
	}

	/**
	 * 通过文件名称和软属性获取文件
	 *
	 * @author qianlong
	 * @date 2012-11-15
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static WTDocument getDocumnetByNameAndIBAAndVersion(String name, String ibaName, String ibaValue,String version) throws WTException,
			WTPropertyVetoException, RemoteException {
		WTDocument document = null;
		QuerySpec querySpec = new QuerySpec(WTDocument.class);
		querySpec.setAdvancedQueryEnabled(true);
		ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "."
				+ PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		querySpec.appendWhere(
				new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.EQUAL, name, true), index);
		querySpec.appendAnd();
		querySpec.appendWhere(new SearchCondition(WTDocument.class,	"state.state", SearchCondition.EQUAL, ProcessPlanConstants.LIFECYCLE_EN_APPROVED), index);
		//根据版本查询
		if(!version.equals("")){
			querySpec.appendAnd();
			if(version.length() ==1){
				querySpec.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", version),index);
			}else{
				String[] versionStr = version.split("\\.");
				querySpec.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", versionStr[0]),index);
				if(versionStr.length==2){
					querySpec.appendAnd();
					querySpec.appendWhere(new SearchCondition(WTDocument.class, "iterationInfo.identifier.iterationId", "=", versionStr[1]),index);
				}
			}
		}
		// 通过对象的软属性查询

		querySpec.appendAnd();
		querySpec.appendOpenParen();
		SubSelectExpression subSelectExpression = getStringIBAQuery(ibaName, ibaValue);
		querySpec.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
		querySpec.appendCloseParen();
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		if (queryResult.hasMoreElements()) {
			document = (WTDocument) queryResult.nextElement();
		}
		return document;
	}
	public static WTDocument getWTDocumentByOid(String oid) throws Exception {
		QuerySpec qSpec = new QuerySpec(WTDocument.class);
		int[] index = { 0 };
		SearchCondition sCondition = new SearchCondition(WTDocument.class,
				"thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL,
				Long.valueOf(oid));
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager
				.find((StatementSpec) qSpec);
		WTDocument document = null;
		qResult = new LatestConfigSpec().process(qResult);
		if (qResult.hasMoreElements()) {
			document = (WTDocument) qResult.nextElement();
		}
		return document;
	}

	public static List<WTDocument> getDocumnetsByNameAndIBAAndVersion(String name, String ibaName, String ibaValue, String version) throws WTException, WTPropertyVetoException, RemoteException {
		List<WTDocument> documents = new ArrayList<WTDocument>();
		QuerySpec querySpec = new QuerySpec(WTDocument.class);
		querySpec.setAdvancedQueryEnabled(true);
		ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		querySpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.EQUAL, name, true), index);
		querySpec.appendAnd();
		querySpec.appendWhere(new SearchCondition(WTDocument.class, "state.state", SearchCondition.EQUAL, ProcessPlanConstants.LIFECYCLE_EN_APPROVED), index);
		// 根据版本查询
		if (!version.equals("")) {
			querySpec.appendAnd();
			if (version.length() == 1) {
				querySpec.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", version), index);
			} else {
				String[] versionStr = version.split("\\.");
				querySpec.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", versionStr[0]), index);
				if (versionStr.length == 2) {
					querySpec.appendAnd();
					querySpec.appendWhere(new SearchCondition(WTDocument.class, "iterationInfo.identifier.iterationId", "=", versionStr[1]), index);
				}
			}
		}
		// 通过对象的软属性查询

		querySpec.appendAnd();
		querySpec.appendOpenParen();
		SubSelectExpression subSelectExpression = getStringIBAQuery(ibaName, ibaValue);
		querySpec.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
		querySpec.appendCloseParen();
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		while (queryResult.hasMoreElements()) {
			WTDocument document = (WTDocument) queryResult.nextElement();
			documents.add(document);
		}
		return documents;
	}

	public static WTDocument getWTDocument(String number, String name, String version, String iteration) throws WTException {
		WTDocument doc = null;
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number), index);
		if (name != null) {
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.EQUAL, name), index);
		}
		//根据版本查询
		if (version != null) {
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", version),index);
		}
		if (iteration != null) {
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTDocument.class, "iterationInfo.identifier.iterationId", "=", iteration),index);
		}
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		if (qr.hasMoreElements()) {
			doc = (WTDocument) qr.nextElement();
		}
		return doc;
	}

	/**
	 * 通过工艺文件编号(软属性)、名称、类型查找(工艺文件编号、名称模糊查询)文档
	 * @param number
	 * @param name
	 * @param typeName
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 */
	public static QueryResult getWTDocumentByLikeNumAndName(String number, String name, String typeName, String[] states) throws WTException, WTPropertyVetoException, RemoteException {
		QuerySpec qSpec = new QuerySpec(WTDocument.class);
		if (number != null && number.length() > 0) {
			qSpec.setAdvancedQueryEnabled(true);
			ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "."
					+ PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
			SubSelectExpression subSelectExpression = getStringIBAQuery2("PPNUMBER", number);
			qSpec.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
		}
		if (name != null && name.length() > 0) {
			if (qSpec.getConditionCount() > 0) {
				qSpec.appendAnd();
			}
			qSpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, name, false),
					index);
		}
		if(typeName != null){
			if (qSpec.getConditionCount() > 0) {
				qSpec.appendAnd();
			}
			TypeUtil.getTypeQuery(WTDocument.class, typeName, qSpec);
		}
		if (states != null && states.length > 0) {
			if (qSpec.getConditionCount() > 0) {
				qSpec.appendAnd();
			}
			qSpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, states, true), index);
		}
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		qResult = new LatestConfigSpec().process(qResult);
		return qResult;
	}

	public static QueryResult getWTDocumentByLikeNumAndName(String number, String name, String typeName) throws WTException, WTPropertyVetoException, RemoteException {
		QuerySpec qSpec = new QuerySpec(WTDocument.class);
		if (number != null && number.length() > 0) {
			qSpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.LIKE, number, false), index);
		}
		if (name != null && name.length() > 0) {
			if (qSpec.getConditionCount() > 0) {
				qSpec.appendAnd();
			}
			qSpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, name, false), index);
		}
		if(typeName != null){
			if (qSpec.getConditionCount() > 0) {
				qSpec.appendAnd();
			}
			TypeUtil.getTypeQuery(WTDocument.class, typeName, qSpec);
		}
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		qResult = new LatestConfigSpec().process(qResult);
		return qResult;
	}

	public static List<WTDocument> getAllLatestWTDocumentByNumber(String number) throws WTException{
		List<WTDocument> documentlist = new ArrayList<WTDocument>();
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number), index);
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTDocument.class, "iterationInfo.latest", "TRUE"), index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			WTDocument document = (WTDocument) qr.nextElement();
			documentlist.add(document);
		}
		return documentlist;


	}
}
