package com.glaway.mpm.qmIntf.template;

import com.glaway.mpm.qmIntf.technics.TechnicsPreview;
import com.glaway.mpm.util.*;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TemplateIntf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

public class TemplateSearchPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static VaLogger logger = VaLogger.getLogger(TemplateSearchPanel.class);
	private String[][] technicsTypes = LoadConfig.getInstance().getTechnicsType();

	private JButton sureButton;
	private JButton cancelButton;
	private JButton previewButton;
	private JPanel leftPanel;
	private JScrollPane jScrollPane;
	private JPanel rightPanel;
	private JPanel rightUpPanel;
	public TpTree tpTree;
	private String filePath;
	public static Vector<Object> vector;
	private String type;
	private JDialog dialog;

	public TemplateSearchPanel(String filePath, String type, JDialog dialog) {
		this.dialog = dialog;
		this.type = type;
		vector = null;
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
		leftPanel = new JPanel();
		jScrollPane = new JScrollPane();
		rightPanel = new JPanel();
		rightUpPanel = new JPanel();
		tpTree = TpTreeXmlUtil.searchTemplates(filePath, type);
		tpTree.updateUI();
		SwingUtil.expandAll(tpTree);
		jScrollPane.setViewportView(tpTree);

		sureButton = new JButton();
		cancelButton = new JButton();
		previewButton = new JButton();
	}

	private void initLayout() {
		jScrollPane.getViewport().setBackground(Color.WHITE);
		jScrollPane.setPreferredSize(new Dimension(520, 700));

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
		c.gridy = 2;
		rightUpPanel.add(previewButton, c);

		this.setLayout(new BorderLayout());
		rightPanel.setPreferredSize(new Dimension(100, 700));
		rightPanel.add(rightUpPanel);
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(jScrollPane, BorderLayout.CENTER);
		this.add(leftPanel, BorderLayout.CENTER);
		this.add(rightPanel, BorderLayout.EAST);
	}

	private void initActions() {
		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vector = null;
				Object node = tpTree.getLastSelectedPathComponent();
				if (node == null || node instanceof TpTreeNode) {
					SwingUtil.showMessageDialog("请选择工艺模板", "提示", 2);
				} else {
					TpNode tpNode = (TpNode) node;
					String name = tpNode.getTemplate().getName();
					TpTreeNode treeNode = (TpTreeNode) tpNode.getParent();
					String type = treeNode.getName();
					vector = new Vector<Object>();
					TpTreeNode parentNode = (TpTreeNode) treeNode.getParent();
					logger.debug(parentNode.getName());
					if (parentNode.getName().equals("个人工艺模板库")) {
						vector.add(type+"@@"+name);
					} else {
						vector = TemplateIntf.downloadProcessTemplate(tpNode.getTemplate().getOid());
					}
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
		previewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Object node = tpTree.getLastSelectedPathComponent();
					if (node == null || node instanceof TpTreeNode) {
						SwingUtil.showMessageDialog("请选择工艺模板", "提示", 2);
					} else {
						TpNode tpNode = (TpNode) node;
						TpTreeNode treeNode = (TpTreeNode) tpNode.getParent();
						TpTreeNode parentNode = (TpTreeNode) treeNode.getParent();
						if (parentNode.getName().equals("个人工艺模板库")) {
							String name = tpNode.getTemplate().getName();
							String templetDirectory = WorkSpaceUtil.getTempletPath(type);
							File file = new File(templetDirectory + File.separator + name);
							if(file.exists() && file.isDirectory()){
								TechnicsPreview.preview(templetDirectory + File.separator + name);
							}
						} else if(parentNode.getName().equals("公共工艺模板库")) {
							Vector<Object> templates = TemplateIntf.downloadProcessTemplate(tpNode.getTemplate().getOid());
							if(templates != null && templates.size() == 2){
								String templateName = (String) templates.get(0);
								if (templateName.toLowerCase().endsWith(".xml")) {
									templateName = templateName.substring(0, templateName.length() - 4);
								}
								byte[] data = (byte[]) templates.get(1);
								String templetDirectory = WorkSpaceUtil.getTempRootPath() + File.separator + templateName;
								TechnicsReleaseUtil.unZip(data, templetDirectory);
								File file = new File(templetDirectory);
								if(file.exists() && file.isDirectory()){
									TechnicsPreview.preview(templetDirectory);
								}
							}
						}
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(TemplateSearchPanel.this, "预览工艺出现错误！", "提示", 1);
					e1.printStackTrace();
				}
			}
		});
	}

	private void loadInitDatas() {
		sureButton.setText("确定");
		cancelButton.setText("取消");
		previewButton.setText("预览");
	}

}