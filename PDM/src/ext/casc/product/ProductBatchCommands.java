package ext.casc.product;

import java.io.Serializable;

import wt.fc.PersistenceHelper;
import wt.inf.container.WTContained;
import wt.method.RemoteAccess;
import wt.util.WTException;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.util.DBUtil;

public class ProductBatchCommands implements RemoteAccess, Serializable {

    private static final long serialVersionUID = 1L;

    public ProductBatchCommands() {}

    public static FormResult addBatch(NmCommandBean nmcommandbean) throws WTException {
        FormResult formresult = new FormResult(FormProcessingStatus.SUCCESS);
        formresult.setNextAction(FormResultAction.REFRESH_OPENER);
        WTContained container = nmcommandbean.getContainer();
        String productOid = String.valueOf(PersistenceHelper.getObjectIdentifier(container).getId());
        String name = nmcommandbean.getTextParameter("batchName");
        if (name != null) {
        	DBUtil.addBatch(productOid, name);
        } else {
            formresult.setStatus(FormProcessingStatus.FAILURE);
            return formresult;
        }
        return formresult;
    }
}
