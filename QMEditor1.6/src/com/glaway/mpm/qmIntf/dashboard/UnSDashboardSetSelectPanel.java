package com.glaway.mpm.qmIntf.dashboard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.TreeNode;

import com.glaway.mpm.qmIntf.equipment.TreeDragSource;
import com.glaway.mpm.qmIntf.equipment.TreeDropTarget;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.JavaUtil;
import com.glaway.mpm.view.NewTechnicsPart;

public class UnSDashboardSetSelectPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JScrollPane jScrollPanel;
	private UnSDashboardTree epTree;
	private JPanel panel;

	private JButton upButton;
	private JButton downButton;
	private JButton sureButton;
	private JButton cancelButton;
	private JDialog dialog;

	private TreeDragSource ds;
	private TreeDropTarget dt;
	private NewTechnicsPart frame;

	public UnSDashboardSetSelectPanel(JDialog dialog,NewTechnicsPart frame) {
		this.dialog = dialog;
		this.frame = frame;
		init();
	}

	private void init() {
		initComponents();
		initLayout();
		initActions();
		initDndListeners();
	}

	private void initDndListeners() {
		ds = new TreeDragSource(epTree, DnDConstants.ACTION_MOVE);
		dt = new TreeDropTarget(epTree);
	}

	private void initComponents() {
		jScrollPanel = new JScrollPane();
		UnSDashboardTreeNode rootNode = new UnSDashboardTreeNode("非标准仪器仪表");
		rootNode = UnSDashboardTreeXmlUtil.generateDashboardTreeFromHobby(rootNode);
		epTree = new UnSDashboardTree(rootNode, null, false,frame);
		epTree.setRootVisible(true);
		epTree.setShowsRootHandles(true);
		// epTree.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		// epTree.setDragEnabled(true);

		jScrollPanel.setViewportView(epTree);

		panel = new JPanel();
		upButton = new JButton("上移");
		downButton = new JButton("下移");
		sureButton = new JButton("确定");
		cancelButton = new JButton("取消");

	}

	private void initActions() {
		upButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object node = epTree.getLastSelectedPathComponent();
				if (node != null) {
					UnSDashboardTreeNode epTreeNode = epTree.getRoot();
					int index = epTreeNode.getIndex((TreeNode) node);
					if (index != -1 && index != 0) {
						UnSDashboardTreeNode selectedNode = (UnSDashboardTreeNode) node;
						epTreeNode.insert(selectedNode, index - 1);
						epTree.updateUI();
					}
				}
			}
		});

		downButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Object node = epTree.getLastSelectedPathComponent();
				if (node != null) {
					UnSDashboardTreeNode epTreeNode = epTree.getRoot();
					int index = epTreeNode.getIndex((TreeNode) node);
					if (index != -1 && index != epTreeNode.getChildCount() - 1) {
						UnSDashboardTreeNode selectedNode = (UnSDashboardTreeNode) node;
						epTreeNode.insert(selectedNode, index + 1);
						epTree.updateUI();
					}
				}
			}
		});

		sureButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String epHobby = FileUtil.generateHobbyPath();
				BufferedWriter bw = null;
				try {
					bw = FileUtil.getBufferWriter(epHobby, "gbk");
					UnSDashboardTreeNode rootNode = epTree.getRoot();
					Enumeration children = rootNode.children();
					while (children.hasMoreElements()) {
						UnSDashboardTreeNode node = (UnSDashboardTreeNode) children.nextElement();
						bw.write(node.getName());
						bw.newLine();
					}
					dialog.dispose();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					JavaUtil.closeStream(bw);
				}
			}
		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
	}

	private void initLayout() {
		this.upButton.setPreferredSize(new Dimension(80, 23));
		this.downButton.setPreferredSize(new Dimension(80, 23));
		this.sureButton.setPreferredSize(new Dimension(80, 23));
		this.cancelButton.setPreferredSize(new Dimension(80, 23));
		this.setLayout(new BorderLayout());
		this.add(jScrollPanel, BorderLayout.CENTER);
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new BorderLayout());
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(10, 0, 10, 0);
		c.gridx = 1;
		panel.add(upButton, c);
		c.insets = new Insets(0, 0, 10, 0);
		c.gridy = 2;
		panel.add(downButton, c);
		c.insets = new Insets(0, 0, 10, 0);
		c.gridy = 3;
		panel.add(sureButton, c);
		c.insets = new Insets(0, 0, 10, 0);
		c.gridy = 4;
		panel.add(cancelButton, c);
		panel.setPreferredSize(new Dimension(120, 160));
		jPanel.add(panel, BorderLayout.NORTH);
		this.add(jPanel, BorderLayout.EAST);
	}

}