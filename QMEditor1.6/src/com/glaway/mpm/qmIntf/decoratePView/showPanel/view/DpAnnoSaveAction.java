package com.glaway.mpm.qmIntf.decoratePView.showPanel.view;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.glaway.mpm.task.CmTaskExecutor;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.action.VaAction;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.visual.view.pview.VaPViewFactory;

public class DpAnnoSaveAction extends VaAction implements CmTaskExecutor {

	private VaLogger					log					= VaLogger.getLogger();
	private boolean						executorActive;
	private static VaActionProgressBar	animFrame			= null;
	private ImageIcon					imgSave			= new ImageIcon(this.getClass().getResource("/images/save_template.gif"));	// CmUtil.getImageFromServer("productView.gif"));
	private String name;
	
	public DpAnnoSaveAction(String name) {
		setIcon(imgSave);
		setToolTipText("保存当前注释集");
		this.name = name;
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
//		VaPViewFactory.getPViewImpl4DP().saveAnnotation("whytest");
		super.actionPerformed(evt);
	}
	
	@Override
	public boolean isExecutorActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setExecutorActive(boolean executorActive) {
		// TODO Auto-generated method stub
		
	}

}
