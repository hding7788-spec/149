package com.glaway.mpm.mesDataSearch.ui;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.FileUtil;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import wt.method.RemoteMethodServer;

import javax.swing.*;
import java.io.File;

public class MesDataSearchMainFrame extends JFrame {
	/**
	 *	MES入口类
	 */
	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(MesDataSearchMainFrame.class.getName());
	private static String imageFolder = System.getProperty("user.home") + File.separator +"paramImages";
	private static MesDataSearchMainPanel mesDataSearchMainPanel;

	private static String processNumber;
	private static String productNumber;

	public MesDataSearchMainFrame() {
		initComponents();
        initUI();
	}

	private void initUI() {
//		addWindowListener(new MesParameterFrameWindowListener(this));

		//加载结构树
//		MesParameterProcessor.loadMesTree();

    	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//    	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//    	gd.setFullScreenWindow(this);
//    	int width = Toolkit.getDefaultToolkit().getScreenSize().width;
//    	int height = Toolkit.getDefaultToolkit().getScreenSize().height;
//    	setSize(width, height);
    	this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    	setTitle("MES数据查询");
    	CommonUIUtil.setMiddleOnScreenWithDialog(this);
		setVisible(true);
	}

	private void initComponents() {

		mesDataSearchMainPanel = new MesDataSearchMainPanel(this);

		add(mesDataSearchMainPanel);

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
			args = new String[2];
			args[0] = "CP-16110001";
			args[1] = "17042601";
		}
		if ((args != null) && (args.length > 0)) {
			processNumber = args[0];
			productNumber = args[1];
		}
		startMesMainFrame(args);
	}
	public static void startMesMainFrame(final String[] args){
		if ((args != null) && (args.length > 0)) {
			if ((args != null) && (args.length > 0)) {
				for (int i = 0; i < args.length; i++) {
					logger.debug("参数 " + i + " ========" + args[i]);
				}

				processNumber = args[0];
				productNumber = args[1];
				processNumber = processNumber.replace("^", "%");

				System.out.println("------MesData-----\r\nprocessNumber:"+processNumber + "\r\nproductNumber:" + productNumber);

			}
		}
		try {
			System.setProperty("swing.useSystemFontSettings", "0");
			System.setProperty("swing.handleTopLevelPaint", "false");
			System.setProperty("-Dswing.aatext", "true");

			LookUtils.setLookAndTheme(new Plastic3DLookAndFeel(), new ExperienceBlue());

			//设置字体样式
			CommonUIUtil.setGlobalFont("宋体", 0, 14);

			RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
			methodServer.setUserName("wcadmin");
			methodServer.setPassword("Admin@149");

			//设置临时工作目录
			File tempFile = new File(imageFolder + File.separator +"content");
			if (!tempFile.exists()) {
				tempFile.mkdirs();
			}
			new MesDataSearchMainFrame();
		} catch (UnsupportedLookAndFeelException e) {
			logger.error(e);
		}
	}

	public static String getProcessNumber() {
		return processNumber;
	}

	public static String getProductNumber() {
		return productNumber;
	}


}