package ext.casc.integrate.pfmea.processor;

import com.glaway.mpm.processplan.checkouttable.TechnicsZipUtil;
import com.glaway.mpm.util.*;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.integrate.pfmea.bean.PfDocBean;
import ext.casc.integrate.pfmea.bean.PfPartBean;
import ext.casc.integrate.pfmea.bean.PfProcessBean;
import ext.casc.integrate.pfmea.bean.PfProductBean;
import ext.casc.part.CSCPart;
import ext.casc.util.IBAUtility;
import org.dom4j.Document;
import org.dom4j.Element;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContainer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.session.SessionHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PfmeaRequestProcessor {

    private static final String SEQ_PRODUCT = "PF_SEQ_PRODUCT";
    private static final String SEQ_PART = "PF_SEQ_PART";
    private static final String SEQ_PROCESS = "PF_SEQ_PROCESS";
    private static final String SEQ_DOC = "PF_SEQ_DOC";

    public static FormResult sendRequest(NmCommandBean commandBean) throws WTException {
        FormResult formResult = new FormResult();
        FeedbackMessage feedBackMsg = null;
        String message = "发送请求成功";
        String templateId = commandBean.getRequest().getParameter("templateId");
        String templateName = commandBean.getRequest().getParameter("templateName");
        WTPart wtPart = (WTPart) commandBean.getPageOid().getRefObject();
        IBAUtility ibaUtility = new IBAUtility(wtPart);
        String phase = ibaUtility.getIBAValue("PHASE_CODE");

        WTContainer container = wtPart.getContainer();
        ibaUtility = new IBAUtility((IBAHolder) container);
        String xhjh = ibaUtility.getIBAValue("XHJH");
        String containerName = container.getName();

        int seq_product = Integer.parseInt(PersistenceHelper.manager.getNextSequence(SEQ_PRODUCT));
        List<PfPartBean> pfPartBeanList = new ArrayList<PfPartBean>();
        List<String> docNumberList = new ArrayList<String>();
        getAllChildParts(pfPartBeanList, wtPart, wtPart, seq_product,docNumberList);

        PfProductBean pfProductBean = new PfProductBean();
        pfProductBean.setId(seq_product);
        pfProductBean.setPindex(xhjh);
        pfProductBean.setPname(containerName);
        pfProductBean.setPhase(phase);
        pfProductBean.setTemplateid(templateId);
        pfProductBean.setPfPartBeanList(pfPartBeanList);

        saveMessage(pfProductBean);

//        try {
//            String result = PfmeaClientRMI.createProject(Long.valueOf(seq_product));
//        } catch (UnsupportedEncodingException_Exception e) {
//            e.printStackTrace();
//        }
        String result = "";
        feedBackMsg = new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), null, null, new String[]{message});
        formResult.addFeedbackMessage(feedBackMsg);
        formResult.setNextAction(FormResultAction.JAVASCRIPT);
        formResult.setJavascript("openPfmeaHome();");
//        formResult.setJavascript("oformResult.setNextAction(FormResultAction.NONE);penPfmeaHome("+result+");");

        return formResult;
    }

    private static void saveMessage(PfProductBean pfProductBean) {
        DBConnUtil dbConnUtil = null;
        try {
            dbConnUtil = new DBConnUtil();
            String productInsert = "insert into PF_PRODUCT values ";
            String partInsert = "insert into PF_BOM values ";
            String processInsert = "insert into PF_PROCESS values ";
            String docInsert = "insert into PF_DOC values ";
            StringBuilder productSb;
            StringBuilder partSb;
            StringBuilder processSb;
            StringBuilder docSb;
            if (pfProductBean != null) {
                productSb = new StringBuilder();
                productSb.append(productInsert);
                productSb.append("(").append(pfProductBean.getId()).append(",'").append(pfProductBean.getPindex()).append("','")
                        .append(pfProductBean.getPname()).append("','").append(pfProductBean.getPhase()).append("',")
                        .append(pfProductBean.getTemplateid()).append(",'").append("')");
                //保存产品信息
                dbConnUtil.executeUpdate(productSb.toString());
                dbConnUtil.commit();
                List<PfPartBean> pfPartBeanList = pfProductBean.getPfPartBeanList();
                if (pfPartBeanList != null && pfPartBeanList.size() > 0) {
                    for (PfPartBean pfPartBean : pfPartBeanList) {
                        partSb = new StringBuilder();
                        partSb.append(partInsert);
                        partSb.append("(").append(pfPartBean.getId()).append(",'").append(pfPartBean.getPartnumber()).append("','")
                                .append(pfPartBean.getPartid()).append("','").append(pfPartBean.getPartversion()).append("','")
                                .append(pfPartBean.getPartname()).append("','").append(pfPartBean.getFpartnumber()).append("','")
                                .append(pfPartBean.getFpartid()).append("',").append(pfPartBean.getPid()).append(")");
                        //保存bom信息
                        dbConnUtil.executeUpdate(partSb.toString());
                        dbConnUtil.commit();
                        List<PfDocBean> docBeanList = pfPartBean.getDocBeanList();
                        if(docBeanList != null && docBeanList.size() > 0){
                            for(PfDocBean docBean : docBeanList){
                                docSb = new StringBuilder();
                                docSb.append(docInsert);
                                docSb.append("(").append(docBean.getId()).append(",'").append(docBean.getName()).append("','")
                                        .append(docBean.getDocid()).append("','").append(docBean.getPardid()).append("',")
                                        .append(docBean.getPid()).append(")");
                                //保存工艺信息
                                dbConnUtil.executeUpdate(docSb.toString());
                                dbConnUtil.commit();
                                List<PfProcessBean> processBeanList = docBean.getProcessBeanList();
                                if(processBeanList != null && processBeanList.size() > 0){
                                    for (PfProcessBean pfProcessBean : processBeanList) {
                                        processSb = new StringBuilder();
                                        processSb.append(processInsert);
                                        processSb.append("(").append(pfProcessBean.getId()).append(",'").append(pfProcessBean.getStepNumber()).append("','")
                                                .append(pfProcessBean.getStepName()).append("','").append(pfProcessBean.getRequirements()).append("','")
                                                .append(pfProcessBean.getPartid()).append("',").append(pfProcessBean.getPid()).append(",'").append(pfProcessBean.getDocid()).append("'").append(")");
                                        //保存工序工步信息
                                        dbConnUtil.executeUpdate(processSb.toString());
                                        dbConnUtil.commit();
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (dbConnUtil != null) {
                    dbConnUtil.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private static void getAllChildParts(List<PfPartBean> partList, WTPart part, WTPart fPart, int productId, List<String> docNumberList) {
        try {

            int seq_part = Integer.parseInt(PersistenceHelper.manager.getNextSequence(SEQ_PART));
            String partOid = String.valueOf(part.getPersistInfo().getObjectIdentifier().getId());
            String partNumber = part.getNumber();
            String partName = part.getName();
            String partVersion = part.getIterationDisplayIdentifier().toString();
            String fPartNumber = fPart.getNumber();
            String fPartOid = String.valueOf(fPart.getPersistInfo().getObjectIdentifier().getId());

            PfPartBean pfPartBean = new PfPartBean();
            pfPartBean.setId(seq_part);
            pfPartBean.setPartid(partOid);
            pfPartBean.setPartnumber(partNumber);
            pfPartBean.setPartname(partName);
            pfPartBean.setPartversion(partVersion);
            pfPartBean.setFpartnumber(fPartNumber);
            pfPartBean.setFpartid(fPartOid);
            pfPartBean.setPid(productId);


            List<PfDocBean> docBeanList = new ArrayList<PfDocBean>();
            PfDocBean pfDocBean;
            List<PfProcessBean> processBeanList;
            PfProcessBean pfProcessBean;
            List<WTDocument> documentList = WTPartUtil.getDescribedDocumentByPart(part, "casc.sast.149.PROCESS_PLAN");
            for (WTDocument wtDocument : documentList) {
                int seq_doc = Integer.parseInt(PersistenceHelper.manager.getNextSequence(SEQ_DOC));
                String docid = String.valueOf(wtDocument.getPersistInfo().getObjectIdentifier().getId());
                pfDocBean = new PfDocBean();
                pfDocBean.setId(seq_doc);
                pfDocBean.setDocid(docid);
                pfDocBean.setName(wtDocument.getName());
                pfDocBean.setPardid(partOid);
                pfDocBean.setPid(productId);
                processBeanList = new ArrayList<PfProcessBean>();
                String docNumber = wtDocument.getNumber();
                String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(wtDocument);
                if (docType.contains("casc.sast.149.reportTechnics")) {
                    continue;
                }
                if(docNumberList.contains(docNumber)){
                    continue;
                }else{
                    docNumberList.add(docNumber);
                }
                ApplicationData data = WTDocumentUtil.getPrimaryByDocument(wtDocument);
                byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
                String technicsDirectory = PropertiesUtil.getTempPath() + File.separator + "pfmeaTemp"  + File.separator + docNumber;
                File fileDir = new File(technicsDirectory);
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }
                TechnicsZipUtil.unZip(bytes, technicsDirectory, technicsDirectory + File.separator);
                String xmlPath = technicsDirectory + File.separator + docNumber + ".xml";
                File xmlFile = new File(xmlPath);
                if (xmlFile.exists()) {
                    Document doc = XmlUtility.getDocument(xmlFile);
                    Element technicsElement = XmlUtility.getTechnicsElement(doc);
                    String technicsType = technicsElement.attributeValue("ZFFLAG");
                    Map<String, Element> zfLink = new HashMap<String, Element>();
                    if ("Z".equals(technicsType)) {
                        String technicsNumber = technicsElement.attributeValue("technicsNumber");
                        String version = technicsElement.attributeValue("version");
                        if (version.contains(".")) {
                            version = version.substring(0,version.indexOf("."));
                        }
                        zfLink = getZfLink(technicsNumber, version);

                    }
                    List<Element> stepElements = XmlUtility.getAllSteps(technicsElement);
                    int count = 10;
                    for (Element stepEle : stepElements) {

                        String stepNumber = stepEle.attributeValue("stepNumber");
                        String newStepNumber = String.valueOf(count);
                        String stepName = stepEle.attributeValue("stepName");
                        String bsoID = stepEle.attributeValue("bsoID");
                        if(zfLink.containsKey(bsoID)){
                            Element fzTech = zfLink.get(bsoID);
                            List<Element> fzStepEleList = XmlUtility.getAllSteps(fzTech);
                            for(Element fzStepELe : fzStepEleList){
                                String fzStepNumber = fzStepELe.attributeValue("stepNumber");
                                String fzStepName = fzStepELe.attributeValue("stepName");
                                newStepNumber = String.valueOf(count);
                                int seq_process = Integer.parseInt(PersistenceHelper.manager.getNextSequence(SEQ_PROCESS));
                                pfProcessBean = new PfProcessBean();
                                pfProcessBean.setId(seq_process);
                                pfProcessBean.setStepNumber(newStepNumber);
                                pfProcessBean.setStepName(fzStepName);
                                pfProcessBean.setRequirements("");
                                pfProcessBean.setPartid(partOid);
                                pfProcessBean.setPid(productId);
                                pfProcessBean.setDocid(docid);
                                processBeanList.add(pfProcessBean);
                                count += 10;
                            }
                        }else{
                            int seq_process = Integer.parseInt(PersistenceHelper.manager.getNextSequence(SEQ_PROCESS));
                            pfProcessBean = new PfProcessBean();
                            pfProcessBean.setId(seq_process);
                            pfProcessBean.setStepNumber(newStepNumber);
                            pfProcessBean.setStepName(stepName);
                            pfProcessBean.setRequirements("");
                            pfProcessBean.setPartid(partOid);
                            pfProcessBean.setPid(productId);
                            pfProcessBean.setDocid(docid);
                            processBeanList.add(pfProcessBean);
                            count += 10;
                        }


                    }

                }
                pfDocBean.setProcessBeanList(processBeanList);
                docBeanList.add(pfDocBean);
            }
            pfPartBean.setDocBeanList(docBeanList);
//            pfPartBean.setProcessBeanList(processBeanList);
            partList.add(pfPartBean);
            QueryResult qr = WTPartHelper.service.getUsesWTParts(part, getConfigSpec());
            if (qr != null) {
                while (qr.hasMoreElements()) {
                    Persistable[] per = (Persistable[]) qr.nextElement();
                    Object obj = per[1];
                    if (obj instanceof WTPart) {
                        WTPart childPart = (WTPart) obj;
                        String viewName = childPart.getViewName();
                        if ("Design".equals(viewName)) {
                            childPart = CSCPart.getPartByNumberAndViewName(childPart.getNumber(), "Manufacturing");
                        }
                        getAllChildParts(partList, childPart, part, productId, docNumberList);
                    } else if (obj instanceof WTPartMaster) {
                        WTPartMaster master = (WTPartMaster) obj;
                        WTPart partTemp = WTPartUtil.getLatestPartByMaster(master);
                        String viewName = partTemp.getViewName();
                        if ("Design".equals(viewName)) {
                            partTemp = CSCPart.getPartByNumberAndViewName(partTemp.getNumber(), "Manufacturing");
                        }
                        getAllChildParts(partList, partTemp, part, productId, docNumberList);
                    }
                }
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }
    }

    private static ConfigSpec getConfigSpec() {
        try {
            return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
        } catch (WTException var1) {
            var1.printStackTrace();
            return null;
        }
    }

    private static Map<String, Element> getZfLink(String zztechnicsNumber, String zztechnicsVerion) {
        DBConnUtil dbConnUtil;
        Map<String, Element> zhufulinkMap = new HashMap<String, Element>();
        try {
            dbConnUtil = new DBConnUtil();
            String sql = "select * from gl_zhufulinkmaster where ZZTECHNICSNUMBER='" + zztechnicsNumber + "' and ZZTECHNICSVERSION='" + zztechnicsVerion + "'";
            ResultSet resultSet = dbConnUtil.executeQuery(sql);
            while (resultSet.next()){
                String zzprocedurebsoid = resultSet.getString("ZZPROCEDUREBSOID");
                String fztechnicsnumber = resultSet.getString("FZTECHNICSNUMBER");
                String fztechnicsversion = resultSet.getString("FZTECHNICSVERSION");
                WTDocument document = WTDocumentUtil.getDocumentByNumberAndVersion(fztechnicsnumber,fztechnicsversion);
                if(document != null){
                    ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
                    byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
                    String technicsDirectory = PropertiesUtil.getTempPath() + File.separator + "pfmeaTemp" + File.separator + document.getNumber();
                    File fileDir = new File(technicsDirectory);
                    if (!fileDir.exists()) {
                        fileDir.mkdirs();
                    }
                    TechnicsZipUtil.unZip(bytes, technicsDirectory, technicsDirectory + File.separator);
                    String xmlPath = technicsDirectory + File.separator + document.getNumber() + ".xml";
                    File xmlFile = new File(xmlPath);
                    if (xmlFile.exists()) {
                        Document doc = XmlUtility.getDocument(xmlFile);
                        Element technicsEle = XmlUtility.getTechnicsElement(doc);
                        zhufulinkMap.put(zzprocedurebsoid,technicsEle);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zhufulinkMap;
    }
}
