package com.glaway.mpm.parameter.designui;

import com.glaway.mpm.parameter.commonui.CommonTableModel;
import com.glaway.mpm.parameter.listener.CheckParamTableButtonListener;
import com.glaway.mpm.qmIntf.tecparam.TechnicsParamSearchDialog;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.JavaUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.IconButton;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.SpecialWordPanel;
import com.glaway.mpm.view.TechnicsStepJPanel_XW;
import com.glaway.speciaword.common.CommonHelper;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;


/**
 * 检验记录表
 * @author zhuhao 2017.10.31
 *
 */
public class NewCheckParamTablePanel extends JPanel{

	private static final long serialVersionUID = 1658646539083060096L;
	private TechnicsStepJPanel_XW stepPanel;
	private NewCheckParamTabbedPanel panel;
	private Container parentPanel;
	private JPanel buttonPanel = new JPanel();
	private JButton addButton;
	private JButton removeButton;
	private JButton copyButton;
	private JButton pasteButton;
	private JButton importexeclButton;
	private JButton getexeclButton;
	private JButton gycsButton;
	private JButton saveButton;
	private JTable table;
	private CommonTableModel tablemodel;
	private Class<?>[] cellData;
	private Element objElement;
	private CheckParamTableButtonListener listener;
	private String technicsNumber = "";
	private List<String> tableType;
	private String[] columnNames;
	private String dybbm_Value;
	private String bzjlx_Value;
	private String xmm_Value;
	private String xmm_Path;
	private String tbm_Value;
	private String tbm_Path;
	private String mxCs_Value;
	private String uuid_Value; //modify by lkc 2017.12.05
	private List<String> cellValue;
	private Map<String, List<String>> cellMap;
	private Vector<Vector<Object>> dataVec =  new Vector<Vector<Object>>();
	private NewTechnicsPart frame;
	private String imageFolder;
	private int allRow;
	private String tabTableName; //add by lkc 2017.12.12

	private JTextComponent currentText = null;


	public NewCheckParamTablePanel(String dybbm_Value, String bzjlx_Value, String xmm_Value,String xmm_Path, String tbm_Value,String tbm_Path, String mxCs_Value, String uuid_Value,
			Class<?>[] cellData, NewCheckParamTabbedPanel panel, Container parentPanel,
			String technicsNumber, List<String> tableType, Vector<Vector<Object>> dataVec, NewTechnicsPart frame, String imageFolder) {
		String tabTableName = dybbm_Value + "("+ bzjlx_Value +")"; //add by lkc 2017.12.12
		this.tabTableName = tabTableName;
		this.panel = panel;
		this.cellData = cellData;
		this.parentPanel = parentPanel;
		this.technicsNumber = technicsNumber;
		this.tableType = tableType;
		this.dybbm_Value = dybbm_Value;
		this.bzjlx_Value = bzjlx_Value;
		this.xmm_Value = xmm_Value;
		this.xmm_Path = xmm_Path;
		this.tbm_Value = tbm_Value;
		this.tbm_Path = tbm_Path;
		this.mxCs_Value = mxCs_Value;
		this.uuid_Value = uuid_Value; //modify by lkc 2017.12.05
		this.dataVec = dataVec;
		this.frame = frame;
		this.imageFolder = imageFolder;
		initButton();
	}

	private void initButton() {
		addButton = new IconButton("/images/button_add.png", "添 加");
		removeButton = new IconButton("/images/button_remove.png", "移 除");
		copyButton = new IconButton("/images/button_copy.png", "复 制");
		pasteButton = new IconButton("/images/button_paste.png", "粘 贴");
		importexeclButton = new IconButton("/images/button_importexecl.png", "导入Execl");
		getexeclButton = new IconButton("/images/button_getexecl.png", "导出Execl");
		gycsButton = new IconButton("/images/expression_create.gif", "插入工艺参数");
		saveButton = new IconButton("/images/save.gif", "保 存");

		buttonPanel = new JPanel();
		BoxLayout boxout = new BoxLayout(buttonPanel, BoxLayout.Y_AXIS);
	    buttonPanel.setLayout(boxout);
	    buttonPanel.add(addButton);
	    buttonPanel.add(removeButton);
	    buttonPanel.add(copyButton);
	    buttonPanel.add(pasteButton);
	    buttonPanel.add(importexeclButton);
	    buttonPanel.add(getexeclButton);
	    buttonPanel.add(gycsButton);

		listener = new CheckParamTableButtonListener(this, parentPanel);
		addButton.addActionListener(listener);
		removeButton.addActionListener(listener);
		copyButton.addActionListener(listener);
		pasteButton.addActionListener(listener);
		importexeclButton.addActionListener(listener);
		getexeclButton.addActionListener(listener);
		saveButton.addActionListener(listener);

		gycsButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {

					String param = null;
					TechnicsParamSearchDialog dia = new TechnicsParamSearchDialog(frame);

					param = dia.showDialog();
					if(param == null) {
						param = "";
					}
					if(currentText !=null){
						String oldText = currentText.getText();
						if(oldText!=null&&!"".equals(oldText)&&!"null".equals(oldText)){
							param = oldText + param;
						}
						currentText.setText(param);
					}

				} catch(Exception ee) {
					ee.printStackTrace();
				}
			}
		});
	}
	public void setUIValues(String bzjlx_Value) {
		initComponents(bzjlx_Value);
		setUIEnabled(true);
	}


	private void initComponents(String bzjlx_Value) {
		removeAll();
		if("检测类".equals(bzjlx_Value)){
			final int[] editableColumns = {1, 2, 3, 4,5};
			cellData = new Class<?>[]{String.class,Boolean.class,String.class,String.class, String.class,String.class,String.class,String.class};
			columnNames = new String[]{"","","检测项","公称值", "上偏差", "下偏差","实测值","判定/结论"};
			tablemodel = new CommonTableModel(columnNames, cellData, editableColumns);
		}else{
			final int[] editableColumns = {1,2,3};
			cellData = new Class<?>[]{String.class,Boolean.class,String.class,String.class, String.class,String.class};
			columnNames = new String[]{ "","","记录项","要求","记录","判定/结论"};
			tablemodel = new CommonTableModel(columnNames, cellData, editableColumns);
		}
		Vector<Object> columnName = new Vector<Object>();
		for(int i=0;i<columnNames.length;i++){
			columnName.add(columnNames[i]);
		}
		//回读数值
		table = new JTable(tablemodel);
		if(dataVec != null&& !dataVec.isEmpty()){
			for(int i=0; i<dataVec.size();i++){
				addOneRow();
				for(int j = 0; j<6;j++){
					Object cellValue = dataVec.get(i).get(j);
					table.setValueAt(cellValue, i, j);
				}
			}
			for(int i=0;i<dataVec.size();i++)
				table.setValueAt(i+1, i, 0);
		}
		//设置序号和复选框列宽
		TableColumn firsetColumn = table.getColumnModel().getColumn(0);
		firsetColumn.setPreferredWidth(30);
		firsetColumn.setMaxWidth(30);
		firsetColumn.setMinWidth(30);
		TableColumn secondColumn = table.getColumnModel().getColumn(1);
		secondColumn.setPreferredWidth(30);
		secondColumn.setMaxWidth(30);
		secondColumn.setMinWidth(30);
		//渲染table
		if("检测类".equals(bzjlx_Value)){
			table.setRowHeight(30);
			this.table.getColumnModel().getColumn(2).setCellEditor(new forEditor(new JTextField()));
			this.table.getColumnModel().getColumn(3).setCellEditor(new checkEditor(new JTextField()));
			this.table.getColumnModel().getColumn(4).setCellEditor(new checkEditor(new JTextField()));
			this.table.getColumnModel().getColumn(5).setCellEditor(new checkEditor(new JTextField()));
		}else if("记录类".equals(bzjlx_Value)){
			this.table.getColumnModel().getColumn(2).setCellRenderer(new SWRenderer());
			this.table.getColumnModel().getColumn(2).setCellEditor(new SWEditor());
			this.table.getColumnModel().getColumn(3).setCellRenderer(new SWRenderer());
			this.table.getColumnModel().getColumn(3).setCellEditor(new SWEditor());
		}
		tablemodel.addTableModelListener(new TableModelListener(){//table监听行数（序号）
			@Override
			public void tableChanged(TableModelEvent e) {
				allRow = tablemodel.getRowCount();
			}
		});
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		tablePanel.add(scrollPane);
		setLayout(new BorderLayout());
		add(tablePanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.EAST);
	}

	@SuppressWarnings("unchecked")
	public void save() {
		String imageFolder = panel.getImageFolder();
		tableType = getTableType();
		Vector<Vector<Object>> dataVec = tablemodel.getDataVector();
		XmlUtility.saveCheckRecordTable(this.getObjElement(), dataVec, technicsNumber, tableType, columnNames, imageFolder);
	}
	class forEditor extends DefaultCellEditor {
		private static final long serialVersionUID = 1L;
		private int editingColumn = 2;
		private JTextField checkField = null;
		forEditor(JTextField textField) {
			super(textField);
			this.checkField = textField;
		}
		public Object getCellEditorValue() {
			String text = this.checkField.getText();
			int clickRow = table.getSelectedRow();
			boolean isClick = (Boolean) table.getValueAt(clickRow, 1);
			if(isClick){
				for(int i=0;i<table.getRowCount();i++){ //批量编辑
					String isChecked = String.valueOf(table.getValueAt(i, 1));
					if("true".equals(isChecked)){
						tablemodel.setValueAt(text, i, editingColumn);
					}
				}
			}
			return text;
		}
	}


	//检验表处理
	class checkEditor extends DefaultCellEditor {
		private static final long serialVersionUID = 1L;
		private JTable editingTable = null;
		private int editingRow = -1;
		private int editingColumn = -1;
		private JTextField checkField = null;
		checkEditor(JTextField textField) {
			super(textField);
			this.checkField = textField;
			this.checkField.setHorizontalAlignment(0);
			checkField.addKeyListener(new KeyAdapter(){
	            public void keyTyped(KeyEvent e) {
	                int keyChar = e.getKeyChar();
	                if(keyChar >= 45 && keyChar <= 57){
	                	if(keyChar == 47){
	                		e.consume();
	                	}
	                }else{
	                    e.consume(); //屏蔽掉非法输入
	                }
	            }
	        });
			this.checkField.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					currentText = checkField;
				}
			});
		}
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			this.editingTable = table;
			this.editingRow = row;
			this.editingColumn = column;
			this.checkField.setText("");
			if ((value != null) && ((value instanceof String))) {
				this.checkField.setText(value.toString());
			}
			return this.checkField;
		}
		public boolean isInt(String str){
			 boolean isInt = Pattern.compile("^-?[0-9]\\d*$").matcher(str).find();
			 return isInt;
		}

		public boolean isDouble(String str) {
			boolean isDouble = Pattern.compile("^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$").matcher(str).find();
			return isDouble;
		}
		public Object getCellEditorValue() {
			String text = this.checkField.getText();
			if(!"".equals(text) && text!=null){
				if(!text.contains("【")){
					String beginText = text.substring(0,1);
					String remainText = "";
					if(text.length()>1 && "-".equals(beginText)){
						remainText = text.substring(1);
					}
					if(!"-".equals(beginText)||"-".equals(beginText)){
						if (text == null || "".equals(text)){
							text = "";
						}else{
							if(!"-".equals(beginText)){ //开头为正
								if(!isDouble(text) && !isInt(text)){       // modify by lkc 2017.12.04
									JOptionPane.showMessageDialog(null, "只能填写正负浮点数或自然数");
									text = "";
								}
							}else { //开头为负
								if(!isDouble(remainText) && !isInt(remainText)){  // modify by lkc 2017.12.04

									{
										JOptionPane.showMessageDialog(null, "只能填写正负浮点数或自然数");
										text = "";
									}
								}
							}
						}
					}else{
						JOptionPane.showMessageDialog(null, "开头只能填写数字或'-'");
						text = "";
					}
				}

			}
			int clickRow = table.getSelectedRow();
			boolean isClick = (Boolean) table.getValueAt(clickRow, 1);
			if(isClick){
				for(int i=0;i<table.getRowCount();i++){ //批量编辑
					String isChecked = String.valueOf(table.getValueAt(i, 1));
					if("true".equals(isChecked)){
						tablemodel.setValueAt(text, i, editingColumn);
					}
				}
			}
			if("-0".equals(text)){
				text = "0";
			}
			return text;
		}
	}
	//记录表特殊字符
	class SWEditor extends DefaultCellEditor {
		private static final long serialVersionUID = 1L;
		final SpecialWordPanel panle = new SpecialWordPanel(frame, imageFolder);
		private JTable editingTable = null;
		private int editingRow = -1;
		private int editingColumn = -1;

		public SWEditor() {
			super(new JTextField());
			editorComponent = panle;
			delegate = new EditorDelegate() {
				private static final long serialVersionUID = 1L;
				@Override
				public Object getCellEditorValue() {
					return panle.getText();
				}
				@Override
				public void setValue(Object value) {
					panle.setText((value != null) ? CommonHelper.replaceReadSeperator(value.toString(),imageFolder) : "");
				}
			};
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			panle.setTechnicsPath(imageFolder);
			this.editingTable = table;
			this.editingRow = row;
			this.editingColumn = column;
			String text = CommonHelper.replaceReadSeperator(JavaUtil.convertNull(value), imageFolder);
			panle.setText(text);
			return this.panle;
		}

		@Override
		public Object getCellEditorValue() {
			String content = panle.getText();
			this.editingTable.setValueAt(content, this.editingRow, this.editingColumn);
			int clickRow = table.getSelectedRow();
			boolean isClick = (Boolean) table.getValueAt(clickRow, 1);
			if(isClick){
				for(int i=0;i<table.getRowCount();i++){ //批量编辑
					String isChecked = String.valueOf(table.getValueAt(i, 1));
					if("true".equals(isChecked)){
						tablemodel.setValueAt(content, i, editingColumn);
					}
				}
			}
			return panle.getText();
		}

		public void insertText(String str) {
			if (str != null && editorComponent != null) {
				((SpecialWordPanel) editorComponent).insertText(str);
			}
		}
	}

	class SWRenderer extends SpecialWordPanel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;
		private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();
		@SuppressWarnings("unchecked")
		private final Map cellSizes = new HashMap();
		public SWRenderer() {
			super(frame, imageFolder);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
			adaptee.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, column);
			setBorder(null);
			setFont(adaptee.getFont());
			setText(adaptee.getText());
			TableColumnModel columnModel = table.getColumnModel();
			setSize(columnModel.getColumn(column).getWidth(), 100000);
			int height_wanted = (int) getPreferredSize().getHeight();
			addSize(table, row, column, height_wanted);
			height_wanted = findTotalMaximumRowSize(table, row);
			if (height_wanted != table.getRowHeight(row)) {
				table.setRowHeight(row, height_wanted+7);
			}
			if (isSelected) {
				setBackgroundColor(table.getSelectionBackground());
			} else {
				setBackgroundColor(Color.WHITE);
			}
			return this;
		}
		@SuppressWarnings("unchecked")
		private void addSize(JTable table, int row, int column, int height) {
			Map rows = (Map) cellSizes.get(table);
			if (rows == null) {
				cellSizes.put(table, rows = new HashMap());
			}
			Map rowheights = (Map) rows.get(new Integer(row));
			if (rowheights == null) {
				rows.put(new Integer(row), rowheights = new HashMap());
			}
			rowheights.put(new Integer(column), new Integer(height));
		}
		@SuppressWarnings("unchecked")
		private int findTotalMaximumRowSize(JTable table, int row) {
			int maximum_height = 0;
			Enumeration columns = table.getColumnModel().getColumns();
			while (columns.hasMoreElements()) {
				TableColumn tc = (TableColumn) columns.nextElement();
				TableCellRenderer cellRenderer = tc.getCellRenderer();
				if (cellRenderer instanceof SWRenderer) {
					SWRenderer tar = (SWRenderer) cellRenderer;
					maximum_height = Math.max(maximum_height, tar.findMaximumRowSize(table, row));
				}
			}

			return maximum_height;
		}
		@SuppressWarnings("unchecked")
		private int findMaximumRowSize(JTable table, int row) {
			Map rows = (Map) cellSizes.get(table);
			if (rows == null)
				return 0;
			Map rowheights = (Map) rows.get(new Integer(row));
			if (rowheights == null)
				return 0;
			int maximum_height = 0;
			for (Iterator it = rowheights.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				int cellHeight = ((Integer) entry.getValue()).intValue();
				maximum_height = Math.max(maximum_height, cellHeight);
			}
			return maximum_height;
		}
	}

	public void setUIEnabled(boolean b) {
		addButton.setEnabled(b);
		removeButton.setEnabled(b);
		copyButton.setEnabled(b);
		pasteButton.setEnabled(b);
		importexeclButton.setEnabled(b);
		getexeclButton.setEnabled(b);
		gycsButton.setEnabled(b);
		saveButton.setEnabled(b);
		saveButton.setVisible(false);
	}

	public void addOneRow() {
		CommonUIUtil.addOneRow(tablemodel);
		int row = table.getRowCount() - 1;
		int columns = table.getColumnCount();
		for(int i = 0; i < columns; i++){
			String columnClass = table.getColumnClass(i).getName();
			if(columnClass.contains("Boolean")){
				table.setValueAt(false, row, i);
			}
		}
		for(int i=0;i<allRow;i++)
			table.setValueAt(i+1, i, 0);
	}

	public void removeRow() {
		int selectRows = table.getSelectedRows().length;//选中的行数
		int[] selRowIndexs = table.getSelectedRows();//选中的数组
		if (selectRows >= 1) {
			int del = 0;
			for(int i=0;i<selRowIndexs.length;i++){
				tablemodel.removeRow(selRowIndexs[i]-del);
				del += 1;
			}
		}
		for(int i=0;i<allRow;i++)
			table.setValueAt(i+1, i, 0);
	}

	public void copyRow(){
		int selRow = table.getSelectedRow();
		cellMap = new HashMap<String, List<String>>();
		int selectRows = table.getSelectedRows().length;
		int[] selRowIndexs = null;
		if(selectRows>=1){
			selRowIndexs=table.getSelectedRows();
			for(int j=0;j < selRowIndexs.length;j++){
				cellValue = new ArrayList<String>();
				selRow = selRowIndexs[j];
				if("检测类".equals(bzjlx_Value)){
					for(int i = 2; i < 7; i++){
						String value = (String) tablemodel.getValueAt(selRow, i);
						if(value==null){
							value = "";
						}
						cellValue.add(value);
					}
				}else if("记录类".equals(bzjlx_Value)){
					for(int i = 2; i < 5; i++){
						String value = (String) tablemodel.getValueAt(selRow, i);
						if(value==null){
							value = "";
						}
						cellValue.add(value);
					}
				}
				cellMap.put(String.valueOf(j), cellValue);
			}
		}
	}
	public void pasteRow(){
		int selRow = table.getSelectedRow();
		int selectRows = table.getSelectedRows().length;
		if(cellMap == null){//是否复制
			JOptionPane.showMessageDialog(null, "请先选择行复制");
			return ;
		}
		if(selectRows==1){//选择了一行
			if(cellMap.size()==1){//复制了一行
				cellValue = cellMap.get("0");
				if("检测类".equals(bzjlx_Value)){
					for(int i = 0; i < 5; i++){
						tablemodel.setValueAt(cellValue.get(i), selRow, i+2);
					}
				}else if("记录类".equals(bzjlx_Value)){
					for(int i = 0; i < 3; i++){
						tablemodel.setValueAt(cellValue.get(i), selRow, i+2);
					}
				}
			}
		}else{//选了很多行或没选行,粘贴到最下面
			for(int j=0;j<cellMap.size();j++){
				cellValue = cellMap.get(String.valueOf(j));
				addOneRow();
				if("检测类".equals(bzjlx_Value)){
					for(int i = 0; i < 5; i++){
						tablemodel.setValueAt(cellValue.get(i), allRow-1, i+2);
					}
				}else if("记录类".equals(bzjlx_Value)){
					for(int i = 0; i < 3; i++){
						tablemodel.setValueAt(cellValue.get(i), allRow-1, i+2);
					}
				}
			}
		}
		for(int i=0;i<allRow;i++)
			table.setValueAt(i+1, i, 0);
	}

	public void removeAllTableValues(){
		int rowCount = table.getRowCount();
		for(int i = 0; i < rowCount; i++){
			tablemodel.removeRow(0);
		}
	}

	public DefaultTableModel getTableModel() {
		return tablemodel;
	}

	public Element getObjElement() {
		return objElement;
	}

	public void setObjElement(Element objElement) {
		this.objElement = objElement;
	}
	public JTable getTable() {
		return table;
	}

	public JButton getAddButton() {
		return addButton;
	}

	public JButton getRemoveButton() {
		return removeButton;
	}

	public JButton getCopyButton() {
		return copyButton;
	}

	public JButton getPasteButton(){
		return pasteButton;
	}

	public JButton getImportexeclButton(){
		return importexeclButton;
	}

	public JButton getGetexeclButton(){
		return getexeclButton;
	}

	public JButton getGycsButton(){
		return gycsButton;
	}

	public JButton getSaveButton() {
		return saveButton;
	}

	public String getTableName(){
		return dybbm_Value;
	}

	public void setTableName(String dybbm_Value){
		this.dybbm_Value = dybbm_Value;
	}

	public String getTypeName(){
		return bzjlx_Value;
	}
    public String getId(){
    	return uuid_Value;
    }
	public String getXmm_Value(){
		return xmm_Value;
	}

	public void setXmm_Value(String xmm_Value){
		this.xmm_Value = xmm_Value;
	}

	public String getTbm_Value(){
		return tbm_Value;
	}

	public void setTbm_Value(String tbm_Value){
		this.tbm_Value = tbm_Value;
	}

	public String getXmm_Path() {
		return xmm_Path;
	}

	public void setXmm_Path(String xmm_Path) {
		this.xmm_Path = xmm_Path;
	}

	public String getTbm_Path() {
		return tbm_Path;
	}

	public void setTbm_Path(String tbm_Path) {
		this.tbm_Path = tbm_Path;
	}

	public String getMxCs_Value(){
		return mxCs_Value;
	}

	public void setMxCs_Value(String mxCs_Value){
		this.mxCs_Value = mxCs_Value;
	}

	public String[] getColumnNames(){
		return columnNames;

	}
	public String getTabTableName(){ // add by lkc 2017.12.12
		if(table.getSelectionModel().isSelectionEmpty()){
			return tabTableName;
		}else{
			return NewCheckParamTabbedPanel.getTabTableName();
		}

	}

	public List<String> getTableType(){
		ArrayList<String> list = new ArrayList<String>();
		list.add(dybbm_Value);
		list.add(bzjlx_Value);
		list.add(xmm_Value);
		list.add(tbm_Value);
		list.add(mxCs_Value);
		list.add(uuid_Value);
		list.add(xmm_Path);
		list.add(tbm_Path);
		return list;
	}

}
