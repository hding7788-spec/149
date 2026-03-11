package com.glaway.mpm.util;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


public class WorkSpaceUtil {
    public static final String SEPARATOR = "`";
    public static final String OLD_SEPARATOR = "/";
    public static final String NEW_SEPARATOR = "~";
    public static final String SLANT = "";
    public static final String ASM_TYPE = "装配工艺";
//	public static final String PART_TYPE = "零件工艺";
//	public static final String PAINT_TYPE = "油漆工艺";
//	public static final String MACHINING_TYPE = "机加工艺";
//	public static final String MOUNT_TYPE = "装联工艺";

    public static HashMap<String, String> mySettingMap = new HashMap<String, String>();
    public static final String PROPERTIES_FILEPATH = System
            .getProperty("user.home") + File.separator + "workSpace.properties";
    private static String workSpace_path = "";
    public static final String TECHNICS_PATH = "technics";
    public static final String PRODUCT_PATH = "product";
    public static final String EBOM_PATH = "ebom";
    public static final String TEMPLET_PATH = "templet";
    public static final String STEP_PATH = "procedureTemlet";

//	public static final String PART_TEMPLATE = "partTemplate";
//	public static final String ASSEMBLE_TEMPLATE = "assembleTemplate";
//	public static final String PAINT_TEMPLATE = "paintTemplate";
//	public static final String MACHINING_TEMPLATE = "machiningTemplate";
//	public static final String MOUNT_TEMPLATE = "mountTemplate";

    public static final String TEMP_PATH = "temp";
    public static final String RESOURCE_PATH = "resource";
    public static final String TERMINOLOGY_PATH = "terminology";
    public static String MYSETTING_FILENAME;
    public static final String TECHNICS_DIAGRAM_TOOL = "TechnicsDiagramToolInfo=";
    public static final String MID_MODEL_TOOL = "MidModelToolInfo=";
    public static final String VIDEO_TOOL = "VedioToolInfo=";
    public static final String ASSEMBLAGE_CARTOON_TOOL = "AssemblageCartoonToolInfo=";
    public static final String TO_ARCHIVE = "拟制";
    private static FileFilter directoryFilter = new DirectoryFilter();
    private static String[][] technicsType = LoadConfig.getInstance().getTechnicsType();

    public static final String XML_HOME = System.getProperty("user.home")
            + File.separator + "XML" + File.separator;

    static {
        try {
            if (isExistWrokSpace()) {
                String temp = getWorkSpace();
                createChildFolder(temp);
            }
            MYSETTING_FILENAME = getWorkSpace().trim() + File.separator + "mySetting.xml";
            setMySettingMap();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * 删除保存xml的路径
     */
    public static void deleteXMLDirectory() {
        File file = new File(XML_HOME);
        if (file.exists()) {
            FileUtil.deleteSubFile(file);
        } else {
            file.mkdirs();
        }
    }

    public static void setMySettingMap() throws Exception {
        mySettingMap.put("TechnicsDiagramToolInfo=", getKeyValue(1, "="));
        mySettingMap.put("MidModelToolInfo=", getKeyValue(2, "="));
        mySettingMap.put("VedioToolInfo=", getKeyValue(3, "="));
        mySettingMap.put("AssemblageCartoonToolInfo=", getKeyValue(4, "="));
    }

    public static void CreatTechnis_ProductSpace(String loc) throws Exception {
        createChildFolder(loc);

        createWorkSpace(loc);
        MYSETTING_FILENAME = getWorkSpace().trim() + File.separator + "mySetting.xml";
    }

    private static void createChildFolder(String loc) throws Exception {
        File technics = new File(loc, "technics");
        if (!technics.exists()) {
            technics.mkdirs();
        }
        File product = new File(loc, "product");
        if (!product.exists()) {
            product.mkdirs();
        }
        File templet = new File(loc, "templet");
        if (!templet.exists()) {
            templet.mkdirs();
        }


        File templateType = null;
        for (int i = 0; i < technicsType[0].length; i++) {
            templateType = new File(templet, technicsType[0][i]);
            if (!templateType.exists()) {
                templateType.mkdirs();
            }
        }

//		File assembleTemplate = new File(loc, "templet\\assembleTemplate"); //装配工艺
//		if (!assembleTemplate.exists()) {
//			assembleTemplate.mkdirs();
//		}
//
//		File mountTemplate = new File(loc, "templet\\mountTemplate");  //装联工艺
//		if (!mountTemplate.exists()) {
//			mountTemplate.mkdirs();
//		}
//
//		File machiningTemplate = new File(loc, "templet\\machiningTemplate");  //机加工艺
//		if (!machiningTemplate.exists()) {
//			machiningTemplate.mkdirs();
//		}
//
//		File paintTemplate = new File(loc, "templet\\paintTemplate");  //油漆工艺
//		if (!paintTemplate.exists()) {
//			paintTemplate.mkdirs();
//		}


        File temp = new File(loc, "temp");
        if (!temp.exists()) {
            temp.mkdirs();
        }

        File step = new File(loc, "procedureTemlet");
        if (!step.exists()) {
            step.mkdirs();
        }


        File stepTemplate = null;
        for (int i = 0; i < technicsType[0].length; i++) {
            stepTemplate = new File(step, technicsType[0][i]);
            if (!stepTemplate.exists()) {
                stepTemplate.mkdirs();
            }
        }

//		File stepPartTemplate = new File(loc, "procedureTemlet\\partTemplate");
//		if (!stepPartTemplate.exists()) {
//			stepPartTemplate.mkdirs();
//		}
//		File steAssembleTemplate = new File(loc,
//				"procedureTemlet\\assembleTemplate");
//		if (!steAssembleTemplate.exists()) {
//			steAssembleTemplate.mkdirs();
//		}


        String mySetting = "";
        if (loc.endsWith(File.separator))
            mySetting = loc + "mySetting.xml";
        else
            mySetting = loc + File.separator + "mySetting.xml";
        File mySettingFile = new File(mySetting);
        if (!mySettingFile.exists())
            mySettingFile.createNewFile();
    }

//	public static String createProductDirectory(String productNumber,
//			String productName) throws Exception {
//		String dir = getProductRootPath() + "\\" + productNumber + "`"
//				+ productName;
//		File file = new File(dir);
//		file.mkdir();
//		File xmlFile = new File(file.getAbsolutePath() + "\\" + productNumber
//				+ "`" + productName + ".xml");
//		xmlFile.createNewFile();
//		return dir;
//	}

    public static String getTechnicsConversion(String technicsNumber,
                                               String technicsName, String technicsType, String partNumber,
                                               String productNumber) {
//		String str = technicsNumber + "`" + technicsName + "`" + technicsType
//				+ "`" + partNumber + "`" + productNumber;
//		return str.replace("/", "~");
        return technicsNumber.toUpperCase();
    }

    public static String createTechnicsDirectory(String technicsNumber,
                                                 String technicsName, String technicsType, String partNumber,
                                                 String productNumber, String technicsCategory) {
        String dir;
        if ("rework".equals(technicsCategory)) {
            dir = getReworkTechnicsRootPath() + File.separator;
        } else if ("temp".equals(technicsCategory)) {
            dir = getTempTechnicsRootPath() + File.separator;
        } else {
            dir = getCommonTechnicsRootPath() + File.separator;
        }
        dir += getTechnicsConversion(technicsNumber, technicsName, technicsType, partNumber, productNumber);

        File file = new File(dir);
        if (!file.exists())
            file.mkdirs();

        return dir;
    }

    public static String getTechnicsRootPath(String technicsCategory) {
        String dir = null;
        if ("rework".equals(technicsCategory)) {
            dir = getReworkTechnicsRootPath() + File.separator;
        } else if ("temp".equals(technicsCategory)) {
            dir = getTempTechnicsRootPath() + File.separator;
        } else {
            dir = getCommonTechnicsRootPath() + File.separator;
        }
        return dir;
    }

    public static String createTechnicsDirectory(String fileName)
            throws Exception {
        String dir = getCommonTechnicsRootPath() + File.separator + fileName;
        File file = new File(dir);
        file.mkdirs();
        return dir;
    }

    public static String createMesTechnicsDirectory(String fileName)
            throws Exception {
        String dir = getMesTempletRootPath() + File.separator + fileName;
        File file = new File(dir);
        file.mkdirs();
        return dir;
    }

    public static String createReworkTechnicsDirectory(String fileName)
            throws Exception {
        String dir = getReworkTechnicsRootPath() + File.separator + fileName;
        File file = new File(dir);
        file.mkdirs();
        return dir;
    }

    public static String createTempTechnicsDirectory(String fileName)
            throws Exception {
        String dir = getTempTechnicsRootPath() + File.separator + fileName;
        File file = new File(dir);
        file.mkdirs();
        return dir;
    }

    private static String[] fileNameArray(String filenName) {
        return filenName.split("`");
    }

    public static String getTechnicsNumber(String fileName) {
//		return fileNameArray(fileName)[0];
        if (fileName.contains(".")) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        } else {
            return fileName;
        }
    }

//	public static String getTechnicsName(String fileName) {
//		return fileNameArray(fileName)[1];
//	}
//
//	public static String getTechnicsType(String fileName) {
//		return fileNameArray(fileName)[2];
//	}
//
//	public static String getPartNumber(String fileName) {
//		return fileNameArray(fileName)[3];
//	}
//
//	public static String getProductNumber(String fileName) {
//		return fileNameArray(fileName)[4];
//	}

    public static Document createTechnics(Element techElement, String dir)
            throws Exception {
        Document document = techElement.getDocument();
        if (document == null) {
            document = XmlUtility.createDocument();
            document.getRootElement().add(techElement);
        }
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("GBK");

        String technicsNumber = techElement.attributeValue("technicsNumber");
        String technicsName = techElement.attributeValue("technicsName");
        String technicsType = techElement.attributeValue("technicsType");
        String partNumber = techElement.attributeValue("partNumber");
        String productNumber = techElement.attributeValue("productNumber");
        String fileName = getTechnicsConversion(technicsNumber, technicsName, technicsType, partNumber, productNumber) + ".xml";
        try {
            System.out.println("aaaa===" + getTechnicsDirectory(technicsNumber));
            System.out.println("bbb===" + fileName);
            XMLWriter writer = new XMLWriter(new FileOutputStream(dir + File.separator + fileName), format);
            writer.write(document);
            writer.close();
        } catch (IOException e1) {
            throw new Exception("在生成工艺信息文件时出现错误！");
        }
        return document;
    }

    public static Document createReportTechnics(Element techElement, String dir)
            throws Exception {
        Document document = techElement.getDocument();
        if (document == null) {
            document = XmlUtility.createDocument();
            document.getRootElement().add(techElement);
        }
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("GBK");

        String technicsNumber = techElement.attributeValue("technicsNumber");
        String fileName = getTechnicsConversion(technicsNumber, null, null, null, null) + ".xml";
        try {
            XMLWriter writer = new XMLWriter(new FileOutputStream(dir + File.separator + fileName), format);
            writer.write(document);
            writer.close();
        } catch (IOException e1) {
            throw new Exception("在生成报表类工艺信息文件时出现错误！\r\n" + e1.getLocalizedMessage());
        }
        return document;
    }

    public static String getCommonTechnicsRootPath() {
        String productAddress = getWorkSpace() + File.separator + "technics" + File.separator;
        return productAddress;
    }

    public static String getReworkTechnicsRootPath() {
        String productAddress = getWorkSpace() + File.separator + "reworkTechnics" + File.separator;
        return productAddress;
    }

    public static String getTempTechnicsRootPath() {
        String productAddress = getWorkSpace() + File.separator + "tempTechnics" + File.separator;
        return productAddress;
    }

    public static String getProductRootPath() throws Exception {
        String productAddress = getWorkSpace() + File.separator + "product";
        return productAddress;
    }

    public static String getTempletRootPath() {
        String templetAddress = getWorkSpace() + File.separator + "templet" + File.separator;
        File templet = new File(templetAddress);
        if (!templet.exists())
            templet.mkdirs();
        return templetAddress;
    }

    public static String getMesTempletRootPath() {
        String templetAddress = getWorkSpace() + File.separator + "mes" + File.separator;
        return templetAddress;
    }
//	public static String getPartTempletPath() throws Exception {
//		String partTempletAddress = getTempletRootPath() + "\\"
//				+ "partTemplate";
//		File partTemplet = new File(partTempletAddress);
//		if (!partTemplet.exists())
//			partTemplet.mkdirs();
//		return partTempletAddress;
//	}
//
//	public static String getAssembleTempletPath() throws Exception {
//		String assembleTempletAddress = getTempletRootPath() + "\\"
//				+ "assembleTemplate";
//		File assembleTemplet = new File(assembleTempletAddress);
//		if (!assembleTemplet.exists())
//			assembleTemplet.mkdirs();
//		return assembleTempletAddress;
//	}

    public static String getTempletPath(String type) throws Exception {
//		String partTempletAddress = getTempletRootPath() + "\\"
//				+ "partTemplate";
//		File partTemplet = new File(partTempletAddress);
//		if (!partTemplet.exists())
//			partTemplet.mkdirs();
//		return partTempletAddress;

        String templetAddress = getTempletRootPath() + File.separator + type;
        File assembleTemplet = new File(templetAddress);
        if (!assembleTemplet.exists())
            assembleTemplet.mkdirs();
        return templetAddress;

    }


    public static String getTempRootPath() {
        String tempAddress = getWorkSpace() + File.separator + "temp" + File.separator;
        File temp = new File(tempAddress);
        if (!temp.exists())
            temp.mkdirs();
        return tempAddress;
    }

    public static String getStepRootPath() {
        String tempAddress = getWorkSpace() + File.separator + "procedureTemlet";
        File temp = new File(tempAddress);
        if (!temp.exists())
            temp.mkdirs();
        return tempAddress;
    }

    public static String getPaceRootPath() {
        String tempAddress = getWorkSpace() + File.separator + "paceTemlet";
        File temp = new File(tempAddress);
        if (!temp.exists())
            temp.mkdirs();
        return tempAddress;
    }

//	public static String getPartProcedureTempletPath() {
//		String partTempletAddress = getStepRootPath() + "\\" + "partTemplate";
//		File partTemplet = new File(partTempletAddress);
//		if (!partTemplet.exists())
//			partTemplet.mkdirs();
//		return partTempletAddress;
//	}
//
//	public static String getAssembleProcedureTempletPath() {
//		String assembleTempletAddress = getStepRootPath() + "\\"
//				+ "assembleTemplate";
//		File assembleTemplet = new File(assembleTempletAddress);
//		if (!assembleTemplet.exists())
//			assembleTemplet.mkdirs();
//		return assembleTempletAddress;
//	}


    public static String getProcedureTempletPath(String type) {
        String templetAddress = null;

        templetAddress = getStepRootPath() + File.separator + type;


        File assembleTemplet = new File(templetAddress);
        if (!assembleTemplet.exists())
            assembleTemplet.mkdirs();
        return templetAddress;
    }

    public static boolean isExistTechnics(String technicsNumber)
            throws Exception {
        String[] technicsArray = getTechnicsDirectoryNames();
        if ((technicsArray == null) || (technicsArray.length == 0))
            return false;
        for (int i = 0; i < technicsArray.length; i++) {
//			if (technicsArray[i].startsWith(technicsNumber.replaceAll("/", "~")
//					+ "`"))
            if (technicsArray[i].equals(technicsNumber.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isReworkExistTechnics(String technicsNumber)
            throws Exception {
        String[] technicsArray = getReworkTechnicsDirectoryNames();
        if ((technicsArray == null) || (technicsArray.length == 0))
            return false;
        for (int i = 0; i < technicsArray.length; i++) {
//			if (technicsArray[i].startsWith(technicsNumber.replaceAll("/", "~")
//					+ "`")) {
            if (technicsArray[i].equals(technicsNumber.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTempExistTechnics(String technicsNumber)
            throws Exception {
        String[] technicsArray = getTempTechnicsDirectoryNames();
        if ((technicsArray == null) || (technicsArray.length == 0))
            return false;
        for (int i = 0; i < technicsArray.length; i++) {
//			if (technicsArray[i].startsWith(technicsNumber.replaceAll("/", "~")
//					+ "`")) {
            if (technicsArray[i].equals(technicsNumber.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getReworkTechnicsDirectories(
            String technicsNumber) {
        List<String> directories = new ArrayList<String>();
        String[] technicsArray = getReworkTechnicsDirectoryNames();
        if ((technicsArray == null) || (technicsArray.length == 0)) {
            return null;
        }
        for (int i = 0; i < technicsArray.length; i++) {
//			if (technicsArray[i].startsWith(technicsNumber.replaceAll("/", "~")
//					+ "`")) {
            if (technicsArray[i].equals(technicsNumber.toUpperCase())) {
                String technicsDirectory = getReworkTechnicsRootPath()
                        + technicsArray[i];
                File file = new File(technicsDirectory);
                if (file.exists()) {
                    directories.add(technicsDirectory);
                }
            }
        }
        return directories;
    }

    public static List<String> getTempTechnicsDirectories(String technicsNumber) {
        List<String> directories = new ArrayList<String>();
        String[] technicsArray = getTempTechnicsDirectoryNames();
        if ((technicsArray == null) || (technicsArray.length == 0)) {
            return null;
        }
        for (int i = 0; i < technicsArray.length; i++) {
//			if (technicsArray[i].startsWith(technicsNumber.replaceAll("/", "~")
//					+ "`")) {
            if (technicsArray[i].equals(technicsNumber.toUpperCase())) {
                String technicsDirectory = getTempTechnicsRootPath()
                        + technicsArray[i];
                File file = new File(technicsDirectory);
                if (file.exists()) {
                    directories.add(technicsDirectory);
                }
            }
        }
        return directories;
    }

    public static List<String> getReworkTechnicsXmlPaths(String technicsNumber) {
        List<String> directories = new ArrayList<String>();
        String[] technicsArray = getReworkTechnicsDirectoryNames();
        if ((technicsArray == null) || (technicsArray.length == 0)) {
            return null;
        }
        for (int i = 0; i < technicsArray.length; i++) {
//			if (technicsArray[i].startsWith(technicsNumber.replaceAll("/", "~")
//					+ "`")) {
            if (technicsArray[i].equals(technicsNumber.toUpperCase())) {
                String technicsDirectory = getReworkTechnicsRootPath()
                        + technicsArray[i] + File.separator + technicsArray[i]
                        + ".xml";
                File file = new File(technicsDirectory);
                if (file.exists()) {
                    directories.add(technicsDirectory);
                }
            }
        }
        return directories;
    }

    public static String getReworkTechnicsDirectory(String technicsNumber,
                                                    String technicsName) {
        String[] technicsArray = getReworkTechnicsDirectoryNames();
        if ((technicsArray == null) || (technicsArray.length == 0)) {
            return null;
        }
        for (int i = 0; i < technicsArray.length; i++) {
//			if (technicsArray[i].startsWith(technicsNumber.replaceAll("/", "~")
//					+ "`")
//					&& technicsArray[i].contains(technicsName.replaceAll("/",
//							"~"))) {
            if (technicsArray[i].equals(technicsNumber.toUpperCase()) && technicsArray[i].contains(technicsName)) {
                String technicsDirectory = getReworkTechnicsRootPath()
                        + technicsArray[i];
                File file = new File(technicsDirectory);
                if (file.exists()) {
                    return technicsDirectory;
                }
            }
        }
        return null;
    }

    public static String getTempTechnicsDirectory(String technicsNumber,
                                                  String technicsName) {
        String[] technicsArray = getTempTechnicsDirectoryNames();
        if ((technicsArray == null) || (technicsArray.length == 0)) {
            return null;
        }
        for (int i = 0; i < technicsArray.length; i++) {
//			if (technicsArray[i].startsWith(technicsNumber.replaceAll("/", "~")
//					+ "`")
//					&& technicsArray[i].contains(technicsName.replaceAll("/",
//							"~"))) {
            if (technicsArray[i].equals(technicsNumber.toUpperCase()) && technicsArray[i].contains(technicsName)) {
                String technicsDirectory = getTempTechnicsRootPath()
                        + technicsArray[i];
                File file = new File(technicsDirectory);
                if (file.exists()) {
                    return technicsDirectory;
                }
            }
        }
        return null;
    }

//	public static String getTechnicsNumber(String fileCode,String partNumber){
//		return fileCode.trim().toUpperCase()+"_"+partNumber;
//	}

    public static String getTechnicsDirectory(String technicsNumber) {
        if (technicsNumber == null) {
            return null;
        }
        String[] technicsArray = getTechnicsDirectoryNames();
        if ((technicsArray == null) || (technicsArray.length == 0)) {
            return null;
        }
        for (int i = 0; i < technicsArray.length; i++) {
//			if (technicsArray[i].startsWith(technicsNumber.replaceAll("/", "~")
//					+ "`"))

            if (technicsArray[i].equalsIgnoreCase(technicsNumber)) {
                String technicsDirectory = getCommonTechnicsRootPath() + technicsArray[i];
                File file = new File(technicsDirectory);
                if (file.exists()) {
                    return technicsDirectory;
                }
            }
        }
        return null;
    }

    public static String getMesTechnicsDirectory(String technicsNumber) {
        if (technicsNumber == null) {
            return null;
        }
        String[] technicsArray = getMesTechnicsDirectoryNames();
        if ((technicsArray == null) || (technicsArray.length == 0)) {
            return null;
        }
        for (int i = 0; i < technicsArray.length; i++) {
//			if (technicsArray[i].startsWith(technicsNumber.replaceAll("/", "~")
//					+ "`"))

            if (technicsArray[i].equalsIgnoreCase(technicsNumber)) {
                String technicsDirectory = getMesTempletRootPath() + technicsArray[i];
                File file = new File(technicsDirectory);
                if (file.exists()) {
                    return technicsDirectory;
                }
            }
        }
        return null;
    }

    public static String[] getProductDictoryNames() throws Exception {
        File productRoot = new File(getProductRootPath());
        File[] array = productRoot.listFiles(directoryFilter);
        String[] stringArray = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            stringArray[i] = array[i].getName();
        }
        return stringArray;
    }

    public static String[] getProductDictoryNumbers() throws Exception {
        String[] productDictoryNames = getProductDictoryNames();
        String[] numbers = new String[productDictoryNames.length];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = productDictoryNames[i].split("`")[0];
        }
        return numbers;
    }

    public static String[] getTechnicsDirectoryNames() {
        File technicsRoot = new File(getCommonTechnicsRootPath());
        File[] array = technicsRoot.listFiles(directoryFilter);
        String[] stringArray = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            stringArray[i] = array[i].getName();
        }
        return stringArray;
    }

    public static String[] getMesTechnicsDirectoryNames() {
        File technicsRoot = new File(getMesTempletRootPath());
        File[] array = technicsRoot.listFiles(directoryFilter);
        String[] stringArray = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            stringArray[i] = array[i].getName();
        }
        return stringArray;
    }

    public static String[] getReworkTechnicsDirectoryNames() {
        File technicsRoot = new File(getReworkTechnicsRootPath());
        File[] array = technicsRoot.listFiles(directoryFilter);
        if (array == null) {
            return new String[0];
        }
        String[] stringArray = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            stringArray[i] = array[i].getName();
        }
        return stringArray;
    }

    public static String[] getTempTechnicsDirectoryNames() {
        File technicsRoot = new File(getTempTechnicsRootPath());
        File[] array = technicsRoot.listFiles(directoryFilter);
        if (array == null) {
            return new String[0];
        }
        String[] stringArray = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            stringArray[i] = array[i].getName();
        }
        return stringArray;
    }

    public static String[] getTempletDirectoryNames() throws Exception {
        File templetRoot = new File(getTempletRootPath());
        File[] array = templetRoot.listFiles();
        String[] stringArray = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            stringArray[i] = array[i].getName();
        }
        return stringArray;
    }

    /**
     * 获取工艺路径
     *
     * @param technicsNumber
     * @return
     * @throws Exception
     */
    public static String getTechnicsPath(String technicsNumber) {
        technicsNumber = technicsNumber.toUpperCase();
        String technicsPathDirectory = getTechnicsDirectory(technicsNumber);
        if (technicsPathDirectory == null || "".equals(technicsPathDirectory)) {
            return null;
        }
        File dir = new File(technicsPathDirectory);
        String[] technicsArray = dir.list();
        if ((technicsArray == null) || (technicsArray.length == 0)) {
            return null;
        }
        for (int i = 0; i < technicsArray.length; i++) {
            if (technicsArray[i].endsWith(".xml")) {
//				if (technicsArray[i].startsWith(technicsNumber.replaceAll("/", "~") + "`")) {
                String fn = technicsArray[i].substring(0, technicsArray[i].lastIndexOf("."));
                if (fn.equals(technicsNumber.toUpperCase())) {
                    String technicsPath = technicsPathDirectory + File.separator + technicsArray[i];
                    File file = new File(technicsPath);
                    if ((file.exists()) && (file.isFile()))
                        return technicsPath;
                }
            }
        }
        return null;
    }

    public static String getMesTechnicsPath(String technicsNumber) {
        technicsNumber = technicsNumber.toUpperCase();
        String technicsPathDirectory = getMesTechnicsDirectory(technicsNumber);
        if (technicsPathDirectory == null || "".equals(technicsPathDirectory)) {
            return null;
        }
        File dir = new File(technicsPathDirectory);
        String[] technicsArray = dir.list();
        if ((technicsArray == null) || (technicsArray.length == 0)) {
            return null;
        }
        for (int i = 0; i < technicsArray.length; i++) {
            if (technicsArray[i].endsWith(".xml")) {
//				if (technicsArray[i].startsWith(technicsNumber.replaceAll("/", "~") + "`")) {
                String fn = technicsArray[i].substring(0, technicsArray[i].lastIndexOf("."));
                if (fn.equals(technicsNumber.toUpperCase())) {
                    String technicsPath = technicsPathDirectory + File.separator + technicsArray[i];
                    File file = new File(technicsPath);
                    if ((file.exists()) && (file.isFile()))
                        return technicsPath;
                }
            }
        }
        return null;
    }

    public static List<String> getReworkTechnicsPath(String technicsNumber) {
        List<String> paths = new ArrayList<String>();
        List<String> directories = getReworkTechnicsDirectories(technicsNumber);
        if (directories == null) {
            return null;
        }
        for (String technicsPathDirectory : directories) {
            String[] technicsArray = new File(technicsPathDirectory).list();
            if ((technicsArray == null) || (technicsArray.length == 0)) {
                continue;
            }
            for (int i = 0; i < technicsArray.length; i++) {
                if (technicsArray[i].endsWith(".xml")) {
                    String technicsFolderName = technicsArray[i];
//					if (technicsFolderName.startsWith(technicsNumber
//							.replaceAll("/", "~") + "`")) {
                    if (technicsArray[i].equals(technicsNumber.toUpperCase())) {
                        String technicsPath = technicsPathDirectory + File.separator + technicsArray[i];
                        File file = new File(technicsPath);
                        if ((file.exists()) && (file.isFile())) {
                            paths.add(technicsPath);
                        }
                    }
                }
            }
        }

        return paths;
    }

    public static List<String> getTempTechnicsPaths(String technicsNumber) {
        List<String> paths = new ArrayList<String>();
        List<String> directories = getTempTechnicsDirectories(technicsNumber);
        if (directories == null) {
            return null;
        }
        for (String technicsPathDirectory : directories) {
            String[] technicsArray = new File(technicsPathDirectory).list();
            if ((technicsArray == null) || (technicsArray.length == 0)) {
                continue;
            }
            for (int i = 0; i < technicsArray.length; i++) {
                if (technicsArray[i].endsWith(".xml")) {
                    String technicsFolderName = technicsArray[i];
//					if (technicsFolderName.startsWith(technicsNumber
//							.replaceAll("/", "~") + "`")) {
                    if (technicsArray[i].equals(technicsNumber.toUpperCase())) {
                        String technicsPath = technicsPathDirectory + File.separator + technicsArray[i];
                        File file = new File(technicsPath);
                        if ((file.exists()) && (file.isFile())) {
                            paths.add(technicsPath);
                        }
                    }
                }
            }
        }

        return paths;
    }

    public static List<Integer> getReworkTechnicsNumbers(String technicsNumber) {
        List<Integer> numbers = new ArrayList<Integer>();
        List<String> directories = getReworkTechnicsDirectories(technicsNumber);
        if (directories == null) {
            return null;
        }
        for (String technicsPathDirectory : directories) {
            String[] technicsArray = new File(technicsPathDirectory).list();
            if ((technicsArray == null) || (technicsArray.length == 0)) {
                return null;
            }
            for (int i = 0; i < technicsArray.length; i++) {
                if (technicsArray[i].endsWith(".xml")) {
                    String technicsFolderName = technicsArray[i];
//					if (technicsFolderName.startsWith(technicsNumber
//							.replaceAll("/", "~") + "`")) {
                    if (technicsArray[i].equals(technicsNumber.toUpperCase())) {
                        String technicsPath = technicsPathDirectory + File.separator + technicsArray[i];
                        File file = new File(technicsPath);
                        if ((file.exists()) && (file.isFile())) {
                            int index = technicsFolderName.indexOf("_fg");
                            String number = technicsFolderName.substring(
                                    index + 3, index + 5);
                            numbers.add(new Integer(number));
                        }
                    }
                }
            }
        }

        return numbers;
    }

    public static List<Integer> getTempTechnicsNumbers(String technicsNumber) {
        List<Integer> numbers = new ArrayList<Integer>();
        List<String> directories = getTempTechnicsDirectories(technicsNumber);
        if (directories == null) {
            return null;
        }
        for (String technicsPathDirectory : directories) {
            String[] technicsArray = new File(technicsPathDirectory).list();
            if ((technicsArray == null) || (technicsArray.length == 0)) {
                return null;
            }
            for (int i = 0; i < technicsArray.length; i++) {
                if (technicsArray[i].endsWith(".xml")) {
                    String technicsFolderName = technicsArray[i];
//					if (technicsFolderName.startsWith(technicsNumber
//							.replaceAll("/", "~") + "`")) {
                    if (technicsArray[i].equals(technicsNumber.toUpperCase())) {
                        String technicsPath = technicsPathDirectory + File.separator + technicsArray[i];
                        File file = new File(technicsPath);
                        if ((file.exists()) && (file.isFile())) {
                            int index = technicsFolderName.indexOf("_ls");
                            String number = technicsFolderName.substring(
                                    index + 3, index + 5);
                            numbers.add(new Integer(number));
                        }
                    }
                }
            }
        }

        return numbers;
    }

//	public static Vector getTechnicsDocumentByProductNumber(String productNumber)
//			throws Exception {
//		Vector vector = new Vector();
//		String[] s = getTechnicsDirectoryNames();
//		for (int i = 0; i < s.length; i++) {
//			String[] ss = s[i].split("`");
//			if (ss[4].equals(productNumber))
//				vector.add(getTechnicsDocumentByTechnicsNumber(ss[0]));
//		}
//		return vector;
//	}
//
//	public static Vector getTechnicsDocumentByPartNumber(String partNumber)
//			throws Exception {
//		Vector vector = new Vector();
//		String[] s = getTechnicsDirectoryNames();
//		for (int i = 0; i < s.length; i++) {
//			String[] ss = s[i].split("`");
//			if (ss[3].equals(partNumber))
//				vector.add(getTechnicsDocumentByTechnicsNumber(ss[0]));
//		}
//		return vector;
//	}
//
//	public static Vector getTechnicsDocumentByPartNumber(String partNumber,
//			String productNumber) throws Exception {
//		Vector vector = new Vector();
//		String[] s = getTechnicsDirectoryNames();
//		for (int i = 0; i < s.length; i++) {
//			String[] ss = s[i].split("`");
//			if ((ss[3].equals(partNumber)) && (ss[4].equals(productNumber)))
//				vector.add(getTechnicsDocumentByTechnicsNumber(ss[0]));
//		}
//		return vector;
//	}

    public static Document getReWorkTechnicsDocumentByTechnicsName(
            String technicsNumber, String technicsName) {
        List<String> paths = getReworkTechnicsPath(technicsNumber);
        if (paths == null) {
            return null;
        }
        for (String temp : paths) {
            String directory = temp.substring(temp.lastIndexOf(File.separator) + 1);
            String prefix = technicsNumber.replaceAll("/", "~") + "`" + technicsName.replaceAll("/", "~");
            System.out.println("prefix= " + prefix);
            System.out.println("directory= " + directory);
//			if (directory.startsWith(prefix)) {
            if (directory.equals(technicsNumber.toUpperCase())) {
                System.out.println("tempPath= " + temp);
                return XmlUtility.getDocument(temp);
            }
        }
        return null;
    }

    public static Document getTempTechnicsDocumentByTechnicsName(
            String technicsNumber, String technicsName) {
        List<String> paths = getTempTechnicsPaths(technicsNumber);
        if (paths == null) {
            return null;
        }
        for (String temp : paths) {
            String directory = temp
                    .substring(temp.lastIndexOf(File.separator) + 1);
            String prefix = technicsNumber.replaceAll("/", "~") + "`"
                    + technicsName.replaceAll("/", "~");
            System.out.println("prefix= " + prefix);
            System.out.println("directory= " + directory);
//			if (directory.startsWith(prefix)) {
            if (directory.equals(technicsNumber.toUpperCase())) {
                System.out.println("tempPath= " + temp);
                return XmlUtility.getDocument(temp);
            }
        }
        return null;
    }

    public static Document getReWorkTechnicsDocumentByTechnicsPath(
            String technicsPath) throws Exception {
        File file = new File(technicsPath);
        if (file.exists()) {
            for (File temp : file.listFiles()) {
                String fileName = temp.getName();
                if (fileName != null && fileName.endsWith(".xml")
                        && !"technics_route.xml".equals(fileName)) {
                    return XmlUtility.getDocument(temp.getAbsoluteFile());
                }
            }
        }
        return null;
    }

    public static Document getTempTechnicsDocumentByTechnicsPath(
            String technicsPath) throws Exception {
        File file = new File(technicsPath);
        if (file.exists()) {
            for (File temp : file.listFiles()) {
                String fileName = temp.getName();
                if (fileName != null && fileName.endsWith(".xml")
                        && !"technics_route.xml".equals(fileName)) {
                    return XmlUtility.getDocument(temp.getAbsoluteFile());
                }
            }
        }
        return null;
    }

    public static String getReWorkTechnicsPathByTechnicsName(
            String technicsNumber, String technicsName) {
        List<String> paths = getReworkTechnicsPath(technicsNumber);
        for (String temp : paths) {
            String directory = temp
                    .substring(temp.lastIndexOf(File.separator) + 1);
            String prefix = technicsNumber.replaceAll("/", "~") + "`"
                    + technicsName.replaceAll("/", "~");
//			if (directory.startsWith(prefix)) {
            if (directory.equals(technicsNumber.toUpperCase())) {
                System.out.println("temp= " + temp);
                return temp;
            }
        }
        return null;
    }

    public static String getTempTechnicsPathByTechnicsName(
            String technicsNumber, String technicsName) {
        List<String> paths = getTempTechnicsPaths(technicsNumber);
        for (String temp : paths) {
            String directory = temp
                    .substring(temp.lastIndexOf(File.separator) + 1);
            String prefix = technicsNumber.replaceAll("/", "~") + "`"
                    + technicsName.replaceAll("/", "~");
//			if (directory.startsWith(prefix)) {
            if (directory.equals(technicsNumber.toUpperCase())) {
                System.out.println("temp= " + temp);
                return temp;
            }
        }
        return null;
    }

    public static Document getTechnicsDocumentByTechnicsNumber(String technicsNumber) {
        return XmlUtility.getDocument(getTechnicsPath(technicsNumber));
    }

    public static Document getMesTechnicsDocumentByTechnicsNumber(String technicsNumber) {
        return XmlUtility.getDocument(getMesTechnicsPath(technicsNumber));
    }

    public static String getWorkSpace() {
        if ((workSpace_path != null) && (workSpace_path.trim().length() > 0))
            return workSpace_path;
        FileReader fr;
        try {
            fr = new FileReader(PROPERTIES_FILEPATH);
            BufferedReader read = new BufferedReader(fr);
            String ss = read.readLine();
            fr.close();
            read.close();
            int i = ss.indexOf("=");
            String workSpace_path = ss.substring(i + 1);
            if (workSpace_path.equals("")) {
                JOptionPane.showMessageDialog(null, "获得空间错误！", "提示", 1);
            }
            return workSpace_path;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workSpace_path;
    }

    public static void createWorkSpace(String workSpaceDirectory)
            throws Exception {
        FileWriter writer = new FileWriter(PROPERTIES_FILEPATH);
        writer.write("workSpace=" + workSpaceDirectory);
        writer.close();
        workSpace_path = getWorkSpace();
    }

    public static void startTechnicsDiagram() throws Exception {
        String address = (String) mySettingMap.get("TechnicsDiagramToolInfo=");
        if ((address == null) || (address.trim().equals(""))) {
            throw new Exception("没有设置工艺简图工具启动程序");
        }
        execOpen(address);
    }

    public static void startMidModel() throws Exception {
        String address = (String) mySettingMap.get("MidModelToolInfo=");
        if ((address == null) || (address.trim().equals(""))) {
            throw new Exception("没有设置中间模型工具启动程序");
        }
        execOpen(address);
    }

    public static void startVideo() throws Exception {
        String address = (String) mySettingMap.get("VedioToolInfo=");
        if ((address == null) || (address.trim().equals(""))) {
            throw new Exception("没有设置可视化工具位置启动程序");
        }
        execOpen(address);
    }

    public static void startAssemblageCartoon() throws Exception {
        String address = (String) mySettingMap
                .get("AssemblageCartoonToolInfo=");
        if ((address == null) || (address.trim().equals(""))) {
            throw new Exception("没有设置装配动画工具位置启动程序");
        }
        execOpen(address);
    }

    public static void execOpen(String filePath) throws Exception {
        String execString = "rundll32.exe url.dll,FileProtocolHandler "
                + filePath;
        try {
            Runtime.getRuntime().exec(execString);
        } catch (Exception e) {

            throw new Exception("打开文件发生错误");
        }
    }

    private static String getKeyValue(int row, String Key) throws Exception {
        FileReader fr = new FileReader(MYSETTING_FILENAME);
        BufferedReader read = new BufferedReader(fr);
        String tr = null;

        Vector vv = new Vector();
        String c2;
        while ((c2 = read.readLine()) != null) {
            vv.addElement(c2);
        }
        fr.close();
        read.close();
        if (vv.size() > 0) {
            tr = (String) vv.elementAt(row - 1);
            int a = tr.indexOf(Key);
            return tr.substring(a + 1);
        }
        return "";
    }

    public static boolean isExistWrokSpace() throws Exception {
        File file = new File(PROPERTIES_FILEPATH);
        if (!file.exists())
            return false;
        if (file.exists()) {
            FileReader fr = new FileReader(PROPERTIES_FILEPATH);
            BufferedReader read = new BufferedReader(fr);
            String ss = read.readLine();
            fr.close();
            read.close();
            int i = ss.indexOf("=");
            String workSpace_path = ss.substring(i + 1);
            if (!workSpace_path.equals(""))
                return true;
        }
        return false;
    }

    public static void delete(File file) {
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                delete(fileList[i]);
            }
            file.delete();
        } else {
            file.delete();
        }
    }

    public static File copyTechnicsToTemplet(Element technicsElement,
                                             String templetName,boolean isCanZhuang,boolean isPaiZhaodian) throws Exception {
        String technicsNumber = technicsElement.attributeValue("technicsNumber");
        String tecnnicsDirectory = getTechnicsDirectory(technicsNumber);
        if (tecnnicsDirectory == null)
            return null;
        String templateName = technicsElement.attributeValue("technicsType");
        String templetDirectory = null;
//		if (templateName.equals("装配工艺")) {
//			templetDirectory = getAssembleTempletPath();
//		} else {
//			templetDirectory = getPartTempletPath();
//		}
        for (int i = 0; i < technicsType[1].length; i++) {
            if (technicsType[1][i].equals(templateName)) {
                templetDirectory = getTempletPath(technicsType[0][i]);
                break;
            }
        }

        File newTemplet = new File(templetDirectory + File.separator + templetName);
        if (newTemplet.exists()) {
            FilesUtil.delFolder(newTemplet.getAbsolutePath());
        } else
            newTemplet.mkdir();
        FilesUtil.copyDirectiory(tecnnicsDirectory, newTemplet.getAbsolutePath());
        String technicsXml = getTechnicsPath(technicsNumber);

        File[] file = newTemplet.listFiles();

        for (int i = 0; i < file.length; i++) {
            if ((file[i].isFile()) && (technicsXml.endsWith(file[i].getName()))) {
                String sourceFileName = tecnnicsDirectory
                        + File.separator
                        + tecnnicsDirectory.substring(
                        tecnnicsDirectory.lastIndexOf(File.separator),
                        tecnnicsDirectory.length());
                if (!sourceFileName.toLowerCase().endsWith(".xml"))
                    sourceFileName = sourceFileName + ".xml";
                FilesUtil.repalce(sourceFileName, file[i], templetName, "xml");
                break;
            }
        }

        /**移除参装件及质量记录表信息*/
        removeQualityElement(newTemplet.getAbsolutePath()+File.separator+templetName+".xml",isCanZhuang,isPaiZhaodian);
        return newTemplet;
    }

    /**
     * 移除参装件及质量记录表信息
     *
     * @param path
     * @throws Exception
     */
    public static void removeQualityElement(String path,boolean isSaveCanZhuang,boolean isSavePaiZhaodian) throws Exception {
        System.out.println("isSaveCanZhuang:"+isSaveCanZhuang);
        System.out.println("isSavePaiZhaodian:"+isSavePaiZhaodian);
        System.out.println("path:"+path);
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(path));
        Element root = document.getRootElement();
        List<Element> stepElements = root.selectNodes("QMFawTechnicsInfo/steps/QMProcedureInfo");
        for (Element stepElement : stepElements) {

            if(!isSaveCanZhuang){
                Element stepParts = stepElement.element("parts");
                stepElement.remove(stepParts);
                Element stepCommonParamTables = stepElement.element("commonParamTables");
                Element stepsSpecialParamTables = stepElement.element("specialParamTables");
                stepElement.remove(stepCommonParamTables);
                stepElement.remove(stepsSpecialParamTables);
            }
            if(!isSavePaiZhaodian){
                Element photoRecords = stepElement.element("photoRecords");
                stepElement.remove(photoRecords);
            }


            List<Element> paceElements = stepElement.selectNodes("paces/QMProcedureInfo");
            for (Element paceElement : paceElements) {

                if(!isSaveCanZhuang){
                    Element paceParts = paceElement.element("parts");
                    paceElement.remove(paceParts);
                    Element paceCommonParamTables = paceElement.element("commonParamTables");
                    Element pacesSpecialParamTables = paceElement.element("specialParamTables");
                    paceElement.remove(paceCommonParamTables);
                    paceElement.remove(pacesSpecialParamTables);
                }
                if(!isSavePaiZhaodian){
                    Element photoRecords = paceElement.element("photoRecords");
                    paceElement.remove(photoRecords);
                }
//                Element schemaData = paceElement.element("schemaData");
//                paceElement.remove(schemaData);
            }
        }

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("GBK");
        FileOutputStream fos = new FileOutputStream(path);
        XMLWriter writer = new XMLWriter(fos, format);
        writer.write(document);
        writer.close();
        fos.close();
//        System.out.println("------ok--------");
    }

    public static String getPersonalTerminologyDirectory() {
        String terminologyDirectory = getWorkSpace() + File.separator + "resource" + File.separator
                + "terminology" + File.separator;
        File file = new File(terminologyDirectory);
        if (!file.exists()) {
            file.mkdirs();
        }
        return terminologyDirectory;
    }

    public static void main(String[] args) throws Exception {
    }

    public static String getTempTechnicsPath(String technicsNumber,
                                             String technicsName, String technicsCategory) throws Exception {
        String technicsDirectory = "";
        if ("rework".equals(technicsCategory)) {
            technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(
                    technicsNumber, technicsName);
        } else if ("temp".equals(technicsCategory)) {
            technicsDirectory = WorkSpaceUtil.getTempTechnicsDirectory(
                    technicsNumber, technicsName);
        } else {
            technicsDirectory = WorkSpaceUtil
                    .getTechnicsDirectory(technicsNumber);
        }
        return getTempRootPath()
                + File.separator
                + technicsDirectory.substring(
                technicsDirectory.lastIndexOf(File.separator) + 1,
                technicsDirectory.length());
    }

    static class DirectoryFilter implements FileFilter {
        public boolean accept(File file) {
//			if ((file != null) && (file.isDirectory())
//					&& (file.getName().contains("`"))) {
//				return true;
//			}
//
//			return false;
            return true;
        }
    }

    public static void deleteTechnicsDirectory() {
        String path = WorkSpaceUtil.getCommonTechnicsRootPath();
//        System.out.println("-------------path--" + path);
        File file = new File(path);
        if (file.exists()) {
            FileUtil.deleteSubFile(file);
        }
        path = WorkSpaceUtil.getReworkTechnicsRootPath();
        file = new File(path);
        if (file.exists()) {
            FileUtil.deleteSubFile(file);
        }
        path = WorkSpaceUtil.getTempTechnicsRootPath();
        file = new File(path);
        if (file.exists()) {
            FileUtil.deleteSubFile(file);
        }

        path = WorkSpaceUtil.getTempRootPath();
        file = new File(path);
        if (file.exists()) {
            FileUtil.deleteSubFile(file);
        }
    }

    /**
     * 复制指定文件夹下的所有文件到指定的目录
     *
     * @param originDirectory 指定的源目录
     * @param targetDirectory 指定的目标目录
     * @throws IOException
     */
    public static void copyDir2Dir(String originDirectory, String targetDirectory) throws IOException {
        File origindirectory = new File(originDirectory); // 源路径File实例
        File targetdirectory = new File(targetDirectory); // 目标路径File实例
        if (!origindirectory.isDirectory() || !targetdirectory.isDirectory()) { // 判断是不是正确的路径
            System.out.println("不是正确的目录！");
            return;
        }
        File[] fileList = origindirectory.listFiles(); // 目录中的所有文件
        for (File file : fileList) {
            if (!file.isFile()) {// 判断是不是文件
                copyDirectiory(file.getPath(), targetdirectory + File.separator + file.getName());
            } else {
                //System.out.println(file.getName());
                FileInputStream fin = new FileInputStream(file);
                BufferedInputStream bin = new BufferedInputStream(fin);
                PrintStream pout = new PrintStream(targetdirectory.getAbsolutePath() + File.separator + file.getName());
                BufferedOutputStream bout = new BufferedOutputStream(pout);
                try {
                    int total = bin.available(); // 文件的总大小
                    int percent = total / 100; // 文件总量的百分之一
                    int count;
                    while ((count = bin.available()) != 0) {
                        int c = bin.read(); // 从输入流中读一个字节
                        bout.write((char) c); // 将字节（字符）写到输出流中

                        if (((total - count) % percent) == 0) {
                            double d = (double) (total - count) / total; // 必须强制转换成double
                            //System.out.println(Math.round(d * 100) + "%"); // 输出百分比进度
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bout != null) {
                        bout.close();
                    }
                    if (pout != null) {
                        pout.close();
                    }
                    if (bin != null) {
                        bin.close();
                    }
                    if (fin != null) {
                        fin.close();
                    }
                }
            }
        }
    }

    public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {

        // 新建目标目录
        (new File(targetDir)).mkdirs();

        // 获取源文件夹当下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();
        if (file == null) {
            return;
        }
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                File sourceFile = file[i];
                // 目标文件
                File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());

                copyFile(sourceFile, targetFile);
            }

            if (file[i].isDirectory()) {
                // 准备复制的源文件夹
                String dir1 = sourceDir + file[i].getName();
                // 准备复制的目标文件夹
                String dir2 = targetDir + File.separator + file[i].getName();

                copyDirectiory(dir1, dir2);
            }
        }
    }

    public static void copyFile(File sourcefile, File targetFile) throws IOException {

        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourcefile);
        BufferedInputStream inbuff = new BufferedInputStream(input);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream out = new FileOutputStream(targetFile);
        BufferedOutputStream outbuff = new BufferedOutputStream(out);

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len = 0;
        while ((len = inbuff.read(b)) != -1) {
            outbuff.write(b, 0, len);
        }

        // 刷新此缓冲的输出流
        outbuff.flush();

        // 关闭流
        inbuff.close();
        outbuff.close();
        out.close();
        input.close();

    }

    public static String getTechnicsPath(Element techEle) {
        String technicsCategory = techEle.attributeValue("technicsCategory");
        String technicsName = techEle.attributeValue("technicsName");
        String technicsNumber = techEle.attributeValue("technicsNumber");
        String techPath;
        if ("rework".equals(technicsCategory)) {
            techPath = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber,
                    technicsName);
        } else if ("temp".equals(technicsCategory)) {
            techPath = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber,
                    technicsName);
        } else {
            techPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
        }
        return techPath;
    }

    /**
     * 中间模型简图位置
     *
     * @return
     */
    public static String makeCmpTmpDir() {
        return FileUtil.makeTmpDir("mpm/cad/cmpview/");
    }

    public static String getCmpTmpDir() {
        return FileUtil.getTmpPath() + "/mpm/cad/cmpview/";
    }

    /**
     * 根据类别编号名称获取路径
     *
     * @param technicsCategory
     * @param technicsName
     * @param technicsNumber
     * @return
     */
    public static String getTechnicsDirectoryByCategory(Element techElement) {
        return getTechnicsDirectoryByCategory(
                techElement.attributeValue("technicsCategory"),
                techElement.attributeValue("technicsName"),
                techElement.attributeValue("technicsNumber"));
    }

    /**
     * 根据类别编号名称获取路径
     *
     * @param technicsCategory
     * @param technicsName
     * @param technicsNumber
     * @return
     */
    public static String getTechnicsDirectoryByCategory(
            String technicsCategory, String technicsName, String technicsNumber) {
        String techPath;
        if ("rework".equals(technicsCategory)) {
            techPath = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber,
                    technicsName);
        } else if ("temp".equals(technicsCategory)) {
            techPath = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber,
                    technicsName);
        } else {
            techPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
        }
        return techPath;
    }

    public static String setJsxyPath(String number) {
        String path = "";
        if (number != null && !"".equals(number) && !"null".equals(number)) {
            number = number.replace("/", "_");
            path = WorkSpaceUtil.getWorkSpace() + File.separator + "technics" + File.separator + number + File.separator;
        }
        return path;
    }

    public static String getJsxyPath(String number) {
        String path = "";
        if (number != null && !"".equals(number) && !"null".equals(number)) {
            number = number.replace("/", "_");
            path = WorkSpaceUtil.getWorkSpace() + File.separator + "technics" + File.separator + number + File.separator;
        }
        return path;
    }


}
