package ext.casc.workflow.signtrue.zp;

import com.ptc.netmarkets.model.NmException;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.wp.WorkPackage;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.constants.Constants;
import ext.casc.part.SignatureHelper;
import ext.casc.preview.Preview;
import ext.casc.process.ProcessTaskItem;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.project.Role;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WorkItem;

import java.util.*;
import java.util.Map.Entry;

public class ExtJsonDataGenerator {
	public static String genCheJianAndGYZZJsonData(String workItemOid) {
		ReferenceFactory rf = new ReferenceFactory();
		try {
			Map<String, Set<WTUser>> dataMap = getAllContainerUsersData(workItemOid);
			Set<Entry<String, Set<WTUser>>> set = dataMap.entrySet();
			StringBuffer result = new StringBuffer("[");
			for (Entry<String, Set<WTUser>> entry : set) {
				String roleS = entry.getKey();
				if (roleS.equals("工艺组长")) {
					Set<WTUser> userSet = entry.getValue();

					for (WTUser u : userSet) {
						String name = u.getName() + "(" + u.getFullName() + ")";
						result.append("{");
						result.append("id:'" + rf.getReferenceString(u) + "',");
						result.append("text:'" + name + "',");
						result.append("checked:false,");
						result.append("leaf:true");
						result.append("},");
					}
					if (!userSet.isEmpty()) {
						result = result.deleteCharAt(result.length() - 1);
					}
				}
			}
			result.append("]");
			return result.toString();
		} catch (WTRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static String genCheJianAndGyyJsonData(String workItemOid) {
		ReferenceFactory rf = new ReferenceFactory();
		try {
			Map<String, Set<WTUser>> dataMap = getAllContainerUsersData(workItemOid);
			Set<Entry<String, Set<WTUser>>> set = dataMap.entrySet();
			StringBuffer result = new StringBuffer("[");
			for (Entry<String, Set<WTUser>> entry : set) {
				String roleS = entry.getKey();
				if (roleS.contains("工艺员")) {
					Set<WTUser> userSet = entry.getValue();
					result.append("{");
					result.append("text:'" + roleS + "',");
					result.append("children:[");
					for (WTUser u : userSet) {
						String name = u.getName() + "(" + u.getFullName() + ")";
						result.append("{");
						result.append("id:'" + rf.getReferenceString(u) + "',");
						result.append("text:'" + name + "',");
						result.append("checked:false,");
						result.append("leaf:true");
						result.append("},");
					}
					if (!userSet.isEmpty()) {
						result = result.deleteCharAt(result.length() - 1);
					}
					result.append("]");
					result.append("}");
					result.append(",");
				}
			}
			if (!set.isEmpty()) {
				result = result.deleteCharAt(result.length() - 1);
			}
			result.append("]");
			return result.toString();
		} catch (WTRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static String genGyyJsonDataByCurrentUser(String workItemOid) {
		//String user = "";
		ReferenceFactory rf = new ReferenceFactory();
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			//user = wt.session.SessionHelper.manager.getPrincipal().getName();
			//wt.session.SessionHelper.manager.setAdministrator();

			Map<String, Set<WTUser>> dataMap = getGYYContainerUsersData(workItemOid);
			Set<Entry<String, Set<WTUser>>> set = dataMap.entrySet();
			StringBuffer result = new StringBuffer("[");
			for (Entry<String, Set<WTUser>> entry : set) {
				String roleS = entry.getKey();
				if (roleS.contains("工艺员")||roleS.equals(Constants.ROLE_WUZIBUWUZIYUAN)) {
					Set<WTUser> userSet = entry.getValue();
					result.append("{");
					result.append("text:'" + roleS + "',");
					result.append("expand:true,");
					result.append("children:[");
					for (WTUser u : userSet) {
						String name = u.getName() + "(" + u.getFullName() + ")";
						result.append("{");
						result.append("id:'" + rf.getReferenceString(u) + "',");
						result.append("text:'" + name + "',");
						result.append("checked:false,");
						result.append("leaf:true");
						result.append("},");
					}
					if (!userSet.isEmpty()) {
						result = result.deleteCharAt(result.length() - 1);
					}
					result.append("]");
					result.append("}");
					result.append(",");
				}
			}
			if (!set.isEmpty()) {
				result = result.deleteCharAt(result.length() - 1);
			}
			result.append("]");
			return result.toString();
		} catch (WTRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			SessionServerHelper.manager.setAccessEnforced(access);
		}
		return "";
	}

	public static Map<String, Set<WTUser>> getAllContainerUsersData(
			String workItemOid) throws WTException {
		boolean b = SessionServerHelper.manager.setAccessEnforced(false);
		Map<String, Set<WTUser>> dataMap = new TreeMap<String, Set<WTUser>>();
		WTContained contained = null;//getContainedByWorkItem(workItemOid);
		if(workItemOid.contains("ProcessTaskItem")) {
			contained = getContainedByProcessTaskItem(workItemOid);
		} else {
			contained = getContainedByWorkItem(workItemOid);
		}
		ContainerTeamManaged teamManaged = (ContainerTeamManaged) contained;
		ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(teamManaged);
		Vector<Role> vector = containerTeam.getRoles();
		Iterator<Role> iterator = vector.iterator();
		Role role = null;
		Set<WTUser> users = null;
		while (iterator.hasNext()) {
			role = iterator.next();
			ArrayList<WTPrincipalReference> allUser = containerTeam.getAllPrincipalsForTarget(role);
			users = new HashSet<WTUser>();
			for (WTPrincipalReference ref : allUser) {
				Persistable per = ref.getObject();
				if (per instanceof WTUser) {
					WTUser user = (WTUser) per;
					users.add(user);
				}
				if (per instanceof WTGroup) {
					SignatureService.getUserFromWTGroup((WTGroup) per, users);
				}
			}
			dataMap.put(role.getDisplay(Locale.CHINA), users);
		}
		SessionServerHelper.manager.setAccessEnforced(b);
		return dataMap;
	}

	public static Map<String, Set<WTUser>> getGYYContainerUsersData(
			String workItemOid) throws WTException {
		Map<String, Set<WTUser>> dataMap = new TreeMap<String, Set<WTUser>>();
		//WTContained contained = getContainedByWorkItem(workItemOid);

		WTContained contained = null;//getContainedByWorkItem(workItemOid);
		if(workItemOid.contains("ProcessTaskItem")) {
			contained = getContainedByProcessTaskItem(workItemOid);
		} else {
			contained = getContainedByWorkItem(workItemOid);
		}

		String chejian = SignatureService.getCheJianOrXiangMuNumByUser((WTContainer) contained);

		if("".equals(chejian)){

			ContainerTeamManaged teamManaged = (ContainerTeamManaged) contained;
			ContainerTeam containerTeam = ContainerTeamHelper.service
					.getContainerTeam(teamManaged);
			Vector<Role> vector = containerTeam.getRoles();
			Iterator<Role> iterator = vector.iterator();
			Role role = null;
			Set<WTUser> users = null;
			while (iterator.hasNext()) {
				role = iterator.next();
				if (Constants.allChejianToWorkFlowGYYRoleMap.containsValue(role.toString())) {
					ArrayList<WTPrincipalReference> allUser = containerTeam
							.getAllPrincipalsForTarget(role);
					users = new HashSet<WTUser>();
					for (WTPrincipalReference ref : allUser) {
						Persistable per = ref.getObject();
						if (per instanceof WTUser) {
							WTUser user = (WTUser) per;
							users.add(user);
						}
						if (per instanceof WTGroup) {
							SignatureService.getUserFromWTGroup((WTGroup) per,
									users);
						}
					}
					dataMap.put(role.getDisplay(Locale.CHINA), users);
				}
			}

		}else{
			String filterRole = "";
			for(String s:Constants.allChejian){
				if(s.equals(chejian)){
					filterRole = Constants.allChejianToWorkFlowGYYRoleMap.get(s);
					break;
				}
			}

			ContainerTeamManaged teamManaged = (ContainerTeamManaged) contained;
			ContainerTeam containerTeam = ContainerTeamHelper.service
					.getContainerTeam(teamManaged);
			Vector<Role> vector = containerTeam.getRoles();
			Iterator<Role> iterator = vector.iterator();
			Role role = null;
			Set<WTUser> users = null;
			while (iterator.hasNext()) {
				role = iterator.next();
				if (role.toString().equals(filterRole)) {
					ArrayList<WTPrincipalReference> allUser = containerTeam
							.getAllPrincipalsForTarget(role);
					users = new HashSet<WTUser>();
					for (WTPrincipalReference ref : allUser) {
						Persistable per = ref.getObject();
						if (per instanceof WTUser) {
							WTUser user = (WTUser) per;
							users.add(user);
						}
						if (per instanceof WTGroup) {
							SignatureService.getUserFromWTGroup((WTGroup) per,
									users);
						}
					}
					dataMap.put(role.getDisplay(Locale.CHINA), users);
				}
			}
		}

		return dataMap;
	}

	public static WTContained getContainedByProcessTaskItem(String workItemOid) throws WTRuntimeException, WTException {
		ReferenceFactory rf = new ReferenceFactory();
		WTContained contained = null;
		ProcessTaskItem taskItem = (ProcessTaskItem) rf.getReference(workItemOid).getObject();
		contained = taskItem.getContainer();
		return contained;
	}

	public static WTContained getContainedByWorkItem(String workItemOid)
			throws WTRuntimeException, WTException {
		ReferenceFactory rf = new ReferenceFactory();
		WTContained contained = null;
		if(workItemOid.contains("WTPart")){
			WTPart part = (WTPart) rf.getReference(workItemOid).getObject();
			contained = part.getContainer();
			return contained;
		}
		WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
		WfActivity wfAct = (WfActivity) wi.getSource().getObject();
		Object pbo = wfAct.getParentProcess().getContext()
				.getValue("primaryBusinessObject");
		if (pbo instanceof WTChangeOrder2) {
			QueryResult qr = ChangeHelper2.service
					.getChangeablesBefore((WTChangeOrder2) pbo);
			boolean checkedOut = false;
			while (qr.hasMoreElements()) {
				Object o = qr.nextElement();
				if (o instanceof WTDocument) {
					WTDocument document = (WTDocument) o;
					contained = document.getContainer();
				} else if (o instanceof EPMDocument) {
					EPMDocument epmDocument = (EPMDocument) o;
					contained = epmDocument.getContainer();
				} else if (o instanceof WTPart) {
					WTPart part = (WTPart) o;
					contained = part.getContainer();
				} else if (o instanceof ProcessEnvelope) {
					ProcessEnvelope processEnvelope = (ProcessEnvelope) o;
					contained = processEnvelope.getContainer();
				} else if (o instanceof MPMProcessPlan) {
					MPMProcessPlan processPlan = (MPMProcessPlan) o;
					contained = processPlan.getContainer();
				}
				if (o instanceof Workable
						&& WorkInProgressHelper.isCheckedOut((Workable) o)) {
					checkedOut = true;
					break;
				}
			}
			if (checkedOut) {
				throw new NmException("ext.casc.workflow.workflowResource",
						"workflow.workitem.docIsCheckout", null);
			}
		}
		if (pbo instanceof WTDocument) {
			WTDocument document = (WTDocument) pbo;
			contained = document.getContainer();
		} else if (pbo instanceof WTPart) {
			WTPart part = (WTPart) pbo;
			contained = part.getContainer();
		} else if (pbo instanceof EPMDocument) {
			EPMDocument epmDocument = (EPMDocument) pbo;
			contained = epmDocument.getContainer();
		} else if (pbo instanceof ManagedBaseline) {
			ManagedBaseline mbl = (ManagedBaseline) pbo;
			contained = mbl.getContainer();
		} else if (pbo instanceof WorkPackage) {
			WorkPackage wp = (WorkPackage) pbo;
			contained = wp.getContainer();
		} else if (pbo instanceof ProcessEnvelope) {
			ProcessEnvelope processEnvelope = (ProcessEnvelope) pbo;
			contained = processEnvelope.getContainer();
		} else if (pbo instanceof MPMProcessPlan) {
			MPMProcessPlan processPlan = (MPMProcessPlan) pbo;
			contained = processPlan.getContainer();
		} else if (pbo instanceof ChangePackaged) {
			ChangePackaged changePackaged = (ChangePackaged) pbo;
			contained = changePackaged.getContainer();
		}else if (pbo instanceof ChangeRequest) {
			ChangeRequest changePackaged = (ChangeRequest) pbo;
			contained = changePackaged.getContainer();
		}else if (pbo instanceof Preview) {
			Preview preview = (Preview) pbo;
			contained = preview.getContainer();
		}
		return contained;
	}
	public static String genAllReviewRecordJsonData(String workItemOid,
            String objOid) {
		Map<String, String> map = SignatureHelper.getAllReviewRecord(workItemOid, objOid);

		Map<String, String> treemap = new TreeMap<String,String>(map);
		Set<Entry<String,String>> set = treemap.entrySet();
		String totalCount = treemap.size()+"";
		List<RecordData> list = new ArrayList<RecordData>();
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			Entry<String, String> entry = (Entry<String, String>) iterator
					.next();
			RecordData data = new RecordData();
			String value = entry.getValue();
			String[] vals =  value.split(";;;qqq");
			String record = "";
			if(vals.length>0){
				if(vals[0].equals("null")){
					record="";
				}else{
					record=vals[0];
				}
				data.setNumber(record);
			}
			if(vals.length>1){
				if(vals[1].equals("null")){
					record="";
				}else{
					record=vals[1];
				}
				data.setName(record);
			}
			if(vals.length>2){
				if(vals[2].equals("null")){
					record="";
				}else{
					record=vals[2];
				}
				data.setVersion(record);
			}
			if(vals.length>3){
				if(vals[3].equals("null")){
					record="";
				}else{
					record=vals[3];
				}
				data.setState(record);
			}else{
				data.setState("");
			}
			if(vals.length>4){
				if(vals[4].equals("null")){
					record="";
				}else{
					record=vals[4];
				}
				data.setImplement(record);
			}else{
				data.setImplement("");
			}
			if(vals.length>5){
				if(vals[5].equals("null")){
					record="";
				}else{
					record=vals[5];
				}
				data.setCart(record);
			}else{
				data.setCart("");
			}
			list.add(data);

		}
		return "{totalCount:" + totalCount + ",result:" + list + "}";
	}
}