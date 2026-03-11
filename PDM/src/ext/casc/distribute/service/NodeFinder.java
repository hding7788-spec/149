package ext.casc.distribute.service;

import cn.hutool.cache.impl.TimedCache;
import ext.casc.distribute.controller.DistributeController;
import ext.casc.distribute.util.JsonVoConverter;
import ext.casc.distribute.vo.DWGraphVo;
import ext.casc.distribute.vo.DWNodeVo;
import ext.casc.distribute.vo.DWPropertiesVo;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import wt.session.SessionServerHelper;

import java.util.List;
import java.util.Map;

public class NodeFinder {

	public static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(NodeFinder.class);

	public static JSONObject findCurrentNode(String changeNumber, String partId, TimedCache<String, DWGraphVo> dwGraphVoTimedCache,
	                                         String taskItemObjectNumber, String dealType, String taskType) {

		System.out.println("findCurrentNode start: " + changeNumber + " / " + partId + " / " + taskItemObjectNumber + " / " + dealType + " / " + taskType);

		JSONObject result = new JSONObject();
		if(changeNumber == null || changeNumber.isEmpty() || partId == null || partId.isEmpty()) {
			return  result;
		}

		if(dealType==null)
		{
			return result;
		}

		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			// Construct cache key
//			String cacheKey = changeNumber + "!!@@" + partId;
//			DWGraphVo dWGraphVo = dwGraphVoTimedCache.get(cacheKey);
//			if (dWGraphVo == null) {
//				dWGraphVo = DistributeController.getGraphInfo(changeNumber, partId);
//				dwGraphVoTimedCache.put(cacheKey, dWGraphVo, 2*60*1000);
//			}

			DWGraphVo dWGraphVo = DistributeController.getGraphInfo(changeNumber, partId);
			DWNodeVo dwNodeVo = NodeInstanceTaskService.getBranchFlowKeyNode(dWGraphVo, taskItemObjectNumber, dealType, taskType);

			if(dwNodeVo!=null)
			{
				DWPropertiesVo dwPropertiesVo = dwNodeVo.getProperties();
				if(dwPropertiesVo!=null)
				{
					JSONObject nodeInfo = new JSONObject();
					nodeInfo.put("taskName", dwPropertiesVo.getBranchFlowCurrentNodeName());
					nodeInfo.put("taskOwner", dwPropertiesVo.getBranchFlowCurrentNodeOwner());
					nodeInfo.put("taskState", dwPropertiesVo.getBranchFlowCurrentNodeState());
					nodeInfo.put("taskStartDate", dwPropertiesVo.getBranchFlowCurrentNodeStartDate());
					nodeInfo.put("taskEndDate", dwPropertiesVo.getBranchFlowCurrentNodeEndDate());
					nodeInfo.put("itSystem", dwPropertiesVo.getBranchFlowCurrentNodeHead());
					nodeInfo.put("card", dwPropertiesVo.getBranchFlowCurrentCard());

					if(StringUtils.isBlank(dwPropertiesVo.getBranchFlowCurrentNodeName()) && "进行中".equals(dwPropertiesVo.getBranchFlowCurrentNodeState()))
					{
						System.out.println("debug warn table taskItemObjectNumber:" + taskItemObjectNumber + " / dealType: " + dealType + " / taskType:" + taskType);
						System.out.println("debug warn table 进行中，却没有任务名称的异常:" + JsonVoConverter.serialize(dwNodeVo));
					}else if(StringUtils.isBlank(dwPropertiesVo.getBranchFlowCurrentNodeName())==false && StringUtils.isBlank(dwPropertiesVo.getBranchFlowCurrentNodeOwner()) )
					{
						System.out.println("debug warn table taskItemObjectNumber:" + taskItemObjectNumber + " / dealType: " + dealType + " / taskType:" + taskType);
						System.out.println("debug warn table 有任务名称确没有负责人的异常:" + JsonVoConverter.serialize(dwNodeVo));
					}

					result.put("node", nodeInfo);
				}

			}


		} catch (Exception e) {
			e.printStackTrace();
			result.put("error", "Error processing JSON: " + e.getMessage());
		}finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}

		return result;
	}

	/*
	寻找当前节点
	 */
	private static JSONObject findNodeByPriority(List<JSONObject> matchingNodes) {
		if (matchingNodes.isEmpty()) {
			return null;
		}

		// 寻找 order 值最高的节点
		JSONObject latestNode = matchingNodes.get(0);
		int maxOrder = latestNode.optInt("order", -1); // 默认值 -1 处理缺失 order 的情况

		for (JSONObject node : matchingNodes) {
			int currentOrder = node.optInt("order", -1);
			if (currentOrder > maxOrder) {
				maxOrder = currentOrder;
				latestNode = node;
			}
		}
		return createNodeInfo(latestNode.optJSONObject("properties"), "Current Node (Latest)");
	}

//	private static JSONObject findNodeByPriority(List<JSONObject> matchingNodes) {
//		if (matchingNodes.isEmpty()) {
//			return null;
//		}
//
//		// 优先级 1：选择 order 最大的“进行中”节点
//		JSONObject latestInProgress = null;
//		int maxOrderInProgress = -1;
//		for (JSONObject node : matchingNodes) {
//			JSONObject props = node.optJSONObject("properties");
//			if ("进行中".equals(props.optString("taskState"))) {
//				int currentOrder = node.optInt("order", -1);
//				if (currentOrder > maxOrderInProgress) {
//					maxOrderInProgress = currentOrder;
//					latestInProgress = node;
//				}
//			}
//		}
//		if (latestInProgress != null) {
//			return createNodeInfo(latestInProgress.optJSONObject("properties"), "Current Node (In Progress)");
//		}
//
//		// 优先级 2：选择 order 最大的“未开始”节点
//		JSONObject latestNotStarted = null;
//		int maxOrderNotStarted = -1;
//		for (JSONObject node : matchingNodes) {
//			JSONObject props = node.optJSONObject("properties");
//			if ("未开始".equals(props.optString("taskState"))) {
//				int currentOrder = node.optInt("order", -1);
//				if (currentOrder > maxOrderNotStarted) {
//					maxOrderNotStarted = currentOrder;
//					latestNotStarted = node;
//				}
//			}
//		}
//		if (latestNotStarted != null) {
//			return createNodeInfo(latestNotStarted.optJSONObject("properties"), "Current Node (Not Started)");
//		}
//
//		// 优先级 3：如果全部是“已完成”或“已闭环”，选择 order 最大的节点
//		boolean allCompleted = true;
//		for (JSONObject node : matchingNodes) {
//			String state = node.optJSONObject("properties").optString("taskState");
//			if (!"已完成".equals(state) && !"已闭环".equals(state)) {
//				allCompleted = false;
//				break;
//			}
//		}
//		if (allCompleted) {
//			JSONObject latestNode = matchingNodes.get(0);
//			int maxOrder = latestNode.optInt("order", -1);
//			for (JSONObject node : matchingNodes) {
//				int currentOrder = node.optInt("order", -1);
//				if (currentOrder > maxOrder) {
//					maxOrder = currentOrder;
//					latestNode = node;
//				}
//			}
//			return createNodeInfo(latestNode.optJSONObject("properties"), "Current Node (Last)");
//		}
//
//		// 默认：选择 order 最大的节点
//		JSONObject latestNode = matchingNodes.get(0);
//		int maxOrder = latestNode.optInt("order", -1);
//		for (JSONObject node : matchingNodes) {
//			int currentOrder = node.optInt("order", -1);
//			if (currentOrder > maxOrder) {
//				maxOrder = currentOrder;
//				latestNode = node;
//			}
//		}
//		return createNodeInfo(latestNode.optJSONObject("properties"), "Current Node (Latest)");
//	}

//	private static JSONObject findNodeByPriority(List<JSONObject> matchingNodes) {
//		// Priority 1: Return "进行中" (In Progress) node
//		for (JSONObject node : matchingNodes) {
//			JSONObject props = node.optJSONObject("properties");
//			if ("进行中".equals(props.optString("taskState"))) {
//				return createNodeInfo(props, "Current Node");
//			}
//		}
//
//		// Priority 2: If all nodes are completed or closed, return the last node
//		boolean allCompleted = true;
//		for (JSONObject node : matchingNodes) {
//			String state = node.optJSONObject("properties").optString("taskState");
//			if (!"已完成".equals(state) && !"已闭环".equals(state)) {
//				allCompleted = false;
//				break;
//			}
//		}
//
//		if (allCompleted) {
//			JSONObject lastNode = matchingNodes.get(0);
//			int maxGridY = lastNode.optInt("gridY", 0);
//			for (JSONObject node : matchingNodes) {
//				int gridY = node.optInt("gridY", 0);
//				if (gridY > maxGridY) {
//					maxGridY = gridY;
//					lastNode = node;
//				}
//			}
//			return createNodeInfo(lastNode.optJSONObject("properties"), "Current Node (Last)");
//		}
//
//		// Priority 3: Return "未开始" (Not Started) node
//		for (JSONObject node : matchingNodes) {
//			JSONObject props = node.optJSONObject("properties");
//			if ("未开始".equals(props.optString("taskState"))) {
//				return createNodeInfo(props, "Current Node");
//			}
//		}
//
//		// Default: Return first matching node
//		return createNodeInfo(matchingNodes.get(0).optJSONObject("properties"), "Current Node");
//	}

	private static JSONObject createNodeInfo(JSONObject properties, String nodeType) {
		if (properties == null) {
			return new JSONObject().put("nodeType", nodeType).put("error", "No properties");
		}
		JSONObject nodeInfo = new JSONObject();
		nodeInfo.put("nodeType", nodeType);
		nodeInfo.put("itSystem", properties.optString("head", ""));
		nodeInfo.put("taskName", properties.optString("taskName", ""));
		nodeInfo.put("taskOwner", properties.optString("taskOwner", ""));
		nodeInfo.put("taskState", properties.optString("taskState", ""));
		nodeInfo.put("taskStartDate", properties.optString("taskStartDate", "N/A"));
		nodeInfo.put("taskEndDate", properties.optString("taskEndDate", "N/A"));
		nodeInfo.put("taskArrivalDate", properties.optString("taskArrivalDate", "N/A"));
		return nodeInfo;
	}
}