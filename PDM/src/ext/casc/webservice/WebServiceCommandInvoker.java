/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类功能：webservice接口调用者
 *
 * @author hding
 * @date 2020/6/24
 */
public class WebServiceCommandInvoker {

    private WebServiceCommand command;

    public WebServiceCommandInvoker(WebServiceCommand command) {
        this.command = command;
    }

    public void setCommand(WebServiceCommand command) {
        this.command = command;
    }

    public String callService(String params) {
        String result = null;
        if (command == null) {
            JSONObject rtnMsgObj = new JSONObject();
            try {
                rtnMsgObj.put("RTN_CODE", "E");
                rtnMsgObj.put("RTN_MSG", "调用的方法未注册为webservice，请联系PDM管理员。");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            result =rtnMsgObj.toString();
        } else {
            result = command.execute(params);
        }

        return result;
    }
}
