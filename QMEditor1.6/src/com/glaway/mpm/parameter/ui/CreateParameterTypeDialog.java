package com.glaway.mpm.parameter.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.WindowConstants;

import com.glaway.mpm.parameter.helper.MPMParameterProcessor;
import com.glaway.mpm.parameter.model.data.CmParameterType;
import com.glaway.mpm.parameter.model.tree.XWTreeNode;

/**
 * 新增参数类型
 *
 * @author 龙秀川
 *
 */
public class CreateParameterTypeDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JButton cancelButton;
   /* private JLabel valueTypeLabel;
    private JComboBox valueTypeComboBox;*/
    private JButton okButton;
//    private JButton applyButton;
    private JLabel parentTypeLabel;
    private JTextField parentTypeTextField;
    private JTextField technicsTypeField;
    private JLabel technicsTypeLabel;
    private JLabel chinaNameLabel;
    private JTextField chinaNameTextField;
    private JLabel enNameLabel;
    private JTextField enNameTextField;
    private JTree tree;
    private CmParameterType parentType;
    private XWTreeNode selTreeNode;

    public CreateParameterTypeDialog(JFrame parent, JTree tree, boolean modal) {
        super(parent, modal);
        this.tree = tree;
        initComponents();
        loadData();
        initUI();
    }

    private void loadData() {
    	selTreeNode = (XWTreeNode)tree.getSelectionPath().getLastPathComponent();

    	parentType = (CmParameterType)selTreeNode.getTreeObject().getTreeNode();
		parentTypeTextField.setText(parentType.getName());
		technicsTypeField.setText(parentType.getTechnicsType());

		enNameTextField.setText(getEnname());
	}

	private void initUI() {
    	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("新增检验特性");
//        setIconImage(IconUtil.getImageIcon(IconUtil.TECHNICS).getImage());
        setSize(500, 350);
//        CommonUIUtil.setMiddleOnScreenWithDialog(this);
        setLocation(MouseInfo.getPointerInfo().getLocation());
        setVisible(true);
	}

	private void initComponents() {
        setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();

		//第一行
		enNameLabel = new JLabel();
		enNameLabel.setText("特性内部名称：");
		g.gridx = 0;
		g.gridy = 0;
		g.insets = new Insets(10, 0, 5, 0);
		g.anchor = GridBagConstraints.EAST;
		add(enNameLabel, g);
		enNameTextField = new JTextField();
		enNameTextField.setEditable(false);
		enNameTextField.setPreferredSize(new Dimension(210, 30));
		g.gridx = 1;
		g.anchor = GridBagConstraints.WEST;
		g.insets = new Insets(10, 5, 5, 45);
		add(enNameTextField, g);

		//第二行
		chinaNameLabel = new JLabel();
		chinaNameLabel.setText("特性名称：");
		g.gridx = 0;
		g.gridy = 1;
		g.insets = new Insets(10, 0, 5, 0);
		g.anchor = GridBagConstraints.EAST;
		add(chinaNameLabel, g);
		chinaNameTextField = new JTextField();
		chinaNameTextField.setPreferredSize(new Dimension(210, 30));
		g.gridx = 1;
		g.anchor = GridBagConstraints.WEST;
		g.insets = new Insets(10, 5, 5, 45);
		add(chinaNameTextField, g);

		//第三行
		technicsTypeLabel = new JLabel();
		technicsTypeLabel.setText("专业：");
		g.gridx = 0;
		g.gridy = 2;
		g.insets = new Insets(10, 0, 5, 0);
		g.anchor = GridBagConstraints.EAST;
		add(technicsTypeLabel, g);
		technicsTypeField = new JTextField();
		technicsTypeField.setEditable(false);
		technicsTypeField.setPreferredSize(new Dimension(210, 30));
		g.gridx = 1;
		g.anchor = GridBagConstraints.WEST;
		g.insets = new Insets(10, 5, 5, 45);
		add(technicsTypeField, g);

		//第四行
		parentTypeLabel = new JLabel();
		parentTypeLabel.setText("父类型：");
		g.gridx = 0;
		g.gridy = 3;
		g.insets = new Insets(10, 0, 5, 0);
		g.anchor = GridBagConstraints.EAST;
		add(parentTypeLabel, g);
		parentTypeTextField = new JTextField();
		parentTypeTextField.setEditable(false);
		parentTypeTextField.setPreferredSize(new Dimension(210, 30));
		g.gridx = 1;
		g.anchor = GridBagConstraints.WEST;
		g.insets = new Insets(10, 5, 5, 45);
		add(parentTypeTextField, g);

		//第五行
//		valueTypeLabel = new JLabel();
//		valueTypeLabel.setText("特性值类型：");
//		g.gridx = 0;
//		g.gridy = 4;
//		g.insets = new Insets(10, 0, 5, 0);
//		g.anchor = GridBagConstraints.EAST;
//		add(valueTypeLabel, g);
//		valueTypeComboBox = new JComboBox(ParameterConstants.COLUMN_DATATYPE);
//		valueTypeComboBox.setPreferredSize(new Dimension(210, 30));
//		g.gridx = 1;
//		g.anchor = GridBagConstraints.WEST;
//		g.insets = new Insets(10, 5, 5, 45);
//		add(valueTypeComboBox, g);

//		applyButton = new JButton();
//		applyButton.addActionListener(this);
//		applyButton.setText("应  用");
//		applyButton.setPreferredSize(new Dimension(100, 30));
		okButton = new JButton();
		okButton.addActionListener(this);
        okButton.setText("确  定");
        okButton.setPreferredSize(new Dimension(100, 30));
        cancelButton = new JButton();
        cancelButton.addActionListener(this);
        cancelButton.setText("取  消");
        cancelButton.setPreferredSize(new Dimension(100, 30));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(0, 20, 0));
//        buttonPanel.add(applyButton);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        //第六行 按钮
		g.gridx = 0;
		g.gridy = 4;
		g.gridwidth = 2;
		g.insets = new Insets(10, 5, 5, 5);
		g.anchor = GridBagConstraints.WEST;
		add(buttonPanel, g);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelButton) {
			this.setVisible(false);
		} else if (e.getSource() == okButton) {
			if (checkValue() && checkIsExsitName()) {
				String chinaName = chinaNameTextField.getText();
				String enName = enNameTextField.getText();
				String technicsType = technicsTypeField.getText();
//				String valueType = CommonUtil.objectToString(valueTypeComboBox.getSelectedItem());
				String number = String.valueOf(System.currentTimeMillis());

				CmParameterType newParameterType = new CmParameterType();
				newParameterType.setNumber(number);
				newParameterType.setName(chinaName);
				newParameterType.setEnName(enName);
				newParameterType.setValueType("");
				newParameterType.setParent(String.valueOf(parentType.getEnName()));
				newParameterType.setTechnicsType(technicsType);

				//调用服务器接口新建参数类型对象
//				boolean hasChinaName = MPMParameterProcessor.hasChinaName(newParameterType);
				XWTreeNode node = getParentTechnicType(selTreeNode);
				List<String> childNodeList = new ArrayList<String>();
				getAllChildNode(node, childNodeList);
				if(childNodeList.contains(chinaName)){
					JOptionPane.showMessageDialog(null, "该专业下已存在该特性！");
				}else{
					newParameterType = MPMParameterProcessor.createParameterType(newParameterType);
					//将新建的参数类型插入到当前选中节点的子节点中
					MPMParameterProcessor.addParameterTypeNode(tree, selTreeNode, newParameterType);
					this.setVisible(false);
				}
			}
		}
//		else if (e.getSource() == applyButton) {
//			if (checkValue() && checkIsExsitName()) {
//				String chinaName = chinaNameTextField.getText();
//				String enName = enNameTextField.getText();
//				String technicsType = technicsTypeField.getText();
////				String valueType = CommonUtil.objectToString(valueTypeComboBox.getSelectedItem());
//				String number = String.valueOf(System.currentTimeMillis());
//
//				CmParameterType newParameterType = new CmParameterType();
//				newParameterType.setNumber(number);
//				newParameterType.setName(chinaName);
//				newParameterType.setEnName(enName);
//				newParameterType.setValueType("");
//				newParameterType.setParent(String.valueOf(parentType.getEnName()));
//				newParameterType.setTechnicsType(technicsType);
//
//				//调用服务器接口新建参数类型对象
//				newParameterType = MPMParameterProcessor.createParameterType(newParameterType);
//
//				//将新建的参数类型插入到当前选中节点的子节点中
//				MPMParameterProcessor.addParameterTypeNode(tree, selTreeNode, newParameterType);
//				chinaNameTextField.setText("");
//				enNameTextField.setText("");
//			}
//		}

	}

	private boolean checkIsExsitName() {
		String enName = enNameTextField.getText();
		XWTreeNode node = MPMParameterProcessor.getParamManagerNode();
		boolean flag = true;
		flag =  loopCheckTreeNode(node, enName, flag);
		return flag;
	}

	@SuppressWarnings("unchecked")
	private boolean loopCheckTreeNode(XWTreeNode parentNode, String name, boolean flag) {
		Enumeration<XWTreeNode> childs = parentNode.children();
		XWTreeNode childNode = null;;
		while (childs.hasMoreElements()) {
			childNode = childs.nextElement();
			CmParameterType parameterType = (CmParameterType) childNode.getTreeObject().getTreeNode();
			if (parameterType.getEnName().equals(name)) {
				JOptionPane.showMessageDialog(null, "已经存在该内部名称的检验特性，不能重复！");
				flag =  false;
				break;
			} else {
				flag = loopCheckTreeNode(childNode, name, flag);
			}
		}
		return flag;
	}

	private boolean checkValue() {
		String chinaName = chinaNameTextField.getText();
		String enName = enNameTextField.getText();
		StringBuffer buf = new StringBuffer();
		if (chinaName == null || "".equals(chinaName)) {
			buf.append("名称不能为空！\n");
		}
		if (enName == null || "".equals(enName)) {
			buf.append("内部名称不能为空！\n");
		}

		if (buf.toString().length() > 0) {
			JOptionPane.showMessageDialog(null, buf.toString());
			return false;
		}
		return true;
	}
	private String getEnname(){
		StringBuffer bf = new StringBuffer();
		String maxEnname = MPMParameterProcessor.queryMaxEnname();
		System.out.println("maxEnname===================>>>>>>>>>>>>>>>>>"+maxEnname);
		if(maxEnname == null || maxEnname == ""){
			bf.append("GW00000001");
		}else{
			bf.append("GW");
			int index = Integer.valueOf(maxEnname.split("W")[1]);
			String index2 = String.valueOf(index+1);
			for(int i = 0;i < 8 - index2.length(); i++){
				bf.append("0");
			}
			bf.append(index2);
		}
		return bf.toString();
	}
	public XWTreeNode getParentTechnicType(XWTreeNode node){
		String nodeName = node.getParentNode().getDisplayName();
		if(nodeName.equals("检验特性管理")){
			return node;
		}else{
			return getParentTechnicType(node.getParentNode());
		}
	}
	public void getAllChildNode(XWTreeNode parentNode, List<String> childNodeList){
		for(int i = 0; i < parentNode.getChildCount(); i++){
			XWTreeNode childNode = (XWTreeNode) parentNode.getChildAt(i);
			childNodeList.add(childNode.getDisplayName());
			getAllChildNode(childNode, childNodeList);
		}
	}
}
