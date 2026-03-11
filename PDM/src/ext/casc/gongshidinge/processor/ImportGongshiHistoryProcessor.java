package ext.casc.gongshidinge.processor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.glaway.mpm.constants.ProcessPlanConstants;
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
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.httpgw.URLFactory;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

import java.io.*;
import java.util.*;

public class ImportGongshiHistoryProcessor extends DefaultObjectFormProcessor {

    private static final Logger LOGGER = LogR.getLogger(ImportGongshiHistoryProcessor.class.getName());

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
            File file = importGongshiHistory(temp_xlsxFile);

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

    private static File importGongshiHistory(File file) {
        Workbook workbook = null;
        InputStream is = null;
        Transaction tx = null;
        try {
            tx = new Transaction();
            tx.start();

            LOGGER.info("导入工时定额历史版本开始...");
            long time1 = System.currentTimeMillis();

            String tPath = PropertiesUtil.getWTHome() + File.separator + "gongshi" + File.separator + UUID.randomUUID() + "_导入工时定额历史版本记录.xlsx";
            FileUtils.copyFile(file, new File(tPath));
            File xlsFile = new File(tPath);
            if(xlsFile == null) {
                return null;
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheetAt = workbook.getSheetAt(0);

            List<String[]> isLatest = new ArrayList<String[]>();
            List<String[]> chongfu = new ArrayList<String[]>();
            List<String[]> hasDiff = new ArrayList<String[]>();
            List<String[]> noPlan = new ArrayList<String[]>();
            List<String[]> noDoc = new ArrayList<String[]>();
            Map<String, String[]> map = new HashMap<>();
            for(Row row : sheetAt) {
                if(row.getRowNum() == 0) {
                    continue;
                }
                String technicsNumber = DealFileUtil.getValue(row.getCell(0)).toString().trim();
                String technicsVersion = DealFileUtil.getValue(row.getCell(1)).toString().trim();
                String[] strs = new String[]{technicsNumber, technicsVersion};
                if(map.containsKey(technicsNumber)) {
                    chongfu.add(strs);
                } else {
                    map.put(technicsNumber, strs);
                }
            }

            long time2 = System.currentTimeMillis();
            LOGGER.info("解析导入数据耗时" + (time2 - time1) + " ms");

            MPMOperationMaster master = null;
            MPMOperation operation = null;
            Iterator<Map.Entry<String, String[]>> it = map.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry<String, String[]> next = it.next();
                String technicsNumber = next.getKey();
                String[] strs = next.getValue();
                String technicsVersion = strs[1];
                WTDocument oldDocument = getWTDocumentByNumberAndVersion(technicsNumber, technicsVersion);
                WTDocument latestDocument = WTDocumentUtil.getLatestDocumentByNumber(technicsNumber);
                if(oldDocument != null && latestDocument != null) {
                    if(oldDocument.getPersistInfo().getObjectIdentifier().getId() == latestDocument.getPersistInfo().getObjectIdentifier().getId()) {
                        isLatest.add(strs);
                        continue;
                    }
                    MPMProcessPlan oldPlan = MPMProcessPlanUtil.getProcessPlanByWTDocument(oldDocument);
                    MPMProcessPlan latestPlan = MPMProcessPlanUtil.getProcessPlanByWTDocument(latestDocument);
                    if(oldPlan != null && latestPlan != null) {
                        Map<String,Map<String,String>> latestStepMap = new HashMap<>();
                        List<MPMOperationUsageLink> latestLinks = ViewWIHelper.getMPMOperationUsageLinkByMpmPr(latestPlan);
                        for(MPMOperationUsageLink link : latestLinks) {
                            String label = link.getOperationLabel();
                            label = Util.formateInteger(label);
                            master = (MPMOperationMaster) link.getRoleBObject();
                            operation = ViewWIHelper.getMpmOperation(master.getNumber());
                            Map<String, String> gongshiMap = new HashMap<>();
                            IBAUtility iba = new IBAUtility(operation);
                            gongshiMap.put("ZJGS", iba.getIBAValue("ZJGS"));
                            gongshiMap.put("DJGS", iba.getIBAValue("DJGS"));
                            gongshiMap.put("DanJianSheBeiGS", iba.getIBAValue("DanJianSheBeiGS"));
                            latestStepMap.put(label + "@@@" + operation.getName(), gongshiMap);
                        }
                        List<MPMOperationUsageLink> oldLinks = ViewWIHelper.getMPMOperationUsageLinkByMpmPr(oldPlan);
                        for(MPMOperationUsageLink link : oldLinks) {
                            String label = link.getOperationLabel();
                            label = Util.formateInteger(label);
                            master = (MPMOperationMaster) link.getRoleBObject();
                            operation = ViewWIHelper.getMpmOperation(master.getNumber());
                            if(latestStepMap.containsKey(label + "@@@" + operation.getName())) {
                                Map<String, String> gongshiMap = latestStepMap.get(label + "@@@" + operation.getName());
                                IBAUtility iba = new IBAUtility(operation);
                                iba.setIBAValue("ZJGS", gongshiMap.get("ZJGS"));
                                iba.setIBAValue("DJGS", gongshiMap.get("DJGS"));
                                iba.setIBAValue("DanJianSheBeiGS", gongshiMap.get("DanJianSheBeiGS"));
                                operation = (MPMOperation) iba.updateAttributeContainer(operation);
                                iba.updateIBAHolder(operation);
                            } else {
                                hasDiff.add(strs);
                                break;
                            }
                        }
                    } else {
                        noPlan.add(strs);
                    }
                } else {
                    noDoc.add(strs);
                }
            }

            long time3 = System.currentTimeMillis();
            LOGGER.info("导入工时定额历史版本数据耗时" + (time3 - time2) + " ms");

            is = new FileInputStream(xlsFile);
            XSSFWorkbook books = new XSSFWorkbook(is);
            SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(books, 100);
            Row row = null;
            Sheet sheet1 = sxssfWorkbook.createSheet("已是最新");
            Sheet sheet3 = sxssfWorkbook.createSheet("文件重复");
            Sheet sheet2 = sxssfWorkbook.createSheet("有差异");
            Sheet sheet4 = sxssfWorkbook.createSheet("未查到实例化的工艺");
            Sheet sheet5 = sxssfWorkbook.createSheet("未查到工艺文件");
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
                String sheetName = sheet.getSheetName();
                if("已是最新".equals(sheetName)) {
                    for(int i = 0; i < isLatest.size(); i++) {
                        row = sheet.createRow(i + 1);
                        String[] strs = isLatest.get(i);
                        writeCellValue(row, 0, strs[0]);
                        writeCellValue(row, 1, strs[1]);
                    }
                } else if("文件重复".equals(sheetName)) {
                    for(int i = 0; i < chongfu.size(); i++) {
                        row = sheet.createRow(i + 1);
                        String[] strs = chongfu.get(i);
                        writeCellValue(row, 0, strs[0]);
                        writeCellValue(row, 1, strs[1]);
                    }
                } else if("有差异".equals(sheetName)) {
                    for(int i = 0; i < hasDiff.size(); i++) {
                        row = sheet.createRow(i + 1);
                        String[] strs = hasDiff.get(i);
                        writeCellValue(row, 0, strs[0]);
                        writeCellValue(row, 1, strs[1]);
                    }
                } else if("未查到实例化的工艺".equals(sheetName)) {
                    for(int i = 0; i < noPlan.size(); i++) {
                        row = sheet.createRow(i + 1);
                        String[] strs = noPlan.get(i);
                        writeCellValue(row, 0, strs[0]);
                        writeCellValue(row, 1, strs[1]);
                    }
                } else if("未查到工艺文件".equals(sheetName)) {
                    for(int i = 0; i < noDoc.size(); i++) {
                        row = sheet.createRow(i + 1);
                        String[] strs = noDoc.get(i);
                        writeCellValue(row, 0, strs[0]);
                        writeCellValue(row, 1, strs[1]);
                    }
                }
            }
            FileOutputStream outputStream = new FileOutputStream(xlsFile);
            sxssfWorkbook.write(outputStream);
            outputStream.close();

            long time4 = System.currentTimeMillis();
            LOGGER.info("导出问题数据耗时" + (time4 - time3) + " ms");

            if(CollUtil.isNotEmpty(isLatest) || CollUtil.isNotEmpty(chongfu) || CollUtil.isNotEmpty(hasDiff)
                    || CollUtil.isNotEmpty(noPlan) || CollUtil.isNotEmpty(noDoc)) {
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

    private static WTDocument getWTDocumentByNumberAndVersion(String number,String version)
            throws WTException {
        int index[] = { 0 };
        WTDocument document = null;
        QuerySpec qs = new QuerySpec(WTDocument.class);
        qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number), index);
        //根据版本查询
        if(StrUtil.isNotEmpty(version)) {
            qs.appendAnd();
            if(version.contains(".")){
                version = version.substring(0, version.indexOf("."));
            }
            qs.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", version),index);
        }
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        qr = new LatestConfigSpec().process(qr);
        if (qr.hasMoreElements()) {
            document = (WTDocument) qr.nextElement();
        }
        return document;
    }

}
