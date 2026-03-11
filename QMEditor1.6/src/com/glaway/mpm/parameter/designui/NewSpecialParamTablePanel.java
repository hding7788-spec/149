package com.glaway.mpm.parameter.designui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;

import org.dom4j.Element;

import com.glaway.mpm.dataPackage.ui.DPRightTechnicInfoPanel;
import com.glaway.mpm.mesDataSearch.ui.MesDataMainInfoPanel;
import com.glaway.mpm.mesParameter.helper.MesParameterProcessor;
import com.glaway.mpm.mesParameter.ui.MESParameterAddImageDialog;
import com.glaway.mpm.mesParameter.ui.MesParameterMainFrame;
import com.glaway.mpm.mesParameter.ui.RightTechnicInfoPanel;
import com.glaway.mpm.parameter.commonui.CommonTableModel;
import com.glaway.mpm.parameter.commonui.CommonTextFiledEditor;
import com.glaway.mpm.parameter.commonui.ParamTypeNameComboxTableCellDitor;
import com.glaway.mpm.parameter.commonui.SpecialSymbolEditor;
import com.glaway.mpm.parameter.commonui.SpecialSymbolRenderer;
import com.glaway.mpm.parameter.constants.ParameterConstants;
import com.glaway.mpm.parameter.helper.MPMParameterProcessor;
import com.glaway.mpm.parameter.listener.SpecialParamTableButtonListener;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.parameter.model.data.CmParameterTableColumn;
import com.glaway.mpm.parameter.model.data.CmParameterTablePackage;
import com.glaway.mpm.parameter.service.ProcessParameterToWCIntf;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.FilesUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.IconButton;

/**
 * 特殊记录表
 * @author Wangxl
 *
 */
public class NewSpecialParamTablePanel extends JPanel {

	private static final long serialVersionUID = 2708509949702663666L;
	private JButton addButton;
	private JButton removeButton;
	private JButton upButton;
	private JButton downButton;
	private JButton importButton;
	private JButton exportButton;
	private JButton saveButton;
	private JButton assignJYRY;
	private JButton assignCZRY;
	private JPanel buttonPanel;
	private JPanel buttonPanelBottom;
	private JTable table;
	private CommonTableModel tableModel;
	private String[] tableColumnName;
	private Class<?>[] tableColumnClass;
	private int[] editableColumns;
	private String[] tableColumnDataType;
	private int[] notShowColumns;
	private List<Integer> doubleClickBooleanColumns;
	private List<Integer> doubleClickMesBooleanColumns;
	private List<Integer> doubleClickPictureColumns;
	private List<Integer> doubleClickMesPictureColumns;
	private List<Integer> recordColumns;
	private SpecialParamTableButtonListener listener;
	private String productNumber = "";
	private String technicsNumber = "";
	private String technicsType = "";
	private String objNumber = "";
	private String objType = "";
	private String lukahao = "";
	private String gxPK = "";
	private String isZF = "";
	private String jianyanyuan;
	private String caozuoyuan;
	private boolean isApproved = false;
	private CmParamTableType paramTableType;
	private CmParameterTablePackage tablePackage;
	private Element objElement;
	private String imageFloder;
	private Container parentPanel;
	private String bsoID;
	private String version;

	private Map<String, List<String>> imageMap = null;
	private List<Integer> imageColumnList = new ArrayList<Integer>();
	private List<String> oldUUIDFileNameList = new ArrayList<String>();

	public NewSpecialParamTablePanel(String productNumber, String technicsNumber, String technicsType, String objNumber, String objType, boolean isApproved, String lukahao, String gxPK, String isZF, String jianyanyuan, String caozuoyuan, CmParamTableType paramTableType, Container parentPanel, String bsoID, String version) {
		this.productNumber = productNumber;
		this.technicsNumber = technicsNumber;
		this.technicsType = technicsType;
		this.objNumber = objNumber;
		this.objType = objType;
		this.isApproved = isApproved;
		this.lukahao = lukahao;
		this.gxPK = gxPK;
		this.isZF = isZF;
		this.jianyanyuan = jianyanyuan;
		this.caozuoyuan = caozuoyuan;
		this.paramTableType = paramTableType;
		this.parentPanel = parentPanel;
		this.bsoID = bsoID;
		this.version = version;
		initButton();
	}

	private void initButton() {
		addButton = new IconButton("/images/button_add.png", "添 加");
		removeButton = new IconButton("/images/button_remove.png", "移 除");
		upButton = new IconButton("/images/button_upmove.png", "上 移");
		downButton = new IconButton("/images/button_downmove.png", "下 移");
		importButton = new IconButton("/images/importData.gif", "导 入");
		exportButton = new IconButton("/images/export.gif", "导 出");
		saveButton = new IconButton("/images/save.gif", "保 存");

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		buttonPanel.add(addButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		buttonPanel.add(removeButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		buttonPanel.add(upButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		buttonPanel.add(downButton, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		buttonPanel.add(importButton, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		buttonPanel.add(exportButton, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		buttonPanel.add(saveButton, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));

		assignCZRY = new JButton(" 批量指定操作人员  ");
		assignJYRY = new JButton(" 批量指定检验人员  ");
		assignCZRY.setPreferredSize(new Dimension(150, 30));
		assignJYRY.setPreferredSize(new Dimension(150, 30));

		buttonPanelBottom = new JPanel();
		buttonPanelBottom.setLayout(new GridBagLayout());
		buttonPanelBottom.add(assignJYRY, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						10, 5, 5, 5), 0, 0));
		buttonPanelBottom.add(assignCZRY, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						10, 5, 5, 5), 0, 0));

		listener = new SpecialParamTableButtonListener(this,parentPanel);
		addButton.addActionListener(listener);
		removeButton.addActionListener(listener);
		upButton.addActionListener(listener);
		downButton.addActionListener(listener);
		importButton.addActionListener(listener);
		exportButton.addActionListener(listener);
		saveButton.addActionListener(listener);
		assignCZRY.addActionListener(listener);
		assignJYRY.addActionListener(listener);
	}

	public void setUIValues() {
		imageMap = new HashMap<String, List<String>>();
		if (paramTableType != null) {
			tablePackage = MPMParameterProcessor.getParameterTablePackage(paramTableType, jianyanyuan, caozuoyuan);
			initComponents();
			updateUI();
			setTableVlues(paramTableType);
		}
		if (isApproved) {
			setUIEnabled(false);
		} else {
			setUIEnabled(true);
		}
		assignCZRY.setVisible(false);
		assignJYRY.setVisible(false);
	}
	public void setMesUIValues(){
		imageMap = new HashMap<String, List<String>>();
		if (paramTableType != null) {
			tablePackage = MPMParameterProcessor.getParameterTablePackage(paramTableType, jianyanyuan,caozuoyuan);
			initComponents();
			if(isAllRecordColumns()){
				setMesButtomIsShow(true);
			}
			updateUI();
			setTableVlues(paramTableType);
			String mainNumber = MesParameterMainFrame.getProdureNumber();
			String produreNumber = MesParameterMainFrame.getMesParameterMainPanel().getProcedureNumberField().getText();
			if(!produreNumber.equals(mainNumber)){
				tableModel.setEditableColumns(null);
				doubleClickMesPictureColumns = new ArrayList<Integer>();
				assignCZRY.setEnabled(false);
				assignJYRY.setEnabled(false);
			}else{
				String jianyanyuan = MesParameterMainFrame.getJianyanyuan();
				String caozuoyuan = MesParameterMainFrame.getCaozuoyuan();
				if(jianyanyuan != null && !"".equals(jianyanyuan)){
					assignJYRY.setEnabled(true);
					assignCZRY.setEnabled(true);
				}
				if(caozuoyuan != null && !"".equals(caozuoyuan)){
					assignJYRY.setEnabled(false);
					assignCZRY.setEnabled(true);
				}
			}
		}
		if (isApproved) {
			setUIEnabled(true);
		} else {
			setUIEnabled(true);
		}
		assignCZRY.setVisible(true);
		assignJYRY.setVisible(true);
	}
	public void reloadTableValues(){
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("processNumber", MesParameterMainFrame.getMesParameterMainPanel().getProcessNumberBox().getTextFieldValue());
		paramsMap.put("technicsNumber", technicsNumber);
		paramsMap.put("objType", objType);
		paramsMap.put("objNumber", objNumber);
		paramsMap.put("lukahao", lukahao);
		paramsMap.put("gxPK", gxPK);
		paramsMap.put("isZF", isZF);
		paramsMap.put("bsoId", bsoID);
		paramsMap.put("version", version);

		List<CmParamTableType> mesList = MesParameterProcessor.getMesParamTableTypes(isApproved, paramsMap);
		boolean isMesDataExist = MesParameterProcessor.isMesDataExist(paramTableType, paramsMap);
		if(isMesDataExist){
			for(CmParamTableType mesParamTableType : mesList){
				if(mesParamTableType.getEnName().equals(paramTableType.getEnName()) && mesParamTableType.getParameters().size() > 0){
					paramTableType = mesParamTableType;
				}
			}
		}
		setTableVlues(paramTableType);
	}
	public void setDataPackageUIValues(){
		imageMap = new HashMap<String, List<String>>();
		if (paramTableType != null) {
			tablePackage = MPMParameterProcessor.getParameterTablePackage(paramTableType, jianyanyuan,caozuoyuan);
			initComponents();
			updateUI();
			setTableVlues(paramTableType);
			tableModel.setEditableColumns(null);
			doubleClickMesPictureColumns = new ArrayList<Integer>();
		}
		if (isApproved) {
			setUIEnabled(false);
		} else {
			setUIEnabled(true);
		}
		assignCZRY.setVisible(false);
		assignJYRY.setVisible(false);
	}

	public void setMesDataSearchUIValues(){
		imageMap = new HashMap<String, List<String>>();
		if (paramTableType != null) {
			tablePackage = MPMParameterProcessor.getParameterTablePackage(paramTableType, jianyanyuan,caozuoyuan);
			initComponents();
			updateUI();
			setTableVlues(paramTableType);
			tableModel.setEditableColumns(null);
			doubleClickMesPictureColumns = new ArrayList<Integer>();
		}
		if (isApproved) {
			setUIEnabled(false);
		} else {
			setUIEnabled(true);
		}
		assignCZRY.setVisible(false);
		assignJYRY.setVisible(false);
	}

	private void initComponents() {
		removeAll();
		this.tableColumnClass = tablePackage.getTableColumnClass();
		this.tableColumnName = tablePackage.getTableColumnName();
		this.tableColumnDataType = tablePackage.getTableColumnDataType();
		this.doubleClickBooleanColumns = tablePackage.getDoubleCilckBooleanColumns();
		this.doubleClickMesBooleanColumns = tablePackage.getDoubleCilckMesBooleanColumns();
		this.doubleClickPictureColumns = tablePackage.getDoubleCilckPictureColumns();
		this.doubleClickMesPictureColumns = tablePackage.getDoubleCilckMesPictureColumns();
		this.recordColumns = tablePackage.getRecordColumns();
		if(parentPanel instanceof RightTechnicInfoPanel || parentPanel instanceof DPRightTechnicInfoPanel){
			this.notShowColumns = tablePackage.getNotShowMesColumns();
			this.editableColumns = tablePackage.getEditableMesColumns();
			setUIisShow(false);
		}else if(parentPanel instanceof MesDataMainInfoPanel){
			this.tableColumnName = tablePackage.getMesDataSearchTableColumnName();
			this.notShowColumns = tablePackage.getNotShowDataSearchColumns();
			this.editableColumns = tablePackage.getEditableMesColumns();
			setUIisShow(false);
		}else{
			this.notShowColumns = tablePackage.getNotShowColumns();
			this.editableColumns = tablePackage.getEditableColumns();
		}

		tableModel = new CommonTableModel(tableColumnName, tableColumnClass, editableColumns);
		table = new JTable(tableModel);
		table.getTableHeader().setReorderingAllowed(false);
		table.setRowHeight(30);
//		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setGridColor(Color.GRAY);
		//表格双击监听事件
		table.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				tableDoubleClick(e);
			}
		});

		/** 隐藏不需要显示的列 */
		if (notShowColumns != null) {
			for (int column : notShowColumns) {
				CommonUIUtil.hiddenCell(table, column);
			}
		}

		/** 定义列编辑方式 */
		for (int i = 0; i < tableColumnDataType.length; i++) {
			CmParameterTableColumn tableColumn = tablePackage.getParamTableType().getTableColumns().get(i);
			String isFromParam = CommonUtil.objectToString(tableColumn.getIsfromparam());
			if (parentPanel instanceof RightTechnicInfoPanel) {
				MPMParameterProcessor.setTableHeaderColorAndAutoNextLine(table, i, Color.LIGHT_GRAY, recordColumns);
			} else {
				MPMParameterProcessor.setTableHeaderColor(table, i, Color.LIGHT_GRAY, recordColumns);
			}
			boolean isFromParamBoolean = "".equals(isFromParam) ? false : Boolean.parseBoolean(isFromParam);
			if (isFromParamBoolean) {
				table.getColumnModel().getColumn(i).setCellEditor(new ParamTypeNameComboxTableCellDitor(technicsType));
			} else {
				if (ParameterConstants.TABLE_COLUMN_DATATYPE_BLOB.equals(tableColumnDataType[i])) {
					//支持插入特殊符号
					if(parentPanel instanceof RightTechnicInfoPanel){
						imageFloder = WorkSpaceUtil.getMesTechnicsPath(technicsNumber);
					}else{
						imageFloder = WorkSpaceUtil.getTechnicsPath(technicsNumber);
					}
//					imageFloder = imageFloder.substring(0, imageFloder.lastIndexOf(technicsNumber) - 1);
					table.getColumnModel().getColumn(i).setCellRenderer(new SpecialSymbolRenderer());
					table.getColumnModel().getColumn(i).setCellEditor(new SpecialSymbolEditor(imageFloder));
				} else if (ParameterConstants.TABLE_COLUMN_DATATYPE_INTEGER.equals(tableColumnDataType[i])) {
					//只能输入整数
					table.getColumnModel().getColumn(i).setCellEditor(new CommonTextFiledEditor(new JTextField(), true));
				} else if (ParameterConstants.TABLE_COLUMN_DATATYPE_DECIMAL.equals(tableColumnDataType[i])) {
					//输出整数或小数
					table.getColumnModel().getColumn(i).setCellEditor(new CommonTextFiledEditor(new JTextField(), false));
				}else if(ParameterConstants.TABLE_COLUMN_DATATYPE_PICTURE.equals(tableColumnDataType[i])){
					imageColumnList.add(i);
				}
			}
		}

		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new BorderLayout());
		JScrollPane scrollPane = null;
		if(parentPanel instanceof RightTechnicInfoPanel){
			scrollPane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			JViewport viewPort = new JViewport();
			viewPort.add(table.getTableHeader());
			scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			scrollPane.setColumnHeader(viewPort);
			tablePanel.add(scrollPane);
		}else{
			tablePanel.add(table, BorderLayout.CENTER);
			scrollPane = new JScrollPane(tablePanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			JViewport viewPort = new JViewport();
			viewPort.add(table.getTableHeader());
			scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			scrollPane.setColumnHeader(viewPort);

		}

		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.EAST);
		add(buttonPanelBottom, BorderLayout.SOUTH);
	}

	public void setUIEnabled(boolean b) {
		table.setEnabled(b);
		addButton.setEnabled(b);
		removeButton.setEnabled(b);
		upButton.setEnabled(b);
		downButton.setEnabled(b);
		saveButton.setEnabled(b);
		saveButton.setVisible(false);
		importButton.setEnabled(b);
	}

	public void addOneRow() {
		CommonUIUtil.addOneRow(tableModel);
		String paramTableId = CommonUtil.objectToString(tablePackage.getParamTableType().getOid());
		int row = table.getRowCount() - 1;
		int columns = table.getColumnCount();
		for(int i = 0; i < columns; i++){
			String columnClass = table.getColumnClass(i).getName();
			if(columnClass.contains("Boolean")){
				table.setValueAt(false, row, i);
			}
		}
		table.setValueAt(version, row, 0);
		table.setValueAt(bsoID, row, 1);
		table.setValueAt(row, row, 2);
		table.setValueAt(objType, row, 3);
		table.setValueAt(CommonUtil.objectToString(System.nanoTime()), row, 4);
		table.setValueAt(technicsNumber, row, 5);
		table.setValueAt(paramTableId, row, 6);
		table.setValueAt(objNumber, row, 7);
	}

	public void removeRow() {
		int selRow = table.getSelectedRow();
		if (selRow > -1) {
			tableModel.removeRow(selRow);
		}
	}

	@SuppressWarnings("unchecked")
	public CmParamTableType getParamTableType() {
		CmParamTableType paramTaleType = tablePackage.getParamTableType();
		Vector<Vector<Object>> parameters = tableModel.getDataVector();
		if(imageFloder != null){
			parameters = replaceSaveSpecialSymbolPath(parameters);
		}
		if(imageColumnList != null && imageColumnList.size() > 0){
			parameters = writeIntoImagePath(parameters);
		}
		paramTaleType.setParameters(parameters);
		return paramTaleType;
	}

	public void setTableVlues(CmParamTableType paramTableType) {
		Vector<Vector<Object>> parameters = paramTableType.getParameters();
		if(imageFloder != null && parameters != null){
			parameters = replaceEditSpecialSymbolPath(parameters);
		}
		tableModel.setRowCount(0);
		if (parameters != null) {
			int row = 0;
			for (Vector<Object> values : parameters) {
				Vector<Object> newValues = new Vector<Object>();
				for(int column = 0; column < values.size(); column++){
					Object object = values.get(column);
					String columnType = table.getColumnClass(column).getName();
					if(columnType.contains("Boolean")){
						if(object.toString().equals("true")){
							object = true;
						}else{
							object = false;
						}
					}
					List<String> imagePathList = null;
					if(imageColumnList != null && imageColumnList.contains(column)){
						if(object != null && !object.toString().equals("")){
							imagePathList = new ArrayList<String>();
							int n = object.toString().split(";").length;
							String[] imagePaths = object.toString().split(";");
							for(int j = 0; j < n; j++){
								String uuid = imagePaths[j].substring(imagePaths[j].lastIndexOf(",") + 1, imagePaths[j].length());
								String imagePath = imagePaths[j].substring(0, imagePaths[j].lastIndexOf("/"));
								String imageName = imagePaths[j].substring(imagePaths[j].lastIndexOf("/") + 1,imagePaths[j].lastIndexOf(","));
								String templatePath = WorkSpaceUtil.getWorkSpace();
								File file = new File(templatePath + File.separator + imagePath + File.separator + uuid);
								if(file.exists()){
									oldUUIDFileNameList.add(imagePath + File.separator + uuid);
									byte[] bytes = FilesUtil.getBytes(file.getPath());
									FilesUtil.getFile(bytes, templatePath + File.separator + imagePath, imageName);
									imagePathList.add(templatePath + File.separator + imagePath + File.separator + imageName +"," + uuid +  ";");
								}
							}
							imageMap.put(row + "," + column, imagePathList);
							if(n == 0){
								object = "";
							}else{
								object = "已选图片数量：" + n;
							}
						}
					}
					newValues.add(object);
				}
				tableModel.addRow(newValues);
				row++;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void save() {
		Vector<Vector<Object>> dataVec = tableModel.getDataVector();
		if(imageFloder != null){
			dataVec = replaceSaveSpecialSymbolPath(dataVec);
		}
		XmlUtility.saveSpecialParamTable(this.getObjElement(), paramTableType, dataVec, technicsNumber, notShowColumns, tableColumnName);
	}

	public Vector<Vector<Object>> replaceSaveSpecialSymbolPath(Vector<Vector<Object>> vec){
		Vector<Vector<Object>> newVec = new Vector<Vector<Object>>();
		Vector<Object> strs = null;
		for(Vector<Object> vecStr : vec){
			strs = new Vector<Object>();
			for(Object str : vecStr){
				if(str.toString().contains(imageFloder)){
					str = str.toString().replace(imageFloder, "@#$%^");
				}
				strs.add(str);
			}
			newVec.add(strs);
		}
		return newVec;
	}
	public Vector<Vector<Object>> replaceEditSpecialSymbolPath(Vector<Vector<Object>> vec){
		Vector<Vector<Object>> newVec = new Vector<Vector<Object>>();
		Vector<Object> strs = null;
		for(Vector<Object> vecStr : vec){
			strs = new Vector<Object>();
			for(Object str : vecStr){
				if(str.toString().contains("@#$%^")){
					str = str.toString().replace("@#$%^", imageFloder);
				}
				strs.add(str);
			}
			newVec.add(strs);
		}
		return newVec;
	}

	private void tableDoubleClick(MouseEvent e){
		if (e.getClickCount() == 2) {
			int rowCount = table.getRowCount();
			int row = table.getSelectedRow();
			int column = table.getSelectedColumn();
			if(row != -1 && column != -1){
			String cellValue = String.valueOf(table.getValueAt(row, column));
			String className = tableModel.getColumnClass(column).getName();
			if(parentPanel instanceof RightTechnicInfoPanel){
				//图片类型双击操作
				if(ParameterConstants.TABLE_COLUMN_DATATYPE_PICTURE.equals(tableColumnDataType[column])){
					MESParameterAddImageDialog dialog = new MESParameterAddImageDialog(this, row, column, imageMap);
					dialog.newDialog();
					List<String> imagePathList = dialog.showDialog();
					imageMap.put(row + "," + column, imagePathList);
				}
				//布尔类型双击操作
				if(className.contains("Boolean") && doubleClickMesBooleanColumns.contains(Integer.valueOf(column))){
					for(int i = 0; i < rowCount; i++){
						if(cellValue.equals("true")){
							table.setValueAt(false, i, column);
						}else{
							table.setValueAt(true, i, column);
						}
					}
				}
			}else{
				//图片类型双击操作
				if(ParameterConstants.TABLE_COLUMN_DATATYPE_PICTURE.equals(tableColumnDataType[column])){
					MESParameterAddImageDialog dialog = new MESParameterAddImageDialog(this, row, column, imageMap);
					dialog.newDialog();
					List<String> imagePathList = dialog.showDialog();
					imageMap.put(row + "," + column, imagePathList);
				}
				//布尔类型双击操作
				if(className.contains("Boolean") && doubleClickBooleanColumns.contains(Integer.valueOf(column))){
					for(int i = 0; i < rowCount; i++){
						if(cellValue.equals("true")){
							table.setValueAt(false, i, column);
						}else{
							table.setValueAt(true, i, column);
						}
					}
				}
			}
			}
		}
	}
	private Vector<Vector<Object>> writeIntoImagePath(Vector<Vector<Object>> parameters){
		Vector<Vector<Object>> newParameters = new Vector<Vector<Object>>();
		Vector<Object> newVec = null;
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		String time = format.format(date);
			for(int row = 0; row < parameters.size(); row++){
				newVec = new Vector<Object>();
				for(int column = 0; column < parameters.get(0).size(); column++){
					if(imageColumnList.contains(column)){
						String imagePath = "";
						List<String> imagePathList = imageMap.get(row + "," + column);
						if(imagePathList != null){
							for(String image : imagePathList){
								image =  image.substring(image.lastIndexOf("\\") + 1, image.length());
								imagePath = imagePath + "mes/" + time + "/" + image;
							}
						}
						newVec.add(imagePath);
					}else{
						newVec.add(parameters.get(row).get(column));
					}
				}
				newParameters.add(newVec);
			}
		return newParameters;
	}

	public void uploadImage(){
		for(Map.Entry<String, List<String>> entry : imageMap.entrySet()){
			List<String> imagePathList = entry.getValue();
			if(imagePathList != null){
				for(String image : imagePathList){
					String imagePath = image.substring(0, image.lastIndexOf(","));
					byte[] bytes = FilesUtil.getBytes(imagePath);
					Date date = new Date();
					DateFormat df = new SimpleDateFormat("yyyyMMdd");
					String time = df.format(date);
					String uuid = image.substring(image.lastIndexOf(",") + 1, image.lastIndexOf(";"));
					try {
						ProcessParameterToWCIntf.uploadImage(bytes, time, uuid);
					} catch (RemoteException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}

			}
		}
	}
	public void removeAllTableValues(){
		int rowCount = table.getRowCount();
		for(int i = 0; i < rowCount; i++){
			tableModel.removeRow(0);
		}
	}
	/**
	 * 上移，下移操作
	 * @param up
	 * @param jTable1
	 */
	public void changeRowValue(boolean up,JTable jTable1) {
		if (jTable1.getSelectedRowCount() > 1)
			return;
		int select = jTable1.getSelectedRow();
		if (select < 0)
			return;
		if (up && select == 0)
			return;
		if (!up && select == jTable1.getRowCount() - 1)
			return;
		Object[] obj1 = new Object[jTable1.getColumnCount()];
		Object[] obj2 = new Object[jTable1.getColumnCount()];
		int neighbor;
		if (up)
			neighbor = select - 1;
		else
			neighbor = select + 1;
		for (int i = 0; i < jTable1.getColumnCount(); i++) {
			obj1[i] = jTable1.getValueAt(select, i);
			obj2[i] = jTable1.getValueAt(neighbor, i);
		}
		for (int j = 0; j < jTable1.getColumnCount(); j++) {
			if(j == 0){
				continue;
			}
			jTable1.setValueAt(obj2[j], select, j);
			jTable1.setValueAt(obj1[j], neighbor, j);
		}
		jTable1.setRowSelectionInterval(neighbor, neighbor);
	}

	public void setUIisShow(boolean b){
		addButton.setVisible(b);
		removeButton.setVisible(b);
		upButton.setVisible(b);
		downButton.setVisible(b);
		saveButton.setVisible(b);
		importButton.setVisible(b);
		exportButton.setVisible(b);
	}
	public void setMesButtomIsShow(boolean b){
		addButton.setVisible(b);
		removeButton.setVisible(b);
		upButton.setVisible(b);
		downButton.setVisible(b);
	}
	public boolean isAllRecordColumns(){
		boolean flag = false;
		List<Integer> recordColumns = tablePackage.getRecordColumns();
		List<Integer> notShowMesColumnsList = new ArrayList<Integer>();
		List<Integer> showColumnsList = new ArrayList<Integer>();
		int[] notShowMesColumns = tablePackage.getNotShowMesColumns();
		for(int i = 0; i < notShowMesColumns.length; i++){
			notShowMesColumnsList.add(notShowMesColumns[i]);
		}
		for(int j = 0; j < tableColumnDataType.length; j++){
			if(!notShowMesColumnsList.contains(j)){
				showColumnsList.add(j);
			}
		}
		for(int k = 0; k < showColumnsList.size(); k++){
			if(recordColumns.contains(showColumnsList.get(k))){
				flag = true;
			}else{
				return false;
			}
		}
		return flag;
	}
	public void stopCellEditing(){
		if(table.getCellEditor() != null){
			table.getCellEditor().stopCellEditing();
		}
	}
	public JButton getAddButton() {
		return addButton;
	}

	public JButton getRemoveButton() {
		return removeButton;
	}

	public JButton getUpButton() {
		return upButton;
	}

	public JButton getDownButton() {
		return downButton;
	}

	public JButton getSaveButton() {
		return saveButton;
	}

	public JTable getTable() {
		return table;
	}

	public CommonTableModel getTableModel() {
		return tableModel;
	}

	public String getTechnicsNumber() {
		return technicsNumber;
	}

	public String getObjNumber() {
		return objNumber;
	}

	public String getObjType() {
		return objType;
	}

	public Element getObjElement() {
		return objElement;
	}

	public void setObjElement(Element objElement) {
		this.objElement = objElement;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public String getLukahao() {
		return lukahao;
	}

	public String getGxPK() {
		return gxPK;
	}

	public String getJianyanyuan() {
		return jianyanyuan;
	}

	public String getCaozuoyuan() {
		return caozuoyuan;
	}

	public Map<String, List<String>> getImageMap() {
		return imageMap;
	}

	public List<String> getOldUUIDFileNameList() {
		return oldUUIDFileNameList;
	}

	public List<Integer> getDoubleClickBooleanColumns() {
		return doubleClickBooleanColumns;
	}

	public Container getParentPanel() {
		return parentPanel;
	}

	public List<Integer> getDoubleClickPictureColumns() {
		return doubleClickPictureColumns;
	}

	public List<Integer> getDoubleClickMesPictureColumns() {
		return doubleClickMesPictureColumns;
	}

	public String[] getTableColumnName() {
		return tableColumnName;
	}

	public int[] getNotShowColumns() {
		return notShowColumns;
	}
	public JButton getImportButton() {
		return importButton;
	}

	public JButton getExportButton() {
		return exportButton;
	}

	public JButton getAssignJYRY() {
		return assignJYRY;
	}

	public JButton getAssignCZRY() {
		return assignCZRY;
	}

	public int[] getEditableColumns() {
		return editableColumns;
	}

	public String getTechnicsType() {
		return technicsType;
	}

	public CmParameterTablePackage getTablePackage() {
		return tablePackage;
	}

	public String getBsoID(){
		return bsoID;
	}

	public String getVersion(){
		return version;
	}
}
