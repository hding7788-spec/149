package com.glaway.mpm.qmIntf.symbol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.JavaUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.visual.log.VaLogger;

public class SymbolPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private VaLogger logger = VaLogger.getLogger(this.getClass());

	public JTable jTable;
	private JScrollPane jScrollPane;
	private CommonObservable observable;

	public SymbolPanel() {
		setBorder(new TitledBorder(null, "选择符号",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		this.setLayout(new BorderLayout());
		jTable = new JTable();
		jScrollPane = new JScrollPane(jTable);
		jScrollPane.setPreferredSize(new Dimension(250, 152));

		this.jTable.getTableHeader().setReorderingAllowed(false);
		this.jTable.getTableHeader().setResizingAllowed(false);
		this.jTable.getTableHeader().setMaximumSize(new Dimension(1, 0));
		this.jTable.getTableHeader().setMinimumSize(new Dimension(1, 0));
		this.jTable.getTableHeader().setPreferredSize(new Dimension(1, 0));
		this.jTable.setRowHeight(25);

		this.jTable.setCellSelectionEnabled(true);
		this.jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.jTable.setGridColor(Color.black);
		jTable.getTableHeader().setVisible(false);
		jTable.setModel(generatorModel());
		this.add(jScrollPane);
	}

	public SymbolPanel(boolean flag) {
		this();
		observable = new CommonObservable(0);
		jTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int column = jTable.getSelectedColumn();
					int row = jTable.getSelectedRow();
					if (column != -1 && row != -1) {
						String value = (String) jTable.getValueAt(row, column);
						if (value != null) {
							observable.setChanged();
							observable.notifyObservers(value);
						}
					}
				}
			}
		});
	}

	public void addObserver(Observer o) {
		observable.addObserver(o);
		logger.debug(o + " has signed to symbol");
	}

	public void deleteObservers() {
		observable.deleteObservers();
		logger.debug("delete all obserbers");
	}

	public void refreshTable() {
		jTable.setModel(generatorModel());
	}

	private DefaultTableModel getModel(Object[][] tableValue) {
		DefaultTableModel model = new DefaultTableModel(tableValue,
				new String[] { "", "", "", "", "" }) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return model;
	}

	public DefaultTableModel generatorModel() {
		Object[][] tableValue = new Object[6][5];
		BufferedReader br = null;
		try {
			String path = WorkSpaceUtil.getPersonalTerminologyDirectory();
			File file = new File(path + "symbol.properties");
			if (!file.exists()) {
				FileUtil.copyFile(FileUtil.class
						.getResourceAsStream("/resource/symbol.properties"),
						path + "symbol.properties");
			}
			br = FileUtil.getBufferedReaderByDirectPath(path
					+ "symbol.properties", "utf-8");
		} catch (Exception e1) {
			e1.printStackTrace();
			return getModel(tableValue);
		}
		List<String> list = new ArrayList<String>();
		try {
			String line = br.readLine();
			while (line != null) {
				list.add(line);
				line = br.readLine();
			}
			int column = list.size() / 5 + 1;
			if (column > 5) {
				tableValue = new Object[column][5];
			}
			int i = 0;
			int j = 0;
			for (String temp : list) {
				if (j == 4) {
					tableValue[i][j] = temp;
					i++;
					j = 0;
				} else {
					tableValue[i][j] = temp;
					j++;
				}
			}
			getModel(tableValue);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			JavaUtil.closeStream(br);
		}
		return getModel(tableValue);
	}
}
