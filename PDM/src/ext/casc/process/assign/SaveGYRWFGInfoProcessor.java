package ext.casc.process.assign;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.workflow.WfUtil;

public class SaveGYRWFGInfoProcessor extends DefaultObjectFormProcessor{
	 private static String wt_temp = "";

	    static{
	        try {
	            WTProperties prop = WTProperties.getLocalProperties();
	            wt_temp = prop.getProperty("wt.temp");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> arg1) throws WTException {
	        FormResult form = new FormResult();
	        FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "数据保存完毕！");
	        try {
	            HttpServletRequest request = commandBean.getRequest();
	            WTUser user = (WTUser)SessionHelper.getPrincipal();
	            String userOid= PersistenceHelper.getObjectIdentifier((Persistable) user).toString();
	            ReferenceFactory rf = new ReferenceFactory();

	            File file = new File(wt_temp+File.separator+user.getName());
	            if (!file.exists()) {
	                file.mkdirs();
	            }
	            File tempFile = new File(wt_temp+File.separator+user.getName()+File.separator+"record.properties");
	            if(!tempFile.exists()){
	            	tempFile.createNewFile();
	            }
	            Properties properties = new Properties();
	            //将新内容添加到附件
	            Map map = request.getParameterMap();
	            Iterator iterator = map.keySet().iterator();
	            while (iterator.hasNext()) {
	                String key = String.valueOf(iterator.next());
	                if (key.indexOf("_renwuyaoqiu")>-1) {
	                    String value = request.getParameter(key);
	                    properties.setProperty(userOid+"_"+key, value);
	                } else if (key.indexOf("_zhuzhichejian")>-1) {
	                    String value = request.getParameter(key);
	                    properties.setProperty(userOid+"_"+key, value);
	                } else if (key.indexOf("_fuzhichejian1")>-1) {
	                    String[] values = request.getParameterValues(key);
	                    String tempValue = "";
	                    for (String value : values) {
	                        if (tempValue.equals("")) {
	                            tempValue = value;
	                        }else {
	                            tempValue = tempValue+";"+value;
	                        }
	                    }
	                    properties.setProperty(userOid+"_"+key, tempValue);
	                } else if (key.indexOf("_jihuawanchengshijian")>-1) {
	                    String value = request.getParameter(key);
	                    properties.setProperty(userOid+"_"+key, value);
	                }
	            }

	            //重新写入附件
	            FileOutputStream fos = new FileOutputStream(tempFile);
	            properties.store(fos, "");
	            fos.close();

	            form.setStatus(FormProcessingStatus.SUCCESS);
	        } catch (WTRuntimeException e) {
	            form.setStatus(FormProcessingStatus.FAILURE);
	            message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "数据保存失败！");
	            e.printStackTrace();
	        } catch (FileNotFoundException e) {
	            form.setStatus(FormProcessingStatus.FAILURE);
	            message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "数据保存失败！");
	            e.printStackTrace();
	        } catch (IOException e) {
	            form.setStatus(FormProcessingStatus.FAILURE);
	            message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "数据保存失败！");
	            e.printStackTrace();
	        }
	        form.addFeedbackMessage(message);
	        form.setNextAction(FormResultAction.NONE);
	        return form;
	    }


}
