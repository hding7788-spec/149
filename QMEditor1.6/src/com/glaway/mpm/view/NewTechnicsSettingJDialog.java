package com.glaway.mpm.view;

import com.glaway.mpm.parameter.service.ProcessParameterToWCIntf;
import com.glaway.mpm.qmIntf.template.TemplateSearchDialog;
import com.glaway.mpm.util.*;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.*;

public class NewTechnicsSettingJDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private NewTechnicsPart frame;

	private JButton okButton = new JButton("确定");
	private JButton cancelButton = new JButton("取消");
	private JButton browseTemplate = new JButton();
	private JButton browseFile = new JButton();
	private JLabel label = new JLabel("零件图号");
	private String technicsNumber;
	private String technicsName;
	private String technicsType; //工艺类型
	private String technicsMethod; //工艺方法
	private JComboBox madeDeptComboBox; //制造部门
	private JTextField technicsFileCodeText ;
	private JComboBox secretComboBox ;
	private JTextField secretDuetimeText;
	private JTextArea technicsDescText;
	private JTextField technicsDescFilePath;
	private JComboBox  phaseComboBox ;
	private JTextField mtslText;
	private String partNumber;
	private Document document;
	private Element partElement;
	public Element techElement = null;
	private JLabel numberLabel;
	// 连号工艺
	private JLabel label_11;
	public JLabel endLabel;
	private JTextField endNumber;
	private JCheckBox isConnect;
	private List<String> upConnectNumbers = null;
	private XWTreeNode node;
	private Map<XWTreeNode, Document> connectObjects;
	String[][] technicsTypes = LoadConfig.getInstance().getTechnicsType();
	// 多个工艺类别
	private String technicsCategory;
	// 令号
	// private JLabel label_12;
	// private JTextField onBuildNumber;

	// 多个工艺
	public String technicsPath;
	public String version;
	private String fileCode = "";
	private JLabel mtslLabel = new JLabel("每台数量");
	private JLabel mjdeLabel = new JLabel("每件定额");
	private JTextField mjdeText  = new JTextField();
	private JLabel hzjsLabel = new JLabel("合制件数");
	private JTextField hzjsText  = new JTextField();
	private JLabel technicsNameLabel = new JLabel("工艺文件名称");
	private JTextField technicsNameText  = new JTextField();
	private JLabel technicsEnglishNameLabel = new JLabel("工艺文件英文名");
	private JTextField technicsEnglishNameText  = new JTextField();
	private JLabel imageVersionLabel = new JLabel("图纸版本");
	private JTextField imageVersionText  = new JTextField();
	private JLabel vseLabel = new JLabel("VSE");
	private JTextField vseText  = new JTextField();
	private Map<String,String> typesMap = new HashMap<String,String>();
	private Map<String,String> attrs = new HashMap<String,String>();
	private Map<String,String> attrsMap = new HashMap<String,String>();
	private Map<String,String> pplanIdMap = new HashMap<String,String>();
	private JPanel panel = new JPanel();
	private List<Component> list = new ArrayList<Component>();
	private List<Component> textFiledList = new ArrayList<Component>();
	private List<Component> comboBoxList = new ArrayList<Component>();
	private int comCnt = 1;
	private int row = 10;
	private JPanel panel0 = new JPanel();
	private JPanel panel2 = new JPanel();
	private List<String> ibaList = new ArrayList<String>();
	private Map<String,String> ibasMap = new HashMap<String,String>();
	private Map<String,List<List<String>>> xmlmap = new HashMap<String, List<List<String>>>();
	private String[] pplantype_array = {"正式工艺文件","临时工艺文件"};
	private String[] zfflag_array = {"Z","F"};
	private String[] secret_array = {"公开","内部"};
//	private String[] secret_array = {"公开","内部","秘密★10年","机密★20年"};

	private String[] department_array = {"1","2","3","4","5","6","7","8","项"};
	JTextField mindex_Value = new JTextField();
	JTextField pindex_value = new JTextField();
	JComboBox keycomponent_value = new JComboBox(new Object[]{"G","Z","N"});
	JTextField phase_code_value = new JTextField();
	JComboBox dept_value = null;
	JComboBox zfflag_value = new JComboBox(zfflag_array);
	final JComboBox pplantype_value = new JComboBox(pplantype_array);
	private JTextField pplanid_value = new JTextField();
	final JLabel tempno_label = new JLabel("临时工艺顺序号");
	final JTextField tempno_value = new JTextField();

	final JLabel bzyjNum_label = new JLabel("编制依据编号");
	final JTextField bzyjNum_value = new JTextField();

	final JLabel bzyjName_label = new JLabel("编制依据名称");
	final JTextField bzyjName_value = new JTextField();

	final JLabel yyfl_label = new JLabel("原因分类");
	final JComboBox yyfl_value = new JComboBox(new Object[]{"","设计","工艺","质量","器材","试验","其他"});

	final JLabel zwpt_label = new JLabel("配套本级图号");
	final JComboBox zwpt_value = new JComboBox(new Object[]{"否","是"});

	/*final JLabel scdyb_label = new JLabel("输出单元表");
	final JComboBox scdyb_value = new JComboBox(new Object[]{"是","否"});
	final JLabel scbyb_label = new JLabel("输出白羽表");
	final JComboBox scbyb_value = new JComboBox(new Object[]{"是","否"});*/

	private JLabel pcno_label = new JLabel("批次号");
	private JTextField pcno_value = new JTextField();
	private JComboBox pcno_box_value = null;
	final JLabel technicsFileNo_label = new JLabel("工艺文件顺序号");
	JTextField technicsFileNo_value = new JTextField();
	final JComboBox secret_value = new JComboBox(secret_array);
	private String[] pplanForms = {"非表格化","表格化","外协"};
	private JComboBox pplanForms_value = new JComboBox(pplanForms);
	private static final String ID = "Rz";
	private Vector<String> batchs;
	private String[] technicsArray = {"增材工艺","机加工艺(英文)","数控工艺(英文)","焊接工艺(英文)","钣金工艺(英文)","装配工艺(英文)"};

	public NewTechnicsSettingJDialog(NewTechnicsPart parent, Element element,
			XWTreeNode node, String technicsCategory) {
		super(parent, true);

		upConnectNumbers = new ArrayList<String>();
		//getContentPane().setLayout(new GridBagLayout());
		frame = parent;
		this.node = node;
		partElement = element;
		this.technicsCategory = technicsCategory;

		partNumber = XmlUtility.getAttributeValue(partElement, "partNumber");

		technicsNumber = fileCode;

		//获取当前产品的批次号
		if(batchs == null) {
			long oid = Long.valueOf(XmlUtility.getAttributeValue(partElement, "containerId"));
			try {
				batchs = TechnicsIntf.getBatchsByProductOid(oid);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(batchs == null) {
				batchs = new Vector<String>();
				batchs.add("");
			}
		}
		pcno_box_value = new JComboBox(batchs);
		pcno_box_value.setEnabled(false);
		String batch = XmlUtility.getAttributeValue(partElement, "BATCH");
		if(batch != null && !"".equals(batch) && batchs.contains(batch)) {
			pcno_box_value.setSelectedItem(batch);
		} else {
			pcno_box_value.setSelectedIndex(0);
		}

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
		dept_value = new JComboBox(allList.toArray());
		String groupName = "";
		try {
			groupName = TechnicsIntf.getUsertechnicsGroupName();
		} catch (RemoteException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (InvocationTargetException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if(allList.contains(groupName)) {
			dept_value.setSelectedItem(groupName);
		}

		//获取工艺规程的XML配置文件信息
		if(xmlmap.isEmpty()) {
			try {
				xmlmap = TechnicsIntf.getMPMPPlanTypeAttrByXML();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		//获取零部件的IBA属性值
		initIBAList();
		if(ibasMap.isEmpty()) {
			try {
				ibasMap = TechnicsIntf.getPartIBAValuesByNumber(partNumber, ibaList);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		setIconImage(frame.getIconImage());
		if ("rework".equals(technicsCategory)) {
			this.setTitle("新建返工工艺");
			technicsName = XmlUtility.getAttributeValue(partElement, "partName") + "_fg" + getLastestReworkNumber(partNumber);
		} else if ("temp".equals(technicsCategory)) {
			this.setTitle("新建临时工艺");
			technicsName = XmlUtility.getAttributeValue(partElement, "partName") + "_ls" + getLastestTempNumber(partNumber);
		} else {
//			this.setTitle("新建" + technicsType);
			this.setTitle("新建工艺");
			technicsName = XmlUtility.getAttributeValue(partElement, "partName");
			technicsName = "";
		}
		Container container = getContentPane();

		panel0.setLayout(new BorderLayout());
		panel.setLayout(new GridBagLayout());
		JScrollPane scrollPane = new JScrollPane(panel0);
		panel0.add(panel,BorderLayout.CENTER);
		container.add(scrollPane);
		panel0.add(panel2,BorderLayout.SOUTH);

		//设置网格布局管理器参数
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 5, 0, 0);
		gridBagConstraints.gridwidth = 2;

		//第一行：产品型号代号
		JLabel mindex_label = new JLabel("产品型号代号");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		panel.add(mindex_label, gridBagConstraints);

		//mindex_Value.setEditable(false);
		mindex_Value.setText(ibasMap.get("MINDEX"));
		mindex_Value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(mindex_Value, gridBagConstraints);

		//第二行：产品代号
		final JLabel pindex_label = new JLabel("产品代号");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 1;
		panel.add(pindex_label, gridBagConstraints);

		pindex_value.setEditable(false);
		pindex_value.setText(ibasMap.get("PINDEX"));
		pindex_value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(pindex_value, gridBagConstraints);

		//第三行：设计图样代号
		final JLabel partNumber_label = new JLabel("设计图样代号");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 1;
		panel.add(partNumber_label, gridBagConstraints);

		JTextField partNumber_value = new JTextField();
		partNumber_value.setEditable(false);
		partNumber_value.setText(XmlUtility.getAttributeValue(partElement, "CINDEX"));
		partNumber_value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(partNumber_value, gridBagConstraints);

		//第四行：设计图样名称
		final JLabel partName_label = new JLabel("设计图样名称");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 1;
		panel.add(partName_label, gridBagConstraints);

		JTextField partName_value = new JTextField();
		partName_value.setEditable(false);
		partName_value.setText(XmlUtility.getAttributeValue(partElement, "partName"));
		partName_value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(partName_value, gridBagConstraints);

		//第五行：关重件标记
		final JLabel keycomponent_label = new JLabel("关重件标记");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 1;
		panel.add(keycomponent_label, gridBagConstraints);

		keycomponent_value.setSelectedItem(ibasMap.get("KEYCOMPONENT"));
//		keycomponent_value.setEditable(false);
//		keycomponent_value.setText(ibasMap.get("KEYCOMPONENT"));
		keycomponent_value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(keycomponent_value, gridBagConstraints);

		//第六行：产品阶段标记
		final JLabel phase_code_label = new JLabel("产品阶段标记");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridwidth = 1;
		panel.add(phase_code_label, gridBagConstraints);

		phase_code_value.setEditable(false);
		phase_code_value.setText(XmlUtility.getAttributeValue(partElement, "PHASE_CODE"));
//		phase_code_value.setText(ibasMap.get("PHASE_CODE"));
		phase_code_value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(phase_code_value, gridBagConstraints);


		//第七行：部门
		final JLabel dept_label = new JLabel("部门");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.gridwidth = 1;
		panel.add(dept_label, gridBagConstraints);

		dept_value.setPreferredSize(new Dimension(150, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(dept_value, gridBagConstraints);

		//第八行：工艺文件类别
		final JLabel pplantype_label = new JLabel("工艺文件类别");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.gridwidth = 1;
		panel.add(pplantype_label, gridBagConstraints);

		gridBagConstraints.gridy = 7;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridwidth = 2;
		pplantype_value.setPreferredSize(new Dimension(150, 23));
		panel.add(pplantype_value, gridBagConstraints);
		//监听选择文件类别事件
		pplantype_value.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(pplantype_value.getSelectedItem().toString().equals("临时工艺文件")) {
					tempno_label.setVisible(true);
					tempno_value.setVisible(true);

					bzyjNum_label.setVisible(true);
					bzyjNum_value.setVisible(true);

					bzyjName_label.setVisible(true);
					bzyjName_value.setVisible(true);

					yyfl_label.setVisible(true);
					yyfl_value.setVisible(true);



					technicsFileNo_value.setText("");
					technicsFileNo_label.setVisible(false);
					technicsFileNo_value.setVisible(false);

					technicsFileCodeText.setEditable(false);
				} else {
					tempno_label.setVisible(false);
					tempno_value.setVisible(false);

					bzyjNum_label.setVisible(false);
					bzyjNum_value.setVisible(false);

					bzyjName_label.setVisible(false);
					bzyjName_value.setVisible(false);

					yyfl_label.setVisible(false);
					yyfl_value.setVisible(false);

					if(zfflag_value.getSelectedItem().toString().equals("Z")) {
						technicsFileNo_label.setVisible(false);
						technicsFileNo_value.setVisible(false);
					} else if(zfflag_value.getSelectedItem().toString().equals("F")) {
						technicsFileNo_label.setVisible(true);
						technicsFileNo_value.setVisible(true);
					}

					technicsFileCodeText.setEditable(true);
				}

//				String number = ID+"/"+XmlUtility.getAttributeValue(partElement, "CINDEX")+zfflag_value.getSelectedItem().toString();
				String number = ID+"/"+XmlUtility.getAttributeValue(partElement, "partNumber")+zfflag_value.getSelectedItem().toString();
				String pplanidValue = pplanid_value.getText();
				String techFileNoValue = technicsFileNo_value.getText();
				String tempnoValue = tempno_value.getText();
				String newValue = number+pplanidValue;

				if(pplantype_value.getSelectedItem().toString().equals("正式工艺文件")
						&&zfflag_value.getSelectedItem().toString().equals("F")) {
					if(techFileNoValue!=null && !"".equals(techFileNoValue)) {
						newValue = newValue+"-"+techFileNoValue;
					}
				}

				if(pplantype_value.getSelectedItem().toString().equals("临时工艺文件")) {
					if(tempnoValue!=null && !"".equals(tempnoValue)) {
						newValue = newValue+"(*"+tempnoValue+")";
					}
				}

				technicsFileCodeText.setText(newValue);
			}
		});

		//第九行：临时工艺顺序号
		tempno_label.setVisible(false);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.gridwidth = 1;
		panel.add(tempno_label, gridBagConstraints);

		tempno_value.setVisible(false);
		tempno_value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(tempno_value, gridBagConstraints);
		tempno_value.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
//				String number = ID+"/"+XmlUtility.getAttributeValue(partElement, "CINDEX")+zfflag_value.getSelectedItem().toString();
				String number = ID+"/"+XmlUtility.getAttributeValue(partElement, "partNumber")+zfflag_value.getSelectedItem().toString();
				String pplanidValue = pplanid_value.getText();
				String techFileNoValue = technicsFileNo_value.getText();
				String tempnoValue = tempno_value.getText();
				String newValue = number+pplanidValue;
				if(techFileNoValue!=null && !"".equals(techFileNoValue)) {
					newValue = newValue+"-"+techFileNoValue;
				}
				if(tempnoValue!=null && !"".equals(tempnoValue)) {
					newValue = newValue+"(*"+tempnoValue+")";
				}
				technicsFileCodeText.setText(newValue);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
//				String number = ID+"/"+XmlUtility.getAttributeValue(partElement, "CINDEX")+zfflag_value.getSelectedItem().toString();
				String number = ID+"/"+XmlUtility.getAttributeValue(partElement, "partNumber")+zfflag_value.getSelectedItem().toString();
				String pplanidValue = pplanid_value.getText();
				String techFileNoValue = technicsFileNo_value.getText();
				String tempnoValue = tempno_value.getText();
				String newValue = number+pplanidValue;
				if(zfflag_value.getSelectedItem().toString().equals("F")) {
					if(techFileNoValue!=null && !"".equals(techFileNoValue)) {
						newValue = newValue+"-"+techFileNoValue;
					}
				}
				if(tempnoValue!=null && !"".equals(tempnoValue)) {
					newValue = newValue+"(*"+tempnoValue+")";
				}
				technicsFileCodeText.setText(newValue);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {}

		});

		//第十行：主辅制类别
		final JLabel zfflag_label = new JLabel("主辅制类别");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.gridwidth = 1;
		panel.add(zfflag_label, gridBagConstraints);

		zfflag_value.setSelectedItem("Z");
		gridBagConstraints.gridy = 9;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridwidth = 2;
		zfflag_value.setPreferredSize(new Dimension(150, 23));
		panel.add(zfflag_value, gridBagConstraints);
		//监听选择文件类别事件
		zfflag_value.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(zfflag_value.getSelectedItem().toString().equals("F")) {
					technicsFileNo_label.setVisible(true);
					technicsFileNo_value.setVisible(true);
				} else {
					technicsFileNo_label.setVisible(false);
					technicsFileNo_value.setVisible(false);
				}

//				String number = ID+"/"+XmlUtility.getAttributeValue(partElement, "CINDEX")+zfflag_value.getSelectedItem().toString();
				String number = ID+"/"+XmlUtility.getAttributeValue(partElement, "partNumber")+zfflag_value.getSelectedItem().toString();
				String pplanidValue = pplanid_value.getText();
				String techFileNoValue = technicsFileNo_value.getText();
				String tempnoValue = tempno_value.getText();
				String newValue = number+pplanidValue;

				if(zfflag_value.getSelectedItem().toString().equals("F")) {
					if(techFileNoValue!=null && !"".equals(techFileNoValue)) {
						newValue = newValue+"-"+techFileNoValue;
					}
				}

				if(pplantype_value.getSelectedItem().toString().equals("临时工艺文件")) {
					if(tempnoValue!=null && !"".equals(tempnoValue)) {
						newValue = newValue+"(*"+tempnoValue+")";
					}
				}

				technicsFileCodeText.setText(newValue);
				//获取到所有的车间组，然后自动定位到当前用户所在的组

			}
		});

		//获取工艺规程的所有子类型
		try {
			typesMap = TechnicsIntf.getMPMPPlanSubTypes();
			pplanIdMap = TechnicsIntf.getPPlanID();
		} catch (RemoteException e2) {
			e2.printStackTrace();
		} catch (InvocationTargetException e2) {
			e2.printStackTrace();
		}

		//默认显示的工艺规程
		technicsType = (String)typesMap.values().toArray()[0];

		//第十一行：工艺类型
		JLabel technicsTypeLabel = new JLabel("工艺类型");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.gridwidth = 1;
		panel.add(technicsTypeLabel,gridBagConstraints);
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.gridwidth = 2;
		final JComboBox technicsTypeComboBox = new JComboBox(typesMap.values().toArray());
		technicsTypeComboBox.setPreferredSize(new Dimension(150, 23));
		panel.add(technicsTypeComboBox,gridBagConstraints);
		//监听选择工艺规程事件
		technicsTypeComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				technicsType = technicsTypeComboBox.getSelectedItem().toString();
				setTitle("新建"+technicsType);

				//设置工艺特征编码
				pplanid_value.setText(pplanIdMap.get(technicsType));

				//计算并设置工艺文件编号
//				String number = ID+"/"+XmlUtility.getAttributeValue(partElement, "CINDEX")+zfflag_value.getSelectedItem().toString()+pplanIdMap.get(technicsType);
				//changed by liangbo
				// 工艺编辑器新建工艺文件的工艺文件默认编号逻辑调整    Rz/+零件图号+工艺特征号----->>>>>>>Rz/+零件编号+工艺特征号
				String number = ID+"/"+XmlUtility.getAttributeValue(partElement, "partNumber")+zfflag_value.getSelectedItem().toString()+pplanIdMap.get(technicsType);
				if(technicsType.equals("典型工艺")){
					zfflag_label.setVisible(false);
					zfflag_value.setVisible(false);
					number = ID + "/" + XmlUtility.getAttributeValue(partElement, "partNumber");;
				}else{
					zfflag_label.setVisible(true);
					zfflag_value.setVisible(true);
				}
				if(technicsType.contains("英文")){
					setEnglishAttributeIsShow(true);
				}else{
					setEnglishAttributeIsShow(false);
				}
				String techFileNoValue = technicsFileNo_value.getText();
				System.out.println("------technicsType---"+technicsType);
				System.out.println(pplanIdMap);
				String tempnoValue = tempno_value.getText();
				String newValue = number;
				if(techFileNoValue!=null && !"".equals(techFileNoValue)) {
					newValue = newValue+"-"+techFileNoValue;
				}
				if(tempnoValue!=null && !"".equals(tempnoValue)) {
					newValue = newValue+"(*"+tempnoValue+")";
				}
				technicsFileCodeText.setText(newValue);

				//设置工艺文件名称
				if(NewTechnicsPart.isTemplateCapp){
					technicsNameText.setText(technicsType+"模板");
				}else{
					technicsNameText.setText(technicsType+"规程");

				}
				Boolean flag=false;
				for (int i = 0; i < technicsArray.length; i++) {
				    if (technicsType.equals(technicsArray[i])) {
				        flag=true;
				        pplanForms_value.setSelectedItem("非表格化");
				        pplanForms_value.setEnabled(false);

                    }
                }
				if(!flag){
                    pplanForms_value.setEnabled(true);
				}

				//首先隐藏上一次显示的属性组建和按钮组建
				//hideIBAAttributes();
				//显示当前选择的工艺规程类型的IBA属性
				//showIBAAttributes(gridBagConstraints);
			}
		});

		//默认调用
		//showIBAAttributes(gridBagConstraints);
		//showButton(gridBagConstraints);

		//第十二行：是否表格化
		final JLabel pplanForms_label = new JLabel("工艺文件形式");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 11;
		gridBagConstraints.gridwidth = 1;
		panel.add(pplanForms_label, gridBagConstraints);

		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 11;
		gridBagConstraints.gridwidth = 2;
		pplanForms_value.setPreferredSize(new Dimension(150, 23));
		panel.add(pplanForms_value,gridBagConstraints);

		//第十三行：工艺特征编号
		final JLabel pplanid_label = new JLabel("工艺特征编号");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 12;
		gridBagConstraints.gridwidth = 1;
		panel.add(pplanid_label, gridBagConstraints);

		pplanid_value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		pplanid_value.setText(pplanIdMap.get(technicsType));
		pplanid_value.setEditable(false);
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 12;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(pplanid_value, gridBagConstraints);

		//第十四行：工艺顺序号
		technicsFileNo_label.setVisible(false);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 13;
		gridBagConstraints.gridwidth = 1;
		panel.add(technicsFileNo_label, gridBagConstraints);

		technicsFileNo_value.setVisible(false);
		technicsFileNo_value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 13;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(technicsFileNo_value, gridBagConstraints);
		technicsFileNo_value.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
//				String number = ID+"/"+XmlUtility.getAttributeValue(partElement, "CINDEX")+zfflag_value.getSelectedItem().toString();
				String number = ID+"/"+XmlUtility.getAttributeValue(partElement, "partNumber")+zfflag_value.getSelectedItem().toString();
				String tempnoValue = tempno_value.getText();
				String technicsFileNoValue = technicsFileNo_value.getText();
				String pplanidValue = pplanid_value.getText();
				String newNumber = number+pplanidValue;
				if(technicsFileNoValue != null && !"".equals(technicsFileNoValue)) {
					newNumber = newNumber+"-"+technicsFileNoValue;
				}
				if(tempnoValue != null && !"".equals(tempnoValue)) {
					newNumber = newNumber + "(*" + tempnoValue + ")";
				}
				technicsFileCodeText.setText(newNumber);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
//				String number = ID+"/"+XmlUtility.getAttributeValue(partElement, "CINDEX")+zfflag_value.getSelectedItem().toString();
				String number = ID+"/"+XmlUtility.getAttributeValue(partElement, "partNumber")+zfflag_value.getSelectedItem().toString();
				String tempnoValue = tempno_value.getText();
				String technicsFileNoValue = technicsFileNo_value.getText();
				String pplanidValue = pplanid_value.getText();
				String newNumber = number+pplanidValue;
				if(technicsFileNoValue != null && !"".equals(technicsFileNoValue)) {
					newNumber = newNumber+"-"+technicsFileNoValue;
				}
				if(pplantype_value.getSelectedItem().toString().equals("临时工艺文件")) {
					if(tempnoValue != null && !"".equals(tempnoValue)) {
						newNumber = newNumber + "(*" + tempnoValue + ")";
					}
				}
				technicsFileCodeText.setText(newNumber);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {}

		});

		//第十五行：工艺文件编号
		JLabel  technicsFileCodeLabel = new JLabel("工艺文件编号");
//		String numner = ID+"/"+XmlUtility.getAttributeValue(partElement, "CINDEX")+zfflag_value.getSelectedItem().toString()+pplanIdMap.get(technicsType);
		String numner = ID+"/"+XmlUtility.getAttributeValue(partElement, "partNumber")+zfflag_value.getSelectedItem().toString()+pplanIdMap.get(technicsType);
		technicsFileCodeText = new JTextField();
		technicsFileCodeText.setEditable(true);
		technicsFileCodeText.setText(numner);
		technicsFileCodeText.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 14;
		gridBagConstraints.gridwidth = 1;
		panel.add(technicsFileCodeLabel,gridBagConstraints);
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 14;
		gridBagConstraints.gridwidth = 2;
		panel.add(technicsFileCodeText,gridBagConstraints);

		//JPanel selectNumberPanel = new JPanel();
		//selectNumberPanel.setLayout(new GridBagLayout());
		//JButton selectFilecodeButton = new JButton("选择");
		//selectNumberPanel.add(technicsFileCodeText, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		//selectNumberPanel.add(selectFilecodeButton,new GridBagConstraints(3, 0, 1, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		//gridBagConstraints.gridwidth = 1;
		//gridBagConstraints.gridx = 1;
		//gridBagConstraints.gridy = 13;
		//panel.add(selectNumberPanel,gridBagConstraints);

//		selectFilecodeButton.setPreferredSize(new Dimension(60, 23));
//		panel.add(selectFilecodeButton,new GridBagConstraints(2, 5, 1, 1, 1.0, 0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
//		technicsFileCodeText.getDocument().addDocumentListener(new ValidateNameNumber());

		//第十六行：工艺文件名称
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 15;
		gridBagConstraints.gridwidth = 1;
		panel.add(technicsNameLabel,gridBagConstraints);
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 15;
		gridBagConstraints.gridwidth = 2;
		technicsNameText.setPreferredSize(new Dimension(300, 23));
		technicsNameText.setText(technicsType+"规程");
		panel.add(technicsNameText,gridBagConstraints);
		//technicsNameText.getDocument().addDocumentListener(new ValidateNameNumber());

		/**英文版工艺文件  新增信息  add by liangbo 20170418*/
		//第十七行：工艺文件英文名称
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 16;
		gridBagConstraints.gridwidth = 1;
		panel.add(technicsEnglishNameLabel, gridBagConstraints);
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 16;
		gridBagConstraints.gridwidth = 2;
		technicsEnglishNameText.setPreferredSize(new Dimension(300, 23));
		panel.add(technicsEnglishNameText, gridBagConstraints);
		//第十八行：图纸版本
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 17;
		gridBagConstraints.gridwidth = 1;
		panel.add(imageVersionLabel, gridBagConstraints);
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 17;
		gridBagConstraints.gridwidth = 2;
		imageVersionText.setPreferredSize(new Dimension(150, 23));
		panel.add(imageVersionText, gridBagConstraints);
		// 第十九行：VSE
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 18;
		gridBagConstraints.gridwidth = 1;
		panel.add(vseLabel, gridBagConstraints);
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 18;
		gridBagConstraints.gridwidth = 2;
		vseText.setPreferredSize(new Dimension(150, 23));
		panel.add(vseText, gridBagConstraints);

		setEnglishAttributeIsShow(false);
		/**英文版工艺文件  新增属性  end*/
		/*
		//第十四行：工艺说明
		JLabel technicsDescLabel = new JLabel("工艺说明");
		gridBagConstraints.gridy = 7;
		gridBagConstraints.gridx = 0;
		panel.add(technicsDescLabel,gridBagConstraints);

		technicsDescFilePath = new JTextField();
		technicsDescFilePath.setPreferredSize(new Dimension(300,23));
		technicsDescFilePath.setEditable(false);
		technicsDescFilePath.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(e.getClickCount()==2){
					selFileAction();
				}
			}
		});

		gridBagConstraints.gridy = 7;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridwidth = 3;

		JPanel selFileJpanel = new JPanel();
		selFileJpanel.setLayout(new GridBagLayout());
		JButton selFileButton = new JButton("选择工艺说明");
		selFileJpanel.add(technicsDescFilePath, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		selFileJpanel.add(selFileButton,new GridBagConstraints(3, 0, 1, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		panel.add(selFileJpanel,gridBagConstraints);

		selFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selFileAction();

			}
		});
	    technicsDescText = new JTextArea();

		technicsDescText.setRows(4);
		gridBagConstraints.gridy = 22;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridwidth = 4;
//		getContentPane().add(new JScrollPane(technicsDescText),gridBagConstraints_21);
		*/

		//第十七行：从模板创建
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
			public void actionPerformed(ActionEvent e) {
				if(technicsType==null||"".equals(technicsType)){
					JOptionPane.showMessageDialog(frame, "工艺类型不能为空！", "提示",JOptionPane.INFORMATION_MESSAGE);
					  return ;
				}

				//校验工艺文件编号
				if(checkNumber(technicsFileCodeText.getText())){
					JOptionPane.showMessageDialog(frame, "编号为【"+technicsFileCodeText.getText()+"】的工艺文件已经存在！", "提示",JOptionPane.INFORMATION_MESSAGE);
					return ;
				}

				if("Z".equals(String.valueOf(zfflag_value.getSelectedItem()))
						&& "正式工艺文件".equals(String.valueOf(pplantype_value.getSelectedItem()))) {
					if(checkZhuZhi()) {
						JOptionPane.showMessageDialog(frame, "正式主工艺文件已经存在,一个零部件下只能有一份正式主工艺！", "提示",JOptionPane.INFORMATION_MESSAGE);
						return ;
					}
					Boolean flag = (Boolean) IntfUtil.getPeRemoteMethodInvoke("hasZhuZhi",
			                new Class[] { String.class,String.class }, new Object[] {partNumber});
					if(flag){
						JOptionPane.showMessageDialog(frame, "正式主工艺文件已经存在,一个零部件下只能有一份正式主工艺！", "提示",JOptionPane.INFORMATION_MESSAGE);
						return ;
					}
				}


				String category = NewTechnicsSettingJDialog.this.technicsCategory;
				if ("rework".equals(category) || "temp".equals(category)) {
					// String onBuildNumberText = onBuildNumber.getText();
					// if (onBuildNumberText == null
					// || onBuildNumberText.trim().length() == 0) {
					// JOptionPane.showMessageDialog(frame, "请先输入令号！", "提示",
					// JOptionPane.INFORMATION_MESSAGE);
					// return;
					// }
				}
				String name = null;
				try {
					String templatePath = WorkSpaceUtil.getTempletRootPath();
					System.out.println("从模板创建====参数路径=======" + templatePath);
					String s = null;
					System.out.println("从模板创建====technicsType=======" + technicsType);
					for(int i=0;i<technicsTypes[1].length;i++){
						System.out.println("从模板创建====technicsTypes[1][i]=======" + technicsTypes[1][i]);
						if(technicsTypes[1][i].equals(technicsType)){
							s = technicsTypes[0][i];
						}
					}

					//technicsNumber = technicsFileCodeText.getText();
					technicsNumber = TechnicsIntf.genTechnicsNumber();
					if("".equals(technicsNumber)){
						JOptionPane.showMessageDialog(frame, "工艺文件流水号生成失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					technicsName = technicsNameText.getText()+"("+technicsFileCodeText.getText()+")";

					String technicsPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);

					if(technicsPath!=null){
						  JOptionPane.showMessageDialog(frame, "工艺文件编号【"+technicsNumber.trim()+"】的工艺文件已经存在！", "提示",JOptionPane.INFORMATION_MESSAGE);
						  return ;
					}
					System.out.println(technicsType);
					TemplateSearchDialog dialog = new TemplateSearchDialog(templatePath, s, frame);
					Vector templates = dialog.showDialog();
					if (templates != null) {
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
							System.out.println("templateName==111======" + templateName);
							String templateXMLPath = "";
							if(templateName.contains("@@")){
								String[] ts = templateName.split("@@");
								if(ts[0].equals("机加工艺")){
									templateXMLPath = WorkSpaceUtil.getTempletRootPath() + "\\Process_MPTemplate\\" + ts[1];
								}else if(ts[0].equals("非金属机加工工艺")) {
									templateXMLPath = WorkSpaceUtil.getTempletRootPath() + "\\Process_FJSJJGTemplate\\" + ts[1];
								}else{
									templateXMLPath = WorkSpaceUtil.getTempletRootPath() + "\\" + s + "\\" + ts[1];
								}
							}else{
								templateXMLPath = WorkSpaceUtil.getTempletRootPath() + "\\" + s + "\\" + templateName;
							}
							System.out.println("templateXMLPath=========" + templateXMLPath);
							FilesUtil.copyDirectiory(templateXMLPath, name);
						}
						// PDS服务模板逻辑
						else if (templates.size() == 2) {
							templateName = (String) templates.get(0);
							System.out.println("templateName==222======" + templateName);
							if (templateName.toLowerCase().endsWith(".xml")) {
								templateName = templateName.substring(0, templateName.length() - 4);
							}
							System.out.println("templateName==333======" + templateName);
							byte[] data = (byte[]) templates.get(1);
							TechnicsReleaseUtil.unZip(data, name);
						}
						// TODO
						// 修改内容
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

								technicsPath = newFile.getAbsolutePath();
								System.out.println("technicsPath= " + technicsPath);
								version = techElement.attributeValue("version");
								Document doc = XmlUtility.getDocument(f);
								techElement = XmlUtility.getTechnicsElement(doc);
								f.delete();
								generateTechDocument(partElement,techElement);
								NewTechnicsSettingJDialog.this.setIBAAttribute(techElement);
								XmlUtility.setAttributeValue(techElement, "version", "");
								XmlUtility.setAttributeValue(techElement, "technicsName", technicsName);
								XmlUtility.setAttributeValue(techElement, "pplanNumber", technicsFileCodeText.getText());
								XmlUtility.setAttributeValue(techElement, "pplanName", technicsNameText.getText());

								XmlUtility.setAttributeValue(techElement, "bzyjNum", bzyjNum_value.getText());
								XmlUtility.setAttributeValue(techElement, "bzyjName", bzyjName_value.getText());
								XmlUtility.setAttributeValue(techElement, "yyfl", yyfl_value.getSelectedItem().toString());

								if(s.equals("Process_FJSJJGTemplate")){
									XmlUtility.setAttributeValue(techElement, "technicsType", "非金属机加工工艺");
									XmlUtility.setAttributeValue(techElement, "PPLANID", "08");
								}else if(s.equals("Process_MPTemplate")){
									XmlUtility.setAttributeValue(techElement, "technicsType", "机加工艺");
									XmlUtility.setAttributeValue(techElement, "PPLANID", "11");
								}
								/**移除参装件信息*/
								/*List<Element> stepElements = XmlUtility.getAllSteps(techElement);
								for(Element stepElement : stepElements){
									XmlUtility.removeCzjElements(stepElement);
								}*/
								//工艺定额
								Element gyde = XmlUtility.getTechnicsDEElement(techElement);
								if(gyde != null) {
									List<Element> newParts = XmlUtility.getTechnicsGYDENewPart(gyde);
									for (Element element : newParts) {
//									  element.setAttributeValue("parentNumber",XmlUtility.getAttributeValue(techElement, "partNumber"));
										XmlUtility.setAttributeValue(element, "parentNumber", XmlUtility.getAttributeValue(techElement, "partNumber"));
									}
									//工艺定额（设计资源库）
									List<Element> SjzykNewParts=XmlUtility.getTechnicsSJZYKGYDENewPart(gyde);
									if(null!=SjzykNewParts){
										for (Element element : SjzykNewParts) {
											XmlUtility.setAttributeValue(element, "partNumber", XmlUtility.getAttributeValue(techElement, "partNumber"));
										}
									}
								}

								//白羽表处理 新建一份引用工艺的白羽表
								//工艺端
								List<Element> tecBaiyuImageList = techElement.selectNodes("schemaData/schemaInfo");
								if(tecBaiyuImageList != null){
									ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();
									for(int i = 0; i < tecBaiyuImageList.size(); i++) {
										ArrayList<String> bylist = new ArrayList<String>();
										Element mEle = tecBaiyuImageList.get(i);
										String number = mEle.attributeValue("id");
										String version = mEle.attributeValue("version");
										bylist.add(number);
										bylist.add(number);
										bylist.add(version);
										bylist.add(version);
										lists.add(bylist);
									}
									if(lists.size() > 0){
										ArrayList<ArrayList<String>> bys = ProcessParameterToWCIntf.quoteBaiyuTemplate(lists, technicsNumber, "", "");
										if(bys != null && bys.size() > 0){
											Element schemaData = XmlUtility.getSchemaData(techElement);
											XmlUtility.removeAllChildElements(schemaData);
											for (ArrayList<String> list : bys) {
												Element schemaEle = schemaData.addElement(XmlUtility.SCHEMA_TAG);
												XmlUtility.setAttributeValue(schemaEle,"order",list.get(0));
												XmlUtility.setAttributeValue(schemaEle,"id",list.get(1));
												XmlUtility.setAttributeValue(schemaEle,"name",list.get(2));
												XmlUtility.setAttributeValue(schemaEle,"version",list.get(3));
												XmlUtility.setAttributeValue(schemaEle,"creator",list.get(4));
												XmlUtility.setAttributeValue(schemaEle,"mofitier",list.get(5));
												XmlUtility.setAttributeValue(schemaEle,"createTime",list.get(6));
												XmlUtility.setAttributeValue(schemaEle,"mofityTime",list.get(7));
												XmlUtility.setAttributeValue(schemaEle,"docNumber",list.get(8));
												XmlUtility.setAttributeValue(schemaEle,"tableType",list.get(9));
												XmlUtility.setAttributeValue(schemaEle,"dept",list.get(10));
											}
										}
									}
								}
								//工步端
								List<Element> steps = XmlUtility.getAllSteps(techElement);
								for(Element step : steps) {
									String stepNumber = step.attributeValue("bsoID");
									List<Element> paces = XmlUtility.getAllPaces(step);
									for(Element pace : paces) {
										String paceNumber = pace.attributeValue("bsoID");
										List<Element> schemaDatas = XmlUtility.getSchemaDatas(pace);
										ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();
										for(Element schemaData : schemaDatas) {
											ArrayList<String> bylist = new ArrayList<String>();
											String number = schemaData.attributeValue("id");
											String version = schemaData.attributeValue("version");
											bylist.add(number);
											bylist.add(number);
											bylist.add(version);
											bylist.add(version);
											lists.add(bylist);
										}
										if(lists.size() > 0) {
											ArrayList<ArrayList<String>> bys = ProcessParameterToWCIntf.quoteBaiyuTemplate(lists, technicsNumber, stepNumber, paceNumber);
											if(bys != null && bys.size() > 0){
												Element schemaData = XmlUtility.getSchemaData(pace);
												XmlUtility.removeAllChildElements(schemaData);
												for (ArrayList<String> list : bys) {
													Element schemaEle = schemaData.addElement(XmlUtility.SCHEMA_TAG);
													XmlUtility.setAttributeValue(schemaEle,"order",list.get(0));
													XmlUtility.setAttributeValue(schemaEle,"id",list.get(1));
													XmlUtility.setAttributeValue(schemaEle,"name",list.get(2));
													XmlUtility.setAttributeValue(schemaEle,"version",list.get(3));
													XmlUtility.setAttributeValue(schemaEle,"creator",list.get(4));
													XmlUtility.setAttributeValue(schemaEle,"mofitier",list.get(5));
													XmlUtility.setAttributeValue(schemaEle,"createTime",list.get(6));
													XmlUtility.setAttributeValue(schemaEle,"mofityTime",list.get(7));
													XmlUtility.setAttributeValue(schemaEle,"docNumber",list.get(8));
													XmlUtility.setAttributeValue(schemaEle,"tableType",list.get(9));
													XmlUtility.setAttributeValue(schemaEle,"dept",list.get(10));
												}
											}
										}
									}
								}

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
					JOptionPane.showMessageDialog(NewTechnicsSettingJDialog.this, "创建工艺出现错误！", "提示",
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

		//第十八行：从文件创建
		final JLabel label_4 = new JLabel();
		label_4.setText("从文件创建");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 20;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.insets = new Insets(5, 5, 0, 0);
		panel.add(label_4, gridBagConstraints);

		browseFile.setPreferredSize(new Dimension(70, 23));
		browseFile.setMinimumSize(new Dimension(70, 23));
		browseFile.setMaximumSize(new Dimension(70, 23));
		browseFile.setText("浏览");
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 20;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(browseFile, gridBagConstraints);
		browseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(technicsType==null||"".equals(technicsType)){
						JOptionPane.showMessageDialog(frame, "工艺类型不能为空！", "提示",JOptionPane.INFORMATION_MESSAGE);
						  return ;
					}

					//校验工艺文件编号
					if(checkNumber(technicsFileCodeText.getText())){
						JOptionPane.showMessageDialog(frame, "编号为【"+technicsFileCodeText.getText()+"】的工艺文件已经存在！", "提示",JOptionPane.INFORMATION_MESSAGE);
						return ;
					}

					if("Z".equals(String.valueOf(zfflag_value.getSelectedItem()))
							&& "正式工艺文件".equals(String.valueOf(pplantype_value.getSelectedItem()))) {
						if(checkZhuZhi()) {
							JOptionPane.showMessageDialog(frame, "正式主工艺文件已经存在,一个零部件下只能有一份正式主工艺！", "提示",JOptionPane.INFORMATION_MESSAGE);
							return ;
						}
						Boolean flag = (Boolean) IntfUtil.getPeRemoteMethodInvoke("hasZhuZhi",
				                new Class[] { String.class,String.class }, new Object[] {partNumber});
						if(flag){
							JOptionPane.showMessageDialog(frame, "正式主工艺文件已经存在,一个零部件下只能有一份正式主工艺！", "提示",JOptionPane.INFORMATION_MESSAGE);
							return ;
						}

					}

					String filepath = FileChooserTool.getFilePath("xml", NewTechnicsSettingJDialog.this);
					if (filepath != null) {
						Document doc = XmlUtility.getDocument(filepath);
						techElement = XmlUtility.getTechnicsElement((Document) doc.clone());
						createProcess(false, NewTechnicsSettingJDialog.this.technicsCategory);
					}
				} catch (Exception ee) {
					ee.printStackTrace();
					JOptionPane.showMessageDialog(frame, "创建工艺过程中出错！", "提示", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

//		if(fileCode!=null&&!"".equals(fileCode.trim())){
//			browseTemplate.setEnabled(true);
//			browseFile.setEnabled(true);
//		}else{
//			browseTemplate.setEnabled(false);
//			browseFile.setEnabled(false);
//		}


		//第十九行：文件密级
		final JLabel secret_label = new JLabel("文件密级");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 21;
		gridBagConstraints.gridwidth = 1;
		panel.add(secret_label, gridBagConstraints);

		secret_value.setPreferredSize(new Dimension(150, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 21;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(secret_value, gridBagConstraints);

		//第二十行：批次号
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 22;
		gridBagConstraints.gridwidth = 1;
		panel.add(pcno_label, gridBagConstraints);

		pcno_box_value.setPreferredSize(new Dimension(150, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 22;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(pcno_box_value, gridBagConstraints);

		//第21行：编制依据编号（临时工艺）
		bzyjNum_label.setVisible(false);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 23;
		gridBagConstraints.gridwidth = 1;
		panel.add(bzyjNum_label, gridBagConstraints);


		bzyjNum_value.setVisible(false);
		bzyjNum_value.setPreferredSize(new Dimension(150, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 23;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(bzyjNum_value, gridBagConstraints);

		//第22行：编制依据名称（临时工艺）
		bzyjName_label.setVisible(false);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 24;
		gridBagConstraints.gridwidth = 1;
		panel.add(bzyjName_label, gridBagConstraints);

		bzyjName_value.setVisible(false);
		bzyjName_value.setPreferredSize(new Dimension(150, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 24;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(bzyjName_value, gridBagConstraints);

		//第23行：原因分类（临时工艺）
		yyfl_label.setVisible(false);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 25;
		gridBagConstraints.gridwidth = 1;
		panel.add(yyfl_label, gridBagConstraints);

		yyfl_value.setVisible(false);
		yyfl_value.setPreferredSize(new Dimension(150, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 25;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(yyfl_value, gridBagConstraints);

		//第24行：配套本级图号
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 26;
		gridBagConstraints.gridwidth = 1;
		panel.add(zwpt_label, gridBagConstraints);

		zwpt_value.setPreferredSize(new Dimension(150, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 26;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(zwpt_value, gridBagConstraints);

		//第25行：输出单元表
		/*gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 27;
		gridBagConstraints.gridwidth = 1;
		panel.add(scdyb_label, gridBagConstraints);

		scdyb_value.setPreferredSize(new Dimension(150, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 27;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(scdyb_value, gridBagConstraints);

		//第26行：输出白羽表
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 28;
		gridBagConstraints.gridwidth = 1;
		panel.add(scbyb_label, gridBagConstraints);

		scbyb_value.setPreferredSize(new Dimension(150, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 28;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(scbyb_value, gridBagConstraints);*/

		//按钮面板
		okButton.setVisible(true);
		cancelButton.setVisible(true);
		panel2.setLayout(new GridBagLayout());
		panel2.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		okButton.setPreferredSize(new Dimension(70, 23));
		okButton.setMinimumSize(new Dimension(70, 23));
		okButton.setMaximumSize(new Dimension(70, 23));
		panel2.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 15, 15), 0, 0));
		cancelButton.setPreferredSize(new Dimension(70, 23));
		cancelButton.setMinimumSize(new Dimension(70, 23));
		cancelButton.setMaximumSize(new Dimension(70, 23));
		panel2.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 15, 15), 0, 0));

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String category = NewTechnicsSettingJDialog.this.technicsCategory;
					if ("rework".equals(category) || "temp".equals(category)) {

					}
					if(technicsFileCodeText.getText()==null||"".equals(technicsFileCodeText.getText())){
						  JOptionPane.showMessageDialog(frame, "工艺文件编号不能为空！", "提示",JOptionPane.INFORMATION_MESSAGE);
						  return ;
					}
					if(technicsType==null||"".equals(technicsType)){
						JOptionPane.showMessageDialog(frame, "工艺类型不能为空！", "提示",JOptionPane.INFORMATION_MESSAGE);
						  return ;
					}
					if(pplantype_value.getSelectedItem().toString().equals("临时工艺文件")) {
						if(tempno_value.getText()==null||"".equals(tempno_value.getText())){
							JOptionPane.showMessageDialog(frame, "临时工艺顺序号不能为空！", "提示",JOptionPane.INFORMATION_MESSAGE);
							return ;
						}
						if(bzyjNum_value.getText()==null||"".equals(bzyjNum_value.getText())){
							JOptionPane.showMessageDialog(frame, "编制依据编号不能为空！", "提示",JOptionPane.INFORMATION_MESSAGE);
							return ;
						}
						if(bzyjName_value.getText()==null||"".equals(bzyjName_value.getText())){
							JOptionPane.showMessageDialog(frame, "编制依据名称不能为空！", "提示",JOptionPane.INFORMATION_MESSAGE);
							return ;
						}
					}


					//technicsNumber = technicsFileCodeText.getText();
					//technicsNumber = String.valueOf(System.currentTimeMillis());
					technicsNumber = TechnicsIntf.genTechnicsNumber();
					if("".equals(technicsNumber)){
						JOptionPane.showMessageDialog(frame, "工艺文件流水号生成失败！", "提示",JOptionPane.INFORMATION_MESSAGE);
					}
//					String technicsNumber = WorkSpaceUtil.getTechnicsNumber(fileCode, partNumber);
					technicsName = technicsNameText.getText();
					if(technicsName==null||"".equals(technicsName.trim() )){
						JOptionPane.showMessageDialog(frame, "工艺文件名称不能为空！", "提示",JOptionPane.INFORMATION_MESSAGE);
						return ;
					}

					technicsName = technicsNameText.getText()+"("+technicsFileCodeText.getText()+")";

					String technicsPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);

					if(checkNumber(technicsFileCodeText.getText())){
						JOptionPane.showMessageDialog(frame, "编号为【"+technicsFileCodeText.getText()+"】的工艺文件已经存在！", "提示",JOptionPane.INFORMATION_MESSAGE);
						return ;
					}

					if("Z".equals(String.valueOf(zfflag_value.getSelectedItem()))
							&& "正式工艺文件".equals(String.valueOf(pplantype_value.getSelectedItem()))) {
						if(checkZhuZhi()) {
							JOptionPane.showMessageDialog(frame, "正式主工艺文件已经存在,一个零部件下只能有一份正式主工艺！", "提示",JOptionPane.INFORMATION_MESSAGE);
							return ;
						}
						Boolean flag = (Boolean) IntfUtil.getPeRemoteMethodInvoke("hasZhuZhi",
				                new Class[] { String.class,String.class }, new Object[] {partNumber});
						if(flag){
							JOptionPane.showMessageDialog(frame, "正式主工艺文件已经存在,一个零部件下只能有一份正式主工艺！", "提示",JOptionPane.INFORMATION_MESSAGE);
							return ;
						}

					}

					createProcess(true, category);
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

		//
		FocusTraversalPolicy policy = new FocusTraversalPolicy() {
			public Component getFirstComponent(Container focusCycleRoot) {
				return label;
			}

			public Component getLastComponent(Container focusCycleRoot) {
				return label;
			}

			public Component getComponentAfter(Container focusCycleRoot,
					Component aComponent) {
				return label;
			}

			public Component getComponentBefore(Container focusCycleRoot,
					Component aComponent) {
				return label;
			}

			public Component getDefaultComponent(Container focusCycleRoot) {
				return label;
			}
		};
		setFocusTraversalPolicy(policy);

		setResizable(false);
		if ("rework".equals(technicsCategory)
				|| "temp".equals(technicsCategory)) {
			hideConnect();
		} else {
			hideOnBuild();
		}

		Dimension dimension2 = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int) (dimension2.getWidth() - 500) / 2 , (int) (dimension2.getHeight() - (625)) / 2, 500, 800);

		setTitle("新建"+technicsType+"");
		setVisible(true);
	}

	private boolean checkNumber(String newNumber) {
		Enumeration childs = frame.xwPartTreePanel.getSelectedTreeNode().children();
		if(childs != null) {
			while(childs.hasMoreElements()) {
				Object obj = childs.nextElement();
				if(obj instanceof XWTreeNode) {
					XWTreeNode techObj = (XWTreeNode) obj;
					XWTreeObject treeObj = techObj.getObject();
					if(treeObj instanceof TechnicsMessageTreeObject) {
						TechnicsMessageTreeObject tmo = (TechnicsMessageTreeObject) treeObj;
						Element ele = tmo.getTreeCellData();
						String state = ele.attributeValue("lifecycle");
						String number = tmo.getPplanNumber();
						if(newNumber.equals(number) && !"已作废".equals(state)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private boolean checkZhuZhi() {
		Enumeration childs = frame.xwPartTreePanel.getSelectedTreeNode().children();
		if(childs != null) {
			while(childs.hasMoreElements()) {
				Object obj = childs.nextElement();
				if(obj instanceof XWTreeNode) {
					XWTreeNode techObj = (XWTreeNode) obj;
					XWTreeObject treeObj = techObj.getObject();
					if(treeObj instanceof TechnicsMessageTreeObject) {
						TechnicsMessageTreeObject tmo = (TechnicsMessageTreeObject) treeObj;
						String isZhuZhi = tmo.getisZhuZhi();
						String pplantype = tmo.getPplantype();
						Element techElement =tmo.getTreeCellData();
						String lifecycle = techElement.attributeValue("lifecycle");
						if("已作废".equals(lifecycle)){
							continue;
						}
						if("Z".equals(isZhuZhi)&&"正式工艺文件".equals(pplantype)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * @param flag
	 *            判断从模板创建，还是直接创建
	 * @param type
	 *            判断返工工艺，增加返工工艺属性
	 */
	private void createProcess(boolean flag, String category) {

		if (techElement == null) {
			techElement = XmlUtility.createTechnics();
		}

		XmlUtility.setAttributeValue(techElement, "secretLevel", "内部");
		XmlUtility.setAttributeValue(techElement, "technicsCategory", category);

		techElement = generateTechDocument(partElement, techElement);
		techElement = setIBAAttribute(techElement);
		System.out.println(techElement.attributeValue("technicsName"));
		document = writeDocument(techElement);
//		String dir = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
//		File wordFile = new File(dir,"工艺说明.doc");
//		File file = null;
//		String descFile = technicsDescFilePath.getText();
//		InputStream is = null;
//
//		try {
//			if(descFile==null||"".equals(descFile.trim())){
//				is = NewTechnicsPart.class.getResourceAsStream("/templates/工艺说明.doc");
//				FileUtils.copyInputStreamToFile(is, wordFile);
//			}else{
//				file = new File(descFile);
//				FileUtils.copyFile(file, wordFile);
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}finally{
//			JavaUtil.closeStream(is);
//		}

		if ("rework".equals(category)) {
			String path = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
			String temp = path.substring(path.lastIndexOf(File.separator)) + ".xml";
			technicsPath = path + File.separator + temp;
			version = techElement.attributeValue("version");
		} else if ("temp".equals(category)) {
			String path = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
			String temp = path.substring(path.lastIndexOf(File.separator)) + ".xml";
			technicsPath = path + File.separator + temp;
			version = techElement.attributeValue("version");
		}

		dispose();
	}

	private void generatorConnects(XWTreeNode node, int start, int end,
			Element techElement) {
		if (start < end - upConnectNumbers.size()) {
			connectObjects = new HashMap<XWTreeNode, Document>();
			flag: for (int i = start + 1; i <= end - upConnectNumbers.size(); i++) {
				if (node == null) {
					break;
				}
				DefaultMutableTreeNode nextNode = node.getNextNode();
				if (nextNode == null) {
					continue;
				}
				node = (XWTreeNode) nextNode;

				XWTreeObject obj = node.getObject();
				if (obj instanceof XWPartTreeObject) {
					XWPartTreeObject object = (XWPartTreeObject) obj;
					Element partElement = object.getTreeCellData();
					String partNumber = partElement.attributeValue("partNumber");
					String responser = partElement.attributeValue("responser");
					List userlist = UserUtil.getCurrentUserOid();
					String creatorOid = null;
					if ((userlist != null) && (userlist.size() == 3)) {
						creatorOid = (String) userlist.get(1);
					}
					for (String number : upConnectNumbers) {
						if (number.equals(partNumber)) {
							continue flag;
						}
					}
					if (responser.equals(creatorOid)) {
						connectObjects.put(node, writeDocument(generateTechDocument(partElement, techElement)));
					}
				}
			}
		}
		if (upConnectNumbers.size() != 0) {
			String numbers = "";
			for (String number : upConnectNumbers) {
				numbers += "," + number;
			}
			if (numbers.startsWith(",")) {
				numbers = numbers.substring(1);
			}
			JOptionPane.showMessageDialog(frame, "编号为" + numbers + "的零件不在该连号区间！");
		}

	}

	private boolean isConnector(XWTreeNode node, int start, int end,
			String prefixNumber) {
		try {
			DefaultMutableTreeNode nextNode = node.getNextNode();
			for (int i = start + 1; i <= end; i++) {
				System.out.println(nextNode);
				if (nextNode != null) {
					node = (XWTreeNode) nextNode;
					XWPartTreeObject object = (XWPartTreeObject) node.getObject();
					Element element = object.getTreeCellData();
					String partNumber = element.attributeValue("partNumber");
					String lastNumber = partNumber.split("\\.")[2];
					int length = lastNumber.length();
					String lastPartNumber = generateLastNumber(i, length);
					if (partNumber.equals(prefixNumber + lastPartNumber)) {
						nextNode = node.getNextNode();
						continue;
					} else if (new Integer(lastNumber) > i) {
						upConnectNumbers.add(prefixNumber + lastPartNumber);
						continue;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private String generateLastNumber(int number, int length) {
		String string = String.valueOf(number);
		for (int i = 0; i < length - string.length(); i++) {
			string = "0" + string;
		}
		System.out.println(string);
		return string;
	}

	private Document writeDocument(Element techElement) {
		try {
			// 创建目录
			String dir = WorkSpaceUtil.createTechnicsDirectory(
					techElement.attributeValue("technicsNumber"),
					techElement.attributeValue("technicsName"),
					techElement.attributeValue("technicsType"),
					techElement.attributeValue("partNumber"),
					techElement.attributeValue("productNumber"),
					techElement.attributeValue("technicsCategory"));
			// 创建文件
			RecentSaveUtil.addRecent(technicsNumber);
			return WorkSpaceUtil.createTechnics(techElement, dir);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(frame, "创建文件时出现错误！");
		}
		return null;
	}

	/**
	 * 获取新建返工工艺的编号
	 *
	 * @param partNumber
	 * @return
	 */
	private String getLastestReworkNumber(String partNumber) {
		String number = "";
		List<Integer> numbers = WorkSpaceUtil
				.getReworkTechnicsNumbers(partNumber);
		if (numbers == null || numbers.size() == 0) {
			number = "01";
		} else {
			Collections.sort(numbers, new NumberComparator());
			number = String.valueOf(numbers.get(numbers.size() - 1) + 1);
			if (number.length() == 1) {
				number = "0" + number;
			}
		}
		System.out.println("number= " + number);
		return number;
	}

	/**
	 * 获取新建临时工艺的编号
	 *
	 * @param partNumber
	 * @return
	 */
	private String getLastestTempNumber(String partNumber) {
		String number = "";
		List<Integer> numbers = WorkSpaceUtil.getTempTechnicsNumbers(partNumber);
		if (numbers == null || numbers.size() == 0) {
			number = "01";
		} else {
			Collections.sort(numbers, new NumberComparator());
			number = String.valueOf(numbers.get(numbers.size() - 1) + 1);
			if (number.length() == 1) {
				number = "0" + number;
			}
		}
		System.out.println("number= " + number);
		return number;
	}

	/**
	 * 点击确定时，设置工艺规程的属性
	 *
	 * @param techElement
	 * @return
	 */
	private Element setIBAAttribute(Element techElement){
		Element ibaElement = XmlUtility.getTechnicsIBAAttriElement(techElement);
		XmlUtility.deleteAllChildElements(ibaElement);

		XmlUtility.setAttributeValue(techElement, "MINDEX", mindex_Value.getText());
		ibaElement.add(this.createTechnicsIBAAttriElement("产品型号代号",mindex_Value.getText()));

		XmlUtility.setAttributeValue(techElement, "PINDEX", pindex_value.getText());
		ibaElement.add(this.createTechnicsIBAAttriElement("产品代号",pindex_value.getText()));

		XmlUtility.setAttributeValue(techElement, "KEYCOMPONENT", (String)keycomponent_value.getSelectedItem());
		ibaElement.add(this.createTechnicsIBAAttriElement("关重件标记",(String)keycomponent_value.getSelectedItem()));

		XmlUtility.setAttributeValue(techElement, "PHASE_CODE", phase_code_value.getText());
		ibaElement.add(this.createTechnicsIBAAttriElement("产品阶段标记",phase_code_value.getText()));

		XmlUtility.setAttributeValue(techElement, "DEPT", dept_value.getSelectedItem().toString());
		ibaElement.add(this.createTechnicsIBAAttriElement("部门",dept_value.getSelectedItem().toString()));

		XmlUtility.setAttributeValue(techElement, "PPLANTYPE", pplantype_value.getSelectedItem().toString());
		ibaElement.add(this.createTechnicsIBAAttriElement("工艺文件类别",pplantype_value.getSelectedItem().toString()));

		//配套本级图号
		XmlUtility.setAttributeValue(techElement, "zwptFlag", zwpt_value.getSelectedItem().toString());
		//XmlUtility.setAttributeValue(techElement, "printDanYuanFlag", scdyb_value.getSelectedItem().toString());
		//XmlUtility.setAttributeValue(techElement, "printBaiYuFlag", scbyb_value.getSelectedItem().toString());

		if(pplantype_value.getSelectedItem().toString().equals("临时工艺文件")) {
			XmlUtility.setAttributeValue(techElement, "TEMPNO", tempno_value.getText());
			ibaElement.add(this.createTechnicsIBAAttriElement("临时工艺顺序号",tempno_value.getText()));

			XmlUtility.setAttributeValue(techElement, "bzyjNum", bzyjNum_value.getText());
			XmlUtility.setAttributeValue(techElement, "bzyjName", bzyjName_value.getText());
			XmlUtility.setAttributeValue(techElement, "yyfl", yyfl_value.getSelectedItem().toString());

		}
		if(pplantype_value.getSelectedItem().toString().equals("典型工艺")){
			XmlUtility.setAttributeValue(techElement, "ZFFLAG", "");
			ibaElement.add(this.createTechnicsIBAAttriElement("主辅制类别",""));
		}else{
			XmlUtility.setAttributeValue(techElement, "ZFFLAG", zfflag_value.getSelectedItem().toString());
			ibaElement.add(this.createTechnicsIBAAttriElement("主辅制类别",zfflag_value.getSelectedItem().toString()));
		}
		XmlUtility.setAttributeValue(techElement, "PPLANID", pplanid_value.getText());
		ibaElement.add(this.createTechnicsIBAAttriElement("工艺特征编号",pplanid_value.getText()));

		XmlUtility.setAttributeValue(techElement, "SECRET", secret_value.getSelectedItem().toString());
		ibaElement.add(this.createTechnicsIBAAttriElement("文件密级",secret_value.getSelectedItem().toString()));

		XmlUtility.setAttributeValue(techElement, "PCNO", pcno_box_value.getSelectedItem().toString());
		ibaElement.add(this.createTechnicsIBAAttriElement("批次号",pcno_box_value.getSelectedItem().toString()));

		//XmlUtility.setAttributeValue(techElement, "printDanYuanFlag", scdyb_value.getSelectedItem().toString());
		//XmlUtility.setAttributeValue(techElement, "printBaiYuFlag", scbyb_value.getSelectedItem().toString());

		return techElement;
	}

	private Element createTechnicsIBAAttriElement(String key,String value) {
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
//		if ("middle".equals(partElement.attributeValue("partType"))) {
//			technicsType = "装配工艺";
//		} else if ("assistant".equals(partElement.attributeValue("partType"))) {
//			technicsType = "零件工艺";
//		} else {
//			technicsType = BomXMLUtil.judgeTechnicsType(partNumber);
//		}

		if ("rework".equals(technicsCategory)) {
			technicsName = XmlUtility.getAttributeValue(partElement, "partName") + "_fg" + getLastestReworkNumber(partNumber);
		} else if ("temp".equals(technicsCategory)) {
			technicsName = XmlUtility.getAttributeValue(partElement, "partName") + "_ls" + getLastestTempNumber(partNumber);
		} else {
//			technicsName = XmlUtility.getAttributeValue(partElement, "partName");
		}
//		String technicsNumber = XmlUtility.getAttributeValue(partElement,"partNumber");

		XmlUtility.setAttributeValue(techElement, "pplanNumber", technicsFileCodeText.getText());
		XmlUtility.setAttributeValue(techElement, "pplanName", technicsNameText.getText());
		XmlUtility.setAttributeValue(techElement, "processTaskItemOid", this.frame.workItemOid);//纪录工艺任务活动的OID

		if(technicsName == null || "".equals(technicsName)) {
			technicsName = technicsNameText.getText()+"("+technicsFileCodeText.getText()+")";
		}
		XmlUtility.setAttributeValue(techElement, "technicsName", technicsName);

		if(technicsNumber == null || "".equals(technicsNumber)) {
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
		/**工艺英文版  属性保存到xml add by liangbo 20170418*/
		String technicsEnglishName = technicsEnglishNameText.getText();
		String imageVersion = imageVersionText.getText();
		String vse = vseText.getText();
		XmlUtility.setAttributeValue(techElement, "technicsEnglishName",technicsEnglishName);
		XmlUtility.setAttributeValue(techElement, "imageVersion",imageVersion);
		XmlUtility.setAttributeValue(techElement, "VSE",vse);
		/***/
		XmlUtility.setAttributeValue(techElement, "technicsNumber",technicsNumber);
		XmlUtility.setAttributeValue(techElement, "technicsType", technicsType);
		XmlUtility.setAttributeValue(techElement, "isTabular", pplanForms_value.getSelectedItem().toString());
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

		XmlUtility.setAttributeValue(techElement, "CINDEX", partElement.attributeValue("CINDEX"));

		//设计资源库新增属性
		//start
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
		//end

		List list = UserUtil.getCurrentUserOid();
		if (list != null && list.size() == 3) {
			String creator = (String) list.get(0);
			String creatorOid = (String) list.get(1);
			String creatorDisplay = (String)list.get(2);
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

		String newProductNumber = "";
		try {
			newProductNumber = TechnicsIntf.getProductNumberAndName(partNumber);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		if(newProductNumber != null && !newProductNumber.isEmpty()){
			XmlUtility.setAttributeValue(techElement, "productNumber", newProductNumber);// 设置整件图号
			XmlUtility.setAttributeValue(techElement, "productName", newProductNumber);// 设置整件名称
		}else{
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
		if (materialNumber == null)
			materialNumber = "";
		if (materialName == null)
			materialName = "";
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

	private void hideConnect() {
//		label_11.setVisible(false);
//		endLabel.setVisible(false);
//		endNumber.setVisible(false);
//		isConnect.setVisible(false);
	}

	private void hideOnBuild() {
		// label_12.setVisible(false);
		// onBuildNumber.setVisible(false);
	}

	private void hideIBAAttributes(){
		for(Component component:list) {
			component.setVisible(false);
		}
		list.clear();
		textFiledList.clear();
		comboBoxList.clear();
	}

	private void showIBAAttributes(GridBagConstraints gridBagConstraints) {
		//获取零部件的IBA属性值
		if(ibaList.isEmpty()) {
			initIBAList();
			try {
				ibasMap = TechnicsIntf.getPartIBAValuesByNumber(partNumber, ibaList);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		row = 10;
		Iterator<String> iter = typesMap.keySet().iterator();
		while(iter.hasNext()) {
			String type = iter.next();
			if(typesMap.get(type).equals(technicsType)) {
				String typeName = type;
				typeName = typeName.substring(typeName.lastIndexOf('.')+1, typeName.length());
				try {
					attrs = TechnicsIntf.getAttrByMPMPlan(typeName);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Iterator<String> iterator = attrs.keySet().iterator();
				JTextField textField = null;
				while(iterator.hasNext()) {
					String key = iterator.next();
					String value = attrs.get(key);
					String[] vals = value.split("@");

					JLabel label = new JLabel(vals[0]);
					list.add(label);
					gridBagConstraints.gridx = 0;
					gridBagConstraints.gridy = row;
					gridBagConstraints.gridwidth = 1;
					panel.add(label,gridBagConstraints);
					attrsMap.put(key, vals[0]);
					if("null".equals(vals[1])){
						textField = new JTextField();
						textField.setName(key);
						if(key.equals("PPLANID")) {
							textField.setText(pplanIdMap.get(technicsType));
							textField.setEditable(false);
						}
						list.add(textField);
						textFiledList.add(textField);
						gridBagConstraints.gridy = row++;
						gridBagConstraints.gridx = 1;
						gridBagConstraints.gridwidth = 1;
					    textField.setPreferredSize(new Dimension(300, 23));
					    panel.add(textField, gridBagConstraints);
					} else {
						String[] vls = vals[1].split("\\|");
						Vector<String> v = new Vector<String>();
						for (String string : vls) {
							v.add(string);
						}
						JComboBox comboBox = new JComboBox(v);
						comboBox.setName(key);
						list.add(comboBox);
						comboBoxList.add(comboBox);
						gridBagConstraints.gridy = row++;
						gridBagConstraints.gridx = 1;
						gridBagConstraints.gridwidth = 1;
					    comboBox.setPreferredSize(new Dimension(300, 23));
					    panel.add(comboBox, gridBagConstraints);
					}
				}
			}
		}
		NewTechnicsSettingJDialog.this.panel.repaint();
	}

	private void showButton(GridBagConstraints gridBagConstraints) {
		okButton.setVisible(true);
		cancelButton.setVisible(true);

		panel2.setLayout(new GridBagLayout());
		panel2.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		okButton.setPreferredSize(new Dimension(70, 23));
		okButton.setMinimumSize(new Dimension(70, 23));
		okButton.setMaximumSize(new Dimension(70, 23));
		panel2.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 5, 5), 0, 0));
		cancelButton.setPreferredSize(new Dimension(70, 23));
		cancelButton.setMinimumSize(new Dimension(70, 23));
		cancelButton.setMaximumSize(new Dimension(70, 23));
		panel2.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 5, 5), 0, 0));

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String category = NewTechnicsSettingJDialog.this.technicsCategory;
					if ("rework".equals(category) || "temp".equals(category)) {

					}
					if(fileCode==null||"".equals(fileCode.trim() )){
						  JOptionPane.showMessageDialog(frame, "工艺文件编号不能为空！", "提示",JOptionPane.INFORMATION_MESSAGE);
						  return ;
					}
					technicsNumber = fileCode;
//					String technicsNumber = WorkSpaceUtil.getTechnicsNumber(fileCode, partNumber);

					if(technicsName==null||"".equals(technicsName.trim() )){
						  JOptionPane.showMessageDialog(frame, "工艺文件名称不能为空！", "提示",JOptionPane.INFORMATION_MESSAGE);
						  return ;
					}

					String technicsPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);

					if(technicsPath!=null){
						  JOptionPane.showMessageDialog(frame, "工艺文件编号【"+fileCode.trim()+"】的工艺文件已经存在！", "提示",JOptionPane.INFORMATION_MESSAGE);
						  return ;
					}
					createProcess(true, category);
				} catch (Exception ee) {
					ee.printStackTrace();
					JOptionPane.showMessageDialog(frame, "创建工艺过程中出错！", "提示",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		//NewTechnicsSettingJDialog.this.repaint();
	}

	public void initIBAList(){
		ibaList.add("PHASE_CODE");
		ibaList.add("MINDEX");
		ibaList.add("PINDEX");
		ibaList.add("KEYCOMPONENT");
	}

	/**
	 * 将dialog屏幕居中显示
	 * @author chenyunlong
	 * @date  2013-6-13
	 * @param dialog
	 *
	 */
	public static void setMiddleOnScreenWithDialog(JDialog dialog){
		int windowWidth = dialog.getWidth();                     //获得窗口宽
	    int windowHeight = dialog.getHeight();                   //获得窗口高
	    Toolkit kit = Toolkit.getDefaultToolkit();              //定义工具包
	    Dimension screenSize = kit.getScreenSize();             //获取屏幕的尺寸
	    int screenWidth = screenSize.width;                     //获取屏幕的宽
	    int screenHeight = screenSize.height;                   //获取屏幕的高
	    dialog.setLocation(screenWidth/2-windowWidth/2, screenHeight/2-windowHeight/2);//设置窗口居中显示
	}

	private void selFileAction(){
		File f = null;
		JFileChooser  jfc = new JFileChooser ();
		jfc.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "*.doc文件";
			}

			@Override
			public boolean accept(File f) {
				if( f.isDirectory()|| f.getName().endsWith( ".doc" ) ) {
					return true;
				} else {
					return false;
				}
			}
		});

        int state=jfc.showDialog(null,"选择工艺说明文件");
	        if(JFileChooser.APPROVE_OPTION!=state){
	            return;
	        }
	        else{
	            f = jfc.getSelectedFile();//f为选择到的目录
	        }
	    String fileName = jfc.getName(f);
	    if(!fileName.endsWith(".doc"))	{
	    	fileName = fileName+ ".doc";
	    }
	    String fpath = jfc.getCurrentDirectory().getAbsolutePath() +File.separator+ fileName;
	    technicsDescFilePath.setText(fpath);
	}

	class ValidateNameNumber implements DocumentListener{

		@Override
		public void removeUpdate(DocumentEvent e) {
			validateFiled();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			validateFiled();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			validateFiled();
		}

		private void validateFiled(){
			fileCode = technicsFileCodeText.getText();
			technicsFileCodeText.setText(fileCode);
			technicsName = technicsNameText.getText();
			technicsNumber = fileCode;  //WorkSpaceUtil.getTechnicsNumber(fileCode, partNumber);
			System.out.println("technicsName="+technicsName +"  technicsNumber="+technicsNumber);
//			if(technicsNumber==null||"".equals(technicsNumber.trim())||technicsName==null||"".equals(technicsName.trim())){
//				browseTemplate.setEnabled(false);
//				browseFile.setEnabled(false);
//				okButton.setEnabled(false);
//			}else{
//				browseTemplate.setEnabled(true);
//				browseFile.setEnabled(true);
//				okButton.setEnabled(true);
//			}
		}

	}
	public void setEnglishAttributeIsShow(boolean flag){
		technicsEnglishNameLabel.setVisible(flag);
		technicsEnglishNameText.setVisible(flag);
		imageVersionLabel.setVisible(flag);
		imageVersionText.setVisible(flag);
		vseLabel.setVisible(flag);
		vseText.setVisible(flag);
	}
}