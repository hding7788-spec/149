package com.glaway.mpm.view;

import java.awt.Image;
import java.util.Vector;
import javax.swing.ImageIcon;
import org.dom4j.Element;

public class MaterialTreeObject implements XWTreeObject {
	private String displayName = "";

	public MaterialTreeObject(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public boolean equals(String message) {
		if ((message != null) && (message.trim().length() > 0)) {
			if (message.equals(this.displayName)) {
				return true;
			}
		}
		return false;
	}

	public Vector expand() throws Exception {
		Vector vec = new Vector();
		return vec;
	}

	public Image getCloseImage() {
		return getOpenImage();
	}

	public Image getOpenImage() {
		ImageIcon icon = new ImageIcon(getClass().getResource(
				"/images/material.gif"));
		return icon.getImage();
	}

	public String getTipNoteText() {
		return this.displayName;
	}

	public Element getTreeCellData() {
		return null;
	}

	public void setTreeCellData(Element data) {
	}

	public int compareTo(Object arg0) {
		if (!(arg0 instanceof MaterialTreeObject)) {
			throw new RuntimeException("树节点类型不匹配！");
		}
		MaterialTreeObject temp = (MaterialTreeObject) arg0;
		String number = temp.getDisplayName();
		String me = getDisplayName();
		return number.compareTo(me);
	}

	@Override
	public String getEqualsName() {
		// TODO Auto-generated method stub
		return this.getDisplayName();
	}
}
