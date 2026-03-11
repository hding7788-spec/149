package ext.casc.ui;

import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("ext.casc.ui.actionsRB")
public class actionsRB extends WTListResourceBundle{


	@RBEntry("重置状况")
    public static final String resetReadyState_010 = "custom.resetReadyState.title";
    @RBEntry("重置状况")
    public static final String resetReadyState_011 = "custom.resetReadyState.description";

    @RBEntry("提交签审")
    public static final String SUBMIT_APPROVAL_010 = "custom.submitApproval.title";
    @RBEntry("提交签审")
    public static final String SUBMIT_APPROVAL_011 = "custom.submitApproval.description";

    @RBEntry("重新生成PDF")
    public static final String rePdf_010 = "custom.rePdf.title";
    @RBEntry("重新生成PDF")
    public static final String rePdf_011 = "custom.rePdf.description";

    @RBEntry("重新电子签名")
    public static final String rePrintPdf_010 = "custom.rePrintPdf.title";
    @RBEntry("重新电子签名")
    public static final String rePrintPdf_011 = "custom.rePrintPdf.description";

    @RBEntry("重新生成PDF版本信息")
    public static final String rePdfVersion_010 = "custom.rePdfVersion.title";
    @RBEntry("重新生成PDF版本信息")
    public static final String rePdfVersion_011 = "custom.rePdfVersion.description";

    @RBEntry("修订工艺")
    public static final String reviseProcessPlanDoc_010 = "custom.reviseProcessPlanDoc.title";
    @RBEntry("修订工艺")
    public static final String reviseProcessPlanDoc_011 = "custom.reviseProcessPlanDoc.description";

    @RBEntry("提交工艺签审")
    public static final String SUBMIT_MPMPPLANAPPROVAL_010 = "customMPMPPlan.submitReview.title";
    @RBEntry("提交工艺签审")
    public static final String SUBMIT_MPMPPLANAPPROVAL_011 = "customMPMPPlan.submitReview.description";

    @RBEntry("批量设置零部件分类")
    @RBComment("Set Part Type")
    public static final String CUSTOM_SETPARTTYPE_0 = "custom.setPartType.title";
    @RBEntry("批量设置零部件分类")
    @RBComment("Set Part Type")
    public static final String CUSTOM_SETPARTTYPE_1 = "custom.setPartType.description";
    @RBEntry("批量设置零部件分类")
    @RBComment("Set Part Type")
    public static final String CUSTOM_SETPARTTYPE_2 = "custom.setPartType.tooltip";
    @RBEntry("attribute_edit.gif")
    public static final String CUSTOM_SETPARTTYPE_3 = "custom.setPartType.icon";

    @RBEntry("批量设置工艺路线")
    @RBComment("Set Process Path")
    public static final String CUSTOM_SETPROCESSPATH_0 = "custom.setProcessPath.title";
    @RBEntry("批量设置工艺路线")
    @RBComment("Set Process Path")
    public static final String CUSTOM_SETPROCESSPATH_1 = "custom.setProcessPath.description";
    @RBEntry("批量设置工艺路线")
    @RBComment("Set Process Path")
    public static final String CUSTOM_SETPROCESSPATH_2 = "custom.setProcessPath.tooltip";
    @RBEntry("attribute_edit.gif")
    public static final String CUSTOM_SETPROCESSPATH_3 = "custom.setProcessPath.icon";

    @RBEntry("生成存货管理报表")
    public static final String CHGLREPORT_1 = "customPartReport.chglReport.title";
    @RBEntry("生成存货管理报表")
    public static final String CHGLREPORT_2 = "customPartReport.chglReport.description";
    @RBEntry("report.gif")
    public static final String CHGLREPORT_3 = "customPartReport.chglReport.icon";

	@RBEntry("导出报表")
    public static final String SIGNATUREREPORT_1 = "showListSignature.exportReport.title";
    @RBEntry("导出报表")
    public static final String SIGNATUREREPORT_2 = "showListSignature.exportReport.description";
    @RBEntry("report.gif")
    public static final String SIGNATUREREPORT_3 = "showListSignature.exportReport.icon";

    @RBEntry("临时保存意见")
    public static final String CUSTOM_SAVEWRITEINFO_TITLE = "custom.saveWriteInfo.title";
    @RBEntry("临时保存意见")
    public static final String CUSTOM_SAVEWRITEINFO_DESCRIPTION = "custom.saveWriteInfo.description";
    @RBEntry("临时保存意见")
    public static final String CUSTOM_SAVEWRITEINFO_TOOLTIP = "custom.saveWriteInfo.tooltip";
    @RBEntry("save.gif")
    public static final String CUSTOM_SAVEWRITEINFO_ICON = "custom.saveWriteInfo.icon";

    @RBEntry("保存填写的信息")
    public static final String CUSTOM_SAVEGYRWFINFO_TITLE = "customProcessTask.saveGYRWFGInfo.title";
    @RBEntry("保存填写的信息")
    public static final String CUSTOM_SAVEGYRWFINFO_DESCRIPTION = "customProcessTask.saveGYRWFGInfo.description";
    @RBEntry("保存填写的信息")
    public static final String CUSTOM_SAVEGYRWFINFO_TOOLTIP = "customProcessTask.saveGYRWFGInfo.tooltip";
    @RBEntry("save.gif")
    public static final String CUSTOM_SAVEGYRWFINFO_ICON = "customProcessTask.saveGYRWFGInfo.icon";

    @RBEntry("动态授权")
    public static final String CUSTOM_DYNAMICAUTHORIZATION_TITLE = "custom.dynamicAuthorization.title";
    @RBEntry("动态授权")
    public static final String CUSTOM_DYNAMICAUTHORIZATION_DESCRIPTION = "custom.dynamicAuthorization.description";
    @RBEntry("动态授权")
    public static final String CUSTOM_DYNAMICAUTHORIZATION_TOOLTIP = "custom.dynamicAuthorization.tooltip";
    @RBEntry("accsstl.gif")
    public static final String CUSTOM_DYNAMICAUTHORIZATION_ICON = "custom.dynamicAuthorization.icon";

    @RBEntry("同步更新EBOM属性")
    @RBComment("Update Part Attributes")
    public static final String CUSTOM_UPDATEPARTATTR_0 = "custom.updatePartAttr.title";
    @RBEntry("同步更新EBOM属性")
    @RBComment("Update Part Attributes")
    public static final String CUSTOM_UPDATEPARTATTR_1 = "custom.updatePartAttr.description";
    @RBEntry("同步更新EBOM属性")
    @RBComment("Update Part Attributes")
    public static final String CUSTOM_UPDATEPARTATTR_2 = "custom.updatePartAttr.tooltip";
    @RBEntry("attribute_edit.gif")
    public static final String CUSTOM_UPDATEPARTATTR_3 = "custom.updatePartAttr.icon";

    @RBEntry("User's Guide Download")
    public static final String CUSTOM_USERGUIDEDOWNLOAD_0 = "custom.userGuideDownload.title";
    @RBEntry("User's Guide Download")
    public static final String CUSTOM_USERGUIDEDOWNLOAD_1 = "custom.userGuideDownload.description";

    @RBEntry("下载模型")
    public static final String CUSTOM_DOWNLOADPRIMARYCONTENT_0 = "custom.downloadPrimaryContent.title";
    @RBEntry("下载模型")
    public static final String CUSTOM_DOWNLOADPRIMARYCONTENT_1 = "custom.downloadPrimaryContent.description";
    @RBEntry("下载模型")
    public static final String CUSTOM_DOWNLOADPRIMARYCONTENT_2 = "custom.downloadPrimaryContent.tooltip";
    @RBEntry("download_cmd.gif")
    public static final String CUSTOM_DOWNLOADPRIMARYCONTENT_3 = "custom.downloadPrimaryContent.icon";

    @RBEntry("批量管理产品团队")
    public static final String CUSTOM_PRODUCTMANAGMENT_0 = "custom.productManagment.title";
    @RBEntry("批量管理产品团队")
    public static final String CUSTOM_PRODUCTMANAGMENT_1 = "custom.productManagment.description";

    @RBEntry("修改所选择产品的角色参与者")
    public static final String CUSTOM_UPDATEROLEUSERS_0 = "custom.updateRoleUsers.title";
    @RBEntry("修改所选择产品的角色参与者")
    public static final String CUSTOM_UPDATEROLEUSERS_1 = "custom.updateRoleUsers.description";
    @RBEntry("修改所选择产品的角色参与者")
    public static final String CUSTOM_UPDATEROLEUSERS_2 = "custom.updateRoleUsers.tooltip";
    @RBEntry("user.gif")
    public static final String CUSTOM_UPDATEROLEUSERS_3 = "custom.updateRoleUsers.icon";

    @RBEntry("一键删除所有产品库所有已离职人员")
    public static final String CUSTOM_DELETEALLQUITUSERS_0 = "custom.deleteAllQuitUsers.title";
    @RBEntry("一键删除所有产品库所有已离职人员")
    public static final String CUSTOM_DELETEALLQUITUSERS_1 = "custom.deleteAllQuitUsers.description";
    @RBEntry("一键删除所有产品库所有已离职人员")
    public static final String CUSTOM_DELETEALLQUITUSERS_2 = "custom.deleteAllQuitUsers.tooltip";
    @RBEntry("remove16x16.gif")
    public static final String CUSTOM_DELETEALLQUITUSERS_3 = "custom.deleteAllQuitUsers.icon";

    @RBEntry("转换涉密型号代号")
    public static final String CUSTOM_HIDEALLMINDEX_0 = "custom.hideAllMindex.title";
    @RBEntry("转换涉密型号代号")
    public static final String CUSTOM_HIDEALLMINDEX_1 = "custom.hideAllMindex.description";
    @RBEntry("转换涉密型号代号")
    public static final String CUSTOM_HIDEALLMINDEX_2 = "custom.hideAllMindex.tooltip";
    @RBEntry("netmarkets/images/delete.gif")
    public static final String CUSTOM_HIDEALLMINDEX_3 = "custom.hideAllMindex.icon";

    @RBEntry("添加参与者")
    public static final String CUSTOM_ADDUSERS_0 = "custom.addUsers.title";
    @RBEntry("添加参与者")
    public static final String CUSTOM_ADDUSERS_1 = "custom.addUsers.description";
    @RBEntry("添加参与者")
    public static final String CUSTOM_ADDUSERS_2 = "custom.addUsers.tooltip";
    @RBEntry("add16x16.gif")
    public static final String CUSTOM_ADDUSERS_3 = "custom.addUsers.icon";

    @RBEntry("删除参与者")
    public static final String CUSTOM_DELETEUSERS_0 = "custom.deleteUsers.title";
    @RBEntry("删除参与者")
    public static final String CUSTOM_DELETEUSERS_1 = "custom.deleteUsers.description";
    @RBEntry("删除参与者")
    public static final String CUSTOM_DELETEUSERS_2 = "custom.deleteUsers.tooltip";
    @RBEntry("remove16x16.gif")
    public static final String CUSTOM_DELETEUSERS_3 = "custom.deleteUsers.icon";

    @RBEntry("修改所选择产品的角色")
    public static final String CUSTOM_UPDATEROLES_0 = "custom.updateRoles.title";
    @RBEntry("修改所选择产品的角色")
    public static final String CUSTOM_UPDATEROLES_1 = "custom.updateRoles.description";
    @RBEntry("修改所选择产品的角色")
    public static final String CUSTOM_UPDATEROLES_2 = "custom.updateRoles.tooltip";
    @RBEntry("role.gif")
    public static final String CUSTOM_UPDATEROLES_3 = "custom.updateRoles.icon";

    @RBEntry("添加角色")
    public static final String CUSTOM_ADDROLES_0 = "custom.addRoles.title";
    @RBEntry("添加角色")
    public static final String CUSTOM_ADDROLES_1 = "custom.addRoles.description";
    @RBEntry("添加角色")
    public static final String CUSTOM_ADDROLES_2 = "custom.addRoles.tooltip";
    @RBEntry("add16x16.gif")
    public static final String CUSTOM_ADDROLES_3 = "custom.addRoles.icon";

    @RBEntry("删除角色")
    public static final String CUSTOM_DELETEROLES_0 = "custom.deleteRoles.title";
    @RBEntry("删除角色")
    public static final String CUSTOM_DELETEROLES_1 = "custom.deleteRoles.description";
    @RBEntry("删除角色")
    public static final String CUSTOM_DELETEROLES_2 = "custom.deleteRoles.tooltip";
    @RBEntry("remove16x16.gif")
    public static final String CUSTOM_DELETEROLES_3 = "custom.deleteRoles.icon";

	@RBEntry("启动工艺文件打印申请")
    public static final String CUSTOM_GYRWPRINTTASK_0 = "custom.gywjPrintTask.title";
    @RBEntry("启动工艺文件打印申请")
    public static final String CUSTOM_GYRWPRINTTASK_1 = "custom.gywjPrintTask.description";
    @RBEntry("启动工艺文件打印申请")
    public static final String CUSTOM_GYRWPRINTTASK_2 = "custom.gywjPrintTask.tooltip";
    @RBEntry("print.gif")
    public static final String CUSTOM_GYRWPRINTTASK_3 = "custom.gywjPrintTask.icon";

    @RBEntry("移除")
    public static final String CUSTOM_removeAffectedData_0 = "custom.removeAffectedData.title";
    @RBEntry("移除")
    public static final String CUSTOM_removeAffectedData_1 = "custom.removeAffectedData.description";
    @RBEntry("移除")
    public static final String CUSTOM_removeAffectedData_2 = "custom.removeAffectedData.tooltip";
    @RBEntry("remove16x16.gif")
    public static final String CUSTOM_removeAffectedData_3 = "custom.removeAffectedData.icon";

    @RBEntry("流程报表")
    public static final String CUSTOM_allWorkFlowReport_0 = "custom.allWorkFlowReport.title";
    @RBEntry("流程报表")
    public static final String CUSTOM_allWorkFlowReport_1 = "custom.allWorkFlowReport.description";

    @RBEntry("查询")
    public static final String CUSTOM_searchAllWorkFlow_0 = "custom.searchAllWorkFlow.title";
    @RBEntry("查询")
    public static final String CUSTOM_searchAllWorkFlow_1 = "custom.searchAllWorkFlow.description";
    @RBEntry("查询")
    public static final String CUSTOM_searchAllWorkFlow_2 = "custom.searchAllWorkFlow.tooltip";

    @RBEntry("导出")
    public static final String CUSTOM_reportAllWorkFlow_0 = "custom.reportAllWorkFlow.title";
    @RBEntry("导出")
    public static final String CUSTOM_reportAllWorkFlow_1 = "custom.reportAllWorkFlow.description";
    @RBEntry("导出")
    public static final String CUSTOM_reportAllWorkFlow_2 = "custom.reportAllWorkFlow.tooltip";

    @RBEntry("BOM查询")
    public static final String CUSTOM_BOMReport_0 = "custom.BOMReport.title";
    @RBEntry("BOM查询")
    public static final String CUSTOM_BOMReport_1 = "custom.BOMReport.description";

    @RBEntry("加载工具")
    public static final String CUSTOM_LoadTools_0 = "custom.LoadTools.title";
    @RBEntry("加载工具")
    public static final String CUSTOM_LoadTools_1 = "custom.LoadTools.description";

    @RBEntry("工艺技术文档配置")
    public static final String CUSTOM_technicsTechnologyManager_0 = "custom.technicsTechnologyManager.title";
    @RBEntry("工艺技术文档配置")
    public static final String CUSTOM_technicsTechnologyManager_1 = "custom.technicsTechnologyManager.description";

    @RBEntry("工艺编辑器启动配置")
    public static final String CUSTOM_processEditorManager_0 = "custom.processEditorManager.title";
    @RBEntry("工艺编辑器启动配置")
    public static final String CUSTOM_processEditorManager_1 = "custom.processEditorManager.description";

    @RBEntry("查询")
    public static final String CUSTOM_searchBOMReport_0 = "custom.searchBOMReport.title";
    @RBEntry("查询")
    public static final String CUSTOM_searchBOMReport_1 = "custom.searchBOMReport.description";
    @RBEntry("查询")
    public static final String CUSTOM_searchBOMReport_2 = "custom.searchBOMReport.tooltip";

    @RBEntry("查看会签列表意见")
    public static final String CUSTOM_VIEWSIGNINFO_TITLE = "custom.viewSignatureListInfo.title";
    @RBEntry("查看会签列表意见")
    public static final String CUSTOM_VIEWSIGNINFO_DESCRIPTION = "custom.viewSignatureListInfo.description";
    @RBEntry("查看会签列表意见")

	public static final String CUSTOM_VIEWSIGNINFO_TOOLTIP = "custom.viewSignatureListInfo.tooltip";

    @RBEntry("产品批次管理")
    public static final String CUSTOM_MANAGERBATCHES_0 = "custom.managerBatches.title";
    @RBEntry("产品批次管理")
    public static final String CUSTOM_MANAGERBATCHES_1 = "custom.managerBatches.description";

    @RBEntry("添加批次号")
    public static final String CUSTOM_ADDBATCH_0 = "custom.addBatch.title";
    @RBEntry("添加批次号")
    public static final String CUSTOM_ADDBATCH_1 = "custom.addBatch.description";
    @RBEntry("添加批次号")
    public static final String CUSTOM_ADDBATCH_2 = "custom.addBatch.tooltip";
    @RBEntry("add16x16.gif")
    public static final String CUSTOM_ADDBATCH_3 = "custom.addBatch.icon";

    @RBEntry("删除所选批次号")
    public static final String CUSTOM_DELETEBATCH_0 = "custom.deleteBatch.title";
    @RBEntry("删除所选批次号")
    public static final String CUSTOM_DELETEBATCH_1 = "custom.deleteBatch.description";
    @RBEntry("删除所选批次号")
    public static final String CUSTOM_DELETEBATCH_2 = "custom.deleteBatch.tooltip";
    @RBEntry("remove16x16.gif")
    public static final String CUSTOM_DELETEBATCH_3 = "custom.deleteBatch.icon";

    @RBEntry("添加附件")
    public static final String CUSTOM_ADDPBOATTACH_0 = "custom.addPboAttach.title";
    @RBEntry("添加附件")
    public static final String CUSTOM_ADDPBOATTACH_1 = "custom.addPboAttach.description";
    @RBEntry("添加附件")
    public static final String CUSTOM_DADDPBOATTACH_2 = "custom.addPboAttach.tooltip";
    @RBEntry("add16x16.gif")
    public static final String CUSTOM_ADDPBOATTACH_3 = "custom.addPboAttach.icon";

    @RBEntry("删除附件")
    public static final String CUSTOM_DELETEPBOATTACH_0 = "custom.deletePboAttach.title";
    @RBEntry("删除附件")
    public static final String CUSTOM_DELETEPBOATTACH_1 = "custom.deletePboAttach.description";
    @RBEntry("删除附件")
    public static final String CUSTOM_DELETEPBOATTACH_2 = "custom.deletePboAttach.tooltip";
    @RBEntry("delete.gif")
    public static final String CUSTOM_DELETEPBOATTACH_3 = "custom.deletePboAttach.icon";

    @RBEntry("自定义签名")
    public static final String CUSTOM_SIGNATURE_TITLE = "custom.signature.title";
    @RBEntry("自定义签名")
    public static final String CUSTOM_SIGNATURE_DESCRIPTION = "custom.signature.description";
    @RBEntry("自定义签名")
    public static final String CUSTOM_SIGNATURE_TOOLTIP = "custom.signature.tooltip";

    @RBEntry("下载附件")
    public static final String CUSTOM_DOWNLOADPBOATTACH_0 = "custom.downloadPboAttach.title";
    @RBEntry("下载附件")
    public static final String CUSTOM_DOWNLOADPBOATTACH_1 = "custom.downloadPboAttach.description";
    @RBEntry("下载附件")
    public static final String CUSTOM_DOWNLOADPBOATTACH_2 = "custom.downloadPboAttach.tooltip";
    @RBEntry("netmarkets/images/download.gif")
    public static final String CUSTOM_DOWNLOADPBOATTACH_3 = "custom.downloadPboAttach.icon";

	@RBEntry("外部会签")
	public static final String PRIVATE_CONSTANT_08 = "custom.outSign.description";

	@RBEntry("设置会签人员")
	public static final String PRIVATE_CONSTANT_09 = "custom.setOutSignInfo.description";

	@RBEntry("会签单位")
	public static final String PRIVATE_CONSTANT_10 = "custom.signCompany.description";

	@RBEntry("会签人")
	public static final String PRIVATE_CONSTANT_11 = "custom.signName.description";

	@RBEntry("会签时间")
	public static final String PRIVATE_CONSTANT_12 = "custom.signDate.description";

	@RBEntry("会签意见")
	public static final String PRIVATE_CONSTANT_13 = "custom.signRemark.description";

	@RBEntry("确定")
	public static final String PRIVATE_CONSTANT_14 = "custom.submit.description";

	@RBEntry("添加")
	public static final String PRIVATE_CONSTANT_15 = "custom.addRow.description";

	@RBEntry("发布文件")
	public static final String PRIVATE_CONSTANT_16 = "custom.setApproved.description";

	@RBEntry("添加")
	public static final String ADDSIGNINFO_DESC = "custom.addSignInfo.description";
	@RBEntry("add16x16.gif")
	public static final String ADDSIGNINFO_ICON = "custom.addSignInfo.icon";
	@RBEntry("移除")
	public static final String REMOVESIGNINFO_DESC = "custom.removeSignInfo.description";
	@RBEntry("remove16x16.gif")
	public static final String REMOVESIGNINFO_ICON = "custom.removeSignInfo.icon";

	 @RBEntry("启动PBOM构建流程")
    public static final String CUSTOM_STARTPBOMWORKFLOW_TITLE = "custom.startPbomWorkflow.title";
    @RBEntry("启动PBOM构建流程")
    public static final String CUSTOM_STARTPBOMWORKFLOW_DESCRIPTION = "custom.startPbomWorkflow.description";
    @RBEntry("启动PBOM构建流程")
    public static final String CUSTOM_STARTPBOMWORKFLOW_TOOLTIP = "custom.startPbomWorkflow.tooltip";

    @RBEntry("启动工艺编辑器")
    public static final String CUSTOM_STARTPE_0 = "custom.startPE.title";
    @RBEntry("启动工艺编辑器")
    public static final String CUSTOM_STARTPE_1 = "custom.startPE.description";
    @RBEntry("启动工艺编辑器")
    public static final String CUSTOM_STARTPE_2 = "custom.startPE.tooltip";

    @RBEntry("添加附件")
    public static final String CUSTOM_ADDPBOWORKFLOWATTACH_0 = "custom.addPboWorkFlowAttach.title";
    @RBEntry("添加附件")
    public static final String CUSTOM_ADDPBOWORKFLOWATTACH_1 = "custom.addPboWorkFlowAttach.description";
    @RBEntry("添加附件")
    public static final String CUSTOM_DADDPBOWORKFLOWATTACH_2 = "custom.addPboWorkFlowAttach.tooltip";
    @RBEntry("add16x16.gif")
    public static final String CUSTOM_ADDPBOWORKFLOWATTACH_3 = "custom.addPboWorkFlowAttach.icon";

    @RBEntry("删除附件")
    public static final String CUSTOM_DELETEPBOWORKFLOWATTACH_0 = "custom.deletePboWorkFlowAttach.title";
    @RBEntry("删除附件")
    public static final String CUSTOM_DELETEPBOWORKFLOWATTACH_1 = "custom.deletePboWorkFlowAttach.description";
    @RBEntry("删除附件")
    public static final String CUSTOM_DELETEPBOWORKFLOWATTACH_2 = "custom.deletePboWorkFlowAttach.tooltip";
    @RBEntry("delete.gif")
    public static final String CUSTOM_DELETEPBOWORKFLOWATTACH_3 = "custom.deletePboWorkFlowAttach.icon";

    @RBEntry("下载附件")
    public static final String CUSTOM_DOWNLOADPBOWORKFLOWATTACH_0 = "custom.downloadPboWorkFlowAttach.title";
    @RBEntry("下载附件")
    public static final String CUSTOM_DOWNLOADPBOWORKFLOWATTACH_1 = "custom.downloadPboWorkFlowAttach.description";
    @RBEntry("下载附件")
    public static final String CUSTOM_DOWNLOADPBOWORKFLOWATTACH_2 = "custom.downloadPboWorkFlowAttach.tooltip";
    @RBEntry("netmarkets/images/download.gif")
    public static final String CUSTOM_DOWNLOADPBOWORKFLOWATTACH_3 = "custom.downloadPboWorkFlowAttach.icon";

    @RBEntry("创建工艺更改单")
    public static final String CUSTOM_NEWCHANGENOTICE_0 = "customChangeNotice.createCustomChangeNotice.title";
    @RBEntry("创建工艺更改单")
    public static final String CUSTOM_NEWCHANGENOTICE_1 = "customChangeNotice.createCustomChangeNotice.description";
    @RBEntry("创建工艺更改单")
    public static final String CUSTOM_NEWCHANGENOTICE_2 = "customChangeNotice.createCustomChangeNotice.tooltip";
    @RBEntry("chgnotice_create.gif")
    public static final String CUSTOM_NEWCHANGENOTICE_3 = "customChangeNotice.createCustomChangeNotice.icon";

    @RBEntry("创建文档更改单")
    public static final String CUSTOM_NEWDOCCHANGENOTICE_0 = "customChangeNotice.createDocCustomChangeNotice.title";
    @RBEntry("创建文档更改单")
    public static final String CUSTOM_NEWDOCCHANGENOTICE_1 = "customChangeNotice.createDocCustomChangeNotice.description";
    @RBEntry("创建文档更改单")
    public static final String CUSTOM_NEWDOCCHANGENOTICE_2 = "customChangeNotice.createDocCustomChangeNotice.tooltip";
    @RBEntry("chgnotice_create.gif")
    public static final String CUSTOM_NEWDOCCHANGENOTICE_3 = "customDocument.createDocCustomChangeNotice.icon";

    @RBEntry("保存工时定额")
    public static final String CUSTOM_saveGongShiDingE_TITLE = "custom.saveGongShiDingE.title";
    @RBEntry("保存工时定额")
    public static final String CUSTOM_saveGongShiDingE_DESCRIPTION = "custom.saveGongShiDingE.description";
    @RBEntry("保存工时定额")
    public static final String CUSTOM_saveGongShiDingE_TOOLTIP = "custom.saveGongShiDingE.tooltip";
    @RBEntry("save.gif")
    public static final String CUSTOM_saveGongShiDingE_ICON = "custom.saveGongShiDingE.icon";

    @RBEntry("创建工艺技术通知单")
    public static final String CUSTOM_createCustomProcessNotice_0 = "customDocument.createCustomProcessNotice.title";
    @RBEntry("创建工艺技术通知单")
    public static final String CUSTOM_createCustomProcessNotice_1 = "customDocument.createCustomProcessNotice.description";
    @RBEntry("创建工艺技术通知单")
    public static final String CUSTOM_createCustomProcessNotice_2 = "customDocument.createCustomProcessNotice.tooltip";
    @RBEntry("newdoc.gif")
    public static final String CUSTOM_createCustomProcessNotice_3 = "customDocument.createCustomProcessNotice.icon";

    @RBEntry("创建技术课题")
    public static final String CUSTOM_createJishuketi_0 = "customDocument.createJishuketi.title";
    @RBEntry("创建技术课题")
    public static final String CUSTOM_createJishuketi_1 = "customDocument.createJishuketi.description";
    @RBEntry("创建技术课题")
    public static final String CUSTOM_createJishuketi_2 = "customDocument.createJishuketi.tooltip";
    @RBEntry("newdoc.gif")
    public static final String CUSTOM_createJishuketi_3 = "customDocument.createJishuketi.icon";

    @RBEntry("回退任务")
    public static final String CUSTOM_recycle_0 = "custom.recycle.title";
    @RBEntry("回退任务")
    public static final String CUSTOM_recycle_1 = "custom.recycle.description";
    @RBEntry("回退任务")
    public static final String CUSTOM_recycle_2 = "custom.recycle.tooltip";
    @RBEntry("reset.gif")
    public static final String CUSTOM_recycle_3 = "custom.recycle.icon";

    @RBEntry("产品工序工时定额汇总")
    public static final String CUSTOMREPORT_GONGXUGONGSHIDINGEHUIZONG_0 = "customReport.gongxugongshidingehuizong.title";
    @RBEntry("产品工序工时定额汇总")
    public static final String CUSTOMREPORT_GONGXUGONGSHIDINGEHUIZONG_1 = "customReport.gongxugongshidingehuizong.description";
    @RBEntry("产品工序工时定额汇总")
    public static final String CUSTOMREPORT_GONGXUGONGSHIDINGEHUIZONG_2 = "customReport.gongxugongshidingehuizong.tooltip";
    @RBEntry("report.gif")
    public static final String CUSTOMREPORT_GONGXUGONGSHIDINGEHUIZONG_3 = "customReport.gongxugongshidingehuizong.icon";

    @RBEntry("产品工艺路线汇总")
    public static final String CUSTOMREPORT_GONGYILUXIANHUIZONG_0 = "customReport.gongyiluxianhuizong.title";
    @RBEntry("产品工艺路线汇总")
    public static final String CUSTOMREPORT_GONGYILUXIANHUIZONG_1 = "customReport.gongyiluxianhuizong.description";
    @RBEntry("产品工艺路线汇总")
    public static final String CUSTOMREPORT_GONGYILUXIANHUIZONG_2 = "customReport.gongyiluxianhuizong.tooltip";
    @RBEntry("report.gif")
    public static final String CUSTOMREPORT_GONGYILUXIANHUIZONG_3 = "customReport.gongyiluxianhuizong.icon";

    @RBEntry("工艺报表")
    public static final String S1 = "object.more_report_actions.description";
    @RBEntry("新建")
    public static final String S2 = "object.more parts toolbar actions new.description";


    @RBEntry("产品材料消耗工艺定额汇总")
    public static final String CUSTOMREPORT_CAILIAOXIAIHAOHUIZONG_0 = "customReport.cailiaoxiaihaohuizong.title";
    @RBEntry("产品材料消耗工艺定额汇总")
    public static final String CUSTOMREPORT_CAILIAOXIAIHAOHUIZONG_1 = "customReport.cailiaoxiaihaohuizong.description";
    @RBEntry("产品材料消耗工艺定额汇总")
    public static final String CUSTOMREPORT_CAILIAOXIAIHAOHUIZONG_2 = "customReport.cailiaoxiaihaohuizong.tooltip";
    @RBEntry("report.gif")
    public static final String CUSTOMREPORT_CAILIAOXIAIHAOHUIZONG_3 = "customReport.cailiaoxiaihaohuizong.icon";

    @RBEntry("产品辅料消耗工艺定额汇总")
    public static final String CUSTOMREPORT_FULIAOXIAOHAOHUIZONG_0 = "customReport.fuliaoxiaohaohuizong.title";
    @RBEntry("产品辅料消耗工艺定额汇总")
    public static final String CUSTOMREPORT_FULIAOXIAOHAOHUIZONG_1 = "customReport.fuliaoxiaohaohuizong.description";
    @RBEntry("产品辅料消耗工艺定额汇总")
    public static final String CUSTOMREPORT_FULIAOXIAOHAOHUIZONG_2 = "customReport.fuliaoxiaohaohuizong.tooltip";
    @RBEntry("report.gif")
    public static final String CUSTOMREPORT_FULIAOXIAOHAOHUIZONG_3 = "customReport.fuliaoxiaohaohuizong.icon";

    @RBEntry("产品元器件消耗工艺定额汇总")
    public static final String CUSTOMREPORT_YUANQIJIANXIAOHAOHUIZONG_0 = "customReport.yuanqijianxiaohaohuizong.title";
    @RBEntry("产品元器件消耗工艺定额汇总")
    public static final String CUSTOMREPORT_YUANQIJIANXIAOHAOHUIZONG_1 = "customReport.yuanqijianxiaohaohuizong.description";
    @RBEntry("产品元器件消耗工艺定额汇总")
    public static final String CUSTOMREPORT_YUANQIJIANXIAOHAOHUIZONG_2 = "customReport.yuanqijianxiaohaohuizong.tooltip";
    @RBEntry("report.gif")
    public static final String CUSTOMREPORT_YUANQIJIANXIAOHAOHUIZONG_3 = "customReport.yuanqijianxiaohaohuizong.icon";

    @RBEntry("产品标准件消耗工艺定额汇总")
    public static final String CUSTOMREPORT_BIAOZHUNJIANXIAOHAOHUIZONG_0 = "customReport.biaozhunjianxiaohaohuizong.title";
    @RBEntry("产品标准件消耗工艺定额汇总")
    public static final String CUSTOMREPORT_BIAOZHUNJIANXIAOHAOHUIZONG_1 = "customReport.biaozhunjianxiaohaohuizong.description";
    @RBEntry("产品标准件消耗工艺定额汇总")
    public static final String CUSTOMREPORT_BIAOZHUNJIANXIAOHAOHUIZONG_2 = "customReport.biaozhunjianxiaohaohuizong.tooltip";
    @RBEntry("report.gif")
    public static final String CUSTOMREPORT_BIAOZHUNJIANXIAOHAOHUIZONG_3 = "customReport.biaozhunjianxiaohaohuizong.icon";

    @RBEntry("产品外购件（含不带料委外加工）汇总")
    public static final String CUSTOMREPORT_WAIGOUJIANHUIZONG_0 = "customReport.waigoujianhuizong.title";
    @RBEntry("产品外购件（含不带料委外加工）汇总")
    public static final String CUSTOMREPORT_WAIGOUJIANHUIZONG_1 = "customReport.waigoujianhuizong.description";
    @RBEntry("产品外购件（含不带料委外加工）汇总")
    public static final String CUSTOMREPORT_WAIGOUJIANHUIZONG_2 = "customReport.waigoujianhuizong.tooltip";
    @RBEntry("report.gif")
    public static final String CUSTOMREPORT_WAIGOUJIANHUIZONG_3 = "customReport.waigoujianhuizong.icon";

    @RBEntry("产品外协件（含带料委外加工）汇总")
    public static final String CUSTOMREPORT_WAIXIEJIANHUIZONG_0 = "customReport.waixiejianhuizong.title";
    @RBEntry("产品外协件（含带料委外加工）汇总")
    public static final String CUSTOMREPORT_WAIXIEJIANHUIZONG_1 = "customReport.waixiejianhuizong.description";
    @RBEntry("产品外协件（含带料委外加工）汇总")
    public static final String CUSTOMREPORT_WAIXIEJIANHUIZONG_2 = "customReport.waixiejianhuizong.tooltip";
    @RBEntry("report.gif")
    public static final String CUSTOMREPORT_WAIXIEJIANHUIZONG_3 = "customReport.waixiejianhuizong.icon";

    @RBEntry("产品所用工艺装备汇总")
    public static final String CUSTOMREPORT_GONGYIZHUANGBEIHUIZONG_0 = "customReport.gongyizhuangbeihuizong.title";
    @RBEntry("产品所用工艺装备汇总")
    public static final String CUSTOMREPORT_GONGYIZHUANGBEIHUIZONG_1 = "customReport.gongyizhuangbeihuizong.description";
    @RBEntry("产品所用工艺装备汇总")
    public static final String CUSTOMREPORT_GONGYIZHUANGBEIHUIZONG_2 = "customReport.gongyizhuangbeihuizong.tooltip";
    @RBEntry("report.gif")
    public static final String CUSTOMREPORT_GONGYIZHUANGBEIHUIZONG_3 = "customReport.gongyizhuangbeihuizong.icon";

    @RBEntry("产品所用刀量具汇总")
    public static final String CUSTOMREPORT_DAOLIANGJUHUIZONG_0 = "customReport.daoliangjuhuizong.title";
    @RBEntry("产品所用刀量具汇总")
    public static final String CUSTOMREPORT_DAOLIANGJUHUIZONG_1 = "customReport.daoliangjuhuizong.description";
    @RBEntry("产品所用刀量具汇总")
    public static final String CUSTOMREPORT_DAOLIANGJUHUIZONG_2 = "customReport.daoliangjuhuizong.tooltip";
    @RBEntry("report.gif")
    public static final String CUSTOMREPORT_DAOLIANGJUHUIZONG_3 = "customReport.daoliangjuhuizong.icon";

    @RBEntry("产品所用仪器设备汇总")
    public static final String CUSTOMREPORT_YIQIYIBIAOHUIZONG_0 = "customReport.yiqiyibiaohuizong.title";
    @RBEntry("产品所用仪器设备汇总")
    public static final String CUSTOMREPORT_YIQIYIBIAOHUIZONG_1 = "customReport.yiqiyibiaohuizong.description";
    @RBEntry("产品所用仪器设备汇总")
    public static final String CUSTOMREPORT_YIQIYIBIAOHUIZONG_2 = "customReport.yiqiyibiaohuizong.tooltip";
    @RBEntry("report.gif")
    public static final String CUSTOMREPORT_YIQIYIBIAOHUIZONG_3 = "customReport.yiqiyibiaohuizong.icon";

    @RBEntry("产品部套定额汇总")
    public static final String CUSTOMREPORT_CHANPINBUTAODINGEHUIZONG_0 = "customReport.chanpinbutaodingehuizong.title";
    @RBEntry("产品部套定额汇总")
    public static final String CUSTOMREPORT_CHANPINBUTAODINGEHUIZONG_1 = "customReport.chanpinbutaodingehuizong.description";
    @RBEntry("产品部套定额汇总")
    public static final String CUSTOMREPORT_CHANPINBUTAODINGEHUIZONG_2 = "customReport.chanpinbutaodingehuizong.tooltip";
    @RBEntry("report.gif")
    public static final String CUSTOMREPORT_CHANPINBUTAODINGEHUIZONG_3 = "customReport.chanpinbutaodingehuizong.icon";

    @RBEntry("材料定额完成状态汇总")
    public static final String CUSTOMREPORT_CLDEWCZTHZ_0 = "customReport.cldewczthz.title";
    @RBEntry("材料定额完成状态汇总")
    public static final String CUSTOMREPORT_CLDEWCZTHZ_1 = "customReport.cldewczthz.description";
    @RBEntry("材料定额完成状态汇总")
    public static final String CUSTOMREPORT_CLDEWCZTHZ_2 = "customReport.cldewczthz.tooltip";
    @RBEntry("report.gif")
    public static final String CUSTOMREPORT_CLDEWCZTHZ_3 = "customReport.cldewczthz.icon";

    @RBEntry("签审包")
    public static final String RELATEDPACKAGEDATA = "custom.relatedPackageObjects.description";

    @RBEntry("add16x16.gif")
    public static final String CUSTOM_RELATED_ADD_DESCRIBED = "part.custom_related_add_described.icon";
    @RBEntry("添加关联文档")
    public static final String CUSTOM_RELATED_ADD_DESCRIBED_1 = "part.custom_related_add_described.tooltip";
    @RBEntry("remove16x16.gif")
    public static final String CUSTOM_RELATED_DELETE_DESCRIBED = "part.custom_related_delete_described.icon";
    @RBEntry("删除关联文档")
    public static final String CUSTOM_RELATED_DELETE_DESCRIBED_1 = "part.custom_related_delete_described.tooltip";

    @RBEntry("add16x16.gif")
    public static final String SETJOINER = "projmgmt.setJoiner.icon";
    @RBEntry("批量设置流程参与者")
    public static final String SETJOINER_1 = "projmgmt.setJoiner.tooltip";
    @RBEntry("批量设置流程参与者")
    public static final String SETJOINER_2 = "projmgmt.setJoiner.description";
    @RBEntry("新建工艺分方案")
    public static final String GONGYIFENFANGAN = "gongyiFenfangan.create.description";
    @RBEntry("新建工艺分析策划总结")
    public static final String GONGYIFENXICEHUAZONGJIE = "gongyiFenxicehuazongjie.create.description";
    @RBEntry("新建工艺定型")
    public static final String GONGYIDINGXING = "gongyiDingxing.create.description";
    @RBEntry("新建工艺鉴定")
    public static final String GONGYIJIANDING = "gongyiJianding.create.description";
    @RBEntry("新建总体测发报告")
    public static final String COMPREHENSIVETESTREPORT = "comprehensiveTestReport.create.description";

    @RBEntry("工艺总方案")
    public static final String GONGYIZONGFANGAN = "product.gongyizongfangan.description";
	@RBEntry("新建其他类文档")
    public static final String QITALEIWENDANG = "qitaleiwendang.create.description";

	@RBEntry("定额信息")
    public static final String CUSTOM_DINGEINFORMATION_TITLE = "custom.dingeInformation.title";
    @RBEntry("定额信息")
    public static final String CUSTOM_DINGEINFORMATION_DESCRIPTION = "custom.dingeInformation.description";
    @RBEntry("定额信息")
    public static final String CUSTOM_DINGEINFORMATION_TOOLTIP = "custom.dingeInformation.tooltip";

    @RBEntry("导出配套明细表")
    public static final String CUSTOM_EXPORTPEITAO_TITLE = "custom.exportPeiTaoMingXi.title";
    @RBEntry("导出配套明细表")
    public static final String CUSTOM_EXPORTPEITAO_DESCRIPTION = "custom.exportPeiTaoMingXi.description";
    @RBEntry("导出配套明细表")
    public static final String CUSTOM_EXPORTPEITAO_TOOLTIP = "custom.exportPeiTaoMingXi.tooltip";

    @RBEntry("导出工序配套表")
    public static final String CUSTOM_EXPORTGONGXUPEITAO_TITLE = "custom.exportGongXuPeiTao.title";
    @RBEntry("导出工序配套表")
    public static final String CUSTOM_EXPORTGONGXUPEITAO_DESCRIPTION = "custom.exportGongXuPeiTao.description";
    @RBEntry("导出工序配套表")
    public static final String CUSTOM_EXPORTGONGXUPEITAO_TOOLTIP = "custom.exportGongXuPeiTao.tooltip";

    @RBEntry("检验记录及照片样张查看")
    public static final String CUSTOM_RECORDCOLLECTTABLE_TITLE = "custom.recordCollectTable.title";
    @RBEntry("检验记录及照片样张查看")
    public static final String CUSTOM_RECORDCOLLECTTABLE_DESCRIPTION = "custom.recordCollectTable.description";
    @RBEntry("检验记录及照片样张查看")
    public static final String CUSTOM_RECORDCOLLECTTABLE_TOOLTIP = "custom.recordCollectTable.tooltip";

    @RBEntry("一键受控")
    public static final String CUSTOM_quickApproved_TITLE = "custom.quickApproved.title";
    @RBEntry("一键受控")
    public static final String CUSTOM_quickApproved_DESCRIPTION = "custom.quickApproved.description";
    @RBEntry("一键受控")
    public static final String CUSTOM_quickApproved_TOOLTIP = "custom.quickApproved.tooltip";

    @RBEntry("同步到现行库")
    public static final String CUSTOM_synchDangan_TITLE = "custom.synchDangan.title";
    @RBEntry("同步到现行库")
    public static final String CUSTOM_synchDangan_DESCRIPTION = "custom.synchDangan.description";
    @RBEntry("同步到现行库")
    public static final String CUSTOM_synchDangan_TOOLTIP = "custom.synchDangan.tooltip";

    @RBEntry("同步到预立卷")
    public static final String CUSTOM_synchDangan2_TITLE = "custom.synchDangan2.title";
    @RBEntry("同步到预立卷")
    public static final String CUSTOM_synchDangan2_DESCRIPTION = "custom.synchDangan2.description";
    @RBEntry("同步到预立卷")
    public static final String CUSTOM_synchDangan2_TOOLTIP = "custom.synchDangan2.tooltip";

    @RBEntry("导出到DNC")
    public static final String CUSTOM_exportDNC_TITLE = "custom.exportDNC.title";
    @RBEntry("导出到DNC")
    public static final String CUSTOM_exportDNC_DESCRIPTION = "custom.exportDNC.description";
    @RBEntry("导出到DNC")
    public static final String CUSTOM_exportDNC_TOOLTIP = "custom.exportDNC.tooltip";

    @RBEntry("上传数控程序")
    public static final String CUSTOM_uploadAttachment4SK_TITLE = "custom.uploadAttachment4SK.title";
    @RBEntry("上传数控程序")
    public static final String CUSTOM_uploadAttachment4SK_DESCRIPTION = "custom.uploadAttachment4SK.description";
    @RBEntry("上传数控程序")
    public static final String CUSTOM_uploadAttachment4SK_TOOLTIP = "custom.uploadAttachment4SK.tooltip";

    @RBEntry("添加数控程序")
    public static final String CUSTOM_ADDATTACHMENT4SK_TITLE = "custom.addAttachment4SK.title";
    @RBEntry("添加数控程序")
    public static final String CUSTOM_ADDATTACHMENT4SK_DESCRIPTION = "custom.addAttachment4SK.description";
    @RBEntry("添加数控程序")
    public static final String CUSTOM_ADDATTACHMENT4SK_TOOLTIP = "custom.addAttachment4SK.tooltip";
    @RBEntry("add16x16.gif")
    public static final String CUSTOM_ADDATTACHMENT4SK_IMG = "custom.addAttachment4SK.icon";

    @RBEntry("删除数控程序")
    public static final String CUSTOM_DELETEATTACHMENT4SK_TITLE = "custom.deleteAttachment4SK.title";
    @RBEntry("删除数控程序")
    public static final String CUSTOM_DELETEATTACHMENT4SK_DESCRIPTION = "custom.deleteAttachment4SK.description";
    @RBEntry("删除数控程序")
    public static final String CUSTOM_DELETEATTACHMENT4SK_TOOLTIP = "custom.deleteAttachment4SK.tooltip";
    @RBEntry("remove16x16.gif")
    public static final String CUSTOM_DELETEATTACHMENT4SK_IMG = "custom.deleteAttachment4SK.icon";

    @RBEntry("工时定额")
    public static final String CUSTOM_workTimeDE_TITLE = "custom.workTimeDE.title";
    @RBEntry("工时定额")
    public static final String CUSTOM_workTimeDE_DESCRIPTION = "custom.workTimeDE.description";
    @RBEntry("工时定额")
    public static final String CUSTOM_workTimeDE_TOOLTIP = "custom.workTimeDE.tooltip";

    @RBEntry("清空历史版本")
    public static final String CUSTOM_deleteIterations_TITLE = "custom.deleteIterations.title";
    @RBEntry("清空历史版本")
    public static final String CUSTOM_deleteIterations_DESCRIPTION = "custom.deleteIterations.description";
    @RBEntry("清空历史版本")
    public static final String CUSTOM_deleteIterations_TOOLTIP = "custom.deleteIterations.tooltip";

    @RBEntry("一键重启流转")
    public static final String CUSTOM_quickReCycle_TITLE = "custom.quickReCycle.title";
    @RBEntry("一键重启流转")
    public static final String CUSTOM_quickReCycle_DESCRIPTION = "custom.quickReCycle.description";
    @RBEntry("一键重启流转")
    public static final String CUSTOM_quickReCycle_TOOLTIP = "custom.quickReCycle.tooltip";

    @RBEntry("编辑")
    public static final String CUSTOM_customEdit_TITLE = "customChangeNotice.customEdit.title";
    @RBEntry("编辑")
    public static final String CUSTOM_customEdit_DESCRIPTION = "customChangeNotice.customEdit.description";
    @RBEntry("编辑")
    public static final String CUSTOM_customEdit_TOOLTIP = "customChangeNotice.customEdit.tooltip";

    @RBEntry("编辑更改信息")
    public static final String CUSTOM_customEditChangeinfo_TITLE = "customChangeNotice.customEditChangeinfo.title";
    @RBEntry("编辑更改信息")
    public static final String CUSTOM_customEditChangeinfo_DESCRIPTION = "customChangeNotice.customEditChangeinfo.description";
    @RBEntry("编辑更改信息")
    public static final String CUSTOM_customEditChangeinfo_TOOLTIP = "customChangeNotice.customEditChangeinfo.tooltip";

    @RBEntry("重新生成PDF")
    public static final String CUSTOM_customReCreatePDF_TITLE = "customChangeNotice.customReCreatePDF.title";
    @RBEntry("重新生成PDF")
    public static final String CUSTOM_customReCreatePDF_DESCRIPTION = "customChangeNotice.customReCreatePDF.description";
    @RBEntry("重新生成PDF")
    public static final String CUSTOM_customReCreatePDF_TOOLTIP = "customChangeNotice.customReCreatePDF.tooltip";

    //add by chum 2017.02.06 begin
    @RBEntry("BOM比较")
    public static final String S3 = "object.bomcompare.description";
    @RBEntry("EBOM和PBOM比较")
    public static final String CUSTOMREPORT_EBOMCompareToPBOM_0 = "customReport.EBOMCompareToPBOM.title";
    @RBEntry("EBOM和PBOM比较")
    public static final String CUSTOMREPORT_EBOMCompareToPBOM_1 = "customReport.EBOMCompareToPBOM.description";
    @RBEntry("EBOM和PBOM比较")
    public static final String CUSTOMREPORT_EBOMCompareToPBOM_2 = "customReport.EBOMCompareToPBOM.tooltip";
    @RBEntry("report.gif")
    public static final String CUSTOMREPORT_EBOMCompareToPBOM_3 = "customReport.EBOMCompareToPBOM.icon";
   //add by chum 2017.02.06 begin

    //add by zhudong 2017.02.06 begin
    @RBEntry("离线数据打包下载")
	public static final String CUSTOM_OFF_LINEDATAPACKDOWNLOAD_0 = "custom.off_lineDataPackDownload.title";
	@RBEntry("离线数据打包下载")
	public static final String CUSTOM_OFF_LINEDATAPACKDOWNLOAD_1 = "custom.off_lineDataPackDownload.description";
	@RBEntry("离线数据打包下载")
	public static final String CUSTOM_OFF_LINEDATAPACKDOWNLOAD_2 = "custom.off_lineDataPackDownload.tooltip";

	//add by likaicheng 2018.3.12
	 @RBEntry("add16x16.gif")
	 public static final String CUSTOM_addUserInfo_img = "custom.addUserInfo.icon";
	 @RBEntry("添加行")
	 public static final String CUSTOM_addUserInfo_des = "custom.addUserInfo.tooltip";
	 @RBEntry("remove16x16.gif")
	 public static final String CUSTOM_deleteUserInfo_img = "custom.deleteUserInfo.icon";
	 @RBEntry("删除行")
	 public static final String CUSTOM_deleteUserInfo_des = "custom.deleteUserInfo.tooltip";
	 @RBEntry("save.gif")
	 public static final String CUSTOM_saveUserInfo_img = "custom.saveUserInfo.icon";
	 @RBEntry("保存")
	 public static final String CUSTOM_saveUserInfo_des = "custom.saveUserInfo.tooltip";

	//add by zhudong 2017.02.06 end

	 //add by zhuhao 2018.07.09 start
	 @RBEntry("completedWork.gif")
	 public static final String SETFINISH = "projmgmt.setFinish.icon";
	 @RBEntry("批量设置完成任务")
	 public static final String SETFINISH_1 = "projmgmt.setFinish.tooltip";
	 @RBEntry("批量设置完成任务")
	 public static final String SETFINISH_2 = "projmgmt.setFinish.description";
	 //add by zhuhao 2018.07.09 end

	 @RBEntry("清空历史版本")
	 public static final String CUSTOM_list_rollup_TITLE = "custom.list_rollup.title";
	 @RBEntry("清空历史版本")
	 public static final String CUSTOM_list_rollup_DESCRIPTION = "custom.list_rollup.description";
	 @RBEntry("清空历史版本")
	 public static final String CUSTOM_list_rollup_TOOLTIP = "custom.list_rollup.tooltip";

	 @RBEntry("add16x16.gif")
	 public static final String CUSTOM_addRow_img = "custom.addRow.icon";
	 @RBEntry("添加关联文档")
	 public static final String CUSTOM_addRow_des = "custom.addRow.tooltip";
	 @RBEntry("remove16x16.gif")
	 public static final String CUSTOM_deleteRow_img = "custom.deleteRow.icon";
	 @RBEntry("删除关联文档")
	 public static final String CUSTOM_deleteRow_des = "custom.deleteRow.tooltip";
	 @RBEntry("save.gif")
	 public static final String CUSTOM_saveItem_img = "custom.saveItem.icon";
	 @RBEntry("保存")
	 public static final String CUSTOM_saveItem_des = "custom.saveItem.tooltip";

	 //结构检验记录/项目配置操作功能-add by hz 171018
	 @RBEntry("add16x16.gif")
	 public static final String CCR_addProConfig_img = "ccr.addProConfig.icon";
	 @RBEntry("添加项目配置")
	 public static final String CCR_addProConfig_des = "ccr.addProConfig.tooltip";
	 @RBEntry("remove16x16.gif")
	 public static final String CCR_deleteProConfig_img = "ccr.deleteProConfig.icon";
	 @RBEntry("删除项目配置")
	 public static final String CCR_deleteProConfig_des = "ccr.deleteProConfig.tooltip";
	 @RBEntry("save.gif")
	 public static final String CCR_saveProConfig_img = "ccr.saveProConfig.icon";
	 @RBEntry("保存")
	 public static final String CCR_saveProConfig_des = "ccr.saveProConfig.tooltip";
	 //结构检验记录/套表配置操作功能-add by hz 171018
	 @RBEntry("add16x16.gif")
	 public static final String CCR_addTableConfig_img = "ccr.addTableConfig.icon";
	 @RBEntry("添加套表配置")
	 public static final String CCR_addTableConfig_des = "ccr.addTableConfig.tooltip";
	 @RBEntry("remove16x16.gif")
	 public static final String CCR_deleteTableConfig_img = "ccr.deleteTableConfig.icon";
	 @RBEntry("删除套表配置")
	 public static final String CCR_deleteTableConfig_des = "ccr.deleteTableConfig.tooltip";
	 @RBEntry("save.gif")
	 public static final String CCR_saveTableConfig_img = "ccr.saveTableConfig.icon";
	 @RBEntry("保存")
	 public static final String CCR_saveTableConfig_des = "ccr.saveTableConfig.tooltip";

	 @RBEntry("查询")
	 public static final String CUSTOM_technologySearch_0 = "custom.technologySearch.title";
	 @RBEntry("查询")
	 public static final String CUSTOM_technologySearch_1 = "custom.technologySearch.description";
	 @RBEntry("查询")
	 public static final String CUSTOM_technologySearch_2 = "custom.technologySearch.tooltip";

	 @RBEntry("下载受控签名文件")
	 public static final String CUSTOM_downloadshouKongDocPrintFile_TITLE = "custom.downloadshouKongDocPrintFile.title";
	 @RBEntry("下载受控签名文件")
	 public static final String CUSTOM_downloadshouKongDocPrintFile_DESCRIPTION = "custom.downloadshouKongDocPrintFile.description";
	 @RBEntry("下载受控签名文件")
	 public static final String CUSTOM_downloadshouKongDocPrintFile_TOOLTIP = "custom.downloadshouKongDocPrintFile.tooltip";

	 @RBEntry("重新加载权限")
	 public static final String CUSTOM_reloadPower_TITLE = "custom.reloadPower.title";
	 @RBEntry("重新加载权限")
	 public static final String CUSTOM_reloadPower_DESCRIPTION = "custom.reloadPower.description";
	 @RBEntry("重新加载权限")
	 public static final String CUSTOM_reloadPower_TOOLTIP = "custom.reloadPower.tooltip";

    @RBEntry("PDM常见问题解答")
    public static final String CUSTOM_FAQDownload_TITLE = "custom.FAQDownload.title";
    @RBEntry("PDM常见问题解答")
    public static final String CUSTOM_FAQDownload_DESCRIPTION = "custom.FAQDownload.description";
    @RBEntry("PDM常见问题解答")
    public static final String CUSTOM_FAQDownload_TOOLTIP = "custom.FAQDownload.tooltip";

    @RBEntry("PDM系统更新记录")
    public static final String CUSTOM_SYSUPDATERECORD_TITLE = "custom.sysUpdateRecord.title";
    @RBEntry("PDM系统更新记录")
    public static final String CUSTOM_SYSUPDATERECORD_DESCRIPTION = "custom.sysUpdateRecord.description";
    @RBEntry("PDM系统更新记录")
    public static final String CUSTOM_SYSUPDATERECORD_TOOLTIP = "custom.sysUpdateRecord.tooltip";

    @RBEntry("用户手册")
    public static final String CUSTOM_USERMANUAL_TITLE = "custom.userManual.title";
    @RBEntry("用户手册")
    public static final String CUSTOM_USERMANUAL_DESCRIPTION = "custom.userManual.description";
    @RBEntry("用户手册")
    public static final String CUSTOM_USERMANUAL_TOOLTIP = "custom.userManual.tooltip";

	 @RBEntry("快速动态授权")
    public static final String CUSTOM_QUICKDYNAMICAUTHORIZATION_TITLE = "custom.quickdynamicAuthorization.title";
    @RBEntry("快速动态授权")
    public static final String CUSTOM_QUICKDYNAMICAUTHORIZATION_DESCRIPTION = "custom.quickdynamicAuthorization.description";
    @RBEntry("快速动态授权")
    public static final String CUSTOM_QUICKDYNAMICAUTHORIZATION_TOOLTIP = "custom.quickdynamicAuthorization.tooltip";
    @RBEntry("accsstl.gif")
    public static final String CUSTOM_QUICKDYNAMICAUTHORIZATION_ICON = "custom.quickdynamicAuthorization.icon";
    @RBEntry("查看回收信息")
	public static final String query_recover_info1 = "object.startRecoverFirstPage.description";
	@RBEntry("查看回收信息")
	public static final String query_recover_info2 = "object.startRecoverSecondPage.description";
	@RBEntry("查看回收信息")
	public static final String query_recover_info3 = "object.startRecoverThirdPage.description";
	@RBEntry("查看延迟信息")
	public static final String query_delay_info1 = "object.startDelayFirstPage.description";
	@RBEntry("查看延迟信息")
	public static final String query_delay_info2 = "object.startDelaySecondPage.description";
	@RBEntry("重新关联典型工艺")
	public static final String CUSTOM_RELATETYPECIALPROCESS = "custom.relateTypecialProcess.description";
	@RBEntry("EBOM导出")
	public static final String CUSTOM_EBOMEXPORT = "customReport.ebomExport.description";
    @RBEntry("excel_export.gif")
    public static final String CUSTOM_EBOMEXPORT_ICON = "customReport.ebomExport.icon";

    @RBEntry("发次BOM导出")
    public static final String CUSTOM_FACIEXPORT = "customReport.faciBomExport.description";
    @RBEntry("excel_export.gif")
    public static final String CUSTOM_FACIEXPORT_ICON = "customReport.faciBomExport.icon";
	@RBEntry("人员工号维护")
	public static final String EMPLOYEENOMANAGER = "custom.employeeNoManager.description";
    @RBEntry("save.gif")
    public static final String CUSTOM_SAVEEMPLOYEENO_IMG = "custom.saveEmployeeNo.icon";
    @RBEntry("保存")
    public static final String CUSTOM_SAVEEMPLOYEENO_DES = "custom.saveEmployeeNo.tooltip";
    @RBEntry("导出会签意见")
    public static final String CUSTOM_EXPORTSIGNATUREADVISE_DES = "custom.exportSignatureAdvise.description";
    @RBEntry("新建")
    public static final String custom_new = "object.docs row actions new.description";

    @RBEntry("新增")
    public static final String PRIVATE_CONSTANT_17 = "custom.addRowTec.description";
    @RBEntry("add16x16.gif")
    public static final String PRIVATE_CONSTANT_18 = "custom.addRowTec.icon";
    @RBEntry("新增")
    public static final String PRIVATE_CONSTANT_19 = "custom.addRowTec.tooltip";
    @RBEntry("启用/禁用")
    public static final String PRIVATE_CONSTANT_20 = "custom.deleteRowTec.description";
    @RBEntry("default_value.gif")
    public static final String PRIVATE_CONSTANT_21 = "custom.deleteRowTec.icon";
    @RBEntry("启用/禁用")
    public static final String PRIVATE_CONSTANT_22 = "custom.deleteRowTec.tooltip";

    @RBEntry("attribute_edit.gif")
    public static final String REFRESHBATCHES = "custom.refreshBatches.icon";
    @RBEntry("批量刷新批次号")
    public static final String REFRESHBATCHES_1 = "custom.refreshBatches.tooltip";
    @RBEntry("批量刷新批次号")
    public static final String REFRESHBATCHES_2 = "custom.refreshBatches.description";

    @RBEntry("attribute_edit.gif")
    public static final String REFRESHBATCHES_STEP = "custom.refreshBatches_step.icon";
    @RBEntry("设置多批次")
    public static final String REFRESHBATCHES_STEP_1 = "custom.refreshBatches_step.tooltip";
    @RBEntry("设置多批次")
    public static final String REFRESHBATCHES_STEP_2 = "custom.refreshBatches_step.description";

    @RBEntry("attribute_edit.gif")
    public static final String REFRESHBATCHES_STEP2 = "custom.refreshBatches_step2.icon";
    @RBEntry("选择刷新的工艺")
    public static final String REFRESHBATCHES_STEP2_1 = "custom.refreshBatches_step2.tooltip";
    @RBEntry("选择刷新的工艺")
    public static final String REFRESHBATCHES_STEP2_2 = "custom.refreshBatches_step2.description";

    @RBEntry("newdoc.gif")
    public static final String CREATEPHOTOTEMPLATE = "custom.createPhotoTemplate.icon";
    @RBEntry("创建照片样张")
    public static final String CREATEPHOTOTEMPLATE_1 = "custom.createPhotoTemplate.tooltip";
    @RBEntry("创建照片样张")
    public static final String CREATEPHOTOTEMPLATE_2 = "custom.createPhotoTemplate.description";

    @RBEntry("add16x16.gif")
    public static final String IMPORTPHOTOTEMPLATE_0 = "custom.importPhotoTemplate.icon";
    @RBEntry("照片样张导入")
    public static final String IMPORTPHOTOTEMPLATE_1 = "custom.importPhotoTemplate.tooltip";
    @RBEntry("照片样张导入")
    public static final String IMPORTPHOTOTEMPLATE_2 = "custom.importPhotoTemplate.description";

    @RBEntry("*本机Excel文件路径:")
    public static final String FILE_IMPORT_EXCEL_STEP = "custom.importPhotoTemplate_step.description";
    @RBEntry("*本机图片zip包路径:")
    public static final String FILE_IMPORT_PHOTO_STEP = "custom.importPhotoTemplate_step2.description";

    @RBEntry("迁移工艺至测试系统")
    public static final String EXCHANGE_PROCESS_0 = "custom.exchangeProcessDoc.title";
    @RBEntry("迁移工艺至测试系统")
    public static final String EXCHANGE_PROCESS_1 = "custom.exchangeProcessDoc.description";
    @RBEntry("迁移工艺至测试系统")
    public static final String EXCHANGE_PROCESS_2 = "custom.exchangeProcessDoc.tooltip";

    @RBEntry("迁移工艺至正式系统")
    public static final String EXCHANGE_PROCESSCS_0 = "custom.exchangeProcessDocCS.title";
    @RBEntry("迁移工艺至正式系统")
    public static final String EXCHANGE_PROCESSCS_1 = "custom.exchangeProcessDocCS.description";
    @RBEntry("迁移工艺至正式统")
    public static final String EXCHANGE_PROCESSCS_2 = "custom.exchangeProcessDocCS.tooltip";

    @RBEntry("提交三级工艺签审")
    public static final String SUBMIT_SIGN3_01 = "custom.submitSign3.title";
    @RBEntry("提交三级工艺签审")
    public static final String SUBMIT_SIGN3_02 = "custom.submitSign3.description";

    @RBEntry("提交五级工艺签审")
    public static final String SUBMIT_SIGN5_01 = "custom.submitSign5.title";
    @RBEntry("提交五级工艺签审")
    public static final String SUBMIT_SIGN5_02 = "custom.submitSign5.description";


    @RBEntry("批量下载")
    public static final String CUSTOM_downloadDocumentsToCompressedFile_TITLE = "custom.downloadToCompressedFile.title";
    @RBEntry("批量下载")
    public static final String CUSTOM_downloadDocumentsToCompressedFile_DESCRIPTION = "custom.downloadToCompressedFile.description";
    @RBEntry("批量下载")
    public static final String CUSTOM_downloadDocumentsToCompressedFile_TOOLTIP = "custom.downloadToCompressedFile.tooltip";
    @RBEntry("download_cmd.gif")
    public static final String CUSTOM_downloadDocumentsToCompressedFile_ICON = "custom.downloadToCompressedFile.icon";


    @RBEntry("completedWork.gif")
    public static final String batchFinish_1 = "custom.batchFinish.icon";
    @RBEntry("批量完成通知")
    public static final String batchFinish_2 = "custom.batchFinish.tooltip";
    @RBEntry("批量完成通知")
    public static final String batchFinish_3 = "custom.batchFinish.description";

    @RBEntry("NC物资映射导入")
    public static final String CUSTOM_importNCMap_TITLE = "custom.importNCMap.title";
    @RBEntry("NC物资映射导入")
    public static final String CUSTOM_importNCMap_DESCRIPTION = "custom.importNCMap.description";
    @RBEntry("NC物资映射导入")
    public static final String CUSTOM_importNCMap_TOOLTIP = "custom.importNCMap.tooltip";

    @RBEntry("part_allocation.gif")
    public static final String CUSTOM_matchHistoryQuota_ICON = "custom.matchHistoryQuota.icon";
    @RBEntry("历史物资匹配")
    public static final String CUSTOM_matchHistoryQuota_TITLE = "custom.matchHistoryQuota.title";
    @RBEntry("历史物资匹配")
    public static final String CUSTOM_matchHistoryQuota_DESCRIPTION = "custom.matchHistoryQuota.description";
    @RBEntry("历史物资匹配")
    public static final String CUSTOM_matchHistoryQuota_TOOLTIP = "custom.matchHistoryQuota.tooltip";

    @RBEntry("hide.gif")
    public static final String SETHIDEPROCESS = "projmgmt.setHideProcess.icon";
    @RBEntry("隐藏流程任务")
    public static final String SETHIDEPROCESS_1 = "projmgmt.setHideProcess.tooltip";
    @RBEntry("隐藏流程任务")
    public static final String SETHIDEPROCESS_2 = "projmgmt.setHideProcess.description";

    @RBEntry("show.gif")
    public static final String SETSHOWPROCESS = "projmgmt.setShowProcess.icon";
    @RBEntry("展示流程任务")
    public static final String SETSHOWPROCESS_1 = "projmgmt.setShowProcess.tooltip";
    @RBEntry("展示流程任务")
    public static final String SETSHOWPROCESS_2 = "projmgmt.setShowProcess.description";

    @RBEntry("主数据同步映射管理")
    public static final String MANAGESYNCDATA = "object.manageSyncData.description";

    @RBEntry("link_object.gif")
    public static final String CUSTOM_IMPORTSYNCDATAMAP_ICON = "custom.importSyncDataMap.icon";
    @RBEntry("主数据同步映射导入")
    public static final String CUSTOM_IMPORTSYNCDATAMAP_TOOLTIP = "custom.importSyncDataMap.tooltip";
    @RBEntry("主数据同步映射导入")
    public static final String CUSTOM_IMPORTSYNCDATAMAP_DESCRIPTION = "custom.importSyncDataMap.description";

    @RBEntry("link_object.gif")
    public static final String CUSTOM_EXPORTSYNCDATAMAP_ICON = "custom.exportSyncDataMap.icon";
    @RBEntry("主数据同步映射导出")
    public static final String CUSTOM_EXPORTSYNCDATAMAP_TOOLTIP = "custom.exportSyncDataMap.tooltip";
    @RBEntry("主数据同步映射导出")
    public static final String CUSTOM_EXPORTSYNCDATAMAP_DESCRIPTION = "custom.exportSyncDataMap.description";

    @RBEntry("收集工艺文件")
    public static final String CUSTOM_COLLECTPROCESSDOCS_0 = "customChangeNotice.collectProcessDocStep.title";
    @RBEntry("收集工艺文件")
    public static final String CUSTOM_COLLECTPROCESSDOCS_1 = "customChangeNotice.collectProcessDocStep.description";
    @RBEntry("收集工艺文件")
    public static final String CUSTOM_COLLECTPROCESSDOCS_2 = "customChangeNotice.collectProcessDocStep.tooltip";

    @RBEntry("批量创建工艺更改单")
    public static final String CUSTOM_CHANGENOTICEBATCHCREATE_0 = "changeNotice.batchCreate.title";

    @RBEntry("phantompart.gif")
    public static final String CUSTOM_processParamsManage_ICON = "custom.processParamsManage.icon";
    @RBEntry("工艺参数管理")
    public static final String CUSTOM_processParamsManage_TITLE = "custom.processParamsManage.title";
    @RBEntry("工艺参数管理")
    public static final String CUSTOM_processParamsManage_DESCRIPTION = "custom.processParamsManage.description";
    @RBEntry("工艺参数管理")
    public static final String CUSTOM_processParamsManage_TOOLTIP = "custom.processParamsManage.tooltip";

    @RBEntry("add16x16.gif")
    public static final String CUSTOM_addProcessParam_ICON = "custom.addProcessParam.icon";
    @RBEntry("新增工艺参数")
    public static final String CUSTOM_addProcessParam_TITLE = "custom.addProcessParam.title";
    @RBEntry("新增工艺参数")
    public static final String CUSTOM_addProcessParam_DESCRIPTION = "custom.addProcessParam.description";
    @RBEntry("新增工艺参数")
    public static final String CUSTOM_addProcessParam_TOOLTIP = "custom.addProcessParam.tooltip";

    @RBEntry("remove16x16.gif")
    public static final String CUSTOM_deleteProcessParam_ICON = "custom.deleteProcessParam.icon";
    @RBEntry("删除工艺参数")
    public static final String CUSTOM_deleteProcessParam_TITLE = "custom.deleteProcessParam.title";
    @RBEntry("删除工艺参数")
    public static final String CUSTOM_deleteProcessParam_DESCRIPTION = "custom.deleteProcessParam.description";
    @RBEntry("删除工艺参数")
    public static final String CUSTOM_deleteProcessParam_TOOLTIP = "custom.deleteProcessParam.tooltip";

    @RBEntry("template.png")
    public static final String CUSTOM_setTemplateEnum_ICON = "custom.setTemplateEnum.icon";
    @RBEntry("设置工艺参数合法值")
    public static final String CUSTOM_setTemplateEnum_TITLE = "custom.setTemplateEnum.title";
    @RBEntry("设置工艺参数合法值")
    public static final String CUSTOM_setTemplateEnum_DESCRIPTION = "custom.setTemplateEnum.description";
    @RBEntry("设置工艺参数合法值")
    public static final String CUSTOM_setTemplateEnum_TOOLTIP = "custom.setTemplateEnum.tooltip";

    @RBEntry("favicon.png")
    public static final String CUSTOM_OPENBAIYUTOOL_ICON = "custom.openBaiYuTool.icon";
    @RBEntry("打开白羽工具")
    public static final String CUSTOM_OPENBAIYUTOOL_TITLE = "custom.openBaiYuTool.title";
    @RBEntry("打开白羽工具")
    public static final String CUSTOM_OPENBAIYUTOOL_DESCRIPTION = "custom.openBaiYuTool.description";
    @RBEntry("打开白羽工具")
    public static final String CUSTOM_OPENBAIYUTOOL_TOOLTIP = "custom.openBaiYuTool.tooltip";

    @RBEntry("edit.gif")
    public static final String CUSTOM_editProcessParam_ICON = "custom.editProcessParam.icon";
    @RBEntry("编辑工艺参数")
    public static final String CUSTOM_editProcessParam_TITLE = "custom.editProcessParam.title";
    @RBEntry("编辑工艺参数")
    public static final String CUSTOM_editProcessParamDESCRIPTION = "custom.editProcessParam.description";
    @RBEntry("编辑工艺参数")
    public static final String CUSTOM_editProcessParam_TOOLTIP = "custom.editProcessParam.tooltip";

    @RBEntry("managed_collection_add.gif")
    public static final String CUSTOM_processKnowledgeManage_ICON = "custom.processKnowledgeManage.icon";
    @RBEntry("工艺知识管理")
    public static final String CUSTOM_processKnowledgeManage_TITLE = "custom.processKnowledgeManage.title";
    @RBEntry("工艺知识管理")
    public static final String CUSTOM_processKnowledgeManage_DESCRIPTION = "custom.processKnowledgeManage.description";
    @RBEntry("工艺知识管理")
    public static final String CUSTOM_processKnowledgeManage_TOOLTIP = "custom.processKnowledgeManage.tooltip";


    @RBEntry("add16x16.gif")
    public static final String CUSTOM_ADDPROCESSKNOWLEDGE_ICON = "custom.addProcessKnowledge.icon";
    @RBEntry("新增工艺知识")
    public static final String CUSTOM_ADDPROCESSKNOWLEDGE_TITLE = "custom.addProcessKnowledge.title";
    @RBEntry("新增工艺知识")
    public static final String CUSTOM_ADDPROCESSKNOWLEDGE_DESCRIPTION = "custom.addProcessKnowledge.description";
    @RBEntry("新增工艺知识")
    public static final String CUSTOM_ADDPROCESSKNOWLEDGE_TOOLTIP = "custom.addProcessKnowledge.tooltip";

    @RBEntry("remove16x16.gif")
    public static final String CUSTOM_DELETEPROCESSKNOWLEDGE_ICON = "custom.deleteProcessKnowledge.icon";
    @RBEntry("删除工艺知识")
    public static final String CUSTOM_DELETEPROCESSKNOWLEDGE_TITLE = "custom.deleteProcessKnowledge.title";
    @RBEntry("删除工艺知识")
    public static final String CUSTOM_DELETEPROCESSKNOWLEDGE_DESCRIPTION = "custom.deleteProcessKnowledge.description";
    @RBEntry("删除工艺知识")
    public static final String CUSTOM_DELETEPROCESSKNOWLEDGE_TOOLTIP = "custom.deleteProcessKnowledge.tooltip";

    @RBEntry("edit.gif")
    public static final String CUSTOM_EDITPROCESSKNOWLEDGE_ICON = "custom.editProcessKnowledge.icon";
    @RBEntry("编辑工艺知识")
    public static final String CUSTOM_EDITPROCESSKNOWLEDGE_TITLE = "custom.editProcessKnowledge.title";
    @RBEntry("编辑工艺知识")
    public static final String CUSTOM_EDITPROCESSKNOWLEDGE_DESCRIPTION = "custom.editProcessKnowledge.description";
    @RBEntry("编辑工艺知识")
    public static final String CUSTOM_EDITPROCESSKNOWLEDGE_TOOLTIP = "custom.editProcessKnowledge.tooltip";

    @RBEntry("import_from_excel.png")
    public static final String CUSTOM_IMPORTPROCESSKNOWLEDGEDATA_ICON = "custom.importProcessKnowledgeData.icon";
    @RBEntry("导入工艺知识")
    public static final String CUSTOM_IMPORTPROCESSKNOWLEDGEDATA_TITLE = "custom.importProcessKnowledgeData.title";
    @RBEntry("导入工艺知识")
    public static final String CUSTOM_IMPORTPROCESSKNOWLEDGEDATA_DESCRIPTION = "custom.importProcessKnowledgeData.description";
    @RBEntry("导入工艺知识")
    public static final String CUSTOM_IMPORTPROCESSKNOWLEDGEDATA_TOOLTIP = "custom.importProcessKnowledgeData.tooltip";

    @RBEntry("系统导入管理")
    public static final String MANAGEIMPORTDATA = "object.manageImportData.description";

    @RBEntry("link_object.gif")
    public static final String CUSTOM_IMPORTGONGSHI_ICON = "custom.importGongshi.icon";
    @RBEntry("工时定额导入")
    public static final String CUSTOM_IMPORTGONGSHI_TOOLTIP = "custom.importGongshi.tooltip";
    @RBEntry("工时定额导入")
    public static final String CUSTOM_IMPORTGONGSHI_DESCRIPTION = "custom.importGongshi.description";

    @RBEntry("link_object.gif")
    public static final String CUSTOM_EXPORTGONGSHI_ICON = "custom.exportGongshi.icon";
    @RBEntry("工时定额差异导出")
    public static final String CUSTOM_EXPORTGONGSHI_TOOLTIP = "custom.exportGongshi.tooltip";
    @RBEntry("工时定额差异导出")
    public static final String CUSTOM_EXPORTGONGSHI_DESCRIPTION = "custom.exportGongshi.description";

    @RBEntry("link_object.gif")
    public static final String CUSTOM_GENERATEPROCESSPLAN_ICON = "custom.generateProcessPlan.icon";
    @RBEntry("手动生成工艺实例化")
    public static final String CUSTOM_GENERATEPROCESSPLAN_TOOLTIP = "custom.generateProcessPlan.tooltip";
    @RBEntry("手动生成工艺实例化")
    public static final String CUSTOM_GENERATEPROCESSPLAN_DESCRIPTION = "custom.generateProcessPlan.description";

    @RBEntry("link_object.gif")
    public static final String CUSTOM_IMPORTGONGSHISIGN_ICON = "custom.importGongshiSign.icon";
    @RBEntry("手动导入工时标识")
    public static final String CUSTOM_IMPORTGONGSHISIGN_TOOLTIP = "custom.importGongshiSign.tooltip";
    @RBEntry("手动导入工时标识")
    public static final String CUSTOM_IMPORTGONGSHISIGN_DESCRIPTION = "custom.importGongshiSign.description";

    @RBEntry("link_object.gif")
    public static final String CUSTOM_IMPORTGONGSHIHISTORY_ICON = "custom.importGongshiHistory.icon";
    @RBEntry("工时定额导入历史版本")
    public static final String CUSTOM_IMPORTGONGSHIHISTORY_TOOLTIP = "custom.importGongshiHistory.tooltip";
    @RBEntry("工时定额导入历史版本")
    public static final String CUSTOM_IMPORTGONGSHIHISTORY_DESCRIPTION = "custom.importGongshiHistory.description";

    @RBEntry("favicon.png")
    public static final String CUSTOM_GONGSHIDINGEMANAGE_ICON = "custom.gongshiDingeManage.icon";
    @RBEntry("工时定额管理")
    public static final String CUSTOM_GONGSHIDINGEMANAGE_TITLE = "custom.gongshiDingeManage.title";
    @RBEntry("工时定额管理")
    public static final String CUSTOM_GONGSHIDINGEMANAGE_TOOLTIP = "custom.gongshiDingeManage.tooltip";
    @RBEntry("工时定额管理")
    public static final String CUSTOM_GONGSHIDINGEMANAGE_DESCRIPTION = "custom.gongshiDingeManage.description";

    @RBEntry("link_object.gif")
    public static final String CUSTOM_SOPDATAMANAGE_ICON = "custom.sopDataManage.icon";
    @RBEntry("SOP数据治理导入")
    public static final String CUSTOM_SOPDATAMANAGE_TOOLTIP = "custom.sopDataManage.tooltip";
    @RBEntry("SOP数据治理导入")
    public static final String CUSTOM_SOPDATAMANAGE_DESCRIPTION = "custom.sopDataManage.description";

    @RBEntry("成熟度历史记录")
    public static final String MATURITYHISTORY = "custom.maturityHistory.description";

    @RBEntry("chgreqst.gif")
    public static final String CHANGEREQUEST_CREATEECR_ICON = "changeRequest.createECR.icon";
    @RBEntry("新建工艺更改申请")
    public static final String CHANGEREQUEST_CREATEECR_TITLE = "changeRequest.createECR.title";
    @RBEntry("新建工艺更改申请")
    public static final String CHANGEREQUEST_CREATEECR_DESCRIPTION = "changeRequest.createECR.description";
    @RBEntry("新建工艺更改申请")
    public static final String CHANGEREQUEST_CREATEECR_TOOLTIP = "changeRequest.createECR.tooltip";

//    @RBEntry("chgreqst.gif")
//    public static final String CHANGEREQUEST_EDIT_ICON = "changeRequest.edit.icon";
//    @RBEntry("修改工艺更改申请")
//    public static final String CHANGEREQUEST_EDIT_TITLE = "changeRequest.edit.title";
//    @RBEntry("修改工艺更改申请")
//    public static final String CHANGEREQUEST_EDIT_DESCRIPTION = "changeRequest.edit.description";
//    @RBEntry("修改工艺更改申请")
//    public static final String CHANGEREQUEST_EDIT_TOOLTIP = "changeRequest.edit.tooltip";

    @RBEntry("anlyactvy.gif")
    public static final String CHANGEREQUEST_CREATEANALYSIS_ICON = "analysisActivity.customCreateAnalysis.icon";
    @RBEntry("新建更改影响分析")
    public static final String CHANGEREQUEST_CREATEANALYSIS_TITLE = "analysisActivity.customCreateAnalysis.title";
    @RBEntry("新建更改影响分析")
    public static final String CHANGEREQUEST_CREATEANALYSIS_DESCRIPTION = "analysisActivity.customCreateAnalysis.description";
    @RBEntry("新建更改影响分析")
    public static final String CHANGEREQUEST_CREATEANALYSIS_TOOLTIP = "analysisActivity.customCreateAnalysis.tooltip";

    @RBEntry("anlyactvy.gif")
    public static final String ANALYSISACTIVITY_CREATE_ICON = "analysisActivity.createAnalysisActivity.icon";
    @RBEntry("新建更改影响分析")
    public static final String ANALYSISACTIVITY_CREATE_TITLE = "analysisActivity.createAnalysisActivity.title";
    @RBEntry("新建更改影响分析")
    public static final String ANALYSISACTIVITY_CREATE_DESCRIPTION = "analysisActivity.createAnalysisActivity.description";
    @RBEntry("新建更改影响分析")
    public static final String ANALYSISACTIVITY_CREATE_TOOLTIP = "analysisActivity.createAnalysisActivity.tooltip";

    @RBEntry("anlyactvy.gif")
    public static final String ANALYSISACTIVITY_RELATEDOBJECTS_ICON = "analysisActivity.relatedObjects.icon";
    @RBEntry("关联变更")
    public static final String ANALYSISACTIVITY_RELATEDOBJECTS_TITLE = "analysisActivity.relatedObjects.title";
    @RBEntry("关联变更")
    public static final String ANALYSISACTIVITY_RELATEDOBJECTS_DESCRIPTION = "analysisActivity.relatedObjects.description";
    @RBEntry("关联变更")
    public static final String ANALYSISACTIVITY_RELATEDOBJECTS_TOOLTIP = "analysisActivity.relatedObjects.tooltip";

    @RBEntry("anlyactvy.gif")
    public static final String ANALYSISACTIVITY_ANALYSISWORKFLOWTAB_ICON = "analysisActivity.analysisWorkflowTab.icon";
    @RBEntry("流程图")
    public static final String ANALYSISACTIVITY_ANALYSISWORKFLOWTAB_TITLE = "analysisActivity.analysisWorkflowTab.title";
    @RBEntry("流程图")
    public static final String ANALYSISACTIVITY_ANALYSISWORKFLOWTAB_DESCRIPTION = "analysisActivity.analysisWorkflowTab.description";
    @RBEntry("流程图")
    public static final String ANALYSISACTIVITY_ANALYSISWORKFLOWTAB_TOOLTIP = "analysisActivity.analysisWorkflowTab.tooltip";

    @RBEntry("add16x16.gif")
    public static final String ANALYSISACTIVITY_ADDRELATEDPBOM_ICON = "analysisActivity.addRelatedPbom.icon";
    @RBEntry("添加受影响PBOM")
    public static final String ANALYSISACTIVITY_ADDRELATEDPBOM_TITLE = "analysisActivity.addRelatedPbom.title";
    @RBEntry("添加受影响PBOM")
    public static final String ANALYSISACTIVITY_ADDRELATEDPBOM_DESCRIPTION = "analysisActivity.addRelatedPbom.description";
    @RBEntry("添加受影响PBOM")
    public static final String ANALYSISACTIVITY_ADDRELATEDPBOM_TOOLTIP = "analysisActivity.addRelatedPbom.tooltip";

    @RBEntry("remove16x16.gif")
    public static final String ANALYSISACTIVITY_DELRELATEDPBOM_ICON = "analysisActivity.delRelatedPbom.icon";
    @RBEntry("移除受影响PBOM")
    public static final String ANALYSISACTIVITY_DELRELATEDPBOM_TITLE = "analysisActivity.delRelatedPbom.title";
    @RBEntry("移除受影响PBOM")
    public static final String ANALYSISACTIVITY_DELRELATEDPBOM_DESCRIPTION = "analysisActivity.delRelatedPbom.description";
    @RBEntry("移除受影响PBOM")
    public static final String ANALYSISACTIVITY_DELRELATEDPBOM_TOOLTIP = "analysisActivity.delRelatedPbom.tooltip";

    @RBEntry("添加受影响工艺")
    public static final String ANALYSISACTIVITY_ADDRELATEDTECHNICS_TITLE = "analysisActivity.addRelatedTechnics.title";

    @RBEntry("添加受影响制品")
    public static final String ANALYSISACTIVITY_ADDRELATEDPRODUCT_TITLE = "analysisActivity.addRelatedProduct.title";

    @RBEntry("关联更改单")
    public static final String ANALYSISACTIVITY_ADDRELATEDCHANGEORDER_TITLE = "analysisActivity.addRelatedChangeOrder.title";

    @RBEntry("关联工艺文件")
    public static final String ANALYSISACTIVITY_RELATEDTECHNICS_TITLE = "analysisActivity.relatedTechnics.title";

    @RBEntry("object_replace.gif")
    public static final String ANALYSISACTIVITY_REPLACEANALYSIS_ICON = "analysisActivity.replaceAnalysis.icon";
    @RBEntry("替换更改影响分析单")
    public static final String ANALYSISACTIVITY_REPLACEANALYSIS_TITLE = "analysisActivity.replaceAnalysis.title";
    @RBEntry("替换更改影响分析单")
    public static final String ANALYSISACTIVITY_REPLACEANALYSIS_DESCRIPTION = "analysisActivity.replaceAnalysis.description";
    @RBEntry("替换更改影响分析单")
    public static final String ANALYSISACTIVITY_REPLACEANALYSIS_TOOLTIP = "analysisActivity.replaceAnalysis.tooltip";

    @RBEntry("关联工艺更改申请单")
    public static final String ANALYSISACTIVITY_ADDRELATEDCHANGEREQUEST_TITLE = "analysisActivity.addRelatedChangeRequest.title";

    @RBEntry("remove16x16.gif")
    public static final String ANALYSISACTIVITY_DELETEANALYSIS_ICON = "analysisActivity.deleteAnalysis.icon";
    @RBEntry("删除")
    public static final String ANALYSISACTIVITY_DELETEANALYSIS_TITLE = "analysisActivity.deleteAnalysis.title";
    @RBEntry("删除")
    public static final String ANALYSISACTIVITY_DELETEANALYSIS_DESCRIPTION = "analysisActivity.deleteAnalysis.description";
    @RBEntry("删除")
    public static final String ANALYSISACTIVITY_DELETEANALYSIS_TOOLTIP = "analysisActivity.deleteAnalysis.tooltip";

    @RBEntry("structure_reorder.gif")
    public static final String CUSTOM_IMPORTBOMSTRUCTURE_ICON = "custom.importBomStructure.icon";
    @RBEntry("导入NC系统")
    public static final String CUSTOM_IMPORTBOMSTRUCTURE_TITLE = "custom.importBomStructure.title";
    @RBEntry("导入NC系统")
    public static final String CUSTOM_IMPORTBOMSTRUCTURE_DESCRIPTION = "custom.importBomStructure.description";
    @RBEntry("导入NC系统")
    public static final String CUSTOM_IMPORTBOMSTRUCTURE_TOOLTIP = "custom.importBomStructure.tooltip";

    @RBEntry("NC导入记录")
    public static final String NC_IMPORT_RECORD = "custom.ncImportRecord.description";

    @RBEntry("编码市场参考价")
    public static final String NC_SJZYKPARTREFERENCEPRICE_DESC = "custom.sjzykPartReferencePrice.description";

    @RBEntry("anlyactvy.gif")
    public static final String CHANGEREQUEST_ADDPRODUCT_ICON = "analysisActivity.customAddProduct.icon";
    @RBEntry("添加受影响制品")
    public static final String CHANGEREQUEST_ADDPRODUCT_TITLE = "analysisActivity.customAddProduct.title";
    @RBEntry("添加受影响制品")
    public static final String CHANGEREQUEST_ADDPRODUCT_DESCRIPTION = "analysisActivity.customAddProduct.description";
    @RBEntry("添加受影响制品")
    public static final String CHANGEREQUEST_ADDPRODUCT_TOOLTIP = "analysisActivity.customAddProduct.tooltip";

    @RBEntry("系统配置管理")
    public static final String CUSTOM_SYSTEMCONFIGURATION_0 = "custom.systemConfiguration.title";
    @RBEntry("系统配置管理")
    public static final String CUSTOM_SYSTEMCONFIGURATION_1 = "custom.systemConfiguration.description";
    @RBEntry("add16x16.gif")
    public static final String CUSTOM_ADDSYSTEMCONFIGURATION_0 = "custom.addSystemConfiguration.icon";
    @RBEntry("新建系统变量")
    public static final String CUSTOM_ADDSYSTEMCONFIGURATION_1 = "custom.addSystemConfiguration.title";
    @RBEntry("新建系统变量")
    public static final String CUSTOM_ADDSYSTEMCONFIGURATION_2 = "custom.addSystemConfiguration.description";
    @RBEntry("新建系统变量")
    public static final String CUSTOM_ADDSYSTEMCONFIGURATION_3 = "custom.addSystemConfiguration.tooltip";
    @RBEntry("edit.gif")
    public static final String CUSTOM_EDITSYSTEMCONFIGURATION_0 = "custom.editSystemConfiguration.icon";
    @RBEntry("修改系统变量")
    public static final String CUSTOM_EDITSYSTEMCONFIGURATION_1 = "custom.editSystemConfiguration.title";
    @RBEntry("修改系统变量")
    public static final String CUSTOM_EDITSYSTEMCONFIGURATION_2 = "custom.editSystemConfiguration.description";
    @RBEntry("修改系统变量")
    public static final String CUSTOM_EDITSYSTEMCONFIGURATION_3 = "custom.editSystemConfiguration.tooltip";
    @RBEntry("remove16x16.gif")
    public static final String CUSTOM_DELETESYSTEMCONFIGURATION_0 = "custom.deleteSystemConfiguration.icon";
    @RBEntry("删除系统变量")
    public static final String CUSTOM_DELETESYSTEMCONFIGURATION_1 = "custom.deleteSystemConfiguration.title";
    @RBEntry("删除系统变量")
    public static final String CUSTOM_DELETESYSTEMCONFIGURATION_2 = "custom.deleteSystemConfiguration.description";
    @RBEntry("删除系统变量")
    public static final String CUSTOM_DELETESYSTEMCONFIGURATION_3 = "custom.deleteSystemConfiguration.tooltip";

    @RBEntry("发起授权申请")
    public static final String CUSTOM_REQUESTAUTHORIZATION_TITLE = "custom.requestAuthorization.title";
    @RBEntry("发起授权申请")
    public static final String CUSTOM_REQUESTAUTHORIZATION_DESCRIPTION = "custom.requestAuthorization.description";
    @RBEntry("发起授权申请")
    public static final String CUSTOM_REQUESTAUTHORIZATION_TOOLTIP = "custom.requestAuthorization.tooltip";
    @RBEntry("accsstl.gif")
    public static final String CUSTOM_REQUESTAUTHORIZATION_ICON = "custom.requestAuthorization.icon";
    @RBEntry("确定发起授权申请？")
    public static final String ISREQUESTAUTHORIZATION = "ISREQUESTAUTHORIZATION";

    @RBEntry("发起工时定额签审流程")
    public static final String CUSTOM_STARTGONGSHISIGN_TITLE = "custom.startGongShiSign.title";
    @RBEntry("发起工时定额签审流程")
    public static final String CUSTOM_STARTGONGSHISIGN_DESCRIPTION = "custom.startGongShiSign.description";
    @RBEntry("发起工时定额签审流程")
    public static final String CUSTOM_STARTGONGSHISIGN_TOOLTIP = "custom.startGongShiSign.tooltip";
    @RBEntry("favicon.png")
    public static final String CUSTOM_STARTGONGSHISIGN_ICON = "custom.startGongShiSign.icon";
    @RBEntry("确定发起工时定额签审流程？")
    public static final String ISSTARTGONGSHISIGN = "ISSTARTGONGSHISIGN";
}
