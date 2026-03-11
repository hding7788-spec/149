package ext.casc.tools;

import ext.casc.mpm.process.TechnicsGenerator;
import ext.casc.util.DBUtil;
import ext.casc.util.DocUtil;
import org.dom4j.DocumentException;
import wt.doc.WTDocument;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitProcessTemplateParams implements RemoteAccess {
    public   static final String PATH = File.separator+"c"+File.separator+"PrintPdf"+File.separator;
    private static final long serialVersionUID = 1L;
    public static void main(String[] args) {
        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        String username = null;
        String passwd = null;
        if (args.length >= 2) {
            username = args[0];
            passwd = args[1];


            if (username == null)
                username = "wcadmin";

            if (passwd == null)
                passwd = "Admin@149.941";
        }
        System.out.println("------user:"+username+"    password:"+passwd);
        rms.setUserName(username);
        rms.setPassword(passwd);
        try {
            process();
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
    public static void process() throws WTException, IOException, PropertyVetoException, DocumentException{
        if (!RemoteMethodServer.ServerFlag) {
            String method = "process";
            Class[] types = {};
            Object[] vals = { };

            RemoteMethodServer rms = RemoteMethodServer.getDefault();
            try {
                rms.invoke(method,
                        InitProcessTemplateParams.class.getName(), null, types, vals);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{

            Map<String,String> paramsMap = new HashMap<String, String>();
            paramsMap.put(DocUtil.QUERY_TYPE,"wt.doc.WTDocument|casc.sast.149.TechnicsTemplate|casc.sast.149.TechnicsParamTemplate");

            List<WTDocument> docs = DocUtil.getLastestDocs(paramsMap);
            for(WTDocument doc :docs){

                try {
                    Map<String,String> paramsMap2 = new HashMap<String, String>();
                    paramsMap2.put("TEMPLATEID","OR:wt.doc.WTDocument:"+doc.getPersistInfo().getObjectIdentifier().getId());
                    if(!DBUtil.existData("GLPROCESSPARAMDEFINITION",paramsMap2)) {
                        TechnicsGenerator.structureGyTempldateParams(doc);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {

                }
            }
        }
    }

}
