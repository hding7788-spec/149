package com.glaway.mpm.parameter.designui;

import com.glaway.mpm.model.UploadTechnics;
import com.glaway.mpm.util.*;
import com.glaway.mpm.view.IconButton;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.TechnicsPaceJDialog;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.*;

/**
 * 拍照点检验记录表
 * @author chenjianhui
 *
 */
public class PhotoRecordTablePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	public static  Map<String,String> photoValueMap = new LinkedHashMap<String,String>();
	public static List<String> photoTypeList = new ArrayList<String>();
	public static List<String> photoValueList = new ArrayList<String>();

	static{
		if(photoValueMap.isEmpty()||photoValueList.isEmpty()||photoTypeList.isEmpty()){
			try {
				Map<String,Object> config = TechnicsIntf.getPhotoConfig();
				if(config!=null){
					photoValueMap = (Map<String,String> )config.get("photoValueMap");
					photoTypeList = (List<String> )config.get("photoTypeList");
					photoValueList = (List<String> )config.get("photoValueList");
				}


			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}
	private JFrame parentPanel;
	private JFrame frame;
	private CommonTableModelSaveListener saveListener;

	JPanel panel = new JPanel();

	private DefaultTableModel tableModel = new DefaultTableModel() {
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int column) {
			if(column==4 || column==5 || column==6 || column==7 || column==8|| column==10){
				return true;
			}
			return false;
		}
	};



	private JTable table = new JTable(tableModel);

	private JButton addJButton = new IconButton("/images/button_add.png", "添加");

	private JButton deleteJButton = new IconButton("/images/button_remove.png", "移除");

	private JButton upJButton = new IconButton("/images/button_upmove.png", "上移");

	private JButton downJButton = new IconButton("/images/button_downmove.png", "下移");

	private JButton uploadJButton = new IconButton("/images/resource_update.gif", "上传");

	private JButton openJButton = new IconButton("/images/button_add.gif", "查看");

	public PhotoRecordTablePanel(JFrame parentPanel, JFrame frame) {
		this.parentPanel = parentPanel;
		this.frame = frame;
		jbInit();
		setName("PhotoRecordTablePanel");
	}

	private void jbInit() {
		addJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				add();
			}
		});
		deleteJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				delete();
			}
		});
		upJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				up();
			}
		});
		downJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				down();
			}
		});
		uploadJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				upload();
			}
		});
		openJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				open();
			}
		});
		saveListener = new CommonTableModelSaveListener(this);
		panel.setLayout(new GridBagLayout());
		panel.add(addJButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		panel.add(deleteJButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 0, 5), 0, 0));
		panel.add(upJButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		panel.add(downJButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		panel.add(uploadJButton, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 0, 5), 0, 0));
		panel.add(openJButton, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 0, 5), 0, 0));
		tableModel.addColumn("序号");
		tableModel.addColumn("编号");
		tableModel.addColumn("样张名称");
		tableModel.addColumn("版本");
		tableModel.addColumn("产品图号");
		tableModel.addColumn("拍摄要求");
		tableModel.addColumn("判别准则");
		tableModel.addColumn("分类");
		tableModel.addColumn("拍摄对象分类");
		tableModel.addColumn("localFileName");
		tableModel.addColumn("判定场景名称");

		table.setRowHeight(25);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		table.getColumnModel().getColumn(4).setCellEditor(new PhotoRecordEditor(this));
		table.getColumnModel().getColumn(5).setCellEditor(new PhotoRecordEditor(this));
		table.getColumnModel().getColumn(6).setCellEditor(new PhotoRecordEditor(this));
		JComboBox comboBox = new JComboBox();
		for(String pt :photoTypeList){
			comboBox.addItem(pt);
		}

		JComboBox comboBox2 = new JComboBox();
		comboBox2.addItem("");
		for(String pv : photoValueList){
			comboBox2.addItem(pv);
		}


		table.getColumnModel().getColumn(7).setCellEditor(new PhotoRecordEditor(this,comboBox));
		table.getColumnModel().getColumn(8).setCellEditor(new PhotoRecordEditor(this));
		CommonUIUtil.hiddenCell(table,9);//隐藏localFileName

		table.getColumnModel().getColumn(10).setCellEditor(new PhotoRecordEditor(this,comboBox2));


		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(table.getTableHeader(), BorderLayout.PAGE_START);
		p.add(table, BorderLayout.CENTER);
		tableModel.addTableModelListener(saveListener);

		JScrollPane pane = new JScrollPane(table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		if(parentPanel instanceof TechnicsPaceJDialog){
			setLayout(new GridBagLayout());
			add(pane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
							0, 0, 0, 0), 0, 0));
			add(panel, new GridBagConstraints(1, 0, 1, 1, 0, 1.0,
					GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(
							0, 0, 0, 0), 0, 0));
		}
		if(parentPanel instanceof TechnicsPaceJDialog){
			TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentPanel;
			Element techElement = paceJDialog.getStepElement().getParent().getParent();
			String state = techElement.attributeValue("lifecycle");
			String creator = techElement.attributeValue("creator");
			boolean isApproved = (!"正在工作".equals(state) && !"修改中".equals(state)) ? true : false;
			if (isApproved) {
				setUIEnabled(false);
			} else {
				if(!creator.equals(NewTechnicsPart.currentUser)){
					setUIEnabled(false);
				}else{
					setUIEnabled(true);

				}
			}
		}
	}

	private void add() {
		tableModel.removeTableModelListener(saveListener);
		if (table.getCellEditor() != null)
			table.getCellEditor().stopCellEditing();
		SearchPhotoDialog dialog = new SearchPhotoDialog(PhotoRecordTablePanel.this, parentPanel);
		Vector<Map<String, String>> vector = dialog.showDialog();
		/*if(vector != null){
			addData(vector);
		}
		Vector<Vector> dataVector = tableModel.getDataVector();
		if(dataVector.size()>0){
			saveToXml(dataVector);
		}*/
		tableModel.addTableModelListener(saveListener);
	}

	private void delete() {
		tableModel.removeTableModelListener(saveListener);
		int count[] = table.getSelectedRows();
		if (count.length <= 0) {
			SwingUtil.showMessageDialog("请选中需要移除的照片样张", "提示", 2);
			return;
		} else {
			for (int i = count.length; i > 0; i--) {
				String order = (String) tableModel.getValueAt(table.getSelectedRow(), 0);
				String localNumber = (String) tableModel.getValueAt(table.getSelectedRow(), 1);
				String localFileName = (String) tableModel.getValueAt(table.getSelectedRow(), 9);
				tableModel.removeRow(table.getSelectedRow());
				Element parentEle = null;
				Element techEle = null;
				if(parentPanel instanceof TechnicsPaceJDialog){
					TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentPanel;
					parentEle = paceJDialog.paceElement;
					techEle = paceJDialog.getStepElement().getParent().getParent();

					UploadTechnics technics = ObjectTransfer.technicsElementToUploadTechnics(techEle);
					if (!NewTechnicsPart.editTechnics.contains(technics)) {
						NewTechnicsPart.editTechnics.add(technics);
					}
				}
				Element photoData = XmlUtility.getPhotoData(parentEle);
				Element photoElement = XmlUtility.getPhotoElementByBsOrder(photoData, order);
				photoData.remove(photoElement);

				int rowCount = table.getRowCount();
				for (int j = 0; j < rowCount; j++) {
					table.setValueAt(j+1+"", j,0);
					String number = CommonUtil.objectToString(table.getValueAt(j,1));
					String version = CommonUtil.objectToString(table.getValueAt(j,3));
					Element photoEle = XmlUtility.getPhotoElementByNumberAndVersion(photoData, number, version);
					if(photoEle!=null){
						XmlUtility.setAttributeValue(photoEle, "order", String.valueOf(j + 1));
					}
				}
				setTabTitle();
				((NewTechnicsPart)frame).saveProcess(techEle);


				if(localFileName!=null && !"".equals(localFileName)){
					String technicsCategory = techEle.attributeValue("technicsCategory");
					String technicsName = techEle.attributeValue("technicsName");
					String technicsNumber = techEle.attributeValue("technicsNumber");
					String technicsDirectory = "";
					if ("rework".equals(technicsCategory)) {
						technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
					} else if ("temp".equals(technicsCategory)) {
						technicsDirectory = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
					} else {
						technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
					}
					String timeDir = technicsDirectory + "/photoTemplate/" + localNumber;
					File photoFile = new File(timeDir);
					if(photoFile.exists()){
						FileUtil.deleteFile(photoFile);
					}
				}
			}
		}
		tableModel.addTableModelListener(saveListener);
	}

	private void up() {
		tableModel.removeTableModelListener(saveListener);
		int selectedRow = table.getSelectedRow();
		String order = (String) tableModel.getValueAt(selectedRow,0);
		if(!"1".equals(order)){
			moveTableRow(true);
		}
		Vector<Vector> dataVector = tableModel.getDataVector();
		if(dataVector.size()>0){
			saveToXml(dataVector);
		}
		tableModel.addTableModelListener(saveListener);
	}

	private void down() {
		tableModel.removeTableModelListener(saveListener);
		int selectedRow = table.getSelectedRow();
		int rowCount = table.getRowCount();
		String order = (String) tableModel.getValueAt(selectedRow,0);
		if(!(rowCount+"").equals(order)){
			moveTableRow(false);
		}
		Vector<Vector> dataVector = tableModel.getDataVector();
		if(dataVector.size()>0){
			saveToXml(dataVector);
		}
		tableModel.addTableModelListener(saveListener);
	}

	private void upload() {
		tableModel.removeTableModelListener(saveListener);
		File file = FileChooserTool.getFile("jpg,png", panel);
		if(file==null)
			return;
		String fileName = file.getName();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String name = tableModel.getValueAt(i, 2).toString();
			if (fileName.equals(name.substring(name.lastIndexOf("/") + 1, name.length()))) {
				JOptionPane.showMessageDialog(null, "相同的文件名，添加照片样张失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		addToTableAndXml(file);
		tableModel.addTableModelListener(saveListener);
	}

	private void addToTableAndXml(File file) {
		int rowCount = tableModel.getRowCount();
		Vector<String> rowData = new Vector<String>();
		rowData.add(rowCount+1+"");
		String users = NewTechnicsPart.currentUser;
		if(users==null || "".equals(users)){
			users="ZPYZ";
		}
		rowData.add(users.toUpperCase()+System.currentTimeMillis());
		rowData.add(file.getName());
		rowData.add("space.1");
		rowData.add("");
		rowData.add("");
		rowData.add("");
		rowData.add("");
		rowData.add("");
		rowData.add(file.getName());
		rowData.add("");
		rowData.add("");
		tableModel.addRow(rowData);
		setTabTitle();
		Element parentEle = null;
		Element techEle = null;
		if(parentPanel instanceof TechnicsPaceJDialog){
			TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentPanel;
			parentEle = paceJDialog.paceElement;
			techEle = paceJDialog.getStepElement().getParent().getParent();
		}
		if(parentEle != null){
			Element photoData = XmlUtility.getPhotoData(parentEle);
			Element photoEle = photoData.addElement(XmlUtility.PHOTO_TAG);
			XmlUtility.setAttributeValue(photoEle,"order", String.valueOf(rowData.get(0)));
			XmlUtility.setAttributeValue(photoEle,"photoNumber", String.valueOf(rowData.get(1)));
			XmlUtility.setAttributeValue(photoEle,"photoName", String.valueOf(rowData.get(2)));
			XmlUtility.setAttributeValue(photoEle,"photoVersion", String.valueOf(rowData.get(3)));
			XmlUtility.setAttributeValue(photoEle,"productNumber", String.valueOf(rowData.get(4)));
			XmlUtility.setAttributeValue(photoEle,"psyq", String.valueOf(rowData.get(5)));
			XmlUtility.setAttributeValue(photoEle,"pbzz", String.valueOf(rowData.get(6)));
			XmlUtility.setAttributeValue(photoEle,"photoType", String.valueOf(rowData.get(7)));
			XmlUtility.setAttributeValue(photoEle,"psdxType", String.valueOf(rowData.get(8)));
			XmlUtility.setAttributeValue(photoEle,"localFileName", String.valueOf(rowData.get(9)));
			String PDCJMC = String.valueOf(rowData.get(10));
			XmlUtility.setAttributeValue(photoEle,"PDCJBH", String.valueOf(photoValueMap.get(PDCJMC)));
			XmlUtility.setAttributeValue(photoEle,"PDCJMC",PDCJMC );
		}
		((NewTechnicsPart)frame).saveProcess(techEle);
		copyFile(file,String.valueOf(rowData.get(1)));
	}

	private void copyFile(File file,String number) {
		Element techEle = null;
		if(parentPanel instanceof TechnicsPaceJDialog){
			TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentPanel;
			techEle = paceJDialog.getStepElement().getParent().getParent();
		}
		String technicsCategory = techEle.attributeValue("technicsCategory");
		String technicsName = techEle.attributeValue("technicsName");
		String technicsNumber = techEle.attributeValue("technicsNumber");
		String technicsDirectory = "";
		if ("rework".equals(technicsCategory)) {
			technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
		} else if ("temp".equals(technicsCategory)) {
			technicsDirectory = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
		} else {
			technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
		}
		String timeDir = technicsDirectory + "/photoTemplate/" + number;
		File timeFile = new File(timeDir);
		if (!timeFile.exists())
			timeFile.mkdirs();
		String targetPath = timeDir + "/" + file.getName();
		try {
			AutoGenerateStepsUtil.copyFile(file.getAbsolutePath(), targetPath);
		} catch (IOException e) {
			SwingUtil.showMessageDialog("获取上传照片样张失败，请移除条目重试或联系系统管理员！", "提示", 2);
			e.printStackTrace();
		}
	}

	private void moveTableRow(boolean isUp) {
		int selRow = table.getSelectedRow();
		String selValue = "";
		String newValue = "";
		int newRow = 0;
		if (isUp) {
			//上移一行
			newRow = selRow - 1;
		} else {
			//下移一行
			newRow = selRow + 1;
		}

		//交换顺序号。
		int column = 0;
		selValue = String.valueOf(table.getValueAt(selRow, column));
		newValue = String.valueOf(table.getValueAt(newRow, column));
		table.setValueAt("0", selRow, column);
		table.setValueAt("00", newRow, column);
		table.setValueAt(newValue, selRow, column);
		table.setValueAt(selValue, newRow, column);

		//交换行
		tableModel.moveRow(selRow, selRow, newRow);

		//使表格重新获得焦点
		table.requestFocus();
		table.setRowSelectionInterval(newRow, newRow);
	}

	public void open(){
		int row = table.getSelectedRow();
		if(row==-1){
			SwingUtil.showMessageDialog("请选择要查看的行！", "提示", 2);
		}
		String localFileName = (String) tableModel.getValueAt(row, 9);
		String number = (String) tableModel.getValueAt(row, 1);
		String name = (String) tableModel.getValueAt(row, 2);
		String version = (String) tableModel.getValueAt(row, 3);
		System.out.println("localFileName=" + localFileName);
		if(!"".equals(localFileName)){
			Element techEle = null;
			if(parentPanel instanceof TechnicsPaceJDialog){
				TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentPanel;
				techEle = paceJDialog.getStepElement().getParent().getParent();
			}
			String technicsCategory = techEle.attributeValue("technicsCategory");
			String technicsName = techEle.attributeValue("technicsName");
			String technicsNumber = techEle.attributeValue("technicsNumber");
			String technicsDirectory = "";
			if ("rework".equals(technicsCategory)) {
				technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
			} else if ("temp".equals(technicsCategory)) {
				technicsDirectory = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
			} else {
				technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
			}
			String timeDir = technicsDirectory + File.separator + "photoTemplate" + File.separator + number + File.separator + name;
			System.out.println("timeDir=" + timeDir);
			openFile(timeDir);
		}else{
			try {
				byte[] bytes = TechnicsIntf.getImageBytesByNumberAndVersion(number, version);
				if(bytes!=null){
					String photoName = TechnicsIntf.getImageNameByNumberAndVersion(number, version);
					ByteArrayInputStream is = new ByteArrayInputStream(bytes);
					String filePath = WorkSpaceUtil.getTempRootPath() + UUID.randomUUID();
					File photoFile = new File(filePath);
					if(!photoFile.exists()) {
						photoFile.mkdirs();
					}
					writeInputStreamToFile(is,filePath+File.separator + photoName);
					openFile(filePath+File.separator + photoName);
				}
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}

	public static void writeInputStreamToFile(InputStream is, String destPath) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(new FileOutputStream(destPath));
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = bis.read(b)) != -1) {
				bos.write(b, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void openFile(String filePath) {
		try {
			Runtime.getRuntime().exec("rundll32.exe url.dll,FileProtocolHandler  " + filePath);
		} catch (IOException e) {
			CommonUIUtil.showMessageDialog(null, "打开文件出错，请检查是否安装了打开该类型文件的软件！");
		}
	}

	public void addData(Vector<Map<String, String>> vector) {
		//插入行改造
		int selectedRow = table.getSelectedRow();
		int oldRowCount = tableModel.getRowCount();
		int rowCount = tableModel.getRowCount();
		if(vector != null) {
			Vector<String> rowData = null;
			for (Map<String, String> map : vector) {
				rowData = new Vector<String>();
				rowData.add(rowCount+1+"");
				rowData.add(map.get("number"));
				rowData.add(map.get("name"));
				rowData.add(map.get("version"));
				rowData.add(map.get("productNumber"));
				rowData.add(map.get("psyq"));
				rowData.add(map.get("pbzz"));
				rowData.add(map.get("photoType"));
				rowData.add(map.get("psdxType"));
				rowData.add("");
				rowData.add(map.get("PDCJMC"));
				Vector<Vector> vectors = tableModel.getDataVector();
				boolean isHas = false;
				if(vectors.size()>0){
					for (Vector vector1 : vectors) {
						String number = (String) vector1.get(1);
						String version = (String) vector1.get(3);
						if(number.equals(map.get("number")) && version.equals(map.get("version"))){
							isHas = true;
							break;
						}
					}
				}
				if(isHas)
					continue;

				tableModel.addRow(rowData);
				rowCount++;
			}
			if(selectedRow > -1 && selectedRow != oldRowCount-1) {
				int count = tableModel.getRowCount();
				if(count>oldRowCount){
					tableModel.moveRow(oldRowCount,tableModel.getRowCount()-1,selectedRow+1);
				}
				for (int j = 0; j < count; j++) {
					table.setValueAt(j+1+"", j,0);
				}
			}
		}
		setTabTitle();
	}

	String startType = com.glaway.mpm.EditorConfig.startType;
	public void setTabTitle() {
		if(!"SOP".equals(startType)){
			int i = tableModel.getRowCount();
			if (i > 0) {
				if (parentPanel instanceof TechnicsPaceJDialog) {
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(15, "拍照点检验记录表" + "(" + i + ")");
				}
			} else {
				if (parentPanel instanceof TechnicsPaceJDialog) {
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(15, "拍照点检验记录表");
				}
			}
		}
	}

	public void setUIEnabled(boolean b) {
		table.setEnabled(b);
		addJButton.setEnabled(b);
		deleteJButton.setEnabled(b);
		upJButton.setEnabled(b);
		downJButton.setEnabled(b);
		uploadJButton.setEnabled(b);
		//openJButton.setEnabled(b);
	}

	public void saveToXml(Vector<Vector> vector) {
		Element parentEle = null;
		Element techEle = null;
		if(parentPanel instanceof TechnicsPaceJDialog){
			TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentPanel;
			parentEle = paceJDialog.paceElement;
			techEle = paceJDialog.getStepElement().getParent().getParent();
		}
		if(parentEle != null){
			Element photoData = XmlUtility.getPhotoData(parentEle);
			XmlUtility.removeAllChildElements(photoData);
			for (Vector vec : vector) {
				Element photoEle = photoData.addElement(XmlUtility.PHOTO_TAG);
				XmlUtility.setAttributeValue(photoEle,"order", String.valueOf(vec.get(0)));
				XmlUtility.setAttributeValue(photoEle,"photoNumber", String.valueOf(vec.get(1)));
				XmlUtility.setAttributeValue(photoEle,"photoName", String.valueOf(vec.get(2)));
				XmlUtility.setAttributeValue(photoEle,"photoVersion", String.valueOf(vec.get(3)));
				XmlUtility.setAttributeValue(photoEle,"productNumber", String.valueOf(vec.get(4)));
				XmlUtility.setAttributeValue(photoEle,"psyq", String.valueOf(vec.get(5)));
				XmlUtility.setAttributeValue(photoEle,"pbzz", String.valueOf(vec.get(6)));
				XmlUtility.setAttributeValue(photoEle,"photoType", String.valueOf(vec.get(7)));
				XmlUtility.setAttributeValue(photoEle,"psdxType", String.valueOf(vec.get(8)));
				XmlUtility.setAttributeValue(photoEle,"localFileName", String.valueOf(vec.get(9)));

				String PDCJMC = String.valueOf(vec.get(10));
				XmlUtility.setAttributeValue(photoEle,"PDCJBH", String.valueOf(photoValueMap.get(PDCJMC)));
				XmlUtility.setAttributeValue(photoEle,"PDCJMC",PDCJMC );
			}
		}
		((NewTechnicsPart)frame).saveProcess(techEle);
	}


	public void setUIValues(Vector<Element> photoList) {
		tableModel.removeTableModelListener(saveListener);
		tableModel.setRowCount(0);
		for (Element element : photoList) {
			Vector<Object> vector = new Vector<Object>();
			vector.add(String.valueOf(element.attributeValue("order")));
			vector.add(String.valueOf(element.attributeValue("photoNumber")));
			vector.add(String.valueOf(element.attributeValue("photoName")));
			vector.add(String.valueOf(element.attributeValue("photoVersion")));
			vector.add(String.valueOf(element.attributeValue("productNumber")));
			vector.add(String.valueOf(element.attributeValue("psyq")));
			vector.add(String.valueOf(element.attributeValue("pbzz")));
			vector.add(String.valueOf(element.attributeValue("photoType")));
			vector.add(String.valueOf(element.attributeValue("psdxType")));
			vector.add(String.valueOf(element.attributeValue("localFileName")));
			vector.add(String.valueOf(element.attributeValue("PDCJMC")));
			tableModel.addRow(vector);
		}
		tableModel.addTableModelListener(saveListener);
		setTabTitle();
	}

	public JTable getTable() {
		return table;
	}

	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	public Container getParentPanel() {
		return parentPanel;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setTableModel(DefaultTableModel tableModel) {
		this.tableModel = tableModel;
	}
}