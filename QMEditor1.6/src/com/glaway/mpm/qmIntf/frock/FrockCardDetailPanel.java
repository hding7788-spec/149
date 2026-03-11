/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glaway.mpm.qmIntf.frock;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.util.JavaUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.wcIntf.ResourceIntf;

/**
 * 
 * @author ylshao
 */
public class FrockCardDetailPanel extends javax.swing.JPanel {

	/**
	 * Creates new form NewFrockApplyPanel
	 */
	public FrockCardDetailPanel(Map map, boolean flag) {
		this.flag = flag;
		initComponents();
		setModel();
		initAction();
		setData(map);
	}

	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jPanel2 = new javax.swing.JPanel();
		productionNumberLabel = new javax.swing.JLabel();
		isRegularlyToolsLabel = new javax.swing.JLabel();
		isCommonToolsLabel = new javax.swing.JLabel();
		insertPart = new javax.swing.JTextField();
		isReview = new javax.swing.JTextField();
		isReviewLabel = new javax.swing.JLabel();
		insertPartLabel = new javax.swing.JLabel();
		wholePartNumberLabel = new javax.swing.JLabel();
		isTestPartLabel = new javax.swing.JLabel();
		wholePartNumber = new javax.swing.JTextField();
		productNumberLabel = new javax.swing.JLabel();
		numberLabel = new javax.swing.JLabel();
		workShopLabel = new javax.swing.JLabel();
		nameLabel = new javax.swing.JLabel();
		name = new javax.swing.JTextField();
		jPanel1 = new javax.swing.JPanel();
		toolingRequirementsLabel = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		toolingRequirements = new javax.swing.JTextArea();
		imageLabel = new javax.swing.JLabel();
		image = new javax.swing.JLabel();
		isRegularlyTools = new javax.swing.JTextField();
		partNumberLabel = new javax.swing.JLabel();
		partNumber = new javax.swing.JTextField();
		number = new javax.swing.JTextField();
		productionNumber = new javax.swing.JTextField();
		isTestPart = new javax.swing.JTextField();
		isCommonTools = new javax.swing.JTextField();
		workShop = new javax.swing.JTextField();
		productNumber = new javax.swing.JTextField();
		jPanel3 = new javax.swing.JPanel();
		searchButton = new javax.swing.JButton();
		searchNumber = new javax.swing.JTextField();
		searchNumberLabel = new javax.swing.JLabel();
		jScrollPane4 = new javax.swing.JScrollPane();
		jTable2 = new javax.swing.JTable();
		jPanel4 = new javax.swing.JPanel();
		jScrollPane = new javax.swing.JScrollPane();
		jTable = new javax.swing.JTable();
		jPanel5 = new javax.swing.JPanel();
		backLabel = new javax.swing.JLabel();
		forwardLabel = new javax.swing.JLabel();

		number.setEditable(false);
		name.setEditable(false);
		productNumber.setEditable(false);
		insertPart.setEditable(false);
		productionNumber.setEditable(false);
		isReview.setEditable(false);
		isCommonTools.setEditable(false);
		isRegularlyTools.setEditable(false);
		isTestPart.setEditable(false);
		partNumber.setEditable(false);
		wholePartNumber.setEditable(false);
		workShop.setEditable(false);
		toolingRequirements.setEditable(false);

		jPanel3.setBorder(new TitledBorder(null, "搜索工装申请卡",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		jPanel2.setBorder(new TitledBorder(null, "工装申请卡信息",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		jPanel4.setBorder(new TitledBorder(null, "签审信息",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));

		productionNumberLabel.setText("投产付数：");

		isRegularlyToolsLabel.setText("常用工装：");

		isCommonToolsLabel.setText("共用工装：");

		isReviewLabel.setText("评审：");

		insertPartLabel.setText("镶件：");

		wholePartNumberLabel.setText("整件图号：");

		isTestPartLabel.setText("试模件：");

		productNumberLabel.setText("产品代号：");

		numberLabel.setText("工装编号：");

		workShopLabel.setText("使用单位：");

		nameLabel.setText("工装名称：");

		toolingRequirementsLabel.setText("要求说明：");

		imageLabel.setText("图片：");

		toolingRequirements.setColumns(20);
		toolingRequirements.setLineWrap(true);
		toolingRequirements.setRows(5);
		jScrollPane1.setViewportView(toolingRequirements);

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(toolingRequirementsLabel)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jScrollPane1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												181,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(60, 60, 60)
										.addComponent(imageLabel)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												26, Short.MAX_VALUE)
										.addComponent(
												image,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												168,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap()));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								toolingRequirementsLabel)
																						.addComponent(
																								imageLabel))
																		.addGap(0,
																				129,
																				Short.MAX_VALUE))
														.addComponent(
																image,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																jScrollPane1))
										.addContainerGap()));

		partNumberLabel.setText("零件图号：");

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout
				.setHorizontalGroup(jPanel2Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel2Layout
										.createSequentialGroup()
										.addGap(22, 22, 22)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel2Layout
																		.createSequentialGroup()
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING)
																						.addComponent(
																								numberLabel)
																						.addComponent(
																								productNumberLabel)
																						.addComponent(
																								productionNumberLabel)
																						.addComponent(
																								isCommonToolsLabel)
																						.addComponent(
																								isRegularlyToolsLabel,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								isRegularlyTools)
																						.addComponent(
																								isCommonTools)
																						.addComponent(
																								productionNumber)
																						.addComponent(
																								number)
																						.addComponent(
																								productNumber)))
														.addGroup(
																jPanel2Layout
																		.createSequentialGroup()
																		.addComponent(
																				partNumberLabel)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(
																				partNumber)))
										.addGap(83, 83, 83)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																isReviewLabel)
														.addComponent(
																insertPartLabel)
														.addComponent(nameLabel)
														.addComponent(
																workShopLabel)
														.addComponent(
																wholePartNumberLabel,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																isTestPartLabel))
										.addGap(18, 18, 18)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																name,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																150,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																insertPart,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(isReview)
														.addComponent(
																isTestPart,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																150,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																wholePartNumber,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																150,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																workShop,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																150,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(1108, 1108, 1108))
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel2Layout
										.createSequentialGroup()
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(
												jPanel1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(1093, 1093, 1093)));

		jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
				new java.awt.Component[] { insertPart, isCommonTools, isReview,
						name, number, productionNumber });

		jPanel2Layout
				.setVerticalGroup(jPanel2Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel2Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel2Layout
																		.createSequentialGroup()
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								name,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								nameLabel))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								insertPartLabel)
																						.addComponent(
																								insertPart,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								isReviewLabel)
																						.addComponent(
																								isReview,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								isTestPartLabel)
																						.addComponent(
																								isTestPart,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								wholePartNumberLabel)
																						.addComponent(
																								wholePartNumber,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addGap(6,
																				6,
																				6)
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								workShopLabel)
																						.addComponent(
																								workShop,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE)))
														.addGroup(
																jPanel2Layout
																		.createSequentialGroup()
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								numberLabel)
																						.addComponent(
																								number,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								productNumberLabel)
																						.addComponent(
																								productNumber,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								productionNumberLabel)
																						.addComponent(
																								productionNumber,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								isCommonToolsLabel)
																						.addComponent(
																								isCommonTools,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addGap(11,
																				11,
																				11)
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								isRegularlyTools,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								isRegularlyToolsLabel))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								partNumberLabel)
																						.addComponent(
																								partNumber,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE))))
										.addGap(31, 31, 31)
										.addComponent(
												jPanel1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)));

		jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL,
				new java.awt.Component[] { name, nameLabel, numberLabel });

		jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL,
				new java.awt.Component[] { insertPart, insertPartLabel, number,
						productNumberLabel });

		jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL,
				new java.awt.Component[] { isReview, isReviewLabel,
						productionNumber, productionNumberLabel });

		jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL,
				new java.awt.Component[] { isCommonTools, isCommonToolsLabel,
						isTestPart, isTestPartLabel });

		searchButton.setText("搜索");

		searchNumberLabel.setText("工装编号或名称：");

		jTable2.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null, null, null },
						{ null, null, null, null }, { null, null, null, null },
						{ null, null, null, null } }, new String[] { "Title 1",
						"Title 2", "Title 3", "Title 4" }));
		jScrollPane4.setViewportView(jTable2);

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(
				jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout
				.setHorizontalGroup(jPanel3Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel3Layout
										.createSequentialGroup()
										.addGap(23, 23, 23)
										.addGroup(
												jPanel3Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jScrollPane4,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGroup(
																jPanel3Layout
																		.createSequentialGroup()
																		.addComponent(
																				searchNumberLabel)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				searchNumber,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				153,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addGap(18,
																				18,
																				18)
																		.addComponent(
																				searchButton)))
										.addContainerGap(114, Short.MAX_VALUE)));
		jPanel3Layout
				.setVerticalGroup(jPanel3Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel3Layout
										.createSequentialGroup()
										.addGroup(
												jPanel3Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																searchNumber,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																searchNumberLabel)
														.addComponent(
																searchButton))
										.addGap(18, 18, 18)
										.addComponent(
												jScrollPane4,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												89,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(0, 23, Short.MAX_VALUE)));

		jTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][] {
				{ null, null, null, null }, { null, null, null, null },
				{ null, null, null, null }, { null, null, null, null } },
				new String[] { "Title 1", "Title 2", "Title 3", "Title 4" }));
		jScrollPane.setViewportView(jTable);

		javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(
				jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel4Layout
						.createSequentialGroup()
						.addGap(30, 30, 30)
						.addComponent(jScrollPane,
								javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(107, Short.MAX_VALUE)));
		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel4Layout
						.createSequentialGroup()
						.addGap(29, 29, 29)
						.addComponent(jScrollPane,
								javax.swing.GroupLayout.PREFERRED_SIZE, 89,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(15, Short.MAX_VALUE)));

		javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(
				jPanel5);
		jPanel5.setLayout(jPanel5Layout);
		jPanel5Layout
				.setHorizontalGroup(jPanel5Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel5Layout
										.createSequentialGroup()
										.addContainerGap(558, Short.MAX_VALUE)
										.addComponent(backLabel)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(forwardLabel)
										.addGap(19, 19, 19)));
		jPanel5Layout
				.setVerticalGroup(jPanel5Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel5Layout
										.createSequentialGroup()
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addGroup(
												jPanel5Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(backLabel)
														.addComponent(
																forwardLabel))
										.addGap(37, 37, 37)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		jPanel2,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		589,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addContainerGap())
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addGap(0,
																		0,
																		Short.MAX_VALUE)
																.addComponent(
																		jPanel5,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(
														layout.createSequentialGroup()
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(
																						jPanel4,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addComponent(
																						jPanel3,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.PREFERRED_SIZE))
																.addGap(0,
																		0,
																		Short.MAX_VALUE)))));

		layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
				new java.awt.Component[] { jPanel2, jPanel3, jPanel4, jPanel5 });

		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jPanel3,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jPanel2,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(jPanel4,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(18, 18, 18)
								.addComponent(jPanel5,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(19, Short.MAX_VALUE)));
	}// </editor-fold>

	private javax.swing.JLabel backLabel;
	private javax.swing.JLabel forwardLabel;
	private javax.swing.JLabel image;
	private javax.swing.JLabel imageLabel;
	private javax.swing.JTextField insertPart;
	private javax.swing.JLabel insertPartLabel;
	private javax.swing.JTextField isCommonTools;
	private javax.swing.JLabel isCommonToolsLabel;
	private javax.swing.JTextField isRegularlyTools;
	private javax.swing.JLabel isRegularlyToolsLabel;
	private javax.swing.JTextField isReview;
	private javax.swing.JLabel isReviewLabel;
	private javax.swing.JTextField isTestPart;
	private javax.swing.JLabel isTestPartLabel;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane4;
	private javax.swing.JTextField name;
	private javax.swing.JLabel nameLabel;
	private javax.swing.JTextField number;
	private javax.swing.JLabel numberLabel;
	private javax.swing.JTextField partNumber;
	private javax.swing.JLabel partNumberLabel;
	private javax.swing.JTextField productNumber;
	private javax.swing.JLabel productNumberLabel;
	private javax.swing.JTextField productionNumber;
	private javax.swing.JLabel productionNumberLabel;
	private javax.swing.JButton searchButton;
	private javax.swing.JTextField searchNumber;
	private javax.swing.JLabel searchNumberLabel;
	private javax.swing.JTextArea toolingRequirements;
	private javax.swing.JLabel toolingRequirementsLabel;
	private javax.swing.JTextField wholePartNumber;
	private javax.swing.JLabel wholePartNumberLabel;
	private javax.swing.JTextField workShop;
	private javax.swing.JLabel workShopLabel;

	// End of variables declaration

	private void setModel() {
		jScrollPane.setViewportView(jTable);
		jScrollPane.setPreferredSize(new Dimension(590, 270));
		jTable.getTableHeader().setReorderingAllowed(false);
		jTable.getTableHeader().setResizingAllowed(false);

		jScrollPane4.setViewportView(jTable2);
		jScrollPane4.setPreferredSize(new Dimension(590, 270));
		jTable2.getTableHeader().setReorderingAllowed(false);
		jTable2.getTableHeader().setResizingAllowed(false);
		jTable2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jTable2.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						int row = jTable2.getSelectedRow();
						if (row != -1) {
							String number = jTable2.getValueAt(row, 0)
									.toString();
							Map map = new HashMap();
							map.put("number", number);
							setData(ResourceIntf.showFrockCard(map));
						}
					}
				});
		jTable2.setRowHeight(23);
		jTable.setRowHeight(23);
		jPanel5.setVisible(false);
		// backLabel.setIcon(new
		// ImageIcon(this.getClass().getResource("/images/public_back.gif")));
		// forwardLabel.setIcon(new
		// ImageIcon(this.getClass().getResource("/images/public_forward.gif")));
		searchNumber.addKeyListener(new KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent e) {
				if (10 == e.getKeyCode()) {
					FrockCardDetailPanel.this.searchFrockCard();
				}
			};
		});
	}

	private JScrollPane jScrollPane = new JScrollPane();
	private JTable jTable = new JTable(generatorModel(null));
	private JTable jTable2 = new JTable(generatorSearchTableModel(null));

	private void initAction() {
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				searchFrockCard();
			}
		});
	}

	private void searchFrockCard() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("number", searchNumber.getText().trim());
		jTable2.setModel(generatorSearchTableModel(ResourceIntf
				.showAllFrockCard(map)));
		jScrollPane4.setVisible(true);
	}

	private boolean flag;

	private void setData(Map map) {
		if (flag) {
			jPanel3.setVisible(false);
		}
		if (map == null) {
			jTable.setModel(generatorModel(null));
			jTable2.setModel(generatorSearchTableModel(null));
			return;
		}
		number.setText(JavaUtil.convertNull(map.get("number")));
		name.setText(JavaUtil.convertNull(map.get("name")));
		productNumber.setText(JavaUtil.convertNull(map.get("productNumber")));
		insertPart.setText("true".equalsIgnoreCase(JavaUtil.convertNull(map
				.get("insertPart"))) ? "是" : "否");
		productionNumber.setText(JavaUtil.convertNull(map
				.get("productionNumber")));
		isReview.setText("true".equalsIgnoreCase(JavaUtil.convertNull(map
				.get("isReview"))) ? "是" : "否");
		isCommonTools.setText(JavaUtil.convertNull(map.get("isCommonTools")));
		isTestPart.setText("true".equalsIgnoreCase(JavaUtil.convertNull(map
				.get("isTestPart"))) ? "是" : "否");
		isRegularlyTools.setText("true".equalsIgnoreCase(JavaUtil
				.convertNull(map.get("isRegularlyTools"))) ? "是" : "否");
		partNumber.setText(JavaUtil.convertNull(map.get("partNumber")));
		wholePartNumber
				.setText(JavaUtil.convertNull(map.get("wholePartNumber")));
		workShop.setText(JavaUtil.convertNull(map.get("workShop")));
		if (map.get("fileBytes") == null) {
			image.setIcon(new ImageIcon());
		} else {
			ImageIcon icon = new ImageIcon((byte[]) map.get("fileBytes"));
			image.setIcon(SwingUtil.scaleImage(icon, 150, 150));
		}
		toolingRequirements.setText(JavaUtil.convertNull(map
				.get("toolingRequirements")));

		jTable.setModel(generatorModel((List) map.get("audit")));
	}

	private DefaultTableModel generatorModel(List list) {
		Object[][] tableValue = null;
		if (list != null && list.size() != 0) {
			tableValue = new Object[list.size()][5];
			for (int i = 0; i < list.size(); i++) {
				List temp = (List) list.get(i);
				tableValue[i][0] = temp.get(0);
				tableValue[i][1] = temp.get(1);
				tableValue[i][2] = temp.get(2);
				tableValue[i][3] = temp.get(3);
				tableValue[i][4] = temp.get(4);
			}
		} else {
			tableValue = new Object[][] {};
		}
		return getModel(tableValue, new String[] { "签审", "签审者", "日期", "投票",
				"备注" });
	}

	private DefaultTableModel generatorSearchTableModel(List<Map> list) {
		Object[][] tableValue = null;
		if (list != null && list.size() != 0) {
			tableValue = new Object[list.size()][4];
			for (int i = 0; i < list.size(); i++) {
				Map temp = list.get(i);
				tableValue[i][0] = temp.get("number");
				tableValue[i][1] = temp.get("name");
				tableValue[i][2] = temp.get("productNumber");
				tableValue[i][3] = temp.get("applyTime");
			}
		} else {
			tableValue = new Object[][] {};
		}
		return getModel(tableValue, new String[] { "工装编号", "工装名称", "产品代号",
				"申请时间" });
	}

	private DefaultTableModel getModel(Object[][] tableValue, String[] header) {
		DefaultTableModel model = new DefaultTableModel(tableValue, header) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return model;
	}
}
