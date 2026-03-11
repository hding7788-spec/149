package com.glaway.mpm.pbom.db;

import java.io.Serializable;
import java.util.List;

public class ErpResult implements Serializable{

	private static final long serialVersionUID = 1L;
	private String msg;
	private List<Wzk> dataList;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<Wzk> getDataList() {
		return dataList;
	}

	public void setDataList(List<Wzk> dataList) {
		this.dataList = dataList;
	}

}
