package ext.casc.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.method.RemoteAccess;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.util.WTException;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.util.WCUtil;

public class ProductTeamAddRoleUsersCommands implements RemoteAccess, Serializable {

    private static final long serialVersionUID = 1L;

    public ProductTeamAddRoleUsersCommands() {

    }

    public static FormResult addUsers(NmCommandBean nmcommandbean) throws WTException {
        FormResult formresult = new FormResult(FormProcessingStatus.SUCCESS);
        formresult.setNextAction(FormResultAction.REFRESH_OPENER);

        HttpSession session = nmcommandbean.getRequest().getSession();
        String selectRole = (String) session.getAttribute("selectRole");
        List<PDMLinkProduct> productList = (List<PDMLinkProduct>) session.getAttribute("productList");

        List<WTPrincipal> list = new ArrayList<WTPrincipal>();

        String s = nmcommandbean.getTextParameter("hiddenemail");
        System.out.println("-----------s:" + s);
        if (s == null) {
            formresult.setStatus(FormProcessingStatus.FAILURE);
            return formresult;
        } else {
            if (s.indexOf("#")>-1) {
                String[] str = s.split("#");
                for (String string : str) {
                	if(string.startsWith("uid=")){
                		 String[] str2 = string.split(",");
                         for (String string2 : str2) {
                             if (string2.startsWith("uid=")) {
                                 String name = string2.substring(string2.indexOf("=") + 1, string2.length());
                                 System.out.println("-----------name:" + name);
                                 if("wcadmin".equals(name)){
                                 	name ="Administrator";
                                 }
                                 WTUser user = WCUtil.getUser(name);
                                 if (user != null) {
                                     list.add(user);
                                 }
                             }
                         }
                	}else if(string.startsWith("cn")){
                		String name = string.substring(string.indexOf("=") + 1,string.indexOf(","));
                		WTGroup group=WCUtil.getGroup(name);
                		list.add(group);
                    }
                   
                }
            }else {
            	if(s.startsWith("uid=")){
            		 String[] str = s.split(",");
                     for (String string : str) {
                         if (string.startsWith("uid=")) {
                             String name = string.substring(string.indexOf("=") + 1, string.length());
                             System.out.println("-----------name:" + name);
                             if("wcadmin".equals(name)){
                             	name ="Administrator";
                             }
                             WTUser user = WCUtil.getUser(name);
                             if (user != null) {
                                 list.add(user);
                             }
                         }
                     }
            	}else if(s.startsWith("cn")){
            		String name = s.substring(s.indexOf("=") + 1,s.indexOf(","));
            		WTGroup group=WCUtil.getGroup(name);
            		list.add(group);
                }
            }
        }
        System.out.println("----------list-size:" + list.size());

        for (PDMLinkProduct product : productList) {
            ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(product);
            Role tempRole = getRoleByName(containerTeam, selectRole);
            for (WTPrincipal wtPrincipal : list) {
                System.out.println("------------add user:" + wtPrincipal.getName() + " to " + selectRole);
                if (tempRole != null) {
                    ContainerTeamHelper.service.addMember(containerTeam, tempRole, wtPrincipal);
                }
            }
        }

        return formresult;
    }

    public static FormResult addRoles(NmCommandBean nmcommandbean) throws WTException {
        FormResult formresult = new FormResult(FormProcessingStatus.SUCCESS);
        formresult.setNextAction(FormResultAction.REFRESH_OPENER);

        HttpSession session = nmcommandbean.getRequest().getSession();
        List<PDMLinkProduct> productList = (List<PDMLinkProduct>) session.getAttribute("productList");
        System.out.println("--------productList:" + productList);

        String as[] = nmcommandbean.getTextParameterValues("orgRoles");
        String s = nmcommandbean.getTextParameter("projectRoles");
        System.out.println("--------as:" + as);
        if (as != null) {
            for (PDMLinkProduct product : productList) {
                ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(product);
                for (String string : as) {
                    Role role = Role.toRole(string);
                    System.out.println("--------string:" + string);
                    System.out.println("--------role:" + role.getDisplay(Locale.CHINA));
                    System.out.println("----------add role:" + role.getDisplay(Locale.CHINA) + " to "
                            + product.getName());
                    containerTeam.addPrincipal(role, null);
                    // TeamHelper.service.addRolePrincipalMap(role, null, containerTeam);
                }
            }
        } else {
            formresult.setStatus(FormProcessingStatus.FAILURE);
            return formresult;
        }

        return formresult;
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
