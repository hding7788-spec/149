package ext.casc.part;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import wt.part.WTPart;
import wt.util.WTException;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;

public class SetPartTypeProcessor {

    public static FormResult execute(NmCommandBean cb){
        FormResult form = new FormResult();
        HttpSession session = cb.getRequest().getSession();
        HttpServletRequest request = cb.getRequest();
        try {
            List<String> oidList = (List<String>)session.getAttribute("oidList");
            //System.out.println("-------oidList:"+oidList);
            IBAUtility ibaUtility = null;
            WTPart part = null;
            for (String oid : oidList) {
                String valueName = oid+"_select";
                String value = request.getParameter(valueName);
                //System.out.println("-------value:"+value);
                part = (WTPart)WCUtil.getPersistable(oid);
                ibaUtility = new IBAUtility(part);
                ibaUtility.setIBAValue("MTYPE", value);
                part = (WTPart)ibaUtility.updateAttributeContainer(part);
                ibaUtility.updateIBAHolder(part);
            }
            
            form.setStatus(FormProcessingStatus.SUCCESS);
            FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "数据处理完毕！");
            form.addFeedbackMessage(message);
            form.setNextAction(FormResultAction.REFRESH_CURRENT_PAGE);
        } catch (Exception e) {
            form.setStatus(FormProcessingStatus.FAILURE);
            FeedbackMessage message;
            try {
                message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "数据处理失败！");
                form.addFeedbackMessage(message);
            } catch (WTException e1) {
                e1.printStackTrace();
            }
            form.setNextAction(FormResultAction.REFRESH_CURRENT_PAGE);
            e.printStackTrace();
        }
        return form;
    }
}
