package com.glaway.mpm.parameter.model;

import java.io.Serializable;

/**
 * 工艺文件特殊符号图片数据集合ZIP包
 * 
 * @author 龙秀川
 *
 */
public class GWImageFilesZip implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 图片名称 */
	private String fileName;
	/** 图片文件字节数组 */
	private byte[] data;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}
