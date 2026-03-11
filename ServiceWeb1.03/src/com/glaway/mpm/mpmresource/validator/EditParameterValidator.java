package com.glaway.mpm.mpmresource.validator;

import ext.casc.access.AccessAdminUtil;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class EditParameterValidator extends DefaultSimpleValidationFilter{

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria arg1) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		try {
			WTPrincipal principal = SessionHelper.manager.getPrincipal();
			if("fanling".equals(principal.getName())){
				return  UIValidationStatus.ENABLED;
			}else if ("Administrator".equals(principal.getName())){
				return  UIValidationStatus.ENABLED;
			}else if ("niyongjun".equals(principal.getName())){
				return  UIValidationStatus.ENABLED;
			}

			WTGroup group = AccessAdminUtil.getGroupByName("白羽管理员组");
			if(group!=null){
				if(group.isMember(principal)){
					return UIValidationStatus.ENABLED;
				}
			}
			
			group = AccessAdminUtil.getGroupByName("白羽普通用户组");
			if(group!=null){
				if(group.isMember(principal)){
					return UIValidationStatus.ENABLED;
				}
			}
			
			
			
		} catch (WTException e) {
			e.printStackTrace();
		}
		return status;
	}

}
