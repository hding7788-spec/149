package ext.casc.process.mvc.builder;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import wt.fc.IdentityHelper;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.mpmresource.processors.CustomerObjectFormProcessor;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class PartRenameProcessor extends CustomerObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean arg0, List<ObjectBean> arg1) throws WTException {
    	String user = "";
    	try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        HttpServletRequest request = arg0.getRequest();
        Map map = request.getParameterMap();
        String[] pflag = (String[]) map.get("partName");
        String name=pflag[0];
//        IBAHolder actionObj = (IBAHolder)arg0.getActionOid().getRefObject();
        Object actionObj = arg0.getActionOid().getRefObject();
        if (actionObj instanceof WTPart) {
            WTPart part=(WTPart) actionObj;
            WTPartMaster master = (WTPartMaster) part.getMaster();
            WTPartMasterIdentity idy = (WTPartMasterIdentity) master.getIdentificationObject();
            try {
                idy.setName(name);
            } catch (WTPropertyVetoException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            master = (WTPartMaster) IdentityHelper.service
                    .changeIdentity(master, idy);

        }
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }
        return super.doOperation(arg0, arg1);
    }

    public  static String getPartNameByoid(NmOid oid) {
        String name="";
        String[] str = (oid.toString()).split("~");
        String oid1 = str[0];
        WTPart part = (WTPart) findObject(oid1);
        name=part.getName();
        return name;

    }

 // 根据oid得到对象
    public static WTObject findObject(String oid) {
        Object obj = null;
        if (oid == null || oid.equals("")) {
            return null;
        }
        ReferenceFactory factory = new ReferenceFactory();
        try {
            WTReference reference = factory.getReference(oid);
            obj = reference.getObject();
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return (WTObject) obj;
    }

}