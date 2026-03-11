package com.glaway.mpm.qmIntf.technics;

import java.io.IOException;

import com.glaway.mpm.sop.util.SopProcessUtil;
import org.dom4j.DocumentException;

import com.glaway.mpm.release.ProcessInfoReleaseController;

public class TechnicsPreview {
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
	}

	/**
	 * 在指定目录下发布
	 */
	public static String previewSop(String zipSrc, String path) {
		return SopProcessUtil.processInfoRelease(zipSrc, path);
	}



}