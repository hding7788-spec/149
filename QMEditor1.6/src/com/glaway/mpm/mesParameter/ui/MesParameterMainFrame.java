package com.glaway.mpm.mesParameter.ui;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.mesParameter.helper.MesParameterProcessor;
import com.glaway.mpm.mesParameter.listener.MesParameterFrameWindowListener;
import com.glaway.mpm.mesParameter.model.MesBfcccpbh;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.FileUtil;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import wt.method.RemoteMethodServer;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MesParameterMainFrame extends JFrame {
	/**
	 *	MES入口类
	 */
	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(MesParameterMainFrame.class.getName());
	private static String imageFolder = System.getProperty("user.home") + File.separator +"paramImages";
	private static MesParameterMainPanel mesParameterMainPanel;

	private static String technicNumber;
	private static String fzTechnicNumber;
	private static String fzVersion;
	private static String ppNumber;
	private static String produreNumber;
	private static String lukahao;
	private static String jianyanyuan;
	private static String caozuoyuan;
	private static String gxPK;


	public MesParameterMainFrame() {
		initComponents();
        initUI();
	}

	private void initUI() {
		addWindowListener(new MesParameterFrameWindowListener(this));

		//加载结构树
//		MesParameterProcessor.loadMesTree();

    	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//    	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//    	gd.setFullScreenWindow(this);
//    	int width = Toolkit.getDefaultToolkit().getScreenSize().width;
//    	int height = Toolkit.getDefaultToolkit().getScreenSize().height;
//    	setSize(width, height);
    	this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    	setTitle("MES数据管理工具");
    	CommonUIUtil.setMiddleOnScreenWithDialog(this);
		setVisible(true);
	}

	private void initComponents() {

		mesParameterMainPanel = new MesParameterMainPanel(this);

		add(mesParameterMainPanel);

		mesParameterMainPanel.initMesTree();

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
			args = new String[8];
			args[0] = "1499052513146_ZF";  //1495071616904_ZF  1499052513146_ZF
			args[1] = "30";
			args[2] = "lukahao";
			args[3] = "mesgxpk";
			args[4] = "";
			args[5] = "123";
			args[6] = "1499053508977";
			args[7] = "space";
		}
		if ((args != null) && (args.length > 0)) {
			technicNumber = args[0];
			produreNumber =args[1];
			lukahao = args[2];
			gxPK = args[3];
			jianyanyuan = args[4];
			caozuoyuan = args[5];
			fzTechnicNumber = args[6];
			fzVersion = args[7];
		}
		startMesMainFrame(args);
	}
	public static void startMesMainFrame(final String[] args){
		if ((args != null) && (args.length > 0)) {
			if ((args != null) && (args.length > 0)) {
				for (int i = 0; i < args.length; i++) {
					logger.debug("参数 " + i + " ========" + args[i]);
				}

				technicNumber = args[0];
				produreNumber =args[1];
				lukahao = args[2];
				gxPK = args[3];
				jianyanyuan = args[4];
				caozuoyuan = args[5];
				fzTechnicNumber = args[6];
				fzVersion = args[7];

				System.out.println("------Mes----\r\ntechnicNumber:"+technicNumber+"\r\n"+
						"produreNumber:"+produreNumber + "\r\n" +
						"lukahao:" + lukahao + "\r\n" +
						"gxPK:" + gxPK + "\r\n" +
						"jianyanyuan:" + jianyanyuan + "\r\n" +
						"caozuoyuan:" + caozuoyuan + "\r\n" +
						"fzTechnicNumber" + fzTechnicNumber + "\r\n" +
						"fzVersion" + fzVersion);
				RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
				methodServer.setUserName("wcadmin");
				methodServer.setPassword("Admin@149");
//				methodServer.setPassword("wcadmin");
				Vector<Object> result = null;
				if(technicNumber.contains("_ZF")){
					result = MesParameterProcessor.getTechnicsByTechnicNumber(technicNumber.substring(0, technicNumber.indexOf("_")));
				}else{
					result = MesParameterProcessor.getTechnicsByTechnicNumber(technicNumber);
				}
				if(result != null && result.size() >= 2){
					ppNumber = (String) result.get(2);
				}

			}
		}
		try {
			System.setProperty("swing.useSystemFontSettings", "0");
			System.setProperty("swing.handleTopLevelPaint", "false");
			System.setProperty("-Dswing.aatext", "true");

			LookUtils.setLookAndTheme(new Plastic3DLookAndFeel(), new ExperienceBlue());

			//设置字体样式
			CommonUIUtil.setGlobalFont("宋体", 0, 14);

//			RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
//			methodServer.setUserName("wcadmin");
//			methodServer.setPassword("Admin@149");

			//设置临时工作目录
			File tempFile = new File(imageFolder + File.separator +"content");
			if (!tempFile.exists()) {
				tempFile.mkdirs();
			}
			new MesParameterMainFrame();
		} catch (UnsupportedLookAndFeelException e) {
			logger.error(e);
		}
	}

	public static List<String> getProcessNumberValues(){
		List<String> processNumberList = new ArrayList<String>();
		processNumberList.add("全选");
		List<MesBfcccpbh> mesBfcccpbhList = MesParameterProcessor.getProcessNumberValues(lukahao);
		for(MesBfcccpbh mesBfcccpbh : mesBfcccpbhList){
			String bffccpbhno = mesBfcccpbh.getBfccpbhno();
			if(bffccpbhno != null){
				processNumberList.add(bffccpbhno);
			}
		}
		return processNumberList;
	}
	public static MesParameterMainPanel getMesParameterMainPanel() {
		return mesParameterMainPanel;
	}

	public static String getTechnicNumber() {
		return technicNumber;
	}

	public static String getProdureNumber() {
		return produreNumber;
	}

	public static String getLukahao() {
		return lukahao;
	}

	public static String getJianyanyuan() {
		return jianyanyuan;
	}

	public static String getCaozuoyuan() {
		return caozuoyuan;
	}

	public static String getGxPK() {
		return gxPK;
	}

	public static String getPpNumber() {
		return ppNumber;
	}

	public static String getFzTechnicNumber() {
		return fzTechnicNumber;
	}

	public static String getFzVersion() {
		return fzVersion;
	}

}
