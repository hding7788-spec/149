package com.glaway.mpm.qmIntf.technics;

import java.io.IOException;

import org.dom4j.DocumentException;

import com.glaway.mpm.release.ProcessInfoReleaseController;

public class BorrowTechnicsPreview {
	public static String PREVIEW = "预览";
	public static String PUBLISH = "发布";

	/**
	 * 在个人主目录下预览
	 * @throws Exception
	 */
	public static void preview(String technicsFolderPath) throws Exception {
		ProcessInfoReleaseController.processInfoPview(technicsFolderPath);
		// ProcessInfoReleaseController.processInfoRelease(technicsFolderPath);
		// String url = main(System.getProperty("user.home"),
		// technicsFolderPath,
		// technicsFolderPath.substring(technicsFolderPath.lastIndexOf(File.separator)
		// + 1),
		// TechnicsPreview.PREVIEW);
		// CommonUtil.openURL(url);
	}

	/**
	 * 在指定目录下发布
	 */
	public static String preview(String zipSrc, String path) {
		return ProcessInfoReleaseController.processInfoRelease(zipSrc, path);
		// return invokeMain(zipSrc, path, TechnicsPreview.PREVIEW);
	}

	// /**
	// * 发布
	// */
	// public String publish(String zipSrc, String path) {
	// return invokeMain(zipSrc, path, TechnicsPreview.PUBLISH);
	// }
	//
	// private String invokeMain(String zipSrc, String path, String type) {
	// return main(path, zipSrc,
	// zipSrc.substring(zipSrc.lastIndexOf(File.separator) + 1,
	// zipSrc.lastIndexOf('.')),
	// type);
	// }
	//
	// private static String main(String destPath, String srcPath, String
	// srcName, String type) {
	// String publishFolderPath = destPath + File.separator + type;
	// File sourceFolder = createDirs(publishFolderPath + File.separator +
	// "resource");
	// copyFromJar(sourceFolder);
	// File pubDir = createDir(publishFolderPath + File.separator + type + "_" +
	// srcName);
	// File srcFile = new File(srcPath);
	// if (srcFile.isDirectory()) {
	// AntTaskUtil.copydir(srcPath, pubDir.getAbsolutePath());
	// } else {
	// // AntTaskUtil.uncompress(srcPath, pubDir.getAbsolutePath());
	// ApacheZipUtil.decompress(srcPath, pubDir.getAbsolutePath());
	// }
	// String xmlFile = findXML(pubDir);
	// XmlMapper.initData(null, pubDir.getAbsolutePath() + File.separator +
	// xmlFile, pubDir.getAbsolutePath(), type);
	// return pubDir.getAbsolutePath() + File.separator +
	// Constants.PUBLISH_HTML;
	// }
	//
	// /**
	// * 从jar包中拷resource里的文件到本地resource中
	// */
	// private static void copyFromJar(File sourceFolder) {
	// try {
	// String sourceFolderPath = sourceFolder.getAbsolutePath() +
	// File.separator;
	// // URL url =
	// //
	// getClass().getProtectionDomain().getCodeSource().getLocation();//得到该类所在jar包路径
	//
	// String path =
	// TechnicsPreview.class.getResource(TechnicsPreview.class.getSimpleName() +
	// ".class").getFile();
	// if (path.lastIndexOf('!') == -1) {
	// System.out.println(path.lastIndexOf("com/glaway/mpm"));
	// path = path.substring(0, path.lastIndexOf("com/glaway/mpm"));
	// File file = new File(path + File.separator + "technicsSource");
	// FileUtil.copyDirectory(file, sourceFolderPath);
	// return;
	// }
	//
	// path = "jar:" + path.substring(0, path.indexOf("!") + 2);
	// URL url = new URL(path);
	// JarURLConnection con = (JarURLConnection) url.openConnection();
	//
	// // JarFile file = new JarFile(url.getFile());
	// JarFile jarFile = con.getJarFile();
	//
	// Enumeration<JarEntry> entries = jarFile.entries();
	// while (entries.hasMoreElements()) {
	// JarEntry entry = entries.nextElement();
	// String name = entry.getName();
	// // if(name.startsWith("technicsSource/com/faw_qm/capputil/util")){
	// // File util = createDirs(sourceFolderPath +
	// // "com\\faw_qm\\capputil\\util");
	// // CommonUtil.writeInputStreamToFile(jarFile.getInputStream(entry),
	// // util.getAbsolutePath() + File.separator +
	// // name.substring(name.lastIndexOf("/") + 1));
	// // }else
	// // if(name.startsWith("technicsSource/com/faw_qm/capputil/view")){
	// // File view = createDirs(sourceFolderPath +
	// // "com\\faw_qm\\capputil\\view");
	// // CommonUtil.writeInputStreamToFile(jarFile.getInputStream(entry),
	// // view.getAbsolutePath() + File.separator +
	// // name.substring(name.lastIndexOf("/") + 1));
	// // }else if(name.startsWith("technicsSource/images")){
	// // File images = createDir(sourceFolderPath + "images");
	// // CommonUtil.writeInputStreamToFile(jarFile.getInputStream(entry),
	// // images.getAbsolutePath() + File.separator +
	// // name.substring(name.lastIndexOf("/") + 1));
	// // }else if(name.startsWith("technicsSource/logimage")){
	// // File logimage = createDir(sourceFolderPath + "logimage");
	// // CommonUtil.writeInputStreamToFile(jarFile.getInputStream(entry),
	// // logimage.getAbsolutePath() + File.separator +
	// // name.substring(name.lastIndexOf("/") + 1));
	// // }else
	// if (name.startsWith("technicsSource" + File.separator +
	// "res_GENERIC_PRC")) {
	// File res_GENERIC_PRC = createDir(sourceFolderPath + "res_GENERIC_PRC");
	// CommonUtil.writeInputStreamToFile(jarFile.getInputStream(entry),
	// res_GENERIC_PRC.getAbsolutePath()
	// + File.separator + name.substring(name.lastIndexOf(File.separator) + 1));
	// } else if (name.startsWith("technicsSource" + File.separator)) {
	// CommonUtil.writeInputStreamToFile(jarFile.getInputStream(entry),
	// sourceFolderPath + name.substring(name.lastIndexOf(File.separator) + 1));
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// /**
	// * 在指定文件夹下查找xml文件
	// */
	// private static String findXML(File dir) {
	// File[] files = dir.listFiles();
	// for (int i = 0; i < files.length; i++) {
	// String name = files[i].getName();
	// if (name.endsWith(".xml") && !name.equals("technics_route.xml")) {
	// return name;
	// }
	// }
	// return "";
	// }
	//
	// /**
	// * 深层创建文件夹
	// */
	// private static File createDirs(String path) {
	// File dir = new File(path);
	// if (!dir.exists()) {
	// dir.mkdirs();
	// }
	// return dir;
	// }
	//
	// /**
	// * 创建文件夹
	// */
	// private static File createDir(String path) {
	// File dir = new File(path);
	// if (!dir.exists()) {
	// dir.mkdir();
	// }
	// return dir;
	// }

}