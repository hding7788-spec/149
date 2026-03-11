package com.glaway.speciaword.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.glaway.speciaword.common.CommonHelper;
import com.glaway.speciaword.common.SWConstant;
import com.glaway.speciaword.common.SvgTranscoderToPng;
import com.glaway.speciaword.util.SPJavaUtil;

/**
 * 
 * @author mosesx
 * @date 2013-4-25
 * @version V1.0
 */
public class FormToleranceSymbolDialog extends SpecDialog {

	private static final long serialVersionUID = 1L;

	public FormToleranceSymbolDialog(String category, String imageFolder) {
		super(category,imageFolder);
		initComponents();
	}

	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jPanel2 = new javax.swing.JPanel();
		toleranceSymbol1 = new javax.swing.JButton();
		toleranceSymbol2 = new javax.swing.JButton();
		toleranceSymbol3 = new javax.swing.JButton();
		toleranceSymbol4 = new javax.swing.JButton();
		toleranceSymbol5 = new javax.swing.JButton();
		toleranceSymbol6 = new javax.swing.JButton();
		toleranceSymbol7 = new javax.swing.JButton();
		toleranceSymbol8 = new javax.swing.JButton();
		toleranceSymbol9 = new javax.swing.JButton();
		toleranceSymbol10 = new javax.swing.JButton();
		toleranceSymbol11 = new javax.swing.JButton();
		toleranceSymbol12 = new javax.swing.JButton();
		toleranceSymbol13 = new javax.swing.JButton();
		toleranceSymbol14 = new javax.swing.JButton();
		toleranceSymbol15 = new javax.swing.JButton();
		jPanel3 = new javax.swing.JPanel();
		S_Check = new javax.swing.JCheckBox();
		O_Check = new javax.swing.JCheckBox();
		toleranceNumValue = new javax.swing.JTextField();
		jPanel4 = new javax.swing.JPanel();
		principleComboBox = new javax.swing.JComboBox();
		requ_Check = new javax.swing.JCheckBox();
		jLabel1 = new javax.swing.JLabel();
		formComboBox = new javax.swing.JComboBox();
		allSign = new javax.swing.JCheckBox();
		jPanel6 = new javax.swing.JPanel();
		basicValue1 = new javax.swing.JTextField();
		basicComboBox1 = new javax.swing.JComboBox();
		jPanel8 = new javax.swing.JPanel();
		basicValue2 = new javax.swing.JTextField();
		basicComboBox2 = new javax.swing.JComboBox();
		jPanel9 = new javax.swing.JPanel();
		basicValue3 = new javax.swing.JTextField();
		basicComboBox3 = new javax.swing.JComboBox();
		okBut = new javax.swing.JButton();
		cancelBut = new javax.swing.JButton();
		jPanel5 = new javax.swing.JPanel();
		scanView = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setPreferredSize(new java.awt.Dimension(563, 475));
		setTitle("形位公差");
		this.setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screenSize.width - this.getPreferredSize().width) / 2,
				(screenSize.height - this.getPreferredSize().height) / 2);

		toleranceSymbol1.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/form_symbol1.png")));
		toleranceSymbol2.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/form_symbol2.png")));
		toleranceSymbol3.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/form_symbol3.png")));
		toleranceSymbol4.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/form_symbol4.png")));
		toleranceSymbol5.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/form_symbol5.png")));
		toleranceSymbol6.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/form_symbol6.png")));
		toleranceSymbol7.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/form_symbol7.png")));
		toleranceSymbol8.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/form_symbol8.png")));
		toleranceSymbol9.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/form_symbol9.png")));
		toleranceSymbol10.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/form_symbol10.png")));
		toleranceSymbol11.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/form_symbol11.png")));
		toleranceSymbol12.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/form_symbol12.png")));
		toleranceSymbol13.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/form_symbol13.png")));
		toleranceSymbol14.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/form_symbol14.png")));
		toleranceSymbol15.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/form_symbol15.png")));
		toleranceNumValue.getDocument().addDocumentListener(eventDocumentListener);

		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("公差代号"));

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout
				.setHorizontalGroup(jPanel2Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel2Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel2Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel2Layout
																		.createSequentialGroup()
																		.addComponent(toleranceSymbol1,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(toleranceSymbol2,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(toleranceSymbol3,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(toleranceSymbol4,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(toleranceSymbol5,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(
																jPanel2Layout
																		.createSequentialGroup()
																		.addComponent(toleranceSymbol6,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(toleranceSymbol7,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(toleranceSymbol8,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(toleranceSymbol9,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(toleranceSymbol10,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(
																jPanel2Layout
																		.createSequentialGroup()
																		.addComponent(toleranceSymbol11,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(toleranceSymbol12,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(toleranceSymbol13,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(toleranceSymbol14,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(toleranceSymbol15,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel2Layout
								.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										jPanel2Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(toleranceSymbol1, javax.swing.GroupLayout.PREFERRED_SIZE,
														30, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(toleranceSymbol2, javax.swing.GroupLayout.PREFERRED_SIZE,
														30, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(toleranceSymbol3, javax.swing.GroupLayout.PREFERRED_SIZE,
														30, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(toleranceSymbol4, javax.swing.GroupLayout.PREFERRED_SIZE,
														30, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(toleranceSymbol5, javax.swing.GroupLayout.PREFERRED_SIZE,
														30, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(10, 10, 10)
								.addGroup(
										jPanel2Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(toleranceSymbol6, javax.swing.GroupLayout.PREFERRED_SIZE,
														30, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(toleranceSymbol7, javax.swing.GroupLayout.PREFERRED_SIZE,
														30, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(toleranceSymbol8, javax.swing.GroupLayout.PREFERRED_SIZE,
														30, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(toleranceSymbol9, javax.swing.GroupLayout.PREFERRED_SIZE,
														30, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(toleranceSymbol10,
														javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10,
										Short.MAX_VALUE)
								.addGroup(
										jPanel2Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(toleranceSymbol11,
														javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(toleranceSymbol12,
														javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(toleranceSymbol13,
														javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(toleranceSymbol14,
														javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(toleranceSymbol15,
														javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE))));

		toleranceSymbol1.addActionListener(eventActionListener);
		toleranceSymbol2.addActionListener(eventActionListener);
		toleranceSymbol3.addActionListener(eventActionListener);
		toleranceSymbol4.addActionListener(eventActionListener);
		toleranceSymbol5.addActionListener(eventActionListener);
		toleranceSymbol6.addActionListener(eventActionListener);
		toleranceSymbol7.addActionListener(eventActionListener);
		toleranceSymbol8.addActionListener(eventActionListener);
		toleranceSymbol9.addActionListener(eventActionListener);
		toleranceSymbol10.addActionListener(eventActionListener);
		toleranceSymbol11.addActionListener(eventActionListener);
		toleranceSymbol12.addActionListener(eventActionListener);
		toleranceSymbol13.addActionListener(eventActionListener);
		toleranceSymbol14.addActionListener(eventActionListener);
		toleranceSymbol15.addActionListener(eventActionListener);

		jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("公差数值"));
		S_Check.setText("S");
		O_Check.setText("Φ");
		S_Check.addActionListener(eventActionListener);
		O_Check.addActionListener(eventActionListener);

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel3Layout.createSequentialGroup().addContainerGap().addComponent(S_Check)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(O_Check)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(toleranceNumValue)));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						jPanel3Layout
								.createSequentialGroup()
								.addGap(0, 2, Short.MAX_VALUE)
								.addGroup(
										jPanel3Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(S_Check)
												.addComponent(O_Check)
												.addComponent(toleranceNumValue,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))));

		jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("相关原则"));

		principleComboBox.setModel(new javax.swing.DefaultComboBoxModel(SWConstant.FormToleranceSymbol_PRINCIPLE));
		principleComboBox.addActionListener(eventActionListener);

		requ_Check.setText("可逆要求");
		requ_Check.addActionListener(eventActionListener);

		javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel4Layout
								.createSequentialGroup()
								.addContainerGap()
								.addComponent(principleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45,
										Short.MAX_VALUE).addComponent(requ_Check)));
		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel4Layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(principleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
										javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(requ_Check)));

		jLabel1.setText("形状限定");

		formComboBox.setModel(new javax.swing.DefaultComboBoxModel(SWConstant.FormToleranceSymbol_FORM));
		formComboBox.addActionListener(eventActionListener);

		allSign.setText("全周符号");
		allSign.addActionListener(eventActionListener);

		jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("基准一"));

		basicComboBox1.setModel(new javax.swing.DefaultComboBoxModel(SWConstant.FormToleranceSymbol_BASIC));
		basicComboBox1.addActionListener(eventActionListener);
		basicValue1.getDocument().addDocumentListener(eventDocumentListener);

		javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
		jPanel6.setLayout(jPanel6Layout);
		jPanel6Layout.setHorizontalGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel6Layout
								.createSequentialGroup()
								.addComponent(basicValue1, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(basicComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));
		jPanel6Layout.setVerticalGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						jPanel6Layout
								.createSequentialGroup()
								.addGap(0, 13, Short.MAX_VALUE)
								.addGroup(
										jPanel6Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(basicValue1, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(basicComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE,
														30, javax.swing.GroupLayout.PREFERRED_SIZE))));

		jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("基准三"));
		basicComboBox2.setModel(new javax.swing.DefaultComboBoxModel(SWConstant.FormToleranceSymbol_BASIC));
		basicComboBox2.addActionListener(eventActionListener);
		basicValue2.getDocument().addDocumentListener(eventDocumentListener);

		javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
		jPanel8.setLayout(jPanel8Layout);
		jPanel8Layout.setHorizontalGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel8Layout
								.createSequentialGroup()
								.addComponent(basicValue2, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(basicComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));
		jPanel8Layout.setVerticalGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						jPanel8Layout
								.createSequentialGroup()
								.addGap(0, 13, Short.MAX_VALUE)
								.addGroup(
										jPanel8Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(basicValue2, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(basicComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE,
														30, javax.swing.GroupLayout.PREFERRED_SIZE))));

		jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("基准二"));
		basicComboBox3.setModel(new javax.swing.DefaultComboBoxModel(SWConstant.FormToleranceSymbol_BASIC));
		basicComboBox3.addActionListener(eventActionListener);
		basicValue3.getDocument().addDocumentListener(eventDocumentListener);

		javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
		jPanel9.setLayout(jPanel9Layout);
		jPanel9Layout.setHorizontalGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel9Layout
								.createSequentialGroup()
								.addComponent(basicValue3, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(basicComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));
		jPanel9Layout.setVerticalGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						jPanel9Layout
								.createSequentialGroup()
								.addGap(0, 13, Short.MAX_VALUE)
								.addGroup(
										jPanel9Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(basicValue3, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(basicComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE,
														30, javax.swing.GroupLayout.PREFERRED_SIZE))));

		okBut.setText("确定(O)");
		okBut.setMnemonic(KeyEvent.VK_O);
		cancelBut.setText("取消(C)");
		cancelBut.setMnemonic(KeyEvent.VK_C);
		okBut.addActionListener(eventActionListener);
		cancelBut.addActionListener(eventActionListener);

		jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("预览"));

		javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
		jPanel5.setLayout(jPanel5Layout);
		jPanel5Layout.setHorizontalGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(scanView, javax.swing.GroupLayout.Alignment.TRAILING,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(scanView, javax.swing.GroupLayout.Alignment.TRAILING,
						javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE));

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
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
																		.addComponent(jPanel2,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addGap(5, 5, 5)
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING,
																								false)
																						.addGroup(
																								jPanel1Layout
																										.createSequentialGroup()
																										.addComponent(
																												jLabel1,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												57,
																												javax.swing.GroupLayout.PREFERRED_SIZE)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent(
																												formComboBox,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												74,
																												javax.swing.GroupLayout.PREFERRED_SIZE)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																												44,
																												Short.MAX_VALUE)
																										.addComponent(
																												allSign)
																										.addGap(8, 8, 8))
																						.addGroup(
																								jPanel1Layout
																										.createParallelGroup(
																												javax.swing.GroupLayout.Alignment.LEADING,
																												false)
																										.addComponent(
																												jPanel3,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												Short.MAX_VALUE)
																										.addComponent(
																												jPanel4,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												Short.MAX_VALUE))))
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(jPanel6,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup(
																								jPanel1Layout
																										.createSequentialGroup()
																										.addComponent(
																												okBut)
																										.addGap(30, 30,
																												30)
																										.addComponent(
																												cancelBut))
																						.addGroup(
																								jPanel1Layout
																										.createSequentialGroup()
																										.addComponent(
																												jPanel9,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												javax.swing.GroupLayout.PREFERRED_SIZE)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent(
																												jPanel8,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												javax.swing.GroupLayout.PREFERRED_SIZE))))
														.addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(jPanel3,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addGap(2, 2, 2)
																		.addComponent(jPanel4,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								jLabel1,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								30,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								formComboBox,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								30,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent(allSign))))
										.addGap(5, 5, 5)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(okBut).addComponent(cancelBut))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap()));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));

		setEnabelModelTwo(false);
		pack();
		this.setModal(true);
		this.setVisible(true);
	}

	private class EventActionListener implements ActionListener {

		private final FormToleranceSymbolDialog parent;

		public EventActionListener(FormToleranceSymbolDialog dialog) {
			parent = dialog;
		}

		@SuppressWarnings({ "unchecked" })
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source.equals(cancelBut)) {
				// 取消
				parent.setVisible(false);
			} else if (source.equals(okBut)) {
				// 确定
				if (mapParam.get("type") != null) {
					Object type = mapParam.get("type");
					/*
					 * if (null != type) { scanView.setIcon(new ImageIcon(transcoder.makeImage("" +
					 * mapParam.get("type"), mapParam))); imageSrc = "SPW:" + type + ":"; for (Iterator<Entry> iter =
					 * mapParam.entrySet().iterator(); iter.hasNext();) { Entry entry = iter.next(); imageSrc +=
					 * entry.getKey() + "=" + entry.getValue() + ","; } }
					 */

					// 图片本地化并替换src指向本地文件file://...
					imageSrc = CommonHelper.saveImageToLocal(
							transcoder.makeImage(getCategory(), type.toString(), mapParam),
							"" + System.currentTimeMillis(),getImageFolder());

					parent.setVisible(false);
				}
			} else if (source.equals(toleranceSymbol1)) {
				// 符号1
				if (isAllEnabelFalse) {
					setEnabelModelTwo(true);
				}
				setEnabelModelOne(false);
				clearBasicText();
				mapParam.put("type", "FORM_TOLERANCE1");
				mapParam.put("model_1", "1");
				toleranceNumValue.setText("0.015");
				changeScanView();
			} else if (source.equals(toleranceSymbol2)) {
				// 符号2
				if (isAllEnabelFalse) {
					setEnabelModelTwo(true);
				}
				setEnabelModelOne(false);
				clearBasicText();
				mapParam.put("type", "FORM_TOLERANCE1");
				mapParam.put("model_1", "2");
				toleranceNumValue.setText("0.015");
				changeScanView();
			} else if (source.equals(toleranceSymbol3)) {
				// 符号3
				if (isAllEnabelFalse) {
					setEnabelModelTwo(true);
				}
				setEnabelModelOne(false);
				clearBasicText(); 
				mapParam.put("type", "FORM_TOLERANCE1");
				mapParam.put("model_1", "3");
				toleranceNumValue.setText("0.01");
				changeScanView();
			} else if (source.equals(toleranceSymbol4)) {
				// 符号4
				if (isAllEnabelFalse) {
					setEnabelModelTwo(true);
				}
				setEnabelModelOne(false);
				clearBasicText();
				mapParam.put("type", "FORM_TOLERANCE1");
				mapParam.put("model_1", "4");
				toleranceNumValue.setText("0.01");
				changeScanView();
			} else if (source.equals(toleranceSymbol5)) {
				// 符号5
				clearAll();
				setEnabelModelTwo(true);
				allSign.setEnabled(true);
				mapParam.put("type", "FORM_TOLERANCE2");
				mapParam.put("model_2", "5");
				changeScanView();
			} else if (source.equals(toleranceSymbol6)) {
				// 符号6
				clearAll();
				setEnabelModelTwo(true);
				allSign.setEnabled(true);
				mapParam.put("type", "FORM_TOLERANCE2");
				mapParam.put("model_2", "6");
				changeScanView();
			} else if (source.equals(toleranceSymbol7)) {
				// 符号7
				clearAll();
				setEnabelModelTwo(true);
				mapParam.put("type", "FORM_TOLERANCE2");
				mapParam.put("model_2", "7");
				changeScanView();
			} else if (source.equals(toleranceSymbol8)) {
				// 符号8
				clearAll();
				setEnabelModelTwo(true);
				mapParam.put("type", "FORM_TOLERANCE2");
				mapParam.put("model_2", "8");
				changeScanView();
			} else if (source.equals(toleranceSymbol9)) {
				// 符号9
				clearAll();
				setEnabelModelTwo(true);
				mapParam.put("type", "FORM_TOLERANCE2");
				mapParam.put("model_2", "9");
				changeScanView();
			} else if (source.equals(toleranceSymbol10)) {
				// 符号10
				clearAll();
				setEnabelModelTwo(true);
				mapParam.put("type", "FORM_TOLERANCE2");
				mapParam.put("model_2", "10");
				changeScanView();
			} else if (source.equals(toleranceSymbol11)) {
				// 符号11
				clearAll();
				setEnabelModelTwo(true);
				mapParam.put("type", "FORM_TOLERANCE2");
				mapParam.put("model_2", "11");
				changeScanView();
			} else if (source.equals(toleranceSymbol12)) {
				// 符号12
				clearAll();
				setEnabelModelTwo(true);
				mapParam.put("type", "FORM_TOLERANCE2");
				mapParam.put("model_2", "12");
				changeScanView();
			} else if (source.equals(toleranceSymbol13)) {
				// 符号13
				clearAll();
				setEnabelModelTwo(true);
				mapParam.put("type", "FORM_TOLERANCE2");
				mapParam.put("model_2", "13");
				changeScanView();
			} else if (source.equals(toleranceSymbol14)) {
				// 符号14
				clearAll();
				setEnabelModelTwo(true);
				mapParam.put("type", "FORM_TOLERANCE2");
				mapParam.put("model_2", "14");
				changeScanView();
			} else if (source.equals(toleranceSymbol15)) {
				// 符号15
				setEnabelModelTwo(false);
				clearAll();
			} else if (source.equals(S_Check)) {
				// S
				changeScanView();
			} else if (source.equals(O_Check)) {
				// O
				changeScanView();
			} else if (source.equals(requ_Check)) {
				// 可逆要求
				changeScanView();
			} else if (source.equals(allSign)) {
				// 全周符号
				changeScanView();
			} else if (source.equals(formComboBox)) {
				// 形状限定
				changeScanView();
			} else if (source.equals(basicComboBox1)) {
				// 基准一
				changeScanView();
			} else if (source.equals(basicComboBox2)) {
				// 基准二
				changeScanView();
			} else if (source.equals(basicComboBox3)) {
				// 基准三
				changeScanView();
			} else if (source.equals(principleComboBox)) {
				// 相关原则
				checkPrincipleComboBox();
				changeScanView();
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
		Object type = mapParam.get("type");
		if (null != type) {
			String text = "";
			if (S_Check.isSelected()) {
				text += "S";
			}
			if (O_Check.isSelected()) {
				text += "Φ";
			}
			text += toleranceNumValue.getText().trim();
			text += formComboBox.getSelectedItem().toString();
			// 计算文字宽度
			// Dimension xSize = CommonHelper.calculateStringToImageSize("X", 16);
			// Dimension textSize = CommonHelper.calculateStringToImageSize(text,18);
			// double size = textSize.getWidth() - xSize.getWidth();
			// Dimension xSize = CommonHelper.calculateStringToImageSize("X", 16);
			Dimension textSize = CommonHelper.calculateStringToImageSize(text, 16);
			double size = textSize.getWidth();

			mapParam.put("text_width", size);
			mapParam.put("text", text);

			if (text.equals("")) {
				mapParam.put("isShowText", "false");
			} else {
				mapParam.put("isShowText", "true");
			}

			// 设置默认的rect宽度
			mapParam.put("rectWidth", "" + (size + 35.686));
			String strPrinciple = principleComboBox.getSelectedItem().toString();
			if (!strPrinciple.equals("")) {
				strPrinciple = strPrinciple.replace("(", "");
				strPrinciple = strPrinciple.replace(")", "");
				mapParam.put("isShowPrinciple", "true");
				//
				mapParam.put("rectWidth", "" + (size + 62.681 + 7.686));
			} else {
				mapParam.put("isShowPrinciple", "false");
			}
			mapParam.put("PrincipleText", strPrinciple);

			// 全周符号
			if (allSign.isEnabled() && allSign.isSelected()) {
				mapParam.put("isShowC", "true");
			} else {
				mapParam.put("isShowC", "false");
			}
			// 可逆要求
			if (requ_Check.isEnabled() && requ_Check.isSelected()) {
				mapParam.put("isShowR", "true");
				mapParam.put("rectWidth", "" + (size + 81.381 + 7.686));
			} else {
				mapParam.put("isShowR", "false");
			}
			if (type.equals("FORM_TOLERANCE1")) {

			} else if (type.equals("FORM_TOLERANCE2")) {
				String strBasic1 = basicValue1.getText().trim();
				String strBasic2 = basicValue3.getText().trim();
				String strBasic3 = basicValue2.getText().trim();
				String strBasicCV1 = basicComboBox1.getSelectedItem().toString();
				String strBasicCV2 = basicComboBox3.getSelectedItem().toString();
				String strBasicCV3 = basicComboBox2.getSelectedItem().toString();
				strBasicCV1 = strBasicCV1.replace("(", "");
				strBasicCV1 = strBasicCV1.replace(")", "");
				strBasicCV2 = strBasicCV2.replace("(", "");
				strBasicCV2 = strBasicCV2.replace(")", "");
				strBasicCV3 = strBasicCV3.replace("(", "");
				strBasicCV3 = strBasicCV3.replace(")", "");

				Dimension b1Size = CommonHelper.calculateStringToImageSize(strBasic1, 16);
				Dimension b2Size = CommonHelper.calculateStringToImageSize(strBasic2, 16);
				Dimension b3Size = CommonHelper.calculateStringToImageSize(strBasic3, 16);

				strBasic1 = SPJavaUtil.replaceAtSymbol(strBasic1);
				mapParam.put("BV1", strBasic1);
				strBasic2 = SPJavaUtil.replaceAtSymbol(strBasic2);
				mapParam.put("BV2", strBasic2);
				strBasic3 = SPJavaUtil.replaceAtSymbol(strBasic3);
				mapParam.put("BV3", strBasic3);
				strBasicCV1 = SPJavaUtil.replaceAtSymbol(strBasicCV1);
				mapParam.put("BC1", strBasicCV1);
				strBasicCV2 = SPJavaUtil.replaceAtSymbol(strBasicCV2);
				mapParam.put("BC2", strBasicCV2);
				strBasicCV3 = SPJavaUtil.replaceAtSymbol(strBasicCV3);
				mapParam.put("BC3", strBasicCV3);
				double beginX = Double.parseDouble((String) mapParam.get("rectWidth"));

				if (strBasic1.equals("") && strBasicCV1.equals("")) {
					mapParam.put("isShowLine1", "false");
				} else {
					mapParam.put("isShowLine1", "true");
					//zhanghao 解決丁浩
					mapParam.put("LX1", "" + (beginX+5));
					beginX = beginX + 4;
					mapParam.put("BVXT1", "" + (beginX+3));
					
					beginX = beginX + b1Size.getWidth();
					if (!strBasicCV1.equals("")) {
						mapParam.put("isShowBC1", "true");
						beginX = beginX + 16;
						mapParam.put("CX1", "" + beginX);
						mapParam.put("CXT1", "" + (beginX - 6));
						beginX = beginX + 10;
					} else {
						mapParam.put("isShowBC1", "false");
					}
				}

				if (strBasic2.equals("") && strBasicCV2.equals("")) {
					mapParam.put("isShowLine2", "false");
				} else {
					mapParam.put("isShowLine2", "true");
					mapParam.put("LX2", "" + beginX);
					beginX = beginX + 4;
					mapParam.put("BVXT2", "" + beginX);
					beginX = beginX + b2Size.getWidth();
					if (!strBasicCV2.equals("")) {
						mapParam.put("isShowBC2", "true");
						beginX = beginX + 16;
						mapParam.put("CX2", "" + beginX);
						mapParam.put("CXT2", "" + (beginX - 6));
						beginX = beginX + 10;
					} else {
						mapParam.put("isShowBC2", "false");
					}
				}

				if (strBasic3.equals("") && strBasicCV3.equals("")) {
					mapParam.put("isShowLine3", "false");
				} else {
					mapParam.put("isShowLine3", "true");
					mapParam.put("LX3", "" + beginX);
					beginX = beginX + 4;
					mapParam.put("BVXT3", "" + beginX);
					beginX = beginX + b3Size.getWidth();
					if (!strBasicCV3.equals("")) {
						mapParam.put("isShowBC3", "true");
						beginX = beginX + 16;
						mapParam.put("CX3", "" + beginX);
						mapParam.put("CXT3", "" + (beginX - 6));
						beginX = beginX + 10;
					} else {
						mapParam.put("isShowBC3", "false");
					}
				}

				mapParam.put("mWith", "" + (beginX + 8));
				mapParam.put("rectWidth", "" + (beginX));
			}

			scanView.setIcon(new ImageIcon(transcoder.makeImage(getCategory(), type.toString(), mapParam)));
		}
	}

	private void setEnabelModelOne(boolean flag) {
		requ_Check.setEnabled(flag);
		allSign.setEnabled(false);
		basicComboBox1.setEnabled(flag);
		basicComboBox2.setEnabled(flag);
		basicComboBox3.setEnabled(flag);
		basicValue1.setEnabled(flag);
		basicValue2.setEnabled(flag);
		basicValue3.setEnabled(flag);
		requ_Check.setEnabled(false);// 可逆要求
	}

	private void setEnabelModelTwo(boolean flag) {

		isAllEnabelFalse = !flag;

		S_Check.setEnabled(flag);
		O_Check.setEnabled(flag);
		toleranceNumValue.setEnabled(flag);
		formComboBox.setEnabled(flag);
		principleComboBox.setEnabled(flag);

		requ_Check.setEnabled(false);// 可逆要求
		allSign.setEnabled(false);
		basicComboBox1.setEnabled(flag);
		basicComboBox2.setEnabled(flag);
		basicComboBox3.setEnabled(flag);
		basicValue1.setEnabled(flag);
		basicValue2.setEnabled(flag);
		basicValue3.setEnabled(flag);
	}

	private void checkPrincipleComboBox() {
		String strPrinciple = principleComboBox.getSelectedItem().toString();
		if (strPrinciple.equals("(P)")) {
			requ_Check.setSelected(false);
			requ_Check.setEnabled(false);// 可逆要求
		} else if (strPrinciple.equals("(M)")) {
			requ_Check.setEnabled(true);// 可逆要求
		} else if (strPrinciple.equals("(E)")) {
			requ_Check.setSelected(false);
			requ_Check.setEnabled(false);// 可逆要求
		} else if (strPrinciple.equals("(L)")) {
			requ_Check.setEnabled(true);// 可逆要求
		} else if (strPrinciple.equals("(F)")) {
			requ_Check.setSelected(false);
			requ_Check.setEnabled(false);// 可逆要求
		} else if (strPrinciple.equals("(S)")) {
			requ_Check.setSelected(false);
			requ_Check.setEnabled(false);// 可逆要求
		} else {
			requ_Check.setSelected(false);
			requ_Check.setEnabled(false);// 可逆要求
		}
	}

	@SuppressWarnings("unchecked")
	private void clearAll() {
		S_Check.setSelected(false);
		O_Check.setSelected(false);
		toleranceNumValue.setText("");
		formComboBox.setSelectedIndex(0);
		principleComboBox.setSelectedIndex(0);
		requ_Check.setSelected(false);// 可逆要求
		allSign.setSelected(false);
		basicComboBox1.setSelectedIndex(0);
		basicComboBox2.setSelectedIndex(0);
		basicComboBox3.setSelectedIndex(0);
		basicValue1.setText("");
		basicValue2.setText("");
		basicValue3.setText("");
		scanView.setIcon(null);
		mapParam.put("type", null);
	}

	private void clearBasicText() {
		allSign.setSelected(false);
		basicComboBox1.setSelectedIndex(0);
		basicComboBox2.setSelectedIndex(0);
		basicComboBox3.setSelectedIndex(0);
		basicValue1.setText("");
		basicValue2.setText("");
		basicValue3.setText("");
	}

	public String getImageSrc() {
		return imageSrc;
	}

	private javax.swing.JButton toleranceSymbol1;
	private javax.swing.JButton toleranceSymbol10;
	private javax.swing.JButton toleranceSymbol11;
	private javax.swing.JButton toleranceSymbol12;
	private javax.swing.JButton toleranceSymbol13;
	private javax.swing.JButton toleranceSymbol14;
	private javax.swing.JButton toleranceSymbol15;
	private javax.swing.JButton okBut;
	private javax.swing.JButton cancelBut;
	private javax.swing.JButton toleranceSymbol2;
	private javax.swing.JButton toleranceSymbol3;
	private javax.swing.JButton toleranceSymbol4;
	private javax.swing.JButton toleranceSymbol5;
	private javax.swing.JButton toleranceSymbol6;
	private javax.swing.JButton toleranceSymbol7;
	private javax.swing.JButton toleranceSymbol8;
	private javax.swing.JButton toleranceSymbol9;
	private javax.swing.JCheckBox S_Check;
	private javax.swing.JCheckBox O_Check;
	private javax.swing.JCheckBox requ_Check;
	private javax.swing.JCheckBox allSign;
	private javax.swing.JComboBox principleComboBox;
	private javax.swing.JComboBox formComboBox;
	private javax.swing.JComboBox basicComboBox1;
	private javax.swing.JComboBox basicComboBox2;
	private javax.swing.JComboBox basicComboBox3;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel scanView;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JPanel jPanel8;
	private javax.swing.JPanel jPanel9;
	private javax.swing.JTextField toleranceNumValue;
	private javax.swing.JTextField basicValue1;
	private javax.swing.JTextField basicValue2;
	private javax.swing.JTextField basicValue3;
	private String imageSrc;
	private boolean isAllEnabelFalse;
	private final EventActionListener eventActionListener = new EventActionListener(this);
	private final EventDocumentListener eventDocumentListener = new EventDocumentListener();
	@SuppressWarnings("rawtypes")
	private final Map mapParam = new HashMap();
	private final SvgTranscoderToPng transcoder = SvgTranscoderToPng.getInstance();
}
