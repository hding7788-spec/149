package com.glaway.mpm.mpmresource.gznumber.classification;

import java.io.Serializable;

public class GZNumberClassification implements GZNumberClassificationInfoContained, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7545490592034906917L;
	
	public static String CLASSIFICATION_PATH = "Classification_Path";
	public static String CLASSIFICATION_PARENTPATH = "Classification_ParentPath";
	public static String CLASSIFICATION_VALUE = "Classification_Value";
	public static String CLASSIFICATION_NAME = "Classification_Name";
	public static String CLASSIFICATION_DESC = "Classification_Desc";
	
	private String objectclasspath;
	private String objectname;
	private String objectclassvalue;
	private String objectclassdesc;
	private String objectparentclasspath;
	
	public String getObjectname() {
		return objectname;
	}
	public void setObjectname(String objectname) {
		this.objectname = objectname;
	}
	public String getObjectclassdesc() {
		return objectclassdesc;
	}
	public void setObjectclassdesc(String objectclassdesc) {
		this.objectclassdesc = objectclassdesc;
	}
	public String getObjectclasspath() {
		return objectclasspath;
	}
	public void setObjectclasspath(String objectclasspath) {
		this.objectclasspath = objectclasspath;
	}
	public String getObjectclassvalue() {
		return objectclassvalue;
	}
	public void setObjectclassvalue(String objectclassvalue) {
		this.objectclassvalue = objectclassvalue;
	}
	public String getObjectparentclasspath() {
		return objectparentclasspath;
	}
	public void setObjectparentclasspath(String objectparentclasspath) {
		this.objectparentclasspath = objectparentclasspath;
	}
	

}
