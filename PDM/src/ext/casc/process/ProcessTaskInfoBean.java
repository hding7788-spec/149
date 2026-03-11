package ext.casc.process;

import java.util.Date;

public class ProcessTaskInfoBean {
    private String taskType = "";
    private String taskState = "";
    private String taskComments = "";
    private String partNumber = "";
    private String partName = "";
    private String chejian = "";
    private String zhuorfuchejian = "";
    private String gongyiyuan = "";
    private Date jihuadate = null;
    private Date shijidate = null;

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public String getTaskComments() {
        return taskComments;
    }

    public void setTaskComments(String taskComments) {
        this.taskComments = taskComments;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getChejian() {
        return chejian;
    }

    public void setChejian(String chejian) {
        this.chejian = chejian;
    }

    public String getZhuorfuchejian() {
        return zhuorfuchejian;
    }

    public void setZhuorfuchejian(String zhuorfuchejian) {
        this.zhuorfuchejian = zhuorfuchejian;
    }

    public String getGongyiyuan() {
        return gongyiyuan;
    }

    public void setGongyiyuan(String gongyiyuan) {
        this.gongyiyuan = gongyiyuan;
    }

    public Date getJihuadate() {
        return jihuadate;
    }

    public void setJihuadate(Date jihuadate) {
        this.jihuadate = jihuadate;
    }

    public Date getShijidate() {
        return shijidate;
    }

    public void setShijidate(Date shijidate) {
        this.shijidate = shijidate;
    }

}
