package ext.sast.center.processor;

import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.sast.center.synch.MQConstants;
import ext.sast.center.synch.MQKRSynchHelper;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class SendToKRProcessor {
    public static FormResult sendToKR(NmCommandBean commandBean) throws WTException {
        FormResult formResult = new FormResult();
        FeedbackMessage feedBackMsg = null;
        String message="发送成功";
        Object actionObj = commandBean.getActionOid().getRefObject();
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            HttpServletRequest request = commandBean.getRequest();
            Map map = request.getParameterMap();
            String[] pflag = (String[]) map.get(MQConstants.FAWANGDANWEI);
            String fawangdanwei=pflag[0];
            Map<String,String>  params = new HashMap<String, String>();
            params.put(MQConstants.FAWANGDANWEI,fawangdanwei);
            if (actionObj instanceof ProcessEnvelope) {
                MQKRSynchHelper.sendToKr((ProcessEnvelope)actionObj,params);
            }else if (actionObj instanceof ChangePackaged) {
                MQKRSynchHelper.sendToKr((ChangePackaged)actionObj,params);
            }
            feedBackMsg = new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), null, null, new String[]{message});
        }catch (Exception e){
            message = "发送失败："+e.getLocalizedMessage();
            feedBackMsg = new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), null, null, new String[]{message});
            e.printStackTrace();
        }finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }

        formResult.addFeedbackMessage(feedBackMsg);
        formResult.setNextAction(FormResultAction.NONE);
        return formResult;
    }
}
