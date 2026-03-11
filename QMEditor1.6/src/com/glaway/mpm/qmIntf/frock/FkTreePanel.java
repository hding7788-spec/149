package com.glaway.mpm.qmIntf.frock;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import com.glaway.mpm.qmIntf.common.model.CommonComboBox;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class FkTreePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JScrollPane jScrollPanel;
	private FkTree fkTree;

	private JLabel workShopLabel = new JLabel("制造单位");
	private JComboBox workShop = new CommonComboBox();

	public static void main(String[] args) {
		SwingUtil.setLookAndFeel();
		JFrame frame = new JFrame();
		frame.setSize(500, 600);
		frame.setLocation(800, 200);
		frame.setLayout(new BorderLayout());
		FkTreePanel panel = new FkTreePanel();
		frame.add(panel);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public FkTreePanel() {
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
		fkTree = FkTreeXmlUtil.generateFkTree(ResourceIntf.getAllFrocks());
		fkTree.setRootVisible(true);
		jScrollPanel.setViewportView(fkTree);
		jScrollPanel.setPreferredSize(new Dimension(400, 600));
		add(jScrollPanel);

	}

	private JToolBar buildToolBar() {
		JToolBar toolBar = new JToolBar();
		// toolBar.setBackground(VaTheme.VA_TURQUOISE);
		toolBar.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0,
				Color.WHITE));
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		// button.setBackground(VaTheme.VA_TURQUOISE);
		// button.setForeground(Color.WHITE);
		toolBar.add(workShopLabel);
		toolBar.add(workShop);
		// button.setIcon(icon);
		return toolBar;
	}

	public void addObserver(Observer o) {
		fkTree.getMouseAdapter().addObserver(o);
	}

	public void deleteObservers() {
		fkTree.getMouseAdapter().deleteObservers();
	}

	private void initLayout() {

	}

	private void loadInitDatas() {

	}

	private void initActions() {
		// workShop.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) fkTree
		// .getModel().getRoot();
		// DefaultMutableTreeNode frockRootNode = (DefaultMutableTreeNode)
		// rootNode
		// .children().nextElement();
		// String workShopValue = String.valueOf(workShop
		// .getSelectedItem());
		// filterByWorkShop(rootNode, frockRootNode, workShopValue);
		// }
		// });
	}

	// private void filterByWorkShop(JTree tree, DefaultMutableTreeNode root,
	// DefaultMutableTreeNode frockRootNode, String workShopValue) {
	// tree.getModel()
	// // Enumeration children = frockRootNode.children();
	// // while (children.hasMoreElements()) {
	// // DefaultMutableTreeNode child = (DefaultMutableTreeNode) children
	// // .nextElement();
	// // child.
	// // }
	// Object[] path = { root };
	// int[] childIndices = new int[root.getChildCount()];
	// Object[] children = new Object[root.getChildCount()];
	// for (int i = 0; i < root.getChildCount(); i++) {
	// childIndices[i] = i;
	// children[i] = root.getChildAt(i);
	// }
	// fireTreeStructureChanged(tree.getModel(), path, childIndices, children);
	// }
}
