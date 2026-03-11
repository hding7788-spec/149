package com.glaway.mpm.mpmresource.processors;

import java.util.List;

import wt.part.WTPart;
import wt.util.WTException;

import com.glaway.mpm.util.MPMResourceUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.resource.MPMSkill;
import com.ptc.windchill.mpml.resource.MPMSkillMaster;
import com.ptc.windchill.mpml.resource.MPMTooling;
import com.ptc.windchill.mpml.resource.MPMToolingMaster;
import com.ptc.windchill.mpml.resource.MPMWorkCenter;
import com.ptc.windchill.mpml.resource.MPMWorkCenterMaster;

public class AddMPMResourceLinkProcessor extends CustomerObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean arg0, List<ObjectBean> arg1) throws WTException {
		FormResult formResult = new FormResult();
		List<NmOid> list = arg0.getNmOidSelected();
		NmOid actionOid = arg0.getActionOid();
		Object actionObj = actionOid.getRef();
		WTPart part = (WTPart) actionObj;
		if (list.size() != 0) {
			for (NmOid nmOid : list) {
				Object object = nmOid.getRef();
				if (object instanceof MPMSkill) {
					MPMSkillMaster skillMaster = (MPMSkillMaster) ((MPMSkill) object).getMaster();
					MPMResourceUtil.createWTPartUsageLink(part, skillMaster);
				} else if (object instanceof MPMWorkCenter) {
					MPMWorkCenterMaster master = (MPMWorkCenterMaster) ((MPMWorkCenter) object).getMaster();
					MPMResourceUtil.createWTPartUsageLink(part, master);
				} else if (object instanceof MPMTooling) {
					MPMToolingMaster master = (MPMToolingMaster) ((MPMTooling) object).getMaster();
					MPMResourceUtil.createWTPartUsageLink(part, master);
				}
			}
		}

		return super.doOperation(arg0, arg1);
	}
}
