package com.glaway.mpm.view;

import com.glaway.mpm.sop.view.SopFileTableJPanel;
import com.glaway.mpm.spechar.SpeCharPanelCommonStringListener;
import com.glaway.mpm.spechar.SpeCharPanelSymbolListener;
import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.InputLimited;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import com.glaway.speciaword.component.EditorPane;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.*;

public class TechnicsStepJPanel_View extends JPanel {

	private static final long serialVersionUID = 1L;
	private NewTechnicsHistoryView frame;
	private Element stepElement;
	private XWTreeNode node;
	private String stepNumber = "";
	private JLabel label_1 = new JLabel("*工序号");
	private JTextField technicsStepNumberField = new JTextField();
	private JLabel label_2 = new JLabel("工序名称");
	private JTextField stepNameField = new JTextField();
	private JLabel label_3 = new JLabel("工种");
	public JComboBox workTypeBox = new JComboBox();
	private JLabel label_4 = new JLabel("制造单位");
	public JComboBox workShopBox = new JComboBox();
	private JLabel label_8 = new JLabel("");
	private JCheckBox stepFlagBox = new JCheckBox();
	private JLabel label_6 = new JLabel("工序简述");
	private JTextPane speCharPanel = new JTextPane();
	private JLabel label_7 = new JLabel("检测操作指导说明");
	JScrollPane operateInstructionJScrollPane;
	private TextPane operateInstructionCharPanel = new TextPane();
	private JLabel label_10 = new JLabel("工序简述");
	private JTextField label10_value = new JTextField();

	private JButton saveButton = new IconButton("/images/button_save.png", "保存");

	private NewEquipJPanel equipJPanel = null;
	private NewToolJPanel toolJPanel = null;
	private NewKnifeToolPanel knifeToolJPanel = null;
	private NewAttachJPanel attachJPanel = new NewAttachJPanel(this);
	private NewStandardDashboardJPanel standardDashboardJPanel = null;
	private NewUnStandardDashboardJPanel unStandardDashboardJPanel = null;
	private NewMeasureJPanel measureJPanel = null;
	private NewWorkSpaceJPanel workspaceJPanel = null;
	private NewMaterialJPanel materialJPanel = null;
	private NewPartJPanel partJPanel = null;
	private NewDrawingJPanel_View drawingJPanel = new NewDrawingJPanel_View(this);
	private SopFileTableJPanel sopFileTableJPanel;

	// 信维二期需求界面
	private JLabel label_5 = new JLabel("工位");
	private JComboBox stepStation = new JComboBox();
	private JPopupMenu pop = new JPopupMenu();

	private JLabel label_9 = new JLabel("工步信息");
	private PaceTablePane_View ptp;

	private JPanel upPane = new JPanel();
	private JPanel middlePanel = new JPanel();
	private JPanel downPane = new JPanel();

	private JTabbedPane tabbedPane = new JTabbedPane();

	private static final String part = "参装件";

	Map workShop = null;
	Map workType = null;
	Map workSpace = null;
	private JSplitPane topSplitPane = new JSplitPane();
	private JSplitPane bottomSplitPane = new JSplitPane();
	JPanel tablePanel = new JPanel();
	private JLabel keyStepLabel = new JLabel("关键工序");
	private int opFiled = -1; //操作字段   1：工序简述  2：检测操作指导说明
	//private  SpeCharPanel stepContentSpeCharPanel = new SpeCharPanel();
	final EditorPane stepContentPane = new EditorPane(null);
	private JFrame parentFrame;
	private JLabel pcno_label = new JLabel("批次号");
	private JTextField pcno_value = new JTextField();

	private JLabel executor_label = new JLabel("操作者");
	private JTextField executor_value = new JTextField();

	private JLabel sysb_label = new JLabel("使用设备");
	private JTextField sysb_value = new JTextField();

	private JLabel hjtj_label = new JLabel("环境条件");
	private JTextField hjtj_value = new JTextField();

	private JLabel jcgj_label = new JLabel("检测工具");
	private JTextField jcgj_value = new JTextField();

	private JLabel kztb_label = new JLabel("控制图表");
	private JTextField kztb_value = new JTextField();

	private JLabel kznr_label = new JLabel("控制内容");
	private TextPane kznr_value = new TextPane();
	JScrollPane kznr_valueJScrollPane;

	private JLabel zlkzcx_label = new JLabel("质量控制程序");
	private TextPane zlkzcx_value = new TextPane();
	JScrollPane zlkzcx_valueJScrollPane;

	private List<Component> list = new ArrayList<Component>();
	private Map<String,String> attrsMap = new HashMap<String,String>();
	private Map<String,List<List<String>>> xmlmap = new HashMap<String, List<List<String>>>();
	private int row = 5;

	public TechnicsStepJPanel_View(NewTechnicsHistoryView frame,JFrame parent) {
		super();
		this.frame = frame;
		this.parentFrame = parent;
		String startType = com.glaway.mpm.EditorConfig.startType;
		partJPanel = new NewPartJPanel(this, parentFrame);
		equipJPanel = new NewEquipJPanel(this, (JDialog) frame);
		toolJPanel = new NewToolJPanel(this, (JDialog) frame);
		knifeToolJPanel = new NewKnifeToolPanel(this, (JDialog) frame);
		materialJPanel = new NewMaterialJPanel(this, (JDialog) frame);
		standardDashboardJPanel = new NewStandardDashboardJPanel(this,frame);
		unStandardDashboardJPanel = new NewUnStandardDashboardJPanel(this,frame);
		measureJPanel = new NewMeasureJPanel(this,frame);
		sopFileTableJPanel = new SopFileTableJPanel(this,frame);
		workspaceJPanel = new NewWorkSpaceJPanel(this,frame);
		upPane.setLayout(new GridBagLayout());
		middlePanel.setLayout(new GridBagLayout());
		downPane.setLayout(new GridBagLayout());

		topSplitPane.setOneTouchExpandable(true);
		topSplitPane.setDividerSize(10);
		topSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(upPane, BorderLayout.WEST);

		JScrollPane scrollPanel = new JScrollPane(panel,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		topSplitPane.setLeftComponent(scrollPanel);
		topSplitPane.setRightComponent(middlePanel);
		topSplitPane.setDividerLocation(100);

		bottomSplitPane.setOneTouchExpandable(true);
		bottomSplitPane.setDividerSize(10);
		bottomSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());
		panel1.add(downPane, BorderLayout.CENTER);
		bottomSplitPane.setLeftComponent(topSplitPane);
		bottomSplitPane.setRightComponent(panel1);
		bottomSplitPane.setDividerLocation(350);

		workTypeBox.setEditable(false);
		setLayout(new GridBagLayout());
		add(bottomSplitPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));

		upPane.setLayout(new GridBagLayout());
		downPane.setLayout(new GridBagLayout());
		ptp = new PaceTablePane_View(frame, this);

		//关键工序标识
		JPanel panel2 = new JPanel();
		panel2.add(stepFlagBox);
		panel2.add(keyStepLabel);
		upPane.add(panel2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 5, 5), 0, 0));

		//工序号
		upPane.add(label_1, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						10, 5, 5), 0, 0));

		technicsStepNumberField.setDocument(new InputLimited(3, true));
		technicsStepNumberField.setPreferredSize(new Dimension(38, 23));
		technicsStepNumberField.setMinimumSize(new Dimension(38, 23));
		technicsStepNumberField.setMaximumSize(new Dimension(38, 23));
		upPane.add(technicsStepNumberField, new GridBagConstraints(3, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		//制造单位
		upPane.add(label_4, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						20, 5, 5), 0, 0));
		upPane.add(workShopBox, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 5, 5), 0, 0));

		//工种
//		upPane.add(label_3, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0,
//				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
//						20, 5, 5), 0, 0));
//
//		upPane.add(workTypeBox, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0,
//				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
//						5, 5, 5), 0, 0));

		//工位
//		upPane.add(label_5, new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0,
//				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
//						20, 5, 5), 0, 0));
//		upPane.add(stepStation, new GridBagConstraints(9, 0, 1, 1, 0.0, 0.0,
//				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
//						5, 5, 5), 0, 0));

		//工序名称
		upPane.add(label_2, new GridBagConstraints(10, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						20, 5, 5), 0, 0));

		stepNameField.setMaximumSize(new Dimension(200, 23));
		stepNameField.setMinimumSize(new Dimension(200, 23));
		stepNameField.setPreferredSize(new Dimension(200, 23));
		stepNameField.setEditable(false);
		upPane.add(stepNameField, new GridBagConstraints(11, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		//工序简述
	    upPane.add(label_10, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
						new Insets(10, 25, 5, 5), 0, 0));

	    upPane.add(label10_value, new GridBagConstraints(1, 1, 11, 1, 0, 0,
						GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
								0, 5, 5), 0, 0));

		//工序内容
		upPane.add(label_6, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(20, 25, 5, 5), 0, 0));

		stepContentPane.setEnabled(false);
		this.stepContentPane.setBackground(Color.white);
		JMenuItem ocommonStringItem1 = new JMenuItem("插入工艺常用语");
		JMenuItem osymbolItem1 = new JMenuItem("插入工艺符号");
		this.stepContentPane.addCustomMenu(ocommonStringItem1);
		this.stepContentPane.addCustomMenu(osymbolItem1);
		osymbolItem1.addActionListener(new SpeCharPanelSymbolListener(this.stepContentPane,this.parentFrame));
		ocommonStringItem1.addActionListener(new SpeCharPanelCommonStringListener(this.stepContentPane,this.parentFrame));

		upPane.add(stepContentPane, new GridBagConstraints(1, 3, 11, 1, 1.0, 0.5,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));

		//检测操作指导说明
		upPane.add(label_7, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(20, 25, 5, 5), 0, 0));

		operateInstructionCharPanel.setEditable(false);
		operateInstructionCharPanel.setMaximumSize(new Dimension(200, 38));
		operateInstructionCharPanel.setMinimumSize(new Dimension(200, 38));
		operateInstructionCharPanel.setPreferredSize(new Dimension(200, 38));
		operateInstructionCharPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		operateInstructionJScrollPane = new JScrollPane(operateInstructionCharPanel,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		upPane.add(operateInstructionJScrollPane, new GridBagConstraints(1, 4, 11, 1, 1.0, 0.5,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));

		//控制内容
		upPane.add(kznr_label, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(20, 25, 5, 5), 0, 0));

		kznr_value.setEditable(false);
		kznr_value.setMaximumSize(new Dimension(200, 38));
		kznr_value.setMinimumSize(new Dimension(200, 38));
		kznr_value.setPreferredSize(new Dimension(200, 38));
		kznr_value.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		kznr_valueJScrollPane = new JScrollPane(kznr_value,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		upPane.add(kznr_valueJScrollPane, new GridBagConstraints(1, 5, 11, 1, 1, 0.5,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));

		//质量控制程序
		upPane.add(zlkzcx_label, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(20, 25, 5, 5), 0, 0));

		zlkzcx_value.setEditable(false);
		zlkzcx_value.setMaximumSize(new Dimension(200, 38));
		zlkzcx_value.setMinimumSize(new Dimension(200, 38));
		zlkzcx_value.setPreferredSize(new Dimension(200, 38));
		zlkzcx_value.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		zlkzcx_valueJScrollPane = new JScrollPane(zlkzcx_value,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		upPane.add(zlkzcx_valueJScrollPane,new GridBagConstraints(1, 6, 11, 1, 1, 0.5,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));

		//批次号
		upPane.add(pcno_label, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 25, 5, 5), 0, 0));

		pcno_value.setEditable(false);
		upPane.add(pcno_value, new GridBagConstraints(1, 7, 3, 1, 1, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));

		//操作者
		upPane.add(executor_label, new GridBagConstraints(4, 7, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 25, 5, 5), 0, 0));

		executor_value.setEditable(false);
		upPane.add(executor_value, new GridBagConstraints(5, 7, 2, 1, 1, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));


		//使用设备
		upPane.add(sysb_label, new GridBagConstraints(7, 7, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 25, 5, 5), 0, 0));

		sysb_value.setEditable(false);
		upPane.add(sysb_value, new GridBagConstraints(8, 7, 3, 1, 1, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));

		//环境条件
		upPane.add(hjtj_label, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 25, 5, 5), 0, 0));

		hjtj_value.setEditable(false);
		upPane.add(hjtj_value, new GridBagConstraints(1, 8, 3, 1, 1, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));


		//检测工具
		upPane.add(jcgj_label, new GridBagConstraints(4, 8, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 25, 5, 5), 0, 0));

		jcgj_value.setEditable(false);
		upPane.add(jcgj_value, new GridBagConstraints(5, 8, 2, 1, 1, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));


		//控制图标
		upPane.add(kztb_label, new GridBagConstraints(7, 8, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 25, 5, 5), 0, 0));

		kztb_value.setEditable(false);
		upPane.add(kztb_value, new GridBagConstraints(8, 8, 3, 1, 1, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));


		tablePanel.setLayout(new BorderLayout());
		middlePanel.add(tablePanel, new GridBagConstraints(0, 4, 6, 1, 1.0,
				1.0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH,
				new Insets(0, 5, 5, 5), 0, 0));

		tabbedPane.addTab("设备", equipJPanel);
		tabbedPane.addTab("工装及工具", toolJPanel);
		tabbedPane.addTab("材料", materialJPanel);
		tabbedPane.addTab("刀具", knifeToolJPanel);
		tabbedPane.addTab("标准仪器仪表", standardDashboardJPanel);
		tabbedPane.addTab("非标准仪器仪表", unStandardDashboardJPanel);
		tabbedPane.addTab("量具", measureJPanel);
		tabbedPane.addTab("工位", workspaceJPanel);
		if(!"SOP".equals(startType)){
			tabbedPane.addTab("简图", drawingJPanel);
		}
		tabbedPane.addTab("附件", attachJPanel);
		if(!"SOP".equals(startType)){
			tabbedPane.addTab("引用SOP文件", sopFileTableJPanel);
		}

		tablePanel.add(tabbedPane, BorderLayout.CENTER);

		downPane.add(ptp, new GridBagConstraints(0, 0, 6, 1, 1.0, 1.0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH,
				new Insets(5, 5, 5, 5), 0, 0));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		add(buttonPanel, new GridBagConstraints(0, 1, 6, 1, 1.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 5, 5, 5), 0, 0));

		buttonPanel.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0,
				0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

		buttonPanel.add(saveButton, new GridBagConstraints(1, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
				new Insets(0, 0, 5, 5), 0, 0));
	}

	public void setUIValues(Element stepElement) {
		this.stepElement = stepElement;
		technicsStepNumberField.setText(stepElement.attributeValue("stepNumber"));
		stepNumber = stepElement.attributeValue("stepNumber");
		// stepNameComboBox.reSetList();
		String shop = stepElement.attributeValue("workShop");
		if (shop != null && shop.trim().length() > 0) {
			workShopBox.addItem(shop);
			workShopBox.setSelectedItem(shop);
		} else {
			workShopBox.setSelectedItem("2");
		}
		stepNameField.setText(stepElement.attributeValue("stepName"));
		label10_value.setText(stepElement.attributeValue("GXJS"));
		// 需要先设置制造单位，此下拉框触发监听，清除工种和工位的下拉框
		System.out.println("当前工序的制造单位为===:" + shop + ":===:" + workShopBox.getSelectedItem() + ":=="+stepNameField.getText());
		// 关键工序
		String isKey = stepElement.attributeValue("isKeyStep");
		System.out.println("当前工序的关键工序标识为===isKey==" + isKey);

		if (isKey != null && isKey.trim().length() != 0) {
			stepFlagBox.setSelected(Boolean.parseBoolean(stepElement.attributeValue("isKeyStep")));
			if("true".equals(isKey)) {
				//TODO 149
				operateInstructionCharPanel.setText(XmlUtility.getHasEnterAttribute(stepElement, "operateInstruction"));
				pcno_value.setText(XmlUtility.getHasEnterAttribute(stepElement, "PCNO"));
				executor_value.setText(XmlUtility.getHasEnterAttribute(stepElement, "CZZ"));
				sysb_value.setText(XmlUtility.getHasEnterAttribute(stepElement, "SYSB"));
				hjtj_value.setText(XmlUtility.getHasEnterAttribute(stepElement, "HJTJ"));
				jcgj_value.setText(XmlUtility.getHasEnterAttribute(stepElement, "JCGJ"));
				kztb_value.setText(XmlUtility.getHasEnterAttribute(stepElement, "KZTB"));
				kznr_value.setText(XmlUtility.getHasEnterAttribute(stepElement, "KZNR"));
				zlkzcx_value.setText(XmlUtility.getHasEnterAttribute(stepElement, "ZLKZCX"));
			} else {
				label_7.setVisible(false);
				//opIllustrationSpeCharPanel.setVisible(false);
				//opIllustrationSpeCharPanel.setText("");
				operateInstructionJScrollPane.setVisible(false);
				operateInstructionCharPanel.setText("");

				pcno_label.setVisible(false);
				pcno_value.setVisible(false);
				pcno_value.setText("");

				executor_label.setVisible(false);
				executor_value.setVisible(false);
				executor_value.setText("");

				sysb_label.setVisible(false);
				sysb_value.setVisible(false);
				sysb_value.setText("");

				hjtj_label.setVisible(false);
				hjtj_value.setVisible(false);
				hjtj_value.setText("");

				jcgj_label.setVisible(false);
				jcgj_value.setVisible(false);
				jcgj_value.setText("");

				kztb_label.setVisible(false);
				kztb_value.setVisible(false);
				kztb_value.setText("");

				kznr_valueJScrollPane.setVisible(false);
				kznr_label.setVisible(false);
				kznr_value.setVisible(false);
				kznr_value.setText("");

				zlkzcx_valueJScrollPane.setVisible(false);
				zlkzcx_label.setVisible(false);
				zlkzcx_value.setVisible(false);
				zlkzcx_value.setText("");
			}
		} else {
			stepFlagBox.setSelected(false);
			label_7.setVisible(false);
			//opIllustrationSpeCharPanel.setVisible(false);
			//opIllustrationSpeCharPanel.setText("");
			operateInstructionJScrollPane.setVisible(false);
			operateInstructionCharPanel.setText("");

			pcno_label.setVisible(false);
			pcno_value.setVisible(false);
			pcno_value.setText("");

			executor_label.setVisible(false);
			executor_value.setVisible(false);
			executor_value.setText("");

			sysb_label.setVisible(false);
			sysb_value.setVisible(false);
			sysb_value.setText("");

			hjtj_label.setVisible(false);
			hjtj_value.setVisible(false);
			hjtj_value.setText("");

			jcgj_label.setVisible(false);
			jcgj_value.setVisible(false);
			jcgj_value.setText("");

			kztb_label.setVisible(false);
			kztb_value.setVisible(false);
			kztb_value.setText("");

			kznr_valueJScrollPane.setVisible(false);
			kznr_label.setVisible(false);
			kznr_value.setVisible(false);
			kznr_value.setText("");

			zlkzcx_valueJScrollPane.setVisible(false);
			zlkzcx_label.setVisible(false);
			zlkzcx_value.setVisible(false);
			zlkzcx_value.setText("");
		}
		// 特殊符号
		//speCharPanel.setText(XmlUtility.getProcedureContent(stepElement));
		// 特殊符号
		//speCharPanel.setText(XmlUtility.getProcedureContent(stepElement));
		stepContentPane.setText(XmlUtility.getProcedureContent(stepElement));
		//stepContentSpeCharPanel

		//显示IBA属性
		showIBAAttributes(stepElement);

		// 添加工位和工种
		String type = stepElement.attributeValue("workType");
		if (type != null && type.trim().length() > 0) {
			workTypeBox.addItem(type);
			workTypeBox.setSelectedItem(type);
		} else {
			workTypeBox.setSelectedItem("");// 工位
		}
		System.out.println("当前工序的工种为===:" + type + ":===:"
				+ workTypeBox.getSelectedItem() + ":==");

		String space = stepElement.attributeValue("workSpace");
		if (space != null && space.trim().length() > 0) {
			stepStation.addItem(space);// 工位
			stepStation.setSelectedItem(space);// 工位
		} else {
			stepStation.setSelectedItem("");// 工位
		}
		System.out.println("当前工序的工位为===:" + space + ":===:"
				+ stepStation.getSelectedItem() + ":==");

		setTableValues(stepElement);
		if (ptp != null) {
			ptp.setPaces(XmlUtility.getPaces(stepElement));
			ptp.displayPaceDatas();
		}

		equipJPanel.setTabTitle();
		toolJPanel.setTabTitle();
		materialJPanel.setTabTitle();
		// partJPanel.setTabTitle();
		drawingJPanel.setTabTitle();
		attachJPanel.setTabTitle();
		knifeToolJPanel.setTabTitle();
	}

	public void showIBAAttributes(Element stepElement) {
		hideIBAAttributes();
		Document docNode = stepElement.getDocument();
		Element element = XmlUtility.getTechnicsElement(docNode);
		String typeName = element.attributeValue("technicsType");
		System.out.println("-------typeName-----"+typeName);
		//attrs = TechnicsIntf.getMPMOperAttrByMPMPlan(typeName);
		try {
			xmlmap = TechnicsIntf.getMPMPPlanStepAttrByXML();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("-------------xmlmap--------"+xmlmap);
		List<List<String>> allAttributes = xmlmap.get(typeName);
		row = 13;
		if(allAttributes != null && !allAttributes.isEmpty()) {
			JTextField textField = null;
			JComboBox comboBox = null;
			JLabel label = null;
			int col = 0;

			for (List<String> ibaList : allAttributes) {
				label = new JLabel(ibaList.get(1));
				list.add(label);
				upPane.add(label, new GridBagConstraints(getGridx(col), row, getGridwidth(col++), 1, 0, 0,
						GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
						new Insets(5, 25, 5, 5), 0, 0));
				attrsMap.put(ibaList.get(0), ibaList.get(1));

				if("string".equals(ibaList.get(4))){
					textField = new JTextField();
					textField.setName(ibaList.get(0));
					textField.setVisible(true);
					textField.setText(XmlUtility.getHasEnterAttribute(stepElement, ibaList.get(0)));
					list.add(textField);
					textField.setEditable(false);
				    upPane.add(textField, new GridBagConstraints(getGridx(col), row, getGridwidth(col++), 1, 1, 0,
							GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
									0, 5, 5), 0, 0));
				} else if ("set".equals(ibaList.get(4))) {
					String[] vls = ibaList.get(3).split("\\|");
					Vector<String> v = new Vector<String>();
					for (String string : vls) {
						v.add(string);
					}
					comboBox = new JComboBox(v);
					comboBox.setName(ibaList.get(0));
					comboBox.setSelectedItem(XmlUtility.getHasEnterAttribute(stepElement, ibaList.get(0)));
					comboBox.setVisible(true);
					list.add(comboBox);
					comboBox.setEnabled(false);
				    upPane.add(comboBox, new GridBagConstraints(getGridx(col), row, getGridwidth(col++), 1, 1, 0,
							GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
									0, 5, 5), 0, 0));
				}

				if(col == 6) {
					col = 0;
					row++;
				}
			}
		}
		this.upPane.repaint();
	}

	private void hideIBAAttributes(){
		for(Component component:list) {
			component.setVisible(false);
		}
		list.clear();
	}

	private int getGridx(int col) {
		int gridx = 1;
		if(col == 0) {
			gridx = 0;
		} else if(col == 1) {
			gridx = 1;
		} else if(col == 2) {
			gridx = 4;
		} else if(col == 3) {
			gridx = 5;
		} else if(col == 4) {
			gridx = 7;
		} else if(col == 5) {
			gridx = 8;
		}
		return gridx;
	}

	private int getGridwidth(int col) {
		int gridwidth = 1;
		if(col == 0) {
			gridwidth = 1;
		} else if(col == 1) {
			gridwidth = 3;
		} else if(col == 2) {
			gridwidth = 1;
		} else if(col == 3) {
			gridwidth = 2;
		} else if(col == 4) {
			gridwidth = 1;
		} else if(col == 5) {
			gridwidth = 3;
		}
		return gridwidth;
	}

	private void setTableValues(Element stepElement) {
		// 零部件
		Element partElement = stepElement.element(XmlUtility.PART_GROUP);
		Vector<Element> partVec = new Vector<Element>();
		for (Iterator<Element> it = partElement.elementIterator(XmlUtility.PART_TAG); it.hasNext();) {
			partVec.add(it.next());
		}
		partJPanel.setTableValues(partVec);
		// 设备
		Element equipElement = stepElement.element(XmlUtility.EQUIP_GROUP);
		Vector<Element> equipVec = new Vector<Element>();
		for (Iterator<Element> it = equipElement.elementIterator(XmlUtility.EQUIP_TAG); it.hasNext();) {
			equipVec.add(it.next());
		}

		// 量具
		Element measureElement = stepElement.element(XmlUtility.MEASURE_GROUP);
		if(measureElement == null) {
			measureElement = stepElement.addElement(XmlUtility.MEASURE_GROUP);
		}
		Vector<Element> measureVec = new Vector<Element>();
		for (Iterator<Element> it = measureElement.elementIterator(XmlUtility.MEASURE_TAG); it.hasNext();) {
			measureVec.add(it.next());
		}
		measureJPanel.setTableValues(measureVec);

		// 工位
		Element workspaceElement = stepElement.element(XmlUtility.WORKSPACE_GROUP);
		if(workspaceElement == null) {
			workspaceElement = stepElement.addElement(XmlUtility.WORKSPACE_GROUP);
		}
		Vector<Element> workspaceVec = new Vector<Element>();
		for (Iterator<Element> it = workspaceElement.elementIterator(XmlUtility.WORKSPACE_TAG); it.hasNext();) {
			workspaceVec.add(it.next());
		}
		workspaceJPanel.setTableValues(workspaceVec);

		// 标准仪器仪表
		Element sDashboardElement = stepElement.element(XmlUtility.SDASHBOARD_GROUP);
		Vector<Element> sDashboardVec = new Vector<Element>();
		if(sDashboardElement != null) {
			for (Iterator<Element> it = sDashboardElement.elementIterator(XmlUtility.SDASHBOARD_TAG); it.hasNext();) {
				sDashboardVec.add(it.next());
			}
		}
		this.standardDashboardJPanel.setTableValues(sDashboardVec);

		// 非标准仪器仪表
		Element unsDashboardElement = stepElement.element(XmlUtility.UNSDASHBOARD_GROUP);
		Vector<Element> unsDashboardVec = new Vector<Element>();
		if(unsDashboardElement != null) {
			for (Iterator<Element> it = unsDashboardElement.elementIterator(XmlUtility.UNSDASHBOARD_TAG); it.hasNext();) {
				unsDashboardVec.add(it.next());
			}
		}
		this.unStandardDashboardJPanel.setTableValues(unsDashboardVec);

		equipJPanel.setTableValues(equipVec);
		// 工具
		Element toolElement = stepElement.element(XmlUtility.TOOL_GROUP);
		Vector<Element> toolVec = new Vector<Element>();
		for (Iterator<Element> it = toolElement.elementIterator(XmlUtility.TOOL_TAG); it.hasNext();) {
			toolVec.add(it.next());
		}
		toolJPanel.setTableValues(toolVec);

		// 刀具
		Element knifeToolElement = stepElement.element(XmlUtility.KNIFETOOL_GROUP);
		Vector<Element> knifeToolVec = new Vector<Element>();
		for (Iterator<Element> it = knifeToolElement.elementIterator(XmlUtility.KNIFETOOL_TAG); it.hasNext();) {
			knifeToolVec.add(it.next());
		}
		knifeToolJPanel.setTableValues(knifeToolVec);

		// 材料
		Element materialElement = stepElement.element(XmlUtility.MATERIAL_GROUP);
		Vector<Element> materialVec = new Vector<Element>();
		for (Iterator<Element> it = materialElement.elementIterator(XmlUtility.MATERIAL_TAG); it.hasNext();) {
			materialVec.add(it.next());
		}
		materialJPanel.setTableValues(materialVec);
		// 简图
		Element imageElement = stepElement.element(XmlUtility.IMAGE_GROUP);
		Vector<Element> imageVec = new Vector<Element>();
		for (Iterator<Element> it = imageElement.elementIterator(XmlUtility.IMAGE_TAG); it.hasNext();) {
			imageVec.add(it.next());
		}
		// Document doc = stepElement.getDocument();
		// Element techElement = doc.getRootElement().element(XMLUtil.TECHNICS);
		// String techNumber = techElement.attributeValue("technicsNumber");
		// String techDictionary = null;
		// try
		// {
		// techDictionary = WorkSpaceUtil.getTechnicsDirectory(techNumber);
		// } catch (Exception e)
		// {
		//
		// JOptionPane.showMessageDialog(frame, e.getMessage());
		// }
		drawingJPanel.setTableValues(imageVec);

		// 附件
		Element attachElement = stepElement.element("attachs");
		Vector<Element> attachElements = new Vector<Element>();
		System.out.println("attachElement===" + attachElement);
		if (attachElement != null) {
			for (Iterator<Element> it = attachElement.elementIterator("PAttachInfo"); it.hasNext();) {
				attachElements.add(it.next());
			}
			System.out.println(attachElements.size() + " attachElements==="
					+ attachElements);
			attachJPanel.setTableValues(attachElements);
		}
	}

	// private boolean checkSavePoint() throws Exception
	// {
	// return CompareUtil.compareTechnics(stepElement, getElement());
	// }

	public void clearUI() {
		stepElement = null;
		node = null;
		technicsStepNumberField.setText("");
		stepNameField.setText("");
		workTypeBox.setSelectedItem("");
		// workShopBox
		workShopBox.setSelectedItem("");
		stepStation.setSelectedItem("");
		stepFlagBox.setSelected(false);
		speCharPanel.setText("");
		equipJPanel.clearTable();
		toolJPanel.clearTable();
		knifeToolJPanel.clearTable();
		materialJPanel.clearTable();
		partJPanel.clearTable();
		drawingJPanel.clearTable();
		attachJPanel.clearTable();
	}

	// public void setTechType(String technicsType)
	// {
	// String[] data = null;
	// if(technicsType == null || technicsType.trim().length() == 0 ||
	// technicsType.equals(WorkSpaceUtil.ASM_TYPE))
	// {
	// data = XMLUtil.ASSEMBLETYPE.split(";");
	// }
	// else
	// {
	// data = XMLUtil.MASHINETYPE.split(";");
	// }
	// workTypeBox.removeAllItems();
	// for(int i = 0; i < data.length; i++)
	// {
	// workTypeBox.addItem(data[i]);
	// }
	// workTypeBox.setSelectedItem("");
	// }

	public void setNode(XWTreeNode node) {
		this.node = node;
	}

	// public void setPartVisible(String technicsType)
	// {
	// if(technicsType.equals(WorkSpaceUtil.PART_TYPE))
	// {
	// int index = tabbedPane.indexOfTab(part);
	// if(index!=-1)
	// tabbedPane.remove(index);
	// }
	// else
	// {
	// if(tabbedPane.getTabCount() == 4)
	// {
	// tabbedPane.insertTab(part,null,partJPanel,null,3);
	// }
	// }
	// }

	public Element getElement() throws Exception {
		ptp.stopTableCellEditing();
		Document doc = stepElement.getDocument();
		if (doc == null) {
			Document techDOC = frame.getDocument();
			String bsoID = stepElement.attributeValue("bsoID");
			Element step = XmlUtility.getStepByID(
					XmlUtility.getTechnicsElement(techDOC), bsoID);
			if (step == null)
				return null;
			stepElement = step;
		}
		Element element = stepElement;
		String text = technicsStepNumberField.getText().replaceAll("　", " ")
				.trim();
		text = XmlUtility.toSemiangle(text);
		System.out.println("转换后=======" + text);
		if (text == null || text.trim().length() == 0) {
			text = stepNumber;
		} else {
			Element ele = XmlUtility.getStepByStepNumber(element.getParent()
					.getParent(), text);
			if (ele != null) {
				String bsoID = ele.attributeValue("bsoID").trim();
				String curBsoID = element.attributeValue("bsoID").trim();
				if (!bsoID.equals(curBsoID)) {
					text = stepNumber;
				}
			}
		}
		XmlUtility.setAttributeValue(element, "stepNumber", text);
		XmlUtility.setAttributeValue(element, "stepName", stepNameField.getText());

		String type = (String) workTypeBox.getSelectedItem();
		if (type == null) {
			type = "";
		}
		XmlUtility.setAttributeValue(element, "workType", type);

		String shop = (String) workShopBox.getSelectedItem();
		if (shop == null) {
			shop = "";
		}
		XmlUtility.setAttributeValue(element, "workShop", shop);

		String space = (String) stepStation.getSelectedItem();
		if (space == null)
			space = "";
		XmlUtility.setAttributeValue(element, "workSpace", space);

		XmlUtility.setAttributeValue(element, "stepHour", "");
		XmlUtility.setAttributeValue(element, "isKey", String.valueOf(stepFlagBox.isSelected()));

		// 设置制造单位ID
		workShop = ResourceIntf.getWorkShops();
		String workShopId = getKey(workShop, element.attributeValue("workShop"));
		XmlUtility.setAttributeValue(element, "workShopID", workShopId);
		// 设置工种ID
		workType = ResourceIntf.getWorkTypeByPdName(element.attributeValue("stepName"), workShopId);
		XmlUtility.setAttributeValue(element, "workTypeID", getKey(workType, element.attributeValue("workType")));
		// 设置工位ID
		workSpace = ResourceIntf.getWorkSpaces(element.attributeValue("workShopID"));
		XmlUtility.setAttributeValue(element, "workSpaceID", getKey(workSpace, element.attributeValue("workSpace")));

		XmlUtility.setProcedureContent(element, stepContentPane.getText());
		setLinkAtrributes(element);
		ptp.getElements();
		XmlUtility.reSetPaceNumbers(element);
		return element;
	}

	private void setLinkAtrributes(Element stepElement) {
		// 零部件
		Element partElement = stepElement.element(XmlUtility.PART_GROUP);
		partElement.elements().clear();
		Vector<Element> part = partJPanel.getElements();
		for (Iterator<Element> it = part.iterator(); it.hasNext();) {
			partElement.add(it.next());
		}
		// 设备
		Element equipElement = stepElement.element(XmlUtility.EQUIP_GROUP);
		equipElement.elements().clear();
		Vector<Element> equip = equipJPanel.getElements();
		for (Iterator<Element> it = equip.iterator(); it.hasNext();) {
			equipElement.add(it.next());
		}

		// 量具
		Element measureElement = stepElement.element(XmlUtility.MEASURE_GROUP);
		if(measureElement == null) {
			measureElement = stepElement.addElement(XmlUtility.SDASHBOARD_GROUP);
		} else {
			measureElement.elements().clear();
		}
		Vector<Element> measure = measureJPanel.getElements();
		for (Iterator<Element> it = measure.iterator(); it.hasNext();) {
			measureElement.add(it.next());
		}

		//工位
		Element workspaceElement = stepElement.element(XmlUtility.WORKSPACE_GROUP);
		if(workspaceElement == null) {
			workspaceElement = stepElement.addElement(XmlUtility.WORKSPACE_GROUP);
		} else {
			workspaceElement.elements().clear();
		}
		Vector<Element> workspace = workspaceJPanel.getElements();
		for (Iterator<Element> it = workspace.iterator(); it.hasNext();) {
			workspaceElement.add(it.next());
		}

		// 标准仪器仪表
		Element sDashboardElement = stepElement.element(XmlUtility.SDASHBOARD_GROUP);
		if(sDashboardElement == null) {
			sDashboardElement = stepElement.addElement(XmlUtility.SDASHBOARD_GROUP);
		} else {
			sDashboardElement.elements().clear();
		}
		Vector<Element> sDashboard = this.standardDashboardJPanel.getElements();
		for (Iterator<Element> it = sDashboard.iterator(); it.hasNext();) {
			sDashboardElement.add(it.next());
		}

		// 非标准仪器仪表
		Element unsDashboardElement = stepElement.element(XmlUtility.UNSDASHBOARD_GROUP);
		if(unsDashboardElement == null) {
			unsDashboardElement = stepElement.addElement(XmlUtility.UNSDASHBOARD_GROUP);
		} else {
			unsDashboardElement.elements().clear();
		}
		Vector<Element> unsDashboard = this.unStandardDashboardJPanel.getElements();
		for (Iterator<Element> it = unsDashboard.iterator(); it.hasNext();) {
			unsDashboardElement.add(it.next());
		}

		// 工具
		Element toolElement = stepElement.element(XmlUtility.TOOL_GROUP);
		toolElement.elements().clear();
		Vector<Element> tool = toolJPanel.getElements();
		for (Iterator<Element> it = tool.iterator(); it.hasNext();) {
			toolElement.add(it.next());
		}
		// 刀具
		Element knifeToolElement = stepElement.element(XmlUtility.KNIFETOOL_GROUP);
		knifeToolElement.elements().clear();
		Vector<Element> kinifeTool = knifeToolJPanel.getElements();
		for (Iterator<Element> it = kinifeTool.iterator(); it.hasNext();) {
			knifeToolElement.add(it.next());
		}
		// 材料
		Element materialElement = stepElement
				.element(XmlUtility.MATERIAL_GROUP);
		materialElement.elements().clear();
		Vector<Element> material = materialJPanel.getElements();
		for (Iterator<Element> it = material.iterator(); it.hasNext();) {
			materialElement.add(it.next());
		}
		// 简图
		Element imageElement = stepElement.element(XmlUtility.IMAGE_GROUP);
		imageElement.elements().clear();
		Vector<Element> image = drawingJPanel.getElements();
		for (Iterator<Element> it = image.iterator(); it.hasNext();) {
			imageElement.add(it.next());
		}
		// 附件
		Element attachElement = stepElement.element("attachs");
		if (attachElement == null) {
			attachElement = stepElement.addElement("attachs");
		} else {
			attachElement.elements().clear();
		}
		Vector<Element> attaches = attachJPanel.getElements();
		for (Iterator<Element> it = attaches.iterator(); it.hasNext();) {
			attachElement.add(it.next());
		}

		// SOP
		Element sopElement = stepElement.element(XmlUtility.SOP_GROUP);
		sopElement.elements().clear();
		Vector<Element> sop = sopFileTableJPanel.getElements();
		for (Iterator<Element> it = sop.iterator(); it.hasNext();) {
			sopElement.add(it.next());
		}
	}

	public String getKey(Map map, String value) {
		if (map == null || value == null)
			return "NOKEY";
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			Object temp = map.get(key);
			if (temp.equals(value)) {
				return key.toString();
			}
		}
		return "NOKEY";
	}

	public void setUIEnabled(boolean b) {
		technicsStepNumberField.setEnabled(b);
		stepNameField.setEnabled(b);
		workTypeBox.setEnabled(b);
		workShopBox.setEnabled(b);
		stepFlagBox.setEnabled(b);
		stepStation.setEnabled(b);
		speCharPanel.setEnabled(b);
		saveButton.setEnabled(b);
		equipJPanel.setUIEnabled(b);
		toolJPanel.setUIEnabled(b);
		knifeToolJPanel.setUIEnabled(b);
		materialJPanel.setUIEnabled(b);
		partJPanel.setUIEnabled(b);
		drawingJPanel.setUIEnabled(b);
		attachJPanel.setUIEnabled(b);
		ptp.setUIEnabled(b);
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public NewTechnicsHistoryView getFrame() {
		return frame;
	}

	/**
	 * 工序简述中添加常用语
	 *
	 * @author xuehu
	 *
	 */
	class TextPane extends JTextArea implements Observer {
		public void update(Observable o, Object arg) {
			try {
				if (o != null && o instanceof CommonObservable) {
					CommonObservable co = (CommonObservable) o;
					int type = co.getType();
					if (type == 0)// 表示是工艺术语
					{
						if (arg != null && arg instanceof String) {
							String commonString = (String) arg;
							if (commonString == null) {
								commonString = "";
							}
							TextPane panel = null;
							if(opFiled==1){
								//panel = speCharPanel;
							}else if(opFiled==2){
								//panel = operateInstructionCharPanel;
							}
							if(panel!=null){
								int loc = panel.getCaretPosition();
								if (loc >= 0) {
									panel.getDocument().insertString(loc, commonString, null);
								}
							}

						}
					}
				}
			} catch (Exception e) {

			}
		}
	}
}