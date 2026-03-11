package ext.casc.integrate.model;

import ext.sast.common.fc.CmPersistable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class GLErpPeiTaoPartBean implements CmPersistable {
	private static final long serialVersionUID = 1L;

	public static final String DOCOID = "docoid";
	public static final String CHILDNUMBER = "childNumber";
	public static final String CHILDNAME= "childName";
	public static final String CHILDVERSION= "childVersion";
	public static final String PARTTYPE= "partType";
	public static final String CHILDPHASE= "childPhase";
	public static final String AMOUNT= "amount";
	public static final String UNIT= "unit";
	public static final String BATCH= "batch";
	public static final String ZZCJ= "zzcj";
	public static final String SETMARK= "setmark";
	public static final String DEPT= "dept";
	public static final String TECHNUMBER="techNumber";
	public static final String TECHNAME="techName";
	public static final String PPLANNUMBER="pplanNumber";
	public static final String VERSION="version";
	public static final String PPLANTYPE="pplanType";
	public static final String ZFFLAG="zfflag";
	public static final String SYNCHTIME= "synchTime";

	private String keyId = "";
	private String docoid = "";
	private String childNumber = "";
	private String childName= "";
	private String childVersion= "";
	private String partType= "";
	private String childPhase= "";
	private String amount= "";
	private String unit= "";
	private String batch= "";
	private String zzcj= "";
	private String setmark= "";
	private String dept= "";

	private String techNumber= "";
	private String techName= "";
	private String pplanNumber= "";
	private String version="";
	private String pplanType="";

	private String zfflag="";
	private String synchTime= "";

	public String getSynchTime() {
		return synchTime;
	}

	public void setSynchTime(String synchTime) {
		this.synchTime = synchTime;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	@Override
	public String toString() {
		return this.getChildNumber() +" "+this.getAmount()+" "+this.getUnit();
	}


	@Override
	public CmPersistable getObject(ResultSet rs) throws Exception {
		if (rs != null) {
			setKeyId(rs.getString(KEY_ID));
			setDocoid(rs.getString(DOCOID));
			setChildNumber(rs.getString(CHILDNUMBER));
			setChildName(rs.getString(CHILDNAME));
			setChildVersion(rs.getString(CHILDVERSION));
			setPartType(rs.getString(PARTTYPE));
			setChildPhase(rs.getString(CHILDPHASE));
			setAmount(rs.getString(AMOUNT ));
			setUnit(rs.getString(UNIT));
			setBatch(rs.getString(BATCH));
			setZzcj(rs.getString(ZZCJ));
			setSetmark(rs.getString(SETMARK));
			setDept(rs.getString(DEPT));
			setTechNumber(rs.getString(TECHNUMBER));
			setTechName(rs.getString(TECHNAME));
			setPplanNumber(rs.getString(PPLANNUMBER));
			setVersion(rs.getString(VERSION));
			setPplanType(rs.getString(PPLANTYPE));
			setZfflag(rs.getString(ZFFLAG));
			setSynchTime(rs.getString(SYNCHTIME));

		}
		return this;
	}

	@Override
	public Object getKeyId() {
		return this.keyId;
	}

	@Override
	public Map getUpdateMap() {
		HashMap ret = new HashMap();
		ret.put(KEY_ID, keyId);
		ret.put(DOCOID, docoid);
		ret.put(CHILDNUMBER, childNumber);
		ret.put(CHILDNAME, childName);
		ret.put(CHILDVERSION, childVersion);
		ret.put(PARTTYPE, partType);
		ret.put(CHILDPHASE, childPhase);
		ret.put(AMOUNT, amount);
		ret.put(UNIT, unit);
		ret.put(BATCH, batch);
		ret.put(ZZCJ, zzcj);
		ret.put(SETMARK, setmark);
		ret.put(DEPT, dept);
		ret.put(TECHNUMBER, techNumber);
		ret.put(TECHNAME, techName);
		ret.put(PPLANNUMBER, pplanNumber);
		ret.put(VERSION, version);
		ret.put(PPLANTYPE, pplanType);
		ret.put(ZFFLAG, zfflag);
		ret.put(SYNCHTIME,synchTime);

		return ret;
	}

	@Override
	public Map getCreateMap() {
		HashMap ret = new HashMap();
		ret.put(KEY_ID, keyId);
		ret.put(DOCOID, docoid);
		ret.put(CHILDNUMBER, childNumber);
		ret.put(CHILDNAME, childName);
		ret.put(CHILDVERSION, childVersion);
		ret.put(PARTTYPE, partType);
		ret.put(CHILDPHASE, childPhase);
		ret.put(AMOUNT, amount);
		ret.put(UNIT, unit);
		ret.put(BATCH, batch);
		ret.put(ZZCJ, zzcj);
		ret.put(SETMARK, setmark);
		ret.put(DEPT, dept);
		ret.put(TECHNUMBER, techNumber);
		ret.put(TECHNAME, techName);
		ret.put(PPLANNUMBER, pplanNumber);
		ret.put(VERSION, version);
		ret.put(PPLANTYPE, pplanType);
		ret.put(ZFFLAG, zfflag);
		ret.put(SYNCHTIME,synchTime);

		return ret;
	}

	public String getDocoid() {
		return docoid;
	}

	public void setDocoid(String docoid) {
		this.docoid = docoid;
	}

	public String getChildNumber() {
		return childNumber;
	}

	public void setChildNumber(String childNumber) {
		this.childNumber = childNumber;
	}

	public String getChildName() {
		return childName;
	}

	public void setChildName(String childName) {
		this.childName = childName;
	}

	public String getChildVersion() {
		return childVersion;
	}

	public void setChildVersion(String childVersion) {
		this.childVersion = childVersion;
	}

	public String getPartType() {
		return partType;
	}

	public void setPartType(String partType) {
		this.partType = partType;
	}

	public String getChildPhase() {
		return childPhase;
	}

	public void setChildPhase(String childPhase) {
		this.childPhase = childPhase;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getZzcj() {
		return zzcj;
	}

	public void setZzcj(String zzcj) {
		this.zzcj = zzcj;
	}

	public String getSetmark() {
		return setmark;
	}

	public void setSetmark(String setmark) {
		this.setmark = setmark;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public String getTechNumber() {
		return techNumber;
	}

	public void setTechNumber(String techNumber) {
		this.techNumber = techNumber;
	}

	public String getTechName() {
		return techName;
	}

	public void setTechName(String techName) {
		this.techName = techName;
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

	public String getPplanType() {
		return pplanType;
	}

	public void setPplanType(String pplanType) {
		this.pplanType = pplanType;
	}

	public String getZfflag() {
		return zfflag;
	}

	public void setZfflag(String zfflag) {
		this.zfflag = zfflag;
	}
}
