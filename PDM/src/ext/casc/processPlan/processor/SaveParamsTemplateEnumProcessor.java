package ext.casc.processPlan.processor;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.mpm.GyCsServerHelper;
import ext.casc.mpm.process.GLProcessParamDefinition;
import ext.casc.processPlan.Constants;
import ext.casc.util.Tools;
import ext.sast.common.fc.CmPersistenceHelper;
import wt.util.WTException;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SaveParamsTemplateEnumProcessor extends DefaultObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> arg1) throws WTException {
        FormResult form = new FormResult();
        HttpServletRequest request = commandBean.getRequest();
        Map map = request.getParameterMap();
        Iterator iterator = map.keySet().iterator();
		String templateId = null;
        while (iterator.hasNext()) {
            String key = String.valueOf(iterator.next());
            if(key.startsWith(Constants.PRE)){
            	String[] ss = (String[])map.get(key);
				String enumValue =  ss[0];
            	if(!Tools.isTrimNull(enumValue)){
					String templateIdAndParamNumber = key.substring(5);
					String paramNumber = "";
					if(templateId==null){
						templateId = templateIdAndParamNumber.substring(0,templateIdAndParamNumber.indexOf("_"));
					}
                    paramNumber =  templateIdAndParamNumber.substring(templateIdAndParamNumber.indexOf("_")+1);

                    GLProcessParamDefinition paramDefinition=GyCsServerHelper.getGLProcessParamDefinition(templateId,paramNumber);
					if(paramDefinition!=null){
						paramDefinition.setEnumValues(enumValue);
						try {
							CmPersistenceHelper.manager.save(paramDefinition);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}


                }
            }

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
