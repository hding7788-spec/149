package com.glaway.mpm.mesParameter.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import org.dom4j.Document;
import org.dom4j.Element;

import com.glaway.mpm.mesParameter.helper.MesParameterProcessor;
import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.parameter.designui.NewCommonParamTablePanel;
import com.glaway.mpm.parameter.designui.NewSpecialParamTabbedPanel;
import com.glaway.mpm.parameter.designui.NewSpecialParamTablePanel;
import com.glaway.mpm.parameter.helper.MPMParameterProcessor;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTechnicsTreeObject;
import com.glaway.mpm.visual.log.VaLogger;

public class MesParameterMainPanel extends JPanel implements ActionListener{
	/**
	 * 主页面
	 */
	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(NewTechnicsPart.class);

	private MesParameterMainFrame frame;

	private JLabel lukahaoLabel;
	private JTextField lukahaoField;
	private JLabel productNumberLabel;
//	private JTextField productNumberField;
	private MultiComboBox processNumberBox;
	private JLabel groupLabel;
	private JComboBox groupComboBox;
	private JLabel technicNumberLabel;
	private JTextField technicNumberField;
	private JLabel procedureNumberLabel;
	private JTextField procedureNumberField;
	private JLabel procedureNameLabel;
	private JTextField procedureNameField;
	private JButton searchButton;
	private JButton saveButton;
	private JSplitPane topAndBottom;
	private JSplitPane leftAndRight;
	private JPanel topPanel;
	private LeftTechnicTreePanel leftTechnicTreePanel;
	private RightTechnicInfoPanel rightTechnicInfoPanel;
	private Document document;

	private String productNumber;
	private String technicNumber;
	private String fzTechnicNumber;
	private String fzVersion;
	private String produreNumber;
	private String lukahao;
	private String jianyanyuan;
	private String caozuoyuan;
	private String gxPK;
	private static List<String> valueList;
	private static List<String> defaultValueList;

	public MesParameterMainPanel(MesParameterMainFrame frame) {
		this.frame = frame;
		initComponent();
		initLayout();
		initListener();
	}

	private void initComponent(){

		lukahaoLabel = new JLabel("工艺过程卡号:");
		lukahaoField = new JTextField();
		productNumberLabel = new JLabel("过程编号:");
//		productNumberField = new JTextField();
		processNumberBox = new MultiComboBox(frame.getProcessNumberValues(), defaultValueList);
		groupLabel = new JLabel("分组信息:");
		groupComboBox = new JComboBox();
		groupComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				String str = (String) e.getItem();
				String key = (String) groupComboBox.getSelectedItem();
				if(key != null && !"".equals(key)){
					String value = rightTechnicInfoPanel.getProcessNumberMap().get(key);
					processNumberBox.setEditText(value);
					defaultValueList = leftTechnicTreePanel.getDefaultValueList(value);
					processNumberBox.setDefaultValue(defaultValueList);
				}else{
					processNumberBox.setEditText("");
					processNumberBox.setDefaultValue(null);
				}
			}
		});
		technicNumberLabel = new JLabel("工艺规程编号:");
		technicNumberField = new JTextField();
		procedureNumberLabel = new JLabel("当前工序号:");
		procedureNumberField = new JTextField();
		procedureNameLabel = new JLabel("工序名称:");
		procedureNameField = new JTextField();

		searchButton = new JButton("查询");
		saveButton = new JButton("保存");

		technicNumberField.setText(MesParameterMainFrame.getPpNumber());
		procedureNumberField.setText(MesParameterMainFrame.getProdureNumber());
		lukahaoField.setText(MesParameterMainFrame.getLukahao());
		technicNumberField.setEditable(false);
		procedureNumberField.setEditable(false);
		procedureNameField.setEditable(false);
		lukahaoField.setEditable(false);

//		productNumberField.setDocument(new PlainDocument() {
//
//			@Override
//			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
//				System.out.println(str);
//				if (str != null && str.length() > 0) {
//					if (str.contains(":") && str.contains("|")) {
//						str = str.substring(str.indexOf(":") + 1, str.indexOf("|"));
//					}
//				}
//				super.insertString(offs, str, a);
//			}
//
//		});
//		productNumberField.addKeyListener(new KeyListener() {
//			@Override
//			public void keyTyped(KeyEvent e) {
//			}
//			@Override
//			public void keyReleased(KeyEvent e) {
//			}
//			@Override
//			public void keyPressed(KeyEvent e) {
//				String value = "";
//				if(e.getKeyCode() == KeyEvent.VK_ENTER){
//					String str = productNumberField.getText();
//					if (str != null && str.length() > 0) {
//						if (str.contains(":") && str.contains("|")) {
//							str = str.substring(str.indexOf(":") + 1, str.indexOf("|"));
//							productNumberField.setText(str);
//						}
//					}
//					System.out.println(str);
//				}else{
//					value = value + e.getKeyChar();
//				}
//			}
//		});
//		productNumberField.addFocusListener(new FocusListener() {
//
//			@Override
//			public void focusLost(FocusEvent e) {
//				// TODO Auto-generated method stub
//				String str = productNumberField.getText();
//				if (str != null && str.length() > 0) {
//					if (str.contains(":") && str.contains("|")) {
//						str = str.substring(str.indexOf(":") + 1, str.indexOf("|"));
//						productNumberField.setText(str);
//					}
//				}
//
//			}
//
//			@Override
//			public void focusGained(FocusEvent e) {
//				// TODO Auto-generated method stub
//			}
//		});




		topPanel = new JPanel();
		leftTechnicTreePanel = new LeftTechnicTreePanel();
		rightTechnicInfoPanel = new RightTechnicInfoPanel(frame, leftTechnicTreePanel);
		topPanel.setLayout(new VFlowLayout(0, 0, 0, true, true));

		leftAndRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		leftAndRight.setLeftComponent(leftTechnicTreePanel);
		leftAndRight.setRightComponent(rightTechnicInfoPanel);
		leftAndRight.setDividerLocation(300);

		topAndBottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		topAndBottom.setTopComponent(topPanel);
		topAndBottom.setBottomComponent(leftAndRight);
		topAndBottom.setDividerLocation(60);

//		productNumberField.setPreferredSize(new Dimension(120, 30));
//		processNumberBox.setPreferredSize(new Dimension(80, 30));
		lukahaoField.setPreferredSize(new Dimension(80, 30));
		technicNumberField.setPreferredSize(new Dimension(80, 30));
		procedureNumberField.setPreferredSize(new Dimension(50, 30));
		procedureNameField.setPreferredSize(new Dimension(80, 30));
		searchButton.setPreferredSize(new Dimension(80, 30));
		saveButton.setPreferredSize(new Dimension(80, 30));
		groupComboBox.setPreferredSize(new Dimension(50, 30));

	}
	private void initLayout(){
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();

		g.gridy = 0;
		g.gridx = 0;
		g.anchor = GridBagConstraints.EAST;
		g.insets = new Insets(0, 0, 0, 5);
		contentPanel.add(lukahaoLabel, g);
		g.gridx = 1;
		g.anchor = GridBagConstraints.WEST;
		g.insets = new Insets(0, 5, 0, 10);
		contentPanel.add(lukahaoField, g);
		g.gridx = 2;
		g.anchor = GridBagConstraints.EAST;
		g.insets = new Insets(0, 10, 0, 5);
		contentPanel.add(productNumberLabel, g);
		g.gridx = 3;
		g.anchor = GridBagConstraints.WEST;
		g.insets = new Insets(0, 5, 0, 10);
		contentPanel.add(processNumberBox, g);
		g.gridx = 4;
		g.anchor = GridBagConstraints.WEST;
		g.insets = new Insets(0, 10, 0, 5);
		contentPanel.add(groupLabel, g);
		g.gridx = 5;
		g.anchor = GridBagConstraints.WEST;
		g.insets = new Insets(0, 5, 0, 10);
		contentPanel.add(groupComboBox, g);
		g.gridx = 6;
		g.insets = new Insets(0, 10, 0, 5);
		g.anchor = GridBagConstraints.EAST;
		contentPanel.add(technicNumberLabel, g);
		g.gridx = 7;
		g.insets = new Insets(0, 5, 0, 10);
		g.anchor = GridBagConstraints.WEST;
		contentPanel.add(technicNumberField, g);
		g.gridx = 8;
		g.insets = new Insets(0, 10, 0, 5);
		g.anchor = GridBagConstraints.EAST;
		contentPanel.add(procedureNumberLabel, g);
		g.gridx = 9;
		g.insets = new Insets(0, 5, 0, 10);
		g.anchor = GridBagConstraints.WEST;
		contentPanel.add(procedureNumberField, g);
		g.gridx = 10;
		g.insets = new Insets(0, 10, 0, 5);
		g.anchor = GridBagConstraints.EAST;
		contentPanel.add(procedureNameLabel, g);
		g.gridx = 11;
		g.insets = new Insets(0, 5, 0, 10);
		g.anchor = GridBagConstraints.WEST;
		contentPanel.add(procedureNameField, g);

		g.gridx = 12;
		g.insets = new Insets(0, 10, 0, 5);
		g.anchor = GridBagConstraints.EAST;
		contentPanel.add(searchButton, g);

		g.gridx = 13;
		g.insets = new Insets(0, 5, 0, 0);
		g.anchor = GridBagConstraints.WEST;
		contentPanel.add(saveButton, g);

		setLayout(new BorderLayout());
		topPanel.add(contentPanel);
		add(topAndBottom);

	}
	public void initMesTree() {
		technicNumber = MesParameterMainFrame.getTechnicNumber();
		fzTechnicNumber = MesParameterMainFrame.getFzTechnicNumber();
		fzVersion = MesParameterMainFrame.getFzVersion();
		produreNumber = procedureNumberField.getText();
		lukahao = MesParameterMainFrame.getLukahao();
		jianyanyuan = MesParameterMainFrame.getJianyanyuan();
		caozuoyuan = MesParameterMainFrame.getCaozuoyuan();
		gxPK = MesParameterMainFrame.getGxPK();
		Vector<Object> result = null;
		if(technicNumber.contains("_ZF")){
			result = MesParameterProcessor.getTechnicsByTechnicNumber(technicNumber.substring(0, technicNumber.indexOf("_")));
		}else{
			result = MesParameterProcessor.getTechnicsByTechnicNumber(technicNumber);
		}
		if(result != null && result.size() > 0){
			document = MesParameterProcessor.downloadData(result);
				String productNumber2 = "";
				if(technicNumber.endsWith("_ZF")){
					productNumber2 = productNumber + "_ZF";
				}else{
					productNumber2 = productNumber;
				}
				leftTechnicTreePanel.loadMesTree(document, technicNumber, fzTechnicNumber, fzVersion, produreNumber, productNumber2, lukahao, gxPK, jianyanyuan, caozuoyuan);
				leftTechnicTreePanel.updateUI();
		}else{
			JOptionPane.showMessageDialog(null, "工艺文件不存在！");
		}
	}
	public void initListener(){
		searchButton.addActionListener(this);
		saveButton.addActionListener(this);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj == searchButton){
//			long searchStart = System.currentTimeMillis();
////			productNumber = productNumberField.getText();
////			if(productNumber == null || "".equals(productNumber)){
////				JOptionPane.showMessageDialog(null, "产品编号不能为空！");
////				return;
////			}
//			technicNumber = MesParameterMainFrame.getTechnicNumber();
//			fzTechnicNumber = MesParameterMainFrame.getFzTechnicNumber();
//			fzVersion = MesParameterMainFrame.getFzVersion();
//			produreNumber = procedureNumberField.getText();
//			lukahao = MesParameterMainFrame.getLukahao();
//			jianyanyuan = MesParameterMainFrame.getJianyanyuan();
//			caozuoyuan = MesParameterMainFrame.getCaozuoyuan();
//			gxPK = MesParameterMainFrame.getGxPK();
//			Vector<Object> result = null;
//			long resultStart = System.currentTimeMillis();
//			if(technicNumber.contains("_ZF")){
//				result = MesParameterProcessor.getTechnicsByTechnicNumber(technicNumber.substring(0, technicNumber.indexOf("_")));
//			}else{
//				result = MesParameterProcessor.getTechnicsByTechnicNumber(technicNumber);
//			}
//			long resultEnd = System.currentTimeMillis();
//			System.out.println("SearchResult----Time----:" + (resultEnd - resultStart) + "ms");
//			if(result != null && result.size() > 0){
//				document = MesParameterProcessor.downloadData(result);
////				if(isProcedureExist(document, produreNumber)){
//					String productNumber2 = "";
//					if(technicNumber.endsWith("_ZF")){
//						productNumber2 = productNumber + "_ZF";
//					}else{
//						productNumber2 = productNumber;
//					}
//					leftTechnicTreePanel.loadMesTree(document, technicNumber, fzTechnicNumber, fzVersion, produreNumber, productNumber2, lukahao, gxPK, jianyanyuan, caozuoyuan);
//					leftTechnicTreePanel.updateUI();
////				}else{
////					JOptionPane.showMessageDialog(null, "当前工艺不存在该工序！");
////				}
//			}else{
//				JOptionPane.showMessageDialog(null, "工艺文件不存在！");
//			}
//			long searchEnd = System.currentTimeMillis();
//			System.out.println("SearchTotalTime   time：" + (searchEnd - searchStart) + "ms");
			rightTechnicInfoPanel.reloadTableValues();
		}
		if(obj == saveButton){
			String isZF = "N";
			if(technicNumber.endsWith("_ZF")){
				isZF = "Y";
			}else{
				isZF = "N";
			}
			String processNumber = processNumberBox.getTextFieldValue();
			if(processNumber == null || "".equals(processNumber)){
				JOptionPane.showMessageDialog(null, "过程编号不能为空！");
			}else{
				Map<String, String> paramsMap = null;
				Component comp = rightTechnicInfoPanel.getTabbedPane().getSelectedComponent();
				if(comp instanceof NewCommonParamTablePanel){
					NewCommonParamTablePanel commonParamTablePanel = (NewCommonParamTablePanel) comp;
					commonParamTablePanel.stopCellEditing();
					CmParamTableType commonParamTableType = commonParamTablePanel.getParamTableType();
					String commonTechnicsNumber = commonParamTablePanel.getTechnicsNumber();
					String commonObjNumber = commonParamTablePanel.getObjNumber();
					String commonObjType = commonParamTablePanel.getObjType();
					String bsoId = commonParamTablePanel.getbsoID();
					String version = commonParamTablePanel.getVersion();
					MPMParameterProcessor.deleteOldUUIDFiles(commonParamTablePanel.getOldUUIDFileNameList());
					commonParamTablePanel.uploadImage();
					paramsMap = new HashMap<String,String>();
					paramsMap.put("tableName", "mes" + commonParamTableType.getEnName());
					paramsMap.put("processNumber", processNumber);
					paramsMap.put("technicsNumber", commonTechnicsNumber);
					paramsMap.put("objType", commonObjType);
					paramsMap.put("objNumber", commonObjNumber);
					paramsMap.put("lukahao", lukahao);
					paramsMap.put("gxPK", gxPK);
					paramsMap.put("isZF", isZF);
					paramsMap.put("bsoId", bsoId);
					paramsMap.put("version", version);
					MesParameterProcessor.saveMesParameters(commonParamTableType, paramsMap);
					JOptionPane.showMessageDialog(null, "保存成功");
				}
				if(comp instanceof NewSpecialParamTabbedPanel){
					NewSpecialParamTabbedPanel specialTabbedPanel = (NewSpecialParamTabbedPanel) comp;
					Component component = specialTabbedPanel.getTabbedPane().getSelectedComponent();
					if(component instanceof NewSpecialParamTablePanel){
						NewSpecialParamTablePanel specialParamTablePanel = (NewSpecialParamTablePanel) component;
						specialParamTablePanel.stopCellEditing();
						CmParamTableType specialParamTableType = specialParamTablePanel.getParamTableType();
						String specialTechnicsNumber = specialParamTablePanel.getTechnicsNumber();
						String specialObjNumber = specialParamTablePanel.getObjNumber();
						String specialObjType = specialParamTablePanel.getObjType();
						String bsoId = specialParamTablePanel.getBsoID();
						String version = specialParamTablePanel.getVersion();
						MPMParameterProcessor.deleteOldUUIDFiles(specialParamTablePanel.getOldUUIDFileNameList());
						specialParamTablePanel.uploadImage();
						paramsMap = new HashMap<String,String>();
						paramsMap.put("tableName", "mes" + specialParamTableType.getEnName());
						paramsMap.put("processNumber", processNumber);
						paramsMap.put("technicsNumber", specialTechnicsNumber);
						paramsMap.put("objType", specialObjType);
						paramsMap.put("objNumber", specialObjNumber);
						paramsMap.put("lukahao", lukahao);
						paramsMap.put("gxPK", gxPK);
						paramsMap.put("isZF", isZF);
						paramsMap.put("bsoId", bsoId);
						paramsMap.put("version", version);
						MesParameterProcessor.saveMesParameters(specialParamTableType, paramsMap);
						JOptionPane.showMessageDialog(null, "保存成功");
					}
			}
				Map<String, String> processNumberMap = MesParameterProcessor.getProcessNumberGroup(paramsMap);
				rightTechnicInfoPanel.setProcessNumberMap(processNumberMap);
				MesParameterMainFrame.getMesParameterMainPanel().getGroupComboBox().removeAllItems();
				MesParameterMainFrame.getMesParameterMainPanel().getGroupComboBox().addItem("");
				if(processNumberMap != null){
					for(Map.Entry<String, String> entry : processNumberMap.entrySet()){
						String key = entry.getKey();
						MesParameterMainFrame.getMesParameterMainPanel().getGroupComboBox().addItem(key);
					}
				}
				MesParameterMainFrame.getMesParameterMainPanel().getGroupComboBox().setSelectedItem("");
//				List<NewSpecialParamTablePanel> specialParamTablePanelList = rightTechnicInfoPanel.getSpecialParamTabbedPanel().getSpecialParamTablePanelList();
//				if(processNumber == null || "".equals(processNumber)){
//					JOptionPane.showMessageDialog(null, "产品编号不能为空！");
//				}else{
//					if(technicNumber.endsWith("_ZF")){
//						isZF = "Y";
//					}else{
//						isZF = "N";
//					}
//					for(NewSpecialParamTablePanel specialParamTablePanel : specialParamTablePanelList){
//						specialParamTablePanel.stopCellEditing();
//						CmParamTableType specialParamTableType = specialParamTablePanel.getParamTableType();
//						String specialTechnicsNumber = specialParamTablePanel.getTechnicsNumber();
//						String specialObjNumber = specialParamTablePanel.getObjNumber();
//						String specialObjType = specialParamTablePanel.getObjType();
//						MPMParameterProcessor.deleteOldUUIDFiles(specialParamTablePanel.getOldUUIDFileNameList());
//						specialParamTablePanel.uploadImage();
//						paramsMap = new HashMap<String,String>();
//						paramsMap.put("processNumber", processNumber);
//						paramsMap.put("technicsNumber", specialTechnicsNumber);
//						paramsMap.put("objType", specialObjType);
//						paramsMap.put("objNumber", specialObjNumber);
//						paramsMap.put("lukahao", lukahao);
//						paramsMap.put("gxPK", gxPK);
//						paramsMap.put("isZF", isZF);
//						MesParameterProcessor.saveMesParameters(specialParamTableType, paramsMap);
//					}
//					JOptionPane.showMessageDialog(null, "保存成功");
//				}
			}

		}
	}
	public boolean isProcedureExist(Document document, String produreNumber){
		XWTechnicsTreeObject technicTreeObject = new XWTechnicsTreeObject(document);
		List<Element> list = XmlUtility.getAllSteps(technicTreeObject.getTreeCellData());
		for(Element procedureElement : list){
			String procedureNumber = procedureElement.attributeValue("stepNumber");
			if(procedureNumber.equals(produreNumber)){
				return true;
			}
		}
		return false;
	}

	public LeftTechnicTreePanel getLeftTechnicTreePanel() {
		return leftTechnicTreePanel;
	}

	public RightTechnicInfoPanel getRightTechnicInfoPanel() {
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

	public MesParameterMainFrame getFrame() {
		return frame;
	}

	public JTextField getTechnicNumberField() {
		return technicNumberField;
	}

	public static void setValueList(List<String> valueList) {
		MesParameterMainPanel.valueList = valueList;
	}

	public static void setDefaultValueList(List<String> defaultValueList) {
		MesParameterMainPanel.defaultValueList = defaultValueList;
	}

	public void setProcessNumberBox(MultiComboBox processNumberBox) {
		this.processNumberBox = processNumberBox;
	}

	public JButton getSearchButton() {
		return searchButton;
	}

	public MultiComboBox getProcessNumberBox() {
		return processNumberBox;
	}

	public JButton getSaveButton() {
		return saveButton;
	}

	public JComboBox getGroupComboBox() {
		return groupComboBox;
	}

}
