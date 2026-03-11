package com.glaway.mpm.qmIntf.viewPanel.cmp;

import com.glaway.mpm.qmIntf.viewPanel.tech.TechImageNode;

public class CmpImageNode extends TechImageNode {

	private static final long serialVersionUID = 1L;
	private boolean used;

	public CmpImageNode(String modelName, String name, String dir,
			String version, String imageOid) {
		super(modelName, name, dir, version, imageOid);
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

}
