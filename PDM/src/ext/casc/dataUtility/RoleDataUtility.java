package ext.casc.dataUtility;

import com.glaway.mpm.print.bean.CmUserQrCodeBean;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.AbstractGuiComponent;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.Label;
import com.ptc.core.components.rendering.guicomponents.StringInputComponent;
import ext.casc.constants.Constants;
import ext.casc.util.NmTableGUIComponent;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.util.WTException;

import javax.servlet.http.HttpSession;
import java.util.*;

public class RoleDataUtility extends AbstractDataUtility {

    @Override
    public Object getDataValue(String componentId, Object object, ModelContext modelContext) throws WTException {
        if (object instanceof Role) {
            Role role = (Role) object;
            if ("name".equals(componentId)) {
                return role.getDisplay(Locale.CHINA);
            } else if ("addUsers".equals(componentId)) {

            }
        } else if (object instanceof PDMLinkProduct) {
            PDMLinkProduct product = (PDMLinkProduct) object;
            ReferenceFactory rf = new ReferenceFactory();
            String productOid = rf.getReferenceString(product).toString();
            if ("name".equals(componentId)) {
                return product.getName();
            }else if ("currentUsers".equals(componentId)) {
                HttpSession session = modelContext.getNmCommandBean().getRequest().getSession();
                String selectRole = (String) session.getAttribute("selectRole");
                if (selectRole != null && !"".equals(selectRole)) {
                    ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(product);
                    Vector<Role> vector = containerTeam.getRoles();
                    List<Role> list = new ArrayList<Role>(vector);
                    Collections.sort(list);
                    StringBuffer sb = new StringBuffer();
                    boolean flag = false;
                    for (Role role : list) {
                        String name = role.getDisplay(Locale.CHINA);
                        if (name.equals(selectRole)) {
                            flag = true;
                            ArrayList<WTPrincipalReference> allUser = containerTeam.getAllPrincipalsForTarget(role);
                            for (WTPrincipalReference ref : allUser) {
                                Persistable per = ref.getObject();
                                if (per instanceof WTUser) {
                                    WTUser user = (WTUser)per;
                                    String userOid = rf.getReferenceString(user).toString();
                                    sb.append("<input type=\"checkbox\" onclick=\"selectAll(this)\" id=\"removeUsers_"+selectRole+"_"+productOid+"_"+userOid+"\" name=\"removeUsers_"+selectRole+"_"+productOid+"_"+userOid+"\" value=\""+user.getName()+"\">"+user.getFullName()+";</input>");
                                }else if (per instanceof WTGroup) {
                                    //getUserFromWTGroup(productOid,(WTGroup)per, sb);
                                	//begin add by zw
                                	WTGroup group=(WTGroup)per;
                                	String groupOid = rf.getReferenceString(group).toString();
                                    sb.append("<input type=\"checkbox\" onclick=\"selectAll(this)\" id=\"removeGroups_"+selectRole+"_"+productOid+"_"+groupOid+"\" name=\"removeGroups_"+selectRole+"_"+productOid+"_"+groupOid+"\" value=\""+group.getName()+"\">"+group.getName()+";</input>");
                                	//end add by zw
                                }
                            }
                        }
                    }
                    if (flag) {
                        GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                        guicomponentarrayMain.setValueHidden(false);
                        NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
                        guicomponentarrayMain.addGUIComponent(gui);
                        return guicomponentarrayMain;
                    }else{
                        return Constants.PRODUCT_NO_SELECTROLE;
                    }
                }else {
                    return Constants.PRODUCT_MSG_PLEASESELECTROLE;
                }

            } else if ("currentRoles".equals(componentId)) {
                ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(product);
                Vector<Role> vector = containerTeam.getRoles();
                Iterator<Role> iterator = vector.iterator();
                StringBuffer sb = new StringBuffer();
                while (iterator.hasNext()) {
                    Role role = iterator.next();
                    String name = role.getDisplay(Locale.CHINA);
                    sb.append("<input type=\"checkbox\" onclick=\"selectAll(this)\" id=\"removeRoles_"+productOid+"_"+name+"\" name=\"removeRoles_"+productOid+"_"+name+"\" value=\""+name+"\">"+name+";</input>");
                }
                GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                guicomponentarrayMain.setValueHidden(false);
                NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
                guicomponentarrayMain.addGUIComponent(gui);
                return guicomponentarrayMain;
            }
        }else  if(object instanceof CmUserQrCodeBean){
            CmUserQrCodeBean cmUserQrCodeBean = (CmUserQrCodeBean) object;
            if("userName".equals(componentId)){
                GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                guicomponentarrayMain.setValueHidden(false);
                AbstractGuiComponent gui = new Label(cmUserQrCodeBean.getUserName());
                guicomponentarrayMain.addGUIComponent(gui);
                return guicomponentarrayMain;
            }
            if("employeeNo".equals(componentId)){
                StringInputComponent strInput = new StringInputComponent();
                strInput.setEditable(true);
                strInput.setName(componentId);
                strInput.setValue(cmUserQrCodeBean.getUserCode());
                return strInput;
            }
        }
        return "";
    }

    /**
     * 循环组并且获取组里面的用户
     *
     * @param group
     * @param roleString
     * @throws WTException
     */
    public static void getUserFromWTGroup(String productOid,WTGroup group, StringBuffer sb) throws WTException {
        Enumeration member = group.members();
        while (member.hasMoreElements()) {
            WTPrincipal principal = (WTPrincipal) member.nextElement();
            if (principal instanceof WTUser) {
                WTUser user = (WTUser)principal;
                ReferenceFactory rf = new ReferenceFactory();
                String userOid = rf.getReferenceString(user).toString();
                sb.append("<input type=\"checkbox\" id=\""+productOid+"_"+userOid+"\" name=\""+productOid+"_"+userOid+"\" value=\""+user.getName()+"\">"+user.getFullName()+";</input>");
            } else if (principal instanceof WTGroup) {
                getUserFromWTGroup(productOid,(WTGroup) principal, sb);
            }
        }
    }
}
