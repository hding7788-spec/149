package ext.casc.workflow.signtrue.zp;

public class SignatureRecord {
    private String empoid;//emp
    private String advise;//会签意见
    private String result;//会签结论
    private String zpr;//指派人
    private String persons;//会签人员oid
    private String personsDis;//会签人员展示全称
    private String version;
    private String reassign;
    private String zhuzhichejian;
    private String fuzhichejian;
    
    public SignatureRecord(){}
    public SignatureRecord(String empoid, String advise, String result, String persons, String personsDis, String zpr,String version,String reassign) {
        this.empoid = empoid;
        this.advise = advise;
        this.result = result;
        this.persons = persons;
        this.personsDis = personsDis;
        this.zpr = zpr;
        this.version = version;
        this.reassign = reassign;
    }
    public SignatureRecord(String empoid, String advise, String result, String persons, String personsDis, String zpr,String version,String reassign,String zhuzhichejian,String fuzhichejian) {
        this.empoid = empoid;
        this.advise = advise;
        this.result = result;
        this.persons = persons;
        this.personsDis = personsDis;
        this.zpr = zpr;
        this.version = version;
        this.reassign = reassign;
        this.zhuzhichejian = zhuzhichejian;
        this.fuzhichejian = fuzhichejian;
    }
    
	public String getZhuzhichejian() {
		return zhuzhichejian;
	}
	public void setZhuzhichejian(String zhuzhichejian) {
		this.zhuzhichejian = zhuzhichejian;
	}
	public String getFuzhichejian() {
		return fuzhichejian;
	}
	public void setFuzhichejian(String fuzhichejian) {
		this.fuzhichejian = fuzhichejian;
	}
	public String getReassign() {
		return reassign;
	}
	public void setReassign(String reassign) {
		this.reassign = reassign;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getZpr() {
        return zpr; 
    }
    public void setZpr(String zpr) {
        this.zpr = zpr;
    }
    public String getEmpoid() {
        return empoid;
    }
    public void setEmpoid(String empoid) {
        this.empoid = empoid;
    }
    public String getAdvise() {
        return advise;
    }
    public void setAdvise(String advise) {
        this.advise = advise;
    }
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public String getPersons() {
        return persons;
    }
    public void setPersons(String persons) {
        this.persons = persons;
    }
    public String getPersonsDis() {
        return personsDis;
    }
    public void setPersonsDis(String personsDis) {
        this.personsDis = personsDis;
    }
    
}
