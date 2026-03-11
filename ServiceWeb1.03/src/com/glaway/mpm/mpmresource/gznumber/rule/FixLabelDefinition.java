package com.glaway.mpm.mpmresource.gznumber.rule;

public class FixLabelDefinition {
	public static String FIXLABEL_DEFINITION = "FixLabelDefinition";
	public static String FIXLABEL_CLASSESNAME = "Name";
	public static String FIXLABEL_CLASSESVALUE = "Value";
	
	private String FixLabelDefinitionName;
	private String fixLabelDefinition;
	private String fixLabelValue;
	
	public String getFixLabelValue() {
		return fixLabelValue;
	}
	public void setFixLabelValue(String fixLabelValue) {
		this.fixLabelValue = fixLabelValue;
	}
	public String getFixLabelDefinition() {
		return fixLabelDefinition;
	}
	public void setFixLabelDefinition(String fixLabelDefinition) {
		this.fixLabelDefinition = fixLabelDefinition;
	}
	public String getFixLabelDefinitionName() {
		return FixLabelDefinitionName;
	}
	public void setFixLabelDefinitionName(String fixLabelDefinitionName) {
		FixLabelDefinitionName = fixLabelDefinitionName;
	}
}
