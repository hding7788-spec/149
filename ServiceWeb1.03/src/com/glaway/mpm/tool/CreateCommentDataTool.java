package com.glaway.mpm.tool;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import com.glaway.mpm.util.IBAHelper;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.resource.MPMPlant;
import com.ptc.windchill.mpml.resource.MPMPlantMaster;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;
import com.ptc.windchill.mpml.resource.MPMResourceGroup;
import com.ptc.windchill.mpml.resource.MPMResourceGroupMaster;
import com.ptc.windchill.mpml.resource.MPMSkill;
import com.ptc.windchill.mpml.resource.MPMSkillMaster;
import com.ptc.windchill.mpml.resource.MPMTooling;
import com.ptc.windchill.mpml.resource.MPMToolingType;

import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.config.LatestConfigSpec;

public class CreateCommentDataTool implements RemoteAccess {

	public static void main(String[] args) {
//		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
//		methodServer.setUserName("wcadmin");
//		methodServer.setPassword("wcadmin");
//		try {
//
//			methodServer.invoke("start", CreateCommentDataUtil.class.getCanonicalName(), null, null, null);
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		createFolder();
	}

	public static void start() {
		createMaterials();
		createPlant();
		createFolder();
		createGongZhuang();
		createOperation();
	}

	public static void createFolder() {
		String libraryName = "工艺知识库";
		String substr1 = "/Default/工艺模板/零件工艺";
		String substr2 = "/Default/工艺模板/装配工艺";
		String str[] = { "/天线钣金", "/天线钣金/天线", "/天线钣金/钣金", "/精密加工", "/精密加工/传动装置", "/精密加工/其他", "/精密加工/减速箱", "/精密加工/同步轮系",
				"/精密加工/天线座", "/精密加工/汇流环", "/高频", "/高频/TR组件", "/高频/分配器", "/高频/功分器", "/高频/双工器", "/高频/合成器",
				"/高频/同轴网络及电缆接头", "/高频/和差器", "/高频/喇叭", "/高频/天线", "/高频/密封窗", "/高频/开关", "/高频/插座及插头", "/高频/放大器",
				"/高频/波导网络", "/高频/滤波器", "/高频/激励器", "/高频/环形及环流器", "/高频/电桥", "/高频/移相器", "/高频/耦合器", "/高频/行及列馈", "/高频/负荷器",
				"/高频/负载", "/高频/移动交连", "/高频/隔离器", "/高频/馈源", "/高频/馈线组件" };
		try {
			WTLibrary library = getLibraryByName(libraryName);

			for (int i = 0; i < str.length; i++) {

				FolderHelper.service.createSubFolder(substr1 + str[i], WTContainerRef.newWTContainerRef(library));
				FolderHelper.service.createSubFolder(substr2 + str[i], WTContainerRef.newWTContainerRef(library));
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createMaterials() {
		String libraryName = "工艺资源库";
		String floderPath = "/Default/工艺辅料";
		String str[][] = {
				{ "铝板5A05H112", "1", "GB/13880-97", "73011301804000", "1", "板类", "δ18Χ70Χ28", "118%", "2.65kg/m³",
						"kg", "5AC5H11" },
				{ "元钢1Cr18Ni9Ti", "2", "GB1220-84GB702-86", "61202700200000", "2", "棒类", "Ø20Χ67", "118%",
						"7.85kg/ m³", "kg", "1Cr18Ni9Ti" },
				{ "铜钢T2M", "3", "GB/T1527-1997", "72020101031000", "3", "管类", "L=386", "118%", "8.9kg/m³", "kg", "T2M" },
				{ "铝型材5AC5F112XC111-29", "4", "GB/T6892-2000", "73040129134000", "4", "型材类", "L=313", "118%",
						"0.392kg/m", "kg", "5A05H112XC111-29" } };
		try {
			WTLibrary library = getLibraryByName(libraryName);
			Folder folder = FolderHelper.service.getFolder(floderPath, WTContainerRef.newWTContainerRef(library));
			for (int i = 0; i < str.length; i++) {
				MPMProcessMaterial material = MPMProcessMaterial.newMPMProcessMaterial();
				material.setName(str[i][0]);
				material.setContainer(library);
				FolderHelper.assignFolder(material, folder);
				PersistenceHelper.manager.save(material);
				IBAHelper.setIBAStringValue(material, "materialCategory", str[i][1]);
				IBAHelper.setIBAStringValue(material, "materialCrision", str[i][2]);
				IBAHelper.setIBAStringValue(material, "materialCode", str[i][3]);
				IBAHelper.setIBAStringValue(material, "materialState", str[i][4]);
				IBAHelper.setIBAStringValue(material, "computeType", str[i][5]);
				IBAHelper.setIBAStringValue(material, "materialSpec", str[i][6]);
				IBAHelper.setIBAStringValue(material, "materialQuotiety", str[i][7]);
				IBAHelper.setIBAStringValue(material, "materialDensity", str[i][8]);
				IBAHelper.setIBAStringValue(material, "materialUnit", str[i][9]);
				IBAHelper.setIBAStringValue(material, "materialBrand", str[i][10]);

			}

		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createGongZhuang() {
		String libraryName = "工艺资源库";
		String floderPath = "/Default/工装";
		String str[][] = { { "AL017.0099", "蜡模" }, { "AL017.0110", "灌注模" }, { "AL017.0114", "失蜡模" },
				{ "AL018.0050", "腔体压铸模" }, { "AL024.0001", "外罩成型模" }, { "AL024.0002", "包角成型模" },
				{ "AL043.0001", "滚轮" }, { "AL056.0013", "锻模" }, { "AL060.0061", "陶瓷模" }, { "AL061.0002", "压模" },
				{ "AL061.0060", "压胶模" }, { "AL061.0127", "挤胶模" }, { "AL061.0698", "橡胶模" }, { "AL061.0929", "注射成型压模" },
				{ "AL061.0977", "注射成型模" }, { "AL061.1079", "注射模" }, { "AL061.1081", "线圈灌注模" }, { "AL061.1234", "发泡模" },
				{ "AL061.1241", "环氧灌注模" }, { "AL061.1286", "环氧树脂灌注模" }, { "AL061.1292", "压塑模" },
				{ "AL061.1293", "汇流条封装模" }, { "AL061.1336", "玻璃钢板压制模" }, { "AL061.1354", "陶瓷压铸模" },
				{ "AL061.1364", "保护罩" }, { "AL061.1365", "热压模" }, { "AL061.1367", "调频天线发泡模" },
				{ "AL061.1368", "天线发泡模" }, { "AL061.1369", "九频道天线发泡模" }, { "AL061.1370", "四频道天线发泡模" },
				{ "AL061.1375", "平衡器发泡模" }, { "AL061.1437", "玻璃钢保护罩成型模" }, { "AL065.0001", "行馈源包封固化夹具" } };
		try {
			WTLibrary library = getLibraryByName(libraryName);
			Folder folder = FolderHelper.service.getFolder(floderPath, WTContainerRef.newWTContainerRef(library));
			for (int i = 0; i < str.length; i++) {
				MPMTooling tooling = MPMTooling.newMPMTooling();
				tooling.setNumber(str[i][0]);
				tooling.setName(str[i][1]);
				tooling.setCategory(MPMToolingType.FIXTURE);
				tooling.setContainer(library);
				FolderHelper.assignFolder(tooling, folder);
				PersistenceHelper.manager.save(tooling);
			}

		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createOperation() {
		String libraryName = "工艺资源库";
		String floderPath = "/Default/工序";
		String str[] = { "钳", "备料", "齐套", "铣", "涂覆", "电镀", "装配", "热处理", "冲", "车", "制标识", "完全外协", "涂复", "油漆", "外协",
				"准备", "刨", "外协加工", "清洗", "线切割", "钻", "包封", "底片制作", "铭牌制作", "电装", "胶木化", "机械加工", "图形转移", "制模板", "蚀刻",
				"涂漆", "印线号", "数控铣", "调试", "冷作", "焊接", "包装", "印字", "镗", "刻字", "电缆制作", "数控钻孔", "电缆加工", "焊", "孔金属化", "检漏",
				"检漏" };
		try {
			WTLibrary library = getLibraryByName(libraryName);
			Folder folder = FolderHelper.service.getFolder(floderPath, WTContainerRef.newWTContainerRef(library));
			TypeDefinitionReference typeRef = TypedUtilityServiceHelper.service
					.getTypeDefinitionReference("com.nriet.Operation");
			for (int i = 0; i < str.length; i++) {
				MPMOperation mpmOperation = MPMOperation.newMPMOperation();
				mpmOperation.setName(str[i]);
				mpmOperation.setContainer(library);
				FolderHelper.assignFolder(mpmOperation, folder);
				mpmOperation.setTypeDefinitionReference(typeRef);
				PersistenceHelper.manager.save(mpmOperation);

			}

		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createPlant() {
		String libraryName = "工艺资源库";
		String folderPath = "/Default/制造单位";
		String str[] = { "外", "外协", "料", "质", "艺", "装", "新一厂", "新二厂", "新三厂", "新四厂", "微", "木", "一部", "二部", "三部", "四部",
				"五部", "六部", "七部", "八部", "九部" };

		String floderPath1 = "/Default/工种";
		String str1[][] = { { "完全外协", "外协加工", "钳", "电镀" }, { "齐套", "备料", "领料", "外协" }, { "备料", "水切割", "拉拔", "下料" },
				{ "检验", "筛选", "按图检验", "环试" }, { "准备", "成形", "齐套", "铺层" }, { "调试", "电调", "电测", "复调" },
				{ "钳", "铣", "齐套", "车" }, { "齐套", "钳", "装配", "制标识" }, { "钳", "铣", "涂覆", "电镀" },
				{ "包装", "机械加工", "图形转移", "制模版" }, { "齐套", "准备", "装配", "清洗" }, { "装配", "齐套", "木", "基建" },
				{ "调试", "电调", "电测", "测试" }, { "调试", "电调", "电测", "测试" }, { "调试", "电调", "电测", "测试" },
				{ "调试", "电调", "电测", "测试" }, { "调试", "电调", "电测", "测试" }, { "调试", "电调", "电测", "测试" },
				{ "调试", "电调", "电测", "测试" }, { "调试", "电调", "电测", "测试" }, { "调试", "电调", "电测", "测试" } };

		String floderPath2 = "/Default/工位";
		String str2[][] = { { "工位1", "工位2" }, { "工位3", "工位4" }, { "工位5", "工位6" }, { "工位7", "工位8" }, { "工位9", "工位10" },
				{ "工位11", "工位12" }, { "工位13", "工位14" }, { "工位15", "工位16" }, { "工位17", "工位18" }, { "工位19", "工位20" },
				{ "工位21", "工位22" }, { "工位23", "工位24" }, { "工位25", "工位26" }, { "工位27", "工位28" }, { "工位29", "工位30" },
				{ "工位31", "工位32" }, { "工位33", "工位34" }, { "工位35", "工位36" }, { "工位37", "工位38" }, { "工位39", "工位40" },
				{ "工位41", "工位42" } };

		try {
			WTLibrary library = getLibraryByName(libraryName);
			Folder folder = FolderHelper.service.getFolder(folderPath, WTContainerRef.newWTContainerRef(library));
			Folder folder1 = FolderHelper.service.getFolder(floderPath1, WTContainerRef.newWTContainerRef(library));
			Folder folder2 = FolderHelper.service.getFolder(floderPath2, WTContainerRef.newWTContainerRef(library));
			for (int i = 0; i < str.length; i++) {
				MPMPlant plant = MPMPlant.newMPMPlant();
				plant.setName(str[i]);
				plant.setContainer(library);
				FolderHelper.assignFolder(plant, folder);
				PersistenceHelper.manager.save(plant);

			}
			for (int i = 0; i < str1.length; i++) {
				String str11[] = str1[i];
				MPMPlant plant = getMPMPlantByName(str[i]);
				System.out.println(plant.getName());
				for (int j = 0; j < str11.length; j++) {
					MPMSkillMaster master = getMPMSkillMasterByName(str11[j]);

					if (master == null) {
						MPMSkill skill = MPMSkill.newMPMSkill();
						skill.setName(str11[j]);
						skill.setContainer(library);
						FolderHelper.assignFolder(skill, folder1);
						PersistenceHelper.manager.save(skill);
						master = (MPMSkillMaster) skill.getMaster();

					}
					System.out.println(master.getName());
					createWTPartUsageLink(plant, master);
				}
			}

			for (int i = 0; i < str2.length; i++) {
				String str22[] = str2[i];
				MPMPlant plant = getMPMPlantByName(str[i]);
				System.out.println(plant.getName());
				for (int j = 0; j < str22.length; j++) {
					MPMResourceGroupMaster master = getMPMResourceGroupMasterByName(str22[j]);

					if (master == null) {
						MPMResourceGroup group = MPMResourceGroup.newMPMResourceGroup();
						group.setName(str22[j]);
						group.setContainer(library);
						FolderHelper.assignFolder(group, folder2);
						PersistenceHelper.manager.save(group);
						master = (MPMResourceGroupMaster) group.getMaster();

					}
					System.out.println(master.getName());
					createWTPartUsageLink(plant, master);
				}
			}

		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static WTLibrary getLibraryByName(String name) throws WTException {
		WTLibrary library = null;

		QuerySpec qs = new QuerySpec(WTLibrary.class);
		qs.appendWhere(new SearchCondition(WTLibrary.class, WTLibrary.NAME, SearchCondition.EQUAL, name),
				new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			library = (WTLibrary) qr.nextElement();
		}
		return library;
	}

	public static MPMSkillMaster getMPMSkillMasterByName(String name) throws WTException {
		MPMSkillMaster mpmSkillMaster = null;
		QuerySpec querySpec = new QuerySpec(MPMSkillMaster.class);
		querySpec.appendWhere(new SearchCondition(MPMSkillMaster.class, "name", SearchCondition.EQUAL, name),
				new int[] { 0 });
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			mpmSkillMaster = (MPMSkillMaster) queryResult.nextElement();
		}
		return mpmSkillMaster;
	}

	public static WTPartUsageLink createWTPartUsageLink(WTPart parentPart, WTPartMaster childPartMaster)
			throws WTException {
		WTPartUsageLink partUsageLink = WTPartUsageLink.newWTPartUsageLink(parentPart, childPartMaster);
		PersistenceServerHelper.manager.insert(partUsageLink);
		return partUsageLink;

	}

	public static MPMPlant getMPMPlantByName(String name) throws WTException {
		MPMPlant plant = null;

		QuerySpec querySpec = new QuerySpec(MPMPlant.class);
		querySpec.appendWhere(new SearchCondition(MPMPlant.class, WTPart.NAME, SearchCondition.EQUAL, name),
				new int[] { 0 });
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			plant = (MPMPlant) queryResult.nextElement();
		}
		return plant;
	}

	public static MPMResourceGroupMaster getMPMResourceGroupMasterByName(String name) throws WTException {
		MPMResourceGroupMaster mpmResourceGroupMaster = null;

		QuerySpec querySpec = new QuerySpec(MPMResourceGroupMaster.class);
		querySpec.appendWhere(new SearchCondition(MPMResourceGroupMaster.class, "name", SearchCondition.EQUAL, name),
				new int[] { 0 });
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			mpmResourceGroupMaster = (MPMResourceGroupMaster) queryResult.nextElement();
		}
		return mpmResourceGroupMaster;
	}
}
