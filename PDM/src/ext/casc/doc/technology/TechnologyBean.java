package ext.casc.doc.technology;

import java.io.Serializable;

public class TechnologyBean implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String keyid;
	private String fatherEname;
	private String fatherCname;
	private String childNumber;
	private String childName;
	/**
	 * 状态： 0，启用； 1，禁用
	 */
	private String status;

	public String getKeyid() {
		return keyid;
	}
	public void setKeyid(String keyid) {
		this.keyid = keyid;
	}
	public String getFatherEname() {
		return fatherEname;
	}
	public void setFatherEname(String fatherEname) {
		this.fatherEname = fatherEname;
	}
	public String getFatherCname() {
		return fatherCname;
	}
	public void setFatherCname(String fatherCname) {
		this.fatherCname = fatherCname;
	}
	public String getChildNumber() {
		return childNumber;
	}
	public void setChildNumber(String childNumber) {
		this.childNumber = childNumber;
	}
	public String getChildName() {
		return childName;
	}
	public void setChildName(String childName) {
		this.childName = childName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
