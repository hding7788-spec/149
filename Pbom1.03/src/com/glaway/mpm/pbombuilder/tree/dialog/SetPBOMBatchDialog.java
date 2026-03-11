package com.glaway.mpm.pbombuilder.tree.dialog;

import com.glaway.mpm.pbombuilder.action.CmActionProgressBar;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * 设置PBOM批次
 *
 * Created on 2013-12-24
 *
 * @author longxiuchuan
 */
public class SetPBOMBatchDialog {
	private CmTree tree;
	private CmTreeNode node;
	private CmLightPart part;
	private JDialog dialog;

	private JPanel mainPanel;
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JLabel label1;

	private JComboBox batch;
	//{"自制件","标准件","元器件","外购件","外配套件","带料委外件","不带料委外件"};
	private Vector<String> batchValues ;

	private JButton sureButton;
	private JButton cancelButton;

	private JButton yesButton;
	private JButton noButton;
	private JDialog suredialog;
	private JLabel surelable;
	private JTextField text;
	private JPanel surePanel;
	private JPanel suretopPanel;
	private JPanel surebottomPanel;

	public SetPBOMBatchDialog(CmTreeNode cmNode, CmTree cmTree) {
		super();
		this.node = cmNode;
		this.tree = cmTree;
	}

	public void showDialog() {
		// 新增对话框
		newJDialog();
		initComponents();
		loadInitDatas();
		initActions1();
		initLayout();
	}

	public void showDialog2() {
		// 新增对话框
		suredialog = new JDialog();
		suredialog.setTitle("更改批次号确认");
		suredialog.setSize(300, 150);
		suredialog.setIconImage(CmUtil.getImageFromServer("edit.gif"));
		suredialog.setResizable(false);
		CmCommonStringUtil.setMiddleOnScreenWithDialog(suredialog);

		surelable=new JLabel("是否更改批次为:");
		surelable.setFont(new Font("宋体",Font.BOLD,13));
		yesButton=new JButton();
		noButton=new JButton();
		yesButton.setText("是");
		noButton.setText("否");
		text=new JTextField();
		text.setText((String)batch.getSelectedItem());
		text.setEnabled(false);
        text.setFont(new Font("宋体",Font.BOLD,13));
        text.setBackground(new Color(255,0,0));


		surePanel = new JPanel();
	    suretopPanel=new JPanel();
		surebottomPanel=new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		suretopPanel.setLayout(new GridBagLayout());

		c.insets = new Insets(26, 5, 5, 15);
		c.gridx = 1;
		c.gridy = 2;
		surelable.setPreferredSize(new Dimension(110, 25));
		suretopPanel.add(surelable, c);

		c.insets = new Insets(25, 110, 5, 15);
		text.setPreferredSize(new Dimension(50, 25));
		suretopPanel.add(text, c);
		surebottomPanel.setLayout(new GridBagLayout());
		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 5;
		c.gridx = 1;
		surebottomPanel.add(yesButton, c);
		c.insets = new Insets(10, 85, 15, 15);
		surebottomPanel.add(noButton, c);

		surePanel.setLayout(new BorderLayout(1, 3));
		surePanel.add(suretopPanel, BorderLayout.NORTH);
		surePanel.add(surebottomPanel, BorderLayout.SOUTH);
		suredialog.add(surePanel);
		Container contentPane = suredialog.getContentPane();
		contentPane.add(surePanel);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				suredialog.setModal(true);
				suredialog.setVisible(true);
			}
		});

		initActions();

	}

	/**
	 * 新增对话框jdialog
	 */
	public void newJDialog() {
		dialog = new JDialog();
		dialog.setTitle("批量设置批次号");
		dialog.setSize(300, 150);
		dialog.setIconImage(CmUtil.getImageFromServer("edit.gif"));
		dialog.setResizable(false);
		CmCommonStringUtil.setMiddleOnScreenWithDialog(dialog);
	}

	private void initComponents() {
		mainPanel = new JPanel();
		topPanel = new JPanel();
		bottomPanel = new JPanel();

		label1 = new JLabel("      选择批次号:");
		CmLightPart rootPart = this.node.getPart();
		batchValues = PBOMEditorToWCIntf.getBatchsByProductOid(rootPart.getContainerId());
		if(batchValues == null) {
			batchValues = new Vector<String>();
			batchValues.add("");
		}

		batch = new JComboBox(batchValues);
		batch.setSelectedItem(rootPart.getBatch());
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
		batch.setPreferredSize(new Dimension(150, 25));
		topPanel.add(batch, c);
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


	private void initActions1(){
		sureButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				showDialog2();
				dialog.setVisible(false);

			}
		});

		cancelButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);

			}
		});

	}




	private void initActions() {
		yesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final CmActionProgressBar progressBar = new CmActionProgressBar(null,suredialog, "设置PBOM批次号", "正在设置批次号,请等待...", "批次号设置中");
				Thread thread = new Thread() {
					public void run() {
						CmTreeNode rootNode = tree.getSelectedNode();
						CmLightPart rootPart = rootNode.getPart();
						long containerId = rootPart.getContainerId();
						long rootOid = rootPart.getOid();
						String rootNumber = rootPart.getPartNumber();

						Map<String,String> ibaMap = new HashMap<String,String>();
						String batchValue = String.valueOf(batch.getSelectedItem());
						ibaMap.put("BATCH", batchValue);

						String state = rootPart.getLifecycle();
						if(!"已批准".equals(state)) {
							String mtype = rootPart.getMtype();
							if("自制件".equals(mtype)||"外配套件".equals(mtype)
				    				||"带料委外件".equals(mtype)||"不带料委外件".equals(mtype)){
								progressBar.setHeaderMessage("零组件编号："+rootNumber+"   批次号："+batchValue);
								//PBOMEditorToWCIntf.savePartIBAValue(rootOid, ibaMap);
								rootPart.setBatch(batchValue);
								rootPart.setEdit(true);
							}
						}

						//循环所有子节点并设置批次号
						loopTreeNode(rootNode,ibaMap,progressBar,containerId);

						//关闭对话框
						suredialog.setVisible(false);

						progressBar.setHeaderMessage("批次号设置完成！");
						progressBar.finish();
						progressBar.setVisible(false);
						tree.updateUI();
					}
				};
				thread.start();
				progressBar.setVisible(true);
			}
		});

		noButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				suredialog.setVisible(false);
			}
		});
	}

	private void loadInitDatas() {
		sureButton.setText("确定");
		cancelButton.setText("取消");
	}

	private void loopTreeNode(CmTreeNode node,Map<String,String> ibaMap,CmActionProgressBar progressBar,long containerId) {
		Enumeration childNodes = node.children();
		while(childNodes.hasMoreElements()){
			CmTreeNode cmTreeNode = (CmTreeNode)childNodes.nextElement();
			CmLightPart part = cmTreeNode.getPart();
			if(containerId != part.getContainerId()) {
				System.out.println("借用件："+part.getPartNumber()+" 未设置批次号："+ibaMap.get("BATCH"));
				progressBar.setHeaderMessage("借用件："+part.getPartNumber()+" 未设置批次号："+ibaMap.get("BATCH"));
				continue;
			}
			String state = part.getLifecycle();
			if(!"已批准".equals(state)) {
				String mtype = part.getMtype();
				if("自制件".equals(mtype)||"外配套件".equals(mtype)
	    				||"带料委外件".equals(mtype)||"不带料委外件".equals(mtype)){
					long oid = part.getOid();
					String partNumber = part.getPartNumber();
					System.out.println("零组件编号："+partNumber+"   批次号："+ibaMap.get("BATCH"));
					progressBar.setHeaderMessage("零组件编号："+partNumber+"   批次号："+ibaMap.get("BATCH"));
					//PBOMEditorToWCIntf.savePartIBAValue(oid, ibaMap);
					part.setBatch(ibaMap.get("BATCH"));
					part.setEdit(true);
				}
			}

			loopTreeNode(cmTreeNode,ibaMap,progressBar,containerId);
		}
	}
}
