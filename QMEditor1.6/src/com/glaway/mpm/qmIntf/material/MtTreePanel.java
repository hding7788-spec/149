package com.glaway.mpm.qmIntf.material;

import java.awt.BorderLayout;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import wt.method.RemoteMethodServer;

import com.glaway.mpm.util.CommonObserver;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class MtTreePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(MtTreePanel.class);
	private JScrollPane jScrollPanel;
	private MtTree mtTree;
	private MtInfoPanel mtInfoPanel = new MtInfoPanel();
	private NewTechnicsPart frame;

	public static void main(String[] args) {
		RemoteMethodServer.getDefault().setUserName("wcadmin");
		RemoteMethodServer.getDefault().setPassword("wcadmin");
		SwingUtil.setLookAndFeel();
		JFrame frame = new JFrame();
		frame.setSize(500, 600);
		frame.setLocation(800, 200);
		frame.setLayout(new BorderLayout());
		MtTreePanel panel = new MtTreePanel(null);
		panel.addObserver(new CommonObserver());
		frame.add(panel);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public MtTreePanel(NewTechnicsPart frame) {
		NewTechnicsPart.startAnimFrame.setHeaderMessage("初始化工艺辅料树");
		this.frame = frame;
		init();
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成初始化工艺辅料树");
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
		setLayout(new BorderLayout());
		jScrollPanel = new JScrollPane();
		mtTree = MtTreeXmlUtil.generateMtTreeRoot(null, mtInfoPanel,frame);
		//mtTree = MtTreeXmlUtil.generateMtTree(ResourceIntf.getMaterialTypes(), mtInfoPanel);
		//SwingUtil.expandBeforeLeaf(mtTree, mtTree.getRoot(), MtNode.class);
		mtTree.setRootVisible(true);
		jScrollPanel.setViewportView(mtTree);
		add(jScrollPanel);
		add(mtInfoPanel, BorderLayout.SOUTH);
	}

	public void addObserver(Observer o) {
		mtTree.getMouseAdapter().addObserver(o);
	}

	public void deleteObservers() {
		mtTree.getMouseAdapter().deleteObservers();
	}

	private void initLayout() {

	}

	private void loadInitDatas() {

	}

	private void initActions() {

	}

}
