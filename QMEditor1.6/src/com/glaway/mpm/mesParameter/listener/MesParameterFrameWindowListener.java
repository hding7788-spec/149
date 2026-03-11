package com.glaway.mpm.mesParameter.listener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.glaway.mpm.mesParameter.helper.MesParameterProcessor;
import com.glaway.mpm.mesParameter.service.ProcessMesParameterToWCIntf;
import com.glaway.mpm.mesParameter.ui.MesParameterMainFrame;
import com.glaway.mpm.parameter.ui.MPMParameterMainFrame;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.FilesUtil;
import com.glaway.mpm.util.WorkSpaceUtil;

public class MesParameterFrameWindowListener extends WindowAdapter {
	private MesParameterMainFrame mesMainFrame;

	public MesParameterFrameWindowListener(MesParameterMainFrame mesMainFrame) {
		this.mesMainFrame = mesMainFrame;
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		int returnMesValue = JOptionPane.showConfirmDialog(mesMainFrame, "确定要退出吗？", "提示", JOptionPane.OK_CANCEL_OPTION);
		if (returnMesValue == JOptionPane.OK_OPTION) {
			String technicNumber = MesParameterMainFrame.getTechnicNumber();
			byte[] bytes = null;
			try {
				bytes = FilesUtil.getMesTechnicsByte(technicNumber);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			//更新工艺文件附件
			if(bytes != null){
				MesParameterProcessor.updataTechnicsPrimary(technicNumber, bytes);
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
