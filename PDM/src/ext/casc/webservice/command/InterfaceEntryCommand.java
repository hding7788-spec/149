package ext.casc.webservice.command;

import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import wt.log4j.LogR;

public class InterfaceEntryCommand  implements WebServiceCommand, InitializingBean {
    private static final Logger LOGGER = LogR.getLogger(InterfaceEntryCommand.class.getName());
    // 方法标识
    public static final String METHOD_NAME = "interfaceEntry";
    @Override
    public String execute(String params) {
        String rtnCode = "E";
        String rtnMsg="";
        JSONObject jrtnObj = new JSONObject();

        JSONObject jparams;
        try {
            jparams = new JSONObject(params);
            String callName =jparams.optString("callName");
            Object[] objectArray = new Object[1];
            objectArray[0]=jparams;
            Object obj = null;
            if("".equals(callName)){
                Class commandHelperClass =  Class.forName("ext.casc.webservice.command.CommandHelper");
                obj = commandHelperClass.getMethod(callName, JSONObject.class).invoke(null, objectArray);

            }
            if(obj!=null){
                jrtnObj.put(WSConstants.RTN_DATA, obj);
                jrtnObj.put(WSConstants.RTN_CODE, "S");
            }else{
                jrtnObj.put(WSConstants.RTN_CODE, "N");
            }

        } catch (JSONException e) {
            rtnMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
            LOGGER.error(rtnMsg, e);
        }  catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            jrtnObj.put(WSConstants.RTN_MSG, rtnMsg);
            jrtnObj.put(WSConstants.RTN_CODE, rtnCode);
        } catch (JSONException ex) {
            LOGGER.error("Error building JSON: ", ex);
        }

        return jrtnObj.toString();
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        WebServiceCommandFactory.register(METHOD_NAME, this);
    }
}
