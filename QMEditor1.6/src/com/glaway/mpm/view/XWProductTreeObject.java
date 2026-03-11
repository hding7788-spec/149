package com.glaway.mpm.view;

import com.glaway.mpm.util.BomXMLUtil;


import java.awt.Image;
import java.util.Vector;
import javax.swing.ImageIcon;

import com.glaway.mpm.visual.log.VaLogger;
import org.dom4j.Document;
import org.dom4j.Element;

public class XWProductTreeObject implements XWTreeObject {
	private Document productDom;
	private Element productElement;
	private static VaLogger logger = VaLogger.getLogger(XWPartTreePanel.class);

	public XWProductTreeObject(Document productDom) {
		this.productDom = productDom;
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
			XWPartTreeObject partObject = new XWPartTreeObject(part);
			coll.add(partObject);
		}
		return coll;
	}

	public Image getCloseImage() {
		return getOpenImage();
	}

	public String getDisplayName() {
		return BomXMLUtil.getProductIdentufy(this.productElement);
	}

	public Image getOpenImage() {
		ImageIcon icon = new ImageIcon(getClass().getResource("/images/domain.gif"));
		return icon.getImage();
	}

	public String getTipNoteText() {
		return BomXMLUtil.getProductIdentufy(this.productElement);
	}

	public Element getTreeCellData() {
		return this.productElement;
	}

	public int compareTo(Object arg0) {
		if (!(arg0 instanceof XWProductTreeObject)) {
			throw new RuntimeException("树节点类型不匹配！");
		}
		XWProductTreeObject temp = (XWProductTreeObject) arg0;
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
