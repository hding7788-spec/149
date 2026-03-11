package com.glaway.mpm.qmIntf.template;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.glaway.mpm.model.TpType;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TemplateIntf;

public class TpSelectTypePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static VaLogger logger = VaLogger.getLogger(TpSelectTypePanel.class);

	public String useType;

	private JButton sureButton;
	private JButton cancelButton;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private JPanel rightUpPanel;
	private JPanel mainPanel;
	private JScrollPane jScrollPane;
	public static TpTree tpTree;
	public static List<String> list;
	private String type;
	private JDialog dialog;

	public TpSelectTypePanel(String type, JDialog dialog, String useType) {
		this.dialog = dialog;
		this.type = type;
		this.useType = useType;
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
		tpTree = new TpTree(new TpTreeNode("tpTree"));
		tpTree.setCellRenderer(new TpTypeRenderer());
		if ("工序".equals(useType)) {
			tpTree = TpTreeXmlUtil.addTpToTpTree(tpTree,
					getStepTemplates(type), false,
					useType);
		} else {
			tpTree = TpTreeXmlUtil.addTpToTpTree(tpTree, getStepTemplates(type), false, useType);
		}
		tpTree.updateUI();
		SwingUtil.expandAll(tpTree);
		jScrollPane.setViewportView(tpTree);
		rightPanel = new JPanel();
		rightUpPanel = new JPanel();

		sureButton = new JButton();
		cancelButton = new JButton();
	}

	private TpType getStepTemplates(String type2) {
		String typeFolderName = null;// propertiesUtil.getProperty(type);
		for (int i = 0; i < NewTechnicsPart.technicsTypes[0].length; i++) {
			System.out.println("=="+NewTechnicsPart.technicsTypes[0][i]);

			if (NewTechnicsPart.technicsTypes[0][i].equals(type2)) {

				typeFolderName = NewTechnicsPart.technicsTypes[1][i];
				break;
			}
		}
		System.out.println(type2);
		System.out.println(typeFolderName);
		TpType tpType = new TpType(typeFolderName);
		return tpType;
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

	private void generatorPath(TpTreeNode tpTreeNode, List<String> list) {
		if (tpTreeNode != null && !tpTreeNode.isRoot()
				&& !((TpTreeNode) tpTreeNode.getParent()).isRoot()) {
			list.add(tpTreeNode.getName());
			generatorPath((TpTreeNode) tpTreeNode.getParent(), list);
		}
	}

	private void initActions() {
		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				list = null;
				Object node = tpTree.getLastSelectedPathComponent();
				if (node != null && node instanceof TpTreeNode) {
					TpTreeNode tpTreeNode = (TpTreeNode) node;
					if (!tpTreeNode.children().hasMoreElements()) {
						list = new ArrayList<String>();
						generatorPath(tpTreeNode, list);
						dialog.dispose();
					} else {
						SwingUtil.showMessageDialog("请选择" + useType + "模板类型",
								"提示", 2);
					}
				} else {
					SwingUtil.showMessageDialog("请选择" + useType + "模板类型", "提示",
							2);
				}
			}

		});
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				list = null;
				dialog.dispose();
			}
		});
	}

	private void loadInitDatas() {
		sureButton.setText("确定");
		cancelButton.setText("取消");
	}

}