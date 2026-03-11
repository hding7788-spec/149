package com.glaway.mpm.qmIntf.template;

import com.glaway.mpm.qmIntf.technics.TechnicsPreview;
import com.glaway.mpm.util.*;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TemplateIntf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TemplateMaintainPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private VaLogger logger = VaLogger.getLogger(this.getClass());
	private String[][] technicsTypes = LoadConfig.getInstance().getTechnicsType();

	private JButton modifyButton;
	private JButton deleteButton;
	private JButton addToLibraryButton;
	private JButton previewButton;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private JPanel rightUpPanel;
	private JScrollPane jScrollPane;
	public static TpTree tpTree;
	private String filePath;
	private JDialog parentDialog;

	public TemplateMaintainPanel(String filePath, JDialog parentDialog) {
		this.parentDialog = parentDialog;
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
		tpTree = TpTreeXmlUtil.xmlToTpTree(filePath, null);
		tpTree.updateUI();
		SwingUtil.expandAll(tpTree);
		jScrollPane.setViewportView(tpTree);
		rightPanel = new JPanel();
		rightUpPanel = new JPanel();

		modifyButton = new JButton();
		deleteButton = new JButton();
		addToLibraryButton = new JButton();
		previewButton = new JButton();
	}

	private void initLayout() {
		jScrollPane.getViewport().setBackground(Color.WHITE);
		jScrollPane.setPreferredSize(new Dimension(300, 420));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(20, 0, 10, 0);
		c.gridx = 1;
		c.gridy = 0;
		rightUpPanel.setLayout(new GridBagLayout());
		c.insets = new Insets(10, 0, 10, 0);
		c.gridy = 1;
		rightUpPanel.add(deleteButton, c);
		c.gridy = 2;
		rightUpPanel.add(modifyButton, c);
		c.gridy = 3;
		rightUpPanel.add(addToLibraryButton, c);
		c.gridy = 4;
		rightUpPanel.add(previewButton, c);
		rightPanel.add(rightUpPanel);

		this.setLayout(new BorderLayout());
		rightPanel.setPreferredSize(new Dimension(100, 700));
		rightPanel.add(rightUpPanel);
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(jScrollPane, BorderLayout.CENTER);
		this.add(leftPanel, BorderLayout.CENTER);
		this.add(rightPanel, BorderLayout.EAST);
	}

	private void initActions() {
		modifyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				modifyNode();

			}
		});
		deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				deleteNode();
			}
		});
		addToLibraryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addToLibrary();
			}
		});
		previewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				preview();
			}
		});
	}

	private void deleteNode() {
		Object node = tpTree.getLastSelectedPathComponent();
		if (node == null || node instanceof TpTreeNode) {
			SwingUtil.showMessageDialog("请选择需要移除的工艺模板", "提示", 2);
		} else {
			TpNode tpNode = (TpNode) node;
			String templateName = tpNode.getTemplate().getName();

			if (SwingUtil.showConfirmDialog("确认移除工艺模板“" + templateName + "”？",
					"提示", 2) == 0) {
				String type = ((TpTreeNode) tpNode.getParent()).getName();
				type = typeSwitch(type);
				File file = new File(filePath + type + File.separator
						+ templateName);
				if (!FileUtil.deleteFile(file)) {
					SwingUtil.showMessageDialog("工艺模板移除失败", "提示", 2);
					return;
				}
				tpNode.removeFromParent();
				tpTree.updateUI();
				SwingUtil.expandAll(tpTree);
				tpTree.setSelectionPath(null);
			}

		}
	}

	private void modifyNode() {
		Object node = tpTree.getLastSelectedPathComponent();
		if (node == null || node instanceof TpTreeNode) {
			SwingUtil.showMessageDialog("请选择需要修改的工艺模板", "提示", 2);
		} else {
			TpNode tpNode = (TpNode) node;
			String beforeName = tpNode.getTemplate().getName();
			TpNodeModifyDialog dialog = new TpNodeModifyDialog(beforeName,
					parentDialog);
			String returnValue = dialog.showDialog();
			if (returnValue != null) {
				try {
					if (beforeName.equals(returnValue)) {
						SwingUtil.showMessageDialog("修改的工艺模板名与原工艺模板名相同", "提示",
								1);
					} else {
						String type = ((TpTreeNode) tpNode.getParent())
								.getName();
						type = typeSwitch(type);
						File fileDir = new File(filePath + type);
						for (File temp : fileDir.listFiles()) {
							if (temp.getName().equals(returnValue)) {
								SwingUtil.showMessageDialog("工艺模板“"
										+ returnValue + "”已存在", "提示", 1);
								return;
							} else {
								continue;
							}
						}

						File file = new File(filePath + type + File.separator
								+ beforeName);
						for (File temp : file.listFiles()) {
							if ((beforeName + ".xml").equals(temp.getName())) {
								temp.renameTo(new File(filePath + type
										+ File.separator + beforeName
										+ File.separator + returnValue + ".xml"));
								break;
							}
						}

						file.renameTo(new File(filePath + type + File.separator
								+ returnValue));
						tpNode.getTemplate().setName(returnValue);
						tpTree.updateUI();
						SwingUtil.expandAll(tpTree);
						SwingUtil.showMessageDialog("工艺模板名“" + beforeName
								+ "”修改为“" + returnValue + "”", "提示", 1);
					}
				} catch (Exception e) {
					SwingUtil.showMessageDialog("工艺模板修改失败", "提示", 2);
				}
			}
		}

	}

	public void addToLibrary() {
		Object node = tpTree.getLastSelectedPathComponent();
		if (node == null || node instanceof TpTreeNode) {
			SwingUtil.showMessageDialog("请选择需要入库的工艺模板", "提示", 2);
		} else {
			TpNode tpNode = (TpNode) node;
			String name = ((TpTreeNode) tpNode.getParent()).getName();
			//TpSelectTypeDialog dialog = new TpSelectTypeDialog(typeSwitch(name), "工艺", parentDialog);
			//List<String> returnValue = dialog.showDialog();
			List<String> returnValue  = new ArrayList<String>();
			returnValue.add(name);
			if (returnValue != null) {
				String tempalteName = tpNode.getTemplate().getName();
				logger.debug("typeName:" + returnValue + " tempalteName:" + tempalteName);
				String result = TemplateIntf.uploadProcessTemplate(returnValue,
						tempalteName, getTemplateByte(typeSwitch(name), tempalteName));
				logger.debug("result=========" + result);
				if ("".equals(result)) {
					SwingUtil.showMessageDialog("工艺模板“" + tempalteName + "”入库成功", "提示", 1);
				} else {
					SwingUtil.showMessageDialog(result, "提示", 2);
				}
			}
		}
	}

	public void preview() {
		try {
			Object node = tpTree.getLastSelectedPathComponent();
			if (node == null || node instanceof TpTreeNode) {
				SwingUtil.showMessageDialog("请选择工艺模板", "提示", 2);
			} else {
				TpNode tpNode = (TpNode) node;
				TpTreeNode treeNode = (TpTreeNode) tpNode.getParent();
				String templetDirectory = "";
				String templateType = treeNode.getName();
				for(int i=0;i<technicsTypes[1].length;i++){
					if(technicsTypes[1][i].equals(templateType)){
						templetDirectory = WorkSpaceUtil.getTempletPath(technicsTypes[0][i]);
					}
				}
				if(!"".equals(templetDirectory)){
					String name = tpNode.getTemplate().getName();
					File file = new File(templetDirectory + File.separator + name);
					if(file.exists() && file.isDirectory()){
						TechnicsPreview.preview(templetDirectory + File.separator + name);
					}
				}
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(TemplateMaintainPanel.this, "预览工艺出现错误！", "提示", 1);
			e1.printStackTrace();
		}
	}

	public byte[] getTemplateByte(String type, String name) {
		byte[] bytes = null;
		File fileDirectory = new File(filePath);
		if (fileDirectory.isDirectory()) {
			for (File file : fileDirectory.listFiles()) {
				if (file.isDirectory() && type.equals(file.getName())) {
					for (File subFile : file.listFiles()) {
						if (subFile.isDirectory()
								&& name.equals(subFile.getName())) {
							String path = subFile.getPath() + ".zip";
							logger.debug("path============" + path);
							ApacheZipUtil.compress(subFile, path);
							bytes = FileUtil.readFilePathToByte(path);
							return bytes;
						}
					}
				}
			}
		} else {
			try {
				throw new Exception("本地工艺模板路径不正确");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}

	private void loadInitDatas() {
		modifyButton.setText("修改");
		deleteButton.setText("移除");
		addToLibraryButton.setText("入库");
		previewButton.setText("预览");
		if(NewTechnicsPart.isTemplateCapp){
			addToLibraryButton.setEnabled(true);
		}else{
			addToLibraryButton.setEnabled(true);
		}

	}

	private String typeSwitch(String type) {
//		if ("装配工艺".equals(type)) {
//			type = "assembleTemplate";
//		} else if ("零件工艺".equals(type)) {
//			type = "partTemplate";
//		} else {
//			logger.error("工艺类型出错");
//		}
		String[][] technicsTypes = LoadConfig.getInstance().getTechnicsType();
		boolean flag = false;
		for(int i=0;i<technicsTypes[1].length;i++){
			if(technicsTypes[1][i].equals(type)){
				type = technicsTypes[0][i];
				flag = true;
				break;
			}
		}
		if(!flag){
			logger.error("工艺类型出错");
		}
		return type;
	}
}
