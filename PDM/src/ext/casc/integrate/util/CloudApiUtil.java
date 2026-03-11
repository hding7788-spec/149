package ext.casc.integrate.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class CloudApiUtil {
    public static String httpReuestBody(String url, JSONObject param) {
        String resultMsg = "";
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        client.getHttpConnectionManager().getParams().setSoTimeout(5000);
        PostMethod postMethod = new PostMethod(url);
        try {
            postMethod.setRequestEntity(new StringRequestEntity(
                    param.toString(),                        // JSON 内容
                    "application/json",          // Content-Type
                    "UTF-8"                      // 字符编码
            ));
            postMethod.setParameter("pdmData",param.toString());
            postMethod.addRequestHeader("Accept", "application/json");
            int statusCode = client.executeMethod(postMethod);
            if (statusCode == HttpStatus.SC_OK) {
                resultMsg = postMethod.getResponseBodyAsString();
            } else {
                resultMsg = "调用失败:"+statusCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            resultMsg = "调用失败"+e.getLocalizedMessage();
        } finally {
            // 释放连接
            postMethod.releaseConnection();
        }
        return resultMsg;
    }

    public static String httpReuestBody(String url,  Map<String,String> params) {
        String resultMsg = "";
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        client.getHttpConnectionManager().getParams().setSoTimeout(5000);
        PostMethod postMethod = new PostMethod(url);
        try {
            postMethod.setRequestEntity(new StringRequestEntity(
                    "",
                    "application/json",          // Content-Type
                    "UTF-8"                      // 字符编码
            ));
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for(Map.Entry<String, String> entry:entrySet){
                postMethod.setParameter(entry.getKey(),entry.getValue());
            }
            postMethod.addRequestHeader("Accept", "application/json");
            int statusCode = client.executeMethod(postMethod);
            if (statusCode == HttpStatus.SC_OK) {
                resultMsg = postMethod.getResponseBodyAsString();
            } else {
                resultMsg = "调用失败:"+statusCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            resultMsg = "调用失败"+e.getLocalizedMessage();
        } finally {
            // 释放连接
            postMethod.releaseConnection();
        }
        return resultMsg;
    }

    public static String httpOnlyBody(String url, JSONObject param) {
        String resultMsg = "";
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        client.getHttpConnectionManager().getParams().setSoTimeout(5000);
        PostMethod postMethod = new PostMethod(url);
        try {
            postMethod.setRequestEntity(new StringRequestEntity(
                    param.toString(),                        // JSON 内容
                    "application/json",          // Content-Type
                    "UTF-8"                      // 字符编码
            ));
            postMethod.addRequestHeader("Accept", "application/json");
            int statusCode = client.executeMethod(postMethod);
            if (statusCode == HttpStatus.SC_OK) {
                resultMsg = postMethod.getResponseBodyAsString();
            } else {
                resultMsg = "调用失败:"+statusCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            resultMsg = "调用失败"+e.getLocalizedMessage();
        } finally {
            // 释放连接
            postMethod.releaseConnection();
        }
        return resultMsg;
    }
}
