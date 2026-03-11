package com.glaway.mpm.pbombuilder.tree.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.glaway.mpm.pbombuilder.action.CmActionProgressBar;
import com.glaway.mpm.pbombuilder.data.CmNode;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

/**
 * 设置PBOM批次
 *
 * Created on 2013-12-24
 *
 * @author longxiuchuan
 */
public class ReNameDialog {
	private CmTree tree;
	private CmTreeNode node;
	private CmLightPart part;
	private JDialog dialog;

	private JPanel mainPanel;
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JLabel label1;

	private JTextField name;

	private JButton sureButton;
	private JButton cancelButton;


	public ReNameDialog(CmTreeNode cmNode, CmTree cmTree) {
		super();
		this.node = cmNode;
		this.tree = cmTree;
	}

	public void showDialog() {
		// 新增对话框
		newJDialog();
		initComponents();
		loadInitDatas();
		initLayout();
		initActions();
	}



	/**
	 * 新增对话框jdialog
	 */
	public void newJDialog() {
		dialog = new JDialog();
		dialog.setTitle("重命名");
		dialog.setSize(300, 150);
		dialog.setIconImage(CmUtil.getImageFromServer("edit.gif"));
		dialog.setResizable(false);
		CmCommonStringUtil.setMiddleOnScreenWithDialog(dialog);
	}

	private void initComponents() {
		mainPanel = new JPanel();
		topPanel = new JPanel();
		bottomPanel = new JPanel();

		label1 = new JLabel("      名称:");
		CmLightPart part = this.node.getPart();
		name = new JTextField(part.getPartName());
		sureButton = new JButton();
		cancelButton = new JButton();

	}

	private void initLayout() {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		topPanel.setLayout(new GridBagLayout());

		//批次号
		c.insets = new Insets(26, 5, 5, 15);
		c.gridx = 1;
		c.gridy = 2;
		label1.setPreferredSize(new Dimension(110, 25));
		topPanel.add(label1, c);

//		batch.setSelectedItem("");
		c.insets = new Insets(25, 105, 5, 15);
		name.setPreferredSize(new Dimension(150, 25));
		topPanel.add(name, c);
		bottomPanel.setLayout(new GridBagLayout());
		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 5;
		c.gridx = 1;
		bottomPanel.add(sureButton, c);
		c.insets = new Insets(10, 85, 15, 15);
		bottomPanel.add(cancelButton, c);

		mainPanel.setLayout(new BorderLayout(1, 3));
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		dialog.add(mainPanel);
		Container contentPane = dialog.getContentPane();
		contentPane.add(mainPanel);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				dialog.setModal(true);
				dialog.setVisible(true);
			}
		});
	}







	private void initActions() {
		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final CmActionProgressBar progressBar = new CmActionProgressBar(null,dialog, "重命名", "正在重命名,请等待...", "重命名设置中");
				Thread thread = new Thread() {
					public void run() {
						CmTreeNode node = tree.getSelectedNode();
						CmLightPart part = node.getPart();
						String sname = name.getText();
						if(sname==null||"".equals(sname)){
							sname = part.getPartName();
						}else{
							part.setPartName(sname);
							node.setUserObject(new CmNode(part));
							//System.out.println(node.toString());
							boolean isSuccess = PBOMEditorToWCIntf.reName(part.getPartNumber(),sname);
						}
						progressBar.setHeaderMessage("重命名完成！");
						progressBar.finish();
						progressBar.setVisible(false);
						dialog.setVisible(false);
						tree.updateUI();
					}
				};
				thread.start();
				progressBar.setVisible(true);
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
	}

	private void loadInitDatas() {
		sureButton.setText("确定");
		cancelButton.setText("取消");
	}
}
