package com.glaway.mpm.parameter.designui;

import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.wcIntf.TechnicsIntf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.*;

public class SearchPhotoPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JFrame frame;
	private JPanel topMainPanel;
	private JPanel mainPanel;
	private JPanel middleMainPanel;
	private JPanel imagePanel;

	private JButton searchButton;
	private JButton clearButton;

	private JLabel photoNumberLabel;
	private JTextField photoNumberTextField;
	private JLabel photoNameLabel;
	private JTextField photoNameTextField;
	private JLabel productNumberLabel;
	private JTextField productNumberTextField;
	private JLabel psyqLabel;
	private JTextField psyqTextField;
	private JLabel pbzzLabel;
	private JTextField pbzzTextField;
	private JLabel psdxTypeLabel;
	private JTextField psdxTypeTextField;



	private JLabel pdcjmcLabel;
	private JComboBox pdcjmcComboBox;

	private JLabel photoTypeLabel;
	private JComboBox photoTypeComboBox;

	private JTable photoTable;
	private DefaultTableModel photoTableModel;
	private JTable insertTable;
	private DefaultTableModel insertTableModel;
	private JButton sureButton;
	private JButton cancelButton;

	public static Vector<Map<String, String>> equipments;
	private JDialog dialog;
	private PhotoRecordTablePanel photoRecordTablePanel;
	private HashMap<String, ArrayList<HashMap<String, String>>> photoRecordList = null;

	public SearchPhotoPanel(JDialog dialog, PhotoRecordTablePanel panel, JFrame frame) {
		this.photoRecordTablePanel = panel;
		this.dialog = dialog;
		this.frame = frame;
		init();
	}

	private void init() {
		initComponents();
		initLayout();
		initActions();
	}

	private void initComponents() {
		topMainPanel = new JPanel();
		middleMainPanel = new JPanel();
		mainPanel = new JPanel();
		imagePanel = new JPanel();

		photoNumberLabel = new JLabel("编号:");
		photoNumberTextField = new JTextField();
		photoNumberTextField.setPreferredSize(new Dimension(200, 25));

		photoNameLabel = new JLabel("样张名称:");
		photoNameTextField = new JTextField();
		photoNameTextField.setPreferredSize(new Dimension(200, 25));

		productNumberLabel = new JLabel("产品图号:");
		productNumberTextField = new JTextField();
		productNumberTextField.setPreferredSize(new Dimension(200, 25));

		psyqLabel = new JLabel("拍摄要求:");
		psyqTextField = new JTextField();
		psyqTextField.setPreferredSize(new Dimension(200, 25));

		pbzzLabel = new JLabel("判别准则:");
		pbzzTextField = new JTextField();
		pbzzTextField.setPreferredSize(new Dimension(200, 25));

		psdxTypeLabel = new JLabel("拍摄对象分类:");
		psdxTypeTextField = new JTextField();
		psdxTypeTextField.setPreferredSize(new Dimension(200, 25));




		photoTypeLabel = new JLabel("分类:");
		Vector vector = new Vector(PhotoRecordTablePanel.photoTypeList);
		photoTypeComboBox = new JComboBox(vector);
		photoTypeComboBox.setPreferredSize(new Dimension(200, 25));


		pdcjmcLabel = new JLabel("判定场景名称:");
		Vector vector2 = new Vector();
		vector2.add("");
		vector2.addAll(PhotoRecordTablePanel.photoValueList);

		pdcjmcComboBox = new JComboBox(vector2);
		pdcjmcComboBox.setPreferredSize(new Dimension(200, 25));

		searchButton = new JButton();
		searchButton.setText("查询");
		searchButton.setPreferredSize(new Dimension(90, 25));

		clearButton = new JButton();
		clearButton.setText("重置");
		clearButton.setPreferredSize(new Dimension(90, 25));

		sureButton = new JButton();
		sureButton.setText("确定");
		sureButton.setPreferredSize(new Dimension(90, 25));

		cancelButton = new JButton();
		cancelButton.setText("取消");
		cancelButton.setPreferredSize(new Dimension(90, 25));

		CmActionListener listener = new CmActionListener();
		searchButton.addActionListener(listener);
		clearButton.addActionListener(listener);
		sureButton.addActionListener(listener);
		cancelButton.addActionListener(listener);

		String[] photoTableHeader = new String[] {"编号", "样张名称"};
		String[] insertTableHeader = new String[] {"编号", "样张名称","版本", "产品图号", "拍摄要求",
				"判别准则", "分类", "拍摄对象分类","判定场景名称"};
		photoTable = new JTable();
		photoTableModel = new CommonTableModel(photoTableHeader, null, null);
		photoTable.setModel(photoTableModel);
		photoTable.getTableHeader().setReorderingAllowed(false);
		photoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		insertTable = new JTable();
		insertTableModel = new CommonTableModel(insertTableHeader, null, null);
		insertTable.setModel(insertTableModel);
		insertTable.getTableHeader().setReorderingAllowed(false);
		insertTable.getTableHeader().setResizingAllowed(false);
	}

	private void initLayout() {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 1;
		c.gridy = 1;
		topMainPanel.setLayout(new GridBagLayout());
		topMainPanel.add(photoNumberLabel, c);
		c.gridx = 2;
		topMainPanel.add(photoNumberTextField, c);
		c.gridx = 3;
		topMainPanel.add(photoNameLabel, c);
		c.gridx = 4;
		topMainPanel.add(photoNameTextField, c);
		c.gridx = 5;
		c.insets = new Insets(10, 3, 5, 5);
		topMainPanel.add(searchButton, c);
		c.insets = new Insets(10, 0, 5, 5);

		c.gridy = 2;
		c.gridx = 1;
		topMainPanel.add(productNumberLabel, c);
		c.gridx = 2;
		topMainPanel.add(productNumberTextField, c);
		c.gridx = 3;
		topMainPanel.add(psyqLabel, c);
		c.gridx = 4;
		topMainPanel.add(psyqTextField, c);
		c.gridx = 5;
		c.insets = new Insets(10, 3, 5, 5);
		topMainPanel.add(clearButton, c);

		c.gridy = 3;
		c.gridx = 1;
		topMainPanel.add(pbzzLabel, c);
		c.gridx = 2;
		topMainPanel.add(pbzzTextField, c);
		c.gridx = 3;
		topMainPanel.add(psdxTypeLabel, c);
		c.gridx = 4;
		topMainPanel.add(psdxTypeTextField, c);
		c.insets = new Insets(10, 3, 5, 5);

		c.gridy = 4;
		c.gridx = 1;
		topMainPanel.add(photoTypeLabel, c);
		c.gridx = 2;
		topMainPanel.add(photoTypeComboBox, c);
		c.insets = new Insets(10, 3, 5, 5);
		c.gridx = 3;
		topMainPanel.add(pdcjmcLabel, c);
		c.gridx = 4;
		topMainPanel.add(pdcjmcComboBox, c);
		c.insets = new Insets(10, 3, 5, 5);


		c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 1;
		c.gridy = 1;

		photoTable.setRowHeight(23);
		photoTable.getTableHeader().setPreferredSize(new Dimension(20, 25));
		JScrollPane jScrollPane1 = new JScrollPane();
		jScrollPane1.setViewportView(photoTable);
		jScrollPane1.setPreferredSize(new Dimension(1000, 200));
		JPanel photoTablePanel = new JPanel();
		photoTablePanel.setBorder(BorderFactory.createTitledBorder("照片样张查询结果"));
		photoTablePanel.setLayout(new BorderLayout());
		photoTablePanel.add(jScrollPane1);

		insertTable.setRowHeight(23);
		insertTable.getTableHeader().setPreferredSize(new Dimension(20, 25));
		JScrollPane jScrollPane2 = new JScrollPane();
		jScrollPane2.setViewportView(insertTable);
		jScrollPane2.setPreferredSize(new Dimension(1000, 200));
		JPanel insertTablePanel = new JPanel();
		insertTablePanel.setBorder(BorderFactory.createTitledBorder("选择添加的样张版本"));
		insertTablePanel.setLayout(new BorderLayout());
		insertTablePanel.add(jScrollPane2);

		VFlowLayout vFlowLayout = new VFlowLayout(5, true, true);
		middleMainPanel.setLayout(vFlowLayout);
		middleMainPanel.add(photoTablePanel);
		middleMainPanel.add(insertTablePanel);

		FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(flowLayout);
		buttonPanel.add(sureButton);
		buttonPanel.add(cancelButton);

		mainPanel.setLayout(new BorderLayout());

		imagePanel.setPreferredSize(new Dimension(400, 400));
		/*JLabel imageLabel=new JLabel();
		ImageIcon icon=new ImageIcon("E:\\321.png");
		icon.setImage(icon.getImage().getScaledInstance(380,400, Image.SCALE_DEFAULT));
		imageLabel.setIcon(icon);
		imagePanel.add(imageLabel,new Integer(Integer.MIN_VALUE));*/
		imagePanel.setBorder(BorderFactory.createTitledBorder("照片样张"));
		mainPanel.add(imagePanel,BorderLayout.EAST);

		mainPanel.add(topMainPanel, BorderLayout.NORTH);
		mainPanel.add(middleMainPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
	}

	private void initActions() {
		photoTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1) {
					imagePanel.removeAll();
					insertTableModel.setRowCount(0);
					int selectedRow = photoTable.getSelectedRow();
					String number = (String) photoTable.getValueAt(selectedRow, 0);
					if(number!=null && !"".equals(number) && photoRecordList!=null){
						Vector<Object> rowData = null;
						ArrayList<HashMap<String, String>> maps = photoRecordList.get(number);
						if(maps!=null && maps.size()>0){
							for (HashMap<String, String> map : maps) {
								String photoNumber = map.get("number");
								String photoName = map.get("name");
								String version = map.get("version");
								String productNumber = map.get("productNumber");
								String psyq = map.get("psyq");
								String pbzz = map.get("pbzz");
								String psdxType = map.get("psdxType");//拍照对象分类
								String photoType = map.get("photoType");//分类
								String PDCJMC = map.get("PDCJMC");
								rowData = new Vector<Object>();
								rowData.add(photoNumber);
								rowData.add(photoName);
								rowData.add(version);
								rowData.add(productNumber);
								rowData.add(psyq);
								rowData.add(pbzz);
								rowData.add(photoType);
								rowData.add(psdxType);
								rowData.add(PDCJMC);
								insertTableModel.addRow(rowData);
							}
						}
					}
					imagePanel.updateUI();
				}
			}
		});

		insertTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1) {
					imagePanel.removeAll();
					int selectedRow = insertTable.getSelectedRow();
					String number = (String) insertTable.getValueAt(selectedRow, 0);
					String version = (String) insertTable.getValueAt(selectedRow, 2);
					try {
						byte[] bytes = TechnicsIntf.getImageBytesByNumberAndVersion(number, version);
						if(bytes!=null){
							JLabel imageLabel=new JLabel();
							ImageIcon icon = new ImageIcon(bytes);
							icon.setImage(icon.getImage().getScaledInstance(380,400, Image.SCALE_DEFAULT));
							imageLabel.setIcon(icon);
							imagePanel.add(imageLabel,new Integer(Integer.MIN_VALUE));
						}
					} catch (InvocationTargetException ex) {
						ex.printStackTrace();
					} catch (RemoteException ex) {
						ex.printStackTrace();
					}
					mainPanel.updateUI();
				}
			}
		});
	}

	private String convertNull(Object str) {
		return str == null ? "" : str.toString();
	}

	public void search() {
		try {
			String number = photoNumberTextField.getText();
			String name = photoNameTextField.getText();
			String productNumber = productNumberTextField.getText();
			String psyq = psyqTextField.getText();
			String pbzz = pbzzTextField.getText();
			String psdxType = psdxTypeTextField.getText();
			String photoType= convertNull(photoTypeComboBox.getSelectedItem());
			String PDCJMC =  convertNull(pdcjmcComboBox.getSelectedItem());
			Map<String,String> map = new HashMap<String, String>();
			map.put("number",number);
			map.put("name",name);
			map.put("productNumber",productNumber);
			map.put("psyq",psyq);
			map.put("pbzz",pbzz);
			map.put("psdxType",psdxType);
			map.put("photoType",photoType);
			map.put("PDCJMC",PDCJMC);
			photoRecordList = TechnicsIntf.getPhotoRecordList(map);
			if(photoRecordList != null && photoRecordList.size()>0) {
				Vector<Object> rowData = null;
				for(String photoNumber : photoRecordList.keySet()){
					ArrayList<HashMap<String, String>> list = photoRecordList.get(photoNumber);
					if(list!=null && list.size()>0){
						rowData = new Vector<Object>();
						String photoName = list.get(0).get("name");
						rowData.add(photoNumber);
						rowData.add(photoName);
						photoTableModel.addRow(rowData);
					}
				}
			} else {
				SwingUtil.showMessageDialog("查询照片样张文件结果为空!", "提示", 2);
			}
		} catch (Exception e) {
			SwingUtil.showMessageDialog("查询照片样张文件时出错!", "提示", 2);
			e.printStackTrace();
		}

	}

	class CmActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == searchButton) {
				photoTableModel.setRowCount(0);
				insertTableModel.setRowCount(0);
				photoRecordList = null;
				imagePanel.removeAll();
				imagePanel.updateUI();
				search();
			} else if (e.getSource() == clearButton) {
				photoNameTextField.setText("");
				photoNumberTextField.setText("");
				productNumberTextField.setText("");
				psyqTextField.setText("");
				pbzzTextField.setText("");
				psdxTypeTextField.setText("");
				pdcjmcComboBox.setSelectedIndex(0);
				photoTypeComboBox.setSelectedIndex(0);
				photoTableModel.setRowCount(0);
				insertTableModel.setRowCount(0);
				imagePanel.removeAll();
				imagePanel.updateUI();
			} else if (e.getSource() == sureButton) {
				if (insertTable.getSelectedRows().length == 0) {
					SwingUtil.showMessageDialog("请选择照片样张文件", "提示", 2);
				} else {
					equipments = new Vector<Map<String, String>>();
					int[] rows = insertTable.getSelectedRows();
					equipments.addAll(generateObjectMap(rows));

					if(equipments != null){
						photoRecordTablePanel.addData(equipments);
					}
					Vector<Vector> dataVector = photoRecordTablePanel.getTableModel().getDataVector();
					if(dataVector.size()>0){
						photoRecordTablePanel.saveToXml(dataVector);
					}
					SwingUtil.showMessageDialog("照片样张文件添加成功！", "提示", 1);
					//dialog.dispose();
				}
			} else if (e.getSource() == cancelButton) {
				equipments = null;
				dialog.dispose();
			}
		}
	}

	public Vector<Map<String, String>> generateObjectMap(int[] rows) {
		Vector<Map<String, String>> list = new Vector<Map<String, String>>();
		for (int i = 0; i < rows.length; i++) {
			Map<String, String> map = new Hashtable<String, String>();
			map.put("number",(String) insertTable.getValueAt(rows[i],0));
			map.put("name",(String) insertTable.getValueAt(rows[i],1));
			map.put("version",(String) insertTable.getValueAt(rows[i],2));
			map.put("productNumber",(String) insertTable.getValueAt(rows[i],3));
			map.put("psyq",(String) insertTable.getValueAt(rows[i],4));
			map.put("pbzz",(String) insertTable.getValueAt(rows[i],5));
			map.put("photoType",(String) insertTable.getValueAt(rows[i],6));
			map.put("psdxType",(String) insertTable.getValueAt(rows[i],7));
			if(insertTable.getValueAt(rows[i],8)!=null){
				map.put("PDCJMC",(String) insertTable.getValueAt(rows[i],8));
			}
			list.add(map);
		}
		return list;
	}

	class CommonTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;
		/** 可编辑的列 */
		private int[] editableColumns;
		/** 表格每列的类型 */
		private Class<?>[] tableColumnClass;

		public CommonTableModel(int[] editableColumns) {
			this.editableColumns = editableColumns;
		}

		public CommonTableModel(String[] tableHeader, Class<?>[] tableColumnClass, int[] editableColumns) {
			super(null, tableHeader);
			this.editableColumns = editableColumns;
			this.tableColumnClass = tableColumnClass;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			if (editableColumns != null) {
				for (int i : editableColumns) {
					if (i == column) {
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (tableColumnClass != null) {
				return tableColumnClass[columnIndex];
			} else {
				return super.getColumnClass(columnIndex);
			}
		}
	}
}