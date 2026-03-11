package ext.csc.utilities;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import com.ptc.core.components.rendering.guicomponents.DateDisplayComponent;
import com.ptc.core.components.rendering.guicomponents.TextDisplayComponent;
import com.ptc.netmarkets.workflow.NmWorkflowHelper;
import com.ptc.windchill.enterprise.workflow.WorkflowDataUtility;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.team.Team;
import wt.util.WTException;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfNode;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.engine.WfVotingEventAudit;
import wt.workflow.work.StandardWorkflowService;
import wt.workflow.work.WorkItem;

public class CSCProcess {
	public static String PROCESS_NAME = "procName";
	public static String PROCESS_CREATOR = "procCreator";
	public static String PROCESS_DATE = "procDate";
	public static String WORK_NAME = "workName";
	public static String WORK_ASSIGNEE = "workAssignee";
	public static String WORK_ROLE = "workRole";
	public static String WORK_VOTE = "workVote";
	public static String WORK_COMMENTS = "workComments";
	public static String WORK_DEADLINE = "workDeadline";
	public static String WORK_COMPLETEDDATE = "workCompletedDate";
	
	public static WfProcess getProcessByID(String processId) {
		WfProcess process = null;
		ReferenceFactory rf = new ReferenceFactory();
		try {
			process = (WfProcess) (rf.getReference(processId).getObject());
		} catch (Exception e) {
		}
		return process;
	}

	public static WfProcess getProcessByName(String processName) {
		WfProcess process = null;
		try {
			QuerySpec qs = new QuerySpec(WfProcess.class);
			SearchCondition critical = new SearchCondition(WfProcess.class,
					WfProcess.NAME, SearchCondition.EQUAL, processName);
			qs.appendSearchCondition(critical);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements())
				process = (WfProcess) qr.nextElement();
		} catch (Exception e) {
			process = null;
		}
		return process;
	}

	/**
	 * 
	 * @param wfobject
	 * @return
	 */
	public static WfProcess getProcessByObject(Object wfobject) {
		WfProcess process = null;
		if (wfobject == null)
			return process;
		try {
			if (wfobject instanceof WfProcess) {
				return (WfProcess) wfobject;
			} else if (wfobject instanceof WfActivity) {
				WfActivity activity = (WfActivity) wfobject;
				process = activity.getParentProcess();
			} else if (wfobject instanceof WfNode) {
				process = (WfProcess) ((WfNode) wfobject).getParentProcessRef()
						.getObject();
			}
		} catch (Exception e) {
			e.printStackTrace();
			process = null;
		}

		return process;
	}

	public static Enumeration getReleatedProcess(Persistable object,
			WfState state) {
		Enumeration process = null;
		try {
			process = WfEngineHelper.service.getAssociatedProcesses(object,
					state);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return process;
	}

	/**
	 * This method is used to get principal by role in the process
	 * 
	 * @param activity
	 * @param role
	 * @return
	 */
	public static ArrayList getParticipatorByRole(Object activity, Role role) {
		ArrayList list = new ArrayList();
		try {
			WfProcess process = getProcessByObject(activity);
			return getParticipatorByRole(process, role);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}

	public static ArrayList getParticipatorByRole(WfProcess process, Role role) {
		ArrayList list = new ArrayList();
		try {
			Team team = (Team) process.getTeamId().getObject();
			Enumeration principals = team.getPrincipalTarget(role);
			while (principals.hasMoreElements()) {
				list.add((WTPrincipalReference) principals.nextElement());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}

	public static String getEmailStringOfAllMembers(Object activity) {
		String mailList = "";
		WfProcess process = getProcessByObject(activity);
		Vector roles = new Vector();
		try {
			roles = ((Team) (process.getTeamId().getObject())).getRoles();
		} catch (WTException e) {
			e.printStackTrace();
		}
		if (roles == null)
			return "";
		for (int i = 0; i < roles.size(); i++) {
			Role role = (Role) roles.get(i);
			String mail = getEmailStringByRole(activity, role);
			if (mail != null && !"".equals(mail))
				mailList = mailList + mail + ";";
		}

		if (mailList.endsWith(";")) {
			mailList = mailList.substring(0, mailList.length() - 1);
		}
		return mailList;
	}

	public static String getEmailStringByRole(Object activity, String role) {
		return getEmailStringByRole(activity, Role.toRole(role));
	}

	public static String getEmailStringByRole(Object activity, Role role) {
		String mailList = "";
		ArrayList userList = getParticipatorByRole(activity, role);
		if (userList != null && userList.size() > 0) {
			for (int i = 0; i < userList.size(); i++) {
				WTPrincipalReference wtp = (WTPrincipalReference) userList
						.get(i);
				String mail = "";
				if (wtp != null) {
					try {
						WTPrincipal principal = wtp.getPrincipal();
						if (principal instanceof WTUser) {
							WTUser user = (WTUser) principal;
							mail = user.getEMail();
						} else if (principal instanceof WTGroup) {
							WTGroup group = (WTGroup) principal;
							Enumeration members = group.members();
							while (members.hasMoreElements()) {
								WTPrincipal groupPrincipal = (WTPrincipal) members
										.nextElement();
								if (groupPrincipal instanceof WTUser) {
									WTUser oUser = (WTUser) groupPrincipal;
									String mail1 = oUser.getEMail();
									if (mail1 != null && !"".equals(mail1))
										mailList = mailList + mail + ";";
								}
							}
						}
					} catch (WTException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (mail != null && !"".equals(mail))
						mailList = mailList + mail + ";";
				}
			}
		}
		if (mailList.endsWith(";")) {
			mailList = mailList.substring(0, mailList.length() - 1);
		}
		return mailList;
	}

	public static void updateActivityVariableValue(WfActivity activity,
			String variable, String value) {
		try {
			ProcessData pd = activity.getContext();
			pd.setValue(variable, value);
			PersistenceHelper.manager.save(activity);
		} catch (Exception e) {

		}
	}

	public static Object getActivityVariableValue(WfActivity activity,
			String variable) {
		try {
			ProcessData pd = activity.getContext();
			return pd.getValue(variable);
		} catch (Exception e) {
			return null;
		}
	}

	public static Object getProcessVariableValue(WfProcess process,
			String variable) {
		try {
			return process.getContext().getValue(variable);
		} catch (Exception e) {
			return null;
		}
	}

	public static Object getProcessVariableValue(String processID,
			String variable) {
		try {
			WfProcess process = getProcessByID(processID);
			return process.getContext().getValue(variable);
		} catch (Exception e) {
			return null;
		}
	}

	public static void updateProcessVariableValue(WfProcess process,
			String variable, String value) {
		try {
			ProcessData pd = process.getContext();
			pd.setValue(variable, value);
			PersistenceHelper.manager.save(process);
		} catch (Exception e) {

		}
	}

	public static String reAssignWorkItem(WorkItem workitem, WTUser user,String comments) {
		String flag = "false";
		if(user == null || workitem == null)
			return "false";
		try {
			wt.session.SessionHelper.manager.setAdministrator();
			workitem.setReassigned(true);
			StandardWorkflowService service = StandardWorkflowService.newStandardWorkflowService();
			service.delegate(workitem, user, true,comments);
			flag = "true";
		} catch (Exception e) {
			e.printStackTrace();
			flag = "false";
		}
		return flag;
	}
	
	/**
	 * This method is for getting all the routing history of the specified process, including 
	 * Process Name, Process Creator, Process Date, Activity Name, Activity Assignee, Activity Role, 
	 * Activity Vote Result, Activity Comments, Activity Deadline and Activity Compelete Date.
	 * @param proc
	 * @return An arraylist contains a structured hashmap. The hasmap's keys are 
	 * CSCProcess.PROCESS_NAME: 		Process Name
	 * CSCProcess.PROCESS_CREATOR:		Process Creator
	 * CSCProcess.PROCESS_DATE:			Process Date
	 * CSCProcess.WORK_NAME:			Activity Name
	 * CSCProcess.WORK_ASSIGNEE:		Activity Assignee
	 * CSCProcess.WORK_ROLE:			Activity Role
	 * CSCProcess.WORK_VOTE:			Activity Vote
	 * CSCProcess.WORK_COMMENTS:		Activity Comments
	 * CSCProcess.WORK_DEADLINE:		Activity Deadline
	 * CSCProcess.WORK_COMPLETEDDATE:	Activity Compelete Date
	 * @throws WTException
	 */
	
	public static ArrayList getProcessRoutingHistory(WfProcess proc) throws WTException{
		WorkflowDataUtility wdu = new WorkflowDataUtility();
		
		ArrayList aProcess = new ArrayList();
		QueryResult qsVoteEvent = NmWorkflowHelper.service.getVotingEventsForProcess(proc);		//Get all completed VotingEvents

		while(qsVoteEvent.hasMoreElements()){			
			WfVotingEventAudit voteEvent = (WfVotingEventAudit)qsVoteEvent.nextElement();
			//--- Process Information ---
			String strProcessName = ((TextDisplayComponent)wdu.getDataValue("procName", proc, null)).getValue();
			String strProcessCreator = proc.getCreator().getDisplayName();
			String strProcessInitialDate = proc.getCreateTimestamp().toLocaleString();
			
			//--- WorkItem Information ---
			String strWorkName = (String)wdu.getDataValue("workName", voteEvent, null);
			String strWorkAssignee = ((TextDisplayComponent)wdu.getDataValue("workAssignee", voteEvent, null)).getValue();
			String strWorkRole = ((TextDisplayComponent)wdu.getDataValue("workRole", voteEvent, null)).getValue();
			String strWorkVote = ((TextDisplayComponent)wdu.getDataValue("workVote", voteEvent, null)).getValue();
			if(strWorkVote.equals("&nbsp;")) strWorkVote = "";

			DateDisplayComponent ddcDeadline = (DateDisplayComponent)wdu.getDataValue("workDeadline", voteEvent, null);
			String strDeadline = "";
			if (ddcDeadline == null){
				strDeadline = "";
			}else{
				strDeadline = ddcDeadline.getDisplayValue();
			}
			
			DateDisplayComponent ddcCompletedDate = (DateDisplayComponent)wdu.getDataValue("workCompletedDate", voteEvent, null);
			String strCompletedDate = "";
			if (ddcCompletedDate == null){
				strCompletedDate = "";
			}else{
				strCompletedDate = ddcCompletedDate.getDisplayValue();
			}

			//--- VotingEvent Information --
			String strWorkComments = ((TextDisplayComponent)wdu.getDataValue("workComments", voteEvent, null)).getFormattedValue();
			
			//--- Envelop the Data into a HashMap ----
			HashMap hmInnerWorkItem = new HashMap();
			
			hmInnerWorkItem.put(PROCESS_NAME, strProcessName);
			hmInnerWorkItem.put(PROCESS_CREATOR, strProcessCreator);
			hmInnerWorkItem.put(PROCESS_DATE, strProcessInitialDate);
			
			hmInnerWorkItem.put(WORK_NAME, strWorkName);
			hmInnerWorkItem.put(WORK_ASSIGNEE, strWorkAssignee);
			hmInnerWorkItem.put(WORK_ROLE, strWorkRole);
			hmInnerWorkItem.put(WORK_VOTE, strWorkVote);
			hmInnerWorkItem.put(WORK_COMMENTS, strWorkComments);
			hmInnerWorkItem.put(WORK_DEADLINE, strDeadline);
			hmInnerWorkItem.put(WORK_COMPLETEDDATE, strCompletedDate);

			aProcess.add(hmInnerWorkItem);
		}
		return aProcess;
	}
}
