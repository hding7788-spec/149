package com.glaway.mpm.view;

import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.speciaword.component.CheckTextContentInterface;
import com.glaway.speciaword.component.EditorPane;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

public class TechnicsPaceJPanel_XW extends JPanel {
	private String startType = "";
	private JFrame frame;
	private Element paceElement;
	private XWTreeNode node;
	private JTextField paceNumberField = new JTextField();
	//private SpeCharPanel speCharPanel = new SpeCharPanel();
	final EditorPane stepContentPane = new EditorPane(null);
	private JCheckBox checkFlagBox = new JCheckBox();

	private NewEquipJPanel equipJPanel = null;
	private NewToolJPanel toolJPanel = null;
	private NewKnifeToolPanel knifeToolPanel = null;
	private NewMaterialJPanel materialJPanel = null;
	private NewPartJPanel partJPanel = null;
	private NewDrawingJPanel drawingJPanel = null;
	private NewStandardDashboardJPanel standardDashboardJPanel = null;
	private NewUnStandardDashboardJPanel unStandardDashboardJPanel = null;
	private NewMeasureJPanel measureJPanel = null;

	private JTabbedPane tabbedPane = new JTabbedPane();

	private static String procedureType = "procedureType";
	private static final String part = "零部件";
	private Element techEle = null;

	public TechnicsPaceJPanel_XW(JFrame parent) {
		NewTechnicsPart.startAnimFrame.setHeaderMessage("加载工步");
		this.frame = parent;
		this.startType = com.glaway.mpm.EditorConfig.startType;
		partJPanel = new NewPartJPanel(this,frame);
		equipJPanel = new NewEquipJPanel(this, frame);
		measureJPanel = new NewMeasureJPanel(this,frame);
		toolJPanel = new NewToolJPanel(this, frame);
		knifeToolPanel = new NewKnifeToolPanel(this,frame);
		materialJPanel = new NewMaterialJPanel(this, frame);
		drawingJPanel = new NewDrawingJPanel(this, frame);
		standardDashboardJPanel = new NewStandardDashboardJPanel(this, frame);
		unStandardDashboardJPanel = new NewUnStandardDashboardJPanel(this, frame);

		setLayout(new GridBagLayout());

		JLabel label = new JLabel("*工步号");
		label.setMaximumSize(new Dimension(100, 23));
		label.setMinimumSize(new Dimension(100, 23));
		label.setPreferredSize(new Dimension(100, 23));
		label.setHorizontalAlignment(4);
		GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.anchor = 18;
		gridBagConstraints_4.gridx = 0;
		gridBagConstraints_4.gridy = 0;
		gridBagConstraints_4.insets = new Insets(5, 10, 0, 0);
		add(label, gridBagConstraints_4);

		GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.fill = 2;
		gridBagConstraints_10.anchor = 18;
		gridBagConstraints_10.weightx = 1.0D;
		gridBagConstraints_10.gridx = 1;
		gridBagConstraints_10.gridy = 0;
		gridBagConstraints_10.insets = new Insets(5, 5, 5, 5);
		add(this.paceNumberField, gridBagConstraints_10);

		GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.fill = 2;
		gridBagConstraints_16.weightx = 1.0D;
		gridBagConstraints_16.anchor = 12;
		gridBagConstraints_16.gridx = 2;
		gridBagConstraints_16.gridy = 0;
		gridBagConstraints_16.insets = new Insets(5, 0, 5, 5);
		this.checkFlagBox.setText("是否为检验工步");
		add(this.checkFlagBox, gridBagConstraints_16);

		JPanel panel0 = new JPanel();
		panel0.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.fill = 1;
		gridBagConstraints_7.anchor = 18;
		gridBagConstraints_7.weighty = 1.0D;
		gridBagConstraints_7.weightx = 1.0D;
		gridBagConstraints_7.gridx = 0;
		gridBagConstraints_7.gridy = 1;
		gridBagConstraints_7.gridwidth = 3;
		gridBagConstraints_7.insets = new Insets(0, 5, 5, 5);
		add(panel0, gridBagConstraints_7);

		JLabel label_5 = new JLabel("工步内容描述");
		label_5.setMaximumSize(new Dimension(100, 23));
		label_5.setMinimumSize(new Dimension(100, 23));
		label_5.setPreferredSize(new Dimension(100, 23));
		label_5.setHorizontalAlignment(4);
		GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.anchor = 18;
		gridBagConstraints_11.gridx = 0;
		gridBagConstraints_11.gridy = 0;
		gridBagConstraints_11.insets = new Insets(0, 5, 5, 5);
		panel0.add(label_5, gridBagConstraints_11);

		GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.fill = 1;
		gridBagConstraints_12.anchor = 12;
		gridBagConstraints_12.weighty = 1.0D;
		gridBagConstraints_12.weightx = 1.0D;
		gridBagConstraints_12.gridx = 1;
		gridBagConstraints_12.gridy = 0;
		gridBagConstraints_12.insets = new Insets(0, 0, 5, 5);
		panel0.add(this.stepContentPane, gridBagConstraints_12);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.fill = 1;
		gridBagConstraints_15.anchor = 12;
		gridBagConstraints_15.weighty = 1.0D;
		gridBagConstraints_15.weightx = 1.0D;
		gridBagConstraints_15.gridx = 1;
		gridBagConstraints_15.gridy = 2;
		gridBagConstraints_15.gridwidth = 2;
		gridBagConstraints_15.insets = new Insets(0, 0, 5, 5);
		add(panel, gridBagConstraints_15);

		panel.add(this.tabbedPane, "Center");
			this.tabbedPane.addTab("设备", this.equipJPanel);
			this.tabbedPane.addTab("工装", this.toolJPanel);
			this.tabbedPane.addTab("材料", this.materialJPanel);
			this.tabbedPane.addTab("参装件", this.partJPanel);
			this.tabbedPane.addTab("简图", this.drawingJPanel);
			this.tabbedPane.addTab("刀具", this.knifeToolPanel);
			this.tabbedPane.addTab("标准仪器仪表", this.standardDashboardJPanel);
			this.tabbedPane.addTab("非标准仪器仪表", this.unStandardDashboardJPanel);
			this.tabbedPane.addTab("量具", this.measureJPanel);

		JPanel panel_6 = new JPanel();
		panel_6.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.fill = 2;
		gridBagConstraints_9.anchor = 18;
		gridBagConstraints_9.weightx = 1.0D;
		gridBagConstraints_9.gridx = 0;
		gridBagConstraints_9.gridy = 3;
		gridBagConstraints_9.gridwidth = 3;
		gridBagConstraints_9.insets = new Insets(0, 5, 5, 5);
		add(panel_6, gridBagConstraints_9);

		JButton button_2 = new JButton("保存");
		button_2.setPreferredSize(new Dimension(80, 23));
		button_2.setMinimumSize(new Dimension(80, 23));
		button_2.setMaximumSize(new Dimension(80, 23));
		GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.anchor = 14;
		gridBagConstraints_1.gridx = 1;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.insets = new Insets(0, 0, 5, 5);
		panel_6.add(button_2, gridBagConstraints_1);
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Element newone = TechnicsPaceJPanel_XW.this.getElement();

					NewTechnicsPart np = (NewTechnicsPart) TechnicsPaceJPanel_XW.this.frame;
					np.saveProcess(TechnicsPaceJPanel_XW.this.paceElement);
					((NewTechnicsPart) TechnicsPaceJPanel_XW.this.frame).repaintTree();
				} catch (Exception ee) {
					ee.printStackTrace();
					JOptionPane.showMessageDialog(TechnicsPaceJPanel_XW.this.frame, "保存过程中出现错误！", "提示", 1);
				}
			}
		});
		JLabel label_7 = new JLabel();
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = 2;
		gridBagConstraints.anchor = 16;
		gridBagConstraints.weightx = 1.0D;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.ipady = 11;
		gridBagConstraints.insets = new Insets(0, 0, 5, 0);
		panel_6.add(label_7, gridBagConstraints);

		JLabel label_6 = new JLabel("关联资源数据");
		label_6.setHorizontalAlignment(4);
		GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.anchor = 12;
		gridBagConstraints_8.gridx = 0;
		gridBagConstraints_8.gridy = 2;
		gridBagConstraints_8.insets = new Insets(0, 5, 5, 5);
		add(label_6, gridBagConstraints_8);

		this.equipJPanel.setTabTitle();
		this.measureJPanel.setTabTitle();
		this.toolJPanel.setTabTitle();
		this.materialJPanel.setTabTitle();
		this.partJPanel.setTabTitle();
		this.drawingJPanel.setTabTitle();
		this.knifeToolPanel.setTabTitle();
		this.standardDashboardJPanel.setTabTitle();
		this.unStandardDashboardJPanel.setTabTitle();

		NewTechnicsPart.startAnimFrame.setHeaderMessage("加载工步完成");
	}

	public Element getElement() throws Exception {
		Element element = this.paceElement;

		XmlUtility.setAttributeValue(element, "stepNumber", this.paceNumberField.getText());
		XmlUtility.setAttributeValue(element, "stepName", "");
		XmlUtility.setAttributeValue(element, "workType", "");
		XmlUtility.setAttributeValue(element, "workShop", "");
		XmlUtility.setAttributeValue(element, "stepHour", "");
		XmlUtility.setAttributeValue(element, "isKey", "");

		if (this.checkFlagBox.isSelected())
			XmlUtility.setAttributeValue(element, "procedureType", procedureType);
		else
			XmlUtility.setAttributeValue(element, "procedureType", "");

		String stepContent = stepContentPane.getText();
		if(stepContent != null && !"".equals(stepContent)) {
			stepContent = stepContent.replaceAll("\\\\", "/");
			//当前选择的工艺对象
			if(techEle == null) {
				techEle = XmlUtility.getTechnicsElement(((NewTechnicsPart)frame).getCurrentTechnics());
			}
			String dir = WorkSpaceUtil.getTechnicsDirectory(techEle.attributeValue("technicsNumber"));
			dir = dir.replaceAll("\\\\", "/");
			stepContent = stepContent.replaceAll(dir, "WORKSPACE_PATH");
		} else {
			stepContent = "";
		}
		XmlUtility.setProcedureContent(element, stepContent);
		setLinkAtrributes(element);
		return element;
	}

	private void setLinkAtrributes(Element paceElement) {
		Element partElement = paceElement.element("parts");
		Vector part = this.partJPanel.getElements();
		for (Iterator it = part.iterator(); it.hasNext();) {
			partElement.add((Element) it.next());
		}

		Element equipElement = paceElement.element("equips");
		Vector equip = this.equipJPanel.getElements();
		for (Iterator it = equip.iterator(); it.hasNext();) {
			equipElement.add((Element) it.next());
		}

		Element measureElement = paceElement.element(XmlUtility.MEASURE_TAG);
		if(measureElement == null) {
			measureElement = paceElement.addElement(XmlUtility.MEASURE_TAG);
		} else {
			measureElement.elements().clear();
		}
		Vector measure = this.measureJPanel.getElements();
		for (Iterator it = measure.iterator(); it.hasNext();) {
			measureElement.add((Element) it.next());
		}

		// 标准仪器仪表
		Element sDashboardElement = paceElement.element(XmlUtility.SDASHBOARD_GROUP);
		if(sDashboardElement == null) {
			sDashboardElement = paceElement.addElement(XmlUtility.SDASHBOARD_GROUP);
		} else {
			sDashboardElement.elements().clear();
		}
		Vector<Element> sDashboard = this.standardDashboardJPanel.getElements();
		for (Iterator<Element> it = sDashboard.iterator(); it.hasNext();) {
			sDashboardElement.add(it.next());
		}

		// 非标准仪器仪表
		Element unsDashboardElement = paceElement.element(XmlUtility.UNSDASHBOARD_GROUP);
		if(unsDashboardElement == null) {
			unsDashboardElement = paceElement.addElement(XmlUtility.UNSDASHBOARD_GROUP);
		} else {
			unsDashboardElement.elements().clear();
		}
		Vector<Element> unsDashboard = this.unStandardDashboardJPanel.getElements();
		for (Iterator<Element> it = unsDashboard.iterator(); it.hasNext();) {
			unsDashboardElement.add(it.next());
		}

		Element toolElement = paceElement.element("tools");
		Vector tool = this.toolJPanel.getElements();
		for (Iterator it = tool.iterator(); it.hasNext();) {
			toolElement.add((Element) it.next());
		}

		Element knifeToolElement = paceElement.element("knifeTools");
		Vector knifetool = this.knifeToolPanel.getElements();
		for (Iterator it = tool.iterator(); it.hasNext();) {
			toolElement.add((Element) it.next());
		}

		Element materialElement = paceElement.element("materials");
		Vector material = this.materialJPanel.getElements();
		for (Iterator it = material.iterator(); it.hasNext();) {
			materialElement.add((Element) it.next());
		}

		Element imageElement = paceElement.element("images");
		Vector image = this.drawingJPanel.getElements();
		for (Iterator it = image.iterator(); it.hasNext();) {
			imageElement.add((Element) it.next());
		}
	}

	public void setUIValues(Element paceElement) {
		this.paceElement = paceElement;
		this.paceNumberField.setText(paceElement.attributeValue("stepNumber"));

		//当前选择的工艺对象
		if(techEle == null) {
			techEle = XmlUtility.getTechnicsElement(((NewTechnicsPart)frame).getCurrentTechnics());
		}
		stepContentPane.addCheckTextContentLengthListener(new CheckTextContentInterface() {

            @Override
            public String localImagePath() {
                // 返回图片存储位置
                // TODO
                return WorkSpaceUtil.getTechnicsDirectory(techEle.attributeValue("technicsNumber"));
            }

            @Override
            public boolean checkContentLength(String html) {
                return true;
            }
		});

		String stepContent = XmlUtility.getProcedureContent(paceElement);
		if(stepContent != null && !"".equals(stepContent)) {
			stepContent = stepContent.replaceAll("\\\\", "/");
			//当前选择的工艺对象
			String dir = WorkSpaceUtil.getTechnicsDirectory(techEle.attributeValue("technicsNumber"));
			dir = dir.replaceAll("\\\\", "/");
			stepContent = stepContent.replaceAll("WORKSPACE_PATH",dir);
		} else {
			stepContent = "";
		}
		this.stepContentPane.setText(stepContent);

		if (paceElement.attributeValue("procedureType").equals(procedureType))
			this.checkFlagBox.setSelected(true);
		setTableValues(paceElement);

		this.equipJPanel.setTabTitle();
		this.measureJPanel.setTabTitle();
		this.toolJPanel.setTabTitle();
		this.materialJPanel.setTabTitle();
		this.partJPanel.setTabTitle();
		this.drawingJPanel.setTabTitle();
		this.knifeToolPanel.setTabTitle();
		this.standardDashboardJPanel.setTabTitle();
		this.unStandardDashboardJPanel.setTabTitle();
	}

	private void setTableValues(Element paceElement) {
		Element partElement = paceElement.element("parts");
		Vector partVec = new Vector();
		for (Iterator it = partElement.elementIterator("QMPartInfo"); it
				.hasNext();) {
			partVec.add((Element) it.next());
		}
		this.partJPanel.setTableValues(partVec);

		Element equipElement = paceElement.element("equips");
		Vector equipVec = new Vector();
		for (Iterator it = equipElement.elementIterator("QMEquipmentInfo"); it
				.hasNext();) {
			equipVec.add((Element) it.next());
		}
		this.equipJPanel.setTableValues(equipVec);

		Element measureElement = paceElement.element(XmlUtility.MEASURE_GROUP);
		Vector measureVec = new Vector();
		for (Iterator it = measureElement.elementIterator(XmlUtility.MEASURE_TAG); it
				.hasNext();) {
			measureVec.add((Element) it.next());
		}
		this.measureJPanel.setTableValues(measureVec);

		// 标准仪器仪表
		Element sDashboardElement = paceElement.element(XmlUtility.SDASHBOARD_GROUP);
		Vector<Element> sDashboardVec = new Vector<Element>();
		if(sDashboardElement != null) {
			for (Iterator<Element> it = sDashboardElement.elementIterator(XmlUtility.SDASHBOARD_TAG); it.hasNext();) {
				sDashboardVec.add(it.next());
			}
		}
		this.standardDashboardJPanel.setTableValues(sDashboardVec);

		// 非标准仪器仪表
		Element unsDashboardElement = paceElement.element(XmlUtility.UNSDASHBOARD_GROUP);
		Vector<Element> unsDashboardVec = new Vector<Element>();
		if(unsDashboardElement != null) {
			for (Iterator<Element> it = unsDashboardElement.elementIterator(XmlUtility.UNSDASHBOARD_TAG); it.hasNext();) {
				unsDashboardVec.add(it.next());
			}
		}
		this.unStandardDashboardJPanel.setTableValues(unsDashboardVec);

		Element toolElement = paceElement.element("tools");
		Vector toolVec = new Vector();
		for (Iterator it = toolElement.elementIterator("QMToolInfo"); it
				.hasNext();) {
			toolVec.add((Element) it.next());
		}
		this.toolJPanel.setTableValues(toolVec);

		Element knifeToolElement = paceElement.element("knifeTools");
		Vector knifetoolVec = new Vector();
		for (Iterator it = knifeToolElement.elementIterator("QMKnifeToolInfo"); it
				.hasNext();) {
			knifetoolVec.add((Element) it.next());
		}
		this.knifeToolPanel.setTableValues(knifetoolVec);

		Element materialElement = paceElement.element("materials");
		Vector materialVec = new Vector();
		for (Iterator it = materialElement.elementIterator("QMMaterialInfo"); it
				.hasNext();) {
			materialVec.add((Element) it.next());
		}
		this.materialJPanel.setTableValues(materialVec);

		Element imageElement = paceElement.element("images");
		Vector imageVec = new Vector();
		for (Iterator it = imageElement.elementIterator("PDrawingInfo"); it
				.hasNext();) {
			imageVec.add((Element) it.next());
		}

		this.drawingJPanel.setTableValues(imageVec);
	}

	public void check() throws Exception {
		if (this.paceNumberField.getText().trim().equals(""))
			throw new Exception("工步号为必填项,请输入");
	}

	public void clearUI() {
		this.paceElement = null;
		this.node = null;
		this.paceNumberField.setText("");
		this.checkFlagBox.setSelected(false);
		this.stepContentPane.setText("");
		this.equipJPanel.clearTable();
		this.measureJPanel.clearTable();
		this.standardDashboardJPanel.clearTable();
		this.unStandardDashboardJPanel.clearTable();
		this.toolJPanel.clearTable();
		this.materialJPanel.clearTable();
		this.partJPanel.clearTable();
		this.drawingJPanel.clearTable();
	}

	public void setNode(XWTreeNode node) {
		this.node = node;
	}

	public XWTreeNode getNode() {
		return this.node;
	}

	public void setPartVisible(String technicsType) {
		if (technicsType.equals("零件工艺")) {
			int index = this.tabbedPane.indexOfTab("零部件");
			if (index != -1) {
				this.tabbedPane.remove(index);
			}

		} else if (this.tabbedPane.getTabCount() == 4) {
			this.tabbedPane.insertTab("零部件", null, this.partJPanel, null, 3);
		}
	}

	public NewLinkJPanel getPartLinkPanel() {
		return this.partJPanel;
	}

	public NewLinkJPanel getEquipLinkPanel() {
		return this.equipJPanel;
	}

	public NewMeasureJPanel getMeasureJPanel() {
		return measureJPanel;
	}

	public NewStandardDashboardJPanel getStandardDashboardJPanel() {
		return standardDashboardJPanel;
	}

	public NewUnStandardDashboardJPanel getUnStandardDashboardJPanel() {
		return unStandardDashboardJPanel;
	}

	public NewLinkJPanel getToolLinkPanel() {
		return this.toolJPanel;
	}

	public NewLinkJPanel getMaterialLinkPanel() {
		return this.materialJPanel;
	}

	public JTabbedPane getTabbedPane() {
		return this.tabbedPane;
	}

	public JFrame getFrame() {
		return this.frame;
	}
}
