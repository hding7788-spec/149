package com.glaway.speciaword.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
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

/***
 * 
 * @author mosesx
 * @date 2013-4-25
 * @version V1.0
 */
public class BasicToleranceSymbolDialog extends SpecDialog {

	private static final long serialVersionUID = 1L;

	public BasicToleranceSymbolDialog(String category, String imageFolder) {
		super(category,imageFolder);
		initComponents();
	}

	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		basicSize = new javax.swing.JTextField();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		sizePreComboBox = new javax.swing.JComboBox();
		inputFormComboBox = new javax.swing.JComboBox();
		toleranceCode = new javax.swing.JTextField();
		jLabel5 = new javax.swing.JLabel();
		sizePostComboBox = new javax.swing.JComboBox();
		jLabel6 = new javax.swing.JLabel();
		outputFormComboBox = new javax.swing.JComboBox();
		jLabel7 = new javax.swing.JLabel();
		downOffset = new javax.swing.JTextField();
		jLabel8 = new javax.swing.JLabel();
		upOffset = new javax.swing.JTextField();
		advancedBut = new javax.swing.JButton();
		okBut = new javax.swing.JButton();
		cancelBut = new javax.swing.JButton();
		jPanel2 = new javax.swing.JPanel();
		scanView = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setPreferredSize(new java.awt.Dimension(517, 400));
		setTitle("基本公差");
		setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(
				(screenSize.width - this.getPreferredSize().width) / 2,
				(screenSize.height - this.getPreferredSize().height) / 2);

		basicSize.getDocument().addDocumentListener(eventDocumentListener);
		toleranceCode.getDocument().addDocumentListener(eventDocumentListener);
		downOffset.getDocument().addDocumentListener(eventDocumentListener);
		upOffset.getDocument().addDocumentListener(eventDocumentListener);

		jLabel1.setText("基本尺寸:");
		jLabel2.setText("尺寸前缀:");
		jLabel3.setText("输入形式:");
		jLabel4.setText("公差代号:");
		sizePreComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				SWConstant.BASICTOLERANCESYMBOL_SIZE_PRE));
		inputFormComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				SWConstant.BASICTOLERANCESYMBOL_INPUT_FORM));
		jLabel5.setText("尺寸后缀:");
		sizePostComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				SWConstant.BASICTOLERANCESYMBOL_SIZE_POST));
		jLabel6.setText("输出形式:");
		outputFormComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				SWConstant.BASICTOLERANCESYMBOL_OUTPUT_FORM));

		sizePreComboBox.addActionListener(eventActionListener);
		inputFormComboBox.addActionListener(eventActionListener);
		sizePostComboBox.addActionListener(eventActionListener);
		outputFormComboBox.addActionListener(eventActionListener);

		jLabel7.setText("下偏差:");
		jLabel8.setText("上偏差:");

		advancedBut.setText("高级(A)");
		advancedBut.setEnabled(false);
		advancedBut.setMnemonic(KeyEvent.VK_A);
		okBut.setText("确定(O)");
		okBut.setMnemonic(KeyEvent.VK_O);
		cancelBut.setText("取消(C)");
		cancelBut.setMnemonic(KeyEvent.VK_C);

		advancedBut.addActionListener(eventActionListener);
		okBut.addActionListener(eventActionListener);
		cancelBut.addActionListener(eventActionListener);

		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("预览:"));
		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				scanView, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				scanView, javax.swing.GroupLayout.DEFAULT_SIZE, 80,
				Short.MAX_VALUE));

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
																						.addGroup(
																								jPanel1Layout
																										.createSequentialGroup()
																										.addContainerGap()
																										.addGroup(
																												jPanel1Layout
																														.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.LEADING,
																																false)
																														.addGroup(
																																jPanel1Layout
																																		.createSequentialGroup()
																																		.addComponent(
																																				jLabel1)
																																		.addPreferredGap(
																																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																																		.addComponent(
																																				basicSize,
																																				javax.swing.GroupLayout.PREFERRED_SIZE,
																																				150,
																																				javax.swing.GroupLayout.PREFERRED_SIZE))
																														.addGroup(
																																jPanel1Layout
																																		.createSequentialGroup()
																																		.addComponent(
																																				jLabel2)
																																		.addPreferredGap(
																																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																																		.addComponent(
																																				sizePreComboBox,
																																				0,
																																				javax.swing.GroupLayout.DEFAULT_SIZE,
																																				Short.MAX_VALUE))
																														.addGroup(
																																jPanel1Layout
																																		.createSequentialGroup()
																																		.addComponent(
																																				jLabel3)
																																		.addPreferredGap(
																																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																																		.addComponent(
																																				inputFormComboBox,
																																				javax.swing.GroupLayout.PREFERRED_SIZE,
																																				150,
																																				javax.swing.GroupLayout.PREFERRED_SIZE))
																														.addGroup(
																																jPanel1Layout
																																		.createSequentialGroup()
																																		.addComponent(
																																				jLabel4)
																																		.addPreferredGap(
																																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																																		.addComponent(
																																				toleranceCode,
																																				javax.swing.GroupLayout.PREFERRED_SIZE,
																																				150,
																																				javax.swing.GroupLayout.PREFERRED_SIZE)))
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																										.addGroup(
																												jPanel1Layout
																														.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.LEADING,
																																false)
																														.addGroup(
																																jPanel1Layout
																																		.createSequentialGroup()
																																		.addComponent(
																																				jLabel6)
																																		.addPreferredGap(
																																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																																		.addComponent(
																																				outputFormComboBox,
																																				0,
																																				javax.swing.GroupLayout.DEFAULT_SIZE,
																																				Short.MAX_VALUE))
																														.addGroup(
																																javax.swing.GroupLayout.Alignment.TRAILING,
																																jPanel1Layout
																																		.createSequentialGroup()
																																		.addComponent(
																																				jLabel5)
																																		.addPreferredGap(
																																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																																		.addComponent(
																																				sizePostComboBox,
																																				0,
																																				javax.swing.GroupLayout.DEFAULT_SIZE,
																																				Short.MAX_VALUE))
																														.addGroup(
																																javax.swing.GroupLayout.Alignment.TRAILING,
																																jPanel1Layout
																																		.createParallelGroup(
																																				javax.swing.GroupLayout.Alignment.LEADING)
																																		.addGroup(
																																				jPanel1Layout
																																						.createSequentialGroup()
																																						.addComponent(
																																								jLabel8,
																																								javax.swing.GroupLayout.PREFERRED_SIZE,
																																								54,
																																								javax.swing.GroupLayout.PREFERRED_SIZE)
																																						.addPreferredGap(
																																								javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																																						.addComponent(
																																								upOffset,
																																								javax.swing.GroupLayout.PREFERRED_SIZE,
																																								150,
																																								javax.swing.GroupLayout.PREFERRED_SIZE))
																																		.addGroup(
																																				jPanel1Layout
																																						.createSequentialGroup()
																																						.addComponent(
																																								jLabel7,
																																								javax.swing.GroupLayout.PREFERRED_SIZE,
																																								54,
																																								javax.swing.GroupLayout.PREFERRED_SIZE)
																																						.addPreferredGap(
																																								javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																																						.addComponent(
																																								downOffset,
																																								javax.swing.GroupLayout.PREFERRED_SIZE,
																																								150,
																																								javax.swing.GroupLayout.PREFERRED_SIZE)))))
																						.addGroup(
																								jPanel1Layout
																										.createSequentialGroup()
																										.addGap(87,
																												87,
																												87)
																										.addComponent(
																												advancedBut)
																										.addGap(30,
																												30,
																												30)
																										.addComponent(
																												okBut)
																										.addGap(30,
																												30,
																												30)
																										.addComponent(
																												cancelBut)))
																		.addGap(0,
																				0,
																				Short.MAX_VALUE))
														.addComponent(
																jPanel2,
																javax.swing.GroupLayout.Alignment.TRAILING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addContainerGap()));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
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
																								basicSize,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								30,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jLabel2,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(
																jPanel1Layout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(
																				sizePreComboBox,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(
																				jLabel5,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(
																				sizePostComboBox,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																jLabel3,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																30,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																inputFormComboBox,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																30,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																jLabel6,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																30,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																outputFormComboBox,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																30,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel1Layout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(
																				jLabel4,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(
																				toleranceCode,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																jPanel1Layout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(
																				jLabel8,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(
																				upOffset,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																jLabel7,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																30,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																downOffset,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																30,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																advancedBut)
														.addComponent(okBut)
														.addComponent(cancelBut))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jPanel2,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addContainerGap()));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jPanel1,
								javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jPanel1,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).addContainerGap()));

		basicSize.setText("30");
		downOffset.setText("0");
		upOffset.setText("0");
		sizePostComboBox.setEditable(true);
		sizePreComboBox.setEditable(true);
		sizePreComboBox.getEditor().getEditorComponent()
				.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent evt) {
						changeScanView();
					}
				});
		sizePostComboBox.getEditor().getEditorComponent()
				.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent evt) {
						changeScanView();
					}
				});

		pack();
		this.setModal(true);
		this.setVisible(true);
	}

	private class EventActionListener implements ActionListener {

		private final BasicToleranceSymbolDialog parent;

		public EventActionListener(BasicToleranceSymbolDialog dialog) {
			parent = dialog;
		}

		@SuppressWarnings({ "unchecked" })
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source.equals(advancedBut)) {
				// 高级
			} else if (source.equals(okBut)) {
				// 确定
				Object obj = mapParam.get("type");
				if (obj != null) {
					String type = obj.toString();
					scanView.setIcon(new ImageIcon(transcoder.makeImage(
							getCategory(), type, mapParam)));
					/*
					 * imageSrc = "SPW:" + type + ":"; for (Iterator<Entry> iter
					 * = mapParam.entrySet().iterator(); iter.hasNext();) {
					 * Entry entry = iter.next(); imageSrc += entry.getKey() +
					 * "=" + entry.getValue() + ","; }
					 */
					// 图片本地化并替换src指向本地文件file://...
					imageSrc = CommonHelper
							.saveImageToLocal(transcoder.makeImage(
									getCategory(), type, mapParam),
									"" + System.currentTimeMillis(),getImageFolder());

					parent.setVisible(false);
				}
			} else if (source.equals(cancelBut)) {
				// 取消
				parent.setVisible(false);
			} else if (source.equals(sizePreComboBox)) {
				// 尺寸前缀
				String strSizePost = sizePreComboBox.getSelectedItem()
						.toString();
				mapParam.put("preText", strSizePost);
				changeScanView();
			} else if (source.equals(inputFormComboBox)) {
				// 输入形式
				changeScanView();
			} else if (source.equals(sizePostComboBox)) {
				// 尺寸后缀
				String strSizePost = sizePostComboBox.getSelectedItem()
						.toString();
				mapParam.put("postText", strSizePost);
				changeScanView();
			} else if (source.equals(outputFormComboBox)) {
				// 输出形式
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
		// 获取输入值
		String strBasicSize = basicSize.getText().trim();
		if (strBasicSize != null && !strBasicSize.equals("")) {

			String strTCode = toleranceCode.getText().trim();
			String strDownOffset = downOffset.getText().trim();
			if (!strDownOffset.startsWith("+")
					&& !strDownOffset.startsWith("-")) {
				strDownOffset = "+" + strDownOffset;
			}
			String strUpOffset = upOffset.getText().trim();
			if (!strUpOffset.startsWith("+") && !strUpOffset.startsWith("-")) {
				strUpOffset = "+" + strUpOffset;
			}

			String strSizePost = /* sizePostComboBox.getSelectedItem().toString() */sizePostComboBox
					.getEditor().getItem().toString();
			String strSizePre = /* sizePreComboBox.getSelectedItem().toString() */sizePreComboBox
					.getEditor().getItem().toString();
			// 模板
			if (mapParam.get("type") == null) {
				mapParam.put("type", "BASIC_TOLERANCE");
			}
			// 输出形式
			String strOutput = outputFormComboBox.getSelectedItem().toString();
			// 若果输出形式为 "代号", "代号偏差", "代号(偏差)" 中的一种
			if (strOutput.equals("代号") || strOutput.equals("代号偏差")
					|| strOutput.equals("代号(偏差)")) {
				strTCode = strBasicSize + strTCode;
			} else {
				strTCode = strBasicSize;
			}
			// 设置上下标是否显示
			boolean flag = false;
			if (strOutput.equals("代号")) {
				mapParam.put("isShow", "false");
			} else {
				mapParam.put("isShow", "true");
				flag = true;
			}

			if ((strOutput.equals("(偏差)") || strOutput.equals("代号(偏差)"))) {
				strTCode += "(";
				strSizePost = ")" + strSizePost;
			}
			// 计算字符所占宽度
			Dimension preSize = CommonHelper.calculateStringToImageSize(
					strSizePre, 17);
			// Dimension basicSize =
			// CommonHelper.calculateStringToImageSize(strBasicSize, 18);
			Dimension tcSize = CommonHelper.calculateStringToImageSize(
					strTCode, 17);
			Dimension upSize = CommonHelper.calculateStringToImageSize(
					strUpOffset, 13);
			Dimension downSize = CommonHelper.calculateStringToImageSize(
					strDownOffset, 13);
			Dimension sizePostSize = CommonHelper.calculateStringToImageSize(
					strSizePost, 17);
			double maxStrWidth = flag == true ? Math.max(upSize.getWidth(),
					downSize.getWidth()) : 0;
			// 占位符宽度 需要减去
			// Dimension defaultSize =
			// CommonHelper.calculateStringToImageSize("X", 13);
			// Dimension defaultBSize =
			// CommonHelper.calculateStringToImageSize("X", 17);
			//
			// double maxSize = preSize.getWidth() + tcSize.getWidth() +
			// maxStrWidth + sizePostSize.getWidth()
			// - defaultBSize.getWidth() * 3 - defaultSize.getWidth();
			//
			// mapParam.put("pre_width", preSize.getWidth());
			// mapParam.put("text_width", maxSize);
			// mapParam.put("basicText_width", (tcSize.getWidth() +
			// preSize.getWidth() - defaultBSize.getWidth()));
			// mapParam.put("postText_width",
			// (tcSize.getWidth() + maxStrWidth - defaultBSize.getWidth() -
			// defaultSize.getWidth()));

			double maxSize = preSize.getWidth() + tcSize.getWidth()
					+ maxStrWidth + sizePostSize.getWidth();

			mapParam.put("pre_width", preSize.getWidth());
			mapParam.put("text_width", maxSize);
			mapParam.put("basicText_width",
					(tcSize.getWidth() + preSize.getWidth()));
			// mapParam.put("postText_width", (tcSize.getWidth() +
			// maxStrWidth));
			mapParam.put("postText_width",
					preSize.getWidth() + tcSize.getWidth() + maxStrWidth);

			strTCode = SPJavaUtil.replaceAtSymbol(strTCode);
			strDownOffset = SPJavaUtil.replaceAtSymbol(strDownOffset);
			strUpOffset = SPJavaUtil.replaceAtSymbol(strUpOffset);
			strSizePost = SPJavaUtil.replaceAtSymbol(strSizePost);
			strSizePre = SPJavaUtil.replaceAtSymbol(strSizePre);
			
			mapParam.put("tcText", strTCode);
			mapParam.put("upText", strUpOffset);
			mapParam.put("downText", strDownOffset);
			mapParam.put("postText", strSizePost);
			mapParam.put("preText", strSizePre);
			scanView.setIcon(new ImageIcon(transcoder.makeImage(getCategory(),
					"" + mapParam.get("type"), mapParam)));
		}
	}

	public String getImageSrc() {
		return imageSrc;
	}

	private javax.swing.JButton advancedBut;
	private javax.swing.JButton okBut;
	private javax.swing.JButton cancelBut;
	private javax.swing.JComboBox sizePreComboBox;
	private javax.swing.JComboBox inputFormComboBox;
	private javax.swing.JComboBox sizePostComboBox;
	private javax.swing.JComboBox outputFormComboBox;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel scanView;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JTextField basicSize;
	private javax.swing.JTextField toleranceCode;
	private javax.swing.JTextField downOffset;
	private javax.swing.JTextField upOffset;
	private String imageSrc;
	private final EventActionListener eventActionListener = new EventActionListener(
			this);
	private final EventDocumentListener eventDocumentListener = new EventDocumentListener();
	@SuppressWarnings("rawtypes")
	private final Map mapParam = new HashMap();
	private final SvgTranscoderToPng transcoder = SvgTranscoderToPng
			.getInstance();
}
