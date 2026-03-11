package com.glaway.mpm.qmIntf.decoratePView.treePanel;

public class Step {
	private String oid;
	private String number;
	private String name;
	private String workshopName;
	private String shopTypeName;

	public Step(String oid, String number, String name, String workshopName,
			String shopTypeName) {
		super();
		this.oid = oid;
		this.number = number;
		this.name = name;
		this.workshopName = workshopName;
		this.shopTypeName = shopTypeName;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWorkshopName() {
		return workshopName;
	}

	public void setWorkshopName(String workshopName) {
		this.workshopName = workshopName;
	}

	public String getShopTypeName() {
		return shopTypeName;
	}

	public void setShopTypeName(String shopTypeName) {
		this.shopTypeName = shopTypeName;
	}

}
