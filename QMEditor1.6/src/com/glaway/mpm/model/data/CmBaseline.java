package com.glaway.mpm.model.data;

public class CmBaseline extends CmTreeNode {

	private static final long serialVersionUID = -5516752574056874212L;
	
	private String status;
	private String phaseCode;
	private String pindex;
	private String productName;
	private String creatorFullName;
	private boolean isAdded;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPindex() {
		return pindex;
	}

	public void setPindex(String pindex) {
		this.pindex = pindex;
	}

	public String getPhaseCode() {
		return phaseCode;
	}

	public void setPhaseCode(String phaseCode) {
		this.phaseCode = phaseCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCreatorFullName() {
		return creatorFullName;
	}

	public void setCreatorFullName(String creatorFullName) {
		this.creatorFullName = creatorFullName;
	}

	public boolean isAdded() {
		return isAdded;
	}

	public void setAdded(boolean isAdded) {
		this.isAdded = isAdded;
	}
	
}
