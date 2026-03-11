package ext.casc.sop.resource;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.glaway.mpm.sop.model.SopResourceBean;
import ext.casc.sop.bean.SopResourceImportBean;
import ext.casc.sop.util.SopPartUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import wt.httpgw.URLFactory;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContainer;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.intf.PrintToWCIntfRMI;
import com.glaway.mpm.print.data.CmImportBean;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMResourceUtil;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.WTContainerUtil;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.resource.MPMTooling;

import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.util.SopUtil;

import static com.glaway.mpm.mpmresource.AttributeConstants.ProfessionalCode;

public class SopMPMResourceImportProcessor {

    private static String tmp_dir = PropertiesUtil.getTempPath() + File.separator;
    static final int BUFFER = 2048;
    private static List<SopResourceImportBean> resourceBeanList;
    private static SopResourceImportBean resourceBean;

    @SuppressWarnings({"unused", "deprecation"})
    public static FormResult importObjects(NmCommandBean nmCommandBean) throws WTException, IOException {
        System.out.println("======================import SOP RESOURCE start======================");
        FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
        String xlsFileName = nmCommandBean.getTextParameter("file2");
        System.out.println("--------xlsFile:" + xlsFileName);
        File temp_xlsFile = (File) nmCommandBean.getRequest().getAttribute("file2");
        System.out.println("--------temp_xlsFile:" + temp_xlsFile.getName());
        if (xlsFileName.contains("\\")) {
            xlsFileName = xlsFileName.substring(xlsFileName.lastIndexOf("\\") + 1, xlsFileName.length());
        }
        String message = "";
        compressFile(temp_xlsFile, xlsFileName);
        // 读取保存在本地的EXCEL文件
        File xlsFile = new File(tmp_dir + xlsFileName);
        try {
            if (xlsFile != null) {
                String errorMsg = "";
                if (xlsFileName.contains("参数项目")) {
                    if (xlsFileName.contains("项目名称")) {
                        errorMsg = getCSXMMC(xlsFile);
                    } else {
                        errorMsg = getCSXM(xlsFile);
                    }
                } else if (xlsFileName.contains("专业类别")) {
                    errorMsg = getZYLB(xlsFile);
                } else if (xlsFileName.contains("操作岗位")) {
                    errorMsg = getCZGW(xlsFile);
                } else if (xlsFileName.contains("定制区域")) {
                    errorMsg = getDZQY(xlsFile);
                } else if (xlsFileName.contains("工序名称")) {
                    errorMsg = getGXMC(xlsFile);
                } else if (xlsFileName.contains("物资类别")) {
                    errorMsg = getWZLB(xlsFile);
                } else if (xlsFileName.contains("操作名称")) {
                    errorMsg = getCZMC(xlsFile);
                }
                //数据导入
                String msg = importSopResource();
                message = errorMsg + "</br>" + msg;
            } else {
                System.out.println("file is not exist");
            }
        } catch (Exception e) {
            message = "读取文件信息异常</br>" + e.getLocalizedMessage() + "</br>";
        }

        URLFactory urlfactory = new URLFactory();
        nmCommandBean.getRequest().getSession().putValue("errorInfo", message);
        String url = urlfactory.getBaseHREF() + "netmarkets/jsp/ext/casc/importdata/importDataInfo.jsp?";
        result.setURL(url);
        result.setNextAction(FormResultAction.FORWARD);

        System.out.println("======================import insideFile end======================");
        return result;
    }

    private static String importSopResource() {
        StringBuilder sb = new StringBuilder();
        try {
            for (SopResourceImportBean resourceBean : resourceBeanList) {
                String type = resourceBean.getType();
                if (SopConstants.SOP_IBA_SPECIALIZEDTYPE.equals(type)) {
                    sb.append(createSpecializedtype(resourceBean));
                } else if (SopConstants.SOP_IBA_PARAMETERS.equals(type)) {
                    sb.append(createParameters(resourceBean));
                } else if (SopConstants.SOP_IBA_PARAMETERSNAME.equals(type)) {
                    sb.append(createParametersName(resourceBean));
                } else if (SopConstants.SOP_IBA_CUSTOMAREA.equals(type)) {
                    sb.append(createCustomArea(resourceBean));
                } else if (SopConstants.SOP_IBA_PROCEDUCENAME.equals(type)) {
                    sb.append(createProceduceName(resourceBean));
                } else if (SopConstants.SOP_IBA_OPERATIONJOB.equals(type)) {
                    sb.append(createOperationJob(resourceBean));
                } else if (SopConstants.SOP_IBA_MATERIALCATEGORY.equals(type)) {
                    sb.append(createMaterialCategory(resourceBean));
                } else if (SopConstants.SOP_IBA_OPERATIONNAME.equals(type)) {
                    sb.append(createOperationName(resourceBean));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 创建物资类别
     *
     * @param resourceBean
     * @return
     */
    private static String createMaterialCategory(SopResourceImportBean resourceBean) {
        String msg = "";
        //基本属性
//        String number = resourceBean.getNumber();
        String name = resourceBean.getName();
        //IBA属性
        String zylb = resourceBean.getZylb();
        String remark = resourceBean.getRemark();
        //行号
        int rowNum = resourceBean.getRowNum();
        String typeName = SopConstants.SOP_TYPE_MPMTOOLING + "|" + SopConstants.SOP_TYPE_MATERIALCATEGORY;
        String folderPath = SopConstants.SOP_FOLDOR_MATERIALCATEGORY;
        Map<String, String> ibaMap = new HashMap<String, String>();
        ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, zylb);
        ibaMap.put(SopConstants.SOP_IBA_REMARK, remark);
        try {
            String number = getLastestNumber(SopConstants.SOP_STR_WZLB, SopConstants.SOP_TYPE_MATERIALCATEGORY, "");
            WTContainer container = WTContainerUtil.getContainerByName(SopConstants.SOP_CONTAINER_GYZYK);
            MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName, "");
            setIBAValue(tooling, ibaMap);
            msg = "第" + rowNum + "行名称为[" + name + "]的物资类别导入成功！</br>";
            SopUtil.getSopZYSeqNumber(2, SopConstants.SOP_STR_WZLB);
        } catch (Exception e) {
            msg = "第" + rowNum + "行名称为[" + name + "]的物资类别导入失败！" + e.getLocalizedMessage() + "</br>";
        }
        return msg;
    }

    /**
     * 创建操作名称
     *
     * @param resourceBean
     * @return
     */
    private static String createOperationName(SopResourceImportBean resourceBean) {
        String msg = "";
        //基本属性
        String name = resourceBean.getName();
        //IBA属性
        String remark = resourceBean.getRemark();
        //行号
        int rowNum = resourceBean.getRowNum();
        String typeName = SopConstants.SOP_TYPE_MPMTOOLING + "|" + SopConstants.SOP_TYPE_OPERATIONNAME;
        String folderPath = SopConstants.SOP_FOLDOR_OPERATIONNAME;
        Map<String, String> ibaMap = new HashMap<String, String>();
        ibaMap.put(SopConstants.SOP_IBA_REMARK, remark);
        try {
            String number = getLastestNumber(SopConstants.SOP_STR_CZMC, SopConstants.SOP_TYPE_OPERATIONNAME, "");
            WTContainer container = WTContainerUtil.getContainerByName(SopConstants.SOP_CONTAINER_GYZYK);
            MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName, "");
            setIBAValue(tooling, ibaMap);
            msg = "第" + rowNum + "行名称为[" + name + "]的操作名称导入成功！</br>";
            SopUtil.getSopZYSeqNumber(2, SopConstants.SOP_STR_WZLB);
        } catch (Exception e) {
            msg = "第" + rowNum + "行名称为[" + name + "]的操作名称导入失败！" + e.getLocalizedMessage() + "</br>";
        }
        return msg;
    }

    /**
     * 创建操作岗位
     *
     * @param resourceBean
     * @return
     */
    private static String createOperationJob(SopResourceImportBean resourceBean) {
        String msg = "";
        //基本属性
        String name = resourceBean.getName();
        //IBA属性
        String zzcj = resourceBean.getDept();
        String remark = resourceBean.getRemark();
        //行号
        int rowNum = resourceBean.getRowNum();
        String typeName = SopConstants.SOP_TYPE_MPMTOOLING + "|" + SopConstants.SOP_TYPE_OPERATIONJOB;
        String folderPath = SopConstants.SOP_FOLDOR_OPERATIONJOB;
        Map<String, String> ibaMap = new HashMap<String, String>();
        ibaMap.put(SopConstants.SOP_IBA_ZZCJ, zzcj);
        ibaMap.put(SopConstants.SOP_IBA_REMARK, remark);
        try {
            String number = getLastestNumber(SopConstants.SOP_STR_CZGW, SopConstants.SOP_TYPE_OPERATIONJOB, "");
            WTContainer container = WTContainerUtil.getContainerByName(SopConstants.SOP_CONTAINER_GYZYK);
            MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName, "");
            setIBAValue(tooling, ibaMap);
            msg = "第" + rowNum + "行名称为[" + name + "]的操作岗位导入成功！</br>";
            SopUtil.getSopZYSeqNumber(2, SopConstants.SOP_STR_CZGW);
        } catch (Exception e) {
            msg = "第" + rowNum + "行名称为[" + name + "]的操作岗位导入失败！" + e.getLocalizedMessage() + "</br>";
        }
        return msg;
    }

    /**
     * 创建工序名称
     *
     * @param resourceBean
     * @return
     */
    private static String createProceduceName(SopResourceImportBean resourceBean) {
        String msg = "";
        //基本属性
        String name = resourceBean.getName();
        //IBA属性
        String gxjh = resourceBean.getGxjh();
        String ywmc = resourceBean.getYwmc();
        String zylb = resourceBean.getZylb();
        String zzcj = resourceBean.getDept();
        String remark = resourceBean.getRemark();
        //行号
        int rowNum = resourceBean.getRowNum();
        String typeName = SopConstants.SOP_TYPE_MPMTOOLING + "|" + SopConstants.SOP_TYPE_PROCEDUCENAME;
        String folderPath = SopConstants.SOP_FOLDOR_PROCEDUCENAME;
        Map<String, String> ibaMap = new HashMap<String, String>();
        ibaMap.put(SopConstants.SOP_IBA_GONGXUJIANHAO, gxjh);
        ibaMap.put(SopConstants.SOP_IBA_ENGLISHNAME, ywmc);
        ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, zylb);
        ibaMap.put(SopConstants.SOP_IBA_ZZCJ, zzcj);
        ibaMap.put(SopConstants.SOP_IBA_REMARK, remark);
        try {
            String number = getLastestNumber(SopConstants.SOP_STR_GXMC, SopConstants.SOP_TYPE_PROCEDUCENAME, "");
            WTContainer container = WTContainerUtil.getContainerByName(SopConstants.SOP_CONTAINER_GYZYK);
            MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName, "");
            setIBAValue(tooling, ibaMap);
            msg = "第" + rowNum + "行名称为[" + name + "]的工序名称导入成功！</br>";
            SopUtil.getSopZYSeqNumber(2, SopConstants.SOP_STR_GXMC);
        } catch (Exception e) {
            msg = "第" + rowNum + "行名称为[" + name + "]的工序名称导入失败！" + e.getLocalizedMessage() + "</br>";
        }
        return msg;
    }

    /**
     * 创建定制区域
     *
     * @param resourceBean
     * @return
     */
    private static String createCustomArea(SopResourceImportBean resourceBean) {
        String msg = "";
        //基本属性
        String name = resourceBean.getName();
        //IBA属性
        String remark = resourceBean.getRemark();
        //行号
        int rowNum = resourceBean.getRowNum();
        String typeName = SopConstants.SOP_TYPE_MPMTOOLING + "|" + SopConstants.SOP_TYPE_CUSTOMAREA;
        String folderPath = SopConstants.SOP_FOLDOR_CUSTOMAREA;
        Map<String, String> ibaMap = new HashMap<String, String>();
        ibaMap.put(SopConstants.SOP_IBA_REMARK, remark);
        try {
            String number = getLastestNumber(SopConstants.SOP_STR_DZQY, SopConstants.SOP_TYPE_CUSTOMAREA, "");
            WTContainer container = WTContainerUtil.getContainerByName(SopConstants.SOP_CONTAINER_GYZYK);
            MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName, "");
            setIBAValue(tooling, ibaMap);
            SopUtil.getSopZYSeqNumber(2, SopConstants.SOP_STR_DZQY);
            msg = "第" + rowNum + "行名称为[" + name + "]的定制区域导入成功！</br>";
        } catch (Exception e) {
            msg = "第" + rowNum + "行名称为[" + name + "]的定制区域导入失败！" + e.getLocalizedMessage() + "</br>";
        }
        return msg;
    }

    /**
     * 创建参数项目名称
     *
     * @param resourceBean
     * @return
     */
    private static String createParametersName(SopResourceImportBean resourceBean) {
        String msg = "";
        //基本属性
        String name = resourceBean.getName();
        //IBA属性
        String zylb = resourceBean.getZylb();
        String remark = resourceBean.getRemark();
        //行号
        int rowNum = resourceBean.getRowNum();
        String typeName = SopConstants.SOP_TYPE_MPMTOOLING + "|" + SopConstants.SOP_TYPE_PARAMETERSNAME;
        String folderPath = SopConstants.SOP_FOLDOR_PARAMETERSNAME;
        Map<String, String> ibaMap = new HashMap<String, String>();
        ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, zylb);
        ibaMap.put(SopConstants.SOP_IBA_REMARK, remark);
        try {
            String number = getLastestNumber(SopConstants.SOP_STR_CSXMMC, SopConstants.SOP_TYPE_PARAMETERSNAME, "");
            WTContainer container = WTContainerUtil.getContainerByName(SopConstants.SOP_CONTAINER_GYZYK);
            MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName, "");
            setIBAValue(tooling, ibaMap);
            msg = "第" + rowNum + "行名称为[" + name + "]的参数项目名称导入成功！</br>";
            SopUtil.getSopZYSeqNumber(2, SopConstants.SOP_STR_CSXMMC);
        } catch (Exception e) {
            msg = "第" + rowNum + "行名称为[" + name + "]的参数项目名称导入失败！" + e.getLocalizedMessage() + "</br>";
        }
        return msg;
    }

    /**
     * 创建参数项目
     *
     * @param resourceBean
     * @return
     */
    private static String createParameters(SopResourceImportBean resourceBean) {
        String msg = "";
        String name = resourceBean.getName();

        String zylb = resourceBean.getZylb();
        String gxmc = resourceBean.getGxmc();
        String wzlb = resourceBean.getWzlb();
        String csz = resourceBean.getCsz();
        String remark = resourceBean.getRemark();
        int rowNum = resourceBean.getRowNum();
        String typeName = SopConstants.SOP_TYPE_MPMTOOLING + "|" + SopConstants.SOP_TYPE_PARAMETERS;
        String folderPath = SopConstants.SOP_FOLDOR_PARAMETERS;
        Map<String, String> ibaMap = new HashMap<String, String>();
        ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, zylb);
        ibaMap.put(SopConstants.SOP_IBA_PARAMETERSNAME, name);
        ibaMap.put(SopConstants.SOP_IBA_PROCEDUCENAME, gxmc);
        ibaMap.put(SopConstants.SOP_IBA_MATERIALCATEGORY, wzlb);
        ibaMap.put(SopConstants.SOP_IBA_CANSHUZHI, csz);
        ibaMap.put(SopConstants.SOP_IBA_REMARK, remark);
        try {
            String number = getLastestNumber(SopConstants.SOP_STR_CSXM, SopConstants.SOP_TYPE_PARAMETERS, "");
            WTContainer container = WTContainerUtil.getContainerByName(SopConstants.SOP_CONTAINER_GYZYK);
            MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName, "");
            setIBAValue(tooling, ibaMap);
            msg = "第" + rowNum + "行名称为[" + name + "]的参数项目导入成功！</br>";
            SopUtil.getSopZYSeqNumber(2, SopConstants.SOP_STR_CSXM);
        } catch (Exception e) {
            msg = "第" + rowNum + "行名称为[" + name + "]的参数项目导入失败！" + e.getLocalizedMessage() + "</br>";
            e.printStackTrace();
        }
        return msg;
    }

    /**
     * 创建专业类别
     *
     * @param resourceBean
     * @return
     */
    private static String createSpecializedtype(SopResourceImportBean resourceBean) {
        String msg = "";
        String name = resourceBean.getName();
        String zydh = resourceBean.getZydh();
        String remark = resourceBean.getRemark();
        int rowNum = resourceBean.getRowNum();
        String typeName = SopConstants.SOP_TYPE_MPMTOOLING + "|" + SopConstants.SOP_TYPE_SPECIALIZEDTYPE;
        String folderPath = SopConstants.SOP_FOLDOR_SPECIALIZEDTYPE;
        Map<String, String> ibaMap = new HashMap<String, String>();
        ibaMap.put(SopConstants.SOP_IBA_PROFESSIONALCODE, zydh);
        ibaMap.put(SopConstants.SOP_IBA_REMARK, remark);
        try {
            String number = getLastestNumber(SopConstants.SOP_STR_ZYLB, SopConstants.SOP_TYPE_SPECIALIZEDTYPE, "");
            WTContainer container = WTContainerUtil.getContainerByName(SopConstants.SOP_CONTAINER_GYZYK);
            MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName, "");
            setIBAValue(tooling, ibaMap);
            msg = "第" + rowNum + "行名称为[" + name + "]的专业类别导入成功！</br>";
            SopUtil.getSopZYSeqNumber(2, SopConstants.SOP_STR_ZYLB);
        } catch (Exception e) {
            msg = "第" + rowNum + "行名称为[" + name + "]的专业类别导入失败！" + e.getLocalizedMessage() + "</br>";
        }
        return msg;
    }

    @SuppressWarnings("resource")
    private static String getCSXM(File file) throws Exception {
        System.out.println("get excel info start");
        resourceBeanList = new ArrayList<SopResourceImportBean>();
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
            Row row = null;
            for (int i = 1; i <= rows; i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String csxmmc = getValue(row.getCell(1)).toString().trim();// 参数项目名称
                if (csxmmc == null || csxmmc.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到参数项目名称;</br>");
                    continue;
                } else {
                    MPMTooling tooling = MPMResourceUtil.getMPMToolingByName(csxmmc, SopConstants.SOP_TYPE_PARAMETERSNAME);
                    if (tooling == null) {
                        sb.append("第").append(i + 1).append("行系统中不存在名称为[" + csxmmc + "]的参数项目名称;</br>");
                        continue;
                    }
                }
                String zylb = getValue(row.getCell(2)).toString().trim();// 专业类别
                if (zylb == null || zylb.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到专业类别;</br>");
                    continue;
                } else {
                    MPMTooling tooling = MPMResourceUtil.getMPMToolingByName(zylb, SopConstants.SOP_TYPE_SPECIALIZEDTYPE);
                    if (tooling == null) {
                        sb.append("第").append(i + 1).append("行系统中不存在名称为[" + zylb + "]的专业类别;</br>");
                        continue;
                    }
                }
                String gxmc = getValue(row.getCell(3)).toString().trim();// 工序名称
                if (gxmc == null || gxmc.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到工序名称;</br>");
                    continue;
                } else {
                    MPMTooling tooling = MPMResourceUtil.getMPMToolingByName(gxmc, SopConstants.SOP_TYPE_PROCEDUCENAME);
                    if (tooling == null) {
                        sb.append("第").append(i + 1).append("行系统中不存在名称为[" + gxmc + "]的工序名称;</br>");
                        continue;
                    }
                }

                String wzlb = getValue(row.getCell(4)).toString().trim();// 物资类别
                if (wzlb == null || wzlb.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到物资类别;</br>");
                    continue;
                } else {
                    MPMTooling tooling = MPMResourceUtil.getMPMToolingByName(wzlb, SopConstants.SOP_TYPE_MATERIALCATEGORY);
                    if (tooling == null) {
                        sb.append("第").append(i + 1).append("行系统中不存在名称为[" + wzlb + "]的物资类别;</br>");
                        continue;
                    }
                }
                String csz = getValue(row.getCell(5)).toString().trim();// 参数值
                String description = getValue(row.getCell(6)).toString().trim();// 说明
                Map<String, String> ibaMap = new HashMap<String, String>();
                ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, zylb);
                ibaMap.put(SopConstants.SOP_IBA_PROCEDUCENAME, gxmc);
                ibaMap.put(SopConstants.SOP_IBA_MATERIALCATEGORY, wzlb);
                List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK, null, csxmmc, null, ibaMap, true,SopConstants.SOP_TYPE_PARAMETERS);
                if (wtPartList != null && wtPartList.size() > 0) {
                    sb.append("第").append(i + 1).append("行名称为[" + csxmmc + "]的参数项目已存在;</br>");
                    continue;
                }

                resourceBean = new SopResourceImportBean();
                String number = SopUtil.getSopZYSeqNumber(1, SopConstants.SOP_STR_CSXM);
                resourceBean.setNumber(number);
                resourceBean.setName(csxmmc);
                resourceBean.setZylb(zylb);
                resourceBean.setGxmc(gxmc);
                resourceBean.setWzlb(wzlb);
                resourceBean.setCsz(csz);
                resourceBean.setRemark(description);
                resourceBean.setType(SopConstants.SOP_IBA_PARAMETERS);
                resourceBean.setRowNum(i + 1);
                resourceBeanList.add(resourceBean);
//                String typeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Parameters";
//                String folderPath = "/Default/SOP资源/工艺参数项目";
//                WTContainer container = 吧 .getContainerByName("工艺资源库");
//                MPMTooling tooling = MPMResourceUtil.createTooling(number, csxmmc, container, folderPath, typeName, description);
//                setRemarkValue(tooling, description);
//                setCSXMIBA(tooling, typeName, ZYLB, GXMC, CANSHUZHI, csxmmc, WZLB);

            }
        } catch (IOException e) {
            return "读取文件错误</br>" + e.getLocalizedMessage() + "</br>";
        }
        return sb.toString();
    }

    @SuppressWarnings("resource")
    private static String getDZQY(File file) throws Exception {
        System.out.println("get excel info start");
        resourceBeanList = new ArrayList<SopResourceImportBean>();
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
            Row row = null;
            for (int i = 1; i <= rows; i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String name = getValue(row.getCell(1)).toString().trim();// 名称
                if (name == null || name.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到名称;</br>");
                    continue;
                }
                String description = (getValue(row.getCell(2)) + "").toString().trim();// 说明
                List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK, null, name, null, null, true,SopConstants.SOP_TYPE_CUSTOMAREA);
                if (wtPartList != null && wtPartList.size() > 0) {
                    sb.append("第").append(i + 1).append("行名称为[" + name + "]的定制区域已存在;</br>");
                    continue;
                }
                resourceBean = new SopResourceImportBean();
                String number = SopUtil.getSopZYSeqNumber(1, SopConstants.SOP_STR_DZQY);
                resourceBean.setNumber(number);
                resourceBean.setName(name);
                resourceBean.setRemark(description);
                resourceBean.setType(SopConstants.SOP_IBA_CUSTOMAREA);
                resourceBean.setRowNum(i + 1);
                resourceBeanList.add(resourceBean);
//                String typeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.CustomArea";
//                String folderPath = "/Default/SOP资源/定制区域";
//                WTContainer container = WTContainerUtil.getContainerByName("工艺资源库");
//
//                MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName, description);
//                setRemarkValue(tooling, description);

            }
        } catch (IOException e) {
            return "读取文件错误</br>" + e.getLocalizedMessage() + "</br>";
        }
        return sb.toString();
    }

    @SuppressWarnings("resource")
    private static String getCZGW(File file) throws Exception {
        System.out.println("get excel info start");
        resourceBeanList = new ArrayList<SopResourceImportBean>();
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
            Row row = null;
            for (int i = 1; i <= rows; i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String name = getValue(row.getCell(1)).toString().trim();// 名称
                if (name == null || name.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到操作岗位名称;</br>");
                    continue;
                }
                String zzcj = getValue(row.getCell(2)).toString().trim();// 车间
//                if (zzcj == null || zzcj.isEmpty()) {
//                    sb.append("第").append(i + 1).append("行未读取到车间;</br>");
//                    continue;
//                }
                String description = getValue(row.getCell(3)).toString().trim();// 说明
                Map<String, String> ibaMap = new HashMap<String, String>();
//                ibaMap.put(SopConstants.SOP_IBA_ZZCJ, zzcj);
                List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK, null, name, null, ibaMap, true,SopConstants.SOP_TYPE_OPERATIONJOB);
                if (wtPartList != null && wtPartList.size() > 0) {
                    sb.append("第").append(i + 1).append("行名称为[" + name + "]的操作岗位已存在;</br>");
                    continue;
                }
                resourceBean = new SopResourceImportBean();
                String number = SopUtil.getSopZYSeqNumber(1, SopConstants.SOP_STR_CZGW);
                resourceBean.setNumber(number);
                resourceBean.setName(name);
                resourceBean.setDept(zzcj);
                resourceBean.setRemark(description);
                resourceBean.setType(SopConstants.SOP_IBA_OPERATIONJOB);
                resourceBean.setRowNum(i + 1);
                resourceBeanList.add(resourceBean);
//                String typeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.OperationJob";
//                String folderPath = "/Default/SOP资源/操作岗位";
//                WTContainer container = WTContainerUtil.getContainerByName("工艺资源库");
//
//                MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName, description);
//                setRemarkValue(tooling, description);
//                setZzcjValue(tooling, ZZCJ);

            }
        } catch (IOException e) {
            return "读取文件错误</br>" + e.getLocalizedMessage() + "</br>";
        }
        return sb.toString();
    }

    @SuppressWarnings("resource")
    private static String getZYLB(File file) throws Exception {
        System.out.println("get excel info start");
        resourceBeanList = new ArrayList<SopResourceImportBean>();
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
            Row row = null;
            for (int i = 1; i <= rows; i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String name = getValue(row.getCell(1)).toString().trim();// 名称
                if (name == null || name.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到专业类别名称;</br>");
                    continue;
                }
                String zydh = getValue(row.getCell(2)).toString().trim();// 专业代号
                if (zydh == null || zydh.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到专业代号;</br>");
                    continue;
                }
                String description = getValue(row.getCell(3)).toString().trim();// 说明

                Map<String, String> ibaMap = new HashMap<String, String>();
                ibaMap.put(SopConstants.SOP_IBA_PROFESSIONALCODE, zydh);
                List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK, null, name, null, ibaMap, true,SopConstants.SOP_TYPE_SPECIALIZEDTYPE);
                if (wtPartList != null && wtPartList.size() > 0) {
                    sb.append("第").append(i + 1).append("行名称为[" + name + "]的专业类别已存在;</br>");
                    continue;
                }

                resourceBean = new SopResourceImportBean();
                String number = SopUtil.getSopZYSeqNumber(1, SopConstants.SOP_STR_ZYLB);
                resourceBean.setNumber(number);
                resourceBean.setName(name);
                resourceBean.setZydh(zydh);
                resourceBean.setRemark(description);
                resourceBean.setType(SopConstants.SOP_IBA_SPECIALIZEDTYPE);
                resourceBean.setRowNum(i + 1);
                resourceBeanList.add(resourceBean);
//                String typeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.SpecializedType";
//                String folderPath = "/Default/SOP资源/专业类别";
//                WTContainer container = WTContainerUtil.getContainerByName("工艺资源库");
//
//                MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName, description);
//                setRemarkValue(tooling, description);
//                setZYLBIBA(tooling, typeName, ProfessionalCode);

            }
        } catch (IOException e) {
            return "读取文件错误</br>" + e.getLocalizedMessage() + "</br>";
        }
        return sb.toString();
    }

    @SuppressWarnings("resource")
    private static String getGXMC(File file) throws Exception {
        System.out.println("get excel info start");
        resourceBeanList = new ArrayList<SopResourceImportBean>();
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
            Row row = null;
            for (int i = 1; i <= rows; i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String name = getValue(row.getCell(1)).toString().trim();// 名称
                if (name == null || name.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到名称;</br>");
                    continue;
                }
                String gxjh = getValue(row.getCell(2)).toString().trim();// 工序简号
                if (gxjh == null || gxjh.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到工序简号;</br>");
                    continue;
                }
                String ywmc = getValue(row.getCell(3)).toString().trim();// 英文名称
                String zylb = getValue(row.getCell(4)).toString().trim();// 专业类别
                if (zylb == null || zylb.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到专业类别;</br>");
                    continue;
                } else {
                    MPMTooling tooling = MPMResourceUtil.getMPMToolingByName(zylb, SopConstants.SOP_TYPE_SPECIALIZEDTYPE);
                    if (tooling == null) {
                        sb.append("第").append(i + 1).append("行系统中不存在名称为[" + zylb + "]的专业类别;</br>");
                        continue;
                    }
                }
                String zzcj = getValue(row.getCell(5)).toString().trim();// 主治车间
                String description = getValue(row.getCell(6)).toString().trim();// 说明
                Map<String, String> ibaMap = new HashMap<String, String>();
                ibaMap.put(SopConstants.SOP_IBA_GONGXUJIANHAO, gxjh);
                ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, zylb);
                List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK, null, name, null, ibaMap, true,SopConstants.SOP_TYPE_PROCEDUCENAME);
                if (wtPartList != null && wtPartList.size() > 0) {
                    sb.append("第").append(i + 1).append("行名称为[" + name + "]的工序名称已存在;</br>");
                    continue;
                }
                resourceBean = new SopResourceImportBean();
                String number = SopUtil.getSopZYSeqNumber(1, SopConstants.SOP_STR_GXMC);
                resourceBean.setNumber(number);
                resourceBean.setName(name);
                resourceBean.setGxjh(gxjh);
                resourceBean.setYwmc(ywmc);
                resourceBean.setZylb(zylb);
                resourceBean.setDept(zzcj);
                resourceBean.setRemark(description);
                resourceBean.setType(SopConstants.SOP_IBA_PROCEDUCENAME);
                resourceBean.setRowNum(i + 1);
                resourceBeanList.add(resourceBean);
//                String typeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.ProceduceName";
//                String folderPath = "/Default/SOP资源/工序名称";
//                WTContainer container = WTContainerUtil.getContainerByName("工艺资源库");
//
//                MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName, description);
//                setRemarkValue(tooling, description);
//                setEnglishNameValue(tooling, EnglishName);
//                setZzcjValue(tooling, ZZCJ);
//                setGXMCIBA(tooling, typeName, GXJH, ZYLB);

            }
        } catch (IOException e) {
            return "读取文件错误</br>" + e.getLocalizedMessage() + "</br>";
        }
        return sb.toString();
    }

    @SuppressWarnings("resource")
    private static String getCSXMMC(File file) throws Exception {
        System.out.println("get excel info start");
        resourceBeanList = new ArrayList<SopResourceImportBean>();
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
            Row row = null;
            for (int i = 1; i <= rows; i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                //校验必填项：名称
                String name = getValue(row.getCell(1)).toString().trim();// 名称
                if (name == null || name.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到名称;</br>");
                    continue;
                }
                //校验必填项：专业类别
                String zylb = getValue(row.getCell(2)).toString().trim();// 专业类别
                if (zylb == null || zylb.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到专业类别;</br>");
                    continue;
                } else {
                    MPMTooling tooling = MPMResourceUtil.getMPMToolingByName(zylb, SopConstants.SOP_TYPE_SPECIALIZEDTYPE);
                    if (tooling == null) {
                        sb.append("第").append(i + 1).append("系统不存在名称为[" + zylb + "]的专业类别;</br>");
                        continue;
                    }
                }
                String description = getValue(row.getCell(3)).toString().trim();//说明
                //校验是否已存在：名称+专业类别
                Map<String, String> ibaMap = new HashMap<String, String>();
                ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, zylb);
                List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK, null, name, null, ibaMap, true,SopConstants.SOP_TYPE_PARAMETERSNAME);
                if (wtPartList != null && wtPartList.size() > 0) {
                    sb.append("第").append(i + 1).append("行名称为[" + name + "]的参数项目名称已存在;</br>");
                    continue;
                }
                resourceBean = new SopResourceImportBean();
                String number = SopUtil.getSopZYSeqNumber(1, SopConstants.SOP_STR_CSXMMC);
                resourceBean.setNumber(number);
                resourceBean.setName(name);
                resourceBean.setZylb(zylb);
                resourceBean.setRemark(description);
                resourceBean.setType(SopConstants.SOP_IBA_PARAMETERSNAME);
                resourceBean.setRowNum(i + 1);
                resourceBeanList.add(resourceBean);
//                String typeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.ParametersName";
//                String folderPath = "/Default/SOP资源/参数项目名称";
//                String sopType = SopConstants.SOP_STR_CSXMMC;
//                WTContainer container = WTContainerUtil.getContainerByName("工艺资源库");
//                String number = SopUtil.getSopZYSeqNumber(1, sopType);
//                MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName, description);
//                setRemarkValue(tooling, description);
//                setCSXMMCIBA(tooling, typeName, zylb);
            }
        } catch (IOException e) {
            return "读取文件错误</br>" + e.getLocalizedMessage() + "</br>";
        }
        return sb.toString();
    }

    @SuppressWarnings("resource")
    private static String getWZLB(File file) throws Exception {
        System.out.println("get excel info start");
        resourceBeanList = new ArrayList<SopResourceImportBean>();
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
            Row row = null;
            for (int i = 1; i <= rows; i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String name = getValue(row.getCell(1)).toString().trim();// 名称
                if (name == null || name.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到名称;</br>");
                    continue;
                }
                String zylb = getValue(row.getCell(2)).toString().trim();// 专业类别
                if (zylb == null || zylb.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到专业类别;</br>");
                    continue;
                } else {
                    MPMTooling tooling = MPMResourceUtil.getMPMToolingByName(zylb, SopConstants.SOP_TYPE_SPECIALIZEDTYPE);
                    if (tooling == null) {
                        sb.append("第").append(i + 1).append("行系统中不存在名称为[" + zylb + "]的专业类别;</br>");
                        continue;
                    }
                }
                String description = getValue(row.getCell(3)).toString().trim();// 说明

                Map<String, String> ibaMap = new HashMap<String, String>();
                ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, zylb);
                List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK, null, name, null, ibaMap, true,SopConstants.SOP_TYPE_MATERIALCATEGORY);
                if (wtPartList != null && wtPartList.size() > 0) {
                    sb.append("第").append(i + 1).append("行名称为[" + name + "]的物资类别已存在;</br>");
                    continue;
                }

                resourceBean = new SopResourceImportBean();
                String number = SopUtil.getSopZYSeqNumber(1, SopConstants.SOP_STR_WZLB);
                resourceBean.setNumber(number);
                resourceBean.setName(name);
                resourceBean.setZylb(zylb);
                resourceBean.setRemark(description);
                resourceBean.setType(SopConstants.SOP_IBA_MATERIALCATEGORY);
                resourceBean.setRowNum(i + 1);
                resourceBeanList.add(resourceBean);

//                String typeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.MaterialCategory";
//                String folderPath = "/Default/SOP资源/物资类别";
//                WTContainer container = WTContainerUtil.getContainerByName("工艺资源库");
//
//                MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName, description);
//                setRemarkValue(tooling, description);
//                setWZLBIBA(tooling, typeName, ZYLB);

            }
        } catch (IOException e) {
            return "读取文件错误</br>" + e.getLocalizedMessage() + "</br>";
        }
        return sb.toString();
    }

    @SuppressWarnings("resource")
    private static String getCZMC(File file) throws Exception {
        System.out.println("get excel info start");
        resourceBeanList = new ArrayList<SopResourceImportBean>();
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
            Row row = null;
            for (int i = 1; i <= rows; i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String name = getValue(row.getCell(1)).toString().trim();// 名称
                if (name == null || name.isEmpty()) {
                    sb.append("第").append(i + 1).append("行未读取到名称;</br>");
                    continue;
                }
                String description = getValue(row.getCell(2)).toString().trim();// 说明

                Map<String, String> ibaMap = new HashMap<String, String>();
                List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK, null, name, null, ibaMap, true,SopConstants.SOP_TYPE_OPERATIONNAME);
                if (wtPartList != null && wtPartList.size() > 0) {
                    sb.append("第").append(i + 1).append("行名称为[" + name + "]的操作名称已存在;</br>");
                    continue;
                }

                resourceBean = new SopResourceImportBean();
                String number = SopUtil.getSopZYSeqNumber(1, SopConstants.SOP_STR_CZMC);
                resourceBean.setNumber(number);
                resourceBean.setName(name);
                resourceBean.setRemark(description);
                resourceBean.setType(SopConstants.SOP_IBA_OPERATIONNAME);
                resourceBean.setRowNum(i + 1);
                resourceBeanList.add(resourceBean);
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
            return "";
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

    private static void setRemarkValue(IBAHolder ibaHolder, String value) throws WTException, WTPropertyVetoException, RemoteException {
        Map<String, String> ibaMap = new HashMap<String, String>();
        if (value != null && !"".equals(value) && !"null".equals(value)) {
            ibaMap.put(SopConstants.SOP_IBA_REMARK, value);
        }

        IBAHelper helper = new IBAHelper(ibaHolder);
        helper.setIBAValue(ibaHolder, ibaMap);
    }

    private static void setZzcjValue(IBAHolder ibaHolder, String value) throws WTException, WTPropertyVetoException, RemoteException {
        Map<String, String> ibaMap = new HashMap<String, String>();
        if (value != null && !"".equals(value) && !"null".equals(value)) {
            ibaMap.put(SopConstants.SOP_IBA_ZZCJ, value);
        }
        IBAHelper helper = new IBAHelper(ibaHolder);
        helper.setIBAValue(ibaHolder, ibaMap);
    }

    private static void setEnglishNameValue(IBAHolder ibaHolder, String value) throws WTException, WTPropertyVetoException, RemoteException {
        Map<String, String> ibaMap = new HashMap<String, String>();
        if (value != null && !"".equals(value) && !"null".equals(value)) {
            ibaMap.put(SopConstants.SOP_IBA_ENGLISHNAME, value);
        }
        IBAHelper helper = new IBAHelper(ibaHolder);
        helper.setIBAValue(ibaHolder, ibaMap);
    }

    private static void setCSXMIBA(MPMTooling tooling, String typeName, String ZYLB, String GXMC, String CSZ, String name, String WZLB) throws WTException, WTPropertyVetoException, RemoteException {
        Map<String, String> ibaMap = new HashMap<String, String>();
        ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, ZYLB);
        ibaMap.put(SopConstants.SOP_IBA_PROCEDUCENAME, GXMC);
        ibaMap.put(SopConstants.SOP_IBA_PARAMETERSNAME, name);
        ibaMap.put(SopConstants.SOP_IBA_MATERIALCATEGORY, WZLB);
        if (CSZ != null && !"".equals(CSZ) && !"null".equals(CSZ)) {
            ibaMap.put(SopConstants.SOP_IBA_CANSHUZHI, CSZ);
        }
        IBAHelper helper = new IBAHelper(tooling);
        helper.setIBAValue(tooling, ibaMap);
    }

    private static void setZYLBIBA(MPMTooling tooling, String typeName, String ProfessionalCode) throws WTException, WTPropertyVetoException, RemoteException {
        Map<String, String> ibaMap = new HashMap<String, String>();
        ibaMap.put(SopConstants.SOP_IBA_PROFESSIONALCODE, ProfessionalCode);
        IBAHelper helper = new IBAHelper(tooling);
        helper.setIBAValue(tooling, ibaMap);
    }

    private static void setGXMCIBA(MPMTooling tooling, String typeName, String GXJH, String ZYLB) throws WTException, WTPropertyVetoException, RemoteException {
        Map<String, String> ibaMap = new HashMap<String, String>();
        ibaMap.put(SopConstants.SOP_IBA_GONGXUJIANHAO, GXJH);
        ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, ZYLB);
        IBAHelper helper = new IBAHelper(tooling);
        helper.setIBAValue(tooling, ibaMap);
    }

    private static void setCSXMMCIBA(MPMTooling tooling, String typeName, String ZYLB) throws WTException, WTPropertyVetoException, RemoteException {
        Map<String, String> ibaMap = new HashMap<String, String>();
        ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, ZYLB);
        IBAHelper helper = new IBAHelper(tooling);
        helper.setIBAValue(tooling, ibaMap);
    }

    private static void setWZLBIBA(MPMTooling tooling, String typeName, String ZYLB) throws WTException, WTPropertyVetoException, RemoteException {
        Map<String, String> ibaMap = new HashMap<String, String>();
        ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, ZYLB);
        IBAHelper helper = new IBAHelper(tooling);
        helper.setIBAValue(tooling, ibaMap);
    }

    private static String trim(String str) {
        if (str == null || "".equals(str)) {
            return "";
        } else {
            str = str.trim();
            return str;
        }
    }

    /**
     * 设置IBA属性
     *
     * @param tooling
     * @param ibaMap
     * @throws Exception
     */
    private static void setIBAValue(MPMTooling tooling, Map<String, String> ibaMap) throws Exception {
        IBAHelper helper = new IBAHelper(tooling);
        helper.setIBAValue(tooling, ibaMap);
    }

    private static String getLastestNumber(String pre, String typeName, String lastestNumber) throws Exception {
        lastestNumber = pre + SopUtil.getSopZYSeqNumber(1, pre);
        MPMTooling tooling = MPMResourceUtil.getMPMToolingByNumber(lastestNumber, typeName);
        if (tooling != null) {
            SopUtil.getSopZYSeqNumber(2, pre);
            lastestNumber = getLastestNumber(pre, typeName, lastestNumber);
        }
        return lastestNumber;
    }
}
