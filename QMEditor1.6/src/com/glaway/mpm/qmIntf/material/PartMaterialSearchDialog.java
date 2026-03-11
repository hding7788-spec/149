package com.glaway.mpm.qmIntf.material;

import java.awt.Container;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;

public class PartMaterialSearchDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;

	private JFrame parentFrame;
	private JDialog parentDialog;

	public PartMaterialSearchDialog(JFrame parentFrame) {
		this.parentFrame = parentFrame;
		newDialog();
	}

	public PartMaterialSearchDialog(JDialog parentDialog) {
		this.parentDialog = parentDialog;
		newDialog();
	}

	public void newDialog() {
		if (parentFrame != null) {
			dialog = new CommonDialog(parentFrame);
		} else {
			dialog = new CommonDialog(parentDialog);
		}
		dialog.setTitle("搜索零件");
		dialog.setSize(650, 550);
		SwingUtil.setMiddle(dialog);
	}

	public Vector<Map<String, String>> showDialog() {
		Container container = dialog.getContentPane();
		container.add(new PartMaterialSearchPanel(dialog));
		dialog.setVisible(true);
		Vector<Map<String, String>> vector = PartMaterialSearchPanel.vector;
		PartMaterialSearchPanel.vector = null;
		return vector;
	}

}