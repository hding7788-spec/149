package ext.casc.process.validator;

import java.util.ArrayList;

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
import wt.session.SessionServerHelper;
import wt.util.WTException;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import ext.casc.constants.Constants;
import ext.casc.process.ProcessTask;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.util.ProcessUtil;
import ext.casc.util.IBAUtility;

public class ProcessTaskGYZZActionsValidators extends DefaultSimpleValidationFilter {

    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
    	 boolean enforce = SessionServerHelper.manager
                 .setAccessEnforced(false);
    	try {
            WTUser curentUser = (WTUser) SessionHelper.getPrincipal();
            Object object = criteria.getContextObject().getObject();

            String id = key.getComponentID();
            if (object instanceof WTPart) {
                WTPart part = (WTPart) object;
                String viewName = part.getViewName();
                IBAUtility ibaUtility = new IBAUtility(part);
                String partType = ibaUtility.getIBAValue("MTYPE");

                //自制件、外配套件、带料委外件、不带料委外件类型的零部件
                if (!Constants.TYPE_ZIZHIJIAN.equals(partType)
                		&& !Constants.TYPE_WAIPEITAOJIAN.equals(partType)
                		&& !Constants.TYPE_DAILIAOWEIWAIJIAN.equals(partType)
                		&& !Constants.TYPE_BUDAILIAOWEIWAIJIAN.equals(partType)) {
                    return UIValidationStatus.HIDDEN;
                }
                if (!"Manufacturing".equals(viewName)) {
                    return UIValidationStatus.HIDDEN;
                }
                if (key.toString().contains("ChangeGYZZProAssignTask")||key.toString().contains("ChangeGYZZProAssignTask")) {//已分工了的自制件不再显示入口
                    if (ProcessUtil.isExistProcessTask(part)) {
                        return UIValidationStatus.HIDDEN;
                    }
                }
//                if (key.toString().contains("ChangeProAssignTask")||key.toString().contains("tempProAssignTask")) {//只针对已经分工过的 自制件
//                    if (!ProcessUtil.isExistProcessTask(part)) {
//                        return UIValidationStatus.HIDDEN;
//                    }
//                }
                WTContainer wtContainer = part.getContainer();
                ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtContainer);

                Role role = Role.toRole("GONGYIZUZHANG");
                if (role==null) {
                    return UIValidationStatus.DISABLED;
                }
                ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
                for (WTPrincipalReference reference : arrayList) {
                    Object object2 = reference.getPrincipal();
                    if (object2 instanceof WTUser) {
                        WTUser user = (WTUser)object2;
                        if (user.getName().equals(curentUser.getName())) {
                            return UIValidationStatus.ENABLED;
                        }
                    }else if (object2 instanceof WTGroup) {
						WTGroup group = (WTGroup) object2;
						if (group.isMember(curentUser)) {
                            return UIValidationStatus.ENABLED;

						}
					}
                }
            }
            if (id.indexOf("deleteTaskItem")>-1||id.indexOf("addProAssignTask")>-1||id.indexOf("reassignTaskItem2")>-1) {
                Object obj = criteria.getPageObject().getObject();
                WTContainer wtContainer = null;
                if (obj instanceof ProcessTask) {
                    ProcessTask processTask = (ProcessTask)obj;
                    wtContainer = processTask.getContainer();
                }else {
                    ProcessTaskItem taskItem = (ProcessTaskItem)obj;
                    wtContainer = taskItem.getContainer();
                }
                if (wtContainer != null) {
                    ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtContainer);
                    Role role = Role.toRole("GONGYIZUZHANG");
                    if (role==null) {
                        return UIValidationStatus.DISABLED;
                    }
                    ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
                    for (WTPrincipalReference reference : arrayList) {
                        Object object2 = reference.getPrincipal();
                        if (object2 instanceof WTUser) {
                            WTUser user = (WTUser)object2;
                            if (user.getName().equals(curentUser.getName())) {
                                return UIValidationStatus.ENABLED;
                            }
                        }else if (object2 instanceof WTGroup) {
    						WTGroup group = (WTGroup) object2;
    						if (group.isMember(curentUser)) {
                                return UIValidationStatus.ENABLED;

    						}
    					}
                    }
                }
            }
        } catch (WTException e) {
            e.printStackTrace();
        }finally{
        	 SessionServerHelper.manager
             .setAccessEnforced(enforce);
        }
        return UIValidationStatus.HIDDEN;
    }

}
