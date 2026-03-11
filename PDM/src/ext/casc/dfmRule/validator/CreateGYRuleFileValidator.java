package ext.casc.dfmRule.validator;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;

import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.folder.Cabinet;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.vc.wip.WorkInProgressHelper;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import ext.casc.dfmRule.util.TeamUtil;

/**
 *
 * @author xuetao
 *
 *
 */
public class CreateGYRuleFileValidator extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		try {
			Object object = criteria.getContextObject().getObject();
			if (object instanceof WTDocument) {
				WTDocument doc = (WTDocument) object;
				// 文档类型
				String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
				// 是否检出
				boolean isCheckOut = WorkInProgressHelper.isCheckedOut(doc);
				// 是否已有流程正在进行
				boolean hasProcess = false;
				QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(doc, null, null);
				while (qrProcs.hasMoreElements()) {
					WfProcess process = (WfProcess) qrProcs.nextElement();
					if (process.getState().equals(WfState.OPEN_RUNNING)) {
						hasProcess = true;
						break;
					}
				}
				// 当前用户是否为存储库管理员
				boolean isManager = TeamUtil.isManager(doc.getContainer());
				if (docType.indexOf("casc.sast.149.GYRULEFILE") != -1 && isManager) {
					return UIValidationStatus.ENABLED;
				} else {
					return UIValidationStatus.HIDDEN;
				}
			}
			if(object instanceof Cabinet){
				Cabinet cab = (Cabinet) object;
				// 当前用户是否为存储库管理员
				boolean isManager = TeamUtil.isManager(cab.getContainer());
				if(isManager){
					return UIValidationStatus.ENABLED;
				}else{
					return UIValidationStatus.HIDDEN;
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return UIValidationStatus.DISABLED;
	}

	public static void getTeamRole(WTContainer library) throws WTException {
		ContainerTeam team = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) library);
		Enumeration enumeration = ContainerTeamHelper.service.findContainerTeamGroups(team, ContainerTeamHelper.ROLE_GROUPS);

		while (enumeration.hasMoreElements()) {
			ArrayList userList = new ArrayList();
			WTGroup roleGroup = (WTGroup) enumeration.nextElement();
			ArrayList principalList = team.getAllPrincipalsForTarget(Role.toRole(roleGroup.getName()));
			for (int i = 0; principalList != null && i < principalList.size(); i++) {
				WTPrincipal principal = (WTPrincipal) ((WTPrincipalReference) principalList.get(i)).getPrincipal();
				if (principal instanceof WTUser)
					userList.add(principal);
				else if (principal instanceof WTGroup) {
					Enumeration en = ((WTGroup) principal).members();
					while (en.hasMoreElements()) {
						Object o = en.nextElement();
						if (o instanceof WTUser && userList.indexOf(o) < 0)
							userList.add(o);
					}
				}
			}

		}

	}
}
