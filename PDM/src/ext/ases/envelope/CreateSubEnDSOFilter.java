package ext.ases.envelope;


import java.util.ArrayList;
import java.util.HashMap;

import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.org.WTUser;
import wt.part.WTPartReferenceLink;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import ext.casc.constants.Constants;
import ext.casc.dfmRule.util.WTContainerTeamHelper;
import ext.casc.util.CommonUtil;


public class CreateSubEnDSOFilter extends DefaultSimpleValidationFilter{

	@SuppressWarnings("deprecation")
	@Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		Object object = criteria.getContextObject().getObject();
		WTContainer wtcontainer = criteria.getParentContainer().getContainer();
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			String currentUser = SessionHelper.manager.getPrincipal().getName();//获取当前用户名
			HashMap<?,?> userMap = WTContainerTeamHelper.findRolePrincipalMap(wtcontainer);//查找指定容器团队的角色及人员，包括本地团队和共享团队
			// 主任工艺师
			if(containsRoleUser(userMap, Constants.ROLE_KEY_ZHURENGONGYISHI, currentUser) ){
				return UIValidationStatus.ENABLED;
			}
			// 系统管理员
			if(CommonUtil.isSiteOrOrgAdmin()){
				return UIValidationStatus.ENABLED;
			}
			
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return UIValidationStatus.HIDDEN;
	}

	/**
	 * 判断是否包含指定用户
	 * @param userMap
	 * @param roleCsrZongtishejirenyuan
	 * @param currentUser
	 * @return
	 */
	private boolean containsRoleUser(HashMap<?, ?> userMap,
			String role, String currentUser) {
		ArrayList<?> roleUsers = (ArrayList<?>) userMap.get(role);
		if(roleUsers==null || roleUsers.isEmpty()){
			return false;
		}else{
			for(int i=0;i<roleUsers.size();i++){
				WTUser user = (WTUser)roleUsers.get(i);
				if(user.getName().equals(currentUser)){
					return true;
				}
			}
		}
		return false;
	}
}
