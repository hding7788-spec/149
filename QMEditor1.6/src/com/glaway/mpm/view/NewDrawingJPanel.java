package com.glaway.mpm.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import wt.doc.WTDocument;
import wt.part.WTPart;

import com.glaway.mpm.model.FileTemplate;
import com.glaway.mpm.qmIntf.viewPanel.CreoModelDialog;
import com.glaway.mpm.util.CappJavaUtil;
import com.glaway.mpm.util.FileChooserTool;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.util.MiddleModelUtil;
import com.glaway.mpm.util.ProcedurePictureCreateUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XPathUtil;
import com.glaway.mpm.util.XmlUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TemplateIntf;

public class NewDrawingJPanel extends JPanel {
	private Container parentPanel;
	private static VaLogger logger = VaLogger.getLogger();
	JPanel panel = new JPanel();

	private ViewPopupMenu viwPop = new ViewPopupMenu();

	private DefaultTableModel tableModel = new DefaultTableModel() {
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	private JTable table = new JTable(tableModel);

	private JButton upJButton = new IconButton("/images/button_upmove.png", "上移");
	private JButton downJButton = new IconButton("/images/button_downmove.png", "下移");
	private JButton newJButton = new IconButton("/images/button_new_pic.png", "新建");
	private JButton openJButton = new IconButton("/images/button_open.png", "编辑");
	private JButton addJButton = new IconButton("/images/button_add_local_pic.png", "添加工序简图");


	private JButton addDwgJButton = new IconButton("/images/button_add_local_pic.png", "添加DWG简图");

	private JButton uploadDwgJButton = new IconButton("/images/button_add_local_pic.png", "上载DWG简图");

	private JButton addMiddleModelJButton = new IconButton("/images/button_add_pds.gif", "添加中间模型");

	private JButton refreshMiddleModelJButton = new IconButton("/images/button_add_pds.gif", "更新中间模型");

	// private JButton viewJButton = new JButton("打开");

	private JButton deleteJButton = new IconButton("/images/button_remove.png", "移除");

	private JButton renameJButton = new IconButton("/images/button_open.png", "重命名");

	// private DrawingJLabel drawingLabel = new DrawingJLabel();

	private static final String suffix = "ol,pvz,prt,ed,edz,eda,jpg,gif,png,bmp,dxf,plt,pdf,drw";

	private static String imagePath = "";

	public static final String MIDDLEMODEL_TYPE_NAME = "middleModel";
	// 右边截图
	public static final String SCREENSHOT_TYPE_NAME = "screenshot";

	public static final String SHORTIMAGE_TYPE_NAME = "shortImage";

	private JFrame frame;
	private boolean flag = false;

	public NewDrawingJPanel(Container parentPanel, JFrame frame) {
		NewTechnicsPart.startAnimFrame.setHeaderMessage("加载简图面板");
		this.parentPanel = parentPanel;
		this.frame = frame;
		jbInit();
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成加载简图面板");
	}

	public void setTabTitle() {
		String machiningTechType = LoadConfig.getInstance().getTechnicsType()[1][2];
		Element techElement = this.getTechnicElment();
		String technicsType = XmlUtility.getAttributeValue(techElement, "technicsType");
//		if(machiningTechType.equals(technicsType)){
			addMiddleModelJButton.setVisible(true);
			refreshMiddleModelJButton.setVisible(true);
//		}else{
//			addMiddleModelJButton.setVisible(false);
//			refreshMiddleModelJButton.setVisible(false);
//		}

		int i = tableModel.getRowCount();
		String startType = com.glaway.mpm.EditorConfig.startType;
			if (i > 0) {
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(7, "简图" + "(" + i + ")");
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
						((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(4, "简图" + "(" + i + ")");
					}
				}
			} else {
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					if(!"SOP".equals(startType)){
						((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(7, "简图");
					}
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
						((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(4, "简图");
					}
				}
			}
	}

	private void jbInit() {
		if (parentPanel instanceof TechnicsStepJPanel_XW) {
			flag = true;
		}
		newJButton.setMaximumSize(new Dimension(130, 23));
		newJButton.setMinimumSize(new Dimension(130, 23));
		newJButton.setPreferredSize(new Dimension(130, 23));
		newJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					new NewToolDialog(((TechnicsStepJPanel_XW) parentPanel)
							.getFrame());
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					new NewToolDialog(((TechnicsPaceJDialog) parentPanel)
							.getFrame());
				}
			}
		});

		addJButton.setMaximumSize(new Dimension(130, 23));
		addJButton.setMinimumSize(new Dimension(130, 23));
		addJButton.setPreferredSize(new Dimension(130, 23));
		addJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addProcess();
			}
		});


		addDwgJButton.setMaximumSize(new Dimension(130, 23));
		addDwgJButton.setMinimumSize(new Dimension(130, 23));
		addDwgJButton.setPreferredSize(new Dimension(130, 23));
		addDwgJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addDwgProcess();
			}
		});

		uploadDwgJButton.setMaximumSize(new Dimension(130, 23));
		uploadDwgJButton.setMinimumSize(new Dimension(130, 23));
		uploadDwgJButton.setPreferredSize(new Dimension(130, 23));
		uploadDwgJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					uploadDwgProcess();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(NewDrawingJPanel.this, "上载DWG文件异常！");
					e1.printStackTrace();
				}
			}
		});

		upJButton.setMaximumSize(new Dimension(130, 23));
		upJButton.setMinimumSize(new Dimension(130, 23));
		upJButton.setPreferredSize(new Dimension(130, 23));
		upJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveUpProcess();
			}
		});

		downJButton.setMaximumSize(new Dimension(130, 23));
		downJButton.setMinimumSize(new Dimension(130, 23));
		downJButton.setPreferredSize(new Dimension(130, 23));
		downJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveDownProcess();
			}
		});


		openJButton.setMaximumSize(new Dimension(130, 23));
		openJButton.setMinimumSize(new Dimension(130, 23));
		openJButton.setPreferredSize(new Dimension(130, 23));
		openJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int j = table.getSelectedRowCount();
				if (j == 0) {
					JOptionPane.showMessageDialog(null, "请选择需要打开的附表！", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				openFile();
			}
		});



//		if (flag) {
			addMiddleModelJButton.setMaximumSize(new Dimension(130, 23));
			addMiddleModelJButton.setMinimumSize(new Dimension(130, 23));
			addMiddleModelJButton.setPreferredSize(new Dimension(130, 23));
			addMiddleModelJButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						addMiddleModelProcess();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});

			refreshMiddleModelJButton.setMaximumSize(new Dimension(130, 23));
			refreshMiddleModelJButton.setMinimumSize(new Dimension(130, 23));
			refreshMiddleModelJButton.setPreferredSize(new Dimension(130, 23));
			refreshMiddleModelJButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						refreshMiddleModel2();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
//		}
		deleteJButton.setMaximumSize(new Dimension(130, 23));
		deleteJButton.setMinimumSize(new Dimension(130, 23));
		deleteJButton.setPreferredSize(new Dimension(130, 23));
		deleteJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteProcess();
			}
		});

		renameJButton.setMaximumSize(new Dimension(130, 23));
		renameJButton.setMinimumSize(new Dimension(130, 23));
		renameJButton.setPreferredSize(new Dimension(130, 23));
		renameJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renameProcess();
			}
		});

		panel.setLayout(new GridBagLayout());
//		panel.add(newJButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
//				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
//						5, 5, 0, 5), 0, 0));
		panel.add(addJButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));

			String machiningTechType = LoadConfig.getInstance().getTechnicsType()[1][2];
			Element techElement = this.getTechnicElment();
			String technicsType = XmlUtility.getAttributeValue(techElement, "technicsType");
//			if(machiningTechType.equals(technicsType)){
				addMiddleModelJButton.setVisible(true);
				refreshMiddleModelJButton.setVisible(true);
//			}else{
//				addMiddleModelJButton.setVisible(false);
//				refreshMiddleModelJButton.setVisible(false);
//			}


		if (true) {
			panel.add(addMiddleModelJButton, new GridBagConstraints(1, 2, 1, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(5, 5, 0, 5), 0, 0));
			panel.add(refreshMiddleModelJButton, new GridBagConstraints(1, 3,
					1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(5, 5, 0, 5), 0, 0));
		}
		panel.add(deleteJButton, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		//TODO DWG
		panel.add(addDwgJButton, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));

		panel.add(openJButton, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		panel.add(uploadDwgJButton, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		panel.add(upJButton, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		panel.add(downJButton, new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		panel.add(renameJButton, new GridBagConstraints(1, 10, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));

		setLayout(new GridBagLayout());

		// "简图名称", "简图类型", "简图大小", "路径", "DwgNumber", "模板Number", "type", "bsoID", "imageOid", "parentImageOid", "版本","模型名称"
		tableModel.addColumn("简图名称");
		tableModel.addColumn("简图类型");
		tableModel.addColumn("简图大小");
		tableModel.addColumn("路径");
		tableModel.addColumn("DwgNumber");
		tableModel.addColumn("模板Number");
		tableModel.addColumn("type");
		tableModel.addColumn("bsoID");
		tableModel.addColumn("imageOid");
		tableModel.addColumn("parentImageOid");
		tableModel.addColumn("版本");
		tableModel.addColumn("modelName");
		tableModel.addColumn("fileName");

		setHideColumn(3);
		setHideColumn(4);
		setHideColumn(5);
		setHideColumn(6);
		setHideColumn(7);
		setHideColumn(8);
		setHideColumn(9);
		setHideColumn(10);
		setHideColumn(11);
		setHideColumn(12);

		table.setRowHeight(25);
		table.setSelectionMode(2);
		table.getTableHeader().setReorderingAllowed(false);
		table.setGridColor(Color.GRAY);
		JScrollPane pane = new JScrollPane(table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane.getViewport().setBackground(Color.white);
		add(pane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		add(panel, new GridBagConstraints(1, 0, 1, 1, 0, 1.0,
				GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0));

		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
					try {
						showSelectImage();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				if (e.getClickCount() == 1||e.getClickCount() == 2) {
					int row = table.getSelectedRow();
					boolean isEnbleDWGButton = false;
					if(row!=-1){
						String type = (String)table.getValueAt(row, 1);
						if("dwg".equalsIgnoreCase(type)){
							isEnbleDWGButton = true;
						}
					}
					uploadDwgJButton.setEnabled(isEnbleDWGButton);
					openJButton.setEnabled(isEnbleDWGButton);

				}
			}

			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					int i = table.getSelectedRowCount();
					if (i == 0)
						return;
					viwPop.show(NewDrawingJPanel.this, e.getX(), e.getY());
				}
			}
		});
	}

	private void renameProcess() {
		int rowCount = tableModel.getRowCount();
		if(rowCount < 1){
			JOptionPane.showMessageDialog(null, "未添加简图,不能重命名", "提示",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		int selectedRow = table.getSelectedRow();
		if(selectedRow < 0){
			JOptionPane.showMessageDialog(null, "未选择简图,不能重命名", "提示",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		String fileName = JOptionPane.showInputDialog(parentPanel, "重新命名简图名称");
		if(fileName == null || "".equals(fileName)){
			return;
		}
		String file = tableModel.getValueAt(selectedRow, 3).toString();
		tableModel.setValueAt(fileName, selectedRow, 0);
		writeRenameXml(fileName,file,selectedRow);
	}

	private void writeRenameXml(String fileName, String file, int selectedRow) {
		Element techele = null;
		Element ele = null;
		NewTechnicsPart newTechnicsPart = null;
		if (parentPanel instanceof NewTechnicsPart) {
			newTechnicsPart = (NewTechnicsPart) parentPanel;
			techele = XmlUtility.getTechnicsElement(newTechnicsPart.getCurrentTechnics());
			ele = techele;
		}else if(parentPanel instanceof TechnicsStepJPanel_XW){
			TechnicsStepJPanel_XW technicsStepJPanel = (TechnicsStepJPanel_XW) parentPanel;
			newTechnicsPart = (NewTechnicsPart) technicsStepJPanel.getFrame();
			techele = XmlUtility.getTechnicsElement(newTechnicsPart.getCurrentTechnics());
			ele = ((TechnicsStepJPanel_XW) parentPanel).getStepElement();
		}else if(parentPanel instanceof TechnicsPaceJDialog){
			TechnicsPaceJDialog technicsPaceJDialog = (TechnicsPaceJDialog) parentPanel;
			newTechnicsPart = (NewTechnicsPart) technicsPaceJDialog.getFrame();
			techele = XmlUtility.getTechnicsElement(newTechnicsPart.getCurrentTechnics());
			ele = ((TechnicsPaceJDialog) parentPanel).getPaceElement();
		}
		if(techele==null) return;
		String technicsNumber = techele.attributeValue("technicsNumber");
		Element images = XmlUtility.getImages(ele);
		List<Element> additionTable = images.elements("PDrawingInfo");
		for(Element element : additionTable){
			String path = element.attributeValue("absolutePath");
			if(file.equals(path)){
				element.setAttributeValue("drawingName", fileName);
				XmlUtility.setAttributeValue(element, "drawingName",
						(String) tableModel.getValueAt(selectedRow, 0));
				break;
			}
		}
		newTechnicsPart.saveProcess(techele);
	}

	public Vector<Element> getElements() {
		Vector<Element> elements = new Vector<Element>();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if (isRowNull(i))
				continue;
			Element element = XmlUtility.createImage();
			// "简图名称", "简图类型", "简图大小", "路径", "DwgNumber", "模板Number", "type", "bsoID", "imageOid", "parentImageOid", "版本"
			XmlUtility.setAttributeValue(element, "bsoID", "");
			XmlUtility.setAttributeValue(element, "drawingName", (String) tableModel.getValueAt(i, 0));
			XmlUtility.setAttributeValue(element, "drawingType", (String) tableModel.getValueAt(i, 1));
			XmlUtility.setAttributeValue(element, "drawingSize", (String) tableModel.getValueAt(i, 2));
			XmlUtility.setAttributeValue(element, "absolutePath", (String) tableModel.getValueAt(i, 3));
			XmlUtility.setAttributeValue(element, "docNumber", (String) tableModel.getValueAt(i, 4));
			XmlUtility.setAttributeValue(element, "tempDocNumber", (String) tableModel.getValueAt(i, 5));
			XmlUtility.setAttributeValue(element, "type", (String) tableModel.getValueAt(i, 6));
			XmlUtility.setAttributeValue(element, "bsoID", (String) tableModel.getValueAt(i, 7));
			XmlUtility.setAttributeValue(element, "imageOid", (String) tableModel.getValueAt(i, 8));
			XmlUtility.setAttributeValue(element, "parentImageOid", (String) tableModel.getValueAt(i, 9));
			XmlUtility.setAttributeValue(element, "version", (String) tableModel.getValueAt(i, 10));
			XmlUtility.setAttributeValue(element, "modelName", (String) tableModel.getValueAt(i, 11));
			XmlUtility.setAttributeValue(element, "fileName", (String) tableModel.getValueAt(i, 12));
			elements.add(element);
		}
		return elements;
	}

	public void setTableValues(Vector<Element> vec) {
		clearTable();
		for (int i = 0; i < vec.size(); i++) {
			Vector vector = new Vector();
			for (int j = 0; j < table.getColumnCount(); j++) {
				vector.add("");
			}
			tableModel.addRow(vector);
			Element element = vec.get(i);
			// "简图名称", "简图类型", "简图大小", "路径", "DwgNumber", "模板Number", "type", "bsoID", "imageOid", "parentImageOid", "版本"
			String drawingName = element.attributeValue("drawingName");
			String drawingType = element.attributeValue("drawingType");
			tableModel.setValueAt(drawingName, i, 0);
			tableModel.setValueAt(drawingType, i, 1);
			tableModel.setValueAt(element.attributeValue("drawingSize"), i, 2);
			tableModel.setValueAt(element.attributeValue("absolutePath"), i, 3);
			tableModel.setValueAt(element.attributeValue("docNumber"), i, 4);
			tableModel.setValueAt(element.attributeValue("tempDocNumber"), i, 5);
			tableModel.setValueAt(element.attributeValue("type"), i, 6);
			tableModel.setValueAt(element.attributeValue("bsoID"), i, 7);
			tableModel.setValueAt(element.attributeValue("imageOid"), i, 8);
			tableModel.setValueAt(element.attributeValue("parentImageOid"), i, 9);
			tableModel.setValueAt(element.attributeValue("version"), i, 10);
			tableModel.setValueAt(element.attributeValue("modelName"), i, 11);
			tableModel.setValueAt(element.attributeValue("fileName"), i, 12);
		}
	}

	private void setHideColumn(int index) {
		table.getColumnModel().getColumn(index).setMinWidth(0);
		table.getColumnModel().getColumn(index).setMaxWidth(0);
	}

	//TODO 上传DWG
	private void uploadDwgProcess() throws IOException{
		int row = table.getSelectedRow();
		if(row==-1){
			JOptionPane.showMessageDialog(this	, "请选择DWG文件行在上载！");
			return;
		}
		String ftype = (String) table.getValueAt(row, 1);
		if(!"dwg".equals(ftype.toLowerCase())){
			JOptionPane.showMessageDialog(this	, "请选择DWG文件行在上载！");
			return;
		}
		InputStream is = null;
		try{

		String dwgNumber = (String)table.getValueAt(row, 4);
		String dwgName = (String)table.getValueAt(row, 0);
		String fileName = (String)table.getValueAt(row, 12);
		Element techele = this.getTechnicElment();
		String technicsNumber = techele.attributeValue("technicsNumber");
		String path = (String)table.getValueAt(row, 3);
		String technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
		File dwgFile = new File(technicsDirectory+File.separator+path);

		byte[] bytes = null;
		is = new FileInputStream(dwgFile);
		bytes = IOUtils.toByteArray(is);
		String partNumber = XmlUtility.getAttributeValue(techele, "partNumber");
		WTPart part = TemplateIntf.getLatestParttByNumber(partNumber);
		WTDocument doc = TemplateIntf.uploadDwgDoc(part.getContainer(), dwgNumber, dwgName, bytes, fileName);
		String docNumber = doc.getNumber();
		table.setValueAt(docNumber, row, 4);
		}finally{
			if(is!=null){
				is.close();
			}
		}
		JOptionPane.showMessageDialog(NewDrawingJPanel.this, "成功上载DWG文件！正在进行DWG转PDF，请先执行PDF工艺预览，确保PDF中的附图已转换成功，再执行工艺上载。");
	}

	private void addDwgProcess() {
//		File file = FileChooserTool.getFile("dwg", imagePath, parentPanel);
//		if (file == null)
//			return;
//		imagePath = file.getAbsolutePath();
		FileTemplate fileTemplate = null;
		try {
			fileTemplate = this.getTemplate();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(fileTemplate==null)return;
		File file = fileTemplate.getFile();
		imagePath = file.getAbsolutePath();
		String fileName = file.getName();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String name = tableModel.getValueAt(i, 3).toString();
			if (fileName.equals(name.substring(name.lastIndexOf("/") + 1, name.length()))) {
				JOptionPane.showMessageDialog(null, "相同的文件名，添加简图失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		String timeFolder = new SimpleDateFormat("yyyyMMddhhmmssSSS").format(new Date());
		String path = timeFolder + "/" + fileName;
		addDwgToTable(file, path, fileTemplate.getDocNumber(), fileTemplate.getTempId(), fileTemplate);
		setTabTitle();
		saveDrawing(null);
		try {
			writePicture(imagePath, path, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// saveDrawing();
	}

	private void addProcess() {
		File file = FileChooserTool.getFile(suffix, imagePath, parentPanel);
		if (file == null)
			return;
		imagePath = file.getAbsolutePath();
		String fileName = file.getName();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String name = tableModel.getValueAt(i, 3).toString();
			if (fileName.equals(name.substring(name.lastIndexOf("/") + 1, name.length()))) {
				JOptionPane.showMessageDialog(null, "相同的文件名，添加简图失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}

		String endFix = fileName.substring(fileName.indexOf("."), fileName.length());
		String fn = UUID.randomUUID()+"";
		fn = fn.replaceAll("-", "");
		String fn2 = fn + endFix;
		String timeFolder = new SimpleDateFormat("yyyyMMddhhmmssSSS").format(new Date());
		String path =timeFolder +"/"+ fn2;



//		String timeFolder = new SimpleDateFormat("yyyyMMddhhmmssSSS").format(new Date());
//		String path = timeFolder + "/" + fileName;
		addToTable("", file, path, SHORTIMAGE_TYPE_NAME, "", false, timeFolder,
				null, null, null);
		setTabTitle();
		saveDrawing(null);
		try {
			writePicture2(imagePath, path, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// saveDrawing();
	}

	private void addDwgToTable(File file, String path,String docNumber,String tempdocNumber, FileTemplate fileTemplate) {
		String absolutePath = file.getAbsolutePath();
		String fileName = file.getName();
		String name, type;
		if (fileName.contains(".")) {
			name = fileName.split("\\.")[0];
			type = file.getName().split("\\.")[1];
		} else {
			name = fileName;
			type = "";
		}
		float size = 0;
		java.io.FileInputStream in = null;
		try {
			in = new FileInputStream(absolutePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			size = in.available();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		addOneRow();
		int rowCount = table.getRowCount();
		// "简图名称", "简图类型", "简图大小", "路径", "DwgNumber", "模板Number", "type", "bsoID", "imageOid", "parentImageOid", "版本"
		tableModel.setValueAt(fileTemplate.getDisplayName(), rowCount - 1, 0);
		tableModel.setValueAt(type, rowCount - 1, 1);
		tableModel.setValueAt(FileUtil.getFileSize(size), rowCount - 1, 2);
		tableModel.setValueAt(path, rowCount - 1, 3);
		tableModel.setValueAt(docNumber, rowCount - 1, 4);
		tableModel.setValueAt(tempdocNumber, rowCount - 1, 5);

		tableModel.setValueAt("", rowCount - 1, 6);
		tableModel.setValueAt("", rowCount - 1, 7);
		tableModel.setValueAt("", rowCount - 1, 8);
		tableModel.setValueAt("", rowCount - 1, 9);
		tableModel.setValueAt("", rowCount - 1, 10);
		tableModel.setValueAt("", rowCount - 1, 11);
		tableModel.setValueAt(name, rowCount - 1, 12);
	}

	private void addToTable(String modelName, File file, String path,
			String imageType, String oid, boolean isScreenShot,
			String imageOid, String parentImageOid, String imageName,
			String version) {
		String absolutePath = file.getAbsolutePath();
		String fileName = file.getName();
		String name, type;
		if (fileName.indexOf(".") != -1) {
			int index = fileName.lastIndexOf(".");
			name = fileName.substring(0, index);
			type = fileName.substring(index + 1);
			if ((name.endsWith(".prt") || name.endsWith(".drw"))
					&& name.length() > 4) {
				String name1 = name.substring(0, name.length() - 4);
				type = name.substring(name.length() - 3) + "." + type;
				name = name1;
			}
		} else {
			name = fileName;
			type = "";
		}
		float size = 0;
		java.io.FileInputStream in = null;
		try {
			in = new FileInputStream(absolutePath);
			size = in.available();
			in.close();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			CappJavaUtil.closeStream(in);
		}
		int row;
		if (isScreenShot) {

		} else {

		}
		addOneRow();
		row = table.getRowCount() - 1;
		if (SCREENSHOT_TYPE_NAME.equals(imageType)) {
			name = imageName;
		}
		// "简图名称", "简图类型", "简图大小", "路径", "DwgNumber", "模板Number", "type", "bsoID", "imageOid", "parentImageOid", "版本"
		tableModel.setValueAt(name, row, 0);
		tableModel.setValueAt(type, row, 1);
		tableModel.setValueAt(FileUtil.getFileSize(size), row, 2);
		tableModel.setValueAt(path, row, 3);

		tableModel.setValueAt("", row, 4);
		tableModel.setValueAt("", row, 5);

		tableModel.setValueAt(imageType, row, 6);
		tableModel.setValueAt(oid, row, 7);
		tableModel.setValueAt(imageOid, row, 8);
		if (parentImageOid != null) {
			tableModel.setValueAt(parentImageOid, row, 9);
		}
		tableModel.setValueAt(version, row, 10);
		tableModel.setValueAt(modelName, row, 11);
	}

	private void addToTable(File file, String path) {
		String absolutePath = file.getAbsolutePath();
		String fileName = file.getName();
		String name, type;
		if (fileName.contains(".")) {
			name = fileName.split("\\.")[0];
			type = file.getName().split("\\.")[1];
		} else {
			name = fileName;
			type = "";
		}
		float size = 0;
		FileInputStream in = null;
		try {
			in = new FileInputStream(absolutePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			size = in.available();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		addOneRow();
		int rowCount = table.getRowCount();
		tableModel.setValueAt(name, rowCount - 1, 0);
		tableModel.setValueAt(type, rowCount - 1, 1);
		tableModel.setValueAt(FileUtil.getFileSize(size), rowCount - 1, 2);
		tableModel.setValueAt(path, rowCount - 1, 3);
//		tableModel.setValueAt("", rowCount - 1, 4);
//		tableModel.setValueAt("", rowCount - 1, 5);
	}

	private void addOneRow() {
		Vector vector = new Vector();
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			vector.add("");
		}
		tableModel.addRow(vector);
	}

	private void viewProcess() {
		int[] rows = table.getSelectedRows();
		if (rows == null || rows.length == 0)
			return;
		for (int i = 0; i < rows.length; i++) {
			String path = (String) table.getValueAt(rows[i], 3);
			try {
				WorkSpaceUtil.execOpen(path);
			} catch (Exception e) {

				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}
	}

	private void deleteProcess() {
		int[] rows = table.getSelectedRows();
		if (rows.length == 0) {
			SwingUtil.showMessageDialog("请选择需要删除的数据", "提示", 2);
			return;
		}

		int result = SwingUtil.showConfirmDialog("是否确认删除该数据，删除后将无法恢复？", "提示", JOptionPane.YES_NO_OPTION);
		if (!(result == JOptionPane.OK_OPTION)) {
			return;
		}

		String technicsPath = null;
		try {
			Element techele = null;
			if (parentPanel instanceof TechnicsStepJPanel_XW) {
				techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) ((TechnicsStepJPanel_XW) parentPanel).getFrame()).getCurrentTechnics());
			} else if (parentPanel instanceof TechnicsPaceJDialog) {
				techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) ((TechnicsPaceJDialog) parentPanel).getFrame()).getCurrentTechnics());
			}
			String technicsCategory = techele.attributeValue("technicsCategory");
			String technicsNumber = techele.attributeValue("technicsNumber");
			String technicsName = techele.attributeValue("technicsName");
			if ("rework".equals(technicsCategory)) {
				technicsPath = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
			} else if ("temp".equals(technicsCategory)) {
				technicsPath = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
			} else {
				technicsPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		// "简图名称", "简图类型", "简图大小", "路径", "DwgNumber", "模板Number", "type", "bsoID", "imageOid", "parentImageOid", "版本","模型名称"
		// "模型名称", "简图名称", "简图类型", "简图大小", "路径", "type", "bsoID", "imageOid", "parentImageOid", "版本"
		for (int i = 0; i < rows.length; i++) {
			String path = tableModel.getValueAt(rows[i], 3).toString();
			String parentOid = null;
			File file;
			Object type = tableModel.getValueAt(rows[i], 6);
			if ("screenshot".equals(type)) {
				file = new File(technicsPath + File.separator + path);
			} else {
				file = new File(technicsPath + File.separator + path).getParentFile();
				if (MIDDLEMODEL_TYPE_NAME.equals(type)) {
					parentOid = tableModel.getValueAt(rows[i], 7).toString();
				} else {
					Object obj = tableModel.getValueAt(rows[i], 8);
					if(obj == null) {
						parentOid = null;
					} else {
						parentOid = obj.toString();
					}

				}
			}
			if (file.exists()) {
				try {
					WorkSpaceUtil.delete(file);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
				file.delete();
			}
			tableModel.removeRow(rows[i]);
			for (int j = i + 1; j < rows.length; j++) {
				rows[j] = rows[j] - 1;
			}

			if (parentOid != null) {
				List<Integer> deleteRows = new ArrayList<Integer>();
				for (int m = tableModel.getRowCount() - 1; m >= 0; m--) {
					if (parentOid != null && !"".equals(parentOid) && parentOid.equals(tableModel.getValueAt(m, 9).toString())) {
						deleteRows.add(m);
					}
				}

				for (int temp : deleteRows) {
					tableModel.removeRow(temp);
					for (int j = i + 1; j < rows.length; j++) {
						rows[j] = rows[j] - 1;
					}
				}
			}

		}
		setTabTitle();
		saveDrawing(null);
	}

	private boolean isRowNull(int row) {
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			if (!tableModel.getValueAt(row, i).equals(""))
				return false;
		}
		return true;
	}

	public void clearTable() {
		tableModel.setRowCount(0);
		// drawingLabel.setIcon(null);
	}

	class DrawingJLabel extends JLabel {
		public DrawingJLabel() {
			setMaximumSize(new Dimension(130, 130));
			setMinimumSize(new Dimension(130, 130));
			setPreferredSize(new Dimension(130, 130));
			setBorder(BorderFactory.createEtchedBorder());

			addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					if (table.getSelectedRow() != -1)
						setCursor(new Cursor(Cursor.HAND_CURSOR));
				}

				public void mouseExited(MouseEvent e) {
					setCursor(Cursor.getDefaultCursor());
				}

				public void mousePressed(MouseEvent e) {
					viewProcess();
				}
			});
		}
	}

	class NewToolDialog extends JDialog {
		private JRadioButton cadButton = new JRadioButton("CAD");
		private JRadioButton proeButton = new JRadioButton("ProE");
		private JRadioButton _3DButton = new JRadioButton("3D");

		private JButton okButton = new JButton("确定");
		private JButton cancelButton = new JButton("取消");

		public NewToolDialog(JFrame f) {
			super(f, true);
			// setModal(true);
			setTitle("创建简图");
			Container container = this.getContentPane();
			container.setLayout(new GridBagLayout());

			ButtonGroup bg = new ButtonGroup();
			bg.add(cadButton);
			bg.add(proeButton);
			bg.add(_3DButton);

			JPanel panel1 = new JPanel();
			container.add(panel1, new GridBagConstraints(0, 0, 1, 1, 0, 0,
					GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(10, 0, 0, 0), 0, 0));
			panel1.setLayout(new GridBagLayout());
			panel1.add(cadButton, new GridBagConstraints(0, 0, 1, 1, 0, 0,
					GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(0, 10, 0, 5), 0, 0));
			panel1.add(proeButton, new GridBagConstraints(1, 0, 1, 1, 0, 0,
					GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(0, 5, 0, 5), 0, 0));
			panel1.add(_3DButton, new GridBagConstraints(2, 0, 1, 1, 0, 0,
					GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(0, 5, 0, 10), 0, 0));

			JPanel panel2 = new JPanel();
			container.add(panel2, new GridBagConstraints(0, 1, 1, 1, 1.0, 0,
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(10, 0, 10, 0), 0, 0));
			panel2.setLayout(new GridBagLayout());
			panel2.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 10, 0, 5), 0, 0));
			panel2.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0, 0,
					GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(0, 5, 0, 5), 0, 0));
			panel2.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0, 0,
					GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(0, 5, 0, 10), 0, 0));

			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int selected = 0;
					if (cadButton.isSelected())
						selected = 0;
					else if (proeButton.isSelected())
						selected = 1;
					else if (_3DButton.isSelected())
						selected = 2;
					NewTechnicsPart frame = null;
					if (parentPanel instanceof TechnicsStepJPanel_XW) {
						frame = (NewTechnicsPart) ((TechnicsStepJPanel_XW) parentPanel)
								.getFrame();
					} else if (parentPanel instanceof TechnicsPaceJDialog) {
						frame = (NewTechnicsPart) ((TechnicsPaceJDialog) parentPanel)
								.getFrame();
					}
					if (selected == 0) {
						try {
							frame.runDiagramProgram();
						} catch (Exception e1) {

							e1.printStackTrace();
							JOptionPane.showMessageDialog(frame,
									"启动工艺简图工具出现错误！", "提示",
									JOptionPane.INFORMATION_MESSAGE);
						}
					} else if (selected == 1) {
						try {
							frame.runMiddleModuleProgram();
						} catch (Exception e1) {

							e1.printStackTrace();
							JOptionPane.showMessageDialog(frame,
									"启动中间模型工具出现错误！", "提示",
									JOptionPane.INFORMATION_MESSAGE);
						}
					} else if (selected == 2) {
						try {
							frame.runAssembleCartoonProgram();
						} catch (Exception e1) {

							e1.printStackTrace();
							JOptionPane.showMessageDialog(frame,
									"启动装配动画工具出现错误！", "提示",
									JOptionPane.INFORMATION_MESSAGE);
						}
					}
					dispose();
				}
			});

			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});

			Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
			setBounds((int) (dimension.getWidth() - 300) / 2,
					(int) (dimension.getHeight() - 120) / 2, 300, 120);
			setResizable(false);
			setVisible(true);
		}
	}

	private void refreshMiddleModel2() {
		int rowCount = table.getRowCount();
		if (rowCount > 0) {
			for (int i = 0; i < rowCount; i++) {
				refreshSingle(i);
			}
		}
	}

	private void refreshSingle(int i) {
		if (i != -1) {
			NewTechnicsPart frame = null;
			if (parentPanel instanceof TechnicsStepJPanel_XW) {
				frame = (NewTechnicsPart) ((TechnicsStepJPanel_XW) parentPanel).getFrame();
			} else if (parentPanel instanceof TechnicsPaceJDialog) {
				frame = (NewTechnicsPart) ((TechnicsPaceJDialog) parentPanel).getFrame();
			}

			Document document = frame.getCurrentTechnics();
			Element techElement = XmlUtility.getTechnicsElement(document);
			String techPath = WorkSpaceUtil.getTechnicsDirectoryByCategory(techElement);
			String type = CappJavaUtil.convertNull(table.getValueAt(i, 6));
			if (MIDDLEMODEL_TYPE_NAME.equals(type)) {
				String oid = CappJavaUtil.convertNull(table.getValueAt(i, 7));
				String absolutePath = CappJavaUtil.convertNull(table.getValueAt(i, 3));
				File file = new File(techPath + File.separator + absolutePath);
				if (file.exists()) {
					List<String> list = MiddleModelUtil.refresh(oid, file.getParent() + File.separator);
					// "简图名称", "简图类型", "简图大小", "路径", "DwgNumber", "模板Number", "type", "bsoID", "imageOid", "parentImageOid", "版本", "模型名称"
					//"模型名称", "简图名称", "简图类型", "简图大小", "路径", "type", "bsoID", "imageOid", "parentImageOid", "版本"
					if (list != null && list.size() != 0) {
						table.setValueAt(list.get(0), i, 7);
						table.setValueAt(list.get(1), i, 10);
						table.setValueAt(list.get(2), i, 11);
					}
				}
			}
		}
	}

	/**
	 * 刷新中间模型
	 */
	public void refreshMiddleModel() {
		if (parentPanel instanceof TechnicsStepJPanel_XW) {
			Element step = ((TechnicsStepJPanel_XW) parentPanel).getElement();
			if (step == null)
				return;
			Document doc = step.getDocument();
			if (doc != null) {
				Element ele = XmlUtility.getTechnicsElement(doc);
				String oid = ele.attributeValue("partOid");
				String partNumber = ele.attributeValue("partNumber");
				String technicsCategory = ele.attributeValue("technicsCategory");
				String technicsNumber = ele.attributeValue("technicsNumber");
				String technicsName = ele.attributeValue("technicsName");

				String filePath = "";
				String technicsPath = "";
				if ("rework".equals(technicsCategory)) {
					technicsPath = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
					filePath = WorkSpaceUtil.getReWorkTechnicsPathByTechnicsName(technicsNumber, technicsName);
				} else if ("temp".equals(technicsCategory)) {
					technicsPath = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
					filePath = WorkSpaceUtil.getTempTechnicsPathByTechnicsName(technicsNumber, technicsName);
				} else {
					technicsPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
					filePath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
				}

				String xpath = "/technics/QMFawTechnicsInfo/steps/QMProcedureInfo";
				Document document = XmlUtil.getDocument(filePath);
				List<Element> elements = XPathUtil.getElements(xpath, document);
				List<String> stepNumbers = new ArrayList<String>();
				if (elements != null && elements.size() != 0) {
					for (Element element : elements) {
						stepNumbers.add(element.attributeValue("stepNumber"));
					}
				}
				File file = new File(NewTechnicsPart.MIDDLE_MODE_PATH);
				File[] files = file.listFiles();
				Map<String, List<String>> middleModelMap = new HashMap<String, List<String>>();
				Map<String, String> fileWritePath = new HashMap<String, String>();
				if (files != null && files.length != 0) {
					flag: for (File temp : files) {
						for (String stepNumber : stepNumbers) {
							String fileName = temp.getName();
							String partName = partNumber + "-pc-" + stepNumber;
							if (fileName.equalsIgnoreCase(partName + ".ol")
									|| fileName.equalsIgnoreCase(partName+ ".pvs")) {
								String writepath = fileWritePath.get(stepNumber);
								if (writepath == null) {
									String newDir = new SimpleDateFormat("yyyyMMddhhmmssSSS").format(new Date());
									writepath = newDir;
									fileWritePath.put(stepNumber, newDir);
								}
								FileUtil.writeMiddleModel(writepath, technicsPath, temp);
								if (fileName.endsWith(".ol")) {
									List<String> list = FileUtil.writeImage(temp, writepath + File.separator + temp.getName());
									middleModelMap.put(stepNumber, list);
								}
								continue flag;
							}
						}
					}
				}
				System.out.println("middleModelMap= " + middleModelMap);

				Set<Entry<String, List<String>>> set = middleModelMap.entrySet();
				for (Entry<String, List<String>> entry : set) {
					String stepNumber = entry.getKey();
					List<String> value = entry.getValue();
					String prefix = "/technics/QMFawTechnicsInfo/steps/QMProcedureInfo[@stepNumber='";
					String middlefix = "']/images";
					String drawingfix = "/PDrawingInfo[@drawingName='";
					String suffix = "']";
					String drawingInfoxpath = prefix + stepNumber + middlefix + drawingfix + value.get(0) + suffix;
					System.out.println(drawingInfoxpath);
					List<Element> imageElements = XPathUtil.getElements(drawingInfoxpath, document);
					if (imageElements != null && imageElements.size() != 0) {
						for (Element element : imageElements) {
							element.setAttributeValue("drawingType", value.get(1));
							element.setAttributeValue("drawingSize", value.get(2));
							element.setAttributeValue("absolutePath", value.get(3));
						}
					} else {
						String imagepath = prefix + stepNumber + middlefix;
						System.out.println(imagepath);
						imageElements = XPathUtil.getElements(imagepath, document);
						if (imageElements != null && imageElements.size() != 0) {
							Element drawingElement = imageElements.get(0).addElement("PDrawingInfo");
							drawingElement.setAttributeValue("drawingName", value.get(0));
							drawingElement.setAttributeValue("drawingType", value.get(1));
							drawingElement.setAttributeValue("drawingSize", value.get(2));
							drawingElement.setAttributeValue("absolutePath", value.get(3));
						}
					}
				}
				XmlUtil.writeDocument(document, filePath);
				((TechnicsStepJPanel_XW) parentPanel).saveRefreshDrawing(document);
				// 从此将 中间件的信息获取出来，然后传到 saveDrawing（）方法保存
			}
		}

	}

	public void addMiddleModelProcess() throws Exception {
		Element element = null;
		if (parentPanel instanceof TechnicsStepJPanel_XW) {
			element = ((TechnicsStepJPanel_XW) parentPanel).getElement();
			if (element == null)
				return;
		} else if (parentPanel instanceof TechnicsPaceJDialog) {
			element = ((TechnicsPaceJDialog) parentPanel).paceElement;
			if (element == null)
				return;
		}
		Document doc = element.getDocument();
		if (doc != null) {
			Element ele = XmlUtility.getTechnicsElement(doc);
			String oid = ele.attributeValue("partOid");
			String partNumber = ele.attributeValue("partNumber");

			String technicsCategory = ele.attributeValue("technicsCategory");
			String technicsNumber = ele.attributeValue("technicsNumber");
			String technicsName = ele.attributeValue("technicsName");

			String filePath = "";
			String technicsPath = "";
			if ("rework".equals(technicsCategory)) {
				technicsPath = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
				filePath = WorkSpaceUtil.getReWorkTechnicsPathByTechnicsName(technicsNumber, technicsName);
			} else if ("temp".equals(technicsCategory)) {
				technicsPath = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
				filePath = WorkSpaceUtil.getTempTechnicsPathByTechnicsName(technicsNumber, technicsName);
			} else {
				technicsPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
				filePath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
			}

			Map<String, String> map = new HashMap<String, String>();
			map.put("oid", oid);
			map.put("partNumber", partNumber);
			map.put("filePath", filePath);
			String technicsType = ele.attributeValue("technicsType");
			if ("零件工艺".equals(technicsType)) {
				map.put("technicsType", "singlePart");
			} else {
				map.put("technicsType", "assemblePart");
			}
			logger.debug("简图添加中间模型传递参数========" + map);
			CreoModelDialog dia = new CreoModelDialog(map, frame);

			// TODO 显示当前工步的简图在当前页面上

			Map<String, Vector<List<String>>> stepMap = dia.showDialog();
			if (stepMap != null && stepMap.keySet() != null) {
				Vector<List<String>> v = stepMap.get(element.attributeValue("bsoID"));
				if (v != null && v.size() > 0) {
					for (int i = 0; i < v.size(); i++) {
						List<String> path = (List<String>) v.get(i);
						File f = new File(technicsPath + File.separator + path.get(1));
						if (f.exists() && f.isFile()) {
							addToTable(path.get(3), f, path.get(1), MIDDLEMODEL_TYPE_NAME, path.get(0), false, null, null, null, path.get(2));
							setTabTitle();
						}
					}
				}
			}
			saveDrawing2(stepMap);
			// 从此将 中间件的信息获取出来，然后传到 saveDrawing（）方法保存
		}
	}

	public void showSelectImage() throws Exception {
		if (parentPanel instanceof TechnicsStepJPanel_XW) {
			int[] select = table.getSelectedRows();
			if (select != null && select.length > 0) {
				Element step = ((TechnicsStepJPanel_XW) parentPanel).getElement();
				if (step == null)
					return;
				Document doc = step.getDocument();
				if (doc != null) {
					Element ele = XmlUtility.getTechnicsElement(doc);

					String category = ele.attributeValue("technicsCategory");
					String filePath;
					if ("rework".equals(category)) {
						filePath = WorkSpaceUtil.getReworkTechnicsDirectory(ele.attributeValue("technicsNumber"), ele.attributeValue("technicsName"));
					} else if ("temp".equals(category)) {
						filePath = WorkSpaceUtil.getTempTechnicsDirectory(ele.attributeValue("technicsNumber"), ele.attributeValue("technicsName"));
					} else {
						filePath = WorkSpaceUtil.getTechnicsDirectory(ele.attributeValue("technicsNumber"));
					}
					Vector v = new Vector();
					for (int i = 0; i < select.length; i++) {
						String path = "";
						String xmlPath = (String) table.getValueAt(select[i], 3);
						System.out.println("xmlPath==========" + xmlPath);
						if (filePath.endsWith("\\") || filePath.endsWith("/")) {
							path = filePath + xmlPath;
						} else {
							path = filePath + "\\" + xmlPath;
						}
						if (path.trim().length() > 0) {
							v.add(path);
						}
					}
					JFrame frame = ((TechnicsStepJPanel_XW) parentPanel).getFrame();
					if (frame instanceof NewTechnicsPart) {
						NewTechnicsPart tp = (NewTechnicsPart) frame;
						System.out.println("简图路径============" + v);
						tp.show2DPane(v);
					}
				}
			}
		}
	}

	private void saveDrawing2(Map<String, Vector<List<String>>> map) {
		if (parentPanel instanceof TechnicsStepJPanel_XW) {
			((TechnicsStepJPanel_XW) parentPanel).save2(map);
		} else if (parentPanel instanceof TechnicsPaceJDialog) {
			((TechnicsPaceJDialog) parentPanel).save(false);
			Document document = ((NewTechnicsPart)frame).getCurrentTechnics();
			Element techElement = XmlUtility.getTechnicsElement(document);
			String techPath = WorkSpaceUtil.getTechnicsDirectoryByCategory(techElement);
			if (map != null) {
				// 获取step并从map中获取简图信息，保存进去
				Iterator it = map.keySet().iterator();
				// 清空已有的图形节点，重新保存图形节点
				while (it.hasNext()) {
					String stepOid = (String) it.next();
					Vector v = (Vector) map.get(stepOid);
					Element stepElement = XmlUtility.getStepByStepOid(techElement, stepOid);
					Element imgElement = XmlUtility.getImages(stepElement);
					List<Element> imageElements = imgElement.elements("PDrawingInfo");

					List<String> oids = new ArrayList<String>();
					for (int i = 0; i < v.size(); i++) {
						List<String> list = (List<String>) v.get(i);
						String oid = list.get(0);
						oids.add(oid);
					}

					if (imageElements != null && imageElements.size() != 0) {
						for (int i = imageElements.size() - 1; i >= 0; i--) {
							Element temp = imageElements.get(i);
							String type = temp.attributeValue("type");
							if (NewDrawingJPanel.MIDDLEMODEL_TYPE_NAME.equals(type)) {
								String oid = temp.attributeValue("bsoID");
								if (!oids.contains(oid)) {
									String path = temp.attributeValue("absolutePath");
									File file = new File(techPath + File.separator + path.substring(0, path.lastIndexOf(File.separator)));
									if (file.exists() && file.isDirectory()) {
										try {
											WorkSpaceUtil.delete(file);
										} catch (Exception e) {
											e.printStackTrace();
											JOptionPane.showMessageDialog(null, e.getMessage());
										}
										file.delete();
									}
								}
								imgElement.remove(temp);
							}
						}
					}

					for (int i = 0; i < v.size(); i++) {
						List<String> list = (List<String>) v.get(i);
						String oid = list.get(0);
						String path = list.get(1);
						String version = list.get(2);
						String modelName = list.get(3);
						File file = new File(techPath + File.separator + path);
						if (file.exists() && file.isFile()) {
							// 取出附属信息
							String absolutePath = file.getAbsolutePath();
							String fileName = file.getName();
							String name, type;
							if (fileName.contains(".")) {
								name = fileName.split("\\.")[0];
								type = file.getName().split("\\.")[1];
							} else {
								name = fileName;
								type = "";
							}
							float size = 0;
							java.io.FileInputStream in = null;
							try {
								in = new FileInputStream(absolutePath);
								size = in.available();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} finally {
								CappJavaUtil.closeStream(in);
							}
							Element element = XmlUtility.createImage();
							XmlUtility.setAttributeValue(element, "bsoID", oid);
							XmlUtility.setAttributeValue(element, "drawingName", name);
							XmlUtility.setAttributeValue(element, "drawingType", type);
							XmlUtility.setAttributeValue(element, "drawingSize", size + "");
							XmlUtility.setAttributeValue(element, "absolutePath", path);
							XmlUtility.setAttributeValue(element, "imageOid", "");
							XmlUtility.setAttributeValue(element, "parentImageOid", "");
							XmlUtility.setAttributeValue(element, "type", NewDrawingJPanel.MIDDLEMODEL_TYPE_NAME);
							XmlUtility.setAttributeValue(element, "version", version);
							XmlUtility.setAttributeValue(element, "modelName", modelName);
							imgElement.add(element);
						}
					}
				}
			}

			// XmlUtility.orderSteps(techEle);
			// Element paceElement;
			// ((NewTechnicsPart) ((TechnicsPaceJDialog)
			// parentPanel).getFrame())
			// .saveProcess(techElement);
			// Element imageElement = paceElement.element("images");
			// Vector imageVec = new Vector();
			// for (Iterator it = imageElement.elementIterator("PDrawingInfo");
			// it
			// .hasNext();) {
			// imageVec.add((Element) it.next());
			// }
			// Document doc = paceElement.getDocument();
			// if (doc != null) {
			// this.setTableValues(imageVec);
			// }
		}
	}

	private void saveDrawing(Map<String, Vector<String>> map) {
		if (parentPanel instanceof TechnicsStepJPanel_XW) {
			((TechnicsStepJPanel_XW) parentPanel).save(map);
		} else if (parentPanel instanceof TechnicsPaceJDialog) {
			((TechnicsPaceJDialog) parentPanel).save();
		}
	}

	class ViewPopupMenu extends JPopupMenu {
		JMenuItem view = new JMenuItem("查看");

		ViewPopupMenu() {
			add(view);
			view.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						showSelectImage();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
		}
	}

	public void setUIEnabled(boolean b) {
		table.setEnabled(b);
		newJButton.setEnabled(b);
		addJButton.setEnabled(b);
		if (flag) {
			addMiddleModelJButton.setEnabled(b);
			refreshMiddleModelJButton.setEnabled(b);
		}
		deleteJButton.setEnabled(b);
		addDwgJButton.setEnabled(b);
		uploadDwgJButton.setEnabled(b);
		openJButton.setEnabled(b);
		upJButton.setEnabled(b);
		downJButton.setEnabled(b);
		renameJButton.setEnabled(b);
	}

	public void setMiddleModelJButtonVisible(Document document) {
		if (document == null)
			return;
		Element techElement = null;
		try {
			techElement = XmlUtility.getTechnicsElement(document);
		} catch (Exception e) {

			e.printStackTrace();
			NewTechnicsPart frame = null;
			if (parentPanel instanceof TechnicsStepJPanel_XW) {
				frame = (NewTechnicsPart) ((TechnicsStepJPanel_XW) parentPanel)
						.getFrame();
			} else if (parentPanel instanceof TechnicsPaceJDialog) {
				frame = (NewTechnicsPart) ((TechnicsPaceJDialog) parentPanel)
						.getFrame();
			}
			JOptionPane.showMessageDialog(frame, "获取工艺类型时出现错误！", "提示",
					JOptionPane.INFORMATION_MESSAGE);
		}
		// String type = techElement.attributeValue("technicsType");
		// if (type != null && type.trim().equals(WorkSpaceUtil.PART_TYPE)) {
		// addMiddleModelJButton.setVisible(true);
		// } else {
		// addMiddleModelJButton.setVisible(true);
		// }
	}

	private void writePicture(String sourcepath, String path, String fileName) throws Exception {
		Element techele = null;
		techele= this.getTechnicElment();
		String technicsCategory = techele.attributeValue("technicsCategory");
		String technicsName = techele.attributeValue("technicsName");
		String technicsNumber = techele.attributeValue("technicsNumber");
		Vector vec = XmlUtility.getAllDrawings(techele);
		for (int i = 0; i < vec.size(); i++) {
			Element element = (Element) vec.get(i);
			XmlUtility.writeDrawingFiles(element, technicsCategory, technicsNumber, technicsName, sourcepath, true);
		}
	}

	private void writePicture2(String sourcepath, String path, String fileName) throws Exception {
		Element techele = null;
		techele= this.getTechnicElment();
		String technicsCategory = techele.attributeValue("technicsCategory");
		String technicsName = techele.attributeValue("technicsName");
		String technicsNumber = techele.attributeValue("technicsNumber");
		Vector vec = XmlUtility.getAllDrawings(techele);
		for (int i = 0; i < vec.size(); i++) {
			Element element = (Element) vec.get(i);
			XmlUtility.writeDrawingFiles(element, technicsCategory, technicsNumber, technicsName, sourcepath, path, true);
		}
	}

	public Element getTechnicElment(){
		Element techele = null;
		if (parentPanel instanceof TechnicsStepJPanel_XW) {
			techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) ((TechnicsStepJPanel_XW) parentPanel)
							.getFrame()).getCurrentTechnics());
		} else if (parentPanel instanceof TechnicsPaceJDialog) {
			techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) ((TechnicsPaceJDialog) parentPanel)
							.getFrame()).getCurrentTechnics());
		}
		return techele;
	}


	private FileTemplate getTemplate() throws Exception{
		Element ele = this.getTechnicElment();

		String technicsNumber = ele.attributeValue("technicsNumber");
		String technicsName = ele.attributeValue("technicsName");
		String technicsCategory = ele.attributeValue("technicsCategory");

		String path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber,technicsName, technicsCategory);
		File floder = new File(path,"fbtemp");
		if(!floder.exists()){
			floder.mkdirs();
		}

		String fName  = null;

		TempChooseDialog dialog = new TempChooseDialog(this.frame,LoadConfig.getInstance().getDwgTemplateType());

		FileTemplate fileTemplate= dialog.showDialog();
		System.out.println(fileTemplate);

		if(fileTemplate==null){
			return null;
		}else{
			fName = fileTemplate.getFileName();
		}
		if(fName==null){
			return null;
		}
		File dwgFile = new File(floder,fName+".dwg");
//		InputStream is = null;
		ByteArrayInputStream bis = null;
			try {
//				is =NewDrawingJPanel.class.getResourceAsStream("/templates/"+fileTemplate.getTempId()+".dwg");
//				FileUtils.copyInputStreamToFile(is, dwgFile);
				byte[] b = TemplateIntf.getDwgTemplateByte(fileTemplate.getTempId());
				bis = new ByteArrayInputStream(b);
				FileUtils.copyInputStreamToFile(bis, dwgFile);
			}
			finally{
//				try {
//					if(is!=null)
//						is.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}

				try {
					if(bis!=null)
						bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			fileTemplate.setFile(dwgFile);
			return fileTemplate;
	}

	private void openFile() {
		Element techele = this.getTechnicElment();
		try {
			if(techele==null) return;
			String technicsCategory = techele.attributeValue("technicsCategory");
			String technicsNumber = techele.attributeValue("technicsNumber");
			String technicsName = techele.attributeValue("technicsName");
			String filePath;
			if ("rework".equals(technicsCategory)) {
				filePath = WorkSpaceUtil.getReworkTechnicsDirectory(
						technicsNumber, technicsName);
			} else if ("temp".equals(technicsCategory)) {
				filePath = WorkSpaceUtil.getTempTechnicsDirectory(
						technicsNumber, technicsName);
			} else {
				filePath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
				if(parentPanel instanceof NewTechnicsHistoryView){
					filePath = WorkSpaceUtil.getTempRootPath()+File.separator+ technicsNumber;
				}
			}
			System.out.println("filePath==========" + filePath);
			Vector<String> vector = new Vector<String>();
			String path = "";
			int[] select = table.getSelectedRows();
			for (int i = 0; i < select.length; i++) {
				String attachPath = (String) table.getValueAt(select[i], 3);
				if (filePath.endsWith("\\") || filePath.endsWith("/")) {
					path = filePath + attachPath;
				} else {
					path = filePath + "\\" + attachPath;
				}
				vector.add(path);
			}
			System.out.println("vector==========" + vector);
			for (int i = 0; i < vector.size(); i++) {
				Runtime.getRuntime().exec(
						"rundll32.exe url.dll,FileProtocolHandler  "
								+ vector.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void moveUpProcess() {
		// stopTableCellEditing();
		changeRowValue(true);
		saveAttach();
	}

	private void moveDownProcess() {
		// stopTableCellEditing();
		changeRowValue(false);
		saveAttach();
	}

	private void changeRowValue(boolean up) {
		if (table.getSelectedRowCount() > 1)
			return;
		int select = table.getSelectedRow();
		if (select < 0 || select >= table.getRowCount())
			return;
		if (up && select == 0)
			return;
		if (!up && select == table.getRowCount() - 1)
			return;
		Object[] obj1 = new Object[table.getColumnCount()];
		Object[] obj2 = new Object[table.getColumnCount()];
		int neighbor;
		if (up)
			neighbor = select - 1;
		else
			neighbor = select + 1;
		for (int i = 0; i < table.getColumnCount(); i++) {
			obj1[i] = table.getValueAt(select, i);
			obj2[i] = table.getValueAt(neighbor, i);
		}
		for (int j = 0; j < table.getColumnCount(); j++) {
			table.setValueAt(obj2[j], select, j);
			table.setValueAt(obj1[j], neighbor, j);
		}
		table.setRowSelectionInterval(neighbor, neighbor);
	}

	private void saveAttach() {
		Document rootdoc=null;
		Element stepElement=null;
		NewTechnicsPart technicspart=null;
		Element paceElement=null;
		Element drawingElement=null;
		Vector<Element> elements=null;
		if (parentPanel instanceof TechnicsStepJPanel_XW) {
			technicspart=(NewTechnicsPart) ((TechnicsStepJPanel_XW) parentPanel).getFrame();
            rootdoc=technicspart.getCurrentTechnics();
            stepElement=((TechnicsStepJPanel_XW) parentPanel).stepElement;
            Document doc = stepElement.getDocument();
    		if (doc == null) {
    			String bsoID = stepElement.attributeValue("bsoID");
    			Element step = XmlUtility.getStepByID(XmlUtility.getTechnicsElement(rootdoc), bsoID);
    			if (step == null)
    				return;
    			stepElement = step;
    		}
    		drawingElement = XmlUtility.getImages(stepElement);
		    elements=getElements();
		} else if (parentPanel instanceof TechnicsPaceJDialog) {
			technicspart=(NewTechnicsPart) ((TechnicsPaceJDialog) parentPanel).getFrame();
            rootdoc=technicspart.getCurrentTechnics();
			stepElement=((TechnicsPaceJDialog) parentPanel).stepElement;
			paceElement=((TechnicsPaceJDialog)parentPanel).paceElement;
			   Document doc = paceElement.getDocument();
	    		if (doc == null) {
	    			String bsoID = paceElement.attributeValue("bsoID");
	    			Element pace = XmlUtility.getPaceByCortonaID(stepElement,bsoID);
	    			if (pace == null)
	    				return;
	    			paceElement = pace;
	    		}
	    		drawingElement = XmlUtility.getImages(paceElement);
				elements=((TechnicsPaceJDialog)parentPanel).getDrawingJPanel().getElements();
		}

		XmlUtility.deleteAllChildElements(drawingElement);
		for(Element element:elements){
			drawingElement.add(element);
		}
		technicspart.saveProcess(XmlUtility.getTechnicsElement(rootdoc));
	}
}