package com.glaway.mpm.parameter.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenuItem;


import com.glaway.mpm.parameter.commonui.CommonJPopupMenu;
import com.glaway.mpm.parameter.designui.CreateCheckParamTableTypeDialog;
import com.glaway.mpm.parameter.designui.NewCheckParamTabbedPanel;
import com.glaway.mpm.parameter.designui.NewCheckParamTablePanel;

public class CheckParamTabbedPopupMenu extends CommonJPopupMenu implements ActionListener {

	private static final long serialVersionUID = 1L;

	/** 添加检验记录表 */
    private JMenuItem createTableItem;
    /** 移除检验记录表 */
    private JMenuItem removeTableItem;
    /** 检验记录表前移 */
//    private JMenuItem moveForward;
    /** 检验记录表后移 */
//    private JMenuItem moveBackward;
    /** 修改检验记录表 */
    private JMenuItem changeTableItem;

    private NewCheckParamTabbedPanel panel;

    public CheckParamTabbedPopupMenu(NewCheckParamTabbedPanel panel){
    	this.panel = panel;
    }


	@Override
	public void actionPerformed(ActionEvent e) {
		/**
		 * false = 新建  , true = 修改
		 */
		if (e.getSource() == createTableItem) {
			new CreateCheckParamTableTypeDialog(panel, false);
		} else if (e.getSource() == removeTableItem) {
			panel.removeCheckParamTableType(null);
		} else if (e.getSource() == changeTableItem) {
			List<NewCheckParamTablePanel> panelList= panel.getCheckParamTablePanelList();
			if(panelList != null && !panelList.isEmpty()){
				new CreateCheckParamTableTypeDialog(panel, true);
			}
		}
	}

	@Override
	protected void initComponent() {
		setDefaultLightWeightPopupEnabled(false);
		setLightWeightPopupEnabled(false);

		createTableItem = new JMenuItem("新建检验记录表");
		createTableItem.addActionListener(this);
		add(createTableItem);

		removeTableItem = new JMenuItem("移除检验记录表");
		removeTableItem.addActionListener(this);
		add(removeTableItem);

		changeTableItem = new JMenuItem("更改属性");
		changeTableItem.addActionListener(this);
		add(changeTableItem);

	}

	@Override
	protected void initMenuItemIcon() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void registerComponentAuthority() {
		// TODO Auto-generated method stub

	}


	public void setUIEnabled(boolean b) {
		createTableItem.setEnabled(b);
		removeTableItem.setEnabled(b);
		changeTableItem.setEnabled(b);
//		moveForward.setEnabled(b);
//		moveBackward.setEnabled(b);
	}

}
