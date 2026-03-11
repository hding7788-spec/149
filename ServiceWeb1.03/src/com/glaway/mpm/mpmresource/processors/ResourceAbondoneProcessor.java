package com.glaway.mpm.mpmresource.processors;

import java.util.List;

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
import com.ptc.windchill.mpml.resource.MPMPlant;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;
import com.ptc.windchill.mpml.resource.MPMSkill;
import com.ptc.windchill.mpml.resource.MPMTooling;
import com.ptc.windchill.mpml.resource.MPMWorkCenter;

public class ResourceAbondoneProcessor extends DefaultObjectFormProcessor {

	@SuppressWarnings("deprecation")
	@Override
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> list) throws WTException {
		FormResult formResult = new FormResult(FormProcessingStatus.SUCCESS);
		
		NmOid nmoid = commandBean.getActionOid();
		Object obj = nmoid.getRef();
		if(obj instanceof MPMWorkCenter){//工位
			MPMWorkCenter workCenter = (MPMWorkCenter)obj;
			String processName = workCenter.getName() + "_" + workCenter.getVersionIdentifier().getValue() + "." + workCenter.getIterationIdentifier().getValue() + "_作废";
			boolean flag1 = WorkflowUtil.startProcess(workCenter, "工艺资源作废流程", processName);
			GLLogger.debug("workCenter==flag1====>" + flag1);
		}
		if(obj instanceof MPMSkill){//工种
			MPMSkill skill = (MPMSkill)obj;
			String processName = skill.getName() + "_" + skill.getVersionIdentifier().getValue() + "." + skill.getIterationIdentifier().getValue() + "_作废";
			boolean flag1 = WorkflowUtil.startProcess(skill, "工艺资源作废流程", processName);
			GLLogger.debug("skill==flag1====>" + flag1);
		}
		if(obj instanceof MPMPlant){//制造单位
			MPMPlant plant = (MPMPlant)obj;
			String processName = plant.getName() + "_" + plant.getVersionIdentifier().getValue() + "." + plant.getIterationIdentifier().getValue() + "_作废";
			boolean flag1 = WorkflowUtil.startProcess(plant, "工艺资源作废流程", processName);
			GLLogger.debug("plant==flag1====>" + flag1);
		}
		if(obj instanceof MPMTooling){//刀具、工具、设备、量具
			MPMTooling tooling = (MPMTooling)obj;
			String processName = tooling.getName() + "_" + tooling.getVersionIdentifier().getValue() + "." + tooling.getIterationIdentifier().getValue() + "_作废";
			boolean flag1 = WorkflowUtil.startProcess(tooling, "工艺资源作废流程", processName);
			GLLogger.debug("flag1====>" + flag1);
		}else if(obj instanceof MPMProcessMaterial){//工艺辅料
			MPMProcessMaterial material = (MPMProcessMaterial)obj;
			String processName = material.getName() + "_" + material.getVersionIdentifier().getValue() + "." + material.getIterationIdentifier().getValue() + "_作废";
			boolean flag2 = WorkflowUtil.startProcess(material, "工艺资源作废流程", processName);
			GLLogger.debug("flag2====>" + flag2);
		}else{
			formResult.setStatus(FormProcessingStatus.FAILURE);
			formResult.addException(new Exception("所选对象不是资源，请重新选择!"));
		}
		
		formResult.setNextAction(FormResultAction.JAVASCRIPT);
		formResult.setJavascript("alert(\"资源作废流程已启动!\");");
		
		return formResult;
	}
}
