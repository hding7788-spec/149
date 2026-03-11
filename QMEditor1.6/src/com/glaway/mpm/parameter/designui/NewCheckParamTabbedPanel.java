package com.glaway.mpm.parameter.designui;

import com.glaway.mpm.parameter.actions.CheckParamTabbedPopupMenu;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.TechnicsPaceJDialog;
import org.dom4j.Attribute;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * 检验记录表主面板
 * @author zhuhao 2017.10.24
 *
 */
public class NewCheckParamTabbedPanel extends JPanel {

	private static final long serialVersionUID = -3328033468246728814L;
	private Container parentPanel;
	private JSplitPane splitpane;
	private JTabbedPane tabbedPane1;
	private JPanel tabbedPane2;
	private JScrollPane scrollPane;
	private CheckParamTabbedPopupMenu popupMenu;
	private String technicsNumber = "";
	private Element objElement;
	private List<NewCheckParamTablePanel> checkPatamTablePanelList;
	private List<String> typeList = new ArrayList<String>();
	private NewTechnicsPart frame = null;
	private static String tabTableName;
	private String imageFolder;


	public NewCheckParamTabbedPanel(Container parentPanel){
		this.parentPanel = parentPanel;
		initComponents();
	}

	private void initComponents() {
		scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		splitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		tabbedPane1 = new JTabbedPane();
		splitpane.setDividerSize(5);
		popupMenu = new CheckParamTabbedPopupMenu(this);
		scrollPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					popupMenu.show(scrollPane, e.getX(), e.getY());
				}
			}

		});
		tabbedPane1.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					popupMenu.show(tabbedPane1, e.getX(), e.getY());
				}else if(e.getButton() == MouseEvent.BUTTON1){
					NewCheckParamTablePanel newcheckparamtablepanel = (NewCheckParamTablePanel)tabbedPane1.getSelectedComponent();
					if(newcheckparamtablepanel != null){
						tabbedPane2.removeAll();
						String dybbm_Value = newcheckparamtablepanel.getTableName();
						String bzjlx_Value = newcheckparamtablepanel.getTypeName();
						String xmm_Value = newcheckparamtablepanel.getXmm_Value();
						String tbm_Value = newcheckparamtablepanel.getTbm_Value();
						String mxCs_Value = newcheckparamtablepanel.getMxCs_Value();
						NewCheckParamTablePanel2 newcheckparamtablepanel2 = new NewCheckParamTablePanel2(dybbm_Value, bzjlx_Value, xmm_Value, tbm_Value, mxCs_Value, null, this);
						newcheckparamtablepanel2.setUIValues();
						tabbedPane2.add(newcheckparamtablepanel2);
						tabbedPane2.validate();
						tabbedPane2.repaint();
					}
				}
			}
		});
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
	}

	public void setUIValues() {
		tabbedPane1.removeAll();
		String objNumber = "";
		String stepNumber = "";

		TechnicsPaceJDialog technicsPaceJDialog = (TechnicsPaceJDialog) parentPanel;
		objElement = technicsPaceJDialog.getPaceElement();
		Element techElement = technicsPaceJDialog.getStepPanel().getTechElement();
		technicsNumber = techElement.attributeValue("technicsNumber");
		stepNumber = technicsPaceJDialog.getStepElement().attributeValue("stepNumber");//工序编号
		objNumber = objElement.attributeValue("stepNumber");//工步编号
		imageFolder = technicsPaceJDialog.getImageFolder();
		frame = (NewTechnicsPart) ((TechnicsPaceJDialog) parentPanel)
				.getFrame();
		checkPatamTablePanelList = new ArrayList<NewCheckParamTablePanel>();
		String xmlPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
		try{
//			File file = new File(xmlPath + File.separator + technicsNumber +".xml");
//			SAXReader reader = new SAXReader();
//			Document doc = reader.read(file);
//			Element root = doc.getRootElement();
			Class<?>[] cellData = null;
//			for (Element element : XmlUtil.getElementsByName(root, "QMFawTechnicsInfo")) {
//				for (Element temp : XmlUtil.getElementsByName(element, "steps")) {
//					for (Element temp1 : XmlUtil.getElementsByName(temp, "QMProcedureInfo")) {
//						String stepNum = parseName(temp1, "stepNumber");
//						if(!stepNumber.equals(stepNum))
//							continue;
//						for (Element temp2 : XmlUtil.getElementsByName(temp1, "paces")) {
//							for (Element temp3 : XmlUtil.getElementsByName(temp2, "QMProcedureInfo")) {
//								String paceNum = parseName(temp3, "stepNumber");
//								if(paceNum.equals(objNumber)){
									for (Element temp4 : XmlUtil.getElementsByName(objElement, "checkRecordTables")) {
										for (Element temp5 : XmlUtil.getElementsByName(temp4, "parameterTable")) {
											Vector<Vector<Object>> dataVec =  new Vector<Vector<Object>>();
											Vector<Object> vecList = null;
											List<String> tableType = new ArrayList<String>();
											String dybbm_Value = parseName(temp5, "name");
											String bzjlx_Value = parseName(temp5, "type");
											String xmm_Value = parseName(temp5, "projectName");
											String xmm_Path = parseName(temp5, "xmmPath");
											String tbm_Value = parseName(temp5, "tableName");
											String tbm_Path = parseName(temp5, "tbmPath");
											String mxCs_Value = parseName(temp5, "eachName");
											String uuid_Value = parseName(temp5, "bsoID");
											String gcz_Value = "";
											String spc_Value = "";
											String xpc_Value = "";
											String yq_Value = "";
											String jyl_Value = "";
											String jll_Value= "";
											tableType.add(dybbm_Value);
											tableType.add(bzjlx_Value);
											tableType.add(xmm_Value);
											tableType.add(tbm_Value);
											tableType.add(mxCs_Value);
											tableType.add(uuid_Value);
											for (Element temp6 : XmlUtil.getElementsByName(temp5, "parameter")) {
												for (Element temp7 : XmlUtil.getElementsByName(temp6, "values")) {
													Object[] vectorList = null ;
													vecList = new Vector<Object>();
													for (Element temp8 : XmlUtil.getElementsByName(temp7, "value")) {
														if("检测项".equals(parseName(temp8, "columnName"))){
															for (Element temp9 : XmlUtil.getElementsByName(temp8, "attribute")) {
																jyl_Value = temp9.getText();
															}
														}
														if("记录项".equals(parseName(temp8, "columnName"))){
															for (Element temp9 : XmlUtil.getElementsByName(temp8, "attribute")) {
																jll_Value = temp9.getText();
															}
														}
														if("公称值".equals(parseName(temp8, "columnName"))){
															for (Element temp9 : XmlUtil.getElementsByName(temp8, "attribute")) {
																gcz_Value = temp9.getText();
															}
														}
														if("上偏差".equals(parseName(temp8, "columnName"))){
															for (Element temp9 : XmlUtil.getElementsByName(temp8, "attribute")) {
																spc_Value = temp9.getText();
															}
														}
														if("下偏差".equals(parseName(temp8, "columnName"))){
															for (Element temp9 : XmlUtil.getElementsByName(temp8, "attribute")) {
																xpc_Value = temp9.getText();
															}
														}
														if("要求".equals(parseName(temp8, "columnName"))){
															for (Element temp9 : XmlUtil.getElementsByName(temp8, "attribute")) {
																yq_Value = temp9.getText();
															}
														}
													}
													if("检测类".equals(bzjlx_Value)){
														vectorList = new Object[]{"",false,jyl_Value,gcz_Value, spc_Value,xpc_Value,"",""};
													}else if("记录类".equals(bzjlx_Value)){
														vectorList = new Object[]{"",false,jll_Value,yq_Value, "",""};
													}
													for(int i=0;i<vectorList.length;i++){
														vecList.add(vectorList[i]);
													}
												}
												dataVec.add(vecList);
											}
											tabbedPane2 = new JPanel();
											NewCheckParamTablePanel newcheckparamtablepanel = new NewCheckParamTablePanel(dybbm_Value, bzjlx_Value, xmm_Value,xmm_Path, tbm_Value,tbm_Path, mxCs_Value, uuid_Value,
													cellData, this, parentPanel,technicsNumber,tableType,dataVec,frame,xmlPath);
											newcheckparamtablepanel.setUIValues(bzjlx_Value);
											newcheckparamtablepanel.setObjElement(objElement);
											checkPatamTablePanelList.add(newcheckparamtablepanel);
											NewCheckParamTablePanel2 newcheckparamtablepanel2 = new NewCheckParamTablePanel2(dybbm_Value, bzjlx_Value, xmm_Value, tbm_Value, mxCs_Value, this,null);
											newcheckparamtablepanel2.setUIValues();
											tabbedPane1.addTab(dybbm_Value + "("+ bzjlx_Value +")", newcheckparamtablepanel);
											tabbedPane2.add(newcheckparamtablepanel2);
											splitpane.setLeftComponent(tabbedPane2);
											splitpane.setRightComponent(tabbedPane1);
											scrollPane.setViewportView(splitpane);
										}
									}
//								}
//							}
//						}
//					}
//				}
//			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}


	public void addCheckRecordTableType(String dybbm_Value, String bzjlx_Value, String xmm_Value,String xmm_Path, String tbm_Value,String tbm_Path, String mxCs_Value, String uuid_Value){

		//判断是否存在表元素
		String name1 = "";
		String xmlPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
		String tabTableName = dybbm_Value + "("+ bzjlx_Value +")"; //add by lkc 2017.121
		this.tabTableName = tabTableName;

		if(typeList!=null && !typeList.isEmpty()){
			for(int i=0;i<typeList.size();i++){
				if(uuid_Value.equals(typeList.get(i))){
					JOptionPane.showMessageDialog(null, "已存在该检验记录表！");
					return ;
				}
			}
		}
		//判断over
		tabbedPane2 = new JPanel();
		Class<?>[] cellData = null;
		if("检测类".equals(bzjlx_Value)){
			cellData = new Class<?>[]{String.class,Boolean.class,String.class, String.class,String.class,String.class,String.class};
		}else if("记录类".equals(bzjlx_Value)){
			cellData = new Class<?>[]{String.class,Boolean.class,String.class, String.class,String.class};
		}
		List<String> tableType = new ArrayList<String>();
		tableType.add(dybbm_Value);
		tableType.add(bzjlx_Value);
		tableType.add(xmm_Value);
		tableType.add(tbm_Value);
		tableType.add(mxCs_Value);
		tableType.add(uuid_Value);
		tableType.add(xmm_Path);
		tableType.add(tbm_Path);


		NewCheckParamTablePanel newcheckparamtablepanel = new NewCheckParamTablePanel(dybbm_Value,bzjlx_Value,xmm_Value,xmm_Path,tbm_Value,tbm_Path,mxCs_Value,uuid_Value,
				cellData,this,parentPanel,technicsNumber,tableType,null, frame,xmlPath);
		newcheckparamtablepanel.setUIValues(bzjlx_Value);
		newcheckparamtablepanel.setObjElement(objElement);
		checkPatamTablePanelList.add(newcheckparamtablepanel);
		NewCheckParamTablePanel2 newcheckparamtablepanel2 = new NewCheckParamTablePanel2(dybbm_Value, bzjlx_Value, xmm_Value, tbm_Value, mxCs_Value, this,null);
		newcheckparamtablepanel2.setUIValues();
		tabbedPane1.addTab(dybbm_Value + "("+ bzjlx_Value +")", newcheckparamtablepanel);
		tabbedPane2.add(newcheckparamtablepanel2);
		splitpane.setLeftComponent(tabbedPane2);
		splitpane.setRightComponent(tabbedPane1);
		scrollPane.setViewportView(splitpane);
		if(typeList!=null && !typeList.isEmpty()){
			for(int i=0;i<typeList.size();i++){
				if(uuid_Value.equals(typeList.get(i))){
					continue;
				}else{
					typeList.add(uuid_Value);
				}
			}
		}else{
			typeList.add(uuid_Value);
		}

	}
	public static String getTabTableName(){
		return tabTableName;
	}
	public void removeCheckParamTableType(List<NewCheckParamTablePanel> checkParamTablePanelList)  {
		NewCheckParamTablePanel newcheckparamtablepanel = null;
		if(checkParamTablePanelList == null){
			newcheckparamtablepanel = (NewCheckParamTablePanel)tabbedPane1.getSelectedComponent();
			deleteTable(newcheckparamtablepanel);
		}else{
			int j = 0;
			int tableSzie = checkParamTablePanelList.size();
			for(int i=0; i < tableSzie; i++){
				deleteTable(checkParamTablePanelList.get(i-j));
				j++;
			}
		}
	}

	public void deleteTable(NewCheckParamTablePanel newcheckparamtablepanel){
		if (newcheckparamtablepanel != null) {
			tabbedPane1.remove(newcheckparamtablepanel);
			checkPatamTablePanelList.remove(newcheckparamtablepanel);
			String tablbID = (String)newcheckparamtablepanel.getId();
			XmlUtility.removeCheckTable(objElement, tablbID, technicsNumber);
			if(typeList!=null && !typeList.isEmpty()){
				for(int i=0;i<typeList.size();i++){
					if(tablbID.equals(typeList.get(i))){
						typeList.remove(i);
						i--;
					}
				}
			}
			if(checkPatamTablePanelList==null || checkPatamTablePanelList.isEmpty()){
				splitpane.remove(tabbedPane2);
				this.validate();
				this.repaint();
			}
		}
	}


	public static String parseName(Element element, String type) {
		Attribute attr = element.attribute(type);
		return attr == null ? null : attr.getValue();
	}

	public List<NewCheckParamTablePanel> getCheckParamTablePanelList() {
		return checkPatamTablePanelList;
	}

	public void setCheckParamTablePanelList(List<NewCheckParamTablePanel> checkPatamTablePanelList){
		this.checkPatamTablePanelList = checkPatamTablePanelList;
	}

	public int getSelectedTAB() {
		return tabbedPane1.getSelectedIndex();
	}

	public void changeValue(String dybbm_Value, String bzjlx_Value, String xmm_Value, String tbm_Value, String mxCs_Value, int selected) {
		tabbedPane1.setTitleAt(selected, dybbm_Value+"("+bzjlx_Value+")");
		tabbedPane2.removeAll();
		NewCheckParamTablePanel2 newcheckparamtablepanel2 = new NewCheckParamTablePanel2(dybbm_Value, bzjlx_Value, xmm_Value, tbm_Value, mxCs_Value, this, null);
		newcheckparamtablepanel2.setUIValues();
		tabbedPane2.add(newcheckparamtablepanel2);
		tabbedPane2.validate();
		tabbedPane2.repaint();
	}

	public String getImageFolder(){
		return imageFolder;
	}
}
