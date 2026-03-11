package com.glaway.mpm.pbom.processors;

import java.util.ArrayList;
import java.util.List;

import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.util.WTException;

import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.Util;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class PBOMReleasedProcessor extends DefaultObjectFormProcessor {

	@Override
	@SuppressWarnings("deprecation")
	public FormResult doOperation(NmCommandBean cb, List<ObjectBean> list) throws WTException {
		FormResult formResult = new FormResult(FormProcessingStatus.SUCCESS);

		NmOid nmoid = cb.getActionOid();
		String partOid = nmoid.getOid().toString();
		ReferenceFactory rf = new ReferenceFactory();
		GLLogger.debug("partOid:" + partOid);
		WTPart part = (WTPart) rf.getReference(partOid).getObject();
		GLLogger.debug("part's view:" + part.getViewName());

		// if(!WTPartUtil.isPartHasChild(part)){
		// formResult.setStatus(FormProcessingStatus.FAILURE);
		// formResult.addException(new Exception("无PBOM结构"));
		// return formResult;
		// }

		List<WTPart> partList = new ArrayList<WTPart>();
		// Util.getAllPBOMChildParts(part, partList);
		Util.getAllChildParts(partList, part);
		// WTPartUtil.setPartLifecycle(part, Constants.RELEASED);//设置归档状态
		for (int i = 0; i < partList.size(); i++) {
			WTPart tempPart = partList.get(i);
			GLLogger.debug("tempPart:" + tempPart.getName() + "  " + tempPart.getViewName());
			Util.setLifecycle(tempPart, Constants.RELEASED);// 设置归档状态
		}
		return formResult;
	}

}
