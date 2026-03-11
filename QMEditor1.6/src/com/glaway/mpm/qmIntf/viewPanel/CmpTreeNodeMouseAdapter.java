package com.glaway.mpm.qmIntf.viewPanel;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.glaway.mpm.visual.log.VaLogger;

/**
 * <br>
 * Created on 2010-10-27
 * 
 * @author Alex.Huang
 */
public class CmpTreeNodeMouseAdapter extends MouseAdapter {
	private VaLogger log = VaLogger.getLogger();
	private JTree tree;

	public CmpTreeNodeMouseAdapter(JTree tree) {
		this.tree = tree;
	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int row = tree.getRowForLocation(x, y);
		Rectangle rect = tree.getRowBounds(row);
		int height = 0;

		if (rect != null)
			height = rect.height + rect.x;

		if (x < height) {
			TreePath path = tree.getPathForRow(row);
			if (path != null) {
				if (path.getLastPathComponent() instanceof CmpTreeNode) {
					CmpTreeNode cmpTreeNode = (CmpTreeNode) path.getLastPathComponent();
					boolean isSelected = !cmpTreeNode.isSelected();
					cmpTreeNode.setSelected(isSelected);
					if (!isSelected) {
						Enumeration<CmpNode> nodes = cmpTreeNode.children();
						while (nodes.hasMoreElements()) {
							nodes.nextElement().setSelected(false);
						}
					}
				} else if (path.getLastPathComponent() instanceof CmpNode) {
					CmpNode cmpNode = (CmpNode) path.getLastPathComponent();
					boolean isSelected = !cmpNode.isSelected();
					cmpNode.setSelected(isSelected);
					if (isSelected) {
						((CmpTreeNode) cmpNode.getParent()).setSelected(isSelected);
					}
				}

				/*
				 * log.debug("mouseClicked"); Vector bboxs = node.getBboxes();
				 * if (bboxs != null && isSelected) { for (Object obj : bboxs) {
				 * String[] bbox = (String[]) obj; if (bbox != null || bbox[0]
				 * != null && bbox[0].trim().length() > 0) { String[]
				 * pointStringArray = bbox[0].split(" "); VaMainframe main =
				 * VaMainframe.getInstance(); main.setX1(pointStringArray[0]);
				 * main.setX2(pointStringArray[1]);
				 * main.setY1(pointStringArray[2]);
				 * main.setY2(pointStringArray[3]);
				 * main.setZ1(pointStringArray[4]);
				 * main.setZ2(pointStringArray[5]); } } }
				 */
				// Dennis 为了做连动节点，必须重画
				tree.revalidate();
				tree.repaint();
			}
		}
	}
}
