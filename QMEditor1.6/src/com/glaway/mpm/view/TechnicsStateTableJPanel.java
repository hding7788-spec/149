package com.glaway.mpm.view;

import com.glaway.mpm.model.FileTemplate;
import com.glaway.mpm.model.TechnicsStateTableBean;
import com.glaway.mpm.pdf.HtmlGenerator;
import com.glaway.mpm.util.*;
import com.glaway.mpm.wcIntf.TemplateIntf;
import com.glaway.speciaword.common.CommonHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import wt.doc.WTDocument;
import wt.part.WTPart;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
/**
 * 工艺状态表
 * @author LongXiuChuan 2014-02-13
 *
 */
public class TechnicsStateTableJPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private NewTechnicsPart parentPanel;

	JPanel panel = new JPanel();

	private DefaultTableModel tableModel = new DefaultTableModel() {
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	public JTable table = new JTable(tableModel);

	private JButton addJButton = new IconButton("/images/button_add.png", "添加");

	private JButton addDwgJButton = new IconButton("/images/button_add.png", "添加DWG简图");

	private JButton openDwgJButton = new IconButton("/images/button_open.png", "编辑DWG简图");

	private JButton uploadDwgJButton = new IconButton("/images/button_add_local_pic.png", "上载DWG简图");

	private JButton deleteJButton = new IconButton("/images/button_remove.png", "移除");

	private JButton openJButton = new IconButton("/images/button_open.png", "编辑");

	private static String imagePath = "";
	private static final Font useFont = new Font("Dialog", 0, 16);
	private static JButton btn = new JButton();
	private static final FontMetrics metrics = btn.getFontMetrics(useFont);
	private Element techele = null;
	private String imageFolder;

	public TechnicsStateTableJPanel(NewTechnicsPart parent) {
		this.parentPanel = parent;
		jbInit();
		setName("TechnicsStateTable");
	}

	public void setTabTitle() {
//		int i = tableModel.getRowCount();

	}

	private void jbInit() {
		addJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addAttach();
			}
		});

		addDwgJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addDwgProcess();
			}
		});

		openDwgJButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});

		uploadDwgJButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					uploadDwgProcess();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(TechnicsStateTableJPanel.this, "上载DWG文件异常！");
					e1.printStackTrace();
				}
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
				editData();
			}
		});

//		panel.setLayout(new GridBagLayout());
//		panel.add(addJButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
//				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
//						5, 5, 0, 5), 0, 0));
//		panel.add(deleteJButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
//				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
//						5, 5, 0, 5), 0, 0));
//		panel.add(openJButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
//				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
//						5, 5, 0, 5), 0, 0));

		setLayout(new GridBagLayout());
		tableModel.addColumn("序号");
		tableModel.addColumn("制造部门");
		tableModel.addColumn("使用部门");
		tableModel.addColumn("工艺状态");
		tableModel.addColumn("图片附件名称");
		tableModel.addColumn("图片附件路径");
		tableModel.addColumn("图片附件类型");
		tableModel.addColumn("图片附件大小");
		tableModel.addColumn("bsoID");
		tableModel.addColumn("DwgNumber");
		tableModel.addColumn("模板Number");
		tableModel.addColumn("type");
		tableModel.addColumn("imageOid");
		tableModel.addColumn("parentImageOid");
		tableModel.addColumn("版本");
		tableModel.addColumn("modelName");
		tableModel.addColumn("fileName");
		setHideColumn(5);
		setHideColumn(6);
		setHideColumn(7);
		setHideColumn(8);
		setHideColumn(9);
		setHideColumn(10);
		setHideColumn(11);
		setHideColumn(12);
		setHideColumn(13);
		setHideColumn(14);
		setHideColumn(15);
		setHideColumn(16);
		table.setRowHeight(50);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		setTableColSize();

		this.table.getColumnModel().getColumn(3).setCellRenderer(new SWRenderer());
		this.table.getColumnModel().getColumn(3).setCellEditor(new SWEditor());

//		JPanel p = new JPanel();
//		p.setLayout(new BorderLayout());
//		p.add(table.getTableHeader(), BorderLayout.PAGE_START);
//		p.add(table, BorderLayout.CENTER);
//
//		JScrollPane pane = new JScrollPane(p,
//				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
//				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//
//		add(pane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
//				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
//						0, 0, 0, 0), 0, 0));
//		add(panel, new GridBagConstraints(1, 0, 1, 1, 0, 1.0,
//				GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(
//						0, 0, 0, 0), 0, 0));

		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		JScrollPane pane = new JScrollPane(table);
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
                    .addComponent(addDwgJButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(openDwgJButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(uploadDwgJButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(deleteJButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(openJButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                     )
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(addJButton)
                .addGap(26, 26, 26)
                .addComponent(addDwgJButton)
                .addGap(26, 26, 26)
                .addComponent(openDwgJButton)
                .addGap(26, 26, 26)
                .addComponent(uploadDwgJButton)
                .addGap(26, 26, 26)
                .addComponent(deleteJButton)
                .addGap(28, 28, 28)
                .addComponent(openJButton)
                .addContainerGap(225, Short.MAX_VALUE))
        );
        GroupLayout jPanel1Layout = new GroupLayout(leftPanel);
        leftPanel.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(pane, GroupLayout.PREFERRED_SIZE, (int)(width*0.4)+80, GroupLayout.PREFERRED_SIZE)
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

		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = table.rowAtPoint(e.getPoint());
					if (row != -1) {
						//openFile();
					}
				}
			}
		});
	}

	private void openFile() {
		try {
			if(techele==null) return;
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
			}
			System.out.println("filePath==========" + filePath);
			Vector<String> vector = new Vector<String>();
			String path = "";
			int[] select = table.getSelectedRows();
			for (int i = 0; i < select.length; i++) {
				String attachPath = (String) table.getValueAt(select[i], 5);
				if (filePath.endsWith("\\") || filePath.endsWith("/")) {
					path = filePath + attachPath;
				} else {
					path = filePath + "\\" + attachPath;
				}
				if(attachPath.endsWith("dwg")){
					vector.add(path);
				}
			}
			System.out.println("vector==========" + vector);
			for (int i = 0; i < vector.size(); i++) {
				Runtime.getRuntime().exec("rundll32.exe url.dll,FileProtocolHandler  " + vector.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Vector<Element> getElements() {
		Vector<Element> elements = new Vector<Element>();
//		for (int i = 0; i < tableModel.getRowCount(); i++) {
//			if (isRowNull(i))
//				continue;
//			Element element = DocumentHelper.createElement("technicsStateTable");
//			element.setAttributeValue("bsoID", "");
//			element.setAttributeValue("zzdw", "");
//			element.setAttributeValue("sydw", "");
//			element.setAttributeValue("gyzt", "");
//			element.setAttributeValue("attachName", "");
//			element.setAttributeValue("attachType", "");
//			element.setAttributeValue("attachSize", "");
//			element.setAttributeValue("absolutePath", "");
//
//			XmlUtility.setAttributeValue(element, "zzdw", String.valueOf(tableModel.getValueAt(i, 1)));
//			XmlUtility.setAttributeValue(element, "sydw", String.valueOf(tableModel.getValueAt(i, 2)));
//			XmlUtility.setAttributeValue(element, "gyzt", String.valueOf(tableModel.getValueAt(i, 3)));
//			XmlUtility.setAttributeValue(element, "attachName", String.valueOf(tableModel.getValueAt(i, 4)));
//			XmlUtility.setAttributeValue(element, "absolutePath", String.valueOf(tableModel.getValueAt(i, 5)));
//			XmlUtility.setAttributeValue(element, "attachType", String.valueOf(tableModel.getValueAt(i, 6)));
//			XmlUtility.setAttributeValue(element, "attachSize", String.valueOf(tableModel.getValueAt(i, 7)));
//			XmlUtility.setAttributeValue(element, "bsoID", String.valueOf(tableModel.getValueAt(i, 8)));
//
//			elements.add(element);
//		}
		return elements;
	}


	private Element createAdditionaltableElement(int row,TechnicsStateTableBean bean) {
		Element element = DocumentHelper.createElement("technicsStateTable");
		element.setAttributeValue("bsoID", "");
		element.setAttributeValue("zzdw", "");
		element.setAttributeValue("sydw", "");
		element.setAttributeValue("gyzt", "");
		element.setAttributeValue("attachName", "");
		element.setAttributeValue("attachType", "");
		element.setAttributeValue("attachSize", "");
		element.setAttributeValue("absolutePath", "");

		XmlUtility.setAttributeValue(element, "zzdw", String.valueOf(tableModel.getValueAt(row, 1)));
		XmlUtility.setAttributeValue(element, "sydw", String.valueOf(tableModel.getValueAt(row, 2)));

		String gyzt = bean.getGyzt();
		if(gyzt != null) {
			gyzt = HtmlGenerator.removeImageTags(gyzt);
			gyzt = CommonHelper.replaceSaveSeperator(gyzt, imageFolder);
		}
		XmlUtility.setGyztContent(element, gyzt);
		XmlUtility.setAttributeValue(element, "attachName", String.valueOf(tableModel.getValueAt(row, 4)));
		XmlUtility.setAttributeValue(element, "absolutePath", String.valueOf(tableModel.getValueAt(row, 5)));
		XmlUtility.setAttributeValue(element, "attachType", String.valueOf(tableModel.getValueAt(row, 6)));
		XmlUtility.setAttributeValue(element, "attachSize", String.valueOf(tableModel.getValueAt(row, 7)));
		XmlUtility.setAttributeValue(element, "bsoID", String.valueOf(tableModel.getValueAt(row, 8)));
		XmlUtility.setAttributeValue(element, "docNumber", String.valueOf(tableModel.getValueAt(row, 9)));
		XmlUtility.setAttributeValue(element, "tempdocNumber", String.valueOf(tableModel.getValueAt(row, 10)));
		XmlUtility.setAttributeValue(element, "type", String.valueOf(tableModel.getValueAt(row, 11)));
		XmlUtility.setAttributeValue(element, "imageOid", String.valueOf(tableModel.getValueAt(row, 12)));
		XmlUtility.setAttributeValue(element, "parentImageOid", String.valueOf(tableModel.getValueAt(row, 13)));
		XmlUtility.setAttributeValue(element, "version", String.valueOf(tableModel.getValueAt(row, 14)));
		XmlUtility.setAttributeValue(element, "modelName", String.valueOf(tableModel.getValueAt(row, 15)));
		XmlUtility.setAttributeValue(element, "fileName", String.valueOf(tableModel.getValueAt(row, 16)));

		return element;
	}

	private Element updateAdditionaltableElement2(int row,TechnicsStateTableBean bean) {
		List<Element> list = XmlUtility.getTechnicsStateTables(techele);
		if(list != null) {
			String bsoID = String.valueOf(tableModel.getValueAt(row, 8));
			for (Element element : list) {
				String bid = element.attributeValue("bsoID");
				if(bid.equals(bsoID)) {
					XmlUtility.setAttributeValue(element, "zzdw", String.valueOf(tableModel.getValueAt(row, 1)));
					XmlUtility.setAttributeValue(element, "sydw", String.valueOf(tableModel.getValueAt(row, 2)));

					String gyzt = bean.getGyzt();
					if(gyzt != null) {
						gyzt = HtmlGenerator.removeImageTags(gyzt);
						gyzt = CommonHelper.replaceSaveSeperator(gyzt, imageFolder);
					}

					XmlUtility.setGyztContent(element, gyzt);
					XmlUtility.setAttributeValue(element, "attachName", String.valueOf(tableModel.getValueAt(row, 4)));
					XmlUtility.setAttributeValue(element, "absolutePath", String.valueOf(tableModel.getValueAt(row, 5)));
					XmlUtility.setAttributeValue(element, "attachType", String.valueOf(tableModel.getValueAt(row, 6)));
					XmlUtility.setAttributeValue(element, "attachSize", String.valueOf(tableModel.getValueAt(row, 7)));
					XmlUtility.setAttributeValue(element, "bsoID", String.valueOf(tableModel.getValueAt(row, 8)));
					XmlUtility.setAttributeValue(element, "docNumber", String.valueOf(tableModel.getValueAt(row, 9)));
					XmlUtility.setAttributeValue(element, "tempdocNumber", String.valueOf(tableModel.getValueAt(row, 10)));
					XmlUtility.setAttributeValue(element, "type", String.valueOf(tableModel.getValueAt(row, 11)));
					XmlUtility.setAttributeValue(element, "imageOid", String.valueOf(tableModel.getValueAt(row, 12)));
					XmlUtility.setAttributeValue(element, "parentImageOid", String.valueOf(tableModel.getValueAt(row, 13)));
					XmlUtility.setAttributeValue(element, "version", String.valueOf(tableModel.getValueAt(row, 14)));
					XmlUtility.setAttributeValue(element, "modelName", String.valueOf(tableModel.getValueAt(row, 15)));
					XmlUtility.setAttributeValue(element, "fileName", String.valueOf(tableModel.getValueAt(row, 16)));
					return element;
				}
			}
		}

		return null;
	}

	public void setTableValues(Vector<Element> vec) {
		techele = XmlUtility.getTechnicsElement(parentPanel.getCurrentTechnics());
		imageFolder = WorkSpaceUtil.getTechnicsPath(techele);
		clearTable();
		for (int i = 0; i < vec.size(); i++) {
			Vector vector = new Vector();
			for (int j = 0; j < table.getColumnCount(); j++) {
				vector.add("");
			}
			tableModel.addRow(vector);
			Element element = vec.get(i);
			String zzdw = element.attributeValue("zzdw");
			String sydw = element.attributeValue("sydw");
			String gyzt = XmlUtility.getGyztContent(element);
			if(gyzt != null) {
				gyzt = CommonHelper.replaceReadSeperator(gyzt, imageFolder);
			}

			String attachName = element.attributeValue("attachName");
			String absolutePath = element.attributeValue("absolutePath");
			String attachType = element.attributeValue("attachType");
			String attachSize = element.attributeValue("attachSize");
			String bsoID = element.attributeValue("bsoID");
			String docNumber = element.attributeValue("docNumber");
			String tempdocNumber = element.attributeValue("tempdocNumber");
			String type = element.attributeValue("type");
			String imageOid = element.attributeValue("imageOid");
			String parentImageOid = element.attributeValue("parentImageOid");
			String version = element.attributeValue("version");
			String modelName = element.attributeValue("modelName");
			String fileName = element.attributeValue("fileName");

			tableModel.setValueAt(i+1, i, 0);
			tableModel.setValueAt(zzdw, i, 1);
			tableModel.setValueAt(sydw, i, 2);
			tableModel.setValueAt(gyzt, i, 3);
			tableModel.setValueAt(attachName, i, 4);
			tableModel.setValueAt(absolutePath, i, 5);
			tableModel.setValueAt(attachType, i, 6);
			tableModel.setValueAt(attachSize, i, 7);
			tableModel.setValueAt(bsoID, i, 8);

			tableModel.setValueAt(docNumber, i, 9);
			tableModel.setValueAt(tempdocNumber, i, 10);
			tableModel.setValueAt(type, i, 11);
			tableModel.setValueAt(imageOid, i, 12);
			tableModel.setValueAt(parentImageOid, i, 13);
			tableModel.setValueAt(version, i, 14);
			tableModel.setValueAt(modelName, i, 15);
			tableModel.setValueAt(fileName, i, 16);
		}
	}

	private void setHideColumn(int index) {
		table.getColumnModel().getColumn(index).setMinWidth(0);
		table.getColumnModel().getColumn(index).setMaxWidth(0);
	}

	private void setTableColSize() {
		table.getColumnModel().getColumn(0).setMinWidth(50);
		table.getColumnModel().getColumn(0).setMaxWidth(50);

		table.getColumnModel().getColumn(1).setMinWidth(100);
		table.getColumnModel().getColumn(1).setMaxWidth(100);

		table.getColumnModel().getColumn(2).setMinWidth(100);
		table.getColumnModel().getColumn(2).setMaxWidth(100);

		table.getColumnModel().getColumn(3).setMinWidth(300);
		table.getColumnModel().getColumn(3).setMaxWidth(300);
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

	private void addAttach() {
		if( techele == null) {
			techele = XmlUtility.getTechnicsElement(parentPanel.getCurrentTechnics());
		}
		new AddTechnicsStateItemJDialog(parentPanel,this,0,techele);
	}

	private void editData() {
		if( techele == null) {
			techele = XmlUtility.getTechnicsElement(parentPanel.getCurrentTechnics());
		}
		new UpdateTechnicsStateItemJDialog(parentPanel,this,1,techele);
	}

	public void addToTable(TechnicsStateTableBean bean) {
		addOneRow();
		int rowCount = table.getRowCount();
		tableModel.setValueAt(rowCount, rowCount - 1, 0);
		tableModel.setValueAt(bean.getZzdw(), rowCount - 1, 1);
		tableModel.setValueAt(bean.getSydw(), rowCount - 1, 2);
		tableModel.setValueAt(bean.getGyzt(), rowCount - 1, 3);

		if(bean.getImagePath() != null && !"".equals(bean.getImagePath())) {
			File file = new File(bean.getImagePath());
			String fileName = file.getName();
			String endFix = fileName.substring(fileName.indexOf("."), fileName.length());
			String fn = UUID.randomUUID()+"";
			fn = fn.replaceAll("-", "");
			String fn2 = fn + endFix;
			String timeFolder = new SimpleDateFormat("yyyyMMddhhmmssSSS").format(new Date());
			String path = "technicsStateTable" + File.separator + timeFolder + File.separator + fn2;

			tableModel.setValueAt(fileName, rowCount - 1, 4);//名称
			tableModel.setValueAt(path, rowCount - 1, 5);//路径
			tableModel.setValueAt(endFix, rowCount - 1, 6);//类型
			tableModel.setValueAt(getFileSize(file), rowCount - 1, 7);//大小
			tableModel.setValueAt(fn, rowCount - 1, 8);


			writeFile2Xml(fn2,file,path,rowCount - 1,bean);
		} else {
			String fn = UUID.randomUUID()+"";
			tableModel.setValueAt(fn, rowCount - 1, 8);
			writeFile2Xml(null,null,null,rowCount - 1,bean);
		}
	}

	private void addOneRow() {
		Vector vector = new Vector();
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			vector.add("");
		}
		tableModel.addRow(vector);
	}

	public void update(TechnicsStateTableBean bean,int row) {
		tableModel.setValueAt(bean.getZzdw(), row, 1);
		tableModel.setValueAt(bean.getSydw(), row, 2);
		tableModel.setValueAt(bean.getGyzt(), row, 3);

		if(!bean.isChangeFile()) {
			updateTechnicsEle(bean,row);
			return;
		}
		if(bean.getImagePath() != null && !"".equals(bean.getImagePath())) {
			File file = new File(bean.getImagePath());
			String fileName = file.getName();
			String endFix = fileName.substring(fileName.indexOf("."), fileName.length());
			String fn = UUID.randomUUID()+"";
			fn = fn.replaceAll("-", "");
			String fn2 = fn + endFix;
			String timeFolder = new SimpleDateFormat("yyyyMMddhhmmssSSS").format(new Date());
			String path = "technicsStateTable" + File.separator + timeFolder + File.separator + fn2;

			tableModel.setValueAt(fileName, row, 4);//名称
			tableModel.setValueAt(path, row, 5);//路径
			tableModel.setValueAt(endFix, row, 6);//类型
			tableModel.setValueAt(getFileSize(file), row, 7);//大小

			//tableModel.setValueAt(fn, row, 8);

			writeFile2Xml2(fn2,file,path,row,bean);
		} else {
			writeFile2Xml2(null,null,null,row,bean);
		}

	}

	private String getFileSize(File file) {
		String absolutePath = file.getAbsolutePath();
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
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		if (size / 1024 > 1024) {
			return String.valueOf(nf.format(size / (1024 * 1024))) + "MB";
		} else {
			return String.valueOf(nf.format(size / 1024)) + "KB";
		}
	}

	private void writeFile2Xml(String fileName,File file, String path,int row,TechnicsStateTableBean bean) {
		try {
			if (parentPanel instanceof NewTechnicsPart) {
				if( techele == null) {
					techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) parentPanel).getCurrentTechnics());
				}
			}
			if(techele==null) return;
			String technicsNumber = techele.attributeValue("technicsNumber");
			String technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
			Element additionTables = XmlUtility.getTechnicsStateTablesElement(techele);
			additionTables.add(this.createAdditionaltableElement(row,bean));
			((NewTechnicsPart) parentPanel).saveProcess(techele);

			if(fileName != null && file != null && path != null) {
				path = path.replace("/", "\\");
				String timeFolder = path.substring(0, path.lastIndexOf(File.separator) + 1);
				final File timeFile = new File(technicsDirectory + File.separator + timeFolder + File.separator);
				if (timeFile.exists())
					return;
				if (!timeFile.exists())
					timeFile.mkdirs();
				String targetFileName = technicsDirectory + File.separator + timeFolder + File.separator
						+ fileName;
				final File additionTableFile = new File(targetFileName);
				FileUtils.copyFile(file, additionTableFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeFile2Xml2(String fileName,File file, String path,int row,TechnicsStateTableBean bean) {
		try {
			if (parentPanel instanceof NewTechnicsPart) {
				if( techele == null) {
					techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) parentPanel).getCurrentTechnics());
				}
			}
			if(techele==null) return;
			String technicsNumber = techele.attributeValue("technicsNumber");
			String technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
			updateAdditionaltableElement2(row,bean);
			((NewTechnicsPart) parentPanel).saveProcess(techele);

			if(fileName != null && file != null && path != null) {
				String timeFolder = path.substring(0, path.lastIndexOf(File.separator) + 1);
				final File timeFile = new File(technicsDirectory + File.separator + timeFolder + File.separator);
				if (timeFile.exists())
					return;
				if (!timeFile.exists())
					timeFile.mkdirs();
				String targetFileName = technicsDirectory + File.separator + timeFolder + File.separator
						+ fileName;
				final File additionTableFile = new File(targetFileName);
				FileUtils.copyFile(file, additionTableFile);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateTechnicsEle(TechnicsStateTableBean bean,int row) {

		if (parentPanel instanceof NewTechnicsPart) {
			if(techele == null) {
				techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) parentPanel).getCurrentTechnics());
			}
		}
		if(techele==null) return;
		String technicsNumber = techele.attributeValue("technicsNumber");
		String technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
		List<Element> list = XmlUtility.getTechnicsStateTables(techele);
		if(list != null) {
			String bsoID = String.valueOf(tableModel.getValueAt(row, 8));
			for (Element element : list) {
				String bid = element.attributeValue("bsoID");
				if(bid.equals(bsoID)) {
					XmlUtility.setAttributeValue(element, "zzdw", bean.getZzdw());
					XmlUtility.setAttributeValue(element, "sydw", bean.getSydw());

					String gyzt = bean.getGyzt();
					gyzt = HtmlGenerator.removeImageTags(gyzt);
					XmlUtility.setGyztContent(element, CommonHelper.replaceSaveSeperator(gyzt, imageFolder));
				}
			}
			((NewTechnicsPart) parentPanel).saveProcess(techele);
		}
	}

	private void deleteAttach() {
		int row = table.getSelectedRow();
		if(row==-1){
			return;
		}

		if (parentPanel instanceof NewTechnicsPart) {
			if(techele == null) {
				techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) parentPanel).getCurrentTechnics());
			}
		}
		if(techele==null) return;
		String technicsNumber = techele.attributeValue("technicsNumber");
		String technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
		List<Element> list = XmlUtility.getTechnicsStateTables(techele);
		if(list != null) {
			String bsoID = String.valueOf(tableModel.getValueAt(row, 8));
			for (Element element : list) {
				String bid = element.attributeValue("bsoID");
				if(bid.equals(bsoID)) {
					Element parentEle = XmlUtility.getTechnicsStateTablesElement(techele);
					String absolutePath = element.attributeValue("absolutePath");
					parentEle.remove(element);
					if(absolutePath != null && !"".equals(absolutePath)) {
						absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));
						File dir = new File(technicsDirectory+File.separator+absolutePath);
						WorkSpaceUtil.delete(dir);
					}
					break;
				}
			}
			((NewTechnicsPart) parentPanel).saveProcess(techele);
		}

		tableModel.removeRow(row);

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
	}

	public void setUIEnabled(boolean b) {
		table.setEnabled(b);
		addJButton.setEnabled(b);
		addDwgJButton.setEnabled(b);
		openDwgJButton.setEnabled(b);
		uploadDwgJButton.setEnabled(b);
		deleteJButton.setEnabled(b);
		openJButton.setEnabled(b);
	}

	class SWEditor extends DefaultCellEditor {

		private static final long serialVersionUID = 1L;

		final SpecialWordPanel panle = new SpecialWordPanel(parentPanel, imageFolder);
		private JTable editingTable = null;
		private int editingRow = -1;
		private int editingColumn = -1;

		public SWEditor() {
			super(new JTextField());

			editorComponent = panle;

			delegate = new DefaultCellEditor.EditorDelegate() {
				@Override
				public Object getCellEditorValue() {
					return panle.getText();
				}

				@Override
				public void setValue(Object value) {
					panle.setText((value != null) ? CommonHelper.replaceReadSeperator(value.toString(), imageFolder) : "");
				}
			};
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			this.editingTable = table;
			this.editingRow = row;
			this.editingColumn = column;
			panle.setText(JavaUtil.convertNull(value));
			return this.panle;
		}

		@Override
		public Object getCellEditorValue() {
			return panle.getText();
		}

		public void insertText(String str) {
			if (str != null && editorComponent != null) {
				((SpecialWordPanel) editorComponent).insertText(str);
			}
		}

	}

	class SWRenderer extends SpecialWordPanel implements TableCellRenderer {

		private static final long serialVersionUID = 1L;
		private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();
		@SuppressWarnings("unchecked")
		private final Map cellSizes = new HashMap();

		public SWRenderer() {
			super(parentPanel, imageFolder);
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table,
				Object obj, boolean isSelected, boolean hasFocus,
				final int row, final int column) {
			Element techEle = XmlUtility.getTechnicsElement(parentPanel.getCurrentTechnics());
			System.out.println("----------------------techEle--"+techEle);
			if(techEle != null) {
				setTechnicsPath(WorkSpaceUtil.getTechnicsPath(techEle));
			}

			adaptee.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, column);
			setBorder(null);
			setFont(adaptee.getFont());
			setText(adaptee.getText());

			TableColumnModel columnModel = table.getColumnModel();
			setSize(columnModel.getColumn(column).getWidth(), 100000);
			int height_wanted = (int) getPreferredSize().getHeight();
			addSize(table, row, column, height_wanted);
			height_wanted = findTotalMaximumRowSize(table, row);
			if (height_wanted != table.getRowHeight(row)) {
				table.setRowHeight(row, height_wanted);
			}

			if (isSelected) {
				setBackgroundColor(table.getSelectionBackground());
			} else {
				setBackgroundColor(Color.WHITE);
			}

			return this;
		}

		@SuppressWarnings("unchecked")
		private void addSize(JTable table, int row, int column, int height) {
			Map rows = (Map) cellSizes.get(table);
			if (rows == null) {
				cellSizes.put(table, rows = new HashMap());
			}
			Map rowheights = (Map) rows.get(new Integer(row));
			if (rowheights == null) {
				rows.put(new Integer(row), rowheights = new HashMap());
			}
			rowheights.put(new Integer(column), new Integer(height));
		}

		@SuppressWarnings("unchecked")
		private int findTotalMaximumRowSize(JTable table, int row) {
			int maximum_height = 0;
			Enumeration columns = table.getColumnModel().getColumns();
			while (columns.hasMoreElements()) {
				TableColumn tc = (TableColumn) columns.nextElement();
				TableCellRenderer cellRenderer = tc.getCellRenderer();
				if (cellRenderer instanceof SWRenderer) {
					SWRenderer tar = (SWRenderer) cellRenderer;
					maximum_height = Math.max(maximum_height, tar.findMaximumRowSize(table, row));
				}
			}

			return maximum_height;
		}

		@SuppressWarnings("unchecked")
		private int findMaximumRowSize(JTable table, int row) {
			Map rows = (Map) cellSizes.get(table);
			if (rows == null)
				return 0;
			Map rowheights = (Map) rows.get(new Integer(row));
			if (rowheights == null)
				return 0;
			int maximum_height = 0;
			for (Iterator it = rowheights.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				int cellHeight = ((Integer) entry.getValue()).intValue();
				maximum_height = Math.max(maximum_height, cellHeight);
			}
			return maximum_height;
		}
	}
	private void addDwgProcess() {
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
		String path = "technicsStateTable" + File.separator + timeFolder + File.separator + fileName;
		addDwgToTable(file, path, fileTemplate.getDocNumber(), fileTemplate.getTempId(), fileTemplate);
		setTabTitle();
//		saveDrawing(null);
		try {
			writePicture(imagePath, path, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// saveDrawing();
	}
	private FileTemplate getTemplate() throws Exception{
		Element ele = techele;

		String technicsNumber = ele.attributeValue("technicsNumber");
		String technicsName = ele.attributeValue("technicsName");
		String technicsCategory = ele.attributeValue("technicsCategory");

		String path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber,technicsName, technicsCategory);
		File floder = new File(path,"fbtemp");
		if(!floder.exists()){
			floder.mkdirs();
		}

		String fName  = null;

		TechnicStateTempChooseDialog dialog = new TechnicStateTempChooseDialog(parentPanel,LoadConfig.getInstance().getDwgTemplateType());

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
		String fn = UUID.randomUUID() + "";
		//序号"制造部门"使用部门"工艺状态"图片附件名称"图片附件路径"图片附件类型"图片附件大小"bsoID");
		// "简图名称", "简图类型", "简图大小", "路径", "DwgNumber", "模板Number", "type", "bsoID", "imageOid", "parentImageOid", "版本"

		tableModel.setValueAt(rowCount, rowCount - 1, 0); //序号
		tableModel.setValueAt(fileTemplate.getMakeDept(), rowCount - 1, 1);  		//制造部门
		tableModel.setValueAt(fileTemplate.getUseDept(), rowCount - 1, 2);			//使用部门
		tableModel.setValueAt(fileTemplate.getTechnicsState(), rowCount - 1, 3);			//工艺状态
		tableModel.setValueAt(fileTemplate.getDisplayName() + "." + type, rowCount - 1, 4);//图片附件名称
		tableModel.setValueAt(path, rowCount - 1, 5);//图片附件路径

		tableModel.setValueAt(type, rowCount - 1, 6); //图片附件类型
		tableModel.setValueAt(FileUtil.getFileSize(size), rowCount - 1, 7); //图片附件大小
		tableModel.setValueAt(fn, rowCount - 1, 8); //bsoID
		tableModel.setValueAt(docNumber, rowCount - 1, 9); //DwgNumber
		tableModel.setValueAt(tempdocNumber, rowCount - 1, 10);//模板Number
		tableModel.setValueAt("", rowCount - 1, 11);//type
		tableModel.setValueAt("", rowCount - 1, 12);//imageOid
		tableModel.setValueAt("", rowCount - 1, 13);//parentImageOid
		tableModel.setValueAt("", rowCount - 1, 14);//版本
		tableModel.setValueAt("", rowCount - 1, 15);//modelName
		tableModel.setValueAt(name, rowCount - 1, 16);//fileName

		TechnicsStateTableBean bean = new TechnicsStateTableBean();
		bean.setGyzt("");
		writeFile2Xml(fileName, file, path, rowCount - 1, bean);
	}
	private void writePicture(String sourcepath, String path, String fileName) throws Exception {
		String technicsCategory = techele.attributeValue("technicsCategory");
		String technicsName = techele.attributeValue("technicsName");
		String technicsNumber = techele.attributeValue("technicsNumber");
		Vector vec = XmlUtility.getAllDrawings(techele);
		for (int i = 0; i < vec.size(); i++) {
			Element element = (Element) vec.get(i);
			XmlUtility.writeDrawingFiles(element, technicsCategory, technicsNumber, technicsName, sourcepath, true);
		}
	}

	//TODO 上传DWG
		private void uploadDwgProcess() throws IOException{
			int row = table.getSelectedRow();
			if(row==-1){
				JOptionPane.showMessageDialog(parentPanel	, "请选择DWG文件行在上载！");
				return;
			}
			String ftype = (String) table.getValueAt(row, 6);
			if(!"dwg".equals(ftype.toLowerCase())){
				JOptionPane.showMessageDialog(parentPanel	, "请选择DWG文件行在上载！");
				return;
			}
			InputStream is = null;
			try{

			String dwgNumber = (String)table.getValueAt(row, 9);
			String dwgName = (String)table.getValueAt(row, 4);
			dwgName = dwgName.substring(0, dwgName.indexOf("."));
			String fileName = (String)table.getValueAt(row, 16);
			String technicsNumber = techele.attributeValue("technicsNumber");
			String path = (String)table.getValueAt(row, 5);
			String technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
			File dwgFile = new File(technicsDirectory+File.separator+path);

			byte[] bytes = null;
			is = new FileInputStream(dwgFile);
			bytes = IOUtils.toByteArray(is);
			String partNumber = XmlUtility.getAttributeValue(techele, "partNumber");
			WTPart part = TemplateIntf.getLatestParttByNumber(partNumber);
			WTDocument doc = TemplateIntf.uploadDwgDoc(part.getContainer(), dwgNumber, dwgName, bytes, fileName);
			String docNumber = doc.getNumber();
			table.setValueAt(docNumber, row, 9);
			List<Element> list = XmlUtility.getTechnicsStateTables(techele);
			if(list != null) {
				String bsoID = String.valueOf(tableModel.getValueAt(row, 8));
				for (Element element : list) {
					String bid = element.attributeValue("bsoID");
					if(bid.equals(bsoID)) {
						XmlUtility.setAttributeValue(element, "docNumber", docNumber);
						}
					}
				}
			}finally{
				if(is!=null){
					is.close();
				}
			}
			JOptionPane.showMessageDialog(parentPanel, "成功上载DWG文件！正在进行DWG转PDF，请先执行PDF工艺预览，确保PDF中的附图已转换成功，再执行工艺上载。");
		}

}