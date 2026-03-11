package com.glaway.mpm.model;

public class PrintFileBean {

	/** 编号 */
	private String fileNumber;
	/** 名称 */
	private String fileName;
	/** 版本 */
	private String version;
	/** 密级 */
	private String secret;
	/** 型号类型 */
	private String xhlx;
	/** 份数 */
	private String count;

	public String getFileNumber() {
		return fileNumber;
	}
	public void setFileNumber(String fileNumber) {
		this.fileNumber = fileNumber;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getXhlx() {
		return xhlx;
	}
	public void setXhlx(String xhlx) {
		this.xhlx = xhlx;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "PrintFileBean [fileNumber=" + fileNumber + ", fileName=" + fileName + ", version=" + version + ", secret=" + secret + ", xhlx=" + xhlx + ", count=" + count + "]";
	}


}
