package com.glaway.mpm.parameter.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTree;

import com.glaway.mpm.parameter.commonui.CommonJPopupMenu;
import com.glaway.mpm.parameter.helper.MPMParameterProcessor;
import com.glaway.mpm.parameter.model.data.CmParameterType;
import com.glaway.mpm.parameter.model.tree.XWTreeNode;
import com.glaway.mpm.parameter.ui.CreateParameterTypeDialog;

/**
 * 检验特性管理右键菜单。
 *
 */
public class CheckParameterPopupMenu extends CommonJPopupMenu implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JTree tree;
	/** 添加检验特性 */
    private JMenuItem addTypeItem;
    /** 删除 */
    private JMenuItem deleteTypeItem;
    private CreateParameterTypeDialog dialog;

	public CheckParameterPopupMenu(JTree tree) {
		this.tree = tree;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addTypeItem) {
			if (dialog == null || !dialog.isVisible()) {
				dialog = new CreateParameterTypeDialog(null, tree, true);
			}
		} else if (e.getSource() == deleteTypeItem) {
			int result = JOptionPane.showConfirmDialog(null, "是否删除？");
			if (result == JOptionPane.YES_OPTION) {
				XWTreeNode treeNode = (XWTreeNode) this.tree.getLastSelectedPathComponent();
				//从服务器删除数据
				CmParameterType parameterType = (CmParameterType) treeNode.getTreeObject().getTreeNode();
				MPMParameterProcessor.deleteParameterType(parameterType);
				//删除树节点
				treeNode.getParentNode().remove(treeNode);

				tree.updateUI();
			}
		}
	}

	@Override
	protected void initComponent() {
		setDefaultLightWeightPopupEnabled(false);
		setLightWeightPopupEnabled(false);

		addTypeItem = new JMenuItem("添加检验特性");
		addTypeItem.addActionListener(this);
		add(addTypeItem);

		deleteTypeItem = new JMenuItem("删除");
		deleteTypeItem.addActionListener(this);
		add(deleteTypeItem);
	}

	@Override
	protected void initMenuItemIcon() {
//		addTypeItem.setIcon(IconUtil.getImageIcon(IconUtil.ADD));
	}

	@Override
	protected void registerComponentAuthority() {

	}

	public void setStatus() {
		XWTreeNode selTreeNode = (XWTreeNode)tree.getSelectionPath().getLastPathComponent();
		if ("检验特性管理".equals(selTreeNode.toString())
				|| "检验记录表管理".equals(selTreeNode.toString())) {
			setUIEnabled(false);
		} else {
			addTypeItem.setEnabled(true);
			XWTreeNode parentNode = selTreeNode.getParentNode();
			if ("检验特性管理".equals(parentNode.toString())) {
				deleteTypeItem.setEnabled(false);
			} else {
				deleteTypeItem.setEnabled(true);
			}
		}
	}

	public void setUIEnabled(boolean b) {
		addTypeItem.setEnabled(b);
		deleteTypeItem.setEnabled(b);
	}
}
