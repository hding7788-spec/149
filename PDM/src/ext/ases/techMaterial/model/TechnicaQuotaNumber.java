package ext.ases.techMaterial.model;

import ext.ases.techMaterial.gwpersistable.GwPersistable;
import wt.fc.InvalidAttributeException;
import wt.fc.PersistInfo;
import wt.introspection.ClassInfo;
import wt.introspection.WTIntrospectionException;
import wt.part.WTPart;
import wt.pds.PersistentRetrieveIfc;
import wt.pds.PersistentStoreIfc;
import wt.pom.DatastoreException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author Mchen
 * 
 */
public class TechnicaQuotaNumber extends WTPart implements GwPersistable {

	private static final long serialVersionUID = 1L;

	/**
	 * GWKEYID WFOID USERNAME WORKITEMOID SELECT ADVISE
	 */

	public static String GWKEYID = "GWKEYID";// id
	public static String TECHNICSOID = "TECHNICSOID";// 物资oid
	public static String QUOTANUMBER = "QUOTANUMBER";//物资编码、工艺物资条目编号
	public static String TECHMATERIALTYPE = "TECHMATERIALTYPE";// 类型
	public static String TMCREATETIME = "TMCREATETIME";// 创建时间
	public static String TMCREATOR = "TMCREATOR";// 创建人

	/**
	 */
	private String gwKey;
	private String technicsoid;
	public String quotanumber;
	public String techmaterialtype;
	public String tmcreatetime;
	public String tmcreator;
	

	

	public static int index = 1;

	public static String generateKeyId() {
		if (index >= 10000) {
			index = 1;
		}
		return String.valueOf(System.currentTimeMillis()) + index++;
	}
	
	public String getOid() {
		// TODO Auto-generated method stub
		return gwKey;

	}

	@Override
	public GwPersistable getObject(ResultSet rs) throws Exception {
		if (rs != null) {
			this.setGwKey(rs.getString(GWKEYID));
			this.setTechnicsoid(rs.getString(TECHNICSOID));
			this.setQuotanumber(rs.getString(QUOTANUMBER));
			this.setTechmaterialtype(rs.getString(TECHMATERIALTYPE));
			this.setTmcreatetime(rs.getString(TMCREATETIME));
			this.setTmcreator(rs.getString(TMCREATOR));
		}
		return this;
	}

	@Override
	public Object getKeyId() {
		if (this.gwKey != null && !this.gwKey.equals("")) {
			return this.gwKey;
		} else {
			String keyId = generateKeyId();
			this.setGwKey(keyId);
			return keyId;
		}
	}

	@Override
	public Map<String, Object> getUpdateMap() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(GWKEYID, gwKey);
		ret.put(TECHNICSOID, technicsoid);
		ret.put(QUOTANUMBER, quotanumber);
		ret.put(TECHMATERIALTYPE, techmaterialtype);
		ret.put(TMCREATETIME, tmcreatetime);
		ret.put(TMCREATOR, tmcreator);
		return ret;
	}

	@Override
	public Map<?, ?> getCreateMap() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(GWKEYID, gwKey);
		ret.put(TECHNICSOID, technicsoid);
		ret.put(QUOTANUMBER, quotanumber);
		ret.put(TECHMATERIALTYPE, techmaterialtype);
		ret.put(TMCREATETIME, tmcreatetime);
		ret.put(TMCREATOR, tmcreator);
		return ret;
	}


	public String getTechmaterialtype() {
		return techmaterialtype;
	}

	public void setTechmaterialtype(String techmaterialtype) {
		this.techmaterialtype = techmaterialtype;
	}

	public String getGwKey() {
		return gwKey;
	}

	public void setGwKey(String gwKey) {
		this.gwKey = gwKey;
	}
	

	public void checkAttributes() throws InvalidAttributeException {
		// TODO Auto-generated method stub
	}

	public String getIdentity() {
		// TODO Auto-generated method stub
		return this.gwKey;
	}

	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public PersistInfo getPersistInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPersistInfo(PersistInfo var1) {
		// TODO Auto-generated method stub
		
	}

	public void readExternal(PersistentRetrieveIfc var1) throws SQLException,
			DatastoreException {
		// TODO Auto-generated method stub
		
	}

	public void writeExternal(PersistentStoreIfc var1) throws SQLException,
			DatastoreException {
		// TODO Auto-generated method stub
		
	}

	public ClassInfo getClassInfo() throws WTIntrospectionException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getConceptualClassname() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTechnicsoid() {
		return technicsoid;
	}

	public void setTechnicsoid(String technicsoid) {
		this.technicsoid = technicsoid;
	}

	public String getQuotanumber() {
		return quotanumber;
	}

	public void setQuotanumber(String quotanumber) {
		this.quotanumber = quotanumber;
	}

	public String getTmcreatetime() {
		return tmcreatetime;
	}

	public void setTmcreatetime(String tmcreatetime) {
		this.tmcreatetime = tmcreatetime;
	}

	public String getTmcreator() {
		return tmcreator;
	}

	public void setTmcreator(String tmcreator) {
		this.tmcreator = tmcreator;
	}

	

}
