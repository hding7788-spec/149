package com.glaway.mpm.mesParameter.ui;

import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.dom4j.Element;

import com.glaway.mpm.parameter.designui.NewCommonParamTablePanel;
import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.qmIntf.technics.entity.Technics;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;

public class CampPersonSearchDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private MesParameterMainFrame frame;
	private Container parent;
	private int columnNum;
	private int tableColumn;

	public CampPersonSearchDialog(MesParameterMainFrame frame, Container parent, int columnNum, int tableColumn) {
		this.parent = parent;
		this.frame = frame;
		this.columnNum = columnNum;
		this.tableColumn = tableColumn;
		newDialog();
	}
	public void newDialog() {
		dialog = new CommonDialog(frame);
		dialog.setTitle("CAMP人员搜索");
		dialog.setSize(650, 500);
		SwingUtil.setMiddle(dialog);
	}

	public void showDialog() {
		Container container = dialog.getContentPane();
		CampPersonSearchPanel panel = new CampPersonSearchPanel(dialog, parent, columnNum, tableColumn);
		container.add(panel);
		dialog.setVisible(true);
	}

}