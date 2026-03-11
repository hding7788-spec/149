package com.glaway.mpm.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.glaway.mpm.parameter.service.ProcessParameterToWCIntf;
import org.dom4j.Document;
import org.dom4j.Element;

import com.glaway.mpm.editor.JTextFiledEditor;
import com.glaway.mpm.model.UploadTechnics;
import com.glaway.mpm.util.BomXMLUtil;
import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.JavaUtil;
import com.glaway.mpm.util.ObjectTransfer;
import com.glaway.mpm.util.TechnicsUtil;
import com.glaway.mpm.util.UserUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class QuickCreateProcedureJPanel extends JPanel implements
		ActionListener, Observer {
	private VaLogger logger = VaLogger.getLogger(QuickCreateProcedureJPanel.class);
//	private static Map<String, String> allStepNameMap = ResourceIntf.getProcessStepName();
	class QuickTable extends JTable {
		QuickTable(DefaultTableModel model) {
			super(model);
		}

		public boolean isCellEditable(int row, int column) {
			if ((row >= 0 && row < getRowCount())
					&& (column >= 0 && column < getColumnCount())) {
				String isEdit = (String) getValueAt(row, 7);
				if (isEdit != null && isEdit.trim().length() > 0) {
					if (isEdit.equals("false"))
						return false;
				}
				if (column == 0 || column == 8) {
					return false;
				}
			}
			return true;
		}
		public void editingStopped(ChangeEvent e)
        {
            int row = getEditingRow();
            int col = getEditingColumn();
            super.editingStopped(e);
            doSomeThing(row, col);

        }
		  private void doSomeThing(int row, int col) {
	            if (col == 3) {
	                Boolean  flag=false;
	                String value = (String) tableModel.getValueAt(row, col);
	                String name = value.trim();
                    if (!"".equals(name) && name != null && !"null".equals(name)) {
                        Map<String,String > map = NewTechnicsPart.pdNameDescribeMap;
                        String zzcj = map.get(name);
                        if (!"".equals(zzcj)&&!"null".equals(zzcj)&&zzcj!=null) {
                          tableModel.setValueAt(zzcj, row, 2);
                      }
                    }
                    Set<String> stepSet = frame.allStepNameMap.keySet();
                    for (String key:stepSet) {
                        String stepName = frame.allStepNameMap.get(key);
                        if (stepName.equals(value)) {
                            flag=true;
                            break;
                        }

                    }
                    if (!flag) {
                        tableModel.setValueAt("", row, col);
                    }


	            }
	        }
	        }
	class Render extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable arg0,
				Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
			JLabel com = (JLabel) super.getTableCellRendererComponent(arg0,
					arg1, arg2, arg3, arg4, arg5);
			com.setHorizontalAlignment(SwingConstants.CENTER);
			com.setVerticalAlignment(SwingConstants.CENTER);
			// com.setForeground(Color.black);
			if (technicsElement != null) {
				UploadTechnics technics = ObjectTransfer.technicsElementToUploadTechnics(technicsElement);
				if (!NewTechnicsPart.downLoadTech.contains(technics)) {
					String unite = technicsElement.attributeValue("unite");
					if (unite != null && !unite.equalsIgnoreCase("common")) {
						String user = frame.getCurrentUser();
						if (user == null)
							user = "";
						String bsoID = (String) arg0.getValueAt(arg4, 6);
						Element step = XmlUtility.getStepByID(technicsElement, bsoID);
						if (step != null) {
							String responser = step.attributeValue("responser");
							if (responser != null && responser.trim().length() > 0) {
								if (responser.equals(user)) {
									com.setForeground(Color.red);
								} else {

								}
							}
						}
					}
				}
			}
			return com;
		}
	}

	class IsKeyEditor extends DefaultCellEditor {
		JPanel panel = null;
		JCheckBox checkBox = null;;
		private JTable editingTable = null;
		private int editingRow = -1;
		private int editingColumn = -1;

		IsKeyEditor(JCheckBox box) {
			super(box);
			checkBox = box;
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			panel.add(checkBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 0, 0));
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			editingTable = table;
			editingRow = row;
			editingColumn = column;
			checkBox.setSelected(false);
			if (value != null && value instanceof String) {
				if (value.toString().equalsIgnoreCase("true")) {
					checkBox.setSelected(true);
				}
			}
			return panel;
		}

		public Object getCellEditorValue() {
			boolean bool = checkBox.isSelected();
			return String.valueOf(bool);
		}
	}

	private Element technicsElement = null;

	class Editor extends DefaultCellEditor {
		StepNameComboBox sncb = new StepNameComboBox(null);
		JComboBox e = null;
		// 用于记录当前表格
		private JTable editingTable = null;
		private int editingRow = -1;
		private int editingColumn = -1;

		public Editor(JComboBox comboBox, JTable table) {
			super(comboBox);
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			editingTable = table;
			editingRow = row;
			editingColumn = column;
			e = null;
			if (value == null)
				value = "";
			if (column == 3) {
				sncb.removeAllItems();
				sncb.setSelectedItem(JavaUtil.convertNull(value));
				return sncb;
			}
			e = createBox(table, value, row, column);
			return e;
		}

		public Object getCellEditorValue() {
			if (editingColumn == 3) {
				table.setValueAt("", editingRow, editingColumn + 1);
			}
			if (e != null) {
				Object value = e.getSelectedItem();
				if (value == null)
					value = "";
				if ((editingTable != null)
						&& (editingRow >= 0 && editingRow < editingTable.getRowCount())
						&& (editingColumn >= 0 && editingColumn < editingTable.getColumnCount())) {
					table.setValueAt(value, editingRow, editingColumn);
				}
				if (editingColumn == 3) {
					int count = sncb.getItemCount();
					for (int i = 0; i < count; i++) {
						if (sncb.getItemAt(i).toString().equals(value)) {
							return value;
						}
					}
					return "";
				}
				return value;
			} else {
				Object value = sncb.getSelectedItem();
				value = JavaUtil.convertNull(value);
				if ((editingTable != null)
						&& (editingRow >= 0 && editingRow < editingTable.getRowCount())
						&& (editingColumn >= 0 && editingColumn < editingTable.getColumnCount())) {
					table.setValueAt(value, editingRow, editingColumn);
				}
				return value;
			}



		}

	}

	NewTechnicsPart frame;
	Vector vv = new Vector();
	private DefaultTableModel tableModel = new DefaultTableModel();
	private JButton zidongpaixu = new IconButton("/images/button_save.png", "自动排序");
	private JButton proveButton = new IconButton("/images/button_save.png", "保存");// 确定
	private JButton batchCreate = new IconButton("/images/button_add.png", "批量");
	private JButton addButton = new IconButton("/images/button_add.png", "添加");
	private JButton insertTemplateButton = new IconButton("/images/button_add.png", "插入模板");
	private JButton removeButton = new IconButton("/images/button_remove.png", "移除");
	private JButton upButton = new IconButton("/images/button_upmove.png", "上移");
	private JButton downButton = new IconButton("/images/button_downmove.png", "下移");

	JTable table = new QuickTable(tableModel);
	String technumber = null;
	String techname = null;
	String techType = null;
	String lifecycle = null;

	private Map workShop = null;
	private Map workType = null;
	private Map workSpace = null;

	private boolean isEdit = true;
	String responserGroup = "";

	public QuickCreateProcedureJPanel(NewTechnicsPart parent) {
		frame = parent;

		setLayout(new GridBagLayout());
		table.setRowHeight(24);
		table.getTableHeader().setReorderingAllowed(false);
		table.setGridColor(Color.GRAY);
		workShop = ResourceIntf.getWorkShops();
//		logger.debug("QuickCreateProcedureDialog ====制造单位========" + workShop);

		table.putClientProperty("terminateEditOnFocusLost", true);
		table.setBackground(Color.white);
		table.setDefaultRenderer(Object.class, new Render());

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				stopTableCellEditing();
			}
		});

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

		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (frame != null) {
					frame.getPdNameTreePanel().deleteObservers();
					frame.getPdNameTreePanel().addObserver(QuickCreateProcedureJPanel.this);
					frame.getLeftTab().setSelectedComponent(frame.getPdNameTreePanel());
				}
			}
		});
		final JLabel ldLabel = new JLabel();
		ldLabel.setPreferredSize(new Dimension(100, 23));
		ldLabel.setMinimumSize(new Dimension(100, 23));
		ldLabel.setMaximumSize(new Dimension(100, 23));
		ldLabel.setText("工艺规程");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_3.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints_3.gridx = 0;
		gridBagConstraints_3.gridy = 0;
		add(ldLabel, gridBagConstraints_3);

		final JLabel label = new JLabel();
		label.setMaximumSize(new Dimension(100, 23));
		label.setMinimumSize(new Dimension(100, 23));
		label.setPreferredSize(new Dimension(100, 23));

		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_6.anchor = GridBagConstraints.NORTHEAST;
		gridBagConstraints_6.weightx = 1.0;
		gridBagConstraints_6.gridx = 1;
		gridBagConstraints_6.gridy = 0;
		gridBagConstraints_6.insets = new Insets(5, 0, 5, 5);
		add(label, gridBagConstraints_6);

		final JPanel panel2 = new JPanel();
		panel2.setLayout(new GridBagLayout());

		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.fill = GridBagConstraints.BOTH;
		gridBagConstraints_4.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints_4.weighty = 1.0;
		gridBagConstraints_4.weightx = 1.0;
		gridBagConstraints_4.gridx = 0;
		gridBagConstraints_4.gridy = 1;
		gridBagConstraints_4.gridwidth = 2;
		gridBagConstraints_4.insets = new Insets(5, 5, 5, 5);
		add(panel2, gridBagConstraints_4);

		tableModel.addColumn("关键");
		tableModel.addColumn("工序号");
		tableModel.addColumn("制造单位");
		tableModel.addColumn("工序名称");
		tableModel.addColumn("工种");
		tableModel.addColumn("工位");
		tableModel.addColumn("ID");
		tableModel.addColumn("isEdit");
		tableModel.addColumn("前置工序");
		tableModel.addColumn("前置工序ID");

		Editor typeEditor = new Editor(new JComboBox(), table);
		Editor type1Editor = new Editor(new StepNameComboBox(null), table);

		JTextField stepNameField = new JTextField();
		stepNameField.setEditable(false);

		TableColumnModel columnModel = table.getColumnModel();

		columnModel.getColumn(0).setCellRenderer(new IsKeyRenderer());
		columnModel.getColumn(0).setCellEditor(new IsKeyEditor(new JCheckBox()));
		columnModel.getColumn(1).setCellEditor(new JTextFiledEditor(new JTextField(),true));
		columnModel.getColumn(2).setCellEditor(typeEditor);// 制造单位
		columnModel.getColumn(3).setCellEditor(type1Editor);
//		columnModel.getColumn(3).setCellEditor(new DefaultCellEditor(stepNameField));
		columnModel.getColumn(3).setCellRenderer(new LabelRender());
		columnModel.getColumn(4).setCellEditor(typeEditor);// 工种
		columnModel.getColumn(5).setCellEditor(typeEditor);// 工位

		JScrollPane pane = new JScrollPane(table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane.getViewport().setBackground(Color.white);

		zidongpaixu.addActionListener(this);
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.anchor = GridBagConstraints.NORTHEAST;
		gridBagConstraints_2.gridx = 1;
		gridBagConstraints_2.gridy = 0;
		gridBagConstraints_2.insets = new Insets(0, 0, 0, 0);
		panel2.add(zidongpaixu, gridBagConstraints_2);

		batchCreate.addActionListener(this);
		final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
		gridBagConstraints_18.anchor = GridBagConstraints.NORTHEAST;
		gridBagConstraints_18.gridx = 1;
		gridBagConstraints_18.gridy = 1;
		gridBagConstraints_18.insets = new Insets(5, 0, 0, 0);
		panel2.add(batchCreate, gridBagConstraints_18);

		addButton.addActionListener(this);
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.anchor = GridBagConstraints.NORTHEAST;
		gridBagConstraints_8.gridx = 1;
		gridBagConstraints_8.gridy = 2;
		gridBagConstraints_8.insets = new Insets(5, 0, 0, 0);
		panel2.add(addButton, gridBagConstraints_8);

		insertTemplateButton.addActionListener(this);
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.anchor = GridBagConstraints.NORTHEAST;
		gridBagConstraints_10.gridx = 1;
		gridBagConstraints_10.gridy = 3;
		gridBagConstraints_10.insets = new Insets(5, 0, 0, 0);
		panel2.add(insertTemplateButton, gridBagConstraints_10);

		upButton.addActionListener(this);
		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.anchor = GridBagConstraints.NORTHEAST;
		gridBagConstraints_13.gridx = 1;
		gridBagConstraints_13.gridy = 4;
		gridBagConstraints_13.insets = new Insets(5, 0, 0, 0);
		panel2.add(upButton, gridBagConstraints_13);

		downButton.addActionListener(this);
		final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.anchor = GridBagConstraints.NORTHEAST;
		gridBagConstraints_14.gridx = 1;
		gridBagConstraints_14.gridy = 5;
		gridBagConstraints_14.insets = new Insets(5, 0, 0, 0);
		panel2.add(downButton, gridBagConstraints_14);

		removeButton.addActionListener(this);
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.anchor = GridBagConstraints.NORTHEAST;
		gridBagConstraints_11.gridx = 1;
		gridBagConstraints_11.gridy = 6;
		gridBagConstraints_11.insets = new Insets(5, 0, 0, 0);
		panel2.add(removeButton, gridBagConstraints_11);

		proveButton.addActionListener(this);
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.anchor = GridBagConstraints.NORTHEAST;
		gridBagConstraints_1.gridx = 1;
		gridBagConstraints_1.gridy = 7;
		gridBagConstraints_1.insets = new Insets(5, 0, 0, 0);
		panel2.add(proveButton, gridBagConstraints_1);

		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.fill = GridBagConstraints.BOTH;
		gridBagConstraints_7.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints_7.weighty = 1.0;
		gridBagConstraints_7.weightx = 1.0;
		gridBagConstraints_7.gridx = 0;
		gridBagConstraints_7.gridy = 0;
		gridBagConstraints_7.gridheight = 10;
		gridBagConstraints_7.insets = new Insets(0, 0, 0, 5);
		panel2.add(pane, gridBagConstraints_7);

		final JLabel label_2 = new JLabel();
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints_12.anchor = GridBagConstraints.SOUTHEAST;
		gridBagConstraints_12.weighty = 1.0;
		gridBagConstraints_12.gridx = 1;
		gridBagConstraints_12.gridy = 7;
		gridBagConstraints_12.insets = new Insets(0, 0, 0, 0);
		panel2.add(label_2, gridBagConstraints_12);

		// 隐藏ID列
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(0).setPreferredWidth(50);
		table.getColumnModel().getColumn(0).setWidth(50);
		table.getColumnModel().getColumn(0).setMinWidth(50);

		table.getColumnModel().getColumn(1).setMaxWidth(60);
		table.getColumnModel().getColumn(1).setPreferredWidth(60);
		table.getColumnModel().getColumn(1).setWidth(60);
		table.getColumnModel().getColumn(1).setMinWidth(60);

		// 隐藏ID列
		table.getTableHeader().getColumnModel().getColumn(6).setMaxWidth(0);
		table.getTableHeader().getColumnModel().getColumn(6).setMinWidth(0);
		table.getColumnModel().getColumn(6).setMaxWidth(0);
		table.getColumnModel().getColumn(6).setPreferredWidth(0);
		table.getColumnModel().getColumn(6).setWidth(0);
		table.getColumnModel().getColumn(6).setMinWidth(0);

		// 隐藏准许编辑标示列
		table.getTableHeader().getColumnModel().getColumn(7).setMaxWidth(0);
		table.getTableHeader().getColumnModel().getColumn(7).setMinWidth(0);
		table.getColumnModel().getColumn(7).setMaxWidth(0);
		table.getColumnModel().getColumn(7).setPreferredWidth(0);
		table.getColumnModel().getColumn(7).setWidth(0);
		table.getColumnModel().getColumn(7).setMinWidth(0);

		// 隐藏工种列
		table.getTableHeader().getColumnModel().getColumn(4).setMaxWidth(0);
		table.getTableHeader().getColumnModel().getColumn(4).setMinWidth(0);
		table.getColumnModel().getColumn(4).setMaxWidth(0);
		table.getColumnModel().getColumn(4).setPreferredWidth(0);
		table.getColumnModel().getColumn(4).setWidth(0);
		table.getColumnModel().getColumn(4).setMinWidth(0);

		// 隐藏工位列
		table.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(0);
		table.getTableHeader().getColumnModel().getColumn(5).setMinWidth(0);
		table.getColumnModel().getColumn(5).setMaxWidth(0);
		table.getColumnModel().getColumn(5).setPreferredWidth(0);
		table.getColumnModel().getColumn(5).setWidth(0);
		table.getColumnModel().getColumn(5).setMinWidth(0);

		// 前置工序列
		table.getTableHeader().getColumnModel().getColumn(8).setMaxWidth(180);
		table.getTableHeader().getColumnModel().getColumn(8).setMinWidth(180);
		table.getColumnModel().getColumn(8).setMaxWidth(0);
		table.getColumnModel().getColumn(8).setPreferredWidth(0);
		table.getColumnModel().getColumn(8).setWidth(0);
		table.getColumnModel().getColumn(8).setMinWidth(0);

		// 隐藏前置工序ID列
		table.getTableHeader().getColumnModel().getColumn(9).setMaxWidth(0);
		table.getTableHeader().getColumnModel().getColumn(9).setMinWidth(0);
		table.getColumnModel().getColumn(9).setMaxWidth(0);
		table.getColumnModel().getColumn(9).setPreferredWidth(0);
		table.getColumnModel().getColumn(9).setWidth(0);
		table.getColumnModel().getColumn(9).setMinWidth(0);

	}

	public void setTableValue(Element techEle) {
		clear();
		setUIEnabled(true);
		technicsElement = techEle;
		technumber = XmlUtility.getAttributeValue(techEle, "technicsNumber");
		techname = XmlUtility.getAttributeValue(techEle, "technicsName");
		techType = XmlUtility.getAttributeValue(techEle, "technicsType");
		lifecycle = XmlUtility.getAttributeValue(techEle, "lifecycle");
		if (!"正在工作".equals(lifecycle) && !"修改中".equals(lifecycle)) {
			setUIEnabled(false);
		}
		XWTreeNode pNode = frame.getXWPartTreePanel().getSelectedTreeNode().getP();
		if(pNode != null) {
			XWTreeObject obj = pNode.getObject();
			if(obj instanceof XWPartTreeObject) {
				XWPartTreeObject partObject = (XWPartTreeObject)obj;
				if(!partObject.isAllowed()) {
					setUIEnabled(false);
				}
			}
		}

		String unite = XmlUtility.getAttributeValue(techEle, "unite");
		if (unite == null)
			unite = "";
		String creatorOid = XmlUtility.getAttributeValue(techEle, "creatorOid");
		if (creatorOid == null)
			creatorOid = "";
		if (techType == null || techType.trim().length() == 0) {
			String partNum = XmlUtility.getAttributeValue(techEle, "partNumber");
			String technicsType = BomXMLUtil.judgeTechnicsType(partNum);
			XmlUtility.setAttributeValue(techEle, "technicsType", technicsType);
			techType = technicsType;
		}

		Element group = XmlUtility.getProcedures(techEle);
		if (group != null) {
			List list = group.elements();
			if (list != null && list.size() > 0) {
				Map<String, String> map = new HashMap<String, String>();
				for (int i = 0; i < list.size(); i++) {
					Element step = (Element) list.get(i);
					String number = XmlUtility.getAttributeValue(step, "stepNumber");
					String ID = XmlUtility.getAttributeValue(step, "bsoID");
					map.put(ID, number);
				}
				for (int i = 0; i < list.size(); i++) {
					Element step = (Element) list.get(i);
					String isKey = XmlUtility.getAttributeValue(step, "isKey");
					String number = XmlUtility.getAttributeValue(step, "stepNumber");
					String name = XmlUtility.getAttributeValue(step, "stepName");
					String workShop = XmlUtility.getAttributeValue(step, "workShop");
					String type = XmlUtility.getAttributeValue(step, "workType");
					String ID = XmlUtility.getAttributeValue(step, "bsoID");
					String paceLocation = XmlUtility.getAttributeValue(step, "workSpace");
					String isEdit = "true";
					String preStep = XmlUtility.getAttributeValue(step, "preStep"); //前置工序
					String preBsoID = XmlUtility.getAttributeValue(step, "preBsoID"); //前置工序ID
					//处理前置工序在界面显示 add by zhuhao 2017.12.15
					String[] preArray = null;
					//map.put(ID, number);
					if(!"null".equals(preBsoID) && preBsoID!=null && !"".equals(preBsoID)){
						if(preBsoID.contains(",")){
							preArray = preBsoID.split(",");
							preStep = "";
							for(int j = 0;j < preArray.length;j++){
								if("".equals(preStep)){
									preStep = map.get(preArray[j]);
								}else{
									preStep = preStep + "," + map.get(preArray[j]);
								}
							}
						}else{
							preStep = map.get(preBsoID);
						}
					}
					//处理前置工序在界面显示 end
					if (unite.equals("routeUnite") || unite.equals("unite"))// 合编工艺
					{
						String user = frame.getCurrentUser();
						if (!creatorOid.equals(user))// 不是当前用户的情况下
						{
							String responser = XmlUtility.getAttributeValue(step, "responser");
							if ((responser != null && responser.trim().length() > 0)
									&& (user != null && user.trim().length() > 0)) {
								if (!user.equals(responser)) {
									isEdit = "false";
								}
							}
						}
					}
					UploadTechnics technics = ObjectTransfer.technicsElementToUploadTechnics(technicsElement);
					if (NewTechnicsPart.downLoadTech.contains(technics)) {
						isEdit = "false";
					}
					tableModel.addRow(new Object[] { isKey, number, workShop,
							name, type, paceLocation, ID, isEdit, preStep, preBsoID });
				}
			} else {
				if (lifecycle != null && (!"".equals(lifecycle))
						&& !lifecycle.equals(WorkSpaceUtil.TO_ARCHIVE)) {
					return;
				}
				tableModel.addRow(new Object[] { "false" });
				tableModel.setValueAt("10", 0, 1);
				tableModel.setValueAt(new java.rmi.server.UID().toString(), 0, 6);
				tableModel.setValueAt("true", 0, 7);
				tableModel.addRow(new Object[] { "false" });
				tableModel.setValueAt("20", 1, 1);
				tableModel.setValueAt(new java.rmi.server.UID().toString(), 1, 6);
				tableModel.setValueAt("true", 1, 7);
			}
		}
	}
	public static String userRemotegroupCache = null;
	public void actionPerformed(ActionEvent arg0) {
		String group = "";
		if(userRemotegroupCache ==null){
			group = TechnicsUtil.getGroupName();
			group = TechnicsUtil.getGroupValue(group);
			userRemotegroupCache= group ;
		}else{
			group = userRemotegroupCache;
		}
		Document doc = frame.getCurrentTechnics();
		if (doc == null) {
			JOptionPane.showMessageDialog(this, "当前工艺树上没有数据！", "提示",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		if (table.getCellEditor() != null)
			table.getCellEditor().stopCellEditing();
		vv.clear();
		if (arg0.getSource() == proveButton)// 确定按钮
		{
			try {
				updataTechnicsData();
				frame.createTechnicsRoute();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "保存过程出现错误！", "提示",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if(arg0.getSource()==zidongpaixu){
//			List<Integer> keys=new ArrayList<Integer>();
//            Map<Object ,Object[]> rowMap=new HashMap<Object ,Object[]>();
//			int Rows = tableModel.getRowCount();
//			for (int i = 0; i < Rows; i++) {
//				Object[] rowDate=new Object[tableModel.getColumnCount()];
//				for(int j=0;j<tableModel.getColumnCount();j++){
//                   rowDate[j]=tableModel.getValueAt(i, j);
//				}
//				rowMap.put(tableModel.getValueAt(i, 1), rowDate);
//				keys.add(Integer.valueOf((String)tableModel.getValueAt(i, 1)));
//			}
//             Integer ob=null;
//			for(int k=0;k<keys.size();k++){
//				for(int m=k+1;m<keys.size();m++)
//				if(keys.get(k)>keys.get(m)){
//					ob=keys.get(m);
//					keys.set(m, keys.get(k));
//					keys.set(k, ob);
//				}
//			}
//           System.out.println("---------keys--------"+keys);
//           Object[] rowData;
//           for(int i=0;i<keys.size();i++){
//        	   rowData=rowMap.get(keys.get(i).toString());
//        	   System.out.println("----------------------rowData---------------"+rowData);
//        		   table.setValueAt(rowData[0], i, 0);
//        		   table.setValueAt(rowData[1], i, 1);
//        		   table.setValueAt(rowData[2], i, 2);
//        		   table.setValueAt(rowData[3], i, 3);
//        		   table.setValueAt(rowData[4], i, 4);
//        		   table.setValueAt(rowData[5], i, 5);
//        		   table.setValueAt(rowData[6], i, 6);
//        		   table.setValueAt(rowData[7], i, 7);
//          }
			int row = tableModel.getRowCount();
			int procedureNumber = 10;
			for(int i = 0; i < row; i++){
				tableModel.setValueAt(String.valueOf(procedureNumber), i, 1);
				procedureNumber += 10;
			}
			changePreID();
			changePreStep();
		}

		if (arg0.getSource() == batchCreate)// 批量按钮
		{
			int[] selects = table.getSelectedRows();
			if (selects != null && selects.length == 1) {
				int curRow = selects[0];
				if (curRow >= 0 && curRow < (table.getRowCount() - 1)) {
					String isEdit = (String) table.getValueAt(curRow, 7);
					if (isEdit != null && isEdit.equals("false")) {
						String number = (String) table.getValueAt(curRow, 1);
						if (number == null)
							number = "";
						JOptionPane.showMessageDialog(this, number
								+ "工序责任人不是当前用户，不能进行插入工序操作操作！", "提示",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					String temp = (String) table.getValueAt(curRow, 1);
					try {
						// 根据当前选中工序，判断其最多能插入几道工序
						int curNumber = Integer.parseInt(temp);
						boolean flag = true;
						int RowCount = tableModel.getRowCount();
						int minN = Integer.MAX_VALUE;
						for (int i = 0; i < RowCount; i++) {
							if (i == curRow)
								continue;
							String number = (String) tableModel.getValueAt(i, 1);// 编号
							if (number != null) {
								int tN = Integer.parseInt(number);
								if (tN > curNumber && tN < minN) {
									minN = tN;
								}
							}
						}
						if (minN == (curNumber + 1)) {
							flag = false;
							JOptionPane.showMessageDialog(this,
									"当前工序下已不能进行插入工序操作！", "提示",
									JOptionPane.INFORMATION_MESSAGE);
							return;
						}

						NumberInputDialog nid = new NumberInputDialog(frame, true, (minN - curNumber - 1));
						int count = nid.showDialog();
						if (flag) {
							int row = curRow + 1;
							String preBsoID = String.valueOf(tableModel.getValueAt(curRow, 6));
							for (int i = 0; i < count; i++) {
								String BsoID = new java.rmi.server.UID().toString();
								tableModel.insertRow(row, new Object[] { "false" });
								tableModel.setValueAt("" + (++curNumber), row, 1);
								tableModel.setValueAt(BsoID, row, 6);
								tableModel.setValueAt("true", row, 7);
								tableModel.setValueAt(curNumber-1, row, 8);
								tableModel.setValueAt(preBsoID, row, 9);
								table.setRowSelectionInterval(row, row);
								preBsoID = BsoID;
								row++;
							}
							return;
						}
					} catch (Exception e) {

					}
					return;
				}
			}

			// 最后一序责任人是其他用户
			if (table.getRowCount() > 0) {
				int index = table.getRowCount() - 1;
				String isEdit = (String) table.getValueAt(index, 7);
				if (isEdit != null && isEdit.equals("false")) {
					String number = (String) table.getValueAt(index, 1);
					if (number == null)
						number = "";
					JOptionPane.showMessageDialog(this, number
							+ "工序责任人不是当前用户，不能进行添加工序操作操作！", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}

			int count = tableModel.getRowCount();
			NumberInputDialog nid = new NumberInputDialog(frame, true);
			int result = nid.showDialog();
			if (count > 0) {
				String str = (String) tableModel.getValueAt(count - 1, 1);
				boolean isInt = false;
				int stepNumber = 0;
				try {
					stepNumber = XmlUtility.getSubFigure(str);
					if (stepNumber >= 0)
						isInt = true;
				} catch (Exception e) {

					e.printStackTrace();
				}
				if (isInt) {
					int preNumber = XmlUtility.getSubFigure(str);
					for (int i = 0; i < result; i++) {
						stepNumber = XmlUtility.STEPINTERVAL + stepNumber;
						tableModel.addRow(new Object[] { "false" });
						int row = tableModel.getRowCount() - 1;
						tableModel.setValueAt("" + stepNumber, row, 1);
						tableModel.setValueAt(new java.rmi.server.UID().toString(), row, 6);
						tableModel.setValueAt(group,row, 2);
						tableModel.setValueAt("true", row, 7);
						tableModel.setValueAt(preNumber, row, 8);
						table.setRowSelectionInterval(row, row);
						preNumber = XmlUtility.STEPINTERVAL + preNumber;
					}
				}
			} else {
				int stepNumber = 0;
				int preNumber = 0;
				String preBsoID = "";
				for (int i = 0; i < result; i++) {
					stepNumber = XmlUtility.STEPINTERVAL + stepNumber;
					tableModel.addRow(new Object[] { "false" });
					int row = tableModel.getRowCount() - 1;
					tableModel.setValueAt("" + stepNumber, row, 1);
					tableModel.setValueAt(group,row, 2);
					String bsoID = new java.rmi.server.UID().toString();
					tableModel.setValueAt(bsoID, row, 6);
					tableModel.setValueAt("true", row, 7);
					if(preNumber == 0){
						tableModel.setValueAt("", row, 8);
					}else{
						tableModel.setValueAt(preNumber, row, 8);
					}
					tableModel.setValueAt(preBsoID, row, 9);
					table.setRowSelectionInterval(row, row);
					preNumber = XmlUtility.STEPINTERVAL + preNumber;//前置工序自增
					preBsoID = bsoID;//上一个bsoID
				}
			}
			changePreID();
			changePreStep();
		}

		if (arg0.getSource() == addButton)// 添加按钮
		{
			int[] selects = table.getSelectedRows();
			if (selects != null && selects.length == 1) {
				int curRow = selects[0];
				if (curRow >= 0 && curRow < (table.getRowCount() - 1)) {
					String isEdit = (String) table.getValueAt(curRow, 7);
					if (isEdit != null && isEdit.equals("false")) {
						String number = (String) table.getValueAt(curRow, 1);
						if (number == null)
							number = "";
						JOptionPane.showMessageDialog(this, number
								+ "工序责任人不是当前用户，不能进行插入工序操作操作！", "提示",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					String temp = (String) table.getValueAt(curRow, 1);
					try {
						int curNumber = Integer.parseInt(temp);
						curNumber++;
						boolean flag = true;
						int RowCount = tableModel.getRowCount();
						for (int i = 0; i < RowCount; i++) {
							String number = (String) tableModel.getValueAt(i, 1);// 编号
							if (number != null) {
								if (number.equals("" + curNumber)) {
									flag = false;
									JOptionPane.showMessageDialog(this,
											"当前工序下已不能进行插入工序操作！", "提示",
											JOptionPane.INFORMATION_MESSAGE);
									return;
								}
							}
						}
						if (flag) {
							tableModel.insertRow((curRow + 1), new Object[] { "false" });
							tableModel.setValueAt("" + curNumber, (curRow + 1), 1);
							tableModel.setValueAt(group,(curRow + 1), 2);
							tableModel.setValueAt(new java.rmi.server.UID().toString(), (curRow + 1), 6);
							tableModel.setValueAt("true", (curRow + 1), 7);
							tableModel.setValueAt(temp, (curRow + 1), 8);
							tableModel.setValueAt(tableModel.getValueAt(curRow, 6), (curRow + 1), 9);
							table.setRowSelectionInterval((curRow + 1), (curRow + 1));
							return;
						}
					} catch (Exception e) {

					}
					return;
				}
			}
			// 最后一序责任人是其他用户
			if (table.getRowCount() > 0) {
				int index = table.getRowCount() - 1;
				String isEdit = (String) table.getValueAt(index, 7);
				if (isEdit != null && isEdit.equals("false")) {
					String number = (String) table.getValueAt(index, 1);
					if (number == null)
						number = "";
					JOptionPane.showMessageDialog(this, number
							+ "工序责任人不是当前用户，不能进行添加工序操作操作！", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}

			tableModel.addRow(new Object[] { "false" });
			int count = tableModel.getRowCount();
			if (count > 1) {
				String str = (String) tableModel.getValueAt(count - 2, 1);
				boolean isInt = false;
				int stepNumber = 0;
				try {
					stepNumber = XmlUtility.getSubFigure(str);
					if (stepNumber >= 0)
						isInt = true;
				} catch (Exception e) {

					e.printStackTrace();
				}
				if (isInt) {
					tableModel.setValueAt("" + (XmlUtility.STEPINTERVAL + stepNumber), count - 1, 1);
					tableModel.setValueAt(group,(count - 1), 2);
					tableModel.setValueAt(new java.rmi.server.UID().toString(), count - 1, 6);
					tableModel.setValueAt("true", count - 1, 7);
					tableModel.setValueAt(str, count - 1, 8);
					tableModel.setValueAt(tableModel.getValueAt(count -2, 6), count - 1, 9);
					table.setRowSelectionInterval(count - 1, count - 1);
				}
			} else {
				tableModel.setValueAt("" + (XmlUtility.STEPINTERVAL), count - 1, 1);
				tableModel.setValueAt(group,(count - 1), 2);
				tableModel.setValueAt(new java.rmi.server.UID().toString(), count - 1, 6);
				tableModel.setValueAt("true", count - 1, 7);
				table.setRowSelectionInterval(count - 1, count - 1);
			}
		}

		if (arg0.getSource() == removeButton)// 移除按钮
		{
			int selectRows[] = table.getSelectedRows();
			if (selectRows != null && selectRows.length > 0) {
				for (int i = selectRows.length - 1; i >= 0; i--) {
					String isEdit = (String) table.getValueAt(selectRows[i], 7);
					if (isEdit != null && isEdit.equals("false")) {
						String number = (String) table.getValueAt(selectRows[i], 1);
						if (number == null)
							number = "";
						JOptionPane.showMessageDialog(this, number
								+ "工序责任人不是当前用户，不能进行删除操作！", "提示",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
				for (int i = selectRows.length - 1; i >= 0; i--) {
					tableModel.removeRow(selectRows[i]);
				}
				changePreID();
				changePreStep();
			}

		}

		if (arg0.getSource() == insertTemplateButton)// 插入工序模板
		{
			if (technicsElement == null) {
				JOptionPane.showMessageDialog(this, "请先选择工艺！", "提示",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			String technicsNumber = technicsElement.attributeValue("technicsNumber");
			Element procedureEle = frame.insertProcedureTemplate(technicsElement);
			if (procedureEle == null) {
				return;
			}
			try {
				int[] selects = table.getSelectedRows();
				if (selects != null && selects.length == 1) {
					int curRow = selects[0];
					if (curRow >= 0 && curRow < (table.getRowCount() - 1)) {
						String isEdit = (String) table.getValueAt(curRow, 7);
						if (isEdit != null && isEdit.equals("false")) {
							String number = (String) table.getValueAt(curRow, 1);
							if (number == null)
								number = "";
							JOptionPane.showMessageDialog(this, number
									+ "工序责任人不是当前用户，不能进行插入工序操作操作！", "提示",
									JOptionPane.INFORMATION_MESSAGE);
							return;
						}

						String temp = (String) table.getValueAt(curRow, 1);
						try {
							int curNumber = Integer.parseInt(temp);
							curNumber++;
							boolean flag = true;
							int RowCount = tableModel.getRowCount();
							for (int i = 0; i < RowCount; i++) {
								String number = (String) tableModel.getValueAt(i, 1);// 编号
								if (number != null) {
									if (number.equals("" + curNumber)) {
										flag = false;
										JOptionPane.showMessageDialog(
														this,
														"当前工序下已不能进行插入工序操作！",
														"提示",
														JOptionPane.INFORMATION_MESSAGE);
										return;
									}
								}
							}
							if (flag) {
								String uid = new java.rmi.server.UID().toString();
								insertStepTemplate(procedureEle, curNumber, uid, technicsNumber);
								return;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						return;
					}
				}
				// 最后一序责任人是其他用户
				if (table.getRowCount() > 0) {
					int index = table.getRowCount() - 1;
					String isEdit = (String) table.getValueAt(index, 7);
					if (isEdit != null && isEdit.equals("false")) {
						String number = (String) table.getValueAt(index, 1);
						if (number == null)
							number = "";
						JOptionPane.showMessageDialog(this, number
								+ "工序责任人不是当前用户，不能进行添加工序操作操作！", "提示",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}

				tableModel.addRow(new Object[] { "false" });
				int count = tableModel.getRowCount();
				if (count > 1) {
					String str = (String) tableModel.getValueAt(count - 2, 1);
					boolean isInt = false;
					int stepNumber = 0;
					try {
						stepNumber = XmlUtility.getSubFigure(str);
						if (stepNumber >= 0)
							isInt = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (isInt) {
						String uid = new java.rmi.server.UID().toString();
						insertStepTemplate(procedureEle, XmlUtility.STEPINTERVAL + stepNumber, uid, technicsNumber);
						tableModel.setValueAt("" + (XmlUtility.STEPINTERVAL + stepNumber), count - 1, 1);
						tableModel.setValueAt(uid, count - 1, 6);
						tableModel.setValueAt("true", count - 1, 7);
						table.setRowSelectionInterval(count - 1, count - 1);
					}
				} else {
					String uid = new java.rmi.server.UID().toString();
					insertStepTemplate(procedureEle, XmlUtility.STEPINTERVAL, uid, technicsNumber);
					tableModel.setValueAt("" + (XmlUtility.STEPINTERVAL), count - 1, 1);
					tableModel.setValueAt(uid, count - 1, 6);
					tableModel.setValueAt("true", count - 1, 7);
					table.setRowSelectionInterval(count - 1, count - 1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (arg0.getSource() == upButton)// 上移按钮
		{
			changeRowValue(true);
		}

		if (arg0.getSource() == downButton)// 下移按钮
		{
			changeRowValue(false);
		}
	}


	/**
	 * 插入工序模板
	 *
	 * @param procedureEle
	 * @param row
	 * @param technicsNumber
	 */
	private void insertStepTemplate(Element procedureEle, int row, String uid, String technicsNumber) {
		XmlUtility.setAttributeValue(procedureEle, "stepNumber", row + "");
		XmlUtility.setAttributeValue(procedureEle, "bsoID", uid);

		List<Element> paceElements = procedureEle.selectNodes("paces/QMProcedureInfo");
		for (Element paceElement : paceElements) {
			String paceId = new UID().toString();
			XmlUtility.setAttributeValue(paceElement, "bsoID", paceId);
			//插入工序模板 工步端白羽文件复用
			List<Element> schemaDatas = XmlUtility.getSchemaDatas(paceElement);
			ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();
			for(Element schemaData : schemaDatas) {
				ArrayList<String> bylist = new ArrayList<String>();
				String bynumber = schemaData.attributeValue("id");
				String version = schemaData.attributeValue("version");
				bylist.add(bynumber);
				bylist.add(bynumber);
				bylist.add(version);
				bylist.add(version);
				lists.add(bylist);
			}
			if(lists.size() > 0) {
				ArrayList<ArrayList<String>> bys = null;
				try {
					bys = ProcessParameterToWCIntf.quoteBaiyuTemplate(lists, technicsNumber, uid, paceId);
				} catch(RemoteException e) {
					throw new RuntimeException(e);
				} catch(InvocationTargetException e) {
					throw new RuntimeException(e);
				}
				if(bys != null && bys.size() > 0){
					Element schemaData = XmlUtility.getSchemaData(paceElement);
					XmlUtility.removeAllChildElements(schemaData);
					for (ArrayList<String> list : bys) {
						Element schemaEle = schemaData.addElement(XmlUtility.SCHEMA_TAG);
						XmlUtility.setAttributeValue(schemaEle,"order",list.get(0));
						XmlUtility.setAttributeValue(schemaEle,"id",list.get(1));
						XmlUtility.setAttributeValue(schemaEle,"name",list.get(2));
						XmlUtility.setAttributeValue(schemaEle,"version",list.get(3));
						XmlUtility.setAttributeValue(schemaEle,"creator",list.get(4));
						XmlUtility.setAttributeValue(schemaEle,"mofitier",list.get(5));
						XmlUtility.setAttributeValue(schemaEle,"createTime",list.get(6));
						XmlUtility.setAttributeValue(schemaEle,"mofityTime",list.get(7));
						XmlUtility.setAttributeValue(schemaEle,"docNumber",list.get(8));
						XmlUtility.setAttributeValue(schemaEle,"tableType",list.get(9));
						XmlUtility.setAttributeValue(schemaEle,"dept",list.get(10));
					}
				}
			}
		}

		List userlist = UserUtil.getCurrentUserOid();
		if ((userlist != null) && (userlist.size() == 3)) {
			String creator = (String) userlist.get(0);
			String creatorOid = (String) userlist.get(1);
			logger.debug("设置当前工序责任人为======" + creatorOid);
			XmlUtility.setAttributeValue(procedureEle, "responser", creatorOid);
		}
		String partOid = technicsElement.attributeValue("partOid");
		if (partOid != null) {
			if(responserGroup == null || "".equals(responserGroup)){
				try {
					responserGroup = TechnicsIntf.getUsertechnicsGroupName(partOid);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			logger.debug("当前工艺零件======" + partOid + "===责任组==" + responserGroup);
			if (responserGroup == null)
				responserGroup = "";
			XmlUtility.setAttributeValue(procedureEle, "responserGroup", responserGroup);
		}

		XmlUtility.addProcedure(technicsElement, procedureEle);
		XmlUtility.orderSteps(technicsElement);
		frame.saveProcess(technicsElement);
		frame.loadTechnics(technicsElement.getDocument());
		XWTreeNode node = frame.technicsTreePanel.getCurrentTechnicsNode();
		if (node == null) {
			return;
		}
		XWTreeObject xo = node.getObject();
		if (!(xo instanceof XWTechnicsTreeObject)) {
			return;
		}
		xo.setTreeCellData(technicsElement);
		try {
			this.frame.initTechnicsRoutePanel(technicsElement);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private JComboBox createBox(JTable table, Object value, int row, int column) {
		JComboBox box = new JComboBox();
		box.addItem("");
		if (value == null || value.toString().trim().length() == 0)
			value = "";
		if (column == 2) {
			if (workShop != null && workShop.size() > 0) {
				List<String> allList = new ArrayList<String>();
				Collection coll = workShop.values();
				Iterator it = coll.iterator();
				while (it.hasNext()) {
					allList.add(String.valueOf(it.next()));
//					box.addItem(it.next());
				}
				Collections.sort(allList);
				for (String str : allList) {
					box.addItem(str);
				}
			}
			box.setSelectedItem(value);
		} else if (column == 4 || column == 5) {
			Object obj = table.getValueAt(row, 2);
			if (obj != null && obj.toString().trim().length() > 0) {
				String shop = obj.toString();
				Collection coll = null;
				if (column == 4) {
					String workShopNumber = getKey(workShop, table.getValueAt(row, column - 2).toString());
					String pdName = table.getValueAt(row, column - 1).toString();
//					workType = ResourceIntf.getWorkTypeByPdName(pdName,workShopNumber);
					workType = ResourceIntf.getSkill(workShopNumber);
					logger.debug("工种========" + workType);
					if (workType != null)
						coll = workType.values();
				} else if (column == 5) {
					workSpace = ResourceIntf.getWorkSpaces(getKey(workShop, shop));
					logger.debug("工位========" + workSpace);
					if (workSpace != null)
						coll = workSpace.values();
				}
				if (coll != null && coll.size() > 0) {
					Iterator it = coll.iterator();
					while (it.hasNext()) {
						box.addItem(it.next());
					}
					if (coll.contains(value)) {
						box.setSelectedItem(value);
					} else {
						box.setSelectedItem("");
					}
				}
			} else {
				box.setSelectedItem("");
			}
		}
		if (column == 2) {
			final JTable curTable = table;
			final int currow = row;
			box.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					curTable.setValueAt("", currow, 4);
					curTable.setValueAt("", currow, 5);
				}
			});
		}
		return box;
	}

	private void changeRowValue(boolean up) {
		if (table.getSelectedRowCount() > 1)
			return;
		int select = table.getSelectedRow();
		if (select < 0)
			return;
		if (up && select == 0)
			return;
		if (!up && select == table.getRowCount() - 1)
			return;
		Object[] obj1 = new Object[table.getColumnCount()];
		Object[] obj2 = new Object[table.getColumnCount()];
		int neighbor;
		if (up)
			neighbor = select - 1;
		else
			neighbor = select + 1;
		for (int i = 0; i < table.getColumnCount(); i++) {
			obj1[i] = table.getValueAt(select, i);
			obj2[i] = table.getValueAt(neighbor, i);
		}
		for (int j = 0; j < table.getColumnCount(); j++) {
			if(j==1)continue;
			table.setValueAt(obj2[j], select, j);
			table.setValueAt(obj1[j], neighbor, j);
		}
		table.setRowSelectionInterval(neighbor, neighbor);
	}

	public boolean check() {
		int RowCount = tableModel.getRowCount();
		Vector vec = new Vector();
		for (int i = 0; i < RowCount; i++) {
			String number = (String) tableModel.getValueAt(i, 1);// 编号
			if (number != null) {
				String text = CommonUtil.trim(number);
				text = XmlUtility.toSemiangle(text);
				number = text;
			}
			if (number == null || number.trim().length() == 0) {
				JOptionPane.showMessageDialog(this, "工序号输入不能为空!", "提示", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			if (vec.contains(number)) {
				JOptionPane.showMessageDialog(this, "工序号不能存在重复数据!", "提示", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			vec.add(number);
		}
		return true;
	}

	public String getKey(Map map, String value) {
		if (map == null || value == null)
			return "NOKEY";
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			Object temp = map.get(key);
			if (temp.equals(value)) {
				return key.toString();
			}
		}
		return "NOKEY";
	}

	public Vector showDialog() {
		return vv;
	}

	public void clear() {
		tableModel.setRowCount(0);
	}

	public void updataTechnicsData() {
		vv.clear();
		changePreID();
		changePreStep();
		if (table.getCellEditor() != null)
			table.getCellEditor().stopCellEditing();
		if (!check())
			return;
		int RowCount = tableModel.getRowCount();
		for (int i = 0; i < RowCount; i++) {
			String rowData[] = new String[10];
			logger.debug("============" + (String) tableModel.getValueAt(i, 0));
			rowData[0] = (String) tableModel.getValueAt(i, 0);// 关键标识
			rowData[1] = (String) tableModel.getValueAt(i, 1);// 编号
			rowData[2] = (String) tableModel.getValueAt(i, 2);// 制造单位
			rowData[3] = (String) tableModel.getValueAt(i, 3);// 名称
			rowData[4] = (String) tableModel.getValueAt(i, 4);// 工种
			rowData[5] = (String) tableModel.getValueAt(i, 5);// 工位
			rowData[6] = (String) tableModel.getValueAt(i, 6);// ID
			rowData[7] = String.valueOf(tableModel.getValueAt(i, 8));// 前置工序
			rowData[8] = String.valueOf(tableModel.getValueAt(i, 9));// 前置工序ID
			rowData[9] = getPreStepbyID(rowData[8]);//preStep
			if (rowData[1] != null && (!rowData[1].trim().equals(""))) {
				String text = CommonUtil.trim(rowData[1]);
				rowData[1] = XmlUtility.toSemiangle(text);
				vv.add(rowData);
			}
		}
		frame.quickCreateProcedures();
	}
	/**
	 * 保存工艺路线图时，设置前置工序
	* @author jyx
	* @date 2018-7-19
	* @param procesMap
	* @param preIdMap
	 */
	public void updataTechnicsDataByMap(Map<String, String> procesMap,Map<String, String> preIdMap) {
		int RowCount = tableModel.getRowCount();
		for (int i = 0; i < RowCount; i++) {
			//String numbertest = (String) tableModel.getValueAt(i, 1);// 编号
			String id = (String) tableModel.getValueAt(i, 6);// ID
			//String preNumbertest = String.valueOf(tableModel.getValueAt(i, 8));// 前置工序
			//String preIdtest = String.valueOf(tableModel.getValueAt(i, 9));// 前置工序ID

			String preNumber = procesMap.get(id);// 前置工序
			String preId = preIdMap.get(id);
			if(preNumber != null){
				tableModel.setValueAt(preNumber, i, 8);
			}
			if(preId != null){
				tableModel.setValueAt(preId, i, 9);
			}
		}
	}

	/**
	 * 根据id获取preStep
	 * @param str
	 * @return
	 */
	private String getPreStepbyID(String str) {
		int row = tableModel.getRowCount();
		ArrayList<String> list = new ArrayList<String>();
		String value = "";
		if("".equals(str)){
			return "";
		}else{
			if(str.contains(",")){
				String[] strs = str.split(",");
				for(int i = 0; i < row;i++){
					String number = String.valueOf(tableModel.getValueAt(i, 1));
					String name = String.valueOf(tableModel.getValueAt(i, 3));
					String bsoID = String.valueOf(tableModel.getValueAt(i, 6));
					for(int j = 0; j < strs.length; j++){
						if(bsoID.equals(strs[j])){
							list.add(number + "_" + name);
						}
					}
				}
				for(int i = 0; i < list.size();i++){
					if("".equals(value)){
						value = list.get(i);
					}else{
						value = value + "," + list.get(i);
					}
				}
			}else{
				for(int i = 0; i < row;i++){
					String number = String.valueOf(tableModel.getValueAt(i, 1));
					String name = String.valueOf(tableModel.getValueAt(i, 3));
					String bsoID = String.valueOf(tableModel.getValueAt(i, 6));
					if(bsoID.equals(str)){
						value = number + "_" + name;
					}
				}
			}
		}
		return value;
	}

	public void stopTableCellEditing() {
		if (table.getCellEditor() != null)
			table.getCellEditor().stopCellEditing();
	}

	public void setUIEnabled(boolean b) {
		isEdit = b;
		table.setEnabled(b);
		batchCreate.setEnabled(b);
		proveButton.setEnabled(b);
		addButton.setEnabled(b);
		insertTemplateButton.setEnabled(b);
		removeButton.setEnabled(b);
		upButton.setEnabled(b);
		downButton.setEnabled(b);
		zidongpaixu.setEnabled(b);
	}

	public void update(Observable o, Object arg) {
		if (o != null && o instanceof CommonObservable) {
			CommonObservable co = (CommonObservable) o;
			int type = co.getType();
			if (type == 4)// 表示工序名
			{
				if (arg != null && arg instanceof String ) {
					String commstring = (String)arg;
					logger.debug("M====" + arg);
						if (table != null && table.getRowCount() > 0) {
							int[] rows = table.getSelectedRows();
							if (rows != null && rows.length == 1) {
								int rowIndex = rows[0];
								if (rowIndex >= 0 && rowIndex < table.getRowCount()) {
									stopTableCellEditing();
									String isEdit = (String) table.getValueAt(rowIndex, 7);
									if (isEdit != null && isEdit.equals("false")) {
										return;
									}
//									table.setValueAt(m.get("workShopName"), rowIndex, 2);
									table.setValueAt(commstring, rowIndex, 3);
//									table.setValueAt(m.get("shopTypeName"), rowIndex, 4);
									updateUI();
								}
							}
						}
				}
			}
		}
	}

	/**
	 * 处理变动之后正确的前置工序 preBsoID add by zhuhao 2017.12.17
	 */
	void changePreID(){
		int row = tableModel.getRowCount();
		ArrayList<String> bsoIDs = new ArrayList<String>();//所有行的bsoID
		if(row > 0){
			for(int i = 0; i < row; i++){//获取每行bsoID
				bsoIDs.add(String.valueOf(tableModel.getValueAt(i, 6)));
			}
			for(int i = 0; i < row; i++){
				String number = String.valueOf(tableModel.getValueAt(i, 1));
				if(i == 0){
					tableModel.setValueAt("", i, 8);
					tableModel.setValueAt("", i, 9);
					continue;
				}
				String truePreBsoID = "";
				ArrayList<String> numberList = getNumberbyID(bsoIDs, row);//变动后剩余的序列号
				ArrayList<String> trueNumbers = createNumberList(numberList, number);//可作前置工序的序列号
				ArrayList<String> trueBsoIDs = getIDbyNunber(trueNumbers,row);//可作前置工序的ID
				String preBsoID = (String.valueOf(tableModel.getValueAt(i, 9)));//每行前置工序ID
				if(preBsoID.contains(",")){
					ArrayList<String> truePreBsoIDs  = new ArrayList<String>();
					String[] preBsoIDs = preBsoID.split(",");
					//得到正确的前置工序
					for(int j = 0; j < preBsoIDs.length; j++){
						if(trueBsoIDs.contains(preBsoIDs[j])){
							truePreBsoIDs.add(preBsoIDs[j]);
						}
					}
					if(truePreBsoIDs != null && !truePreBsoIDs.isEmpty()){
						for(int j = 0; j < truePreBsoIDs.size(); j++){
							if("".equals(truePreBsoID)){
								truePreBsoID = truePreBsoIDs.get(j);
							}else{
								truePreBsoID = truePreBsoID + "," +truePreBsoIDs.get(j);
							}
						}
					}else{
						truePreBsoID = String.valueOf(tableModel.getValueAt(i-1, 6));
					}
					//tableModel.setValueAt(truePreBsoID, i, 9);
				}else{
					if(trueBsoIDs.contains(preBsoID)){
						truePreBsoID = preBsoID;
					}else{
						//truePreBsoID = String.valueOf(tableModel.getValueAt(i-1, 6));
					}
					//tableModel.setValueAt(truePreBsoID, i, 9);
				}
			}
		}
	}
	/**
	 * 前置工序列根据id变动
	 */
	void changePreStep(){
		int row = tableModel.getRowCount();
		for(int i = 1; i < row; i++){
			String value = "";
			ArrayList<String> list = new ArrayList<String>();
			ArrayList<String> preStep = new ArrayList<String>();
			String preBsoID = String.valueOf(tableModel.getValueAt(i, 9));
			if(preBsoID.contains(",")){
				String[] preBsoIDs = preBsoID.split(",");
				for(int j = 0; j < preBsoIDs.length; j++){
					list.add(preBsoIDs[j]);
				}
				preStep = getNumberbyID(list, row);
				for(int j = 0; j < preStep.size(); j++){
					if("".equals(value)){
						value = preStep.get(j);
					}else{
						value = value + "," +preStep.get(j);
					}
				}
			}else{
				list.add(preBsoID);
				preStep = getNumberbyID(list, row);
				for(int j = 0; j < preStep.size(); j++){
					value = preStep.get(j);
				}
			}
			tableModel.setValueAt(value, i, 8);
		}
	}


	/**
	 * 根据序列号获取ID
	 * @param Numbers
	 * @param row
	 * @return
	 */
	private ArrayList<String> getIDbyNunber(ArrayList<String> Numbers, int row) {
		ArrayList<String> bsoIDs = new ArrayList<String>();
		for(int i = 0; i < row;i++){
			String number = String.valueOf(tableModel.getValueAt(i, 1));
			String bsoID = String.valueOf(tableModel.getValueAt(i, 6));
			for(int j = 0; j < Numbers.size(); j++){
				if(number.equals(Numbers.get(j))){
					bsoIDs.add(bsoID);
				}
			}
		}
		return bsoIDs;
	}

	/**
	 * 根据ID获取序列号
	 * @param bsoIDs
	 * @param row
	 * @return
	 */
	private ArrayList<String> getNumberbyID(ArrayList<String> bsoIDs, int row) {
		ArrayList<String> numberList = new ArrayList<String>();
		for(int i = 0; i < row; i++){
			String number = String.valueOf(tableModel.getValueAt(i, 1));
			String bsoID = String.valueOf(tableModel.getValueAt(i, 6));
			for(int j = 0; j < bsoIDs.size(); j++){
				if(bsoID.equals(bsoIDs.get(j))){
					numberList.add(number);
				}
			}
		}
		return numberList;
	}
	/**
	 * 根据序列号和当前工序获取可以作前置工序的序列号
	 * @param numberList
	 * @param preValue
	 * @return
	 */
	public static ArrayList<String> createNumberList(ArrayList<String> numberList, String preValue) {
		ArrayList<String> inList = new ArrayList<String>();
		int pre = Integer.parseInt(preValue);
		for(String str : numberList) {
			int l = Integer.parseInt(str);
			if(l < pre)	{
				inList.add(str);
			}
		}
		return inList;
	}

}