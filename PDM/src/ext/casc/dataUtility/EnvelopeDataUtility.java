package ext.casc.dataUtility;

import wt.part.WTPart;
import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class EnvelopeDataUtility extends AbstractDataUtility {

    @Override
    public Object getDataValue(String str, Object obj, ModelContext context) throws WTException {
        NmCommandBean commandBean = context.getNmCommandBean();
        NmOid nmOid = commandBean.getPageOid();
        Object object = nmOid.getRefObject();
        if (object instanceof WTPart) {
            WTPart part = (WTPart)object;
            String number = part.getNumber();
            if ("number".equals(str)) {
                return number;
            }
        }
        return null;
    }

}
