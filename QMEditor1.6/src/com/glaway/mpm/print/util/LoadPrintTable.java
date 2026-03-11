package com.glaway.mpm.print.util;

import org.apache.log4j.Logger;
import ext.casc.util.PropertiesReader;

public class LoadPrintTable {

	private static final String FILE = "com/glaway/mpm/print/config/tableAttribute";
	private static LoadPrintTable instance = null;
	private static final Logger log = Logger.getLogger(LoadPrintTable.class);
	public synchronized static LoadPrintTable getInstance() {
		instance = new LoadPrintTable();
		instance.loadConfig();
		return instance;
	}

	private PropertiesReader properties = null;

	public LoadPrintTable() {
	}

	public Class<?>[] getTableColumnClass(String tableType) {
		String value = this.properties.getValue(tableType + "_class");
		String[] values = value.split("\\|");
		Class<?>[] clazz = new Class<?>[values.length];
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals("String")) {
				clazz[i] = String.class;
			} else if (values[i].equals("Boolean")) {
				clazz[i] = Boolean.class;
			} else if (values[i].equals("Object")) {
				clazz[i] = Object.class;
			}
		}
		return clazz;
	}

	public String[] getTableColumnName(String tableType) {
		String value = this.properties.getValue(tableType + "_name");
		return value.split("\\|");
	}

	public int[] getTableEditableColumns(String tableType) {
		String value = this.properties.getValue(tableType + "_editableColumns");
		String[] values = value.split("\\|");
		int[] intArray = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals("")) {
				continue;
			}
			intArray[i] = Integer.parseInt(values[i]);
		}
		return intArray;
	}

	public int[] getTableButtonColumns(String tableType) {
		String value = this.properties.getValue(tableType + "_buttonColumns");
		String[] values = value.split("\\|");
		int[] intArray = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals("")) {
				continue;
			}
			intArray[i] = Integer.parseInt(values[i]);
		}
		return intArray;
	}

	public int[] getTableColumnsWidth(String tableType) {
		String value = this.properties.getValue(tableType + "_columnsWidth");
		String[] values = value.split("\\|");
		int[] intArray = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals("")) {
				continue;
			}
			intArray[i] = Integer.parseInt(values[i]);
		}
		return intArray;
	}

	public int getTableHeight(String tableType) {
		String value = this.properties.getValue(tableType + "_height");
		return Integer.parseInt(value);
	}

	private void loadConfig() {
		properties = new PropertiesReader(FILE, "UTF-8", "ISO-8859-1");
		log.info("导入配置文件成功");
	}

}
