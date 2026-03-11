package com.glaway.mpm.pbom.processors;

import java.util.List;

import wt.part.WTPart;
import wt.util.WTException;

import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.WorkflowUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class StartPBOMWorkFlowProcessor extends DefaultObjectFormProcessor {

	/**
	 * 启动PBOM构造流程
	 * @author lbzhang
	 * @date  2012-11-19下午08:39:34
	 * @param cb
	 * @param list
	 * @return
	 * @throws WTException
	 */
	
	@SuppressWarnings("deprecation")
	@Override
	public FormResult doOperation(NmCommandBean cb, List<ObjectBean> list) throws WTException {
		FormResult formResult = new FormResult(FormProcessingStatus.SUCCESS);
		GLLogger.debug("=====StartPBOMWorkFlowProcessor====");
		NmOid nmoid = cb.getActionOid();
		Object obj = nmoid.getRef();
		if(!(obj instanceof WTPart)){
			
			formResult.setStatus(FormProcessingStatus.FAILURE);
			formResult.addException(new Exception("The select is not ZJPart!"));
			return formResult;
		}
		if(obj instanceof WTPart){
			WTPart part = (WTPart)obj;
			GLLogger.debug("part:" + part.getName() + "  " + part.getNumber() + "  " + part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue());
			
			WorkflowUtil.startProcess(part, "工艺派工流程", part.getNumber() + "_" + part.getName());
			GLLogger.debug("start pbom workflow has been started!");
			formResult.setNextAction(FormResultAction.JAVASCRIPT);
			formResult.setJavascript("alert(\"PBOM构造流程已启动,并提交给工艺主师!\");");
		}
		
		return formResult;
	}
	
}
