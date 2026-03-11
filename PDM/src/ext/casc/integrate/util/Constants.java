package ext.casc.integrate.util;



import java.util.ArrayList;
import java.util.List;

public class Constants {

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
    public static final String WF_149APPROVAL_HUIQIAN = "149签审包工艺会签流程";
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

    public static final String TYPE_ZIZHIJIAN = "自制件";
    public static final String TYPE_BIAOZHUNJIAN = "标准件";
    public static final String TYPE_WAIXIEJIAN = "外协件";//----149以前处理方式，后续废除
    public static final String TYPE_WAIGOUJIAN = "外购件";
    public static final String TYPE_WAIPEITAOJIAN = "外配套件";//chaoxiaona 2013-11-20
    public static final String TYPE_DAILIAOWEIWAIJIAN = "带料委外件";//chaoxiaona 2013-11-25
    public static final String TYPE_BUDAILIAOWEIWAIJIAN = "不带料委外件";//chaoxiaona 2013-11-25

    public static final String TYPE_YUANQIJIAN = "元器件";
    public static final String TYPE_FUZHUCAILIAO = "辅助材料";
    public static final String[] TYPE_MATERIALS = {TYPE_WAIGOUJIAN,TYPE_BIAOZHUNJIAN,TYPE_YUANQIJIAN,TYPE_FUZHUCAILIAO};
    public static final List<String> list = new ArrayList<String>();
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

    public static String SETMARK_IS = "是";
  //////////////////////
  //chaoxiaona 2013-11-20

    public static final String PROCESS_TYPEC_PRIMARY = "Z";
    public static final String PROCESS_TYPEC_ASSIST = "F";

    public static final String PROCESS_TYPEB_FORMAL = "正式工艺文件";
    public static final String PROCESS_TYPEB_TEMP = "临时工艺文件";

    public static final String LABEL_PROCESS_TYPEC_PRIMARY = "PRIMARY";
    public static final String LABEL_PROCESS_TYPEC_ASSIST = "ASSIST";
    public static final String LABEL_PROCESS_TYPEC_ALL = "ALL";

    public static final String LABEL_PROCESS_TYPEB_FORMAL = "FORMAL";
    public static final String LABEL_PROCESS_TYPEB_TEMP = "TEMP";
    public static final String LABEL_PROCESS_TYPEB_ALL = "ALL";


    public static final String LABEL_VERSION_BATCH = "BATCH";
    public static final String ZF_SUFFIX = "_ZF";
    public static final String DX_SUFFIX = "_DX";
    public static final String ZFDX_SUFFIX = "_ZFDX";
    //////////////////////
}
