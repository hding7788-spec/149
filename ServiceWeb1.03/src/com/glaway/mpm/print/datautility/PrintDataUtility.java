package com.glaway.mpm.print.datautility;

import wt.fc.QueryResult;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.AbstractGuiComponent;
import com.ptc.core.components.rendering.guicomponents.Label;
import com.ptc.core.components.rendering.guicomponents.TextBox;

import com.glaway.mpm.print.util.PrintDataQueryUtil;
import com.glaway.mpm.util.MPMUtil;

public class PrintDataUtility extends AbstractDataUtility {

    @Override
    public Object getDataValue(String componentId, Object object, ModelContext context) throws WTException {
    	Object reObj = null;
        if (object instanceof WTUser) {
        	WTUser user = (WTUser)object;
        	long oid = user.getPersistInfo().getObjectIdentifier().getId();
            if ("fullName".endsWith(componentId)) {
            	AbstractGuiComponent gui = new Label(user.getFullName());
            	((Label) gui).setId(oid + "_fullName");
                ((Label) gui).setName(oid + "_fullName");
                reObj = gui;
            } else if ("dept".endsWith(componentId)) {
            	String groupName = "";
            	WTGroup group = getGroupsByUser(user);
            	if (group != null) {
            		groupName = group.getName();
            	}
            	AbstractGuiComponent gui = new TextBox();
            	((TextBox) gui).setId(oid + "_dept");
                ((TextBox) gui).setName(oid + "_dept");
                ((TextBox) gui).setWidth(30);
                ((TextBox) gui).setValue(groupName);
                ((TextBox) gui).setReadOnly(true);
                reObj = gui;
            } else if ("userCode".endsWith(componentId)) {
            	String userCode = PrintDataQueryUtil.queryUserCode(String.valueOf(oid));
            	AbstractGuiComponent gui = new TextBox();
                ((TextBox) gui).setId(oid + "_userCode");
                ((TextBox) gui).setName(oid + "_userCode");
                ((TextBox) gui).setWidth(100);
                ((TextBox) gui).setValue(userCode);
                reObj = gui;
            }
        }
        return reObj;
    }

    public static WTGroup getGroupsByUser(WTPrincipal principal) throws WTException {
		WTGroup wtGroup = null;
    	QueryResult qResult = MPMUtil.queryGroup();
		while (qResult.hasMoreElements()) {
			wtGroup = (WTGroup) qResult.nextElement();
			if(wtGroup.isMember(principal) && wtGroup.getName().startsWith("部门_")) {
				return wtGroup;
			}
		}
		return null;
    }

}
