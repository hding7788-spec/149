package com.glaway.mpm.view;

import com.glaway.mpm.parameter.designui.*;
import com.glaway.mpm.parameter.listener.CheckParamTableButtonListener;
import com.glaway.mpm.pdf.HtmlGenerator;
import com.glaway.mpm.sop.view.SopFileTableJPanel;
import com.glaway.mpm.util.*;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import com.glaway.speciaword.common.CommonHelper;
import ext.casc.sop.util.StringUtil;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.*;

public class TechnicsPaceJDialog extends JFrame {
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private TechnicsStepJPanel_XW stepPanel;
	public Element stepElement;
	public Element paceElement;
	private Element result;
	private XWTreeNode node;
	private JTextField paceNumberField = new JTextField();
	private JTextField programNoField = new JTextField();
	private JTextField beizhuField = new JTextField();
	// private SpeCharPanel speCharPanel = new SpeCharPanel();
	private SpecialWordPanel editorPane;
	private SpecialWordPanel checkMethodPane;
	private SpecialWordPanel checkContentpane;
	private JCheckBox checkFlagBox = new JCheckBox();
	private JComboBox shop = new JComboBox();
	private JComboBox box = new JComboBox();
	private JCheckBox isKeyBox = new JCheckBox();
	private JCheckBox isKeyBox1 = new JCheckBox();
	private JCheckBox isKeyBox2 = new JCheckBox();
	private JCheckBox isKeyBox3 = new JCheckBox();
	private JCheckBox isKeyBox4 = new JCheckBox();
	private JCheckBox isKeyBox5 = new JCheckBox();
	private JCheckBox isKeyBox6 = new JCheckBox();
	private JCheckBox isKeyBox7 = new JCheckBox();
	private JCheckBox isKeyBox8 = new JCheckBox();
	private JCheckBox isKeyBox9 = new JCheckBox();
	private JPanel keyJPanel = new JPanel();

	private NewEquipJPanel equipJPanel;
	private NewToolJPanel toolJPanel;
	private NewKnifeToolPanel knifeToolJPanel;
	private NewMaterialJPanel materialJPanel;
	private JSplitPane bottomSplitPane = new JSplitPane();

	public NewDrawingJPanel getDrawingJPanel() {
		return drawingJPanel;
	}

	public void setDrawingJPanel(NewDrawingJPanel drawingJPanel) {
		this.drawingJPanel = drawingJPanel;
	}

	private NewPartJPanel partJPanel;
	private NewDrawingJPanel drawingJPanel;
	private NewStandardDashboardJPanel standardDashboardJPanel = null;
	private NewUnStandardDashboardJPanel unStandardDashboardJPanel = null;
	private NewMeasureJPanel measureJPanel = null;
	private SopFileTableJPanel sopFileTableJPanel;
	private BaiYuTestCheckFileTableJPanel baiYuTestCheckFileTableJPanel;
	private PhotoRecordTablePanel photoRecordTablePanel;
	private GongYiCanShuTableJPanel gongYiCanShuTableJPanel;

	private JTabbedPane tabbedPane = new JTabbedPane();
	public static final String procedureType = "procedureType";
	private static final String part = "参装件";
	private PaceTablePane ptp = null;

	private JButton button_2 = new IconButton("/images/button_save.png", "保存");
	private JButton button_3 = new IconButton("/images/button_add.png", "添加");
	private JButton editButton = new JButton("最大化工步内容");

	private Map workShop = null;
	private Map workType = null;
	private Map paceWorkType = null;
	private Element techEle = null;

	private String imageFolder;
	private AdditionalTableJPanel additionalTableJPanel = null;
	// private BorrowThecnicsJPanel borrowThecnicsJPanel = null;
	/** Add By Wangxl */
	private NewCommonParamTablePanel commonParamTablePanel = null;
	private NewSpecialParamTabbedPanel specialParamTabbedPanel = null;
	private NewCheckParamTabbedPanel checkParamTabbedPanel = null;

	private NewCheckResourcesTablePanel checkPanel = null;
	private PaceTablePane paceTablePane = null;
	private int row = 0;

	public TechnicsPaceJDialog(JFrame parent, TechnicsStepJPanel_XW ts, Element step, Element pace, final PaceTablePane paceTablePane, int row) {
		// super(parent, true);
		System.runFinalization();
		System.gc();
		String startType = com.glaway.mpm.EditorConfig.startType;
		this.paceTablePane = paceTablePane;
		this.row = row;
		this.frame = parent;
		this.stepPanel = ts;
		this.imageFolder = stepPanel.getImageFolder();
		editorPane = new SpecialWordPanel(frame, true, imageFolder);
		checkMethodPane = new SpecialWordPanel(frame, true, imageFolder);
		checkContentpane = new SpecialWordPanel(frame, true, imageFolder);
		this.equipJPanel = new NewEquipJPanel(this, frame);
		this.measureJPanel = new NewMeasureJPanel(this, frame);
		this.standardDashboardJPanel = new NewStandardDashboardJPanel(this, frame);
		this.unStandardDashboardJPanel = new NewUnStandardDashboardJPanel(this, frame);
		this.toolJPanel = new NewToolJPanel(this, frame);
		this.knifeToolJPanel = new NewKnifeToolPanel(this, frame);
		this.materialJPanel = new NewMaterialJPanel(this, frame);
		this.partJPanel = new NewPartJPanel(this, frame);
		this.drawingJPanel = new NewDrawingJPanel(this, frame);
		this.additionalTableJPanel = new AdditionalTableJPanel(this);
		// this.borrowThecnicsJPanel = new BorrowThecnicsJPanel(this);
		/** Add By Wangxl */
		this.commonParamTablePanel = new NewCommonParamTablePanel(this);
		this.specialParamTabbedPanel = new NewSpecialParamTabbedPanel(this);
		this.checkParamTabbedPanel = new NewCheckParamTabbedPanel(this);
		this.stepElement = step;
		this.checkPanel = new NewCheckResourcesTablePanel(this);
		sopFileTableJPanel = new SopFileTableJPanel(this, frame,this);
		baiYuTestCheckFileTableJPanel = new BaiYuTestCheckFileTableJPanel(this,frame);
		photoRecordTablePanel = new PhotoRecordTablePanel(this,frame);
		gongYiCanShuTableJPanel = new GongYiCanShuTableJPanel(this,frame);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				paceTablePane.dia = null;
				super.windowClosing(e);
			}
		});

		setLayout(new GridBagLayout());
		setIconImage(parent.getIconImage());
		setTitle("工步信息");
		this.paceElement = pace;
		// JPanel xXpanel = new JPanel();
		// xXpanel.setLayout(new GridBagLayout());
		// JScrollPane scrollPane1 = new JScrollPane(xXpanel,
		// JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		// JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// bottomSplitPane.setOneTouchExpandable(true);
		// bottomSplitPane.setDividerSize(10);
		// bottomSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		// bottomSplitPane.setLeftComponent(scrollPane1);

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
		getContentPane().add(label, gridBagConstraints_4);

		GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.fill = GridBagConstraints.NONE;
		gridBagConstraints_10.anchor = GridBagConstraints.WEST;
		gridBagConstraints_10.weightx = 1.0D;
		gridBagConstraints_10.gridx = 1;
		gridBagConstraints_10.gridy = 0;
		gridBagConstraints_10.gridwidth = 1;
		gridBagConstraints_10.insets = new Insets(5, 5, 5, 0);
		paceNumberField.setMaximumSize(new Dimension(100, 23));
		paceNumberField.setMinimumSize(new Dimension(100, 23));
		paceNumberField.setPreferredSize(new Dimension(100, 23));
		getContentPane().add(this.paceNumberField, gridBagConstraints_10);

		// “检验工步”监听器add by zhuhao 2017.10.18
		GridBagConstraints gridBagConstraints_26 = new GridBagConstraints();
		gridBagConstraints_26.fill = GridBagConstraints.NONE;
		gridBagConstraints_26.weightx = 0D;
		gridBagConstraints_26.anchor = GridBagConstraints.WEST;
		gridBagConstraints_26.gridx = 2;
		gridBagConstraints_26.gridy = 0;
		gridBagConstraints_26.insets = new Insets(5, 5, 5, 5);
		this.isKeyBox1.setText("检验工步");
		getContentPane().add(this.isKeyBox1, gridBagConstraints_26);
		String isCheck = paceElement.attributeValue("isCheck");
		this.isKeyBox1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String type = com.glaway.mpm.EditorConfig.startType;;
				if (TechnicsPaceJDialog.this.isKeyBox1.isSelected()) {
					TechnicsPaceJDialog.this.keyJPanel.setVisible(true);
					if (!"SOP".equals(type)) {
						// TechnicsPaceJDialog.this.tabbedPane.insertTab("检验资源",
						// null, checkPanel, "", 3);
						// TechnicsPaceJDialog.this.tabbedPane.insertTab("检验记录表",
						// null, checkParamTabbedPanel, "", 4);
						TechnicsPaceJDialog.this.tabbedPane.insertTab("检验资源", null, checkPanel, "",TechnicsPaceJDialog.this.tabbedPane.getTabCount());
//						TechnicsPaceJDialog.this.tabbedPane.insertTab("检验记录表", null, checkParamTabbedPanel, "", 13);
					}
				} else {
					List<NewCheckParamTablePanel> checkParamTablePanelList = checkParamTabbedPanel.getCheckParamTablePanelList();
					Element checkEle = paceElement.element("checkRecordTables");
					Element checkEquips = paceElement.element("checkEquips");
					Element checkSdashboard = paceElement.element("checkSdashboard");
					Element checkUnsdashboard = paceElement.element("checkUnsdashboard");
					Element checkMeasures = paceElement.element("checkMeasures");
					Element checkTools = paceElement.element("checkTools");
					Element checkMaterials = paceElement.element("checkMaterials");
					Element checkmethodContent = paceElement.element("checkmethodContent");
					Element checkcontentContent = paceElement.element("checkcontentContent");
					List<Integer> allRow = checkPanel.getRow();
					boolean isShow = false;
					if (checkParamTablePanelList != null && !checkParamTablePanelList.isEmpty()) {
						isShow = true;
					}
					for (int i = 0; i < allRow.size(); i++) {
						if (0 < allRow.get(i)) {
							isShow = true;
						}
					}
					String methodStr = CheckParamTableButtonListener.delHTMLTag(checkMethodPane.getText());
					String contentStr = CheckParamTableButtonListener.delHTMLTag(checkContentpane.getText());
					if (!"".equals(methodStr) || !"".equals(contentStr)) {
						isShow = true;
					}
					if (isShow) {
						int select = JOptionPane.showConfirmDialog(null, "检验工步存在内容，取消勾选会全部删除！", "提示", JOptionPane.YES_NO_OPTION);
						if (select == JOptionPane.YES_OPTION) {
							Element ele = getStepPanel().getTechElement();
							String technicsNumber = ele.attributeValue("technicsNumber");
							List<Element> eleList = new ArrayList<Element>();
							eleList.add(checkEle);
							eleList.add(checkEquips);
							eleList.add(checkSdashboard);
							eleList.add(checkUnsdashboard);
							eleList.add(checkMeasures);
							eleList.add(checkTools);
							eleList.add(checkMaterials);
							if (checkmethodContent != null) {
								checkmethodContent.clearContent();
							}
							checkMethodPane.setText("");
							if (checkcontentContent != null) {
								checkcontentContent.clearContent();
							}
							checkContentpane.setText("");
							if (eleList != null && !eleList.isEmpty()) {
								XmlUtility.removeAllCheckElement(ele, technicsNumber, eleList);
							}
							checkParamTabbedPanel.removeCheckParamTableType(checkParamTablePanelList);
							upDate();
						} else if (select == JOptionPane.NO_OPTION || select == -1) {
							isKeyBox1.setSelected(true);
							return;
						}
					}
					// 隐藏界面
					TechnicsPaceJDialog.this.keyJPanel.setVisible(false);
//					for (int i = 0; i < 2; i++) {
//						if (TechnicsPaceJDialog.this.tabbedPane.getTabCount() > 12) {
							TechnicsPaceJDialog.this.tabbedPane.removeTabAt(TechnicsPaceJDialog.this.tabbedPane.getTabCount() - 1);
//						}
//					}
				}
			}
		});
		JLabel programNolabel = new JLabel("程序号");
		programNolabel.setMaximumSize(new Dimension(100, 23));
		programNolabel.setMinimumSize(new Dimension(100, 23));
		programNolabel.setPreferredSize(new Dimension(100, 23));
		programNolabel.setHorizontalAlignment(4);
		GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.anchor = 18;
		gridBagConstraints_5.gridx = 3;
		gridBagConstraints_5.gridy = 0;
		gridBagConstraints_5.insets = new Insets(5, 10, 0, 0);
		getContentPane().add(programNolabel, gridBagConstraints_5);

		GridBagConstraints gridBagConstraints_100 = new GridBagConstraints();
		gridBagConstraints_100.fill = GridBagConstraints.NONE;
		gridBagConstraints_100.anchor = GridBagConstraints.WEST;
		gridBagConstraints_100.weightx = 1.0D;
		gridBagConstraints_100.gridx = 4;
		gridBagConstraints_100.gridy = 0;
		gridBagConstraints_100.gridwidth = 1;
		gridBagConstraints_100.insets = new Insets(5, 5, 5, 0);
		programNoField.setMaximumSize(new Dimension(100, 23));
		programNoField.setMinimumSize(new Dimension(100, 23));
		programNoField.setPreferredSize(new Dimension(100, 23));
		getContentPane().add(this.programNoField, gridBagConstraints_100);

		JLabel beizhulabel = new JLabel("备注");
		beizhulabel.setMaximumSize(new Dimension(100, 23));
		beizhulabel.setMinimumSize(new Dimension(100, 23));
		beizhulabel.setPreferredSize(new Dimension(100, 23));
		beizhulabel.setHorizontalAlignment(4);
		GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.anchor = 18;
		gridBagConstraints_6.gridx = 5;
		gridBagConstraints_6.gridy = 0;
		gridBagConstraints_6.insets = new Insets(5, 10, 0, 0);
		getContentPane().add(beizhulabel, gridBagConstraints_6);

		GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.fill = GridBagConstraints.NONE;
		gridBagConstraints_7.anchor = GridBagConstraints.WEST;
		gridBagConstraints_7.weightx = 1.0D;
		gridBagConstraints_7.gridx = 6;
		gridBagConstraints_7.gridy = 0;
		gridBagConstraints_7.gridwidth = 1;
		gridBagConstraints_7.insets = new Insets(5, 5, 5, 0);
		beizhuField.setMaximumSize(new Dimension(100, 23));
		beizhuField.setMinimumSize(new Dimension(100, 23));
		beizhuField.setPreferredSize(new Dimension(100, 23));
		getContentPane().add(this.beizhuField, gridBagConstraints_7);

		GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.fill = GridBagConstraints.NONE;
		gridBagConstraints_19.weightx = 0D;
		gridBagConstraints_19.anchor = GridBagConstraints.WEST;
		gridBagConstraints_19.gridx = 1;
		gridBagConstraints_19.gridy = 1;
		gridBagConstraints_19.insets = new Insets(5, 5, 5, 5);
		this.isKeyBox.setText("控制点(G)");
		getContentPane().add(this.isKeyBox, gridBagConstraints_19);

		GridBagConstraints gridBagConstraints_20 = new GridBagConstraints();
		gridBagConstraints_20.fill = GridBagConstraints.NONE;
		gridBagConstraints_20.weightx = 0D;
		gridBagConstraints_20.anchor = GridBagConstraints.WEST;
		gridBagConstraints_20.gridx = 2;
		gridBagConstraints_20.gridy = 1;
		gridBagConstraints_20.insets = new Insets(5, 5, 5, 5);
		this.isKeyBox2.setText("关键检验点");
		// isKeyBox2.setVisible(false);
		getContentPane().add(this.isKeyBox2, gridBagConstraints_20);

		GridBagConstraints gridBagConstraints_21 = new GridBagConstraints();
		gridBagConstraints_21.fill = GridBagConstraints.NONE;
		gridBagConstraints_21.weightx = 0D;
		gridBagConstraints_21.anchor = GridBagConstraints.WEST;
		gridBagConstraints_21.gridx = 3;
		gridBagConstraints_21.gridy = 1;
		gridBagConstraints_21.insets = new Insets(5, 5, 5, 5);
		this.isKeyBox3.setText("强制检验点");
		// isKeyBox3.setVisible(false);
		getContentPane().add(this.isKeyBox3, gridBagConstraints_21);

		GridBagConstraints gridBagConstraints_22 = new GridBagConstraints();
		gridBagConstraints_22.fill = GridBagConstraints.NONE;
		gridBagConstraints_22.weightx = 0D;
		gridBagConstraints_22.anchor = GridBagConstraints.WEST;
		gridBagConstraints_22.gridx = 4;
		gridBagConstraints_22.gridy = 1;
		gridBagConstraints_22.insets = new Insets(5, 5, 5, 5);
		this.isKeyBox4.setText("工艺关键特性");
		// isKeyBox4.setVisible(false);
		getContentPane().add(this.isKeyBox4, gridBagConstraints_22);

		GridBagConstraints gridBagConstraints_23 = new GridBagConstraints();
		gridBagConstraints_23.fill = GridBagConstraints.NONE;
		gridBagConstraints_23.weightx = 0D;
		gridBagConstraints_23.anchor = GridBagConstraints.WEST;
		gridBagConstraints_23.gridx = 5;
		gridBagConstraints_23.gridy = 1;
		gridBagConstraints_23.insets = new Insets(5, 5, 5, 5);
		this.isKeyBox5.setText("过程关键特性");
		// isKeyBox5.setVisible(false);
		getContentPane().add(this.isKeyBox5, gridBagConstraints_23);

		GridBagConstraints gridBagConstraints_24 = new GridBagConstraints();
		gridBagConstraints_24.fill = GridBagConstraints.NONE;
		gridBagConstraints_24.weightx = 0D;
		gridBagConstraints_24.anchor = GridBagConstraints.WEST;
		gridBagConstraints_24.gridx = 6;
		gridBagConstraints_24.gridy = 1;
		gridBagConstraints_24.insets = new Insets(5, 5, 5, 5);
		this.isKeyBox6.setText("多余物控制点");
		// isKeyBox5.setVisible(false);
		getContentPane().add(this.isKeyBox6, gridBagConstraints_24);

		GridBagConstraints gridBagConstraints_25 = new GridBagConstraints();
		gridBagConstraints_25.fill = GridBagConstraints.NONE;
		gridBagConstraints_25.weightx = 0D;
		gridBagConstraints_25.anchor = GridBagConstraints.WEST;
		gridBagConstraints_25.gridx = 1;
		gridBagConstraints_25.gridy = 2;
		gridBagConstraints_25.insets = new Insets(5, 5, 5, 5);
		this.isKeyBox7.setText("工艺关键检验点");
		// isKeyBox5.setVisible(false);
		getContentPane().add(this.isKeyBox7, gridBagConstraints_25);

		GridBagConstraints gridBagConstraints_sg = new GridBagConstraints();
		gridBagConstraints_sg.fill = GridBagConstraints.NONE;
		gridBagConstraints_sg.weightx = 0D;
		gridBagConstraints_sg.anchor = GridBagConstraints.WEST;
		gridBagConstraints_sg.gridx = 2;
		gridBagConstraints_sg.gridy = 2;
		gridBagConstraints_sg.insets = new Insets(5, 5, 5, 5);
		this.isKeyBox8.setText("双岗");
		getContentPane().add(this.isKeyBox8, gridBagConstraints_sg);

		GridBagConstraints gridBagConstraints_czd = new GridBagConstraints();
		gridBagConstraints_czd.fill = GridBagConstraints.NONE;
		gridBagConstraints_czd.weightx = 0D;
		gridBagConstraints_czd.anchor = GridBagConstraints.WEST;
		gridBagConstraints_czd.gridx = 3;
		gridBagConstraints_czd.gridy = 2;
		gridBagConstraints_czd.insets = new Insets(5, 5, 5, 5);
		this.isKeyBox9.setText("参装点");
		getContentPane().add(this.isKeyBox9, gridBagConstraints_czd);

		GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.fill = GridBagConstraints.NONE;
		gridBagConstraints_16.weightx = 0.0D;
		gridBagConstraints_16.anchor = GridBagConstraints.WEST;
		gridBagConstraints_16.gridx = 8;
		gridBagConstraints_16.gridy = 0;
		gridBagConstraints_16.insets = new Insets(5, 5, 5, 5);
		checkFlagBox.setText("检验工步");
		checkFlagBox.setVisible(false);
		// getContentPane().add(this.checkFlagBox, gridBagConstraints_16);

		this.checkFlagBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (TechnicsPaceJDialog.this.checkFlagBox.isSelected()) {
					TechnicsPaceJDialog.this.paceNumberField.setText("检");
					TechnicsPaceJDialog.this.paceNumberField.setEditable(false);
				} else {
					TechnicsPaceJDialog.this.paceNumberField.setEditable(true);
				}
			}
		});
		GridBagConstraints gridBagConstraints_editButton = new GridBagConstraints();
		gridBagConstraints_editButton.fill = GridBagConstraints.NONE;
		gridBagConstraints_editButton.weightx = 0D;
		gridBagConstraints_editButton.anchor = GridBagConstraints.WEST;
		gridBagConstraints_editButton.gridx = 4;
		gridBagConstraints_editButton.gridy = 2;
		gridBagConstraints_editButton.insets = new Insets(5, 5, 5, 5);
		getContentPane().add(this.editButton, gridBagConstraints_editButton);
		this.editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new EditTechnicsStepContent(TechnicsPaceJDialog.this.editorPane, TechnicsPaceJDialog.this, "工步内容");
			}
		});

		JPanel panel0 = new JPanel();
		panel0.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints_70 = new GridBagConstraints();
		gridBagConstraints_70.fill = 1;
		gridBagConstraints_70.anchor = 18;
		gridBagConstraints_70.weighty = 1.0D;
		gridBagConstraints_70.weightx = 1.0D;
		gridBagConstraints_70.gridx = 0;
		gridBagConstraints_70.gridy = 3;
		gridBagConstraints_70.gridwidth = 8;
		gridBagConstraints_70.insets = new Insets(0, 5, 5, 5);
		getContentPane().add(panel0, gridBagConstraints_70);

		JLabel label_5 = new JLabel("工步内容描述");
		label_5.setMaximumSize(new Dimension(100, 92));
		label_5.setMinimumSize(new Dimension(100, 92));
		label_5.setPreferredSize(new Dimension(100, 92));
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
		panel0.add(this.editorPane, gridBagConstraints_12);
		this.editorPane.setBackground(Color.white);

		// 新建keyJPanel add by zhuhao 2017.10.19
		keyJPanel.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints_71 = new GridBagConstraints();
		gridBagConstraints_71.fill = 1;
		gridBagConstraints_71.anchor = 12;
		gridBagConstraints_71.weighty = 2.0D;
		gridBagConstraints_71.weightx = 1.0D;
		gridBagConstraints_71.gridx = 0;
		gridBagConstraints_71.gridy = 4;
		gridBagConstraints_71.gridwidth = 8;
		gridBagConstraints_71.insets = new Insets(0, 5, 5, 5);
		getContentPane().add(keyJPanel, gridBagConstraints_71);
		TechnicsPaceJDialog.this.keyJPanel.setVisible(false);

		// 新增“检验方法描述”text页 add by zhuhao 2017.10.19
		JLabel label_8 = new JLabel("检验方法描述");
		label_8.setMaximumSize(new Dimension(100, 92));
		label_8.setMinimumSize(new Dimension(100, 92));
		label_8.setPreferredSize(new Dimension(100, 92));
		label_8.setHorizontalAlignment(4);
		GridBagConstraints gridBagConstraints_27 = new GridBagConstraints();
		gridBagConstraints_27.anchor = 18;
		gridBagConstraints_27.gridx = 0;
		gridBagConstraints_27.gridy = 1;
		gridBagConstraints_27.insets = new Insets(0, 5, 5, 5);
		keyJPanel.add(label_8, gridBagConstraints_27);

		GridBagConstraints gridBagConstraints_28 = new GridBagConstraints();
		gridBagConstraints_28.fill = 1;
		gridBagConstraints_28.anchor = 12;
		gridBagConstraints_28.weighty = 1.0D;
		gridBagConstraints_28.weightx = 1.0D;
		gridBagConstraints_28.gridx = 1;
		gridBagConstraints_28.gridy = 1;
		gridBagConstraints_28.insets = new Insets(0, 0, 5, 5);
		keyJPanel.add(this.checkMethodPane, gridBagConstraints_28);
		this.checkMethodPane.setBackground(Color.white);

		// 新增“检验内容描述”text页 add by zhuhao 2017.10.19
		JLabel label_9 = new JLabel("检验内容描述");
		label_9.setMaximumSize(new Dimension(100, 92));
		label_9.setMinimumSize(new Dimension(100, 92));
		label_9.setPreferredSize(new Dimension(100, 92));
		label_9.setHorizontalAlignment(4);
		GridBagConstraints gridBagConstraints_29 = new GridBagConstraints();
		gridBagConstraints_29.anchor = 18;
		gridBagConstraints_29.gridx = 0;
		gridBagConstraints_29.gridy = 2;
		gridBagConstraints_29.insets = new Insets(0, 5, 5, 5);
		keyJPanel.add(label_9, gridBagConstraints_29);

		GridBagConstraints gridBagConstraints_30 = new GridBagConstraints();
		gridBagConstraints_30.fill = 1;
		gridBagConstraints_30.anchor = 12;
		gridBagConstraints_30.weighty = 1.0D;
		gridBagConstraints_30.weightx = 1.0D;
		gridBagConstraints_30.gridx = 1;
		gridBagConstraints_30.gridy = 2;
		gridBagConstraints_30.insets = new Insets(0, 0, 5, 5);
		keyJPanel.add(this.checkContentpane, gridBagConstraints_30);
		this.checkContentpane.setBackground(Color.white);

		JPanel panel = new JPanel();
		// JScrollPane scrollPane2 = new JScrollPane(panel,
		// JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		// JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// bottomSplitPane.setRightComponent(scrollPane2);
		// bottomSplitPane.setDividerLocation(280);
		panel.setLayout(new BorderLayout());
		GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.fill = 1;
		gridBagConstraints_15.anchor = 12;
		gridBagConstraints_15.weighty = 1.0D;
		gridBagConstraints_15.weightx = 1.0D;
		gridBagConstraints_15.gridx = 1;
		gridBagConstraints_15.gridy = 5;
		gridBagConstraints_15.gridwidth = 7;
		gridBagConstraints_15.insets = new Insets(0, 0, 5, 5);
		getContentPane().add(panel, gridBagConstraints_15);

		panel.add(this.tabbedPane, "Center");
		if(!"SOP".equals(startType)){
			this.tabbedPane.addTab("设备", this.equipJPanel);
			this.tabbedPane.addTab("工装", this.toolJPanel);
			this.tabbedPane.addTab("材料", this.materialJPanel);
		}
		Document doc = ((NewTechnicsPart) this.frame).getCurrentTechnics();
		String techType = "";
		try {
			Element tech = XmlUtility.getTechnicsElement(doc);
			techType = tech.attributeValue("technicsType");
		} catch (Exception localException1) {
		}
		if ("SOP".equals(startType)) {
			this.tabbedPane.addTab("工步附图", this.additionalTableJPanel);
		}
		if (!"SOP".equals(startType)) {
			this.tabbedPane.addTab("参装件", this.partJPanel);
			this.tabbedPane.addTab("简图", this.drawingJPanel);
			this.tabbedPane.addTab("刀具",this.knifeToolJPanel);
			this.tabbedPane.addTab("标准仪器仪表", this.standardDashboardJPanel);
			this.tabbedPane.addTab("非标准仪器仪表", this.unStandardDashboardJPanel);
			this.tabbedPane.addTab("量具", this.measureJPanel);
			this.tabbedPane.addTab("工艺附表", this.additionalTableJPanel);
			// this.tabbedPane.addTab("典型/通用工艺、标准", borrowThecnicsJPanel);
			/** Add By Wangxl */
			this.tabbedPane.addTab("质量记录表", this.commonParamTablePanel);
			this.tabbedPane.addTab("特殊记录表", this.specialParamTabbedPanel);
			this.tabbedPane.addTab("检验记录表", this.checkParamTabbedPanel);
			this.tabbedPane.addTab("引用SOP文件", sopFileTableJPanel);
			this.tabbedPane.addTab("白羽检验记录表", this.baiYuTestCheckFileTableJPanel);
			this.tabbedPane.addTab("拍照点检验记录表", this.photoRecordTablePanel);
			this.tabbedPane.addTab("工艺参数", this.gongYiCanShuTableJPanel);

		}

		/** 设置tab页选中时变色，add by liangbo */
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

		JPanel panel_6 = new JPanel();
		panel_6.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.fill = 2;
		gridBagConstraints_9.anchor = 18;
		gridBagConstraints_9.weightx = 1.0D;
		gridBagConstraints_9.gridx = 0;
		gridBagConstraints_9.gridy = 6;
		gridBagConstraints_9.gridwidth = 8;
		gridBagConstraints_9.insets = new Insets(0, 5, 5, 5);
		getContentPane().add(panel_6, gridBagConstraints_9);

		GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.anchor = 14;
		gridBagConstraints_1.gridx = 2;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.insets = new Insets(0, 0, 5, 5);
		panel_6.add(this.button_2, gridBagConstraints_1);
		this.button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TechnicsPaceJDialog.this.save();
			}
		});
		GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.anchor = 14;
		gridBagConstraints_14.gridx = 1;
		gridBagConstraints_14.gridy = 0;
		gridBagConstraints_14.insets = new Insets(0, 0, 5, 5);
		panel_6.add(this.button_3, gridBagConstraints_14);
		this.button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					boolean bool = TechnicsPaceJDialog.this.check();
					System.out.println("BOOL=======" + bool);
					if (bool) {
						if (TechnicsPaceJDialog.this.paceElement != null) {
							TechnicsPaceJDialog.this.getElement();
							int select = TechnicsPaceJDialog.this.stepPanel.getPaceTable().getTable().getSelectedRow();
							TechnicsPaceJDialog.this.stepPanel.getPaceTable().setOneRowTableValue(TechnicsPaceJDialog.this.paceElement, select);
						} else {
							TechnicsPaceJDialog.this.stepPanel.getPaceTable().addProcess(TechnicsPaceJDialog.this.getElement());
						}
						TechnicsPaceJDialog.this.paceElement = null;
						TechnicsPaceJDialog.this.clearUI();
						TechnicsPaceJDialog.this.paceNumberField.setText(XmlUtility.nextPaceNumber(TechnicsPaceJDialog.this.stepElement) + "");
					}
				} catch (Exception ee) {
					ee.printStackTrace();
					JOptionPane.showMessageDialog(TechnicsPaceJDialog.this.frame, "保存过程中出现错误！", "提示", 1);
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
		gridBagConstraints.ipady = 0;
		gridBagConstraints.insets = new Insets(0, 0, 5, 0);
		panel_6.add(label_7, gridBagConstraints);

		JLabel label_6 = new JLabel("工步资源");
		label_6.setHorizontalAlignment(4);
		GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.anchor = 12;
		gridBagConstraints_8.gridx = 0;
		gridBagConstraints_8.gridy = 3;
		gridBagConstraints_8.insets = new Insets(0, 5, 5, 5);
		getContentPane().add(label_6, gridBagConstraints_8);

		if (this.paceElement != null)
			setUIValues(this.paceElement);
		else {
			this.paceNumberField.setText(XmlUtility.nextPaceNumber(this.stepElement) + "");
		}
		if ((this.frame != null) && ((this.frame instanceof NewTechnicsPart))) {
			NewTechnicsPart tp = (NewTechnicsPart) this.frame;
			Document document = tp.getCurrentTechnics();
			if (doc != null) {
				this.drawingJPanel.setMiddleModelJButtonVisible(document);
			}
		}

		TechnicsPaceJDialog.this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				commonParamTablePanel.getSaveButton().doClick();
				List<NewSpecialParamTablePanel> specialParamTablePanelList = new ArrayList<NewSpecialParamTablePanel>();
				specialParamTablePanelList = specialParamTabbedPanel.getSpecialParamTablePanelList();
				for (NewSpecialParamTablePanel specialParamTablePanel : specialParamTablePanelList) {
					specialParamTablePanel.getSaveButton().doClick();
				}
				boolean isCheck = isKeyBox1.isSelected();
				Element ele = getStepPanel().getTechElement();
				String technicsNumber = ele.attributeValue("technicsNumber");
				if (paceElement != null) {
					XmlUtility.isCheck(paceElement, String.valueOf(isCheck), ele, technicsNumber);// 是否检验记录表
				}
				List<NewCheckParamTablePanel> checkParamTablePanelList = new ArrayList<NewCheckParamTablePanel>();
				checkParamTablePanelList = checkParamTabbedPanel.getCheckParamTablePanelList();
				for (NewCheckParamTablePanel checkParamTablePanel : checkParamTablePanelList) {
					String name = checkParamTablePanel.getTabTableName();
					int row = checkParamTablePanel.getTable().getRowCount();
					if (row <= 0) {
						return;
					} else {
						checkParamTablePanel.getSaveButton().doClick();
					}
				}
				super.windowClosing(e);
			}
		});

		// 隐藏"检验记录表"
		if (tabbedPane.getTabCount() == 13) {
			tabbedPane.removeTabAt(12);
		}
		if ("true".equals(isCheck)) {
			isKeyBox1.setSelected(false);
			isKeyBox1.doClick();
		} else {
			isKeyBox1.setSelected(false);
		}

		/*
		 * JMenuBar jMenuBar1 = new JMenuBar(); JMenu scrMenu = new JMenu();
		 *
		 * JMenuItem fullScrMenuItem = new JMenuItem("最大化"); JMenuItem
		 * deoxidizeMenuItem = new JMenuItem("还原");
		 * //fullScrMenuItem.setAccelerator
		 * (javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER,
		 * java.awt.event.InputEvent.ALT_MASK));
		 *
		 * scrMenu.add(fullScrMenuItem ); scrMenu.add(deoxidizeMenuItem );
		 * jMenuBar1.add(fullScrMenuItem); jMenuBar1.add(deoxidizeMenuItem);
		 * this.setJMenuBar(jMenuBar1);
		 */
	}

	private void init() {
		this.workShop = ResourceIntf.getWorkShops();
		System.out.println("工步===车间========" + this.workShop);
		if ((this.workShop != null) && (this.workShop.size() > 0)) {
			Collection coll = this.workShop.values();
			Iterator it = coll.iterator();
			this.shop.removeAllItems();
			this.shop.addItem("");
			while (it.hasNext()) {
				String temp = (String) it.next();
				if ((temp != null) && (temp.trim().length() > 0)) {
					this.shop.addItem(temp);
				}
			}
			this.shop.setSelectedItem("");
		}

		this.shop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TechnicsPaceJDialog.this.box.removeAllItems();
				TechnicsPaceJDialog.this.box.addItem("");

				if ((TechnicsPaceJDialog.this.shop.getSelectedItem() != null) && (TechnicsPaceJDialog.this.shop.getSelectedItem().toString().trim().length() > 0)) {
					String shopId = (TechnicsPaceJDialog.this.getKey(TechnicsPaceJDialog.this.workShop, TechnicsPaceJDialog.this.shop.getSelectedItem().toString()));
					// TechnicsPaceJDialog.this.workType =
					// ResourceUtil.generateShopTypeMap(shopId);
					TechnicsPaceJDialog.this.workType = ResourceUtil.generate812ShopTypeMap(shopId);
					System.out.println("工种========" + TechnicsPaceJDialog.this.workType);

					if ((TechnicsPaceJDialog.this.workType != null) && (TechnicsPaceJDialog.this.workType.size() > 0)) {
						Collection coll = TechnicsPaceJDialog.this.workType.values();
						Iterator it = coll.iterator();
						while (it.hasNext()) {
							TechnicsPaceJDialog.this.box.addItem(it.next());
						}
					}
				}
				TechnicsPaceJDialog.this.box.setSelectedItem("");
			}

		});
	}

	public Element getElement() throws Exception {
		Element element = null;
		if (this.paceElement == null)
			element = XmlUtility.createProcedure();
		else
			element = this.paceElement;
		String num = this.paceNumberField.getText().replaceAll("　", "").trim();
		num = XmlUtility.toSemiangle(num);
		System.out.println("转换后=======" + num);
		XmlUtility.setAttributeValue(element, "stepNumber", num);
		XmlUtility.setAttributeValue(element, "stepName", "");

		String programNo = this.programNoField.getText();
		if (programNo != null && !"".equals(programNo)) {
			programNo = programNo.trim();
		} else {
			programNo = "";
		}
		XmlUtility.setAttributeValue(element, "programNo", programNo);

		String beizhu = this.beizhuField.getText();
		if (beizhu != null && !"".equals(beizhu)) {
			beizhu = beizhu.trim();
		} else {
			beizhu = "";
		}
		XmlUtility.setAttributeValue(element, "gbbz", beizhu);

		Object obj1 = this.shop.getSelectedItem();
		String sh = "";
		if (obj1 != null)
			sh = obj1.toString();
		XmlUtility.setAttributeValue(element, "workShop", sh);

		Object obj = this.box.getSelectedItem();
		String workType = "";
		if (obj != null)
			workType = obj.toString();
		XmlUtility.setAttributeValue(element, "workType", workType);

		this.workShop = ResourceIntf.getWorkShops();
		XmlUtility.setAttributeValue(element, "workShopID", getKey(this.workShop, element.attributeValue("workShop")));

		Map Type = ResourceUtil.generateShopTypeMap(element.attributeValue("workShopID"));
		XmlUtility.setAttributeValue(element, "workTypeID", getKey(Type, element.attributeValue("workType")));

		XmlUtility.setAttributeValue(element, "stepHour", "");
		XmlUtility.setAttributeValue(element, "isKey", String.valueOf(this.isKeyBox.isSelected()));
		XmlUtility.setAttributeValue(element, "isGJJYD", String.valueOf(this.isKeyBox2.isSelected()));
		XmlUtility.setAttributeValue(element, "isQZJYD", String.valueOf(this.isKeyBox3.isSelected()));
		XmlUtility.setAttributeValue(element, "isGYGJTX", String.valueOf(this.isKeyBox4.isSelected()));
		XmlUtility.setAttributeValue(element, "isGCGJTX", String.valueOf(this.isKeyBox5.isSelected()));
		XmlUtility.setAttributeValue(element, "isDYWKZD", String.valueOf(this.isKeyBox6.isSelected()));
		XmlUtility.setAttributeValue(element, "isGYGJJYD", String.valueOf(this.isKeyBox7.isSelected()));
		XmlUtility.setAttributeValue(element, "isShuangGang", String.valueOf(this.isKeyBox8.isSelected()));
		XmlUtility.setAttributeValue(element, "isCanZhuangDian", String.valueOf(this.isKeyBox9.isSelected()));
		if (this.checkFlagBox.isSelected())
			XmlUtility.setAttributeValue(element, "procedureType", "procedureType");
		else
			XmlUtility.setAttributeValue(element, "procedureType", "");

		String speCharPanelText = editorPane.getText().replaceAll(CommonUtil.SPECIAL_SPACE, " ");
		String checkMethodPaneText = checkMethodPane.getText().replaceAll(CommonUtil.SPECIAL_SPACE, " ");
		String checkContentpaneText = checkContentpane.getText().replaceAll(CommonUtil.SPECIAL_SPACE, " ");
		speCharPanelText = SwitchUtil.filterSpecialChar(speCharPanelText);
		checkMethodPaneText = SwitchUtil.filterSpecialChar(checkMethodPaneText);
		checkContentpaneText = SwitchUtil.filterSpecialChar(checkContentpaneText);
		speCharPanelText = HtmlGenerator.removeImageTags(speCharPanelText);
		speCharPanelText = CommonHelper.replaceSaveSeperator(speCharPanelText, imageFolder);
		checkMethodPaneText = HtmlGenerator.removeImageTags(checkMethodPaneText);
		checkMethodPaneText = CommonHelper.replaceSaveSeperator(checkMethodPaneText, imageFolder);
		checkContentpaneText = HtmlGenerator.removeImageTags(checkContentpaneText);
		checkContentpaneText = CommonHelper.replaceSaveSeperator(checkContentpaneText, imageFolder);
		XmlUtility.setProcedureContent(element, speCharPanelText);
		XmlUtility.setCheckMethodContent(element, checkMethodPaneText);
		XmlUtility.setCheckContentContent(element, checkContentpaneText);

		setLinkAtrributes(element);
		setCheckLinkAtrributes(element);
		return element;
	}

	private void setLinkAtrributes(Element paceElement) {
		// Element partElement = paceElement.element("parts");
		// List list1 = partElement.elements();
		// if (list1 != null)
		// list1.clear();
		// Vector part = this.partJPanel.getElements();
		// for (Iterator it = part.iterator(); it.hasNext();) {
		// partElement.add((Element) it.next());
		// }

		Element equipElement = paceElement.element("equips");
		List list2 = equipElement.elements();
		if (list2 != null)
			list2.clear();
		Vector equip = this.equipJPanel.getElements();
		for (Iterator it = equip.iterator(); it.hasNext();) {
			equipElement.add((Element) it.next());
		}

		// 量具
		Element measureElement = paceElement.element(XmlUtility.MEASURE_GROUP);
		if (measureElement == null) {
			measureElement = paceElement.addElement(XmlUtility.MEASURE_GROUP);
		} else {
			measureElement.elements().clear();
		}
		Vector<Element> measureVec = this.measureJPanel.getElements();
		for (Iterator<Element> it = measureVec.iterator(); it.hasNext();) {
			measureElement.add((Element) it.next());
		}

		// 标准仪器仪表
		Element sDashboardElement = paceElement.element(XmlUtility.SDASHBOARD_GROUP);
		if (sDashboardElement == null) {
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
		if (unsDashboardElement == null) {
			unsDashboardElement = paceElement.addElement(XmlUtility.UNSDASHBOARD_GROUP);
		} else {
			unsDashboardElement.elements().clear();
		}
		Vector<Element> unsDashboard = this.unStandardDashboardJPanel.getElements();
		for (Iterator<Element> it = unsDashboard.iterator(); it.hasNext();) {
			unsDashboardElement.add(it.next());
		}

		Element toolElement = paceElement.element("tools");
		List list3 = toolElement.elements();
		if (list3 != null)
			list3.clear();
		Vector tool = this.toolJPanel.getElements();
		for (Iterator it = tool.iterator(); it.hasNext();) {
			toolElement.add((Element) it.next());
		}

		Element knifeToolElement = paceElement.element("knifeTools");
		List knifes = knifeToolElement.elements();
		if (knifes != null)
			knifes.clear();
		Vector knifeTool = this.knifeToolJPanel.getElements();
		for (Iterator it = knifeTool.iterator(); it.hasNext();) {
			knifeToolElement.add((Element) it.next());
		}

		Element materialElement = paceElement.element("materials");
		List list4 = materialElement.elements();
		if (list4 != null)
			list4.clear();
		Vector material = this.materialJPanel.getElements();
		for (Iterator it = material.iterator(); it.hasNext();) {
			materialElement.add((Element) it.next());
		}

		Element imageElement = paceElement.element("images");
		List list5 = imageElement.elements();
		if (list5 != null)
			list5.clear();
		Vector image = this.drawingJPanel.getElements();
		for (Iterator it = image.iterator(); it.hasNext();) {
			imageElement.add((Element) it.next());
		}

		Element sopElement = paceElement.element(XmlUtility.SOP_GROUP);
		if (sopElement != null) {
			List sopList = sopElement.elements();
			if (sopList != null)
				sopList.clear();
			Vector sop = this.sopFileTableJPanel.getElements();
			for (Iterator it = sop.iterator(); it.hasNext();) {
				sopElement.add((Element) it.next());
			}
		}
	}

	// 保存检验资源
	public void setCheckLinkAtrributes(Element paceElement) {
		// 检验设备
		Element checkEquipElement = paceElement.element("checkEquips");
		if (checkEquipElement == null) {
			checkEquipElement = paceElement.addElement("checkEquips");
		} else {
			checkEquipElement.elements().clear();
		}
		List list = checkEquipElement.elements();
		if (list != null)
			list.clear();
		Vector checkEquip = this.checkPanel.getElements(1);
		for (Iterator it = checkEquip.iterator(); it.hasNext();) {
			checkEquipElement.add((Element) it.next());
		}
		// 检验标准仪器仪表
		Element sDashboardElement = paceElement.element(XmlUtility.CHECK_SDASHBOARD_GROUP);
		if (sDashboardElement == null) {
			sDashboardElement = paceElement.addElement(XmlUtility.CHECK_SDASHBOARD_GROUP);
		} else {
			sDashboardElement.elements().clear();
		}
		@SuppressWarnings("unchecked")
		Vector<Element> sDashboard = this.checkPanel.getElements(2);
		for (Iterator<Element> it = sDashboard.iterator(); it.hasNext();) {
			sDashboardElement.add(it.next());
		}
		// 检验非标准仪器仪表
		Element unsDashboardElement = paceElement.element(XmlUtility.CHECK_UNSDASHBOARD_GROUP);
		if (unsDashboardElement == null) {
			unsDashboardElement = paceElement.addElement(XmlUtility.CHECK_UNSDASHBOARD_GROUP);
		} else {
			unsDashboardElement.elements().clear();
		}
		@SuppressWarnings("unchecked")
		Vector<Element> unsDashboard = this.checkPanel.getElements(3);
		for (Iterator<Element> it = unsDashboard.iterator(); it.hasNext();) {
			unsDashboardElement.add(it.next());
		}
		// 检验量具
		Element measureElement = paceElement.element(XmlUtility.CHECK_MEASURE_GROUP);
		if (measureElement == null) {
			measureElement = paceElement.addElement(XmlUtility.CHECK_MEASURE_GROUP);
		} else {
			measureElement.elements().clear();
		}
		@SuppressWarnings("unchecked")
		Vector<Element> measureVec = this.checkPanel.getElements(4);
		for (Iterator<Element> it = measureVec.iterator(); it.hasNext();) {
			measureElement.add((Element) it.next());
		}
		// 检验工装
		Element toolElement = paceElement.element("checkTools");
		if (toolElement == null) {
			toolElement = paceElement.addElement("checkTools");
		} else {
			toolElement.elements().clear();
		}
		List list3 = toolElement.elements();
		Vector tool = this.checkPanel.getElements(5);
		for (Iterator it = tool.iterator(); it.hasNext();) {
			toolElement.add((Element) it.next());
		}
		// 检验工艺辅料
		Element materialElement = paceElement.element("checkMaterials");
		if (materialElement == null) {
			materialElement = paceElement.addElement("checkMaterials");
		} else {
			materialElement.elements().clear();
		}
		List list4 = materialElement.elements();
		Vector material = this.checkPanel.getElements(6);
		for (Iterator it = material.iterator(); it.hasNext();) {
			materialElement.add((Element) it.next());
		}
	}

	public void setUIValues(Element paceElement) {
		this.paceElement = paceElement;

		String paceWorkShop = paceElement.attributeValue("workShop");
		if ((paceWorkShop != null) && (paceWorkShop.trim().length() > 0)) {
			this.shop.setSelectedItem(paceWorkShop);
		}

		this.paceNumberField.setText(paceElement.attributeValue("stepNumber"));

		String programNo = paceElement.attributeValue("programNo");
		if (programNo == null) {
			programNo = "";
		}
		this.programNoField.setText(programNo);

		String beizhu = paceElement.attributeValue("gbbz");
		if (beizhu == null) {
			beizhu = "";
		}
		this.beizhuField.setText(beizhu);

		// 读取文本内容
		String speCharPanelText = CommonHelper.replaceReadSeperator(XmlUtility.getProcedureContent(paceElement), imageFolder);
		this.editorPane.setText(speCharPanelText);

		String checkMethodPaneText = CommonHelper.replaceReadSeperator(XmlUtility.getCheckMethodContent(paceElement), imageFolder);
		this.checkMethodPane.setText(checkMethodPaneText);

		String checkContentpaneText = CommonHelper.replaceReadSeperator(XmlUtility.getCheckContentContent(paceElement), imageFolder);
		this.checkContentpane.setText(checkContentpaneText);

		String isKey = paceElement.attributeValue("isKey");
		if ((isKey != null) && (isKey.trim().length() > 0)) {
			boolean bool = Boolean.parseBoolean(isKey);
			if (bool) {
				this.isKeyBox.setSelected(true);
			}
		}

		isKey = paceElement.attributeValue("isGJJYD");
		if ((isKey != null) && (isKey.trim().length() > 0)) {
			boolean bool = Boolean.parseBoolean(isKey);
			if (bool) {
				this.isKeyBox2.setSelected(true);
			}
		}

		isKey = paceElement.attributeValue("isQZJYD");
		if ((isKey != null) && (isKey.trim().length() > 0)) {
			boolean bool = Boolean.parseBoolean(isKey);
			if (bool) {
				this.isKeyBox3.setSelected(true);
			}
		}
		isKey = paceElement.attributeValue("isGYGJTX");
		if ((isKey != null) && (isKey.trim().length() > 0)) {
			boolean bool = Boolean.parseBoolean(isKey);
			if (bool) {
				this.isKeyBox4.setSelected(true);
			}
		}

		isKey = paceElement.attributeValue("isGCGJTX");
		if ((isKey != null) && (isKey.trim().length() > 0)) {
			boolean bool = Boolean.parseBoolean(isKey);
			if (bool) {
				this.isKeyBox5.setSelected(true);
			}
		}
		isKey = paceElement.attributeValue("isDYWKZD");
		if ((isKey != null) && (isKey.trim().length() > 0)) {
			boolean bool = Boolean.parseBoolean(isKey);
			if (bool) {
				this.isKeyBox6.setSelected(true);
			}
		}
		isKey = paceElement.attributeValue("isGYGJJYD");
		if ((isKey != null) && (isKey.trim().length() > 0)) {
			boolean bool = Boolean.parseBoolean(isKey);
			if (bool) {
				this.isKeyBox7.setSelected(true);
			}
		}
		isKey = paceElement.attributeValue("isShuangGang");
		if ((isKey != null) && (isKey.trim().length() > 0)) {
			boolean bool = Boolean.parseBoolean(isKey);
			if (bool) {
				this.isKeyBox8.setSelected(true);
			}
		}
		isKey = paceElement.attributeValue("isCanZhuangDian");
		if ((isKey != null) && (isKey.trim().length() > 0)) {
			boolean bool = Boolean.parseBoolean(isKey);
			if (bool) {
				this.isKeyBox9.setSelected(true);
			}
		}
		if (("procedureType").equals(paceElement.attributeValue("procedureType"))) {
			this.checkFlagBox.setSelected(true);
			this.paceNumberField.setText("检");
			this.paceNumberField.setEditable(false);
		}
		String paceWorkType = paceElement.attributeValue("workType");
		if ((paceWorkType != null) && (paceWorkType.trim().length() > 0)) {
			this.box.setSelectedItem(paceWorkType);
		}

		setTableValues(paceElement);
		setCheckTableValues(paceElement);

		this.equipJPanel.setTabTitle();
		this.measureJPanel.setTabTitle();
		this.standardDashboardJPanel.setTabTitle();
		this.unStandardDashboardJPanel.setTabTitle();
		this.toolJPanel.setTabTitle();
		this.knifeToolJPanel.setTabTitle();
		this.materialJPanel.setTabTitle();
		this.partJPanel.setTabTitle();
		this.drawingJPanel.setTabTitle();
		this.additionalTableJPanel.setTabTitle();
	}

	private void setTableValues(Element paceElement) {
		Element partElement = paceElement.element("parts");
		Vector partVec = new Vector();
		for (Iterator it = partElement.elementIterator("QMPartInfo"); it.hasNext();) {
			partVec.add((Element) it.next());
		}
		this.partJPanel.setTableValues(partVec);

		Element equipElement = paceElement.element("equips");
		Vector equipVec = new Vector();
		for (Iterator it = equipElement.elementIterator("QMEquipmentInfo"); it.hasNext();) {
			equipVec.add((Element) it.next());
		}
		this.equipJPanel.setTableValues(equipVec);

		// 量具
		Element measureElement = paceElement.element(XmlUtility.MEASURE_GROUP);
		Vector<Element> measureVec = new Vector<Element>();
		if (measureElement != null) {
			for (Iterator<Element> it = measureElement.elementIterator(XmlUtility.MEASURE_TAG); it.hasNext();) {
				measureVec.add(it.next());
			}
		}
		this.measureJPanel.setTableValues(measureVec);

		// 标准仪器仪表
		Element sDashboardElement = paceElement.element(XmlUtility.SDASHBOARD_GROUP);
		Vector<Element> sDashboardVec = new Vector<Element>();
		if (sDashboardElement != null) {
			for (Iterator<Element> it = sDashboardElement.elementIterator(XmlUtility.SDASHBOARD_TAG); it.hasNext();) {
				sDashboardVec.add(it.next());
			}
		}
		this.standardDashboardJPanel.setTableValues(sDashboardVec);

		// 非标准仪器仪表
		Element unsDashboardElement = paceElement.element(XmlUtility.UNSDASHBOARD_GROUP);
		Vector<Element> unsDashboardVec = new Vector<Element>();
		if (unsDashboardElement != null) {
			for (Iterator<Element> it = unsDashboardElement.elementIterator(XmlUtility.UNSDASHBOARD_TAG); it.hasNext();) {
				unsDashboardVec.add(it.next());
			}
		}
		this.unStandardDashboardJPanel.setTableValues(unsDashboardVec);

		Element toolElement = paceElement.element("tools");
		Vector toolVec = new Vector();
		for (Iterator it = toolElement.elementIterator("QMToolInfo"); it.hasNext();) {
			toolVec.add((Element) it.next());
		}
		this.toolJPanel.setTableValues(toolVec);

		// SOP文件
		Element sopElement = paceElement.element(XmlUtility.SOP_GROUP);
		if (sopElement != null) {
			Vector<Element> sopVec = new Vector<Element>();
			for (Iterator<Element> it = sopElement.elementIterator(XmlUtility.SOP_TAG); it.hasNext();) {
				sopVec.add(it.next());
			}
			this.sopFileTableJPanel.setTableValues(sopVec);
		}

		Element knifeToolElement = paceElement.element("knifeTools");
		Vector knifeToolVec = new Vector();
		for (Iterator it = knifeToolElement.elementIterator("QMKnifeToolInfo"); it.hasNext();) {
			knifeToolVec.add((Element) it.next());
		}
		this.knifeToolJPanel.setTableValues(knifeToolVec);

		Element materialElement = paceElement.element("materials");
		Vector materialVec = new Vector();
		for (Iterator it = materialElement.elementIterator("QMMaterialInfo"); it.hasNext();) {
			materialVec.add((Element) it.next());
		}
		this.materialJPanel.setTableValues(materialVec);

		Element imageElement = paceElement.element("images");
		Vector imageVec = new Vector();
		if(imageElement != null){
			for (Iterator it = imageElement.elementIterator("PDrawingInfo"); it.hasNext();) {
				imageVec.add((Element) it.next());
			}
		}
		Document doc = paceElement.getDocument();
		if (doc != null) {
			this.drawingJPanel.setTableValues(imageVec);
		}

		// 工艺附表 add by liangbo
		List<Element> additionalTableElement = XmlUtility.getTechnicsAdditionTables(paceElement);
		Vector<Element> additionalTableList = new Vector<Element>();
		if (additionalTableElement != null)
			for (Element ee : additionalTableElement) {
				additionalTableList.add(ee);
			}
		additionalTableJPanel.setTableValues(additionalTableList);
		// 典型/通用工艺、标准 add by liangbo
		List<Element> borrowThecnicsElement = XmlUtility.getBorrowTechnics(paceElement);
		Vector<Element> borrowThecnicsElements = new Vector<Element>();
		if (borrowThecnicsElement != null)
			for (Element ee : borrowThecnicsElement) {
				borrowThecnicsElements.add(ee);
			}
		// borrowThecnicsJPanel.setTableValues(borrowThecnicsElements);
		/** Add By Wangxl */
		commonParamTablePanel.setUIValues();
		specialParamTabbedPanel.setUIValues();
		checkParamTabbedPanel.setUIValues();
		List<Element> schemaDatas = XmlUtility.getSchemaDatas(paceElement);
		Vector<Element> schemaData = new Vector<Element>();
		if (schemaDatas != null){
			for (Element schema : schemaDatas) {
				schemaData.add(schema);
			}
		}
		baiYuTestCheckFileTableJPanel.setUIValues(schemaData);

		List<Element> photoDatas = XmlUtility.getPhotoElementsByOrder(paceElement);
		Vector<Element> photoData = new Vector<Element>();
		if (photoDatas != null){
			for (Element photo : photoDatas) {
				photoData.add(photo);
			}
		}
		photoRecordTablePanel.setUIValues(photoData);

		gongYiCanShuTableJPanel.setTableValues(paceElement,this.editorPane);
	}

	// 读取检验资源表格数据
	private void setCheckTableValues(Element paceElement) {
		// 检验设备
		Element equipElement = paceElement.element("checkEquips");
		if (equipElement == null)
			return;
		Vector equipVec = new Vector();
		for (Iterator it = equipElement.elementIterator("QMEquipmentInfo"); it.hasNext();) {
			equipVec.add((Element) it.next());
		}
		if (equipVec != null && !equipVec.isEmpty() && !this.isKeyBox1.isSelected())
			this.isKeyBox1.doClick();
		this.checkPanel.setTableValues(equipVec, 1);
		// 检验标准仪器仪表
		Element sDashboardElement = paceElement.element(XmlUtility.CHECK_SDASHBOARD_GROUP);
		if (sDashboardElement == null)
			return;
		Vector<Element> sDashboardVec = new Vector<Element>();
		if (sDashboardElement != null) {
			for (Iterator<Element> it = sDashboardElement.elementIterator(XmlUtility.SDASHBOARD_TAG); it.hasNext();) {
				sDashboardVec.add(it.next());
			}
		}
		if (sDashboardVec != null && !sDashboardVec.isEmpty() && !this.isKeyBox1.isSelected())
			this.isKeyBox1.doClick();
		this.checkPanel.setTableValues(sDashboardVec, 2);
		// 检验非标准仪器仪表
		Element unsDashboardElement = paceElement.element(XmlUtility.CHECK_UNSDASHBOARD_GROUP);
		if (unsDashboardElement == null)
			return;
		Vector<Element> unsDashboardVec = new Vector<Element>();
		if (unsDashboardElement != null) {
			for (Iterator<Element> it = unsDashboardElement.elementIterator(XmlUtility.UNSDASHBOARD_TAG); it.hasNext();) {
				unsDashboardVec.add(it.next());
			}
		}
		if (unsDashboardVec != null && !unsDashboardVec.isEmpty() && !this.isKeyBox1.isSelected())
			this.isKeyBox1.doClick();
		this.checkPanel.setTableValues(unsDashboardVec, 3);
		// 检验量具
		Element measureElement = paceElement.element(XmlUtility.CHECK_MEASURE_GROUP);
		Vector<Element> measureVec = new Vector<Element>();
		if (measureElement != null) {
			for (Iterator<Element> it = measureElement.elementIterator(XmlUtility.MEASURE_TAG); it.hasNext();) {
				measureVec.add(it.next());
			}
		}
		if (measureVec != null && !measureVec.isEmpty() && !this.isKeyBox1.isSelected())
			this.isKeyBox1.doClick();
		this.checkPanel.setTableValues(measureVec, 4);
		// 检验工装
		Element toolElement = paceElement.element("checkTools");
		Vector toolVec = new Vector();
		for (Iterator it = toolElement.elementIterator("QMToolInfo"); it.hasNext();) {
			toolVec.add((Element) it.next());
		}
		if (toolVec != null && !toolVec.isEmpty() && !this.isKeyBox1.isSelected())
			this.isKeyBox1.doClick();
		this.checkPanel.setTableValues(toolVec, 5);
		// 检验工艺辅料
		Element materialElement = paceElement.element("checkMaterials");
		Vector materialVec = new Vector();
		for (Iterator it = materialElement.elementIterator("QMMaterialInfo"); it.hasNext();) {
			materialVec.add((Element) it.next());
		}
		if (materialVec != null && !materialVec.isEmpty() && !this.isKeyBox1.isSelected())
			this.isKeyBox1.doClick();
		this.checkPanel.setTableValues(materialVec, 6);
	}

	public boolean check() throws Exception {
		if (this.isKeyBox2.isSelected() && this.isKeyBox3.isSelected()) {
			JOptionPane.showMessageDialog(this, "关键检验点和强制检验点只能二选一！", "提示", 1);
			return false;
		}

//		DefaultTableModel tableModel = baiYuTestCheckFileTableJPanel.getTableModel();
//		int rowCount = tableModel.getRowCount();
//		boolean isHasCheck = false;
//		boolean isHasCon = false;
//		String qiangZhiTableName = TechnicsIntf.getSystemConfiguration("QiangZhiTableName");
//		String guoChengTableName = TechnicsIntf.getSystemConfiguration("GuoChengTableName");
//		if(StringUtil.isEmpty(qiangZhiTableName)) {
//			qiangZhiTableName = "表A.2关键（强制）检验点控制情况检查表";
//		}
//		if(StringUtil.isEmpty(guoChengTableName)) {
//			guoChengTableName = "A.5产品过程控制关键特性表";
//		}
//		for(int i = 0; i < rowCount; i++) {
//			String name = (String) tableModel.getValueAt(i, 2);
//			if(qiangZhiTableName.equals(name)) {
//				isHasCheck = true;
//			} else if(guoChengTableName.equals(name)) {
//				isHasCon = true;
//			}
//		}
//
//		if(isHasCheck){
//			if (!this.isKeyBox2.isSelected() && !this.isKeyBox3.isSelected()) {
//				JOptionPane.showMessageDialog(this, "使用" + qiangZhiTableName + "需勾选强制检验点或关键检验点！", "提示", 1);
//				return false;
//			}
//		} else {
//			if (this.isKeyBox2.isSelected() || this.isKeyBox3.isSelected()) {
//				JOptionPane.showMessageDialog(this, "关键检验点或强制检验点未使用" + qiangZhiTableName + "！", "提示", 1);
//				return false;
//			}
//		}
//
//		if(isHasCon){
//			if (!this.isKeyBox5.isSelected()) {
//				JOptionPane.showMessageDialog(this, "使用" + guoChengTableName + "需勾选过程关键特性！", "提示", 1);
//				return false;
//			}
//		} else {
//			if (this.isKeyBox5.isSelected()) {
//				JOptionPane.showMessageDialog(this, "过程关键特性未使用" + guoChengTableName + "！", "提示", 1);
//				return false;
//			}
//		}



		if (this.paceNumberField.getText().replaceAll("　", "").trim().equals("")) {
			JOptionPane.showMessageDialog(this, "工步号为必填项,请输入！", "提示", 1);
			return false;
		}
		if(this.isKeyBox9.isSelected()){
			//todo
			Element steps = this.getStepElement().getParent();
			String nowPaceId =  XmlUtility.getAttributeValue(this.getPaceElement(),"bsoID");


			List<Element> allStep = steps.elements();
			for(Element step:allStep){
				String stepNumber =  XmlUtility.getAttributeValue(step,"stepNumber");
				List<Element> paces = XmlUtility.getAllPaces(step);
				for(Element pace :paces){
					String isCanZhuangDian = XmlUtility.getAttributeValue(pace,"isCanZhuangDian");
					String paceNumber =  XmlUtility.getAttributeValue(pace,"stepNumber");
					String paceId =  XmlUtility.getAttributeValue(pace,"bsoID");
					if(!paceId.equals(nowPaceId)){
						if("true".equals(isCanZhuangDian)){
							JOptionPane.showMessageDialog(this, stepNumber+"工序"+paceNumber+"工步已经定义过参装点，一份工艺只能设置一个参装点！", "提示", 1);
							return false;
						}
					}


				}

			}

		}

		String num = this.paceNumberField.getText().replaceAll("　", "").trim();
		num = XmlUtility.toSemiangle(num);
		System.out.println("转换后=======" + num);
		if (num.equals("检")) {
			if (!this.checkFlagBox.isSelected()) {
				JOptionPane.showMessageDialog(this, "非检验工步工步号不能为“检”！", "提示", 1);
				this.paceNumberField.setText(XmlUtility.nextPaceNumber(this.stepElement) + "");
				return false;
			}
		}
		if (XmlUtility.getPaceByPaceNumber(this.stepElement, num) != null) {
			if (this.paceElement != null) {
				String temp = XmlUtility.getAttributeValue(this.paceElement, "stepNumber");
				this.paceNumberField.setText(temp);
			} else {
				JOptionPane.showMessageDialog(this, "工步号为" + num + "的工步数据已存在，请重新填写工步号！", "提示", 1);
				return false;
			}
		}
		return true;
	}

	public void clearUI() {
		this.box.setSelectedItem("");
		this.isKeyBox.setSelected(false);
		this.isKeyBox1.setSelected(false);
		this.isKeyBox2.setSelected(false);
		this.isKeyBox3.setSelected(false);
		this.isKeyBox4.setSelected(false);
		this.isKeyBox5.setSelected(false);
		this.isKeyBox6.setSelected(false);
		this.isKeyBox7.setSelected(false);
		this.isKeyBox8.setSelected(false);
		this.isKeyBox9.setSelected(false);
		this.paceNumberField.setText("");
		this.programNoField.setText("");
		this.beizhuField.setText("");
		this.checkFlagBox.setSelected(false);
		this.editorPane.setText("");

		this.equipJPanel.clearTable();
		this.measureJPanel.clearTable();
		this.standardDashboardJPanel.clearTable();
		this.unStandardDashboardJPanel.clearTable();
		this.toolJPanel.clearTable();
		this.knifeToolJPanel.clearTable();
		this.materialJPanel.clearTable();
		this.partJPanel.clearTable();
		this.drawingJPanel.clearTable();

		this.equipJPanel.setTabTitle();
		this.measureJPanel.setTabTitle();
		this.standardDashboardJPanel.setTabTitle();
		this.unStandardDashboardJPanel.setTabTitle();
		this.toolJPanel.setTabTitle();
		this.knifeToolJPanel.setTabTitle();
		this.materialJPanel.setTabTitle();
		this.partJPanel.setTabTitle();
		this.drawingJPanel.setTabTitle();
		this.additionalTableJPanel.setTabTitle();
	}

	public void setNode(XWTreeNode node) {
		this.node = node;
	}

	public XWTreeNode getNode() {
		return this.node;
	}

	public void setPartVisible(String technicsType) {
		if (technicsType.equals("零件工艺")) {
			int index = this.tabbedPane.indexOfTab("参装件");
			if (index != -1) {
				this.tabbedPane.remove(index);
			}

		} else if (this.tabbedPane.getTabCount() == 4) {
			this.tabbedPane.insertTab("参装件", null, this.partJPanel, null, 3);
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

	public NewLinkJPanel getKnifeToolPanel() {
		return this.knifeToolJPanel;
	}

	public NewLinkJPanel getMaterialLinkPanel() {
		return this.materialJPanel;
	}

	public JTabbedPane getTabbedPane() {
		return this.tabbedPane;
	}

	public Element showDialog() {
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		setSize((int) (width * 0.6), (int) (height * 0.8));
		SwingUtil.setMiddle(this);
		setVisible(true);
		return this.result;
	}

	public String getKey(Map map, String value) {
		if ((map == null) || (value == null))
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

	public void save(boolean flag) {
		try {
			if (check()) {
				if (this.paceElement == null) {
					this.result = getElement();
				} else {
					getElement();
					commonParamTablePanel.getSaveButton().doClick();
					List<NewSpecialParamTablePanel> specialParamTablePanelList = new ArrayList<NewSpecialParamTablePanel>();
					specialParamTablePanelList = specialParamTabbedPanel.getSpecialParamTablePanelList();
					for (NewSpecialParamTablePanel specialParamTablePanel : specialParamTablePanelList) {
						specialParamTablePanel.getSaveButton().doClick();
					}
					List<NewCheckParamTablePanel> checkParamTablePanelList = new ArrayList<NewCheckParamTablePanel>();
					checkParamTablePanelList = checkParamTabbedPanel.getCheckParamTablePanelList();
					for (NewCheckParamTablePanel checkParamTablePanel : checkParamTablePanelList) {
						checkParamTablePanel.getSaveButton().doClick();
					}
					this.result = null;
				}
				if (flag) {
					dispose();
				}
				System.runFinalization();
				System.gc();
			}

		} catch (Exception ee) {
			ee.printStackTrace();
			JOptionPane.showMessageDialog(this.frame, "保存过程中出现错误！", "提示", 1);
		}
	}

	public void save() {// 保存按钮
		try {
			boolean bool = check();
			System.out.println("BOOL=======" + bool);
			if (bool) {
				if (this.paceElement == null) {
					this.result = getElement();
					if (paceTablePane != null) {
						paceTablePane.addProcess(this.result);
					}
				} else {
					getElement();
					// 保存通用质量记录表 add by liangbo
					commonParamTablePanel.getSaveButton().doClick();
					// 保存特殊质量记录表 add by liangbo
					List<NewSpecialParamTablePanel> specialParamTablePanelList = specialParamTabbedPanel.getSpecialParamTablePanelList();
					if(specialParamTablePanelList!=null){
						for (NewSpecialParamTablePanel specialParamTablePanel : specialParamTablePanelList) {
							specialParamTablePanel.getSaveButton().doClick();
						}
					}

					// 保存检验记录表 add by zhuhao
					boolean isCheck = isKeyBox1.isSelected();
					Element ele = getStepPanel().getTechElement();
					String technicsNumber = ele.attributeValue("technicsNumber");
					XmlUtility.isCheck(paceElement, String.valueOf(isCheck), ele, technicsNumber);// 是否检验记录表
					List<NewCheckParamTablePanel> checkParamTablePanelList = checkParamTabbedPanel.getCheckParamTablePanelList();
					for (NewCheckParamTablePanel checkParamTablePanel : checkParamTablePanelList) {
						String name = checkParamTablePanel.getTabTableName();
						int row = checkParamTablePanel.getTable().getRowCount();
						if (row <= 0) {
							int select = JOptionPane.showConfirmDialog(null, "表名为 [ " + name + " ] 的表没有任何编辑，不能保存！", "提示", JOptionPane.YES_NO_OPTION);
							if (select == JOptionPane.YES_OPTION) {

							} else if (select == JOptionPane.NO_OPTION) {
								return;
							}
						} else {
							checkParamTablePanel.getSaveButton().doClick();
						}
					}
					this.result = null;
					if (paceTablePane != null) {
						paceTablePane.setOneRowTableValue(paceElement, row);
					}
				}
				paceTablePane.dia = null;
				dispose();
				System.runFinalization();
				System.gc();
			}
		} catch (Exception ee) {
			ee.printStackTrace();
			JOptionPane.showMessageDialog(this.frame, "保存过程中出现错误！", "提示", 1);
		}
	}

	public JFrame getFrame() {
		return this.frame;
	}

	public void refreshPartDatas() {
		this.partJPanel.clearTable();
		Element partElement = this.paceElement.element("parts");
		Vector partVec = new Vector();
		for (Iterator it = partElement.elementIterator("QMPartInfo"); it.hasNext();) {
			partVec.add((Element) it.next());
		}
		this.partJPanel.setTableValues(partVec);
	}

	public void setUIEnabled(boolean b) {
		this.paceNumberField.setEnabled(b);
		this.programNoField.setEnabled(b);
		this.beizhuField.setEnabled(b);
		this.editorPane.setEnabled(b);
		this.checkFlagBox.setEnabled(b);
		this.shop.setEnabled(b);
		this.box.setEnabled(b);
		this.isKeyBox.setEnabled(b);
		this.isKeyBox1.setEnabled(b);
		this.isKeyBox2.setEnabled(b);
		this.isKeyBox3.setEnabled(b);
		this.isKeyBox4.setEnabled(b);
		this.isKeyBox5.setEnabled(b);
		this.isKeyBox6.setEnabled(b);
		this.isKeyBox7.setEnabled(b);
		this.isKeyBox8.setEnabled(b);
		this.isKeyBox9.setEnabled(b);
		this.equipJPanel.setUIEnabled(b);
		this.measureJPanel.setUIEnabled(b);
		this.standardDashboardJPanel.setUIEnabled(b);
		this.unStandardDashboardJPanel.setUIEnabled(b);
		this.toolJPanel.setUIEnabled(b);
		this.knifeToolJPanel.setUIEnabled(b);
		this.materialJPanel.setUIEnabled(b);
		this.partJPanel.setUIEnabled(b);
		this.drawingJPanel.setUIEnabled(b);
		this.button_2.setEnabled(b);
		this.button_3.setEnabled(b);
		if ("SOP".equals(com.glaway.mpm.EditorConfig.startType)) {
			this.partJPanel.setUIEnabled(b);
			this.drawingJPanel.setUIEnabled(b);
			this.additionalTableJPanel.setUIEnabled(b);
		}
	}

	/** Add By Wangxl */
	public Element getPaceElement() {
		return paceElement;
	}

	public Element getStepElement() {
		return stepElement;
	}

	public TechnicsStepJPanel_XW getStepPanel() {
		return stepPanel;
	}

	public void upDate() {
		this.checkPanel = new NewCheckResourcesTablePanel(this);
	}

	public String getImageFolder() {
		return imageFolder;
	}

	public SpecialWordPanel getEditorPane() {
		return editorPane;
	}

	public PaceTablePane getPaceTablePane() {
		return paceTablePane;
	}
}