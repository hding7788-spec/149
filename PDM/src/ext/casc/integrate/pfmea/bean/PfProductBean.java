package ext.casc.integrate.pfmea.bean;

import java.util.List;

public class PfProductBean {

    private int id;

    private String pindex;

    private String pname;

    private String phase;

    private String templateid;

    private String error;

    private List<PfPartBean> pfPartBeanList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPindex() {
        return pindex;
    }

    public void setPindex(String pindex) {
        this.pindex = pindex;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getTemplateid() {
        return templateid;
    }

    public void setTemplateid(String templateid) {
        this.templateid = templateid;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<PfPartBean> getPfPartBeanList() {
        return pfPartBeanList;
    }

    public void setPfPartBeanList(List<PfPartBean> pfPartBeanList) {
        this.pfPartBeanList = pfPartBeanList;
    }
}
