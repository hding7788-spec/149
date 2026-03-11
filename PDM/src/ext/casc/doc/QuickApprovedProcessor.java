package ext.casc.doc;

import java.util.List;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.session.SessionServerHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;

import com.glaway.mpm.change.qchange.helper.ChangeProcessPlanStructure;
import com.glaway.mpm.processplan.ProcessPlanStructure;
import com.glaway.mpm.processplan.helper.ZhuFuLinkUtil;
import com.glaway.mpm.util.MPMProcessPlanUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import ext.casc.workflow.WorkflowHelper;

/**
 * @describe used to submit approval workFlow process
 * @author Long,XiuChuan
 * @since 2012/7/23
 *
 */
public class QuickApprovedProcessor extends DefaultObjectFormProcessor {
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
        FormResult formresult = super.doOperation(commandBean, objectBeans);
        Object actionObj = commandBean.getActionOid().getRefObject();
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        System.out.println("actionObj" + actionObj);
        String msg ="操作成功";
        try {
            if (actionObj instanceof WTDocument) {//文档提交签审
                WTDocument doc = (WTDocument) actionObj;
                WorkflowHelper.setObjectLifeCycle(doc, "APPROVED");

                List<MPMProcessPlan> pplans = MPMProcessPlanUtil.getAllProcessPlanByWTDocument(doc);
        		for(MPMProcessPlan pplan:pplans){
        			if(!pplan.getNumber().equals(doc.getNumber())){
        				ext.casc.purge.PurgeDataProcessor.removeFromChange(pplan);
            			PersistenceHelper.manager.delete(pplan);
        			}
        		}

        		quickApproved(doc);

            }
        } catch (Exception e) {
        	msg = e.getMessage();
            e.printStackTrace();
        } finally {
            FeedbackMessage message = new FeedbackMessage();
            message.addMessage(msg);
            formresult.addFeedbackMessage(message);
            formresult.setNextAction(FormResultAction.NONE);
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formresult;
    }
    public void quickApproved(WTDocument doc) throws Exception{
    	QueryResult qr = PartDocServiceCommand.getAssociatedDescParts(doc);
		if(qr.hasMoreElements()) {
			WTPart part = (WTPart)qr.nextElement();
			long oid = part.getPersistInfo().getObjectIdentifier().getId();
			ProcessPlanStructure pps = new ProcessPlanStructure(doc,""+oid,"normal");
			if (TypedUtilityServiceHelper.service.getTypeIdentifier(doc).toString().contains("reportTechnics")) {
				String docVersion = doc.getVersionIdentifier().getValue();
				if(!docVersion.startsWith("space")){

					 new ChangeProcessPlanStructure(doc).structureProcessPlan();

				}else{
					pps.structureReportTechnicsProcessPlan();

				}
			}else{
				 pps.structureProcessPlan2();
				 ZhuFuLinkUtil.syncZFLink(doc);
			}

		}
    }
}
