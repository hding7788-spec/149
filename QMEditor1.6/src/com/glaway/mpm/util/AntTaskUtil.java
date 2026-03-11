package com.glaway.mpm.util;

import java.net.URL;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class AntTaskUtil {
	private static Project p;

	static {
		p = new Project();
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_DEBUG);
		p.addBuildListener(consoleLogger);
		p.fireBuildStarted();
		p.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		URL buildUrl = AntTaskUtil.class
				.getResource("/resource/task_build.xml");
		helper.parse(p, buildUrl);
	}

	/**
	 * 解压缩
	 */
	public static void uncompress(String zipsrc, String zipdest) {
		p.setProperty("zipsrc", zipsrc);
		p.setProperty("zipdest", zipdest);
		p.executeTarget("uncompress");
		p.fireBuildFinished(null);
	}

	/**
	 * 拷贝文件夹
	 */
	public static void copydir(String srcdir, String destdir) {
		p.setProperty("srcdir", srcdir);
		p.setProperty("destdir", destdir);
		p.executeTarget("copydir");
		p.fireBuildFinished(null);
	}

}
