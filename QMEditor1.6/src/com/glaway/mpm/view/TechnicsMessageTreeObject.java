package com.glaway.mpm.view;

import java.awt.Image;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.dom4j.Document;
import org.dom4j.Element;

import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;

public class TechnicsMessageTreeObject implements XWTreeObject {
	private String partNumber;
	private String techNumber;
	private String techName;
	private String techType;
	private String version;
	private String technicsCategory;
	private String batch;
	private String pplanNumber;
	private String isZhuZhi;
	private String pplantype;

	public String getPplantype() {
		return pplantype;
	}

	public void setPplantype(String pplantype) {
		this.pplantype = pplantype;
	}

	public TechnicsMessageTreeObject(String partNumber,String techNumber, String techName,
			String techType, String version, String technicsCategory,String batch,String pplanNumber,String isZhuZhi,String pplantype) {
		this.partNumber = partNumber;
		this.techNumber = techNumber;
		this.techName = techName;
		this.techType = techType;
		this.version = version;
		this.technicsCategory = technicsCategory;
		this.batch = batch;
		this.pplanNumber = pplanNumber;
		this.isZhuZhi = isZhuZhi;
		this.pplantype = pplantype;
	}

	public Vector expand() throws Exception {
		Vector vec = new Vector();
		return vec;
	}

	public Image getCloseImage() {
		return getOpenImage();
	}

	public String getDisplayName() {
		//return this.techNumber + "_" + this.techName + "_" + this.version;
		//return this.techName+"("+this.techNumber + ")_" + this.version;
		if(this.batch != null && !"".equals(this.batch)) {
			return this.techName+"("+this.pplanNumber+")" +"("+this.batch+")_" + this.version;
		}
		return this.techName+"("+this.pplanNumber+")" +"_" + this.version;
	}


	public Image getOpenImage() {
		String iconName = "/images/technics.gif";
		if ("rework".equals(this.technicsCategory)) {
			iconName = "/images/rework.gif";
		} else if ("temp".equals(this.technicsCategory)) {
			iconName = "/images/temp.gif";
		} else if (techType.equals("零件工艺")) {
			iconName = "/images/lingjian.gif";
		}
		ImageIcon icon = new ImageIcon(getClass().getResource(iconName));
		return icon.getImage();
	}

	public String getTipNoteText() {
		return this.techNumber + "_" + this.techName + this.techType + "_" + this.version;
	}

	public Element getTreeCellData() {
		Document document = getTechnicsDocument();
		return XmlUtility.getTechnicsElement(document);
	}

	public int compareTo(Object arg0) {
		if (!(arg0 instanceof TechnicsMessageTreeObject)) {
			throw new RuntimeException("树节点类型不匹配！");
		}
		TechnicsMessageTreeObject temp = (TechnicsMessageTreeObject) arg0;
//		String me = getEqualsName();
//		String number = temp.getEqualsName();
		String me=techNumber;
		String number = temp.getTechnicsNumber();
		return number.compareTo(me);
	}

	public Document getTechnicsDocument() {
		Document doc = null;
		try {
			if ("rework".equals(this.technicsCategory)) {
				doc = WorkSpaceUtil.getReWorkTechnicsDocumentByTechnicsName(this.techNumber, this.techName);
			} else if ("temp".equals(this.technicsCategory)) {
				doc = WorkSpaceUtil.getTempTechnicsDocumentByTechnicsName(this.techNumber, this.techName);
			} else {
				doc = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(this.techNumber);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	public String getTechnicsNumber() {
		return this.techNumber;
	}

	public String getTechNumber() {
		return techNumber;
	}

	public void setTechNumber(String techNumber) {
		this.techNumber = techNumber;
	}

	public String getTechName() {
		return techName;
	}

	public void setTechName(String techName) {
		this.techName = techName;
	}

	public String getTechType() {
		return techType;
	}

	public void setTechType(String techType) {
		this.techType = techType;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTechnicsCategory() {
		return technicsCategory;
	}

	public void setTechnicsCategory(String technicsCategory) {
		this.technicsCategory = technicsCategory;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public String getPplanNumber() {
		return pplanNumber;
	}

	public void setPplanNumber(String pplanNumber) {
		this.pplanNumber = pplanNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getisZhuZhi() {
		return isZhuZhi;
	}

	public void setZhuZhi(String isZhuZhi) {
		this.isZhuZhi = isZhuZhi;
	}

	public void setTreeCellData(Element data) {
	}

	@Override
	public String getEqualsName() {
		return this.techName+"("+this.pplanNumber+")";
	}
}
