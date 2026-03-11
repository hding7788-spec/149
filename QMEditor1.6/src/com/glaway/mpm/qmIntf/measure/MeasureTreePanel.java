package com.glaway.mpm.qmIntf.measure;

import java.awt.BorderLayout;
import java.util.Enumeration;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import wt.method.RemoteMethodServer;

import com.glaway.mpm.model.Frock;
import com.glaway.mpm.qmIntf.resourceTree.RsTreeUtil;
import com.glaway.mpm.qmIntf.resourceTree.model.FkNode;
import com.glaway.mpm.qmIntf.resourceTree.model.FkTreeNode;
import com.glaway.mpm.util.CommonObserver;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.log.VaLogger;

public class MeasureTreePanel extends JPanel {
	private JScrollPane jScrollPanel;

	private static VaLogger logger = VaLogger.getLogger(MeasureTreePanel.class);
	private static final long serialVersionUID = 1L;
	public MeasureTree measureTree;
	private MeasureInfoPanel measureInfoPanel;
	private NewTechnicsPart frame;

	public static void main(String[] args) {
		 RemoteMethodServer.getDefault().setUserName("wcadmin");
		 RemoteMethodServer.getDefault().setPassword("wcadmin");
		 SwingUtil.setLookAndFeel();
		 JFrame frame = new JFrame();
		 frame.setSize(500, 600);
		 frame.setLocation(800, 200);
		 frame.setLayout(new BorderLayout());
		 MeasureTreePanel panel = new MeasureTreePanel(null);
		 panel.addObserver(new CommonObserver());
		 frame.add(panel);
		 frame.setVisible(true);
		 frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public MeasureTreePanel(NewTechnicsPart frame) {
		NewTechnicsPart.startAnimFrame.setHeaderMessage("初始化量具资源树");
		this.frame = frame;
		measureInfoPanel = new MeasureInfoPanel(frame);
		init();
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成初始化量具资源树");
	}

	private void init() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		jScrollPanel = new JScrollPane();
		measureTree = RsTreeUtil.generateMeasureTreeRoot(measureInfoPanel, frame);
		measureTree.setRootVisible(true);
		jScrollPanel.setViewportView(measureTree);
		add(jScrollPanel);
		add(measureInfoPanel, BorderLayout.SOUTH);
	}

	public boolean addFrockNode(Frock frock) {
		try {
			if (measureTree != null) {
				MeasureTreeNode root = measureTree.getRoot();
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
			FkTreeNode parentNode = (FkTreeNode) obj;
			parentNode.add(new FkNode(frock));
			measureTree.updateUI();
			return true;
		}
		return false;
	}

	public void addObserver(Observer o) {
		measureTree.getMouseAdapter().addObserver(o);
	}

	public void deleteObservers() {
		measureTree.getMouseAdapter().deleteObservers();
	}

}
