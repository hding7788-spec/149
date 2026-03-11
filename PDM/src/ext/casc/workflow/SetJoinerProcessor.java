package ext.casc.workflow;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.TeamReference;
import wt.util.WTException;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfBlock;
import wt.workflow.engine.WfContainer;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

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

import ext.casc.constants.Constants;
import ext.casc.process.util.ProcessUtil;
import ext.casc.util.CSCPrincipal;

public class SetJoinerProcessor extends DefaultObjectFormProcessor {
	@Override
	public FormResult doOperation(NmCommandBean cb, List<ObjectBean> arg1)
			throws WTException {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		List oids = cb.getSelectedOidForPopup();
		Map join = cb.getRequest().getParameterMap();
		WTPrincipal xiaoduizhe = null;
		WTPrincipal dayinzhe=null;
		WTPrincipal shenhezhe=null;
		WTPrincipal neibuhuiqian=null;
		WTPrincipal waibuhuiqian=null;
		WTPrincipal biaoshezhe=null;
		WTPrincipal pizhunzhe=null;
		WTPrincipal gongshidingeryuan=null;
		WTPrincipal gongshishenheyuan=null;
		String[] xiaodui = (String[]) join.get("角色.校对者");
		if (xiaodui!=null&&!"".equals(xiaodui)&&!"null".equals(xiaodui)) {
		 xiaoduizhe = CSCPrincipal.getUserByNumber(xiaodui[0]) ;
		}
		String[] dayin = (String[]) join.get("角色.打印者");
		if (dayin!=null&&!"".equals(dayin)&&!"null".equals(dayin)) {
			 dayinzhe = CSCPrincipal.getUserByNumber(dayin[0]) ;
		}
		String[] shenhe = (String[]) join.get("角色.审核者");
		if (shenhe!=null&&!"".equals(shenhe)&&!"null".equals(shenhe)) {
			 shenhezhe = CSCPrincipal.getUserByNumber(shenhe[0] ) ;
		}
		String[] neibuhuiqian1 = (String[]) join.get("角色.内部会签者");
		if (neibuhuiqian1!=null&&!"".equals(neibuhuiqian1)&&!"null".equals(neibuhuiqian1)) {
			 neibuhuiqian =CSCPrincipal.getUserByNumber( neibuhuiqian1[0])  ;
		}
		String[] waibuhuiqian1 = (String[]) join.get("角色.外部会签者");
		if (waibuhuiqian1!=null&&!"".equals(waibuhuiqian1)&&!"null".equals(waibuhuiqian1)) {
			 waibuhuiqian = CSCPrincipal.getUserByNumber(waibuhuiqian1[0] ) ;
		}
		String[] biaoshen = (String[]) join.get("角色.标审者");
		if (biaoshen!=null&&!"".equals(biaoshen)&&!"null".equals(biaoshen)) {
			 biaoshezhe = CSCPrincipal.getUserByNumber(biaoshen[0] ) ;
		}
		String[] pizhun = (String[]) join.get("角色.批准者");
		if (pizhun!=null&&!"".equals(pizhun)&&!"null".equals(pizhun)) {
			 pizhunzhe = CSCPrincipal.getUserByNumber(pizhun[0]) ;
		}
		String[] gongshidinger = (String[]) join.get("角色.工时定额员");
		if (gongshidinger!=null&&!"".equals(gongshidinger)&&!"null".equals(gongshidinger)) {
			 gongshidingeryuan = CSCPrincipal.getUserByNumber( gongshidinger[0]) ;
		}
		String[] gongshishenheer = (String[]) join.get("角色.工时审核员");
		if (gongshishenheer!=null&&!"".equals(gongshishenheer)&&!"null".equals(gongshishenheer)) {
			gongshishenheyuan = CSCPrincipal.getUserByNumber( gongshishenheer[0]) ;
		}

		ArrayList<String> list= new ArrayList<String>();
		list.add("BIAOSHENZHE");
		list.add("DAYINZHE");
		list.add("NEIBUHUIQIANZHE");
		list.add("WAIBUHUIQIANZHE");
		list.add("JIAODUIZHE");
		list.add("APPROVER");
		list.add("GONGSHIDINGEYUAN");
		list.add("SHENHEZHE");
		list.add("GONGSHISHENHEYUAN");


		for (int i = 0; i < oids.size(); i++) {

			Object value = (Object) oids.get(i);
			NmOid oid1 = (NmOid) value;
			WorkItem workItem = (WorkItem) oid1.getRefObject();
			Vector rolesDefinedVec = new Vector();
			WfActivity wfAct = (WfActivity) workItem.getSource().getObject();
			String wfName = "";
			List<Role> roleList = new ArrayList<Role>();
			WfContainer wfcont = (WfContainer) wfAct.getParentProcessRef()
					.getObject();
			WfProcess process = null;
			if (wfcont instanceof WfBlock) {
				WfBlock wfBlock = (WfBlock) wfcont;
				process = wfBlock.getParentProcess();
			} else {
				process = (WfProcess) wfcont;
			}
			Team team = (Team) process.getTeamId().getObject();
			HashMap rolePrincipalListMap = TeamHelper.service
					.findAllParticipantsByRole(team);


			for (String s:list) {
				Role  role = Role.toRole(s);
                List tempUserList = (List) rolePrincipalListMap.get(role);
                    for (int j = 0; tempUserList != null && j < tempUserList.size(); j++) {
                        WTUser user1 = (WTUser) ((WTPrincipalReference) tempUserList.get(j))
                                .getObject();
                        team.deletePrincipalTarget(role, user1);
                }

			}

			if (process.getName().replaceAll(" ", "").startsWith("五级工艺文件签审流程")) {
				if (biaoshezhe!=null&&!"".equals(biaoshezhe)&&!"null".equals(biaoshezhe)) {
					team.addPrincipal(Role.toRole("BIAOSHENZHE"), biaoshezhe);
				}
				if (dayinzhe!=null&&!"".equals(dayinzhe)&&!"null".equals(dayinzhe)) {
					team.addPrincipal(Role.toRole("DAYINZHE"), dayinzhe);
				}
				if (neibuhuiqian!=null&&!"".equals(neibuhuiqian)&&!"null".equals(neibuhuiqian)) {
					team.addPrincipal(Role.toRole("NEIBUHUIQIANZHE"), neibuhuiqian);
				}
				if (waibuhuiqian!=null&&!"".equals(waibuhuiqian)&&!"null".equals(waibuhuiqian)) {

					team.addPrincipal(Role.toRole("WAIBUHUIQIANZHE"), waibuhuiqian);
				}
				if (xiaoduizhe!=null&&!"".equals(xiaoduizhe)&&!"null".equals(xiaoduizhe)) {
					team.addPrincipal(Role.toRole("JIAODUIZHE"), xiaoduizhe);
				}
				if (pizhunzhe!=null&&!"".equals(pizhunzhe)&&!"null".equals(pizhunzhe)) {

					team.addPrincipal(Role.toRole("APPROVER"), pizhunzhe);
				}
				if (gongshidingeryuan!=null&&!"".equals(gongshidingeryuan)&&!"null".equals(gongshidingeryuan)) {

					team.addPrincipal(Role.toRole("GONGSHIDINGEYUAN"),gongshidingeryuan);
				}
				if (shenhezhe!=null&&!"".equals(shenhezhe)&&!"null".equals(shenhezhe)) {

					team.addPrincipal(Role.toRole("SHENHEZHE"), shenhezhe);
				}
				if (gongshishenheyuan!=null&&!"".equals(gongshishenheyuan)&&!"null".equals(gongshishenheyuan)) {

					team.addPrincipal(Role.toRole("GONGSHISHENHEYUAN"), gongshishenheyuan);
				}
				team = (Team) PersistenceHelper.manager.refresh(team);
				team = (Team) PersistenceHelper.manager.save(team);
			}else if(process.getName().replaceAll(" ", "").startsWith("三级工艺文件签审流程")){
				if (xiaoduizhe!=null&&!"".equals(xiaoduizhe)&&!"null".equals(xiaoduizhe)) {

					team.addPrincipal(Role.toRole("JIAODUIZHE"), xiaoduizhe);
				}
				if (pizhunzhe!=null&&!"".equals(pizhunzhe)&&!"null".equals(pizhunzhe)) {

					team.addPrincipal(Role.toRole("APPROVER"), pizhunzhe);
				}
				if (dayinzhe!=null&&!"".equals(dayinzhe)&&!"null".equals(dayinzhe)) {

					team.addPrincipal(Role.toRole("DAYINZHE"), dayinzhe);
				}
				if (gongshidingeryuan!=null&&!"".equals(gongshidingeryuan)&&!"null".equals(gongshidingeryuan)) {

					team.addPrincipal(Role.toRole("GONGSHIDINGEYUAN"), gongshidingeryuan);
				}
				if (gongshishenheyuan!=null&&!"".equals(gongshishenheyuan)&&!"null".equals(gongshishenheyuan)) {

					team.addPrincipal(Role.toRole("GONGSHISHENHEYUAN"), gongshishenheyuan);
				}
				team = (Team) PersistenceHelper.manager.refresh(team);
				team = (Team) PersistenceHelper.manager.save(team);
			} else if(process.getName().replaceAll(" ", "").startsWith("工艺更改单签审流程")
					|| process.getName().replaceAll(" ", "").startsWith("工时定额签审流程")) {
				if (gongshishenheyuan!=null&&!"".equals(gongshishenheyuan)&&!"null".equals(gongshishenheyuan)) {
					team.addPrincipal(Role.toRole("GONGSHISHENHEYUAN"), gongshishenheyuan);
				}
				team = (Team) PersistenceHelper.manager.refresh(team);
				team = (Team) PersistenceHelper.manager.save(team);
			}

		}

		SessionServerHelper.manager.setAccessEnforced(enforce);

		return null;

	}
}