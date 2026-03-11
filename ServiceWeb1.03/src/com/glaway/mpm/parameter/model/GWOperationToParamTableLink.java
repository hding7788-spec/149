package com.glaway.mpm.parameter.model;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.glaway.mpm.parameter.service.gwpersistable.GwPersistable;

/**
 * 材料
 *
 * @author 龙秀川
 *
 */
public class GWOperationToParamTableLink implements GwPersistable {

	private static final long serialVersionUID = 1L;

	/**
	 * 表列名
	 */
	public static String TECHNICSNUMBER = "TECHNICSNUMBER";
	public static String OBJNUMBER = "OBJNUMBER";
	public static String OBJTYPE = "OBJTYPE";// 数据记录类型
	public static String PARAMETERTABLEID = "PARAMETERTABLEID";// 参数表类型ID
	public static String TABLEINDEX = "TABLEINDEX";
	public static String BSOID = "BSOID";
	public static String VERSION = "VERSION";

	/**
	 * 属性字段
	 */
	private String gwKey; // 主键
	private String technicsnumber; // 类名
	private String objnumber; // 对象ID
	private String objtype;// 材料编码
	private String parametertableid;// 材料名
	private String tableindex;
	private String bsoID;
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
			this.setGwKey(rs.getString(KEY_ID));
			this.setTechnicsnumber(rs.getString(TECHNICSNUMBER));
			this.setObjnumber(rs.getString(OBJNUMBER));
			this.setObjtype(rs.getString(OBJTYPE));
			this.setParametertableid(rs.getString(PARAMETERTABLEID));
			this.setTableindex(rs.getString(TABLEINDEX));
			this.setBsoID(rs.getString(BSOID));
			this.setVersion(rs.getString(VERSION));
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
		ret.put(TECHNICSNUMBER, technicsnumber);
		ret.put(OBJNUMBER, objnumber);
		ret.put(OBJTYPE, objtype);
		ret.put(PARAMETERTABLEID, parametertableid);
		ret.put(TABLEINDEX, tableindex);
		ret.put(BSOID, bsoID);
		ret.put(VERSION, version);
		return ret;
	}

	@Override
	public Map<?, ?> getCreateMap() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(TECHNICSNUMBER, technicsnumber);
		ret.put(OBJNUMBER, objnumber);
		ret.put(OBJTYPE, objtype);
		ret.put(PARAMETERTABLEID, parametertableid);
		ret.put(TABLEINDEX, tableindex);
		ret.put(BSOID, bsoID);
		ret.put(VERSION, version);
		return ret;
	}

	public String getGwKey() {
		return gwKey;
	}

	public void setGwKey(String gwKey) {
		this.gwKey = gwKey;
	}

	public String getTechnicsnumber() {
		return technicsnumber;
	}

	public void setTechnicsnumber(String technicsnumber) {
		this.technicsnumber = technicsnumber;
	}

	public String getObjnumber() {
		return objnumber;
	}

	public void setObjnumber(String objnumber) {
		this.objnumber = objnumber;
	}

	public String getObjtype() {
		return objtype;
	}

	public void setObjtype(String objtype) {
		this.objtype = objtype;
	}

	public String getParametertableid() {
		return parametertableid;
	}

	public void setParametertableid(String parametertableid) {
		this.parametertableid = parametertableid;
	}

	public String getTableindex() {
		return tableindex;
	}

	public void setTableindex(String tableindex) {
		this.tableindex = tableindex;
	}

	public String getBsoID() {
		return bsoID;
	}

	public void setBsoID(String bsoID) {
		this.bsoID = bsoID;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}



}
