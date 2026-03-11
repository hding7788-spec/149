package com.glaway.speciaword.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

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
public class CircleSymbolDialog extends SpecDialog {

	private static final long serialVersionUID = 1L;

	public CircleSymbolDialog(String category, String imageFolder) {
		super(category,imageFolder);
		initComponents();
	}

	private void initComponents() {

		jPanel3 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		textComboBox = new javax.swing.JComboBox();
		jPanel4 = new javax.swing.JPanel();
		circelBut = new javax.swing.JButton();
		rectBut = new javax.swing.JButton();
		hTrigonBut = new javax.swing.JButton();
		vTrigonBut = new javax.swing.JButton();
		polygonBut = new javax.swing.JButton();
		jPanel5 = new javax.swing.JPanel();
		okButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		jPanel1 = new javax.swing.JPanel();
		scanView = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("带圈字符");
		this.setResizable(false);
		this.setBackground(Color.WHITE);
		setPreferredSize(new java.awt.Dimension(580, 550));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screenSize.width - this.getPreferredSize().width) / 2,
				(screenSize.height - this.getPreferredSize().height) / 2);

		jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

		jLabel1.setText("文字:");
		jPanel3.add(jLabel1);

		textComboBox.setModel(new javax.swing.DefaultComboBoxModel(SWConstant.CIRCLESYMBOL_COMBOBOX_VALUE));
		textComboBox.setPreferredSize(new java.awt.Dimension(280, 30));
		textComboBox.addActionListener(eventActionListener);
		jPanel3.add(textComboBox);

		textComboBox.setEditable(false);
		textComboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent evt) {
				changeScanView();
			}
		});

		jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("样式:"));
		jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

		circelBut.setPreferredSize(new java.awt.Dimension(30, 30));
		circelBut.addActionListener(eventActionListener);
		circelBut.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/circel.png")));
		jPanel4.add(circelBut);

		rectBut.setPreferredSize(new java.awt.Dimension(30, 30));
		rectBut.addActionListener(eventActionListener);
		rectBut.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/rect.png")));
		jPanel4.add(rectBut);

		hTrigonBut.setPreferredSize(new java.awt.Dimension(30, 30));
		hTrigonBut.addActionListener(eventActionListener);
		hTrigonBut.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/hTrigon.png")));
		jPanel4.add(hTrigonBut);

		vTrigonBut.setPreferredSize(new java.awt.Dimension(30, 30));
		vTrigonBut.setEnabled(true);
		vTrigonBut.addActionListener(eventActionListener);
		vTrigonBut.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/vTrigon.png")));
		// jPanel4.add(vTrigonBut);

		polygonBut.setPreferredSize(new java.awt.Dimension(30, 30));
		polygonBut.addActionListener(eventActionListener);
		polygonBut.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/polygon.png")));
		jPanel4.add(polygonBut);

		jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 30, 5));

		okButton.setText("确定(O)");
		okButton.setMnemonic(KeyEvent.VK_O);
		okButton.addActionListener(eventActionListener);
		jPanel5.add(okButton);

		cancelButton.setText("取消(C)");
		cancelButton.setMnemonic(KeyEvent.VK_C);
		cancelButton.addActionListener(eventActionListener);
		jPanel5.add(cancelButton);

		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("预览"));

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(scanView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel1Layout
								.createSequentialGroup()
								.addComponent(scanView, javax.swing.GroupLayout.PREFERRED_SIZE, 310,
										javax.swing.GroupLayout.PREFERRED_SIZE).addGap(0, 1, Short.MAX_VALUE)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addGap(10, 10, 10)
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
										.addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 557,
												Short.MAX_VALUE)
										.addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addContainerGap(30, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addGap(4, 4, 4)
						.addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));

		pack();
		this.setModal(true);
		this.setVisible(true);
	}

	/**
	 * 更新预浏览视图
	 */
	@SuppressWarnings("unchecked")
	private void changeScanView() {
		String text = textComboBox.getSelectedItem().toString();
		Object obj = mapParam.get("type");
		if (obj != null) {
			String type = obj.toString();
			Dimension size = CommonHelper.calculateStringToImageSize(text, 14);
			double strWidth = size.getWidth();
			mapParam.put("text_width", strWidth);
			text = SPJavaUtil.replaceAtSymbol(text);
			mapParam.put("text", text);
			scanView.setIcon(new ImageIcon(transcoder.makeImage(getCategory(), type.toString(), mapParam)));
		}
		// 占位符宽度 需要减去
	}

	/***
	 * 时间监听器
	 * 
	 * @author MosesX
	 * @2013-4-26
	 * 
	 */
	private class EventActionListener implements ActionListener {
		private final CircleSymbolDialog parent;

		public EventActionListener(CircleSymbolDialog dialog) {
			parent = dialog;
		}

		@SuppressWarnings({ "unchecked" })
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (okButton.equals(source)) {
				if (mapParam.get("type") != null) {
					String type = mapParam.get("type").toString();
					if (null != type && !"".equals(type)) {
						/***
						 * 自定义spw协议的方案暂时屏蔽
						 */
						/*
						 * imageSrc = "SPW:" + type + ":"; for (Iterator<Entry> iter = mapParam.entrySet().iterator();
						 * iter.hasNext();) { Entry entry = iter.next(); imageSrc += entry.getKey() + "=" +
						 * entry.getValue() + ","; }
						 */
						// 图片本地化并替换src指向本地文件file://...
						imageSrc = CommonHelper.saveImageToLocal(
								transcoder.makeImage(getCategory(), type.toString(), mapParam),
								"" + System.currentTimeMillis(),getImageFolder());

						parent.setVisible(false);
					}
				}
			} else if (cancelButton.equals(source)) {
				parent.setVisible(false);
			} else if (circelBut.equals(source)) {
				mapParam.put("type", CIRCEL);
				changeScanView();
			} else if (rectBut.equals(source)) {
				mapParam.put("type", RECT);
				changeScanView();
			} else if (hTrigonBut.equals(source)) {
				mapParam.put("type", H_TRIGON);
				changeScanView();
			} else if (polygonBut.equals(source)) {
				mapParam.put("type", POLYGON);
				changeScanView();
			} else if (vTrigonBut.equals(source)) {
				mapParam.put("type", V_TRIGON);
				changeScanView();
			} else if (textComboBox.equals(source)) {
				if (mapParam.get("type") != null) {
					String type = mapParam.get("type").toString();
					if (null != type && !"".equals(type)) {
						changeScanView();
					}
				}
			}

		}
	}

	public String getImageSrc() {
		return imageSrc;
	}

	private javax.swing.JButton circelBut;
	private javax.swing.JButton rectBut;
	private javax.swing.JButton hTrigonBut;
	private javax.swing.JButton polygonBut;
	private javax.swing.JButton vTrigonBut;
	private javax.swing.JButton okButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JComboBox textComboBox;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel scanView;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private String imageSrc;
	@SuppressWarnings("rawtypes")
	private final Map mapParam = new HashMap();
	private final SvgTranscoderToPng transcoder = SvgTranscoderToPng.getInstance();
	private final String CIRCEL = "CIRCEL";
	private final String RECT = "RECT";
	private final String H_TRIGON = "H_TRIGON";
	private final String POLYGON = "POLYGON";
	private final String V_TRIGON = "V_TRIGON";
	private final EventActionListener eventActionListener = new EventActionListener(this);
}
