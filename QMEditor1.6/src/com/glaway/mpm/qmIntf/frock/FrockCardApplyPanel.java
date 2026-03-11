/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glaway.mpm.qmIntf.frock;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.glaway.mpm.util.ImageFileChooser;
import com.glaway.mpm.util.InputLimited;
import com.glaway.mpm.util.JavaUtil;
import com.glaway.mpm.util.MutableFilter;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.util.Translatrix;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ResourceIntf;

/**
 *
 * @author ylshao
 */
public class FrockCardApplyPanel extends javax.swing.JPanel {

	private VaLogger logger = VaLogger.getLogger(FrockCardApplyPanel.class);

	private String number1;
	private String number2;
	private String productNumberValue;
	private FrockCardApplyDialog dialog;
	private JDialog jDialog;
	// 工装编号及名称的集合
	private Map frockNumberName;
	private Map<String, String> inputMap;

	/**
	 * Creates new form NewFrockApplyPanel
	 */
	public FrockCardApplyPanel(Map<String, String> map,
			FrockCardApplyDialog dialog, JDialog jDialog) {
		this.inputMap = map;
		this.dialog = dialog;
		this.jDialog = jDialog;
		this.number1 = map.get("partNumber");
		this.number2 = map.get("parentPartNumber");
		this.productNumberValue = map.get("productNumberValue");
		initComponents();
		initAction();
		setModel();
	}

	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jPanel2 = new javax.swing.JPanel();
		productionNumberLabel = new javax.swing.JLabel();
		isCommonTools = new javax.swing.JComboBox();
		isRegularlyToolsLabel = new javax.swing.JLabel();
		insertPart = new javax.swing.JComboBox();
		isCommonToolsLabel = new javax.swing.JLabel();
		isReview = new javax.swing.JComboBox();
		productNumber = new javax.swing.JTextField();
		isTestPart = new javax.swing.JComboBox();
		productionNumber = new javax.swing.JTextField();
		isReviewLabel = new javax.swing.JLabel();
		insertPartLabel = new javax.swing.JLabel();
		wholePartNumberLabel = new javax.swing.JLabel();
		isTestPartLabel = new javax.swing.JLabel();
		workShop = new javax.swing.JTextField();
		number = new javax.swing.JComboBox();
		productNumberLabel = new javax.swing.JLabel();
		numberLabel = new javax.swing.JLabel();
		workShopLabel = new javax.swing.JLabel();
		nameLabel = new javax.swing.JLabel();
		name = new javax.swing.JTextField();
		jPanel1 = new javax.swing.JPanel();
		jLabel12 = new javax.swing.JLabel();
		toolingRequirementsLabel = new javax.swing.JLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		toolingRequirements = new javax.swing.JTextArea();
		imageField = new javax.swing.JTextField();
		viewButton = new javax.swing.JButton();
		imageLabel = new javax.swing.JLabel();
		sureButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		partNumberLabel = new javax.swing.JLabel();
		wholePartNumber = new javax.swing.JTextField();
		partNumber = new javax.swing.JTextField();
		isRegularlyTools = new javax.swing.JComboBox();

		productionNumberLabel.setText("*投产付数：");

		isRegularlyToolsLabel.setText("*常用工装：");

		isCommonToolsLabel.setText("*共用工装：");

		isReviewLabel.setText("*评审：");

		insertPartLabel.setText("*镶件：");

		wholePartNumberLabel.setText("*整件图号：");

		isTestPartLabel.setText("*试模件：");

		productNumberLabel.setText("*产品代号：");

		numberLabel.setText("*工装编号：");

		workShopLabel.setText("*使用单位：");

		nameLabel.setText("*工装名称：");

		jLabel12.setText("选择图片：");

		toolingRequirementsLabel.setText("*要求说明：");

		toolingRequirements.setColumns(20);
		toolingRequirements.setRows(5);
		jScrollPane2.setViewportView(toolingRequirements);

		imageField.setBackground(javax.swing.UIManager.getDefaults().getColor(
				"CheckBox.light"));

		viewButton.setText("浏览");
		viewButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				viewButtonActionPerformed(evt);
			}
		});

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
										.addContainerGap(18, Short.MAX_VALUE)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(
																				toolingRequirementsLabel)
																		.addGap(18,
																				18,
																				18)
																		.addComponent(
																				jScrollPane2,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				253,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel12)
																		.addGap(18,
																				18,
																				18)
																		.addComponent(
																				imageField,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				155,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(
																				viewButton,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				85,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addGap(34, 34, 34)
										.addComponent(
												imageLabel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												154,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(16, 16, 16)));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addGap(22, 22, 22)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel12)
														.addComponent(
																imageField,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																viewButton))
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addGap(67,
																				67,
																				67)
																		.addComponent(
																				toolingRequirementsLabel))
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addGap(27,
																				27,
																				27)
																		.addComponent(
																				jScrollPane2,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				116,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE))
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addGap(26, 26, 26)
										.addComponent(
												imageLabel,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addContainerGap()));

		sureButton.setText("确定");
		sureButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				sureButtonActionPerformed(evt);
			}
		});

		cancelButton.setText("取消");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		partNumberLabel.setText("零件图号：");

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout
				.setHorizontalGroup(jPanel2Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel2Layout
										.createSequentialGroup()
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addGroup(
																jPanel2Layout
																		.createSequentialGroup()
																		.addContainerGap(
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addComponent(
																				jPanel1,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(
																jPanel2Layout
																		.createSequentialGroup()
																		.addGap(150,
																				150,
																				150)
																		.addComponent(
																				sureButton)
																		.addGap(79,
																				79,
																				79)
																		.addComponent(
																				cancelButton)
																		.addGap(0,
																				0,
																				Short.MAX_VALUE)))
										.addGap(320, 320, 320))
						.addGroup(
								jPanel2Layout
										.createSequentialGroup()
										.addGap(22, 22, 22)
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
																isRegularlyToolsLabel)
														.addComponent(
																partNumberLabel))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel2Layout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING,
																				false)
																		.addComponent(
																				productionNumber)
																		.addComponent(
																				number,
																				0,
																				150,
																				Short.MAX_VALUE)
																		.addComponent(
																				isCommonTools,
																				0,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addComponent(
																				productNumber)
																		.addComponent(
																				isRegularlyTools,
																				0,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE))
														.addComponent(
																partNumber,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																150,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(76, 76, 76)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																insertPartLabel,
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																nameLabel,
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																isTestPartLabel,
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																isReviewLabel,
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																wholePartNumberLabel,
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																workShopLabel,
																javax.swing.GroupLayout.Alignment.TRAILING))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel2Layout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.TRAILING,
																				false)
																		.addComponent(
																				workShop,
																				0,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addComponent(
																				name,
																				javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(
																				isReview,
																				javax.swing.GroupLayout.Alignment.LEADING,
																				0,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addComponent(
																				isTestPart,
																				javax.swing.GroupLayout.Alignment.LEADING,
																				0,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addComponent(
																				insertPart,
																				javax.swing.GroupLayout.Alignment.LEADING,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				150,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addComponent(
																wholePartNumber,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																150,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
				new java.awt.Component[] { isCommonTools, number, partNumber,
						productNumber, productionNumber });

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
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
																								productNumber,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								productNumberLabel))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								productionNumber,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								productionNumberLabel))
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
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								isRegularlyToolsLabel)
																						.addComponent(
																								isRegularlyTools,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addGap(9,
																				9,
																				9)
																		.addGroup(
																				jPanel2Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								partNumberLabel)
																						.addComponent(
																								partNumber,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE))))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												39, Short.MAX_VALUE)
										.addComponent(
												jPanel1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																sureButton)
														.addComponent(
																cancelButton))
										.addContainerGap()));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup()
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addComponent(jPanel2,
								javax.swing.GroupLayout.PREFERRED_SIZE, 644,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.PREFERRED_SIZE));
	}// </editor-fold>

	private void sureButtonActionPerformed(java.awt.event.ActionEvent evt) {
		Object selectedNumber = number.getSelectedItem();
		if (selectedNumber == null) {
			JOptionPane.showMessageDialog(this, "请申请工装号！", "提示", 1);
			return;
		}
		String nameValue = name.getText();
		if (nameValue == null || nameValue.trim().equals("")) {
			JOptionPane.showMessageDialog(this, "请重新选择工装号带出名称！", "提示", 1);
			return;
		}
		String productionNumberValue = productionNumber.getText();
		if (productionNumberValue == null
				|| productionNumberValue.trim().equals("")) {
			JOptionPane.showMessageDialog(this, "请填写投产付数！", "提示", 1);
			return;
		}

		String toolingRequirementsValue = toolingRequirements.getText();
		if (toolingRequirementsValue == null
				|| toolingRequirementsValue.trim().equals("")) {
			JOptionPane.showMessageDialog(this, "请填写工装设计要求说明！", "提示", 1);
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("partOid", inputMap.get("partOid"));
		map.put("number", selectedNumber.toString());
		map.put("name", nameValue);
		map.put("productNumber", productNumberValue);
		map.put("insertPart", insertPart.getSelectedIndex() == 0 ? "true"
				: "false");
		map.put("productionNumber", productionNumberValue);

		map.put("isReview", isReview.getSelectedIndex() == 0 ? "true" : "false");
		map.put("isCommonTools", isCommonTools.getSelectedItem());
		map.put("isRegularlyTools",
				isRegularlyTools.getSelectedIndex() == 0 ? "true" : "false");
		map.put("isTestPart", isTestPart.getSelectedIndex() == 0 ? "true"
				: "false");

		map.put("partNumber", partNumber.getText());
		map.put("wholePartNumber", wholePartNumber.getText());
		map.put("workShop", workShop.getText().toString());

		map.put("fileBytes", fileBytes);
		map.put("fileName", fileName);

		map.put("toolingRequirements", toolingRequirements.getText());
		dialog.frock = ResourceIntf.createFrockCard(map);
		jDialog.dispose();
	}

	private void initAction() {
		number.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				numberChanged();
			}
		});
	}

	private void numberChanged() {
		name.setText(frockNumberName.get(number.getSelectedItem()).toString());
	}

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		dialog.frock = null;
		jDialog.dispose();
	}

	private String imageChooserStartDir = System.getProperty("user.dir");
	private final String[] extsIMG = { "gif", "jpg", "jpeg", "png" };
	private byte[] fileBytes = null;
	private String fileName = null;

	private void viewButtonActionPerformed(java.awt.event.ActionEvent evt) {
		ImageFileChooser jImageDialog = new ImageFileChooser(
				imageChooserStartDir);
		jImageDialog.setDialogType(JFileChooser.CUSTOM_DIALOG);
		jImageDialog.setFileFilter(new MutableFilter(extsIMG, "选择图片"));
		jImageDialog.setDialogTitle(Translatrix.getTranslationString("请选择图片"));
		int optionSelected = JFileChooser.CANCEL_OPTION;
		optionSelected = jImageDialog.showDialog(this,
				Translatrix.getTranslationString("确定"));
		System.out.println(optionSelected);
		if (optionSelected == JFileChooser.APPROVE_OPTION) {
			File file = jImageDialog.getSelectedFile();
			if (file != null) {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
					byte[] fileBytes = new byte[fis.available()];
					fis.read(fileBytes);
					this.fileBytes = fileBytes;
					this.fileName = file.getName();
					JavaUtil.closeStream(fis);
					imageLabel.setIcon(SwingUtil.scaleImage(new ImageIcon(
							fileBytes), 150, 150));
					imageField.setText(file.getAbsolutePath());
					imageChooserStartDir = file.getAbsolutePath();
					jImageDialog.setVisible(false);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					JavaUtil.closeStream(fis);
				}
			}
		} else {
			jImageDialog.setVisible(false);
		}
		jImageDialog = null;
	}

	private javax.swing.JButton cancelButton;
	private javax.swing.JTextField imageField;
	private javax.swing.JLabel imageLabel;
	private javax.swing.JComboBox insertPart;
	private javax.swing.JLabel insertPartLabel;
	private javax.swing.JComboBox isCommonTools;
	private javax.swing.JLabel isCommonToolsLabel;
	private javax.swing.JComboBox isRegularlyTools;
	private javax.swing.JLabel isRegularlyToolsLabel;
	private javax.swing.JComboBox isReview;
	private javax.swing.JLabel isReviewLabel;
	private javax.swing.JComboBox isTestPart;
	private javax.swing.JLabel isTestPartLabel;
	private javax.swing.JLabel jLabel12;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JTextField name;
	private javax.swing.JLabel nameLabel;
	private javax.swing.JComboBox number;
	private javax.swing.JLabel numberLabel;
	private javax.swing.JTextField partNumber;
	private javax.swing.JLabel partNumberLabel;
	private javax.swing.JTextField productNumber;
	private javax.swing.JLabel productNumberLabel;
	private javax.swing.JTextField productionNumber;
	private javax.swing.JLabel productionNumberLabel;
	private javax.swing.JButton sureButton;
	private javax.swing.JTextArea toolingRequirements;
	private javax.swing.JLabel toolingRequirementsLabel;
	private javax.swing.JButton viewButton;
	private javax.swing.JTextField wholePartNumber;
	private javax.swing.JLabel wholePartNumberLabel;
	private javax.swing.JTextField workShop;
	private javax.swing.JLabel workShopLabel;

	// End of variables declaration
	private void setModel() {
		isCommonTools.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "N", "A", "B", "C" }));
		insertPart.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
				"是", "否" }));

		insertPart.setSelectedIndex(1);
		isReview.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
				"是", "否" }));
		isReview.setSelectedIndex(1);

		isTestPart.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
				"是", "否" }));
		isTestPart.setSelectedIndex(0);

		isRegularlyTools.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "是", "否" }));
		isRegularlyTools.setSelectedIndex(1);

		frockNumberName = ResourceIntf.getNewFrockInfo();
		if (frockNumberName != null) {
			Set set = frockNumberName.keySet();
			number.setModel(new javax.swing.DefaultComboBoxModel(set.toArray()));
		}
		if (number.getSelectedItem() != null) {
			numberChanged();
		}
		// Vector<String> workShopName = new Vector<String>();
		// if (ResourceCache.workShops != null) {
		// for (WorkShop workshop : ResourceCache.workShops) {
		// workShopName.add(workshop.getName());
		// }
		// }
		String workShopValue = inputMap.get("workshop");
		if (workShopValue != null) {
			workShop.setText(workShopValue);
		}
		productionNumber.setDocument(new InputLimited(5, true));
		productionNumber.setText("1");
		partNumber.setText(number1);
		wholePartNumber.setText(number2);
		productNumber.setText(productNumberValue);

		workShop.setEditable(false);
		productNumber.setEditable(false);
		partNumber.setEditable(false);
		wholePartNumber.setEditable(false);
		imageField.setEditable(false);
		name.setEditable(false);

		imageLabel.setMaximumSize(new Dimension(100, 100));
		imageLabel.setMinimumSize(new Dimension(100, 100));
		imageLabel.setPreferredSize(new Dimension(100, 100));
		imageLabel.setSize(new Dimension(100, 100));

	}
}
