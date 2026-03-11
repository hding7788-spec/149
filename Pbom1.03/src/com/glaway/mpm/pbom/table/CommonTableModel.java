package com.glaway.mpm.pbom.table;

import javax.swing.table.DefaultTableModel;


public class CommonTableModel extends DefaultTableModel{

	private static final long serialVersionUID = -4400060662334565754L;

	private int[] cols;

	/**
	 * <BR>
	 * <UL>设置某一列是否可以编辑。
	 * <LI>设置某一列是否可以编辑。true表示可以，false不可以。</LI>
	 * </UL>
	 *
	 * @param row 某一行。
	 * @param col 某一列。
	 * @return 是否可以编辑。
	 * @throws 无。
	 */
	public boolean isCellEditable(int row, int col) {
		if(cols!=null)
		for (int i = 0; i < cols.length; i++) {
			if(cols[i] == col){
				return true;
			}
		}
		return false;
	}

	public void setColumnsEditable(int[] cols){
		this.cols = cols;
	}

	/**
	 * <BR>
	 * <UL>构造函数初始化。
	 * <LI>构造函数初始化。</LI>
	 * </UL>
	 *
	 * @param  data 表体对象。
	 * @param columnNames 表头对象。
	 * @return 无。
	 * @throws 无。
	 */
	public CommonTableModel(Object[][] data, Object[] columnNames) {
        setDataVector(data, columnNames);
    }
}

