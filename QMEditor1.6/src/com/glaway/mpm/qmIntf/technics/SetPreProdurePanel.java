package com.glaway.mpm.qmIntf.technics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.glaway.mpm.qmIntf.technics.entity.Technics;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.TechnicsStepJPanel_XW;
import com.glaway.mpm.view.XWStepTreeObject;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.view.XWTreeObject;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class SetPreProdurePanel extends JPanel{

	private static final long serialVersionUID = 1L;

	private JPanel mainPanel;
	private JPanel middlePanel;
	private JPanel middleMainPanel;

	private JCheckBox allSelectBox;
	private JComboBox comboBox;

	private JLabel tips;

	private JButton sureButton;
	private JButton cancelButton;

	private JScrollPane jScrollPane;
	private JTable jTable;

	private JDialog dialog;
	private NewTechnicsPart frame;

	public SetPreProdurePanel(JDialog dialog, NewTechnicsPart frame) {
		this.frame = frame;
		this.dialog = dialog;
		init();
	}

	private void init() {
		initLookAndFeel();
		initDimension();
		initComponents();
		initLayout();
		loadInitDatas();
		initActions();
	}

	private void initLookAndFeel() {

	}

	private void initDimension() {

	}

	private void initComponents() {
		mainPanel = new JPanel();
		middlePanel = new JPanel();
		middleMainPanel = new JPanel();

		allSelectBox = new JCheckBox();

		tips = new JLabel();
		tips.setVisible(false);

		sureButton = new JButton();
		cancelButton = new JButton();

		jScrollPane = new JScrollPane();
		jTable = new JTable();
	}

	private void initLayout(){

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 1;

		c.gridy = 1;
		middleMainPanel.setLayout(new GridBagLayout());
		middleMainPanel.add(tips, c);

		c.gridy = 2;
		jTable.setRowHeight(23);
		jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jTable.getTableHeader().setPreferredSize(new Dimension(20, 25));
		jScrollPane.setViewportView(jTable);
		jScrollPane.setPreferredSize(new Dimension(590, 270));
		middleMainPanel.add(jScrollPane, c);

		c.gridy = 3;
		allSelectBox.setPreferredSize(new Dimension(90,25));
		sureButton.setPreferredSize(new Dimension(90, 25));
		cancelButton.setPreferredSize(new Dimension(90, 25));

		c.insets = new Insets(0, 15, 5, 290);
		middleMainPanel.add(allSelectBox, c);

		c.insets = new Insets(30, 390, 5, 0);
		middleMainPanel.add(sureButton, c);

		c.insets = new Insets(30, 490, 5, 20);
		middleMainPanel.add(cancelButton, c);

		middlePanel.add(middleMainPanel,BorderLayout.CENTER);

		mainPanel.setLayout(new BorderLayout(1, 2));
		mainPanel.add(middlePanel, BorderLayout.CENTER);

		this.add(mainPanel);

	}

	private void initActions(){
//		TableModel model = jTable.getModel();
//		model.addTableModelListener(new TableModelListener() {
//			@Override
//			public void tableChanged(TableModelEvent e) {
//				int selectedRow = jTable.getSelectedRow();
//				int selectedColumn = jTable.getSelectedColumn();
//				if(selectedColumn == 2){
//					String selectedValue = (String) jTable.getValueAt(selectedRow, selectedColumn);
//					String isSelected = String.valueOf(jTable.getValueAt(selectedRow, 0));
//					if(isSelected.equals("true")){
//						if(rowNumber < jTable.getRowCount()){
//							String isSelect = String.valueOf(jTable.getValueAt(rowNumber, 0));
//							if(isSelect.equals("true")){
//								rowNumber++;
//								jTable.setValueAt(selectedValue, rowNumber - 1, 2);
//							}
//						}else{
//							rowNumber = 0;
//						}
//					}
//				}
//			}
//		});
//		jTable.addMouseListener(new MouseAdapter() {
//
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				Point point = e.getPoint();
//				int row = jTable.rowAtPoint(point);
//				int column = jTable.columnAtPoint(point);
//				System.out.println(row + "," + column);
//				if(row != -1 && column != -1){
//					List<Element> proceduresFZ = XmlUtility.getAllSteps(technicElement);
//					Element fzProcedure = proceduresFZ.get(row);
//
//					String workShop = fzProcedure.getParent().getParent().attributeValue("DEPT");
//					Vector<String> vector = new Vector<String>();
//					try {
//						proceduresDX = TechnicsIntf.getProceduresOfTechnic(docNumber);
//						for(Element dxProcedure : proceduresDX){
//							String dxWorkShop = dxProcedure.attributeValue("workShop");
//							if(dxWorkShop.equals(workShop)){
//								String stepNumber = dxProcedure.attributeValue("stepNumber");
//								String stepName = dxProcedure.attributeValue("stepName");
//								vector.add(stepNumber + "_" + stepName);
//							}
//						}
//						comboBox = new JComboBox(vector);
//						jTable.getColumnModel().getColumn(column).setCellEditor(new DefaultCellEditor(comboBox));
//						jTable.getTableHeader().setReorderingAllowed(false);
//					} catch (RemoteException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					} catch (InvocationTargetException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//					jTable.updateUI();
//				}
//			}
//
//			@Override
//			public void mouseExited(MouseEvent e) {
//				Point point = e.getPoint();
//				int row = jTable.rowAtPoint(point);
//				int column = jTable.columnAtPoint(point);
//				if(row != -1 && column != -1){
//					jTable.getColumnModel().getColumn(column).setCellEditor(null);
//					jTable.updateUI();
//				}
//			}
//
//
//
//		});
		allSelectBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				JCheckBox box = (JCheckBox) e.getSource();
				int row = jTable.getRowCount();
				String isSelected = String.valueOf(box.isSelected());
				if(isSelected.equals("true")){
					for(int i = 0; i < row; i++){
						jTable.setValueAt(true, i, 0);
					}
				}else if(isSelected.equals("false")){
					for(int i = 0; i < row; i++){
						jTable.setValueAt(false, i, 0);
					}
				}
			}
		});
		sureButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String preProdures = "";
				String preBsoIDs = "";
				DefaultTableModel tableModel = (DefaultTableModel) jTable.getModel();
				int row = tableModel.getRowCount();
				for(int i = 0; i < row; i++){
					boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);
					String preProcedure = (String) tableModel.getValueAt(i, 1);
					String preBsoId = (String) tableModel.getValueAt(i, 2);
					if(isSelected){
						if("".equals(preProdures)){
							preProdures = preProcedure;
							preBsoIDs = preBsoId;
						}else{
							preProdures = preProdures + "," + preProcedure;
							preBsoIDs = preBsoIDs + "," + preBsoId;
						}
					}
				}
				if("".equals(preBsoIDs) && row > 0){ //设置必须存在前置工序 add by zhuhao 2017.12.15
					preProdures = (String) tableModel.getValueAt(row - 1, 1);
					preBsoIDs = (String) tableModel.getValueAt(row - 1, 2);
				}
				XWTreeNode node = frame.technicsTreePanel.getSelectedTreeNode();
				XWTreeObject xo = node.getObject();
				if ((xo instanceof XWStepTreeObject)) {
					Element stepElement = xo.getTreeCellData();
					stepElement.addAttribute("preStep", preProdures);
					stepElement.addAttribute("preBsoID", preBsoIDs);
					Element techele = XmlUtility.getTechnicsElement(frame.getCurrentTechnics());
					String technicsNumber = techele.attributeValue("technicsNumber");
					String technicsFilePath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
					saveDocument(stepElement.getDocument(), new File(technicsFilePath));
				}
				dialog.dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
	}
	public static void saveDocument(Document document, File file){
		if ((document == null) || (file == null))
			return;
		XMLWriter writer = null;
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GBK");
			writer = new XMLWriter(new FileOutputStream(file), format);
			writer.write(document);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(writer != null){
					writer.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void loadInitDatas(){
		allSelectBox.setText("全选");
		sureButton.setText("确定");
		cancelButton.setText("取消");
		jTable.setModel(getModel());
		loadTable();

	}
	private DefaultTableModel getModel() {
		DefaultTableModel model = null;
			model = new DefaultTableModel(new Object[][]{},
					new Object[] {"", "可选前置工序","bsoID"}) {
				private static final long serialVersionUID = 1L;
				Class[] types = new Class[] { Boolean.class,
						String.class,String.class };
				boolean[] canEdit = new boolean[] {true, false, false };

				public Class getColumnClass(int columnIndex) {
					return types[columnIndex];
				}
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return canEdit[columnIndex];
				}
			};
				return model;
	}



	private String convertNull(Object str) {
		return str == null ? "" : str.toString();
	}
	private void loadTable() {
		TableColumn column = jTable.getColumnModel().getColumn(0);
		column.setMaxWidth(50);
		column.setMinWidth(50);
		column.setPreferredWidth(50);
		column.setWidth(50);
		jTable.getColumnModel().getColumn(2).setMinWidth(0);
		jTable.getColumnModel().getColumn(2).setMaxWidth(0);

		DefaultTableModel tableModel = (DefaultTableModel) jTable.getModel();
		XWTreeNode node = frame.technicsTreePanel.getSelectedTreeNode();
		if (node != null) {
			XWTreeObject xo = node.getObject();
			if ((xo instanceof XWStepTreeObject)) {
				Element stepElement = xo.getTreeCellData();
				String preStep = stepElement.attributeValue("preStep");
				String preBsoID = stepElement.attributeValue("preBsoID");
				XWTreeNode techNode = frame.technicsTreePanel.getCurrentTechnicsNode();
				for(int i = 0 ; i < techNode.getChildCount(); i++){
					XWTreeNode childNode = (XWTreeNode) techNode.getChildAt(i);
					String childNodeName = childNode.getDisplayName();
					String bsoID = childNode.getObject().getTreeCellData().attributeValue("bsoID");
					if(childNode.equals(node)){
						break;
					}
					addOneRow(tableModel);
					if(preStep !=null && preBsoID.indexOf(bsoID) != -1){
						tableModel.setValueAt(true, i, 0);
					}else{
						tableModel.setValueAt(false, i, 0);

					}
					tableModel.setValueAt(childNodeName, i, 1);
					tableModel.setValueAt(bsoID, i, 2);
				}
			}
		}
	}

	private void addOneRow(DefaultTableModel tableModel) {
		Vector vector = new Vector();
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			vector.add("");
		}
		tableModel.addRow(vector);
	}
//	public List<Element> getElements() {
//		Vector<Element> elements = new Vector<Element>();
//		Element technicDX = proceduresDX.get(0).getParent().getParent();
//			Element element = DocumentHelper.createElement("borrowTechnics");
//			element.setAttributeValue("bsoID", "");
//			element.setAttributeValue("oid", "");
//			element.setAttributeValue("technicsNumber", "");
//			element.setAttributeValue("technicsName", "");
//			element.setAttributeValue("technicsType", "");
//			element.setAttributeValue("docNumber", "");
//
//			XmlUtility.setAttributeValue(element, "bsoID", "");
//			XmlUtility.setAttributeValue(element, "technicsNumber", technicDX.attributeValue("technicsNumber"));
//			XmlUtility.setAttributeValue(element, "technicsName", technicDX.attributeValue("technicsName"));
//			XmlUtility.setAttributeValue(element, "technicsType", technicDX.attributeValue("technicsType"));
//			XmlUtility.setAttributeValue(element, "oid", technicDX.attributeValue("oid"));
//			XmlUtility.setAttributeValue(element, "docNumber", technicDX.attributeValue("docNumber"));
//			XmlUtility.setAttributeValue(element, "DEPT", technicDX.attributeValue("DEPT"));
//			XmlUtility.setAttributeValue(element, "comment", technicDX.attributeValue("comment"));
//			elements.add(element);
//		return elements;
//	}
	public static void removeHistoryData(DefaultTableModel model,Element technicElement){
		int row = model.getRowCount();
		for(int i = 0; i < row; i++){
			String stepNumberTE = model.getValueAt(i, 2).toString();
			List<Element> proceduresList = XmlUtility.getAllSteps(technicElement);
			for(Element procedure : proceduresList){
				if(procedure.attributeValue("stepNumber").equals(stepNumberTE)){
					List<Attribute> attributes = procedure.attributes();
					List<String> nameList = new ArrayList<String>();
					for(Attribute attribute : attributes){
						String name = attribute.getName();
						if(name.contains("relatedTypical")){
							nameList.add(name);
						}
					}
					for(String name : nameList){
						procedure.remove(procedure.attribute(name));
					}
				}
			}
		}

	}
}
