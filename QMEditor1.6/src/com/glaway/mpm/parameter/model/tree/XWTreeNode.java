package com.glaway.mpm.parameter.model.tree;

import java.awt.Image;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import com.glaway.mpm.log.VaLogger;

/**
 * 继承于DefaultMutableTreeNode的树节点对象，封装了各种类型的树节点对象。
 *
 * @author 龙秀川
 *
 */
public class XWTreeNode extends DefaultMutableTreeNode {
	private static VaLogger logger = VaLogger.getLogger(XWTreeNode.class.getName());
	private static final long serialVersionUID = 1L;

	private boolean isSelected;

	private boolean isSearched;
	/** 树节点对象抽象接口 */
	private XWTreeObject treeObject;

	public XWTreeNode(XWTreeObject xwObject) {
		super(xwObject);
		this.treeObject = xwObject;
	}

	public XWTreeNode addChild(XWTreeObject child) {
		XWTreeNode node = new XWTreeNode(child);
		add(node);
		node.setParent(this);
		return node;
	}

	public void expand() {
		try {
			List<XWTreeObject> datas = this.treeObject.expand();
			if(datas != null) {
				for (int i = 0; i < datas.size(); i++) {
					XWTreeObject child = datas.get(i);
					addChild(child);
				}
			}
		} catch (Exception e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, "我的任务加载出现错误！", "提示", 1);
		}
	}

	public void expandAll() {
		try {
			List<XWTreeObject> datas = this.treeObject.expand();
			for (int i = 0; i < datas.size(); i++) {
				XWTreeObject child = datas.get(i);
				XWTreeNode childNode = addChild(child);
				childNode.expandAll();
			}
		} catch (Exception e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, "我的任务加载出现错误！", "提示", 1);
		}
	}

	public void expandNode() {
		try {
			List<XWTreeObject> datas = this.treeObject.expandNode();
			if(datas != null) {
				for (int i = 0; i < datas.size(); i++) {
					XWTreeObject child = datas.get(i);
					addChild(child);
				}
			}
		} catch (Exception e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, "我的任务加载出现错误！", "提示", 1);
		}
	}

	public Image getCloseImage() {
		return treeObject.getCloseImage();
	}

	public String getDisplayName() {
		return treeObject.getDisplayName();
	}

	public Image getOpenImage() {
		return treeObject.getOpenImage();
	}

	public XWTreeNode getParentNode() {
		return (XWTreeNode)getParent();
	}

	public String getTipNoteText() {
		return treeObject.getTipNoteText();
	}

	public XWTreeObject getTreeObject() {
		return treeObject;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public void setTreeObject(XWTreeObject treeObject) {
		this.treeObject = treeObject;
	}

	public boolean isSearched() {
		return isSearched;
	}

	public void setSearched(boolean isSearched) {
		this.isSearched = isSearched;
	}

	@Override
	public String toString() {
		return treeObject.getDisplayName();
	}

}
