package ext.ases.dataSearch;

import java.util.Date;


public class ObjectDetailDataBean {

	private String number;
	private String name;
	private String objVersion;
	private String objOid;
	private String sendType;
	private String productCode;
	private String packNum;
	private String packOid;
	private Date sendDate;
	
	public ObjectDetailDataBean(String number, String name, String objVersion,
			String objOid, Date sendDate, String sendType, String productCode, String packNum,
			String packOid) {
		super();
		this.number = number;
		this.name = name;
		this.objVersion = objVersion;
		this.objOid = objOid;
		this.sendDate = sendDate;
		this.sendType = sendType;
		this.productCode = productCode;
		this.packNum = packNum;
		this.packOid = packOid;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
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

	public String getObjVersion() {
		return objVersion;
	}

	public void setObjVersion(String objVersion) {
		this.objVersion = objVersion;
	}

	public String getObjOid() {
		return objOid;
	}

	public void setObjOid(String objOid) {
		this.objOid = objOid;
	}

	public String getSendType() {
		return sendType;
	}

	public void setSendType(String sendType) {
		this.sendType = sendType;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getPackNum() {
		return packNum;
	}

	public void setPackNum(String packNum) {
		this.packNum = packNum;
	}

	public String getPackOid() {
		return packOid;
	}

	public void setPackOid(String packOid) {
		this.packOid = packOid;
	}
	


}
