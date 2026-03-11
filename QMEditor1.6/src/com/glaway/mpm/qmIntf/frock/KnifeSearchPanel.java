package com.glaway.mpm.qmIntf.frock;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Hashtable;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.glaway.mpm.model.KnifeTool;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewKnifeToolPanel;
import com.glaway.mpm.view.NewToolJPanel;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class KnifeSearchPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private VaLogger logger = VaLogger.getLogger(this.getClass());

	private JPanel topPanel;
	private JPanel topMainPanel;
	private JPanel mainPanel;
	private JPanel middlePanel;
	private JPanel middleMainPanel;
	private JScrollPane jScrollPane;

	//编号
	private JLabel numberLabel;
	private JTextField number;
	//名称
	private JLabel nameLabel;
	private JTextField name;
	//类别
	private JLabel knifetypeLabel;
	private JTextField knifetype;
	//材料
	private JLabel cmatLabel;
	private JTextField cmat;
	//刃口直径
	private JLabel rkzjLabel;
	private JTextField rkzj;
	//夹持直径
	private JLabel jczjLabel;
	private JTextField jczj;
	//刃口长度
	private JLabel rkcdLabel;
	private JTextField rkcd;
	//总长度
	private JLabel zcdLabel;
	private JTextField zcd;
	//公差
	private JLabel gcLabel;
	private JTextField gc;
	//最小加工尺寸
	private JLabel zxjgccLabel;
	private JTextField zxjgcc;
	//最大加工尺寸
	private JLabel zdjgccLabel;
	private JTextField zdjgcc;
	//结构形式
	private JLabel jgxsLabel;
	private JTextField jgxs;
	//接口类型
	private JLabel jklxLabel;
	private JTextField jklx;
	//技术备注
	private JLabel jsbzLabel;
	private JTextField jsbz;
	//刃口圆角半径
	private JLabel rkyjbjLabel;
	private JTextField rkyjbj;
	//齿数
	private JLabel csLabel;
	private JTextField cs;

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

	public KnifeSearchPanel(JDialog dialog, NewToolJPanel panel) {
		this.dialog = dialog;
		this.panel = panel;
		init();
	}

	public KnifeSearchPanel(JDialog dialog, NewKnifeToolPanel panel) {
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

		numberLabel = new JLabel();
		number = new JTextField();

		nameLabel = new JLabel();
		name = new JTextField();

		knifetypeLabel = new JLabel();
		knifetype = new JTextField();

		cmatLabel = new JLabel();
		cmat = new JTextField();

		rkzjLabel = new JLabel();
		rkzj = new JTextField();

		jczjLabel = new JLabel();
		jczj = new JTextField();

		rkcdLabel = new JLabel();
		rkcd = new JTextField();

		zcdLabel = new JLabel();
		zcd = new JTextField();

		gcLabel = new JLabel();
		gc = new JTextField();

		zxjgccLabel = new JLabel();
		zxjgcc = new JTextField();

		zdjgccLabel = new JLabel();
		zdjgcc = new JTextField();

		jgxsLabel = new JLabel();
		jgxs = new JTextField();

		jklxLabel = new JLabel();
		jklx = new JTextField();

		jsbzLabel = new JLabel();
		jsbz = new JTextField();

		rkyjbjLabel = new JLabel();
		rkyjbj = new JTextField();

		csLabel = new JLabel();
		cs = new JTextField();

		label1 = new JLabel();

		searchButton = new JButton();
		clearButton = new JButton();

		jComboBox1 = new JComboBox();

		jTable = new JTable();

		selectAllButton = new JButton();
		sureButton = new JButton();
		cancelButton = new JButton();
	}

	private void initLayout() {
		numberLabel.setPreferredSize(new Dimension(80, 25));
		number.setPreferredSize(new Dimension(150, 25));

		nameLabel.setPreferredSize(new Dimension(80, 25));
		name.setPreferredSize(new Dimension(150, 25));

		knifetypeLabel.setPreferredSize(new Dimension(80, 25));
		knifetype.setPreferredSize(new Dimension(150, 25));

		cmatLabel.setPreferredSize(new Dimension(80, 25));
		cmat.setPreferredSize(new Dimension(150, 25));

		rkzjLabel.setPreferredSize(new Dimension(80, 25));
		rkzj.setPreferredSize(new Dimension(150, 25));

		jczjLabel.setPreferredSize(new Dimension(80, 25));
		jczj.setPreferredSize(new Dimension(150, 25));

		rkcdLabel.setPreferredSize(new Dimension(80, 25));
		rkcd.setPreferredSize(new Dimension(150, 25));

		zcdLabel.setPreferredSize(new Dimension(80, 25));
		zcd.setPreferredSize(new Dimension(150, 25));

		gcLabel.setPreferredSize(new Dimension(80, 25));
		gc.setPreferredSize(new Dimension(150, 25));

		zxjgccLabel.setPreferredSize(new Dimension(80, 25));
		zxjgcc.setPreferredSize(new Dimension(150, 25));

		zdjgccLabel.setPreferredSize(new Dimension(80, 25));
		zdjgcc.setPreferredSize(new Dimension(150, 25));

		jgxsLabel.setPreferredSize(new Dimension(80, 25));
		jgxs.setPreferredSize(new Dimension(150, 25));

		jklxLabel.setPreferredSize(new Dimension(80, 25));
		jklx.setPreferredSize(new Dimension(150, 25));

		jsbzLabel.setPreferredSize(new Dimension(80, 25));
		jsbz.setPreferredSize(new Dimension(150, 25));

		rkyjbjLabel.setPreferredSize(new Dimension(80, 25));
		rkyjbj.setPreferredSize(new Dimension(150, 25));

		csLabel.setPreferredSize(new Dimension(80, 25));
		cs.setPreferredSize(new Dimension(150, 25));

		searchButton.setPreferredSize(new Dimension(90, 25));
		clearButton.setPreferredSize(new Dimension(90, 25));

		topMainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 0, 5, 5);

		//第一行
		c.gridx = 1;
		c.gridy = 1;
		topMainPanel.add(numberLabel, c);
		c.gridx = 2;
		topMainPanel.add(number, c);

		c.gridx = 3;
		topMainPanel.add(nameLabel, c);
		c.gridx = 4;
		topMainPanel.add(name, c);

		c.gridx = 5;
		topMainPanel.add(knifetypeLabel, c);
		c.gridx = 6;
		topMainPanel.add(knifetype, c);
		//第二行
		c.gridy = 2;
		c.gridx = 1;
		topMainPanel.add(cmatLabel, c);
		c.gridx = 2;
		topMainPanel.add(cmat, c);

		c.gridx = 3;
		topMainPanel.add(rkzjLabel, c);
		c.gridx = 4;
		topMainPanel.add(rkzj, c);

		c.gridx = 5;
		topMainPanel.add(jczjLabel, c);
		c.gridx = 6;
		topMainPanel.add(jczj, c);
		//第三行
		c.gridy = 3;
		c.gridx = 1;
		topMainPanel.add(rkcdLabel, c);
		c.gridx = 2;
		topMainPanel.add(rkcd, c);

		c.gridx = 3;
		topMainPanel.add(zcdLabel, c);
		c.gridx = 4;
		topMainPanel.add(zcd, c);

		c.gridx = 5;
		topMainPanel.add(gcLabel, c);
		c.gridx = 6;
		topMainPanel.add(gc, c);
		//第四行
		c.gridy = 4;
		c.gridx = 1;
		topMainPanel.add(zxjgccLabel, c);
		c.gridx = 2;
		topMainPanel.add(zxjgcc, c);

		c.gridx = 3;
		topMainPanel.add(zdjgccLabel, c);
		c.gridx = 4;
		topMainPanel.add(zdjgcc, c);

		c.gridx = 5;
		topMainPanel.add(jgxsLabel, c);
		c.gridx = 6;
		topMainPanel.add(jgxs, c);
		//第五行
		c.gridy = 5;
		c.gridx = 1;
		topMainPanel.add(jklxLabel, c);
		c.gridx = 2;
		topMainPanel.add(jklx, c);

		c.gridx = 3;
		topMainPanel.add(jsbzLabel, c);
		c.gridx = 4;
		topMainPanel.add(jsbz, c);

		c.gridx = 5;
		topMainPanel.add(rkyjbjLabel, c);
		c.gridx = 6;
		topMainPanel.add(rkyjbj, c);
		//第六行
		c.gridy = 6;
		c.gridx = 1;
		topMainPanel.add(csLabel, c);
		c.gridx = 2;
		topMainPanel.add(cs, c);

		//搜索按钮
		c.gridy = 7;
		c.gridx = 1;
		c.insets = new Insets(10, 3, 5, 5);
		topMainPanel.add(searchButton, c);

		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 2;
		c.insets = new Insets(10, 3, 5, 5);
		topMainPanel.add(clearButton, c);

//		label1.setPreferredSize(new Dimension(35, 25));
//
//		c.insets = new Insets(10, 0, 5, 5);
//		c.gridy = 3;
//		c.gridx = 1;
//		topMainPanel.add(label1, c);

//		jComboBox1.setPreferredSize(new Dimension(150, 25));
//		c.insets = new Insets(10, 0, 5, 5);
//		c.gridx = 2;
//		topMainPanel.add(jComboBox1, c);

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
		Dimension scrSize=Toolkit.getDefaultToolkit().getScreenSize();
		jScrollPane.setPreferredSize(new Dimension(scrSize.width-150, 270));
		middleMainPanel.add(jScrollPane, c);

		c.gridy = 2;

		selectAllButton.setPreferredSize(new Dimension(90, 25));
		sureButton.setPreferredSize(new Dimension(90, 25));
		cancelButton.setPreferredSize(new Dimension(90, 25));

		c.insets = new Insets(20, 290, 10, 0);
		middleMainPanel.add(selectAllButton, c);

		c.insets = new Insets(20, 390, 10, 0);
		middleMainPanel.add(sureButton, c);

		c.insets = new Insets(20, 490, 10, 20);
		middleMainPanel.add(cancelButton, c);

		middlePanel.add(middleMainPanel);

		mainPanel.setLayout(new BorderLayout(1, 2));
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(middlePanel, BorderLayout.CENTER);
		this.add(mainPanel);
	}

	private DefaultTableModel getModel(Object[][] tableValue) {
		DefaultTableModel model = new DefaultTableModel(tableValue,
				new String[] { "oid", "编号", "名称", "类别", "材料", "刃口直径", "夹持直径",
		        "刃口长度", "总长度", "公差", "最小加工尺寸", "最大加工尺寸", "结构形式", "接口类型", "技术备注", "刃口圆角半径", "齿数","英文名称" }) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return model;
	}

	private DefaultTableModel generatorModel(List<KnifeTool> frocks, int index) {
		Object[][] tableValue = null;
		if (frocks == null || frocks.size() == 0) {
			tableValue = new Object[0][18];
		} else {
			tableValue = new Object[frocks.size()][18];
			for (int i = 0; i < tableValue.length; i++) {
				KnifeTool frock = (KnifeTool) frocks.get(i);
				tableValue[i][0] = convertNull(frock.getOid());
				tableValue[i][1] = convertNull(frock.getKnifeToolNum());
				tableValue[i][2] = convertNull(frock.getKnifeToolName());
				tableValue[i][3] = convertNull(frock.getKnifetype());
				tableValue[i][4] = convertNull(frock.getCmat());
				tableValue[i][5] = convertNull(frock.getRkzj());
				tableValue[i][6] = convertNull(frock.getJczj());
				tableValue[i][7] = convertNull(frock.getRkcd());
				tableValue[i][8] = convertNull(frock.getZcd());
				tableValue[i][9] = convertNull(frock.getGc());
				tableValue[i][10] = convertNull(frock.getZxjgcc());
				tableValue[i][11] = convertNull(frock.getZdjgcc());
				tableValue[i][12] = convertNull(frock.getJgxs());
				tableValue[i][13] = convertNull(frock.getJklx());
				tableValue[i][14] = convertNull(frock.getJsbz());
				tableValue[i][15] = convertNull(frock.getRkyjbj());
				tableValue[i][16] = convertNull(frock.getCs());
				tableValue[i][17] = convertNull(frock.getEngLishName());
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

				final String knifetypeValue = CommonUtil.trim(knifetype.getText());
				final String cmatValue = CommonUtil.trim(cmat.getText());
				final String rkzjValue = CommonUtil.trim(rkzj.getText());
				final String jczjValue = CommonUtil.trim(jczj.getText());
				final String rkcdValue = CommonUtil.trim(rkcd.getText());
				final String zcdValue = CommonUtil.trim(zcd.getText());
				final String gcValue = CommonUtil.trim(gc.getText());
				final String zxjgccValue = CommonUtil.trim(zxjgcc.getText());
				final String zdjgccValue = CommonUtil.trim(zdjgcc.getText());
				final String jgxsValue = CommonUtil.trim(jgxs.getText());
				final String jklxValue = CommonUtil.trim(jklx.getText());
				final String jsbzValue = CommonUtil.trim(jsbz.getText());
				final String rkyjbjValue = CommonUtil.trim(rkyjbj.getText());
				final String csValue = CommonUtil.trim(cs.getText());

				final int index = jComboBox1.getSelectedIndex() + 1;
				logger.debug("number:" + numberValue + " name:" + nameValue);
				if (numberValue.equals("") && nameValue.equals("")&& knifetypeValue.equals("")&& cmatValue.equals("")
						&& rkzjValue.equals("")&& jczjValue.equals("")&& rkcdValue.equals("")&& zcdValue.equals("")
						&& gcValue.equals("")&& zxjgccValue.equals("")&& zdjgccValue.equals("")&& jgxsValue.equals("")
						&& jklxValue.equals("")&& jsbzValue.equals("")&& rkyjbjValue.equals("")&& csValue.equals("")) {
					List<KnifeTool> frocks = ResourceIntf.getKnifes(null,"1");

					logger.debug("search frocks====================" + frocks.size());
					jTable.setModel(generatorModel(frocks, index));
					loadTable();
					if (frocks.size() == 0) {
						SwingUtil.showMessageDialog("没用找到符合你输入条件的数据,请重试！", "提示", 1);
					}
					setAble(true);
					//SwingUtil.showMessageDialog("请输入编号或者名称", "提示", 2);
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
								if (!knifetypeValue.equals("")) {
									map.put("KNIFETYPE", knifetypeValue);
								}
								if (!cmatValue.equals("")) {
									map.put("CMAT", cmatValue);
								}
								if (!rkzjValue.equals("")) {
									map.put("RKZJ", rkzjValue);
								}
								if (!jczjValue.equals("")) {
									map.put("JCZJ", jczjValue);
								}
								if (!rkcdValue.equals("")) {
									map.put("RKCD", rkcdValue);
								}
								if (!zcdValue.equals("")) {
									map.put("ZCD", zcdValue);
								}
								if (!gcValue.equals("")) {
									map.put("GC", gcValue);
								}
								if (!zxjgccValue.equals("")) {
									map.put("ZXJGCC", zxjgccValue);
								}
								if (!zdjgccValue.equals("")) {
									map.put("ZDJGCC", zdjgccValue);
								}
								if (!jgxsValue.equals("")) {
									map.put("JGXS", jgxsValue);
								}
								if (!jklxValue.equals("")) {
									map.put("JKLX", jklxValue);
								}
								if (!jsbzValue.equals("")) {
									map.put("JSBZ", jsbzValue);
								}
								if (!rkyjbjValue.equals("")) {
									map.put("RKYJBJ", rkyjbjValue);
								}
								if (!csValue.equals("")) {
									map.put("CS", csValue);
								}

								List<KnifeTool> frocks = ResourceIntf.getKnifes(map,"1");

								logger.debug("search frocks====================" + frocks.size());
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
				knifetype.setText("");
				cmat.setText("");
				rkzj.setText("");
				jczj.setText("");
				rkcd.setText("");
				zcd.setText("");
				gc.setText("");
				zxjgcc.setText("");
				zdjgcc.setText("");
				jgxs.setText("");
				jklx.setText("");
				jsbz.setText("");
				rkyjbj.setText("");
				cs.setText("");
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
					} else {
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
		map.put("knifetype", CommonUtil.trim(convertNull(jTable.getValueAt(row, 3))));
		map.put("cmat", CommonUtil.trim(convertNull(jTable.getValueAt(row, 4))));
		map.put("rkzj", CommonUtil.trim(convertNull(jTable.getValueAt(row, 5))));
		map.put("jczj", CommonUtil.trim(convertNull(jTable.getValueAt(row, 6))));
		map.put("rkcd", CommonUtil.trim(convertNull(jTable.getValueAt(row, 7))));
		map.put("zcd", CommonUtil.trim(convertNull(jTable.getValueAt(row, 8))));
		map.put("gc", CommonUtil.trim(convertNull(jTable.getValueAt(row, 9))));
		map.put("zxjgcc", CommonUtil.trim(convertNull(jTable.getValueAt(row, 10))));
		map.put("zdjgcc", CommonUtil.trim(convertNull(jTable.getValueAt(row, 11))));
		map.put("jgxs", CommonUtil.trim(convertNull(jTable.getValueAt(row, 12))));
		map.put("jklx", CommonUtil.trim(convertNull(jTable.getValueAt(row, 13))));
		map.put("jsbz", CommonUtil.trim(convertNull(jTable.getValueAt(row, 14))));
		map.put("rkyjbj", CommonUtil.trim(convertNull(jTable.getValueAt(row, 15))));
		map.put("cs", CommonUtil.trim(convertNull(jTable.getValueAt(row, 16))));
		map.put("EnglishName", CommonUtil.trim(convertNull(jTable.getValueAt(row, 17))));
		map.put("useCount","1");
		return map;
	}

	private void loadInitDatas() {
		nameLabel.setText("名称");
		numberLabel.setText("编号");
		knifetypeLabel.setText("类别");
		cmatLabel.setText("材料");
		rkzjLabel.setText("刃口直径");
		jczjLabel.setText("夹持直径");
		rkcdLabel.setText("刃口长度");
		zcdLabel.setText("总长度");
		gcLabel.setText("公差");
		zxjgccLabel.setText("最小加工尺寸");
		zdjgccLabel.setText("最大加工尺寸");
		jgxsLabel.setText("结构形式");
		jklxLabel.setText("接口类型");
		jsbzLabel.setText("技术备注");
		rkyjbjLabel.setText("刃口圆角半径");
		csLabel.setText("齿数");

		label1.setText("类型");
		searchButton.setText("搜索");
		clearButton.setText("清除条件");
		selectAllButton.setText("全部选中");
		sureButton.setText("确定");
		cancelButton.setText("取消");
		String[] condtions = ResourceIntf.getFkSearchCondition();
		jComboBox1.setModel(new DefaultComboBoxModel(condtions));
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
		jComboBox1.setEnabled(flag);
	}
}