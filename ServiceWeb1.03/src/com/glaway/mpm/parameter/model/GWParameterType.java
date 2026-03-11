package com.glaway.mpm.parameter.model;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.glaway.mpm.parameter.service.gwpersistable.GwPersistable;

/**
 * 参数类型
 *
 * @author 龙秀川
 *
 */
public class GWParameterType implements GwPersistable {

	private static final long serialVersionUID = 1L;

	public static String PARAMNUMBER = "PARAMNUMBER";
	public static String PARENT = "PARENT";
	public static String ENNAME = "ENNAME";
	public static String CHINANAME = "CHINANAME";
	public static String TECHNICSTYPE = "TECHNICSTYPE";
	public static String VALUETYPE = "VALUETYPE";

	private String gwKey;
	private String number;
	private String parent;
	private String enname;
	private String chinaname;
	private String technicstype;
	private String valuetype;

	public static int index = 1;

	public static String generateKeyId() {
		if (index >= 10000) {
			index = 1;
		}
		return String.valueOf(System.currentTimeMillis()) + index++;
	}

	@Override
	public GwPersistable getObject(ResultSet rs) throws Exception {
		if (rs != null) {
			setGwKey(rs.getString(KEY_ID));
			setNumber(rs.getString(PARAMNUMBER));
			setParent(rs.getString(PARENT));
			setEnname(rs.getString(ENNAME));
			setChinaname(rs.getString(CHINANAME));
			setTechnicstype(rs.getString(TECHNICSTYPE));
			setValuetype(rs.getString(VALUETYPE));
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
	public Map<?, ?> getUpdateMap() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(PARAMNUMBER, number);
		ret.put(PARENT, parent);
		ret.put(ENNAME, enname);
		ret.put(CHINANAME, chinaname);
		ret.put(TECHNICSTYPE, technicstype);
		ret.put(VALUETYPE, valuetype);
		return ret;
	}

	@Override
	public Map<?, ?> getCreateMap() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(PARAMNUMBER, number);
		ret.put(PARENT, parent);
		ret.put(ENNAME, enname);
		ret.put(CHINANAME, chinaname);
		ret.put(TECHNICSTYPE, technicstype);
		ret.put(VALUETYPE, valuetype);
		return ret;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getEnname() {
		return enname;
	}

	public void setEnname(String enname) {
		this.enname = enname;
	}

	public String getChinaname() {
		return chinaname;
	}

	public void setChinaname(String chinaname) {
		this.chinaname = chinaname;
	}

	public String getTechnicstype() {
		return technicstype;
	}

	public void setTechnicstype(String technicstype) {
		this.technicstype = technicstype;
	}

	public String getValuetype() {
		return valuetype;
	}

	public void setValuetype(String valuetype) {
		this.valuetype = valuetype;
	}

	public String getGwKey() {
		return gwKey;
	}

	public void setGwKey(String gwKey) {
		this.gwKey = gwKey;
	}

}
