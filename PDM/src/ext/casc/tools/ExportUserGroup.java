package ext.casc.tools;

import com.glaway.mpm.util.PropertiesUtil;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.session.SessionServerHelper;

import java.io.*;
import java.util.*;

/**
 * @program: SAST-149-PDM
 * @description: 导出用户组
 * @author: cjh
 * @create: 2025-9-8 16:35:02
 */
public class ExportUserGroup implements RemoteAccess {

    public static void main(String[] args) {
        if(!RemoteMethodServer.ServerFlag) {
            try {
                RemoteMethodServer server = RemoteMethodServer.getDefault();
                server.setUserName("wcadmin");
                server.setPassword("Admin@149.941");
                String method = "";
                Class<?>[] types = null;
                Object[] vals = null;
                method = "exportExcel";
                types = new Class<?>[]{};
                vals = new Object[]{};
                if(types != null && vals != null) {
                    server.invoke(method, ExportUserGroup.class.getName(), null, types, vals);
                } else {
                    exportExcel();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 导出excel
     */
    public static void exportExcel() {
        System.out.println("----start-------");
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        InputStream inputStream = null;
        try {
            //创建Excel文件薄
            XSSFWorkbook workbook = new XSSFWorkbook();
            //创建工作表sheeet
            Sheet sheet = workbook.createSheet();
            //创建第一行
            Row row = sheet.createRow(0);
            String[] title = {"用户名", "全名", "所在组"};
            Cell cell = null;
            for(int i = 0; i < title.length; i++) {
                cell = row.createCell(i);
                cell.setCellValue(title[i]);
            }

            QuerySpec qs = new QuerySpec(WTUser.class);
            QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
            int i = 1;
            while(qr.hasMoreElements()) {
                WTUser user = (WTUser) qr.nextElement();
                Enumeration groups = user.parentGroups(false);
                String groupName = "";
                while(groups.hasMoreElements()) {
                    WTPrincipalReference reference = (WTPrincipalReference) groups.nextElement();
                    WTGroup group = (WTGroup) reference.getPrincipal();
                    if(!group.isInternal() && !(group instanceof WTOrganization)) {
                        groupName += group.getName() + ";";
                    }
                }
                row = sheet.createRow(i);
                cell = row.createCell(0);
                cell.setCellValue(user.getName());
                cell = row.createCell(1);
                cell.setCellValue(user.getFullName());
                cell = row.createCell(2);
                cell.setCellValue(groupName);
                i++;
            }

            //创建一个文件
            File file = new File(PropertiesUtil.getTempPath() + File.separator + "exportUserGroup_" + UUID.randomUUID() + ".xlsx");
            file.createNewFile();
            FileOutputStream stream = FileUtils.openOutputStream(file);
            workbook.write(stream);
            stream.close();

            System.out.println("----end-------");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enforce);
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}