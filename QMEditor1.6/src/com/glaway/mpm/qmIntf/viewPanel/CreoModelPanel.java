package com.glaway.mpm.qmIntf.viewPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.dom4j.Document;
import org.dom4j.Node;

import com.glaway.mpm.qmIntf.participatePart.VaClipboard;
import com.glaway.mpm.qmIntf.viewPanel.cmp.CmpImageNode;
import com.glaway.mpm.qmIntf.viewPanel.cmp.CmpTree;
import com.glaway.mpm.qmIntf.viewPanel.cmp.CmpTreeXmlUtil;
import com.glaway.mpm.qmIntf.viewPanel.tech.TechImageNode;
import com.glaway.mpm.qmIntf.viewPanel.tech.TechMiddleTreeXmlUtil;
import com.glaway.mpm.qmIntf.viewPanel.tech.TechStepTreeNode;
import com.glaway.mpm.qmIntf.viewPanel.tech.TechTree;
import com.glaway.mpm.qmIntf.viewPanel.tech.TechTreeRootNode;
import com.glaway.mpm.util.CappJavaUtil;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.MiddleModelUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.util.XmlUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.VaTheme;
import com.glaway.mpm.wcIntf.ImageIntf;

public class CreoModelPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static VaLogger logger = VaLogger.getLogger();
		// 用来获取之前已存在的节点
	private Map<String, List<TechImageNode>> childNodes;

	private JSplitPane mainSplitePane = new JSplitPane();
	private JSplitPane leftMainPane;
	private JPanel leftPanel;
	private JScrollPane jScrollPane;
	private CmpTree cmpTree;

	private JButton reOrder;
	private JButton sureButton;
	private JButton btnCopy;
	private JButton quickAdd;
	private JButton btnPaste;
	private JButton btnDelete;

	private JPanel techPanel;
	private TechTree techTree;
	private JScrollPane techScrollPane;

	private String filePath;
	private JPanel imagePanel;
	public static JLabel image;

	protected static Map<String, Vector<List<String>>> stepsInforMap = new HashMap<String, Vector<List<String>>>();
	private List<List<Object>> listData;
	private Map<String, String> map;
	private JDialog dialog;

	private JPopupMenu popupMenu = new JPopupMenu();
	private JMenuItem copyMenu = new JMenuItem("复制");
	private JMenuItem pasteMenu = new JMenuItem("粘贴");
	private KeyInputListener keyInputListener = new KeyInputListener();
	private Map<String, String> numbers;

	public CreoModelPanel(Map<String, String> map, JDialog dialog,
			List<List<Object>> listData) {
		this.dialog = dialog;
		this.map = map;
		this.filePath = map.get("filePath");
		this.listData = listData;
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
		this.leftMainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		leftPanel = new JPanel();
		this.techPanel = new JPanel();

		this.reOrder = new JButton("倒序");
		this.sureButton = new JButton("确定");
		this.btnCopy = new JButton("复制");
		this.quickAdd = new JButton("快速加入");
		this.btnPaste = new JButton("粘贴");
		this.btnDelete = new JButton("移除");

		imagePanel = new JPanel();
		imagePanel.setLayout(new BorderLayout());

		image = new JLabel();
		image.setIcon(new ImageIcon());
		image.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		imagePanel.add(image, BorderLayout.CENTER);

		this.setLayout(new BorderLayout());
		this.add(mainSplitePane, BorderLayout.CENTER);

	}

	private void initLayout() {
		jScrollPane = new JScrollPane();
		jScrollPane.setPreferredSize(new Dimension(300, 250));
		jScrollPane.setMinimumSize(new Dimension(300, 250));
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(buildToolBar(new JButton[] { this.btnCopy, this.quickAdd,
				this.sureButton }), BorderLayout.NORTH);
		leftPanel.add(jScrollPane, BorderLayout.CENTER);
		this.leftMainPane.add(leftPanel, 0);

		// double
		techScrollPane = new JScrollPane();
		techScrollPane.setPreferredSize(new Dimension(300, 350));
		techScrollPane.setMinimumSize(new Dimension(300, 350));

		Document doc = XmlUtil.getDocument(this.filePath);
		// TypeNode root = new TypeNode("工艺树", 0);
		List<Object> list = TechMiddleTreeXmlUtil.xmlToTechImgTree(doc);// new

		this.techTree = (TechTree) list.get(0);// JTree(root);
		childNodes = (Map<String, List<TechImageNode>>) list.get(1);
		numbers = (Map<String, String>) list.get(2);
		this.techTree.setRootVisible(true);

		cmpTree = CmpTreeXmlUtil.xmlToCmpTree(listData, numbers);
		cmpTree.add(popupMenu);
		popupMenu.add(copyMenu);
		popupMenu.add(pasteMenu);

		SwingUtil.expandAll(cmpTree);
		jScrollPane.setViewportView(cmpTree);

		this.techScrollPane.getViewport().add(this.techTree);

		List<Node> nodeList = doc
				.selectNodes("/technics/QMFawTechnicsInfo/steps/QMProcedureInfo/paces");
		for (int i = 0; i < nodeList.size(); i++) {
			nodeList.get(i).getParent().remove(nodeList.get(i));
		}
		SwingUtil.expandAll(techTree);
		techPanel.setPreferredSize(new Dimension(300, 350));
		techPanel.setLayout(new BorderLayout());

		techPanel.add(techScrollPane, BorderLayout.CENTER);
		// 添加button在顶部
		techPanel.add(buildToolBar(new JButton[] { this.reOrder, this.btnPaste,
				this.btnDelete }), BorderLayout.NORTH);

		this.leftMainPane.add(techPanel, 1);

		mainSplitePane.setLeftComponent(leftMainPane);
		mainSplitePane.setRightComponent(imagePanel);
		mainSplitePane.setDividerSize(5);
		reOrder();
		setVisible(true);
	}

	private void initActions() {
		image.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1) {
					String url = ImageIntf
							.getCreoViewUrl(getCurrentImagePartOid());
					if (url != null && !"".equals(url)) {
						CommonUtil.openURL(url);
					}
				}
			}
		});

		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TreePath[] paths = techTree.getSelectionPaths();
				if (paths == null || paths.length == 0) {
					return;
				}

				if (!(paths[0].getLastPathComponent() instanceof TechImageNode)) {
					return;
				}
				TechImageNode node = (TechImageNode) paths[0]
						.getLastPathComponent();
				TechStepTreeNode parent = (TechStepTreeNode) node.getParent();
				parent.remove(node);
				loadInitDatas();
				cmpTree.repaint();
				techTree.updateUI();
				techTree.setSelectionPath(null);
			}
		});

		btnCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doCopy();
			}

		});

		btnPaste.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doPaste();
			}
		});

		sureButton.addActionListener(new ActionListener() {

			public void addImage(TechImageNode imageNode, String stepOid) {
				Vector<List<String>> vector = new Vector<List<String>>();
				Vector<List<String>> olVector = new Vector<List<String>>();
				List<String> list = new ArrayList<String>();
				String oid = imageNode.getImageOid();
				String imageName = imageNode.getName();
				Enumeration<CmpImageNode> cmpNodes = cmpTree.getRoot().children();
				while (cmpNodes.hasMoreElements()) {
					CmpImageNode node = cmpNodes.nextElement();
					String imageName1 = node.getName();
					if (imageName != null && imageName1 != null && imageName.equals(imageName1)) {
						List<String> list1 = new ArrayList<String>();
						if (existBefore(stepOid, imageName1)) {
							list1.add(oid);
							list1.add(imageNode.getDir());
							list1.add(imageNode.getVersion());
							list1.add(imageNode.getModelName());
							olVector.add(list1);
						} else {
							list.add(imageNode.getImageOid());
							list.add(imageNode.getVersion());
							list.add(imageNode.getModelName());
						}
						break;
					}
				}

				// while (cmpNodes.hasMoreElements()) {
				// CmpImageNode node = cmpNodes.nextElement();
				// String oid1 = node.getImageOid();
				// if (oid != null && oid1 != null && oid.equals(oid1)) {
				// if (existBefore(stepOid, oid)) {
				// List<String> list1 = new ArrayList<String>();
				// list1.add(oid);
				// list1.add(imageNode.getDir());
				// list1.add(imageNode.getVersion());
				// olVector.add(list1);
				// } else {
				// hashMap.put(oid1, new ArrayList<String>());
				// }
				// break;
				// }
				// }

				logger.debug("return list======" + list);
				if (list != null && list.size() > 0) {
					vector = ImageIntf.getCAD(list, filePath);
				}
				vector.addAll(olVector);
				Vector<List<String>> vec = stepsInforMap.get(stepOid);
				if (vec != null) {
					vec.addAll(vector);
				} else {
					vec = vector;
				}
				stepsInforMap.put(stepOid, vec);

			}

			@Override
			public void actionPerformed(ActionEvent e) {
				TechTreeRootNode root = techTree.getRoot();
				Enumeration<TechStepTreeNode> stepNodes = root.children();
				CreoModelPanel.stepsInforMap = new HashMap<String, Vector<List<String>>>();
				while (stepNodes.hasMoreElements()) {
					TechStepTreeNode stepNode = stepNodes.nextElement();
					String stepOid = stepNode.getOid();
					stepsInforMap.put(stepOid, new Vector<List<String>>());
					Enumeration nodes = stepNode.children();
					while (nodes.hasMoreElements()) {
						Object obj = nodes.nextElement();
						if (obj instanceof TechImageNode) {
							addImage((TechImageNode) obj, stepOid);
						} else if (obj instanceof TechStepTreeNode) {
							TechStepTreeNode paceNode = (TechStepTreeNode) obj;
							String paceOid = paceNode.getOid();
							stepsInforMap.put(paceOid, new Vector<List<String>>());
							Enumeration<TechImageNode> paceNodes = paceNode.children();
							while (paceNodes.hasMoreElements()) {
								addImage(paceNodes.nextElement(), paceOid);
							}
						}
					}
				}

				dialog.dispose();
			}
		});

		reOrder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					reOrder();
					logger.debug("reOrder menu..");
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		});

		copyMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					doCopy();
					logger.debug("copy menu..");
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		});

		quickAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					quickAdd();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		});

		pasteMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					logger.debug("paste menu.....");
					doPaste();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		cmpTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				int row = cmpTree.getRowForLocation(x, y);

				if (true) {
					TreePath path = cmpTree.getPathForRow(row);
					if (path != null) {
						Object obj = path.getLastPathComponent();
						if (obj instanceof CmpImageNode) {

							if (e.isPopupTrigger()) {
								pasteMenu.setVisible(false);
								copyMenu.setVisible(true);
								popupMenu.show(e.getComponent(), e.getX(),
										e.getY());
							} else {
								super.mouseReleased(e);
							}
							cmpTree.updateUI();
						}
					}
				}

			}
		});

		techTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				int row = techTree.getRowForLocation(x, y);
				Rectangle rect = techTree.getRowBounds(row);
				int height = 0;

				if (rect != null)
					height = rect.height + rect.x;

				TreePath path = techTree.getPathForRow(row);
				if (path != null) {
					if (path.getLastPathComponent() instanceof TechStepTreeNode) {
						techTree.setSelectionPath(path);
						if (e.isPopupTrigger()) {
							copyMenu.setVisible(false);
							pasteMenu.setVisible(true);
							popupMenu.show(e.getComponent(), e.getX(), e.getY());
						} else {
							super.mouseReleased(e);
						}
					}
				}
			}
		});

		leftPanel.registerKeyboardAction(keyInputListener, "copy",
				KeyStroke.getKeyStroke(67, 2, true), 2);
		leftPanel.registerKeyboardAction(keyInputListener, "paste",
				KeyStroke.getKeyStroke(86, 2, true), 2);

	}

	private void reOrder() {
		TechTreeRootNode techRoot = (TechTreeRootNode) techTree.getRoot();
		ArrayList<TechStepTreeNode> stepNodes = Collections.list(techRoot
				.children());
		Collections.reverse(stepNodes);
		techRoot.removeAllChildren();
		for (TechStepTreeNode temp : stepNodes) {
			techRoot.add(temp);
		}
		techTree.updateUI();

	}

	private void doCopy() {
		// 将node 拷贝到缓存中
		TreePath[] paths = cmpTree.getSelectionPaths();
		if (paths != null && paths.length != 0) {
			VaClipboard.clipboardCmpTreeNodes.clear();
			for (TreePath path : paths) {

				Object obj = path.getLastPathComponent();
				if (obj instanceof CmpImageNode) {
					VaClipboard.clipboardCmpTreeNodes.add((CmpImageNode) ((CmpImageNode) obj).clone());
				}
			}
		} else {
			JOptionPane.showMessageDialog(null, "请先选择中间模型。");
		}
	}

	/**
	 * 快速加入
	 */
	private void quickAdd() {
		// 将node 拷贝到缓存中
		VaClipboard.clipboardCmpTreeNodes.clear();
		DefaultTreeModel treeModel = (DefaultTreeModel) techTree.getModel();
		TechTreeRootNode techRoot = (TechTreeRootNode) treeModel.getRoot();
		String partNumber = techRoot.getStepNumber().replace(".", "_");
		Enumeration<CmpImageNode> nodes = cmpTree.getRoot().children();
		while (nodes.hasMoreElements()) {
			CmpImageNode cmpTreeNode = nodes.nextElement();
			Enumeration<Object> stepNodes = techRoot.children();
			add(cmpTreeNode, stepNodes, treeModel, partNumber);
		}
		SwingUtil.expandAll(techTree);
	}

	private void add(CmpImageNode cmpTreeNode, Enumeration<Object> stepNodes,
			DefaultTreeModel treeModel, String partNumber) {
		String modelName = cmpTreeNode.getName();
		while (stepNodes.hasMoreElements()) {
			Object obj = stepNodes.nextElement();
			if (obj instanceof TechStepTreeNode) {
				TechStepTreeNode stepNode = (TechStepTreeNode) obj;
				String stepOid = stepNode.getOid();
				int length = stepOid.length();
				if (length > MiddleModelUtil.ID_COUNT) {
					String stepModelName = partNumber
							+ "M"
							+ CappJavaUtil.toHexString(stepOid.substring(length
									- MiddleModelUtil.ID_COUNT));
					if (modelName.equalsIgnoreCase(stepModelName)) {
						Enumeration children = stepNode.children();
						while (children.hasMoreElements()) {
							Object child = children.nextElement();
							if (child instanceof TechImageNode) {
								TechImageNode image = (TechImageNode) child;
								if (modelName.equals(image.getName())) {
									return;
								}
							}
						}

						treeModel.insertNodeInto(
								(TechImageNode) cmpTreeNode.clone(), stepNode,
								0);
						break;
					}
				}
				add(cmpTreeNode, stepNode.children(), treeModel, partNumber);
			}
		}
	}

	private void doPaste() {
		TreePath[] paths = techTree.getSelectionPaths();
		if (paths == null || paths.length == 0) {
			JOptionPane.showMessageDialog(null, "请先选择粘贴的工序或工步。");
			return;
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0]
				.getLastPathComponent();
		if (node instanceof TechStepTreeNode) {

			if (VaClipboard.clipboardCmpTreeNodes.size() == 0) {
				JOptionPane.showMessageDialog(null, "请先复制中间模型。");
				return;
			}

			boolean flag = true;
			for (CmpImageNode cmpTreeNode : VaClipboard.clipboardCmpTreeNodes) {

				Enumeration existNodes = node.children();
				while (existNodes.hasMoreElements()) {
					Object obj = existNodes.nextElement();
					if (obj instanceof TechImageNode) {
						TechImageNode tempNode = (TechImageNode) obj;
						String tempName = tempNode.getName();
						if (tempName.equals(cmpTreeNode.getName())) {
							JOptionPane.showMessageDialog(null, "中间模型"
									+ tempName + "在工序下已经存在。");
							return;
						}
					}
				}

				DefaultTreeModel treeModel = (DefaultTreeModel) (techTree
						.getModel());
				treeModel.insertNodeInto((MutableTreeNode) cmpTreeNode.clone(),
						node, 0);
				techTree.expandPath(paths[0]);
				// TechTreeRootNode oCmpTreeNodeRoot = cmpTree.getRoot();
				// Enumeration allNodes = oCmpTreeNodeRoot.children();
				// while (allNodes.hasMoreElements()) {
				// CmpStepTreeNode aNode = (CmpStepTreeNode) allNodes
				// .nextElement();
				// if (aNode.getImage().getName()
				// .equals(cmpTreeNode.getImage().getName())) {
				// // aNode.setUsed(true);
				// flag &= aNode.isOriginal();
				// // break;
				// }
				//
				// logger.debug("aNode is used  ==  " + aNode.isUsed());
				// }
			}

			logger.debug("========== end ");
			cmpTree.updateUI();
			if (!flag) {
				// VaClipboard.clipboardCmpTreeNodes.clear();
			}
		} else {
			JOptionPane.showMessageDialog(null, "中间模型只能添加在工序或者工步下!");
		}

		techTree.updateUI();

	}

	private void markUsedMiddleModel() {
		TechTreeRootNode cmpRoot = cmpTree.getRoot();
		Enumeration<CmpImageNode> cmpNodes = cmpRoot.children();
		while (cmpNodes.hasMoreElements()) {
			CmpImageNode cmpNodeM = cmpNodes.nextElement();
			// TechTreeRootNode techRoot = techTree.getRoot();
			// Enumeration<IMCmpTreeNode> stepNodes = techRoot.children();
			cmpNodeM.setUsed(false);
			// while (stepNodes.hasMoreElements()) {
			// IMCmpTreeNode stepNode = stepNodes.nextElement();
			// Enumeration<CmpStepTreeNode> techNodes = stepNode.children();
			// while (techNodes.hasMoreElements()) {
			// CmpStepTreeNode cmpNodeT = techNodes.nextElement();
			// logger.debug("middle === " + cmpNodeM + "  tech ====="
			// + cmpNodeT);
			// if (cmpNodeM.getImage().getName()
			// .equals(cmpNodeT.getImage().getName())) {
			// // cmpNodeM.setUsed(true);
			// }
			// }
			// }
		}
	}

	private boolean existBefore(String stepOid, String imageName1) {
		boolean flag = false;
		if (childNodes != null) {
			List<TechImageNode> cmpTreeNodes = childNodes.get(stepOid);
			for (TechImageNode temp : cmpTreeNodes) {
				if (temp.getName().equals(imageName1)) {
					flag = true;
				}
			}
		}
		return flag;
	}

	//
	class KeyInputListener implements ActionListener {
		KeyInputListener() {
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().compareTo("copy") == 0) {
				try {
					doCopy();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else if (e.getActionCommand().compareTo("paste") == 0) {
				doPaste();
			}
		}
	}

	private String getCurrentImagePartOid() {
		String oid = null;
		TreePath[] paths = cmpTree.getSelectionPaths();
		if (paths != null && paths.length > 0) {
			CmpImageNode node = (CmpImageNode) paths[0].getLastPathComponent();
			oid = node.getImageOid();
		}
		return oid;

	}

	private JToolBar buildToolBar(JButton[] buttons) {
		JToolBar toolBar = new JToolBar();
		toolBar.setBackground(VaTheme.VA_TURQUOISE);
		toolBar.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0,
				Color.WHITE));
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		for (int i = 0; i < buttons.length; i++) {
			JButton button = buttons[i];
			button.setForeground(Color.WHITE);
			toolBar.add(button);
		}
		return toolBar;
	}

	private void loadInitDatas() {
		markUsedMiddleModel();
	}
}