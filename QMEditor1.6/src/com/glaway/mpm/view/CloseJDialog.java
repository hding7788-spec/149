package com.glaway.mpm.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.util.*;

import org.dom4j.Document;
import org.dom4j.Element;

import com.glaway.mpm.model.UploadTechnics;
import com.glaway.mpm.pdf.PDFPreviewFactory;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.TechnicsIntf;

import wt.util.WTException;

public class CloseJDialog extends JDialog implements ActionListener{
	private HashMap<String, Element> technicsCashe = new HashMap<String, Element>();

	private NewTechnicsPart parent = null;
	private TechnicsModel tableModel = new TechnicsModel();
	private JTable table = new JTable(tableModel);
	private   boolean flag=false;
	private LabelRender labelRender = new LabelRender();
	private IsKeyRenderer checkRender = new IsKeyRenderer();
	private JButton noButton = new JButton("不上载");
	private JButton okButton = new JButton("上载");
	private JButton cancelButton = new JButton("取消");
	private JPanel bottomPanel = new JPanel();
	private JCheckBox box = new JCheckBox("全部选中");
	public static String docStyle;
	private int result = JOptionPane.NO_OPTION;

	public CloseJDialog(NewTechnicsPart frame) {
		super(frame, "请保存修改后的工艺规程", true);
		parent = frame;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		tableModel.addColumn("选中");
		tableModel.addColumn("工艺规程名称");
		tableModel.addColumn("版本");
		tableModel.addColumn("备注");
		tableModel.addColumn("工艺编号");
		tableModel.addColumn("工艺类别");
		box.setSelected(true);
		box.addActionListener(this);
		okButton.addActionListener(this);
		noButton.addActionListener(this);
		cancelButton.addActionListener(this);

		okButton.setMaximumSize(new Dimension(80, 23));
		okButton.setMinimumSize(new Dimension(80, 23));
		okButton.setPreferredSize(new Dimension(80, 23));

		noButton.setMaximumSize(new Dimension(80, 23));
		noButton.setMinimumSize(new Dimension(80, 23));
		noButton.setPreferredSize(new Dimension(80, 23));

		cancelButton.setMaximumSize(new Dimension(80, 23));
		cancelButton.setMinimumSize(new Dimension(80, 23));
		cancelButton.setPreferredSize(new Dimension(80, 23));

		// checkRender.setBackground(Color.gray);
		// table.getColumnModel().getColumn(0).setHeaderRenderer(checkRender);
		// table.getColumnModel().getColumn(1).setHeaderRenderer(new
		// LabelRender());
		// table.getColumnModel().getColumn(2).setHeaderRenderer(new
		// LabelRender());
		// table.getColumnModel().getColumn(3).setHeaderRenderer(new
		// LabelRender());

		table.putClientProperty("terminateEditOnFocusLost", true);
		table.getColumnModel().getColumn(0).setCellRenderer(checkRender);
		table.getColumnModel().getColumn(0)
				.setCellEditor(new IsKeyEditor(new JCheckBox()));
		table.getColumnModel().getColumn(1).setCellRenderer(labelRender);
		table.getColumnModel().getColumn(2).setCellRenderer(labelRender);
		table.getColumnModel().getColumn(3).setCellRenderer(new TextRender());
		table.getColumnModel().getColumn(3)
				.setCellEditor(new TextEditor(new JCheckBox()));

		table.getColumnModel().getColumn(0).setPreferredWidth(50);
		table.getColumnModel().getColumn(0).setMinWidth(50);
		table.getColumnModel().getColumn(0).setMaxWidth(50);

		table.getColumnModel().getColumn(1).setPreferredWidth(250);
		table.getColumnModel().getColumn(1).setMinWidth(250);
		table.getColumnModel().getColumn(1).setMaxWidth(250);

		table.getColumnModel().getColumn(2).setPreferredWidth(70);
		table.getColumnModel().getColumn(2).setMinWidth(70);
		table.getColumnModel().getColumn(2).setMaxWidth(70);

		setHiddenColumn(4);
		setHiddenColumn(5);
		JScrollPane pane = new JScrollPane(table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		table.setRowHeight(60);
		table.getTableHeader().setReorderingAllowed(false);
		table.setGridColor(Color.GRAY);
		pane.getViewport().setBackground(Color.white);
		setLayout(new GridBagLayout());
		add(pane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						5, 5, 5, 5), 0, 0));

		bottomPanel.setLayout(new GridBagLayout());
		bottomPanel.add(box, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(0, 5, 5, 5), 0, 0));
		bottomPanel.add(new JLabel(), new GridBagConstraints(1, 0, 1, 1, 1.0,
				1.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
		bottomPanel.add(okButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
				new Insets(0, 5, 5, 5), 0, 0));
		bottomPanel.add(noButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
				new Insets(0, 5, 5, 5), 0, 0));
		bottomPanel.add(cancelButton, new GridBagConstraints(4, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
				new Insets(0, 5, 5, 5), 0, 0));

		add(bottomPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 5, 5, 5), 0, 0));

		initData();

		table.getColumnModel().addColumnModelListener(
				new TableColumnModelListener() {
					public void columnAdded(TableColumnModelEvent e) {
					}

					public void columnMarginChanged(ChangeEvent e) {
						stopTableCellEditing();
					}

					public void columnMoved(TableColumnModelEvent e) {
					}

					public void columnRemoved(TableColumnModelEvent e) {
					}

					public void columnSelectionChanged(ListSelectionEvent e) {
					}
				});
	}

	public void addProcess() {
		Vector vector = new Vector();
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			vector.add("");
		}
		tableModel.addRow(vector);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
		    if (table.getRowCount()>0) {
		        run();
            }else{
                dispose();
            }

		} else if (e.getSource() == noButton) {
			result = JOptionPane.YES_OPTION;
			dispose();
		} else if (e.getSource() == cancelButton) {
			result = JOptionPane.NO_OPTION;
			dispose();
		} else if (e.getSource() == box) {
			for (int i = 0; i < table.getRowCount(); i++) {
				boolean b = box.isSelected();
				table.setValueAt(String.valueOf(b), i, 0);
			}
		}
	}

	public int showDialog() {
		setSize(800, 500);
		SwingUtil.setMiddle(this);
		setResizable(false);
		setVisible(true);
		return result;
	}

	public void initData() {
		try {
			Vector<UploadTechnics> data = NewTechnicsPart.editTechnics;
			System.out.println("编辑的工艺为======" + data);
			if (data != null && data.size() > 0) {
				for (int i = 0; i < data.size(); i++) {
					UploadTechnics uploadTechnics = (UploadTechnics) data.get(i);
					Document doc;
					String technicsNumber = uploadTechnics.getTechnicsNumber();
					String technicsName = uploadTechnics.getTechnicsName();
					System.out.println("technicsName = " + technicsName);
					String technicsCategory = uploadTechnics.getTechnicsCategory();
					if ("rework".equals(technicsCategory)) {
						if (WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName) == null) {
							continue;
						}
						doc = WorkSpaceUtil.getReWorkTechnicsDocumentByTechnicsName(technicsNumber, technicsName);
					} else if ("temp".equals(technicsCategory)) {
						if (WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName) == null) {
							continue;
						}
						doc = WorkSpaceUtil.getTempTechnicsDocumentByTechnicsName(technicsNumber, technicsName);
					} else {
						if (WorkSpaceUtil.getTechnicsDirectory(technicsNumber) == null) {
							continue;
						}
						doc = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(technicsNumber);
					}
					if (doc != null) {

						Element tech = XmlUtility.getTechnicsElement(doc);
						// UploadTechnics technics = ObjectTransfer
						// .technicsElementToUploadTechnics(tech);
						if(tech != null && !NewTechnicsPart.currentUser.equals(tech.attributeValue("creator"))){
							continue;
						}
						addProcess();
						int row = table.getRowCount() - 1;
						table.setValueAt("true", row, 0);
						technicsCashe.put(tech.attributeValue("technicsName"), tech);
						System.out.println("technicsName = " + technicsName);
						table.setValueAt(technicsName, row, 1);

						String version = tech.attributeValue("version");
						if (version == null || version.equals(""))
							version = "1.0";
						table.setValueAt(version, row, 2);

						table.setValueAt(technicsNumber, row, 4);
						table.setValueAt(technicsCategory, row, 5);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean technicsUpload(String technicsNumber, String technicsName,
			String technicsCategory, String version, String content) {
		UploadTechnics technics = new UploadTechnics();
		technics.setTechnicsNumber(technicsNumber);
		technics.setTechnicsName(technicsName);
		technics.setTechnicsCategory(technicsCategory);
		if (technicsNumber != null && version != null) {
			Element element = (Element) technicsCashe.get(technicsName);
			if (version == null || version.equals(""))
				version = "1.0";
			System.out.println("获得的工艺编号为=====" + technicsNumber);
			System.out.println("获得的工艺名称为=====" + technicsName);
			String technicsType = element.attributeValue("technicsType");

//-----------modify by caolei 2015-08-24

			try {
				String docState = TechnicsIntf.getDocumentStateByNumber(technicsNumber);
				if(!"".equals(docState) && !"正在工作".equals(docState) && !"修改中".equals(docState)) {
					JOptionPane.showMessageDialog(parent, "此工艺文件已经提交签审，不能再修改！");
					return false;
				}
			} catch (WTException e2) {
				e2.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

//------------modify by caolei 2015-08-24

			String pdfFile=null;
			String path=null;
			try {
				path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber,technicsName, technicsCategory);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pdfFile = path + File.separator + "PDFPreview.pdf";
			File floder1 = new File(pdfFile);
			if(floder1.exists()){
				boolean flag = floder1.delete();
				if(!flag){
					JOptionPane.showMessageDialog(parent, "工艺名称为:"+technicsName+"的PDF文件正在使用，请先关闭再上载！");
					return false;
				 }
             }



			try {
				byte[] bytes;
				String technicsDirectory = "";
				if ("rework".equals(technicsCategory)) {
					bytes = FilesUtil.getReworkTechnicsByte(technicsNumber, technicsName);
					technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
				} else if ("temp".equals(technicsCategory)) {
					bytes = FilesUtil.getTempTechnicsByte(technicsNumber, technicsName);
					technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
				} else {
					bytes = FilesUtil.getTechnicsByte(technicsNumber);
					technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
				}
				String technicsFolderName = technicsDirectory.substring(technicsDirectory.lastIndexOf("\\") + 1, technicsDirectory.length());
				String partNumber = element.attributeValue("partNumber");
				String partOid = element.attributeValue("partOid");
				String partName = element.attributeValue("partName");
				String partType = element.attributeValue("partType");
//				String technicsType = element.attributeValue("technicsType");
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("oid", partOid);
				map.put("partNumber", partNumber);
				map.put("partName", partName);
				map.put("partType", partType);
				map.put("version", version);
				map.put("technicsType", technicsType);
				map.put("technicsNumber", technicsNumber);
				map.put("technicsName", technicsName);
				map.put("pplanNumber", element.attributeValue("pplanNumber"));
				map.put("SECRET", element.attributeValue("SECRET"));
				 if("SOP标准操作规程".equals(technicsType)){
                 	map.put("SopNumber",element.attributeValue("SopNumber"));
                 	map.put("SpecializedType",element.attributeValue("SpecializedType"));
                 	map.put("ProceduceName",element.attributeValue("ProceduceName"));
                 	map.put("OperationJob",element.attributeValue("OperationJob"));
                 	map.put("CustomArea",element.attributeValue("CustomArea"));
                 	map.put("Parameters",element.attributeValue("Parameters"));
        			map.put("Term", element.attributeValue("Term"));
                 	map.put("ProfessionalCode",element.attributeValue("ProfessionalCode"));
                 	map.put("GONGXUJIANHAO",element.attributeValue("GONGXUJIANHAO"));
                 }
				String CINDEX = element.attributeValue("CINDEX");
				String MINDEX = element.attributeValue("MINDEX");
				String PINDEX = element.attributeValue("PINDEX");
				String PPLANTYPE = element.attributeValue("PPLANTYPE");
				String ZFFLAG = element.attributeValue("ZFFLAG");
				String DEPT = element.attributeValue("DEPT");
				String PHASE_CODE = element.attributeValue("PHASE_CODE");
				String KEYCOMPONENT = element.attributeValue("KEYCOMPONENT");
				String BATCH=element.attributeValue("PCNO");
				String BIAOSHI=element.attributeValue("BIAOSHI");
				String isCLDE=XmlUtility.isCLDE(element);

				map.put("CINDEX", CINDEX);
				map.put("MINDEX", MINDEX);
				map.put("PINDEX", PINDEX);
				map.put("PPLANTYPE", PPLANTYPE);
				map.put("ZFFLAG", ZFFLAG);
				map.put("DEPT", DEPT);
				map.put("PHASE_CODE", PHASE_CODE);
				map.put("KEYCOMPONENT", KEYCOMPONENT);
				map.put("BATCH", BATCH);
				map.put("BIAOSHI", BIAOSHI);
				map.put("isCLDE", isCLDE);
				System.out.println("map=======" + map);
				HashMap<String, String> returnMap;
				returnMap = TechnicsIntf.uploadTechnics(bytes, map, version, content);
				System.out.println("returnMap= " + returnMap);
				String success = returnMap.get("success");
				if (success.equals("success")) {
					String newVersion = returnMap.get("version");
					String lifecycle = returnMap.get("lifecycle");
					XmlUtility.setAttributeValue(element, "version", newVersion);
					XmlUtility.setAttributeValue(element, "lifecycle", lifecycle);
					XWTreeNode rootNode = (XWTreeNode) parent.getXWPartTreePanel().getTree().getModel().getRoot();
					parent.updateTreeTechnics(rootNode, partOid, technicsName, technicsCategory, newVersion);

					String qname = element.getName();
					System.out.println("qname======"+qname);
					if("QMFawTechnicsInfo".equals(qname)) {
						parent.saveProcess(element);
					} else if("XWReportTechnicsInfo".equals(qname)) {
						//报表类工艺文件
						parent.saveReportProcess(element);
					}
				} else {
					String errorMessage = returnMap.get("errorMessage");
					if (errorMessage != null && !errorMessage.equals("")) {
						JOptionPane.showMessageDialog(parent, errorMessage, "提示", JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(parent, "工艺上载过程中出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
					}
					return false;
				}

				 technicsPdfUpload(technicsNumber, technicsName, technicsType, element, technicsCategory);

			} catch (Exception e1) {
				JOptionPane.showMessageDialog(parent, "工艺上载过程中出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
				e1.printStackTrace();
				return false;
			}
			NewTechnicsPart.editTechnics.remove(technics);
			return true;
		}
		return true;
	}


	public void technicsPdfUpload(String technicsNumber, String technicsName,String technicsType,Element element,String technicsCategory){
        int pages=0;
		String pdfFile = null;
		String path=null;
//		Map<String,String> map = new HashMap<String,String>();
		HashMap<String, String> map = new HashMap<String, String>();
		byte[] bytes;
		try {
			List<Map<String,String>> list = new ArrayList<Map<String,String>>();
							map = new HashMap<String, String>();
							map.put("partNumber", element.attributeValue("partNumber"));
							map.put("partName", element.attributeValue("partName"));
							map.put("useCount", element.attributeValue("useCount"));
							map.put("XHPH", element.attributeValue("XHPH"));
							map.put("CSIZE", element.attributeValue("CSIZE"));
							map.put("MTYPE", element.attributeValue("MTYPE"));
							map.put("oid", element.attributeValue("partOid"));
							map.put("partType", element.attributeValue("partType"));
							map.put("technicsType", technicsType);
							map.put("technicsNumber", technicsNumber);
							map.put("technicsName", technicsName);
							list.add(map);
			path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber,technicsName, technicsCategory);
			File floder = new File(path,"fbtemp");
//			String docStyle = NewTechnicsPart.docStyle;

			 docStyle = element.getName();

			if ("XWReportTechnicsInfo".equals(docStyle)) {
			    pages=PDFPreviewFactory.previewForReport(path, false);
            }else{

                pages=PDFPreviewFactory.preview(path, false, list);
            }

			pdfFile = path + File.separator + "PDFPreview.pdf";
			FileUtil.deleteFile(floder);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//--------------------------------------------------------再次上载工艺文件(begin)--------------------------------------
		//将页数pages写入XML
           XmlUtility.setAttributeValue(element, "pageSize", String.valueOf(pages));
           String rootpath= path + File.separator + technicsNumber + ".xml";
           File rootfile = new File(rootpath);
           try {
			XmlUtility.saveDocument(element.getDocument(), rootfile);
			if ("rework".equals(technicsCategory)) {
				bytes = FilesUtil.getReworkTechnicsByte(technicsNumber, technicsName);
			} else if ("temp".equals(technicsCategory)) {
				bytes = FilesUtil.getTempTechnicsByte(technicsNumber, technicsName);
			} else {
				bytes = FilesUtil.getTechnicsByte(technicsNumber);
			}
           map.put("page", String.valueOf(pages));
           //再次打包工艺文件夹并上传


           TechnicsIntf.reUploadTechnics(bytes, map);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

//--------------------------------------------------------再次上载工艺文件(end)--------------------------------------

		try {
			File file = new File(pdfFile);
			TechHelper.uploadAttachForDocument(technicsNumber, "PDFPreview.pdf", file);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(parent, "上载PDF工艺文件过程中出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
		}


	}


	public void setHiddenColumn(int columnIndex) {
		if (columnIndex >= 0 && columnIndex < table.getColumnCount()) {
			// 隐藏ID列
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setMaxWidth(0);
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setMinWidth(0);
			table.getColumnModel().getColumn(columnIndex).setMaxWidth(0);
			table.getColumnModel().getColumn(columnIndex).setPreferredWidth(0);
			table.getColumnModel().getColumn(columnIndex).setWidth(0);
			table.getColumnModel().getColumn(columnIndex).setMinWidth(0);
		}
	}

	class TechnicsModel extends DefaultTableModel {
		public boolean isCellEditable(int row, int column) {
			if (column == 1 || column == 2||column==4)
				return false;
			return true;
		}
	}

	class IsKeyEditor extends DefaultCellEditor {
		JPanel panel = null;
		JCheckBox checkBox = null;;
		private JTable editingTable = null;
		private int editingRow = -1;
		private int editingColumn = -1;

		IsKeyEditor(JCheckBox box) {
			super(box);
			checkBox = box;
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			panel.add(checkBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 0, 0));
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			editingTable = table;
			editingRow = row;
			editingColumn = column;
			checkBox.setSelected(false);
			if (value != null && value instanceof String) {
				if (value.toString().equalsIgnoreCase("true")) {
					checkBox.setSelected(true);
				}
			}
			return panel;
		}

		public Object getCellEditorValue() {
			boolean bool = checkBox.isSelected();
			System.out.println("单元格返回值==" + bool);
			if (editingTable != null) {
				if ((editingRow >= 0 && editingRow < editingTable.getRowCount())
						&& (editingColumn >= 0 && editingColumn < editingTable
								.getColumnCount())) {
					editingTable.setValueAt(String.valueOf(bool), editingRow,
							editingColumn);
				}
			}
			return String.valueOf(bool);
		}

		public int getClickCountToStart() {
			return 1;
		}
	}

	class TextRender extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable arg0,
				Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			JTextPane textPanel = new JTextPane();
			JScrollPane js = new JScrollPane(textPanel,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			panel.add(js, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
					GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			if (arg1 != null && arg1 instanceof String) {
				textPanel.setText(arg1.toString());
			}

			if (arg2) {
				panel.setForeground(arg0.getSelectionForeground());
				panel.setBackground(arg0.getSelectionBackground());
				textPanel.setForeground(arg0.getSelectionForeground());
				textPanel.setBackground(arg0.getSelectionBackground());
			} else {
				panel.setForeground(arg0.getForeground());
				panel.setBackground(arg0.getBackground());
				textPanel.setForeground(arg0.getForeground());
				textPanel.setBackground(arg0.getBackground());
			}

			return panel;
		}
	}

	class TextEditor extends DefaultCellEditor {
		private JPanel panel = new JPanel();
		private JTextPane textPanel = new JTextPane();
		private JTable editingTable = null;
		private int editingRow = -1;
		private int editingColumn = -1;

		TextEditor(JCheckBox box) {
			super(box);
			panel.setLayout(new GridBagLayout());
			JScrollPane js = new JScrollPane(textPanel,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			panel.add(js, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
					GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			editingTable = table;
			editingRow = row;
			editingColumn = column;
			if (value != null && value instanceof String) {
				textPanel.setText(value.toString());
			}
			return panel;
		}

		public Object getCellEditorValue() {
			String text = textPanel.getText();
			if (text == null)
				text = "";
			if (editingTable != null) {
				if ((editingRow >= 0 && editingRow < editingTable.getRowCount())
						&& (editingColumn >= 0 && editingColumn < editingTable
								.getColumnCount())) {
					editingTable.setValueAt(text, editingRow, editingColumn);
				}
			}
			return text;
		}

		public int getClickCountToStart() {
			return 1;
		}
	}

	public void stopTableCellEditing() {
		if (table.getCellEditor() != null)
			table.getCellEditor().stopCellEditing();
	}

    public void run() {
        final VaActionProgressBar progressBar = new VaActionProgressBar(
                parent, "批量上载工艺", "正在批量上载工艺,请等待...", "请勿关闭,正在批量上载工艺....");
        progressBar.setAlwaysOnTop(true);
        try{
            Thread thread = new Thread() {
                public void run() {
                    result = JOptionPane.YES_OPTION;
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if (i==table.getRowCount()-1) {
                            flag=true;
                        }
                        String isUp = (String) table.getValueAt(i, 0);
                        if (isUp != null) {
                            if (isUp.equalsIgnoreCase("true")) {
                                String technicsName = (String) table.getValueAt(i, 1);
                                if (technicsName == null) {
                                    technicsName = "";
                                }
                                String technicsNumber = (String) table.getValueAt(i, 4);
                                if (technicsNumber == null){
                                    technicsNumber = "";
                                }

                                Document doc = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(technicsNumber);
                        		Element technicsElement = XmlUtility.getTechnicsElement(doc);
                        		String nowTechnicsName = XmlUtility.getAttributeValue(technicsElement, "technicsName");

                                String checkMsg = TechnicsUtil.checkTechnics(parent,technicsElement);
                                if(!"".equals(checkMsg)){
									result = JOptionPane.NO_OPTION;
                                    progressBar.finish();
                                    progressBar.setVisible(false);
                                    JOptionPane.showMessageDialog(null,  checkMsg);
                                    break;
                                }
                                String  stepNumber = TechnicsUtil.checkBeforeStep(parent, technicsElement);
                                if (!"".equals(stepNumber)) {
									result = JOptionPane.NO_OPTION;
                                    progressBar.finish();
                                    progressBar.setVisible(false);
                                    JOptionPane.showMessageDialog(null, nowTechnicsName+"的"+stepNumber + "工序存在不合理前置工序！");
                                    break;
                                }

                                String checkBsoID = TechnicsUtil.checkBsoID(technicsElement);
                                if (!"".equals(checkBsoID)) {
                                    progressBar.finish();
                                    progressBar.setVisible(false);
                                    if("duplicateBsoIds".equals(checkBsoID)){
                                        JOptionPane.showMessageDialog(null, "存在重复的BsoId工序，系统已自动为您重置重复的ID，但需要重新生成工艺流程图。操作指示：工艺路线页签-工艺流程图-刷新按钮");
                                    }else{
                                        JOptionPane.showMessageDialog(null, "有已删除的工序，需要重新生成工艺流程图。操作指示：工艺路线页签-工艺流程图-刷新按钮");
                                    }
                                    break;
                                }

                                boolean canTransPdf = TechnicsUtil.checkWordTransPDF(technicsElement);
                                if(!canTransPdf){
									result = JOptionPane.NO_OPTION;
									progressBar.finish();
									progressBar.setVisible(false);
									JOptionPane.showMessageDialog(null,  "Word转换PDF失败，请尝试通过以下方式解决!\n" +
											"1、请检查确认jacob插件是否安装;\n" +
											"2、若1已安装，请尝试升级本地Word至Word2010;\n" +
											"3、若以上都不行，请联系管理员！");
									break;
								}
								String technicsCategory = (String) table.getValueAt(i, 5);
                                if (technicsCategory == null){
                                    technicsCategory = "";
                                }
                                String version = (String) table.getValueAt(i, 2);
                                if (version == null){
                                    version = "";
                                }
                                String content = (String) table.getValueAt(i, 3);
                                if (content == null){
                                    content = "";
                                }

                                boolean bool = technicsUpload(technicsNumber, technicsName, technicsCategory, version, content);
                                if (!bool) {
                                    result = JOptionPane.NO_OPTION;
                                    JOptionPane.showMessageDialog(parent, technicsNumber + technicsName + "提交过程中出现错误！", "提示",
                                            JOptionPane.INFORMATION_MESSAGE);
                                    break;
                                }

                                Vector<UploadTechnics> editTechnics = NewTechnicsPart.editTechnics;
                                if (editTechnics != null) {
                                    for (UploadTechnics temp : editTechnics) {
                                        if (temp.getTechnicsNumber().equals(technicsNumber)) {
                                            NewTechnicsPart.editTechnics.remove(temp);
                                            break;
                                        }
                                    }
                                }
                            } else {
                                continue;
                            }
                        }
                        boolean b = box.isSelected();
                        table.setValueAt(String.valueOf(b), i, 0);
                    }
                    if (flag) {
                        dispose();
                        progressBar.finish();
                        progressBar.setVisible(false);
                        flag=false;
                    }
                }
                };
                thread.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}