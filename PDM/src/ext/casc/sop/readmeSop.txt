

1、DataUtility配置
将以下内容添加至  Windchill/codebase/com/ptc/core/components/components.dataUtilities.properties文件末尾

wt.services/svc/default/com.ptc.core.components.descriptor.DataUtility/SopObjectDataUtility/java.lang.Object/0=ext.casc.sop.datautility.SopObjectDataUtility/singleton
wt.services/svc/default/com.ptc.core.components.descriptor.DataUtility/SopResourceDataUtility/java.lang.Object/0=ext.casc.sop.datautility.SopResourceDataUtility/singleton
将以下内容添加至  Windchill/codebase/service.properties
wt.services/svc/default/com.ptc.core.ui.validation.SimpleValidationFilter/SopObjectValidator/java.lang.Object/0=ext.casc.sop.validator.SopObjectValidator/duplicate

2、类型属性配置


3、策略管理器
配置processtask和processtaskitem
配置sop文档修订权限

4、电子签名
ext/casc/fileprint/signtemplate.properties
ext.casc.fileprint.FilePrintUtil2
ext.casc.workflow.PrintHelper
5、修订对象
com.glaway.mpm.change.qchange.helper.QChangeHelper.improvedProcessZipDocVresion(primaryBusinessObject);
6、创建更改单权限
ext.casc.validator.CreateProcessChangeNoticeValidator
7、工作流调整
工艺更改单签审流程
工艺实例化：
String ecnType = ext.casc.workflow.WorkflowHelper.getEcnType(primaryBusinessObject);
if(!"作废更改".equals(ecnType)){
boolean isSop = ext.casc.sop.util.SopWorkflowUtil.isSop(primaryBusinessObject);
if(isSop){
  ext.casc.sop.util.SopWorkflowUtil.structureSopChangeOrder(topPartOid,technicsOid);
}else{
  com.glaway.mpm.change.qchange.helper.ChangeProcessPlanStructure pps= new com.glaway.mpm.change.qchange.helper.ChangeProcessPlanStructure(topPartOid,technicsOid,processType);
  pps.structureProcessPlan();
}
}

加入条件判断：
boolean isSop = ext.casc.sop.util.SopWorkflowUtil.isSop(primaryBusinessObject);
if(isSop){
  result = "Y";
}else{
  result = "N";
}

9.其他类
com.glaway.mpm.processplan.ProcessPlanActionsValidator
ext.casc.ecn.AddAttriubutesDatautility
ext.casc.process.util.ProcessUtil
ext.casc.workflow.CmWfTaskProcessorCommands


map.put("部门_一车间", "301");
        map.put("部门_二车间", "302");
        map.put("部门_三车间", "303");
        map.put("部门_四车间", "304");
        map.put("部门_五车间", "305");
        map.put("部门_六车间", "306");
        map.put("部门_七车间", "307");
        map.put("部门_八车间", "308");
        map.put("部门_九车间", "309");
        map.put("部门_十车间", "310");


===>>>测试<<<===
实例化测试
pdm.149.sast.casc/Windchill/netmarkets/jsp/ext/structureProcessPlan.jsp

SOP工艺文件签审流程需要在站点下部署

流程添加
三级签审流程
五级签审流程
工艺更改单签审流程
	建立工艺与依据文件关联
ext.casc.sop.util.SopWorkflowUtil.saveDocGJBZLinks(primaryBusinessObject);

sql
alter table GL_DOCPARAMETERSLINK add (TECTYPE varchar2(500) );