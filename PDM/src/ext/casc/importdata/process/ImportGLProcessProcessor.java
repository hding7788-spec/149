package ext.casc.importdata.process;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.importdata.productRB;
import ext.casc.mpm.process.*;
import ext.casc.report.technics.DownloadTechnicsReportUtil;
import ext.casc.util.DBUtil;
import ext.casc.util.Tools;
import ext.casc.util.WTUtil;
import ext.sast.common.fc.CmPersistenceHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import wt.httpgw.URLFactory;
import wt.pom.Transaction;
import wt.util.WTException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class ImportGLProcessProcessor implements Serializable {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ImportGLProcessProcessor.class);

    private static final long serialVersionUID = 1L;
    private static String MYRESOURCE = "ext.casc.importdata.productRB";

    /**
     * 导入工艺参数
     *
     * @param cb
     * @return
     * @throws WTException
     */
    public static FormResult importGLProcessParams(NmCommandBean cb) throws WTException {
        File temp_file = (File) cb.getRequest().getAttribute("file");
        File temp_xlsFile = (File) cb.getRequest().getAttribute("file2");
        ResourceBundle rb = ResourceBundle.getBundle(MYRESOURCE);
        FormResult form = new FormResult();
        try {
            String returnValue = importData(temp_xlsFile, "工艺参数");
            if (returnValue != null && !returnValue.equals("")) {
                String info = "";
                if (returnValue.charAt(0) == '1') {
                    info = "<TABLE width='100%' border=0 cellpadding=0 cellspacing=0><tr><font color='#FF0000'><b>" + rb.getString(productRB.IMPORTERRORMESSAGE_TITLES2) + "</b></font></tr><br>" + returnValue + "<tr><td><input type=button name='S1' value='" + rb.getString(productRB.IMPORTDATA_CLOSE) + "' onclick=\"javascript:window.close();\"></td></tr></TABLE><script>document.body.style.bgColor='#F5F6F0';</script>";

                } else {
                    info = "<TABLE width='100%' border=0 cellpadding=0 cellspacing=0><tr><font color='#FF0000'><b>" + rb.getString(productRB.IMPORTERRORMESSAGE_TITLES) + "</b></font></tr><br>" + returnValue + "<tr><td><input type=button name='S1' value='" + rb.getString(productRB.IMPORTDATA_CLOSE) + "' onclick=\"javascript:window.close();\"></td></tr></TABLE><script>document.body.style.bgColor='#F5F6F0';</script>";
                }
                form.setStatus(FormProcessingStatus.FAILURE);
                URLFactory urlfactory = new URLFactory();
                cb.getRequest().getSession().putValue("errorInfo", info);
                String url = urlfactory.getBaseHREF() + "netmarkets/jsp/ext/casc/importdata/importDataInfo.jsp?";
                form.setURL(url);
                form.setNextAction(FormResultAction.FORWARD);
                return form;
            } else {
                form.setStatus(FormProcessingStatus.SUCCESS);
                FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "数据导入成功");
                form.addFeedbackMessage(message);
                form.setNextAction(FormResultAction.REFRESH_OPENER);
                return form;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return form;
        } finally {
            if (temp_file != null) temp_file.delete();
        }
    }


    /**
     * 导入工艺知识
     *
     * @param cb
     * @return
     * @throws WTException
     */
    public static FormResult importGLProcessKnowledge(NmCommandBean cb) throws WTException {
        File temp_file = (File) cb.getRequest().getAttribute("file");
        File temp_xlsFile = (File) cb.getRequest().getAttribute("file2");
        ResourceBundle rb = ResourceBundle.getBundle(MYRESOURCE);
        FormResult form = new FormResult();
        try {
            String returnValue = importData(temp_xlsFile, "工艺知识");
            if (returnValue != null && !returnValue.equals("")) {
                String info = "";
                if (returnValue.charAt(0) == '1') {
                    info = "<TABLE width='100%' border=0 cellpadding=0 cellspacing=0><tr><font color='#FF0000'><b>" + rb.getString(productRB.IMPORTERRORMESSAGE_TITLES2) + "</b></font></tr><br>" + returnValue + "<tr><td><input type=button name='S1' value='" + rb.getString(productRB.IMPORTDATA_CLOSE) + "' onclick=\"javascript:window.close();\"></td></tr></TABLE><script>document.body.style.bgColor='#F5F6F0';</script>";

                } else {
                    info = "<TABLE width='100%' border=0 cellpadding=0 cellspacing=0><tr><font color='#FF0000'><b>" + rb.getString(productRB.IMPORTERRORMESSAGE_TITLES) + "</b></font></tr><br>" + returnValue + "<tr><td><input type=button name='S1' value='" + rb.getString(productRB.IMPORTDATA_CLOSE) + "' onclick=\"javascript:window.close();\"></td></tr></TABLE><script>document.body.style.bgColor='#F5F6F0';</script>";
                }
                form.setStatus(FormProcessingStatus.FAILURE);
                URLFactory urlfactory = new URLFactory();
                cb.getRequest().getSession().putValue("errorInfo", info);
                String url = urlfactory.getBaseHREF() + "netmarkets/jsp/ext/casc/importdata/importDataInfo.jsp?";
                form.setURL(url);
                form.setNextAction(FormResultAction.FORWARD);
                return form;
            } else {
                form.setStatus(FormProcessingStatus.SUCCESS);
                FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "数据导入成功");
                form.addFeedbackMessage(message);
                form.setNextAction(FormResultAction.REFRESH_OPENER);
                return form;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return form;
        } finally {
            if (temp_file != null) temp_file.delete();
        }
    }


    public static String importData(File file, String type) {
        String result = "";
        InputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(fileInputStream);
            if (type.equals("工艺参数")) {
                importSheet4Params(workbook, 0);
                importSheet4Params(workbook, 1);
                importSheet4Params(workbook, 2);
            } else if (type.equals("工艺知识")) {
                int sheetNos = workbook.getNumberOfSheets();
                for (int i = 0; i < sheetNos; i++) {
                    String sheetName = workbook.getSheetAt(i).getSheetName();
                    if (StringUtils.equals(sheetName, "工艺模板映射")) {
                        importSheet4Knowledge_ProcessTemplate(workbook, sheetName);
                    } else if (StringUtils.equals(sheetName, "工序模板映射")) {
                        importSheet4Knowledge_ProcessStepTemplate(workbook, sheetName);
                    } else {
                        // 判断sheetName在知识参数中是否存在，不存在弹出错误
                        List<GLProcessParams> list = ProcessUtil.queryGLProcessParams("", sheetName,"");
                        if (list.size() != 0) {
                            importSheet4Knowledge(workbook, sheetName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = e.getLocalizedMessage();
        } finally {
            try {
                fileInputStream.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    /**
     * 导入工艺卡片模板映射
     *
     * @param workbook
     * @param sheetName
     * @throws Exception
     */
    private static void importSheet4Knowledge_ProcessTemplate(Workbook workbook, String sheetName) throws Exception {
        StringBuilder throwMessage = new StringBuilder();
        Sheet sheet = workbook.getSheet(sheetName);

        String xuhao = "";
        String caiLiaoFenLei = "";
        String yaXiaXian = "";
        String reChuLi = "";
        String biaoMianChuLi = "";
        String processTemplate = "";
        String templateNumber = "";

        for (Row row : sheet) {
            if (row.getRowNum() == 0 || isRowEmpty(row)) {
                continue;
            }
            LOGGER.info("begin read the {} sheet,the {} row", sheetName, row.getRowNum());
            GLProcessTemplateLink link = new GLProcessTemplateLink();
            xuhao = getValue(row.getCell(0)).toString().trim();
            caiLiaoFenLei = getValue(row.getCell(1)).toString().trim();
            yaXiaXian = getValue(row.getCell(2)).toString().trim();
            reChuLi = getValue(row.getCell(3)).toString().trim();
            biaoMianChuLi = getValue(row.getCell(4)).toString().trim();
            processTemplate = getValue(row.getCell(5)).toString().trim();
            templateNumber = getValue(row.getCell(6)).toString().trim();

            link.setXuHao(xuhao);
            link.setCaiLiaoFenLei(caiLiaoFenLei);
            link.setYaXiaXian(yaXiaXian);
            link.setReChuLi(reChuLi);
            link.setBiaoMianChuLi(biaoMianChuLi);
            link.setProcessTemplate(processTemplate);
            link.setTemplateNumber(templateNumber);

            link.setKeyId(link.getXuHao() + "_" + link.getCaiLiaoFenLei() + "_" + link.getProcessTemplate());

            if (throwMessage.length() > 0) {
                throw new Exception(throwMessage.toString());
            }
            //根据方案上的规定，【压下陷、热处理、表面处理有值则保留该工序，无值则自动删除该工序】
            if (StringUtils.isEmpty(yaXiaXian) && StringUtils.isEmpty(reChuLi) && StringUtils.isEmpty(biaoMianChuLi)) {
                CmPersistenceHelper.manager.delete(link);
            } else {
                CmPersistenceHelper.manager.save(link);
            }
        }
    }

    /**
     * 导入工序卡片模板映射
     *
     * @param workbook
     * @param sheetName
     * @throws Exception
     */
    private static void importSheet4Knowledge_ProcessStepTemplate(Workbook workbook, String sheetName) throws Exception {
        StringBuilder throwMessage = new StringBuilder();
        Sheet sheet = workbook.getSheet(sheetName);

        int size = sheet.getLastRowNum();
        String stepName = "";
        String xuhao = "";
        String caiLiaoFenLei = "";
        String guiGe = "";
        String biaoMianChuLi = "";
        String processTemplate = "";
        String templateNum = "";

        for (int i = 0; i < size + 1; i++) {
            Row row = sheet.getRow(i);

            if (isRowEmpty(row)) {
                continue;
            }

            LOGGER.info("begin read the {} sheet,the {} row", sheetName, row.getRowNum());
            GLProcessStepTemplateLink link = new GLProcessStepTemplateLink();
            xuhao = getValue(row.getCell(0)).toString().trim();
            caiLiaoFenLei = getValue(row.getCell(1)).toString().trim();

            if ("表面处理".equals(stepName)) {
                biaoMianChuLi = getValue(row.getCell(2)).toString().trim();
            } else {
                guiGe = getValue(row.getCell(2)).toString().trim();
            }

            processTemplate = getValue(row.getCell(3)).toString().trim();
            templateNum = getValue(row.getCell(4)).toString().trim();

            if (StringUtils.isNotEmpty(xuhao) && StringUtils.isEmpty(caiLiaoFenLei) && StringUtils.isEmpty(guiGe) && StringUtils.isEmpty(processTemplate)) {
                //说明此行是工序名称行
                stepName = xuhao;
                i++; //递归到下两行
                continue;
            }

            link.setXuHao(xuhao);
            link.setStepName(stepName);
            link.setCaiLiaoFenLei(caiLiaoFenLei);
            link.setGuiGe(guiGe);
            link.setBiaoMianChuLi(biaoMianChuLi);
            link.setProcessStepTemplate(processTemplate);
            link.setTemplateNumber(templateNum);

            link.setKeyId(link.getStepName() + "_" + link.getProcessStepTemplate());

            if (throwMessage.length() > 0) {
                throw new Exception(throwMessage.toString());
            }
            //【无值则自动删除该工序】
            if (StringUtils.isEmpty(caiLiaoFenLei) && StringUtils.isEmpty(guiGe) && StringUtils.isEmpty(biaoMianChuLi)) {
                CmPersistenceHelper.manager.delete(link);
            } else {
                CmPersistenceHelper.manager.save(link);
            }
        }
    }

    //知识参数导入
    private static void importSheet4Knowledge(Workbook workbook, String sheetName) throws Exception {
        //StringBuilder throwMessage = new StringBuilder();
        Sheet sheet = workbook.getSheet(sheetName);
        StringBuilder heads = new StringBuilder();
        String cellValue = "";
        for (Row row : sheet) {
            if (row.getRowNum() == 0 || isRowEmpty(row)) {
                if (row.getRowNum() == 0 && heads.length() == 0) {
                    //写表头, 写入GLProcessLink里，导出时候使用
                    for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
                        cellValue = getValue(row.getCell(c)).toString().trim();
                        heads.append(cellValue).append(",");
                    }
                    heads = heads.deleteCharAt(heads.length() - 1);
                    GLProcessLink link = new GLProcessLink();
                    link.setParaName(sheetName);
                    link.setHeads(heads.toString());
                    link.setKeyId(sheetName);
                    CmPersistenceHelper.manager.save(link);
                }
                continue;
            }

            GLProcessKnowledge processKnowledge = new GLProcessKnowledge();
            processKnowledge.setSheetName(sheetName);

            for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
                cellValue = getValue(row.getCell(c)).toString().trim();
//                if (StringUtils.isEmpty(cellValue)) {
//                    continue;
//                }
                switch (c) {
                    case 0:
                        processKnowledge.setXuHao(cellValue);
                        break;
                   /* case 1:
                        processKnowledge.setState(cellValue);
                        break;*/
                    case 1:
                        processKnowledge.setColumn1(cellValue);
                        break;
                    case 2:
                        processKnowledge.setColumn2(cellValue);
                        break;
                    case 3:
                        processKnowledge.setColumn3(cellValue);
                        break;
                    case 4:
                        processKnowledge.setColumn4(cellValue);
                        break;
                    case 5:
                        processKnowledge.setColumn5(cellValue);
                        break;
                    case 6:
                        processKnowledge.setColumn6(cellValue);
                        break;
                    case 7:
                        processKnowledge.setColumn7(cellValue);
                        break;
                    case 8:
                        processKnowledge.setColumn8(cellValue);
                        break;
                    default:
                        break;
                }
            }
            processKnowledge.setKeyId(processKnowledge.getSheetName() + "_" + processKnowledge.getXuHao());
            CmPersistenceHelper.manager.save(processKnowledge);
        }
    }

    //工艺参数导入
    private static void importSheet4Params(Workbook workbook, int sheetNo) throws Exception {
        StringBuilder throwMessage = new StringBuilder();
        Sheet sheet = workbook.getSheetAt(sheetNo);
        int actionNo = 6;  //操作目的列
        String knowledgeInferencePara = "";
        String knowledgeOutputPara = "";
        for (Row row : sheet) {

            if (row.getRowNum() == 0 || isRowEmpty(row)) {
                continue;
            }
            LOGGER.info("begin read {} sheet,the {} row", sheetNo, row.getRowNum());
            GLProcessParams processParams = new GLProcessParams();
            processParams.setGyNumber(getValue(row.getCell(1)).toString().trim()); //编号
            processParams.setGyName(getValue(row.getCell(2)).toString().trim());   //名称
            processParams.setKeyId(processParams.getGyNumber() + "_" + processParams.getGyName());
            processParams.setParameterCategory(getValue(row.getCell(3)).toString().trim());  //参数类别
            processParams.setProcessCategory(getValue(row.getCell(4)).toString().trim());  //工艺类别
            if (sheetNo == 0) {
                processParams.setUnit(getValue(row.getCell(5)).toString().trim());  //计量单位
            } else if (sheetNo == 1) {
                processParams.setEnumValues(getValue(row.getCell(5)).toString().trim());//枚举
                processParams.setOutputRules(getValue(row.getCell(6)).toString().trim());
                actionNo = 7;
            } else if (sheetNo == 2) {
                knowledgeInferencePara = getValue(row.getCell(5)).toString().trim();
               // knowledgeInferencePara = StringUtils.split(knowledgeInferencePara, "|")[0];
                knowledgeOutputPara = getValue(row.getCell(6)).toString().trim();
                //knowledgeOutputPara = StringUtils.split(knowledgeOutputPara, "|")[0];
                /*if (ProcessUtil.getProcessParamsList(knowledgeInferencePara).isEmpty()) {
                    throwMessage.append("知识参数sheet页面").append("第").append(row.getRowNum() + 1).append("行").append("知识参数,的知识推理参数在系统中不存在").append("<br>");
                } else {*/
                    processParams.setKnowledgeInferencePara(knowledgeInferencePara);
                //}
                /*if (ProcessUtil.getProcessParamsList(knowledgeOutputPara).isEmpty()) {
                    throwMessage.append("知识参数sheet页面").append("第").append(row.getRowNum() + 1).append("行").append("知识参数,的知识输出参数在系统中不存在").append("<br>");
                } else {*/
                    processParams.setKnowledgeOutputPara(knowledgeOutputPara);
               // }
                processParams.setOutputRules(getValue(row.getCell(7)).toString().trim());
                actionNo = 8;
            }
            processParams.setSource("设计");
//            抛出验证
            if (throwMessage.length() > 0) {
                throw new Exception(throwMessage.toString());
            }
            if ("删除".equals(row.getCell(actionNo).getStringCellValue())) {
                CmPersistenceHelper.manager.delete(processParams);
            } else {
                CmPersistenceHelper.manager.save(processParams);
            }
        }
    }

    // 检查一行是否为空
    private static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                return false;
            }
        }
        return true;
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


    public static FormResult importGLProcessOtherKnowledge(NmCommandBean cb) throws WTException {

        File temp_xlsFile = (File) cb.getRequest().getAttribute("file2");
        ResourceBundle rb = ResourceBundle.getBundle(MYRESOURCE);
        FormResult form = new FormResult();
        try {

            String returnValue = importOtherKnowledgeData(temp_xlsFile);
            if (returnValue != null && !returnValue.equals("")) {
                String info = "";
                if (returnValue.charAt(0) == '1') {
                    info = "<TABLE width='100%' border=0 cellpadding=0 cellspacing=0><tr><font color='#FF0000'><b>" + rb.getString(productRB.IMPORTERRORMESSAGE_TITLES2) + "</b></font></tr><br>" + returnValue + "<tr><td><input type=button name='S1' value='" + rb.getString(productRB.IMPORTDATA_CLOSE) + "' onclick=\"javascript:window.close();\"></td></tr></TABLE><script>document.body.style.bgColor='#F5F6F0';</script>";

                } else {
                    info = "<TABLE width='100%' border=0 cellpadding=0 cellspacing=0><tr><font color='#FF0000'><b>" + rb.getString(productRB.IMPORTERRORMESSAGE_TITLES) + "</b></font></tr><br>" + returnValue + "<tr><td><input type=button name='S1' value='" + rb.getString(productRB.IMPORTDATA_CLOSE) + "' onclick=\"javascript:window.close();\"></td></tr></TABLE><script>document.body.style.bgColor='#F5F6F0';</script>";
                }
                form.setStatus(FormProcessingStatus.FAILURE);
                URLFactory urlfactory = new URLFactory();
                cb.getRequest().getSession().putValue("errorInfo", info);
                String url = urlfactory.getBaseHREF() + "netmarkets/jsp/ext/casc/importdata/importDataInfo.jsp?";
                form.setURL(url);
                form.setNextAction(FormResultAction.FORWARD);
                return form;
            } else {
                FileUtils.copyFile(temp_xlsFile, new File(DownloadTechnicsReportUtil.templateDir+File.separator+"ProcessOtherKnowledge.xlsx"));
                form.setStatus(FormProcessingStatus.SUCCESS);
                FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "数据导入成功");
                form.addFeedbackMessage(message);
                form.setNextAction(FormResultAction.REFRESH_OPENER);
                return form;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return form;
        } finally {

        }
    }

    private static String importOtherKnowledgeData(File temp_xlsFile) {
        Transaction tx = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(temp_xlsFile);
            Workbook workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            tx = new Transaction();
            tx.start();

            DBUtil.deleteAll("GLProcessOtherKnowledge");
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // Skip the header row
                }
                GLProcessOtherKnowledge ok = new GLProcessOtherKnowledge();
                String zhuanye = WTUtil.getCellStringValue(row.getCell(0));
                String column1 = WTUtil.getCellStringValue(row.getCell(1));

                if(Tools.isNull(zhuanye)&&Tools.isNull(column1)){
                	continue;
                }
                String column2 = WTUtil.getCellStringValue(row.getCell(2));
                String column3 = WTUtil.getCellStringValue(row.getCell(3));
                String column4 = WTUtil.getCellStringValue(row.getCell(4));
                String column5 = WTUtil.getCellStringValue(row.getCell(5));
                ok.setKeyId(UUID.randomUUID().toString());
                ok.setZhuanye(zhuanye);
                ok.setColumn1(column1);
                ok.setColumn2(column2);
                ok.setColumn3(column3);
                ok.setColumn4(column4);
                ok.setColumn5(column5);
                ok.setColumn6(WTUtil.getCellStringValue(row.getCell(6)));
                ok.setColumn7(WTUtil.getCellStringValue(row.getCell(7)));
                ok.setColumn8(WTUtil.getCellStringValue(row.getCell(8)));
                ok.setColumn9(WTUtil.getCellStringValue(row.getCell(9)));
                CmPersistenceHelper.manager.insert(ok);

            }

            tx.commit();
            tx = null;
            return "";
        }catch(Exception e){
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }
}
