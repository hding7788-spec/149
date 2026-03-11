package ext.casc.constants;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import wt.util.WTProperties;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class Constants {
	//public static final String ALL_ZHUZHI_BUMEN_VALUE = "1&2&3&4&5&6&7&8&项";
	//public static final String ALL_FUZHI_BUMEN_VALUE = "1&2&3&4&5&6&7&8&项&物资";
	public static List<String> allPartMents = new ArrayList<String>();
	public static List<String> processNoticeConfig = new ArrayList<String>();
	public static List<String> ALL_ZHUZHI_BUMEN_VALUE = new ArrayList<String>();
	public static List<String>  ALL_FUZHI_BUMEN_VALUE = new ArrayList<String>();
	public static List<String> allChejian = new ArrayList<String>();
	public static List<String> ALLGONGYIYUAN = new ArrayList<String>();
	public static List<String> allReleaseDepartment = new ArrayList<String>();
	public static List<String> allChejianExceptXiangMuBan = new ArrayList<String>();
	public static Map<String,String> allChejianToWorkFlowGYZZRoleMap = new TreeMap<String,String>();
	public static Map<String,String> allChejianToWorkFlowGYYRoleMap = new TreeMap<String,String>();
	public static Map<String,String> allChejianToWorkFlowGYZZTOGYYRoleMap = new TreeMap<String,String>();
	public static Map<String,String> unitMap = new TreeMap<String,String>();
	public static List<String>  ctypes = new ArrayList<String>();
	public static List<String>  phases = new ArrayList<String>();
	public static List<String>  keycomponents = new ArrayList<String>();
	public static final String TYPE_ZIZHIJIAN = "自制件";
	public static final String TYPE_BIAOZHUNJIAN = "标准件";
	public static final String TYPE_TUYANG_LINGJIANTU = "零件图";
	public static final String TYPE_TUYANG_MINGXIBIAO = "明细表";
	public static final String TYPE_TUYANG_JIEXIANBIAO = "接线表";
	public static final String TYPE_TUYANG_YUANJIANBIAO = "元件表";
	public static final String TYPE_WAIPEITAOJIAN = "外配套件";
	public static final String TYPE_DAILIAOWEIWAIJIAN = "带料委外件";
	public static final String TYPE_BUDAILIAOWEIWAIJIAN = "不带料委外件";
	public static List<String>  allBumen = new ArrayList<String>();
	public static  Map<String,String> photoValueMap = new HashMap<String,String>();
	public static  List<String> photoValueList = new ArrayList<String>();
	public static  List<String> photoTypeList = new ArrayList<String>();

	public static Map<String, String> miji = new HashMap<String, String>();
	public  static Map<String, String> useUnitMap  = new HashMap<String,String>();
	static{
		useUnitMap.put("每个", "ea");
		useUnitMap.put("根据需要", "as_needed");
		useUnitMap.put("千克", "kg");
		useUnitMap.put("米", "m");
		useUnitMap.put("升", "l");
		useUnitMap.put("平方米", "sq_m");
		useUnitMap.put("立方米", "cu_m");
		useUnitMap.put("包", "bao");
		useUnitMap.put("筒", "tong");
		useUnitMap.put("瓶", "ping");
		useUnitMap.put("张", "zhang");
		useUnitMap.put("只", "zhi");
		useUnitMap.put("支", "zhi_2");
		useUnitMap.put("毫升", "haosheng");
		useUnitMap.put("克", "g");
		//装配类型映射   可分,不可分,组件
		useUnitMap.put("可分","separable");
		useUnitMap.put("不可分","inseparable");
		useUnitMap.put("组件","component");
		//默认追踪代码映射   批号,批号/序列号,序列号,未追踪
		useUnitMap.put("批号", "S");
		useUnitMap.put("批号/序列号", "L");
		useUnitMap.put("序列号", "X");
		useUnitMap.put("未追踪", "0");

		unitMap.put("no8", "八部");
		unitMap.put("八部", "八部");
		unitMap.put("NO8", "八部");
		unitMap.put("805", "805");
		unitMap.put("805.11", "805");
		miji.put("无", "GONGKAI");
		miji.put("公开", "GONGKAI");
		miji.put("内部", "NEIBU");
		miji.put("普通", "NEIBU");
		miji.put("秘密", "MIMI");
		miji.put("秘密★", "MIMI");
		miji.put("秘密★10年", "MIMI");
		miji.put("机密", "JIMI");
		miji.put("机密★", "JIMI");
		miji.put("机密★20年", "JIMI");

	    allBumen.add("事业一部");
	    allBumen.add("事业二部");
	    allBumen.add("事业三部");
	    allBumen.add("事业四部");

		//Excel:自制件,标准件,外购件,外配套件,外协件,辅助材料
		ctypes.add(TYPE_ZIZHIJIAN);
		ctypes.add(TYPE_BIAOZHUNJIAN);
		ctypes.add(TYPE_WAIPEITAOJIAN);
		ctypes.add(TYPE_DAILIAOWEIWAIJIAN);
		ctypes.add(TYPE_BUDAILIAOWEIWAIJIAN);
		ctypes.add("元器件");
		ctypes.add("外购件");
		ctypes.add("主要材料");
		ctypes.add("辅助材料");
		ctypes.add("外协件");

		//Excel:Y,M,M1,M2,C,S,Z,Z1,Z2,Z3,D,D1,D2,D3,G,P,N,A,MC,CS
		phases.add("A");
		phases.add("Y");
		phases.add("M");
		phases.add("M1");
		phases.add("M2");
		phases.add("C");
		phases.add("C1");
		phases.add("C2");
		phases.add("C3");
		phases.add("S");
		phases.add("S1");
		phases.add("S2");
		phases.add("S3");
		phases.add("Z");
		phases.add("Z1");
		phases.add("Z2");
		phases.add("Z3");
		phases.add("D");
		phases.add("D1");
		phases.add("D2");
		phases.add("D3");
		phases.add("G");
		phases.add("P");
		phases.add("N");
		phases.add("A");
		phases.add("MC");
		phases.add("CS");
		phases.add("B");

		//Excel:N/G/Z
		keycomponents.add("N");
		keycomponents.add("G");
		keycomponents.add("Z");



		WTProperties wtProperties;
		try {
			wtProperties = WTProperties.getLocalProperties();
			String codebasePath = wtProperties.getProperty("wt.codebase.location");
	        String filePath = codebasePath + File.separator + "ext"
	                 + File.separator + "casc"
	                 + File.separator + "conf" + File.separator + "config_149.xml";
	       // FileInputStream inputStream = new FileInputStream(new File(filePath));
	    	SAXReader reader = new SAXReader();
			Document document;
			try {
				document = reader.read(new File(filePath));
				Element rootElement = document.getRootElement();
				Element deptConfig  = rootElement.element("deptConfig");
				List<Element> configs = deptConfig.elements("config");
				for(Element e:configs){
					Element dept = e.element("dept");
					String sdept = dept.getText();

					Element role_gyzz = e.element("role_gyzz");
					String srole_gyzz = role_gyzz.getText();

					Element role_gyy = e.element("role_gyy");
					String srole_gyy = role_gyy.getText();

					allChejian.add(sdept);
					ALLGONGYIYUAN.add(srole_gyy);
		        	ALL_ZHUZHI_BUMEN_VALUE.add(sdept);
		        	ALL_FUZHI_BUMEN_VALUE.add(sdept);

		        	allChejianExceptXiangMuBan.add(sdept);
	        		allChejianToWorkFlowGYZZRoleMap.put(sdept,srole_gyzz);
	        		allChejianToWorkFlowGYYRoleMap.put(sdept,srole_gyy);
	        		allChejianToWorkFlowGYZZTOGYYRoleMap.put(srole_gyzz, srole_gyy);
		        	if(sdept.equals("项")){
		        		allReleaseDepartment.add(sdept+"目部");
		        	}else if(sdept.equals("物资")){

		        	}else if(sdept.equals("科瑞")){
		        		allReleaseDepartment.add(sdept+"所");
		        	}else if(sdept.equals("科")){

					}else if(sdept.equals("研发")){
		        		allReleaseDepartment.add(sdept+"部");
		        	}else if(sdept.equals("后")){
		        		allReleaseDepartment.add(sdept+"勤部");
		        	}else{
		        		allReleaseDepartment.add(sdept+"分厂");
		        	}
				}


				Element ppdeptConfig  = rootElement.element("ProcessPlanDeptConfig");
				List<Element> ppconfigs = ppdeptConfig.elements("config");
				for(Element e:ppconfigs){
					Element dept = e.element("dept");
					String sdept = dept.getText();
					allPartMents.add(sdept);
				}

				Element pnc  = rootElement.element("ProcessNoticeConfig");
				List<Element> pnconfigs = pnc.elements("config");
				for(Element e:pnconfigs){
					Element product = e.element("product");
					String sproduct = product.getText();
					processNoticeConfig.add(sproduct);
				}

				Element photoConfig  = rootElement.element("PhotoConfig");
				Element pdcjConfig  = photoConfig.element("pdcjConfig");
				List<Element> pdcjConfigs = pdcjConfig.elements("config");
				for(Element e:pdcjConfigs){
					Element key = e.element("key");
					String skey = key.getText();
					Element value = e.element("value");
					String svalue = value.getText();
					photoValueMap.put(svalue,skey);
					photoValueList.add(svalue);
				}

				Element photoTypeConfig  = photoConfig.element("photoTypeConfig");
				List<Element> photoTypeConfigs = photoTypeConfig.elements("config");
				for(Element e:photoTypeConfigs){
					Element value = e.element("value");
					String svalue = value.getText();
					photoTypeList.add(svalue);
				}



			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        ALL_ZHUZHI_BUMEN_VALUE.remove("物资");//主制车间去掉物资
	        allChejian.add("后");
	        allChejianExceptXiangMuBan.remove("项");
	        allChejianToWorkFlowGYYRoleMap.put("后", "HOUQINBUGONGYIYUAN");

	        //allReleaseDepartment.remove("物资");
	        allReleaseDepartment.add("档案室");
			allReleaseDepartment.add("军代室");


			/*allChejianToWorkFlowGYZZRoleMap.put("1", "YICHEJIANGONGYIZUZHANG");
			allChejianToWorkFlowGYZZRoleMap.put("2", "ERCHEJIANGONGYIZUZHANG");
			allChejianToWorkFlowGYZZRoleMap.put("3", "SANCHEJIANGONGYIZUZHANG");
			allChejianToWorkFlowGYZZRoleMap.put("4", "SICHEJIANGONGYIZUZHANG");
			allChejianToWorkFlowGYZZRoleMap.put("5", "WUCHEJIANGONGYIZUZHANG");
			allChejianToWorkFlowGYZZRoleMap.put("6", "LIUCHEJIANGONGYIZUZHANG");
			allChejianToWorkFlowGYZZRoleMap.put("7", "QICHEJIANGONGYIZUZHANG");
			allChejianToWorkFlowGYZZRoleMap.put("8", "BACHEJIANGONGYIZUZHANG");
			allChejianToWorkFlowGYZZRoleMap.put("项", "XIANGMUBUGONGYIZUZHANG");
			allChejianToWorkFlowGYZZRoleMap.put("物资", "WUZIBUGONGYIZUZHANG");

			allChejianToWorkFlowGYYRoleMap.put("1", "YICHEJIANGONGYIYUAN");
			allChejianToWorkFlowGYYRoleMap.put("2", "ERCHEJIANGONGYIYUAN");
			allChejianToWorkFlowGYYRoleMap.put("3", "SANCHEJIANGONGYIYUAN");
			allChejianToWorkFlowGYYRoleMap.put("4", "SICHEJIANGONGYIYUAN");
			allChejianToWorkFlowGYYRoleMap.put("5", "WUCHEJIANGONGYIYUAN");
			allChejianToWorkFlowGYYRoleMap.put("6", "LIUCHEJIANGONGYIYUAN");
			allChejianToWorkFlowGYYRoleMap.put("7", "QICHEJIANGONGYIYUAN");
			allChejianToWorkFlowGYYRoleMap.put("8", "BACHEJIANGONGYIYUAN");
			allChejianToWorkFlowGYYRoleMap.put("项", "XIANGMUBUGONGYIYUAN");
			allChejianToWorkFlowGYYRoleMap.put("物资", "WUZIBUWUZIYUAN");
			*/


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*allReleaseDepartment.add("1车间");
		allReleaseDepartment.add("2车间");
		allReleaseDepartment.add("3车间");
		allReleaseDepartment.add("4车间");
		allReleaseDepartment.add("5车间");
		allReleaseDepartment.add("6车间");
		allReleaseDepartment.add("7车间");
		allReleaseDepartment.add("8车间");
		allReleaseDepartment.add("项目办");*/


	}

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
    public static String ROLE_DAYINZHE_VALUE = "DAYINZHE";
    public static String ROLE_NEIBUHUIQIANZHE = "内部会签者";
    public static String ROLE_WAIBUHUIQIANZHE = "外部会签者";
    public static String ROLE_ZHIPAIGONGYIYUANZHE = "指派工艺员者";
    public static String ROLE_ZHIPAIGONGYIZUZHANGZHE = "指派工艺组长者";
    public static String ROLE_XINXIHUASHUJVYUAN = "信息化部数据员";
    public static String ROLE_BIAOSHENZHE = "标审者";
    public static String ROLE_GONGYIZUZHANG = "工艺组长";
    public static String ROLE_ZHURENGONGYISHI = "主任工艺师";
    public static String ROLE_KEY_ZHURENGONGYISHI = "ZHURENGONGYISHI";
    public static String ROLE_XIANGMUBUXINGHAOZHUGUAN = "项";
    public static String ROLE_HOUQINBU = "后";
    public static String ROLE_WUZIBU = "物资";
    public static String ROLE_CANYUREN = "参与人";
    public static String ROLE_SHOUJIANREN="RECIPIENT";

    public static String CHANGE_REQUEST_REVIEW_BOARD="CHANGE REQUEST REVIEW BOARD";//变更审阅委员会
    public static String ECR_AUTHOR="ECR AUTHOR";//变更请求作者
    public static String CHANGE_ADMINISTRATOR_I="CHANGE ADMINISTRATOR I";//变更管理员 I
    public static String CHANGE_ADMINISTRATOR_II="CHANGE ADMINISTRATOR II";//变更管理员 II
    public static String REVIEWER="REVIEWER";//审阅者
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
    public static final String WF_149ECNPAKAGE_JISHUHUIQIAN = "149变更签审包技术会签流程";
    public static final String WF_149APPROVAL_HUIQIAN = "149签审包工艺会签流程";
    public static final String WF_149APPROVAL_JISHUHUIQIAN = "149签审包技术会签流程";
    public static final String WF_149APPROVAL_ZHENGSHI_HUIQIAN = "149正式发放包流程";
    public static final String WF_SJ_ECN = "ECN流程";

    public static final String WF_WORKITEM_STATUS_COMPLETED = "已完成";

    public static String WF_CHECK_NO_BEFOREOBJ = "请将修改前的对象添加到该变更通过的受影响对象列表中再完成任务。";
    public static String WF_CHECK_NO_AFTEROBJ = "请将修改后的对象添加到该变更通过的结果对象列表中再完成任务。";
    public static String WF_CHECK_PR_NOATTACHENT = "请将更改建议单添加到当前问题报告的附件列表后再完成任务！";
    public static String WF_CHECK_ECN_NOATTACHENT = "请将更改单添加到当前问题报告的附件列表后再完成任务！";

    public static final String STATE_INWORK = "INWORK";
    public static final String STATE_APPROVED = "APPROVED";
    public static final String STATE_YIFABU = "已发布";
    public static final String STATE_YIGUIDANG = "已归档";
    public static final String STATE_YIPIZHUN = "已批准";
    public static final String STATE_ZHENGZAIGONGZUO = "正在工作";
    public static final String STATE_XIUGAIZHONG = "修改中";


    public static final String TASK_BIANZHI = "编制";
    public static final String TASK_JIAODUI = "校对";
    public static final String TASK_SHENHE = "审核";
    public static final String TASK_NEIBUHUIQIAN = "内部会签";
    public static final String TASK_WAIBUHUIQIAN = "外部会签";
    public static final String TASK_BIAOSHEN = "标审";
    public static final String TASK_PIZHUN = "批准";
    public static final String TASK_ZHIPAIGONGYIHUIQIAN = "指派工艺会签";
	public static final String TASK_ZHIPAIGONGYIYUSHEN = "指派工艺预审";
	public static final String TASK_ZHIPAIGONGYIYUAN = "指派工艺员";
    public static final String TASK_GONGYIHUIQIAN = "工艺会签";
    public static final String TASK_GONGYYUSHEN = "工艺预审";
    public static final String TASK_GONGYIHUIQIANYIJIANHUIZONG = "工艺会签汇总";

    public static final String TASK_WFBLOCK_YICHEJIAN = "一车间工艺会签";
    public static final String TASK_WFBLOCK_ERCHEJIAN = "二车间工艺会签";
    public static final String TASK_WFBLOCK_SANCHEJIAN = "三车间工艺会签";
    public static final String TASK_WFBLOCK_SICHEJIAN = "四车间工艺会签";
    public static final String TASK_WFBLOCK_WUCHEJIAN = "五车间工艺会签";
    public static final String TASK_WFBLOCK_LIUCHEJIAN = "六车间工艺会签";
    public static final String TASK_WFBLOCK_QICHEJIAN = "七车间工艺会签";
    public static final String TASK_WFBLOCK_BACHEJIAN = "八车间工艺会签";
    public static final String TASK_WFBLOCK_JIUCHEJIAN = "九车间工艺会签";
    public static final String TASK_WFBLOCK_XIANGMUBU = "项目部工艺会签";

    public static String SETMARK_IS = "是";

    public static final String PRODUCT_SELECT_ROLE = "请选择角色：";
    public static final String PRODUCT_NO_SELECTROLE = "该产品团队没有你所选择的角色，不能添加用户。";
    public static final String PRODUCT_MSG_PLEASESELECTROLE = "请从下拉框选择需要设置用户的角色。";
    public static final String PRODUCT_MSG_PLEASEINPUTBATCH = "请输入批次号：";


    /**
     *首页任务活动"开启的"不显示"驳回重新指派工艺组长",在视图中增加"驳回重新指派工艺组长"用于显示"驳回重新指派工艺组长"活动对象
     *任务活动名称通过颜色标示超期活动任务，超过5天显示为黄色，超过10天显示为红色
     *只显示还在待办的事项
     */
    public  static final String CONST_EXT_PLAN_HOME_OVERVIEW_WORKLIST_TABLE_ID = "ext.casc.work.mvc.builders.OverviewAssignmentsBuilder";
    public  static final String WORKITEM_NAME_BHCXZPGYZZ = "驳回重新指派工艺组长";
    public  static final String WORKITEM_NAME_YIWEIPAI = "已委派";
    public  static final String WORKITEM_NAME_GSDE = "工时定额";
    public  static final String WORKITEM_NAME_JXSZXHD = "仅显示最新活动";
    public  static final String WORKITEM_NAME_BRFQ = "本人发起的";
    public  static final String WORKITEM_NAME_BRFQWWC = "本人发起未完成";
	public static final String WORKITEM_NAME_YIYINCANG = "已隐藏";
	// public  static final String WORKITEM_NAME_ZYCPSJFFTZ = "自研产品设计发放通知";//自研产品设计发放通知
   // public  static final String WORKITEM_NAME_ZYCPSJGGFFTZ = "自研产品设计更改发放通知";//自研产品设计更改发放通知
   // public  static final String WORKITEM_NAME_ZYCPWDFFTZ = "自研产品文档发放通知";//自研产品文档发放通知
   // public  static final String WORKITEM_NAME_SELECTED_ZYCPFFTZ = "自研产品发放通知";//自研产品发放通知
   public  static final String WORKITEM_NAME_SELECTED_TZZRGYS = "通知主任工艺师";
   public  static final String SELECT_NAME_ALL = "全部";
   public static final String SHOWVIEW805 = "805发来";
   public static final String SHOWVIEWNO8 = "八部发来";
   public static final String SHOWVIEWGONGYI = "工艺流程";
   public static final String SHOWVIEWZIYAN = "自研产品流程";
   public  static final String SELECT_UPLOAD_FILE = "选择需要上传的文件：";

    public  static final String ACTIVITYNAME_SHEJISHUJUGONGYIYUSHEN = "工艺预审汇总";
    public  static final String ACTIVITYNAME_GONGYIYUSHENLUOSHIYIJIANFANKUI = "工艺预审落实意见反馈";
    public  static final String ACTIVITYNAME_ZHIPAIGONGYIZUZHANG = "指派工艺组长";
	public static String ACTIVITYNAME_ZPGYY = "指派工艺员";
	public static String ACTIVITYNAME_ZPGYHQ = "指派工艺会签";
	public static String ACTIVITYNAME_GYHQ = "工艺会签";
	public static String ACTIVITYNAME_WZHQ = "物资会签";
	public static String ACTIVITYNAME_ZPWZHQ = "指派物资会签";
	public static String ACTIVITYNAME_GYHQHZ = "工艺会签汇总";
	public static String ACTIVITYNAME_XHJSFZRHQ = "型号结算负责人会签";
	public static final String ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN = "指派工艺会签部门";

    public  static final String ROLE_WUZIBUWUZIYUAN = "物资部物资员";

    public  static final String WFN_PROCESS_CAILIAODINGE = "材料定额流程";
    public  static final String WFN_PROCESS_DOCUMENT = "工艺通知单签审流程";
    public  static final String WFN_PROCESS_WAIXIEDOCUMENT = "外协技术协议签审流程";
    public  static final String WFN_PROCESS_TONGYONGDOCUMENT = "通用工艺签审流程";
    public  static final String WFN_PROCESS_GONGYIFANGAN = "工艺方案签审流程";
    public  static final String WFN_PROCESS_GONGYIFANGANBAOGAO = "工艺方案报告签审流程";
    public  static final String WFN_DOCUMENT_ECN = "文档更改单签审流程";
    public  static final String WFN_PROCESS_ECN = "工艺更改单签审流程";
    public  static final String WFN_REPORT_PROCESS_ECN = "报表类工艺更改单签审流程";
    public  static final String WFN_WUJIPROCESSWF = "五级工艺文件签审流程";
    public  static final String WFN_WUJIREPORTPROCESSWF = "五级报表类工艺文件签审流程";
    public  static final String PROCESS_NOTICE_DOCUMENT = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_NOTICE";
	public static final String TASK_SHEZHIFENFAFANWEIHEFENSHU = "设置分发部门和份数";

	public static final String JSP_DISPLAY_FENSHU = "份数";

	public static final String JSP_DISPLAY_OK = "确定";
	public static final String JSP_DISPLAY_CANCEL = "取消";

	public static final String TASK_ZHIPAIGONGYIHUIQIANBUMEN = "指派工艺会签部门";
	public static final String DOC_LC = "文档生命周期";

	public static final String FOLDER_PATH1="/Default/01总体/01图样及数模";
	public static final String WFN_SANJIPROCESSWF = "三级工艺文件签审流程";
	public static final String WFN_SANJIREPORTPROCESSWF = "三级报表类工艺文件签审流程";
	public static final String WFN_SANJIGENGGAIWF = "三级工艺文件更改流程";


	public static final String GROUP_NAME_YUNZAI = "部门_运载项目部";
	public static final String GROUP_NAME_GONGYIGUANLIZU = "工艺管理组";


	public static final String CWBM_MESSAGE = "当前的流程不支持此功能!";
	public static final String QINGSHURUZHENGQUEMINGCHENG = "请输入正确的名称！";
	public static final String JUESE_XIAODUIZHE = "角色.校对者";
	public static final String JUESE_SHENHEZHE = "角色.审核者";
	public static final String JUESE_NEIBUHUIQIANZHE = "角色.内部会签者";
	public static final String JUESE_BIAOSHENZHE = "角色.标审者";
	public static final String JUESE_PIZHUNZHE = "角色.批准者";
	public static final String JUESE_DAYINZHE = "角色.打印者";
	public static final String JUESE_GONGSHIDINGEYUAN = "角色.工时定额员";
	public static final String JUESE_WAIBUHUIQIANZHE = "角色.外部会签者";
	public static final String MUBANMINGCHENG = "模板名称:";
	public static final String BITIAN = "必填";

	public static final String HUIQIAN = "会签";
	public static final String BUMENG = "部门";
	public static final String SHIJIAN = "时间";
	public static final String ZHE = "者";


	public  static final String WORKITEM_NAME_TONGZHI_GONGYIQIANSHEN = "通知主任工艺师(工艺签审)";
	public  static final String WORKITEM_NAME_TONGZHI_CAILIAODINGEQIANSHEN = "通知主任工艺师(材料定额签审)";
	public  static final String WORKITEM_NAME_KAIQI_BUBAOHANTONGZHI = "开启的(不包含通知任务)";
	public  static final String WORKITEM_NAME_TONGZHI = "通知";
}
