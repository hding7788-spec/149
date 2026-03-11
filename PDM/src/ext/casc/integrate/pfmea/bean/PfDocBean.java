package ext.casc.integrate.pfmea.bean;

import java.util.List;

public class PfDocBean {
    private int id;
    private String name;
    private String docid;
    private String pardid;
    private int pid;
    private List<PfProcessBean> processBeanList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getPardid() {
        return pardid;
    }

    public void setPardid(String pardid) {
        this.pardid = pardid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public List<PfProcessBean> getProcessBeanList() {
        return processBeanList;
    }

    public void setProcessBeanList(List<PfProcessBean> processBeanList) {
        this.processBeanList = processBeanList;
    }
}
