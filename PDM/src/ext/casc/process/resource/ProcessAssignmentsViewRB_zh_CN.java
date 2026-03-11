package ext.casc.process.resource;

import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("ext.casc.process.resource.ProcessAssignmentsViewRB")
public class ProcessAssignmentsViewRB_zh_CN extends WTListResourceBundle {
    @RBEntry("工艺任务表格视图")
    @RBComment("工艺任务表格视图")
    public static final String PROCESSASSIGNMENT_TABLE_VIEWS = "PROCESSASSIGNMENT_TABLE_VIEWS";
    
    @RBEntry("已完成")
    @RBComment("已完成的工艺任务活动")
    public static final String YIWANCHENG_TABLE_VIEW = "YIWANCHENG_TABLE_VIEW";
    
    @RBEntry("已完工")
    @RBComment("已完工的工艺任务")
    public static final String YIWANGONG_TABLE_VIEW = "YIWANGONG_TABLE_VIEW";
    
    @RBEntry("正在进行")
    @RBComment("正在进行的工艺任务")
    public static final String ZHENGZAIJINXING_TABLE_VIEW = "ZHENGZAIJINXING_TABLE_VIEW";
    
    @RBEntry("已作废")
    @RBComment("已作废的工艺任务")
    public static final String YIZUOFEI_TABLE_VIEW = "YIZUOFEI_TABLE_VIEW";
    
    @RBEntry("已删除")
    @RBComment("已删除的工艺任务")
    public static final String YISHANCHU_TABLE_VIEW = "YISHANCHU_TABLE_VIEW";
    
    @RBEntry("工艺设计任务")
    @RBComment("工艺设计任务")
    public static final String GONGYISHEJI_TABLE_VIEW = "GONGYISHEJI_TABLE_VIEW";
    
    @RBEntry("工艺更改任务")
    @RBComment("工艺更改任务")
    public static final String GONGYIGENGGAI_TABLE_VIEW = "GONGYIGENGGAI_TABLE_VIEW";
    
    @RBEntry("临时工艺任务")
    @RBComment("临时工艺任务")
    public static final String LINSHIGONGYI_TABLE_VIEW = "LINSHIGONGYI_TABLE_VIEW";
    
    @RBEntry("全部")
    @RBComment("全部")
    public static final String ALL_TABLE_VIEW = "ALL_TABLE_VIEW";
}
