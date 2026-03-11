package ext.casc.part;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContained;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.project.Role;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamReference;
import wt.util.WTException;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.process.util.ProcessUtil;
import ext.casc.util.CSCPrincipal;

public class GYWJPrintTaskProcessor extends DefaultObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean cb, List<ObjectBean> arg1)
			throws WTException {
		FormResult form = new FormResult();
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			Map removeItems = cb.getRemovedItems();
			ArrayList<NmOid> removeOids = (ArrayList<NmOid>)removeItems.get("ext.casc.part.mvc.builder.GYWJPrintTaskBuilder");
			List<String> removeGywjs = new ArrayList<String>();
			for(NmOid nm :removeOids){
				removeGywjs.add(PersistenceHelper.getObjectIdentifier((Persistable) nm.getRefObject()).toString());
			}
			Map map = cb.getRequestData().getParameterMap();
			Object oidO = map.get("oid");				//读取任务页面的流程oid
			String oid = "";
			if(oidO instanceof String[]){
				oid = ((String[])oidO)[0];
			}else{
				oid = oidO.toString();
			}
			ReferenceFactory rf = new ReferenceFactory();
			WTPart pbo = (WTPart) rf.getReference(oid).getObject();
			List<WTPart> parts = PackagedPartHelper.getCHGLPartBOMData(oid);
			List<String> gywjs = new ArrayList<String>();
	
			for (WTPart part : parts) {
				List<WTDocument> docs = CSCPart.getDescribedDocumentsWithoutEpmByPart(part);
				for(WTDocument doc :docs){
					String docoid = PersistenceHelper.getObjectIdentifier(doc).toString();
					String docType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(doc).toString();
					if(docType.contains("casc.sast.149.PROCESS_DOC")&&!gywjs.contains(docoid)){
						gywjs.add(docoid);
					}
				}
			}
			gywjs.removeAll(removeGywjs);
			String gywjPrintOid = "";
			for(String s:gywjs){
				gywjPrintOid=gywjPrintOid+s+";";
			}
			if(gywjPrintOid.endsWith(";")){
				gywjPrintOid = gywjPrintOid.substring(0, gywjPrintOid.length()-1);
			}
			WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
			.getProcessDefinition("工艺文件打印流程");
			WfProcess wfprocess = null;
			wfprocess = WfEngineHelper.service.createProcess(
					wfprocessdefinition, null,
					pbo.getContainerReference());
			wfprocess.setName("工艺文件打印流程_"
					+ pbo.getNumber());
			ProcessData processdata = wfprocess.getContext();
			processdata.setValue("printFiles",
					gywjPrintOid);
			processdata.setValue("primaryBusinessObject",
					pbo);// 设置流程主对象
			
			
			Role danganyuan = Role.toRole("DANGANYUAN");
			WTContained contained = pbo.getContainer();
			ContainerTeamManaged teamManaged = (ContainerTeamManaged) contained;
		    ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(teamManaged);			
		    ArrayList<WTPrincipalReference> allUser =containerTeam.getAllPrincipalsForTarget(danganyuan);
		    System.out.println("-----------allUser:"+allUser);
		    List<WTUser> users = new ArrayList<WTUser>();
		    if (allUser==null||allUser.isEmpty()) {//从共享团队里获取档案员
		        OrgContainer orgCon = ProcessUtil.getOrgContainer();
		        ContainerTeam shareContainerTeam = ContainerTeamHelper.service.getSharedTeamByName(orgCon, "产品共享团队");
		        allUser = shareContainerTeam.getAllPrincipalsForTarget(danganyuan);
            }
		    System.out.println("-----------allUser:"+allUser);
			Team caTeam = null;
			TeamReference teamRef = pbo.getTeamId();
			caTeam = (Team) teamRef.getObject();
			for (WTPrincipalReference ref : allUser) {
				Persistable per = ref.getObject();
                if (per instanceof WTUser) {
                	WTUser user = (WTUser) per;
                	users.add(user);
                }
                if (per instanceof WTGroup) {
                    getUserFromWTGroup((WTGroup) per, users);
                }
			}
			for(WTUser u:users){
				caTeam.addPrincipal(danganyuan, u);
			}
			caTeam = (Team) PersistenceHelper.manager.refresh(caTeam);
			wfprocess.setTeamId(teamRef);
			WfEngineHelper.service.startProcess(wfprocess, processdata, 1);
			SessionServerHelper.manager.setAccessEnforced(access);
			form.setStatus(FormProcessingStatus.SUCCESS);

			FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS,
					null, null, null, "启动工艺文件打印成功！");
			form.addFeedbackMessage(message);
		} catch (Exception e) {
			form.setStatus(FormProcessingStatus.FAILURE);
			FeedbackMessage message;
			try {
				message = new FeedbackMessage(FeedbackType.FAILURE, null, null,
						null, "启动工艺文件打印失败！");
				form.addFeedbackMessage(message);
			} catch (WTException e1) {
				e1.printStackTrace();
			}
			form.setNextAction(FormResultAction.REFRESH_CURRENT_PAGE);
			e.printStackTrace();
		}
		return form;
	}
	/**
     * 循环组并且获取组里面的用户
     * 
     * @param group
     * @param list
     * @throws WTException
     */
    public static void getUserFromWTGroup(WTGroup group, List list) throws WTException {
        if (group == null || list == null) {
            return;
        }
        Enumeration member = group.members();
        while (member.hasMoreElements()) {
            WTPrincipal principal = (WTPrincipal) member.nextElement();
            if (principal instanceof WTUser) {
                list.add((WTUser) principal);
            } else if (principal instanceof WTGroup) {
                getUserFromWTGroup((WTGroup) principal, list);
            }
        }
    }
}
