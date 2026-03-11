package com.glaway.mpm.view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.dom4j.Element;

import com.glaway.mpm.mesParameter.ui.MesParameterMainFrame;
import com.glaway.mpm.model.UploadTechnics;
import com.glaway.mpm.util.ObjectTransfer;

public class MESTypeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;
	private static String userOID = "";
	private String state = "";
	private String creator;
	private String isKey;

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, this.selected, expanded, leaf, row, hasFocus);
		if ((value instanceof XWTreeNode)) {
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
			if(to instanceof XWMesStepTreeObject || to instanceof XWStepTreeObject){
				String stepNumber = node.getDisplayName();
				stepNumber = stepNumber.substring(0, stepNumber.indexOf("_"));
				String mainNumber = MesParameterMainFrame.getProdureNumber();
				if(stepNumber != null && stepNumber.equals(mainNumber)){
					setForeground(Color.red);
				}
			}
		}
		this.selected = sel;



		return this;
	}
}
