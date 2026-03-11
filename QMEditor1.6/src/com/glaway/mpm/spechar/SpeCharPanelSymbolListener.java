package com.glaway.mpm.spechar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import com.glaway.mpm.qmIntf.symbol.SymbolAddDialog;
import com.glaway.speciaword.component.EditorPane;

public class SpeCharPanelSymbolListener implements ActionListener {
	private EditorPane speCharPanel;
	private JFrame parentFrame;
	public SpeCharPanelSymbolListener(EditorPane speCharPanel,JFrame parentFrame){
		this.speCharPanel = speCharPanel;
		this.parentFrame = parentFrame;
	}
	public void actionPerformed(ActionEvent e) {
		try {
			String commonString = null;
			SymbolAddDialog dialog = new SymbolAddDialog(parentFrame);
			commonString = dialog.showDialog();
			if (commonString == null) {
				commonString = "";
			}
			this.speCharPanel.setText(commonString);
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}
}