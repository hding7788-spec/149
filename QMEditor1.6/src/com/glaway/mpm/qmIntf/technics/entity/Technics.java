/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glaway.mpm.qmIntf.technics.entity;

import java.util.List;

/**
 *
 * @author hywang
 */
public class Technics {

	private String oid;
	private String creator = "";
	private String createTime;
	private String technicsName = "";
	private String technicsNumber = "";
	private String technicsType = "";
	private String workShop = "";
	private String productNumber = "";
	private String productName = "";
	private String partNumber = "";
	private String partName = "";
	private String parentPartNumber = "";
	private String backupNote = "";
	private String backupRate = "";
	private String maxBackupCount = "";
	private String maoWeight = "";
	private String stateSize = "";
	private String partCount = "";
	private String partSize = "";
	private String lifeCycleState = "";
	private String wrlFile = "";
	private String version = "";
	private List Steps;
	private List Materials;
	private List parts;
	private List images;
	private String user3;
	private String user2;
	private String user1;
	private String date3;
	private String date2;
	private String date1;
	private String comment3;
	private String comment2;
	private String comment1;
	private String docNumber;

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getLifeCycleState() {
		return lifeCycleState;
	}

	public void setLifeCycleState(String lifeCycleState) {
		this.lifeCycleState = lifeCycleState;
	}

	public String getParentPartNumber() {
		return parentPartNumber;
	}

	public void setParentPartNumber(String parentPartNumber) {
		this.parentPartNumber = parentPartNumber;
	}

	public String getPartName() {
		return partName;
	}

	public void setPartName(String partName) {
		this.partName = partName;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}

	public String getTechnicsName() {
		return technicsName;
	}

	public void setTechnicsName(String technicsName) {
		this.technicsName = technicsName;
	}

	public String getTechnicsNumber() {
		return technicsNumber;
	}

	public void setTechnicsNumber(String technicsNumber) {
		this.technicsNumber = technicsNumber;
	}

	public String getTechnicsType() {
		return technicsType;
	}

	public void setTechnicsType(String technicsType) {
		this.technicsType = technicsType;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getWorkShop() {
		return workShop;
	}

	public void setWorkShop(String workShop) {
		this.workShop = workShop;
	}

	public String getBackupNote() {
		return backupNote;
	}

	public void setBackupNote(String backupNote) {
		this.backupNote = backupNote;
	}

	public String getBackupRate() {
		return backupRate;
	}

	public void setBackupRate(String backupRate) {
		this.backupRate = backupRate;
	}

	public String getMaoWeight() {
		return maoWeight;
	}

	public void setMaoWeight(String maoWeight) {
		this.maoWeight = maoWeight;
	}

	public String getMaxBackupCount() {
		return maxBackupCount;
	}

	public void setMaxBackupCount(String maxBackupCount) {
		this.maxBackupCount = maxBackupCount;
	}

	public String getPartCount() {
		return partCount;
	}

	public void setPartCount(String partCount) {
		this.partCount = partCount;
	}

	public String getPartSize() {
		return partSize;
	}

	public void setPartSize(String partSize) {
		this.partSize = partSize;
	}

	public String getStateSize() {
		return stateSize;
	}

	public void setStateSize(String stateSize) {
		this.stateSize = stateSize;
	}

	public String getWrlFile() {
		return wrlFile;
	}

	public void setWrlFile(String wrlFile) {
		this.wrlFile = wrlFile;
	}

	public List getMaterials() {
		return Materials;
	}

	public void setMaterials(List Materials) {
		this.Materials = Materials;
	}

	public List getSteps() {
		return Steps;
	}

	public void setSteps(List Steps) {
		this.Steps = Steps;
	}

	public List getParts() {
		return parts;
	}

	public void setParts(List parts) {
		this.parts = parts;
	}

	public List getImages() {
		return images;
	}

	public void setImages(List images) {
		this.images = images;
	}

	public String toFormatString() {

		System.out.println("====工艺树===");
		System.out.println("工艺：" + this.technicsName);
		if (this.Steps != null && this.Steps.size() > 0) {
			for (int i = 0; i < this.Steps.size(); i++) {
				Step cStep = (Step) this.Steps.get(i);
				cStep.toFormatString();
			}
		}
		return "";
	}

	public String toJSONString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" items:[ ");
		if (this.Steps != null && this.Steps.size() > 0) {
			for (Object object : Steps) {
				if (object instanceof Step) {
					Step step = (Step) object;
					sb.append(step.toJSONString());
					sb.append(",");
					if (step.getSubSteps() != null
							&& step.getSubSteps().size() > 0) {
						for (Object obj : ((Step) object).getSubSteps()) {
							if (object instanceof Step) {
								sb.append(((Step) obj).toJSONString());
								sb.append(",");
							}
						}
					}
				}
			}
			sb.deleteCharAt(sb.lastIndexOf(","));
		}
		sb.append("] ");
		String temp =sb.toString().replace("@#$", "<br/>");
		return temp;
	}

	public String getUser3() {
		return user3;
	}

	public void setUser3(String user3) {
		this.user3 = user3;
	}

	public String getUser2() {
		return user2;
	}

	public void setUser2(String user2) {
		this.user2 = user2;
	}

	public String getUser1() {
		return user1;
	}

	public void setUser1(String user1) {
		this.user1 = user1;
	}

	public String getDate3() {
		return date3;
	}

	public void setDate3(String date3) {
		this.date3 = date3;
	}

	public String getDate2() {
		return date2;
	}

	public void setDate2(String date2) {
		this.date2 = date2;
	}

	public String getDate1() {
		return date1;
	}

	public void setDate1(String date1) {
		this.date1 = date1;
	}

	public String getComment3() {
		return comment3;
	}

	public void setComment3(String comment3) {
		this.comment3 = comment3;
	}

	public String getComment2() {
		return comment2;
	}

	public void setComment2(String comment2) {
		this.comment2 = comment2;
	}

	public String getComment1() {
		return comment1;
	}

	public void setComment1(String comment1) {
		this.comment1 = comment1;
	}

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

}
