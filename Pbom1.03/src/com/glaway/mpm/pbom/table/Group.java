package com.glaway.mpm.pbom.table;

public interface Group {
    /**
     * 获取所在行
     * 
     * @return
     */
    public int getRow();

    /**
     * 获取所在列
     * 
     * @return
     */
    public int getColumn();

    /**
     * 获取占列个数
     * 
     * @return
     */
    public int getColumnSpan();

    /**
     * 获取占行个数
     * 
     * @return
     */
    public int getRowSpan();

    /**
     * 获取文字
     * 
     * @return
     */
    public Object getHeaderValue();
}