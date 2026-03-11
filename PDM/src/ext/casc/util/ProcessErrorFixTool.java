package ext.casc.util;

import com.glaway.mpm.util.*;
import org.dom4j.Document;
import org.dom4j.Element;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.method.RemoteAccess;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class ProcessErrorFixTool implements RemoteAccess {

    public static String  rePreStep(String oid,String docNumber) throws WTException, PropertyVetoException {
        WTDocument doc = null;
        if (!Tools.isNull(oid)) {
            doc = (WTDocument) ReferenceFactory.getObjectbyOid(oid);
        } else if (!Tools.isNull(docNumber)) {
            doc = DocUtil.getDoc(docNumber, false);
        }
        String number = "";
        if (doc == null) {
            return "找不到对应的工艺文件";
        } else {
            WTUser currentuser = null;
            WTUser modifer = (WTUser) doc.getModifier().getPrincipal();
            SessionHelper.manager.setPrincipal(modifer.getAuthenticationName());
            FileInputStream fileInputStream = null;
            boolean enforce = wt.session.SessionServerHelper.manager
                    .setAccessEnforced(false);
            try {
                currentuser = (WTUser) SessionHelper.manager.getPrincipal();
                ApplicationData appData = WTDocumentUtil.getPrimaryByDocument(doc);
                byte[] bytes = WTDocumentUtil.applicationDataToByte(appData);
                String tempFilePath = PropertiesUtil.getTempPath() + File.separator + "fixProcess" + File.separator + UUID.randomUUID() + File.separator;
                FileUtil.writeBytes(tempFilePath, appData.getFileName(), bytes);
                number = doc.getNumber();
                ApacheZipUtil.decompress(tempFilePath + appData.getFileName(), tempFilePath + number);
                File xmlFile = new File(tempFilePath + number + File.separator + number + ".xml");
                Document docXml = XmlUtility.getDocument(xmlFile);
                Element techElement = XmlUtility.getTechnicsElement(docXml);

                List<Element> steps = XmlUtility.getAllSteps(techElement);
                //int procedureNumber = 10;
                //String preBsoId = "";
                //String preStep = "";
                //String nextBsoId = "";
                String preStep = "";
                for (Element step : steps) {
                    XmlUtility.setAttributeValue(step, "preStep", preStep);
                    preStep = XmlUtility.getAttributeValue(step,"stepNumber")+"_"+XmlUtility.getAttributeValue(step,"stepName");
                }
                XmlUtility.saveDocument(docXml, xmlFile);
                boolean compressflag = ApacheZipUtil.compress(tempFilePath + number, tempFilePath + number + ".zip");
                if (compressflag) {
                    fileInputStream = new FileInputStream(new File(tempFilePath + number + ".zip"));
                    WTDocumentUtil.setPrimaryForDocument(doc, number + ".zip", fileInputStream);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "生成异常:"+e.getLocalizedMessage();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (currentuser != null) {
                    try {
                        SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
                    } catch (WTException e) {
                        e.printStackTrace();
                    }

                }

                wt.session.SessionServerHelper.manager
                        .setAccessEnforced(enforce);
            }


        }
        return number+"生成成功";
    }

}
