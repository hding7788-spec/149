package com.glaway.mpm.qmIntf.material;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewMaterialJPanel;
import com.glaway.mpm.visual.log.VaLogger;

public class MaterialSearchDialog extends JPanel {

	private static final long serialVersionUID = 1L;

	private VaLogger logger = VaLogger.getLogger(this.getClass());

	private JDialog dialog;
	private String materialType;
	private String name;
	private String materialBrand;
	private boolean flag;
	private JFrame parentFrame;
	private JDialog parentDialog;
	private NewMaterialJPanel panel;

	public MaterialSearchDialog(String materialType, String name,
			String materialBrand, boolean flag, JFrame parentFrame,
			NewMaterialJPanel panel) {
		this.parentFrame = parentFrame;
		this.materialType = materialType;
		this.name = name;
		this.materialBrand = materialBrand;
		this.flag = flag;
		this.panel = panel;
		newDialog();
	}

	public MaterialSearchDialog(String materialType, String name,
			String materialBrand, boolean flag, JDialog parentDialog,
			NewMaterialJPanel panel) {
		this.parentDialog = parentDialog;
		this.materialType = materialType;
		this.name = name;
		this.materialBrand = materialBrand;
		this.flag = flag;
		this.panel = panel;
		newDialog();
	}

	public void newDialog() {
		if (parentFrame != null) {
			dialog = new CommonDialog(parentFrame);
		} else {
			dialog = new CommonDialog(parentDialog);
		}
		dialog.setTitle("搜索材料");
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setSize(size.width-200, 590);
		SwingUtil.setMiddle(dialog);
	}

	public Vector<Map<String, String>> showDialog() {
		Container container = dialog.getContentPane();
		container.add(new MaterialSearchPanel(materialType, name, materialBrand, flag, dialog, panel));
		dialog.setVisible(true);
		logger.debug("return search materials===================" + MaterialSearchPanel.materials);
		Vector<Map<String, String>> materials = MaterialSearchPanel.materials;
		MaterialSearchPanel.materials = null;
		return materials;
	}
}