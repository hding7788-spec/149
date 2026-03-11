package ext.casc.integrate.bean;

public class TaskBean {

    /* 系统标识*/
    private String syscode;

    /* 流程实例id*/
    private String flowid;

    /* 标题*/
    private String requestname;

    /* 流程类型名称*/
    private String workflowname;

    /* 节点名称*/
    private String nodename;

    /* PC地址*/
    private String pcurl;

    /* 流程处理状态 0：待办 2：已办 4：办结 8：抄送（待阅）*/
    private String isremark;

    /* 流程查看状态 0：未读 （注：isremark = 0 且 viewtype = 0 会给OA消息中心推送消息，对接钉钉、企业微信） 1：已读;*/
    private String viewtype;

    /* 创建人（原值）*/
    private String creator;

    /* 创建日期时间*/
    private String createdatetime;

    /* 接收人（原值）*/
    private String receiver;

    /* 接收日期时间*/
    private String receivedatetime;

    /* 时间戳*/
    private String receivets;

    public String getSyscode() {
        return syscode;
    }

    public void setSyscode(String syscode) {
        this.syscode = syscode;
    }

    public String getFlowid() {
        return flowid;
    }

    public void setFlowid(String flowid) {
        this.flowid = flowid;
    }

    public String getRequestname() {
        return requestname;
    }

    public void setRequestname(String requestname) {
        this.requestname = requestname;
    }

    public String getWorkflowname() {
        return workflowname;
    }

    public void setWorkflowname(String workflowname) {
        this.workflowname = workflowname;
    }

    public String getNodename() {
        return nodename;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    public String getPcurl() {
        return pcurl;
    }

    public void setPcurl(String pcurl) {
        this.pcurl = pcurl;
    }

    public String getIsremark() {
        return isremark;
    }

    public void setIsremark(String isremark) {
        this.isremark = isremark;
    }

    public String getViewtype() {
        return viewtype;
    }

    public void setViewtype(String viewtype) {
        this.viewtype = viewtype;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatedatetime() {
        return createdatetime;
    }

    public void setCreatedatetime(String createdatetime) {
        this.createdatetime = createdatetime;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceivedatetime() {
        return receivedatetime;
    }

    public void setReceivedatetime(String receivedatetime) {
        this.receivedatetime = receivedatetime;
    }

    public String getReceivets() {
        return receivets;
    }

    public void setReceivets(String receivets) {
        this.receivets = receivets;
    }
}