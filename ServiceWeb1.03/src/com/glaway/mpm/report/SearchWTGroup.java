package com.glaway.mpm.report;

import java.util.ArrayList;
import java.util.List;

import wt.util.WTException;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class SearchWTGroup extends DefaultObjectFormProcessor{
	public FormResult doOperation(NmCommandBean param, List<ObjectBean> listBean) throws WTException {
		List list = new ArrayList();
		String gourp = param.getTextParameter("group");
		FormResult formResult = new FormResult();
		

		return super.doOperation(param, listBean);
	}
}
