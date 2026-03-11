package com.glaway.mpm.pbom.datautilities;

import wt.part.WTPart;
import wt.util.WTException;

import com.glaway.mpm.util.IBAHelper;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;

public class PBOMReleasedReportDataUtility extends AbstractDataUtility {

	@Override
	public Object getDataValue(String componentId, Object obj, ModelContext modelcontext)
			throws WTException {
		WTPart part = (WTPart) obj;

		if(componentId.equals("routing")) {
			IBAHelper ibaHelper = new IBAHelper(part);
			String zzcj = ibaHelper.getIBAValue("ZZCJ");
			String fzcj = ibaHelper.getIBAValue("FZCJ");
			String str = "";
			if(zzcj != null && !"".equals(zzcj)) {
				str = zzcj;
			}
			if(fzcj != null && !"".equals(fzcj)) {
				if(!"".equals(str)) {
					str = str + "-" + fzcj;
				} else {
					str = fzcj;
				}
			}
			return str;
		}

		return null;
	}

}
