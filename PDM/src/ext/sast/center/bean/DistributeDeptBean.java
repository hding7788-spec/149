package ext.sast.center.bean;

public class DistributeDeptBean {
    private String objOid;
    private String  bjNumber;
    private String  objVersion;
    private String  department;
    private String  amount;

    public String getObjOid() {
        return objOid;
    }

    public void setObjOid(String objOid) {
        this.objOid = objOid;
    }

    public String getBjNumber() {
        return bjNumber;
    }

    public void setBjNumber(String bjNumber) {
        this.bjNumber = bjNumber;
    }

    public String getObjVersion() {
        return objVersion;
    }

    public void setObjVersion(String objVersion) {
        this.objVersion = objVersion;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
