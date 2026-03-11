package com.glaway.mpm.qmIntf.pdName;

import java.awt.BorderLayout;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.glaway.mpm.qmIntf.resourceTree.PdNameInfoPanel;
import com.glaway.mpm.util.CommonObserver;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;

public class PdNameTreePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private PdNameInfoPanel pdNameInfoPanel = new PdNameInfoPanel();
	private JScrollPane jScrollPanel;
	private PdNameTree tree;
	private NewTechnicsPart frame;

	public static void main(String[] args) {
		SwingUtil.setLookAndFeel();
		JFrame frame = new JFrame();
		frame.setSize(500, 600);
		frame.setLocation(800, 200);
		frame.setLayout(new BorderLayout());
		PdNameTreePanel panel = new PdNameTreePanel(null);
		panel.addObserver(new CommonObserver());
		frame.add(panel);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public PdNameTreePanel(NewTechnicsPart frame) {
		NewTechnicsPart.startAnimFrame.setHeaderMessage("初始化工序名称树");
		this.frame = frame;
		init();
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成初始化工序名称树");
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
//		tree = PdNameTreeXmlUtil.generatePdNameTree();
		tree = PdNameTreeXmlUtil.generateSkillTreeRoot(pdNameInfoPanel,frame);
		//tree = PdNameTreeXmlUtil.generateSkillTree(pdNameInfoPanel);
		tree.setRootVisible(true);
		jScrollPanel.setViewportView(tree);
		add(jScrollPanel);
		add(pdNameInfoPanel, BorderLayout.SOUTH);
	}

	public void addObserver(Observer o) {
		tree.getMouseAdapter().addObserver(o);
	}

	public void deleteObservers() {
		tree.getMouseAdapter().deleteObservers();
	}

	private void initLayout() {

	}

	private void loadInitDatas() {

	}

	private void initActions() {

	}

}
