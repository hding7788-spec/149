package ext.casc.purge.validator;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import wt.fc.Persistable;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import ext.casc.constants.Constants;

public class DeleteInvalidDataValidator extends DefaultSimpleValidationFilter {

    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        try {
            WTUser currentUser = (WTUser)SessionHelper.manager.getPrincipal();
            Object object = criteria.getContextObject().getObject();
            //判断是否是产品经理
            if (object instanceof PDMLinkProduct) {
                PDMLinkProduct product = (PDMLinkProduct)object;
                ContainerTeamManaged teamManaged = (ContainerTeamManaged) product;
                ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(teamManaged);
                Vector<Role> vector = containerTeam.getRoles();
                Iterator<Role> iterator = vector.iterator();
                Role role = null;
                while (iterator.hasNext()) {
                    role = iterator.next();
                    String name = role.getDisplay();
                    if (Constants.ROLE_PRODUCTMANAGER.equals(name)) {
                        ArrayList<WTPrincipalReference> allUser = containerTeam.getAllPrincipalsForTarget(role);
                        for (WTPrincipalReference ref : allUser) {
                            Persistable per = ref.getObject();
                            if (per instanceof WTUser) {
                                WTUser user = (WTUser) per;
                                if (user.equals(currentUser)) {
                                    return UIValidationStatus.ENABLED;
                                }
                            }

                        }
                    }
                }
            }

            //判断是否是管理员
            Enumeration enumeration = currentUser.parentGroupNames();
            while(enumeration.hasMoreElements()){
                String groupName = (String)enumeration.nextElement();
                if ("Administrators".equals(groupName)) {
                    return UIValidationStatus.ENABLED;
                }
            }

        } catch (WTRuntimeException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        }
        return UIValidationStatus.DISABLED;
    }

}
