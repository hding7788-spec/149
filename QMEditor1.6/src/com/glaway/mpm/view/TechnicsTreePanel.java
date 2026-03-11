package com.glaway.mpm.view;

import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.IntfUtil;
import com.glaway.mpm.util.XmlUtility;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.List;
import java.util.*;

public class TechnicsTreePanel extends JPanel implements TreeSelectionListener {

	private static final long serialVersionUID = 1L;
	private Vector nodeState = new Vector();
	private NewTechnicsPart frame = null;
	private JScrollPane scrollPane = new JScrollPane();
	private JTree typeTree = null;
	private TechnicsTreePopupMenu mainPopupMenu = null;
	private TechnicsStepPopupMenu stepPopupMenu = null;
	private TechnicsPacePopupMenu pacePopupMenu = null;
	private CommonObservable co = new CommonObservable(0);

	public TechnicsTreePanel(NewTechnicsPart f) {
		NewTechnicsPart.startAnimFrame.setHeaderMessage("加载工艺树");
		this.frame = f;
		setLayout(new BorderLayout());
		add(this.scrollPane, "Center");
		TypeNode root = new TypeNode("工艺树", 0);
		this.typeTree = new JTree(root);
		this.typeTree.setCellRenderer(new XWTypeCellRenderer());
		this.typeTree.setRootVisible(false);
		this.scrollPane.getViewport().add(this.typeTree, null);
		this.typeTree.addTreeSelectionListener(this);
		this.scrollPane.getViewport().repaint();
		this.mainPopupMenu = new TechnicsTreePopupMenu(this.frame, this);
		this.stepPopupMenu = new TechnicsStepPopupMenu(this.frame, this);
		this.pacePopupMenu = new TechnicsPacePopupMenu(this.frame, this);
		this.scrollPane.setHorizontalScrollBarPolicy(30);
		this.scrollPane.setVerticalScrollBarPolicy(20);

		this.typeTree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 3) {
					XWTreeNode node = TechnicsTreePanel.this.getSelectedTreeNode();
					TechnicsTreePanel.this.frame.showContent(0);
					if (node != null) {
						XWTreeObject xo = node.getObject();
						TechnicsTreePanel.this.mainPopupMenu.setMenuState(xo);
						TechnicsTreePanel.this.stepPopupMenu.setMenuState(xo);
						if ((xo instanceof XWTechnicsTreeObject)) {
							TechnicsTreePanel.this.mainPopupMenu.show(TechnicsTreePanel.this.typeTree, e.getX(), e.getY());
						} else if ((xo instanceof XWStepTreeObject)) {
							TechnicsTreePanel.this.stepPopupMenu.show(TechnicsTreePanel.this.typeTree, e.getX(),e.getY());
						} else if(xo instanceof XWPaceTreeObject){
							TechnicsTreePanel.this.pacePopupMenu.show(TechnicsTreePanel.this.typeTree, e.getX(),e.getY());
						}
					} else {
						TechnicsTreePanel.this.mainPopupMenu.setMenuState(null);
						TechnicsTreePanel.this.stepPopupMenu.setMenuState(null);
					}
				}

				if (e.getClickCount() == 2 || e.getButton() == 1) {
					XWTreeNode node = TechnicsTreePanel.this.getSelectedTreeNode();
					if (node != null) {
						XWTreeObject xo = node.getObject();
						if (xo != null) {
							if ((xo instanceof XWStepTreeObject)) {
								TechnicsTreePanel.this.frame.showContent(0);
								String bsoID = xo.getTreeCellData().attributeValue("bsoID");
								XWTreeNode temp = TechnicsTreePanel.this.getStepNode(bsoID);
								if (temp != null) {
									TechnicsTreePanel.this.typeTree.setSelectionPath(new TreePath(temp.getPath()));
								}
							} else if ((xo instanceof XWTechnicsTreeObject)) {
								TechnicsTreePanel.this.frame.showContent(0);
								XWTreeNode temp = TechnicsTreePanel.this.getCurrentTechnicsNode();
								TechnicsTreePanel.this.typeTree.setSelectionPath(new TreePath(temp.getPath()));
								//add by machongqi 2015-6-15
								if(TechnicsTreePanel.this.frame.tecnicsJTabbedPane.getTabCount()>=2){
									TechnicsTreePanel.this.frame.tecnicsJTabbedPane.setSelectedIndex(1);
									TechnicsTreePanel.this.frame.tecnicsJTabbedPane.setSelectedIndex(0);
								}
								//add by machongqi end
							}
//							else if ((xo instanceof PartMessageTreeObject)) {
//								PartMessageTreeObject pmt = (PartMessageTreeObject) xo;
//								String identify = pmt.getDisplayName();
//								TechnicsTreePanel.this.co.setChanged();
//								TechnicsTreePanel.this.co.notifyObservers(identify);
//							}
						}
					}
					TechnicsTreePanel.this.frame.getUniversalToolBar().setToolBarEnabled();
				}
			}
			@Override
			public void mouseExited(MouseEvent e) {
				TechnicsTreePanel.this.typeTree.setToolTipText("");
			}
		});
		this.typeTree.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				Point point = e.getPoint();
				TreePath treePath = TechnicsTreePanel.this.typeTree.getPathForLocation(point.x, point.y);
				if (treePath != null) {
					Object obj = treePath.getLastPathComponent();
					if ((obj instanceof XWTreeNode)) {
						XWTreeNode node = (XWTreeNode) obj;
						XWTreeObject treeObject = node.getObject();
						if (treeObject instanceof XWStepTreeObject) {
							StringBuffer buffer = new StringBuffer();
							Element stepElement = treeObject.getTreeCellData();
							String step = stepElement.attributeValue("GXJS");
							// 简图
							Element imageElement = stepElement.element(XmlUtility.IMAGE_GROUP);
							Vector<Element> imageVec = new Vector<Element>();
							if(imageElement != null){
								for (Iterator<Element> it = imageElement.elementIterator(XmlUtility.IMAGE_TAG); it.hasNext();) {
									imageVec.add(it.next());
								}
							}
							// 工艺附表 add by liangbo
							List<Element> additionalTableElement = XmlUtility.getTechnicsAdditionTables(stepElement);
							Vector<Element> additionalTableList = new Vector<Element>();
							if (additionalTableElement != null){
								for (Element ee : additionalTableElement) {
									additionalTableList.add(ee);
								}
							}
							// 工步简图/附表 start by zhuhao 2018.0801
							Element paceElement = stepElement.element(XmlUtility.PACE_TAG);
							List<Element> paces = paceElement.elements(XmlUtility.PROCEDURE);
							for(Element pace : paces){
								Element paceImageElement = pace.element(XmlUtility.IMAGE_GROUP);
								if(paceImageElement != null){
									for (Iterator<Element> it = paceImageElement.elementIterator(XmlUtility.IMAGE_TAG); it.hasNext();) {
										imageVec.add(it.next());
									}
								}
								List<Element> paceAdditionalTableElement = XmlUtility.getTechnicsAdditionTables(pace);
								if (paceAdditionalTableElement != null){
									for (Element ee : paceAdditionalTableElement) {
										additionalTableList.add(ee);
									}
								}
							}
							// 工步简图/附表 end by zhuhao 2018.0801
							if(step != null && !"".equals(step)){
								buffer.append(step);
								buffer.append("; ");
							}
							buffer.append("有");
							buffer.append(imageVec.size());
							buffer.append("附图，");
							buffer.append(additionalTableList.size());
							buffer.append("附表");

							//System.out.println(step+"_imageVec"+imageVec.size()+"--additionalTableList"+additionalTableList.size());
							TechnicsTreePanel.this.typeTree.setToolTipText(buffer.toString());
						}else{
							TechnicsTreePanel.this.typeTree.setToolTipText("");
						}
					}else{
						TechnicsTreePanel.this.typeTree.setToolTipText("");
					}
				}else{
					TechnicsTreePanel.this.typeTree.setToolTipText("");
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		this.scrollPane.getViewport().updateUI();

		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成加载工艺树");
	}

	public void expend(XWTreeNode node) throws Exception {
		if (node != null) {
			node.expandAll();
			Vector vec = new Vector();
			Enumeration en = node.depthFirstEnumeration();
			while (en.hasMoreElements()) {
				Object temp = en.nextElement();
				if ((temp instanceof DefaultMutableTreeNode)) {
					DefaultMutableTreeNode no = (DefaultMutableTreeNode) temp;
					if (!no.isLeaf()) {
						vec.add(no);
					}
				}
			}
			for (int i = 0; i < vec.size(); i++) {
				XWTreeNode no = (XWTreeNode) vec.get(i);
				TreePath p = new TreePath(no.getPath());
				this.typeTree.expandPath(p);
			}
			this.typeTree.updateUI();
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		System.out.println("工艺树节点切换===============START===========");
		long changeStart = System.currentTimeMillis();
		TreePath oldPath = arg0.getOldLeadSelectionPath();
		TreePath newPath = arg0.getNewLeadSelectionPath();

		if(frame.getTechnicsStepJPanel().getPaceTable().dia != null) {
			JOptionPane.showMessageDialog(frame, "工步页面已打开，请先关闭工步页面！", "提示", 1);
			typeTree.removeTreeSelectionListener(this);
			typeTree.setSelectionPath(oldPath);
			typeTree.addTreeSelectionListener(this);
			return;
		}

		if(NewTechnicsPart.foregoingPanel != null){
			if ((NewTechnicsPart.foregoingPanel instanceof TechnicsStepJPanel_XW)) {
				TechnicsStepJPanel_XW step = (TechnicsStepJPanel_XW) NewTechnicsPart.foregoingPanel;
				//TODO-SOP:需要修改
				frame.sopStandardFileTableJPanel.getSopTableModel().setRowCount(0);
				frame.sopStandardFileTableJPanel.getParametersTableModel().setRowCount(0);
				if(!step.checkOperationLevel()){
					JOptionPane.showMessageDialog(frame,"操作星级为必填内容", "提示", 1);
					typeTree.removeTreeSelectionListener(this);
					typeTree.setSelectionPath(oldPath);
					typeTree.addTreeSelectionListener(this);
					return;
				}
			}
		}
		System.out.println(newPath);
		if (newPath != null) {
			Object newobj = newPath.getLastPathComponent();
			if ((newobj instanceof XWTreeNode)) {
				XWTreeNode newone = (XWTreeNode) newobj;
				this.frame.treeSelectedValueChanged(newone);
			}
		}
		long changeEnd = System.currentTimeMillis();
		System.out.println("工艺树节点切换===============END===========共耗时：" + (changeEnd - changeStart) + "ms");
	}


	public void showJsxyDoc(Document doc) {
		if (doc == null) {
			return;
		}
		Element element = XmlUtility.getTechnicsElement(doc);
		String number = element.attributeValue("technicsNumber");
		ArrayList<Map<String,String>> list = (ArrayList) IntfUtil.getPeRemoteMethodInvoke("getJsxyDocByTechnicsNumber",
					new Class[] { String.class }, new Object[] {number});
		XWTreeNode node = frame.xwPartTreePanel.getSelectedTreeNode();
		node.removeAllChildren();
		for (int i = 0; i < list.size(); i++) {
			String number1=(String) list.get(i).get("number");
			String name=(String) list.get(i).get("name");
			String version=(String) list.get(i).get("version");
            if (number1!=null) {
                JsxyDocTreeObject xto = new JsxyDocTreeObject(number1, name, version);
                XWTreeNode technode = new XWTreeNode(xto);
                if (node.getObject() instanceof TechnicsMessageTreeObject) {
                    node.add(technode);
                    node.expandAll();
                    updateUI();
                }
			}
		}

	}

	public void hangTechnicsDocument(Document doc) {
		if (doc == null) {
			return;
		}
		Element element = XmlUtility.getTechnicsElement(doc);

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.typeTree.getModel().getRoot();
		root.removeAllChildren();
		DefaultTreeModel model = (DefaultTreeModel) this.typeTree.getModel();
		model.reload(root);

		XWTreeNode treeNode = frame.xwPartTreePanel.getSelectedTreeNode().getP();

		if(treeNode != null && treeNode.isUsed()) {
			((TypeNode)root).setUsed(true);
		} else {
			((TypeNode)root).setUsed(false);
		}

		XWTechnicsTreeObject xto = new XWTechnicsTreeObject(doc);
		XWTreeNode technode = new XWTreeNode(xto);
		root.add(technode);
		TreePath path = new TreePath(technode.getPath());
		this.typeTree.expandPath(path);
		this.typeTree.scrollPathToVisible(path);
		this.typeTree.setSelectionPath(path);
		this.typeTree.repaint();

		// 展开所有工艺树上的节点
		expandAllNode(technode);
		try {
			this.frame.initTechnicsRoutePanel(element);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public XWTreeNode findTechnicsNode(String technicsNumber) {
		if ((this.typeTree != null) && (technicsNumber != null)) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.typeTree
					.getModel().getRoot();
			Enumeration en = node.depthFirstEnumeration();
			System.out.println(en);
			while (en.hasMoreElements()) {
				Object temp = en.nextElement();
				if ((temp instanceof XWTreeNode)) {
					XWTreeNode no = (XWTreeNode) temp;
					XWTreeObject xo = no.getObject();
					if ((xo instanceof XWTechnicsTreeObject)) {
						XWTechnicsTreeObject to = (XWTechnicsTreeObject) xo;
						Element ele = to.getTreeCellData();
						String number = XmlUtility.getAttributeValue(ele, "technicsNumber");
						if (technicsNumber.equals(number))
							return no;
					}
				}
			}
		}
		return null;
	}

	public XWTreeNode getSelectedTreeNode() {
		if (this.typeTree == null)
			return null;
		if (this.typeTree.getSelectionCount() <= 0)
			return null;
		TreePath path = this.typeTree.getSelectionPath();
		Object obj = path.getLastPathComponent();
		if ((obj instanceof XWTreeNode)) {
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
			DefaultTreeModel model = (DefaultTreeModel) this.typeTree.getModel();
			model.reload(node);

			if (expend) {
				TreePath path = new TreePath(node.getPath());
				node.expand();
				this.typeTree.expandPath(path);
				this.typeTree.scrollPathToVisible(path);
			}
			this.typeTree.repaint();
		}
	}

	public void refreshSelectNode(XWTreeNode node, boolean expend) {
		XWTreeObject xo = null;
		if (node != null) {
			XWTreeObject treeObject = node.getObject();
			node.removeAllChildren();
			DefaultTreeModel model = (DefaultTreeModel) this.typeTree.getModel();
			model.reload(node);

			if (expend) {
				TreePath path = new TreePath(node.getPath());
				node.expand();
				this.typeTree.expandPath(path);
				this.typeTree.scrollPathToVisible(path);
			}
			this.typeTree.repaint();
		}
	}

	public void insertProcedureNode(XWTreeNode node, Element ele)
			throws Exception {
		XWTreeObject xo = node.getObject();
		if ((xo instanceof XWStepTreeObject)) {
			DefaultTreeModel model = (DefaultTreeModel) this.typeTree.getModel();
			XWTreeNode techNode = getCurrentTechnicsNode();

			XWStepTreeObject to = new XWStepTreeObject(ele);
			XWTreeNode tnode = new XWTreeNode(to);
			int index = model.getIndexOfChild(techNode, node);
			if (index >= 0) {
				model.insertNodeInto(tnode, techNode, index + 1);
				tnode.setParent(techNode);
				TreePath path = new TreePath(tnode.getPath());
				this.typeTree.scrollPathToVisible(path);
				this.typeTree.setSelectionPath(path);
				expend(tnode);
				collapse(tnode);
			}

		} else {
			JOptionPane.showMessageDialog(null, "当前选中节点非零部件节点，不能添加工艺数据。", "提示", 1);
		}
	}

	public void addProcedureNode(XWTreeNode node, Element ele) throws Exception {
		XWTreeObject xo = node.getObject();
		if ((xo instanceof XWTechnicsTreeObject)) {
			recordTreeState();
			XWStepTreeObject to = new XWStepTreeObject(ele);
			XWTreeNode tnode = new XWTreeNode(to);
			node.insert(tnode, node.getChildCount());
			tnode.setParent(node);
			Element techEle = xo.getTreeCellData();
			XmlUtility.addProcedure(techEle, ele);
			expend(node);
			TreePath path = new TreePath(tnode.getPath());
			this.typeTree.scrollPathToVisible(path);
			this.typeTree.setSelectionPath(path);
			this.typeTree.updateUI();
			collapse(node);
			reExpand(node);
			return;
		}

		JOptionPane.showMessageDialog(null, "当前选中节点非零部件节点，不能添加工艺数据。", "提示", 1);
	}

	public void removeNode(XWTreeNode node) {
		if (node == null)
			return;
		DefaultTreeModel model = (DefaultTreeModel) this.typeTree.getModel();
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
		if (parent != null) {
			parent.remove(node);
			model.reload(parent);
		}
	}

	public Vector getSelectedPaths() {
		if (this.typeTree == null)
			return null;
		if (this.typeTree.getSelectionCount() <= 0)
			return null;
		TreePath[] paths = this.typeTree.getSelectionPaths();
		Vector total = new Vector();
		Vector pace = new Vector();
		Vector step = new Vector();
		Vector tech = new Vector();
		for (int i = 0; i < paths.length; i++) {
			Object obj = paths[i].getLastPathComponent();
			if ((obj instanceof XWTreeNode)) {
				XWTreeNode node = (XWTreeNode) obj;
				XWTreeObject treeObject = node.getObject();
				if ((treeObject instanceof XWTechnicsTreeObject)) {
					tech.add(node);
				} else if ((treeObject instanceof XWStepTreeObject)) {
					step.add(node);
				} else if ((treeObject instanceof XWPaceTreeObject)) {
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
		return this.typeTree;
	}

	public void expandAllNode(XWTreeNode node) {
		if (node != null) {
			this.frame.refreshData(node);
			refreshSelectNode(node, false);
			node.expandAll();
			Vector vec = new Vector();
			Enumeration en = node.depthFirstEnumeration();
			while (en.hasMoreElements()) {
				Object temp = en.nextElement();
				if ((temp instanceof DefaultMutableTreeNode)) {
					DefaultMutableTreeNode no = (DefaultMutableTreeNode) temp;
					if (!no.isLeaf()) {
						vec.add(no);
					}
				}
			}
			for (int i = 0; i < vec.size(); i++) {
				XWTreeNode no = (XWTreeNode) vec.get(i);
				TreePath p = new TreePath(no.getPath());
				this.typeTree.expandPath(p);
			}
			collapse(node);
			reExpand(node);
			this.typeTree.updateUI();
		}
	}

	public void expandTechnicsNode(XWTreeNode node) throws Exception {
		if (node != null) {
			this.frame.refreshData(node);
			refreshSelectNode(node, false);
			node.expandAll();
			Vector vec = new Vector();
			Enumeration en = node.depthFirstEnumeration();
			while (en.hasMoreElements()) {
				Object temp = en.nextElement();
				if ((temp instanceof DefaultMutableTreeNode)) {
					DefaultMutableTreeNode no = (DefaultMutableTreeNode) temp;
					if (!no.isLeaf()) {
						vec.add(no);
					}
				}
			}
			for (int i = 0; i < vec.size(); i++) {
				XWTreeNode no = (XWTreeNode) vec.get(i);
				TreePath p = new TreePath(no.getPath());
				this.typeTree.expandPath(p);
			}
			this.typeTree.updateUI();
		}
	}

	public void removeCurrentTechnicd() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.typeTree.getModel().getRoot();
		root.removeAllChildren();
		DefaultTreeModel model = (DefaultTreeModel) this.typeTree.getModel();
		model.reload(root);
	}

	public XWTreeNode getCurrentTechnicsNode() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.typeTree.getModel().getRoot();
		if (root.getChildCount() <= 0)
			return null;
		XWTreeNode node = (XWTreeNode) root.getChildAt(0);
		return node;
	}

	public XWTreeNode getStepNode(String stepID) {
		if ((this.typeTree != null) && (stepID != null) && (stepID.trim().length() > 0)) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.typeTree.getModel().getRoot();
			Enumeration en = node.depthFirstEnumeration();
			while (en.hasMoreElements()) {
				Object temp = en.nextElement();
				if ((temp instanceof XWTreeNode)) {
					XWTreeNode no = (XWTreeNode) temp;
					XWTreeObject xo = no.getObject();
					if (xo != null) {
						if ((xo instanceof XWStepTreeObject)) {
							XWStepTreeObject to = (XWStepTreeObject) xo;
							Element ele = to.getTreeCellData();
							String number = XmlUtility.getAttributeValue(ele, "bsoID");
							if (stepID.equals(number))
								return no;
						} else if ((xo instanceof XWPaceTreeObject)) {
							XWPaceTreeObject to = (XWPaceTreeObject) xo;
							Element ele = to.getTreeCellData();
							String number = XmlUtility.getAttributeValue(ele, "bsoID");
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
		if (node == null) {
			return;
		}
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			TreeNode obj = node.getChildAt(i);
			if ((obj instanceof XWTreeNode)) {
				XWTreeNode temp = (XWTreeNode) obj;
				if (!temp.isLeaf()) {
					collapse(temp);
				}
			}
		}
		if (!(node.getObject() instanceof XWTechnicsTreeObject)) {
			TreePath p = new TreePath(node.getPath());
			this.typeTree.collapsePath(p);
		}
	}

	public void recordTreeState() {
		this.nodeState.clear();

		XWTreeNode techNode = getCurrentTechnicsNode();
		if (techNode != null) {
			Enumeration it = techNode.preorderEnumeration();
			while (it.hasMoreElements()) {
				Object obj = it.nextElement();
				if ((obj instanceof XWTreeNode)) {
					XWTreeNode temp = (XWTreeNode) obj;
					if (!temp.isLeaf()) {
						TreePath path = new TreePath(temp.getPath());
						if (this.typeTree.isExpanded(new TreePath(temp.getPath()))) {
							XWTreeObject xo = temp.getObject();
							if ((xo instanceof XWStepTreeObject)) {
								Element step = xo.getTreeCellData();
								String bsoID = step.attributeValue("bsoID");
								if (!this.nodeState.contains(bsoID)) {
									this.nodeState.add(bsoID);
								}
							} else if ((xo instanceof XWPaceTreeObject)) {
								Element step = xo.getTreeCellData();
								String bsoID = step.attributeValue("bsoID");
								if (!this.nodeState.contains(bsoID)) {
									this.nodeState.add(bsoID);
								}
							} else if ((xo instanceof ResourceTreeObject)) {
								XWTreeNode parent = temp.getP();
								if ((parent != null)
										&& (((parent.getObject() instanceof XWStepTreeObject)) || ((parent
												.getObject() instanceof XWPaceTreeObject)))) {
									Element step = parent.getObject()
											.getTreeCellData();
									String bsoID = step.attributeValue("bsoID");
									String type = String
											.valueOf(((ResourceTreeObject) xo)
													.getType());
									this.nodeState.add(bsoID + "`" + type);
								}
							}
						}

					}

				}

			}

		}

//		System.out.println("记录=====" + this.nodeState);
	}

	public void reExpand(XWTreeNode node) {
		if (node == null)
			return;
		if (this.nodeState.isEmpty())
			return;
		if (!(node.getObject() instanceof XWTechnicsTreeObject)) {
			return;
		}
		for (int i = 0; i < this.nodeState.size(); i++) {
			String id = (String) this.nodeState.get(i);
			if (id != null) {
				int type = -1;
				int index = id.indexOf("`");
				if (index > 0) {
					type = Integer.parseInt(id.substring(index + 1));
					id = id.substring(0, index);
				}
				XWTreeNode step = getStepNode(id);
				if (step != null) {
					this.typeTree.expandPath(new TreePath(step.getPath()));
					if (type != -1) {
						int count = step.getChildCount();
						for (int j = 0; j < count; j++) {
							XWTreeNode res = (XWTreeNode) step.getChildAt(j);
							if ((res.getObject() instanceof ResourceTreeObject)) {
								ResourceTreeObject rt = (ResourceTreeObject) res.getObject();
								if (rt.getType() == type) {
									this.typeTree.expandPath(new TreePath(res.getPath()));
								}
							}
						}
					}
				}
			}
		}
	}

	public void deleteObservers() {
		this.co.deleteObservers();
	}

	public void addObserver(Observer o) {
		this.co.addObserver(o);
	}

	public Vector recordStepNodeState(XWTreeNode node) {
		Vector stateCashe = new Vector();

		if (node != null) {
			Enumeration it = node.preorderEnumeration();
			while (it.hasMoreElements()) {
				Object obj = it.nextElement();
				if ((obj instanceof XWTreeNode)) {
					XWTreeNode temp = (XWTreeNode) obj;
					if (!temp.isLeaf()) {
						TreePath path = new TreePath(temp.getPath());
						if (this.typeTree.isExpanded(new TreePath(temp
								.getPath()))) {
							XWTreeObject xo = temp.getObject();
							if ((xo instanceof XWPaceTreeObject)) {
								Element step = xo.getTreeCellData();
								String bsoID = step.attributeValue("bsoID");
								if (!stateCashe.contains(bsoID)) {
									stateCashe.add(bsoID);
								}
							} else if ((xo instanceof ResourceTreeObject)) {
								XWTreeNode parent = temp.getP();
								if ((parent != null)
										&& (((parent.getObject() instanceof XWStepTreeObject)) || ((parent
												.getObject() instanceof XWPaceTreeObject)))) {
									Element step = parent.getObject()
											.getTreeCellData();
									String bsoID = step.attributeValue("bsoID");
									String type = String
											.valueOf(((ResourceTreeObject) xo)
													.getType());
									stateCashe.add(bsoID + "`" + type);
								}
							}
						}
					}

				}

			}

		}

		return stateCashe;
	}

	public void reExpandStepNode(XWTreeNode node, Vector cashe) {
		if (node == null)
			return;
		if (cashe.isEmpty())
			return;
		if (!(node.getObject() instanceof XWStepTreeObject)) {
			return;
		}
		for (int i = 0; i < cashe.size(); i++) {
			String id = (String) cashe.get(i);
			if (id != null) {
				int type = -1;
				int index = id.indexOf("`");
				if (index > 0) {
					type = Integer.parseInt(id.substring(index + 1));
					id = id.substring(0, index);
				}
				XWTreeNode step = getStepNode(id);
				if (step != null) {
					this.typeTree.expandPath(new TreePath(step.getPath()));
					if (type != -1) {
						int count = step.getChildCount();
						for (int j = 0; j < count; j++) {
							XWTreeNode res = (XWTreeNode) step.getChildAt(j);
							if ((res.getObject() instanceof ResourceTreeObject)) {
								ResourceTreeObject rt = (ResourceTreeObject) res
										.getObject();
								if (rt.getType() == type) {
									this.typeTree.expandPath(new TreePath(res
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
