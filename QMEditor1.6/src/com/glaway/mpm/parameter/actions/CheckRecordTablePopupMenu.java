package com.glaway.mpm.parameter.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import com.glaway.mpm.parameter.commonui.CommonJPopupMenu;
import com.glaway.mpm.parameter.ui.CreateParameterTableDialog;

/**
 * 检验记录表管理右键菜单。
 *
 */
public class CheckRecordTablePopupMenu extends CommonJPopupMenu implements ActionListener {

	private static final long serialVersionUID = 1L;

	/** 添加检验记录表 */
    private JMenuItem addTypeItem;
    private CreateParameterTableDialog dialog;

	public CheckRecordTablePopupMenu() {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addTypeItem) {
			if (dialog == null || !dialog.isVisible()) {
				dialog = new CreateParameterTableDialog(null, true);
			}

		}
	}

	@Override
	protected void initComponent() {
		setDefaultLightWeightPopupEnabled(false);
		setLightWeightPopupEnabled(false);

		addTypeItem = new JMenuItem("添加检验记录表");
		addTypeItem.addActionListener(this);
		add(addTypeItem);
	}

	@Override
	protected void initMenuItemIcon() {
//		addTypeItem.setIcon(IconUtil.getImageIcon(IconUtil.ADD));
	}

	@Override
	protected void registerComponentAuthority() {

	}

	public void setStatus() {
//		XWTreeNode selTreeNode = (XWTreeNode)tree.getSelectionPath().getLastPathComponent();
//		if ("检验记录表管理".equals(selTreeNode.toString())
//				|| "通用检查项定义".equals(selTreeNode.toString())) {
//			setUIEnabled(false);
//		} else {
			setUIEnabled(true);
//		}
	}

	public void setUIEnabled(boolean b) {
		addTypeItem.setEnabled(b);
	}
}
