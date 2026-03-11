package com.glaway.speciaword.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.glaway.speciaword.common.CommonHelper;
import com.glaway.speciaword.common.SvgTranscoderToPng;

public class SpecSymbolDialog extends SpecDialog {

	private JLabel typeJLabel = new JLabel();
	private JComboBox typeComb = new JComboBox();
	private JButton insertBtn = new JButton();
	private JButton cancelBtn = new JButton();
	private JScrollPane scrollPane = new JScrollPane();
	private JPanel selectJPanel = new JPanel();
	private MyModel model;
	private JTable speTable = new JTable();

	private JLabel imgLab = new JLabel();

	private String imageSrc = null;

	private int columnCount = 8;
	private int rowCount = 6;

	private String[] symbolType = { "焊接符号", "包容条件符号", "其他符号" };

	public SpecSymbolDialog(String category, String imageFolder) {
		super(category,imageFolder);
		setTitle("特殊符号");
		setModal(true);
		getContentPane().setLayout(null);
		try {
			TitledBorder selectBorder = new TitledBorder("选择区");
			this.selectJPanel.setBorder(selectBorder);
			this.selectJPanel.setLayout(null);
			this.selectJPanel.setBounds(10, 10, 365, 332);
			getContentPane().add(this.selectJPanel);
			this.selectJPanel.setMaximumSize(new Dimension(315, 2147483647));
			this.selectJPanel.setMinimumSize(new Dimension(90, 0));
			this.selectJPanel.setPreferredSize(new Dimension(180, 320));

			this.typeComb.setPreferredSize(new Dimension(60, 22));
			this.typeComb.setBounds(38, 22, 299, 22);
			this.selectJPanel.add(this.typeComb);
			this.selectJPanel.add(this.typeJLabel);
			this.typeJLabel.setBounds(183, 229, 6, 15);

			this.imgLab.setBackground(Color.white);
			this.imgLab.setBounds(148, 237, 65, 65);
			this.selectJPanel.add(this.imgLab);
			this.imgLab.setBorder(BorderFactory.createLoweredBevelBorder());
			this.imgLab.setMaximumSize(new Dimension(40, 40));
			this.imgLab.setMinimumSize(new Dimension(40, 40));
			this.imgLab.setOpaque(true);
			this.imgLab.setPreferredSize(new Dimension(40, 40));
			this.imgLab.setHorizontalAlignment(0);
			this.imgLab.setIcon(null);

			this.scrollPane.setHorizontalScrollBarPolicy(31);

			this.scrollPane.setBounds(31, 59, 306, 172);
			this.scrollPane.setMinimumSize(new Dimension(80, 150));
			this.scrollPane.setPreferredSize(new Dimension(80, 150));
			this.scrollPane.getViewport().add(this.speTable, null);
			this.speTable.setRowMargin(0);

			this.scrollPane.setViewportView(this.speTable);
			this.selectJPanel.add(this.scrollPane);
			this.speTable.setCellSelectionEnabled(true);
			this.speTable.setPreferredScrollableViewportSize(new Dimension(80,
					150));

			this.speTable.getTableHeader().setReorderingAllowed(false);
			this.speTable.getTableHeader().setResizingAllowed(false);

			this.speTable.getTableHeader().setMaximumSize(new Dimension(1, 0));
			this.speTable.getTableHeader().setMinimumSize(new Dimension(1, 0));
			this.speTable.getTableHeader()
					.setPreferredSize(new Dimension(1, 0));

			this.speTable.setRowHeight(36);

			this.speTable.setSelectionMode(0);

			this.speTable.setGridColor(Color.black);
			this.typeComb.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					typeComb_itemStateChanged();
				}
			});

			this.speTable.getColumnModel().addColumnModelListener(
					new TableColumnModelListener() {
						public void columnAdded(TableColumnModelEvent e) {
						}

						public void columnRemoved(TableColumnModelEvent e) {
						}

						public void columnMoved(TableColumnModelEvent e) {
						}

						public void columnMarginChanged(ChangeEvent e) {
						}

						public void columnSelectionChanged(ListSelectionEvent e) {
							SpecSymbolDialog.this.table_valueChanged();
						}
					});

			this.speTable.getSelectionModel().addListSelectionListener(
					new ListSelectionListener() {
						public void valueChanged(ListSelectionEvent e) {
							SpecSymbolDialog.this.table_valueChanged();
						}
					});

			this.insertBtn.setMaximumSize(new Dimension(65, 23));
			this.insertBtn.setBounds(261, 274, 75, 22);
			this.selectJPanel.add(this.insertBtn);
			this.insertBtn.setMinimumSize(new Dimension(65, 23));
			this.insertBtn.setPreferredSize(new Dimension(65, 23));
			this.insertBtn.setText("插入");

			this.insertBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SpecSymbolDialog.this.insertBtn_actionPerformed(e);
				}
			});

			this.cancelBtn.setMaximumSize(new Dimension(65, 23));
			this.cancelBtn.setBounds(261, 300, 75, 22);
			this.selectJPanel.add(this.cancelBtn);
			this.cancelBtn.setMinimumSize(new Dimension(65, 23));
			this.cancelBtn.setPreferredSize(new Dimension(65, 23));
			this.cancelBtn.setText("取消");

			this.cancelBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SpecSymbolDialog.this.dlgCloseAction();
				}
			});

			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		setSize(395, 400);
		setResizable(false);
		setTitle("符号");

		initComponent();
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				SpecSymbolDialog.this.dlgCloseAction();
			}
		});
	}

	private void initComponent() {
		this.model = new MyModel();
		this.speTable.setModel(this.model);
		this.model.setColumnCount(columnCount);
		this.model.setRowCount(rowCount);

		MyCellRenderer cellRenderer = new MyCellRenderer();
		for (int i = 0; i < 8; i++) {
			this.speTable.getColumnModel().getColumn(i)
					.setCellRenderer(cellRenderer);
		}

		for (String temp : symbolType) {
			this.typeComb.addItem(temp);
		}

		this.typeComb.setSelectedIndex(0);
		initTable(0);

	}

	private void initTable(int index) {
		speTable.clearSelection();
		this.imgLab.setIcon(new ImageIcon());
		List<String> list = CommonHelper.getSpecWordNames(index);
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < columnCount; j++) {
				String value;
				int m = i * columnCount + j;
				if (m < list.size()) {
					value = list.get(m);
				} else {
					value = "";
				}
				model.setValueAt(value, i, j);
			}
		}
	}

	private void table_valueChanged() {
		int selectedCol = this.speTable.getSelectedColumn();
		int selectedRow = this.speTable.getSelectedRow();
		if (selectedCol != -1 && selectedRow != -1) {
			int index = typeComb.getSelectedIndex();
			Object value = this.speTable.getValueAt(selectedRow, selectedCol);
			if (value != null && !"".equals(value) && index != -1) {
				this.imgLab.setIcon(new ImageIcon(CommonHelper
						.getResourceImage(getCategory() + "/"
								+ CommonHelper.hashMap.get(index), value
								+ ".png")));
			}
		}
	}

	private void typeComb_itemStateChanged() {
		initTable(typeComb.getSelectedIndex());
	}

	private final SvgTranscoderToPng transcoder = SvgTranscoderToPng
			.getInstance();

	void insertBtn_actionPerformed(ActionEvent e) {
		int selectedCol = this.speTable.getSelectedColumn();
		int selectedRow = this.speTable.getSelectedRow();
		if (selectedCol != -1 && selectedRow != -1) {
			int index = typeComb.getSelectedIndex();
			Object value = this.speTable.getValueAt(selectedRow, selectedCol);
			if (value != null && !"".equals(value) && index != -1) {
				Map<String, Object> map = new HashMap<String, Object>();
				imageSrc = CommonHelper.saveImageToLocal(
						transcoder.makeImage(getCategory() + "/"
								+ CommonHelper.hashMap.get(index),
								value.toString(), map),
						"" + System.currentTimeMillis(),getImageFolder());
				dispose();
			}
		}
	}

	public void setVisible(boolean flag) {
		showLocation();
		super.setVisible(flag);
	}

	private void showLocation() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
	}

	void dlgCloseAction() {
		setVisible(false);

	}

	class MyCellRenderer extends DefaultTableCellRenderer {
		public MyCellRenderer() {
			setHorizontalAlignment(0);
		}

		public void setValue(Object value) {
			Icon icon;
			icon = new ImageIcon();
			if (value != null && !"".equals(value)) {
				try {
					icon = new ImageIcon(CommonHelper.getResourceImage(
							getCategory()
									+ "/"
									+ CommonHelper.hashMap.get(typeComb
											.getSelectedIndex()),
							value.toString() + ".png"));
				} catch (Exception e) {

				}
			}
			setIcon(icon);
		}
	}

	class MyModel extends DefaultTableModel {
		public MyModel() {

		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
	}

	public class MyTextField extends JTextField {
		final KeyStroke enterKey = KeyStroke.getKeyStroke(10, 0);
		final KeyStroke delKey = KeyStroke.getKeyStroke(127, 0);
		final KeyStroke backspaceKey = KeyStroke.getKeyStroke(8, 0);
		final KeyStroke leftKey = KeyStroke.getKeyStroke(37, 0);
		final KeyStroke rightKey = KeyStroke.getKeyStroke(39, 0);

		public MyTextField() {
		}

		protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
				int condition, boolean pressed) {
			if (getText().length() > 4) {
				if ((!ks.equals(this.delKey))
						&& (!ks.equals(this.backspaceKey))
						&& (!ks.equals(this.leftKey))
						&& (!ks.equals(this.rightKey))) {
					return false;
				}
			}
			return super.processKeyBinding(ks, e, condition, pressed);
		}
	}

	public String getImageSrc() {
		return imageSrc;
	}
}