package com.glaway.mpm.dataPackage.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.dom4j.Document;
import org.dom4j.Element;

import com.glaway.mpm.dataPackage.helper.DPMesParameterProcessor;
import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.parameter.designui.NewCommonParamTablePanel;
import com.glaway.mpm.parameter.designui.NewSpecialParamTablePanel;
import com.glaway.mpm.parameter.helper.MPMParameterProcessor;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.util.TechnicsReleaseUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTechnicsTreeObject;
import com.glaway.mpm.visual.log.VaLogger;

public class MesParameterMainPanel extends JPanel implements ActionListener {
	/**
	 * 主页面
	 */
	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(NewTechnicsPart.class);

	private JLabel productNumberLabel;
	private JTextField productNumberField;
	private JLabel technicNumberLabel;
	private JTextField technicNumberField;
	private JLabel procedureNumberLabel;
	private JTextField procedureNumberField;
	private JLabel procedureNameLabel;
	private JTextField procedureNameField;
	private JButton searchButton;
	// private JButton saveButton;
	private JSplitPane topAndBottom;
	private JSplitPane leftAndRight;
	private JPanel topPanel;
	private DPLeftTechnicTreePanel leftTechnicTreePanel;
	private DPRightTechnicInfoPanel rightTechnicInfoPanel;
	private Document document;

	private String productNumber;
	private String technicNumber;
	private String produreNumber;
	private String lukahao;
	private String jianyanyuan;
	private String caozuoyuan;
	private String gxPK;

	public MesParameterMainPanel() {
		initComponent();
		initLayout();
		initListener();
	}

	private void initComponent() {

		productNumberLabel = new JLabel("产品编号:");
		productNumberField = new JTextField();
		technicNumberLabel = new JLabel("工艺规程编号:");
		technicNumberField = new JTextField();
		procedureNumberLabel = new JLabel("当前工序号:");
		procedureNumberField = new JTextField();
		procedureNameLabel = new JLabel("工序名称:");
		procedureNameField = new JTextField();
		searchButton = new JButton("查询");
		// saveButton = new JButton("保存");

		technicNumberField.setText(DPMesParameterMainFrame.getPpNumber());
		productNumberField.setText(DPMesParameterMainFrame.getProductNumber());
		procedureNumberField.setText("");
		technicNumberField.setEditable(false);
		procedureNumberField.setEditable(false);
		procedureNameField.setEditable(false);

		// productNumberField.setDocument(new PlainDocument(){
		//
		// @Override
		// public void insertString(int offs, String str, AttributeSet a) throws
		// BadLocationException {
		// System.out.println(str);
		// if(str != null && str.length() > 0){
		// if(str.contains(":") && str.contains("|")){
		// str = str.substring(str.indexOf(":") + 1, str.indexOf("|"));
		// }
		// }
		// super.insertString(offs, str, a);
		// }
		//
		// });

		topPanel = new JPanel();
		leftTechnicTreePanel = new DPLeftTechnicTreePanel();
		rightTechnicInfoPanel = new DPRightTechnicInfoPanel(leftTechnicTreePanel);
		topPanel.setLayout(new VFlowLayout(0, 0, 0, true, true));

		leftAndRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		leftAndRight.setLeftComponent(leftTechnicTreePanel);
		leftAndRight.setRightComponent(rightTechnicInfoPanel);
		leftAndRight.setDividerLocation(300);

		topAndBottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		topAndBottom.setTopComponent(topPanel);
		topAndBottom.setBottomComponent(leftAndRight);
		topAndBottom.setDividerLocation(60);

		productNumberField.setPreferredSize(new Dimension(120, 30));
		technicNumberField.setPreferredSize(new Dimension(120, 30));
		procedureNumberField.setPreferredSize(new Dimension(120, 30));
		procedureNameField.setPreferredSize(new Dimension(120, 30));
		searchButton.setPreferredSize(new Dimension(100, 30));
		// saveButton.setPreferredSize(new Dimension(100, 30));

	}

	private void initLayout() {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();

		g.gridy = 0;
		g.gridx = 0;
		g.anchor = GridBagConstraints.EAST;
		g.insets = new Insets(0, 20, 0, 5);
		contentPanel.add(productNumberLabel, g);
		g.gridx = 1;
		g.anchor = GridBagConstraints.WEST;
		g.insets = new Insets(0, 5, 0, 20);
		contentPanel.add(productNumberField, g);
		g.gridx = 2;
		g.insets = new Insets(0, 20, 0, 5);
		g.anchor = GridBagConstraints.EAST;
		contentPanel.add(technicNumberLabel, g);
		g.gridx = 3;
		g.insets = new Insets(0, 5, 0, 20);
		g.anchor = GridBagConstraints.WEST;
		contentPanel.add(technicNumberField, g);
		g.gridx = 4;
		g.insets = new Insets(0, 20, 0, 5);
		g.anchor = GridBagConstraints.EAST;
		contentPanel.add(procedureNumberLabel, g);
		g.gridx = 5;
		g.insets = new Insets(0, 5, 0, 20);
		g.anchor = GridBagConstraints.WEST;
		contentPanel.add(procedureNumberField, g);
		g.gridx = 6;
		g.insets = new Insets(0, 20, 0, 5);
		g.anchor = GridBagConstraints.EAST;
		contentPanel.add(procedureNameLabel, g);
		g.gridx = 7;
		g.insets = new Insets(0, 5, 0, 20);
		g.anchor = GridBagConstraints.WEST;
		contentPanel.add(procedureNameField, g);
		g.gridx = 8;
		g.insets = new Insets(0, 20, 0, 20);
		g.anchor = GridBagConstraints.EAST;
		contentPanel.add(searchButton, g);
		// g.gridx = 9;
		// g.insets = new Insets(0, 5, 0, 20);
		// g.anchor = GridBagConstraints.WEST;
		// contentPanel.add(saveButton, g);

		setLayout(new BorderLayout());
		topPanel.add(contentPanel);
		add(topAndBottom);

	}

	public void initListener() {
		searchButton.addActionListener(this);
		// saveButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == searchButton) {
			productNumber = DPMesParameterMainFrame.getProductNumber();
			technicNumber = DPMesParameterMainFrame.getTechnicsNumber();
			Vector<Object> result = null;
			if (technicNumber.endsWith("_ZF")) {
				result = DPMesParameterProcessor.getTechnicsByTechnicNumber(technicNumber.substring(0, technicNumber.indexOf("_")));
			} else {
				result = DPMesParameterProcessor.getTechnicsByTechnicNumber(technicNumber);
			}

			if (result != null && result.size() > 0) {
				document = downloadData(result);
				// if(isProcedureExist(document, produreNumber)){
				String productNumber2 = "";
				if (technicNumber.endsWith("_ZF")) {
					productNumber2 = productNumber + "_ZF";
				} else {
					productNumber2 = productNumber;
				}
				leftTechnicTreePanel.loadMesTree(document, technicNumber, produreNumber, productNumber2, lukahao, gxPK, jianyanyuan, caozuoyuan);
				leftTechnicTreePanel.updateUI();
				// }else{
				// JOptionPane.showMessageDialog(null, "当前工艺不存在该工序！");
				// }
			} else {
				JOptionPane.showMessageDialog(null, "工艺文件不存在！");
			}
		}
		// if(obj == saveButton){
		// NewCommonParamTablePanel commonParamTablePanel =
		// rightTechnicInfoPanel.getCommonParamTablePanel();
		// List<NewSpecialParamTablePanel> specialParamTablePanelList =
		// rightTechnicInfoPanel.getSpecialParamTabbedPanel().getSpecialParamTablePanelList();
		// CmParamTableType commonParamTableType =
		// commonParamTablePanel.getParamTableType();
		// String commonTechnicsNumber =
		// commonParamTablePanel.getTechnicsNumber();
		// String commonObjNumber = commonParamTablePanel.getObjNumber();
		// String commonObjType = commonParamTablePanel.getObjType();
		// if(productNumber == null || "".equals(productNumber)){
		// JOptionPane.showMessageDialog(null, "产品编号不能为空！");
		// }else{
		// MPMParameterProcessor.deleteOldUUIDFiles(commonParamTablePanel.getOldUUIDFileNameList());
		// commonParamTablePanel.uploadImage();
		// MesParameterProcessor.saveMesParameters(commonParamTableType,
		// productNumber, commonTechnicsNumber, commonObjType, commonObjNumber,
		// lukahao, gxPK);
		// for(NewSpecialParamTablePanel specialParamTablePanel :
		// specialParamTablePanelList){
		// CmParamTableType specialParamTableType =
		// specialParamTablePanel.getParamTableType();
		// String specialTechnicsNumber =
		// specialParamTablePanel.getTechnicsNumber();
		// String specialObjNumber = specialParamTablePanel.getObjNumber();
		// String specialObjType = specialParamTablePanel.getObjType();
		// MPMParameterProcessor.deleteOldUUIDFiles(specialParamTablePanel.getOldUUIDFileNameList());
		// specialParamTablePanel.uploadImage();
		// MesParameterProcessor.saveMesParameters(specialParamTableType,
		// productNumber, specialTechnicsNumber, specialObjType,
		// specialObjNumber, lukahao, gxPK);
		// }
		// JOptionPane.showMessageDialog(null, "保存成功");
		// }
		//
		// }
	}

	public boolean isProcedureExist(Document document, String produreNumber) {
		XWTechnicsTreeObject technicTreeObject = new XWTechnicsTreeObject(document);
		List<Element> list = XmlUtility.getAllSteps(technicTreeObject.getTreeCellData());
		for (Element procedureElement : list) {
			String procedureNumber = procedureElement.attributeValue("stepNumber");
			if (procedureNumber.equals(produreNumber)) {
				return true;
			}
		}
		return false;
	}

	public Document downloadData(Vector<Object> result) {
		String fileName = (String) result.get(0);
		String filepath = "";
		Document doc = null;
		try {
			byte[] data = (byte[]) result.get(1);
			if ((data == null) || (data.length <= 0)) {

			}
			if (fileName.toLowerCase().endsWith(".zip")) {
				fileName = fileName.substring(0, fileName.length() - 4);
			}
			logger.debug("工艺编号========" + technicNumber);
			File f = new File(WorkSpaceUtil.getCommonTechnicsRootPath() + "\\" + fileName);
			logger.debug("校验是否存在========" + f);
			if (f.exists()) {
				WorkSpaceUtil.delete(f);
			}
			filepath = WorkSpaceUtil.createTechnicsDirectory(fileName);
			logger.debug("filepath=======" + filepath);
			TechnicsReleaseUtil.unZip(data, filepath);
			String technicsNumber = WorkSpaceUtil.getTechnicsNumber(fileName);
			doc = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(technicsNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return doc;
	}

	public DPLeftTechnicTreePanel getLeftTechnicTreePanel() {
		return leftTechnicTreePanel;
	}

	public DPRightTechnicInfoPanel getRightTechnicInfoPanel() {
		return rightTechnicInfoPanel;
	}

	public JPanel getTopPanel() {
		return topPanel;
	}

	public JTextField getProcedureNumberField() {
		return procedureNumberField;
	}

	public JLabel getProcedureNameLabel() {
		return procedureNameLabel;
	}

	public JTextField getProcedureNameField() {
		return procedureNameField;
	}

}
