package com.glaway.mpm.mpmresource.processors;

import java.util.List;

import wt.doc.WTDocument;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.type.TypedUtility;
import wt.util.WTException;

import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.mpmresource.TypeNameConstants;
import com.glaway.mpm.util.MPMResourceUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.resource.MPMTooling;

public class SetStateProcessor extends CustomerObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> list) throws WTException {
		FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
//		List<NmOid> nmOidList = commandBean.getNmOidSelected();
//		for (NmOid nmOid : nmOidList) {
//			Object object = nmOid.getRef();
//			if (object instanceof WTDocument) {
//				WTDocument document = (WTDocument) object;
//				TypeIdentifier typeIdentifier = TypedUtility.getTypeIdentifier(object);
//				if (typeIdentifier.getTypename().endsWith(TypeNameConstants.gzCardTypeName)) {
//					LifeCycleHelper.service.setLifeCycleState(document, State.toState(Constants.YZF));
//					MPMTooling tooling = MPMResourceUtil.getMPMToolingByNumber(document.getNumber().replace(".", "-"));
//					LifeCycleHelper.service.setLifeCycleState(tooling, State.toState(Constants.YZF));
//				}
//			}
//		}
		return result;
	}

}
