package ext.casc.access;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;

public class SYFLValidator extends DefaultSimpleValidationFilter {
	@Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		try {
			if(isSysAdmin()){
				return UIValidationStatus.HIDDEN;
			}else if(isSecAdmin()){
				return UIValidationStatus.HIDDEN;
			}else if(isAuditAdmin()){
				return UIValidationStatus.HIDDEN;
			}else{
				return UIValidationStatus.ENABLED;
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return UIValidationStatus.ENABLED;
    }

	public static boolean isSecAdmin() throws WTException {
		WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
		WTGroup group = getGroupByName("安全保密员");
		if(group == null) {
			return false;
		}
		return group.isMember(currentUser);
	}

    public static boolean isSysAdmin() throws WTException{
		WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
		WTGroup group = getGroupByName("系统管理员");
		if(group == null) {
			return false;
		}
		return group.isMember(currentUser);
	}

	public static boolean isAuditAdmin() throws WTException{
		WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
		WTGroup group = getGroupByName("安全审计员");
		if(group == null) {
			return false;
		}
		return group.isMember(currentUser);
	}

	public static WTGroup getGroupByName(String groupName){
		WTGroup wtGroup = null;

		try {
			QuerySpec qs = new QuerySpec(WTGroup.class);
			SearchCondition sc = new SearchCondition(WTGroup.class, WTGroup.NAME, SearchCondition.EQUAL,groupName,false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while(qr.hasMoreElements()){
				wtGroup = (WTGroup) qr.nextElement();
				if(wtGroup.getDn().indexOf("cn=public") >= 0){
					return wtGroup;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wtGroup;
	}



}
