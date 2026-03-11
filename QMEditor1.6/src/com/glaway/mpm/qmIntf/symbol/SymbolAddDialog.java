package com.glaway.mpm.qmIntf.symbol;

import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.visual.log.VaLogger;

public class SymbolAddDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	public String returnStr;
	private VaLogger logger = VaLogger.getLogger(this.getClass());
	private JFrame parentFrame;

	public static void main(String[] args) {
		SwingUtil.setLookAndFeel();
		SymbolAddDialog dialog = new SymbolAddDialog(null);
		dialog.showDialog();
	}

	public SymbolAddDialog(JFrame parentFrame) {
		this.parentFrame = parentFrame;
		logger.debug("SymbolInsertDialog invoke");
		newDialog();
	}

	public void newDialog() {
		dialog = new CommonDialog(parentFrame);
		dialog.setTitle("符号");
		dialog.setSize(350, 270);
		SwingUtil.setMiddle(dialog);
	}

	public String showDialog() {
		Container container = dialog.getContentPane();
		container.add(new SymbolAddPanel(this, dialog));
		dialog.setVisible(true);
		logger.debug("return String===" + returnStr);
		return returnStr;
	}

}