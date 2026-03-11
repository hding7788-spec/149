package ext.casc.workflow;


import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.ptc.extend.ixb.center.MQExpImpProcessEnvelope;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.ases.part.ASESHuiqianSignature;
import ext.casc.change.ChangeHelper;
import ext.casc.constants.Constants;
import ext.casc.ixb.CmExportHandler;
import ext.casc.part.SignatureHelper;
import ext.casc.preview.Preview;
import ext.casc.preview.PreviewUtil;
import ext.sast.center.synch.MQConstants;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.ixb.clientAccess.StandardIXBService;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.project.Role;
import wt.query.ColumnSubSelectExpression;
import wt.query.ConstantExpression;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.query.TableColumn;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.workflow.engine.InvalidDataException;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WfAssignment;
import wt.workflow.work.WfBallot;
import wt.workflow.work.WorkItem;


/**
 * 用于工作流路由投票相关的操作
 * @author Jacky
 * @date 2025年9月2日上午9:47:54
 */
public class WorkflowVoteUtils {
	private static Logger LOGGER = Logger.getLogger(WorkflowVoteUtils.class);
	private static final String MARK_SEMICOLON=";";
	
	/**
	 *  路由：驳回直接返回
	 */
	private static final String STR_REJECTDIRECTRETURN="驳回修改后直接返回";
	
	/**
	 *  路由：通过
	 */
	private static final String STR_PASS="通过";
	
	/**
	 * 审批的角色key
	 */
	private static final String KEY_APPROVEROLE="NEIBUHUIQIANZHE";
	
	/**
	 * 用于存放移除用户id的流程变量key
	 */
	private static final String STR_REMOVEDUSER="removedUser";
	
	/** 
	  * @Description: 流程驳回时，用户要重新审批，将之前移除的用户添加回角色中
	  * @date 2025年9月2日上午11:48:24
	  * @author Jacky
	  * @param self
	  * @param primaryBusinessObject  
	  * @return 
	*/
	public static void addUserToRole(ObjectReference self, WTObject primaryBusinessObject) {
		Persistable persistable = self.getObject();
		
		try {
			if (persistable instanceof WfAssignedActivity) {
				WfAssignedActivity activity = (WfAssignedActivity) persistable;
				WfProcess process = activity.getParentProcess();
				ProcessData processData = process.getContext();
				String removedUser = (String) processData.getValue(STR_REMOVEDUSER);
				LOGGER.debug(MessageFormat.format("addUserToRole.removedUser {0}",removedUser));
				if (StringUtils.isBlank(removedUser)) {
					return;
				}

				if (primaryBusinessObject instanceof WTDocument) {
					WTDocument wtDoc = (WTDocument) primaryBusinessObject;
					List<WTUser> wtUserList = getUserByID(removedUser);
					Team team = TeamHelper.service.getTeam(process);
					Role targetRole = Role.toRole(KEY_APPROVEROLE);
					for (int i = 0; i < wtUserList.size(); i++) {
						WTUser wtuser = wtUserList.get(i);
						team.addPrincipal(targetRole, wtuser);
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}

	}
	
	/** 
	  * @Description: 查询指定工作流活动最新一次的审批记录
	  * @date 2025年9月4日上午11:43:10
	  * @author Jacky
	  * @param activityID
	  * @return  
	  * @return 
	*/
	public static WfAssignment getLatestAssignment(String activityID) {
		WfAssignment wfAssignment=null;
		try {
			QuerySpec qs = new QuerySpec(WfAssignment.class);
			qs.setAdvancedQueryEnabled(true);
			String a0 = qs.getFromClause().getAliasAt(0);
			
			TableColumn tc1 = new TableColumn(a0, "IDA3A4");
			SearchCondition sc2 = new SearchCondition(tc1, "=", new ConstantExpression(new Long(activityID)));
			qs.appendWhere(sc2, new int[] { 0 });
			
			qs.appendAnd();
			
			QuerySpec qs2 = new QuerySpec();
			int classIndex = qs2.appendClassList(WfAssignment.class, false);
			qs2.getFromClause().setAliasPrefix("b");
			String b0=qs2.getFromClause().getAliasAt(classIndex);
			TableColumn dummyColumn = new TableColumn(b0, "TRIPCOUNT");
			SQLFunction currentDate = SQLFunction.newSQLFunction(SQLFunction.MAXIMUM,dummyColumn);
			qs2.appendSelect(currentDate, new int[] { classIndex }, false);
			
			TableColumn tc2 = new TableColumn(b0, "IDA3A4");
			SearchCondition sc22 = new SearchCondition(tc2, "=", new ConstantExpression(new Long(activityID)));
			qs2.appendWhere(sc22, new int[] { 0 });
			
			ColumnSubSelectExpression subSelectExpr = new ColumnSubSelectExpression(qs2);
			TableColumn a0TripCount = new TableColumn(a0, "TRIPCOUNT");
			SearchCondition sc3 = new SearchCondition(a0TripCount, "=", subSelectExpr);
			qs.appendWhere(sc3, new int[] { 0 });
			
			LOGGER.debug(MessageFormat.format("getLatestAssignment.sql {0}", qs.toString()));
			QueryResult qr = PersistenceServerHelper.manager.query(qs);
			while (qr.hasMoreElements()) {
				Object object = qr.nextElement();
				if(object instanceof WfAssignment) {
					wfAssignment=(WfAssignment)object;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wfAssignment;
	}
	
	/** 
	  * @Description: 通过用户ID获取用户
	  * @date 2025年9月4日上午11:47:33
	  * @author Jacky
	  * @param removedUser
	  * @return
	  * @throws WTRuntimeException
	  * @throws WTException  
	  * @return 
	*/
	private static List<WTUser> getUserByID(String removedUser) throws WTRuntimeException, WTException {
		List<WTUser> userList=new ArrayList<WTUser>();
		String[] ids=removedUser.split(";");
		for(int i=0;i<ids.length;i++) {
			String userID=ids[i];
			ReferenceFactory rf=new ReferenceFactory();
			String oid="OR:wt.org.WTUser:"+userID;
			WTUser wtUser=(WTUser)rf.getReference(oid).getObject();
			userList.add(wtUser);
		}
		return userList;
	}

	/** 
	  * @Description: 移除已经评审通过的角色用户，如果路由选择的驳回，重新提交后所有用户都要审批，无需移除之前审批通过的用户
	  * @date 2025年9月2日下午3:57:31
	  * @author Jacky  
	  * @return 
	*/
	public static void removeApprovedUser(ObjectReference self,WTObject primaryBusinessObject,boolean isRejectRoute) {
		//路由驳回的不移除用户
		if(isRejectRoute) {
			return ;
		}
		if(primaryBusinessObject instanceof WTDocument) {
			try {
				Persistable persistable = self.getObject();
				if (persistable instanceof WfAssignedActivity) {
					WfAssignedActivity activity = (WfAssignedActivity) persistable;
					List<WTUser> wtUserList=getApprovedUsers(activity);
					
					saveRemoveUser(activity,wtUserList);
					
					if(CollectionUtils.isEmpty(wtUserList)) {
						return ;
					}
					
					List<WTUser> needApproveUserList = getNeedApproveUsers(activity);
					//移除已经投票通过的用户，不再分配任务给用户，对于第一次“通过”，最后一次“驳回直接返回”的用户不移除。
					WfProcess wfProcess=activity.getParentProcess();
					//printProcessTeam(wfProcess);
					
					
					Team team=TeamHelper.service.getTeam(wfProcess);
					Role targetRole = Role.toRole(KEY_APPROVEROLE);
					for(WTUser wtUser:wtUserList) {
						if(needApproveUserList.contains(wtUser)) {
							continue;
						}
						team.deletePrincipalTarget(targetRole, wtUser);
					}
				}
			} catch (WTException e) {
				e.printStackTrace();
			}

		}
	}

	/** 
	  * @Description: 获取需要审批的用户
	  * 当A、B、C、D、E 5人会签时
	  * 1、第一次路由选择“驳回后直接返回”  A B选择“驳回后直接返回”，其它人选择同意
	  * 2、第二次路由选择“驳回”时，所有人员重新签审
	  * 3、第三次A、B、C选择“驳回后直接返回”，其它人同意，再次提交签审C之前投票通过被排除，所以要获取C情况的用户保留。
	  * @date 2025年9月4日上午11:59:35
	  * @author Jacky
	  * @param activity
	  * @return
	  * @throws WTException  
	  * @return 
	*/
	private static List<WTUser> getNeedApproveUsers(WfAssignedActivity activity) throws WTException {
		List<WTUser> needApproveUserList=new ArrayList<WTUser>();
		String activityID=activity.getPersistInfo().getObjectIdentifier().getId()+"";
		WfAssignment wfAssignment=getLatestAssignment(activityID);
		Enumeration paramEnumeration=wfAssignment.getBallots();
		while (paramEnumeration.hasMoreElements()) {
			WfBallot ballots=(WfBallot)paramEnumeration.nextElement();
			Vector voteVt=ballots.getEventList();
			if(voteVt.contains(STR_REJECTDIRECTRETURN)) {
				//获取投票人
				WTPrincipalReference ref=ballots.getVoter();
				Persistable pUser=ref.getObject();
				WTUser targetUser=(WTUser)pUser;
				needApproveUserList.add(targetUser);
			}
		}
		return needApproveUserList;
	}


	/** 
	  * @Description: 记录移除的用户保存到流程变量中
	  * @date 2025年9月3日下午2:33:20
	  * @author Jacky
	  * @param process
	  * @param wtUserList  
	  * @return 
	*/
	private static void saveRemoveUser(WfAssignedActivity activity, List<WTUser> wtUserList) {
		if(CollectionUtils.isEmpty(wtUserList)) {
			return ;
		}
		try {
			String userIDs=generateUserIDs(wtUserList);
			WfProcess process=activity.getParentProcess();
			ProcessData processData=process.getContext();
			processData.setValue(STR_REMOVEDUSER, userIDs);
			process.setContext(processData);
			process=(WfProcess)PersistenceHelper.manager.save(process);
		} catch (InvalidDataException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	private static String generateUserIDs(List<WTUser> wtUserList) {
		StringBuilder data=new StringBuilder();
		for(WTUser wtUser:wtUserList) {
			data.append(wtUser.getPersistInfo().getObjectIdentifier().getId()).append(MARK_SEMICOLON);
		}
		return data.toString();
	}

	/** 
	  * @Description: 获取投票通过的用户
	  * @date 2025年9月2日下午6:12:50
	  * @author Jacky
	  * @param persistable
	  * @return
	  * @throws WTException  
	  * @return 
	*/
	private static List<WTUser> getApprovedUsers(WfAssignedActivity activity) throws WTException {
		List<WorkItem> workItemList=getActivitygetWorkItems(activity);
	
		List<WTUser> wtUserList=new ArrayList<WTUser>();
		for(int i=0;i<workItemList.size();i++) {
			WorkItem workItem=workItemList.get(i);
			Object assigementObj=workItem.getParentWA().getObject();
			if(assigementObj instanceof WfAssignment) {
				WfAssignment  wfAssignment=(WfAssignment)assigementObj;
				Enumeration paramEnumeration=wfAssignment.getBallots();
				while (paramEnumeration.hasMoreElements()) {
					WfBallot ballots=(WfBallot)paramEnumeration.nextElement();
					Vector voteVt=ballots.getEventList();
					if(voteVt.contains(STR_PASS)) {
						//获取投票人
						WTPrincipalReference ref=ballots.getVoter();
						Persistable pUser=ref.getObject();
						WTUser targetUser=(WTUser)pUser;
						if(pUser instanceof WTUser&&!wtUserList.contains(targetUser)) {
							wtUserList.add(targetUser);
						}
					}
				}
			}
			
		}
		return wtUserList;
	}
	
	/** 
	  * @Description: 获取活动对应的所有workItem
	  * @date 2025年9月2日下午4:28:27
	  * @author Jacky
	  * @param activity
	  * @return  
	  * @return 
	 * @throws WTException 
	*/
	private static List<WorkItem> getActivitygetWorkItems(WfAssignedActivity activity) throws WTException {
		long activityID=activity.getPersistInfo().getObjectIdentifier().getId();
		List<WorkItem> wkItemList=new ArrayList<WorkItem>();
		QuerySpec qs = new QuerySpec(WorkItem.class);
		SearchCondition scNumber = new SearchCondition(WorkItem.class, "source.key.id", SearchCondition.EQUAL,
				activityID);
		qs.appendWhere(scNumber,new int[]{0});
		qs.appendAnd();
		SearchCondition statusSc = new SearchCondition(WorkItem.class, "status", SearchCondition.EQUAL,
				"COMPLETED");
		qs.appendWhere(statusSc,new int[]{0});
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while(qr.hasMoreElements()) {
			WorkItem obj=(WorkItem)qr.nextElement();
			wkItemList.add(obj);
		}
		
		return wkItemList;
	}

	/** 
	  * @Description: 判断当前任务页面是否要显示驳回信息到备注中
	  * @date 2025年9月19日下午4:38:12
	  * @author Liluwen
	  * @param signOid
	  * @return  
	  * @return 
	*/
	public static boolean needAddRejectOpinion(String signOid) {
		List resultList = new ArrayList();
		ReferenceFactory rf = new ReferenceFactory();
		try {
			WorkItem wi= (WorkItem) rf.getReference(signOid).getObject();
			WfActivity wfAct = (WfActivity) wi.getSource().getObject();
			if(Constants.ACTIVITYNAME_GYHQHZ.equals(wfAct.getName())) {
				return true;
			}
		} catch (WTRuntimeException | WTException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/** 
	  * @Description: 获取需要添加到备注框中的驳回内容
	  * @date 2025年9月19日下午3:44:21
	  * @author Liluwen
	  * @param signMap
	  * @param signOid
	  * @return  
	  * @return 
	*/
	public static String getVoteRejectContent(Map signMap, String signOid) {
		if (signMap == null) {
			return "";
		}

		StringBuilder data = new StringBuilder();
		try {
			List resultList = getResultList(signOid);
			if (CollectionUtils.isEmpty(resultList)) {
				return "";
			}
			for (int i = 0; i < resultList.size(); i++) {
				Object object = resultList.get(i);
				String rejectInfo = getRejectInfo(object, signMap);
				if (StringUtils.isBlank(rejectInfo)) {
					continue;
				}

				if (!rejectInfo.contains("不同意")) {
					continue;
				}
				String objectInfo = getPersistableInfo(object);
				data.append(objectInfo).append(MARK_SEMICOLON).append(rejectInfo);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}

		return data.toString();
	}
	
	/** 
	  * @Description: 获取签审对象
	  * @date 2025年9月19日上午11:38:08
	  * @author Liluwen
	  * @param oid
	  * @return
	  * @throws WTRuntimeException
	  * @throws WTException  
	  * @return 
	*/
	private static List getResultList(String oid) throws WTRuntimeException, WTException {
		List resultList = new ArrayList();
		ReferenceFactory rf = new ReferenceFactory();
		WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
		WfActivity wfAct = (WfActivity) wi.getSource().getObject();
		Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
		if (pbo instanceof ProcessEnvelope) {
			ProcessEnvelope pe = (ProcessEnvelope) pbo; // 获取pbo对象
			//add for test
			testPackage(pe);
			List list = ProcessEnvelopeUtil.getAllMembersNoPart(wi, pe);
			resultList.addAll(list);
		} else if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 ecn = (WTChangeOrder2) pbo;
			List list = ChangeHelper.getChangeResultItem(ecn);
			resultList.addAll(list);
			resultList.add(ecn);
		} else if (pbo instanceof MPMProcessPlan) {
			resultList.add(pbo);
		} else if (pbo instanceof WTDocument) {
			resultList.add(pbo);
		} else if (pbo instanceof ChangePackaged) {
			ChangePackaged change = (ChangePackaged) pbo; // 获取pbo对象
			resultList.add(change);
			QueryResult qr = PersistenceHelper.manager.navigate(change, ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
					ChangePackagedResultLink.class, true);
			while (qr.hasMoreElements()) {
				WTObject wtobject = (WTObject) qr.nextElement();
				if (!(wtobject instanceof WTPart)) {
					resultList.add(wtobject);
				}
			}
		} else if (pbo instanceof ChangeRequest) {
			ChangeRequest request = (ChangeRequest) pbo; // 获取pbo对象
			resultList.add(request);
		} else if (pbo instanceof Preview) {
			Preview preview = (Preview) pbo;
			ArrayList<WTObject> list = PreviewUtil.getAllMembers(preview);
			resultList.addAll(list);
		}
		return resultList;
	}

	private static void testPackage(ProcessEnvelope obj) {
		File fileonserver = StandardIXBService.getSaveFileOnServer();
		try {
			CmExportHandler exphnd = new CmExportHandler(fileonserver);
			 new MQExpImpProcessEnvelope(exphnd).exportObject(obj);
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	/** 
	  * @Description: 获取对象的编号名称版本
	  * @date 2025年9月19日上午11:09:40
	  * @author Liluwen
	  * @param object
	  * @return  
	  * @return 
	*/
	private static String getPersistableInfo(Object obj) {
		WTPart part = null;
		WTDocument doc = null;
		EPMDocument epmdoc = null;
		WTChangeOrder2 co = null;
		MPMProcessPlan processPlan = null;
		StringBuilder data=new StringBuilder();
		if (obj instanceof WTPart) {
			part = (WTPart) obj;
			data.append(part.getNumber()).append(MARK_SEMICOLON).append(part.getName()).append(MARK_SEMICOLON);
			data.append(part.getVersionIdentifier().getValue()).append(".").append(part.getIterationIdentifier().getValue());
		} else if (obj instanceof WTDocument) {
			doc = (WTDocument) obj;
			data.append(doc.getNumber()).append(MARK_SEMICOLON).append(doc.getName()).append(MARK_SEMICOLON);
			data.append(doc.getVersionIdentifier().getValue()).append(".").append(doc.getIterationIdentifier().getValue());
		} else if (obj instanceof EPMDocument) {
			epmdoc = (EPMDocument) obj;
			data.append(epmdoc.getNumber()).append(MARK_SEMICOLON).append(epmdoc.getName()).append(MARK_SEMICOLON);
			data.append(epmdoc.getVersionIdentifier().getValue()).append(".").append(epmdoc.getIterationIdentifier().getValue());
		} else if (obj instanceof WTChangeOrder2) {
			co = (WTChangeOrder2) obj;
			data.append(co.getNumber()).append(MARK_SEMICOLON).append(co.getName()).append(MARK_SEMICOLON);
			data.append(co.getVersionIdentifier().getValue()).append(".").append(co.getIterationIdentifier().getValue());
		} else if (obj instanceof MPMProcessPlan) {
			processPlan = (MPMProcessPlan) obj;
			data.append(processPlan.getNumber()).append(MARK_SEMICOLON).append(processPlan.getName()).append(MARK_SEMICOLON);
			data.append(processPlan.getVersionIdentifier().getValue()).append(".").append(processPlan.getIterationIdentifier().getValue());
		}
		return data.toString();
	}

	/** 
	  * @Description: 获取驳回的信息
	  * @date 2025年9月19日上午11:05:40
	  * @author Liluwen
	  * @param obj
	  * @param signMap
	  * @return
	  * @throws WTException  
	  * @return 
	*/
	public static String getRejectInfo(Object obj,
			Map<WTObject, List<ASESHuiqianSignature>> signMap) throws WTException {
		String value = "";
		String value2 = "";
		try {
			value2 = getSignValue(obj, signMap,
					Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG);
			if ("".equals(value2)) {
				value2 = getSignValue(obj, signMap,
						Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN);
			}
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		String tempValue1 = "";
		try {
			tempValue1 = getSignValue(obj, signMap,
					Constants.ACTIVITYNAME_GYHQ);
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		String tempValue2 = "";
		try {
			tempValue2 = getSignValue(obj, signMap,
					Constants.TASK_GONGYYUSHEN);
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		
		if (!value2.equals("")) {
			value = value2;
		}
		if (!tempValue1.equals("")) {
			if ("".equals(value)) {
				value = tempValue1;
			} else {
				value = value + ";;" + tempValue1;
			}

		}
		if (!tempValue2.equals("")) {
			if ("".equals(value)) {
				value = tempValue2;
			} else {
				value = value + ";;" + tempValue2;
			}

		}
		return value;
	}
	
	/** 
	  * @Description: 获取投票内容
	  * @date 2025年9月18日下午9:39:02
	  * @author Liluwen
	  * @param obj
	  * @param signMap
	  * @param activityName
	  * @return
	  * @throws WTException
	  * @throws WTPropertyVetoException  
	  * @return 
	*/
	public static String getSignValue(Object obj,
			Map<WTObject, List<ASESHuiqianSignature>> signMap,
			String activityName) throws WTException, WTPropertyVetoException {
		String value = "";
		ReferenceFactory rf = new ReferenceFactory();
		if (SignatureHelper.isShowSignature(obj)) {
			List<ASESHuiqianSignature> tempSignList = signMap.get(obj);
			String tempValue = "";
			Hashtable ht = new Hashtable();
			for (int i = 0; i < tempSignList.size(); i++) {
				ASESHuiqianSignature tempSign = tempSignList.get(i);
				String tempActOid = tempSign.getActivity();
				WfActivity wfAct = (WfActivity) rf.getReference(tempActOid)
						.getObject();
				if (wfAct.getName().equalsIgnoreCase(activityName)||(activityName.equalsIgnoreCase(Constants.ACTIVITYNAME_GYHQ)&&wfAct.getName().equalsIgnoreCase(MQConstants.ACTIVITY_NAME_KRS))) {
					String conclution = tempSign.getConclusion();
					if (conclution == null) {
						continue;
					}
					String userName = "";
					if (conclution.contains("同意")
							&& !conclution.contains("不同意")) {
						userName = conclution.substring(0,
								conclution.length() - 2);
					} else if (conclution.contains("不同意")) {
						userName = conclution.substring(0,
								conclution.length() - 3);
					} else {// 无需会签
						userName = conclution.substring(0,
								conclution.length() - 4);
					}
					ASESHuiqianSignature tempSign1 = (ASESHuiqianSignature) ht
							.get(userName);
					if (tempSign1 != null) {
						if (tempSign.getCreateTimestamp().after(
								tempSign1.getCreateTimestamp())) {
							ht.put(userName, tempSign);
						}
					} else {
						ht.put(userName, tempSign);
					}
				}
			}
			if (ht.size() > 0) {
				Enumeration enum1 = ht.keys();
				while (enum1.hasMoreElements()) {
					Object obj1 = enum1.nextElement();
					ASESHuiqianSignature tempSign = (ASESHuiqianSignature) ht
							.get(obj1);

					String tempActOid = tempSign.getActivity();
					WfActivity wfAct = (WfActivity) rf.getReference(tempActOid)
							.getObject();
					String name = wfAct.getName();
					String realConclusion = tempSign.getConclusion();

					String conclusion = "";
					if (name.equalsIgnoreCase("内部会签")
							|| name.equalsIgnoreCase("外部会签")
							|| name.equalsIgnoreCase("内部工艺会签")
							|| name.equalsIgnoreCase("用户会签")
							|| name.equalsIgnoreCase(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)
							|| name.equalsIgnoreCase(Constants.ACTIVITYNAME_ZPGYHQ)
							|| name.equalsIgnoreCase("工艺会签")
							|| name.equalsIgnoreCase(Constants.TASK_GONGYYUSHEN)
							|| name.equalsIgnoreCase(MQConstants.ACTIVITY_NAME_KRS)
							|| name.equalsIgnoreCase("标审")
							|| name.equalsIgnoreCase(Constants.ACTIVITYNAME_WZHQ)
							|| name.equalsIgnoreCase(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)) {
						conclusion = realConclusion;

					} else {
						conclusion = realConclusion.substring(
								realConclusion.indexOf(" ") + 1,
								realConclusion.length());
					}
					conclusion = conclusion.replace(", ", "");
					conclusion = conclusion.replace(",", "");
					String sig = tempSign.getSignature();
					if (sig == null) {
						sig = "";
					}
					String opinion = tempSign.getOpinion();
					if (opinion == null) {
						opinion = "";
					}
					if (tempValue.equals("")) {
						if (sig == null || "".equals(sig)) {
							tempValue = conclusion + ";" + opinion;
						} else {
							if (activityName.equals("外部会签")
									|| activityName.equals("外部工艺会签")) {
								tempValue = conclusion + ";" + sig + ";"
										+ opinion;
							} else {
								tempValue = conclusion + ";" + opinion;
							}
						}
					} else {
						if (sig == null || "".equals(sig)) {
							tempValue = tempValue + "/" + conclusion + ";"
									+ opinion;
						} else {
							if (activityName.equals("外部会签")
									|| activityName.equals("外部工艺会签")) {
								tempValue = tempValue + "/" + conclusion + ";"
										+ sig + ";" + opinion;
							} else {
								tempValue = tempValue + "/" + conclusion + ";"
										+ opinion;
							}
						}
					}
					value = tempValue;
				}
			}

		}
		return value;
	}
	
	/** 
	  * @Description: 获取任务的状态
	  * @date 2025年10月21日上午9:35:35
	  * @author Liluwen
	  * @param signOid
	  * @return  
	  * @return 
	*/
	public static String getWorkItemStatus(String signOid) {
		ReferenceFactory rf = new ReferenceFactory();
		String status="";
		try {
			WorkItem wi= (WorkItem) rf.getReference(signOid).getObject();
			status=wi.getStatus().toString();
			LOGGER.debug(MessageFormat.format("getWorkItemStatus {0},status {1}", signOid,status));
		} catch (WTRuntimeException | WTException e) {
			e.printStackTrace();
		}
		
		
		return status;
	}
}
