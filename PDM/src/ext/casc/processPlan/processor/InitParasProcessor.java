package ext.casc.processPlan.processor;

import com.glaway.mpm.mpmresource.processors.CustomerObjectFormProcessor;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import ext.casc.doc.DocumentCommonHelper;
import ext.casc.integrate.util.BomUtil;
import ext.casc.mpm.process.TechnicsGenerator;
import ext.casc.util.IBAHelper;
import org.apache.commons.lang3.StringUtils;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.container.WTContainerTemplate;
import wt.inf.template.ContainerTemplateHelper;
import wt.inf.template.WTContainerTemplateMaster;
import wt.inf.template._WTContainerTemplateMaster;
import wt.org.WTOrganization;
import wt.org.WTUser;
import wt.org._WTPrincipal;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static ext.casc.mpm.process.TechnicsGenerator.PPLANTYPE;

public class InitParasProcessor extends CustomerObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> var2) throws WTException {
        List<WTPart> parts = new ArrayList<WTPart>();
        ArrayList<NmOid> oids = commandBean.getNmOidSelected();
        if(oids.isEmpty()){
            ArrayList list = commandBean.getSelectedInOpener();
        	for (Object singleObject : list) {
                 if (singleObject instanceof NmContext) {
                     NmOid oid = ((NmContext) singleObject).getTargetOid();
                     Persistable selectObj = oid.getWtRef().getObject();
                     if(selectObj instanceof WTPart){
                         parts.add((WTPart)selectObj);
                     }
                 }
        	}
        }else{
        	for (NmOid nmOid : oids) {
                Object object = nmOid.getRefObject();
                if (object instanceof WTPart) {
                    WTPart part = (WTPart) object;
                    parts.add(part);
                }
            }
        }

        FormResult formResult = new FormResult();
        FeedbackMessage message = new FeedbackMessage();
        FormProcessingStatus status = FormProcessingStatus.SUCCESS;
        String msgStr = "";
        HttpServletRequest request = commandBean.getRequest();
        Map map = request.getParameterMap();
        String[] deptArray = (String[]) map.get("dept");
        String[] pplantypeArray = (String[]) map.get("pplantype");
        String[] linShiNumArray = (String[]) map.get("linShiNum");
        String[] zfFlagArray = (String[]) map.get("zfFlag");
        String[] technicsTypeArray = (String[]) map.get("technicsType");

        String dept = deptArray[0];
        String pplantype = pplantypeArray[0];
        String linShiNum = linShiNumArray[0];
        String zfFlag = zfFlagArray[0];
        String technicsType = technicsTypeArray[0];
        Map<String, Object> attris = new HashMap<String, Object>();

        attris.put(TechnicsGenerator.TECHNICSTYPE, technicsType);
        attris.put(PPLANTYPE, pplantype);
        attris.put(TechnicsGenerator.LINSHINUM, linShiNum);
        attris.put(TechnicsGenerator.ZFFLAG, zfFlag);
        attris.put(TechnicsGenerator.DEPT, dept);

        HashMap<String,WTDocument> needRevise = new HashMap<String,WTDocument>();

        try {
            if("正式工艺文件".equals(pplantype)&&"Z".equals(zfFlag)){
                for(WTPart part :parts){
                    List<WTDocument> documents =  BomUtil.getAllWTDocumentByAllSameVersionViewPart(part);
                    for(WTDocument doc:documents) {
                        String pplantype1 = IBAHelper.getIBAStringValue(doc, "PPLANTYPE");
                        String zfflag1 = IBAHelper.getIBAStringValue(doc, "ZFFLAG");
                        if("正式工艺文件".equals(pplantype1)&&"Z".equals(zfflag1)){
                            //WorkflowHelper.setObjectLifeCycle(doc,"OBSOLESCENCE");
                            // DocumentCommonHelper.changeName(doc,doc.getName()+"_作废");
                            if("space".equals(doc.getVersionIdentifier().getValue())) {
                                QueryResult allIterations = VersionControlHelper.service.allIterationsOf(doc.getMaster());
                                while(allIterations.hasMoreElements()){
                                    DocumentCommonHelper.removePartDescLink((WTDocument) allIterations.nextElement());
                                }
                                String state = doc.getState().getState().toString();
                                if(state.equals("INWORK")||state.equals("REWORK")){
                                    PersistenceHelper.manager.delete(doc);
                                }else{
                                    msgStr = "【"+doc.getName()+"】状态已提交签审流程或该工艺修改者非本人，无法自动删除并重新生成!";
                                }
                                break;
                            }else{
                                needRevise.put(part.getNumber(),doc);
                                break;
                            }
                        }
                    }

                }
            }
            attris.put("needRevise", needRevise);
            msgStr = TechnicsGenerator.technicsGenerator(parts, attris);
            if (StringUtils.isEmpty(msgStr)) {
               msgStr = "工艺批量生成成功!";

            } else {
                status = FormProcessingStatus.FAILURE;
            }
        } catch (Exception e) {
            e.printStackTrace();
            msgStr = e.getLocalizedMessage();
            formResult.setStatus(FormProcessingStatus.FAILURE);
        } finally {
            message.addMessage(msgStr);
        }
        formResult.addFeedbackMessage(message);
        formResult.setStatus(status);
        formResult.setNextAction(FormResultAction.NONE);
        return formResult;
}

public static PDMLinkProduct createProduct(String productName, String productTemplate, String description) throws WTException {
    boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
    WTContainerTemplate localWTContainerTemplate = null;
    WTUser user = (WTUser) SessionHelper.getPrincipal();
    try {
        WTContainerTemplateMaster master = getTemplateByTemplateName(productTemplate);
        localWTContainerTemplate = ContainerTemplateHelper.service.getContainerTemplate(master);
        WTOrganization localWTOrganization = getOrgByOrgName("149");
        WTContainerRef localWTContainerRef = WTContainerHelper.service.getOrgContainerRef(localWTOrganization);
        PDMLinkProduct product = PDMLinkProduct.newPDMLinkProduct();
        product.setName(productName);
        product.setCreator(user);
        product.setContainerReference(localWTContainerRef);
        product.setContainerTemplate(localWTContainerTemplate);
        product.setDescription(description);
        product = (PDMLinkProduct) WTContainerHelper.service.create(product);
        return product;
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        SessionServerHelper.manager.setAccessEnforced(enforce);
    }
    return null;
}

public static WTOrganization getOrgByOrgName(String name) {
    WTOrganization org = null;
    try {
        QuerySpec spec = new QuerySpec(WTOrganization.class);
        spec.appendWhere(new SearchCondition(WTOrganization.class, _WTPrincipal.NAME, SearchCondition.EQUAL, name));
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) spec);
        if (qResult.hasMoreElements()) {
            org = (WTOrganization) qResult.nextElement();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return org;

}

public static WTContainerTemplateMaster getTemplateByTemplateName(String name) {
    WTContainerTemplateMaster template = null;
    try {
        QuerySpec spec = new QuerySpec(WTContainerTemplateMaster.class);
        spec.appendWhere(new SearchCondition(WTContainerTemplateMaster.class, _WTContainerTemplateMaster.NAME, SearchCondition.EQUAL, name));
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) spec);
        if (qResult.hasMoreElements()) {
            template = (WTContainerTemplateMaster) qResult.nextElement();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return template;

}

}
