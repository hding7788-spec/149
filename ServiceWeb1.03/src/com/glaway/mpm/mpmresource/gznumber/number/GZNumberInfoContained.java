package com.glaway.mpm.mpmresource.gznumber.number;

public interface GZNumberInfoContained {
	public String getNumber();
	public String getClasspath();
	public int getFlag();
	public int getSeq();
	
	public void setNumber(String number);
	public void setClasspath(String classpath);
	public void setFlag(int flag);
	public void setSeq(int seq);
}
