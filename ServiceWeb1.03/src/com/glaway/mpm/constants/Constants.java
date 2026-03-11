package com.glaway.mpm.constants;

import java.lang.reflect.Field;

public class Constants {

	// 工艺资源类型
	public static final String GJ = "GJ";// 工具
	public static final String DJ = "DJ";// 刀具
	public static final String LJ = "LJ";// 量具
	public static final String ZZDW = "ZZDW";// 制造单位
	public static final String GW = "GW";// 工位
	public static final String GZhuang = "GZhuang";// 工装
	public static final String GZhong = "GZhong";// 工种
	public static final String SB = "SB";// 设备
	public static final String GYFL = "GYFL";// 工艺辅料
	public static final String GXMC = "GXMC";// 工序名称
	public static final String GYCYY = "GYCYY";// 工艺常用语
	public static final String ChildSB = "ChildSB";// 子设备

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
	public static final String nonStdLibraryName = "非档设计库";

	// 文件夹路径和名称
	public static final String rootFolder = "/Default";
	public static final String gzCardFolderName = "工装申请卡";
	public static final String[] gzFolderName = { "01:Part", "02:模型", "03:工装设计文件", "04:更改单文档", "05:研发文档", "06:工艺文档",
			"07:PBOM", "08:工艺任务" };
	public static final String[] mpmResourceFolderName = { "制造单位", "工种", "工位", "设备", "刀具", "工具", "工装", "工艺辅料", "工序名称",
			"工艺常用语" ,"量具", "标准仪器仪表" ,"非标准仪器仪表"};
	public static final String dashboardName = "仪器仪表";


	// 状态名称
	public static final String YZF = "YZF";// 已作废
	public static final String BOHUI = "BOHUI";// 驳回
	public static final String RELEASED = "APPROVED";// 已归档
	public static final String INWORK = "INWORK";// 拟制
	public static final String SYZ = "SYZ";// 审阅中
	public static final String COMPLETED = "COMPLETED";// 已完工
	public static final String RESOLVED = "RESOLVED";// 已解决
	public static final String SHENHE = "SHENHE";// 审核
	public static final String PERFORMING = "PERFORMING";// 执行中

	// 角色名称
	public static final String GONGYIBUJIHUAYUAN = "GONGYIBUJIHUAYUAN";// 工艺计划员
	public static final String TECHNICALLEADER = "TECHNICALLEADER";// 工艺组长
	public static final String ASSIGNEE = "ASSIGNEE";// 工作负责人
	public static final String REVIEWER = "REVIEWER";// 审阅者
	public static final String DESIGNERSYSTEM15 = "DESIGNERSYSTEM15";// 1560-工艺副总设计师（工艺主师）
	public static final String TEMPSHENHE = "WFSHENHE";// 临时工艺审核流程审核者
	public static final String WFCAPPGONGSHITIANXIE = "WFCAPPGONGSHITIANXIE";// 工时填写者

	// 零件视图名称
	public static final String planning = "Manufacturing";
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
	public static final String sopProcess = "SOPDoc";
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
	public static final String ShiZhuRen702 = "702室";
	public static final String ShiZhuRen703 = "703室";
	public static final String ShiZhuRen704 = "704室";

	// 版本
	public static final String numberVersion = "1";
	public static final String letterVersion = "A";

	public static final String gzNameSplitStr = "|";

    public static final String JSP_DISPLAY_OK = "确定";
    public static final String JSP_DISPLAY_CANCEL = "取消";

    //20170929增加   add by jyx start
    public static String ROLE_PRODUCTMANAGER = "产品经理";
    public static String ROLE_DISABLE_CAI = "更改管理员 I";
    public static String ROLE_DISABLE_CAII = "更改管理员 II";
    public static String ROLE_DISABLE_CAIII = "更改管理员 III";
    public static String ROLE_DISABLE_PACKAGECREATOR = "包创建者";
    public static String ROLE_DISABLE_CHANGEREQUESTREVIEWBOARD = "更改审阅委员会";
    public static String ROLE_DISABLE_VARIANCEAPPROVERS = "超差批准者";
    public static String ROLE_DISABLE_PROMOTIONAPPROVERS = "升级批准者";
    public static String ROLE_DISABLE_PROMOTIONREVIEWERS = "升级审阅者";
    public static String ROLE_DISABLE_COLLABORATIONMANAGER = "协作管理员";
    public static String ROLE_DISABLE_OPTIONADMINISTRATOR = "选项管理员";
    public static String ROLE_DISABLE_GUEST = "访客";
    public static String ROLE_PIZHUNZHE = "批准者";
    public static String ROLE_JIAODUIZHE = "校对者";
    public static String ROLE_DAYINZHE = "打印者";
    public static String ROLE_NEIBUHUIQIANZHE = "内部会签者";
    public static String ROLE_WAIBUHUIQIANZHE = "外部会签者";
    public static String ROLE_ZHIPAIGONGYIYUANZHE = "指派工艺员者";
    public static String ROLE_ZHIPAIGONGYIZUZHANGZHE = "指派工艺组长者";
    public static String ROLE_BIAOSHENZHE = "标审者";
    public static String ROLE_GONGYIZUZHANG = "工艺组长";
    public static String ROLE_ZHURENGONGYISHI = "主任工艺师";
    public static String ROLE_XIANGMUBUXINGHAOZHUGUAN = "项";

    public static String CHANGE_REQUEST_REVIEW_BOARD="CHANGE REQUEST REVIEW BOARD";//变更审阅委员会
    public static String ECR_AUTHOR="ECR AUTHOR";//变更请求作者
    public static String CHANGE_ADMINISTRATOR_I="CHANGE ADMINISTRATOR I";//变更管理员 I
    public static String CHANGE_ADMINISTRATOR_II="CHANGE ADMINISTRATOR II";//变更管理员 II
    public static String VARIANCE_AUTHOR="VARIANCE AUTHOR";//超差作者
    public static String PR_AUTHOR="PR AUTHOR";//问题报告作者

    public static final String PROCESS_ASSIGN_TASKTYPE = "任务类型：";
    public static final String PROCESS_ASSIGN_TASKSTATE = "任务状态：";
    public static final String PROCESS_ASSIGN_SHIFOUZHUZHICHEJIAN = "主/辅制车间：";
    public static final String PROCESS_ASSIGN_ZHUZHICHEJIAN = "主制车间";
    public static final String PROCESS_ASSIGN_FUZHICHEJIAN = "辅制车间";
    public static final String PROCESS_ASSIGN_GONGYIYUAN = "工艺员：";
    public static final String PROCESS_ASSIGN_DATE = "日期：";
    public static final String PROCESS_ASSIGN_DATETO = "至：";
    public static final String PROCESS_ASSIGN_TASKTYPE_GONGYISHEJI = "工艺设计";
    public static final String PROCESS_ASSIGN_TASKTYPE_GONGYIGENGGAI = "工艺更改";
    public static final String PROCESS_ASSIGN_TASKTYPE_LINGBUJIANGONGYI = "零部件工艺";
    public static final String PROCESS_ASSIGN_TASKTYPE_LINSHIGONGYI = "临时工艺";
    public static final String PROCESS_ASSIGN_TASKSTATE_WANGONG = "完工";
    public static final String PROCESS_ASSIGN_TASKSTATE_JINXINGZHONG = "进行中";

    public static final String BUTTON_JSP_SEARCH = "搜索";

    public static final String WF_START_ERROR = ",流程启动失败！";
    public static final String WF_START_MSG = ",已启动！";
    public static final String WF_DOCUMENT_APPROVAL = "文档签审流程";
    public static final String WF_PART_APPROVAL = "零部件签审流程";
    public static final String WF_ZILIANGBAOGAO_APPROVAL = "质量报告签审流程";
    public static final String WF_MPMPPLAN_APPROVAL = "三维工艺签审流程";
    public static final String WF_GONGYIRENWUFENGONG = "工艺任务分工流程";
    public static final String WF_149ECNPAKAGE = "149变更签审包工艺会签流程";
    public static final String WF_149APPROVAL_HUIQIAN = "149签审包工艺会签流程";
    public static final String WF_149APPROVAL_ZHENGSHI_HUIQIAN = "149正式发放包流程";
    public static final String WF_SJ_ECN = "ECN流程";
    public static final String WFN_DOCUMENT = "文档签审流程";
    public static final String WFN_MANAGEDBASELINE = "产品状态签审流程";//基线流程
    public static final String WFN_PROCESS_DOCUMENT = "工艺单签审流程";
    public static final String WFN_PROCESS_SIMULATIONREPORT = "工艺仿真报告签审流程";
    public static final String WF_WORKITEM_STATUS_COMPLETED = "已完成";
    /**定版工艺修改签审流程*/
    public static final String WFN_TECHNICMODIFY = "定版工艺修改签审流程";

    /**"试验检测报告签审流程"*/
    public static final String WFN_SYJCDOC_PROCESS = "试验检测报告签审流程";;

    public static String WF_CHECK_NO_BEFOREOBJ = "请将修改前的对象添加到该变更通过的受影响对象列表中再完成任务。";
    public static String WF_CHECK_NO_AFTEROBJ = "请将修改后的对象添加到该变更通过的结果对象列表中再完成任务。";
    public static String WF_CHECK_PR_NOATTACHENT = "请将更改建议单添加到当前问题报告的附件列表后再完成任务！";
    public static String WF_CHECK_ECN_NOATTACHENT = "请将更改单添加到当前问题报告的附件列表后再完成任务！";

    public static final String STATE_INWORK = "INWORK";
    public static final String STATE_APPROVED = "APPROVED";
    public static final String STATE_REWORK = "REWORK";

    public static final String STATE_YIFABU = "已发布";
    public static final String STATE_YIGUIDANG = "已归档";
    public static final String STATE_YIPIZHUN = "已批准";
    public static final String STATE_ZHENGZAIGONGZUO = "正在工作";

    public static final String TYPE_ZIZHIJIAN = "自制件";
    public static final String TYPE_BIAOZHUNJIAN = "标准件";
    public static final String TYPE_TUYANG_LINGJIANTU = "零件图";
    public static final String TYPE_TUYANG_MINGXIBIAO = "明细表";
    public static final String TYPE_TUYANG_JIEXIANBIAO = "接线表";
    public static final String TYPE_TUYANG_YUANJIANBIAO = "元件表";

    public static final String TASK_BIANZHI = "编制";
    public static final String TASK_JIAODUI = "校对";
    public static final String TASK_SHENHE = "审核";
    public static final String TASK_NEIBUHUIQIAN = "内部会签";
    public static final String TASK_WAIBUHUIQIAN = "外部会签";
    public static final String TASK_BIAOSHEN = "标审";
    public static final String TASK_PIZHUN = "批准";
    public static final String TASK_ZHIPAIGONGYIHUIQIAN = "指派工艺会签";
    public static final String TASK_ZHIPAIGONGYIYUAN = "指派工艺员";
    public static final String TASK_GONGYIHUIQIAN = "工艺会签";

    public static final String TASK_WFBLOCK_YICHEJIAN = "一车间工艺会签";
    public static final String TASK_WFBLOCK_ERCHEJIAN = "二车间工艺会签";
    public static final String TASK_WFBLOCK_SANCHEJIAN = "三车间工艺会签";
    public static final String TASK_WFBLOCK_SICHEJIAN = "四车间工艺会签";
    public static final String TASK_WFBLOCK_WUCHEJIAN = "五车间工艺会签";
    public static final String TASK_WFBLOCK_LIUCHEJIAN = "六车间工艺会签";
    public static final String TASK_WFBLOCK_QICHEJIAN = "七车间工艺会签";
    public static final String TASK_WFBLOCK_BACHEJIAN = "八车间工艺会签";
    public static final String TASK_WFBLOCK_XIANGMUBU = "项目部工艺会签";

    public static String ROLE_VALUE_YISHI = "一室";
    public static String ROLE_VALUE_ERSHI = "二室";
    public static String ROLE_VALUE_SANSHI = "三室";
    public static String ROLE_VALUE_SISHI = "四室";
    public static String ROLE_VALUE_WUSHI = "五室";
    public static String ROLE_VALUE_LIUSHI = "六室";
    public static String ROLE_VALUE_ZONGZHUANGZHONGXIN = "总装中心";
    public static String ROLE_VALUE_DIANZHUANGZHONGXIN = "电装中心";
    public static String ROLE_VALUE_KEJICHU = "科技处";
    public static String ROLE_VALUE_XINDANGCHU = "信档处";
    public static String ROLE_VALUE_ZHILIANGCHU = "质量处";
    public static String ROLE_VALUE_WAIXIE = "外协";


    public static String ROLE_NAME_YISHI = "工程师_一室";
    public static String ROLE_NAME_ERSHI = "工程师_二室";
    public static String ROLE_NAME_SANSHI = "工艺师_三室";
    public static String ROLE_NAME_SISHI = "工艺师_四室";
    public static String ROLE_NAME_WUSHI = "工艺师_五室";
    public static String ROLE_NAME_LIUSHI = "工艺师_六室";
    public static String ROLE_NAME_ZONGZHUANGZHONGXIN = "工艺师_总装中心";
    public static String ROLE_NAME_DIANZHUANGZHONGXIN = "工艺师_电装中心";


    public static String SETMARK_IS = "是";


    public static String GROUP_SYMBOL_101CHANJIAN = "101车间";
    public static String GROUP_SYMBOL_103CHANJIAN = "103车间";
    public static String GROUP_SYMBOL_106CHANJIAN = "106车间";
    public static String GROUP_SYMBOL_ERSHI = "二室";
    public static String GROUP_SYMBOL_SANSHI = "三室";
    public static String GROUP_SYMBOL_SISHI = "四室";
    public static String GROUP_SYMBOL_ZLC = "质量处";
    public static String GROUP_NAME_101CHANJIAN_GZZ = "车间工艺组长_101车间";
    public static String GROUP_NAME_103CHANJIAN_GZZ = "车间工艺组长_103车间";
    public static String GROUP_NAME_106CHANJIAN_GZZ = "车间工艺组长_106车间";
    public static String GROUP_NAME_ERSHI_GZZ = "车间工艺组长_二室";
    public static String GROUP_NAME_SANSHI_GZZ = "车间工艺组长_三室";
    public static String GROUP_NAME_SISHI_GZZ = "车间工艺组长_四室";
    public static String GROUP_NAME_ZLC_GZZ = "车间工艺组长_质量处";

    public static String GROUP_NAME_101CHANJIAN_GYY = "车间工艺员_101车间";
    public static String GROUP_NAME_103CHANJIAN_GYY = "车间工艺员_103车间";
    public static String GROUP_NAME_106CHANJIAN_GYY = "车间工艺员_106车间";
    public static String GROUP_NAME_ERSHI_GYY = "车间工艺员_二室";
    public static String GROUP_NAME_SANSHI_GYY = "车间工艺员_三室";
    public static String GROUP_NAME_SISHI_GYY = "车间工艺员_四室";
    public static String GROUP_NAME_ZLC_GYY = "车间工艺员_质量处";

    public static final String PRODUCT_SELECT_ROLE = "请选择角色：";
    public static final String PRODUCT_NO_SELECTROLE = "该产品团队没有你所选择的角色，不能添加用户。";
    public static final String PRODUCT_MSG_PLEASESELECTROLE = "请从下拉框选择需要设置用户的角色。";

    public static final String PROCESS_NOTICE_NOTE1 = "注：当前版本为--";
    public static final String PROCESS_NOTICE_NOTE2 = "--，作废版本--";
    public static final String PROCESS_NOTICE_NOTE3 = "--。";

    /**
     * 标准化综合要求
     */
    public static final String OBJECT_TYPE_STANDARD_SUMREQUIREMENT = "STANDARD_SUMREQUIREMENT";
    /**
     * 标准化审查报告
     */
    public static final String OBJECT_TYPE_STANDARD_REVIEWREPORT = "STANDARD_REVIEWREPORT";
    /**
     * 标准化大纲
     */
    public static final String OBJECT_TYPE_STANDARD_SUMARRY = "STANDARD_SUMARRY";
    /**
     * 试验实施细则
     */
    public static final String OBJECT_TYPE_TEST_LIST = "TEST_LIST";
    /**
     * 试验报告
     */
    public static final String OBJECT_TYPE_TEST_REPORT = "TEST_REPORT";
    /**
     * 总结报告
     */
    public static final String OBJECT_TYPE_TEST_SUMMARY = "TEST_SUMMARY";
    /**
     * 评审报告
     */
    public static final String OBJECT_TYPE_TEST_ASSESSMENT = "TEST_ASSESSMENT";
    /**
     * 其它报告
     */
    public static final String OBJECT_TYPE_TEST_OTHER = "TEST_OTHER";
    /**
     * 通用试验文件
     */
    public static final String OBJECT_TYPE_TEST_UNIVERSAL = "TEST_UNIVERSAL";

    /**
     * 工装设计报告
     */
    public static final String OBJECT_TYPE_TOOLING_DESIGN_REPORT = "TOOLING_DESIGN_REPORT";
    /**
     * CNAS专用试验报告
     */
    public static final String OBJECT_TYPE_CNAS_TEST_REPORT = "CNAS_TEST_REPORT";
    /**
     * CNAS专用试验实施细则
     */
    public static final String OBJECT_TYPE_CNAS_TEST_LIST = "CNAS_TEST_LIST";
    /**
     * 工装设计任务书
     */
    public static final String OBJECT_TYPE_TOOLING_TASKBOOK = "TOOLING_TASKBOOK";
    /**
     * 工艺总方案
     */
    public static final String OBJECT_TYPE_PROCESS_PROGRAM = "PROCESS_PROGRAM";
    /**
     * 技术协议
     */
    public static final String OBJECT_TYPE_PROCESS_AGREEMENT = "PROCESS_AGREEMENT";
    /**
     * 工艺通知单
     */
    public static final String OBJECT_TYPE_PROCESS_NOTICE = "PROCESS_NOTICE";
    /**
     * 工艺状态表
     */
    public static final String OBJECT_TYPE_PROCESSSTATUSTABLE = "PROCESSSTATUSTABLE";
    /**
     * 工艺 更改申请
     */
    public static final String OBJECT_TYPE_PROCESS_EC_REQUEST = "PROCESSECREQUEST"; //add by  liangbo
    /**
     * 工艺 更改单
     */
    public static final String OBJECT_TYPE_PROCESS_EC_NOTICE = "PROCESS_EC_NOTICE";

    /**
     * 工艺 更改单
     */
    public static final String OBJECT_TYPE_PROCESSECFORM = "PROCESSECFORM";

    /**
     * 转阶段更改单
     */
    public static final String OBJECT_TYPE_PROCESS_PHASECODE_NOTICE = "PROCESSPHASECHANGEECFORM";
    /**
     * 标准件定额明细表
     */
    public static final String OBJECT_TYPE_STANDARDPART_DETAILFORM = "STANDARDPART_DETAILFORM";
    /**
     * 标准件定额汇总表
     */
    public static final String OBJECT_TYPE_STANDARDPART_COLLECTFORM = "STANDARDPART_COLLECTFORM";
    /**
     * 主要材料工艺定额明细表
     */
    public static final String OBJECT_TYPE_PRIMARYMAT_DETAILFORM = "PRIMARYMAT_DETAILFORM";
    /**
     * 主要材料工艺定额汇总表
     */
    public static final String OBJECT_TYPE_PRIMARYMAT_COLLECTFORM = "PRIMARYMAT_COLLECTFORM";
    /**
     * 材料工艺定额明细表
     */
    public static final String OBJECT_TYPE_NOTPRIMARYMAT_DETAILFORM = "NOTPRIMARYMAT_DETAILFORM";
    /**
     * 材料工艺定额汇总表
     */
    public static final String OBJECT_TYPE_NOTPRIMARYMAT_COLLECTFORM = "NOTPRIMARYMAT_COLLECTFORM";
    /**
     * 外协件明细表
     */
    public static final String OBJECT_TYPE_OUTSOURCE_DETAILFORM = "OUTSOURCE_DETAILFORM";
    /**
     * 外购件工艺定额汇总表
     */
    public static final String OBJECT_TYPE_OUTSOURCE_COLLECTFORM = "OUTSOURCE_COLLECTFORM";

    /**
     * 元器件工艺定额汇总表
     */
    public static final String OBJECT_TYPE_COMPONENTSOURCE_COLLECTFORM = "COMPONENTSOURCE_COLLECTFORM";

    /**
     * 工艺路线表
     */
    public static final String OBJECT_TYPE_PROCESSLINE_FORM = "PROCESSLINE_FORM";

    /**
     * 工艺评审报告
     */
    public static final String OBJECT_TYPE_TECHNICREVIEWREPORT = "TECHNICREVIEWREPORT";

    /**
     * 工艺评审申请报告
     * add by liangbo
     */
    public static final String OBJECT_TYPE_TECHNICREVIEWREQUEST = "TECHNICREVIEWREQUEST";

    /**
     * 研发中心项目类文档
     */
    public static final String OBJECT_TYPE_PROJECTFILE = "PROJECTFILE";

    /**
     * 研发中心非项目类文档
     */
    public static final String OBJECT_TYPE_NOPROJECTFILE = "NOPROJECTFILE";

    /**
     * 研发中心产学研文件
     */
    public static final String OBJECT_TYPE_CXYFILE = "CXYFILE";

    /**
     * 研发中心创新平台文档
     */
    public static final String OBJECT_TYPE_CXPTFILE = "CXPTFILE";

    /**
     * 研发中心人才队伍文档
     */
    public static final String OBJECT_TYPE_RCDWFILE = "RCDWFILE";

    /**
     * 研发中心技术体系文档
     */
    public static final String OBJECT_TYPE_JSTXFILE = "JSTXFILE";

    /**
     * 研发中心规划文档
     */
    public static final String OBJECT_TYPE_GHFILE = "GHFILE";

    /**
     * 研发中心技术交流文档
     */
    public static final String OBJECT_TYPE_JSJLFILE = "JSJLFILE";

    /**
     * 研发中心中心技改文档
     */
    public static final String OBJECT_TYPE_ZXJGFILE = "ZXJGFILE";

    /**
     * 研发中心中心市场开发文档
     */
    public static final String OBJECT_TYPE_ZXSCKFFILE = "ZXSCKFFILE";

    /**
     * 研发中心所内任务文档
     */
    public static final String OBJECT_TYPE_SNRWFILE = "SNRWFILE";

    /**
     * 研发中心管理文档
     */
    public static final String OBJECT_TYPE_GLWJFILE = "GLWJFILE";

    /**
     * 研发中心成果类文档
     */
    public static final String OBJECT_TYPE_CGLWJFILE = "CGLWJFILE";

    /**
     * 其它报告类组
     */
    public static final String OBJECT_TYPE_PROCESS_OTHERREPORT = "PROCESS_OTHERREPORT";

    /**
     * MES创建无损检测报告
     */
    public static final String OBJECT_TYPE_MESWUSUN = "MESDOC_WUSUN";

    /**
     * MES创建理化试验检测报告
     */
    public static final String OBJECT_TYPE_MESDOC_LHSY = "MESDOC_LHSY";

    /**
     * 备料清单
     */
    public static final String OBJECT_TYPE_BEILIAOQINGDAN = "BEILIAOQINGDAN";

    /**
     * 细则
     */
    public static final String OBJECT_TYPE_XIZE= "XIZE";

	//add by jyx end

	public static String getConsString(String str) {
		String returnStr = "";
		try {
			Class c = Class.forName(Constants.class.getName());
			Field field = c.getField(str);
			returnStr = (String) field.get(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStr;
	}

}
