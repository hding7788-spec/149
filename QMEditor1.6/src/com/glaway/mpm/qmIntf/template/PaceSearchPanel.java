package com.glaway.mpm.qmIntf.template;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TemplateIntf;

public class PaceSearchPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(PaceSearchPanel.class);
	private JButton sureButton;
	private JButton cancelButton;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private JPanel rightUpPanel;
	private JPanel mainPanel;
	private JScrollPane jScrollPane;
	public StepTree stTree;
	private String filePath;
	public static Vector<Object> vector;
	private JDialog dialog;

	public PaceSearchPanel(String filePath, JDialog dialog){
		this.dialog = dialog;
		vector = null;
		this.filePath = FileUtil.addSeperator(filePath);
		init();
	}
	private void init() {
		initComponents();
		initLayout();
		initActions();
		loadInitDatas();
	}
	private void initComponents() {
		mainPanel = new JPanel();
		leftPanel = new JPanel();
		jScrollPane = new JScrollPane();
		stTree = PaceTemplateUtil.getTemplates(filePath);
		stTree.updateUI();
		SwingUtil.expandAll(stTree);
		jScrollPane.setViewportView(stTree);
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
				vector = null;
				Object node = stTree.getLastSelectedPathComponent();
				if (node == null || node instanceof TpTreeNode) {
					SwingUtil.showMessageDialog("请选择工步模板", "提示", 2);
				} else {
					TpNode tpNode = (TpNode) node;
					String name = tpNode.getTemplate().getName();
					vector = new Vector<Object>();
					vector.add(name);
					logger.debug("vector= " + vector);
					dialog.dispose();
				}
			}
		});
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				vector = null;
				dialog.dispose();
			}
		});
	}

	private void loadInitDatas() {
		sureButton.setText("确定");
		cancelButton.setText("取消");
	}

}
