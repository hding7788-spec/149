package com.glaway.mpm.report.helper;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import wt.doc.WTDocumentMaster;
import wt.fc.ObjectIdentifier;
import wt.fc.PersistInfo;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.query.AttributeRange;
import wt.query.ClassAttribute;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.Util;

public class GMTReportBuilderHelper {
	private static int index[] = { 0 };

	/**
	 * 通过专业组，任务类型，任务创建区间查找工艺任务，返回任务的计算情况，信息放在map中
	 *
	 * @author liuzhaogang
	 * @date 2013-7-14
	 * @param group
	 *            专业组
	 * @param principal
	 * @param taskType
	 *            任务类型
	 * @param beginTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param flag
	 *            判断页面选择哪种统计
	 * @param export
	 *            判断是否在页面上显示table
	 * @param view
	 *            判断是否是通过ajax来查找
	 * @return
	 */

	public static Map getResult(List list, String flag, String export, String group, String principal, String taskType,
			String beginTime, String endTime, boolean isCycle) {
		Map map = new HashMap();
//		QueryResult qr = null;
//		group = group.replace("；", ";");
//		group = group.trim();
//		System.out.println("---group---group---" + group);
//		if ("".equals(group)  && "".equals(principal) && "".equals(taskType)
//				&& "".equals(beginTime) && "".equals(endTime)) {
//			return map;
//		}
//		try {
//			QuerySpec qs = null;
//			if(!"".equals(taskType)){
//				qs = new QuerySpec(Class.forName(taskType));
//			}else{
//				qs = new QuerySpec(GMTask.class);
//			}
//			qs.setAdvancedQueryEnabled(true);
//			String[] groupArr = group.split(";");
//			if (groupArr.length > 0) {
//				group = groupArr[0];
//				group = group.trim();
//				if (!"".equals(group)) {
//					qs.appendOpenParen();
//					qs.appendWhere(new SearchCondition(GMTask.class, GMTask.PROFESSIONAL_GROUP, SearchCondition.EQUAL,
//							group));
//					for (int i = 1; i < groupArr.length; i++) {
//						group = groupArr[i];
//						group = group.trim();
//						// System.out.println("==inner group==" + group);
//						if (group != null && !"".equals(group)) {
//							// System.out.println("+++++++++++++");
//							qs.appendOr();
//							qs.appendWhere(new SearchCondition(GMTask.class, GMTask.PROFESSIONAL_GROUP,
//									SearchCondition.EQUAL, group));
//						}
//					}
//					qs.appendCloseParen();
//				}
//			}
//			if (principal != null && !"".equals(principal)) {
//				String[] arr = principal.split(",");
//				if (arr.length > 0) {
//					principal = arr[0].replace("uid=", "");
//				}
//				if ("wcadmin".equals(principal)) {
//					principal = "Administrator";
//				}
//				if (qs.getWhere() != null) {
//					qs.appendAnd();
//				}
//				qs.appendOpenParen();
//				SubSelectExpression subSelectExpression = getUserQuery(principal);
//				// qs.appendWhere(new SearchCondition(userId,
//				// SearchCondition.IN, subSelectExpression), index);
//				if ("dispatch".equals(flag)) {
//					ClassAttribute userId = new ClassAttribute(GMTask.class, "planner.key.id");
//					qs.appendWhere(new SearchCondition(userId, SearchCondition.IN, subSelectExpression), index);
//				} else if ("design".equals(flag)) {
//					ClassAttribute userId = new ClassAttribute(GMTask.class, "disgner.key.id");
//					qs.appendWhere(new SearchCondition(userId, SearchCondition.IN, subSelectExpression), index);
////					qs.appendWhere(new SearchCondition(GMTask.class, GMTask.DISGNER, SearchCondition.EQUAL, principal));
//				} else if ("approve".equals(flag)) {
//					ClassAttribute userId = new ClassAttribute(GMTask.class, "approver.key.id");
//					qs.appendWhere(new SearchCondition(userId, SearchCondition.IN, subSelectExpression), index);
////					qs.appendWhere(new SearchCondition(GMTask.class, GMTask.APPROVER, SearchCondition.EQUAL,
////									principal));
//				}
//				qs.appendCloseParen();
//			}
//			if (beginTime != null && !"".equals(beginTime) && endTime != null && !"".equals(endTime)) {
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00.0");
//				beginTime = dateFormat.format(new Date(beginTime));
//				SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd 23:59:59.9");
//				endTime = dateFormat1.format(new Date(endTime));
//				if (qs.getWhere() != null) {
//					qs.appendAnd();
//				}
//				// qs.appendSearchCondition(new
//				// SearchCondition(GMTask.class,GMTask.CREATE_TIMESTAMP, true,
//				// new AttributeRange(beginTime, endTime)),index);
//				qs.appendWhere(new SearchCondition(GMTask.class, GMTask.CREATE_TIMESTAMP, true, new AttributeRange(
//						beginTime, endTime)));
//				// System.out.println("----choose bu time-----");
//			}
//			// if(!"".equals(type)){
//			// if(qs.getWhere() != null){
//			// qs.appendAnd();
//			// }
//			// if(type.contains("com.glaway.mpm.task.model.GMTechnicTask")){
//			// // System.out.println("type--------" + type);
//			// TypeUtil.getTypeQuery(GMTask.class, type, qs);
//			// }else if(type.contains("com.glaway.mpm.task.model.GMItemTask")){
//			// TypeUtil.getTypeQuery(GMTask.class, type, qs);
//			// }
//			//
//			// }
//			System.out.println("---++-qs-++--" + qs.toString());
//			qr = PersistenceHelper.manager.find(qs);
//			LatestConfigSpec lcs = new LatestConfigSpec();
//			qr = lcs.process(qr);
//			System.out.println("qr=====" + qr.size());
//		} catch (QueryException e) {
//			System.out.println("--查询错误--");
//			e.printStackTrace();
//			return map;
//		} catch (WTException e) {
//			System.out.println("--添加子类型条件错误--");
//			e.printStackTrace();
//			return map;
//		} catch (ParseException e) {
//			System.out.println("--添加时间范围错误--");
//			e.printStackTrace();
//			return map;
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//			return map;
//		}
//
//		try {
//			if(!isCycle){
//				setMap(qr, list, map, flag, export);
//			}else{
//				setCycleMap(qr, list, map, flag, export);
//			}
//		} catch (WTException e) {
//			System.out.println("===获取任务属性错误===");
//			e.printStackTrace();
//			return map;
//		}
//		System.out.println("list.size()=====" + list.size());
		return map;

	}

	/**
	 * 将工艺任务相关属性计算添加到list中以便返回显示，计算任务完成情况放在countMap中
	 *
	 * @author qianlong
	 * @date 2013-7-20
	 * @param qr
	 *            查询信息
	 * @param list
	 *            table中要显示的信息
	 * @param countMap
	 *            计算完成情况
	 * @param flag
	 *            判断页面选择哪种统计
	 * @param isExport
	 *            判断是否在页面上显示table
	 * @param view
	 *            判断是否是通过ajax来查找
	 * @throws WTException
	 *
	 */

	private static void setMap(QueryResult qr, List list, Map countMap, String flag, String isExport)
			throws WTException {
//		System.out.println("====start setMap====");
//		List countList = new ArrayList();
//		int all = 0;
//		int complishInTime = 0;
//		int complishOutTime = 0;
//		int noComplished = 0;
//		double percent = 0.00;
//		String complishState = "";
//		if (qr == null) {
//			return;
//		}
//		while (qr.hasMoreElements()) {
//			all = qr.size();
//			GMTask task = (GMTask) qr.nextElement();
//			String state = task.getState().getState().getDisplay();
//			Timestamp createTime = task.getCreateTimestamp();
//			Timestamp PDispatchTime = task.getPlannerDispatchTime();
//			Timestamp estimatedSubmitTimeStamp = task.getEstimatedSubmitTime();
//			Timestamp actualSubmitTimeStamp = task.getActualSubmitTime();
//			Timestamp estimatedApproveTimeStamp = task.getEstimatedApproveTime();
//			Timestamp actualApproveTimeStamp = task.getActualApproveTime();
//			boolean compareBoo = false;
//			if ("dispatch".equals(flag)) {
//				compareBoo = compareTime(createTime, PDispatchTime, flag);
//				if(PDispatchTime != null){
//					if(compareBoo){
//						complishState = "计划内完成";
//						complishInTime++;
//					}else{
//						complishState = "超期完成";
//						complishOutTime++;
//					}
//				}else{
//					if(!compareBoo){
//						complishState = "超期未完成";
//						noComplished++;
//					}else{
//						continue;
//					}
//				}
////				if (compareBoo && "计划员未派工".equals(state)) {
////					break;
////				} else if (compareBoo && "组长未派工".equals(state) || "拟制".equals(state) || "审核".equals(state)
////						|| "批准".equals(state) || "已归档".equals(state)) {
////					complishState = "计划内完成";
////					complishInTime++;
////				} else if (!compareBoo && "组长未派工".equals(state) || "拟制".equals(state) || "审核".equals(state)
////						|| "批准".equals(state) || "已归档".equals(state)) {
////					complishState = "超期完成";
////					complishOutTime++;
////				} else if (!compareBoo && "计划员未派工".equals(state)) {
////					complishState = "超期未完成";
////					noComplished++;
////				}
//			} else if ("design".equals(flag)) {
//				System.out.println("=====design====");
//				compareBoo = compareTime(estimatedSubmitTimeStamp, actualSubmitTimeStamp, flag);
//				if(actualSubmitTimeStamp != null || (actualSubmitTimeStamp != null && estimatedSubmitTimeStamp == null)){
//					if(compareBoo){
//						complishState = "计划内完成";
//						complishInTime++;
//					}else{
//						complishState = "超期完成";
//						complishOutTime++;
//					}
//				}else{
//					if(!compareBoo){
//						complishState = "超期未完成";
//						noComplished++;
//					}else{
//						continue;
//					}
//				}
////				if (compareBoo && "未派工".equals(state) || "组长未派工".equals(state) || "拟制".equals(state)
////						|| "审核".equals(state)) {
////					break;
////				} else if (compareBoo && "批准".equals(state) || "已归档".equals(state)) {
////					complishState = "计划内完成";
////					complishInTime++;
////				} else if (!compareBoo && "批准".equals(state) || "已归档".equals(state)) {
////					complishState = "超期完成";
////					complishOutTime++;
////				} else if (!compareBoo && "计划员未派工".equals(state) || "组长未派工".equals(state) || "拟制".equals(state)
////						|| "审核".equals(state)) {
////					complishState = "超期未完成";
////					noComplished++;
////				}
//				System.out.println("=====complishInTime====" + complishInTime + "--complishOutTime--" + complishOutTime + "--noComplished--" + noComplished);
//			} else if ("approve".equals(flag)) {
//				compareBoo = compareTime(estimatedApproveTimeStamp, actualApproveTimeStamp, flag);
//				if(actualApproveTimeStamp != null || (actualApproveTimeStamp != null && estimatedApproveTimeStamp == null)){
//					if(compareBoo){
//						complishState = "计划内完成";
//						complishInTime++;
//					}else{
//						complishState = "超期完成";
//						complishOutTime++;
//					}
//				}else{
//					if(!compareBoo){
//						complishState = "超期未完成";
//						noComplished++;
//					}else{
//						continue;
//					}
//				}
////				if (compareBoo && "计划员未派工".equals(state) || "组长未派工".equals(state) || "拟制".equals(state)
////						|| "审核".equals(state) || "批准".equals(state)) {
////					break;
////				} else if (compareBoo && "已归档".equals(state)) {
////					complishState = "计划内完成";
////					complishInTime++;
////				} else if (!compareBoo && "已归档".equals(state)) {
////					complishState = "超期完成";
////					complishOutTime++;
////				} else if (!compareBoo && "计划员未派工".equals(state) || "组长未派工".equals(state) || "拟制".equals(state)
////						|| "审核".equals(state) || "批准".equals(state)) {
////					complishState = "超期未完成";
////					noComplished++;
////				}
//			}
//			if ("export".equals(isExport)) {
//				Map map = new HashMap();
//				boolean boo = task.getIsUrgency() == null ? false : true;
//				String isUrgency = String.valueOf(boo).toString();
//				if(boo){
//					isUrgency = "是";
//				}else{
//					isUrgency = "否";
//				}
//				String workshop = "";
//				try{
//					WTPart part = getPartByTask(task);
//					if(part != null){
//						IBAHelper iba = new IBAHelper(part);
//						workshop = iba.getIBAValue(part, "workshop");
//					}
//				}catch(WTException e){
//					e.printStackTrace();
//					System.out.println("===start return====");
//					return;
//				}
//				System.out.println("workshop====" + workshop);
//				String partNumber = task.getPartNumber();
//				String planner = task.getPlanner() == null ? "" : task.getPlanner().getDisplayName();
//				String plannerDispatchTime = task.getPlannerDispatchTime() == null ? "" : task.getPlannerDispatchTime()
//						.toString();
//				String disgner = task.getDisgner() == null ? "" : task.getDisgner().getDisplayName();
//				String reviewer = task.getReviewer() == null ? "" : task.getReviewer().getDisplayName();
//				String professionalGroup = task.getProfessionalGroup();
//				String groupLeader = task.getGroupLeader() == null ? "" : task.getGroupLeader().getDisplayName();
//				String mainDivish = task.getMainDivish() == null ? "" : task.getMainDivish().getDisplayName();
//				String createStamp = task.getCreateTimestamp() == null ? "" : task.getCreateTimestamp().toString();
//				String taskType = task.getType();
//				String estimatedSubmitTime = estimatedSubmitTimeStamp == null ? "" : estimatedSubmitTimeStamp
//						.toString();
//				String actualSubmitTime = actualSubmitTimeStamp == null ? "" : actualSubmitTimeStamp.toString();
//				String estimatedApproveTime = estimatedApproveTimeStamp == null ? "" : estimatedApproveTimeStamp
//						.toString();
//				String actualApproveTime = actualApproveTimeStamp == null ? "" : actualApproveTimeStamp.toString();
//				map.put("isUrgency", isUrgency);
//				map.put("partpictureNumber", partNumber);
//				map.put("MytaskType", taskType);
//				map.put("state", state);
//				map.put("planner", planner);
//				map.put("plannerDispatchTime", plannerDispatchTime);
//				map.put("accomplishedPercent", complishState);
//				map.put("workshop", workshop);
//				map.put("disgner", disgner);
//				map.put("reviewer", reviewer);
//				map.put("professionalGroup", professionalGroup);
//				map.put("groupLeader", groupLeader);
//				map.put("mainDivish", mainDivish);
//				map.put("createStamp", createStamp);
//				map.put("myestimatedSubmitTime", estimatedSubmitTime);
//				map.put("myestimatedApproveTime", estimatedApproveTime);
//				map.put("myactualSubmitTime", actualSubmitTime);
//				map.put("myactualApproveTime", actualApproveTime);
//				list.add(map);
//			}
//
//		}
//		if (all != 0) {
//			System.out.println("complishInTime---" + complishInTime);
//			System.out.println("complishOutTime---" + complishOutTime);
//			System.out.println("all---" + all);
//			System.out.println("percent---" + (complishInTime + complishOutTime) % all);
//			double complishInTimedouble =  complishInTime;
//			percent = ((complishInTimedouble + complishOutTime) / all) * 100;
//		}
//		String allStr = String.valueOf(all);
//		String complishInTimeStr = String.valueOf(complishInTime);
//		String complishOutTimeStr = String.valueOf(complishOutTime);
//		String noComplishedStr = String.valueOf(noComplished);
//		String percentStr = String.valueOf(percent);
//		if(percentStr.length() < 5){
//			percentStr = percentStr + "%";
//		}else{
//		percentStr = percentStr.substring(0, 4);
//		percentStr = percentStr + "%";
//		}
//		// System.out.println("--allStr--" + allStr);
//		// System.out.println("--complishInTime--" + complishInTime);
//		countMap.put("all", allStr);
//		countMap.put("complishInTime", complishInTimeStr);
//		countMap.put("complishOutTime", complishOutTimeStr);
//		countMap.put("noComplished", noComplishedStr);
//		countMap.put("percent", percentStr);
	}


	/**
	 * 将工艺任务相关属性计算添加到list中以便返回显示，计算任务完成情况放在countMap中
	 *
	 * @author qianlong
	 * @date 2013-7-20
	 * @param qr
	 *            查询信息
	 * @param list
	 *            table中要显示的信息
	 * @param countMap
	 *            计算完成周期
	 * @param flag
	 *            判断页面选择哪种统计
	 * @param isExport
	 *            判断是否在页面上显示table
	 *
	 * @throws WTException
	 *
	 */

	private static void setCycleMap(QueryResult qr, List list, Map countMap, String flag, String isExport)
			 {
//		System.out.println("====start setMap====");
//		int all = 0;
//		int less4 = 0;
//		int betweem1 = 0;
//		int betweem2 = 0;
//		int betweem3 = 0;
//		int betweem4 = 0;
//		int betweem5 = 0;
//		int more = 0;
//		int allTime = 0;
//		int average = 0;
//		String complishState = "";
//		if (qr == null) {
//			return;
//		}
//		while (qr.hasMoreElements()) {
//			GMTask task = (GMTask) qr.nextElement();
//			String state = task.getState().getState().getDisplay();
//			Timestamp createTime = task.getCreateTimestamp();
//			Timestamp PDispatchTime = task.getPlannerDispatchTime();
//			Timestamp estimatedSubmitTimeStamp = task.getEstimatedSubmitTime();
//			Timestamp actualSubmitTimeStamp = task.getActualSubmitTime();
//			Timestamp estimatedApproveTimeStamp = task.getEstimatedApproveTime();
//			Timestamp actualApproveTimeStamp = task.getActualApproveTime();
//			int cycleInt = -1;
//			if ("dispatch".equals(flag)) {
//				cycleInt = countTime(PDispatchTime,createTime,flag);
//				if(cycleInt == -1){
//					continue;
//				}
//				allTime = allTime + cycleInt;
//				if(cycleInt <= 4){
//					less4 ++;
//				}else if(cycleInt > 4 && cycleInt <= 6){
//					betweem1 ++;
//				}else if(cycleInt > 6 && cycleInt <= 8){
//					betweem2++;
//				}else if(cycleInt > 8 && cycleInt <= 10){
//					betweem3 ++;
//				}else if(cycleInt > 10 && cycleInt <= 12){
//					betweem4 ++;
//				}else if(cycleInt > 12 && cycleInt <= 14){
//					betweem5 ++;
//				}else{
//					more ++;
//				}
//			} else if ("design".equals(flag)) {
//				cycleInt = countTime(actualSubmitTimeStamp,PDispatchTime,flag);
//				if(cycleInt == -1){
//					continue;
//				}
//				allTime = allTime + cycleInt;
//				if(cycleInt <= 4){
//					less4 ++;
//				}else if(cycleInt > 4 && cycleInt <= 6){
//					betweem1++;
//				}else if(cycleInt > 6 && cycleInt <= 8){
//					betweem2 ++;
//				}else if(cycleInt > 8 && cycleInt <= 10){
//					betweem3 ++;
//				}else if(cycleInt > 10 && cycleInt <= 12){
//					betweem4 ++;
//				}else if(cycleInt > 12 && cycleInt <= 14){
//					betweem5 ++;
//				}else{
//					more ++;
//				}
//			} else if ("approve".equals(flag)) {
//				cycleInt = countTime(actualApproveTimeStamp,actualSubmitTimeStamp,flag);
//				if(cycleInt == -1){
//					continue;
//				}
//				allTime = allTime + cycleInt;
//				if(cycleInt <= 4){
//					less4 ++;
//				}else if(cycleInt > 4 && cycleInt <= 6){
//					betweem1 ++;
//				}else if(cycleInt > 6 && cycleInt <= 8){
//					betweem2 ++;
//				}else if(cycleInt > 8 && cycleInt <= 10){
//					betweem3 ++;
//				}else if(cycleInt > 10 && cycleInt <= 12){
//					betweem4 ++;
//				}else if(cycleInt > 12 && cycleInt <= 14){
//					betweem5 ++;
//				}else{
//					more ++;
//				}
//			}
//			all ++;
//			if ("export".equals(isExport)) {
//				Map map = new HashMap();
//				boolean boo = task.getIsUrgency() == null ? false : true;
//				String isUrgency = String.valueOf(boo).toString();
//				if(boo){
//					isUrgency = "是";
//				}else{
//					isUrgency = "否";
//				}
//				String workshop = "";
//				try{
//					WTPart part = getPartByTask(task);
//					if(part != null){
//						IBAHelper iba = new IBAHelper(part);
//						workshop = iba.getIBAValue(part, "workshop");
//					}
//				}catch(WTException e){
//					e.printStackTrace();
//					System.out.println("===start return====");
//					return;
//				}
//				System.out.println("workshop====" + workshop);
//
//				try {
//					String partNumber = task.getPartNumber();
//					String planner = task.getPlanner() == null ? "" : task.getPlanner().getDisplayName();
//					String plannerDispatchTime = task.getPlannerDispatchTime() == null ? "" : task.getPlannerDispatchTime()
//							.toString();
//					String disgner = task.getDisgner() == null ? "" : task.getDisgner().getDisplayName();
//					String reviewer = task.getReviewer() == null ? "" : task.getReviewer().getDisplayName();
//					String professionalGroup = task.getProfessionalGroup();
//					String groupLeader = task.getGroupLeader() == null ? "" : task.getGroupLeader().getDisplayName();
//					String mainDivish = task.getMainDivish() == null ? "" : task.getMainDivish().getDisplayName();
//					String createStamp = task.getCreateTimestamp() == null ? "" : task.getCreateTimestamp().toString();
//					String taskType = task.getType();
//					String estimatedSubmitTime = estimatedSubmitTimeStamp == null ? "" : estimatedSubmitTimeStamp
//							.toString();
//					String actualSubmitTime = actualSubmitTimeStamp == null ? "" : actualSubmitTimeStamp.toString();
//					String estimatedApproveTime = estimatedApproveTimeStamp == null ? "" : estimatedApproveTimeStamp
//							.toString();
//					String actualApproveTime = actualApproveTimeStamp == null ? "" : actualApproveTimeStamp.toString();
//					map.put("isUrgency", isUrgency);
//					map.put("partpictureNumber", partNumber);
//					map.put("MytaskType", taskType);
//					map.put("state", state);
//					map.put("planner", planner);
//					map.put("plannerDispatchTime", plannerDispatchTime);
//					map.put("cycle", String.valueOf(cycleInt));
//					map.put("workshop", workshop);
//					map.put("disgner", disgner);
//					map.put("reviewer", reviewer);
//					map.put("professionalGroup", professionalGroup);
//					map.put("groupLeader", groupLeader);
//					map.put("mainDivish", mainDivish);
//					map.put("createStamp", createStamp);
//					map.put("myestimatedSubmitTime", estimatedSubmitTime);
//					map.put("myestimatedApproveTime", estimatedApproveTime);
//					map.put("myactualSubmitTime", actualSubmitTime);
//					map.put("myactualApproveTime", actualApproveTime);
//					list.add(map);
//				} catch (WTException e) {
//					e.printStackTrace();
//					return;
//				}
//			}
//
//		}
//		if(all != 0 ){
//			average = allTime/all;
//		}
//
//		String less4Sre = String.valueOf(less4);
//		String betweem1Str = String.valueOf(betweem1);
//		String betweem2Str = String.valueOf(betweem2);
//		String betweem3Str = String.valueOf(betweem3);
//		String betweem4Str = String.valueOf(betweem4);
//		String betweem5Str = String.valueOf(betweem5);
//		String moreStr = String.valueOf(more);
//		String allStr = String.valueOf(all);
//		String averageStr = String.valueOf(average);
//		if("design".equals(flag)){
//			averageStr = averageStr + " day";
//		}
//		countMap.put("less4Str", less4Sre);
//		countMap.put("betweem1Str", betweem1Str);
//		countMap.put("betweem2Str", betweem2Str);
//		countMap.put("betweem3Str", betweem3Str);
//		countMap.put("betweem4Str", betweem4Str);
//		countMap.put("betweem5Str", betweem5Str);
//		countMap.put("moreStr", moreStr);
//		countMap.put("all", allStr);
//		countMap.put("averageStr", averageStr);
	}


	private static int countTime(Timestamp frontTime, Timestamp afterTime, String flag){
		int intTime = -1;
		if(frontTime == null){
			return intTime;
		}
		long frontDay = frontTime.getTime();
		long afterDay = afterTime.getTime();
		if("design".equals(flag)){
			long time =  frontDay - afterDay;
			if(time%(24*60*60*1000) != 0){
				intTime = (int) (time/(24*60*60*1000) + 1);
			}else{
				intTime = (int) (time/(24*60*60*1000));
			}
		}else{
			long time =  frontDay - afterDay;
			if(time%(60*60*1000) != 0){
				intTime = (int) (time/(60*60*1000) + 1);
			}else{
				intTime = (int) (time/(60*60*1000));
			}

		}
		return intTime;

	}

	/**
	 * 对比两个时间判断是否超期
	 *
	 * @author qianlong
	 * @date 2013-7-16
	 * @param endTime
	 *            最后日期 在dispatch中为建立时间
	 * @param accomplishTime
	 *            完成时间  在dispatch中为计划员派工时间
	 * @return true 计划内，false为超期
	 */
	private static boolean compareTime(Timestamp endTime, Timestamp accomplishTime, String flag) {
		int hour = 0;
		if (endTime == null) {
			return true;
		} else if (accomplishTime == null) {
			// 有最后期限没有完成时间，比较当前时间和最后期限
			// TimeZone timeZone = TimeZone.getTimeZone("GMT+8");
			// Calendar calendar = Calendar.getInstance(timeZone);
			Calendar calendar = Calendar.getInstance();
			long nowTimeMillis = calendar.getTimeInMillis();
			Timestamp nowTime = new Timestamp(nowTimeMillis);
			long endTimeMillis = endTime.getTime();
			hour = endTime.getHours();
			if ("dispatch".equals(flag)) {
				if (hour < 14) {
					String nowTimeStr = nowTime.toString().split(" ")[0];
					String timeOneStr = endTime.toString().split(" ")[0];
					if (nowTimeStr.equals(timeOneStr)) {
						return true;
					}
				} else if (hour > 14) {
					int endDay = endTime.getDate();
					endDay = endDay + 2;
					endTime.setHours(0);
					endTime.setMinutes(0);
					endTime.setSeconds(0);
					endTime.setNanos(0);
					endTime.setDate(endDay);
					if (endTime.after(nowTime)) {
						return true;
					}
				}
			} else {
				if (endTime.after(nowTime)) {
					return true;
				}
			}
		} else if ("dispatch".equals(flag) && accomplishTime != null && endTime != null) {
			hour = endTime.getHours();
			// System.out.println("hour----" + hour);

			if (hour < 14) {
				String complishTimeStr = accomplishTime.toString().split(" ")[0];
				String timeOneStr = endTime.toString().split(" ")[0];
				if (complishTimeStr.equals(timeOneStr)) {
					return true;
				}
			} else if (hour > 14) {
				int endDay = endTime.getDate();
				endDay = endDay + 2;
				endTime.setHours(0);
				endTime.setMinutes(0);
				endTime.setSeconds(0);
				endTime.setNanos(0);
				endTime.setDate(endDay);
				if (endTime.after(accomplishTime)) {
					return true;

				}
			} else {
				if (endTime.after(accomplishTime)) {
					return true;
				}
			}
		}
		return false;
	}


	private static SubSelectExpression getUserQuery(String userName) throws QueryException {
		QuerySpec qs = new QuerySpec();
		int idx = qs.appendClassList(WTPrincipal.class, false);
		qs.appendSelect(new ClassAttribute(WTPrincipal.class, Persistable.PERSIST_INFO + "."
				+ PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID), new int[] { idx }, false);
		qs.appendWhere(new SearchCondition(WTPrincipal.class, WTPrincipal.NAME, SearchCondition.EQUAL, userName),
				new int[] { idx });
		return new SubSelectExpression(qs);
	}


	/**
	 * 获得配置文件里部门下的组
	 * @author qianlong
	 * @date 2013-7-22
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String getGroupInDepartment(String path,String department) throws IOException{
		String group = "";
		Properties properties = Util.getProperties(path);
		group = (String) properties.get(department);
		return group;
	}


	/**
	 * 通过群组名称模糊查询群组
	 * @author qianlong
	 * @date 2013-7-22
	 * @param groupName
	 * @return
	 */
	public static String getGroup(String groupName){
		StringBuffer resultStr = new StringBuffer();
		if(groupName == null){
			return "";
		}
			try {
				QuerySpec qs = new QuerySpec(WTGroup.class);
				LatestConfigSpec lcs = new LatestConfigSpec();
				groupName = groupName.replace("*", "%");
				groupName = groupName.replace("×", "%");
				qs.appendWhere(new SearchCondition(WTGroup.class, WTGroup.NAME, SearchCondition.LIKE,groupName),index);
				QueryResult qr = PersistenceHelper.manager.find(qs);
	//			qr = lcs.process(qr);
				System.out.println("qr.size++++++" + qr.size());
				while(qr.hasMoreElements()){
					WTObject obj = (WTGroup) qr.nextElement();
					if(obj instanceof WTOrganization){
						break;
					}else if(obj instanceof WTGroup){
						String name = ((WTGroup) obj).getName();
						name =name.trim();
						if(resultStr.indexOf(name) < 0){
							if(resultStr.indexOf(",") == 0){
								resultStr.deleteCharAt(0);
								resultStr.append(name);
							}else{
								resultStr.append(",");
								resultStr.append(name);
							}
						}
					}
				}
				return resultStr.toString();
			} catch (QueryException e) {
				e.printStackTrace();
			} catch (WTException e) {
				System.out.println("--find qs wrong--");
				e.printStackTrace();
			}
			return "";
		}


	/**
	 * 判断登录者是否属于指定组
	 * @author qianlong
	 * @date 2013-7-22
	 * @return
	 */
		public static boolean isInGroup(List<String> list){
			boolean boo = false;
			try {
				WTPrincipal principal = SessionHelper.manager.getPrincipal();
				WTGroup group;
				for(String groupName:list){
					group = OrganizationServicesHelper.manager.getGroup(groupName);
					boo = group.isMember(principal);
				}
			} catch (WTException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("boo====" + boo);
			return boo;

	}

		/**
		 * 判断登录者是否属于指定组
		 * @author qianlong
		 * @date 2013-7-22
		 * @return
		 */
			public static boolean isInGroup(String allGroupName){
				boolean boo = false;
				String[] groupStr = allGroupName.split(";");
				try {
					WTPrincipal principal = SessionHelper.manager.getPrincipal();
					WTGroup group;
					for(String groupName:groupStr){
						group = OrganizationServicesHelper.manager.getGroup(groupName);
						boo = group.isMember(principal);
						if(boo){
							return boo;
						}
					}
				} catch (WTException e) {
					e.printStackTrace();
				}
				return boo;
		}


			/**
			 * 通过任务获取对应的零件
			 * @author lbzhang
			 * @date  2013-6-24
			 * @param obj
			 * @return
			 * @throws WTException
			 *
			 */
//			@SuppressWarnings("deprecation")
//			public static WTPart getPartByTask(GMTask obj) throws WTException{
//				WTDocumentMaster master = (WTDocumentMaster) obj.getMaster();
//				long oid = Util.getLongOid(master);
//				System.out.println("oid===>" + oid);
//				QuerySpec qs = new QuerySpec(GMPartToTaskLink.class);
//				qs.appendWhere(new SearchCondition(GMPartToTaskLink.class, "roleBObjectRef.key.id",
//								SearchCondition.EQUAL, oid));
//				QueryResult qr = PersistenceHelper.manager.find(qs);
//				if (qr.hasMoreElements()) {
//					GMPartToTaskLink link = (GMPartToTaskLink) qr.nextElement();
//					WTPart part = (WTPart) link.getRoleAObject();
//					return part;
//				}
//				return null;
//			}


	public static void main(String args[]){

	}

}
