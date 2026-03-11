package com.glaway.mpm.swing;

public class KVItem {
	private String key;
	private String value;
	private boolean isSelected;
	
	// dwg->pdf 使用
	private String desc;  // 描述
	private float top; 
	private float left;
	private byte[] pdfTemplate;

	public KVItem(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return value;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public float getTop() {
		return top;
	}

	public void setTop(float top) {
		this.top = top;
	}

	public float getLeft() {
		return left;
	}

	public void setLeft(float left) {
		this.left = left;
	}

	public byte[] getPdfTemplate() {
		return pdfTemplate;
	}

	public void setPdfTemplate(byte[] pdfTemplate) {
		this.pdfTemplate = pdfTemplate;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setValue(String value) {
		this.value = value;
	}

	
	
	
}