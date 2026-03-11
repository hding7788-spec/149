package com.glaway.mpm.report.helper;

import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.ArrayExpression;
import wt.query.AttributeRange;
import wt.query.ClassAttribute;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.util.WTException;
import wt.vc.VersionControlException;
import wt.workflow.definer.WfProcessTemplate;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import com.glaway.mpm.util.WTPartUtil;

public class CraftworkPSBuilderHelper {


	private static List<WorkItem> list = new ArrayList<WorkItem>();

	/**
	 * 获得table显示信息
	 * @author
	 * @date 2013-7-29
	 * @param listWorkItems  存放workItem
	 * @return
	 */
	public static List getDisPlayMessage(List<WorkItem> listWorkItems){
		List messageList = new ArrayList();
		List groupList = getAllWTGroup();
		WTPrincipal oldPrincipal= null;
		String groupName = "";
		for(WorkItem workItem:listWorkItems){
			Map map = new HashMap();
			String identifier = workItem.getDisplayIdentifier().toString();
			Object obj = workItem.getPrimaryBusinessObject().getObject();
			WTPart part = null;
			String completeBy = "";
			String completeTime = "";
			String partNumber = "";
			String partName = "";
			String productName = "";
			StringBuffer parentNumber = new StringBuffer();
			if(obj instanceof WTPart){
				part = (WTPart)obj;
			}
			//获得任务相关信息
			completeBy = workItem.getCompletedBy();
			completeTime = workItem.getModifyTimestamp().toString();
			//获得审核者所在的组
			WTPrincipal principal = (WTPrincipal) getPrincipal(completeBy);
			if(principal != null && !principal.equals(oldPrincipal) && groupList != null){
				for(int i = 0;i < groupList.size(); i ++){
					WTGroup group = (WTGroup) groupList.get(i);
					try {
						if(group.isMember(principal)){
							groupName = group.getName() + "," + groupName;
							oldPrincipal = principal;
						}
					} catch (WTException e) {
						groupName = "";
						e.printStackTrace();
					}
				}//end for
			}//end if
			if(groupName.endsWith(",")){
				groupName = groupName.substring(0, groupName.length()-1);
			}
			partNumber = part.getNumber();
			partName = part.getName();
			List<WTPart> parentList;
			try {
				parentList  = WTPartUtil.getParentPart(part);
			} catch (WTException e) {
				parentList = null;
				e.printStackTrace();
			}
			for(WTPart parentPart:parentList){
				parentNumber.append(parentPart.getNumber());
			}
			productName = part.getContainer().getName();
			map.put("proNumber", productName);
			map.put("wholeNumber", parentNumber.toString());
			map.put("hardwareNumber", partNumber);
			map.put("hardwarePartName", partName);
			map.put("signaturer", completeBy);
			map.put("group", groupName);
			map.put("signatureTime", completeTime);
			messageList.add(map);
		}
		return messageList;
	}


/**
 * 获得签审的零件的的分类个数
 * @author qianlong
 * @date 2013-7-30
 * @param listWorkItems
 * @return
 */
	public static String countSingaturePart(List<WorkItem> listWorkItems){
		System.out.println("listWorkItems-----" + listWorkItems);
		StringBuffer countStr = new StringBuffer();
		int wholePart = 0;
		int hardwarePart = 0;
		int srPart = 0;
		int formalPart = 0;
		String wholeregEx="^AL1.*|^AL2.*|^AL3.*|^AL4.*|^ALK1.*|^ALK2.*|^ALK3.*|^ALK4.*";
		String hardwareregEx="^AL5.*|^AL6.*|^AL7.*|^AL8.*|^ALK5.*|^ALK6.*|^ALK7.*|^ALK8.*";
		Pattern wholep =Pattern.compile(wholeregEx);
		Pattern hardwarep =Pattern.compile(hardwareregEx);
		Map countMap = new HashMap();
		for(WorkItem workItem:listWorkItems){
			Object obj = workItem.getPrimaryBusinessObject().getObject();
			if(obj instanceof WTPart){
				WTPart part = (WTPart)obj;
				String number = part.getNumber();
				System.out.println("number----" + number);
				if(!countMap.containsKey(part)){
					countMap.put(part, workItem);
					Matcher wholem = wholep.matcher(number);
					Matcher hardwarem = hardwarep.matcher(number);
					boolean wholemR=wholem.find();
					boolean hardwareR=hardwarem.find();
					if(wholemR){
						wholePart ++;
					}else if(hardwareR){
						hardwarePart ++;
					}
					if(number.startsWith("ALK")){
						srPart ++;
					}else if(number.startsWith("AL")){
						formalPart ++;
					}

				}else{
					WorkItem item = (WorkItem) countMap.get(part);
					Timestamp oneTime = item.getModifyTimestamp();
					Timestamp twoTime = workItem.getModifyTimestamp();
					if(oneTime.before(twoTime)){
						countMap.remove(part);
						countMap.put(part, workItem);
					}
				}
			}
		}
		countStr.append(String.valueOf(wholePart));
		countStr.append(",");
		countStr.append(String.valueOf(hardwarePart));
		countStr.append(",");
		countStr.append(String.valueOf(srPart));
		countStr.append(",");
		countStr.append(String.valueOf(formalPart));
		return countStr.toString();

	}

	/**
	 * 查询用户的WorkItem
	 *
	 * @author
	 * @date 2012-6-18
	 * @modify xzheng
	 * @param user
	 *            需要查询的用户
	 * @param nameList
	 *            流程模板的名称
	 * @param status
	 *            状态列表。如果为空，则查询所有状态
	 * @return
	 * @throws WTException
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public static List<WorkItem> listWorkItems(WTUser user,
			String startTime, String endTime,String group) {
		list.clear();
		group = group == null?"":group.trim();
		startTime = startTime == null?"":startTime.trim();
		endTime = endTime == null?"":endTime.trim();
		if(user == null && "".equals(startTime) && "".equals(endTime) && "".equals(group)){
			return list;
		}
		Map countMap = new HashMap();
		List<WTUser> userList = new ArrayList<WTUser>();
		String[] nameList = {"工艺派工流程"};
		group = group.replace("；", ";");
		String[] groupArr = group.split(";");
		for(int i =0; i< groupArr.length; i ++){
			String groupName = groupArr[i];
			groupName = groupName == null?"":groupName.trim();
			if(!"".equals(groupName)){
			try {
				WTGroup wtgroup = OrganizationServicesHelper.manager.getGroup(groupName);
				if(wtgroup != null){
					Enumeration enu = wtgroup.members();
					while(enu.hasMoreElements()){
						Object object = enu.nextElement();
						if(object instanceof WTPrincipal || object instanceof WTUser){
							WTUser wtuser = (WTUser)object;
//							System.out.println("wtuser.getName()+++++" + wtuser.getName());
							userList.add(wtuser);
						}
					}
				}
			} catch (WTException e) {
				e.printStackTrace();
				continue;
			}
			}
		}
		try {
			QuerySpec qs = new QuerySpec();
			int wfpIdx = qs.appendClassList(WfProcess.class, false);
			int wfaIdx = qs.appendClassList(WfAssignedActivity.class, false);
			int wfiIdx = qs.appendClassList(WorkItem.class, true);
			qs.setAdvancedQueryEnabled(true);
			if(user != null){
				userList.add(user);
			}
//			System.out.println("userList.size()-----" + userList.size());
			if(userList.size() > 0){
				qs.appendOpenParen();
				WTUser use = userList.get(0);
				qs.appendWhere(new SearchCondition(WorkItem.class,
						"ownership.owner.key.id", SearchCondition.EQUAL, use
								.getPersistInfo().getObjectIdentifier().getId()),
						new int[] { wfiIdx });
				for(int i = 1;i < userList.size();i ++){
					WTUser wtuser = userList.get(i);
					qs.appendOr();
					qs.appendWhere(new SearchCondition(WorkItem.class,
							"ownership.owner.key.id", SearchCondition.EQUAL, wtuser
									.getPersistInfo().getObjectIdentifier().getId()),
							new int[] { wfiIdx });
					}
				qs.appendCloseParen();
			}
			if (qs.getWhere() != null) {
				qs.appendAnd();
			}
			qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id",
					WfAssignedActivity.class,
					"thePersistInfo.theObjectIdentifier.id"), wfiIdx, wfaIdx);
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WfAssignedActivity.class,
					"parentProcessRef.key.id", WfProcess.class,
					"thePersistInfo.theObjectIdentifier.id"), wfaIdx, wfpIdx);
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(new ClassAttribute(WfProcess.class,
					"template.key.id"), SearchCondition.IN,
					new SubSelectExpression(getWfProcessQuerySpec(nameList))),
					new int[] { wfpIdx });
			if (startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00.0");
				startTime = dateFormat.format(new Date(startTime));
				SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd 23:59:59.9");
				endTime = dateFormat1.format(new Date(endTime));
				if (qs.getWhere() != null) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(WorkItem.class, WorkItem.MODIFY_TIMESTAMP, true, new AttributeRange(
						startTime, endTime)),new int[] { wfiIdx });
			}
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			System.out.println("qr.size-----" + qr.size());
			while (qr.hasMoreElements()) {
				Persistable[] ps = (Persistable[]) qr.nextElement();
				WorkItem workItem = (WorkItem) ps[0];
				String completeBy = workItem.getCompletedBy();
				if(completeBy != null && !"".equals(completeBy)){
					Object obj = workItem.getPrimaryBusinessObject().getObject();
					String identifier = workItem.getDisplayIdentifier().toString();
					identifier = identifier.trim();
					int idLength = identifier.length();
					String workItemName = "";
					if(idLength > 6){
						workItemName = identifier.substring(idLength-6,idLength);
					}else{
						continue;
					}
					if("工艺主师会签".equals(workItemName)){
						if(obj instanceof WTPart){
							WTPart part = (WTPart)obj;
							if(!countMap.containsKey(part)){
								countMap.put(part, workItem);
								list.add(workItem);
							}else{
								WorkItem item = (WorkItem) countMap.get(part);
								Timestamp oneTime = item.getModifyTimestamp();
								Timestamp twoTime = workItem.getModifyTimestamp();
								if(oneTime.before(twoTime)){
									list.remove(item);
									list.add(workItem);
									}
								}
							}
					}//end if
				}//end if
			}
		} catch (QueryException e) {
			System.out.println("====new qs wrong====");
			e.printStackTrace();
			return list;
		} catch (VersionControlException e) {
			e.printStackTrace();
			return list;
		} catch (IOException e) {
			e.printStackTrace();
			return list;
		} catch (ParseException e) {
			e.printStackTrace();
			return list;
		} catch (WTException e) {
			e.printStackTrace();
			return list;
		}
		return list;
	}

	/**
	 * 获取查询符合名称的流程模板ID的SQL
	 *
	 * @author
	 * @date 2012-6-18
	 * @modify xzheng
	 * @param nameList
	 * @return
	 * @throws QueryException
	 * @throws VersionControlException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static QuerySpec getWfProcessQuerySpec(String[] nameList)
			throws QueryException, VersionControlException, IOException {
		Class wftemClass = WfProcessTemplate.class;
		QuerySpec qs = new QuerySpec();
		int wftemIdx = qs.addClassList(WfProcessTemplate.class, false);

		qs.appendSelect(new ClassAttribute(wftemClass,
				"thePersistInfo.theObjectIdentifier.id"), true);
		qs.setAdvancedQueryEnabled(true);
		ClassAttribute classAttribute = new ClassAttribute(wftemClass,
				WfProcessTemplate.NAME);

		ArrayExpression arrayExpression = new ArrayExpression(nameList);
		qs.appendWhere(new SearchCondition(classAttribute, SearchCondition.IN,
				arrayExpression), new int[] { wftemIdx });
		return qs;
	}

	/**
	 * 通过人名查找使用者
	 * @author qianlong
	 * @date 2013-7-30
	 * @param userid
	 * @return
	 */
	public static Principal getPrincipal(String userid){
		WTPrincipal principal = null;
		if (userid != null && !"".equals(userid)) {
			String[] arr = userid.split(",");
			if (arr.length > 0) {
				userid = arr[0].replace("uid=", "");
			}
			if ("wcadmin".equals(userid)) {
				userid = "Administrator";
			}
			try {
				QuerySpec qs = new QuerySpec(WTPrincipal.class);
//				qs.appendSelect(new ClassAttribute(WTPrincipal.class, Persistable.PERSIST_INFO + "."
//						+ PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID), new int[] { idx }, false);
				qs.appendWhere(new SearchCondition(WTPrincipal.class, WTPrincipal.NAME, SearchCondition.EQUAL, userid),
						new int[] {0});
				QueryResult qr = PersistenceHelper.manager.find(qs);
				while(qr.hasMoreElements()){
					Object obj = qr.nextElement();
					if(obj instanceof WTPrincipal){
						principal = (WTPrincipal) obj;
					}else{
						System.out.println("className++=" + obj.getClass().getName());
					}
				}
			} catch (QueryException e) {
				System.out.println("====add condition wrong=======");
				e.printStackTrace();
				return principal;
			} catch (WTException e) {
				System.out.println("====get principal wrong=======");
				e.printStackTrace();
				return principal;
			}
		}
		return principal;
	}

/**
 *  获得系统的所有WTGroup
 * @author qianlong
 * @date 2013-7-30
 * @return
 */
	public static List getAllWTGroup(){
		List groupList = new ArrayList();
		try {
			QuerySpec qs = new QuerySpec(WTGroup.class);
			qs.appendWhere(new SearchCondition(WTGroup.class, WTGroup.NAME, SearchCondition.LIKE,"%"),new int[]{0});
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while(qr.hasMoreElements()){
				Object obj = qr.nextElement();
				if(obj instanceof WTOrganization){
					break;
				}else if(obj instanceof WTGroup){
					WTGroup group = (WTGroup) obj;
					if(!groupList.contains(group)){
						groupList.add(group);
					}
				}
			}
		} catch (QueryException e) {
			e.printStackTrace();
			return groupList;
		} catch (WTException e) {
			System.out.println("====find group wrong===");
			e.printStackTrace();
			return groupList;
		}
		return groupList;
	}

	public static List getWorkItemList(){
		return list;
	}
}
