package ext.casc.integrate.service;

import ext.casc.integrate.model.GLErpPbomPartBean;
import ext.casc.integrate.model.GLErpPeiTaoPartBean;
import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;

import java.util.ArrayList;
import java.util.List;

public class ERPBomDataService {
    public static final String MATCH = "1";
    public static final String  NEW = "0";
    public static final String  PBOM = "2";
    public static List<GLErpPbomPartBean> query(Long oid) throws Exception {
        List<GLErpPbomPartBean> list = new ArrayList<GLErpPbomPartBean>();
        CmQuerySpec qs = new CmQuerySpec(GLErpPbomPartBean.class);
        qs.appendWhere(GLErpPbomPartBean.DOCOID,CmQuerySpec.EQUAL,oid);
        CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
        while (qr.hasNext()) {
            list.add((GLErpPbomPartBean) qr.next());
        }
        return list;
    }

    public static boolean  add(GLErpPbomPartBean data) throws Exception {

        CmPersistenceHelper.manager.save(data,false);

        return true;
    }


    public static List<GLErpPbomPartBean> query(String oid, String isMatch) throws Exception {
        List<GLErpPbomPartBean> list = new ArrayList<GLErpPbomPartBean>();
        CmQuerySpec qs = new CmQuerySpec(GLErpPbomPartBean.class);
        qs.appendWhere(GLErpPbomPartBean.DOCOID,CmQuerySpec.EQUAL,oid);
        qs.appendAnd();
        qs.appendWhere(GLErpPbomPartBean.ISMATCH,CmQuerySpec.EQUAL,isMatch);
        CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
        while (qr.hasNext()) {
            list.add((GLErpPbomPartBean) qr.next());
        }
        return list;
    }

    public static List<GLErpPbomPartBean> query(String oid) throws Exception {
        List<GLErpPbomPartBean> list = new ArrayList<GLErpPbomPartBean>();
        CmQuerySpec qs = new CmQuerySpec(GLErpPbomPartBean.class);
        qs.appendWhere(GLErpPbomPartBean.DOCOID,CmQuerySpec.EQUAL,oid);
        
        CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
        while (qr.hasNext()) {
            list.add((GLErpPbomPartBean) qr.next());
        }
        return list;
    }


    public static List<GLErpPeiTaoPartBean> queryPeiTao(String oid) throws Exception {
        List<GLErpPeiTaoPartBean> list = new ArrayList<GLErpPeiTaoPartBean>();
        CmQuerySpec qs = new CmQuerySpec(GLErpPeiTaoPartBean.class);
        qs.appendWhere(GLErpPeiTaoPartBean.DOCOID,CmQuerySpec.EQUAL,oid);
        CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
        while (qr.hasNext()) {
            list.add((GLErpPeiTaoPartBean) qr.next());
        }
        return list;
    }
}
