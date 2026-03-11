package com.glaway.mpm.parameter.ui;

import com.glaway.mpm.parameter.model.tree.XWTreeNode;

import javax.swing.*;

/**
 * 白羽表单模板管理主面板
 * @author cjh
 */
public class BaiyuParamTablePanel extends JPanel {

	private static final long serialVersionUID = 2950431904101658186L;
	private BaiyuTableBasicInfoPanel basicInfoPanel;
	private BaiyuTableDetailInfoPanel detailInfoPanel;
	private MPMParameterMainFrame frame;

	public BaiyuParamTablePanel(MPMParameterMainFrame frame) {
		this.frame = frame;
		initComponents();
		initLayout();
	}

	private void initComponents() {
		basicInfoPanel = new BaiyuTableBasicInfoPanel(frame);
		detailInfoPanel = new BaiyuTableDetailInfoPanel();

	}

	private void initLayout() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		layout.putConstraint(SpringLayout.NORTH, basicInfoPanel, 0, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, basicInfoPanel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, basicInfoPanel, 0, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.WEST, detailInfoPanel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, detailInfoPanel, 0, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.SOUTH, detailInfoPanel, 0, SpringLayout.SOUTH, this);

		layout.putConstraint(SpringLayout.SOUTH, basicInfoPanel, 0, SpringLayout.NORTH, detailInfoPanel);

		add(basicInfoPanel);
		add(detailInfoPanel);
	}

	public BaiyuTableBasicInfoPanel getBasicInfoPanel() {
		return basicInfoPanel;
	}

	public BaiyuTableDetailInfoPanel getDetailInfoPanel() {
		return detailInfoPanel;
	}

	public void setUIValues(XWTreeNode treeNode) {
		basicInfoPanel.setUIValues(treeNode);
		detailInfoPanel.setUIValues(treeNode);
	}
}
