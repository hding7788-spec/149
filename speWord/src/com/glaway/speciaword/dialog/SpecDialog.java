package com.glaway.speciaword.dialog;

import javax.swing.JDialog;

public class SpecDialog extends JDialog {
	private String category;
	private String imageFolder;

	public SpecDialog(String category,String imageFolder) {
		super();
		setModal(true);
		this.category = category;
		this.imageFolder = imageFolder;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getImageFolder() {
		return imageFolder;
	}

	public void setImageFolder(String imageFolder) {
		this.imageFolder = imageFolder;
	}

}
