package ext.casc.analysisActivity.bean;

import cn.hutool.core.util.IdUtil;

public class JointCellBean {

    public static final String TYPE_RECT = "RECT";
    public static final String TYPE_HEAD = "HEAD";

    public JointCellBean() {
    }

    public JointCellBean(String pre, String type, Integer x, Integer y, Integer width, Integer height, String title, String header, String label) {
        this.id = IdUtil.randomUUID();
        this.pre = pre;
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.title = title;
        this.header = header;
        this.label = label;
    }

    private String id = "";
    private String pre = "";
    private String type = "";
    private Integer x = 0;
    private Integer y = 0;
    private Integer width = 0;
    private Integer height = 0;
    private String title = "";
    private String header = "";
    private String label = "";
    private String status = "已完成";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPre() {
        return pre;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
