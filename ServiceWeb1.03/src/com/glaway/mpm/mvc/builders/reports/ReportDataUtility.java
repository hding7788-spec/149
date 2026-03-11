package com.glaway.mpm.mvc.builders.reports;

import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.TextDisplayComponent;

class ReportDataUtility extends AbstractDataUtility{

	public Object getDataValue(String componentid, Object datum, ModelContext context) throws WTException {
		TextDisplayComponent text = new TextDisplayComponent(getLabel(componentid,context));
		context.getNmCommandBean();
		if("workshop".equals(componentid)){
			String value = "";
			text.setValue(value);
		}
		if("accomplishedPercent".equals(componentid)){
			
		}
		return text;
	}

}
