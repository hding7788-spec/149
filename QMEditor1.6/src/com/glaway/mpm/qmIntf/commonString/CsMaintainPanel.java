package com.glaway.mpm.qmIntf.commonString;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultTreeModel;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.glaway.mpm.model.CsType;
import com.glaway.mpm.model.ShopType;
import com.glaway.mpm.qmIntf.symbol.SymbolPanel;
import com.glaway.mpm.resource.Constants;
import com.glaway.mpm.resource.ResourceCache;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.JavaUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.CommonStringIntf;

public class CsMaintainPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private VaLogger logger = VaLogger.getLogger(this.getClass());
	private JButton addButton;
	private JButton deleteButton;
	private JButton modifyButton;

	private JButton addTypeButton;
	private JButton deleteTypeButton;
	private JButton modifyTypeButton;

	private JButton addToLibraryButton;

	private JButton addSymbolButton;
	private JButton deleteSymbolButton;
	private SymbolPanel symbolPanel = new SymbolPanel();

	private JPanel rightPanel;
	private JPanel rightUpPanel;
	private JPanel leftPanel;
	private JScrollPane jScrollPane;
	private CsTree csTree;
	private String filePath;
	private static String fileName = Constants.PERSONAL_CS_FILENAME;
	private JDialog parentDialog;

	public CsMaintainPanel(String filePath, JDialog parentDialog) {
		this.parentDialog = parentDialog;
		this.filePath = FileUtil.addSeperator(filePath);
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
		jScrollPane = new JScrollPane();
		csTree = CsTreeXmlUtil.xmlToCsTree(filePath, false);
		csTree.updateUI();
		SwingUtil.expandAll(csTree);
		jScrollPane.setViewportView(csTree);
		rightPanel = new JPanel();
		rightUpPanel = new JPanel();
		leftPanel = new JPanel();

		addButton = new JButton();
		deleteButton = new JButton();
		modifyButton = new JButton();

		addTypeButton = new JButton();
		deleteTypeButton = new JButton();
		modifyTypeButton = new JButton();

		addToLibraryButton = new JButton();

		addSymbolButton = new JButton();
		deleteSymbolButton = new JButton();
	}

	private void initLayout() {
		jScrollPane.getViewport().setBackground(Color.WHITE);
		jScrollPane.setPreferredSize(new Dimension(100, 360));

		addButton.setPreferredSize(new Dimension(123, 25));
		deleteButton.setPreferredSize(new Dimension(123, 25));
		modifyButton.setPreferredSize(new Dimension(123, 25));
		addTypeButton.setPreferredSize(new Dimension(123, 25));
		deleteTypeButton.setPreferredSize(new Dimension(123, 25));
		modifyTypeButton.setPreferredSize(new Dimension(123, 25));
		addToLibraryButton.setPreferredSize(new Dimension(123, 25));
		addSymbolButton.setPreferredSize(new Dimension(123, 25));
		deleteSymbolButton.setPreferredSize(new Dimension(123, 25));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.WEST;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 0, 10, 0);
		c.gridx = 1;
		rightUpPanel.setLayout(new GridBagLayout());
		c.insets = new Insets(30, 0, 10, 0);
		c.gridy = 1;
		rightUpPanel.add(addButton, c);
		c.insets = new Insets(10, 0, 10, 0);
		c.gridy = 2;
		rightUpPanel.add(deleteButton, c);
		c.gridy = 3;
		rightUpPanel.add(modifyButton, c);
		c.gridy = 4;
		rightUpPanel.add(addToLibraryButton, c);
		c.gridy = 5;
		rightUpPanel.add(addTypeButton, c);
		c.gridy = 6;
		rightUpPanel.add(deleteTypeButton, c);
		c.gridy = 7;
		rightUpPanel.add(modifyTypeButton, c);
		c.gridy = 8;
		rightUpPanel.add(addSymbolButton, c);
		c.gridy = 9;
		rightUpPanel.add(deleteSymbolButton, c);

		rightPanel.add(rightUpPanel);
		rightPanel.setPreferredSize(new Dimension(200, 100));
		this.setLayout(new BorderLayout());
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(jScrollPane, BorderLayout.CENTER);
		leftPanel.add(symbolPanel, BorderLayout.SOUTH);

		// this.add(new CsTreePanel(filePath), BorderLayout.WEST);
		this.add(leftPanel, BorderLayout.CENTER);
		this.add(rightPanel, BorderLayout.EAST);

	}

	private void initActions() {
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addNode();
			}
		});
		deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				deleteNode();
				csTree.setSelectionPath(null);
			}
		});

		modifyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				modifyNode();
			}
		});

		addTypeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addNodeType();
			}
		});

		deleteTypeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteNodeType();
				csTree.setSelectionPath(null);
			}
		});
		modifyTypeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				modifyNodeType();
			}
		});
		addToLibraryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addNodeToLibrary();
				csTree.setSelectionPath(null);
			}
		});
		addSymbolButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CsNodeTypeAddDialog dialog = new CsNodeTypeAddDialog("符号",
						parentDialog);
				String returnValue = dialog.showDialog();
				if (returnValue != null) {
					BufferedReader br = null;
					BufferedWriter bw = null;
					try {
						String path = WorkSpaceUtil
								.getPersonalTerminologyDirectory()
								+ "symbol.properties";
						br = FileUtil.getBufferedReaderByDirectPath(path,
								"utf-8");
						List<String> list = new ArrayList<String>();
						String line;
						while ((line = br.readLine()) != null) {
							list.add(line);
						}
						list.add(returnValue);
						bw = FileUtil.getBufferWriter(path, "utf-8");
						for (String temp : list) {
							bw.write(temp);
							bw.newLine();
						}
						JavaUtil.closeStream(br);
						JavaUtil.closeStream(bw);
						symbolPanel.refreshTable();
						CsTreePanel.symbolPanel.refreshTable();
					} catch (Exception e1) {
						e1.printStackTrace();
					} finally {
						JavaUtil.closeStream(br);
						JavaUtil.closeStream(bw);
					}

				}
			}
		});
		deleteSymbolButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				BufferedReader br = null;
				BufferedWriter bw = null;
				try {
					String path = WorkSpaceUtil
							.getPersonalTerminologyDirectory()
							+ "symbol.properties";
					int column = symbolPanel.jTable.getSelectedColumn();
					int row = symbolPanel.jTable.getSelectedRow();
					if (column != -1 && row != -1) {
						String str = (String) symbolPanel.jTable.getValueAt(
								row, column);
						br = FileUtil.getBufferedReaderByDirectPath(path,
								"utf-8");
						List<String> list = new ArrayList<String>();
						String line;
						while ((line = br.readLine()) != null) {
							if (!line.equals(str)) {
								list.add(line);
							}
						}
						bw = FileUtil.getBufferWriter(path, "utf-8");
						for (String temp : list) {
							bw.write(temp);
							bw.newLine();
						}
					}
					JavaUtil.closeStream(br);
					JavaUtil.closeStream(bw);
					symbolPanel.refreshTable();
					CsTreePanel.symbolPanel.refreshTable();
				} catch (Exception e1) {
					e1.printStackTrace();
				} finally {
					JavaUtil.closeStream(br);
					JavaUtil.closeStream(bw);
				}

			}
		});
	}

	private void addNode() {
		CsNodeAddPanel.returnValue = null;
		CsNodeModifyPanel.returnValue = null;
		Object node = csTree.getLastSelectedPathComponent();
		if (node == null) {
			SwingUtil.showMessageDialog(Constants.SELECT_ADD_CSTYPE,
					Constants.TIP, 2);
		} else {
			if (node instanceof CsTreeNode) {
				if (((CsTreeNode) node).getValue()
						.equals(Constants.PERSONAL_CS)
						|| ((CsTreeNode) node).getValue().equals(
								Constants.PUBLIC_CS)) {
					SwingUtil.showMessageDialog(Constants.SELECT_ADD_CSTYPE,
							Constants.TIP, 2);
					return;
				}
			}
			CsNodeAddDialog dialog = new CsNodeAddDialog(parentDialog);
			String returnValue = dialog.showDialog();
			if (returnValue != null) {
				/**
				 * 加入最后一行
				 */
				// CsTreeNode treeNode = null;
				// if (node instanceof CsNode) {
				// treeNode = (CsTreeNode) ((CsNode) node).getParent();
				// } else {
				// treeNode = (CsTreeNode) node;
				// }
				// if (!isCsExist(treeNode, returnValue)) {
				// CsNode childNode = new CsNode(returnValue);
				// treeNode.add(childNode);
				// csTree.updateUI();
				// SwingUtil.expandAll(csTree);
				// CsTreeXmlUtil.csTreeToXML(csTree, filePath + fileName);
				// SwingUtil.showMessageDialog("常用语“" + returnValue + "”新增成功",
				// Constants.TIP, 1);
				// }

				/**
				 * 加入下一行
				 */
				CsTreeNode treeNode = null;
				if (node instanceof CsNode) {
					CsNode selectedNode = (CsNode) node;
					treeNode = (CsTreeNode) selectedNode.getParent();
					if (!isCsExist(treeNode, returnValue)) {
						addCsToXml(treeNode.getValue(), returnValue,
								selectedNode.getValue());
						csTree = CsTreeXmlUtil.xmlToCsTree(filePath, false);
						csTree.updateUI();
						jScrollPane.setViewportView(csTree);
						SwingUtil.expandAll(csTree);
						String message = Constants.CS + Constants.LEFT_QUOTA
								+ returnValue + Constants.RIGHT_QUOTA
								+ Constants.ADD_SUCCESS;
						SwingUtil.showMessageDialog(message, Constants.TIP, 1);
					}
				} else {
					treeNode = (CsTreeNode) node;
					if (!isCsExist(treeNode, returnValue)) {
						CsNode childNode = new CsNode(returnValue);
						treeNode.add(childNode);
						addCsToCsTreePanel(treeNode.getValue(), returnValue);
						csTree.updateUI();
						SwingUtil.expandAll(csTree);
						CsTreeXmlUtil.csTreeToXML(csTree, filePath + fileName);
						String message = Constants.CS + Constants.LEFT_QUOTA
								+ returnValue + Constants.RIGHT_QUOTA
								+ Constants.ADD_SUCCESS;
						SwingUtil.showMessageDialog(message, Constants.TIP, 1);
					}

				}
			}
		}
	}

	private void deleteNode() {
		CsNodeAddPanel.returnValue = null;
		CsNodeModifyPanel.returnValue = null;
		Object node = csTree.getLastSelectedPathComponent();
		if (node == null || node instanceof CsTreeNode) {
			SwingUtil.showMessageDialog(Constants.SELECT_DELETE_CSTYPE,
					Constants.TIP, 2);
		} else {
			CsNode csNode = (CsNode) node;
			CsTreeNode parentNode = (CsTreeNode) csNode.getParent();
			String value = csNode.getValue();
			if (SwingUtil.showConfirmDialog("确认移除常用语“" + csNode.getValue()
					+ "”？", Constants.TIP, 2) == 0) {
				csNode.removeFromParent();
				CsTreeXmlUtil.csTreeToXML(csTree, filePath + fileName);
				removeCsFromCsTreePanel(parentNode, value);
				csTree.updateUI();
				csTree.setSelectionPath(null);
				SwingUtil.expandAll(csTree);
			}
		}
	}

	private void modifyNode() {
		CsNodeAddPanel.returnValue = null;
		CsNodeModifyPanel.returnValue = null;
		Object node = csTree.getLastSelectedPathComponent();
		if (node == null || node instanceof CsTreeNode) {
			SwingUtil.showMessageDialog("请选择需要修改的常用语", Constants.TIP, 2);
		} else {
			CsNode csNode = (CsNode) node;
			String beforeValue = csNode.getValue();
			CsNodeModifyDialog dialog = new CsNodeModifyDialog(beforeValue,
					parentDialog);
			String returnValue = dialog.showDialog();
			if (returnValue != null) {
				if (beforeValue.equals(returnValue)) {
					SwingUtil.showMessageDialog("修改的常用语与原常用语相同", Constants.TIP,
							1);
				} else {
					CsTreeNode treeNode = (CsTreeNode) csNode.getParent();
					if (!isCsExist(treeNode, returnValue)) {
						csNode.setValue(returnValue);
						CsTreeXmlUtil.csTreeToXML(csTree, filePath + fileName);

						updateCsFromCsTreePanel(treeNode.getValue(),
								beforeValue, returnValue);

						csTree.updateUI();
						SwingUtil.expandAll(csTree);
						SwingUtil
								.showMessageDialog("常用语“" + beforeValue
										+ "”修改为“" + returnValue + "”",
										Constants.TIP, 1);
					}
				}
			}
		}
	}

	private boolean isCsExist(CsTreeNode treeNode, String returnValue) {
		Enumeration<Object> childs = treeNode.children();
		while (childs.hasMoreElements()) {
			CsNode childNode = (CsNode) childs.nextElement();
			if (returnValue.equals(childNode.getValue())) {
				SwingUtil.showMessageDialog("常用语“" + returnValue + "”已存在",
						Constants.TIP, 1);
				return true;
			} else {
				continue;
			}
		}
		return false;
	}

	private boolean isCsTypeExist(CsTreeNode treeNode, String returnValue) {
		Enumeration<Object> childs = treeNode.children();
		while (childs.hasMoreElements()) {
			CsTreeNode childNode = (CsTreeNode) childs.nextElement();
			if (returnValue.equals(childNode.getValue())) {
				SwingUtil.showMessageDialog("常用语类型“" + returnValue + "”已存在",
						Constants.TIP, 1);
				return true;
			} else {
				continue;
			}
		}
		return false;
	}

	private void addNodeToLibrary() {
		CsNodeAddPanel.returnValue = null;
		CsNodeModifyPanel.returnValue = null;
		Object node = csTree.getLastSelectedPathComponent();

	    Object[] paths=csTree.getSelectionPath().getPath();
	    Object pNode=csTree.getSelectionPath().getPathComponent(paths.length-2);



		if (node == null || node instanceof CsTreeNode) {
			SwingUtil.showMessageDialog("请选择需要入库的常用语", Constants.TIP, 2);
		} else {
			CsNode csNode = (CsNode) node;

			CsTreeNode parentNode = (CsTreeNode) pNode;
			String typeString=parentNode.getValue();

			String value = csNode.getValue();
            StringBuffer buffer=new StringBuffer(value);
            buffer.append("|");
			buffer.append(typeString);

			value=buffer.toString();

			List<String> list = new ArrayList<String>();
			list.add(value);

			logger.debug("list: " + list);
			List returnValue = new ArrayList();
			try {

// 入库方法
       returnValue = CommonStringIntf.addToCsLibrary(null, list);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (returnValue == null || returnValue.size() != 2) {
				SwingUtil.showMessageDialog("常用语入库出错", Constants.TIP, 2);
				return;
			}
			String message = returnValue.get(0).toString();
			if ("success".equals(message)) {
				List<CsType> css = (List<CsType>) returnValue.get(1);
//更新公共常用语树
                         justRefreshCsTree(css);
					SwingUtil.showMessageDialog("常用语“" + value.substring(0, value.indexOf("|")) + "“已入库",
							Constants.TIP, 1);

			} else {
				SwingUtil.showMessageDialog(message, Constants.TIP, 2);
			}
		}
	}

	public void addTypeCommon(CsTreeNode csTreeNode, String returnValue) {
		CsTreeNode newTreeNode = new CsTreeNode(returnValue);
		csTreeNode.add(newTreeNode);
		csTree.updateUI();
		SwingUtil.expandAll(csTree);
		CsTreeXmlUtil.csTreeToXML(csTree, filePath + fileName);

		Enumeration csTreeNodes = getRootChild();
		CsTreeNode node = (CsTreeNode) csTreeNodes.nextElement();
		node.add(new CsTreeNode(returnValue));
		CsTreePanel.csTree.updateUI();
		SwingUtil.showMessageDialog("常用语类型“" + returnValue + "”新增成功",
				Constants.TIP, 1);
	}

	private void addNodeType() {
		CsNodeTypeAddPanel.returnValue = null;
		CsNodeTypeModifyPanel.returnValue = null;
		CsNodeTypeAddDialog dialog = new CsNodeTypeAddDialog("常用语类型",
				parentDialog);
		String returnValue = dialog.showDialog();
		if (returnValue != null) {
			CsTreeNode csTreeNode = (CsTreeNode) csTree.getRoot().children()
					.nextElement();
			if (!isCsTypeExist(csTreeNode, returnValue)) {
				/**
				 * 增加到最后一行
				 */
				// CsTreeNode newTreeNode = new CsTreeNode(returnValue);
				// csTreeNode.add(newTreeNode);
				// csTree.updateUI();
				// SwingUtil.expandAll(csTree);
				// CsTreeXmlUtil.csTreeToXML(csTree, filePath + fileName);
				// SwingUtil.showMessageDialog("常用语类型“" + returnValue + "”新增成功",
				// Constants.TIP, 1);
				/**
				 * 增加到下一行
				 */
				Object node = csTree.getLastSelectedPathComponent();
				if (node instanceof CsTreeNode) {
					CsTreeNode treeNode = (CsTreeNode) node;
					CsTreeNode subNode = (CsTreeNode) treeNode.getParent();
					if (!subNode.isRoot()) {
						Document document = XmlUtil.getDocument(filePath
								+ fileName);
						Element rootElement = document.getRootElement();
						Element titleElement = XmlUtil
								.getFirstElement(rootElement);
						int index = 0;
						List<Element> elements = (List<Element>) titleElement
								.elements();
						for (Element temp : elements) {
							index++;
							String value = treeNode.getValue();
							if (value.equals(temp.attributeValue("value"))) {
								Element element = DocumentHelper
										.createElement("type");
								element.addAttribute("value", returnValue);
								elements.add(index, element);
								XmlUtil.writeDocument(document, filePath
										+ fileName);
								addCsTypeToCsTreePanel(value, returnValue);
								break;
							}
						}
						csTree = CsTreeXmlUtil.xmlToCsTree(filePath, false);
						csTree.updateUI();
						jScrollPane.setViewportView(csTree);
						SwingUtil.expandAll(csTree);
						SwingUtil.showMessageDialog("常用语类型“" + returnValue
								+ "”新增成功", Constants.TIP, 1);
					} else {
						addTypeCommon(csTreeNode, returnValue);
					}
				} else {
					addTypeCommon(csTreeNode, returnValue);
				}
			}
		}
	}

	private void deleteNodeType() {
		CsNodeTypeAddPanel.returnValue = null;
		CsNodeTypeModifyPanel.returnValue = null;
		Object node = csTree.getLastSelectedPathComponent();
		if (!(node instanceof CsTreeNode)) {
			SwingUtil.showMessageDialog("请选择需要移除的常用语类型", Constants.TIP, 2);
		} else {
			CsTreeNode csTreeNode = (CsTreeNode) node;
			if (csTreeNode.getValue().equals("个人工艺常用语")) {
				SwingUtil.showMessageDialog("请选择需要移除的常用语类型", Constants.TIP, 2);
				return;
			}
			if (SwingUtil.showConfirmDialog(
					"确认移除常用语类型“" + csTreeNode.getValue() + "”？", Constants.TIP,
					2) == 0) {
				csTreeNode.removeFromParent();
				// 删除常用语类型
				removeCsTypeFromCsTreePanel(csTreeNode.getValue());
				CsTreeXmlUtil.csTreeToXML(csTree, filePath + fileName);
				csTree.updateUI();
				csTree.setSelectionPath(null);
				SwingUtil.expandAll(csTree);
			}
		}
	}

	private void modifyNodeType() {
		CsNodeTypeAddPanel.returnValue = null;
		CsNodeTypeModifyPanel.returnValue = null;
		Object node = csTree.getLastSelectedPathComponent();
		if (!(node instanceof CsTreeNode)) {
			SwingUtil.showMessageDialog("请选择需要修改的常用语类型", Constants.TIP, 2);
		} else {
			CsTreeNode csTreeNode = (CsTreeNode) node;
			if (csTreeNode.getValue().equals("个人工艺常用语")) {
				SwingUtil.showMessageDialog("请选择需要修改的常用语类型", Constants.TIP, 2);
				return;
			}
			String beforeValue = csTreeNode.getValue();
			CsNodeTypeModifyDialog dialog = new CsNodeTypeModifyDialog(
					beforeValue, parentDialog);
			String returnValue = dialog.showDialog();
			if (returnValue != null) {
				if (beforeValue.equals(returnValue)) {
					SwingUtil.showMessageDialog("修改的常用语类型与原常用语类型相同",
							Constants.TIP, 1);
				} else {
					if (!isCsTypeExist((CsTreeNode) csTreeNode.getParent(),
							returnValue)) {
						csTreeNode.setValue(returnValue);
						CsTreeXmlUtil.csTreeToXML(csTree, filePath + fileName);
						csTree.updateUI();
						updateCsTypeFromCsTreePanel(beforeValue, returnValue);
						SwingUtil.expandAll(csTree);
						SwingUtil
								.showMessageDialog("常用语类型“" + beforeValue
										+ "”修改为“" + returnValue + "”",
										Constants.TIP, 1);
					}
				}
			}
		}
	}

	private void loadInitDatas() {
		addButton.setText("新增常用语");
		deleteButton.setText("移除常用语");
		modifyButton.setText("修改常用语");
		addToLibraryButton.setText("常用语入库");

		addTypeButton.setText("新增常用语类型");
		deleteTypeButton.setText("移除常用语类型");
		modifyTypeButton.setText("修改常用语类型");
		addSymbolButton.setText("增加符号");
		deleteSymbolButton.setText("删除符号");
	}

	/**
	 * @Description: 常用语入库刷新常用语树
	 */
	public boolean refreshCsTree(String shopType, List<String> css) {
		Enumeration nodes = getNodes(2);
		while (nodes.hasMoreElements()) {
			CsTreeNode node = (CsTreeNode) nodes.nextElement();
			logger.debug(node.getValue());
			logger.debug("shopType oid=" + node.getShopType().getOid());
			if (node.getShopType().getOid().equals(shopType)) {
				node.removeAllChildren();
				logger.debug("css=" + css);
				if (css != null) {
					for (String cs : css) {
						logger.debug("cs=" + cs);
						node.add(new CsNode(cs));
					}
				}
				CsTreePanel.csTree.updateUI();
				return true;
			}
		}
		return false;
	}
	/**
	 * @Author caolei
	 * @Date 2015/6/3
	 * @Check caolei
	 * @Description 刷新公共常用语树
	 */
	public boolean justRefreshCsTree(List<CsType> css) {
		Enumeration nodes=getRootChild();
		while (nodes.hasMoreElements()) {
			CsTreeNode Rootnode = (CsTreeNode) nodes.nextElement();
			System.out.println(Rootnode);
			if(Rootnode.getValue().equals("公共工艺常用语")){
			Rootnode.removeAllChildren();
				if (css != null) {
					for (CsType cs : css) {
						CsTreeNode parentNode=new CsTreeNode(cs.getName());
						Rootnode.add(parentNode);
						if(cs.getCommonStrings()!=null){
							for(String s:cs.getCommonStrings()){
								parentNode.add(new CsNode(s));
							}

						}
					}
				}
				CsTreePanel.csTree.updateUI();
				return true;
		 }
		}
		return false;
	}


	/**
	 * @param cs
	 * @param elements
	 * @Description: 新增常用语刷新常用语树
	 */
	public boolean refreshCsTree(String type, String returnValue, String cs,
			List<Element> elements) {
		Enumeration nodes = getNodes(1);
		while (nodes.hasMoreElements()) {
			CsTreeNode node = (CsTreeNode) nodes.nextElement();
			logger.debug(node.getValue());
			if (node.getValue().equals(type)) {
				node.removeAllChildren();
				if (elements != null) {
					for (Element element : elements) {
						logger.debug(element.attributeValue("value"));
						node.add(new CsNode(element.attributeValue("value")));
					}
				}
				CsTreePanel.csTree.updateUI();
				return true;
			}
		}
		return false;
	}

	/**
	 * @Description: 增加常用语
	 * @param type
	 *            常用语类型
	 * @param returnValue
	 *            新增的常用语
	 * @param cs
	 *            选中的常用语,用于加到该常用语下面
	 * @return void
	 */
	private void addCsToXml(String type, String returnValue, String cs) {
		Document document = XmlUtil.getDocument(filePath + fileName);
		Element titleElement = getTitleElement(document);
		flag: for (Element temp : (List<Element>) titleElement.elements()) {
			if (type.equals(temp.attributeValue("value"))) {
				int index = 0;
				List<Element> elements = temp.elements();
				for (Element temp1 : elements) {
					index++;
					if (cs.equals(temp1.attributeValue("value"))) {
						Element element = DocumentHelper
								.createElement("commonString");
						element.addAttribute("value", returnValue);
						elements.add(index, element);
						XmlUtil.writeDocument(document, filePath + fileName);
						logger.debug(type + ":" + cs);
						refreshCsTree(type, returnValue, cs, elements);
						break flag;
					}
				}
			}
		}
	}

	private Element getTitleElement(Document document) {
		return XmlUtil.getFirstElement(document.getRootElement());
	}

	/**
	 * @Description: 增加常用语到资源树
	 */
	public boolean addCsToCsTreePanel(String type, String returnValue) {
		Enumeration nodes = getNodes(1);
		while (nodes.hasMoreElements()) {
			CsTreeNode node = (CsTreeNode) nodes.nextElement();
			logger.debug(node.getValue());
			if (node.getValue().equals(type)) {
				node.add(new CsNode(returnValue));
				CsTreePanel.csTree.updateUI();
				return true;
			}
		}
		return false;
	}

	/**
	 * @Description: 增加常用语类型到资源树
	 */
	public boolean addCsTypeToCsTreePanel(String type, String returnValue) {
		CsTreeNode rootNode = CsTreePanel.csTree.getRoot();
		DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
		Enumeration csTreeNodes = rootNode.children();
		CsTreeNode csTreeNode = (CsTreeNode) csTreeNodes.nextElement();
		Enumeration nodes = csTreeNode.children();

		while (nodes.hasMoreElements()) {
			CsTreeNode node = (CsTreeNode) nodes.nextElement();
			logger.debug(node.getValue());
			if (node.getValue().equals(type)) {
				int selectedIndex = csTreeNode.getIndex(node);
				treeModel.insertNodeInto(new CsTreeNode(returnValue),
						csTreeNode, selectedIndex + 1);
				CsTreePanel.csTree.updateUI();
				return true;
			}
		}
		return false;

	}

	/**
	 * @Description: 删除常用语到资源树
	 */
	public boolean removeCsFromCsTreePanel(CsTreeNode parentNode, String value) {
		Enumeration nodes = getNodes(1);
		while (nodes.hasMoreElements()) {
			CsTreeNode csTreeNode = (CsTreeNode) nodes.nextElement();
			if (csTreeNode.getValue().equals(parentNode.getValue())) {
				Enumeration csNodes = csTreeNode.children();
				while (csNodes.hasMoreElements()) {
					CsNode node = (CsNode) csNodes.nextElement();
					if (node.getValue().equals(value)) {
						node.removeFromParent();
						CsTreePanel.csTree.updateUI();
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @Description: 删除常用语类型到资源树
	 */
	private boolean removeCsTypeFromCsTreePanel(String value) {
		Enumeration nodes = getNodes(1);
		while (nodes.hasMoreElements()) {
			CsTreeNode csTreeNode = (CsTreeNode) nodes.nextElement();
			if (csTreeNode.getValue().equals(value)) {
				csTreeNode.removeFromParent();
				CsTreePanel.csTree.updateUI();
				return true;
			}
		}
		return false;
	}

	/**
	 * @Description: 更新常用语到资源树
	 */
	private boolean updateCsFromCsTreePanel(String value, String beforeValue,
			String returnValue) {
		Enumeration nodes = getNodes(1);
		while (nodes.hasMoreElements()) {
			CsTreeNode csTreeNode = (CsTreeNode) nodes.nextElement();
			if (csTreeNode.getValue().equals(value)) {
				Enumeration csNodes = csTreeNode.children();
				while (csNodes.hasMoreElements()) {
					CsNode node = (CsNode) csNodes.nextElement();
					if (node.getValue().equals(beforeValue)) {
						node.setValue(returnValue);
						CsTreePanel.csTree.updateUI();
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @Description: 更新常用语类型到资源树
	 */
	private boolean updateCsTypeFromCsTreePanel(String beforeValue,
			String returnValue) {
		Enumeration nodes = getNodes(1);
		while (nodes.hasMoreElements()) {
			CsTreeNode csTreeNode = (CsTreeNode) nodes.nextElement();
			if (csTreeNode.getValue().equals(beforeValue)) {
				csTreeNode.setValue(returnValue);
				CsTreePanel.csTree.updateUI();
				return true;
			}
		}
		return false;
	}

	/**
	 * @Description: 获取个人或者公共常用语子节点
	 */
	public Enumeration getNodes(int flag) {
		Enumeration csTreeNodes = getRootChild();
		if (flag == 2) {
			csTreeNodes.nextElement();
		}
		CsTreeNode csTreeNode = (CsTreeNode) csTreeNodes.nextElement();
		Enumeration nodes = csTreeNode.children();

		return csTreeNode.children();
	}

	/**
	 * @Description: 个人常用语，公共常用语
	 */
	public Enumeration getRootChild() {
		return CsTreePanel.csTree.getRoot().children();
	}
}