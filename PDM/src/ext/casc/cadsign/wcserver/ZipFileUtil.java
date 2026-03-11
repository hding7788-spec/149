package ext.casc.cadsign.wcserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/**
 *
 * @author Administrator
 */
public class ZipFileUtil {

	public static boolean VERBOSE = false;

	//public static String ZIPBASE = "ext.xac2.download.home";

	public static String CLASSNAME = ZipFileUtil.class.getName();

/*	static {
		try {
			WTProperties properties = WTProperties.getLocalProperties();
			VERBOSE = properties.getProperty("ext.generic.util.verbose", false);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new ExceptionInInitializerError(t);
		}
	}*/
	public static void main(String arg[]) throws Exception {
		File file = new File("d:\\a");
		zip(file,"d:\\a.zip");
		//UnZipFile("D:\\FTPFile\\c.zip","d:\\a");
	}


	/**
	 *
	 * 3:17:33 PM
	 * @param inputFile 要压缩的文件路径
	 * @param zipFileName 压缩后的文件路径 如d:\\a.zip
	 * @throws Exception
	 */
	public static void zip(File inputFile,String zipFileName)
			throws Exception {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
		zip(out, inputFile, "");
		out.close();
		if(inputFile.exists()){
			//deleteFiles(inputFile.getPath());
		}

	}


	private static void zip(ZipOutputStream out,File f,String base)
			throws Exception {
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			base = base.length() == 0 ? "" : base + "/";
			for (int i = 0; i < fl.length; i++) {
				zip(out, fl[i], base + fl[i].getName());
			}
		} else {
			ZipEntry zipentry = new org.apache.tools.zip.ZipEntry(base);
			zipentry.setMethod(ZipEntry.STORED);// 不压缩
			zipentry.setSize(f.length());
			zipentry.setCrc(calcChecksum(f));
			out.putNextEntry(zipentry);
			FileInputStream in = new FileInputStream(f);
			int b;
			byte[] buf = new byte[2048];
			while ((b = in.read(buf)) > 0) {
				out.write(buf, 0, b);
			}
			in.close();
		}
	}
	public static String zipFile(String filePath,String zipName)
			throws Exception {
		zip(new File(filePath),zipName);
		// 删除文件
		deleteFiles(filePath);
		return zipName;
	}

	/**
	 * 将文件或文件夹打包成zip，路径可含中文 add by liangbo.
	 * @param filePath
	 * @param zipName
	 * @return
	 * @throws Exception
	 */
	public static String zipFile2(String filePath,String zipName)
			throws Exception {
		zip2(new File(filePath),zipName);
		// 删除文件
		deleteFiles(filePath);
		return zipName;
	}
	public static void zip2(File inputFile,String zipFileName)
			throws Exception {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
		out.setEncoding("utf-8");
		zip(out, inputFile, "");
		out.close();
		if(inputFile.exists()){
			//deleteFiles(inputFile.getPath());
		}

	}

	/**
	 *
	 * 3:40:28 PM
	 * @param zipFilePath 压缩文件路径
	 * @param outputDirectory 解压后存放的文件夹，如果不存在,先创建，如果存在则删除
	 * @return
	 * @throws Exception
	 */
	public static String UnZipFile(String zipFilePath,String outputDirectory) throws Exception {
		if(!outputDirectory.endsWith("\\") && !outputDirectory.endsWith("/")){
			outputDirectory = outputDirectory + File.separator;
		}
		System.out.println(outputDirectory);
		String zipFileName = zipFilePath;
		String endinfo = null;
		File fdir = new File(outputDirectory);
		if (fdir.exists()) {
			deleteFiles(outputDirectory);
		}
		fdir.mkdirs();
		org.apache.tools.zip.ZipFile zipFile = new org.apache.tools.zip.ZipFile(zipFileName);
		try {
			java.util.Enumeration e = zipFile.getEntries();
			org.apache.tools.zip.ZipEntry zipEntry = null;
			while (e.hasMoreElements()) {
				zipEntry = (org.apache.tools.zip.ZipEntry) e.nextElement();
				if (zipEntry.getName() == null
						|| zipEntry.getName().trim().length() == 0
						|| zipEntry.getName().equalsIgnoreCase("/")
						|| zipEntry.getName().equalsIgnoreCase("\\")) {
					continue;
				}
				if (zipEntry.isDirectory()) {
					// System.out.println("目录...");
					String name = zipEntry.getName();
					name = name.substring(0, name.length() - 1);
					File f = new File(outputDirectory + File.separator + name);
					f.mkdir();
				} else {
					// System.out.println("文件...");
					String fileName = zipEntry.getName();
					fileName = fileName.replace('\\', '/');
					if (fileName.endsWith("/") || fileName.endsWith("\\"))
						fileName = fileName.substring(0, fileName.length() - 1);
					if ((fileName.indexOf("/") > -1)) {
						String dirStr = outputDirectory+ fileName.substring(0, fileName.lastIndexOf("/"));
						File f1 = new File(dirStr);
						f1.mkdir();
						fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
					} else if (fileName.indexOf("\\") > -1) {
						String dirStr = outputDirectory+ fileName.substring(0, fileName.lastIndexOf("\\"));
						File f1 = new File(dirStr);
						f1.mkdir();
						fileName = fileName.substring(fileName
								.lastIndexOf("\\") + 1, fileName.length());
					}
					File f = new File(outputDirectory + zipEntry.getName());
					f.createNewFile();
					InputStream in = zipFile.getInputStream(zipEntry);
					FileOutputStream out = new FileOutputStream(f);

					byte[] by = new byte[2048];
					int c;
					while ((c = in.read(by)) != -1) {
						out.write(by, 0, c);
					}
					out.close();
					in.close();
				}
			}
			zipFile.close();
		} catch (Exception ex) {
			endinfo = "解压失败，请确认您所提交的数据包是正确有效的zip文件，确认数据包无误后若错误依然存在，请联系系统管理员";
			zipFile.close();
			//deleteFiles(zipFileName);
			return endinfo;
		} finally {
			//deleteFiles(zipFileName);
		}
		return endinfo;
	}

	/*
	 * Necessary in the case where you add a entry that is not compressed.
	 */
	private static long calcChecksum(File f) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));

		return calcChecksum(in, f.length());
	}

	/*
	 * Necessary in the case where you add a entry that is not compressed.
	 */
	private static byte[] buffer = new byte[8192];

	private static long calcChecksum(InputStream in, long size)
			throws IOException {
		CRC32 crc = new CRC32();
		int len = buffer.length;
		int count = -1;
		int haveRead = 0;

		while ((count = in.read(buffer, 0, len)) > 0) {
			haveRead += count;
			crc.update(buffer, 0, count);
		}
		in.close();
		return crc.getValue();
	}

	/**
	 * 删除某一个文件或者文件夹
	 * 3:41:50 PM
	 * @param inputPath
	 * @return
	 */
	public static boolean deleteFiles(String inputPath) {
		try {
			File f = new File(inputPath);
			if (f.isDirectory()) {
				File[] flist = f.listFiles();
				for (int i = 0; i < flist.length; i++) {
					File tmpfile = (File) flist[i];
					deleteFiles(tmpfile.getAbsolutePath());
				}
				f.delete();
			} else
				f.delete();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
