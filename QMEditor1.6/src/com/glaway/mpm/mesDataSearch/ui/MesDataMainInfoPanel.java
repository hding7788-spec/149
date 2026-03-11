package com.glaway.mpm.mesDataSearch.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.glaway.mpm.mesDataSearch.helper.MesDataSearchProcesser;
import com.glaway.mpm.model.TempObject;
import com.glaway.mpm.parameter.designui.NewCommonParamTablePanel;
import com.glaway.mpm.parameter.designui.NewSpecialParamTabbedPanel;

public class MesDataMainInfoPanel extends JPanel{

	/**
	 * 右边工艺信息页面
	 */
	private static final long serialVersionUID = 1L;
	private MesDataSearchMainFrame frame;
	private JTabbedPane tabbedPane;
	private NewCommonParamTablePanel commonParamTablePanel;
	private NewSpecialParamTabbedPanel specialParamTabbedPanel;

	private String productNumber;
	private String technicsNumber;

	public MesDataMainInfoPanel(MesDataSearchMainFrame frame){
		this.frame = frame;
		this.productNumber = frame.getProcessNumber();
		initCompont();
		initUIValues();
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
			}
		});
	}
	public void initUIValues(){

			List<TempObject> technicsNumberList = MesDataSearchProcesser.getTechnicsNumberList("", productNumber, "", "");
			if(technicsNumberList != null && technicsNumberList.size() > 0){
				for (TempObject tempObj : technicsNumberList) {
					String isZF = tempObj.getLifecycle();
					String number = tempObj.getDocNumber();
					if("Y".equals(isZF)){
						String zfFlag = tempObj.getName();
						if("Z".equals(zfFlag)){
							this.technicsNumber = number + "_ZF";
						}
					}else if("N".equals(isZF)){
						this.technicsNumber = number;
					}
				}
			}

		commonParamTablePanel.setDataSearchUIValues();
		specialParamTabbedPanel.setDataSearchUIValues();
	}
//	public void setUIValues(String technicsNumber, String productNumber, String lukahao){
//		this.technicsNumber = technicsNumber;
//		this.productNumber = productNumber;
//		commonParamTablePanel.setDataSearchUIValues();
//		specialParamTabbedPanel.setDataSearchUIValues();
//	}

	public NewCommonParamTablePanel getCommonParamTablePanel() {
		return commonParamTablePanel;
	}

	public NewSpecialParamTabbedPanel getSpecialParamTabbedPanel() {
		return specialParamTabbedPanel;
	}

	public MesDataSearchMainFrame getFrame() {
		return frame;
	}

	public String getTechnicsNumber() {
		return technicsNumber;
	}

	public void setTechnicsNumber(String technicsNumber) {
		this.technicsNumber = technicsNumber;
	}

	public String getProductNumber() {
		return productNumber;
	}





}
