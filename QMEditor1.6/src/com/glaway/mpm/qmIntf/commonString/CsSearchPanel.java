package com.glaway.mpm.qmIntf.commonString;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.visual.log.VaLogger;

class CsSearchPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private VaLogger logger = VaLogger.getLogger(this.getClass());

	private JButton sureButton;
	private JButton cancelButton;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private JPanel rightUpPanel;
	private JPanel mainPanel;
	private JScrollPane jScrollPane;
	public static CsTree csTree;
	private String filePath;
	public static String returnValue;
	private JDialog dialog;

	public CsSearchPanel(String filePath, JDialog dialog) {
		this.dialog = dialog;
		returnValue = null;
		this.filePath = FileUtil.addSeperator(filePath);
		init();
	}

	private void init() {
		initLookAndFeel();
		initDimension();
		initComponents();
		initLayout();
		initActions();
		loadInitDatas();
	}

	private void initLookAndFeel() {

	}

	private void initDimension() {

	}

	private void initComponents() {
		mainPanel = new JPanel();
		leftPanel = new JPanel();
		jScrollPane = new JScrollPane();
		csTree = CsTreeXmlUtil.justSearchCommonStrings(filePath, false);
		csTree.updateUI();
		SwingUtil.expandAll(csTree);
		jScrollPane.setViewportView(csTree);
		rightPanel = new JPanel();
		rightUpPanel = new JPanel();

		sureButton = new JButton();
		cancelButton = new JButton();
	}

	private void initLayout() {
		setSize(400, 500);
		jScrollPane.getViewport().setBackground(Color.WHITE);
		jScrollPane.setPreferredSize(new Dimension(300, 420));
		leftPanel.add(jScrollPane);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(20, 0, 10, 0);
		c.gridx = 1;
		c.gridy = 0;
		rightUpPanel.setLayout(new GridBagLayout());
		rightUpPanel.add(sureButton, c);
		c.insets = new Insets(10, 0, 10, 0);
		c.gridy = 1;
		rightUpPanel.add(cancelButton, c);

		rightPanel.add(rightUpPanel);
		mainPanel.setLayout(new BorderLayout(1, 2));
		mainPanel.add(leftPanel, BorderLayout.WEST);
		mainPanel.add(rightPanel, BorderLayout.CENTER);
		add(mainPanel);
	}

	private void initActions() {
		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				returnValue = null;
				Object node = csTree.getLastSelectedPathComponent();
				if (node == null || node instanceof CsTreeNode) {
					SwingUtil.showMessageDialog("请选择常用语", "提示", 2);
				} else {
					CsNode csNode = (CsNode) node;
					returnValue = csNode.getValue();
					dialog.dispose();
					logger.debug("返回常用语====================" + returnValue);
				}
			}
		});
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				returnValue = null;
				dialog.dispose();
			}
		});
	}

	private void loadInitDatas() {
		sureButton.setText("确定");
		cancelButton.setText("取消");
	}

}