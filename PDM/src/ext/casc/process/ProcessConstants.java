package ext.casc.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessConstants {
	public static String NUMBER = "编号";
	public static String NAME = "名称";
    public static String REPORT_SUCCESS_MES = "操作成功,报表类任务已完成！";
    public static String REPORT_FAILURE_MES = "操作失败,请检查零部件下是否有报表类工艺未完成！";
    public static String GROUP_GONGYIZUZHANG_NAME = "工艺组长";
    public static String GROUP_GONGYIZUZHANG_NAME_SPLIT = "_工艺组长";

    public static String TASK_STATE_JINGXINZHONG = "正在进行";
    public static String TASK_STATE_YIWANGONG = "已完工";
    public static String TASK_STATE_YIZUOFEI = "已作废";

    public static String TASKITEM_STATE_ZHENGZAIJINGXIN = "正在进行";
    public static String TASKITEM_STATE_YIWANCHENG = "已完成";
    public static String TASKITEM_STATE_CANCLE = "已删除";
    public static String PROCESS_STATE_ZHENGZAIYUNXING = "正在运行";
    public static String PROCESS_STATE_YIZHIXING = "已执行";
    public static String PROCESS_STATE_ZHONGZHI = "已终止";

    public static String TASK_TYPE_LINSHIGONGYI = "临时工艺任务";
    public static String TASK_TYPE_GONGYISHEJI = "工艺设计任务";
    public static String TASK_TYPE_BAOBIAOLEI = "报表类工艺任务";
    public static String TASK_TYPE_GONGYIGENGGAI = "工艺更改任务";
    public static String REPORT_TASK_TYPE_GONGYIGENGGAI = "报表类工艺编制";
    public static String TASK_TYPE_ALL = "全部";

    public static String ROLE_GONGYIZUZHANG = "工艺组长";
    public static String ROLE_ZHURENGONGYISHI = "主任工艺师";
    public static String ROLE_GONGYIYUAN = "工艺员";
    public static String TASK_CREATOR = "任务提交者";

    public static String TASK_NAME_ZHIPAIGONGYIZUZHANG = "指派工艺组长";
    public static String TASK_NAME_ZHIPAIGONGYIZUZHANG_JUJUE = "指派工艺组长_拒绝";
    public static String TASK_NAME_ZHIPAIGONGYIYUAN = "工艺任务指派";
    public static String TASK_NAME_ZHIPAIGONGYIYUAN_JUJUE = "工艺任务指派_拒绝";
    public static String TASK_NAME_BIANZHIGONGYI = "工艺编制";
    public static String TASK_NAME_FUZHIBIANZHIGONGYI = "辅制工艺编制";
    public static String TASK_NAME_GONGYIGENGGAIRENWUZHIPAI = "工艺更改任务指派";
    public static String TASK_NAME_BAOBIAOLEIRENWUZHIPAI = "报表类工艺任务指派";
    public static String TASK_NAME_BAOBIAOLEIRENWUZHIPAI_REFUSE = "报表类工艺任务指派_拒绝";
    public static String TASK_NAME_BAOBIAOLEIRENWUZUOFEI = "报表类工艺任务_作废";
    public static String TASK_NAME_GONGYIGENGGAIRENWUZHIPAI_JUJUE = "工艺更改任务指派_拒绝";
    public static String TASK_NAME_GONGYIGENGGAIRENWU = "工艺更改任务";
    public static String TASK_NAME_FUZHIGONGYIGENGGAIRENWU = "辅制工艺更改任务";
    public static String TASK_NAME_LINSHIGONGYIRENWUZHIPAI = "临时工艺任务指派";
    public static String TASK_NAME_LINSHIGONGYIRENWUZHIPAI_JUJUE = "临时工艺任务指派_拒绝";
    public static String TASK_NAME_LINSHIGONGYIRENWU = "临时工艺任务";
    public static String TASK_NAME_FUZHILINSHIGONGYIRENWU = "辅制临时工艺任务";
    public static String TASK_NAME_LINGBUJIANGONGYIRENWUZHIPAI = "零部件工艺任务指派";
    public static String TASK_NAME_LINGBUJIANGONGYIRENWUZHIPAI_JUJUE = "零部件工艺任务指派_拒绝";
    public static String TASK_NAME_LINGBUJIANGONGYIRENWU = "零部件工艺任务";
    public static String TASK_NAME_WUXUBIANZHIGONGY = "辅制工艺意见反馈";
    public static String TASK_NAME_FUZHIZHIPAIGONGYIYUAN = "辅制工艺任务指派";
    public static String TASK_NAME_FUZHIGONGYIGENGGAIRENWUZHIPAI = "辅制更改工艺任务指派";
    public static String TASK_NAME_FUZHILINSHIGONGYIRENWUZHIPAI = "辅制临时工艺任务指派";
    public static String TASK_NAME_END_JUJUE = "拒绝";


    public static String TASK_TYPE_ZZGYRW = "主制工艺任务";
    public static String TASK_TYPE_FZGYRW = "辅制工艺任务";
    public static String TASK_TYPE_BBLGY = "报表类工艺任务";
    public static String GYLXB = "工艺路线表";
    public static String GYZBMXB = "工艺装备明细表";
    public static String YQYBMXB = "仪器仪表明细表";
    public static String FYQYBMXB = "非标仪器仪表、设备明细表";
    public static String BZDLJMXB = "标准刀量具明细表";
    public static String WXJMXB = "外协件明细表";
    public static String GJGXMXB = "关键工序明细表";
    public static String CLXHDEMXB = "材料消耗工艺定额明细表";
    public static String FZCLDEB = "辅助材料定额表";
    public static String FZCLDEHZB = "辅助材料定额汇总表";
    public static String XHGYDEHZB = "外购件（元器件、标准件）消耗工艺定额汇总表";
    public static String GYWJML = "工艺文件目录";

    public static String TASK_ISZHUZHI_SHI = "是";
    public static String TASK_ISZHUZHI_FOU = "否";

    public static String TASKITEM_ROUTESELECT_BOHUI = "驳回";
    public static String TASKITEM_ROUTESELECT_WANCHENGRENWU = "完成任务";
    public static String TASKITEM_ROUTESELECT_WUXUBIANZHIGONGY = "无需编制工艺";

    public static String TASKITEM_JSP_ZHIPAIGONGYIYUAN = "指派工艺员";
    public static String TASKITEM_JSP_ZHIPAIGONGYIZUZHANG = "指派工艺组长";
    public static String TASKITEM_JSP_XUANZHEZHUZHICHEJIAN = "选择主制车间";
    public static String TASKITEM_JSP_XUANZHEFUZHICHEJIAN = "选择辅制车间";
    public static String TASKITEM_JSP_SELECTUSER = "选择工艺员";

    public static String JSP_SEARCH_ALLCONTAINER = "所有上下文";
    public static String JSP_SEARCH_CONTAINER = "上下文：";
    public static String JSP_SEARCH_NUMBER = "部件编号：";
    public static String JSP_SEARCH_NAME = "部件名称：";
    public static String JSP_SEARCH_VERSION = "部件版本：";
    public static String JSP_SEARCH_TASKTYPE = "任务类型：";
    public static String JSP_SEARCH_TASKSTATE = "任务状态：";
    public static String JSP_SEARCH_ENDDATE = "任务分配时间：";
    public static String JSP_SEARCH_TO = " 至 ";
    public static String JSP_SEARCH_CHEJIAN = "车间：";
    public static String JSP_SEARCH_GONGYIYUAN = "当前查询者：";
    public static String JSP_SEARCH_ISZHUZHI = "是否主制：";
    public static String JSP_SEARCH_SEARCHBUTTON = "查    询";
    public static String JSP_SEARCH_RESET = "重    置";
    public static String JSP_SEARCH_BUILD = "新    建";
    public static String JSP_MSG_VALIDTE = "部件编号和名称必须填写一个!";
    public static String JSP_JS_VALIDATE="您必须为所有带星号 (*) 的必填字段，指定有效的信息。";
    public static String JSP_MSG_SUCCESS = "工艺任务分工成功完成！";
    public static String JSP_MSG_FAILD = "工艺任务分工失败,请检查必填信息！";
    public static String JSP_ACTIONS_COMPLETED_ERROR = "操作失败，请选择正在进行的工艺任务活动！";
    public static String JSP_ACTIONS_COMPLETED_TYPE_ERROR = "操作失败，任务指派活动不能点击此按钮完成任务！";
    public static String JSP_ACTIONS_COMPLETED_NOSELECTUSER_ERROR = "操作失败，请选择工艺员后再完成任务！";
    public static String JSP_ACTIONS_COMPLETED_NOSELECTUSER_ZUZHANG_ERROR = "操作失败，请选择工艺组长后再完成任务！";
    public static String JSP_ACTIONS_COMPLETED_CANNOTZHIPAI_ERROR = "操作失败，指派工艺组长活动不能被拒绝！";
    public static String JSP_ACTIONS_ADMIN_ERROR = "操作失败，不允许选择管理员进行重新分配！";
    public static String JSP_ACTIONS_REASSIGN_ERROR = "操作失败，请选择正在进行的任务活动进行重新分配！";
    public static String JSP_REASSIGN_MSG_SUCCESS = "操作成功，任务重新分配完成！";
    public static String JSP_REASSIGN_MSG_FAILED = "操作失败，任务未重新分配完成，请联系管理员！";
    public static String JSP_ACTIONS_ZHIPAI_FAILED = "操作失败，请选择工艺任务指派类型的活动！";
    public static String JSP_ACTIONS_ZHIPAI_FAILED2 = "操作失败，请选择正在进行中的工艺任务活动！";
    public static String JSP_ACTIONS_CANCLE_SUCCESS = "操作成功，工艺任务活动已删除！";
    public static String JSP_ACTIONS_CANCLE_FAILED = "操作失败，工艺任务活动未删除，请联系管理员！";
    public static String JSP_ACTIONS_ZUOFEI_SUCCESS = "操作成功，工艺任务活动已作废！";
    public static String JSP_ACTIONS_ZUOFEI_FAILED = "操作失败，工艺任务活动未作废，请联系管理员！";
    public static String JSP_ACTIONS_REASSIGN_FAILED = "拒绝该操作，请选择正在进行中的工艺任务活动！";
    public static String JSP_MSG_DATEINFO = "请选择今天以后的日期！";
    public static String JSP_BUTTON_OK = "确定";
    public static String JSP_BUTTON_CANCEL = "取消";
    public static String JSP_BUTTON_SELECT = "选择";

    public static String PICI="批次";
    public static String DOC_STYLE="文档类型";
    public static String GYFFA_STYLE="工艺分方案";
    public static String QTLWD="其他类文档";


    public static String PART_VIEW_DESIGN = "Design";
    public static String PART_VIEW_MANUFACTURING = "Manufacturing";
    public static String ROLE_KEY_GONGYIZUZHANG = "GONGYIZUZHANG";

   /* public static String ROLE_NAME_YICHEJIAN = "一车间工艺员";
    public static String ROLE_NAME_ERCHEJIAN = "二车间工艺员";
    public static String ROLE_NAME_SANCHEJIAN = "三车间工艺员";
    public static String ROLE_NAME_SICHEJIAN = "四车间工艺员";
    public static String ROLE_NAME_WUCHEJIAN = "五车间工艺员";
    public static String ROLE_NAME_LIUCHEJIAN = "六车间工艺员";
    public static String ROLE_NAME_QICHEJIAN = "七车间工艺员";
    public static String ROLE_NAME_BACHEJIAN = "八车间工艺员";
    public static String ROLE_NAME_JIUCHEJIAN = "九车间工艺员";
    public static String ROLE_NAME_XIANGMUBU = "项目部工艺员";
    public static String ROLE_HOUQINBUGONGYIYUAN = "后勤部工艺员";*/

 public static String QIDONGCONG = "启动从";
    public static String QIDONGZHI = "启动至";

    public static String WANCHENGCONG = "完成从";
    public static String WANCHENGZHI = "完成至";
    public static String JIHUACONG = "计划从";
    public static String JIHUAGZHI = "计划至";

    public static String SHOUKONGGCONG = "受控从";
    public static String SHOUKONGZHI = "受控至";
    public static String SHUJULEIXING = "数据类型";
    public static String LIUCHENGZHUANGTAI = "流程状态";
    public static String WENJIANMINGCHENG = "文件名称";
    public static String WENJIANBIANHAO = "文件编号";
    public static String PART = "部件";
    public static String DOCUMENT = "文档";
    public static String ProcessEnvelope = "签审包";

    public static String WANCHENGSHIJIAN = "完成时间";
    public static String YUQIRENWU = "逾期任务";
    public static String SHENGMINGZHOUQIZHUANGTAI = "生命周期状态";
    public static String CHUANGJIANSHIJIAN = "创建时间";

    public static String XINGHAO = "型号";
    public static String XZXYDMB = "选择现有的模板";
    public static String CUNWEIMUBAN = "存为模板";
    public static String SHANCHUMUBAN = "删除模板";
    public static List<String> titles=new ArrayList<String>();
    public static List<String> docLifecycleStateList=new ArrayList<String>();
    public static List<String> technicsDocLifecycleStateList=new ArrayList<String>();

    public static Map<String, String> map=new  HashMap<String, String>();
    static{
    	titles.add("技术攻关");
    	titles.add("技术总结");
    	titles.add("技术课题");
    	titles.add("技术改进");
    	titles.add("技术试验");
//    	正在工作 - 修改中 - 校对 - 审核 - 内部会签 - 外部会签 - 工艺会签 - 工艺会签通过 - 工艺会签驳回 - 标审 - 批准 - 已批准 - 已作废
    	docLifecycleStateList.add("正在进行");
    	docLifecycleStateList.add("已完成");
    	docLifecycleStateList.add("已作废");

    	technicsDocLifecycleStateList.add("正在工作");
    	technicsDocLifecycleStateList.add("修改中");
    	technicsDocLifecycleStateList.add("校对");
    	technicsDocLifecycleStateList.add("审核");
    	technicsDocLifecycleStateList.add("内部会签");
    	technicsDocLifecycleStateList.add("外部会签");
        technicsDocLifecycleStateList.add("工艺会签");
        technicsDocLifecycleStateList.add("工艺会签通过");
        technicsDocLifecycleStateList.add("工艺会签驳回");
        technicsDocLifecycleStateList.add("标审");
        technicsDocLifecycleStateList.add("批准");
        technicsDocLifecycleStateList.add("已批准");
        technicsDocLifecycleStateList.add("已作废");


        map.put("INWORK", "正在工作");
        map.put("REWORK", "修改中");
        map.put("PROOFREAD", "校对");
        map.put("REVIEW", "审核");
        map.put("INTERNALCOUNTERSIGN", "内部会签");
        map.put("EXTERNALCOUNTERSIGN", "外部会签");
        map.put("STANDARDIZATIONEXAM", "标审");
        map.put("APPROVED", "已批准");
        map.put("OBSOLESCENCE", "已作废");


    }

    public static String JSP_ACTIONS_NEWPROCESSPLAN_FAILED = "新建非工艺设计类工艺任务计划失败！";
    public static String JSP_ACTIONS_NEWPROCESSPLAN_SUCCESS = "新建非工艺设计类工艺任务任务计划成功！";

    public static String JSP_ACTIONS_NEWCHILDPROCESSPLAN_FAILED = "新建非工艺子计划失败！";
    public static String JSP_ACTIONS_NEWCHILDPROCESSPLAN_SUCCESS = "新建非工艺子计划成功！";

    public static String JSP_ACTIONS_ZHIPAIFGYPROCESSTASKITEM_FAILED = "非工艺计划任务下达失败！";
    public static String JSP_ACTIONS_ZHIPAIFGYPROCESSTASKITEM_SUCCESS = "非工艺计划任务下达成功！";


    public static String TASK_TYPE_FEIGONGYISHEJI = "非工艺设计类工艺任务";
    public static String TASK_NAME_FEIGONGYISHEJIZHIPAI = "非工艺设计类工艺任务指派";
    public static String TASK_NAME_FEIGONGYISHEFANKUI = "非工艺设计类工艺任务问题反馈";

    public static String TASK_STATE_FEIGONGZHIPAIZHONG = "指派中";
    public static String TASK_STATE_FEIGONGZHENGZAIJINXING = "正在进行";
    public static String TASK_STATE_FEIGONGYIWANCHENG = "已完成";
    public static String TASK_STATE_FEIGONGYIZUZHANGJUJUE = "组长拒绝";

    public static String JIHUABIANHAO = "*计划编号:";
    public static String JIHUAMINGCHENG = "*计划名称:";
    public static String JIHUAZHUTI = "*计划主题:";
    public static String XIANGMUBU = "*项目部:";
    public static String XINGHAO2 = "*型号:";

    public static String RENWUMINGCHENG = "*任务名称:";
    public static String ZHIXINGBUMEN = "*执行部门:";
    public static String JIHUAWANCHENGSHIJIAN = "*计划完成时间:";
    public static String RENWUYAOQIU = "任务要求:";
    public static String ZHUZHICHEJIAN = "主制车间:";
    public static String XINGHAODAIHAO = "型号代号:";
    public static String DAYINZHUANGTAI = "打印状态:";
    public static String GONGYIWENJIANLEIXING = "工艺文件类型:";
    public static String ZHENGSHIGONGYIWENJIAN = "正式工艺文件";
    public static String LINSHIGONGYIWENJIAN = "临时工艺文件";

    public static String CLDEJHWANCHENGSHIJIANCONG = "材料定额计划完成时间从";
    public static String CLDEJHWANCHENGSHIJIANZHI = "材料定额计划完成时间至";

    public static String LIUCHENGNAME = "流程名称";
    public static String LC_1 = "149正式发放包流程";
    public static String LC_2 = "149签审包工艺会签流程";
    public static String LC_3 = "149变更签审包工艺会签流程";
    public static String LC_4 = "149变更签审包技术会签流程";
    public static String LC_5 = "149变更申请包工艺会签流程";
    public static String LC_6 = "149变更申请包技术会签流程";
    public static String LC_7 = "工艺更改单签审流程";
    public static String LC_8 = "三级工艺文件签审流程";
    public static String LC_9 = "五级工艺文件签审流程";
    public static String LC_10 = "工艺通知单签审流程";
    public static String LIUCHENGHUANJIE = "流程环节";








}
