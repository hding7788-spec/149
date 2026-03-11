package ext.casc.system;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import ext.sast.common.fc.CmPersistenceHelper;
import wt.method.RemoteAccess;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import java.io.Serializable;
import java.util.List;

public class SystemConfigurationProcessor implements RemoteAccess, Serializable {

    private static final long serialVersionUID = 1L;

    public SystemConfigurationProcessor() {}

    public static FormResult addSystemConfiguration(NmCommandBean nmCommandBean) {
        FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        FeedbackMessage message = new FeedbackMessage();
        try {
            String key = (String) nmCommandBean.getText().get("key");
            String value = (String) nmCommandBean.getText().get("value");
            String remark = (String) nmCommandBean.getTextArea().get("remark");
            SystemConfigurationBean bean = SystemConfigurationUtil.getSystemConfigurationBean(key);
            if(bean != null) {
                message.addMessage("配置：" + key + "已存在，添加失败！");
                result.addFeedbackMessage(message);
                result.setStatus(FormProcessingStatus.FAILURE);
            } else {
                bean = new SystemConfigurationBean(key, value, remark);
                CmPersistenceHelper.manager.save(bean);
                message.addMessage("新增成功！");
                result.addFeedbackMessage(message);
                result.setStatus(FormProcessingStatus.SUCCESS);
                result.setNextAction(FormResultAction.REFRESH_OPENER);
            }
        } catch (Exception e) {
            message.addMessage("添加系统变量失败！");
            result.addFeedbackMessage(message);
            result.setStatus(FormProcessingStatus.FAILURE);
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enforce);
        }
        return result;
    }

    public static FormResult deleteSystemConfiguration(NmCommandBean commandbean) throws WTException {
        FormResult form = new FormResult();
        FeedbackMessage message = new FeedbackMessage();
        List selected = commandbean.getSelected();
        for (Object obj : selected) {
            NmContext nmContext = (NmContext)obj;
            String key = nmContext.getTargetOid().toString();
            SystemConfigurationBean bean = SystemConfigurationUtil.getSystemConfigurationBean(key);
            if(bean != null) {
                try {
                    CmPersistenceHelper.manager.delete(bean);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        message.addMessage("删除成功！");
        form.addFeedbackMessage(message);
        form.setStatus(FormProcessingStatus.SUCCESS);
        form.setNextAction(FormResultAction.NONE);
        form.setNextAction(FormResultAction.REFRESH_OPENER);
        return form;
    }

    public static FormResult editSystemConfiguration(NmCommandBean nmCommandBean) {
        FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        FeedbackMessage message = new FeedbackMessage();
        try {
            String key = (String) nmCommandBean.getText().get("key");
            String value = (String) nmCommandBean.getText().get("value");
            String remark = (String) nmCommandBean.getTextArea().get("remark");
            SystemConfigurationBean bean = SystemConfigurationUtil.getSystemConfigurationBean(key);
            if(bean != null) {
                bean.setValue(value);
                bean.setRemark(remark);
                CmPersistenceHelper.manager.update(bean);
                message.addMessage("修改成功！");
                result.addFeedbackMessage(message);
                result.setStatus(FormProcessingStatus.SUCCESS);
                result.setNextAction(FormResultAction.REFRESH_OPENER);
            } else {
                message.addMessage("配置：" + key + "不存在，修改失败！");
                result.addFeedbackMessage(message);
                result.setStatus(FormProcessingStatus.FAILURE);
            }
        } catch (Exception e) {
            message.addMessage("修改系统变量失败！");
            result.addFeedbackMessage(message);
            result.setStatus(FormProcessingStatus.FAILURE);
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enforce);
        }
        return result;
    }

}
