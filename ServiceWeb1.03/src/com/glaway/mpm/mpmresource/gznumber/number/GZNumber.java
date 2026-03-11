package com.glaway.mpm.mpmresource.gznumber.number;

import com.glaway.mpm.mpmresource.gznumber.bean.RequestInfoContained;


public class GZNumber implements RequestInfoContained, GZNumberInfoContained {
	public static String GZNUMBER_NUMBER = "GZNUMBER_Number";
	public static String GZNUMBER_NAME = "GZNUMBER_Name";
	public static String GZNUMBER_CLASS = "GZNUMBER_Class";
	public static String GZNUMBER_REQUESTOR = "GZNUMBER_Requestor";
	public static String GZNUMBER_REQUESTDATETIME = "GZNUMBER_RequestDateTime";
	public static String GZNUMBER_FLAG = "GZNUMBER_Flag";
	public static String GZNUMBER_REQUESTDESC = "GZNUMBER_RequestDesc";
	public static String GZNUMBER_CANCELDESC = "GZNUMBER_CancelDesc";
	public static String GZNUMBER_SEQ = "GZNUMBER_Seq";

	private String number;
	private String classpath;
	private String objectname;
	private int flag;
	private String requestor;
	private String datetime;
	private String requestdesc;
	private String canceldesc;
	private int seq;

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

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
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

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getClasspath() {
		return classpath;
	}

	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}

}
