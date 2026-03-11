package com.glaway.mpm.wcIntf;

import com.glaway.mpm.model.*;
import com.glaway.mpm.resource.ResourceCache;
import com.glaway.mpm.util.IntfUtil;
import com.glaway.mpm.visual.log.VaLogger;
import wt.method.RemoteAccess;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ylshao
 * @ClassName: ProcessEditorToWCIntf
 * @Description:
 * @date 2012-11-28
 *
 */
public class ResourceIntf implements RemoteAccess {
	private static VaLogger logger = VaLogger.getLogger(ResourceIntf.class);

	public static String getDocStyleByDocOid(String docOid) {
		Class<?>[] cls = new Class[]{String.class};
		Object[] obj = new Object[]{docOid};
		return (String) IntfUtil.getPeRemoteMethodInvoke("getDocStyleByDocOid", cls, obj);
	}
	
	public static boolean getRenwuLeiXing(String workItemOid) {
		Class<?>[] cls = new Class[]{String.class};
		Object[] obj = new Object[]{workItemOid};
		return (Boolean) IntfUtil.getPeRemoteMethodInvoke("getRenwuLeiXing", cls, obj);
	}
	
	public static String getGengGaiLeiXing(String changeOrderOid) {
		Class<?>[] cls = new Class[]{String.class};
		Object[] obj = new Object[]{changeOrderOid};
		return (String) IntfUtil.getPeRemoteMethodInvoke("getECNType", cls, obj);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> getPdNameDescribe() {
		Class<?>[] cls = new Class[]{};
		Object[] obj = new Object[]{};
		return (Map<String, String>) IntfUtil.getPeRemoteMethodInvoke("getPdNameDescribe", cls, obj);
	}
	
	/**
	 * 获取工序名称列表
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 *
	 */
	public static Map<String, String> getProcessStepName() {
		if (ResourceCache.processStepNames != null) {
			return ResourceCache.processStepNames;
		} else {
			HashMap<String, String> pdNames = (HashMap<String, String>) remoteMethodInvoke(
					"getProcessStepNamesRMI", new Class[] {}, new Object[] {});
			ResourceCache.processStepNames = pdNames;
//			logger.debug("getProcessStepName" + pdNames);
			return pdNames;

		}
	}

	public static PdNameType get812AllPdNameType(){
		if (ResourceCache.pdNameType != null) {
			return ResourceCache.pdNameType;
		} else {
			PdNameType pdNameType = (PdNameType) remoteMethodInvoke("get812AllPdNameTypeRMI", new Class[] {}, new Object[] {});
			ResourceCache.pdNameType = pdNameType;
//			logger.debug("get812AllPdNameTypeRMI" + pdNameType);
			return pdNameType;

		}
	}

	/**
	 * 获取查询Frock条件
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 */
	public static String[] getFkSearchCondition() {
		return new String[] { "工装", "工具", "量具","刀具" };
	}

	/**
	 * 获取查询设备结果
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 */
	public static List<Equipment> getEquipments(String number, String name, String mindex) {
		return (List<Equipment>) remoteMethodInvoke("getEquipmentsRMI",
				new Class[] { String.class, String.class, String.class }, new Object[] { number, name, mindex });
	}

	public static List<Equipment> getEquipments(Map<String, String> map, String library) {
		return (List<Equipment>) remoteMethodInvoke("getEquipments",
				new Class[] { Map.class, String.class }, new Object[] { map, library });
	}

	/**
	 * 查询标准仪器仪表
	 *
	 * @param number
	 * @param name
	 * @return
	 */
	public static List<Dashboard> getSDashboards(String number, String name) {
		return (List<Dashboard>) remoteMethodInvoke("getSDashboards",
				new Class[] { String.class, String.class }, new Object[] {
						number, name });
	}

	public static List<Dashboard> getSDashboards2(Map<String, String> map, String type) {
		logger.debug("map:" + map);
		return (List<Dashboard>) remoteMethodInvoke("getSDashboards",
				new Class[] { Map.class, String.class }, new Object[] { map, type });
	}

	/**
	 * 查询非标准仪器仪表
	 *
	 * @param number
	 * @param name
	 * @return
	 */
	public static List<UnSDashboard> getUnSDashboards(String number, String name) {
		return (List<UnSDashboard>) remoteMethodInvoke("getUnSDashboards",
				new Class[] { String.class, String.class }, new Object[] {
						number, name });
	}

	public static List<UnSDashboard> getUnSDashboards2(Map<String, String> map, String type) {
		logger.debug("map:" + map);
		return (List<UnSDashboard>) remoteMethodInvoke("getUnSDashboards",
				new Class[] { Map.class, String.class }, new Object[] { map, type });
	}

	/**
	 * 获取查询工装结果
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 */
	public static List<Object> getFrocks(String number, String name,
			String index) {
		return (List<Object>) remoteMethodInvoke("getFrocksRMI", new Class[] {
				String.class, String.class, String.class }, new Object[] {
				number, name, index });

	}

	/**
	 * 获取查询工位结果
	 *
	 * @author zhuhao
	 * @date 2017-10-17
	 * @return
	 */
	public static List<WorkPlace> getWorkPlace(Map<String, String> map, String type) {
		return (List<WorkPlace>) remoteMethodInvoke("getWorkPlaceRMI", new Class[] {
				Map.class, String.class }, new Object[] { map, type });
	}

	public static List<Tool> getMeasures(Map<String, String> map, String type) {
		logger.debug("map:" + map);
		return (List<Tool>) remoteMethodInvoke("getMeasures",
				new Class[] { Map.class, String.class }, new Object[] { map, type });
	}

	public static List<KnifeTool> getKnifes(Map<String, String> map, String type) {
		logger.debug("map:" + map);
		return (List<KnifeTool>) remoteMethodInvoke("getKnifes",
				new Class[] { Map.class, String.class }, new Object[] { map, type });
	}

	/**
	 * 根据工装编号搜索工装
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 */
	public static Frock getFrockByNumber(String number) {
		return (Frock) remoteMethodInvoke("getFrockByNumberRMI",
				new Class[] { String.class }, new Object[] { number });

	}

	/**
	 * 获取工装申请卡的编号
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 */
	public static Map getNewFrockInfo() {
		Map map = (Map) remoteMethodInvoke("getNewFrockInfoRMI",
				new Class[] {}, new Object[] {});
		logger.debug("map= " + map);

		return (Map) map.get("number");
	}

	/**
	 * @Description:获取工装申请卡的信息
	 * @param @param text
	 * @return void
	 */
	public static Map showFrockCard(Map map) {
		logger.debug("map= " + map);
		Map returnMap = (Map) remoteMethodInvoke("showFrockCardRMI",
				new Class[] { Map.class }, new Object[] { map });
		logger.debug("returnMap= " + returnMap);
		return returnMap;
	}

	/**
	 * @Description:根据输入的编号搜索工装申请卡的编号
	 */
	public static List<Map> showAllFrockCard(Map<String, String> map) {
		logger.debug("map= " + map);
		List<Map> returnMap = (List<Map>) remoteMethodInvoke(
				"showAllFrockCardRMI", new Class[] { Map.class },
				new Object[] { map });
		logger.debug("returnMap= " + returnMap);
		return returnMap;
	}

	/**
	 * 申请工装
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 */
	public static Frock createFrockCard(Map map) {
		logger.debug("map= " + map);
		Frock frock = (Frock) remoteMethodInvoke("createFrockCardRMI",
				new Class[] { Map.class }, new Object[] { map });
		return frock;
	}

	/**
	 * 获取查询Material结果
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @param type
	 *            1：原始材料 2：标准件 3：外购件
	 * @return
	 */
	public static List<Material> getMaterials(Map<String, String> map,
			String type) {
		logger.debug("map:" + map);
		return (List<Material>) remoteMethodInvoke("getMaterialsRMI",
				new Class[] { Map.class, String.class }, new Object[] { map,
						type });
	}

	/**
	 * 获取计算材料定额MaterialCal
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 */
	public static List<MaterialCal> getMaterialCals() {
		if (ResourceCache.materialCals == null) {
			ResourceCache.materialCals = (List<MaterialCal>) remoteMethodInvoke(
					"getMaterialQuotaRMI", new Class[] {}, new Object[] {});
		}
		logger.debug("ResourceCache.materialCals= "
				+ ResourceCache.materialCals);
		return ResourceCache.materialCals;
	}

	/**
	 * 初始化数据
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 *
	 */
	public static void getInitData() {
		if (ResourceCache.workShops == null
				|| ResourceCache.workShops.size() == 0
				|| ResourceCache.shopTypes == null
				|| ResourceCache.shopTypes.size() == 0) {
			List<Object> list = (List<Object>) remoteMethodInvoke(
					"getAllWorkShopsRMI", null, null);
			if (list != null && list.size() == 2) {
				ResourceCache.workShops = (List<WorkShop>) list.get(0);
				ResourceCache.shopTypes = (List<ShopType>) list.get(1);
//				ResourceCache.allShopTypes = (Map<WorkShop, List<ShopType>>) list.get(2);
				logger.debug(ResourceCache.shopTypes);
			}
		}

	}

	/**
	 * 获得所有的制造单位
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 *
	 */
	public static Map<String, String> getWorkShops() {
		if(ResourceCache.workShops == null || ResourceCache.workShops.isEmpty()) {
			getInitData();
		}

		Map<String, String> workshopMap = new LinkedHashMap<String, String>();
		if(ResourceCache.workShops != null){
			for (WorkShop wst : ResourceCache.workShops) {
				workshopMap.put(wst.getNumber(), wst.getName());
			}
		}
//		logger.debug("workshopMap======================" + workshopMap);
		return workshopMap;
	}

	/**
	 * 获取制造单位下的工序名称
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @param workShopNumber
	 * @return
	 *
	 */
	public static Map<String, String> getPdNames(String workShopNumber) {
		if(ResourceCache.workShops == null || ResourceCache.workShops.isEmpty()) {
			getInitData();
		}
		Map<String, String> pdNameMap = new HashMap<String, String>();
		for (WorkShop workShops : ResourceCache.workShops) {
			if (workShops.getNumber().equals(workShopNumber)) {
				List<PdName> pdNames = workShops.getPdNames();
				if (pdNames != null) {
					for (PdName pdName : pdNames) {
						pdNameMap.put(pdName.getName(), pdName.getShortcut());
					}
				}

			}
		}
		return pdNameMap;
	}

	/**
	 * 获取制造单位下的工序名称集合包括工种
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @param workShopNumber
	 * @return
	 * @return
	 *
	 */
	public static List<PdName> getPdNameList(String workShopNumber) {
		if(ResourceCache.workShops == null || ResourceCache.workShops.isEmpty()) {
			getInitData();
		}
		for (WorkShop workShops : ResourceCache.workShops) {
			if (workShops.getNumber().equals(workShopNumber)) {
				return workShops.getPdNames();
			}
		}
		return null;
	}

	/**
	 * @Description: 获取一个工序名称下所有工种列表接口
	 * @param @param workShopNumber
	 * @param @return
	 * @return HashMap<String,String>
	 * @throws
	 */
	public static HashMap<String, String> getWorkTypeByPdName(String pdName,
			String workShopNumber) {
		logger.debug("getWorkTypeByPdName start..." + pdName);
		HashMap<String, String> workTypeMap = new HashMap<String, String>();
		List<PdName> pdNames = getPdNameList(workShopNumber);
		logger.debug("ResourceCache.pdNames=" + pdNames);
		if (pdNames != null) {
			for (PdName temp : pdNames) {
				if (temp.getName().equals(pdName)) {
					List<ShopType> shopTypes = temp.getShopTypes();
					if (shopTypes != null) {
						for (ShopType shopType : shopTypes) {
							workTypeMap.put(shopType.getNumber(),
									shopType.getName());
						}
					}
					break;
				}
			}
		}
		logger.debug("workTypeMap=" + workTypeMap);
		return workTypeMap;
	}

	/**
	 * 获得制造单位下面的工位
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @param workShopNumber
	 * @return
	 *
	 */
	public static Map<String, String> getWorkSpaces(String workShopNumber) {
		if(ResourceCache.workShops == null || ResourceCache.workShops.isEmpty()) {
			getInitData();
		}
		Map<String, String> workSpacesMap = new HashMap<String, String>();
		for (WorkShop ws : ResourceCache.workShops) {
			if (ws.getNumber().equals(workShopNumber)) {
				if (ws.getWorkSpaces() != null) {
					for (WorkSpace st : ws.getWorkSpaces()) {
						workSpacesMap.put(st.getNumber(), st.getName());
					}
				}
			}
		}
		return workSpacesMap;
	}

	/**
	 * 获得制造单位下面的工种
	 * @param workShopNumber
	 * @return
	 */
	public static Map<String, String> getSkill(String workShopNumber) {
		if(ResourceCache.workShops == null || ResourceCache.workShops.isEmpty()) {
			getInitData();
		}
		Map<String, String> skillMap = new HashMap<String, String>();
		for (WorkShop ws : ResourceCache.workShops) {
			if (ws.getNumber().equals(workShopNumber)) {
				if (ws.getSkillList() != null) {
					for (Skill skill : ws.getSkillList()) {
						skillMap.put(skill.getNumber(), skill.getName());
					}
				}
			}
		}
		return skillMap;
	}

	public static EpType getAllEquipments() {
//		getInitData();
//		List<EpType> epTypes = new ArrayList<EpType>();
//		for (WorkShop workShop : ResourceCache.workShops) {
//			EpType eptype = new EpType(workShop.getName());
//			for (EpType type : workShop.getEpTypes()) {
//				if (type.getEquipments() != null
//						&& type.getEquipments().size() != 0) {
//					eptype.setEquipments(type.getEquipments());
//				} else {
//					eptype.setEpTypes(type.getEpTypes());
//				}
//			}
//			epTypes.add(eptype);
//		}
//		return new EpType("设备", null, epTypes);
		return get812AllEquipments();
	}

	public static EpType get812AllEquipments() {
		EpType types = null;
		if (ResourceCache.epType == null) {
			types = (EpType) remoteMethodInvoke("get812AllEquipmentsRMI", new Class[] {}, new Object[] {});
			ResourceCache.epType = types;
		} else {
			types = ResourceCache.epType;
		}
		return types;
	}

	public static DashboardType getAllDashboards() {
		DashboardType types = null;
		if (ResourceCache.dashboardType == null) {
			types = (DashboardType) remoteMethodInvoke("getAllDashboards", new Class[] {}, new Object[] {});
			ResourceCache.dashboardType = types;
		} else {
			types = ResourceCache.dashboardType;
		}
		return types;
	}

	public static UnSDashboardType getAllUnSDashboards() {
		UnSDashboardType types = null;
		if (ResourceCache.unsdashboardType == null) {
			types = (UnSDashboardType) remoteMethodInvoke("getAllUnSDashboards", new Class[] {}, new Object[] {});
			ResourceCache.unsdashboardType = types;
		} else {
			types = ResourceCache.unsdashboardType;
		}
		return types;
	}

	public static FkType getAllFrocks() {
		FkType types = null;
		if (ResourceCache.fkType == null) {
			types = (FkType) remoteMethodInvoke("getAllFrocksRMI", new Class[] {}, new Object[] {});
			ResourceCache.fkType = types;
		} else {
			types = ResourceCache.fkType;
		}
		return types;
	}

	public static ToolType getAllTools() {
//		getInitData();
//		List<ToolType> toolTypes = new ArrayList<ToolType>();
//		for (WorkShop workShop : ResourceCache.workShops) {
//			ToolType toolType = new ToolType(workShop.getName());
//			// logger.debug(workShop.getName());
//			for (ToolType type : workShop.getToolTypes()) {
//				// logger.debug(type.getTools());
//
//				if (type.getToolTypes() == null
//						|| type.getToolTypes().size() == 0) {
//					toolType.setTools(type.getTools());
//				} else {
//					toolType.setToolTypes(type.getToolTypes());
//				}
//			}
//			// logger.debug(toolType);
//			toolTypes.add(toolType);
//		}
//		// printToolTypes(toolTypes);
//		return new ToolType("工具", null, toolTypes);
		return get812AllTools();

	}

	public static ToolType get812AllTools() {

		ToolType types = null;
		if (ResourceCache.toolType == null) {
			types = (ToolType) remoteMethodInvoke("get812AllToolsRMI", new Class[] {}, new Object[] {});
			ResourceCache.toolType = types;
		} else {
			types = ResourceCache.toolType;
		}
		return types;

	}

	public static ToolType get812AllMeasures() {

		ToolType types = null;
		if (ResourceCache.measuresType == null) {
			types = (ToolType) remoteMethodInvoke("get812AllMeasuresRMI", new Class[] {}, new Object[] {});
			ResourceCache.measuresType = types;
		} else {
			types = ResourceCache.measuresType;
		}
		return types;

	}

	public static ToolType getAllMeasures() {
//		getInitData();
//		List<ToolType> toolTypes = new ArrayList<ToolType>();
//		for (WorkShop workShop : ResourceCache.workShops) {
//			ToolType toolType = new ToolType(workShop.getName());
//			// logger.debug(workShop.getName());
//			for (ToolType type : workShop.getMeasureTypes()) {
//				// logger.debug(type.getTools());
//
//				if (type.getToolTypes() == null
//						|| type.getToolTypes().size() == 0) {
//					toolType.setTools(type.getTools());
//				} else {
//					toolType.setToolTypes(type.getToolTypes());
//				}
//			}
//			// logger.debug(toolType);
//			toolTypes.add(toolType);
//		}
//		// printToolTypes(toolTypes);
//		return new ToolType("量具", null, toolTypes);
		return get812AllMeasures();
	}

	// public static void printToolTypes(List<ToolType> toolTypes) {
	// if (toolTypes != null) {
	// for (ToolType temp : toolTypes) {
	// logger.debug(temp.getName());
	// printTools(temp.getTools());
	// printToolTypes(temp.getToolTypes());
	// }
	// }
	// }

	// private static void printTools(List<Tool> tools) {
	// if (tools != null) {
	// for (Tool temp : tools) {
	// logger.debug(temp);
	// }
	// }
	// }

	// public static ToolType getAllTools() {
	// ToolType types = null;
	// if (ResourceCache.toolType == null) {
	// types = (ToolType) remoteMethodInvoke("getAllToolsRMI",
	// new Class[] {}, new Object[] {});
	// ResourceCache.toolType = types;
	// } else {
	// types = ResourceCache.toolType;
	// }
	// return types;
	//
	// }

	public static MtType getMaterialTypes() {
		MtType types = null;
		if (ResourceCache.mtType == null) {
			types = (MtType) remoteMethodInvoke("getMaterialTypesRMI", new Class[] {}, new Object[] {});
			ResourceCache.mtType = types;
		} else {
			types = ResourceCache.mtType;
		}
		logger.debug("MtType=" + types);
		return types;
	}

	public static List<Material> getMaterialsByType(String typePath) {
		logger.debug("typePath==" + typePath);
		return (List<Material>) remoteMethodInvoke("getMaterialsByTypeRMI",
				new Class[] { String.class }, new Object[] { typePath });
	}

	public static KtType getAllKnifeTools() {
		KtType types = null;
		if (ResourceCache.ktType == null) {
			types = (KtType) remoteMethodInvoke("getAllKnifeToolsRMI", new Class[] {}, new Object[] {});
			ResourceCache.ktType = types;
		} else {
			types = ResourceCache.ktType;
		}
		logger.debug("types=" + types);
		return types;
	}

	/**
	 * @Description:切换树节点获取节点的信息
	 */
	public static List getResourceObject(Map map) {
		logger.debug("map= " + map);
		return (List) remoteMethodInvoke("getResourceObjectRMI",
				new Class[] { Map.class }, new Object[] { map });
	}

	/**
	 * @Description: 工艺资源是否废弃
	 */
	public static Boolean isResourceAbandon(String type, String oid) {
		logger.debug("type= " + type + "   oid= " + oid);
		return (Boolean) remoteMethodInvoke("isAbandonRMI", new Class[] {
				String.class, String.class }, new Object[] { type, oid });
	}

	/**
	 * @Description: 工装是否归档
	 */
	public static Boolean isFrockCompleted(String oid) {
		logger.debug("oid= " + oid);
		return (Boolean) remoteMethodInvoke("isFrockCompletedRMI",
				new Class[] { String.class }, new Object[] { oid });
	}

	/**
	 * @Description: 根据oid获取图片
	 * @param @return
	 * @return EpType
	 */
	public static byte[] getResourceImage(String oid) {
		logger.debug("oid= " + oid);
		byte[] bytes = (byte[]) remoteMethodInvoke("getResourceImageRMI",
				new Class[] { String.class }, new Object[] { oid });
		return bytes;
	}

	/**
	 *
	 *
	 * @Title: remoteMethodInvoke
	 * @Description:
	 * @return Object
	 * @throws
	 */
	public static Object remoteMethodInvoke(String mentodName,
			Class[] classArray, Object[] objectArray) {
		return IntfUtil.getRemoteMethodInvoke(mentodName, classArray,
				objectArray);
	}

    public static String getEnglishNameByGxmc(String name) {

        return (String) remoteMethodInvoke("getEnglishNameByGxmc", new Class[] {String.class}, new Object[] {name});

    }


	/**
	 * 从config_149.xml获得所有车间
	 *
	 * @author cjh
	 * @date 2025-1-22
	 * @return
	 */
	public static List<String> getAllDepts() {
		if(ResourceCache.depts == null || ResourceCache.depts.isEmpty()) {
			List<String> list = (List<String>) remoteMethodInvoke("getAllDeptsRMI", null, null);
			ResourceCache.depts = list;
		}
		return ResourceCache.depts;
	}


}
