package com.glaway.mpm.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.sop.intf.SopIntf;
import com.glaway.mpm.util.*;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class NewAttachJPanel extends JPanel {
	private Container parentPanel;

	JPanel panel = new JPanel();

	private DefaultTableModel tableModel = new DefaultTableModel() {
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	private JTable table = new JTable(tableModel);

	private JButton addJButton = new IconButton("/images/button_add.png", "添加附件");
	private JButton deleteJButton = new IconButton("/images/button_remove.png", "移除附件");
	private JButton openJButton = new IconButton("/images/button_open.png", "打开附件");
	private JButton uploadButton = new IconButton("/images/button_open.png", "上传附件");

	private static final String suffix = "doc,docx,xlsx,xls";

	private static String imagePath = "";

	public NewAttachJPanel(Container parentPanel) {
		NewTechnicsPart.startAnimFrame.setHeaderMessage("加载附件面板");
		this.parentPanel = parentPanel;
		jbInit();
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成加载附件面板");
	}
	String startType = com.glaway.mpm.EditorConfig.startType;
	public void setTabTitle() {
		int i = tableModel.getRowCount();
		if("SOP".equals(startType)){
			if (i > 0) {
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(7, "附件" + "(" + i + ")");
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(4, "附件" + "(" + i + ")");
				} else if (parentPanel instanceof TechnicsStepJPanel_View) {
					((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(7, "附件" + "(" + i + ")");
				}
			} else {
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(7, "附件");
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(4, "附件");
				} else if (parentPanel instanceof TechnicsStepJPanel_View) {
					((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(7, "附件");
				}
			}
		}else{
			if (i > 0) {
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(8, "附件" + "(" + i + ")");
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(4, "附件" + "(" + i + ")");
				} else if (parentPanel instanceof TechnicsStepJPanel_View) {
					((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(8, "附件" + "(" + i + ")");
				}
			} else {
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(8, "附件");
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(4, "附件");
				} else if (parentPanel instanceof TechnicsStepJPanel_View) {
					((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(8, "附件");
				}
			}
		}
	}

	private void jbInit() {
		addJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addAttach();
			}
		});

		deleteJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteAttach();
			}
		});
		openJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int j = table.getSelectedRowCount();
				if (j == 0) {
					JOptionPane.showMessageDialog(null, "请选择需要打开的附件！", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				openFile();
			}
		});
		uploadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				uploadAttach();
			}
		});

		panel.setLayout(new GridBagLayout());
		panel.add(addJButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		panel.add(deleteJButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		panel.add(openJButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		panel.add(uploadButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 0, 5), 0, 0));

		setLayout(new GridBagLayout());
		tableModel.addColumn("附件名称");
		tableModel.addColumn("附件类型");
		tableModel.addColumn("附件大小");
		tableModel.addColumn("附件路径");
		setHideColumn(3);
		table.setRowHeight(25);
		table.setSelectionMode(2);
		table.getTableHeader().setReorderingAllowed(false);

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(table.getTableHeader(), BorderLayout.PAGE_START);
		p.add(table, BorderLayout.CENTER);

		JScrollPane pane = new JScrollPane(table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);


		if(parentPanel instanceof TechnicsStepJPanel_XW || parentPanel instanceof TechnicsPaceJDialog){
			setLayout(new GridBagLayout());
			add(pane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
							0, 0, 0, 0), 0, 0));
			add(panel, new GridBagConstraints(1, 0, 1, 1, 0, 1.0,
					GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(
							0, 0, 0, 0), 0, 0));
		}else{
			JPanel leftPanel = new JPanel();
			JPanel totalPanel = new JPanel();
			GroupLayout jPanel2Layout = new GroupLayout(panel);
			panel.setLayout(jPanel2Layout);
	        jPanel2Layout.setHorizontalGroup(
	            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel2Layout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
	                    .addComponent(addJButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(deleteJButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(openJButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(uploadButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						)
	                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        );
	        jPanel2Layout.setVerticalGroup(
	            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel2Layout.createSequentialGroup()
	                .addGap(26, 26, 26)
	                .addComponent(addJButton)
	                .addGap(26, 26, 26)
	                .addComponent(deleteJButton)
	                .addGap(28, 28, 28)
	                .addComponent(openJButton)
					.addGap(28, 28, 28)
					.addComponent(uploadButton)
	                .addGap(26, 26, 26)
	                .addContainerGap(225, Short.MAX_VALUE))
	        );
	        GroupLayout jPanel1Layout = new GroupLayout(leftPanel);
	        leftPanel.setLayout(jPanel1Layout);
	        jPanel1Layout.setHorizontalGroup(
	            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addComponent(pane, GroupLayout.PREFERRED_SIZE, 766, GroupLayout.PREFERRED_SIZE)
	        );
	        jPanel1Layout.setVerticalGroup(
	            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addComponent(pane, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
	        );

	        GroupLayout jPanel3Layout = new GroupLayout(totalPanel);
	        totalPanel.setLayout(jPanel3Layout);
	        jPanel3Layout.setHorizontalGroup(
	            jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGap(0, 917, Short.MAX_VALUE)
	            .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	                .addGroup(jPanel3Layout.createSequentialGroup()
	                    .addContainerGap()
	                    .addComponent(leftPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                    .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                    .addContainerGap()))
	        );
	        jPanel3Layout.setVerticalGroup(
	            jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGap(0, 407, Short.MAX_VALUE)
	            .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	                .addGroup(jPanel3Layout.createSequentialGroup()
	                    .addContainerGap()
	                    .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	                        .addComponent(leftPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                        .addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
	                    .addContainerGap()))
	        );

	        GroupLayout layout = new GroupLayout(this);
	        this.setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addGap(0, 0, 0)
	                .addComponent(totalPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addGap(0, 0, 0)
	                .addComponent(totalPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        );
		}
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = table.rowAtPoint(e.getPoint());
					if (row != -1) {
						openFile();
					}
				}
			}
		});
	}

	/**
	 * 方法功能: 上传附件，仅供SOP工艺使用
	 *
	 * @param
	 * @return void
	 * @author LB
	 * @date 2019/12/25
	 */
	private void uploadAttach() {
		Element techele = null;
		if (parentPanel instanceof TechnicsStepJPanel_XW) {
			techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) ((TechnicsStepJPanel_XW) parentPanel).getFrame()).getCurrentTechnics());
		} else if (parentPanel instanceof NewTechnicsPart) {
			techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) parentPanel).getCurrentTechnics());
		}
		if (techele == null) {
			return;
		}
		String technicsNumber = techele.attributeValue("technicsNumber");
		String technicsVersion = techele.attributeValue("version");
		boolean isBiaoShenZhe = SopIntf.checkIsBiaoShenZhe(technicsNumber, technicsVersion, NewTechnicsPart.currentUser);
		if(isBiaoShenZhe){
			try {
				String dirPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
				Vector allTechnicsDrawings = getAllTechnicsDrawings(techele);
				TechHelper.deleteAttachForSOP(technicsNumber);
				for (int i = 0; i < allTechnicsDrawings.size(); i++) {
					Element element = (Element) allTechnicsDrawings.get(i);
					String reviewPath = dirPath + File.separator + element.attributeValue("absolutePath");
					String reviewName = element.attributeValue("attachName")+"."+element.attributeValue("attachType");
					File file = new File(reviewPath);
					TechHelper.uploadAttachForSOP(technicsNumber,reviewName, file);
					if(parentPanel instanceof NewTechnicsPart){
						((NewTechnicsPart) parentPanel).saveProcess(element);
					}
				}

				boolean flag = getTechnicsByte(technicsNumber, technicsVersion, dirPath);
				if (flag) {
					JOptionPane.showMessageDialog(parentPanel, "上传成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(parentPanel, "上传失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			JOptionPane.showMessageDialog(parentPanel, "上传失败！您不是当前工艺所属流程的标审者或当前流程不在标审环节，无法上传附件！", "提示", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public static boolean getTechnicsByte(String technicsNumber, String version, String dirPath) throws Exception {
		File file = new File(dirPath);
		String tempFile = System.getenv("TEMP") + File.separator + System.currentTimeMillis() + ".zip";
		ApacheZipUtil.compress(file, tempFile);
		byte[] bytes = FileUtil.readFilePathToByte(tempFile);
		return TechnicsIntf.uploadPrimaryOfDocument(bytes, technicsNumber, version);
	}

	public static void main(String[] args) {
		String path = "D:\\mpm\\technics\\GB_T13806_2_A-ST2_2X9_5`自攻螺钉 ST2.2×9零件工艺`零件工艺`GB_T13806_2_A-ST2_2X9_5`多基地面雷达\\attachs\\20130125065422355\\deleteLog.log";
		try {
			Runtime.getRuntime().exec(
					"rundll32 url.dll FileProtocolHandler   " + path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openFile() {
		Element techele = null;
		try {
			if (parentPanel instanceof TechnicsStepJPanel_XW) {
				techele = XmlUtility
						.getTechnicsElement(((NewTechnicsPart) ((TechnicsStepJPanel_XW) parentPanel)
								.getFrame()).getCurrentTechnics());
			} else if(parentPanel instanceof NewTechnicsPart){
				techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) parentPanel).getCurrentTechnics());
			}
			if (techele == null){
				return;
			}
			Document doc = techele.getDocument();
			Element ele = XmlUtility.getTechnicsElement(doc);
			String technicsCategory = ele.attributeValue("technicsCategory");
			String technicsNumber = ele.attributeValue("technicsNumber");
			String technicsName = ele.attributeValue("technicsName");
			String filePath;
			if ("rework".equals(technicsCategory)) {
				filePath = WorkSpaceUtil.getReworkTechnicsDirectory(
						technicsNumber, technicsName);
			} else if ("temp".equals(technicsCategory)) {
				filePath = WorkSpaceUtil.getTempTechnicsDirectory(
						technicsNumber, technicsName);
			} else {
				filePath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
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
					path = filePath + "/" + attachPath;
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

	public Vector<Element> getElements() {
		Vector<Element> elements = new Vector<Element>();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if (isRowNull(i))
				continue;
			Element element = DocumentHelper.createElement("PAttachInfo");
			element.setAttributeValue("bsoID", "");
			element.setAttributeValue("attachName", "");
			element.setAttributeValue("attachType", "");
			element.setAttributeValue("attachSize", "");
			element.setAttributeValue("absolutePath", "");

			XmlUtility.setAttributeValue(element, "bsoID", "");
			XmlUtility.setAttributeValue(element, "attachName",
					(String) tableModel.getValueAt(i, 0));
			XmlUtility.setAttributeValue(element, "attachType",
					(String) tableModel.getValueAt(i, 1));
			XmlUtility.setAttributeValue(element, "attachSize",
					(String) tableModel.getValueAt(i, 2));
			XmlUtility.setAttributeValue(element, "absolutePath",
					(String) tableModel.getValueAt(i, 3));
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
			String drawingName = element.attributeValue("attachName");
			String drawingType = element.attributeValue("attachType");
			tableModel.setValueAt(drawingName, i, 0);
			tableModel.setValueAt(drawingType, i, 1);
			tableModel.setValueAt(element.attributeValue("attachSize"), i, 2);
			tableModel.setValueAt(element.attributeValue("absolutePath"), i, 3);
		}
	}

	private void setHideColumn(int index) {
		table.getColumnModel().getColumn(index).setMinWidth(0);
		table.getColumnModel().getColumn(index).setMaxWidth(0);
	}

	public boolean judgeFileType(String name, String suffix) {
		if (name == null) {
			return false;
		}
		for (String str : suffix.split(",")) {
			if (name.endsWith(str)) {
				return true;
			} else {
				continue;
			}
		}
		return false;
	}

	private File getAttachFile() {
		File file = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogType(0);
		chooser.setFileSelectionMode(0);
		chooser.setSelectedFile(new File(imagePath));
		int returnVal = chooser.showOpenDialog(parentPanel);
		if (returnVal == 0) {
			file = chooser.getSelectedFile();
		}
		return file;
	}

	private void addAttach() {
		// File file = FileDialogTool.getFile(suffix, imagePath, parentPanel);
		File file = getAttachFile();
		System.out.println("file=" + file);
		if (file == null) {
			return;
		}
		// if (!judgeFileType(file.getName(), suffix)) {
		// JOptionPane.showMessageDialog(null, "请选择正确的文件类型！", "提示",
		// JOptionPane.INFORMATION_MESSAGE);
		// return;
		// }
		imagePath = file.getAbsolutePath();
		System.out.println("imagePath=" + imagePath);
		String fileName = file.getName();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String name = tableModel.getValueAt(i, 3).toString();
			System.out.println("name=" + name);
			if (fileName.equals(name.substring(name.lastIndexOf("/") + 1, name.length()))) {
				JOptionPane.showMessageDialog(null, "相同的文件名，添加附件失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		long length = file.length();
		if(length/1048576 > 10){
			JOptionPane.showMessageDialog(null, "附件大于10M，请将该附件添加至视频类大文件！", "提示", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		String fn = UUID.randomUUID()+"";
		fn = fn.replaceAll("-", "");

		String fn2 = fn + "." + file.getName().split("\\.")[1];
		String timeFolder = new SimpleDateFormat("yyyyMMddhhmmssSSS").format(new Date());
		String path = "attachs/" + timeFolder + "/" + fn2;
		System.out.println("path=" + path);
		addToTable(file, path);
		setTabTitle();
		saveDrawing();
		try {
			writePicture(imagePath, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writePicture(String sourcepath, String path) throws Exception {
		Element techele = null;
		if (parentPanel instanceof TechnicsStepJPanel_XW) {
			techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) ((TechnicsStepJPanel_XW) parentPanel)
							.getFrame()).getCurrentTechnics());
		} else if (parentPanel instanceof TechnicsPaceJDialog) {
			techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) ((TechnicsPaceJDialog) parentPanel)
							.getFrame()).getCurrentTechnics());
		} else if(parentPanel instanceof NewTechnicsPart){
			techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) parentPanel).getCurrentTechnics());
		}
		String technicsCategory = techele.attributeValue("technicsCategory");
		String technicsName = techele.attributeValue("technicsName");
		String technicsNumber = techele.attributeValue("technicsNumber");
		Vector vec = null;
		if(parentPanel instanceof NewTechnicsPart){
			vec = getAllTechnicsDrawings(techele);
		}else{
			vec = getAllDrawings(techele);
		}
		System.out.println("vec==" + vec);
		for (int i = 0; i < vec.size(); i++) {
			Element element = (Element) vec.get(i);
			XmlUtility.writeDrawingFiles(element, technicsCategory, technicsNumber, technicsName, sourcepath, false);
			if(parentPanel instanceof NewTechnicsPart){
				((NewTechnicsPart) parentPanel).saveProcess(element);
			}
		}
	}

	public static Vector getAllTechnicsDrawings(Element techElement) {
		Vector vec = new Vector();
		addDrawings(gatDrawings(techElement), vec);
		return vec;
	}

	public static Vector getAllDrawings(Element techElement) {
		Vector vec = new Vector();
		List stepList = XmlUtility.getAllSteps(techElement);
		for (Iterator it = stepList.iterator(); it.hasNext();) {
			Element stepElement = (Element) it.next();
			addDrawings(gatDrawings(stepElement), vec);
			List paceList = XmlUtility.getAllPaces(stepElement);
			for (Iterator ite = paceList.iterator(); ite.hasNext();) {
				Element paceElement = (Element) ite.next();
				addDrawings(gatDrawings(paceElement), vec);
			}
		}
		return vec;
	}

	private static void addDrawings(List list, Vector vector) {
		if (list != null) {
			for (Iterator it = list.iterator(); it.hasNext();) {
				Element drawingElement = (Element) it.next();
				if (drawingElement != null) {
					if (!drawingElement.attributeValue("absolutePath").equals(""))
						vector.add(drawingElement);
				}
			}
		}
	}

	private static List gatDrawings(Element procedureElement) {
		Element drawingElement = procedureElement.element("attachs");
		return drawingElement == null ? null : drawingElement.elements();
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
		tableModel.setValueAt(name, rowCount - 1, 0);
		tableModel.setValueAt(type, rowCount - 1, 1);
		tableModel.setValueAt(getFileSize(size), rowCount - 1, 2);
		tableModel.setValueAt(path, rowCount - 1, 3);
	}

	private void addOneRow() {
		Vector vector = new Vector();
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			vector.add("");
		}
		tableModel.addRow(vector);
	}

	private String getFileSize(float size) {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		if (size / 1024 > 1024) {
			return String.valueOf(nf.format(size / (1024 * 1024))) + "MB";
		} else {
			return String.valueOf(nf.format(size / 1024)) + "KB";
		}
	}

	private void deleteAttach() {
		int[] rows = table.getSelectedRows();
		if (rows.length == 0)
			return;
		String technicsPath = null;
		Element techele = null;
		try {
			if (parentPanel instanceof TechnicsStepJPanel_XW) {
				techele = XmlUtility
						.getTechnicsElement(((NewTechnicsPart) ((TechnicsStepJPanel_XW) parentPanel)
								.getFrame()).getCurrentTechnics());
			} else if (parentPanel instanceof TechnicsPaceJDialog) {
				techele = XmlUtility
						.getTechnicsElement(((NewTechnicsPart) ((TechnicsPaceJDialog) parentPanel)
								.getFrame()).getCurrentTechnics());
			} else if(parentPanel instanceof NewTechnicsPart){
				techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) parentPanel).getCurrentTechnics());
			}
			String technicsCategory = techele
					.attributeValue("technicsCategory");
			String technicsNumber = techele.attributeValue("technicsNumber");
			String technicsName = techele.attributeValue("technicsName");
			if ("rework".equals(technicsCategory)) {
				technicsPath = WorkSpaceUtil.getReworkTechnicsDirectory(
						technicsNumber, technicsName);
			} else if ("temp".equals(technicsCategory)) {
				technicsPath = WorkSpaceUtil.getTempTechnicsDirectory(
						technicsNumber, technicsName);
			} else {
				technicsPath = WorkSpaceUtil
						.getTechnicsDirectory(technicsNumber);
			}
		} catch (Exception e) {

			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		for (int i = 0; i < rows.length; i++) {
			String path = tableModel.getValueAt(rows[i], 3).toString();
			File file = new File(technicsPath + "/" + path.substring(0, path.lastIndexOf("/")));
			System.out.println("file Name=" + file.getName());
			if (file.exists() && file.isDirectory()) {
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

		}
		setTabTitle();
		saveDrawing();
		if(parentPanel instanceof NewTechnicsPart){
			((NewTechnicsPart) parentPanel).saveProcess(techele);
		}
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
	}

	public void setUIEnabled(boolean b) {
		table.setEnabled(b);
		addJButton.setEnabled(b);
		deleteJButton.setEnabled(b);
		openJButton.setEnabled(b);
		if(parentPanel instanceof NewTechnicsPart){
			uploadButton.setVisible(b);
			uploadButton.setEnabled(b);
		}else{
			uploadButton.setVisible(false);
		}
	}

	private void saveDrawing() {
		if (parentPanel instanceof TechnicsStepJPanel_XW) {
			((TechnicsStepJPanel_XW) parentPanel).save(null);
		} else if (parentPanel instanceof TechnicsPaceJDialog) {
			((TechnicsPaceJDialog) parentPanel).save();
		} else if(parentPanel instanceof NewTechnicsPart){
			Document doc = ((NewTechnicsPart) parentPanel).getCurrentTechnics();
			Element element = XmlUtility.getTechnicsElement(doc);
			Element attachElement = element.element("attachs");
			if (attachElement == null) {
				attachElement = element.addElement("attachs");
			} else {
				attachElement.elements().clear();
			}
			Vector<Element> attaches = this.getElements();
			for (Iterator<Element> it = attaches.iterator(); it.hasNext();) {
				attachElement.add(it.next());
			}
		}
	}

	public void loadInitData(Element technicsElement) {
		Element attachs = XmlUtility.getAttachs(technicsElement);
		Vector<Element> attachElements = new Vector<Element>();
		if (attachs != null) {
			for (Iterator<Element> it = attachs.elementIterator("PAttachInfo"); it.hasNext();) {
				attachElements.add(it.next());
			}
			setTableValues(attachElements);
		}
	}
}