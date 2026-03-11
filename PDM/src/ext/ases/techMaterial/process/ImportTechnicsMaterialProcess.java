package ext.ases.techMaterial.process;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wt.fc.PersistenceServerHelper;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.glaway.mpm.util.PropertiesUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ases.techMaterial.TechnicsMaterialEntries;
import ext.ases.techMaterial.gwpersistable.GwPersistenceHelper;
import ext.ases.techMaterial.model.TechnicsMaterialLink;
import ext.ases.techMaterial.util.TechnicsMaterialUtils;
import ext.casc.sop.util.StringUtil;

public class ImportTechnicsMaterialProcess extends DefaultObjectFormProcessor {

    private static String tmp_dir = PropertiesUtil.getTempPath() + File.separator;
    static final int BUFFER = 2048;

    @Override
    public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> listBean) throws WTException {
        System.out.println("======================import technicsMaterialInfo start======================");
        FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
        FeedbackMessage feedbackMessage = new FeedbackMessage();
        Object fileMap = nmCommandBean.getMap().get("fileUploadMap");
        String fileName = nmCommandBean.getTextParameter("xlsFile");
        if (fileName.contains("\\")) {
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.length());
        }
        if (!fileName.contains("标准紧固件") && !fileName.contains("电子元器件") && !fileName.contains("非金属材料") && !fileName.contains("复合材料")
                && !fileName.contains("金属材料") && !fileName.contains("机电材料") && !fileName.contains("火工品")) {
            result.setStatus(FormProcessingStatus.FAILURE);
            FeedbackMessage fMessage = new FeedbackMessage();
            fMessage.addMessage("文件命名不规范");
            result.addFeedbackMessage(fMessage);
            return result;
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
                        Map<String, String> beforeImportInfo = beforeImportTechnicsMaterialInfo(xlsFile);
                        String returnCode = beforeImportInfo.get("returnCode");
                        if ("1".equals(returnCode)) {
                            String msg = beforeImportInfo.get("msg");
                            result.setStatus(FormProcessingStatus.FAILURE);
                            FeedbackMessage fMessage = new FeedbackMessage();
                            fMessage.addMessage(msg);
                            result.addFeedbackMessage(fMessage);
                            return result;
                        }
                        // 如判断没问题导入数据
                        boolean importData = importData(xlsFile, fileName);
                        if (importData) {
                            feedbackMessage.addMessage("文件导入成功");
                            result.addFeedbackMessage(feedbackMessage);
                            result.setStatus(FormProcessingStatus.SUCCESS);
                        } else {
                            feedbackMessage.addMessage("文件导入失败");
                            result.addFeedbackMessage(feedbackMessage);
                            result.setStatus(FormProcessingStatus.FAILURE);
                        }

                    } else {
                        System.out.println("file is not exist");
                    }
                } catch (Exception e) {
                    feedbackMessage.addMessage("文件导入失败");
                    result.addFeedbackMessage(feedbackMessage);
                    result.setStatus(FormProcessingStatus.FAILURE);
                }
            }
        }
        System.out.println("======================import technicsMaterialInfo end======================");
        return result;
    }

    /**
     * 导入数据
     *
     * @param <E>
     * @param file
     * @return
     */
    @SuppressWarnings("all")
    private static boolean importData(File file, String fileName) {
        System.out.println("=====importData===start======");
        boolean flag = true;
        InputStream is;
        Workbook workbook = null;
        try {
            is = new FileInputStream(file);
            if (file.getName().endsWith("xls")) {
                workbook = new HSSFWorkbook(is);
            } else if (file.getName().endsWith("xlsx")) {
                workbook = new XSSFWorkbook(is);
            }
            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            Row ZeroRow = sheet.getRow(0);
            // 获取总列数
            short lastCellNum = ZeroRow.getLastCellNum();
            // 记录每一个列名
            Map<Integer, String> columnMap = new HashMap<Integer, String>();
            for (int i = 0; i < lastCellNum; i++) {
                Cell cell = ZeroRow.getCell(i);
                String titleValue = getValue(cell).toString();
                columnMap.put(i, titleValue);
                // 根据数据字典的名称查询oid
                if ("设计编码".equals(titleValue)||"物资分类".equals(titleValue)) {
                    continue;
                }
                String oidByName = TechnicsMaterialUtils.getTMOidByName(titleValue);
                Map<String, Object> map = TechnicsMaterialUtils.getTechncsiMaterialOidByName(titleValue);
                Set<String> dicSet = (Set<String>) map.get("dicSet");
                // 查询名称对应的数据字典值
                Set<String> set = new HashSet<String>();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date date = new Date();
                String time = simpleDateFormat.format(date);
                WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
                System.out.println("=====insertTechnicsMaterialLink===start======");
                for (int j = 1; j <= lastRowNum; j++) {
                    Row row = sheet.getRow(j);
                    String dicName = TechnicsMaterialUtils.leftAndRightTrim(getValue(row.getCell(i)).toString());
                    if (!StringUtil.isEmpty(dicName) && !dicSet.contains(dicName)) {
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("INSERT INTO TECHNICSMATERIALLINK(\"GWKEYID\", \"TECHNICSMATERIALID\", \"DICTIONARYID\",  \"TMCREATETIME\", \"TMCREATOR\") VALUES (sys_guid(),");
                        stringBuffer.append("'"+oidByName + "' ,");
                        stringBuffer.append("'"+dicName +  "' ,");
                        stringBuffer.append("'"+time +  "' ,");
                        stringBuffer.append("'"+currentUser.getFullName() + "' )");
                        System.out.println(stringBuffer.toString());
                        TechnicsMaterialUtils.insertTechnicsMaterialLink(stringBuffer.toString());
                    }
                }
            }
            System.out.println("=====insertTechnicsMaterialLink===end======");
            if (fileName.contains("标准紧固件")) {
                // 按行遍历
                for (int i = 1; i <= lastRowNum; i++) {
                    LinkedHashMap<String, String> attrMap = new LinkedHashMap<String, String>();
                    Row row = sheet.getRow(i);
                    short lastCellNum2 = row.getLastCellNum();

                    for (int j = 0; j < lastCellNum2; j++) {
                        Cell cell = row.getCell(j);
                        String attrName = getValue(cell).toString();
                        attrMap.put(TechnicsMaterialUtils.standardInfoMap.get(columnMap.get(j)), attrName);
                    }
                    if (attrMap.size() != 0) {
                        StringBuffer valueStirng = new StringBuffer();
                        // 查询数据库中是否存在
                        boolean entriesFlag = TechnicsMaterialUtils.matchSameStandardInfo(attrMap, "TMESTANDPARTLINK");
                        if (!entriesFlag) {
                            TechnicsMaterialEntries material = TechnicsMaterialEntries.newTechnicsMaterial();
                            // 设置生命周期为已发布
                            LifeCycleState materialState = LifeCycleState.newLifeCycleState();
                            materialState.setState(State.toState("APPROVED"));
                            material.setState(materialState);
                            // String uuid = "GYWZTM" + TechnicsMaterialUtils.getUqipNumber();
                            material.setName(attrMap.get("WZMC"));
                            material.setNumber(attrMap.get("WZBM"));
                            material.setDescription("标准紧固件");
                            WTContainerRef wtContainerRef = TechnicsMaterialUtils.getWTContainerRef(WTContainer.class, "工艺物资信息库");
                            long nowtime = Calendar.getInstance().getTimeInMillis();
                            Timestamp createStamp = new Timestamp(nowtime);
                            material.setContainerReference(wtContainerRef);
                            Folder folder = FolderHelper.service.getFolder("/Default/工艺物资条目/标准紧固件", wtContainerRef);
                            FolderHelper.assignLocation(material, folder);
                            material = (TechnicsMaterialEntries) PersistenceServerHelper.manager.store(material, createStamp, createStamp);
                            String id = material.getPersistInfo().getObjectIdentifier().getStringValue();
                            // 建立关联关系
                            TechnicsMaterialUtils.saveTechnicsMaterialEntriesLink(id, attrMap, "TMESTANDPARTLINK");

                        }

                    }
                }
            } else if (fileName.contains("电子元器件")) {
                // 按行遍历
                for (int i = 1; i <= lastRowNum; i++) {
                    LinkedHashMap<String, String> attrMap = new LinkedHashMap<String, String>();
                    Row row = sheet.getRow(i);
                    short lastCellNum2 = row.getLastCellNum();
                    for (int j = 0; j < lastCellNum2; j++) {
                        Cell cell = row.getCell(j);
                        String attrName = getValue(cell).toString();
                        attrMap.put(TechnicsMaterialUtils.eleComponentsInfoMap.get(columnMap.get(j)), attrName);
                    }
                    if (attrMap.size() != 0) {
                        StringBuffer valueStirng = new StringBuffer();
                        System.out.println("--matchSameStandardInfo----");
                        // 查询数据库中是否存在
                        boolean entriesFlag = TechnicsMaterialUtils.matchSameStandardInfo(attrMap, "TMEELECOMPONENTSPARTLINK");
                        if (!entriesFlag) {
                            TechnicsMaterialEntries material = TechnicsMaterialEntries.newTechnicsMaterial();
                            // 设置生命周期为已发布
                            LifeCycleState materialState = LifeCycleState.newLifeCycleState();
                            materialState.setState(State.toState("APPROVED"));
                            material.setState(materialState);
                            material.setName(attrMap.get("WZMC"));
                            material.setNumber(attrMap.get("WZBM"));
                            material.setDescription("电子元器件");
                            WTContainerRef wtContainerRef = TechnicsMaterialUtils.getWTContainerRef(WTContainer.class, "工艺物资信息库");
                            long nowtime = Calendar.getInstance().getTimeInMillis();
                            Timestamp createStamp = new Timestamp(nowtime);
                            material.setContainerReference(wtContainerRef);
                            Folder folder = FolderHelper.service.getFolder("/Default/工艺物资条目/电子元器件", wtContainerRef);
                            FolderHelper.assignLocation(material, folder);
                            material = (TechnicsMaterialEntries) PersistenceServerHelper.manager.store(material, createStamp, createStamp);
                            String id = material.getPersistInfo().getObjectIdentifier().getStringValue();
                            // 建立关联关系
                            TechnicsMaterialUtils.saveTechnicsMaterialEntriesLink(id, attrMap, "TMEELECOMPONENTSPARTLINK");

                        }

                    }
                }
            } else if (fileName.contains("非金属材料")) {
                // 按行遍历
                for (int i = 1; i <= lastRowNum; i++) {
                    LinkedHashMap<String, String> attrMap = new LinkedHashMap<String, String>();
                    Row row = sheet.getRow(i);
                    short lastCellNum2 = row.getLastCellNum();
                    for (int j = 0; j < lastCellNum2; j++) {
                        Cell cell = row.getCell(j);
                        String attrName = getValue(cell).toString();
                        attrMap.put(TechnicsMaterialUtils.nonmetallicInfoMap.get(columnMap.get(j)), attrName);
                    }
                    if (attrMap.size() != 0) {
                        StringBuffer valueStirng = new StringBuffer();
                        // 查询数据库中是否存在
                        boolean entriesFlag = TechnicsMaterialUtils.matchSameStandardInfo(attrMap, "TMENONMETALLICPARTLINK");
                        if (!entriesFlag) {
                            TechnicsMaterialEntries material = TechnicsMaterialEntries.newTechnicsMaterial();
                            // 设置生命周期为已发布
                            LifeCycleState materialState = LifeCycleState.newLifeCycleState();
                            materialState.setState(State.toState("APPROVED"));
                            material.setState(materialState);
                            material.setName(attrMap.get("WZMC"));
                            material.setNumber(attrMap.get("WZBM"));
                            material.setDescription("非金属材料");
                            WTContainerRef wtContainerRef = TechnicsMaterialUtils.getWTContainerRef(WTContainer.class, "工艺物资信息库");
                            long nowtime = Calendar.getInstance().getTimeInMillis();
                            Timestamp createStamp = new Timestamp(nowtime);
                            material.setContainerReference(wtContainerRef);
                            Folder folder = FolderHelper.service.getFolder("/Default/工艺物资条目/非金属材料", wtContainerRef);
                            FolderHelper.assignLocation(material, folder);
                            material = (TechnicsMaterialEntries) PersistenceServerHelper.manager.store(material, createStamp, createStamp);
                            String id = material.getPersistInfo().getObjectIdentifier().getStringValue();
                            // 建立关联关系
                            TechnicsMaterialUtils.saveTechnicsMaterialEntriesLink(id, attrMap, "TMENONMETALLICPARTLINK");

                        }
                    }
                }
            } else if (fileName.contains("复合材料")) {
                // 按行遍历
                for (int i = 1; i <= lastRowNum; i++) {
                    LinkedHashMap<String, String> attrMap = new LinkedHashMap<String, String>();
                    Row row = sheet.getRow(i);
                    short lastCellNum2 = row.getLastCellNum();
                    for (int j = 0; j < lastCellNum2; j++) {
                        Cell cell = row.getCell(j);
                        String attrName = getValue(cell).toString();
                        attrMap.put(TechnicsMaterialUtils.compoundMaterialInfoMap.get(columnMap.get(j)), attrName);
                    }
                    if (attrMap.size() != 0) {
                        StringBuffer valueStirng = new StringBuffer();
                        // 查询数据库中是否存在
                        boolean entriesFlag = TechnicsMaterialUtils.matchSameStandardInfo(attrMap, "TMECOMPOUNDMATERIALPARTLINK");
                        if (!entriesFlag) {
                            TechnicsMaterialEntries material = TechnicsMaterialEntries.newTechnicsMaterial();
                            // 设置生命周期为已发布
                            LifeCycleState materialState = LifeCycleState.newLifeCycleState();
                            materialState.setState(State.toState("APPROVED"));
                            material.setState(materialState);
                            material.setName(attrMap.get("WZMC"));
                            material.setNumber(attrMap.get("WZBM"));
                            material.setDescription("复合材料");
                            WTContainerRef wtContainerRef = TechnicsMaterialUtils.getWTContainerRef(WTContainer.class, "工艺物资信息库");
                            long nowtime = Calendar.getInstance().getTimeInMillis();
                            Timestamp createStamp = new Timestamp(nowtime);
                            material.setContainerReference(wtContainerRef);
                            Folder folder = FolderHelper.service.getFolder("/Default/工艺物资条目/复合材料", wtContainerRef);
                            FolderHelper.assignLocation(material, folder);
                            material = (TechnicsMaterialEntries) PersistenceServerHelper.manager.store(material, createStamp, createStamp);
                            String id = material.getPersistInfo().getObjectIdentifier().getStringValue();
                            // 建立关联关系
                            TechnicsMaterialUtils.saveTechnicsMaterialEntriesLink(id, attrMap, "TMECOMPOUNDMATERIALPARTLINK");

                        }
                    }
                }
            } else if (fileName.contains("金属材料")) {
                // 按行遍历
                for (int i = 1; i <= lastRowNum; i++) {
                    LinkedHashMap<String, String> attrMap = new LinkedHashMap<String, String>();
                    Row row = sheet.getRow(i);
                    short lastCellNum2 = row.getLastCellNum();
                    for (int j = 0; j < lastCellNum2; j++) {
                        Cell cell = row.getCell(j);
                        String attrName = getValue(cell).toString();
                        attrMap.put(TechnicsMaterialUtils.metallicInfoMap.get(columnMap.get(j)), attrName);
                    }
                    if (attrMap.size() != 0) {
                        StringBuffer valueStirng = new StringBuffer();
                        // 查询数据库中是否存在
                        boolean entriesFlag = TechnicsMaterialUtils.matchSameStandardInfo(attrMap, "TMEMETALLICPARTLINK");
                        if (!entriesFlag) {
                            TechnicsMaterialEntries material = TechnicsMaterialEntries.newTechnicsMaterial();
                            // 设置生命周期为已发布
                            LifeCycleState materialState = LifeCycleState.newLifeCycleState();
                            materialState.setState(State.toState("APPROVED"));
                            material.setState(materialState);
                            material.setName(attrMap.get("WZMC"));
                            material.setNumber(attrMap.get("WZBM"));
                            material.setDescription("金属材料");
                            WTContainerRef wtContainerRef = TechnicsMaterialUtils.getWTContainerRef(WTContainer.class, "工艺物资信息库");
                            long nowtime = Calendar.getInstance().getTimeInMillis();
                            Timestamp createStamp = new Timestamp(nowtime);
                            material.setContainerReference(wtContainerRef);
                            Folder folder = FolderHelper.service.getFolder("/Default/工艺物资条目/金属材料", wtContainerRef);
                            FolderHelper.assignLocation(material, folder);
                            material = (TechnicsMaterialEntries) PersistenceServerHelper.manager.store(material, createStamp, createStamp);
                            String id = material.getPersistInfo().getObjectIdentifier().getStringValue();
                            // 建立关联关系
                            TechnicsMaterialUtils.saveTechnicsMaterialEntriesLink(id, attrMap, "TMEMETALLICPARTLINK");

                        }
                    }
                }
            } else if (fileName.contains("机电材料")) {
                // 按行遍历
                for (int i = 1; i <= lastRowNum; i++) {
                    LinkedHashMap<String, String> attrMap = new LinkedHashMap<String, String>();
                    Row row = sheet.getRow(i);
                    short lastCellNum2 = row.getLastCellNum();
                    for (int j = 0; j < lastCellNum2; j++) {
                        Cell cell = row.getCell(j);
                        String attrName = getValue(cell).toString();
                        attrMap.put(TechnicsMaterialUtils.eleMachineInfoMap.get(columnMap.get(j)), attrName);
                    }
                    if (attrMap.size() != 0) {
                        StringBuffer valueStirng = new StringBuffer();
                        // 查询数据库中是否存在
                        boolean entriesFlag = TechnicsMaterialUtils.matchSameStandardInfo(attrMap, "TMEEleMachinePartLink");
                        if (!entriesFlag) {
                            TechnicsMaterialEntries material = TechnicsMaterialEntries.newTechnicsMaterial();
                            // 设置生命周期为已发布
                            LifeCycleState materialState = LifeCycleState.newLifeCycleState();
                            materialState.setState(State.toState("APPROVED"));
                            material.setState(materialState);
                            material.setName(attrMap.get("WZMC"));
                            material.setNumber(attrMap.get("WZBM"));
                            material.setDescription("机电材料");
                            WTContainerRef wtContainerRef = TechnicsMaterialUtils.getWTContainerRef(WTContainer.class, "工艺物资信息库");
                            long nowtime = Calendar.getInstance().getTimeInMillis();
                            Timestamp createStamp = new Timestamp(nowtime);
                            material.setContainerReference(wtContainerRef);
                            Folder folder = FolderHelper.service.getFolder("/Default/工艺物资条目/机电材料", wtContainerRef);
                            FolderHelper.assignLocation(material, folder);
                            material = (TechnicsMaterialEntries) PersistenceServerHelper.manager.store(material, createStamp, createStamp);
                            String id = material.getPersistInfo().getObjectIdentifier().getStringValue();
                            // 建立关联关系
                            TechnicsMaterialUtils.saveTechnicsMaterialEntriesLink(id, attrMap, "TMEEleMachinePartLink");

                        }
                    }
                }
            } else if (fileName.contains("火工品")) {
                // 按行遍历
                for (int i = 1; i <= lastRowNum; i++) {
                    LinkedHashMap<String, String> attrMap = new LinkedHashMap<String, String>();
                    Row row = sheet.getRow(i);
                    short lastCellNum2 = row.getLastCellNum();
                    for (int j = 0; j < lastCellNum2; j++) {
                        Cell cell = row.getCell(j);
                        String attrName = getValue(cell).toString();
                        attrMap.put(TechnicsMaterialUtils.expDeviceInfoMap.get(columnMap.get(j)), attrName);
                    }
                    if (attrMap.size() != 0) {
                        StringBuffer valueStirng = new StringBuffer();
                        // 查询数据库中是否存在
                        boolean entriesFlag = TechnicsMaterialUtils.matchSameStandardInfo(attrMap, "TMEExpDevicePartLink");
                        if (!entriesFlag) {
                            TechnicsMaterialEntries material = TechnicsMaterialEntries.newTechnicsMaterial();
                            // 设置生命周期为已发布
                            LifeCycleState materialState = LifeCycleState.newLifeCycleState();
                            materialState.setState(State.toState("APPROVED"));
                            material.setState(materialState);
                            material.setName(attrMap.get("WZMC"));
                            material.setNumber(attrMap.get("WZBM"));
                            material.setDescription("火工品");
                            WTContainerRef wtContainerRef = TechnicsMaterialUtils.getWTContainerRef(WTContainer.class, "工艺物资信息库");
                            long nowtime = Calendar.getInstance().getTimeInMillis();
                            Timestamp createStamp = new Timestamp(nowtime);
                            material.setContainerReference(wtContainerRef);
                            Folder folder = FolderHelper.service.getFolder("/Default/工艺物资条目/火工品", wtContainerRef);
                            FolderHelper.assignLocation(material, folder);
                            material = (TechnicsMaterialEntries) PersistenceServerHelper.manager.store(material, createStamp, createStamp);
                            String id = material.getPersistInfo().getObjectIdentifier().getStringValue();
                            // 建立关联关系
                            TechnicsMaterialUtils.saveTechnicsMaterialEntriesLink(id, attrMap, "TMEExpDevicePartLink");

                        }
                    }
                }
            }
            System.out.println("========end======");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;

    }

    /**
     * 导入Excel前校验
     *
     * @return
     */
    private static Map<String, String> beforeImportTechnicsMaterialInfo(File file) {
        System.out.println("====beforeImportTechnicsMaterialInfo---start---=====");
        Map<String, String> infoMap = new HashMap<String, String>();
        StringBuffer stringBuffer = new StringBuffer();
        String returnCode = "0";
        InputStream is;
        Workbook workbook = null;
        try {
            System.out.println("====FileInputStream---start---=====");
            is = new FileInputStream(file);
            if (file.getName().endsWith("xls")) {
                workbook = new HSSFWorkbook(is);
            } else if (file.getName().endsWith("xlsx")) {
                workbook = new XSSFWorkbook(is);
            } else {
                stringBuffer.append("该文件不是EXCEL,或格式不正确 </br>");
                infoMap.put("returnCode", "1");
                infoMap.put("msg", stringBuffer.toString());
                return infoMap;
            }
            System.out.println("====FileInputStream---start---=====");
            Sheet sheet = workbook.getSheetAt(0);
            Row ZeroRow = sheet.getRow(0);
            // 获取总列数
            short lastCellNum = ZeroRow.getLastCellNum();
            Set<String> sjbmSet = new HashSet<String>();
            System.out.println("====lastCellNum---start---=====");
            for (int i = 0; i < lastCellNum; i++) {
                Cell cell = ZeroRow.getCell(i);
                String titleValue = getValue(cell).toString();
                if ("设计编码".equals(titleValue)||"物资分类".equals(titleValue)) {
                    continue;
                }
                boolean flag = TechnicsMaterialUtils.hasTechncsiMaterialName(titleValue);
                if (!flag) {
                    int intl = i + 1;
                    stringBuffer.append("第" + intl + "列：" + titleValue + "在系统中不存在\n");
                    returnCode = "1";
                }
                System.out.println("========开始=====");
                if ("物资编码".equals(titleValue)) {
                    int lastRowNum = sheet.getLastRowNum();
                    for (int j = 1; j <= lastRowNum; j++) {
                        Row row = sheet.getRow(j);
                        String dicName = TechnicsMaterialUtils.leftAndRightTrim(getValue(row.getCell(i)).toString());
                        if (!StringUtil.isEmpty(dicName)) {
                           sjbmSet.add(dicName);
                        }
                    }
                    String msg = TechnicsMaterialUtils.validdateData(sjbmSet);
                    if (msg.length() != 0) {
                        stringBuffer.append(msg);
                        returnCode = "1";
                    }
                }
            }
            System.out.println("========结束=====");

        } catch (Exception e) {
            e.printStackTrace();
        }
        infoMap.put("returnCode", returnCode);
        infoMap.put("msg", stringBuffer.toString());
        return infoMap;

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

}
