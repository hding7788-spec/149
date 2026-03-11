<%@page import="wt.util.WTException"%>
<%@page import="java.util.Enumeration"%>
<%@page import="wt.workflow.engine.WfActivity"%>
<%@page import="ext.casc.constants.Constants"%>
<%@page import="wt.workflow.work.WfAssignedActivity"%>
<%@page import="ext.sast.center.synch.MQDataFeedBackHelper"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="com.glaway.mpm.util.ReferenceFactory"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="ext.ases.part.ASESHuiqianSignature"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="ext.ases.part.SignLink"%>
<%@page import="wt.inf.container.WTContainerReferenceSearch"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="ext.ases.envelope.ProcessEnvelopeUtil"%>
<%@page import="ext.ases.envelope.ProcessEnvelope"%>
<%@page import="wt.workflow.definer.WfDefinerHelper"%>
<%@page import="wt.workflow.engine.ProcessData"%>
<%@page import="wt.workflow.engine.WfEngineHelper"%>
<%@page import="wt.workflow.engine.WfProcess"%>
<%@page import="wt.workflow.definer.WfProcessDefinition"%>
<%@page import="java.util.Vector"%>
<%@page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<%

WTObject wto = (WTObject)ReferenceFactory.getObjectbyOid("VR:wt.epm.EPMDocument:4138853");
WfProcess process = (WfProcess)ReferenceFactory.getObjectbyOid("OR:wt.workflow.engine.WfProcess:4238790");
WfAssignedActivity zhipaiZuhang = null;
Enumeration enumeration;
try {
	enumeration = WfEngineHelper.service.getProcessSteps(process, null);
	 while (enumeration.hasMoreElements()) {
            WfActivity wfactivity = (WfActivity) enumeration.nextElement();
            if (wfactivity instanceof WfAssignedActivity) {
                WfAssignedActivity wfassignedactivity = (WfAssignedActivity) wfactivity;
                if(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG.equals(wfassignedactivity.getName())){
                	zhipaiZuhang = wfassignedactivity;

                }
            }
        }
} catch (WTException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}

QueryResult qr2 = PersistenceHelper.manager.navigate(wto,
        SignLink.ROLE_BOBJECT_ROLE, SignLink.class, true);// 取得对象所有审签信息
List<ASESHuiqianSignature> list = new ArrayList<ASESHuiqianSignature>();
while (qr2.hasMoreElements()) {// 遍历审签信息
    ASESHuiqianSignature tempSign = (ASESHuiqianSignature) qr2
            .nextElement();
    System.out.println(zhipaiZuhang.getModifyTimestamp().getTime() +"   "+tempSign.getModifyTimestamp().getTime());

    if(zhipaiZuhang!=null&&zhipaiZuhang.getModifyTimestamp().getTime()>(tempSign.getModifyTimestamp().getTime()+30000)){//先修改意见，提前30秒把刚完成的指派组长意见放入有效意见
        System.out.println(tempSign.getConclusion()+"  del  "+tempSign.getPersistInfo().getObjectIdentifier().getId());

    	continue;
	}
    System.out.println(tempSign.getConclusion()+"  "+tempSign.getPersistInfo().getObjectIdentifier().getId());

}
%>