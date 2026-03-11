package com.glaway.mpm.qmIntf.tool;

import java.awt.BorderLayout;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class ToolTreePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JScrollPane jScrollPanel;
	private ToolTree toolTree;

	public static void main(String[] args) {
		SwingUtil.setLookAndFeel();
		JFrame frame = new JFrame();
		frame.setSize(500, 600);
		// frame.setLocation(800, 200);
		frame.setLayout(new BorderLayout());
		ToolTreePanel panel = new ToolTreePanel();
		frame.add(panel);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public ToolTreePanel() {
		// init();

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
		toolTree = ToolTreeXmlUtil.generateToolTree(ResourceIntf.getAllTools());
		toolTree.setRootVisible(true);
		jScrollPanel.setViewportView(toolTree);
		add(jScrollPanel);

	}

	public void addObserver(Observer o) {
		toolTree.getMouseAdapter().addObserver(o);
	}

	public void deleteObservers() {
		toolTree.getMouseAdapter().deleteObservers();
	}

	private void initLayout() {

	}

	private void loadInitDatas() {

	}

	private void initActions() {

	}

}
