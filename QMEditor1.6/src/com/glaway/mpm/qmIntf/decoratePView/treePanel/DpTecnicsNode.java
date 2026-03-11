package com.glaway.mpm.qmIntf.decoratePView.treePanel;

import java.io.Serializable;

import com.glaway.mpm.visual.view.tree.VaTreeNode;

public class DpTecnicsNode extends VaTreeNode implements
		Serializable {

	private static final long serialVersionUID = 1L;
	private boolean isSelected;

	public DpTecnicsNode(Object useObject) {
		super(useObject);
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
