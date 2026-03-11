package com.glaway.mpm.parameter.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.glaway.mpm.parameter.model.data.CmBaiyuParamTable;
import com.glaway.mpm.parameter.model.data.CmBaiyuParamTableColumn;
import com.glaway.mpm.parameter.model.data.CmSearchCondition;
import com.glaway.mpm.parameter.model.tree.XWTreeNode;
import com.glaway.mpm.parameter.service.ProcessParameterToWCIntf;
import com.glaway.mpm.release.ProcessInfoReleaseController;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import com.toedter.calendar.JDateChooser;



public class BaiyuTableBasicInfoPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 8445153220088635892L;
	
	private static Logger LOGGER=Logger.getLogger(BaiyuTableBasicInfoPanel.class);
	
	private JLabel tableNameLabel;
	private JTextField tableNameTextField;
	private JLabel tableStatusLabel;
	private JComboBox tableStatusComboBox;
	private JButton searchButton;
	private JButton addButton;
	private JButton deleteButton;
	private JButton reviseButton;
	private JButton checkButton;
	private JButton disableButton;
	private JButton enableButton;
	private JButton modifyButton;
	private JPanel buttonPanel;
	private CmBaiyuParamTable baiyuParamTable;
	private MPMParameterMainFrame frame;
	
	/**
	 * 搜索条件的panel
	 */
	private JPanel conditionPanel;
	private JPanel conditionDatePanel;
	
	private JLabel conditionIDLabel;
	private JTextField conditionIDTextField;
	private JLabel conditionNameLabel;
	private JTextField conditionNameTextField;
	
	private JLabel conditionCreatorLabel;
	private JTextField conditionCreatorTextField;
	
	private JLabel conditionModifierLabel;
	private JTextField conditionModifierTextField;
	
	//状态
	private JLabel conditionStatusLabel;
	private JComboBox conditionStatusTextField;
	
	//表单类型
	private JLabel conditionFormTypeLabel;
	private JComboBox conditionFormTypeTextField;
	
	//部门
	private JLabel conditionDepartmentLabel;
	private JComboBox conditionDepartmentTextField;
	
	//创建时间
	private JLabel conditionCreateDateLabel;
	private JDateChooser conditionCreateDateFromTextField;
	private JDateChooser conditionCreateDateToTextField;
	private JLabel conditionToLabel,conditionToLabel2,conditionSpaceLabel;
	
	//修改时间
	private JLabel conditionModifyDateLabel;
	private JDateChooser conditionModifyDateFromTextField;
	private JDateChooser conditionModifyDateToTextField;
	
	//时间清除按钮
	private JButton createClearButton,modifyClearButton;
	
	private JButton clearConditionButton;
	private String[] tableType_array = {"","通用表","周期表","专用表","全局表"};
	private String[] conditionStatus = {"","启用", "禁用"};

	public BaiyuTableBasicInfoPanel(MPMParameterMainFrame frame) {
		this.frame = frame;
		initComponents();
		initLayout();
		initListener();
	}

	private void initComponents() {
		String[] status = new String[] {"启用", "禁用"};

		tableNameLabel = new JLabel("表单模板名称：");
		tableNameTextField = new JTextField();
		tableStatusLabel = new JLabel("启用/禁用：");
		tableStatusComboBox = new JComboBox(status);
		searchButton = new JButton("查询");
		addButton = new JButton("新增");
		deleteButton = new JButton("删除");
		reviseButton = new JButton("修改");
		checkButton = new JButton("查看");
		disableButton = new JButton("禁用");
		enableButton = new JButton("启用");
		modifyButton = new JButton("修改模板属性");
		buttonPanel = new JPanel();
		
		conditionDatePanel=new JPanel();
		initConditionComponents();

		tableNameTextField.setPreferredSize(new Dimension(210, 30));
		tableStatusComboBox.setPreferredSize(new Dimension(80, 30));
		searchButton.setPreferredSize(new Dimension(80, 30));
		addButton.setPreferredSize(new Dimension(80, 25));
		deleteButton.setPreferredSize(new Dimension(80, 25));
		reviseButton.setPreferredSize(new Dimension(80, 25));
		checkButton.setPreferredSize(new Dimension(80, 25));
		disableButton.setPreferredSize(new Dimension(80, 25));
		enableButton.setPreferredSize(new Dimension(80, 25));
		modifyButton.setPreferredSize(new Dimension(210, 25));
	}

	/** 
	  * @Description: 初始化搜索条件组件
	  * @date 2025年10月20日上午10:13:53
	  * @author Liluwen  
	  * @return 
	*/
	private void initConditionComponents() {
		conditionPanel = new JPanel();
		conditionIDLabel= new JLabel("ID：");
		conditionIDTextField = new JTextField();
		conditionNameLabel = new JLabel("名称：");
		conditionNameTextField = new JTextField();
		
		conditionCreatorLabel = new JLabel("创建人：");
		conditionCreatorTextField = new JTextField();
		
		conditionModifierLabel = new JLabel("修改人：");
		conditionModifierTextField = new JTextField();
		
		conditionStatusLabel = new JLabel("状态：");
		conditionStatusTextField = new JComboBox(conditionStatus);
		
		conditionFormTypeLabel = new JLabel("表单类型：");
		conditionFormTypeTextField = new JComboBox(tableType_array);
		conditionDepartmentLabel = new JLabel("部门：");
		List<String> allList = getDeparmentList();
		conditionDepartmentTextField =  new JComboBox(allList.toArray());
		
		conditionCreateDateLabel = new JLabel("创建时间：");
		conditionToLabel = new JLabel("至");
		conditionToLabel2= new JLabel("至");
		conditionSpaceLabel= new JLabel("        ");
		conditionCreateDateFromTextField = new JDateChooser();
		conditionCreateDateFromTextField.setDateFormatString("yyyy-MM-dd"); // 日期格式
		conditionCreateDateToTextField = new JDateChooser();
		conditionCreateDateToTextField.setDateFormatString("yyyy-MM-dd"); // 日期格式
		createClearButton= new JButton("清除");
		
		conditionModifyDateLabel = new JLabel("        修改时间：");
		conditionModifyDateFromTextField = new JDateChooser();
		conditionModifyDateFromTextField.setDateFormatString("yyyy-MM-dd");
		conditionModifyDateToTextField = new JDateChooser();
		conditionModifyDateToTextField.setDateFormatString("yyyy-MM-dd");
		modifyClearButton= new JButton("清除");
		
		clearConditionButton = new JButton("清除条件");
		
		conditionIDTextField.setPreferredSize(new Dimension(210, 30));
		conditionNameTextField.setPreferredSize(new Dimension(210, 30));
		conditionCreatorTextField.setPreferredSize(new Dimension(210, 30));
		conditionModifierTextField.setPreferredSize(new Dimension(210, 30));
		conditionStatusTextField.setPreferredSize(new Dimension(210, 30));
		conditionFormTypeTextField.setPreferredSize(new Dimension(210, 30));
		conditionDepartmentTextField.setPreferredSize(new Dimension(210, 30));
		conditionCreateDateFromTextField.setPreferredSize(new Dimension(150, 30));
		conditionCreateDateToTextField.setPreferredSize(new Dimension(150, 30));
		conditionModifyDateFromTextField.setPreferredSize(new Dimension(150, 30));
		conditionModifyDateToTextField.setPreferredSize(new Dimension(150, 30));
		clearConditionButton.setPreferredSize(new Dimension(210, 25));
		createClearButton.setPreferredSize(new Dimension(60, 25));
		modifyClearButton.setPreferredSize(new Dimension(60, 25));
	}

	/** 
	  * @Description: 获取部门列表 
	  * @date 2025年10月20日下午7:05:44
	  * @author Liluwen
	  * @return  
	  * @return 
	*/
	private List<String> getDeparmentList() {
		List<String> allList = new ArrayList<String>();
		Map<String,String> workShop = ResourceIntf.getWorkShops();
		if (workShop != null && workShop.size() > 0) {
			Collection<String> coll =  workShop.values();
			Iterator<String> it = coll.iterator();
			while (it.hasNext()) {
				String temp = (String) it.next();
				if (temp != null && temp.trim().length() > 0) {
					allList.add(temp);
				}
			}

			Collections.sort(allList);
		}
		allList.add(0,"");
		allList.add(1,"通用");
		return allList;
	}

	private void initLayout() {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();

		g.gridx = 0;
		g.gridy = 0;
		g.insets = new Insets(10, 10, 0, 10);
		g.anchor = GridBagConstraints.EAST;
		contentPanel.add(tableNameLabel, g);
		g.gridx = 1;
		g.anchor = GridBagConstraints.WEST;
		contentPanel.add(tableNameTextField, g);
		
		initConditionPanel();
		g.gridx = 0;
		g.gridy = 1;
		g.gridwidth = 6;
		g.insets = new Insets(10, 10, 0, 10);
		contentPanel.add(conditionPanel, g);
		
		initConditionDatePanel();
		g.gridx = 0;
		g.gridy = 2;
		g.gridwidth = 6;
		g.insets = new Insets(10, 10,20, 10);
		contentPanel.add(conditionDatePanel, g);

		buttonPanel.setLayout(new GridBagLayout());
		GridBagConstraints g1 = new GridBagConstraints();
		g1.gridx = 0;
		g1.gridy = 0;
		g1.insets = new Insets(10, 10, 0, 90);
		buttonPanel.add(addButton, g1);
		g1.gridx =  1;
		buttonPanel.add(deleteButton, g1);
		g1.gridx =  2;
		buttonPanel.add(reviseButton, g1);
		g1.gridx = 3;
		buttonPanel.add(checkButton,g1);
		g1.gridx = 4;
		buttonPanel.add(disableButton,g1);
		g1.gridx =  5;
		buttonPanel.add(enableButton, g1);
		g1.gridx =  6;
		buttonPanel.add(modifyButton, g1);
		
		g.gridx = 0;
		g.gridy = 3;
		g.gridwidth = 6;
		g.insets = new Insets(0, 0, 0, 10);
		contentPanel.add(buttonPanel, g);

		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(contentPanel);
	}
	
	/** 
	  * @Description: 初始化时间搜索框的panel
	  * @date 2025年10月20日下午3:43:25
	  * @author Liluwen  
	  * @return 
	*/
	private void initConditionDatePanel() {
		conditionDatePanel.setLayout(new GridBagLayout());
		GridBagConstraints g1 = new GridBagConstraints();
		g1.gridx = 0;
		g1.gridy = 0;
		g1.insets = new Insets(0, 0, 0, 5);
		conditionDatePanel.add(conditionCreateDateLabel,g1);
		g1.gridx = 1;
		conditionDatePanel.add(conditionCreateDateFromTextField,g1);
		g1.gridx = 2;
		conditionDatePanel.add(conditionToLabel,g1);
		g1.gridx = 3;
		conditionDatePanel.add(conditionCreateDateToTextField,g1);
		g1.gridx = 4;
		conditionDatePanel.add(createClearButton,g1);
		g1.gridx = 5;
		conditionDatePanel.add(conditionModifyDateLabel,g1);
		g1.gridx = 6;
		conditionDatePanel.add(conditionModifyDateFromTextField,g1);
		g1.gridx = 7;
		conditionDatePanel.add(conditionToLabel2,g1);
		g1.gridx = 8;
		conditionDatePanel.add(conditionModifyDateToTextField,g1);
		g1.gridx = 9;
		conditionDatePanel.add(modifyClearButton,g1);
		g1.gridx = 10;
		conditionDatePanel.add(conditionSpaceLabel,g1);
		g1.gridx = 11;
		conditionDatePanel.add(searchButton, g1);
		g1.gridx = 12;
		conditionDatePanel.add(clearConditionButton, g1);
	}

	/** 
	  * @Description: 初始化条件面板
	  * @date 2025年10月20日上午10:14:49
	  * @author Liluwen
	  * @return  
	  * @return 
	*/
	private void initConditionPanel() {
		conditionPanel.setLayout(new GridBagLayout());
		GridBagConstraints g1 = new GridBagConstraints();
		g1.gridx = 0;
		g1.gridy = 0;
		g1.insets = new Insets(10, 10, 0, 10);
		conditionPanel.add(conditionIDLabel, g1);
		g1.gridx =  1;
		conditionPanel.add(conditionIDTextField, g1);
		g1.gridx =  2;
		conditionPanel.add(conditionNameLabel, g1);
		g1.gridx = 3;
		conditionPanel.add(conditionNameTextField,g1);
		/**不显示创建人、修改人
		g1.gridx = 4;
		conditionPanel.add(conditionCreatorLabel,g1);
		g1.gridx = 5;
		conditionPanel.add(conditionCreatorTextField,g1);
		g1.gridx = 6;
		conditionPanel.add(conditionModifierLabel,g1);
		g1.gridx = 7;
		conditionPanel.add(conditionModifierTextField,g1);
		*/
		
		g1.gridx = 0;
		g1.gridy = 1;
		conditionPanel.add(conditionStatusLabel,g1);
		g1.gridx = 1;
		conditionPanel.add(conditionStatusTextField,g1);
		g1.gridx = 2;
		conditionPanel.add(conditionFormTypeLabel,g1);
		g1.gridx = 3;
		conditionPanel.add(conditionFormTypeTextField,g1);
		g1.gridx = 4;
		conditionPanel.add(conditionDepartmentLabel,g1);
		g1.gridx = 5;
		conditionPanel.add(conditionDepartmentTextField,g1);
	}

	public void setUIValues(XWTreeNode treeNode) {
		if (treeNode == null)
			return ;
		clear();
		CmBaiyuParamTable cmTreeNode = (CmBaiyuParamTable) treeNode.getTreeObject().getTreeNode();
		if (cmTreeNode != null) {
			tableNameTextField.setEditable(false);
			tableNameTextField.setText(cmTreeNode.getName());
			this.baiyuParamTable = cmTreeNode;
		}
	}

	private void clear() {
		tableNameTextField.setText("");
		tableStatusComboBox.setSelectedIndex(0);
	}

	private void initListener() {
		searchButton.addActionListener(this);
		addButton.addActionListener(this);
		deleteButton.addActionListener(this);
		reviseButton.addActionListener(this);
		checkButton.addActionListener(this);
		disableButton.addActionListener(this);
		enableButton.addActionListener(this);
		modifyButton.addActionListener(this);
		clearConditionButton.addActionListener(this);
		modifyClearButton.addActionListener(this);
		createClearButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		BaiyuParamTablePanel baiyuParamTablePanel = MPMParameterMainFrame.getBaiyuParamTablePanel();
		if (baiyuParamTablePanel == null)
			return ;
		BaiyuTableDetailInfoPanel detailPanel = baiyuParamTablePanel.getDetailInfoPanel();
		JTable table = detailPanel.getTable();
		if (obj == addButton) {
			String url = "baiyu://schema?type=templateCreate";
			ProcessInfoReleaseController.openURL(url);
		} else if (obj == deleteButton) {
			int selRow = table.getSelectedRow();
			if (selRow == -1) {
				JOptionPane.showMessageDialog(null, "请先选择一行！");
				return;
			}
			String oid = (String) table.getValueAt(selRow, 0);
			try {
				String returnStr = ProcessParameterToWCIntf.deleteBaiyuTemplateByOid(oid);
				if("Y".equals(returnStr)){
					refreshTables("启用");
					JOptionPane.showMessageDialog(null, "删除成功！");
				}else{
					JOptionPane.showMessageDialog(null, "删除失败！");
					return;
				}
			} catch (RemoteException ex) {
				ex.printStackTrace();
			} catch (InvocationTargetException ex) {
				ex.printStackTrace();
			}
		}  else if (obj == reviseButton) {
			int selRow = table.getSelectedRow();
			if (selRow == -1) {
				JOptionPane.showMessageDialog(null, "请先选择一行！");
				return;
			}
			String oid = (String) table.getValueAt(selRow, 0);
			try {
				Map<String,String> fileUrl = TechnicsIntf.getFileURLByDocOid(oid);
				if(fileUrl!=null){
					String qbyURL = fileUrl.get("qby");
					String mjsonURL = fileUrl.get("mjson");
					String ojsonURL = fileUrl.get("ojson");
					System.out.println("baiyu://schema?type=templateUpdate&qbyFileAbsolutePath="+qbyURL+"&xlsxFileAbsolutePath="+mjsonURL+"&jsonFileAbsolutePath="+ojsonURL+"&templateOid="+oid);
					String url = "baiyu://schema?type=templateUpdate&qbyFileAbsolutePath="+qbyURL+"&xlsxFileAbsolutePath="+mjsonURL+"&jsonFileAbsolutePath="+ojsonURL+"&templateOid="+oid;
					ProcessInfoReleaseController.openURL(url);
				}
			} catch (InvocationTargetException ex) {
				ex.printStackTrace();
			} catch (RemoteException ex) {
				ex.printStackTrace();
			}
		}else if(obj == checkButton){
		}else if(obj == disableButton){
			int selRow = table.getSelectedRow();
			if (selRow == -1) {
				JOptionPane.showMessageDialog(null, "请先选择一行！");
				return;
			}
			String oid = (String) table.getValueAt(selRow, 0);
			String isUsed = (String) table.getValueAt(selRow, 8);
			if("禁用".equals(isUsed)){
				return;
			}
			try {
				String returnStr = ProcessParameterToWCIntf.changeBaiyuTemplateStateByOid(oid,"禁用");
				if("Y".equals(returnStr)){
					refreshTables("启用");
					JOptionPane.showMessageDialog(null, "禁用模板成功！");
				}else{
					JOptionPane.showMessageDialog(null, "禁用模板失败！");
					return;
				}
			} catch (RemoteException ex) {
				ex.printStackTrace();
			} catch (InvocationTargetException ex) {
				ex.printStackTrace();
			}
		}else if (obj == enableButton) {
			int selRow = table.getSelectedRow();
			if (selRow == -1) {
				JOptionPane.showMessageDialog(null, "请先选择一行！");
				return;
			}
			String oid = (String) table.getValueAt(selRow, 0);
			String isUsed = (String) table.getValueAt(selRow, 8);
			if("启用".equals(isUsed)){
				return;
			}
			try {
				String returnStr = ProcessParameterToWCIntf.changeBaiyuTemplateStateByOid(oid,"启用");
				if("Y".equals(returnStr)){
					refreshTables("启用");
					JOptionPane.showMessageDialog(null, "启用模板成功！");
				}else{
					JOptionPane.showMessageDialog(null, "启用模板失败！");
					return;
				}
			} catch (RemoteException ex) {
				ex.printStackTrace();
			} catch (InvocationTargetException ex) {
				ex.printStackTrace();
			}
		}else if(obj == searchButton){
			String status = (String) conditionStatusTextField.getSelectedItem();
			String conditionID=conditionIDTextField.getText();
			String conditionName=conditionNameTextField.getText();
			String creator=conditionCreatorTextField.getText();
			String modifier=conditionModifierTextField.getText();
			String formType=(String)conditionFormTypeTextField.getSelectedItem();
			String conditionDepartment=(String)conditionDepartmentTextField.getSelectedItem();
			Date createDateFrom=conditionCreateDateFromTextField.getDate();
			Date createDateTo=conditionCreateDateToTextField.getDate();
			Date modifyDateFrom=conditionModifyDateFromTextField.getDate();
			Date modifyDateTo=conditionModifyDateToTextField.getDate();
			
			if(createDateFrom!=null&&createDateTo!=null) {
				if(createDateTo.compareTo(createDateFrom)<0) {
					JOptionPane.showMessageDialog(null, "创建时间的结束时间不能早于开始时间！");
					return;
				}
			}
			
			if(modifyDateFrom!=null&&modifyDateTo!=null) {
				if(modifyDateTo.compareTo(modifyDateFrom)<0) {
					JOptionPane.showMessageDialog(null, "修改时间的结束时间不能早于开始时间！");
					return;
				}
			}
			
			
			
			CmSearchCondition condition=new CmSearchCondition();
			condition.setConditionID(conditionID);
			condition.setConditionName(conditionName);
			condition.setCreator(creator);
			condition.setModifier(modifier);
			condition.setFormType(formType);
			condition.setConditionDepartment(conditionDepartment);
			condition.setCreateDateFrom(createDateFrom);
			condition.setCreateDateTo(createDateTo);
			condition.setModifyDateFrom(modifyDateFrom);
			condition.setModifyDateTo(modifyDateTo);
			condition.setStatus(status);
			System.out.println("Search Condition:"+condition.toString());
			refreshTables(condition);
		}else if(obj == modifyButton){
			int selRow = table.getSelectedRow();
			if (selRow == -1) {
				JOptionPane.showMessageDialog(null, "请先选择一行！");
				return;
			}
			String oid = (String) table.getValueAt(selRow, 0);
			String tableType = (String) table.getValueAt(selRow, 9);
			String dept = (String) table.getValueAt(selRow, 10);
			new BaiyuModifyTableJDialog(frame,oid,tableType,dept);
			String value = (String) tableStatusComboBox.getSelectedItem();
			refreshTables(value);
		} else if(obj==clearConditionButton) {
			clearSearchCondition();
		} else if(obj==createClearButton) {
			conditionCreateDateFromTextField.setDate(null);
			conditionCreateDateToTextField.setDate(null);
		} else if(obj==modifyClearButton) {
			conditionModifyDateFromTextField.setDate(null);
			conditionModifyDateToTextField.setDate(null);
		} 
	}

	/** 
	  * @Description:清空搜索条件
	  * @date 2025年10月21日下午3:35:20
	  * @author Liluwen  
	  * @return 
	*/
	private void clearSearchCondition() {
		conditionIDTextField.setText("");
		conditionNameTextField.setText("");
		conditionCreatorTextField.setText("");
		conditionModifierTextField.setText("");
		conditionStatusTextField.setSelectedIndex(0);
		conditionFormTypeTextField.setSelectedIndex(0);
		conditionDepartmentTextField.setSelectedIndex(0);
		conditionCreateDateFromTextField.setDate(null);
		conditionCreateDateToTextField.setDate(null);
		conditionModifyDateFromTextField.setDate(null);
		conditionModifyDateToTextField.setDate(null);
	}

	/** 
	  * @Description: 根据条件查询白羽表数据
	  * @date 2025年10月21日下午6:33:29
	  * @author Liluwen
	  * @param condition  
	  * @return 
	*/
	private void refreshTables(CmSearchCondition condition) {
		try {
			BaiyuParamTablePanel baiyuParamTablePanel = MPMParameterMainFrame.getBaiyuParamTablePanel();
			if (baiyuParamTablePanel == null)
				return ;
			BaiyuTableDetailInfoPanel detailPanel = baiyuParamTablePanel.getDetailInfoPanel();
			JTable table = detailPanel.getTable();
			DefaultTableModel tableModel = detailPanel.getTableModel();
			String[] columnName = detailPanel.getColumnName();
			
			List<CmBaiyuParamTableColumn> paramLists = ProcessParameterToWCIntf.getBaiyuParamListsByCondition(condition.getConditionID(),condition.getConditionName(),condition.getCreator(),condition.getModifier()
					,condition.getStatus(),condition.getConditionDepartment(),condition.getFormType(),condition.getCreateDateFrom(),condition.getCreateDateTo(),condition.getModifyDateFrom(),condition.getModifyDateTo());
			Object[][] tableBody = new Object[paramLists.size()][columnName.length];
			CmBaiyuParamTableColumn column;
			for (int i = 0; i < paramLists.size(); i++) {
				column = paramLists.get(i);
				tableBody[i][0] = column.getTableOid();
				tableBody[i][1] = column.getOrderNo();
				tableBody[i][2] = column.getTableId();
				tableBody[i][3] = column.getTableName();
				tableBody[i][4] = column.getTableCreator();
				tableBody[i][5] = column.getTableModifier();
				tableBody[i][6] = column.getTableCreateTime();
				tableBody[i][7] = column.getTableModifyTime();
				tableBody[i][8] = column.getIsUsed();
				tableBody[i][9] = column.getTableType();
				tableBody[i][10] = column.getDept();
			}
			tableModel.setDataVector(tableBody,columnName);
			CommonUIUtil.hiddenCell(table, 0);
			table.updateUI();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} 
	}
	
	private void refreshTables(String status) {
		try {
			BaiyuParamTablePanel baiyuParamTablePanel = MPMParameterMainFrame.getBaiyuParamTablePanel();
			if (baiyuParamTablePanel == null)
				return ;
			BaiyuTableDetailInfoPanel detailPanel = baiyuParamTablePanel.getDetailInfoPanel();
			JTable table = detailPanel.getTable();
			DefaultTableModel tableModel = detailPanel.getTableModel();
			String[] columnName = detailPanel.getColumnName();
			List<CmBaiyuParamTableColumn> paramLists = ProcessParameterToWCIntf.getBaiyuParamLists(status);
			Object[][] tableBody = new Object[paramLists.size()][columnName.length];
			CmBaiyuParamTableColumn column;
			for (int i = 0; i < paramLists.size(); i++) {
				column = paramLists.get(i);
				System.out.println("refreshTables TableId="+column.getTableId());
				System.out.println("refreshTables TableName="+column.getTableName());
				tableBody[i][0] = column.getTableOid();
				tableBody[i][1] = column.getOrderNo();
				tableBody[i][2] = column.getTableId();
				tableBody[i][3] = column.getTableName();
				tableBody[i][4] = column.getTableCreator();
				tableBody[i][5] = column.getTableModifier();
				tableBody[i][6] = column.getTableCreateTime();
				tableBody[i][7] = column.getTableModifyTime();
				tableBody[i][8] = column.getIsUsed();
				tableBody[i][9] = column.getTableType();
				tableBody[i][10] = column.getDept();
			}
			tableModel.setDataVector(tableBody,columnName);
			CommonUIUtil.hiddenCell(table, 0);
			table.updateUI();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
