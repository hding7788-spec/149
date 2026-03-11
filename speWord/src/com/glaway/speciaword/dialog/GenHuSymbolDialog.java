package com.glaway.speciaword.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.glaway.speciaword.common.CommonHelper;
import com.glaway.speciaword.common.SvgTranscoderToPng;
import com.glaway.speciaword.util.SPJavaUtil;

/**
 *
 * @author MosesX
 */
public class GenHuSymbolDialog extends SpecDialog {

    public GenHuSymbolDialog(String category,String imageFolder) {
		super(category,imageFolder);
		initComponents();
	}

    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        genBut = new javax.swing.JButton();
        huBut = new javax.swing.JButton();
        value = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        okBut = new javax.swing.JButton();
        canelBut = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        scanView = new JLabel();
        
        setTitle("根号弧形");
        setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(
				(screenSize.width - this.getPreferredSize().width) / 2,
				(screenSize.height - this.getPreferredSize().height) / 2);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        genBut.setPreferredSize(new java.awt.Dimension(30, 30));
        genBut.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/gen.png")));
        huBut.setPreferredSize(new java.awt.Dimension(30, 30));
        huBut.setIcon(new ImageIcon(this.getClass().getResource(
				"/com/glaway/speciaword/resource/templates/" + getCategory() + "/hu.png")));

        value.setText("");
        value.setPreferredSize(new java.awt.Dimension(94, 30));
        jLabel1.setText("值");
        okBut.setText("确定");
        canelBut.setText("取消");
        
        value.getDocument().addDocumentListener(new EventDocumentListener());
        genBut.addActionListener(eventActionListener);
        huBut.addActionListener(eventActionListener);
        okBut.addActionListener(eventActionListener);
        canelBut.addActionListener(eventActionListener);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("预览"));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scanView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE));
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
            		jPanel2Layout
								.createSequentialGroup()
								.addComponent(scanView, javax.swing.GroupLayout.PREFERRED_SIZE, 120,
										javax.swing.GroupLayout.PREFERRED_SIZE).addGap(0, 1, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(genBut, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(huBut, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(value)))
                        .addGap(37, 37, 37)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(okBut)
                            .addComponent(canelBut))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(okBut)
                    .addComponent(genBut, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(huBut, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(value, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(canelBut))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        this.setModal(true);
		this.setVisible(true);
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
    
    private class EventActionListener implements ActionListener {
		private final GenHuSymbolDialog parent;

		public EventActionListener(GenHuSymbolDialog dialog) {
			parent = dialog;
		}

		@SuppressWarnings({ "unchecked" })
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (okBut.equals(source)) {
				if (mapParam.get("type") != null) {
					String type = mapParam.get("type").toString();
					if (null != type && !"".equals(type)) {
						
						imageSrc = CommonHelper.saveImageToLocal(
								transcoder.makeImage(getCategory(), type.toString(), mapParam),
								"" + System.currentTimeMillis(),getImageFolder());

						parent.setVisible(false);
					}
				}
			} else if (canelBut.equals(source)) {
				parent.setVisible(false);
			} else if (genBut.equals(source)) {
				mapParam.put("type", GEN);
				changeScanView();
			} else if (huBut.equals(source)) {
				mapParam.put("type", HU);
				changeScanView();
			}
		}
	}
    
    /**
	 * 更新预浏览视图
	 */
	@SuppressWarnings("unchecked")
	private void changeScanView() {
		String text = value.getText().trim();
		Object obj = mapParam.get("type");
		if (obj != null) {
			String type = obj.toString();
			Dimension size = CommonHelper.calculateStringToImageSize(text, 16);
			double strWidth = size.getWidth();
			mapParam.put("text_width", strWidth);
			text = SPJavaUtil.replaceAtSymbol(text);
			mapParam.put("text", text);
			scanView.setIcon(new ImageIcon(transcoder.makeImage(getCategory(), type.toString(), mapParam)));
		}
	}
    
    public String getImageSrc() {
		return imageSrc;
	}


    private final Map mapParam = new HashMap();
    private final SvgTranscoderToPng transcoder = SvgTranscoderToPng.getInstance();
	private javax.swing.JButton genBut;
    private javax.swing.JButton huBut;
    private javax.swing.JButton okBut;
    private javax.swing.JButton canelBut;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField value;
    private javax.swing.JLabel scanView;
    private String imageSrc = null;
    private String GEN = "gen";
    private String HU = "hu";
    private final EventActionListener eventActionListener = new EventActionListener(this);
}
