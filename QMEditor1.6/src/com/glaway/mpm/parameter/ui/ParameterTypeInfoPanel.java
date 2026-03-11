package com.glaway.mpm.parameter.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.glaway.mpm.parameter.constants.ParameterConstants;
import com.glaway.mpm.parameter.helper.MPMParameterProcessor;
import com.glaway.mpm.parameter.model.data.CmParameterType;
import com.glaway.mpm.parameter.model.tree.XWTreeNode;
import com.glaway.mpm.util.CommonUtil;

/**
 * 检验特性编辑面板
 * @author wxl
 */
public class ParameterTypeInfoPanel extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
    private JLabel valueTypeLabel;
    private JComboBox valueTypeComboBox;
    private JButton okButton;
    private JTextField technicsTypeField;
    private JLabel technicsTypeLabel;
    private JLabel chinaNameLabel;
    private JTextField chinaNameTextField;
    private JLabel enNameLabel;
    private JTextField enNameTextField;

    public ParameterTypeInfoPanel() {
        initComponents();
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
		valueTypeLabel = new JLabel();
		valueTypeLabel.setText("特性值类型：");
		g.gridx = 0;
		g.gridy = 3;
		g.insets = new Insets(10, 0, 5, 0);
		g.anchor = GridBagConstraints.EAST;
		add(valueTypeLabel, g);
		valueTypeComboBox = new JComboBox(ParameterConstants.COLUMN_DATATYPE);
		valueTypeComboBox.setPreferredSize(new Dimension(210, 30));
		g.gridx = 1;
		g.anchor = GridBagConstraints.WEST;
		g.insets = new Insets(10, 5, 5, 45);
		add(valueTypeComboBox, g);

		okButton = new JButton();
		okButton.addActionListener(this);
        okButton.setText("保  存");
        okButton.setPreferredSize(new Dimension(100, 30));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(0, 20, 0));
        buttonPanel.add(okButton);

        //第六行 按钮
		g.gridx = 1;
		g.gridy = 4;
		g.insets = new Insets(10, 5, 5, 5);
		g.anchor = GridBagConstraints.WEST;
		add(buttonPanel, g);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			if (checkValue()) {
				String enName = enNameTextField.getText();
				String chinaName = chinaNameTextField.getText();
				String technicsType = technicsTypeField.getText();
				String valueType = CommonUtil.objectToString(valueTypeComboBox.getSelectedItem());

				CmParameterType parameterType = new CmParameterType();
				parameterType.setName(chinaName);
				parameterType.setEnName(enName);
				parameterType.setValueType(valueType);
				parameterType.setTechnicsType(technicsType);

				parameterType = MPMParameterProcessor.saveParameterType(parameterType);
				MPMParameterProcessor.refreshTreeNode(parameterType);
			}
		}

	}

	private boolean checkValue() {
		String chinaName = chinaNameTextField.getText();
		StringBuffer buf = new StringBuffer();
		if (chinaName == null || "".equals(chinaName)) {
			buf.append("名称不能为空！\n");
		}
		if (buf.toString().length() > 0) {
			JOptionPane.showMessageDialog(null, buf.toString());
			return false;
		}
		return true;
	}

	public void setUIValues(XWTreeNode treeNode) {
		if (treeNode == null)
			return ;

		clear();
		CmParameterType parameterType = (CmParameterType) treeNode.getTreeObject().getTreeNode();
		if (parameterType != null) {
			if (parameterType.getEnName().equals(parameterType.getName())) {
				setUIEnabled(false);
			} else {
				setUIEnabled(true);
			}

			enNameTextField.setText(parameterType.getEnName());
			chinaNameTextField.setText(parameterType.getName());
			technicsTypeField.setText(parameterType.getTechnicsType());
			valueTypeComboBox.setSelectedItem(parameterType.getValueType());
		}
	}

	private void clear() {
		enNameTextField.setText("");
		chinaNameTextField.setText("");
		technicsTypeField.setText("");
		valueTypeComboBox.setSelectedIndex(0);
	}

	private void setUIEnabled(boolean b) {
		chinaNameTextField.setEditable(b);
		valueTypeComboBox.setEnabled(b);
		okButton.setEnabled(b);
	}
}
