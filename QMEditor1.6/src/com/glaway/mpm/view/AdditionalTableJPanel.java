package com.glaway.mpm.view;

import java.awt.BorderLayout;
import java.awt.Container;
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
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

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
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.io.FileUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.glaway.mpm.util.ProcedurePictureCreateUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
/**
 * 附表操作
 * @author Administrator
 *
 */
public class AdditionalTableJPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Container parentPanel;

	JPanel panel = new JPanel();

	private DefaultTableModel tableModel = new DefaultTableModel() {
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private JTable table = new JTable(tableModel);

	private JButton addJButton = new IconButton("/images/button_add.png", "添加附表");

	private JButton deleteJButton = new IconButton("/images/button_remove.png", "移除附表");

	private JButton openJButton = new IconButton("/images/button_open.png", "编辑附表");

	private JButton upJButton = new IconButton("/images/button_open.png", "上移");

	private JButton downJButton = new IconButton("/images/button_open.png", "下移");

	private JButton saveJButton = new IconButton("/images/button_open.png", "保存");

	private JButton renameJButton = new IconButton("/images/button_open.png", "重命名");


	private static String imagePath = "";

	public AdditionalTableJPanel(Container parentPanel) {
		NewTechnicsPart.startAnimFrame.setHeaderMessage("加载附表面板");
		this.parentPanel = parentPanel;
		jbInit();
		setName("AdditionalTable");
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成加载附表面板");
	}
	String startType = com.glaway.mpm.EditorConfig.startType;
	public void setTabTitle() {
		int i = tableModel.getRowCount();
		if("SOP".equals(startType)){
			if (i > 0) {
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(8, "工艺附表" + "(" + i + ")");
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(0, "工步附图" + "(" + i + ")");
				} else if (parentPanel instanceof NewTechnicsPart){
					((NewTechnicsPart) parentPanel).getNewTechnicsMasterJPanel_XW().getTabbedPane().setTitleAt(4, "工艺附表" + "(" + i + ")");
				}
			} else {
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(8, "工艺附表");
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(0, "工步附图");
				} else if (parentPanel instanceof NewTechnicsPart){
					((NewTechnicsPart) parentPanel).getNewTechnicsMasterJPanel_XW().getTabbedPane().setTitleAt(4, "工艺附表");
				}
			}
		}else{
			if (i > 0) {
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(9, "工艺附表" + "(" + i + ")");
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(9, "工艺附表" + "(" + i + ")");
				} else if (parentPanel instanceof NewTechnicsPart){
					((NewTechnicsPart) parentPanel).getNewTechnicsMasterJPanel_XW().getTabbedPane().setTitleAt(2, "工艺附表" + "(" + i + ")");
				}
			} else {
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(9, "工艺附表");
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(9, "工艺附表");
				} else if (parentPanel instanceof NewTechnicsPart){
					((NewTechnicsPart) parentPanel).getNewTechnicsMasterJPanel_XW().getTabbedPane().setTitleAt(2, "工艺附表");
				}
			}
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
	private void jbInit() {
		if (parentPanel instanceof TechnicsPaceJDialog && "SOP".equals(startType)) {
			addJButton.setText("添加附图");
			deleteJButton.setText("移除附图");
			openJButton.setText("编辑附图");
		}
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
					JOptionPane.showMessageDialog(null, "请选择需要打开的附表！", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				openFile();
			}
		});
		upJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				upAttach();
				saveAttach();
			}
		});
		downJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				downAttach();
				saveAttach();
			}
		});
		saveJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAttach();
			}
		});
		renameJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renameAttach();
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
		panel.add(upJButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		panel.add(downJButton, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		/*panel.add(saveJButton, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));*/
		panel.add(renameJButton, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		if (parentPanel instanceof TechnicsPaceJDialog && "SOP".equals(startType)) {
			tableModel.addColumn("附图名称");
			tableModel.addColumn("附图类型");
			tableModel.addColumn("附图大小");
		}else{
			tableModel.addColumn("附表名称");
			tableModel.addColumn("附表类型");
			tableModel.addColumn("附表大小");
		}
		tableModel.addColumn("附表路径");
		tableModel.addColumn("附表路径mht");
		tableModel.addColumn("bsoID");

		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(1).setMaxWidth(100);
		table.getColumnModel().getColumn(1).setMinWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setMaxWidth(100);
        table.getColumnModel().getColumn(2).setMinWidth(100);

		setHideColumn(3);
		setHideColumn(4);
		setHideColumn(5);
		table.setRowHeight(25);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(table.getTableHeader(), BorderLayout.PAGE_START);
		p.add(table, BorderLayout.CENTER);
//
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
	                     .addComponent(upJButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                      .addComponent(downJButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    //.addComponent(saveJButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(renameJButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
	                .addGap(26, 26, 26)
	                .addComponent(upJButton)
	                .addGap(26, 26, 26)
	                .addComponent(downJButton)
	                .addGap(26, 26, 26)
	                //.addComponent(saveJButton)
	                //.addGap(26, 26, 26)
	                .addComponent(renameJButton)
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
			if (parentPanel instanceof NewTechnicsPart) {
				techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) parentPanel).getCurrentTechnics());
			}else if(parentPanel instanceof TechnicsStepJPanel_XW){
				TechnicsStepJPanel_XW technicsStepJPanel = (TechnicsStepJPanel_XW) parentPanel;
				NewTechnicsPart newTechnicsPart = (NewTechnicsPart) technicsStepJPanel.getFrame();
				techele = XmlUtility.getTechnicsElement(newTechnicsPart.getCurrentTechnics());
			}else if(parentPanel instanceof TechnicsPaceJDialog){
				TechnicsPaceJDialog technicsPaceJDialog = (TechnicsPaceJDialog) parentPanel;
				NewTechnicsPart newTechnicsPart = (NewTechnicsPart) technicsPaceJDialog.getFrame();
				techele = XmlUtility.getTechnicsElement(newTechnicsPart.getCurrentTechnics());
			}
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

	public Vector<Element> getElements() {
		Vector<Element> elements = new Vector<Element>();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if (isRowNull(i))
				continue;
			Element element = DocumentHelper.createElement("additionaltable");
			element.setAttributeValue("bsoID", "");
			element.setAttributeValue("attachName", "");
			element.setAttributeValue("attachType", "");
			element.setAttributeValue("attachSize", "");
			element.setAttributeValue("absolutePath", "");

			XmlUtility.setAttributeValue(element, "bsoID", (String) tableModel.getValueAt(i, 5));
			XmlUtility.setAttributeValue(element, "attachName", (String) tableModel.getValueAt(i, 0));
			XmlUtility.setAttributeValue(element, "attachType", (String) tableModel.getValueAt(i, 1));
			XmlUtility.setAttributeValue(element, "attachSize", (String) tableModel.getValueAt(i, 2));
			XmlUtility.setAttributeValue(element, "absolutePath", (String) tableModel.getValueAt(i, 3));
			XmlUtility.setAttributeValue(element, "absolutePath2", (String) tableModel.getValueAt(i, 4));
			elements.add(element);
		}
		return elements;
	}


	private Element createAdditionaltableElement(int row) {

			Element element = DocumentHelper.createElement("additionaltable");
			element.setAttributeValue("bsoID", "");
			element.setAttributeValue("attachName", "");
			element.setAttributeValue("attachType", "");
			element.setAttributeValue("attachSize", "");
			element.setAttributeValue("absolutePath", "");

			XmlUtility.setAttributeValue(element, "bsoID", (String) tableModel.getValueAt(row, 5));
			XmlUtility.setAttributeValue(element, "attachName",
					(String) tableModel.getValueAt(row, 0));
			XmlUtility.setAttributeValue(element, "attachType",
					(String) tableModel.getValueAt(row, 1));
			XmlUtility.setAttributeValue(element, "attachSize",
					(String) tableModel.getValueAt(row, 2));
			XmlUtility.setAttributeValue(element, "absolutePath",
					(String) tableModel.getValueAt(row, 3));
			XmlUtility.setAttributeValue(element, "absolutePath2",
					(String) tableModel.getValueAt(row, 4));

		return element;
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
			tableModel.setValueAt(element.attributeValue("absolutePath2"), i, 4);
			tableModel.setValueAt(element.attributeValue("bsoID"), i, 5);
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
		chooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "*.doc文件";
			}

			@Override
			public boolean accept(File f) {
				if( f.isDirectory() || f.getName().endsWith( ".doc" ) ) {
					return true;
				} else {
					return false;
				}
			}
		});



		chooser.setMultiSelectionEnabled(false);
        int state=chooser.showDialog(parentPanel,"保存工艺附表");
        if(JFileChooser.APPROVE_OPTION!=state){
            return null;
        }
        else{
        	file = chooser.getSelectedFile();//f为选择到的目录
        }
		return file;
	}

	private File getTemplate() throws Exception{
		Element ele = null;
		if (parentPanel instanceof NewTechnicsPart) {
			ele = XmlUtility.getTechnicsElement(((NewTechnicsPart) parentPanel).getCurrentTechnics());
		}else if(parentPanel instanceof TechnicsStepJPanel_XW){
			TechnicsStepJPanel_XW technicsStepJPanel = (TechnicsStepJPanel_XW) parentPanel;
			NewTechnicsPart newTechnicsPart = (NewTechnicsPart) technicsStepJPanel.getFrame();
			ele = XmlUtility.getTechnicsElement(newTechnicsPart.getCurrentTechnics());
		}else if(parentPanel instanceof TechnicsPaceJDialog){
			TechnicsPaceJDialog technicsPaceJDialog = (TechnicsPaceJDialog) parentPanel;
			NewTechnicsPart newTechnicsPart = (NewTechnicsPart) technicsPaceJDialog.getFrame();
			ele = XmlUtility.getTechnicsElement(newTechnicsPart.getCurrentTechnics());
		}
		String technicsNumber = ele.attributeValue("technicsNumber");
		String technicsName = ele.attributeValue("technicsName");
		String technicsCategory = ele.attributeValue("technicsCategory");
		String technicsType = ele.attributeValue("technicsType");

		String path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber,technicsName, technicsCategory);
		File floder = new File(path,"fbtemp");
		if(!floder.exists()){
			floder.mkdirs();
		}
		String title;
		if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			title = "请输入工步附图名称";
		}else{
			title = "请输入工艺附表名称";
		}
		String fName = JOptionPane.showInputDialog(parentPanel, title);
		while(fName!=null){
			if("".equals(fName)){
				fName = JOptionPane.showInputDialog(parentPanel, title);
			}else{
				break;
			}
		}

		if(fName==null){
			return null;
		}
		File wordFile = new File(floder,fName+".doc");
		InputStream is = null;
			try {
				if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
					is = AdditionalTableJPanel.class.getResourceAsStream("/templates/SOP附表.doc");
				}else{
					if (technicsType.contains("英文")) {
						is = AdditionalTableJPanel.class.getResourceAsStream("/templates/工艺附表(英文).doc");
					} else {
						is = AdditionalTableJPanel.class.getResourceAsStream("/templates/工艺附表.doc");
					}
				}
				FileUtils.copyInputStreamToFile(is, wordFile);
			}
			finally{
				try {
					if(is!=null) {
						is.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return wordFile;
	}

	private void renameAttach() {
		int rowCount = tableModel.getRowCount();
		if(rowCount < 1){
			JOptionPane.showMessageDialog(null, "未添加附表,不能重命名", "提示",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		int selectedRow = table.getSelectedRow();
		if(selectedRow < 0){
			JOptionPane.showMessageDialog(null, "未选择附表,不能重命名", "提示",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		String fileName = JOptionPane.showInputDialog(parentPanel, "重新命名工艺附表名称");
		if(fileName == null || "".equals(fileName)){
			return;
		}
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String name = tableModel.getValueAt(i, 0).toString();
			if (fileName.equals(name)) {
				JOptionPane.showMessageDialog(null, "相同的文件名，重命名附表失败！", "提示",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		String bsoID = tableModel.getValueAt(selectedRow, 5).toString();
		try {
			tableModel.setValueAt(fileName, selectedRow, 0);
			writeRenameXml(fileName, bsoID, selectedRow);
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	private void addAttach() {
		//File file = getAttachFile();
		if (parentPanel instanceof TechnicsPaceJDialog && "SOP".equals(startType)) {
			if(tableModel.getRowCount()>0){
				JOptionPane.showMessageDialog(null, "只能添加一份工步附图！", "提示",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		File file = null;
		try {
			file = this.getTemplate();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		System.out.println("file=" + file);
		if (file == null) {
			return;
		}
		imagePath = file.getAbsolutePath();
		System.out.println("imagePath=" + imagePath);
		String fileName = file.getName();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String name = tableModel.getValueAt(i, 0).toString();
			System.out.println("name=" + name);
			System.out.println("fileName=" + fileName);
			String fn = fileName.substring(0,fileName.lastIndexOf("."));
			if (fn.equals(name)) {
				JOptionPane.showMessageDialog(null, "相同的文件名，添加附表失败！", "提示",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		String fn = UUID.randomUUID()+"";
		fn = fn.replaceAll("-", "");
		String fn2 = file.getName().replace(".doc", "")+"_"+fn+".doc";
		String timeFolder = new SimpleDateFormat("yyyyMMddhhmmssSSS").format(new Date());
		String path = "additionaltable\\" + timeFolder + "\\" + fn2;
		System.out.println("path=" + path);
		int row = addToTable(fn,file, path);

		setTabTitle();
		try {
			writeFile2Xml(fn2,file,path,row);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void writeRenameXml(String fileName, String bsoID, int row){
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
		Element additionTables = XmlUtility.getTechnicsAdditionTablesElement(ele);
		List<Element> additionTable = additionTables.elements("additionaltable");
		for(Element element : additionTable){
			String id = element.attributeValue("bsoID");
			if(id.equals(bsoID)){
				element.setAttributeValue("attachName", fileName);
				XmlUtility.setAttributeValue(element, "attachName",
						(String) tableModel.getValueAt(row, 0));
				break;
			}
		}
		newTechnicsPart.saveProcess(techele);
	}

	private void writeFile2Xml(String fileName,File file, String path,int row) throws Exception {
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
		String technicsCategory = techele.attributeValue("technicsCategory");
		String technicsName = techele.attributeValue("technicsName");
		String technicsNumber = techele.attributeValue("technicsNumber");
		String technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
		Element additionTables = XmlUtility.getTechnicsAdditionTablesElement(ele);
		additionTables.add(this.createAdditionaltableElement(row));
		newTechnicsPart.saveProcess(techele);

		String timeFolder = path.substring(0, path.lastIndexOf("\\") + 1);
		final File timeFile = new File(technicsDirectory + "\\" + timeFolder + "\\");
		if (timeFile.exists())
			return;
		if (!timeFile.exists())
			timeFile.mkdirs();
		String targetFileName = technicsDirectory + "\\" + timeFolder + "\\"
				+ fileName;
		final File additionTableFile = new File(targetFileName);
		FileUtils.copyFile(file, additionTableFile);
	}

	private int addToTable(String bosid,File file, String path) {
		String absolutePath = file.getAbsolutePath();
		String fileName = file.getName();
		String name, type;
		if (fileName.contains(".")) {
			name = fileName.substring(0, fileName.lastIndexOf('.'));//fileName.split("\\.")[0];
			type = fileName.substring(fileName.lastIndexOf('.'),fileName.length());;//file.getName().split("\\.")[1];
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
		String path2 = path.replace(".doc", ".mht");
//		path2 = path2.replace("\\", "/");
		tableModel.setValueAt(path2, rowCount - 1, 4);
		tableModel.setValueAt(bosid, rowCount - 1, 5);
		return rowCount-1;
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
//		int[] rows = table.getSelectedRows();
//		if (rows.length == 0)
//			return;
		int row = table.getSelectedRow();
		if(row==-1){
			return;
		}

		String technicsPath = null;

		Element techele = null;
		Element ele = null;
		NewTechnicsPart newTechnicsPart = null;
		if (parentPanel instanceof NewTechnicsPart) {
			newTechnicsPart = (NewTechnicsPart) parentPanel;
			techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) parentPanel).getCurrentTechnics());
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

//		for (int i = 0; i < rows.length; i++) {
			String path = tableModel.getValueAt(row, 3).toString();
			File file = new File(technicsPath + "\\"+ path.substring(0, path.lastIndexOf("\\")));
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
			tableModel.removeRow(row);
			Element additionTables = 	XmlUtility.getTechnicsAdditionTablesElement(ele);
			List additionTableElementList = XmlUtility.getTechnicsAdditionTables(ele);
			if(additionTableElementList!=null){
				for(int j=0;j<additionTableElementList.size();j++){
					Element e = (Element)additionTableElementList.get(j);
					if(path.equals(XmlUtility.getAttributeValue(e, "absolutePath"))){
						additionTables.remove(e);
						newTechnicsPart.saveProcess(techele);
					}
				}
			}


//			for (int j = i + 1; j < rows.length; j++) {
//				rows[j] = rows[j] - 1;
//			}
//		}
		setTabTitle();
	}

	private void upAttach(){
		int row=table.getSelectedRow();
		if(row==0 || row==-1){
			return;
		}
		int tagerow=row-1;
		changeValue(row,tagerow);
		table.setRowSelectionInterval(tagerow, tagerow);
	}

	private void downAttach(){
		int row=table.getSelectedRow();
		if(row==table.getRowCount()-1 || row==-1){
			return;
		}
		int tagerow=row+1;
		changeValue(row,tagerow);
		table.setRowSelectionInterval(tagerow, tagerow);
	}

	private void saveAttach(){
		Element techele = null;
		Element ele = null;
		NewTechnicsPart newTechnicsPart = null;
		if (parentPanel instanceof NewTechnicsPart) {
			techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) parentPanel).getCurrentTechnics());
			newTechnicsPart = (NewTechnicsPart) parentPanel;
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
		Element additionTables = XmlUtility.getTechnicsAdditionTablesElement(ele);
		XmlUtility.deleteAllChildElements(additionTables);
		Vector<Element> elements=getElements();
		for(Element element:elements){
			additionTables.add(element);
		}
		newTechnicsPart.saveProcess(techele);
	}

	public void changeValue(int row,int tagerow){
		String drawingName = (String) tableModel.getValueAt(tagerow, 0);
		String drawingType = (String) tableModel.getValueAt(tagerow, 1);
		String attachSize = (String) tableModel.getValueAt(tagerow, 2);
		String absolutePath = (String) tableModel.getValueAt(tagerow, 3);
		String absolutePath2 = (String) tableModel.getValueAt(tagerow, 4);
		String bsoID = (String) tableModel.getValueAt(tagerow, 5);

		tableModel.setValueAt((String) tableModel.getValueAt(row, 0), tagerow, 0);
		tableModel.setValueAt((String) tableModel.getValueAt(row, 1), tagerow, 1);
		tableModel.setValueAt((String) tableModel.getValueAt(row, 2), tagerow, 2);
		tableModel.setValueAt((String) tableModel.getValueAt(row, 3), tagerow, 3);
		tableModel.setValueAt((String) tableModel.getValueAt(row, 4), tagerow, 4);
		tableModel.setValueAt((String) tableModel.getValueAt(row, 5), tagerow, 5);

		tableModel.setValueAt(drawingName, row, 0);
		tableModel.setValueAt(drawingType, row, 1);
		tableModel.setValueAt(attachSize, row, 2);
		tableModel.setValueAt(absolutePath, row, 3);
		tableModel.setValueAt(absolutePath2, row, 4);
		tableModel.setValueAt(bsoID, row, 5);
		table.updateUI();
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
		upJButton.setEnabled(b);
		downJButton.setEnabled(b);
		//saveJButton.setEnabled(b);
		renameJButton.setEnabled(b);
	}

	public void firstSetTitle(int row) {
		if(parentPanel instanceof NewTechnicsPart){
			if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
				if(row > 0){
					((NewTechnicsPart) parentPanel).getNewTechnicsMasterJPanel_XW().getTabbedPane().setTitleAt(4, "工艺附表" + "(" + row + ")");
				}else{
					((NewTechnicsPart) parentPanel).getNewTechnicsMasterJPanel_XW().getTabbedPane().setTitleAt(4, "工艺附表");
				}
			}else{
				if(row > 0){
					((NewTechnicsPart) parentPanel).getNewTechnicsMasterJPanel_XW().getTabbedPane().setTitleAt(2, "工艺附表" + "(" + row + ")");
				}else{
					((NewTechnicsPart) parentPanel).getNewTechnicsMasterJPanel_XW().getTabbedPane().setTitleAt(2, "工艺附表");
				}
			}

		}
	}

}