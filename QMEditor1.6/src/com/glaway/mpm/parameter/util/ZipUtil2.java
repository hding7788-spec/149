package com.glaway.mpm.parameter.util;

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

import com.glaway.mpm.log.VaLogger;

/**
 * 文件打包解包工具类
 *
 * @author 龙秀川
 *
 */
public class ZipUtil2 {

	private static final String ENCODING_GBK = "GBK";

	private static VaLogger logger = VaLogger.getLogger(ZipUtil2.class.getName());

	private static void closeInputStream(InputStream is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (IOException e) {
			logger.error(e);
		}
	}

	private static void closeOutputStream(OutputStream os) {
		try {
			if (os != null) {
				os.close();
			}
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 打包指定文件夹下的所有文件
	 *
	 * @param file 需要打包的文件夹
	 * @param zipFilepath 压缩包路径
	 * @return boolean
	 */
	public static boolean compress(File file, String zipFilepath) {
		File targetZipFile = new File(zipFilepath);
		ZipOutputStream out = null;
		CheckedOutputStream cos = null;
		FileOutputStream fos = null;
		boolean b = false;
		try {
			fos = new FileOutputStream(targetZipFile);
			out = new ZipOutputStream(fos);
			out.setEncoding(ENCODING_GBK);
			for (File files : file.listFiles()) {
				zip(files, out, "", true);
			}
			b = true;
		} catch (IOException e) {
			logger.error(e);
		} finally {
			closeOutputStream(out);
			closeOutputStream(cos);
			closeOutputStream(fos);
		}
		return b;
	}

	/**
	 * 打包指定文加件下的文件
	 *
	 * @param fileDir 需要打包的文件夹路径
	 * @param zipFilepath 压缩包路径
	 * @return boolean
	 */
	public static boolean compress(String fileDir, String zipFilepath) {
		File srcFile = new File(fileDir);
		if (!srcFile.exists()) {
			logger.debug(fileDir+" 文件夹不存在!");
			return false;
		}
		return compress(srcFile, zipFilepath);

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

	/**
	 * 解压文件到指定路径
	 *
	 * @param zipFile 压缩包文件
	 * @param destDir 文件加压存放的路径
	 * @return boolean
	 */
	public static boolean decompress(File zipFile, String destDir) {
		if (!zipFile.exists()) {
			logger.debug(zipFile+" 文件不存在!");
			return false;
		}
		boolean b = false;
		try {
			unZip(zipFile, destDir);
			b = true;
		} catch (IOException e) {
			logger.error(e);
		} finally {
			if (!b) {
				deleteDirectory(new File(destDir));
			}
		}
		return b;
	}

	/**
	 * 解压指定路径的压缩包文件到指定路径
	 *
	 * @param zipFilepath 压缩包文件路径
	 * @param destDir 文件加压存放的路径
	 * @return boolean
	 */
	public static boolean decompress(String zipFilepath, String destDir) {
		File srcZipFile = new File(zipFilepath);
		if (!srcZipFile.exists()) {
			logger.debug(zipFilepath+" 文件不存在!");
			return false;
		}
		boolean b = false;
		try {
			unZip(srcZipFile, destDir);
			b = true;
		} catch (IOException e) {
			logger.error(e);
		} finally {
			if (!b) {
				deleteDirectory(new File(destDir));
			}
		}
		return b;
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

	private static void unZip(File file, String outputDir) throws IOException {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(file, ENCODING_GBK);
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
						logger.error(e);
					} finally {
						closeInputStream(in);
						closeOutputStream(out);
					}
				}
			}
		} catch (IOException e) {
			logger.error(e);
		} finally {
			try {
				if (zipFile != null) {
					zipFile.close();
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}
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
				logger.error(e);
			} finally {
				closeInputStream(fis);
			}
		}
	}
}