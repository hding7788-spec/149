package com.glaway.mpm.view;

import com.glaway.mpm.util.*;
import com.glaway.mpm.visual.view.VaContext;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.glaway.mpm.wcIntf.PBomIntf;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.*;

public class GwPbomQuotaPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JPanel middleMainPanel;
	private JScrollPane jScrollPane;

	private JTable jTable;

	private JButton batchSetAllButton;
	private JButton selectAllButton;
	private JButton cancelSelectAllButton;
	private JButton sureButton;
	private JButton cancelButton;

	public static Vector<VaTreeNode> parts;
	private Map<String, Double> partCountMap;

	private JDialog dialog;
	private NewPartJPanel newPartJPanel;

	public GwPbomQuotaPanel(JDialog dialog, NewPartJPanel newPartJPanel) {
		this.dialog = dialog;
		this.newPartJPanel = newPartJPanel;
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
		middleMainPanel = new JPanel();
		jScrollPane = new JScrollPane();
		jTable = new JTable();

		batchSetAllButton = new JButton();
		selectAllButton = new JButton();
		cancelSelectAllButton = new JButton();
		sureButton = new JButton();
		cancelButton = new JButton();

		batchSetAllButton.setText("批量设置数量");
		selectAllButton.setText("全部选中");
		cancelSelectAllButton.setText("取消选中");
		sureButton.setText("确定");
		cancelButton.setText("取消");
	}

	private void initLayout() {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 1;
		c.gridy = 1;

		middleMainPanel.setLayout(new GridBagLayout());
		jTable.setRowHeight(23);
		jTable.getTableHeader().setPreferredSize(new Dimension(20, 25));
		jScrollPane.setViewportView(jTable);
		jScrollPane.setPreferredSize(new Dimension(1560, 650));
		middleMainPanel.add(jScrollPane, c);

		c.gridy = 2;

		batchSetAllButton.setPreferredSize(new Dimension(160, 25));
		selectAllButton.setPreferredSize(new Dimension(100, 25));
		cancelSelectAllButton.setPreferredSize(new Dimension(100, 25));
		sureButton.setPreferredSize(new Dimension(100, 25));
		cancelButton.setPreferredSize(new Dimension(100, 25));

		c.insets = new Insets(30, 430, 5, 0);
		middleMainPanel.add(batchSetAllButton, c);

		c.insets = new Insets(30, 600, 5, 0);
		middleMainPanel.add(selectAllButton, c);

		c.insets = new Insets(30, 710, 5, 0);
		middleMainPanel.add(cancelSelectAllButton, c);

		c.insets = new Insets(30, 820, 5, 0);
		middleMainPanel.add(sureButton, c);

		c.insets = new Insets(30, 930, 5, 20);
		middleMainPanel.add(cancelButton, c);

		this.add(middleMainPanel);
	}

	private DefaultTableModel getModel(Object[][] tableValue) {
		DefaultTableModel model = new DefaultTableModel(tableValue,
				new String[] { "object", "选择", "零部件编号", "零部件名称", "型号牌号", "规格","技术条件","备注","使用数量","剩余数量","参装数量","替代件" }) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				if(column == 1 || column == 10){
					return true;
				}
				return false;
			}
		};
		return model;
	}

	private void initActions() {
		batchSetAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String count = JOptionPane.showInputDialog(dialog, "请输入批量设置的参装数量:");
				if(count == null || "".equals(count)){
					return;
				}
				if(!StringUtils.isNumeric(count)){
					SwingUtil.showMessageDialog("请输入数字", "提示", 1);
				}
				double sl = Double.valueOf(count);
				int rowCount = jTable.getRowCount();
				for(int i = 0; i < rowCount; i++) {
					boolean isSelected = (Boolean) jTable.getValueAt(i, 1);
					if(isSelected){
						jTable.setValueAt(String.valueOf(sl),i,10);
					}
				}
			}
		});

		selectAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int rowCount = jTable.getRowCount();
				for(int i = 0; i < rowCount; i++) {
					jTable.setValueAt(true,i,1);
				}
			}
		});

		cancelSelectAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int rowCount = jTable.getRowCount();
				for(int i = 0; i < rowCount; i++) {
					jTable.setValueAt(false,i,1);
				}
			}
		});

		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parts = new Vector<VaTreeNode>();
				int rowCount = jTable.getRowCount();
				for(int i = 0; i < rowCount; i++) {
					boolean isSelected = (Boolean) jTable.getValueAt(i, 1);
					if(isSelected){
						String number = (String) jTable.getValueAt(i, 2);
						String count = (String) jTable.getValueAt(i, 10);
						double cz = 0;
						try {
							cz = Double.valueOf(count);
						} catch(NumberFormatException ex) {
							SwingUtil.showMessageDialog("编号" + number + "的参装数量请输入数字！", "提示", 1);
							return;
						}
						if(cz <= 0 || cz > (Double)jTable.getValueAt(i, 9)) {
							SwingUtil.showMessageDialog("编号" + number + "的参装数量需大于0且不能大于剩余数量！", "提示", 1);
							return;
						}
						VaTreeNode treeNode = (VaTreeNode) jTable.getValueAt(i, 0);
						treeNode.getPart().setAmount(cz);
						parts.add(treeNode);
					}
				}
				dialog.dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parts = null;
				dialog.dispose();
			}
		});
	}

	private void loadInitDatas() {
		//加载数据
		try {
			List<VaTreeNode> treeNodes = loadData();
			Object[][] data = new Object[treeNodes.size()][12];
			for(int i = 0; i < treeNodes.size(); i++) {
				VaTreeNode node = treeNodes.get(i);
				data[i][0] = node;
				data[i][1] = false;
				data[i][2] = node.getPart().getNumber();
				data[i][3] = node.getPart().getName();
				data[i][4] = node.getPart().getXhph();
				data[i][5] = node.getPart().getGg();
				data[i][6] = node.getPart().getJstj();
				data[i][7] = node.getPart().getRemark();
				data[i][8] = node.getPart().getAmount();
				data[i][9] = node.getzCount();
				data[i][10] = String.valueOf(node.getzCount());
				data[i][11] = node.getReplaceableParts();
				}
			jTable.setModel(getModel(data));
			TableColumnModel columnModel = jTable.getColumnModel();
			columnModel.getColumn(1).setCellEditor(jTable.getDefaultEditor(Boolean.class));
			columnModel.getColumn(1).setCellRenderer(jTable.getDefaultRenderer(Boolean.class));
			jTable.getTableHeader().setReorderingAllowed(false);
			jTable.getTableHeader().setResizingAllowed(true);
			TableColumn column = columnModel.getColumn(0);
			column.setMaxWidth(0);
			column.setMinWidth(0);
			column.setPreferredWidth(0);
			column.setWidth(0);
		} catch(RemoteException e) {
			throw new RuntimeException(e);
		} catch(InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private List<VaTreeNode> loadData() throws RemoteException, InvocationTargetException {
		List<VaTreeNode> treeNodes = new ArrayList<VaTreeNode>();
		NewTechnicsPart frame = newPartJPanel.getFrame();
		Document doc = frame.getCurrentTechnics();
		if(doc != null) {
			Element tech = XmlUtility.getTechnicsElement(doc);
			String partNum = tech.attributeValue("partNumber");
			String path = WorkSpaceUtil.getTechnicsPath(tech.attributeValue("technicsNumber"));
			Document document = XmlUtil.getDocument(new File(path));
			Element rootElement = document.getRootElement();
			Element technicsEle = XmlUtil.getElementsByName(rootElement, "QMFawTechnicsInfo").get(0);
			List<Element> stepElements = technicsEle.selectNodes("steps/QMProcedureInfo");
			List<Element> paceElements = technicsEle.selectNodes("steps/QMProcedureInfo/paces/QMProcedureInfo");
			partCountMap = new HashMap<String, Double>();
			getPartZCount(stepElements,partCountMap);
			getPartZCount(paceElements,partCountMap);
			List<Element> elements = buildTreePbom(partNum);
			for(Element e : elements) {
				List<Element> list = e.elements();
				for (Element e1 : list) {
					// 第二层QMPartInfo层
					List<Element> childList = e1.elements();
					for (Element e2 : childList) {
						String partNumber = e2.attributeValue("partNumber");
						String middleIndex = e2.attributeValue("middleIndex");
						String[] middleIndexs = null;

						if (middleIndex.indexOf(e.attributeValue("partNumber")) > 0) {
							middleIndexs = middleIndex.split("&" + e.attributeValue("partNumber") + "&");
							middleIndexs = middleIndexs[0].split(",");
						} else {
							middleIndexs = middleIndex.split(",");
						}
						VaTreeNode brother = null;
						String gysl = BomXMLUtil.getGYSLFromWNC(e2, e2.attributeValue("partNumber"));
						String curOccId;
						String curOccpath;

						//如果没有模型，则合并展示
						curOccId = partNumber;
						String key = partNumber + "_" + curOccId;
						curOccpath = "-1+" + curOccId;
						brother = VaTree.addTreeNode(e2, curOccId, curOccpath, middleIndexs[0]);
						double zCount = Double.valueOf(gysl);
						if(partCountMap.containsKey(key)){
							zCount = CommonUtil.subDouble(Double.valueOf(gysl),partCountMap.get(key));
						}
						brother.setzCount(zCount);
						brother.setcCount(CommonUtil.subDouble(Double.valueOf(gysl), zCount));
						brother.setFlag("ZPBOM");
						brother.getPart().setAmount(Double.valueOf(gysl));
						brother.getPart().setzCount(brother.getzCount());
						brother.getPart().setcCount(brother.getcCount());
						brother.getPart().setRootType("ZPBOM");
						brother.getPart().setZcmark("Z");
						if(brother.getzCount() == 0){
							brother.setUsed(true);
						}else{
							brother.setUsed(false);
						}
						//可替代件信息
						List<String> wtPartUsageList = PBomIntf.getWTPartUsageList(String.valueOf(brother.getPart().getOid()));
						brother.setReplaceableParts(StringUtils.join(wtPartUsageList, "、"));
						treeNodes.add(brother);
					}
				}
			}
		}
		return treeNodes;
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

	/**
	 * 获取工序工步已参装物资的数量
	 * @param stepElements
	 * @param paceElements
	 * @return
	 */
	private void getPartZCount(List<Element> elementList, Map<String, Double> partCountMap) {
		for (Element stepEle : elementList) {
			List<Element> partsEleList = stepEle.selectNodes("parts/QMPartInfo");
			for (Element partEle : partsEleList) {
				String partNumber = partEle.attributeValue("partNumber");
				String occId = partEle.attributeValue("occId");
				String key = partNumber + "_" + occId;
				String zcMark = partEle.attributeValue("ZCMARK");
				double amount = Double.valueOf(partEle.attributeValue("useCount"));
				if (partCountMap.containsKey(key)) {
					if ("Z".equals(zcMark)) {
						partCountMap.put(key, CommonUtil.addDouble(partCountMap.get(key), amount));
					} else if ("C".equals(zcMark)) {
						partCountMap.put(key, CommonUtil.subDouble(partCountMap.get(key), amount));
					}
				} else {
					if ("Z".equals(zcMark)) {
						partCountMap.put(key, CommonUtil.addDouble(0, amount));
					} else if ("C".equals(zcMark)) {
						partCountMap.put(key, CommonUtil.subDouble(0, amount));
					}
				}

			}
		}
	}

	public static List<Element> buildTreePbom(String currNum) {
		byte[] bytes = ((NewTechnicsPart) VaContext.getMainFrame()).pbomBytes;
		if (bytes == null) {
			JOptionPane.showMessageDialog(null, "PBom_xml获取失败!\nPartNumber: " + currNum);
			return null;
		}
		Document doc = null;
		try {
			String xml = new String(bytes, "GBK");
			doc = DocumentHelper.parseText(xml);
			String uri = doc.getRootElement().getNamespaceURI();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("xx", uri);
			String prefix = " /Product/parts/QMPartInfo";
			String queryField = "[@partNumber='" + currNum + "']";
			XPath xpath = DocumentHelper.createXPath(prefix + queryField);
			xpath.setNamespaceURIs(map);
			List<Element> list = xpath.selectNodes(doc);

			int index = 0;
			if (list.size() == 0) {
				while (true) {
					index++;
					prefix += "/childs";
					xpath = DocumentHelper.createXPath(prefix);
					List<Element> listForCheck = xpath.selectNodes(doc);
					prefix += "/QMPartInfo";
					if (listForCheck.size() > 0) {
						xpath = DocumentHelper.createXPath(prefix + queryField);
						list = xpath.selectNodes(doc);
						if (list.size() > 0) {
							break;
						}
					}else{
						if(index>50){//只往下找50层，找不到就跳出
							break;
						}
					}
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}