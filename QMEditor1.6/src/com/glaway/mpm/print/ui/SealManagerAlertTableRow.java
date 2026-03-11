package com.glaway.mpm.print.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.glaway.mpm.print.service.PrintToWCIntf;

/**
*
* @author likaicheng
*
*/

public class SealManagerAlertTableRow extends JPanel{
	/**
	 *
	 */
	private static final long serialVersionUID = 4558512593382276070L;
	private JLabel uuidLabel;
	private JLabel idLabel;
	private JLabel sealLabel;
	private JTextField sealValue;
	private JTextField idValue;
	private JTextField uuidValue;
	private JButton okButton;
	private boolean ok;
	private JDialog dialog;
	private String uuid;
	private String id;
	private String value;
	public SealManagerAlertTableRow(String uuid, String id, String value){
		this.uuid = uuid;
		this.id = id;
		this.value = value;
		setLayout(new BorderLayout());
		JPanel content = new JPanel();
		content.setLayout(new GridLayout(3,3));
		content.add(uuidLabel = new JLabel("ID："));
		uuidLabel.setVisible(false);
		content.add(uuidValue=new JTextField(uuid, 15));
		uuidValue.setVisible(false);
		content.add(idLabel = new JLabel("序号："));
		content.add(idValue=new JTextField(id, 15));
		idValue.setEditable(false);
		content.add(sealLabel = new JLabel("名称："));
		content.add(sealValue=new JTextField(value, 15));
		add(content,BorderLayout.CENTER);

		JPanel submit = new JPanel();
		okButton = new JButton("确定");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String uuid = getUuid();
				String value =  getValue();
				if("".equals(value) || null == value){
					ok = false;
					JOptionPane.showMessageDialog(null, "修改后的印章名不可为空！");
				}else{
					try {
						Boolean flag = SealManagerAlertTableRow.alterSealToDB(uuid, value);
						if(flag){
							ok = true;
							JOptionPane.showMessageDialog(null, "修改成功！");
							dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							closeWindow();
						}else{
							ok = false;
							JOptionPane.showMessageDialog(null, "修改失败！");
							dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							closeWindow();
						}
					} catch (RemoteException e1) {
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		JButton cancelButton = new JButton("取消");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ok = false;
				dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				closeWindow();
			}
		});
		submit.add(okButton);
		submit.add(cancelButton);
		add(submit,BorderLayout.SOUTH);
	}
	public String getUuid(){
		return uuidValue.getText();
	}
	public String getValue(){
		return sealValue.getText();
	}
	public boolean selectedButton(){
		return ok;
	}
	private  static Boolean alterSealToDB(String uuid, String name) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.alterSeal(uuid, name);
	}
	public void showDialog(Component parent,String title){
		//ok = false;
		JFrame ower = null;
		if(parent instanceof JFrame)
			ower = (JFrame)parent;
		else
			ower = (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, parent);

		if(dialog == null || dialog.getOwner() != ower){
			dialog = new JDialog(ower,true);
			dialog.add(this);
			dialog.getRootPane().setDefaultButton(okButton);
			dialog.pack();
		}
		dialog.setLocationRelativeTo(ower);
		dialog.setTitle(title);
		dialog.setVisible(true);
	}

	public void closeWindow(){
		this.dialog.dispose();
	}
}