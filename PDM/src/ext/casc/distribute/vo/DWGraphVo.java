package ext.casc.distribute.vo;

import java.util.List;

public class DWGraphVo {
    private List<DWNodeVo> nodes;
    private List<DWEdgeVo> edges;

    public DWGraphVo() {
    }

    public DWGraphVo(List<DWNodeVo> nodes, List<DWEdgeVo> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public List<DWNodeVo> getNodes() {
        return nodes;
    }

    public void setNodes(List<DWNodeVo> nodes) {
        this.nodes = nodes;
    }

    public List<DWEdgeVo> getEdges() {
        return edges;
    }

    public void setEdges(List<DWEdgeVo> edges) {
        this.edges = edges;
    }
}
