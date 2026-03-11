package com.glaway.mpm.mesDataSearch.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.dom4j.Document;
import org.dom4j.Element;

import com.glaway.mpm.mesDataSearch.helper.MesDataSearchProcesser;
import com.glaway.mpm.model.TempObject;
import com.glaway.mpm.qmIntf.technics.RelateMainMakeTechnicDialog;
import com.glaway.mpm.qmIntf.technics.entity.Technics;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.FilesUtil;
import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.util.TechnicsReleaseUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.NewTechnicsHistoryView;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.TechnicsMessageTreeObject;
import com.glaway.mpm.view.XWTechnicsTreeObject;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.view.XWTreeObject;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class MesDataTechnicSearchPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private VaLogger logger = VaLogger.getLogger(this.getClass());

	private JPanel mainPanel;
	private JPanel topPanel;
	private JPanel middlePanel;
	private JPanel middleMainPanel;
	private JPanel topMainPanel;

	private JLabel nameLabel;
	private JLabel numberLabel;
	private JLabel technicTypeLabel;

	private JTextField number;
	private JTextField name;
	private JComboBox technicType;

	private JButton searchButton;
	private JButton clearButton;

	private JScrollPane jScrollPane;
	private JTable jTable;

	private JButton sureButton;
	private JButton cancelButton;
	private JDialog dialog;
	private MesDataSearchMainFrame frame;
	private MesDataSearchMainPanel parent;
	private Technics technics;
	public MesDataTechnicSearchPanel(JDialog dialog, MesDataSearchMainFrame frame,MesDataSearchMainPanel parent) {
		this.frame = frame;
		this.parent = parent;
		this.dialog = dialog;
		init();
	}
	public MesDataTechnicSearchPanel(JDialog dialog, MesDataSearchMainFrame frame,Technics technics, Element technicElement) {
		this.frame = frame;
		this.dialog = dialog;
		this.technics = technics;
		init();
	}

	private void init() {
		initLookAndFeel();
		initDimension();
		initComponents();
		initLayout();
		initActions();
		loadInitDatas();
		initTableValues();
	}

	private void initLookAndFeel() {

	}

	private void initDimension() {

	}

	private void initComponents() {
		mainPanel = new JPanel();
		topPanel = new JPanel();
		middlePanel = new JPanel();
		topMainPanel = new JPanel();
		middleMainPanel = new JPanel();

		nameLabel = new JLabel();
		numberLabel = new JLabel();
		technicTypeLabel = new JLabel();

		searchButton = new JButton();

		number = new JTextField();
		name = new JTextField();
		technicType = new JComboBox();

		sureButton = new JButton();
		cancelButton = new JButton();
		clearButton = new JButton();

		jScrollPane = new JScrollPane();
		jTable = new JTable();
	}

	private void initLayout() {
		nameLabel.setPreferredSize(new Dimension(75, 25));
		numberLabel.setPreferredSize(new Dimension(75, 25));
		technicTypeLabel.setPreferredSize(new Dimension(75, 25));

		number.setPreferredSize(new Dimension(430, 25));
		name.setPreferredSize(new Dimension(430, 25));
		technicType.setPreferredSize(new Dimension(200, 25));

		searchButton.setPreferredSize(new Dimension(90, 25));

		clearButton.setPreferredSize(new Dimension(90, 25));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 0, 5, 5);
		c.gridy = 1;
		c.gridx = 1;
		topMainPanel.setLayout(new GridBagLayout());
		topMainPanel.add(numberLabel, c);
		c.gridx = 2;
		topMainPanel.add(number, c);
		c.gridx = 3;
		c.insets = new Insets(10, 3, 5, 5);
		topMainPanel.add(searchButton, c);

		c.insets = new Insets(10, 0, 5, 5);
		c.gridy = 2;
		c.gridx = 1;
		topMainPanel.add(nameLabel, c);
		c.gridx = 2;
		topMainPanel.add(name, c);
		c.gridx = 3;
		c.insets = new Insets(10, 3, 5, 5);
		topMainPanel.add(clearButton, c);

		c.insets = new Insets(10, 3, 5, 5);
		c.gridy = 3;
		c.gridx = 1;
		topMainPanel.add(technicTypeLabel, c);
		c.gridx = 2;
		topMainPanel.add(technicType, c);

//		topPanel.add(topMainPanel);

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 1;
		c.gridy = 1;

		middleMainPanel.setLayout(new GridBagLayout());
		jTable.setRowHeight(23);
		jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jTable.getTableHeader().setPreferredSize(new Dimension(20, 25));
		jScrollPane.setViewportView(jTable);
		jScrollPane.setPreferredSize(new Dimension(590, 270));
		middleMainPanel.add(jScrollPane, c);

		c.gridy = 2;

		sureButton.setPreferredSize(new Dimension(90, 25));
		cancelButton.setPreferredSize(new Dimension(90, 25));

		c.insets = new Insets(30, 390, 5, 0);
		middleMainPanel.add(sureButton, c);

		c.insets = new Insets(30, 490, 5, 20);
		middleMainPanel.add(cancelButton, c);

		middlePanel.add(middleMainPanel);

		mainPanel.setLayout(new BorderLayout(1, 2));
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(middlePanel, BorderLayout.CENTER);

		this.add(mainPanel);
	}
	public void initTableValues(){
//		String lukahao = frame.getLukahao();
//		String productNumber = frame.getProductNumber();
//		String batch = frame.getBatch();
//		String tuhao = frame.getTuhao();
//		List<TempObject> technicsNumberList = MesDataSearchProcesser.getTechnicsNumberList(lukahao, productNumber, batch, tuhao);
//		jTable.setModel(generatorModel(technicsNumberList));
//		loadTable();
	}

	private DefaultTableModel getModel(Object[][] tableValue) {
		DefaultTableModel model = new DefaultTableModel(tableValue,
				new String[] { "", "工艺编号", "工艺名称","工艺过程卡号", "路卡号" }) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return model;
	}

	private DefaultTableModel generatorModel(List<TempObject> list) {
		Object[][] tableValue = null;
		if (list == null || list.size() == 0) {
			tableValue = new Object[0][5];
		} else {
			tableValue = new Object[list.size()][5];
			for (int i = 0; i < list.size(); i++) {
				tableValue[i][0] = convertNull(list.get(i).getOid());
				tableValue[i][1] = convertNull(list.get(i).getName());
				tableValue[i][2] = convertNull(list.get(i).getName());
				tableValue[i][3] = convertNull(list.get(i).getType());
				tableValue[i][4] = convertNull(list.get(i).getVersion());
			}
		}
		return getModel(tableValue);
	}

	private void loadTable() {
		TableColumn column = jTable.getColumnModel().getColumn(0);
		column.setMaxWidth(0);
		column.setMinWidth(0);
		column.setPreferredWidth(0);
		column.setWidth(0);

		jTable.getColumnModel().getColumn(3).setMinWidth(10);
		jTable.getColumnModel().getColumn(3).setPreferredWidth(10);
	}

	private void initActions() {

		jTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1 && e.getClickCount() == 2) {
				}
			}

		});

		sureButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int row = jTable.getSelectedRow();
				String technicsNumber = (String) jTable.getValueAt(row, 1);
				String productNumber = (String) jTable.getValueAt(row, 3);
				String lukahao = (String) jTable.getValueAt(row, 4);
//				parent.getMainInfoPanel().setUIValues(technicsNumber, productNumber, lukahao);
				dialog.dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				technics = null;
				dialog.dispose();
			}
		});
	}

	private void loadInitDatas() {
		searchButton.setText("搜索");
		clearButton.setText("清除条件");
		nameLabel.setText("工艺名称");
		numberLabel.setText("工艺编号");
		technicTypeLabel.setText("工艺类型");
		sureButton.setText("确定");
		cancelButton.setText("取消");
		jTable.setModel(getModel(null));
		jTable.getTableHeader().setReorderingAllowed(false);
		String[] technicsTypes = LoadConfig.getInstance().getTechnicsType()[1];
		String[] ttypes = new String[technicsTypes.length+1];
		ttypes[0] = "全部";
		for(int i=0;i<technicsTypes.length;i++){
			ttypes[i+1] = technicsTypes[i];
		}
		technicType.setModel(new DefaultComboBoxModel(ttypes));
		loadTable();
	}

	private String convertNull(Object str) {
		return str == null ? "" : str.toString();
	}

	public Technics getBorrowTechnics(){
		return technics;
	}


	public static void openFile(String filePath) throws IOException {
        Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler   " + filePath);
    }

}
