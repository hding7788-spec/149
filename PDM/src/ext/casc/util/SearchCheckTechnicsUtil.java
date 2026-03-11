package ext.casc.util;

import com.glaway.mpm.util.WTContainerUtil;
import com.ptc.core.meta.common.TypeIdentifier;
import ext.casc.integrate.util.CldeUtil;
import ext.casc.integrate.util.ZipUtil;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTStandardDateFormat;
import wt.vc.config.LatestConfigSpec;

import java.beans.PropertyVetoException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

import static ext.casc.util.SearchErrorProcessDocUtility.fileToBytes;

public class SearchCheckTechnicsUtil implements RemoteAccess {

    public static void main(String[] args) {
        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        String username = null;
        String passwd = null;
        String startTime = "";
        if (args.length >= 3) {
            username = args[0];
            passwd = args[1];
            startTime = args[2];
            if (username == null)
                username = "wcadmin";

            if (passwd == null)
                passwd = "Admin@149";
        }
        System.out.println("------user:" + username + "    password:" + passwd);
        rms.setUserName(username);
        rms.setPassword(passwd);
        try {
            searchCheckTechnics(startTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String searchAllCheckTechnicsAndExport(String createTime, String containerName) throws Exception {
        System.out.println("============searchAllCheckTechnicsAndExport===========");
        System.out.println("createTime:" + createTime+ ",containerName:"+ containerName);
        List<WTDocument> documentList = getDoc(createTime, containerName);

        Map<String, List<Element>> allCheckedTechnics = getAllCheckedTechnics(documentList);

        String filePath = exportToExcel(allCheckedTechnics);
        System.out.println(filePath);
        System.out.println("============searchAllCheckTechnicsAndExport===========");
        return filePath;
    }

    /**
     * 获取某时间后，某型号下所有工艺文件
     * @return
     */
    public static List<WTDocument> getDoc(String createTime, String containerName) throws Exception {
        System.out.println("============getDoc===========");
        List<WTDocument> documentList = new ArrayList<WTDocument>();
        ArrayList<TypeIdentifier> list = SoftTypeUtil.getChildTypes("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", null);
        for (TypeIdentifier ti : list) {
            String docType = SoftTypeUtil.getTypeIdentifierDefinition(ti).getDisplay();
            List<WTDocument> docList = getAllGongyiWenJian(ti.toString().substring(7),createTime,containerName);
            documentList.addAll(docList);
        }
        System.out.println("============getDoc===========end ==all technics size:" + documentList.size());
        return documentList;
    }

    public static Map<String, List<Element>> getAllCheckedTechnics(List<WTDocument> documentList) throws Exception {
        System.out.println("============getAllCheckedTechnics===========");
        Map<String, List<Element>> checkTechnicsInfoMap = new HashMap<String, List<Element>>();
        WTProperties pro = WTProperties.getLocalProperties();
        String zip_temp_dir = pro.getProperty("wt.temp");
        for (WTDocument doc : documentList) {
            if (doc != null) {
                System.out.println("update " + doc.getNumber() + " " + doc.getName());
                ApplicationData data = (ApplicationData) ContentHelper.service.getPrimary(doc);
                if (data == null) {
                    continue;
                }

                String zipFilePath = zip_temp_dir + File.separator + "checkTemp" + File.separator + doc.getNumber() + File.separator + doc.getNumber();
                File techDir = new File(zipFilePath);
                if (!techDir.exists()) {
                    techDir.mkdirs();
                }

                String xmlFile = zipFilePath + File.separator + doc.getNumber() + ".xml";
                InputStream inputStream = ContentServerHelper.service.findContentStream(data);
                byte[] bytes = fileToBytes(inputStream);
                ZipUtil.unZip(bytes, zipFilePath);

                File file = new File(xmlFile);
                if (file.exists() && file.length() > 0) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(file);
                    Element rootElement = document.getRootElement();

                    //工艺文件信息
                    List pplanList = rootElement.selectNodes("//QMFawTechnicsInfo");
                    Element technicElement;
                    for (Object object : pplanList) {
                        technicElement = (Element) object;
                        String partNumber = technicElement.attributeValue("partNumber");
                        String partVersion = technicElement.attributeValue("partVersion");
                        String key = partNumber + "," + partVersion;
                        List<Element> stepElementList = technicElement.selectNodes("steps/QMProcedureInfo");
                        for (Element stepElement : stepElementList) {
                            List<Element> paceElementList = stepElement.selectNodes("paces/QMProcedureInfo");
                            for (Element paceElement : paceElementList) {
                                String isCheck = paceElement.attributeValue("isCheck");
                                if (isCheck != null && "true".equals(isCheck)) {
                                    if (checkTechnicsInfoMap.containsKey(key)) {
                                        List<Element> elements = checkTechnicsInfoMap.get(key);
                                        if (!elements.contains(technicElement)) {
                                            elements.add(technicElement);
                                        }
                                    } else {
                                        List<Element> techList = new ArrayList<Element>();
                                        techList.add(technicElement);
                                        checkTechnicsInfoMap.put(key, techList);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                //删除临时文件
                CldeUtil.deleteFiles(new File(zip_temp_dir + File.separator + "checkTemp" + File.separator + doc.getNumber()));
            }
        }
        System.out.println("============getAllCheckedTechnics===========end all checktechnicsSize :" + checkTechnicsInfoMap.size());
        return checkTechnicsInfoMap;
    }

    public static String exportToExcel(Map<String, List<Element>> checkTechnicsInfoMap) throws Exception {
        System.out.println("============exportToExcel===========");
        WTProperties pro = WTProperties.getLocalProperties();
        String wt_temp = pro.getProperty("wt.temp");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFCellStyle style = workbook.createCellStyle(); // 样式对象
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 使用枚举值
        style.setAlignment(HorizontalAlignment.CENTER);
        HSSFSheet sheet = workbook.createSheet("检验工艺列表");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("工艺文件流水号");

        cell = row.createCell(1);
        cell.setCellValue("工艺文件编号");

        cell = row.createCell(2);
        cell.setCellValue("工艺文件版本号");

        cell = row.createCell(3);
        cell.setCellValue("部件编号");

        cell = row.createCell(4);
        cell.setCellValue("部件版本号");

        cell = row.createCell(5);
        cell.setCellValue("产品名称");

        int rowCount = 1;
        for (Map.Entry<String, List<Element>> entry : checkTechnicsInfoMap.entrySet()) {
            String key = entry.getKey();
            String partNumber = key.split(",")[0];
            String partVersion = key.split(",")[1];
            List<Element> techList = entry.getValue();
            for (Element techEle : techList) {
                String technicsNumber = techEle.attributeValue("technicsNumber");
                String technicsVersion = techEle.attributeValue("version");
                String pplanNumber = techEle.attributeValue("pplanNumber");
                String productName = techEle.attributeValue("productName");

                row = sheet.createRow(rowCount);

                cell = row.createCell(0);
                cell.setCellValue(technicsNumber);

                cell = row.createCell(1);
                cell.setCellValue(pplanNumber);

                cell = row.createCell(2);
                cell.setCellValue(technicsVersion);

                cell = row.createCell(3);
                cell.setCellValue(partNumber);

                cell = row.createCell(4);
                cell.setCellValue(partVersion);

                cell = row.createCell(5);
                cell.setCellValue(productName);
                rowCount++;
            }
        }
        String outPutPath = wt_temp + File.separator + "checkTechnicsList.xls";
        FileOutputStream writeFile = null;
        try {
            writeFile = new FileOutputStream(outPutPath);
            workbook.write(writeFile);
            writeFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(writeFile != null){
                    writeFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("============exportToExcel===========end");
        return outPutPath;
    }


    public static String searchCheckTechnics(String startTime) throws DocumentException, WTException, IOException, PropertyVetoException, ParseException {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "searchCheckTechnics";
            Class[] types = {};
            Object[] vals = {};

            RemoteMethodServer rms = RemoteMethodServer.getDefault();
            try {
                rms.invoke(method, SearchCheckTechnicsUtil.class.getName(), null, types, vals);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        Map<String, List<Element>> checkTechnicsInfoMap = new HashMap<String, List<Element>>();
        List<String> techncisNumber = new ArrayList<String>();
        WTProperties pro = WTProperties.getLocalProperties();
        String wt_temp = pro.getProperty("wt.temp");
        String zip_temp_dir = wt_temp;
        String log = "";
        ArrayList<TypeIdentifier> list = SoftTypeUtil.getChildTypes("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", null);
        for (TypeIdentifier ti : list) {
            String type = SoftTypeUtil.getTypeIdentifierDefinition(ti).getDisplay();
            System.out.println(type + " " + ti.toString());
            List<WTDocument> docs = getAllGongyiWenJian(ti.toString().substring(7), startTime,"");

            for (WTDocument doc : docs) {
                if (doc != null) {
                    System.out.println("update " + doc.getNumber() + " " + doc.getName());
                    ApplicationData data = (ApplicationData) ContentHelper.service.getPrimary(doc);
                    if (data == null) {
                        continue;
                    }

                    String zipFilePath = zip_temp_dir + File.separator + "checkTemp" + File.separator + doc.getNumber() + File.separator + doc.getNumber();
                    File techDir = new File(zipFilePath);
                    if (!techDir.exists()) {
                        techDir.mkdirs();
                    }

                    String xmlFile = zipFilePath + File.separator + doc.getNumber() + ".xml";
                    InputStream inputStream = ContentServerHelper.service.findContentStream(data);
                    byte[] bytes = fileToBytes(inputStream);
                    ZipUtil.unZip(bytes, zipFilePath);

                    File file = new File(xmlFile);
                    if (file.exists() && file.length() > 0) {
                        SAXReader reader = new SAXReader();
                        Document document = reader.read(file);
                        Element rootElement = document.getRootElement();

                        //工艺文件信息
                        List pplanList = rootElement.selectNodes("//QMFawTechnicsInfo");
                        Element technicElement;
                        for (Object object : pplanList) {
                            technicElement = (Element) object;
                            String partNumber = technicElement.attributeValue("partNumber");
                            String partVersion = technicElement.attributeValue("partVersion");
                            String key = partNumber + "," + partVersion;
                            List<Element> stepElementList = technicElement.selectNodes("steps/QMProcedureInfo");
                            for (Element stepElement : stepElementList) {
                                List<Element> paceElementList = stepElement.selectNodes("paces/QMProcedureInfo");
                                for (Element paceElement : paceElementList) {
                                    String isCheck = paceElement.attributeValue("isCheck");
                                    if (isCheck != null && "true".equals(isCheck)) {
                                        if (checkTechnicsInfoMap.containsKey(key)) {
                                            List<Element> elements = checkTechnicsInfoMap.get(key);
                                            if (!elements.contains(technicElement)) {
                                                elements.add(technicElement);
                                            }
                                        } else {
                                            List<Element> techList = new ArrayList<Element>();
                                            techList.add(technicElement);
                                            checkTechnicsInfoMap.put(key, techList);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    //删除临时文件
                    CldeUtil.deleteFiles(new File(zip_temp_dir + File.separator + "checkTemp" + File.separator + doc.getNumber()));
                }
            }

        }
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFCellStyle style = workbook.createCellStyle(); // 样式对象
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 使用枚举值
        style.setAlignment(HorizontalAlignment.CENTER);
        HSSFSheet sheet = workbook.createSheet("检验工艺列表");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("工艺文件流水号");

        cell = row.createCell(1);
        cell.setCellValue("工艺文件编号");

        cell = row.createCell(2);
        cell.setCellValue("工艺文件版本号");

        cell = row.createCell(3);
        cell.setCellValue("部件编号");

        cell = row.createCell(4);
        cell.setCellValue("部件版本号");

        int rowCount = 1;
        for (Map.Entry<String, List<Element>> entry : checkTechnicsInfoMap.entrySet()) {
            String key = entry.getKey();
            String partNumber = key.split(",")[0];
            String partVersion = key.split(",")[1];
            List<Element> techList = entry.getValue();
            for (Element techEle : techList) {
                String technicsNumber = techEle.attributeValue("technicsNumber");
                String technicsVersion = techEle.attributeValue("version");
                String pplanNumber = techEle.attributeValue("pplanNumber");

                row = sheet.createRow(rowCount);

                cell = row.createCell(0);
                cell.setCellValue(technicsNumber);

                cell = row.createCell(1);
                cell.setCellValue(pplanNumber);

                cell = row.createCell(2);
                cell.setCellValue(technicsVersion);

                cell = row.createCell(3);
                cell.setCellValue(partNumber);

                cell = row.createCell(4);
                cell.setCellValue(partVersion);
                rowCount++;
            }

        }
        String outPutPath = wt_temp + File.separator + "checkTechnicsList.xls";
        FileOutputStream writeFile = null;
        try {
            writeFile = new FileOutputStream(outPutPath);
            workbook.write(writeFile);
            writeFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outPutPath;
    }

    public static List<WTDocument> getAllGongyiWenJian(String type, String startTime,String containerName) throws RemoteException, WTException, ParseException {
        List<WTDocument> list = new ArrayList<WTDocument>();
        TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(type);
        long typeId = 0;
        if (tdr != null) {
            typeId = tdr.getKey().getBranchId();
        }
        QuerySpec qs = new QuerySpec(WTDocument.class);
//        qs = new LatestConfigSpec().appendSearchCriteria(qs);
//        qs.appendAnd();
        qs.appendWhere(new SearchCondition(WTDocument.class, "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeId), new int[]{0});
        if(containerName != null && !containerName.isEmpty()){
            WTContainer container = WTContainerUtil.getContainerByName(containerName);
            long containerId = PersistenceHelper.getObjectIdentifier(container).getId();
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(WTDocument.class, "containerReference.key.id", SearchCondition.EQUAL, containerId), new int[]{0});
        }
        qs.appendAnd();
        Date dateFrom = WTStandardDateFormat.parse(startTime, "yyyy/M/d");
        qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.MODIFY_TIMESTAMP, SearchCondition.GREATER_THAN_OR_EQUAL, new Timestamp(dateFrom.getTime())));
        QueryResult qr = PersistenceHelper.manager.find(qs);
        qr = new LatestConfigSpec().process(qr);
        while (qr.hasMoreElements()) {
            WTDocument doc = (WTDocument) qr.nextElement();
            list.add(doc);
        }

        return list;
    }

}
