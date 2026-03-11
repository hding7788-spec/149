package ext.casc.gongshidinge.processor;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.change.qchange.helper.ChangeProcessPlanStructure;
import com.glaway.mpm.processplan.ProcessPlanStructure;
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
import ext.casc.importdata.productRB;
import ext.casc.sop.util.SopWorkflowUtil;
import ext.casc.util.WCUtil;
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

public class GenerateProcessPlanProcessor extends DefaultObjectFormProcessor {

    private static final Logger LOGGER = LogR.getLogger(GenerateProcessPlanProcessor.class.getName());

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
            File file = generateProcessPlan(temp_xlsxFile);

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

    private static File generateProcessPlan(File file) {
        Workbook workbook = null;
        InputStream is = null;
        Transaction tx = null;
        try {
            tx = new Transaction();
            tx.start();

            LOGGER.info("生成工艺实例化开始...");
            long time1 = System.currentTimeMillis();

            String tPath = PropertiesUtil.getWTHome() + File.separator + "gongshi" + File.separator + UUID.randomUUID() + "_生成工艺实例化记录.xlsx";
            FileUtils.copyFile(file, new File(tPath));
            File xlsFile = new File(tPath);
            if(xlsFile == null) {
                return null;
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheetAt = workbook.getSheetAt(0);

            List<String> created = new ArrayList<String>();
            List<String> hasPlan = new ArrayList<String>();
            List<String> noHas = new ArrayList<String>();
            List<String> noPart = new ArrayList<String>();
            List<String> noApproved = new ArrayList<String>();

            for(Row row : sheetAt) {
                if(row.getRowNum() == 0) {
                    continue;
                }
                String technicsNumber = DealFileUtil.getValue(row.getCell(0)).toString().trim();
                String version = DealFileUtil.getValue(row.getCell(1)).toString().trim();
                if(StrUtil.isEmpty(technicsNumber)) {
                    break;
                }
                WTDocument document = WCUtil.getDocumentByIBANumberAndVersion(technicsNumber, version);
                if(document != null) {
                    String state = document.getState().getState().getDisplay(Locale.CHINA);
                    if("已批准".equals(state)) {
                        WTPart part = WTDocumentUtil.getLatestDescribesWTPartsByDocument(document);
                        if(part != null) {
                            MPMProcessPlan plan = MPMProcessPlanUtil.getProcessPlanByWTDocument(document);
                            if(plan == null) {
                                boolean isSop = SopWorkflowUtil.isSop(document);
                                String docVersion = document.getVersionIdentifier().getValue();
                                if(!docVersion.startsWith("space")) {
                                    if(isSop) {
                                        SopWorkflowUtil.structureSopChangeOrder(part.getPersistInfo().getObjectIdentifier().getId() + "", document.getPersistInfo().getObjectIdentifier().getId() + "");
                                    } else {
                                        ChangeProcessPlanStructure pps = new ChangeProcessPlanStructure(part, document);
                                        pps.structureProcessPlan();
                                    }
                                } else {
                                    if(isSop) {
                                        SopWorkflowUtil.structureSopProcessPlan(document, part.getPersistInfo().getObjectIdentifier().getId() + "");
                                    } else {
                                        ProcessPlanStructure structure = new ProcessPlanStructure(part, document, "normal");
                                        structure.structureProcessPlan();
                                    }
                                }
                                created.add(technicsNumber);
                            } else {
                                hasPlan.add(technicsNumber);
                            }
                        } else {
                            noPart.add(technicsNumber);
                        }
                    } else {
                        noApproved.add(technicsNumber);
                    }
                } else {
                    noHas.add(technicsNumber);
                }
            }

            long time2 = System.currentTimeMillis();
            LOGGER.info("导入数据耗时" + (time2 - time1) + " ms");

            is = new FileInputStream(xlsFile);
            XSSFWorkbook books = new XSSFWorkbook(is);
            SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(books, 100);
            Row row = null;
            Sheet sheet1 = sxssfWorkbook.createSheet("已导入");
            Sheet sheet2 = sxssfWorkbook.createSheet("系统已存在");
            Sheet sheet3 = sxssfWorkbook.createSheet("未查询到");
            Sheet sheet4 = sxssfWorkbook.createSheet("未关联部件");
            Sheet sheet5 = sxssfWorkbook.createSheet("未批准");
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
                String sheetName = sheet.getSheetName();
                if("已导入".equals(sheetName)) {
                    for(int i = 0; i < created.size(); i++) {
                        row = sheet.createRow(i + 1);
                        writeCellValue(row, 0, created.get(i));
                    }
                } else if("系统已存在".equals(sheetName)) {
                    for(int i = 0; i < hasPlan.size(); i++) {
                        row = sheet.createRow(i + 1);
                        writeCellValue(row, 0, hasPlan.get(i));
                    }
                } else if("未查询到".equals(sheetName)) {
                    for(int i = 0; i < noHas.size(); i++) {
                        row = sheet.createRow(i + 1);
                        writeCellValue(row, 0, noHas.get(i));
                    }
                } else if("未关联部件".equals(sheetName)) {
                    for(int i = 0; i < noPart.size(); i++) {
                        row = sheet.createRow(i + 1);
                        writeCellValue(row, 0, noPart.get(i));
                    }
                } else if("未批准".equals(sheetName)) {
                    for(int i = 0; i < noApproved.size(); i++) {
                        row = sheet.createRow(i + 1);
                        writeCellValue(row, 0, noApproved.get(i));
                    }
                }
            }
            FileOutputStream outputStream = new FileOutputStream(xlsFile);
            sxssfWorkbook.write(outputStream);
            outputStream.close();

            long time3 = System.currentTimeMillis();
            LOGGER.info("导出问题数据耗时" + (time3 - time2) + " ms");

            tx.commit();
            tx = null;
            return xlsFile;
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
