package com.glaway.mpm.mpmresource.gznumber.classification;

public interface GZNumberClassificationInfoContained {
	public String getObjectclasspath();
	public String getObjectname();
	public String getObjectclassvalue();
	public String getObjectclassdesc();
	public String getObjectparentclasspath();
	
	public void setObjectclasspath(String objectclasspath);
	public void setObjectname(String objectname);
	public void setObjectclassvalue(String objectclassvalue);
	public void setObjectclassdesc(String objectclassdesc);
	public void setObjectparentclasspath(String objectparentclasspath);
}
