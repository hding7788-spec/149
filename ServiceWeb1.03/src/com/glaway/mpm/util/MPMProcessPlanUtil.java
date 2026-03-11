package com.glaway.mpm.util;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ext.casc.integrate.util.BomUtil;
import ext.casc.report.technics.DownloadTechnicsReportUtil;
import ext.casc.util.WCUtil;
import org.dom4j.DocumentException;
import org.jdom.Element;
import org.xml.sax.Attributes;

import wt.associativity.NCServerHolder;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.ObjectIdentifier;
import wt.fc.PersistInfo;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTUser;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartMaster;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.IterationIdentifier;
import wt.vc.OneOffVersioned;
import wt.vc.VersionControlHelper;
import wt.vc.VersionIdentifier;
import wt.vc.Versioned;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;

import com.ptc.windchill.mpml.MPMDocumentDescribeLink;
import com.ptc.windchill.mpml.processplan.MPMPartToProcessPlanLink;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.MPMProcessPlanHelper;
import com.ptc.windchill.mpml.processplan.MPMProcessPlanMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMConsumableResourceMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationHolder;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationToConsumableLink;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationToOperatedPartLink;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationToPartLink;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationToWorkCenterLink;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import com.ptc.windchill.mpml.resource.MPMWorkCenterMaster;

import ext.casc.purge.PurgeDataProcessor;

public class MPMProcessPlanUtil implements RemoteAccess {
	private static int[] index = new int[] { 0 };

	/**
	 * 通过编号查询 MPMProcessPlan
	 *
	 * @author qianlong
	 * @date 2012-9-6
	 * @param number
	 * @return
	 * @throws Exception
	 */
	public static MPMProcessPlan getMPMProcessPlanByNumber(String number) throws WTPropertyVetoException, WTException {

		MPMProcessPlan processPlan = null;
		QuerySpec querySpec = new QuerySpec(MPMProcessPlan.class);
		querySpec.appendWhere(new SearchCondition(MPMProcessPlan.class, MPMProcessPlan.NUMBER, SearchCondition.EQUAL,
				number.toUpperCase()), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		if (queryResult.hasMoreElements()) {
			processPlan = (MPMProcessPlan) queryResult.nextElement();
		}

		return processPlan;
	}

	/**
	 * 获取工艺关联的工艺压缩包
	 *
	 * @author qianlong
	 * @date 2012-12-28
	 * @param holder
	 * @return
	 * @throws WTException
	 *
	 */
	public static WTDocument getWTDocumentByProcessPlan(MPMProcessPlan processPlan) throws WTException {
		WTDocument document = null;
		QueryResult queryResult = PersistenceHelper.manager.navigate(processPlan,
				MPMDocumentDescribeLink.ROLE_BOBJECT_ROLE, MPMDocumentDescribeLink.class);
		queryResult = new LatestConfigSpec().process(queryResult);
		if (queryResult.hasMoreElements()) {
			document = (WTDocument) queryResult.nextElement();
		}
		return document;
	}

	/**
	 * 获取工艺计划关联的零件对象
	 *
	 * @author qianlong
	 * @date 2013-7-25
	 * @param processPlan
	 * @return
	 * @throws WTException
	 */
	public static WTPart getMPMProcessplanRelatedPart(MPMProcessPlan processPlan) throws WTException {
		WTPart part = null;
		QueryResult qr = MPMProcessPlanHelper.service.getWTParts(processPlan, NCServerHolder.makeForLatestConfigSpec());
		if (qr.hasMoreElements()) {
			part = (WTPart) qr.nextElement();
		}
		return part;
	}

	/**
	 * 获取子操作
	 *
	 * @author qianlong
	 * @date 2012-12-28
	 * @param operation
	 * @return
	 * @throws WTException
	 *
	 */
	public static QueryResult getChildMPMOperation(MPMOperationHolder holder) throws WTException {

		return MPMProcessPlanHelper.service.getOperationsFromOperationHolder(holder, NCServerHolder
				.makeForLatestConfigSpec(), false);

	}

	/**
	 * 获取工步工序和消耗资源之间的link
	 *
	 * @author qianlong
	 * @date 2012-12-28
	 * @param operation
	 * @return
	 * @throws WTException
	 *
	 */
	public static QueryResult getMPMOperationToConsumableLink(MPMOperation operation) throws WTException {
		return MPMProcessPlanHelper.service.getMPMOperationToConsumables(operation, NCServerHolder
				.makeForLatestConfigSpec(), false);
	}

	/**
	 * 通过编号查询最新的工序或工步
	 *
	 * @author lbzhang
	 * @date 2012-12-15下午09:28:50
	 * @param number
	 * @return
	 * @throws WTException
	 */
	public static MPMOperation getMPMOperationLatestByNumber(String number) throws WTException {
		if (null == number || "".equals(number)) {
			return null;
		}
		MPMOperation operation = null;
		int[] index = new int[] { 0 };
		QuerySpec querySpec = new QuerySpec(MPMOperation.class);
		querySpec.appendWhere(new SearchCondition(MPMOperation.class, MPMOperation.NUMBER, SearchCondition.EQUAL,
				number.toUpperCase()), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		while (queryResult.hasMoreElements()) {
			operation = (MPMOperation) queryResult.nextElement();
		}
		return operation;
	}

	/**
	 * 通过编号名称和类型获取工艺
	 *
	 * @author qianlong
	 * @date 2012-11-5
	 * @param number
	 * @param name
	 * @param type
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @throws RemoteException
	 * 
	 */
	public static List<MPMProcessPlan> getMPMProcessPlanByLikeNumberNameType(String number, String name, String type)
			throws WTPropertyVetoException, WTException, RemoteException {
		List<MPMProcessPlan> list = new ArrayList<MPMProcessPlan>();
		QuerySpec querySpec = new QuerySpec(MPMProcessPlan.class);
		querySpec.setAdvancedQueryEnabled(true);
		type = Util.formateString(type);
		if (!"".equals(type)) {
			TypeUtil.getTypeQuery(MPMProcessPlan.class, type, querySpec);
			querySpec.appendAnd();
		}

//		querySpec.appendWhere(new SearchCondition(MPMProcessPlan.class, MPMProcessPlan.NUMBER, SearchCondition.LIKE,
//				number, false), index);

		//查询工艺规程软属性工艺文件编号"PPNUMBER"
		ClassAttribute caId = new ClassAttribute(MPMProcessPlan.class, Persistable.PERSIST_INFO + "."
				+ PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		SubSelectExpression subSelectExpression = MPMResourceUtil.getStringIBAQuery("PPNUMBER", Util.formatSearchString(number));
		querySpec.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);

		querySpec.appendAnd();

		querySpec.appendWhere(new SearchCondition(MPMProcessPlan.class, MPMProcessPlan.NAME, SearchCondition.LIKE,
				name, false), index);

		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		while (queryResult.hasMoreElements()) {
			MPMProcessPlan processPlan = (MPMProcessPlan) queryResult.nextElement();
			list.add(processPlan);
		}

		return list;
	}

	/**
	 * 通过编号查询 MPMProcessPlanMaster
	 * 
	 * @author qianlong
	 * @date 2012-9-6
	 * @param number
	 * @return
	 * @throws Exception
	 */
	public static MPMProcessPlanMaster getMPMProcessPlanMaster(String number) throws WTPropertyVetoException,
			WTException {
		MPMProcessPlanMaster processPlanMaster = null;
		QuerySpec querySpec = new QuerySpec(MPMProcessPlanMaster.class);
		querySpec.appendWhere(new SearchCondition(MPMProcessPlan.class, MPMProcessPlan.NUMBER, SearchCondition.EQUAL,
				number.toUpperCase()), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		while (queryResult.hasMoreElements()) {
			processPlanMaster = (MPMProcessPlanMaster) queryResult.nextElement();
		}

		return processPlanMaster;
	}

	/**
	 * 创建 MPMProcessPlan
	 * 
	 * @author qianlong
	 * @date 2012-9-6
	 * @param container
	 * @throws Exception
	 * 
	 */
	public static MPMProcessPlan createMPMProcessPlan(Attributes attributes, WTContainer wtContainer)
			throws WTPropertyVetoException, WTException {
		MPMProcessPlan mpmProcessPlan = null;

		mpmProcessPlan = MPMProcessPlan.newMPMProcessPlan(attributes.getValue(""), attributes.getValue(""),
				QuantityUnit.EA);
		mpmProcessPlan.setContainer(wtContainer);
		PersistenceHelper.manager.save(mpmProcessPlan);

		return mpmProcessPlan;
	}

	/**
	 * 创建工艺
	 * 
	 * @author qianlong
	 * @date 2012-10-31
	 * @param element
	 * @param wtContainer
	 * @return
	 * @throws Exception
	 * 
	 */
	@SuppressWarnings("deprecation")
	public static MPMProcessPlan createMPMProcessPlan(String number, String name, WTContainer wtContainer,
			String typeName, String folderPath, String version) throws Exception {
		MPMProcessPlan mpmProcessPlan = null;
		//判断该编号的对象是否存在
		mpmProcessPlan = getMPMProcessPlanByNumber(number);
		if(mpmProcessPlan != null) {
			System.out.println("MPMProcessPlanUtil.createMPMProcessPlan() number is " + number + " not null!");
			return mpmProcessPlan;
		}

		String versions[] = version.split("\\.");
		mpmProcessPlan = MPMProcessPlan.newMPMProcessPlan();
		if (null != number && !"".equals(number)) {
			mpmProcessPlan.setNumber(number);
		}
		mpmProcessPlan.setName(name);
		mpmProcessPlan.setContainer(wtContainer);
		TypeUtil.setType(mpmProcessPlan, typeName);
		Folder folder = FolderUtil.getFolder(folderPath, WTContainerRef.newWTContainerRef(wtContainer));
		FolderHelper.assignFolder(mpmProcessPlan, folder);
		Util.setVersion(mpmProcessPlan, versions[0]);
		Util.setIteration(mpmProcessPlan, versions[1]);
		PersistenceHelper.manager.save(mpmProcessPlan);
		return mpmProcessPlan;
	}

	/**
	 * 创建操作
	 * 
	 * @author qianlong
	 * @date 2012-10-31
	 * @param attributes
	 * @param wtContainer
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * 
	 */
	public static MPMOperation createOperation(Attributes attributes, WTContainer wtContainer)
			throws WTPropertyVetoException, WTException {
		MPMOperation mpmOperation = null;

		mpmOperation = MPMOperation.newMPMOperation();
		mpmOperation.setName(attributes.getValue(""));
		mpmOperation.setContainer(wtContainer);
		PersistenceHelper.manager.save(mpmOperation);

		return mpmOperation;
	}

	/**
	 * 创建操作
	 * 
	 * @author qianlong
	 * @date 2012-10-31
	 * @param element
	 * @param wtContainer
	 * @return
	 * @throws Exception
	 * 
	 */
	@SuppressWarnings("deprecation")
	public static MPMOperation createOperation(String number, String name, WTContainer wtContainer, String typeName,
			String folderPath) throws Exception {
		MPMOperation mpmOperation = null;
		//判断该编号的对象是否已经存在
		mpmOperation = getMPMOperationLatestByNumber(number);
		if(mpmOperation != null) {
			System.out.println("MPMProcessPlanUtil.createOperation(): number is " + number + " not null!");
			return mpmOperation;
		}

		mpmOperation = MPMOperation.newMPMOperation();
		if (null != number && !"".equals(number)) {
			mpmOperation.setNumber(number);
		}
		mpmOperation.setName(name);
		mpmOperation.setContainer(wtContainer);
		TypeUtil.setType(mpmOperation, typeName);
		Folder folder = FolderUtil.getFolder(folderPath, WTContainerRef.newWTContainerRef(wtContainer));
		FolderHelper.assignFolder(mpmOperation, folder);
		PersistenceHelper.manager.save(mpmOperation);

		return mpmOperation;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-10-31
	 * @param element
	 * @param wtContainer
	 * @return
	 * @throws Exception
	 *
	 */
	public static MPMOperation createOperation(Element element, WTContainer wtContainer, String typeName,
			String folderPath) throws Exception {
		MPMOperation mpmOperation = null;

		mpmOperation = MPMOperation.newMPMOperation();
		mpmOperation.setName(element.getAttributeValue("stepName"));
		mpmOperation.setContainer(wtContainer);
		TypeDefinitionReference typeRef = ClientTypedUtility.getTypeDefinitionReference(typeName);
		mpmOperation.setTypeDefinitionReference(typeRef);
		Folder folder = FolderUtil.getFolder(folderPath, WTContainerRef.newWTContainerRef(wtContainer));
		FolderHelper.assignFolder(mpmOperation, folder);
		PersistenceHelper.manager.save(mpmOperation);
		// IBAHelper helper = new IBAHelper();
		// helper.setIBAValue(mpmOperation, "", element.getAttributeValue(""));

		return mpmOperation;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-10-31
	 * @param element
	 * @param wtContainer
	 * @return
	 * @throws Exception
	 *
	 */
	public static MPMOperation createSubOperation(Element element, WTContainer wtContainer, String typeName,
			String folderPath) throws Exception {
		MPMOperation mpmOperation = null;

		mpmOperation = MPMOperation.newMPMOperation();
		mpmOperation.setName(element.getAttributeValue("stepNumber"));
		mpmOperation.setContainer(wtContainer);
		TypeDefinitionReference typeRef = ClientTypedUtility.getTypeDefinitionReference(typeName);
		mpmOperation.setTypeDefinitionReference(typeRef);
		Folder folder = FolderUtil.getFolder(folderPath, WTContainerRef.newWTContainerRef(wtContainer));
		FolderHelper.assignFolder(mpmOperation, folder);
		PersistenceHelper.manager.save(mpmOperation);
		// IBAHelper helper = new IBAHelper();
		// helper.setIBAValue(mpmOperation, "", element.getAttributeValue(""));

		return mpmOperation;
	}

	/**
	 * 创建操作
	 * 
	 * @author qianlong
	 * @date 2012-10-31
	 * @param element
	 * @param wtContainer
	 * @return
	 * @throws Exception
	 * 
	 */
	@SuppressWarnings("deprecation")
	public static MPMOperation createSubOperation(String number, String name, WTContainer wtContainer, String typeName,
			String folderPath) throws Exception {
		MPMOperation mpmOperation = MPMOperation.newMPMOperation();
		if (null != number && !"".equals(number)) {
			mpmOperation.setNumber(number);
		}
		mpmOperation.setName(name);
		mpmOperation.setContainer(wtContainer);
		TypeUtil.setType(mpmOperation, typeName);
		Folder folder = FolderUtil.getFolder(folderPath, WTContainerRef.newWTContainerRef(wtContainer));
		FolderHelper.assignFolder(mpmOperation, folder);
		PersistenceHelper.manager.save(mpmOperation);

		return mpmOperation;
	}

	/**
	 * 创建工艺和操作之间，操作和操作之间的link
	 * 
	 * @author qianlong
	 * @date 2012-10-31
	 * @param operationHolder
	 * @param operationMaster
	 * @param lable
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws Exception
	 */
	public static void createMPMOperationUsageLink(MPMOperationHolder operationHolder,
			MPMOperationMaster operationMaster, String lable) throws WTException,
			WTPropertyVetoException {
		MPMOperationUsageLink mpmOperationUsageLink = MPMOperationUsageLink.newMPMOperationUsageLink(operationHolder,
				operationMaster, lable);
		PersistenceServerHelper.manager.insert(mpmOperationUsageLink);

	}

	public static MPMOperationUsageLink getMPMOperationUsageLinkByMPMOperation(MPMOperation operation) throws WTException {
        QuerySpec qSpec = new QuerySpec(MPMOperationUsageLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(operation.getMaster()).getId();
        SearchCondition sCondition = new SearchCondition(MPMOperationUsageLink.class, "roleBObjectRef.key.id",
                SearchCondition.EQUAL, longId);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        while (qResult.hasMoreElements()) {
            MPMOperationUsageLink link = (MPMOperationUsageLink) qResult.nextElement();
            return link;
        }
        return null;
    }

	/**
	 * 创建操作和工作中心之间的link
	 * 
	 * @author qianlong
	 * @date 2012-10-31
	 * @param operation
	 * @param workCenterMaster
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * 
	 */
	public static void createMPMOperationToWorkCenterLink(

	MPMOperation operation, MPMWorkCenterMaster workCenterMaster) throws WTException, WTPropertyVetoException {
		MPMOperationToWorkCenterLink operationToWorkCenterLink = MPMOperationToWorkCenterLink
				.newMPMOperationToWorkCenterLink(operation, workCenterMaster);
		PersistenceServerHelper.manager.insert(operationToWorkCenterLink);

	}

	/**
	 * 创建操作之间link
	 * 
	 * @author qianlong
	 * @date 2012-10-31
	 * @param operation
	 * @param wtPartMaster
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * 
	 */
	public static void createMPMOperationToPartLink(

	MPMOperation operation, WTPartMaster wtPartMaster) throws WTException, WTPropertyVetoException {
		MPMOperationToPartLink mpmOperationToPartLink = MPMOperationToPartLink.newMPMOperationToPartLink(operation,
				wtPartMaster);
		PersistenceServerHelper.manager.insert(mpmOperationToPartLink);
	}

	/**
	 * 创建操作与零件之间link
	 * 
	 * @author qianlong
	 * @date 2012-10-31
	 * @param operation
	 * @param wtPartMaster
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * 
	 */
	public static void createOperationToOperatedPartLink(

	MPMOperation operation, WTPartMaster wtPartMaster) throws WTException, WTPropertyVetoException {
		MPMOperationToOperatedPartLink operationToOperatedPartLink = MPMOperationToOperatedPartLink
				.newMPMOperationToOperatedPartLink(operation, wtPartMaster);
		PersistenceServerHelper.manager.insert(operationToOperatedPartLink);
	}

	/**
	 * 创佳操作与消耗品之间link
	 * 
	 * @author qianlong
	 * @date 2012-10-31
	 * @param operation
	 * @param resourceMaster
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * 
	 */
	public static MPMOperationToConsumableLink createMPMOperationToConsumableLink(

	MPMOperation operation, MPMConsumableResourceMaster resourceMaster) throws WTException, WTPropertyVetoException {
		MPMOperationToConsumableLink mpmOperationToConsumableLink = MPMOperationToConsumableLink
				.newMPMOperationToConsumableLink(operation, resourceMaster);
		PersistenceServerHelper.manager.insert(mpmOperationToConsumableLink);
		return mpmOperationToConsumableLink;
	}

	/**
	 * . 创建MPMPartToProcessPlanLink
	 * 
	 * @author qianlong
	 * @date 2012-9-10
	 * @param part
	 * @param processPlan
	 * @throws Exception
	 */
	public static void createMPMPartToProcessPlanLink(WTPart part, MPMProcessPlan processPlan)
			throws WTPropertyVetoException, WTException {

		MPMPartToProcessPlanLink mpmPartToProcessPlanLink = MPMPartToProcessPlanLink.newMPMPartToProcessPlanLink(part,
				processPlan);
		PersistenceServerHelper.manager.insert(mpmPartToProcessPlanLink);

	}

	/**
	 * 创建MPMDocumentDescribeLink
	 * 
	 * @author qianlong
	 * @date 2013-7-23
	 * @param processPlan
	 * @param document
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	public static void createMPMDocumentDescribeLink(MPMProcessPlan processPlan, WTDocument document)
			throws WTPropertyVetoException, WTException {

		MPMDocumentDescribeLink mpmDocumentDescribeLink = MPMDocumentDescribeLink.newMPMDocumentDescribeLink(
				processPlan, document);
		PersistenceServerHelper.manager.insert(mpmDocumentDescribeLink);

	}

	/**
	 * 删除工艺下面的所有的工序link
	 * 
	 * @author qianlong
	 * @throws WTException
	 * @date 2013-5-24
	 * 
	 */
	public static void deleteMPMOperationUsageLink(MPMProcessPlan processPlan) throws WTException {
		QueryResult qr = MPMProcessPlanHelper.service.getMPMOperationUsageLinks(processPlan);
		while (qr.hasMoreElements()) {
			MPMOperationUsageLink link = (MPMOperationUsageLink) qr.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}
	}

	/**
	 * 删除 MPMProcessPlan关联零件的link
	 * 
	 * @author qianlong
	 * @date 2012-9-12
	 * @param mpmProcessPlan
	 * @throws Exception
	 * @throws Exception
	 */
	public static void deleteMPMPartToProcessPlanLink(MPMProcessPlan mpmProcessPlan) throws WTPropertyVetoException,
			WTException {
		QueryResult queryResult = PersistenceHelper.manager.navigate(mpmProcessPlan,
				MPMPartToProcessPlanLink.ROLE_AOBJECT_ROLE, MPMPartToProcessPlanLink.class, false);
		while (queryResult.hasMoreElements()) {
			PersistenceHelper.manager.delete((MPMPartToProcessPlanLink) queryResult.nextElement());
		}

	}

	/**
	 * 删除 MPMProcessPlan 关联文档的link
	 * 
	 * @author qianlong
	 * @date 2012-9-12
	 * @param mpmProcessPlan
	 * @throws Exception
	 * @throws Exception
	 */
	public static void deleteMPMDocumentDescribeLink(MPMProcessPlan mpmProcessPlan) throws WTPropertyVetoException,
			WTException {
		QueryResult queryResult = PersistenceHelper.manager.navigate(mpmProcessPlan,
				MPMDocumentDescribeLink.ROLE_BOBJECT_ROLE, MPMDocumentDescribeLink.class, false);
		while (queryResult.hasMoreElements()) {
			PersistenceServerHelper.manager.remove((MPMDocumentDescribeLink) queryResult.nextElement());
		}
	}

	/**
	 * 删除工艺与工序或者工序与工步之间的关联关系
	 * 
	 * @author lbzhang
	 * @date 2012-12-15下午10:02:42
	 * @param linkList
	 * @throws WTException
	 */
	public static void deleteOperationUsageLink(List<MPMOperationUsageLink> linkList) throws WTException {
		for (int i = 0; i < linkList.size(); i++) {
			MPMOperationUsageLink link = linkList.get(i);
			PersistenceServerHelper.manager.remove(link);
		}
	}

	public static void deleteWTPartDescribeDocLink(String docNumber) throws RemoteException, WTException {
		boolean falg = SessionServerHelper.manager.setAccessEnforced(false);
		String user = null;
		try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
	    } catch (Exception e) {
	    }
		WTDocument doc = WTDocumentUtil.getDocumentByNumber(docNumber);
		if(doc != null) {
			List list = getDocDescribeLinksByDoc(doc);
			WTPartDescribeLink wtPartDescribeLink = null;
	        for (int i = 0; i < list.size(); i++) {
	            wtPartDescribeLink = (WTPartDescribeLink) list.get(i);
	           // PersistenceServerHelper.manager.remove(wtPartDescribeLink);
	        }
	        //Iterated iterated = VersionControlHelper.service.getLatestIteration(doc, false);
			//删除流程
			QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(doc, null, null);
			if (qrProcs.hasMoreElements()) {
				WfProcess pro = (WfProcess) qrProcs.nextElement();
				PersistenceHelper.manager.delete(pro);
			}
			//从更改单中移除
			PurgeDataProcessor.removeFromChange(doc);
			//如果是space版本则全部删除
			if(doc.getVersionInfo().getIdentifier().getValue().equals("space")){
				//删除所有小版本Link
		        QueryResult qr =  VersionControlHelper.service.allIterationsOf(doc.getMaster());
		        while(qr.hasMoreElements()){
		        	WTDocument iterated = (WTDocument)qr.nextElement();
		        	list = getDocDescribeLinksByDoc(iterated);
		        	for (int i = 0; i < list.size(); i++) {
		 	            wtPartDescribeLink = (WTPartDescribeLink) list.get(i);
		 	            PersistenceServerHelper.manager.remove(wtPartDescribeLink);
		 	        }
		        }
		        PersistenceHelper.manager.delete(doc);

			} else {//如果是修订版本则删除最新小版本

				//PurgeDataProcessor.removeFromChange(doc);
				Iterated iterated = VersionControlHelper.service.getLatestIteration(doc, false);
				PersistenceHelper.manager.delete(iterated);
			}

		}
		SessionServerHelper.manager.setAccessEnforced(falg);
		 if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
		 }
	}

	/**
	 * 升版工艺
	 * 
	 * @author qianlong
	 * @param creator
	 * @throws Exception
	 * @date 2013-7-25
	 */
	public static MPMProcessPlan reviseProcessPlanVersion(MPMProcessPlan processPlan, WTUser creator) throws Exception {
		// 取最新大版本中的最新一个非一次性版本作为版本编号基础
		if(creator==null){
			 creator =(WTUser) processPlan.getModifier().getObject();
		}
		WTUser currentuser = (WTUser)SessionHelper.manager.getPrincipal();
		SessionHelper.manager.setPrincipal(creator.getAuthenticationName());
		QueryResult qr = VersionControlHelper.service.allVersionsOf(processPlan);
		Versioned vMax = (Versioned) qr.nextElement();

		// 取统一大版本中的最新一个非一次性版本作为新版内容基础
		Versioned vBase = null;
		qr = VersionControlHelper.service.allVersionsFrom(processPlan);
		while (qr.hasMoreElements()) {
			vBase = (Versioned) qr.nextElement();
			if (!(vBase instanceof OneOffVersioned) || !VersionControlHelper.isAOneOff((OneOffVersioned) vBase))
				break;
		}
		if (vBase == null) // 应该不可能的错误
			throw new Exception("Unknown Error, no normal version found.");

		// 检查同一大版本中最新一个非一次性版本是否被检出
		if (vBase instanceof Workable && WorkInProgressHelper.isCheckedOut((Workable) vBase))
			throw new Exception("选中版本的最新非先行更改版本正被检出，不能进行修订!");

		// 取其下一版本号和初始小版本号创建新版本
		// 新建并保存修订版本
		VersionIdentifier vi = VersionControlHelper.nextVersionId(vMax);
		IterationIdentifier ii = VersionControlHelper.firstIterationId(vMax);
		MPMProcessPlan newVersioned = (MPMProcessPlan) VersionControlHelper.service.newVersion(vMax, vi, ii);
		newVersioned = (MPMProcessPlan) PersistenceHelper.manager.store(newVersioned);
		SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
		return newVersioned;
	}

	/**
	 * 升版工序或者工步
	 * 
	 * @author lbzhang
	 * @date 2012-12-15下午09:43:53
	 * @param operation
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("deprecation")
	public static MPMOperation reviseOperationVersion(MPMOperation operation) throws WTException,
			WTPropertyVetoException {
		MPMOperation operationO = getMPMOperationLatestByNumber(operation.getNumber());

		MPMOperation newOperation = (MPMOperation) VersionControlHelper.service.newVersion(operationO);
		String version = newOperation.getVersionIdentifier().getValue();
		GLLogger.debug("new version==>" + version);
		FolderHelper.assignFolder(newOperation, FolderHelper.service.getFolder(operationO));
		newOperation = (MPMOperation) PersistenceHelper.manager.store(newOperation);
		return newOperation;
	}

	/**
	 * 设置工序或者工步的软属性
	 * 
	 * @author lbzhang
	 * @date 2012-11-27下午09:36:00
	 * @param processPlan
	 * @param ibaName
	 * @param ibaValue
	 * @throws Exception
	 */
	public static void setMPMProcessPlanAttri(String oid, String ibaName, String ibaValue) throws Exception {

		MPMOperation operation = (MPMOperation) ReferenceFactory
				.getObjectbyOid("com.ptc.windchill.mpml.processplan.operation.MPMOperation:" + oid);
		IBAHelper helper = new IBAHelper(operation);
		Map<String, String> ibaMap = new HashMap<String, String>();
		ibaMap.put(ibaName, ibaValue);
		GLLogger.debug("ibaName===>" + ibaName);
		GLLogger.debug("ibaValue===>" + ibaValue);
		helper.setIBAValue(operation, ibaMap);
		operation = (MPMOperation) PersistenceHelper.manager.refresh(operation);
	}

	/**
	 * 判断零件是否关联工艺
	 * 
	 * @author lbzhang
	 * @date 2013-5-10
	 * @param part
	 * @return
	 * @throws WTException
	 * 
	 */
	public static boolean getMpmProcessPlanByPart(WTPart part) throws WTException {
		QuerySpec qs = new QuerySpec(MPMPartToProcessPlanLink.class);
		qs.appendWhere(new SearchCondition(MPMPartToProcessPlanLink.class, "roleAObjectRef.key.id",
				SearchCondition.EQUAL, Util.getLongOid(part)), index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		if (qr.hasMoreElements()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取零件关联的工艺规程
	 * 
	 * @author lbzhang
	 * @date 2013-5-14
	 * @param part
	 * @return
	 * @throws WTException
	 * 
	 */
	public static MPMProcessPlan getProcessPlanByPart(WTPart part) throws WTException {
		MPMProcessPlan mpmprocessPlan = null;
		QuerySpec qs = new QuerySpec(MPMPartToProcessPlanLink.class);
		qs.appendWhere(new SearchCondition(MPMPartToProcessPlanLink.class, "roleAObjectRef.key.id",
				SearchCondition.EQUAL, Util.getLongOid(part)), index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		if (qr.hasMoreElements()) {
			MPMPartToProcessPlanLink link = (MPMPartToProcessPlanLink) qr.nextElement();
			mpmprocessPlan = (MPMProcessPlan) link.getRoleBObject();
			QueryResult qrTemp = VersionControlHelper.service.allVersionsOf(mpmprocessPlan.getMaster());
			mpmprocessPlan = (MPMProcessPlan) qrTemp.nextElement();
		}
		return mpmprocessPlan;
	}

	/**
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
	public static MPMOperation setPrimaryForMPMOperation() throws WTException, PropertyVetoException,
			FileNotFoundException, IOException {
		String fileName = "计划管理项目配置说明 - 2011-12-07.doc";
		MPMOperation operation = (MPMOperation) ReferenceFactory
				.getObjectbyOid("OR:com.ptc.windchill.mpml.processplan.operation.MPMOperation:447364");
		FileInputStream inputStream = new FileInputStream(new File(
"C:\\Users\\Administrator\\Desktop/计划管理项目配置说明 - 2011-12-07.doc"));

		Transaction trans = new Transaction();
		trans.start();
		operation = (MPMOperation) PersistenceHelper.manager.refresh(operation);
		ApplicationData appData = ApplicationData.newApplicationData(operation);
		// 主物件和附件
		// ContentRoleType.SECONDARY 表示 附件
		// ContentRoleType.PRIMARY 表示主物件
		appData.setRole(ContentRoleType.SECONDARY);
		// 设置文件名称
		appData.setFileName(fileName);

		appData = ContentServerHelper.service.updateContent((ContentHolder) operation, appData, inputStream); // 更新内容
		PersistenceServerHelper.manager.update(operation);
		// operation = (MPMOperation)
		// ContentServerHelper.service.updateHolderFormat((FormatContentHolder)
		// operation); // 更新格式
		if (null != inputStream) {
			inputStream.close();
		}
		trans.commit();

		return operation;
	}

	/**
	 * 获取工艺规程关联的工序的link(其中包含工艺与工序的link、工序与工步的link)
	 * 
	 * @author lbzhang
	 * @date 2012-12-15下午04:27:54
	 * @param mpmProcessPlan
	 * @return
	 * @throws WTException
	 */
	public static List<MPMOperationUsageLink> getMPMOperationUsageLinkList(MPMOperationHolder holder)
			throws WTException {
		List<MPMOperationUsageLink> linkList = new ArrayList<MPMOperationUsageLink>();
		QueryResult qr = getMPMOperationUsageLink(holder);
		while (qr.hasMoreElements()) {
			MPMOperationUsageLink link = (MPMOperationUsageLink) qr.nextElement();
			linkList.add(link);
		}
		return linkList;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-12-28
	 * @param holder
	 * @return
	 * @throws WTException
	 *
	 */
	public static QueryResult getMPMOperationUsageLink(MPMOperationHolder holder) throws WTException {
		return MPMProcessPlanHelper.service.getMPMOperationUsageLinks(holder);
	}

    /**
	 * 获取文档的相关部件
	 * 
	 * @param doc
	 * @return List<WTPartDescribeLink> 相关部件的link的集合
	 * @throws WTException
	 */
    private static List getDocDescribeLinksByDoc(WTDocument doc) throws WTException {
        List list = new ArrayList();
        QuerySpec qSpec = new QuerySpec(WTPartDescribeLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(doc).getId();
        SearchCondition sCondition = new SearchCondition(WTPartDescribeLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL,
                longId);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        WTPartDescribeLink link = null;
        while (qResult.hasMoreElements()) {
            link = (WTPartDescribeLink) qResult.nextElement();
            list.add(link);
        }
        return list;
    }

    public static void uploadAttach(MPMProcessPlan pplan,String filePath,String fileName) throws WTException {
        try {
        	boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        	File file = new File(filePath);
        	if(!file.exists()) {
        		return;
        	}
        	InputStream is = new FileInputStream(file);
        	ContentHolder holder = (ContentHolder) ContentHelper.service.getContents((ContentHolder)pplan);
            holder = (ContentHolder) PersistenceHelper.manager.refresh(holder);
            ApplicationData data = ApplicationData.newApplicationData(holder);
            data.setRole(ContentRoleType.SECONDARY);
            data.setFileName(fileName);
            data.setUploadedFromPath(fileName);
            ContentServerHelper.service.updateContent(holder, data, is);

            is.close();
            SessionServerHelper.manager.setAccessEnforced(flag);
        } catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public static MPMProcessPlan getProcessPlanByWTDocument(WTDocument doc) throws WTException {
    	MPMProcessPlan plan = null;
		QueryResult queryResult = PersistenceHelper.manager.navigate(doc,
				MPMDocumentDescribeLink.ROLE_AOBJECT_ROLE, MPMDocumentDescribeLink.class);
		queryResult = new LatestConfigSpec().process(queryResult);
		if (queryResult.hasMoreElements()) {
			plan= (MPMProcessPlan) queryResult.nextElement();
		}
		return plan;
	}

    public static List<MPMProcessPlan> getAllProcessPlanByWTDocument(WTDocument doc) throws WTException {
    	List<MPMProcessPlan>  plans = new ArrayList<MPMProcessPlan>();
		QueryResult queryResult = PersistenceHelper.manager.navigate(doc,
				MPMDocumentDescribeLink.ROLE_AOBJECT_ROLE, MPMDocumentDescribeLink.class);
		queryResult = new LatestConfigSpec().process(queryResult);
		while (queryResult.hasMoreElements()) {
			MPMProcessPlan plan= (MPMProcessPlan) queryResult.nextElement();
			plans.add(plan);
		}
		return plans;
	}
    
	public static void main(String[] args) throws WTException {
		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		methodServer.setUserName("wcadmin");
		methodServer.setPassword("wcadmin");
		MPMProcessPlan mpmProcessPlan = (MPMProcessPlan) Util.getObjectByOid(MPMProcessPlan.class, "1469281");
		QueryResult queryResult = PersistenceHelper.manager.navigate(mpmProcessPlan,
				MPMDocumentDescribeLink.ROLE_BOBJECT_ROLE, MPMDocumentDescribeLink.class, false);
		while (queryResult.hasMoreElements()) {
			System.out.println(queryResult.nextElement());
		}
	}
	
	public static MPMProcessPlan getMPMProcessPlanByOid(long oid) throws WTException {
        QuerySpec qs = new QuerySpec(MPMProcessPlan.class);
        int[] index = {0};
        SearchCondition sc = new SearchCondition(MPMProcessPlan.class,
                "thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL,
                oid);
        qs.appendWhere(sc, index);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        MPMProcessPlan processPlan = null;
        if (qr.hasMoreElements()) {
        	processPlan = (MPMProcessPlan) qr.nextElement();
        }
        return processPlan;
    }

	public static List<org.dom4j.Element> getAllZhuZhiTechnicsElementByPart(String oid, String userName) {
		List<org.dom4j.Element> list = new ArrayList<org.dom4j.Element>();
		try {
			Persistable per = WCUtil.getPersistable("OR:wt.part.WTPart:" + oid);
			if (per instanceof WTPart) {
				WTPart rootPart = (WTPart) per;
				List<WTPart> allPart = new ArrayList<WTPart>();
				allPart.add(rootPart);
				DownloadTechnicsReportUtil.getAllChildPart(rootPart, allPart);
				List<org.dom4j.Element> techList = null;
				String mtype = "";
				for (WTPart part : allPart) {
					mtype = IBAHelper.getIBAValue(part, "MTYPE");
					if (!"自制件".equals(mtype) && !"带料委外件".equals(mtype)) {
						continue;
					}
					techList = BomUtil.getZhuZhiTechnicsDocumentByPart(part, userName);
					list.addAll(techList);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<org.dom4j.Element> getAllInWorkTechnicsElementByPart(String oid, String userName, String signedType) {
		List<org.dom4j.Element> list = new ArrayList<org.dom4j.Element>();
		try {
			Persistable per = WCUtil.getPersistable("OR:wt.part.WTPart:" + oid);
			if (per instanceof WTPart) {
				WTPart rootPart = (WTPart) per;
				List<WTPart> allPart = new ArrayList<WTPart>();
				allPart.add(rootPart);
				DownloadTechnicsReportUtil.getAllChildPart(rootPart, allPart);
				List<org.dom4j.Element> techList = null;
				String mtype = "";
				for (WTPart part : allPart) {
					mtype = IBAHelper.getIBAValue(part, "MTYPE");
					if (!"自制件".equals(mtype) && !"带料委外件".equals(mtype)) {
						continue;
					}
					techList = BomUtil.getInWorkTechnicsDocumentByPart(part, userName, signedType);
					list.addAll(techList);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch(RemoteException e) {
            e.printStackTrace();
        }
        return list;
	}
}
