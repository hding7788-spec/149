package ext.casc.workflow.processor;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import wt.fc.PersistenceServerHelper;
import wt.fc.WTStringSet;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

import java.io.Serializable;
import java.util.ArrayList;

public class SetHideProcessor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 隐藏流程任务
     *
     * @param cb
     * @return
     * @throws WTException
     */
    public static FormResult setHideProcess(NmCommandBean cb) throws WTException {
        FormResult form = new FormResult();
        try {
            ArrayList<NmOid> list = cb.getNmOidSelected();
            if(list != null && list.size() > 0) {
                for(NmOid nmOid : list) {
                    if(nmOid.getRef() instanceof WorkItem){
                        WorkItem workItem = (WorkItem) nmOid.getRef();
                        workItem.setEventSet(new WTStringSet("已隐藏"));
                        PersistenceServerHelper.manager.update(workItem);
                    }
                }
            }
            form.setStatus(FormProcessingStatus.SUCCESS);
            FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "隐藏流程任务成功");
            form.addFeedbackMessage(message);
            form.setNextAction(FormResultAction.REFRESH_OPENER);
            return form;
        } catch (Exception e) {
            e.printStackTrace();
            form.setStatus(FormProcessingStatus.FAILURE);
            FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "隐藏流程任务失败");
            form.addFeedbackMessage(message);
            form.setNextAction(FormResultAction.REFRESH_OPENER);
            return form;
        }
    }

    /**
     * 展示流程任务
     *
     * @param cb
     * @return
     * @throws WTException
     */
    public static FormResult setShowProcess(NmCommandBean cb) throws WTException {
        FormResult form = new FormResult();
        try {
            ArrayList<NmOid> list = cb.getNmOidSelected();
            if(list != null && list.size() > 0) {
                for(NmOid nmOid : list) {
                    if(nmOid.getRef() instanceof WorkItem){
                        WorkItem workItem = (WorkItem) nmOid.getRef();
                        workItem.setEventSet(null);
                        PersistenceServerHelper.manager.update(workItem);
                    }
                }
            }
            form.setStatus(FormProcessingStatus.SUCCESS);
            FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "展示流程任务成功");
            form.addFeedbackMessage(message);
            form.setNextAction(FormResultAction.REFRESH_OPENER);
            return form;
        } catch (Exception e) {
            e.printStackTrace();
            form.setStatus(FormProcessingStatus.FAILURE);
            FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "展示流程任务失败");
            form.addFeedbackMessage(message);
            form.setNextAction(FormResultAction.REFRESH_OPENER);
            return form;
        }
    }

}