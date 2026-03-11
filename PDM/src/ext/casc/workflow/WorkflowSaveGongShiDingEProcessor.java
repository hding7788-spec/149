package ext.casc.workflow;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.util.MPMResourceUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.resource.MPMTooling;
import ext.casc.sop.constants.SopConstants;
import ext.casc.util.IBAHelper;
import org.apache.commons.lang3.StringUtils;
import wt.fc.ReferenceFactory;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.util.WTRuntimeException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public class WorkflowSaveGongShiDingEProcessor extends DefaultObjectFormProcessor {

    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> arg1) throws WTException {
        FormResult form = new FormResult();
        boolean flag = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
		FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "数据保存完毕！");		Transaction trx = null;
		Transaction tx = null;
		try {
			tx = new Transaction();
			tx.start();

			HttpServletRequest request = commandBean.getRequest();
			ReferenceFactory rf = new ReferenceFactory();
			Map map = request.getParameterMap();
			for (Object o : map.keySet()) {
				String key = String.valueOf(o);
				//mod by yfn 20250618 增加了单件设备工时
				if (key.endsWith("_zhunjie")) {
					if (!handleInput(request, key, "ZJGS", rf, form)) return form;
				} else if (key.endsWith("_danjian")) {
					if (!handleInput(request, key, "DJGS", rf, form)) return form;
				} else if (key.endsWith("_danJianSheBeiGS")) {
					if (!handleInput(request, key, "DanJianSheBeiGS", rf, form)) return form;
				}
			}
			form.setStatus(FormProcessingStatus.SUCCESS);
			tx.commit();
			tx = null;
        } catch(WTRuntimeException e) {
            form.setStatus(FormProcessingStatus.FAILURE);
            message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "数据保存失败！");
            e.printStackTrace();
        } finally {
            wt.session.SessionServerHelper.manager.setAccessEnforced(flag);
			if(tx != null) {
				tx.rollback();
			}
        }
        form.addFeedbackMessage(message);
        form.setNextAction(FormResultAction.NONE);
        return form;
    }

	private boolean handleInput(HttpServletRequest request, String key, String ibaKey, ReferenceFactory rf, FormResult form) throws WTException {
		String message = "";
		String[] parts = key.split("_");
		String oid = parts[0];
		String value = request.getParameter(key);
		String stepName = "";
		String isSheBeiGS = "";
		if (key.endsWith("_danJianSheBeiGS")) {
			stepName = parts[1];
			MPMTooling tooling = null;
			try {
				tooling = MPMResourceUtil.getMPMToolingByName(stepName, SopConstants.SOP_TYPE_PROCEDUCENAME);
				isSheBeiGS = IBAHelper.getIBAStringValue(tooling, "IsSheBeiGS");
				if ("是".equalsIgnoreCase(isSheBeiGS) && StringUtils.isEmpty(value)) {
					message = stepName + "，必须填写设备工时";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			if (StringUtils.isNotEmpty(value)) {
				Double.parseDouble(value);
			}
		} catch (Exception e) {
			message = "工时定额仅能填写数字";

		}
		if (StringUtils.isNotEmpty(message)) {
			form.setStatus(FormProcessingStatus.FAILURE);
			form.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, null, null, null, message));
			form.setNextAction(FormResultAction.REFRESH_CURRENT_PAGE);
			return false;
		}

		//	add by yfn 20250703 如果输入为空，则设置为0
		if (StrUtil.isEmpty(value)) {
			value = "0";
		}

		MPMOperation operation = (MPMOperation) rf.getReference(oid).getObject();
		IBAHelper.setIBAStringValue(operation, ibaKey, value);
		return true;
	}

}
