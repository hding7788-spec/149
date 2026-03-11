package ext.casc.validator;

import java.util.Enumeration;

import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import ext.csc.utilities.principal.CSCPrincipal;

public class RecoverRuleOfRoleValidator extends DefaultSimpleValidationFilter{

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		String id = key.getComponentID();
		//System.out.println("lkc>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		try {
			WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
			if (currentUser.getName().equals("Administrator")){
				return UIValidationStatus.ENABLED;
			}else{
			//	Object object = criteria.getContextObject().getObject();
				//String userName = currentUser.getName();
				//WTUser user = CSCPrincipal.getUserByName(userName);
				Enumeration groups = currentUser.parentGroupNames();
				while(groups.hasMoreElements()){
					String gname = (String) groups.nextElement();
					if(gname.contains("档案员")){
						System.out.println("当前用户所在的组>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + gname);
						return UIValidationStatus.ENABLED;
					}
				}
			}
		} catch (WTException e){
			e.printStackTrace();
		}
		return UIValidationStatus.HIDDEN;
	}

}
