package com.glaway.mpm.view;

import com.glaway.mpm.EditorConfig;
import com.glaway.mpm.intf.workproceduce.WorkproceduceUtil;
import com.glaway.mpm.model.CsType;
import com.glaway.mpm.model.TechnicsCyy;
import com.glaway.mpm.parameter.designui.NewCommonParamTablePanel;
import com.glaway.mpm.parameter.designui.NewSpecialParamTabbedPanel;
import com.glaway.mpm.parameter.designui.NewSpecialParamTablePanel;
import com.glaway.mpm.pdf.HtmlGenerator;
import com.glaway.mpm.qmIntf.common.model.CommonComboBox;
import com.glaway.mpm.qmIntf.commonString.CsSearchDialog;
import com.glaway.mpm.sop.view.SopFileTableJPanel;
import com.glaway.mpm.util.*;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.CommonStringIntf;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import com.glaway.speciaword.common.CommonHelper;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import wt.method.RemoteMethodServer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.*;

public class TechnicsStepJPanel_XW extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(TechnicsStepJPanel_XW.class);

	private NewTechnicsPart frame;
	public static Element stepElement;
	private Element techElement;
	private String[] operationLevelValues = {"","一星","二星","三星","四星","五星"};
	private String[] enforceValues = {"是"};
	private String startType = "";
	public static Element getStepElement() {
		return stepElement;
	}

	public void setStepElement(Element stepElement) {
		this.stepElement = stepElement;
	}

	public Element getTechElement() {
		return techElement;
	}

	public void setTechElement(Element techElement) {
		this.techElement = techElement;
	}

	private XWTreeNode node;
	public static List<CsType> csTypes;
	public static List<TechnicsCyy> cyys = new ArrayList<TechnicsCyy>();
	private String stepNumber = "";
	private JLabel label_1 = new JLabel("*工序号");
	public JTextField technicsStepNumberField = new JTextField();
	private JLabel label_2 = new JLabel("工序名称");
	private StepNameComboBox stepNameComboBox = new StepNameComboBox(null);
	private JLabel operationLevelLabel = new JLabel("*操作星级");
	private JComboBox operationLevelComboBox = new JComboBox(operationLevelValues);
	private JLabel enforceLabel = new JLabel("是否强制执行");
	private JComboBox enforceComboBox = new JComboBox(enforceValues);
	//	private StepNameTextField stepNameText = new StepNameTextField();
//  private static Map<String, String> allStepNameMap = ResourceIntf.getProcessStepName();
	private JLabel label_3 = new JLabel("工种");
	public CommonComboBox workTypeBox = new CommonComboBox();
	private JLabel label_4 = new JLabel("制造单位");
	public CommonComboBox workShopBox = new CommonComboBox();
	private JLabel keyStepLabel = new JLabel("关键工序");
	private JCheckBox stepFlagBox = new JCheckBox();
	private JLabel label_6 = new JLabel("工序内容");
	private JLabel label_zpjcjg = new JLabel("装配检测结果");
	public JTextField zpjcjg = new JTextField();
	private JLabel label_8 = new JLabel("工序简述");
	private JTextField label8_value = new JTextField();
	private JButton editButton = new JButton("最大化工序内容");

	private JLabel zjgsLabel = new JLabel("准结工时");
	private JTextField zjgsValue = new JTextField();

	private JLabel djgsLabel = new JLabel("单件工时");
	private JTextField djgsValue = new JTextField();

	//private TextPane speCharPanel = new TextPane();
	//private  SpeCharPanel stepContentSpeCharPanel = new SpeCharPanel();
	//final EditorPane stepContentPane = new EditorPane(true);
	private SpecialWordPanel speCharPanel = null;

	public SpecialWordPanel getSpeCharPanel() {
		return speCharPanel;
	}

	public void setSpeCharPanel(SpecialWordPanel speCharPanel) {
		this.speCharPanel = speCharPanel;
	}

	private SpecialWordPanel kznrspeCharPanel = null;

	private SpecialWordPanel zlkzcxspeCharPanel = null;

	JMenuItem terminologyItem = new JMenuItem("插入工艺常用语");

	private JLabel label_7 = new JLabel("检测操作指导说明");
	JScrollPane operateInstructionJScrollPane;
	private JTextArea operateInstructionCharPanel = new JTextArea();

	//private  SpeCharPanel opIllustrationSpeCharPanel = new SpeCharPanel(); //检测及操作指导说明

	private JLabel pcno_label = new JLabel("批次号");
	private JTextField pcno_value = new JTextField();
	private JComboBox pcno_box_value = new JComboBox();
	private Vector<String> batchs;

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
	private JTextArea kznr_value = new JTextArea();
	JScrollPane kznr_valueJScrollPane;

	private JLabel zlkzcx_label = new JLabel("质量控制程序");
	private JTextArea zlkzcx_value = new JTextArea();
	JScrollPane zlkzcx_valueJScrollPane;

	private NewEquipJPanel equipJPanel = null;
	private NewToolJPanel toolJPanel = null;
	private NewMaterialJPanel materialJPanel = null;
	private NewDrawingJPanel drawingJPanel = null;
	private NewAttachJPanel attachJPanel = null;
	private NewKnifeToolPanel knifeToolPanel = null;
	private NewStandardDashboardJPanel standardDashboardJPanel = null;
	private NewUnStandardDashboardJPanel unStandardDashboardJPanel = null;
	private NewMeasureJPanel measureJPanel = null;
	private AdditionalTableJPanel additionalTableJPanel = null;
	private BorrowThecnicsJPanel borrowThecnicsJPanel = null;
	private LargeFileJPanel largeFileJPanel = null;
	private NewWorkSpaceJPanel workspaceJPanel = null;
	/** Add By Wangxl */
	private NewCommonParamTablePanel commonParamTablePanel = null;
	private NewSpecialParamTabbedPanel specialParamTabbedPanel = null;
//	private NewCheckParamTabbedPanel checkParamTabbedPanel = null;

	private SopFileTableJPanel sopFileTableJPanel;
	private NewPartJPanel partJPanel = null;

	private JLabel label_5 = new JLabel("工位");
	private CommonComboBox stepStation = new CommonComboBox();
	private JPopupMenu popupMenu = new JPopupMenu();

	private PaceTablePane ptp;

	private JSplitPane topSplitPane = new JSplitPane();

	private JPanel upPane = new JPanel();
	private JPanel middlePanel = new JPanel();

	private JPanel tablePanel = new JPanel();

	private JSplitPane bottomSplitPane = new JSplitPane();

	private JPanel downPane = new JPanel();

	private JTabbedPane tabbedPane = new JTabbedPane();

	public static final int LEFTSPACE = 20;

	Map workShop = null;
	Map workType = null;
	Map workSpace = null;
	Map stepName = null;

	private int opFiled = -1; //操作字段   1：工序简述  2：检测操作指导说明

	private Map<String,String> attrs = new HashMap<String,String>();
	private Map<String,String> attrsMap = new HashMap<String,String>();
	private JPanel panel = new JPanel();
	private List<Component> list = new ArrayList<Component>();
	private Map<String,List<List<String>>> xmlmap = new HashMap<String, List<List<String>>>();
	private int row = 5;
	private Map<String,List<Component>> compMap = new HashMap<String,List<Component>>();

	private String imageFolder;
	//add by machongqi 2015-6-3
	private JScrollPane js2 =null;
	private JScrollPane js3=null;
	//add by machongqi end
	public TechnicsStepJPanel_XW(NewTechnicsPart parent) {
		super();
		NewTechnicsPart.startAnimFrame.setHeaderMessage("加载工序");
		frame = parent;
		this.startType = com.glaway.mpm.EditorConfig.startType;
		speCharPanel = new SpecialWordPanel(frame, true, null);
		kznrspeCharPanel = new SpecialWordPanel(frame, true, null);
		zlkzcxspeCharPanel = new SpecialWordPanel(frame, true, null);
		init();
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成加载工序");
	}

	private void init() {
		initLookAndFeel();
		initDimension();
		initComponents();
		initLayout();
		initActions();
		loadInitDatas();
		label_zpjcjg.setVisible(false);
        zpjcjg.setVisible(false);
	}

	private void initLookAndFeel() {
		workTypeBox.setEditable(false);
	}

	private void initDimension() {

	}

	private void initComponents() {


		//工序信息上分割线
		topSplitPane.setOneTouchExpandable(true);
		topSplitPane.setDividerSize(10);
		topSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(upPane, BorderLayout.WEST);

		JScrollPane scrollPanel = new JScrollPane(panel,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		middlePanel.setPreferredSize(new Dimension(downPane.getWidth(),downPane.getHeight()));
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		panel2.add(middlePanel, BorderLayout.CENTER);

		JScrollPane scrollPane2 = new JScrollPane(panel2,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		topSplitPane.setLeftComponent(scrollPanel);
		topSplitPane.setRightComponent(scrollPane2);
		topSplitPane.setDividerLocation(130);

		bottomSplitPane.setOneTouchExpandable(true);
		bottomSplitPane.setDividerSize(10);
		bottomSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());
		panel1.add(downPane, BorderLayout.CENTER);
		JScrollPane scrollPane3 = new JScrollPane(panel1,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		bottomSplitPane.setLeftComponent(topSplitPane);
		bottomSplitPane.setRightComponent(panel1);
		bottomSplitPane.setDividerLocation(430);

		technicsStepNumberField.setDocument(new InputLimited(3, true));
		technicsStepNumberField.setPreferredSize(new Dimension(38, 23));
		technicsStepNumberField.setMinimumSize(new Dimension(38, 23));
		technicsStepNumberField.setMaximumSize(new Dimension(38, 23));

		stepNameComboBox.setMaximumSize(new Dimension(200, 23));
		stepNameComboBox.setMinimumSize(new Dimension(200, 23));
		stepNameComboBox.setPreferredSize(new Dimension(200, 23));

		speCharPanel.setMaximumSize(new Dimension(200, 80));
		speCharPanel.setMinimumSize(new Dimension(200, 80));
		speCharPanel.setPreferredSize(new Dimension(200, 200));
		speCharPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

		kznrspeCharPanel.setMaximumSize(new Dimension(200, 38));
		kznrspeCharPanel.setMinimumSize(new Dimension(200, 38));
		kznrspeCharPanel.setPreferredSize(new Dimension(200, 38));
		kznrspeCharPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

		zlkzcxspeCharPanel.setMaximumSize(new Dimension(200, 38));
		zlkzcxspeCharPanel.setMinimumSize(new Dimension(200, 38));
		zlkzcxspeCharPanel.setPreferredSize(new Dimension(200, 38));
		zlkzcxspeCharPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));


//		operateInstructionCharPanel.setMaximumSize(new Dimension(200, 38));
//		operateInstructionCharPanel.setMinimumSize(new Dimension(200, 38));
//		operateInstructionCharPanel.setPreferredSize(new Dimension(200, 38));
//		operateInstructionCharPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

		operateInstructionJScrollPane = new JScrollPane(operateInstructionCharPanel,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		operateInstructionJScrollPane.setMaximumSize(new Dimension(200, 38));
		operateInstructionJScrollPane.setMinimumSize(new Dimension(200, 38));
		operateInstructionJScrollPane.setPreferredSize(new Dimension(200, 38));
		operateInstructionJScrollPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

//		kznr_value.setMaximumSize(new Dimension(200, 38));
//		kznr_value.setMinimumSize(new Dimension(200, 38));
//		kznr_value.setPreferredSize(new Dimension(200, 38));
//		kznr_value.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

		kznr_valueJScrollPane = new JScrollPane(kznr_value,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		kznr_valueJScrollPane.setMaximumSize(new Dimension(200, 38));
		kznr_valueJScrollPane.setMinimumSize(new Dimension(200, 38));
		kznr_valueJScrollPane.setPreferredSize(new Dimension(200, 38));
		kznr_valueJScrollPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

//		zlkzcx_value.setMaximumSize(new Dimension(200, 38));
//		zlkzcx_value.setMinimumSize(new Dimension(200, 38));
//		zlkzcx_value.setPreferredSize(new Dimension(200, 38));
//		zlkzcx_value.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

		zlkzcx_valueJScrollPane = new JScrollPane(zlkzcx_value,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		zlkzcx_valueJScrollPane.setMaximumSize(new Dimension(200, 38));
		zlkzcx_valueJScrollPane.setMinimumSize(new Dimension(200, 38));
		zlkzcx_valueJScrollPane.setPreferredSize(new Dimension(200, 38));
		zlkzcx_valueJScrollPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
	}

	private void initLayout() {
		setLayout(new GridBagLayout());
		add(bottomSplitPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));

		upPane.setLayout(new GridBagLayout());
		middlePanel.setLayout(new GridBagLayout());
		downPane.setLayout(new GridBagLayout());

		tablePanel.setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.add(stepFlagBox);
		panel.add(keyStepLabel);

		upPane.add(panel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 5, 5), 0, 0));

		// upPane.add(keyStepLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
		// GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
		// 5, 5, 5), 0, 0));

		//工序号
		upPane.add(label_1, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						10, 5, 5), 0, 0));

		upPane.add(technicsStepNumberField, new GridBagConstraints(3, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		//制造单位
		upPane.add(label_4, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						LEFTSPACE, 5, 5), 0, 0));

		upPane.add(workShopBox, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 5, 5), 0, 0));

		//工种
//		upPane.add(label_3, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0,
//				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
//						LEFTSPACE, 5, 5), 0, 0));
//
//		upPane.add(workTypeBox, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0,
//				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
//						5, 5, 5), 0, 0));

		//工位
//		upPane.add(label_5, new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0,
//				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
//						LEFTSPACE, 5, 5), 0, 0));
//
//		upPane.add(stepStation, new GridBagConstraints(9, 0, 1, 1, 0.0, 0.0,
//				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
//						5, 5, 5), 0, 0));

		//工序名称
		upPane.add(label_2, new GridBagConstraints(10, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						LEFTSPACE, 5, 5), 0, 0));

//		upPane.add(stepNameText, new GridBagConstraints(11, 0, 1, 1, 0.0,
//				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
//				new Insets(5, 5, 5, 5), 0, 0));

		upPane.add(stepNameComboBox, new GridBagConstraints(11, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		//是否强制执行
		upPane.add(enforceLabel, new GridBagConstraints(12, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(10, 25, 5, 5), 0, 0));
		upPane.add(enforceComboBox, new GridBagConstraints(13, 0, 3, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
				0, 5, 5), 0, 0));

		//操作星级
		upPane.add(operationLevelLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(10, 25, 5, 5), 0, 0));
		upPane.add(operationLevelComboBox, new GridBagConstraints(1, 1, 3, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));

		if(!EditorConfig.isZS) {
			//准结工时
			upPane.add(zjgsLabel, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.NONE,
					new Insets(10, LEFTSPACE, 5, 5), 0, 0));
			upPane.add(zjgsValue, new GridBagConstraints(5, 1, 1, 1, 0, 0,
					GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
					0, 5, 5), 0, 0));

			//单件工时
			upPane.add(djgsLabel, new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.NONE,
					new Insets(10, LEFTSPACE, 5, 5), 0, 0));
			upPane.add(djgsValue, new GridBagConstraints(8, 1, 3, 1, 0, 0,
					GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
					5, 5, 5), 0, 0));
		}

		//工序简述
		upPane.add(label_8, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(10, 25, 5, 5), 0, 0));

		upPane.add(label8_value, new GridBagConstraints(1, 2, 11, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));

		//工序内容
		upPane.add(label_6, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(LEFTSPACE, 25, 5, 5), 0, 0));

		//speCharPanel.setSize(new Dimension(200,50));
		JScrollPane js = new JScrollPane(speCharPanel,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//js.setSize(new Dimension(200,50));
		upPane.add(js, new GridBagConstraints(1, 3, 11, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));
		//单独编辑工序内容
		upPane.add(editButton, new GridBagConstraints(12, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 0, 5, 5), 0, 0));
//		String isTabular="";
//		XWTreeNode treeNode = frame.xwPartTreePanel.getSelectedTreeNode();
//		if (treeNode!=null) {
//		    XWTreeObject object = treeNode.getObject();
//		    if (object instanceof TechnicsMessageTreeObject) {
//		        TechnicsMessageTreeObject obj=(TechnicsMessageTreeObject) object;
//		        Element data = obj.getTreeCellData();
//		        isTabular= data.attributeValue("isTabular");
//            }
//        }
//		if ("表格化".equals(isTabular)) {

		    //装配检测结果
		    upPane.add(label_zpjcjg, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
		            GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		            new Insets(LEFTSPACE, 25, 5, 5), 0, 0));
		    upPane.add(zpjcjg, new GridBagConstraints(1, 4, 11, 1, 1.0, 0.0,
		            GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
		                    0, 5, 5), 0, 0));
//        }
		//检测操作指导说明
		upPane.add(label_7, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(LEFTSPACE, 25, 5, 5), 0, 0));

		upPane.add(operateInstructionJScrollPane, new GridBagConstraints(1, 5, 11, 1, 1.0, 0.5,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));

//		this.opIllustrationSpeCharPanel.setBackground(Color.white);
//		JMenuItem ocommonStringItem = new JMenuItem("插入工艺常用语");
//		JMenuItem osymbolItem = new JMenuItem("插入工艺符号");
//		this.opIllustrationSpeCharPanel.addItem(ocommonStringItem);
//		this.opIllustrationSpeCharPanel.addItem(osymbolItem);
//		osymbolItem.addActionListener(new SpeCharPanelSymbolListener(this.opIllustrationSpeCharPanel,this.frame));
//		ocommonStringItem.addActionListener(new SpeCharPanelCommonStringListener(this.opIllustrationSpeCharPanel,this.frame));
//
//		upPane.add(opIllustrationSpeCharPanel, new GridBagConstraints(1, 4, 11, 1, 1.0, 0.5,
//				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
//				0, 5, 5), 0, 0));

		//控制内容
		upPane.add(kznr_label, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(LEFTSPACE, 25, 5, 5), 0, 0));
		//modify by machongqi 2015-6-3
		 js2 = new JScrollPane(kznrspeCharPanel ,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		/*upPane.add(kznr_valueJScrollPane, new GridBagConstraints(1, 5, 11, 1, 1, 0.5,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));*/
		upPane.add(js2, new GridBagConstraints(1, 6, 11, 1, 1, 0.5,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));
		//质量控制程序
		upPane.add(zlkzcx_label, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(LEFTSPACE, 25, 5, 5), 0, 0));
		//modify by machongqi 2015-6-3
		 js3 = new JScrollPane(zlkzcxspeCharPanel  ,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		upPane.add(js3,new GridBagConstraints(1, 7, 11, 1, 1, 0.5,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));
		/*upPane.add(zlkzcx_valueJScrollPane,new GridBagConstraints(1, 6, 11, 1, 1, 0.5,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));*/

		//批次号
		upPane.add(pcno_label, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 25, 5, 5), 0, 0));

		pcno_box_value.addItem("");
		upPane.add(pcno_box_value, new GridBagConstraints(1, 8, 3, 1, 1, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));

		//操作者
		upPane.add(executor_label, new GridBagConstraints(4, 8, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 25, 5, 5), 0, 0));

		upPane.add(executor_value, new GridBagConstraints(5, 8, 2, 1, 1, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));


		//使用设备
		upPane.add(sysb_label, new GridBagConstraints(7, 8, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 25, 5, 5), 0, 0));

		upPane.add(sysb_value, new GridBagConstraints(8, 8, 3, 1, 1, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));

		//环境条件
		upPane.add(hjtj_label, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 25, 5, 5), 0, 0));

		upPane.add(hjtj_value, new GridBagConstraints(1, 9, 3, 1, 1, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));


		//检测工具
		upPane.add(jcgj_label, new GridBagConstraints(4, 9, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 25, 5, 5), 0, 0));

		upPane.add(jcgj_value, new GridBagConstraints(5, 9, 2, 1, 1, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));


		//控制图标
		upPane.add(kztb_label, new GridBagConstraints(7, 9, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 25, 5, 5), 0, 0));

		upPane.add(kztb_value, new GridBagConstraints(8, 9, 3, 1, 1, 0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						0, 5, 5), 0, 0));

		popupMenu.add(terminologyItem);


		middlePanel.add(tablePanel, new GridBagConstraints(0, 4, 6, 1, 1.0,
				1.0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH,
				new Insets(0, 5, 5, 5), 0, 0));

		equipJPanel = new NewEquipJPanel(this, frame);
		toolJPanel = new NewToolJPanel(this, frame);
		materialJPanel = new NewMaterialJPanel(this, frame);
		drawingJPanel = new NewDrawingJPanel(this, frame);
		knifeToolPanel = new NewKnifeToolPanel(this,frame);
		standardDashboardJPanel = new NewStandardDashboardJPanel(this,frame);
		unStandardDashboardJPanel = new NewUnStandardDashboardJPanel(this,frame);
		measureJPanel = new NewMeasureJPanel(this,frame);
		workspaceJPanel = new NewWorkSpaceJPanel(this,frame);
		attachJPanel = new NewAttachJPanel(this);
		additionalTableJPanel = new AdditionalTableJPanel(this);
		borrowThecnicsJPanel = new BorrowThecnicsJPanel(this);
		borrowThecnicsJPanel.setRelatedTypicalTechButtonEnable(false);
		largeFileJPanel = new LargeFileJPanel(this, frame);
		/** Add By Wangxl */
		commonParamTablePanel = new NewCommonParamTablePanel(this);
		specialParamTabbedPanel = new NewSpecialParamTabbedPanel(this);
//		checkParamTabbedPanel = new NewCheckParamTabbedPanel(this);
		sopFileTableJPanel = new SopFileTableJPanel(this,frame);
		partJPanel = new NewPartJPanel(this,frame);

		tabbedPane.addTab("设备", equipJPanel);
		tabbedPane.addTab("工装", toolJPanel);
		tabbedPane.addTab("工艺辅料", materialJPanel);
		tabbedPane.addTab("刀具", knifeToolPanel);
		tabbedPane.addTab("标准仪器仪表", standardDashboardJPanel);
		tabbedPane.addTab("非标准仪器仪表", unStandardDashboardJPanel);
		tabbedPane.addTab("量具", measureJPanel);
		if(!"SOP".equals(startType)){
			tabbedPane.addTab("简图", drawingJPanel);
		}
		tabbedPane.addTab("附件", attachJPanel);
		tabbedPane.addTab("工艺附表", additionalTableJPanel);
		if(!"SOP".equals(startType)){
			tabbedPane.addTab("典型/通用工艺、标准", borrowThecnicsJPanel);
		}
		tabbedPane.addTab("视频类大文件", largeFileJPanel);
		tabbedPane.addTab("工位", workspaceJPanel);

		/** Add By Wangxl */
		if(!"SOP".equals(startType)){
			tabbedPane.addTab("质量记录表", commonParamTablePanel);
			tabbedPane.addTab("特殊记录表", specialParamTabbedPanel);
			tabbedPane.addTab("引用SOP文件", sopFileTableJPanel);
			if(!EditorConfig.isZS ||EditorConfig.ACL_POSITIVE.contains(NewTechnicsPart.currentUser)){
				tabbedPane.addTab("参装件", partJPanel);
			}
		}
//		tabbedPane.addTab("检验记录表", checkParamTabbedPanel);
		/** End */
		/**  设置tab页选中时变色，add by liangbo*/
		tabbedPane.setForegroundAt(0, Color.RED);
		this.tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int index = tabbedPane.getSelectedIndex();
				if (index != -1) {
					for (int i = 0; i < tabbedPane.getTabCount(); i++) {
						if (index != i) {
							tabbedPane.setForegroundAt(i, Color.BLACK);
						}
					}
					tabbedPane.setForegroundAt(index, Color.RED);
				}
			}
		});
//		JScrollPane scrollPane = new JScrollPane(tabbedPane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tablePanel.add(tabbedPane, BorderLayout.CENTER);

		ptp = new PaceTablePane(frame, this);
		downPane.add(ptp, new GridBagConstraints(0, 0, 6, 1, 1.0, 1.0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH,
				new Insets(5, 5, 5, 5), 0, 0));

		initData();
	}

	private void hideIBAAttributes(){
		for(Component component:list) {
			component.setVisible(false);
		}
		list.clear();
	}

	private void setIBAAttributes(List<Component> compList,Element stepElement,String typeName) {
		for(Component component:compList) {
			String name = component.getName();
			if(name == null) {
				continue;
			}
			list.add(component);
			component.setVisible(true);
			if(component instanceof JTextField) {
				((JTextField) component).setText(XmlUtility.getHasEnterAttribute(stepElement, name));
			} else if (component instanceof JComboBox) {
				((JComboBox) component).setSelectedItem(XmlUtility.getHasEnterAttribute(stepElement, name));
			} else if (component instanceof JLabel) {
				((JLabel) component).setText(name);
			}
		}
	}

	public void showIBAAttributes(Element stepElement) {
		hideIBAAttributes();

		Document docNode = stepElement.getDocument();
		Element element = XmlUtility.getTechnicsElement(docNode);
		String typeName = element.attributeValue("technicsType");

		if(compMap.containsKey(typeName)) {
			setIBAAttributes(compMap.get(typeName),stepElement,typeName);
			return;
		}

		//attrs = TechnicsIntf.getMPMOperAttrByMPMPlan(typeName);

		if(xmlmap.isEmpty()) {
			try {
				xmlmap = TechnicsIntf.getMPMPPlanStepAttrByXML();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		List<Component> compList = new ArrayList<Component>();
		List<List<String>> allAttributes = xmlmap.get(typeName);
		row = 13;
		if(allAttributes != null && !allAttributes.isEmpty()) {
			JTextField textField = null;
			JComboBox comboBox = null;
			JLabel label = null;
			int col = 0;

			for (List<String> ibaList : allAttributes) {
				label = new JLabel(ibaList.get(1));
				label.setName(ibaList.get(1));
				list.add(label);
				compList.add(label);

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
					compList.add(textField);

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
					compList.add(comboBox);

				    upPane.add(comboBox, new GridBagConstraints(getGridx(col), row, getGridwidth(col++), 1, 1, 0,
							GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
									0, 5, 5), 0, 0));
				}

				if(col == 6) {
					col = 0;
					row++;
				}
			}
			compMap.put(typeName, compList);
		}
		this.upPane.repaint();
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

	private void initActions() {




//		cyy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, Event.CTRL_MASK));
//
//		cyy.addActionListener(new ActionListener() {
//
//			public void actionPerformed(ActionEvent e) {
//				new CyyDialog(frame);
//
//			  }
//			});

		stepNameComboBox.getEditor().getEditorComponent()
				.addFocusListener(new FocusListener() {
					public void focusGained(FocusEvent arg0) {
						if (frame != null && frame instanceof NewTechnicsPart) {
							NewTechnicsPart tp = (NewTechnicsPart) frame;
							tp.getPdNameTreePanel().deleteObservers();
							tp.getPdNameTreePanel().addObserver(stepNameComboBox);
							JTabbedPane treeTab = tp.getLeftTab();
							treeTab.setSelectedComponent(tp.getPdNameTreePanel());
							treeTab.updateUI();
						}
					}

					public void focusLost(FocusEvent arg0) {
					    String name = (String) stepNameComboBox.getSelectedItem();
					    if(!"".equals(name)&&name!=null){
					        String englishName = ResourceIntf.getEnglishNameByGxmc(name);
					        if(englishName==null){
	                             englishName="";
	                         }
					        XmlUtility.setAttributeValue(getStepElement(), "stepEnglishName", englishName);
					    }

					}
				});

//		stepNameText.addFocusListener(new FocusListener()
//			{
//				public void focusGained(FocusEvent arg0) {
//					logger.debug("表格获得焦点");
//					if (frame != null && frame instanceof NewTechnicsPart) {
//						NewTechnicsPart tp = (NewTechnicsPart) frame;
//						tp.getPdNameTreePanel().deleteObservers();
//						tp.getPdNameTreePanel().addObserver(stepNameText);
//						tp.getLeftTab().setSelectedComponent(tp.getPdNameTreePanel());
//					}
//				}
//
//				public void focusLost(FocusEvent arg0) {
//
//				}
//		});

//		speCharPanel.addMouseListener(new MouseAdapter() {
//			public void mouseReleased(MouseEvent e) {
//				/*if (e.getButton() == 3) {
//					opFiled = 1;
//					popupMenu.show(speCharPanel, e.getX(), e.getY());
//				}*/
//				if(e.getButton() == MouseEvent.BUTTON1){
//					System.out.println("1");
//					if(e.getClickCount()==2){
//						System.out.println("1");
//					}
//				}
//			}
//		});
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new EditTechnicsStepContent(speCharPanel, frame, "工序内容");
			}
		});


		speCharPanel.addFocusListener(new FocusListener()// 工具
				{
					public void focusGained(FocusEvent arg0) {
						logger.debug("表格获得焦点");
						opFiled = 1;
						if (frame != null && frame instanceof NewTechnicsPart) {
							NewTechnicsPart tp = (NewTechnicsPart) frame;
							tp.getCSTreePanel().deleteObservers();
							tp.getCSTreePanel().addObserver(speCharPanel);
							tp.getLeftTab().setSelectedComponent(tp.getCSTreePanel());
						}
					}

					public void focusLost(FocusEvent arg0) {

					}
				});

//		operateInstructionCharPanel.addMouseListener(new MouseAdapter() {
//			public void mouseClicked(MouseEvent e) {
//				if (e.getButton() == 3) {
//					opFiled = 2;
//					popupMenu.show(operateInstructionCharPanel, e.getX(), e.getY());
//				}
//			}
//		});
//
//		operateInstructionCharPanel.addFocusListener(new FocusListener()// 工具
//				{
//					public void focusGained(FocusEvent arg0) {
//						logger.debug("表格获得焦点");
//						opFiled = 2;
//						if (frame != null && frame instanceof NewTechnicsPart) {
//							NewTechnicsPart tp = (NewTechnicsPart) frame;
//							tp.getCSTreePanel().deleteObservers();
//							tp.getCSTreePanel().addObserver(operateInstructionCharPanel);
//							tp.getLeftTab().setSelectedComponent(
//									tp.getCSTreePanel());
//						}
//					}
//
//					public void focusLost(FocusEvent arg0) {
//
//					}
//				});

		terminologyItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					String terminologyXMLPath = WorkSpaceUtil
							.getPersonalTerminologyDirectory();
					CsSearchDialog dia = new CsSearchDialog(terminologyXMLPath,
							frame);
					String commonString = dia.showDialog();
					logger.debug("commonString====" + commonString);
					if (commonString == null) {
						commonString = "";
					}
					TextPane pane = null;
					if(opFiled==1){
						//pane = speCharPanel;
					}else if(opFiled==2) {
						//pane = operateInstructionCharPanel;
					}
					if(pane!=null){
						int loc = pane.getCaretPosition();
						if (loc >= 0) {
							pane.getDocument().insertString(loc,
									commonString, null);
						}
					}

				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		});



		tabbedPane.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (frame != null && frame instanceof NewTechnicsPart) {
					NewTechnicsPart tp = (NewTechnicsPart) frame;

					tp.getEpTreePanel().deleteObservers();
					tp.getEpTreePanel().addObserver(equipJPanel);// 设备

					tp.getResourceTreePanel().deleteObservers();
					tp.getResourceTreePanel().addObserver(toolJPanel);// 工装/工具

					tp.getMTPanel().deleteObservers();
					tp.getMTPanel().addObserver(materialJPanel);// 工艺辅料

					tp.getKtTreePanel().deleteObservers();
					tp.getKtTreePanel().addObserver(knifeToolPanel);//刀具

					tp.getsDashboardPanel().deleteObservers();
					tp.getsDashboardPanel().addObserver(standardDashboardJPanel);//标准仪器仪表

					tp.getUnSDashboardPanel().deleteObservers();
					tp.getUnSDashboardPanel().addObserver(unStandardDashboardJPanel);//非标准仪器仪表

					tp.getMeasurePanel().deleteObservers();
					tp.getMeasurePanel().addObserver(measureJPanel);

					int select = tabbedPane.getSelectedIndex();
					JTabbedPane treeTab = tp.getLeftTab();
					if (select == 0)// 设备
					{
						treeTab.setSelectedIndex(2);
					}
					if (select == 1)// 工装
					{
						treeTab.setSelectedIndex(3);
					}
					if (select == 2)// 材料
					{
						treeTab.setSelectedIndex(4);
					}
					if(select == 3) //刀具
					{
						treeTab.setSelectedIndex(5);
					}
					if(select == 4) //标准仪器仪表
					{
						treeTab.setSelectedIndex(6);
					}
					if(select == 5) //非标准仪器仪表
					{
						treeTab.setSelectedIndex(7);
					}
					if(select == 6) //量具
					{
						treeTab.setSelectedIndex(8);
					}
				}
			}
		});

		stepFlagBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(stepFlagBox.isSelected()){
					operateInstructionJScrollPane.setVisible(true);
					//opIllustrationSpeCharPanel.setVisible(true);
					label_7.setVisible(true);

					pcno_label.setVisible(true);
					//pcno_value.setVisible(true);
					pcno_box_value.setVisible(true);

					executor_label.setVisible(true);
					executor_value.setVisible(true);

					sysb_label.setVisible(true);
					sysb_value.setVisible(true);

					hjtj_label.setVisible(true);
					hjtj_value.setVisible(true);

					jcgj_label.setVisible(true);
					jcgj_value.setVisible(true);

					kztb_label.setVisible(true);
					kztb_value.setVisible(true);

					//add by machongqi 2015-6-3
					js2.setVisible(true);
					kznr_valueJScrollPane.setVisible(true);
					kznr_label.setVisible(true);
					kznr_value.setVisible(true);

					//add by machongqi 2015-6-3
					js3.setVisible(true);
					zlkzcx_valueJScrollPane.setVisible(true);
					zlkzcx_label.setVisible(true);
					zlkzcx_value.setVisible(true);

				}else {
					label_7.setVisible(false);
					//opIllustrationSpeCharPanel.setVisible(false);
					//opIllustrationSpeCharPanel.setText("");
					operateInstructionJScrollPane.setVisible(false);
					operateInstructionCharPanel.setText("");

					pcno_label.setVisible(false);
					//pcno_value.setVisible(false);
					//pcno_value.setText("");
					pcno_box_value.setVisible(false);
					pcno_box_value.setSelectedIndex(0);

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

					//add by machongqi 2015-6-3
					js2.setVisible(false);
					kznr_valueJScrollPane.setVisible(false);
					kznr_label.setVisible(false);
					kznr_value.setVisible(false);
					kznr_value.setText("");

					//add by machongqi 2015-6-3
					js3.setVisible(false);
					zlkzcx_valueJScrollPane.setVisible(false);
					zlkzcx_label.setVisible(false);
					zlkzcx_value.setVisible(false);
					zlkzcx_value.setText("");
				}
			}
		});

	}

	private void loadInitDatas() {
		try {
			csTypes = CommonStringIntf.justGetPublicCommonStrings();
			for(CsType csty:csTypes){
				for(String str:csty.getCommonStrings()){
					TechnicsCyy cyy = new TechnicsCyy();
					cyy.setName(str);
					cyy.setShortName(WorkproceduceUtil.convertStr(str));
//					System.out.println("shortname===="+cyy.getShortName());
					cyys.add(cyy);
			  }
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 取值
	public Element getElement() {
		ptp.stopTableCellEditing();
		Document doc = stepElement.getDocument();
		if (doc == null) {
			Document techDOC = ((NewTechnicsPart) frame).getCurrentTechnics();
			String bsoID = stepElement.attributeValue("bsoID");
			Element step = XmlUtility.getStepByID(XmlUtility.getTechnicsElement(techDOC), bsoID);
			if (step == null)
				return null;
			stepElement = step;
		}
		Element element = stepElement;
		String text = CommonUtil.trim(technicsStepNumberField.getText());
		text = XmlUtility.toSemiangle(text);
		if (text == null || text.trim().length() == 0) {
			text = stepNumber;
		} else {
			Element ele = XmlUtility.getStepByStepNumber(element.getParent().getParent(), text);
			if (ele != null) {
				String bsoID = ele.attributeValue("bsoID").trim();
				String curBsoID = element.attributeValue("bsoID").trim();
				if (!bsoID.equals(curBsoID)) {
					text = stepNumber;
				}
			}
//			else{
//			    if(text.toCharArray().length>2){
//				char[] chAr=text.toCharArray();
//				char[] newChar={chAr[chAr.length-2],chAr[chAr.length-1]};
//				String temp=String.valueOf(newChar);
//				Element ele1 = XmlUtility.getStepByStepNumber(element.getParent()
//						.getParent(), temp);
//				if (ele1 != null) {
//						JOptionPane.showMessageDialog(frame, "工序号 "+text+" 与 "+temp+" 重复 ");
//						text = stepNumber;
//				    }
//			     }
//		       }
		    }

		XmlUtility.setAttributeValue(element, "stepNumber", text);
		String selectedItem = (String) stepNameComboBox.getSelectedItem();
		Boolean stepNameFlag=false;
		 Set<String> stepSet = frame.allStepNameMap.keySet();
         for (String key:stepSet) {
             String stepName =  frame.allStepNameMap.get(key);
             if (stepName.equals(selectedItem)) {
                 stepNameFlag=true;
                 break;
             }

         }
         if (stepNameFlag) {
             XmlUtility.setAttributeValue(element, "stepName",(String) stepNameComboBox.getSelectedItem());
         }
		//XmlUtility.setAttributeValue(element, "stepName",(String) stepNameText.getText());
		XmlUtility.setAttributeValue(element, "GXJS", label8_value.getText());
		XmlUtility.setAttributeValue(element, "ZJGS", zjgsValue.getText());
		XmlUtility.setAttributeValue(element, "DJGS", djgsValue.getText());
		String type = (String) workTypeBox.getSelectedItem();
		if (type == null) {
			type = "";
		}
		XmlUtility.setAttributeValue(element, "workType", type);
		//保存操作星级 add by liangbo
		String operationLevel = operationLevelComboBox.getSelectedItem().toString();
		XmlUtility.setAttributeValue(element, "operationLevel", operationLevel);

		//保存是否强制执行
		String enforceValue = enforceComboBox.getSelectedItem().toString();
		XmlUtility.setAttributeValue(element,"enforce",enforceValue);

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
		boolean flag = stepFlagBox.isSelected();
		XmlUtility.setAttributeValue(element, "isKey", String.valueOf(flag));

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

		String speCharPanelText = speCharPanel.getText().replaceAll(CommonUtil.SPECIAL_SPACE, " ");
		speCharPanelText = SwitchUtil.filterSpecialChar(speCharPanelText);
		logger.debug(speCharPanelText);
		// String technicsPath = frame.getTechnicsTreePanel().getTechnicsPath();
		// String stepOid = stepElement.attributeValue("bsoID");
		// SpecialCharUtil.generateImage(speCharPanelText, technicsPath,
		// stepOid);
		speCharPanelText = HtmlGenerator.removeImageTags(speCharPanelText);
		speCharPanelText = CommonHelper.replaceSaveSeperator(speCharPanelText,imageFolder);
		XmlUtility.setProcedureContent(element, speCharPanelText);

		//保存控制内容
		String kznrspeCharPanelText = kznrspeCharPanel .getText().replaceAll(CommonUtil.SPECIAL_SPACE, " ");
		kznrspeCharPanelText = SwitchUtil.filterSpecialChar(kznrspeCharPanelText);
		logger.debug(kznrspeCharPanelText);
		kznrspeCharPanelText = HtmlGenerator.removeImageTags(kznrspeCharPanelText);
		kznrspeCharPanelText = CommonHelper.replaceSaveSeperator(kznrspeCharPanelText,imageFolder);
		XmlUtility.setKZNRContent(element, kznrspeCharPanelText);

		//保存质量控制程序
		String zlkzcxspeCharPanelText = zlkzcxspeCharPanel  .getText().replaceAll(CommonUtil.SPECIAL_SPACE, " ");
		zlkzcxspeCharPanelText = SwitchUtil.filterSpecialChar(zlkzcxspeCharPanelText);

		zlkzcxspeCharPanelText = HtmlGenerator.removeImageTags(zlkzcxspeCharPanelText);
		zlkzcxspeCharPanelText = CommonHelper.replaceSaveSeperator(zlkzcxspeCharPanelText,imageFolder);
		XmlUtility.setZLKZCXContent(element, zlkzcxspeCharPanelText);


		//TODO
		XmlUtility.setHasEnterAttribute(element, "operateInstruction", operateInstructionCharPanel.getText());
		//XmlUtility.setHasEnterAttribute(element, "operateInstruction", opIllustrationSpeCharPanel.getText());

		//TODO 149
		XmlUtility.setHasEnterAttribute(element, "PCNO", pcno_box_value.getSelectedItem().toString());
		XmlUtility.setHasEnterAttribute(element, "CZZ", executor_value.getText());
		XmlUtility.setHasEnterAttribute(element, "SYSB", sysb_value.getText());
		XmlUtility.setHasEnterAttribute(element, "HJTJ", hjtj_value.getText());
		XmlUtility.setHasEnterAttribute(element, "JCGJ", jcgj_value.getText());
		XmlUtility.setHasEnterAttribute(element, "KZTB", kztb_value.getText());
		XmlUtility.setHasEnterAttribute(element, "KZNR", kznr_value.getText());
		XmlUtility.setHasEnterAttribute(element, "ZLKZCX", zlkzcx_value.getText());
		XmlUtility.setHasEnterAttribute(element, "ZPJCJG", zpjcjg.getText());


		//保存IBA属性
		Element ibaElement = XmlUtility.getProcedureIBAAttriElement(element);
		XmlUtility.deleteAllChildElements(ibaElement);
		for(Component component:list) {
			if("null".equals(component.getName()) || "".equals(component.getName())
					|| component.getName() == null){
				continue;
			}
			String setVal = "";
			if(component instanceof JTextField) {
				setVal = ((JTextField) component).getText();
			} else if (component instanceof JComboBox) {
				setVal = ((JComboBox) component).getSelectedItem().toString();
			}

			XmlUtility.setHasEnterAttribute(element, component.getName(), setVal);
			if(!"".equals(attrsMap.get(component.getName())) && (attrsMap.get(component.getName()) != null)) {
				ibaElement.add(this.createProcedureIBAAttriElement(attrsMap.get(component.getName()),setVal));
			}

		}

		setLinkAtrributes(element);
		ptp.getElements();
		XmlUtility.reSetPaceNumbers(element);
		//保存通用质量记录表 add by liangbo
		commonParamTablePanel.getSaveButton().doClick();
		//保存特殊质量记录表 add by liangbo
		List<NewSpecialParamTablePanel> specialParamTablePanelList = new ArrayList<NewSpecialParamTablePanel>();
		specialParamTablePanelList = specialParamTabbedPanel.getSpecialParamTablePanelList();
		if(specialParamTablePanelList != null){
			for(NewSpecialParamTablePanel specialParamTablePanel : specialParamTablePanelList){
				specialParamTablePanel.getSaveButton().doClick();
			}
		}
		//保存检验记录表 add by zhuhao
//		List<NewCheckParamTablePanel> checkParamTablePanelList = new ArrayList<NewCheckParamTablePanel>();
//		checkParamTablePanelList = checkParamTabbedPanel.getCheckParamTablePanelList();
//		if(checkParamTablePanelList != null){
//			for(NewCheckParamTablePanel checkParamTablePanel : checkParamTablePanelList){
//				checkParamTablePanel.getSaveButton().doClick();
//			}
//		}
		return element;
	}

	private Element createProcedureIBAAttriElement(String key,String value) {
		Element element = DocumentHelper.createElement("attribute");
		XmlUtility.setAttributeValue(element, "key", key);
		XmlUtility.setAttributeValue(element, "value", value);

		return element;
	}

	// 取表格里的值
	private void setLinkAtrributes(Element stepElement) {
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
		// 工位
		Element workspaceElement = stepElement.element(XmlUtility.WORKSPACE_GROUP);
		if(workspaceElement == null) {
			workspaceElement = stepElement.addElement(XmlUtility.SDASHBOARD_GROUP);
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
		Vector<Element> knifeTool = knifeToolPanel.getElements();
		for (Iterator<Element> it = knifeTool.iterator(); it.hasNext();) {
			knifeToolElement.add(it.next());
		}

		// 材料
		Element materialElement = stepElement.element(XmlUtility.MATERIAL_GROUP);
		materialElement.elements().clear();
		Vector<Element> material = materialJPanel.getElements();
		for (Iterator<Element> it = material.iterator(); it.hasNext();) {
			materialElement.add(it.next());
		}

		// 简图 从参数中获取。。
		Element imageElement = stepElement.element(XmlUtility.IMAGE_GROUP);
		if(imageElement!=null){
			imageElement.elements().clear();
			Vector<Element> image = drawingJPanel.getElements();
			for (Iterator<Element> it = image.iterator(); it.hasNext();) {
				imageElement.add(it.next());
			}
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
		// 视频类大文件
		Element largeFileElement = stepElement.element("LargeFiles");
		if (largeFileElement == null) {
			largeFileElement = stepElement.addElement("LargeFiles");
		} else {
			largeFileElement.elements().clear();
		}
		Vector<Element> largeFileElements = largeFileJPanel.getElements();
		for (Iterator<Element> it = largeFileElements.iterator(); it.hasNext();) {
			largeFileElement.add(it.next());
		}
		//SOP
		Element relatedSopTech = stepElement.element(XmlUtility.SOP_GROUP);;
		if (relatedSopTech == null) {
			relatedSopTech = stepElement.addElement(XmlUtility.SOP_GROUP);
		} else {
			relatedSopTech.elements().clear();
		}
		Vector<Element> relatedSops = sopFileTableJPanel.getElements();
		for (Iterator<Element> it = relatedSops.iterator(); it.hasNext();) {
			relatedSopTech.add(it.next());
		}
	}

	// 设置值
	public void setUIValues(Element stepElement) {
		this.stepElement = stepElement;
		String isTabular="";
      XWTreeNode treeNode = frame.xwPartTreePanel.getSelectedTreeNode();
      if (treeNode!=null) {
          XWTreeObject object = treeNode.getObject();
          if (object instanceof TechnicsMessageTreeObject) {
              TechnicsMessageTreeObject obj=(TechnicsMessageTreeObject) object;
              Element data = obj.getTreeCellData();
              isTabular= data.attributeValue("isTabular");
          }
      }
      if ("表格化".equals(isTabular)) {
          label_zpjcjg.setVisible(true);
          zpjcjg.setVisible(true);
    }else{
        label_zpjcjg.setVisible(false);
        zpjcjg.setVisible(false);
    }

		if(stepElement != null){
			String stepNo = stepElement.attributeValue("stepNumber");
			int i = Integer.parseInt(stepNo);
			if(i >= 1000){
				technicsStepNumberField.setDocument(new InputLimited(4, true));
			}
		}
		technicsStepNumberField.setText(stepElement.attributeValue("stepNumber"));
		stepNumber = stepElement.attributeValue("stepNumber");
		String shop = stepElement.attributeValue("workShop");
		label8_value.setText(stepElement.attributeValue("GXJS"));
		zjgsValue.setText(stepElement.attributeValue("ZJGS"));
		djgsValue.setText(stepElement.attributeValue("DJGS"));
		Document techDOC1 = ((NewTechnicsPart) frame).getCurrentTechnics();
		techElement = XmlUtility.getTechnicsElement(techDOC1);

		/*
		 Element eleStep=techElement.element("steps");
		 List liststep=eleStep.elements();Element maxNumber=(Element)liststep.get(0);
        for(int i=1;i<liststep.size();i++){
        	Element element2=(Element)liststep.get(i);
        	if(Integer.valueOf(element2.attributeValue("stepNumber"))>Integer.valueOf(maxNumber.attributeValue("stepNumber"))){
        		maxNumber=element2;
        	}
        }
*/
		if (shop != null && shop.trim().length() > 0) {
			  workShopBox.setSelectedItem(shop);
			  workShopBox.setEnabled(true);
		} else {
			if(techElement != null) {
				String groupName = techElement.attributeValue("DEPT");
				if(groupName != null && !"".equals(groupName)) {
					workShopBox.setSelectedItem(groupName);
				} else {
					workShopBox.setSelectedIndex(0);
				}
			} else {
				workShopBox.setSelectedIndex(0);
			}
			workShopBox.setEnabled(true);
		}


		/*if(stepNumber.equals(maxNumber.attributeValue("stepNumber"))){
			String groupName = techElement.attributeValue("DEPT");
			if(groupName != null && !"".equals(groupName)) {
				workShopBox.setSelectedItem(groupName);
		      }else{
		    	 workShopBox.setSelectedIndex(0);
		      }
			     workShopBox.setEnabled(false);
		}*/

		stepNameComboBox.setSelectedItem(stepElement.attributeValue("stepName"));
		stepNameComboBox.reSetList();
		//设置操作星级 add by liangbo
		String operationLevel = stepElement.attributeValue("operationLevel");
		if(operationLevel == null || "".equals(operationLevel)){
			operationLevelComboBox.setSelectedItem("一星");
		}else{
			operationLevelComboBox.setSelectedItem(operationLevel);
		}

		//设置是否强制执行
		String enforceValue = stepElement.attributeValue("enforce");
		if(enforceValue == null || "".equals(enforceValue)){
			enforceComboBox.setSelectedItem("是");
		}else{
			enforceComboBox.setSelectedItem(enforceValue);
		}

		// 需要先设置制造单位，此下拉框触发监听，清除工种和工位的下拉框
		logger.debug("当前工序的制造单位为===:" + shop + ":===:" + workShopBox.getSelectedItem() + ":==");
		// 关键工序
		String isKey = stepElement.attributeValue("isKey");
		boolean flag = Boolean.parseBoolean(isKey);
		if (isKey != null && isKey.trim().length() != 0) {
			stepFlagBox.setSelected(flag);
		} else {
			stepFlagBox.setSelected(false);
		}
		// 特殊符号
		Document document = this.frame.getCurrentTechnics();
		if (document == null)
			return;
		Element techEle = XmlUtility.getTechnicsElement(document);

		imageFolder = WorkSpaceUtil.getTechnicsPath(techEle);
		speCharPanel.setTechnicsPath(imageFolder);
		ptp.setImageFolder(imageFolder);

//		checkParamTabbedPanel.setImageFolder(imageFolder);

		//speCharPanel.setTechnicsPath(WorkSpaceUtil.getTechnicsPath(techEle));
		speCharPanel.setText(CommonHelper.replaceReadSeperator(XmlUtility.getProcedureContent(stepElement),imageFolder));

		kznrspeCharPanel .setTechnicsPath(imageFolder);
		kznrspeCharPanel.setText(CommonHelper.replaceReadSeperator(XmlUtility.getKZNRContent(stepElement),imageFolder));

		zlkzcxspeCharPanel  .setTechnicsPath(imageFolder);
		zlkzcxspeCharPanel .setText(CommonHelper.replaceReadSeperator(XmlUtility.getZLKZCXContent(stepElement),imageFolder));

		//TODO 149
		operateInstructionCharPanel.setText(XmlUtility.getHasEnterAttribute(stepElement, "operateInstruction"));
		//opIllustrationSpeCharPanel.setText(XmlUtility.getHasEnterAttribute(stepElement, "operateInstruction"));

		//TODO 149
		//获取当前产品的批次号
		if(batchs == null) {
			if(techElement == null) {
				Document techDOC = ((NewTechnicsPart) frame).getCurrentTechnics();
				techElement = XmlUtility.getTechnicsElement(techDOC);
			}
			String productName = techElement.attributeValue("productName");
			try {
				batchs = TechnicsIntf.getBatchsByProductName(productName);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(batchs == null) {
				batchs = new Vector<String>();
				batchs.add("");
			}
		}
		pcno_box_value.removeAllItems();
		for (String str : batchs) {
			pcno_box_value.addItem(str);
		}
		//pcno_box_value = new JComboBox(batchs);
		String pc = XmlUtility.getHasEnterAttribute(stepElement, "PCNO");
		if(pc != null && batchs.contains(pc)) {
			pcno_box_value.setSelectedItem(pc);
		} else {
			pcno_box_value.setSelectedIndex(0);
		}
		//pcno_box_value.setSelectedItem(XmlUtility.getHasEnterAttribute(stepElement, "PCNO"));

		executor_value.setText(XmlUtility.getHasEnterAttribute(stepElement, "CZZ"));
		sysb_value.setText(XmlUtility.getHasEnterAttribute(stepElement, "SYSB"));
		hjtj_value.setText(XmlUtility.getHasEnterAttribute(stepElement, "HJTJ"));
		jcgj_value.setText(XmlUtility.getHasEnterAttribute(stepElement, "JCGJ"));
		kztb_value.setText(XmlUtility.getHasEnterAttribute(stepElement, "KZTB"));
		kznr_value.setText(XmlUtility.getHasEnterAttribute(stepElement, "KZNR"));
		zlkzcx_value.setText(XmlUtility.getHasEnterAttribute(stepElement, "ZLKZCX"));
		zpjcjg.setText(XmlUtility.getHasEnterAttribute(stepElement, "ZPJCJG"));

		//显示IBA属性
		showIBAAttributes(stepElement);

		//stepNameText.setText(XmlUtility.getHasEnterAttribute(stepElement, "stepName"));

		if(stepFlagBox.isSelected()){
			operateInstructionJScrollPane.setVisible(true);
			//opIllustrationSpeCharPanel.setVisible(true);
			label_7.setVisible(true);

			pcno_label.setVisible(true);
			//pcno_value.setVisible(true);
			pcno_box_value.setVisible(true);

			executor_label.setVisible(true);
			executor_value.setVisible(true);

			sysb_label.setVisible(true);
			sysb_value.setVisible(true);

			hjtj_label.setVisible(true);
			hjtj_value.setVisible(true);

			jcgj_label.setVisible(true);
			jcgj_value.setVisible(true);

			kztb_label.setVisible(true);
			kztb_value.setVisible(true);

			//add by machongqi 2015-6-3
			js2.setVisible(true);
			kznr_valueJScrollPane.setVisible(true);
			kznr_label.setVisible(true);
			kznr_value.setVisible(true);

			//add by machongqi 2015-6-3
			js3.setVisible(true);
			zlkzcx_valueJScrollPane.setVisible(true);
			zlkzcx_label.setVisible(true);
			zlkzcx_value.setVisible(true);
		}else{
			label_7.setVisible(false);
			operateInstructionJScrollPane.setVisible(false);
			//opIllustrationSpeCharPanel.setVisible(false);

			pcno_label.setVisible(false);
			//pcno_value.setVisible(false);
			pcno_box_value.setVisible(false);

			executor_label.setVisible(false);
			executor_value.setVisible(false);

			sysb_label.setVisible(false);
			sysb_value.setVisible(false);

			hjtj_label.setVisible(false);
			hjtj_value.setVisible(false);

			jcgj_label.setVisible(false);
			jcgj_value.setVisible(false);

			kztb_label.setVisible(false);
			kztb_value.setVisible(false);

			//add by machongqi 2015-6-3
			js2.setVisible(false);
			kznr_valueJScrollPane.setVisible(false);
			kznr_label.setVisible(false);
			kznr_value.setVisible(false);

			//add by machongqi 2015-6-3
			js3.setVisible(false);
			zlkzcx_valueJScrollPane.setVisible(false);
			zlkzcx_label.setVisible(false);
			zlkzcx_value.setVisible(false);
		}

		// 添加工位和工种
		String type = stepElement.attributeValue("workType");
		if (type != null && type.trim().length() > 0) {
			workTypeBox.setSelectedItem(type);
		} else {
			workTypeBox.setSelectedItem("");// 工位
		}
		logger.debug("当前工序的工种为===:" + type + ":===:" + workTypeBox.getSelectedItem() + ":==");

		String space = stepElement.attributeValue("workSpace");
		if (space != null && space.trim().length() > 0) {
			stepStation.setSelectedItem(space);// 工位
		} else {
			stepStation.setSelectedItem("");// 工位
		}
		logger.debug("当前工序的工位为===:" + space + ":===:" + stepStation.getSelectedItem() + ":==");
		setTableValues(stepElement);
		if (ptp != null) {
			ptp.setPaces(XmlUtility.getPaces(stepElement));
			ptp.displayPaceDatas();
		}

		equipJPanel.setTabTitle();
		measureJPanel.setTabTitle();
		workspaceJPanel.setTabTitle();
		toolJPanel.setTabTitle();
		materialJPanel.setTabTitle();
		drawingJPanel.setTabTitle();
		attachJPanel.setTabTitle();
		knifeToolPanel.setTabTitle();
		additionalTableJPanel.setTabTitle();
		this.standardDashboardJPanel.setTabTitle();
		this.unStandardDashboardJPanel.setTabTitle();

		if (frame != null && frame instanceof NewTechnicsPart) {
			NewTechnicsPart tp = (NewTechnicsPart) frame;
			Document doc = tp.getCurrentTechnics();
			if (doc != null) {
				drawingJPanel.setMiddleModelJButtonVisible(doc);
			}
		}
		commonParamTablePanel.setUIValues();
		specialParamTabbedPanel.setUIValues();
//		checkParamTabbedPanel.setUIValues();

	}

	// 设置表格里的值
	private void setTableValues(Element stepElement) {
		// 设备
		Element equipElement = stepElement.element(XmlUtility.EQUIP_GROUP);
		Vector<Element> equipVec = new Vector<Element>();
		for (Iterator<Element> it = equipElement.elementIterator(XmlUtility.EQUIP_TAG); it.hasNext();) {
			equipVec.add(it.next());
		}
		equipJPanel.setTableValues(equipVec);

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

		// 量具
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

		// 工具
		Element toolElement = stepElement.element(XmlUtility.TOOL_GROUP);
		Vector<Element> toolVec = new Vector<Element>();
		for (Iterator<Element> it = toolElement.elementIterator(XmlUtility.TOOL_TAG); it.hasNext();) {
			toolVec.add(it.next());
		}
		toolJPanel.setTableValues(toolVec);

		// SOP文件
		Element sopElement = stepElement.element(XmlUtility.SOP_GROUP);
		Vector<Element> sopVec = new Vector<Element>();
		if (sopElement != null) {
			for (Iterator<Element> it = sopElement.elementIterator(XmlUtility.SOP_TAG); it.hasNext(); ) {
				sopVec.add(it.next());
			}
		}
		sopFileTableJPanel.setTableValues(sopVec);

		// 刀具
		Element knifeToolElement = stepElement.element(XmlUtility.KNIFETOOL_GROUP);
		Vector<Element> knifeToolVec = new Vector<Element>();
		for (Iterator<Element> it = knifeToolElement.elementIterator(XmlUtility.KNIFETOOL_TAG); it.hasNext();) {
			knifeToolVec.add(it.next());
		}
		knifeToolPanel.setTableValues(knifeToolVec);

		// 材料
		Element materialElement = stepElement.element(XmlUtility.MATERIAL_GROUP);
		Vector<Element> materialVec = new Vector<Element>();
		for (Iterator<Element> it = materialElement.elementIterator(XmlUtility.MATERIAL_TAG); it.hasNext();) {
			materialVec.add(it.next());
		}
		materialJPanel.setTableValues(materialVec);

		// 简图
		Element imageElement = stepElement.element(XmlUtility.IMAGE_GROUP);
		if(imageElement!=null) {
			Vector<Element> imageVec = new Vector<Element>();
			for (Iterator<Element> it = imageElement.elementIterator(XmlUtility.IMAGE_TAG); it.hasNext(); ) {
				imageVec.add(it.next());
			}
			drawingJPanel.setTableValues(imageVec);
		}

		// 附件
		Element attachElement = stepElement.element("attachs");
		Vector<Element> attachElements = new Vector<Element>();
		logger.debug("attachElement===" + attachElement);
		if (attachElement != null) {
			for (Iterator<Element> it = attachElement.elementIterator("PAttachInfo"); it.hasNext();) {
				attachElements.add(it.next());
			}
			attachJPanel.setTableValues(attachElements);
		}
		// 工艺附表 add by liangbo
		List<Element> additionalTableElement = XmlUtility.getTechnicsAdditionTables(stepElement);
		Vector<Element> additionalTableList = new Vector<Element>();
		if (additionalTableElement != null)
			for (Element ee : additionalTableElement) {
				additionalTableList.add(ee);
			}
		additionalTableJPanel.setTableValues(additionalTableList);
		//典型/通用工艺、标准 add by liangbo
		List<Element> borrowThecnicsElement = XmlUtility.getBorrowTechnics(stepElement);
		Vector<Element> borrowThecnicsElements = new Vector<Element>();
		if(borrowThecnicsElement!=null)
			for (Element ee: borrowThecnicsElement) {
				borrowThecnicsElements.add(ee);
			}
		borrowThecnicsJPanel.setTableValues(borrowThecnicsElements);
		//视频类大文件 add by liangbo
		List<Element> largeFileElement = XmlUtility.getLargeFileElements(stepElement);
		Vector<Element> largeFileElements = new Vector<Element>();
		if(largeFileElement!=null)
			for (Element ee: largeFileElement) {
				largeFileElements.add(ee);
			}
		largeFileJPanel.setTableValues(largeFileElements);
		// 检验设备
//		Element checkEquipElement = stepElement.element(XmlUtility.CHECK_EQUIP_GROUP);
//		Vector<Element> checkEquipVec = new Vector<Element>();
//		for (Iterator<Element> it = checkEquipElement.elementIterator(XmlUtility.EQUIP_TAG); it.hasNext();) {
//			checkEquipVec.add(it.next());
//		}
//		equipJPanel.setTableValues(checkEquipVec);

		//参装件
		List<Element> parts = XmlUtility.getAllPartElement(stepElement);
		Vector<Element> partVec = new Vector<Element>();
		if (parts != null && parts.size()>0) {
			for(Element part : parts) {
				partVec.add(part);
			}
		}
		partJPanel.setTableValues(partVec);
	}

	// 检查输入是否合法
	public boolean check() throws Exception {
		if (technicsStepNumberField.getText().replaceAll("　", " ").toString().trim().equals("")) {
			JOptionPane.showMessageDialog(frame, "工序号不能为空!");
			return false;
		}

		String text = technicsStepNumberField.getText().replaceAll("　", " ").trim();
		text = XmlUtility.toSemiangle(text);
		logger.debug("转换后=======" + text);
		Document techDOC = ((NewTechnicsPart) frame).getCurrentTechnics();
		if (techDOC != null) {
			Element ele = XmlUtility.getStepByStepNumber(XmlUtility.getTechnicsElement(techDOC), text);
			if (ele != null) {
				String bsoID = ele.attributeValue("bsoID").trim();
				String curBsoID = stepElement.attributeValue("bsoID").trim();
				if (!bsoID.equals(curBsoID)) {
					text = stepNumber;
					JOptionPane.showMessageDialog(frame, "工序号重复!");
					return false;
				}
			}
		}
		/*if (stepFlagBox.isSelected()) {
			if (!ptp.hasKeyPace()) {
				//stepFlagBox.setSelected(false);
				//JOptionPane.showMessageDialog(frame, "关键工序至少应有一个控制点(G)工步!");
				//return false;
			}
		}*/
		return true;
	}

	public void clearUI() {
		logger.debug("----------TechnicsStepJPanel_XW.clearUI()-----------");
		stepElement = null;
		node = null;
		technicsStepNumberField.setText("");
		stepNameComboBox.setSelectedItem("");
		//stepNameText.setText("");
		workTypeBox.setSelectedItem("");
		workShopBox.setSelectedItem("");
		stepStation.setSelectedItem("");
		stepFlagBox.setSelected(false);
		speCharPanel.setText("");
		kznrspeCharPanel .setText("");
		zlkzcxspeCharPanel .setText("");
		equipJPanel.clearTable();
		measureJPanel.clearTable();
		workspaceJPanel.clearTable();
		standardDashboardJPanel.clearTable();
		unStandardDashboardJPanel.clearTable();
		toolJPanel.clearTable();
		materialJPanel.clearTable();
		drawingJPanel.clearTable();
		attachJPanel.clearTable();
		knifeToolPanel.clearTable();
		ptp.clearTable();

		imageFolder = null;
		speCharPanel.setTechnicsPath(imageFolder);
		kznrspeCharPanel.setTechnicsPath(imageFolder);
		zlkzcxspeCharPanel.setTechnicsPath(imageFolder);
		ptp.setImageFolder(null);
		//speCharPanel.setTechnicsPath(null);

		//pcno_value.setText("");
		pcno_box_value.setSelectedIndex(0);
		executor_value.setText("");
		sysb_value.setText("");
		hjtj_value.setText("");
		jcgj_value.setText("");
		kztb_value.setText("");
		kznr_value.setText("");
		zlkzcx_value.setText("");

		for(Component com:list) {
			if(com instanceof JTextField) {
				((JTextField) com).setText("");
			} else if (com instanceof JComboBox) {
				((JComboBox) com).setSelectedIndex(0);
			}
		}
	}

	public void setNode(XWTreeNode node) {
		this.node = node;
	}

	public XWTreeNode getNode() throws Exception {
		return node;
	}

	private void initData() {
		// 初始化制造单位下拉框
		workShop = ResourceIntf.getWorkShops();
//		logger.debug("制造单位=" + workShop);

		stepName = NewTechnicsPart.allStepNameMap;
		stepNameComboBox.setSelectedItem("");

		if (stepName != null && stepName.size() > 0) {
		Collection coll = stepName.values();
		Iterator it = coll.iterator();
		stepNameComboBox.removeAllItems();
		stepNameComboBox.addItem("");
		while (it.hasNext()) {
				String temp = (String) it.next();
				if (temp != null && temp.trim().length() > 0) {
					stepNameComboBox.addItem(temp);
					}
				}
			}


		if( workShopBox.getSelectedItem()!=null){
			workTypeBox.setSelectedItem("");
			workType = ResourceIntf.getSkill(getKey(workShop, workShopBox.getSelectedItem().toString()));
			if (workType != null && workType.size() > 0) {
				Collection coll = workType.values();
				Iterator it = coll.iterator();
				workTypeBox.removeAllItems();
				workTypeBox.addItem("");
				while (it.hasNext()) {
					String temp = (String) it.next();
					if (temp != null && temp.trim().length() > 0) {
						workTypeBox.addItem(temp);
					}
				}
		} else {
			workTypeBox.removeAllItems();
			workTypeBox.addItem("");
		}

			// 初始化工位数据
			workSpace = ResourceIntf.getWorkSpaces(getKey(workShop, workShopBox.getSelectedItem().toString()));
			logger.debug("工位========" + workSpace);
			stepStation.setSelectedItem("");
			if (workSpace != null && workSpace.size() > 0) {
				Collection coll = workSpace.values();
				Iterator it = coll.iterator();
				stepStation.removeAllItems();
				stepStation.addItem("");
				while (it.hasNext()) {
					String temp = (String) it.next();
					if (temp != null && temp.trim().length() > 0) {
						stepStation.addItem(temp);
					}
				}
			} else {
				stepStation.removeAllItems();
				stepStation.addItem("");
			}
		}

		workShopBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object item = workShopBox.getSelectedItem();
				if( workShopBox.getSelectedItem()!=null){

					workTypeBox.setSelectedItem("");
					workType = ResourceIntf.getSkill(getKey(workShop, workShopBox.getSelectedItem().toString()));
					if (workType != null && workType.size() > 0) {
						Collection coll = workType.values();
						Iterator it = coll.iterator();
						workTypeBox.removeAllItems();
						workTypeBox.addItem("");
						while (it.hasNext()) {
							String temp = (String) it.next();
							if (temp != null && temp.trim().length() > 0) {
								workTypeBox.addItem(temp);
							}
						}
				} else {
					workTypeBox.removeAllItems();
					workTypeBox.addItem("");
				}

					// 初始化工位数据
					workSpace = ResourceIntf.getWorkSpaces(getKey(workShop, workShopBox.getSelectedItem().toString()));
//					logger.debug("工位========" + workSpace);
					stepStation.setSelectedItem("");
					if (workSpace != null && workSpace.size() > 0) {
						Collection coll = workSpace.values();
						Iterator it = coll.iterator();
						stepStation.removeAllItems();
						stepStation.addItem("");
						while (it.hasNext()) {
							String temp = (String) it.next();
							if (temp != null && temp.trim().length() > 0) {
								stepStation.addItem(temp);
							}
						}
					} else {
						stepStation.removeAllItems();
						stepStation.addItem("");
					}
				}
			}
		});

		// 工序名称决定工种监听
		stepNameComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 初始化工种数据
				String item = (String) stepNameComboBox.getSelectedItem();
				String item1 = (String) workShopBox.getSelectedItem();
				if (item != null && item1 != null) {
					String workTypeID = getKey(workShop, item1);
					workType = ResourceIntf.getWorkTypeByPdName(item, workTypeID);
					logger.debug("工种========" + workType);
					if (workType != null && workType.size() > 0) {
						Collection coll = workType.values();
						Iterator it = coll.iterator();
						workTypeBox.removeAllItems();
						workTypeBox.addItem("");
						while (it.hasNext()) {
							String temp = (String) it.next();
							if (temp != null && temp.trim().length() > 0) {
								if (coll.size() == 1) {
									workTypeBox.addItem(temp);
									workTypeBox.setSelectedItem(temp);
								} else {
									workTypeBox.addItem(temp);
								}
							}
						}
					} else {
						workTypeBox.removeAllItems();
						workTypeBox.addItem("");
					}
					updateUI();
				}


			}

		});

		stepNameComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String name = "";
                String item = (String) stepNameComboBox.getSelectedItem();
                Document techDOC1 = ((NewTechnicsPart) frame).getCurrentTechnics();
                techElement = XmlUtility.getTechnicsElement(techDOC1);
                Element eleStep=techElement.element("steps");
                List<Element> liststep=eleStep.elements();
                for (Element element:liststep) {
                    String value1 = element.attributeValue("stepNumber");
                    if (stepNumber.equals(value1)) {
                         name=element.attributeValue("stepName");
                    }
                }
               // Element maxNumber=(Element)liststep.get(liststep.size()-1);
                Map<String, String> map = NewTechnicsPart.pdNameDescribeMap;
                Set<String> set = map.keySet();
                if (set.contains(item)) {
                    String zzcj = map.get(item);
                    if (!"".equals(zzcj)&&!"null".equals(zzcj)&&zzcj!=null) {
                      //  if (!stepNumber.equals(maxNumber.attributeValue("stepNumber"))) {
                            if (!name.equals(item)) {
                                workShopBox.setSelectedItem(zzcj);
                            }
                        //}
                    }
                    }
            }
        });
		if (workShop != null && workShop.size() > 0) {
            Collection<String> coll = workShop.values();
            Iterator<String> it = coll.iterator();
            workShopBox.removeAllItems();
            workShopBox.addItem("");
            List<String> allList = new ArrayList<String>();
            while (it.hasNext()) {
                String temp = (String) it.next();
                if (temp != null && temp.trim().length() > 0) {
                    allList.add(temp);
                    //workShopBox.addItem(temp);
                }
            }

            Collections.sort(allList);

            for (String string : allList) {
                workShopBox.addItem(string);
            }

            String groupName = "";
            try {
                groupName = TechnicsIntf.getUsertechnicsGroupName();
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if(allList.contains(groupName)) {
                workShopBox.setSelectedItem(groupName);
            }
        }
	}

	public NewEquipJPanel getEquipLinkPanel() {
		return equipJPanel;
	}

	public NewMeasureJPanel getMeasureJPanel() {
		return measureJPanel;
	}

	public NewWorkSpaceJPanel getWorkSpaceJPanel() {
		return workspaceJPanel;
	}

	public NewStandardDashboardJPanel getStandardDashboardJPanel() {
		return standardDashboardJPanel;
	}

	public NewUnStandardDashboardJPanel getUnStandardDashboardJPanel() {
		return unStandardDashboardJPanel;
	}

	public NewToolJPanel getToolLinkPanel() {
		return toolJPanel;
	}

	public NewKnifeToolPanel getKinfeToolPanel() {
		return knifeToolPanel;
	}

	public NewMaterialJPanel getMaterialLinkPanel() {
		return materialJPanel;
	}

	public SopFileTableJPanel getSopFileTableJPanel() {
		return sopFileTableJPanel;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
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

	public void save2(Map<String, Vector<List<String>>> map) {
		logger.debug("map=" + map);
		try {
			if (!check())
				return;
			Element newone = getElement();
			if (newone == null)
				return;
			NewTechnicsPart np = (NewTechnicsPart) frame;
			Document doc = stepElement.getDocument();
			String bsoID = stepElement.attributeValue("bsoID");
			Element techEle = XmlUtility.getTechnicsElement(doc);
			String technicsNumber = techEle.attributeValue("technicsNumber");
			String technicsName = techEle.attributeValue("technicsName");
			String technicsCategory = techEle
					.attributeValue("technicsCategory");
			String technicsPath = "";
			if ("rework".equals(technicsCategory)) {
				technicsPath = WorkSpaceUtil.getReworkTechnicsDirectory(
						technicsNumber, technicsName);
			} else if ("temp".equals(technicsCategory)) {
				technicsPath = WorkSpaceUtil.getTempTechnicsDirectory(
						technicsNumber, technicsName);
			} else {
				technicsPath = WorkSpaceUtil
						.getTechnicsDirectory(technicsNumber);
			}

			if (map != null) {
				// 获取step并从map中获取简图信息，保存进去
				Iterator it = map.keySet().iterator();
				// 清空已有的图形节点，重新保存图形节点
				while (it.hasNext()) {
					String stepOid = (String) it.next();
					Vector v = (Vector) map.get(stepOid);
					Element stepElement = XmlUtility.getStepByStepOid(techEle,
							stepOid);
					Element imgElement = XmlUtility.getImages(stepElement);
					List<Element> imageElements = imgElement
							.elements("PDrawingInfo");

					List<String> oids = new ArrayList<String>();
					for (int i = 0; i < v.size(); i++) {
						List<String> list = (List<String>) v.get(i);
						String oid = list.get(0);
						oids.add(oid);
					}

					if (imageElements != null && imageElements.size() != 0) {
						for (int i = imageElements.size() - 1; i >= 0; i--) {
							Element temp = imageElements.get(i);
							String type = temp.attributeValue("type");
							if (NewDrawingJPanel.MIDDLEMODEL_TYPE_NAME
									.equals(type)) {
								String oid = temp.attributeValue("bsoID");
								if (!oids.contains(oid)) {
									String path = temp
											.attributeValue("absolutePath");
									File file = new File(
											technicsPath
													+ File.separator
													+ path.substring(
															0,
															path.lastIndexOf(File.separator)));
									if (file.exists() && file.isDirectory()) {
										try {
											WorkSpaceUtil.delete(file);
										} catch (Exception e) {
											e.printStackTrace();
											JOptionPane.showMessageDialog(null,
													e.getMessage());
										}
										file.delete();
									}
								}
								imgElement.remove(temp);
							}
						}
					}

					for (int i = 0; i < v.size(); i++) {
						List<String> list = (List<String>) v.get(i);
						String oid = list.get(0);
						String path = list.get(1);
						String version = list.get(2);
						String modelName = list.get(3);
						File file = new File(technicsPath + File.separator
								+ path);
						if (file.exists() && file.isFile()) {
							// 取出附属信息
							String absolutePath = file.getAbsolutePath();
							String fileName = file.getName();
							String name, type;
							if (fileName.contains(".")) {
								name = fileName.split("\\.")[0];
								type = file.getName().split("\\.")[1];
							} else {
								name = fileName;
								type = "";
							}
							float size = 0;
							java.io.FileInputStream in = null;
							try {
								in = new FileInputStream(absolutePath);
								size = in.available();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} finally {
								CappJavaUtil.closeStream(in);
							}
							Element element = XmlUtility.createImage();
							XmlUtility.setAttributeValue(element, "bsoID", oid);
							XmlUtility.setAttributeValue(element,
									"drawingName", name);
							XmlUtility.setAttributeValue(element,
									"drawingType", type);
							XmlUtility.setAttributeValue(element,
									"drawingSize", size + "");
							XmlUtility.setAttributeValue(element,
									"absolutePath", path);
							XmlUtility.setAttributeValue(element, "imageOid",
									"");
							XmlUtility.setAttributeValue(element,
									"parentImageOid", "");
							XmlUtility.setAttributeValue(element, "type",
									NewDrawingJPanel.MIDDLEMODEL_TYPE_NAME);
							XmlUtility.setAttributeValue(element, "version",
									version);
							XmlUtility.setAttributeValue(element, "modelName",
									modelName);
							imgElement.add(element);
						}
					}
				}
			}

			// XmlUtility.orderSteps(techEle);
			np.saveProcess(techEle);
			((NewTechnicsPart) frame).getTechnicsTreePanel().recordTreeState();
			TechnicsTreePanel treePanel = ((NewTechnicsPart) frame)
					.getTechnicsTreePanel();
			XWTreeNode techNode = treePanel.getCurrentTechnicsNode();
			techNode.removeAllChildren();
			DefaultTreeModel model = (DefaultTreeModel) treePanel.getTree()
					.getModel();
			model.reload(techNode);
			((NewTechnicsPart) frame).expand(techNode);
			((NewTechnicsPart) frame).clearRightContent();
			XWTreeNode stepNode = treePanel.getStepNode(bsoID);
			if (stepNode != null) {
				treePanel.getTree().setSelectionPath(
						new TreePath(stepNode.getPath()));
				((NewTechnicsPart) frame).createTechnicsRoute();
			}
		} catch (Exception ee) {
			ee.printStackTrace();
			JOptionPane.showMessageDialog(frame, "保存过程中错误！", "提示",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void save(Map<String, Vector<String>> map) {
		try {
			if (!check())
				return;
			Element newone = getElement();
			if (newone == null)
				return;
			NewTechnicsPart np = (NewTechnicsPart) frame;
			Document doc = stepElement.getDocument();
			String bsoID = stepElement.attributeValue("bsoID");
			Element techEle = XmlUtility.getTechnicsElement(doc);
			String technicsNumber = techEle.attributeValue("technicsNumber");
			String technicsName = techEle.attributeValue("technicsName");
			String technicsCategory = techEle.attributeValue("technicsCategory");
			String technicsPath = "";
			if ("rework".equals(technicsCategory)) {
				technicsPath = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
			} else if ("temp".equals(technicsCategory)) {
				technicsPath = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
			} else {
				technicsPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
			}

			if (map != null) {
				// 获取step并从map中获取简图信息，保存进去
				Iterator it = map.keySet().iterator();
				// 清空已有的图形节点，重新保存图形节点
				while (it.hasNext()) {
					String stepNumber = (String) it.next();
					Vector v = (Vector) map.get(stepNumber);
					Element stepElement = XmlUtility.getStepByStepNumber(techEle, stepNumber);
					Element imgElement = XmlUtility.getImages(stepElement);
					Map<String,Element> oldEleMap = getImgEle(imgElement);
					imgElement.clearContent();

					for (int i = 0; i < v.size(); i++) {
						String path = (String) v.get(i);
						File file = new File(technicsPath + "\\" + path);
						if (file.exists() && file.isFile()) {
							// 取出附属信息
							String absolutePath = file.getAbsolutePath();
							String fileName = file.getName();
							if(oldEleMap.containsKey(fileName)) {
								imgElement.add(oldEleMap.get(fileName));
								continue;
							}
							String name, type;
							if (fileName.contains(".")) {
								name = fileName.split("\\.")[0];
								type = file.getName().split("\\.")[1];
							} else {
								name = fileName;
								type = "";
							}
							float size = 0;
							FileInputStream in = null;
							try {
								in = new FileInputStream(absolutePath);
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
							try {
								size = in.available();
								in.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							Element element = XmlUtility.createImage();
							XmlUtility.setAttributeValue(element, "bsoID", "");
							XmlUtility.setAttributeValue(element, "drawingName", name);
							XmlUtility.setAttributeValue(element, "drawingType", type);
							XmlUtility.setAttributeValue(element, "drawingSize", size + "");
							XmlUtility.setAttributeValue(element, "absolutePath", path);
							imgElement.add(element);
						}
					}
				}
			}

			XmlUtility.orderSteps(techEle);
			np.saveProcess(techEle);
			((NewTechnicsPart) frame).getTechnicsTreePanel().recordTreeState();
			TechnicsTreePanel treePanel = ((NewTechnicsPart) frame).getTechnicsTreePanel();
			XWTreeNode techNode = treePanel.getCurrentTechnicsNode();
			techNode.removeAllChildren();
			DefaultTreeModel model = (DefaultTreeModel) treePanel.getTree().getModel();
			model.reload(techNode);
			((NewTechnicsPart) frame).expand(techNode);
			((NewTechnicsPart) frame).clearRightContent();
			XWTreeNode stepNode = treePanel.getStepNode(bsoID);
			if (stepNode != null) {
				treePanel.getTree().setSelectionPath(new TreePath(stepNode.getPath()));
				((NewTechnicsPart) frame).createTechnicsRoute();
			}
		} catch (Exception ee) {
			ee.printStackTrace();
			JOptionPane.showMessageDialog(frame, "保存过程中错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private Map<String,Element> getImgEle(Element imgElement) {
		Map<String,Element> map = new HashMap<String,Element>();
		List oldList = imgElement.elements();
		if(oldList != null && !oldList.isEmpty()) {
			for(int i = 0;i<oldList.size();i++) {
				Element ele = (Element)oldList.get(i);
				String absolutePath = ele.attributeValue("absolutePath");
				String fileName = absolutePath.substring(absolutePath.lastIndexOf("/") + 1, absolutePath.length());
				map.put(fileName, ele);
			}
		}
		return map;
	}

	public void saveRefreshDrawing(Document doc) {
		try {
			if (!check())
				return;
			Element newone = getElement();
			if (newone == null)
				return;
			NewTechnicsPart np = (NewTechnicsPart) frame;
			String bsoID = stepElement.attributeValue("bsoID");
			Element techEle = XmlUtility.getTechnicsElement(doc);

			XmlUtility.orderSteps(techEle);
			np.saveProcess(techEle);
			((NewTechnicsPart) frame).getTechnicsTreePanel().recordTreeState();
			TechnicsTreePanel treePanel = ((NewTechnicsPart) frame).getTechnicsTreePanel();
			XWTreeNode techNode = treePanel.getCurrentTechnicsNode();
			techNode.removeAllChildren();
			DefaultTreeModel model = (DefaultTreeModel) treePanel.getTree().getModel();
			model.reload(techNode);
			((NewTechnicsPart) frame).expand(techNode);
			((NewTechnicsPart) frame).clearRightContent();
			XWTreeNode stepNode = treePanel.getStepNode(bsoID);
			if (stepNode != null) {
				treePanel.getTree().setSelectionPath(new TreePath(stepNode.getPath()));
				((NewTechnicsPart) frame).createTechnicsRoute();
			}
		} catch (Exception ee) {
			ee.printStackTrace();
			JOptionPane.showMessageDialog(frame, "保存过程中错误！", "提示",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public PaceTablePane getPaceTable() {
		return ptp;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void stopTableCellEditing() {
		equipJPanel.stopTableCellEditing();
		measureJPanel.stopTableCellEditing();
		workspaceJPanel.stopTableCellEditing();
		standardDashboardJPanel.stopTableCellEditing();
		unStandardDashboardJPanel.stopTableCellEditing();
		toolJPanel.stopTableCellEditing();
		materialJPanel.stopTableCellEditing();
		knifeToolPanel.stopTableCellEditing();
		sopFileTableJPanel.stopTableCellEditing();
		((NewTechnicsPart) frame).getQuickCreateProcedureJPanel().stopTableCellEditing();
	}

	private boolean isEdit = true;

	public void setUIEnabled(boolean b) {
		isEdit = b;
		technicsStepNumberField.setEnabled(b);
		stepNameComboBox.setEnabled(b);
		//stepNameText.setEnabled(b);
		workTypeBox.setEnabled(b);
//		workShopBox.setEnabled(b);
		stepFlagBox.setEnabled(b);
		stepStation.setEnabled(b);
		//speCharPanel.setEnabled(b);
		operateInstructionCharPanel.setEnabled(b);
		if(b) {
			speCharPanel.setBackground(Color.white);
			kznrspeCharPanel.setBackground(Color.white);
			zlkzcxspeCharPanel.setBackground(Color.white);
		}
		//stepContentSpeCharPanel.getTextPane().setEditable(b);
		if(!b) {
			speCharPanel.setEnabled(false);
			kznrspeCharPanel.setEnabled(false);
			zlkzcxspeCharPanel.setEnabled(false);
		} else {
			speCharPanel.setEnabled(true);
			kznrspeCharPanel.setEnabled(true);
			zlkzcxspeCharPanel.setEnabled(true);
		}
		//stepContentSpeCharPanel.setEnabled(b);
		equipJPanel.setUIEnabled(b);
		measureJPanel.setUIEnabled(b);
		workspaceJPanel.setUIEnabled(b);
		standardDashboardJPanel.setUIEnabled(b);
		unStandardDashboardJPanel.setUIEnabled(b);
		toolJPanel.setUIEnabled(b);
		knifeToolPanel.setUIEnabled(b);
		materialJPanel.setUIEnabled(b);
		drawingJPanel.setUIEnabled(b);
		attachJPanel.setUIEnabled(b);
		sopFileTableJPanel.setUIEnabled(b);
		ptp.setUIEnabled(b);
		partJPanel.setUIEnabled(b);

		//pcno_value.setEnabled(b);
		pcno_box_value.setEnabled(b);
		executor_value.setEnabled(b);
		sysb_value.setEnabled(b);
		hjtj_value.setEnabled(b);
		jcgj_value.setEnabled(b);
		kztb_value.setEnabled(b);
		kznr_value.setEnabled(b);
		zlkzcx_value.setEnabled(b);

		for(Component component:list) {
			component.setEnabled(b);
		}

		if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			additionalTableJPanel.setUIEnabled(b);
			borrowThecnicsJPanel.setUIEnabled(b);
			largeFileJPanel.setUIEnabled(b);
		}

	}
	public boolean checkOperationLevel(){
		String operationLevel = operationLevelComboBox.getSelectedItem().toString();
		if(operationLevel == null || "".equals(operationLevel)){
			System.out.println("--------操作星级为必填内容--------");
			return false;
		}
		return true;
	}

	public void update(Observable o, Object arg) {
		System.out.println("-------update-------");
		if (!isEdit)
			return;
		if (o != null && o instanceof CommonObservable) {
			CommonObservable co = (CommonObservable) o;
			int type = co.getType();
			logger.debug("type====" + type);
			if (type == 4)// 表示工序名
			{
				if (arg != null && arg instanceof String) {
					String commString = (String)arg;
					if (this.stepNameComboBox.isEnabled()) {
						if(!"检验".equals(stepNameComboBox.getSelectedItem().toString())) {//检验工序的名称不能改变
							stepNameComboBox.setSelectedItem(commString);
							updateUI();
						}
					}
				}
			}
		}
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
									panel.getDocument().insertString(loc,
											commonString, null);
								}
							}

						}
					}
				}
			} catch (Exception e) {

			}
		}
	}
//	class StepNameTextField extends JTextField implements Observer {
//		public void update(Observable o, Object arg) {
//			try {
//				if (o != null && o instanceof CommonObservable) {
//					CommonObservable co = (CommonObservable) o;
//					int type = co.getType();
//					if (type == 4)// 表示是工序
//					{
//						if (arg != null && arg instanceof String) {
//							String commonString = (String) arg;
//							if (commonString == null) {
//								commonString = "";
//							}
//							if(stepNameText!=null){
//								if(!"检验".equals(stepNameText.getText())) {//检验工序的名称不能改变
//									stepNameText.setText(commonString);
//								}
//							}
//
//						}
//					}
//				}
//			} catch (Exception e) {
//
//			}
//		}
//	}
	public static void main(String[] args) {
		try {
			System.setProperty("swing.useSystemFontSettings", "0");
			System.setProperty("swing.handleTopLevelPaint", "false");
			System.setProperty("-Dswing.aatext", "true");

			LookUtils.setLookAndTheme(new Plastic3DLookAndFeel(),
					new ExperienceBlue());
		} catch (Exception e) {

			e.printStackTrace();
		}
		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		methodServer.setUserName("ql");
		methodServer.setPassword("1");
		JFrame frame = new JFrame();
		frame.setSize(SwingUtil.SCREEN_WIDTH, SwingUtil.SCREEN_HEIGHT);
		frame.add(new TechnicsStepJPanel_XW(null));
		frame.setVisible(true);

	}

	public void refreshPartDatas() {
		this.partJPanel.clearTable();
		Element partElement = this.stepElement.element("parts");
		Vector partVec = new Vector();
		for (Iterator it = partElement.elementIterator("QMPartInfo"); it.hasNext();) {
			partVec.add((Element) it.next());
		}
		this.partJPanel.setTableValues(partVec);
	}

	public String getImageFolder() {
		return imageFolder;
	}

	public void setImageFolder(String imageFolder) {
		this.imageFolder = imageFolder;
	}
}