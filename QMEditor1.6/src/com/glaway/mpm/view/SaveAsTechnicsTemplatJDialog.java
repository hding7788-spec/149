package com.glaway.mpm.view;

import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.util.WorkSpaceUtil;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SaveAsTechnicsTemplatJDialog extends JDialog {
	private NewTechnicsPart frame;
	private String[][] technicsTypes = LoadConfig.getInstance().getTechnicsType();
	private JTextField nameField = new JTextField();
	private JCheckBox canzhuangjian = new JCheckBox();
	private JCheckBox paizhaodian = new JCheckBox();

	private JButton okButton = new JButton("确定");
	private JButton cancelButton = new JButton("取消");

	private Element techElement;

	public SaveAsTechnicsTemplatJDialog(NewTechnicsPart frame,
			Element techElement) {
		super(frame, true);
		this.frame = frame;
		this.techElement = techElement;
		// setModal(true);
		setTitle("另存为工艺模板");

		Container container = getContentPane();
		container.setLayout(new GridBagLayout());
		container.add(new JLabel("模板名称"), new GridBagConstraints(0, 0, 1, 1, 0,
				0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(10, 10, 5, 5), 0, 0));
		container.add(nameField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(10, 5, 5, 10), 0, 0));

		if(!"SOP标准操作规程".equals(techElement.attributeValue("technicsType"))){
			container.add(new JLabel("参装件"), new GridBagConstraints(0, 1, 1, 1, 0,
					0, GridBagConstraints.EAST, GridBagConstraints.NONE,
					new Insets(10, 10, 5, 5), 0, 0));
			container.add(canzhuangjian, new GridBagConstraints(1, 1, 1, 1, 1.0, 0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
					new Insets(10, 5, 5, 10), 0, 0));

			container.add(new JLabel("拍照点记录表"), new GridBagConstraints(0, 2, 1, 1, 0,
					0, GridBagConstraints.EAST, GridBagConstraints.NONE,
					new Insets(10, 10, 5, 5), 0, 0));
			container.add(paizhaodian, new GridBagConstraints(1, 2, 1, 1, 1.0, 0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
					new Insets(10, 5, 5, 10), 0, 0));
		}

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		container.add(buttonPanel, new GridBagConstraints(0, 5, 3, 1, 1.0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0));
		buttonPanel.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0,
				0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 0, 5, 5), 0, 0));
		okButton.setPreferredSize(new Dimension(80, 23));
		okButton.setMinimumSize(new Dimension(80, 23));
		okButton.setMaximumSize(new Dimension(80, 23));
		buttonPanel.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 5, 5), 0, 0));
		cancelButton.setPreferredSize(new Dimension(80, 23));
		cancelButton.setMinimumSize(new Dimension(80, 23));
		cancelButton.setMaximumSize(new Dimension(80, 23));
		buttonPanel.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 5, 0), 0, 0));

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					okProcess();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "另存为模板失败！");
					e1.printStackTrace();
				}
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelProcess();
			}
		});

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int) (dimension.getWidth() - 500) / 2,
				(int) (dimension.getHeight() - 130) / 2, 500, 230);
		setVisible(true);
	}

	private void okProcess() throws Exception {
		String type = techElement.attributeValue("technicsType");
		String templetDirectory = null;
//		if (type.equals("装配工艺")) {
//			templetDirectory = WorkSpaceUtil.getAssembleTempletPath();
//		} else {
//			templetDirectory = WorkSpaceUtil.getPartTempletPath();
//		}

		for(int i=0;i<technicsTypes[1].length;i++){
			if(technicsTypes[1][i].equals(type)){
				templetDirectory = WorkSpaceUtil.getTempletPath(technicsTypes[0][i]);
			}
		}

		File file = new File(templetDirectory);
		File[] array = file.listFiles();
		String[] stringArray = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			stringArray[i] = array[i].getName();
		}
		String templetName = nameField.getText().trim();
		if (templetName.equals("")) {
			JOptionPane.showMessageDialog(this, "模板名称不能为空！");
			return;
		}
		for (int index = 0; index < stringArray.length; index++) {
			if (stringArray[index].equals(templetName)) {
				int i = JOptionPane.showConfirmDialog(frame, "已存在的工艺模板名，是否覆盖？",
						"提示", JOptionPane.YES_NO_OPTION);
				if (i == JOptionPane.NO_OPTION) {
					return;
				}
			}
		}
		WorkSpaceUtil.copyTechnicsToTemplet(techElement, templetName,canzhuangjian.isSelected(),paizhaodian.isSelected());
		dispose();
		JOptionPane.showMessageDialog(null, "另存为模板成功！");
	}

	private void cancelProcess() {
		dispose();
	}
}