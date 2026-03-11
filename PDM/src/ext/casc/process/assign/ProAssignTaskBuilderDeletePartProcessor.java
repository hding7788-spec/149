package ext.casc.process.assign;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import wt.fc.PersistenceHelper;
import wt.part.WTPart;
import wt.util.WTException;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;

import ext.casc.process.util.ProcessUtil;

public class ProAssignTaskBuilderDeletePartProcessor extends DefaultObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean commandbean, List<ObjectBean> list) throws WTException {
        FormResult form = new FormResult();
        HttpServletRequest request = commandbean.getRequest();
        HttpSession session = commandbean.getRequest().getSession();
        List<String> oidList = (List<String>) session.getAttribute("oidList");
        List seleted = commandbean.getSelected();
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
                        oidList.remove(oid);
                        tempList.add(part);
                    }
                }
            }
            session.setAttribute("oidList", oidList);
            session.setAttribute("deleteObject", tempList);
            session.setAttribute("flag", "1");
        }

        form.setStatus(FormProcessingStatus.SUCCESS);
        super.doOperation(commandbean, list);

        return form;
    }

}
