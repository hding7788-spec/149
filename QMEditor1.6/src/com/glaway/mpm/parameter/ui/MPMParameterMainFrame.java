package com.glaway.mpm.parameter.ui;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.parameter.helper.MPMParameterProcessor;
import com.glaway.mpm.parameter.listener.ParameterFrameWindowListener;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.FileUtil;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import wt.method.RemoteMethodServer;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MPMParameterMainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(MPMParameterMainFrame.class.getName());
	private static String imageFolder = System.getProperty("user.home") + File.separator +"paramImages";
	private static JSplitPane mainJPanel;
	private static LeftTreePanel leftPanel;
	private static JPanel rightPanel;
	private static ParameterTypeInfoPanel parameterTypeInfoPanel;
	private static ParamTableTypePanel paramTableTypePanel;
	private static BaiyuParamTablePanel baiyuParamTablePanel;

	public MPMParameterMainFrame() {
		initComponents();
        initUI();
	}

	private void initUI() {
		addWindowListener(new ParameterFrameWindowListener(this));

		//加载结构树
		MPMParameterProcessor.loadQualityTree();

    	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    	int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    	int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    	setSize(width, height);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    	setTitle("工艺质量数据管理工具");
//    	CommonUIUtil.setMiddleOnScreenWithDialog(this);
		setVisible(true);
		mainJPanel.setDividerLocation(0.15);
	}

	private void initComponents() {
		mainJPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainJPanel.setDividerSize(3);

		leftPanel = new LeftTreePanel();
		rightPanel = new JPanel();
		rightPanel.setLayout(new VFlowLayout(0, 0, 0, true, true));
		mainJPanel.setLeftComponent(leftPanel);
		mainJPanel.setRightComponent(rightPanel);
		add(mainJPanel);

		parameterTypeInfoPanel = new ParameterTypeInfoPanel();
		paramTableTypePanel = new ParamTableTypePanel();
		baiyuParamTablePanel = new BaiyuParamTablePanel(this);
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

	public static LeftTreePanel getLeftPanel() {
		return leftPanel;
	}

	public static JPanel getRightPanel() {
		return rightPanel;
	}

	public static ParameterTypeInfoPanel getParameterTypeInfoPanel() {
		return parameterTypeInfoPanel;
	}

	public static ParamTableTypePanel getParamTableTypePanel() {
		return paramTableTypePanel;
	}

	public static BaiyuParamTablePanel getBaiyuParamTablePanel() {
		return baiyuParamTablePanel;
	}

	public static void main(String[] args) {
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

			new MPMParameterMainFrame();
		} catch (UnsupportedLookAndFeelException e) {
			logger.error(e);
		}
	}
}
