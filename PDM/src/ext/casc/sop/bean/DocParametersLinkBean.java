package ext.casc.sop.bean;

public class DocParametersLinkBean {

    /**SOP工艺文件oid*/
    private long docOid;
    /**SOP工艺关联对象oid*/
    private long parameterOid;
    /**依据文件编号*/
    private String gistNumber;
    /**依据文件名称*/
    private String gistName;
    /**SOP工艺文件流水号*/
    private String technicsNumber;
    /**SOP工艺文件编号*/
    private String ppnumber;
    /**SOP工艺文件名称*/
    private String technicsName;
    /**SOP工艺文件大版本*/
    private String technicsVersion;
    /**编制者*/
    private String bianzhizhe;
    /**SOP关联对象类型*/
    private String type;
    /**文档类型*/
    private String tecType;


    public long getDocOid() {
        return docOid;
    }

    public void setDocOid(long docOid) {
        this.docOid = docOid;
    }

    public long getParameterOid() {
        return parameterOid;
    }

    public void setParameterOid(long parameterOid) {
        this.parameterOid = parameterOid;
    }

    public String getTechnicsNumber() {
        return technicsNumber;
    }

    public void setTechnicsNumber(String technicsNumber) {
        this.technicsNumber = technicsNumber;
    }

    public String getPpnumber() {
        return ppnumber;
    }

    public void setPpnumber(String ppnumber) {
        this.ppnumber = ppnumber;
    }

    public String getTechnicsName() {
        return technicsName;
    }

    public void setTechnicsName(String technicsName) {
        this.technicsName = technicsName;
    }

    public String getTechnicsVersion() {
        return technicsVersion;
    }

    public void setTechnicsVersion(String technicsVersion) {
        this.technicsVersion = technicsVersion;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

	public String getGistNumber() {
		return gistNumber;
	}

	public void setGistNumber(String gistNumber) {
		this.gistNumber = gistNumber;
	}

	public String getGistName() {
		return gistName;
	}

	public void setGistName(String gistName) {
		this.gistName = gistName;
	}

	public String getBianzhizhe() {
		return bianzhizhe;
	}

	public void setBianzhizhe(String bianzhizhe) {
		this.bianzhizhe = bianzhizhe;
	}

	public String getTecType() {
		return tecType;
	}

	public void setTecType(String tecType) {
		this.tecType = tecType;
	}

}
