package com.glaway.mpm.mesParameter.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import com.glaway.mpm.parameter.commonui.CommonTableModel;
import com.glaway.mpm.parameter.designui.NewCommonParamTablePanel;
import com.glaway.mpm.parameter.designui.NewSpecialParamTablePanel;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.FileChooserTool;
import com.glaway.mpm.view.IconButton;
import com.glaway.mpm.view.TechnicsPaceJDialog;
import com.glaway.mpm.view.TechnicsStepJPanel_XW;

public class MESParameterAddImagePanel extends JPanel{
	private JTable jTable;
	private CommonTableModel tableModel;
	private JButton addImage;
	private JButton removeImage;
	private JButton viewImage;
	private JButton sureButton;

	private JPanel tablePanel;
	private JPanel buttonPanel;
	private JDialog dialog;

	private String[] columnName;
	private Class<?>[] columnClass;
	private int[] editableColumn;

	private Container obj;
	private int row;
	private int column;
	private Map<String, List<String>> imageMap;
	private List<String> imagePathList;

	public MESParameterAddImagePanel(JDialog parent,Container obj, int row, int column, Map<String, List<String>> imageMap){
		this.obj = obj;
		this.row = row;
		this.column = column;
		this.imageMap = imageMap;
		this.dialog = parent;
		initComponent();
		initListener();
		initUIValues();
	}

	public void initComponent(){
		addImage = new IconButton("/images/button_add.png","添加图片");
		removeImage = new IconButton("/images/button_remove.png","移除图片");
		viewImage = new IconButton("/images/button_open.png","查看图片");
		sureButton = new IconButton("/images/button_open.png","确定");

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		buttonPanel.add(addImage, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 10, 5), 0, 0));
		buttonPanel.add(removeImage, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 10, 5), 0, 0));
		buttonPanel.add(viewImage, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 10, 5), 0, 0));
		buttonPanel.add(sureButton, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 10, 5), 0, 0));

		columnClass = new Class[] {String.class, String .class,String.class, String.class};
		columnName = new String[] {"图片名称","图片格式", "图片大小", "图片路径"};
		editableColumn = new int[] {};

		tableModel = new CommonTableModel(columnName, columnClass, editableColumn);
		jTable = new JTable(tableModel);
		jTable.getTableHeader().setReorderingAllowed(false);
		jTable.setRowHeight(30);
		jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jTable.setGridColor(Color.GRAY);

		CommonUIUtil.hiddenCell(jTable, 3);

		tablePanel = new JPanel();
		tablePanel.setLayout(new BorderLayout());
		tablePanel.add(jTable, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane(tablePanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JViewport viewPort = new JViewport();
		viewPort.add(jTable.getTableHeader());
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		scrollPane.setColumnHeader(viewPort);

		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.EAST);

		if(obj instanceof NewCommonParamTablePanel){
			NewCommonParamTablePanel commonParamTablePanel = (com.glaway.mpm.parameter.designui.NewCommonParamTablePanel) obj;
			if(commonParamTablePanel.getParentPanel() instanceof RightTechnicInfoPanel
					&& !commonParamTablePanel.getDoubleClickMesPictureColumns().contains(column)){
				addImage.setEnabled(false);
				removeImage.setEnabled(false);
			}
			if((commonParamTablePanel.getParentPanel() instanceof TechnicsStepJPanel_XW
					|| commonParamTablePanel.getParentPanel() instanceof TechnicsPaceJDialog)
					&& !commonParamTablePanel.getDoubleClickPictureColumns().contains(column)){
				addImage.setEnabled(false);
				removeImage.setEnabled(false);
			}
		}
		if(obj instanceof NewSpecialParamTablePanel){
			NewSpecialParamTablePanel specialParamTablePanel = (com.glaway.mpm.parameter.designui.NewSpecialParamTablePanel) obj;
			if(specialParamTablePanel.getParentPanel() instanceof RightTechnicInfoPanel
					&& !specialParamTablePanel.getDoubleClickMesPictureColumns().contains(column)){
				addImage.setEnabled(false);
				removeImage.setEnabled(false);
			}
			if((specialParamTablePanel.getParentPanel() instanceof TechnicsStepJPanel_XW
					|| specialParamTablePanel.getParentPanel() instanceof TechnicsPaceJDialog)
					&& !specialParamTablePanel.getDoubleClickPictureColumns().contains(column)){
				addImage.setEnabled(false);
				removeImage.setEnabled(false);
			}
		}

	}

	public void initListener(){
		addImage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				chooseButtonActionPerformed(e);
			}
		});
		removeImage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int selectRow =jTable.getSelectedRow();
				if(selectRow == -1){
					return;
				}
				tableModel.removeRow(selectRow);

			}
		});
		viewImage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = jTable.getSelectedRow();
				if(selectedRow == -1){
					JOptionPane.showMessageDialog(null, "请选择要查看的图片！", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}else{
					String filePath = String.valueOf(jTable.getValueAt(selectedRow, 3));
					ViewPictureDialog viewPictureDialog = new ViewPictureDialog(dialog,filePath);
					viewPictureDialog.showImage();
				}

			}
		});
		sureButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				imagePathList = new ArrayList<String>();
				String fileData = "";
				for(int i = 0; i < jTable.getRowCount(); i++){
					String filePath = String.valueOf(jTable.getValueAt(i, 3));
					fileData = filePath + "," + UUID.randomUUID() + ";";
					imagePathList.add(fileData);
				}
				if(obj instanceof NewSpecialParamTablePanel){
					NewSpecialParamTablePanel specialPanel = (NewSpecialParamTablePanel) obj;
					if(imagePathList.size() == 0){
						specialPanel.getTable().setValueAt("", row, column);
					}else{
						specialPanel.getTable().setValueAt("已选图片数量：" + imagePathList.size(), row, column);
					}
				}else if(obj instanceof NewCommonParamTablePanel){
					NewCommonParamTablePanel commonPanel = (NewCommonParamTablePanel) obj;
					if(imagePathList.size() == 0){
						commonPanel.getTable().setValueAt("", row, column);
					}else{
						commonPanel.getTable().setValueAt("已选图片数量：" + imagePathList.size(), row, column);
					}
				}
				dialog.dispose();
			}
		});
	}
	public void initUIValues(){
		if(imageMap != null && imageMap.size() >0){
			List<String> filepathList = imageMap.get(row + "," + column);
			if(filepathList != null && filepathList.size() > 0){
				for(String filepath : filepathList){
					addOneRow();
					filepath = filepath.substring(0, filepath.lastIndexOf(","));
					String fileName = filepath.substring(filepath.lastIndexOf("\\") + 1, filepath.lastIndexOf("."));
					String fileSize = getFileSize(filepath);
					int rowCount = tableModel.getRowCount();
					tableModel.setValueAt(fileName, rowCount - 1, 0);
					tableModel.setValueAt("jpg", rowCount - 1, 1);
					tableModel.setValueAt(fileSize, rowCount - 1, 2);
					tableModel.setValueAt(filepath, rowCount - 1, 3);
				}
			}
		}
	}

	private void chooseButtonActionPerformed(ActionEvent evt) {
		// 选择图片
		String filepath = FileChooserTool.getFilePath("jpg", dialog);
		System.out.println("-------filepath---"+filepath);
		if(filepath == null || "".equals(filepath)) {

		} else {
			addOneRow();
			String fileName = filepath.substring(filepath.lastIndexOf("\\") + 1, filepath.lastIndexOf("."));
			String fileSize = getFileSize(filepath);
			int rowCount = tableModel.getRowCount();
			tableModel.setValueAt(fileName, rowCount - 1, 0);
			tableModel.setValueAt("jpg", rowCount - 1, 1);
			tableModel.setValueAt(fileSize, rowCount - 1, 2);
			tableModel.setValueAt(filepath, rowCount - 1, 3);
		}
	}
	private void addOneRow() {
		Vector vector = new Vector();
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			vector.add("");
		}
		tableModel.addRow(vector);
	}
	/**
	 * 获取文件大小
	 * @param filePath
	 * @return
	 */
	public static String getFileSize(String filePath){
		File file = new File(filePath);
		String resourceSize = "";
		double fileSize = 0;
		try {
			FileInputStream fis = new FileInputStream(file);
			DecimalFormat df = new DecimalFormat("#.##");
			fileSize = (double)((double)fis.available()/1024);
			if(fileSize > 1000){
				resourceSize = df.format(fileSize/1024) + "MB";
			}else{
				resourceSize = df.format(fileSize) + "KB";
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resourceSize;
	}

	public List<String> getImagePathList() {
		return imagePathList;
	}

	public JButton getSureButton() {
		return sureButton;
	}


}