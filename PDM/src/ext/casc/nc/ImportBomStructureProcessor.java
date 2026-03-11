package ext.casc.nc;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.integrate.nc.ErpSynchHelper;

import wt.part.WTPart;
import wt.session.SessionServerHelper;

import wt.util.WTException;

import java.util.List;


public class ImportBomStructureProcessor extends DefaultObjectFormProcessor {

    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
        FormResult formresult = super.doOperation(commandBean, objectBeans);
        Object actionObj = commandBean.getActionOid().getRefObject();
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        String msg = "";
        try {
            if (actionObj instanceof WTPart) {
                WTPart part = (WTPart) actionObj;
                msg = ErpSynchHelper.importBomStructure(part);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FeedbackMessage message = new FeedbackMessage();
            if(!"".equals(msg)){
                message.addMessage("导入存在问题："+msg);
                formresult.addFeedbackMessage(message);
                formresult.setStatus(FormProcessingStatus.FAILURE);
            }else{
            	formresult.setStatus(FormProcessingStatus.SUCCESS);
                message.addMessage("导入成功");
            }
            formresult.addFeedbackMessage(message);
            formresult.setNextAction(FormResultAction.NONE);
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formresult;
    }

}
