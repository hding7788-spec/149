package ext.casc.integrate.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.JFileChooser;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

import wt.util.WTProperties;

public class ZipUtil {

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
					unzipFile.delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (unzipFile.exists()) {
//					System.out.println("临时压缩包生成路径=========" + unzipFile.getAbsolutePath());
					unzipFile.delete();
				}
			}
		}
	}

	/**
	 * 根据原始rar路径，解压到指定文件夹下.
	 *
	 * @param srcRarPath
	 *            原始rar路径
	 * @param dstDirectoryPath
	 *            解压到的文件夹
	 */
//	public static void unRar(String srcRarPath, String dstDirectoryPath) {
//		if (!srcRarPath.toLowerCase().endsWith(".rar")) {
//			System.out.println("非rar文件！");
//			return;
//		}
//		File dstDiretory = new File(dstDirectoryPath);
//		if (!dstDiretory.exists()) {// 目标目录不存在时，创建该文件夹
//			dstDiretory.mkdirs();
//		}
//		Archive a = null;
//		try {
//			a = new Archive(new File(srcRarPath));
//			if (a != null) {
//				a.getMainHeader().print(); // 打印文件信息.
//				FileHeader fh = a.nextFileHeader();
//				while (fh != null) {
//					if (fh.isDirectory()) { // 文件夹
//						File fol = new File(dstDirectoryPath + File.separator + fh.getFileNameString());
//						fol.mkdirs();
//					} else { // 文件
//						File out = new File(dstDirectoryPath + File.separator + fh.getFileNameString().trim());
//						// System.out.println(out.getAbsolutePath());
//						try {// 之所以这么写try，是因为万一这里面有了异常，不影响继续解压.
//							if (!out.exists()) {
//								if (!out.getParentFile().exists()) {// 相对路径可能多级，可能需要创建父目录.
//									out.getParentFile().mkdirs();
//								}
//								out.createNewFile();
//							}
//							FileOutputStream os = new FileOutputStream(out);
//							a.extractFile(fh, os);
//							os.close();
//						} catch (Exception ex) {
//							ex.printStackTrace();
//						}
//					}
//					fh = a.nextFileHeader();
//				}
//				a.close();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * 解压rar格式压缩包。
	 * 对应的是java-unrar-0.3.jar，但是java-unrar-0.3.jar又会用到commons-logging-1.1.1.jar
	 */
	public static void unrar(String sourceRar, String destDir) throws Exception {
		Archive a = null;
		FileOutputStream fos = null;
		try {
			a = new Archive(new File(sourceRar));
			FileHeader fh = a.nextFileHeader();
			while (fh != null) {
				if (!fh.isDirectory()) {
					// 1 根据不同的操作系统拿到相应的 destDirName 和 destFileName
					String compressFileName = fh.getFileNameString().trim();
					String destFileName = "";
					String destDirName = "";
					// 非windows系统
					if (File.separator.equals("/")) {
						destFileName = destDir + compressFileName.replaceAll("\\\\", "/");
						destDirName = destFileName.substring(0, destFileName.lastIndexOf("/"));
						// windows系统
					} else {
						destFileName = destDir + compressFileName.replaceAll("/", "\\\\");
						destDirName = destFileName.substring(0, destFileName.lastIndexOf("\\"));
					}
					// 2创建文件夹
					File dir = new File(destDirName);
					if (!dir.exists() || !dir.isDirectory()) {
						dir.mkdirs();
					}
					// 3解压缩文件
					fos = new FileOutputStream(new File(destFileName));
					a.extractFile(fh, fos);
					fos.close();
					fos = null;
				}
				fh = a.nextFileHeader();
			}
			a.close();
			a = null;
		} catch (Exception e) {
			throw e;
		} finally {
			if (fos != null) {
				try {
					fos.close();
					fos = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (a != null) {
				try {
					a.close();
					a = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	private static void unZipFile(File destFile, ZipFile zipFile, ZipEntry entry)
			throws Exception {
		if (entry.isDirectory()) {
			destFile.mkdirs();
		} else {
			File parent = destFile.getParentFile();
			if ((parent != null) && (!parent.exists())) {
				parent.mkdirs();
			}

			InputStream inputStream = zipFile.getInputStream(entry);

			FileOutputStream fileOut = new FileOutputStream(destFile);
			byte[] buf = new byte[4096];
			int readedBytes;
			while ((readedBytes = inputStream.read(buf)) > 0) {
				fileOut.write(buf, 0, readedBytes);
				fileOut.flush();
			}
			fileOut.close();

			inputStream.close();
		}
	}

	public static void jFileChooser() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(1);
	}

	public static void unZip(byte[] data, String outputDirectory) {
		if ((data == null) || (outputDirectory == null)
				|| (outputDirectory.trim().length() == 0))
			return;
		FileOutputStream out = null;
		File file = null;
		try {
			WTProperties pro = WTProperties.getLocalProperties();
			String temp_dir = pro.getProperty("wt.temp");
			String tempPath = java.util.UUID.randomUUID().toString();
			String tempZipPath = temp_dir + File.separator +tempPath+".zip";
			file = new File(tempZipPath);
			if(!file.exists()) {
				ZipOutputStream temp_out = new ZipOutputStream(new FileOutputStream(file));
				temp_out.setEncoding("GBK");
				zip(temp_out, File.createTempFile("tmp", null), "");
				temp_out.close();
			}

			out = new FileOutputStream(file);
			out.write(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		unZip(file.getAbsolutePath(), outputDirectory);
	}


}
