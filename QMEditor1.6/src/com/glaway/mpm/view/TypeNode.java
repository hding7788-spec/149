package com.glaway.mpm.view;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import org.dom4j.Document;

public class TypeNode extends DefaultMutableTreeNode {
	public static final int ROOTTYPE = 0;
	public static final int PRODUCTTYPE = 1;
	public static final int TECHNICSTYPE = 2;
	private String displayName = null;

	private boolean isLeafType = false;

	private int type = -1;
	public boolean isUsed = false;
	private NewTechnicsPart frame;

	public TypeNode(String displayName, int type) {
		super(displayName);
		this.displayName = displayName;
		this.type = type;
	}

	public ImageIcon getIcon() {
		ImageIcon icon = null;
		if (this.type == 0) {
			icon = new ImageIcon(getClass().getResource("/images/codingClassi.gif"));
		} else if (this.type == 1) {
			icon = new ImageIcon(getClass().getResource("/images/domain.gif"));
		} else if (this.type == 2) {
			icon = new ImageIcon(getClass().getResource("/images/modelTechnics.gif"));
		} else {
			icon = new ImageIcon(getClass().getResource("/images/route_emptyRoute.gif"));
		}
		return icon;
	}

	public String getFilterCondition() {
		if (this.type == 1) {
			String[] ss = this.displayName.split("_");
			return ss[0];
		}

		return this.displayName;
	}

	public void setLeafSign(boolean bool) {
		this.isLeafType = bool;
	}

	public boolean isLeafType() {
		return this.isLeafType;
	}

	public XWTreeNode addTechnics(Document doc) {
		XWTechnicsTreeObject xo = new XWTechnicsTreeObject(doc);
		if (!isLeaf()) {
			int count = getChildCount();
			for (int i = 0; i < count; i++) {
				TreeNode n = getChildAt(i);
				if ((n instanceof XWTreeNode)) {
					XWTreeNode xn = (XWTreeNode) n;
					if (xn.getObject().compareTo(xo) == 0)
						return xn;
				}
			}
		}
		XWTreeNode node = new XWTreeNode(xo);
		add(node);
		node.setParent(this);
		return node;
	}

	public boolean isUsed() {
		return isUsed;
	}

	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}
}
