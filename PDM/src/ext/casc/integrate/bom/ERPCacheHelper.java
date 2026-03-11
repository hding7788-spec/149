package ext.casc.integrate.bom;

import ext.casc.integrate.model.GLErpMaterialsBean;
import ext.casc.integrate.model.GLErpPbomPartBean;
import ext.casc.integrate.model.GLErpPeiTaoPartBean;
import ext.casc.integrate.service.ERPBomDataService;
import ext.casc.integrate.service.ERPMaterialDataService;
import ext.casc.persistence.PersistenceCommonHelper;
import ext.casc.util.TimestampConverter;
import ext.sast.common.fc.CmPersistenceHelper;
import wt.doc.WTDocument;

import java.util.ArrayList;
import java.util.List;

public class ERPCacheHelper {
    public static ERPCacheBean getErpCacheList(List<WTDocument> documentList,String pplanType) throws Exception {
        ERPCacheBean bean = new ERPCacheBean();
        List<WTDocument> otherDocList = new ArrayList<WTDocument>();
        for(WTDocument doc:documentList){
            String  oid = PersistenceCommonHelper.getOid(doc);
            List<GLErpMaterialsBean> materialsBeanList = ERPMaterialDataService.query(oid);
            //List<GLErpPbomPartBean> glErpMatchPartBeans = ERPBomDataService.query(oid, ERPBomDataService.MATCH);
            //List<GLErpPbomPartBean>  glErpNewPartBeans= ERPBomDataService.query(oid, ERPBomDataService.NEW);
            List<GLErpPbomPartBean> glErpMatchPartBeans = new ArrayList<GLErpPbomPartBean>();
            List<GLErpPbomPartBean>  glErpNewPartBeans= new ArrayList<GLErpPbomPartBean>();
            List<GLErpPbomPartBean> erpPartList = ERPBomDataService.query(oid);
            for(GLErpPbomPartBean erpPart:erpPartList){
                if(ERPBomDataService.MATCH.equals(erpPart.getIsMatch())){
                    glErpMatchPartBeans.add(erpPart);
                }else{
                    glErpNewPartBeans.add(erpPart);
                }
            }
            if("TEMP".equals(pplanType)){
                List<GLErpPeiTaoPartBean>  glErpPeitaoBeans= ERPBomDataService.queryPeiTao(oid);
                if(glErpNewPartBeans.isEmpty()&&materialsBeanList.isEmpty()&&glErpMatchPartBeans.isEmpty()&&glErpPeitaoBeans.isEmpty()){
                    otherDocList.add(doc);
                    bean.setDocumentList(otherDocList);
                }else{
                    bean.getMaterialBeanList().addAll(materialsBeanList);
                    bean.getErpNewPartList().addAll(glErpNewPartBeans);
                    bean.getErpMatchPartList().addAll(glErpMatchPartBeans);
                    bean.getErpPeiTaoPartList().addAll(glErpPeitaoBeans);
                }
            }else{
                if(glErpNewPartBeans.isEmpty()&&materialsBeanList.isEmpty()&&glErpMatchPartBeans.isEmpty()){
                    otherDocList.add(doc);
                    bean.setDocumentList(otherDocList);
                }else{
                    bean.getMaterialBeanList().addAll(materialsBeanList);
                    bean.getErpNewPartList().addAll(glErpNewPartBeans);
                    bean.getErpMatchPartList().addAll(glErpMatchPartBeans);
                }
            }
        }
        return bean;
    }

    public static boolean saveErpNewPartCache(List<GLErpPbomPartBean>  erpNewPartList ) throws Exception {
        boolean flag = true;
        String synchTime = TimestampConverter.toChinaStandardTime(System.currentTimeMillis());
        for(GLErpPbomPartBean erpPbomPartBean:erpNewPartList){
            erpPbomPartBean.setKeyId(erpPbomPartBean.getDocoid().substring(erpPbomPartBean.getDocoid().lastIndexOf(":")+1)+"_"+erpPbomPartBean.getChbm()+"_"+erpPbomPartBean.getUsingType());
            erpPbomPartBean.setIsMatch(ERPBomDataService.NEW);
            erpPbomPartBean.setSynchTime(synchTime);
            boolean b = ERPBomDataService.add(erpPbomPartBean);
            if(!b){
                flag = false;
            }
        }
        return flag;
    }

    public static boolean saveErpMatchPartCache(List<GLErpPbomPartBean>  erpMatchPartList ) throws Exception {
        boolean flag = true;
        String synchTime = TimestampConverter.toChinaStandardTime(System.currentTimeMillis());
        for(GLErpPbomPartBean erpPbomPartBean:erpMatchPartList){
            erpPbomPartBean.setKeyId(erpPbomPartBean.getDocoid().substring(erpPbomPartBean.getDocoid().lastIndexOf(":")+1)+"_"+erpPbomPartBean.getChbm()+"_"+erpPbomPartBean.getUsingType());
            erpPbomPartBean.setIsMatch(ERPBomDataService.MATCH);
            erpPbomPartBean.setSynchTime(synchTime);

            boolean b = ERPBomDataService.add(erpPbomPartBean);
            if(!b){
                flag = false;
            }
        }
        return flag;
    }

    public static boolean  saveMaterialCache(List<GLErpMaterialsBean> materialsBeanList) throws Exception {
        boolean flag = true;
        String synchTime = TimestampConverter.toChinaStandardTime(System.currentTimeMillis());

        for(GLErpMaterialsBean erpMaterialsBean:materialsBeanList){
            erpMaterialsBean.setKeyId(erpMaterialsBean.getDocoid().substring(erpMaterialsBean.getDocoid().lastIndexOf(":")+1)+"_"+erpMaterialsBean.getChbm()+"_"+erpMaterialsBean.getUsingType());
            erpMaterialsBean.setSynchTime(synchTime);
            boolean b = ERPMaterialDataService.add(erpMaterialsBean);
            if(!b){
                flag = false;
            }
        }
        return flag;
    }


    public static void  savePeiTaoPbomCache(List<GLErpPeiTaoPartBean> erpPeiTaoPartBeans) throws Exception {
        String synchTime = TimestampConverter.toChinaStandardTime(System.currentTimeMillis());

        for(GLErpPeiTaoPartBean erpPeiTaoPartBean:erpPeiTaoPartBeans){
            erpPeiTaoPartBean.setKeyId(erpPeiTaoPartBean.getDocoid().substring(erpPeiTaoPartBean.getDocoid().lastIndexOf(":")+1)+"_"+erpPeiTaoPartBean.getChildNumber()+"_"+erpPeiTaoPartBean.getPartType());
            erpPeiTaoPartBean.setSynchTime(synchTime);
            CmPersistenceHelper.manager.save(erpPeiTaoPartBean,false);

        }
    }

    public static void  savePeiTaoPbomCache(List<GLErpPeiTaoPartBean> erpPeiTaoPartBeans,boolean autoCommit) throws Exception {
        String synchTime = TimestampConverter.toChinaStandardTime(System.currentTimeMillis());

        for(GLErpPeiTaoPartBean erpPeiTaoPartBean:erpPeiTaoPartBeans){
            erpPeiTaoPartBean.setKeyId(erpPeiTaoPartBean.getDocoid().substring(erpPeiTaoPartBean.getDocoid().lastIndexOf(":")+1)+"_"+erpPeiTaoPartBean.getChildNumber()+"_"+erpPeiTaoPartBean.getPartType());
            erpPeiTaoPartBean.setSynchTime(synchTime);
            CmPersistenceHelper.manager.save(erpPeiTaoPartBean,autoCommit);

        }
    }


}
