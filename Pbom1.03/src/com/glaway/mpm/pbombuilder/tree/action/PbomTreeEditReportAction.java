package com.glaway.mpm.pbombuilder.tree.action;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.tree.dialog.PbomTreeEditReportDialog;
import com.glaway.mpm.pbombuilder.util.CmUtil;

public class PbomTreeEditReportAction extends CmAction {
	private static final long serialVersionUID = 1L;
	public static boolean flag=false;

	private ImageIcon expandImage = new ImageIcon(CmUtil.getImageFromServer("pbom_report.gif"));

	public PbomTreeEditReportAction() {
		setIcon(expandImage);
		setToolTipText("PBOM编辑报表");
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		flag=true;
		PbomTreeEditReportDialog dialog=PbomTreeEditReportDialog.getInstance();
		dialog.showDialog();
		dialog.setVisible(true);
		dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                jDialog1WindowClosing(evt);
            }
        });
	}
	
	private void jDialog1WindowClosing(java.awt.event.WindowEvent evt) {
		flag=false;
		CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree().updateUI();
    }
	
	public static void updatePbomTreeEditReport(){
		if(flag){
			PbomTreeEditReportDialog dialog=PbomTreeEditReportDialog.getInstance();
			dialog.showDialog();
		}
	}
}
