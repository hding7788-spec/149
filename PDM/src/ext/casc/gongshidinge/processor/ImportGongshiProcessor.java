package ext.casc.gongshidinge.processor;

import cn.hutool.core.collection.CollUtil;
import com.glaway.mpm.util.*;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import ext.casc.importdata.productRB;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;
import ext.ptc.ViewWIHelper;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import wt.doc.WTDocument;
import wt.httpgw.URLFactory;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.util.WTException;

import java.io.*;
import java.util.*;

public class ImportGongshiProcessor extends DefaultObjectFormProcessor {

    private static final Logger LOGGER = LogR.getLogger(ImportGongshiProcessor.class.getName());

    private static String MYRESOURCE = "ext.casc.importdata.productRB";

    @Override
    public FormResult doOperation(NmCommandBean cb, List<ObjectBean> listBean) throws WTException {
        File temp_file = (File) cb.getRequest().getAttribute("file");
        File temp_xlsxFile = (File) cb.getRequest().getAttribute("file2");
        FormResult form = new FormResult();
        String fileName = cb.getRequest().getParameter("file2");
        if(!fileName.endsWith("xlsx")) {
            form.setStatus(FormProcessingStatus.FAILURE);
            FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "只能选择xlsx格式文件导入！");
            form.addFeedbackMessage(message);
            return form;
        }
        ResourceBundle rb = ResourceBundle.getBundle(MYRESOURCE);
        try {
            File file = importGongshi(temp_xlsxFile);

            if(file != null) {
                form.setStatus(FormProcessingStatus.SUCCESS);
                URLFactory urlfactory = new URLFactory();
                cb.getRequest().getSession().putValue("file", file.getName());
                String info = "<TABLE width='100%' border=0 cellpadding=0 cellspacing=0><tr><font color='#FF0000'><b>" + rb.getString(productRB.IMPORTERRORMESSAGE_TITLES2) + "</b></font></tr><br>请下载文件查看<tr><td><input type=button name='S1' value='下载导入结果文件' onclick=\"javascript:download();\"></td></tr></TABLE><script>document.body.style.bgColor='#F5F6F0';</script>";
                cb.getRequest().getSession().putValue("info", info);
                String url = urlfactory.getBaseHREF() + "netmarkets/jsp/ext/casc/gongshidinge/importMsg.jsp?";
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
        } catch(Exception e) {
            e.printStackTrace();
            return form;
        } finally {
            if(temp_file != null) temp_file.delete();
        }
    }

    private static File importGongshi(File file) {
        Workbook workbook = null;
        InputStream is = null;
        Transaction tx = null;
        try {
            tx = new Transaction();
            tx.start();

            LOGGER.info("导入工时定额开始...");
            long time1 = System.currentTimeMillis();

            String tPath = PropertiesUtil.getWTHome() + File.separator + "gongshi" + File.separator + UUID.randomUUID() + "_导入工时定额记录.xlsx";
            FileUtils.copyFile(file, new File(tPath));
            File xlsFile = new File(tPath);
            if(xlsFile == null) {
                return null;
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheetAt = workbook.getSheetAt(0);

            List<Map<String, String>> excelHas = new ArrayList<Map<String, String>>();
            List<Map<String, String>> pdmHas = new ArrayList<Map<String, String>>();
            List<Map<String, String>> chongfu = new ArrayList<Map<String, String>>();
            List<Map<String, String>> noPlan = new ArrayList<Map<String, String>>();
            List<Map<String, String>> noLatest = new ArrayList<Map<String, String>>();
            Map<String, Map<String, Map<String, String>>> map = new HashMap<String, Map<String, Map<String, String>>>();

            for(Row row : sheetAt) {
                if(row.getRowNum() == 0) {
                    continue;
                }
                //编号 + @@@ + 版本
                String technicsNumber = DealFileUtil.getValue(row.getCell(0)).toString().trim() + "@@@" + DealFileUtil.getValue(row.getCell(1)).toString().trim();
                String stepNumber = DealFileUtil.getValue(row.getCell(2)).toString().trim();
                String stepName = DealFileUtil.getValue(row.getCell(3)).toString().trim();
                String zhunjie = DealFileUtil.getValue(row.getCell(4)).toString().trim();
                String danjian = DealFileUtil.getValue(row.getCell(5)).toString().trim();
                String shebei = DealFileUtil.getValue(row.getCell(6)).toString().trim();
                String zzdw = DealFileUtil.getValue(row.getCell(7)).toString().trim();
                Map<String, String> rowMap = new HashMap<String, String>();
                rowMap.put("technicsNumber", DealFileUtil.getValue(row.getCell(0)).toString().trim());
                rowMap.put("technicsVersion", DealFileUtil.getValue(row.getCell(1)).toString().trim());
                rowMap.put("stepNumber", stepNumber);
                rowMap.put("stepName", stepName);
                rowMap.put("zhunjie", zhunjie);
                rowMap.put("danjian", danjian);
                rowMap.put("shebei", shebei);
                rowMap.put("zzdw", zzdw);
                if(map.containsKey(technicsNumber)) {
                    Map<String, Map<String, String>> stepMap = map.get(technicsNumber);
                    if(stepMap.containsKey(stepNumber)) {
                        chongfu.add(rowMap);
                    } else {
                        stepMap.put(stepNumber, rowMap);
                    }
                } else {
                    Map<String, Map<String, String>> stepMap = new HashMap<String, Map<String, String>>();
                    stepMap.put(stepNumber, rowMap);
                    map.put(technicsNumber, stepMap);
                }
            }

            long time2 = System.currentTimeMillis();
            LOGGER.info("解析导入数据耗时" + (time2 - time1) + " ms");

            MPMOperationMaster master = null;
            MPMOperation operation = null;
            Iterator<Map.Entry<String, Map<String, Map<String, String>>>> it = map.entrySet().iterator();
            int recordCount = 0;
            while(it.hasNext()) {
                recordCount++;
                if (recordCount % 10000 == 0) {
                    LOGGER.info("工时定额导入已处理 " + recordCount + " 条记录");
                }
                Map.Entry<String, Map<String, Map<String, String>>> next = it.next();
                String technicsNumber = next.getKey();
                String[] strings = technicsNumber.split("@@@");
                technicsNumber = strings[0];
                String technicsVersion = "";
                if(strings.length > 1) {
                    technicsVersion = strings[1];
                }
                Map<String, Map<String, String>> stepMap = next.getValue();
                WTDocument document = WCUtil.getDocumentByIBANumberAndVersion(technicsNumber,technicsVersion);
                if(document != null) {
                    WTPart part = WTDocumentUtil.getLatestDescribesWTPartsByDocument(document);
                    if(part != null) {
                        WTPart latestPart = WTPartUtil.getLatestPartByNumberAndView(part.getNumber(), "Manufacturing");
                        if(part.getPersistInfo().getObjectIdentifier().getId() != latestPart.getPersistInfo().getObjectIdentifier().getId()) {
                            noLatest.addAll(stepMap.values());
                            continue;
                        }
                    }
                    MPMProcessPlan plan = MPMProcessPlanUtil.getProcessPlanByWTDocument(document);
                    if(plan != null) {
                        List<MPMOperationUsageLink> links = ViewWIHelper.getMPMOperationUsageLinkByMpmPr(plan);
                        for(MPMOperationUsageLink link : links) {
                            String label = link.getOperationLabel();
                            label = Util.formateInteger(label);
                            master = (MPMOperationMaster) link.getRoleBObject();
                            operation = ViewWIHelper.getMpmOperation(master.getNumber());
                            if(operation != null && stepMap.containsKey(label) && stepMap.get(label).get("stepName").equals(operation.getName())) {
                                IBAUtility iba = new IBAUtility(operation);
                                Map<String, String> steps = stepMap.get(label);
                                iba.setIBAValue("ZJGS", steps.get("zhunjie"));
                                iba.setIBAValue("DJGS", steps.get("danjian"));
                                iba.setIBAValue("DanJianSheBeiGS", steps.get("shebei"));
                                operation = (MPMOperation) iba.updateAttributeContainer(operation);
                                iba.updateIBAHolder(operation);
                                stepMap.remove(label);
                            } else {
                                Map<String, String> stringMap = new HashMap<String, String>();
                                stringMap.put("technicsNumber", technicsNumber);
                                stringMap.put("technicsVersion", technicsVersion);
                                stringMap.put("stepNumber", label);
                                stringMap.put("stepName", operation.getName());
                                stringMap.put("zhunjie", "");
                                stringMap.put("danjian", "");
                                stringMap.put("shebei", "");
                                stringMap.put("zzdw", "");
                                IBAUtility iba = new IBAUtility(operation);
                                iba.setIBAValue("ZJGS", "0");
                                iba.setIBAValue("DJGS", "0");
                                iba.setIBAValue("DanJianSheBeiGS", "0");
                                operation = (MPMOperation) iba.updateAttributeContainer(operation);
                                iba.updateIBAHolder(operation);
                                pdmHas.add(stringMap);
                            }
                        }
                    } else {
                        noPlan.addAll(stepMap.values());
                    }
                }
                if(stepMap.size() > 0) {
                    excelHas.addAll(stepMap.values());
                }
            }

            long time3 = System.currentTimeMillis();
            LOGGER.info("导入工时数据耗时" + (time3 - time2) + " ms");

            is = new FileInputStream(xlsFile);
            XSSFWorkbook books = new XSSFWorkbook(is);
            SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(books, 100);
            Row row = null;
            Sheet sheet1 = sxssfWorkbook.createSheet("系统存在文件不存在");
            Sheet sheet2 = sxssfWorkbook.createSheet("文件存在系统不存在");
            Sheet sheet3 = sxssfWorkbook.createSheet("文件重复");
            Sheet sheet4 = sxssfWorkbook.createSheet("没有实例化的工艺");
            Sheet sheet5 = sxssfWorkbook.createSheet("没有关联最新部件");
            List<Sheet> sheets = new ArrayList<Sheet>();
            sheets.add(sheet1);
            sheets.add(sheet2);
            sheets.add(sheet3);
            sheets.add(sheet4);
            sheets.add(sheet5);
            for(Sheet sheet : sheets) {
                row = sheet.createRow(0);
                Cell cell = row.createCell(0);
                cell.setCellValue("工艺文件编号");
                cell = row.createCell(1);
                cell.setCellValue("工艺文件版本");
                cell = row.createCell(2);
                cell.setCellValue("工序号");
                cell = row.createCell(3);
                cell.setCellValue("工序名称");
                cell = row.createCell(4);
                cell.setCellValue("准结");
                cell = row.createCell(5);
                cell.setCellValue("单件人工");
                cell = row.createCell(6);
                cell.setCellValue("单件设备");
                cell = row.createCell(7);
                cell.setCellValue("制造单位");
                String sheetName = sheet.getSheetName();
                if("系统存在文件不存在".equals(sheetName)) {
                    for(int i = 0; i < pdmHas.size(); i++) {
                        row = sheet.createRow(i + 1);
                        Map<String, String> step = pdmHas.get(i);
                        writeCellValue(row, 0, step.get("technicsNumber"));
                        writeCellValue(row, 1, step.get("technicsVersion"));
                        writeCellValue(row, 2, step.get("stepNumber"));
                        writeCellValue(row, 3, step.get("stepName"));
                        writeCellValue(row, 4, step.get("zhunjie"));
                        writeCellValue(row, 5, step.get("danjian"));
                        writeCellValue(row, 6, step.get("shebei"));
                        writeCellValue(row, 7, step.get("zzdw"));
                    }
                } else if("文件存在系统不存在".equals(sheetName)) {
                    for(int i = 0; i < excelHas.size(); i++) {
                        row = sheet.createRow(i + 1);
                        Map<String, String> step = excelHas.get(i);
                        writeCellValue(row, 0, step.get("technicsNumber"));
                        writeCellValue(row, 1, step.get("technicsVersion"));
                        writeCellValue(row, 2, step.get("stepNumber"));
                        writeCellValue(row, 3, step.get("stepName"));
                        writeCellValue(row, 4, step.get("zhunjie"));
                        writeCellValue(row, 5, step.get("danjian"));
                        writeCellValue(row, 6, step.get("shebei"));
                        writeCellValue(row, 7, step.get("zzdw"));
                    }
                } else if("文件重复".equals(sheetName)) {
                    for(int i = 0; i < chongfu.size(); i++) {
                        row = sheet.createRow(i + 1);
                        Map<String, String> step = chongfu.get(i);
                        writeCellValue(row, 0, step.get("technicsNumber"));
                        writeCellValue(row, 1, step.get("technicsVersion"));
                        writeCellValue(row, 2, step.get("stepNumber"));
                        writeCellValue(row, 3, step.get("stepName"));
                        writeCellValue(row, 4, step.get("zhunjie"));
                        writeCellValue(row, 5, step.get("danjian"));
                        writeCellValue(row, 6, step.get("shebei"));
                        writeCellValue(row, 7, step.get("zzdw"));
                    }
                } else if("没有实例化的工艺".equals(sheetName)) {
                    for(int i = 0; i < noPlan.size(); i++) {
                        row = sheet.createRow(i + 1);
                        Map<String, String> step = noPlan.get(i);
                        writeCellValue(row, 0, step.get("technicsNumber"));
                        writeCellValue(row, 1, step.get("technicsVersion"));
                        writeCellValue(row, 2, step.get("stepNumber"));
                        writeCellValue(row, 3, step.get("stepName"));
                        writeCellValue(row, 4, step.get("zhunjie"));
                        writeCellValue(row, 5, step.get("danjian"));
                        writeCellValue(row, 6, step.get("shebei"));
                        writeCellValue(row, 7, step.get("zzdw"));
                    }
                } else if("没有关联最新部件".equals(sheetName)) {
                    for(int i = 0; i < noLatest.size(); i++) {
                        row = sheet.createRow(i + 1);
                        Map<String, String> step = noLatest.get(i);
                        writeCellValue(row, 0, step.get("technicsNumber"));
                        writeCellValue(row, 1, step.get("technicsVersion"));
                        writeCellValue(row, 2, step.get("stepNumber"));
                        writeCellValue(row, 3, step.get("stepName"));
                        writeCellValue(row, 4, step.get("zhunjie"));
                        writeCellValue(row, 5, step.get("danjian"));
                        writeCellValue(row, 6, step.get("shebei"));
                        writeCellValue(row, 7, step.get("zzdw"));
                    }
                }
            }
            FileOutputStream outputStream = new FileOutputStream(xlsFile);
            sxssfWorkbook.write(outputStream);
            outputStream.close();

            long time4 = System.currentTimeMillis();
            LOGGER.info("导出问题数据耗时" + (time4 - time3) + " ms");

            if(CollUtil.isNotEmpty(pdmHas) || CollUtil.isNotEmpty(excelHas) || CollUtil.isNotEmpty(chongfu)) {
                tx.commit();
                tx = null;
                return xlsFile;
            }

            tx.commit();
            tx = null;
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
            if(tx != null) {
                tx.rollback();
            }
        }
        return null;
    }

    public static void writeCellValue(Row row, int col, String value) {
        Cell cell = row.createCell(col, SXSSFCell.CELL_TYPE_STRING);
        cell.setCellValue(new XSSFRichTextString(value));
    }

}
