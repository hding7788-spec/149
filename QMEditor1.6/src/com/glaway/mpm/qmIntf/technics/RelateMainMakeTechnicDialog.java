package com.glaway.mpm.qmIntf.technics;

import java.awt.Container;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.dom4j.Element;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.qmIntf.technics.entity.Technics;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;

public class RelateMainMakeTechnicDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private NewTechnicsPart frame;
	private Technics technics;
	private Element technicElement;
	private String docNumber;
	private String picihao;
	private Map<String,Element> zzTechnicsElement;

	public RelateMainMakeTechnicDialog(NewTechnicsPart frame) {
		this.frame = frame;
		newDialog();
	}
	public RelateMainMakeTechnicDialog(NewTechnicsPart frame,Technics technics,Element technicElement,String docNumber, String picihao, Map<String, Element> zzTechnicsElement) {
		this(frame);
		this.technics = technics;
		this.technicElement = technicElement;
		this.docNumber = docNumber;
		this.picihao = picihao;
		this.zzTechnicsElement = zzTechnicsElement;
	}
	public void newDialog() {
		dialog = new CommonDialog(frame);
		dialog.setTitle("与主制工艺工序关联");
		dialog.setSize(650, 450);
		SwingUtil.setMiddle(dialog);
	}

	public Technics showDialog() {
		Container container = dialog.getContentPane();
		RelateMainMakeTechnicPanel panel = new RelateMainMakeTechnicPanel(dialog, frame, technics,technicElement,docNumber, picihao,zzTechnicsElement);
		container.add(panel);

		dialog.setVisible(true);
		return panel.getBorrowTechnics();
	}

}