package com.glaway.mpm.view;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.dom4j.Document;
import org.dom4j.Element;


import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;

public class TechnicsTreePanel_View extends JPanel implements
		TreeSelectionListener {

	private Vector nodeState = new Vector();

	private JScrollPane scrollPane = new JScrollPane();

	private JTree typeTree = null;

	private NewTechnicsHistoryView frame = null;

	private TechnicsHistoryViewPopupMenu mainPopupMenu = null;

	public TechnicsTreePanel_View(NewTechnicsHistoryView f) {
		this.frame = f;
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		TypeNode root = new TypeNode("工艺树", TypeNode.ROOTTYPE);
		root.setUsed(false);
		typeTree = new JTree(root);
		typeTree.setCellRenderer(new XWTypeCellRenderer());
		typeTree.setRootVisible(false);
		scrollPane.getViewport().add(typeTree, null);
		typeTree.addTreeSelectionListener(this);
		scrollPane.getViewport().repaint();
		// mainPopupMenu = new TechnicsTreePopupMenu(frame, this);
		// stepPopupMenu = new TechnicsStepPopupMenu(frame,this);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		mainPopupMenu = new TechnicsHistoryViewPopupMenu(frame, this);
		// 树加监听
		typeTree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 3) {
					XWTreeNode node = getSelectedTreeNode();
					if (node != null) {
						XWTreeObject xo = node.getObject();
						mainPopupMenu.setMenuState(xo);
						if (xo instanceof XWStepTreeObject) {
							mainPopupMenu.copy.setVisible(true);
							mainPopupMenu.reviewTechnics.setVisible(false);
							mainPopupMenu.show(typeTree, e.getX(), e.getY());
						} else {
							mainPopupMenu.copy.setVisible(false);
							mainPopupMenu.reviewTechnics.setVisible(true);
							mainPopupMenu.show(typeTree, e.getX(), e.getY());
						}
					} else {
						mainPopupMenu.setMenuState(null);
					}
					// mainPopupMenu.show(frame, e.getX(), e.getY());
				}
				if (e.getButton() == 1) {
					TreePath path = typeTree.getSelectionPath();
					if (path != null) {
						Object obj = path.getLastPathComponent();
						typeTree.updateUI();
					}
				}
				if (e.getClickCount() == 2) {
					XWTreeNode node = getSelectedTreeNode();
					if (node != null) {
						XWTreeObject xo = node.getObject();
						if (xo != null) {
							if (xo instanceof XWStepTreeObject) {
								frame.showContent(0);
								String bsoID = xo.getTreeCellData()
										.attributeValue("bsoID");
								XWTreeNode temp = getStepNode(bsoID);
								if (temp != null) {
									typeTree.setSelectionPath(new TreePath(temp
											.getPath()));
								}
							} else if (xo instanceof XWTechnicsTreeObject) {
								frame.showContent(0);
								XWTreeNode temp = getCurrentTechnicsNode();
								typeTree.setSelectionPath(new TreePath(temp
										.getPath()));
							}
						}
					}
				}
			}
		});
		scrollPane.getViewport().updateUI();
	}

	private void loadTechnics() {
		try {
			// 根节点
			TypeNode root = (TypeNode) typeTree.getModel().getRoot();
			// 添加工艺类型

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void expend(XWTreeNode node) throws Exception {
		if (node != null) {
			node.expandAll();
			Vector vec = new Vector();
			Enumeration en = node.depthFirstEnumeration();
			while (en.hasMoreElements()) {
				Object temp = en.nextElement();
				if (temp instanceof DefaultMutableTreeNode) {
					DefaultMutableTreeNode no = (DefaultMutableTreeNode) temp;
					if (!no.isLeaf()) {
						vec.add(no);
					}
				}
			}
			for (int i = 0; i < vec.size(); i++) {
				XWTreeNode no = (XWTreeNode) vec.get(i);
				TreePath p = new TreePath(no.getPath());
				typeTree.expandPath(p);
			}
			typeTree.updateUI();
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		TreePath newPath = arg0.getNewLeadSelectionPath();
		if (newPath != null) {
			Object newobj = newPath.getLastPathComponent();
			if (newobj instanceof XWTreeNode) {
				XWTreeNode newone = (XWTreeNode) newobj;
				frame.treeSelectedValueChanged(newone);
			}
		}
	}

	public void hangTechnicsDocument(Document doc) throws Exception {
		if (doc == null)
			return;
		// 获得当前工艺关键的产品树和所关联的零部件数据
		Element ele = XmlUtility.getTechnicsElement(doc);
		// String oid = ele.attributeValue("oid");
		// if(oid == null || oid.trim().length() == 0)
		// {
		//
		// }
		String productNumber = XmlUtility.getAttributeValue(ele, "productNumber");
		String techNumber = XmlUtility.getAttributeValue(ele, "technicsNumber");
		String techType = XmlUtility.getAttributeValue(ele, "technicsType");
		XWTreeNode tn = findTechnicsNode(techNumber);
		// 当前工艺已经在树上
		if (tn != null) {
			typeTree.setSelectionPath(new TreePath(tn.getPath()));
			frame.showData(tn);
			return;
		}
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) typeTree
				.getModel().getRoot();
		root.removeAllChildren();
		DefaultTreeModel model = (DefaultTreeModel) typeTree.getModel();
		model.reload(root);

		XWTechnicsTreeObject xto = new XWTechnicsTreeObject(doc);
		XWTreeNode technode = new XWTreeNode(xto);
		root.add(technode);
		TreePath path = new TreePath(technode.getPath());
		typeTree.expandPath(path);
		typeTree.scrollPathToVisible(path);
		typeTree.setSelectionPath(path);
		expandAllNode(technode);
		// List list = XMLUtil.getAllSteps(xto.getTreeCellData());
		// if(list != null && list.size() > 0)
		// frame.getTechnicsRouteToolBar().setVisible(true);
	}

	public XWTreeNode findTechnicsNode(String technicsNumber) {
		if (typeTree != null && technicsNumber != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) typeTree
					.getModel().getRoot();
			Enumeration en = node.depthFirstEnumeration();
			while (en.hasMoreElements()) {
				Object temp = en.nextElement();
				if (temp instanceof XWTreeNode) {
					XWTreeNode no = (XWTreeNode) temp;
					XWTreeObject xo = no.getObject();
					if (xo instanceof XWTechnicsTreeObject) {
						XWTechnicsTreeObject to = (XWTechnicsTreeObject) xo;
						Element ele = to.getTreeCellData();
						String number = XmlUtility.getAttributeValue(ele,
								"technicsNumber");
						if (technicsNumber.equals(number))
							return no;
					}
				}
			}
		}
		return null;
	}

	public XWTreeNode getSelectedTreeNode() {
		if (typeTree == null)
			return null;
		if (typeTree.getSelectionCount() <= 0)
			return null;
		TreePath path = typeTree.getSelectionPath();
		Object obj = path.getLastPathComponent();
		if (obj instanceof XWTreeNode) {
			XWTreeNode node = (XWTreeNode) obj;
			return node;
		}
		return null;
	}

	public void refreshSelectNode(boolean expend) {
		XWTreeNode node = getSelectedTreeNode();
		XWTreeObject xo = null;
		if (node != null) {
			XWTreeObject treeObject = node.getObject();
			node.removeAllChildren();
			DefaultTreeModel model = (DefaultTreeModel) typeTree.getModel();
			model.reload(node);

			if (expend) {
				TreePath path = new TreePath(node.getPath());
				node.expand();
				typeTree.expandPath(path);
				typeTree.scrollPathToVisible(path);
			}
			typeTree.repaint();
		}
	}

	public void refreshSelectNode(XWTreeNode node, boolean expend) {
		XWTreeObject xo = null;
		if (node != null) {
			XWTreeObject treeObject = node.getObject();
			node.removeAllChildren();
			DefaultTreeModel model = (DefaultTreeModel) typeTree.getModel();
			model.reload(node);

			if (expend) {
				TreePath path = new TreePath(node.getPath());
				node.expand();
				typeTree.expandPath(path);
				typeTree.scrollPathToVisible(path);
			}
			typeTree.repaint();
		}
	}

	public void insertProcedureNode(XWTreeNode node, Element ele)
			throws Exception {
		XWTreeObject xo = node.getObject();
		if (xo instanceof XWStepTreeObject) {
			DefaultTreeModel model = (DefaultTreeModel) typeTree.getModel();
			XWTreeNode techNode = getCurrentTechnicsNode();

			XWStepTreeObject to = new XWStepTreeObject(ele);
			XWTreeNode tnode = new XWTreeNode(to);
			int index = model.getIndexOfChild(techNode, node);
			System.out.println("AA===" + index);
			if (index >= 0) {
				model.insertNodeInto(tnode, techNode, (index + 1));
				tnode.setParent(techNode);
				TreePath path = new TreePath(tnode.getPath());
				typeTree.scrollPathToVisible(path);
				typeTree.setSelectionPath(path);
				expend(tnode);
				// typeTree.updateUI();
				return;
			}
		} else {
			JOptionPane.showMessageDialog(null, "当前选中节点非零部件节点，不能添加工艺数据。", "提示",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void addProcedureNode(XWTreeNode node, Element ele) throws Exception {
		XWTreeObject xo = node.getObject();
		if (xo instanceof XWTechnicsTreeObject) {
			recordTreeState();
			XWStepTreeObject to = new XWStepTreeObject(ele);
			XWTreeNode tnode = new XWTreeNode(to);
			node.insert(tnode, node.getChildCount());
			tnode.setParent(node);
			Element techEle = xo.getTreeCellData();
			XmlUtility.addProcedure(techEle, ele);
			expend(node);
			TreePath path = new TreePath(tnode.getPath());
			typeTree.scrollPathToVisible(path);
			typeTree.setSelectionPath(path);
			typeTree.updateUI();
			collapse(node);
			reExpand(node);
			return;
		}
		// else if(xo instanceof XWStepTreeObject)
		// {
		// XWPaceTreeObject to = new XWPaceTreeObject(ele);
		// XWTreeNode tnode = new XWTreeNode(to);
		// node.insert(tnode, node.getChildCount());
		// tnode.setParent(node);
		// Element procedure = xo.getTreeCellData();
		// XMLUtil.addChildProcedure(procedure,ele);
		// TreePath path = new TreePath(tnode.getPath());
		// typeTree.scrollPathToVisible(path);
		// typeTree.setSelectionPath(path);
		// typeTree.updateUI();
		// expend(node);
		// return;
		// }
		// else if(xo instanceof XWPaceTreeObject)
		// {
		// XWPaceTreeObject to = new XWPaceTreeObject(ele);
		// XWTreeNode tnode = new XWTreeNode(to);
		// node.insert(tnode, node.getChildCount());
		// tnode.setParent(node);
		// Element procedure = xo.getTreeCellData();
		// XMLUtil.addChildProcedure(procedure,ele);
		// TreePath path = new TreePath(tnode.getPath());
		// typeTree.scrollPathToVisible(path);
		// typeTree.setSelectionPath(path);
		// typeTree.updateUI();
		// expend(node);
		// return;
		// }
		JOptionPane.showMessageDialog(null, "当前选中节点非零部件节点，不能添加工艺数据。", "提示",
				JOptionPane.INFORMATION_MESSAGE);
	}

	//
	// public void deleteOneNode(XWTreeNode node) throws Exception
	// {
	// removeNode(node);
	//
	// XWTreeObject xo = node.getObject();
	// Element ele = xo.getTreeCellData();
	// if(xo instanceof XWTechnicsTreeObject)//工艺节点
	// {
	// String technumber = XMLUtil.getAttributeValue(ele, "technicsNumber");
	// String path = WorkSpaceUtil.getTechnicsDirectory(technumber);
	// File f = new File(path);
	// WorkSpaceUtil.delete(f);
	// }
	// else//工序和工步节点
	// {
	// Element pare = ele.getParent();
	// pare.remove(ele);
	// frame.saveProcess(pare);
	// }
	// }

	public void removeNode(XWTreeNode node) {
		if (node == null)
			return;
		DefaultTreeModel model = (DefaultTreeModel) typeTree.getModel();
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node
				.getParent();
		if (parent != null) {
			parent.remove(node);
			model.reload(parent);
		}
	}

	//
	// public void delete() throws Exception
	// {
	// Vector total = getSelectedPaths();
	// if(total !=null && total.size() > 0)
	// {
	// //提示
	// int reslut =
	// JOptionPane.showConfirmDialog(this,"是否确定删除节点数据？","提示",JOptionPane.YES_NO_OPTION);
	// if(reslut == 0)
	// {
	// for(int i = 0; i < total.size(); i++)
	// {
	// XWTreeNode node = (XWTreeNode)total.get(i);
	// deleteOneNode(node);
	// }
	//
	// frame.clearRightContent();
	// }
	// }
	// }

	public Vector getSelectedPaths() {
		if (typeTree == null)
			return null;
		if (typeTree.getSelectionCount() <= 0)
			return null;
		TreePath[] paths = typeTree.getSelectionPaths();
		Vector total = new Vector();
		Vector pace = new Vector();
		Vector step = new Vector();
		Vector tech = new Vector();
		for (int i = 0; i < paths.length; i++) {
			Object obj = paths[i].getLastPathComponent();
			if (obj instanceof XWTreeNode) {
				XWTreeNode node = (XWTreeNode) obj;
				XWTreeObject treeObject = node.getObject();
				if (treeObject instanceof XWTechnicsTreeObject)// 当前选择工艺
				{
					tech.add(node);
				} else if (treeObject instanceof XWStepTreeObject)// 当前选择工序
				{
					step.add(node);
				} else if (treeObject instanceof XWPaceTreeObject)// 当前选择工步
				{
					pace.add(node);
				}
			}
		}
		total.addAll(pace);
		total.addAll(step);
		total.addAll(tech);
		return total;
	}

	public JTree getTree() {
		return typeTree;
	}

	public void expandAllNode(XWTreeNode node) throws Exception {
		if (node != null) {
			refreshSelectNode(node, false);
			node.expandAll();
			Vector vec = new Vector();
			Enumeration en = node.depthFirstEnumeration();
			while (en.hasMoreElements()) {
				Object temp = en.nextElement();
				if (temp instanceof DefaultMutableTreeNode) {
					DefaultMutableTreeNode no = (DefaultMutableTreeNode) temp;
					if (!no.isLeaf()) {
						vec.add(no);
					}
				}
			}
			for (int i = 0; i < vec.size(); i++) {
				XWTreeNode no = (XWTreeNode) vec.get(i);
				TreePath p = new TreePath(no.getPath());
				typeTree.expandPath(p);
			}
			collapse(node);
			reExpand(node);
			typeTree.updateUI();
		}
	}

	public void removeCurrentTechnicd() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) typeTree
				.getModel().getRoot();
		root.removeAllChildren();
		DefaultTreeModel model = (DefaultTreeModel) typeTree.getModel();
		model.reload(root);
	}

	public XWTreeNode getCurrentTechnicsNode() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) typeTree
				.getModel().getRoot();
		if (root.getChildCount() <= 0)
			return null;
		XWTreeNode node = (XWTreeNode) root.getChildAt(0);
		return node;
	}

	public XWTreeNode getStepNode(String stepID) {
		if (typeTree != null && stepID != null && stepID.trim().length() > 0) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) typeTree
					.getModel().getRoot();
			Enumeration en = node.depthFirstEnumeration();
			while (en.hasMoreElements()) {
				Object temp = en.nextElement();
				if (temp instanceof XWTreeNode) {
					XWTreeNode no = (XWTreeNode) temp;
					XWTreeObject xo = no.getObject();
					if (xo != null) {
						if (xo instanceof XWStepTreeObject) {
							XWStepTreeObject to = (XWStepTreeObject) xo;
							Element ele = to.getTreeCellData();
							String number = XmlUtility.getAttributeValue(ele,
									"bsoID");
							if (stepID.equals(number))
								return no;
						} else if (xo instanceof XWPaceTreeObject) {
							XWPaceTreeObject to = (XWPaceTreeObject) xo;
							Element ele = to.getTreeCellData();
							String number = XmlUtility.getAttributeValue(ele,
									"bsoID");
							if (stepID.equals(number))
								return no;
						}
					}
				}
			}
		}
		return null;
	}

	private void collapse(XWTreeNode node) {
		if (node == null)
			return;

		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			TreeNode obj = node.getChildAt(i);
			if (obj instanceof XWTreeNode) {
				XWTreeNode temp = (XWTreeNode) obj;
				if (temp.isLeaf())
					continue;
				collapse(temp);
			}
		}
		if (!(node.getObject() instanceof XWTechnicsTreeObject))// 工艺节点外都折叠
		{
			TreePath p = new TreePath(node.getPath());
			typeTree.collapsePath(p);
		}
	}

	public void recordTreeState() {
		nodeState.clear();
		// 工艺节点
		XWTreeNode techNode = getCurrentTechnicsNode();
		if (techNode != null) {
			Enumeration it = techNode.preorderEnumeration();// 前序遍历
			while (it.hasMoreElements()) {
				Object obj = it.nextElement();
				if (obj instanceof XWTreeNode) {

					XWTreeNode temp = (XWTreeNode) obj;
					if (!temp.isLeaf()) {
						TreePath path = new TreePath(temp.getPath());
						if (typeTree.isExpanded(new TreePath(temp.getPath())))// 当前树节点已展开
						{
							XWTreeObject xo = temp.getObject();
							if (xo instanceof XWStepTreeObject)// 是工序
							{
								Element step = xo.getTreeCellData();
								String bsoID = step.attributeValue("bsoID");
								if (!nodeState.contains(bsoID)) {
									nodeState.add(bsoID);
								}
							} else if (xo instanceof XWPaceTreeObject)// 是工步
							{
								Element step = xo.getTreeCellData();
								String bsoID = step.attributeValue("bsoID");
								if (!nodeState.contains(bsoID)) {
									nodeState.add(bsoID);
								}
							} else if (xo instanceof ResourceTreeObject)// 资源父节点
							{
								XWTreeNode parent = temp.getP();
								if (parent != null
										&& ((parent.getObject() instanceof XWStepTreeObject) || (parent
												.getObject() instanceof XWPaceTreeObject))) {
									Element step = parent.getObject()
											.getTreeCellData();
									String bsoID = step.attributeValue("bsoID");
									String type = String
											.valueOf(((ResourceTreeObject) xo)
													.getType());
									nodeState.add(bsoID
											+ WorkSpaceUtil.SEPARATOR + type);
								}
							} else {

							}
						}
					}
				}
			}
		}

		System.out.println("记录=====" + nodeState);
	}

	private void reExpand(XWTreeNode node) {
		if (node == null)
			return;
		if (nodeState.isEmpty())
			return;
		if (!(node.getObject() instanceof XWTechnicsTreeObject))// 工艺节点外都折叠
		{
			return;
		}
		for (int i = 0; i < nodeState.size(); i++) {
			String id = (String) nodeState.get(i);
			if (id != null) {
				int type = -1;
				int index = id.indexOf(WorkSpaceUtil.SEPARATOR);
				if (index > 0) {
					type = Integer.parseInt(id.substring((index + 1)));
					id = id.substring(0, index);
				}
				XWTreeNode step = getStepNode(id);
				if (step != null) {
					typeTree.expandPath(new TreePath(step.getPath()));
					if (type != -1) {
						int count = step.getChildCount();
						for (int j = 0; j < count; j++) {
							XWTreeNode res = (XWTreeNode) step.getChildAt(j);
							if (res.getObject() instanceof ResourceTreeObject) {
								ResourceTreeObject rt = (ResourceTreeObject) res
										.getObject();
								if (rt.getType() == type) {
									typeTree.expandPath(new TreePath(res
											.getPath()));
								}
							}
						}
					}
				}
			}
		}
	}
}