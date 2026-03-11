package ext.casc.sop.resource;

import com.glaway.mpm.util.*;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.resource.MPMTooling;
import ext.casc.process.util.ProcessUtil;
import ext.casc.sop.bean.SOPPartImportBean;
import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.util.SopPartUtil;
import ext.casc.sop.util.SopUtil;
import ext.casc.util.IBAUtility;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wt.clients.epm.WTPartUtility;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.httpgw.URLFactory;
import wt.inf.library.WTLibrary;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.config.LatestConfigSpec;

import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ImportSOPPart extends DefaultObjectFormProcessor {

    private static String tmp_dir = PropertiesUtil.getTempPath() + File.separator;
    static final int BUFFER = 2048;
    private static List<SOPPartImportBean> sopPartImportBeanList;
    private static List<String> secretList = new ArrayList<String>();
    private static ArrayList<String> gongXuCheJian = new ArrayList<String>();

    private static int COL_NUMBER = 1;
    private static int COL_NAME = 2;
    private static int COL_SECRET = 3;
    private static int COL_TREM = 4;
    private static int COL_SPECIALIZEDTYPE = 5;
    private static int COL_PROCEDURENAME = 6;
    private static int COL_DEPARTMENT = 7;
    private static int index[] = { 0 };


    static {
        secretList.add("公开");
        secretList.add("商密");
        secretList.add("内部");
        secretList.add("秘密");
        secretList.add("机密");
        try {
            gongXuCheJian = ProcessUtil.getGongXuCheJian();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> listBean) throws WTException {
        System.out.println("======================import SOP start======================");
        FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
        Object fileMap = nmCommandBean.getMap().get("fileUploadMap");
        String fileName = nmCommandBean.getTextParameter("xlsFile");
        if (fileName.contains("\\")) {
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.length());
        }
        String message = "";
        if (fileMap != null) {
            Map<?, ?> map = (Map<?, ?>) fileMap;
            Object fileObj = map.get("xlsFile");
            File file;
            if (fileObj instanceof File) {
                file = (File) fileObj;
                compressFile(file, fileName);
                // 读取保存在本地的EXCEL文件
                File xlsFile = new File(tmp_dir + fileName);
                try {
                    if (xlsFile.exists()) {
                        String errorMsg = getExcelInfo(xlsFile);
                        String msg = importSOPPartData(sopPartImportBeanList);
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

        System.out.println("======================import insideFile end======================");
        return result;
    }

    private static String importSOPPartData(List<SOPPartImportBean> sopPartImportBeanList) {
        StringBuilder msg = new StringBuilder();
        try {
            WTLibrary wtContainer = WTContainerUtil.getLibraryByName(SopConstants.SOP_CONTAINER_GYZSK);
            IBAUtility ibaUtility;
            for (SOPPartImportBean sopPartImportBean : sopPartImportBeanList) {
//            	String pre = "S" + sopPartImportBean.getProfessionalCode() + "-" + sopPartImportBean.getStationNo() + "-";
//                String number = getLastestNumber(pre,"");
            	String number = sopPartImportBean.getNumber();
            	String[] numbers = number.split("-");
            	if(numbers.length>2 && numbers[0].startsWith("S")){
            		String pre = numbers[0] + "-" + numbers[1] + "-";
            		String n = numbers[2];
            		long num = Long.parseLong(n);
            		SopUtil.dealImpSopSeqNumber(num, pre);
            	}else{
            		msg.append("第"+ sopPartImportBean.getIndex() +"行编号书写不规范;</br>");
                    continue;
            	}
            	WTPart part = WTPartUtil.getLatestPartByPartNumber(number);
                if (part != null) {
                	msg.append("第"+ sopPartImportBean.getIndex() +"行系统存在编号为[" + number + "]的SOP体系BOM;</br>");
                    continue;
                }
                Map<String, String> ibaMap = new HashMap<String, String>();
                ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, sopPartImportBean.getSpecializedType());
                ibaMap.put(SopConstants.SOP_IBA_PROCEDUCENAME, sopPartImportBean.getProdureName());
                List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZSK, "", sopPartImportBean.getName(), SopConstants.PBOM_VIEW, ibaMap, true,SopConstants.SOP_TYPE_SOPPART);
                if (wtPartList != null && wtPartList.size() > 0) {
                	msg.append("第").append(sopPartImportBean.getIndex()).append("行[" + sopPartImportBean.getName() + "|" + sopPartImportBean.getSpecializedType() + "|" + sopPartImportBean.getProdureName() + "]SOP体系BOM已存在;</br>");
                    continue;
                }
                String foldor = SopConstants.SOP_FOLDOR_BOM + sopPartImportBean.getSpecializedType() + "/" + sopPartImportBean.getProdureName();
                WTPart wtPart = WTPartUtil.createPart(number, sopPartImportBean.getName(), wtContainer, foldor, SopConstants.PBOM_VIEW, SopConstants.SOP_TYPE_SOPPART);
                ibaUtility = new IBAUtility(wtPart);
                ibaUtility.setIBAValue(SopConstants.SOP_IBA_SECRET, sopPartImportBean.getSecret());
                ibaUtility.setIBAValue(SopConstants.SOP_IBA_TERM, sopPartImportBean.getTrem());
                ibaUtility.setIBAValue(SopConstants.SOP_IBA_SPECIALIZEDTYPE, sopPartImportBean.getSpecializedType());
                ibaUtility.setIBAValue(SopConstants.SOP_IBA_PROFESSIONALCODE, sopPartImportBean.getProfessionalCode());
                ibaUtility.setIBAValue(SopConstants.SOP_IBA_PROCEDUCENAME, sopPartImportBean.getProdureName());
                ibaUtility.setIBAValue(SopConstants.SOP_IBA_GONGXUJIANHAO, sopPartImportBean.getStationNo());
                ibaUtility.setIBAValue(SopConstants.SOP_IBA_DEPARTMENT, sopPartImportBean.getDepartment());
                wtPart = (WTPart) ibaUtility.updateAttributeContainer(wtPart);
                IBAUtility.updateIBAHolder(wtPart);
                msg.append("第" + sopPartImportBean.getIndex() + "行[" + sopPartImportBean.getName() + "|" + sopPartImportBean.getSpecializedType() + "|" + sopPartImportBean.getProdureName() + "]导入成功</br>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg.append(e.getLocalizedMessage());
        }
        return msg.toString();
    }


    private static String getExcelInfo(File file) throws Exception {
        System.out.println("get excel info start");
        sopPartImportBeanList = new ArrayList<SOPPartImportBean>();
        StringBuilder sb = new StringBuilder();
        Workbook workbook = null;
        InputStream is;
        SOPPartImportBean sopPartImportBean;
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
                String number = getValue(row.getCell(COL_NUMBER)).toString().trim();//编号
                if(number == null || number.isEmpty()){
                	sb.append("第").append(i+1).append("行未读取到编号;</br>");
                	continue;
                }

                String name = getValue(row.getCell(COL_NAME)).toString().trim();// 名称
                if (name == null || name.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到名称;</br>");
                    continue;
                }

                String secret = getValue(row.getCell(COL_SECRET)).toString().trim();// 密级
                if (secret == null || secret.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到密级;</br>");
                    continue;
                } else {
                    if (!secretList.contains(secret)) {
                        sb.append("第").append(i + 1).append("行密级属性填写不规范，密级应为：公开|商密|内部|秘密|机密;</br>");
                        continue;
                    }
                }
                String trem = getValue(row.getCell(COL_TREM)).toString().trim();// 期限
//                if (trem == null || trem.isEmpty()) {
//                    sb.append("第").append(i + 1).append("行未读取到期限;</br>");
//                    continue;
//                } else {
                    String mimiMat = "^([1-9]|10)$";
                    String jimiMat = "^([1-9]|1[0-9]|20)$";
                    boolean isMatch;
                    if ("秘密".equals(secret)) {
                        isMatch = Pattern.matches(mimiMat, trem);
                        if (!isMatch) {
                            sb.append("第").append(i + 1).append("行期限值填写不规范，秘密应为：1-10;</br>");
                            continue;
                        }
                    } else if ("机密".equals(secret)) {
                        isMatch = Pattern.matches(jimiMat, trem);
                        if (!isMatch) {
                            sb.append("第").append(i + 1).append("行期限值填写不规范，机密应为：1-20;</br>");
                            continue;
                        }
                    }
//                }
                String professionalCode = "";
                String specializedType = getValue(row.getCell(COL_SPECIALIZEDTYPE)).toString().trim();// 专业类别
                if (specializedType == null || specializedType.isEmpty()) {
                    sb.append("第").append(i + 1).append("行专业类别未填写;</br>");
                    continue;
                } else {
                    MPMTooling tooling = MPMResourceUtil.getMPMToolingByName(specializedType, SopConstants.SOP_TYPE_SPECIALIZEDTYPE);
                    if (tooling == null) {
                        sb.append("第").append(i + 1).append("行系统不存在名称为[" + specializedType + "]的专业类别;</br>");
                        continue;
                    } else {
                        IBAUtility ibaUtility = new IBAUtility(tooling);
                        professionalCode = ibaUtility.getIBAValue(SopConstants.SOP_IBA_PROFESSIONALCODE);
                    }
                }

                String stationNo = "";
                String produreName = getValue(row.getCell(COL_PROCEDURENAME)).toString().trim();//工序名称
                if (produreName == null || produreName.isEmpty()) {
                    sb.append("第").append(i + 1).append("行工序名称未填写;</br>");
                    continue;
                } else {
                    List<MPMTooling> toolingList = getMPMToolingByName(produreName, SopConstants.SOP_TYPE_PROCEDUCENAME);

                    if (toolingList == null || toolingList.size() == 0) {
                        sb.append("第").append(i + 1).append("行系统不存在名称为[" + produreName + "]的工序名称;</br>");
                        continue;
                    } else {
                        IBAUtility ibaUtility;
                        for(MPMTooling tooling : toolingList){
                            ibaUtility = new IBAUtility(tooling);
                            String zylb = ibaUtility.getIBAValue(SopConstants.SOP_TYPE_SPECIALIZEDTYPE);
                            stationNo = ibaUtility.getIBAValue(SopConstants.SOP_IBA_GONGXUJIANHAO);
                            if(stationNo != null && !stationNo.isEmpty()){
                                break;
                            }
                        }
                    }
                }

                String department = getValue(row.getCell(COL_DEPARTMENT)).toString().trim();//部门
                if (department == null || department.isEmpty()) {
                    sb.append("第").append(i + 1).append("行部门未填写;</br>");
                    continue;
                } else {
                    if (!gongXuCheJian.contains(department)) {
                        sb.append("第").append(i + 1).append("行部门填写不规范，部门应为" + gongXuCheJian + ";</br>");
                        continue;
                    }
                }

                Map<String, String> ibaMap = new HashMap<String, String>();
                ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, specializedType);
                ibaMap.put(SopConstants.SOP_IBA_PROCEDUCENAME, produreName);
                WTPart wtPart = WTPartUtil.getLatestPartByPartNumber(number);
                if(wtPart!= null){
                	sb.append("第").append(i + 1).append("行编号为[" + number + "]SOP体系BOM已存在;</br>");
                    continue;
                }
                List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZSK, "", name, SopConstants.PBOM_VIEW, ibaMap, true,SopConstants.SOP_TYPE_SOPPART);
                if (wtPartList != null && wtPartList.size() > 0) {
                    sb.append("第").append(i + 1).append("行[" + name + "|" + specializedType + "|" + produreName + "]SOP体系BOM已存在;</br>");
                    continue;
                }

//                String pre = "S" + professionalCode + "-" + stationNo + "-";
//                String seqNumber = SopUtil.getSopSeqNumber(1, pre);
                sopPartImportBean = new SOPPartImportBean();
                sopPartImportBean.setNumber(number);
                sopPartImportBean.setName(name);
                sopPartImportBean.setSecret(secret);
                sopPartImportBean.setTrem(trem);
                sopPartImportBean.setSpecializedType(specializedType);
                sopPartImportBean.setProfessionalCode(professionalCode);
                sopPartImportBean.setProdureName(produreName);
                sopPartImportBean.setStationNo(stationNo);
                sopPartImportBean.setDepartment(department);
                sopPartImportBean.setIndex((i + 1) + "");
                sopPartImportBeanList.add(sopPartImportBean);
            }
        } catch (IOException e) {
            return "读取文件错误</br>" + e.getLocalizedMessage() + "</br>";
        }
        System.out.println("get excel info end " + sopPartImportBeanList.size());
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

    private static String getLastestNumber(String pre, String lastestNumber) throws Exception {
        lastestNumber = pre + SopUtil.getSopSeqNumber(1, pre);
        WTPart part = WTPartUtil.getPartByNumberAndView(lastestNumber, SopConstants.PBOM_VIEW);
        if (part != null) {
            SopUtil.getSopSeqNumber(2, pre);
            lastestNumber = getLastestNumber(pre, lastestNumber);
        }
        return lastestNumber;
    }

    public static List<MPMTooling> getMPMToolingByName(String name, String typeName) throws WTException, RemoteException, WTPropertyVetoException {
        List<MPMTooling> toolingList = new ArrayList<MPMTooling>();
        MPMTooling tooling = null;
        QuerySpec querySpec = new QuerySpec(MPMTooling.class);
        querySpec.appendWhere(new SearchCondition(MPMTooling.class, WTPart.NAME, SearchCondition.EQUAL, name, true), index);
        querySpec.appendAnd();
        TypeUtil.getTypeQuery(MPMTooling.class, typeName, querySpec);
        QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
        queryResult = new LatestConfigSpec().process(queryResult);
        while (queryResult.hasMoreElements()) {
            tooling = (MPMTooling) queryResult.nextElement();
            toolingList.add(tooling);
        }

        return toolingList;
    }
}
