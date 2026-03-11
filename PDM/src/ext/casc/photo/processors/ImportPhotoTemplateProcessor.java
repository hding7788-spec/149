package ext.casc.photo.processors;

import com.glaway.mpm.print.util.MBAUtil;
import com.glaway.mpm.util.*;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.constants.Constants;
import ext.casc.integrate.util.ZipUtil;
import ext.casc.sop.bean.SOPPartImportBean;
import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.util.SopPartUtil;
import ext.casc.sop.util.SopUtil;
import ext.casc.util.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.httpgw.URLFactory;
import wt.inf.container.WTContainer;
import wt.inf.library.WTLibrary;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.beans.PropertyVetoException;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportPhotoTemplateProcessor extends DefaultObjectFormProcessor {

    private static String tmp_dir = PropertiesUtil.getTempPath() + File.separator;
    static final int BUFFER = 2048;

    private static int COL_NAME = 0;
    private static int COL_PRODUCTNUMBER = 1;
    private static int COL_PSYQ = 2;
    private static int COL_PBZZ = 3;
    private static int COL_PHOTOTYPE = 4;
    private static int COL_PSDXTYPE = 5;
    private static int COL_PHOTOFILE = 6;
    //private static int COL_PHOTO_PDCJBH = 7;
    private static int COL_PHOTO_PDCJMC = 7;
    private static int index[] = { 0 };



    @Override
    public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> listBean) throws WTException {
        System.out.println("======================import PhotoTemplate start======================");
        FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
        Object fileMap = nmCommandBean.getMap().get("fileUploadMap");
        String fileName = nmCommandBean.getTextParameter("xlsFile");
        String photoFileName = nmCommandBean.getTextParameter("photoFile");
        if (fileName.contains("\\")) {
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.length());
        }
        if (photoFileName.contains("\\")) {
            photoFileName = photoFileName.substring(photoFileName.lastIndexOf("\\") + 1, photoFileName.length());
        }
        String message = "";
        if (fileMap != null) {
            Map<?, ?> map = (Map<?, ?>) fileMap;
            Object fileObj = map.get("xlsFile");
            Object photoObj = map.get("photoFile");
            File file;
            File photoFile;
            if (fileObj instanceof File && photoObj instanceof File) {
                file = (File) fileObj;
                photoFile = (File) photoObj;
                compressFile(file, fileName);
                compressFile(photoFile, photoFileName);
                // 读取保存在本地的EXCEL文件
                File xlsFile = new File(tmp_dir + fileName);
                File photoZip = new File(tmp_dir + photoFileName);
                String photoDir = tmp_dir+ "photoFile" + File.separator + photoFileName.replaceAll(".zip","");
                ZipUtil.unZip(photoZip.getAbsolutePath(),photoDir);
                try {
                    if (xlsFile.exists()) {
                        message = getExcelInfo(xlsFile,photoDir);
                        if ("".equals(message)) {
                            message = importPhotoTemplate(xlsFile,photoDir);
                        }
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

        System.out.println("======================import insideFile end======================");
        return result;
    }

    private static String importPhotoTemplate(File file,String photoDir) {
        StringBuilder sb = new StringBuilder();
        Workbook workbook = null;
        InputStream is;
        Transaction tx = null;
        try {
            tx = new Transaction();
            tx.start();
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
            Row row;
            for (int i = 1; i <= rows; i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String name = getValue(row.getCell(COL_NAME)).toString().trim();
                String productNumber = getValue(row.getCell(COL_PRODUCTNUMBER)).toString().trim();
                String psyq = getValue(row.getCell(COL_PSYQ)).toString().trim();
                String pbzz = getValue(row.getCell(COL_PBZZ)).toString().trim();
                String photoType = getValue(row.getCell(COL_PHOTOTYPE)).toString().trim();
                String psdxType = getValue(row.getCell(COL_PSDXTYPE)).toString().trim();
                String photoFileName = getValue(row.getCell(COL_PHOTOFILE)).toString().trim();
                //String pdchbh = getValue(row.getCell(COL_PHOTO_PDCJBH)).toString().trim();
                String pdchmc = getValue(row.getCell(COL_PHOTO_PDCJMC)).toString().trim();
                File photoFile = new File(photoDir+File.separator+photoFileName);;
                if(!photoFile.exists()){
                    sb.append("第" + i+1 + "行[" + name + "]未找到对应文件!</br>");
                    continue;
                }
                String zpyz =getZPYZSeqNumber("ZPYZ");
                String docNumber = "ZPYZ"+zpyz;
                String docType = "casc.sast.149.PhotoTemplate";
                String docFolder = "/Default/照片样张库/"+photoType.replaceAll("/","、");
                WTContainer container = WTContainerUtil.getContainerByName("工艺资源库");
                WTDocument doc = WTDocumentUtil.createDocument(docNumber,name,container,docFolder,docType);
                if(photoFile.exists()){
                    try {
                        FileInputStream stream = new FileInputStream(photoFile);
                        WTDocumentUtil.setPrimaryForDocument(doc, photoFileName, stream);
                        stream.close();
                    } catch (PropertyVetoException e) {
                        e.printStackTrace();
                    }
                }
                LifeCycleState state = LifeCycleState.newLifeCycleState();
                state.setState(State.toState("APPROVED"));
                doc.setState(state);
                doc = (WTDocument) PersistenceHelper.manager.refresh(doc);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("productNumber",productNumber);
                map.put("psyq",psyq);
                map.put("pbzz",pbzz);
                if(!Tools.isNull(pdchmc)) {
                    map.put("photoType", photoType);
                }
                map.put("psdxType",psdxType);
                if(!Tools.isNull(pdchmc)){
                    map.put("PDCJMC",pdchmc);
                    map.put( "PDCJBH", Constants.photoValueMap.get(pdchmc));
                }


                MBAUtil.setValue(doc,map);
                sb.append("第" + i+1 + "行[" + name + "]导入成功</br>");
            }
            is.close();
            tx.commit();
            tx = null;
        } catch (IOException e) {
            return "读取文件错误</br>" + e.getLocalizedMessage() + "</br>";
        } catch (WTException e) {
            e.printStackTrace();
            return "获取产品库失败</br>" + e.getLocalizedMessage() + "</br>";
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
            return "文件创建失败</br>" + e.getLocalizedMessage() + "</br>";
        } finally {
            if (tx != null) {
                tx.rollback();
            }
            deleteFiles(photoDir);
        }
        return sb.toString();
    }

    public static String getZPYZSeqNumber(String pre) {
		StringBuilder sql = new StringBuilder("select NUM FROM ");
		sql.append("GL_BBLGY_SEQ ").append("WHERE PRE='").append(pre).append("'");
		DBConn conn = null;
		long num = 0;
		try {
			conn = new DBConn();
			conn.start();
			ResultSet rs = conn.executeQuery(sql.toString());
			if (rs.next()) {
				num = rs.getLong("NUM");
			}
			if (num == 0) {
				num = 0000001;
				// 更新
				StringBuilder insertSql = new StringBuilder();
				insertSql.append("INSERT INTO GL_BBLGY_SEQ(PRE,NUM) ")
						.append("VALUES('").append(pre).append("',")
						.append("'").append(num).append("')");
				conn.executeUpdate(insertSql.toString());
				conn.commit();
			} else {
				num = num + 1;
				// 更新
				StringBuilder updateSql = new StringBuilder();
				updateSql.append("UPDATE GL_BBLGY_SEQ SET NUM='").append(num).append("'")
						.append(" WHERE PRE='").append(pre).append("'");
				conn.executeUpdate(updateSql.toString());
				conn.commit();
			}
		} catch (Exception e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return String.format("%0" + 7 + "d", num);
	}
    private static String getExcelInfo(File file,String photoDir) {
        System.out.println("get excel info start");
        StringBuilder sb = new StringBuilder();
        Workbook workbook = null;
        InputStream is;
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
            Row row;
            for (int i = 1; i <= rows; i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String name = getValue(row.getCell(COL_NAME)).toString().trim();
                if (name == null || name.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到名称;</br>");
                    continue;
                }
                String productNumber = getValue(row.getCell(COL_PRODUCTNUMBER)).toString().trim();
                if (productNumber == null || productNumber.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到产品图号;</br>");
                    continue;
                }
                String psyq = getValue(row.getCell(COL_PSYQ)).toString().trim();
                if (psyq == null || psyq.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到拍摄要求;</br>");
                    continue;
                }
                String pbzz = getValue(row.getCell(COL_PBZZ)).toString().trim();
                if (pbzz == null || pbzz.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到判别准则;</br>");
                    continue;
                }
                String photoType = getValue(row.getCell(COL_PHOTOTYPE)).toString().trim();
                if (photoType == null || photoType.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到分类;</br>");
                    continue;
                } else {
                    if(photoType.contains(",")){
                        String[] ss = photoType.split(",");
                        for(String s:ss){
                            if (!Constants.photoTypeList.contains(s)) {
                                sb.append("第").append(i + 1).append("行分类填写不规范，分类应为："+Constants.photoTypeList+";</br>");
                                continue;
                            }
                        }
                    }else{
                        if (!Constants.photoTypeList.contains(photoType)) {
                            sb.append("第").append(i + 1).append("行分类填写不规范，分类应为："+Constants.photoTypeList+";</br>");
                            continue;
                        }
                    }

                }
                String pdchmc = getValue(row.getCell(COL_PHOTO_PDCJMC)).toString().trim();
                if (pdchmc == null || pdchmc.isEmpty()) {
                    //sb.append("第").append(i + 1).append("行未读取到判定场景名称;</br>");
                    //continue;
                } else {
                    if (!Constants.photoValueList.contains(pdchmc)) {
                        sb.append("第").append(i + 1).append("行判定场景名称填写不规范，应为："+Constants.photoValueList+";</br>");
                        continue;
                    }
                }
                String psdxType = getValue(row.getCell(COL_PSDXTYPE)).toString().trim();
                if (psdxType == null || psdxType.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到拍摄对象分类;</br>");
                    continue;
                }
                String photoFileName = getValue(row.getCell(COL_PHOTOFILE)).toString().trim();
                if (photoFileName == null || photoFileName.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到照片样张名称;</br>");
                    continue;
                }else{
                    File photoFile = new File(photoDir+File.separator+photoFileName);
                    if(!photoFile.exists()){
                        sb.append("第").append(i + 1).append("行zip包中没有["+photoFileName+"]文件;</br>");
                        continue;
                    }
                }
            }
        } catch (IOException e) {
            return "读取文件错误</br>" + e.getLocalizedMessage() + "</br>";
        }
        return sb.toString();
    }

    private static Object getValue(Cell cell) {
        if (cell == null) {
            return "";
        }
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
        if (obj == null) {
            obj = "";
        }
        return obj;
    }


    /**
     * 处理上载的文件，将其先保存在服务器端指定路径，然后再对其进行解压。
     */
    public static void compressFile(File zipFile, String fileName) {

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

    public static boolean deleteFiles(String inputPath) {
        try {
            File f = new File(inputPath);
            if (f.isDirectory()) {
                File[] flist = f.listFiles();
                for (int i = 0; i < flist.length; i++) {
                    File tmpfile = (File) flist[i];
                    deleteFiles(tmpfile.getAbsolutePath());
                }
                f.delete();
            } else
                f.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
