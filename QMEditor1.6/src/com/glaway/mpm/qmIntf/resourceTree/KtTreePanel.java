package com.glaway.mpm.qmIntf.resourceTree;

import java.awt.BorderLayout;
import java.util.Enumeration;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import wt.method.RemoteMethodServer;

import com.glaway.mpm.model.KnifeTool;
import com.glaway.mpm.model.KtType;
import com.glaway.mpm.qmIntf.resourceTree.model.KtNode;
import com.glaway.mpm.qmIntf.resourceTree.model.KtTreeNode;
import com.glaway.mpm.resource.ResourceCache;
import com.glaway.mpm.util.CommonObserver;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class KtTreePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(KtTreePanel.class);
	private JScrollPane jScrollPanel;
	public KtTree ktTree;
	private RsInfoPanel rsInfoPanel;
	private NewTechnicsPart frame;

	public static void main(String[] args) {
		RemoteMethodServer.getDefault().setUserName("wcadmin");
		RemoteMethodServer.getDefault().setPassword("wcadmin");
		SwingUtil.setLookAndFeel();
		JFrame frame = new JFrame();
		frame.setSize(500, 600);
		frame.setLocation(800, 200);
		frame.setLayout(new BorderLayout());
		KtTreePanel panel = new KtTreePanel(null);
		panel.addObserver(new CommonObserver());
		frame.add(panel);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		SwingUtil.setMiddle(frame);
	}

	public KtTreePanel(NewTechnicsPart frame) {
		NewTechnicsPart.startAnimFrame.setHeaderMessage("初始化刀具资源树");
		this.frame = frame;
		rsInfoPanel = new RsInfoPanel(frame);
		init();
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成初始化刀具资源树");
	}

	private void init() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		jScrollPanel = new JScrollPane();
		ktTree = RsTreeUtil.generateKtTreeRoot(rsInfoPanel, frame);
		ktTree.setRootVisible(true);
		jScrollPanel.setViewportView(ktTree);
		add(jScrollPanel);
		add(rsInfoPanel, BorderLayout.SOUTH);
	}

	public void refreshKnife() {
		if (ktTree != null) {
			KtTreeNode root = ktTree.getRoot();
			Enumeration nodes = root.children();
			if (nodes.hasMoreElements()) {
				KtTreeNode node = (KtTreeNode) nodes.nextElement();
				if ("刀具".equals(node.getName())) {
					node.removeFromParent();
					KtTreeNode ktTreeNode = new KtTreeNode("刀具");
					root.add(ktTreeNode);
					ResourceCache.ktType = null;
					KtType ktType = ResourceIntf.getAllKnifeTools();
					if (ktType != null) {
						RsTreeUtil.getData(KtType.class, ktType, ktTreeNode);
					}
					root.insert(ktTreeNode, 0);
					ktTree.updateUI();
				}
			}
		}
	}

	public boolean addFrockNode(KnifeTool knife) {
		try {
			if (ktTree != null) {
				KtTreeNode root = ktTree.getRoot();
				return addNode(root, knife);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean addNode(DefaultMutableTreeNode node, KnifeTool knife) {
		Enumeration nodes = node.children();
		while (nodes.hasMoreElements()) {
			Object obj = nodes.nextElement();
			KtTreeNode parentNode = (KtTreeNode) obj;
			parentNode.add(new KtNode(knife));
			ktTree.updateUI();
			return true;
		}
		return false;
	}

	public void addObserver(Observer o) {
		ktTree.getMouseAdapter().addObserver(o);
	}

	public void deleteObservers() {
		ktTree.getMouseAdapter().deleteObservers();
	}
}
