package com.glaway.mpm.view;

import com.glaway.mpm.util.FileChooserTool;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.TemplateIntf;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import wt.doc.WTDocument;
import wt.part.WTPart;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.List;
import java.util.Vector;

public class LargeFileJPanel extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = -6434623637871698802L;
	private Container parentPanel;
	JPanel panel = new JPanel();

	private DefaultTableModel tableModel = new DefaultTableModel() {
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	private JTable table = new JTable(tableModel);
	/** 预留，可能会用到 */
	// private JButton upJButton = new IconButton("/images/button_upmove.png",
	// "上移");
	// private JButton downJButton = new
	// IconButton("/images/button_downmove.png", "下移");
	private JButton addJButton = new IconButton("/images/button_add_local_pic.png", "添加");

	private JButton associateExists = new IconButton("/images/button_add_local_pic.png", "关联已有");

	private JButton downloadButton = new IconButton("/images/button_add_local_pic.png", "下载");

	private JButton deleteJButton = new IconButton("/images/button_remove.png", "移除");

	private static String imagePath = "";

	private JFrame frame;

	private JPanel thisPanel;

	public LargeFileJPanel(Container parentPanel, JFrame frame) {
		NewTechnicsPart.startAnimFrame.setHeaderMessage("加载简图面板");
		this.parentPanel = parentPanel;
		this.frame = frame;
		this.thisPanel = this;
		jbInit();
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成加载简图面板");
	}

	String startType = com.glaway.mpm.EditorConfig.startType;

	public void setTabTitle() {
		int i = tableModel.getRowCount();
		if ("SOP".equals(startType)) {
			if (i > 0) {
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(9, "视频类大文件" + "(" + i + ")");
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					// ((TechnicsPaceJDialog)
					// parentPanel).getTabbedPane().setTitleAt(4, "视频类大文件" + "(" + i
					// + ")");
				}
			} else {
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(9, "视频类大文件");
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					// ((TechnicsPaceJDialog)
					// parentPanel).getTabbedPane().setTitleAt(4, "视频类大文件");
				}
			}
		}else{
			if (i > 0) {
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(11, "视频类大文件" + "(" + i + ")");
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					// ((TechnicsPaceJDialog)
					// parentPanel).getTabbedPane().setTitleAt(4, "视频类大文件" + "(" + i
					// + ")");
				}
			} else {
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(11, "视频类大文件");
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					// ((TechnicsPaceJDialog)
					// parentPanel).getTabbedPane().setTitleAt(4, "视频类大文件");
				}
			}
		}
	}

	private void jbInit() {

		addJButton.setMaximumSize(new Dimension(130, 23));
		addJButton.setMinimumSize(new Dimension(130, 23));
		addJButton.setPreferredSize(new Dimension(130, 23));
		addJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				addLargeFile();

			}
		});

		associateExists.setMaximumSize(new Dimension(130, 23));
		associateExists.setMinimumSize(new Dimension(130, 23));
		associateExists.setPreferredSize(new Dimension(130, 23));
		associateExists.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Element stepElement = null;
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					stepElement = ((TechnicsStepJPanel_XW) parentPanel).stepElement;
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					stepElement = ((TechnicsPaceJDialog) parentPanel).stepElement;
				}
				LargeFileSearchDialog dialog = new LargeFileSearchDialog((NewTechnicsPart) frame, thisPanel, stepElement);
				dialog.showDialog();
			}
		});

		downloadButton.setMaximumSize(new Dimension(130, 23));
		downloadButton.setMinimumSize(new Dimension(130, 23));
		downloadButton.setPreferredSize(new Dimension(130, 23));
		downloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int row = table.getSelectedRow();
					String number = (String) table.getValueAt(row, 2);
					String fileName = (String) table.getValueAt(row, 1);
					String type = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
					// File file = FileChooserTool.getSaveFile(type,
					// parentPanel);
					File file = FileChooserTool.getSaveFile(new JFileChooser(), type, fileName, parentPanel);
					if (file != null) {
						if (file.isFile() && file.exists()) {
							if (!file.renameTo(file)) {
								JOptionPane.showMessageDialog(parentPanel, "另一个程序正在使用此文件！", "提示", 1);
								return;
							}
						}
						String path = file.getPath();
						if (!path.endsWith("." + type)) {
							path = path.concat("." + type);
						}
						downloadLargeFile(number, path);
						JOptionPane.showMessageDialog(parentPanel, "下载成功！", "提示", 1);
					}
				} catch (Exception exc) {
					exc.printStackTrace();
					JOptionPane.showMessageDialog(parentPanel, "下载过程出错！", "提示", 1);
				}
			}

		});

		deleteJButton.setMaximumSize(new Dimension(130, 23));
		deleteJButton.setMinimumSize(new Dimension(130, 23));
		deleteJButton.setPreferredSize(new Dimension(130, 23));
		deleteJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteFile();
			}
		});

		panel.setLayout(new GridBagLayout());

		panel.add(addJButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));

		panel.add(associateExists, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0,
				0));
		panel.add(downloadButton,
				new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));

		panel.add(deleteJButton, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));

		setLayout(new GridBagLayout());

		tableModel.addColumn("序号");
		tableModel.addColumn("文件名称");
		tableModel.addColumn("文件编号");

		table.getColumnModel().getColumn(0).setMaxWidth(500);
		setHideColumn(2);

		table.setRowHeight(25);
		table.setSelectionMode(2);
		table.getTableHeader().setReorderingAllowed(false);
		table.setGridColor(Color.GRAY);
		DefaultTableCellRenderer r = new DefaultTableCellRenderer();
		r.setHorizontalAlignment(JTextField.CENTER);
		table.getColumn("序号").setCellRenderer(r);
		JScrollPane pane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane.getViewport().setBackground(Color.white);
		add(pane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(panel, new GridBagConstraints(1, 0, 1, 1, 0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		// 表格监听
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
				}
				if (e.getClickCount() == 1 || e.getClickCount() == 2) {

				}
			}

			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {

				}
			}
		});
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
			String docNumber = element.attributeValue("docNumber");
			String fileName = element.attributeValue("fileName");
			tableModel.setValueAt(i + 1, i, 0);
			tableModel.setValueAt(fileName, i, 1);
			tableModel.setValueAt(docNumber, i, 2);
		}
		setTabTitle();
	}

	private void setHideColumn(int index) {
		table.getColumnModel().getColumn(index).setMinWidth(0);
		table.getColumnModel().getColumn(index).setMaxWidth(0);
	}

	private void addLargeFile() {
		// File file = FileChooserTool.getFile(suffix, imagePath, parentPanel);
		final File file = FileChooserTool.getFile(parentPanel);
		if (file == null)
			return;
		imagePath = file.getAbsolutePath();
		final String fileName = file.getName();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String name = tableModel.getValueAt(i, 1).toString();
			if (fileName.equals(name.substring(name.lastIndexOf("/") + 1, name.length()))) {
				JOptionPane.showMessageDialog(null, "相同的文件名，添加失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}

		final VaActionProgressBar progressBar = new VaActionProgressBar(frame, "文件上传", "大文件上传中", "");
		Thread thread = new Thread() {
			public void run() {

				// 上传至PDM
				String largeFileNumber = uploadLargeFile(file, null, fileName);
				// 将数据写入表格
				addToTable(fileName, largeFileNumber);
				// 设置Tab页
				setTabTitle();
				// 保存至XML
				saveToXml(largeFileNumber, fileName);
				progressBar.finish();
				progressBar.setVisible(false);
			}
		};
		thread.start();
		progressBar.setVisible(true);
		// progressBar.finish();
		// progressBar.setVisible(false);
	}

	public Vector<Element> getElements() {
		Vector<Element> elements = new Vector<Element>();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if (isRowNull(i)) {
				continue;
			}
			Element element = DocumentHelper.createElement("LargeFile");
			element.setAttributeValue("docNumber", "");
			element.setAttributeValue("fileName", "");

			XmlUtility.setAttributeValue(element, "fileName", (String) tableModel.getValueAt(i, 1));
			XmlUtility.setAttributeValue(element, "docNumber", (String) tableModel.getValueAt(i, 2));
			elements.add(element);
		}
		return elements;
	}

	public void saveToXml(String largeFileNumber, String fileName) {
		Element techele = this.getTechnicElment();
		String technicsNumber = techele.attributeValue("technicsNumber");
		Element stepElement = ((TechnicsStepJPanel_XW) parentPanel).stepElement;
		Element largeFilesElement = stepElement.element("LargeFiles");
		if (largeFilesElement == null) {
			largeFilesElement = stepElement.addElement("LargeFiles");
		}
		Element largeFileElement = largeFilesElement.addElement("LargeFile");
		largeFileElement.addAttribute("docNumber", largeFileNumber);
		largeFileElement.addAttribute("fileName", fileName);
		String technicsFilePath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
		saveDocument(stepElement.getDocument(), new File(technicsFilePath));
	}

	public static void saveDocument(Document document, File file) {
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
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void downloadLargeFile(String number, String path) {
		FileOutputStream fos = null;
		byte[] bytes = TemplateIntf.downloadLargeFile(number);
		try {
			fos = new FileOutputStream(path);
			fos.write(bytes);
			fos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private String uploadLargeFile(File largeFile, String largeFileNumber, String largeFileName) {
		Element techele = this.getTechnicElment();
		byte[] bytes = null;
		InputStream is = null;
		String docNumber = "";
		try {
			is = new FileInputStream(largeFile);
			bytes = IOUtils.toByteArray(is);
			String partNumber = XmlUtility.getAttributeValue(techele, "partNumber");
			WTPart part = TemplateIntf.getLatestParttByNumber(partNumber);
			WTDocument doc = TemplateIntf.uploadLargeFileDoc(part.getContainer(), largeFileNumber, largeFileName, bytes);
			if (doc != null) {
				docNumber = doc.getNumber();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return docNumber;
	}

	public void addToTable(String fileName, String largeFileNumber) {

		int row;
		addOneRow();
		row = table.getRowCount() - 1;
		tableModel.setValueAt(row + 1, row, 0);
		tableModel.setValueAt(fileName, row, 1);
		tableModel.setValueAt(largeFileNumber, row, 2);
	}

	private void addOneRow() {
		Vector vector = new Vector();
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			vector.add("");
		}
		tableModel.addRow(vector);
	}

	private void deleteFile() {
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
		Element techele = null;
		Element stepElement = null;
		try {
			if (parentPanel instanceof TechnicsStepJPanel_XW) {
				techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) ((TechnicsStepJPanel_XW) parentPanel).getFrame()).getCurrentTechnics());
				stepElement = ((TechnicsStepJPanel_XW) parentPanel).stepElement;
			} else if (parentPanel instanceof TechnicsPaceJDialog) {
				techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) ((TechnicsPaceJDialog) parentPanel).getFrame()).getCurrentTechnics());
				stepElement = ((TechnicsPaceJDialog) parentPanel).stepElement;
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
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
		for (int i = 0; i < rows.length; i++) {
			String docNumber = tableModel.getValueAt(rows[i], 2).toString();
			Element largeFile = stepElement.element("LargeFiles");
			List<Element> largeFileElements = largeFile.elements();
			for (Element largeFileElement : largeFileElements) {
				String number = largeFileElement.attributeValue("docNumber");
				if (number.equals(docNumber)) {
					// 删除xml中内容
					stepElement.remove(largeFileElement);
					// 表格删除一行
					tableModel.removeRow(rows[i]);
					// PDM中对应文件删除
					// TemplateIntf.deleteLargeFileDoc(docNumber);
				}
			}
		}
		// String filePath = technicsPath + File.separator + technicsNumber +
		// ".xml";
		// //保存更新xml
		// saveDocument(stepElement.getDocument(), new File(filePath));
		((NewTechnicsPart) frame).saveProcess(techele);
		setTabTitle();
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

	public void setUIEnabled(boolean b) {
		table.setEnabled(b);
		addJButton.setEnabled(b);
		deleteJButton.setEnabled(b);
		associateExists.setEnabled(b);
		downloadButton.setEnabled(b);
		// openJButton.setEnabled(b);
		// upJButton.setEnabled(b);
		// downJButton.setEnabled(b);
	}

	public Element getTechnicElment() {
		Element techele = null;
		if (parentPanel instanceof TechnicsStepJPanel_XW) {
			techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) ((TechnicsStepJPanel_XW) parentPanel).getFrame()).getCurrentTechnics());
		} else if (parentPanel instanceof TechnicsPaceJDialog) {
			techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) ((TechnicsPaceJDialog) parentPanel).getFrame()).getCurrentTechnics());
		}
		return techele;
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

	private void openFile() {
		Element techele = this.getTechnicElment();
		try {
			if (techele == null)
				return;
			String technicsCategory = techele.attributeValue("technicsCategory");
			String technicsNumber = techele.attributeValue("technicsNumber");
			String technicsName = techele.attributeValue("technicsName");
			String filePath;
			if ("rework".equals(technicsCategory)) {
				filePath = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
			} else if ("temp".equals(technicsCategory)) {
				filePath = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
			} else {
				filePath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
				if (parentPanel instanceof NewTechnicsHistoryView) {
					filePath = WorkSpaceUtil.getTempRootPath() + File.separator + technicsNumber;
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
				Runtime.getRuntime().exec("rundll32.exe url.dll,FileProtocolHandler  " + vector.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void moveUpProcess() {
		// stopTableCellEditing();
		changeRowValue(true);
	}

	private void moveDownProcess() {
		// stopTableCellEditing();
		changeRowValue(false);
	}

	public JTable getTable() {
		return table;
	}
}