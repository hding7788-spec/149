package ext.casc.integrate.mes;

import com.glaway.mpm.util.*;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import ext.casc.constants.Constants;
import ext.casc.doc.CSCDoc;
import ext.casc.util.IBAHelper;
import ext.casc.util.Tools;
import ext.casc.workflow.WorkflowHelper;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.*;
import wt.fc.collections.WTCollection;
import wt.folder.Folder;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

import java.beans.PropertyVetoException;
import java.io.*;
import java.util.*;

public class ProcessInfoService {
    private static PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.QIAN_LONG_CONFIG_PATH);
    public static String KEY1="字段";
    public static String KEY2="唯一编码";

    public static String KEY3_END="_下限";
    public static String KEY4_END="_上限";
    public static String KEY5_END="_上偏差";
    public static String KEY6_END="_下偏差";
    public static String KEY7_END="_公称值";
    public static String KEY8_END="_规定值";
    public static String KEY9_END="_工序";
    public static String KEY10_END="_工序ID";
    public static String KEY11_END="_工序stepNumber";
    public static String KEY12_END="_工步";
    public static String KEY13_END="_工步ID";
    public static String KEY14_END="_工步stepNumber";
    public String getAllProcessPlanVersion(String technicsNumber) throws WTException {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<technics>");
        List<WTDocument> allVersionWTDocument = getAllVersionWTDocument(technicsNumber);
        for (WTDocument wtDocument : allVersionWTDocument) {
            String docNumber = wtDocument.getNumber();
            String docVersion = wtDocument.getIterationDisplayIdentifier().toString();
            String ecnNumber = "";
            WTCollection coll2 = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(wtDocument);
            Iterator it2 = coll2.iterator();
            if (it2.hasNext()) {
                WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it2.next()).getObject();
                if(ecn != null){
                    ecnNumber = ecn.getNumber();
                }
            }

            stringBuffer.append("<technic number=\"" + docNumber + "\" version=\"" + docVersion + "\" ecnNumber=\""+ecnNumber+"\">");
            stringBuffer.append("</technic>");
        }
        stringBuffer.append("</technics>");

        return stringBuffer.toString();
    }

    public String getProcessFileUrl(String number, String batch, String version) {
        WTDocument doc = null;

        if("APPROVED".equals(version)){//产保
            doc = CSCDoc.getDoc(number);
            if(doc!=null){
                try {
                    String state = doc.getState().getState().getDisplay(Locale.CHINA);
                    if (Constants.STATE_YIPIZHUN.equals(state)) {
                        WTProperties props = WTProperties.getLocalProperties();
                        String tempFolder = props.getProperty("wt.temp");
                        String tempFilePath = tempFolder + File.separator + "IXBExpImp" + File.separator;
                        String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(doc, tempFilePath);
                        return zipFileName;
                    } else {
                        QueryResult qr =  VersionControlHelper.service.allVersionsOf(doc.getMaster());
                        while(qr.hasMoreElements()){
                            WTDocument tempDoc = (WTDocument) qr.nextElement();
                            if (Constants.STATE_YIPIZHUN.equals( tempDoc.getState().getState().getDisplay(Locale.CHINA))) {
                                WTProperties props = WTProperties.getLocalProperties();
                                String tempFolder = props.getProperty("wt.temp");
                                String tempFilePath = tempFolder + File.separator + "IXBExpImp" + File.separator;
                                String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(tempDoc, tempFilePath);
                                return zipFileName;
                            }
                        }
                    }
                    return "工艺文件" + number + "未受控！";

                } catch (WTException e) {
                    e.printStackTrace();
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }else{
            if (version == null || "".equals(version)) {
                doc = CSCDoc.getDoc(number);
            } else {
                doc = CSCDoc.getLatestDocByNumberAndVersion(number, version);
            }
        }



        if (doc != null) {
            try {
                String state = doc.getState().getState().getDisplay(Locale.CHINA);
                if (state.contains(Constants.STATE_YIPIZHUN)) {
                    WTProperties props = WTProperties.getLocalProperties();
                    String tempFolder = props.getProperty("wt.temp");
                    String tempFilePath = tempFolder + File.separator + "IXBExpImp" + File.separator;
                    String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(doc, tempFilePath);
                    String baiyuChangeZip = genBaiYuMesZip(zipFileName,tempFilePath,doc);
                    if(baiyuChangeZip!=null){
                        return baiyuChangeZip;
                    }
                    return zipFileName;
                } else {
                    return "工艺文件" + number + "(" + version + ")未受控！";
                }

            } catch (WTException e) {
                e.printStackTrace();
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            return "不存在编号为" + number + "的工艺文件";
        }

        return "";
    }

    public String getProcessFileUrl(String number, String batch, String version,String from) {
        WTDocument doc = null;
        if (version == null || "".equals(version)) {
            doc = CSCDoc.getDoc(number);
        } else {
            doc = CSCDoc.getLatestDocByNumberAndVersion(number, version);
        }
        if (doc != null) {
            try {
                String state = doc.getState().getState().getDisplay(Locale.CHINA);
                if (state.contains(Constants.STATE_YIPIZHUN)) {
                    WTProperties props = WTProperties.getLocalProperties();
                    String tempFolder = props.getProperty("wt.temp");
                    String tempFilePath = tempFolder + File.separator + "IXBExpImp" + File.separator;
                    String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(doc, tempFilePath);
                    // URLFactory urlfactory = new URLFactory();
                    // String baseHREF = urlfactory.getBaseHREF();
                    // baseHREF = baseHREF.replaceAll("/Windchill", "");
                    return zipFileName;
                } else {
                    return "工艺文件" + number + "(" + version + ")未受控！";
                }

            } catch (WTException e) {
                e.printStackTrace();
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            return "不存在编号为" + number + "的工艺文件";
        }

        return "";
    }

    private String genBaiYuMesZip(String zipFileName, String tempFilePath, WTDocument doc) {

        InputStream inputStream = null;
        FileOutputStream fileoutputStream=null;
        StringBuilder returnMsg = new StringBuilder("");
        try{
            String subFileName = zipFileName.substring(0, zipFileName.lastIndexOf("."));
            ApacheZipUtil.decompress(tempFilePath + zipFileName, tempFilePath + subFileName);
            File xmlFile = new File(tempFilePath + subFileName + File.separator + subFileName + ".xml");
            inputStream = new FileInputStream(xmlFile);
            SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
            Element rootElement = xmlUtil.getRootElement();
            GLLogger.debug(this, "--rootElement--" + rootElement.getName());
            Element QMFawTechnicsInfo = rootElement.getChild("QMFawTechnicsInfo");
            //工艺端
            Element tecSchemaData = QMFawTechnicsInfo.getChild("schemaData");
            String msg = dealBaiyuRecord4Mes(QMFawTechnicsInfo, tecSchemaData, tempFilePath, subFileName,doc,true);
            if(!"sucess".equals(msg)){
                returnMsg.append(msg).append(";");
            }
            //工步端
            Element steps = QMFawTechnicsInfo.getChild("steps");
            List<Element> proceduresList = steps.getChildren("QMProcedureInfo");
            for (Element procedure : proceduresList) {
                Element paces = procedure.getChild("paces");
                List<Element> paceList = paces.getChildren("QMProcedureInfo");
                for (Element pace : paceList) {
                    Element schemaData = pace.getChild("schemaData");
                    msg = dealBaiyuRecord4Mes(pace, schemaData, tempFilePath, subFileName,doc,false);
                    if(!"sucess".equals(msg)){
                        returnMsg.append(msg).append(";");
                    }
                }

            }
            if(returnMsg.length()==0){
                fileoutputStream = new FileOutputStream(new File(tempFilePath + subFileName + File.separator + subFileName + ".xml"), false);
                Format format = Format.getPrettyFormat();
                format.setEncoding("GBK");
                XMLOutputter xmlOutput = new XMLOutputter(format);
                xmlOutput.output(xmlUtil.getDocument(), fileoutputStream);
                boolean compressflag = ApacheZipUtil.compress(tempFilePath + subFileName, tempFilePath + subFileName+".zip");

                if(compressflag){
                    return subFileName+".zip";
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileoutputStream != null) {
                try {
                    fileoutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnMsg.toString();

    }

    private void downloadBaiYuFiles(WTDocument doc, String baiyuPath) throws WTException {
        QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.SECONDARY);
        //String stepNum = IBAHelper.getIBAStringValue(doc,"stepNum");
        //String paceNum = IBAHelper.getIBAStringValue(doc,"paceNum");
        //String tableName = doc.getName();
        String baiyuFilesPath = baiyuPath+File.separator+doc.getNumber();
        while (qr.hasMoreElements()) {
            ApplicationData data = (ApplicationData) qr.nextElement();
            InputStream is = ContentServerHelper.service.findContentStream(data);
            String appFileName = data.getFileName();
            File file = new File(baiyuFilesPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(new File(baiyuFilesPath + File.separatorChar + appFileName));
                int i = 0;
                byte abyte[] = new byte[8192];
                while ((i = is.read(abyte, 0, abyte.length)) >= 0) {
                    fos.write(abyte, 0, i);
                }
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

        }
    }

    private JSONObject getJsonData(WTDocument doc) throws WTException {
        QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.SECONDARY);
        while (qr.hasMoreElements()) {
            ApplicationData appData = (ApplicationData) qr.nextElement();
            String fileName = appData.getFileName();
            if("tableData.json".equals(fileName)){
                byte[] bytes = WTDocumentUtil.applicationDataToByte(appData);
                String jsonString = null;
                try {
                    jsonString = new String(bytes, "UTF-8");
                    return new JSONObject(jsonString);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public static List<WTDocument> getAllVersionWTDocument(String documentNumber) throws WTException {

        List<WTDocument> documentList = new ArrayList<WTDocument>();
        WTDocument document;
        QuerySpec qs = new QuerySpec(WTDocument.class);
        SearchCondition temp = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, documentNumber.toUpperCase());
        qs.appendSearchCondition(temp);
        qs.appendAnd();
        SearchCondition latest = VersionControlHelper.getSearchCondition(WTDocument.class, true);
        qs.appendSearchCondition(latest);
        // qr里存储的是每个大版本的最新小版本,按顺序存放如：A.2,B.1,C.4
        // 如果要取得C.4，则需要while循环到最后
        QueryResult qr = PersistenceHelper.manager.find(qs);
        while (qr.hasMoreElements()) {
            document = (WTDocument) qr.nextElement();
            if ("APPROVED".equals(document.getState().toString())) {
                documentList.add(document);

            }
        }
        return documentList;
    }

    public static Map<String,List<WTDocument>> getBaiYuDoc(String technicsNumber){
        Map<String,List<WTDocument>> resultMap = new HashMap<String, List<WTDocument>>();
        try {
            int index[] = { 0 };
            QuerySpec qs = new QuerySpec(WTDocument.class);
            TypeUtil.getTypeQuery(WTDocument.class, "casc.sast.149.BaiYuDocument", qs);
            ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
            qs.setAdvancedQueryEnabled(true);
            String folderName = propertiesUtil.getProperty("baiyu-document-save-folder");
            String containerName = propertiesUtil.getProperty("baiyu-document-save-container");
            WTContainer container = WTContainerUtil.getContainerByName(containerName);
            Folder folder = FolderUtil.getFolder(folderName, WTContainerRef.newWTContainerRef(container));
            if(folder != null){
                qs.appendAnd();
                qs.appendWhere(new SearchCondition(WTDocument.class, "folderingInfo.parentFolder.key.id", SearchCondition.EQUAL,PersistenceHelper.getObjectIdentifier(folder).getId()), index);
            }
            if (technicsNumber != null && !"".equals(technicsNumber)) {
                qs.appendAnd();
                qs.appendOpenParen();

                AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("PPNUMBER");
                if (addv == null)
                    throw new IBADefinitionException("No IBA Definition: " + "PPNUMBER");
                long ibaDefId = addv.getObjectID().getId();
                QuerySpec qs2 = new QuerySpec();
                int idx = qs2.appendClassList(StringValue.class, false);
                qs2.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx }, false);
                qs2.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId), new int[] { idx });
                qs2.appendAnd();
                qs2.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.EQUAL, technicsNumber, true), new int[] { idx });

                SubSelectExpression stringIBAQuery = new SubSelectExpression(qs2);
                qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, stringIBAQuery), index);
                qs.appendCloseParen();

            }
            QueryResult qr = PersistenceHelper.manager.find(qs);
            LatestConfigSpec lcs = new LatestConfigSpec();
            qr = lcs.process(qr);
            while (qr.hasMoreElements()) {
                WTDocument doc = (WTDocument) qr.nextElement();
                if(doc!=null){
                    String stepNum = IBAHelper.getIBAStringValue(doc,"stepNum");
                    String paceNum =IBAHelper.getIBAStringValue(doc,"paceNum");
                    List<WTDocument> docList =  resultMap.get(stepNum+"->"+paceNum);
                    if(docList!=null){
                        docList.add(doc);
                    }else{
                        docList = new ArrayList<WTDocument>();
                        docList.add(doc);
                        resultMap.put(stepNum+"->"+paceNum,docList);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    public static WTDocument getBaiYuDocById(String id){
        WTDocument doc = null;
        try {
            int index[] = { 0 };
            QuerySpec qs = new QuerySpec(WTDocument.class);
            TypeUtil.getTypeQuery(WTDocument.class, "casc.sast.149.BaiYuDocument", qs);
            qs.setAdvancedQueryEnabled(true);
            String folderName = propertiesUtil.getProperty("baiyu-document-save-folder");
            String containerName = propertiesUtil.getProperty("baiyu-document-save-container");
            WTContainer container = WTContainerUtil.getContainerByName(containerName);
            Folder folder = FolderUtil.getFolder(folderName, WTContainerRef.newWTContainerRef(container));
            if(folder != null){
                qs.appendAnd();
                qs.appendWhere(new SearchCondition(WTDocument.class, "folderingInfo.parentFolder.key.id", SearchCondition.EQUAL,PersistenceHelper.getObjectIdentifier(folder).getId()), index);
            }
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL,id), index);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            LatestConfigSpec lcs = new LatestConfigSpec();
            qr = lcs.process(qr);
            if (qr.hasMoreElements()) {
                doc = (WTDocument) qr.nextElement();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    private String dealBaiyuRecord4Mes(Element parentElement, Element schemaData, String tempFilePath, String subFileName,WTDocument doc,boolean isGongYi) throws WTException {
        String msg = "sucess";
        String KEY3 = "_下限";
        String KEY4 = "_上限";
        String KEY5 = "_上偏差";
        String KEY6 = "_下偏差";
        String KEY7 = "_公称值";
        String KEY8 = "_规定值";

        Set<String> mulDocsMsg = new HashSet<String>();

        Map<String,String> collectDocs = new HashMap<>();
        Set<String> errorIdDocs = new HashSet<String>();
        boolean isQuanJuBiao = false;
        if(schemaData != null) {
            List<Element> schemaInfo = schemaData.getChildren("schemaInfo");
            if(schemaInfo != null && schemaInfo.size() > 0) {
                for(Element element : schemaInfo) {
                    String id = element.getAttributeValue("id");
                    WTDocument baiyuDoc = getBaiYuDocById(id);
                    if(baiyuDoc != null) {
                        downloadBaiYuFiles(baiyuDoc, tempFilePath + subFileName);
                        JSONObject tableJson = getJsonData(baiyuDoc);
                        if(tableJson != null) {
                            JSONArray data = tableJson.getJSONArray("data2");
                            if(data != null) {
                                Element checkRecordTablesBaiYu = parentElement.getChild("checkRecordTablesBaiYu");
                                if(checkRecordTablesBaiYu == null) {
                                    checkRecordTablesBaiYu = new Element("checkRecordTablesBaiYu");
                                    parentElement.addContent(checkRecordTablesBaiYu);
                                }

                                int index1 = 0;
                                int index2 = 0;
                                Element jiance = null;
                                Element jilu = null;
                                for(int i = 0; i < data.length(); i++) {
                                    JSONObject jsonObject = data.getJSONObject(i);
                                    String weiYiNumber = jsonObject.optString(KEY2);
                                    if(collectDocs.containsKey(weiYiNumber)) {
                                        mulDocsMsg.add(baiyuDoc.getNumber() +"与"+collectDocs.get(weiYiNumber)+"存在重复,重复编码为:"+weiYiNumber);
                                    }
                                    if("cee58b8b7d7f6fac".equals(weiYiNumber)){
                                        errorIdDocs.add(baiyuDoc.getNumber());
                                    }
                                    if(collectDocs.containsKey(weiYiNumber)){
                                        collectDocs.put(weiYiNumber,collectDocs.get(weiYiNumber)+"、"+baiyuDoc.getNumber());
                                    }else{
                                        collectDocs.put(weiYiNumber,baiyuDoc.getNumber());
                                    }

                                    List<Element> parameterTables = checkRecordTablesBaiYu.getChildren("parameterTable");

                                    if(jiance == null || jilu == null) {
                                        for(Element parameterTable : parameterTables) {
                                            if("检测类".equals(parameterTable.getAttribute("type")) && jiance == null) {
                                                jiance = parameterTable;
                                            } else if("记录类".equals(parameterTable.getAttribute("type")) && jilu == null) {
                                                jilu = parameterTable;
                                            }
                                        }
                                    }
                                    Iterator<String> keys = jsonObject.keys();
                                    List<String> otherAttris = new ArrayList<String>();
                                    while(keys.hasNext()) {
                                        String key = keys.next();
                                        if(key.endsWith(KEY3_END)) {
                                            KEY3 = key;
                                        } else if(key.endsWith(KEY4_END)) {
                                            KEY4 = key;
                                        } else if(key.endsWith(KEY5_END)) {
                                            KEY5 = key;
                                        } else if(key.endsWith(KEY6_END)) {
                                            KEY6 = key;
                                        } else if(key.endsWith(KEY7_END)) {
                                            KEY7 = key;
                                        } else if(key.endsWith(KEY8_END)) {
                                            KEY8 = key;
                                        } else if(key.endsWith(KEY9_END)||key.endsWith(KEY10_END)||key.endsWith(KEY11_END)||
                                        		key.endsWith(KEY12_END)||key.endsWith(KEY13_END)||key.endsWith(KEY14_END)) {
                                            otherAttris.add(key);
                                            if(isGongYi&&!isQuanJuBiao&&!Tools.isNull(jsonObject.optString(key))){
                                                isQuanJuBiao = true;
                                            }

                                        }
                                    }
                                    if(jsonObject.length() > 3) {
                                        String type = "检测类";
                                        if(Tools.isNull(jsonObject.optString(KEY3)) && Tools.isNull(jsonObject.optString(KEY4))
                                                && Tools.isNull(jsonObject.optString(KEY5)) && Tools.isNull(jsonObject.optString(KEY6))
                                                && Tools.isNull(jsonObject.optString(KEY7))) {
                                            type = "记录类";
                                        }
                                        if("检测类".equals(type)) {

                                            if(jiance == null) {
                                                jiance = new Element("parameterTable");
                                                jiance.setAttribute("name", baiyuDoc.getName() + "_" + type);
                                                jiance.setAttribute("type", type);
                                                jiance.setAttribute("projectName", "通用");
                                                jiance.setAttribute("tableName", "通用");
                                                jiance.setAttribute("eachName", "1");
                                                jiance.setAttribute("from", "baiyu");
                                                checkRecordTablesBaiYu.addContent(jiance);
                                            }

                                            Element parameter = new Element("parameter");
                                            Element values = new Element("values");
                                            values.setAttribute("number", index1 + "");

                                            Element value1 = new Element("value");
                                            value1.setAttribute("columnName", "检测项");
                                            Element attribute = new Element("attribute");
                                            attribute.setText(jsonObject.optString(KEY1));
                                            value1.addContent(attribute);
                                            values.addContent(value1);

                                            Element value2 = new Element("value");
                                            value2.setAttribute("columnName", "唯一编码");
                                            attribute = new Element("attribute");
                                            attribute.setText(jsonObject.optString(KEY2));
                                            value2.addContent(attribute);
                                            values.addContent(value2);

                                            Element value3 = new Element("value");
                                            value3.setAttribute("columnName", "公称值");
                                            attribute = new Element("attribute");
                                            attribute.setText(jsonObject.optString(KEY7));
                                            value3.addContent(attribute);
                                            values.addContent(value3);

                                            Element value4 = new Element("value");
                                            value4.setAttribute("columnName", "上偏差");
                                            attribute = new Element("attribute");
                                            attribute.setText(jsonObject.optString(KEY5));
                                            value4.addContent(attribute);
                                            values.addContent(value4);

                                            Element value5 = new Element("value");
                                            value5.setAttribute("columnName", "下偏差");
                                            attribute = new Element("attribute");
                                            attribute.setText(jsonObject.optString(KEY6));
                                            value5.addContent(attribute);
                                            values.addContent(value5);

                                            Element value6 = new Element("value");
                                            value6.setAttribute("columnName", "上限");
                                            attribute = new Element("attribute");
                                            attribute.setText(jsonObject.optString(KEY4));
                                            value6.addContent(attribute);
                                            values.addContent(value6);

                                            Element value7 = new Element("value");
                                            value7.setAttribute("columnName", "下限");
                                            attribute = new Element("attribute");
                                            attribute.setText(jsonObject.optString(KEY3));
                                            value7.addContent(attribute);
                                            values.addContent(value7);

                                            Element value8 = new Element("value");
                                            value8.setAttribute("columnName", "规定值");
                                            attribute = new Element("attribute");
                                            attribute.setText(jsonObject.optString(KEY8));
                                            value8.addContent(attribute);
                                            values.addContent(value8);

                                            Element value9 = new Element("value");
                                            value9.setAttribute("columnName", "实测值");
                                            attribute = new Element("attribute");
                                            attribute.setText("");
                                            value9.addContent(attribute);
                                            values.addContent(value9);

                                            Element value10 = new Element("value");
                                            value10.setAttribute("columnName", "判定/结论");
                                            attribute = new Element("attribute");
                                            attribute.setText("");
                                            value10.addContent(attribute);
                                            values.addContent(value10);

                                            processOtherAttris(otherAttris,values,jsonObject);

                                            parameter.addContent(values);
                                            jiance.addContent(parameter);

                                            index1++;
                                        } else if("记录类".equals(type)) {

                                            if(jilu == null) {
                                                jilu = new Element("parameterTable");
                                                jilu.setAttribute("name", baiyuDoc.getName() + "_" + type);
                                                jilu.setAttribute("type", type);
                                                jilu.setAttribute("projectName", "通用");
                                                jilu.setAttribute("tableName", "通用");
                                                jilu.setAttribute("eachName", "1");
                                                jilu.setAttribute("from", "baiyu");
                                                checkRecordTablesBaiYu.addContent(jilu);
                                            }

                                            Element parameter = new Element("parameter");
                                            Element values = new Element("values");
                                            values.setAttribute("number", index2 + "");

                                            Element value1 = new Element("value");
                                            value1.setAttribute("columnName", "记录项");
                                            Element attribute = new Element("attribute");
                                            attribute.setText(jsonObject.optString(KEY1));
                                            value1.addContent(attribute);
                                            values.addContent(value1);

                                            Element value2 = new Element("value");
                                            value2.setAttribute("columnName", "唯一编码");
                                            attribute = new Element("attribute");
                                            attribute.setText(jsonObject.optString(KEY2));
                                            value2.addContent(attribute);
                                            values.addContent(value2);

                                            Element value3 = new Element("value");
                                            value3.setAttribute("columnName", "要求");
                                            attribute = new Element("attribute");
                                            attribute.setText("");
                                            value3.addContent(attribute);
                                            values.addContent(value3);

                                            Element value4 = new Element("value");
                                            value4.setAttribute("columnName", "记录");
                                            attribute = new Element("attribute");
                                            attribute.setText("");
                                            value4.addContent(attribute);
                                            values.addContent(value4);

                                            Element value5 = new Element("value");
                                            value5.setAttribute("columnName", "判定/结论");
                                            attribute = new Element("attribute");
                                            attribute.setText("");
                                            value5.addContent(attribute);
                                            values.addContent(value5);

                                            processOtherAttris(otherAttris,values,jsonObject);


                                            parameter.addContent(values);
                                            jilu.addContent(parameter);
                                            index2++;
                                        }

                                    } else if(jsonObject.length() == 3) {
                                        String type = "记录类";
                                        if(jilu == null) {
                                            jilu = new Element("parameterTable");
                                            jilu.setAttribute("name", baiyuDoc.getName() + "_" + type);
                                            jilu.setAttribute("type", type);
                                            jilu.setAttribute("projectName", "通用");
                                            jilu.setAttribute("tableName", "通用");
                                            jilu.setAttribute("eachName", "1");
                                            jilu.setAttribute("from", "baiyu");
                                            checkRecordTablesBaiYu.addContent(jilu);
                                        }

                                        Element parameter = new Element("parameter");
                                        Element values = new Element("values");
                                        values.setAttribute("number", index2 + "");

                                        Element value1 = new Element("value");
                                        value1.setAttribute("columnName", "记录项");
                                        Element attribute = new Element("attribute");
                                        attribute.setText(jsonObject.optString(KEY1));
                                        value1.addContent(attribute);
                                        values.addContent(value1);

                                        Element value2 = new Element("value");
                                        value2.setAttribute("columnName", "唯一编码");
                                        attribute = new Element("attribute");
                                        attribute.setText(jsonObject.optString(KEY2));
                                        value2.addContent(attribute);
                                        values.addContent(value2);

                                        Element value3 = new Element("value");
                                        value3.setAttribute("columnName", "要求");
                                        attribute = new Element("attribute");
                                        attribute.setText("");
                                        value3.addContent(attribute);
                                        values.addContent(value3);

                                        Element value4 = new Element("value");
                                        value4.setAttribute("columnName", "记录");
                                        attribute = new Element("attribute");
                                        attribute.setText("");
                                        value4.addContent(attribute);
                                        values.addContent(value4);

                                        Element value5 = new Element("value");
                                        value5.setAttribute("columnName", "判定/结论");
                                        attribute = new Element("attribute");
                                        attribute.setText("");
                                        value5.addContent(attribute);
                                        values.addContent(value5);

                                        processOtherAttris(otherAttris,values,jsonObject);

                                        parameter.addContent(values);
                                        jilu.addContent(parameter);
                                        index2++;
                                    }

                                }
                            }

                        } else {
                            msg = "解析编号"+baiyuDoc.getNumber()+"的白羽json失败";
                        }
                    }
                }
            }
        }
        if(isQuanJuBiao&&doc!=null){
            WorkflowHelper.setIBAValue(doc,"tableType","全局表");
        }
        if(!mulDocsMsg.isEmpty()){
           msg = mulDocsMsg+ "的白羽文件json唯一编码重复！";
        }

        if(!errorIdDocs.isEmpty()){
            msg = errorIdDocs+ "的白羽文件json存在错误编码（cee58b8b7d7f6fac）！";
        }
        return msg;
    }


    private void processOtherAttris(List<String> keys, Element values, JSONObject jsonObject) {
        for(String key:keys){
            if(key!=null &&!"".equals(key)){
                String _key = key;
                if(key.contains("_")){
                    String[] ss = key.split("_");
                    _key = ss[1];
                }
                Element value = new Element("value");
                value.setAttribute("columnName", _key);
                Element attribute = new Element("attribute");
                attribute.setText(jsonObject.optString(key));
                value.addContent(attribute);
                values.addContent(value);
            }
        }

    }

}
