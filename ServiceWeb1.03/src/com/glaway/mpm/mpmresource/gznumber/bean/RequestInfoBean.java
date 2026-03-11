package com.glaway.mpm.mpmresource.gznumber.bean;

import java.util.ArrayList;

import com.glaway.mpm.mpmresource.gznumber.classification.GZNumberClassificationInfoContained;

public class RequestInfoBean implements RequestInfoContained, GZNumberClassificationInfoContained {
	public static int REQUEST_ACTION = 1;
	public static int CANCEL_ACTION = 2;

	private String objectclasspath;
	private String objectname;
	private String objectclassvalue;
	private String objectclassdesc;
	private String objectparentclasspath;

	private ArrayList selectedFixedLabels;

	private String requestor;
	private String datetime;
	private int actiontype;
	private String requestdesc;
	private String canceldesc;
	private int requestvolumn;

	public int getActiontype() {
		return actiontype;
	}

	public void setActiontype(int actiontype) {
		this.actiontype = actiontype;
	}

	public String getCanceldesc() {
		return canceldesc;
	}

	public void setCanceldesc(String canceldesc) {
		this.canceldesc = canceldesc;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
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

	public String getObjectname() {
		return objectname;
	}

	public void setObjectname(String objectname) {
		this.objectname = objectname;
	}

	public String getRequestdesc() {
		return requestdesc;
	}

	public void setRequestdesc(String requestdesc) {
		this.requestdesc = requestdesc;
	}

	public String getRequestor() {
		return requestor;
	}

	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	public int getRequestvolumn() {
		return requestvolumn;
	}

	public void setRequestvolumn(int requestvolumn) {
		this.requestvolumn = requestvolumn;
	}

	public ArrayList getSelectedFixedLabels() {
		return selectedFixedLabels;
	}

	public void setSelectedFixedLabels(ArrayList selectedFixedLabels) {
		this.selectedFixedLabels = selectedFixedLabels;
	}

	public String getObjectparentclasspath() {
		return objectparentclasspath;
	}

	public void setObjectparentclasspath(String objectparentclasspath) {
		this.objectparentclasspath = objectparentclasspath;
	}

}
