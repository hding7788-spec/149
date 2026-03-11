package com.glaway.mpm.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.glaway.mpm.model.FileTemplate;
import com.glaway.mpm.swing.KVItem;
import com.glaway.mpm.swing.KVJComboBox;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.ptc.windchill.uwgm.common.prefs.res.newCadDocPrefsResource;

public class TechnicStateTempChooseDialog extends JDialog{
	private static final long serialVersionUID = 5035035424791967956L;
	private JLabel fileNameLabel;
	private JTextField fileNameField;
	private JLabel fileTemplateLabel;
	private KVJComboBox fileTemplateComboBox;
	private FileTemplate fileTempalte;

	private JTextField tempDescText;
	private JLabel tempDescLabel;

	private JLabel makeDeptLabel;
	private JComboBox makeDeptCombobox;
	private JLabel useDeptLabel;
	private JComboBox useDeptCombobox;
	private JLabel technicStateLabel;
	private JTextField technicStateField;


	public TechnicStateTempChooseDialog(JFrame f, Vector<KVItem> items) {

		super(f, true);
		setTitle("选择模板");
		setResizable(false);
		setLocationRelativeTo(f);

		JPanel main = new JPanel();
		main.setLayout(new GridBagLayout());

		//获取到所有的车间组，然后自动定位到当前用户所在的组
		Vector<String> allList = new Vector<String>();
		Map<String, String> workShop = ResourceIntf.getWorkShops();
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

		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		makeDeptLabel = new JLabel("制造部门");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		main.add(makeDeptLabel, gridBagConstraints);
		makeDeptCombobox = new JComboBox(allList);
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		main.add(makeDeptCombobox, gridBagConstraints);

		useDeptLabel = new JLabel("使用部门");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		main.add(useDeptLabel, gridBagConstraints);
		useDeptCombobox = new JComboBox(allList);
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		main.add(useDeptCombobox, gridBagConstraints);

		technicStateLabel = new JLabel("工艺状态");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		main.add(technicStateLabel, gridBagConstraints);
		technicStateField = new JTextField();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		// gridBagConstraints.gridheight = 5;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		main.add(technicStateField, gridBagConstraints);

		fileNameLabel = new JLabel("文件名称：");

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		main.add(fileNameLabel, gridBagConstraints);
		fileNameField = new JTextField();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		fileNameField.setPreferredSize(new Dimension(150,25));
		main.add(fileNameField, gridBagConstraints);

		fileTemplateLabel = new JLabel("模板类型");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		main.add(fileTemplateLabel, gridBagConstraints);
		fileTemplateComboBox = new KVJComboBox(items);
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		main.add(fileTemplateComboBox, gridBagConstraints);

		tempDescLabel = new JLabel("模板描述");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		main.add(tempDescLabel, gridBagConstraints);
		tempDescText =  new JTextField();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		main.add(tempDescText, gridBagConstraints);




		final JButton button = new JButton();
		button.setText(" 确 定 ");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.gridx = 0;
		gridBagConstraints_1.gridy = 6;
		gridBagConstraints_1.gridwidth = 2;
		gridBagConstraints_1.insets = new Insets(30, 5, 0, 5);
		main.add(button, gridBagConstraints_1);
		this.getContentPane().add(main);

	    TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"选择模板类型");
	    main.setBorder(border);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String fileName = fileNameField.getText();
				KVItem item = (KVItem)fileTemplateComboBox.getSelectedItem();
				if(fileName==null||item==null||"".equals(fileName.trim())){
					fileTempalte = null;
					JOptionPane.showMessageDialog(TechnicStateTempChooseDialog.this, "请输入文件名称！");
				}else{
					fileTempalte = new FileTemplate();
					fileTempalte.setDisplayName(fileName);
					fileTempalte.setFileName(System.currentTimeMillis() + "");
					fileTempalte.setTempId(item.getKey());
					fileTempalte.setTempName(item.getValue());
					fileTempalte.setDesc(tempDescText.getText());
					fileTempalte.setMakeDept((String) makeDeptCombobox.getSelectedItem());
					fileTempalte.setUseDept((String) useDeptCombobox.getSelectedItem());
					fileTempalte.setTechnicsState(technicStateField.getText());
					TechnicStateTempChooseDialog.this.dispose();
				}
			}
		});

	}



	public boolean isNumber(String str) {
		java.util.regex.Pattern pattern = java.util.regex.Pattern
				.compile("[0-9]*");
		java.util.regex.Matcher match = pattern.matcher(str);
		if (match.matches() == false) {
			return false;
		} else {
			return true;
		}
	}

	public FileTemplate showDialog() {
		setSize(300, 400);
		setVisible(true);
		return fileTempalte;
	}


}
