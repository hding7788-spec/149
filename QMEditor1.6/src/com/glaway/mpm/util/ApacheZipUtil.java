package com.glaway.mpm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.CheckedOutputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class ApacheZipUtil {

	public static void main(String[] args) {
		String srcFilepath = "C:/Users/ylshao/Desktop/三维工艺/xml/";
		String zipFilepath = "C:/Users/ylshao/Desktop/三维工艺/模板2 - 副本 (4).zip";
		String destDir = "C:/Users/ylshao/Desktop/三维工艺/decompress";
		ApacheZipUtil.compress(srcFilepath, zipFilepath);
		ApacheZipUtil.decompress(zipFilepath, destDir);
	}

	public static boolean compress(File file, String zipFilepath) {
		File targetZipFile = new File(zipFilepath);
		ZipOutputStream out = null;
		CheckedOutputStream cos = null;
		FileOutputStream fos = null;
		boolean boo = false;
		try {
			fos = new FileOutputStream(targetZipFile);
			out = new ZipOutputStream(fos);
			out.setEncoding("GBK");
			for (File files : file.listFiles()) {
				zip(files, out, "", true);
			}
			boo = true;
			// System.out.println("压缩成功");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeOutputStream(out);
			closeOutputStream(cos);
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
//			if (!boo && targetZipFile.exists())
//				targetZipFile.delete();
		}
		return boo;
	}

	public static boolean compress(String srcFilepath, String zipFilepath) {
		File srcFile = new File(srcFilepath);
		if (!srcFile.exists()) {
			// System.out.println("文件不存在");
			return false;
		}
		return compress(srcFile, zipFilepath);

	}

	public static boolean decompress(String zipFilepath, String destDir) {
		File srcZipFile = new File(zipFilepath);
		if (!srcZipFile.exists()) {
			// System.out.println("文件不存在");
			return false;
		}
		boolean boo = false;
		try {
			unZip(srcZipFile, destDir);
			boo = true;
			// System.out.println("解压成功");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (!boo)
				deleteDirectory(new File(destDir));
		}
		return boo;
	}

	private static void zip(File file, ZipOutputStream out, String dir,
			boolean boo) throws IOException {
		if (file.isDirectory()) {
			File[] listFile = file.listFiles();
			if (listFile.length == 0 && boo) {
				out.putNextEntry(new ZipEntry(dir + file.getName() + "/"));
				return;
			} else {
				for (File cfile : listFile) {
					zip(cfile, out, dir + file.getName() + "/", boo);
				}
			}
		} else if (file.isFile()) {
			byte[] bt = new byte[2048 * 2];
			ZipEntry entry = new ZipEntry(dir + file.getName());
			entry.setSize(file.length());
			out.putNextEntry(entry);
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				int i = 0;
				while ((i = fis.read(bt)) != -1) {
					out.write(bt, 0, i);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(fis != null) {
					fis.close();
				}
			}
		}
	}

	private static void unZip(File file, String outputDir) throws IOException {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(file, "GBK");
			createDirectory(outputDir, null);
			Enumeration<?> enums = zipFile.getEntries();
			while (enums.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) enums.nextElement();
				if (entry.isDirectory()) {
					createDirectory(outputDir, entry.getName());
				} else {
					File tmpFile = new File(outputDir + "/" + entry.getName());
					createDirectory(tmpFile.getParent() + "/", null);
					InputStream in = null;
					OutputStream out = null;
					try {
						in = zipFile.getInputStream(entry);
						out = new FileOutputStream(tmpFile);
						int length = 0;
						byte[] b = new byte[2048];
						while ((length = in.read(b)) != -1) {
							out.write(b, 0, length);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						closeInputStream(in);
						closeOutputStream(out);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (zipFile != null) {
					zipFile.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void createDirectory(String outputDir, String subDir) {
		File file = new File(outputDir);
		if (!(subDir == null || subDir.trim().equals(""))) {
			file = new File(outputDir + "/" + subDir);
		}
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	private static void deleteDirectory(File file) {
		if (file.isFile()) {
			file.delete();
		} else {
			File list[] = file.listFiles();
			if (list != null) {
				for (File f : list) {
					deleteDirectory(f);
				}
				file.delete();
			}
		}
	}

	private static void closeInputStream(InputStream is) {
		try {
			if (is != null)
				is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void closeOutputStream(OutputStream os) {
		try {
			if (os != null)
				os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}