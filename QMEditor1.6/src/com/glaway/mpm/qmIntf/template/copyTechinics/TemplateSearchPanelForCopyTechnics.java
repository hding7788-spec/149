package com.glaway.mpm.qmIntf.template.copyTechinics;

import com.glaway.mpm.parameter.service.ProcessParameterToWCIntf;
import com.glaway.mpm.qmIntf.template.TpNode;
import com.glaway.mpm.qmIntf.template.TpTree;
import com.glaway.mpm.qmIntf.template.TpTreeNode;
import com.glaway.mpm.qmIntf.template.TpTreeXmlUtil;
import com.glaway.mpm.util.*;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TemplateIntf;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;

public class TemplateSearchPanelForCopyTechnics extends JPanel {
	private static final long serialVersionUID = 1L;

	private static VaLogger logger = VaLogger.getLogger(TemplateSearchPanelForCopyTechnics.class);

	private JButton sureButton;
	private JButton cancelButton;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private JPanel rightUpPanel;
	private JPanel mainPanel;
	private JScrollPane jScrollPane;
	public TpTree tpTree;
	private String filePath;
	public static Vector<Object> vector;
	private String type;
	private JDialog dialog;
	private	Document document;
	public TemplateSearchPanelForCopyTechnics(Document document, JDialog dialog) {
		this.dialog = dialog;
		this.document = document;
		Element techElement = XmlUtility.getTechnicsElement(document);
		String technicsType = techElement.attributeValue("technicsType");
		String[][] technicsTypes = LoadConfig.getInstance().getTechnicsType();
		String s = "";
		for(int i=0;i<technicsTypes[1].length;i++){
			System.out.println("从模板创建====technicsTypes[1][i]=======" + technicsTypes[1][i]);
			if(technicsTypes[1][i].equals(technicsType)){
				s = technicsTypes[0][i];
			}
		}
		String templatePath = WorkSpaceUtil.getTempletRootPath();
		this.filePath = templatePath;
		this.type= s;
		init();
	}

	private void init() {
		initLookAndFeel();
		initDimension();
		initComponents();
		initLayout();
		initActions();
		loadInitDatas();
	}

	private void initLookAndFeel() {

	}

	private void initDimension() {

	}

	private void initComponents() {
		mainPanel = new JPanel();
		leftPanel = new JPanel();
		jScrollPane = new JScrollPane();
		tpTree = TpTreeXmlUtil.searchTemplates(filePath, type);
		tpTree.updateUI();
		SwingUtil.expandAll(tpTree);
		jScrollPane.setViewportView(tpTree);
		rightPanel = new JPanel();
		rightUpPanel = new JPanel();

		sureButton = new JButton();
		cancelButton = new JButton();
	}

	private void initLayout() {
		setSize(400, 500);
		jScrollPane.getViewport().setBackground(Color.WHITE);
		jScrollPane.setPreferredSize(new Dimension(300, 420));
		leftPanel.add(jScrollPane);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(20, 0, 10, 0);
		c.gridx = 1;
		c.gridy = 0;
		rightUpPanel.setLayout(new GridBagLayout());
		rightUpPanel.add(sureButton, c);
		c.insets = new Insets(10, 0, 10, 0);
		c.gridy = 1;
		rightUpPanel.add(cancelButton, c);

		rightPanel.add(rightUpPanel);
		mainPanel.setLayout(new BorderLayout(1, 2));
		mainPanel.add(leftPanel, BorderLayout.WEST);
		mainPanel.add(rightPanel, BorderLayout.CENTER);
		add(mainPanel);
	}

	String[] copyAttris = {"partNumber","version","technicsName","pplanNumber","pplanName","technicsType","PPLANID","processTaskItemOid","technicsEnglishName","imageVersion","VSE"
			,"technicsNumber","technicsType","isTabular","unite","treePath","partNumber","partName","partOid","partVersion","materialType","workShop","backupRate","maxBackupCount"
			,"backupReason","isKey","isSpecial","partType","pbomLifecycle","eu_version","e_version","partVersion","isPartKey","occId","material","dutu","remark","useCount","gysl","creator"
			,"creatorOid","creatorDisplay","productNumber","productName","parentPartNumber","parentPartName","parentPartOid","isKey"
			,"isSpecial","MINDEX","PINDEX","KEYCOMPONENT","PHASE_CODE","DEPT","PPLANTYPE","TEMPNO","ZFFLAG","SECRET","PCNO","CINDEX","XHPHCL"
			,"CSIZE","JSTJBZH","CMAT_UP","CMAT_DOWN","PZGGBZH","JDDJ","CLZT","ZLDJ","ZQCLBZH","ZQCLBZH","ZQCLMC","XHPH","JSTJ"
			,"JBCLMC","CMAT","MTYPE","ZZCJ","FZCJ","materialNumber","materialName","SHORTNAME","STANDARDNUMBER","MECHANICALPROPERTYORHARDNESS","SURFACETREATMENT"
			,"HEATTREATMENT","PRODUCTFORM","PRODUCTLEVEL","PLATECSCREWFORM","ISIMPORT","SPECIALINSTRUCTION","MEASUREUNIT","TYPE"
			,"TYPESTANDARD","QUALITYLEVEL","TOTALSTANDARD","DETAILSTANDARD","PACKAGINGFORM","OUTLINESIZE","SPECIALCONDITION","EXTRACONDITION","MATTYPE","lifecycle","lifeCycleState"};
	String[] copyIBAAttris = {"MINDEX",""};
	String[] copyTags = {"steps","PEITAOTABLE","CLDE","GYDE","PEITAOTABLE"};
	private void initActions() {
		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vector = null;
				Object node = tpTree.getLastSelectedPathComponent();
				if (node == null || node instanceof TpTreeNode) {
					SwingUtil.showMessageDialog("请选择工艺模板", "提示", 2);
				} else {
					TpNode tpNode = (TpNode) node;
					String name = tpNode.getTemplate().getName();
					TpTreeNode treeNode = (TpTreeNode) tpNode.getParent();
					vector = new Vector<Object>();
					TpTreeNode parentNode = (TpTreeNode) treeNode.getParent();
					logger.debug(parentNode.getName());
					String templatePath = WorkSpaceUtil.getTempletRootPath();
					Element techElement = XmlUtility.getTechnicsElement(document);
					Map<String,String> oldAttris = new LinkedHashMap<String, String>();
					for(String s:copyAttris){
						oldAttris.put(s,techElement.attributeValue(s));
					}
					String technicsNumber = techElement.attributeValue("technicsNumber");

					String technicsPath = WorkSpaceUtil.getTechnicsPath(techElement);
					if (parentNode.getName().equals("个人工艺模板库")) {
						String templateXMLPath = templatePath + type + File.separator + name;
						FilesUtil.copyDirectiory(templateXMLPath, technicsPath);

						String tempFilePath  = technicsPath+ File.separator+name+".xml";
						File tempFile = new File(tempFilePath);

						Document tempDocument = XmlUtility.getDocument(tempFile);
						Element tempElement = XmlUtility.getTechnicsElement(tempDocument);
						Set<Map.Entry<String, String>> oldAttrisEntry = oldAttris.entrySet();
						for(Map.Entry<String, String> entry:oldAttrisEntry){
							XmlUtility.setAttributeValue(tempElement,entry.getKey(),entry.getValue());
						}
						try {
							//白羽表处理 新建一份引用工艺的白羽表
							//工艺端
							List<Element> tecBaiyuImageList = tempElement.selectNodes("schemaData/schemaInfo");
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
										Element schemaData = XmlUtility.getSchemaData(tempElement);
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
							List<Element> steps = XmlUtility.getAllSteps(tempElement);
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

							XmlUtility.saveDocument(tempElement.getDocument(), tempFile);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						File oldFile = new File(technicsPath+ File.separator+technicsNumber+".xml");

						FilesUtil.copyFile(tempFile, oldFile);
					} else {
						//vector = TemplateIntf.downloadProcessTemplate(tpNode.getTemplate().getOid());

						SwingUtil.showMessageDialog("该功能目前只支持本地模板库服用，暂不支持公共模板库", "提示", 2);
					}


					dialog.dispose();
				}
			}
		});
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				vector = null;
				dialog.dispose();
			}
		});
	}

	private void loadInitDatas() {
		sureButton.setText("确定");
		cancelButton.setText("取消");
	}

}