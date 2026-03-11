package ext.casc.analysisActivity.process;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.changeRequest.Change2WorkflowHelper;
import wt.fc.Persistable;
import wt.fc.WTObject;
import wt.session.SessionServerHelper;

import java.io.*;

public class ExtCreateAnalysisFromPboProcessor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 方法功能:签审包操作按钮 创建工艺更改申请，申请批准后再自动创建更改影响分析
     *
     * @author cjh
     * @date 2024/8/29
     */
    public static FormResult create(NmCommandBean commandBean) {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        FormResult formResult = new FormResult();
        try {
            Persistable persistable = commandBean.getPrimaryOid().getWtRef().getObject();
            Change2WorkflowHelper.createWTChangeRequest2Auto((WTObject) persistable, null);
            formResult.setStatus(FormProcessingStatus.SUCCESS);
            formResult.setNextAction(FormResultAction.NONE);
            FeedbackMessage message = new FeedbackMessage();
            message.addMessage("启动更改影响分析成功");
            formResult.addFeedbackMessage(message);
        } catch(Exception e) {
            e.printStackTrace();
            formResult.setStatus(FormProcessingStatus.FAILURE);
            FeedbackMessage message = new FeedbackMessage();
            message.addMessage("启动更改影响分析失败");
            formResult.addFeedbackMessage(message);
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formResult;
    }

}