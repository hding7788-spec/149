package com.glaway.mpm.mpmresource;

import java.lang.reflect.Field;

import com.glaway.mpm.util.LoadConfig;

public class Constants {

	// 工艺资源类型
	public static final String GJ = "GJ";// 工具
	public static final String DJ = "DJ";// 刀具
	public static final String LJ = "LJ";// 量具
	public static final String YQYB = "YQYB";// 仪器仪表
	public static final String ZZDW = "ZZDW";// 制造单位
	public static final String GW = "GW";// 工位
	public static final String GZhuang = "GZhuang";// 工装
	public static final String SpecGZhuang = "GZhuang";// 工装
	public static final String CommGZhuang = "GZhuang";// 工装
	public static final String GZhong = "GZhong";// 工种
	public static final String GZhong2 = "GZhong2";// 工种
	public static final String SB = "SB";// 设备
	public static final String GYFL = "GYFL";// 工艺辅料
	public static final String GXMC = "GXMC";// 工序名称
	public static final String GYCYY = "GYCYY";// 工艺常用语
	public static final String DMSB = "DMSB";// 地面设备
	public static final String ChildSB = "ChildSB";// 子设备
	public static final String DMSBNAME = "地面设备";// 子设备
	public static final String GWWH = "GWWH";//工位维护

	public static final String CSXM = "CSXM";//参数项目
	public static final String DZQY = "DZQY";//定制区域
	public static final String CZGW = "CZGW";//操作岗位
	public static final String ZYLB = "ZYLB";//专业类别
	public static final String CSXMMC = "CSXMMC";//参数项目名称
	public static final String WZLB = "WZLB";//物资类别


	// 编号分类
	public static final String root = "Root";
	public static final String alGZ = "GAL";// 自制工装
	public static final String kGZ = "GK";// 科研工装
	public static final String tGZ = "GT";// 通用工装


	// 物件库名称
	public static final String ItemLib = "条目任务文档库";
	public static final String mpmResourceLibraryName = "工艺资源库";
	public static final String mpmKnowledgeLibraryName = "工艺知识库";
	public static final String alGZLibraryName = "自制工装设计库";
	public static final String kGZLibraryName = "科研工装设计库";
	public static final String tGZLibraryName = "通用工装设计库";

	// 文件夹路径和名称
	public static final String rootFolder = "/Default";
	public static final String gzCardFolderName = "工装申请卡";
	public static final String[] gzFolderName = { "01:Part", "02:模型", "03:工装设计文件", "04:更改单文档", "05:研发文档", "06:工艺文档",
			"07:PBOM", "08:工艺任务" };
	public static final String[] mpmResourceFolderName = { "制造单位", "工种", "工位", "设备", "刀具", "工量具", "工装", "工艺辅料", "工序名称",
			"工艺常用语" };

	// 流程名称
	public static final String gzCardAuditWorkflowTemplateName = "工装申请卡审核流程";
	public static final String gzDesignAuditorkflowTemplateName = "工装设计审核流程";
	public static final String gyPaiGongTemplateName = "PBOM构建流程";

	// 流程主物件的变量名称
	public static final String primaryBusinessObject = "primaryBusinessObject";

	// 状态名称
	public static final String YZF = "YZF";// 已作废
	public static final String BOHUI = "BOHUI";// 驳回
	public static final String RELEASED = "APPROVED";// 已归档
	public static final String INWORK = "INWORK";// 拟制
	public static final String SYZ = "SYZ";// 审阅中
	public static final String COMPLETED = "COMPLETED";// 已完工
	public static final String RESOLVED = "RESOLVED";// 已解决
	public static final String SHENHE = "SHENHE";// 审核

	// 角色名称
	public static final String GONGYIBUJIHUAYUAN = "GONGYIBUJIHUAYUAN";// 工艺计划员
	public static final String TECHNICALLEADER = "TECHNICALLEADER";// 工艺组长
	public static final String ASSIGNEE = "ASSIGNEE";// 工作负责人
	public static final String REVIEWER = "REVIEWER";// 审阅者
	public static final String DESIGNERSYSTEM15 = "DESIGNERSYSTEM15";// 1560-工艺副总设计师（工艺主师）
	public static final String TEMPSHENHE = "WFSHENHE";// 临时工艺审核流程审核者

	// 零件视图名称
	public static final String planning = LoadConfig.getInstance().getPbomView();
	public static final String design = "Design";

	// bom xml和工艺包文件的名称字段区分
	public static final String pbomDocEndwith = "-pbom.xml";
	public static final String ebomDocEndwith = "-ebom.xml";
	public static final String reworkProcessDocEndwith = "_fg";
	public static final String tempProcessDocEndwith = "_ls";
	public static final String middleModelNameContains = "-PC-";

	// 不同工艺
	public static final String normalProcess = "normal";
	public static final String reworkProcess = "rework";
	public static final String tempProcess = "temp";
	public static final String changeProcess = "change";

	// 群组的名称
	public static final String GongYiBu = "工艺部";
	public static final String BuLingDao = "部领导";
	public static final String ChanPinZu = "产品组";
	public static final String ChanPinZuLeader = "产品组组长";
	public static final String XinXiHuaZu = "信息化组";
	public static final String XinXiHuaZuLeader = "信息化组组长";
	public static final String GongYiJiHuaYuanZu = "工艺计划员组";
	public static final String GongYiJiHuaYuanZuLeader = "工艺计划员组组长";
	public static final String GongShiDingEZu = "工时定额组";
	public static final String GongShiDingEZuLeader = "工时定额组组长";
	public static final String ZhuanYeZu = "专业组";
	public static final String GongZhuangZu = "工装组";
	public static final String GongZhuangZuLeader = "工装组组长";
	public static final String JiXieZu = "机械组";
	public static final String JiXieZuLeader = "机械组组长";
	public static final String ReJiaGongZu = "热加工组";
	public static final String ReJiaGongZuLeader = "热加工组组长";
	public static final String BiaoMianChuLiZu = "表面处理组";
	public static final String BiaoMianChuLiZuLeader = "表面处理组组长";
	public static final String FeiJinShuCaiLiaoZu = "非金属材料组";
	public static final String FeiJinShuCaiLiaoZuLeader = "非金属材料组组长";
	public static final String YinZhiBanZu = "印制板组";
	public static final String YinZhiBanZuLeader = "印制板组组长";
	public static final String ZhuangLianZu = "装联组";
	public static final String ZhuangLianZuLeader = "装联组组长";
	public static final String WeiDianZiZu = "微电子组";
	public static final String WeiDianZiZuLeader = "微电子组组长";
	public static final String FeiBiaoZu = "非标组";
	public static final String FeiBiaoZuLeader = "非标组组长";
	public static final String ShiZhuRen = "室主任";

	//流程活动名称
	public static final String TechnicInworkRevise = "工艺拟制修订";
	public static final String TechnicShenhe = "工艺审核";
	public static final String TechnicPizhun = "工艺批准";
	public static final String TempTechnicShenhe = "临时工艺审核流程（审核）";
	public static final String TempTechnicBohui = "临时工艺审核流程（驳回）";
	public static final String ReworkTechnicPizhun = "工艺返工流程审核流程（工艺主师批准）";
	public static final String ReworkTechnicShenhe = "工艺返工流程审核流程（科技部计划员审核）";

	// 版本
	public static final String numberVersion = "1";
	public static final String letterVersion = "A";

	public static final String gzNameSplitStr = "|";

	// 不同工艺类型
	public static final String ASSEMBLE_PROCESSPLAN = "ASSEMBLE_PROCESSPLAN";
	public static final String MOUNT_PROCESSPLAN = "MOUNT_PROCESSPLAN";
	public static final String MACHINING_PROCESSPLAN = "MACHINING_PROCESSPLAN";
	public static final String PAINT_PROCESSPLAN = "PAINT_PROCESSPLAN";

	public static String getConsString(String str) {
		String returnStr = "";
		try {
			Class c = Class.forName(Constants.class.getName());
			Field field = c.getField(str);
			returnStr = (String) field.get(c);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return returnStr;
	}

}
