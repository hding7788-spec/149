package com.glaway.mpm.view;

import com.glaway.mpm.EditorConfig;
import com.glaway.mpm.resource.CopyCache;
import com.glaway.mpm.util.*;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.Document;
import org.dom4j.Element;
import wt.part.WTPart;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class XWPartTreePanel extends JPanel implements TreeSelectionListener {

	private static VaLogger logger = VaLogger.getLogger(XWPartTreePanel.class);

	private static final long serialVersionUID = 1L;
	private JScrollPane scrollPane = new JScrollPane();
	private   JTree partTree = null;
	public PartTreePopupMenu mainPopupMenu = null;
	private TechnicsTreeAtPartPopupMenu technicsPopupMenu = null;
	private JsxyDocPopupMenu jsxyPopupMenu = null;
	private NewTechnicsPart frame = null;
	  public static int cishu=0;
	  public static XWTreeNode cmNode;
	public static boolean isDownloadTechnics = Boolean.FALSE;
	private String[] attributes={"ZZCJ","FZCJ","STANDARDNUMBER","MECHANICALPROPERTYORHARDNESS",
			"SURFACETREATMENT","HEATTREATMENT","PRODUCTFORM","PRODUCTLEVEL","PLATECSCREWFORM",
			"ISIMPORT","SPECIALINSTRUCTION","MEASUREUNIT","TYPE","TYPESTANDARD","QUALITYLEVEL",
			"TOTALSTANDARD","DETAILSTANDARD","PACKAGINGFORM","OUTLINESIZE","SPECIALCONDITION",
			"EXTRACONDITION","MATTYPE","KEYCOMPONENT","PHASE_CODE","CINDEX","PINDEX","MTYPE"};

	private String[] sopAttributes={"ProceduceName","SpecializedType","OperationJob","Term",
			"CustomArea","ProfessionalCode","GONGXUJIANHAO"};

	public XWPartTreePanel(NewTechnicsPart frame) {
		NewTechnicsPart.startAnimFrame.setHeaderMessage("加载PBOM树");
		this.frame = frame;
		setLayout(new BorderLayout());
		add(this.scrollPane, "Center");
		this.mainPopupMenu = new PartTreePopupMenu(frame, this);
		this.technicsPopupMenu = new TechnicsTreeAtPartPopupMenu(frame, this);
		this.jsxyPopupMenu = new JsxyDocPopupMenu(frame, this);
		this.scrollPane.setHorizontalScrollBarPolicy(30);
		this.scrollPane.setVerticalScrollBarPolicy(20);
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成加载PBOM树");
	}

	public Element getSelectedTreeElement() {
		XWTreeNode node = getSelectedTreeNode();
		if (node != null) {
			return node.getObject().getTreeCellData();
		}
		return null;
	}

	public XWTreeNode getSelectedTreeNode() {
		if (this.partTree == null)
			return null;
		if (this.partTree.getSelectionCount() <= 0)
			return null;
		TreePath path = this.partTree.getSelectionPath();
		XWTreeNode node = (XWTreeNode) path.getLastPathComponent();
		return node;
	}


	private void showTree() {
		this.scrollPane.getViewport().add(this.partTree, null);
		this.partTree.addTreeSelectionListener(this);
		this.partTree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 3) {
					XWTreeNode node = XWPartTreePanel.this.getSelectedTreeNode();
					if (node != null) {
						XWTreeObject xo = node.getObject();
						XWPartTreePanel.this.mainPopupMenu.setMenuState(xo);
						XWTreeObject treeObject = node.getObject();
						if ((treeObject instanceof XWPartTreeObject)) {
							XWPartTreePanel.this.mainPopupMenu.show(XWPartTreePanel.this.partTree, e.getX(), e.getY());
						} else if ((treeObject instanceof TechnicsMessageTreeObject)) {
							TechnicsMessageTreeObject object = (TechnicsMessageTreeObject) treeObject;
							if ("rework".equals(object.getTechnicsCategory())) {
								XWPartTreePanel.this.technicsPopupMenu.setReworkMenuState(xo);
							} else if ("temp".equals(object.getTechnicsCategory())) {
								XWPartTreePanel.this.technicsPopupMenu.setReworkMenuState(xo);
							} else {
								XWPartTreePanel.this.technicsPopupMenu.setMenuState(xo);
							}
							XWPartTreePanel.this.technicsPopupMenu.show(XWPartTreePanel.this.partTree, e.getX(), e.getY());
						} else if ((treeObject instanceof ReportTechnicsTreeObject)) {
							XWPartTreePanel.this.technicsPopupMenu.setMenuState(xo);
							XWPartTreePanel.this.technicsPopupMenu.show(XWPartTreePanel.this.partTree, e.getX(), e.getY());
						}else if (treeObject instanceof JsxyDocTreeObject) {
							XWPartTreePanel.this.jsxyPopupMenu.show(XWPartTreePanel.this.partTree, e.getX(), e.getY());
							XWPartTreePanel.this.jsxyPopupMenu.setMenuState(xo);
						}

						//设置查看批次信息按钮的状态,只有PBOM树的顶层节点显示此按钮
						if(!node.isRoot() && node.getParent().equals(node.getRoot())) {
							XWPartTreePanel.this.mainPopupMenu.setShowBatchsMenuState(true);
						} else {
							XWPartTreePanel.this.mainPopupMenu.setShowBatchsMenuState(false);
						}
					} else {
						XWPartTreePanel.this.mainPopupMenu.setMenuState(null);
					}
				}
				if (e.getButton() == 1) {
					TreePath[] paths = XWPartTreePanel.this.partTree.getSelectionPaths();
					if ((paths != null) && (paths.length > 1))
						return;
					TreePath path = XWPartTreePanel.this.partTree.getSelectionPath();
					if (path != null) {
						XWTreeNode node = (XWTreeNode) path.getLastPathComponent();
						if (!(node.getObject() instanceof JsxyDocTreeObject)) {
						    if (node.getChildCount()==0) {
						        node.expand();
                            }
						    Boolean flag=false;
                              for (int i = 0; i < node.getChildCount(); i++) {
                                XWTreeNode childAt = (XWTreeNode) node.getChildAt(i);
                                if (childAt.getObject() instanceof TechnicsMessageTreeObject||childAt.getObject() instanceof ReportTechnicsTreeObject) {
                                    flag=true;
                                }
                            }
						    if (!flag) {
						        node.expand();
                            }

						}
						XWTreeObject xo = node.getObject();
						if (xo != null) {
							if ((xo instanceof TechnicsMessageTreeObject)) {
								XWPartTreePanel.this.frame.showContent(0);
								TechnicsMessageTreeObject xto = (TechnicsMessageTreeObject) xo;
								try {
									Document doc = xto.getTechnicsDocument();
									if (doc == null) {
										JOptionPane.showMessageDialog(XWPartTreePanel.this.frame, "工艺文件丢失或内容已经损坏！");
										return;
									}
									XWPartTreePanel.this.frame.loadTechnics(doc);
								    XWPartTreePanel.this.frame.loadJsxyDoc(doc);
								} catch (Exception e1) {
									e1.printStackTrace();
								}
								if (e.getClickCount() == 2) {
									XWPartTreePanel.this.frame.showTree(1);
								}
							} else if ((xo instanceof XWPartTreeObject)) {
								try {
									XWPartTreePanel.this.frame.getTechnicsTreePanel().getTree().setSelectionPath(null);
									XWPartTreePanel.this.frame.judgeValueModified();
									XWPartTreePanel.this.frame.showData(node,null);
								} catch (Exception e1) {
									e1.printStackTrace();
									JOptionPane.showMessageDialog(XWPartTreePanel.this.frame, "显示出现错误！", "提示", 1);
								}
							} else if ((xo instanceof ReportTechnicsTreeObject)) {
								try {
									XWPartTreePanel.this.frame.getTechnicsTreePanel().getTree().setSelectionPath(null);
									XWPartTreePanel.this.frame.judgeValueModified();
									XWPartTreePanel.this.frame.showData(node,null);
								} catch (Exception e1) {
									e1.printStackTrace();
									JOptionPane.showMessageDialog(XWPartTreePanel.this.frame, "显示出现错误！", "提示", 1);
								}
							}else if (xo instanceof JsxyDocTreeObject ) {
							    try {
                                    XWPartTreePanel.this.frame.getTechnicsTreePanel().getTree().setSelectionPath(null);
                                    XWPartTreePanel.this.frame.judgeValueModified();
                                    XWPartTreePanel.this.frame.showData(node,null);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                    JOptionPane.showMessageDialog(XWPartTreePanel.this.frame, "显示出现错误！", "提示", 1);
                                }
                            }else if(xo instanceof XWPartTreeObjectForSearchTech){
								try {
									XWPartTreePanel.this.frame.getTechnicsTreePanel().getTree().setSelectionPath(null);
									XWPartTreePanel.this.frame.judgeValueModified();
									XWPartTreePanel.this.frame.showData(node,null);
								} catch (Exception e1) {
									e1.printStackTrace();
									JOptionPane.showMessageDialog(XWPartTreePanel.this.frame, "显示出现错误！", "提示", 1);
								}
							}
						}
					}

					XWPartTreePanel.this.partTree.updateUI();
					XWPartTreePanel.this.frame.getUniversalToolBar().setToolBarEnabled();
				}
			}
		});
		this.scrollPane.getViewport().updateUI();
	}

	/**
	 * 根据 PBOM xml 文档  加载PBOM 树
	 * @param doc
	 * @throws Exception
	 */
	public void loadParts(Document doc) throws Exception {
		removePart();
		if (doc == null)
			return;
		XWTreeObject partObject = new XWProductTreeObject(doc);
		XWTreeNode node = new XWTreeNode(partObject);
		this.partTree = new JTree(node);
		partTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.partTree.setCellRenderer(new XWTreeCellRenderer());
		showTree();
		expandAllNode(node);
	}

	public void removePart() {
		if (this.partTree != null)
			this.scrollPane.getViewport().remove(this.partTree);
		this.partTree = null;
		this.scrollPane.getViewport().updateUI();
	}

	public void valueChanged(TreeSelectionEvent arg0) {
	}

	public void addTechnicsNode(String fileCode,String technicsType,XWTreeNode node) {
		XWTreeObject xo = node.getObject();
		if ((xo instanceof XWPartTreeObject)) {
			Element partEle = xo.getTreeCellData();
			String partNumber = XmlUtility.getAttributeValue(partEle,"partNumber");
//			String technicsNumber = WorkSpaceUtil.getTechnicsNumber(fileCode, partNumber);
			String technicsNumber = fileCode;
			String type = technicsType;
			String filepath = "";
			String technicsVersion = null;
			try {
				filepath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
				System.out.println(filepath);
				File f = new File(filepath);
				if (!f.exists())
					return;
				technicsVersion = VersionUtil.getVersionByTechnicsNumber(technicsNumber);
				if (technicsVersion == null)
					return;
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			Document doucment = XmlUtil.getDocument(filepath);
			Element technicsElement = doucment.getRootElement().element("QMFawTechnicsInfo");
			TechnicsMessageTreeObject technicsMessageTreeObject = new TechnicsMessageTreeObject(partNumber,
					technicsNumber, technicsElement.attributeValue("pplanName"),
					type, technicsVersion,
					technicsElement.attributeValue("technicsCategory"),
					technicsElement.attributeValue("PCNO"),
					technicsElement.attributeValue("pplanNumber"),
					technicsElement.attributeValue("ZFFLAG"),technicsElement.attributeValue("PPLANTYPE"));
			// PBOM树上所有partNumber相同的增加工艺节点
			XWTreeNode rootNode = (XWTreeNode) partTree.getModel().getRoot();
			addTechnicsNode(technicsMessageTreeObject, rootNode, technicsElement.attributeValue("technicsCategory"));
			return;
		}
		JOptionPane.showMessageDialog(null, "当前选中节点非零部件节点，不能添加工艺数据。", "提示", 1);
	}

	public void addTechnicsNode(XWTreeNode node, String technicsPath, String technicsVersion) {
		XWTreeObject xo = node.getObject();
		if ((xo instanceof XWPartTreeObject)) {
			Element partEle = xo.getTreeCellData();
			String partNumber = XmlUtility.getAttributeValue(partEle,"partNumber");
			String type = "";
			if ("middle".equals(partEle.attributeValue("partType"))) {
				type = "装配工艺";
			} else if ("assistant".equals(partEle.attributeValue("partType"))) {
				type = "零件工艺";
			} else {
				type = BomXMLUtil.judgeTechnicsType(partNumber);
			}
			try {
				File f = new File(technicsPath);
				if (!f.exists())
					return;
				if (technicsVersion == null)
					return;
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			Document doucment = XmlUtil.getDocument(technicsPath);
			Element technicsElement = doucment.getRootElement().element("QMFawTechnicsInfo");
			//TODO XCZ
			TechnicsMessageTreeObject technicsMessageTreeObject = new TechnicsMessageTreeObject("",
					partNumber, technicsElement.attributeValue("pplanName"),
					type, technicsVersion,
					technicsElement.attributeValue("technicsCategory"),
					technicsElement.attributeValue("PCNO"),
					technicsElement.attributeValue("pplanNumber"),
					technicsElement.attributeValue("ZFFLAG"),
					technicsElement.attributeValue("PPLANTYPE"));
			// PBOM树上所有partNumber相同的增加工艺节点
			XWTreeNode rootNode = (XWTreeNode) partTree.getModel().getRoot();
			addTechnicsNode(technicsMessageTreeObject, rootNode, technicsElement.attributeValue("technicsCategory"));
			try {
				expandAllNode(rootNode);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		JOptionPane.showMessageDialog(null, "当前选中节点非零部件节点，不能添加工艺数据。", "提示", 1);
	}

	public void addReportTechnicsNode(XWTreeNode node, String technicsNumber) {
		XWTreeObject xo = node.getObject();
		if ((xo instanceof XWPartTreeObject)) {
			String filepath = "";
			String technicsVersion = null;
			try {
				filepath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
				System.out.println(filepath);
				File f = new File(filepath);
				if (!f.exists())
					return;
				technicsVersion = VersionUtil.getVersionByTechnicsNumber(technicsNumber);
				if (technicsVersion == null)
					return;
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			Document doucment = XmlUtil.getDocument(filepath);
			Element technicsElement = doucment.getRootElement().element("XWReportTechnicsInfo");

			ReportTechnicsTreeObject treeObj = new ReportTechnicsTreeObject(technicsElement.attributeValue("partNumber"),
					technicsElement.attributeValue("pplanNumber"),
					technicsElement.attributeValue("technicsNumber"),
					technicsElement.attributeValue("version"),
					technicsElement.attributeValue("technicsType"),
					technicsElement.attributeValue("batch"),
					"normal",
					technicsElement.attributeValue("pplanName"),
					technicsElement.attributeValue("technicsName"));

			// PBOM树上所有partNumber相同的增加工艺节点
			XWTreeNode rootNode = (XWTreeNode) partTree.getModel().getRoot();
			addReportTechnicsNode(treeObj, rootNode, technicsElement.attributeValue("technicsCategory"));
			return;
		}
		JOptionPane.showMessageDialog(null, "当前选中节点非零部件节点，不能添加工艺数据。", "提示", 1);
	}

	public void addJsxyDocNode(XWTreeNode pNode,JsxyDocTreeObject node) {
		// TODO Auto-generated method stub
		if (pNode == null) {
			XWTreeNode node1 = new XWTreeNode(node);
			DefaultTreeModel model = (DefaultTreeModel) this.partTree.getModel();
			model.insertNodeInto(pNode, node1, 0);
		}
		TreePath path = new TreePath(pNode.getPath());
		this.partTree.scrollPathToVisible(path);
		this.partTree.setSelectionPath(path);
		this.partTree.updateUI();
	}
	public void addTechnicsNode(TechnicsMessageTreeObject technicsMessageTreeObject,
			XWTreeNode node, String technicsCategory) {
		Enumeration<XWTreeNode> children = node.children();
		while (children.hasMoreElements()) {
			XWTreeNode childNode = children.nextElement();
			XWTreeObject xo = childNode.getObject();
			if (xo instanceof XWPartTreeObject) {
				XWPartTreeObject object = (XWPartTreeObject) xo;
				Element element = object.getTreeCellData();
				if (technicsMessageTreeObject.getPartNumber().equals(element.attributeValue("partNumber"))) {
					// 增加
					XWTreeNode tnode = childNode.contianInChildren(technicsMessageTreeObject);
					if (tnode == null) {
						tnode = new XWTreeNode(technicsMessageTreeObject);
						DefaultTreeModel model = (DefaultTreeModel) this.partTree.getModel();
						model.insertNodeInto(tnode, childNode, 0);
					}
					TreePath path = new TreePath(tnode.getPath());
					this.partTree.scrollPathToVisible(path);
					this.partTree.setSelectionPath(path);
					this.partTree.updateUI();
				}
				addTechnicsNode(technicsMessageTreeObject, childNode, technicsCategory);
			}
		}
	}


	public void addReportTechnicsNode(ReportTechnicsTreeObject technicsMessageTreeObject,
			XWTreeNode node, String technicsCategory) {
		Enumeration<XWTreeNode> children = node.children();
		while (children.hasMoreElements()) {
			XWTreeNode childNode = children.nextElement();
			XWTreeObject xo = childNode.getObject();
			if (xo instanceof XWPartTreeObject) {
				XWPartTreeObject object = (XWPartTreeObject) xo;
				Element element = object.getTreeCellData();
				if (technicsMessageTreeObject.getPartNumber().equals(element.attributeValue("partNumber"))) {
					// 增加
					XWTreeNode tnode = childNode.contianInChildren(technicsMessageTreeObject);
					if (tnode == null) {
						tnode = new XWTreeNode(technicsMessageTreeObject);
						DefaultTreeModel model = (DefaultTreeModel) this.partTree.getModel();
						model.insertNodeInto(tnode, childNode, 0);
					}
					TreePath path = new TreePath(tnode.getPath());
					this.partTree.scrollPathToVisible(path);
					this.partTree.setSelectionPath(path);
					this.partTree.updateUI();
				}
				addReportTechnicsNode(technicsMessageTreeObject, childNode, technicsCategory);
			}
		}
	}

	public void addProcedureNode(XWTreeNode node, Element ele) {
		XWTreeObject xo = node.getObject();
		if ((xo instanceof XWTechnicsTreeObject)) {
			XWStepTreeObject to = new XWStepTreeObject(ele);
			XWTreeNode tnode = new XWTreeNode(to);
			node.insert(tnode, node.getChildCount());
			tnode.setParent(node);
			Element techEle = xo.getTreeCellData();
			XmlUtility.addProcedure(techEle, ele);
			TreePath path = new TreePath(tnode.getPath());
			this.partTree.scrollPathToVisible(path);
			this.partTree.setSelectionPath(path);
			this.partTree.updateUI();
			return;
		}
		if ((xo instanceof XWStepTreeObject)) {
			XWPaceTreeObject to = new XWPaceTreeObject(ele);
			XWTreeNode tnode = new XWTreeNode(to);
			node.insert(tnode, node.getChildCount());
			tnode.setParent(node);
			Element procedure = xo.getTreeCellData();
			XmlUtility.addChildProcedure(procedure, ele);
			TreePath path = new TreePath(tnode.getPath());
			this.partTree.scrollPathToVisible(path);
			this.partTree.setSelectionPath(path);
			this.partTree.updateUI();
			return;
		}
		if ((xo instanceof XWPaceTreeObject)) {
			XWPaceTreeObject to = new XWPaceTreeObject(ele);
			XWTreeNode tnode = new XWTreeNode(to);
			node.insert(tnode, node.getChildCount());
			tnode.setParent(node);
			Element procedure = xo.getTreeCellData();
			XmlUtility.addChildProcedure(procedure, ele);
			TreePath path = new TreePath(tnode.getPath());
			this.partTree.scrollPathToVisible(path);
			this.partTree.setSelectionPath(path);
			this.partTree.updateUI();
			return;
		}
		JOptionPane.showMessageDialog(null, "当前选中节点非零部件节点，不能添加工艺数据。", "提示", 1);
	}

	public Vector getSelectedPaths() {
		if (this.partTree == null)
			return null;
		if (this.partTree.getSelectionCount() <= 0)
			return null;
		TreePath[] paths = this.partTree.getSelectionPaths();
		Vector total = new Vector();
		Vector pace = new Vector();
		Vector step = new Vector();
		Vector tech = new Vector();
		for (int i = 0; i < paths.length; i++) {
			XWTreeNode node = (XWTreeNode) paths[i].getLastPathComponent();
			XWTreeObject treeObject = node.getObject();
			if ((treeObject instanceof XWTechnicsTreeObject)) {
				tech.add(node);
			} else if ((treeObject instanceof XWStepTreeObject)) {
				step.add(node);
			} else if ((treeObject instanceof XWPaceTreeObject)) {
				pace.add(node);
			}
		}
		total.addAll(pace);
		total.addAll(step);
		total.addAll(tech);
		return total;
	}

	public void refreshSelectNode(boolean expend) {
		XWTreeNode node = getSelectedTreeNode();
		XWTreeObject xo = null;
		if (node != null) {
			XWTreeObject treeObject = node.getObject();
			node.removeAllChildren();
			DefaultTreeModel model = (DefaultTreeModel) this.partTree.getModel();
			model.reload(node);
			if (expend) {
				TreePath path = new TreePath(node.getPath());
				node.expand();
				this.partTree.expandPath(path);
				this.partTree.scrollPathToVisible(path);
			}
			this.partTree.repaint();
		}
	}

	public void refreshSelectNode(XWTreeNode node, boolean expend) {
		XWTreeObject xo = null;
		if (node != null) {
			XWTreeObject treeObject = node.getObject();
			node.removeAllChildren();
			DefaultTreeModel model = (DefaultTreeModel) this.partTree.getModel();
			model.reload(node);
			if (expend) {
				TreePath path = new TreePath(node.getPath());
				node.expand();
				this.partTree.expandPath(path);
				this.partTree.scrollPathToVisible(path);
			}
			this.partTree.repaint();
		}
	}

	public Document getCurrentTechnicsData() {
		XWTreeNode node = getSelectedTreeNode();
		if (node != null) {
			XWTreeObject xo = node.getObject();
			if ((xo instanceof XWTechnicsTreeObject)) {
				XWTechnicsTreeObject xto = (XWTechnicsTreeObject) xo;
				return xto.getTechnicsDocument();
			}
			if ((xo instanceof XWStepTreeObject)) {
				XWTreeNode tnode = node.getP();
				XWTechnicsTreeObject xto = (XWTechnicsTreeObject) tnode.getObject();
				return xto.getTechnicsDocument();
			}
			if ((xo instanceof XWPaceTreeObject)) {
				XWTreeNode tnode = node.getP().getP();
				XWTechnicsTreeObject xto = (XWTechnicsTreeObject) tnode.getObject();
				return xto.getTechnicsDocument();
			}
		}
		return null;
	}

	public JTree getTree() {
		return this.partTree;
	}

	public Element getCurrentProductElement() {
		if (this.partTree != null) {
			Object obj = this.partTree.getModel().getRoot();
			XWTreeNode node = (XWTreeNode) obj;
			XWTreeObject xo = node.getObject();
			return xo.getTreeCellData();
		}
		return null;
	}

	public XWTreeNode findTechnicsNode(String technicsNumber) {
		if ((this.partTree != null) && (technicsNumber != null)) {
			XWTreeNode node = (XWTreeNode) this.partTree.getModel().getRoot();
			Enumeration en = node.depthFirstEnumeration();
			while (en.hasMoreElements()) {
				XWTreeNode temp = (XWTreeNode) en.nextElement();
				XWTreeObject xo = temp.getObject();
				if ((xo instanceof TechnicsMessageTreeObject)) {
					TechnicsMessageTreeObject to = (TechnicsMessageTreeObject) xo;
					String number = to.getTechnicsNumber();
					if (technicsNumber.equals(number))
						return temp;
				}
			}
		}
		return null;
	}

	public List<XWTreeNode> findAllTechnicsNodes(String technicsNumber, String technicsName) {
		List<XWTreeNode> xwTreeNodes = new ArrayList<XWTreeNode>();
		if ((this.partTree != null) && (technicsNumber != null)) {
			XWTreeNode node = (XWTreeNode) this.partTree.getModel().getRoot();
			Enumeration en = node.depthFirstEnumeration();
			while (en.hasMoreElements()) {
				XWTreeNode temp = (XWTreeNode) en.nextElement();
				XWTreeObject xo = temp.getObject();
				if ((xo instanceof TechnicsMessageTreeObject)) {
					TechnicsMessageTreeObject to = (TechnicsMessageTreeObject) xo;
					String number = to.getTechnicsNumber();
					String name = to.getTechName();
					if (technicsNumber.equals(number) && technicsName.equals(name))
						xwTreeNodes.add(temp);
				} else if (xo instanceof ReportTechnicsTreeObject) {
					ReportTechnicsTreeObject ro = (ReportTechnicsTreeObject)xo;
					String number = ro.getTechnicsNumber();
					if (technicsNumber.equals(number)) {
						xwTreeNodes.add(temp);
					}
				}
			}
		}
		return xwTreeNodes;
	}

	public XWTreeNode getPartNode(String partNumber, int row) {
		XWTreeNode curNode = null;
		XWTreeNode node = (XWTreeNode) this.partTree.getModel().getRoot();

		Enumeration en = node.breadthFirstEnumeration();

		while (en.hasMoreElements()) {
			XWTreeNode localXWTreeNode1 = (XWTreeNode) en.nextElement();
		}

		node.expand();
		if (row == 1) {
			return (XWTreeNode) node.getFirstChild();
		}
		return curNode;
	}

	public void removeNode(XWTreeNode node) {
		if (this.partTree == null)
			return;
		if (node == null)
			return;
		DefaultTreeModel model = (DefaultTreeModel) this.partTree.getModel();
		XWTreeNode parent = node.getP();
		if (parent != null) {
			parent.remove(node);
			model.reload(parent);
		}
	}

	public Vector getSelectedPartsElements() {
		if (this.partTree == null)
			return null;
		if (this.partTree.getSelectionCount() <= 0)
			return null;
		TreePath[] paths = this.partTree.getSelectionPaths();
		Vector total = new Vector();
		for (int i = 0; i < paths.length; i++) {
			XWTreeNode node = (XWTreeNode) paths[i].getLastPathComponent();
			XWTreeObject treeObject = node.getObject();
			if ((treeObject instanceof XWPartTreeObject)) {
				Element ele = treeObject.getTreeCellData();
				Element partEle = XmlUtility.createPart();
				XmlUtility.setAttributeValue(partEle, "bsoID", "");
				XmlUtility.setAttributeValue(partEle, "partNumber", ele.attributeValue("partNumber"));
				XmlUtility.setAttributeValue(partEle, "partName", ele.attributeValue("partName"));
				XmlUtility.setAttributeValue(partEle, "material", ele.attributeValue("material"));
				XmlUtility.setAttributeValue(partEle, "dutu", ele.attributeValue("dutu"));
				XmlUtility.setAttributeValue(partEle, "remark", ele.attributeValue("remark"));
				XmlUtility.setAttributeValue(partEle, "useCount", "1");
				total.add(partEle);
			}
		}
		return total;
	}

	public Vector getSelectedPartsPaths() {
		if (this.partTree == null)
			return null;
		if (this.partTree.getSelectionCount() <= 0)
			return null;
		TreePath[] paths = this.partTree.getSelectionPaths();
		Vector total = new Vector();
		for (int i = 0; i < paths.length; i++) {
			XWTreeNode node = (XWTreeNode) paths[i].getLastPathComponent();
			XWTreeObject treeObject = node.getObject();
			if ((treeObject instanceof XWPartTreeObject)) {
				total.add(node);
			}
		}
		return total;
	}

	public void expandAllNode(XWTreeNode node) {
		if (node != null) {
			XWTreeObject xo = node.getObject();
			if (((xo instanceof XWPartTreeObject)) || ((xo instanceof XWProductTreeObject))) {
				if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
					long st = System.currentTimeMillis();
					expandSopProduct(node);
					long end = System.currentTimeMillis();
					logger.debug("expandSopProduct:" + (end-st) + "ms");

				}else{
					expandProduct(node);
				}
			}
		}
	}

	/**
	 * 方法功能: 展开SOP树
	 *
	 * @param node
	 * @return void
	 * @author LB
	 * @date 2019/12/23
	 */
	private void expandSopProduct(XWTreeNode node) {
		if(node != null){
			XWTreeObject xo = node.getObject();
			if (((xo instanceof XWPartTreeObject)) || ((xo instanceof XWProductTreeObject))) {
				node.expand();
				cmNode=node;
				TreePath path1 = new TreePath(node.getPath());
				this.partTree.expandPath(path1);
				if (node.getChildCount()>0) {
					XWTreeNode child = (XWTreeNode) node.getChildAt(0);
					child.expand();
					TreePath path2 = new TreePath(child.getPath());
					this.partTree.expandPath(path2);
				}
			}
			this.partTree.updateUI();
		}
	}

	public void expandAllNode2(XWTreeNode node) {
		if (node != null) {
			XWTreeObject xo = node.getObject();
			if (((xo instanceof XWPartTreeObject)) || ((xo instanceof XWProductTreeObject))) {
				XWTreeObject xo2 = node.getObject();
				System.out.println(xo2.getDisplayName());
				if (((xo2 instanceof XWPartTreeObject)) || ((xo2 instanceof XWProductTreeObject))) {
//					expandAllSubParts(node);
					Vector vec = new Vector();
					Enumeration en = node.depthFirstEnumeration();
					while (en.hasMoreElements()) {
						Object temp = en.nextElement();
						if ((temp instanceof XWTreeNode)) {
							XWTreeNode no = (XWTreeNode) temp;
							if ((!no.isLeaf()) && ((no.getObject() instanceof XWPartTreeObject))) {
								vec.add(no);
							}
						}
					}
					for (int i = 0; i < vec.size(); i++) {
						XWTreeNode no = (XWTreeNode) vec.get(i);
						TreePath p = new TreePath(no.getPath());
						this.partTree.expandPath(p);
					}
					this.partTree.updateUI();
				}
			}
		}
	}

	public void expandTechnics(XWTreeNode node) throws Exception {
		if (node != null) {
			XWTreeObject xo = node.getObject();
			if ((xo instanceof XWTechnicsTreeObject)) {
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
					this.partTree.expandPath(p);
				}
				this.partTree.updateUI();
			}
		}
	}

	public void expandProduct(XWTreeNode node) {
		if (node != null) {
			XWTreeObject xo = node.getObject();
			if (((xo instanceof XWPartTreeObject)) ||((xo instanceof XWProductTreeObject))) {
			   node.expand();
			   cmNode=node;
				expandAllSubParts(node);
				Vector vec = new Vector();
				Enumeration en = node.depthFirstEnumeration();
				while (en.hasMoreElements()) {
					Object temp = en.nextElement();
					if ((temp instanceof XWTreeNode)) {
						XWTreeNode no = (XWTreeNode) temp;
						if ((!no.isLeaf()) && ((no.getObject() instanceof XWPartTreeObject))) {
							vec.add(no);
						}
					}
				}
				 for (int i = 0; i < vec.size(); i++) {
				 XWTreeNode no = (XWTreeNode) vec.get(i);
				 TreePath p = new TreePath(no.getPath());
				 this.partTree.expandPath(p);
				 }
				 this.partTree.updateUI();
			}
		}
	}


	private void expandAllSubParts(XWTreeNode node) {
		if (node != null) {
			XWTreeObject xo = node.getObject();
			if (((xo instanceof XWPartTreeObject)) || ((xo instanceof XWProductTreeObject))) {
				node.expand();
				if (cmNode.getChildCount()>0) {
				    XWTreeNode child0 = (XWTreeNode) cmNode.getChildAt(0);
				    XWTreeObject childObject = child0.getObject();
				    Element treeCellData = childObject.getTreeCellData();
				    String number = treeCellData.attributeValue("partNumber");
				    if (frame.reportTechnics&&!"1".equals(EditorConfig.startType)) {//报表类工艺且 不是大装配加载 则全部加载
				        for (int i = 0; i < node.getChildCount(); i++) {
                            XWTreeNode child = (XWTreeNode) node.getChildAt(i);
                            expandAllSubParts(child);
                        }
                    }else{
                        if (!"1".equals(EditorConfig.startType)) {
                            for (int i = 0; i < node.getChildCount(); i++) {
                              XWTreeNode child = (XWTreeNode) node.getChildAt(i);
                             expandAllSubParts(child);
                          }
                        }else{
                            cishu++;
                            if (cishu<2) {
                                for (int i = 0; i < node.getChildCount(); i++) {
                                    XWTreeNode child = (XWTreeNode) node.getChildAt(i);
                                    expandAllSubParts(child);
                                }
                            }
                        }
                    }
                }
			}
		}
	}

	public void reExpandPBOM() throws Exception {
		if (this.partTree != null) {
			Object obj = this.partTree.getModel().getRoot();
			if ((obj instanceof XWTreeNode)) {
				XWTreeNode root = (XWTreeNode) obj;
				root.removeAllChildren();
				DefaultTreeModel model = (DefaultTreeModel) this.partTree
						.getModel();
				model.reload(root);
				expandProduct(root);
			}
		}
	}

	/**
	 * 复制PBOM零件名称
	 */
	public void copyPartName() {
		XWTreeNode node = this.getSelectedTreeNode();
		if (node != null) {
			XWTreeObject xo = node.getObject();
			this.mainPopupMenu.setMenuState(xo);
			XWTreeObject treeObject = node.getObject();
			if ((treeObject instanceof XWPartTreeObject)) {
				XWPartTreeObject object = (XWPartTreeObject) treeObject;
				Element elment = object.getTreeCellData();
				CopyCache.partName = elment.attributeValue("partNumber") + "_"
						+ elment.attributeValue("partName");
			}
		}

	}


   /**
    * 同步更新工艺文件属性
    */
	public void synchUpdate(){
		XWTreeNode node = this.getSelectedTreeNode();
		synchUpdate2(node);
	}

	public void synchUpdate2(XWTreeNode node) {
		System.out.println("-----synchUpdate2------start-----");
		if (node != null) {
			XWTreeObject xo = node.getObject();
			XWPartTreeObject object = (XWPartTreeObject) xo;
			Element elementPart = object.getTreeCellData();
			String phaseCode = elementPart.attributeValue("PHASE_CODE");
			String partNumber = XmlUtility.getAttributeValue(elementPart,
					"partNumber");
			String version = XmlUtility.getAttributeValue(elementPart, "version");
			String gysl = XmlUtility.getAttributeValue(elementPart, "gysl");
//			version = version.substring(0, version.indexOf("."));
			try {
//				WTPart newpart = TechnicsIntf.getLatestMPartByPartNumber(partNumber);
				WTPart newpart = TechnicsIntf.getMPartByNumberAndVersion(partNumber, version);
				if(newpart!=null){
					Enumeration childList = node.children();
					while (childList.hasMoreElements()) {
						XWTreeNode childNode = (XWTreeNode) childList.nextElement();
						XWTreeObject oxo = childNode.getObject();
						if (oxo instanceof TechnicsMessageTreeObject) {
							TechnicsMessageTreeObject techObject = (TechnicsMessageTreeObject) oxo;
							Document doc = techObject.getTechnicsDocument();
							Element ele = XmlUtility.getTechnicsElement(doc);
							PartIBAHelper ibaUtility = new PartIBAHelper(newpart);
							if(!"SOP".equals(EditorConfig.startType)) {
								for (String attr : attributes) {
									if (ele.attributeValue(attr) != null
											&& ibaUtility.getIBAValue(attr) != null) {
										if (!ibaUtility.getIBAValue(attr).equals(
												ele.attributeValue(attr))) {
											ele.setAttributeValue(attr,
													ibaUtility.getIBAValue(attr));
										}
									}
								}
								if (ele.attributeValue("partName") != null) {
									ele.setAttributeValue("partName", newpart.getName());
								}
								ele.setAttributeValue("CINDEX", ibaUtility.getIBAValue("CINDEX"));
								ele.setAttributeValue("MINDEX", ibaUtility.getIBAValue("MINDEX"));
								ele.setAttributeValue("PCNO", ibaUtility.getIBAValue("BATCH"));
								if(gysl!=null &&!"".equals(gysl)) {
									ele.setAttributeValue("gysl", gysl);
								}
							}else{
								for (String attr : sopAttributes) {
									if (ele.attributeValue(attr) != null
											&& ibaUtility.getIBAValue(attr) != null) {
										if (!ibaUtility.getIBAValue(attr).equals(
												ele.attributeValue(attr))) {
											ele.setAttributeValue(attr,
													ibaUtility.getIBAValue(attr));
										}
									}
								}
								if (ele.attributeValue("partName") != null) {
									ele.setAttributeValue("partName", newpart.getName());
									ele.setAttributeValue("parentPartName", newpart.getName());
									ele.setAttributeValue("pplanName", newpart.getName());
								}
								String oldName = ele.attributeValue("technicsName");
								String newName = newpart.getName()+"("+ele.attributeValue("pplanNumber") +")";
								if(oldName!=null &&!oldName.equals(newName)){
									ele.setAttributeValue("technicsName",newName );
									TechnicsIntf.reName(ele.attributeValue("technicsNumber"),newName);
								}
							}
							this.frame.saveProcess(ele);
						} else if (oxo instanceof ReportTechnicsTreeObject) {
							ReportTechnicsTreeObject techObject = (ReportTechnicsTreeObject) oxo;
							Document doc = techObject.getTechnicsDocument();
							Element ele = XmlUtility.getTechnicsElement(doc);
							PartIBAHelper ibaUtility = new PartIBAHelper(newpart);
							for (String attr : attributes) {
								if (ele.attributeValue(attr) != null
										&& ibaUtility.getIBAValue(attr) != null) {
									if (!ibaUtility.getIBAValue(attr).equals(
											ele.attributeValue(attr))) {
										ele.setAttributeValue(attr,
												ibaUtility.getIBAValue(attr));
									}
								}
							}
							if (ele.attributeValue("partName") != null) {
								ele.setAttributeValue("partName",newpart.getName());
							}
							ele.setAttributeValue("CINDEX",ibaUtility.getIBAValue("CINDEX"));
							ele.setAttributeValue("MINDEX",ibaUtility.getIBAValue("MINDEX"));
							ele.setAttributeValue("PCNO",ibaUtility.getIBAValue("BATCH"));
							this.frame.saveReportProcess(ele);
						} else if (oxo instanceof XWPartTreeObject) {
							synchUpdate2(childNode);
						}
					}
				}


			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("-----synchUpdate2------end-----");
	}
}


