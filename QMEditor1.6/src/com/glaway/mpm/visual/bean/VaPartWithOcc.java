package com.glaway.mpm.visual.bean;

import java.io.Serializable;
import java.util.Vector;

public class VaPartWithOcc implements Serializable {
	private static final long serialVersionUID = -2381142206538049876L;

	private VaLightPart part;
	private Vector<VaPartUsesOcc> occurences;
	private Vector<VaPartWithOcc> children;
	private int quantity;
	private Vector wvsBboxes;

	public Vector getWvsBboxes() {
		return wvsBboxes;
	}

	public void setWvsBboxes(Vector wvsBboxes) {
		this.wvsBboxes = wvsBboxes;
	}

	public static VaPartWithOcc newVaPartWithOcc(VaLightPart part) {
		return newVaPartWithOcc(part, null);
	}

	public static VaPartWithOcc newVaPartWithOcc(VaLightPart part,
			VaPartUsesOcc partUseOcc) {
		VaPartWithOcc ret = new VaPartWithOcc(part);
		if (partUseOcc != null)
			ret.addUseOcc(partUseOcc);

		return ret;
	}

	private VaPartWithOcc(VaLightPart part) {
		this.part = part;
		this.occurences = new Vector<VaPartUsesOcc>();
		this.children = new Vector<VaPartWithOcc>();
		this.quantity = 1;
	}

	public VaLightPart getPart() {
		return part;
	}

	public Vector<VaPartUsesOcc> getOccurences() {
		return occurences;
	}

	public Vector<VaPartWithOcc> getChildren() {
		return children;
	}

	public void addChild(VaPartWithOcc child) {
		if (child != null) {
			this.children.add(child);
		}
	}

	public void addUseOcc(VaPartUsesOcc useOcc) {
		if (useOcc != null) {
			this.occurences.add(useOcc);
		}
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
