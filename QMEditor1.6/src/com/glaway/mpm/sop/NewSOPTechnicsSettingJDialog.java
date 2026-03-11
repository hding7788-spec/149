package com.glaway.mpm.sop;

import com.glaway.mpm.qmIntf.template.TemplateSearchDialog;
import com.glaway.mpm.sop.intf.SopIntf;
import com.glaway.mpm.util.*;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import ext.casc.sop.constants.SopConstants;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.*;

public class NewSOPTechnicsSettingJDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private NewTechnicsPart frame;

	private JPanel panel = new JPanel();
	private JPanel panel0 = new JPanel();
	private JPanel panel2 = new JPanel();

	private JTextField technicsSOPNumberText = null;
	private JTextField name_Value = null;
	private JTextField specializedType_value = null;
	private JTextField proceduceName_value = null;
	private JTextField professionalCode_value = null;
	private JTextField GONGXUJIANHAO_value = null;
	private JComboBox operationJob_value = null;
	private JComboBox customArea_value = null;
	// private JComboBox parameters_value = null;
	private JTextField term_value = null;
	private JComboBox term_value2 = null;
	private JComboBox secret_value = null;
	private JTextField remark_value = null;
	private JButton okButton = new JButton("确定");
	private JButton cancelButton = new JButton("取消");
	private JButton browseTemplate = new JButton();

	private String technicsNumber;
	private String technicsName;
	private String technicsType = "SOP标准操作规程"; // 工艺类型
	private Document document;
	private Element partElement;
	public Element techElement = null;
	private Map<XWTreeNode, Document> connectObjects;
	// 多个工艺类别
	private String technicsCategory;
	// 多个工艺
	public String technicsPath;
	public String version;
	private String fileCode = "";
	private String[] secret_array = { "公开", "商密", "内部", "秘密", "机密" };
	String gongxujianhao = "";
	String professionalCode = "";
	String department = "";
	private static final String ID = "Rz/";

	public NewSOPTechnicsSettingJDialog(NewTechnicsPart parent, Element element, XWTreeNode node, String technicsCategory) {
		super(parent, true);
		frame = parent;
		partElement = element;
		this.technicsCategory = technicsCategory;
		technicsNumber = fileCode;
		initComponent();
		loadInitData();
		setEditable();
		initDialog();

	}

	private void setEditable() {
		technicsSOPNumberText.setEditable(false);
		name_Value.setEditable(true);
		specializedType_value.setEditable(false);
		proceduceName_value.setEditable(false);
		term_value.setEditable(true);
		term_value2.setEditable(true);
		remark_value.setEditable(true);
		okButton.setVisible(true);
		cancelButton.setVisible(true);
		professionalCode_value.setEditable(false);
		GONGXUJIANHAO_value.setEditable(false);
		setResizable(false);
	}

	private void initDialog() {
		setIconImage(frame.getIconImage());
		this.setTitle("新建工艺");
		setTitle("新建SOP");
		setVisible(true);
	}

	private void initComponent() {
		Container container = getContentPane();
		panel0.setLayout(new BorderLayout());
		panel.setLayout(new GridBagLayout());
		JScrollPane scrollPane = new JScrollPane(panel0);
		panel0.add(panel, BorderLayout.CENTER);
		container.add(scrollPane);
		panel0.add(panel2, BorderLayout.SOUTH);
		// 设置网格布局管理器参数
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 5, 0, 0);
		gridBagConstraints.gridwidth = 2;

		// SOP第一行：工艺文件编号
		JLabel technicsSOPNumberLabel = new JLabel("SOP文件编号");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		panel.add(technicsSOPNumberLabel, gridBagConstraints);
		technicsSOPNumberText = new JTextField();
		technicsSOPNumberText.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets = new Insets(5, 5, 0, 50);
		panel.add(technicsSOPNumberText, gridBagConstraints);

		// 第二行：文件名称
		JLabel name_label = new JLabel("名称");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 1;
		panel.add(name_label, gridBagConstraints);
		name_Value = new JTextField();
		name_Value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(name_Value, gridBagConstraints);

		// 第三行：专业类别
		final JLabel specializedType_label = new JLabel("专业类别");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 1;
		panel.add(specializedType_label, gridBagConstraints);
		specializedType_value = new JTextField();
		specializedType_value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(specializedType_value, gridBagConstraints);

		// 第四行：工序名称
		final JLabel proceduceName_label = new JLabel("工序名称");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 1;
		panel.add(proceduceName_label, gridBagConstraints);
		proceduceName_value = new JTextField();
		proceduceName_value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(proceduceName_value, gridBagConstraints);

		// 第五行：专业代号
		final JLabel professionalCode_label = new JLabel("专业代号");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 1;
		panel.add(professionalCode_label, gridBagConstraints);
		professionalCode_value = new JTextField();
		professionalCode_value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(professionalCode_value, gridBagConstraints);

		// 第六行：工序简号
		final JLabel GONGXUJIANHAO_lable = new JLabel("工序简号");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridwidth = 1;
		panel.add(GONGXUJIANHAO_lable, gridBagConstraints);
		GONGXUJIANHAO_value = new JTextField();
		GONGXUJIANHAO_value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(GONGXUJIANHAO_value, gridBagConstraints);

		// 第五行：操作岗位
		final JLabel operationJob_label = new JLabel("操作岗位");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.gridwidth = 1;
		panel.add(operationJob_label, gridBagConstraints);

		operationJob_value = new JComboBox();
		operationJob_value.setPreferredSize(new Dimension(150, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(operationJob_value, gridBagConstraints);

		// 第六行：定制区域
		final JLabel customArea_label = new JLabel("定制区域");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.gridwidth = 1;
		panel.add(customArea_label, gridBagConstraints);

		customArea_value = new JComboBox();
		customArea_value.setPreferredSize(new Dimension(150, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(customArea_value, gridBagConstraints);

		// 第七行：参数项目
		// final JLabel Parameters_label = new JLabel("参数项目");
		// gridBagConstraints.gridx = 0;
		// gridBagConstraints.gridy = 6;
		// gridBagConstraints.gridwidth = 1;
		// panel.add(Parameters_label, gridBagConstraints);
		//
		// parameters_value = new JComboBox();
		// parameters_value.setPreferredSize(new Dimension(150, 23));
		// gridBagConstraints.gridwidth = 2;
		// gridBagConstraints.gridx = 1;
		// gridBagConstraints.gridy = 6;
		// gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		// panel.add(parameters_value, gridBagConstraints);

		// 第八行：密级
		final JLabel secret_label = new JLabel("密级");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.gridwidth = 1;
		panel.add(secret_label, gridBagConstraints);
		secret_value = new JComboBox(secret_array);
		secret_value.setPreferredSize(new Dimension(150, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		final String sop_secret = partElement.attributeValue("SECRET").trim();
		for (String string : secret_array) {
			if (string.equals(sop_secret)) {
				secret_value.setSelectedItem(sop_secret);
				break;
			}
		}
		secret_value.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox source = ((JComboBox) e.getSource());
				String secreted = (String) source.getItemAt(source.getSelectedIndex());
				if ("秘密".equals(secreted)) {
					term_value.setVisible(false);
					term_value.setText(null);
					term_value2.removeAllItems();
					term_value2.setVisible(true);
					String term = partElement.attributeValue("Term").trim();
					term_value2.addItem("");
					for (int i = 0; i < 10; i++) {
						String t = i + 1 + "";
						term_value2.addItem(t);
						if ((t).equals(term)) {
							term_value2.setSelectedItem(t);
						}
					}
				} else if ("机密".equals(secreted)) {
					term_value.setVisible(false);
					term_value.setText(null);
					term_value2.removeAllItems();
					term_value2.setVisible(true);
					String term = partElement.attributeValue("Term").trim();
					term_value2.addItem("");
					for (int i = 0; i < 20; i++) {
						String t = i + 1 + "";
						term_value2.addItem(t);
						if ((t).equals(term)) {
							term_value2.setSelectedItem(t);
						}
					}
				} else {
					term_value2.setVisible(false);
					term_value2.removeAllItems();
					term_value.setVisible(true);
					term_value.setText(partElement.attributeValue("Term"));
				}
			}
		});
		panel.add(secret_value, gridBagConstraints);

		// 第九行：期限
		final JLabel term_label = new JLabel("期限");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.gridwidth = 1;
		panel.add(term_label, gridBagConstraints);
		term_value = new JTextField();
		term_value.setVisible(false);
		term_value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(term_value, gridBagConstraints);
		term_value2 = new JComboBox();
		term_value2.setVisible(false);
		term_value2.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(term_value2, gridBagConstraints);
		if ("公开".equals(sop_secret) || "商密".equals(sop_secret) || "内部".equals(sop_secret)) {
			term_value.setVisible(true);
			term_value.setText(partElement.attributeValue("Term"));
		} else if ("秘密".equals(sop_secret)) {
			term_value2.setVisible(true);
			String term = partElement.attributeValue("Term").trim();
			for (int i = 0; i < 10; i++) {
				String t = i + 1 + "";
				term_value2.addItem(t);
				if ((t).equals(term)) {
					term_value2.setSelectedItem(t);
				}
			}
		} else if ("机密".equals(sop_secret)) {
			term_value2.setVisible(true);
			String term = partElement.attributeValue("Term").trim();
			for (int i = 0; i < 20; i++) {
				String t = i + 1 + "";
				term_value2.addItem(t);
				if ((t).equals(term)) {
					term_value2.setSelectedItem(t);
				}
			}
		}

		// 第十行：备注
		final JLabel Remark_label = new JLabel("备注");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.gridwidth = 1;
		panel.add(Remark_label, gridBagConstraints);
		remark_value = new JTextField();
		remark_value.setPreferredSize(new Dimension(300, 46));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(remark_value, gridBagConstraints);

		//第十一行：从模板创建
		final JLabel label_3 = new JLabel();
		label_3.setText("从模板创建");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 19;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.insets = new Insets(5, 5, 0, 0);
		panel.add(label_3, gridBagConstraints);

		browseTemplate.setMaximumSize(new Dimension(70, 23));
		browseTemplate.setMinimumSize(new Dimension(70, 23));
		browseTemplate.setPreferredSize(new Dimension(70, 23));
		browseTemplate.setText("浏览");
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 19;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(browseTemplate, gridBagConstraints);
		browseTemplate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (technicsSOPNumberText.getText() == null || "".equals(technicsSOPNumberText.getText())) {
					JOptionPane.showMessageDialog(frame, "SOP文件编号不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if (name_Value.getText() == null || "".equals(name_Value.getText())) {
					JOptionPane.showMessageDialog(frame, "SOP文件名称不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if (specializedType_value.getText() == null || "".equals(specializedType_value.getText())) {
					JOptionPane.showMessageDialog(frame, "SOP文件专业类别不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if (proceduceName_value.getText() == null || "".equals(proceduceName_value.getText())) {
					JOptionPane.showMessageDialog(frame, "SOP文件工序名称不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if (String.valueOf(secret_value.getSelectedItem()) == null || "".equals(String.valueOf(secret_value.getSelectedItem()))) {
					JOptionPane.showMessageDialog(frame, "SOP文件密级不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if ("机密".equals(String.valueOf(secret_value.getSelectedItem())) || "秘密".equals(String.valueOf(secret_value.getSelectedItem()))) {
					if (term_value2.getSelectedItem() == null || "".equals(term_value2.getSelectedItem())) {
						JOptionPane.showMessageDialog(frame, "SOP文件期限不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
				if (String.valueOf(operationJob_value.getSelectedItem()) == null || "".equals(String.valueOf(operationJob_value.getSelectedItem()))) {
					JOptionPane.showMessageDialog(frame, "SOP文件操作岗位不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if (String.valueOf(customArea_value.getSelectedItem()) == null || "".equals(String.valueOf(customArea_value.getSelectedItem()))) {
					JOptionPane.showMessageDialog(frame, "SOP文件定制区域不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				Enumeration childs = frame.xwPartTreePanel.getSelectedTreeNode().children();
				if (childs.hasMoreElements()) {
					Object o = childs.nextElement();
					if (o != null) {
						JOptionPane.showMessageDialog(frame, "一个体系BOM下只能创建一份SOP工艺！", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
				String name = null;
				try {
					String templatePath = WorkSpaceUtil.getTempletRootPath();
					System.out.println("从模板创建====参数路径=======" + templatePath);
					System.out.println("从模板创建====technicsType=======" + technicsType);
					String s = "Process_SOPTemplate";
					// 编号
					technicsNumber = TechnicsIntf.genTechnicsNumber();
					if("".equals(technicsNumber)){
						JOptionPane.showMessageDialog(frame, "工艺文件流水号生成失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					String pplanNumber = technicsSOPNumberText.getText();
					if (technicsName == null || "".equals(technicsName)) {
						technicsName = name_Value.getText()+"("+pplanNumber+")";
					}
					String technicsPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);

					if(technicsPath!=null){
						JOptionPane.showMessageDialog(frame, "工艺文件编号【"+technicsNumber.trim()+"】的工艺文件已经存在！", "提示",JOptionPane.INFORMATION_MESSAGE);
						return ;
					}
					TemplateSearchDialog dialog = new TemplateSearchDialog(templatePath, s, frame);
					Vector templates = dialog.showDialog();
					if (templates != null) {
						String category = NewSOPTechnicsSettingJDialog.this.technicsCategory;
						String partNumber = XmlUtility.getAttributeValue(partElement, "partNumber");
						String productNumber = BomXMLUtil.getProduct(partElement.getDocument()).attributeValue("productNumber");
						name = WorkSpaceUtil.createTechnicsDirectory(technicsNumber, technicsName, technicsType, partNumber, productNumber, category);
						System.out.println("新建工艺包路径===========" + name);
						String fileName = new StringBuffer().append(WorkSpaceUtil.getTechnicsConversion(technicsNumber,
								technicsName, technicsType, partNumber, productNumber)).append(".xml").toString();
						File newFile = new File(name + File.separator + fileName);
						// 本地模板逻辑
						String templateName = null;
						if (templates.size() == 1) {
							templateName = (String) templates.get(0);
							if (templateName.toLowerCase().endsWith(".zip")) {templateName = templateName.substring(0, templateName.length() - 4);
							}
							String templateXMLPath = "";
							if(templateName.contains("@@")){
								String[] ts = templateName.split("@@");
								templateXMLPath = WorkSpaceUtil.getTempletRootPath() + "\\" + s + "\\" + ts[1];
							}else{
								templateXMLPath = WorkSpaceUtil.getTempletRootPath() + "\\" + s + "\\" + templateName;
							}
							System.out.println("templateXMLPath=========" + templateXMLPath);
							FilesUtil.copyDirectiory(templateXMLPath, name);
						}
						// PDS服务模板逻辑
						else if (templates.size() == 2) {
							templateName = (String) templates.get(0);
							if (templateName.toLowerCase().endsWith(".xml")) {
								templateName = templateName.substring(0, templateName.length() - 4);
							}
							byte[] data = (byte[]) templates.get(1);
							TechnicsReleaseUtil.unZip(data, name);
						}
						// 修改内容
						System.out.println("templateName========" + templateName);
						if (templateName != null) {
							if(templateName.contains("@@")){
								String[] ts = templateName.split("@@");
								templateName = ts[1];
							}
							File f = new File(name + File.separator + templateName + ".xml");
							if (f.exists()) {
								if (techElement == null) {
									techElement = XmlUtility.createTechnics();
								}
								Document doc = XmlUtility.getDocument(f);
								techElement = XmlUtility.getTechnicsElement(doc);
								f.delete();
								generateTechDocument(partElement,techElement);
								setIBAAttribute(techElement);
								XmlUtility.setAttributeValue(techElement, "secretLevel", String.valueOf(secret_value.getSelectedItem()));
								XmlUtility.setAttributeValue(techElement, "technicsCategory", category);
								XmlUtility.setAttributeValue(techElement, "version", "");
								XmlUtility.saveDocument(doc, newFile);
								document = doc;
							} else {
								File dir = new File(name);
								if (dir.exists())
									WorkSpaceUtil.delete(dir);
							}
						}
						dispose();
					}
				} catch (Exception ee) {
					ee.printStackTrace();
					JOptionPane.showMessageDialog(NewSOPTechnicsSettingJDialog.this, "创建SOP工艺出现错误！", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					File dir = new File(name);
					if (dir.exists()) {
						try {
							WorkSpaceUtil.delete(dir);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					dispose();
				}
			}
		});

		// 按钮面板
		panel2.setLayout(new GridBagLayout());
		panel2.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		okButton.setPreferredSize(new Dimension(70, 23));
		okButton.setMinimumSize(new Dimension(70, 23));
		okButton.setMaximumSize(new Dimension(70, 23));
		panel2.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 15, 15), 0, 0));
		cancelButton.setPreferredSize(new Dimension(70, 23));
		cancelButton.setMinimumSize(new Dimension(70, 23));
		cancelButton.setMaximumSize(new Dimension(70, 23));
		panel2.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 15, 15), 0, 0));

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String category = NewSOPTechnicsSettingJDialog.this.technicsCategory;
					if (technicsSOPNumberText.getText() == null || "".equals(technicsSOPNumberText.getText())) {
						JOptionPane.showMessageDialog(frame, "SOP文件编号不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					if (name_Value.getText() == null || "".equals(name_Value.getText())) {
						JOptionPane.showMessageDialog(frame, "SOP文件名称不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					if (specializedType_value.getText() == null || "".equals(specializedType_value.getText())) {
						JOptionPane.showMessageDialog(frame, "SOP文件专业类别不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					if (proceduceName_value.getText() == null || "".equals(proceduceName_value.getText())) {
						JOptionPane.showMessageDialog(frame, "SOP文件工序名称不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					if (String.valueOf(secret_value.getSelectedItem()) == null || "".equals(String.valueOf(secret_value.getSelectedItem()))) {
						JOptionPane.showMessageDialog(frame, "SOP文件密级不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					if ("机密".equals(String.valueOf(secret_value.getSelectedItem())) || "秘密".equals(String.valueOf(secret_value.getSelectedItem()))) {
						if (term_value2.getSelectedItem() == null || "".equals(term_value2.getSelectedItem())) {
							JOptionPane.showMessageDialog(frame, "SOP文件期限不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
							return;
						}
					}
					if (String.valueOf(operationJob_value.getSelectedItem()) == null || "".equals(String.valueOf(operationJob_value.getSelectedItem()))) {
						JOptionPane.showMessageDialog(frame, "SOP文件操作岗位不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					if (String.valueOf(customArea_value.getSelectedItem()) == null || "".equals(String.valueOf(customArea_value.getSelectedItem()))) {
						JOptionPane.showMessageDialog(frame, "SOP文件定制区域不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					Enumeration childs = frame.xwPartTreePanel.getSelectedTreeNode().children();
					if (childs.hasMoreElements()) {
						Object o = childs.nextElement();
						if (o != null) {
							JOptionPane.showMessageDialog(frame, "一个体系BOM下只能创建一份SOP工艺！", "提示", JOptionPane.INFORMATION_MESSAGE);
							return;
						}
					}
					createProcess(category);
				} catch (Exception ee) {
					ee.printStackTrace();
					JOptionPane.showMessageDialog(frame, "创建工艺过程中出错！", "提示", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		Dimension dimension2 = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int) (dimension2.getWidth() - 500) / 2, (int) (dimension2.getHeight() - (625)) / 2, 500, 550);
	}

	private void loadInitData() {
		professionalCode = partElement.attributeValue("ProfessionalCode");
		gongxujianhao = partElement.attributeValue("GONGXUJIANHAO");
		department = partElement.attributeValue("ZZCJ");
		// 编号
		String technicsNumber = ID + partElement.attributeValue("partNumber");

		technicsSOPNumberText.setText(technicsNumber);
		// 名称
		String parentPartName = partElement.attributeValue("partName");
		name_Value.setText(parentPartName);
		// 专业类别
		specializedType_value.setText(partElement.attributeValue("SpecializedType"));
		// 工序名称
		proceduceName_value.setText(partElement.attributeValue("ProceduceName"));
		professionalCode_value.setText(partElement.attributeValue("ProfessionalCode"));
		GONGXUJIANHAO_value.setText(partElement.attributeValue("GONGXUJIANHAO"));
		// 操作岗位
		List<String> operationJob_list = new ArrayList<String>();
		operationJob_list.add("");
		operationJob_list.addAll(SopIntf.getSopResourceByType(SopConstants.SOP_TYPE_OPERATIONJOB));
		for (String czgw : operationJob_list) {
			operationJob_value.addItem(czgw);
		}
		operationJob_value.setSelectedItem("");
		// 定制区域
		List<String> customArea_list = new ArrayList<String>();
		customArea_list.add("");
		customArea_list.addAll(SopIntf.getSopResourceByType(SopConstants.SOP_TYPE_CUSTOMAREA));
		for (String dzqy : customArea_list) {
			customArea_value.addItem(dzqy);
		}
		customArea_value.setSelectedItem("");
		// 参数项目
		// List<String> parameters_list =
		// SopIntf.getAllCSXM(partElement.attributeValue("SpecializedType"));
		// for(String parameter : parameters_list){
		// parameters_value.addItem(parameter);
		// }
		// 密级
	}

	/**
	 */
	private void createProcess(String category) {

		if (techElement == null) {
			techElement = XmlUtility.createTechnics();
		}

		XmlUtility.setAttributeValue(techElement, "secretLevel", String.valueOf(secret_value.getSelectedItem()));
		XmlUtility.setAttributeValue(techElement, "technicsCategory", category);

		techElement = generateTechDocument(partElement, techElement);
		techElement = setIBAAttribute(techElement);
		System.out.println(techElement.attributeValue("technicsName"));
		document = writeDocument(techElement);

		dispose();
	}

	private Document writeDocument(Element techElement) {
		try {
			// 创建目录
			String dir = WorkSpaceUtil.createTechnicsDirectory(techElement.attributeValue("technicsNumber"), techElement.attributeValue("technicsName"), techElement.attributeValue("technicsType"),
					techElement.attributeValue("partNumber"), techElement.attributeValue("productNumber"), techElement.attributeValue("technicsCategory"));
			// 创建文件
			RecentSaveUtil.addRecent(technicsNumber);
			return WorkSpaceUtil.createTechnics(techElement, dir);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(frame, "创建文件时出现错误！");
		}
		return null;
	}

	/**
	 * 点击确定时，设置工艺规程的属性
	 *
	 * @param techElement
	 * @return
	 */
	private Element setIBAAttribute(Element techElement) {
		Element ibaElement = XmlUtility.getTechnicsIBAAttriElement(techElement);
		XmlUtility.deleteAllChildElements(ibaElement);

		XmlUtility.setAttributeValue(techElement, "SopNumber", technicsSOPNumberText.getText());
		ibaElement.add(this.createTechnicsIBAAttriElement("SOP编号", technicsSOPNumberText.getText()));

		XmlUtility.setAttributeValue(techElement, "SpecializedType", specializedType_value.getText());
		ibaElement.add(this.createTechnicsIBAAttriElement("专业类别", specializedType_value.getText()));

		XmlUtility.setAttributeValue(techElement, "ProceduceName", proceduceName_value.getText());
		ibaElement.add(this.createTechnicsIBAAttriElement("工序名称", proceduceName_value.getText()));

		XmlUtility.setAttributeValue(techElement, "OperationJob", operationJob_value.getSelectedItem().toString());
		ibaElement.add(this.createTechnicsIBAAttriElement("操作岗位", operationJob_value.getSelectedItem().toString()));

		XmlUtility.setAttributeValue(techElement, "CustomArea", customArea_value.getSelectedItem().toString());
		ibaElement.add(this.createTechnicsIBAAttriElement("定制区域", customArea_value.getSelectedItem().toString()));

		// XmlUtility.setAttributeValue(techElement, "Parameters",
		// parameters_value.getSelectedItem().toString());
		// ibaElement.add(this.createTechnicsIBAAttriElement("参数项目",
		// parameters_value.getSelectedItem().toString()));

		XmlUtility.setAttributeValue(techElement, "SECRET", secret_value.getSelectedItem().toString());
		ibaElement.add(this.createTechnicsIBAAttriElement("密级", secret_value.getSelectedItem().toString()));

		if ("秘密".equals(secret_value.getSelectedItem().toString()) || "机密".equals(secret_value.getSelectedItem().toString())) {
			XmlUtility.setAttributeValue(techElement, "Term", term_value2.getSelectedItem().toString());
			ibaElement.add(this.createTechnicsIBAAttriElement("期限", term_value2.getSelectedItem().toString()));
		} else {
			XmlUtility.setAttributeValue(techElement, "Term", term_value.getText());
			ibaElement.add(this.createTechnicsIBAAttriElement("期限", term_value.getText()));
		}

		XmlUtility.setAttributeValue(techElement, "ProfessionalCode", professionalCode);
		ibaElement.add(this.createTechnicsIBAAttriElement("专业代号", professionalCode));

		XmlUtility.setAttributeValue(techElement, "GONGXUJIANHAO", gongxujianhao);
		ibaElement.add(this.createTechnicsIBAAttriElement("工序简号", gongxujianhao));

		XmlUtility.setAttributeValue(techElement, "SopRemark", remark_value.getText());
		ibaElement.add(this.createTechnicsIBAAttriElement("备注", remark_value.getText()));

		return techElement;
	}

	private Element createTechnicsIBAAttriElement(String key, String value) {
		Element element = DocumentHelper.createElement("attribute");
		XmlUtility.setAttributeValue(element, "key", key);
		XmlUtility.setAttributeValue(element, "value", value);

		return element;
	}

	private Element generateTechDocument(Element partElement, Element techElement) {
		if (techElement == null) {
			techElement = XmlUtility.createTechnics();
		}
		String partNumber = partElement.attributeValue("partNumber");

		// 编号
		String pplanNumber = technicsSOPNumberText.getText();
		String pplanName = name_Value.getText();
		XmlUtility.setAttributeValue(techElement, "pplanNumber", pplanNumber);
		XmlUtility.setAttributeValue(techElement, "pplanName", pplanName);
		XmlUtility.setAttributeValue(techElement, "processTaskItemOid", this.frame.workItemOid);// 纪录工艺任务活动的OID

		if (technicsName == null || "".equals(technicsName)) {
			technicsName = name_Value.getText()+"("+pplanNumber+")";
		}
		XmlUtility.setAttributeValue(techElement, "technicsName", technicsName);

		if (technicsNumber == null || "".equals(technicsNumber)) {
			// 编号
			try {
				technicsNumber = TechnicsIntf.genTechnicsNumber();
			} catch(InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch(RemoteException e) {
				throw new RuntimeException(e);
			}
			if("".equals(technicsNumber)){
				JOptionPane.showMessageDialog(frame, "工艺文件流水号生成失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		XmlUtility.setAttributeValue(techElement, "technicsNumber", technicsNumber);
		XmlUtility.setAttributeValue(techElement, "technicsType", technicsType);
		XmlUtility.setAttributeValue(techElement, "unite", "common");
		XmlUtility.setAttributeValue(techElement, "treePath", BomXMLUtil.getPath(partElement));
		XmlUtility.setAttributeValue(techElement, "partNumber", partElement.attributeValue("partNumber"));// 设置部件图号
		XmlUtility.setAttributeValue(techElement, "partName", partElement.attributeValue("partName"));// 设置部件名称
		XmlUtility.setAttributeValue(techElement, "partOid", partElement.attributeValue("oid"));// 设置零部件oid
		XmlUtility.setAttributeValue(techElement, "partVersion", partElement.attributeValue("partVersion"));
		XmlUtility.setAttributeValue(techElement, "materialType", partElement.attributeValue("materialType"));
		XmlUtility.setAttributeValue(techElement, "workShop", partElement.attributeValue("workShop"));
		XmlUtility.setAttributeValue(techElement, "backupRate", partElement.attributeValue("backupRate"));
		XmlUtility.setAttributeValue(techElement, "maxBackupCount", partElement.attributeValue("maxBackupCount"));
		XmlUtility.setAttributeValue(techElement, "backupReason", partElement.attributeValue("backupReason"));
		XmlUtility.setAttributeValue(techElement, "isKey", partElement.attributeValue("isKey"));
		XmlUtility.setAttributeValue(techElement, "isSpecial", partElement.attributeValue("isSpecial"));
		XmlUtility.setAttributeValue(techElement, "partType", partElement.attributeValue("partType"));
		XmlUtility.setAttributeValue(techElement, "pbomLifecycle", partElement.attributeValue("pbomLifecycle"));

		XmlUtility.setAttributeValue(techElement, "eu_version", partElement.attributeValue("eu_version"));
		XmlUtility.setAttributeValue(techElement, "e_version", partElement.attributeValue("e_version"));
		XmlUtility.setAttributeValue(techElement, "partVersion", partElement.attributeValue("version"));

		XmlUtility.setAttributeValue(techElement, "isPartKey", partElement.attributeValue("isKey"));
		XmlUtility.setAttributeValue(techElement, "modifyTime", XmlUtility.getCurrentTime());

		XmlUtility.setAttributeValue(techElement, "occId", partElement.attributeValue("occId"));
		XmlUtility.setAttributeValue(techElement, "material", partElement.attributeValue("material"));
		XmlUtility.setAttributeValue(techElement, "dutu", partElement.attributeValue("dutu"));
		XmlUtility.setAttributeValue(techElement, "remark", partElement.attributeValue("remark"));
		XmlUtility.setAttributeValue(techElement, "useCount", partElement.attributeValue("useCount"));
		XmlUtility.setAttributeValue(techElement, "gysl", partElement.attributeValue("gysl"));

		XmlUtility.setAttributeValue(techElement, "XHPHCL", partElement.attributeValue("XHPHCL"));
		XmlUtility.setAttributeValue(techElement, "CSIZE", partElement.attributeValue("CSIZE"));
		XmlUtility.setAttributeValue(techElement, "JSTJBZH", partElement.attributeValue("JSTJBZH"));

		XmlUtility.setAttributeValue(techElement, "CMAT_UP", partElement.attributeValue("CMAT_UP"));
		XmlUtility.setAttributeValue(techElement, "CMAT_DOWN", partElement.attributeValue("CMAT_DOWN"));
		XmlUtility.setAttributeValue(techElement, "PZGGBZH", partElement.attributeValue("PZGGBZH"));
		XmlUtility.setAttributeValue(techElement, "JDDJ", partElement.attributeValue("JDDJ"));
		XmlUtility.setAttributeValue(techElement, "CLZT", partElement.attributeValue("CLZT"));
		XmlUtility.setAttributeValue(techElement, "ZLDJ", partElement.attributeValue("ZLDJ"));
		XmlUtility.setAttributeValue(techElement, "ZQCLBZH", partElement.attributeValue("ZQCLBZH"));
		XmlUtility.setAttributeValue(techElement, "ZQCLBZH", partElement.attributeValue("ZQCLBZH"));
		XmlUtility.setAttributeValue(techElement, "ZQCLMC", partElement.attributeValue("ZQCLMC"));
		XmlUtility.setAttributeValue(techElement, "XHPH", partElement.attributeValue("XHPH"));
		XmlUtility.setAttributeValue(techElement, "JSTJ", partElement.attributeValue("JSTJ"));
		XmlUtility.setAttributeValue(techElement, "JBCLMC", partElement.attributeValue("JBCLMC"));
		XmlUtility.setAttributeValue(techElement, "CMAT", partElement.attributeValue("CMAT"));

		XmlUtility.setAttributeValue(techElement, "MTYPE", partElement.attributeValue("MTYPE"));
		XmlUtility.setAttributeValue(techElement, "ZZCJ", partElement.attributeValue("ZZCJ"));
		XmlUtility.setAttributeValue(techElement, "FZCJ", partElement.attributeValue("FZCJ"));
		XmlUtility.setAttributeValue(techElement, "department", partElement.attributeValue("ZZCJ"));
		XmlUtility.setAttributeValue(techElement, "description", remark_value.getText());

		XmlUtility.setAttributeValue(techElement, "CINDEX", partElement.attributeValue("CINDEX"));

		// 设计资源库新增属性
		// start
		XmlUtility.setAttributeValue(techElement, "SHORTNAME", partElement.attributeValue("SHORTNAME"));
		XmlUtility.setAttributeValue(techElement, "STANDARDNUMBER", partElement.attributeValue("STANDARDNUMBER"));
		XmlUtility.setAttributeValue(techElement, "MECHANICALPROPERTYORHARDNESS", partElement.attributeValue("MECHANICALPROPERTYORHARDNESS"));
		XmlUtility.setAttributeValue(techElement, "SURFACETREATMENT", partElement.attributeValue("SURFACETREATMENT"));
		XmlUtility.setAttributeValue(techElement, "HEATTREATMENT", partElement.attributeValue("HEATTREATMENT"));
		XmlUtility.setAttributeValue(techElement, "PRODUCTFORM", partElement.attributeValue("PRODUCTFORM"));
		XmlUtility.setAttributeValue(techElement, "PRODUCTLEVEL", partElement.attributeValue("PRODUCTLEVEL"));
		XmlUtility.setAttributeValue(techElement, "PLATECSCREWFORM", partElement.attributeValue("PLATECSCREWFORM"));
		XmlUtility.setAttributeValue(techElement, "ISIMPORT", partElement.attributeValue("ISIMPORT"));
		XmlUtility.setAttributeValue(techElement, "SPECIALINSTRUCTION", partElement.attributeValue("SPECIALINSTRUCTION"));
		XmlUtility.setAttributeValue(techElement, "MEASUREUNIT", partElement.attributeValue("MEASUREUNIT"));
		XmlUtility.setAttributeValue(techElement, "TYPE", partElement.attributeValue("TYPE"));
		XmlUtility.setAttributeValue(techElement, "TYPESTANDARD", partElement.attributeValue("TYPESTANDARD"));
		XmlUtility.setAttributeValue(techElement, "QUALITYLEVEL", partElement.attributeValue("QUALITYLEVEL"));
		XmlUtility.setAttributeValue(techElement, "TOTALSTANDARD", partElement.attributeValue("TOTALSTANDARD"));
		XmlUtility.setAttributeValue(techElement, "DETAILSTANDARD", partElement.attributeValue("DETAILSTANDARD"));
		XmlUtility.setAttributeValue(techElement, "PACKAGINGFORM", partElement.attributeValue("PACKAGINGFORM"));
		XmlUtility.setAttributeValue(techElement, "OUTLINESIZE", partElement.attributeValue("OUTLINESIZE"));
		XmlUtility.setAttributeValue(techElement, "SPECIALCONDITION", partElement.attributeValue("SPECIALCONDITION"));
		XmlUtility.setAttributeValue(techElement, "EXTRACONDITION", partElement.attributeValue("EXTRACONDITION"));
		XmlUtility.setAttributeValue(techElement, "MATTYPE", partElement.attributeValue("MATTYPE"));
		// end

		List list = UserUtil.getCurrentUserOid();
		if (list != null && list.size() == 3) {
			String creator = (String) list.get(0);
			String creatorOid = (String) list.get(1);
			String creatorDisplay = (String) list.get(2);
			XmlUtility.setAttributeValue(techElement, "creator", creator);
			XmlUtility.setAttributeValue(techElement, "creatorOid", creatorOid);
			XmlUtility.setAttributeValue(techElement, "creatorDisplay", creatorDisplay);
		}

		Element productElement = null;
		Element rootPart = null;
		try {
			productElement = BomXMLUtil.getProductMessage(partElement);
			rootPart = BomXMLUtil.getMainPart(BomXMLUtil.getProduct(partElement.getDocument()));
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(frame, "获得产品信息时出现错误！");
		}

		String newProductNumber = SopIntf.getProductNumberAndName(partNumber);
		if (newProductNumber != null && !newProductNumber.isEmpty()) {
			XmlUtility.setAttributeValue(techElement, "productNumber", newProductNumber);// 设置整件图号
			XmlUtility.setAttributeValue(techElement, "productName", newProductNumber);// 设置整件名称
		} else {
			XmlUtility.setAttributeValue(techElement, "productNumber", productElement.attributeValue("productNumber"));// 设置整件图号
			XmlUtility.setAttributeValue(techElement, "productName", productElement.attributeValue("productName"));// 设置整件名称
		}

		// 添加整件信息
		System.out.println("partNumber" + rootPart.attributeValue("partNumber"));
		XmlUtility.setAttributeValue(techElement, "parentPartNumber", rootPart.attributeValue("partNumber"));// 设置整件图号
		XmlUtility.setAttributeValue(techElement, "parentPartName", rootPart.attributeValue("partName"));// 设置整件名称
		XmlUtility.setAttributeValue(techElement, "parentPartOid", rootPart.attributeValue("oid"));// 设置整件名称
		// 信维二期新需求，材料名称和材料编号也需要从零部件出带过来
		String materialNumber = partElement.attributeValue("materialNumber");
		String materialName = partElement.attributeValue("materialName");
		if (materialNumber == null || materialNumber.isEmpty()) {
			materialNumber = "";
		}

		if (materialName == null || materialName.isEmpty()) {
			materialName = "";
		}
		if (materialNumber.trim().length() > 0 || materialName.trim().length() > 0)// 零部件中包含材料的信息
		{
			Element materialELement = XmlUtility.createMaterial();
			XmlUtility.setAttributeValue(materialELement, "materialNumber", materialNumber);// 设置材料编号
			XmlUtility.setAttributeValue(materialELement, "materialName", materialName);// 设置材料编号
			XmlUtility.addMaterial(techElement, materialELement);
		}

		// 信维二期新需求，判断零部件是否为关键件，是关键件，则工艺也为关键工艺
		String key = partElement.attributeValue("isKey");
		if (key == null) {
			key = "";
		}
		XmlUtility.setAttributeValue(techElement, "isKey", key);// 设置关键工艺
		String isSpecial = partElement.attributeValue("isSpecial");
		if (isSpecial == null) {
			isSpecial = "";
		}
		XmlUtility.setAttributeValue(techElement, "isSpecial", isSpecial);// 设置关键工艺

		XmlUtility.setAttributeValue(techElement, "lifecycle", "正在工作");

		return techElement;
	}

	public Document getTechDocument() {
		return document;
	}

	public String getTechnicsNumber() {
		return technicsNumber;
	}

	public String getTechnicsName() {
		return technicsName;
	}

	public String getTechnicsType() {
		return technicsType;
	}

	public Map<XWTreeNode, Document> getConnectObjects() {
		return connectObjects;
	}

	public void setConnectObjects(Map<XWTreeNode, Document> connectObjects) {
		this.connectObjects = connectObjects;
	}

	/**
	 * 将dialog屏幕居中显示
	 *
	 * @author chenyunlong
	 * @date 2013-6-13
	 * @param dialog
	 *
	 */
	public static void setMiddleOnScreenWithDialog(JDialog dialog) {
		int windowWidth = dialog.getWidth(); // 获得窗口宽
		int windowHeight = dialog.getHeight(); // 获得窗口高
		Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
		Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
		int screenWidth = screenSize.width; // 获取屏幕的宽
		int screenHeight = screenSize.height; // 获取屏幕的高
		dialog.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2);// 设置窗口居中显示
	}

}