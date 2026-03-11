package com.glaway.mpm.view;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.InputLimited;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.glaway.mpm.wcIntf.TechnicsIntf;

/**
 * 主工艺文件的辅制工艺文件信息
 *
 * @author LongXiuChuan 2014/6/4
 */
public class FuZhiTechnicsJPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Container parentPanel;

	private JButton jButton1;
	private JButton jButton2;
	private JButton jButton3;
	private JButton addButton;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JPanel jPanel1;
	private JPanel jPanel2;
	private JScrollPane jScrollPane2;
	private JTable jTable1;
	private JTextField jTextField1;
	private JTextField jTextField2;

	/**
	 * Creates new form FuZhiTechnicsJPanel
	 */
	public FuZhiTechnicsJPanel(Container parentPanel) {
		this.parentPanel = parentPanel;
		initComponents();
		setName("FuZhiTechnicsJPanel");
	}

	private void initComponents() {
		jPanel1 = new JPanel();
		jLabel1 = new JLabel();
		jLabel2 = new JLabel();
		jTextField1 = new JTextField();
		jTextField2 = new JTextField();
		jButton1 = new JButton();
		jPanel2 = new JPanel();
		jScrollPane2 = new JScrollPane();
		jTable1 = new JTable();
		jButton3 = new JButton();
		jButton2 = new JButton();
		addButton = new JButton();

		jPanel1.setBorder(BorderFactory.createTitledBorder("添加辅制工艺"));

		jLabel1.setText("编   号：");

		jLabel2.setText("名   称：");

		jButton1.setText("添  加");
		jButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		jPanel1.setVisible(false);
		GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING,
																false)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel2)
																		.addPreferredGap(
																				LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jTextField2))
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel1)
																		.addPreferredGap(
																				LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jTextField1,
																				GroupLayout.PREFERRED_SIZE,
																				231,
																				GroupLayout.PREFERRED_SIZE)))
										.addGap(35, 35, 35)
										.addComponent(jButton1)
										.addContainerGap(357, Short.MAX_VALUE)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addGroup(
				jPanel1Layout
						.createSequentialGroup()
						.addGap(12, 12, 12)
						.addGroup(
								jPanel1Layout
										.createParallelGroup(
												GroupLayout.Alignment.LEADING)
										.addComponent(jLabel1)
										.addComponent(jTextField1,
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE))
						.addGap(15, 15, 15)
						.addGroup(
								jPanel1Layout
										.createParallelGroup(
												GroupLayout.Alignment.BASELINE)
										.addComponent(jLabel2)
										.addComponent(jTextField2,
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(jButton1))
						.addContainerGap(GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));

		jPanel2.setBorder(BorderFactory.createTitledBorder("辅制工艺信息"));

		jTable1.setModel(new DefaultTableModel(new Object[][] {}, new String[] {
				"编号", "名称", "页数", "责任部门" }) {
			private static final long serialVersionUID = 1L;
			Class[] types = new Class[] { String.class, String.class,
					String.class, String.class };
			boolean[] canEdit = new boolean[] { true, true, true, true };

			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});


		//获取到所有的车间组，然后自动定位到当前用户所在的组
        List<String> allList = new ArrayList<String>();
        Map<String,String> workShop = ResourceIntf.getWorkShops();
        if (workShop != null && workShop.size() > 0) {
            Collection<String> coll = workShop.values();
            Iterator<String> it = coll.iterator();
            while (it.hasNext()) {
                String temp = (String) it.next();
                if (temp != null && temp.trim().length() > 0) {
                    allList.add(temp);
                }
            }

            Collections.sort(allList);
        }
		String groupName = "";
		javax.swing.JComboBox  box16= new javax.swing.JComboBox(allList.toArray());
        try {
            groupName = TechnicsIntf.getUsertechnicsGroupName();
        } catch (RemoteException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        } catch (InvocationTargetException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        if(allList.contains(groupName)) {
            box16.setSelectedItem(groupName);
        }
        TableColumn deptColumn = jTable1.getColumn("责任部门");
        deptColumn.setCellEditor(new DefaultCellEditor(box16));
		TableColumn pagesColumn = jTable1.getColumn("页数");
		JTextField inputInt = new JTextField();
		inputInt.setDocument(new InputLimited(10000, true));
		pagesColumn.setCellEditor(new DefaultCellEditor(inputInt));

		jTable1.setRowHeight(25);
		jTable1.getTableHeader().setReorderingAllowed(false);
		jScrollPane2.setViewportView(jTable1);
		jTable1.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				checkNumber(e);
			}
		});

		jTable1.getColumnModel().getColumn(2).setMinWidth(75);
		jTable1.getColumnModel().getColumn(2).setMaxWidth(75);
		jTable1.getColumnModel().getColumn(2).setPreferredWidth(75);
		jTable1.getColumnModel().getColumn(3).setMinWidth(75);
		jTable1.getColumnModel().getColumn(3).setMaxWidth(75);
		jTable1.getColumnModel().getColumn(3).setPreferredWidth(75);

		jButton3.setText("删除选中");
		jButton3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButton3ActionPerformed(evt);
			}
		});

		jButton2.setText("保存修改");
		jButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButton2ActionPerformed(evt);
			}
		});

		addButton.setText("添加一行");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addButtonActionPerformed(evt);
			}
		});

		GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout
				.setHorizontalGroup(jPanel2Layout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								GroupLayout.Alignment.TRAILING,
								jPanel2Layout
										.createSequentialGroup()
										.addContainerGap(661, Short.MAX_VALUE)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																GroupLayout.Alignment.TRAILING)
														.addComponent(addButton)
														.addComponent(jButton3)
														.addComponent(jButton2)
														)
										.addGap(30, 30, 30))
						.addGroup(
								jPanel2Layout
										.createParallelGroup(
												GroupLayout.Alignment.LEADING)
										.addGroup(
												jPanel2Layout
														.createSequentialGroup()
														.addComponent(
																jScrollPane2,
																GroupLayout.PREFERRED_SIZE,
																655,
																GroupLayout.PREFERRED_SIZE)
														.addGap(0, 105,
																Short.MAX_VALUE))));
		jPanel2Layout.setVerticalGroup(jPanel2Layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel2Layout.createSequentialGroup().addContainerGap()
						.addComponent(addButton).addGap(50, 50, 50)
						.addComponent(jButton2).addGap(50, 50, 50)
						.addComponent(jButton3).addGap(50, 50, 50))
				.addGroup(
						jPanel2Layout.createParallelGroup(
								GroupLayout.Alignment.LEADING).addComponent(
								jScrollPane2, GroupLayout.DEFAULT_SIZE, 232,
								Short.MAX_VALUE)));

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		jPanel1,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.PREFERRED_SIZE)
																.addGap(0,
																		0,
																		Short.MAX_VALUE))
												.addComponent(
														jPanel2,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE))
								.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jPanel1, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(
								LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jPanel2, GroupLayout.DEFAULT_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE).addGap(47, 47, 47)));
	}

	protected void checkNumber(TableModelEvent e) {
		System.out.println(e.getSource().getClass());
		System.out.println(e.getLastRow()+","+e.getColumn());
		if(e.getLastRow() != -1 && e.getColumn() != -1 && e.getColumn() == 0) {
			String number = String.valueOf(jTable1.getValueAt(e.getLastRow(), e.getColumn()));
			if (number != null && !"".equals(number)) {
				int rows = jTable1.getRowCount();
				if (rows > 1) {
					for (int i = 0; i < rows; i++) {
						if (i != e.getLastRow()) {
							String tempNumber = String.valueOf(jTable1.getValueAt(i, 0));
							if (number.equals(tempNumber)) {
								JOptionPane.showMessageDialog(jTable1, "编号不能重复，请重新输入！");
								jTable1.setValueAt("", e.getLastRow(), e.getColumn());
								break;
							}
						}
					}
				}
			}
		}
	}

	private void jButton1ActionPerformed(ActionEvent evt) {
		String number = jTextField1.getText();
		String name = jTextField2.getText();
		if (number == null || "".equals(number) || name == null
				|| "".equals(name)) {
			JOptionPane.showMessageDialog(parentPanel, "编号或名称不能为空！");
			return;
		}
		DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();

		if (checkNumber(tableModel, number)) {
			JOptionPane.showMessageDialog(parentPanel, "该编号已经存在，请重新填写！");
			return;
		}

		addOneRow(tableModel);
		int rowCount = jTable1.getRowCount();
		tableModel.setValueAt(number, rowCount - 1, 0);
		tableModel.setValueAt(name, rowCount - 1, 1);

		jTextField1.setText("");
		jTextField2.setText("");
	}

	private void addButtonActionPerformed(ActionEvent evt) {
		DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
		addOneRow(tableModel);
	}

	private void jButton2ActionPerformed(ActionEvent evt) {
		if(!checkIsNull()) {
			JOptionPane.showMessageDialog(jTable1, "编号，名称，责任部门不能为空，请填写完整！");
			return;
		}

		DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
		int rows = tableModel.getRowCount();
		if (rows == 0) {
			JOptionPane.showMessageDialog(jTable1, "没有数据需要保存！");
			return;
		}

		int flag = JOptionPane.showConfirmDialog(jTable1, "确定保存吗？", "确认", JOptionPane.OK_CANCEL_OPTION);
		if (flag == JOptionPane.YES_OPTION) {
			Element techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) parentPanel).getCurrentTechnics());
			Element fzgy = XmlUtility.getFZTechnicsElement(techele);
			XmlUtility.deleteAllChildElements(fzgy);

			for (int i = 0; i < rows; i++) {
				Element element = createFZTechnicElement(i, tableModel);
				fzgy.add(element);
			}

			String techPath = WorkSpaceUtil.getTechnicsDirectory(techele.attributeValue("technicsNumber"));
			String xmlFilePath = techPath + File.separator + techele.attributeValue("technicsNumber") + ".xml";
			try {
				XmlUtility.saveDocument(techele.getDocument(), xmlFilePath);
				JOptionPane.showMessageDialog(jTable1, "保存成功！");
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(jTable1, "保存失败,错误信息:" + e1.getLocalizedMessage());
				e1.printStackTrace();
			}
		}
	}

	private boolean checkIsNull() {
		boolean flag = true;
		int rows = jTable1.getRowCount();
		if (rows > 0) {
			for (int i = 0; i < rows; i++) {
				String number = CommonUtil.objectToString(jTable1.getValueAt(i, 0));
				String name = CommonUtil.objectToString(jTable1.getValueAt(i, 1));
				//modify by machongqi,2015-6-9
				//String pages = CommonUtil.objectToString(jTable1.getValueAt(i, 2));
				String dept = CommonUtil.objectToString(jTable1.getValueAt(i, 3));
				if("".equals(number) || "".equals(name) || "".equals(dept)) {
					flag = false;
					break;
				}
				//modify by machongqi,end
			}
		}
		return flag;
	}

	private void jButton3ActionPerformed(ActionEvent evt) {
		int row = jTable1.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(jTable1, "请选择需要删除的行！");
			return;
		}
		DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
		tableModel.removeRow(row);
	//
		Element techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) parentPanel).getCurrentTechnics());
		Element fzgy = XmlUtility.getFZTechnicsElement(techele);
		XmlUtility.deleteAllChildElements(fzgy);
		int rows = tableModel.getRowCount();
		for (int i = 0; i < rows; i++) {
			Element element = createFZTechnicElement(i, tableModel);
			fzgy.add(element);
		}

		String techPath = WorkSpaceUtil.getTechnicsDirectory(techele.attributeValue("technicsNumber"));
		String xmlFilePath = techPath + File.separator + techele.attributeValue("technicsNumber") + ".xml";
		try {
			XmlUtility.saveDocument(techele.getDocument(), xmlFilePath);
			JOptionPane.showMessageDialog(jTable1, "删除成功！");
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(jTable1, "删除失败,错误信息:" + e1.getLocalizedMessage());
			e1.printStackTrace();
		}
		//
	}

	private Element createFZTechnicElement(int i, TableModel tm) {
		Element element = DocumentHelper.createElement("FZTECHNICS");
		XmlUtility.setAttributeValue(element, "number", String.valueOf(tm.getValueAt(i, 0)));
		XmlUtility.setAttributeValue(element, "name", String.valueOf(tm.getValueAt(i, 1)));
		XmlUtility.setAttributeValue(element, "pages", String.valueOf(tm.getValueAt(i, 2)));
		XmlUtility.setAttributeValue(element, "department", String.valueOf(tm.getValueAt(i, 3)));
		return element;
	}

	private void addOneRow(DefaultTableModel tableModel) {
		Vector vector = new Vector();
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			vector.add("");
		}
		tableModel.addRow(vector);
	}

	private boolean checkNumber(DefaultTableModel tableModel, String number) {
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String tempNumber = String.valueOf(tableModel.getValueAt(i, 0));
			if (number.equals(tempNumber)) {
				return true;
			}
		}
		return false;
	}

	public void setTableValues(Element techEle) {
		DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
		tableModel.setRowCount(0);
		List<Element> list = XmlUtility.getFZTechnics(techEle);
		if (list != null && !list.isEmpty()) {
			for (Element element : list) {
				addOneRow(tableModel);
				int rowCount = jTable1.getRowCount();
				tableModel.setValueAt(element.attributeValue("number"), rowCount - 1, 0);
				tableModel.setValueAt(element.attributeValue("name"), rowCount - 1, 1);
				tableModel.setValueAt(element.attributeValue("pages"), rowCount - 1, 2);
				tableModel.setValueAt(element.attributeValue("department"), rowCount - 1, 3);
			}
		}
	}

	public void setUIEnabled(boolean b) {
		jButton1.setEnabled(b);
		jButton2.setEnabled(b);
		jButton3.setEnabled(b);
		addButton.setEnabled(b);
		jTable1.setEnabled(b);
		jTextField1.setEnabled(b);
		jTextField2.setEnabled(b);
	}

}
