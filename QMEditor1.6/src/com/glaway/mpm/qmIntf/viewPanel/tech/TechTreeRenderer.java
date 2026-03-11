package com.glaway.mpm.qmIntf.viewPanel.tech;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.TreeCellRenderer;

import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpTreeRenderer;
import com.glaway.mpm.qmIntf.viewPanel.cmp.CmpImageNode;
import com.glaway.mpm.util.CappJavaUtil;

public class TechTreeRenderer extends JPanel implements TreeCellRenderer {
	private static final long serialVersionUID = 1L;

	protected JLabel icon;
	protected JLabel text;
	private Icon icon1 = new ImageIcon(
			TechTreeRenderer.class.getResource("/image/step.gif"));
	private Icon icon2 = new ImageIcon(
			TechTreeRenderer.class.getResource("/image/pace.gif"));
	private Icon icon3 = new ImageIcon(
			TechTreeRenderer.class.getResource("/images/zhongjian.gif"));
	private Icon icon4 = new ImageIcon(
			DpTreeRenderer.class.getResource("/image/technics.gif"));

	private Map<String, String> numbers;

	public TechTreeRenderer(Map<String, String> numbers) {
		this.numbers = numbers;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		icon = new JLabel() {
			private static final long serialVersionUID = 1L;

			public void setBackground(Color color) {
				if (color instanceof ColorUIResource)
					color = null;
				super.setBackground(color);
			}

		};
		add(icon);
		add(Box.createHorizontalStrut(4));
		add(text = new JLabel());
		text.setOpaque(true);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		setBackground(Color.WHITE);
		if (isSelected) {
			text.setBackground(UIManager.getColor("Tree.selectionBackground"));
		} else {
			text.setBackground(UIManager.getColor("Tree.textBackground"));
		}
		if (value instanceof TechStepTreeNode) {
			TechStepTreeNode cmpTreeNode = (TechStepTreeNode) value;
			text.setText(cmpTreeNode.getStepNumber());
			if (cmpTreeNode.isStep()) {
				icon.setIcon(icon1);
			} else {
				icon.setIcon(icon2);
			}
		} else if (value instanceof CmpImageNode) {
			icon.setIcon(icon3);
			CmpImageNode imageNode = (CmpImageNode) value;
			String name = imageNode.getName();
			int index = name.lastIndexOf("M");
			String number = "";
			if (index != -1) {
				number = numbers.get(name.substring(index + 1));
			}
			text.setText(name + " " + imageNode.getModelName() + " "
					+ CappJavaUtil.convertNull(number));
		} else if (value instanceof TechImageNode) {
			icon.setIcon(icon3);
			TechImageNode imageNode = (TechImageNode) value;
			String name = imageNode.getName();
			int index = name.lastIndexOf("M");
			String number = "";
			if (index != -1) {
				number = numbers.get(name.substring(index + 1));
			}
			text.setText(name + " " + imageNode.getModelName() + " " + number);
		} else if (value instanceof TechTreeRootNode) {
			icon.setIcon(icon4);
			TechTreeRootNode techTreeNode = (TechTreeRootNode) value;
			techTreeNode.setSelected(isSelected);
			text.setText(techTreeNode.getName());
		}
		return this;
	}

	public Dimension getPreferredSize() {

		Dimension d_label = icon.getPreferredSize();
		Dimension d_info = text.getPreferredSize();

		return new Dimension(d_label.width + d_info.width + 16 + 6, 16);

	}

	// -----------------------------------------------
	/**
	 * 
	 * doLayout
	 * 
	 */
	// ------------------------------------------------
	public void doLayout() {
		Dimension d_label = icon.getPreferredSize();
		Dimension d_info = text.getPreferredSize();

		icon.setLocation(0, 0);
		icon.setBounds(0, 0, d_label.width + 2, d_label.height);

		text.setLocation(0 + d_label.width + 2, 0);
		text.setBounds(0 + d_label.width + 2, 0, d_info.width, d_info.height);

	}
}
