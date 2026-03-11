package com.glaway.mpm.spechar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.glaway.mpm.qmIntf.commonString.CsSearchDialog;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.speciaword.component.EditorPane;

public class SpeCharPanelCommonStringListener implements ActionListener {
	private EditorPane speCharPanel;
	private JDialog parentDialog;
	private JFrame parentFrame;
	public SpeCharPanelCommonStringListener(EditorPane speCharPanel,JDialog parentDialog){
		this.speCharPanel = speCharPanel;
		this.parentDialog = parentDialog;
	}
	public SpeCharPanelCommonStringListener(EditorPane speCharPanel,JFrame parentFrame){
		this.speCharPanel = speCharPanel;
		this.parentFrame = parentFrame;
	}

	public void actionPerformed(ActionEvent e) {
		try {
			String terminologyXMLPath = WorkSpaceUtil
					.getPersonalTerminologyDirectory();
			CsSearchDialog dia = null;
			if(parentDialog!=null){
				dia = new CsSearchDialog(terminologyXMLPath,parentDialog);
			}else{
				dia = new CsSearchDialog(terminologyXMLPath,parentFrame);
			}
			String commonString = dia.showDialog();
			if (commonString == null) {
				commonString = "";
			}
			this.speCharPanel.setText(commonString);
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}
}