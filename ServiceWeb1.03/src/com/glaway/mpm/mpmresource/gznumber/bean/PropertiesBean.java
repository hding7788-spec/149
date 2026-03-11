package com.glaway.mpm.mpmresource.gznumber.bean;

import java.io.File;

import com.glaway.mpm.util.PropertiesUtil;

public class PropertiesBean {
	private String filepath = File.separator + "netmarkets" + File.separator + "jsp" + File.separator + "ext"
			+ File.separator + "numbergen" + File.separator + "config" + File.separator + "NumberGen.properties";
	private PropertiesUtil propertiesUtil;

	private String numberFormat;
	private String sequenceFormat;
	private int sequenceBeginAt;
	private String adminGroup;

	public PropertiesBean() {
		propertiesUtil = new PropertiesUtil(filepath);
		numberFormat = propertiesUtil.getProperty("GenerationNumber.Format");
		sequenceFormat = propertiesUtil.getProperty("Sequence.Format");
		sequenceBeginAt = Integer.parseInt(propertiesUtil.getProperty("Sequence.BeginAt"));
		adminGroup = propertiesUtil.getProperty("Administration.Group");
	}

	public String getValue(String key) {
		String strValue = propertiesUtil.getProperty(key);
		if (strValue == null || "".equals(strValue))
			return "";
		strValue = strValue.trim();
		return strValue;
	}

	public String getNumberFormat() {
		return numberFormat;
	}

	public String getSequenceFormat() {
		return sequenceFormat;
	}

	public int getSequenceBeginAt() {
		return sequenceBeginAt;
	}

	public String getAdminGroup() {
		return adminGroup;
	}
	public static void main(String[] args) {
		PropertiesBean bean=new PropertiesBean();
	}
}
