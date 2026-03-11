package com.glaway.mpm.qmIntf.material;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

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

import com.glaway.mpm.model.Material;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class MtTreeRenderer extends JPanel implements TreeCellRenderer {
	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(MtTreeRenderer.class);
	protected JLabel icon;
	protected JLabel text;
	private Icon icon1 = new ImageIcon(
			MtTreeRenderer.class.getResource("/image/close.png"));
	private Icon icon2 = new ImageIcon(
			MtTreeRenderer.class.getResource("/image/file.png"));
	private Icon icon3 = new ImageIcon(
			MtTreeRenderer.class.getResource("/image/open.png"));

	public MtTreeRenderer() {
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
		text.setPreferredSize(new Dimension(200, 18));
		if (isSelected) {
			text.setBackground(UIManager.getColor("Tree.selectionBackground"));
		} else {
			text.setBackground(UIManager.getColor("Tree.textBackground"));
		}
		if (value instanceof MtTreeNode) {
			if (expanded) {
				MtTreeNode mtTreeNode = (MtTreeNode) value;
				icon.setIcon(icon3);
				if (mtTreeNode.getChildCount() == 1) {
					Object obj = mtTreeNode.children().nextElement();
					if (obj instanceof MtNode) {
						MtNode node = (MtNode) obj;
						Material material = node.getMaterial();
						if ("mpm_material_temp_oid".equals(material.getOid())) {
							mtTreeNode.remove(node);
							List<Material> materials = ResourceIntf.getMaterialsByType(mtTreeNode.getTypePath());
							logger.debug("materials===" + materials);
							for (Material temp : materials) {
								MtNode mtNode = new MtNode(temp);
								mtTreeNode.add(mtNode);
							}
							// tree.revalidate();
							// tree.repaint();
							// tree.updateUI();
						}
					}
				}
			} else {
				icon.setIcon(icon1);
			}
			MtTreeNode mtTreeNode = (MtTreeNode) value;
			mtTreeNode.setSelected(isSelected);
			text.setText(mtTreeNode.getName());
		} else if (value instanceof MtNode) {
			MtNode mtNode = (MtNode) value;
			mtNode.setSelected(isSelected);
			text.setText(mtNode.getName() + "_" + mtNode.getMaterial().getCsize());
			icon.setIcon(icon2);
		}
		return this;
	}

}
