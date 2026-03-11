package com.glaway.mpm.print.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.editor.FileSelectedCellEditor;
import com.glaway.mpm.print.editor.FileSelectedCellRenderer;
import com.glaway.mpm.print.listener.FileListTableModelListener;
import com.glaway.mpm.print.listener.FileListTableMouseListener;
import com.glaway.mpm.print.listener.ScanInputKeyListener;
import com.glaway.mpm.print.util.LoadPrintTable;
import com.glaway.mpm.print.util.LocalPrintUtil;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.CommonUtil;

public class FileListTable extends JPanel {

	private static final long serialVersionUID = 6970889010110137157L;
	private JTable table;
	private DefaultTableModel tableModel;
	/** 表头 */
	private String[] tableColumnName;
	private Class<?>[] tableColumnClass;
	/** 可编辑的列 */
	private int[] editableColumns = new int[] { };
	private int[] buttonColumns = new int[] { };
	private int[] columnsWidth = new int[] { };
	private String type;
	private String category;
	private Container component;
	private int height = 0;

	public FileListTable(Container component, String type, String category) {
		this.type = type;
		this.category = category;
		this.component = component;
		init();
		initComponent();
	}

	/**
	 * 初始化变量值
	 */
	private void init() {
		if(type.equals(PrintConstants.TITLE_MAINPANEL_JGYZQR) && "JC".equals(category)){//加盖印章检查
			this.tableColumnName = LoadPrintTable.getInstance().getTableColumnName(PrintConstants.TITLE_MAINPANEL_JGYZJC);//表头
			this.tableColumnClass = LoadPrintTable.getInstance().getTableColumnClass(PrintConstants.TITLE_MAINPANEL_JGYZJC);//表属性
			this.editableColumns = LoadPrintTable.getInstance().getTableEditableColumns(PrintConstants.TITLE_MAINPANEL_JGYZJC);
			this.buttonColumns = LoadPrintTable.getInstance().getTableButtonColumns(PrintConstants.TITLE_MAINPANEL_JGYZJC);
			this.columnsWidth = LoadPrintTable.getInstance().getTableColumnsWidth(PrintConstants.TITLE_MAINPANEL_JGYZJC);
			this.height = LoadPrintTable.getInstance().getTableHeight(PrintConstants.TITLE_MAINPANEL_JGYZJC);
		}else{
			// 根据打印类型初始化不同的列表
			this.tableColumnName = LoadPrintTable.getInstance().getTableColumnName(type);//表头
			this.tableColumnClass = LoadPrintTable.getInstance().getTableColumnClass(type);//表属性
			this.editableColumns = LoadPrintTable.getInstance().getTableEditableColumns(type);
			this.buttonColumns = LoadPrintTable.getInstance().getTableButtonColumns(type);
			this.columnsWidth = LoadPrintTable.getInstance().getTableColumnsWidth(type);
			this.height = LoadPrintTable.getInstance().getTableHeight(type);
		}
	}

	private void initComponent() {
		tableModel = new CommonTableModel();
		for (String columnName : tableColumnName) {
			tableModel.addColumn(columnName);
		}

		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setModel(tableModel);
		table.addMouseListener(new FileListTableMouseListener(table, type, this, component));
		table.addKeyListener(new ScanInputKeyListener());
		try {
			table.setDefaultRenderer(Class.forName("java.lang.Object"), new FileListTableCellRenderer(type));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		tableModel.addTableModelListener(new FileListTableModelListener(this, type));

		for (int cloumn : buttonColumns) {
			table.getColumnModel().getColumn(cloumn).setCellEditor(new FileSelectedCellEditor(table));
			table.getColumnModel().getColumn(cloumn).setCellRenderer(new FileSelectedCellRenderer(table));
		}

		table.setRowHeight(30);

		for(int i = 0; i < table.getColumnCount(); i++) {
			int columnWidth = columnsWidth[i];
			if (columnWidth > 0) {
				CommonUIUtil.setColumnPrefferredWidth(table, i, columnWidth);
			} else if (columnWidth == 0) {
				CommonUIUtil.hiddenCell(table, i);
			}
		}

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(table);

		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;

		scrollPane.setPreferredSize(new Dimension(width / 2, height/3));
		setLayout(new VFlowLayout(0, 0, 0, true, true));
		add(scrollPane);
	}

	public JTable getTable() {
		return table;
	}

	public Container getComponent() {
		return component;
	}

	public String getType() {
		return type;
	}

	public String getCategory(){
		return category;
	}

	class CommonTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (tableColumnClass != null) {
				return tableColumnClass[columnIndex];
			} else {
				return super.getColumnClass(columnIndex);
			}
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			for (int i : editableColumns) {
				if (i == column) {
					return true;
				}
			}
			return false;
		}
	}

	class TableColumnPropertyListener implements PropertyChangeListener {

		private int column;
		private int columnWidth;

		public TableColumnPropertyListener(int column, int columnWidth) {
			super();
			this.column = column;
			this.columnWidth = columnWidth;
		}

		@Override
		public void propertyChange(PropertyChangeEvent e) {
			int columnWidth = table.getColumnModel().getColumn(this.column).getWidth();
			if (columnWidth > this.columnWidth) {
				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			} else {
				int width = (int) table.getSize().getWidth();
				int frameWidth = MPMPrintFileFrame.getFrameWidth();
				if (width < frameWidth) {
					table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
				}
			}
		}

	}

	public void removeRow(List<Integer> list) {
		for (int i = 0; i < list.size(); i++) {
			tableModel.removeRow(list.get(i) - i);
		}
	}

	public void setUIValues(List<CmPrintInfoBean> printInfoBeanList) {
		if(printInfoBeanList == null){
			return;
		}
		Collections.sort(printInfoBeanList);
		tableModel.setRowCount(0);
		if (type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST)) {
			setFileRequestValues(printInfoBeanList);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_ADDREQUEST)){
			setAddPrintApplicationValues(printInfoBeanList);
		} else if(type.equals(PrintConstants.TITLE_ADDFILE)){
			setAddFileValues(printInfoBeanList);
		} else if(type.equals(PrintConstants.TITLE_ADDONBOM_PART)){
			setAddFilesOnBom(printInfoBeanList);
		} else if(type.equals(PrintConstants.TITLE_ADDONBOM_PART_RELATE)){
			setAddBomFiles(printInfoBeanList);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_YLDYSQ)){
			setYLDYSQValues(printInfoBeanList);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_ZXDYSQ)){
			setZXDYSQValues(printInfoBeanList);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_LQZZWJ)){
			setLQZZWJValues(printInfoBeanList);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_JGYZGL_RELATE)){
			setJGSearchValues(printInfoBeanList);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_JGYZQR)){
			setJGYZQRValues(printInfoBeanList);
		} else if(type.equals(PrintConstants.TITLE_ADDONPROCESSDIRECTORY)){
			setGYWJMLValues(printInfoBeanList);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_WLWJLR)){
			setWLWJLRValues(printInfoBeanList);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_JGYZGL)){
			setAddJGYZGLValues(printInfoBeanList);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_ADDWJBDSQ)){
			setADDWJBDSQValues(printInfoBeanList);
		}
	}

	private void setADDWJBDSQValues(List<CmPrintInfoBean> printInfoBeanList) {
		if(printInfoBeanList != null){
			for (CmPrintInfoBean printInfoBean : printInfoBeanList) {
				CommonUIUtil.addOneRow(tableModel);
				int row = table.getRowCount() - 1;
				tableModel.setValueAt(printInfoBean.getOid(), row, 0);
				tableModel.setValueAt(false, row, 1);
				tableModel.setValueAt(printInfoBean.getFileNumber(), row, 2);
				tableModel.setValueAt(printInfoBean.getFileName(), row, 3);
				tableModel.setValueAt(printInfoBean.getVersion(), row, 4);
				tableModel.setValueAt(printInfoBean.getPhaseCode(), row, 5);
				tableModel.setValueAt(printInfoBean.getSecret(), row, 6);
				tableModel.setValueAt(printInfoBean.getTemporarySeal(), row, 7);
				tableModel.setValueAt(printInfoBean.getDistributeDeptAndCount(), row, 8);
				tableModel.setValueAt(printInfoBean.getContainerName(), row, 9);
				tableModel.setValueAt(printInfoBean.getFileState(), row, 10);
				tableModel.setValueAt(printInfoBean.getLifeCycle(), row, 11);
				tableModel.setValueAt(printInfoBean.getPbooid(), row, 12);
				tableModel.setValueAt(printInfoBean, row, 13);
			}
		}
	}

	private void setYLDYSQValues(List<CmPrintInfoBean> printInfoBeanList) {
		if(printInfoBeanList != null){
			for (CmPrintInfoBean printInfoBean : printInfoBeanList) {
				if(verifyRequestTable(table, printInfoBean)){
					CommonUIUtil.addOneRow(tableModel);
					int row = table.getRowCount() - 1;
					tableModel.setValueAt(printInfoBean.getOid(), row, 0);
					tableModel.setValueAt(false, row, 1);
					tableModel.setValueAt(printInfoBean.getFileNumber(), row, 2);
					tableModel.setValueAt(printInfoBean.getFileName(), row, 3);
					tableModel.setValueAt(printInfoBean.getVersion(), row, 4);
					tableModel.setValueAt(printInfoBean.getPhaseCode(), row, 5);
					tableModel.setValueAt(printInfoBean.getSecret(), row, 6);
					tableModel.setValueAt(printInfoBean.getTemporarySeal(), row, 7);
					tableModel.setValueAt(printInfoBean.getDistributeDeptAndCount(), row, 8);
					tableModel.setValueAt(printInfoBean.getDocVR(), row, 9);
					tableModel.setValueAt(printInfoBean, row, 10);
				}
			}
		}
	}

	private void setWLWJLRValues(List<CmPrintInfoBean> printInfoBeanList) {
		if(printInfoBeanList != null){
			for (CmPrintInfoBean printInfoBean : printInfoBeanList) {
					CommonUIUtil.addOneRow(tableModel);
					int row = table.getRowCount() - 1;
					tableModel.setValueAt(printInfoBean.getOid(), row, 0);
					tableModel.setValueAt(false, row, 1);
					tableModel.setValueAt(printInfoBean.getFileNumber(), row, 2);
					tableModel.setValueAt(printInfoBean.getFileName(), row, 3);
					tableModel.setValueAt(printInfoBean.getVersion(), row, 4);
					tableModel.setValueAt(printInfoBean.getPhaseCode(), row, 5);
					tableModel.setValueAt(printInfoBean.getSecret(), row, 6);
					tableModel.setValueAt(printInfoBean.getPageCount(), row, 7);
					tableModel.setValueAt(printInfoBean.getContainerName(), row, 8);
					tableModel.setValueAt(printInfoBean, row, 9);
			}
		}
	}

	private void setGYWJMLValues(List<CmPrintInfoBean> printInfoBeanList) {
		if(printInfoBeanList != null){
			for (CmPrintInfoBean printInfoBean : printInfoBeanList) {
					CommonUIUtil.addOneRow(tableModel);
					int row = table.getRowCount() - 1;
					tableModel.setValueAt(printInfoBean.getOid(), row, 0);
					tableModel.setValueAt(printInfoBean.getFileNumber(), row, 1);
					tableModel.setValueAt(printInfoBean.getFileName(), row, 2);
					tableModel.setValueAt(printInfoBean.getVersion(), row, 3);
					tableModel.setValueAt(printInfoBean.getPhaseCode(), row, 4);
					tableModel.setValueAt(printInfoBean.getSecret(), row, 5);
					tableModel.setValueAt(printInfoBean.getContainerName(), row, 6);
					tableModel.setValueAt(printInfoBean.getFileState(), row, 7);
					tableModel.setValueAt(printInfoBean.getLifeCycle(), row, 8);
					tableModel.setValueAt(printInfoBean, row, 9);
			}
		}
	}

	private void setJGYZQRValues(List<CmPrintInfoBean> printInfoBeanList) {
//		int index = table.getRowCount()+1;
		if(printInfoBeanList != null){
			for (CmPrintInfoBean printInfoBean : printInfoBeanList) {
//				if(verifyRequestTable(table, printInfoBean)){
				CommonUIUtil.addOneRow(tableModel);
				int row = table.getRowCount() - 1;
				if("JC".equals(category)){
					tableModel.setValueAt(printInfoBean.getOid(), row, 0);
					tableModel.setValueAt(false, row, 1);
					tableModel.setValueAt(printInfoBean.getFileNumber(), row, 2);
					tableModel.setValueAt(printInfoBean.getFileName(), row, 3);
					tableModel.setValueAt(printInfoBean.getVersion(), row, 4);
					tableModel.setValueAt(printInfoBean.getPhaseCode(), row, 5);
					tableModel.setValueAt(printInfoBean.getSecret(), row, 6);
					tableModel.setValueAt(printInfoBean.getPrintDate(), row, 7);
					tableModel.setValueAt(printInfoBean.getDistributeDeptAndCount(), row, 8);
					tableModel.setValueAt(printInfoBean.getTemporarySeal(), row, 9);
					tableModel.setValueAt(printInfoBean.getSealPlus(), row, 10);
					tableModel.setValueAt(printInfoBean.getQrName(), row, 11);
					tableModel.setValueAt(printInfoBean, row, 12);
				}else{
					tableModel.setValueAt(printInfoBean.getOid(), row, 0);
					tableModel.setValueAt(printInfoBean.getFileNumber(), row, 1);
					tableModel.setValueAt(printInfoBean.getFileName(), row, 2);
					tableModel.setValueAt(printInfoBean.getVersion(), row, 3);
					tableModel.setValueAt(printInfoBean.getPhaseCode(), row, 4);
					tableModel.setValueAt(printInfoBean.getSecret(), row, 5);
					tableModel.setValueAt(printInfoBean.getPrintDate(), row, 6);
					tableModel.setValueAt(printInfoBean.getDistributeDeptAndCount(), row, 7);
					tableModel.setValueAt(printInfoBean.getTemporarySeal(), row, 8);
					tableModel.setValueAt(printInfoBean.getSealPlus(), row, 9);
					tableModel.setValueAt(printInfoBean.getQrName(), row, 10);
					tableModel.setValueAt(printInfoBean, row, 11);
				}
//					index++;
//				}
			}
		}
	}

	private void setJGSearchValues(List<CmPrintInfoBean> printInfoBeanList) {
		if(printInfoBeanList != null){
			for (CmPrintInfoBean printInfoBean : printInfoBeanList) {
					CommonUIUtil.addOneRow(tableModel);
					int row = table.getRowCount() - 1;
					tableModel.setValueAt(printInfoBean.getOid(), row, 0);
					tableModel.setValueAt(false, row, 1);
					tableModel.setValueAt(printInfoBean.getFileNumber(), row, 2);
					tableModel.setValueAt(printInfoBean.getFileName(), row, 3);
					tableModel.setValueAt(printInfoBean.getVersion(), row, 4);
					tableModel.setValueAt(printInfoBean.getPhaseCode(), row, 5);
					tableModel.setValueAt(printInfoBean.getSecret(), row, 6);
					tableModel.setValueAt(printInfoBean.getPrintDate(), row, 7);
					tableModel.setValueAt(printInfoBean.getDistributeDeptAndCount(), row, 8);
					tableModel.setValueAt(printInfoBean.getQrName(), row, 9);
					tableModel.setValueAt(printInfoBean.getTemporarySeal(), row, 10);
					tableModel.setValueAt(printInfoBean.getFileState(), row, 11);
					tableModel.setValueAt(printInfoBean.getContainerName(), row, 12);
					tableModel.setValueAt(printInfoBean, row, 13);
			}
		}
	}

	private void setLQZZWJValues(List<CmPrintInfoBean> printInfoBeanList) {
		if(printInfoBeanList != null){
			for (CmPrintInfoBean printInfoBean : printInfoBeanList) {
				if(verifyRequestTable(table, printInfoBean)){
					CommonUIUtil.addOneRow(tableModel);
					int row = table.getRowCount() - 1;
					tableModel.setValueAt(printInfoBean.getOid(), row, 0);
					tableModel.setValueAt(false, row, 1);
					tableModel.setValueAt(printInfoBean.getFileNumber(), row, 2);
					tableModel.setValueAt(printInfoBean.getFileName(), row, 3);
					tableModel.setValueAt(printInfoBean.getVersion(), row, 4);
					tableModel.setValueAt(printInfoBean.getPhaseCode(), row, 5);
					tableModel.setValueAt(printInfoBean.getSecret(), row, 6);
					tableModel.setValueAt(printInfoBean.getTemporarySeal(), row, 7);
					tableModel.setValueAt(printInfoBean.getQrName(), row, 8);
					tableModel.setValueAt(printInfoBean.getFileState(), row, 9);
					tableModel.setValueAt(printInfoBean.getReceiptDept(), row, 10);
					tableModel.setValueAt(printInfoBean.getReceipter(), row, 11);
					tableModel.setValueAt(printInfoBean.getReceiptDate(), row, 12);
					tableModel.setValueAt(null, row, 13);
					tableModel.setValueAt(printInfoBean, row, 14);
				}
			}
		}
	}

	public void setZXDYSQValues(List<CmPrintInfoBean> printInfoBeanList) {
		if(printInfoBeanList != null){
			for (CmPrintInfoBean printInfoBean : printInfoBeanList) {
				if(verifyRequestTable(table, printInfoBean)){
					CommonUIUtil.addOneRow(tableModel);
					int row = table.getRowCount() - 1;
					tableModel.setValueAt(printInfoBean.getOid(), row, 0);
					tableModel.setValueAt(false, row, 1);
					tableModel.setValueAt(printInfoBean.getFileNumber(), row, 2);
					tableModel.setValueAt(printInfoBean.getFileName(), row, 3);
					tableModel.setValueAt(printInfoBean.getVersion(), row, 4);
					tableModel.setValueAt(printInfoBean.getPhaseCode(), row, 5);
					tableModel.setValueAt(printInfoBean.getSecret(), row, 6);
					tableModel.setValueAt(printInfoBean.getTemporarySeal(), row, 7);
					tableModel.setValueAt(printInfoBean.getDistributeDeptAndCount(), row, 8);
					tableModel.setValueAt(printInfoBean.getQrCode(), row, 9);
					tableModel.setValueAt(printInfoBean.getFileState(), row, 10);
					tableModel.setValueAt(printInfoBean.getPrinter(), row, 11);
					tableModel.setValueAt(printInfoBean.getPrintDate(), row, 12);
					tableModel.setValueAt(printInfoBean.getDocVR(), row, 13);
					tableModel.setValueAt(printInfoBean, row, 14);
				}
			}
		}
	}

	public void setAddValues(List<CmPrintInfoBean> printInfoBeanList) {
		if(printInfoBeanList == null){
			return;
		}
		Collections.sort(printInfoBeanList);
		if (type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST)) {
			setFileRequestValues(printInfoBeanList);
		} else if(type.equals(PrintConstants.TITLE_ADDFILE)){
			setAddFileValues(printInfoBeanList);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_JGYZGL)){
			setAddJGYZGLValues(printInfoBeanList);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_WLWJLR)){
			setAddWLWJLRValues(printInfoBeanList);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_WJBDSQ)){
			setAddWJBDSQValues(printInfoBeanList);
		}
	}

	private void setAddWJBDSQValues(List<CmPrintInfoBean> printInfoBeanList) {
		if(printInfoBeanList != null){
			for (CmPrintInfoBean printInfoBean : printInfoBeanList) {
				if(verifyRequestTable(table, printInfoBean)){
					CommonUIUtil.addOneRow(tableModel);
					int row = table.getRowCount() - 1;
					tableModel.setValueAt(printInfoBean.getOid(), row, 0);
					tableModel.setValueAt(false, row, 1);
					tableModel.setValueAt(printInfoBean.getFileNumber(), row, 2);
					tableModel.setValueAt(printInfoBean.getFileName(), row, 3);
					tableModel.setValueAt(printInfoBean.getVersion(), row, 4);
					tableModel.setValueAt(printInfoBean.getPhaseCode(), row, 5);
					tableModel.setValueAt(printInfoBean.getSecret(), row, 6);
					tableModel.setValueAt(printInfoBean.getTemporarySeal(), row, 7);
					tableModel.setValueAt(printInfoBean.getDistributeDeptAndCount(), row, 8);
					tableModel.setValueAt("", row, 9);
					tableModel.setValueAt(printInfoBean.getContainerName(), row, 10);
					tableModel.setValueAt(printInfoBean.getFileState(), row, 11);
					tableModel.setValueAt(printInfoBean.getLifeCycle(), row, 12);
					tableModel.setValueAt(printInfoBean.getPbooid(), row, 13);
					tableModel.setValueAt(printInfoBean, row, 14);
				}
			}
		}
	}

	private void setAddWLWJLRValues(List<CmPrintInfoBean> printInfoBeanList) {
		for(CmPrintInfoBean printInfoBean : printInfoBeanList){
			CommonUIUtil.addOneRow(tableModel);
			int row = table.getRowCount() - 1;
			tableModel.setValueAt(printInfoBean.getOid(), row, 0);
			tableModel.setValueAt(false, row, 1);
			tableModel.setValueAt(printInfoBean.getFileNumber(), row, 2);
			tableModel.setValueAt(printInfoBean.getFileName(), row, 3);
			tableModel.setValueAt(printInfoBean.getVersion(), row, 4);
			tableModel.setValueAt(printInfoBean.getPhaseCode(), row, 5);
			tableModel.setValueAt(printInfoBean.getSecret(), row, 6);
			tableModel.setValueAt(printInfoBean.getPageCount(), row, 7);
			tableModel.setValueAt(printInfoBean.getContainerName(), row, 8);
			tableModel.setValueAt(printInfoBean, row, 9);
		}
	}

	private void setAddJGYZGLValues(List<CmPrintInfoBean> printInfoBeanList) {
		print:for (CmPrintInfoBean printInfoBean : printInfoBeanList) {
			int tableRow = table.getRowCount();
			if(tableRow != 0){
				for(int i = 0;i < tableRow;i++){
					String oid = (String) table.getValueAt(i, 0);
					if(oid.equals(printInfoBean.getOid())){
						break print;
					}
				}
			}
			CommonUIUtil.addOneRow(tableModel);
			int row = table.getRowCount() - 1;
			tableModel.setValueAt(printInfoBean.getOid(), row, 0);
			tableModel.setValueAt(false, row, 1);
			tableModel.setValueAt(printInfoBean.getFileNumber(), row, 2);
			tableModel.setValueAt(printInfoBean.getFileName(), row, 3);
			tableModel.setValueAt(printInfoBean.getVersion(), row, 4);
			tableModel.setValueAt(printInfoBean.getPhaseCode(), row, 5);
			tableModel.setValueAt(printInfoBean.getSecret(), row, 6);
			tableModel.setValueAt(printInfoBean.getPrintDate(), row, 7);
			tableModel.setValueAt(printInfoBean.getDistributeDeptAndCount(), row, 8);
			tableModel.setValueAt(printInfoBean.getQrName(), row, 9);
			tableModel.setValueAt(printInfoBean.getTemporarySeal(), row, 10);
			tableModel.setValueAt(printInfoBean.getSealPlus(), row, 11);
			tableModel.setValueAt(printInfoBean.getFileState(), row, 12);
			tableModel.setValueAt(printInfoBean.getContainerName(), row, 13);
			tableModel.setValueAt(printInfoBean, row, 14);
		}
	}

	public void setFileRequestValues(List<CmPrintInfoBean> printInfoBeanList) {
		if(printInfoBeanList != null){
			for (CmPrintInfoBean printInfoBean : printInfoBeanList) {
				if(verifyRequestTable(table, printInfoBean)){
					CommonUIUtil.addOneRow(tableModel);
					int row = table.getRowCount() - 1;
					tableModel.setValueAt(printInfoBean.getOid(), row, 0);
					tableModel.setValueAt(false, row, 1);
					tableModel.setValueAt(printInfoBean.getFileNumber(), row, 2);
					tableModel.setValueAt(printInfoBean.getFileName(), row, 3);
					tableModel.setValueAt(printInfoBean.getVersion(), row, 4);
					tableModel.setValueAt(printInfoBean.getPhaseCode(), row, 5);
					tableModel.setValueAt(printInfoBean.getSecret(), row, 6);
					tableModel.setValueAt(printInfoBean.getTemporarySeal(), row, 7);
					if(printInfoBean.getDistributeDeptAndCount() != null && !"".equals(printInfoBean.getDistributeDeptAndCount())){
						tableModel.setValueAt(printInfoBean.getDistributeDeptAndCount(), row, 8);
					}else if("null".equals(printInfoBean.getDistributeDeptAndCount())){
						tableModel.setValueAt(printInfoBean.getDistributeDeptAndCount(), row, 8);
					}else{
						String fileType = printInfoBean.getFileType();
						if(fileType != null && !"".equals(fileType)){
//							if(fileType.equals(PrintConstants.FILETYPE_GYGC) || fileType.equals(PrintConstants.FILETYPE_GYJSTZD)){
							String compileDept = printInfoBean.getCompileDept();
							if(compileDept != null){
								Pattern pattern = Pattern.compile("^[0-9]*$");
								boolean isInt = pattern.matcher(compileDept).matches();
									if(compileDept != null && !"".equals(compileDept) && isInt){
										String dept = LocalPrintUtil.changeIntToStr(Integer.parseInt(compileDept));
										String deptAndCount = dept + "分厂:1份";
										tableModel.setValueAt(deptAndCount, row, 8);
//									}
							}
							}
						}
					}
					tableModel.setValueAt(printInfoBean.getContainerName(), row, 9);
					tableModel.setValueAt(printInfoBean.getFileState(), row, 10);
					tableModel.setValueAt(printInfoBean.getLifeCycle(), row, 11);
					tableModel.setValueAt(printInfoBean.getPbooid(), row, 12);
					tableModel.setValueAt(printInfoBean, row, 13);
				}
			}
		}
	}

	public boolean verifyRequestTable(JTable table, CmPrintInfoBean printInfoBean) {
		boolean flag = true;
		for (int row = 0; row < table.getRowCount(); row++) {
			String id = CommonUtil.objectToString(table.getValueAt(row, 0));
			if (id.equals(CommonUtil.objectToString(printInfoBean.getOid()))) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	public boolean verifyDestroyRequestTable(JTable table, CmPrintInfoBean printInfoBean) {
		boolean flag = true;
		for (int row = 0; row < table.getRowCount(); row++) {
			String barCode = CommonUtil.objectToString(table.getValueAt(row, 7));
			if (barCode.equals(printInfoBean.getQrCode())) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	public void setAddFileValues(List<CmPrintInfoBean> printInfoBeanList) {
		int index = 1;
		for (CmPrintInfoBean printInfoBean : printInfoBeanList) {
			CommonUIUtil.addOneRow(tableModel);
			int row = table.getRowCount() - 1;
			tableModel.setValueAt(printInfoBean.getOid(), row, 0);
			tableModel.setValueAt(false, row, 1);
			tableModel.setValueAt(index, row, 2);
			tableModel.setValueAt(printInfoBean.getFileNumber(), row, 3);
			tableModel.setValueAt(printInfoBean.getFileName(), row, 4);
			tableModel.setValueAt(printInfoBean.getPindex(), row, 5);
			tableModel.setValueAt(printInfoBean.getVersion(), row, 6);
			tableModel.setValueAt(printInfoBean.getPhaseCode(), row, 7);
			tableModel.setValueAt(printInfoBean.getFileType(), row, 8);
			tableModel.setValueAt(printInfoBean.getSecret(), row, 9);
			tableModel.setValueAt(printInfoBean.getModifior(), row, 10);
			tableModel.setValueAt(printInfoBean.getApproveDate(), row, 11);
			tableModel.setValueAt(printInfoBean.getFileState(), row, 12);
			tableModel.setValueAt(printInfoBean.getDistributeDeptAndCount(), row, 13);
			tableModel.setValueAt(printInfoBean, row, 14);
			index++;
		}
	}

	public void setAddPrintApplicationValues(List<CmPrintInfoBean> printInfoBeanList) {
		for (CmPrintInfoBean printInfoBean : printInfoBeanList) {
			CommonUIUtil.addOneRow(tableModel);
			int row = table.getRowCount() - 1;
			tableModel.setValueAt(printInfoBean.getOid(), row, 0);
			tableModel.setValueAt(false, row, 1);
			tableModel.setValueAt(printInfoBean.getFileNumber(), row, 2);
			tableModel.setValueAt(printInfoBean.getFileName(), row, 3);
			tableModel.setValueAt(printInfoBean.getVersion(), row, 4);
			tableModel.setValueAt(printInfoBean.getPhaseCode(), row, 5);
			tableModel.setValueAt(printInfoBean.getSecret(), row, 6);
			tableModel.setValueAt(printInfoBean.getDistributeDeptAndCount(), row, 7);//分发情况
			tableModel.setValueAt(printInfoBean.getContainerName(), row, 8);
			tableModel.setValueAt(printInfoBean.getFileState(), row, 9);
			tableModel.setValueAt(printInfoBean.getLifeCycle(), row, 10);
			tableModel.setValueAt(printInfoBean, row, 11);
		}
	}

	public void setAddFilesOnBom(List<CmPrintInfoBean> printInfoBeanList) {
		int index = 1;
		for (CmPrintInfoBean printInfoBean : printInfoBeanList) {
			CommonUIUtil.addOneRow(tableModel);
			int row = table.getRowCount() - 1;
			tableModel.setValueAt(printInfoBean.getOid(), row, 0);
			tableModel.setValueAt(index, row, 1);
			tableModel.setValueAt(printInfoBean.getFileNumber(), row, 2);
			tableModel.setValueAt(printInfoBean.getFileName(), row, 3);
			tableModel.setValueAt(printInfoBean.getVersion(), row, 4);
			tableModel.setValueAt(printInfoBean.getPhaseCode(), row, 5);
			String hasFound = "";
			if(printInfoBean.getMainTechnics() != null && !"".equals(printInfoBean.getMainTechnics())){
				hasFound = "有";
			}else{
				hasFound = "无";
			}
			tableModel.setValueAt(hasFound, row, 6);
			tableModel.setValueAt(printInfoBean, row, 7);
			index++;
		}
	}

	public void setAddBomFiles(List<CmPrintInfoBean> printInfoBeanList) {
		for (CmPrintInfoBean printInfoBean : printInfoBeanList) {
			CommonUIUtil.addOneRow(tableModel);
			int row = table.getRowCount() - 1;
			tableModel.setValueAt(printInfoBean.getOid(), row, 0);
			tableModel.setValueAt(false, row, 1);
			tableModel.setValueAt(printInfoBean.getFileNumber(), row, 2);
			tableModel.setValueAt(printInfoBean.getFileName(), row, 3);
			tableModel.setValueAt(printInfoBean.getVersion(), row, 4);
			tableModel.setValueAt(printInfoBean.getPhaseCode(), row, 5);
			tableModel.setValueAt(printInfoBean.getSecret(), row, 6);
			tableModel.setValueAt(printInfoBean.getDistributeDeptAndCount(), row, 7);
			tableModel.setValueAt(printInfoBean.getContainerName(), row, 8);
			tableModel.setValueAt(printInfoBean.getFileState(), row, 9);
			tableModel.setValueAt(printInfoBean.getLifeCycle(), row, 10);
			tableModel.setValueAt(printInfoBean, row, 11);
		}
	}

	public void setFileZXDYGLPrintValues(List<CmPrintInfoBean> printInfoBeanList) {
		for (CmPrintInfoBean printInfoBean : printInfoBeanList) {
			if(verifyRequestTable(table, printInfoBean)){
				CommonUIUtil.addOneRow(tableModel);
				int row = table.getRowCount() - 1;
				tableModel.setValueAt(printInfoBean.getOid(), row, 0);
				tableModel.setValueAt(printInfoBean.getFileNumber(), row, 1);
				tableModel.setValueAt(printInfoBean.getFileName(), row, 2);
				tableModel.setValueAt(printInfoBean.getPindex(), row, 3);
				tableModel.setValueAt(printInfoBean.getVersion(), row, 4);
				tableModel.setValueAt(printInfoBean.getPhaseCode(), row, 5);
				tableModel.setValueAt(printInfoBean.getFileType(), row, 6);
				tableModel.setValueAt(printInfoBean.getPageCount(), row, 7);
				tableModel.setValueAt(printInfoBean.getSecret(), row, 8);
				tableModel.setValueAt(printInfoBean.getQrCode(), row, 9);
				tableModel.setValueAt(printInfoBean.getPrinter(), row, 10);
				tableModel.setValueAt(printInfoBean.getPrintDate(), row, 11);
			}
		}
	}
	//删除时序号重新编排_20170331_jiangyixing
	public void repaintTable(){
		int index = 1;
		for (int row = 0; row < table.getRowCount(); row++) {
			tableModel.setValueAt(index, row, 2);
			index++;
		}
	}

	public void setAddValues(CmPrintInfoBean cmPrintInfoBean) {
		if(type.equals(PrintConstants.TITLE_MAINPANEL_WLWJLR)){
			setAddWLWJLRValues(cmPrintInfoBean);
		}
	}

	private void setAddWLWJLRValues(CmPrintInfoBean cmPrintInfoBean) {
		CommonUIUtil.addOneRow(tableModel);
		int row = table.getRowCount() - 1;
		tableModel.setValueAt(cmPrintInfoBean.getOid(), row, 0);
		tableModel.setValueAt(false, row, 1);
		tableModel.setValueAt(cmPrintInfoBean.getFileNumber(), row, 2);
		tableModel.setValueAt(cmPrintInfoBean.getFileName(), row, 3);
		tableModel.setValueAt(cmPrintInfoBean.getVersion(), row, 4);
		tableModel.setValueAt(cmPrintInfoBean.getPhaseCode(), row, 5);
		tableModel.setValueAt(cmPrintInfoBean.getSecret(), row, 6);
		tableModel.setValueAt(cmPrintInfoBean.getPageCount(), row, 7);
		tableModel.setValueAt(cmPrintInfoBean.getContainerName(), row, 8);
		tableModel.setValueAt(cmPrintInfoBean, row, 9);
	}

	public void setUIValues(CmPrintInfoBean cmPrintInfoBean) {
		if(type.equals(PrintConstants.TITLE_MAINPANEL_WLWJLR)){
			setWLWJLRValues(cmPrintInfoBean);
		}
	}

	private void setWLWJLRValues(CmPrintInfoBean cmPrintInfoBean) {
		for (int row = 0; row < table.getRowCount(); row++) {
			boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
			if(isSelect){
				tableModel.setValueAt(cmPrintInfoBean.getOid(), row, 0);
				tableModel.setValueAt(true, row, 1);
				tableModel.setValueAt(cmPrintInfoBean.getFileNumber(), row, 2);
				tableModel.setValueAt(cmPrintInfoBean.getFileName(), row, 3);
				tableModel.setValueAt(cmPrintInfoBean.getVersion(), row, 4);
				tableModel.setValueAt(cmPrintInfoBean.getPhaseCode(), row, 5);
				tableModel.setValueAt(cmPrintInfoBean.getSecret(), row, 6);
				tableModel.setValueAt(cmPrintInfoBean.getPageCount(), row, 7);
				tableModel.setValueAt(cmPrintInfoBean.getContainerName(), row, 8);
				tableModel.setValueAt(cmPrintInfoBean, row, 9);
			}
		}
	}
}
