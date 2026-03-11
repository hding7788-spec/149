package com.glaway.mpm.qmIntf.material;

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

import com.glaway.mpm.model.Material;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewMaterialJPanel;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class MaterialSearchPanel extends JPanel {

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

	//编码
	private JTextField number;
	private JLabel numberLabel;

	//名称
	private JTextField name;
	private JLabel nameLabel;

	//型号
	private JTextField brand;
	private JLabel brandLabel;

	//规格
	private JTextField crision;
	private JLabel crisionLabel;

	//技术条件
	private JTextField jstj;
	private JLabel jstjLabel;

	//附加条件
	private JTextField fjtj;
	private JLabel fjtjLabel;

	 private JTextField jldw;
	 private JLabel jldwLabel;

	private JTable jTable;
	private JButton selectAllButton;
	private JButton sureButton;
	private JButton cancelButton;

	public static Vector<Map<String, String>> materials;

	private JDialog dialog;

	private NewMaterialJPanel panel;

	public MaterialSearchPanel(String materialType, String name,
			String materialBrand, boolean flag, JDialog dialog,
			NewMaterialJPanel panel) {
		this.dialog = dialog;
		this.panel = panel;
		init(materialType, name, materialBrand, flag);
	}

	private void init(String materialType, String name, String materialBrand,
			boolean flag) {
		initLookAndFeel();
		initDimension();
		initComponents();
		initLayout();
		initActions();
		loadInitDatas(materialType, name, materialBrand, flag);
	}

	private void initLookAndFeel() {

	}

	private void initDimension() {

	}

	/**
	 * @Title: initComponents
	 * @Description:
	 * @param
	 * @return void 返回类型
	 * @throws
	 */
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

		brandLabel = new JLabel();
		brand = new JTextField();

		crisionLabel = new JLabel();
		crision = new JTextField();

		jstjLabel = new JLabel();
		jstj = new JTextField();

		fjtjLabel = new JLabel();
		fjtj = new JTextField();

		jldwLabel = new JLabel();
		jldw = new JTextField();

		searchButton = new JButton();
		clearButton = new JButton();

		jTable = new JTable();

		selectAllButton = new JButton();
		sureButton = new JButton();
		cancelButton = new JButton();
	}

	private void initLayout() {
		number.setPreferredSize(new Dimension(180, 25));
		name.setPreferredSize(new Dimension(180, 25));
		brand.setPreferredSize(new Dimension(180, 25));
		crision.setPreferredSize(new Dimension(180, 25));
		jstj.setPreferredSize(new Dimension(180, 25));
		fjtj.setPreferredSize(new Dimension(180, 25));
		jldw.setPreferredSize(new Dimension(180, 25));

		numberLabel.setPreferredSize(new Dimension(70, 25));
		nameLabel.setPreferredSize(new Dimension(70, 25));
		brandLabel.setPreferredSize(new Dimension(70, 25));
		crisionLabel.setPreferredSize(new Dimension(70, 25));
		jstjLabel.setPreferredSize(new Dimension(70, 25));
		fjtjLabel.setPreferredSize(new Dimension(70, 25));
		jldwLabel.setPreferredSize(new Dimension(70, 25));

		searchButton.setPreferredSize(new Dimension(70, 25));
		clearButton.setPreferredSize(new Dimension(70, 25));

		topMainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		//名称
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 1;
		c.gridy = 1;
		topMainPanel.add(nameLabel, c);
		c.gridx = 2;
		topMainPanel.add(name, c);

		//编号
		c.insets = new Insets(10, 60, 5, 5);
		c.gridx = 3;
		topMainPanel.add(numberLabel, c);
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 4;
		topMainPanel.add(number, c);

		//型号
		c.insets = new Insets(10, 0, 5, 5);
		c.gridy = 2;
		c.gridx = 1;
		topMainPanel.add(brandLabel, c);
		c.gridx = 2;
		topMainPanel.add(brand, c);

		//规格
		c.insets = new Insets(10, 60, 5, 5);
		c.gridx = 3;
		topMainPanel.add(crisionLabel, c);
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 4;
		topMainPanel.add(crision, c);

		//技术条件
		c.gridy = 3;
		c.gridx = 1;
		topMainPanel.add(jstjLabel, c);
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 2;
		topMainPanel.add(jstj, c);

		//附加条件
		c.insets = new Insets(10, 60, 5, 5);
		c.gridx = 3;
		topMainPanel.add(fjtjLabel, c);
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 4;
		topMainPanel.add(fjtj, c);

		//计量单位
		c.gridy = 4;
		c.gridx = 1;
		topMainPanel.add(jldwLabel, c);
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 2;
		topMainPanel.add(jldw, c);


		c.insets = new Insets(10, 0, 5, 5);
		c.gridy = 5;
		c.gridx = 1;
		topMainPanel.add(searchButton, c);

		c.gridx = 2;
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
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		jScrollPane.setPreferredSize(new Dimension(size.width-250, 270));
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
				new String[] { "oid", "number", "编码", "名称", "型号", "规格", "技术条件", "计量单位", "附加条件","英文名称"}) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

		};
		return model;
	}

	private DefaultTableModel generatorModel(List<Material> materials) {
		Object[][] tableValue = null;
		if (materials == null || materials.size() == 0) {
			tableValue = new Object[0][9];
		} else {
			tableValue = new Object[materials.size()][12];
			for (int i = 0; i < tableValue.length; i++) {
				tableValue[i][0] = convertNull(materials.get(i).getOid());
				tableValue[i][1] = convertNull(materials.get(i).getNumber());
				tableValue[i][2] = convertNull(materials.get(i).getMaterialNumber());
				tableValue[i][3] = convertNull(materials.get(i).getMaterialName());
				tableValue[i][4] = convertNull(materials.get(i).getMindex());
				tableValue[i][5] = convertNull(materials.get(i).getCsize());
				tableValue[i][6] = convertNull(materials.get(i).getJstj());
				tableValue[i][7] = convertNull(materials.get(i).getJldw());
				tableValue[i][8] = convertNull(materials.get(i).getFjtj());
				tableValue[i][9] = convertNull(materials.get(i).getEnglishName());

//				tableValue[i][9] = convertNull(materials.get(i).getMaterialUnit());
//				tableValue[i][10] = convertNull(materials.get(i).getMaterialSpec());
			}
		}
		return getModel(tableValue);
	}

	private void initActions() {
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final String nameValue = CommonUtil.trim(name.getText());
				final String numberValue = CommonUtil.trim(number.getText());
				final String mindex = CommonUtil.trim(brand.getText());
				final String csize = CommonUtil.trim(crision.getText());
				final String jstjValue = CommonUtil.trim(jstj.getText());
				final String fjtjValue = CommonUtil.trim(fjtj.getText());
				final String jldwValue = CommonUtil.trim(jldw.getText());

				Thread thread = new Thread() {

					@Override
					public void run() {
						if (numberValue.equals("") && nameValue.equals("") && mindex.equals("") && csize.equals("")
								&& jstjValue.equals("") && fjtjValue.equals("") && jldwValue.equals("")) {
							List<Material> materials = ResourceIntf.getMaterials(null, "1");
							logger.debug("search materials=" + materials);
							jTable.setModel(generatorModel(materials));
							loadTable();
							if (materials.size() == 0) {
								SwingUtil.showMessageDialog("搜索结果为空", "提示", 1);
							}
							setAble(true);
							//SwingUtil.showMessageDialog("请输入查询条件", "提示", 2);
						} else {
							if (mainPanel != null) {
								setAble(false);
								Map<String, String> map = new HashMap<String, String>();
								if (!nameValue.equals("")) {
									map.put("name", nameValue);
								}
								if (!numberValue.equals("")) {
									map.put("number", numberValue);
								}
								if (!mindex.equals("")) {
									map.put("MINDEX", mindex);
								}
								if (!csize.equals("")) {
									map.put("CSIZE", csize);
								}
								if (!jstjValue.equals("")) {
									map.put("JSTJ", jstjValue);
								}
								if (!fjtjValue.equals("")) {
									map.put("FJTJ", fjtjValue);
								}
								if (!jldwValue.equals("")) {
									map.put("JLDW", jldwValue);
								}

								List<Material> materials = ResourceIntf.getMaterials(map, "1");
								logger.debug("search materials=" + materials);
								jTable.setModel(generatorModel(materials));
								loadTable();
								if (materials.size() == 0) {
									SwingUtil.showMessageDialog("搜索结果为空", "提示", 1);
								}
								setAble(true);
							}
						}
					}
				};
				thread.start();
			}
		});

		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				name.setText("");
				number.setText("");
				brand.setText("");
				crision.setText("");
				jstj.setText("");
				fjtj.setText("");
				jldw.setText("");
			}
		});
		selectAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (jTable.getRowCount() == 0) {
					SwingUtil.showMessageDialog("没有材料可以选择", "提示", 2);
				} else {
					jTable.selectAll();
				}
			}
		});
		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (jTable.getSelectedRows().length == 0) {
					SwingUtil.showMessageDialog("请选择材料", "提示", 2);
				} else {
					materials = new Vector<Map<String, String>>();
					for (int row : jTable.getSelectedRows()) {
						materials.add(generateObjectMap(row));
					}
					logger.debug("return materials=" + materials);
					dialog.dispose();
				}
			}
		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				materials = null;
				dialog.dispose();
			}
		});

		jTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1 && e.getClickCount() == 2) {
					int row = ((JTable) e.getSource()).rowAtPoint(e.getPoint()); // 获得行位置
					Vector<Map<String, String>> materials = new Vector<Map<String, String>>();
					materials.add(generateObjectMap(row));
					panel.addData(materials);
				}
			}
		});
	}

	private Map<String, String> generateObjectMap(int row) {
		Map<String, String> map = new HashMap<String, String>();
		String materialNumber = CommonUtil.trim(convertNull(jTable.getValueAt(row, 2)));
		String name = CommonUtil.trim(convertNull(jTable.getValueAt(row, 3)));
		map.put("oid", CommonUtil.trim(convertNull(jTable.getValueAt(row, 0))));
		map.put("number",CommonUtil.trim(convertNull(jTable.getValueAt(row, 1))));
		map.put("materialName",name);
		map.put("materialNumber", materialNumber);

		map.put("mindex",CommonUtil.trim(convertNull(jTable.getValueAt(row, 4))));
		map.put("csize",CommonUtil.trim(convertNull(jTable.getValueAt(row, 5))));
		map.put("jstj",CommonUtil.trim(convertNull(jTable.getValueAt(row, 6))));
		map.put("jldw",CommonUtil.trim(convertNull(jTable.getValueAt(row, 7))));
		map.put("fjtj",CommonUtil.trim(convertNull(jTable.getValueAt(row, 8))));
		map.put("EnglishName",CommonUtil.trim(convertNull(jTable.getValueAt(row, 9))));
		map.put("toolTip", number+","+name);
//		map.put("materialType", "material");
		return map;
	}

	private void loadInitDatas(String materialType, String nameValue,
			String materialBrand, boolean flag) {
		nameLabel.setText("名称");
		numberLabel.setText("编码");
		brandLabel.setText("型号");
		crisionLabel.setText("规格");
		jstjLabel.setText("技术条件");
		fjtjLabel.setText("附加条件");
		jldwLabel.setText("计量单位");

		searchButton.setText("搜索");
		clearButton.setText("清除条件");
		selectAllButton.setText("全部选中");
		sureButton.setText("确定");
		cancelButton.setText("取消");

		jTable.setModel(getModel(null));
		jTable.getTableHeader().setReorderingAllowed(false);
		jTable.getTableHeader().setResizingAllowed(false);
		loadTable();
		if (flag) {
			number.setText(convertNull(materialBrand));
			name.setText(convertNull(nameValue));
		} else {
			number.setText("");
			name.setText("");
		}

	}

	private String convertNull(Object str) {
		return str == null ? "" : str.toString();
	}

	private void loadTable() {
		hiddenCell(0);
		hiddenCell(1);
	}

	private void hiddenCell(int column) {
		TableColumn tc = jTable.getTableHeader().getColumnModel().getColumn(column);
		tc.setMaxWidth(0);
		tc.setPreferredWidth(0);
		tc.setWidth(0);
		tc.setMinWidth(0);
		jTable.getTableHeader().getColumnModel().getColumn(column).setMaxWidth(0);
		jTable.getTableHeader().getColumnModel().getColumn(column).setMinWidth(0);
	}

	public void setAble(boolean flag) {
		searchButton.setEnabled(flag);
		clearButton.setEnabled(flag);
		number.setEnabled(flag);
		name.setEnabled(flag);
		brand.setEnabled(flag);
		crision.setEnabled(flag);
		jstj.setEnabled(flag);
		jldw.setEnabled(flag);
		fjtj.setEnabled(flag);

		jTable.setEnabled(flag);
		selectAllButton.setEnabled(flag);
		sureButton.setEnabled(flag);
		cancelButton.setEnabled(flag);
	}
}