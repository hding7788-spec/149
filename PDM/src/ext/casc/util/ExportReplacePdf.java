package ext.casc.util;

import com.glaway.mpm.util.ApacheZipUtil;
import org.dom4j.DocumentException;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pom.Transaction;
import wt.util.WTException;

import java.beans.PropertyVetoException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.ResultSet;

public class ExportReplacePdf implements RemoteAccess {
    public   static final String PATH = File.separator+"c"+File.separator+"PrintPdf"+File.separator;
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
                        ExportReplacePdf.class.getName(), null, types, vals);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{

            DBConn conn = null;

            try {
                conn = new DBConn();

                StringBuilder sql = new StringBuilder("select * from GLBACKWTDOCUMENT ");
                if("ALL".equals(number)){
                }else{
                    sql.append("where DOCNUMBER='").append(number).append("'");
                }
                ReferenceFactory rf = new ReferenceFactory();

                ResultSet rs = conn.executeQuery(sql.toString());

                File path = new File(PATH);
                if(!path.exists()){
                    path.mkdirs();
                }

                while (rs.next()) {
                    String docid = rs.getString("DOCID");
                    WTDocument document =null;
                    try{
                         document = (WTDocument)(rf.getReference("OR:wt.doc.WTDocument:"+docid).getObject());

                    }catch(Exception e){
                    	e.printStackTrace();
                    }
                    if(document ==null) continue;
                    QueryResult secondaryqr = ContentHelper.service.getContentsByRole(
                            document, ContentRoleType.SECONDARY);
                    while (secondaryqr.hasMoreElements()) {
                        ApplicationData ad = (ApplicationData) secondaryqr.nextElement();
                        if (ad == null) {
                            continue;
                        }
                        String fileName = ad.getFileName();
                        if(fileName!=null &&fileName.startsWith("Print_")) {
                            String downloadName = "Print_" + document.getName() + "_" + document.getVersionIdentifier().getValue() + ".pdf";
                            downloadName = downloadName.replaceAll("/", "_");
                            InputStream inputstream = null;
                            FileOutputStream tout = null;
                            try{
                                tout = new FileOutputStream(PATH + downloadName);
                                inputstream = ContentServerHelper.service.findContentStream(ad);
                                if (inputstream == null) {
                                    continue;
                                }
                                byte abyte1[] = new byte[2048];
                                int k;
                                while ((k = inputstream.read(abyte1, 0, abyte1.length)) >= 0)
                                    tout.write(abyte1, 0, k);
                                tout.close();
                             }finally {
                                ApacheZipUtil.closeInputStream(inputstream);
                                ApacheZipUtil.closeOutputStream(tout);
                            }
                            break;
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                try {
                    if(conn!=null) conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
