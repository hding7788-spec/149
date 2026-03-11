package ext.casc.gongshidinge.processor;

import cn.hutool.core.collection.CollUtil;
import com.glaway.mpm.util.*;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import wt.httpgw.URLFactory;
import wt.util.WTException;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ExportGongshiProcessor extends DefaultObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean cb, List<ObjectBean> listBean) throws WTException {
        File temp_file = (File) cb.getRequest().getAttribute("file");
        File temp_xlsxFile = (File) cb.getRequest().getAttribute("file2");
        FormResult form = new FormResult();
        try {
            File file = exportGongshi(temp_xlsxFile);
            form.setStatus(FormProcessingStatus.SUCCESS);
            URLFactory urlfactory = new URLFactory();
            cb.getRequest().getSession().putValue("file", file.getName());
            String info = "<TABLE width='100%' border=0 cellpadding=0 cellspacing=0><tr><font color='#FF0000'><b>工时差异导出完成</b></font></tr><br>请下载文件查看<tr><td><input type=button name='S1' value='下载导入结果文件' onclick=\"javascript:download();\"></td></tr></TABLE><script>document.body.style.bgColor='#F5F6F0';</script>";
            cb.getRequest().getSession().putValue("info", info);
            String url = urlfactory.getBaseHREF() + "netmarkets/jsp/ext/casc/gongshidinge/importMsg.jsp?";
            form.setURL(url);
            form.setNextAction(FormResultAction.FORWARD);
            return form;
        } catch(Exception e) {
            e.printStackTrace();
            return form;
        } finally {
            if(temp_file != null) temp_file.delete();
        }
    }

    private static File exportGongshi(File file) {
        XSSFWorkbook workbook = null;
        InputStream is = null;
        try {
            String tPath = PropertiesUtil.getWTHome() + File.separator + "gongshi" + File.separator + "工时定额差异导出.xlsx";
            FileUtils.copyFile(file, new File(tPath));
            File xlsFile = new File(tPath);
            if(xlsFile == null) {
                return null;
            }
            Set<String> numberSet = new HashSet<String>();
            List<String> list = new ArrayList<String>();
            FileInputStream fileInputStream = new FileInputStream(file);
            workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheetAt = workbook.getSheetAt(0);
            for(Row row : sheetAt) {
                String technicsNumber = DealFileUtil.getValue(row.getCell(0)).toString().trim();
                numberSet.add(technicsNumber);
            }

            DBConnUtil conn = null;
            ResultSet rs = null;
            try {
                conn = new DBConnUtil();
                StringBuilder sb = new StringBuilder();
                sb.append("select sv.VALUE2 " +
                        "from WTDOCUMENT doc " +
                        "         left join STRINGVALUE sv on doc.IDA2A2 = sv.IDA3A4 " +
                        "         left join STRINGDEFINITION sd on sd.IDA2A2 = sv.IDA3A6 " +
                        "WHERE sd.NAME = 'PPNUMBER' " +
                        "  AND doc.LATESTITERATIONINFO = 1 " +
                        "  and sv.VALUE2 is not null " +
                        "group by sv.VALUE2 ");
                rs = conn.executeQuery(sb.toString());
                while(rs.next()) {
                    String value = rs.getString("VALUE2");
                    if(!numberSet.contains(value)){
                        list.add(value);
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if(rs != null) {
                    try {
                        rs.close();
                    } catch(SQLException e) {
                        e.printStackTrace();
                    }
                }
                if(conn != null) {
                    try {
                        conn.close();
                    } catch(SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            is = new FileInputStream(xlsFile);
            XSSFWorkbook books = new XSSFWorkbook(is);
            SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(books,100);
            Sheet sheet = sxssfWorkbook.createSheet("文件中不包含工艺");
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("工艺文件编号");
//            for(int i = 0; i < 400000; i++) {
//                row = sheet.createRow(i + 1);
//                String number = "NUMBER" + i;
//                Cell rowCell = row.createCell(0, Cell.CELL_TYPE_STRING);
//                rowCell.setCellValue(new XSSFRichTextString(number));
//            }
            for(int i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 1);
                String number = list.get(i);
                Cell rowCell = row.createCell(0, Cell.CELL_TYPE_STRING);
                rowCell.setCellValue(new XSSFRichTextString(number));
            }
            FileOutputStream outputStream = new FileOutputStream(xlsFile);
            sxssfWorkbook.write(outputStream);
            outputStream.close();
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
        }
        return null;
    }
}