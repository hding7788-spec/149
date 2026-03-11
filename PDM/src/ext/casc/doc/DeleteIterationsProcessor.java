package ext.casc.doc;

import java.util.List;

import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import com.glaway.mpm.processplan.ProcessPlanStructure;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

import ext.casc.util.DeleteProcessDocUtility;
import ext.casc.workflow.WorkflowHelper;

/**
 * @describe used to submit approval workFlow process
 * @author Long,XiuChuan
 * @since 2012/7/23
 *
 */
public class DeleteIterationsProcessor extends DefaultObjectFormProcessor {
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
        FormResult formresult = super.doOperation(commandBean, objectBeans);
        Object actionObj = commandBean.getActionOid().getRefObject();
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        System.out.println("actionObj" + actionObj);
        try {
            if (actionObj instanceof WTDocument) {//文档提交签审
                WTDocument doc = (WTDocument) actionObj;
                if( doc.isLatestIteration()){
                	DeleteProcessDocUtility.deleteIterations(doc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FeedbackMessage message = new FeedbackMessage();
            message.addMessage("操作成功");
            formresult.addFeedbackMessage(message);
            formresult.setNextAction(FormResultAction.NONE);
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formresult;
    }

}
