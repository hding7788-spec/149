package ext.casc.nc;

import ext.casc.nc.bean.GLZYKPartMapping;
import ext.casc.util.DBUtil;
import ext.sast.common.fc.CmPersistenceHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.dom4j.DocumentException;
import wt.method.RemoteMethodServer;
import wt.pom.Transaction;
import wt.util.WTException;

import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * @author Administrator
 *windchill ext.casc.nc.ImportZYKPartMapingUtility wcadmin wcadmin
 */
public class ImportZYKPartMapingUtility implements wt.method.RemoteAccess{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static void main(String[] args) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		String username = null;
		String passwd = null;
		String filePath = null;
		String deleteAll = "0";
		if (args.length >= 2) {
			username = args[0];
			passwd = args[1];
			filePath = args[2];
			if(args.length>=4){
				deleteAll = args[3];
			}
			if (username == null)
				username = "wcadmin";

			if (passwd == null)
				passwd = "Admin@149.941";
		}
		System.out.println("------user:"+username+"    password:"+passwd);
		rms.setUserName("wcadmin");
		rms.setPassword("Admin@149.941");
		try {
			process(filePath,deleteAll);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void process(String filePath,String deleteAll) throws WTException, IOException, PropertyVetoException, DocumentException{
		if (!RemoteMethodServer.ServerFlag) {
			String method = "process";
			Class[] types = {String.class ,String.class};
			Object[] vals = { filePath,deleteAll};

			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			try {
				 rms.invoke(method,
						ImportZYKPartMapingUtility.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Transaction tx = null;
			try {
				FileInputStream fileInputStream = new FileInputStream(filePath);
				Workbook workbook = WorkbookFactory.create(fileInputStream);
				Sheet sheet = workbook.getSheetAt(0);
				tx = new Transaction();
				tx.start();
				String synchTime = String.valueOf(System.currentTimeMillis());
				if("1".equals(deleteAll)){
					DBUtil.deleteAll("GLZYKPartMapping");
				}
				for (Row row : sheet) {
					if (row.getRowNum() == 0) {
						continue; // Skip the header row
					}
					GLZYKPartMapping glPartMapping = new GLZYKPartMapping();
					glPartMapping.setKeyId(UUID.randomUUID().toString());
					glPartMapping.setOldPartNumber(row.getCell(0).getStringCellValue());
					glPartMapping.setNewPartNumber(row.getCell(1).getStringCellValue());
					glPartMapping.setState("启用");
					glPartMapping.setSynchtime(synchTime);
					CmPersistenceHelper.manager.save(glPartMapping);
				}
				tx.commit();
				tx = null;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (null != tx) {
					tx.rollback();
				}
			}
		}
	}

}
