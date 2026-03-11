package ext.sast.center.bean;

import java.io.Serializable;

public class SynchRecord implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//tab标签页使用
	private String unit;
	private String activity_Name;
	private String principal;
	private String userRole;
	private String status;
	private String startTime;
	private String completeTime;
	private String remarks;
	
	//导出excel使用
	private String synch_invoice_number;
	private String synch_type;
	private String synch_processStatus;
	private String synch_pboNumber;
	private String synch_pboName;
	private String synch_pboType;
	private String synch_pboCreator;
	private String synch_startTime;
	private String synch_receiveTime;
	
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getActivity_Name() {
		return activity_Name;
	}
	public void setActivity_Name(String activity_Name) {
		this.activity_Name = activity_Name;
	}
	public String getPrincipal() {
		return principal;
	}
	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	public String getUserRole() {
		return userRole;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getCompleteTime() {
		return completeTime;
	}
	public void setCompleteTime(String completeTime) {
		this.completeTime = completeTime;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getSynch_invoice_number() {
		return synch_invoice_number;
	}
	public void setSynch_invoice_number(String synch_invoice_number) {
		this.synch_invoice_number = synch_invoice_number;
	}
	public String getSynch_type() {
		return synch_type;
	}
	public void setSynch_type(String synch_type) {
		this.synch_type = synch_type;
	}
	public String getSynch_processStatus() {
		return synch_processStatus;
	}
	public void setSynch_processStatus(String synch_processStatus) {
		this.synch_processStatus = synch_processStatus;
	}
	public String getSynch_pboNumber() {
		return synch_pboNumber;
	}
	public void setSynch_pboNumber(String synch_pboNumber) {
		this.synch_pboNumber = synch_pboNumber;
	}
	public String getSynch_pboName() {
		return synch_pboName;
	}
	public void setSynch_pboName(String synch_pboName) {
		this.synch_pboName = synch_pboName;
	}
	public String getSynch_pboType() {
		return synch_pboType;
	}
	public void setSynch_pboType(String synch_pboType) {
		this.synch_pboType = synch_pboType;
	}
	public String getSynch_pboCreator() {
		return synch_pboCreator;
	}
	public void setSynch_pboCreator(String synch_pboCreator) {
		this.synch_pboCreator = synch_pboCreator;
	}
	public String getSynch_startTime() {
		return synch_startTime;
	}
	public void setSynch_startTime(String synch_startTime) {
		this.synch_startTime = synch_startTime;
	}
	public String getSynch_receiveTime() {
		return synch_receiveTime;
	}
	public void setSynch_receiveTime(String synch_receiveTime) {
		this.synch_receiveTime = synch_receiveTime;
	}
	
}
