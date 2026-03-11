package com.glaway.mpm.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
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
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import org.dom4j.Document;
import org.dom4j.Element;

import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;

public class NewDrawingJPanel_View extends JPanel {
	private TechnicsStepJPanel_View parentPanel;

	JPanel panel = new JPanel();

	private ViewPopupMenu viwPop = new ViewPopupMenu();

	private DefaultTableModel tableModel = new DefaultTableModel() {
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	private JTable table = new JTable(tableModel);

	private JButton newJButton = new IconButton("/images/button_new_pic.png",
			"新建");

	private JButton addJButton = new IconButton(
			"/images/button_add_local_pic.png", "添加本地工序简图");

	private JButton addMiddleModelJButton = new IconButton(
			"/images/button_add_pds.gif", "添加PDS中间模型");

	private JButton deleteJButton = new IconButton("/images/button_remove.png",
			"移除");

	public NewDrawingJPanel_View(TechnicsStepJPanel_View parentPanel) {
		this.parentPanel = parentPanel;
		jbInit();
	}

	public void setTabTitle() {
		int i = tableModel.getRowCount();
		if (i > 0) {
			parentPanel.getTabbedPane().setTitleAt(7, "简图" + "(" + i + ")");
		} else {
			parentPanel.getTabbedPane().setTitleAt(7, "简图");
		}
	}

	private void jbInit() {
		panel.setLayout(new GridBagLayout());
		panel.add(newJButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		panel.add(addJButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		panel.add(addMiddleModelJButton, new GridBagConstraints(1, 2, 1, 1,
				0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(5, 5, 0, 5), 0, 0));
		panel.add(deleteJButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));

		setLayout(new GridBagLayout());
		tableModel.addColumn("简图名称");
		tableModel.addColumn("简图类型");
		tableModel.addColumn("简图大小");
		tableModel.addColumn("路径");
		setHideColumn(3);
		table.setRowHeight(25);
		table.setSelectionMode(2);
		table.getTableHeader().setReorderingAllowed(false);

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(table.getTableHeader(), BorderLayout.PAGE_START);
		p.add(table, BorderLayout.CENTER);

		JScrollPane pane = new JScrollPane(p,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		add(pane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		add(panel, new GridBagConstraints(1, 0, 1, 1, 0, 1.0,
				GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0));

		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2
						&& e.getButton() == MouseEvent.BUTTON1) {
					try {
						showSelectImage();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}

			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					int i = table.getSelectedRowCount();
					if (i == 0)
						return;
					viwPop.show(NewDrawingJPanel_View.this, e.getX(), e.getY());
				}
			}
		});
	}

	public Vector<Element> getElements() {
		Vector<Element> elements = new Vector<Element>();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if (isRowNull(i))
				continue;
			Element element = XmlUtility.createImage();
			XmlUtility.setAttributeValue(element, "bsoID", "");
			XmlUtility.setAttributeValue(element, "drawingName",
					(String) tableModel.getValueAt(i, 0));
			XmlUtility.setAttributeValue(element, "drawingType",
					(String) tableModel.getValueAt(i, 1));
			XmlUtility.setAttributeValue(element, "drawingSize",
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
			String drawingName = element.attributeValue("drawingName");
			String drawingType = element.attributeValue("drawingType");
			tableModel.setValueAt(drawingName, i, 0);
			tableModel.setValueAt(drawingType, i, 1);
			tableModel.setValueAt(element.attributeValue("drawingSize"), i, 2);
			tableModel.setValueAt(element.attributeValue("absolutePath"), i, 3);
			// tableModel.setValueAt(techDictionary+"\\"+element.attributeValue("addTime")+"\\"+drawingName+"."+drawingType,
			// i, 3);
		}
	}

	private void setHideColumn(int index) {
		table.getColumnModel().getColumn(index).setMinWidth(0);
		table.getColumnModel().getColumn(index).setMaxWidth(0);
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

	public void showSelectImage() throws Exception {
		if (parentPanel instanceof TechnicsStepJPanel_View) {
			int[] select = table.getSelectedRows();
			if (select != null && select.length > 0) {
				Element step = parentPanel.getElement();
				if (step == null)
					return;
				Document doc = step.getDocument();
				if (doc != null) {
					Element ele = XmlUtility.getTechnicsElement(doc);
					String filePath = WorkSpaceUtil.getTempTechnicsPath(
							ele.attributeValue("technicsNumber"),
							ele.attributeValue("technicsNumber"),
							ele.attributeValue("technicsNumber"));
					System.out.println("filePath==========" + filePath);
					Vector v = new Vector();
					for (int i = 0; i < select.length; i++) {
						String path = "";
						String xmlPath = (String) table
								.getValueAt(select[i], 3);
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
					System.out.println("简图路径============" + v);
					parentPanel.getFrame().show2DPane(v);
				}
			}
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
		newJButton.setEnabled(b);
		addJButton.setEnabled(b);
		addMiddleModelJButton.setEnabled(b);
		deleteJButton.setEnabled(b);

	}
}