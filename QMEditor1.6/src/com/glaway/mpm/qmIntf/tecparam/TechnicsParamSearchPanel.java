package com.glaway.mpm.qmIntf.tecparam;

import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.util.XmlUtil;
import com.glaway.mpm.wcIntf.TechnicsIntf;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class TechnicsParamSearchPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JPanel topPanel;
	private JPanel topMainPanel;
	private JPanel mainPanel;
	private JPanel middlePanel;
	private JPanel middleMainPanel;
	private JScrollPane jScrollPane;

	private JButton searchButton;
	private JButton clearButton;
	//编号
	private JLabel numberLabel;
	private JTextField number;
	//名称
	private JLabel nameLabel;
	private JTextField name;
	//类型
	private JLabel typeLabel;
	private JComboBox types;
	private String[] typesValue = new String[]{"全部","基础参数","枚举参数","知识参数"};
	public Object[] specialityValue = null;

	//专业
	private JLabel specialityLabel;
	private JComboBox specialities;

	private JTable jTable;
	private JButton sureButton;
	private JButton cancelButton;

	public static String param ="";
	private JDialog dialog;

	public TechnicsParamSearchPanel(JDialog dialog) {
		this.dialog = dialog;
		init();
	}

	private void init() {
		initComponents();
		initLayout();
		initActions();
		loadInitDatas();
	}

	private void initComponents() {
		if(specialityValue==null){
            try {
                specialityValue = TechnicsIntf.getMPMPPlanSubTypes().values().toArray();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }

        }
		topPanel = new JPanel();
		topMainPanel = new JPanel();
		middlePanel = new JPanel();
		middleMainPanel = new JPanel();
		mainPanel = new JPanel();
		jScrollPane = new JScrollPane();

		numberLabel = new JLabel();
		number = new JTextField();
		nameLabel = new JLabel();
		name = new JTextField();
		typeLabel = new JLabel();
		types = new JComboBox(typesValue);
		specialityLabel = new JLabel();
		specialities = new JComboBox(specialityValue);

        searchButton = new JButton();
		clearButton = new JButton();

		jTable = new JTable();

		sureButton = new JButton();
		cancelButton = new JButton();
	}

	private void initLayout() {
		numberLabel.setPreferredSize(new Dimension(75, 25));
		nameLabel.setPreferredSize(new Dimension(75, 25));
		typeLabel.setPreferredSize(new Dimension(75, 25));
		specialityLabel.setPreferredSize(new Dimension(75, 25));
		number.setPreferredSize(new Dimension(500, 25));
		name.setPreferredSize(new Dimension(500, 25));
		types.setPreferredSize(new Dimension(200, 25));
		specialities.setPreferredSize(new Dimension(200, 25));

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
		c.gridx = 2;
		topMainPanel.add(types, c);
		c.gridy = 4;
		c.gridx = 1;
		topMainPanel.add(specialityLabel, c);
		c.gridx = 2;
		topMainPanel.add(specialities, c);

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
		jScrollPane.setPreferredSize(new Dimension(900, 480));
		middleMainPanel.add(jScrollPane, c);

		c.gridy = 2;

		sureButton.setPreferredSize(new Dimension(90, 25));
		cancelButton.setPreferredSize(new Dimension(90, 25));

		c.insets = new Insets(30, 690, 5, 0);
		middleMainPanel.add(sureButton, c);

		c.insets = new Insets(30, 790, 5, 20);
		middleMainPanel.add(cancelButton, c);

		middlePanel.add(middleMainPanel);

		mainPanel.setLayout(new BorderLayout(1, 2));
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(middlePanel, BorderLayout.CENTER);
		this.add(mainPanel);
	}

	private DefaultTableModel getModel(Object[][] tableValue) {
		DefaultTableModel model = new DefaultTableModel(tableValue,
				new String[] { "", "参数编号", "参数名称", "参数类型","专业","计量单位"}) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return model;
	}

	private DefaultTableModel generatorModel(List<String[]> params) {
		Object[][] tableValue = null;
		if (params == null || params.size() == 0) {
			tableValue = new Object[0][6];
		} else {
			tableValue = new Object[params.size()][6];
			for (int i = 0; i < params.size(); i++) {
				tableValue[i][0] = convertNull(params.get(i)[0]);
				tableValue[i][1] = convertNull(params.get(i)[1]);
				tableValue[i][2] = convertNull(params.get(i)[2]);
				tableValue[i][3] = convertNull(params.get(i)[3]);
				tableValue[i][4] = convertNull(params.get(i)[4]);
				tableValue[i][5] = convertNull(params.get(i)[5]);
			}
		}
		return getModel(tableValue);
	}

	private void loadTable() {
		TableColumn column = jTable.getColumnModel().getColumn(0);

	}

	private void initActions() {
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final String numberValue = CommonUtil.trim(number.getText());
				final String nameValue = CommonUtil.trim(name.getText());
				final String type = CommonUtil.trim((String) types.getSelectedItem());
				final String special = CommonUtil.trim((String) specialities.getSelectedItem());
				// TODO query
				List<String[]> params = new ArrayList<String[]>();
				try {
					params = TechnicsIntf.queryTechnicsParsms(numberValue,nameValue,type,special);
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//params.add(new String[]{"1", "CS001", "实验时间", "", ""});
				//params.add(new String[]{"2", "CS002", "试验压力", "", ""});
				//params.add(new String[]{"3", "CS003", "工艺参数", "", ""});
				jTable.setModel(generatorModel(params));
				loadTable();
				if (params.size() == 0) {
					SwingUtil.showMessageDialog("搜索结果为空", "提示", 1);
				}
				setAble(true);
			}

		});

		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				name.setText("");
				number.setText("");
				types.setSelectedIndex(0);
				specialities.setSelectedIndex(0);
			}
		});

		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (jTable.getSelectedRows().length == 0) {
					SwingUtil.showMessageDialog("请选择工艺参数", "提示", 2);
				} else {
					int[] rows = jTable.getSelectedRows();
					for(int row:rows){
						String name = (String) jTable.getValueAt(row, 2);
						String unit = (String) jTable.getValueAt(row, 5);
						if(XmlUtil.isNotEmpty(name)){
							param = param + "【"+ name+ "："+unit+"】";
						}
					}
					dialog.dispose();
				}
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				param = null;
				dialog.dispose();
			}
		});
	}

	private String convertNull(Object str) {
		return str == null ? "" : str.toString();
	}

	private void loadInitDatas() {
		numberLabel.setText("参数编号");
		nameLabel.setText("参数名称");
		typeLabel.setText("参数类型");
		specialityLabel.setText("专业");
		searchButton.setText("搜索");
		clearButton.setText("清除条件");
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
		number.setEnabled(flag);
		name.setEnabled(flag);

		jTable.setEnabled(flag);
		sureButton.setEnabled(flag);
		cancelButton.setEnabled(flag);
	}
}