package com.glaway.mpm.mpmresource.gznumber.bean;

public interface RequestInfoContained {
	public String getRequestor();
	public String getDatetime();
	public String getRequestdesc();
	public String getObjectname();
	public String getCanceldesc();
	
	public void setRequestor(String requestor);
	public void setDatetime(String datetime);
	public void setRequestdesc(String requestdesc);
	public void setObjectname(String objectname);
	public void setCanceldesc(String canceldesc);
}
