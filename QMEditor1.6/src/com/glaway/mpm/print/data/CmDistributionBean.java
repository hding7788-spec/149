package com.glaway.mpm.print.data;

import java.io.Serializable;


/**
 * 打印分发领取模型
 * @author zhuhao
 */
public class CmDistributionBean implements Serializable{

	private static final long serialVersionUID = 5637250550534338674L;

	/** 领取人 */
	private String receiptor;

	/** 领取时间 */
	private String receiveTime;

	/** 领用部门 */
	private String receiveDept;

	/** 领用文件 */
	private String receiveFile;

	public String getReceiptor() {
		return receiptor;
	}

	public void setReceiptor(String receiptor) {
		this.receiptor = receiptor;
	}

	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	public String getReceiveDept() {
		return receiveDept;
	}

	public void setReceiveDept(String receiveDept) {
		this.receiveDept = receiveDept;
	}

	public String getReceiveFile() {
		return receiveFile;
	}

	public void setReceiveFile(String receiveFile) {
		this.receiveFile = receiveFile;
	}

	@Override
	public String toString() {
		return "CmDistributionBean [receiptor=" + receiptor + ", receiveTime=" + receiveTime + ", receiveDept=" + receiveDept + ", receiveFile=" + receiveFile + "]";
	}



}
