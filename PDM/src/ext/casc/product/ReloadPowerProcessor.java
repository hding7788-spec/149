package ext.casc.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import wt.fc.Persistable;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.util.WCUtil;

/**
 * @describe used to submit approval workFlow process
 * @author Long,XiuChuan
 * @since 2012/7/23
 *
 */
public class ReloadPowerProcessor extends DefaultObjectFormProcessor {
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
        FormResult formresult = super.doOperation(commandBean, objectBeans);
    	WTPrincipal currentUser = SessionHelper.manager.getPrincipal();
        wt.session.SessionHelper.manager.setAdministrator();
        try{
        	FeedbackMessage message = new FeedbackMessage();
            message.addMessage("加载完成");
            formresult.addFeedbackMessage(message);
            formresult.setNextAction(FormResultAction.NONE);
        	PDMLinkProduct product = WCUtil.getProductByName("CE-3_测试");
        	ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(product);
        	Role role = Role.toRole("YICHEJIANGONGYIYUAN");
            ArrayList<WTPrincipalReference> allUser = containerTeam.getAllPrincipalsForTarget(role);
            boolean isReload = false;

            for (WTPrincipalReference ref : allUser) {
                Persistable per = ref.getObject();
           	    if (per instanceof WTUser) {
                    WTUser user = (WTUser)per;
                    if(currentUser.getName().equals(user.getName())){
                        ContainerTeamHelper.service.removeMember(containerTeam, role, user);
                        ContainerTeamHelper.service.addMember(containerTeam, role, user);
                        isReload = true;
                        break;
                    }
                }
            }
            if(!isReload){
            	 ContainerTeamHelper.service.addMember(containerTeam, role, currentUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            wt.session.SessionHelper.manager.setPrincipal(currentUser.getName());
        }
        return formresult;
    }

}
