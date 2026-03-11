package com.glaway.mpm.qmIntf.decoratePView.treePanel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import com.glaway.mpm.visual.view.tree.VaTreeRenderer;

public class DpTreeRenderer extends VaTreeRenderer implements TreeCellRenderer {
	private static final long serialVersionUID = 1L;


	private Icon icon1 = new ImageIcon(
			DpTreeRenderer.class.getResource("/image/step.gif"));
	private Icon icon2 = new ImageIcon(
			DpTreeRenderer.class.getResource("/image/pace.gif"));
	private Icon icon3 = new ImageIcon(
			DpTreeRenderer.class.getResource("/image/part.gif"));

	public DpTreeRenderer() {
		super();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, hasFocus);
//		if (isSelected) {
//			super.labelDisplay.setBackground(UIManager.getColor("Tree.selectionBackground"));
//		} else {
//			super.labelDisplay.setBackground(UIManager.getColor("Tree.textBackground"));
//		}
		if (value instanceof DpStepNode) {
			DpStepNode node = (DpStepNode) value;
			Step step = node.getStep();
			super.labelDisplay.setText(step.getNumber() + "_" + step.getName() + "_"
					+ step.getWorkshopName() + "_" + step.getShopTypeName());
//			chkSelect.setSelected(node.isSelected());
			super.labelDisplay.setIcon(icon1);
//			if(node.isAnno()){
//				super.labelAnno.setIcon(new ImageIcon(VaTreeRenderer.class.getResource("/images/cappBook.gif")));
//
//	         }else{
//	        	 super.labelAnno.setIcon(null);
//	         }
		} else if (value instanceof DpPaceNode) {
			DpPaceNode node = (DpPaceNode) value;
			super.labelDisplay.setText(node.getPace().getNumber());
//			chkSelect.setSelected(node.isSelected());
			super.labelDisplay.setIcon(icon2);

		}else if (value instanceof DpPartNode) {
			DpPartNode node = (DpPartNode) value;
			super.labelDisplay.setText(node.getPart().toString());
//			chkSelect.setSelected(node.isSelected());
			super.labelDisplay.setIcon(icon3);
//			System.out.println("node.getPart().getZcmark()=========================="+node.getPart().getZcmark());
//			if("C".equals(node.getPart().getZcmark())){
//		        labelDisplay.setForeground(Color.RED);
//			}else if("Z".equals(node.getPart().getZcmark())){
//		        labelDisplay.setForeground(Color.BLUE);
//			}
		}

		return this;
	}
}
