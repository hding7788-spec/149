/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONException;
import org.json.JSONObject;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import ext.casc.process.ProcessTask;

/**
 * 类功能：接口功能共用方法类
 *
 * @author liaojun
 * @date 2020-7-8
 */
public class Util {

    public static ProcessTask queryProcessTaskByNumber(String processTaskNumber, String processTaskName) throws WTException {
        ProcessTask processTask = null;
        QuerySpec querySpec = new QuerySpec(ProcessTask.class);
        querySpec.appendWhere(new SearchCondition(ProcessTask.class, ProcessTask.NUMBER, SearchCondition.EQUAL, processTaskNumber), new int[]{0});
        querySpec.appendAnd();
        querySpec.appendWhere(new SearchCondition(ProcessTask.class, ProcessTask.NAME, SearchCondition.EQUAL, processTaskName), new int[]{0});
        QueryResult queryresult = PersistenceHelper.manager.find(querySpec);
        if (queryresult.hasMoreElements()) {
            processTask = (ProcessTask) queryresult.nextElement();
        }
        return processTask;
    }

    public static String invokeOtherSystemByPostMethod(String url, String userName, String password) throws IOException {
        InputStream is = null;  // 输入流
        BufferedReader br = null;
        String result = null;
        HttpClient httpClient = new HttpClient(); // 创建httpClient实例
        if (userName != null) {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userName, password);  //需要验证
            httpClient.getState().setCredentials(AuthScope.ANY, credentials);
        }
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000); // 设置http连接主机服务超时时间：15000毫秒
        PostMethod postMethod = new PostMethod(url); // 创建一个Post方法实例对象
        postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);   // 设置get请求超时为60000毫秒
        // 设置请求重试机制，默认重试次数：3次，参数设置为true，重试机制可用，false相反
//        postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));
        try {
            int statusCode = httpClient.executeMethod(postMethod); // 执行Post方法
            // 判断返回码
            if (statusCode != 200) {
                JSONObject temp = new JSONObject();
                temp.put("status", "N");
                temp.put("msg", postMethod.getStatusLine());//如果状态码返回的不是ok,说明失败了,打印错误信息
                return temp.toString();
            } else {
                is = postMethod.getResponseBodyAsStream();  // 通过postMethod实例，获取远程的一个输入流
                br = new BufferedReader(new InputStreamReader(is, "UTF-8")); // 包装输入流
                StringBuffer sbf = new StringBuffer();    // 读取封装的输入流
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp).append("\r\n");
                }
                result = sbf.toString();
                br.close();
                is.close();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 释放连接
            postMethod.releaseConnection();
        }
        return result;
    }


    public static String processNameStr(String name) {
        if (name.indexOf("(") > 0) {
            return name.substring(0, name.lastIndexOf("("));
        } else {
            return name;
        }
    }

}
