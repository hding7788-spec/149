package com.glaway.speciaword.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.glaway.speciaword.common.CommonHelper;
import com.glaway.speciaword.common.SWConstant;
import com.glaway.speciaword.common.SvgTranscoderToPng;
import com.glaway.speciaword.util.SPJavaUtil;

/***
 * 
 * @author mosesx
 * @date 2013-4-24
 * @version V1.0
 */
public class RoughSymbolDialog extends SpecDialog {

	private static final long serialVersionUID = 1L;

	public RoughSymbolDialog(String category, String imageFolder) {
		super(category,imageFolder);
		initComponents();
	}

	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jPanel2 = new javax.swing.JPanel();
		symbol1 = new javax.swing.JButton();
		symbol3 = new javax.swing.JButton();
		symbol2 = new javax.swing.JButton();
		jPanel3 = new javax.swing.JPanel();
		upComboBoxValue = new javax.swing.JComboBox();
		jPanel4 = new javax.swing.JPanel();
		upDiscription = new javax.swing.JTextField();
		jPanel5 = new javax.swing.JPanel();
		grainBut1 = new javax.swing.JButton();
		grainBut3 = new javax.swing.JButton();
		grainBut2 = new javax.swing.JButton();
		grainBut4 = new javax.swing.JButton();
		grainBut5 = new javax.swing.JButton();
		grainBut6 = new javax.swing.JButton();
		grainBut7 = new javax.swing.JButton();
		grainBut8 = new javax.swing.JButton();
		jPanel7 = new javax.swing.JPanel();
		downComboBoxValue = new javax.swing.JComboBox();
		jPanel8 = new javax.swing.JPanel();
		downDiscription = new javax.swing.JTextField();
		requestCheck = new javax.swing.JCheckBox();
		descLabel = new javax.swing.JPanel();
		okButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		jPanel9 = new javax.swing.JPanel();
		scanView = new javax.swing.JLabel();
		upComboBoxValue.setEditable(true);
		downComboBoxValue.setEditable(true);
		upComboBoxValue.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent evt) {
				changeScanView();
			}
		});
		downComboBoxValue.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent evt) {
				changeScanView();
			}
		});

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("表面粗糙度");
		setPreferredSize(new java.awt.Dimension(625, 470));
		setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screenSize.width - this.getPreferredSize().width) / 2,
				(screenSize.height - this.getPreferredSize().height) / 2);

		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("基本符号"));

		upDiscription.getDocument().addDocumentListener(eventDocumentListener);
		downDiscription.getDocument().addDocumentListener(eventDocumentListener);

		symbol1.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/symbol01.png")));
		symbol1.addActionListener(eventActionListener);
		symbol3.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/symbol03.png")));
		symbol3.addActionListener(eventActionListener);
		symbol2.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/symbol02.png")));
		symbol2.addActionListener(eventActionListener);

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel2Layout
								.createSequentialGroup()
								.addComponent(symbol1, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(14, 14, 14)
								.addComponent(symbol2, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(14, 14, 14)
								.addComponent(symbol3, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
										javax.swing.GroupLayout.PREFERRED_SIZE).addGap(0, 0, Short.MAX_VALUE)));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel2Layout
								.createSequentialGroup()
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(
										jPanel2Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(symbol1, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(symbol3, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(symbol2, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE))));

		jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("上限值"));
		jPanel3.setPreferredSize(new java.awt.Dimension(180, 30));

		upComboBoxValue.setModel(new javax.swing.DefaultComboBoxModel(SWConstant.ROUGHSYMBOL_COMBOBOX_VALUE));
		upComboBoxValue.addActionListener(eventActionListener);

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel3Layout.createSequentialGroup().addContainerGap()
								.addComponent(upComboBoxValue, 0, 150, Short.MAX_VALUE).addContainerGap()));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel3Layout
								.createSequentialGroup()
								.addContainerGap()
								.addComponent(upComboBoxValue, javax.swing.GroupLayout.PREFERRED_SIZE, 32,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("上说明"));
		jPanel4.setPreferredSize(new java.awt.Dimension(180, 30));

		javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel4Layout.createSequentialGroup().addContainerGap()
								.addComponent(upDiscription, 0, 150, Short.MAX_VALUE).addContainerGap()));
		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel4Layout
								.createSequentialGroup()
								.addContainerGap()
								.addComponent(upDiscription, javax.swing.GroupLayout.PREFERRED_SIZE, 32,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("纹理方向"));

		grainBut1.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/grain01.png")));
		grainBut3.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/grain03.png")));
		grainBut2.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/grain02.png")));
		grainBut4.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/grain04.png")));
		grainBut5.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/grain05.png")));
		grainBut6.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/grain06.png")));
		grainBut7.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/grain07.png")));
		grainBut8.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/grain08.png")));
		grainBut1.addActionListener(eventActionListener);
		grainBut3.addActionListener(eventActionListener);
		grainBut2.addActionListener(eventActionListener);
		grainBut4.addActionListener(eventActionListener);
		grainBut5.addActionListener(eventActionListener);
		grainBut6.addActionListener(eventActionListener);
		grainBut7.addActionListener(eventActionListener);
		grainBut8.addActionListener(eventActionListener);

		javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
		jPanel5.setLayout(jPanel5Layout);
		jPanel5Layout.setHorizontalGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel5Layout
								.createSequentialGroup()
								.addGroup(
										jPanel5Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
												.addGroup(
														jPanel5Layout
																.createSequentialGroup()
																.addComponent(grainBut5,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addGap(14, 14, 14)
																.addComponent(grainBut6,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(
														jPanel5Layout
																.createSequentialGroup()
																.addComponent(grainBut1,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addGap(14, 14, 14)
																.addComponent(grainBut2,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																		javax.swing.GroupLayout.PREFERRED_SIZE)))
								.addGap(14, 14, 14)
								.addGroup(
										jPanel5Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
												.addComponent(grainBut3, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(grainBut7, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(14, 14, 14)
								.addGroup(
										jPanel5Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(grainBut4, javax.swing.GroupLayout.Alignment.TRAILING,
														javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(grainBut8, javax.swing.GroupLayout.Alignment.TRAILING,
														javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE))));
		jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel5Layout
								.createSequentialGroup()
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(
										jPanel5Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(grainBut1, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(grainBut3, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(grainBut2, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(grainBut4, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(18, 18, 18)
								.addGroup(
										jPanel5Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														jPanel5Layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(grainBut5,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(grainBut6,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(
														jPanel5Layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(grainBut7,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(grainBut8,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																		javax.swing.GroupLayout.PREFERRED_SIZE)))));

		jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("下限值"));
		jPanel7.setPreferredSize(new java.awt.Dimension(180, 30));
		downComboBoxValue.setModel(new javax.swing.DefaultComboBoxModel(SWConstant.ROUGHSYMBOL_COMBOBOX_VALUE));
		downComboBoxValue.addActionListener(eventActionListener);

		javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
		jPanel7.setLayout(jPanel7Layout);
		jPanel7Layout.setHorizontalGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel7Layout.createSequentialGroup().addContainerGap()
								.addComponent(downComboBoxValue, 0, 150, Short.MAX_VALUE).addContainerGap()));
		jPanel7Layout.setVerticalGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel7Layout
								.createSequentialGroup()
								.addContainerGap()
								.addComponent(downComboBoxValue, javax.swing.GroupLayout.PREFERRED_SIZE, 32,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("下说明:"));
		jPanel8.setPreferredSize(new java.awt.Dimension(180, 30));

		javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
		jPanel8.setLayout(jPanel8Layout);
		jPanel8Layout.setHorizontalGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel8Layout.createSequentialGroup().addContainerGap()
								.addComponent(downDiscription, 0, 150, Short.MAX_VALUE).addContainerGap()));
		jPanel8Layout.setVerticalGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel8Layout
								.createSequentialGroup()
								.addContainerGap()
								.addComponent(downDiscription, javax.swing.GroupLayout.PREFERRED_SIZE, 32,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		requestCheck.setText("相同要求");
		requestCheck.addActionListener(eventActionListener);

		descLabel.setBackground(new java.awt.Color(204, 204, 204));
		JLabel desc = new JLabel();
		desc.setPreferredSize(new Dimension(110, 130));
		desc.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/rough_desc.png")));
		descLabel.add(desc, BorderLayout.CENTER);

		okButton.setText("确定(O)");
		okButton.setMnemonic(KeyEvent.VK_O);
		cancelButton.setText("取消(C)");
		cancelButton.setMnemonic(KeyEvent.VK_C);
		okButton.addActionListener(eventActionListener);
		cancelButton.addActionListener(eventActionListener);

		jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("预览:"));

		javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
		jPanel9.setLayout(jPanel9Layout);
		jPanel9Layout.setHorizontalGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel9Layout.createSequentialGroup().addContainerGap()
								.addComponent(scanView, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
								.addContainerGap()));
		jPanel9Layout.setVerticalGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel9Layout
								.createSequentialGroup()
								.addComponent(scanView, javax.swing.GroupLayout.PREFERRED_SIZE, 161,
										javax.swing.GroupLayout.PREFERRED_SIZE).addGap(0, 0, Short.MAX_VALUE)));

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING,
																								false)
																						.addComponent(
																								descLabel,
																								javax.swing.GroupLayout.Alignment.LEADING,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addComponent(
																								jPanel2,
																								javax.swing.GroupLayout.Alignment.LEADING,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addComponent(
																								jPanel5,
																								javax.swing.GroupLayout.Alignment.LEADING,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE)))
														.addGroup(
																jPanel1Layout.createSequentialGroup()
																		.addGap(12, 12, 12).addComponent(okButton)
																		.addGap(18, 18, 18).addComponent(cancelButton)))
										.addGap(5, 5, 5)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(jPanel3,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				190,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(jPanel4,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				190,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(jPanel7,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				190,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(jPanel8,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				190,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addComponent(requestCheck)
														.addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(10, 10, 10)));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING,
																								false)
																						.addComponent(
																								jPanel3,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								72, Short.MAX_VALUE)
																						.addComponent(
																								jPanel4,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								0, Short.MAX_VALUE))
																		.addGap(5, 5, 5)
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								jPanel7,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								87,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								jPanel8,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								87,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addComponent(requestCheck))
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(jPanel2,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addGap(5, 5, 5)
																		.addComponent(jPanel5,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(descLabel,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addGap(18, 18, 18)
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(okButton)
																						.addComponent(cancelButton)))
														.addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		pack();
		this.setModal(true);
		this.setVisible(true);
	}

	private class EventActionListener implements ActionListener {

		private final RoughSymbolDialog parent;

		public EventActionListener(RoughSymbolDialog dialog) {
			parent = dialog;
		}

		@SuppressWarnings({ "unchecked" })
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (symbol1.equals(source)) {
				mapParam.put("type", "ROUGH_SYMBOL2");
				changeScanView();
			} else if (symbol3.equals(source)) {
				mapParam.put("type", "ROUGH_SYMBOL3");
				changeScanView();
			} else if (symbol2.equals(source)) {
				mapParam.put("type", "ROUGH_SYMBOL1");
				changeScanView();
			} else if (grainBut1.equals(source)) {
				mapParam.put("GrainType", "=");
				changeScanView();
			} else if (grainBut3.equals(source)) {
				mapParam.put("GrainType", "Х");
				changeScanView();
			} else if (grainBut2.equals(source)) {
				mapParam.put("GrainType", "⊥");
				changeScanView();
			} else if (grainBut4.equals(source)) {
				mapParam.put("GrainType", "М");
				changeScanView();
			} else if (grainBut5.equals(source)) {
				mapParam.put("GrainType", "С");
				changeScanView();
			} else if (grainBut6.equals(source)) {
				mapParam.put("GrainType", "R");
				changeScanView();
			} else if (grainBut7.equals(source)) {
				mapParam.put("GrainType", "P");
				changeScanView();
			} else if (grainBut8.equals(source)) {
				mapParam.put("GrainType", "");
				changeScanView();
			} else if (downComboBoxValue.equals(source)) {
				changeScanView();
			} else if (upComboBoxValue.equals(source)) {
				changeScanView();
			} else if (requestCheck.equals(source)) {
				// 相同要求：选中则带圈
				changeScanView();
			} else if (cancelButton.equals(source)) {
				parent.setVisible(false);
			} else if (okButton.equals(source)) {
				if (isShow()) {
					String type = mapParam.get("type").toString();
					/*
					 * imageSrc = "SPW:" + type + ":"; for (Iterator<Entry> iter = mapParam.entrySet().iterator();
					 * iter.hasNext();) { Entry entry = iter.next(); imageSrc += entry.getKey() + "=" + entry.getValue()
					 * + ","; }
					 */
					changeScanView();

					// 图片本地化并替换src指向本地文件file://...
					imageSrc = CommonHelper.saveImageToLocal(
							transcoder.makeImage(getCategory(), type.toString(), mapParam),
							"" + System.currentTimeMillis(),getImageFolder());

					parent.setVisible(false);
				}
			}
		}
	}

	private class EventDocumentListener implements DocumentListener {

		@Override
		public void insertUpdate(DocumentEvent e) {
			changeScanView();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			changeScanView();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			changeScanView();
		}
	}

	/**
	 * 更新预浏览视图
	 */
	@SuppressWarnings("unchecked")
	private void changeScanView() {
		if (isShow()) {
			String upCV = /* upComboBoxValue.getSelectedItem().toString() */upComboBoxValue.getEditor().getItem()
					.toString();
			String downCV = /* downComboBoxValue.getSelectedItem().toString() */downComboBoxValue.getEditor().getItem()
					.toString();
			String upDesc = upDiscription.getText().trim();
			String downDesc = downDiscription.getText().trim();
			Dimension upSize = CommonHelper.calculateStringToImageSize(upDesc, 10);
			Dimension downSize = CommonHelper.calculateStringToImageSize(downDesc, 10);
			double maxStrWidth = Math.max(upSize.getWidth(), downSize.getWidth());
			if (requestCheck.isSelected()) {
				mapParam.put("requestCheck", "true");
			} else {
				mapParam.put("requestCheck", "false");
			}

			if ((upDesc != null && !upDesc.equals("")) || (downDesc != null && !downDesc.equals(""))) {
				mapParam.put("isLineShow", "true");
			} else {
				mapParam.put("isLineShow", "false");
			}

			if (mapParam.get("GrainType") == null) {
				mapParam.put("GrainType", "");
			}

			mapParam.put("upCV", upCV);
			mapParam.put("downCV", downCV);
			upDesc = SPJavaUtil.replaceAtSymbol(upDesc);
			mapParam.put("upDesc", upDesc);
			downDesc = SPJavaUtil.replaceAtSymbol(downDesc);
			mapParam.put("downDesc", downDesc);
			mapParam.put("text_width", maxStrWidth);

			scanView.setIcon(new ImageIcon(transcoder.makeImage(getCategory(), mapParam.get("type").toString(),
					mapParam)));
		}
	}

	private boolean isShow() {
		return mapParam.get("type") != null /*
											 * && mapParam.get("GrainType") != null
											 */;
	}

	public String getImageSrc() {
		return imageSrc;
	}

	private javax.swing.JButton symbol1;
	private javax.swing.JButton grainBut7;
	private javax.swing.JButton grainBut8;
	private javax.swing.JButton okButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JButton symbol3;
	private javax.swing.JButton symbol2;
	private javax.swing.JButton grainBut1;
	private javax.swing.JButton grainBut3;
	private javax.swing.JButton grainBut2;
	private javax.swing.JButton grainBut4;
	private javax.swing.JButton grainBut5;
	private javax.swing.JButton grainBut6;
	private javax.swing.JCheckBox requestCheck;
	private javax.swing.JComboBox upComboBoxValue;
	private javax.swing.JTextField upDiscription;
	private javax.swing.JComboBox downComboBoxValue;
	private javax.swing.JTextField downDiscription;
	private javax.swing.JLabel scanView;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel descLabel;
	private javax.swing.JPanel jPanel7;
	private javax.swing.JPanel jPanel8;
	private javax.swing.JPanel jPanel9;
	private String imageSrc;
	private final SvgTranscoderToPng transcoder = SvgTranscoderToPng.getInstance();
	@SuppressWarnings("rawtypes")
	private final Map mapParam = new HashMap();
	private final EventActionListener eventActionListener = new EventActionListener(this);
	private final EventDocumentListener eventDocumentListener = new EventDocumentListener();
}
