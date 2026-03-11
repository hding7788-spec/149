package com.glaway.mpm.print.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.UUID;
import java.util.regex.Pattern;

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

public class SealManagerAddTableRow extends JPanel{
	/**
	 *
	 */
	private static final long serialVersionUID = -3410101801967567120L;
	private JLabel uuidLabel;
	private JLabel sealLabel;
	private JLabel numberLabel;
	private JTextField uuidValue;
	private JTextField sealValue;
	private JTextField numberValue;
	private JButton okButton;
	private boolean ok;
	private JDialog dialog;
	private String number;
	public SealManagerAddTableRow(String number){
		this.number = number;
		setLayout(new BorderLayout());
		JPanel content = new JPanel();
		content.setLayout(new GridLayout(3,3));
		content.add(uuidLabel = new JLabel("ID："));
		uuidLabel.setVisible(false);
		content.add(uuidValue = new JTextField(UUID.randomUUID().toString(), 15));
		uuidValue.setVisible(false);

		content.add(sealLabel = new JLabel("名称："));
		content.add(sealValue = new JTextField(null, 15));

		content.add(numberLabel = new JLabel("序号："));
		numberLabel.setVisible(false);
		content.add(numberValue = new JTextField(number, 15));
		numberValue.setVisible(false);
		add(content,BorderLayout.CENTER);

		JPanel submit = new JPanel();
		okButton = new JButton("确定");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String uuid = (String)getUuidValue();
				String value = (String)getSealValue();
				String number = (String)getSealNumber();
				if("".equals(value) || null == value){
					ok = false;
					JOptionPane.showMessageDialog(SealManagerAddTableRow.this,"印章名不能为空！");
					return;
				}else{
					Boolean flag;
					try {
						flag = SealManagerAddTableRow.addSealToDB(uuid, value, number);
						if(flag == true){
							ok = true;
							JOptionPane.showMessageDialog(SealManagerAddTableRow.this,"添加成功！");
							dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							closeWindow();
						}else{
							ok = false;
							JOptionPane.showMessageDialog(SealManagerAddTableRow.this,"添加失败！");
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
//	public DataItem getDataItem(){
//		return new DataItem(name.getText(), number.getText(), type.getText());
//	}
//	public boolean isInt(String str){
//		 boolean isInt = Pattern.compile("^-?[0-9]\\d*$").matcher(str).find();
//		 return isInt;
//	}
	private  static Boolean addSealToDB(String uuid, String name, String number) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.addSeal(uuid, name, number);
	}
	public String getUuidValue(){
		return uuidValue.getText();
	}
	public String getSealValue(){
		return sealValue.getText();
	}
	public String getSealNumber(){
		return numberValue.getText();
	}
	public boolean selectedButton(){
		return ok;
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

