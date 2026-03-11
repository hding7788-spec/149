package com.glaway.mpm.pbombuilder.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.pbombuilder.data.PBOMReleasedValidatorBean;
import com.glaway.mpm.pbombuilder.tree.CmTree;


public class PBOMReleasedValidatorPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Container parentPanel;
	private JPanel panel = new JPanel();
	private CmTree tree;
	public static boolean isOk = true;

	private DefaultTableModel tableModel = new DefaultTableModel() {
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private JTable table = new JTable(tableModel);
	private String[] tableHeader;

	public PBOMReleasedValidatorPanel(Container parentPanel,CmTree tree) {
		this.parentPanel = parentPanel;
		this.tree = tree;
		initComponennt();
		setName("PBOM完整性检查");
		//setSize(800, 600);
	}

	private void initComponennt() {
		setLayout(new GridBagLayout());
		table.setDefaultRenderer(Object.class, new MyDefaultTableCellRenderer());

		tableHeader = new String[]{"序号","是否通过","图号","名称","阶段标记","关重件标记","零组件生产类型","工艺路线","物资编码","主工艺编号","主工艺状态","材料定额状态"};
		for (String string : tableHeader) {
			tableModel.addColumn(string);
		}

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);

		JScrollPane jScrollPane1 = new JScrollPane();

		jScrollPane1.setViewportView(table);

		JPanel jPanel2 = new JPanel();
		GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addComponent(
				jScrollPane1, GroupLayout.DEFAULT_SIZE, 800,
				Short.MAX_VALUE));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addGroup(
				jPanel2Layout.createSequentialGroup().addComponent(jScrollPane1,
								GroupLayout.PREFERRED_SIZE, 450,
								GroupLayout.PREFERRED_SIZE)
						.addGap(0, 10, Short.MAX_VALUE)));

		add(jPanel2, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
	}

	public void setTableValues(List<PBOMReleasedValidatorBean> returnList) {
		clearTable();

		int row = 0;
		int n = 1;

		for (PBOMReleasedValidatorBean bean : returnList) {
			Vector vector = new Vector();
			for (int j = 0; j < table.getColumnCount(); j++) {
				vector.add("");
			}
			tableModel.addRow(vector);
			//"图号","名称","阶段标记","关重件标记","零组件生产类型","工艺路线","物资编码","主工艺编号","主工艺状态","材料定额状态"
			tableModel.setValueAt(n, row, 0);
			tableModel.setValueAt(bean.getIsOk(), row, 1);
			tableModel.setValueAt(bean.getNumber(), row, 2);
			tableModel.setValueAt(bean.getName(), row, 3);
			tableModel.setValueAt(bean.getPhaseCode(), row, 4);
			tableModel.setValueAt(bean.getKeycomponent(), row, 5);
			tableModel.setValueAt(bean.getMtype(), row, 6);
			tableModel.setValueAt(bean.getGylx(), row, 7);
			tableModel.setValueAt(bean.getChbm(), row, 8);
			tableModel.setValueAt(bean.getZgyNumber(), row, 9);
			tableModel.setValueAt(bean.getZgyzt(), row, 10);
			tableModel.setValueAt(bean.getZldezt(), row, 11);

			if("否".equals(bean.getIsOk())) {
				isOk = false;
			}

			row++;
			n++;
		}

		int tw1 = 200;
		int[] tableColWidth = new int[]{tw1-100,tw1-100,tw1+20,tw1+20,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1};
		for (int j = 0; j < tableHeader.length; j++) {
			table.getColumn(tableHeader[j]).setPreferredWidth(tableColWidth[j]);
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

			if((column == 1) && "否".equals(value)) {
				setBackground(Color.RED);
			}

//	        if (row % 2 == 0)  {
//	            setBackground(Color.white); //设置奇数行底色
//	        } else if (row % 2 == 1) {
//	            setBackground(new Color(206, 231, 255)); //设置偶数行底色
//	            //(206, 231, 255)
//	        }
	        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	    }
	}

}
