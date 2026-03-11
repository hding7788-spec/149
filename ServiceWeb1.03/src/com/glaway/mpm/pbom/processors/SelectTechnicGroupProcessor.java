package com.glaway.mpm.pbom.processors;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wt.fc.PersistenceHelper;
import wt.part.WTPart;
import wt.project.Role;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.Util;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class SelectTechnicGroupProcessor extends DefaultObjectFormProcessor {

	@Override
	@SuppressWarnings(value={"deprecation","unchecked"})
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> list) throws WTException {
		GLLogger.debug("======SelectTechnicGroup=====");
		HashMap boxMap = commandBean.getComboBox();
		ArrayList selectGroupList = (ArrayList)boxMap.get("group");//选择的工艺组
		ArrayList selectResponserList = (ArrayList)boxMap.get("respon");//选择的负责人
		String selectGroupRole = (String)selectGroupList.get(0);//获取角色组
		String selectResponser = (String)selectResponserList.get(0);//获取角色组中的负责人
		GLLogger.debug("selected group role is:" + selectGroupRole);
		GLLogger.debug("selected responer is:" + selectResponser);
		Role role = Role.toRole(selectGroupRole);
		GLLogger.debug("group role:" + role.getShortDescription());
		
		
		ArrayList oidList = commandBean.getSelectedOidForPopup();
		for(int i = 0; i < oidList.size(); i++){
			NmOid nmoid = (NmOid)oidList.get(i);
			WTPart part = (WTPart)nmoid.getRef();
			try {
				Util.setPartSoftAttri(part, "technicGroup", role.getFullDisplay());//设置软属性,设置工艺组
				part = (WTPart)PersistenceHelper.manager.refresh(part);
				Util.setPartSoftAttri(part, "responsor", selectResponser.trim());
				part = (WTPart)PersistenceHelper.manager.refresh(part);
				
//				setChildParts(part, role.getFullDisplay(), selectResponser.trim());
			} catch (WTPropertyVetoException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		FormResult fromResult = new FormResult(FormProcessingStatus.SUCCESS);
		fromResult.setNextAction(FormResultAction.REFRESH_OPENER);
		return fromResult;
	}
	
	/**
	 * 设置其子阶零件的工艺组及其工艺负责人
	 * @author lbzhang
	 * @date  2012-11-19下午02:43:44
	 * @param temp
	 * @throws WTException 
	 * @throws RemoteException 
	 * @throws WTPropertyVetoException 
	 */
	public static void setChildParts(WTPart temp, String role, String responser) throws WTException, WTPropertyVetoException, RemoteException{
		ArrayList<WTPart> list = (ArrayList<WTPart>) WTPartUtil.getChildPart(temp);
		for(int i = 0; i < list.size(); i++){
			WTPart part = list.get(i);
			if(WTPartUtil.isPartHasChild(part)){
				Util.setPartSoftAttri(part, "technicGroup", role);//设置软属性,设置工艺组
				part = (WTPart)PersistenceHelper.manager.refresh(part);
				Util.setPartSoftAttri(part, "responsor", responser);
			}
		}
	}

}
