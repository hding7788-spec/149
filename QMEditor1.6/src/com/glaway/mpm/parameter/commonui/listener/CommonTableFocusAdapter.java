package com.glaway.mpm.parameter.commonui.listener;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTable;

/**
 * 表格焦点监听
 * 
 * @author wxl
 *
 */
public class CommonTableFocusAdapter extends FocusAdapter {
	private int point;
	private int[] selectRows;
	private JTable table;
	
	public CommonTableFocusAdapter(int[] selectRows, JTable table, int point) {
		this.selectRows = selectRows;
		this.table = table;
		this.point = point;
	}

	@Override
	public void focusGained(FocusEvent focusevent) {
		//清除当前表格选择
		table.clearSelection();
		int length = selectRows.length;
		if (length > 1) {//选中多行时，不改变选择
			for (int row : selectRows) {
				table.getSelectionModel().addSelectionInterval(row, row);
			}
		} else if (length == 1){//只选中一行时，右击可以重新选择行
			if (point >= 0) {
				table.getSelectionModel().setSelectionInterval(point, point);
			} else {
				table.getSelectionModel().setSelectionInterval(selectRows[0], selectRows[0]);
			}
		}
	}

}
