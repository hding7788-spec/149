package com.glaway.mpm.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import org.dom4j.Document;

import com.borland.dx.sql.dataset.Load;
import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.util.SearchTool;
import com.glaway.mpm.util.WorkSpaceUtil;

public class NewTechnicsSearchJDialog extends JDialog {
	private JFrame frame;

	private JLabel technicsNumberLabel = new JLabel("工艺编号");
	private JLabel partNumberLabel = new JLabel("零件编号");
	private JLabel technicsTypeLabel = new JLabel("工艺种类");
	private JLabel productNumberLabel = new JLabel("产品编号");
	private JTextField technicsNumberField = new JTextField();
	private JTextField partNumberField = new JTextField();
	private JComboBox technicsTypeBox = new JComboBox();
	private JComboBox productNumberBox = new JComboBox();

	private JButton searchButton = new JButton("搜索");
	private JButton clearButton = new JButton("清除条件");
	private JButton selecteAllButton = new JButton("选中全部");
	private JButton okButton = new JButton("确定");
	private JButton cancelButton = new JButton("取消");

	private DefaultTableModel tableModel = new DefaultTableModel() {
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	private JTable table = new JTable(tableModel);

	private Vector<Document> documents = new Vector<Document>();

	public NewTechnicsSearchJDialog(JFrame parent) {
		super(parent, true);
		frame = parent;
		// setModal(true);
		setTitle("搜索本地工艺");

		Container container = getContentPane();
		container.setLayout(new GridBagLayout());
		container.add(technicsNumberLabel, new GridBagConstraints(0, 0, 1, 1,
				0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(10, 10, 5, 5), 0, 0));
		container.add(technicsNumberField, new GridBagConstraints(1, 0, 1, 1,
				1.0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(10, 5, 5, 5), 0, 0));
		searchButton.setPreferredSize(new Dimension(80, 23));
		searchButton.setMinimumSize(new Dimension(80, 23));
		searchButton.setMaximumSize(new Dimension(80, 23));
		container.add(searchButton, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						10, 5, 5, 10), 0, 0));

		container.add(partNumberLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,
						10, 5, 5), 0, 0));
		container.add(partNumberField, new GridBagConstraints(1, 1, 1, 1, 1.0,
				0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		clearButton.setPreferredSize(new Dimension(80, 23));
		clearButton.setMinimumSize(new Dimension(80, 23));
		clearButton.setMaximumSize(new Dimension(80, 23));
		container.add(clearButton, new GridBagConstraints(2, 1, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 5, 10), 0, 0));

		container.add(technicsTypeLabel, new GridBagConstraints(0, 2, 1, 1, 0,
				0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 10, 5, 5), 0, 0));
		container.add(technicsTypeBox, new GridBagConstraints(1, 2, 1, 1, 1.0,
				0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		String[][] types = LoadConfig.getInstance().getTechnicsType();
		for(int i=0;i<types[1].length;i++){
			technicsTypeBox.addItem(types[1][i]);
		}
//		technicsTypeBox.addItem(WorkSpaceUtil.ASM_TYPE);
//		technicsTypeBox.addItem(WorkSpaceUtil.MOUNT_TYPE);
//		technicsTypeBox.addItem(WorkSpaceUtil.PAINT_TYPE);
//		technicsTypeBox.addItem(WorkSpaceUtil.MOUNT_TYPE);

		technicsTypeBox.setSelectedIndex(0);
		// container.add(productNumberLabel,new GridBagConstraints(0, 3, 1, 1,
		// 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,
		// 10, 5, 5), 0, 0));
		// container.add(productNumberBox,new GridBagConstraints(1, 3, 1, 1,
		// 1.0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new
		// Insets(5, 5, 5, 5), 0, 0));

		// try
		// {
		// addProductNumber();
		// } catch (Exception e1)
		// {
		//
		// JOptionPane.showMessageDialog(this, "查找产品编号时出错！");
		// return;
		// }

		addTableColumn();
		table.setGridColor(Color.GRAY);
		JScrollPane pane = new JScrollPane(table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		container.add(pane, new GridBagConstraints(0, 4, 3, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						5, 10, 5, 10), 0, 0));
		table.setSelectionMode(0);
		table.setRowHeight(25);
		table.getTableHeader().setReorderingAllowed(false);
		pane.getViewport().setBackground(Color.white);
		table.setGridColor(Color.GRAY);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		container.add(buttonPanel, new GridBagConstraints(0, 5, 3, 1, 1.0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0));
		buttonPanel.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0,
				0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 0, 5, 5), 0, 0));
		selecteAllButton.setPreferredSize(new Dimension(80, 23));
		selecteAllButton.setMinimumSize(new Dimension(80, 23));
		selecteAllButton.setMaximumSize(new Dimension(80, 23));
		// buttonPanel.add(selecteAllButton,new GridBagConstraints(1, 0, 1, 1,
		// 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new
		// Insets(5, 5, 5, 5), 0, 0));
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

		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchProcess();
			}
		});

		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearProcess();
			}
		});

		selecteAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selecteAllProcess();
			}
		});

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okProcess();
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelProcess();
			}
		});

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int) (dimension.getWidth() - 750) / 2,
				(int) (dimension.getHeight() - 500) / 2, 750, 500);
		setVisible(true);
	}

	// private void addProductNumber() throws Exception
	// {
	// String[] s = WorkSpaceUtil.getProductDictoryNumbers();
	// if(s != null)
	// {
	// for(int i=0;i<s.length;i++)
	// {
	// productNumberBox.addItem(s[i]);
	// }
	// productNumberBox.setSelectedIndex(0);
	// }
	// }

	private void addTableColumn() {
		tableModel.addColumn("编号");
		tableModel.addColumn("名称");
		tableModel.addColumn("种类");
		tableModel.addColumn("零件编号");
		tableModel.addColumn("产品编号");
	}

	private void searchProcess() {
		// 获得搜索条件信息
		String technicsNumber = technicsNumberField.getText().trim();
		String partNumber = partNumberField.getText().trim();
		if (technicsNumber.equals("")) {
			JOptionPane.showMessageDialog(frame, "工艺编号不能为空！");
			return;
		}
		String technicsType = String.valueOf(technicsTypeBox.getSelectedItem());
		// String productNumber =
		// String.valueOf(productNumberBox.getSelectedItem());
		// 定义搜索结果集合
		Vector vector = new Vector();
		String[] s = null;
		try {
			// 获得工艺目录下所有工艺信息文件夹字符串
			s = WorkSpaceUtil.getTechnicsDirectoryNames();
		} catch (Exception e) {

			JOptionPane.showMessageDialog(frame, "获得工艺目录时出错！");
		}
		if (s == null) {
			JOptionPane.showMessageDialog(frame, "工艺目录下不存在工艺信息！");
			return;
		}
		for (int i = 0; i < s.length; i++) {
			// 将符合搜索结果的字符串放入搜索集合中
			if (SearchTool.isResult(s[i], technicsNumber, technicsType,
					partNumber, null))
				vector.add(s[i]);
		}
		if (vector.size() == 0) {
			if (table.getRowCount() > 0)
				tableModel.setRowCount(0);
			JOptionPane.showMessageDialog(frame, "搜索的结果为空！");
			return;
		}
		// 将结果集的数据添加到结果表格中
		addResultToTable(vector);
	}

	private void clearProcess() {
		technicsNumberField.setText("");
		partNumberField.setText("");
		technicsTypeBox.setSelectedIndex(0);
		// productNumberBox.setSelectedIndex(0);
	}

	private void selecteAllProcess() {
		table.selectAll();
	}

	private void okProcess() {
		Vector technicsNumbers = getSelectedTechnicsNumber();
		if (technicsNumbers.size() == 0) {
			JOptionPane.showMessageDialog(frame, "请在工艺结果列表中选择一个工艺！");
			return;
		}
		try {
			for (int i = 0; i < technicsNumbers.size(); i++) {
				documents
						.add(WorkSpaceUtil
								.getTechnicsDocumentByTechnicsNumber((String) technicsNumbers
										.get(i)));
			}
		} catch (Exception e) {

			JOptionPane.showMessageDialog(frame, "获得工艺Document时出错！");
			dispose();
		}
		dispose();
	}

	private void cancelProcess() {
		dispose();
	}

	private void addResultToTable(Vector vector) {
		if (table.getRowCount() > 0)
			tableModel.setRowCount(0);
		for (int i = 0; i < vector.size(); i++) {
			Vector vec = new Vector();
			String s[] = vector.get(i).toString()
					.split(WorkSpaceUtil.SEPARATOR);
			for (int j = 0; j < tableModel.getColumnCount(); j++) {
				vec.add(s[j]);
			}
			tableModel.addRow(vec);
		}
	}

	private Vector getSelectedTechnicsNumber() {
		Vector vector = new Vector();
		int[] rows = table.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			vector.add(table.getValueAt(rows[i], 0));
		}
		return vector;
	}

	public Vector<Document> getDocuments() {
		return documents;
	}
}