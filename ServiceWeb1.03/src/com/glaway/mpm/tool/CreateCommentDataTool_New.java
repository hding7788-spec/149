package com.glaway.mpm.tool;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
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
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMResourceUtil;
import com.glaway.mpm.util.TypeUtil;
import com.ptc.windchill.mpml.resource.MPMPlant;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;
import com.ptc.windchill.mpml.resource.MPMSkill;
import com.ptc.windchill.mpml.resource.MPMSkillMaster;
import com.ptc.windchill.mpml.resource.MPMTooling;
import com.ptc.windchill.mpml.resource.MPMToolingMaster;
import com.ptc.windchill.mpml.resource.MPMWorkCenter;
import com.ptc.windchill.mpml.resource.MPMWorkCenterMaster;

public class CreateCommentDataTool_New implements RemoteAccess {
	private static final String CLASSNAME = CreateCommentDataTool_New.class.getName();

	public static void main(String[] args) throws WTException {
		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		methodServer.setUserName("wcadmin");
		methodServer.setPassword("wcadmin");
		try {

			methodServer.invoke("start", CreateCommentDataTool_New.class.getCanonicalName(), null, null, null);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void start() {
		try {

			// createFolder();
			createMaterials();
			createFrock();
			createTool();
			createKnife();
			createMeasure();
			createPlant();
			createCSAndStepNames();
		} catch (WTException e) {
			e.printStackTrace();
		}
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
		String folder = "/Default/工艺辅料";
		String str[][][] = {
				{
						{ "com.ptc.windchill.mpml.resource.MPMProcessMaterial|com.nriet.电子材料", "" },
						{ "", "铝板5A05H112", "1", "GB/13880-97", "73011301804000", "1", "板类", "δ18Χ70Χ28", "118%",
								"2.65kg/m³", "kg", "5AC5H11" } },
				{
						{ "com.ptc.windchill.mpml.resource.MPMProcessMaterial|com.nriet.电子材料", "" },
						{ "", "元钢1Cr18Ni9Ti", "2", "GB1220-84GB702-86", "61202700200000", "2", "棒类", "Ø20Χ67", "118%",
								"7.85kg/ m³", "kg", "1Cr18Ni9Ti" } },
				{
						{ "com.ptc.windchill.mpml.resource.MPMProcessMaterial|com.nriet.电子材料", "" },
						{ "", "铜钢T2M", "3", "GB/T1527-1997", "72020101031000", "3", "管类", "L=386", "118%", "8.9kg/m³",
								"kg", "T2M" } },
				{
						{ "com.ptc.windchill.mpml.resource.MPMProcessMaterial|com.nriet.电子材料", "" },
						{ "", "铝型材5AC5F112XC111-29", "4", "GB/T6892-2000", "73040129134000", "4", "型材类", "L=313",
								"118%", "0.392kg/m", "kg", "5A05H112XC111-29" } } };
		try {
			WTLibrary library = getLibraryByName(libraryName);
			for (int i = 0; i < str.length; i++) {
				String folderPath = "";
				String objectType = "";
				for (int j = 0; j < str[i].length; j++) {
					if (j == 0) {
						objectType = str[i][j][0];
						folderPath = folder + str[i][j][1];
					} else {
						MPMProcessMaterial material = MPMResourceUtil.createProcessMaterial(str[i][j][0], str[i][j][1],
								library, folderPath, objectType,"");
						IBAHelper.setIBAStringValue(material, "materialCategory", str[i][j][2]);
						IBAHelper.setIBAStringValue(material, "materialCrision", str[i][j][3]);
						IBAHelper.setIBAStringValue(material, "materialCode", str[i][j][4]);
						IBAHelper.setIBAStringValue(material, "materialState", str[i][j][5]);
						IBAHelper.setIBAStringValue(material, "computeType", str[i][j][6]);
						IBAHelper.setIBAStringValue(material, "materialSpec", str[i][j][7]);
						IBAHelper.setIBAStringValue(material, "materialQuotiety", str[i][j][8]);
						IBAHelper.setIBAStringValue(material, "materialDensity", str[i][j][9]);
						IBAHelper.setIBAStringValue(material, "materialUnit", str[i][j][10]);
						IBAHelper.setIBAStringValue(material, "materialBrand", str[i][j][11]);
					}
				}
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

	public static void createFrock() {
		String libraryName = "工艺资源库";
		String folder = "/Default/工装";
		String str[][][] = {
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Frock", "" }, { "AL017.0099", "蜡模" },
						{ "AL024.0002", "包角成型模" }, { "AL060.0061", "陶瓷模" }, { "AL061.0698", "橡胶模" } },
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Frock", "" }, { "AL017.0110", "灌注模" },
						{ "AL043.0001", "滚轮" }, { "AL061.0002", "压模" }, { "AL061.0929", "注射成型压模" } },
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Frock", "" }, { "AL017.0114", "失蜡模" },
						{ "AL056.0013", "锻模" }, { "AL061.0060", "压胶模" }, { "AL061.0977", "注射成型模" } },
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Frock", "" }, { "AL018.0050", "腔体压铸模" },
						{ "AL024.0001", "外罩成型模" }, { "AL061.0127", "挤胶模" }, { "AL061.1079", "注射模" } } };
		try {
			WTLibrary library = getLibraryByName(libraryName);
			for (int i = 0; i < str.length; i++) {
				String folderPath = "";
				String objectType = "";
				for (int j = 0; j < str[i].length; j++) {
					if (j == 0) {
						objectType = str[i][j][0];
						folderPath = folder + str[i][j][1];
					} else {
						MPMTooling frock = MPMResourceUtil.createTooling(str[i][j][0], str[i][j][1], library,
								folderPath, objectType,"");
					}
				}
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

	public static void createTool() {
		String libraryName = "工艺资源库";
		String folder = "/Default/工具";
		String str[][][] = {
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Tool", "" }, { "", "工具1" }, { "", "工具2" },
						{ "", "工具3" }, { "", "工具4" } },
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Tool", "" }, { "", "工具5" }, { "", "工具6" },
						{ "", "工具7" }, { "", "工具8" } },
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Tool", "" }, { "", "工具9" }, { "", "工具10" },
						{ "", "工具11" }, { "", "工具12" } },
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Tool", "" }, { "", "工具13" }, { "", "工具14" },
						{ "", "工具15" }, { "", "工具16" } } };
		try {
			WTLibrary library = getLibraryByName(libraryName);
			for (int i = 0; i < str.length; i++) {
				String folderPath = "";
				String objectType = "";
				for (int j = 0; j < str[i].length; j++) {
					if (j == 0) {
						objectType = str[i][j][0];
						folderPath = folder + str[i][j][1];
					} else {
						MPMTooling tool = MPMResourceUtil.createTooling(str[i][j][0], str[i][j][1], library,
								folderPath, objectType,"");
					}
				}
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

	public static void createKnife() {
		String libraryName = "工艺资源库";
		String folder = "/Default/刀具";
		String str[][][] = {
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Knife", "" }, { "", "刀具1" }, { "", "刀具2" },
						{ "", "刀具3" }, { "", "刀具4" } },
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Knife", "" }, { "", "刀具5" }, { "", "刀具6" },
						{ "", "刀具7" }, { "", "刀具8" } },
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Knife", "" }, { "", "刀具9" }, { "", "刀具10" },
						{ "", "刀具11" }, { "", "刀具12" } },
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Knife", "" }, { "", "刀具13" }, { "", "刀具14" },
						{ "", "刀具15" }, { "", "刀具16" } } };
		try {
			WTLibrary library = getLibraryByName(libraryName);
			for (int i = 0; i < str.length; i++) {
				String folderPath = "";
				String objectType = "";
				for (int j = 0; j < str[i].length; j++) {
					if (j == 0) {
						objectType = str[i][j][0];
						folderPath = folder + str[i][j][1];
					} else {
						MPMTooling knife = MPMResourceUtil.createTooling(str[i][j][0], str[i][j][1], library,
								folderPath, objectType,"");
					}
				}
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

	public static void createMeasure() {
		String libraryName = "工艺资源库";
		String folder = "/Default/量具";
		String str[][][] = {
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Measure", "" }, { "", "量具1" }, { "", "量具2" },
						{ "", "量具3" }, { "", "量具4" } },
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Measure", "" }, { "", "量具5" }, { "", "量具6" },
						{ "", "量具7" }, { "", "量具8" } },
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Measure", "" }, { "", "量具9" },
						{ "", "量具10" }, { "", "量具11" }, { "", "量具12" } },
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Measure", "" }, { "", "量具13" },
						{ "", "量具14" }, { "", "量具15" }, { "", "量具16" } } };
		try {
			WTLibrary library = getLibraryByName(libraryName);
			for (int i = 0; i < str.length; i++) {
				String folderPath = "";
				String objectType = "";
				for (int j = 0; j < str[i].length; j++) {
					if (j == 0) {
						objectType = str[i][j][0];
						folderPath = folder + str[i][j][1];
					} else {
						MPMTooling measure = MPMResourceUtil.createTooling(str[i][j][0], str[i][j][1], library,
								folderPath, objectType,"");
					}
				}
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
		String folder = "/Default/制造单位";
		// String str[][] = { "外", "外办", "料", "质", "艺", "装", "一厂", "二厂", "三厂",
		// "四厂", "微", "木", "一部", "二部", "三部", "四部",
		// "五部", "六部", "八部", "九部","车" };
		String str[][] = { { "外", "23" }, { "外办", "60" }, { "料", "20" }, { "质", "24" }, { "艺", "27" }, { "装", "30" },
				{ "一厂", "41" }, { "二厂", "42" }, { "三厂", "43" }, { "四厂", "44" }, { "微", "47" }, { "木", "50" },
				{ "一部", "51" }, { "二部", "52" }, { "三部", "53" }, { "四部", "54" }, { "五部", "55" }, { "六部", "56" },
				{ "八部", "58" }, { "九部", "59" }, { "车", "61" } };

		String folder1 = "/Default/工种";
		String str1[][][] = {

		{ { "", "" }, { "料", "", "铣工" }, { "料", "", "剪工" }, { "料", "", "变形工" }, { "料", "", "焊工" }, { "料", "", "钳工" },
				{ "料", "", "备料工" }, { "外", "", "检工" }, { "外", "", "例试工" }, { "外", "", "环试工" }, { "外", "", "质量师" },
				{ "艺", "", "合材料工" }, { "艺", "", "焊接师" }, { "艺", "", "表面处理工" }, { "装", "", "调试工" }, { "装", "", "齐套工" },
				{ "装", "", "周转工" }, { "一厂", "", "铣工" }, { "一厂", "", "冷作工" }, { "一厂", "", "车工" }, { "一厂", "", "冲工" },
				{ "一厂", "", "刨工" }, { "一厂", "", "镗工" }, { "一厂", "", "钻工" }, { "一厂", "", "吊装工" }, { "一厂", "", "包装工" },
				{ "一厂", "", "焊工" }, { "一厂", "", "钳工" }, { "一厂", "", "齐套工" }, { "一厂", "", "周转工" }, { "二厂", "", "元装工" },
				{ "二厂", "", "电装工" }, { "二厂", "", "压胶工" }, { "二厂", "", "胶木化工" }, { "二厂", "", "浸烘工" },
				{ "二厂", "", "涂覆工" }, { "二厂", "", "钳工" }, { "三厂", "", "齐套工" }, { "三厂", "", "周转工" }, { "三厂", "", "铣工" },
				{ "三厂", "", "线切割工" }, { "三厂", "", "车工" }, { "三厂", "", "冲工" }, { "三厂", "", "电火花工" },
				{ "三厂", "", "抛光工" }, { "三厂", "", "镗工" }, { "三厂", "", "研磨工" }, { "三厂", "", "钻工" }, { "三厂", "", "刻字工" },
				{ "三厂", "", "焊工" }, { "三厂", "", "热处理工" }, { "三厂", "", "电镀工" }, { "三厂", "", "喷砂工" },
				{ "三厂", "", "喷丸工" }, { "三厂", "", "油漆工" }, { "三厂", "", "钳工" }, { "三厂", "", "齐套工" }, { "三厂", "", "周转工" },
				{ "三厂", "", "铭牌工" }, { "三厂", "", "备料工" }, { "四厂", "", "镀覆工" }, { "四厂", "", "机加工" },
				{ "四厂", "", "图形制作工" }, { "四厂", "", "照相工" }, { "微", "", "光刻工" }, { "微", "", "混装工" }, { "微", "", "调试工" },
				{ "微", "", "镀膜工" }, { "微", "", "设备操作工" }, { "微", "", "电镀工" }, { "微", "", "钳工" }, { "微", "", "齐套工" },
				{ "微", "", "周转工" }, { "木", "", "齐套工" }, { "木", "", "木工" }, { "一部", "", "设计师" }, { "二部", "", "设计师" },
				{ "三部", "", "设计师" }, { "四部", "", "设计师" }, { "五部", "", "设计师" }, { "六部", "", "设计师" },
				{ "八部", "", "设计师" }, { "九部", "", "设计师" }, { "车", "", "司机" }, { "外办", "", "齐套工" } },

		// { { "", "" }, { "艺", "", "齐套" }, { "装", "", "备料" }, { "新一厂", "", "领料"
		// }, { "新二厂", "", "外协" } },
		// { { "", "" }, { "新三厂", "", "备料" }, { "新四厂", "", "水切割" }, { "微", "",
		// "拉拔" }, { "木", "", "下料" } },
		// { { "", "" }, { "一部", "", "检验" }, { "二部", "", "筛选" }, { "三部", "",
		// "按图检验" }, { "四部", "", "环试" } },
		// { { "", "" }, { "五部", "", "准备" }, { "六部", "", "成形" }, { "七部", "",
		// "齐套" }, { "八部", "", "铺层" } },
		// { { "", "" }, { "九部", "", "调试" }, { "九部", "", "电调" }, { "九部", "",
		// "电测" }, { "九部", "", "复调" } }
		};

		String folder2 = "/Default/工位";

		String str2[][][] = {
				{ { "", "" }, { "外", "", "工位1" }, { "外办", "", "工位2" }, { "料", "", "工位3" }, { "质", "", "工位4" } },
				{ { "", "" }, { "艺", "", "工位5" }, { "装", "", "工位6" }, { "一厂", "", "工位7" }, { "二厂", "", "工位8" } },
				{ { "", "" }, { "三厂", "", "工位9" }, { "四厂", "", "工位10" }, { "微", "", "工位11" }, { "木", "", "工位12" } },
				{ { "", "" }, { "一部", "", "工位13" }, { "二部", "", "工位14" }, { "三部", "", "工位15" }, { "四部", "", "工位16" } },
				{ { "", "" }, { "五部", "", "工位17" }, { "六部", "", "工位18" }, { "车", "", "工位19" }, { "八部", "", "工位20" } },
				{ { "", "" }, { "九部", "", "工位21" }, { "九部", "", "工位22" }, { "九部", "", "工位23" }, { "九部", "", "工位24" } } };

		String folder3 = "/Default/设备";

		String str3[][][] = {
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Equipment|com.nriet.铣床", "/铣床" },
						{ "外", "", "铣床1" }, { "外办", "", "铣床2" }, { "料", "", "铣床3" }, { "质", "", "铣床4" },
						{ "三厂", "", "铣床5" }, { "一厂", "", "铣床6" } },
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Equipment|com.nriet.车床", "/车床" },
						{ "艺", "", "车床1" }, { "装", "", "车床2" }, { "一厂", "", "车床3" }, { "二厂", "", "车床4" },
						{ "外", "", "车床5" }, { "外办", "", "车床6" } },
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Equipment|com.nriet.刨床", "/刨床" },
						{ "三厂", "", "刨床1" }, { "四厂", "", "刨床2" }, { "微", "", "刨床3" }, { "木", "", "刨床4" },
						{ "外", "", "刨床5" }, { "一厂", "", "刨床6" } },
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Equipment|com.nriet.镗床", "/镗床" },
						{ "一部", "", "镗床1" }, { "二部", "", "镗床2" }, { "三部", "", "镗床3" }, { "四部", "", "镗床4" },
						{ "外", "", "镗床5" }, { "一厂", "", "镗床6" } },
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Equipment|com.nriet.钻床", "/钻床" },
						{ "五部", "", "钻床1" }, { "六部", "", "钻床2" }, { "车", "", "钻床3" }, { "八部", "", "钻床4" },
						{ "外", "", "钻床5" }, { "一厂", "", "钻床6" } },
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Equipment|com.nriet.磨床", "/磨床" },
						{ "外", "", "磨床1" }, { "九部", "", "磨床2" }, { "车", "", "磨床3" }, { "九部", "", "磨床4" },
						{ "外办", "", "磨床5" }, { "一厂", "", "磨床6" } },
				{ { "com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.Equipment|com.nriet.线切割", "/线切割" },
						{ "九部", "", "线切割1" }, { "装", "", "线切割2" }, { "四部", "", "线切割3" }, { "微", "", "线切割4" },
						{ "外", "", "线切割5" }, { "一厂", "", "线切割6" } } };

		try {
			WTLibrary library = getLibraryByName(libraryName);
			for (int i = 0; i < str.length; i++) {
				MPMPlant plant = MPMResourceUtil.createPlant(str[i][1], str[i][0], library, folder, "","");
			}
			for (int i = 0; i < str1.length; i++) {
				String folderPath = "";
				String objectType = "";
				for (int j = 0; j < str1[i].length; j++) {
					if (j == 0) {
						objectType = str1[i][j][0];
						folderPath = folder1 + str1[i][j][1];
					} else {
						MPMPlant plant = getPlantByName(str1[i][j][0]);
						MPMSkill skill = getSkillByNameType(str1[i][j][2], objectType);
						if (skill == null) {
							skill = MPMResourceUtil.createSkill(str1[i][j][1], str1[i][j][2], library, folderPath,
									objectType,"");
						}
						createWTPartUsageLink(plant, (MPMSkillMaster) skill.getMaster());
					}
				}

			}

			for (int i = 0; i < str2.length; i++) {
				String folderPath = "";
				String objectType = "";
				for (int j = 0; j < str2[i].length; j++) {
					if (j == 0) {
						objectType = str2[i][j][0];
						folderPath = folder2 + str2[i][j][1];
					} else {
						MPMPlant plant = getPlantByName(str2[i][j][0]);
						MPMWorkCenter workCenter = getWorkSpaceByName(str2[i][j][2], objectType);
						if (workCenter == null) {
							workCenter = MPMResourceUtil.createWorkSpace(str2[i][j][1], str2[i][j][2], library,
									folderPath, objectType,"");
						}
						createWTPartUsageLink(plant, (MPMWorkCenterMaster) workCenter.getMaster());
					}
				}

			}

			for (int i = 0; i < str3.length; i++) {
				String folderPath = "";
				String objectType = "";
				for (int j = 0; j < str3[i].length; j++) {
					if (j == 0) {
						objectType = str3[i][j][0];
						folderPath = folder3 + str3[i][j][1];
					} else {
						MPMPlant plant = getPlantByName(str3[i][j][0]);
						MPMTooling equipment = getEquipmentByName(str3[i][j][2], objectType);
						if (equipment == null) {
							equipment = MPMResourceUtil.createTooling(str3[i][j][1], str3[i][j][2], library,
									folderPath, objectType,"");
						}
						createWTPartUsageLink(plant, (MPMToolingMaster) equipment.getMaster());
					}
				}

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

	public static MPMSkill getSkillByNameType(String name, String type) throws WTException, RemoteException {
		GLLogger.debug(CLASSNAME, "name:" + name + ":type:" + type);
		MPMSkill skill = null;
		QuerySpec querySpec = new QuerySpec(MPMSkill.class);
		addTypeAppend(querySpec, type, MPMSkill.class);
		querySpec.appendWhere(new SearchCondition(MPMSkill.class, WTPart.NAME, SearchCondition.EQUAL, name),
				new int[] { 0 });
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			skill = (MPMSkill) queryResult.nextElement();
		}
		return skill;
	}

	public static MPMPlant getPlantByName(String name) throws WTException {
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

	public static MPMWorkCenter getWorkSpaceByName(String name, String type) throws WTException, RemoteException {
		GLLogger.debug(CLASSNAME, "name:" + name + ":type:" + type);
		MPMWorkCenter workSpace = null;

		QuerySpec querySpec = new QuerySpec(MPMWorkCenter.class);
		addTypeAppend(querySpec, type, MPMWorkCenter.class);
		querySpec.appendWhere(new SearchCondition(MPMWorkCenter.class, WTPart.NAME, SearchCondition.EQUAL, name),
				new int[] { 0 });
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			workSpace = (MPMWorkCenter) queryResult.nextElement();
		}
		return workSpace;
	}

	public static MPMTooling getEquipmentByName(String name, String type) throws WTException, RemoteException {
		GLLogger.debug(CLASSNAME, "name:" + name + ":type:" + type);
		MPMTooling equipment = null;

		QuerySpec querySpec = new QuerySpec(MPMTooling.class);
		addTypeAppend(querySpec, type, MPMTooling.class);
		querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NAME, SearchCondition.EQUAL, name),
				new int[] { 0 });
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			equipment = (MPMTooling) queryResult.nextElement();
		}
		return equipment;
	}

	public static void addTypeAppend(QuerySpec querySpec, String type, Class objectClass) throws RemoteException,
			WTException {
		GLLogger.debug(CLASSNAME, "querySpec:" + querySpec + ":type:" + type + ":objectClasss:" + objectClass);
		if (null != type && !"".equals(type)) {
			TypeUtil.getTypeQuery(WTDocument.class, type, querySpec);
			querySpec.appendAnd();
		}
	}

	public static WTPartUsageLink createWTPartUsageLink(WTPart parentPart, WTPartMaster childPartMaster)
			throws WTException {
		WTPartUsageLink partUsageLink = WTPartUsageLink.newWTPartUsageLink(parentPart, childPartMaster);
		PersistenceServerHelper.manager.insert(partUsageLink);
		return partUsageLink;

	}

	public static void createCSAndStepNames() throws WTException {
		WTLibrary library = getLibraryByName("工艺资源库");
		String StepName[] = { "钳", "备料", "齐套", "铣", "涂覆", "电镀", "装配", "热处理", "冲", "车", "制标识", "完全外协", "涂复", "油漆", "外协",
				"准备", "刨", "协加工", "清洗", "线切割", "钻", "包封", "底片制作", "铭牌制作", "电装", "胶木化", "机械加工", "制模板", "蚀刻", "涂漆",
				"印线号", "数控铣", "调试", "冷作", "电缆加工", "装焊", "焊接", "齐套按图及明细表齐套各零部件", "钳、焊", "包装", "印字", "镗", "刻字", "电缆制作",
				"数控钻孔", "电缆加工", "焊", "孔金属化", "	检漏", "钳(焊工配合)", "图形电镀", "数控冲", "印阻焊膜" };
		HashMap<String, String> StepNames = new HashMap<String, String>();
		StepNames.put("铣齿工", "铣齿");
		StepNames.put("铣工", "铣,数控铣,粗铣,粗铣(数控),数铣,铣(数控),精铣,精铣(数控)");
		StepNames.put("线切割工", "线切割");
		StepNames.put("冷作工", "冷作");
		StepNames.put("插齿工", "插齿");
		StepNames.put("车工", "粗车,精车,车");
		StepNames.put("冲工", "冲");
		StepNames.put("等离子切割工", "等离子切割");
		StepNames.put("锻工", "锻造");
		StepNames.put("滚齿工", "滚齿");
		StepNames.put("电火花工", "电火花");
		StepNames.put("剪工", "剪,粗剪");
		StepNames.put("磨工", "磨,外磨,平磨");
		StepNames.put("抛光工", "抛光");
		StepNames.put("刨齿工", "刨齿");
		StepNames.put("刨工", "刨");
		StepNames.put("镗工", "镗");
		StepNames.put("剃齿工", "剃齿");
		StepNames.put("铸造工", "铸");
		StepNames.put("激光切割工", "切割");
		StepNames.put("研磨工", "研磨");
		StepNames.put("钻工", "钻,数控钻孔");
		StepNames.put("刻线工", "刻线");
		StepNames.put("光刻工", "光刻");
		StepNames.put("刻字工", "刻字");
		StepNames.put("元装工", "元器件插装焊接");
		StepNames.put("变装工", "变装");
		StepNames.put("电装工", "电装,电调,电测,电讯调试");
		StepNames.put("吊装工", "吊装");
		StepNames.put("混装工", "混装");
		StepNames.put("绕线工", "绕线");
		StepNames.put("调试工", "调试");
		StepNames.put("镀膜工", "镀膜");
		StepNames.put("设备操作工", "配套设备维护保养");
		StepNames.put("包装工", "包装,周转");
		StepNames.put("变形工", "变形");
		StepNames.put("胶木化工", "粘不干胶");
		StepNames.put("浸烘工", "浸烘");
		StepNames.put("复合材料工", "复合材料");
		StepNames.put("焊接师", "焊接");
		StepNames.put("表面处理工", "表面处理");
		StepNames.put("焊工", "焊");
		StepNames.put("渗氮工", "渗氮");
		StepNames.put("热处理工", "热处理");
		StepNames.put("电镀工", "电镀");
		StepNames.put("喷砂工", "喷砂");
		StepNames.put("喷丸工", "喷丸");
		StepNames.put("喷锌工", "喷锌");
		StepNames.put("浸锌工", "浸锌");
		StepNames.put("油漆工", "油漆");
		StepNames.put("涂覆工", "涂覆");
		StepNames.put("镀覆工", "镀涂");
		StepNames.put("机加工", "机加");
		StepNames.put("图形制作工", "图形转移(内层),图形转移(外层)");
		StepNames.put("照相工", "照相制版");
		StepNames.put("检工", "检验,按图检验,军检,所检,检漏,来所复检,通电检查,热处理复检");
		StepNames.put("例试工", "例试");
		StepNames.put("环试工", "环试");
		StepNames.put("质量师", "质量");
		StepNames.put("钳工", "钳,钳(焊),去毛刺,钳冲工(配合)");
		StepNames.put("齐套工", "齐套");
		StepNames.put("周转工", "周转");
		StepNames.put("铭牌工", "铭牌,铭牌制作,制铭牌");
		StepNames.put("备料工", "备料,下料");
		StepNames.put("木工", "木,木材加工");
		StepNames.put("司机", "司机");
		StepNames.put("设计师", "设计师");

		MPMSkill skill = null;
		QuerySpec querySpec = new QuerySpec(MPMSkill.class);
		querySpec.appendWhere(new SearchCondition(MPMSkill.class, WTPart.NAME, SearchCondition.NOT_NULL, true),
				new int[] { 0 });
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		System.out.println("qr size=" + queryResult.size());
		try {
			while (queryResult.hasMoreElements()) {
				skill = (MPMSkill) queryResult.nextElement();

				String snList = StepNames.get(skill.getName());
				if (snList != null) {
					String[] snLists = snList.split(",");
					for (String str : snLists) {
						MPMTooling name;

						name = MPMResourceUtil.createTooling("", str, library, "/Default/工序名称",
								"com.ptc.windchill.mpml.resource.MPMTooling|com.812.ProceduceName","");

						MPMResourceUtil.createWTPartUsageLink(skill, (WTPartMaster) name.getMaster());
						System.out.println("add stepName: " + name + " to skill: " + skill.getName());
					}

				}
				MPMTooling cs = MPMResourceUtil.createTooling("", skill.getName() + "_工艺常用语", library,
						"/Default/工艺常用语", "com.ptc.windchill.mpml.resource.MPMTooling|com.812.CommonString","");
				MPMResourceUtil.createWTPartUsageLink(skill, (WTPartMaster) cs.getMaster());
				System.out.println("add cs: " + cs + " to skill: " + skill.getName());
			}
		} catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
