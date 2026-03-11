package com.glaway.mpm.view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import com.glaway.mpm.qmIntf.technics.TechnicsPreview;
import com.glaway.mpm.util.FilesUtil;
import com.glaway.mpm.util.ProcedurePictureCreateUtil;
import com.glaway.mpm.util.TechnicsReleaseUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class NewTechnicsHistorySelect extends JDialog {
	private VaLogger logger = VaLogger.getLogger(this.getClass());

	private NewTechnicsPart frame;

	private Element techElement = null;

	private String partOid = null;

	private String technicsPath;

	private DefaultTableModel tableModel = new DefaultTableModel() {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	private JTable table = new JTable(tableModel);

	private JButton okButton = new JButton("确定");
	private JButton cancelButton = new JButton("取消");

	public NewTechnicsHistorySelect(NewTechnicsPart parent) {
		super(parent);
		frame = parent;
		setModal(true);

		Container container = getContentPane();
		container.setLayout(new GridBagLayout());

		Document document = frame.getCurrentTechnics();
		try {
			techElement = XmlUtility.getTechnicsElement(document);
			setTitle("查看历史版本(" + techElement.attributeValue("technicsNumber")
					+ "_" + techElement.attributeValue("technicsName") + ")");
		} catch (Exception e) {
			e.printStackTrace();
		}

		addTableColumn();
		container.add(new JScrollPane(table), new GridBagConstraints(0, 0, 3,
				1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 10, 5, 10), 0, 0));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowHeight(25);
		table.getTableHeader().setReorderingAllowed(false);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1 && e.getClickCount() == 2) {
					view();
				}
			}
		});

		setHideColumn(3);

		partOid = techElement.attributeValue("partOid");
		if (partOid != null) {
			setTableValue();
			if (table.getRowCount() == 0) {
				okButton.setEnabled(false);
			}
		}

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		container.add(buttonPanel, new GridBagConstraints(0, 1, 3, 1, 1.0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0));
		buttonPanel.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0,
				0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 0, 5, 5), 0, 0));
		okButton.setPreferredSize(new Dimension(80, 23));
		okButton.setMinimumSize(new Dimension(80, 23));
		okButton.setMaximumSize(new Dimension(80, 23));
		buttonPanel.add(okButton, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 5, 5), 0, 0));
		cancelButton.setPreferredSize(new Dimension(80, 23));
		cancelButton.setMinimumSize(new Dimension(80, 23));
		cancelButton.setMaximumSize(new Dimension(80, 23));
		buttonPanel.add(cancelButton, new GridBagConstraints(3, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 5, 0), 0, 0));

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view();
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int) (dimension.getWidth() - 750) / 2,
				(int) (dimension.getHeight() - 500) / 2, 750, 500);
		setVisible(true);
	}

	private void addTableColumn() {
		tableModel.addColumn("工艺规程版本名称");
		tableModel.addColumn("工艺规程版本状态");
		tableModel.addColumn("工艺规程版本描述");
		tableModel.addColumn("工艺规程oid");
	}

	private void setHideColumn(int index) {
		table.getColumnModel().getColumn(index).setMinWidth(0);
		table.getColumnModel().getColumn(index).setMaxWidth(0);
	}

	private void setTableValue() {
		List<List<String>> history = new ArrayList<List<String>>();
		String category = techElement.attributeValue("technicsCategory");
		String technicsType = techElement.attributeValue("technicsType");;
		String technicsDirectory;
		if ("rework".equals(category)) {
			technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(
					techElement.attributeValue("technicsNumber"),
					techElement.attributeValue("technicsName"));
		} else if ("temp".equals(category)) {
			technicsDirectory = WorkSpaceUtil.getTempTechnicsDirectory(
					techElement.attributeValue("technicsNumber"),
					techElement.attributeValue("technicsName"));
		} else {
			technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(techElement
					.attributeValue("technicsNumber"));
		}
		String technicsFolderName = technicsDirectory.substring(
				technicsDirectory.lastIndexOf("\\") + 1,
				technicsDirectory.length());
		try {
			history = TechnicsIntf.getProcessPlanHistory(partOid, category,
					technicsFolderName,technicsType);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		if (history != null) {
			for (int i = 0; i < history.size(); i++) {
				addOneRow();
				int count = tableModel.getRowCount() - 1;
				List<String> list = history.get(i);
				tableModel.setValueAt(list.get(0), count, 0);
				tableModel.setValueAt(list.get(1), count, 1);
				tableModel.setValueAt(list.get(2), count, 2);
				tableModel.setValueAt(list.get(3), count, 3);
			}
		}
	}

	private void addOneRow() {
		Vector vector = new Vector();
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			vector.add("");
		}
		tableModel.addRow(vector);
	}

	private void view() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "请选择历史工艺！", "提示", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		String version = tableModel.getValueAt(selectedRow, 0).toString();
		String lifecycle = tableModel.getValueAt(selectedRow, 1).toString();
		String technicsOid = tableModel.getValueAt(selectedRow, 3).toString();
		Vector vector = null;
		try {
			vector = TechnicsIntf.getProcessPlanbyOid(partOid, technicsOid, version);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
		logger.debug("历史文件vector====" + vector);
		if (vector == null) {
			return;
		}

		String technicsFolderName = (String) vector.get(0);
		if (technicsFolderName.toLowerCase().endsWith(".zip")) {
			technicsFolderName = technicsFolderName.substring(0, technicsFolderName.length() - 4);
		}
		logger.debug("历史文件路径====" + technicsFolderName);
		byte[] bytes = (byte[]) vector.get(1);
		String folderName = null;
		try {
			folderName = WorkSpaceUtil.getTempRootPath() + "\\" + technicsFolderName;
			File file = new File(folderName);
			if (file.exists()) {
				FilesUtil.delFolder(folderName);
			}
			file.mkdir();
			TechnicsReleaseUtil.unZip(bytes, folderName);
			File xmlFile = new File(folderName + "\\" + technicsFolderName + ".xml");
			Document technicsDocument = null;
			if (xmlFile.exists()) {
				technicsDocument = XmlUtility.getDocument(xmlFile);
			}
			if (technicsDocument != null) {
				technicsPath = folderName;
				this.dispose();
				Element technicsElement = XmlUtility.getTechnicsElement(technicsDocument);
				XmlUtility.setAttributeValue(technicsElement, "version", version);
				XmlUtility.setAttributeValue(technicsElement, "lifecycle", lifecycle);
				new NewTechnicsHistoryView(technicsDocument, frame, this, technicsPath, "查看历史版本");
			}
			this.dispose();
		} catch (Exception e) {
			if (folderName != null) {
				File file = new File(folderName);
				if (file.exists())
					FilesUtil.delFolder(folderName);
			}
			JOptionPane.showMessageDialog(this, "将相关工艺版本下载到本地时出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
			this.dispose();
		}

	}

	public void reviewTechnics() throws Exception {
		// 获取工艺xml文件名
		String fileName = technicsPath.substring(technicsPath
				.lastIndexOf(File.separator) + 1) + ".xml";
		String xmlFileName = technicsPath + File.separator + fileName;

		ProcedurePictureCreateUtil.createPictureDirectory(technicsPath);
		ProcedurePictureCreateUtil.operateDocument(
				XmlUtil.getDocument(xmlFileName), technicsPath);
		TechnicsPreview.preview(technicsPath);
	}
}