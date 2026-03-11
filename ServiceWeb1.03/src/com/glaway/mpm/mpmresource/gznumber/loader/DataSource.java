package com.glaway.mpm.mpmresource.gznumber.loader;

import java.util.HashMap;

public interface DataSource {
	public String getValue(int row, int col);
	public int end();
	public HashMap getRecordData(int row);
}
