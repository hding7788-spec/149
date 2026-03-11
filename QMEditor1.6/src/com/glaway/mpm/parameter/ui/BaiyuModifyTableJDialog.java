package com.glaway.mpm.parameter.ui;

import com.glaway.mpm.parameter.model.data.CmBaiyuParamTableColumn;
import com.glaway.mpm.parameter.service.ProcessParameterToWCIntf;
import com.glaway.mpm.wcIntf.ResourceIntf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.*;

public class BaiyuModifyTableJDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel panel = new JPanel();
	private JPanel panel0 = new JPanel();
	private JPanel panel2 = new JPanel();
	private JButton okButton = new JButton("确定");
	private JButton cancelButton = new JButton("取消");
	private JComboBox tableType_value; //表格类型
	private JComboBox dept_value; //部门
	private String[] tableType_array = {"通用表","周期表","专用表","全局表"};
	private BaiyuModifyTableJDialog dialog;

	public BaiyuModifyTableJDialog(JFrame parent, final String oid, String tableType, String dept) {
		super(parent, true);
		this.dialog = this;
		tableType_value = new JComboBox(tableType_array);
		for (String string : tableType_array) {
			if (string.equals(tableType)) {
				tableType_value.setSelectedItem(tableType);
				break;
			}
		}
		//获取到所有的车间组，然后自动定位到当前用户所在的组
		List<String> allList = new ArrayList<String>();
		Map<String,String> workShop = ResourceIntf.getWorkShops();
		if (workShop != null && workShop.size() > 0) {
			Collection<String> coll = workShop.values();
			Iterator<String> it = coll.iterator();
			while (it.hasNext()) {
				String temp = (String) it.next();
				if (temp != null && temp.trim().length() > 0) {
					allList.add(temp);
				}
			}

			Collections.sort(allList);
		}
		allList.add(0,"通用");
		dept_value = new JComboBox(allList.toArray());
		if(allList.contains(dept)){
			dept_value.setSelectedItem(dept);
		}

		Container container = getContentPane();
		panel0.setLayout(new BorderLayout());
		panel.setLayout(new GridBagLayout());
		JScrollPane scrollPane = new JScrollPane(panel0);
		panel0.add(panel,BorderLayout.CENTER);
		container.add(scrollPane);
		panel0.add(panel2,BorderLayout.SOUTH);

		//设置网格布局管理器参数
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 5, 0, 0);
		gridBagConstraints.gridwidth = 2;

		//第一行：表格类型
		JLabel tableType_label = new JLabel("表格类型");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		panel.add(tableType_label, gridBagConstraints);

		tableType_value.setPreferredSize(new Dimension(150, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(tableType_value, gridBagConstraints);

		//第二行：部门
		final JLabel dept_label = new JLabel("部门");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 1;
		panel.add(dept_label, gridBagConstraints);

		dept_value.setPreferredSize(new Dimension(150, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(dept_value, gridBagConstraints);

		//按钮面板
		okButton.setVisible(true);
		cancelButton.setVisible(true);
		panel2.setLayout(new GridBagLayout());
		panel2.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		okButton.setPreferredSize(new Dimension(70, 23));
		okButton.setMinimumSize(new Dimension(70, 23));
		okButton.setMaximumSize(new Dimension(70, 23));
		panel2.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 15, 15), 0, 0));
		cancelButton.setPreferredSize(new Dimension(70, 23));
		cancelButton.setMinimumSize(new Dimension(70, 23));
		cancelButton.setMaximumSize(new Dimension(70, 23));
		panel2.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 15, 15), 0, 0));

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CmBaiyuParamTableColumn column = new CmBaiyuParamTableColumn();
				column.setTableType((String) tableType_value.getSelectedItem());
				column.setDept((String) dept_value.getSelectedItem());
				try {
					String result = ProcessParameterToWCIntf.updateBaiyuTemplateByOid(oid, column);
					if("Y".equals(result)){
						JOptionPane.showMessageDialog(dialog, "修改属性成功");
					}
				} catch(RemoteException ex) {
					throw new RuntimeException(ex);
				} catch(InvocationTargetException ex) {
					throw new RuntimeException(ex);
				}
				dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		setResizable(false);
		Dimension dimension2 = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int) (dimension2.getWidth() - 500) / 2 , (int) (dimension2.getHeight() - (625)) / 2, 300, 300);
		setTitle("修改模板属性");
		setVisible(true);
	}
}