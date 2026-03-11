package com.glaway.mpm.mesParameter.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.glaway.mpm.mesParameter.helper.MesParameterProcessor;
import com.glaway.mpm.parameter.designui.NewCommonParamTablePanel;
import com.glaway.mpm.parameter.designui.NewSpecialParamTabbedPanel;
import com.glaway.mpm.parameter.designui.NewSpecialParamTablePanel;

public class RightTechnicInfoPanel extends JPanel{

	/**
	 * 右边工艺信息页面
	 */
	private static final long serialVersionUID = 1L;
	private MesParameterMainFrame frame;
	private JTabbedPane tabbedPane;
	private LeftTechnicTreePanel leftTechnicTreePanel;
	private NewCommonParamTablePanel commonParamTablePanel;
	private NewSpecialParamTabbedPanel specialParamTabbedPanel;

	private Map<String, String> processNumberMap;

	public RightTechnicInfoPanel(MesParameterMainFrame frame, LeftTechnicTreePanel leftTechnicTreePanel){
		this.frame = frame;
		this.leftTechnicTreePanel = leftTechnicTreePanel;
		initCompont();

	}

	private void initCompont(){
		tabbedPane = new JTabbedPane();
		commonParamTablePanel = new NewCommonParamTablePanel(this);
		specialParamTabbedPanel = new NewSpecialParamTabbedPanel(this);
		tabbedPane.addTab("质量记录表", commonParamTablePanel);
		tabbedPane.addTab("特殊记录表", specialParamTabbedPanel);
		tabbedPane.setForegroundAt(0, Color.RED);
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
		this.tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int index = tabbedPane.getSelectedIndex();
				for (int i = 0; i < 2; i++) {
					if (index != i) {
						tabbedPane.setForegroundAt(i, Color.BLACK);
					}
				}
				tabbedPane.setForegroundAt(index, Color.RED);
				/**tab页切换时，更新分组信息*/
				Map<String, String> paramsMap = null;
				processNumberMap = null;
				Component component = tabbedPane.getSelectedComponent();
				if(component instanceof NewCommonParamTablePanel){
					NewCommonParamTablePanel commonTable = (NewCommonParamTablePanel) component;
					paramsMap = new HashMap<String, String>();
					paramsMap.put("tableName", "mes" + commonTable.getParamTableType().getEnName());
					paramsMap.put("technicsNumber", commonTable.getTechnicsNumber());
					paramsMap.put("objNumber", commonTable.getObjNumber());
					paramsMap.put("objType", commonTable.getObjType());
					paramsMap.put("lukahao", commonTable.getLukahao());
					paramsMap.put("isZF", commonTable.getIsZF());
					paramsMap.put("bsoId", commonTable.getbsoID());
					paramsMap.put("version", commonTable.getVersion());
					processNumberMap = MesParameterProcessor.getProcessNumberGroup(paramsMap);
				}else if(component instanceof NewSpecialParamTabbedPanel){
					NewSpecialParamTabbedPanel specialTabbed = (NewSpecialParamTabbedPanel) component;
					Component specialComponent = specialTabbed.getTabbedPane().getSelectedComponent();
					if(specialComponent instanceof NewSpecialParamTablePanel){
						NewSpecialParamTablePanel specialTable = (NewSpecialParamTablePanel) specialComponent;
						paramsMap = new HashMap<String, String>();
						paramsMap.put("tableName", "mes" + specialTable.getParamTableType().getEnName());
						paramsMap.put("technicsNumber", specialTabbed.getTechnicsNumber());
						paramsMap.put("objNumber", specialTabbed.getObjNumber());
						paramsMap.put("objType", specialTabbed.getObjType());
						paramsMap.put("lukahao", specialTabbed.getLukahao());
						paramsMap.put("isZF", specialTabbed.getIsZF());
						paramsMap.put("bsoId", specialTabbed.getBsoID());
						paramsMap.put("version", specialTabbed.getVersion());
						processNumberMap = MesParameterProcessor.getProcessNumberGroup(paramsMap);
					}
				}
				MesParameterMainFrame.getMesParameterMainPanel().getGroupComboBox().removeAllItems();
				MesParameterMainFrame.getMesParameterMainPanel().getGroupComboBox().addItem("");
				if(processNumberMap != null){
					for(Map.Entry<String, String> entry : processNumberMap.entrySet()){
						String key = entry.getKey();
						MesParameterMainFrame.getMesParameterMainPanel().getGroupComboBox().addItem(key);
					}
				}
				MesParameterMainFrame.getMesParameterMainPanel().getGroupComboBox().setSelectedItem("");
			}
		});
	}
	public void initUIValues(){
		commonParamTablePanel.setMesUIValues();
		specialParamTabbedPanel.setMesUIValues();

		tabbedPane.setSelectedIndex(0);
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("tableName", "mes" + commonParamTablePanel.getParamTableType().getEnName());
		paramsMap.put("technicsNumber", commonParamTablePanel.getTechnicsNumber());
		paramsMap.put("objNumber", commonParamTablePanel.getObjNumber());
		paramsMap.put("objType", commonParamTablePanel.getObjType());
		paramsMap.put("lukahao", commonParamTablePanel.getLukahao());
		paramsMap.put("isZF", commonParamTablePanel.getIsZF());
		paramsMap.put("bsoId", commonParamTablePanel.getbsoID());
		paramsMap.put("version", commonParamTablePanel.getVersion());
		processNumberMap = MesParameterProcessor.getProcessNumberGroup(paramsMap);
		MesParameterMainFrame.getMesParameterMainPanel().getGroupComboBox().removeAllItems();
		MesParameterMainFrame.getMesParameterMainPanel().getGroupComboBox().addItem("");
		if(processNumberMap != null){
			for(Map.Entry<String, String> entry : processNumberMap.entrySet()){
				String key = entry.getKey();
				MesParameterMainFrame.getMesParameterMainPanel().getGroupComboBox().addItem(key);
			}
		}
		MesParameterMainFrame.getMesParameterMainPanel().getGroupComboBox().setSelectedItem("");
	}
	public void reloadTableValues(){
		Component component = tabbedPane.getSelectedComponent();
		if(component instanceof NewCommonParamTablePanel){
			commonParamTablePanel.reloadTableValues();
		}else if(component instanceof NewSpecialParamTabbedPanel){
			Component speComponent = specialParamTabbedPanel.getTabbedPane().getSelectedComponent();
			if(speComponent instanceof NewSpecialParamTablePanel){
				NewSpecialParamTablePanel specialTablePanel = (NewSpecialParamTablePanel) speComponent;
				specialTablePanel.reloadTableValues();
			}
		}
	}
	public LeftTechnicTreePanel getLeftTechnicTreePanel() {
		return leftTechnicTreePanel;
	}

	public NewCommonParamTablePanel getCommonParamTablePanel() {
		return commonParamTablePanel;
	}

	public NewSpecialParamTabbedPanel getSpecialParamTabbedPanel() {
		return specialParamTabbedPanel;
	}

	public MesParameterMainFrame getFrame() {
		return frame;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public Map<String, String> getProcessNumberMap() {
		return processNumberMap;
	}

	public void setProcessNumberMap(Map<String, String> processNumberMap) {
		this.processNumberMap = processNumberMap;
	}





}
