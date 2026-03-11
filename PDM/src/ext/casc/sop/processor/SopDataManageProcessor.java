package ext.casc.sop.processor;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.util.*;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import wt.doc.WTDocument;
import ext.casc.util.IBAHelper;
import ext.casc.util.WCUtil;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import wt.log4j.LogR;
import wt.pom.Transaction;
import wt.util.WTException;

import java.io.*;
import java.util.*;

public class SopDataManageProcessor extends DefaultObjectFormProcessor {

    private static final Logger LOGGER = LogR.getLogger(SopDataManageProcessor.class.getName());

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
        try {
            importSOPData(temp_xlsxFile);
            form.setStatus(FormProcessingStatus.SUCCESS);
            FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "数据导入成功");
            form.addFeedbackMessage(message);
            form.setNextAction(FormResultAction.REFRESH_OPENER);
            return form;
        } catch(Exception e) {
            e.printStackTrace();
            return form;
        } finally {
            if(temp_file != null) temp_file.delete();
        }
    }

    private static void importSOPData(File file) {
        Workbook workbook = null;
        InputStream is = null;
        Transaction tx = null;
        try {
            tx = new Transaction();
            tx.start();

            LOGGER.info("导入SOP数据治理开始...");
            long time1 = System.currentTimeMillis();

            FileInputStream fileInputStream = new FileInputStream(file);
            workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheetAt = workbook.getSheetAt(0);

            for(Row row : sheetAt) {
                if(row.getRowNum() == 0) {
                    continue;
                }
                String technicsNumber = DealFileUtil.getValue(row.getCell(0)).toString().trim();
                String dept = DealFileUtil.getValue(row.getCell(1)).toString().trim();
                if(StrUtil.isNotEmpty(technicsNumber) && StrUtil.isNotEmpty(dept)) {
                    WTDocument document = WCUtil.getDocumentByIBANumber(technicsNumber);
                    if(document != null) {
                        IBAHelper.setIBAStringValue(document, "SopDepartment", dept);

                    }
                }
            }
            long time2 = System.currentTimeMillis();
            LOGGER.info("导入SOP数据治理数据耗时" + (time2 - time1) + " ms");

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
    }

}
