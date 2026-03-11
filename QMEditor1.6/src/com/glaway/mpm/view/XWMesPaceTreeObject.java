package com.glaway.mpm.view;

import com.glaway.mpm.util.XmlUtility;

import java.awt.Image;
import java.util.List;
import java.util.Vector;
import javax.swing.ImageIcon;
import org.dom4j.Element;

public class XWMesPaceTreeObject implements XWTreeObject {
	private Element paceElement;

	public XWMesPaceTreeObject(Element paceElement) {
		this.paceElement = paceElement;
	}
	/**
	 * 工艺树的工步节点展开
	 * @修改      马崇奇
	 * @date 2015-6-3
	 * @throws Exception
	 * @return
	 */

	public Vector expand() throws Exception {
		Vector result = new Vector();
//		Vector parts = XmlUtility.getAllPartIdentify(this.paceElement);
		List<Element> parts = XmlUtility.getAllPartElement(this.paceElement);
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
		Vector equips = XmlUtility.getAllEquipmentIdentify(this.paceElement);
		Vector tools = XmlUtility.getAllToolIdentify(this.paceElement);
		Vector knifeTools = XmlUtility.getAllKnifeToolIdentify(this.paceElement);
		Vector sdashboard = XmlUtility.getAllSdashboardIdentify(this.paceElement);
		Vector unSdashboard = XmlUtility.getAllUnSdashboardIdentify(this.paceElement);
		Vector measures = XmlUtility.getAllMeasuresIdentify(this.paceElement);

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
				|| ((tools != null) && (tools.size() > 0))) {
			ResourceTreeObject r = new ResourceTreeObject("工艺资源", 4);
			r.addData(1, equips);
			r.addData(0, tools);
			result.add(r);
		}

		Vector materials = XmlUtility.getAllMaterialIdentify(this.paceElement);
		if ((materials != null) && (materials.size() > 0)) {
			ResourceTreeObject m = new ResourceTreeObject("材料", 2);
			m.addData(2, materials);
			result.add(m);
		}

		return result;
	}

	public Image getCloseImage() {
		return getOpenImage();
	}

	public String getDisplayName() {
		String number = XmlUtility.getAttributeValue(this.paceElement,
				"stepNumber");

		return number;
	}

	public Image getOpenImage() {
		ImageIcon icon = new ImageIcon(getClass().getResource(
				"/images/sequence.gif"));
		return icon.getImage();
	}

	public String getTipNoteText() {
		String number = XmlUtility.getAttributeValue(this.paceElement,
				"stepNumber");
		String name = XmlUtility.getAttributeValue(this.paceElement, "stepName");
		return number + "_" + name;
	}

	public Element getTreeCellData() {
		return this.paceElement;
	}

	public int compareTo(Object arg0) {
		if (!(arg0 instanceof XWMesPaceTreeObject)) {
			throw new RuntimeException("树节点类型不匹配！");
		}
		XWMesPaceTreeObject temp = (XWMesPaceTreeObject) arg0;

		String myNum = XmlUtility
				.getAttributeValue(this.paceElement, "stepNumber");
		String number = XmlUtility.getAttributeValue(temp.getTreeCellData(),
				"stepNumber");

		int result = number.compareTo(myNum);
		if (result == 0) {
			String myContent = XmlUtility.getProcedureContent(this.paceElement);
			String conntent = XmlUtility.getProcedureContent(temp
					.getTreeCellData());
			result = conntent.compareTo(myContent);
		}
		return result;
	}

	public void setTreeCellData(Element data) {
		this.paceElement = data;
	}
	@Override
	public String getEqualsName() {
		// TODO Auto-generated method stub
		return this.getDisplayName();
	}
}
