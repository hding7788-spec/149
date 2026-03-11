package com.glaway.mpm.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
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

import org.dom4j.Element;

import wt.doc.WTDocument;

import com.glaway.mpm.mesParameter.helper.MesParameterProcessor;
import com.glaway.mpm.mesParameter.model.Bdpsndoc;
import com.glaway.mpm.parameter.designui.NewCommonParamTablePanel;
import com.glaway.mpm.parameter.designui.NewSpecialParamTablePanel;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TemplateIntf;

public class LargeFileSearchPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private VaLogger logger = VaLogger.getLogger(this.getClass());

	private JPanel mainPanel;
	private JPanel topPanel;
	private JPanel middlePanel;
	private JPanel middleMainPanel;
	private JPanel topMainPanel;
	private Container parent;

	private JLabel nameLabel;

	private JTextField name;

	private JButton searchButton;

	private JScrollPane jScrollPane;
	private JTable jTable;

	private JButton sureButton;
	private JButton cancelButton;
	private JDialog dialog;
	private Element stepElement;
	public LargeFileSearchPanel(JDialog dialog,Container parent, Element stepElement) {
		this.dialog = dialog;
		this.parent = parent;
		this.stepElement = stepElement;
		init();
	}

	private void init() {
		initLookAndFeel();
		initDimension();
		initComponents();
		initLayout();
		initActions();
		loadInitDatas();
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

		searchButton = new JButton();

		name = new JTextField();
		sureButton = new JButton();
		cancelButton = new JButton();

		jScrollPane = new JScrollPane();
		jTable = new JTable();
	}

	private void initLayout() {
		name.setPreferredSize(new Dimension(430, 25));
		nameLabel.setPreferredSize(new Dimension(75, 25));

		searchButton.setPreferredSize(new Dimension(90, 25));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 1;
		c.gridy = 1;
		topMainPanel.setLayout(new GridBagLayout());
		topMainPanel.add(nameLabel, c);
		c.gridx = 2;
		topMainPanel.add(name, c);
		c.gridx = 3;
		c.insets = new Insets(10, 3, 5, 5);
		topMainPanel.add(searchButton, c);

		topPanel.add(topMainPanel);

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

	private DefaultTableModel getModel(Object[][] tableValue) {
		DefaultTableModel model = new DefaultTableModel(tableValue,
				new String[] { "", "序号", "文件名称","文件编号" }) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return model;
	}

	private DefaultTableModel generatorModel(List<WTDocument> list) {
		Object[][] tableValue = null;
		if (list == null || list.size() == 0) {
			tableValue = new Object[0][5];
		} else {
			tableValue = new Object[list.size()][5];
			for (int i = 0; i < list.size(); i++) {
				tableValue[i][1] = convertNull(i + 1);
				tableValue[i][2] = convertNull(list.get(i).getName());
				tableValue[i][3] = convertNull(list.get(i).getNumber());
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
		jTable.getColumnModel().getColumn(3).setMinWidth(0);
		jTable.getColumnModel().getColumn(3).setMaxWidth(0);
	}

	private void initActions() {

		jTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1 && e.getClickCount() == 2) {
					getSureButton().doClick();
				}
			}
		});

		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String nameValue = CommonUtil.trim(name.getText());
				nameValue = "%" + nameValue + "%";
				logger.debug( " name:" + nameValue);
				List<WTDocument> list = TemplateIntf.getAllLargeFileDoc(nameValue);
				if (list != null) {
					jTable.setModel(generatorModel(list));
					loadTable();
				}
				if (list == null || list.size() == 0) {
					JOptionPane.showMessageDialog(dialog, "搜索结果为空", "提示", JOptionPane.OK_OPTION);
				}
			}
		});
		sureButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int row = jTable.getSelectedRow();
				if(row == -1){
					JOptionPane.showMessageDialog(dialog, "请选择相应的文件", "提示", JOptionPane.OK_OPTION);
				}else{
					String name = (String) jTable.getValueAt(row, 2);
					String number = (String) jTable.getValueAt(row, 3);
					if(parent instanceof LargeFileJPanel){
						LargeFileJPanel largeFilePanel = (LargeFileJPanel) parent;
						largeFilePanel.addToTable(name, number);
						largeFilePanel.setTabTitle();
						largeFilePanel.saveToXml(number, name);
					}
					dialog.dispose();
				}
			}
		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
	}

	private void loadInitDatas() {
		searchButton.setText("搜索");
		nameLabel.setText("文件名称");
		sureButton.setText("确定");
		cancelButton.setText("取消");
		jTable.setModel(getModel(null));
		jTable.getTableHeader().setReorderingAllowed(false);
//		jTable.getTableHeader().setResizingAllowed(false);
		//String [] technicsTypes = {"全部","典型工艺","通用工艺","国家标准"};//;LoadConfig.getInstance().getTechnicsType()[1];
//		String [] ttypes = new String[technicsTypes.length+1];
//		ttypes[0] = "请选择工艺类型";
//		for(int i=0;i<technicsTypes.length;i++){
//			ttypes[i+1] = technicsTypes[i];
//		}
		loadTable();
	}

	private String convertNull(Object str) {
		return str == null ? "" : str.toString();
	}

	public JButton getSureButton() {
		return sureButton;
	}

}
