package com.glaway.mpm.view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.dom4j.Element;

import com.glaway.mpm.model.UploadTechnics;
import com.glaway.mpm.util.ObjectTransfer;

public class XWTypeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;
	private static String userOID = "";
	private String state = "";
	private String creator;
	private String isKey;

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, this.selected, expanded, leaf, row, hasFocus);
		if ((value instanceof TypeNode)) {
			TypeNode temp = (TypeNode) value;
			setIcon(temp.getIcon());
		} else if ((value instanceof XWTreeNode)) {
			XWTreeNode node = (XWTreeNode) value;
			setIcon(new ImageIcon(node.getOpenImage()));
		}
		if (sel) {
			setForeground(getTextSelectionColor());
		} else {
			setForeground(getTextNonSelectionColor());
		}
		if ((value instanceof XWTreeNode)) {
			XWTreeNode node = (XWTreeNode) value;
			XWTreeObject to = node.getObject();
			if ((to instanceof XWStepTreeObject)) {
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
				if (root.getChildCount() == 1) {
					XWTreeNode techNode = (XWTreeNode) root.getChildAt(0);
					Element techEle = techNode.getObject().getTreeCellData();
					UploadTechnics technics = ObjectTransfer.technicsElementToUploadTechnics(techEle);
					if (!NewTechnicsPart.downLoadTech.contains(technics)) {
						String unite = techEle.attributeValue("unite");
						//if ((unite != null) && (!unite.equalsIgnoreCase("common"))) {
							Element step = to.getTreeCellData();
							String responser = step.attributeValue("responser");
							if ((responser != null) && (responser.trim().length() > 0)) {
								if (responser.equals(userOID)) {
									setForeground(Color.red);
									if((!"正在工作".equals(state) && !"修改中".equals(state))||!creator.equals(NewTechnicsPart.currentUser)) {
										setForeground(Color.gray);
									}
								}
							}
							isKey = step.attributeValue("isKey");
							if("true".equals(isKey)) {
								setForeground(Color.red);
							}
						//}
					}
				}
			} else if ((to instanceof XWTechnicsTreeObject)) {
				XWTechnicsTreeObject obj = (XWTechnicsTreeObject)node.getObject();
				Element element = obj.getTreeCellData();
				state = element.attributeValue("lifecycle");
				creator = element.attributeValue("creator");
				if((!"正在工作".equals(state) && !"修改中".equals(state))||!creator.equals(NewTechnicsPart.currentUser)) {
					setForeground(Color.gray);
				}
			} else if (to instanceof XWPaceTreeObject) {
				Element step = to.getTreeCellData();
				isKey = step.attributeValue("isKey");
				if("true".equals(isKey)) {
					setForeground(Color.red);
				}
			}

			TypeNode root = (TypeNode)node.getRoot();
			if(!root.isUsed) {
				setForeground(Color.gray);
			}
		}
		this.selected = sel;

		if((!"正在工作".equals(state) && !"修改中".equals(state))||!creator.equals(NewTechnicsPart.currentUser)) {
			setForeground(Color.gray);
		}

		return this;
	}
}
