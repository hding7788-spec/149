package ext.casc.process.assign;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import wt.util.WTException;

import java.util.List;

public class ProAssignTaskBuilderAddPartProcessor extends DefaultObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean commandbean, List<ObjectBean> list) throws WTException {
        //HttpServletRequest request = commandbean.getRequest();
      //  HttpSession session = commandbean.getRequest().getSession();
       // List<String> oidList = (List<String>) session.getAttribute("oidList");
        FormResult form = new FormResult();
       // FeedbackMessage message = new FeedbackMessage();
       // StringBuffer msg = new StringBuffer();
       // FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
       /* List seleted = commandbean.getSelected();
        if (seleted != null && !seleted.isEmpty()) {
            List tempList = new ArrayList();
            for (Object object : seleted) {
                if (object instanceof NmContext) {
                    NmContext nmContext = (NmContext)object;
                    NmOid nmOid = nmContext.getTargetOid();
                    Object tempObject = nmOid.getRefObject();
                    if (tempObject instanceof WTPart) {
                        WTPart part = (WTPart)tempObject;
                        part = ProcessUtil.getPartByNumber(part.getNumber(), "Manufacturing");
                        String oid = PersistenceHelper.getObjectIdentifier(part).toString();
                        oidList.add(oid);
                        tempList.add(part);
                    }
                }
            }
            session.setAttribute("oidList", oidList);
            session.setAttribute("addObject", tempList);
        }
        msg.append(ProcessConstants.JSP_MSG_SUCCESS);
        form.setStatus(formProcessingStatus);
        message.addMessage(msg.toString());
        form.addFeedbackMessage(message);*/
        form.setNextAction(FormResultAction.NONE);
        form.setJavascript("addParts();window.close();");
        form.setNextAction(FormResultAction.JAVASCRIPT);
        return form;
    }

}
