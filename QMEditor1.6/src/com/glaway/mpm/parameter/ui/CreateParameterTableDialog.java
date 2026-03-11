package com.glaway.mpm.parameter.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import com.glaway.mpm.parameter.model.GWParamTableTypeMaster;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.parameter.model.data.CmTechnicsType;
import com.glaway.mpm.parameter.model.tree.XWTreeNode;
import com.glaway.mpm.parameter.util.InputEnglishLimited;
import com.glaway.mpm.util.CommonUIUtil;

public class CreateParameterTableDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton cancelButton;
    private JLabel chinaNameLabel;
    private JTextField chinaNameTextField;
    private JLabel nameLabel;
    private JTextField nameTextField;
    private JButton okButton;
    private JTextField technicsTypeTextField;
    private JLabel technicsTypeLabel;
    private XWTreeNode selTreeNode;

    public CreateParameterTableDialog(JFrame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        loadData();
        initUI();
    }

    private void loadData() {
    	JTree tree = MPMParameterMainFrame.getLeftPanel().getQualityTree();
    	selTreeNode = (XWTreeNode)tree.getSelectionPath().getLastPathComponent();
    	CmTechnicsType cmTechnicsType = (CmTechnicsType) selTreeNode.getTreeObject().getTreeNode();
    	technicsTypeTextField.setText(cmTechnicsType.getName());
    	nameTextField.setText(getEnname());
    }

	private void initUI() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("新增记录表");
//        setIconImage(IconUtil.getImageIcon(IconUtil.TECHNICS).getImage());
        setSize(460, 230);
        setResizable(false);
//        CommonUIUtil.setMiddleOnScreenWithDialog(this);
        setLocation(MouseInfo.getPointerInfo().getLocation());
        setVisible(true);
	}

	private void initComponents() {
        setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();

		//第一行
		nameLabel = new JLabel();
		nameLabel.setText("表内部名称：");
		g.gridx = 0;
		g.gridy = 0;
		g.insets = new Insets(10, 5, 5, 0);
		g.anchor = GridBagConstraints.EAST;
		add(nameLabel, g);
		nameTextField = new JTextField();
		nameTextField.setEditable(false);
		nameTextField.setPreferredSize(new Dimension(210, 30));
		nameTextField.setDocument(new InputEnglishLimited());
		g.gridx = 1;
		g.anchor = GridBagConstraints.WEST;
		g.insets = new Insets(10, 5, 5, 45);
		add(nameTextField, g);

		//第二行
		chinaNameLabel = new JLabel();
		chinaNameLabel.setText("表中文名称：");
		g.gridx = 0;
		g.gridy = 1;
		g.insets = new Insets(10, 5, 5, 0);
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
		technicsTypeLabel.setText("工艺类型：");
		g.gridx = 0;
		g.gridy = 2;
		g.insets = new Insets(10, 5, 5, 0);
		g.anchor = GridBagConstraints.EAST;
		add(technicsTypeLabel, g);
		technicsTypeTextField = new JTextField();
		technicsTypeTextField.setEditable(false);
		technicsTypeTextField.setPreferredSize(new Dimension(210, 30));
		g.gridx = 1;
		g.anchor = GridBagConstraints.WEST;
		g.insets = new Insets(10, 5, 5, 45);
		add(technicsTypeTextField, g);

		okButton = new JButton();
		okButton.addActionListener(this);
        cancelButton = new JButton();
        cancelButton.addActionListener(this);
        okButton.setText("确  定");
        okButton.setPreferredSize(new Dimension(100, 30));
        cancelButton.setText("取  消");
        cancelButton.setPreferredSize(new Dimension(100, 30));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(0, 20, 0));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        //第五行 按钮
		g.gridx = 1;
		g.gridy = 3;
		g.insets = new Insets(10, 5, 5, 5);
		g.anchor = GridBagConstraints.CENTER;
		add(buttonPanel, g);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelButton) {
			this.setVisible(false);
		} else if (e.getSource() == okButton) {
			if (checkValue() && checkValuesExsit()) {
				String name = nameTextField.getText();
				String chinaName = chinaNameTextField.getText();
				String technicsType = technicsTypeTextField.getText();
				String version = "1";

				CmParamTableType paramTableType = new CmParamTableType();
				paramTableType.setName(chinaName);
				paramTableType.setEnName(name);
				paramTableType.setTechnicsType(technicsType);
				paramTableType.setVersion(version);

				//保存参数表格类型信息至数据库
				paramTableType = MPMParameterProcessor.createParamTableType(paramTableType);

				//将新建的参数类型插入到当前选中节点的子节点中
				MPMParameterProcessor.addParamTableTypeNode(selTreeNode, paramTableType);

				this.setVisible(false);
			}
		}
	}

	private boolean checkValuesExsit() {
		String name = nameTextField.getText();

		List<GWParamTableTypeMaster> list = MPMParameterProcessor.queryAllParamTableTypeMasters();
		if (list != null && !list.isEmpty()) {
			for (GWParamTableTypeMaster master : list) {
				if (name.equals(master.getName())) {
					JOptionPane.showMessageDialog(null, "内部名称["+name+"]已存在，不能创建！");
					return false;
				}
			}
		}
		return true;
	}

	private boolean checkValue() {
		String name = nameTextField.getText();
		String chinaName = chinaNameTextField.getText();
		StringBuffer buf = new StringBuffer();
		if (name == null || "".equals(name)) {
			buf.append("表内部名称不能空！\n");
		}
		if (chinaName == null || "".equals(chinaName)) {
			buf.append("表中文名称不能空！\n");
		}

		if (buf.toString().length() > 0) {
			JOptionPane.showMessageDialog(null, buf.toString());
			return false;
		}
		return true;
	}
	private String getEnname(){
		StringBuffer bf = new StringBuffer();
		String maxEnname = MPMParameterProcessor.queryMaxNameFromTypeMaster();
		System.out.println("maxEnname===================>>>>>>>>>>>>>>>>>"+maxEnname);
		if(maxEnname.equals("CommonParamTable")){
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
}
