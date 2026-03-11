package com.glaway.mpm.erp;

import com.glaway.mpm.util.XmlUtility;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class GWTechnicsCLDEJPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Container parentPanel;
	JPanel panel = new JPanel();

	private DefaultTableModel tableModel = new DefaultTableModel() {
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int column) {
			if(column==1){
				return true;
			}
			return false;
		}
	};

	private JTable table = new JTable(tableModel);

	public GWTechnicsCLDEJPanel(Container parentPanel) {
		this.parentPanel = parentPanel;
		initComponennt();
		setName("clde");
	}

	public JTable getTable(){
		return table;
	}

	private void initComponennt() {
		setLayout(new GridBagLayout());
		table.setDefaultRenderer(Object.class, new MyDefaultTableCellRenderer());

		//"存货编码","存货名称","数量","型号牌号","规格","技术条件","生产厂家","主计量单位","附加条件","供应状态/热处理"
		//"存货编码","存货名称","下料尺寸","可制件数","型号牌号","规格","技术条件","生产厂家","主计量单位","附加条件","供应状态/热处理"
		//"存货编码","存货名称","试件尺寸","试件可制件数","试件数量","型号牌号","规格","技术条件","生产厂家","主计量单位","附加条件","供应状态/热处理"
		tableModel.addColumn("序号");
		tableModel.addColumn("是否已匹配");
		tableModel.addColumn("历史数据");
		tableModel.addColumn("定额类型");
		tableModel.addColumn("存货编码");
		tableModel.addColumn("存货名称");
		tableModel.addColumn("下料尺寸");
		tableModel.addColumn("可制件数");
		tableModel.addColumn("数量");
		tableModel.addColumn("试件尺寸");
		tableModel.addColumn("试件可制件数");
		tableModel.addColumn("试件数量");
		tableModel.addColumn("单位");
		tableModel.addColumn("型号牌号");
		tableModel.addColumn("规格");
		tableModel.addColumn("技术条件");
		tableModel.addColumn("生产厂家");
		tableModel.addColumn("主计量单位");
		tableModel.addColumn("附加条件");
		tableModel.addColumn("供应状态/热处理");
		tableModel.addColumn("质量等级");
		tableModel.addColumn("封装形式");
		tableModel.addColumn("精度等级");
		tableModel.addColumn("螺纹规格/公称尺寸");
		tableModel.addColumn("机械性能等级");
		tableModel.addColumn("电参考特选要求");
		tableModel.addColumn("备注");

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);

		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(1).setCellEditor(table.getDefaultEditor(Boolean.class));
		columnModel.getColumn(1).setCellRenderer(table.getDefaultRenderer(Boolean.class));

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(table.getTableHeader(), BorderLayout.PAGE_START);
		p.add(table, BorderLayout.CENTER);

		JScrollPane pane = new JScrollPane(p,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		add(pane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
	}

	public void setTableValues(Element technicsElement) {
		clearTable();
//		System.out.println("-----------clde info------------");
//		System.out.println(technicsElement.asXML());
		Element clde = XmlUtility.getTechnicsCLDEElement(technicsElement);
		if(clde == null) {
			return ;
		}

		int row = 0;
		int n = 1;
		//"存货编码","存货名称","数量","下料尺寸","可制件数","试件尺寸","试件可制件数","试件数量","型号牌号","规格","技术条件","生产厂家","主计量单位","附加条件","供应状态/热处理"
		List<Element> ycl = XmlUtility.getTechnicsYCLDE(clde);
		if(ycl != null && !ycl.isEmpty()) {
			for (Element element : ycl) {
				Vector vector = new Vector();
				for (int j = 0; j < table.getColumnCount(); j++) {
					if(j==1){
						vector.add(false);
					}else{
						vector.add("");
					}
				}
				tableModel.addRow(vector);
				tableModel.setValueAt(n, row, 0);
				String tabType = element.attributeValue("tabType");
				if (!"".equals(tabType)&&!"null".equals(tabType)&&tabType!=null) {
					tableModel.setValueAt(true,row,1);
					tableModel.setValueAt("否", row, 2);
				}else{
					tableModel.setValueAt(false,row,1);
					tableModel.setValueAt("是", row, 2);
				}
				tableModel.setValueAt("原材料", row, 3);
				tableModel.setValueAt(element.attributeValue("chbm"), row, 4);
				tableModel.setValueAt(element.attributeValue("chmc"), row, 5);
				tableModel.setValueAt(element.attributeValue("xlcc"), row, 6);
				tableModel.setValueAt(element.attributeValue("kzjs"), row, 7);
				tableModel.setValueAt(element.attributeValue("sl"), row, 8);
				tableModel.setValueAt("", row, 9);
				tableModel.setValueAt("", row, 10);
				tableModel.setValueAt("", row, 11);
				tableModel.setValueAt(element.attributeValue("dw"), row, 12);
				tableModel.setValueAt(element.attributeValue("xhph"), row, 13);
				tableModel.setValueAt(element.attributeValue("gg"), row, 14);
				tableModel.setValueAt(element.attributeValue("jstj"), row, 15);
				tableModel.setValueAt(element.attributeValue("sccj"), row, 16);
				tableModel.setValueAt(element.attributeValue("zjldw"), row, 17);
				tableModel.setValueAt(element.attributeValue("fjtj"), row, 18);
				tableModel.setValueAt(element.attributeValue("gyztrcl"), row, 19);

				tableModel.setValueAt(element.attributeValue("zldj"), row, 20);
				tableModel.setValueAt(element.attributeValue("fzxs"), row, 21);
				tableModel.setValueAt(element.attributeValue("jddj"), row, 22);
				tableModel.setValueAt(element.attributeValue("lwgg"), row, 23);
				tableModel.setValueAt(element.attributeValue("jxxndj"), row, 24);
				tableModel.setValueAt(element.attributeValue("dcstxyq"), row, 25);

				tableModel.setValueAt(element.attributeValue("comment"), row, 26);

				row++;
				n++;
			}
		}

		List<Element> zycl = XmlUtility.getTechnicsZYCLDE(clde);
		if(zycl != null && !zycl.isEmpty()) {
			for (Element element : zycl) {
				Vector vector = new Vector();
				for (int j = 0; j < table.getColumnCount(); j++) {
					if(j==1){
						vector.add(false);
					}else{
						vector.add("");
					}
				}
				tableModel.addRow(vector);
				tableModel.setValueAt(n, row, 0);
				String tabType = element.attributeValue("tabType");
				if (!"".equals(tabType)&&!"null".equals(tabType)&&tabType!=null) {
					tableModel.setValueAt(true,row,1);
					tableModel.setValueAt("否", row, 2);
				}else{
					tableModel.setValueAt(false,row,1);
					tableModel.setValueAt("是", row, 2);
				}
				tableModel.setValueAt("主要材料", row, 3);
				tableModel.setValueAt(element.attributeValue("chbm"), row, 4);
				tableModel.setValueAt(element.attributeValue("chmc"), row, 5);
				tableModel.setValueAt(element.attributeValue("xlcc"), row, 6);
				tableModel.setValueAt("", row, 7);
				tableModel.setValueAt(element.attributeValue("sl"), row, 8);
				tableModel.setValueAt("", row, 9);
				tableModel.setValueAt("", row, 10);
				tableModel.setValueAt("", row, 11);
				tableModel.setValueAt(element.attributeValue("dw"), row, 12);
				tableModel.setValueAt(element.attributeValue("xhph"), row, 13);
				tableModel.setValueAt(element.attributeValue("gg"), row, 14);
				tableModel.setValueAt(element.attributeValue("jstj"), row, 15);
				tableModel.setValueAt(element.attributeValue("sccj"), row, 16);
				tableModel.setValueAt(element.attributeValue("zjldw"), row, 17);
				tableModel.setValueAt(element.attributeValue("fjtj"), row, 18);
				tableModel.setValueAt(element.attributeValue("gyztrcl"), row, 19);
				tableModel.setValueAt(element.attributeValue("zldj"), row, 20);
				tableModel.setValueAt(element.attributeValue("fzxs"), row, 21);
				tableModel.setValueAt(element.attributeValue("jddj"), row, 22);
				tableModel.setValueAt(element.attributeValue("lwgg"), row, 23);
				tableModel.setValueAt(element.attributeValue("jxxndj"), row, 24);
				tableModel.setValueAt(element.attributeValue("dcstxyq"), row, 25);
				tableModel.setValueAt(element.attributeValue("comment"), row, 26);

				row++;
				n++;
			}
		}

		List<Element> sjycl = XmlUtility.getTechnicsSJYCLDE(clde);
		if(sjycl != null && !sjycl.isEmpty()) {
			for (Element element : sjycl) {
				Vector vector = new Vector();
				for (int j = 0; j < table.getColumnCount(); j++) {
					if(j==1){
						vector.add(false);
					}else{
						vector.add("");
					}
				}
				tableModel.addRow(vector);
				tableModel.setValueAt(n, row, 0);
				String tabType = element.attributeValue("tabType");
				if (!"".equals(tabType)&&!"null".equals(tabType)&&tabType!=null) {
					tableModel.setValueAt(true,row,1);
					tableModel.setValueAt("否", row, 2);
				}else{
					tableModel.setValueAt(false,row,1);
					tableModel.setValueAt("是", row, 2);
				}
				tableModel.setValueAt("试件原材料", row, 3);
				tableModel.setValueAt(element.attributeValue("chbm"), row, 4);
				tableModel.setValueAt(element.attributeValue("chmc"), row, 5);
				tableModel.setValueAt(element.attributeValue("xlcc"), row, 6);
				tableModel.setValueAt("", row, 7);
				tableModel.setValueAt("", row, 8);
				tableModel.setValueAt(element.attributeValue("sjcc"), row, 9);
				tableModel.setValueAt(element.attributeValue("sjkzjs"), row, 10);
				tableModel.setValueAt(element.attributeValue("sjsl"), row, 11);
				tableModel.setValueAt(element.attributeValue("dw"), row, 12);
				tableModel.setValueAt(element.attributeValue("xhph"), row, 13);
				tableModel.setValueAt(element.attributeValue("gg"), row, 14);
				tableModel.setValueAt(element.attributeValue("jstj"), row, 15);
				tableModel.setValueAt(element.attributeValue("sccj"), row, 16);
				tableModel.setValueAt(element.attributeValue("zjldw"), row, 17);
				tableModel.setValueAt(element.attributeValue("fjtj"), row, 18);
				tableModel.setValueAt(element.attributeValue("gyztrcl"), row, 19);
				tableModel.setValueAt(element.attributeValue("zldj"), row, 20);
				tableModel.setValueAt(element.attributeValue("fzxs"), row, 21);
				tableModel.setValueAt(element.attributeValue("jddj"), row, 22);
				tableModel.setValueAt(element.attributeValue("lwgg"), row, 23);
				tableModel.setValueAt(element.attributeValue("jxxndj"), row, 24);
				tableModel.setValueAt(element.attributeValue("dcstxyq"), row, 25);

				tableModel.setValueAt(element.attributeValue("comment"), row, 26);

				row++;
				n++;
			}
		}
	}

	public void clearTable() {
		tableModel.setRowCount(0);
	}

	public void setUIEnabled(boolean b) {
		table.setEnabled(b);
	}

	class MyDefaultTableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	            int row, int column) {
	        if (row % 2 == 0)  {
	            setBackground(Color.white); //设置奇数行底色
	        } else if (row % 2 == 1) {
	            setBackground(new Color(206, 231, 255)); //设置偶数行底色
	            //(206, 231, 255)
	        }
	        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	    }
	}
}
