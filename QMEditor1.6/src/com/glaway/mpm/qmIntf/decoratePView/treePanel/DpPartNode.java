package com.glaway.mpm.qmIntf.decoratePView.treePanel;

import java.io.Serializable;

import com.glaway.mpm.model.TempObject;
import com.glaway.mpm.visual.view.tree.VaTreeNode;

public class DpPartNode extends VaTreeNode implements Serializable{
	private static final long serialVersionUID = 1L;
	private String zcmark;
	public DpPartNode(Object useObject) {
		super(useObject);
	}
    public String getZcmark() {
        return zcmark;
    }
    public void setZcmark(String zcmark) {
        this.zcmark = zcmark;
    }

}
