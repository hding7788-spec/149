package com.glaway.mpm.view;

import com.glaway.mpm.util.BomXMLUtil;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class XWTreeObjectForSearchTech implements XWTreeObject {
	private Document productDom;
	private Element productElement;
	private String technicsOid;

	public XWTreeObjectForSearchTech(Document productDom, String technicsOid) {
		this.productDom = productDom;
		this.technicsOid = technicsOid;
		try {
			this.productElement = BomXMLUtil.getProduct(productDom);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Vector expand() throws Exception {
		Element part = BomXMLUtil.getMainPart(this.productElement);
		Vector coll = new Vector();
		if (part != null) {
			XWPartTreeObjectForSearchTech partObject = new XWPartTreeObjectForSearchTech(part, technicsOid);
			coll.add(partObject);
		}
		return coll;
	}

	public Image getCloseImage() {
		return getOpenImage();
	}

	public String getDisplayName() {
		String displayName = "搜索到的工艺";
		return displayName;
	}

	public Image getOpenImage() {
		ImageIcon icon = new ImageIcon(getClass().getResource("/images/viewm.gif"));
		return icon.getImage();
	}

	public String getTipNoteText() {
		String displayName = "搜索到的工艺";
//		return BomXMLUtil.getProductIdentufy(this.productElement);
		return  displayName;
	}

	public Element getTreeCellData() {
		return this.productElement;
	}

	public int compareTo(Object arg0) {
		if (!(arg0 instanceof XWTreeObjectForSearchTech)) {
			throw new RuntimeException("树节点类型不匹配！");
		}
		XWTreeObjectForSearchTech temp = (XWTreeObjectForSearchTech) arg0;
		String number = temp.getTreeCellData().attributeValue("productNumber");
		String me = this.productElement.attributeValue("productNumber");
		return number.compareTo(me);
	}

	public void setTreeCellData(Element data) {
		this.productDom = data.getDocument();
		this.productElement = data;
	}

	@Override
	public String getEqualsName() {
		// TODO Auto-generated method stub
		return this.getDisplayName();
	}
}
