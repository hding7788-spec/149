package ext.casc.report.bomcompare;

import wt.part.WTPart;

public class ExportBomDetailsBean {

	int layer;//层级
	String name;//部件名
	String number;//部件编号
	Double amount;//部件数量
	WTPart childPart;
	public WTPart getChildPart() {
		return childPart;
	}
	public void setChildPart(WTPart childPart) {
		this.childPart = childPart;
	}
	public int getLayer() {
		return layer;
	}
	public void setLayer(int layer) {
		this.layer = layer;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
}
