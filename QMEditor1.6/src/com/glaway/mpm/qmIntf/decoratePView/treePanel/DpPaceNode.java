package com.glaway.mpm.qmIntf.decoratePView.treePanel;

import java.io.Serializable;
import java.util.List;

import com.glaway.mpm.model.TempObject;
import com.glaway.mpm.visual.view.tree.VaTreeNode;

public class DpPaceNode extends VaTreeNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private Pace pace;
	private boolean isSelected;

	public DpPaceNode(String oid, String number, String name,String content,
			List<DpPartNode> parts) {
		super(number);
		pace = new Pace(oid, number, name, content,parts);
	}

	public Pace getPace() {
		return pace;
	}

	public void setPace(Pace pace) {
		this.pace = pace;
	}

}
