package com.glaway.mpm.mpmresource.gznumber.number;

import com.glaway.mpm.mpmresource.gznumber.classification.GZNumberClassificationInfoContained;

public class FormatedNumber {
	private String value;
	private GZNumberClassificationInfoContained classification;
	private String requestor;
	private String dateTime;
	private String requestDesc;

	public GZNumberClassificationInfoContained getClassification() {
		return classification;
	}

	public void setClassification(GZNumberClassificationInfoContained classification) {
		this.classification = classification;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getRequestDesc() {
		return requestDesc;
	}

	public void setRequestDesc(String requestDesc) {
		this.requestDesc = requestDesc;
	}

	public String getRequestor() {
		return requestor;
	}

	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
