package com.glaway.mpm.view;

import java.awt.Image;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.dom4j.Element;

public class ResourceTreeObject implements XWTreeObject {
	public static final int TOOLTYPE = 0;
	public static final int EQUIPTYPE = 1;
	public static final int MATERIALTYPE = 2;
	public static final int PARTTYPE = 3;
	public static final int RESOURCETYPE = 4;
	private String displayName = "";

	private int childType = -1;

	private HashMap resources = new HashMap();

	public ResourceTreeObject(String displayName, int type) {
		this.childType = type;
		this.displayName = displayName;
	}

	public void addData(int type, Vector data) {
		if (data != null) {
			this.resources.put(String.valueOf(type), data);
		}
	}

//	public void addData(int type, List<Element> data) {
//		if (data != null) {
//			this.resources.put(String.valueOf(type), data);
//		}
//	}

	public String getDisplayName() {
		return this.displayName;
	}

	public boolean equals(String message) {
		if ((message != null) && (message.trim().length() > 0)) {
			if (message.equals(this.displayName)) {
				return true;
			}
		}
		return false;
	}

	public Vector expand() throws Exception {
		Vector vec = new Vector();
		if ((this.resources != null) && (this.resources.size() > 0)) {
			if (this.childType == 2) {
				Vector temp = (Vector) this.resources.get(String.valueOf(2));
				if ((temp != null) && (temp.size() > 0)) {
					for (int i = 0; i < temp.size(); i++) {
						String message = (String) temp.get(i);
						XWTreeObject xo = new MaterialTreeObject(message);
						vec.add(xo);
					}
				}
			}
			if (this.childType == 3) {
				Vector temp = (Vector) this.resources.get(String.valueOf(3));
				if ((temp != null) && (temp.size() > 0)) {
					for (int i = 0; i < temp.size(); i++) {
//						String message = (String) temp.get(i);
						Element element = (Element)temp.get(i);
						XWTreeObject xo = new PartMessageTreeObject(element);
						vec.add(xo);
					}
				}
			}
			if (this.childType == 4) {
				Vector temp = (Vector) this.resources.get(String.valueOf(1));
				if ((temp != null) && (temp.size() > 0)) {
					for (int i = 0; i < temp.size(); i++) {
						String message = (String) temp.get(i);
						XWTreeObject xo = new EquipTreeObject(message);
						vec.add(xo);
					}

				}

				temp = (Vector) this.resources.get(String.valueOf(0));
				if ((temp != null) && (temp.size() > 0)) {
					for (int i = 0; i < temp.size(); i++) {
						String message = (String) temp.get(i);
						XWTreeObject xo = new ToolTreeObject(message);
						vec.add(xo);
					}
				}
			}
		}
		return vec;
	}

	public Image getCloseImage() {
		return getOpenImage();
	}

	public Image getOpenImage() {
		ImageIcon icon = new ImageIcon(getClass()
				.getResource("/images/jsf.gif"));
		return icon.getImage();
	}

	public String getTipNoteText() {
		return this.displayName;
	}

	public Element getTreeCellData() {
		return null;
	}

	public void setTreeCellData(Element data) {
	}

	public int compareTo(Object arg0) {
		if (!(arg0 instanceof ResourceTreeObject)) {
			throw new RuntimeException("树节点类型不匹配！");
		}
		ResourceTreeObject temp = (ResourceTreeObject) arg0;
		String number = temp.getDisplayName();
		String me = getDisplayName();
		return number.compareTo(me);
	}

	public int getType() {
		return this.childType;
	}

	@Override
	public String getEqualsName() {
		// TODO Auto-generated method stub
		return this.getDisplayName();
	}
}
