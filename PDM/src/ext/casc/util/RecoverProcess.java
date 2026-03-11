package ext.casc.util;

import com.glaway.mpm.util.ApacheZipUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import ext.casc.webservice.command.ExchangeProcessDocCommandZS;
import org.dom4j.DocumentException;
import wt.content.*;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pom.Transaction;
import wt.util.WTException;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.Set;
import java.util.Map.Entry;

public class RecoverProcess implements RemoteAccess {

    private static final long serialVersionUID = 1L;
    public static void main(String[] args) {
        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        String username = null;
        String passwd = null;
        String docNumber = null;


        if (args.length >= 2) {
            username = args[0];
            passwd = args[1];
            docNumber = args[2];

            if (username == null)
                username = "wcadmin";

            if (passwd == null)
                passwd = "Admin@149.941";
        }
        System.out.println("------user:"+username+"    password:"+passwd);
        rms.setUserName(username);
        rms.setPassword(passwd);
        try {
            process(docNumber);
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
    public static void process(String number) throws WTException, IOException, PropertyVetoException, DocumentException{
        if (!RemoteMethodServer.ServerFlag) {
            String method = "process";
            Class[] types = {String.class };
            Object[] vals = { number};

            RemoteMethodServer rms = RemoteMethodServer.getDefault();
            try {
                rms.invoke(method,
                        RecoverProcess.class.getName(), null, types, vals);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            Transaction tx = null;
            DBConn conn = null;
            FileInputStream fileInputStream =null;
            FileInputStream fileInputStream2 =null;
            FileInputStream fileInputStream3 =null;

            try {
                conn = new DBConn();
                tx = new Transaction();
                tx.start();
                StringBuilder sql = new StringBuilder("select * from GLBACKWTDOCUMENT ");
                if("ALL".equals(number)){
                }else{
                    sql.append("where DOCNUMBER='").append(number).append("'");
                }
                ReferenceFactory rf = new ReferenceFactory();

                ResultSet rs = conn.executeQuery(sql.toString());
                while (rs.next()) {
                    String docid = rs.getString("DOCID");
                    String docNumber = rs.getString("DOCNUMBER");
                    docNumber  = docNumber.replace(ExchangeProcessDocCommandZS.DOC_END, "");
                    String state = rs.getString("STATE");
                    if("UPDATE".equals(state)){
                    	try{
	                        WTDocument document = (WTDocument)(rf.getReference("OR:wt.doc.WTDocument:"+docid).getObject());
	                        String bkfileZip = ExchangeProcessDocCommandZS.BACKUP_PATH+docNumber+File.separatorChar+docid +
	                                File.separatorChar+docNumber+".zip";
	                        fileInputStream = new FileInputStream(bkfileZip);
	                        ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
	                        data = ContentServerHelper.service.updateContent((ContentHolder) document, data, fileInputStream);

	                        QueryResult qr = ContentHelper.service.getContentsByRole(document, ContentRoleType.SECONDARY);
	                        while (qr.hasMoreElements()) {
	                            ApplicationData applicationdata = (ApplicationData) qr.nextElement();
	                            String name = applicationdata.getFileName();
	                            if (name.startsWith("PDFPreview")) {
	                                String bkfilePdf = ExchangeProcessDocCommandZS.BACKUP_PATH+docNumber+File.separatorChar+docid +
	                                        File.separatorChar+"pdf"+File.separatorChar+"PDFPreview.pdf";
	                                File file = new  File(bkfilePdf);
	                                if(file.exists()){
	                                    fileInputStream2 = new FileInputStream(file);
	                                    ContentServerHelper.service.updateContent((ContentHolder) document, applicationdata, fileInputStream2);
	                                }
	                            }else  if (name.startsWith("Print_")) {
	                                String bkfilePdf = ExchangeProcessDocCommandZS.BACKUP_PATH+docNumber+File.separatorChar+docid +
	                                        File.separatorChar+"pdf"+File.separatorChar+"Print.pdf";
	                                File file = new  File(bkfilePdf);
	                                if(file.exists()){
	                                    fileInputStream3 = new FileInputStream(file);
	                                    ContentServerHelper.service.updateContent((ContentHolder) document, applicationdata, fileInputStream3);
	                                }                            }
	                        }

	                        updateState(docid,"RECOVERED");
                    	}catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else if("ADD".equals(state)){
                    	try{
	                        WTDocument document = (WTDocument)(rf.getReference("OR:wt.doc.WTDocument:"+docid).getObject());
	                        PurgeDataProcessor.deleteLinkD2P(document);
	                        PersistenceHelper.manager.delete(document);
	                        updateState(docid,"DELETED");
                    	}catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                tx.commit();
                tx = null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ApacheZipUtil.closeInputStream(fileInputStream);
                ApacheZipUtil.closeInputStream(fileInputStream2);
                ApacheZipUtil.closeInputStream(fileInputStream3);
                if (null != tx) {
                    tx.rollback();
                }
                try {
                    if(conn!=null) conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
	private static void updateState(String docid, String state) {
		DBConn conn = null;
		try {
			conn = new DBConn();
			StringBuilder sql = new StringBuilder("update ");
			sql.append(" GLBACKWTDOCUMENT");
			sql.append(" set STATE='").append(state).append("'");

			sql.append(" where DOCID='").append(docid).append("'");
			conn.executeUpdate(sql.toString());
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


	}
}
