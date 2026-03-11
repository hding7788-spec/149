package com.glaway.mpm.qmIntf.resourceTree;

import java.awt.BorderLayout;
import java.util.Enumeration;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import wt.method.RemoteMethodServer;

import com.glaway.mpm.model.FkType;
import com.glaway.mpm.model.Frock;
import com.glaway.mpm.qmIntf.resourceTree.model.FkNode;
import com.glaway.mpm.qmIntf.resourceTree.model.FkTreeNode;
import com.glaway.mpm.resource.ResourceCache;
import com.glaway.mpm.util.CommonObserver;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class ResourceTreePanel extends JPanel {
	private JScrollPane jScrollPanel;

	private static VaLogger logger = VaLogger.getLogger(ResourceTreePanel.class);
	private static final long serialVersionUID = 1L;
	public ResourceTree resourceTree;
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
		 ResourceTreePanel panel = new ResourceTreePanel(null);
		 panel.addObserver(new CommonObserver());
		 frame.add(panel);
		 frame.setVisible(true);
		 frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public ResourceTreePanel(NewTechnicsPart frame) {
		NewTechnicsPart.startAnimFrame.setHeaderMessage("初始化工装资源树");
		this.frame = frame;
		rsInfoPanel = new RsInfoPanel(frame);
		init();
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成初始化工装资源树");
	}

	public void refreshFrock() {
		if (resourceTree != null) {
			ResourceTreeNode root = resourceTree.getRoot();
			Enumeration nodes = root.children();
			if (nodes.hasMoreElements()) {
				FkTreeNode node = (FkTreeNode) nodes.nextElement();
				if ("工装".equals(node.getName())) {
					node.removeFromParent();
					FkTreeNode fkTreeNode = new FkTreeNode("工装");
					root.add(fkTreeNode);
					ResourceCache.fkType = null;
					FkType fkType = ResourceIntf.getAllFrocks();
					if (fkType != null) {
						RsTreeUtil.getData(FkType.class, fkType, fkTreeNode);
					}
					root.insert(fkTreeNode, 0);
					resourceTree.updateUI();

				}
			}
		}
	}

	private void init() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		jScrollPanel = new JScrollPane();
		resourceTree = RsTreeUtil.generateResourceTreeRoot(rsInfoPanel, frame);
		resourceTree.setRootVisible(true);
		jScrollPanel.setViewportView(resourceTree);
		add(jScrollPanel);
		add(rsInfoPanel, BorderLayout.SOUTH);
	}

	public boolean addFrockNode(Frock frock) {
		try {
			if (resourceTree != null) {
				ResourceTreeNode root = resourceTree.getRoot();
				return addNode(root, frock);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean addNode(DefaultMutableTreeNode node, Frock frock) {
		Enumeration nodes = node.children();
		while (nodes.hasMoreElements()) {
			Object obj = nodes.nextElement();
			// if (obj instanceof FkTreeNode) {
			FkTreeNode parentNode = (FkTreeNode) obj;
			// Enumeration childNodes = fkTreeNode.children();
			// if (childNodes.hasMoreElements()
			// && childNodes instanceof FkTreeNode) {
			// return addNode(
			// (DefaultMutableTreeNode) childNodes.nextElement(),
			// frock);
			// } else {
			// FkNode fkNode = (FkNode) obj;
			// DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)
			// fkNode
			// .getParent();
			// parentNode.add(new FkNode(frock));
			// return true;
			// }
			// } else {
			// FkNode fkNode = (FkNode) obj;
			// DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)
			// fkNode
			// .getParent();
			parentNode.add(new FkNode(frock));
			resourceTree.updateUI();
			return true;
			// }
		}
		return false;
	}

	public void addObserver(Observer o) {
		resourceTree.getMouseAdapter().addObserver(o);
	}

	public void deleteObservers() {
		resourceTree.getMouseAdapter().deleteObservers();
	}

}
