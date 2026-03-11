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
import com.glaway.speciaword.common.SvgTranscoderToPng;
import com.glaway.speciaword.util.SPJavaUtil;

/**
 * 
 * @author mosesx
 * @date 2013-4-24
 * @version V1.0
 */
public class UpOrDownLogoSymbolDialog extends SpecDialog {

	private static final long serialVersionUID = 1L;

	public UpOrDownLogoSymbolDialog(String category, String imageFolder) {
		super(category,imageFolder);
		initComponents();
	}

	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		upText = new javax.swing.JTextField();
		jLabel2 = new javax.swing.JLabel();
		basicText = new javax.swing.JTextField();
		jLabel3 = new javax.swing.JLabel();
		downText = new javax.swing.JTextField();
		okBut = new javax.swing.JButton();
		cancelBut = new javax.swing.JButton();
		jPanel2 = new javax.swing.JPanel();
		scanView = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("上下标");
		setPreferredSize(new java.awt.Dimension(380, 320));
		setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screenSize.width - this.getPreferredSize().width) / 2,
				(screenSize.height - this.getPreferredSize().height) / 2);

		basicText.getDocument().addDocumentListener(eventDocumentListener);
		downText.getDocument().addDocumentListener(eventDocumentListener);
		upText.getDocument().addDocumentListener(eventDocumentListener);
		jLabel1.setText("上标:");
		jLabel2.setText("基数:");
		jLabel3.setText("下标:");
		okBut.setText("确定(O)");
		okBut.setMnemonic(KeyEvent.VK_O);
		okBut.addActionListener(eventActionListener);
		cancelBut.setText("取消(C)");
		cancelBut.setMnemonic(KeyEvent.VK_C);
		cancelBut.addActionListener(eventActionListener);
		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("预览"));
		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel2Layout
								.createSequentialGroup()
								.addContainerGap()
								.addComponent(scanView, javax.swing.GroupLayout.PREFERRED_SIZE, 309,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						jPanel2Layout
								.createSequentialGroup()
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(scanView, javax.swing.GroupLayout.PREFERRED_SIZE, 75,
										javax.swing.GroupLayout.PREFERRED_SIZE).addGap(5, 5, 5)));

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
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING, false)
														.addComponent(jPanel2,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.PREFERRED_SIZE, 0,
																Short.MAX_VALUE)
														.addGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																jPanel1Layout
																		.createSequentialGroup()
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING)
																						.addComponent(
																								jLabel1,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								47,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addGroup(
																								jPanel1Layout
																										.createSequentialGroup()
																										.addComponent(
																												jLabel2,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												47,
																												javax.swing.GroupLayout.PREFERRED_SIZE)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent(
																												basicText,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												139,
																												javax.swing.GroupLayout.PREFERRED_SIZE))
																						.addComponent(
																								jLabel3,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								47,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addGroup(
																								jPanel1Layout
																										.createSequentialGroup()
																										.addComponent(
																												okBut)
																										.addGap(46, 46,
																												46)))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup(
																								jPanel1Layout
																										.createParallelGroup(
																												javax.swing.GroupLayout.Alignment.LEADING,
																												false)
																										.addComponent(
																												upText,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												140,
																												Short.MAX_VALUE)
																										.addComponent(
																												downText))
																						.addComponent(cancelBut))))
										.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel1Layout
								.createSequentialGroup()
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(
										jPanel1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(upText, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(8, 8, 8)
								.addGroup(
										jPanel1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(basicText, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(8, 8, 8)
								.addGroup(
										jPanel1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(downText, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(5, 5, 5)
								.addGroup(
										jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(okBut).addComponent(cancelBut))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap()));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addGap(5, 5, 5)
						.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(2, 2, 2)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addGap(5, 5, 5)
						.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));

		pack();
		this.setModal(true);
		this.setVisible(true);
	}

	private class EventActionListener implements ActionListener {

		private final UpOrDownLogoSymbolDialog parent;

		public EventActionListener(UpOrDownLogoSymbolDialog dialog) {
			parent = dialog;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (okBut.equals(source)) {
				// modify at 2013-10-09:放开不输入上下标不能生成图符的限制
				// if (/*
				// * CommonStringUtil.isEmpty(upText.getText()) || CommonStringUtil.isEmpty(downText.getText()) ||
				// */CommonStringUtil.isEmpty(basicText.getText())) {
				// return;
				// } else {
				// /*
				// * imageSrc = "SPW:UPORDOWNLOGO:"; for (Iterator<Entry> iter = mapParam.entrySet().iterator();
				// * iter.hasNext();) { Entry entry = iter.next(); imageSrc += entry.getKey() + "=" + entry.getValue()
				// * + ","; }
				// */
				//
				// // 图片本地化并替换src指向本地文件file://...
				// imageSrc = CommonHelper.saveImageToLocal(
				// transcoder.makeImage(getCategory(), "UPORDOWNLOGO", mapParam),
				// "" + System.currentTimeMillis());
				//
				// parent.setVisible(false);
				// }

				imageSrc = CommonHelper.saveImageToLocal(transcoder.makeImage(getCategory(), "UPORDOWNLOGO", mapParam),
						"" + System.currentTimeMillis(),getImageFolder());

				parent.setVisible(false);
			} else if (cancelBut.equals(source)) {
				parent.setVisible(false);
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
		// 计算分子分母占用宽度 取最大宽度计算图片宽度
		String strBasicText = basicText.getText().trim();
		String strUptext = upText.getText().trim();
		String strDowntext = downText.getText().trim();
		strBasicText = SPJavaUtil.replaceAtSymbol(strBasicText);
		strUptext = SPJavaUtil.replaceAtSymbol(strUptext);
		strDowntext = SPJavaUtil.replaceAtSymbol(strDowntext);
		Dimension basicSize = CommonHelper.calculateStringToImageSize(strBasicText, 16);
		Dimension upSize = CommonHelper.calculateStringToImageSize(strUptext, 12);
		Dimension downSize = CommonHelper.calculateStringToImageSize(strDowntext, 12);
		// 占位符宽度 需要减去
		// Dimension defaultUSize = CommonHelper.calculateStringToImageSize("X",
		// 13);
		// // 占位符宽度 需要减去
		// Dimension defaultBSize = CommonHelper.calculateStringToImageSize("X",
		// 18);
		double maxStrWidth = Math.max(upSize.getWidth(), downSize.getWidth());
		// mapParam.put("basictext_width",
		// (maxStrWidth + basicSize.getWidth() - defaultBSize.getWidth() -
		// defaultUSize.getWidth()));
		mapParam.put("basictext_width", (maxStrWidth + basicSize.getWidth()));
		// mapParam.put("updowntext_width", (basicSize.getWidth() -
		// defaultBSize.getWidth()));
		mapParam.put("updowntext_width", (basicSize.getWidth()));
		mapParam.put("basicText", strBasicText);
		mapParam.put("upText", strUptext);
		mapParam.put("downText", strDowntext);
		scanView.setIcon(new ImageIcon(transcoder.makeImage(getCategory(), "UPORDOWNLOGO", mapParam)));
	}

	public String getImageSrc() {
		return imageSrc;
	}

	private javax.swing.JButton okBut;
	private javax.swing.JButton cancelBut;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel scanView;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JTextField upText;
	private javax.swing.JTextField basicText;
	private javax.swing.JTextField downText;
	private String imageSrc;
	private final SvgTranscoderToPng transcoder = SvgTranscoderToPng.getInstance();
	@SuppressWarnings("rawtypes")
	private final Map mapParam = new HashMap();
	private final EventActionListener eventActionListener = new EventActionListener(this);
	private final EventDocumentListener eventDocumentListener = new EventDocumentListener();
}
