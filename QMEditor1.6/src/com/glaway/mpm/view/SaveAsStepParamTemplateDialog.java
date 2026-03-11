package com.glaway.mpm.view;

import com.glaway.mpm.controller.StepTemplateCopyHandler;
import com.glaway.mpm.util.ApacheZipUtil;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.ValueCache;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.glaway.mpm.wcIntf.TemplateIntf;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.*;

public class SaveAsStepParamTemplateDialog extends JDialog implements ActionListener {
	private JTextField textField;
	private String templateName;
	private JComboBox depts = null;
	private JComboBox fields = null;
	private JComboBox productTypes = null;
	private JLabel paramLabel = new JLabel("_参数化工序");

	private JButton okButton = new JButton("确定");
	private JButton cancelButton = new JButton("取消");

	private NewTechnicsPart frame;
	private Element stepElement;

	public SaveAsStepParamTemplateDialog(NewTechnicsPart f, Element step) {

		super(f, "存为参数化工序模板", true);
		frame = f;
		stepElement = step;

		Container container = getContentPane();
		container.setLayout(new GridBagLayout());

		final JLabel label = new JLabel();
		label.setText("模板名称");
		textField = new JTextField();

		final JLabel deptLabel = new JLabel();
		deptLabel.setText("部门");
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
		depts = new JComboBox(allList.toArray());

		final JLabel fieldLabel = new JLabel();
		fieldLabel.setText("专业");
		fields = new JComboBox(ValueCache.typeValues);

		final JLabel pLabel = new JLabel();
		pLabel.setText("产品类型");
		//TODO 产品类型取值
		productTypes = new JComboBox(ValueCache.productTypes);

		container.add(label, new GridBagConstraints(0, 0, 1, 1, 0,
				0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(10, 10, 5, 5), 0, 0));
		container.add(textField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(10, 5, 5, 10), 0, 0));
		container.add(paramLabel, new GridBagConstraints(2, 0, 1, 1, 0.1, 0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(10, 0, 5, 0), 0, 0));
		container.add(deptLabel, new GridBagConstraints(0, 1, 1, 1, 0,
				0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(10, 10, 5, 5), 0, 0));
		container.add(depts, new GridBagConstraints(1, 1, 1, 1, 1.0, 0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(10, 5, 5, 10), 0, 0));
		container.add(fieldLabel, new GridBagConstraints(0, 2, 1, 1, 0,
				0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(10, 10, 5, 5), 0, 0));
		container.add(fields, new GridBagConstraints(1, 2, 1, 1, 1.0, 0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(10, 5, 5, 10), 0, 0));
		container.add(pLabel, new GridBagConstraints(0, 3, 1, 1, 0,
				0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(10, 10, 5, 5), 0, 0));
		container.add(productTypes, new GridBagConstraints(1, 3, 1, 1, 1.0, 0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(10, 5, 5, 10), 0, 0));

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

		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
	}

	public void showDialog() {
		setSize(500, 400);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static String toSemiangle(String src) {
		if (src == null)
			return null;
		if (src.length() == 0)
			return "";
		char[] c = src.toCharArray();
		for (int index = 0; index < c.length; index++) {
			if (c[index] == 12288) {
				c[index] = (char) 32;
			} else if (c[index] > 65280 && c[index] < 65375) {
				c[index] = (char) (c[index] - 65248);
			}
		}
		return String.valueOf(c);
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.trim().equals("确定")) {
			templateName = textField.getText();
			templateName = toSemiangle(templateName);

			if (templateName.trim().equals("")) {
				JOptionPane.showMessageDialog(this, "        您还没有输入信息！", "提示",
						JOptionPane.WARNING_MESSAGE);
			} else {
				templateName += "_参数化工序";
				okProcess();
			}
		}
		if (cmd.equals("取消")) {
			dispose();
		}
	}

	private void okProcess() {
		String dept = (String) depts.getSelectedItem();
		String field = (String) fields.getSelectedItem();
		String productType = (String) productTypes.getSelectedItem();

		String templetDirectory = WorkSpaceUtil.getProcedureTempletPath("Process_ProcedureParam");
		FileUtil.delAllFile(templetDirectory);
		File templateFile = null;
		try {
			if (templetDirectory != null) {
				String path = templetDirectory + "\\" + templateName;
				templateFile = new File(path);

				// 开始复制具体文件数据
				StepTemplateCopyHandler.createStepTemplate(path, templateName, stepElement);

				String result = TemplateIntf.uploadProcessParamTemplate(templateName, getTemplateByte(templateFile),dept,field,productType,"step");
				if ("".equals(result)) {
					JOptionPane.showMessageDialog(this, "工序参数化模板“" + templateName + "”保存成功");
				} else {
					JOptionPane.showMessageDialog(this, result);
				}
				dispose();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (templateFile != null) {
				try {
					WorkSpaceUtil.delete(templateFile);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			JOptionPane.showMessageDialog(this, "另存为参数化工序模板出现错误！", "提示",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public byte[] getTemplateByte(File template) {
		String path = template.getPath() + ".zip";
		ApacheZipUtil.compress(template, path);
		byte[] bytes = FileUtil.readFilePathToByte(path);
		return bytes;
	}
}
