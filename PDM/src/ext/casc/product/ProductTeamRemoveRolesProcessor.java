package ext.casc.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import wt.fc.ReferenceFactory;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.util.WTException;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.role.NmRoleCommands;
import com.ptc.netmarkets.role.StandardNmRoleService;

public class ProductTeamRemoveRolesProcessor extends DefaultObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean commandbean, List<ObjectBean> list) throws WTException {
        FormResult form = new FormResult();
        HttpServletRequest request = commandbean.getRequest();
        Map map = request.getParameterMap();
        Iterator iterator = map.keySet().iterator();
        Map<String, List<String>> userMap = new HashMap<String, List<String>>();
        while (iterator.hasNext()) {
            String key = String.valueOf(iterator.next());
            if (key.contains("removeRoles_")) {
                System.out.println("-------------key:" + key);
                String[] str = key.split("_");
                String productOid = str[1];
                String roleOid = str[2];
                System.out.println("-------------productOid:" + productOid);
                System.out.println("-------------roleOid:" + roleOid);
                System.out.println("----------userMap:" + userMap);
                if (userMap.size() > 0) {
                    System.out.println("-----1");
                    if (userMap.keySet().contains(productOid)) {
                        List<String> roleOidList = userMap.get(productOid);
                        roleOidList.add(roleOid);
                        userMap.put(productOid, roleOidList);
                    }else {
                        List<String> roleOidList = new ArrayList<String>();
                        roleOidList.add(roleOid);
                        userMap.put(productOid, roleOidList);
                    }
                } else {
                    System.out.println("-----2");
                    List<String> roleOidList = new ArrayList<String>();
                    roleOidList.add(roleOid);
                    userMap.put(productOid, roleOidList);
                }

            }

        }
        System.out.println("----------userMap:" + userMap);
        Iterator<String> iterator2 = userMap.keySet().iterator();
        ReferenceFactory rf = new ReferenceFactory();
        while (iterator2.hasNext()) {
            String tempProductOid = iterator2.next();
            List<String> roleList = userMap.get(tempProductOid);
            PDMLinkProduct product = (PDMLinkProduct) rf.getReference(tempProductOid).getObject();
            System.out.println("----------product:" + product.getName());
            ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(product);
            for (String name : roleList) {
                Role role = getRoleByName(containerTeam, name);
                System.out.println("---------remove role:" + name + " from " + product.getName());
                if (role != null) {
                    containerTeam.deleteRole(role);
                }
            }

        }

        form.setStatus(FormProcessingStatus.SUCCESS);
        form.setNextAction(FormResultAction.NONE);
        form.setNextAction(FormResultAction.REFRESH_OPENER);
        return form;
    }

    private static Role getRoleByName(ContainerTeam containerTeam, String name) throws WTException {
        Vector<Role> vector = containerTeam.getRoles();
        Iterator<Role> iterator = vector.iterator();
        while (iterator.hasNext()) {
            Role role = iterator.next();
            String nameTemp = role.getDisplay(Locale.CHINA);
            if (name.equals(nameTemp)) {
                return role;
            }
        }
        return null;
    }
}
