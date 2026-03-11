package ext.casc.changeDept;

import com.glaway.mpm.util.*;
import com.ptc.core.meta.common.TypeIdentifier;
import ext.casc.constants.PDMConfig;
import ext.casc.doc.CSCDoc;
import ext.casc.util.*;
import ext.casc.util.IBAHelper;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.StringDefinition;
import wt.iba.definition._AttributeHierarchyChild;
import wt.iba.value._StringValue;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pom.Transaction;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTStandardDateFormat;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

public class ChangeProcessDeptUtil2 implements RemoteAccess {
    public static void main(String[] args) throws WTException {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        rms.setUserName("wcadmin");
        rms.setPassword(PDMConfig.WCADMIN_PASSWORD);
        if(excute(args[0]) ) {
            System.out.println("全部转换完成！");
        }else {
            System.out.println("转换未完成，请明天继续！");
        }

    }
    public static boolean excute(String etype) throws WTException {

        if (!RemoteMethodServer.ServerFlag) {
            Class[] argTypes = {String.class};
            Object[] args = {etype};
            try {
                SessionHelper.manager.setPrincipal("administrator");
                RemoteMethodServer server = RemoteMethodServer.getDefault();
                server.setUserName("wcadmin");
                //server.setPassword("Admin@149");
                server.setPassword(PDMConfig.WCADMIN_PASSWORD);
                return  (Boolean) server.invoke("excute", ChangeProcessDeptUtil2.class.getName(), null, argTypes, args);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                return false;
            }
        }else{
            boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
            try {

                WTProperties wtProperties = WTProperties.getLocalProperties();
                String codebasePath = wtProperties.getProperty("wt.codebase.location");
                String filePath = codebasePath + File.separator + "ext"
                        + File.separator + "casc"
                        + File.separator + "conf" + File.separator + "config_149.xml";
                // FileInputStream inputStream = new FileInputStream(new File(filePath));
                SAXReader reader = new SAXReader();
                Document dmdocument = reader.read(new File(filePath));
                org.dom4j.Element rootElement = dmdocument.getRootElement();
                org.dom4j.Element changeDeptConfig  = rootElement.element("ChangeProcessDeptConfig");
                List<org.dom4j.Element> configs = changeDeptConfig.elements("convertMap");


                for(org.dom4j.Element e:configs) {
                    String srcDept = e.attributeValue("fromValue");
                    String destDept = e.attributeValue("toValue");
                    System.out.println("转换配置:" + srcDept + "---->" + destDept);
                    if (Tools.isNull(srcDept) || Tools.isNull(destDept)) {
                         continue;
                    }
                    Set<String> flags = new HashSet<String>();

                    if ("ALL".equals(etype)) {

                        DBConnUtil util = new DBConnUtil();
                        ResultSet rs = null;
                        try{
                            String sql = "select BIANHAO from tmp_processNumber where srcDept = '"+srcDept+"'";
                            rs = util.executeQuery(sql);
                            String number = "";
                            while (rs.next()) {
                                number = rs.getString("BIANHAO");
                                flags.add(number);
                            }
                        }finally{
                            util.close();
                            if(rs!=null){
                                if(!rs.isClosed()){
                                    rs.close();
                                }
                            }
                        }

                        ArrayList<TypeIdentifier> list = SoftTypeUtil.getChildTypes("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", null);
                        for (TypeIdentifier ti : list) {
                            SoftTypeUtil.getTypeIdentifierDefinition(ti).getDisplay();
                            String type = ti.toString().substring(7);
                            TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(type);
                            long typeId = 0;
                            if (tdr != null) {
                                typeId = tdr.getKey().getBranchId();
                            }

                            QuerySpec qs = new QuerySpec();
                            qs.setAdvancedQueryEnabled(true);
                            int ibaHolderIndex = qs.appendClassList(WTDocument.class, true);

                            qs.appendWhere(new SearchCondition(WTDocument.class,
                                            "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeId),
                                    new int[]{0});
                            //qs.appendAnd();
                            //qs.appendWhere(new SearchCondition(WTDocument.class,WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] { 0 });
                            qs.appendAnd();
                            qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.EQUAL, "APPROVED", true));

                          /*  qs.appendAnd();
                            Date dateFrom1 = WTStandardDateFormat.parse("2022-08-28", "yyyy-MM-dd");
                            qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.MODIFY_TIMESTAMP, SearchCondition.LESS_THAN, new Timestamp(dateFrom1.getTime())));*/
                       /* if (containerId != null) {
                            qs.appendAnd();
                            qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.CONTAINER_ID, SearchCondition.EQUAL, containerId), new int[]{ibaHolderIndex});
                        } else {
                            return;
                        }*/

                          /* if (srcDept != null && !"".equals(srcDept)) {
                                qs.appendAnd();
                                int ibaStringValueIndex = qs.appendClassList(wt.iba.value.StringValue.class, false);
                                int ibaStringDefinitionIndex = qs.appendClassList(StringDefinition.class, false);
                                // Latest Iteration
                                SearchCondition scLatestIteration = new SearchCondition(WTDocument.class, WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
                                // String Value With IBA Holder
                                SearchCondition scJoinStringValueIBAHolder = new SearchCondition(wt.iba.value.StringValue.class, "theIBAHolderReference.key.id", WTDocument.class, WTAttributeNameIfc.ID_NAME);
                                // String Value With Definition
                                SearchCondition scJoinStringValueStringDefinition = new SearchCondition(wt.iba.value.StringValue.class, "definitionReference.key.id", StringDefinition.class,
                                        WTAttributeNameIfc.ID_NAME);
                                qs.appendWhere(scLatestIteration, ibaHolderIndex);
                                qs.appendAnd();
                                qs.appendWhere(scJoinStringValueIBAHolder, ibaStringValueIndex, ibaHolderIndex);
                                qs.appendAnd();
                                qs.appendWhere(scJoinStringValueStringDefinition, ibaStringValueIndex, ibaStringDefinitionIndex);
                                SearchCondition scStringDefinitionName1 = new SearchCondition(StringDefinition.class, _AttributeHierarchyChild.NAME, SearchCondition.EQUAL, "CAPPVER");
                                SearchCondition scStringValueValue1 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE, SearchCondition.NOT_EQUAL, "1");
                                qs.appendAnd();
                                qs.appendWhere(scStringDefinitionName1, ibaStringDefinitionIndex);
                                qs.appendAnd();
                                qs.appendWhere(scStringValueValue1, ibaStringValueIndex);
                            }*/

                            //qs = new LatestConfigSpec().appendSearchCriteria(qs);
                            QueryResult qr = PersistenceHelper.manager.find(qs);
                            GLLogger.debug(ChangeProcessDeptUtil2.class, "已检索到工艺数量:"+qr.size());


                            while (qr.hasMoreElements()) {
                                Object[] obj = (Object[]) qr.nextElement();
                                WTDocument document = (WTDocument) obj[0];
                                QueryResult qr2 = VersionControlHelper.service.allVersionsOf(document.getMaster());
                                if (qr2.hasMoreElements()) {
                                    document = (WTDocument) qr2.nextElement();
                                }
                                if (flags.contains(document.getNumber())) {
                                    continue;
                                }
                                boolean checkedOut = WorkInProgressHelper.isCheckedOut((Workable) document);
                                if (!checkedOut) {
                                    if (!flags.contains(document.getNumber())) {
                                        GLLogger.debug(ChangeProcessDeptUtil2.class, "是否转换工艺:" + document.getNumber() + " " + document.getVersionIdentifier().getValue());
                                        if (replaceProcessXML(document, srcDept, destDept)) {
                                            GLLogger.debug(ChangeProcessDeptUtil2.class, "确认转换工艺:" + document.getNumber() + " " + document.getVersionIdentifier().getValue());
                                            document = (WTDocument) PersistenceHelper.manager.refresh(document);
                                            String oldDept = IBAHelper.getIBAStringValue(document, "DEPT");
                                            if (srcDept.equals(oldDept)) {
                                                IBAUtility iba = new IBAUtility(document);
                                                iba.setIBAValue("DEPT", destDept);
                                                document = (WTDocument) iba.updateAttributeContainer(document);
                                                iba.updateIBAHolder(document);
                                            }

                                        }
                                        /*IBAUtility iba = new IBAUtility(document);
                                        iba.setIBAValue("CAPPVER", "1");
                                        document = (WTDocument) iba.updateAttributeContainer(document);
                                        iba.updateIBAHolder(document);*/
                                        DBConnUtil dbConnUtil = new DBConnUtil();
                                        try {
                                            dbConnUtil.executeUpdate("insert into TMP_PROCESSNUMBER (BIANHAO,SRCDEPT) values ('" + document.getNumber() + "','" + srcDept + "')");
                                            dbConnUtil.commit();
                                        }catch(Exception ee) {
                                            ee.printStackTrace();
                                        }finally {
                                            dbConnUtil.close();
                                        }
                                    }
                                }
                            }
                        }
                    }else{
                        WTDocument document = CSCDoc.getDoc(etype);
                        if(document!=null){
                            GLLogger.debug(ChangeProcessDeptUtil2.class, "是否转换工艺:" + document.getNumber() + " " + document.getVersionIdentifier().getValue());
                            if (replaceProcessXML(document, srcDept, destDept)) {
                                GLLogger.debug(ChangeProcessDeptUtil2.class, "确认转换工艺:" + document.getNumber() + " " + document.getVersionIdentifier().getValue());
                                document = (WTDocument) PersistenceHelper.manager.refresh(document);
                                String oldDept = IBAHelper.getIBAStringValue(document, "DEPT");
                                if (srcDept.equals(oldDept)) {
                                    IBAUtility iba = new IBAUtility(document);
                                    iba.setIBAValue("DEPT", destDept);
                                    document = (WTDocument) iba.updateAttributeContainer(document);
                                    iba.updateIBAHolder(document);
                                }

                            }
                           /* IBAUtility iba = new IBAUtility(document);
                            iba.setIBAValue("CAPPVER", "1");
                            document = (WTDocument) iba.updateAttributeContainer(document);
                            iba.updateIBAHolder(document);*/
                        }
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            } finally {
                SessionServerHelper.manager.setAccessEnforced(enforce);
            }
            return  true;
        }


    }



    private static boolean  replaceProcessXML(
            WTDocument doc,String srcDept,String destDept) {
        Transaction trans = new Transaction();
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        FileInputStream fileInputStream = null;
        String deleteFile1 = null;
        try{
            trans.start();

            TypeIdentifier identifier = TypedUtility.getTypeIdentifier(doc);
            String typename = identifier.getTypename();
            String flag = "";
            if ("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN|casc.sast.149.reportTechnics".equals(typename)) {
                flag ="baobiao";
            }
            String tempFilePath = PropertiesUtil.getWTHome() + File.separator + "temp" + File.separator
                    + java.util.UUID.randomUUID().toString() + File.separator;
            String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(doc, tempFilePath);
            deleteFile1 = tempFilePath;
            if(zipFileName==null||!zipFileName.endsWith(".zip")) return false;

            String subFileName = zipFileName.substring(0, zipFileName.lastIndexOf("."));
            ApacheZipUtil.decompress(tempFilePath + zipFileName, tempFilePath + subFileName);
            File xmlFile = new File(tempFilePath + subFileName + File.separator + subFileName + ".xml");
            if(!xmlFile.exists()) return false;

            inputStream = new FileInputStream(xmlFile);
            SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
            Element rootElement = xmlUtil.getRootElement();

            boolean isNeedSave = false;

            if ("baobiao".equals(flag)) {
                if( rootElement.getChildren("XWReportTechnicsInfo").size()>0) {
                    Element techEle = (Element) rootElement.getChildren("XWReportTechnicsInfo").get(0);
                    if(techEle==null){
                        return false;
                    }
                    String oldDept = techEle.getAttribute("DEPT").getValue();
                    if (srcDept.equals(oldDept)) {
                        techEle.setAttribute("DEPT", destDept);
                    }
                }

            } else {
                if( rootElement.getChildren("QMFawTechnicsInfo").size()==0) {
                    return false;
                }
                Element techEle = (Element) rootElement.getChildren("QMFawTechnicsInfo").get(0);
                String oldDept = techEle.getAttributeValue("DEPT");
                if(srcDept.equals(oldDept)) {
                    techEle.setAttribute("DEPT", destDept);
                    isNeedSave = true;
                }


                Element elIBAAttibutes = techEle.getChild("IBAAttibutes");
                if(elIBAAttibutes!=null){
                    List<Element> attributes = elIBAAttibutes.getChildren("attribute");
                    for(Element attribute:attributes){
                        if("部门".equals(attribute.getAttributeValue("key"))){
                            String value = attribute.getAttributeValue("value");
                            if(srcDept.equals(value)){
                                attribute.setAttribute("value", destDept);
                            }
                        }
                    }
                }
                List<Element> elQMProcedureInfos =  techEle.getChild("steps").getChildren("QMProcedureInfo");
                for(Element elQMProcedureInfo:elQMProcedureInfos){
                    String workShop = elQMProcedureInfo.getAttributeValue("workShop");
                    if(srcDept.equals(workShop)){
                        elQMProcedureInfo.setAttribute("workShop", destDept);
                        isNeedSave = true;
                    }
                }

            }
            // 替换工艺xml，打包工艺文件夹，上传工艺压缩包
            if(isNeedSave) {
                fileOutputStream = new FileOutputStream(new File(tempFilePath + subFileName + File.separator + subFileName
                        + ".xml"), false);
                Format format = Format.getPrettyFormat();
                format.setEncoding("GBK");
                XMLOutputter xmlOutput = new XMLOutputter(format);
                xmlOutput.output(xmlUtil.getDocument(), fileOutputStream);

                boolean compressflag = ApacheZipUtil.compress(tempFilePath + subFileName, tempFilePath + zipFileName);
                // 重新上传PBOM的XML

                if (compressflag) {
                /*WTDocumentUtil.setPrimaryForDocument(doc, zipFileName, new FileInputStream(new File(tempFilePath
                        + zipFileName)));*/
                    fileInputStream = new FileInputStream(tempFilePath
                            + zipFileName);
                    ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
                    data = ContentServerHelper.service.updateContent((ContentHolder) doc, data, fileInputStream); // 更新内容

                }
                trans.commit();
                trans = null;
                return true;
            }else{
                return false;
            }
        }catch (Exception e){

            e.printStackTrace();
            return false;
        }finally{
            if (trans != null) {
                trans.rollback();
            }
            try {
                ApacheZipUtil.closeOutputStream(fileOutputStream);
                ApacheZipUtil.closeInputStream(inputStream);
                ApacheZipUtil.closeInputStream(fileInputStream);
                if(deleteFile1!=null){
                    FileUtil.deleteFile(new File(deleteFile1));
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
