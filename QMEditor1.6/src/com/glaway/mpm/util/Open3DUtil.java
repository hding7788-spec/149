package com.glaway.mpm.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.JOptionPane;

import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ImageIntf;

public class Open3DUtil {
	private static VaLogger logger = VaLogger.getLogger(Open3DUtil.class);

	public static boolean open(Map<String, String> map, String filePath) {
		try {
//			String cmdStr = map.get("toolPath") + " " + filePath;
			String cmdPath = map.get("toolPath");
			cmdPath = cmdPath.replace(" ", "\" \"");
			String cmdStr = "cmd /c start "+ cmdPath + " " + filePath;
			logger.debug("createMidModel open " + cmdStr);
			Runtime.getRuntime().exec(cmdStr);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 打开中间模型
	 *
	 * @param map
	 * @param filePath
	 * @return
	 */
	public static boolean createMidModel(Map<String, String> map,
			String filePath) {
		return Open3DUtil.open(map, filePath);
	}

	@SuppressWarnings("unused")
	public static boolean createAnimation3D(Map<String, String> map) {
		logger.debug("createMidModel::oid " + map.get("oid"));
		logger.debug("createMidModel::partNumber " + map.get("partNumber"));

		logger.debug("createAnimation3D::xmlPath" + map.get("xmlPath"));
		logger.debug("createMidModel::toolPath " + map.get("toolPath"));
		logger.debug("createMidModel::filePath " + map.get("filePath"));
		String filDir = ImageIntf.getAnimationCAD(map);
		logger.debug("filDir from windchill " + filDir);
		if (filDir != null) {
			try {
				String projectName = map.get("partNumber");

				String toolPath = map.get("toolPath");
				File file = new File(toolPath);
				String rapidMaunl;
				if (file == null) {
					JOptionPane.showMessageDialog(null,
							"Cortona3d安装路径指定错误，请在菜单设置-》我的设置中设置",
							"Cortona3d启动异常", JOptionPane.WARNING_MESSAGE);
					return false;
				} else {
					rapidMaunl = "\"" + file.getParentFile().getParent()
							+ "\\RapidManual\\RapidManual.exe\"";
				}

				String cmdStr = "\"" + toolPath + "\"" + " /newproject "
						+ projectName + " /runplugin RapidDataImportTool_ProE "
						+ "\"" + filDir + "\"";// +
				// " GENERIC_SIMPLE";
				logger.debug("createAnimation3D windchill cmd String :::::"
						+ cmdStr);

				Runtime.getRuntime().exec(cmdStr);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Runtime.getRuntime().exec(rapidMaunl);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

		} else {
			try {
				String projectName = "testCortona";

				File f = new File("D:\\asm\\");
				String[] fname = f.list();
				String asmFile = null;
				for (int i = 0; i < fname.length; i++) {
					if (fname[i].indexOf(".asm") > 0) {
						asmFile = fname[i];
						break;
					}
				}
				String cmdStr = "\"" + map.get("toolPath") + "\""
						+ " /newproject " + projectName
						+ " /runplugin RapidDataImportTool_ProE "
						+ "\"D:\\asm\\" + asmFile + "\"" + " GENERIC_SIMPLE";
				logger.debug("createAnimation3D cmd String :::::" + cmdStr);
				Runtime.getRuntime().exec(cmdStr);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public static void main(String[] args) {
		// Map<String, String> m = new HashMap<String, String> ();
		// m.put("toolPath",
		// "D:\\creo2.0\\Creo 2.0\\Parametric\\bin\\parametric.exe");
		// m.put("filePath", "D:\\model\\arm.prt");

		// Open3DUtil.createMidModel(m);
		try {
			// Runtime.getRuntime().exec("D:\\creo2.0\\Creo 2.0\\Parametric\\bin\\parametric.bat");
			// Runtime.getRuntime().exec("cmd.exe");
			// Runtime.getRuntime().exec("java -version");
			// Open3DUtil.copyXmlFile("C:\\mpm\\mySetting.xml","c:\\mpms\\2");
			// File file = new File("C:\\mpm\\mySetting.xml");
			//
			// FileUtil.copyFile(new FileInputStream(file), "c:\\mpm\\2" + "\\"
			// + file.getName());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
