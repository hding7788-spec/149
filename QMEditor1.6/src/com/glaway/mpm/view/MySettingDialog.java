package com.glaway.mpm.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.glaway.mpm.util.FileChooserTool;
import com.glaway.mpm.util.WorkSpaceUtil;

public class MySettingDialog extends JDialog {
	private JFrame parent = null;

	private JTextField t1 = new JTextField();
	private JTextField t2 = new JTextField();
	private JTextField t3 = new JTextField();
	private JTextField t4 = new JTextField();

	private JButton select1Jbutton = new JButton("选择");
	private JButton select2Jbutton = new JButton("选择");
	private JButton select3Jbutton = new JButton("选择");
	private JButton select4Jbutton = new JButton("选择");

	private JButton confirmJbutton = new JButton("确定");
	private JButton cancelJbutton = new JButton("取消");

	public MySettingDialog(JFrame f) {
		super(f, "我的设置", true);
		parent = f;
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();

		JLabel TECHNICS_REGULATIONS_PROCEDURE = new JLabel("工艺简图工具位置");
		JLabel TECHNICS_REGULATIONS_VIEW = new JLabel("中间模型工具位置");
		JLabel Creo_INSTALL_EXECUTE = new JLabel("可视化工具位置");
		JLabel Cortona3D_INSTALL_EXECUTE = new JLabel("装配动画工具位置");

		select1Jbutton.setMaximumSize(new Dimension(80, 23));
		select1Jbutton.setMinimumSize(new Dimension(80, 23));
		select1Jbutton.setPreferredSize(new Dimension(80, 23));
		select2Jbutton.setMaximumSize(new Dimension(80, 23));
		select2Jbutton.setMinimumSize(new Dimension(80, 23));
		select2Jbutton.setPreferredSize(new Dimension(80, 23));
		select3Jbutton.setMaximumSize(new Dimension(80, 23));
		select3Jbutton.setMinimumSize(new Dimension(80, 23));
		select3Jbutton.setPreferredSize(new Dimension(80, 23));
		select4Jbutton.setMaximumSize(new Dimension(80, 23));
		select4Jbutton.setMinimumSize(new Dimension(80, 23));
		select4Jbutton.setPreferredSize(new Dimension(80, 23));

		confirmJbutton.setMaximumSize(new Dimension(80, 23));
		confirmJbutton.setMinimumSize(new Dimension(80, 23));
		confirmJbutton.setPreferredSize(new Dimension(80, 23));
		cancelJbutton.setMaximumSize(new Dimension(80, 23));
		cancelJbutton.setMinimumSize(new Dimension(80, 23));
		cancelJbutton.setPreferredSize(new Dimension(80, 23));

		getContentPane().setLayout(new GridBagLayout());
		panel1.setLayout(new GridBagLayout());
		panel2.setLayout(new GridBagLayout());

		getContentPane().add(
				TECHNICS_REGULATIONS_PROCEDURE,
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
						new Insets(20, 10, 10, 10), 0, 0));
		getContentPane().add(
				t1,
				new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL,
						new Insets(10, 0, 10, 10), 0, 0));
		getContentPane().add(
				select1Jbutton,
				new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL,
						new Insets(10, 0, 10, 10), 0, 0));

		getContentPane().add(
				TECHNICS_REGULATIONS_VIEW,
				new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
						new Insets(10, 10, 10, 10), 0, 0));
		getContentPane().add(
				t2,
				new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL,
						new Insets(10, 0, 10, 10), 0, 0));
		getContentPane().add(
				select2Jbutton,
				new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL,
						new Insets(10, 0, 10, 10), 0, 0));

		getContentPane().add(
				Creo_INSTALL_EXECUTE,
				new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
						new Insets(10, 10, 10, 10), 0, 0));
		getContentPane().add(
				t3,
				new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL,
						new Insets(10, 0, 10, 10), 0, 0));
		getContentPane().add(
				select3Jbutton,
				new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL,
						new Insets(10, 0, 10, 10), 0, 0));

		getContentPane().add(
				Cortona3D_INSTALL_EXECUTE,
				new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
						new Insets(10, 10, 10, 10), 0, 0));
		getContentPane().add(
				t4,
				new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL,
						new Insets(10, 0, 10, 10), 0, 0));
		getContentPane().add(
				select4Jbutton,
				new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL,
						new Insets(10, 0, 10, 10), 0, 0));

		getContentPane().add(
				panel2,
				new GridBagConstraints(0, 4, 3, 1, 1.0, 0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL,
						new Insets(0, 10, 10, 10), 0, 0));

		panel2.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(10, 0, 10, 0), 0, 0));
		panel2.add(confirmJbutton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						10, 10, 10, 10), 0, 0));
		panel2.add(cancelJbutton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						10, 0, 10, 0), 0, 0));

		t1.setText(WorkSpaceUtil.mySettingMap
				.get(WorkSpaceUtil.TECHNICS_DIAGRAM_TOOL));
		t2.setText(WorkSpaceUtil.mySettingMap.get(WorkSpaceUtil.MID_MODEL_TOOL));
		t3.setText(WorkSpaceUtil.mySettingMap.get(WorkSpaceUtil.VIDEO_TOOL));
		t4.setText(WorkSpaceUtil.mySettingMap
				.get(WorkSpaceUtil.ASSEMBLAGE_CARTOON_TOOL));

		t1.setEnabled(false);
		t2.setEnabled(false);
		t3.setEnabled(false);
		t4.setEnabled(false);

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int) (dimension.getWidth() - 600) / 2,
				(int) (dimension.getHeight() - 260) / 2, 600, 260);

		select1Jbutton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String strFilePath = FileChooserTool
						.getFilePath(MySettingDialog.this);
				if (strFilePath != null) {
					t1.setText(strFilePath);
				}
			}
		});

		select2Jbutton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String strFilePath = FileChooserTool
						.getFilePath(MySettingDialog.this);
				if (strFilePath != null) {
					t2.setText(strFilePath);
				}
			}
		});

		select3Jbutton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String strFilePath = FileChooserTool
						.getFilePath(MySettingDialog.this);
				if (strFilePath != null) {
					t3.setText(strFilePath);
				}
			}
		});

		select4Jbutton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String strFilePath = FileChooserTool
						.getFilePath(MySettingDialog.this);
				if (strFilePath != null) {
					t4.setText(strFilePath);
				}
			}
		});

		confirmJbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File file = new File(WorkSpaceUtil.MYSETTING_FILENAME);
				try {
					// 判断属性文件是否存在,不存在则新建
					if (!file.exists())
						file.createNewFile();
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				java.io.FileWriter writer = null;
				try {
					// if (t1.getText().equals("")) {
					// JOptionPane.showMessageDialog(null, "工艺规程信息目录不能为空");
					// return;
					// }
					writer = new java.io.FileWriter(WorkSpaceUtil.MYSETTING_FILENAME);
					WorkSpaceUtil.mySettingMap.clear();
					writer.write(WorkSpaceUtil.TECHNICS_DIAGRAM_TOOL + t1.getText() + "\n");
					writer.write(WorkSpaceUtil.MID_MODEL_TOOL + t2.getText() + "\n");
					writer.write(WorkSpaceUtil.VIDEO_TOOL + t3.getText() + "\n");
					writer.write(WorkSpaceUtil.ASSEMBLAGE_CARTOON_TOOL + t4.getText() + "\n");
					writer.close();
					WorkSpaceUtil.setMySettingMap();
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "我的设置出现错误");
				} finally {
					dispose();
				}

			}
		});

		cancelJbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		setResizable(false);
		setVisible(true);
	}

}
