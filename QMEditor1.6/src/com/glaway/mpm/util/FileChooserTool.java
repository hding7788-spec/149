package com.glaway.mpm.util;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileChooserTool {

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
		FileNameExtensionFilter filter = new FileNameExtensionFilter(filterString, filterString.split(","));
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

		//wanghaoyu修改，增加 prt.1 文件类型识别
//		FileNameExtensionFilter filter = new FileNameExtensionFilter(
//				filterString, filterString.split(","));
		FileChooserTool.MyFileFilter filter = new FileChooserTool.MyFileFilter(filterString.split(","), "");
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
		chooser.setAcceptAllFileFilterUsed(false);
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

	static class  MyFileFilter extends javax.swing.filechooser.FileFilter {
	    private List<String> all_extension=null;        //存储加入的后缀名
	    private String remark="对文件类型的描述";

	    public MyFileFilter(){
	        all_extension=new ArrayList<String>();
	    }
	    //这个构造方法接收一个后缀名和一个描述
	    public MyFileFilter(String ext,String des){
	        this();        //调用本类中的构造方法
	        all_extension.add(ext);
	        remark=des;
	    }
	    public MyFileFilter(String[] ext,String des){
	        this();
	        all_extension.addAll(Arrays.asList(ext)); //把ext转换为Collection类型
	        remark=des;
	    }

	    //添加一个后缀名和一个描述
	    public void addExtension(String ext){
	        all_extension.add(ext);
	    }
	    public void setDescription(String des){
	        remark=des;
	    }

	    //添加一个数组后缀名
	    public void addExtension(String[] ext){
	        all_extension.addAll(Arrays.asList(ext));
	    }

	    //添加一个数组后缀名和一个描述
	    public void addExtension(String[] ext,String des){
	        all_extension.addAll(Arrays.asList(ext));
	        remark=des;
	    }

	    @Override
	    public boolean accept(File f) {
	        String file_name=f.getName().toLowerCase();
	        if(f.isDirectory()){
	            return true;
	        }
	        //以下方法显得有点繁琐，不用

	        for(String ext:all_extension){
	            if(file_name.endsWith(ext)){
	                return true;
	            }
	            else if(file_name.indexOf(".prt.")> -1){
	            	return true;
	            }
	        }
	        return false;
	    }

	    @Override
	    public String getDescription() {
	        return remark;
	    }
	}
}
