package com.glaway.mpm.mpmresource.gznumber.loader;

import java.util.HashMap;

import com.glaway.mpm.mpmresource.gznumber.classification.GZNumberClassification;
import com.glaway.mpm.mpmresource.gznumber.classification.GZNumberClassificationInfoContained;


public class GZNumberClassificationLoaderTranslator implements GZNumberClassificationTranslated{
	private GZNumberClassification classInfo;
	
	public static final short ROW_BEGIN = 1;
	
	public static final short COL_PATH = 0;
	public static final short COL_PARENT = 1;
	public static final short COL_NAME = 2;
	public static final short COL_VALUE = 3;
	public static final short COL_DESC = 4;
	
	public GZNumberClassificationLoaderTranslator(){
		classInfo = new GZNumberClassification();
	}

	public GZNumberClassificationTranslated translate(HashMap record) {
		String value_path = (String) record.get("ROW " + COL_PATH);
		String value_parent = (String) record.get("ROW " + COL_PARENT);
		String value_name = (String) record.get("ROW " + COL_NAME);
		String value_value = (String) record.get("ROW " + COL_VALUE);
		String value_desc = (String) record.get("ROW " + COL_DESC);
		
		classInfo.setObjectclasspath(value_path);
		classInfo.setObjectparentclasspath(value_parent);
		classInfo.setObjectname(value_name);
		classInfo.setObjectclassvalue(value_value);
		classInfo.setObjectclassdesc(value_desc);
		
		return this;
	}
	
	public GZNumberClassificationInfoContained getClassInfo() {
		return classInfo;
	}

	public short getRowBegin() {
		return this.ROW_BEGIN;
	}
}
