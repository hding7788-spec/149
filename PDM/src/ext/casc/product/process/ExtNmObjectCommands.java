package ext.casc.product.process;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.model.NmException;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.object.objectResource;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import ext.casc.product.ExtNmObjectHelper;
import wt.ixb.handlers.netmarkets.JSPFeedback;
import wt.ixb.handlers.netmarkets.NmFeedbackSpec;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfEventHelper;
import wt.workflow.work.WorkItem;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class ExtNmObjectCommands {
    private static final String OBJECT_RESOURCE = "com.ptc.netmarkets.object.objectResource";

    public static void downloadFolderContentFiles(NmCommandBean cb) throws WTException {
        JSPFeedback jfb = null;
        NmFeedbackSpec nfbs = null;
        URL downloadUrl = null;

        HashMap<Object, Object> map = cb.getMap();
        map.put("jfb", jfb);
        map.put("primary", cb.getTextParameter("primary"));
        map.put("signfile", cb.getTextParameter("signfile"));

        try {
            HashMap<Object, Object> params = new HashMap<Object, Object>();
            params.put("refresh", "true");
            jfb = cb.initializeFeedback("export1", cb.getElementOid(), false, params);
            nfbs = jfb.getSpec();
            nfbs.setTitle(WTMessage.getLocalizedMessage(OBJECT_RESOURCE, objectResource.DOWNLOAD_ARCHIVE_TITLE, null,
                    SessionHelper.getLocale()));

            downloadUrl = new ExtNmObjectHelper().downloadFolderContentFiles(cb);
            cb.getSessionBean().getStorage().put("urlToDownload", downloadUrl.toExternalForm());
        } catch (NmException nme) {
            if (nfbs != null) {
                nfbs.setException(nme);
            }

            throw nme;
        } finally {
            String returnUrl = null;
            if (downloadUrl != null) {
                returnUrl = downloadUrl.toExternalForm();
            }
            cb.finalizeFeedback(jfb, returnUrl);
        }
    }

    public static FormResult batchFinish(NmCommandBean cb) throws WTException {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);

        List<String> BATCH_FINISH_LABEL = new ArrayList<String>();
        BATCH_FINISH_LABEL.add("工艺编制完成情况");
        BATCH_FINISH_LABEL.add("材料定额完成情况的通知");
        BATCH_FINISH_LABEL.add("通知");
        BATCH_FINISH_LABEL.add("分发审核");
        BATCH_FINISH_LABEL.add("设计数据正式接收通知");
        BATCH_FINISH_LABEL.add("设计数据正式接收通知（工艺员）");
        FormResult formresult = new FormResult();

        formresult.setNextAction(FormResultAction.NONE);
        ArrayList oids = cb.getSelected();
        //System.out.println("CB->oids.size(): NmContext [" + oids.size()+"]");
        int len = oids.size();
        int num = 0;
        for(int count = 0; count < len; count++) {
            NmContext currentContext = (NmContext) oids.get(count);
            NmOid currentOid = currentContext.getTargetOid();
            if(currentOid != null) {
                Object object = currentOid.getRefObject();
                if(object instanceof WorkItem) {
                    WorkItem workItem = (WorkItem) object;
                    if(workItem.isComplete()) {
                        continue;
                    }
                    WfActivity activity = (WfActivity) workItem.getSource().getObject();
                    String name = activity.getName();
                    if(BATCH_FINISH_LABEL.contains(name)) {
                        num++;
                        Vector vector = new Vector();
                        vector.addElement("通过");
                        wt.workflow.work.WorkflowHelper.service.workComplete(workItem, workItem
                                .getOwnership().getOwner(), vector);
                        WfEventHelper.createVotingEvent(null, activity, workItem,
                                workItem.getOwnership().getOwner(), "批量完成", vector,
                                false, workItem.isRequired());
                    }
                }
            }
        }
        FeedbackMessage message = new FeedbackMessage();
        if(num != 0) {
            formresult.setStatus(FormProcessingStatus.SUCCESS);
            message.addMessage("批量完成" + num + "个符合条件任务！");
        } else {
            formresult.setStatus(FormProcessingStatus.SUCCESS);
            message.addMessage("没有选中符合条件任务！");
        }
        formresult.addFeedbackMessage(message);

        SessionServerHelper.manager.setAccessEnforced(accessFlag);

        return formresult;
    }

}
