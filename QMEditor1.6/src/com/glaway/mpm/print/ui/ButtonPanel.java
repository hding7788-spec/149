package com.glaway.mpm.print.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.listener.PrintFileButtonListener;
import com.glaway.mpm.print.listener.ScanInputKeyListener;

public class ButtonPanel extends JPanel {

	private static final long serialVersionUID = 6574815648937337937L;

	private JCheckBox selectAll;
	private JButton addButton;
	private JButton deleteButton;
	private JButton sureButton;
	private JButton cancelButton;
	private JButton lookUpButton;
	private JButton viewButton;
	private JButton printButton;
	private JButton useButton;
	private JButton searchButton;
	private JButton addOnBomButton;
	private JButton setDeptButton;
	private JButton setSealButton;
	private JButton setNotPrinted;
	private JButton setToPrinted;
	private JButton addOnProcessDirectory;
	private JButton distribution;
	private JButton modifyButton;
	private JButton addChangeButton;
	private JButton importButton;
	private JPanel tablePanel;
	private String type;
	private String category;

	private JButton allSetDeptButton;
	private JButton allSetSealButton;
	private JButton allDeleteDeptButton;
	private JButton allDeleteSealButton;
	private JButton allSetButton;

	public ButtonPanel(JPanel tablePanel, String type, String category) {
		this.tablePanel = tablePanel;
		this.type = type;
		this.category = category;
		initComponents();
		initLayout();
		initListener();
		initUI();
	}

	private void initComponents() {
		selectAll = new JCheckBox("全选");
		addButton = new JButton("添加");
		deleteButton = new JButton("删除");
		sureButton = new JButton("确认");
		cancelButton = new JButton("关闭");
		lookUpButton = new JButton("查看");
		viewButton = new JButton("预览");
		printButton = new JButton("打印");
		useButton = new JButton("应用");
		addOnBomButton = new JButton("基于BOM添加");
		addOnProcessDirectory = new JButton("基于工艺目录添加");
		setDeptButton = new JButton("设置");
		setSealButton = new JButton("设置");

		searchButton = new JButton("查询");
		setNotPrinted = new JButton("设置未打印");
		setToPrinted = new JButton("设置已打印");

		distribution = new JButton("分发");
		modifyButton = new JButton("修改");
		addChangeButton = new JButton("添加更改");
		importButton = new JButton("导入");

		allSetDeptButton = new JButton("批量添加分发部门");
		allSetSealButton = new JButton("批量添加印章");
		allDeleteSealButton = new JButton("批量清除印章");
		allDeleteDeptButton = new JButton("批量清除分发部门");
		allSetButton = new JButton("批量添加");


		addButton.setPreferredSize(new Dimension(80, 25));
		deleteButton.setPreferredSize(new Dimension(80, 25));
		sureButton.setPreferredSize(new Dimension(80, 25));
		cancelButton.setPreferredSize(new Dimension(80, 25));
		lookUpButton.setPreferredSize(new Dimension(80, 25));
		viewButton.setPreferredSize(new Dimension(80, 25));
		printButton.setPreferredSize(new Dimension(80, 25));
		useButton.setPreferredSize(new Dimension(80, 25));
		searchButton.setPreferredSize(new Dimension(80, 25));
		setDeptButton.setPreferredSize(new Dimension(80, 25));
		setSealButton.setPreferredSize(new Dimension(80, 25));
		setNotPrinted.setPreferredSize(new Dimension(80, 25));
		setToPrinted.setPreferredSize(new Dimension(80, 25));
		addOnProcessDirectory.setPreferredSize(new Dimension(130, 25));
		distribution.setPreferredSize(new Dimension(80, 25));
		modifyButton.setPreferredSize(new Dimension(80, 25));
		addChangeButton.setPreferredSize(new Dimension(80, 25));
		importButton.setPreferredSize(new Dimension(80, 25));

		allSetDeptButton.setPreferredSize(new Dimension(130, 25));
		allSetSealButton.setPreferredSize(new Dimension(130, 25));
		allDeleteDeptButton.setPreferredSize(new Dimension(130, 25));
		allDeleteSealButton.setPreferredSize(new Dimension(130, 25));
		allSetButton.setPreferredSize(new Dimension(90, 25));
	}

	private void initLayout() {
		JPanel leftPanel = new JPanel();
		JPanel rightPanel = new JPanel();

		if (type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST)) {
			leftPanel.add(selectAll);
			leftPanel.add(allSetDeptButton);
			leftPanel.add(allSetSealButton);
			leftPanel.add(allDeleteDeptButton);
			leftPanel.add(allDeleteSealButton);


			rightPanel.add(addButton);
			if("".equals(category) || category == null){
				rightPanel.add(addOnBomButton);
				rightPanel.add(addOnProcessDirectory);
			}
			rightPanel.add(deleteButton);
			rightPanel.add(sureButton);
			rightPanel.add(cancelButton);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_ADDREQUEST)){
			leftPanel.add(selectAll);
			if("".equals(category) || category == null){
				rightPanel.add(lookUpButton);
			}
			rightPanel.add(sureButton);
			rightPanel.add(cancelButton);
		} else if (type.equals(PrintConstants.TITLE_DIALOG_DEPT)) {
			rightPanel.add(setDeptButton);
		} else if (type.equals(PrintConstants.TITLE_MAINPANEL_ZXDYSQ)){
			leftPanel.add(selectAll);
			if(!"WL".equals(category) && !"ZZ".equals(category)){
				rightPanel.add(viewButton);
				rightPanel.add(printButton);
			}
			rightPanel.add(cancelButton);
		}else if (type.equals(PrintConstants.TITLE_MAINPANEL_YLDYSQ)){
			leftPanel.add(selectAll);
			if(category == null || "".equals(category)){
				rightPanel.add(lookUpButton);
			}
			rightPanel.add(cancelButton);
		}
		else if (type.equals(PrintConstants.TITLE_DIALOG_SEAL)) {
			rightPanel.add(setSealButton);
			leftPanel.add(selectAll);
		} else if (type.equals(PrintConstants.TITLE_ADDFILE)) {
			leftPanel.add(selectAll);
			rightPanel.add(useButton);
			rightPanel.add(lookUpButton);
			rightPanel.add(sureButton);
			rightPanel.add(cancelButton);
		} else if (type.equals(PrintConstants.TITLE_ADDONBOM_PART_RELATE)) {
			leftPanel.add(selectAll);
			rightPanel.add(lookUpButton);
			rightPanel.add(sureButton);
			rightPanel.add(cancelButton);
		} if (type.equals(PrintConstants.TITLE_MAINPANEL_LQZZWJ)) {
			leftPanel.add(selectAll);
			sureButton.setText("过滤");
			rightPanel.add(sureButton);
			rightPanel.add(distribution);
			rightPanel.add(cancelButton);
		} else if (type.equals(PrintConstants.TITLE_MAINPANEL_JGYZGL)) {
			leftPanel.add(selectAll);
			rightPanel.add(sureButton);
			rightPanel.add(deleteButton);
			rightPanel.add(cancelButton);
		} else if (type.equals(PrintConstants.TITLE_MAINPANEL_JGYZGL + "add")){
			leftPanel.add(selectAll);
			rightPanel.add(addButton);
		} else if (type.equals(PrintConstants.TITLE_MAINPANEL_JGYZQR)){
			if("JC".equals(category)){
				leftPanel.add(selectAll);
			}
			rightPanel.add(sureButton);
			rightPanel.add(cancelButton);
		} else if (type.equals(PrintConstants.TITLE_ADDONPROCESSDIRECTORY)){
			rightPanel.add(sureButton);
			rightPanel.add(cancelButton);
		} else if (type.equals(PrintConstants.TITLE_MAINPANEL_WLWJLR)){
			leftPanel.add(selectAll);
			rightPanel.add(importButton);
			rightPanel.add(addButton);
			rightPanel.add(addChangeButton);
			rightPanel.add(modifyButton);
			rightPanel.add(deleteButton);
			rightPanel.add(cancelButton);
		} else if (type.equals(PrintConstants.TITLE_MAINPANEL_WLWJTJ)){
			rightPanel.add(sureButton);
			rightPanel.add(cancelButton);
		} else if (type.equals(PrintConstants.TITLE_ADDFILECHANGEINFO)){
			rightPanel.add(sureButton);
			rightPanel.add(cancelButton);
		} else if (type.equals(PrintConstants.TITLE_MAINPANEL_WJBDSQ)){
			leftPanel.add(selectAll);
			rightPanel.add(addButton);
			rightPanel.add(addOnProcessDirectory);
			rightPanel.add(deleteButton);
			rightPanel.add(sureButton);
			rightPanel.add(cancelButton);
		}else if (type.equals(PrintConstants.TITLE_MAINPANEL_ADDWJBDSQ)){
			leftPanel.add(selectAll);
			rightPanel.add(sureButton);
			rightPanel.add(cancelButton);
		}else if (type.equals(PrintConstants.TITLE_ALLDIALOG_DEPT)||type.equals(PrintConstants.TITLE_ALLDIALOG_SEAL)) {
			rightPanel.add(allSetButton);
		}

		setLayout(new BorderLayout());
		add(leftPanel, BorderLayout.WEST);
		add(rightPanel, BorderLayout.EAST);
	}

	private void initListener() {
		selectAll.addActionListener(new PrintFileButtonListener(this));
		selectAll.addKeyListener(new ScanInputKeyListener());
		addButton.addActionListener(new PrintFileButtonListener(this));
		addButton.addKeyListener(new ScanInputKeyListener());
		addOnBomButton.addActionListener(new PrintFileButtonListener(this));
		addOnBomButton.addKeyListener(new ScanInputKeyListener());
		deleteButton.addActionListener(new PrintFileButtonListener(this));
		deleteButton.addKeyListener(new ScanInputKeyListener());
		sureButton.addActionListener(new PrintFileButtonListener(this));
		sureButton.addKeyListener(new ScanInputKeyListener());
		cancelButton.addActionListener(new PrintFileButtonListener(this));
		cancelButton.addKeyListener(new ScanInputKeyListener());
		printButton.addActionListener(new PrintFileButtonListener(this));
		printButton.addKeyListener(new ScanInputKeyListener());
		viewButton.addActionListener(new PrintFileButtonListener(this));
		viewButton.addKeyListener(new ScanInputKeyListener());
		lookUpButton.addActionListener(new PrintFileButtonListener(this));
		lookUpButton.addKeyListener(new ScanInputKeyListener());
		useButton.addActionListener(new PrintFileButtonListener(this));
		useButton.addKeyListener(new ScanInputKeyListener());
		searchButton.addActionListener(new PrintFileButtonListener(this));
		searchButton.addKeyListener(new ScanInputKeyListener());
		setSealButton.addActionListener(new PrintFileButtonListener(this));
		setSealButton.addKeyListener(new ScanInputKeyListener());
		setDeptButton.addActionListener(new PrintFileButtonListener(this));
		setDeptButton.addKeyListener(new ScanInputKeyListener());
		setNotPrinted.addActionListener(new PrintFileButtonListener(this));
		setNotPrinted.addKeyListener(new ScanInputKeyListener());
		setToPrinted.addActionListener(new PrintFileButtonListener(this));
		setToPrinted.addKeyListener(new ScanInputKeyListener());
		addOnProcessDirectory.addActionListener(new PrintFileButtonListener(this));
		addOnProcessDirectory.addKeyListener(new ScanInputKeyListener());
		distribution.addActionListener(new PrintFileButtonListener(this));
		distribution.addKeyListener(new ScanInputKeyListener());
		modifyButton.addActionListener(new PrintFileButtonListener(this));
		modifyButton.addKeyListener(new ScanInputKeyListener());
		addChangeButton.addActionListener(new PrintFileButtonListener(this));
		addChangeButton.addKeyListener(new ScanInputKeyListener());
		importButton.addActionListener(new PrintFileButtonListener(this));
		importButton.addKeyListener(new ScanInputKeyListener());

		allSetDeptButton.addActionListener(new PrintFileButtonListener(this));
		allSetDeptButton.addKeyListener(new ScanInputKeyListener());
		allSetSealButton.addActionListener(new PrintFileButtonListener(this));
		allSetSealButton.addKeyListener(new ScanInputKeyListener());
		allDeleteDeptButton.addActionListener(new PrintFileButtonListener(this));
		allDeleteDeptButton.addKeyListener(new ScanInputKeyListener());
		allDeleteSealButton.addActionListener(new PrintFileButtonListener(this));
		allDeleteSealButton.addKeyListener(new ScanInputKeyListener());
		allSetButton.addActionListener(new PrintFileButtonListener(this));
		allSetButton.addKeyListener(new ScanInputKeyListener());
	}

	private void initUI() {

	}

	public JPanel getTablePanel() {
		return tablePanel;
	}

	public JCheckBox getSelectAll() {
		return selectAll;
	}

	public JButton getAddButton() {
		return addButton;
	}

	public JButton getDeleteButton() {
		return deleteButton;
	}

	public JButton getSureButton() {
		return sureButton;
	}

	public JButton getCancelButton() {
		return cancelButton;
	}

	public JButton getLookUpButton() {
		return lookUpButton;
	}

	public JButton getViewButton() {
		return viewButton;
	}

	public JButton getPrintButton() {
		return printButton;
	}

	public JButton getUseButton() {
		return useButton;
	}

	public JButton getSearchButton() {
		return searchButton;
	}

	public String getType() {
		return type;
	}

	public String getCategory(){
		return category;
	}

	public JButton getAddOnBomButton() {
		return addOnBomButton;
	}

	public JButton getSetDeptButton() {
		return setDeptButton;
	}

	public JButton getSetSealButton() {
		return setSealButton;
	}

	public JButton getSetNotPrinted() {
		return setNotPrinted;
	}

	public JButton getSetToPrinted() {
		return setToPrinted;
	}

	public JButton getAddOnProcessDirectory(){
		return addOnProcessDirectory;
	}

	public JButton getDistributionButton(){
		return distribution;
	}

	public JButton getModifyButton(){
		return modifyButton;
	}

	public JButton getAddChangeButton(){
		return addChangeButton;
	}

	public JButton getImportButton(){
		return importButton;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public JButton getDistribution() {
		return distribution;
	}

	public JButton getAllSetDeptButton() {
		return allSetDeptButton;
	}

	public JButton getAllSetSealButton() {
		return allSetSealButton;
	}

	public JButton getAllDeleteDeptButton() {
		return allDeleteDeptButton;
	}

	public JButton getAllDeleteSealButton() {
		return allDeleteSealButton;
	}

	public JButton getAllSetButton() {
		return allSetButton;
	}


}
