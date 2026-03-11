package ext.sast.center.processor;

import ext.sast.center.synch.MQConstants;
import ext.sast.center.util.DataBaseUtil;
import ext.sast.center.util.PropertiesUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;
import wt.method.MethodContext;
import wt.pom.WTConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/4/12
 * @ Description：
 * @ Modified By：
 */
public class InvokeRestServiceProcessor {

    private static String center_properties_path = "/ext/sast/center/center.properties";


    public static JSONObject getUserAndUnitInfo(String domain_iid, String unitInner, String processName, String userName) {

        String result = "";
        String url = MQConstants.DC_USERS+"/avidm/rest/dc/dom/" + domain_iid + "/users";
        if (userName != null && !userName.isEmpty()) {
            url = MQConstants.DC_USERS+"/avidm/rest/dc/dom/query/users?domain_iid=" + domain_iid + "&user_id=" + userName;
        }
        PropertiesUtil propertiesUtil = new PropertiesUtil(File.separator + center_properties_path);
        String PROCESS_HUIQIAN = propertiesUtil.getProperty("PROCESS_HUIQIAN");
        String PROCESS_FAFANG = propertiesUtil.getProperty("PROCESS_FAFANG");
        String PROCESS_YUSHEN = propertiesUtil.getProperty("PROCESS_YUSHEN");
        /**
         *
         *
         * PDM-TODO:此判断逻辑还需确认修改 add by liangbo 20190702
         *
         *
         */

        if (PROCESS_HUIQIAN.contains(processName)) {
            if (unitInner.contains("812")
                    || unitInner.contains("149")) {
                //主任工艺师
                result = "[{\"id\":\"\",\"name\":\"主任工艺师\"}]";
            } else if (unitInner.contains("800")) {
                //型号工艺师
                result = "[{\"id\":\"\",\"name\":\"型号工艺师\"}]";
            } else {
                //执行人
                result = invokeRestService(url);
            }
        } else if (PROCESS_FAFANG.contains(processName)) {
            if (unitInner.contains("800")) {
                //型号工艺师
                result = "[{\"id\":\"\",\"name\":\"型号工艺师\"}]";
            } else if (unitInner.contains("812")) {
                //调度/档案/主任工艺师
                result = "[{\"id\":\"\",\"name\":\"调度\"},{\"id\":\"\",\"name\":\"档案\"},{\"id\":\"\",\"name\":\"主任工艺师\"}]";
            } else if (unitInner.contains("149")) {
                //主任工艺师
            } else if (unitInner.contains("802")
                    || unitInner.contains("803")
                    || unitInner.contains("804")
                    || unitInner.contains("806")
                    || unitInner.contains("811")) {
                //资料员
                result = "[{\"id\":\"\",\"name\":\"资料员\"}]";
            }
        } else if (PROCESS_YUSHEN.contains(processName)) {
            if (unitInner.contains("800")) {
                //型号工艺师
                result = "[{\"id\":\"\",\"name\":\"型号工艺师\"}]";
            } else if (unitInner.contains("812")) {
                //主任工艺师
                result = "[{\"id\":\"\",\"name\":\"主任工艺师\"}]";
            } else {
                //执行人
                result = invokeRestService(url);
            }
        } else if("工艺更改单签审流程".equals(processName) || "三级工艺文件签审流程".equals(processName) || "五级工艺文件签审流程".equals(processName)) {
            if (unitInner.contains("800")) {
                //型号工艺师
                result = "[{\"id\":\"\",\"name\":\"型号工艺师\"}]";
            } else if (unitInner.contains("812")) {
                //主任工艺师
                result = "[{\"id\":\"\",\"name\":\"主任工艺师\"}]";
            } else {
                //执行人
                result = invokeRestService(url);
            }
        }

        // result = "[{\"iid\":\"111\",\"id\":\"zhangsan\",\"name\":\"张三\"},{\"iid\":\"222\",\"id\":\"lisi\",\"name\":\"李四\"}]";
        System.out.println(result);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray;
        if (!result.isEmpty()) {
            jsonArray = new JSONArray(result);
        } else {
            jsonArray = new JSONArray();
        }
        jsonObject.put("data", jsonArray);
        jsonObject.put("totalCount", jsonArray.length());
        return jsonObject;
    }

    public static JSONObject getAllSiteInfo() {
        JSONObject jsonObject = generateSiteData();
        JSONArray jsonArray = (JSONArray) jsonObject.get("data");
        jsonObject.put("totalCount", jsonArray.length());
        return jsonObject;
    }

    private static JSONObject generateSiteData() {

        MethodContext methodcontext = null;
        WTConnection wtConnection = null;
//        Connection connection = null;
        PreparedStatement preparedStatement = null;
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject childObj;
        ResultSet resultSet = null;
        int index = 1;
        try {
            methodcontext = MethodContext.getContext();
            String sql = "select * from SYNCHSITEINFO";
            wtConnection = (WTConnection) methodcontext.getConnection();
//            connection = wtConnection.getConnection();
            preparedStatement = wtConnection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                childObj = new JSONObject();
                childObj.put("index", index);
                childObj.put("unitid", resultSet.getString("IID"));
                childObj.put("unitcode", resultSet.getString("ID"));
                childObj.put("unit", resultSet.getString("NAME"));
                childObj.put("centerUser", "");
                jsonArray.put(childObj);
                index++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(resultSet != null){
                    resultSet.close();;
                }
                if(preparedStatement != null){
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        jsonObject.put("data", jsonArray);
        return jsonObject;
    }

    private static JSONObject generateUserData() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject childObj;
        for (int i = 0; i < 10; i++) {
            childObj = new JSONObject();
            childObj.put("index", "" + i);
            childObj.put("userName", "张三" + i);
            childObj.put("loginName", "zhangsan" + i);
            childObj.put("deptName", i + "部门");
            jsonArray.put(childObj);
        }
        jsonObject.put("data", jsonArray);
        return jsonObject;
    }

    /**
     * 调用rest接口 add by liangbo 20190702
     *
     * @param url
     * @return
     */
    public static String invokeRestService(String url) {
        StringBuffer stringBuffer = new StringBuffer("");
        try {
            URL restServiceURL = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) restServiceURL.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            if (httpURLConnection.getResponseCode() != 200) {
                throw new RuntimeException("HTTP GET Request Failed with Error code : " + httpURLConnection.getResponseCode());
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
            String output;
            while ((output = bufferedReader.readLine()) != null) {
                stringBuffer.append(output);
            }
            httpURLConnection.disconnect();
            bufferedReader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }
}
