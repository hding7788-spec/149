package com.glaway.mpm.qmIntf.decoratePView.treePanel;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

import com.glaway.mpm.visual.view.tree.VaTreeNode;

public class DpStepNode extends VaTreeNode implements Serializable {
	private static final long serialVersionUID = 1L;
	private Step step;
	private boolean isSelected;

	public DpStepNode(String oid, String number, String name,
			String workshopName, String shopTypeName) {
		super(name);
		step = new Step(oid, number, name, workshopName, shopTypeName);
		
	}

	public Step getStep() {
		return step;
	}

	public void setStep(Step step) {
		this.step = step;
	}

}