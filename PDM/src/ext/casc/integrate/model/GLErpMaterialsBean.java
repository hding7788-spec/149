package ext.casc.integrate.model;

import ext.sast.common.fc.CmPersistable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class GLErpMaterialsBean implements CmPersistable {
	private static final long serialVersionUID = 1L;

	public static final String DOCOID = "docoid";
	public static final String PPLANNUMBER = "pplanNumber";
	public static final String PPLANNAME= "pplanName";
	public static final String PPLANVERSION= "pplanVersion";
	public static final String MATERIALTYPE= "materialType";
	public static final String USINGTYPE= "usingType";
	public static final String PPLANSTATE= "pplanState";
	public static final String CHBM= "chbm";
	public static final String CHMC= "chmc";
	public static final String XLCC= "xlcc";
	public static final String KZJS= "kzjs";
	public static final String SJCC= "sjcc";
	public static final String SJKZJS= "sjkzjs";
	public static final String SJSL= "sjsl";
	public static final String SL= "sl";
	public static final String ZJLDW= "zjldw";
	public static final String DW= "dw";
	public static final String DATAFROM= "dataFrom";
	public static final String NOTE= "note";
	public static final String PROCESSFILENUMBER= "processFileNumber";
	public static final String PPLANTYPE= "pplanType";
	public static final String ZFFLAG= "zfflag";
	public static final String DEPT= "dept";
	public static final String SYNCHTIME= "synchTime";


	private String keyId = "";
	private String docoid = "";
	// 工艺文件编号
	private String pplanNumber = "";
	// 工艺文件名称
	private String pplanName= "";
	// 工艺文件版本
	private String pplanVersion= "";
	// 工艺文件状态
	private String pplanState= "";
	// 材料类别：外购件
	private String materialType= "";
	// 材料定额类别：主要材料定额、原材料类别、试件材料定额
	private String usingType= "";
	// 存货编码
	private String chbm= "";
	// 存货名称
	private String chmc= "";
	// 下料尺寸
	private String xlcc= "";
	// 可制件数
	private String kzjs= "";
	// 试件尺寸
	private String sjcc= "";
	// 试件可制件数
	private String sjkzjs= "";
	// 试件数量
	private String sjsl= "";
	// 主要材料数量
	private String sl= "";
	// 主计量单位
	private String zjldw= "";
	//单位
	private String dw= "";
	//数据源
	private String dataFrom= "";
//备注
	private String note= "";
	private String processFileNumber= "";
	private String pplanType= "";
	private String zfflag= "";
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

	private String dept= "";
	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

    public String getDocoid() {
        return docoid;
    }

    public void setDocoid(String docoid) {
        this.docoid = docoid;
    }

    public String getZfflag() {
		return zfflag;
	}


	public void setZfflag(String zfflag) {
		this.zfflag = zfflag;
	}


	public String getPplanType() {
		return pplanType;
	}


	public void setPplanType(String pplanType) {
		this.pplanType = pplanType;
	}

	//批次
	private String batch;
	@Override
	public String toString() {
		return "chbm:"
				+ chbm + "pplanNumber:" + pplanNumber + "  pplanName:" + pplanName
				+ "  pplanVersion:" + pplanVersion
				+ "  sl:" + sl  + " dw:"+ dw ;
	}


	public String getProcessFileNumber() {
		return processFileNumber;
	}


	public void setProcessFileNumber(String processFileNumber) {
		this.processFileNumber = processFileNumber;
	}


	public String getBatch() {
		return batch;
	}


	public void setBatch(String batch) {
		this.batch = batch;
	}


	public String getDw() {
		return dw;
	}

	public void setDw(String dw) {
		this.dw = dw;
	}

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPplanNumber() {
		return pplanNumber;
	}

	public void setPplanNumber(String pplanNumber) {
		this.pplanNumber = pplanNumber;
	}

	public String getPplanName() {
		return pplanName;
	}

	public void setPplanName(String pplanName) {
		this.pplanName = pplanName;
	}

	public String getPplanVersion() {
		return pplanVersion;
	}

	public void setPplanVersion(String pplanVersion) {
		this.pplanVersion = pplanVersion;
	}

	public String getPplanState() {
		return pplanState;
	}

	public void setPplanState(String pplanState) {
		this.pplanState = pplanState;
	}

	public String getMaterialType() {
		return materialType;
	}

	public void setMaterialType(String materialType) {
		this.materialType = materialType;
	}


	public String getUsingType() {
		return usingType;
	}

	public void setUsingType(String usingType) {
		this.usingType = usingType;
	}


	public String getChbm() {
		return chbm;
	}

	public void setChbm(String chbm) {
		this.chbm = chbm;
	}

	public String getChmc() {
		return chmc;
	}

	public void setChmc(String chmc) {
		this.chmc = chmc;
	}

	public String getXlcc() {
		return xlcc;
	}

	public void setXlcc(String xlcc) {
		this.xlcc = xlcc;
	}

	public String getKzjs() {
		return kzjs;
	}

	public void setKzjs(String kzjs) {
		this.kzjs = kzjs;
	}

	public String getSjcc() {
		return sjcc;
	}

	public void setSjcc(String sjcc) {
		this.sjcc = sjcc;
	}

	public String getSjkzjs() {
		return sjkzjs;
	}

	public void setSjkzjs(String sjkzjs) {
		this.sjkzjs = sjkzjs;
	}

	public String getSjsl() {
		return sjsl;
	}

	public void setSjsl(String sjsl) {
		this.sjsl = sjsl;
	}

	public String getSl() {
		return sl;
	}

	public void setSl(String sl) {
		this.sl = sl;
	}

	public String getZjldw() {
		return zjldw;
	}

	public void setZjldw(String zjldw) {
		this.zjldw = zjldw;
	}

	public String getDataFrom() {
		return dataFrom;
	}

	public void setDataFrom(String dataFrom) {
		this.dataFrom = dataFrom;
	}

	@Override
	public CmPersistable getObject(ResultSet rs) throws Exception {
		if (rs != null) {
			setKeyId(rs.getString(KEY_ID));
			setDocoid(rs.getString(DOCOID));
			setPplanNumber(rs.getString(PPLANNUMBER));
			setPplanName(rs.getString(PPLANNAME));
			setPplanVersion(rs.getString(PPLANVERSION));
			setPplanState(rs.getString(PPLANSTATE));
			setMaterialType(rs.getString(MATERIALTYPE));
			setUsingType(rs.getString(USINGTYPE ));
			setChbm(rs.getString(CHBM));
			setChmc(rs.getString(CHMC));
			setXlcc(rs.getString(XLCC));
			setKzjs(rs.getString(KZJS));
			setSjcc(rs.getString(SJCC));
			setSjkzjs(rs.getString(SJKZJS ));
			setSjsl(rs.getString(SJSL));
			setSl(rs.getString(SL));
			setDw(rs.getString(DW));
			setZjldw(rs.getString(ZJLDW));
			setDataFrom(rs.getString(DATAFROM));
			setProcessFileNumber(rs.getString(PROCESSFILENUMBER));
			setPplanType(rs.getString(PPLANTYPE));
			setZfflag(rs.getString(ZFFLAG));
			setDept(rs.getString(DEPT));
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
		ret.put(PPLANNUMBER, pplanNumber);
		ret.put(PPLANNAME, pplanName);
		ret.put(PPLANVERSION, pplanVersion);
		ret.put(PPLANSTATE, pplanState);
		ret.put(MATERIALTYPE, materialType);
		ret.put(USINGTYPE, usingType);
		ret.put(CHBM, chbm);
		if(chmc!=null && chmc.length()<600){
			ret.put(CHMC, chmc);
		}
		ret.put(XLCC, xlcc);
		ret.put(KZJS, kzjs);
		ret.put(SJCC, sjcc);
		ret.put(SJKZJS, sjkzjs);
		ret.put(SJSL, sjsl);
		ret.put(SL, sl);
		ret.put(ZJLDW, zjldw);
		ret.put(DW, dw);
		ret.put(DATAFROM,dataFrom);
		ret.put(NOTE, note);
		ret.put(PROCESSFILENUMBER, processFileNumber);
		ret.put(PPLANTYPE,pplanType);
		ret.put(ZFFLAG, zfflag);
		ret.put(DEPT, dept);
		ret.put(SYNCHTIME,synchTime);

		return ret;
	}

	@Override
	public Map getCreateMap() {
		HashMap ret = new HashMap();
		ret.put(KEY_ID, keyId);
		ret.put(DOCOID, docoid);
		ret.put(PPLANNUMBER, pplanNumber);
		ret.put(PPLANNAME, pplanName);
		ret.put(PPLANVERSION, pplanVersion);
		ret.put(PPLANSTATE, pplanState);
		ret.put(MATERIALTYPE, materialType);
		ret.put(USINGTYPE, usingType);
		ret.put(CHBM, chbm);

		if(chmc!=null && chmc.length()<100){
			ret.put(CHMC, chmc);
		}

		ret.put(XLCC, xlcc);
		ret.put(KZJS, kzjs);
		ret.put(SJCC, sjcc);
		ret.put(SJKZJS, sjkzjs);
		ret.put(SJSL, sjsl);
		ret.put(SL, sl);
		ret.put(ZJLDW, zjldw);
		ret.put(DW, dw);
		ret.put(DATAFROM,dataFrom);
		ret.put(NOTE, note);
		ret.put(PROCESSFILENUMBER, processFileNumber);
		ret.put(PPLANTYPE,pplanType);
		ret.put(ZFFLAG, zfflag);
		ret.put(DEPT, dept);
		ret.put(SYNCHTIME,synchTime);
		return ret;
	}
}
