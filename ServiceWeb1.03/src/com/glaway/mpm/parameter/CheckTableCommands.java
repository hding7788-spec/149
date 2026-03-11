package com.glaway.mpm.parameter;

import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import wt.doc.WTDocument;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;

import java.io.IOException;

public class CheckTableCommands {

    public static FormResult recordCollect(NmCommandBean commandBean) throws WTException {
        FormResult formResult = new FormResult();
        FeedbackMessage feedBackMsg = null;
        String message = "";
        String technicsNumber = "";
        String technicsVersion = "";
        Object obj = commandBean.getActionOid().getRefObject();
        if(obj instanceof WTDocument){
            WTDocument document = (WTDocument) obj;
            technicsNumber = document.getNumber();
            technicsVersion = document.getIterationDisplayIdentifier().toString();
        }
        if(technicsNumber == null || "".equals(technicsNumber)){
            message = "获取文档编号出错";
        }
        try {
            if("".equals(message)){
                String urlBase = WTProperties.getLocalProperties().getProperty("java.rmi.server.hostname");
                String webAPP = WTProperties.getLocalProperties().getProperty("wt.webapp.name");
                String url = "http://"+urlBase+"/"+webAPP;
                url += "/netmarkets/jsp/ext/glaway/mpm/checkTable/showSummaryTable.jsp?technicsNumber=" + technicsNumber + "&technicsVersion=" + technicsVersion;
                formResult.setNextAction(FormResultAction.JAVASCRIPT);
                formResult.setJavascript("window.open(\""+url+"\")");
            }else{
                formResult.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), null, null, new String[]{message}));
                formResult.setNextAction(FormResultAction.NONE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return formResult;
    }
}
