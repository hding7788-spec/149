package com.glaway.mpm.parameter.designui;

import com.glaway.mpm.consCheck.ConsCheckApplyMainDialog;
import com.glaway.mpm.consCheck.ConsCheckComboBox;
import com.glaway.mpm.parameter.service.ProcessParameterToWCIntf;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.IconUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;


/**
 * 新建检验记录表
 * @author zhuhao 2017.10.30
 *
 */
public class CreateCheckParamTableTypeDialog extends JDialog{

	private static final long serialVersionUID = 8809250949724305830L;
	private NewCheckParamTabbedPanel panel;
	private NewCheckParamTablePanel checkPanel;
	private JPanel panel0 = new JPanel();
	private JPanel panel1 = new JPanel();
	private JPanel panel2 = new JPanel();
	private JButton okButton = new JButton("确定");
	private JButton cancelButton = new JButton("取消");
	JTextField DYBBM_Value = new JTextField();
	JComboBox BZJLX_Value = new JComboBox(new Object[]{"检测类","记录类"});
	JLabel BZJLX_Value2 = new JLabel("");
	private ConsCheckComboBox proComboBox;
	private JButton proJButton;
	private ConsCheckComboBox tableComboBox;
	private JButton tableJButton;
	JTextField MXCS_Value = new JTextField();
	private boolean isModify;
	int Selected;

	UUID id = UUID.randomUUID();    //modify by lkc 2017.12.04
	String struuid = id.toString();
	JLabel uuid = new JLabel(struuid);


	public CreateCheckParamTableTypeDialog(NewCheckParamTabbedPanel panel, boolean b) {
		this.panel = panel;
		this.isModify = b;
		List<NewCheckParamTablePanel> tabPanel = panel.getCheckParamTablePanelList();//所有检验记录表
		if(tabPanel != null && !tabPanel.isEmpty()){
			Selected = panel.getSelectedTAB();
			checkPanel = tabPanel.get(Selected);//选中的表
		}
		initComponents();
		initUI();
	}
	private void initComponents() { //modify by lkc 2017.12.06

		String value = "";
		if(isModify && checkPanel != null){
			value = checkPanel.getTypeName();
		}
		if("检测类".equals(value) || "".equals(value)){
			proComboBox = new ConsCheckComboBox("JC");
		}else if("记录类".equals(value)){
			proComboBox = new ConsCheckComboBox("JL");
		}
		BZJLX_Value.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				String value = BZJLX_Value.getSelectedItem().toString();
				if(value.equals("检测类")){
					proComboBox.type = "JC";
					proComboBox.recordMap = proComboBox.jcMap;
					proComboBox.reSetList();
				}else if(value.equals("记录类")){
					proComboBox.type = "JL";
					proComboBox.recordMap = proComboBox.jlMap;
					proComboBox.reSetList();
				}
			}
		});
		tableComboBox = new ConsCheckComboBox("TABLE");
		proJButton = new JButton(IconUtil.getImageIcon("/images/cappBom_cappDesign.gif"));
		proJButton.setToolTipText("项目名");
		tableJButton = new JButton(IconUtil.getImageIcon("/images/cappBom_cappDesign.gif"));
		tableJButton.setToolTipText("套表名");

		this.proJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ConsCheckApplyMainDialog dialog = new ConsCheckApplyMainDialog(CreateCheckParamTableTypeDialog.this,proComboBox.type);
					dialog.showDialog();
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(CreateCheckParamTableTypeDialog.this, "选取项目名出现错误！", "提示", 1);
				}
			}
		});
		this.tableJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ConsCheckApplyMainDialog dialog = new ConsCheckApplyMainDialog(CreateCheckParamTableTypeDialog.this,"TABLE");
					dialog.showDialog();
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(CreateCheckParamTableTypeDialog.this, "选取套表名出现错误！", "提示", 1);
				}
			}
		});

		Container container = getContentPane();
		panel0.setLayout(new BorderLayout());
		panel1.setLayout(new GridBagLayout());
		JScrollPane scrollPane = new JScrollPane(panel0);
		panel0.add(panel1,BorderLayout.CENTER);
		container.add(scrollPane);
		panel0.add(panel2,BorderLayout.SOUTH);

		//设置网格布局管理器参数
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 5, 0, 0);
		gridBagConstraints.gridwidth = 2;


		//第一行：单元表表名
		JLabel DYBBM = new JLabel("单元表表名*");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		panel1.add(DYBBM, gridBagConstraints);

		DYBBM_Value.setText("");
		DYBBM_Value.setPreferredSize(new Dimension(500, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel1.add(DYBBM_Value,gridBagConstraints);

		//第二行：表主件类型
		JLabel BZJLX = new JLabel("表主件类型*");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 1;
		panel1.add(BZJLX, gridBagConstraints);
		if(isModify){
			BZJLX_Value2.setPreferredSize(new Dimension(500, 23));
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.insets = new Insets(5, 5, 0, 5);
			panel1.add(BZJLX_Value2, gridBagConstraints);
		}else{
			BZJLX_Value.getSelectedItem();
			BZJLX_Value.setPreferredSize(new Dimension(400, 23));
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.insets = new Insets(5, 5, 0, 5);
			panel1.add(BZJLX_Value, gridBagConstraints);
		}


		//第三行：项目名
		JLabel XMM = new JLabel("项目名*");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 1;
		panel1.add(XMM, gridBagConstraints);

		proComboBox.setPreferredSize(new Dimension(400, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(5, 5, 0, 0);
		panel1.add(proComboBox, gridBagConstraints);

		proJButton.setPreferredSize(new Dimension(30, 23));
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(5, -100, 0, 5);
		panel1.add(proJButton, gridBagConstraints);

		//第四行：套表名
		JLabel TBM = new JLabel("套表名*");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel1.add(TBM, gridBagConstraints);

//		TBM_Value.getSelectedItem();
		tableComboBox.setPreferredSize(new Dimension(400, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel1.add(tableComboBox, gridBagConstraints);

		tableJButton.setPreferredSize(new Dimension(30, 23));
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = new Insets(5, -100, 0, 5);
		panel1.add(tableJButton, gridBagConstraints);

		//第五行：每项/次数
		JLabel MXCS = new JLabel("每项/次数*");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel1.add(MXCS, gridBagConstraints);

		MXCS_Value.setText("1"); //modify by lkc 2017.12.4

		MXCS_Value.setPreferredSize(new Dimension(500, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel1.add(MXCS_Value, gridBagConstraints);


		JLabel ID = new JLabel("id");
		ID.setVisible(false);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridwidth = 1;
		panel1.add(ID, gridBagConstraints);

		uuid.setVisible(false);
		uuid.setPreferredSize(new Dimension(500, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel1.add(uuid, gridBagConstraints);

		if(isModify){
			String getDYBBM = new String();
			String getBZJLX = new String();
			String getXMM = new String();
			String getTBM = new String();
			String getMXCS = new String();
			String getID = new String();
			if(checkPanel != null){
				getDYBBM = checkPanel.getTableName();
				getBZJLX = checkPanel.getTypeName();
				getXMM = checkPanel.getXmm_Path();
				getTBM = checkPanel.getTbm_Path();
				getMXCS = checkPanel.getMxCs_Value();
				getID = checkPanel.getId();
				DYBBM_Value.setText(getDYBBM);
				BZJLX_Value2.setText(getBZJLX);
				proComboBox.setSelectedItem(getXMM);
				tableComboBox.setSelectedItem(getTBM);
				MXCS_Value.setText(getMXCS);
				uuid.setText(getID);
			}
		}

		MXCS_Value.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent e) {
                int keyChar = e.getKeyChar();
                if(keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9){

                }else{
                    e.consume(); //屏蔽掉非法输入
                }
            }
        });

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


		//确定按钮监听
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String Dybbm_Value = DYBBM_Value.getText();
				String Bzjlx_Value = (String) BZJLX_Value.getSelectedItem();
				String Xmm_Value = (String) proComboBox.getSelectedItem();
				String Tbm_Value = (String) tableComboBox.getSelectedItem();
				String MxCs_Value = MXCS_Value.getText();

				String uuid_Value = uuid.getText();  //modify by lkc 2017.12.05

				if("".equals(Dybbm_Value) || Bzjlx_Value == null){
					JOptionPane.showMessageDialog(null, "单元表表名不能为空");
					return ;
				}else if("".equals(Bzjlx_Value) || Bzjlx_Value == null){
					JOptionPane.showMessageDialog(null, "表主件类型不能为空");
					return ;
				}else if("".equals(Xmm_Value) || Xmm_Value == null){
					JOptionPane.showMessageDialog(null, "项目名不能为空");
					return ;
				}else if("".equals(Tbm_Value) || Tbm_Value == null){
					JOptionPane.showMessageDialog(null, "套表名不能为空");
					return ;
				}else if("".equals(MxCs_Value) || MxCs_Value == null){
					JOptionPane.showMessageDialog(null, "每项/次数不能为空");
					return ;
				}
				if(!isNumeric(MxCs_Value)){
					JOptionPane.showMessageDialog(null, "每项/次数只能填写数字");
					return ;
				}

				if(isModify){
					checkPanel.setTableName(Dybbm_Value);
					checkPanel.setXmm_Path(Xmm_Value);
					Xmm_Value = Xmm_Value.lastIndexOf("_") > -1 ? Xmm_Value.substring(Xmm_Value.lastIndexOf("_")+1) : Xmm_Value;
					checkPanel.setXmm_Value(Xmm_Value);
					checkPanel.setTbm_Path(Tbm_Value);
					Tbm_Value = Tbm_Value.lastIndexOf("_") > -1 ? Tbm_Value.substring(Tbm_Value.lastIndexOf("_")+1) : Tbm_Value;
					checkPanel.setTbm_Value(Tbm_Value);
					checkPanel.setMxCs_Value(MxCs_Value);
					panel.changeValue(Dybbm_Value, Bzjlx_Value, Xmm_Value, Tbm_Value, MxCs_Value,Selected);
					dispose();
				}else{
					String xmm_Value = Xmm_Value.lastIndexOf("_") > -1 ? Xmm_Value.substring(Xmm_Value.lastIndexOf("_")+1) : Xmm_Value;
					String tbm_Value = Tbm_Value.lastIndexOf("_") > -1 ? Tbm_Value.substring(Tbm_Value.lastIndexOf("_")+1) : Tbm_Value;
					panel.addCheckRecordTableType(Dybbm_Value, Bzjlx_Value, xmm_Value,Xmm_Value, tbm_Value,Tbm_Value, MxCs_Value, uuid_Value);
					dispose();
				}
			}

		});

		//取消按钮监听
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});


	}
	private void initUI() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if(isModify){
			setTitle("更改属性");
		}else{
			setTitle("创建检验记录表");
		}
		setModal(true);
		setSize(600, 300);
		CommonUIUtil.setMiddleOnScreenWithDialog(this);
		setVisible(true);
	}

	private List<String> getTableConfigName(){
		try {
			return ProcessParameterToWCIntf.searhTableConfigName();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<String> getObjConfigNameJC(){
		try {
			return ProcessParameterToWCIntf.searhObjConfigNameJC();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	private List<String> getObjConfigNameJL(){
		try {
			return ProcessParameterToWCIntf.searhObjConfigNameJL();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	//判断是否为数字
	public static boolean isNumeric(String str){
	    for(int i=str.length();--i>=0;){
	        int chr=str.charAt(i);
	        if(chr<48 || chr>57)
	            return false;
	    }
	   return true;
	}

	public ConsCheckComboBox getProComboBox() {
		return proComboBox;
	}

	public ConsCheckComboBox getTableComboBox() {
		return tableComboBox;
	}
}
