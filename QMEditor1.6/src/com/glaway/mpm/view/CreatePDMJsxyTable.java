package com.glaway.mpm.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.util.IntfUtil;

public class CreatePDMJsxyTable  extends JDialog {
	public static String number;
	public static String version;
	 private  NewTechnicsPart frame;
	 private  XWTreeNode treeNode;
	 private  JPanel panel=new JPanel();
	 private  JPanel top=new JPanel();
	 private  JScrollPane center=new JScrollPane();
	 private  JPanel bottom=new JPanel();
	 private JButton  sure=new JButton("确定");
	 private JButton  close=new JButton("取消");
	 private DefaultTableModel tableModel = new DefaultTableModel() {
         private static final long serialVersionUID = 1L;

         public boolean isCellEditable(int row, int column) {
             return false;
         }
     };
	 private JTable table= new JTable(tableModel);
	 private Object[][] tableData;
	 private JLabel bianhao = new JLabel("编号");
	 private  JTextField bianhaoText = new JTextField();
	 private JLabel mingcheng = new JLabel("名称");
	 private JTextField mingchengText = new JTextField();
	 private JButton sousuo = new JButton("搜索");
	public CreatePDMJsxyTable(NewTechnicsPart frame,XWTreeNode node) {
		super(frame, true);
		this.frame = frame;
		treeNode=node;
		setTitle("选择技术协议");
		setIconImage(new ImageIcon(getClass().getResource("/images/technics.gif")).getImage());
		Dimension dimension2 = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int) (dimension2.getWidth() - 800) / 2 , (int) (dimension2.getHeight() - (625)) / 2, 800, 580);

//		initData();
		initComponents();
		setVisible(true);

	}
	private void initData(ArrayList list) {
	        tableModel.setRowCount(0);

//		tableData=new Object[list.size()][3];
		for (int i = 0; i < list.size(); i++) {
		    Vector vector = new Vector();
		    for (int j = 0; j < table.getColumnCount(); j++) {
                vector.add("");
            }
            tableModel.addRow(vector);
			HashMap map = (HashMap) list.get(i);
			String number = (String) map.get("number");
			String name = (String) map.get("name");
			String version = (String) map.get("version");
			tableModel.setValueAt(number, i, 0);
			tableModel.setValueAt(name, i, 1);
			tableModel.setValueAt(version, i, 2);
//			tableData[i][0]=number;
//			tableData[i][1]=name;
//			tableData[i][2]=version;
		}

	}
	private void initComponents() {
//		Object[][] tableData = {};

	    sousuo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String number = bianhaoText.getText();
                String mingcheng = mingchengText.getText();
                ArrayList list = (ArrayList) IntfUtil.getPeRemoteMethodInvoke("getAllJsxyByContainer",
                        new Class[] { String.class,String.class }, new Object[] {number,mingcheng });
                initData(list);
            }
        });
	    final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(5, 5, 0, 0);
        gridBagConstraints.gridwidth = 20;

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        top.add(bianhao, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        bianhaoText.setPreferredSize(new Dimension(100, 23));
        top.add(bianhaoText, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 1;
        bianhaoText.setPreferredSize(new Dimension(100, 23));
        top.add(mingcheng, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 1;
        mingchengText.setPreferredSize(new Dimension(100, 23));
        top.add(mingchengText, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 1;
        sousuo.setPreferredSize(new Dimension(50, 23));
        top.add(sousuo, gridBagConstraints);

		String[] obj = new String[]{"编号","名称","版本"};

//		table.setModel(new DefaultTableModel(tableData,obj){
//			@Override
//			public boolean isCellEditable(int row, int column) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		});
		tableModel.addColumn("编号");
		tableModel.addColumn("名称");
		tableModel.addColumn("版本");
		Container container = getContentPane();
		JScrollPane scrollPane = new JScrollPane(panel);
		container.add(scrollPane);
		panel.setLayout(new BorderLayout());
//		center.setLayout(new GridBagLayout());
		panel.add(top,BorderLayout.NORTH);
		panel.add(center,BorderLayout.CENTER);
		center.setViewportView(table);
		panel.add(bottom,BorderLayout.SOUTH);

		bottom.setLayout(new GridBagLayout());
		bottom.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		sure.setPreferredSize(new Dimension(70, 23));
		sure.setMinimumSize(new Dimension(70, 23));
		sure.setMaximumSize(new Dimension(70, 23));
		bottom.add(sure, new GridBagConstraints(1, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 15, 15), 0, 0));
		close.setPreferredSize(new Dimension(70, 23));
		close.setMinimumSize(new Dimension(70, 23));
		close.setMaximumSize(new Dimension(70, 23));
		bottom.add(close, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 15, 15), 0, 0));

		sure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 int i = table.getSelectedRow();
				  number=(String) table.getValueAt(i, 0);
				  version=(String) table.getValueAt(i, 2);
				  if (number==null||"".equals(number)||"null".equals(number)||version==null||"".equals(version)||"null".equals(version)) {
					  JOptionPane.showMessageDialog(frame, " 编号或版本为空，请选择正确数据！", "提示",JOptionPane.INFORMATION_MESSAGE);
                  	return ;
				}else{
					dispose();
				}
			}
		});
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

	}
//	public static void main(String[] args) {
//		CreatePDMJsxyTable createPDMJsxyTable = new CreatePDMJsxyTable(null, null, null);
//		createPDMJsxyTable.setVisible(true);
//	}
}
