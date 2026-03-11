package com.glaway.mpm.qmIntf.viewPanel;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * show details information of the image
 */
public class CreoImgInfoPanel extends JPanel {
	
	JLabel lblFileNameValue;
	
	JLabel lblFileNoValue;
	
	JLabel lblNameValue;
	
	public JLabel getLblFileNameValue() {
		return lblFileNameValue;
	}

	public void setLblFileNameValue(JLabel lblFileNameValue) {
		this.lblFileNameValue = lblFileNameValue;
	}

	public JLabel getLblFileNoValue() {
		return lblFileNoValue;
	}

	public void setLblFileNoValue(JLabel lblFileNoValue) {
		this.lblFileNoValue = lblFileNoValue;
	}

	public JLabel getLblNameValue() {
		return lblNameValue;
	}

	public void setLblNameValue(JLabel lblNameValue) {
		this.lblNameValue = lblNameValue;
	}

	public JLabel getLblFileStatusValue() {
		return lblFileStatusValue;
	}

	public void setLblFileStatusValue(JLabel lblFileStatusValue) {
		this.lblFileStatusValue = lblFileStatusValue;
	}

	public JLabel getLblUpdatedByValue() {
		return lblUpdatedByValue;
	}

	public void setLblUpdatedByValue(JLabel lblUpdatedByValue) {
		this.lblUpdatedByValue = lblUpdatedByValue;
	}

	public JLabel getLblUpdatedTimeValue() {
		return lblUpdatedTimeValue;
	}

	public void setLblUpdatedTimeValue(JLabel lblUpdatedTimeValue) {
		this.lblUpdatedTimeValue = lblUpdatedTimeValue;
	}

	JLabel lblFileStatusValue;
	
	JLabel lblUpdatedByValue;
	
	JLabel lblUpdatedTimeValue;

	/**
	 * Create the panel.
	 */
	public CreoImgInfoPanel() {
		
		JLabel lblFileName = new JLabel("文件名：");
		
		JLabel lblFileNo = new JLabel("编号：");
		
		JLabel lblName = new JLabel("名称：");
		
		JLabel lblFileStatus = new JLabel("状况：");
		
		JLabel lblUpdatedBy = new JLabel("修改者：");
		
		JLabel lblUpdatedTime = new JLabel("上次修改时间：");
		
		lblFileNameValue = new JLabel("1");
		
		lblFileNoValue = new JLabel("2");
		
		lblNameValue = new JLabel("3");
		
		lblFileStatusValue = new JLabel("4");
		
		lblUpdatedByValue = new JLabel("5");
		
		lblUpdatedTimeValue = new JLabel("6");
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblFileName)
						.addComponent(lblFileNo)
						.addComponent(lblName)
						.addComponent(lblFileStatus)
						.addComponent(lblUpdatedBy)
						.addComponent(lblUpdatedTime))
					.addGap(27)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblUpdatedTimeValue)
						.addComponent(lblUpdatedByValue)
						.addComponent(lblFileStatusValue)
						.addComponent(lblNameValue)
						.addComponent(lblFileNoValue)
						.addComponent(lblFileNameValue))
					.addContainerGap(275, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(28)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblFileName)
						.addComponent(lblFileNameValue))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblFileNo)
						.addComponent(lblFileNoValue))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblName)
						.addComponent(lblNameValue))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblFileStatus)
						.addComponent(lblFileStatusValue))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUpdatedBy)
						.addComponent(lblUpdatedByValue))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUpdatedTime)
						.addComponent(lblUpdatedTimeValue))
					.addContainerGap(132, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
		 //initComponents();
	}
}
