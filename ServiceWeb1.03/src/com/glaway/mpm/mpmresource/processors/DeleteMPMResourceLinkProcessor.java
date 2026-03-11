package com.glaway.mpm.mpmresource.processors;

import java.util.HashMap;
import java.util.List;

import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.iba.value.IBAHolder;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.Workable;

import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMResourceUtil;
import com.glaway.mpm.util.WorkInProcessUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.resource.MPMPlant;
import com.ptc.windchill.mpml.resource.MPMPlantMaster;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;
import com.ptc.windchill.mpml.resource.MPMProcessMaterialMaster;
import com.ptc.windchill.mpml.resource.MPMResourceGroup;
import com.ptc.windchill.mpml.resource.MPMResourceGroupMaster;
import com.ptc.windchill.mpml.resource.MPMSkill;
import com.ptc.windchill.mpml.resource.MPMSkillMaster;
import com.ptc.windchill.mpml.resource.MPMTooling;
import com.ptc.windchill.mpml.resource.MPMToolingMaster;
import com.ptc.windchill.mpml.resource.MPMWorkCenter;
import com.ptc.windchill.mpml.resource.MPMWorkCenterMaster;

public class DeleteMPMResourceLinkProcessor extends CustomerObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean arg0, List<ObjectBean> arg1) throws WTException {
		FormResult formResult = new FormResult();
		List<NmOid> list = arg0.getNmOidSelected();
		NmOid actionOid = arg0.getActionOid();
		Object actionObj = actionOid.getRef();
		for (NmOid nmOid : list) {
			Object object = nmOid.getRef();

			WTPartUsageLink link = MPMResourceUtil.getLinkByParentAndChild((WTPart) actionObj, (WTPart) object);
			if (link != null) {
				PersistenceServerHelper.manager.remove(link);
			}
		}
		return super.doOperation(arg0, arg1);

	}
}
