package com.glaway.mpm.consCheck;

import com.glaway.mpm.parameter.designui.CreateCheckParamTableTypeDialog;
import com.glaway.mpm.resource.Images;
import com.glaway.mpm.util.SwingUtil;

import javax.swing.*;
import java.awt.*;

public class ConsCheckApplyMainDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private String type;
	private CreateCheckParamTableTypeDialog parentDialog;

	public ConsCheckApplyMainDialog(CreateCheckParamTableTypeDialog parentDialog, String type) {
		super(parentDialog, true);
		this.parentDialog = parentDialog;
		this.type = type;
		initDialog();
	}

	public void initDialog() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("配置选择");
		setIconImage(Images.technicsIcon.getImage());
		setResizable(false);
		setSize(500, 700);
		SwingUtil.setMiddle(this);
	}

	public void showDialog() {
		Container container = getContentPane();
		container.add(new ConsCheckApplyMainPanel(parentDialog,this,type));
		setVisible(true);
	}

}
