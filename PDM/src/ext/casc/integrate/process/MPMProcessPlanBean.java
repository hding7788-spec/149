package ext.casc.integrate.process;

import java.io.Serializable;

public class MPMProcessPlanBean implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String processType;//工艺文件类型
	private String zfType;//工艺文件主辅类别
	private String ftType;//正式工艺、临时工艺
	private String processNumber;//工艺文件唯一流水号
	private String processName;//工艺文件名称
	private String processVersion;//工艺文件版本
	private String pplanNumber;//工艺文件编号
	private String batch;//工艺文件批次号
	public String getProcessType() {
		return processType;
	}
	public void setProcessType(String processType) {
		this.processType = processType;
	}
	public String getZfType() {
		return zfType;
	}
	public void setZfType(String zfType) {
		this.zfType = zfType;
	}
	public String getFtType() {
		return ftType;
	}
	public void setFtType(String ftType) {
		this.ftType = ftType;
	}
	public String getProcessNumber() {
		return processNumber;
	}
	public void setProcessNumber(String processNumber) {
		this.processNumber = processNumber;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getProcessVersion() {
		return processVersion;
	}
	public void setProcessVersion(String processVersion) {
		this.processVersion = processVersion;
	}
	public String getPplanNumber() {
		return pplanNumber;
	}
	public void setPplanNumber(String pplanNumber) {
		this.pplanNumber = pplanNumber;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}


}
