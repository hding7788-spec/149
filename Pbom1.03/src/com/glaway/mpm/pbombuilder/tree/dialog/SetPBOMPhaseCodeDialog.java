package com.glaway.mpm.pbombuilder.tree.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.PbomTreeUpdateAction;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;
import com.glaway.mpm.pbombuilder.util.Constants;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

/**
 * 设置PBOM阶段标记
 *
 * Created on 2014-2-17
 *
 * @author LongXiuChuan
 */
public class SetPBOMPhaseCodeDialog {
	private CmTree tree;
	private CmTreeNode node;
	private CmLightPart part;
	private JDialog dialog;

	private JPanel mainPanel;
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JLabel label1;

	private JComboBox phaseCode;
	//{"自制件","标准件","元器件","外购件","外配套件","带料委外件","不带料委外件"};
	private Vector<String> phaseCodeValues ;

	private JButton sureButton;
	private JButton cancelButton;

	public SetPBOMPhaseCodeDialog(CmTreeNode cmNode, CmTree cmTree) {
		super();
		this.node = cmNode;
		this.tree = cmTree;
	}

	public void showDialog() {
		// 新增对话框
		newJDialog();
		initComponents();
		loadInitDatas();
		initActions();
		initLayout();
	}

	/**
	 * 新增对话框jdialog
	 */
	public void newJDialog() {
		dialog = new JDialog();
		dialog.setTitle("PBOM转阶段");
		dialog.setSize(300, 150);
		dialog.setIconImage(CmUtil.getImageFromServer("edit.gif"));
		dialog.setResizable(false);
		CmCommonStringUtil.setMiddleOnScreenWithDialog(dialog);
	}

	private void initComponents() {
		mainPanel = new JPanel();
		topPanel = new JPanel();
		bottomPanel = new JPanel();
		label1 = new JLabel("    选择阶段标记:");
		phaseCode = new JComboBox(Constants.phasecodeValues);
		sureButton = new JButton();
		cancelButton = new JButton();

	}

	private void initLayout() {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		topPanel.setLayout(new GridBagLayout());

		//阶段标记
		c.insets = new Insets(26, 5, 5, 15);
		c.gridx = 1;
		c.gridy = 2;
		label1.setPreferredSize(new Dimension(110, 25));
		topPanel.add(label1, c);

		c.insets = new Insets(25, 105, 5, 15);
		phaseCode.setPreferredSize(new Dimension(150, 25));
		topPanel.add(phaseCode, c);

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
				dialog.setVisible(false);
				CmTreeNode rootNode = tree.getRoot();
				/*StringBuffer msg = new StringBuffer();
				msg = checkPbom(rootNode,msg);
				if(!"".equals(msg.toString())) {
					JOptionPane.showMessageDialog(dialog, "以下零部件的工艺文件未批准，不允许转阶段：\r\n"+msg.toString());
				} else {
					updateVersion(rootNode);
					tree.updateUI();
				}*/
				updateVersion(rootNode);
				tree.updateUI();
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

	private StringBuffer checkPbom(CmTreeNode root,StringBuffer sb) {
		Enumeration children = root.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			CmLightPart part = child.getPart();
			long partOid = part.getOid();
			boolean b = PBOMEditorToWCIntf.checkPartTechnicsIsReleased(partOid);
			if(!b) {
				if("".equals(sb.toString())) {
					sb.append(part.getPartNumber());
				} else {
					sb.append(",").append(part.getPartNumber());
				}
			}
			checkPbom(child,sb);
		}
		return sb;
	}

	@SuppressWarnings("unchecked")
	public void updateVersion(CmTreeNode root){
		Enumeration children = root.children();
		String mtype = "";
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			mtype = child.getPart().getMtype();
			if("自制件".equals(mtype)||"外配套件".equals(mtype)
					||"带料委外件".equals(mtype)||"不带料委外件".equals(mtype)||"配套产品".equals(mtype)) {
				PbomTreeUpdateAction.updateVersion2(child,phaseCode.getSelectedItem().toString());
			}

			if(CmCommonStringUtil.isPackage(child)){
				for(CmTreeNode brother:child.getListNode()){
					mtype = brother.getPart().getMtype();
					if("自制件".equals(mtype)||"外配套件".equals(mtype)
							||"带料委外件".equals(mtype)||"不带料委外件".equals(mtype)||"配套产品".equals(mtype)) {
						PbomTreeUpdateAction.updateVersion2(brother,phaseCode.getSelectedItem().toString());
					}

					updateVersion(brother);
				}
			}

			updateVersion(child);
		}
	}
}
