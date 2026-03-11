package com.glaway.mpm.consCheck;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.util.CommonUIUtil;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import wt.method.RemoteMethodServer;

import javax.swing.*;
import java.awt.*;

public class ConsCheckRecordsMainFrame extends JFrame {
	/**
	 *	结构检验记录入口类
	 */
	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(ConsCheckRecordsMainFrame.class.getName());
	private static ConsCheckRecordsMainPanel consCheckRecordsMainPanel;

	public static String type;
	public static String PRO = "PRO";
	public static String TABLE = "TABLE";

	public ConsCheckRecordsMainFrame() {
		initComponents();
        initUI();
	}

	private void initUI() {
		if(PRO.equals(type)){
			setTitle("项目配置");
		}else if(TABLE.equals(type)){
			setTitle("套表配置");
		}
		int height = Toolkit.getDefaultToolkit().getScreenSize().height/2;
		setSize(800, height);
		CommonUIUtil.setMiddleOnScreenWithDialog(this);
		setIconImage(new ImageIcon(getClass().getResource("/images/cappBom_cappDesign.gif")).getImage());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private void initComponents() {
		consCheckRecordsMainPanel = new ConsCheckRecordsMainPanel(this);
		add(consCheckRecordsMainPanel);
	}

	public void closeWindow() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		System.gc();
	}

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			args = new String[1];
			args[0] = "PRO";
		}
		if ((args != null) && (args.length > 0)) {
			type = args[0];
		}
		startConsCheckMainFrame(args);
	}
	public static void startConsCheckMainFrame(final String[] args){
		if ((args != null) && (args.length > 0)) {
			if ((args != null) && (args.length > 0)) {
				for (int i = 0; i < args.length; i++) {
					logger.debug("参数 " + i + " ========" + args[i]);
				}
				type = args[0];
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
			methodServer.setPassword("wcadmin");

			new ConsCheckRecordsMainFrame();
		} catch (UnsupportedLookAndFeelException e) {
			logger.error(e);
		}
	}

	public String getTypeStr() {
		return type;
	}

	public String getPRO() {
		return PRO;
	}

	public String getTABLE() {
		return TABLE;
	}
}