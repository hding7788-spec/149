package com.glaway.mpm.qmIntf.workspace;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.glaway.mpm.model.WorkPlace;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewWorkSpaceJPanel;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class WorkSpaceSearchPanel extends JPanel{
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


	private JTable jTable;
	private JButton selectAllButton;
	private JButton sureButton;
	private JButton cancelButton;

	public static Vector<Map<String, String>> equipments;
	private JDialog dialog;
	private NewWorkSpaceJPanel panel;

	public WorkSpaceSearchPanel(JDialog dialog, NewWorkSpaceJPanel panel){
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

		equipmentNumber = new JTextField();
		name = new JTextField();

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
		c.insets = new Insets(10, 0, 5, 5);
		c.gridy = 4;
		c.gridx = 1;

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
				new String[] { "", "编号", "名称", "简述","位置","英文名称" }) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return model;
	}
	private DefaultTableModel generatorModel(List<WorkPlace> workspace) {
		Object[][] tableValue = null;
		if(workspace == null || workspace.size() == 0){
			tableValue = new Object[0][6];
		} else{
			tableValue = new Object[workspace.size()][6];
			for (int i = 0; i < workspace.size(); i++) {
				WorkPlace workspaces = (WorkPlace) workspace.get(i);
				tableValue[i][0] = convertNull(workspaces.getOid());
				tableValue[i][1] = convertNull(workspaces.getNumber());
				tableValue[i][2] = convertNull(workspaces.getName());
				tableValue[i][3] = convertNull(workspaces.getSelf());
				tableValue[i][4] = convertNull(workspaces.getSpace());
				tableValue[i][5] = convertNull(workspaces.getEngLishName());
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
				logger.debug("equipmentNumber:" + numberValue + " name:" + nameValue);
				if (numberValue.equals("") && nameValue.equals("")) {
					List<WorkPlace> workspace = ResourceIntf.getWorkPlace(null, "1");
					logger.debug("search workspace====================" + workspace);
					jTable.setModel(generatorModel(workspace));
					loadTable();
					if (workspace.size() == 0) {
						SwingUtil.showMessageDialog("搜索结果为空", "提示", 1);
					}
					setAble(true);
				}else{
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
								List<WorkPlace> workspace = ResourceIntf.getWorkPlace(map, "1");
								logger.debug("search workspace====================" + workspace);
								jTable.setModel(generatorModel(workspace));
								loadTable();
								if (workspace.size() == 0) {
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
			}
		});
		selectAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (jTable.getRowCount() == 0) {
					SwingUtil.showMessageDialog("没有量具可以选择", "提示", 1);
				} else {
					jTable.selectAll();
				}
			}
		});
		sureButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (jTable.getSelectedRows().length == 0) {
					SwingUtil.showMessageDialog("请选择量具", "提示", 2);
				} else {
					equipments = new Vector<Map<String, String>>();
					for (int row : jTable.getSelectedRows()) {
						equipments.add(generateObjectMap(row));
					}
					logger.debug("return equipments====================" + equipments);
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
		map.put("Number",CommonUtil.trim(convertNull(jTable.getValueAt(row, 1))));
		map.put("Name", CommonUtil.trim(convertNull(jTable.getValueAt(row, 2))));
		map.put("Remark",CommonUtil.trim(convertNull(jTable.getValueAt(row, 3))));
		map.put("WorkPlace",CommonUtil.trim(convertNull(jTable.getValueAt(row, 4))));
		map.put("EnglishName",CommonUtil.trim(convertNull(jTable.getValueAt(row, 5))));
		return map;
	}

	private String convertNull(Object str) {
		return str == null ? "" : str.toString();
	}
	private void loadInitDatas() {
		nameLabel.setText("名称");
		numberLabel.setText("编号");
		searchButton.setText("搜索");
		clearButton.setText("清除条件");
		selectAllButton.setText("全部选中");
		sureButton.setText("确定");
		cancelButton.setText("取消");
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
