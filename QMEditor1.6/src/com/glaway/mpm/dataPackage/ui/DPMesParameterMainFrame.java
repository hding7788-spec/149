package com.glaway.mpm.dataPackage.ui;

import java.io.File;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import wt.method.RemoteMethodServer;

import com.glaway.mpm.dataPackage.helper.DPMesParameterProcessor;
import com.glaway.mpm.dataPackage.listener.MesParameterFrameWindowListener;
import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.mesParameter.helper.MesParameterProcessor;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.FileUtil;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;

public class DPMesParameterMainFrame extends JFrame {
	/**
	 * MES入口类
	 */
	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(DPMesParameterMainFrame.class.getName());
	private static String imageFolder = System.getProperty("user.home") + File.separator + "paramImages";
	private static MesParameterMainPanel mesParameterMainPanel;

	private static String productNumber;
	private static String technicsNumber;
	private static String ppNumber;

	public DPMesParameterMainFrame() {
		initComponents();
		initUI();
	}

	private void initUI() {
		addWindowListener(new MesParameterFrameWindowListener(this));

		// 加载结构树
		// MesParameterProcessor.loadMesTree();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		// GraphicsDevice gd =
		// GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		// gd.setFullScreenWindow(this);
		// int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		// int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		// setSize(width, height);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		setTitle("MES数据管理工具");
		CommonUIUtil.setMiddleOnScreenWithDialog(this);
		setVisible(true);
	}

	private void initComponents() {

		mesParameterMainPanel = new MesParameterMainPanel();

		add(mesParameterMainPanel);

	}

	public static String getImageFolder() {
		imageFolder = imageFolder.replace(File.separator, "/");
		return imageFolder;
	}

	public void closeWindow() {
		FileUtil.delAllFile(imageFolder);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		System.gc();
	}

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			args = new String[6];
			args[0] = "CP-161-S%2F-20(*1)";

		}
		if ((args != null) && (args.length > 0)) {
			productNumber = args[0];

		}
		startMesMainFrame(args);
	}

	public static void startMesMainFrame(final String[] args) {
		if ((args != null) && (args.length > 0)) {
			if ((args != null) && (args.length > 0)) {
				for (int i = 0; i < args.length; i++) {
					logger.debug("参数 " + i + " ========" + args[i]);
				}

				productNumber = args[0];

				RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
				methodServer.setUserName("wcadmin");
				methodServer.setPassword("wcadmin");

				technicsNumber = DPMesParameterProcessor.getTechnicsNumberByProductNumber(productNumber);
				if ("".equals(technicsNumber)) {
					technicsNumber = DPMesParameterProcessor.getTechnicsNumberByProductNumber(productNumber + "_ZF");
					technicsNumber = DPMesParameterProcessor.getZzTechnicsNumber(technicsNumber) + "_ZF";
				}
				Vector<Object> result = null;
				if (!"_ZF".equals(technicsNumber)) {
					if (technicsNumber.contains("_ZF")) {
						result = MesParameterProcessor.getTechnicsByTechnicNumber(technicsNumber.substring(0, technicsNumber.indexOf("_")));
					} else {
						result = MesParameterProcessor.getTechnicsByTechnicNumber(technicsNumber);
					}
				}else{
					System.out.println("该产品编号没有对应的工艺");
				}
				if (result != null && result.size() >= 2) {
					ppNumber = (String) result.get(2);
				}
				System.out.println("------DataPackage------productNumber:" + productNumber);
			}
		}
		try {
			System.setProperty("swing.useSystemFontSettings", "0");
			System.setProperty("swing.handleTopLevelPaint", "false");
			System.setProperty("-Dswing.aatext", "true");

			LookUtils.setLookAndTheme(new Plastic3DLookAndFeel(), new ExperienceBlue());

			// 设置字体样式
			CommonUIUtil.setGlobalFont("宋体", 0, 14);

			RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
			methodServer.setUserName("wcadmin");
			methodServer.setPassword("wcadmin");

			// 设置临时工作目录
			File tempFile = new File(imageFolder + File.separator + "content");
			if (!tempFile.exists()) {
				tempFile.mkdirs();
			}
			new DPMesParameterMainFrame();
		} catch (UnsupportedLookAndFeelException e) {
			logger.error(e);
		}
	}

	public static MesParameterMainPanel getMesParameterMainPanel() {
		return mesParameterMainPanel;
	}

	public static String getProductNumber() {
		return productNumber;
	}

	public static String getTechnicsNumber() {
		return technicsNumber;
	}

	public static String getPpNumber() {
		return ppNumber;
	}

}
