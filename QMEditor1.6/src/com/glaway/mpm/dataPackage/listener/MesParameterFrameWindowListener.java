package com.glaway.mpm.dataPackage.listener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.glaway.mpm.dataPackage.helper.DPMesParameterProcessor;
import com.glaway.mpm.dataPackage.ui.DPMesParameterMainFrame;
import com.glaway.mpm.util.FilesUtil;
import com.glaway.mpm.util.WorkSpaceUtil;

public class MesParameterFrameWindowListener extends WindowAdapter {
	private DPMesParameterMainFrame mesMainFrame;

	public MesParameterFrameWindowListener(DPMesParameterMainFrame mesMainFrame) {
		this.mesMainFrame = mesMainFrame;
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		int returnMesValue = JOptionPane.showConfirmDialog(mesMainFrame, "确定要退出吗？", "提示", JOptionPane.OK_CANCEL_OPTION);
		if (returnMesValue == JOptionPane.OK_OPTION) {
			String technicNumber = DPMesParameterMainFrame.getTechnicsNumber();
			byte[] bytes = null;
			try {
				bytes = FilesUtil.getMesTechnicsByte(technicNumber);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			//更新工艺文件附件
			if(bytes != null){
				DPMesParameterProcessor.updataTechnicsPrimary(technicNumber, bytes);
			}
			//删除临时目录
			WorkSpaceUtil.deleteTechnicsDirectory();
			mesMainFrame.closeWindow();
		} else {
			mesMainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

}
