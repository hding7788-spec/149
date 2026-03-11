package com.glaway.mpm.print.commands;

import com.glaway.mpm.print.util.PrintDataQueryUtil;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import wt.session.SessionHelper;
import wt.util.WTException;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;

public class PrintCommands {

    public static FormResult save(NmCommandBean commandBean) throws WTException {
        FormResult formResult = new FormResult();
        FeedbackMessage feedBackMsg = null;
        String message = "";
        HttpServletRequest request = commandBean.getRequest();
        Map map = request.getParameterMap();
        Iterator iterator = map.keySet().iterator();
        String[] userNames = null;
        String[] employeeNos = null;
        while (iterator.hasNext()) {
            String key = String.valueOf(iterator.next());
            if (key.contains("_userName") && !key.contains("old")) {
                userNames = (String[]) map.get(key);
            }
            if (key.contains("_employeeNo") && !key.contains("old")) {
                employeeNos = (String[]) map.get(key);
            }
        }
        if (userNames != null && employeeNos != null) {
            for (int i = 0; i < userNames.length; i++) {
                message += PrintDataQueryUtil.setEmployeeNo(userNames[i], employeeNos[i]);
            }
        }
        if ("保存成功".equals(message) || "更新成功".equals(message)) {
            feedBackMsg = new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), null, null, new String[]{message});
        }else{
            feedBackMsg = new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), null, null, new String[]{message});
        }
        formResult.addFeedbackMessage(feedBackMsg);
        formResult.setNextAction(FormResultAction.NONE);
        return formResult;
    }
}
