package com.glaway.mpm.pbom.datautilities;


import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.ReferenceFactory;
import com.glaway.mpm.util.Util;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class PBOMResourceInfoDataUtility extends AbstractDataUtility {

	/**
	 * 工艺派工条件限制
	 */
	public Object getDataValue(String componentId, Object obj, ModelContext modelcontext) throws WTException {
		GLLogger.debug("componentId:" + componentId);
		Object commandBean = modelcontext.getNmCommandBean();
		NmCommandBean command = (NmCommandBean)commandBean;
		HttpServletRequest request = command.getRequest();
		String oid = request.getParameter("oid");
		GLLogger.debug("oid:" + oid);

		WTPrincipal principal = SessionHelper.manager.getPrincipal();//当前用户
		String currentUserName = principal.getName();

		WTPart temp = (WTPart) ReferenceFactory.getObjectbyOid(oid);

		WTPart part = (WTPart) obj;

		String lifecycleStatus = part.getLifeCycleState().getStringValue();
		//当前状态已归档则不可以选择责任组
		if(lifecycleStatus.endsWith(Constants.RELEASED)){
			return Boolean.valueOf(true);
		}

		String responsor = Util.getSoftAttribute(part, "responsor");

		//无责任人则不可以选择责任组
		if("".equals(responsor) || responsor == null){
			return Boolean.valueOf(true);
		}

		WTUser user = (WTUser)ReferenceFactory.getObjectbyOid(responsor);
		GLLogger.debug("user===>" + user.getName());
		//零件的责任人不是当前用户则不可以选择责任组
		if(!currentUserName.equals(user.getName())){
			ArrayList<String> currentList = Util.getUsersGroups(currentUserName, part);
			ArrayList<String> userList = Util.getUsersGroups(user.getName(), part);

			boolean flag = false;
			for(int i = 0; i < currentList.size(); i++){
				String currentGroup = currentList.get(i);
				if(currentGroup.indexOf("MPMLEADER") < 0){
					continue;
				}

				String currentGroupTemp = currentGroup.substring(0, currentGroup.indexOf("MPMLEADER"));
				if(userList.contains(currentGroupTemp) || userList.contains(currentGroup)){
					flag = true;
					break;
				}
			}
			if(!flag){
				return Boolean.valueOf(true);
			}
		}

		if (WTPartUtil.isPartHasChild(part) && !part.getNumber().equals(temp.getNumber())) {
			GLLogger.debug("currentPart:" + part.getName());
			return Boolean.valueOf(false);
		}

		return Boolean.valueOf(false);
	}
}
