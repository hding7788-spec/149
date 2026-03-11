package com.glaway.mpm.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import wt.util.WTProperties;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.release.ProcessInfoReleaseController;

public class FileUtil {

	public File dirFrom;
	public File dirTo;
	private static VaLogger logger = VaLogger.getLogger(FileUtil.class);
	/**
	 * 从指定文件夹下面寻找对应文件
	 *
	 * @author qianlong
	 * @date 2012-11-28
	 * @param folderPath
	 * @param fileName
	 * @return
	 *
	 */
	public static File getFileByNameFromFolder(String folderPath, String fileName) {
		File file = null;
		File folder = new File(folderPath);
		if (fileName == null || folder == null || folder.isDirectory()) {
			return file;
		}
		for (File f : folder.listFiles()) {
			if (!f.isDirectory() && fileName.equals(f.getName())) {
				file = f;
			}
		}
		return file;
	}

	/**
	 *构建临时文件夹
	 *
	 * @author qianlong
	 * @date 2012-12-5
	 * @param path
	 * @return
	 *
	 */
	public static String makeTmpDir(String path) {
		String filePath = System.getProperty("java.io.tmpdir");
		if (filePath == null) {
			return null;
		}
		if (!filePath.endsWith(File.separator)) {
			filePath += File.separator;
		}
		filePath += path;
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return filePath;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-12-5
	 * @param filePath
	 * @param bytes
	 *
	 */
	public static void writeBytes(String filePath, String fileName, byte[] bytes) {
		FileOutputStream fos = null;
		try {
			isPathExist(filePath, true);
			fos = new FileOutputStream(filePath+fileName);
			int size = bytes.length / 1024;
			for (int i = 0; i < size; i++) {
				fos.write(bytes, i * 1024, 1024);

			}
			fos.write(bytes, size * 1024, bytes.length % 1024);
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
	}

	/**
	 *
	 * @author qianlong
	 * @date 2012-12-5
	 * @param filePath
	 * @param bytes
	 *
	 */
	public static void writeInputStream(String filePath, String fileName, InputStream inputStream) {
		FileOutputStream fos = null;
		try {
			isPathExist(filePath, true);
			byte[] bytes = new byte[1024];
			int length;
			fos = new FileOutputStream(filePath + File.separator + fileName);
			while ((length = inputStream.read(bytes)) != -1) {
				fos.write(bytes, 0, length);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != inputStream) {
					inputStream.close();
				}
				if (null != fos) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 *文件到字节数组的转换
	 *
	 * @author qianlong
	 * @date 2013-4-22
	 *
	 */
	public static byte[] fileToBytes(String filePath) {
		File file = new File(filePath);
		return fileToBytes(file);
	}

	/**
	 *文件到字节数组的转换
	 *
	 * @author qianlong
	 * @date 2013-4-22
	 *
	 */
	public static byte[] fileToBytes(File file) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return fileToBytes(inputStream);
	}

	/**
	 *文件到字节数组的转换
	 *
	 * @author qianlong
	 * @date 2013-4-22
	 *
	 */
	public static byte[] fileToBytes(InputStream inputStream) {
		ByteArrayOutputStream baos = null;
		try {
			byte[] bytes = new byte[1024];
			int length;
			baos = new ByteArrayOutputStream();
			while ((length = inputStream.read(bytes)) != -1) {
				baos.write(bytes, 0, length);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != inputStream) {
					inputStream.close();
				}
				if (null != baos) {
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return baos.toByteArray();
	}

	/**
	 * 判断路径是否存在 flag true 创建 false 不创建
	 *
	 * @author qianlong
	 * @date 2013-4-1
	 * @param filePath
	 *
	 */
	public static boolean isPathExist(String filePath, boolean flag) {
		boolean tag = true;
		File file = new File(filePath);
		if (!file.exists()) {
			if (flag) {
				file.mkdirs();
			} else {
				tag = false;
			}
		}

		return tag;
	}

	/**
	 * 目标路径创建文件夹
	 *
	 * @author lbzhang
	 * @date 2013-2-19上午10:45:00
	 * @param file
	 */
	public void listFileInDir(File file) {
		File[] files = file.listFiles();
		for (File f : files) {
			String tempfrom = f.getAbsolutePath();
			String tempto = tempfrom.replace(dirFrom.getAbsolutePath(), dirTo.getAbsolutePath());
			if (f.isDirectory()) {
				File tempFile = new File(tempto);
				tempFile.mkdirs();
				listFileInDir(f);
			} else {
				GLLogger.debug("源文件:" + f.getAbsolutePath());
				// int endindex = tempto.lastIndexOf("\\");
				// String mkdirPath = tempto.substring(0, endindex);
				// File tempFile = new File(mkdirPath);
				GLLogger.debug("目标点:" + tempto);
				copy(tempfrom, tempto);
			}
		}
	}

	/**
	 * 拷贝资料夹下的文件到指定目录中
	 *
	 * @author lbzhang
	 * @date 2013-2-19上午10:48:36
	 * @param from
	 * @param to
	 */
	public void copy(String from, String to) {
		try {
			InputStream in = new FileInputStream(from);
			OutputStream out = new FileOutputStream(to);

			byte[] buff = new byte[1024];
			int len = 0;
			while ((len = in.read(buff)) != -1) {
				out.write(buff, 0, len);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断资料夹下是否有指定名称的文件
	 *
	 * @author lbzhang
	 * @date 2013-2-19上午11:18:04
	 * @param fromfile
	 * @param name
	 * @return
	 */
	public boolean isexistJPG(String fromfile, String name) {
		File file = new File(fromfile);
		File[] files = file.listFiles();
		for (File f : files) {
			String fileName = f.getName();
			if (fileName.equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 删除资料夹及其下的文件
	 *
	 * @author fly
	 * @date 2013-5-15
	 * @param file
	 *
	 */
	public static void deleteFile(File file) {
		if (file.isFile()) {
			file.delete();
		} else if (file.isDirectory()) {
			File files[] = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteFile(files[i]);
			}
			file.delete();
		}
	}

	public static void main(String[] args) {
		System.out.println(isPathExist("C:/ptc/Windchill_10.0/Windchill/tomcat/instances/1313/2121/", false));
	}

	public static void deleteFile(String filePath) {
		File file = new File(filePath);
		if (file != null && file.exists()) {
			deleteFile2(file);
		}
	}

	public static boolean deleteFile2(File file) {
		try {
			File[] files = file.listFiles();
			if(files != null){
				for (File subFile : files) {
					if (subFile.isFile()) {
						subFile.delete();
					} else {
						deleteFile2(subFile);
						subFile.delete();
					}
				}
			}
			file.delete();
			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	public static boolean deleteSubFile(File file) {
		if (file == null) {
			return false;
		}
		if (!file.isDirectory()) {
			return false;
		}
		try {
			File[] files = file.listFiles();
			for (File subFile : files) {
				if (subFile.isFile()) {
					subFile.delete();
				} else {
					deleteFile2(subFile);
					subFile.delete();
				}
			}
			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	public static boolean deleteSubFile(String filePath) {
		return deleteSubFile(new File(filePath));
	}

	public static String getWncTmpPath() {
		String wncTempPath = "";
		try {
			WTProperties properties = WTProperties.getLocalProperties();
			wncTempPath = properties.getProperty("wt.temp");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wncTempPath;
	}
	public static String getExtension(String filePath) {
		String extension = "";
		extension = filePath.substring(filePath.lastIndexOf(".") + 1);
		return extension;
	}
	public static String makeWncTmpDir(String path) {
		String filePath = getWncTmpPath();
		if (filePath == null) {
			return null;
		}
		filePath = FileUtil.addSeperator(filePath);
		filePath += path;
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		} else {
			deleteSubFile(file);
		}
		return filePath;
	}
	public static String addSeperator(String str) {
		return str.endsWith(File.separator) ? str : str + File.separator;
	}
	public static void writeBytes(String filePath, byte[] bytes) {
		if (bytes == null) {
			return;
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath);
			int size = bytes.length / 1024;
			for (int i = 0; i < size; i++) {
				fos.write(bytes, i * 1024, 1024);
			}
			fos.write(bytes, size * 1024, bytes.length % 1024);
		} catch (FileNotFoundException e) {
			logger.error(e);;
		} catch (IOException e) {
			logger.error(e);;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					logger.error(e);;
				}
			}
		}
	}

	public static byte[] readFilePathToByte(File file) {
		FileInputStream fis = null;
		byte[] bytes = null;
		try {
			fis = new FileInputStream(file);
			bytes = new byte[fis.available()];
			fis.read(bytes);
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}

		return bytes;
	}

	public static byte[] readFilePathToByte(String filePath) {
		FileInputStream fis = null;
		byte[] bytes = null;
		try {
			fis = new FileInputStream(filePath);
			bytes = new byte[fis.available()];
			fis.read(bytes);
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);;
		} finally {
			if (fis != null) {
				try {
					fis.close();
					File zipFile = new File(filePath);
					boolean isDeleted = zipFile.delete();
					logger.debug(isDeleted);
				} catch (IOException e) {
					logger.error(e);;
				}
			}
		}
		return bytes;
	}
	public static String getWncTmpDir(String path) {
		String filePath = getWncTmpPath();
		if (filePath == null) {
			return null;
		}
		filePath = FileUtil.addSeperator(filePath);
		filePath += path;
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return filePath;
	}
	/**
	 * 生成目录
	 *
	 * @param path
	 * @return
	 */
	public static boolean mkDirs(String path) {
		boolean flag = true;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return flag;
	}

}
