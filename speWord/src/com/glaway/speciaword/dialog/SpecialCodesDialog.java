/**
 * 
 */
package com.glaway.speciaword.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.glaway.speciaword.component.SpeciaWordComponent;

/**
 * @author MosesX
 * 
 */
public class SpecialCodesDialog extends javax.swing.JDialog {

	private static final long serialVersionUID = -1867452405484397417L;
	
	private SpeciaWordComponent spComponent;

	public SpecialCodesDialog(SpeciaWordComponent comp) {
		super();
		spComponent = comp;
		initComponents();
	}

	private void initComponents() {

		tabPanel = new javax.swing.JTabbedPane();
		okBut = new javax.swing.JButton();
		cancelBut = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setPreferredSize(new java.awt.Dimension(580, 300));
		setTitle("特殊字符");
		setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(
				(screenSize.width - this.getPreferredSize().width) / 2,
				(screenSize.height - this.getPreferredSize().height) / 2);

		initTab();

		okBut.setText("确定");
		okBut.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if (currSelectLabel != null) {
					specialCode = currSelectLabel.getText();
					spComponent.insertSpecialCode(specialCode);
				}
//				setVisible(false);
			}
		});

		cancelBut.setText("关闭");
		cancelBut.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setVisible(false);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addComponent(tabPanel,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										500,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(okBut)
												.addComponent(cancelBut))
								.addGap(0, 9, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(tabPanel)
				.addGroup(
						layout.createSequentialGroup()
								.addGap(92, 92, 92)
								.addComponent(okBut)
								.addGap(37, 37, 37)
								.addComponent(cancelBut)
								.addContainerGap(
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));

		pack();
		this.setModal(true);
		this.setVisible(true);
	}

	/**
	 * 根据配置文件初始化tab页面
	 */
	private void initTab() {

		try {
			InputStream in = SpecialCodesDialog.this
					.getClass()
					.getResourceAsStream(
							"/com/glaway/speciaword/resource/file/speciacodes.txt");

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					in, "UTF-8"));
			if (reader.ready()) {

				String strLine = reader.readLine();
				String temp = null;
				JLabel tempLabel = null;
				JPanel tempPanel = null;
				while (strLine != null) {
					String tabName = strLine.substring(0, strLine.indexOf("="));
					String smybol = strLine.substring(strLine.indexOf("=") + 1);

					tempPanel = new JPanel();
					tempPanel
							.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
					if (tabName.indexOf("希腊") != -1) {
						tabPanel.addTab("希腊", tempPanel);
					} else {
						tabPanel.addTab(tabName, tempPanel);
					}
					String[] xlArr = smybol.split("，");
					for (int i = 0; i < xlArr.length; i++) {
						temp = xlArr[i];
						tempLabel = new JLabel(temp);
						initLabelActions(tempLabel);
						tempLabel.setHorizontalAlignment(JLabel.CENTER);
						tempLabel.setBorder(BorderFactory.createLineBorder(
								Color.GRAY, 1));
						tempLabel.setPreferredSize(new Dimension(30, 30));
						tempPanel.add(tempLabel);
					}

					strLine = reader.readLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置label事件
	 * 
	 * @param label
	 */
	private void initLabelActions(final JLabel label) {
		label.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (label != currSelectLabel) {
					label.setBorder(BorderFactory.createLineBorder(Color.GRAY,
							1));
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				label.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (currSelectLabel != null) {
					currSelectLabel.setBorder(BorderFactory.createLineBorder(
							Color.GRAY, 1));
				}
				currSelectLabel = label;
				currSelectLabel.setBorder(BorderFactory.createLineBorder(
						Color.BLUE, 3));
			}
		});
	}

	public String getSpecialCode() {
		return specialCode;
	}

	private javax.swing.JButton okBut;
	private javax.swing.JButton cancelBut;
	private javax.swing.JTabbedPane tabPanel;
	private JLabel currSelectLabel;
	private String specialCode;
}
