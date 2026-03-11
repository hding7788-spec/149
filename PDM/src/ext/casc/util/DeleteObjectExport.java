package ext.casc.util;

import com.bjsasc.avidm.mq.message.Based;
import com.glaway.mpm.util.PropertiesUtil;
import com.ptc.extend.ixb.CmExpImpSearchHelper;
import ext.casc.constants.PDMConfig;
import ext.sast.center.util.JsonConvertUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.Iterated;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DeleteObjectExport implements RemoteAccess {
    public static void main(String[] args) throws WTException {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        rms.setUserName("wcadmin");
        rms.setPassword(PDMConfig.WCADMIN_PASSWORD);
        excute() ;
    }
    public  static void excute() throws WTException {

        if (!RemoteMethodServer.ServerFlag) {
            Class[] argTypes = {};
            Object[] args = {};
            try {
                SessionHelper.manager.setPrincipal("administrator");
                RemoteMethodServer server = RemoteMethodServer.getDefault();
                server.setUserName("wcadmin");
                //				server.setPassword("wcadmin");
                server.setPassword(PDMConfig.WCADMIN_PASSWORD);
                server.invoke("excute", DeleteObjectExport.class.getName(), null, argTypes, args);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return;
        } else {
            try {
                ArrayList<ArrayList<String>> allList = new ArrayList<ArrayList<String>>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String exportPath =  PropertiesUtil.getWTHome()+File.separator+"export";
                File exportPathFile = new File(exportPath);
                if(!exportPathFile.exists())   exportPathFile.mkdirs();
                File dcSignRequestHandlerPath = new File(JsonConvertUtil.PERSISTENTPATH+File.separator+JsonConvertUtil.DcSignRequestHandler);
                File[] jsonFiles =  dcSignRequestHandlerPath.listFiles();
                for(File jsonFile:jsonFiles){
                    if(jsonFile.getName().endsWith("_805")&&jsonFile.getName().startsWith("GPZ")){
                        String msg = JsonConvertUtil.fileRead(jsonFile);
                        JSONObject jsonObject = new JSONObject(msg);
                        JSONArray ja_objects = jsonObject.getJSONArray(Based.JA_OBJECTS_REQUEST);
                        for(int i = 0; i < ja_objects.length(); i++) {
                            JSONObject obj = ja_objects.getJSONObject(i);
                            String objId = obj.optString(Based.OBJECT_OID);
                            String version = obj.optString(Based.OBJECT_VERSION);
                            String number = obj.optString(Based.OBJECT_ID);
                            if(!Tools.isNull(objId)&&!Tools.isNull(version)&&!Tools.isNull(number)){
                                number = number.toUpperCase();
                                String[] versionStr = version.split("\\.");
                                Class clazzName ;
                                if(objId.contains("WTDocument")){
                                    clazzName = WTDocument.class;
                                }else if(objId.contains("WTPart")){
                                    clazzName = WTPart.class;
                                }else if(objId.contains("EPMDocument")){
                                    clazzName = EPMDocument.class;
                                }else{
                                    continue;
                                }
                                if(versionStr.length==2){
                                    Iterated verionDoc = CmExpImpSearchHelper.searchLatestIteratedByNumberVersion(clazzName,
                                            number.toUpperCase(), versionStr[0]);
                                    if(verionDoc==null){
                                        ArrayList<String> list = new ArrayList<String>();
                                        list.add(number);
                                        list.add(version);
                                        list.add(clazzName.getSimpleName());
                                        list.add("大版本被删除");
                                        allList.add(list);
                                    }else{
                                        Iterated doc = CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(clazzName,
                                                number.toUpperCase(), versionStr[0],versionStr[1]);
                                        if(doc==null){
                                            ArrayList<String> list = new ArrayList<String>();
                                            list.add(number);
                                            list.add(version);
                                            list.add(clazzName.getSimpleName());
                                            list.add("小版本被删除");
                                            allList.add(list);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                ArrayList<String> titles = new ArrayList<String>();
                titles.add("删除对象编号");
                titles.add("删除对象版本");
                titles.add("删除对象类型");
                titles.add("删除情况");

                ExcelFileGenerator gen = new ExcelFileGenerator(titles,allList);
                String filePre = "deleteObjectExport"+sdf.format(new Date());
                File file = new File(exportPath + File.separator +filePre+".xls");
                gen.expordExcel(new FileOutputStream(file));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
