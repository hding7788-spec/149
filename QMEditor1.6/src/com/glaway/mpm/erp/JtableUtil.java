/*
 * 南京国睿信维软件有限公司
 */
package com.glaway.mpm.erp;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.Enumeration;

/**
 * 类功能：
 *
 * @author MChen
 * @date 2021/3/12
 */
public class JtableUtil {


    /**
     * <BR>
     * <UL>
     * 设置若干列为隐藏列。
     * <LI>将table中指定的若干列设为隐藏。</LI>
     * </UL>
     *
     * @param table
     *            用户操作table。
     *            需要隐藏的列。
     * @return 无。
     *             。
     */
    public static void setColumnsHidden(JTable table, int[] columnNum) {
        TableColumnModel dfcm = table.getColumnModel();
        for (int i = 0; i < columnNum.length; i++) {
            dfcm.getColumn(columnNum[i]).setMinWidth(0);
            dfcm.getColumn(columnNum[i]).setMaxWidth(0);
        }
    }

    /**
     * 设置列隐藏
     * @param table
     * @param hiddenColumnValue
     */
    public static void setColumnHidden(JTable table,  int[] hiddenColumnValue){
            TableColumnModel dfcm = table.getColumnModel();
            for (int i = 0; i < hiddenColumnValue.length; i++) {
                dfcm.getColumn(hiddenColumnValue[i]).setMinWidth(0);
                dfcm.getColumn(hiddenColumnValue[i]).setMaxWidth(0);
        }
    }

    /**
     * 设置列宽
     * @param table
     * @param preferredWidthValue
     */
    public static void setColumnWidth(JTable table,  int[] preferredWidthValue){
        for (int i = 0; i < preferredWidthValue.length; i++) {
            table.getColumnModel().getColumn(i).setMinWidth(preferredWidthValue[i]);
        }
    }

    /**
     * 设置列宽自适应
     * @param myTable
     */
    public static void fitTableColumns(JTable myTable){
        JTableHeader header = myTable.getTableHeader();
        int rowCount = myTable.getRowCount();
        Enumeration columns = myTable.getColumnModel().getColumns();
        while(columns.hasMoreElements()){
            TableColumn column = (TableColumn)columns.nextElement();
            int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
            int width = (int)myTable.getTableHeader().getDefaultRenderer()
                    .getTableCellRendererComponent(myTable, column.getIdentifier()
                            , false, false, -1, col).getPreferredSize().getWidth();
            for(int row = 0; row<rowCount; row++){
                int preferedWidth = (int)myTable.getCellRenderer(row, col).getTableCellRendererComponent(myTable,
                        myTable.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
                width = Math.max(width, preferedWidth);
            }
            header.setResizingColumn(column); // 此行很重要
            column.setWidth(width+myTable.getIntercellSpacing().width);
        }

}
}
