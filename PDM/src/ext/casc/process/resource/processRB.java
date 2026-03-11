package ext.casc.process.resource;

import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("ext.casc.process.resource.processRB")
public class processRB extends WTListResourceBundle {

    @RBEntry("工艺设计任务分工")
    public static final String PBOMPROASSIGNTASK_TITLE = "customProcessTask.PBOMProAssignTask.title";

    @RBEntry("报表类工艺任务分工")
    public static final String TechnicsReportAssignTask_TITLE = "customProcessTask.TechnicsReportAssignTask.title";
    @RBEntry("工艺更改任务分工")
    public static final String CHANGEPROASSIGNTASK_TITLE = "customProcessTask.ChangeProAssignTask.title";

    @RBEntry("零部件工艺任务分工")
    public static final String PARTPROASSIGNTASK_TITLE = "customProcessTask.PartProAssignTask.title";

    @RBEntry("临时工艺任务分工")
    public static final String TMEPPROASSIGNTASK_TITLE = "customProcessTask.tempProAssignTask.title";

    @RBEntry("补加工艺任务分工")
    public static final String ADDPROASSIGNTASK_TITLE = "customProcessTask.addProAssignTask.title";

    @RBEntry("指派工艺员")
    public static final String ZHIPAIGONGYIYUAN_TITLE = "customProcessTask.zhipaigongyiyuan.title";

    @RBEntry("工艺任务表格视图")
    @RBComment("工艺任务表格视图")
    public static final String PROCESSASSIGNMENT_TABLE_VIEWS = "PROCESSASSIGNMENT_TABLE_VIEWS";

    @RBEntry("已完工")
    @RBComment("已完工的工艺任务")
    public static final String YIWANGONG_TABLE_VIEW = "YIWANGONG_TABLE_VIEW";

    @RBEntry("正在进行")
    @RBComment("正在进行的工艺任务")
    public static final String ZHENGZAIJINXING_TABLE_VIEW = "ZHENGZAIJINXING_TABLE_VIEW";

    @RBEntry("工艺设计任务")
    @RBComment("工艺设计任务")
    public static final String GONGYISHEJI_TABLE_VIEW = "GONGYISHEJI_TABLE_VIEW";

    @RBEntry("工艺更改任务")
    @RBComment("工艺更改任务")
    public static final String GONGYIGENGGAI_TABLE_VIEW = "GONGYIGENGGAI_TABLE_VIEW";

    @RBEntry("临时工艺任务")
    @RBComment("临时工艺任务")
    public static final String LINSHIGONGYI_TABLE_VIEW = "LINSHIGONGYI_TABLE_VIEW";

    /**** 用于工艺任务详细信息页面显示  ****/
    @RBEntry("名称:")
    public static final String PROCESSTASK_NAME = "100";

    @RBEntry("编号:")
    public static final String PROCESSTASK_NUMBER = "101";

    @RBEntry("版本:")
    public static final String PROCESSTASK_VERSION = "102";

    @RBEntry("主制车间:")
    public static final String PROCESSTASK_ZHUZHICHEJIAN = "103";

    @RBEntry("复制车间:")
    public static final String PROCESSTASK_FUZHICHEJIAN = "104";

    @RBEntry("任务开始时间:")
    public static final String PROCESSTASK_STARTDATE = "105";

    @RBEntry("计划完成时间:")
    public static final String PROCESSTASK_ENDDATE = "106";

    @RBEntry("任务状态:")
    public static final String PROCESSTASK_TASKSTATE = "107";

    @RBEntry("任务类型:")
    public static final String PROCESSTASK_TASKTYPE = "108";

    @RBEntry("任务要求:")
    public static final String PROCESSTASK_RENWUYAOQIU = "109";

    @RBEntry("任务依据:")
    public static final String PROCESSTASKITEM_RENWUYIJU = "110";

    @RBEntry("材料定额计划开始时间:")
    public static final String PROCESSTASK_CLDEPLANTIME = "111";

    @RBEntry("材料定额计划结束时间:")
    public static final String PROCESSTASK_CLDEENDTIME = "112";

    /**** 用于工艺任务活动条目详细信息页面显示  ****/

    @RBEntry("任务名称:")
    public static final String PROCESSTASKITEM_TASKNAME = "200";

    @RBEntry("名称:")
    public static final String PROCESSTASKITEM_NAME = "201";

    @RBEntry("编号:")
    public static final String PROCESSTASKITEM_NUMBER = "202";

    @RBEntry("任务类型:")
    public static final String PROCESSTASKITEM_TASKTYPE = "203";

    @RBEntry("任务状态:")
    public static final String PROCESSTASKITEM_TASKSTATE = "204";

    @RBEntry("任务开始时间:")
    public static final String PROCESSTASKITEM_STARTDATE = "205";

    @RBEntry("计划完成时间:")
    public static final String PROCESSTASKITEM_ENDDATE = "206";

    @RBEntry("是否主制:")
    public static final String PROCESSTASKITEM_ISZHUZHI = "207";

    @RBEntry("角色:")
    public static final String PROCESSTASKITEM_EXECUTORROLE = "208";

    @RBEntry("任务要求:")
    public static final String PROCESSTASKITEM_RENWUYAOQIU = "209";

    @RBEntry("备注:")
    public static final String PROCESSTASKITEM_BEIZHU = "210";

    @RBEntry("主制工艺文件编号:")
    public static final String PROCESSTASKITEM_ZZGYNUMBER = "211";

    @RBEntry("主制工艺文件名称:")
    public static final String PROCESSTASKITEM_FZGYNUMBER = "212";

    @RBEntry("主辅类别:")
    public static final String PROCESSTASKITEM_ZFTYPE = "213";

    @RBEntry("任务提交者:")
    public static final String PROCESSTASKITEM_TASKCREATOR = "214";

    @RBEntry("报表类型:")
    public static final String PROCESSTASKITEM_BBLX = "215";

    @RBEntry("材料定额计划开始时间:")
    public static final String PROCESSTASKITEM_CLDEPLANTIME = "215";

    @RBEntry("材料定额计划结束时间:")
    public static final String PROCESSTASKITEM_CLDEENDTIME = "216";

    @RBEntry("查找零部件")
    public static final String ADDPART_TITLE = "customProcessTask.addPart.title";

    @RBEntry("创建车间临时工艺任务")
    public static final String TMEPGYZZPROASSIGNTASK_TITLE = "customProcessTask.tempGYZZProAssignTask.title";

    @RBEntry("创建车间工艺更改任务")
    public static final String CHANGEGYZZPROASSIGNTASK_TITLE = "customProcessTask.ChangeGYZZProAssignTask.title";

    @RBEntry("关联更改影响分析:")
    public static final String PROCESSTASKITEM_ANALYSIS = "220";

    @RBEntry("关联更改影响源:")
    public static final String PROCESSTASKITEM_SOURCE = "221";
}