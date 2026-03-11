package com.glaway.mpm.qmIntf.equipment;

import com.glaway.mpm.model.Equipment;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewEquipJPanel;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ResourceIntf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;

public class EquipmentSearchPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private VaLogger logger = VaLogger.getLogger(this.getClass());

	private JPanel topPanel;
	private JPanel topMainPanel;
	private JPanel mainPanel;
	private JPanel middlePanel;
	private JPanel middleMainPanel;
	private JScrollPane jScrollPane;

	private JButton searchButton;
	private JButton clearButton;
	private JTextField equipmentNumber;
	private JTextField name;
	private JLabel numberLabel;
	private JLabel nameLabel;

	private JLabel mindexLabel;
	private JTextField mindexField;

	// private JLabel label1;
	// private JLabel label2;
	// private JComboBox jComboBox1;
	// private JComboBox jComboBox2;

	private JTable jTable;
	private JButton selectAllButton;
	private JButton sureButton;
	private JButton cancelButton;

	public static Vector<Map<String, String>> equipments;
	private JDialog dialog;
	private NewEquipJPanel panel;

	public EquipmentSearchPanel(JDialog dialog, NewEquipJPanel panel) {
		this.panel = panel;
		this.dialog = dialog;
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
		topPanel = new JPanel();
		topMainPanel = new JPanel();
		middlePanel = new JPanel();
		middleMainPanel = new JPanel();
		mainPanel = new JPanel();
		jScrollPane = new JScrollPane();

		nameLabel = new JLabel();
		numberLabel = new JLabel();
		mindexLabel = new JLabel();

		// label1 = new JLabel();
		// label2 = new JLabel();
		// jComboBox1 = new JComboBox();
		// jComboBox2 = new JComboBox();

		equipmentNumber = new JTextField();
		name = new JTextField();
		mindexField = new JTextField();

		searchButton = new JButton();
		clearButton = new JButton();

		jTable = new JTable();

		selectAllButton = new JButton();
		sureButton = new JButton();
		cancelButton = new JButton();
	}

	private void initLayout() {
		equipmentNumber.setPreferredSize(new Dimension(430, 25));
		name.setPreferredSize(new Dimension(430, 25));
		mindexField.setPreferredSize(new Dimension(430, 25));
		nameLabel.setPreferredSize(new Dimension(75, 25));
		numberLabel.setPreferredSize(new Dimension(75, 25));
		mindexLabel.setPreferredSize(new Dimension(75, 25));

		searchButton.setPreferredSize(new Dimension(90, 25));
		clearButton.setPreferredSize(new Dimension(90, 25));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 1;
		c.gridy = 1;
		topMainPanel.setLayout(new GridBagLayout());
		topMainPanel.add(numberLabel, c);
		c.gridx = 2;
		topMainPanel.add(equipmentNumber, c);
		c.gridx = 3;
		c.insets = new Insets(10, 3, 5, 5);
		topMainPanel.add(searchButton, c);
		c.insets = new Insets(10, 0, 5, 5);

		c.gridy = 2;
		c.gridx = 1;
		topMainPanel.add(nameLabel, c);
		c.gridx = 2;
		topMainPanel.add(name, c);
		c.gridx = 3;
		c.insets = new Insets(10, 3, 5, 5);
		topMainPanel.add(clearButton, c);

		c.insets = new Insets(10, 0, 5, 5);
		c.gridy = 3;
		c.gridx = 1;
		topMainPanel.add(mindexLabel, c);
		c.gridx = 2;
		topMainPanel.add(mindexField, c);
		c.gridx = 3;

		// label1.setPreferredSize(new Dimension(35, 25));
		// label2.setPreferredSize(new Dimension(35, 25));

		// c.insets = new Insets(10, 0, 5, 5);
		// c.gridy = 3;
		// c.gridx = 1;
		// topMainPanel.add(label1, c);
		//
		// jComboBox1.setPreferredSize(new Dimension(150, 25));
		// c.insets = new Insets(10, 0, 5, 5);
		// c.gridx = 2;
		// topMainPanel.add(jComboBox1, c);
		//
		// c.insets = new Insets(10, 180, 5, 5);
		// topMainPanel.add(label2, c);
		//
		// jComboBox2.setPreferredSize(new Dimension(150, 25));
		//
		// c.insets = new Insets(10, 230, 5, 5);
		// topMainPanel.add(jComboBox2, c);

		topPanel.add(topMainPanel);

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 1;
		c.gridy = 1;

		middleMainPanel.setLayout(new GridBagLayout());
		jTable.setRowHeight(23);
		jTable.getTableHeader().setPreferredSize(new Dimension(20, 25));
		jScrollPane.setViewportView(jTable);
		jScrollPane.setPreferredSize(new Dimension(590, 270));
		middleMainPanel.add(jScrollPane, c);

		c.gridy = 2;

		selectAllButton.setPreferredSize(new Dimension(90, 25));
		sureButton.setPreferredSize(new Dimension(90, 25));
		cancelButton.setPreferredSize(new Dimension(90, 25));

		c.insets = new Insets(30, 290, 5, 0);
		middleMainPanel.add(selectAllButton, c);

		c.insets = new Insets(30, 390, 5, 0);
		middleMainPanel.add(sureButton, c);

		c.insets = new Insets(30, 490, 5, 20);
		middleMainPanel.add(cancelButton, c);

		middlePanel.add(middleMainPanel);

		mainPanel.setLayout(new BorderLayout(1, 2));
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(middlePanel, BorderLayout.CENTER);
		this.add(mainPanel);
	}

	private DefaultTableModel getModel(Object[][] tableValue) {
		DefaultTableModel model = new DefaultTableModel(tableValue,
				new String[] { "", "设备编号", "设备名称", "设备型号","设备类型","规格","英文名称" }) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return model;
	}

	private DefaultTableModel generatorModel(List<Equipment> equipments) {
		Object[][] tableValue = null;
		if (equipments == null || equipments.size() == 0) {
			tableValue = new Object[0][7];
		} else {
			tableValue = new Object[equipments.size()][7];
			for (int i = 0; i < equipments.size(); i++) {
				tableValue[i][0] = convertNull(equipments.get(i).getOid());
				tableValue[i][1] = convertNull(equipments.get(i).getNumber());
				tableValue[i][2] = convertNull(equipments.get(i).getName());
				tableValue[i][3] = convertNull(equipments.get(i).getMindex());
				tableValue[i][4] = convertNull(equipments.get(i).getEquipmentType());
				tableValue[i][5] = convertNull(equipments.get(i).getCsize());
				tableValue[i][6] = convertNull(equipments.get(i).getEnglishName());
			}
		}
		return getModel(tableValue);
	}

	private void loadTable() {
		TableColumn column = jTable.getColumnModel().getColumn(0);
		column.setMaxWidth(0);
		column.setMinWidth(0);
		column.setPreferredWidth(0);
		column.setWidth(0);
	}

	private void initActions() {
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final String numberValue = CommonUtil.trim(equipmentNumber.getText());
				final String nameValue = CommonUtil.trim(name.getText());
				final String mindex = CommonUtil.trim(mindexField.getText());
				logger.debug("equipmentNumber:" + numberValue + " name:" + nameValue+ " mindex:" + mindex);
				if (numberValue.equals("") && nameValue.equals("") && mindex.equals("")) {
					List<Equipment> equipments = ResourceIntf.getEquipments(null, "1");
					logger.debug("search equipments====================" + equipments);
					jTable.setModel(generatorModel(equipments));
					loadTable();
					if (equipments.size() == 0) {
						SwingUtil.showMessageDialog("搜索结果为空", "提示", 1);
					}
					setAble(true);
				} else {
					Thread thread = new Thread() {
						@Override
						public void run() {
							if (mainPanel != null) {
								setAble(false);

								Map<String, String> map = new HashMap<String, String>();
								if (!numberValue.equals("")) {
									map.put("number", numberValue);
								}
								if (!nameValue.equals("")) {
									map.put("name", nameValue);
								}
								if (!mindex.equals("")) {
									map.put("MINDEX", mindex);
								}

								//List<Equipment> equipments = ResourceIntf.getEquipments(CommonUtil.trim(equipmentNumber.getText()), CommonUtil.trim(name.getText()),CommonUtil.trim(mindexField.getText()));

								List<Equipment> equipments = ResourceIntf.getEquipments(map, "1");
								logger.debug("search equipments====================" + equipments);
								jTable.setModel(generatorModel(equipments));
								loadTable();
								if (equipments.size() == 0) {
									SwingUtil.showMessageDialog("搜索结果为空", "提示", 1);
								}
								setAble(true);
							}
						}
					};
					thread.start();
				}
			}

		});

		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				name.setText("");
				equipmentNumber.setText("");
				// if (jComboBox1.getSelectedObjects().length != 0) {
				// jComboBox1.setSelectedIndex(0);
				// }
				// if (jComboBox2.getSelectedObjects().length != 0) {
				// jComboBox2.setSelectedIndex(0);
				// }
			}
		});
		selectAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (jTable.getRowCount() == 0) {
					SwingUtil.showMessageDialog("没有设备可以选择", "提示", 1);
				} else {
					jTable.selectAll();
				}
			}
		});
		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (jTable.getSelectedRows().length == 0) {
					SwingUtil.showMessageDialog("请选择设备", "提示", 2);
				} else {
					equipments = new Vector<Map<String, String>>();
					for (int row : jTable.getSelectedRows()) {
						equipments.add(generateObjectMap(row));
					}
					logger.debug("return equipments===================="
							+ equipments);
					dialog.dispose();
				}
			}
		});

		jTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1 && e.getClickCount() == 2) {
					int row = ((JTable) e.getSource()).rowAtPoint(e.getPoint()); // 获得行位置
					Vector<Map<String, String>> equipments = new Vector<Map<String, String>>();
					equipments.add(generateObjectMap(row));
					panel.addData(equipments);
				}
			}
		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				equipments = null;
				dialog.dispose();
			}
		});
	}

	public Map<String, String> generateObjectMap(int row) {
		Map<String, String> map = new Hashtable<String, String>();
		map.put("oid", CommonUtil.trim(convertNull(jTable.getValueAt(row, 0))));
		map.put("number",CommonUtil.trim(convertNull(jTable.getValueAt(row, 1))));
		map.put("name", CommonUtil.trim(convertNull(jTable.getValueAt(row, 2))));
		map.put("mindex",CommonUtil.trim(convertNull(jTable.getValueAt(row, 3))));
		map.put("equipmentType",CommonUtil.trim(convertNull(jTable.getValueAt(row, 4))));
		map.put("csize",CommonUtil.trim(convertNull(jTable.getValueAt(row, 5))));
		map.put("EnglishName",CommonUtil.trim(convertNull(jTable.getValueAt(row, 6))));
		map.put("useCount","1");
		return map;
	}

	private String convertNull(Object str) {
		return str == null ? "" : str.toString();
	}

	private void loadInitDatas() {
		nameLabel.setText("设备名称");
		numberLabel.setText("设备编号");
		mindexLabel.setText("型   号");
		// label1.setText("大类");
		// label2.setText("小类");
		searchButton.setText("搜索");
		clearButton.setText("清除条件");
		selectAllButton.setText("全部选中");
		sureButton.setText("确定");
		cancelButton.setText("取消");
		// String[] condtions1 = ProcessEditorToWCIntf.getEpSearchCondition(1);
		// String[] condtions2 = ProcessEditorToWCIntf.getEpSearchCondition(2);
		// jComboBox1.setModel(new DefaultComboBoxModel(condtions1));
		// jComboBox2.setModel(new DefaultComboBoxModel(condtions2));
		jTable.setModel(getModel(null));
		jTable.getTableHeader().setReorderingAllowed(false);
		jTable.getTableHeader().setResizingAllowed(false);
		loadTable();
	}

	private void setAble(boolean flag) {
		searchButton.setEnabled(flag);
		clearButton.setEnabled(flag);
		equipmentNumber.setEnabled(flag);
		name.setEnabled(flag);

		jTable.setEnabled(flag);
		selectAllButton.setEnabled(flag);
		sureButton.setEnabled(flag);
		cancelButton.setEnabled(flag);
	}
}