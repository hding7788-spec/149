package com.glaway.mpm.qmIntf.viewPanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import com.glaway.mpm.util.FileUtil;

public class CmpTreeRenderer extends JPanel implements TreeCellRenderer {
	private static final long serialVersionUID = 1L;

	protected JCheckBox checkBox;
	protected JLabel text;

	public CmpTreeRenderer() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(checkBox = new JCheckBox());
		checkBox.setOpaque(false);

		add(Box.createHorizontalStrut(4));
		add(text = new JLabel());
		text.setOpaque(true);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		setBackground(Color.WHITE);
		text.setPreferredSize(new Dimension(200, 15));
		checkBox.setEnabled(true);
		if (isSelected) {
			text.setBackground(UIManager.getColor("Tree.selectionBackground"));
		} else {
			text.setBackground(UIManager.getColor("Tree.textBackground"));
		}
		if (value instanceof CmpTreeNode) {
			CmpTreeNode cmpTreeNode = (CmpTreeNode) value;
			String name = cmpTreeNode.getImage().getName();
			String fullName = cmpTreeNode.getImage().getFullName();
			text.setText(name);
			if(cmpTreeNode.isUsed()){
				text.setForeground(Color.gray);
			}else{
				text.setForeground(Color.black);
			}

			checkBox.setSelected(cmpTreeNode.isSelected());

			if (isSelected) {
				String imagePath = FileUtil.getTmpPath("mpm/cad/cmpview/")
						+ name + "/" + fullName;
				CreoModelPanel.image.setIcon(new ImageIcon(imagePath));

				//TODO get image information from xml and then show it on the panel

//				CreoModelPanel.detailsPanel.getLblFileNameValue().setText("fileName");
//				CreoModelPanel.detailsPanel.getLblFileNoValue().setText("FileNo");
//				CreoModelPanel.detailsPanel.getLblFileStatusValue().setText("status");
//				CreoModelPanel.detailsPanel.getLblNameValue().setText("name");
//				CreoModelPanel.detailsPanel.getLblUpdatedByValue().setText("updater");
//				CreoModelPanel.detailsPanel.getLblUpdatedTimeValue().setText("2013-05-02");
//				CreoModelPanel.detailsPanel.setVisible(false);
			}
		} else if (value instanceof CmpNode) {
			CmpNode cmpNode = (CmpNode) value;
			String name = cmpNode.getNote().getName();
			text.setText(name);
			checkBox.setSelected(cmpNode.isSelected());
			if (isSelected) {
				String parentName = ((CmpTreeNode) cmpNode.getParent())
						.getImage().getName();
				String imagePath = FileUtil.getTmpPath("mpm/cad/cmpview/")
						+ parentName + "/" + name + ".gif";
				CreoModelPanel.image.setIcon(new ImageIcon(imagePath));
			}
		}
		return this;
	}
}