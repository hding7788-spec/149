package ext.casc.fileprint.cache;

import ext.sast.common.fc.CmPersistable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class GLFilePrintData implements CmPersistable {

	private static final long serialVersionUID = 900302062399922488L;

	public static final String DOCNUMBER = "docNumber";
	public static final String DOCVERSION = "docVersion";
	public static final String PRINTDATA = "printData";


	private String keyId = "";
	private String docNumber = "";
	private String docVersion = "";
	private String printData = "";

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	public String getDocVersion() {
		return docVersion;
	}

	public void setDocVersion(String docVersion) {
		this.docVersion = docVersion;
	}

	public String getPrintData() {
		return printData;
	}

	public void setPrintData(String printData) {
		this.printData = printData;
	}

	@Override
	public CmPersistable getObject(ResultSet rs) throws Exception {
		if (rs != null) {
			setKeyId(rs.getString(KEY_ID));
			setDocNumber(rs.getString(DOCNUMBER));
			setDocVersion(rs.getString(DOCVERSION));
			setPrintData(rs.getString(PRINTDATA));
		}
		return this;
	}

	@Override
	public Object getKeyId() {
		// TODO Auto-generated method stub
		return this.keyId;
	}

	@Override
	public Map getUpdateMap() {
		HashMap ret = new HashMap();
		ret.put(KEY_ID, keyId);
		ret.put(DOCNUMBER, docNumber);
		ret.put(DOCVERSION, docVersion);
		ret.put(PRINTDATA, printData);
		return ret;
	}

	@Override
	public Map getCreateMap() {
		HashMap ret = new HashMap();
		ret.put(KEY_ID, keyId);
		ret.put(DOCNUMBER, docNumber);
		ret.put(DOCVERSION, docVersion);
		ret.put(PRINTDATA, printData);
		return ret;
	}

}
