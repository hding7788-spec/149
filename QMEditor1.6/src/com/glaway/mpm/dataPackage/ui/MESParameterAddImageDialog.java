package com.glaway.mpm.dataPackage.ui;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;

public class MESParameterAddImageDialog {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private DPMesParameterMainFrame frame;

	private Container obj;
	private int row;
	private int column;
	private Map<String, List<String>> imageMap;
	private MESParameterAddImagePanel panel;

	public MESParameterAddImageDialog(Container obj, int row, int column, Map<String,List<String>> imageMap){
		this.obj = obj;
		this.row = row;
		this.column = column;
		this.imageMap = imageMap;
		newDialog();
	}
	public void newDialog() {
		dialog = new CommonDialog(dialog);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setTitle("添加图片");
		dialog.setSize(500, 400);
		dialog.setResizable(false);
		dialog.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
//			    int a = JOptionPane.showConfirmDialog(null, "确定关闭吗？", "温馨提示",
//			      JOptionPane.YES_NO_OPTION);

//			    if (a == 0) {
			    	panel.getSureButton().doClick();
			    	dialog.dispose();
//			    }
			   }
		});
		SwingUtil.setMiddle(dialog);
	}
	public List<String> showDialog() {
		Container container = dialog.getContentPane();
		panel = new MESParameterAddImagePanel(dialog, obj, row, column, imageMap);
		container.add(panel);
		dialog.setVisible(true);
		return panel.getImagePathList();
	}

//	public static void main(String[] args) {
//		MESParameterAddImageDialog dialog = new MESParameterAddImageDialog(1);
//		dialog.newDialog();
//		dialog.showDialog();
//	}
}
