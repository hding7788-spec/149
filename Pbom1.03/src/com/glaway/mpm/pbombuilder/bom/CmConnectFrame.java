package com.glaway.mpm.pbombuilder.bom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UnsupportedLookAndFeelException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import wt.fc.PersistenceHelper;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTRuntimeException;

import com.glaway.mpm.pbombuilder.action.CmActionProgressBar;
import com.glaway.mpm.pbombuilder.gui.CmTheme;
import com.glaway.mpm.pbombuilder.jws.CmContext;
import com.glaway.mpm.pbombuilder.license.LicenseHelper;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmSearchHelper;
import com.glaway.mpm.pbombuilder.util.LoadConfig;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.ptc.jws.JWSUtil;

/**
 * Created on 2012-10-16
 *
 * @author chenyunlong
 *
 */
public class CmConnectFrame extends JFrame implements ActionListener {
	public static HashMap<String, HashMap<String, String>> partAttrsFromWNC = new HashMap<String, HashMap<String, String>>();
	public static HashMap<String, String> partLinkGYSLFromWNC = new HashMap<String, String>();
	public static Map<String, Map<String,String>> partProsFromWNC = new HashMap<String, Map<String,String>>();
	public static int OCCPATH_MAX = 600;

	private static final long serialVersionUID = 1L;
	private static final CmLogger log = CmLogger.getLogger(CmConnectFrame.class.getName());
	JTextField textUser = null;
	JPasswordField textPwd = null;
	JButton btnClose = null;
	JButton btnConfirm = null;
	public static String partOid;
	public static Long planningOid;
	public static Long designOid;
	public static String productName = "";
	public static String from = "";//启动来源：windchill -WC,工艺编辑器-PE
	public static boolean isPlanningView = false;
	public static String view = "Design";
	public static final CmActionProgressBar startAnimFrame = new CmActionProgressBar(null, null, "启动PBOM编辑器", "正在启动PBOM编辑器，请等待...", "正在启动PBOM编辑器，请等待...");

	public static void main(String[] args) {

		//		checkLicense();
		try {
			LookUtils.setLookAndTheme(new Plastic3DLookAndFeel(), new CmTheme());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		try {
			String tempoid;
			if (args == null || args.length == 0) {
				tempoid = "2539266";
				//				2348721
			} else {
				tempoid = args[0];
				from = args[1];
				if ("PE".equals(from)) {
					log.debug("从工艺编辑器启动PBOM编辑器");
				} else if ("WC".equals(from)) {
					log.debug("从Windchill启动PBOM编辑器");
				}
			}
			partOid = tempoid;
			startPBOM();
			//new CmConnectFrame(partOid);
			//			 new CmConnectFrame("480525");
			log.debug("args[0]----" + partOid);
		} catch (NumberFormatException e) {
			new CmConnectFrame("0");
		}
	}

	public static void startPBOM() {
		Thread startMBom = new Thread() {
			public void run() {
				startAnimFrame.setHeaderMessage("正在启动PBOM编辑器");
				try {
					new CmConnectFrame(partOid);
				} catch (Exception e) {
					log.error(e);
					startAnimFrame.setVisible(false);
					JOptionPane.showMessageDialog(null, "PBOM启动异常");
					System.exit(0);
				}
				startAnimFrame.setHeaderMessage("完成启动PBOM编辑器");
				//检查本地是否存在临时文件
				checkTempPbom();
				startAnimFrame.finish();
				startAnimFrame.setVisible(false);
			}
		};
		startMBom.start();
		startAnimFrame.setVisible(true);
	}

	/**
	 * 检查本地是否存在临时文件
	 * 若存在，提示是否加载临时文件数据
	 */
	private static void checkTempPbom() {
		CmTree mtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree();
		String rootPartNumber = "";
		String rootPartVersion = "";
		String containerId = "";
		Enumeration pbom = mtree.getRoot().children();
		while (pbom.hasMoreElements()) {
			CmTreeNode parentNode = (CmTreeNode) pbom.nextElement();
			rootPartNumber = parentNode.getPart().getPartNumber();
			rootPartVersion = parentNode.getPart().getVersion();
			containerId = String.valueOf(parentNode.getPart().getContainerId());
		}
		if (rootPartNumber == null || "".equals(rootPartNumber) || containerId == null || "".equals(containerId)) {
			return;
		}
		rootPartNumber = rootPartNumber.replace("/", "_");
		rootPartVersion = rootPartVersion.replace(".", "_");
		String filePath = System.getProperty("user.home") + File.separator + "pbomTemp" + File.separator + rootPartNumber + "_" + rootPartVersion + "-pbom_bak.xml";
		File tempFile = new File(filePath);
		if (tempFile.exists()) {
			String modifyTime = getModifiedTime(tempFile);
			int flag = JOptionPane.showConfirmDialog(mtree.getRootPane(), "检测到本地存在 " + modifyTime + " 临时保存的PBOM信息，是否加载？", "确认", JOptionPane.OK_CANCEL_OPTION);
			if (flag == 0) {
				CmLightPart cmLightPart;
				String partOid;
				List<CmTreeNode> mbomlist = CmCommonStringUtil.getBomNodeList(mtree);
				Map<String, Element> tempInfoMap = getTempInfo(tempFile);
				for (CmTreeNode cmTreeNode : mbomlist) {
					cmLightPart = cmTreeNode.getPart();
					partOid = String.valueOf(cmLightPart.getOid());
					if (tempInfoMap.containsKey(partOid)) {
						/**批次*/
						cmLightPart.setBatch(tempInfoMap.get(partOid).attributeValue("batch"));
						/**零组件生产类型*/
						cmLightPart.setMtype(tempInfoMap.get(partOid).attributeValue("mtype"));
						/**主制车间*/
						cmLightPart.setZzcj(tempInfoMap.get(partOid).attributeValue("zplant"));
						/**辅制车间*/
						String fzbm = tempInfoMap.get(partOid).attributeValue("fplant");
						cmLightPart.setFzcj(fzbm);
						String[] plants = LoadConfig.getInstance().getMainPlant();
						Map<String, Boolean> plantMap = new HashMap<String, Boolean>();
						for (int i = 0; i < plants.length; i++) {
							if (null != fzbm && !"".equals(fzbm)) {
								String[] fzbms = fzbm.split("-");
								if (fzbms != null) {
									for (int j = 0; j < fzbms.length; j++) {
										if (plants[i].equals(fzbms[j])) {
											plantMap.put(plants[i], true);
											break;
										}
									}
								}
							} else {
								plantMap.put(plants[i], false);
							}
						}
						cmLightPart.setSecondePlant(plantMap);
						/**关重件标识*/
						cmLightPart.setKeycomponent(tempInfoMap.get(partOid).attributeValue("keycomponent"));
						/**产品代号*/
						cmLightPart.setPindex(tempInfoMap.get(partOid).attributeValue("pindex"));
						/**图号*/
						cmLightPart.setCindex(tempInfoMap.get(partOid).attributeValue("cindex"));
						/**型号代号*/
						cmLightPart.setMindex(tempInfoMap.get(partOid).attributeValue("mindex"));
						/**阶段标记*/
						cmLightPart.setPhase_code(tempInfoMap.get(partOid).attributeValue("phase_code"));
						/**是否编辑*/
						cmLightPart.setEdit(Boolean.valueOf(tempInfoMap.get(partOid).attributeValue("isEdit")));
					}
				}
				mtree.updateUI();
			}
		}
	}

	private static Map<String, Element> getTempInfo(File tempFile) {
		Map<String, Element> partsMap = new HashMap<String, Element>();
		SAXReader saxReader;
		String partOid;
		try {
			saxReader = new SAXReader();
			Document document = saxReader.read(tempFile);
			Element rootElement = document.getRootElement();
			List<Element> partsElement = rootElement.elements("PbomPart");
			for (Element part : partsElement) {
				partOid = part.attributeValue("partOid");
				partsMap.put(partOid, part);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return partsMap;
	}

	public static String getModifiedTime(File file) {
		Calendar cal = Calendar.getInstance();
		long time = file.lastModified();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		cal.setTimeInMillis(time);
		return formatter.format(cal.getTime());
	}

	private static void checkLicense() {
		try {
			int rt = new LicenseHelper().checkLicense(System.getProperty("user.dir"));
			switch (rt) {
			case LicenseHelper.EXPIRY:
				JOptionPane.showMessageDialog(null, "License已经过期。", "提示", 1);
				System.exit(0);
			case LicenseHelper.LICENSE_NOT_EXISTS:
				JOptionPane.showMessageDialog(null, "License不存在。", "提示", 1);
				System.exit(0);
			case LicenseHelper.REG_FAIL:
				JOptionPane.showMessageDialog(null, "License注册失败。", "提示", 1);
				System.exit(0);
			case LicenseHelper.REG_SUCCESS:
				break;
			case LicenseHelper.WRONG_HARDWARE:
				JOptionPane.showMessageDialog(null, "硬件验证错误。", "提示", 1);
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "License注册失败。", "提示", 1);
			System.exit(0);
		}
	}

	private String checkInput(String poid) throws WTRuntimeException, WTException {
		log.debug("=====ida2a2=====" + poid);
		String ida2a2 = poid;
		if (null == ida2a2 || "".equals(ida2a2.trim()) || "0".equals(ida2a2.trim())) {
			ida2a2 = JOptionPane.showInputDialog(this, "请输入整件编号");
		}
		if (null == ida2a2) {
			if (CmContext.isJWS())
				JWSUtil.shutdown();
			System.exit(0);
		} else if ("".equals(ida2a2.trim())) {
			JOptionPane.showMessageDialog(CmContext.getMainFrame(), "输入不能为空！");
			checkInput(ida2a2);
		} else {
			WTPart part;
			try {
				startAnimFrame.setHeaderMessage("查找part");
				RemoteMethodServer rms = RemoteMethodServer.getDefault();
				rms.setUserName("wcadmin");
				rms.setPassword("Admin@149");
				part = (WTPart) CmSearchHelper.search(WTPart.class, Long.valueOf(ida2a2));
				startAnimFrame.setHeaderMessage("完成查找part");
				planningOid = PBOMEditorToWCIntf.getViewByName(LoadConfig.getInstance().getPbomView());
				designOid = PBOMEditorToWCIntf.getViewByName("Design");
				if (part == null) {
					JOptionPane.showMessageDialog(CmContext.getMainFrame(), "找不到该整件！");
					ida2a2 = "0";
					checkInput(ida2a2);
				} else {
					String view = part.getViewName();
					if (LoadConfig.getInstance().getPbomView().equals(view)) {
						isPlanningView = true;
						CmConnectFrame.view = LoadConfig.getInstance().getPbomView();
					} else {
						isPlanningView = false;
						partOid = ida2a2;
					}

					if (isPlanningView) {
						WTPartMaster master = (WTPartMaster) part.getMaster();
						WTPart newPart = WTPartUtil.getLatestDesinPartByMaster(master);
						//WTPart newPart = WTPartUtil.getDesinPart(part);
						if (newPart != null) {
							log.info("old:partOid=" + ida2a2);
							partOid = String.valueOf(PersistenceHelper.getObjectIdentifier(newPart).getId());
							isPlanningView = false;
							CmConnectFrame.view = "Design";
							log.info("new:partOid=" + partOid);
						}
					}

					productName = part.getContainer().getName();
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ida2a2;
		}
		return "";
	}

	@SuppressWarnings("static-access")
	public CmConnectFrame(String partOid) {
		super("登录");
		try {
			startAnimFrame.setHeaderMessage("检验参数PartOid=" + partOid);
			checkInput(partOid);
			startAnimFrame.setHeaderMessage("完成检验参数PartOid");
		} catch (WTRuntimeException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(CmContext.getMainFrame(), "输入异常！");
			this.dispose();
		} catch (WTException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(CmContext.getMainFrame(), "输入异常！");
			this.dispose();
		}
		log.debug("partOid=" + this.partOid);
		startApplication("MBOM");
	}

	public void actionPerformed(ActionEvent e) {

	}

	private static void startApplication(String application) {
		if ("MBOM".equalsIgnoreCase(application)) {
			startAnimFrame.setHeaderMessage("启动PBOM程序");
			CmMBomMainFrame.getMainFrame().setVisible(true);
			CmContext.setMainFrame(CmMBomMainFrame.getMainFrame());
			startAnimFrame.setHeaderMessage("完成启动PBOM程序");
		}
	}
}
