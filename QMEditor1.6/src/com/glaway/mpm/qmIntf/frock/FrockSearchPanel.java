package com.glaway.mpm.qmIntf.frock;

import com.glaway.mpm.model.Frock;
import com.glaway.mpm.model.KnifeTool;
import com.glaway.mpm.model.Tool;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewKnifeToolPanel;
import com.glaway.mpm.view.NewToolJPanel;
import com.glaway.mpm.view.WaiXieTecDescirbeJPanel;
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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class FrockSearchPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private VaLogger logger = VaLogger.getLogger(this.getClass());

	private JPanel topPanel;
	private JPanel topMainPanel;
	private JPanel mainPanel;
	private JPanel middlePanel;
	private JPanel middleMainPanel;
	private JScrollPane jScrollPane;

	private JTextField number;
	private JTextField name;
	private JLabel numberLabel;
	private JLabel nameLabel;
	private JLabel label1;

	private JComboBox jComboBox1;
	private JTable jTable;

	private JButton searchButton;
	private JButton clearButton;

	private JButton selectAllButton;
	private JButton sureButton;
	private JButton cancelButton;

	public static Vector<Map<String, String>> frocks;

	private JDialog dialog;
	private NewToolJPanel panel;
	private NewKnifeToolPanel knifePanel;
	private WaiXieTecDescirbeJPanel waiXieTecDescirbeJPanel;

	public FrockSearchPanel(JDialog dialog, WaiXieTecDescirbeJPanel panel) {
		this.dialog = dialog;
		this.waiXieTecDescirbeJPanel = panel;
		init();
	}

	public FrockSearchPanel(JDialog dialog, NewToolJPanel panel) {
		this.dialog = dialog;
		this.panel = panel;
		init();
	}

	public FrockSearchPanel(JDialog dialog, NewKnifeToolPanel panel) {
		this.dialog = dialog;
		this.knifePanel = panel;
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
		label1 = new JLabel();

		number = new JTextField();
		name = new JTextField();

		searchButton = new JButton();
		clearButton = new JButton();

		jComboBox1 = new JComboBox();

		jTable = new JTable();

		selectAllButton = new JButton();
		sureButton = new JButton();
		cancelButton = new JButton();
	}

	private void initLayout() {
		number.setPreferredSize(new Dimension(430, 25));
		name.setPreferredSize(new Dimension(430, 25));
		nameLabel.setPreferredSize(new Dimension(55, 25));
		numberLabel.setPreferredSize(new Dimension(55, 25));

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

		label1.setPreferredSize(new Dimension(35, 25));

		c.insets = new Insets(10, 0, 5, 5);
		c.gridy = 3;
		c.gridx = 1;
		topMainPanel.add(label1, c);

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
				new String[] { "oid", "编号", "名称", "型号", "类别", "规格","英文名称" }) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return model;
	}

	private DefaultTableModel generatorModel(List<Object> frocks, int index) {
		Object[][] tableValue = null;
		if (frocks == null || frocks.size() == 0) {
			tableValue = new Object[0][7];
		} else {
			tableValue = new Object[frocks.size()][7];
			for (int i = 0; i < tableValue.length; i++) {
				if (index == 1) {
					Frock frock = (Frock) frocks.get(i);
					tableValue[i][0] = convertNull(frock.getOid());

					String number = frock.getFrockNum();
					if(number.startsWith("A%")) {
						number = number.replace("A%", "");
					}
					if(number.startsWith("B%")) {
						number = number.replace("B%", "");
					}

					tableValue[i][1] = convertNull(number);
					tableValue[i][2] = convertNull(frock.getFrockName());
					tableValue[i][3] = convertNull(frock.getPindex());
					tableValue[i][4] = convertNull(frock.getFrockType());
					tableValue[i][5] = convertNull(frock.getFrockSpec());
					tableValue[i][6] = convertNull(frock.getEngLishName());
				} else if (index == 2||index==3) {
					Tool frock = (Tool) frocks.get(i);
					tableValue[i][0] = convertNull(frock.getOid());
					tableValue[i][1] = convertNull(frock.getToolNum());
					tableValue[i][2] = convertNull(frock.getToolName());
					tableValue[i][3] = convertNull(frock.getMindex());
					tableValue[i][4] = convertNull("");
					tableValue[i][5] = convertNull(frock.getCsize());
					tableValue[i][6] = convertNull(frock.getEngLishName());
				}
//				else if (index == 3) {
//					MeasureTool frock = (MeasureTool) frocks.get(i);
//					tableValue[i][0] = convertNull(frock.getOid());
//					tableValue[i][1] = convertNull(frock.getMeasureToolNum());
//					tableValue[i][2] = convertNull(frock.getMeasureToolName());
//					tableValue[i][3] = convertNull(frock.getMeasureToolSpec());
//					tableValue[i][4] = convertNull(frock.getMeasureToolSpec());
//				}
				else if (index == 4) {
					KnifeTool frock = (KnifeTool) frocks.get(i);
					tableValue[i][0] = convertNull(frock.getOid());
					tableValue[i][1] = convertNull(frock.getKnifeToolNum());
					tableValue[i][2] = convertNull(frock.getKnifeToolName());
					tableValue[i][3] = convertNull("");
					tableValue[i][4] = convertNull(frock.getKnifetype());
					tableValue[i][5] = convertNull(frock.getCsize());
					tableValue[i][6] = convertNull(frock.getEngLishName());
				}

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

//		this.setHiddenColumn(jTable, 3);
//		this.setHiddenColumn(jTable, 4);
	}

	private void initActions() {
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final String numberValue = CommonUtil.trim(number.getText());
				final String nameValue = CommonUtil.trim(name.getText());
				final int index = jComboBox1.getSelectedIndex() + 1;
				logger.debug("number:" + numberValue + " name:" + nameValue);
				if (numberValue.equals("") && nameValue.equals("")) {
					SwingUtil.showMessageDialog("请输入编号或者名称", "提示", 2);
				} else {
					Thread thread = new Thread() {
						@Override
						public void run() {
							if (mainPanel != null) {
								setAble(false);
								List<Object> frocks = ResourceIntf.getFrocks(numberValue, nameValue, index + "");
								logger.debug("search frocks====================" + frocks);
								jTable.setModel(generatorModel(frocks, index));
								loadTable();
								if (frocks.size() == 0) {
									SwingUtil.showMessageDialog("没用找到符合你输入条件的数据,请重试！", "提示", 1);
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
				number.setText("");
				if (jComboBox1.getSelectedObjects().length != 0) {
					jComboBox1.setSelectedIndex(0);
				}
			}
		});
		selectAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (jTable.getRowCount() == 0) {
					SwingUtil.showMessageDialog("没有数据可以选择", "提示", 1);
				} else {
					jTable.selectAll();
				}
			}
		});

		jTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1 && e.getClickCount() == 2) {
					int row = ((JTable) e.getSource()).rowAtPoint(e.getPoint()); // 获得行位置
					Vector<Map<String, String>> frocks = new Vector<Map<String, String>>();
					frocks.add(generateObjectMap(row));
					if(panel != null) {
						panel.addData(frocks);
					} else if (waiXieTecDescirbeJPanel != null) {
						waiXieTecDescirbeJPanel.addData(frocks);
					}
					else {
						knifePanel.addData(frocks);
					}
				}
			}
		});

		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (jTable.getSelectedRows().length == 0) {
					SwingUtil.showMessageDialog("请选择数据", "提示", 2);
				} else {
					frocks = new Vector<Map<String, String>>();
					for (int row : jTable.getSelectedRows()) {
						frocks.add(generateObjectMap(row));
					}
					logger.debug("return frocks====================" + frocks);
					dialog.dispose();
				}
			}
		});
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frocks = null;
				dialog.dispose();
			}
		});
	}

	private Map<String, String> generateObjectMap(int row) {
		Map<String, String> map = new Hashtable<String, String>();
		map.put("oid", CommonUtil.trim(convertNull(jTable.getValueAt(row, 0))));
		map.put("toolNum", CommonUtil.trim(convertNull(jTable.getValueAt(row, 1))));
		map.put("toolName", CommonUtil.trim(convertNull(jTable.getValueAt(row, 2))));
		map.put("mindex", CommonUtil.trim(convertNull(jTable.getValueAt(row, 3))));
		map.put("frockType", CommonUtil.trim(convertNull(jTable.getValueAt(row, 4))));
		map.put("knifetype", CommonUtil.trim(convertNull(jTable.getValueAt(row, 4))));
		map.put("csize", CommonUtil.trim(convertNull(jTable.getValueAt(row, 5))));
		map.put("EnglishName", CommonUtil.trim(convertNull(jTable.getValueAt(row, 6))));
		map.put("useCount","1");
		return map;
	}

	private void loadInitDatas() {
		nameLabel.setText("名称");
		numberLabel.setText("编号");
		label1.setText("类型");
		searchButton.setText("搜索");
		clearButton.setText("清除条件");
		selectAllButton.setText("全部选中");
		sureButton.setText("确定");
		cancelButton.setText("取消");
		String[] condtions = ResourceIntf.getFkSearchCondition();
		jComboBox1.setModel(new DefaultComboBoxModel(condtions));
		jComboBox1.setSelectedItem("工装");
		jComboBox1.setEnabled(false);
		jTable.setModel(getModel(null));
		jTable.getTableHeader().setReorderingAllowed(false);
		jTable.getTableHeader().setResizingAllowed(true);
		loadTable();
	}
	public void setHiddenColumn(JTable table,int columnIndex) {
		if (columnIndex >= 0 && columnIndex < table.getColumnCount()) {
			// 隐藏ID列
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setMaxWidth(0);
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setMinWidth(0);
			table.getColumnModel().getColumn(columnIndex).setMaxWidth(0);
			table.getColumnModel().getColumn(columnIndex).setPreferredWidth(0);
			table.getColumnModel().getColumn(columnIndex).setWidth(0);
			table.getColumnModel().getColumn(columnIndex).setMinWidth(0);
		}
	}
	private String convertNull(Object str) {
		return str == null ? "" : str.toString();
	}

	private void setAble(boolean flag) {
		searchButton.setEnabled(flag);
		clearButton.setEnabled(flag);
		number.setEnabled(flag);
		name.setEnabled(flag);

		jTable.setEnabled(flag);
		selectAllButton.setEnabled(flag);
		sureButton.setEnabled(flag);
		cancelButton.setEnabled(flag);
		jComboBox1.setEnabled(false);
	}
}