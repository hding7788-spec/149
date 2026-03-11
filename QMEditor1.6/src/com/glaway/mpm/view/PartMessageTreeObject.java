package com.glaway.mpm.view;

import java.awt.Image;
import java.util.Vector;
import javax.swing.ImageIcon;
import org.dom4j.Element;

public class PartMessageTreeObject implements XWTreeObject {
	private String partIdentify = "";

	private String type = "";

	private String count = null;


	private Element element = null;
	public PartMessageTreeObject( Element element) {
		this.element = element;
		String tempPartIdentify = genPartIdentify(element);
		if (tempPartIdentify != null) {
			int index1 = tempPartIdentify.indexOf("$");
			int index2 = tempPartIdentify.indexOf("#");
			if ((index1 >= 0) && (index1 < tempPartIdentify.length())) {
				this.partIdentify = tempPartIdentify.substring(0, index1);
				this.partIdentify = this.partIdentify.trim();

				if ((index2 >= index1) && (index2 < tempPartIdentify.length())) {
					this.type = tempPartIdentify.substring(index1 + 1, index2);
					this.count = tempPartIdentify.substring(index2 + 1);
				} else {
					this.type = tempPartIdentify.substring(index1 + 1);
				}
			}
			if ((this.count == null) || (this.count.trim().length() == 0))
				this.count = "1";
		}
	}

	private String genPartIdentify(Element part){
		String number = part.attributeValue("partNumber");
		if (number == null)
			number = "";
		String name = part.attributeValue("partName");
		if (name == null)
			name = "";
		String version = part.attributeValue("version");
		if (version == null)
			version = "";
		String identify = number + "(" + name + ") " + version;
		String type = part.attributeValue("partType");
		String count = part.attributeValue("useCount");
		if ((count == null) || (count.trim().length() == 0))
			count = "1";
		identify= identify + "$" + type + "#" + count;
		return identify;
	}

	public Vector expand() throws Exception {
		Vector vec = new Vector();
		return vec;
	}

	public Image getCloseImage() {
		return getOpenImage();
	}

	public String getDisplayName() {
		if ((this.count != null) && (this.count.trim().length() > 0)
				&& (!this.count.equals("1"))) {
			return this.partIdentify + " x " + this.count;
		}
		return this.partIdentify;
	}

	public Image getOpenImage() {
		String iconName = "/images/part.gif";
		if ((this.type != null) && (this.type.trim().length() > 0)) {
			if (this.type.equalsIgnoreCase("middle")) {
				iconName = "/images/zhongjian.gif";
			}
			if (this.type.equalsIgnoreCase("assistant")) {
				iconName = "/images/fujian.gif";
			}
		}
		ImageIcon icon = new ImageIcon(getClass().getResource(iconName));
		return icon.getImage();
	}

	public String getTipNoteText() {
		return getDisplayName();
	}

	public Element getTreeCellData() {
		return element;
	}

	public int compareTo(Object arg0) {
		if (!(arg0 instanceof PartMessageTreeObject)) {
			throw new RuntimeException("树节点类型不匹配！");
		}
		PartMessageTreeObject temp = (PartMessageTreeObject) arg0;
		String iden = temp.getDisplayName();
		String me = getDisplayName();
		return iden.compareTo(me);
	}

	public void setTreeCellData(Element data) {
		this.element = data;
	}

	public String getPartIdentify() {
		return partIdentify;
	}

	@Override
	public String getEqualsName() {
		// TODO Auto-generated method stub
		return this.getDisplayName();
	}

}
