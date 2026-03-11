package ext.casc.distribute.service;

import ext.casc.distribute.contant.DistributeConstants;
import ext.casc.distribute.util.JsonVoConverter;
import ext.casc.distribute.vo.DWEdgeVo;
import ext.casc.distribute.vo.DWGraphVo;
import ext.casc.distribute.vo.DWNodeVo;
import ext.casc.distribute.vo.DWPropertiesVo;

import org.apache.commons.lang3.StringUtils;
import wt.method.MethodContext;
import wt.pom.WTConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class NodeHierarchyService {

    public List<DWNodeVo> generateNodeList() {

        List<DWNodeVo> baseNodeList = getDefaultDWNodeVoList();
        List<DWEdgeVo> dwEdgeVoList = getDefaultDWEdgeVoList();

        DWGraphVo dwGraphVo = new DWGraphVo(baseNodeList, dwEdgeVoList);
        calculateGridYByIteration(dwGraphVo);

        calculateGridX(baseNodeList, dwEdgeVoList);

        return baseNodeList;
    }

    public DWGraphVo generateDWGraphVo()
    {
        List<DWNodeVo> baseNodeList = getDefaultDWNodeVoList();
        List<DWEdgeVo> dwEdgeVoList = getDefaultDWEdgeVoList();

        DWGraphVo dwGraphVo = new DWGraphVo(baseNodeList, dwEdgeVoList);
        return dwGraphVo;
    }

    public void formatLayout(DWGraphVo dwGraphVo)
    {
        calculateGridYByIteration(dwGraphVo);

        calculateGridX(dwGraphVo.getNodes(), dwGraphVo.getEdges());

        //重新计算父节点gridX。循环多次，确保调整。
        for(int i = 0; i < 20; i++)
        {
            Map<String, List<DWNodeVo>> parentToChildrenMap = getParentToChildrenMap(dwGraphVo);
            for(String parentId : parentToChildrenMap.keySet())
            {
                List<DWNodeVo> childrenList = parentToChildrenMap.get(parentId);


                if(childrenList!=null && childrenList.size()>0)
                {
                    DWNodeVo tempDWNodeVo = findClosestToAverage(childrenList);
                    DWNodeVo parentNode = findNodeById(dwGraphVo.getNodes(), parentId);
                    if(parentNode.getGridX()<tempDWNodeVo.getGridX())
                    {
                        parentNode.setGridX(tempDWNodeVo.getGridX());
                    }

                }
            }
        }

    }


        public static DWNodeVo findClosestToAverage(List<DWNodeVo> objects) {
        if (objects == null || objects.isEmpty()) {
            throw new IllegalArgumentException("对象列表不能为空");
        }

        // 计算总和
        int sum = 0;
        for (DWNodeVo dWNodeVo : objects) {
            sum = sum + dWNodeVo.getGridX();
        }

        // 计算平均值（使用浮点数避免精度丢失）
        double average = (double) sum / objects.size();

        // 遍历查找最接近的对象
        DWNodeVo closest = objects.get(0);
        double minDiff = Math.abs(closest.getGridX() - average);

        for (DWNodeVo obj : objects) {
            double currentDiff = Math.abs(obj.getGridX() - average);
            if (currentDiff < minDiff) {
                minDiff = currentDiff;
                closest = obj;
            }
        }

        return closest;
    }

    /**
     * 第一步：查询DW_NODE_CONFIG表的基础信息
     */
    private List<DWNodeVo> getDefaultDWNodeVoList() {

        List<DWNodeVo> result = new ArrayList<>();
        String sql = "SELECT * FROM DW_NODE_CONFIG where IS_DELETED IS NULL OR IS_DELETED!='1'";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        WTConnection wtconnection = null;
        try {
            MethodContext methodcontext = MethodContext.getContext();
            wtconnection = (WTConnection) methodcontext.getConnection();
            pstmt = wtconnection.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                DWNodeVo dwNodeVo = new DWNodeVo();
                dwNodeVo.setId(rs.getString("ID"));
                dwNodeVo.setType(rs.getString("NODE_TYPE"));
                dwNodeVo.setText(rs.getString("NODE_TEXT"));
                dwNodeVo.setOrder(rs.getInt("NODE_ORDER"));
                String strProperties = rs.getString("NODE_PROPERTIES");
                if(StringUtils.isBlank(strProperties)==false)
                {
                    DWPropertiesVo dwPropertiesVo = JsonVoConverter.deserialize(strProperties, DWPropertiesVo.class);
                    dwNodeVo.setProperties(dwPropertiesVo);
                }
                result.add(dwNodeVo);
            }
            rs.close();
            pstmt.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }



    public static List<DWEdgeVo> getDefaultDWEdgeVoList() {
        List<DWEdgeVo> result = new ArrayList<>();
        String sql = "SELECT * FROM DW_EDGE_CONFIG where IS_DELETED IS NULL OR IS_DELETED!='1'";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        WTConnection wtconnection = null;
        try {
            MethodContext methodcontext = MethodContext.getContext();
            wtconnection = (WTConnection) methodcontext.getConnection();
            pstmt = wtconnection.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                DWEdgeVo dWEdgeVo = new DWEdgeVo();
                dWEdgeVo.setId(rs.getString("ID"));
                dWEdgeVo.setType(rs.getString("EDGE_TYPE"));
                dWEdgeVo.setText(rs.getString("EDGE_TEXT"));
                dWEdgeVo.setSourceNodeId(rs.getString("SOURCENODEID"));
                dWEdgeVo.setTargetNodeId(rs.getString("TARGETNODEID"));
                result.add(dWEdgeVo);
            }
            rs.close();
            pstmt.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }



    public void calculateGridYByIteration(DWGraphVo dwGraphVo) {

        Map<String, List<DWNodeVo>> parentToChildrenMap = getParentToChildrenMap(dwGraphVo);
        List<DWNodeVo> rootNodes = getRootNode(dwGraphVo);
        Queue<DWNodeVo> queue = new LinkedList<>();
        for (DWNodeVo root : rootNodes) {
            root.setGridY(0);
            queue.add(root);
        }

        while (!queue.isEmpty()) {
            DWNodeVo currentNode = queue.poll();
            int currentLevel = currentNode.getGridY();

            List<DWNodeVo> childIds = parentToChildrenMap.get(currentNode.getId());

            if(childIds!=null)
            {
                for (DWNodeVo childNode : childIds) {
                    if (childNode != null && childNode.getGridY() == -1) {  // 仅处理未计算的节点
                        childNode.setGridY(currentLevel + 1);  // 子节点层级=父层级+1
                        queue.add(childNode);  // 子节点加入队列，继续处理其子节点
                    }
                }
            }

        }
    }

    public List<DWNodeVo> getRootNode(DWGraphVo dwGraphVo) {
        Set<String> allChildIds = new HashSet<>();
        for (DWEdgeVo edge : dwGraphVo.getEdges()) {
            String childId = edge.getTargetNodeId();
            if (childId != null) {
                allChildIds.add(childId);
            }
        }
        List<DWNodeVo> rootNodes = new ArrayList<>();
        for (DWNodeVo node : dwGraphVo.getNodes()) {
            String nodeId = node.getId();
            if (nodeId != null && !allChildIds.contains(nodeId)) {
                rootNodes.add(node);
            }
        }
        return rootNodes;

    }

    /**
     * 辅助方法：根据ID查找节点（JDK 1.8兼容）
     */
    private DWNodeVo findNodeById(List<DWNodeVo> nodes, String targetId) {
        for (DWNodeVo node : nodes) {
            if (targetId.equals(node.getId())) {
                return node;
            }
        }
        return null;
    }

    private void calculateGridX(List<DWNodeVo> nodeList, List<DWEdgeVo> edges) {
        // 1. 按gridY分组（层级→该层所有节点）
        Map<Integer, List<DWNodeVo>> levelToNodes = new HashMap<>();
        int minLevel = Integer.MAX_VALUE; // 最小层级（初始值设为MAX_VALUE，保证最小层级不为-1）
        int maxLevel = Integer.MIN_VALUE;
        for (DWNodeVo node : nodeList) {
            int level = node.getGridY();
            minLevel = Math.min(minLevel, level);
            maxLevel = Math.max(maxLevel, level);

            if (level == -1) {
                continue; // 跳过未计算层级的节点（异常情况）
            }

            if (!levelToNodes.containsKey(level)) {
                levelToNodes.put(level, new ArrayList<DWNodeVo>());
            }
            levelToNodes.get(level).add(node);
        }

        Map<String, DWNodeVo> parentMap = getParentMap(nodeList, edges);
        for(int i=minLevel;i<=maxLevel;i++)
        {
            List<DWNodeVo> nodesInLevel = levelToNodes.get(i);
            if(nodesInLevel!=null)
            {
                Collections.sort(nodesInLevel, new Comparator<DWNodeVo>() {
                    @Override
                    public int compare(DWNodeVo o1, DWNodeVo o2) {
                        // 先按order升序，order相同则按id升序
                        if(o1.getOrder()<o2.getOrder())
                        {
                            return -1;
                        }else if(o1.getOrder()>o2.getOrder())
                        {
                            return 1;
                        }else
                        {
                            String id1 = o1.getId();
                            String id2 = o2.getId();
                            if(id1.length()<id2.length())
                            {
                                return -1;
                            }else if(id1.length()>id2.length())
                            {
                                return 1;
                            }else
                            {
                                int id1Int = Integer.valueOf(StringUtils.substringAfterLast(id1, DistributeConstants.ID_SPLIT_STR));
                                int id2Int = Integer.valueOf(StringUtils.substringAfterLast(id2, DistributeConstants.ID_SPLIT_STR));
                                if(id1Int<id2Int)
                                {
                                    return -1;
                                } else if (id1Int>id2Int) {
                                    return 1;
                                }else
                                {
                                    throw new RuntimeException("ID重复！" + id1);
                                }
                            }
                        }

                    }
                });
            }
            int levelXMax = 1;
            if(nodesInLevel!=null)
            {
                for (int j = 0; j < nodesInLevel.size(); j++) {
                    DWNodeVo parentNode = parentMap.get(nodesInLevel.get(j).getId());
                    if(parentNode==null)
                    {
                        nodesInLevel.get(j).setGridX(j + 1);
                    }else
                    {

                        if(levelXMax<parentNode.getGridX())
                        {
                            levelXMax = parentNode.getGridX();
                        }else
                        {
                            levelXMax = levelXMax + 1;
                        }
                        nodesInLevel.get(j).setGridX(levelXMax);
                    }

                }
            }

        }

    }


    /**
     * 子节点与父节点。仅支持一个父节点。
     * @param nodeList
     * @param edges
     * @return
     */
    public Map<String, DWNodeVo> getParentMap(List<DWNodeVo> nodeList, List<DWEdgeVo> edges) {
        Map<String, DWNodeVo> result =  new HashMap<>();
        for (DWEdgeVo edge : edges) {
            String parentId = edge.getSourceNodeId();
            String childId = edge.getTargetNodeId();

            if (parentId == null || childId == null) {
                continue;
            }

            DWNodeVo parentNode = findNodeById(nodeList, parentId);
            result.put(childId, parentNode);
        }
        return result;
    }


    public Map<String, List<DWNodeVo>> getParentToChildrenMap(DWGraphVo dWGraphVo) {
        Map<String, List<DWNodeVo>> result = new HashMap<>();

        Map<String, DWNodeVo> idToNodeMap = new HashMap<>();
        for (DWNodeVo node : dWGraphVo.getNodes()) {
            idToNodeMap.put(node.getId(), node);
        }

        for (DWEdgeVo edge : dWGraphVo.getEdges()) {
            String parentId = edge.getSourceNodeId();
            String childId = edge.getTargetNodeId();

            if (parentId == null || childId == null) {
                continue;
            }

            DWNodeVo childNode = idToNodeMap.get(childId);

            if(result.get(parentId)==null)
            {
                result.put(parentId, new ArrayList<DWNodeVo>());
            }
            result.get(parentId).add(childNode);
        }
        return result;
    }

    public void removeNode(DWGraphVo dwGraphVo, String nodeId) {
        for (Iterator<DWNodeVo> iterator = dwGraphVo.getNodes().iterator(); iterator.hasNext(); ) {
            DWNodeVo node = iterator.next();
            if (node.getId().equals(nodeId)) {
                iterator.remove();
                break;
            }
        }
    }

}
