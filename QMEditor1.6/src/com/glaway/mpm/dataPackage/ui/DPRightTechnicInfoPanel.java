package com.glaway.mpm.dataPackage.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.glaway.mpm.parameter.designui.NewCommonParamTablePanel;
import com.glaway.mpm.parameter.designui.NewSpecialParamTabbedPanel;

public class DPRightTechnicInfoPanel extends JPanel{

	/**
	 * 右边工艺信息页面
	 */
	private static final long serialVersionUID = 1L;
	private JTabbedPane tabbedPane;
	private DPLeftTechnicTreePanel leftTechnicTreePanel;
	private NewCommonParamTablePanel commonParamTablePanel;
	private NewSpecialParamTabbedPanel specialParamTabbedPanel;

	public DPRightTechnicInfoPanel(DPLeftTechnicTreePanel leftTechnicTreePanel){
		this.leftTechnicTreePanel = leftTechnicTreePanel;
		initCompont();

	}

	private void initCompont(){
		tabbedPane = new JTabbedPane();
		commonParamTablePanel = new NewCommonParamTablePanel(this);
		specialParamTabbedPanel = new NewSpecialParamTabbedPanel(this);
		tabbedPane.addTab("质量记录表", commonParamTablePanel);
		tabbedPane.addTab("特殊记录表", specialParamTabbedPanel);
		tabbedPane.setForegroundAt(0, Color.RED);
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
		this.tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int index = tabbedPane.getSelectedIndex();
				for (int i = 0; i < 2; i++) {
					if (index != i) {
						tabbedPane.setForegroundAt(i, Color.BLACK);
					}
				}
				tabbedPane.setForegroundAt(index, Color.RED);
			}
		});
	}
	public void initUIValues(){
		commonParamTablePanel.setDataPackageUIValues();
		specialParamTabbedPanel.setDataPackageUIValues();
	}



	public DPLeftTechnicTreePanel getLeftTechnicTreePanel() {
		return leftTechnicTreePanel;
	}

	public NewCommonParamTablePanel getCommonParamTablePanel() {
		return commonParamTablePanel;
	}

	public NewSpecialParamTabbedPanel getSpecialParamTabbedPanel() {
		return specialParamTabbedPanel;
	}





}
