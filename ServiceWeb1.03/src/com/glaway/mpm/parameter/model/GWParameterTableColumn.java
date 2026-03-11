package com.glaway.mpm.parameter.model;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.glaway.mpm.parameter.service.gwpersistable.GwPersistable;

/**
 * 参数表格列定义
 *
 * @author 龙秀川
 *
 */
public class GWParameterTableColumn implements GwPersistable {

	private static final long serialVersionUID = 1L;

	public static String PARAMETERTABLETYPEID = "PARAMETERTABLETYPEID";
	public static String NAME = "NAME";
	public static String CHINANAME = "CHINANAME";
	public static String DATATYPE = "DATATYPE";
	public static String MAXLONG = "MAXLONG";
	public static String ISRECORD = "ISRECORD";
	public static String ISFROMPARAM = "ISFROMPARAM";
	public static String VALUERANGE = "VALUERANGE";
	public static String STATUS = "STATUS";
	public static String ORDERNO = "ORDERNO";
	public static String VISILESS = "VISILESS";
	public static String VISILESSINMES = "VISILESSINMES";

	private String gwKey;
	private String parametertabletypeid;
	private String name;
	private String chinaname;
	private String datatype;
	private String maxlong;
	private String isrecord;
	private String isfromparam;
	private String valuerange;
	private String status;
	private String orderno;
	private String visiless;
	private String visilessinmes;

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
			setParametertabletypeid(rs.getString(PARAMETERTABLETYPEID));
			setName(rs.getString(NAME));
			setChinaname(rs.getString(CHINANAME));
			setDatatype(rs.getString(DATATYPE));
			setMaxlong(rs.getString(MAXLONG));
			setIsrecord(rs.getString(ISRECORD));
			setIsfromparam(rs.getString(ISFROMPARAM));
			setValuerange(rs.getString(VALUERANGE));
			setStatus(rs.getString(STATUS));
			setOrderno(rs.getString(ORDERNO));
			setVisiless(rs.getString(VISILESS));
			setVisilessinmes(rs.getString(VISILESSINMES));

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
		ret.put(PARAMETERTABLETYPEID, parametertabletypeid);
		ret.put(NAME, name);
		ret.put(CHINANAME, chinaname);
		ret.put(DATATYPE, datatype);
		ret.put(MAXLONG, maxlong);
		ret.put(ISRECORD, isrecord);
		ret.put(ISFROMPARAM, isfromparam);
		ret.put(VALUERANGE, valuerange);
		ret.put(STATUS, status);
		ret.put(ORDERNO,orderno);
		ret.put(VISILESS, visiless);
		ret.put(VISILESSINMES, visilessinmes);
		return ret;
	}

	@Override
	public Map<?, ?> getCreateMap() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(PARAMETERTABLETYPEID, parametertabletypeid);
		ret.put(NAME, name);
		ret.put(CHINANAME, chinaname);
		ret.put(DATATYPE, datatype);
		ret.put(MAXLONG, maxlong);
		ret.put(ISRECORD, isrecord);
		ret.put(ISFROMPARAM, isfromparam);
		ret.put(VALUERANGE, valuerange);
		ret.put(STATUS, status);
		ret.put(ORDERNO, orderno);
		ret.put(VISILESS, visiless);
		ret.put(VISILESSINMES, visilessinmes);
		return ret;
	}

	public String getParametertabletypeid() {
		return parametertabletypeid;
	}

	public void setParametertabletypeid(String parametertabletypeid) {
		this.parametertabletypeid = parametertabletypeid;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getMaxlong() {
		return maxlong;
	}

	public void setMaxlong(String maxlong) {
		this.maxlong = maxlong;
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

	public String getIsrecord() {
		return isrecord;
	}

	public void setIsrecord(String isrecord) {
		this.isrecord = isrecord;
	}

	public String getIsfromparam() {
		return isfromparam;
	}

	public void setIsfromparam(String isfromparam) {
		this.isfromparam = isfromparam;
	}

	public String getValuerange() {
		return valuerange;
	}

	public void setValuerange(String valuerange) {
		this.valuerange = valuerange;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}

	public String getVisiless() {
		return visiless;
	}

	public void setVisiless(String visiless) {
		this.visiless = visiless;
	}

	public String getVisilessinmes() {
		return visilessinmes;
	}

	public void setVisilessinmes(String visilessinmes) {
		this.visilessinmes = visilessinmes;
	}


}
