package com.glaway.mpm.qmIntf.viewPanel;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

public class CmpNode extends DefaultMutableTreeNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private Note note;
	private boolean isSelected;

	public CmpNode(String name) {
		this.note = new Note(name);
	}

	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
