package ext.casc.sop.bean;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "QMPartInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class SOPQMPartInfoBean {

    public SOPQMPartInfoBean() {
        this.useCount = "1";
        this.occid="-1";
        this.occpath="-1";
        this.partType="WTPart";
        this.materialType="自制件";
        this.mtype="自制件";
        this.ctype="自制件";

    }

    @XmlAttribute(name = "oid")
    private String oid;

    @XmlAttribute(name = "useCount")
    private String useCount;

    @XmlAttribute(name = "occId")
    private String occid;

    @XmlAttribute(name = "occpath")
    private String occpath;

    @XmlAttribute(name = "partNumber")
    private String partNumber;

    @XmlAttribute(name = "partName")
    private String partName;

    @XmlAttribute(name = "partType")
    private String partType;

    @XmlAttribute(name = "materialType")
    private String materialType;

    @XmlAttribute(name = "lifecycle")
    private String lifecycle;

    @XmlAttribute(name = "version")
    private String version;

    @XmlAttribute(name = "MTYPE")
    private String mtype;

    @XmlAttribute(name = "CTYPE")
    private String ctype;

    @XmlAttribute(name = "SECRET")
    private String secret;

    @XmlAttribute(name = "Term")
    private String term;

    @XmlAttribute(name = "SpecializedType")
    private String specializedType;

    @XmlAttribute(name = "ProceduceName")
    private String procedureName;

    @XmlAttribute(name = "ProfessionalCode")
    private String professionalCode;

    @XmlAttribute(name = "GONGXUJIANHAO")
    private String gxjh;

    @XmlAttribute(name = "ZZCJ")
    private String dept;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getUseCount() {
        return useCount;
    }

    public void setUseCount(String useCount) {
        this.useCount = useCount;
    }

    public String getOccid() {
        return occid;
    }

    public void setOccid(String occid) {
        this.occid = occid;
    }

    public String getOccpath() {
        return occpath;
    }

    public void setOccpath(String occpath) {
        this.occpath = occpath;
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

    public String getPartType() {
        return partType;
    }

    public void setPartType(String partType) {
        this.partType = partType;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(String lifecycle) {
        this.lifecycle = lifecycle;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMtype() {
        return mtype;
    }

    public void setMtype(String mtype) {
        this.mtype = mtype;
    }

    public String getCtype() {
        return ctype;
    }

    public void setCtype(String ctype) {
        this.ctype = ctype;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getSpecializedType() {
        return specializedType;
    }

    public void setSpecializedType(String specializedType) {
        this.specializedType = specializedType;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getProfessionalCode() {
        return professionalCode;
    }

    public void setProfessionalCode(String professionalCode) {
        this.professionalCode = professionalCode;
    }

    public String getGxjh() {
        return gxjh;
    }

    public void setGxjh(String gxjh) {
        this.gxjh = gxjh;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }
}
