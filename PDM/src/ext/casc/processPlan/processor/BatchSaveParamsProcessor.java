package ext.casc.processPlan.processor;

import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.mpm.GyCsServerHelper;
import ext.casc.mpm.process.GLProcessParamDefinition;
import ext.casc.mpm.process.GLProcessParamValues;
import ext.casc.processPlan.Constants;
import ext.casc.util.DBUtil;
import ext.casc.util.Tools;
import ext.sast.common.fc.CmPersistenceHelper;
import wt.part.WTPart;
import wt.util.WTException;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class BatchSaveParamsProcessor extends DefaultObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> arg1) throws WTException {
        FormResult form = new FormResult();
        HttpServletRequest request = commandBean.getRequest();

		//Set<String> hasDelete = new HashSet<String>();
        Map map = request.getParameterMap();
        Iterator iterator = map.keySet().iterator();
		String templateId = null;
		HashMap<String,String> deleteTempValues = new HashMap<String, String>();
        while (iterator.hasNext()) {
            String key = String.valueOf(iterator.next());
            if(key.startsWith(Constants.PRE)){
            	String[] ss = (String[])map.get(key);
				String templateIdAndParamNumber = key.substring(4);
				if(templateId==null){
					templateId = templateIdAndParamNumber.substring(0,templateIdAndParamNumber.indexOf("_"));
				}
				String paramNumberAndPartNumber =  templateIdAndParamNumber.substring(templateIdAndParamNumber.indexOf("_")+1);
            	String paramNumber = paramNumberAndPartNumber.substring(0,paramNumberAndPartNumber.indexOf("_"));
				String partNumber = paramNumberAndPartNumber.substring(paramNumberAndPartNumber.indexOf("_")+1);
            	WTPart part = WTPartUtil.getLatestPartByNumberAndView(partNumber, "Manufacturing");
            	String allV = ss[0];
				deleteTempValues.put(partNumber,templateId);
            	if(part!=null &&!Tools.isNull(allV)){

            		GLProcessParamValues pv = new GLProcessParamValues();
            		pv.setKeyId(partNumber+"_"+paramNumber);
            		pv.setPartNumber(partNumber);
            		pv.setPartVersion(part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue());
            		pv.setGyParamNumber(paramNumber);
            		pv.setTemplateId(templateId);
					GLProcessParamDefinition ppd = GyCsServerHelper.getGLProcessParamDefinition(templateId,paramNumber);
            		if(ppd==null){
            			continue;
            		}
            		pv.setGyParamName(ppd.getGyParamName());

            		if(allV.contains(";")){
            			String[] vv = allV.split(";");

            			for(String v:vv){
            				if(v.contains(":")){
            					String[] vv2 = v.split(":");
                				if(vv2.length>=2){
                					if("公称值".equals(vv2[0])){
                            			pv.setGongChengZhi(vv2[1]);
                					}else if("上偏差".equals(vv2[0])){
                            			pv.setShangPianCha(vv2[1]);
                					}else if("下偏差".equals(vv2[0])){
                            			pv.setXiaPianCha(vv2[1]);
                					}else if("符号".equals(vv2[0])){
                            			pv.setFuHao(vv2[1]);
                					}else if("基准1".equals(vv2[0])){
                            			pv.setJiZhun1(vv2[1]);
                					}else if("基准2".equals(vv2[0])){
                            			pv.setJiZhun2(vv2[1]);
                					}else if("基准3".equals(vv2[0])){
                            			pv.setJiZhun3(vv2[1]);
                					}
                				}
            				}else{
                    			pv.setParamValue(v);
            				}

            			}
            		}else{
            			pv.setParamValue(allV);
            		}
            		try {
						CmPersistenceHelper.manager.save(pv);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            }

        }

		Set<Map.Entry<String, String>> entries = deleteTempValues.entrySet();
		for(Map.Entry<String, String> entry:entries){
			String sql = "delete from GLPROCESSPARAMVALUES where PARTNUMBER='"+entry.getKey()+"' AND TEMPLATEID !='"+entry.getValue()+"'";
			DBUtil.deleteBySql(sql);
		}


		FeedbackMessage message = new FeedbackMessage();
        StringBuffer msg = new StringBuffer("参数保存成功");
        FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;

        form.setStatus(formProcessingStatus);
        message.addMessage(msg.toString());
        form.addFeedbackMessage(message);
        form.setNextAction(FormResultAction.NONE);


        return form;
    }

}
