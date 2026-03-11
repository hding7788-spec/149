package ext.casc.test;

import com.glaway.mpm.sjzyk.SjzykSchedule;
import com.glaway.mpm.util.DealFileUtil;
import com.glaway.mpm.util.WTPartUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.dom4j.DocumentException;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.util.WTException;

import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

/**
 * 方法功能:根据文件中部件编号同步工艺物资条目
 *
 * @author cjh
 * windchill ext.casc.nc.SyncWzUtility wcadmin wcadmin
 * @date 2024/3/21
 */
public class SyncWzUtility implements wt.method.RemoteAccess {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static void main(String[] args) {
        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        String username = null;
        String passwd = null;
        String filePath = null;

        if(args.length >= 2) {
            username = args[0];
            passwd = args[1];
            filePath = args[2];

            if(username == null)
                username = "wcadmin";

            if(passwd == null)
                passwd = "Admin@149.941";
        }
        System.out.println("------user:" + username + "    password:" + passwd);
        rms.setUserName("wcadmin");
        rms.setPassword("Admin@149.941");
        try {
            process(filePath);
        } catch(WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch(IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch(PropertyVetoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch(DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static void process(String filePath) throws WTException, IOException, PropertyVetoException, DocumentException {
        if(!RemoteMethodServer.ServerFlag) {
            String method = "process";
            Class[] types = {String.class};
            Object[] vals = {filePath};

            RemoteMethodServer rms = RemoteMethodServer.getDefault();
            try {
                rms.invoke(method, SyncWzUtility.class.getName(), null, types, vals);
            } catch(RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch(InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Transaction tx = null;
            try {
                FileInputStream fileInputStream = new FileInputStream(filePath);
                Workbook workbook = WorkbookFactory.create(fileInputStream);
                Sheet sheet = workbook.getSheetAt(0);
                tx = new Transaction();
                tx.start();

                for(Row row : sheet) {
                    String partNumber = DealFileUtil.getValue(row.getCell(0)).toString();
                    WTPart part = WTPartUtil.getLatestPartByNumberAndView(partNumber, "Design");
                    if(part != null) {
                        SjzykSchedule.updateTechnicMaterialInfo(part);
                    }
                }
                tx.commit();
                tx = null;
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if(null != tx) {
                    tx.rollback();
                }
            }
        }
    }


}
