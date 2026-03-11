package com.glaway.mpm.qmIntf.technics;

import com.glaway.mpm.model.TempObject;
import com.glaway.mpm.qmIntf.technics.entity.Technics;
import com.glaway.mpm.util.*;
import com.glaway.mpm.view.NewTechnicsHistoryView;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.TechnicsStepJPanel_XW;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class BorrowTechnicsSearchPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private VaLogger logger = VaLogger.getLogger(this.getClass());

	private Container parentPanel;
	private JPanel mainPanel;
	private JPanel topPanel;
	private JPanel middlePanel;
	private JPanel middleMainPanel;
	private JPanel topMainPanel;

	private JLabel nameLabel;
	private JLabel numberLabel;
	private JLabel typeLabel;

	private JTextField number;
	private JTextField name;
	private JComboBox jComboBox1;

	private JButton searchButton;
	private JButton clearButton;

	private JScrollPane jScrollPane;
	private JTable jTable;

	private JButton sureButton;
	private JButton borrowTechnicsButton;
	private JButton cancelButton;
	private JDialog dialog;
	private NewTechnicsPart frame;
	private Technics technics;
	public BorrowTechnicsSearchPanel(JDialog dialog, NewTechnicsPart frame) {
		this.frame = frame;
		this.dialog = dialog;
		init();
	}
	public BorrowTechnicsSearchPanel(JDialog dialog, NewTechnicsPart frame,Technics technics,Container parentPanel) {
		this.frame = frame;
		this.dialog = dialog;
		this.technics = technics;
		this.parentPanel = parentPanel;
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
		numberLabel = new JLabel();
		typeLabel = new JLabel();

		jComboBox1 = new JComboBox();

		searchButton = new JButton();

		number = new JTextField();
		name = new JTextField();
		sureButton = new JButton();
		borrowTechnicsButton = new JButton();
		cancelButton = new JButton();
		clearButton = new JButton();

		jScrollPane = new JScrollPane();
		jTable = new JTable();
	}

	private void initLayout() {
		number.setPreferredSize(new Dimension(430, 25));
		name.setPreferredSize(new Dimension(430, 25));
		nameLabel.setPreferredSize(new Dimension(75, 25));
		numberLabel.setPreferredSize(new Dimension(75, 25));
		jComboBox1.setPreferredSize(new Dimension(200, 25));
		typeLabel.setPreferredSize(new Dimension(75, 25));

		searchButton.setPreferredSize(new Dimension(90, 25));

		clearButton.setPreferredSize(new Dimension(90, 25));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 1;
		c.gridy = 1;
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

		c.insets = new Insets(10, 0, 5, 5);
		c.gridy = 3;
		c.gridx = 1;
		topMainPanel.add(typeLabel, c);

		jComboBox1.setPreferredSize(new Dimension(150, 25));
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 2;
		topMainPanel.add(jComboBox1, c);

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
		borrowTechnicsButton.setPreferredSize(new Dimension(90, 25));
		cancelButton.setPreferredSize(new Dimension(90, 25));

		c.insets = new Insets(30, 290, 5, 0);
		middleMainPanel.add(borrowTechnicsButton, c);

		c.insets = new Insets(30, 390, 5, 0);
		middleMainPanel.add(sureButton, c);

		c.insets = new Insets(30, 490, 5, 20);
		middleMainPanel.add(cancelButton, c);

		middlePanel.add(middleMainPanel);

		mainPanel.setLayout(new BorderLayout(1, 2));
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(middlePanel, BorderLayout.CENTER);
		if(technics==null){
			borrowTechnicsButton.setVisible(false);
		}else{
			borrowTechnicsButton.setVisible(true);
		}

		this.add(mainPanel);
	}

	private DefaultTableModel getModel(Object[][] tableValue) {
		DefaultTableModel model = new DefaultTableModel(tableValue,
				new String[] { "", "工艺编号", "工艺名称", "工艺类型", "文档编号", "版本" }) {
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
			tableValue = new Object[list.size()][6];
			for (int i = 0; i < list.size(); i++) {
				tableValue[i][0] = convertNull(list.get(i).getOid());
				tableValue[i][1] = convertNull(list.get(i).getNumber());
				tableValue[i][2] = convertNull(list.get(i).getName());
				tableValue[i][3] = convertNull(list.get(i).getType());
				tableValue[i][4] = convertNull(list.get(i).getDocNumber());
				tableValue[i][5] = convertNull(list.get(i).getVersion());
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
		jTable.getColumnModel().getColumn(4).setMinWidth(10);
		jTable.getColumnModel().getColumn(4).setPreferredWidth(10);
		jTable.getColumnModel().getColumn(5).setMinWidth(0);
		jTable.getColumnModel().getColumn(5).setMaxWidth(0);
	}

	private void initActions() {

		jTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1 && e.getClickCount() == 2) {
					openTechnics2();
				}
			}

		});

		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String numberValue = CommonUtil.trim(number.getText());
				String nameValue = CommonUtil.trim(name.getText());
				String type = String.valueOf(jComboBox1.getSelectedItem());
				logger.debug("number：" + numberValue + " name:" + nameValue + " type:" + type);
				List<TempObject> list = new ArrayList<TempObject>();
				try {
					if(parentPanel instanceof  TechnicsStepJPanel_XW){
						list = TechnicsIntf.searchTechnicsAttributes2(numberValue, nameValue, type,true);
					}else{
						list = TechnicsIntf.searchTechnicsAttributes2(numberValue, nameValue, type,false);
					}
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				logger.debug("搜索工艺编号和名称====================" + list);
				if (list != null) {
					jTable.setModel(generatorModel(list));
					loadTable();
				}
				if (list == null || list.size() == 0) {
					JOptionPane.showMessageDialog(dialog, "搜索结果为空", "提示", JOptionPane.OK_OPTION);
				}
			}
		});
		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				name.setText("");
				number.setText("");
				if (jComboBox1.getSelectedObjects().length != 0) {
					jComboBox1.setSelectedIndex(0);
				}
			}
		});
		sureButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				openTechnics2();
			}
		});

		borrowTechnicsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				borrowTechnics();

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
		typeLabel.setText("工艺类型");
		sureButton.setText("查看");
		borrowTechnicsButton.setText("添加");
		cancelButton.setText("取消");
		jTable.setModel(getModel(null));
		jTable.getTableHeader().setReorderingAllowed(false);
//		jTable.getTableHeader().setResizingAllowed(false);
		String [] technicsTypes;//;LoadConfig.getInstance().getTechnicsType()[1];
		if(parentPanel instanceof TechnicsStepJPanel_XW){
			technicsTypes = new String[]{"全部", "典型工艺", "通用工艺", "国家标准"};
		}else{
			technicsTypes = new String[]{"全部", "典型工艺", "通用工艺", "国家标准", "结构化典型工艺"};
		}
//		String [] ttypes = new String[technicsTypes.length+1];
//		ttypes[0] = "请选择工艺类型";
//		for(int i=0;i<technicsTypes.length;i++){
//			ttypes[i+1] = technicsTypes[i];
//		}
		jComboBox1.setModel(new DefaultComboBoxModel(technicsTypes));
		loadTable();
	}

	private String convertNull(Object str) {
		return str == null ? "" : str.toString();
	}


	private void borrowTechnics(){
		technics = new Technics();
		int row = jTable.getSelectedRow();
		if (row == -1) {
			SwingUtil.showMessageDialog("请选择工艺", "提示", 2);
			technics = null;
		} else {
			String oid = (String) jTable.getValueAt(row, 0);
			String technicsNumber = (String) jTable.getValueAt(row, 1);
			String technicsName = (String)jTable.getValueAt(row, 2);
			String technicsType = (String)jTable.getValueAt(row, 3);
			String docNumber = (String)jTable.getValueAt(row, 4);
			String version = (String)jTable.getValueAt(row, 5);

			technics.setTechnicsName(technicsName);
			technics.setTechnicsNumber(technicsNumber);
			technics.setTechnicsType(technicsType);
			technics.setOid(oid);
			technics.setDocNumber(docNumber);
			technics.setVersion(version);

			dialog.dispose();
		}
	}

	public Technics getBorrowTechnics(){
		return technics;
	}

	private void openTechnics2() {
		int row = jTable.getSelectedRow();
		if (row == -1) {
			SwingUtil.showMessageDialog("请选择工艺", "提示", 2);
		} else {
			Object obj = jTable.getValueAt(row, 4);
			String number = obj == null ? "" : obj.toString().trim();
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			try {
				byte[] bytes = null;
				try {
					bytes = TechnicsIntf.getPrintPdf(number);
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(bytes != null) {
					String tempFile = WorkSpaceUtil.getWorkSpace() + File.separator + "technics" + File.separator + number+".pdf";
					fos = new FileOutputStream(tempFile);
					bos = new BufferedOutputStream(fos);

					bos.write(bytes);

					bos.close();
					fos.close();

					openFile(tempFile);
				} else {
					SwingUtil.showMessageDialog("没有找到主内容为pdf的工艺文件", "提示", 2);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(bos != null) {
					try {
						bos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void openFile(String filePath) throws IOException {
        Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler   " + filePath);
    }

	private void openTechnics() {
		int row = jTable.getSelectedRow();
		if (row == -1) {
			SwingUtil.showMessageDialog("请选择工艺", "提示", 2);
		} else {
			Object oid = jTable.getValueAt(row, 0);
			String oidValue = oid == null ? "" : oid.toString().trim();
			List<Object> returnList = new ArrayList<Object>();
			try {
				returnList = TechnicsIntf.searchTechnics(oidValue);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("returnList= " + returnList);
			if (returnList == null || returnList.size() == 0) {
				JOptionPane.showMessageDialog(null, "工艺下载出错！", "提示", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			List<Object> list = (List<Object>) returnList.get(0);
			String technicsFolderName = (String) list.get(0);
			String versionId = (String) list.get(3);
			if (technicsFolderName.toLowerCase().endsWith(".zip")) {
				technicsFolderName = technicsFolderName.substring(0, technicsFolderName.length() - 4);
			}
			logger.debug("历史文件路径====" + technicsFolderName);
			byte[] bytes = (byte[]) list.get(1);
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
					Element technicsElement = XmlUtility.getTechnicsElement(technicsDocument);
					XmlUtility.setAttributeValue(technicsElement, "version", versionId);
					new NewTechnicsHistoryView(technicsDocument, frame, null, folderName, "查看工艺");
				}
			} catch (Exception e1) {
				if (folderName != null) {
					File file = new File(folderName);
					if (file.exists())
						FilesUtil.delFolder(folderName);
				}
				JOptionPane.showMessageDialog(null, "将相关工艺版本下载到本地时出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
				e1.printStackTrace();
			}

//			dialog.dispose();
		}

	}
}
