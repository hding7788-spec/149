package com.glaway.mpm.erp;

import javax.swing.JTable;
import javax.swing.table.TableModel;


public interface ZTableOp {

	/**
	 * <BR>
	 * <UL>设置table中的信息。
	 * <LI>设置table中的信息。</LI>
	 * </UL>
	 *
	 * @param tableHeadValue 表头信息。
	 * @param tableContentValue 表体信息。
	 * @param preferredWidthValue 表头列宽。
	 * @return 无。
	 * @throws 无。
	 */
	public void setTableInfors(Object[] tableHeadValue,
			Object[][] tableContentValue, int[] preferredWidthValue);

	/**
	 * <BR>
	 * <UL>设置table中的信息。
	 * <LI>设置table中的信息。</LI>
	 * </UL>
	 *
	 * @param tableHeadValue 表头信息。
	 * @param tableContentValue 表体信息。
	 * @return 无。
	 * @throws 无。
	 */
	public void setTableInfors(Object[] tableHeadValue,
			Object[][] tableContentValue) ;

	/**
	 * <BR>
	 * <UL>设置table中的列宽。
	 * <LI>设置table中的列宽。</LI>
	 * </UL>
	 *
	 * @param preferredWidthValue table的列宽。
	 * @return 无。
	 * @throws 无。
	 */
	public void setPreferredWidth(int[] preferredWidthValue) ;

	/**
	 * <BR>
	 * <UL>增加一行。
	 * <LI>在table的最后面插入一行。</LI>
	 * </UL>
	 *
	 * @param tableAddRow table中需要插入的内容。
	 * @return 无。
	 * @throws 无。
	 */
	public void addOneRow(Object[] tableAddRow);

	/**
	 * <BR>
	 * <UL>增加一行。
	 * <LI>在table的最后面插入一行。</LI>
	 * </UL>
	 *
	 * @param table 需要操作的table。
	 * @param tableAddRow table中需要插入的内容。
	 * @return 无。
	 * @throws 无。
	 */
	public void addOneRow(JTable table,Object[] tableAddRow);

	/**
	 * <BR>
	 * <UL>插入一行。
	 * <LI>在用户选择的行的上面插入用户需要的行，并使插入行处于选中状态。</LI>
	 * </UL>
	 *
	 * @param currentRow 当前的行。
	 * @param tableInsertRow 需要插入的内容。
	 * @return 无。
	 * @throws 无。
	 */
	public void insertOneRow(int currentRow,Object[] tableInsertRow);

	/**
	 * <BR>
	 * <UL>插入一行。
	 * <LI>在用户选择的行的上面插入用户需要的行，并使插入行处于选中状态。</LI>
	 * </UL>
	 *
	 * @param table 操作table。
	 * @param currentRow 当前的行。
	 * @param tableInsertRow 需要插入的内容。
	 * @return 无。
	 * @throws 无。
	 */
	public void insertOneRow(JTable table,int currentRow,Object[] tableInsertRow);

	/**
	 * <BR>
	 * <UL>对指定table的指定的行进行维护。
	 * <LI>对指定table的指定的行进行维护。</LI>
	 * </UL>
	 *
	 * @param table 需要操作的table。
	 * @param opRow table中需要操作的一行。
	 * @param voValues 订单的信息。
	 * @return 无。
	 * @throws 无。
	 */
	public void updateTableOneRow(int opRow,Object[] voValues);
	/**
	 * <BR>
	 * <UL>对指定table的指定的行进行维护。
	 * <LI>对指定table的指定的行进行维护。</LI>
	 * </UL>
	 *
	 * @param table 需要操作的table。
	 * @param opRow table中需要操作的一行。
	 * @param voValues 订单的信息。
	 * @return 无。
	 * @throws 无。
	 */
	public void updateTableOneRow(JTable table,int opRow,Object[] voValues);

	/**
	 * <BR>
	 * <UL>删除一行。
	 * <LI>删除操作table中选中的一行。</LI>
	 * </UL>
	 *
	 * @param currentRow 当前的行。
	 * @return 无。
	 * @throws 无。
	 */
	public void removeOneRow(int currentRow);

	/**
	 * <BR>
	 * <UL>删除一行。
	 * <LI>删除操作table中选中的一行。</LI>
	 * </UL>
	 *
	 * @param table 操作table。
	 * @param currentRow 当前的行。
	 * @return 无。
	 * @throws 无。
	 */
	public void removeOneRow(JTable table,int currentRow);

	/**
	 * <BR>
	 * <UL>将table中选中的一行上移。
	 * <LI>将table中选中的一行上移。</LI>
	 * </UL>
	 *
	 * @param currentRow table中选中的行。
	 * @return 无。
	 * @throws 无。
	 */
	public void currentRowMoveUp(int currentRow);

	/**
	 * <BR>
	 * <UL>将table中选中的一行上移。
	 * <LI>将table中选中的一行上移。</LI>
	 * </UL>
	 *
	 * @param table 需要操作的table。
	 * @param currentRow table中选中的行。
	 * @return 无。
	 * @throws 无。
	 */
	public void currentRowMoveUp(JTable table,int currentRow);

	/**
	 * <BR>
	 * <UL>将table中选中的一行下移。
	 * <LI>将table中选中的一行下移。</LI>
	 * </UL>
	 *
	 * @param currentRow table中选中的行。
	 * @return 无。
	 * @throws 无。
	 */
	public void currentRowMoveDown(int currentRow);

	/**
	 * <BR>
	 * <UL>将table中选中的一行下移。
	 * <LI>将table中选中的一行下移。</LI>
	 * </UL>
	 *
	 * @param table 需要操作的table。
	 * @param currentRow table中选中的行。
	 * @return 无。
	 * @throws 无。
	 */
	public void currentRowMoveDown(JTable table,int currentRow);

	/**
	 * <BR>
	 * <UL>设置若干列为隐藏列。
	 * <LI>将table中指定的列设为隐藏。</LI>
	 * </UL>
	 *
	 * @param table 用户操作table。
	 * @param clumnNum 需要隐藏的列。
	 * @return 无。
	 * @throws 无。
	 */
	public void setColumnsHidden(JTable table, int[] colmnNum);

	/**
	 * <BR>
	 * <UL>设置一列为隐藏列。
	 * <LI>将table中指定的列设为隐藏。</LI>
	 * </UL>
	 *
	 * @param table 用户操作table。
	 * @param clumnNum 需要隐藏的列。
	 * @return 无。
	 * @throws 无。
	 */
	public void setOneColumnHidden(JTable table, int colmnNum);

	/**
	 * <BR>
	 * <UL>得到需要的table。
	 * <LI>得到经过加工后的table。</LI>
	 * </UL>
	 *
	 * @param 无。
	 * @return table 经过加工的table。
	 * @throws 。
	 */
	public JTable getZTable();

	/**
	 * <BR>
	 * <UL>设置生成的table的样式。
	 * <LI>设置生成的table的样式。</LI>
	 * </UL>
	 *
	 * @param table 需要设置样式的table。
	 * @return 无。
	 * @throws 无。
	 */
	public void setTableStyle(JTable table);

	/**
	 * <BR>
	 * <UL>得到table的model。
	 * <LI>得到table的model，为防止脏读，脏写进行加锁控制。</LI>
	 * </UL>
	 *
	 * @param table 需要操作的table。
	 * @param opRow table中需要操作的一行。
	 * @param voValues 订单的信息。
	 * @return 无。
	 * @throws 无。
	 */
	public TableModel getTableModel();

	/**
	 * <BR>
	 * <UL>保留方法。
	 * <LI>设置table中单元格是否可以被编辑。</LI>
	 * </UL>
	 *
	 * @param columns 列。
	 * @return 无。
	 * @throws 无。
	 */
	public void setColumnsEditable(int[] columns);

}

