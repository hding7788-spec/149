package com.glaway.mpm.print.util;

import com.glaway.mpm.intf.PrintToWCIntfRMI;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.print.data.CmImportBean;
import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.PropertiesUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.util.WCUtil;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.httpgw.URLFactory;
import wt.util.WTException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import jxl.Sheet;
//import jxl.Workbook;
//import jxl.read.biff.BiffException;


public class InsideFileImport extends DefaultObjectFormProcessor {

    private static List<CmImportBean> cmImportBeanList;
    private static String tmp_dir = PropertiesUtil.getTempPath() + File.separator;
    static final int BUFFER = 2048;

    @Override
    public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> listBean) throws WTException {
        System.out.println("======================import insideFile start======================");
        FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
        Object fileMap = nmCommandBean.getMap().get("fileUploadMap");
        String fileName = nmCommandBean.getTextParameter("xlsFile");
        if (fileName.contains("\\")) {
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.length());
        }
        String message = "";
        if (fileMap != null) {
            Map<?, ?> map = (Map<?, ?>) fileMap;
            Object fileObj = (File) map.get("xlsFile");
            File file = null;
            if (fileObj instanceof File) {
                file = (File) fileObj;
                compressFile(file, fileName);
                // 读取保存在本地的EXCEL文件
                File xlsFile = new File(tmp_dir + fileName);
                try {
                    if (xlsFile != null) {
                        //1.读取excel数据
                        String errorMsg = getExcelInfo2(xlsFile);
                        //2.处理数据
                        String msg = PrintToWCIntfRMI.inFactoryImportInfoToDB2(cmImportBeanList);
                        message = errorMsg + "</br>" + msg;
                    } else {
                        System.out.println("file is not exist");
                    }
                } catch (Exception e) {
                    message = "读取文件信息异常</br>" + e.getLocalizedMessage() + "</br>";
                }
            }
        }

        URLFactory urlfactory = new URLFactory();
        nmCommandBean.getRequest().getSession().putValue("errorInfo", message);
        String url = urlfactory.getBaseHREF() + "netmarkets/jsp/ext/casc/importdata/importDataInfo.jsp?";
        result.setURL(url);
        result.setNextAction(FormResultAction.FORWARD);


//        result.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), "", null, "导入成功！"));
//        result.setURL("app/#ptc1/homepage");
        System.out.println("======================import insideFile end======================");
        return result;
    }

    public static void importInfoToDB(List<CmImportBean> list) throws RemoteException, InvocationTargetException {
        PrintToWCIntfRMI.inFactoryImportInfoToDB(list);
    }

//    public static List<CmImportBean> getExcelInfo(File file) throws WTException {
//        List<CmImportBean> listBean = new ArrayList<CmImportBean>();
//        Workbook workbook;
//        try {
//            workbook = Workbook.getWorkbook(file);
//            Sheet sheet = workbook.getSheet("Sheet1");
//            int rows = sheet.getRows();
//            for (int i = 1; i < rows; i++) {
//                if ("工艺文件".equals(sheet.getCell(0, i).getContents()) || "工装".equals(sheet.getCell(0, i).getContents())) {
//                    CmImportBean bean = new CmImportBean();
//                    bean.setFileType(sheet.getCell(0, i).getContents());
//                    bean.setFileNumber(sheet.getCell(1, i).getContents());
//                    bean.setFileName(sheet.getCell(2, i).getContents());
//                    bean.setVersion(sheet.getCell(3, i).getContents());
//                    bean.setDistributeDept(sheet.getCell(4, i).getContents());
//                    bean.setDistributeQuantity(sheet.getCell(5, i).getContents());
//                    listBean.add(bean);
//                }
//            }
//        } catch (BiffException e) {
//            throw new WTException("所选择的文件类型不符合规范！");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return listBean;
//    }

    public static void main(String[] args) {
        String file = "ssss.xlsx";
        String endIndex = file.substring(file.lastIndexOf("."), file.length());
        System.out.println(endIndex.toUpperCase());

    }

    private static String getExcelInfo2(File file) throws Exception {
        System.out.println("get excel info start");
        cmImportBeanList = new ArrayList<CmImportBean>();
        StringBuilder sb = new StringBuilder();
        Workbook workbook = null;
        InputStream is;
        CmImportBean cmImportBean;
//        String fileEndIndex = file.getName().substring(file.getName().lastIndexOf("."), file.getName().length());
        try {
            is = new FileInputStream(file);
            if (file.getName().endsWith("xls")) {
                workbook = new HSSFWorkbook(is);
            } else if (file.getName().endsWith("xlsx")) {
                workbook = new XSSFWorkbook(is);
            } else {
                return "该文件不是EXCEL,或格式不正确 </br>";
            }

            Sheet sheet = workbook.getSheetAt(0);
            int rows = sheet.getLastRowNum() - sheet.getFirstRowNum();
            Row row = null;
            for (int i = 3; i <= rows; i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String technicsPPNumber = "";
                if (row.getCell(1) != null) {
                    technicsPPNumber = getValue(row.getCell(1)).toString();//工艺文件编号
                    if (technicsPPNumber == null || technicsPPNumber.isEmpty()) {
                        sb.append("第").append(i + 1).append("行未读取到工艺文件编号;</br>");
                        continue;
                    }
                } else {
                    sb.append("第").append(i + 1).append("行未读取到工艺文件编号;</br>");
                    continue;
                }
                String distributeDept = "";
                if (row.getCell(6) != null) {
                    distributeDept = getValue(row.getCell(6)).toString();//分发部门
                    if (distributeDept == null || distributeDept.isEmpty()) {
                        sb.append("第").append(i + 1).append("行未读取到分发部门;</br>");
                        continue;
                    } else {
                        distributeDept = getDept(distributeDept);
                        if (distributeDept == null) {
                            sb.append("第").append(i + 1).append("行分发部门填写有误;</br>");
                            continue;
                        }
                    }
                } else {
                    sb.append("第").append(i + 1).append("行未读取到分发部门;</br>");
                    continue;
                }
                String distributeCount = "";
                if (row.getCell(7) != null) {
                    distributeCount = getValue(row.getCell(7)).toString();//分发份数
                    if (distributeCount == null || distributeCount.isEmpty()) {
                        sb.append("第").append(i + 1).append("行未读取到分发份数;</br>");
                        continue;
                    }
                } else {
                    sb.append("第").append(i + 1).append("行未读取到分发份数;</br>");
                    continue;
                }

                WTDocument document = WCUtil.getDocumentByIBANumber(technicsPPNumber);
                if (document == null) {
                    sb.append("第").append(i + 1).append("行未找到相应的工艺文件;</br>");
                    continue;
                }
                String number = document.getNumber();
                boolean isExist = checkIsExist(number, distributeDept);
                if (isExist) {
                    sb.append("第").append(i + 1).append("行工艺文件在数据库中已存在;</br>");
                    continue;
                }
                String name = document.getName();
                String version = document.getIterationDisplayIdentifier().toString();
                String oid = "OR:" + PersistenceHelper.getObjectIdentifier(document).toString();
                IBAHelper ibaHelper = new IBAHelper(document);
                String phase = PDFUtil.objectToString(ibaHelper.getIBAValue("PHASECODE"));
                String secret = PDFUtil.objectToString(ibaHelper.getIBAValue("SECRET"));
//                String batch = PDFUtil.objectToString(ibaHelper.getIBAValue("BATCH"));

                cmImportBean = new CmImportBean();
                cmImportBean.setOid(oid);
                cmImportBean.setFileNumber(number);
                cmImportBean.setFileName(name);
                cmImportBean.setVersion(version);
                cmImportBean.setPhaseCode(phase);
                cmImportBean.setSecret(secret);
                cmImportBean.setBatch("");
                cmImportBean.setFileType("工艺规程");
                cmImportBean.setOutDept("厂内电子");
                cmImportBean.setDistributeQuantity(distributeCount);
                cmImportBean.setDistributeDept(distributeDept);
                cmImportBean.setIndex((i + 1) + "");
                cmImportBeanList.add(cmImportBean);
            }
        } catch (IOException e) {
            return "读取文件错误</br>" + e.getLocalizedMessage() + "</br>";
        }
        System.out.println("get excel info end " + cmImportBeanList.size());
        return sb.toString();
    }

    private static Object getValue(Cell cell) {
        Object obj = null;
        switch (cell.getCellType()) {
            case 4:
                obj = cell.getBooleanCellValue();
                break;
            case 5:
                obj = cell.getErrorCellValue();
                break;
            case 0:
                cell.setCellType(1);
                obj = cell.getStringCellValue();
                break;
            case 1:
                obj = cell.getStringCellValue();
                break;
            case 2:
                obj = cell.getCellFormula();
                break;
            default:
                break;
        }
        return obj;
    }

    private static String getDept(String distributeDept) {
        if ("1".equals(distributeDept)) {
            distributeDept = "一分厂";
        } else if ("2".equals(distributeDept)) {
            distributeDept = "二分厂";
        } else if ("3".equals(distributeDept)) {
            distributeDept = "三分厂";
        } else if ("4".equals(distributeDept)) {
            distributeDept = "四分厂";
        } else if ("5".equals(distributeDept)) {
            distributeDept = "五分厂";
        } else if ("6".equals(distributeDept)) {
            distributeDept = "六分厂";
        } else if ("7".equals(distributeDept)) {
            distributeDept = "七分厂";
        } else if ("8".equals(distributeDept)) {
            distributeDept = "八分厂";
        } else if ("9".equals(distributeDept)) {
            distributeDept = "九分厂";
        } else if ("10".equals(distributeDept)) {
            distributeDept = "十分厂";
        } else {
            distributeDept = null;
        }
        return distributeDept;
    }

    private static boolean checkIsExist(String docNumber, String dept) {
        String sql = "select count(*) from GWPRINTAPPLYRECORD where DOCNUMBER='" + docNumber + "' and DISMESSAGE like '%" + dept + "%'";
        DBConnUtil dbConnUtil = null;
        try {
            dbConnUtil = new DBConnUtil();
            ResultSet resultSet = dbConnUtil.executeQuery(sql);
            if (resultSet.next()) {
                int i = resultSet.getInt(1);
                if (i != 0) {
                    return true;
                } else {
                    return false;
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
        return false;
    }


    /**
     * 处理上载的文件，将其先保存在服务器端指定路径，然后再对其进行解压。
     */
    public void compressFile(File zipFile, String fileName) {

        File tempFile = new File(tmp_dir + fileName);
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            inBuff = new BufferedInputStream(new FileInputStream(zipFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(tempFile));

            // 缓冲数组
            byte[] b = new byte[BUFFER * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            try {
                if (outBuff != null) {
                    outBuff.close();
                }
                if (inBuff != null) {
                    inBuff.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
