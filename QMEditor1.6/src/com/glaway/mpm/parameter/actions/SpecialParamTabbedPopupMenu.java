package com.glaway.mpm.parameter.actions;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import org.dom4j.Element;

import com.glaway.mpm.parameter.commonui.CommonJPopupMenu;
import com.glaway.mpm.parameter.designui.AddParamTableTypeDialog;
import com.glaway.mpm.parameter.designui.NewSpecialParamTabbedPanel;
import com.glaway.mpm.view.TechnicsPaceJDialog;
import com.glaway.mpm.view.TechnicsStepJPanel_XW;

/**
 * 检验记录表管理右键菜单。
 *
 */
public class SpecialParamTabbedPopupMenu extends CommonJPopupMenu implements ActionListener {

	private static final long serialVersionUID = 1L;

	/** 添加检验记录表 */
    private JMenuItem addTableItem;
    /** 移除检验记录表 */
    private JMenuItem removeTableItem;
    /** 检验记录表前移 */
    private JMenuItem moveForward;
    /** 检验记录表后移 */
    private JMenuItem moveBackward;
    private NewSpecialParamTabbedPanel panel;

	public SpecialParamTabbedPopupMenu(NewSpecialParamTabbedPanel panel) {
		this.panel = panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addTableItem) {
			new AddParamTableTypeDialog(panel);
		} else if (e.getSource() == removeTableItem) {
			panel.removeParamTableType();
		} else if (e.getSource() == moveForward) {
			panel.tabMoveForward();
		} else if (e.getSource() == moveBackward) {
			panel.tabMoveBackword();
		}
	}

	@Override
	protected void initComponent() {
		setDefaultLightWeightPopupEnabled(false);
		setLightWeightPopupEnabled(false);

		addTableItem = new JMenuItem("添加检验记录表");
		addTableItem.addActionListener(this);
		add(addTableItem);

		removeTableItem = new JMenuItem("移除检验记录表");
		removeTableItem.addActionListener(this);
		add(removeTableItem);

		moveForward = new JMenuItem("前移");
		moveForward.addActionListener(this);
		add(moveForward);

		moveBackward = new JMenuItem("后移");
		moveBackward.addActionListener(this);
		add(moveBackward);
	}

	@Override
	protected void initMenuItemIcon() {
//		addTypeItem.setIcon(IconUtil.getImageIcon(IconUtil.ADD));
	}

	@Override
	protected void registerComponentAuthority() {

	}

	public void setStatus(Container parentPanel) {
		String state = "";
		if (parentPanel instanceof TechnicsStepJPanel_XW) {
			TechnicsStepJPanel_XW technicsStepJPanel_XW = (TechnicsStepJPanel_XW) parentPanel;
			Element techElement = technicsStepJPanel_XW.getTechElement();
			state = techElement.attributeValue("lifecycle");
		} else if (parentPanel instanceof TechnicsPaceJDialog) {
			TechnicsPaceJDialog technicsPaceJDialog = (TechnicsPaceJDialog) parentPanel;
			Element techElement = technicsPaceJDialog.getStepPanel().getTechElement();
			state = techElement.attributeValue("lifecycle");
		}
		 boolean isApproved = (!"正在工作".equals(state) && !"修改中".equals(state)) ? true : false;
		 if (isApproved) {
			 setUIEnabled(false);
		 } else {
			 setUIEnabled(true);
		 }
	}

	public void setUIEnabled(boolean b) {
		addTableItem.setEnabled(b);
		removeTableItem.setEnabled(b);
		moveForward.setEnabled(b);
		moveBackward.setEnabled(b);
	}
}
