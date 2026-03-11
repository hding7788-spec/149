package com.glaway.mpm.parameter.designui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


import com.glaway.mpm.view.AdditionalTableJPanel;
import com.glaway.mpm.view.NewEquipJPanel;
import com.glaway.mpm.view.NewMaterialJPanel;
import com.glaway.mpm.view.NewMeasureJPanel;
import com.glaway.mpm.view.NewStandardDashboardJPanel;
import com.glaway.mpm.view.NewToolJPanel;
import com.glaway.mpm.view.NewUnStandardDashboardJPanel;

/**
 * 检验资源面板
 * @author zhuhao 2017.10.20
 *
 */
public class NewCheckResourcesTablePanel extends JPanel {
	private JFrame frame;
	private Container parentPanel;
	private JTabbedPane tabbedPane = new JTabbedPane();
	private NewEquipJPanel equipJPanel;
	private NewStandardDashboardJPanel standardDashboardJPanel = null;
	private NewUnStandardDashboardJPanel unStandardDashboardJPanel = null;
	private NewMeasureJPanel measureJPanel = null;
	private NewToolJPanel toolJPanel;
	private NewMaterialJPanel materialJPanel;
//	private AdditionalTableJPanel additionalTableJPanel = null;

	public NewCheckResourcesTablePanel(Container parentPanel) {
		this.parentPanel = parentPanel;
		this.setLayout(new BorderLayout());
		this.equipJPanel = new NewEquipJPanel(this, frame);
		this.standardDashboardJPanel = new NewStandardDashboardJPanel(this,frame);
		this.unStandardDashboardJPanel = new NewUnStandardDashboardJPanel(this,frame);
		this.measureJPanel = new NewMeasureJPanel(this,frame);
		this.toolJPanel = new NewToolJPanel(this, frame);
		this.materialJPanel = new NewMaterialJPanel(this, frame);
//		this.additionalTableJPanel = new AdditionalTableJPanel(this);


		this.add(this.tabbedPane, "Center");
		this.tabbedPane.addTab("检验设备", this.equipJPanel);
		this.tabbedPane.addTab("检验标准仪器仪表", this.standardDashboardJPanel);
		this.tabbedPane.addTab("检验非标准仪器仪表", this.unStandardDashboardJPanel);
		this.tabbedPane.addTab("检验量具", this.measureJPanel);
		this.tabbedPane.addTab("检验工装", this.toolJPanel);
		this.tabbedPane.addTab("检验工艺辅料", this.materialJPanel);

//		this.tabbedPane.addTab("检验工艺附表", this.additionalTableJPanel);
	}

	private static final long serialVersionUID = 1658646539083060096L;

	public Vector getElements(int i){
		if(i==1){
			return this.equipJPanel.getElements();
		}else if(i==2){
			return this.standardDashboardJPanel.getElements();
		}else if(i==3){
			return this.unStandardDashboardJPanel.getElements();
		}else if(i==4){
			return this.measureJPanel.getElements();
		}else if(i==5){
			return this.toolJPanel.getElements();
		}else if(i==6){
			return this.materialJPanel.getElements();
		}
		return null;
	}

	public void setTableValues(Vector vec, int i){
		if(i==1){
			this.equipJPanel.setTableValues(vec);
		}else if(i==2){
			this.standardDashboardJPanel.setTableValues(vec);
		}else if(i==3){
			this.unStandardDashboardJPanel.setTableValues(vec);
		}else if(i==4){
			this.measureJPanel.setTableValues(vec);
		}else if(i==5){
			this.toolJPanel.setTableValues(vec);
		}else if(i==6){
			this.materialJPanel.setTableValues(vec);
		}

	}

	public List<Integer> getRow(){
		List<Integer> allRow = new ArrayList<Integer>();
		allRow.add(this.equipJPanel.getRow());
		allRow.add(this.standardDashboardJPanel.getRow());
		allRow.add(this.unStandardDashboardJPanel.getRow());
		allRow.add(this.measureJPanel.getRow());
		allRow.add(this.toolJPanel.getRow());
		allRow.add(this.materialJPanel.getRow());
		return allRow;
	}

}
