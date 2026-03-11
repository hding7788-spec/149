package com.glaway.mpm.util;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wt.enterprise.RevisionControlled;
import wt.fc.IdentityHelper;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.PersistInfo;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTHashSet;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.BooleanValue;
import wt.iba.value.StringValue;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.part.WTPartUsageLink;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.StructHelper;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfBlock;
import wt.workflow.engine.WfContainer;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

import com.glaway.mpm.constants.Constants;
import com.glaway.mpm.constants.TypeNameConstants;
import com.glaway.mpm.tool.CreateCommentDataTool_New;
import com.ptc.core.lwc.server.LWCColumnAllocation;
import com.ptc.core.lwc.server.LWCFlexAttDefinition;
import com.ptc.core.lwc.server._LWCAttributeDefinition;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMConsumableResource;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationToConsumableLink;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import com.ptc.windchill.mpml.processplan.sequence.MPMSequenceUsageLink;
import com.ptc.windchill.mpml.resource.MPMOperationAssignableResource;
import com.ptc.windchill.mpml.resource.MPMPlant;
import com.ptc.windchill.mpml.resource.MPMPlantMaster;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;
import com.ptc.windchill.mpml.resource.MPMProcessMaterialMaster;
import com.ptc.windchill.mpml.resource.MPMSkill;
import com.ptc.windchill.mpml.resource.MPMSkillMaster;
import com.ptc.windchill.mpml.resource.MPMTooling;
import com.ptc.windchill.mpml.resource.MPMToolingMaster;
import com.ptc.windchill.mpml.resource.MPMWorkCenter;
import com.ptc.windchill.mpml.resource.MPMWorkCenterMaster;

import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.util.SopUtil;

public class MPMResourceUtil implements RemoteAccess {
	private static int index[] = { 0 };
	private static final String CLASSNAME = MPMResourceUtil.class.getName();

	public static void main(String[] args) {
		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		methodServer.setUserName("wcadmin");
		methodServer.setPassword("wcadmin");
		try {
			methodServer.invoke("getChildPart", CLASSNAME, null, new Class[] { WTPart.class }, new Object[] { (WTPart) Util.getObjectByOid(MPMPlant.class, "918864") });
			// getChildPart((WTPart) Util.getObjectByOid(MPMPlant.class,
			// "918864"));
		} catch (WTException e1) {
			e1.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		try {
			QueryResult queryResult = getAllPlant();
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 新建制造单位
	 *
	 * @author qianlong
	 * @date 2012-12-13
	 * @param number
	 * @param name
	 * @param container
	 * @param folderPath
	 * @param objectType
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @throws RemoteException
	 *
	 */
	public static MPMPlant createPlant(String number, String name, WTContainer container, String folderPath, String objectType, String description) throws WTPropertyVetoException, WTException,
			RemoteException {
		GLLogger.debug(CLASSNAME, "number:" + number + ":name:" + name + ":container:" + container + ":folderPath:" + folderPath + ":objectType:" + objectType);
		MPMPlantMaster master = MPMResourceUtil.getMPMPlantMasterByNumber(number);
		if (master != null) {
			GLLogger.debug(CLASSNAME, "createPlant()  number:" + number + " is exsited!");
			// QueryResult qResult =
			// VersionControlHelper.service.allVersionsOf(master);
			// return (MPMPlant)qResult.nextElement();
			throw new WTException("存在相同编号的工艺资源，请重新指定编号创建！");
		}
		MPMPlant plant = MPMPlant.newMPMPlant();
		plant.setDescription(description);
		setNumNameContainer(plant, number, name, container);
		setFolder(plant, folderPath, container);
		setSecret(plant); // 设置密级 add by liangbo 20171012
		TypeUtil.setType(plant, objectType);
		PersistenceHelper.manager.save(plant);
		return plant;
	}

	/**
	 * 新建工位
	 *
	 * @author qianlong
	 * @date 2012-12-13
	 * @param number
	 * @param name
	 * @param container
	 * @param folderPath
	 * @param objectType
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @throws RemoteException
	 *
	 */
	public static MPMWorkCenter createWorkSpace(String number, String name, WTContainer container, String folderPath, String objectType, String description) throws WTPropertyVetoException,
			WTException, RemoteException {
		GLLogger.debug(CLASSNAME, "number:" + number + ":name:" + name + ":container:" + container + ":folderPath:" + folderPath + ":objectType:" + objectType);
		MPMWorkCenterMaster master = MPMResourceUtil.getMPMWorkCenterMasterByNumber(number);
		if (master != null) {
			GLLogger.debug(CLASSNAME, "createWorkSpace() number:" + number + " is exsited!");
			// QueryResult qResult =
			// VersionControlHelper.service.allVersionsOf(master);
			// return (MPMWorkCenter)qResult.nextElement();
			throw new WTException("存在相同编号的工艺资源，请重新指定编号创建！");
		}
		MPMWorkCenter workSpace = MPMWorkCenter.newMPMWorkCenter();
		workSpace.setDescription(description);
		setNumNameContainer(workSpace, number, name, container);
		setFolder(workSpace, folderPath, container);
		setSecret(workSpace); // 设置密级 add by liangbo 20171012
		TypeUtil.setType(workSpace, objectType);
		PersistenceHelper.manager.save(workSpace);
		return workSpace;

	}

	/**
	 * 新建工种
	 *
	 * @author qianlong
	 * @date 2012-12-13
	 * @param number
	 * @param name
	 * @param container
	 * @param folderPath
	 * @param objectType
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @throws RemoteException
	 *
	 */
	public static MPMSkill createSkill(String number, String name, WTContainer container, String folderPath, String objectType, String description) throws WTPropertyVetoException, WTException,
			RemoteException {
		GLLogger.debug(CLASSNAME, "number:" + number + ":name:" + name + ":container:" + container + ":folderPath:" + folderPath + ":objectType:" + objectType);
		MPMSkill mpmSkill = MPMResourceUtil.getMPMSkill(number);
		if (mpmSkill != null) {
			GLLogger.debug(CLASSNAME, "createSkill() number:" + number + " is exsited!");
			// return mpmSkill;
			throw new WTException("存在相同编号的工艺资源，请重新指定编号创建！");
		}
		MPMSkill skill = MPMSkill.newMPMSkill();
		skill.setDescription(description);
		setNumNameContainer(skill, number, name, container);
		setFolder(skill, folderPath, container);
		setSecret(skill); // 设置密级 add by liangbo 20171012
		TypeUtil.setType(skill, objectType);
		PersistenceHelper.manager.save(skill);
		return skill;
	}

	/**
	 * 新建工艺辅料
	 *
	 * @author qianlong
	 * @date 2012-12-13
	 * @param number
	 * @param name
	 * @param container
	 * @param folderPath
	 * @param objectType
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @throws RemoteException
	 *
	 */
	public static MPMProcessMaterial createProcessMaterial(String number, String name, WTContainer container, String folderPath, String objectType, String description) throws WTPropertyVetoException,
			WTException, RemoteException {
		GLLogger.debug(CLASSNAME, "number:" + number + ":name:" + name + ":container:" + container + ":folderPath:" + folderPath + ":objectType:" + objectType);
		MPMProcessMaterial processMaterial = MPMResourceUtil.getMPMProcessMaterialByNumber(number);
		if (processMaterial == null) {
			processMaterial = MPMProcessMaterial.newMPMProcessMaterial();
			processMaterial.setDescription(description);
			setNumNameContainer(processMaterial, number, name, container);
			setFolder(processMaterial, folderPath, container);
			setSecret(processMaterial); // 设置密级 add by liangbo 20171012
			TypeUtil.setType(processMaterial, objectType);
			PersistenceHelper.manager.save(processMaterial);
		} else {
			throw new WTException("存在相同编号的工艺资源，请重新指定编号创建！");
		}
		return processMaterial;
	}

	/**
	 * 新建工具，设备，刀具，量具，工装，工艺常用语，工序名称
	 *
	 * @author qianlong
	 * @date 2012-12-13
	 * @param number
	 * @param name
	 * @param container
	 * @param folderPath
	 * @param objectType
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 *
	 */
	public static MPMTooling createTooling(String number, String name, WTContainer container, String folderPath, String objectType, String description) throws WTException, WTPropertyVetoException,
			RemoteException {
		GLLogger.debug(CLASSNAME, "number:" + number + ":name:" + name + ":container:" + container + ":folderPath:" + folderPath + ":objectType:" + objectType);
		MPMToolingMaster master = MPMResourceUtil.getMPMToolingMasterByNumber(number.toUpperCase());
		if (master != null) {
			GLLogger.debug(CLASSNAME, "createTooling() number:" + number + " is exsited!");
			// QueryResult qResult =
			// VersionControlHelper.service.allVersionsOf(master);
			// return (MPMTooling)qResult.nextElement();
			//throw new WTException("存在相同编号的工艺资源，请重新指定编号创建！");
			return null;
		}
		MPMTooling tooling = MPMTooling.newMPMTooling();
		LifeCycleState state = LifeCycleState.newLifeCycleState();
		state.setState(State.toState("APPROVED"));
		tooling.setState(state);
		// tooling.setDescription(description);
		setNumNameContainer(tooling, number, name, container);
		setFolder(tooling, folderPath, container);
		setSecret(tooling); // 设置密级 add by liangbo 20171012
		TypeUtil.setType(tooling, objectType);
		PersistenceHelper.manager.save(tooling);
		return tooling;
	}

	/**
	 * @param number
	 * @param name
	 * @param container
	 * @param folderPath
	 * @param objectType
	 * @return MPMTooling
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 *             add by chenjianhui 2019/12/09
	 */
	public static MPMTooling createSOPTooling(String number, String name, WTContainer container, String folderPath, String objectType)
			throws WTException, WTPropertyVetoException, RemoteException {
		GLLogger.debug(CLASSNAME, "number:" + number + ":name:" + name + ":container:" + container + ":folderPath:" + folderPath + ":objectType:" + objectType);
		MPMTooling tooling = MPMTooling.newMPMTooling();
		LifeCycleState state = LifeCycleState.newLifeCycleState();
		state.setState(State.toState("APPROVED"));
		tooling.setState(state);
		setNumNameContainer(tooling, number, name, container);
		setFolder(tooling, folderPath, container);
		setSecret(tooling);
		TypeUtil.setType(tooling, objectType);
		if (objectType.contains("OperationName")) {
			boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
			try {
				PersistenceHelper.manager.save(tooling);
			} finally {
				wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
			}
		}else{
			PersistenceHelper.manager.save(tooling);
		}
		return tooling;
	}

	public static MPMTooling getMPMToolingByIBAName(Map<String, String> ibaMap, String objectType) throws WTException, RemoteException, WTPropertyVetoException {
		QuerySpec qs = new QuerySpec(MPMTooling.class);
		qs.setAdvancedQueryEnabled(true);
		TypeUtil.getTypeQuery(MPMTooling.class, objectType, qs);
		ClassAttribute caId = new ClassAttribute(MPMTooling.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		for (String ibaName : ibaMap.keySet()) {
			qs.appendAnd();
			qs.appendOpenParen();
			SubSelectExpression subSelectExpression = getStringIBAQuery(ibaName, ibaMap.get(ibaName));
			qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
			qs.appendCloseParen();
		}
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		MPMTooling tooling = null;
		if (qr.hasMoreElements()) {
			tooling = (MPMTooling) qr.nextElement();
		}
		return tooling;

	}

	public static MPMTooling onlyCreateTooling(String number, String name, WTContainer container, String folderPath, String objectType, String description) throws WTException,
			WTPropertyVetoException, RemoteException {

		GLLogger.debug(CLASSNAME, "number:" + number + ":name:" + name + ":container:" + container + ":folderPath:" + folderPath + ":objectType:" + objectType);
		MPMTooling tooling = MPMTooling.newMPMTooling();
		// tooling.setDescription(description);
		setNumNameContainer(tooling, number, name, container);
		setFolder(tooling, folderPath, container);
		TypeUtil.setType(tooling, objectType);
		PersistenceHelper.manager.save(tooling);
		return tooling;
	}

	/**
	 * 工装资源和使用部门之间建立关联关系
	 *
	 * @author LongXiuChuan
	 * @date 2013-9-4
	 * @param plantString
	 * @param tooling
	 * @throws WTException
	 *
	 */
	public static void createUsingLinkWithPlant(String plantString, WTPartMaster master) throws WTException {
		if (plantString != null && !"".equals(plantString) && plantString.length() > 0) {
			if (plantString.contains(";")) {
				String plants[] = plantString.split(";");
				for (String plant : plants) {
					MPMPlant mpmPlant = CreateCommentDataTool_New.getPlantByName(plant);
					if (mpmPlant != null) {
						CreateCommentDataTool_New.createWTPartUsageLink(mpmPlant, master);
					}
				}
			} else {
				MPMPlant mpmPlant = CreateCommentDataTool_New.getPlantByName(plantString);
				if (mpmPlant != null) {
					CreateCommentDataTool_New.createWTPartUsageLink(mpmPlant, master);
				}
			}

		}
	}

	/**
	 * 设置编号、名称、和Container
	 *
	 * @author qianlong
	 * @date 2012-12-12
	 * @param number
	 * @param name
	 * @param container
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 *
	 */
	public static void setNumNameContainer(WTPart part, String number, String name, WTContainer container) throws WTPropertyVetoException, WTException {
		if (number != null && !"".equals(number)) {
			part.setNumber(number);
		}
		if (name != null && !"".equals(name)) {
			part.setName(name);
		}
		if (container != null) {
			part.setContainer(container);
		}
	}

	/**
	 * 设置密级
	 *
	 * @param part
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 */
	public static void setSecret(WTPart part) throws WTPropertyVetoException, WTException, RemoteException {
		IBAHelper helper = new IBAHelper(part);
		helper.setIBAValue("SECRET", "公开");
		helper.updateAttributeContainer(part);
	}

	/**
	 * 给对象设置文件夹
	 *
	 * @author qianlong
	 * @date 2012-12-13
	 * @param part
	 * @param folderPath
	 * @param container
	 * @throws WTException
	 *
	 */
	@SuppressWarnings("deprecation")
	public static void setFolder(WTPart part, String folderPath, WTContainer container) throws WTException {
		if (folderPath == null || "".equals(folderPath)) {
			return;
		}
		Folder folder = FolderUtil.getFolder(folderPath, WTContainerRef.newWTContainerRef(container));
		FolderHelper.assignFolder(part, folder);
	}

	/**
	 * 设置对象类型
	 *
	 * @author qianlong
	 * @date 2012-12-13
	 * @param part
	 * @param objectType
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 * @throws WTException
	 *
	 */
	public static void setType(WTPart part, String objectType) throws WTPropertyVetoException, RemoteException, WTException {
		if (objectType == null || "".equals(objectType)) {
			return;
		}
		TypeDefinitionReference typeRef = TypedUtilityServiceHelper.service.getTypeDefinitionReference(objectType);
		part.setTypeDefinitionReference(typeRef);
	}

	/**
	 * 查询子皆零件
	 *
	 * @author qianlong
	 * @date 2013-4-8
	 * @param parentPart
	 * @param childPartMaster
	 * @return
	 * @throws WTException
	 *
	 */
	public static QueryResult getChildPart(WTPart parentPart) throws WTException {
		QuerySpec qs = new QuerySpec(WTPart.class);
		qs.setAdvancedQueryEnabled(true);
		getExcludeLifeCycleStateQuery(WTPart.class, Constants.YZF, qs);
		qs.appendAnd();
		qs.appendOpenParen();
		SubSelectExpression subSelectExpression = getChildPartMasterQuery(parentPart);
		ClassAttribute masterId = new ClassAttribute(WTPart.class, "masterReference.key.id");
		qs.appendWhere(new SearchCondition(masterId, SearchCondition.IN, subSelectExpression), index);
		qs.appendCloseParen();
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		return qr;
	}

	/**
	 * 查询子皆零件的master的子查询语句
	 *
	 * @author qianlong
	 * @throws QueryException
	 * @date 2013-4-8
	 *
	 */
	private static SubSelectExpression getChildPartMasterQuery(WTPart parentPart) throws QueryException {
		QuerySpec qs = new QuerySpec();
		qs.appendClassList(WTPartUsageLink.class, false);
		qs.appendSelect(new ClassAttribute(WTPartUsageLink.class, "roleBObjectRef.key.id"), index, false);
		qs.appendWhere(new SearchCondition(WTPartUsageLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, Util.getLongOid(parentPart)), index);

		return new SubSelectExpression(qs);
	}

	/**
	 *
	 *
	 * @author qianlong
	 * @date 2012-11-12
	 * @param number
	 * @param name
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 *
	 */

	public static List<MPMTooling> getMPMToolingByLike(String number, String name, String typeName) throws WTException, RemoteException, WTPropertyVetoException {
		List<MPMTooling> list = new ArrayList<MPMTooling>();
		List<TypeIdentifier> identifierList = new ArrayList<TypeIdentifier>();
		TypeIdentifier identifier = TypedUtility.getTypeIdentifier(typeName);
		TypeUtil.getAllLastType(identifier, identifierList);
		for (TypeIdentifier typeIdentifier : identifierList) {
			QuerySpec querySpec = new QuerySpec(MPMTooling.class);
			querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NUMBER, SearchCondition.LIKE, number, false), index);
			querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NAME, SearchCondition.LIKE, name, false), index);
			querySpec.appendAnd();
			TypeUtil.getTypeQuery(MPMTooling.class, typeIdentifier.getTypename(), querySpec);
			querySpec.appendAnd();
			getExcludeLifeCycleStateQuery(MPMTooling.class, Constants.YZF, querySpec);
			QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
			queryResult = new LatestConfigSpec().process(queryResult);
			while (queryResult.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) queryResult.nextElement();
				list.add(tooling);
			}
		}

		return list;
	}

	public static List<MPMTooling> getMeasures(String number, String name, Map<String, String> map, String typeName) throws WTException, RemoteException, WTPropertyVetoException {
		List<MPMTooling> list = new ArrayList<MPMTooling>();
		ClassAttribute caId = new ClassAttribute(MPMTooling.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		List<TypeIdentifier> identifierList = new ArrayList<TypeIdentifier>();
		TypeIdentifier identifier = TypedUtility.getTypeIdentifier(typeName);
		TypeUtil.getAllLastType(identifier, identifierList);
		for (TypeIdentifier typeIdentifier : identifierList) {
			QuerySpec querySpec = new QuerySpec(MPMTooling.class);
			querySpec.setAdvancedQueryEnabled(true);
			querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NUMBER, SearchCondition.LIKE, number, false), index);
			querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NAME, SearchCondition.LIKE, name, false), index);
			querySpec.appendAnd();
			TypeUtil.getTypeQuery(MPMTooling.class, typeIdentifier.getTypename(), querySpec);
			querySpec.appendAnd();
			getExcludeLifeCycleStateQuery(MPMTooling.class, Constants.YZF, querySpec);

			// 通过对象的软属性查询--arithmeticProgrammeLanguage
			for (String ibaName : map.keySet()) {
				querySpec.appendAnd();
				querySpec.appendOpenParen();
				SubSelectExpression subSelectExpression = getStringIBAQuery(ibaName, Util.formatSearchString(map.get(ibaName)));
				querySpec.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
				querySpec.appendCloseParen();
			}

			QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
			queryResult = new LatestConfigSpec().process(queryResult);
			while (queryResult.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) queryResult.nextElement();
				list.add(tooling);
			}
		}

		return list;
	}

	/**
	 * 获取工位维护信息 add by zhuhao 2017.10.17
	 */
	public static List<MPMTooling> getWorkplace(String number, String name, String typeName) throws WTException, RemoteException, WTPropertyVetoException {
		List<MPMTooling> list = new ArrayList<MPMTooling>();
		ClassAttribute caId = new ClassAttribute(MPMTooling.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		List<TypeIdentifier> identifierList = new ArrayList<TypeIdentifier>();
		TypeIdentifier identifier = TypedUtility.getTypeIdentifier(typeName);
		TypeUtil.getAllLastType(identifier, identifierList);
		for (TypeIdentifier typeIdentifier : identifierList) {
			QuerySpec querySpec = new QuerySpec(MPMTooling.class);
			querySpec.setAdvancedQueryEnabled(true);
			querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NUMBER, SearchCondition.LIKE, number, false), index);
			querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NAME, SearchCondition.LIKE, name, false), index);
			querySpec.appendAnd();
			TypeUtil.getTypeQuery(MPMTooling.class, typeIdentifier.getTypename(), querySpec);
			querySpec.appendAnd();
			getExcludeLifeCycleStateQuery(MPMTooling.class, Constants.YZF, querySpec);

			QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
			queryResult = new LatestConfigSpec().process(queryResult);
			while (queryResult.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) queryResult.nextElement();
				list.add(tooling);
			}
		}

		return list;
	}

	public static List<MPMTooling> getMPMToolingByLike(String number, String name, String mindex, String typeName) throws WTException, RemoteException, WTPropertyVetoException {
		List<MPMTooling> list = new ArrayList<MPMTooling>();
		List<TypeIdentifier> identifierList = new ArrayList<TypeIdentifier>();
		TypeIdentifier identifier = TypedUtility.getTypeIdentifier(typeName);
		TypeUtil.getAllLastType(identifier, identifierList);
		for (TypeIdentifier typeIdentifier : identifierList) {
			QuerySpec querySpec = new QuerySpec(MPMTooling.class);
			querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NUMBER, SearchCondition.LIKE, number, false), index);
			querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NAME, SearchCondition.LIKE, name, false), index);
			querySpec.appendAnd();
			TypeUtil.getTypeQuery(MPMTooling.class, typeIdentifier.getTypename(), querySpec);
			querySpec.appendAnd();
			getExcludeLifeCycleStateQuery(MPMTooling.class, Constants.YZF, querySpec);
			QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
			queryResult = new LatestConfigSpec().process(queryResult);
			while (queryResult.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) queryResult.nextElement();
				list.add(tooling);
			}
		}

		return list;
	}

	public static List<MPMTooling> getAllKnifes(String typeName) throws WTPropertyVetoException, WTException, RemoteException {
		List<MPMTooling> list = new ArrayList<MPMTooling>();
		List<TypeIdentifier> identifierList = new ArrayList<TypeIdentifier>();
		TypeIdentifier identifier = TypedUtility.getTypeIdentifier(typeName);
		TypeUtil.getAllLastType(identifier, identifierList);
		for (TypeIdentifier typeIdentifier : identifierList) {
			QuerySpec querySpec = new QuerySpec(MPMTooling.class);
			TypeUtil.getTypeQuery(MPMTooling.class, typeIdentifier.getTypename(), querySpec);
			QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
			queryResult = new LatestConfigSpec().process(queryResult);
			while (queryResult.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) queryResult.nextElement();
				list.add(tooling);
			}
		}
		return list;
	}

	public static List<MPMTooling> getAllUnSDashboards(String typeName) throws WTPropertyVetoException, WTException, RemoteException {
		List<MPMTooling> list = new ArrayList<MPMTooling>();
		List<TypeIdentifier> identifierList = new ArrayList<TypeIdentifier>();
		TypeIdentifier identifier = TypedUtility.getTypeIdentifier(typeName);
		TypeUtil.getAllLastType(identifier, identifierList);
		for (TypeIdentifier typeIdentifier : identifierList) {
			QuerySpec querySpec = new QuerySpec(MPMTooling.class);
			TypeUtil.getTypeQuery(MPMTooling.class, typeIdentifier.getTypename(), querySpec);
			QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
			queryResult = new LatestConfigSpec().process(queryResult);
			while (queryResult.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) queryResult.nextElement();
				list.add(tooling);
			}
		}
		return list;
	}

	public static List<MPMTooling> getAllSDashboards(String typeName) throws WTPropertyVetoException, WTException, RemoteException {
		List<MPMTooling> list = new ArrayList<MPMTooling>();
		List<TypeIdentifier> identifierList = new ArrayList<TypeIdentifier>();
		TypeIdentifier identifier = TypedUtility.getTypeIdentifier(typeName);
		TypeUtil.getAllLastType(identifier, identifierList);
		for (TypeIdentifier typeIdentifier : identifierList) {
			QuerySpec querySpec = new QuerySpec(MPMTooling.class);
			TypeUtil.getTypeQuery(MPMTooling.class, typeIdentifier.getTypename(), querySpec);
			QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
			queryResult = new LatestConfigSpec().process(queryResult);
			while (queryResult.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) queryResult.nextElement();
				list.add(tooling);
			}
		}
		return list;
	}

	public static List<MPMTooling> getAllMeasures(String typeName) throws WTPropertyVetoException, WTException, RemoteException {
		List<MPMTooling> list = new ArrayList<MPMTooling>();
		List<TypeIdentifier> identifierList = new ArrayList<TypeIdentifier>();
		TypeIdentifier identifier = TypedUtility.getTypeIdentifier(typeName);
		TypeUtil.getAllLastType(identifier, identifierList);
		for (TypeIdentifier typeIdentifier : identifierList) {
			QuerySpec querySpec = new QuerySpec(MPMTooling.class);
			TypeUtil.getTypeQuery(MPMTooling.class, typeIdentifier.getTypename(), querySpec);
			QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
			queryResult = new LatestConfigSpec().process(queryResult);
			while (queryResult.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) queryResult.nextElement();
				list.add(tooling);
			}
		}
		return list;
	}

	public static List<MPMTooling> getAllWorkplace(String typeName) throws WTPropertyVetoException, WTException, RemoteException {
		List<MPMTooling> list = new ArrayList<MPMTooling>();
		List<TypeIdentifier> identifierList = new ArrayList<TypeIdentifier>();
		TypeIdentifier identifier = TypedUtility.getTypeIdentifier(typeName);
		TypeUtil.getAllLastType(identifier, identifierList);
		for (TypeIdentifier typeIdentifier : identifierList) {
			QuerySpec querySpec = new QuerySpec(MPMTooling.class);
			TypeUtil.getTypeQuery(MPMTooling.class, typeIdentifier.getTypename(), querySpec);
			QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
			queryResult = new LatestConfigSpec().process(queryResult);
			while (queryResult.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) queryResult.nextElement();
				list.add(tooling);
			}
		}
		return list;
	}

	public static List<MPMTooling> getKnifes(String number, String name, Map<String, String> map, String typeName) throws WTException, RemoteException, WTPropertyVetoException {
		List<MPMTooling> list = new ArrayList<MPMTooling>();
		ClassAttribute caId = new ClassAttribute(MPMTooling.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		List<TypeIdentifier> identifierList = new ArrayList<TypeIdentifier>();
		TypeIdentifier identifier = TypedUtility.getTypeIdentifier(typeName);
		TypeUtil.getAllLastType(identifier, identifierList);
		for (TypeIdentifier typeIdentifier : identifierList) {
			QuerySpec querySpec = new QuerySpec(MPMTooling.class);
			querySpec.setAdvancedQueryEnabled(true);
			querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NUMBER, SearchCondition.LIKE, number, false), index);
			querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NAME, SearchCondition.LIKE, name, false), index);
			querySpec.appendAnd();
			TypeUtil.getTypeQuery(MPMTooling.class, typeIdentifier.getTypename(), querySpec);
			querySpec.appendAnd();
			getExcludeLifeCycleStateQuery(MPMTooling.class, Constants.YZF, querySpec);

			// 通过对象的软属性查询--arithmeticProgrammeLanguage
			for (String ibaName : map.keySet()) {
				querySpec.appendAnd();
				querySpec.appendOpenParen();
				SubSelectExpression subSelectExpression = getStringIBAQuery(ibaName, Util.formatSearchString(map.get(ibaName)));
				querySpec.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
				querySpec.appendCloseParen();
			}

			QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
			queryResult = new LatestConfigSpec().process(queryResult);
			while (queryResult.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) queryResult.nextElement();
				list.add(tooling);
			}
		}

		return list;
	}

	public static List<MPMTooling> getUnSDashboards(String number, String name, Map<String, String> map, String typeName) throws WTException, RemoteException, WTPropertyVetoException {
		List<MPMTooling> list = new ArrayList<MPMTooling>();
		ClassAttribute caId = new ClassAttribute(MPMTooling.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		List<TypeIdentifier> identifierList = new ArrayList<TypeIdentifier>();
		TypeIdentifier identifier = TypedUtility.getTypeIdentifier(typeName);
		TypeUtil.getAllLastType(identifier, identifierList);
		for (TypeIdentifier typeIdentifier : identifierList) {
			QuerySpec querySpec = new QuerySpec(MPMTooling.class);
			querySpec.setAdvancedQueryEnabled(true);
			querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NUMBER, SearchCondition.LIKE, number, false), index);
			querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NAME, SearchCondition.LIKE, name, false), index);
			querySpec.appendAnd();
			TypeUtil.getTypeQuery(MPMTooling.class, typeIdentifier.getTypename(), querySpec);
			querySpec.appendAnd();
			getExcludeLifeCycleStateQuery(MPMTooling.class, Constants.YZF, querySpec);

			// 通过对象的软属性查询--arithmeticProgrammeLanguage
			for (String ibaName : map.keySet()) {
				querySpec.appendAnd();
				querySpec.appendOpenParen();
				SubSelectExpression subSelectExpression = getStringIBAQuery(ibaName, Util.formatSearchString(map.get(ibaName)));
				querySpec.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
				querySpec.appendCloseParen();
			}

			QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
			queryResult = new LatestConfigSpec().process(queryResult);
			while (queryResult.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) queryResult.nextElement();
				list.add(tooling);
			}
		}

		return list;
	}

	public static List<MPMTooling> getSDashboards(String number, String name, Map<String, String> map, String typeName) throws WTException, RemoteException, WTPropertyVetoException {
		List<MPMTooling> list = new ArrayList<MPMTooling>();
		ClassAttribute caId = new ClassAttribute(MPMTooling.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		List<TypeIdentifier> identifierList = new ArrayList<TypeIdentifier>();
		TypeIdentifier identifier = TypedUtility.getTypeIdentifier(typeName);
		TypeUtil.getAllLastType(identifier, identifierList);
		for (TypeIdentifier typeIdentifier : identifierList) {
			QuerySpec querySpec = new QuerySpec(MPMTooling.class);
			querySpec.setAdvancedQueryEnabled(true);
			querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NUMBER, SearchCondition.LIKE, number, false), index);
			querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NAME, SearchCondition.LIKE, name, false), index);
			querySpec.appendAnd();
			TypeUtil.getTypeQuery(MPMTooling.class, typeIdentifier.getTypename(), querySpec);
			querySpec.appendAnd();
			getExcludeLifeCycleStateQuery(MPMTooling.class, Constants.YZF, querySpec);

			// 通过对象的软属性查询--arithmeticProgrammeLanguage
			for (String ibaName : map.keySet()) {
				querySpec.appendAnd();
				querySpec.appendOpenParen();
				SubSelectExpression subSelectExpression = getStringIBAQuery(ibaName, Util.formatSearchString(map.get(ibaName)));
				querySpec.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
				querySpec.appendCloseParen();
			}

			QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
			queryResult = new LatestConfigSpec().process(queryResult);
			while (queryResult.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) queryResult.nextElement();
				list.add(tooling);
			}
		}

		return list;
	}

	/**
	 * 根据设备的编号与名称模糊匹配 查询设备,工装,工具,量具,刀具
	 *
	 * @author qianlong
	 * @date 2012-11-12
	 * @param number
	 * @param name
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 *
	 */

	public static List<MPMTooling> getMPMToolingByLikeNumberAndName(String number, String name, String typeName) throws WTException, RemoteException, WTPropertyVetoException {
		List<MPMTooling> list = new ArrayList<MPMTooling>();
		QuerySpec querySpec = new QuerySpec(MPMTooling.class);
		querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NUMBER, SearchCondition.LIKE, number, false), index);
		querySpec.appendAnd();
		querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NAME, SearchCondition.LIKE, name, false), index);
		// querySpec.appendAnd();

		// TypeUtil.getTypeQuery(MPMTooling.class, typeName, querySpec);

		querySpec.appendAnd();
		getExcludeLifeCycleStateQuery(MPMTooling.class, Constants.YZF, querySpec);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		while (queryResult.hasMoreElements()) {
			MPMTooling tooling = (MPMTooling) queryResult.nextElement();
			String path = tooling.getFolderPath();
			if (path.startsWith(TypeNameConstants.GZFloder) || path.startsWith(TypeNameConstants.DMSBFloder))
				list.add(tooling);
		}

		return list;
	}

	/**
	 * 根据编号模糊匹配 查询工装
	 *
	 * @author qianlong
	 * @date 2013-5-28
	 * @param number
	 * @param typeName
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static QueryResult getMPMToolingByLike(String number, String typeName) throws WTException, RemoteException, WTPropertyVetoException {
		QuerySpec querySpec = new QuerySpec(MPMTooling.class);
		querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NUMBER, SearchCondition.LIKE, number, false), index);
		querySpec.appendAnd();
		TypeUtil.getTypeQuery(MPMTooling.class, typeName, querySpec);
		querySpec.appendAnd();
		getExcludeLifeCycleStateQuery(MPMTooling.class, Constants.YZF, querySpec);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);

		return queryResult;
	}

	/**
	 * 根据编号模糊匹配 查询工装
	 *
	 * @author qianlong
	 * @date 2013-5-28
	 * @param number
	 * @param typeName
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static QueryResult getMPMToolingByLike(String number, long typeBranchId) throws WTException, RemoteException, WTPropertyVetoException {
		QuerySpec querySpec = new QuerySpec(MPMTooling.class);
		querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NUMBER, SearchCondition.LIKE, number, false), index);
		querySpec.appendAnd();
		querySpec.appendWhere(new SearchCondition(MPMTooling.class, "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeBranchId), index);
		querySpec.appendAnd();
		getExcludeLifeCycleStateQuery(MPMTooling.class, Constants.YZF, querySpec);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);

		return queryResult;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2013-5-29
	 * @param name
	 * @param typeName
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static MPMTooling getMPMToolingByNumber(String number, String typeName) throws WTException, RemoteException, WTPropertyVetoException {
		MPMTooling tooling = null;
		QuerySpec querySpec = new QuerySpec(MPMTooling.class);
		querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NUMBER, SearchCondition.EQUAL, number, true), index);
		querySpec.appendAnd();
		TypeUtil.getTypeQuery(MPMTooling.class, typeName, querySpec);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		while (queryResult.hasMoreElements()) {
			tooling = (MPMTooling) queryResult.nextElement();
		}
		return tooling;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2013-5-29
	 * @param name
	 * @param typeName
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static MPMTooling getMPMToolingByName(String name, String typeName) throws WTException, RemoteException, WTPropertyVetoException {
		MPMTooling tooling = null;
		QuerySpec querySpec = new QuerySpec(MPMTooling.class);
		querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NAME, SearchCondition.EQUAL, name, true), index);
		querySpec.appendAnd();
		TypeUtil.getTypeQuery(MPMTooling.class, typeName, querySpec);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		while (queryResult.hasMoreElements()) {
			tooling = (MPMTooling) queryResult.nextElement();
		}

		return tooling;
	}

	/**
	 * 根据类型和IBA查询对象
	 *
	 * @author qianlong
	 * @date 2012-12-13
	 * @param name
	 * @param ibaName
	 * @param ibaValue
	 * @param container
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static QueryResult getMPMToolingByIBAType(Map<String, Boolean> ibaMap, long typeBranchId) throws WTException, RemoteException, WTPropertyVetoException {

		QuerySpec querySpec = new QuerySpec(MPMTooling.class);
		querySpec.setAdvancedQueryEnabled(true);
		ClassAttribute caId = new ClassAttribute(MPMTooling.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		querySpec.appendWhere(new SearchCondition(MPMTooling.class, "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeBranchId), index);
		querySpec.appendAnd();
		getExcludeLifeCycleStateQuery(MPMTooling.class, Constants.YZF, querySpec);
		// 通过对象的软属性查询
		for (String ibaName : ibaMap.keySet()) {

			querySpec.appendAnd();
			querySpec.appendOpenParen();
			SubSelectExpression subSelectExpression = getBooleanIBAQuery(ibaName, ibaMap.get(ibaName));
			querySpec.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
			querySpec.appendCloseParen();
		}
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);

		return queryResult;
	}

	/**
	 * 通过类型 获取MPMTooling
	 *
	 * @author qianlong
	 * @date 2012-12-14
	 * @param number
	 * @param name
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 *
	 */
	public static QueryResult getMPMToolingByType(String objectType) throws WTException, RemoteException {

		QuerySpec querySpec = new QuerySpec(MPMTooling.class);

		TypeUtil.getTypeQuery(MPMTooling.class, objectType, querySpec);
		querySpec.appendAnd();
		getExcludeLifeCycleStateQuery(MPMTooling.class, Constants.YZF, querySpec);
		if(objectType==TypeNameConstants.GXMC){
			querySpec.setAdvancedQueryEnabled(true);
			querySpec.appendAnd();
			querySpec.appendOpenParen();
			ClassAttribute caId = new ClassAttribute(MPMTooling.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
			String ibaName = "SpecializedType";
			AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
			if (addv == null)
				throw new IBADefinitionException("No IBA Definition: " + ibaName);
			long ibaDefId = addv.getObjectID().getId();
			QuerySpec qs1 = new QuerySpec();
			int idx = qs1.appendClassList(StringValue.class, false);
			qs1.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx }, false);
			qs1.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId), new int[] { idx });
			qs1.appendAnd();
			qs1.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.NOT_NULL, false), new int[] { idx });
			SubSelectExpression subSelectExpression = new SubSelectExpression(qs1);
			querySpec.appendWhere(new SearchCondition(caId, SearchCondition.NOT_IN, subSelectExpression), index);
			querySpec.appendCloseParen();
		}
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		return queryResult;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-10-18
	 * @param number
	 * @return
	 * @throws WTException
	 * @return MPMToolingMaster
	 *
	 */
	public static MPMToolingMaster getMPMToolingMasterByNumber(String number) throws WTException {
		if (number == null) {
			return null;
		}
		MPMToolingMaster mpmToolingMaster = null;
		QuerySpec querySpec = new QuerySpec(MPMToolingMaster.class);
		querySpec.appendWhere(new SearchCondition(MPMToolingMaster.class, "number", SearchCondition.EQUAL, number), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			mpmToolingMaster = (MPMToolingMaster) queryResult.nextElement();
		}
		return mpmToolingMaster;
	}

	public static MPMToolingMaster getMPMToolingMasterByName(String name) throws WTException {
		if (name == null) {
			return null;
		}
		MPMToolingMaster mpmToolingMaster = null;
		QuerySpec querySpec = new QuerySpec(MPMToolingMaster.class);
		querySpec.appendWhere(new SearchCondition(MPMToolingMaster.class, "name", SearchCondition.EQUAL, name), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			mpmToolingMaster = (MPMToolingMaster) queryResult.nextElement();
		}
		return mpmToolingMaster;
	}

	/**
	 * 根据设备的编号与名称模糊匹配 查询工位
	 *
	 * @author qianlong
	 * @date 2012-11-12
	 * @param number
	 * @param name
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 *
	 */

	public static List<MPMWorkCenter> getWorkSpaceByLike(String number, String name) throws WTException, RemoteException, WTPropertyVetoException {
		List<MPMWorkCenter> list = new ArrayList<MPMWorkCenter>();
		QuerySpec querySpec = new QuerySpec(MPMWorkCenter.class);
		querySpec.appendWhere(new SearchCondition(MPMWorkCenter.class, WTPart.NUMBER, SearchCondition.LIKE, number, false), index);
		querySpec.appendAnd();
		querySpec.appendWhere(new SearchCondition(MPMWorkCenter.class, WTPart.NAME, SearchCondition.LIKE, name, false), index);
		querySpec.appendAnd();
		getExcludeLifeCycleStateQuery(MPMWorkCenter.class, Constants.YZF, querySpec);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		while (queryResult.hasMoreElements()) {
			MPMWorkCenter workSpace = (MPMWorkCenter) queryResult.nextElement();
			list.add(workSpace);
		}
		return list;
	}

	/**
	 * 根据设备的编号与名称模糊匹配 查询工种
	 *
	 * @author qianlong
	 * @date 2012-11-12
	 * @param number
	 * @param name
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 *
	 */

	public static List<MPMSkill> getSkillByLike(String number, String name) throws WTException, RemoteException, WTPropertyVetoException {
		List<MPMSkill> list = new ArrayList<MPMSkill>();
		QuerySpec querySpec = new QuerySpec(MPMSkill.class);
		querySpec.appendWhere(new SearchCondition(MPMSkill.class, WTPart.NUMBER, SearchCondition.LIKE, number, false), index);
		querySpec.appendAnd();
		querySpec.appendWhere(new SearchCondition(MPMSkill.class, WTPart.NAME, SearchCondition.LIKE, name, false), index);
		querySpec.appendAnd();
		getExcludeLifeCycleStateQuery(MPMSkill.class, Constants.YZF, querySpec);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		while (queryResult.hasMoreElements()) {
			MPMSkill skill = (MPMSkill) queryResult.nextElement();
			list.add(skill);
		}
		return list;
	}

	/**
	 * 查询材料 1.若是零件 根据编号,名称,WTContainer模糊查询 2.若是材料 根据牌号,名称,WTContainer模糊查询
	 *
	 * @author qianlong
	 * @date 2012-12-13
	 * @param name
	 * @param ibaName
	 * @param ibaValue
	 * @param container
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static QueryResult getProcessMaterialByIBANameConatiner(String number, String name, Map<String, String> map, WTContainer container) throws WTException, RemoteException,
			WTPropertyVetoException {
		QuerySpec querySpec = new QuerySpec(MPMProcessMaterial.class);
		querySpec.setAdvancedQueryEnabled(true);
		ClassAttribute caId = new ClassAttribute(MPMProcessMaterial.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);

		long libraryid = PersistenceHelper.getObjectIdentifier(container).getId();
		querySpec.appendWhere(new SearchCondition(MPMProcessMaterial.class, WTPart.CONTAINER_ID, SearchCondition.EQUAL, libraryid), index);
		querySpec.appendAnd();
		if (name != null) {
			querySpec.appendWhere(new SearchCondition(MPMProcessMaterial.class, WTPart.NAME, SearchCondition.LIKE, name, false), index);
			querySpec.appendAnd();
		}
		if (number != null) {
			querySpec.appendWhere(new SearchCondition(MPMProcessMaterial.class, WTPart.NUMBER, SearchCondition.LIKE, number, false), index);
			querySpec.appendAnd();
		}
		getExcludeLifeCycleStateQuery(MPMProcessMaterial.class, Constants.YZF, querySpec);
		// 通过对象的软属性查询--arithmeticProgrammeLanguage
		for (String ibaName : map.keySet()) {
			querySpec.appendAnd();
			querySpec.appendOpenParen();
			SubSelectExpression subSelectExpression = getStringIBAQuery(ibaName, Util.formatSearchString(map.get(ibaName)));
			querySpec.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
			querySpec.appendCloseParen();
		}
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);

		return queryResult;
	}

	/**
	 * 构建根据String软属性查询的子查询语句
	 *
	 * @author qianlong
	 * @date 2012-12-13
	 * @param ibaName
	 * @param ibaValue
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 *
	 */
	public static SubSelectExpression getStringIBAQuery(String ibaName, String ibaValue) throws WTException, WTPropertyVetoException, RemoteException {
		// 获取IBA属性定义
		AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
		if (addv == null)
			throw new IBADefinitionException("No IBA Definition: " + ibaName);
		long ibaDefId = addv.getObjectID().getId();
		QuerySpec qs = new QuerySpec();
		int idx = qs.appendClassList(StringValue.class, false);
		qs.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx }, false);
		qs.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId), new int[] { idx });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.LIKE, ibaValue, false), new int[] { idx });
		return new SubSelectExpression(qs);
	}

	/**
	 * 构建根据Boolean软属性查询的子查询语句
	 *
	 * @author qianlong
	 * @date 2012-12-13
	 * @param ibaName
	 * @param ibaValue
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 *
	 */
	public static SubSelectExpression getBooleanIBAQuery(String ibaName, Boolean ibaValue) throws WTException, WTPropertyVetoException, RemoteException {

		// 获取IBA属性定义
		AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
		if (addv == null)
			throw new IBADefinitionException("No IBA Definition: " + ibaName);
		long ibaDefId = addv.getObjectID().getId();
		QuerySpec qs = new QuerySpec();
		int idx = qs.appendClassList(BooleanValue.class, false);
		qs.appendSelect(new ClassAttribute(BooleanValue.class, "theIBAHolderReference.key.id"), new int[] { idx }, false);
		qs.appendWhere(new SearchCondition(BooleanValue.class, "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId), new int[] { idx });
		qs.appendAnd();
		if (ibaValue) {
			qs.appendWhere(new SearchCondition(BooleanValue.class, BooleanValue.VALUE, SearchCondition.IS_TRUE, false), new int[] { idx });
		} else {
			qs.appendWhere(new SearchCondition(BooleanValue.class, BooleanValue.VALUE, SearchCondition.IS_FALSE, false), new int[] { idx });

		}
		return new SubSelectExpression(qs);
	}

	/**
	 * 根据材料类型获取所有的材料
	 *
	 * @author qianlong
	 * @throws WTException
	 * @throws RemoteException
	 * @date 2012-12-18
	 *
	 */
	public static QueryResult getProcessMaterialsByType(String typeName) throws RemoteException, WTException {
		QuerySpec querySpec = new QuerySpec(MPMProcessMaterial.class);
		TypeUtil.getTypeQuery(MPMProcessMaterial.class, typeName, querySpec);
		querySpec.appendAnd();
		getExcludeLifeCycleStateQuery(MPMProcessMaterial.class, Constants.YZF, querySpec);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		return queryResult;
	}

	/**
	 * 获取所有的制造单位
	 *
	 * @author qianlong
	 * @date 2012-11-10
	 * @return
	 * @throws WTException
	 *
	 */
	public static QueryResult getAllPlant() throws WTException {
		QuerySpec querySpec = new QuerySpec(MPMPlant.class);
		querySpec.appendWhere(new SearchCondition(MPMPlant.class, WTPart.NUMBER, SearchCondition.NOT_NULL, true), index);
		querySpec.appendAnd();
		getExcludeLifeCycleStateQuery(MPMPlant.class, Constants.YZF, querySpec);
		querySpec.appendAnd();
		WTContainer container = WTContainerUtil.getContainerByName("工艺资源库");
		Folder folder = FolderUtil.getFolder("/Default/制造单位", WTContainerRef.newWTContainerRef(container));
		querySpec.appendWhere(new SearchCondition(MPMPlant.class, "folderingInfo.parentFolder.key.id", SearchCondition.EQUAL, folder.getPersistInfo().getObjectIdentifier().getId()), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);

		return queryResult;
	}

	/**
	 * 添加过滤生命周期状态的查询
	 *
	 * @author qianlong
	 * @throws QueryException
	 * @date 2012-12-26
	 *
	 */
	public static void getExcludeLifeCycleStateQuery(Class objectClass, String state, QuerySpec querySpec) throws QueryException {
		querySpec.appendWhere(new SearchCondition(objectClass, "state.state", SearchCondition.NOT_EQUAL, state), index);
	}

	// --------------------------------------------------------old-------------------------------------------------------

	/**
	 *
	 * @author qianlong
	 * @date 2012-10-23
	 * @param number
	 * @return
	 * @throws WTException
	 *
	 */
	public static MPMPlantMaster getMPMPlantMasterByNumber(String number) throws WTException {
		MPMPlantMaster mpmPlantMaster = null;

		QuerySpec querySpec = new QuerySpec(MPMPlantMaster.class);
		querySpec.appendWhere(new SearchCondition(MPMPlantMaster.class, "number", SearchCondition.EQUAL, number), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			mpmPlantMaster = (MPMPlantMaster) queryResult.nextElement();
		}
		return mpmPlantMaster;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-10-24
	 * @param number
	 * @param name
	 * @return
	 * @throws WTException
	 *
	 */
	public static MPMPlantMaster getMPMPlantMaster(String number, String name) throws WTException {
		MPMPlantMaster mpmPlantMaster = null;

		QuerySpec querySpec = new QuerySpec(MPMPlantMaster.class);
		querySpec.appendWhere(new SearchCondition(MPMPlantMaster.class, "number", SearchCondition.EQUAL, number), index);
		querySpec.appendAnd();
		querySpec.appendWhere(new SearchCondition(MPMPlantMaster.class, "name", SearchCondition.EQUAL, name), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			mpmPlantMaster = (MPMPlantMaster) queryResult.nextElement();
		}
		return mpmPlantMaster;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-10-24
	 * @param name
	 * @return
	 * @throws WTException
	 *
	 */
	public static MPMPlantMaster getMPMPlantMasterByName(String name) throws WTException {
		MPMPlantMaster mpmPlantMaster = null;
		QuerySpec querySpec = new QuerySpec(MPMPlantMaster.class);
		querySpec.appendWhere(new SearchCondition(MPMPlantMaster.class, "name", SearchCondition.EQUAL, name), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			mpmPlantMaster = (MPMPlantMaster) queryResult.nextElement();
		}
		return mpmPlantMaster;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-11-3
	 * @return
	 * @throws WTException
	 *
	 */
	public static List<MPMProcessMaterial> getAllMPMProcessMaterial() throws WTException {
		List<MPMProcessMaterial> processMaterialList = new ArrayList<MPMProcessMaterial>();

		QuerySpec querySpec = new QuerySpec(MPMProcessMaterial.class);
		querySpec.appendWhere(new SearchCondition(MPMProcessMaterial.class, WTPart.NUMBER, SearchCondition.NOT_NULL, true), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		while (queryResult.hasMoreElements()) {
			processMaterialList.add((MPMProcessMaterial) queryResult.nextElement());
		}
		return processMaterialList;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-10-31
	 * @param number
	 * @return
	 * @throws WTException
	 *
	 */
	public static MPMProcessMaterial getMPMProcessMaterialByNumber(String number) throws WTException {
		MPMProcessMaterial mpmProcessMaterial = null;
		QuerySpec querySpec = new QuerySpec(MPMProcessMaterial.class);
		querySpec.appendWhere(new SearchCondition(MPMProcessMaterial.class, WTPart.NUMBER, SearchCondition.EQUAL, number), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			mpmProcessMaterial = (MPMProcessMaterial) queryResult.nextElement();
		}
		return mpmProcessMaterial;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-10-23
	 * @param number
	 * @return
	 * @throws WTException
	 * @return MPMProcessMaterialMaster
	 *
	 */
	public static MPMProcessMaterialMaster getMPMProcessMaterialMasterByNumber(String number) throws WTException {
		MPMProcessMaterialMaster mpmProcessMaterialMaster = null;
		QuerySpec querySpec = new QuerySpec(MPMProcessMaterialMaster.class);
		querySpec.appendWhere(new SearchCondition(MPMProcessMaterialMaster.class, "number", SearchCondition.EQUAL, number), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			mpmProcessMaterialMaster = (MPMProcessMaterialMaster) queryResult.nextElement();
		}
		return mpmProcessMaterialMaster;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-10-24
	 * @param number
	 * @param name
	 * @return
	 * @throws WTException
	 *
	 */
	public static MPMProcessMaterialMaster getMPMProcessMaterialMaster(String number, String name) throws WTException {
		MPMProcessMaterialMaster mpmProcessMaterialMaster = null;
		QuerySpec querySpec = new QuerySpec(MPMProcessMaterialMaster.class);
		querySpec.appendWhere(new SearchCondition(MPMProcessMaterialMaster.class, "number", SearchCondition.EQUAL, number), index);
		querySpec.appendAnd();
		querySpec.appendWhere(new SearchCondition(MPMProcessMaterialMaster.class, "name", SearchCondition.EQUAL, name), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			mpmProcessMaterialMaster = (MPMProcessMaterialMaster) queryResult.nextElement();
		}
		return mpmProcessMaterialMaster;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-10-24
	 * @param name
	 * @return
	 * @throws WTException
	 *
	 */
	public static MPMProcessMaterialMaster getMPMProcessMaterialMasterByName(String name) throws WTException {
		MPMProcessMaterialMaster mpmProcessMaterialMaster = null;
		QuerySpec querySpec = new QuerySpec(MPMProcessMaterialMaster.class);
		querySpec.appendWhere(new SearchCondition(MPMProcessMaterialMaster.class, "name", SearchCondition.EQUAL, name), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			mpmProcessMaterialMaster = (MPMProcessMaterialMaster) queryResult.nextElement();
		}
		return mpmProcessMaterialMaster;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-11-3
	 * @return
	 * @throws WTException
	 *
	 */
	public static QueryResult getAllMPMSkill() throws WTException {

		QuerySpec querySpec = new QuerySpec(MPMSkill.class);
		querySpec.appendWhere(new SearchCondition(MPMSkill.class, WTPart.NUMBER, SearchCondition.NOT_NULL, true), index);
		querySpec.appendAnd();
		getExcludeLifeCycleStateQuery(MPMSkill.class, Constants.YZF, querySpec);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);

		return queryResult;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-10-23
	 * @param number
	 * @return
	 * @throws WTException
	 * @return MPMSkillMaster
	 *
	 */
	public static MPMSkillMaster getMPMSkillMasterByNumber(String number) throws WTException {
		MPMSkillMaster mpmSkillMaster = null;
		QuerySpec querySpec = new QuerySpec(MPMSkillMaster.class);
		querySpec.appendWhere(new SearchCondition(MPMSkillMaster.class, "number", SearchCondition.EQUAL, number), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			mpmSkillMaster = (MPMSkillMaster) queryResult.nextElement();
		}
		return mpmSkillMaster;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-10-24
	 * @param number
	 * @param name
	 * @return
	 * @throws WTException
	 *
	 */
	public static MPMSkillMaster getMPMSkillMaster(String number, String name) throws WTException {
		MPMSkillMaster mpmSkillMaster = null;
		QuerySpec querySpec = new QuerySpec(MPMSkillMaster.class);
		querySpec.appendWhere(new SearchCondition(MPMSkillMaster.class, "number", SearchCondition.EQUAL, number), index);
		querySpec.appendAnd();
		querySpec.appendWhere(new SearchCondition(MPMSkillMaster.class, "name", SearchCondition.EQUAL, name), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			mpmSkillMaster = (MPMSkillMaster) queryResult.nextElement();
		}
		return mpmSkillMaster;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-10-24
	 * @param name
	 * @return
	 * @throws WTException
	 *
	 */
	public static MPMSkillMaster getMPMSkillMasterByName(String name) throws WTException {
		MPMSkillMaster mpmSkillMaster = null;
		QuerySpec querySpec = new QuerySpec(MPMSkillMaster.class);
		querySpec.appendWhere(new SearchCondition(MPMSkillMaster.class, "name", SearchCondition.EQUAL, name), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			mpmSkillMaster = (MPMSkillMaster) queryResult.nextElement();
		}
		return mpmSkillMaster;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-10-20
	 * @param number
	 * @return
	 * @return MPMSkill
	 * @throws WTException
	 *
	 */
	public static MPMSkill getMPMSkill(String number) throws WTException {
		MPMSkill skill = null;

		MPMSkillMaster skillMaster = getMPMSkillMasterByNumber(number);
		if (skillMaster != null) {
			QueryResult qr = VersionControlHelper.service.allVersionsOf(skillMaster);
			qr = new LatestConfigSpec().process(qr);
			while (qr.hasMoreElements()) {
				skill = (MPMSkill) qr.nextElement();
			}
		}

		return skill;
	}

	public static MPMSkill getMPMSkillByName(String name) throws WTException {
		MPMSkill skill = null;
		MPMSkillMaster skillMaster = getMPMSkillMasterByName(name);
		if (skillMaster != null) {
			QueryResult qr = VersionControlHelper.service.allVersionsOf(skillMaster);
			qr = new LatestConfigSpec().process(qr);
			while (qr.hasMoreElements()) {
				skill = (MPMSkill) qr.nextElement();
			}
		}
		return skill;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-11-3
	 * @return
	 * @throws WTException
	 *
	 */
	public static List<MPMWorkCenter> getAllMPMWorkCenter() throws WTException {
		List<MPMWorkCenter> workCenterList = new ArrayList<MPMWorkCenter>();

		QuerySpec querySpec = new QuerySpec(MPMWorkCenter.class);
		querySpec.appendWhere(new SearchCondition(MPMWorkCenter.class, WTPart.NUMBER, SearchCondition.NOT_NULL, true), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		while (queryResult.hasMoreElements()) {
			workCenterList.add((MPMWorkCenter) queryResult.nextElement());
		}
		return workCenterList;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-10-18
	 * @param number
	 * @return
	 * @throws WTException
	 * @return MPMWorkCenterMaster
	 *
	 */
	public static MPMWorkCenterMaster getMPMWorkCenterMasterByNumber(String number) throws WTException {
		MPMWorkCenterMaster mpmWorkCenterMaster = null;

		QuerySpec querySpec = new QuerySpec(MPMWorkCenterMaster.class);
		querySpec.appendWhere(new SearchCondition(MPMWorkCenterMaster.class, "number", SearchCondition.EQUAL, number), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			mpmWorkCenterMaster = (MPMWorkCenterMaster) queryResult.nextElement();
		}
		return mpmWorkCenterMaster;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-10-24
	 * @param number
	 * @param name
	 * @return
	 * @throws WTException
	 *
	 */
	public static MPMWorkCenterMaster getMPMWorkCenterMaster(String number, String name) throws WTException {
		MPMWorkCenterMaster mpmWorkCenterMaster = null;

		QuerySpec querySpec = new QuerySpec(MPMWorkCenterMaster.class);
		querySpec.appendWhere(new SearchCondition(MPMWorkCenterMaster.class, "number", SearchCondition.EQUAL, number), index);
		querySpec.appendAnd();
		querySpec.appendWhere(new SearchCondition(MPMWorkCenterMaster.class, "name", SearchCondition.EQUAL, name), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			mpmWorkCenterMaster = (MPMWorkCenterMaster) queryResult.nextElement();
		}
		return mpmWorkCenterMaster;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-10-24
	 * @param name
	 * @return
	 * @throws WTException
	 *
	 */
	public static MPMWorkCenterMaster getMPMWorkCenterMasterByName(String name) throws WTException {
		MPMWorkCenterMaster mpmWorkCenterMaster = null;

		QuerySpec querySpec = new QuerySpec(MPMWorkCenterMaster.class);
		querySpec.appendWhere(new SearchCondition(MPMWorkCenterMaster.class, "name", SearchCondition.EQUAL, name), index);
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			mpmWorkCenterMaster = (MPMWorkCenterMaster) queryResult.nextElement();
		}
		return mpmWorkCenterMaster;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-11-9
	 * @param wtPartMaster
	 * @param number
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 *
	 */
	public static void changeWTPartMasterNumber(WTPartMaster wtPartMaster, String number) throws WTPropertyVetoException, WTException {
		WTPartMasterIdentity wtPartMasterIdentity = (WTPartMasterIdentity) wtPartMaster.getIdentificationObject();
		wtPartMasterIdentity.setNumber(number);
		wtPartMaster = (WTPartMaster) IdentityHelper.service.changeIdentity(wtPartMaster, wtPartMasterIdentity);

	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-11-9
	 * @param wtPartMaster
	 * @param name
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 *
	 */
	public static void changeWTPartMasterName(WTPartMaster wtPartMaster, String name) throws WTPropertyVetoException, WTException {
		WTPartMasterIdentity wtPartMasterIdentity = (WTPartMasterIdentity) wtPartMaster.getIdentificationObject();
		wtPartMasterIdentity.setName(name);
		wtPartMaster = (WTPartMaster) IdentityHelper.service.changeIdentity(wtPartMaster, wtPartMasterIdentity);

	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-10-22
	 * @param parentPart
	 * @param childPartMaster
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @return WTPartUsageLink
	 *
	 */

	public static WTPartUsageLink createWTPartUsageLink(WTPart parentPart, WTPartMaster childPartMaster) throws WTException {
		WTPartUsageLink partUsageLink = WTPartUsageLink.newWTPartUsageLink(parentPart, childPartMaster);
		PersistenceServerHelper.manager.insert(partUsageLink);
		return partUsageLink;

	}

	/**
	 * 根据父子皆查询他们之间的link
	 *
	 * @author qianlong
	 * @date 2012-10-21
	 * @return
	 * @throws WTException
	 * @return ConfigSpec
	 * @throws WTException
	 *
	 */

	@SuppressWarnings("deprecation")
	public static WTPartUsageLink getLinkByParentAndChild(WTPart parentPart, WTPart childPart) throws WTException {
		WTPartUsageLink link = null;
		QueryResult queryResult = WTPartHelper.service.getUsesWTParts(parentPart, getConfigSpec());
		while (queryResult.hasMoreElements()) {
			Persistable[] per = (Persistable[]) queryResult.nextElement();

			if (per[1].equals(childPart)) {

				link = (WTPartUsageLink) per[0];
			}
		}
		return link;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-10-23
	 * @return
	 * @throws WTException
	 *
	 */
	private static ConfigSpec getConfigSpec() throws WTException {

		return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);

	}

	/**
	 * 通过工艺资源找到其应用的工艺计划
	 *
	 * @author lbzhang
	 * @date 2013-1-6下午05:37:37
	 * @param obj
	 * @return
	 * @throws WTException
	 */
	public static String getMPMProcessPlanByMPMResource(Object obj) throws WTException {
		WTHashSet localWTHashSet1 = new WTHashSet();

		MPMOperationAssignableResource resource = (MPMOperationAssignableResource) obj;

		if (resource instanceof MPMConsumableResource) {
			QueryResult qr = StructHelper.service.navigateReferencedBy(resource.getMaster(), MPMOperationToConsumableLink.class, true);
			while (qr.hasMoreElements()) {
				Iterated it = (Iterated) qr.nextElement();
				localWTHashSet1.add(it.getMaster());
			}
		}

		WTHashSet localObject = new WTHashSet();
		LatestConfigSpec localLatestConfigSpec = new LatestConfigSpec();
		WTHashSet localWTHashSet2 = new WTHashSet();
		if (!localWTHashSet1.isEmpty()) {
			QueryResult qr2 = localLatestConfigSpec.process(PersistenceHelper.navigate(localWTHashSet1, "usedBy", MPMOperationUsageLink.class, true));
			localWTHashSet1.clear();

			while (qr2.hasMoreElements()) {
				Iterated it2 = (Iterated) qr2.nextElement();
				if (it2 instanceof MPMProcessPlan) {
					localObject.add(it2);
				} else if (it2 instanceof MPMOperation) {
					localWTHashSet1.add(it2.getMaster());
				} else {
					localWTHashSet2.add(it2.getMaster());
				}
			}
		}

		while (!localWTHashSet2.isEmpty()) {
			QueryResult qr3 = localLatestConfigSpec.process(PersistenceHelper.navigate(localWTHashSet2, "usedBy", MPMSequenceUsageLink.class, true));
			localWTHashSet2 = new WTHashSet();
			while (qr3.hasMoreElements()) {
				Iterated it3 = (Iterated) qr3.nextElement();
				if (it3 instanceof MPMProcessPlan) {
					localObject.add(it3);
				} else {
					localWTHashSet2.add(it3.getMaster());
				}
			}
		}
		String mpmProcessPlanStr = "";
		Object[] object = localObject.toArray();
		for (int i = 0; i < object.length; i++) {
			Object o = object[i];
			ObjectReference objRef = (ObjectReference) o;
			MPMProcessPlan plan = (MPMProcessPlan) objRef.getObject();
			String planName = plan.getNumber() + "_" + plan.getName();
			GLLogger.debug("planName====>" + planName);
			mpmProcessPlanStr += planName + ",";
		}
		if (mpmProcessPlanStr.indexOf(",") > 0) {
			mpmProcessPlanStr = mpmProcessPlanStr.substring(0, mpmProcessPlanStr.length() - 1);
		}

		return mpmProcessPlanStr;
	}

	/**
	 * 通过标准属性名称查询标准属性ida2a2
	 *
	 * @param SAttributeName
	 * @return
	 * @throws WTException
	 */
	public static Long querySAttributeId(String SAttributeName) throws WTException {
		QuerySpec querySpec = new QuerySpec(LWCFlexAttDefinition.class);
		querySpec.appendWhere(new SearchCondition(LWCFlexAttDefinition.class, _LWCAttributeDefinition.NAME, SearchCondition.EQUAL, SAttributeName), new int[] { 0 });
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			LWCFlexAttDefinition lwc = (LWCFlexAttDefinition) queryResult.nextElement();
			return lwc.getPersistInfo().getObjectIdentifier().getId();
		}
		return null;
	}

	/**
	 * 通过MBA值查询
	 */
	public static void querySAttributeValue(Class objectClass, QuerySpec querySpec, Map<String, Object> mbaMap) throws WTException {
		for (String mbaName : mbaMap.keySet()) {
			Long id = querySAttributeId(mbaName);
			String columnName = querySAttributeColumnName(id);
			querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(objectClass, columnName, SearchCondition.LIKE, "%" + CommonUtil.objectToString(mbaMap.get(mbaName)) + "%", false), index);
		}

	}

	/**
	 * 通过标准属性ida2a2查询映射列名
	 *
	 * @param id
	 * @return
	 * @throws WTException
	 */
	public static String querySAttributeColumnName(Long id) throws WTException {
		QuerySpec querySpec = new QuerySpec(LWCColumnAllocation.class);
		querySpec.appendWhere(new SearchCondition(LWCColumnAllocation.class, "attributeDefReference.key.id", SearchCondition.EQUAL, id), new int[] { 0 });
		querySpec.appendAnd();
		querySpec.appendWhere(new SearchCondition(LWCColumnAllocation.class, "logicalName", SearchCondition.EQUAL, "value"), new int[] { 0 });
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			LWCColumnAllocation lwc = (LWCColumnAllocation) queryResult.nextElement();
			return lwc.getPhysicalName();
		}
		return null;
	}
}
