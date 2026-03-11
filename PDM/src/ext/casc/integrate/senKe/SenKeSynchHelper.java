package ext.casc.integrate.senKe;

import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import com.ptc.wvs.common.ui.Representer;
import com.ptc.wvs.common.ui.VisualizationHelper;
import ext.casc.common.util.CommonValuesUtil;
import ext.casc.fileprint.FilePrintUtil;
import ext.casc.integrate.SynchConfig;
import ext.casc.integrate.util.CloudApiUtil;
import ext.casc.persistence.PersistenceCommonHelper;
import ext.casc.util.DocUtil;
import ext.casc.util.WCUtil;
import ext.casc.version.VersionCommonHelper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.content.*;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.representation.Representation;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

import java.beans.PropertyVetoException;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class SenKeSynchHelper {
    public static String getViewUrl(Object primaryBusinessObject){
        String viewUrl = "<a href=\"" +SynchConfig.SENKE_VIEW_URL+  PersistenceCommonHelper.getOid((Persistable) primaryBusinessObject)
                + FilePrintUtil.HTML_LINK_BLANK_MID
                + "查看数字样机" + FilePrintUtil.HTML_LINK_AFTER;
        return viewUrl;
    }

    public static String sendProcessPlanData(Object primaryBusinessObject){
        String resultMsg = "";
        try {
            WTDocument doc= null;
            if(primaryBusinessObject instanceof WTChangeOrder2){
                QueryResult qResult = ChangeHelper2.service.getChangeablesAfter((WTChangeOrder2)primaryBusinessObject);
                while (qResult.hasMoreElements()) {
                    Object object = qResult.nextElement();
                    if (object instanceof WTDocument) {
                        doc = (WTDocument) object;
                        break;
                    }
                }
            }else if(primaryBusinessObject instanceof WTDocument){
                doc= (WTDocument)primaryBusinessObject;
            }
            String  responseBody = CloudApiUtil.httpReuestBody(SynchConfig.SENKE_SEND_URL,getSendSenKeData(doc));
            if(responseBody.contains("调用失败")){
                resultMsg = responseBody;
            }else{
                try {
                    JSONObject resultJson = new JSONObject(responseBody);
                    String success = resultJson.optString("success");
                    if("true".equals(success)){
                        resultMsg = "";
                    }else{
                        resultMsg = "森科处理失败，错误详情：" + resultJson.optString("message");
                    }
                }catch (JSONException jsonException){
                    jsonException.printStackTrace();
                    resultMsg = "调用成功，返回Json无法解析："+jsonException.getLocalizedMessage();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMsg = "调用失败："+e.getLocalizedMessage();
        }
        return resultMsg;
    }


    public static JSONObject getSendSenKeData(WTDocument doc) throws Exception {
        JSONObject jsonData  = new JSONObject();
        JSONObject processDocData = new JSONObject();
        jsonData.put("processDocData",processDocData);
        processDocData.put("oid",PersistenceCommonHelper.getOid(doc));
        processDocData.put("number",doc.getNumber());
        processDocData.put("name",doc.getName());
        processDocData.put("version", VersionCommonHelper.getVersion(doc));
        processDocData.put("dataUrl",getDataUrl(doc));

        WTPart part = getRelatedPart(doc);
        if(part!=null) {
            processDocData.put("realatedPart",getPartJson(part));
            JSONObject pvs3dData = new JSONObject();
            JSONArray prealatedDrws = new JSONArray();
            JSONArray prealatedDocs = new JSONArray();
            jsonData.put("pvs3dData",pvs3dData);
            jsonData.put("realatedDrws",prealatedDrws);
            jsonData.put("realatedDocs",prealatedDocs);

            WTPart designPart = WTPartUtil.getLatestPartByNumberAndView(part, "Design");
            if(designPart!=null){
                QueryResult queryResult = PartDocServiceCommand.getAssociatedCADDocuments(designPart);
                EPMDocument parentEpm = null;
                while (queryResult.hasMoreElements()) {
                    Object object = queryResult.nextElement();
                    if (object instanceof EPMDocument) {
                        EPMDocument epmDocument = (EPMDocument) object;
                        String docType = epmDocument.getDocType().toString();
                        if(!"CADDRAWING".equals(docType) &&!epmDocument.getCADName().toLowerCase().endsWith("drw")){
                            pvs3dData.put("oid",PersistenceCommonHelper.getOid(epmDocument));
                            pvs3dData.put("number",epmDocument.getNumber());
                            pvs3dData.put("name",epmDocument.getName());
                            pvs3dData.put("cadName",epmDocument.getCADName());
                            pvs3dData.put("version", VersionCommonHelper.getVersion(epmDocument));
                            pvs3dData.put("dataUrl",getPvsZipUrl(epmDocument));

                            JSONObject realatedDegignPart = new JSONObject();
                            realatedDegignPart.put("oid", PersistenceCommonHelper.getOid(designPart));
                            realatedDegignPart.put("number", designPart.getNumber());
                            realatedDegignPart.put("name", designPart.getName());
                            realatedDegignPart.put("version",VersionCommonHelper.getVersion(designPart));
                            pvs3dData.put("realatedPart",realatedDegignPart);

                            parentEpm = epmDocument;

                            Set<EPMDocument> cad2ds =  WCUtil.get2DesignDocs(epmDocument);
                            for(EPMDocument cad2d :cad2ds){
                                prealatedDrws.put(getDrwJson(cad2d));
                            }

                        }
                    }
                }

                String docNumber = designPart.getNumber()+".DWG";
                WTDocument dwgDocument = DocUtil.getDoc(docNumber,false);
                if(dwgDocument!=null){
                    prealatedDocs.put(getDocJson(dwgDocument));
                }

                JSONArray children = new JSONArray();
                jsonData.put("children",children);
                if(parentEpm!=null){
                    HashSet<String> hasContain = new HashSet<String>();
                    QueryResult qr = EPMStructureHelper.service.navigateUsesToIteration(parentEpm, null, true,
                            new LatestConfigSpec());
                    while (qr.hasMoreElements()) {
                        EPMDocument childEpm = (EPMDocument) qr.nextElement();
                        if(!hasContain.contains(childEpm.getNumber())){
                            JSONObject childJson = new JSONObject();
                            childJson.put("oid",PersistenceCommonHelper.getOid(childEpm));
                            childJson.put("number",childEpm.getNumber());
                            childJson.put("name",childEpm.getName());
                            childJson.put("cadName",childEpm.getCADName());
                            childJson.put("version", VersionCommonHelper.getVersion(childEpm));
                            childJson.put("location", "1");
                            //childJson.put("dataUrl",getPvsZipUrl(childEpm));

                            JSONArray realatedDrws = new JSONArray();
                            childJson.put("realatedDrws", realatedDrws);

                            JSONArray realatedDocs = new JSONArray();
                            childJson.put("realatedDocs", realatedDocs);


                            Set<EPMDocument> cad2ds =  WCUtil.get2DesignDocs(childEpm);
                            for(EPMDocument cad2d :cad2ds){
                                realatedDrws.put(getDrwJson(cad2d));
                            }


                            QueryResult partQr = PersistenceHelper.manager.navigate(childEpm,
                                    EPMBuildRule.BUILD_TARGET_ROLE, EPMBuildRule.class,
                                    true);
                            if (partQr.hasMoreElements()) {
                                WTPart childPart = (WTPart) partQr.nextElement();
                                childJson.put("realatedPart", getPartJson(childPart));

                                String tmpDocNumber = childPart.getNumber()+".DWG";
                                WTDocument tmpDwgDocument = DocUtil.getDoc(tmpDocNumber,false);
                                if(tmpDwgDocument!=null){
                                    realatedDocs.put(getDocJson(tmpDwgDocument));
                                }
                            }

                            children.put(childJson);
                        }
                        hasContain.add(childEpm.getNumber());
                    }
                }
            }
        }
        System.out.println("### SenKe Json :"+jsonData);
        return jsonData;
    }

    public static JSONObject getDrwJson(EPMDocument cad2d) throws Exception {
        JSONObject pvs2dData1 = new JSONObject();
        pvs2dData1.put("oid",PersistenceCommonHelper.getOid(cad2d));
        pvs2dData1.put("number",cad2d.getNumber());
        pvs2dData1.put("name",cad2d.getName());
        pvs2dData1.put("cadName",cad2d.getCADName());
        pvs2dData1.put("state",cad2d.getState().getState().getDisplay(Locale.CHINA));
        pvs2dData1.put("version", VersionCommonHelper.getVersion(cad2d));
        pvs2dData1.put("dataUrl",getDataUrl(cad2d));
        pvs2dData1.put("printDataUrl",getDataUrl2(cad2d));
        return pvs2dData1;
    }

    public static JSONObject getDocJson(WTDocument document) throws Exception {
        JSONObject pvs2dData1 = new JSONObject();
        pvs2dData1.put("oid",PersistenceCommonHelper.getOid(document));
        pvs2dData1.put("number",document.getNumber());
        pvs2dData1.put("name",document.getName());
        pvs2dData1.put("cadName","");
        pvs2dData1.put("state",document.getState().getState().getDisplay(Locale.CHINA));
        pvs2dData1.put("version", VersionCommonHelper.getVersion(document));
        pvs2dData1.put("dataUrl",getDataUrl(document));
        pvs2dData1.put("printDataUrl",getDataUrl2(document));
        return pvs2dData1;
    }
    public  static JSONObject getPartJson(WTPart realatedPart) {
        JSONObject partJson = new JSONObject();
        partJson.put("oid", PersistenceCommonHelper.getOid(realatedPart));
        partJson.put("number", realatedPart.getNumber());
        partJson.put("name", realatedPart.getName());
        partJson.put("version",VersionCommonHelper.getVersion(realatedPart));
        partJson.put("view",realatedPart.getViewName());
        return partJson;
    }
    public  static JSONObject getDataUrl(ContentHolder contentHolder) throws Exception {
        JSONObject result = new JSONObject();
        QueryResult qr = wt.content.ContentHelper.service.getContentsByRole(contentHolder, ContentRoleType.PRIMARY);
        ApplicationData appData = null;
        if (qr.hasMoreElements()) {
            appData = (ApplicationData) qr.nextElement();
            String fileName = appData.getFileName();
            if(contentHolder instanceof EPMDocument){
                fileName = ((EPMDocument) contentHolder).getCADName();
            }
            result.put("fileName",fileName);
            URL localUrl = wt.content.ContentHelper.service.getDownloadURL(contentHolder, appData);
            try {
                File toFile = dwonloadContentFileToDirectory(contentHolder,appData,  CommonValuesUtil.IXBEXPIMP_FOLDER);
                if(toFile!=null){
                    result.put("downloadUrl", CommonValuesUtil.HOST_URL + URLEncoder.encode(toFile.getName(),"UTF-8"));
                }else{
                    result.put("downloadUrl", localUrl.toString());
                }
            } catch (Exception e) {
                result.put("downloadUrl", localUrl.toString());
                e.printStackTrace();
            }
        }
        return result;
    }
    public  static JSONObject  getDataUrl2(ContentHolder contentHolder) throws Exception {
        return getDataUrl2(contentHolder,true);
    }
    public  static JSONObject getDataUrl2(ContentHolder contentHolder, boolean isTmp) throws Exception {
        JSONObject result = new JSONObject();
        QueryResult qr = wt.content.ContentHelper.service.getContentsByRole(contentHolder, ContentRoleType.SECONDARY);
        while (qr.hasMoreElements()) {
            ApplicationData applicationdata = (ApplicationData) qr.nextElement();
            if (applicationdata.getFileName().toUpperCase().startsWith("PRINT")||applicationdata.getFileName().toUpperCase().startsWith("SIGN")) {
                result.put("fileName",applicationdata.getFileName());
                URL localUrl = wt.content.ContentHelper.service.getDownloadURL(contentHolder, applicationdata);
               if(isTmp){
                   try {
                       File toFile = dwonloadContentFileToDirectory(contentHolder,applicationdata, CommonValuesUtil.IXBEXPIMP_FOLDER);
                       if(toFile!=null){
                           result.put("downloadUrl",CommonValuesUtil.HOST_URL +  URLEncoder.encode(toFile.getName(),"UTF-8"));
                       }else{
                           result.put("downloadUrl",localUrl.toString());
                       }
                   } catch (Exception e) {
                       result.put("downloadUrl",localUrl.toString());
                       e.printStackTrace();
                   }
               }else{
                   result.put("downloadUrl",localUrl.toString());
                   return result;
               }

            }
        }
        return result;
    }


    public static File dwonloadContentFileToDirectory(ContentHolder contentHolder,ApplicationData applicationData,  String toPath) throws Exception {
        File file = null;
        InputStream is = ContentServerHelper.service.findContentStream(applicationData);
        FileOutputStream fos = null;
        try {
            String appFileName = applicationData.getFileName();
            if("{$CAD_NAME}".equals(appFileName) &&contentHolder instanceof  EPMDocument){
                EPMDocument epmDocument = (EPMDocument) contentHolder;
                appFileName = epmDocument.getCADName();
            }
            fos = new FileOutputStream(toPath + appFileName);
            int i = 0;
            byte abyte[] = new byte[8192];
            while ((i = is.read(abyte, 0, abyte.length)) >= 0) {
                fos.write(abyte, 0, i);
            }
            file = new File(toPath + appFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
                if (null != fos) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }



    public static JSONObject getPvsZipUrl(EPMDocument contentHolder) throws WTException {
        JSONObject result = new JSONObject();
        VisualizationHelper helper = new VisualizationHelper();
        QueryResult qr = helper.getRepresentations(contentHolder);
        while (qr.hasMoreElements()) {
            Representation rep = (Representation) qr.nextElement();
            new Representer().saveAsZIPFile(getRefFromObject(rep), false, true, CommonValuesUtil.IXBEXPIMP_FOLDER+contentHolder.getNumber()+".pvz");
            result.put("fileName",contentHolder.getNumber()+".pvz");
            result.put("downloadUrl",CommonValuesUtil.HOST_URL+contentHolder.getNumber()+".pvz");
            return result;
        }
        return result;
    }

    public static WTPart getRelatedPart(WTDocument doc) throws WTException {
        QueryResult qr = PartDocServiceCommand.getAssociatedDescParts(doc);
        if(qr.hasMoreElements()) {
            WTPart part = (WTPart) qr.nextElement();
            return  part;
        }
        return null;
    }

    public static String getRefFromObject(Persistable persistable) {
        try {
            wt.fc.ReferenceFactory referencefactory = new wt.fc.ReferenceFactory();
            return referencefactory.getReferenceString(ObjectReference.newObjectReference(persistable.getPersistInfo()
                    .getObjectIdentifier()));
        } catch (Exception exception) {
        }
        return null;
    }
}
