package com.glaway.mpm.model;

import java.io.File;

public class FileTemplate {

	private String fileName;
	private String tempId;
	private String tempName;
	private String desc;
	private File file;
	private String displayName;
	private String makeDept;
	private String useDept;
	private String technicsState;

	private String docNumber; // dwg 文件

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public String getTempName() {
		return tempName;
	}

	public void setTempName(String tempName) {
		this.tempName = tempName;
	}

	@Override
	public String toString() {
		return "FileTemplate [fileName=" + fileName + ", tempId=" + tempId
				+ ", tempName=" + tempName + ", desc=" + desc + ", file="
				+ file + ", displayName=" + displayName + ", docNumber="
				+ docNumber + "]";
	}

	public File getFile() {
		return file;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getMakeDept() {
		return makeDept;
	}

	public void setMakeDept(String makeDept) {
		this.makeDept = makeDept;
	}

	public String getUseDept() {
		return useDept;
	}

	public void setUseDept(String useDept) {
		this.useDept = useDept;
	}

	public String getTechnicsState() {
		return technicsState;
	}

	public void setTechnicsState(String technicsState) {
		this.technicsState = technicsState;
	}

}
