package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.service.PrintToWCIntf;
/**
 *
 * @author lkc
 *
 */
public class FileBarcodeQueryPanel extends JFrame{
	/**
	 *
	 */
	private static final long serialVersionUID = -6887254098046897052L;
	private JPanel topPanel;
	private JPanel downPanel;
	private JScrollPane tablePanel;
	private JTable table;
	private DefaultTableModel tableModel;
	private JTextField barCodeValue;
	private JTextField deptValue;
	private JLabel barCodeLabel;
	private JLabel deptLabel;
	private JButton closeButton;
	private CmPrintRecordInfoBean cmPrintRecordInfoBean;
	public FileBarcodeQueryPanel(){
		initComponents();
		initLayout();
		initListener();
		initUI();
	}
	public void initComponents(){
		cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
		barCodeValue = new JTextField();
		deptValue = new JTextField();
		barCodeLabel = new JLabel("文件条码:");
		deptLabel = new JLabel("分发部门:");
		closeButton = new JButton("关闭");
		topPanel = new JPanel();
		downPanel = new JPanel();
		tablePanel = new JScrollPane();
		String[] tableColumn = {"文件编号", "文件名称", "版本", "阶段标记", "密级", "印章", "条码"};
		String[][] tableData = {};
		tableModel = new DefaultTableModel(tableData,tableColumn)
		{
			private static final long serialVersionUID = -5817421228650378228L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}

		};
		table = new JTable(tableModel);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 20));
		table.setRowHeight(30);
		table.getColumnModel().getColumn(0).setPreferredWidth(80);
		table.getColumnModel().getColumn(1).setPreferredWidth(120);
		table.getColumnModel().getColumn(2).setPreferredWidth(50);
		table.getColumnModel().getColumn(3).setPreferredWidth(30);
		table.getColumnModel().getColumn(4).setPreferredWidth(50);
		table.getColumnModel().getColumn(5).setPreferredWidth(80);
		table.getColumnModel().getColumn(6).setPreferredWidth(80);
		DefaultTableCellRenderer render = (DefaultTableCellRenderer) table.getDefaultRenderer(getClass());
		render.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		tablePanel.setViewportView(table);
	}

	public void initLayout(){
		topPanel.setLayout(new GridBagLayout());
		GridBagConstraints topGrid = new GridBagConstraints();
		topGrid.gridx = 0;
		topGrid.gridy = 0;
		topGrid.gridwidth = 1;
		topGrid.gridheight = 1;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.anchor = GridBagConstraints.NORTHEAST;
		topGrid.insets = new Insets(30, 5, 20, 5);
		topPanel.add(barCodeLabel, topGrid);

		topGrid.gridx =1;
		topGrid.gridy = 0;
		topGrid.gridwidth = 1;
		topGrid.gridheight = 1;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topGrid.insets = new Insets(30, 5, 20, 5);
		topPanel.add(barCodeValue, topGrid);

		topGrid.gridx = 2;
		topGrid.gridy = 0;
		topGrid.gridwidth = 1;
		topGrid.gridheight = 1;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.anchor = GridBagConstraints.NORTHEAST;
		topGrid.insets = new Insets(30, 0, 20, 5);
		topPanel.add(deptLabel, topGrid);

		topGrid.gridx = 3;
		topGrid.gridy = 0;
		topGrid.gridwidth = 1;
		topGrid.gridheight = 1;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topGrid.insets = new Insets(30, 5, 20, 5);
		topPanel.add(deptValue, topGrid);

		downPanel.setLayout(new GridBagLayout());
		GridBagConstraints downGrid = new GridBagConstraints();

		downGrid.gridx = 0;
		downGrid.gridy = 0;
		downGrid.gridwidth = 9;
		downGrid.gridheight = 1;
		downGrid.weightx = 1;
		downGrid.weighty = 1;
		downGrid.fill = GridBagConstraints.BOTH;
		downGrid.insets = new Insets(5, 5, 5, 5);
		tablePanel.setPreferredSize(new Dimension(690, 350));
		downPanel.add(tablePanel, downGrid);

		downGrid.gridx = 8;
		downGrid.gridy = 1;
		downGrid.gridwidth = 1;
		downGrid.gridheight = 1;
		downGrid.weightx = 0;
		downGrid.weighty = 0;
		downGrid.fill = GridBagConstraints.NORTHWEST;
		downGrid.anchor = GridBagConstraints.NORTHEAST;
		downGrid.insets = new Insets(5, 5, 5, 5);
		downPanel.add(closeButton, downGrid);

		this.add(topPanel);
        this.add(downPanel);
		SpringLayout springLayout = new SpringLayout();
        springLayout.putConstraint(SpringLayout.NORTH, topPanel, 0, SpringLayout.NORTH, this.getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, topPanel, 0, SpringLayout.WEST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, topPanel, 0, SpringLayout.EAST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.NORTH, downPanel, 0, SpringLayout.SOUTH, topPanel);
        springLayout.putConstraint(SpringLayout.WEST, downPanel, 0, SpringLayout.WEST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, downPanel, 0, SpringLayout.EAST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.SOUTH, downPanel, 0, SpringLayout.SOUTH, this.getContentPane());
        this.setLayout(springLayout);

		barCodeValue.setPreferredSize(new Dimension(120, 25));
		deptValue.setPreferredSize(new Dimension(120, 25));
		closeButton.setPreferredSize(new Dimension(120, 25));

	}
	public void initListener(){
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String str = e.getActionCommand();
				if(str == "关闭"){
				   int result = JOptionPane.showConfirmDialog(getContentPane(),"是否关闭当前页面？","提示",JOptionPane.YES_NO_OPTION);
				   if(result == JOptionPane.YES_OPTION){
					   System.exit(0);
				   }
				}
			}
		});

		this.addWindowListener(new WindowAdapter() {
			   public void windowClosing(WindowEvent e) {
			   int close = JOptionPane.showConfirmDialog(getContentPane(), "是否关闭当前页面？", "提示",JOptionPane.YES_NO_OPTION);
			   if (close == JOptionPane.YES_OPTION) {
			     System.exit(0);
			    }else{
					   setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				   }
			 }
		});
		barCodeValue.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				String barcode = (String)barCodeValue.getText();
				if("".equals(barcode) && null == barcode){
					deptValue.setText("");
				}else{
					try {
						cmPrintRecordInfoBean = getInfoByBarCode(barcode);
						String dept = cmPrintRecordInfoBean.getGetDept();
						deptValue.setText(dept);
						if(!"".equals(dept) && null != dept){
							tableModel.setRowCount(0);
							String fileNumber = cmPrintRecordInfoBean.getFileNumber();
							String fileName = cmPrintRecordInfoBean.getFileName();
							String docVersion = cmPrintRecordInfoBean.getDocVersion();
							String phaseCode = cmPrintRecordInfoBean.getPhaseCode();
							String secret = cmPrintRecordInfoBean.getSecret();
							String batch = cmPrintRecordInfoBean.getBatch();
							String barCode = cmPrintRecordInfoBean.getBarCode();
							String[] value = {fileNumber, fileName, docVersion, phaseCode, secret, batch, barCode};

							tableModel.addRow(value);
						}
					} catch (RemoteException e1) {
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
					}
				}
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
			}
			});
	}

	public void initUI(){
		this.setTitle("文件条码信息查询");
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize((int)(width*0.9), (int)(height*0.9));
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
	public CmPrintRecordInfoBean getInfoByBarCode(String barCode) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getInfoByBarCode(barCode);
	}
}

