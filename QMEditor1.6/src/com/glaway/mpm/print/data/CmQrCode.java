package com.glaway.mpm.print.data;

import java.io.Serializable;

import com.glaway.mpm.model.data.CmAttachment;

/**
 * 二维码数据模型
 *
 */
public class CmQrCode implements Serializable{
	private static final long serialVersionUID = 1L;
	/** 编号 */
	private String number;
	/** 文件类型 */
	private String fileType;
	/** 二维码编号 */
	private String fileNumber;
	/** 二维码名称 */
	private String fileName;
	/** 二维码路径*/
	private String filePath;
	/** 更改单号 */
	private String ecnNumber;
	/** 页数 */
	private String pageCount;
	/** 产品代号 */
	private String pindex;
	/** 编制者 */
	private String creator;
	/** 打印人 */
	private String printer;
	/** 打印日期 */
	private String printDate;
	/** 申请日期 */
	private String applyDate;
	/** 打印申请部门 */
	private String applyDept;
	/** 分发部门 */
	private String distributeDept;
	/** 技术状态基线 */
	private String baseline;
	/** 打印要求 */
	private String printDescription;
	/** 二维码图片 */
	private CmAttachment attachment;


	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
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
	public String getEcnNumber() {
		return ecnNumber;
	}
	public void setEcnNumber(String ecnNumber) {
		this.ecnNumber = ecnNumber;
	}
	public String getPageCount() {
		return pageCount;
	}
	public void setPageCount(String pageCount) {
		this.pageCount = pageCount;
	}
	public String getPindex() {
		return pindex;
	}
	public void setPindex(String pindex) {
		this.pindex = pindex;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getPrinter() {
		return printer;
	}
	public void setPrinter(String printer) {
		this.printer = printer;
	}
	public String getPrintDate() {
		return printDate;
	}
	public void setPrintDate(String printDate) {
		this.printDate = printDate;
	}
	public String getDistributeDept() {
		return distributeDept;
	}
	public void setDistributeDept(String distributeDept) {
		this.distributeDept = distributeDept;
	}
	public String getBaseline() {
		return baseline;
	}
	public void setBaseline(String baseline) {
		this.baseline = baseline;
	}
	public String getPrintDescription() {
		return printDescription;
	}
	public void setPrintDescription(String printDescription) {
		this.printDescription = printDescription;
	}
	public CmAttachment getAttachment() {
		return attachment;
	}
	public void setAttachment(CmAttachment attachment) {
		this.attachment = attachment;
	}
	public String getApplyDept() {
		return applyDept;
	}
	public void setApplyDept(String applyDept) {
		this.applyDept = applyDept;
	}

	public String getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	@Override
	public String toString() {
		return "CmQrCode [number=" + number + ", fileType=" + fileType + ", fileNumber=" + fileNumber + ", fileName=" + fileName + ", filePath=" + filePath + ", ecnNumber=" + ecnNumber
				+ ", pageCount=" + pageCount + ", pindex=" + pindex + ", creator=" + creator + ", printer=" + printer + ", printDate=" + printDate + ", applyDate=" + applyDate + ", applyDept="
				+ applyDept + ", distributeDept=" + distributeDept + ", baseline=" + baseline + ", printDescription=" + printDescription + ", attachment=" + attachment + "]";
	}



}