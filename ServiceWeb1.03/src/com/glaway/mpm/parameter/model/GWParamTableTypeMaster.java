package com.glaway.mpm.parameter.model;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.glaway.mpm.parameter.service.gwpersistable.GwPersistable;

/**
 * 参数表格主对象
 *
 * @author 龙秀川
 *
 */
public class GWParamTableTypeMaster implements GwPersistable {

	private static final long serialVersionUID = 1L;

	public static String NAME = "NAME";
	public static String CHINANAME = "CHINANAME";

	private String gwKey;
	private String name;
	private String chinaname;

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
			setName(rs.getString(NAME));
			setChinaname(rs.getString(CHINANAME));
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
		ret.put(NAME, name);
		ret.put(CHINANAME, chinaname);
		return ret;
	}

	@Override
	public Map<?, ?> getCreateMap() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(NAME, name);
		ret.put(CHINANAME, chinaname);
		return ret;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getChinaname() {
		return chinaname;
	}

	public void setChinaname(String chinaname) {
		this.chinaname = chinaname;
	}

	public String getGwKey() {
		return gwKey;
	}

	public void setGwKey(String gwKey) {
		this.gwKey = gwKey;
	}

}
