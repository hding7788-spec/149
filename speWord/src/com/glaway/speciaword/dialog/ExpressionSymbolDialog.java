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
import com.glaway.speciaword.common.CommonStringUtil;
import com.glaway.speciaword.common.SvgTranscoderToPng;
import com.glaway.speciaword.util.SPJavaUtil;

/***
 * 
 * @author mosesx
 * @date 2013-4-24
 * @version V1.0
 */
public class ExpressionSymbolDialog extends SpecDialog {

	private static final long serialVersionUID = 1L;

	public ExpressionSymbolDialog(String category, String imageFolder) {
		super(category,imageFolder);
		initComponents();
	}

	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		upText = new javax.swing.JTextField();
		downText = new javax.swing.JTextField();
		okButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		jPanel2 = new javax.swing.JPanel();
		scanView = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("分式");
		setPreferredSize(new java.awt.Dimension(340, 235));
		setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screenSize.width - this.getPreferredSize().width) / 2,
				(screenSize.height - this.getPreferredSize().height) / 2);

		upText.getDocument().addDocumentListener(eventDocumentListener);
		downText.getDocument().addDocumentListener(eventDocumentListener);
		jLabel1.setText("分母");
		jLabel2.setText("分子");
		upText.setPreferredSize(new java.awt.Dimension(94, 30));
		downText.setPreferredSize(new java.awt.Dimension(94, 30));
		okButton.setText("确定(O)");
		okButton.setMnemonic(KeyEvent.VK_O);
		okButton.addActionListener(eventActionListener);
		cancelButton.setText("取消(C)");
		cancelButton.setMnemonic(KeyEvent.VK_C);
		cancelButton.addActionListener(eventActionListener);
		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("预览:"));
		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel2Layout
								.createSequentialGroup()
								.addContainerGap()
								.addComponent(scanView, javax.swing.GroupLayout.PREFERRED_SIZE, 261,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel2Layout.createSequentialGroup()
								.addComponent(scanView, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
								.addContainerGap()));

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addGap(5, 5, 5)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE,
																0, Short.MAX_VALUE)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING,
																								false)
																						.addGroup(
																								jPanel1Layout
																										.createSequentialGroup()
																										.addComponent(
																												jLabel2,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												39,
																												javax.swing.GroupLayout.PREFERRED_SIZE)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																										.addComponent(
																												upText,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												140,
																												javax.swing.GroupLayout.PREFERRED_SIZE))
																						.addGroup(
																								jPanel1Layout
																										.createSequentialGroup()
																										.addComponent(
																												jLabel1,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												39,
																												javax.swing.GroupLayout.PREFERRED_SIZE)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																										.addComponent(
																												downText)))
																		.addGap(33, 33, 33)
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(okButton)
																						.addComponent(cancelButton))))
										.addGap(5, 5, 5)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel1Layout
								.createSequentialGroup()
								.addGap(5, 5, 5)
								.addGroup(
										jPanel1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
												.addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(upText, javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(
										jPanel1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(downText, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(18, 18, 18)
								.addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(5, 5, 5)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGap(5, 5, 5)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup()
						.addGap(5, 5, 5)
						.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(5, 5, 5)));

		pack();
		this.setModal(true);
		this.setVisible(true);
	}

	private class EventActionListener implements ActionListener {

		private final ExpressionSymbolDialog parent;

		public EventActionListener(ExpressionSymbolDialog dialog) {
			parent = dialog;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (okButton.equals(source)) {
				if (CommonStringUtil.isEmpty(upText.getText()) || CommonStringUtil.isEmpty(downText.getText())) {
					return;
				} else {
					// imageSrc = "SPW:EXPRESSION:";
					// for (Iterator<Entry> iter =
					// mapParam.entrySet().iterator(); iter.hasNext();) {
					// Entry entry = iter.next();
					// imageSrc += entry.getKey() + "=" + entry.getValue() +
					// ",";
					// }

					// 图片本地化并替换src指向本地文件file://...
					imageSrc = CommonHelper.saveImageToLocal(
							transcoder.makeImage(getCategory(), "EXPRESSION", mapParam),
							"" + System.currentTimeMillis(),getImageFolder());

					parent.setVisible(false);
				}
			} else if (cancelButton.equals(source)) {
				parent.setVisible(false);
			}
		}
	}

	private class EventDocumentListener implements DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent arg0) {
			changeScanView();
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			changeScanView();
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			changeScanView();
		}
	}

	/**
	 * 更新预浏览视图
	 */
	@SuppressWarnings("unchecked")
	private void changeScanView() {
		// 计算分子分母占用宽度 取最大宽度计算图片宽度
		String strUptext = upText.getText().trim();
		String strDowntext = downText.getText().trim();
		Dimension upSize = CommonHelper.calculateStringToImageSize(strUptext, 13);
		Dimension downSize = CommonHelper.calculateStringToImageSize(strDowntext, 13);
		double maxStrWidth = Math.max(upSize.getWidth(), downSize.getWidth());

		mapParam.put("text_width", maxStrWidth);
		mapParam.put("up_width", upSize.getWidth());
		mapParam.put("down_width", downSize.getWidth());
		strUptext = SPJavaUtil.replaceAtSymbol(strUptext);
		mapParam.put("upText", strUptext);
		strDowntext = SPJavaUtil.replaceAtSymbol(strDowntext);
		mapParam.put("downText", strDowntext);
		scanView.setIcon(new ImageIcon(transcoder.makeImage(getCategory(), "EXPRESSION", mapParam)));
	}

	public String getImageSrc() {
		return this.imageSrc;
	}

	private javax.swing.JButton okButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel scanView;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JTextField upText;
	private javax.swing.JTextField downText;
	private String imageSrc;
	private final SvgTranscoderToPng transcoder = SvgTranscoderToPng.getInstance();
	@SuppressWarnings("rawtypes")
	private final Map mapParam = new HashMap();
	private final EventActionListener eventActionListener = new EventActionListener(this);
	private final EventDocumentListener eventDocumentListener = new EventDocumentListener();
}
