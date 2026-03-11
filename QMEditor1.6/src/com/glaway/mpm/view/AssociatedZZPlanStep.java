package com.glaway.mpm.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;

import org.dom4j.Element;

import com.glaway.mpm.model.TempObject;
import com.glaway.mpm.parameter.commonui.CommonTableModel;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.util.UserUtil;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class AssociatedZZPlanStep extends JDialog implements ActionListener{

	private static final long serialVersionUID = 1L;
	private AssociatedZZPlanDialog step1Dialog = null;
	private AssociatedFZPlanDialog associatedFZPlanDialog = null;
	private NewTechnicsPart mainFrame = null;
	private Element technicsElement = null;
	private JTable table = null;
	private CommonTableModel tableModel = null;
	private JButton searchBtn = null;
	private JButton comfirmBtn = null;
	private JButton cancleBtn = null;
	private JTextField number = null;
	private JTextField name = null;
	private static final int COL_DOCNUMBER = 0;
	private static final int COL_TECHNICNUMBER = 2;
	private static final int COL_TECHNICNAME = 3;
	private static final int COL_VERSION = 4;
	private static final int COL_PCH = 5;

	public AssociatedZZPlanStep (AssociatedZZPlanDialog step1Dialog, Element technicsElement, NewTechnicsPart mainFrame) {
		setModal(true);
		this.step1Dialog = step1Dialog;
		this.technicsElement = technicsElement;
		this.mainFrame = mainFrame;
		initComponent();
		initData();
		initDialog();
	}
	
	public AssociatedZZPlanStep (AssociatedFZPlanDialog associatedFZPlanDialog, Element technicsElement, NewTechnicsPart mainFrame) {
		setModal(true);
		this.associatedFZPlanDialog = associatedFZPlanDialog;
		this.technicsElement = technicsElement;
		this.mainFrame = mainFrame;
		initComponent();
		initData();
		initDialog();
	}
	
	private void initComponent() {
		JScrollPane tableScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JPanel topBtnPanel = new JPanel();
		JPanel topPanel2 = new JPanel();
		JPanel downBtnPanel = new JPanel();
		String[] header = {"文档编号", "序号", "工艺文件编号", "工艺文件名称", "版本", "批次号" };
		Class<?>[] colClass = { String.class, String.class, String.class, String.class, String.class, String.class};
		tableModel = new CommonTableModel(header, colClass, null);
		table = new JTable(tableModel);
		table.setRowHeight(30);
		table.getTableHeader().setReorderingAllowed(false);
		CommonUIUtil.hiddenCell(table, COL_DOCNUMBER);
		CommonUIUtil.hiddenCell(table, COL_PCH);
		JViewport viewport = new JViewport();
		viewport.add(table.getTableHeader());
		tableScrollPane.setColumnHeader(viewport);
		tableScrollPane.setViewportView(table);
		this.add(tableScrollPane, BorderLayout.CENTER);
		
		JLabel numberLabel = new JLabel("编号：");
		JLabel nameLabel = new JLabel("名称：");
		number = new JTextField();
		name = new JTextField();
		number.setPreferredSize(new Dimension(200, 25));
		name.setPreferredSize(new Dimension(200, 25));
		searchBtn = new JButton("搜索");
		searchBtn.addActionListener(this);
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setHgap(20);
		flowLayout.setAlignment(FlowLayout.LEFT);
		topBtnPanel.setLayout(flowLayout);
		topBtnPanel.add(numberLabel);
		topBtnPanel.add(number);
		topBtnPanel.add(nameLabel);
		topBtnPanel.add(name);
		FlowLayout layout = new FlowLayout();
		layout.setHgap(20);
		layout.setAlignment(FlowLayout.RIGHT);
		topPanel2.setLayout(layout);
		topPanel2.add(searchBtn);
		JPanel panel = new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 0, 5, 5);
		c.gridy = 1;
		c.gridx = 1;
		panel.setLayout(new GridBagLayout());
		panel.add(topBtnPanel, c);
		c.gridy = 2;
		c.anchor = GridBagConstraints.NORTHEAST;
		panel.add(topPanel2, c);
		this.add(panel, BorderLayout.NORTH);
		comfirmBtn = new JButton("确定");
		comfirmBtn.addActionListener(this);
		cancleBtn = new JButton("取消");
		cancleBtn.addActionListener(this);
		FlowLayout flowLayout2 = new FlowLayout();
		flowLayout2.setHgap(20);
		flowLayout2.setAlignment(FlowLayout.RIGHT);
		downBtnPanel.setLayout(flowLayout2);
		downBtnPanel.add(comfirmBtn);
		downBtnPanel.add(cancleBtn);
		this.add(downBtnPanel, BorderLayout.SOUTH);
	}
	
	private void initDialog() {
		setTitle("添加");
		setSize(800, 600);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		SwingUtil.setMiddle(this);
		setVisible(true);
	}
	
	private void initData() {
		String pplanType = technicsElement.attributeValue("PPLANTYPE");
		String zf = technicsElement.attributeValue("ZFFLAG");
		XWTreeNode node = mainFrame.getXWPartTreePanel().getSelectedTreeNode();
		XWTreeNode parentNode = (XWTreeNode) node.getParent();
		List<Element> childrenList = new ArrayList<Element>();
//		zzTechnicsElement = new HashMap<String, Element>();
		for(int i = 0; i < parentNode.getChildCount(); i++){
			XWTreeNode childNode = (XWTreeNode) parentNode.getChildAt(i);
			XWTreeObject xo = childNode.getObject();
			if (xo instanceof TechnicsMessageTreeObject) {
				TechnicsMessageTreeObject xto = (TechnicsMessageTreeObject) xo;
				Element childElement = (Element) xto.getTreeCellData();
				if("Z".equals(zf) && childElement.attributeValue("ZFFLAG").equals("F") && childElement.attributeValue("PPLANTYPE").equals(pplanType)){
					childrenList.add(childElement);
				}else if("F".equals(zf) && childElement.attributeValue("ZFFLAG").equals("Z") && childElement.attributeValue("PPLANTYPE").equals(pplanType)){
//					zzTechnicsElement.put(childElement.attributeValue("technicsNumber"), childElement);
					childrenList.add(childElement);
				}
			}
		}
		if(childrenList != null && !childrenList.isEmpty()){
			tableModel.setRowCount(0);
			for(int i = 0; i < childrenList.size(); i++){
				Vector<Object> rowData = new Vector<Object>();
				rowData.add(convertNull(childrenList.get(i).attributeValue("technicsNumber")));
				rowData.add(i + 1);
				rowData.add(convertNull(childrenList.get(i).attributeValue("pplanNumber")));
				rowData.add(convertNull(childrenList.get(i).attributeValue("technicsName")));
				rowData.add(convertNull(childrenList.get(i).attributeValue("version")));
				rowData.add(convertNull(childrenList.get(i).attributeValue("PCNO")));
				tableModel.addRow(rowData);
			}
		}
	}
	
	private String convertNull(Object str) {
		return str == null ? "" : str.toString();
	}
	
	private void searchPlan(final String number, final String name, final String technicType, final String zfFlag) {
		final VaActionProgressBar progressBar = new VaActionProgressBar(null, this, "搜索工艺文件", "正在搜索工艺文件，请等待...", String.format("正在通过编号【%s】名称【%s】模糊搜索工艺文件，请等待...", number, name));
		Thread th = new Thread() {
			@Override
			public void run() {
				try {
					String[] states = null;
					if (associatedFZPlanDialog != null) {
						states = new String[]{ PrintConstants.LIFECYCLE_EN_APPROVED };
					}
					List<TempObject> technics = TechnicsIntf.searchTechnics(number, name, technicType, zfFlag, states);
					if (technics != null && technics.size() > 0) {
						tableModel.setRowCount(0);
						int i = 1;
						for (TempObject obj : technics) {
							Vector<Object> rowData = new Vector<Object>();
							String picihao = convertNull(obj.getOccId());
							rowData.add(convertNull(obj.getDocNumber()));
							rowData.add(i++);
							rowData.add(convertNull(obj.getNumber()));
							rowData.add(convertNull(obj.getName()));
							rowData.add(convertNull(obj.getVersion()));
							rowData.add(picihao.length() == 0 ? "无" : picihao); //批次号
							tableModel.addRow(rowData);
						}
					} else {
						JOptionPane.showMessageDialog(AssociatedZZPlanStep.this, "没有符合条件数据！", "查询结果", JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(AssociatedZZPlanStep.this, e.getMessage(), "查询出错", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				} finally {
					progressBar.finish();
					progressBar.setVisible(false);
				}
			}
		};
		th.start();
		progressBar.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (comfirmBtn == e.getSource()) {
			int[] selectedRows = table.getSelectedRows();
			if (selectedRows == null || selectedRows.length == 0) {
				SwingUtil.showMessageDialog("请选择主制工艺", "提示", 2);
				return;
			}
			SimpleDateFormat dataFormate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE);
			String date = dataFormate.format(new Date());
			List<Map<String, Object>> tableDatas = new ArrayList<Map<String, Object>>();
			for (int index : selectedRows) {
				Map<String, Object> rowMap = new HashMap<String, Object>();
				String docNumber = convertNull(tableModel.getValueAt(index, COL_DOCNUMBER));
				String technicNumber = convertNull(tableModel.getValueAt(index, COL_TECHNICNUMBER));
				String technicName = convertNull(tableModel.getValueAt(index, COL_TECHNICNAME));
				String version = convertNull(tableModel.getValueAt(index, COL_VERSION));
				String picihao = convertNull(tableModel.getValueAt(index, COL_PCH));
				rowMap.put("gwKey", "");
				rowMap.put("docNumber", docNumber);
				rowMap.put("technicNumber", technicNumber);
				rowMap.put("technicName", technicName);
				rowMap.put("version", version);
				rowMap.put("creator", UserUtil.getCurrentUserOid().get(2));
				rowMap.put("createTime", date);
				rowMap.put("picihao", picihao);
				tableDatas.add(rowMap);
			}
			if (this.step1Dialog != null) {
				//关联主制工艺
				this.step1Dialog.setTableValues(tableDatas, true);
			} else if (this.associatedFZPlanDialog != null) {
				//关联辅制工艺
				if (selectedRows.length == 1) {
					this.associatedFZPlanDialog.associatedFZPlan(tableDatas);
				} else {
					JOptionPane.showMessageDialog(this, "每道主制工序只能关联一份辅制工艺！", "无法关联", JOptionPane.WARNING_MESSAGE);
				}
			}
			this.dispose();
		} else if (cancleBtn == e.getSource()) {
			this.dispose();
		} else if (searchBtn == e.getSource()) {
			String technicType = technicsElement.attributeValue("PPLANTYPE");
			String planNumber = number.getText().trim();
			String planName = name.getText().trim();
			if (planNumber.length() == 0 && planName.length() == 0) {
				JOptionPane.showMessageDialog(this, "至少输入一个搜索条件");
				return;
			}
			String zfFlag = "";
			if (this.step1Dialog != null) {
				zfFlag = "Z";
			} else if (this.associatedFZPlanDialog != null) {
				zfFlag = "F";
			}
			searchPlan(planNumber, planName, technicType, zfFlag);
		}
	}
}
