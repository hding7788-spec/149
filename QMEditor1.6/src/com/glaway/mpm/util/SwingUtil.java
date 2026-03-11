package com.glaway.mpm.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.Enumeration;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.dom4j.Element;

import com.glaway.mpm.qmIntf.common.model.CommonComboBox;
import com.glaway.mpm.view.XWPartTreeObject;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class SwingUtil {
	private static VaLogger logger = VaLogger.getLogger(SwingUtil.class);

	public static void setIcon(JLabel label, byte[] bytes) {
		try {
			if (bytes != null) {
				label.setIcon(new ImageIcon(bytes));
			} else {
				label.setIcon(new ImageIcon());
			}
		} catch (Exception ex) {
			logger.debug("Image Error= " + ex.getMessage());
			label.setIcon(new ImageIcon());
		}
	}

	public static void setIcon1(JLabel label, String filePath) {
		try {
			if (filePath != null) {
				label.setIcon(new ImageIcon(filePath));
			} else {
				label.setIcon(new ImageIcon());
			}
		} catch (Exception ex) {
			logger.debug("Image Error= " + ex.getMessage());
			label.setIcon(new ImageIcon());
		}
	}

	public static byte[] getIcon(String oid) {
		return ResourceIntf.getResourceImage(oid);
	}

	public static int SCREEN_WIDTH = Toolkit.getDefaultToolkit()
			.getScreenSize().width;

	public static int SCREEN_HEIGHT = Toolkit.getDefaultToolkit()
			.getScreenSize().height;

	public static void setMiddle(JDialog dialog) {
		dialog.setLocation((SCREEN_WIDTH - dialog.getWidth()) / 2,
				(SCREEN_HEIGHT - dialog.getHeight()) / 2);
	}

	public static void setMiddle(JFrame frame) {
		frame.setLocation((SCREEN_WIDTH - frame.getWidth()) / 2,
				(SCREEN_HEIGHT - frame.getHeight()) / 2);
	}

	public static void showMessageDialog(String message, String title, int flag) {
		JOptionPane.showMessageDialog(null, message, title, flag);
	}

	public static int showConfirmDialog(String message, String title, int flag) {
		return JOptionPane.showConfirmDialog(null, message, "提示", flag);
	}

	/**
	 * 展开到叶节点的前一阶
	 *
	 * @param tree
	 * @param treeNode
	 * @param clazz
	 */
	public static void expandBeforeLeaf(JTree tree,
			DefaultMutableTreeNode treeNode, Class<?> clazz) {
		if (tree != null) {
			expandNode(tree, treeNode, clazz);
		}

	}

	private static void expandNode(JTree tree, DefaultMutableTreeNode treeNode,
			Class<?> clazz) {
		if (treeNode != null && treeNode.getChildCount() != 0) {
			Enumeration children = treeNode.children();
			while (children.hasMoreElements()) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) children
						.nextElement();
				if (!child.getClass().equals(clazz)) {
					tree.expandPath(new TreePath(((DefaultTreeModel) tree
							.getModel()).getPathToRoot(treeNode)));
					expandNode(tree, child, clazz);
				}
			}
		}
	}

	public static void printNode(XWTreeNode treeNode) {
		System.out.println(treeNode.getDisplayName());
		if (treeNode != null && treeNode.getChildCount() != 0) {
			Enumeration children = treeNode.children();
			while (children.hasMoreElements()) {
				XWTreeNode child = (XWTreeNode) children.nextElement();
				printNode(child);
			}
		}
	}

	public static void expandAll(JTree tree) {
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
	}

	public static void setLookAndFeel() {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	public static ImageIcon scaleImage(ImageIcon icon, int width, int height) {
		int iconWidth = icon.getIconWidth();
		int iconHeight = icon.getIconHeight();

		if (iconWidth == width && iconHeight == height) {
			return icon;
		}
		Image image = icon.getImage();
		image = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);

		return new ImageIcon(image);
	}

	/**
	 * 展开到责任的part
	 *
	 * @param partOids
	 * @param tree
	 */
	public static void expandToResponsePart(List<String> partOids, JTree tree) {
		Object root = tree.getModel().getRoot();
		if (root == null) {
			return;
		}
		tree.expandPath(new TreePath((XWTreeNode) root));
		if (partOids == null || partOids.size() == 0) {
			return;
		}
		for (String temp : partOids) {
			expandPart(temp, tree, (DefaultMutableTreeNode) root);
		}
	}

	/**
	 * 展开
	 *
	 * @param partOid
	 * @param tree
	 * @param treeNode
	 */
	private static void expandPart(String partOid, JTree tree,
			DefaultMutableTreeNode treeNode) {

		if (treeNode != null && treeNode.getChildCount() != 0) {
			Enumeration children = treeNode.children();
			while (children.hasMoreElements()) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) children
						.nextElement();
				if (child.getClass().equals(XWTreeNode.class)) {
					XWTreeNode node = (XWTreeNode) child;
					Object obj = node.getObject();
					if (obj instanceof XWPartTreeObject) {
						XWPartTreeObject partObject = (XWPartTreeObject) obj;
						Element element = partObject.getTreeCellData();
						if (partOid.equals(element.attributeValue("oid"))) {
							if (child.children().hasMoreElements()) {
								tree.expandPath(new TreePath(
										((DefaultTreeModel) tree.getModel())
												.getPathToRoot(child)));
							} else {
								tree.expandPath(new TreePath(
										((DefaultTreeModel) tree.getModel())
												.getPathToRoot(child
														.getParent())));
							}
						}
						expandPart(partOid, tree, child);
					}
				}

			}
		}
	}

	public static JComboBox generateBooleanComboBox(List<String> selections) {
		JComboBox comboBox = new CommonComboBox();
		if (selections != null) {
			for (String temp : selections) {
				comboBox.addItem(temp);
			}
		} else {
			comboBox.addItem("是");
			comboBox.addItem("否");
		}

		return comboBox;
	}

	/**
	 * 设置最小宽度
	 *
	 * @param columnModel
	 * @param is
	 * @param size
	 */
	public static void setMinColumnSize(TableColumnModel columnModel, int[] is,
			int size) {
		if (is != null && is.length != 0) {
			for (int i : is) {
				TableColumn column = columnModel.getColumn(i);
				column.setPreferredWidth(size);
				column.setMinWidth(size);
				column.setMaxWidth(2147483647);
			}
		}
	}

	/**
	 * 设置最大宽度
	 *
	 * @param columnModel
	 * @param is
	 * @param size
	 */

	public static void setMaxColumnSize(TableColumnModel columnModel, int[] is,
			int size) {
		if (is != null && is.length != 0) {
			for (int i : is) {
				TableColumn column = columnModel.getColumn(i);
				column.setPreferredWidth(size);
				column.setMaxWidth(size);
				column.setMinWidth(2147483647);
			}
		}
	}

	/**
	 * 设置固定宽度
	 *
	 * @param columnModel
	 * @param is
	 * @param size
	 */
	public static void setFreezeColumnSize(TableColumnModel columnModel,
			int[] is, int size) {
		if (is != null && is.length != 0) {
			for (int i : is) {
				TableColumn column = columnModel.getColumn(i);
				column.setPreferredWidth(size);
				column.setMinWidth(size);
				column.setMaxWidth(size);
			}
		}
	}
}
