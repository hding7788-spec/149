package ext.casc.product;

import java.util.List;

import wt.util.WTException;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;

import ext.casc.util.DBUtil;

public class ProductBatchesProcessor extends DefaultObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean commandbean, List<ObjectBean> list) throws WTException {
        FormResult form = new FormResult();
        List selected = commandbean.getSelected();
        for (Object obj : selected) {
    		NmContext nmContext = (NmContext)obj;
    		String oid = nmContext.getTargetOid().toString();
    		DBUtil.deleteBatch(oid);
		}
        form.setStatus(FormProcessingStatus.SUCCESS);
        form.setNextAction(FormResultAction.NONE);
        form.setNextAction(FormResultAction.REFRESH_OPENER);
        return form;
    }
}
