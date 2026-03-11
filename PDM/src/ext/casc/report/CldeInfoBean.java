package ext.casc.report;

public class CldeInfoBean {

    /**
     * 工艺文件流水号
     */
    private String technicsNumber;

    /**
     * 工艺文件名称
     */
    private String technicsName;

    /**
     * 工艺文件编号
     */
    private String pplanNumber;

    /**
     * 文件版本号
     */
    private String version;

    /**
     * 部件编号
     */
    private String partNumber;

    /**
     * 部件名称
     */
    private String partName;

    /**
     * 材料定额计划开始时间
     */
    private String startTime;

    /**
     * 材料定额计划完成时间
     */
    private String planTime;

    /**
     * 材料定额实际完成时间
     */
    private String endTime;

    /**
     * 材料定额完成状态
     */
    private String completeState;

    /**
     * 材料定额是否超期
     */
    private String isOverDate;

    /**
     * 材料定额签审包编号
     */
    private String packetNumber;

    public String getTechnicsNumber() {
        return technicsNumber;
    }

    public void setTechnicsNumber(String technicsNumber) {
        this.technicsNumber = technicsNumber;
    }

    public String getTechnicsName() {
        return technicsName;
    }

    public void setTechnicsName(String technicsName) {
        this.technicsName = technicsName;
    }

    public String getPplanNumber() {
        return pplanNumber;
    }

    public void setPplanNumber(String pplanNumber) {
        this.pplanNumber = pplanNumber;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getPlanTime() {
        return planTime;
    }

    public void setPlanTime(String planTime) {
        this.planTime = planTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCompleteState() {
        return completeState;
    }

    public void setCompleteState(String completeState) {
        this.completeState = completeState;
    }

    public String getIsOverDate() {
        return isOverDate;
    }

    public void setIsOverDate(String isOverDate) {
        this.isOverDate = isOverDate;
    }

    public String getPacketNumber() {
        return packetNumber;
    }

    public void setPacketNumber(String packetNumber) {
        this.packetNumber = packetNumber;
    }
}
