package com.glaway.mpm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class TechnicsReleaseUtil {

	public static void zipTechnics(String technicsNumber, String technicsName,
			String technicsType, String partNumber, String productNumber,
			String zipFilePath) throws Exception {
		String zipPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
		File file = new File(zipPath);
		String zipFileName = zipFilePath
				+ "\\"
				+ WorkSpaceUtil.getTechnicsConversion(technicsNumber,
						technicsName, technicsType, partNumber, productNumber)
				+ ".zip";
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				zipFileName));
		out.setEncoding("GBK");
		zip(out, file, "");
		out.close();
	}

	private static void zip(ZipOutputStream out, File f, String base)
			throws Exception {
		out.setEncoding("GBK");
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			out.putNextEntry(new ZipEntry(base + "/"));
			base = base + "/";
			for (int i = 0; i < fl.length; i++) {
				zip(out, fl[i], base + fl[i].getName());
			}
		} else {
			out.putNextEntry(new ZipEntry(base));
			FileInputStream in = new FileInputStream(f);
			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			in.close();
		}
	}

	public static void unZip(String unZipFileName, String destFileName) {
		File unzipFile = new File(unZipFileName);

		if ((destFileName == null) || (destFileName.trim().length() == 0)) {
			destFileName = unzipFile.getParent();
		}

		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(unzipFile, "GBK");
			for (Enumeration<?> entries = zipFile.getEntries(); entries.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				File destFile = new File(destFileName, entry.getName());
				unZipFile(destFile, zipFile, entry);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "解压过程中出现错误！请尝试关闭相关工艺文件的PDF和工艺附表，再重新启动工艺编辑器。\r\n"+e.getMessage(), "提示", 1);
			e.printStackTrace();
		} finally {
			try {
				if (zipFile != null)
					zipFile.close();
				if (unzipFile.exists()) {
					//unzipFile.delete();
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "解压过程中出现错误！请尝试关闭相关工艺文件的PDF和工艺附表，再重新启动工艺编辑器。\r\n"+e.getMessage(), "提示", 1);
				e.printStackTrace();
			} finally {
				if (unzipFile.exists()) {
					//unzipFile.delete();
				}
			}
		}
	}

	private static void unZipFile(File destFile, ZipFile zipFile, ZipEntry entry) {
		if (entry.isDirectory()) {
			destFile.mkdirs();
		} else {
			File parent = destFile.getParentFile();
			if ((parent != null) && (!parent.exists())) {
				parent.mkdirs();
			}

			InputStream inputStream = null;
			FileOutputStream fileOut = null;
			try {
				inputStream = zipFile.getInputStream(entry);
				fileOut = new FileOutputStream(destFile);
				byte[] buf = new byte[8096];
				int readedBytes;
				while ((readedBytes = inputStream.read(buf)) > 0) {
					fileOut.write(buf, 0, readedBytes);
				}
			} catch (ZipException e) {
				if(e.getMessage()!=null&&!e.getMessage().contains("PDFPreview")){
					JOptionPane.showMessageDialog(null, "解压过程中出现错误！找不到相关文件,请尝试关闭或清空工艺附表，再重新启动工艺编辑器。\r\n"+e.getMessage(), "提示", 1);
					e.printStackTrace();
				}
			} catch (IOException e) {
				if(e.getMessage()!=null&&!e.getMessage().contains("PDFPreview")){
					JOptionPane.showMessageDialog(null, "解压过程中出现错误！找不到相关文件,请尝试关闭或清空工艺附表，再重新启动工艺编辑器。\r\n"+e.getMessage(), "提示", 1);
					e.printStackTrace();
				}
			}finally{
				try {
					if(fileOut!=null){
						fileOut.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					if(inputStream!=null){
						inputStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void unZipTechnics(String technicsZipFilePath)
			throws Exception {
		File file = new File(technicsZipFilePath);
		String fileName = file.getName();
		String technicsNumber = WorkSpaceUtil.getTechnicsNumber(fileName);
		String unZipPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
		String zipFileName = technicsZipFilePath;

		unZip(zipFileName, unZipPath);
	}

	public static void jFileChooser() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(1);
	}

	public static void unZip(byte[] data, String outputDirectory) {
		if ((data == null) || (outputDirectory == null) || (outputDirectory.trim().length() == 0))
			return;
		String tempZipPath = WorkSpaceUtil.getTempRootPath() + "temp.zip";
		File file = new File(tempZipPath);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(tempZipPath);
			int size = data.length / 1024;
			for (int i = 0; i < size; i++) {
				fos.write(data, i * 1024, 1024);
			}
			fos.write(data, size * 1024, data.length % 1024);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		unZip(file.getAbsolutePath(), outputDirectory);
	}
}
