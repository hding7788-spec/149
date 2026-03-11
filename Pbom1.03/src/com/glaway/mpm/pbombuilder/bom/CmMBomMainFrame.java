package com.glaway.mpm.pbombuilder.bom;

import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.action.CmActionProgressBar;
import com.glaway.mpm.pbombuilder.action.CmCommonPackageAction;
import com.glaway.mpm.pbombuilder.data.CmTreeNodeComparator;
import com.glaway.mpm.pbombuilder.data.CmXmlDataProxy;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.gui.CmTheme;
import com.glaway.mpm.pbombuilder.jws.CmContext;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.panel.CmMBomMainClientSplitPane;
import com.glaway.mpm.pbombuilder.panel.CmMBomRightClientSplitPane;
import com.glaway.mpm.pbombuilder.panel.CmPViewScenesPanel;
import com.glaway.mpm.pbombuilder.pview.CmPViewFactory;
import com.glaway.mpm.pbombuilder.pview.CmPViewImpl;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmScrollPaneTree;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.PbomTreeUpdateAction;
import com.glaway.mpm.pbombuilder.tree.dialog.BomTreeReportDialog;
import com.glaway.mpm.pbombuilder.tree.dialog.PbomTreeEditReportDialog;
import com.glaway.mpm.pbombuilder.tree.dialog.SetPbomAttributeDialog;
import com.glaway.mpm.pbombuilder.util.*;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;
import com.ptc.jws.JWSUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import wt.part.WTPart;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.List;
import java.util.*;

/**
 * <br>
 * Created on 2012-10-16
 *
 * @author chenyunlong
 */
public class CmMBomMainFrame extends CmAbstractFrame {
	private static final long serialVersionUID = -1922512384439459802L;
	private static final CmLogger log = CmLogger.getLogger(CmMBomMainFrame.class.getName());
	public static final String NODE = "pbom";
	private static CmMBomMainFrame mainFrame = null;
	private static Object lock = new Object();
	// Action components -> start
	private CmAction actSave;
	private CmAction actExit;
	// Action components -> en
	// UI components -> start, from UP to DOWN
	private JButton btnSave;

	private CmAction actOnlySave;
	private JButton onlySave;
	private CmAction actTempSave;
	private JButton tempSave;
	// private JButton btnExit;
	// private JButton btnToggleInstanceOrNumber; // 切换按实例/数量显示

	private CmMBomMainClientSplitPane splitMainClient; // 主界面
	private CmMBomRightClientSplitPane splitRightClient; // 右侧EBOM及MBOM树
	private JLabel labelStatus; // 底部状态栏
	public static JTextArea pbomLable;
	private boolean pviewInitialized;
	private JPanel labelPanel;
	private CmLightPart viewObject;
	private Boolean isClose = true;
	// UI components -> end

	private boolean isEdit = true;
	private static String useroid;
	private List<CmTreeNode> newplaningnodelist = new ArrayList<CmTreeNode>();

	public static CmMBomMainFrame getMainFrame() {
		if (mainFrame == null) {
			synchronized (lock) {
				if (mainFrame == null) {
					mainFrame = new CmMBomMainFrame();
				}
			}
		}
		return mainFrame;
	}

	private CmMBomMainFrame() {
		super();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.isEdit = CmContext.isEdit();
		setFrameTitle();
		try {
			this.initUI();
		} catch (CmTaskException e) {
			log.error(e);
		}
		CmXmlDataProxy proxy = CmXmlDataProxy.getCmXmlDataProxy();
		proxy.registerStructureProxy(CmXmlDataProxy.MBOM_STRUCTURE_PROXY);
		proxy.registerAttributProxy(CmXmlDataProxy.MBOM_ATTRIBUTE_PROXY);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				CmTree mtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree();
				CmTreeNode root = mtree.getRoot();
				if (root.children().hasMoreElements()) {
					List<List<Object>> updateNodesList = dealWithAllChangeNodes(root);
					if (null != updateNodesList && updateNodesList.size() > 0) {
						int option = JOptionPane.showConfirmDialog(mtree.getRootPane(), "是否保存?", "提示", JOptionPane.YES_NO_CANCEL_OPTION);
						// 0 是 1 否 2 取消
						if (option == 0) {
							actSave.actionPerformed(null);
							if (!isClose) {
								mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
							}
						} else if (option == 1) {
							closeBOMWindow();
						} else if (option == 2) {
							mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
						}
					} else {
						closeBOMWindow();
					}
				}
			}
		});
	}

	private void setFrameTitle() {
		this.setIconImage(CmUtil.getImageFromServer("mbom_edit.png"));
		if (this.isEdit) {
			this.setTitle("PBOM编辑器");
		} else {
			this.setTitle("PBOM查看器");
		}
	}

	/**
	 * 初始化窗口
	 */
	protected void initDimension() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int width = screen.width > 1200 ? 1100 : 800;
		int height = 700;
		String maximized = "";

		// try {
		// width = Integer.parseInt((CmPreferences.getOldStylePreference(NODE,
		// "MAIN_WIDTH", String.valueOf(width))));
		// height = Integer.parseInt(CmPreferences.getOldStylePreference(NODE,
		// "MAIN_HEIGHT", String.valueOf(height)));
		// maximized = CmPreferences.getOldStylePreference(NODE,
		// "MAIN_MAXIMIZED", "");
		// } catch (Exception e) {
		// log.error(e);
		// }

		if (maximized.equals("MAIN_MAXIMIZED")) {
			setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
			setBounds(0, 0, screen.width, screen.height);
		} else {
			int ancleft = (screen.width - width) / 2;
			int anctop = (screen.height - height) / 2;

			setBounds(ancleft, anctop, width, height);
		}
	}

	/**
	 * 初始化保存、退出动作
	 */
	protected void initActions() {
		ImageIcon saveImage = new ImageIcon(CmUtil.getImageFromServer("save.gif"));
		actSave = new CmMBomSaveAction("保存并关闭", saveImage);
		ImageIcon exitImage = new ImageIcon(CmUtil.getImageFromServer("save.gif"));
		actOnlySave = new CmMBomOnlySaveAction("保存", exitImage);
		ImageIcon tempSaveImage = new ImageIcon(CmUtil.getImageFromServer("save.gif"));
		actTempSave = new CmMBomTempSaveAction("临时保存", tempSaveImage);
		// ImageIcon exitImage = new
		// ImageIcon(CmUtil.getImageFromServer("loginhover.png"));
		// actExit = new CmMBomExitAction("提交", exitImage);
	}

	protected void initComponents() {
		btnSave = new JButton(actSave);
		btnSave.setBackground(CmTheme.CM_TURQUOISE);
		btnSave.setForeground(Color.WHITE);

		onlySave = new JButton(actOnlySave);
		onlySave.setBackground(CmTheme.CM_TURQUOISE);
		onlySave.setForeground(Color.WHITE);

		tempSave = new JButton(actTempSave);
		tempSave.setBackground(CmTheme.CM_TURQUOISE);
		tempSave.setForeground(Color.WHITE);

		// btnExit = new JButton(actExit);
		// btnExit.setBackground(CmTheme.CM_TURQUOISE);
		// btnExit.setForeground(Color.WHITE);

		splitRightClient = new CmMBomRightClientSplitPane(this);
		splitMainClient = new CmMBomMainClientSplitPane(this);
		splitMainClient.setRightComponent(splitRightClient);

		labelPanel = new JPanel();
		labelPanel.setBorder(new EtchedBorder());
		labelPanel.setLayout(new BorderLayout());
		JPanel panel = buildToolBar();
		panel.setBorder(new EtchedBorder());
		labelPanel.add(panel, BorderLayout.EAST);
		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.RIGHT);
		panel.setLayout(flow);
		pbomLable = new JTextArea("");
		pbomLable.setRows(6);
		pbomLable.setLineWrap(true);
		pbomLable.setEditable(false);
		labelPanel.add(pbomLable, BorderLayout.CENTER);

		labelStatus = new JLabel("已就绪");
		labelStatus.setBorder(new EtchedBorder());
	}

	protected void initLayout() {
		getContentPane().add(labelPanel, BorderLayout.NORTH);
		getContentPane().add(splitMainClient, BorderLayout.CENTER);
		getContentPane().add(labelStatus, BorderLayout.SOUTH);
		initDividerLocation();
	}

	private void initDividerLocation() {
		int divPanelMain = -1;
		int divPanelRight = -1;
		try {
			// divPanelMain =
			// Integer.parseInt(CmPreferences.getOldStylePreference(NODE,
			// "DIV_PANEL_MAIN", String.valueOf(divPanelMain)));
			// divPanelRight =
			// Integer.parseInt(CmPreferences.getOldStylePreference(NODE,
			// "DIV_PANEL_RIGHT", String.valueOf(divPanelRight)));

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (divPanelMain >= 0 && divPanelRight >= 0) {
			splitMainClient.setDividerLocation(divPanelMain);
			splitRightClient.setDividerLocation(divPanelRight);
		} else {
			splitRightClient.setDividerLocation(1d);
		}
	}

	protected void loadInitDatas() {

	}

	protected void registerTaskExecutor() throws CmTaskException {
		PviewTask.registerTaskExecutor("mainframe.setStatus", this);
		PviewTask.registerTaskExecutor("mainframe.saveBeforeLoad", this);
	}

	public boolean isEdit() {
		return this.isEdit;
	}

	public CmLightPart getViewObject() {
		if (this.viewObject == null) {
			// WTContext context = WTContext.getContext();
			// String oidKey = context.getParameter("partOidKey");
			// String number = context.getParameter("partNumber");
			// String version = context.getParameter("partVersion");
			// WTPart part = null;
			// if (oidKey != null) {
			// long oid = Long.parseLong(oidKey);
			// part = (WTPart) CmSearchHelper.search(WTPart.class, oid);
			// } else if (number != null && version != null) {
			// try {
			// part = (WTPart) CmBizObjHelper.findPart(number, version);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }
			// if (part != null)
			// this.viewObject = CmBizObjUtil.buildCmLightPartFromWTPart(part);
		}
		return this.viewObject;
	}

	public void closeWindow() {
		try {
			PviewTask.unregisterTaskExecutor();
			this.saveClientStylePrefs();
			this.clearTempDir();
			CmTreeNode ebomfirstNode = (CmTreeNode) CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree().getRoot().children().nextElement();
			// 关闭参装工具窗口时，删除下载的图形文件
			String folderPath = System.getProperty("java.io.tmpdir") + "\\" + ebomfirstNode.getPart().getOid();
			File olFolder = new File(folderPath);
			if (olFolder.exists() && olFolder.isDirectory()) {
				File[] files = olFolder.listFiles();
				for (int i = 0; i < files.length; i++) {
					files[i].delete();
				}
				olFolder.delete();
			}
		} catch (Throwable tt) {
			log.error(tt);
		}

		this.dispose();
		mainFrame = null;
	}

	private void saveClientStylePrefs() {
		Properties props = new Properties();

		if ((CmMBomMainFrame.getMainFrame().getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH)
			props.setProperty("MAIN_MAXIMIZED", "MAIN_MAXIMIZED");
		else
			props.setProperty("MAIN_MAXIMIZED", "MAIN_NOT_MAXIMIZED");

		props.setProperty("MAIN_WIDTH", String.valueOf(getWidth()));
		props.setProperty("MAIN_HEIGHT", String.valueOf(getHeight()));

		int divPanelMain = CmMBomMainFrame.getMainFrame().getSplitMainClient().getDividerLocation();
		int divPanelRight = CmMBomMainFrame.getMainFrame().getSplitRightClient().getDividerLocation();
		props.setProperty("DIV_PANEL_MAIN", String.valueOf(divPanelMain));
		props.setProperty("DIV_PANEL_RIGHT", String.valueOf(divPanelRight));

		// try {
		// CmPreferences.setOldStylePreferences(NODE, props);
		// } catch (WTException e) {
		// log.error(e);
		// }
	}

	private void clearTempDir() {
		try {
			// Clean up of temp dir
			File tdir = CmContext.getTempDir();
			if (tdir != null) {
				File[] fcont = tdir.listFiles();
				for (int i = 0; fcont != null && i < fcont.length; i++)
					fcont[i].delete();
				tdir.delete();
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void unregisterTaskExecutor() throws CmTaskException {
		PviewTask.unregisterTaskExecutor(this);
	}

	private JPanel buildToolBar() {
		JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.CENTER));

		toolBar.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.WHITE));
		// toolBar.setBackground(CmTheme.CM_BLUE);

		if (isEdit) {
			// toolBar.add(onlySave);
			toolBar.add(tempSave);
			toolBar.add(btnSave);
		}
		// toolBar.add(btnExit);
		return toolBar;
	}

	public synchronized void setStatus(Object sender, Object param, CmTaskExecutorCallback callback) {
		StringBuffer buf = new StringBuffer(" PBOM: ");
		if (param instanceof Object[]) {
			Object[] params = (Object[]) param;
			for (Object obj : params)
				buf.append(String.valueOf(obj));
		} else
			buf.append(String.valueOf(param));
		labelStatus.setText(buf.toString());
	}

	/**
	 * 当有新的AO组件需要加载的MBOM树上的时候,保存当前正在编辑的AO组件
	 */
	public void saveBeforeLoad(Object sender, Object param, CmTaskExecutorCallback callback) {
		btnSave.doClick();
	}

	public CmMBomMainClientSplitPane getSplitMainClient() {
		return splitMainClient;
	}

	public CmMBomRightClientSplitPane getSplitRightClient() {
		return splitRightClient;
	}

	public CmPViewScenesPanel getProductViewScenesPanel() {
		return getSplitMainClient().getPViewScenesPanel();
	}

	public static void main(String[] args) {
		// RemoteMethodServer.getDefault().setUserName("wcadmin");
		// RemoteMethodServer.getDefault().setPassword("wcadmin");
		getMainFrame().setVisible(true);
	}

	// inner class
	class CmMBomSaveAction extends CmAction {
		private static final long serialVersionUID = 1377871349230695953L;

		public CmMBomSaveAction(String name, ImageIcon icon) {
			super(icon);
			setLabel(name);
		}

		public void actionPerformed(ActionEvent evt) {
			CmCommonNodeUtil common = new CmCommonNodeUtil();
			CmTree ebomtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree();
			// common.clearBomSearchResult(ebomtree.getRoot());
			List<CmTreeNode> ebomlist = CmCommonStringUtil.getBomNodeList(ebomtree);
			boolean flag = false;
			boolean isChangeEbomXmlFlag = false;
			for (CmTreeNode ebom : ebomlist) {
				if (ebom.getPart().getEchangeIndex() == 1 || ebom.getPart().getEchangeIndex() == 2 || ebom.getPart().getEchangeIndex() == 3) {
					flag = true;
					break;
				}
			}
			CmTree mtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree();
			List<CmTreeNode> mbomlist = CmCommonStringUtil.getBomNodeList(mtree);
			CmTreeNode ppart = mtree.getRoot();
			Enumeration nodes = ppart.children();
			if (nodes.hasMoreElements()) {
				CmTreeNode rootPart = (CmTreeNode) nodes.nextElement();
				List<String> hasPassNumbers = new ArrayList<String>();

				for (CmTreeNode node : mbomlist) {
					if (rootPart.getPart().getContainerId() != node.getPart().getContainerId()) {
						continue;
					}

					if (hasPassNumbers.contains(node.getPart().getPartNumber())) {
						continue;
					}
					String mtype = node.getPart().getMtype();
					if (isNull(mtype)) {
						JOptionPane.showMessageDialog(mtree.getRootPane(), node.getPart().getPartNumber() + "的零组件生产类型必须填写值");
						return;
					}

					if ("自制件".equals(mtype) || "外配套件".equals(mtype) || "带料委外件".equals(mtype) || "不带料委外件".equals(mtype)) {
						String zzcj = node.getPart().getZzcj();
						if (isNull(zzcj)) {
							JOptionPane.showMessageDialog(mtree.getRootPane(), node.getPart().getPartNumber() + "的主制车间必须填写值");
							return;
						}

						String pindex = node.getPart().getPindex();
						if (isNull(pindex)) {
							JOptionPane.showMessageDialog(mtree.getRootPane(), node.getPart().getPartNumber() + "的产品代号必须填写值");
							return;
						}

						String cindex = node.getPart().getCindex();
						if (isNull(cindex)) {
							JOptionPane.showMessageDialog(mtree.getRootPane(), node.getPart().getPartNumber() + "的图号必须填写值");
							return;
						}

						String mindex = node.getPart().getMindex();
						if (isNull(mindex)) {
							JOptionPane.showMessageDialog(mtree.getRootPane(), node.getPart().getPartNumber() + "的型号代号必须填写值");
							return;
						}

						String Phase_code = node.getPart().getPhase_code();
						if (isNull(Phase_code)) {
							JOptionPane.showMessageDialog(mtree.getRootPane(), node.getPart().getPartNumber() + "的阶段标记必须填写值");
							return;
						}

						String setmark = node.getPart().getSetmark();
						String state = node.getPart().getState();
						if (isNull(setmark) && "正在工作".equals(state)) {
							JOptionPane.showMessageDialog(mtree.getRootPane(), node.getPart().getPartNumber() + "的成套件标识必须填写值");
							return;
						}

						String adjustable = node.getPart().getAdjustable();
						if (isNull(adjustable) && "正在工作".equals(state)) {
							JOptionPane.showMessageDialog(mtree.getRootPane(), node.getPart().getPartNumber() + "的可调整必须填写值");
							return;
						}

						String keycomponent = node.getPart().getKeycomponent();
						if (isNull(keycomponent)) {
							JOptionPane.showMessageDialog(mtree.getRootPane(), node.getPart().getPartNumber() + "的关重件标识必须填写值");
							return;
						}
					}
					hasPassNumbers.add(node.getPart().getPartNumber());
				}
			}

			// common.clearBomSearchResult(mtree.getRoot());
			CmCommonPackageAction action = new CmCommonPackageAction();
			List<CmTreeNode> pbomlist = CmCommonStringUtil.getBomNodeList(mtree);
			if (flag) {
				isChangeEbomXmlFlag = true;
				StringBuffer oldpbom = new StringBuffer(1024);
				StringBuffer delebom = new StringBuffer(1024);
				StringBuffer newebom = new StringBuffer(1024);
				StringBuffer updateversionebom = new StringBuffer(1024);
				StringBuffer changeebom = new StringBuffer(1024);
				if (PbomTreeUpdateAction.isUpdate) {
					flag = false;
					JOptionPane.showMessageDialog(mtree.getRootPane(), "PBOM中有节点需要升版本，请点击同步EBOM按钮！");
				}
				// else
				// if(getPbomOldNode(ebomlist,pbomlist,oldpbom).toString().length()>0){
				// updateTreeUI(mtree);
				// flag = false;
				// JOptionPane.showMessageDialog(mtree.getRootPane(),
				// oldpbom.toString());
				// }
				// else
				// if(getEbomDeleteNode(ebomlist,pbomlist,delebom).toString().length()>0){
				// flag = false;
				// updateTreeUI(ebomtree);
				// JOptionPane.showMessageDialog(mtree.getRootPane(),
				// delebom.toString());
				// }else
				// if(getEbomNewNode(ebomlist,pbomlist,newebom).toString().length()>0){
				// flag = false;
				// JOptionPane.showMessageDialog(mtree.getRootPane(),
				// newebom.toString());
				// }else
				// if(getEbomUpdateVersionNode(ebomlist,pbomlist,updateversionebom).toString().length()>0){
				// flag = false;
				// updateTreeUI(ebomtree);
				// JOptionPane.showMessageDialog(mtree.getRootPane(),
				// updateversionebom.toString());
				// }else
				// if(getEbomChangeNode(ebomlist,pbomlist,changeebom).toString().length()>0){
				// flag = false;
				// updateTreeUI(ebomtree);
				// JOptionPane.showMessageDialog(mtree.getRootPane(),
				// changeebom.toString());
				// }
			} else {
				flag = true;
				ebomtree = null;
				ebomlist = null;
			}
			if (flag) {
				StringBuffer middlebuf = new StringBuffer(1024);
				StringBuffer packagebuf = new StringBuffer(1024);
				StringBuffer linkbuf = new StringBuffer(1024);

				if (action.checkThePackagePbomTree(mtree.getRoot(), mtree.getRoot(), packagebuf, new ArrayList<CmTreeNode>(), "PBOM").toString().length() > 0) {
					isClose = false;
					JOptionPane.showMessageDialog(mtree.getRootPane(), packagebuf);
				} else {
					boolean isSave = true;

					getMiddleNodeWithNoChildNode(mtree.getRoot(), middlebuf);
					if (middlebuf.toString().length() > 0) {
						isClose = false;
						// JOptionPane.showMessageDialog(mtree.getRootPane(),
						// middlebuf);
						int option = JOptionPane.showConfirmDialog(mtree.getRootPane(), middlebuf, "提示", JOptionPane.YES_OPTION);
						if (option == 1) {
							isSave = false;
							isClose = false;
						}
					}

					// if(getPbomNodeWithoutPositionId(pbomlist,linkbuf).toString().length()>0){
					// int option =
					// JOptionPane.showConfirmDialog(mtree.getRootPane(),
					// linkbuf, "提示", JOptionPane.YES_OPTION);
					// // 0 是 1 否
					// if(option==1){
					// updateTreeUI(mtree);
					// isSave = false;
					// isClose = false;
					// }
					// }
					if (isSave) {
						// 点击提交关闭窗口
						final CmActionProgressBar animFrame = new CmActionProgressBar(this, CmMBomMainFrame.getMainFrame(), "保存数据", "正在保存PBOM数据", "正在保存PBOM数据，请等待...");
						savePbomTree(animFrame, mtree, isChangeEbomXmlFlag);
						/**删除本地临时文件 start*/
						Enumeration pbom = mtree.getRoot().children();
						while (pbom.hasMoreElements()) {
							CmTreeNode parentNode = (CmTreeNode) pbom.nextElement();
							String rootPartNumber = parentNode.getPart().getPartNumber();
							rootPartNumber = rootPartNumber.replace("/", "_");
							String fileDirPath = System.getProperty("user.home") + File.separator + "pbomTemp";
							File fileDir = new File(fileDirPath);
							if (fileDir.exists() && fileDir.isDirectory()) {
								File[] files = fileDir.listFiles();
								for(File file : files){
									if(file.getName().contains(rootPartNumber)){
										file.delete();
									}
								}
					        }
//							String filePath = System.getProperty("user.home") + File.separator + "pbomTemp" + File.separator + rootPartNumber + "-pbom_bak.xml";
//							File file = new File(filePath);
//							if(file.exists()){
//								file.delete();
//							}
						}
						/**删除本地临时文件 end*/
					}
				}
			} else {
				isClose = false;
			}
		}

	}

	/**
	 * 临时保存按钮
	 * @author LB
	 *
	 */
	class CmMBomTempSaveAction extends CmAction {
		private static final long serialVersionUID = 1377871349230695953L;

		public CmMBomTempSaveAction(String name, ImageIcon icon) {
			super(icon);
			setLabel(name);
		}

		public void actionPerformed(ActionEvent evt) {
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
				JOptionPane.showMessageDialog(mtree.getRootPane(), "保存失败");
				return;
			}
			List<CmTreeNode> mbomlist = CmCommonStringUtil.getBomNodeList(mtree);
			CmLightPart cmLightPart;
			Map<String, CmLightPart> cmLightPartMap = new HashMap<String, CmLightPart>();
			for (CmTreeNode cmTreeNode : mbomlist) {
				cmLightPart = cmTreeNode.getPart();
				if (containerId.equals(String.valueOf(cmLightPart.getContainerId())) && !cmLightPartMap.containsKey(cmLightPart.getPartNumber())) {
					cmLightPartMap.put(cmLightPart.getPartNumber(), cmLightPart);
				}
			}
			File file = tempSavePbomInfo(rootPartNumber, rootPartVersion, cmLightPartMap);
			if (file.exists()) {
				JOptionPane.showMessageDialog(mtree.getRootPane(), "保存成功");
			}
		}
	}


	/**
	 * 将PBOM信息保存在本地
	 * @param rootPartNumber
	 * @param cmLightPartMap
	 * @return
	 */
	public static File tempSavePbomInfo(String rootPartNumber,String rootPartVersion, Map<String, CmLightPart> cmLightPartMap) {
		String fileDir = System.getProperty("user.home") + File.separator + "pbomTemp";
		File file = new File(fileDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		rootPartNumber = rootPartNumber.replace("/", "_");
		rootPartVersion = rootPartVersion.replace(".", "_");
		String filePath = fileDir + File.separator + rootPartNumber + "_" +rootPartVersion + "-pbom_bak.xml";
		XMLWriter writer = null;
		try {
			Document document = DocumentHelper.createDocument();
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			Element root = document.addElement("PbomParts");
			CmLightPart cmLightPart;
			for (Map.Entry<String, CmLightPart> entry : cmLightPartMap.entrySet()) {
				cmLightPart = entry.getValue();
				Element pbomPart = root.addElement("PbomPart");
				/**部件OID*/
				pbomPart.addAttribute("partOid", String.valueOf(cmLightPart.getOid()));
				/**部件编号*/
				pbomPart.addAttribute("partNumber", cmLightPart.getPartNumber());
				/**部件版本*/
				pbomPart.addAttribute("partVersion", cmLightPart.getVersion());
				/**批次*/
				pbomPart.addAttribute("batch", cmLightPart.getBatch());
				/**零组件生产类型*/
				pbomPart.addAttribute("mtype", cmLightPart.getMtype());
				/**主制车间*/
				pbomPart.addAttribute("zplant", cmLightPart.getZzcj());
				/**辅制车间*/
				pbomPart.addAttribute("fplant", cmLightPart.getFzcj());
				/**关重件标识*/
				pbomPart.addAttribute("keycomponent", cmLightPart.getKeycomponent());
				/**图号*/
				pbomPart.addAttribute("pindex", cmLightPart.getPindex());
				/**产品代号*/
				pbomPart.addAttribute("cindex", cmLightPart.getCindex());
				/**型号代号*/
				pbomPart.addAttribute("mindex", cmLightPart.getMindex());
				/**阶段标记*/
				pbomPart.addAttribute("phase_code", cmLightPart.getPhase_code());
				/**是否成套件*/
				pbomPart.addAttribute("SETMARK", cmLightPart.getSetmark());
				/**可调整*/
				pbomPart.addAttribute("ADJUSTABLE", cmLightPart.getAdjustable());
				/**是否编辑*/
				pbomPart.addAttribute("isEdit", String.valueOf(cmLightPart.isEdit()));
			}
			writer = new XMLWriter(new FileOutputStream(filePath), format);
			writer.write(document);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new File(filePath);
	}

	class CmMBomOnlySaveAction extends CmAction {
		private static final long serialVersionUID = 1377871349230695953L;

		public CmMBomOnlySaveAction(String name, ImageIcon icon) {
			super(icon);
			setLabel(name);
		}

		public void actionPerformed(ActionEvent evt) {
			CmCommonNodeUtil common = new CmCommonNodeUtil();
			CmTree ebomtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree();
			common.clearBomSearchResult(ebomtree.getRoot());
			List<CmTreeNode> ebomlist = CmCommonStringUtil.getBomNodeList(ebomtree);
			boolean flag = false;
			boolean isChangeEbomXmlFlag = false;
			for (CmTreeNode ebom : ebomlist) {
				if (ebom.getPart().getEchangeIndex() == 1 || ebom.getPart().getEchangeIndex() == 2 || ebom.getPart().getEchangeIndex() == 3) {
					flag = true;
					break;
				}
			}
			CmTree mtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree();
			Enumeration e = mtree.getRoot().children();
			int i = 0;
			boolean isSave1 = true;
			while (e.hasMoreElements()) {
				i++;
				CmTreeNode nd = (CmTreeNode) e.nextElement();

				if (i == 1 && nd.isLeaf()) {
					isSave1 = false;
				}

				if (i > 1) {
					isSave1 = true;
					break;
				}
			}

			if (!isSave1)
				return;

			updateAtrribute(mtree.getRoot());
			common.clearBomSearchResult(mtree.getRoot());
			CmCommonPackageAction action = new CmCommonPackageAction();
			List<CmTreeNode> pbomlist = CmCommonStringUtil.getBomNodeList(mtree);
			if (flag) {
				isChangeEbomXmlFlag = true;
				StringBuffer oldpbom = new StringBuffer(1024);
				StringBuffer delebom = new StringBuffer(1024);
				StringBuffer newebom = new StringBuffer(1024);
				StringBuffer updateversionebom = new StringBuffer(1024);
				StringBuffer changeebom = new StringBuffer(1024);
				if (PbomTreeUpdateAction.isUpdate) {
					flag = false;
					JOptionPane.showMessageDialog(mtree.getRootPane(), "PBOM中有节点需要升版本，请点击同步EBOM按钮！");
				}
				// else
				// if(getPbomOldNode(ebomlist,pbomlist,oldpbom).toString().length()>0){
				// updateTreeUI(mtree);
				// flag = false;
				// JOptionPane.showMessageDialog(mtree.getRootPane(),
				// oldpbom.toString());
				// }else
				// if(getEbomDeleteNode(ebomlist,pbomlist,delebom).toString().length()>0){
				// flag = false;
				// updateTreeUI(ebomtree);
				// JOptionPane.showMessageDialog(mtree.getRootPane(),
				// delebom.toString());
				// }else
				// if(getEbomNewNode(ebomlist,pbomlist,newebom).toString().length()>0){
				// flag = false;
				// JOptionPane.showMessageDialog(mtree.getRootPane(),
				// newebom.toString());
				// }else
				// if(getEbomUpdateVersionNode(ebomlist,pbomlist,updateversionebom).toString().length()>0){
				// flag = false;
				// updateTreeUI(ebomtree);
				// JOptionPane.showMessageDialog(mtree.getRootPane(),
				// updateversionebom.toString());
				// }else
				// if(getEbomChangeNode(ebomlist,pbomlist,changeebom).toString().length()>0){
				// flag = false;
				// updateTreeUI(ebomtree);
				// JOptionPane.showMessageDialog(mtree.getRootPane(),
				// changeebom.toString());
				// }
			} else {
				flag = true;
				ebomtree = null;
				ebomlist = null;
			}
			if (flag) {
				StringBuffer middlebuf = new StringBuffer(1024);
				StringBuffer packagebuf = new StringBuffer(1024);
				StringBuffer linkbuf = new StringBuffer(1024);
				getMiddleNodeWithNoChildNode(mtree.getRoot(), middlebuf);
				if (middlebuf.toString().length() > 0) {
					isClose = false;
					JOptionPane.showMessageDialog(mtree.getRootPane(), middlebuf);
				} else if (action.checkThePackagePbomTree(mtree.getRoot(), mtree.getRoot(), packagebuf, new ArrayList<CmTreeNode>(), "PBOM").toString().length() > 0) {
					isClose = false;
					JOptionPane.showMessageDialog(mtree.getRootPane(), packagebuf);
				} else {
					boolean isSave = true;
					if (getPbomNodeWithoutPositionId(pbomlist, linkbuf).toString().length() > 0) {
						int option = JOptionPane.showConfirmDialog(mtree.getRootPane(), linkbuf, "提示", JOptionPane.YES_OPTION);
						// 0 是 1 否
						if (option == 1) {
							updateTreeUI(mtree);
							isSave = false;
							isClose = false;
						}
					}
					if (isSave) {
						// 点击提交关闭窗口
						final CmActionProgressBar animFrame = new CmActionProgressBar(this, CmMBomMainFrame.getMainFrame(), "保存数据", "正在保存PBOM数据", "正在保存PBOM数据，请等待...");
						savePbomTree(animFrame, mtree, isChangeEbomXmlFlag);
					}
				}
			} else {
				isClose = false;
			}
		}
	}

	class CmMBomExitAction extends CmAction {
		private static final long serialVersionUID = 1377871349230695953L;

		public CmMBomExitAction(String name, ImageIcon exitImage) {
			super(exitImage);
			setLabel(name);
		}

		public void actionPerformed(ActionEvent evt) {
			if (null == evt) {
				// 直接关闭窗口
				closeBOMWindow();
			} else {

			}
		}
	}

	public void savePbomTree(final CmActionProgressBar animFrame, final CmTree mtree, final boolean isChangeEbomXmlFlag) {
		try {
			Thread saveMBom = new Thread() {
				public void run() {
					boolean flag = true;
					CmTreeNode root = mtree.getRoot();
					CmCommonPackageAction action = new CmCommonPackageAction();
					if (root.children().hasMoreElements()) {
						action.packageAllNode(mtree);
						List<Map<String, Object>> planninglist = new ArrayList<Map<String, Object>>();
						getUpdateVersionNode(mtree.getRoot(), newplaningnodelist, planninglist);// 获取升版本的零件
						Map<String, Object> newplanningMap = PBOMEditorToWCIntf.updatePartVersion(planninglist);
						if (null != newplanningMap.get("errorMessage")) {
							JOptionPane.showMessageDialog(mtree.getRootPane(), newplanningMap.get("errorMessage"));
							flag = false;
						} else {
							// updateNewPlanning(mtree.getRoot(),newplanningMap,CmScrollPaneTree.pbomlist);//更新升版本的planning的oid
							updateNewPlanning(mtree.getRoot(), newplanningMap, new ArrayList<CmTreeNode>(CmScrollPaneTree.pbomMap.values()));// 更新升版本的planning的oid
							if ("PE".equals(CmConnectFrame.from)) {
								useroid = PBOMEditorToWCIntf.getCurrentUserInfoRMI().get(1);
							}
							List<List<Object>> updateNodesList = dealWithAllChangeNodes(root);
							String str = PBOMEditorToWCIntf.pbomStructure(updateNodesList);// 实例化
							// String str = "success";
							if ("success".equals(str)) {
								upatetTreeUI(mtree);
								log.debug("开始获取数据");
								CmXmlUtil xml = new CmXmlUtil();
								saveModifyPartNumber(root, xml);
								// byte[] bytes = xml.saveTreeToBytes(root);
								// log.debug("节点数据转化成XML成功，开始提交服务器");
								// flag
								// =PBOMEditorToWCIntf.savePBOMXml(String.valueOf(((CmTreeNode)root.children().nextElement()).getPart().getOid()),
								// bytes);
								CmTreeNode firstNode = (CmTreeNode) root.children().nextElement();
								List<Map<String, Object>> byteslist = xml.saveTreeToBytes(firstNode);
								log.debug("节点数据转化成XML成功，开始提交服务器");
								log.debug("--------byteslist---" + byteslist);
								if (isNewVersion(mtree.getRoot())) {
									log.debug("PBOM修订了版本");
									for (Map<String, Object> map : byteslist) {
										flag = PBOMEditorToWCIntf.savePBOMXml2(String.valueOf(map.get("oid")), (byte[]) map.get("bytes"));
									}
								} else {
									log.debug("PBOM升小版本");
									for (Map<String, Object> map : byteslist) {
										flag = PBOMEditorToWCIntf.savePBOMXml(String.valueOf(map.get("oid")), (byte[]) map.get("bytes"));
									}
								}

								// PBOMEditorToWCIntf.pbomStructure2(byteslist);
								PBOMEditorToWCIntf.pbomStructure2(byteslist, CmScrollPaneTree.changeGysl);

								if (!flag) {
									JOptionPane.showMessageDialog(mtree.getRootPane(), "XML保存失败！");
								} else if (isChangeEbomXmlFlag || true) {
									log.debug("保存成功");
									WTPart ebomPart;
									try {
										ebomPart = (WTPart) CmSearchHelper.search(WTPart.class, Long.valueOf(CmConnectFrame.partOid));
										boolean isChange = PBOMEditorToWCIntf.saveEbomComparedFlageRMI(ebomPart);
										if (!isChange) {
											JOptionPane.showMessageDialog(mtree.getRootPane(), "EBOM不存在或修改EBOM的XML状态失败！暂不影响PBOM使用，可忽略该提示。");
										}
									} catch (NumberFormatException e) {
										e.printStackTrace();
									} catch (Exception e) {
										e.printStackTrace();
									}

								}
							} else if ("failed".equals(str)) {
								JOptionPane.showMessageDialog(mtree.getRootPane(), "PBOM实例化失败！");
								flag = false;
							}
						}
					} else {
						flag = false;
						JOptionPane.showMessageDialog(mtree.getRootPane(), "PBOM为空！");
					}
					animFrame.finish();
					animFrame.setVisible(false);
					if (flag) {
						closeBOMWindow();
					}
				}
			};
			saveMBom.start();
			animFrame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isNull(String zzcj) {
		if (zzcj == null) {
			return true;
		}
		if ("null".equals(zzcj)) {
			return true;
		}

		if ("".equals(zzcj)) {
			return true;
		}
		return false;

	}

	private void updateAtrribute(CmTreeNode node) {
		// CmCommonNodeUtil.updateNodeAtrribute(node);
		Enumeration root = node.children();
		while (root.hasMoreElements()) {
			CmTreeNode childe = (CmTreeNode) root.nextElement();
			updateAtrribute(childe);
		}
	}

	public void closeBOMWindow() {
		if (CmPViewImpl.isPviewInstalled() && CmPViewImpl.isPviewInitialized()) {
			CmPViewFactory.getPViewImpl4MBOM().shutdown();
			CmPViewImpl.getPviewInit().Stop();
		}
		BomTreeReportDialog.colseBomReport();
		PbomTreeEditReportDialog.colseBomReport();
		SetPbomAttributeDialog.colseEditPbomDialog();
		CmMBomMainFrame.getMainFrame().closeWindow();
		if (CmContext.isJWS())
			JWSUtil.shutdown();
		System.exit(0);
	}

	/**
	 * 得到所有改动过的节点：新增、删除、移动、属性修改、移动和属性修改
	 *
	 * @author chenyunlong
	 * @date 2013-3-20
	 * @param root
	 * @return
	 *
	 */
	public List<List<Object>> dealWithAllChangeNodes(CmTreeNode root) {
		// 获取所有的PBOM所有的节点，包括原有的和新增的节点，此处用处是遍历获取删除的节点信息
		// List<CmTreeNode> list = CmScrollPaneTree.pbomlist;
		// List<CmTreeNode> list = new
		// ArrayList<CmTreeNode>(CmScrollPaneTree.pbomMap.values());

		// 记录所有变化的节点信息
		List<List<Object>> updateNodesList = new ArrayList<List<Object>>();
		// 记录PBOM树上完全不同的节点信息 ,此处用处是----如果有多个相同的零件，节点属性有修改有且只修改一次
		List<CmTreeNode> onlyNodeList = new ArrayList<CmTreeNode>();
		updatePbomPlanning((CmTreeNode) root.children().nextElement(), null, onlyNodeList, updateNodesList);
		// 更新planning视图-----删除节点

		List<CmTreeNode> delList = getDeleteNode(root);

		System.out.println("----------------delete-------------");
		for (CmTreeNode cmnode : delList) {
			// if ("delete".equals(cmnode.getPart().getOperType())) {
			log.debug("删除的节点*****：" + cmnode.toString() + "===============" + cmnode.getPart().getOperType());
			log.debug("OldParentPartOid" + "==============" + getOldParentPartOid(cmnode));
			log.debug("oid" + "==============" + String.valueOf(cmnode.getPart().getOid()));

			List<Object> ulist = new ArrayList<Object>();
			ulist.add("delete");
			ulist.add(getOldParentPartOid(cmnode));
			ulist.add(String.valueOf(cmnode.getPart().getOid()));
			ulist.add(cmnode.getPart().getPartType());

			updateNodesList.add(ulist);

			// }
		}
		System.out.println("----------------delete-------------");

		return updateNodesList;
	}

	public List<CmTreeNode> getDeleteNode(CmTreeNode root) {
		List<CmTreeNode> delList = new ArrayList<CmTreeNode>();
		List<CmTreeNode> realDelList = new ArrayList<CmTreeNode>();
		HashMap<CmTreeNode, CmTreeNode> mMap = new HashMap<CmTreeNode, CmTreeNode>(); // MBOM修改后
																						// 结构
		HashMap<CmTreeNode, CmTreeNode> initMap = CmScrollPaneTree.pbomMap;
		CmCommonStringUtil.node2StructureMap(root, mMap);

		Set<CmTreeNode> set = mMap.keySet();
		for (CmTreeNode node : set) {
			initMap.remove(node);
		}

		set = initMap.keySet();
		for (CmTreeNode node : set) {
			CmTreeNode delNode = initMap.get(node);
			if(delNode != null){
				delNode.getPart().setOperType(node.getPart().getOperType()); // 修改数量
				delList.add(initMap.get(delNode));
			}
		}

		for (CmTreeNode n1 : delList) {
			CmTreeNode n = (CmTreeNode) n1.getParent();
			if (!"new".equals(n1.getPart().getOperType())) { // 修改数量 添加
				// if(!n1.isNewTopNode()){ //刚添加就删除的情况
				if (!delList.contains(n)) {
					realDelList.add(n1);
				}
				// }

			}
		}

		return realDelList;

	}

	@SuppressWarnings("unchecked")
	public void updatePbomPlanning(CmTreeNode node, CmTreeNode parent, List<CmTreeNode> list, List<List<Object>> updateNodesList) {
		log.debug(node.toString());
		CmLightPart part = node.getPart();
		boolean flag = isHasOtherNodeInTree(list, node);// 是否已经处理过相同的节点

		if ("new".equals(part.getOperType())) {
			boolean isAdd = true;
			if (node.getParent() == null) {
				isAdd = false;
			} else {
				for (CmTreeNode lNode : list) {
					if (CmCommonStringUtil.isCommon(lNode, node)) {
						if ("new".equals(lNode.getPart().getOperType())) {
							if (CmCommonStringUtil.isCommon((CmTreeNode) lNode.getParent(), (CmTreeNode) node.getParent())) {
								isAdd = false;
								if (lNode.getParent().equals(node.getParent())) {
									isAdd = true;
									break;
								}
							}
						}
					}
				}
			}
			if (isAdd) {
				// 更新planning视图-----新增节点
				log.debug("新增的节点*****：" + node.toString() + "===============" + node.getPart().getOperType());
				log.debug("parentPartOid" + "==============" + getParentPart(node, parent).getOid());
				log.debug("oid" + "==============" + node.getPart().getOid());
				log.debug("partNumber" + "==============" + part.getPartNumber(), part.getPartName());
				log.debug("containerId" + "==============" + getParentPart(node, parent).getContainerId());
				List<Object> ulist = new ArrayList<Object>();
				// 使用数量
				int count = 1;
				if (CmCommonStringUtil.isPackage(node)) {
					count += node.getListNode().size();
				} else {
					count = node.getPart().getUseCount();
				}

				ulist.add("new");
				ulist.add(getParentPart(node, parent).getOid() + "");
				ulist.add(node.getPart().getOid() + "");
				ulist.add(part.getPartType());
				ulist.add(getNodePart(part, part.isEdit()));
				// TODO 812 link invcode
				Wzk wzk = part.getWzk();
				ulist.add(CmCommonStringUtil.emptyToString(wzk.getInvcode()));

				updateNodesList.add(ulist);
				if (!CmCommonStringUtil.isEmpty(useroid) && CmCommonStringUtil.isNewNode(part.getPartType())) {
					part.setResponser(useroid);
				}
				ulist.add(count);
				list.add(node);
			}

			// part.setOperType("");
			// part.setEdit(false);
			// part.setMove(false);
		} else if (part.isMove()) {
			// 更新planning视图---节点位置有变化
			log.debug("节点位置和节点属性都变化：" + node.toString() + "===============" + node.getPart().getOperType());
			log.debug("OldParentPartOid" + "==============" + getOldParentPartOid(node));
			log.debug("Parent" + "==============" + getNewParentPartOid(node, parent));
			log.debug("oid" + "==============" + String.valueOf(part.getOid()));
			List<Object> ulist = new ArrayList<Object>();
			ulist.add("move");
			ulist.add(getOldParentPartOid(node));
			ulist.add(getNewParentPartOid(node, parent));
			ulist.add(String.valueOf(part.getOid()));
			ulist.add(part.getPartType());
			Map<String, String> map = new HashMap<String, String>();
			if (!flag) {
				map = getNodePart(part, part.isEdit());
			}
			ulist.add(map);

			// TODO 812 link invcode
			Wzk wzk = part.getWzk();
			ulist.add(CmCommonStringUtil.emptyToString(wzk.getInvcode()));

			updateNodesList.add(ulist);

			// part.setEdit(false);
			// part.setMove(false);
		} else if (!part.isMove() && part.isEdit() && !flag) {
			// 更新planning视图---节点属性有修改---如果有多个相同的零件，有且只修改一次
			log.debug("节点属性变化的节点*****：" + node.toString() + "===============" + node.getPart().getOperType());
			List<Object> ulist = new ArrayList<Object>();
			ulist.add("modify");
			ulist.add(getParentPart(node, parent).getOid() + "");
			// ulist.add(node.getPart().getOid() + "");
			ulist.add(String.valueOf(part.getOid()));
			// TODO 812 link invcode
			Wzk wzk = part.getWzk();
			ulist.add(CmCommonStringUtil.emptyToString(wzk.getInvcode()));

			ulist.add(getNodePart(part, true));

			// 使用数量
			int count = 1;
			if (CmCommonStringUtil.isPackage(node)) {
				count += node.getListNode().size();
			} else {
				count = node.getPart().getUseCount();
			}

			ulist.add(count);

			updateNodesList.add(ulist);
			// part.setEdit(false);
		}
		if (!flag) {
			list.add(node);
		}
		Enumeration children = node.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			updatePbomPlanning(child, null, list, updateNodesList);
		}
		if (CmCommonStringUtil.isPackage(node)) {
			for (CmTreeNode brother : node.getListNode()) {
				updatePbomPlanning(brother, (CmTreeNode) node.getParent(), list, updateNodesList);
			}
		}
	}

	public Map<String, String> getNodePart(CmLightPart part, boolean isEdit) {
		Map<String, String> map = new HashMap<String, String>();
		if (isEdit) {
			// map.put("isKey", String.valueOf(part.isKey()));
			// map.put("isSpecial", String.valueOf(part.isSpecial()));
			// map.put("spaceBorneTable",
			// String.valueOf(part.isSpaceBorneTable()));
			// map.put("workShop",
			// CmCommonStringUtil.isEmpty(part.getWorkShop())?"":part.getWorkShop());
			// map.put("outsourcingUnits",
			// CmCommonStringUtil.isEmpty(part.getOutsourcingUnits())?"":part.getOutsourcingUnits());
			// map.put("materialType",
			// CmCommonStringUtil.isEmpty(part.getMaterialType())?"":part.getMaterialType());
			// map.put("backupRate",
			// CmCommonStringUtil.isEmpty(part.getBackupRate())?"0":part.getBackupRate());
			// map.put("maxBackupCount",
			// CmCommonStringUtil.isEmpty(part.getMaxBackupCount())?"0":part.getMaxBackupCount());
			// map.put("backupReason",
			// CmCommonStringUtil.isEmpty(part.getBackupReason())?"":part.getBackupReason());
			// map.put("remark",
			// CmCommonStringUtil.isEmpty(part.getRemark())?"":part.getRemark());

			// TODO 添加149属性 保存
			map.put("ZZCJ", CmCommonStringUtil.emptyToString(part.getZzcj()));
			map.put("FZCJ", CmCommonStringUtil.emptyToString(part.getFzcj()));

			String zzcj = part.getZzcj();
			String fzcj = part.getFzcj();
			String routing = "";
			if (zzcj != null && !"".equals(zzcj)) {
				routing = zzcj;
			}
			if (fzcj != null && !"".equals(fzcj)) {
				if (!"".equals(routing)) {
					routing = routing + "-" + fzcj;
				} else {
					routing = fzcj;
				}
			}
			map.put("ROUTING", routing);

			map.put("MTYPE", CmCommonStringUtil.emptyToString(part.getMtype()));
			map.put("BATCH", CmCommonStringUtil.emptyToString(part.getBatch()));

			map.put("CMAT", CmCommonStringUtil.emptyToString(part.getCmat()));
			map.put("PTC_MATERIAL_NAME", CmCommonStringUtil.emptyToString(part.getPtc_material_name()));
			map.put("CMAT_UP", CmCommonStringUtil.emptyToString(part.getCmat_up()));
			map.put("CMAT_DOWN", CmCommonStringUtil.emptyToString(part.getCmat_down()));
			map.put("SETMARK", CmCommonStringUtil.emptyToString(part.getSetmark()));
			map.put("ADJUSTABLE", CmCommonStringUtil.emptyToString(part.getAdjustable()));
			map.put("PHASE_CODE", CmCommonStringUtil.emptyToString(part.getPhase_code()));
			map.put("KEYCOMPONENT", CmCommonStringUtil.emptyToString(part.getKeycomponent()));
			map.put("CSIZE", CmCommonStringUtil.emptyToString(part.getCsize()));
			map.put("CTYPE", CmCommonStringUtil.emptyToString(part.getCtype()));
			map.put("SECRET", CmCommonStringUtil.emptyToString(part.getSecret()));
			map.put("ENDITEMIN", CmCommonStringUtil.emptyToString(part.getEnditemin()));
			map.put("MINDEX", CmCommonStringUtil.emptyToString(part.getMindex()));
			map.put("CINDEX", CmCommonStringUtil.emptyToString(part.getCindex()));
			map.put("PINDEX", CmCommonStringUtil.emptyToString(part.getPindex()));
			map.put("PTC_COMMON_NAME", CmCommonStringUtil.emptyToString(part.getPtc_common_name()));

			map.put("XHPH", CmCommonStringUtil.emptyToString(part.getXhph()));
			map.put("JSTJ", CmCommonStringUtil.emptyToString(part.getJstj()));
			// map.put("CHBM",CmCommonStringUtil.emptyToString(
			// part.getChbm()));

			// 设计资源库应用改造新增属性
			// start
			map.put("SHORTNAME", CmCommonStringUtil.emptyToString(part.getShortname()));// 物资简称
			map.put("STANDARDNUMBER", CmCommonStringUtil.emptyToString(part.getStandardnumber()));// 标准号
			map.put("MECHANICALPROPERTYORHARDNESS", CmCommonStringUtil.emptyToString(part.getMechanicalpropertyorhardness()));// 机械性能等级或硬度
			map.put("SURFACETREATMENT", CmCommonStringUtil.emptyToString(part.getSurfacetreatment()));// 表面处理
			map.put("HEATTREATMENT", CmCommonStringUtil.emptyToString(part.getHeattreatment()));// 热处理
			map.put("PRODUCTFORM", CmCommonStringUtil.emptyToString(part.getProductform()));// 产品型式
			map.put("PRODUCTLEVEL", CmCommonStringUtil.emptyToString(part.getProductlevel()));// 产品等级
			map.put("PLATECSCREWFORM", CmCommonStringUtil.emptyToString(part.getPlatecscrewform()));// 板拧形式
			map.put("ISIMPORT", CmCommonStringUtil.emptyToString(part.getIsimport()));// 是否进口
			map.put("SPECIALINSTRUCTION", CmCommonStringUtil.emptyToString(part.getSpecialinstruction()));// 特殊说明
			map.put("MEASUREUNIT", CmCommonStringUtil.emptyToString(part.getMeasureunit()));// 计量单位
			map.put("TYPE", CmCommonStringUtil.emptyToString(part.getType()));// 型号
			map.put("TYPESTANDARD", CmCommonStringUtil.emptyToString(part.getTypestandard()));// 型号规格
			map.put("QUALITYLEVEL", CmCommonStringUtil.emptyToString(part.getQualitylevel()));// 质量等级
			map.put("TOTALSTANDARD", CmCommonStringUtil.emptyToString(part.getTotalstandard()));// 总规范
			map.put("DETAILSTANDARD", CmCommonStringUtil.emptyToString(part.getDetailstandard()));// 详细规范
			map.put("PACKAGINGFORM", CmCommonStringUtil.emptyToString(part.getPackagingform()));// 封装形式
			map.put("OUTLINESIZE", CmCommonStringUtil.emptyToString(part.getOutlinesize()));// 外形尺寸
			map.put("SPECIALCONDITION", CmCommonStringUtil.emptyToString(part.getSpecialcondition()));// 专用条件
			map.put("EXTRACONDITION", CmCommonStringUtil.emptyToString(part.getExtracondition()));// 附加协议
			map.put("MATTYPE", CmCommonStringUtil.emptyToString(part.getMattype()));// 材料类型

			map.put("NUMBER", CmCommonStringUtil.emptyToString(part.getCmatnumber()));// 材料编号
			map.put("MARKNUMBER", CmCommonStringUtil.emptyToString(part.getMarknumber()));// 牌号
			map.put("SUPPLYSTATE", CmCommonStringUtil.emptyToString(part.getSupplystate()));// 供应状态
			map.put("USESTANDARD", CmCommonStringUtil.emptyToString(part.getUsestandard()));// 采用标准
			// end

			if (part.getWzk() != null) {
				// ErpUtil.setPartByWzk(map, part.getWzk(),
				// part.getWzk().getOpType());
			}

			String gysl = part.getGysl();
			if (gysl != null && !"".equals(gysl)) {
				map.put("GYSL", gysl);
			}
		}
		return map;
	}

	public String getOldParentPartOid(CmTreeNode node) {
		// CmTreeNode cmnode = CmCommonStringUtil.checkTheNodeIsInList(node,
		// CmScrollPaneTree.pbomlist);
		CmTreeNode cmnode = CmScrollPaneTree.pbomMap.get(node);
		return null == cmnode ? "" : ((CmTreeNode) cmnode.getParent()).getPart().getOid() + "";
	}

	public String getNewParentPartOid(CmTreeNode node, CmTreeNode parent) {
		return null == node.getParent() ? String.valueOf(parent.getPart().getOid()) : String.valueOf(((CmTreeNode) node.getParent()).getPart().getOid());
	}

	public CmLightPart getParentPart(CmTreeNode node, CmTreeNode parent) {
		return null == node.getParent() ? parent.getPart() : ((CmTreeNode) node.getParent()).getPart();
	}

	public boolean isHasOtherNodeInTree(List<CmTreeNode> list, CmTreeNode node) {
		boolean flag = false;
		for (CmTreeNode treeNode : list) {
			if (CmCommonStringUtil.isCommon(node, treeNode)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public CmMBomMainFrame(boolean flag) {

	}

	public static void savePbomWithOnlyPbomEdit(CmTree mtree, JDialog dialog, boolean isclose) {
		CmMBomMainFrame main = new CmMBomMainFrame(true);
		main.savePbomEdit(mtree, dialog, isclose);
	}

	@SuppressWarnings("static-access")
	public void savePbomEdit(CmTree mtree, JDialog dialog, boolean isclose) {
		CmTreeNode root = mtree.getRoot();
		if (root.children().hasMoreElements()) {
			List<List<Object>> updateNodesList = dealWithAllChangeNodes(root);
			if (isclose && null != updateNodesList && updateNodesList.size() > 0) {
				int option = JOptionPane.showConfirmDialog(mtree.getRootPane(), "是否保存?", "提示", JOptionPane.YES_NO_CANCEL_OPTION);
				// 0 是 1 否 2 取消
				if (option == 0) {
					final CmActionProgressBar animFrame = new CmActionProgressBar(null, dialog, "保存数据", "正在保存PBOM数据", "正在保存PBOM数据，请等待...");
					savePbomWithOnlyEdit(animFrame, mtree, dialog);
				} else if (option == 1) {
					SetPbomAttributeDialog.colseEditPbomDialog();
					if (CmContext.isJWS())
						JWSUtil.shutdown();
					System.exit(0);
				} else if (option == 2) {
					dialog.setDefaultCloseOperation(dialog.DO_NOTHING_ON_CLOSE);
				}
			} else if (!isclose && null != updateNodesList && updateNodesList.size() > 0) {
				final CmActionProgressBar animFrame = new CmActionProgressBar(null, dialog, "保存数据", "正在保存PBOM数据", "正在保存PBOM数据，请等待...");
				savePbomWithOnlyEdit(animFrame, mtree, dialog);
			} else {
				SetPbomAttributeDialog.colseEditPbomDialog();
				if (CmContext.isJWS())
					JWSUtil.shutdown();
				System.exit(0);
			}
		}
	}

	public void savePbomWithOnlyEdit(final CmActionProgressBar animFrame, final CmTree mtree, final JDialog dialog) {
		try {
			Thread saveMBom = new Thread() {
				public void run() {
					boolean flag = true;
					CmTreeNode root = mtree.getRoot();
					CmCommonPackageAction action = new CmCommonPackageAction();
					if (root.children().hasMoreElements()) {
						action.packageAllNode(mtree);
						List<Map<String, Object>> planninglist = new ArrayList<Map<String, Object>>();
						getUpdateVersionNode(mtree.getRoot(), newplaningnodelist, planninglist);// 获取升版本的零件
						Map<String, Object> newplanningMap = PBOMEditorToWCIntf.updatePartVersion(planninglist);
						if (null != newplanningMap.get("errorMessage")) {
							JOptionPane.showMessageDialog(mtree.getRootPane(), newplanningMap.get("errorMessage"));
							flag = false;
						} else {
							// updateNewPlanning(mtree.getRoot(),newplanningMap,CmScrollPaneTree.pbomlist);//更新升版本的planning的oid
							updateNewPlanning(mtree.getRoot(), newplanningMap, new ArrayList<CmTreeNode>(CmScrollPaneTree.pbomMap.values()));// 更新升版本的planning的oid
							if ("PE".equals(CmConnectFrame.from)) {
								useroid = PBOMEditorToWCIntf.getCurrentUserInfoRMI().get(1);
							}
							List<List<Object>> updateNodesList = dealWithAllChangeNodes(root);
							String str = PBOMEditorToWCIntf.pbomStructure(updateNodesList);// 实例化
							if ("success".equals(str)) {
								upatetTreeUI(mtree);
								log.debug("开始获取数据");
								CmXmlUtil xml = new CmXmlUtil();
								saveModifyPartNumber(root, xml);
								// byte[] bytes = xml.saveTreeToBytes(root);
								// log.debug("节点数据转化成XML成功，开始提交服务器");
								// flag
								// =PBOMEditorToWCIntf.savePBOMXml(String.valueOf(((CmTreeNode)root.children().nextElement()).getPart().getOid()),
								// bytes);
								CmTreeNode firstNode = (CmTreeNode) root.children().nextElement();
								List<Map<String, Object>> byteslist = xml.saveTreeToBytes(firstNode);
								String oid = String.valueOf(firstNode.getPart().getOid());
								log.debug("节点数据转化成XML成功，开始提交服务器");
								for (Map<String, Object> map : byteslist) {
									if (oid.equals(map.get("oid")))
										flag = PBOMEditorToWCIntf.savePBOMXml(String.valueOf(map.get("oid")), (byte[]) map.get("bytes"));
								}

								if (!flag) {
									JOptionPane.showMessageDialog(dialog, "XML保存失败！");
								} else {
									log.debug("保存成功");
								}
							} else if ("failed".equals(str)) {
								JOptionPane.showMessageDialog(dialog, "PBOM实例化失败！");
								flag = false;
							}
						}
					} else {
						flag = false;
						JOptionPane.showMessageDialog(dialog, "PBOM为空！");
					}
					animFrame.finish();
					animFrame.setVisible(false);
					if (flag) {
						SetPbomAttributeDialog.colseEditPbomDialog();
						if (CmContext.isJWS())
							JWSUtil.shutdown();
						System.exit(0);
					}
				}
			};
			saveMBom.start();
			animFrame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void upatetTreeUI(CmTree tree) {
		if (null != tree) {
			tree.updateUI();
		}
	}

	/**
	 * 找出PBOM中不能和EBOM联动的节点
	 *
	 * @author chenyunlong
	 * @date 2013-5-28
	 * @param ebomlist
	 * @param pbomList
	 * @return
	 *
	 */
	public StringBuffer getPbomOldNode(List<CmTreeNode> ebomlist, List<CmTreeNode> pbomList, StringBuffer errorBuf) {
		CmCommonNodeUtil common = new CmCommonNodeUtil();
		List<CmTreeNode> list = new ArrayList<CmTreeNode>();
		for (CmTreeNode pbom : pbomList) {
			if (!CmCommonStringUtil.isNewNode(pbom.getPart().getPartType())) {
				CmTreeNode same = common.getSameOccpathInList(pbom, ebomlist);
				if (null == same && CmTreeNodeComparator.checkNodeHasPositionId(pbom)) {
					pbom.getPart().setSearch(true);
					setSelectedWithParent(pbom);
					list.add(pbom);
				}
			}
		}
		if (list.size() > 0) {
			for (int j = 0; j < list.size(); j++) {
				CmCommonPackageAction.buildPath(list.get(j), errorBuf, "PBOM");
				errorBuf.append("\n");
				if (j == 5) {
					errorBuf.append("       .......            \n");
					break;
				}
			}
			errorBuf.insert(0, "下列PBOM树节点或其子节点：\n");
			errorBuf.append("\n在EBOM中不存在或存在不能联动的节点，应移除!\n");
		}
		return errorBuf;
	}

	/**
	 * EBOM中删除的结构在PBOM中未移除
	 *
	 * @author chenyunlong
	 * @date 2013-5-28
	 * @param ebomlist
	 * @param pbomList
	 * @return
	 *
	 */
	public StringBuffer getEbomDeleteNode(List<CmTreeNode> ebomlist, List<CmTreeNode> pbomList, StringBuffer errorBuf) {
		CmCommonNodeUtil common = new CmCommonNodeUtil();
		List<CmTreeNode> list = new ArrayList<CmTreeNode>();
		for (CmTreeNode ebom : ebomlist) {
			if (ebom.getPart().getEchangeIndex() == 3) {
				CmTreeNode same = common.getSameOidInList(ebom, pbomList);
				if (null != same && CmCommonNodeUtil.checkNodeIsCommon((CmTreeNode) same.getParent(), (CmTreeNode) ebom.getParent())) {
					same.getPart().setSearch(true);
					setSelectedWithParent(same);
					list.add(same);
				}
			}
		}
		if (list.size() > 0) {
			for (int j = 0; j < list.size(); j++) {
				CmCommonPackageAction.buildPath(list.get(j), errorBuf, "EBOM");
				errorBuf.append("\n");
				if (j == 5) {
					errorBuf.append("       .......            \n");
					break;
				}
			}
			errorBuf.insert(0, "下面EBOM上移除的节点：\n");
			errorBuf.append("\n在PBOM上相同的结构下未移除!\n");
		}
		return errorBuf;
	}

	/**
	 * EBOM变更过程中新增的节点在PBOM中没有
	 *
	 * @author chenyunlong
	 * @date 2013-5-28
	 * @param ebomlist
	 * @param pbomList
	 * @return
	 *
	 */
	public StringBuffer getEbomNewNode(List<CmTreeNode> ebomlist, List<CmTreeNode> pbomList, StringBuffer errorBuf) {
		CmTreeNode old = null;
		CmCommonNodeUtil common = new CmCommonNodeUtil();
		for (CmTreeNode ebom : ebomlist) {
			if (ebom.getPart().getEchangeIndex() == 2) {
				CmTreeNode same = common.getSameOccpathInList(ebom, pbomList);
				if (null == same) {
					old = ebom;
					break;
				}
			}
		}
		if (null != old) {
			CmCommonPackageAction.buildPath(old, errorBuf, "EBOM");
			errorBuf.append("\n");
			errorBuf.insert(0, "下面EBOM上新增的节点：\n");
			errorBuf.append("\n在PBOM上不存在!\n");
		}
		return errorBuf;
	}

	/**
	 * EBOM变更过程中升版本的零件在PBOM中没有
	 *
	 * @author chenyunlong
	 * @date 2013-5-28
	 * @param ebomlist
	 * @param pbomList
	 * @return
	 *
	 */
	public StringBuffer getEbomUpdateVersionNode(List<CmTreeNode> ebomlist, List<CmTreeNode> pbomList, StringBuffer errorBuf) {
		CmCommonNodeUtil common = new CmCommonNodeUtil();
		List<CmTreeNode> list = new ArrayList<CmTreeNode>();
		for (CmTreeNode ebom : ebomlist) {
			if (ebom.getPart().getEchangeIndex() == 1) {
				CmTreeNode same = common.getSameOccpathInList(ebom, pbomList);
				if (null == same && CmTreeNodeComparator.checkNodeHasPositionId(ebom)) {
					ebom.getPart().setSearch(true);
					setSelectedWithParent(ebom);
					list.add(ebom);
				}
			}
		}
		if (list.size() > 0) {
			for (int j = 0; j < list.size(); j++) {
				CmCommonPackageAction.buildPath(list.get(j), errorBuf, "PBOM");
				errorBuf.append("\n");
				if (j == 5) {
					errorBuf.append("       .......            \n");
					break;
				}
			}
			errorBuf.insert(0, "下面EBOM上升版的节点：\n");
			errorBuf.append("\n在PBOM上不存在或存在不能联动的节点!\n");
		}
		return errorBuf;
	}

	/**
	 * EBOM变更过程中导致部分节点版本未作变化，但是也失去了与PBOM联动的功能，这部分节点也必须同步到PBOM中去
	 *
	 * @author chenyunlong
	 * @date 2013-5-28
	 * @param ebomlist
	 * @param pbomList
	 * @return
	 *
	 */
	public StringBuffer getEbomChangeNode(List<CmTreeNode> ebomlist, List<CmTreeNode> pbomList, StringBuffer errorBuf) {
		CmCommonNodeUtil common = new CmCommonNodeUtil();
		List<CmTreeNode> list = new ArrayList<CmTreeNode>();
		for (CmTreeNode ebom : ebomlist) {
			if (ebom.getPart().getEchangeIndex() == 0) {
				CmTreeNode same = common.getSameOccpathInList(ebom, pbomList);
				if (null == same && CmTreeNodeComparator.checkNodeHasPositionId(ebom)) {
					ebom.getPart().setSearch(true);
					setSelectedWithParent(ebom);
					list.add(ebom);
				}
			}
		}
		if (list.size() > 0) {
			for (int j = 0; j < list.size(); j++) {
				CmCommonPackageAction.buildPath(list.get(j), errorBuf, "PBOM");
				errorBuf.append("\n");
				if (j == 5) {
					errorBuf.append("       .......            \n");
					break;
				}
			}
			errorBuf.insert(0, "下面EBOM上的节点：\n");
			errorBuf.append("\n在PBOM上不存在或存在不能联动的节点!\n");
		}
		return errorBuf;
	}

	/**
	 * 获取PBOM树上的无结构的工艺中间件
	 *
	 * @author chenyunlong
	 * @date 2013-5-28
	 * @param root
	 * @param errorBuf
	 * @return
	 *
	 */
	@SuppressWarnings("unchecked")
	public StringBuffer getMiddleNodeWithNoChildNode(CmTreeNode root, StringBuffer errorBuf) {
		Enumeration children = root.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if ("middle".equals(child.getPart().getPartType()) && !child.children().hasMoreElements()) {
				CmCommonPackageAction.buildPath(child, errorBuf, "PBOM");
				errorBuf.append("\n");
				errorBuf.insert(0, "下面PBOM上的工艺中间件或工艺组合件：\n");
				errorBuf.append("\n无下级节点，请确认是否保存？\n");
				break;
			}
			errorBuf = getMiddleNodeWithNoChildNode(child, errorBuf);
		}
		return errorBuf;
	}

	/**
	 * 获取不能和图形联动的零件的信息
	 *
	 * @author chenyunlong
	 * @date 2013-7-25
	 * @param pbomList
	 * @param errorBuf
	 * @return
	 *
	 */
	public StringBuffer getPbomNodeWithoutPositionId(List<CmTreeNode> pbomList, StringBuffer errorBuf) {
		List<CmTreeNode> list = new ArrayList<CmTreeNode>();
		for (CmTreeNode pbom : pbomList) {
			if (null != pbom.getPart() && !CmCommonStringUtil.isNewNode(pbom.getPart().getPartType()) && !CmTreeNodeComparator.checkNodeHasPositionId(pbom)) {
				pbom.getPart().setSearch(true);
				setSelectedWithParent(pbom);
				list.add(pbom);
			}
		}
		if (list.size() > 0) {
			for (int j = 0; j < list.size(); j++) {
				CmCommonPackageAction.buildPath(list.get(j), errorBuf, "PBOM");
				errorBuf.append("\n");
				if (j == 5) {
					errorBuf.append("       .......            \n");
					break;
				}
			}
			errorBuf.insert(0, "下面PBOM上的节点：\n");
			errorBuf.append("\n与图形无法联动,是否继续保存？\n");
		}
		return errorBuf;
	}

	/**
	 * 更新PBOM树上升版本的零件的oid等信息
	 *
	 * @author chenyunlong
	 * @date 2013-6-7
	 * @param root
	 * @param newplanningMap
	 *
	 */
	@SuppressWarnings("unchecked")
	public void updateNewPlanning(CmTreeNode root, Map<String, Object> newplanningMap, List<CmTreeNode> plist) {
		Enumeration children = root.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if (CmCommonNodeUtil.checkHasSameOidInList(child, newplaningnodelist)) {
				List<Object> list = (List<Object>) newplanningMap.get(child.getPart().getPartNumber());
				if (null != list && list.size() > 0) {
					child.getPart().setOid(Long.valueOf(String.valueOf(list.get(0))));
					child.getPart().setVersion(String.valueOf(list.get(1)));
					updateDeleteNodeOid(child, plist, list.get(0));
				}
			}
			updateNewPlanning(child, newplanningMap, plist);
			if (CmCommonStringUtil.isPackage(child)) {
				for (CmTreeNode brother : child.getListNode()) {
					if (CmCommonNodeUtil.checkHasSameOidInList(brother, newplaningnodelist)) {
						List<Object> list = (List<Object>) newplanningMap.get(brother.getPart().getPartNumber());
						if (null != list && list.size() > 0) {
							brother.getPart().setOid(Long.valueOf(String.valueOf(list.get(0))));
							brother.getPart().setVersion(String.valueOf(list.get(1)));
							updateDeleteNodeOid(brother, plist, list.get(0));
						}
					}
					updateNewPlanning(brother, newplanningMap, plist);
				}
			}
		}
	}

	public void updateDeleteNodeOid(CmTreeNode obj, List<CmTreeNode> pbomlist, Object newoid) {
		for (CmTreeNode del : pbomlist) {
			if (null != del.getParent() && CmCommonStringUtil.isEqual(((CmTreeNode) del.getParent()).getOccpath(), obj.getOccpath())) {
				((CmTreeNode) del.getParent()).getPart().setOid(Long.valueOf(String.valueOf(newoid)));
			}
		}
	}

	/**
	 * 获取升版本的节点对象
	 *
	 * @author chenyunlong
	 * @date 2013-6-7
	 * @param root
	 * @param list
	 * @param planninglist
	 *
	 */
	@SuppressWarnings("unchecked")
	public void getUpdateVersionNode(CmTreeNode root, List<CmTreeNode> list, List<Map<String, Object>> planninglist) {
		Enumeration children = root.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			int verIndex = PbomTreeUpdateAction.getNodeUpdateVersionIndex(child);
			if (verIndex > 0 && !CmCommonNodeUtil.checkHasSameOidInList(child, list)) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("oid", child.getPart().getOid());
				map.put("versionIndex", verIndex);
				map.put("partNumber", child.getPart().getPartNumber());
				map.put("phase", child.getPart().getPhase_code());// 转阶段时需要阶段标记
				planninglist.add(map);
				list.add(child);
			}
			getUpdateVersionNode(child, list, planninglist);
			if (CmCommonStringUtil.isPackage(child)) {
				for (CmTreeNode brother : child.getListNode()) {
					getUpdateVersionNode(brother, list, planninglist);
				}
			}
		}
	}

	public boolean isNewVersion(CmTreeNode root) {
		Enumeration children = root.children();
		if (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			int verIndex = PbomTreeUpdateAction.getNodeUpdateVersionIndex(child);
			if (verIndex == 2) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将直接或间接父节点置为选中状态
	 *
	 * @author chenyunlong
	 * @date 2013-8-2
	 * @param root
	 *
	 */
	public void setSelectedWithParent(CmTreeNode root) {
		root.setSelected(true);
		CmTreeNode parent = (CmTreeNode) root.getParent();
		if (null != parent && !parent.getPart().isSelected()) {
			setSelectedWithParent(parent);
		}
	}

	public void updateTreeUI(CmTree tree) {
		if (null != tree) {
			tree.updateUI();
		}
	}

	/**
	 * 将有修改的xml的零组件的partNumber记录到直接或间接有xml的父节点中去
	 *
	 * @author chenyunlong
	 * @date 2013-8-2
	 * @param root
	 * @param xml
	 *
	 */
	@SuppressWarnings("unchecked")
	public void saveModifyPartNumber(CmTreeNode root, CmXmlUtil xml) {
		Enumeration children = root.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if (child.getPart().isHasXml()) {
				if (xml.checkNodeIsSaveXML(child)) {
					saveModifyChildPartNumber((CmTreeNode) child.getParent(), child.getPart().getPartNumber(), xml);
				}
			} else {
				boolean flag = xml.checkNodeIsChange(child);
				if (flag) {
					CmTreeNode parent = getParentNodeWithXML((CmTreeNode) child.getParent());
					if (null != parent) {
						parent.getPart().setEditWithChild(true);
						if (CmCommonStringUtil.isPackage(parent)) {
							for (CmTreeNode brother : parent.getListNode()) {
								brother.getPart().setEditWithChild(true);
							}
						}
						saveModifyChildPartNumber((CmTreeNode) parent.getParent(), parent.getPart().getPartNumber(), xml);
					}
				}
			}
			saveModifyPartNumber(child, xml);
		}
	}

	public boolean isPviewInitialized() {
		return pviewInitialized;
	}

	public void setPviewInitialized(boolean pviewInitialized) {
		this.pviewInitialized = pviewInitialized;
	}

	/**
	 * 为了提高工艺变更查找变更的有xml的零组件的效率 需要将变化的有xml的零组件的partNumber信息记录到直接或间接有xml的父节点中去
	 *
	 * @author chenyunlong
	 * @date 2013-8-2
	 * @param parent
	 * @param partNumber
	 * @param xml
	 *
	 */
	public void saveModifyChildPartNumber(CmTreeNode parent, String partNumber, CmXmlUtil xml) {
		if (null != parent) {
			if ((parent.getPart().isHasXml() || (null != parent.getParent() && "PBOM".equals(parent.getParent().toString())))
					&& !CmCommonStringUtil.isEqual(parent.getPart().getPartNumber(), partNumber)) {
				String modifyPartNumber = parent.getPart().getModifyXmlPartNumber() + "";
				String[] number = null;
				if (CmCommonStringUtil.isEmpty(modifyPartNumber)) {
					number = new String[0];
				} else {
					number = modifyPartNumber.split(",");
				}
				boolean flag = true;
				for (int i = 0; i < number.length; i++) {
					if (CmCommonStringUtil.isEqual(number[i], partNumber)) {
						flag = false;
						break;
					}
				}
				if (flag) {
					if (CmCommonStringUtil.isEmpty(modifyPartNumber)) {
						parent.getPart().setModifyXmlPartNumber(partNumber);
					} else {
						parent.getPart().setModifyXmlPartNumber(modifyPartNumber + "," + partNumber);
					}
				}
			}
			saveModifyChildPartNumber((CmTreeNode) parent.getParent(), partNumber, xml);
		}
	}

	/**
	 * 有些零件被编辑过属性，或升版，但是自己没有xml，就要找出有xml的父节点
	 *
	 * @author chenyunlong
	 * @date 2013-8-2
	 * @param parent
	 * @return
	 *
	 */
	public CmTreeNode getParentNodeWithXML(CmTreeNode parent) {
		if (null != parent) {
			if (null != parent.getParent() && !"PBOM".equals(parent.getParent().toString())) {
				if (parent.getPart().isHasXml()) {
					return parent;
				} else {
					return getParentNodeWithXML((CmTreeNode) parent.getParent());
				}
			} else if (null != parent.getParent() && "PBOM".equals(parent.getParent().toString())) {
				return parent;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

}