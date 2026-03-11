package com.glaway.mpm.parameter.designui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.dom4j.Element;

import com.glaway.mpm.dataPackage.ui.DPLeftTechnicTreePanel;
import com.glaway.mpm.dataPackage.ui.DPRightTechnicInfoPanel;
import com.glaway.mpm.mesDataSearch.helper.MesDataSearchProcesser;
import com.glaway.mpm.mesDataSearch.ui.MesDataMainInfoPanel;
import com.glaway.mpm.mesParameter.helper.MesParameterProcessor;
import com.glaway.mpm.mesParameter.ui.LeftTechnicTreePanel;
import com.glaway.mpm.mesParameter.ui.MesParameterMainFrame;
import com.glaway.mpm.mesParameter.ui.RightTechnicInfoPanel;
import com.glaway.mpm.parameter.actions.SpecialParamTabbedPopupMenu;
import com.glaway.mpm.parameter.helper.MPMParameterProcessor;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.TechnicsPaceJDialog;
import com.glaway.mpm.view.TechnicsStepJPanel_XW;

/**
 * 特殊记录表主面板
 * @author Wangxl
 *
 */
public class NewSpecialParamTabbedPanel extends JPanel {

	private static final long serialVersionUID = 2708509949702663666L;
	private Container parentPanel;
	private JTabbedPane tabbedPane;
	private SpecialParamTabbedPopupMenu popupMenu;
	private boolean isApproved = false;
	private String productNumber = "";
	private String technicsNumber = "";
	private String technicsType = "";
	private String objType = "";
	private String objNumber = "";
	private String lukahao = "";
	private String gxPK = "";
	private String isZF = "";
	private String jianyanyuan = "";
	private String caozuoyuan = "";
	private String bsoID;
	private String version;
	private Element objElement = null;
	private List<String> tables = new ArrayList<String>();
	private List<NewSpecialParamTablePanel> specialParamTablePanelList;

	public NewSpecialParamTabbedPanel(Container parentPanel) {
		this.parentPanel = parentPanel;
		initComponents();
	}

	private void initComponents() {
		popupMenu = new SpecialParamTabbedPopupMenu(this);
		tabbedPane = new JTabbedPane();
		tabbedPane.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					//弹出菜单
					popupMenu.show(tabbedPane, e.getX(), e.getY());
					popupMenu.setStatus(parentPanel);
				}
			}

		});

		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
	}

	public void setUIValues() {
		tabbedPane.removeAll();
		tables.clear();
		String state = "";
		if (parentPanel instanceof TechnicsStepJPanel_XW) {
			TechnicsStepJPanel_XW technicsStepJPanel_XW = (TechnicsStepJPanel_XW) parentPanel;
			Element techElement = technicsStepJPanel_XW.getTechElement();
			objElement = technicsStepJPanel_XW.getStepElement();
			state = techElement.attributeValue("lifecycle");
			technicsNumber = techElement.attributeValue("technicsNumber");
			technicsType = techElement.attributeValue("technicsType");
			version = techElement.attributeValue("version");
			objNumber = objElement.attributeValue("stepNumber");
			objType = "工序";
			bsoID = objElement.attributeValue("bsoID");
		} else if (parentPanel instanceof TechnicsPaceJDialog) {
			TechnicsPaceJDialog technicsPaceJDialog = (TechnicsPaceJDialog) parentPanel;
			Element techElement = technicsPaceJDialog.getStepPanel().getTechElement();
			objElement = technicsPaceJDialog.getPaceElement();
			Element parentElement = objElement.getParent().getParent();
			String parentObjNumber = parentElement.attributeValue("stepNumber");
			state = techElement.attributeValue("lifecycle");
			technicsNumber = techElement.attributeValue("technicsNumber");
			technicsType = techElement.attributeValue("technicsType");
			version = techElement.attributeValue("version");
			objNumber = objElement.attributeValue("stepNumber");
			objNumber = parentObjNumber + "-" + objElement.attributeValue("stepNumber");
			objType = "工步";
			bsoID = parentElement.attributeValue("bsoID") + objElement.attributeValue("bsoID");
		}
		//取大版本
		if(version.contains(".")){
			version = version.substring(0, version.indexOf("."));
		}
		isApproved = (!"正在工作".equals(state) && !"修改中".equals(state)) ? true : false;
		List<CmParamTableType> list = MPMParameterProcessor.getParamTableTypes(technicsNumber, objType, objNumber, isApproved, bsoID, version);
		if (list != null) {
			MPMParameterProcessor.downloadAllImages();
			specialParamTablePanelList = new ArrayList<NewSpecialParamTablePanel>();
			int index = 0;
			for (CmParamTableType paramTableType : list) {
				/**temp历史数据处理*/
				MPMParameterProcessor.setSpecialParamTableIndex(String.valueOf(paramTableType.getOid()),technicsNumber, objType, objNumber, String.valueOf(index));
				/**temp*/
				NewSpecialParamTablePanel newSpecialParamTablePanel = new NewSpecialParamTablePanel(productNumber, technicsNumber,technicsType, objNumber, objType, isApproved, lukahao, gxPK, "", jianyanyuan, caozuoyuan, paramTableType, parentPanel, bsoID, version);
				newSpecialParamTablePanel.setUIValues();
				newSpecialParamTablePanel.setObjElement(objElement);
				specialParamTablePanelList.add(newSpecialParamTablePanel);

				tabbedPane.addTab(paramTableType.getName(), newSpecialParamTablePanel);
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
				tables.add(paramTableType.getEnName());
				index++;
			}
		}
	}
	public void setMesUIValues(){
		tabbedPane.removeAll();
		tables.clear();
		String state = "";
		if(parentPanel instanceof RightTechnicInfoPanel){
			RightTechnicInfoPanel rightTechnicInfoPanel = (RightTechnicInfoPanel) parentPanel;
			LeftTechnicTreePanel leftTechnicTreePanel = rightTechnicInfoPanel.getLeftTechnicTreePanel();
			productNumber = leftTechnicTreePanel.getProductNumber();
			technicsNumber = leftTechnicTreePanel.getTechnicNumber();
			objNumber = leftTechnicTreePanel.getObjNumber();
			objType = leftTechnicTreePanel.getObjType();
			lukahao = leftTechnicTreePanel.getLukahao();
			gxPK = leftTechnicTreePanel.getGxPK();
			isZF = leftTechnicTreePanel.getIsZF();
			bsoID = leftTechnicTreePanel.getBsoID();
			version = leftTechnicTreePanel.getVersion();
			jianyanyuan = leftTechnicTreePanel.getJianyanyuan();
			caozuoyuan = leftTechnicTreePanel.getCaozuoyuan();
			state = leftTechnicTreePanel.getState();
			System.out.println("productNumber===" + productNumber + ",technicsNumber===" + technicsNumber + ",objNumber===" + objNumber + ",objType===" + objType + ",lukahao===" + lukahao + ",gxPK===" + gxPK);
		}
		//取大版本
		if(version.contains(".")){
			version = version.substring(0, version.indexOf("."));
		}
		isApproved = !"已批准".equals(state) ? true : false;
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("processNumber", productNumber);
		paramsMap.put("technicsNumber", technicsNumber);
		paramsMap.put("objType", objType);
		paramsMap.put("objNumber", objNumber);
		paramsMap.put("lukahao", lukahao);
		paramsMap.put("gxPK", gxPK);
		paramsMap.put("isZF", isZF);
		paramsMap.put("bsoId", bsoID);
		paramsMap.put("version", version);
		List<CmParamTableType> list = MPMParameterProcessor.getParamTableTypesForMes(technicsNumber, objType, objNumber, isApproved, bsoID, version);
		List<CmParamTableType> mesList = MesParameterProcessor.getMesParamTableTypes(isApproved, paramsMap);
		MPMParameterProcessor.downloadAllImages();

		if (list != null) {
			specialParamTablePanelList = new ArrayList<NewSpecialParamTablePanel>();
			for (CmParamTableType paramTableType : list) {
				boolean isMesDataExist = MesParameterProcessor.isMesDataExist(paramTableType, paramsMap);
				if(isMesDataExist){
					for(CmParamTableType mesParamTableType : mesList){
						if(mesParamTableType.getEnName().equals(paramTableType.getEnName())){
							paramTableType = mesParamTableType;
						}
					}
				}
				NewSpecialParamTablePanel newSpecialParamTablePanel = new NewSpecialParamTablePanel(productNumber, technicsNumber, technicsType, objNumber, objType, isApproved, lukahao, gxPK, isZF, jianyanyuan, caozuoyuan, paramTableType, parentPanel, bsoID, version);
				newSpecialParamTablePanel.setMesUIValues();
				newSpecialParamTablePanel.setObjElement(objElement);
				specialParamTablePanelList.add(newSpecialParamTablePanel);

				tabbedPane.addTab(paramTableType.getName(), newSpecialParamTablePanel);
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
						Component specialTable = tabbedPane.getSelectedComponent();
						Map<String, String> processNumberMap = null;
						if(specialTable instanceof NewSpecialParamTablePanel){
							NewSpecialParamTablePanel specialTablePanel = (NewSpecialParamTablePanel) specialTable;
							Map<String, String> paramsMap = new HashMap<String, String>();
							paramsMap.put("tableName", "mes" + specialTablePanel.getParamTableType().getEnName());
							paramsMap.put("technicsNumber", technicsNumber);
							paramsMap.put("objNumber", objNumber);
							paramsMap.put("objType", objType);
							paramsMap.put("lukahao", lukahao);
							paramsMap.put("isZF", isZF);
							paramsMap.put("bsoId", bsoID);
							paramsMap.put("version", version);
							processNumberMap = MesParameterProcessor.getProcessNumberGroup(paramsMap);
						}
						MesParameterMainFrame.getMesParameterMainPanel().getGroupComboBox().removeAllItems();
						MesParameterMainFrame.getMesParameterMainPanel().getGroupComboBox().addItem("");
						if(processNumberMap != null){
							MesParameterMainFrame.getMesParameterMainPanel().getRightTechnicInfoPanel().setProcessNumberMap(processNumberMap);
							for(Map.Entry<String, String> entry : processNumberMap.entrySet()){
								String key = entry.getKey();
								MesParameterMainFrame.getMesParameterMainPanel().getGroupComboBox().addItem(key);
							}
						}
						MesParameterMainFrame.getMesParameterMainPanel().getGroupComboBox().setSelectedItem("");
					}

				});
				tables.add(paramTableType.getEnName());
			}
		}
	}
	public void setDataPackageUIValues(){
		tabbedPane.removeAll();
		tables.clear();
		String state = "";
		if(parentPanel instanceof DPRightTechnicInfoPanel){
			DPRightTechnicInfoPanel rightTechnicInfoPanel = (DPRightTechnicInfoPanel) parentPanel;
			DPLeftTechnicTreePanel leftTechnicTreePanel = rightTechnicInfoPanel.getLeftTechnicTreePanel();
			productNumber = leftTechnicTreePanel.getProductNumber();
			technicsNumber = leftTechnicTreePanel.getTechnicNumber();
			objNumber = leftTechnicTreePanel.getObjNumber();
			objType = leftTechnicTreePanel.getObjType();
			state = leftTechnicTreePanel.getState();
		}
		isApproved = !"已批准".equals(state) ? true : false;
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("processNumber", productNumber);
		paramsMap.put("technicsNumber", technicsNumber);
		paramsMap.put("objType", objType);
		paramsMap.put("objNumber", objNumber);
		paramsMap.put("lukahao", lukahao);
		paramsMap.put("gxPK", gxPK);
		paramsMap.put("isZF", isZF);
		List<CmParamTableType> list = MPMParameterProcessor.getParamTableTypesForMes(technicsNumber, objType, objNumber, isApproved, bsoID, version);
		List<CmParamTableType> mesList = MesParameterProcessor.getMesParamTableTypes(isApproved, paramsMap);
		MPMParameterProcessor.downloadAllImages();
		if (list != null) {
			specialParamTablePanelList = new ArrayList<NewSpecialParamTablePanel>();
			for (CmParamTableType paramTableType : list) {
				boolean isMesDataExist = MesParameterProcessor.isMesDataExist(paramTableType, paramsMap);
				if(isMesDataExist){
					for(CmParamTableType mesParamTableType : mesList){
						if(mesParamTableType.getEnName().equals(paramTableType.getEnName())){
							paramTableType = mesParamTableType;
						}
					}
				}
				NewSpecialParamTablePanel newSpecialParamTablePanel = new NewSpecialParamTablePanel(productNumber, technicsNumber, technicsType, objNumber, objType, isApproved, lukahao, gxPK,isZF, jianyanyuan, caozuoyuan, paramTableType, parentPanel,bsoID, version);
				newSpecialParamTablePanel.setDataPackageUIValues();
				newSpecialParamTablePanel.setObjElement(objElement);
				specialParamTablePanelList.add(newSpecialParamTablePanel);

				tabbedPane.addTab(paramTableType.getName(), newSpecialParamTablePanel);
				tables.add(paramTableType.getEnName());
			}
		}
	}

	public void setDataSearchUIValues(){
		tabbedPane.removeAll();
		tables.clear();
		String lukahao = "";
		String productNumber ="";
		String technicsNumber = "";
		if(parentPanel instanceof MesDataMainInfoPanel){
			MesDataMainInfoPanel mesDataMainInfoPanel = (MesDataMainInfoPanel) parentPanel;
			lukahao = "";
			productNumber = mesDataMainInfoPanel.getProductNumber();
			technicsNumber = mesDataMainInfoPanel.getTechnicsNumber();
		}
		List<CmParamTableType> list = MesDataSearchProcesser.getMesDataSearchParamTableTypes(technicsNumber, productNumber, lukahao);
		if (list != null) {
			specialParamTablePanelList = new ArrayList<NewSpecialParamTablePanel>();
			for (CmParamTableType paramTableType : list) {
				NewSpecialParamTablePanel newSpecialParamTablePanel = new NewSpecialParamTablePanel(productNumber, technicsNumber, technicsType, objNumber, objType, isApproved, lukahao, gxPK,isZF, jianyanyuan, caozuoyuan, paramTableType, parentPanel,bsoID, version);
				newSpecialParamTablePanel.setMesDataSearchUIValues();
				newSpecialParamTablePanel.setObjElement(objElement);
				specialParamTablePanelList.add(newSpecialParamTablePanel);

				tabbedPane.addTab(paramTableType.getName(), newSpecialParamTablePanel);
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
				/**end*/
				tables.add(paramTableType.getEnName());
			}
		}
	}

	public void addParamTableType(CmParamTableType paramTableType) {
		NewSpecialParamTablePanel newSpecialParamTablePanel = new NewSpecialParamTablePanel(productNumber, technicsNumber, technicsType, objNumber, objType, isApproved, lukahao, gxPK,isZF, jianyanyuan, caozuoyuan, paramTableType, parentPanel, bsoID, version);
		newSpecialParamTablePanel.setUIValues();
		newSpecialParamTablePanel.setObjElement(objElement);
		specialParamTablePanelList.add(newSpecialParamTablePanel);

		tabbedPane.addTab(paramTableType.getName(), newSpecialParamTablePanel);
		tables.add(paramTableType.getEnName());
	}

	public void removeParamTableType() {
		NewSpecialParamTablePanel newSpecialParamTablePanel = (NewSpecialParamTablePanel) tabbedPane.getSelectedComponent();
		if (newSpecialParamTablePanel != null) {
			tabbedPane.remove(newSpecialParamTablePanel);
			specialParamTablePanelList.remove(newSpecialParamTablePanel);

			CmParamTableType paramTableType = newSpecialParamTablePanel.getParamTableType();
			tables.remove(paramTableType.getEnName());
			MPMParameterProcessor.deleteObjToParamTableLink(technicsNumber, objType, objNumber, String.valueOf(paramTableType.getOid()), bsoID, version);
			//删除表中数据
			MPMParameterProcessor.deleteTableParams(paramTableType.getEnName(), technicsNumber, objType, objNumber, bsoID, version);
			XmlUtility.removeParamTable(objElement, paramTableType, technicsNumber);
		}
	}
	public void tabMoveForward(){
		int index = tabbedPane.getSelectedIndex();
		if(index == 0){
			return;
		}
		NewSpecialParamTablePanel newSpecialParamTablePanel = (NewSpecialParamTablePanel) tabbedPane.getSelectedComponent();
		NewSpecialParamTablePanel newSpecialParamTablePanel2 = (NewSpecialParamTablePanel) tabbedPane.getComponentAt(index - 1);

		if (newSpecialParamTablePanel != null) {
			CmParamTableType paramTableType = newSpecialParamTablePanel.getParamTableType();
			String oid = String.valueOf(paramTableType.getOid());
			MPMParameterProcessor.setSpecialParamTableIndex(oid, technicsNumber, objType, objNumber, String.valueOf(index - 1));
		}
		if (newSpecialParamTablePanel2 != null) {
			CmParamTableType paramTableType2 = newSpecialParamTablePanel2.getParamTableType();
			String oid2 = String.valueOf(paramTableType2.getOid());
			MPMParameterProcessor.setSpecialParamTableIndex(oid2, technicsNumber, objType, objNumber, String.valueOf(index));
		}
		this.setUIValues();
	}
	public void tabMoveBackword(){
		int index = tabbedPane.getSelectedIndex();
		if(index == tables.size() - 1){
			return;
		}
		NewSpecialParamTablePanel newSpecialParamTablePanel = (NewSpecialParamTablePanel) tabbedPane.getSelectedComponent();
		NewSpecialParamTablePanel newSpecialParamTablePanel2 = (NewSpecialParamTablePanel) tabbedPane.getComponentAt(index + 1);

		if (newSpecialParamTablePanel != null) {
			CmParamTableType paramTableType = newSpecialParamTablePanel.getParamTableType();
			String oid = String.valueOf(paramTableType.getOid());
			MPMParameterProcessor.setSpecialParamTableIndex(oid, technicsNumber, objType, objNumber, String.valueOf(index + 1));
		}
		if (newSpecialParamTablePanel2 != null) {
			CmParamTableType paramTableType2 = newSpecialParamTablePanel2.getParamTableType();
			String oid2 = String.valueOf(paramTableType2.getOid());
			MPMParameterProcessor.setSpecialParamTableIndex(oid2, technicsNumber, objType, objNumber, String.valueOf(index));
		}
		this.setUIValues();
	}

	public List<String> getTables() {
		return tables;
	}

	public boolean isApproved() {
		return isApproved;
	}

	public String getTechnicsNumber() {
		return technicsNumber;
	}

	public String getObjType() {
		return objType;
	}

	public String getObjNumber() {
		return objNumber;
	}

	public List<NewSpecialParamTablePanel> getSpecialParamTablePanelList() {
		return specialParamTablePanelList;
	}

	public void setSpecialParamTablePanelList(List<NewSpecialParamTablePanel> specialParamTablePanelList) {
		this.specialParamTablePanelList = specialParamTablePanelList;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public String getLukahao() {
		return lukahao;
	}

	public String getGxPK() {
		return gxPK;
	}

	public String getJianyanyuan() {
		return jianyanyuan;
	}

	public String getCaozuoyuan() {
		return caozuoyuan;
	}

	public String getTechnicsType() {
		return technicsType;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public String getIsZF() {
		return isZF;
	}

	public String getBsoID(){
		return bsoID;
	}

	public String getVersion(){
		return version;
	}


}
