package com.glaway.mpm.view;


import java.awt.Image;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.dom4j.Document;
import org.dom4j.Element;

import com.glaway.mpm.util.XmlUtility;

public class XWMesTechnicsTreeObject implements XWTreeObject {
	private Document technicsDom;
	private Element technicsElement;

	private String displayName;

	public XWMesTechnicsTreeObject(Document technicsDom) {
		this.technicsDom = technicsDom;
		try {
			this.technicsElement = XmlUtility.getTechnicsElement(technicsDom);
			String number = XmlUtility.getAttributeValue(this.technicsElement,"pplanNumber");
			String technicsName = XmlUtility.getAttributeValue(this.technicsElement, "pplanName");
			String version = XmlUtility.getAttributeValue(this.technicsElement,"version");
			String batch = XmlUtility.getAttributeValue(this.technicsElement,"PCNO");
			if(batch != null && !"".equals(batch)) {
				displayName = technicsName + "("+ number + ") "+ " ("+ batch +") ";
			}
			displayName = technicsName + "("+ number +") "+ " "+ version;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Vector expand() throws Exception {
		Vector vec = new Vector();
		int count = XmlUtility.countSteps(this.technicsElement);
		for (int i = 0; i < count; i++) {
			Element productElement = XmlUtility.getStep(this.technicsElement, i);
			XWMesStepTreeObject sto = new XWMesStepTreeObject(productElement);
			vec.add(sto);
		}
		String number=technicsElement.attributeValue("technicsNumber");
		/*ArrayList<Map<String,String>> list = (ArrayList) IntfUtil.getPeRemoteMethodInvoke("getGzsqdDocByTechnicsNumber",
                new Class[] { String.class }, new Object[] {number});
		 for (int i = 0; i < list.size(); i++) {
	            String name = list.get(i).get("name");
	            String version = list.get(i).get("version");
	            if (name!=null&&!"".equals(name)&&!"null".equals(name)) {

	                GongZhuangSQDObject zhuangSQDObject = new GongZhuangSQDObject(name, version);
	                vec.add(zhuangSQDObject);
                }
		 }*/


		return vec;
	}

	public Image getCloseImage() {
		return getOpenImage();
	}

	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String addedName){
		String number = XmlUtility.getAttributeValue(this.technicsElement,"pplanNumber");
		String technicsName = XmlUtility.getAttributeValue(this.technicsElement, "pplanName");
		String version = XmlUtility.getAttributeValue(this.technicsElement,"version");
		String batch = XmlUtility.getAttributeValue(this.technicsElement,"PCNO");
		if(batch != null && !"".equals(batch)) {
			displayName = technicsName + "("+ number + addedName + ") "+ " ("+ batch +") ";
		}
		displayName = technicsName + "("+ number + addedName + ") "+ " "+ version;
	}
	public String getEqualsName() {
		String number = XmlUtility.getAttributeValue(this.technicsElement,"pplanNumber");
		String technicsName = XmlUtility.getAttributeValue(this.technicsElement, "pplanName");
		String batch = XmlUtility.getAttributeValue(this.technicsElement,"PCNO");
		if(batch != null && !"".equals(batch)) {
			return technicsName + "("+ number +") "+ " ("+ batch +") ";
		}
		return technicsName + "("+ number +") "+ " ";
	}

	public Image getOpenImage() {
		String type = XmlUtility.getAttributeValue(this.technicsElement, "technicsType");
		String technicsCategory = XmlUtility.getAttributeValue(this.technicsElement, "technicsCategory");
		String iconName = "/images/technics.gif";
		if ("rework".equals(technicsCategory)) {
			iconName = "/images/rework.gif";
		} else if ("temp".equals(technicsCategory)) {
			iconName = "/images/temp.gif";
		} else if (type.equals("零件工艺")) {
			iconName = "/images/lingjian.gif";
		}
		//TODO 812工艺类型图片
		ImageIcon icon = new ImageIcon(getClass().getResource(iconName));
		return icon.getImage();
	}

	public String getTipNoteText() {
		String number = XmlUtility.getAttributeValue(this.technicsElement, "technicsNumber");
		String name = XmlUtility.getAttributeValue(this.technicsElement, "technicsName");
		String version = XmlUtility.getAttributeValue(this.technicsElement, "version");
		return number + "_" + name + "_" + version;
	}

	public Element getTreeCellData() {
		return this.technicsElement;
	}

	public int compareTo(Object arg0) {
		if (!(arg0 instanceof XWMesTechnicsTreeObject)) {
			throw new RuntimeException("树节点类型不匹配！");
		}
		XWMesTechnicsTreeObject temp = (XWMesTechnicsTreeObject) arg0;
		String me = XmlUtility.getAttributeValue(this.technicsElement, "technicsNumber");
		String number = XmlUtility.getAttributeValue(temp.getTreeCellData(), "technicsNumber");
		return number.compareTo(me);
	}

	public Document getTechnicsDocument() {
		return this.technicsDom;
	}

	public void setTreeCellData(Element data) {
		this.technicsDom = data.getDocument();
		this.technicsElement = data;
	}

}
