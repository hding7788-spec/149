package com.glaway.mpm.parameter.ui;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

import com.glaway.mpm.parameter.model.tree.XWTreeNode;

/**
 * 检验记录表主面板
 * @author wxl
 */
public class ParamTableTypePanel extends JPanel {

	private static final long serialVersionUID = 2950431904101658186L;
	private ParamTableBasicInfoPanel basicInfoPanel;
	private ParamTableDetailInfoPanel detailInfoPanel;

	public ParamTableTypePanel() {
		 initComponents();
		 initLayout();
	}

	private void initComponents() {
		basicInfoPanel = new ParamTableBasicInfoPanel();
		detailInfoPanel = new ParamTableDetailInfoPanel();

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

	public ParamTableBasicInfoPanel getBasicInfoPanel() {
		return basicInfoPanel;
	}

	public ParamTableDetailInfoPanel getDetailInfoPanel() {
		return detailInfoPanel;
	}

	public void setUIValues(XWTreeNode treeNode) {
		basicInfoPanel.setUIValues(treeNode);
		detailInfoPanel.setUIValues(treeNode);
	}
}
