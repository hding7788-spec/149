package ext.casc.distribute.vo;

public class DWPropertiesVo {
    private DWStyleVo style;
    private Integer r;
    private Integer rx;
    private Integer ry;
    private Integer width;
    private Integer height;

    private String head;
    private String taskName;
    private String taskType;
    private String taskOwner;
    private String taskStartDate;
    private String taskEndDate;
    private String taskState;

    private String conditionItemType;
    private String conditionCount;

    private String instanceCountRange;

    private String taskItemObjectNumber;

    private String quantity; //数量
    private String actualQuantity; //实际处理数量
    private String extNumber; //不合格品单号,报废单号/返修计划号, 计划号
    private String repairProcessNumber; //返修工艺编号

    private String branchFlowCurrentNodeHead;
    private String branchFlowCurrentNodeName;
    private String branchFlowCurrentNodeState;
    private String branchFlowCurrentNodeOwner;
    private String branchFlowCurrentNodeStartDate;
    private String branchFlowCurrentNodeEndDate;
    private String branchFlowCurrentCard;

    public DWPropertiesVo() {
    }

    public DWStyleVo getStyle() {
        return style;
    }

    public void setStyle(DWStyleVo style) {
        this.style = style;
    }

    public Integer getR() {
        return r;
    }

    public void setR(Integer r) {
        this.r = r;
    }

    public Integer getRx() {
        return rx;
    }

    public void setRx(Integer rx) {
        this.rx = rx;
    }

    public Integer getRy() {
        return ry;
    }

    public void setRy(Integer ry) {
        this.ry = ry;
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

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskOwner() {
        return taskOwner;
    }

    public void setTaskOwner(String taskOwner) {
        this.taskOwner = taskOwner;
    }

    public String getTaskStartDate() {
        return taskStartDate;
    }

    public void setTaskStartDate(String taskStartDate) {
        this.taskStartDate = taskStartDate;
    }

    public String getTaskEndDate() {
        return taskEndDate;
    }

    public void setTaskEndDate(String taskEndDate) {
        this.taskEndDate = taskEndDate;
    }

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

    public String getTaskItemObjectNumber() {
        return taskItemObjectNumber;
    }

    public void setTaskItemObjectNumber(String taskItemObjectNumber) {
        this.taskItemObjectNumber = taskItemObjectNumber;
    }

    public String getConditionItemType() {
        return conditionItemType;
    }

    public void setConditionItemType(String conditionItemType) {
        this.conditionItemType = conditionItemType;
    }

    public String getConditionCount() {
        return conditionCount;
    }

    public void setConditionCount(String conditionCount) {
        this.conditionCount = conditionCount;
    }

    public String getInstanceCountRange() {
        return instanceCountRange;
    }

    public void setInstanceCountRange(String instanceCountRange) {
        this.instanceCountRange = instanceCountRange;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(String actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public String getExtNumber() {
        return extNumber;
    }

    public void setExtNumber(String extNumber) {
        this.extNumber = extNumber;
    }

    public String getRepairProcessNumber() {
        return repairProcessNumber;
    }

    public void setRepairProcessNumber(String repairProcessNumber) {
        this.repairProcessNumber = repairProcessNumber;
    }

    public String getBranchFlowCurrentNodeHead() {
        return branchFlowCurrentNodeHead;
    }

    public void setBranchFlowCurrentNodeHead(String branchFlowCurrentNodeHead) {
        this.branchFlowCurrentNodeHead = branchFlowCurrentNodeHead;
    }

    public String getBranchFlowCurrentNodeName() {
        return branchFlowCurrentNodeName;
    }

    public void setBranchFlowCurrentNodeName(String branchFlowCurrentNodeName) {
        this.branchFlowCurrentNodeName = branchFlowCurrentNodeName;
    }

    public String getBranchFlowCurrentNodeState() {
        return branchFlowCurrentNodeState;
    }

    public void setBranchFlowCurrentNodeState(String branchFlowCurrentNodeState) {
        this.branchFlowCurrentNodeState = branchFlowCurrentNodeState;
    }

    public String getBranchFlowCurrentNodeOwner() {
        return branchFlowCurrentNodeOwner;
    }

    public void setBranchFlowCurrentNodeOwner(String branchFlowCurrentNodeOwner) {
        this.branchFlowCurrentNodeOwner = branchFlowCurrentNodeOwner;
    }

    public String getBranchFlowCurrentNodeStartDate() {
        return branchFlowCurrentNodeStartDate;
    }

    public void setBranchFlowCurrentNodeStartDate(String branchFlowCurrentNodeStartDate) {
        this.branchFlowCurrentNodeStartDate = branchFlowCurrentNodeStartDate;
    }

    public String getBranchFlowCurrentNodeEndDate() {
        return branchFlowCurrentNodeEndDate;
    }

    public void setBranchFlowCurrentNodeEndDate(String branchFlowCurrentNodeEndDate) {
        this.branchFlowCurrentNodeEndDate = branchFlowCurrentNodeEndDate;
    }

    public String getBranchFlowCurrentCard() {
        return branchFlowCurrentCard;
    }

    public void setBranchFlowCurrentCard(String branchFlowCurrentCard) {
        this.branchFlowCurrentCard = branchFlowCurrentCard;
    }
}
