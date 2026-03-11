package ext.casc.doc;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import wt.content.ApplicationData;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;

import java.util.ArrayList;
import java.util.List;

public class DeleteAttachment4SKProcessor extends DefaultObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> listBean) throws WTException {
        System.out.println("======================DeleteAttachment4SKProcessors start======================");
        FormResult form = new FormResult();
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        String oid = nmCommandBean.getActionOid().getOid().toString();
        WTDocument document = (WTDocument) new ReferenceFactory().getReference(oid).getObject();
        try {
            ArrayList list = nmCommandBean.getSelected();
            for (Object object : list) {
                NmContext nmContext = (NmContext)object;
                ApplicationData seleData = (ApplicationData)nmContext.getTargetOid().getRefObject();
                ContentServerHelper.service.deleteContent(document, seleData);
            }
            PersistenceHelper.manager.refresh(document);
            form.setStatus(FormProcessingStatus.SUCCESS);
            form.setNextAction(FormResultAction.REFRESH_OPENER);
        } catch (WTRuntimeException e) {
            e.printStackTrace();
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
        }finally{
            SessionServerHelper.manager.setAccessEnforced(enforce);
        }
        return form;
    }

}
