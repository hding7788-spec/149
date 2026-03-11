package ext.casc.gongshidinge.processor;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.util.DealFileUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.util.IBAHelper;
import ext.casc.util.WCUtil;
import org.apache.poi.ss.usermodel.*;
import wt.doc.WTDocument;
import wt.pom.Transaction;
import wt.util.WTException;

import java.io.*;
import java.util.*;

public class ImportGongshiSignProcessor extends DefaultObjectFormProcessor {

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
            importGongshiSign(temp_xlsxFile);
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

    private static void importGongshiSign(File file) {
        Workbook workbook = null;
        InputStream is = null;
        Transaction tx = null;
        try {
            tx = new Transaction();
            tx.start();

            FileInputStream fileInputStream = new FileInputStream(file);
            workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheetAt = workbook.getSheetAt(0);

            Set<String> numberSet = new HashSet<String>();

            for(Row row : sheetAt) {
                if(row.getRowNum() == 0) {
                    continue;
                }
                String technicsNumber = DealFileUtil.getValue(row.getCell(0)).toString().trim();
                String technicsVersion = DealFileUtil.getValue(row.getCell(1)).toString().trim();
                if(StrUtil.isEmpty(technicsNumber)) {
                    break;
                }
                if(numberSet.contains(technicsNumber)){
                    continue;
                }else {
                    numberSet.add(technicsNumber);
                }
                WTDocument document = WCUtil.getDocumentByIBANumberAndVersion(technicsNumber, technicsVersion);
                if(document != null) {
                    try {
                        IBAHelper.setIBAStringValue(document, "GongShiDingEState", "已导入");
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
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
    }

}
