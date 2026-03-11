package com.glaway.mpm.qmIntf.template;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TemplateIntf;

public class StepTemplateSearchPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static VaLogger logger = VaLogger.getLogger(StepTemplateSearchPanel.class);

	private JButton sureButton;
	private JButton cancelButton;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private JPanel rightUpPanel;
	private JPanel mainPanel;
	private JScrollPane jScrollPane;
	public StepTree stTree;
	private String filePath;
	public static Vector<Object> vector;
	private String type;
	private JDialog dialog;
	
	/**
	 * 搜索面板
	 */
	private JPanel topPanel;
	/**
	 * 搜索框名称
	 */
	private JLabel searchNameLabel= new JLabel("名称：");
	/**
	 * 搜索框
	 */
	private JTextField searchTextField=new JTextField();
	
	/**
	 * 搜索按钮
	 */
	private JButton searchButton=new JButton("搜索");
	private JButton clearButton=new JButton("清除");

	public StepTemplateSearchPanel(String filePath, String type, JDialog dialog) {
		this.dialog = dialog;
		this.type = type;
		vector = null;
		this.filePath = FileUtil.addSeperator(filePath);
		init();
	}

	private void init() {
		initLookAndFeel();
		initDimension();
		initComponents();
		initSearchLayout();
		initLayout();
		initActions();
		loadInitDatas();
	}
	
	/** 
	  * @Description: 设置顶部搜索布局
	  * @date 2025年10月27日上午9:44:31
	  * @author Liluwen  
	  * @return 
	*/
	private void initSearchLayout() {
		searchTextField.setPreferredSize(new Dimension(210, 25));
		searchButton.setPreferredSize(new Dimension(60, 25));
		clearButton.setPreferredSize(new Dimension(60, 25));
		
		topPanel=new JPanel(new GridBagLayout());
		topPanel.setMaximumSize(topPanel.getPreferredSize());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST; 
		c.weightx = 0;
		c.insets = new Insets(2, 2, 2, 2);
		c.gridx = 0;
		c.gridy = 0;
		topPanel.add(searchNameLabel,c);
		c.gridx = 1;
		topPanel.add(searchTextField,c);
		c.gridx = 2;
		topPanel.add(searchButton,c);
		c.gridx = 3;
		topPanel.add(clearButton,c);
		
	}

	private void initLookAndFeel() {

	}

	private void initDimension() {

	}

	private void initComponents() {
		mainPanel = new JPanel();
		leftPanel = new JPanel();
		jScrollPane = new JScrollPane();
		stTree = StepTemplateUtil.getTemplates(filePath, type);
		stTree.updateUI();
		SwingUtil.expandAll(stTree);
		jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollPane.setViewportView(stTree);
		rightPanel = new JPanel();
		rightUpPanel = new JPanel();

		sureButton = new JButton();
		cancelButton = new JButton();
	}

	private void initLayout() {
		setSize(500, 510);
		jScrollPane.getViewport().setBackground(Color.WHITE);
		jScrollPane.setPreferredSize(new Dimension(500, 420));
		leftPanel.add(jScrollPane);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(20, 0, 10, 0);
		c.gridx = 1;
		c.gridy = 0;
		rightUpPanel.setLayout(new GridBagLayout());
		rightUpPanel.add(sureButton, c);
		c.insets = new Insets(10, 0, 10, 0);
		c.gridy = 1;
		rightUpPanel.add(cancelButton, c);

		rightPanel.add(rightUpPanel);
		mainPanel.setLayout(new BorderLayout(1, 2));
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(leftPanel, BorderLayout.WEST);
		mainPanel.add(rightPanel, BorderLayout.CENTER);
		add(mainPanel);
	}

	private void initActions() {
		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vector = null;
				StepTemplateSearchDialog.currentTreePath=stTree.getSelectionPath();
				Object node = stTree.getLastSelectedPathComponent();
				if (node == null || node instanceof TpTreeNode) {
					SwingUtil.showMessageDialog("请选择工序模板", "提示", 2);
				} else {
					TpNode tpNode = (TpNode) node;
					String name = tpNode.getTemplate().getName();
					TpTreeNode treeNode = (TpTreeNode) tpNode.getParent();
					vector = new Vector<Object>();
					TpTreeNode parentNode = (TpTreeNode) treeNode.getParent();
					if (TpTreeNode.STEP_TYPE_LOCAL.equals(treeNode.getStepType() )) {
						vector.add(name);
					} else if (TpTreeNode.STEP_TYPE_PUBLIC.equals(treeNode.getStepType() )){
						vector = TemplateIntf
								.downloadProcessTemplate(tpNode.getTemplate()
										.getOid());
						String returnName = vector.get(0).toString();
						vector.set(
								0,
								returnName.endsWith(".zip") ? returnName
										.substring(0, returnName.length() - 4)
										: returnName);
					}
					logger.debug("vector= " + vector);
					dialog.dispose();
				}
			}
		});
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				vector = null;
				dialog.dispose();
			}
		});
		
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String value=searchTextField.getText();
				reloadTree();
				
				if(value==null||value.trim().equals("")) {
					reloadTree();
				} else {
					TpTreeNode rootNode=stTree.getRoot();
					DefaultTreeModel model = new DefaultTreeModel(rootNode);
					int count=rootNode.getChildCount();
					for(int i=count-1;i>=0;i--) {
						TpTreeNode treeNode = (TpTreeNode)rootNode.getChildAt(i);
						//SwingUtil.showMessageDialog("一层子节点名称："+ treeNode.getName(), "提示", 2);
						int childCount=treeNode.getChildCount();
						//没有子节点时，判断当前节点是否匹配
						if(childCount==0) {
							String nodeName=treeNode.getName();
							if(!nodeName.contains(value)) {
								model.removeNodeFromParent(treeNode);
							}
						} else {
							for(int n=childCount-1;n>=0;n--) {
								TpTreeNode childTreeNode = (TpTreeNode)treeNode.getChildAt(n);
								removeMatchNode(value, model,childTreeNode);
							}
						}
						
						
					}
					stTree.updateUI();
					
				}
			}
			
		});
		
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchTextField.setText("");
			}
		});
	}
	
	/** 
	  * @Description: 递归移除匹配上的节点
	  * @date 2025年10月27日下午1:23:49
	  * @author Liluwen
	  * @param value
	  * @param model
	  * @param childTreeNode  
	  * @return 
	*/
	private void removeMatchNode(String value, DefaultTreeModel model, TpTreeNode childTreeNode) {
		int grandChildCount=childTreeNode.getChildCount();
		
		if(grandChildCount==0) {
			String childNodeName=childTreeNode.getName();
			if(!childNodeName.contains(value)) {
				model.removeNodeFromParent(childTreeNode);
			}
		} else {
			//如果父节点包含搜索值，不遍历子节点
			String  treeNodeName=childTreeNode.getName();
			if(treeNodeName.contains(value)) {
				return ;
			}
			
			for(int k=grandChildCount-1;k>=0;k--) {
				TreeNode treeNode=childTreeNode.getChildAt(k);
				
				if(treeNode instanceof TpNode) {
					TpNode tpNode=(TpNode)treeNode;
					String name = tpNode.getTemplate().getName();
					String returnName=name.endsWith(".zip") ? name.substring(0, name.length() - 4):name;
					//SwingUtil.showMessageDialog("Tp节点类型名称："+ returnName, "提示", 2);
					if(!returnName.contains(value)) {
						model.removeNodeFromParent(tpNode);
					}
					
				} else if(treeNode instanceof TpTreeNode) {
					TpTreeNode grandChildTreeNode = (TpTreeNode)treeNode;
					removeMatchNode(value,model,grandChildTreeNode);
				}
				
			}
			
			//如果子节点都移除完了，把父节点也移除掉。
			int num=childTreeNode.getChildCount();
			if(num==0) {
				model.removeNodeFromParent(childTreeNode);
			}
		}
	}
	
	/** 
	  * @Description: 重新载入树中的数据
	  * @date 2025年10月27日下午4:03:29
	  * @author Liluwen  
	  * @return 
	*/
	private void reloadTree() {
		stTree = StepTemplateUtil.getTemplates(WorkSpaceUtil.getStepRootPath(), null);
		stTree.updateUI();
		SwingUtil.expandAll(stTree);
		jScrollPane.setViewportView(stTree);
		jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	}

	private void loadInitDatas() {
		sureButton.setText("确定");
		cancelButton.setText("取消");
	}

}