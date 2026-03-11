package com.glaway.mpm.qmIntf.template;

import com.glaway.mpm.util.ApacheZipUtil;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TemplateIntf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StepTemplateMaintainPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private VaLogger logger = VaLogger.getLogger(this.getClass());

	private JButton modifyButton;
	private JButton deleteButton;
	private JButton addToLibraryButton;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private JPanel rightUpPanel;
	private JScrollPane jScrollPane;
	public StepTree stTree;
	private String filePath;
	private JDialog parentDialog;

	public StepTemplateMaintainPanel(String filePath, JDialog parentDialog) {
		this.filePath = FileUtil.addSeperator(filePath);
		this.parentDialog = parentDialog;
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

		TpTreeNode rootNode = StepTemplateUtil
				.getLocalTemplates(filePath, null);
		stTree = new StepTree(rootNode);
		// tpTree.updateUI();
		SwingUtil.expandAll(stTree);
		jScrollPane.setViewportView(stTree);
		rightPanel = new JPanel();
		rightUpPanel = new JPanel();

		modifyButton = new JButton();
		deleteButton = new JButton();
		addToLibraryButton = new JButton();
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
	}

	private void deleteNode() {
		Object node = stTree.getLastSelectedPathComponent();
		if (node == null || node instanceof TpTreeNode) {
			SwingUtil.showMessageDialog("请选择需要移除的工序模板", "提示", 2);
		} else {
			TpNode tpNode = (TpNode) node;
			String templateName = tpNode.getTemplate().getName();
			if (SwingUtil.showConfirmDialog("确认移除工序模板“" + templateName + "”？",
					"提示", 2) == 0) {
				String type = ((TpTreeNode) tpNode.getParent()).getName();
				File file = new File(filePath + typeSwitch(type)
						+ File.separator + templateName);
				logger.debug("file= " + file);
				if (!FileUtil.deleteFile(file)) {
					SwingUtil.showMessageDialog("工序模板移除失败", "提示", 2);
					return;
				}
				tpNode.removeFromParent();
				stTree.updateUI();
				SwingUtil.expandAll(stTree);
				stTree.setSelectionPath(null);
				SwingUtil.showMessageDialog("工序模板“" + templateName + "”移除成功",
						"提示", 2);
			}

		}
	}

	private void modifyNode() {
		Object node = stTree.getLastSelectedPathComponent();
		if (node == null || node instanceof TpTreeNode) {
			SwingUtil.showMessageDialog("请选择需要修改的工序模板", "提示", 2);
		} else {
			TpNode tpNode = (TpNode) node;
			String beforeName = tpNode.getTemplate().getName();
			TpNodeModifyDialog dialog = new TpNodeModifyDialog(beforeName,
					parentDialog);
			String returnValue = dialog.showDialog();
			if (returnValue != null) {
				try {
					if (beforeName.equals(returnValue)) {
						SwingUtil.showMessageDialog("修改的工序模板名与原工序模板名相同", "提示",
								1);
					} else {
						String type = ((TpTreeNode) tpNode.getParent())
								.getName();
						type = typeSwitch(type);
						File fileDir = new File(filePath + type);
						for (File temp : fileDir.listFiles()) {
							if (temp.getName().equals(returnValue)) {
								SwingUtil.showMessageDialog("工序模板“"
										+ returnValue + "”已存在", "提示", 1);
								return;
							} else {
								continue;
							}
						}

						File file = new File(filePath + type + File.separator
								+ beforeName);

						file.renameTo(new File(filePath + type + File.separator
								+ returnValue));

						File xmlFile = new File(filePath + type
								+ File.separator + returnValue + File.separator
								+ beforeName + ".xml");
						if (xmlFile.exists()) {
							xmlFile.renameTo(new File(filePath + type
									+ File.separator + returnValue
									+ File.separator + returnValue + ".xml"));
						}
						tpNode.getTemplate().setName(returnValue);
						stTree.updateUI();
						SwingUtil.expandAll(stTree);
						SwingUtil.showMessageDialog("工序模板名“" + beforeName
								+ "”修改为“" + returnValue + "”", "提示", 1);
					}
				} catch (Exception e) {
					SwingUtil.showMessageDialog("工序模板修改失败", "提示", 2);
				}
			}
		}

	}

	public void addToLibrary() {
		Object node = stTree.getLastSelectedPathComponent();
		if (node == null || node instanceof TpTreeNode) {
			SwingUtil.showMessageDialog("请选择需要入库的工序模板", "提示", 2);
		} else {
			TpNode tpNode = (TpNode) node;
			String name = ((TpTreeNode) tpNode.getParent()).getName();
			//TpSelectTypeDialog dialog = new TpSelectTypeDialog(typeSwitch(name), "工序", parentDialog);
			//List<String> returnValue = dialog.showDialog();
			//if (returnValue != null) {
				String tempalteName = tpNode.getTemplate().getName();
				//logger.debug("typeName:" + returnValue + " tempalteName:" + tempalteName);
				List<String> returnValue  = new ArrayList<String>();
				returnValue.add(name);
				String result = TemplateIntf.uploadStepTemplate(returnValue,
						tempalteName + ".zip", getTemplateByte(typeSwitch(name), tempalteName));
				logger.debug("result=========" + result);
				if ("".equals(result)) {
					SwingUtil.showMessageDialog("工序模板“" + tempalteName + "”入库流程启动成功，请到主页查看！", "提示", 1);
				} else {
					SwingUtil.showMessageDialog(result, "提示", 2);
				}
			//}
		}
	}

	public byte[] getTemplateByte(String type, String name) {
		byte[] bytes = null;
		File fileDirectory = new File(filePath);
		if (fileDirectory.isDirectory()) {
			for (File file : fileDirectory.listFiles()) {
				if (file.isDirectory() && type.equals(file.getName())) {
					for (File subFile : file.listFiles()) {
						if (name.equals(subFile.getName())) {
							String path = subFile.getPath() + ".zip";
							ApacheZipUtil.compress(subFile, path);
							bytes = FileUtil.readFilePathToByte(path);
							return bytes;
						}
					}
				}
			}
		} else {
			try {
				throw new Exception("本地工序模板路径不正确");
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