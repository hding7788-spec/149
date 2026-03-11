/**
 * 南京国睿信维软件有限公司
 */
package ext.sast.common.util;
import com.bjsasc.avidm.mq.handler.HandlerProcess;
import com.bjsasc.avidm.mq.queue.RetryQueue;
import com.bjsasc.avidm.mq.sender.Sender;
import ext.sast.center.util.JsonConvertUtil;
import org.json.JSONObject;

import java.io.File;

/**
 * 类功能：
 *
 * @author YaQii
 * @date 2021/1/7
 */
public class RetryHelper {
    //重试操作1
    public static void retryOperationFirst(String msgId){
        JSONObject msg = RetryQueue.getInstance().getMessage(msgId);
        Sender.getInstance().sendToSelf(msg);
        RetryQueue.getInstance().finish(msgId);
    }

    //重试操作2
    public static String retryOperationSecond(String msgId){
        String result = "操作成功";
        try {
            File file = JsonConvertUtil.getJsonFromFile(msgId);
            if(file == null){
                //重新发送
                file = JsonConvertUtil.getSendJsonFromFile(msgId);
                if(file != null){
                    String msg = JsonConvertUtil.fileRead(file);
                    JSONObject jsonObject = new JSONObject(msg);
                    Sender sender = Sender.getInstance();
                    sender.send(jsonObject);
                }
            }
            if(file!=null){
                String msg = JsonConvertUtil.fileRead(file);
                if(file.getAbsolutePath().contains(JsonConvertUtil.DcDistributeRequestHandler)){
                    JSONObject jsonObject = new JSONObject(msg);
                    HandlerProcess.doDcDistributeRequestHandler(jsonObject);
                }else if(file.getAbsolutePath().contains(JsonConvertUtil.DcShareRequestHandler)){
                    JSONObject jsonObject = new JSONObject(msg);
                    HandlerProcess.doDcShareRequestHandler(jsonObject);
                }
                else if(file.getAbsolutePath().contains(JsonConvertUtil.DcSignRequestHandler)){
                    JSONObject jsonObject = new JSONObject(msg);
                    HandlerProcess.doDcSignRequestHandler(jsonObject);
                }
                else if(file.getAbsolutePath().contains(JsonConvertUtil.DcSignTaskSynResponseHandler)){
                    JSONObject jsonObject = new JSONObject(msg);
                    HandlerProcess.doDcSignTaskSynResponseHandler(jsonObject);
                }
                else if(file.getAbsolutePath().contains(JsonConvertUtil.DcSignTeminateRequestHandler)){
                    JSONObject jsonObject = new JSONObject(msg);
                    HandlerProcess.doDcSignTeminateRequestHandler(jsonObject);
                }
            }else{
                result = "没有对应的消息文件！";
            }
        }catch (Exception e){
            e.printStackTrace();
            result = "操作失败";
        }
        return result;
    }
}
