package ext.casc.util;

import com.glaway.mpm.intf.ProcessEditorToWCIntfRMI;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import ext.casc.constants.PDMConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CollectErrorBaiYuFileUtil2 implements RemoteAccess {
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
                server.invoke("excute", CollectErrorBaiYuFileUtil2.class.getName(), null, argTypes, args);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return;
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String exportPath =  PropertiesUtil.getWTHome()+File.separator+"export";
                File exportPathFile = new File(exportPath);
                if(!exportPathFile.exists())   exportPathFile.mkdirs();
                TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference("wt.doc.WTDocument|casc.sast.149.BaiYuDocument");
                long typeId = 0;
                if (tdr != null) {
                    typeId = tdr.getKey().getBranchId();
                }
                QuerySpec qs = new QuerySpec(WTDocument.class);

                qs.appendWhere(new SearchCondition(WTDocument.class,
                                "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeId),
                        new int[]{0});
                qs.appendAnd();
                qs.appendWhere(new SearchCondition(WTDocument.class,WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] { 0 });
                QueryResult qr = PersistenceHelper.manager.find(qs);

                ArrayList<ArrayList<String>> allList = new ArrayList<ArrayList<String>>();

                JSONArray urldatas  = new JSONArray();

                while(qr.hasMoreElements()) {
                    WTDocument document = (WTDocument) qr.nextElement();
                    System.out.println(document.getNumber());
                    boolean isFind = false;
                    JSONObject tableData =  getTableData(document);
                    if(tableData!=null){
                        JSONArray data = tableData.getJSONArray("data2");
                        if(data != null) {
                            for(int i = 0; i < data.length(); i++) {
                                JSONObject jsonObject = data.getJSONObject(i);
                                Iterator<String> keys = jsonObject.keys();
                                while(keys.hasNext()) {
                                    String key = keys.next();
                                    if (key.endsWith("唯一编码")) {
                                        String wybm = jsonObject.optString(key);
                                        if("cee58b8b7d7f6fac".equals(wybm)){
                                            ArrayList<String> list = new ArrayList<String>();
                                            list.add(document.getNumber());
                                            list.add(document.getName());
                                            String stepNumber = IBAHelper.getIBAStringValue(document,"stepNum");
                                            String paceNumber =  IBAHelper.getIBAStringValue(document,"paceNum");
                                            String tableType = IBAHelper.getIBAStringValue(document,"tableType");
                                            if(Tools.isNull(tableType))  tableType = "";
                                            list.add(IBAHelper.getIBAStringValue(document,"PPNUMBER"));
                                            list.add(stepNumber);
                                            list.add(paceNumber);
                                            list.add(document.getState().getState().getDisplay(Locale.CHINA));
                                            list.add(tableType);

                                            Map<String,String> map = ProcessEditorToWCIntfRMI.getFileURLByDocNumber(document.getNumber());
                                            String qbyURL = map.get("qby");
                                            String mjsonURL = map.get("mjson");
                                            String ojsonURL = map.get("ojson");

                                            list.add("qbyFileAbsolutePath="+qbyURL+
                                                    "&xlsxFileAbsolutePath="+mjsonURL+
                                                            "&jsonFileAbsolutePath="+ojsonURL+
                                                            "&technicsNumber="+document.getNumber()+
                                                            "&stepNum="+stepNumber+
                                                            "&paceNum="+paceNumber+
                                                            "&tableId="+document.getNumber()+
                                                            "&technicsPath="+
                                                            "&tableType="+tableType+
                                                            "&templateNumber="+
                                                            "&templateName=");
                                            allList.add(list);

                                            JSONObject urlJson = new JSONObject();
                                            urlJson.put("url","qbyFileAbsolutePath="+qbyURL+
                                                    "&xlsxFileAbsolutePath="+mjsonURL+
                                                    "&jsonFileAbsolutePath="+ojsonURL+
                                                    "&technicsNumber="+document.getNumber()+
                                                    "&stepNum="+stepNumber+
                                                    "&paceNum="+paceNumber+
                                                    "&tableId="+document.getNumber()+
                                                    "&technicsPath="+
                                                    "&tableType="+tableType+
                                                    "&templateNumber="+
                                                    "&templateName=");
                                            urldatas.put(urlJson);

                                            isFind = true;
                                            break;
                                        }
                                    }
                                }
                                if(isFind){
                                    break;
                                }

                            }
                        }
                    }


                }
                ArrayList<String> titles = new ArrayList<String>();
                titles.add("白羽文件编号");
                titles.add("白羽文件名称");
                titles.add("工艺文件编号");
                titles.add("工序号");
                titles.add("工步号");
                titles.add("状态");
                titles.add("表格类型");
                titles.add("工具链接");

                ExcelFileGenerator gen = new ExcelFileGenerator(titles,allList);
                String filePre = "baiyuErrorJsonExport_ERRORID(cce58b8b7d7f6fac)"+sdf.format(new Date());
                File file = new File(exportPath + File.separator +filePre+".xls");
                gen.expordExcel(new FileOutputStream(file));

                JSONArray[] splitArrays =   splitJsonArray(urldatas,300);
                int splitIndex = 0;
                for(JSONArray jsonArray:splitArrays){
                    splitIndex ++;
                    JSONObject baiYuJson = new JSONObject();
                    baiYuJson.put("pdmData",jsonArray);
                    String jsonFilePath = exportPath + File.separator +filePre+"_"+splitIndex+".json";
                    writeJsonToFile(baiYuJson.toString(4),jsonFilePath);
                }



            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static JSONArray[] splitJsonArray(JSONArray array, int chunkSize) {
        int arrayLength = array.length();
        int numChunks = (int) Math.ceil((double) arrayLength / chunkSize);
        JSONArray[] splitArrays = new JSONArray[numChunks];

        for (int i = 0; i < numChunks; i++) {
            int start = i * chunkSize;
            int end = Math.min((i + 1) * chunkSize, arrayLength);
            splitArrays[i] = new JSONArray();
            for (int j = start; j < end; j++) {
                try {
                    splitArrays[i].put(array.get(j));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return splitArrays;
    }
    public static void writeJsonToFile(String json, String filePath) {
        FileWriter fileWriter = null;
        try {
            // 创建 FileWriter 对象
            fileWriter = new FileWriter(filePath);
            // 将 JSON 字符串写入文件
            fileWriter.write(json);
            System.out.println("JSON 数据已成功写入文件: " + filePath);
        } catch (IOException e) {
            System.err.println("写入文件时出现错误: " + e.getMessage());
        } finally {
            // 手动关闭 FileWriter
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    System.err.println("关闭文件写入流时出现错误: " + e.getMessage());
                }
            }
        }
    }
    private static JSONObject getTableData(WTDocument doc) throws WTException {
        QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.SECONDARY);
        while (qr.hasMoreElements()) {
            ApplicationData appData = (ApplicationData) qr.nextElement();
            String fileName = appData.getFileName();
            if("tableData.json".equals(fileName)){
                byte[] bytes = WTDocumentUtil.applicationDataToByte(appData);
                String jsonString = null;
                try {
                    jsonString = new String(bytes, "UTF-8");
                    return new JSONObject(jsonString);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
