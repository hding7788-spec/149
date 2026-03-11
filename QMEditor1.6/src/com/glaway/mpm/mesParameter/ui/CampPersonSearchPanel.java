package com.glaway.mpm.mesParameter.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.glaway.mpm.mesParameter.helper.MesParameterProcessor;
import com.glaway.mpm.mesParameter.model.Bdpsndoc;
import com.glaway.mpm.parameter.designui.NewCommonParamTablePanel;
import com.glaway.mpm.parameter.designui.NewSpecialParamTablePanel;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.visual.log.VaLogger;

public class CampPersonSearchPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private VaLogger logger = VaLogger.getLogger(this.getClass());

	private JPanel mainPanel;
	private JPanel topPanel;
	private JPanel middlePanel;
	private JPanel middleMainPanel;
	private JPanel topMainPanel;
	private Container parent;

	private JLabel nameLabel;
	private JLabel numberLabel;

	private JTextField number;
	private JTextField name;

	private JButton searchButton;
	private JButton clearButton;

	private JScrollPane jScrollPane;
	private JTable jTable;

	private JButton sureButton;
	private JButton cancelButton;
	private JDialog dialog;
	private int columnNum;
	private int tableColumn;
	public CampPersonSearchPanel(JDialog dialog, Container parent, int columnNum, int tableColumn) {
		this.parent = parent;
		this.dialog = dialog;
		this.columnNum = columnNum;
		this.tableColumn = tableColumn;
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

		searchButton = new JButton();

		number = new JTextField();
		name = new JTextField();
		sureButton = new JButton();
		cancelButton = new JButton();
		clearButton = new JButton();

		jScrollPane = new JScrollPane();
		jTable = new JTable();
		number.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				System.out.println("focusLost");
				String str = number.getText();
				if (str != null && str.length() > 10) {
					str = str.substring(0, 10);
					number.setText(str);
				}

			}

			@Override
			public void focusGained(FocusEvent e) {

			}
		});
	}

	private void initLayout() {
		number.setPreferredSize(new Dimension(430, 25));
		name.setPreferredSize(new Dimension(430, 25));
		nameLabel.setPreferredSize(new Dimension(75, 25));
		numberLabel.setPreferredSize(new Dimension(75, 25));

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
				new String[] { "", "人员编码", "人员姓名", "业务员编号" }) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return model;
	}

	private DefaultTableModel generatorModel(List<Bdpsndoc> list) {
		Object[][] tableValue = null;
		if (list == null || list.size() == 0) {
			tableValue = new Object[0][5];
		} else {
			tableValue = new Object[list.size()][5];
			for (int i = 0; i < list.size(); i++) {
				tableValue[i][1] = convertNull(list.get(i).getPsncode());
				tableValue[i][2] = convertNull(list.get(i).getPsnname());
				tableValue[i][3] = convertNull(list.get(i).getClerkcode());
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

		jTable.getColumnModel().getColumn(3).setMinWidth(10);
		jTable.getColumnModel().getColumn(3).setPreferredWidth(10);
	}

	private void initActions() {

		jTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1 && e.getClickCount() == 2) {
					getSureButton().doClick();
				}
			}
		});

		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String numberValue = CommonUtil.trim(number.getText());
				String nameValue = CommonUtil.trim(name.getText());
				logger.debug("number：" + numberValue + " name:" + nameValue);
				List<Bdpsndoc> list = MesParameterProcessor.getCampPersonInfo(nameValue, numberValue);
				logger.debug("搜索工艺编号和名称====================" + list);
				if (list != null) {
					jTable.setModel(generatorModel(list));
					loadTable();
				}
				if (list == null || list.size() == 0) {
					JOptionPane.showMessageDialog(dialog, "搜索结果为空", "提示", JOptionPane.OK_OPTION);
				}
			}
		});
		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				name.setText("");
				number.setText("");
			}
		});
		sureButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int row = jTable.getSelectedRow();
				if(row == -1){
					JOptionPane.showMessageDialog(dialog, "请选择相应人员", "提示", JOptionPane.OK_OPTION);
				}else{
					String name = (String) jTable.getValueAt(row, tableColumn);
					if(parent instanceof NewCommonParamTablePanel){
						NewCommonParamTablePanel commonParamTablePanel = (NewCommonParamTablePanel) parent;
						JTable table = commonParamTablePanel.getTable();
						if(table.getSelectedRows().length > 0){
							for(int i = 0; i < table.getSelectedRows().length; i++){
								table.setValueAt(name, table.getSelectedRows()[i], columnNum);
							}
						}else{
							JOptionPane.showMessageDialog(dialog, "请选择需要填写的行！", "提示", JOptionPane.OK_OPTION);
						}
					}else if(parent instanceof NewSpecialParamTablePanel){
						NewSpecialParamTablePanel specialParamTablePanel = (NewSpecialParamTablePanel) parent;
						JTable table = specialParamTablePanel.getTable();
						if(table.getSelectedRows().length > 0){
							for(int i = 0; i < table.getSelectedRows().length; i++){
								table.setValueAt(name, table.getSelectedRows()[i], columnNum);
							}
						}else{
							JOptionPane.showMessageDialog(dialog, "请选择需要填写的行！", "提示", JOptionPane.OK_OPTION);
						}
					}
					dialog.dispose();
				}
			}
		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
	}

	private void loadInitDatas() {
		searchButton.setText("搜索");
		clearButton.setText("清除条件");
		nameLabel.setText("人员姓名");
		numberLabel.setText("人员编码");
		sureButton.setText("确定");
		cancelButton.setText("取消");
		jTable.setModel(getModel(null));
		jTable.getTableHeader().setReorderingAllowed(false);
//		jTable.getTableHeader().setResizingAllowed(false);
		//String [] technicsTypes = {"全部","典型工艺","通用工艺","国家标准"};//;LoadConfig.getInstance().getTechnicsType()[1];
//		String [] ttypes = new String[technicsTypes.length+1];
//		ttypes[0] = "请选择工艺类型";
//		for(int i=0;i<technicsTypes.length;i++){
//			ttypes[i+1] = technicsTypes[i];
//		}
		loadTable();
	}

	private String convertNull(Object str) {
		return str == null ? "" : str.toString();
	}

	public JButton getSureButton() {
		return sureButton;
	}

}
