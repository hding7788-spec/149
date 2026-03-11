package ext.casc.distribute.vo;

public class DWNodeVo {
    private String id;
    private String type;
    private String text;

    private DWPropertiesVo properties;

    private int order;

    private int gridX = -1;
    private int gridY = -1;


    public DWNodeVo() {
    }

    public DWNodeVo(String id, String type, String text, DWPropertiesVo properties) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.properties = properties;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public DWPropertiesVo getProperties() {
        return properties;
    }

    public void setProperties(DWPropertiesVo properties) {
        this.properties = properties;
    }

    public int getGridX() {
        return gridX;
    }

    public void setGridX(int gridX) {
        this.gridX = gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public void setGridY(int gridY) {
        this.gridY = gridY;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
