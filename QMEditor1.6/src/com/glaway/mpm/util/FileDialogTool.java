package com.glaway.mpm.util;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileDialogTool {
	public static File getFile(Component parent) {
		File file = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogType(0);
		int returnVal = chooser.showOpenDialog(parent);
		if (returnVal == 0) {
			file = chooser.getSelectedFile();
		}
		return file;
	}

	public static String getFilePath(Component parent) {
		File file = getFile(parent);
		if (file != null) {
			return file.getAbsolutePath();
		}
		return null;
	}

	public static File getFile(String filterString, Component parent) {
		File file = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogType(0);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				filterString, filterString.split(","));
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(parent);
		if (returnVal == 0) {
			file = chooser.getSelectedFile();
		}
		return file;
	}

	public static File getFile(String filterString, String defaultPath,
			Component parent) {
		File file = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogType(0);
		chooser.setSelectedFile(new File(defaultPath));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				filterString, filterString.split(","));
		chooser.setFileFilter(filter);
		chooser.setAcceptAllFileFilterUsed(false);
		int returnVal = chooser.showOpenDialog(parent);
		if (returnVal == 0) {
			file = chooser.getSelectedFile();
		}
		return file;
	}

	public static String getFilePath(String filterString, Component parent) {
		File file = getFile(filterString, parent);
		if (file != null) {
			return file.getAbsolutePath();
		}
		return null;
	}

	public static File getDirectory(Component parent) {
		File file = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogType(0);
		chooser.setFileSelectionMode(1);
		int returnVal = chooser.showOpenDialog(parent);
		if (returnVal == 0) {
			file = chooser.getSelectedFile();
		}
		return file;
	}

	public static String getDirectoryPath(Component parent) {
		File file = getDirectory(parent);
		if (file != null) {
			return file.getAbsolutePath();
		}
		return null;
	}

	public static File getDirectory(String defaultPath, Component parent) {
		File file = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogType(0);
		chooser.setFileSelectionMode(1);
		chooser.setSelectedFile(new File(defaultPath));
		int returnVal = chooser.showOpenDialog(parent);
		if (returnVal == 0) {
			file = chooser.getSelectedFile();
		}
		return file;
	}

	public static String getDirectoryPath(String defaultPath, Component parent) {
		File file = getDirectory(defaultPath, parent);
		if (file != null) {
			return file.getAbsolutePath();
		}
		return null;
	}

	public static File getSaveFile(String filterString, Component parent) {
		File file = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogType(1);
		chooser.setApproveButtonText("保存");
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				filterString, filterString.split(","));
		chooser.setFileFilter(filter);
		int returnVal = chooser.showSaveDialog(parent);
		if (returnVal == 0) {
			file = chooser.getSelectedFile();
		}
		return file;
	}

	public static File getSaveFile(JFileChooser chooser, String filterString,
			String defaultFileName, Component parent) {
		File file = null;
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogType(1);
		chooser.setApproveButtonText("保存");
		chooser.setSelectedFile(new File(""));
		chooser.setSelectedFile(new File(defaultFileName));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				filterString, filterString.split(","));
		chooser.setFileFilter(filter);
		int returnVal = chooser.showSaveDialog(parent);
		if (returnVal == 0) {
			file = chooser.getSelectedFile();
		}
		return file;
	}

	public static File getSaveFile(JFileChooser chooser, String filterString,
			String defaultFileName, String defaultPath, Component parent) {
		File file = null;
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogType(1);
		chooser.setApproveButtonText("保存");
		chooser.setSelectedFile(new File(defaultFileName));
		chooser.setSelectedFile(new File(defaultPath));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				filterString, filterString.split(","));
		chooser.setFileFilter(filter);
		int returnVal = chooser.showSaveDialog(parent);
		if (returnVal == 0) {
			file = chooser.getSelectedFile();
		}
		return file;
	}
}
