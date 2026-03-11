package ext.sast.center.productModel.bean;

import java.io.Serializable;

public class ReceivedModelAttrInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sastModelTypeId;
	private String sastModelTypeName;
	private String localModelTypeId;
	private String localModelTypeName;
	
	private String sastModelAttr_us;
	private String sastModelAttr_zh;
	private String localModelAttr_us;
	private String localModelAttr_zh;
	
	
	public String getSastModelTypeId() {
		return sastModelTypeId;
	}
	public void setSastModelTypeId(String sastModelTypeId) {
		this.sastModelTypeId = sastModelTypeId;
	}
	public String getSastModelTypeName() {
		return sastModelTypeName;
	}
	public void setSastModelTypeName(String sastModelTypeName) {
		this.sastModelTypeName = sastModelTypeName;
	}
	public String getLocalModelTypeId() {
		return localModelTypeId;
	}
	public void setLocalModelTypeId(String localModelTypeId) {
		this.localModelTypeId = localModelTypeId;
	}
	public String getLocalModelTypeName() {
		return localModelTypeName;
	}
	public void setLocalModelTypeName(String localModelTypeName) {
		this.localModelTypeName = localModelTypeName;
	}
	public String getSastModelAttr_us() {
		return sastModelAttr_us;
	}
	public void setSastModelAttr_us(String sastModelAttr_us) {
		this.sastModelAttr_us = sastModelAttr_us;
	}
	public String getSastModelAttr_zh() {
		return sastModelAttr_zh;
	}
	public void setSastModelAttr_zh(String sastModelAttr_zh) {
		this.sastModelAttr_zh = sastModelAttr_zh;
	}
	public String getLocalModelAttr_us() {
		return localModelAttr_us;
	}
	public void setLocalModelAttr_us(String localModelAttr_us) {
		this.localModelAttr_us = localModelAttr_us;
	}
	public String getLocalModelAttr_zh() {
		return localModelAttr_zh;
	}
	public void setLocalModelAttr_zh(String localModelAttr_zh) {
		this.localModelAttr_zh = localModelAttr_zh;
	}
	
}
