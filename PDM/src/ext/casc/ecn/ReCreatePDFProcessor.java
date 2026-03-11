package ext.casc.ecn;

import java.util.List;

import wt.fc.WTObject;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import com.glaway.mpm.change.qchange.helper.QChangeHelper;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class ReCreatePDFProcessor extends DefaultObjectFormProcessor{

	@Override
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
		FormResult formresult = super.doOperation(commandBean, objectBeans);
        WTObject actionObj = (WTObject) commandBean.getActionOid().getRefObject();
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {

        	QChangeHelper.creatWtChangeOrder2PDF(actionObj);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FeedbackMessage message = new FeedbackMessage();
            message.addMessage("操作成功");
            formresult.addFeedbackMessage(message);
            formresult.setNextAction(FormResultAction.NONE);
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formresult;
	}


}
