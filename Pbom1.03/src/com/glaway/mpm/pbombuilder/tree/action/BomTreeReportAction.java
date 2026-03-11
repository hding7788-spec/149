package com.glaway.mpm.pbombuilder.tree.action;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.tree.dialog.BomTreeReportDialog;
import com.glaway.mpm.pbombuilder.util.CmUtil;

public class BomTreeReportAction extends CmAction {
	private static final long serialVersionUID = 1L;
	public static boolean flag=false;

	private ImageIcon expandImage = new ImageIcon(CmUtil.getImageFromServer("report.gif"));

	public BomTreeReportAction() {
		setIcon(expandImage);
		setToolTipText("BOM比较报表");
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		flag=true;
		BomTreeReportDialog dialog = BomTreeReportDialog.getInstance();
		dialog.showDialog();
		CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree().updateUI();
		CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree().updateUI();
		dialog.setVisible(true);
		dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                jDialog1WindowClosing(evt);
            }
        });
	}
	
	private void jDialog1WindowClosing(java.awt.event.WindowEvent evt) {
		flag=false;
		CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree().updateUI();
		CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree().updateUI();
    }
	
	public static  void updateBomReport(){
		if(flag){
			BomTreeReportDialog dialog = BomTreeReportDialog.getInstance();
			dialog.showDialog();
			CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree().updateUI();
			CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree().updateUI();
		}
	}
}
