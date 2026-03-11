package ext.casc.distribute.service;

import ext.casc.distribute.contant.DistributeConstants;
import ext.casc.distribute.util.JsonVoConverter;
import ext.casc.distribute.vo.DWEdgeVo;
import ext.casc.distribute.vo.DWNodeVo;
import ext.casc.distribute.vo.DWGraphVo;

import java.util.*;

public class NodeOperationService {

    private int instanceIdInterger = 0;


    public DWGraphVo cloneWholeTree() {

        NodeHierarchyService nodeHierarchyService = new NodeHierarchyService();
        DWGraphVo originalDWGraphVo = nodeHierarchyService.generateDWGraphVo();

        // 1. 克隆节点并生成新ID
        Map<String, String> oldIdToNewIdMap = new HashMap<>();
        List<DWNodeVo> clonedNodes = new ArrayList<>();
        for (DWNodeVo node : originalDWGraphVo.getNodes()) {
            DWNodeVo clonedNode = cloneNode(node);
            String newNodeId = generateNewId(node.getId());
            clonedNode.setId(newNodeId);
            oldIdToNewIdMap.put(node.getId(), newNodeId);
            clonedNodes.add(clonedNode);
        }

        // 2. 克隆边并替换节点ID引用
        List<DWEdgeVo> clonedEdges = new ArrayList<>();
        for (DWEdgeVo edge : originalDWGraphVo.getEdges()) {
            DWEdgeVo clonedEdge = cloneEdge(edge);
            // 替换源节点和目标节点的ID为克隆后的ID
            clonedEdge.setSourceNodeId(oldIdToNewIdMap.get(edge.getSourceNodeId()));
            clonedEdge.setTargetNodeId(oldIdToNewIdMap.get(edge.getTargetNodeId()));
            clonedEdge.setId(generateNewId(edge.getId())); // 生成新边ID
            clonedEdges.add(clonedEdge);
        }

        // 返回克隆后的结果
        DWGraphVo root = new DWGraphVo(clonedNodes, clonedEdges);
        return root;
    }

    /**
     * 克隆单个节点（复制属性）
     */
    private DWNodeVo cloneNode(DWNodeVo originalNode) {
        String jsonStr = JsonVoConverter.serialize(originalNode);
        DWNodeVo restoredVo = JsonVoConverter.deserialize(jsonStr, DWNodeVo.class);
        return restoredVo;

    }

    /**
     * 克隆单个边（复制属性）
     */
    private DWEdgeVo cloneEdge(DWEdgeVo originalEdge) {
        DWEdgeVo clonedEdge = new DWEdgeVo();
        clonedEdge.setType(originalEdge.getType());
        clonedEdge.setText(originalEdge.getText());
        return clonedEdge;
    }

    private synchronized String generateNewId(String oldId) {
        instanceIdInterger = instanceIdInterger + 1;
        return oldId + DistributeConstants.ID_SPLIT_STR + instanceIdInterger;
    }


    /**
     * 移除指定节点及其所有子节点，并清理相关边
     * @param graph 目标图对象
     * @param rootNode 要移除的根节点
     */
    public void removeNodeAndChildren(DWGraphVo graph, DWNodeVo rootNode) {
        if (graph == null || rootNode == null) {
            throw new IllegalArgumentException("图对象或根节点不能为null");
        }

        // 收集所有待移除的节点ID（含根节点及其所有子节点）
        Set<String> removedNodeIds = collectAllDescendantIds(graph, rootNode);

        // 收集所有需要移除的边（与待移除节点相关的边）
        List<DWEdgeVo> edgesToRemove = collectRelatedEdges(graph, removedNodeIds);

        // 执行移除操作
        removeNodes(graph, removedNodeIds);
        removeEdges(graph, edgesToRemove);
    }

    /**
     * 收集根节点及其所有子节点的ID
     */
    private Set<String> collectAllDescendantIds(DWGraphVo graph, DWNodeVo rootNode) {
        Set<String> removedIds = new HashSet<String>();
        Queue<String> nodeQueue = new LinkedList<String>();
        String rootId = rootNode.getId();

        // 校验根节点是否存在于图中
        boolean rootExists = false;
        for (DWNodeVo node : graph.getNodes()) {
            if (rootId.equals(node.getId())) {
                rootExists = true;
                break;
            }
        }
        if (!rootExists) {
            return removedIds; // 根节点不在图中，返回空集合
        }

        removedIds.add(rootId);
        nodeQueue.add(rootId);

        // 广度优先搜索所有子节点（通过边的 sourceNodeId 关联）
        while (!nodeQueue.isEmpty()) {
            String currentNodeId = nodeQueue.poll();
            // 遍历所有边，查找当前节点指向的子节点
            for (DWEdgeVo edge : graph.getEdges()) {
                if (currentNodeId.equals(edge.getSourceNodeId())) {
                    String childNodeId = edge.getTargetNodeId();
                    if (!removedIds.contains(childNodeId)) {
                        removedIds.add(childNodeId);
                        nodeQueue.add(childNodeId);
                    }
                }
            }
        }
        return removedIds;
    }

    /**
     * 收集与待移除节点相关的所有边
     */
    private List<DWEdgeVo> collectRelatedEdges(DWGraphVo graph, Set<String> removedNodeIds) {
        List<DWEdgeVo> edgesToRemove = new ArrayList<DWEdgeVo>();
        for (DWEdgeVo edge : graph.getEdges()) {
            if (removedNodeIds.contains(edge.getSourceNodeId())
                    || removedNodeIds.contains(edge.getTargetNodeId())) {
                edgesToRemove.add(edge);
            }
        }
        return edgesToRemove;
    }

    /**
     * 从图中移除指定节点
     */
    private void removeNodes(DWGraphVo graph, Set<String> removedNodeIds) {
        Iterator<DWNodeVo> nodeIterator = graph.getNodes().iterator();
        while (nodeIterator.hasNext()) {
            DWNodeVo node = nodeIterator.next();
            if (removedNodeIds.contains(node.getId())) {
                nodeIterator.remove();
            }
        }
    }

    /**
     * 从图中移除指定边
     */
    private void removeEdges(DWGraphVo graph, List<DWEdgeVo> edgesToRemove) {
        graph.getEdges().removeAll(edgesToRemove);
    }


    public DWGraphVo copyNodeAndChildren(DWGraphVo graph, DWNodeVo parentDwNodeVo, DWNodeVo sourceNode) {
        if (graph == null || sourceNode == null) {
            throw new IllegalArgumentException("原始图或源节点不能为null");
        }

        // 步骤1：收集源节点及其所有子节点ID（复用移除逻辑中的收集方法）
        Set<String> sourceNodeIds = collectAllDescendantIds(graph, sourceNode);
        if (sourceNodeIds.isEmpty()) {
            return new DWGraphVo(new ArrayList<DWNodeVo>(), new ArrayList<DWEdgeVo>());
        }

        // 步骤2：克隆节点并生成新ID（复用cloneNode和generateNewId）
        Map<String, String> oldIdToNewIdMap = new HashMap<String, String>();
        List<DWNodeVo> clonedNodes = new ArrayList<DWNodeVo>();
        for (DWNodeVo node : graph.getNodes()) {
            if (sourceNodeIds.contains(node.getId())) {
                DWNodeVo clonedNode = cloneNode(node);  // 复用已有节点克隆方法
                String newNodeId = generateNewId(node.getId());  // 复用ID生成逻辑
                clonedNode.setId(newNodeId);
                oldIdToNewIdMap.put(node.getId(), newNodeId);
                clonedNodes.add(clonedNode);
            }
        }

        // 步骤3：克隆关联边并替换节点引用（复用cloneEdge和ID映射）
        List<DWEdgeVo> clonedEdges = new ArrayList<DWEdgeVo>();
        for (DWEdgeVo edge : graph.getEdges()) {
            // 仅克隆与源子树相关的边（源或目标在源节点集合中）
            if (sourceNodeIds.contains(edge.getSourceNodeId())
                    || sourceNodeIds.contains(edge.getTargetNodeId())) {

                DWEdgeVo clonedEdge = cloneEdge(edge);  // 复用已有边克隆方法
                // 替换为新节点ID
                if(oldIdToNewIdMap.get(edge.getSourceNodeId())==null)
                {
                    clonedEdge.setSourceNodeId(parentDwNodeVo.getId());
                }else
                {
                    clonedEdge.setSourceNodeId(oldIdToNewIdMap.get(edge.getSourceNodeId()));
                }
                clonedEdge.setTargetNodeId(oldIdToNewIdMap.get(edge.getTargetNodeId()));
                // 生成新边ID
                clonedEdge.setId(generateNewId(edge.getId()));
                clonedEdges.add(clonedEdge);
            }
        }

        return new DWGraphVo(clonedNodes, clonedEdges);
    }

}
