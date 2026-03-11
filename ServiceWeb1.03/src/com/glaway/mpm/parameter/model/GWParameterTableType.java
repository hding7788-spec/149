package com.glaway.mpm.parameter.model;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.glaway.mpm.parameter.service.gwpersistable.GwPersistable;

/**
 * 参数表格
 *
 * @author 龙秀川
 *
 */
public class GWParameterTableType implements GwPersistable, Comparable<GWParameterTableType> {

	private static final long serialVersionUID = 1L;

	public static String TABLETYPEMASTERID = "TABLETYPEMASTERID";
	public static String TECHNICSTYPE = "TECHNICSTYPE";
	public static String ISUSED = "ISUSED";
	public static String VERSION = "VERSION";

	private String gwKey;
	private String tabletypemasterid;
	private String isUsed;
	private String technicstype;
	private String version;

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
			setTabletypemasterid(rs.getString(TABLETYPEMASTERID));
			setIsUsed(rs.getString(ISUSED));
			setTechnicstype(rs.getString(TECHNICSTYPE));
			setVersion(rs.getString(VERSION));
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
		ret.put(TABLETYPEMASTERID, tabletypemasterid);
		ret.put(TECHNICSTYPE, technicstype);
		ret.put(ISUSED, isUsed);
		ret.put(VERSION, version);
		return ret;
	}

	@Override
	public Map<?, ?> getCreateMap() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(TABLETYPEMASTERID, tabletypemasterid);
		ret.put(TECHNICSTYPE, technicstype);
		ret.put(ISUSED, isUsed);
		ret.put(VERSION, version);
		return ret;
	}

	public String getTabletypemasterid() {
		return tabletypemasterid;
	}

	public void setTabletypemasterid(String tabletypemasterid) {
		this.tabletypemasterid = tabletypemasterid;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getGwKey() {
		return gwKey;
	}

	public void setGwKey(String gwKey) {
		this.gwKey = gwKey;
	}

	public String getTechnicstype() {
		return technicstype;
	}

	public void setTechnicstype(String technicstype) {
		this.technicstype = technicstype;
	}

	public String getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(String isUsed) {
		this.isUsed = isUsed;
	}

	@Override
	public int compareTo(GWParameterTableType tableType) {
		int thisVersion = Integer.valueOf(this.getVersion());
		int version = Integer.valueOf(tableType.getVersion());

		if (thisVersion > version) {
			return -1;
		} else if (thisVersion == version) {
			return 0;
		} else {
			return 1;
		}
	}

}
