package com.glaway.mpm.sop.util;

import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.release.ProcessInfoReleaseController;
import com.glaway.mpm.sop.intf.SopIntf;
import com.glaway.mpm.util.*;
import com.glaway.mpm.visual.log.VaLogger;
import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.util.SopUtil;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.json.JSONObject;
import wt.doc.WTDocument;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * SOP工艺处理类
 */
public class SopProcessUtil {
    private static VaLogger logger = VaLogger.getLogger(SopProcessUtil.class);

    public static Map<String, String> getAllStepNameByZylb(String sopPartXml) throws DocumentException {
        Map<String, String> stepMap = new HashMap<String, String>();
        long startTime = System.currentTimeMillis();
        Document doc = DocumentHelper.parseText(sopPartXml);
        long endTime = System.currentTimeMillis();
        logger.debug("解析pbom-xml耗时：" + (endTime-startTime));
        if(doc != null){
            Element productEle = doc.getRootElement();
            Element parts = productEle.element("parts");
            Element qmPartInfo = parts.element("QMPartInfo");
            String zylb = qmPartInfo.attributeValue("SpecializedType");
            stepMap = SopIntf.getSopProcessStepName(zylb);
        }
        return stepMap;
    }

    public static Map<String, String> getAllStepName() throws DocumentException {
        Map<String, String> stepMap = new HashMap<String, String>();
        String type = SopConstants.SOP_TYPE_PROCEDUCENAME;
        stepMap = SopIntf.getAllSopProcessStepName(type);
        return stepMap;
    }

    public static String checkYQZ(Element techElement){
        StringBuilder sb = new StringBuilder();
        List<Element> allSteps = XmlUtility.getAllSteps(techElement);
        for (Element step : allSteps) {
            String stepNumber = step.attributeValue("stepNumber");
            String stepName = step.attributeValue("stepName");
            List<Element> sopinfoList = step.selectNodes("sops/SOPInfo");
            for (Element element : sopinfoList) {
                String yqz = element.attributeValue("canshuzhi");
                if(yqz == null || yqz.isEmpty()){
                    sb.append("工序["+stepNumber+"_"+stepName+"]引用的SOP文件要求值未填写！\n");
                    break;
                }
            }

            List<Element> allPaces = XmlUtility.getAllPaces(step);
            for (Element pace : allPaces) {
                String paceNumber = step.attributeValue("stepNumber");
                sopinfoList = pace.selectNodes("sops/SOPInfo");
                for (Element element : sopinfoList) {
                    String yqz = element.attributeValue("canshuzhi");
                    if(yqz == null || yqz.isEmpty()){
                        sb.append("工序["+stepNumber+"_"+stepName+"]-工步["+paceNumber+"]引用的SOP文件要求值未填写！\n");
                        break;
                    }
                }
            }
        }
        return sb.toString();
    }

    private static String initTargetPath = System.getProperty("user.home") + File.separator + "3D_PView";

    public static void previewSop(String filePath) {
        if (filePath == null) {
            return;
        }
        // 初始化保存路径
        String targetPath = initTargetPath + File.separator + System.currentTimeMillis();
        // 获取工艺xml文件名
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1) + ".xml";

        try {
            // copy工艺文件&模板文件到指定目录下
            File targetFile = new File(targetPath);
            if (targetFile.exists()) {
                FileUtil.deleteFile(targetFile);
            }
            FileUtil.createDirs(targetPath);
            FileUtil.copyFiles(filePath, targetPath);
            String path = SopProcessUtil.class.getResource(SopProcessUtil.class.getSimpleName() + ".class").getFile();
            if (path.lastIndexOf('!') == -1) {
                String templetesPath = ProcessInfoReleaseController.class.getResource("/com/glaway/mpm/release/templetes").getPath();
                FileUtil.copyFiles(templetesPath, targetPath);
            } else {
                FileUtil.copyFromJar(targetFile);
            }

            String xmlFileName = targetPath + File.separator + fileName;
            Document doc = XmlUtility.getDocument(xmlFileName);
            Element technicsElement = XmlUtility.getTechnicsElement(doc);
            //扩展XML内容，
            extendXmlContent(technicsElement,targetPath,false);
            writeDocument(doc, xmlFileName);

            String navTemplate = "sopTemplate_Nav.xsl";
            String mainTemplate = "sopTemplate_Main.xsl";
            // 生成导航页
            String xslFileName = targetPath + File.separator + "xsl" + File.separator + navTemplate;
            String htmlFileName = targetPath + File.separator + "nav.html";
            transform(xmlFileName, xslFileName, htmlFileName);
            // 生成内容页
            String xslFileName1 = targetPath + File.separator + "xsl" + File.separator + mainTemplate;
            String htmlFileName1 = targetPath + File.separator + "content.html";
            transform(xmlFileName, xslFileName1, htmlFileName1);

            //------------------------end----------------------------
            System.out.println("==发布路径==" + targetPath);
            // 浏览器打开预览文件
            openURL(targetPath + File.separator + "ReleasePage.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String previewSop2(String filePath) {
        if (filePath == null) {
            return null;
        }
        // 初始化保存路径
        String targetPath = initTargetPath + File.separator + System.currentTimeMillis();
        // 获取工艺xml文件名
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1) + ".xml";

        try {
            // copy工艺文件&模板文件到指定目录下
            File targetFile = new File(targetPath);
            if (targetFile.exists()) {
                FileUtil.deleteFile(targetFile);
            }
            FileUtil.createDirs(targetPath);
            FileUtil.copyFiles(filePath, targetPath);
            String path = SopProcessUtil.class.getResource(SopProcessUtil.class.getSimpleName() + ".class").getFile();
            if (path.lastIndexOf('!') == -1) {
                String templetesPath = ProcessInfoReleaseController.class.getResource("/com/glaway/mpm/release/templetes").getPath();
                FileUtil.copyFiles(templetesPath, targetPath);
            } else {
                FileUtil.copyFromJar(targetFile);
            }

            String xmlFileName = targetPath + File.separator + fileName;
            Document doc = XmlUtility.getDocument(xmlFileName);
            Element technicsElement = XmlUtility.getTechnicsElement(doc);
            //扩展XML内容，
            extendXmlContent(technicsElement,targetPath,false);
            writeDocument(doc, xmlFileName);

            String navTemplate = "sopTemplate_Nav.xsl";
            String mainTemplate = "sopTemplate_Main.xsl";
            // 生成导航页
            String xslFileName = targetPath + File.separator + "xsl" + File.separator + navTemplate;
            String htmlFileName = targetPath + File.separator + "nav.html";
            transform(xmlFileName, xslFileName, htmlFileName);
            // 生成内容页
            String xslFileName1 = targetPath + File.separator + "xsl" + File.separator + mainTemplate;
            String htmlFileName1 = targetPath + File.separator + "content.html";
            transform(xmlFileName, xslFileName1, htmlFileName1);

            //------------------------end----------------------------
            System.out.println("==发布路径==" + targetPath);
            // 返回主页URL
            return targetPath + File.separator + "ReleasePage.html";
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
    }

    public static String processInfoRelease(String zipPath, String path) {
        try {
            System.out.println("=========================发布Begin==========================");
            String targetPath = path;
            File pathFile = new File(path);
            if (!pathFile.exists()) {
                SopUtil.createDirs(path);
            }

            File targetFile = new File(targetPath);

            String tempPath = SopProcessUtil.class.getResource(SopProcessUtil.class.getSimpleName() + ".class").getFile();

            if (tempPath.lastIndexOf('!') == -1) {
                // 获取模板路径
                String templetesPath = ProcessInfoReleaseController.class.getResource("/com/glaway/mpm/release/templetes").getPath();
                FileUtil.copyFiles(templetesPath, targetPath);
            } else {
                SopUtil.copyFromJar(targetFile);
            }
            // copy资源文件到指定目录
            File zipFile = new File(zipPath);
            if (zipFile.isDirectory()) {
                AntTaskUtil.copydir(zipPath, targetPath);
            } else {
                ApacheZipUtil.decompress(zipPath, targetPath);
            }

            // 获取工艺xml文件名
            String fileName = zipPath.substring(zipPath.lastIndexOf(File.separator) + 1, zipPath.lastIndexOf(".zip")) + ".xml";
            String xmlFileName = targetPath + File.separator + fileName;

            ProcedurePictureCreateUtil.createPictureDirectory(targetPath);
            ProcedurePictureCreateUtil.operateDocument(XmlUtility.getDocument(xmlFileName), targetPath);
            System.out.println("=========================xmlFileName==========================");
            Document doc = XmlUtility.getDocument(xmlFileName);
            Element technicsElement = XmlUtility.getTechnicsElement(doc);
            //扩展XML内容，
            extendXmlContent(technicsElement,targetPath,true);

            //更新工艺预览生命周期状态
            String technicNumber = technicsElement.attributeValue("technicsNumber");
            WTDocument document = WTDocumentUtil.getLatestDocumentByNumber(technicNumber);
            String state = document.getState().getState().getDisplay(Locale.CHINA);
            technicsElement.addAttribute("lifecycle", state);

            writeDocument(doc, xmlFileName);

            String navTemplate = "sopTemplate_Nav.xsl";
            String mainTemplate = "sopTemplate_Main.xsl";

            // 生成导航页
            String xslFileName = targetPath + File.separator + "xsl" + File.separator + navTemplate;
            String htmlFileName = targetPath + File.separator + "nav.html";
            transform(xmlFileName, xslFileName, htmlFileName);
            // 生成内容页
            String xslFileName1 = targetPath + File.separator + "xsl" + File.separator + mainTemplate;
            String htmlFileName1 = targetPath + File.separator + "content.html";
            transform(xmlFileName, xslFileName1, htmlFileName1);

            return targetPath + File.separator + "ReleasePage.html";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    /**
     * @Title: writeDocument
     * @Description: 将Document元素写到filePath路径下
     * @param @param document
     * @param @param filePath
     * @return void
     * @throws
     */
    public static void writeDocument(Document document, String filePath) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            OutputFormat xmlFormat = OutputFormat.createPrettyPrint();
            xmlFormat.setEncoding("GBK");
            XMLWriter xmlWriter = new XMLWriter(fos, xmlFormat);
            xmlWriter.write(document);
            xmlWriter.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开URL浏览器
     *
     * @param url
     */
    private static void openURL(String url) {
        try {
            browse(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void browse(String url) throws Exception {
        String osName = System.getProperty("os.name", "");
        if (osName.startsWith("Mac OS")) {
            Class fileMgr = Class.forName("com.apple.eio.FileManager");
            Method openURL = fileMgr.getDeclaredMethod("openURL",
                    new Class[]{String.class});
            openURL.invoke(null, new Object[]{url});
        } else if (osName.startsWith("Windows")) {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
        } else {
            String[] browsers = {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
            String browser = null;
            for (int count = 0; count < browsers.length && browser == null; count++)
                if (Runtime.getRuntime().exec(new String[]{"which", browsers[count]}).waitFor() == 0)
                    browser = browsers[count];
            if (browser == null)
                throw new Exception("Could not find web browser");
            else
                Runtime.getRuntime().exec(new String[]{browser, url});
        }
    }

    /**
     * 方法功能:扩展XML内容，以便展示
     *
     * @param technicsElement
     * @param targetPath
     * @return void
     * @author LB
     * @date 2020/1/16
     */
    private static void extendXmlContent(Element technicsElement, String targetPath,boolean isFromService) throws Exception {
        //1.增加电子签名信息
        addPrintInfo(technicsElement);
        //2.处理工序工步节点信息
        dealwithStepsAndPaces(technicsElement,targetPath,isFromService);

    }

    /**
     * 方法功能: 处理工序工步节点信息
     *   1.替换工艺说明中的特殊字符
     *   2.替换工序工步内容中的特殊字符
     *   3.工步节点增加属性paceId
     *
     * @param technicsElement
     * @param targetPath
     * @param isFromService
     * @return void
     * @author LB
     * @date 2020/1/16
     */
    private static void dealwithStepsAndPaces(Element technicsElement, String targetPath, boolean isFromService) throws IOException {
//        String technicsPath = WorkSpaceUtil.getTechnicsPath(technicsElement);
        //工艺说明
        Element technicsDescribeEle = technicsElement.element("TechnicsDescribe");
        if(technicsDescribeEle != null){
            String des = PDFUtil.objectToString(technicsDescribeEle.getTextTrim());
            des = des.replace("@#$%^\\", "");
            /*des = des.replace("\\", "/");
            des = des.replace("WORKSPACE_PATH/", "");*/
            technicsDescribeEle.setText(des);
        }

        List<Element> allSteps = XmlUtility.getAllSteps(technicsElement);
        for(Element step : allSteps){
            String stepNumber = XmlUtility.getAttributeValue(step,"stepNumber");
            //工序内容
            Element stepContentEle = step.element("procedureContent");
            String stepContent = PDFUtil.objectToString(stepContentEle.getTextTrim());
            stepContent = stepContent.replace("@#$%^\\", "");
            stepContent = stepContent.replace("\\", "/");
            stepContent = stepContent.replace("WORKSPACE_PATH/", "");
            stepContentEle.setText(stepContent);
            List<Element> allPaces = XmlUtility.getAllPaces(step);
            for (Element pace : allPaces) {
                //工步内容
                Element paceContentEle = pace.element("procedureContent");
                String paceContent = PDFUtil.objectToString(paceContentEle.getTextTrim());
                paceContent = paceContent.replace("@#$%^\\", "");
                paceContent = paceContent.replace("\\", "/");
                paceContent = paceContent.replace("WORKSPACE_PATH/", "");
                paceContentEle.setText(paceContent);
                //新增属性paceId
                String paceNumber = XmlUtility.getAttributeValue(pace,"stepNumber");
                XmlUtility.setAttributeValue(pace,"paceId",stepNumber + "_" + paceNumber);
                //工艺附表
                //如果不是从服务端预览的，需重新转换下pdf，保证最新
                if(!isFromService){
                    List<Element> additionTables = XmlUtility.getTechnicsAdditionTables(pace);
                    for (Element additionTable : additionTables) {
                        String docName = additionTable.attributeValue("absolutePath");
                        String docPath = targetPath + File.separator + docName;
                        File docFile = new File(docPath);
                        if (docFile.exists() && docFile.isFile()) {
                            File pdfFile = Word2HtmlUtil.genPdf(new File(docPath));
                            FileUtils.copyFileToDirectory(pdfFile, new File(targetPath));
                            String pdfFilePath = pdfFile.getName();
                            additionTable.addAttribute("pdfAbsolutePath", pdfFilePath);
                        }
                    }
                }
            }
        }
    }

    /**
     * 方法功能: 增加电子签名信息
     *
     * @param technicsElement
     * @return void
     * @author LB
     * @date 2020/1/16
     */
    private static void addPrintInfo(Element technicsElement) {
        String technicNumber = technicsElement.attributeValue("technicsNumber");
        String version = technicsElement.attributeValue("version");
        if(version.contains(".")){
            version = version.split("\\.")[0];
        }
        JSONObject signInfo = SopIntf.getPrintInfo(technicNumber,version);
        if(signInfo != null ){
            technicsElement.addAttribute("SHEJI",signInfo.optString("SHEJI"));
            technicsElement.addAttribute("JIAODUI",signInfo.optString("JIAODUI"));
            technicsElement.addAttribute("SHENHE", signInfo.optString("SHENHE"));
            technicsElement.addAttribute("NEIBUHUIQIAN", signInfo.optString("NEIBUHUIQIAN"));
            technicsElement.addAttribute("WAIBUHUIQIAN",signInfo.optString("WAIBUHUIQIAN"));
            technicsElement.addAttribute("BIAOSHEN", signInfo.optString("BIAOSHEN"));
            technicsElement.addAttribute("PIZHUN", signInfo.optString("PIZHUN"));

        }
    }

    /**
     * 模板转换
     *
     * @param xmlFileName
     * @param xslFileName
     * @param htmlFileName
     */
    private static void transform(String xmlFileName, String xslFileName,
                                  String htmlFileName) {
        try {
            TransformerFactory tFac = TransformerFactory.newInstance();
            Source xslSource = new StreamSource(xslFileName);
            Transformer t = tFac.newTransformer(xslSource);
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            File xmlFile = new File(xmlFileName);
            File htmlFile = new File(htmlFileName);
            Source source = new StreamSource(xmlFile);
            Result result = new StreamResult(htmlFile);
            t.transform(source, result);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getAllCZMC() throws DocumentException {
    	Map<String, String> czmcs = new HashMap<String, String>();
        String type = SopConstants.SOP_TYPE_OPERATIONNAME;
//        List<MPMTooling> czmc = SopIntf.getSopResourceListByType(type);
//        for (MPMTooling tooling : czmc) {
//            czmcs.put(tooling.getNumber(), tooling.getName());
//        }
        czmcs = SopIntf.getAllCZMCByType(type);
        return czmcs;
    }


}
