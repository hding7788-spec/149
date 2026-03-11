package ext.casc.sop.ui;


import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;


@RBUUID("ext.casc.sop.ui.SopActionsRB")
public class SopActionsRB extends WTListResourceBundle {

    @RBEntry("新建SOP资源")
    public static final String SOPZY = "object.create_sopziyuan_submenu.description";

    @RBEntry("编辑SOP资源")
    public static final String Edit_SOPZY = "sopCustom.edit_sopziyuan.description";

    @RBEntry("新建工艺参数项目")
    public static final String SOPCSXM = "sopCustom.createSOPCSXM.description";

    @RBEntry("新建定制区域")
    public static final String SOPDZQY = "sopCustom.createSOPDZQY.description";

    @RBEntry("新建操作岗位")
    public static final String SOPCZGW = "sopCustom.createSOPCZGW.description";

    @RBEntry("新建工序名称")
    public static final String SOPGXMC = "sopCustom.createSOPGXMC.description";

    @RBEntry("新建专业类别")
    public static final String SOPZYLB = "sopCustom.createSOPZYLB.description";

    @RBEntry("新建参数项目名称")
    public static final String SOPCSXMMC = "sopCustom.createSOPCSXMMC.description";

    @RBEntry("新建物资类别")
    public static final String SOPWZLB = "sopCustom.createSOPWZLB.description";

    @RBEntry("新建操作名称")
    public static final String SOPCZMC = "sopCustom.createSOPCZMC.description";

    @RBEntry("新建SOP资源")
    public static final String NEW_SOPZY = "NEW_SOPZY";

    @RBEntry("编辑SOP资源")
    public static final String EDIT_SOPZY = "EDIT_SOPZY";

    @RBEntry("新建工艺参数项目")
    public static final String NEW_SOPCSXM = "NEW_SOPCSXM";

    @RBEntry("新建定制区域")
    public static final String NEW_SOPDZQY = "NEW_SOPDZQY";

    @RBEntry("新建操作岗位")
    public static final String NEW_SOPCZGW = "NEW_SOPCZGW";

    @RBEntry("新建专业类别")
    public static final String NEW_SOPZYLB = "NEW_SOPZYLB";

    @RBEntry("新建工序名称")
    public static final String New_SOPGXMC = "New_SOPGXMC";

    @RBEntry("新建参数项目名称")
    public static final String New_SOPCSXMMC = "New_SOPCSXMMC";

    @RBEntry("新建物资类别")
    public static final String New_SOPWZLB = "New_SOPWZLB";

    @RBEntry("新建操作名称")
    public static final String New_SOPCZMC = "New_SOPCZMC";

    @RBEntry("SOP资源导入")
    public static final String SOP_IMPOERT = "sopCustom.sopMPMResourceImport.title";
    @RBEntry("SOP资源导入")
    public static final String SOP_IMPOERT_2 = "sopCustom.sopMPMResourceImport.description";

    @RBEntry("*请选择EXCEL表文件：")
    public static final String IMPORTDATA_EXCEL_TITLE = "1";

    @RBEntry("在执行导入前，请选择相应的SOP资源文件：")
    public static final String IMPORTDATA_NOTICE = "2";

    @RBEntry("新建SOP零部件")
    public static final String CREATESOPPART_0 = "sopCustom.createSOPPart.title";
    @RBEntry("新建SOP零部件")
    public static final String CREATESOPPART_1 = "sopCustom.createSOPPart.tooltip";
    @RBEntry("新建SOP零部件")
    public static final String CREATESOPPART_2 = "sopCustom.createSOPPart.description";
    @RBEntry("adv_config_part.gif")
    public static final String CREATESOPPART_3 = "sopCustom.createSOPPart.icon";


    @RBEntry("SOP文件BOM导入")
    public static final String IMPORTSOPPART_0 = "sopCustom.importSOPPart.title";
    @RBEntry("SOP文件BOM导入")
    public static final String IMPORTSOPPART_1 = "sopCustom.importSOPPart.tooltip";
    @RBEntry("SOP文件BOM导入")
    public static final String IMPORTSOPPART_2 = "sopCustom.importSOPPart.description";

    @RBEntry("*本机文件路径:")
    public static final String FILE_IMPORT_INSIDE_STEP = "sopCustom.importSOPPart_step.description";

    @RBEntry("SOP设计任务分工")
    public static final String SOPPROASSIGNTASK_0 = "sopCustom.sopProAssignTask.title";
    @RBEntry("SOP设计任务分工")
    public static final String SOPPROASSIGNTASK_1 = "sopCustom.sopProAssignTask.description";
    @RBEntry("attribute_edit.gif")
    public static final String SOPPROASSIGNTASK_2 = "sopCustom.sopProAssignTask.icon";

    @RBEntry("SOP更改任务分工")
    public static final String SOPCHANGEPROASSIGNTASK_0 = "sopCustom.sopChangeProAssignTask.title";
    @RBEntry("SOP更改任务分工")
    public static final String SOPCHANGEPROASSIGNTASK_1 = "sopCustom.sopChangeProAssignTask.description";
    @RBEntry("attribute_edit.gif")
    public static final String SOPCHANGEPROASSIGNTASK_2 = "sopCustom.sopChangeProAssignTask.icon";

    @RBEntry("拒绝任务")
    public static final String REJECTTASK_0 = "sopCustom.rejectTask.title";
    @RBEntry("拒绝任务")
    public static final String REJECTTASK_1 = "sopCustom.rejectTask.description";
    @RBEntry("拒绝选择的任务")
    public static final String REJECTTASK_2 = "sopCustom.rejectTask.tooltip";


    @RBEntry("作废任务")
    public static final String DELETETASK_0 = "sopCustom.deleteTask.title";
    @RBEntry("作废任务")
    public static final String DELETETASK_1 = "sopCustom.deleteTask.description";
    @RBEntry("作废选择的任务")
    public static final String DELETETASK_2 = "sopCustom.deleteTask.tooltip";


    @RBEntry("完成任务")
    public static final String COMPLETETASK_0 = "sopCustom.completeTask.title";
    @RBEntry("完成任务")
    public static final String COMPLETETASK_1 = "sopCustom.completeTask.description";
    @RBEntry("完成选择的任务")
    public static final String COMPLETETASK_2 = "sopCustom.completeTask.tooltip";

    @RBEntry("启动工艺编辑器(SOP)")
    public static final String OPENSOPPROCECSSEDITOR_0 = "sopCustom.openSopProcecssEditor.title";
    @RBEntry("启动工艺编辑器(SOP)")
    public static final String OPENSOPPROCECSSEDITOR_1 = "sopCustom.openSopProcecssEditor.description";
    @RBEntry("启动工艺编辑器(SOP)")
    public static final String OPENSOPPROCECSSEDITOR_2 = "sopCustom.openSopProcecssEditor.tooltip";
    @RBEntry("process_plan.gif")
    public static final String OPENSOPPROCECSSEDITOR_3 = "sopCustom.openSopProcecssEditor.icon";

    @RBEntry("关联对象")
    public static final String RELATEDSOPPROCESSPLAN_1 = "sopCustom.relatedSopProcessPlan.description";

}
