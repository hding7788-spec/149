package com.glaway.mpm.view;

import java.awt.Image;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.dom4j.Element;

import com.glaway.mpm.util.XmlUtility;

public class XWStepTreeObject implements XWTreeObject {
	private Element stepElement;

	public XWStepTreeObject(Element stepElement) {
		this.stepElement = stepElement;
	}
	/**
	 * 工艺树的工序节点展开
	 * @修改     马崇奇
	 * @date 2015-6-3
	 * @throws Exception
	 * @return
	 */

	public Vector expand() throws Exception {
		Vector result = new Vector();
//		Vector parts = XmlUtility.getAllPartIdentify(this.stepElement);
		List<Element> parts = XmlUtility.getAllPartElement(this.stepElement);
		Vector vector = new Vector();
		if(parts!=null)
			for(Element e:parts){
				vector.add(e);
			}
		if ((parts != null) && (parts.size() > 0)) {
			ResourceTreeObject p = new ResourceTreeObject("参装件", 3);
			p.addData(3, vector);
			result.add(p);
		}

		Vector equips = XmlUtility.getAllEquipmentIdentify(this.stepElement);
		Vector tools = XmlUtility.getAllToolIdentify(this.stepElement);
		Vector knifeTools = XmlUtility.getAllKnifeToolIdentify(this.stepElement);
		Vector sdashboard = XmlUtility.getAllSdashboardIdentify(this.stepElement);
		Vector unSdashboard = XmlUtility.getAllUnSdashboardIdentify(this.stepElement);
		Vector measures = XmlUtility.getAllMeasuresIdentify(this.stepElement);

		if(tools == null) {
			tools = knifeTools;
			tools.addAll(sdashboard);
			tools.addAll(unSdashboard);
			tools.addAll(measures);
		} else {
			tools.addAll(knifeTools);
			tools.addAll(sdashboard);
			tools.addAll(unSdashboard);
			tools.addAll(measures);
		}
		if (((equips != null) && (equips.size() > 0))
				|| ((tools != null) && (tools.size() > 0))
				|| ((knifeTools != null) && (knifeTools.size() > 0))) {
			ResourceTreeObject r = new ResourceTreeObject("工艺资源", 4);
			r.addData(1, equips);
			r.addData(0, tools);
			result.add(r);
		}

		Vector materials = XmlUtility.getAllMaterialIdentify(this.stepElement);
		if ((materials != null) && (materials.size() > 0)) {
			ResourceTreeObject m = new ResourceTreeObject("材料", 2);
			m.addData(2, materials);
			result.add(m);
		}

		int count = XmlUtility.countPaces(this.stepElement);
		for (int i = 0; i < count; i++) {
			Element productElement = XmlUtility.getPace(this.stepElement, i);
			XWPaceTreeObject pto = new XWPaceTreeObject(productElement);
			result.add(pto);
		}

		return result;
	}

	public Image getCloseImage() {
		return getOpenImage();
	}

	public String getDisplayName() {
		String number = XmlUtility.getAttributeValue(this.stepElement, "stepNumber");
		String name = XmlUtility.getAttributeValue(this.stepElement, "stepName");
		String workShop = XmlUtility.getAttributeValue(this.stepElement, "workShop");
		String workType = XmlUtility.getAttributeValue(this.stepElement, "workType");
		//return number + "_" + name + "_" + workShop + "_" + workType;
		return number + "_" + name;
	}

	public Image getOpenImage() {
		ImageIcon icon = new ImageIcon(getClass().getResource("/images/procedure.gif"));
		return icon.getImage();
	}

	public String getTipNoteText() {
		return getDisplayName();
	}

	public Element getTreeCellData() {
		return this.stepElement;
	}

	public int compareTo(Object arg0) {
		if (!(arg0 instanceof XWStepTreeObject)) {
			throw new RuntimeException("树节点类型不匹配！");
		}
		XWStepTreeObject temp = (XWStepTreeObject) arg0;
		String myNum = XmlUtility.getAttributeValue(this.stepElement, "stepNumber");
		String number = XmlUtility.getAttributeValue(temp.getTreeCellData(), "stepNumber");
		int result = number.compareTo(myNum);
		if (result == 0) {
			String myName = XmlUtility.getAttributeValue(this.stepElement, "stepName");
			String name = XmlUtility.getAttributeValue(temp.getTreeCellData(), "stepName");
			result = name.compareTo(myName);
		}
		return result;
	}

	public void setTreeCellData(Element data) {
		this.stepElement = data;
	}

	public boolean equals(String stepNumber, String stepName) {
		String number = XmlUtility.getAttributeValue(this.stepElement, "stepNumber");
		String name = XmlUtility.getAttributeValue(this.stepElement, "stepName");
		if ((number.equals(stepNumber)) && (name.equals(stepName)))
			return true;
		return false;
	}

	public String getStepNumber() {
		if (this.stepElement != null) {
			return this.stepElement.attributeValue("stepNumber");
		}
		return null;
	}
	@Override
	public String getEqualsName() {
		// TODO Auto-generated method stub
		return this.getDisplayName();
	}

}
