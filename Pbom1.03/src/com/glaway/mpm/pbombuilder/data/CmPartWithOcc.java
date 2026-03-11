package com.glaway.mpm.pbombuilder.data;

import java.io.Serializable;
import java.util.Vector;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;

public class CmPartWithOcc implements Serializable {
	private static final long serialVersionUID = -2381142206538049876L;

	private CmLightPart part;
	private Vector<CmPartUsesOcc> occurences;
	private Vector<CmPartWithOcc> children;
	private int quantity;
	private String ulink;

	public static CmPartWithOcc newCmPartWithOcc(CmLightPart part) {
		return newCmPartWithOcc(part, null);
	}

	public static CmPartWithOcc newCmPartWithOcc(CmLightPart part, CmPartUsesOcc partUseOcc) {
		CmPartWithOcc ret = new CmPartWithOcc(part);
		if (partUseOcc != null)
			ret.addUseOcc(partUseOcc);

		return ret;
	}

	private CmPartWithOcc(CmLightPart part) {
		this.part = part;
		this.occurences = new Vector<CmPartUsesOcc>();
		this.children = new Vector<CmPartWithOcc>();
		this.quantity = 1;
	}

	public CmLightPart getPart() {
		return part;
	}

	public Vector<CmPartUsesOcc> getOccurences() {
		return occurences;
	}

	public Vector<CmPartWithOcc> getChildren() {
		return children;
	}

	public void addChild(CmPartWithOcc child) {
		if (child != null) {
			this.children.add(child);
		}
	}

	public void addUseOcc(CmPartUsesOcc useOcc) {
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

	public String getUlink() {
		return ulink;
	}

	public void setUlink(String ulink) {
		this.ulink = ulink;
	}
}
