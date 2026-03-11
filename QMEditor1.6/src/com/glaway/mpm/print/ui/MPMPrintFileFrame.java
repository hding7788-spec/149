package com.glaway.mpm.print.ui;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.model.data.CmUser;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.helper.MPMPrintHelper;
import com.glaway.mpm.print.helper.MPMPrintProcessor;
import com.glaway.mpm.print.listener.PrintFrameWindowListener;
import com.glaway.mpm.print.service.PrintToWCIntf;
import com.glaway.mpm.print.util.FilePrintUtil;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.IconUtil;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import wt.method.RemoteMethodServer;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

public class MPMPrintFileFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(MPMPrintFileFrame.class.getClass());

	private static String oid;
	private static String type;
	private static String infor;
	private static String category;
	public static String dept;
	public static String wfaOid;
	public static boolean isComplete = false;
//	private static String pboOid;
	private static Boolean isFromRecover;
	//private static Boolean isFromStore;
	private static FilePrintMainPanel fileMainPanel;
    /** 当前用户信息 */
    private static CmUser currentUser;
    private static int width;
    private static List<CmPrintInfoBean> printInfoBeanList;

    public MPMPrintFileFrame() {
        initComponents();
        initUI();
    }

	private void initComponents() {
		currentUser = MPMPrintProcessor.getCurrentUser();
		fileMainPanel = new FilePrintMainPanel(this, type, category);
		if(PrintConstants.TITLE_MAINPANEL_REQUEST.equals(type)||
				PrintConstants.TITLE_MAINPANEL_ZXDYSQ.equals(type)||
				PrintConstants.TITLE_MAINPANEL_YLDYSQ.equals(type)||
					PrintConstants.TITLE_MAINPANEL_LQZZWJ.equals(type)||
						PrintConstants.TITLE_MAINPANEL_JGYZGL.equals(type)||
							PrintConstants.TITLE_MAINPANEL_JGYZQR.equals(type)){
			printInfoBeanList = MPMPrintHelper.loadData(oid, type,dept);
		}else{
			if (oid != null && !"".equals(oid)) {
				System.out.println(" ==== oid =====" + oid);
				System.out.println(" ==== type =====" + type);
				printInfoBeanList = MPMPrintHelper.loadData(oid, type,dept);
			}else {
				printInfoBeanList = MPMPrintHelper.loadData(type, type,dept);
			}
		}
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		springLayout.putConstraint(SpringLayout.NORTH, fileMainPanel, 0, SpringLayout.NORTH, this.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, fileMainPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, fileMainPanel, 0, SpringLayout.EAST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, fileMainPanel, 0, SpringLayout.SOUTH, this.getContentPane());
		add(fileMainPanel);
    }

    private void initUI() {
    	addWindowListener(new PrintFrameWindowListener(this));

    	setIconImage(IconUtil.getImageIcon(IconUtil.TECHNICS).getImage());
    	setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    	setResizable(true);
		setTitle(type);
    	if("ZZ".equals(category) && type.equals(PrintConstants.TITLE_MAINPANEL_WLWJLR)){
    		setTitle(PrintConstants.TITLE_MAINPANEL_ZZWJLR);
    	}else if("SJ".equals(category)){
    		setTitle(PrintConstants.TITLE_MAINPANEL_SJZZWJ);
    	}else if("LQ".equals(category)){
    		setTitle(PrintConstants.TITLE_MAINPANEL_LQZZWJ);
    	}else if("WL".equals(category)){
    		if("文件打印申请".equals(type)){
				setTitle(PrintConstants.TITLE_MAINPANEL_WLWJDYSQ);
			}
		}
    	width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		setSize((int) (width/1.125), (int) (height/1.125));
		setLocationRelativeTo(null);
		setVisible(true);
    }

    public void closeWindow() {
		System.gc();
		String pdfTempPath = FileUtil.getTmpPath(PrintConstants.FOLDER_PDFTEMP);
		FileUtil.delAllFile(pdfTempPath);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		System.exit(0);
	}

	public static String getOid() {
		return oid;
	}

	public static String getTypeStr() {
		return type;
	}

	public static FilePrintMainPanel getFileMainPanel() {
		return fileMainPanel;
	}

	public static CmUser getCurrentUser() {
		return currentUser;
	}

	public static int getFrameWidth() {
		return width;
	}

	public static String getCategory(){
		return category;
	}

	public static List<CmPrintInfoBean> getPrintInfoBeanList(){
		return printInfoBeanList;
	}

	/**
	* 参数说：
	* oid:工艺规程对象OID
	*/
	public static void main(String[] args) {
		logger.info("args:"+Arrays.toString(args));
		try {
			if (args != null && args.length > 0) {
				System.out.println("args:"+args);
				if(args[1].contains("_")){//如果type中带有"_"，截取以"_"分割的种类category
					category = args[1].substring(args[1].indexOf("_") + 1, args[1].length());
					type = FilePrintUtil.getPrintType(args[1].substring(0,args[1].indexOf("_")));
				}else{
					type = FilePrintUtil.getPrintType(args[1]);
				}
				oid = args[0];
				infor = args[2];
//				pboOid = args[3];
				isFromRecover = Boolean.parseBoolean(args[4]);
				if("WL".equals(MPMPrintProcessor.getCategory(oid)) || "ZZ".equals(MPMPrintProcessor.getCategory(oid))){
					category = MPMPrintProcessor.getCategory(oid);
				}
				dept = args[6];
				wfaOid = args[7];
				System.out.println(dept);
			}else {
				//[3662919, WJDYSQ, null, null, null, null, 一分厂]3666408, ZXDYSQ, null, null, null, null, 1
				oid = "";
				type = FilePrintUtil.getPrintType("GYWJZY");//打印申请
//				type = FilePrintUtil.getPrintType("CNWJZXFQHS");//厂内文件自行发起回收
//				type = FilePrintUtil.getPrintType("HSWJQR");//回收文件确认
				category = "";
				infor = "";
				dept = "";
				isComplete=false;
				isFromRecover = true;
//				pboOid = "3990768";
//				WJDYSQ  文件打印申请
//				WJBDSQ  文件补打申请
//              YLDYSQ  预览打印申请
//              ZXDYSQ  执行打印申请
//				LQZZWJ  领取纸质文件
//				JGYZGL  加盖印章管理
//				JGYZQR  加盖印章确认
//				WLWJLR  外来文件录入
//				GYWJZY  工艺文件转移
//				-----------------
//				WJCXDY  文件重新打印
//				WJRK    文件入库
//				CNWJZXFQHS 厂内文件回收
//				WLWJZXFQHS 外来文件回收
//				CNZZWJZXFQHS 厂内纸质文件回收
//				WJTMCX  文件条码信息查询
//				CNWJFC  厂内文件封存
//				DYFFJLCX  打印分发记录查询
//				GGHSWJQR  更改回收文件确认
				RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
				methodServer.setUserName("huhuili");
				methodServer.setPassword("1");
			}

			dept = transDept(dept);
			if(wfaOid != null){
				isComplete = PrintToWCIntf.isWfaComplete(wfaOid);
			}
			System.setProperty("swing.useSystemFontSettings", "0");
			System.setProperty("swing.handleTopLevelPaint", "false");
			System.setProperty("-Dswing.aatext", "true");

			LookUtils.setLookAndTheme(new Plastic3DLookAndFeel(), new ExperienceBlue());

			//设置字体样式
			CommonUIUtil.setGlobalFont("宋体", 0, 14);

			if(FilePrintUtil.getPrintType("YZGL").equals(type)){
				new SealManagerMainPanel();//印章管理
			}else if(FilePrintUtil.getPrintType("DYFFJLCX").equals(type)){
				new PrintRecordQueryMainPanel();//打印分发记录查询
			}else if(FilePrintUtil.getPrintType("CNWJZXFQHS").equals(type)){
				new InFactoryRecoverBySelfMainPanel(category);//厂内文件自行发起回收
			}else if(FilePrintUtil.getPrintType("WLWJZXFQHS").equals(type)){
				new OutsideRecoverBySelfMainPanel();//外来文件自行发起回收
			}else if(FilePrintUtil.getPrintType("CNZZWJZXFQHS").equals(type)){
				new InFactoryPaperRecoverBySelfMainPanel();//厂内纸质文件自行发起回收
			}else if(FilePrintUtil.getPrintType("CXHSXX").equals(type)){
				new RecoverFirstPanel(infor);//查询回收信息
			}else if(FilePrintUtil.getPrintType("HSZZWJ").equals(type)){
				new RecoverSecondPanel(infor);//回收纸质文件
			}else if(FilePrintUtil.getPrintType("HSWJQR").equals(type)){
				new RecoverThirdPanel(infor);//回收文件确认
			}else if(FilePrintUtil.getPrintType("XGYCXX").equals(type)){
				new DelayFirstPanel(infor, isFromRecover);//修改延迟信息
			}else if(FilePrintUtil.getPrintType("CKYCXX").equals(type)){
				new DelaySecondPanel(infor, isFromRecover,oid);//查看延迟信息
			}else if(FilePrintUtil.getPrintType("HSYCWJXX").equals(type)){
				new ImmediateSubmitDelayPanel(infor, isFromRecover);//需要回收的延迟文件信息
			}else if(FilePrintUtil.getPrintType("YSXX").equals(type)){
				new LoseFirstPanel(infor,oid);//遗失信息
			}else if(FilePrintUtil.getPrintType("XGYSXX").equals(type)){
				new LoseSecondPanel(infor);//修改遗失信息
			}else if(FilePrintUtil.getPrintType("YCHSYQWJXX").equals(type)){
				new OverTimeFileInfoPanel(infor);//延迟回收逾期文件信息
			}else if(FilePrintUtil.getPrintType("WJCXDY").equals(type)){
				new ReprintMainPanel();//文件重新打印
			}else if(FilePrintUtil.getPrintType("WJTMCX").equals(type)){
				new FileBarcodeQueryPanel();//文件条码查询
			}else if(FilePrintUtil.getPrintType("WJRK").equals(type)){
				new FileStockManagementPanel(category);//文件入库
			}else if(FilePrintUtil.getPrintType("GYWJZY").equals(type)){
				new PrintRecordTransferMainPanel();//工艺文件转移
			}else if(FilePrintUtil.getPrintType("WJZYLB").equals(type)){
				new TransferRecordMainPanel();//文件转移列表
			}else if(FilePrintUtil.getPrintType("CNWJFC").equals(type)){
				new InFactoryFileStorageManagementPanel(category);//厂内文件封存
			}else if(FilePrintUtil.getPrintType("FCZZWJHS").equals(type)){
				new StoreSecondPanel(infor,oid);//封存纸质文件回收
			}else if(FilePrintUtil.getPrintType("FCZZWJQR").equals(type)){
				new StoreThirdPanel(infor,oid);//封存纸质文件确认
			}else if(FilePrintUtil.getPrintType("WLWJFC").equals(type)){
				new OutsideFileStorageManagementPanel();//外来文件封存
			}else if(FilePrintUtil.getPrintType("CNZZWJFC").equals(type)){
				new InFactoryPaperFileStorageManagementPanel();//厂内纸质文件封存
			}else if(FilePrintUtil.getPrintType("CKFCXX").equals(type)){
				new FileOpenStorageInfoShowPanel(infor, oid);//查看封存信息
			}else if(FilePrintUtil.getPrintType("CKXXBYDEPT").equals(type)){
				new FileStorageInfoShowPanel(infor);//根据部门查看封存信息
			}else if(FilePrintUtil.getPrintType("GGWJHSQR").equals(type)){
				new InFactoryChangeRecover(infor, oid);//更改文件回收确认
			}else if(FilePrintUtil.getPrintType("GGHSZZWJ").equals(type)){
				new ChangeRecoverSecondPanel(oid);//更改回收纸质文件
			}else if(FilePrintUtil.getPrintType("GGHSWJQR").equals(type)){
				new ChangeRecoverThirdPanel(oid);//更改回收文件确认
			}else {
				new MPMPrintFileFrame();
			}
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static String transDept(String dept) {
		String str;
		if("1".equals(dept)){
			str = "一分厂";
		}else if("2".equals(dept)){
			str = "二分厂";
		}else if("3".equals(dept)){
			str = "三分厂";
		}else if("4".equals(dept)){
			str = "四分厂";
		}else if("5".equals(dept)){
			str = "五分厂";
		}else if("6".equals(dept)){
			str = "六分厂";
		}else if("7".equals(dept)){
			str = "七分厂";
		}else if("8".equals(dept)){
			str = "八分厂";
		}else if("9".equals(dept)){
			str = "九分厂";
		}else if("10".equals(dept)){
			str = "十分厂";
		}else if("D".equals(dept)){
			str = "档案室";
		}else{
			str = "";
		}
		return str;
	}
}
