package com.glaway.mpm.processplan.checkouttable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public class TechnicsZipUtil {
	public static void unZip(byte[] data, String outputDirectory,String directoryPatn) {
		if ((data == null) || (outputDirectory == null) || (outputDirectory.trim().length() == 0))
			return;
		String tempZipPath = directoryPatn + "temp.zip";
//		System.out.println("tempZipPath========" + tempZipPath);
//		System.out.println("tempZipPath========" + data.length);
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
	public static void unZip(String unZipFileName, String destFileName) {
		System.out.println("unZipFileName=========" + unZipFileName);
		File unzipFile = new File(unZipFileName);

		if ((destFileName == null) || (destFileName.trim().length() == 0)) {
			destFileName = unzipFile.getParent();
		}

		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(unzipFile, "GBK");
			for (Enumeration entries = zipFile.getEntries(); entries.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				File destFile = new File(destFileName, entry.getName());
				unZipFile(destFile, zipFile, entry);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (zipFile != null)
					zipFile.close();
				if (unzipFile.exists()) {
//					System.out.println("临时压缩包生成路径=========" + unzipFile.getAbsolutePath());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (unzipFile.exists()) {
//					System.out.println("临时压缩包生成路径=========" + unzipFile.getAbsolutePath());
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
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					if(fileOut!=null){
						fileOut.close();
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					if(inputStream!=null){
						inputStream.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
