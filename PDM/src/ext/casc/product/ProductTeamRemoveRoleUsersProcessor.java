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
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.util.WTException;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class ProductTeamRemoveRoleUsersProcessor extends DefaultObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean commandbean, List<ObjectBean> list) throws WTException {
        FormResult form = new FormResult();
        HttpServletRequest request = commandbean.getRequest();
        Map map = request.getParameterMap();
        Iterator iterator = map.keySet().iterator();
        Map<String, List<String>> userMap = new HashMap<String, List<String>>();
        Map<String, List<String>> groupMap = new HashMap<String, List<String>>();
        String role = "";
        while (iterator.hasNext()) {
            String key = String.valueOf(iterator.next());
            if (key.contains("removeUsers_")) {
                String[] str = key.split("_");
                String productOid = str[2];
                String userOid = str[3];
                role = str[1];
                if (userMap.size()>0) {
                    if (userMap.keySet().contains(productOid)) {
                        List<String> userOidList = userMap.get(productOid);
                        userOidList.add(userOid);
                        userMap.put(productOid, userOidList);
                    } else {
                        List<String> userOidList = new ArrayList<String>();
                        userOidList.add(userOid);
                        userMap.put(productOid, userOidList);
                    }
                } else {
                    List<String> userOidList = new ArrayList<String>();
                    userOidList.add(userOid);
                    userMap.put(productOid, userOidList);
                }

            }
            //begin add by zw
            else if(key.contains("removeGroups_")){
            	String[] str = key.split("_");
                String productOid = str[2];
                String groupOid = str[3];
                role = str[1];
                if(groupMap.size()>0){
                	if (groupMap.keySet().contains(productOid)) {
                        List<String> groupOidList = groupMap.get(productOid);
                        groupOidList.add(groupOid);
                        groupMap.put(productOid, groupOidList);
                    } else {
                        List<String> groupOidList = new ArrayList<String>();
                        groupOidList.add(groupOid);
                        groupMap.put(productOid, groupOidList);
                    }
                }else{
                	List<String> groupOidList = new ArrayList<String>();
                	groupOidList.add(groupOid);
                	groupMap.put(productOid, groupOidList);
                }
            }
            //end add by zw

        }

        Iterator<String> iterator2 = userMap.keySet().iterator();
        ReferenceFactory rf = new ReferenceFactory();
        while(iterator2.hasNext()){
            String tempProductOid = iterator2.next();
            List<String> userList = userMap.get(tempProductOid);
            PDMLinkProduct product = (PDMLinkProduct)rf.getReference(tempProductOid).getObject();
            System.out.println("----------product:"+product.getName());
            ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(product);
            Role tempRole = getRoleByName(containerTeam, role);
            for (String userOid : userList) {
                WTUser user = (WTUser)rf.getReference(userOid).getObject();
                System.out.println("----------remove user:"+user.getFullName()+" from "+role);
                ContainerTeamHelper.service.removeMember(containerTeam, tempRole, user);
            }
        }

        Iterator<String> iterator3 = groupMap.keySet().iterator();
        while(iterator3.hasNext()){
            String tempProductOid = iterator3.next();
            List<String> groupList = groupMap.get(tempProductOid);
            PDMLinkProduct product = (PDMLinkProduct)rf.getReference(tempProductOid).getObject();
            System.out.println("----------product:"+product.getName());
            ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(product);
            Role tempRole = getRoleByName(containerTeam, role);
            for (String groupOid : groupList) {
                WTGroup group = (WTGroup)rf.getReference(groupOid).getObject();
                System.out.println("----------remove user:"+group.getName()+" from "+role);
                ContainerTeamHelper.service.removeMember(containerTeam, tempRole, group);
            }
        }

        FeedbackMessage message = new FeedbackMessage();
        message.addMessage("操作成功");
        form.addFeedbackMessage(message);
        form.setStatus(FormProcessingStatus.SUCCESS);
        //form.setNextAction(FormResultAction.NONE);
        form.setNextAction(FormResultAction.REFRESH_OPENER);

        return form;
    }

    private static Role getRoleByName(ContainerTeam containerTeam,String name) throws WTException{
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
