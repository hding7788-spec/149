package com.glaway.mpm.model;

import java.io.File;

public class TechnicsStateTableBean {

	private String zzdw;
	private String sydw;
	private String gyzt;
	private String imagePath;
	private File file;
	private boolean isChangeFile;

	public String getZzdw() {
		return zzdw;
	}

	public void setZzdw(String zzdw) {
		this.zzdw = zzdw;
	}

	public String getSydw() {
		return sydw;
	}

	public void setSydw(String sydw) {
		this.sydw = sydw;
	}

	public String getGyzt() {
		return gyzt;
	}

	public void setGyzt(String gyzt) {
		this.gyzt = gyzt;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public boolean isChangeFile() {
		return isChangeFile;
	}

	public void setChangeFile(boolean isChangeFile) {
		this.isChangeFile = isChangeFile;
	}

}
