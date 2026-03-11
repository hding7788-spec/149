package com.glaway.mpm.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class ReportTechnicsTableModel extends DefaultTableModel {

	private List<Integer> editableColums = new ArrayList<Integer>();

	public ReportTechnicsTableModel(Object[][] dataVector, Object[] columnIdentifiers) {
		super.setDataVector(dataVector, columnIdentifiers);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
			if(editableColums.contains(column)) {
				return true;
			} else {
				return false;
			}
	}

	public void setEditableColums(List<Integer> editableColums) {
		this.editableColums = editableColums;
	}


}
