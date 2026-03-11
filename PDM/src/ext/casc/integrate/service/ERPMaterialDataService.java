package ext.casc.integrate.service;

import ext.casc.integrate.model.GLErpMaterialsBean;
import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;

import java.util.ArrayList;
import java.util.List;

public class ERPMaterialDataService {
    public static List<GLErpMaterialsBean> query(String oid) throws Exception {
        List<GLErpMaterialsBean> list = new ArrayList<GLErpMaterialsBean>();
        CmQuerySpec qs = new CmQuerySpec(GLErpMaterialsBean.class);
        qs.appendWhere(GLErpMaterialsBean.DOCOID,CmQuerySpec.EQUAL,oid);
        CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
        while (qr.hasNext()) {
            list.add((GLErpMaterialsBean) qr.next());
        }
        return list;
    }

    public static boolean  add(GLErpMaterialsBean data) throws Exception {

        CmPersistenceHelper.manager.save(data,false);

        return true;
    }
}
