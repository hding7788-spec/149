package ext.casc.integrate.pfmea.bean;

import java.util.List;

public class PfPartBean {

    private int id;

    private String partnumber;

    private String partid;

    private String partversion;

    private String partname;

    private String fpartnumber;

    private String fpartid;

//    private List<PfProcessBean> processBeanList;

    private List<PfDocBean> docBeanList;

    private int pid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPartnumber() {
        return partnumber;
    }

    public void setPartnumber(String partnumber) {
        this.partnumber = partnumber;
    }

    public String getPartid() {
        return partid;
    }

    public void setPartid(String partid) {
        this.partid = partid;
    }

    public String getPartversion() {
        return partversion;
    }

    public void setPartversion(String partversion) {
        this.partversion = partversion;
    }

    public String getPartname() {
        return partname;
    }

    public void setPartname(String partname) {
        this.partname = partname;
    }

    public String getFpartnumber() {
        return fpartnumber;
    }

    public void setFpartnumber(String fpartnumber) {
        this.fpartnumber = fpartnumber;
    }

    public String getFpartid() {
        return fpartid;
    }

    public void setFpartid(String fpartid) {
        this.fpartid = fpartid;
    }

//    public List<PfProcessBean> getProcessBeanList() {
//        return processBeanList;
//    }
//
//    public void setProcessBeanList(List<PfProcessBean> processBeanList) {
//        this.processBeanList = processBeanList;
//    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public List<PfDocBean> getDocBeanList() {
        return docBeanList;
    }

    public void setDocBeanList(List<PfDocBean> docBeanList) {
        this.docBeanList = docBeanList;
    }
}
