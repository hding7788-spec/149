package com.glaway.mpm.pbom.validator;

import java.util.ArrayList;
import java.util.List;

import wt.fc.QueryResult;
import wt.fc.WTReference;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;

import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.Util;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class PBOMStartFlowZJPartValidator extends DefaultSimpleValidationFilter {

	/**
	 * 启动PBOM构造流程按钮的隐藏
	 */
	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		try {
            WTReference wtref = criteria.getContextObject();
            WTUser curentUser = (WTUser) SessionHelper.getPrincipal();
            if (wtref != null) {
            	Object obj = wtref.getObject();
            	if (obj instanceof WTPart) {
            		WTPart part = (WTPart) obj;

            		//判断是否是主任工艺师
            		boolean flag = false;
            		WTContainer wtContainer = part.getContainer();
                    ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtContainer);
                    Role role = Role.toRole("ZHURENGONGYISHI");
                    ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
                    for (WTPrincipalReference reference : arrayList) {
                        Object object2 = reference.getPrincipal();
                        if (object2 instanceof WTUser) {
                            WTUser user = (WTUser) object2;
                            if (user.getName().equals(curentUser.getName())) {
                                flag = true;
                                break;
                            }
                        }else if (object2 instanceof WTGroup) {
    						WTGroup group = (WTGroup) object2;
    						if (group.isMember(curentUser)) {
    							 flag = true;
                                 break;

    						}
    					}
                    }
                    if(!flag) {
                        return UIValidationStatus.HIDDEN;
                    }

            		String viewName = part.getViewName();
            		if(viewName.equals(Constants.planning)){
            			return UIValidationStatus.HIDDEN;
            		}

            		//判断当前对象是否有已经启动的PBOM构建流程
            		QueryResult qr = WfEngineHelper.service.getAssociatedProcesses(part, WfState.OPEN_RUNNING, null);
                    while(qr.hasMoreElements()){
                        WfProcess process = (WfProcess) qr.nextElement();
                        String temp = process.getTemplate().getName();
                        if(Constants.gyPaiGongTemplateName.equals(temp)){
                            GLLogger.debug("part===>" + part.getName() + " has exist!");
                            return UIValidationStatus.HIDDEN;
                        }
                    }

//            		if("RELEASED".equals(part.getLifeCycleState().toString())){
//            			return UIValidationStatus.ENABLED;
//            		}
            	}
            }
        } catch (WTRuntimeException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        }
		return UIValidationStatus.ENABLED;
	}

	/**
	 * 是否BOM都已归档
	 *
	 * @author lbzhang
	 * @date 2012-12-3下午12:16:43
	 * @param part
	 * @return
	 */
	public boolean isAllPartIsReleased(WTPart part) {
		List<WTPart> list = new ArrayList<WTPart>();
		Util.getAllChildParts(list, part);
		for (int i = 0; i < list.size(); i++) {
			WTPart temp = list.get(i);
			String lifeStatus = temp.getLifeCycleState().getStringValue();
			if (!lifeStatus.endsWith(Constants.RELEASED)) {
				return false;
			}
		}
		return true;
	}
}
