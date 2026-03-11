package com.glaway.mpm.mpmresource.helper;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.part.WTPart;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.intf.workproceduce.WorkproceduceUtil;
import com.glaway.mpm.model.CsType;
import com.glaway.mpm.model.Dashboard;
import com.glaway.mpm.model.DashboardType;
import com.glaway.mpm.model.EpType;
import com.glaway.mpm.model.Equipment;
import com.glaway.mpm.model.FkType;
import com.glaway.mpm.model.Frock;
import com.glaway.mpm.model.KnifeTool;
import com.glaway.mpm.model.KtType;
import com.glaway.mpm.model.Material;
import com.glaway.mpm.model.MtType;
import com.glaway.mpm.model.PdName;
import com.glaway.mpm.model.PdNameType;
import com.glaway.mpm.model.ProcessTemplate;
import com.glaway.mpm.model.ShopType;
import com.glaway.mpm.model.Skill;
import com.glaway.mpm.model.Tool;
import com.glaway.mpm.model.ToolType;
import com.glaway.mpm.model.TpType;
import com.glaway.mpm.model.UnSDashboard;
import com.glaway.mpm.model.UnSDashboardType;
import com.glaway.mpm.model.WorkPlace;
import com.glaway.mpm.model.WorkShop;
import com.glaway.mpm.model.WorkSpace;
import com.glaway.mpm.mpmresource.AttributeConstants;
import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.mpmresource.TypeNameConstants;
import com.glaway.mpm.mpmresource.gznumber.classification.GZNumberClassification;
import com.glaway.mpm.mpmresource.gznumber.classification.GZNumberClassificationHelper;
import com.glaway.mpm.util.FolderUtil;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMResourceUtil;
import com.glaway.mpm.util.Util;
import com.glaway.mpm.util.WTContainerUtil;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.windchill.mpml.resource.MPMPlant;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;
import com.ptc.windchill.mpml.resource.MPMSkill;
import com.ptc.windchill.mpml.resource.MPMTooling;
import com.ptc.windchill.mpml.resource.MPMWorkCenter;

public class MPMResourceHelper {

	/**
	 * 获取所有的工装
	 *
	 * @author qianlong
	 * @date 2013-4-27
	 * @param typeMap
	 * @param typeIdentifier
	 * @param fkType
	 * @throws WTException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static void getAllFrocks(FkType fkType) throws WTException, RemoteException, WTPropertyVetoException {
		List<FkType> fkTypeList = new ArrayList<FkType>();
		FkType comFkType = getAllFrocks(TypeNameConstants.GZCommonFrog, "通用工装");
		FkType specFkType = getAllFrocks(TypeNameConstants.GZSpecFrog, "专用工装");
		fkTypeList.add(comFkType);
		fkTypeList.add(specFkType);
		fkType.setFkTypes(fkTypeList);

	}


	public static FkType getAllFrocks(String gztype, String gzTypeName) throws WTException, RemoteException, WTPropertyVetoException {
		FkType fkType = new FkType(gzTypeName);
		TypeDefinitionReference typeRef = TypedUtilityServiceHelper.service.getTypeDefinitionReference(gztype);
		long branchid = typeRef.getKey().getBranchId();
		getRegularlyFrock(fkType, branchid);
		// 获取第一层的工装分类
//		ArrayList<GZNumberClassification> rootClassificationList = GZNumberClassificationHelper
//				.getChildClassifications(Constants.root);
//		if (rootClassificationList.size() != 0) {
//			// 获取第二层的工装分类,因为第二层的分类都一样的，所以只要寻找通过某一个第一层分类获取第二层分类
//			ArrayList<GZNumberClassification> nextClassificationList = GZNumberClassificationHelper
//					.getChildClassifications(rootClassificationList.get(0).getObjectclasspath());
//			// 由于树结构不需要按照第一层工装结构分类，所以先循环第二层
//			for (GZNumberClassification childClassification : nextClassificationList) {
//				FkType childFkType = new FkType(childClassification.getObjectclassvalue() + "_"
//						+ childClassification.getObjectname());
//
//				for (GZNumberClassification rootClassification : rootClassificationList) {
//					getChildFrockClassifications(childFkType, childClassification, rootClassification
//							.getObjectclassvalue()
//							+ childClassification.getObjectclassvalue(), branchid);
//				}
//				fkTypeList.add(childFkType);
//			}
//			fkType.setFkTypes(fkTypeList);
//		}
		return fkType;
	}
	/**
	 * 获取常用工装
	 *
	 * @author qianlong
	 * @date 2013-5-29
	 * @param fkType
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 *
	 */
	private static void getRegularlyFrock(FkType fkType, long branchId) throws RemoteException,
			WTPropertyVetoException, WTException {
		Map<String, Boolean> ibaMap = new HashMap<String, Boolean>();
//		ibaMap.put(AttributeConstants.isRegularlyTools, true);
		QueryResult qr = MPMResourceUtil.getMPMToolingByIBAType(ibaMap, branchId);
		List<Frock> frockList = new ArrayList<Frock>();
		while (qr.hasMoreElements()) {
			MPMTooling tooling = (MPMTooling) qr.nextElement();
			Frock frock = getFrock(tooling);
			frockList.add(frock);
		}
		fkType.setFrocks(frockList);
	}

	/**
	 *
	 * @author qianlong
	 * @date 2013-5-27
	 * @param fkType
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 *
	 */
	private static void getChildFrockClassifications(FkType fkType, GZNumberClassification parentClassification,
			String allClassValue, long branchid) throws RemoteException, WTPropertyVetoException, WTException {
		ArrayList<GZNumberClassification> parentClassificationList = GZNumberClassificationHelper
				.getChildClassifications(parentClassification.getObjectclasspath());
		if (parentClassificationList.size() == 0) {
			if (parentClassification.getObjectclasspath().length() == 3) {
				QueryResult result = MPMResourceUtil.getMPMToolingByLike(allClassValue + "%", branchid);
				if (result.size() != 0) {
					List<FkType> fkTypeList = fkType.getFkTypes();
					if (null == fkTypeList) {
						fkTypeList = new ArrayList<FkType>();
					}
					FkType childFkType = new FkType(allClassValue + "_" + parentClassification.getObjectname());
					List<Frock> frockList = new ArrayList<Frock>();
					while (result.hasMoreElements()) {
						MPMTooling tooling = (MPMTooling) result.nextElement();
						Frock frock = getFrock(tooling);
						frockList.add(frock);
					}
					childFkType.setFrocks(frockList);
					fkTypeList.add(childFkType);
					fkType.setFkTypes(fkTypeList);
				}
			}
		} else {
			for (GZNumberClassification childClassification : parentClassificationList) {
				String allClassValue_new = allClassValue + childClassification.getObjectclassvalue();
				getChildFrockClassifications(fkType, childClassification, allClassValue_new, branchid);
			}
		}
	}

	/**
	 * @Author caolei
	 * @Date 2015/6/3
	 * @Check caolei
	 * @Description 取出常用语数据
	 */
	public static List<CsType> getAllTemplateFolder(QueryResult queryResult,List<CsType> subTypeList) {
		try{
			while (queryResult.hasMoreElements()) {
				Folder folder = (Folder) queryResult.nextElement();
				List<String> commonStrings=new ArrayList<String>();
				CsType csType = new CsType();
				csType.setName(folder.getName());
					QueryResult MpmResult = FolderHelper.service.findFolderContents(folder, MPMTooling.class);
					while (MpmResult.hasMoreElements()) {
						MPMTooling cycyy = (MPMTooling) MpmResult.nextElement();
						commonStrings.add(cycyy.getName());
					}
					csType.setCommonStrings(commonStrings);
					subTypeList.add(csType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return subTypeList;
	}

	/**
	 * 获取制造单位下面的设备
	 *
	 * @author qianlong
	 * @throws WTException
	 * @date 2013-5-29
	 *
	 */
	public static void getEquipmentByWorkShop(MPMTooling tooling, TypeIdentifier identifier,
			Map<TypeIdentifier, List<Equipment>> equipMap, Map<String, List<Equipment>> nameEquipMap)
			throws WTException {
		Equipment equipment = getEquipment(tooling);
		List<Equipment> equipmentList = equipMap.get(identifier);
		if (equipmentList == null) {
			equipmentList = new ArrayList<Equipment>();
			equipMap.put(identifier, equipmentList);
		}
		equipmentList.add(equipment);
		String typeName = TypedUtility.getLocalizedTypeName(identifier, Locale.CHINA);
		List<Equipment> equipmentList1 = nameEquipMap.get(typeName);
		if (equipmentList1 == null) {
			equipmentList1 = new ArrayList<Equipment>();
			nameEquipMap.put(typeName, equipmentList1);
		}
		equipmentList1.add(equipment);

		// 获取设备下面的子设备
		List<Equipment> childEquipments = new ArrayList<Equipment>();
		QueryResult queryResult = MPMResourceUtil.getChildPart(tooling);
		while (queryResult.hasMoreElements()) {
			WTPart childPart = (WTPart) queryResult.nextElement();
			if (childPart instanceof MPMTooling) {
				MPMTooling tooling1 = (MPMTooling) childPart;
				Equipment childEquipment = getEquipment(tooling1);
				childEquipments.add(childEquipment);
			}
		}
		equipment.setEquipments(childEquipments);
	}

	/**
	 * 获取制造单位下面的工量具
	 *
	 * @author qianlong
	 * @throws WTException
	 * @date 2013-5-29
	 *
	 */
	public static void getToolByWorkShop(MPMTooling tooling, TypeIdentifier identifier,
			Map<TypeIdentifier, List<Tool>> toolMap, Map<String, List<Tool>> nameToolMap) throws WTException {
		// 以下是工具存在类型的一个版本
		Tool tool = getTool(tooling);
		List<Tool> toolList = toolMap.get(identifier);
		if (toolList == null) {
			toolList = new ArrayList<Tool>();
			toolMap.put(identifier, toolList);
		}
		toolList.add(tool);
		String typeName = TypedUtility.getLocalizedTypeName(identifier, Locale.CHINA);
		List<Tool> toolList1 = nameToolMap.get(typeName);
		if (toolList1 == null) {
			toolList1 = new ArrayList<Tool>();
			nameToolMap.put(typeName, toolList1);
		}
		toolList1.add(tool);
	}

	/**
	 * 获取制造单位下面的工序名称
	 *
	 * @author qianlong
	 * @throws WTException
	 * @date 2013-7-10
	 */
	public static void getPdNameByWorkShop(MPMTooling tooling, List<ShopType> workShopToShopTypeList,
			List<PdName> pdNameList, Map<String, ShopType> allShopTypeMap) throws WTException {
		PdName pdName = getPdName(tooling);

		List<ShopType> shopTypeList = new ArrayList<ShopType>();
		QueryResult qr = MPMResourceUtil.getChildPart(tooling);
		while (qr.hasMoreElements()) {
			WTPart childPart = (WTPart) qr.nextElement();
			// 获取所有的工种
			if (childPart instanceof MPMSkill) {
				shopTypeList.add(allShopTypeMap.get(childPart.getNumber()));
				workShopToShopTypeList.add(allShopTypeMap.get(childPart.getNumber()));
			}
		}
		Util.sort(shopTypeList);
		pdName.setShopTypes(shopTypeList);
		pdNameList.add(pdName);
	}

	public static Skill getSkill(MPMSkill mpmskill) throws WTException {
		Skill skill = new Skill();
		skill.setName(mpmskill.getName());
		skill.setNumber(mpmskill.getNumber());
		skill.setOid(Util.getStringOid(mpmskill));
		return skill;


	}

	/**
	 * 获取所有的刀具
	 *
	 * @author qianlong
	 * @date 2013-4-27
	 * @param typeMap
	 * @param typeIdentifier
	 * @param ktType
	 * @throws WTException
	 * @throws RemoteException
	 *
	 */
	public static void getAllKnifeTools(Map<TypeIdentifier, List<TypeIdentifier>> typeMap,
			TypeIdentifier typeIdentifier, KtType ktType) throws WTException, RemoteException {
		List<TypeIdentifier> identifierList = typeMap.get(typeIdentifier);
		if (identifierList == null || identifierList.size() == 0) {
			List<KnifeTool> knifeToolList = new ArrayList<KnifeTool>();
			QueryResult result = MPMResourceUtil.getMPMToolingByType(TypeNameConstants.DJ);
			while (result.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) result.nextElement();
				KnifeTool knifeTool = getKnifeTool(tooling);
				knifeToolList.add(knifeTool);
			}
			Util.sort(knifeToolList);
			ktType.setKnifeTools(knifeToolList);
		} else {

			List<KtType> childKtTypeList = new ArrayList<KtType>();
			ktType.setKtTypes(childKtTypeList);
			for (TypeIdentifier identifier : identifierList) {
				KtType childKtType = new KtType(TypedUtility.getLocalizedTypeName(identifier, Locale.CHINA));
				childKtTypeList.add(childKtType);
				getAllKnifeTools(typeMap, identifier, childKtType);
			}
		}
	}

	/**
	 * 获取所有的工种及其下面的结构
	 *
	 * @author qianlong
	 * @date 2013-2-18
	 * @throws WTException
	 *
	 */
	public static void getAllShopType(Map<String, ShopType> map, List<ShopType> list) throws WTException {
		QueryResult qr = MPMResourceUtil.getAllMPMSkill();
		while (qr.hasMoreElements()) {
			MPMSkill skill = (MPMSkill) qr.nextElement();
			ShopType shopType = getShopType(skill);
			// 获取所有的工艺术语
			List<String> csList = new ArrayList<String>();

			QueryResult qrResult = MPMResourceUtil.getChildPart(skill);
			while (qrResult.hasMoreElements()) {
				WTPart child = (WTPart) qrResult.nextElement();
				csList.add(child.getName());
			}
			Util.sort(csList);
			shopType.setCss(csList);

			list.add(shopType);
			Util.sort(list);
			map.put(skill.getNumber(), shopType);
		}
	}

	/**
	 * 按设备类型分类设备
	 *
	 * @author qianlong
	 * @date 2013-5-28
	 * @param map
	 * @param nameEquipMap
	 * @param startNum
	 * @param startTypeName
	 * @param epTypes
	 * @throws WTException
	 *
	 */
	public static void getEquipStructureType(Map<TypeIdentifier, List<Equipment>> map,
			Map<String, List<Equipment>> nameEquipMap, List<EpType> epTypes) throws WTException {
		Map<String, List<String>> typeMap = deelWithTypeName(map.keySet(), TypeNameConstants.SB);
		String displayName = TypedUtility.getLocalizedTypeName(TypedUtility.getTypeIdentifier(TypeNameConstants.SB),
				Locale.CHINA);
		EpType epType = new EpType(displayName);
		getEquipTypeCycle(typeMap, nameEquipMap, TypeNameConstants.SB, displayName, epType);
		epTypes.add(epType);
	}

	private static void getEquipTypeCycle(Map<String, List<String>> typeMap, Map<String, List<Equipment>> nameEquipMap,
			String typeName, String displayName, EpType epType) throws WTException {

		List<String> typeList = typeMap.get(typeName);
		if (typeList != null && typeList.size() != 0) {
			List<EpType> childEpTypeList = new ArrayList<EpType>();
			for (String childTypeName : typeList) {
				String childDisplayName = TypedUtility.getLocalizedTypeName(TypedUtility
						.getTypeIdentifier(childTypeName), Locale.CHINA);
				EpType childEpType = new EpType(childDisplayName);
				getEquipTypeCycle(typeMap, nameEquipMap, childTypeName, childDisplayName, childEpType);
				childEpTypeList.add(childEpType);
			}
			epType.setEpTypes(childEpTypeList);
		} else {
			List<Equipment> equipmentList = nameEquipMap.get(displayName);
			if (equipmentList != null) {
				epType.setEquipments(equipmentList);
			}
		}
	}

	/**
	 * 按工量具类型分类工量具
	 *
	 * @author qianlong
	 * @date 2013-5-28
	 * @param map
	 * @param nameToolMap
	 * @param startNum
	 * @param startTypeName
	 * @param toolTypes
	 * @throws WTException
	 *
	 */
	public static void getToolStructureType(Map<TypeIdentifier, List<Tool>> map, Map<String, List<Tool>> nameToolMap,
			List<ToolType> toolTypes) throws WTException {
//		Map<String, List<String>> typeMap = deelWithTypeName(map.keySet(), TypeNameConstants.GJ);
//		String displayName = TypedUtility.getLocalizedTypeName(TypedUtility.getTypeIdentifier(TypeNameConstants.GJ),
//				Locale.CHINA);
//		ToolType toolType = new ToolType(displayName);
//		getToolTypeCycle(typeMap, nameToolMap, TypeNameConstants.GJ, displayName, toolType);
//		toolTypes.add(toolType);
		getStructureType(map, nameToolMap, toolTypes, TypeNameConstants.GJ);
	}


	public static void getMeasureStructureType(Map<TypeIdentifier, List<Tool>> map, Map<String, List<Tool>> nameToolMap,
			List<ToolType> toolTypes) throws WTException {
		getStructureType(map, nameToolMap, toolTypes, TypeNameConstants.LJ);

	}
	public static void getStructureType(Map<TypeIdentifier, List<Tool>> map, Map<String, List<Tool>> nameToolMap,
			List<ToolType> toolTypes,String softType) throws WTException {
		Map<String, List<String>> typeMap = deelWithTypeName(map.keySet(), softType);
		String displayName = TypedUtility.getLocalizedTypeName(TypedUtility.getTypeIdentifier(softType),
				Locale.CHINA);
		ToolType toolType = new ToolType(displayName);
		getToolTypeCycle(typeMap, nameToolMap, softType, displayName, toolType);
		toolTypes.add(toolType);
	}

	private static void getToolTypeCycle(Map<String, List<String>> typeMap, Map<String, List<Tool>> nameToolMap,
			String typeName, String displayName, ToolType toolType) throws WTException {

		List<String> typeList = typeMap.get(typeName);
		if (typeList != null && typeList.size() != 0) {
			List<ToolType> childToolTypeList = new ArrayList<ToolType>();
			for (String childTypeName : typeList) {
				String childDisplayName = TypedUtility.getLocalizedTypeName(TypedUtility
						.getTypeIdentifier(childTypeName), Locale.CHINA);
				ToolType childEpType = new ToolType(childDisplayName);
				getToolTypeCycle(typeMap, nameToolMap, childTypeName, childDisplayName, childEpType);
				childToolTypeList.add(childEpType);
			}
			toolType.setToolTypes(childToolTypeList);
		} else {
			List<Tool> toolList = nameToolMap.get(displayName);
			if (toolList != null) {
				toolType.setTools(toolList);
			}
		}
	}

	private static Map<String, List<String>> deelWithTypeName(Set<TypeIdentifier> typeSet, String firstType) {

		Map<String, List<String>> typeMap = new HashMap<String, List<String>>();
		for (TypeIdentifier identifier : typeSet) {
			String typeNameArray[] = null;
			String typeName = identifier.getTypename();
			if (!typeName.contains(firstType + "|")) {
				continue;
			}

			typeNameArray = identifier.getTypename().replace(firstType + "|", "").split("\\|");

			// 判断第一个节点 ，需要与大类创建父子关系
			List<String> typeList = typeMap.get(firstType);
			if (typeList == null) {
				typeList = new ArrayList<String>();
				typeMap.put(firstType, typeList);
			}
			if (!typeList.contains(typeNameArray[0])) {
				typeList.add(typeNameArray[0]);
			}

			for (int i = 0; i < typeNameArray.length; i++) {

				// 只要不是最后一级的类型，就需要创建父子关系的Map
				if (typeNameArray.length <= i + 1) {
					continue;
				}
				typeList = typeMap.get(typeNameArray[i]);
				if (typeList == null) {
					typeList = new ArrayList<String>();
					typeMap.put(typeNameArray[i], typeList);
				}
				if (!typeList.contains(typeNameArray[i + 1])) {
					typeList.add(typeNameArray[i + 1]);
				}
			}
		}
		return typeMap;
	}

	/**
	 * 获取模板文件夹结构
	 *
	 * @author qianlong
	 * @date 2012-11-17
	 * @param queryResult
	 * @param tpType
	 * @throws WTException
	 *
	 */
	@SuppressWarnings("deprecation")
	public static void getTemplateFolder(QueryResult queryResult, TpType tpType) throws WTException {
		List<TpType> subTypeList = new ArrayList<TpType>();
		while (queryResult.hasMoreElements()) {
			Folder folder = (Folder) queryResult.nextElement();
			TpType subType = new TpType(folder.getName());
			subTypeList.add(subType);
			QueryResult subResult = FolderHelper.service.findSubFolders(folder);
			if (!subResult.hasMoreElements()) {
				List<ProcessTemplate> templateList = new ArrayList<ProcessTemplate>();
				QueryResult fileResult = FolderHelper.service.findFolderContents(folder, WTDocument.class);
				while (fileResult.hasMoreElements()) {
					WTDocument document = (WTDocument) fileResult.nextElement();
					ProcessTemplate processTemplate = new ProcessTemplate();
					processTemplate.setOid(Util.getStringOid(document));
					processTemplate.setNumber(document.getNumber());
					processTemplate.setName(document.getName());
					templateList.add(processTemplate);
				}
				subType.setProcessTemplates(templateList);
			} else {
				getTemplateFolder(subResult, subType);
			}
		}
		tpType.setTpTypes(subTypeList);
	}

	/**
	 * 获取工艺辅料文件夹结构
	 *
	 * @author qianlong
	 * @date 2012-11-17
	 * @param queryResult
	 * @param tpType
	 * @throws WTException
	 *
	 */
	@SuppressWarnings("deprecation")
	public static void getMaterialFolder(Folder parentFolder, MtType mtType) throws WTException {

		List<MtType> subTypeList = new ArrayList<MtType>();
		QueryResult subResult = FolderHelper.service.findSubFolders(parentFolder);
		if (subResult.hasMoreElements()) {
			while (subResult.hasMoreElements()) {
				Folder subFolder = (Folder) subResult.nextElement();
				MtType subType = new MtType(subFolder.getName());
				subTypeList.add(subType);
				getMaterialFolder(subFolder, subType);
			}
		} else {
			mtType.setTypePath(parentFolder.getFolderPath());
		}
		//TODO  812 查询材料

		mtType.setMtTypes(subTypeList);
	}

	/**
	 * 获取工装文件夹结构
	 * @param parentFolder
	 * @param mtType
	 * @throws WTException
	 */
	public static void getFrockFolder(Folder parentFolder, FkType fkType) throws WTException {

		List<FkType> subTypeList = new ArrayList<FkType>();
		QueryResult subResult = FolderHelper.service.findSubFolders(parentFolder);
		fkType.setTypePath(parentFolder.getFolderPath());
		List<Frock> frocks = getFrockByTypeRMI( fkType.getTypePath());
		fkType.setFrocks(frocks);

		if (subResult!=null) {
			while (subResult.hasMoreElements()) {
				Folder subFolder = (Folder) subResult.nextElement();
				FkType subType = new FkType(subFolder.getName());
				subTypeList.add(subType);
				getFrockFolder(subFolder, subType);
			}
		}

		fkType.setFkTypes(subTypeList);
	}


	public static List<Frock> getFrockByTypeRMI(String typePath) {
		GLLogger.debug("typePath----" + typePath);
		List<Frock> frockList = new ArrayList<Frock>();
		try {
			WTContainer container = WTContainerUtil.getContainerByName(Constants.mpmResourceLibraryName);
			Folder folder = FolderUtil.getFolder(typePath, WTContainerRef.newWTContainerRef(container));
			QueryResult result = FolderHelper.service.findFolderContents(folder, MPMTooling.class);
			while (result.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) result.nextElement();
				Frock frock = getFrock(tooling);
				frockList.add(frock);
			}
			Util.sort(frockList);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return frockList;
	}


	public static void getToolFolder(Folder parentFolder, ToolType toolType) throws WTException {

		List<ToolType> subTypeList = new ArrayList<ToolType>();
		QueryResult subResult = FolderHelper.service.findSubFolders(parentFolder);
		toolType.setTypePath(parentFolder.getFolderPath());
		List<Tool> tools = getToolByType( toolType.getTypePath());
		toolType.setTools(tools);

		if (subResult!=null) {
			while (subResult.hasMoreElements()) {
				Folder subFolder = (Folder) subResult.nextElement();
				ToolType subType = new ToolType(subFolder.getName());
				subTypeList.add(subType);
				getToolFolder(subFolder, subType);
			}
		}

		toolType.setToolTypes(subTypeList);
	}

	public static void getEquipmentFolder(Folder parentFolder, EpType epType) throws WTException {
		List<EpType> subTypeList = new ArrayList<EpType>();
		QueryResult subResult = FolderHelper.service.findSubFolders(parentFolder);
		epType.setTypePath(parentFolder.getFolderPath());
		List<Equipment> equipments = getEquipmentByType( epType.getTypePath());
		epType.setEquipments(equipments);
		if (subResult!=null) {
			while (subResult.hasMoreElements()) {
				Folder subFolder = (Folder) subResult.nextElement();
				EpType subType = new EpType(subFolder.getName());
				subTypeList.add(subType);
				getEquipmentFolder(subFolder, subType);
			}
		}
		epType.setEpTypes(subTypeList);
	}

	public static void getDashboardFolder(Folder parentFolder, DashboardType epType) throws WTException {
		List<DashboardType> subTypeList = new ArrayList<DashboardType>();
		QueryResult subResult = FolderHelper.service.findSubFolders(parentFolder);
		epType.setTypePath(parentFolder.getFolderPath());
		List<Dashboard> equipments = getDashboardByType( epType.getTypePath());
		epType.setDashboards(equipments);
		if (subResult!=null) {
			while (subResult.hasMoreElements()) {
				Folder subFolder = (Folder) subResult.nextElement();
				DashboardType subType = new DashboardType(subFolder.getName());
				subTypeList.add(subType);
				getDashboardFolder(subFolder, subType);
			}
		}
		epType.setDashboardTypes(subTypeList);
	}

	public static void getUnSDashboardFolder(Folder parentFolder, UnSDashboardType epType) throws WTException {
		List<UnSDashboardType> subTypeList = new ArrayList<UnSDashboardType>();
		QueryResult subResult = FolderHelper.service.findSubFolders(parentFolder);
		epType.setTypePath(parentFolder.getFolderPath());
		List<UnSDashboard> equipments = getUnSDashboardByType( epType.getTypePath());
		epType.setDashboards(equipments);
		if (subResult!=null) {
			while (subResult.hasMoreElements()) {
				Folder subFolder = (Folder) subResult.nextElement();
				UnSDashboardType subType = new UnSDashboardType(subFolder.getName());
				subTypeList.add(subType);
				getUnSDashboardFolder(subFolder, subType);
			}
		}
		epType.setDashboardTypes(subTypeList);
	}

	public static List<Equipment> getEquipmentByType(String typePath) {
		GLLogger.debug("typePath----" + typePath);
		List<Equipment> frockList = new ArrayList<Equipment>();
		try {
			WTContainer container = WTContainerUtil.getContainerByName(Constants.mpmResourceLibraryName);
			Folder folder = FolderUtil.getFolder(typePath, WTContainerRef.newWTContainerRef(container));
			QueryResult result = FolderHelper.service.findFolderContents(folder, MPMTooling.class);
			while (result.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) result.nextElement();
				Equipment tool = getEquipment(tooling);
				frockList.add(tool);
			}
			Util.sort(frockList);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return frockList;
	}

	public static List<Dashboard> getDashboardByType(String typePath) {
		GLLogger.debug("typePath-----" + typePath);
		List<Dashboard> frockList = new ArrayList<Dashboard>();
		try {
			WTContainer container = WTContainerUtil.getContainerByName(Constants.mpmResourceLibraryName);
			Folder folder = FolderUtil.getFolder(typePath, WTContainerRef.newWTContainerRef(container));
			QueryResult result = FolderHelper.service.findFolderContents(folder, MPMTooling.class);
			GLLogger.debug("result-----" + result.size());
			while (result.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) result.nextElement();
				Dashboard tool = getDashboard(tooling);
				frockList.add(tool);
			}
			Util.sort(frockList);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return frockList;
	}

	public static List<UnSDashboard> getUnSDashboardByType(String typePath) {
		GLLogger.debug("typePath-----" + typePath);
		List<UnSDashboard> frockList = new ArrayList<UnSDashboard>();
		try {
			WTContainer container = WTContainerUtil.getContainerByName(Constants.mpmResourceLibraryName);
			Folder folder = FolderUtil.getFolder(typePath, WTContainerRef.newWTContainerRef(container));
			QueryResult result = FolderHelper.service.findFolderContents(folder, MPMTooling.class);
			GLLogger.debug("result-----" + result.size());
			while (result.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) result.nextElement();
				UnSDashboard tool = getUnSDashboard(tooling);
				frockList.add(tool);
			}
			Util.sort(frockList);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return frockList;
	}

	public static void getKnifeFolder(Folder parentFolder, KtType ktType) throws WTException {

		List<KtType> subTypeList = new ArrayList<KtType>();
		QueryResult subResult = FolderHelper.service.findSubFolders(parentFolder);
		ktType.setTypePath(parentFolder.getFolderPath());
		List<KnifeTool> tools = getKnifeByType( ktType.getTypePath());
		ktType.setKnifeTools(tools);

		if (subResult!=null) {
			while (subResult.hasMoreElements()) {
				Folder subFolder = (Folder) subResult.nextElement();
				KtType subType = new KtType(subFolder.getName());
				subTypeList.add(subType);
				getKnifeFolder(subFolder, subType);
			}
		}

		ktType.setKtTypes(subTypeList);
	}

	public static List<Tool> getToolByType(String typePath) {
		GLLogger.debug("typePath----" + typePath);
		List<Tool> frockList = new ArrayList<Tool>();
		try {
			WTContainer container = WTContainerUtil.getContainerByName(Constants.mpmResourceLibraryName);
			Folder folder = FolderUtil.getFolder(typePath, WTContainerRef.newWTContainerRef(container));
			QueryResult result = FolderHelper.service.findFolderContents(folder, MPMTooling.class);
			while (result.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) result.nextElement();
				Tool tool = getTool(tooling);
				frockList.add(tool);
			}
			Util.sort(frockList);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return frockList;
	}
	public static List<KnifeTool> getKnifeByType(String typePath) {
		GLLogger.debug("typePath----" + typePath);
		List<KnifeTool> frockList = new ArrayList<KnifeTool>();
		try {
			WTContainer container = WTContainerUtil.getContainerByName(Constants.mpmResourceLibraryName);
			Folder folder = FolderUtil.getFolder(typePath, WTContainerRef.newWTContainerRef(container));
			QueryResult result = FolderHelper.service.findFolderContents(folder, MPMTooling.class);
			while (result.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) result.nextElement();
				KnifeTool tool = getKnifeTool(tooling);
				frockList.add(tool);
			}
			Util.sort(frockList);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return frockList;
	}
	/**
	 *
	 * @author qianlong
	 * @date 2013-6-8
	 * @return
	 *
	 */

	public static String getFolderPath(List<String> locationList, String folderName) {
		String location = "";
		if (null != locationList && locationList.size() != 0) {
			for (String str : locationList) {
				if ("".equals(location)) {
					location = str;
				} else {
					location = str + "/" + location;
				}
			}
			location = Constants.rootFolder + "/" + folderName + "/" + location;
		}
		return location;
	}

	/**
	 *构建制造单位模型对象
	 *
	 * @author qianlong
	 * @throws WTException
	 * @date 2013-7-18
	 */
	public static WorkShop getWorkShop(MPMPlant plant) throws WTException {
		WorkShop workShop = new WorkShop();
		workShop.setOid(Util.getStringOid(plant));
		workShop.setNumber(plant.getNumber());
		workShop.setName(plant.getName());
		return workShop;
	}

	/**
	 *构建工位模型对象
	 *
	 * @author qianlong
	 * @throws WTException
	 * @date 2013-7-18
	 */
	public static WorkSpace getWorkSpace(MPMWorkCenter workCenter) throws WTException {
		WorkSpace workSpace = new WorkSpace();
		workSpace.setOid(Util.getStringOid(workCenter));
		workSpace.setNumber(workCenter.getNumber());
		workSpace.setName(workCenter.getName());
		return workSpace;
	}

	/**
	 * 构建工位维护对象
	 * @author zhuhao
	 * @date 2017.10.17
	 * @throws WTException
	 */
	public static WorkPlace getWorkplace(MPMTooling tooling) throws WTException {
		WorkPlace workplace = new WorkPlace();
		workplace.setOid(Util.getStringOid(tooling));
		workplace.setNumber(tooling.getNumber());
		workplace.setName(tooling.getName());
		IBAHelper helper = new IBAHelper(tooling);
		workplace.setSpace(Util.formateString(helper.getIBAValue("WORKPLACE")));
		workplace.setSelf(Util.formateString(helper.getIBAValue("REMARK")));
		workplace.setEngLishName(Util.formateString(helper.getIBAValue("EnglishName")));
		return workplace;
	}

	/**
	 *构建设备模型对象
	 *
	 * @author qianlong
	 * @throws WTException
	 * @date 2013-7-18
	 */
	public static Equipment getEquipment(MPMTooling tooling) throws WTException {
		Equipment equipment = new Equipment();
		IBAHelper helper = new IBAHelper(tooling);
		equipment.setOid(Util.getStringOid(tooling));
		equipment.setNumber(tooling.getNumber());
		equipment.setName(tooling.getName());
//		equipment.setEquipmentNumber(Util.formateString(helper.getIBAValue(AttributeConstants.equipmentNumber)));
//		equipment.setEquipmentAmount(Util.formateString(helper.getIBAValue(AttributeConstants.equipmentAmount)));
//		equipment.setEquipmentVender(Util.formateString(helper.getIBAValue(AttributeConstants.equipmentVender)));
//		equipment.setModelNumber(Util.formateString(helper.getIBAValue(AttributeConstants.pindex)));
//		equipment.setRemark(Util.formateString(helper.getIBAValue(AttributeConstants.remark)));
		//TODO 812
		equipment.setEnglishName(Util.formateString(helper.getIBAValue("EnglishName")));

		equipment.setCsize(Util.formateString(helper.getIBAValue("CSIZE")));
		equipment.setMindex(Util.formateString(helper.getIBAValue("MINDEX")));
		equipment.setEquipmentType(Util.formateString(helper.getIBAValue("EQUIPMENTTYPE")));

		return equipment;
	}

	public static Dashboard getDashboard(MPMTooling tooling) throws WTException {
		Dashboard dashboard = new Dashboard();
		IBAHelper helper = new IBAHelper(tooling);
		dashboard.setOid(Util.getStringOid(tooling));
		dashboard.setNumber(tooling.getNumber());
		dashboard.setName(tooling.getName());
		dashboard.setCsize(Util.formateString(helper.getIBAValue("CSIZE")));
		dashboard.setMindex(Util.formateString(helper.getIBAValue("MINDEX")));
		dashboard.setEquipmentType(Util.formateString(helper.getIBAValue("EQUIPMENTTYPE")));
		return dashboard;
	}

	public static UnSDashboard getUnSDashboard(MPMTooling tooling) throws WTException {
		UnSDashboard dashboard = new UnSDashboard();
		IBAHelper helper = new IBAHelper(tooling);
		dashboard.setOid(Util.getStringOid(tooling));
		dashboard.setNumber(tooling.getNumber());
		dashboard.setName(tooling.getName());
		dashboard.setCsize(Util.formateString(helper.getIBAValue("CSIZE")));
		dashboard.setMindex(Util.formateString(helper.getIBAValue("MINDEX")));
		dashboard.setEquipmentType(Util.formateString(helper.getIBAValue("EQUIPMENTTYPE")));
		return dashboard;
	}

	/**
	 *构建工序名称模型对象
	 *
	 * @author qianlong
	 * @throws WTException
	 * @date 2013-7-18
	 */
	public static PdName getPdName(MPMTooling tooling) throws WTException {
		PdName pdName = new PdName();
		pdName.setName(tooling.getName());
		pdName.setShortcut(WorkproceduceUtil.convertStr(tooling.getName()));
		pdName.setNumber(tooling.getNumber());

		IBAHelper helper = new IBAHelper(tooling);
		pdName.setRemark(helper.getIBAValue("REMARK"));
		return pdName;
	}

	/**
	 *构建工种模型对象
	 *
	 * @author qianlong
	 * @throws WTException
	 * @date 2013-7-18
	 */
	public static ShopType getShopType(MPMSkill skill) throws WTException {
		ShopType shopType = new ShopType();
		shopType.setOid(Util.getStringOid(skill));
		shopType.setNumber(skill.getNumber());
		shopType.setName(skill.getName());
		return shopType;
	}

	/**
	 *构建工装模型对象
	 *
	 * @author qianlong
	 * @throws WTException
	 * @date 2013-7-18
	 */
	public static Frock getFrock(MPMTooling tooling) throws WTException {
		Frock frock = new Frock();
		IBAHelper helper = new IBAHelper(tooling);
		//TODO 812
		String type = tooling.getType();
		try {
			type = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(tooling);

		} catch (RemoteException e) {
			e.printStackTrace();
		}

		frock.setType(type);
		frock.setOid(Util.getStringOid(tooling));
		frock.setFrockName(tooling.getName());
		frock.setFrockNum(tooling.getNumber());
		frock.setPindex(Util.formateString(helper.getIBAValue(AttributeConstants.pindex)));
		frock.setTypeno(Util.formateString(helper.getIBAValue(AttributeConstants.typeno)));
		frock.setLevle(Util.formateString(helper.getIBAValue(AttributeConstants.level)));
		frock.setFrockType(helper.getIBAValue(AttributeConstants.frocktype));
		frock.setEngLishName(helper.getIBAValue("EnglishName"));
		return frock;
	}

	/**
	 *构建工量具模型对象
	 *
	 * @author qianlong
	 * @throws WTException
	 * @date 2013-7-18
	 */
	public static Tool getTool(MPMTooling tooling) throws WTException {
		Tool tool = new Tool();
		String type = tooling.getType();
		try {
			type = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(tooling);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		IBAHelper ibaHelper = new IBAHelper(tooling);
		tool.setType(type);
		tool.setOid(Util.getStringOid(tooling));
		tool.setToolNum(tooling.getNumber());
		tool.setToolName(tooling.getName());
		tool.setMindex(ibaHelper.getIBAValue("MINDEX"));
		tool.setCsize(ibaHelper.getIBAValue("CSIZE"));
		tool.setEngLishName(ibaHelper.getIBAValue("EnglishName"));
		return tool;
	}

	/**
	 *构建刀具模型对象
	 *
	 * @author qianlong
	 * @throws WTException
	 * @date 2013-7-18
	 */
	public static KnifeTool getKnifeTool(MPMTooling tooling) throws WTException {
		KnifeTool knifeTool = new KnifeTool();
		String type = tooling.getType();
		try {
			type = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(tooling);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		IBAHelper ibaHelper = new IBAHelper(tooling);
		knifeTool.setType(type);
		knifeTool.setOid(Util.getStringOid(tooling));
		knifeTool.setKnifeToolNum(tooling.getNumber());
		knifeTool.setKnifeToolName(tooling.getName());
		knifeTool.setCmat(ibaHelper.getIBAValue("CMAT"));
		knifeTool.setRkzj(ibaHelper.getIBAValue("RKZJ"));
		knifeTool.setJczj(ibaHelper.getIBAValue("JCZJ"));
		knifeTool.setRkcd(ibaHelper.getIBAValue("RKCD"));
		knifeTool.setZcd(ibaHelper.getIBAValue("ZCD"));
		knifeTool.setGc(ibaHelper.getIBAValue("GC"));
		knifeTool.setZxjgcc(ibaHelper.getIBAValue("ZXJGCC"));
		knifeTool.setZdjgcc(ibaHelper.getIBAValue("ZDJGCC"));
		knifeTool.setJgxs(ibaHelper.getIBAValue("JGXS"));
		knifeTool.setJklx(ibaHelper.getIBAValue("JKLX"));
		knifeTool.setJsbz(ibaHelper.getIBAValue("JSBZ"));
		knifeTool.setRkyjbj(ibaHelper.getIBAValue("RKYJBJ"));
		knifeTool.setCs(ibaHelper.getIBAValue("CS"));
		knifeTool.setKnifetype(ibaHelper.getIBAValue("KNIFETYPE"));
		knifeTool.setEngLishName(ibaHelper.getIBAValue("EnglishName"));
		return knifeTool;
	}

	/**
	 *构建材料模型对象
	 *
	 * @author qianlong
	 * @throws WTException
	 * @date 2013-7-18
	 */
	public static Material getMaterial(WTPart processMaterial) throws WTException {
		Material material = new Material();
		IBAHelper helper = new IBAHelper((IBAHolder) processMaterial);
		if (processMaterial instanceof MPMProcessMaterial) {
			material.setOid(Util.getStringOid(processMaterial));
			material.setNumber(processMaterial.getNumber());
			material.setMaterialName(processMaterial.getName());
			material.setMaterialNumber(processMaterial.getNumber());
//			material.setMaterialBrand(Util.formateString(helper.getIBAValue(AttributeConstants.materialBrand)));
//			material.setMaterialCrision(Util.formateString(helper.getIBAValue(AttributeConstants.materialCrision)));
//			material.setMaterialCategory(Util.formateString(helper.getIBAValue(AttributeConstants.materialCategory)));
//			material.setMaterialDensity(Util.formateString(helper.getIBAValue(AttributeConstants.materialDensity)));
//			material.setAttritionRate(Util.formateString(helper.getIBAValue(AttributeConstants.attritionRate)));
//			material.setMaterialUnit(Util.formateString(helper.getIBAValue(AttributeConstants.materialUnit)));
//			material.setMaterialSpec(Util.formateString(helper.getIBAValue(AttributeConstants.materialSpec)));

			material.setClph(Util.formateString(helper.getIBAValue(AttributeConstants.MATERIAL_CLPH)));
			material.setClgg(Util.formateString(helper.getIBAValue(AttributeConstants.MATERIAL_CLGG)));
			material.setJldw(Util.formateString(helper.getIBAValue(AttributeConstants.MATERIAL_JLDW)));
			material.setClbz(Util.formateString(helper.getIBAValue(AttributeConstants.MATERIAL_CLBZ)));

			material.setCsize(Util.formateString(helper.getIBAValue("CSIZE")));
			material.setMindex(Util.formateString(helper.getIBAValue("MINDEX")));
			material.setJstj(Util.formateString(helper.getIBAValue("JSTJ")));
			material.setFjtj(Util.formateString(helper.getIBAValue("FJTJ")));
			//TODO 812 设置材料属性
			material.setEnglishName(Util.formateString(helper.getIBAValue("EnglishName")));
		} else {
			material.setOid(Util.getStringOid(processMaterial));
			material.setMaterialNumber(processMaterial.getNumber());
			material.setMaterialName(processMaterial.getName());
		}
		return material;
	}

	public static List<String> getAllMPMPlant() {
	    List<String> list = new ArrayList<String>();
	    try {
            QueryResult qr = MPMResourceUtil.getAllPlant();
            while(qr.hasMoreElements()) {
                MPMPlant plant = (MPMPlant)qr.nextElement();
                list.add(plant.getName());
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
	    return list;
	}


	public static void getPdNameFolder(Folder parentFolder,PdNameType pdNameType) throws WTException {

		List<PdNameType> subTypeList = new ArrayList<PdNameType>();
		QueryResult subResult = FolderHelper.service.findSubFolders(parentFolder);
		pdNameType.setTypePath(parentFolder.getFolderPath());
		List<PdName> pdNames = getPdNameByTypeRMI(pdNameType.getTypePath());
		pdNameType.setPdNameList(pdNames);
		if (subResult!=null) {
			while (subResult.hasMoreElements()) {
				Folder subFolder = (Folder) subResult.nextElement();
				PdNameType subType = new PdNameType(subFolder.getName());
				subTypeList.add(subType);
				getPdNameFolder(subFolder, subType);
			}
		}

		pdNameType.setPdNameTypeList(subTypeList);
	}



	public static List<PdName> getPdNameByTypeRMI(String typePath) {
		GLLogger.debug("typePath----" + typePath);
		List<PdName> pdNamekList = new ArrayList<PdName>();
		try {
			WTContainer container = WTContainerUtil.getContainerByName(Constants.mpmResourceLibraryName);
			Folder folder = FolderUtil.getFolder(typePath, WTContainerRef.newWTContainerRef(container));
			QueryResult result = FolderHelper.service.findFolderContents(folder, MPMTooling.class);
			while (result.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) result.nextElement();
				PdName frock = getPdName(tooling);
				pdNamekList.add(frock);
			}
			Util.sort(pdNamekList);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return pdNamekList;
	}



	public static void main(String[] args) {
		// try {
		// SubFolder folder = (SubFolder) Util.getObjectByOid(SubFolder.class,
		// "1327827");
		// System.out.println(folder.getFolderPath());
		// } catch (WTException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		WTPart material = new WTPart();


	}
}