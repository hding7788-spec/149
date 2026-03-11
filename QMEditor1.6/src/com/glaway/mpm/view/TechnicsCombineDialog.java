package com.glaway.mpm.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.dom4j.Document;
import org.dom4j.Element;

import com.glaway.mpm.util.FilesUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class TechnicsCombineDialog extends JDialog {
	private NewTechnicsPart frame;

	private JTable table;

	private DefaultTableModel tableModel = new CombineTableModel();

	private JComboBox groupBox = new JComboBox();

	private Element techElement;

	private JTextPane textPanel = new JTextPane();
	List<String> technicsGroupNames = new ArrayList<String>();
	String groupName = "";

	public TechnicsCombineDialog(NewTechnicsPart frame) {
		super(frame, "工艺合编", true);
		this.frame = frame;
		getContentPane().setLayout(new GridBagLayout());

		final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.weightx = 1.0;
		gridBagConstraints_4.anchor = GridBagConstraints.SOUTH;
		gridBagConstraints_4.gridx = 0;
		gridBagConstraints_4.gridy = 2;
		gridBagConstraints_4.insets = new Insets(5, 5, 5, 5);
		getContentPane().add(panel, gridBagConstraints_4);

		final JLabel label = new JLabel();
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.SOUTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(0, 0, 0, 0);
		panel.add(label, gridBagConstraints);

		final JButton okButton = new JButton();
		okButton.setMultiClickThreshhold(23);
		okButton.setMinimumSize(new Dimension(0, 80));
		okButton.setMaximumSize(new Dimension(80, 23));
		okButton.setPreferredSize(new Dimension(80, 23));
		okButton.setText("确定");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.anchor = GridBagConstraints.SOUTHEAST;
		gridBagConstraints_1.gridx = 1;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.insets = new Insets(0, 0, 0, 0);
		panel.add(okButton, gridBagConstraints_1);

		final JButton cancelButton = new JButton();
		cancelButton.setMaximumSize(new Dimension(80, 23));
		cancelButton.setMinimumSize(new Dimension(80, 23));
		cancelButton.setPreferredSize(new Dimension(80, 23));
		cancelButton.setText("取消");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.anchor = GridBagConstraints.SOUTHEAST;
		gridBagConstraints_2.gridx = 2;
		gridBagConstraints_2.gridy = 0;
		gridBagConstraints_2.insets = new Insets(0, 5, 0, 5);
		panel.add(cancelButton, gridBagConstraints_2);

		final JPanel command = new JPanel();
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_5.fill = GridBagConstraints.BOTH;
		gridBagConstraints_5.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_5.weighty = 1.0;
		gridBagConstraints_5.weightx = 1.0;
		gridBagConstraints_5.gridx = 0;
		gridBagConstraints_5.gridy = 1;
		getContentPane().add(command, gridBagConstraints_5);

		command.setLayout(new GridBagLayout());
		final JLabel beizhu = new JLabel();
		beizhu.setText("备注");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_6.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints_6.weighty = 0.0;
		gridBagConstraints_6.weightx = 1.0;
		gridBagConstraints_6.gridx = 0;
		gridBagConstraints_6.gridy = 0;
		command.add(beizhu, gridBagConstraints_6);

		JScrollPane js = new JScrollPane(textPanel,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_7.fill = GridBagConstraints.BOTH;
		gridBagConstraints_7.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints_7.weighty = 1.0;
		gridBagConstraints_7.weightx = 1.0;
		gridBagConstraints_7.gridx = 0;
		gridBagConstraints_7.gridy = 1;
		command.add(js, gridBagConstraints_7);

		table = new JTable(tableModel);
		table.setRowHeight(25);
		table.getTableHeader().setReorderingAllowed(false);

		table.setGridColor(Color.GRAY);

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

		final JScrollPane scrollPane = new JScrollPane(table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getViewport().setBackground(Color.white);
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(5, 5, 0, 5);
		gridBagConstraints_3.fill = GridBagConstraints.BOTH;
		gridBagConstraints_3.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_3.weighty = 1.0;
		gridBagConstraints_3.weightx = 1.0;
		gridBagConstraints_3.gridx = 0;
		gridBagConstraints_3.gridy = 0;
		getContentPane().add(scrollPane, gridBagConstraints_3);
		//
		//
		// scrollPane.setViewportView(table);

		tableModel.addColumn("工序号");
		tableModel.addColumn("工序名称");
		tableModel.addColumn("所属组");

		Document document = frame.getCurrentTechnics();
		try {
			techElement = XmlUtility.getTechnicsElement(document);
			setTableValue(techElement);
			setGroupvalue(techElement);
		} catch (Exception e) {

			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "获取工艺信息节点时出现错误！", "提示",
					JOptionPane.INFORMATION_MESSAGE);
		}

		Editor groupEditor = new Editor(groupBox, table);
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(2).setCellEditor(groupEditor);

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getCellEditor() != null)
					table.getCellEditor().stopCellEditing();
				if(groupName == null || "".equals(groupName)) {
					try {
						groupName = TechnicsIntf.getUsertechnicsGroupName(techElement.attributeValue("partOid"));
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				System.out.println("groupName = " + groupName);
				boolean flag = false;
				boolean notAllFlag = false;
				for (int i = 0; i < table.getRowCount(); i++) {
					String seletedGroup = table.getValueAt(i, 2).toString();
					if (seletedGroup == null
							|| seletedGroup.trim().length() == 0) {
						JOptionPane.showMessageDialog(
								TechnicsCombineDialog.this.frame,
								"请选择所有工序合编的专业组！", "提示",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					if (groupName.equals(seletedGroup)) {
						flag = true;
					} else {
						notAllFlag = true;
					}
				}
				if (!flag) {
					JOptionPane.showMessageDialog(
							TechnicsCombineDialog.this.frame,
							"合编的专业组需包括您所在的专业组！", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if (!notAllFlag) {
					JOptionPane.showMessageDialog(
							TechnicsCombineDialog.this.frame,
							"合编的专业组需包括其它专业组！", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				System.out.println("工艺合编...................");
				String version = techElement.attributeValue("version");
				if (version == null || version.equals(""))
					version = "1.0";
				TechnicsCombineDialog.this.frame.saveProcess(techElement);
				try {
					HashMap<String, String> map = technicsUnite();
					String success = map.get("success");
					if (success.equals("success")) {
						String newVersion = map.get("version");
						String lifecycle = map.get("lifecycle");
						XmlUtility.setAttributeValue(techElement, "version",
								newVersion);
						XmlUtility.setAttributeValue(techElement, "lifecycle",
								lifecycle);
						TechnicsCombineDialog.this.frame
								.saveProcess(techElement);
						XWTreeNode rootNode = (XWTreeNode) TechnicsCombineDialog.this.frame
								.getXWPartTreePanel().getTree().getModel()
								.getRoot();
						TechnicsCombineDialog.this.frame.updateTreeTechnics(
								rootNode,
								techElement.attributeValue("partOid"),
								techElement.attributeValue("technicsName"),
								techElement.attributeValue("technicsCategory"),
								newVersion);
						TechnicsCombineDialog.this.frame.getXWPartTreePanel()
								.expandAllNode(rootNode);
						TechnicsCombineDialog.this.frame.technicsMasterJPanel
								.setUIValues(techElement);
						JOptionPane.showMessageDialog(
								TechnicsCombineDialog.this.frame, "启动工艺合编成功！",
								"提示", JOptionPane.INFORMATION_MESSAGE);
					} else {
						// version = new
						// StringBuffer(bigVersion).append(".").append(versionID).toString();
						// XMLUtil.setAttributeValue(techElement, "version",
						// version);
						// TechnicsCombineDialog.this.frame.saveProcess(techElement);
						String errorMessage = map.get("errorMessage");
						if (errorMessage != null && !errorMessage.equals("")) {
							JOptionPane.showMessageDialog(
									TechnicsCombineDialog.this.frame,
									errorMessage, "提示",
									JOptionPane.INFORMATION_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(
									TechnicsCombineDialog.this.frame,
									"工艺合编失败！", "提示",
									JOptionPane.INFORMATION_MESSAGE);
						}
					}
				} catch (Exception e1) {
					// if(!version.substring(version.lastIndexOf(".")+1).equals(versionID))
					// {
					// version = new
					// StringBuffer(bigVersion).append(".").append(versionID).toString();
					// XMLUtil.setAttributeValue(techElement, "version",
					// version);
					// TechnicsCombineDialog.this.frame.saveProcess(techElement);
					// }

					e1.printStackTrace();
					JOptionPane.showMessageDialog(
							TechnicsCombineDialog.this.frame, "工艺合编出现错误！",
							"提示", JOptionPane.INFORMATION_MESSAGE);
				} finally {
					dispose();
				}
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int) (dimension.getWidth() - 500) / 2,
				(int) (dimension.getHeight() - 400) / 2, 500, 400);
		if (table.getRowCount() < 1) {
			okButton.setEnabled(false);
		}
		setVisible(true);
	}

	private void setTableValue(Element techElement) {
		List<Element> list = XmlUtility.getAllSteps(techElement);
		for (int i = 0; i < list.size(); i++) {
			Element element = (Element) list.get(i);
			addOneRow();
			tableModel.setValueAt(element.attributeValue("stepNumber"), i, 0);
			tableModel.setValueAt(element.attributeValue("stepName"), i, 1);
			tableModel.setValueAt(element.attributeValue("responserGroup"), i,
					2);
		}
	}

	private void setGroupvalue(Element techElement) {
		String partOid = techElement.attributeValue("partOid");
		if (partOid == null)
			return;
		if(technicsGroupNames.isEmpty()) {
			try {
				technicsGroupNames = TechnicsIntf.getAlltechnicsGroupNames(partOid);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (int i = 0; i < technicsGroupNames.size(); i++) {
			groupBox.addItem(technicsGroupNames.get(i));
		}
	}

	private void addOneRow() {
		Vector<String> vector = new Vector<String>();
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			vector.add("");
		}
		tableModel.addRow(vector);
	}

	private HashMap<String, String> technicsUnite() throws Exception {
		String topPartOid = techElement.attributeValue("parentPartOid");
		String partOid = techElement.attributeValue("partOid");
		String pplanNumber = techElement.attributeValue("pplanNumber");
		String technicsName = WorkSpaceUtil.getTechnicsDirectory(techElement.attributeValue("technicsNumber"));
		int index = technicsName.lastIndexOf("\\");
		if (index >= 0) {
			technicsName = technicsName.substring((index + 1));
		}
		reSetGroup(techElement);
		String unite = techElement.attributeValue("unite");
		if (unite != null && unite.equals("common"))
			XmlUtility.setAttributeValue(techElement, "unite", "unite");
		else if (unite != null && unite.equals("route"))
			XmlUtility.setAttributeValue(techElement, "unite", "routeUnite");
		frame.saveProcess(techElement);
		byte[] technicsZip = FilesUtil.getTechnicsByte(techElement.attributeValue("technicsNumber"));
		String text = textPanel.getText();
		if (text == null)
			text = "";
		return TechnicsIntf.startTechnicsUnite(topPartOid, partOid, technicsName, technicsZip, text,pplanNumber);
	}

	private void reSetGroup(Element techElement) {
		List<Element> list = XmlUtility.getAllSteps(techElement);
		for (int i = 0; i < list.size(); i++) {
			Element element = (Element) list.get(i);
			System.out.println(element.attributeValue("responserGroup"));
			System.out.println(table.getValueAt(i, 2));
			if (!element.attributeValue("responserGroup").equals(
					table.getValueAt(i, 2))) {
				XmlUtility.setAttributeValue(element, "responserGroup", table
						.getValueAt(i, 2).toString());
				XmlUtility.setAttributeValue(element, "responser", "");
			}
		}
	}

	class Editor extends DefaultCellEditor {
		public Editor(JComboBox comboBox, JTable table) {
			super(comboBox);
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			if (value == null)
				value = "";

			groupBox.setSelectedItem(value.toString());
			return groupBox;
		}

		public Object getCellEditorValue() {
			if (groupBox != null) {
				Object value = groupBox.getSelectedItem();
				if (value == null)
					value = "";
				return value;
			} else
				return "";
		}
	}

	public void stopTableCellEditing() {
		if (table.getCellEditor() != null)
			table.getCellEditor().stopCellEditing();
	}

	class CombineTableModel extends DefaultTableModel {
		public boolean isCellEditable(int row, int column) {
			if (column == 2)
				return true;
			return false;
		}
	}
}