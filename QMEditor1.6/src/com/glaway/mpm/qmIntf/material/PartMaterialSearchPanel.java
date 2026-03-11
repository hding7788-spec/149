package com.glaway.mpm.qmIntf.material;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.glaway.mpm.model.Material;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class PartMaterialSearchPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private VaLogger logger = VaLogger.getLogger(this.getClass());

	private JPanel mainPanel;
	private JPanel topPanel;
	private JPanel middlePanel;
	private JPanel middleMainPanel;
	private JPanel topMainPanel;

	private JLabel nameLabel;
	private JLabel numberLabel;
	private JLabel typeLabel;

	private JTextField number;
	private JTextField name;
	private JComboBox jComboBox1;

	private JButton searchButton;
	private JButton clearButton;

	private JScrollPane jScrollPane;
	private JTable jTable;

	private JButton sureButton;
	private JButton cancelButton;
	public static Vector<Map<String, String>> vector;
	private JDialog dialog;

	public PartMaterialSearchPanel(JDialog dialog) {
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
		mainPanel = new JPanel();
		topPanel = new JPanel();
		middlePanel = new JPanel();
		topMainPanel = new JPanel();
		middleMainPanel = new JPanel();

		nameLabel = new JLabel();
		numberLabel = new JLabel();
		typeLabel = new JLabel();

		jComboBox1 = new JComboBox();

		searchButton = new JButton();

		number = new JTextField();
		name = new JTextField();
		sureButton = new JButton();
		cancelButton = new JButton();
		clearButton = new JButton();

		jScrollPane = new JScrollPane();
		jTable = new JTable();
	}

	private void initLayout() {
		number.setPreferredSize(new Dimension(430, 25));
		name.setPreferredSize(new Dimension(430, 25));
		nameLabel.setPreferredSize(new Dimension(70, 25));
		numberLabel.setPreferredSize(new Dimension(70, 25));
		jComboBox1.setPreferredSize(new Dimension(200, 25));
		typeLabel.setPreferredSize(new Dimension(70, 25));

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
		topMainPanel.add(number, c);
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
		topMainPanel.add(typeLabel, c);

		jComboBox1.setPreferredSize(new Dimension(150, 25));
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 2;
		topMainPanel.add(jComboBox1, c);

		topPanel.add(topMainPanel);

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 1;
		c.gridy = 1;

		middleMainPanel.setLayout(new GridBagLayout());
		jTable.setRowHeight(23);
		jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jTable.getTableHeader().setPreferredSize(new Dimension(20, 25));
		jScrollPane.setViewportView(jTable);
		jScrollPane.setPreferredSize(new Dimension(590, 270));
		middleMainPanel.add(jScrollPane, c);

		c.gridy = 2;

		sureButton.setPreferredSize(new Dimension(90, 25));
		cancelButton.setPreferredSize(new Dimension(90, 25));

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
				new String[] { "oid", "零件编号", "零件名称", "零件类型" }) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return model;
	}

	private DefaultTableModel generatorModel(List<Material> list, String type) {
		Object[][] tableValue = null;
		if (list == null || list.size() == 0) {
			tableValue = new Object[0][4];
		} else {
			tableValue = new Object[list.size()][4];
			for (int i = 0; i < list.size(); i++) {
				tableValue[i][0] = convertNull(list.get(i).getOid());
				tableValue[i][1] = convertNull(list.get(i).getMaterialNumber());
				tableValue[i][2] = convertNull(list.get(i).getMaterialName());
				tableValue[i][3] = convertNull(switchMaterialType(type));
			}
		}
		return getModel(tableValue);
	}

	private void loadTable() {
		hideColumn(0);
	}

	private void hideColumn(int i) {
		TableColumn column = jTable.getColumnModel().getColumn(i);
		column.setMaxWidth(i);
		column.setMinWidth(i);
		column.setPreferredWidth(i);
		column.setWidth(i);
	}

	private String switchMaterialType(String type) {
		String result = "";
		if ("2".equals(type)) {
			result = "standardPart";
		} else if ("3".equals(type)) {
			result = "purchasedPart";
		}
		return result;
	}

	private void initActions() {

		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String numberValue = CommonUtil.trim(number.getText());
				String nameValue = CommonUtil.trim(name.getText());
				String type = String.valueOf(jComboBox1.getSelectedIndex() + 1);
				if (numberValue.equals("") && nameValue.equals("")) {
					SwingUtil.showMessageDialog("请输入零件编号或者名称", "提示", 2);
				} else if ("1".equals(type)) {
					SwingUtil.showMessageDialog("请选择零件类型", "提示", 2);
				} else {
					logger.debug("number：" + numberValue + " name:" + nameValue
							+ " type:" + type);
					Map<String, String> map = new HashMap<String, String>();
					map.put("number", numberValue);
					map.put("name", nameValue);
					List<Material> list = ResourceIntf.getMaterials(map, type);
					logger.debug("搜索零件编号和名称====================" + list);
					jTable.setModel(generatorModel(list, type));
					loadTable();
					if (list == null || list.size() == 0) {
						SwingUtil.showMessageDialog("搜索结果为空", "提示", 1);
					}
				}
			}
		});
		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				name.setText("");
				number.setText("");
				if (jComboBox1.getSelectedObjects().length != 0) {
					jComboBox1.setSelectedIndex(0);
				}
			}
		});

		sureButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rows = jTable.getSelectedRows();
				if (rows.length == 0) {
					SwingUtil.showMessageDialog("请选择零件", "提示", 2);
				} else {
					vector = new Vector<Map<String, String>>();
					for (int row : rows) {
						Map<String, String> map = new HashMap<String, String>();
						map.put("oid", convertNull(jTable.getValueAt(row, 0)));
						map.put("materialNumber",
								convertNull(jTable.getValueAt(row, 1)));
						map.put("materialName",
								convertNull(jTable.getValueAt(row, 2)));
						String materialType = convertNull(jTable.getValueAt(
								row, 3));
						map.put("toolTip", materialType);
						map.put("materialType", materialType);
						vector.add(map);
					}
					logger.debug(vector);
					dialog.dispose();
				}
			}
		});
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				vector = null;
				dialog.dispose();
			}
		});
	}

	private void loadInitDatas() {
		searchButton.setText("搜索");
		clearButton.setText("清除条件");
		nameLabel.setText("零件名称");
		numberLabel.setText("零件编号");
		typeLabel.setText("零件类型");
		sureButton.setText("确定");
		cancelButton.setText("取消");
		jTable.setModel(getModel(null));
		jTable.getTableHeader().setReorderingAllowed(false);
		jTable.getTableHeader().setResizingAllowed(false);
		jComboBox1.setModel(new DefaultComboBoxModel(new String[] { "请选择零件类型",
				"标准件", "外购件" }));
		loadTable();
	}

	private String convertNull(Object str) {
		return str == null ? "" : str.toString();
	}

}
