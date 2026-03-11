package ext.casc.distribute.vo;

public class DWStyleVo {
    private String fill;
    private String stroke;

    public DWStyleVo() {
    }

    public DWStyleVo(String fill, String stroke) {
        this.fill = fill;
        this.stroke = stroke;
    }

    public String getFill() {
        return fill;
    }

    public void setFill(String fill) {
        this.fill = fill;
    }

    public String getStroke() {
        return stroke;
    }

    public void setStroke(String stroke) {
        this.stroke = stroke;
    }
}
