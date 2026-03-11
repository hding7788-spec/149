package com.glaway.mpm.qmIntf.frock;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.glaway.mpm.model.Frock;
import com.glaway.mpm.qmIntf.common.model.CommonActionButton;
import com.glaway.mpm.qmIntf.common.model.CommonButton;
import com.glaway.mpm.qmIntf.common.model.CommonComboBox;
import com.glaway.mpm.qmIntf.common.model.CommonTextField;
import com.glaway.mpm.qmIntf.resourceTree.ResourceTreePanel;
import com.glaway.mpm.qmIntf.resourceTree.model.FkNode;
import com.glaway.mpm.util.IconUtil;
import com.glaway.mpm.util.JTableUtil;
import com.glaway.mpm.util.JavaUtil;
import com.glaway.mpm.view.NewKnifeToolPanel;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.NewToolJPanel;
import com.glaway.mpm.view.TechnicsStepJPanel_XW;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class FrockCardSearchPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(FrockCardSearchPanel.class);

	private JPanel mainPanel = new JPanel();
	// 上面的panel
	private JPanel topPanel = new JPanel();
	private JPanel searchPanel = new JPanel();
	private JScrollPane searchResultPanel = new JScrollPane(
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	private JPanel navigatePanel = new JPanel();

	// 下面的panel
	private FrockCardInfoPanel frockCardInfoPanel = null;

	private JComboBox searchSelection = new CommonComboBox();
	private String[] searchCondition = new String[] { "工装编号" };
	private JTextField searchText = new CommonTextField();
	private JButton searchButton = new CommonButton("搜索");

	private JTable frockTable = new JTable();
	private String[] frockTableHeader = new String[] { "工装编号", "工装名称", "常用工装", "零件图号" };

	private String frockNumber;

	private CommonActionButton previousButton = new CommonActionButton(
			new AbstractAction("", IconUtil.getImageIcon(IconUtil.PREVIOUS_STEP)) {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (frame != null) {
						NewTechnicsPart partFrame = (NewTechnicsPart) frame;
						ResourceTreePanel panel = partFrame.getResourceTreePanel();
						Object selectedNode = panel.resourceTree.getLastSelectedPathComponent();
						if (selectedNode instanceof FkNode) {
							FkNode fkNode = (FkNode) selectedNode;
							if (fkNode != null) {
								DefaultMutableTreeNode nextNode = fkNode.getPreviousLeaf();
								while (true) {
									if (nextNode == null) {
										break;
									}
									if (nextNode instanceof FkNode) {
										FkNode nextFkNode = (FkNode) nextNode;
										Map paramMap = new HashMap();
										frockNumber = nextFkNode.getFrock().getFrockNum();
										logger.debug("frockNumber= " + frockNumber);
										paramMap.put("number", frockNumber);
										Map returnMap = ResourceIntf.showFrockCard(paramMap);
										frockCardInfoPanel.initData(returnMap);
										frockCardInfoPanel.frockTableInfo.initData(returnMap);
										TreePath path = new TreePath(nextFkNode.getPath());
										panel.resourceTree.setSelectionPath(path);
										panel.resourceTree.updateUI();
										break;
									}
									nextNode = nextNode.getPreviousLeaf();
								}
							}
						}
					}
				}
			}, "上一个工装申请卡");

	private CommonActionButton nextButton = new CommonActionButton(
			new AbstractAction("", IconUtil.getImageIcon(IconUtil.NEXT_STEP)) {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (frame != null) {
						NewTechnicsPart partFrame = (NewTechnicsPart) frame;
						ResourceTreePanel panel = partFrame.getResourceTreePanel();
						Object selectedNode = panel.resourceTree.getLastSelectedPathComponent();
						if (selectedNode instanceof FkNode) {
							FkNode fkNode = (FkNode) selectedNode;
							if (fkNode != null) {
								DefaultMutableTreeNode nextNode = fkNode.getNextLeaf();
								while (true) {
									if (nextNode == null) {
										break;
									}
									if (nextNode instanceof FkNode) {
										FkNode nextFkNode = (FkNode) nextNode;
										Map paramMap = new HashMap();
										frockNumber = nextFkNode.getFrock().getFrockNum();
										logger.debug("frockNumber= " + frockNumber);
										paramMap.put("number", frockNumber);
										Map returnMap = ResourceIntf.showFrockCard(paramMap);
										frockCardInfoPanel.initData(returnMap);
										frockCardInfoPanel.frockTableInfo.initData(returnMap);
										TreePath path = new TreePath(nextFkNode.getPath());
										panel.resourceTree.setSelectionPath(path);
										panel.resourceTree.updateUI();
										break;
									}
									nextNode = nextNode.getNextLeaf();
								}
							}
						}
					}
				}
			}, "下一个工装申请卡");

	private CommonActionButton addButton = new CommonActionButton(
			new AbstractAction("", IconUtil.getImageIcon(IconUtil.ADD_FROCK)) {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (frame != null) {
						NewTechnicsPart partFrame = (NewTechnicsPart) frame;
						if (partFrame.foregoingPanel instanceof TechnicsStepJPanel_XW) {
							TechnicsStepJPanel_XW panel = (TechnicsStepJPanel_XW) partFrame.foregoingPanel;
							panel.getTabbedPane().setSelectedIndex(1);
							NewToolJPanel toolJPanel = panel.getToolLinkPanel();
							Vector<Map<String, String>> frocks = new Vector<Map<String, String>>();
							Frock frock = ResourceIntf.getFrockByNumber(frockNumber);
							if (frock != null) {
								frocks.add(generateObjectMap(frock));
								toolJPanel.addData(frocks);
							}
						}

					}
				}
			}, "增加工装");

	private NewToolJPanel toolPanel;
	private NewKnifeToolPanel knifeToolPanel;

	private JFrame frame;
	private Map<String, String> map;

	public FrockCardSearchPanel(Map<String, String> map, NewToolJPanel toolPanel, JFrame frame) {
		this.frame = frame;
		this.map = map;
		frockCardInfoPanel = new FrockCardInfoPanel(map);
		this.toolPanel = toolPanel;
		if (map == null) {
			nextButton.setVisible(false);
			previousButton.setVisible(false);
			topPanel.setBorder(new TitledBorder(null, "搜索",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, null));
		} else {
			searchPanel.setVisible(false);
			searchResultPanel.setVisible(false);
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("number", map.get("number"));
			Map returnMap = ResourceIntf.showFrockCard(paramMap);
			frockCardInfoPanel.initData(returnMap);
			frockCardInfoPanel.frockTableInfo.initData(returnMap);
			frockNumber = map.get("number");
		}
		init();
	}

	public FrockCardSearchPanel(Map<String, String> map,
			NewKnifeToolPanel toolPanel, JFrame frame) {
		this.frame = frame;
		this.map = map;
		frockCardInfoPanel = new FrockCardInfoPanel(map);
		this.knifeToolPanel = toolPanel;
		if (map == null) {
			nextButton.setVisible(false);
			previousButton.setVisible(false);
			topPanel.setBorder(new TitledBorder(null, "搜索",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, null));
		} else {
			searchPanel.setVisible(false);
			searchResultPanel.setVisible(false);
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("number", map.get("number"));
			Map returnMap = ResourceIntf.showFrockCard(paramMap);
			frockCardInfoPanel.initData(returnMap);
			frockCardInfoPanel.frockTableInfo.initData(returnMap);
			frockNumber = map.get("number");
		}
		init();
	}

	private void init() {
		initLookAndFeel();
		initDimension();
		initLayout();
		initComponents();
		initActions();
		loadInitDatas();
	}

	private void initLookAndFeel() {

	}

	private void initDimension() {

	}

	private void initComponents() {
		searchResultPanel.setPreferredSize(new Dimension(600, 100));
		searchResultPanel.setMinimumSize(new Dimension(600, 100));

		frockCardInfoPanel.setBorder(new TitledBorder(null, "工装申请卡内容",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));

		frockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		frockTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						int row = frockTable.getSelectedRow();
						if (row != -1) {
							String number = frockTable.getValueAt(row, 0).toString();
							Map map = new HashMap();
							map.put("number", number);
							Map returnMap = ResourceIntf.showFrockCard(map);
							frockCardInfoPanel.initData(returnMap);
							frockCardInfoPanel.frockTableInfo
									.initData(returnMap);
						}
					}
				});
		frockTable.setRowHeight(23);
	}

	private void initLayout() {
		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(frockCardInfoPanel, BorderLayout.CENTER);

		topPanel.setLayout(new BorderLayout());
		topPanel.add(searchPanel, BorderLayout.NORTH);
		topPanel.add(searchResultPanel, BorderLayout.CENTER);
		topPanel.add(navigatePanel, BorderLayout.SOUTH);

		JPanel panel = new JPanel();
		searchPanel.setLayout(new BorderLayout());
		searchPanel.add(panel, BorderLayout.WEST);
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.PAGE_START;
		c1.anchor = GridBagConstraints.NORTHWEST;
		c1.insets = new Insets(5, 5, 5, 5);
		c1.gridx = 1;
		panel.add(searchSelection, c1);
		c1.gridx = 2;
		panel.add(searchText, c1);
		c1.gridx = 3;
		panel.add(searchButton, c1);

		searchResultPanel.setViewportView(frockTable);

		JPanel panel1 = new JPanel();
		navigatePanel.setLayout(new BorderLayout());
		navigatePanel.add(panel1, BorderLayout.EAST);
		panel1.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTHEAST;
		c.insets = new Insets(10, 0, 5, 0);
		c.gridx = 1;
		panel1.add(previousButton, c);
		c.insets = new Insets(10, 0, 5, 0);
		c.gridx = 2;
		panel1.add(nextButton, c);
		c.gridx = 3;
		panel1.add(addButton, c);
	}

	private void loadInitDatas() {
		searchSelection.setModel(new DefaultComboBoxModel(searchCondition));
		frockTable.setModel(JTableUtil.getModel(null, frockTableHeader));

		frockTable.getTableHeader().setReorderingAllowed(false);
		frockTable.getTableHeader().setResizingAllowed(false);
		// loadTable();
	}

	private DefaultTableModel generatorSearchTableModel(List<Map> list) {
		Object[][] tableValue = null;
		if (list != null && list.size() != 0) {
			tableValue = new Object[list.size()][4];
			for (int i = 0; i < list.size(); i++) {
				Map temp = list.get(i);
				tableValue[i][0] = temp.get("number");
				tableValue[i][1] = temp.get("name");
				tableValue[i][2] = JavaUtil.transferBoolean(String.valueOf(temp
						.get("isRegularlyTools")));
				tableValue[i][3] = temp.get("partNumber");
			}
		} else {
			tableValue = new Object[][] {};
		}
		return JTableUtil.getModel(tableValue, frockTableHeader);
	}

	private void initActions() {
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				searchSelection.getSelectedItem();
				String text = searchText.getText();
				Map<String, String> map = new HashMap<String, String>();
				map.put("number", text);
				frockTable.setModel(generatorSearchTableModel(ResourceIntf
						.showAllFrockCard(map)));
			}
		});

		if (toolPanel != null) {
			addButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int row = frockTable.getSelectedRow(); // 获得行位置
					if (row != -1) {
						String number = String.valueOf(frockTable.getValueAt(
								row, 0));

						Frock frock = ResourceIntf.getFrockByNumber(number);
						logger.debug("frock= " + frock);
						if (frock != null) {
							Vector<Map<String, String>> frocks = new Vector<Map<String, String>>();
							frocks.add(generateObjectMap(frock));
							toolPanel.addData(frocks);
						}

					}
				}
			});
		}
		if (knifeToolPanel != null) {
			addButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int row = frockTable.getSelectedRow(); // 获得行位置
					if (row != -1) {
						String number = String.valueOf(frockTable.getValueAt(
								row, 0));

						Frock frock = ResourceIntf.getFrockByNumber(number);
						logger.debug("frock= " + frock);
						if (frock != null) {
							Vector<Map<String, String>> frocks = new Vector<Map<String, String>>();
							frocks.add(generateObjectMap(frock));
							knifeToolPanel.addData(frocks);
						}

					}
				}
			});
		}
	}

	/**
	 * 根据工装编号获取工装节点
	 *
	 * @param resourceTree
	 * @param number
	 * @return
	 */
	private FkNode getFkNodeByNumber(DefaultMutableTreeNode node, String number) {
		if (node != null) {
			Enumeration children = node.children();
			while (children.hasMoreElements()) {
				Object child = children.nextElement();
				if (child instanceof FkNode) {
					FkNode childFkNode = (FkNode) child;
					String frockNum = childFkNode.getFrock().getFrockNum();
					if (frockNum.equals(number)) {
						return childFkNode;
					} else {
						FkNode returnNode = getFkNodeByNumber(
								(DefaultMutableTreeNode) child, number);
						if (returnNode != null) {
							return returnNode;
						}
					}
				} else {
					FkNode returnNode = getFkNodeByNumber(
							(DefaultMutableTreeNode) child, number);
					if (returnNode != null) {
						return returnNode;
					}
				}
			}
		}
		return null;
	}

	private Map<String, String> generateObjectMap(Frock frock) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("oid", frock.getOid());
		map.put("toolNum", frock.getFrockNum());
		map.put("toolName", frock.getFrockName());
		map.put("toolStdNum", frock.getFrockStdNum());
		map.put("toolSpec", frock.getFrockSpec());
		return map;
	}
}
