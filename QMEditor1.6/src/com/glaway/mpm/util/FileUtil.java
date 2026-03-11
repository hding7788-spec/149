package com.glaway.mpm.util;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.glaway.mpm.release.ProcessInfoReleaseController;
import com.glaway.mpm.visual.log.VaLogger;

public class FileUtil {

	private static VaLogger logger = VaLogger.getLogger(FileUtil.class);

	public static boolean deleteFile(File file) {
		try {
			File[] files = file.listFiles();
			for (File subFile : files) {
				if (subFile.isFile()) {
					subFile.delete();
				} else {
					deleteFile(subFile);
					subFile.delete();
				}
			}
			file.delete();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 复制文件
	 *
	 * @param fromPath
	 * @param toPath
	 * @throws Exception
	 */
	public static void copyFiles(String fromPath, String toPath)
			throws Exception {
		File fromFile = new File(fromPath);
		File toFile = new File(toPath);
		if (fromFile.exists()) {
			if (fromFile.isFile()) {
				File newToFile = new File(toPath);
				newToFile.createNewFile();
				FileInputStream inFile = new FileInputStream(fromFile);
				FileOutputStream outFile = new FileOutputStream(newToFile);
				FileChannel inChannel = inFile.getChannel();
				FileChannel outChannel = outFile.getChannel();
				long bytesWritten = 0;
				long byteCount = inChannel.size();
				while (bytesWritten < byteCount) {
					bytesWritten += inChannel.transferTo(bytesWritten,
							byteCount - bytesWritten, outChannel);
				}
				inFile.close();
				outFile.close();
			} else {
				if (toFile.exists()) {
					File[] info = fromFile.listFiles();
					for (int i = 0; i < info.length; i++) {
						String toPathTemp = toPath + File.separator
								+ info[i].getName();
						copyFiles(info[i].getAbsolutePath(), toPathTemp);//
					}
				} else {
					if (toFile.mkdir()) {
						File[] info = fromFile.listFiles();
						for (int i = 0; i < info.length; i++) {
							String toPathTemp = toPath + File.separator
									+ info[i].getName();
							copyFiles(info[i].getAbsolutePath(), toPathTemp);//
						}
					} else {
					}
				}

			}

		}
	}

	/**
	 * 创建目录
	 *
	 * @param path
	 * @return
	 */
	private static File createDir(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	/**
	 * 从jar中copy文件
	 *
	 * @param sourceFolder
	 */
	public static void copyFromJar(File sourceFolder) {
		try {
			String sourceFolderPath = sourceFolder.getAbsolutePath()
					+ File.separator;
			//String path = ProcessInfoReleaseController.class.getResource("/com/glaway/mpm/release/templetes").getPath();
			String path = ProcessInfoReleaseController.class.getResource(ProcessInfoReleaseController.class.getSimpleName() + ".class").getFile();
			//System.out.println(">>>>>>>>>>>>>path:" + path);
			path = "jar:" + path.substring(0, path.indexOf("!") + 2);
			URL url = new URL(path);
			JarURLConnection con = (JarURLConnection) url.openConnection();
			JarFile jarFile = con.getJarFile();

			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				if (name.startsWith("com/glaway/mpm/release/templetes")) {
					name = name
							.replace("com/glaway/mpm/release/templetes/", "");
					if (!name.equals("")) {
						if (name.indexOf("/") == -1) {
							writeInputStreamToFile(
									jarFile.getInputStream(entry),
									sourceFolderPath + name);
						} else {
							int i = name.lastIndexOf("/");
							String tempPath = name.substring(0, i);
							String fileName = name.substring(i + 1);
							if (!fileName.equals("")) {
								File f = createDir(sourceFolderPath + tempPath);
								writeInputStreamToFile(
										jarFile.getInputStream(entry),
										f.getAbsolutePath() + File.separator
												+ fileName);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写文件
	 *
	 * @param is
	 * @param destPath
	 */
	public static void writeInputStreamToFile(InputStream is, String destPath) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(new FileOutputStream(destPath));
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = bis.read(b)) != -1) {
				bos.write(b, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 深层创建目录
	 *
	 * @param path
	 * @return
	 */
	public static File createDirs(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
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
					deleteFile(subFile);
					subFile.delete();
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean deleteSubFile(String filePath) {
		return deleteSubFile(new File(filePath));
	}

	public static byte[] readFilePathToByte(File file) {
		FileInputStream fis = null;
		byte[] bytes = null;
		try {
			fis = new FileInputStream(file);
			bytes = new byte[fis.available()];
			fis.read(bytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
					File zipFile = new File(filePath);
					boolean isDeleted = zipFile.delete();
					logger.debug(isDeleted);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bytes;
	}

	public static String getTmpPath() {
		return System.getProperty("java.io.tmpdir");
	}

	public static String makeTmpDir(String path) {
		String filePath = getTmpPath();
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

	public static String getTmpPath(String path) {
		String filePath = getTmpPath();
		filePath = FileUtil.addSeperator(filePath);
		filePath += path;
		return filePath;
	}

	public static String generatePath(String filePath, String stepNumber) {
		if (!new File(filePath).isDirectory()) {
			return null;
		}
		filePath = FileUtil.addSeperator(filePath);
		filePath += stepNumber + File.separator;
		return filePath;
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

	public static String addSeperator(String str) {
		return str.endsWith(File.separator) ? str : str + File.separator;
	}

	public static String getResourcePath() {
		try {
			return FileUtil.addSeperator(WorkSpaceUtil.getWorkSpace())
					+ "resource" + File.separator;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getResourceImagePath() {
		try {
			return getResourcePath() + "image" + File.separator;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getResourceImagePath(String type) {
		try {
			return getResourceImagePath() + type + File.separator;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String generateHobbyPath() {
		String resourcePath = getResourcePath();
		String hobby = resourcePath + "hobby" + File.separator;
		File file = new File(hobby);
		if (!file.exists()) {
			file.mkdirs();
		}
		String epHobby = hobby + "epHobby.properties";
		return epHobby;
	}

	// public static void clearResourceImageFolder() {
	// if (!DateUtil.isFirstDate()) {
	// return;
	// }
	// try {
	// File file = new File(getResourceImagePath());
	// if (file.exists()) {
	// FileUtil.deleteSubFile(file);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	public static BufferedWriter getBufferWriter(String path, String encoding) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(path), encoding));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return bw;
	}

	public static BufferedReader getBufferedReaderByRelativePath(String path) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(
					FileUtil.class.getResourceAsStream(path), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return br;
	}

	public static BufferedReader getBufferedReaderByDirectPath(String path,
			String encoding) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					path), encoding));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return br;
	}

	public static BufferedReader getBufferedReaderByDirectPath(File file,
			String encoding) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), encoding));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return br;
	}

	public static void createFile(String path) {
		FileOutputStream fis = null;
		try {
			fis = new FileOutputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			JavaUtil.closeStream(fis);
		}
	}

	public static void copyFile(String sourcePath, String destPath) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(sourcePath);
			copyFile(fis, destPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void copyDirectory(File file, String destPath) {
		if (file != null && file.isDirectory()) {
			for (File subFile : file.listFiles()) {
				if (subFile.isDirectory()) {
					new File(destPath + File.separator + subFile.getName())
							.mkdirs();
					copyDirectory(subFile,
							destPath + File.separator + subFile.getName());
				} else {
					try {
						CommonUtil.writeInputStreamToFile(new FileInputStream(
								subFile),
								destPath + File.separator + subFile.getName());
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void copyFile(InputStream fis, String destPath) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(destPath);
			byte[] bytes = new byte[1024];
			int length = 0;
			while ((length = fis.read(bytes)) != -1) {
				fos.write(bytes, 0, length);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} finally {
			JavaUtil.closeStream(fis);
			JavaUtil.closeStream(fos);
		}
	}

	public static void writeMiddleModel(String time, String technicsPath,
			File file) {
		String path = FileUtil.generatePath(technicsPath, time);
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String outputPath = path + file.getName();
		File outputFile = new File(outputPath);
		if (!outputFile.exists()) {
			try {
				outputFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		copyFile(file.getAbsolutePath(), outputPath);
	}

	/**
	 * 写简图
	 */
	public static List<String> writeImage(File file, String path) {
		List<String> list = new ArrayList<String>();
		String absolutePath = file.getAbsolutePath();
		String fileName = file.getName();
		String name, type;
		if (fileName.contains(".")) {
			name = fileName.split("\\.")[0];
			type = file.getName().split("\\.")[1];
		} else {
			name = fileName;
			type = "";
		}
		float size = 0;
		java.io.FileInputStream in = null;
		try {
			in = new FileInputStream(absolutePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			size = in.available();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		list.add(name);
		list.add(type);
		list.add(getFileSize(size));
		list.add(path);
		return list;

	}

	public static String getFileSize(float size) {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		if (size / 1024 > 1024) {
			return String.valueOf(nf.format(size / (1024 * 1024))) + "MB";
		} else {
			return String.valueOf(nf.format(size / 1024)) + "KB";
		}
	}

	/**
	 * 获取DefaultPV图片
	 *
	 * @return
	 */
	public static byte[] getImage(String imageName) {
		InputStream is = null;
		byte[] bytes = null;
		BufferedInputStream bis = null;
		try {
			is = FileUtil.class.getResourceAsStream("/image/" + imageName);
			bis = new BufferedInputStream(is);
			// bytes = new byte[is.available()];
			// is.read(bytes);
			bytes = new byte[bis.available()];
			bis.read(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			CappJavaUtil.closeStream(bis);
		}
		return bytes;
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

	public static void main(String[] args) {
		copyFile("d://jd-gui.exe", "d://a.log");
	}


	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "\\" + tempList[i]);
				delFolder(path + "\\" + tempList[i]);
				flag = true;
			}
		}
		return flag;
	}
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath);
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
