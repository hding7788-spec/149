package com.glaway.mpm.view;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import com.glaway.mpm.EditorConfig;
import com.glaway.mpm.controller.StepTemplateCopyHandler;
import com.glaway.mpm.erp.ErpToWCIntf;
import com.glaway.mpm.flowchart.TechnicsRouteJPanel;
import com.glaway.mpm.flowchart.TechnicsRouteToolBar;
import com.glaway.mpm.flowchart.TechnicsRouteUtil;
import com.glaway.mpm.license.LicenseTask;
import com.glaway.mpm.model.CMatBean;
import com.glaway.mpm.model.Frock;
import com.glaway.mpm.model.UploadTechnics;
import com.glaway.mpm.parameter.helper.MPMParameterProcessor;
import com.glaway.mpm.parameter.service.ProcessParameterToWCIntf;
import com.glaway.mpm.pdf.GenTechnicsPdfProcessor;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.pdf.PDFPreviewFactory;
import com.glaway.mpm.qmIntf.commonString.CsMaintainDialog;
import com.glaway.mpm.qmIntf.commonString.CsTreePanel;
import com.glaway.mpm.qmIntf.commonString.CsTreeXmlUtil;
import com.glaway.mpm.qmIntf.dashboard.SDashboardTreePanel;
import com.glaway.mpm.qmIntf.dashboard.UnSDashboardTreePanel;
import com.glaway.mpm.qmIntf.equipment.EpTreePanel;
import com.glaway.mpm.qmIntf.frock.FrockCardApplyDialog;
import com.glaway.mpm.qmIntf.frock.FrockCardDetailDialog;
import com.glaway.mpm.qmIntf.material.MtTreePanel;
import com.glaway.mpm.qmIntf.measure.MeasureTreePanel;
import com.glaway.mpm.qmIntf.pdName.PdNameTreePanel;
import com.glaway.mpm.qmIntf.resourceTree.KtTreePanel;
import com.glaway.mpm.qmIntf.resourceTree.ResourceTreePanel;
import com.glaway.mpm.qmIntf.technics.SetPreProdureDialog;
import com.glaway.mpm.qmIntf.technics.TechnicsPreview;
import com.glaway.mpm.qmIntf.technics.TechnicsSearchDialog;
import com.glaway.mpm.qmIntf.template.*;
import com.glaway.mpm.qmIntf.viewHistory.ViewHistory;
import com.glaway.mpm.qmIntf.viewPanel.Cortona3DPanel;
import com.glaway.mpm.qmIntf.viewPanel.CreoViewPanel;
import com.glaway.mpm.resource.Constants;
import com.glaway.mpm.sop.NewSOPTechnicsSettingJDialog;
import com.glaway.mpm.sop.util.SopProcessUtil;
import com.glaway.mpm.sop.view.SopStandardFileTableJPanel;
import com.glaway.mpm.util.*;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.ImageIntf;
import com.glaway.mpm.wcIntf.PBomIntf;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import ext.casc.process.ProcessConstants;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import wt.method.RemoteMethodServer;
import wt.util.WTException;

import javax.jnlp.ServiceManager;
import javax.jnlp.SingleInstanceListener;
import javax.jnlp.SingleInstanceService;
import javax.jnlp.UnavailableServiceException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.util.List;
import java.util.*;
import java.util.Timer;
import java.util.Map.Entry;

public class NewTechnicsPart extends JFrame implements SingleInstanceListener {

    public static HashMap<String, String> partLinkGYSLFromWNC = new HashMap<String, String>();
    public static boolean isXinZengGengGai = false;
    public static boolean isTemplateCapp = false;
    private static int pages = 0;
    private static int pages1 = 0;
    private static final long serialVersionUID = 1L;
    private static VaLogger logger = VaLogger.getLogger(NewTechnicsPart.class);
    public static String[][] technicsTypes = LoadConfig.getInstance().getTechnicsType();
    public static boolean isEditorStarted = false;
    public static byte[] pbomBytes;
    public static boolean flag = false;
    private static Timer timer = null;
    private static LicenseTask licenseTask = null;
    public static String runType;
    public static String BianzhiOid;
    public static String isSanJiGengGai = "false";
    // 单例
    private SingleInstanceService sis;
    private SingleInstanceListener sisListener;

    // 判断变更和驳回启动编辑器
    public static boolean isDownload = false;
    public static String pbomOid = null;
    public static String currentUser = null;
    public static String creatorOid = "";
    public static String childPartOid = null;
    public static String allPartOid = null;
    public static String changeTechnicsId = null;
    public static String workItemOid = null;
    public static String workItemState = null;
    public static String docOid = null;
    public static String sopPartXml = null;
    public static String changeOrderOid = null;
    public static Map<String, String> pdNameDescribeMap = new HashMap<String, String>();
    public static boolean reportTechnics = false;
    public static Vector<UploadTechnics> downLoadTech = new Vector<UploadTechnics>();
    public String reworkPartNumber = null;
    public String tempNumber = null;
    private static String tmpTechnicsNumber = null;
    public static Vector<UploadTechnics> editTechnics = new Vector<UploadTechnics>();
    private static String autoFilePath = "";
    private boolean isMain = true;
    protected static String technicsTaskId;
    protected static List<String> technicsPartList;

    public final static String MIDDLE_MODE_PATH = Constants.USER_HOME + File.separator + "mpm" + File.separator + "middlemodel" + File.separator;

    public final String ERROR1 = "该操作需要针对工艺进行，产品树中无选中工艺节点！";

    private JMenuBar jMenuBar;
    private JMenu jMenuFile;
    private JMenu jMenuSet;
    private JMenu jMenuTool;
    private JMenu jMenuSearch;
    private JMenu jMenuHelp;
    private JMenuItem newTechnicsPart = new JMenuItem("打开新工艺规程管理器");
    private JMenuItem jMenuFileexit = new JMenuItem();
    private JMenu create = new JMenu("新建");
    // 工艺路线
    private TotalTechnicsRouteJPanel totalTechnicsRouteJPanel;
    private JPanel imagePanel = new JPanel();
    private JPanel dataPanel = new JPanel();
    private PartMasterJPanel partMasterPanel = new PartMasterJPanel(this);
    private JMenuItem reviewTechnics = new JMenuItem("工艺预览");
    private JMenuItem pdfReviewTechnics = new JMenuItem("PDF工艺预览");
    private JMenuItem autoCreateProcedure = new JMenuItem("自动生成工序工步");
    private JMenuItem technicsUpload = new JMenuItem("工艺上载");
    private JMenuItem technicsUpdate = new JMenuItem("工艺更新");
    private JMenuItem delete = new JMenuItem("删除                Ctrl+D");
    private JMenu jMenuSelect = new JMenu("选中");
    private JMenuItem copy = new JMenuItem("复制                Ctrl+Q");
    private JMenuItem paste = new JMenuItem("粘贴                Ctrl+W");
    private JMenuItem signed = new JMenuItem("提交三级工艺签审");
    private JMenuItem signed2 = new JMenuItem("提交五级工艺签审");
    private JMenuItem completeTask = new JMenuItem("完成工艺任务");
    private JMenuItem createTechnics = new JMenuItem("新建工艺                Ctrl+T");
    private JMenuItem createStep = new JMenuItem("新建工序                Ctrl+P");
    private JMenuItem workspacesItem = new JMenuItem("设置工作空间");
    private JMenuItem mySettingItem = new JMenuItem("我的设置");
    private JMenu run = new JMenu("启动工具");
    private JMenuItem runDiagram = new JMenuItem("工艺简图工具");
    private JMenuItem runMiddleModule = new JMenuItem("中间模型工具");
    private JMenuItem runVisual = new JMenuItem("可视化工具");
    private JMenuItem runAssembleCartoon = new JMenuItem("装配动画工具");
    private JMenuItem updatePBOM = new JMenuItem("PBOM更新");
    private JMenuItem startPBOM = new JMenuItem("启动PBOM编辑器");
    private JMenuItem templateMaintain = new JMenuItem("本地工艺模板维护");
    private JMenuItem procedureTempletMaintain = new JMenuItem("本地工序模板维护");
    private JMenuItem downloadCsTemplate = new JMenuItem("下载常用语模板");
    private JMenuItem importCommonString = new JMenuItem("导入个人工艺常用语");
    private JMenuItem exportCommonString = new JMenuItem("导出个人工艺常用语");
    private JMenuItem terminology = new JMenuItem("个人工艺常用语库维护");
    private JMenuItem viewHistory = new JMenuItem("查看历史工艺");
    private JMenuItem technicsCoWork = new JMenuItem("工艺合编");
    private JMenuItem submitUniteTechnics = new JMenuItem("提交工艺合编");
    private JMenuItem technicsConfirm = new JMenuItem("工艺路线确认");
    private JMenuItem jMenuPDSTechnics = new JMenuItem();
    private JMenuItem jMenuHelpabout = new JMenuItem();
    private JMenuItem batchUploadTechnics = new JMenuItem("批量上载工艺工程");
    private GridBagLayout gridBagLayout3 = new GridBagLayout();
    private JPanel contentPane = new JPanel();
    private JSplitPane jSplitPane = new JSplitPane();
    private JSplitPane contentSplitPane = new JSplitPane();
    private JPanel rightJPanel = new JPanel();
    private JPanel leftJPanel = new JPanel();
    private JLabel leftJLabel = new JLabel();
    private JTabbedPane treeJTabbedPane = new JTabbedPane();
    private JPanel tecnicsPanel = new JPanel();
    public JTabbedPane tecnicsJTabbedPane = new JTabbedPane();
    public JTabbedPane sopJTabbedPane = new JTabbedPane();
    public XWPartTreePanel xwPartTreePanel = new XWPartTreePanel(this);
    public XWReportTechnicsMasterJPanel reportTreeJPanel = new XWReportTechnicsMasterJPanel(this);
    public XWJsxyJPanel xwJsxyJPanel;

    public TechnicsTreePanel technicsTreePanel = new TechnicsTreePanel(this);
    public NewTechnicsMasterJPanel_XW technicsMasterJPanel = new NewTechnicsMasterJPanel_XW(this);
    private TechnicsStepJPanel_XW technicsStepJPanel = new TechnicsStepJPanel_XW(this);
    private TechnicsPaceJPanel_XW technicsPaceJPanel = new TechnicsPaceJPanel_XW(this);
    public static JPanel foregoingPanel;
    private static Element copyElement;
    private static Element copyTechElement;
    public static Vector copyPace = new Vector();
    public static Vector copyDatas = new Vector();
    public static String technicsType = "";
    private CreoViewPanel creopanel = null;
    private Cortona3DPanel panel3D = null;
    private KeyInputListener keyInputListener = new KeyInputListener();
    public SopStandardFileTableJPanel sopStandardFileTableJPanel = new SopStandardFileTableJPanel(this);
    private PeiTaoListTableJPanel peiTaoListTableJPanel = new PeiTaoListTableJPanel( this);
    private GongYiCanShuTableJPanel gongYiCanShuTableJPanel = new GongYiCanShuTableJPanel(this);

    //工装
    private ResourceTreePanel rtPanel = new ResourceTreePanel(this);
    //量具
    private MeasureTreePanel measurePanel = new MeasureTreePanel(this);
    //刀具
    private KtTreePanel ktPanel = new KtTreePanel(this);
    //工艺辅料
    private MtTreePanel mtPanel = new MtTreePanel(this);
    //常用语
    private CsTreePanel csTreePanel = null;
    //设备
    private EpTreePanel eqPanel = new EpTreePanel(this);
    //标准仪器仪表
    private SDashboardTreePanel sDashboardPanel = new SDashboardTreePanel(this);
    //非标准仪器仪表
    private UnSDashboardTreePanel unSDashboardPanel = new UnSDashboardTreePanel(this);
    //工序名称
    private PdNameTreePanel namePanel = new PdNameTreePanel(this);

    private UniversalToolBar universalToolBar = new UniversalToolBar(this);
    private MenuSelectedAdapter mouseAdapter = new MenuSelectedAdapter();
    //private static long ebomOid = -1;

    public static XWTreeNode parentNode;
    public static String copyTechNumber;
    public static String submitFlag;
    public static String docStyle;
    public static String partNumber;
    // 用来存放每种类型零部件属性键值对
    private static Map<String, List<Map<String, String>>> attriMap;

    //保存报表类工艺的属性，从配置文件中读取，新建报表类工艺文件时加载
    private static Map<String, List<List<String>>> reportTechnicsAttriMap;
    public static Map<String, String> allStepNameMap = new HashMap<String, String>();
    //add By Mchen
    public static  Map<String, Vector<String>>  dicNameMap= new HashMap<String, Vector<String>>();

    public static final VaActionProgressBar startAnimFrame = new VaActionProgressBar(null, null, "启动工艺编辑器", "正在启动工艺编辑器，请等待...", "正在启动工艺编辑器，请等待...");

    /**
     * 工艺文件形式-外协
     */
    private static final String ISTABULAR_OUTSOURCE="外协";
    
    /**
     * 状态已批准
     */
    private static final String STATUS_APPROVED="APPROVED";
    
    public NewTechnicsPart() {
        this(true);

        logger.debug("开始下载PBOM数据和工艺数据...");
        long startTime = System.currentTimeMillis();
        if("SOP".equals(EditorConfig.startType)){
            loadSopData();
        }else{
            loadData();
        }
        long endTime = System.currentTimeMillis();
        logger.debug("下载PBOM数据和工艺数据耗时："+(endTime-startTime)+" ms");

        this.universalToolBar.setToolBarEnabled();
        setVisible(true);
        this.contentSplitPane.setDividerLocation(getWidth());

    }

    public NewTechnicsPart(boolean b) {
        logger.debug("开始初始化界面...");
        long startTime = System.currentTimeMillis();
        this.isMain = b;
        if (!b)
            setTitle("工艺规程管理器(新)");
        else {
            setTitle("工艺规程管理器");
        }

        try {
            sis = (SingleInstanceService) ServiceManager.lookup("javax.jnlp.SingleInstanceService");
            sisListener = this;
            sis.addSingleInstanceListener(sisListener);
        } catch (UnavailableServiceException e2) {
            e2.printStackTrace();
        }

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        if (this.isMain) {
            int width = Toolkit.getDefaultToolkit().getScreenSize().width;
            int height = Toolkit.getDefaultToolkit().getScreenSize().height;
            setSize(width, height - 40);
        } else {
            int width = Toolkit.getDefaultToolkit().getScreenSize().width;
            int height = Toolkit.getDefaultToolkit().getScreenSize().height;
            setBounds(width / 2 - width / 3, height / 2 - height / 3, width * 2 / 3, height * 2 / 3);
        }

        setIconImage(new ImageIcon(getClass().getResource("/images/technics.gif")).getImage());

        this.contentPane = ((JPanel) getContentPane());

        this.contentPane.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(this.universalToolBar, BorderLayout.CENTER);
        this.contentPane.add(panel, BorderLayout.NORTH);
        this.contentPane.add(this.jSplitPane, "Center");
        this.jSplitPane.setOneTouchExpandable(true);
        this.jSplitPane.setDividerSize(10);

        this.jSplitPane.setMinimumSize(new Dimension(100, 226));
        this.jSplitPane.setContinuousLayout(true);
        this.jSplitPane.setDividerLocation(280);
        this.jSplitPane.add(this.leftJPanel, "left");

        this.leftJPanel.setLayout(this.gridBagLayout3);
        this.leftJPanel.setBorder(null);
        this.leftJPanel.setDebugGraphicsOptions(0);
        this.leftJPanel.setMinimumSize(new Dimension(100, 224));
        this.leftJPanel.setPreferredSize(new Dimension(240, 524));
        this.leftJPanel.add(this.leftJLabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 2, new Insets(0, 0, 0, 0), 0, 0));
        this.leftJPanel.add(this.treeJTabbedPane, new GridBagConstraints(0, 1, 1, 1, 1.0D, 1.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0));

        //工艺资源面板
        this.treeJTabbedPane.setBorder(BorderFactory.createEtchedBorder());

        //PBOM结构树
        this.treeJTabbedPane.add(this.xwPartTreePanel, "PBOM");
        treeJTabbedPane.setForegroundAt(0, Color.RED);

        this.creopanel = new CreoViewPanel();
        this.panel3D = new Cortona3DPanel();
        this.imagePanel.setLayout(new GridBagLayout());
        this.imagePanel.setBackground(Color.white);
        this.imagePanel.add(this.creopanel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 18, 1, new Insets(0, 0, 0, 0), 0, 0));
        this.imagePanel.add(this.panel3D, new GridBagConstraints(0, 1, 1, 1, 1.0D, 1.0D, 18, 1, new Insets(0, 0, 0, 0), 0, 0));
        this.creopanel.setVisible(false);
        this.panel3D.setVisible(false);

        //工艺树
        this.treeJTabbedPane.add(this.technicsTreePanel, "工艺树");

        this.treeJTabbedPane.add(this.eqPanel, "设备");
        this.treeJTabbedPane.add(this.rtPanel, "工装");
        this.treeJTabbedPane.add(this.mtPanel, "工艺辅料");
        this.treeJTabbedPane.add(this.ktPanel, "刀具");
        this.treeJTabbedPane.add(this.sDashboardPanel, "标准仪器仪表");
        this.treeJTabbedPane.add(this.unSDashboardPanel, "非标准仪器仪表");
        this.treeJTabbedPane.add(this.measurePanel, "量具");

        try {
            String terminologyXMLPath = WorkSpaceUtil.getPersonalTerminologyDirectory();
            this.csTreePanel = new CsTreePanel(terminologyXMLPath);
            this.treeJTabbedPane.add(this.csTreePanel, "常用语");
        } catch (Exception localException) {
        }

        this.treeJTabbedPane.add(this.namePanel, "工序名称");

        this.leftJLabel.setBorder(BorderFactory.createEtchedBorder());
        this.leftJLabel.setToolTipText("");
        this.leftJLabel.setText("产品及工艺");

        this.jSplitPane.add(this.rightJPanel, "right");

        this.rightJPanel.setLayout(new BorderLayout());
        this.rightJPanel.add(this.contentSplitPane, "Center");

        this.contentSplitPane.setMinimumSize(new Dimension(100, 226));
        this.contentSplitPane.setContinuousLayout(true);
        this.contentSplitPane.setOneTouchExpandable(true);
        this.contentSplitPane.setDividerSize(10);

        this.contentSplitPane.add(this.tecnicsPanel, "left");

        this.contentSplitPane.add(this.imagePanel, "right");
        this.jSplitPane.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals("dividerLocation")) {
                    NewTechnicsPart.this.technicsStepJPanel.stopTableCellEditing();
                }
            }
        });
        this.contentSplitPane.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals("dividerLocation")) {
                    NewTechnicsPart.this.technicsStepJPanel.stopTableCellEditing();
                }
            }
        });
        this.tecnicsPanel.setLayout(new GridBagLayout());
        this.tecnicsPanel.add(this.tecnicsJTabbedPane, new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 11, 1, new Insets(5, 5, 5, 5), 0, 0));
        this.tecnicsJTabbedPane.add(this.dataPanel, "工艺信息");
        this.totalTechnicsRouteJPanel = new TotalTechnicsRouteJPanel(this);
        this.tecnicsJTabbedPane.add(this.totalTechnicsRouteJPanel, "工艺路线");
        if(!"SOP".equals(EditorConfig.startType)) {
            this.tecnicsJTabbedPane.add(sopJTabbedPane, "工艺知识");
            sopJTabbedPane.add(sopStandardFileTableJPanel, "SOP标准操作规程");
            this.tecnicsJTabbedPane.add(peiTaoListTableJPanel, "配套明细表");
        }
        this.tecnicsJTabbedPane.add(gongYiCanShuTableJPanel, "工艺参数");


        this.tecnicsJTabbedPane.setSelectedIndex(0);
        this.dataPanel.setLayout(new GridBagLayout());

        this.jMenuBar = new JMenuBar();
        this.jMenuFile = new JMenu();

        this.jMenuSet = new JMenu();
        this.jMenuTool = new JMenu();

        this.jMenuSearch = new JMenu();
        this.jMenuHelp = new JMenu();

        this.jMenuBar.add(this.jMenuFile);

        this.jMenuFile.setText("文件");

        this.jMenuBar.add(this.jMenuSet);
        this.jMenuSet.setText("设置");

        this.jMenuBar.add(this.jMenuSelect);

        this.jMenuBar.add(this.jMenuTool);
        this.jMenuTool.setText("工具");

        this.jMenuSet.add(this.workspacesItem);
        this.jMenuSet.add(this.mySettingItem);

        this.jMenuSelect.add(this.copy);
        this.leftJPanel.registerKeyboardAction(this.keyInputListener, "copy", KeyStroke.getKeyStroke(67, 2, true), 2);
        this.jMenuSelect.add(this.paste);
        this.leftJPanel.registerKeyboardAction(this.keyInputListener, "paste", KeyStroke.getKeyStroke(86, 2, true), 2);
        this.jMenuSelect.add(this.delete);
        this.leftJPanel.registerKeyboardAction(this.keyInputListener, "delete", KeyStroke.getKeyStroke(68, 2, true), 2);

        //this.jMenuSelect.addSeparator();
        //this.jMenuSelect.add(this.updatePBOM);

        //this.jMenuSelect.addSeparator();
        //this.jMenuSelect.add(this.startPBOM);

        // this.jMenuTool.add(this.technicsCoWork);
        // this.jMenuTool.add(this.submitUniteTechnics);
        // this.jMenuTool.add(this.technicsConfirm);
        // this.jMenuTool.addSeparator();
        this.jMenuTool.add(this.reviewTechnics);
        this.jMenuTool.add(this.pdfReviewTechnics);
        this.jMenuTool.addSeparator();

        this.jMenuTool.add(this.technicsUpload);
        //this.jMenuTool.add(this.technicsUpdate);
        this.jMenuTool.add(this.signed);
        this.jMenuTool.add(this.signed2);
        // this.jMenuTool.add(this.completeTask);
        this.jMenuTool.addSeparator();

        this.jMenuTool.add(this.run);
        this.run.add(this.runDiagram);
        this.run.add(this.runMiddleModule);
        this.run.add(this.runVisual);
        this.run.add(this.runAssembleCartoon);

        this.jMenuTool.addSeparator();
        this.jMenuTool.add(this.templateMaintain);
        this.jMenuTool.add(this.procedureTempletMaintain);
        this.jMenuTool.add(this.downloadCsTemplate);
        this.jMenuTool.add(this.importCommonString);
        this.jMenuTool.add(this.exportCommonString);
        this.jMenuTool.add(this.terminology);
        this.jMenuTool.addSeparator();

        this.jMenuTool.add(this.viewHistory);

        this.jMenuFile.add(this.create);
        this.create.add(this.createTechnics);
        this.contentPane.registerKeyboardAction(this.keyInputListener, "createTechnics", KeyStroke.getKeyStroke(84, 2, false), 2);
        this.create.add(this.createStep);
        this.contentPane.registerKeyboardAction(this.keyInputListener, "createStep", KeyStroke.getKeyStroke(80, 2, false), 2);

        this.jMenuFile.addSeparator();
        this.jMenuFile.add(this.batchUploadTechnics);
        // this.jMenuFile.addSeparator();
        // this.jMenuFile.add(this.newTechnicsPart);

        this.jMenuFile.addSeparator();
        this.jMenuFile.add(this.jMenuFileexit);

        this.jMenuFileexit.setText("退出");

        this.jMenuBar.add(this.jMenuSearch);

        this.jMenuSearch.setText("搜索");

        // this.jMenuSearch.add(this.jMenuSearchTechnics);
        this.jMenuSearch.add(this.jMenuPDSTechnics);
        // this.jMenuSearch.add(this.jMenuSearchProduct);

        // this.jMenuSearchTechnics.setText("搜索本地工艺");
        this.jMenuPDSTechnics.setText("搜索工艺");
        // this.jMenuSearchProduct.setText("搜索整件");

        this.jMenuBar.add(this.jMenuHelp);

        this.jMenuHelp.setText("关于");

        this.jMenuHelp.add(this.jMenuHelpabout);

        this.jMenuHelpabout.setText("关于 工艺规程管理器5.1");

        this.jMenuBar.setBackground(Color.LIGHT_GRAY);

        setJMenuBar(this.jMenuBar);
        addMenuListener();
        initMenuItemIcon();

        this.jMenuHelpabout.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                VersionDialog version = new VersionDialog();
                version.showDialog();

            }
        });

        // 新建普通工艺
        this.createTechnics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    NewTechnicsPart.this.createTechnics();
                } catch (Exception ee) {
                    ee.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "创建工艺出现错误！", "提示", 1);
                }
            }
        });
        // 新建工序
        this.createStep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    NewTechnicsPart.this.createProdcutStep();
                } catch (Exception ee) {

                    ee.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "创建工序出现错误！", "提示", 1);
                }
            }
        });
        // 设置工作空间
        this.workspacesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Boolean flag = NewTechnicsPart.setworkSpace(NewTechnicsPart.this);
                    NewTechnicsPart.this.reSettingData();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "设置工作空间成功，需重新启动编辑器生效！", "提示", 1);
                    dispose();
                } catch (Exception ee) {

                    ee.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "设置工作空间出现错误！", "提示", 1);
                }
            }
        });
        // 批量上载工艺工程
        this.batchUploadTechnics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    CloseJDialog cj = new CloseJDialog(NewTechnicsPart.this);
                    cj.showDialog();

                    XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getCurrentTechnicsNode();
                    if (node != null) {
                        NewTechnicsPart.this.foregoingPanel = null;
                        NewTechnicsPart.this.technicsTreePanel.expandAllNode(node);
                        NewTechnicsPart.this.technicsTreePanel.getTree().setSelectionPath(new TreePath(node.getPath()));
                        NewTechnicsPart.this.xwPartTreePanel.getTree().updateUI();
                    }
                    XWTreeNode rootNode = (XWTreeNode) NewTechnicsPart.this.xwPartTreePanel.getTree().getModel().getRoot();
                    System.out.println("----------------------------------------------------");
                    SwingUtil.printNode(rootNode);
                    System.out.println("----------------------------------------------------");
                    NewTechnicsPart.this.xwPartTreePanel.expandAllNode(rootNode);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "批量上载工艺工程出现错误！", "提示", 1);
                }
            }
        });
        // 复制工序
        this.copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    NewTechnicsPart.this.copy();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "复制工序出现错误！", "提示", 1);
                }
            }
        });

        /**
         * 粘贴工艺文件、工序、工步
         * @修改 杨青
         * @校对 马崇奇
         * @date 2015-6-3
         */
        this.paste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (NewTechnicsPart.this.treeJTabbedPane.getSelectedIndex() == 1) {
                    XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
                    if (node != null) {
                        XWTreeObject obj = node.getObject();

                        try {
                            if (obj instanceof XWTechnicsTreeObject) {
                                NewTechnicsPart.this.pasteProductTechnics();
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            JOptionPane.showMessageDialog(NewTechnicsPart.this, "粘贴工序出现错误！", "提示", 1);
                        }

                        try {
                            if (obj instanceof XWStepTreeObject) {
                                NewTechnicsPart.this.pastePace();
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            JOptionPane.showMessageDialog(NewTechnicsPart.this, "粘贴工步出现错误！", "提示", 1);
                        }
                    }
                }

                try {
                    if (NewTechnicsPart.this.treeJTabbedPane.getSelectedIndex() == 0) {

                        NewTechnicsPart.this.pastTechnics();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "粘贴工艺出现错误！", "提示", 1);
                }
            }

        });
        // 我的设置
        this.mySettingItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MySettingDialog(NewTechnicsPart.this);
            }
        });
        // 更新PBOM
        this.updatePBOM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    NewTechnicsPart.this.updatePBOM();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "PBOM更新成功！", "提示", 1);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "PBOM更新时出错！", "提示", 1);
                }
            }
        });
        // 启动PBOM
        this.startPBOM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NewTechnicsPart.this.startPBOM(NewTechnicsPart.this.xwPartTreePanel);
            }
        });
        // 自动创建工序
        this.autoCreateProcedure.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    XWTreeNode node = null;
                    if (NewTechnicsPart.this.treeJTabbedPane.getSelectedIndex() == 1) {
                        node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
                        NewTechnicsPart.this.autoCreateProcedure(node);
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "自动创建工序出现错误！", "提示", 1);
                }
            }
        });
        // 启动工艺简图工具
        this.runDiagram.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    NewTechnicsPart.this.runDiagramProgram();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "启动工艺简图工具出现错误！", "提示", 1);
                }
            }
        });
        // 启动中间模型工具
        this.runMiddleModule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    NewTechnicsPart.this.runMiddleModuleProgram();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "启动中间模型工具出现错误！", "提示", 1);
                }
            }
        });
        // 启动可视化工具
        this.runVisual.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    NewTechnicsPart.this.runVisualProgram();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "启动可视化工具出现错误！", "提示", 1);
                }
            }
        });
        // 启动装配动画工具
        this.runAssembleCartoon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    NewTechnicsPart.this.runAssembleCartoonProgram();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "启动装配动画工具出现错误！", "提示", 1);
                }
            }
        });
        // 本地工艺模板维护
        this.templateMaintain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NewTechnicsPart.this.templateMaintain();
            }
        });
        // 本地工序模板维护
        this.procedureTempletMaintain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NewTechnicsPart.this.procedureTempletMaintain();
            }
        });
        // 个人工艺常用语库维护
        this.terminology.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NewTechnicsPart.this.maintenancePersonalTerminology();
            }
        });
        // 下载个人工艺常用语模板
        this.downloadCsTemplate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    NewTechnicsPart.this.downloadCsTemplate();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "下载个人工艺常用语模板失败！", "提示", 1);
                }
            }
        });
        // 导入个人工艺常用语库
        this.importCommonString.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    NewTechnicsPart.this.importCommonString();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "个人工艺常用语导入失败！", "提示", 1);
                }
            }
        });
        // 导出个人工艺常用语库
        this.exportCommonString.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    NewTechnicsPart.this.exportCommonString();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "个人工艺常用语导出失败！", "提示", 1);
                }
            }
        });
        // 查看历史工艺
        this.viewHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //NewTechnicsPart.this.viewHistoryTechnics();
                NewTechnicsPart.this.qmViewHistorytechnics();
            }
        });
        // 工艺合编
        this.technicsCoWork.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NewTechnicsPart.this.technicsCoWork();
            }
        });
        // 提交工艺合编
        this.submitUniteTechnics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    NewTechnicsPart.this.submitUniteTechnics();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        });
        // 工艺路线确认
        this.technicsConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NewTechnicsPart.this.technicsRouteConfirm();
            }
        });
        // 删除
        this.delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    NewTechnicsPart.this.delete();
                } catch (Exception ee) {
                    ee.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "删除操作出现错误！", "提示", 1);
                }
            }
        });
        // 打开新工艺规程管理器
        this.newTechnicsPart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new NewTechnicsPart(false);
            }
        });
        this.jMenuFileexit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NewTechnicsPart.this.exitProcess();
            }
        });
        this.jMenuPDSTechnics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NewTechnicsPart.this.searchServerTechnics();
            }
        });
        this.signed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    NewTechnicsPart.this.commitForSignedThread("3");
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺规程提交签审出现错误！", "提示", 1);
                }
            }
        });
        this.signed2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    NewTechnicsPart.this.commitForSignedThread("5");
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺规程提交签审出现错误！", "提示", 1);
                }
            }
        });
        this.completeTask.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    NewTechnicsPart.this.completeTechnicsTask();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "完成工艺任务出现错误！", "提示", 1);
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺规程提交签审出现错误！", "提示", 1);
                }
            }
        });
        this.reviewTechnics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    XWTreeNode node = null;
                    if (NewTechnicsPart.this.treeJTabbedPane.getSelectedIndex() == 1) {
                    	node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
                    	NewTechnicsPart.this.reviewTechnicsThread(node);
                    }else if(NewTechnicsPart.this.treeJTabbedPane.getSelectedIndex() == 0 && "SOP".equals(EditorConfig.startType)){
                    	node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
                    	NewTechnicsPart.this.reviewTechnicsThread(node);
                    }
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "预览工艺出现错误！", "提示", 1);
                    e1.printStackTrace();
                }
            }
        });
        this.pdfReviewTechnics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    if (NewTechnicsPart.this.treeJTabbedPane.getSelectedIndex() == 0) {
                        XWTreeNode sNode = NewTechnicsPart.this.xwPartTreePanel.getSelectedTreeNode();
                        XWTreeObject object = sNode.getObject();
                        if (object instanceof ReportTechnicsTreeObject) {
                            JOptionPane.showMessageDialog(NewTechnicsPart.this, "请选中当前节点，点击右键PDF工艺预览！", "提示", 1);
                            return;
                        }
                    }
                    XWTreeNode node = null;
                    if (NewTechnicsPart.this.treeJTabbedPane.getSelectedIndex() == 1 || NewTechnicsPart.this.treeJTabbedPane.getSelectedIndex() == 0) {
                        // add by machongqi 2015-6-15
                        if (NewTechnicsPart.this.tecnicsJTabbedPane.getTabCount() >= 2) {
                            int selectIndex = NewTechnicsPart.this.tecnicsJTabbedPane.getSelectedIndex();
                            if (selectIndex == 1) {
                                NewTechnicsPart.this.tecnicsJTabbedPane.setSelectedIndex(0);
                                NewTechnicsPart.this.tecnicsJTabbedPane.setSelectedIndex(1);
                            } else if (selectIndex == 0) {
                                NewTechnicsPart.this.tecnicsJTabbedPane.setSelectedIndex(1);
                                NewTechnicsPart.this.tecnicsJTabbedPane.setSelectedIndex(0);
                            }
                        }
                        //add by machongqi end
                        node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
                        NewTechnicsPart.this.pdfReviewTechnicsThread(node);
                    }
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "预览工艺出现错误！", "提示", 1);
                    e1.printStackTrace();
                }
            }
        });
        this.technicsUpload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NewTechnicsPart.this.technicsUploadThread();
//                NewTechnicsPart.this.newTechnicsUploadThread();
            }
        });

        this.technicsUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    NewTechnicsPart.this.technicsUpdate();
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺更新时出现错误！", "提示", 1);
                    e1.printStackTrace();
                }
            }
        });
        this.tecnicsJTabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                NewTechnicsPart.this.namePanel.deleteObservers();
                try {
                    int index = NewTechnicsPart.this.tecnicsJTabbedPane.getSelectedIndex();
                    if (index == 1) {
                        XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
                        if (node != null) {
                            NewTechnicsPart.this.judgeValueModified();
                            NewTechnicsPart.this.createTechnicsRoute();
                            NewTechnicsPart.this.showData(node, null);
                            NewTechnicsPart.this.getQuickCreateProcedureJPanel().setTableValue(NewTechnicsPart.this.technicsTreePanel.getCurrentTechnicsNode().getObject().getTreeCellData());
                            NewTechnicsPart.this.namePanel.addObserver(NewTechnicsPart.this.getQuickCreateProcedureJPanel());
                        }
                    } else if (index == 0) {
                        NewTechnicsPart.this.getQuickCreateProcedureJPanel().updataTechnicsData();
                        NewTechnicsPart.this.createTechnicsRoute();

                        JPanel com = NewTechnicsPart.this.foregoingPanel;
                        if (com != null) {
                            NewTechnicsPart.this.foregoingPanel = null;
                            if ((com instanceof NewTechnicsMasterJPanel_XW)) {
                                NewTechnicsMasterJPanel_XW masterPanel = (NewTechnicsMasterJPanel_XW) com;
                                XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getCurrentTechnicsNode();
                                if (node != null) {
                                    masterPanel.clearUI();
                                    NewTechnicsPart.this.technicsTreePanel.getTree().setSelectionPath(new TreePath(node.getPath()));
                                    NewTechnicsPart.this.showData(node, null);
                                }
                            } else if ((com instanceof TechnicsStepJPanel_XW)) {
                                TechnicsStepJPanel_XW stepPanel = (TechnicsStepJPanel_XW) com;
                                NewTechnicsPart.this.namePanel.addObserver(stepPanel);
                                String bsoID = stepPanel.getNode().getObject().getTreeCellData().attributeValue("bsoID");
                                XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getStepNode(bsoID);
                                if (node != null) {
                                    stepPanel.clearUI();
                                    NewTechnicsPart.this.technicsTreePanel.getTree().setSelectionPath(new TreePath(node.getPath()));
                                    NewTechnicsPart.this.showData(node, null);
                                } else {
                                    NewTechnicsPart.this.clearRightContent();
                                    XWTreeNode techNode = NewTechnicsPart.this.technicsTreePanel.getCurrentTechnicsNode();
                                    if (techNode != null) {
                                        NewTechnicsPart.this.technicsTreePanel.getTree().setSelectionPath(new TreePath(techNode.getPath()));
                                        NewTechnicsPart.this.showData(techNode, null);
                                    }
                                }
                            }
                        }
                    }else if(index == 2){
                        XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
                        if (node != null) {
                            NewTechnicsPart.this.judgeValueModified();
                        }
                    }else if(index == 3){
                        XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
                        if (node != null) {
                            XWTreeObject treeObject = node.getObject();
                            Element data = treeObject.getTreeCellData();
                            peiTaoListTableJPanel.setTableValues(data);
                        }
                    }else if(index == 4){
                        XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
                        if (node != null) {
                            judgeValueModified();

                            XWTreeObject treeObject = node.getObject();
                            Element data = treeObject.getTreeCellData();
                            JPanel com = NewTechnicsPart.this.foregoingPanel;
                            if (com != null) {
                                //NewTechnicsPart.this.foregoingPanel = null;
                                if ((com instanceof NewTechnicsMasterJPanel_XW)) {
                                    gongYiCanShuTableJPanel.setTableValues(data,null);
                                }else if ((com instanceof TechnicsStepJPanel_XW)) {
                                    gongYiCanShuTableJPanel.setTableValues(data,NewTechnicsPart.this.getTechnicsStepJPanel().getSpeCharPanel());
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "图形显示出现错误！", "提示", 1);
                }
            }
        });
        this.treeJTabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int index = treeJTabbedPane.getSelectedIndex();
                for (int i = 0; i < 11; i++) {
                    if (index != i) {
                        treeJTabbedPane.setForegroundAt(i, Color.BLACK);
                    }
                }
                treeJTabbedPane.setForegroundAt(index, Color.red);
                NewTechnicsPart.this.universalToolBar.setToolBarEnabled();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (NativeInterface.isOpen()) {
                    NativeInterface.close();
                }
                NewTechnicsPart.this.exitProcess();

                // 注销license
                // LicenseHelper helper = new LicenseHelper();
                // helper.dischargeLicense();
            }
        });
        this.universalToolBar.setToolBarEnabled();
        if (!this.isMain)
            setVisible(true);

        long endTime = System.currentTimeMillis();
        logger.debug("初始化界面耗时：" + (endTime-startTime) + " ms");
    }
    private void loadSopData(){
        try {
            Document doc = DocumentHelper.parseText(sopPartXml);
            if (doc != null) {
                startAnimFrame.setHeaderMessage("加载PBOM树");
                this.xwPartTreePanel.loadParts(doc);
                startAnimFrame.setHeaderMessage("完成加载PBOM树");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadData() {
        if (flag) {
            long startTime1 = System.currentTimeMillis();
            downloadTechnics(technicsPartList, "rework");
            long endTime1 = System.currentTimeMillis();
            logger.debug("downloadTechnics rework 耗时：" + (endTime1-startTime1) + " ms");

            long startTime2 = System.currentTimeMillis();
            downloadTechnics(technicsPartList, "temp");
            long endTime2 = System.currentTimeMillis();
            logger.debug("downloadTechnics temp 耗时：" + (endTime2-startTime2) + " ms");

            long startTime3 = System.currentTimeMillis();
            downloadTechnics(technicsPartList, "normal");
            long endTime3 = System.currentTimeMillis();
            logger.debug("downloadTechnics normal 耗时：" + (endTime3-startTime3) + " ms");
        } else {
            if (!isDownload) {
                if ("4".equals(runType)) {
                    downloadTechnics(technicsPartList, "rework");
                } else if ("5".equals(runType)) {
                    downloadTechnics(technicsPartList, "temp");
                }
            }
        }
        com.glaway.mpm.controller.CancelPartHandler.frame = this;
        if (pbomOid != null) {
            String[] str = {pbomOid};
            startAnimFrame.setHeaderMessage("获取顶层PBOM的xml数据");
            logger.debug("开始获取顶层PBOM的xml数据...");
            long pbomXmlStartTime1 = System.currentTimeMillis();
            pbomBytes = PbomUtil.generatePbomBytes(str);
//			pbomBytes = PBomIntf.getPBomXML(pbomOid);
            long pbomXmlEndTime = System.currentTimeMillis();
            logger.debug("获取顶层PBOM的xml数据耗时：" + (pbomXmlEndTime - pbomXmlStartTime1) + " ms");
            logger.debug("pbomBytes="+pbomBytes);
            if (pbomBytes == null) {
                logger.debug("bytes == null");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(System.getProperty("user.home") + "//pbom.xml");
                fos.write(pbomBytes);
                fos.close();
            } catch (FileNotFoundException e) {
                logger.debug(e.getMessage());
            } catch (IOException e) {
                logger.debug(e.getMessage());
            }
            if (pbomBytes != null) {
                try {
                    List<String> responseOid = new ArrayList<String>();
                    Document doc = BomXMLUtil.getDocument(pbomBytes);
                    logger.debug("childPartOid="+childPartOid);
                    if ((childPartOid != null) && (childPartOid.trim().length() > 0)) {
                        String[] parts;
                        if ("3".equals(runType)) {
                            parts = childPartOid.split(",");
                            if (parts != null) {
                                for (int i = 0; i < parts.length; i++) {
                                    responseOid.add(parts[i]);
                                }
                            }
                        } else {
                            parts = new String[1];
                            parts[0] = childPartOid;
                            responseOid.add(childPartOid);
                        }

                        for (String temp : parts) {
                            //获取顶层零部件
                            Element mainPart = BomXMLUtil.getMainPart(BomXMLUtil.getProduct(doc));
                            Element part = BomXMLUtil.findPart(mainPart, temp);
                            if (part != null) {
                                partNumber = part.attributeValue("partNumber");
                                logger.debug("===========partNumber>>>>"+partNumber);
                                if (changeTechnicsId != null && !"".equals(changeTechnicsId)) {
                                    TechnicsUtil.downloadTechncisById(changeTechnicsId);
                                } else {
                                    logger.debug("开始下载工艺文件数据包并解压数据包...");
                                    long startTime = System.currentTimeMillis();
                                    downloadTechnics(partNumber, part);
                                    long endTime = System.currentTimeMillis();
                                    logger.debug("下载工艺文件数据包并解压数据包总共耗时："+(endTime-startTime)+" ms");
                                }
                            }
                        }

//                        updateTechnics(pbomBytes);
                    } else {
                        Set<String> set = updateTechnics(pbomBytes);
                        for (String temp : set) {
                            responseOid.add(temp);
                        }
                    }
                    if (doc != null) {
                        startAnimFrame.setHeaderMessage("加载PBOM树");
                        logger.debug("开始加载PBOM树...");
                        long startTime = System.currentTimeMillis();
                        this.xwPartTreePanel.loadParts(doc);
                        long endTime = System.currentTimeMillis();
                        logger.debug("加载PBOM树总共耗时："+(endTime-startTime)+" ms");
                        startAnimFrame.setHeaderMessage("完成加载PBOM树");
                    }

                    SwingUtil.expandToResponsePart(responseOid, xwPartTreePanel.getTree());
                    if ((tmpTechnicsNumber != null) && (tmpTechnicsNumber.trim().length() > 0)) {
                        XWTreeNode node = this.xwPartTreePanel.findTechnicsNode(tmpTechnicsNumber);
                        if (node != null) {
                            TreePath path = new TreePath(node.getPath());
                            this.xwPartTreePanel.getTree().scrollPathToVisible(path);
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void reviewTechnicsThread(final XWTreeNode node) throws Exception {
        final VaActionProgressBar progressBar = new VaActionProgressBar(NewTechnicsPart.this, "工艺预览", "正在工艺预览,请等待...", "工艺预览中");
        Thread thread = new Thread() {
            public void run() {
                if (node != null) {
                    try {
                        XWTreeObject xo = node.getObject();
                        Element ele = xo.getTreeCellData();
                        if ((xo instanceof XWTechnicsTreeObject)) {
                            logger.debug("AAAA=====");
                            String technicsNumber = ele.attributeValue("technicsNumber");
                            String technicsName = ele.attributeValue("technicsName");
                            String technicsCategory = ele.attributeValue("technicsCategory");
                            String path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber, technicsName, technicsCategory);
                            if("SOP".equals(EditorConfig.startType)){
                                SopProcessUtil.previewSop(path);
                            }else{
                                FilesUtil.handTechnicsWordFile(technicsNumber, progressBar);
                                TechnicsPreview.preview(path);
                            }

                        } else if ((xo instanceof TechnicsMessageTreeObject)) {
                            TechnicsMessageTreeObject tmt = (TechnicsMessageTreeObject) xo;
                            String technicsNumber = tmt.getTechNumber();
                            String technicsName = tmt.getTechName();
                            String technicsCategory = tmt.getTechnicsCategory();
                            String path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber, technicsName, technicsCategory);
                            if("SOP".equals(EditorConfig.startType)){
                                SopProcessUtil.previewSop(path);
                            }else{
                                FilesUtil.handTechnicsWordFile(technicsNumber, progressBar);
                                TechnicsPreview.preview(path);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                progressBar.finish();
                progressBar.setVisible(false);
            }
        };
        thread.start();
        progressBar.setVisible(true);
    }

    /**
     * PDF 工艺预览
     *
     * @param node
     * @throws Exception
     */
    public void pdfReviewTechnicsThread(final XWTreeNode node) throws Exception {
        // 如果工艺文件已经批准，则直接打开PDFPreview.pdf
        XWTreeObject xo = node.getObject();
        Element ele = xo.getTreeCellData();
        String lifecycle = ele.attributeValue("lifecycle");
        if ("已批准".equals(lifecycle)) {
            String techDir = WorkSpaceUtil.getTechnicsDirectory(ele.attributeValue("technicsNumber"));
            if (techDir != null && !"".equals(techDir)) {
                String filePath = techDir + File.separator + "PDFPreview.pdf";
                System.out.println("dizhi-----?>>>" + filePath);
                GenTechnicsPdfProcessor.openFile(filePath);
            }
        } else {
            final VaActionProgressBar progressBar = new VaActionProgressBar(NewTechnicsPart.this, "PDF 工艺预览", "正在执行PDF 工艺预览,请等待...", "PDF 工艺预览中");
            Thread thread = new Thread() {
                public void run() {
                    if (node != null) {
                        try {
                            XWTreeObject xo = node.getObject();
                            Element ele = xo.getTreeCellData();

                            //判断是报表类工艺文件还是一般的工艺文
                            //ReportTechnicsTreeObject是报表类工艺文件
                            if (xo instanceof ReportTechnicsTreeObject) {
                                String technicsNumber = ele.attributeValue("technicsNumber");
                                String path = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
                                //	System.out.println("路径-------------》》》"+path);

                                PDFPreviewFactory.previewForReport(path, true);
                            } else {
                                String technicsType = ele.attributeValue("technicsType");

                                //获取该零部件下所有子件（一层，标准件、自制件、元器件、外购件）信息
                                XWTreeNode treeNode = xwPartTreePanel.getSelectedTreeNode().getP();
                                XWTreeObject partObj = treeNode.getObject();
                                List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                                if (partObj instanceof XWPartTreeObject) {
                                    if ("试验工艺".equals(technicsType)) {
                                        XWTreeNode tNode = treeNode.getP();
                                        if (tNode != null) {
                                            Map<String, String> map = null;
                                            if (tNode.getObject() instanceof XWPartTreeObject) {
                                                XWPartTreeObject childObj = (XWPartTreeObject) tNode.getObject();
                                                Element pele = childObj.getTreeCellData();
                                                map = new HashMap<String, String>();
                                                map.put("partNumber", pele.attributeValue("partNumber"));
                                                map.put("partName", pele.attributeValue("partName"));
                                                map.put("useCount", pele.attributeValue("useCount"));
                                                map.put("XHPH", pele.attributeValue("XHPH"));
                                                map.put("CSIZE", pele.attributeValue("CSIZE"));
                                                map.put("MTYPE", pele.attributeValue("MTYPE"));
                                                list.add(map);
                                            }
                                        }
                                    } else {
                                        Enumeration<XWTreeNode> enu = treeNode.children();
                                        Map<String, String> map = null;
                                        while (enu.hasMoreElements()) {
                                            XWTreeNode tNode = enu.nextElement();
                                            if (tNode.getObject() instanceof XWPartTreeObject) {
                                                XWPartTreeObject childObj = (XWPartTreeObject) tNode.getObject();
                                                Element pele = childObj.getTreeCellData();
                                                map = new HashMap<String, String>();
                                                map.put("partNumber", pele.attributeValue("partNumber"));
                                                map.put("partName", pele.attributeValue("partName"));
                                                map.put("useCount", pele.attributeValue("useCount"));
                                                map.put("XHPH", pele.attributeValue("XHPH"));
                                                map.put("CSIZE", pele.attributeValue("CSIZE"));
                                                map.put("MTYPE", pele.attributeValue("MTYPE"));
                                                list.add(map);
                                            }
                                        }
                                    }
                                }

                                if ((xo instanceof XWTechnicsTreeObject)) {
                                    String technicsNumber = ele.attributeValue("technicsNumber");
                                    String technicsName = ele.attributeValue("technicsName");
                                    String technicsCategory = ele.attributeValue("technicsCategory");
                                    String path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber, technicsName, technicsCategory);

                                    //生成MHT文件
                                    FilesUtil.handTechnicsWordFile(technicsNumber, progressBar);

//									PDFTechnicsPreview.preview(path);
                                    progressBar.setHeaderMessage("开始生成PDF");
                                    int pages = PDFPreviewFactory.preview(path, true, list);
                                    if(pages==-1){
                                    	progressBar.finish();
                                    	progressBar.setVisible(false);
                                    	return;
                                    }
                                    progressBar.setHeaderMessage("结束生成PDF");
                                } else if ((xo instanceof TechnicsMessageTreeObject)) {
                                    TechnicsMessageTreeObject tmt = (TechnicsMessageTreeObject) xo;
                                    String technicsNumber = tmt.getTechNumber();
                                    String technicsName = tmt.getTechName();
                                    String technicsCategory = tmt.getTechnicsCategory();
                                    String path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber, technicsName, technicsCategory);
                                    FilesUtil.handTechnicsWordFile(technicsNumber, progressBar);
                                    progressBar.setHeaderMessage("开始生成PDF");
                                    PDFPreviewFactory.preview(path, true, list);
                                    progressBar.setHeaderMessage("结束生成PDF");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    progressBar.finish();
                    progressBar.setVisible(false);
                }
            };
            thread.start();
            progressBar.setVisible(true);
        }
    }

    public void reviewTechnics(XWTreeNode node) throws Exception {
        if (node != null) {
            XWTreeObject xo = node.getObject();
            Element ele = xo.getTreeCellData();
            if ((xo instanceof XWTechnicsTreeObject)) {
                logger.debug("AAAA=====");
                String technicsNumber = ele.attributeValue("technicsNumber");
                String technicsName = ele.attributeValue("technicsName");
                String technicsCategory = ele.attributeValue("technicsCategory");
                String path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber, technicsName, technicsCategory);
                FilesUtil.handTechnicsWordFile(technicsNumber);
                TechnicsPreview.preview(path);
            } else if ((xo instanceof TechnicsMessageTreeObject)) {
                TechnicsMessageTreeObject tmt = (TechnicsMessageTreeObject) xo;
                String technicsNumber = tmt.getTechNumber();
                String technicsName = tmt.getTechName();
                String technicsCategory = tmt.getTechnicsCategory();
                String path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber, technicsName, technicsCategory);
                FilesUtil.handTechnicsWordFile(technicsNumber);
                TechnicsPreview.preview(path);
            }
        }
    }

    /**
     * 保存工艺文件之前进行校验
     *
     * @param element
     * @param partMap
     * @param progressBar
     * @return
     * @throws InvocationTargetException
     * @throws WTException
     * @throws RemoteException
     */
    public static boolean check(Element element, VaActionProgressBar progressBar, NewTechnicsPart frame, Component parentComponent) throws RemoteException, WTException, InvocationTargetException {

        String pplanNumber = element.attributeValue("pplanNumber");
        // 检查工序名称，工步内容是否填写
        if(!"".equals(TechnicsUtil.checkTechnics(frame, element))) {
            progressBar.finish();
            progressBar.setVisible(false);
            return false;
        }
        // 检查前置工序是否合理 add by zhuhao 2017.12.05
        String stepNumber = TechnicsUtil.checkBeforeStep(frame, element);
        if(!"".equals(stepNumber)) {
            progressBar.finish();
            progressBar.setVisible(false);
            JOptionPane.showMessageDialog(parentComponent, "工艺" + pplanNumber + "，" + stepNumber + "工序存在不合理前置工序！");
            return false;
        }
        //检查preBsoID nextBsoID 是否都属于现有的bsoID
        String checkBsoID = TechnicsUtil.checkBsoID(element);
        if(!"".equals(checkBsoID)) {
            progressBar.finish();
            progressBar.setVisible(false);
            if("duplicateBsoIds".equals(checkBsoID)) {
                JOptionPane.showMessageDialog(parentComponent, "工艺" + pplanNumber + "存在重复的BsoId工序，系统已自动为您重置重复的ID，但需要重新生成工艺流程图。操作指示：工艺路线页签-工艺流程图-刷新按钮");
            } else {
                JOptionPane.showMessageDialog(parentComponent, "工艺" + pplanNumber + "有已删除的工序，需要重新生成工艺流程图。操作指示：工艺路线页签-工艺流程图-刷新按钮");
            }
            return false;

        }

        //上载校验
//        String message = TechnicsUtil.checkUpload(element);
//        if (!"".equals(message)) {
//            progressBar.finish();
//            progressBar.setVisible(false);
//            JOptionPane.showMessageDialog(null, message);
//            return false;
//
//        }

        //规范工步stepNumber顺序
        TechnicsUtil.standardPaceStepNumber(element);

        // 校验PBOM工艺数量是否大于实际参装数量
        // try {
        // Map<String, String> partMap = BomXMLUtil.getPartMap(pbomBytes,
        // parentPartNumber);
        // Map<String, PDFBuilder.CzjPart> czjPartMap =
        // PDFBuilder.getAllCzjMap(element);
        // for (Map.Entry<String, String> entry : partMap.entrySet()) {
        // String partNumber = entry.getKey();
        // if (czjPartMap.containsKey(partNumber)) {
        // double czjCount = czjPartMap.get(partNumber).getCount();
        // double partGysl = Double.valueOf(entry.getValue());
        // if (czjCount > partGysl) {
        // progressBar.finish();
        // progressBar.setVisible(false);
        // JOptionPane.showMessageDialog(null, partNumber + "参装数量不合理，请重新进行参装！");
        // return;
        // }
        // }
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // 校验Word转PDF
        boolean isTransPdf = TechnicsUtil.checkWordTransPDF(element);
        if(!isTransPdf) {
            progressBar.finish();
            progressBar.setVisible(false);
            JOptionPane.showMessageDialog(parentComponent, "Word转换PDF失败，请尝试通过以下方式解决!\n" + "1、请检查确认jacob插件是否安装;\n" + "2、若1已安装，请尝试升级本地Word至Word2010;\n" + "3、若以上都不行，请联系管理员！");
            return false;
        }
        String technicsNumber = XmlUtility.getAttributeValue(element, "technicsNumber");
        String docState = TechnicsIntf.getDocumentStateByNumber(technicsNumber);
        if(!"".equals(docState) && !"正在工作".equals(docState) && !"修改中".equals(docState)) {
            JOptionPane.showMessageDialog(parentComponent, "工艺文件" + pplanNumber + "已经提交签审，不能再修改！");
            progressBar.finish();
            progressBar.setVisible(false);
            return false;
        }

        return true;
    }

    private HashMap<String, String> getAttributes(Element element) {
        HashMap<String, String> map = new HashMap<String, String>();
        String technicsName = element.attributeValue("technicsName");
        String partNumber = element.attributeValue("partNumber");
        String partOid = element.attributeValue("partOid");
        String partName = element.attributeValue("partName");
        String partType = element.attributeValue("partType");
        String pplanNumber = element.attributeValue("pplanNumber");
        String technicsType = element.attributeValue("technicsType");
        String technicsNumber = element.attributeValue("technicsNumber");
        map.put("oid", partOid);
        map.put("partNumber", partNumber);
        map.put("partName", partName);
        map.put("partType", partType);
        map.put("technicsType", technicsType);
        map.put("technicsNumber", technicsNumber);
        map.put("technicsName", technicsName);
        map.put("pplanNumber", pplanNumber);
        map.put("SECRET", element.attributeValue("SECRET"));
        if ("SOP标准操作规程".equals(technicsType)) {
            map.put("SopNumber", element.attributeValue("SopNumber"));
            map.put("SpecializedType", element.attributeValue("SpecializedType"));
            map.put("ProceduceName", element.attributeValue("ProceduceName"));
            map.put("OperationJob", element.attributeValue("OperationJob"));
            map.put("CustomArea", element.attributeValue("CustomArea"));
            map.put("Term", element.attributeValue("Term"));
            map.put("ProfessionalCode", element.attributeValue("ProfessionalCode"));
            map.put("GONGXUJIANHAO", element.attributeValue("GONGXUJIANHAO"));
            map.put("department", element.attributeValue("department"));
            map.put("description", element.attributeValue("description"));
        }
        String CINDEX = element.attributeValue("CINDEX");
        String MINDEX = element.attributeValue("MINDEX");
        String PINDEX = element.attributeValue("PINDEX");
        String PPLANTYPE = element.attributeValue("PPLANTYPE");
        String ZFFLAG = element.attributeValue("ZFFLAG");
        String DEPT = element.attributeValue("DEPT");
        String PHASE_CODE = element.attributeValue("PHASE_CODE");
        String KEYCOMPONENT = element.attributeValue("KEYCOMPONENT");
        String BATCH = element.attributeValue("PCNO");//批次号，add by liangbo
        String BIAOSHI = element.attributeValue("BIAOSHI");//
        String isCLDE = XmlUtility.isCLDE(element);
        map.put("isCLDE", isCLDE);
        map.put("CINDEX", CINDEX);
        map.put("MINDEX", MINDEX);
        map.put("PINDEX", PINDEX);
        map.put("PPLANTYPE", PPLANTYPE);
        map.put("ZFFLAG", ZFFLAG);
        map.put("DEPT", DEPT);
        map.put("PHASE_CODE", PHASE_CODE);
        map.put("KEYCOMPONENT", KEYCOMPONENT);
        map.put("BATCH", BATCH);
        map.put("BIAOSHI", BIAOSHI);
        map.put("changeOrderOid", changeOrderOid);

        String bzyjNum = element.attributeValue("bzyjNum");
        String bzyjName = element.attributeValue("bzyjName");
        String yyfl = element.attributeValue("yyfl");
        map.put("bzyjNum", bzyjNum);
        map.put("bzyjName", bzyjName);
        map.put("yyfl", yyfl);
        return map;
    }
    public void newTechnicsUploadThread() {
        final VaActionProgressBar progressBar = new VaActionProgressBar(NewTechnicsPart.this, "工艺上载", "正在工艺上载,请等待...", "工艺上载中");
        Thread thread = new Thread() {
            public void run() {
                XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
                XWTreeNode reportNode = NewTechnicsPart.this.xwPartTreePanel.getSelectedTreeNode();
                Map<String, String> partMap = new HashMap<String, String>();
                if (reportNode != null) {
                    XWTreeNode parent = (XWTreeNode) reportNode.getParent();
                    XWTreeObject parentObj = parent.getObject();
                    if (parentObj instanceof XWPartTreeObject) {
                        Element partElenet = parentObj.getTreeCellData();
                        List<Element> childProducts = BomXMLUtil.getChildProducts(partElenet);
                        for (Element element : childProducts) {
                            partMap.put(element.attributeValue("partNumber"), element.attributeValue("gysl"));
                        }

                    }

                }
                if (node != null) {
                    XWTreeObject xo = node.getObject();
                    if ((xo instanceof XWTechnicsTreeObject)) {
                        Element element = xo.getTreeCellData();
                        //检查工序名称，工步内容是否填写
                        if (!"".equals(TechnicsUtil.checkTechnics(NewTechnicsPart.this, element))) {
                            progressBar.finish();
                            progressBar.setVisible(false);
                            return;
                        }
                        //检查前置工序是否合理 add by zhuhao 2017.12.05
                        String stepNumber = TechnicsUtil.checkBeforeStep(NewTechnicsPart.this, element);
                        if (!"".equals(stepNumber)) {
                            progressBar.finish();
                            progressBar.setVisible(false);
                            JOptionPane.showMessageDialog(null, stepNumber + "工序存在不合理前置工序！");
                            return;
                        }
                        //校验PBOM工艺数量是否大于实际参装数量
//                        try {
//                            Map<String, String> partMap = BomXMLUtil.getPartMap(pbomBytes, parentPartNumber);
//                            Map<String, PDFBuilder.CzjPart> czjPartMap = PDFBuilder.getAllCzjMap(element);
//                            for (Map.Entry<String, String> entry : partMap.entrySet()) {
//                                String partNumber = entry.getKey();
//                                if (czjPartMap.containsKey(partNumber)) {
//                                    double czjCount = czjPartMap.get(partNumber).getCount();
//                                    double partGysl = Double.valueOf(entry.getValue());
//                                    if (czjCount > partGysl) {
//                                        progressBar.finish();
//                                        progressBar.setVisible(false);
//                                        JOptionPane.showMessageDialog(null, partNumber + "参装数量不合理，请重新进行参装！");
//                                        return;
//                                    }
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }

                        String technicsNumber = element.attributeValue("technicsNumber");
                        Document doc = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(technicsNumber);
                		Element technicsElement = XmlUtility.getTechnicsElement(doc);
                        // 校验Word转PDF
                        boolean isTransPdf = TechnicsUtil.checkWordTransPDF(technicsElement);
                        if (!isTransPdf) {
                            progressBar.finish();
                            progressBar.setVisible(false);
                            JOptionPane.showMessageDialog(null, "Word转换PDF失败，请尝试通过以下方式解决!\n" + "1、请检查确认jacob插件是否安装;\n" + "2、若1已安装，请尝试升级本地Word至Word2010;\n" + "3、若以上都不行，请联系管理员！");
                            return;
                        }
                        try {
                            String docState = TechnicsIntf.getDocumentStateByNumber(technicsNumber);
                            System.out.println("----------------------docState-------------------------" + docState);
                            if (!"".equals(docState) && !"正在工作".equals(docState) && !"修改中".equals(docState)) {
                                JOptionPane.showMessageDialog(NewTechnicsPart.this, "此工艺文件已经提交签审，不能再修改！");
                                progressBar.finish();
                                progressBar.setVisible(false);
                                return;
                            }
                        } catch (WTException e2) {
                            e2.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }

                        RemarkJDialog remarkJDialog = new RemarkJDialog("工艺上载", NewTechnicsPart.this);
                        String remark = remarkJDialog.showDialog();
                        if (remark == null) {
                            progressBar.finish();
                            progressBar.setVisible(false);
                            return;
                        }

                        String technicsCategory = element.attributeValue("technicsCategory");
                        String technicsName = element.attributeValue("technicsName");
                        String version = element.attributeValue("version");
                        String technicsType = element.attributeValue("technicsType"); // 工艺类型
                        if ((version == null) || (version.equals(""))) {
                            version = "1.0";
                        }
                        String pdfFile = null;
                        String path = null;

                        try {
                            path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber, technicsName, technicsCategory);
                        } catch (Exception e2) {
                            // TODO Auto-generated catch block
                            e2.printStackTrace();
                        }
                        pdfFile = path + File.separator + "PDFPreview.pdf";
                        File floder1 = new File(pdfFile);
                        if (floder1.exists()) {
                            boolean flag = floder1.delete();
                            if (!flag) {
                                JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺名称为:" + technicsName + "的PDF文件正在使用，请先关闭再上载！");
                                progressBar.finish();
                                progressBar.setVisible(false);
                                return;
                            }
                        }


                        try {
                            byte[] bytes;
                            String technicsDirectory = "";
                            String technicsFolderName = technicsDirectory.substring(technicsDirectory.lastIndexOf("\\") + 1, technicsDirectory.length());
                            String partNumber = element.attributeValue("partNumber");
                            String partOid = element.attributeValue("partOid");
                            String partName = element.attributeValue("partName");
                            String partType = element.attributeValue("partType");
                            String pplanNumber = element.attributeValue("pplanNumber");


                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("oid", partOid);
                            map.put("partNumber", partNumber);
                            map.put("partName", partName);
                            map.put("partType", partType);
                            map.put("technicsType", technicsType);
                            map.put("technicsNumber", technicsNumber);
                            map.put("technicsName", technicsName);
                            map.put("pplanNumber", pplanNumber);

                            String CINDEX = element.attributeValue("CINDEX");
                            String MINDEX = element.attributeValue("MINDEX");
                            String PINDEX = element.attributeValue("PINDEX");
                            String PPLANTYPE = element.attributeValue("PPLANTYPE");
                            String ZFFLAG = element.attributeValue("ZFFLAG");
                            String DEPT = element.attributeValue("DEPT");
                            String PHASE_CODE = element.attributeValue("PHASE_CODE");
                            String KEYCOMPONENT = element.attributeValue("KEYCOMPONENT");
                            String BATCH = element.attributeValue("PCNO");//批次号，add by liangbo
                            String BIAOSHI = element.attributeValue("BIAOSHI");
                            String isCLDE = XmlUtility.isCLDE(element);
                            map.put("isCLDE", isCLDE);
                            map.put("CINDEX", CINDEX);
                            map.put("MINDEX", MINDEX);
                            map.put("PINDEX", PINDEX);
                            map.put("PPLANTYPE", PPLANTYPE);
                            map.put("ZFFLAG", ZFFLAG);
                            map.put("DEPT", DEPT);
                            map.put("PHASE_CODE", PHASE_CODE);
                            map.put("KEYCOMPONENT", KEYCOMPONENT);
                            map.put("BATCH", BATCH);
                            map.put("BIAOSHI", BIAOSHI);

                            map.put("changeOrderOid", changeOrderOid);

                            String bzyjNum = element.attributeValue("bzyjNum");
                            String bzyjName = element.attributeValue("bzyjName");
                            String yyfl = element.attributeValue("yyfl");
                            map.put("bzyjNum", bzyjNum);
                            map.put("bzyjName", bzyjName);
                            map.put("yyfl", yyfl);
                            String newVersion = TechnicsIntf.getNextVersion(technicsNumber);
                            XmlUtility.setAttributeValue(element, "version", newVersion);
                            saveProcess(element);

                            try {
                                progressBar.setHeaderMessage("开始生成工艺文件PDF格式！");

                                //获取该零部件下所有子件（一层，标准件、自制件、元器件、外购件）信息
                                XWTreeNode treeNode = xwPartTreePanel.getSelectedTreeNode().getP();
                                XWTreeObject partObj = treeNode.getObject();
                                List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                                if (partObj instanceof XWPartTreeObject) {
                                    if ("试验工艺".equals(technicsType)) {
                                        XWTreeNode tNode = treeNode.getP();
                                        if (tNode != null) {
                                            Map<String, String> map1 = null;
                                            if (tNode.getObject() instanceof XWPartTreeObject) {
                                                XWPartTreeObject childObj = (XWPartTreeObject) tNode.getObject();
                                                Element pele = childObj.getTreeCellData();
                                                map1 = new HashMap<String, String>();
                                                map1.put("partNumber", pele.attributeValue("partNumber"));
                                                map1.put("partName", pele.attributeValue("partName"));
                                                map1.put("useCount", pele.attributeValue("useCount"));
                                                map1.put("XHPH", pele.attributeValue("XHPH"));
                                                map1.put("CSIZE", pele.attributeValue("CSIZE"));
                                                map1.put("MTYPE", pele.attributeValue("MTYPE"));
                                                list.add(map1);
                                            }
                                        }
                                    } else {
                                        Enumeration<XWTreeNode> enu = treeNode.children();
                                        Map<String, String> map2 = null;
                                        while (enu.hasMoreElements()) {
                                            XWTreeNode tNode = enu.nextElement();
                                            if (tNode.getObject() instanceof XWPartTreeObject) {
                                                XWPartTreeObject childObj = (XWPartTreeObject) tNode.getObject();
                                                Element pele = childObj.getTreeCellData();
                                                map2 = new HashMap<String, String>();
                                                map2.put("partNumber", pele.attributeValue("partNumber"));
                                                map2.put("partName", pele.attributeValue("partName"));
                                                map2.put("useCount", pele.attributeValue("useCount"));
                                                map2.put("XHPH", pele.attributeValue("XHPH"));
                                                map2.put("CSIZE", pele.attributeValue("CSIZE"));
                                                map2.put("MTYPE", pele.attributeValue("MTYPE"));
                                                list.add(map2);
                                            }
                                        }
                                    }
                                }

                                File floder = new File(path, "fbtemp");
                                pages = PDFPreviewFactory.preview(path, false, list);
                                pdfFile = path + File.separator + "PDFPreview.pdf";
                                FileUtil.deleteFile(floder);
                                progressBar.setHeaderMessage("结束生成工艺文件PDF格式！");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //将页数pages写入XML
                            XmlUtility.setAttributeValue(element, "pageSize", String.valueOf(pages));
                            String rootpath = path + File.separator + technicsNumber + ".xml";
                            File rootfile = new File(rootpath);
                            XmlUtility.saveDocument(element.getDocument(), rootfile);
                            //打包工艺文件夹并上传
                            progressBar.setHeaderMessage("获取上载流！");
                            if ("rework".equals(technicsCategory)) {
                                bytes = FilesUtil.getReworkTechnicsByte(technicsNumber, technicsName);
                                technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
                            } else if ("temp".equals(technicsCategory)) {
                                bytes = FilesUtil.getTempTechnicsByte(technicsNumber, technicsName);
                                technicsDirectory = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
                            } else {
                                bytes = FilesUtil.getTechnicsByte(technicsNumber, progressBar);
                                technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
                            }
                            progressBar.setHeaderMessage("获取上载流结束！");
                            map.put("page", String.valueOf(pages));
                            progressBar.setHeaderMessage("上载开始！");
                            HashMap<String, String> infoMap = TechnicsIntf.uploadTechnics(bytes, map, version, remark);
                            //add by Mchen 根据工艺文件编号，查询中间表，刷新中间表
                            String oid = infoMap.get("oid");
                            ErpToWCIntf.updateTechnicaQuotaInfo(oid,technicsNumber);
                            //end by Mchen
                            progressBar.setHeaderMessage("上载结束！");

                            try {
                                progressBar.setHeaderMessage("开始上载PDF工艺文件！");
                                File file = new File(pdfFile);
                                TechHelper.uploadAttachForDocument(technicsNumber, "PDFPreview.pdf", file);
                                progressBar.setHeaderMessage("结束上载PDF工艺文件！");
                            } catch (Exception e) {
                                progressBar.setHeaderMessage("上载PDF工艺文件过程中出现错误！");
                                e.printStackTrace();
                            }

                            Vector<UploadTechnics> editTechnics = NewTechnicsPart.this.editTechnics;
                            if (editTechnics != null) {
                                for (UploadTechnics temp : editTechnics) {
                                    if (temp.getTechnicsNumber().equals(technicsNumber)) {
                                        NewTechnicsPart.editTechnics.remove(temp);
                                        break;
                                    }
                                }
                            }

                            progressBar.setHeaderMessage("工艺上载成功！");

                            sysTechnicNodeVesion(node, partNumber, technicsNumber);
                            JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺上载成功！", "提示", 1);
                        } catch (Exception e1) {
                            JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺上载过程中出现错误！", "提示", 1);
                            progressBar.setHeaderMessage("工艺上载过程中出现错误！");
                            e1.printStackTrace();
                        }
                    }
                } else if (reportNode != null) {
                    XWTreeObject xo = reportNode.getObject();
                    if (xo instanceof ReportTechnicsTreeObject) {
                        String path1 = null;
                        Element element = xo.getTreeCellData();
                        String technicsType = element.attributeValue("technicsType");
                        if ("工艺文件目录".equals(technicsType)) {
                            List<Element> dataList = element.elements("dataItemValue");
                            Map<String, String> technicsMap = new HashMap<String, String>();
                            for (Element data : dataList) {
                                String state = data.attributeValue("lifecycle");
                                String technicsNumber = data.attributeValue("fileNumber");
                                String version = data.attributeValue("version");
                                technicsMap.put(technicsNumber, version);
                                if (!"已批准".equals(state)) {
                                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺文件目录中存在未受控的工艺，不能上载！");
                                    progressBar.finish();
                                    progressBar.setVisible(false);
                                    return;
                                }
                            }
                            try {
                                String message = TechnicsIntf.checkTechnicsIsLatest(technicsMap);
                                if (!"".equals(message)) {
                                    int result = JOptionPane.showConfirmDialog(getContentPane(), message + "是否继续上载？", "提示", JOptionPane.YES_NO_OPTION);
                                    if (result != JOptionPane.YES_OPTION) {
                                        progressBar.finish();
                                        progressBar.setVisible(false);
                                        return;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        String technicsNumber = element.attributeValue("technicsNumber");
                        try {
                            String docState = TechnicsIntf.getDocumentStateByNumber(technicsNumber);
                            System.out.println("----------------------docState-------------------------" + docState);
                            if (!"".equals(docState) && !"正在工作".equals(docState) && !"修改中".equals(docState)) {
                                JOptionPane.showMessageDialog(NewTechnicsPart.this, "此工艺文件已经提交签审，不能再修改！");
                                progressBar.finish();
                                progressBar.setVisible(false);
                                return;
                            }
                        } catch (WTException e2) {
                            e2.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }

                        RemarkJDialog remarkJDialog = new RemarkJDialog("工艺上载", NewTechnicsPart.this);
                        String remark = remarkJDialog.showDialog();
                        if (remark == null) {
                            progressBar.finish();
                            progressBar.setVisible(false);
                            return;
                        }


                        String technicsNumber1 = element.attributeValue("technicsNumber");
                        String technicsName1 = element.attributeValue("technicsName");
                        String technicsCategory1 = element.attributeValue("technicsCategory");
                        String technicsType1 = element.attributeValue("technicsType");
                        String pdfFile = null;
                        String path = null;
                        try {
                            path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber1, technicsName1, technicsCategory1);
                        } catch (Exception e2) {
                            // TODO Auto-generated catch block
                            e2.printStackTrace();
                        }
                        pdfFile = path + File.separator + "PDFPreview.pdf";
                        File floder1 = new File(pdfFile);
                        if (floder1.exists()) {
                            boolean flag = floder1.delete();
                            if (!flag) {
                                JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺名称为:" + technicsName1 + "的PDF文件正在使用，请先关闭再上载！");
                                progressBar.finish();
                                progressBar.setVisible(false);
                                return;
                            }
                        }
                        //上载报表类工艺
                        uploadReportTechnics(progressBar, element, remark);
                        try {

                            progressBar.setHeaderMessage("开始生成工艺文件PDF格式！");

//							//获取该零部件下所有子件（一层，标准件、自制件、元器件、外购件）信息
//							XWTreeNode treeNode = xwPartTreePanel.getSelectedTreeNode().getP();
//							XWTreeObject partObj = treeNode.getObject();
//							List<Map<String,String>> list = new ArrayList<Map<String,String>>();


                            File floder = new File(path, "fbtemp");
                            path1 = WorkSpaceUtil.getTechnicsDirectory(technicsNumber1);
                            //PDFPreviewFactory.preview(path, false, list);
                            pages1 = PDFPreviewFactory.previewForReport(path1, false);
                            pdfFile = path1 + File.separator + "PDFPreview.pdf";
                            FileUtil.deleteFile(floder);
                            progressBar.setHeaderMessage("结束生成工艺文件PDF格式！");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            HashMap map = new HashMap();
//						--------------------------再次上载工艺文件(begin)--------------------------------------
                            //将页数pages写入XML
                            XmlUtility.setAttributeValue(element, "pageSize", String.valueOf(pages1));
                            String rootpath = path1 + File.separator + technicsNumber1 + ".xml";
                            File rootfile = new File(rootpath);
                            XmlUtility.saveDocument(element.getDocument(), rootfile);
                            //再次打包工艺文件夹并上传
                            byte[] bytes;
                            String technicsDirectory = "";
                            progressBar.setHeaderMessage("再次获取上载流！");
                            if ("rework".equals(technicsCategory1)) {
                                bytes = FilesUtil.getReworkTechnicsByte(technicsNumber1, technicsName1);
                                technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber1, technicsName1);
                            } else if ("temp".equals(technicsCategory1)) {
                                bytes = FilesUtil.getTempTechnicsByte(technicsNumber1, technicsName1);
                                technicsDirectory = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber1, technicsName1);
                            } else {
                                bytes = FilesUtil.getTechnicsByte(technicsNumber1, progressBar);
                                technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber1);
                            }

                            progressBar.setHeaderMessage("再次获取上载流结束！");
                            map.put("page", String.valueOf(pages));
                            map.put("oid", element.attributeValue("partOid"));
                            map.put("technicsNumber", technicsNumber1);
                            map.put("technicsName", technicsName1);
//                           map.put("bytes",bytes);
                            map.put("pplanNumber", element.attributeValue("pplanNumber"));
                            progressBar.setHeaderMessage("再次上载开始！");
                            TechnicsIntf.reUploadTechnics(bytes, map);
                            //上载报表类工艺
//                           uploadReportTechnics(progressBar, element, remark);
                            progressBar.setHeaderMessage("再次上载结束！");

//--------------------------------------------------------再次上载工艺文件(end)--------------------------------------

                            try {
                                progressBar.setHeaderMessage("开始上载PDF工艺文件！");
                                File file = new File(pdfFile);
                                TechHelper.uploadAttachForDocument(technicsNumber1, "PDFPreview.pdf", file);
                                progressBar.setHeaderMessage("结束上载PDF工艺文件！");
                            } catch (Exception e) {
                                progressBar.setHeaderMessage("上载PDF工艺文件过程中出现错误！");
                                e.printStackTrace();
                            }

//--------------
                            progressBar.setHeaderMessage("工艺上载成功！");
                            JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺上载成功！", "提示", 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
                progressBar.finish();
                progressBar.setVisible(false);
            }
        };

        thread.start();
        progressBar.setVisible(true);

    }

    public void technicsUploadThread() {
        final VaActionProgressBar progressBar = new VaActionProgressBar(NewTechnicsPart.this, "工艺上载", "正在工艺上载,请等待...", "工艺上载中");
        Thread thread = new Thread() {
            public void run() {
                try {
                    logger.debug("开始上载工艺文件...");
                    long startTime = System.currentTimeMillis();
                    XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
                    XWTreeNode reportNode = NewTechnicsPart.this.xwPartTreePanel.getSelectedTreeNode();
                    /*Map<String, String> partMap = new HashMap<String, String>();
                    if (reportNode != null) {
                        XWTreeNode parent = (XWTreeNode) reportNode.getParent();
                        XWTreeObject parentObj = parent.getObject();
                        if (parentObj instanceof XWPartTreeObject) {
                            Element partElenet = parentObj.getTreeCellData();
                            List<Element> childProducts = BomXMLUtil.getChildProducts(partElenet);
                            for (Element element : childProducts) {
                                partMap.put(element.attributeValue("partNumber"), element.attributeValue("gysl"));
                            }
                        }
                    }*/
                    if (node != null) {
                        XWTreeObject xo = node.getObject();
                        if ((xo instanceof XWTechnicsTreeObject)) {
                            Element element = xo.getTreeCellData();
                            String technicsNumber = element.attributeValue("technicsNumber");

                            logger.debug("开始校验工艺文件...");
                            long startTime4 = System.currentTimeMillis();
                            // 校验工艺文件
                            boolean checkOk = check(element, progressBar, NewTechnicsPart.this, NewTechnicsPart.this);
                            long endTime4 = System.currentTimeMillis();
                            logger.debug("校验工艺文件耗时："+(endTime4-startTime4)+" ms");
                            if(!checkOk) {
                                return;
                            }

                            // 校验Word转PDF
                            boolean isTransPdf = TechnicsUtil.checkWordTransPDF(element);
                            if (!isTransPdf) {
                                progressBar.finish();
                                progressBar.setVisible(false);
                                JOptionPane.showMessageDialog(null, "Word转换PDF失败，请尝试通过以下方式解决!\n" + "1、请检查确认jacob插件是否安装;\n" + "2、若1已安装，请尝试升级本地Word至Word2010;\n" + "3、若以上都不行，请联系管理员！");
                                return;
                            }

                            RemarkJDialog remarkJDialog = new RemarkJDialog("工艺上载", NewTechnicsPart.this);
                            String remark = remarkJDialog.showDialog();
                            if (remark == null) {
                                progressBar.finish();
                                progressBar.setVisible(false);
                                return;
                            }

                            String technicsCategory = element.attributeValue("technicsCategory");
                            String technicsName = element.attributeValue("technicsName");
                            String version = element.attributeValue("version");
                            String technicsType = element.attributeValue("technicsType"); // 工艺类型
                            String oldVersion = version;
                            if ((version == null) || (version.equals(""))) {
                                version = "space.1";
                                oldVersion = "space.1";
                            } else {
                                version = dealVersion(version);
                            }
                            String pdfFile = null;
                            String path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber, technicsName, technicsCategory);
                            pdfFile = path + File.separator + "PDFPreview.pdf";
                            File floder1 = new File(pdfFile);
                            if (floder1.exists()) {
                                boolean flag = floder1.delete();
                                if (!flag) {
                                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺名称为:" + technicsName + "的PDF文件正在使用，请先关闭再上载！");
                                    progressBar.finish();
                                    progressBar.setVisible(false);
                                    return;
                                }
                            }
                            //删除MHT文件
    						/*if(!"".equals(technicsNumber) && technicsNumber != null){
                            	String filePath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
                                 //删除MHT文件
         						deleteHTSFile(filePath);
                            }*/

                            try {
                                //开始
                                progressBar.setHeaderMessage("开始获取上载流！");
                                byte[] bytes;
                                if ("rework".equals(technicsCategory)) {
                                    bytes = FilesUtil.getReworkTechnicsByte(technicsNumber, technicsName);
                                } else if ("temp".equals(technicsCategory)) {
                                    bytes = FilesUtil.getTempTechnicsByte(technicsNumber, technicsName);
                                } else {
                                    bytes = FilesUtil.getTechnicsByte(technicsNumber, progressBar);
                                }
                                progressBar.setHeaderMessage("结束获取上载流！");
                                String partNumber = element.attributeValue("partNumber");

                                HashMap<String, String> map = getAttributes(element);
                                logger.debug("map=======" + map);
                                progressBar.setHeaderMessage("开始上载！");

//                                logger.debug("开始上载工艺文件...");
//                                long startTime3 = System.currentTimeMillis();
//                                HashMap<String, String> returnMap = TechnicsIntf.uploadTechnics(bytes, map, version, remark);
//                                long endTime3 = System.currentTimeMillis();
//                                logger.debug("上载工艺文件耗时："+(endTime3-startTime3)+" ms");
//
//                                progressBar.setHeaderMessage("结束上载！");
//                                logger.debug("returnMap= " + returnMap);
//                                String success = returnMap.get("success");
//                                if ("success".equals(success)) {
//                                    String newVersion = returnMap.get("version");
//                                    String newLifecycle = returnMap.get("lifecycle");
//                                    XmlUtility.setAttributeValue(element, "version", newVersion);
//                                    XmlUtility.setAttributeValue(element, "lifecycle", newLifecycle);
                                    XmlUtility.setAttributeValue(element, "version", version);
                                    saveProcess(element);
                                    //-------------------------------------------------------------------modify by caolei 2015-08-24
                                    progressBar.setHeaderMessage("开始生成工艺文件PDF格式！");

                                    //获取该零部件下所有子件（一层，标准件、自制件、元器件、外购件）信息
                                    XWTreeNode treeNode = xwPartTreePanel.getSelectedTreeNode().getP();
                                    XWTreeObject partObj = treeNode.getObject();
                                    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                                    if (partObj instanceof XWPartTreeObject) {
                                        if ("试验工艺".equals(technicsType)) {
                                            XWTreeNode tNode = treeNode.getP();
                                            if (tNode != null) {
                                                Map<String, String> map1 = null;
                                                if (tNode.getObject() instanceof XWPartTreeObject) {
                                                    XWPartTreeObject childObj = (XWPartTreeObject) tNode.getObject();
                                                    Element pele = childObj.getTreeCellData();
                                                    map1 = new HashMap<String, String>();
                                                    map1.put("partNumber", pele.attributeValue("partNumber"));
                                                    map1.put("partName", pele.attributeValue("partName"));
                                                    map1.put("useCount", pele.attributeValue("useCount"));
                                                    map1.put("XHPH", pele.attributeValue("XHPH"));
                                                    map1.put("CSIZE", pele.attributeValue("CSIZE"));
                                                    map1.put("MTYPE", pele.attributeValue("MTYPE"));
                                                    list.add(map1);
                                                }
                                            }
                                        } else {
                                            Enumeration<XWTreeNode> enu = treeNode.children();
                                            Map<String, String> map2 = null;
                                            while (enu.hasMoreElements()) {
                                                XWTreeNode tNode = enu.nextElement();
                                                if (tNode.getObject() instanceof XWPartTreeObject) {
                                                    XWPartTreeObject childObj = (XWPartTreeObject) tNode.getObject();
                                                    Element pele = childObj.getTreeCellData();
                                                    map2 = new HashMap<String, String>();
                                                    map2.put("partNumber", pele.attributeValue("partNumber"));
                                                    map2.put("partName", pele.attributeValue("partName"));
                                                    map2.put("useCount", pele.attributeValue("useCount"));
                                                    map2.put("XHPH", pele.attributeValue("XHPH"));
                                                    map2.put("CSIZE", pele.attributeValue("CSIZE"));
                                                    map2.put("MTYPE", pele.attributeValue("MTYPE"));
                                                    list.add(map2);
                                                }
                                            }
                                        }
                                    }

                                    //结束
                                    //将页数pages写入XML
                                    XmlUtility.setAttributeValue(element, "pageSize", String.valueOf(pages));
                                    System.out.println("@@@@@@@@@@@@@@@@@tmpTechnicsNumber="+tmpTechnicsNumber);
                                    String rootpath = path + File.separator + technicsNumber + ".xml";
                                    File rootfile = new File(rootpath);
                                    XmlUtility.saveDocument(element.getDocument(), rootfile);

                                    deleteTechnicsTempFiles(technicsNumber);
                                    File floder = new File(path, "fbtemp");
                                    logger.debug("开始生成PDF...");
                                    long startTime0 = System.currentTimeMillis();
                                    pages = PDFPreviewFactory.preview(path, false, list);
                                    if(pages==-1){
                                    	progressBar.finish();
                                        progressBar.setVisible(false);
                                        return;
                                    }
                                    long endTime0 = System.currentTimeMillis();
                                    logger.debug("生成PDF耗时："+(endTime0-startTime0)+" ms");
                                    pdfFile = path + File.separator + "PDFPreview.pdf";
                                    FileUtil.deleteFile(floder);
                                    progressBar.setHeaderMessage("结束生成工艺文件PDF格式！");

                                    //--------------------------------------------------------再次上载工艺文件(begin)--------------------------------------
                                    //再次打包工艺文件夹并上传
                                    progressBar.setHeaderMessage("再次获取上载流！");
                                    if ("rework".equals(technicsCategory)) {
                                        bytes = FilesUtil.getReworkTechnicsByte(technicsNumber, technicsName);
                                    } else if ("temp".equals(technicsCategory)) {
                                        bytes = FilesUtil.getTempTechnicsByte(technicsNumber, technicsName);
                                    } else {
                                        bytes = FilesUtil.getTechnicsByte(technicsNumber, progressBar);
                                    }
                                    progressBar.setHeaderMessage("再次获取上载流结束！");
                                    map.put("page", String.valueOf(pages));
                                    progressBar.setHeaderMessage("再次上载开始！");
                                    logger.debug("开始再次上载工艺文件...");
                                    long startTime1 = System.currentTimeMillis();
//                                    TechnicsIntf.reUploadTechnics(bytes, map);
                                    HashMap<String, String> returnMap = TechnicsIntf.uploadTechnics(bytes, map, version, remark);
                                    String success = returnMap.get("success");
                                    if ("success".equals(success)) {
                                      String newVersion = returnMap.get("version");
                                      String newLifecycle = returnMap.get("lifecycle");
                                      XmlUtility.setAttributeValue(element, "version", newVersion);
                                      XmlUtility.setAttributeValue(element, "lifecycle", newLifecycle);
                                        //add by Mchen 根据工艺文件编号，查询中间表，刷新中间表
                                        String oid = returnMap.get("oid");
                                        ErpToWCIntf.updateTechnicaQuotaInfo("wt.doc.WTDocument:"+oid,technicsNumber);
                                    }else{
                                        progressBar.finish();
                                        progressBar.setVisible(false);
                                        JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺上载过程中出现错误！", "提示", 1);
                                        return;
                                    }
                                    long endTime1 = System.currentTimeMillis();
                                    logger.debug("再次上载工艺文件耗时："+(endTime1-startTime1)+" ms");
                                    progressBar.setHeaderMessage("再次上载结束！");

                                    //--------------------------------------------------------再次上载工艺文件(end)--------------------------------------

                                    //------------------------------------------------------------------------------------modify by caolei 2015-08-24
                                    progressBar.setHeaderMessage("开始上载PDF工艺文件！");
                                    File file = new File(pdfFile);
                                    logger.debug("开始上载PDF...");
                                    long startTime2 = System.currentTimeMillis();
                                    TechHelper.uploadAttachForDocument(technicsNumber, "PDFPreview.pdf", file);
                                    long endTime2 = System.currentTimeMillis();
                                    logger.debug("上载PDF耗时："+(endTime2-startTime2)+" ms");
                                    progressBar.setHeaderMessage("结束上载PDF工艺文件！");

                                    Vector<UploadTechnics> editTechnics = NewTechnicsPart.this.editTechnics;
                                    if (editTechnics != null) {
                                        for (UploadTechnics temp : editTechnics) {
                                            if (temp.getTechnicsNumber().equals(technicsNumber)) {
                                                NewTechnicsPart.editTechnics.remove(temp);
                                                break;
                                            }
                                        }
                                    }
                                    progressBar.setHeaderMessage("工艺上载成功！");

                                    sysTechnicNodeVesion(node, partNumber, technicsNumber);
                                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺上载成功！", "提示", 1);
//                                } else {
//                                    XmlUtility.setAttributeValue(element, "version", version);
//                                    saveProcess(element);
//                                    String errorMessage = returnMap.get("errormessage");
//                                    if ((errorMessage != null) && (!errorMessage.equals(""))) {
//                                        JOptionPane.showMessageDialog(NewTechnicsPart.this, errorMessage, "提示", 1);
//                                        progressBar.setHeaderMessage(errorMessage);
//                                    } else {
//                                        JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺上载过程中出现错误！", "提示", 1);
//                                        progressBar.setHeaderMessage("工艺上载过程中出现错误！");
//                                    }
//                                }
                            } catch (Exception e1) {
                                XmlUtility.setAttributeValue(element, "version", oldVersion);
                                saveProcess(element);
                                JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺上载过程中出现错误！", "提示", 1);
                                progressBar.setHeaderMessage("工艺上载过程中出现错误！");
                                e1.printStackTrace();
                            }
                        }
                    } else if (reportNode != null) {
                        XWTreeObject xo = reportNode.getObject();
                        if (xo instanceof ReportTechnicsTreeObject) {
                            String path1 = null;
                            Element element = xo.getTreeCellData();
                            String technicsType = element.attributeValue("technicsType");
                            if ("工艺文件目录".equals(technicsType)) {
                                List<Element> dataList = element.elements("dataItemValue");
                                Map<String, String> technicsMap = new HashMap<String, String>();
                                for (Element data : dataList) {
                                    String state = data.attributeValue("lifecycle");
                                    String technicsNumber = data.attributeValue("fileNumber");
                                    String version = data.attributeValue("version");
                                    technicsMap.put(technicsNumber, version);
                                    if (!"已批准".equals(state)) {
                                        JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺文件目录中存在未受控的工艺，不能上载！");
                                        progressBar.finish();
                                        progressBar.setVisible(false);
                                        return;
                                    }
                                }

                                String message = TechnicsIntf.checkTechnicsIsLatest(technicsMap);
                                if (!"".equals(message)) {
                                    int result = JOptionPane.showConfirmDialog(getContentPane(), message + "是否继续上载？", "提示", JOptionPane.YES_NO_OPTION);
                                    if (result != JOptionPane.YES_OPTION) {
                                        progressBar.finish();
                                        progressBar.setVisible(false);
                                        return;
                                    }
                                }
                            }
                            String technicsNumber = element.attributeValue("technicsNumber");
                            String docState = TechnicsIntf.getDocumentStateByNumber(technicsNumber);
                            if (!"".equals(docState) && !"正在工作".equals(docState) && !"修改中".equals(docState)) {
                                JOptionPane.showMessageDialog(NewTechnicsPart.this, "此工艺文件已经提交签审，不能再修改！");
                                progressBar.finish();
                                progressBar.setVisible(false);
                                return;
                            }

                            RemarkJDialog remarkJDialog = new RemarkJDialog("工艺上载", NewTechnicsPart.this);
                            String remark = remarkJDialog.showDialog();
                            if (remark == null) {
                                progressBar.finish();
                                progressBar.setVisible(false);
                                return;
                            }

                            String technicsNumber1 = element.attributeValue("technicsNumber");
                            String technicsName1 = element.attributeValue("technicsName");
                            String technicsCategory1 = element.attributeValue("technicsCategory");
                            String pdfFile = null;
                            String path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber1, technicsName1, technicsCategory1);
                            pdfFile = path + File.separator + "PDFPreview.pdf";
                            File floder1 = new File(pdfFile);
                            if (floder1.exists()) {
                                boolean flag = floder1.delete();
                                if (!flag) {
                                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺名称为:" + technicsName1 + "的PDF文件正在使用，请先关闭再上载！");
                                    progressBar.finish();
                                    progressBar.setVisible(false);
                                    return;
                                }
                            }
                            //上载报表类工艺
                            uploadReportTechnics(progressBar, element, remark);

                            progressBar.setHeaderMessage("开始生成工艺文件PDF格式！");
                            File floder = new File(path, "fbtemp");
                            path1 = WorkSpaceUtil.getTechnicsDirectory(technicsNumber1);
                            //PDFPreviewFactory.preview(path, false, list);
                            pages1 = PDFPreviewFactory.previewForReport(path1, false);
                            pdfFile = path1 + File.separator + "PDFPreview.pdf";
                            FileUtil.deleteFile(floder);
                            progressBar.setHeaderMessage("结束生成工艺文件PDF格式！");

                            HashMap map = new HashMap();
//						--------------------------再次上载工艺文件(begin)--------------------------------------
                            //将页数pages写入XML
                            XmlUtility.setAttributeValue(element, "pageSize", String.valueOf(pages1));
                            String rootpath = path1 + File.separator + technicsNumber1 + ".xml";
                            File rootfile = new File(rootpath);
                            XmlUtility.saveDocument(element.getDocument(), rootfile);
                            //再次打包工艺文件夹并上传
                            byte[] bytes;
                            String technicsDirectory = "";
                            progressBar.setHeaderMessage("再次获取上载流！");
                            if ("rework".equals(technicsCategory1)) {
                                bytes = FilesUtil.getReworkTechnicsByte(technicsNumber1, technicsName1);
                                technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber1, technicsName1);
                            } else if ("temp".equals(technicsCategory1)) {
                                bytes = FilesUtil.getTempTechnicsByte(technicsNumber1, technicsName1);
                                technicsDirectory = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber1, technicsName1);
                            } else {
                                bytes = FilesUtil.getTechnicsByte(technicsNumber1, progressBar);
                                technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber1);
                            }

                            progressBar.setHeaderMessage("再次获取上载流结束！");
                            map.put("page", String.valueOf(pages));
                            map.put("oid", element.attributeValue("partOid"));
                            map.put("technicsNumber", technicsNumber1);
                            map.put("technicsName", technicsName1);
//                           map.put("bytes",bytes);
                            map.put("pplanNumber", element.attributeValue("pplanNumber"));
                            progressBar.setHeaderMessage("再次上载开始！");
                            TechnicsIntf.reUploadTechnics(bytes, map);
                            //上载报表类工艺
//                           uploadReportTechnics(progressBar, element, remark);
                            progressBar.setHeaderMessage("再次上载结束！");

//--------------------------------------------------------再次上载工艺文件(end)--------------------------------------

                            progressBar.setHeaderMessage("开始上载PDF工艺文件！");
                            File file = new File(pdfFile);
                            TechHelper.uploadAttachForDocument(technicsNumber1, "PDFPreview.pdf", file);
                            progressBar.setHeaderMessage("结束上载PDF工艺文件！");

//--------------
                            progressBar.setHeaderMessage("工艺上载成功！");
                            JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺上载成功！", "提示", 1);
                        }
                    }
                    long endTime = System.currentTimeMillis();
                    logger.debug("上载工艺文件总共耗时："+(endTime-startTime)+" ms");
                    progressBar.finish();
                    progressBar.setVisible(false);
                } catch (Exception e) {
                    progressBar.setHeaderMessage("上载PDF工艺文件过程中出现错误！");
                    e.printStackTrace();
                }
            }
        };

        thread.start();
        progressBar.setVisible(true);

    }

    /**
     * 自动将版本升级处理
     *
     * @param version
     * @return
     */
    private String dealVersion(String version) {
        if (version == null || "".equals(version)) {
            return "space.1";
        }
        String[] vers = version.split("\\.");
        if (vers.length > 1) {
            return vers[0] + "." + (Integer.valueOf(vers[1]) + 1);
        }
        return "space.1";
    }

    /**
     * 删除缓存的PDF文件
     *
     * @param technicsNumber
     */
    public void deleteTechnicsTempFiles(String technicsNumber) {
        String technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
        File technicsDicFile = new File(technicsDirectory);
        if (technicsDicFile != null && technicsDicFile.isDirectory()) {
            File[] listFiles = technicsDicFile.listFiles();
            if (listFiles != null) {
                for (int i = listFiles.length - 1; i >= 0; i--) {
                    File tempFile = listFiles[i];
                    if (tempFile.getName().endsWith(".pdf") && !"PDFPreview.pdf".equals(tempFile.getName())) {
                        tempFile.delete();
                    }
                }
            }
        }
    }

    /**
     * 上载报表类工艺文件
     *
     * @param progressBar
     * @param element
     * @param remark
     */
    private void uploadReportTechnics(VaActionProgressBar progressBar, Element element, String remark) {
        try {
            String technicsNumber = element.attributeValue("technicsNumber");
            byte[] bytes = FilesUtil.getTechnicsByte(technicsNumber, progressBar);
            String technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
            progressBar.setHeaderMessage("结束获取上载流！");
            String technicsFolderName = technicsDirectory.substring(technicsDirectory.lastIndexOf("\\") + 1, technicsDirectory.length());
            String partNumber = element.attributeValue("partNumber");
            String partOid = element.attributeValue("partOid");
            String partName = element.attributeValue("partName");
            String partType = element.attributeValue("partType");
            String pplanNumber = element.attributeValue("pplanNumber");
            String version = element.attributeValue("version");
            String technicsName = element.attributeValue("technicsName");
            String technicsType = element.attributeValue("technicsType");
            String BATCH = element.attributeValue("PCNO");
            String BIAOSHI = element.attributeValue("BIAOSHI");
            String SECRET = element.attributeValue("SECRET");
            String isCLDE = XmlUtility.isCLDE(element);
            HashMap<String, String> map = new HashMap<String, String>();

            map.put("isCLDE", isCLDE);
            map.put("SECRET", SECRET);
            map.put("oid", partOid);
            map.put("partNumber", partNumber);
            map.put("partName", partName);
            map.put("partType", partType);
            map.put("technicsType", technicsType);
            map.put("technicsNumber", technicsNumber);
            map.put("technicsName", technicsName);
            map.put("pplanNumber", pplanNumber);
            map.put("BATCH", BATCH);
            map.put("BIAOSHI", BIAOSHI);
            String CINDEX = element.attributeValue("CINDEX");
            String MINDEX = element.attributeValue("MINDEX");
            String PINDEX = element.attributeValue("PINDEX");
            String PHASE_CODE = element.attributeValue("PHASE_CODE");
            map.put("CINDEX", CINDEX);
            map.put("MINDEX", MINDEX);
            map.put("PINDEX", PINDEX);
            map.put("PHASE_CODE", PHASE_CODE);
            map.put("changeOrderOid", changeOrderOid);

            String bzyjNum = element.attributeValue("bzyjNum");
            String bzyjName = element.attributeValue("bzyjName");
            String yyfl = element.attributeValue("yyfl");
            map.put("bzyjNum", bzyjNum);
            map.put("bzyjName", bzyjName);
            map.put("yyfl", yyfl);
            logger.debug("map=======" + map);
            progressBar.setHeaderMessage("开始上载！");
            HashMap<String, String> returnMap = TechnicsIntf.uploadTechnics(bytes, map, version, remark);

            progressBar.setHeaderMessage("结束上载！");
            logger.debug("returnMap= " + returnMap);
            String success = returnMap.get("success");
            if ("success".equals(success)) {
                String newVersion = returnMap.get("version");
                String newLifecycle = returnMap.get("lifecycle");
                XmlUtility.setAttributeValue(element, "version", newVersion);
                XmlUtility.setAttributeValue(element, "lifecycle", newLifecycle);
                saveReportProcess(element);
				/*XWTreeNode rootNode = (XWTreeNode) xwPartTreePanel.getTree().getModel().getRoot();
				rootNode.removeAllChildren();
				NewTechnicsPart.this.xwPartTreePanel.expandAllNode(rootNode);
//				for (int i = 0; i < NewTechnicsPart.this.xwPartTreePanel.getTree().getRowCount(); i++) {
//					NewTechnicsPart.this.xwPartTreePanel.getTree().expandRow(i);
//					NewTechnicsPart.this.xwPartTreePanel.getTree().updateUI();
//				}
				NewTechnicsPart.this.xwPartTreePanel.getTree().updateUI();*/

                Vector<UploadTechnics> editTechnics = NewTechnicsPart.this.editTechnics;
                if (editTechnics != null) {
                    for (UploadTechnics temp : editTechnics) {
                        if (temp.getTechnicsNumber().equals(technicsNumber)) {
                            NewTechnicsPart.editTechnics.remove(temp);
                            break;
                        }
                    }
                }


                progressBar.setHeaderMessage("工艺上载成功！");
//				JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺上载成功！", "提示", 1);
            } else {
                XmlUtility.setAttributeValue(element, "version", version);
                saveReportProcess(element);
                String errorMessage = returnMap.get("errormessage");
                if ((errorMessage != null) && (!errorMessage.equals(""))) {
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, errorMessage, "提示", 1);
                    progressBar.setHeaderMessage(errorMessage);
                } else {
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺上载过程中出现错误！", "提示", 1);
                    progressBar.setHeaderMessage("工艺上载过程中出现错误！");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 上载报表类工艺文件
     *
     * @param progressBar
     * @param element
     * @param remark
     */
    private void uploadReportTechnics1(VaActionProgressBar progressBar, Element element, String remark) {
        try {
            String technicsNumber = element.attributeValue("technicsNumber");
            byte[] bytes = FilesUtil.getTechnicsByte(technicsNumber, progressBar);
            String technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
            progressBar.setHeaderMessage("结束获取上载流！");
            String technicsFolderName = technicsDirectory.substring(technicsDirectory.lastIndexOf("\\") + 1, technicsDirectory.length());
            String partNumber = element.attributeValue("partNumber");
            String partOid = element.attributeValue("partOid");
            String partName = element.attributeValue("partName");
            String partType = element.attributeValue("partType");
            String pplanNumber = element.attributeValue("pplanNumber");
            String version = element.attributeValue("version");
            String technicsName = element.attributeValue("technicsName");
            String technicsType = element.attributeValue("technicsType");
            String BATCH = element.attributeValue("PCNO");
            String BIAOSHI = element.attributeValue("BIAOSHI");
            String SECRET = element.attributeValue("SECRET");
            HashMap<String, String> map = new HashMap<String, String>();
            String isCLDE = XmlUtility.isCLDE(element);
            map.put("isCLDE", isCLDE);
            map.put("SECRET", SECRET);
            map.put("oid", partOid);
            map.put("partNumber", partNumber);
            map.put("partName", partName);
            map.put("partType", partType);
            map.put("technicsType", technicsType);
            map.put("technicsNumber", technicsNumber);
            map.put("technicsName", technicsName);
            map.put("pplanNumber", pplanNumber);
            map.put("BATCH", BATCH);
            map.put("BIAOSHI", BIAOSHI);
            map.put("changeOrderOid", changeOrderOid);

            logger.debug("map=======" + map);
            progressBar.setHeaderMessage("开始上载！");
            HashMap<String, String> returnMap = TechnicsIntf.uploadTechnics(bytes, map, version, remark);

            progressBar.setHeaderMessage("结束上载！");
            logger.debug("returnMap= " + returnMap);
            String success = returnMap.get("success");
            if ("success".equals(success)) {
                String newVersion = returnMap.get("version");
                String newLifecycle = returnMap.get("lifecycle");
                XmlUtility.setAttributeValue(element, "version", newVersion);
                XmlUtility.setAttributeValue(element, "lifecycle", newLifecycle);
                saveReportProcess(element);
                XWTreeNode rootNode = (XWTreeNode) xwPartTreePanel.getTree().getModel().getRoot();
                rootNode.removeAllChildren();
                NewTechnicsPart.this.xwPartTreePanel.expandAllNode(rootNode);
//              for (int i = 0; i < NewTechnicsPart.this.xwPartTreePanel.getTree().getRowCount(); i++) {
//                  NewTechnicsPart.this.xwPartTreePanel.getTree().expandRow(i);
//                  NewTechnicsPart.this.xwPartTreePanel.getTree().updateUI();
//              }
                NewTechnicsPart.this.xwPartTreePanel.getTree().updateUI();

//                Vector<UploadTechnics> editTechnics = NewTechnicsPart.this.editTechnics;
//                if (editTechnics != null) {
//                    for (UploadTechnics temp : editTechnics) {
//                        if (temp.getTechnicsNumber().equals(technicsNumber)) {
//                            NewTechnicsPart.editTechnics.remove(temp);
//                            break;
//                        }
//                    }
//                }


                progressBar.setHeaderMessage("工艺上载成功！");
//              JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺上载成功！", "提示", 1);
            } else {
                XmlUtility.setAttributeValue(element, "version", version);
                saveReportProcess(element);
                String errorMessage = returnMap.get("errormessage");
                if ((errorMessage != null) && (!errorMessage.equals(""))) {
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, errorMessage, "提示", 1);
                    progressBar.setHeaderMessage(errorMessage);
                } else {
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺上载过程中出现错误！", "提示", 1);
                    progressBar.setHeaderMessage("工艺上载过程中出现错误！");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void technicsUpload() {
        XWTreeNode node = this.technicsTreePanel.getSelectedTreeNode();
        if (node != null) {
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWTechnicsTreeObject)) {
                Element element = xo.getTreeCellData();
//                System.out.println("-----------------------------------------------");
//                System.out.println(element.asXML());
//                System.out.println("-----------------------------------------------");
                // if (!TechnicsUtil.checkTechnics(element)) {
                // return;
                // }
                RemarkJDialog remarkJDialog = new RemarkJDialog("工艺上载", this);
                String remark = remarkJDialog.showDialog();
                if (remark == null)
                    return;
                String technicsCategory = element.attributeValue("technicsCategory");
                String technicsNumber = element.attributeValue("technicsNumber");
                String technicsName = element.attributeValue("technicsName");
                String version = element.attributeValue("version");
                String technicsType = element.attributeValue("technicsType"); // 工艺类型
                if ((version == null) || (version.equals(""))) {
                    version = "1.0";
                }
                try {
                    byte[] bytes;
                    if ("rework".equals(technicsCategory)) {
                        bytes = FilesUtil.getReworkTechnicsByte(technicsNumber, technicsName);
                    } else if ("temp".equals(technicsCategory)) {
                        bytes = FilesUtil.getTempTechnicsByte(technicsNumber, technicsName);
                    } else {
                        bytes = FilesUtil.getTechnicsByte(technicsNumber);
                    }
                    String partNumber = element.attributeValue("partNumber");
                    String partOid = element.attributeValue("partOid");
                    String partName = element.attributeValue("partName");
                    String partType = element.attributeValue("partType");
                    String BATCH = element.attributeValue("PCNO");
                    String BIAOSHI = element.attributeValue("BIAOSHI");
                    String SECRET = element.attributeValue("SECRET");
                    String isCLDE = XmlUtility.isCLDE(element);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("oid", partOid);
                    map.put("partNumber", partNumber);
                    map.put("partName", partName);
                    map.put("partType", partType);
                    map.put("technicsType", technicsType);
                    map.put("technicsNumber", technicsNumber);
                    map.put("technicsName", technicsName);
                    map.put("isCLDE", isCLDE);
                    map.put("BATCH", BATCH);
                    map.put("BIAOSHI", BIAOSHI);
                    map.put("SECRET", SECRET);

                    String bzyjNum = element.attributeValue("bzyjNum");
                    String bzyjName = element.attributeValue("bzyjName");
                    String yyfl = element.attributeValue("yyfl");
                    map.put("bzyjNum", bzyjNum);
                    map.put("bzyjName", bzyjName);
                    map.put("yyfl", yyfl);
                    logger.debug("map=======" + map);
                    HashMap<String, String> returnMap = TechnicsIntf.uploadTechnics(bytes, map, version, remark);
                    logger.debug("returnMap= " + returnMap);
                    String success = returnMap.get("success");
                    if ("success".equals(success)) {
                        String newVersion = returnMap.get("version");
                        String newLifecycle = returnMap.get("lifecycle");
                        XmlUtility.setAttributeValue(element, "version", newVersion);
                        XmlUtility.setAttributeValue(element, "lifecycle", newLifecycle);

                        saveProcess(element);

                        this.technicsTreePanel.expandAllNode(node);
                        this.technicsTreePanel.getTree().setSelectionPath(new TreePath(node.getPath()));
                        XWTreeNode rootNode = (XWTreeNode) xwPartTreePanel.getTree().getModel().getRoot();
                        // updateTreeTechnics(rootNode, partOid, technicsName,
                        // technicsCategory, newVersion);
                        rootNode.removeAllChildren();
                        this.xwPartTreePanel.expandAllNode(rootNode);
                        for (int i = 0; i < this.xwPartTreePanel.getTree().getRowCount(); i++) {
                            this.xwPartTreePanel.getTree().expandRow(i);
                            this.xwPartTreePanel.getTree().updateUI();
                        }
                        this.xwPartTreePanel.getTree().updateUI();

                        technicsMasterJPanel.setUIValues(element);

                        Vector<UploadTechnics> editTechnics = this.editTechnics;
                        if (editTechnics != null) {
                            for (UploadTechnics temp : editTechnics) {
                                if (temp.getTechnicsNumber().equals(partNumber)) {
                                    this.editTechnics.remove(temp);
                                    break;
                                }
                            }
                        }
                        JOptionPane.showMessageDialog(this, "工艺上载成功！", "提示", 1);
                    } else {
                        XmlUtility.setAttributeValue(element, "version", version);
                        saveProcess(element);
                        String errorMessage = returnMap.get("errorMessage");
                        if ((errorMessage != null) && (!errorMessage.equals(""))) {
                            JOptionPane.showMessageDialog(this, errorMessage, "提示", 1);
                        } else {
                            JOptionPane.showMessageDialog(this, "工艺上载过程中出现错误！", "提示", 1);
                        }
                    }
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(this, "工艺上载过程中出现错误！", "提示", 1);
                    e1.printStackTrace();
                }
            }
        }
    }

    void updateTreeTechnics(XWTreeNode node, String partOid, String technicsName, String technicsCategory, String newVersion) {
        Enumeration children = node.children();
        while (children.hasMoreElements()) {
            XWTreeNode obj = (XWTreeNode) children.nextElement();
            Object object = obj.getObject();
            if (object instanceof XWPartTreeObject) {
                XWPartTreeObject partObject = (XWPartTreeObject) object;
                Element element = partObject.getTreeCellData();
                if (partOid.equals(element.attributeValue("oid"))) {
                    Enumeration childs = obj.children();
                    while (childs.hasMoreElements()) {
                        XWTreeNode childNode = (XWTreeNode) childs.nextElement();
                        Object childObject = childNode.getObject();
                        if (childObject instanceof TechnicsMessageTreeObject) {
                            TechnicsMessageTreeObject technicsObject = (TechnicsMessageTreeObject) childObject;
                            String name = technicsObject.getDisplayName();
                            String currentCategory = technicsObject.getTechnicsCategory();
                            String version = technicsObject.getVersion();
                            logger.debug(technicsObject.getTechName() + " version=" + version);
                            logger.debug("technicsName= " + technicsName + " newVersion=" + newVersion);
                            if (!version.equals(newVersion) && currentCategory != null) {
                                if (currentCategory.equals(technicsCategory) || (!"rework".equals(currentCategory) && !"rework".equals(technicsCategory))
                                        || (!"temp".equals(currentCategory) && !"temp".equals(technicsCategory))) {
                                    if (currentCategory.equals("rework") || currentCategory.equals("temp")) {
                                        if (name.contains(technicsName)) {
                                            logger.debug("rework/temp name= " + name);
                                            childNode.removeFromParent();
                                        }
                                    } else {
                                        if (name.contains(technicsName)) {
                                            logger.debug("name= " + name);
                                            childNode.removeFromParent();
                                        }
                                    }
                                }
                            }
                        } else if (childObject instanceof ReportTechnicsTreeObject) {
                            ReportTechnicsTreeObject technicsObject = (ReportTechnicsTreeObject) childObject;
                            String name = technicsObject.getDisplayName();
                            String currentCategory = technicsObject.getTechnicsCategory();
                            String version = technicsObject.getVersion();
                            logger.debug("technicsName= " + technicsName + " newVersion=" + newVersion);
                            if (!version.equals(newVersion) && currentCategory != null) {
                                if (currentCategory.equals(technicsCategory) || (!"rework".equals(currentCategory) && !"rework".equals(technicsCategory))
                                        || (!"temp".equals(currentCategory) && !"temp".equals(technicsCategory))) {
                                    if (currentCategory.equals("rework") || currentCategory.equals("temp")) {
                                        if (name.contains(technicsName)) {
                                            logger.debug("rework/temp name= " + name);
                                            childNode.removeFromParent();
                                        }
                                    } else {
                                        if (name.contains(technicsName)) {
                                            logger.debug("name= " + name);
                                            childNode.removeFromParent();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                updateTreeTechnics(obj, partOid, technicsName, technicsCategory, newVersion);
            }
        }

    }

    public void technicsUpdate() throws Exception {
        Document document = getCurrentTechnics();
        if (document == null) {
            JOptionPane.showMessageDialog(this, "请选择需要更新的工艺！", "提示", 1);
        } else {
            Element techEle = XmlUtility.getTechnicsElement(document);
            String technicsName = techEle.attributeValue("technicsName");
            String technicsNumber = techEle.attributeValue("technicsNumber");
            String partOid = techEle.attributeValue("partOid");
            String technicsCategory = techEle.attributeValue("technicsCategory");
            String technicsDirectory = "";
            if ("rework".equals(technicsCategory)) {
                technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
            } else if ("temp".equals(technicsCategory)) {
                technicsDirectory = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
            } else {
                technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
            }

            String fileName = technicsDirectory.substring(technicsDirectory.lastIndexOf("\\") + 1, technicsDirectory.length());

            if ((partOid != null) && (partOid.trim().length() > 0)) {
                try {
                    // Vector result =
                    // TechnicsIntf.getLastProcessPlan(partOid,fileName);
                    Vector result = TechnicsIntf.getLastProcessDocument(technicsNumber);
                    if ((result != null) && (result.size() == 5)) {
                        byte[] data = (byte[]) result.get(2);
                        if ((data == null) || (data.length <= 0)) {
                            return;
                        }
                        String version = (String) result.get(0);
                        String lifecycle = (String) result.get(1);

                        File f = new File(technicsDirectory);
                        if (f.exists()) {
                            int yes = JOptionPane.showConfirmDialog(this, "当前工艺数据已存在，是否覆盖？", "提示", 1);
                            if (yes == 1) {
                                return;
                            }
                            WorkSpaceUtil.delete(f);
                        }
                        TechnicsReleaseUtil.unZip(data, technicsDirectory);
                        XmlUtility.setAttributeValue(techEle, "version", version);
                        XmlUtility.setAttributeValue(techEle, "lifecycle", lifecycle);
                        saveProcess(techEle);
                    } else {
                        JOptionPane.showMessageDialog(this, "该零部件尚未创建工艺规程！", "提示", 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    File file = new File(technicsDirectory);
                    if (!file.exists()) {
                        try {
                            WorkSpaceUtil.delete(file);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static String getCurrentWorkSpace() throws Exception {
        String path = null;
        File f = new File(WorkSpaceUtil.PROPERTIES_FILEPATH);
        if (f.exists()) {
            FileReader fr = new FileReader(WorkSpaceUtil.PROPERTIES_FILEPATH);
            BufferedReader read = new BufferedReader(fr);
            String ss = read.readLine();
            fr.close();
            read.close();
            if (ss != null) {
                int i = ss.indexOf("=");
                if (i > 0) {
                    String workSpace_path = ss.substring(i + 1);
                    if ((workSpace_path != null) && (workSpace_path.trim().length() > 0))
                        path = workSpace_path;
                }
            }
        }
        return path;
    }

    private static boolean setworkSpace(Component parent) throws Exception {
        String path = getCurrentWorkSpace();
        logger.debug("path====" + path);
        String strFilePath = null;
        if ((path != null) && (path.trim().length() > 0)) {
            strFilePath = FileChooserTool.getDirectoryPath(path, parent);
        } else {
            strFilePath = FileChooserTool.getDirectoryPath(parent);
        }
        int index = strFilePath.lastIndexOf("\\");
        int index2 = (strFilePath.length() - 1);
        if (index > 0 && index2 > 0 && index2 == index) {
            strFilePath = strFilePath.replace("\\", "");
        }
        if (strFilePath != null) {
            File mYsetting = new File(strFilePath + "\\mySetting.xml");
            mYsetting.createNewFile();
            WorkSpaceUtil.CreatTechnis_ProductSpace(strFilePath);
            return true;
        }
        return false;
    }

    public void createTechnics() throws Exception {
        XWTreeNode node = this.xwPartTreePanel.getSelectedTreeNode();
        if (node != null) {
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWPartTreeObject)) {
                Element ele = xo.getTreeCellData();
//                System.out.println("---------------------------------------------");
//                System.out.println(ele.asXML());
//                System.out.println("---------------------------------------------");
                // String number = XmlUtility.getAttributeValue(ele,
                // "partNumber");
                // String path = WorkSpaceUtil.getTechnicsDirectory(number);
                // if (path != null) {
                // JOptionPane.showMessageDialog(this, "当前零部件下已有工艺，不能新建！",
                // "提示", 1);
                // return;
                // }```
                this.tecnicsJTabbedPane.setSelectedIndex(0);
                NewTechnicsSettingJDialog dialog = new NewTechnicsSettingJDialog(this, xo.getTreeCellData(), node, "normal");
                Document doc = dialog.getTechDocument();
                if (doc == null) {
                    return;
                }
//                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++");
//                System.out.println(doc.asXML());
//                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++");
                Element technicsElement = doc.getRootElement().element("QMFawTechnicsInfo");

                String fileCode = XmlUtility.getAttributeValue(technicsElement, "fileCode");
                String technicsNumber = XmlUtility.getAttributeValue(technicsElement, "technicsNumber");
                String technicsType = XmlUtility.getAttributeValue(technicsElement, "technicsType");

                Map<XWTreeNode, Document> connectObjects = dialog.getConnectObjects();
                if (connectObjects != null) {
                    Set<Entry<XWTreeNode, Document>> set = connectObjects.entrySet();
                    for (Entry<XWTreeNode, Document> entry : set) {
                        this.xwPartTreePanel.addTechnicsNode(technicsNumber, technicsType, entry.getKey());
                        loadTechnics(entry.getValue());
                    }
                }
                this.xwPartTreePanel.addTechnicsNode(technicsNumber, technicsType, node);
                loadTechnics(doc);
                // if (connectObjects == null) {
                this.treeJTabbedPane.setSelectedIndex(1);
                // } else {
                // this.treeJTabbedPane.setSelectedIndex(0);
                // }
                showData(this.technicsTreePanel.getSelectedTreeNode(), null);
                tecnicsJTabbedPane.setSelectedIndex(1);
                repaint();
            }
        }
    }

    public void createReworkTechnics() throws Exception {
        XWTreeNode node = this.xwPartTreePanel.getSelectedTreeNode();
        if (node != null) {
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWPartTreeObject)) {
                this.tecnicsJTabbedPane.setSelectedIndex(0);
                NewTechnicsSettingJDialog dialog = new NewTechnicsSettingJDialog(this, xo.getTreeCellData(), node, "rework");
                Document doc = dialog.getTechDocument();
                if (doc == null) {
                    return;
                }
                this.xwPartTreePanel.addTechnicsNode(node, dialog.technicsPath, dialog.version);
                loadTechnics(doc);
                this.treeJTabbedPane.setSelectedIndex(1);
                showData(this.technicsTreePanel.getSelectedTreeNode(), null);
                tecnicsJTabbedPane.setSelectedIndex(1);
                repaint();
            }
        }
    }

    public void createChangeMarkTechnics(VaActionProgressBar progressBar) {
        XWTreeNode node = this.xwPartTreePanel.getSelectedTreeNode();
        if (node != null) {
            CreateChangeMarkTechnicsDialog dialog = new CreateChangeMarkTechnicsDialog(this, progressBar);
            dialog.showDialog();
        }
    }

    public void createTempTechnics() throws Exception {
        XWTreeNode node = this.xwPartTreePanel.getSelectedTreeNode();
        if (node != null) {
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWPartTreeObject)) {
                this.tecnicsJTabbedPane.setSelectedIndex(0);
                NewTechnicsSettingJDialog dialog = new NewTechnicsSettingJDialog(this, xo.getTreeCellData(), node, "temp");
                Document doc = dialog.getTechDocument();
                if (doc == null) {
                    return;
                }
                this.xwPartTreePanel.addTechnicsNode(node, dialog.technicsPath, dialog.version);
                loadTechnics(doc);
                this.treeJTabbedPane.setSelectedIndex(1);
                showData(this.technicsTreePanel.getSelectedTreeNode(), null);
                tecnicsJTabbedPane.setSelectedIndex(1);
                repaint();

            }
        }
    }

    public void createReportTechnics() throws Exception {
        XWTreeNode node = this.xwPartTreePanel.getSelectedTreeNode();
        if (node != null) {
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWPartTreeObject)) {
                this.tecnicsJTabbedPane.setSelectedIndex(0);
//				CreateReportTechnicsJDialog dialog = new CreateReportTechnicsJDialog(this, "normal", true);
                CreateReportTechnicsJDialog dialog = new CreateReportTechnicsJDialog(this, xo.getTreeCellData(), node);

                Document doc = dialog.getDocument();
                if (doc == null) {
                    return;
                }
                Element technicsElement = doc.getRootElement().element("XWReportTechnicsInfo");
                String technicsNumber = XmlUtility.getAttributeValue(technicsElement, "technicsNumber");
                String technicsType = XmlUtility.getAttributeValue(technicsElement, "technicsType");

                this.xwPartTreePanel.addReportTechnicsNode(node, technicsNumber);

                saveReportProcess(technicsElement);

                showData(this.xwPartTreePanel.getSelectedTreeNode(), null);
                repaint();
            }
        }
    }

    public void createProdcutStep() throws Exception {
        if (this.treeJTabbedPane.getSelectedIndex() == 1) {
            XWTreeNode node = this.technicsTreePanel.getSelectedTreeNode();
            if (node != null) {
                judgeValueModified();
                createTechnicsRoute();
                XWTreeObject xo = node.getObject();
                this.tecnicsJTabbedPane.setSelectedIndex(0);
                if ((xo instanceof XWTechnicsTreeObject)) {
                    XWTechnicsTreeObject xto = (XWTechnicsTreeObject) xo;
                    Element techEle = xto.getTreeCellData();
                    Element procedureEle = XmlUtility.createProcedure();
                    int Max = 0;
                    List list = XmlUtility.getAllSteps(techEle);
                    String name = "";
                    String number = "";
                    String preID = "";
                    if ((list != null) && (list.size() > 0)) {
                        for (int i = 0; i < list.size(); i++) {
                            Element last = (Element) list.get(i);
                            String on = XmlUtility.getAttributeValue(last, "stepNumber");
                            String stepName = XmlUtility.getAttributeValue(last, "stepName");
                            String preBsoID = XmlUtility.getAttributeValue(last, "bsoID");
                            int num = XmlUtility.getSubFigure(on);
                            if (Max < num) {
                                Max = num;
                                name = stepName;
                                number = on;
                                preID = preBsoID;
                            }
                        }
                        XmlUtility.setAttributeValue(procedureEle, "preStep", number + "_" + name);
                        XmlUtility.setAttributeValue(procedureEle, "preBsoID", preID);
                    }
                    if (Max < 0)
                        Max = 0;
                    Max += 10;
                    XmlUtility.setAttributeValue(procedureEle, "stepNumber", Max + "");

                    List userlist = UserUtil.getCurrentUserOid();
                    if ((userlist != null) && (userlist.size() == 3)) {
                        String creator = (String) userlist.get(0);
                        String creatorOid = (String) userlist.get(1);
                        logger.debug("设置当前工序责任人为======" + creatorOid);
                        XmlUtility.setAttributeValue(procedureEle, "responser", creatorOid);
                    }

                    String partOid = techEle.attributeValue("partOid");
                    if (partOid != null) {
                        String responserGroup = TechnicsIntf.getUsertechnicsGroupName(partOid);
                        logger.debug("当前工艺零件======" + partOid + "===责任组==" + responserGroup);
                        if (responserGroup == null)
                            responserGroup = "";
                        XmlUtility.setAttributeValue(procedureEle, "responserGroup", responserGroup);
                    }

                    this.technicsTreePanel.refreshSelectNode(true);
                    this.technicsTreePanel.addProcedureNode(node, procedureEle);
                    showData(this.technicsTreePanel.getSelectedTreeNode(), null);
                }
            }
        }
    }

    public void CreateGongZhuangShenQinDan() throws Exception {
        XWTreeNode node = this.technicsTreePanel.getSelectedTreeNode();
        if (node != null) {
            judgeValueModified();
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWTechnicsTreeObject)) {
                XWTechnicsTreeObject xto = (XWTechnicsTreeObject) xo;
                Element element = xto.getTreeCellData();
                CreateGongZhuangSQD sqd = new CreateGongZhuangSQD(this, element, node);
                String sqdName = CreateGongZhuangSQD.getGongZhuangSQDName();
                GongZhuangSQDObject sqdObject = new GongZhuangSQDObject(sqdName, "space.1");
                if (node != null) {
                    XWTreeNode tnode = new XWTreeNode(sqdObject);
                    node.add(tnode);
                    if (!node.isLeaf()) {
                        this.technicsTreePanel.getTree().collapsePath(new TreePath(((DefaultTreeModel) this.technicsTreePanel.getTree().getModel()).getPathToRoot(node)));
                    }
                    this.technicsTreePanel.getTree().expandPath(new TreePath(((TreePath) this.technicsTreePanel.getTree().getSelectionPath()).getPath()));
                    this.technicsTreePanel.getTree().updateUI();
                }

            }
        }

    }

    public void createProductPace() throws Exception {
        XWTreeNode node = this.technicsTreePanel.getSelectedTreeNode();
        if (node != null) {
            judgeValueModified();
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWStepTreeObject)) {
                this.tecnicsJTabbedPane.setSelectedIndex(0);

                XWStepTreeObject xto = (XWStepTreeObject) xo;
                Element procedureELE = xto.getTreeCellData();

                int childrenCount = XmlUtility.countPaces(procedureELE);
                Element procedureEle = XmlUtility.createProcedure();
                int num = 0;

                List list = XmlUtility.getAllPaces(procedureELE);
                if ((list != null) && (list.size() > 0)) {
                    Element last = (Element) list.get(list.size() - 1);
                    String on = XmlUtility.getAttributeValue(last, "stepNumber");
                    num = XmlUtility.getSubFigure(on);
                    if (num < 0)
                        num = 0;
                }
                num++;
                XmlUtility.setAttributeValue(procedureEle, "stepNumber", num + "");
                this.technicsTreePanel.refreshSelectNode(true);

                this.technicsTreePanel.addProcedureNode(node, procedureEle);
                showData(this.technicsTreePanel.getSelectedTreeNode(), null);
            }
        }
    }

    public void showData(XWTreeNode newone, String flag) {
        this.namePanel.deleteObservers();
        int index = 0;
        if (this.tecnicsJTabbedPane != null)
            index = this.tecnicsJTabbedPane.getSelectedIndex();
        if ((index < 0) || (index > 1))
            index = 0;
        if (newone != null) {
            boolean isUsed = true;
            XWTreeObject treeObject = newone.getObject();
            Element data = treeObject.getTreeCellData();
            if ((treeObject instanceof XWTechnicsTreeObject)) {
                show3DPane(null);
                clearRightContent();
                String curType = getTechType(data);

                this.technicsMasterJPanel.clearUI();
                this.foregoingPanel = this.technicsMasterJPanel;
                this.dataPanel.add(this.technicsMasterJPanel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 11, 1, new Insets(5, 5, 5, 5), 0, 0));
                this.technicsMasterJPanel.setUIValues(data);
                if (this.tecnicsJTabbedPane.getTabCount() == 1)
                    this.tecnicsJTabbedPane.add(this.totalTechnicsRouteJPanel, "工艺路线");
                this.totalTechnicsRouteJPanel.setUIValues(data);
                this.technicsMasterJPanel.setNode(newone);
                this.tecnicsJTabbedPane.setSelectedIndex(index);

                XWTreeNode pNode = this.xwPartTreePanel.getSelectedTreeNode().getP();
                if (pNode != null) {
                    XWTreeObject xwObj = pNode.getObject();
                    if (xwObj instanceof XWPartTreeObject) {
                        isUsed = pNode.isUsed();
                        XWPartTreeObject partObject = (XWPartTreeObject) xwObj;
                        if (!partObject.isAllowed()) {
                            this.technicsMasterJPanel.setUIEnabled(false);
                            this.totalTechnicsRouteJPanel.setUIEnabled(false);
                        } else {
                            this.technicsMasterJPanel.setUIEnabled(true);
                            this.totalTechnicsRouteJPanel.setUIEnabled(true);
                        }
                    }
                }

                this.tecnicsJTabbedPane.setTitleAt(0, "工艺信息");
                if ((curType != null) && (curType.equals("装配工艺"))) {
                    String wrlFile = data.attributeValue("wrlFile");
                    if ((wrlFile != null) && (wrlFile.trim().length() > 0)) {
                        this.creopanel.setVisible(false);
                        this.panel3D.setVisible(true);
                        logger.debug("工艺————自动创建工艺文件000==" + wrlFile);
                        String techNumber = data.attributeValue("technicsNumber");
                        String path = WorkSpaceUtil.getTechnicsDirectory(techNumber) + "\\" + wrlFile + ".wrl";
                        logger.debug("3D图形文件路径为=====" + path);
                        this.panel3D.setView(path);
                        this.panel3D.play();
                    } else {
                        show3DPane(null);
                    }
                } else {
                    this.panel3D.setVisible(false);
                    this.creopanel.setVisible(true);
                }

                String state = data.attributeValue("lifecycle");
                String creator = data.attributeValue("creator");
                if ((!"正在工作".equals(state) && !"修改中".equals(state)) || !creator.equals(currentUser) || !isUsed) {
                    this.technicsMasterJPanel.setUIEnabled(false);
                    this.technicsStepJPanel.setUIEnabled(false);
                    this.totalTechnicsRouteJPanel.setUIEnabled(false);
                }

                this.technicsMasterJPanel.firstSetTitle(data);

                this.totalTechnicsRouteJPanel.repaint();
                this.technicsMasterJPanel.repaint();
                this.dataPanel.repaint();
                this.imagePanel.repaint();
                this.tecnicsPanel.repaint();
            }
            if ((treeObject instanceof XWStepTreeObject)) {
                show3DPane(null);
                clearRightContent();
                String curType = getTechType(data);
                this.technicsStepJPanel.clearUI();
                this.foregoingPanel = this.technicsStepJPanel;
                this.dataPanel.add(this.technicsStepJPanel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 11, 1, new Insets(5, 5, 5, 5), 0, 0));
                this.technicsStepJPanel.setUIValues(data);
                this.technicsStepJPanel.setNode(newone);
                this.tecnicsJTabbedPane.setSelectedIndex(index);
                this.tecnicsJTabbedPane.setTitleAt(0, "工序信息");
                if (this.tecnicsJTabbedPane.getTabCount() == 1)
                    this.tecnicsJTabbedPane.add(this.totalTechnicsRouteJPanel, "工艺路线");
                if ((curType != null) && (curType.equals("装配工艺"))) {
                    String cortonaID = data.attributeValue("cortonaID");
                    if ((cortonaID != null) && (cortonaID.trim().length() > 0)) {
                        logger.debug("工序————自动创建工艺文件222==" + cortonaID);
                        this.creopanel.setVisible(false);
                        this.panel3D.setVisible(true);
                        show3DPane(cortonaID);
                    } else {
                        show3DPane(null);
                    }

                } else {
                    this.panel3D.setVisible(false);
                    this.creopanel.setVisible(true);
                }

                this.namePanel.addObserver(this.technicsStepJPanel);
                Element techEle = XmlUtility.getTechnicsElement(getCurrentTechnics());


                XWTreeNode pNode = this.xwPartTreePanel.getSelectedTreeNode().getP();
                if (pNode != null) {
                    XWTreeObject obj = pNode.getObject();
                    if (obj instanceof XWPartTreeObject) {
                        isUsed = pNode.isUsed();
                        XWPartTreeObject partObject = (XWPartTreeObject) obj;
                        if (!partObject.isAllowed()) {
                            this.technicsStepJPanel.setUIEnabled(false);
                            this.totalTechnicsRouteJPanel.setUIEnabled(false);
                        }
                    }
                }

                String unite = XmlUtility.getAttributeValue(techEle, "unite");
                String creatorOid = XmlUtility.getAttributeValue(techEle, "creatorOid");
                if (unite == null)
                    unite = "";
                if (creatorOid == null)
                    creatorOid = "";
                if ((unite.equals("routeUnite")) || (unite.equals("unite"))) {
                    String user = getCurrentUser();
                    if (!creatorOid.equals(user)) {
                        String responser = XmlUtility.getAttributeValue(data, "responser");
                        if ((responser != null) && (responser.trim().length() > 0) && (user != null) && (user.trim().length() > 0)) {
                            if (!user.equals(responser)) {
                                this.namePanel.deleteObservers();
                                // this.technicsStepJPanel.setUIEnabled(false);
                            }
                        }
                    }
                }
                String lifecycle = XmlUtility.getAttributeValue(techEle, "lifecycle");
                if ((lifecycle != null) && (!"".equals(lifecycle)) && (!lifecycle.equals("拟制")) && (!lifecycle.equals("驳回"))) {
                    this.namePanel.deleteObservers();
                    // this.technicsStepJPanel.setUIEnabled(false);
                }

                UploadTechnics technics = ObjectTransfer.technicsElementToUploadTechnics(techEle);
                if (downLoadTech.contains(technics)) {
                    this.namePanel.deleteObservers();
                    // this.technicsStepJPanel.setUIEnabled(false);
                }

                String state = techEle.attributeValue("lifecycle");
                String creator = techEle.attributeValue("creator");
                if ((!"正在工作".equals(state) && !"修改中".equals(state)) || !creator.equals(currentUser) || !isUsed) {
                    this.technicsStepJPanel.setUIEnabled(false);
                    this.totalTechnicsRouteJPanel.setUIEnabled(false);
                } else {
                    this.technicsStepJPanel.setUIEnabled(true);
                    this.totalTechnicsRouteJPanel.setUIEnabled(true);
                }

                this.totalTechnicsRouteJPanel.repaint();
                this.technicsStepJPanel.repaint();
                this.dataPanel.repaint();
                this.imagePanel.repaint();
                this.tecnicsPanel.repaint();
            } else if ((treeObject instanceof XWPartTreeObject)) {
                clearRightContent();
                this.technicsPaceJPanel.clearUI();
                this.foregoingPanel = this.partMasterPanel;
                this.dataPanel.add(this.partMasterPanel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 11, 1, new Insets(5, 5, 5, 5), 0, 0));
                this.partMasterPanel.setUIValues(data);
                this.partMasterPanel.setNode(newone);
                this.tecnicsJTabbedPane.setSelectedIndex(0);
                this.tecnicsJTabbedPane.setTitleAt(0, "零部件信息");
                if (this.tecnicsJTabbedPane.getTabCount() == 2)
                    this.tecnicsJTabbedPane.remove(1);
                this.partMasterPanel.repaint();
                this.dataPanel.repaint();
                this.imagePanel.repaint();
                this.tecnicsPanel.repaint();
            } else if (treeObject instanceof ReportTechnicsTreeObject) {
                clearRightContent();
                this.technicsPaceJPanel.clearUI();
                this.foregoingPanel = this.reportTreeJPanel;
                this.dataPanel.add(this.reportTreeJPanel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 11, 1, new Insets(5, 5, 5, 5), 0, 0));
                reportTreeJPanel.getBottomSplit().setDividerLocation(500);

                reportTreeJPanel.setUIValues(data, newone, flag);
                this.tecnicsJTabbedPane.setSelectedIndex(0);
                // this.tecnicsJTabbedPane.setTitleAt(0, "报表类工艺文件信息");
                if (this.tecnicsJTabbedPane.getTabCount() == 2)
                    this.tecnicsJTabbedPane.remove(1);
                this.reportTreeJPanel.repaint();
                this.dataPanel.repaint();
                this.imagePanel.repaint();
                this.tecnicsPanel.repaint();
            } else if (treeObject instanceof JsxyDocTreeObject) {
                xwJsxyJPanel = new XWJsxyJPanel(this);
                clearRightContent();
                this.technicsPaceJPanel.clearUI();
                this.foregoingPanel = this.xwJsxyJPanel;
                this.dataPanel.add(this.xwJsxyJPanel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 11, 1, new Insets(5, 5, 5, 5), 0, 0));

//                reportTreeJPanel.setUIValues(data,newone,flag);
                this.tecnicsJTabbedPane.setSelectedIndex(0);
                this.tecnicsJTabbedPane.setTitleAt(0, "技术协议信息：");
                if (this.tecnicsJTabbedPane.getTabCount() == 2)
                    this.tecnicsJTabbedPane.remove(1);
                this.xwJsxyJPanel.repaint();
                this.dataPanel.repaint();
                this.imagePanel.repaint();
                this.tecnicsPanel.repaint();
            }
        }
    }

    public static void checkJVM(){
        long vmFree = 0;
        long vmUse = 0;
        long vmTotal = 0;
        long vmMax = 0;
        int byteToMb = 1024 * 1024;
        Runtime rt = Runtime.getRuntime();
        vmTotal = rt.totalMemory() / byteToMb;
        vmFree = rt.freeMemory() / byteToMb;
        vmMax = rt.maxMemory() / byteToMb;
        vmUse = vmTotal - vmFree;
        System.out.println("JVM内存已用的空间为：" + vmUse + " MB");
        System.out.println("JVM内存的空闲空间为：" + vmFree + " MB");
        System.out.println("JVM总内存空间为：" + vmTotal + " MB");
        System.out.println("JVM总内存空间为：" + vmMax + " MB");
    }

    public void judgeValueModified() throws Exception {
        if (this.foregoingPanel != null) {
            XWTreeNode node = null;

            Element temp = null;
            if ((this.foregoingPanel instanceof NewTechnicsMasterJPanel_XW)) {
                NewTechnicsMasterJPanel_XW master = (NewTechnicsMasterJPanel_XW) this.foregoingPanel;
                temp = master.getElement();
            } else if ((this.foregoingPanel instanceof TechnicsStepJPanel_XW)) {
                TechnicsStepJPanel_XW step = (TechnicsStepJPanel_XW) this.foregoingPanel;
                temp = step.getElement();
                node = step.getNode();
                Vector vec = this.technicsTreePanel.recordStepNodeState(node);
                node.removeAllChildren();
                node.expandAll();
                DefaultTreeModel model = (DefaultTreeModel) this.technicsTreePanel.getTree().getModel();
                model.reload(node);
                this.technicsTreePanel.reExpandStepNode(node, vec);
            } else if ((this.foregoingPanel instanceof TechnicsPaceJPanel_XW)) {
                TechnicsPaceJPanel_XW pace = (TechnicsPaceJPanel_XW) this.foregoingPanel;
                temp = pace.getElement();
            }

            if (temp != null) {
                saveProcess(temp);
            }
        }
        repaint();
    }

    public void saveProcess(Element newone) {
        logger.debug("saveProcess()===============starting");
        long start = System.currentTimeMillis();
        if (newone == null) {
            return;
        }
        Document doc = newone.getDocument();
        if (doc != null) {
            try {
                Element techele = XmlUtility.getTechnicsElement(doc);
                XmlUtility.hasKeyStepInTechnics(techele);
                String technicsNumber = XmlUtility.getAttributeValue(techele, "technicsNumber");
                String technicsName = XmlUtility.getAttributeValue(techele, "technicsName");
                String technicsCategory = XmlUtility.getAttributeValue(techele, "technicsCategory");
                UploadTechnics technics = ObjectTransfer.technicsElementToUploadTechnics(techele);
                if ((!editTechnics.contains(technics))
                    // && (!downLoadTech.contains(technics))
                        ) {
                    String lifecycle = techele.attributeValue("lifecycle");
                    if ((lifecycle != null) && ("".equals(lifecycle) || (lifecycle.equals("正在工作")) || (lifecycle.equals("修改中")))) {
                        logger.debug("technicsName=" + technicsName);
                        editTechnics.add(technics);
                    }
                }
                String s = XmlUtility.getCurrentTime();
                XmlUtility.setAttributeValue(techele, "modifyTime", s);
                this.technicsMasterJPanel.setCreatTimeJLabel(s);

                RecentSaveUtil.addRecent(technicsNumber);
                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setTrimText(false);
                format.setEncoding("GBK");
                String path;
                if ("rework".equals(technicsCategory)) {
                    path = WorkSpaceUtil.getReWorkTechnicsPathByTechnicsName(technicsNumber, technicsName);
                } else if ("temp".equals(technicsCategory)) {
                    path = WorkSpaceUtil.getTempTechnicsPathByTechnicsName(technicsNumber, technicsName);
                } else {
                    path = WorkSpaceUtil.getTechnicsPath(technicsNumber);
                }
                XMLWriter writer = new XMLWriter(new FileOutputStream(path), format);
                writer.write(doc);
                writer.close();
                // this.technicsMasterJPanel.setUIValues(techele);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "保存过程出现错误！", "提示", 1);
            }
        }
        long end = System.currentTimeMillis();
        logger.debug("saveProcess()===============end 耗时：" + (end - start) + "ms");
    }

    public void saveReportProcess(Element newone) {
        if (newone == null) {
            return;
        }
        Document doc = newone.getDocument();
        if (doc != null) {
            try {
                Element techele = XmlUtility.getTechnicsElement(doc);
                String technicsNumber = XmlUtility.getAttributeValue(techele, "technicsNumber");
                String technicsName = XmlUtility.getAttributeValue(techele, "technicsName");
                UploadTechnics technics = ObjectTransfer.technicsElementToUploadTechnics(techele);
                if ((!editTechnics.contains(technics))
                    // && (!downLoadTech.contains(technics))
                        ) {
                    String lifecycle = techele.attributeValue("lifecycle");
                    if ((lifecycle != null) && ("".equals(lifecycle) || (lifecycle.equals("正在工作")) || (lifecycle.equals("修改中")))) {
                        logger.debug("technicsName=" + technicsName);
                        editTechnics.add(technics);
                    }
                }
                String s = XmlUtility.getCurrentTime();
                XmlUtility.setAttributeValue(techele, "modifyTime", s);

                RecentSaveUtil.addRecent(technicsNumber);
                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setTrimText(false);
                format.setEncoding("GBK");
                String path = WorkSpaceUtil.getTechnicsPath(technicsNumber);
                XMLWriter writer = new XMLWriter(new FileOutputStream(path), format);
                writer.write(doc);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "保存过程出现错误！", "提示", 1);
            }
        }
    }

    public void firstSaveProcess(Element newone) {
        if (newone == null)
            return;
        Document doc = newone.getDocument();
        if (doc != null) {
            try {
                Element techele = XmlUtility.getTechnicsElement(doc);
                XmlUtility.hasKeyStepInTechnics(techele);
                String technicsNumber = XmlUtility.getAttributeValue(techele, "technicsNumber");
                String technicsName = XmlUtility.getAttributeValue(techele, "technicsName");
                String technicsCategory = XmlUtility.getAttributeValue(techele, "technicsCategory");
                String s = XmlUtility.getCurrentTime();
                XmlUtility.setAttributeValue(techele, "modifyTime", s);
                this.technicsMasterJPanel.setCreatTimeJLabel(s);
                RecentSaveUtil.addRecent(technicsNumber);
                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setTrimText(false);
                format.setEncoding("GBK");
                String path;
                if ("rework".equals(technicsCategory)) {
                    path = WorkSpaceUtil.getReWorkTechnicsPathByTechnicsName(technicsNumber, technicsName);
                } else if ("temp".equals(technicsCategory)) {
                    path = WorkSpaceUtil.getTempTechnicsPathByTechnicsName(technicsNumber, technicsName);
                } else {
                    path = WorkSpaceUtil.getTechnicsPath(technicsNumber);
                }
                XMLWriter writer = new XMLWriter(new FileOutputStream(path), format);
                writer.write(doc);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "保存过程出现错误！", "提示", 1);
            }
        }
    }

    public void quickCreateProcedures() {
        XWTreeNode node = this.technicsTreePanel.getCurrentTechnicsNode();

        if (node == null) {
            return;
        }
        XWTreeObject xo = node.getObject();
        if (!(xo instanceof XWTechnicsTreeObject)) {
            return;
        }
        Element techEle = xo.getTreeCellData();
//        logger.debug(techEle.asXML());
        List userlist = UserUtil.getCurrentUserOid();
        String creatorOid = "";
        String responserGroup = "";
        if ((userlist != null) && (userlist.size() == 3)) {
            String creator = (String) userlist.get(0);
            creatorOid = (String) userlist.get(1);
        }

        String partOid = techEle.attributeValue("partOid");
        String technicsType = techEle.attributeValue("technicsType");
        if (partOid != null) {
            try {
                responserGroup = TechnicsIntf.getUsertechnicsGroupName(partOid);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (responserGroup == null)
                responserGroup = "";
        }
        logger.debug("设置当前工序责任人为======" + creatorOid);
        logger.debug("当前工艺零件======" + partOid + "===责任组==" + responserGroup);
        QuickCreateProcedureJPanel m = this.totalTechnicsRouteJPanel.getQuickCreateProcedureJPanel();
        Vector v = m.showDialog();
        Vector vec = new Vector();
//        logger.debug(techEle.asXML());
        if ((v != null) && (v.size() > 0)) {
            for (int i = 0; i < v.size(); i++) {
                String[] datas = (String[]) v.get(i);
                Element ele = XmlUtility.getStepByID(techEle, datas[6]);
                if (ele != null) {
                    Element temp = (Element) ele.clone();
                    XmlUtility.setAttributeValue(temp, "isKey", datas[0]);
                    XmlUtility.setAttributeValue(temp, "stepNumber", datas[1]);
                    XmlUtility.setAttributeValue(temp, "workShop", datas[2]);
                    XmlUtility.setAttributeValue(temp, "stepName", datas[3]);
                    XmlUtility.setAttributeValue(temp, "workType", datas[4]);
                    XmlUtility.setAttributeValue(temp, "workSpace", datas[5]);
                    XmlUtility.setAttributeValue(temp, "preBsoID", datas[8]);
                    XmlUtility.setAttributeValue(temp, "preStep", datas[9]);
                    if (technicsType.contains("英文") && !"".equals(datas[3]) && datas[3] != null) {
                        String englishName = ResourceIntf.getEnglishNameByGxmc(datas[3]);
                        if (englishName == null) {
                            englishName = "";
                        }
                        XmlUtility.setAttributeValue(temp, "stepEnglishName", englishName);
                    }
                    vec.add(temp);
                } else {
                    ele = XmlUtility.createProcedure();
                    XmlUtility.setAttributeValue(ele, "isKey", datas[0]);
                    XmlUtility.setAttributeValue(ele, "stepNumber", datas[1]);
                    XmlUtility.setAttributeValue(ele, "workShop", datas[2]);
                    XmlUtility.setAttributeValue(ele, "stepName", datas[3]);
                    XmlUtility.setAttributeValue(ele, "workType", datas[4]);
                    XmlUtility.setAttributeValue(ele, "workSpace", datas[5]);
                    XmlUtility.setAttributeValue(ele, "bsoID", datas[6]);
                    XmlUtility.setAttributeValue(ele, "responser", creatorOid);
                    XmlUtility.setAttributeValue(ele, "responserGroup", responserGroup);
                    //前置工序 add by zhuhao 2017.12.06
                    XmlUtility.setAttributeValue(ele, "preBsoID", datas[8]);
                    XmlUtility.setAttributeValue(ele, "preStep", datas[9]);
                    if (technicsType.contains("英文") && !"".equals(datas[3]) && datas[3] != null) {
                        String englishName = ResourceIntf.getEnglishNameByGxmc(datas[3]);
                        if (englishName == null) {
                            englishName = "";
                        }
                        XmlUtility.setAttributeValue(ele, "stepEnglishName", englishName);
                    }
                    vec.add(ele);
                }
            }
        }
//        logger.debug(techEle.asXML());
        List list = XmlUtility.getAllSteps(techEle);
//        logger.debug(techEle.asXML());
        list.clear();
//        logger.debug(techEle.asXML());
        if (vec.size() > 0) {
            list.addAll(vec);
        }
//        logger.debug(techEle.asXML());
        XmlUtility.orderSteps(techEle);
//        logger.debug(techEle.asXML());
        saveProcess(techEle);
        this.totalTechnicsRouteJPanel.setUIValues(techEle);

        node.removeAllChildren();
        DefaultTreeModel model = (DefaultTreeModel) this.technicsTreePanel.getTree().getModel();
        model.reload(node);
        expand(node);
    }

    private void exitProcess() {
        try {
            int returnValue = 0;
            if (this.isMain) {
                //判断登陆人是否为创建者，以creator为判别标准 add by zhuhao
                Element techEle = XmlUtility.getTechnicsElement(getCurrentTechnics());
                String creator = XmlUtility.getAttributeValue(techEle, "creator");
//				String user = getCurrentUser();
                if ((editTechnics != null) && (editTechnics.size() > 0) && creator != null && creator.equals(currentUser)) {
                    CloseJDialog cj = new CloseJDialog(this);
                    returnValue = cj.showDialog();
                } else {
                    returnValue = JOptionPane.showConfirmDialog(this, "确定要退出吗？", "提示", 0);
                }
            } else {
                returnValue = JOptionPane.showConfirmDialog(this, "确定要退出吗？", "提示", 0);
            }

            if (returnValue == 0) {
                if (this.tecnicsJTabbedPane.getSelectedIndex() == 0) {
                    judgeValueModified();
                } else if (this.tecnicsJTabbedPane.getSelectedIndex() == 1) {
                    getQuickCreateProcedureJPanel().updataTechnicsData();
                }
                createTechnicsRoute();
                RecentSaveUtil.saveRecentOpenFile();
                if (this.isMain) {
                    WorkSpaceUtil.deleteTechnicsDirectory();

                    if (sis != null) {
                        sis.removeSingleInstanceListener(sisListener);
                    }

                    System.exit(0);
                } else {
                    if (sis != null) {
                        sis.removeSingleInstanceListener(sisListener);
                        System.exit(0);
                    } else {
                        dispose();
                        setDefaultCloseOperation(0);
                    }
                }
            } else {
                setDefaultCloseOperation(0);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(this, "系统退出出现异常，异常信息已写入日志文件。", "提示", 1);
            setDefaultCloseOperation(0);
            System.exit(0);
        }
    }

    public void treeSelectedValueChanged(XWTreeNode newone) {
        System.out.println("treeSelectedValueChanged=====treeSelectedValueChanged");
        if (this.tecnicsJTabbedPane.getSelectedIndex() == 1) {
            // return;
        }
        try {
            long start = System.currentTimeMillis();
            judgeValueModified();
            long end = System.currentTimeMillis();
            System.out.println("judgeValueModified 耗时：" + (end - start) + "ms");
            logger.debug("newone======" + newone);
            if (newone != null) {
                showData(newone, null);
            }
        } catch (Exception ee) {
            ee.printStackTrace();
            JOptionPane.showMessageDialog(this, ee.getMessage(), "提示", 1);
        }
    }

    public void autoCreateProcedure(XWTreeNode node) throws Exception {
        if (this.treeJTabbedPane.getSelectedIndex() == 0) {
            return;
        }
        if (node != null) {
            judgeValueModified();
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWTechnicsTreeObject)) {
                XWTechnicsTreeObject xto = (XWTechnicsTreeObject) xo;
                Element techEle = xto.getTreeCellData();
                try {
                    File file = FileChooserTool.getFile("xml", autoFilePath, this);
                    if (file == null)
                        return;
                    autoFilePath = file.getAbsolutePath();
                    String filePath = file.getAbsolutePath();
                    AutoGenerateStepsUtil.autoGenerateSteps(techEle, filePath);
                    saveProcess(techEle);
                    this.technicsTreePanel.refreshSelectNode(true);
                    this.technicsTreePanel.expandAllNode(node);
                    getQuickCreateProcedureJPanel().setTableValue(techEle);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "自动生成工序功能出现错误！");
                }
            } else {
                logger.debug("=======可能出现错误处777");
                JOptionPane.showMessageDialog(this, "该操作需要针对工艺进行，产品树中无选中工艺节点！");
            }
        } else {
            logger.debug("=======可能出现错误处888");
            JOptionPane.showMessageDialog(this, "该操作需要针对工艺进行，产品树中无选中工艺节点！");
            return;
        }
    }

    public void searchServerTechnics() {
        String path = null;
        try {
            TechnicsSearchDialog dia = new TechnicsSearchDialog(this);
            dia.showDialog();
        } catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(this, "搜索工艺出现错误！", "提示", 1);
            File technDIR = new File(path);
            if ((technDIR != null) && (technDIR.exists()))
                try {
                    WorkSpaceUtil.delete(technDIR);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    public void reGenerateStepNumbers() throws Exception {
        XWTreeNode node = this.technicsTreePanel.getSelectedTreeNode();
        if (node != null) {
            XWTreeObject treeObject = node.getObject();
            if ((treeObject instanceof XWTechnicsTreeObject)) {
                Element ele = treeObject.getTreeCellData();
                if ("common".equals(ele.attributeValue("unite"))) {
                    XmlUtility.orderSteps(ele);
                    XmlUtility.reSetStepNumbers(ele);
                    saveProcess(ele);
                    this.technicsTreePanel.refreshSelectNode(true);
                } else {
                    JOptionPane.showMessageDialog(this, "合编工艺不可以刷新工序号！", "提示", 1);
                }
            }
        }
    }

    public JPanel getForegoingPanel() {
        return this.foregoingPanel;
    }

    public void copy() throws Exception {
        copyElement = null;
        copyDatas.clear();
        copyElement = null;
        copyPace.clear();
        logger.debug("**************copy*******************");
        XWTreeNode node = null;
        if (this.treeJTabbedPane.getSelectedIndex() == 1) {
            Vector vec = this.technicsTreePanel.getSelectedPaths();
            if ((vec != null) && (vec.size() > 0)) {
                judgeValueModified();
                technicsType = this.technicsTreePanel.getCurrentTechnicsNode().getObject().getTreeCellData().attributeValue("technicsType");
                for (int i = 0; i < vec.size(); i++) {
                    node = (XWTreeNode) vec.get(i);
                    XWTreeObject xo = node.getObject();
                    if ((xo != null) && ((xo instanceof XWStepTreeObject))) {
                        XWStepTreeObject xto = (XWStepTreeObject) xo;
                        Element stepElement = (Element) xto.getTreeCellData().clone();
                        Element stepCommonParamTables = stepElement.element("commonParamTables");
                        Element stepSpecialParamTables = stepElement.element("specialParamTables");
                        stepElement.remove(stepCommonParamTables);
                        stepElement.remove(stepSpecialParamTables);
                        List<Element> paceElements = stepElement.selectNodes("paces/QMProcedureInfo");
                        for (Element paceElement : paceElements) {
                            Element paceCommonParamTables = paceElement.element("commonParamTables");
                            Element pacesSpecialParamTables = paceElement.element("specialParamTables");
                            paceElement.remove(paceCommonParamTables);
                            paceElement.remove(pacesSpecialParamTables);
                            Element schemaData = paceElement.element("schemaData");
                            if(schemaData!=null){
                                paceElement.remove(schemaData);
                            }
                        }
                        copyDatas.add(stepElement);
                    }
                    // else if ((xo != null) && ((xo instanceof
                    // XWTechnicsTreeObject))) {
                    // // copyElement = xo.getTreeCellData();
                    // } else if ((xo != null) && ((xo instanceof
                    // XWPaceTreeObject))) {
                    // copyDatas.add(xo.getTreeCellData());
                    // }
                    else if ((xo != null) && ((xo instanceof XWTechnicsTreeObject))) {
                        copyElement = (Element) xo.getTreeCellData().clone();
                        copyTechNumber = copyElement.attributeValue("technicsNumber");
                    } else if ((xo != null) && xo instanceof XWPaceTreeObject) {
                        XWPaceTreeObject paceObject = (XWPaceTreeObject) xo;
                        copyPace.add(paceObject.getTreeCellData().clone());
                    }
                }

            }
        } else if (this.treeJTabbedPane.getSelectedIndex() == 0) {
            node = this.xwPartTreePanel.getSelectedTreeNode();
            XWTreeObject treeobject = node.getObject();
            if (treeobject instanceof TechnicsMessageTreeObject) {
                TechnicsMessageTreeObject techobj = (TechnicsMessageTreeObject) treeobject;
                copyTechElement = (Element) techobj.getTreeCellData().clone();
                copyTechNumber = copyTechElement.attributeValue("technicsNumber");
            }
        } else {
            logger.debug("=======可能出现错误处999");
        }
    }

    public void copyTechnics() throws Exception {
        XWTreeNode node = this.xwPartTreePanel.getSelectedTreeNode();
        if (node != null) {

        } else {
            JOptionPane.showMessageDialog(this, "该操作需要针对PBOM进行，产品树中无选中零部件节点！");
        }
    }

    /**
     * 新建转阶段工艺
     *
     * @param selectPartNode
     * @param technicsNumber
     * @throws Exception
     */
    public void addChangeMarkTechnic(XWTreeNode selectPartNode, String technicsNumber) throws Exception {
        if (selectPartNode != null) {
            XWTreeObject object = selectPartNode.getObject();
            if (object instanceof XWPartTreeObject) {
                XWPartTreeObject obj = (XWPartTreeObject) object;
                Element partElement = obj.getTreeCellData();
                Document document = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(technicsNumber);
                Element technicsElement = XmlUtility.getTechnicsElement(document);
                //1、修改xml中工艺文件编号，设置状态为正在工作、修改阶段标记为PBOM当前阶段
                String technicsType = XmlUtility.getAttributeValue(technicsElement, "technicsType");
                XmlUtility.setAttributeValue(technicsElement, "technicsNumber", technicsNumber);
                XmlUtility.setAttributeValue(technicsElement, "lifecycle", "正在工作");
                XmlUtility.setAttributeValue(technicsElement, "version", "");
                XmlUtility.generatePartInfo(partElement, technicsElement);
                //2、移除原xml中参装件、检验记录表、工艺中间模型相关信息
                XmlUtility.removeUnusefulElement(technicsElement);
                //3、保存修改后的XML文件
                XmlUtility.saveDocument(document, WorkSpaceUtil.getTechnicsPath(technicsNumber));
                UploadTechnics technics = ObjectTransfer.technicsElementToUploadTechnics(technicsElement);
                if (!editTechnics.contains(technics)) {
                    editTechnics.add(technics);
                }
                this.xwPartTreePanel.addTechnicsNode(technicsNumber, technicsType, selectPartNode);
                loadTechnics(document);
                repaint();
            }
        }
    }

    public void pastTechnics() throws Exception {
        String cindex = "";
        String gysl = "";
        String useCount = "";
        String partNumber = "";
        XWTreeNode selectNode = this.xwPartTreePanel.getSelectedTreeNode();
        if (selectNode != null) {
            XWTreeObject object = selectNode.getObject();
            if (object instanceof XWPartTreeObject) {
                XWPartTreeObject obj = (XWPartTreeObject) object;
                Element data = obj.getTreeCellData();
                cindex = data.attributeValue("CINDEX");
                gysl = data.attributeValue("gysl");
                useCount = data.attributeValue("useCount");
                partNumber = data.attributeValue("partNumber");
                System.out.println("ceshi ----->" + cindex);
            }
        }
        if (this.copyTechNumber != null && !"".equals(this.copyTechNumber)) {
            String copyTechFile = WorkSpaceUtil.getTechnicsPath(this.copyTechNumber);
            String copyTechDir = WorkSpaceUtil.getCommonTechnicsRootPath() + this.copyTechNumber;
            logger.debug("======pastTechnics======copyTechDir:" + copyTechDir);
            String newTechNumber = TechnicsIntf.genTechnicsNumber();
            if("".equals(newTechNumber)){
                JOptionPane.showMessageDialog(this, "工艺文件流水号生成失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String newTechDir = WorkSpaceUtil.getCommonTechnicsRootPath() + newTechNumber;
            logger.debug("======pastTechnics=======newTechDir:" + newTechDir);
            File newTechDirFile = new File(newTechDir);
            if (!newTechDirFile.exists()) {
                newTechDirFile.mkdir();
            }
            // 拷贝被复制的工艺的临时目录下的所有文件到新的工艺的目录下
            WorkSpaceUtil.copyDir2Dir(copyTechDir, newTechDir);
            // 修改工艺XML文件名称为新工艺编号
            String newTechFile = newTechDir + File.separator + this.copyTechNumber + ".xml";
            //WorkSpaceUtil.removeQualityElement(newTechFile);
            File newFile = new File(newTechFile);
            boolean flag = newFile.renameTo(new File(newTechDir + File.separator + newTechNumber + ".xml"));
            if (flag) {
                Document document = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(newTechNumber);
                Element techElement = XmlUtility.getTechnicsElement(document);
                XWTreeNode node = this.xwPartTreePanel.getSelectedTreeNode();
                XmlUtility.setAttributeValue(techElement, "CINDEX", cindex);
                XmlUtility.setAttributeValue(techElement, "gysl", gysl);
                XmlUtility.setAttributeValue(techElement, "useCount", useCount);
                Element gyde = XmlUtility.getTechnicsDEElement(techElement);

                List<Element> newParts = XmlUtility.getTechnicsGYDENewPart(gyde);
                if (newParts != null && !newParts.isEmpty()) {
                    for (Element element : newParts) {
                        XmlUtility.setAttributeValue(element, "parentNumber", partNumber);
                    }
                }
                List<Element> newSjzykParts = XmlUtility.getTechnicsSJZYKGYDENewPart(gyde);
                if (newSjzykParts != null && !newSjzykParts.isEmpty()) {
                    for (Element element : newSjzykParts) {
                        XmlUtility.setAttributeValue(element, "parentPartNumber", partNumber);
                        XmlUtility.setAttributeValue(element, "partNumber", partNumber);

                    }
                }

                int op = JOptionPane.showConfirmDialog(this, "是否需要修改工艺文件编号及其属性？", "提示", 0);
                if (op == 0) {
                    new CopyTechnicsDialog(this, node, techElement, newTechNumber);
                } else {
                    boolean isOk = true;
                    String pplanNumber = XmlUtility.getAttributeValue(techElement, "pplanNumber");
                    if (checkNumber(pplanNumber)) {
                        JOptionPane.showMessageDialog(this, "编号为【" + pplanNumber + "】的工艺文件已经存在！", "提示", JOptionPane.INFORMATION_MESSAGE);
                        isOk = false;
                    }

                    String zfLfag = XmlUtility.getAttributeValue(techElement, "ZFFLAG");
                    String pplantype = XmlUtility.getAttributeValue(techElement, "PPLANTYPE");
                    if ("Z".equals(String.valueOf(zfLfag)) && "正式工艺文件".equals(pplantype)) {
                        if (checkZhuZhi()) {
                            JOptionPane.showMessageDialog(this, "正式主工艺文件已经存在,一个零部件下只能有一份正式主工艺！", "提示", JOptionPane.INFORMATION_MESSAGE);
                            isOk = false;
                        }
                    }
                    Boolean hasZhuZhi = (Boolean) IntfUtil.getPeRemoteMethodInvoke("hasZhuZhi", new Class[] { String.class, String.class }, new Object[] { partNumber });
                    if (hasZhuZhi) {
                        JOptionPane.showMessageDialog(this, "正式主工艺文件已经存在,一个零部件下只能有一份正式主工艺！", "提示", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    if (!isOk) {
                        new CopyTechnicsDialog(this, node, techElement, newTechNumber);
                    } else {
                        XWPartTreeObject partObject = (XWPartTreeObject) node.getObject();
                        Element partElement = partObject.getTreeCellData();

                        XmlUtility.modifyTechnicsAttr(techElement, partElement);

                        XmlUtility.setAttributeValue(techElement, "technicsNumber", newTechNumber);
                        XmlUtility.setAttributeValue(techElement, "lifecycle", "正在工作");
                        XmlUtility.setAttributeValue(techElement, "version", "");
                        XmlUtility.setAttributeValue(techElement, "createTime", XmlUtility.getCurrentTime());
                        XmlUtility.setAttributeValue(techElement, "modifyTime", XmlUtility.getCurrentTime());
                        XmlUtility.setAttributeValue(techElement, "creator", currentUser);

                        //复制工艺白羽表优化
                        Element tecBaiyuElement = techElement.element("schemaData");
                        if(tecBaiyuElement!=null){
                            techElement.remove(tecBaiyuElement);
                        }

                        List<Element> stepElements = XmlUtility.getAllSteps(techElement);
                        for (Element stepEle : stepElements) {
                            XmlUtility.removeCzjElements(stepEle);
                            List<Element> paceElements = stepEle.selectNodes("paces/QMProcedureInfo");
                            if(paceElements != null && paceElements.size() > 0) {
                                for (Element paceElement : paceElements) {
                                    Element schemaData = paceElement.element("schemaData");
                                    if(schemaData!=null){
                                        paceElement.remove(schemaData);
                                    }
                                }
                            }
                        }
                        //保存修改后的XML文件
                        XmlUtility.saveDocument(document, WorkSpaceUtil.getTechnicsPath(newTechNumber));

                        String technicsNumber = XmlUtility.getAttributeValue(techElement, "technicsNumber");
                        String technicsType = XmlUtility.getAttributeValue(techElement, "technicsType");

                        this.xwPartTreePanel.addTechnicsNode(technicsNumber, technicsType, node);
                        loadTechnics(document);
                        this.treeJTabbedPane.setSelectedIndex(1);
                        showData(this.technicsTreePanel.getSelectedTreeNode(), null);
                        tecnicsJTabbedPane.setSelectedIndex(1);
                        repaint();
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "请先复制工艺文件后在进行粘贴！");
        }
    }

    private boolean checkNumber(String newNumber) {
        Enumeration childs = xwPartTreePanel.getSelectedTreeNode().children();
        if (childs != null) {
            while (childs.hasMoreElements()) {
                Object obj = childs.nextElement();
                if (obj instanceof XWTreeNode) {
                    XWTreeNode techObj = (XWTreeNode) obj;
                    XWTreeObject treeObj = techObj.getObject();
                    if (treeObj instanceof TechnicsMessageTreeObject) {
                        TechnicsMessageTreeObject tmo = (TechnicsMessageTreeObject) treeObj;
                        String number = tmo.getPplanNumber();
                        if (newNumber.equals(number)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean checkZhuZhi() {
        Enumeration childs = xwPartTreePanel.getSelectedTreeNode().children();
        if (childs != null) {
            while (childs.hasMoreElements()) {
                Object obj = childs.nextElement();
                if (obj instanceof XWTreeNode) {
                    XWTreeNode techObj = (XWTreeNode) obj;
                    XWTreeObject treeObj = techObj.getObject();
                    if (treeObj instanceof TechnicsMessageTreeObject) {
                        TechnicsMessageTreeObject tmo = (TechnicsMessageTreeObject) treeObj;
                        String isZhuZhi = tmo.getisZhuZhi();
                        String pplantype = tmo.getPplantype();
                        Element techElement = tmo.getTreeCellData();
                        String lifecycle = techElement.attributeValue("lifecycle");
                        if ("已作废".equals(lifecycle)) {
                            continue;
                        }
                        if ("Z".equals(isZhuZhi) && "正式工艺文件".equals(pplantype)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void pastTechnicsAndModifyAttri(String techNumber, Map<String, String> map, Element partElement) throws Exception {
        Document document = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(techNumber);
        Element techElement = XmlUtility.getTechnicsElement(document);
        XWTreeNode node1 = this.xwPartTreePanel.getSelectedTreeNode();
        XWTreeObject object = node1.getObject();
        XWPartTreeObject obj = (XWPartTreeObject) object;
        Element data = obj.getTreeCellData();
        String gysl = data.attributeValue("gysl");
        String useCount = data.attributeValue("useCount");
        XmlUtility.setAttributeValue(techElement, "technicsNumber", techNumber);
        XmlUtility.setAttributeValue(techElement, "pplanNumber", map.get("pplanNumber"));
        XmlUtility.setAttributeValue(techElement, "DEPT", map.get("DEPT"));
        XmlUtility.setAttributeValue(techElement, "PPLANTYPE", map.get("PPLANTYPE"));
        XmlUtility.setAttributeValue(techElement, "ZFFLAG", map.get("ZFFLAG"));
        XmlUtility.setAttributeValue(techElement, "SECRET", map.get("SECRET"));
        XmlUtility.setAttributeValue(techElement, "PCNO", map.get("PCNO"));
        XmlUtility.setAttributeValue(techElement, "technicsType", map.get("technicsType"));
        XmlUtility.setAttributeValue(techElement, "PPLANID", map.get("PPLANID"));
        XmlUtility.setAttributeValue(techElement, "createTime", map.get("createTime"));
        XmlUtility.setAttributeValue(techElement, "modifyTime", map.get("modifyTime"));
        XmlUtility.setAttributeValue(techElement, "pplanName", map.get("pplanName"));
        XmlUtility.setAttributeValue(techElement, "technicsName", map.get("technicsName"));
        XmlUtility.setAttributeValue(techElement, "lifecycle", "正在工作");
        XmlUtility.setAttributeValue(techElement, "version", "");
        XmlUtility.setAttributeValue(techElement, "gysl", gysl);
        XmlUtility.setAttributeValue(techElement, "useCount", useCount);
        XmlUtility.setAttributeValue(techElement, "parentNumber", data.attributeValue("partNumber"));
        XmlUtility.setAttributeValue(techElement, "creator", currentUser);

        Element ibaElement = XmlUtility.getTechnicsIBAAttriElement(techElement);
        XmlUtility.deleteAllChildElements(ibaElement);
        ibaElement.add(XmlUtility.createTechnicsIBAAttriElement("产品型号代号", map.get("MINDEX")));
        ibaElement.add(XmlUtility.createTechnicsIBAAttriElement("产品代号", map.get("PINDEX")));
        ibaElement.add(XmlUtility.createTechnicsIBAAttriElement("关重件标记", map.get("KEYCOMPONENT")));
        ibaElement.add(XmlUtility.createTechnicsIBAAttriElement("产品阶段标记", map.get("PHASE_CODE")));
        ibaElement.add(XmlUtility.createTechnicsIBAAttriElement("部门", map.get("DEPT")));
        ibaElement.add(XmlUtility.createTechnicsIBAAttriElement("工艺文件类别", map.get("PPLANTYPE")));
        if ("临时工艺文件".equals(map.get("PPLANTYPE"))) {
            ibaElement.add(XmlUtility.createTechnicsIBAAttriElement("临时工艺顺序号", map.get("tempNo")));
        }
        ibaElement.add(XmlUtility.createTechnicsIBAAttriElement("主辅制类别", map.get("ZFFLAG")));
        ibaElement.add(XmlUtility.createTechnicsIBAAttriElement("工艺特征编号", map.get("PPLANID")));
        ibaElement.add(XmlUtility.createTechnicsIBAAttriElement("文件密级", map.get("SECRET")));
        ibaElement.add(XmlUtility.createTechnicsIBAAttriElement("批次号", map.get("PCNO")));

        //复制工艺白羽表优化
        Element tecBaiyuElement = techElement.element("schemaData");
        if(tecBaiyuElement!=null){
            techElement.remove(tecBaiyuElement);
        }

        XmlUtility.modifyTechnicsAttr(techElement, partElement);
        List<Element> stepElements = XmlUtility.getAllSteps(techElement);
        for (Element stepELe : stepElements) {
            XmlUtility.removeCzjElements(stepELe);
            List<Element> paceElements = stepELe.selectNodes("paces/QMProcedureInfo");
            if(paceElements != null && paceElements.size() > 0) {
                for (Element paceElement : paceElements) {
                    Element schemaData = paceElement.element("schemaData");
                    if(schemaData!=null){
                        paceElement.remove(schemaData);
                    }
                }
            }
        }

        //保存修改后的XML文件
        XmlUtility.saveDocument(document, WorkSpaceUtil.getTechnicsPath(techNumber));

        document = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(techNumber);
//        System.out.println("---------pastTechnicsAndModifyAttri------\r\n" + document.asXML());
        techElement = XmlUtility.getTechnicsElement(document);
        String technicsNumber = XmlUtility.getAttributeValue(techElement, "technicsNumber");
        String technicsType = XmlUtility.getAttributeValue(techElement, "technicsType");

        XWTreeNode node = this.xwPartTreePanel.getSelectedTreeNode();
        this.xwPartTreePanel.addTechnicsNode(technicsNumber, technicsType, node);
        loadTechnics(document);
        this.treeJTabbedPane.setSelectedIndex(1);
        //showData(this.technicsTreePanel.getSelectedTreeNode());
        tecnicsJTabbedPane.setSelectedIndex(1);
        repaint();
    }

    public void pasteProductTechnics() throws Exception {
        XWTreeNode node = this.technicsTreePanel.getSelectedTreeNode();
        if (node != null) {
            judgeValueModified();
            createTechnicsRoute();
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWTechnicsTreeObject)) {
                Element techElement = xo.getTreeCellData();

                List userlist = UserUtil.getCurrentUserOid();
                String creatorOid = "";
                String responserGroup = "";
                if ((userlist != null) && (userlist.size() == 3)) {
                    String creator = (String) userlist.get(0);
                    creatorOid = (String) userlist.get(1);
                }
                String partOid = techElement.attributeValue("partOid");
                if (partOid != null) {
                    responserGroup = TechnicsIntf.getUsertechnicsGroupName(partOid);
                    if (responserGroup == null)
                        responserGroup = "";
                }
                logger.debug("设置当前工序责任人为======" + creatorOid);
                logger.debug("当前工艺零件======" + partOid + "===责任组==" + responserGroup);

                for (int i = 0; i < copyDatas.size(); i++) {
                    Element temp = (Element) copyDatas.get(i);
                    /**移除参装件信息*/
                    //XmlUtility.removeCzjElements(temp);
                    XmlUtility.setAttributeValue(temp, "responser", creatorOid);
                    XmlUtility.setAttributeValue(temp, "responserGroup", responserGroup);
                    CopyUtil.copyStep((Element) temp.clone(), techElement);
                }

                XmlUtility.orderSteps(techElement);
                saveProcess(techElement);
                expand(node);
                if (this.tecnicsJTabbedPane.getSelectedIndex() == 1) {
                    getQuickCreateProcedureJPanel().setTableValue(techElement);
                }
            } else {
                logger.debug("=======可能出现错误处AAA");
                JOptionPane.showMessageDialog(this, "该操作需要针对工艺进行，产品树中无选中工艺节点！");
            }
        } else {
            logger.debug("=======可能出现错误处BBB");
            JOptionPane.showMessageDialog(this, "该操作需要针对工艺进行，产品树中无选中工艺节点！");
        }
    }

    public void createTechnicsRoute() throws Exception {
        Document document = getCurrentTechnics();
        if (document == null)
            return;
        Element techEle = XmlUtility.getTechnicsElement(document);
        Element element = (Element) techEle.clone();
        List list = XmlUtility.getAllSteps(element);
        String technicsNumber = techEle.attributeValue("technicsNumber");
        String technicsName = techEle.attributeValue("technicsName");
        String technicsCategory = techEle.attributeValue("technicsCategory");
        File file;
        if ("rework".equals(technicsCategory)) {
            file = TechnicsRouteUtil.getReworkTechnicsRouteXML(technicsNumber, technicsName);
        } else if ("temp".equals(technicsCategory)) {
            file = TechnicsRouteUtil.getTempTechnicsRouteXML(technicsNumber, technicsName);
        } else {
            file = TechnicsRouteUtil.getTechnicsRouteXML(technicsNumber);
        }
        if (((list == null) || (list.size() == 0)) && (file == null)) {
            return;
        }
        if (file == null) {
            TechnicsRouteUtil.firstCreateTechnicsRoute(technicsNumber, technicsName, technicsCategory, list, getTechnicsRouteJPanel());
        } else {
            TechnicsRouteUtil.reCreateTechnicsRoute(technicsNumber, technicsName, technicsCategory, list, getTechnicsRouteJPanel());
        }
        TechnicsRouteUtil.setTechnicsXMLsProcedureLinks(techEle, getTechnicsRouteJPanel().getProcedureUnits(), this);
    }

    public void runCreoProgram() {
    }

    public void run3DProgram() {
    }

    private void setTechnicsSelectedStatus(XWTreeObject node) {
        this.createTechnics.setEnabled(false);
        this.createStep.setEnabled(true);
        if (node.getTreeCellData().attributeValue("technicsType").equals("装配工艺"))
            this.autoCreateProcedure.setEnabled(true);
        else {
            this.autoCreateProcedure.setEnabled(false);
        }

        this.delete.setEnabled(true);
        this.copy.setEnabled(false);
        if (copyDatas.size() > 0) {
            boolean sameType = node.getTreeCellData().attributeValue("technicsType").equals(technicsType);
            if (sameType)
                this.paste.setEnabled(true);
            else
                this.paste.setEnabled(false);
        } else {
            this.paste.setEnabled(false);
        }

        this.reviewTechnics.setEnabled(true);
        this.pdfReviewTechnics.setEnabled(true);
        this.technicsCoWork.setEnabled(true);
        this.viewHistory.setEnabled(true);

        this.submitUniteTechnics.setEnabled(true);

        this.technicsConfirm.setEnabled(true);
        this.technicsUpload.setEnabled(true);
        this.technicsUpdate.setEnabled(true);
        logger.debug("isDownload= " + isDownload);
        if (isDownload) {
            NewTechnicsPart.this.signed.setEnabled(false);
            NewTechnicsPart.this.signed2.setEnabled(false);
            NewTechnicsPart.this.completeTask.setEnabled(true);
        } else {

            Element currentElement =  node.getTreeCellData();
            NewTechnicsPart.this.signed.setEnabled( AclUtl.sign3Enable(currentElement));
            NewTechnicsPart.this.signed2.setEnabled(true);
            NewTechnicsPart.this.completeTask.setEnabled(false);
        }

        if (this.treeJTabbedPane.getSelectedIndex() == 0)
            this.startPBOM.setEnabled(true);
        else
            this.startPBOM.setEnabled(false);
    }

    private void setTechnicsSelectedStatus() {
        this.createTechnics.setEnabled(false);
        this.createStep.setEnabled(true);
        this.autoCreateProcedure.setEnabled(false);
        this.delete.setEnabled(false);
        this.copy.setEnabled(false);
        this.paste.setEnabled(false);
        this.reviewTechnics.setEnabled(true);
        this.pdfReviewTechnics.setEnabled(true);
        this.technicsCoWork.setEnabled(false);
        this.viewHistory.setEnabled(false);
        this.submitUniteTechnics.setEnabled(false);
        this.technicsConfirm.setEnabled(false);
        this.technicsUpload.setEnabled(false);
        this.technicsUpdate.setEnabled(false);
        this.signed.setEnabled(false);
        this.signed2.setEnabled(false);
        this.completeTask.setEnabled(false);

        this.startPBOM.setEnabled(false);
    }

    private void setStepSelectedStatus(XWTreeObject node) {
        this.createTechnics.setEnabled(false);
        this.createStep.setEnabled(false);
        this.autoCreateProcedure.setEnabled(false);
        this.delete.setEnabled(true);

        this.copy.setEnabled(true);
        this.paste.setEnabled(false);

        this.reviewTechnics.setEnabled(false);
        this.pdfReviewTechnics.setEnabled(false);
        this.technicsCoWork.setEnabled(false);
        this.viewHistory.setEnabled(false);
        this.submitUniteTechnics.setEnabled(false);
        this.technicsConfirm.setEnabled(false);
        this.technicsUpload.setEnabled(false);
        this.technicsUpdate.setEnabled(false);

        this.signed.setEnabled(false);
        this.startPBOM.setEnabled(false);
        this.signed2.setEnabled(false);
        this.completeTask.setEnabled(false);
    }

    private void setStepSelectedStatus() {
        this.createTechnics.setEnabled(false);
        this.createStep.setEnabled(false);
        this.autoCreateProcedure.setEnabled(false);
        this.delete.setEnabled(false);

        this.copy.setEnabled(true);
        this.paste.setEnabled(false);
        this.reviewTechnics.setEnabled(false);
        this.pdfReviewTechnics.setEnabled(false);
        this.technicsCoWork.setEnabled(false);
        this.viewHistory.setEnabled(false);
        this.submitUniteTechnics.setEnabled(false);
        this.technicsConfirm.setEnabled(false);
        this.technicsUpload.setEnabled(false);
        this.technicsUpdate.setEnabled(false);

        this.signed.setEnabled(false);
        this.signed2.setEnabled(false);
        this.completeTask.setEnabled(false);
        this.startPBOM.setEnabled(false);
    }

    private void setPartSelectedStatus() {
        this.createTechnics.setEnabled(true);
        this.createStep.setEnabled(false);
        this.autoCreateProcedure.setEnabled(false);
        this.delete.setEnabled(false);
        this.copy.setEnabled(false);
        this.paste.setEnabled(false);

        this.reviewTechnics.setEnabled(false);
        this.pdfReviewTechnics.setEnabled(false);
        this.technicsConfirm.setEnabled(false);
        this.technicsUpload.setEnabled(false);
        this.technicsUpdate.setEnabled(false);
        this.signed.setEnabled(false);
        this.signed2.setEnabled(false);
        this.completeTask.setEnabled(false);
        this.technicsCoWork.setEnabled(false);
        this.viewHistory.setEnabled(false);
        this.submitUniteTechnics.setEnabled(false);
        this.startPBOM.setEnabled(false);
    }

    private void setProductSelectedStatus() {
        this.createTechnics.setEnabled(false);
        this.createStep.setEnabled(false);
        this.autoCreateProcedure.setEnabled(false);
        this.delete.setEnabled(false);

        this.copy.setEnabled(false);
        this.paste.setEnabled(false);

        this.reviewTechnics.setEnabled(false);
        this.pdfReviewTechnics.setEnabled(false);
        this.technicsConfirm.setEnabled(false);
        this.technicsCoWork.setEnabled(false);
        this.viewHistory.setEnabled(false);
        this.submitUniteTechnics.setEnabled(false);
        this.technicsUpload.setEnabled(false);
        this.technicsUpdate.setEnabled(false);
        this.signed.setEnabled(false);
        this.signed2.setEnabled(false);
        this.completeTask.setEnabled(false);

        this.startPBOM.setEnabled(true);
    }

    private void setNullSelectedStatus() {
        this.createTechnics.setEnabled(false);
        this.createStep.setEnabled(false);
        this.autoCreateProcedure.setEnabled(false);
        this.delete.setEnabled(false);

        this.copy.setEnabled(false);
        this.paste.setEnabled(false);

        this.reviewTechnics.setEnabled(false);
        this.pdfReviewTechnics.setEnabled(false);
        this.technicsConfirm.setEnabled(false);
        this.technicsUpload.setEnabled(false);
        this.technicsUpdate.setEnabled(false);
        this.technicsCoWork.setEnabled(false);
        this.viewHistory.setEnabled(false);
        this.signed.setEnabled(false);
        this.signed2.setEnabled(false);
        this.completeTask.setEnabled(false);
        this.submitUniteTechnics.setEnabled(false);

        this.startPBOM.setEnabled(false);
    }

    private void addMenuListener() {
        this.jMenuFile.addMenuListener(this.mouseAdapter);
        this.jMenuTool.addMenuListener(this.mouseAdapter);
        this.jMenuSelect.addMenuListener(this.mouseAdapter);
    }

    public Element getCopyElement() {
        return copyElement;
    }

    public String getTechnicsType() {
        return technicsType;
    }

    private void initMenuItemIcon() {
        this.copy.setIcon(IconUtil.getImageIcon("/images/copy_edit.gif"));
        this.paste.setIcon(IconUtil.getImageIcon("/images/paste_edit.gif"));
        this.delete.setIcon(IconUtil.getImageIcon("/images/delete_edit.gif"));

        this.mySettingItem.setIcon(IconUtil.getImageIcon("/images/cat_icon_connect.png"));
        this.autoCreateProcedure.setIcon(IconUtil.getImageIcon("/images/partM_dependency.gif"));
        // this.jMenuSearchTechnics.setIcon(IconUtil
        // .getImageIcon("/images/viewm.gif"));
        // this.jMenuSearchProduct.setIcon(IconUtil
        // .getImageIcon("/images/zoomwindow.gif"));
        this.workspacesItem.setIcon(IconUtil.getImageIcon("/images/home.gif"));

        this.createTechnics.setIcon(IconUtil.getImageIcon("/images/technics.gif"));
        this.createStep.setIcon(IconUtil.getImageIcon("/images/procedure.gif"));

        this.reviewTechnics.setIcon(IconUtil.getImageIcon("/images/preview_template.gif"));
        this.pdfReviewTechnics.setIcon(IconUtil.getImageIcon("/images/preview_template.gif"));
    }

    public String getTechType(Element ele) {
        if (ele == null)
            return null;
        Element techele = getTechElement(ele);
        String techType = XmlUtility.getAttributeValue(techele, "technicsType");
        return techType;
    }

    public Element getTechElement(Element ele) {
        Document doc = ele.getDocument();
        Element techele = XmlUtility.getTechnicsElement(doc);
        return techele;
    }

    public String getTechPath(Element ele) throws Exception {
        Element techele = getTechElement(ele);
        String technum = XmlUtility.getAttributeValue(techele, "technicsNumber");
        String path = WorkSpaceUtil.getTechnicsPath(technum);
        return path;
    }

    public static void startLicenseTask() {
        if (licenseTask == null) {
            licenseTask = new LicenseTask();
        }

        if (timer == null) {
            timer = new Timer(true);
        } else {
            timer.cancel();
            timer.purge();
            timer = new Timer(true);
        }
        licenseTask = new LicenseTask();
        timer.schedule(licenseTask, 0L, 30 * 60 * 1000L);
    }

    // 第一个参数
    // ---------1.正常做工艺
    // --------------------第二个参数： PBOM最上皆整件oid
    // ---------2.合编工艺
    // --------------------第二个参数： PBOM最上皆整件oid
    // --------------------第三个参数： 需要合编的工艺对应的零件的oid
    // ---------3.变更工艺
    // --------------------第二个参数： PBOM最上皆整件oid
    // --------------------第三个参数： 需要变更的工艺对应的零件的oid
    // ---------4.返工/临时工艺
    // --------------------第二个参数： PBOM最上皆整件oid
    // --------------------第三个参数： 需要返工的工艺任务的oid
    public static void main(String[] args) throws Exception {
        if (isEditorStarted) {
            JOptionPane.showMessageDialog(null, "只能启动一个工艺编辑器", "提示", 1);
            return;
        }
        flag = true;
        isEditorStarted = true;
        if (args == null || args.length == 0) {
            flag = true;
            RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
            methodServer.setUserName("wcadmin");
            methodServer.setPassword("wcadmin");
            args = new String[11];
            args[0] = "1";
            // pbomOid
            args[1]="4557519";//4148565 4470439 //SOP 3954759
            //childPartOid
            args[2]="4557519";//4148565  4470439 //SOP 3954759
            args[3]="";
            //workItemOid
            args[4]="";// //SOP 4449781
            args[5]="";
            args[6]="";
            args[7]="1";//SOP
            args[8] = "";
            args[9] = "false";
            isDownload = false;
            args[10] = null;
            //<?xml version="1.0" encoding="UTF-8" standalone="yes"?><Product oid="198269" productName="工艺知识库" productNumber="工艺知识库">    <parts>        <QMPartInfo oid="3954759" useCount="1" occId="-1" occpath="-1" partNumber="S05-CX-0011" partName="元器件引线手工成形操作规范" partType="WTPart" materialType="自制件" lifecycle="正在工作" version="space.1 (Manufacturing)" MTYPE="自制件" CTYPE="自制件" SECRET="机密" Term="15" SpecializedType="电装" ProceduceName="成型" ProfessionalCode="DZ" GONGXUJIANHAO="CX1" ZZCJ="3"/>    </parts></Product>
        }


        if ((args != null) && (args.length > 0)) {
            for (int i = 0; i < args.length; i++) {
                logger.debug("参数 " + i + " ========" + args[i]);
            }
            logger.debug("isDownload= " + isDownload);
            runType = args[0];
            pbomOid = args[1];
            childPartOid = args[2];
            workItemOid = args[4];
            workItemState = args[5];
            docOid = args[6];
            EditorConfig.startType = args[7];
            changeOrderOid = args[8];// isXinZengGengGai
            isTemplateCapp = Boolean.valueOf(args[9]);
            sopPartXml = args[10];
        }

        startQMEdit(args);
    }

    public static void startQMEdit(final String[] args) {
        Thread startMBom = new Thread() {
            public void run() {
                long startTime = System.currentTimeMillis();
                startAnimFrame.setHeaderMessage("正在启动工艺编辑器");
                try {
                    logger.debug("开始初始化需要用到的条件数据...");
                    long startTime0 = System.currentTimeMillis();
                    // 1.初始化需要用到的条件数据
                    loadInitializeData();
                    long endTime0 = System.currentTimeMillis();
                    logger.debug("初始化需要用到的条件数据耗时：" + (endTime0 - startTime0) + " ms");

                    // 2.设置编辑器外观
                    setLookAndTheme();

                    // 3.如果工作空间不存在则提示用户设置工作空间
                    if (!WorkSpaceUtil.isExistWrokSpace()) {
                        JOptionPane.showMessageDialog(null, "工作空间属性文件不存在或者为空,请先设置工作空间");
                        if (!setworkSpace(null)) {
                            System.exit(0);
                        }
                    }

                    // 4.初始化编辑器界面、加载PBOM数据、下载工艺数据
                    new NewTechnicsPart();
                } catch (Exception e) {
                    startAnimFrame.setVisible(false);
                    e.printStackTrace();
                    String err = e.getMessage();
                    if (err != null) {
                        JOptionPane.showMessageDialog(null, err);
                    } else {
                        JOptionPane.showMessageDialog(null, "工艺编辑器启动异常");
                    }

                    System.exit(0);
                }
                startAnimFrame.setHeaderMessage("完成启动工艺编辑器");
                startAnimFrame.finish();
                startAnimFrame.setVisible(false);

                long endTime = System.currentTimeMillis();
                logger.debug("启动编辑器总共耗时：" + (endTime - startTime) + " ms");
            }
        };
        startMBom.start();
        startAnimFrame.setVisible(true);
    }

    private static void loadInitializeData() throws Exception {
        /**
         * 加载当前用户信息
         */
        UserUtil.getCurrentUserOid();

        /**
         * 判断是否是新增更改
         */
        if (changeOrderOid != null && !"".equals(changeOrderOid) && !"null".equals(changeOrderOid)) {
            String genggaileixing = ResourceIntf.getGengGaiLeiXing(changeOrderOid);
            if ("新增更改".equals(genggaileixing)) {
                isXinZengGengGai = true;
            }
        }

        /**
         * author :chenming data:2015.10.30
         *
         * */
        if (workItemOid != null && !"".equals(workItemOid) && !"null".equals(workItemOid)) {
            boolean reportFlag = ResourceIntf.getRenwuLeiXing(workItemOid);
            if (reportFlag) {
                reportTechnics = true;
            } else {
                reportTechnics = false;
            }
        }

        /**
         * author :chenming data:2016.1.13
         *
         * */
        if (docOid != null && !"".equals(docOid) && !"null".equals(docOid)) {
            docStyle = ResourceIntf.getDocStyleByDocOid(docOid);
            if ("报表类工艺".equals(docStyle)) {
                reportTechnics = true;
            }
        }

        logger.debug("开始加载工序名称资源 pdNameDescribeMap...");
        long pdStartTime = System.currentTimeMillis();
        //从ServiceWeb获得工序名称信息
        pdNameDescribeMap = ResourceIntf.getPdNameDescribe();
        logger.debug("pdNameDescribeMap=>>>" + pdNameDescribeMap);
        long pdEndTime = System.currentTimeMillis();
        logger.debug("加载工序名称资源耗时：" + (pdEndTime - pdStartTime) + " ms");

        /*logger.debug("开始判断是否isTemplateCapp...");
    	long startTime = System.currentTimeMillis();
        WTPart tagerpart = TechnicsIntf.getPartByOid(childPartOid);
        if (tagerpart != null) {
            if (tagerpart.getNumber().endsWith("PROCESS_PLAN")
            		|| tagerpart.getNumber().endsWith("PROCESSPLAN")) {
                isTemplateCapp = true;
            }
        }
        logger.debug("isTemplateCapp=>>>" + isTemplateCapp);
        long endTime = System.currentTimeMillis();
        logger.debug("判断是否isTemplateCapp耗时：" + (endTime - startTime) + " ms");*/

        long allPartOidStartTime = System.currentTimeMillis();
        allPartOid = TechnicsIntf.getAllPartOidByOid(childPartOid);
        logger.debug("allPartOid= " + allPartOid);
        long allPartOidEndTime = System.currentTimeMillis();
        logger.debug("加载allPartOid耗时：" + (allPartOidEndTime - allPartOidStartTime) + " ms");


        //add By MChen
        try{
            dicNameMap= ErpToWCIntf.getAllTechncisMaterialDic();
        }catch (Exception e){
            logger.debug("加载失败...");
        }

        logger.debug("开始加载工序名称资源...");
        long stepStartTime = System.currentTimeMillis();
        if("SOP".equals(EditorConfig.startType)){
//        		SOP工序名称库
//            allStepNameMap = SopProcessUtil.getAllStepName();
//            SopIntf.initSopResource();

        	//以前的工序名称库
        	allStepNameMap = SopProcessUtil.getAllCZMC();
        }else{
            allStepNameMap = ResourceIntf.getProcessStepName();
        }
        long stepEndTime = System.currentTimeMillis();
        logger.debug("加载工序名称资源信息耗时 allStepNameMap：" + (stepEndTime - stepStartTime) + " ms");

        startAnimFrame.setHeaderMessage("查询当前用户信息");
        long loadUserStartTime = System.currentTimeMillis();
        List<String> list = UserUtil.getCurrentUserOid();
        currentUser = list.get(0);
        long loadUserEndTime = System.currentTimeMillis();
        logger.debug("查询当前用户信息耗时：" + (loadUserEndTime - loadUserStartTime) + " ms");

        startAnimFrame.setHeaderMessage("获取零部件类型属性配置信息");
        long attriMapUserStartTime = System.currentTimeMillis();
        //从服务器端EXCEL配置文件中读取所有零部件类型对应的显示属性
        attriMap = TechnicsIntf.getAttributes();
        long attriMapUserEndTime = System.currentTimeMillis();
        logger.debug("获取零部件类型属性配置信息耗时：" + (attriMapUserEndTime - attriMapUserStartTime) + " ms");
    }

    private static void setLookAndTheme() {
        try {
            System.setProperty("swing.useSystemFontSettings", "0");
            System.setProperty("swing.handleTopLevelPaint", "false");
            System.setProperty("-Dswing.aatext", "true");

            LookUtils.setLookAndTheme(new Plastic3DLookAndFeel(), new ExperienceBlue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getPbomOid() {
        return pbomOid;
    }

    public void clearRightContent() {
        this.dataPanel.removeAll();
        this.foregoingPanel = null;
        this.technicsMasterJPanel.clearUI();
        this.technicsStepJPanel.clearUI();
        this.technicsPaceJPanel.clearUI();
        this.rtPanel.deleteObservers();
        this.measurePanel.deleteObservers();
        this.ktPanel.deleteObservers();
        this.sDashboardPanel.deleteObservers();
        this.unSDashboardPanel.deleteObservers();
        this.mtPanel.deleteObservers();
        this.csTreePanel.deleteObservers();
        this.tecnicsPanel.repaint();
        repaint();
    }

    public void setContentIndex(int index) {
        this.tecnicsJTabbedPane.setSelectedIndex(index);
    }

    public void refreshData(XWTreeNode node) {
        if (node == null)
            return;
        XWTreeObject xo = node.getObject();
        if ((xo instanceof XWTechnicsTreeObject)) {
            Element ele = xo.getTreeCellData();
            String number = XmlUtility.getAttributeValue(ele, "technicsNumber");
            String technicsName = XmlUtility.getAttributeValue(ele, "technicsName");
            String technicsCategory = XmlUtility.getAttributeValue(ele, "technicsCategory");
            Document doc;
            if ("rework".equals(technicsCategory)) {
                doc = WorkSpaceUtil.getReWorkTechnicsDocumentByTechnicsName(number, technicsName);
            } else if ("temp".equals(technicsCategory)) {
                doc = WorkSpaceUtil.getTempTechnicsDocumentByTechnicsName(number, technicsName);
            } else {
                doc = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(number);
            }
            Element tech = XmlUtility.getTechnicsElement(doc);
            if (tech != null) {
//                logger.debug(tech.asXML());
                xo.setTreeCellData(tech);
            }
        }
    }

    public void expand(XWTreeNode node) {
        if (node != null) {
            node.removeAllChildren();
            this.technicsTreePanel.expandAllNode(node);
        }
    }

    public void delete() throws Exception {
        QuickCreateProcedureJPanel m = this.totalTechnicsRouteJPanel.getQuickCreateProcedureJPanel();
        Vector total = null;
        // 判断是否是切换到选中工艺树
        if (this.treeJTabbedPane.getSelectedIndex() == 0) {
            XWTreeNode treeNode = this.xwPartTreePanel.getSelectedTreeNode();
            XWTreeObject treeNodeObj = treeNode.getObject();
            if (treeNodeObj instanceof TechnicsMessageTreeObject) {
                total = this.technicsTreePanel.getSelectedPaths();
            } else if (treeNodeObj instanceof ReportTechnicsTreeObject) {
                deleteReportTechnicsNode(treeNode);
            }
        } else if (this.treeJTabbedPane.getSelectedIndex() == 1) {
            total = this.technicsTreePanel.getSelectedPaths();
        }

        if ((total != null) && (total.size() > 0)) {
            int reslut = JOptionPane.showConfirmDialog(this, "是否确定删除节点数据？", "提示", 0);
            if (reslut == 0) {
                Element techEle = XmlUtility.getTechnicsElement(getCurrentTechnics());
                //判断当前工艺文件关联的零部件是否已经发布，如果是发布了的则不能删除工艺文件
                String partOid = techEle.attributeValue("partOid");
                String partState = TechnicsIntf.getPartStateByOid(partOid);
                XWTreeNode nodeStep = (XWTreeNode) total.get(0);
                XWTreeObject xoStep = nodeStep.getObject();
                if ("已批准".equals(partState) && !(xoStep instanceof XWStepTreeObject)) {
                    JOptionPane.showMessageDialog(this, "PBOM已发布，不能删除工艺文件！", "提示", 1);
                    return;
                }
                String version = XmlUtility.getAttributeValue(techEle, "version");
                if (!version.startsWith("space")) {
                    for (int i = 0; i < total.size(); i++) {
                        XWTreeNode node = (XWTreeNode) total.get(i);
                        XWTreeObject xo = node.getObject();
                        if (xo instanceof XWTechnicsTreeObject) {
                            JOptionPane.showMessageDialog(this, "不能删除修订版本的工艺文件！", "提示", 1);
                            return;
                        }
                    }
                }
                String technicsNumber = XmlUtility.getAttributeValue(techEle, "technicsNumber");
                Boolean isEditState = TechnicsIntf.checkTechnicsState(technicsNumber);
                if (!isEditState) {
                    JOptionPane.showMessageDialog(this, "该工艺在服务端已处于不可编辑状态，无法删除该工艺文件！", "提示", 1);
                    return;
                }

                String unite = XmlUtility.getAttributeValue(techEle, "unite");
                if (unite == null)
                    unite = "";
                if (!unite.equals("common")) {
                    String user = getCurrentUser();
                    for (int i = 0; i < total.size(); i++) {
                        XWTreeNode node = (XWTreeNode) total.get(i);
                        XWTreeObject xo = node.getObject();
                        if ((xo instanceof XWStepTreeObject)) {
                            Element step = xo.getTreeCellData();
                            String responser = XmlUtility.getAttributeValue(step, "responser");
                            if ((responser != null) && (responser.trim().length() > 0) && (user != null) && (user.trim().length() > 0)) {
                                if (!user.equals(responser)) {
                                    String number = XmlUtility.getAttributeValue(step, "stepNumber");
                                    JOptionPane.showMessageDialog(this, number + "工序责任人不是当前用户，不能进行删除操作！", "提示", 1);
                                    return;
                                }
                            }
                        }
                    }
                }

                clearRightContent();
                for (int i = 0; i < total.size(); i++) {
                    XWTreeNode node = (XWTreeNode) total.get(i);
                    deleteOneNode(node);
                }

                XWTreeNode node = this.technicsTreePanel.getCurrentTechnicsNode();
                if (node != null) {
                    node.removeAllChildren();
                    DefaultTreeModel model = (DefaultTreeModel) this.technicsTreePanel.getTree().getModel();
                    model.reload(node);
                    expand(node);
                }
            }
        }
        m.changePreID();
        m.changePreStep();
    }

    public void deleteOneNode(XWTreeNode node) throws Exception {
        XWTreeObject xo = node.getObject();
        if (((xo instanceof XWProductTreeObject)) || ((xo instanceof XWPartTreeObject))) {
            throw new Exception("产品和零部件节点不能删除！");
        }
        Element ele = xo.getTreeCellData();
        if ((xo instanceof XWTechnicsTreeObject)) {
            String techNumber = XmlUtility.getAttributeValue(ele, "technicsNumber");
            String pplanNumber = XmlUtility.getAttributeValue(ele, "pplanNumber");
            String technicsName = XmlUtility.getAttributeValue(ele, "pplanName");
            String technicsCategory = XmlUtility.getAttributeValue(ele, "technicsCategory");
            String partOid = XmlUtility.getAttributeValue(ele, "partOid");
            TechnicsIntf.removeFromCache(partOid);
            //移除缓存中的oid
            // 删除所有相同partNumber的工艺节点
            List<XWTreeNode> xwTreeNodes = this.xwPartTreePanel.findAllTechnicsNodes(techNumber, technicsName);
            for (XWTreeNode xwTreeNode : xwTreeNodes) {
                this.xwPartTreePanel.removeNode(xwTreeNode);
                this.technicsTreePanel.removeCurrentTechnicd();
            }
            String path;
            if ("rework".equals(technicsCategory)) {
                path = WorkSpaceUtil.getReworkTechnicsDirectory(techNumber, technicsName);
            } else if ("temmp".equals(technicsCategory)) {
                path = WorkSpaceUtil.getTempTechnicsDirectory(techNumber, technicsName);
            } else {
                path = WorkSpaceUtil.getTechnicsDirectory(techNumber);
            }

            File f = new File(path);
            WorkSpaceUtil.delete(f);
            TechnicsIntf.deleteWTPartDescribeDocLink(techNumber);
            getQuickCreateProcedureJPanel().clear();
            getTechnicsRouteJPanel().clearAll();
        } else {
            // 删除主制工艺的工序要删除GL_ZHUFULINKMASTER中记录 start  add by zeng yao
            if (xo instanceof XWStepTreeObject) {
                String bsoid = xo.getTreeCellData().attributeValue("bsoID");
                if (bsoid != null && bsoid.length() > 0) {
                    XWTreeNode techNode = (XWTreeNode) node.getParent();
                    XWTreeObject object = techNode.getObject();
                    if (object instanceof XWTechnicsTreeObject) {
                        Element techElement = ((XWTechnicsTreeObject)object).getTreeCellData();
                        String flag = techElement.attributeValue("ZFFLAG");
                        if ("Z".equals(flag)) {
                            Map<String, String> params = new HashMap<String, String>();
                            String zDocNum = techElement.attributeValue("technicsNumber");
                            String version = techElement.attributeValue("version");
                            if (zDocNum != null && zDocNum.length() > 0 && version != null && version.length() > 0) {
                                version = version.lastIndexOf(".") > 0 ? version.substring(0, version.lastIndexOf(".")) : version;
                                params.put("ZZTECHNICSNUMBER", zDocNum);
                                params.put("ZZTECHNICSVERSION", version);
                                params.put("ZZPROCEDUREBSOID", bsoid);
                                TechnicsIntf.deleteZhuFuLink(params);
                            } else {
                                logger.error("无法获取工艺文档编号和版本");
                            }
                        }
                    } else {
                        logger.error("工序父节点不是工艺节点？？？");
                    }
                } else {
                    logger.error("工序BSOID为空: " + xo.getDisplayName());
                }
            }
            // 删除主制工艺的工序要删除GL_ZHUFULINKMASTER中记录  end add by zeng yao
            Element pare = ele.getParent();
            if (this.treeJTabbedPane.getSelectedIndex() == 1)
                this.technicsTreePanel.removeNode(node);
            pare.remove(ele);
            saveProcess(pare);
            if (this.tecnicsJTabbedPane.getSelectedIndex() == 1) {
                getQuickCreateProcedureJPanel().setTableValue(pare.getParent());
            }
        }
    }

    /**
     * 删除报表类工艺文件节点
     *
     * @param node
     * @throws Exception
     */
    public void deleteReportTechnicsNode(XWTreeNode node) throws Exception {
        XWTreeObject xo = node.getObject();
        if (((xo instanceof XWProductTreeObject)) || ((xo instanceof XWPartTreeObject))) {
            throw new Exception("产品和零部件节点不能删除！");
        }
        Element ele = xo.getTreeCellData();
        if (xo instanceof ReportTechnicsTreeObject) {
            String techNumber = XmlUtility.getAttributeValue(ele, "technicsNumber");
            String technicsName = XmlUtility.getAttributeValue(ele, "pplanName");

            clearRightContent();

            // 删除所有相同partNumber的工艺节点
            List<XWTreeNode> xwTreeNodes = this.xwPartTreePanel.findAllTechnicsNodes(techNumber, technicsName);
            for (XWTreeNode xwTreeNode : xwTreeNodes) {
                this.xwPartTreePanel.removeNode(xwTreeNode);
            }
            String path = WorkSpaceUtil.getTechnicsDirectory(techNumber);
            File f = new File(path);
            WorkSpaceUtil.delete(f);
            TechnicsIntf.deleteWTPartDescribeDocLink(techNumber);
        }
    }

    public void loadTechnics(Document technicsDom) {
        if (technicsDom == null) {
            return;
        }
        this.technicsTreePanel.hangTechnicsDocument(technicsDom);
        Element data = XmlUtility.getTechnicsElement(technicsDom);
        this.totalTechnicsRouteJPanel.setUIValues(data);
    }

    public void loadJsxyDoc(Document doc) {
        if (doc == null) {
            return;
        }
        this.technicsTreePanel.showJsxyDoc(doc);

    }

    /**
     * 刷新参装件
     *
     * @param xmlPath
     * @throws Exception
     */
    public void refreshParticipateParts(String xmlPath, String stepNumber) throws Exception {
        if (xmlPath == null || stepNumber == null)
            return;
        Document document = XmlUtil.getDocument(xmlPath);
        Element techElement = XmlUtility.getTechnicsElement(document);
        Element stepElement = techElement.element("steps");
        List<Element> tempElements = stepElement.elements("QMProcedureInfo");
        Element element = null;
        for (Element temp : tempElements) {
            if (stepNumber.equals(temp.attributeValue("stepNumber"))) {
                element = temp;
                break;
            }
        }
        Element elements = element.element("paces");
        List list = elements.elements("QMProcedureInfo");
        if ((list != null) && (list.size() > 0)) {
            int num = 1;
            for (int i = 0; i < list.size(); i++) {
                Element pace = (Element) list.get(i);
                logger.debug(pace.elements("parts").size());
                String parts = getTechnicsStepJPanel().getPaceTable().getAllParts(pace);
                String temp = XmlUtility.getAttributeValue(pace, "stepNumber");
                Vector cashe = XmlUtility.getPaceNumberCashe();
                getTechnicsStepJPanel().getPaceTable().setOneRowTableValue(pace, i);
                // if ((temp != null) && (!cashe.contains(temp))) {
                // XmlUtility.setAttributeValue(pace, "stepNumber", "-" + num);
                // num++;
                // }
            }
        } else {
            logger.debug("clearTable();");
        }
    }

    public void repaintTree() {
        if (this.xwPartTreePanel.getTree() != null)
            this.xwPartTreePanel.getTree().updateUI();
        if (this.technicsTreePanel.getTree() != null)
            this.technicsTreePanel.getTree().updateUI();
    }

    public void runDiagramProgram() throws Exception {
        WorkSpaceUtil.startTechnicsDiagram();
    }

    public void runMiddleModuleProgram() throws Exception {
        WorkSpaceUtil.startMidModel();
    }

    public void runVisualProgram() throws Exception {
        WorkSpaceUtil.startVideo();
    }

    public void runAssembleCartoonProgram() throws Exception {
        WorkSpaceUtil.startAssemblageCartoon();
    }

    public void updatePBOM() throws Exception {
        Element productElement = this.xwPartTreePanel.getCurrentProductElement();
        if (productElement == null) {
            JOptionPane.showMessageDialog(this, "产品为空！", "提示", 1);
            return;
        }
        Element mainPart = BomXMLUtil.getMainPart(productElement);
        String oid = mainPart.attributeValue("oid");
        if (oid != null) {
            pbomBytes = PBomIntf.getPBomXML(oid);
            FileOutputStream fos = new FileOutputStream(System.getProperty("user.home") + "//pbom.xml");
            fos.write(pbomBytes);
            fos.close();
            if (pbomBytes != null) {
                Document doc = BomXMLUtil.getDocument(pbomBytes);
                if (doc != null) {
                    Element product = BomXMLUtil.getProduct(doc);
                    if (product != null) {
                        Element main = BomXMLUtil.getMainPart(productElement);

                        reSetTechnicsPBOMData(main);

                        this.xwPartTreePanel.loadParts(doc);

                        Document techDOC = getCurrentTechnics();
                        clearRightContent();
                        getQuickCreateProcedureJPanel().clear();
                        getTechnicsRouteJPanel().clearAll();
                        this.creopanel.setVisible(false);
                        this.panel3D.setVisible(false);
                        if (techDOC != null) {
                            Element curTech = XmlUtility.getTechnicsElement(techDOC);
                            String techNUm = curTech.attributeValue("technicsNumber");
                            XWTreeNode node = this.xwPartTreePanel.findTechnicsNode(techNUm);
                            if (node != null) {
                                this.xwPartTreePanel.getTree().setSelectionPath(new TreePath(node.getPath()));
                                this.xwPartTreePanel.getTree().updateUI();
                            }
                        }
                    }
                }
            }
        }
    }

    public void resetPbomBatch(String pbomOid) throws Exception {
        System.out.println("------resetPbomBatch()------pbomOid=" + pbomOid);
        Element productElement = this.xwPartTreePanel.getCurrentProductElement();
        if (productElement == null) {
            JOptionPane.showMessageDialog(this, "产品为空！", "提示", 1);
            return;
        }
//        pbomBytes = PBomIntf.getPBomXML(pbomOid);
        String[] str = {pbomOid};
        pbomBytes = PbomUtil.generatePbomBytes(str);
        System.out.println("------resetPbomBatch()------pbomBytes=" + pbomBytes);
        FileOutputStream fos = new FileOutputStream(System.getProperty("user.home") + "//pbom.xml");
        fos.write(pbomBytes);
        fos.close();
        if (pbomBytes != null) {
            Document doc = BomXMLUtil.getDocument(pbomBytes);
            if (doc != null) {
                Element product = BomXMLUtil.getProduct(doc);
                if (product != null) {
                    System.out.println("---------------updateTechnics----------");
                    updateTechnics(pbomBytes);

                    Element main = BomXMLUtil.getMainPart(productElement);
                    System.out.println("-----------------reSetTechnicsPBOMData---------------");
                    reSetTechnicsPBOMData(main);
                    System.out.println("-----------------loadParts加载PBOM树---------------");
                    this.xwPartTreePanel.loadParts(doc);
                    System.out.println("-----------------getCurrentTechnics---------------");
                    Document techDOC = getCurrentTechnics();
                    System.out.println("-----------------clear---------------");
                    clearRightContent();
                    getQuickCreateProcedureJPanel().clear();
                    getTechnicsRouteJPanel().clearAll();
                    this.creopanel.setVisible(false);
                    this.panel3D.setVisible(false);
                    if (techDOC != null) {
                        System.out.println("-----------------getTechnicsElement---------------");
                        Element curTech = XmlUtility.getTechnicsElement(techDOC);
                        String techNUm = curTech.attributeValue("technicsNumber");
                        XWTreeNode node = this.xwPartTreePanel.findTechnicsNode(techNUm);
                        if (node != null) {
                            this.xwPartTreePanel.getTree().setSelectionPath(new TreePath(node.getPath()));
                            this.xwPartTreePanel.getTree().updateUI();
                        }
                    }else{
                        this.xwPartTreePanel.getTree().updateUI();
                    }

                }
            }
        }
    }

    public void reSetTechnicsPBOMData(Element mainPart) throws Exception {
        if (mainPart != null) {
            String partNumber = mainPart.attributeValue("partNumber");
            List<String> reworkPaths = WorkSpaceUtil.getReworkTechnicsXmlPaths(partNumber);
            if (reworkPaths != null) {
                for (String temp : reworkPaths) {
                    Document doc = XmlUtil.getDocument(temp);
                    resetData(doc, mainPart);
                }
            }
            if (WorkSpaceUtil.getTechnicsDirectory(partNumber) != null) {
                Document doc = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(partNumber);
                if (doc != null) {
                    resetData(doc, mainPart);
                }
            }
            List list = BomXMLUtil.getChildProducts(mainPart);
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    Element childPart = (Element) list.get(i);
                    reSetTechnicsPBOMData(childPart);
                }
            }
        }
    }

    public void resetData(Document doc, Element mainPart) {
        String oid = mainPart.attributeValue("oid");
        if (doc != null) {
            Element techElement;
            try {
                techElement = XmlUtility.getTechnicsElement(doc);
                if (techElement != null) {
                    String partOid = techElement.attributeValue("partOid");
                    if ((oid != null) && (partOid != null)) {
                        XmlUtility.setAttributeValue(techElement, "treePath", BomXMLUtil.getPath(mainPart));
                        XmlUtility.setAttributeValue(techElement, "partNumber", mainPart.attributeValue("partNumber"));
                        XmlUtility.setAttributeValue(techElement, "partName", mainPart.attributeValue("partName"));
                        XmlUtility.setAttributeValue(techElement, "partOid", mainPart.attributeValue("oid"));
                        XmlUtility.setAttributeValue(techElement, "partVersion", mainPart.attributeValue("partVersion"));
                        XmlUtility.setAttributeValue(techElement, "materialType", mainPart.attributeValue("materialType"));
                        XmlUtility.setAttributeValue(techElement, "workShop", mainPart.attributeValue("workShop"));
                        XmlUtility.setAttributeValue(techElement, "backupRate", mainPart.attributeValue("backupRate"));
                        XmlUtility.setAttributeValue(techElement, "maxBackupCount", mainPart.attributeValue("maxBackupCount"));
                        XmlUtility.setAttributeValue(techElement, "backupReason", mainPart.attributeValue("backupReason"));
                        XmlUtility.setAttributeValue(techElement, "remark", mainPart.attributeValue("remark"));
                        XmlUtility.setAttributeValue(techElement, "isKey", mainPart.attributeValue("isKey"));
                        XmlUtility.setAttributeValue(techElement, "isSpecial", mainPart.attributeValue("isSpecial"));
                        XmlUtility.setAttributeValue(techElement, "partType", mainPart.attributeValue("partType"));
                        XmlUtility.setAttributeValue(techElement, "isPartKey", mainPart.attributeValue("isKey"));
                        XmlUtility.setAttributeValue(techElement, "eu_version", mainPart.attributeValue("eu_version"));
                        XmlUtility.setAttributeValue(techElement, "e_version", mainPart.attributeValue("e_version"));
                        XmlUtility.setAttributeValue(techElement, "partVersion", mainPart.attributeValue("version"));
                        XmlUtility.setAttributeValue(techElement, "pbomLifecycle", mainPart.attributeValue("lifecycle"));
                        XmlUtility.setAttributeValue(techElement, "occId", mainPart.attributeValue("occId"));
                        XmlUtility.setAttributeValue(techElement, "material", mainPart.attributeValue("material"));
                        XmlUtility.setAttributeValue(techElement, "dutu", mainPart.attributeValue("dutu"));
                        XmlUtility.setAttributeValue(techElement, "useCount", mainPart.attributeValue("useCount"));

                        Element productElement = null;
                        Element rootPart = null;
                        productElement = BomXMLUtil.getProductMessage(mainPart);
                        rootPart = BomXMLUtil.getMainPart(BomXMLUtil.getProduct(mainPart.getDocument()));
                        XmlUtility.setAttributeValue(techElement, "productNumber", productElement.attributeValue("productNumber"));
                        XmlUtility.setAttributeValue(techElement, "productName", productElement.attributeValue("productName"));
                        XmlUtility.setAttributeValue(techElement, "parentPartNumber", rootPart.attributeValue("partNumber"));
                        XmlUtility.setAttributeValue(techElement, "parentPartName", rootPart.attributeValue("partName"));
                        XmlUtility.setAttributeValue(techElement, "parentPartOid", rootPart.attributeValue("oid"));
                        String materialNumber = mainPart.attributeValue("materialNumber");
                        String materialName = mainPart.attributeValue("materialName");
                        if (materialNumber == null)
                            materialNumber = "";
                        if (materialName == null)
                            materialName = "";
                        if ((materialNumber.trim().length() > 0) || (materialName.trim().length() > 0)) {
                            Element materialELement = XmlUtility.createMaterial();
                            XmlUtility.setAttributeValue(materialELement, "materialNumber", materialNumber);
                            XmlUtility.setAttributeValue(materialELement, "materialName", materialName);
                            XmlUtility.addMaterial(techElement, materialELement);
                        }
                        saveProcess(techElement);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void startPBOM(XWPartTreePanel xwPartTreePanel) {
        Element productElement = xwPartTreePanel.getCurrentProductElement();
        if (productElement == null)
            return;
        Element partElement = BomXMLUtil.getMainPart(productElement);
        if (partElement == null)
            return;
        String topPartOid = partElement.attributeValue("oid");
        String topPartNumber = partElement.attributeValue("partNumber");
        try {
            PbomUtil.startPBOM(topPartOid, topPartNumber);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "启动PBOM编辑器时出现错误！", "提示", 1);
        }
    }

    private void templateMaintain() {
        try {
            TemplateMaintainDialog dialog = new TemplateMaintainDialog(WorkSpaceUtil.getTempletRootPath(), this);
            dialog.showDialog();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "本地工艺模板维护过程出现错误！", "提示", 1);
        }
    }

    private void procedureTempletMaintain() {
        try {
            logger.debug("工序模板维护");
            StepTemplateMaintainDialog dialog = new StepTemplateMaintainDialog(WorkSpaceUtil.getStepRootPath(), this);
            dialog.showDialog();
        } catch (Exception e) {

            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "本地工序模板维护过程出现错误！", "提示", 1);
        }
    }

    public void maintenancePersonalTerminology() {
        try {
            String terminologyXMLPath = WorkSpaceUtil.getPersonalTerminologyDirectory();
            CsMaintainDialog dia = new CsMaintainDialog(terminologyXMLPath, this);
            dia.showDialog();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "个人工艺常用语维护过程出现错误！", "提示", 1);
        }
    }

    public void downloadCsTemplate() {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            File file = FileChooserTool.getDirectory(this);
            if (file != null) {
                if (file.isDirectory() && file.exists()) {
                    is = this.getClass().getResourceAsStream("/templates/常用语模板.xls");
                    if (is != null) {
                        fos = new FileOutputStream(file + File.separator + "常用语模板.xls");
                        byte[] b = new byte[1024];
                        int temp = 0;
                        while ((temp = is.read(b)) != -1) {
                            fos.write(b, 0, temp);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "下载常用语模板过程出现错误！", "提示", 1);
        } finally {
            JavaUtil.closeStream(is);
            JavaUtil.closeStream(fos);
        }
    }

    public void importCommonString() {
        try {
            File file = FileChooserTool.getSaveFile("xls", this);
            if (file != null) {
                if (file.isFile() && file.exists()) {
                    if (file.renameTo(file)) {
                        HSSFWorkbook workbook = ExcelUtil.getWorkbook(file);
                        CommonStringUtil.importCommonStringFromWorkbook(workbook);
                        CsTreeXmlUtil.refreshCsTree();
                        JOptionPane.showMessageDialog(NewTechnicsPart.this, "个人工艺常用语导入成功！", "提示", 1);
                    } else {
                        JOptionPane.showMessageDialog(this, "另一个程序正在使用此文件！", "提示", 1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "导入个人工艺常用语过程出现错误！", "提示", 1);
        }
    }

    public void exportCommonString() {
        try {
            File file = FileChooserTool.getSaveFile("xls", this);
            if (file != null) {
                if (file.isFile() && file.exists()) {
                    if (!file.renameTo(file)) {
                        JOptionPane.showMessageDialog(this, "另一个程序正在使用此文件！", "提示", 1);
                        return;
                    }
                }
                CommonStringUtil.exportCommonString(file.getPath());
                JOptionPane.showMessageDialog(NewTechnicsPart.this, "个人工艺常用语导出成功！", "提示", 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "导出个人工艺常用语过程出现错误！", "提示", 1);
        }
    }

    public void viewHistoryTechnics() {
        logger.debug("查看历史");
        ViewHistory.openHistoryUrl();
    }

    public void qmViewHistorytechnics() {
        new NewTechnicsHistorySelect(this);
    }

    public void technicsCoWork() {
        try {
            Document document = getCurrentTechnics();
            if (document != null) {
                XWTreeNode node = this.technicsTreePanel.getCurrentTechnicsNode();
                TechnicsCombineDialog dialog = new TechnicsCombineDialog(this);
                this.technicsTreePanel.expandAllNode(node);
                this.technicsTreePanel.getTree().setSelectionPath(new TreePath(node.getPath()));
                this.xwPartTreePanel.getTree().updateUI();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void technicsRouteConfirm() {
        logger.debug("工艺路线确认...................");
        Document document = getCurrentTechnics();
        if (document == null)
            return;
        XWTreeNode node = this.technicsTreePanel.getCurrentTechnicsNode();

        Element techElement = null;
        String version = null;
        try {
            techElement = XmlUtility.getTechnicsElement(document);
//            logger.debug("----------------------------");
//            logger.debug(techElement.asXML());
//            logger.debug("-----------------------------------");
            version = techElement.attributeValue("version");
            if ((version == null) || (version.equals(""))) {
                version = "1.0";
            }

            XmlUtility.setAttributeValue(techElement, "unite", "route");
            String topPartOid = techElement.attributeValue("parentPartOid");
            String partOid = techElement.attributeValue("partOid");
            String pplanNumber = techElement.attributeValue("pplanNumber");
            String technicsName = WorkSpaceUtil.getTechnicsDirectory(techElement.attributeValue("technicsNumber"));
            int index = technicsName.lastIndexOf("\\");
            if (index >= 0) {
                technicsName = technicsName.substring(index + 1);
            }
            saveProcess(techElement);
            byte[] technicsZip = FilesUtil.getTechnicsByte(techElement.attributeValue("technicsNumber"));
            String technicsType = techElement.attributeValue("technicsType");
            HashMap map = TechnicsIntf.routeConfirm(topPartOid, partOid, technicsName, technicsType, technicsZip, version, pplanNumber);
            String success = (String) map.get("success");
            if (success.equals("success")) {
                String newVersion = (String) map.get("version");
                String lifecycle = (String) map.get("lifecycle");
                XmlUtility.setAttributeValue(techElement, "version", newVersion);
                XmlUtility.setAttributeValue(techElement, "lifecycle", lifecycle);
                saveProcess(techElement);
                XWTreeNode rootNode = (XWTreeNode) xwPartTreePanel.getTree().getModel().getRoot();
                updateTreeTechnics(rootNode, techElement.attributeValue("partOid"), techElement.attributeValue("technicsName"), techElement.attributeValue("technicsCategory"), newVersion);
                xwPartTreePanel.expandAllNode(rootNode);
                technicsMasterJPanel.setUIValues(techElement);
                JOptionPane.showMessageDialog(this, "启动工艺路线确认成功！", "提示", 1);
            } else {
                String errorMessage = (String) map.get("errorMessage");
                if ((errorMessage != null) && (!errorMessage.equals(""))) {
                    JOptionPane.showMessageDialog(this, errorMessage, "提示", 1);
                } else {
                    JOptionPane.showMessageDialog(this, "工艺路线确认失败！", "提示", 1);
                }
            }

            this.technicsTreePanel.expandAllNode(node);
            this.technicsTreePanel.getTree().setSelectionPath(new TreePath(node.getPath()));
            this.xwPartTreePanel.getTree().updateUI();
        } catch (Exception e1) {

            e1.printStackTrace();
            JOptionPane.showMessageDialog(this, "工艺路线确认出现错误！", "提示", 1);
        }
    }

    public void initTechnicsRoutePanel(Element element) throws Exception {
        getTechnicsRouteJPanel().clearAll();
        // createTechnicsRoute();
        String technicsNumber = XmlUtility.getAttributeValue(element, "technicsNumber");
        String technicsName = XmlUtility.getAttributeValue(element, "technicsName");
        String technicsCategory = XmlUtility.getAttributeValue(element, "technicsCategory");
        Document document = getCurrentTechnics();
        if (document != null) {
            try {
                Element techElement = XmlUtility.getTechnicsElement(document);
                String lifecycle = techElement.attributeValue("lifecycle");
                if ((lifecycle != null) && (!"".equals(lifecycle)) && (!lifecycle.equals("拟制")) && (!lifecycle.equals("驳回"))) {
                    // getTechnicsRouteJPanel().setEventEnabled(false);
                    // getTechnicsRouteToolBar().setToolBarEnabled(false);
                } else {
                    getTechnicsRouteJPanel().setEventEnabled(true);
                    getTechnicsRouteToolBar().setToolBarEnabled(true);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "工艺路线图读取当前工艺节点时出错！", "提示", 1);
                e.printStackTrace();
            }
        }
        getTechnicsRouteJPanel().setTechnicsNumber(technicsNumber);
        getTechnicsRouteJPanel().setTechnicsName(technicsName);
        getTechnicsRouteJPanel().setTechnicsCategory(technicsCategory);
        try {
            File file = null;
            if ("rework".equals(technicsCategory)) {
                file = TechnicsRouteUtil.getReworkTechnicsRouteXML(technicsNumber, technicsName);
            } else if ("temp".equals(technicsCategory)) {
                file = TechnicsRouteUtil.getTempTechnicsRouteXML(technicsNumber, technicsName);
            } else {
                file = TechnicsRouteUtil.getTechnicsRouteXML(technicsNumber);
            }
            if (file == null) {
                createTechnicsRoute();
                return;
            }
            TechnicsRouteUtil.getTechnicsRoute(XmlUtil.getDocument(file), getTechnicsRouteJPanel());
        } catch (Exception e1) {
            e1.printStackTrace();
            //JOptionPane.showMessageDialog(this, "生成工艺路线图出现错误！", "提示", 1);
        }
    }

    public String getUITechnicsNumber() throws Exception {
        Document document = getCurrentTechnics();
        if (document == null) {
            return null;
        }
        return TechnicsRouteUtil.getTechnicsNumber(document);
    }

    public void viewStepMessage(String bsoID) throws Exception {
        XWTreeNode node = this.technicsTreePanel.getStepNode(bsoID);
        if (node != null) {
            this.foregoingPanel = null;
            this.tecnicsJTabbedPane.setSelectedIndex(0);

            node = this.technicsTreePanel.getStepNode(bsoID);

            TreePath path = new TreePath(node.getPath());
            this.technicsTreePanel.getTree().setSelectionPath(path);
        }
    }

    public Document getCurrentTechnics() {
        Document doc = null;
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.technicsTreePanel.getTree().getModel().getRoot();
        if (root.getChildCount() > 0) {
            XWTreeNode node = (XWTreeNode) root.getChildAt(0);
            if (node != null)
                doc = node.getObject().getTreeCellData().getDocument();
        }
        return doc;
    }

    public QuickCreateProcedureJPanel getQuickCreateProcedureJPanel() {
        return this.totalTechnicsRouteJPanel.getQuickCreateProcedureJPanel();
    }

    public TechnicsRouteToolBar getTechnicsRouteToolBar() {
        return this.totalTechnicsRouteJPanel.getTechnicsRouteToolBar();
    }

    public TechnicsRouteJPanel getTechnicsRouteJPanel() {
        return this.totalTechnicsRouteJPanel.getTechnicsRouteJPanel();
    }

    public TechnicsStepJPanel_XW getTechnicsStepJPanel() {
        return this.technicsStepJPanel;
    }

    /**
     * 判断是否已经提交签审
     * <p>
     * LongXiuChuan
     *
     * @return
     */
    public boolean isSubmited() {

        XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
        if (node != null) {
            XWTreeObject xo = node.getObject();
            if (xo instanceof XWTechnicsTreeObject) {
                Element element = xo.getTreeCellData();
                String technicsNumber = element.attributeValue("technicsNumber");
                return TechnicsIntf.isSubmited(technicsNumber);
            }
        } else {
            XWTreeNode node2 = NewTechnicsPart.this.xwPartTreePanel.getSelectedTreeNode();
            if (node2 != null) {
                XWTreeObject xo = node2.getObject();
                if (xo instanceof ReportTechnicsTreeObject) {
                    Element element = xo.getTreeCellData();
                    String technicsNumber = element.attributeValue("technicsNumber");
                    return TechnicsIntf.isSubmited(technicsNumber);
                }
            }
        }
        return false;
    }

    /**
     * 判断是否正在工作
     * <p>
     * LongXiuChuan
     *
     * @return
     */
    public boolean isWorking() {
        XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
        if (node != null) {
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWTechnicsTreeObject)) {
                Element element = xo.getTreeCellData();
                String state = element.attributeValue("lifecycle");
                if ("正在工作".equals(state)) {
                    return true;
                }
            }
        } else {
            node = NewTechnicsPart.this.xwPartTreePanel.getSelectedTreeNode();
            if (node != null) {
                XWTreeObject xo = node.getObject();
                if ((xo instanceof ReportTechnicsTreeObject)) {
                    Element element = xo.getTreeCellData();
                    String state = element.attributeValue("lifecycle");
                    if ("正在工作".equals(state)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断是否更改工艺文件
     * <p>
     * LongXiuChuan
     *
     * @return
     */
    public boolean hasGengGaiDan() {
        XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
        if (node != null) {
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWTechnicsTreeObject)) {
                Element element = xo.getTreeCellData();
                String techNo = XmlUtility.getAttributeValue(element, "technicsNumber");
                String ecnNo = (String) IntfUtil.getPeRemoteMethodInvoke("getChangeNoByTechnics", new Class[] { String.class }, new Object[] { techNo });
                if (ecnNo != null && !"".equals(ecnNo)) {
                    return true;
                }
            }
        } else {
            node = NewTechnicsPart.this.xwPartTreePanel.getSelectedTreeNode();
            if (node != null) {
                XWTreeObject xo = node.getObject();
                if ((xo instanceof ReportTechnicsTreeObject)) {
                    Element element = xo.getTreeCellData();
                    String techNo = XmlUtility.getAttributeValue(element, "technicsNumber");
                    String ecnNo = (String) IntfUtil.getPeRemoteMethodInvoke("getChangeNoByTechnics", new Class[] { String.class }, new Object[] { techNo });
                    if (ecnNo != null && !"".equals(ecnNo)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean needProcessTaskItem() {
        XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
        if (node != null) {
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWTechnicsTreeObject)) {
                Element element = xo.getTreeCellData();
                String version = element.attributeValue("version");
                if (version == null || version.isEmpty() || version.startsWith("space")) {
                    return true;
                }
            }
        } else {
            node = NewTechnicsPart.this.xwPartTreePanel.getSelectedTreeNode();
            if (node != null) {
                XWTreeObject xo = node.getObject();
                if ((xo instanceof ReportTechnicsTreeObject)) {
                    Element element = xo.getTreeCellData();
                    String version = element.attributeValue("version");
                    if (version.startsWith("space")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断是否已经材料
     * <p>
     * LongXiuChuan
     *
     * @return
     */
    public boolean isCldeAndDyde() {
        XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
        if (node != null) {
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWTechnicsTreeObject)) {
                Element element = xo.getTreeCellData();
                Element clde = XmlUtility.getTechnicsCLDEElement(element);
                boolean isClde = false;
                if (clde != null) {
                    List<Element> ycl = XmlUtility.getTechnicsYCLDE(clde);
                    if (ycl != null && !ycl.isEmpty()) {
                        isClde = true;
                    }
                    List<Element> zycl = XmlUtility.getTechnicsZYCLDE(clde);
                    if (zycl != null && !zycl.isEmpty()) {
                        isClde = true;
                    }
                    List<Element> sjycl = XmlUtility.getTechnicsSJYCLDE(clde);
                    if (sjycl != null && !sjycl.isEmpty()) {
                        isClde = true;
                    }
                }

                Element gyde = XmlUtility.getTechnicsDEElement(element);
                boolean isGyde = false;
                if (gyde != null) {
                    List<Element> newParts = XmlUtility.getTechnicsGYDENewPart(gyde);
                    if (newParts != null && !newParts.isEmpty()) {
                        isGyde = true;
                    }
                    List<Element> matchParts = XmlUtility.getTechnicsGYDEMatchPart(gyde);
                    if (matchParts != null && !matchParts.isEmpty()) {
                        isGyde = true;
                    }
                    List<Element> zyclde = XmlUtility.getTechnicsZYCLDE(gyde);
                    if (zyclde != null && !zyclde.isEmpty()) {
                        isGyde = true;
                    }
                }

                if (isClde && isGyde) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public void commitForSignedThread(String flag) {
        if (isSubmited()) {
            JOptionPane.showMessageDialog(NewTechnicsPart.this, "该工艺文件已经提交签审，不能再提交！", "提示", 1);
            return;
        }

        //提交签审前，校验工序流程图
        try {
            if(!getTechnicsRouteJPanel().checksaveTechnicsRoute()){
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        isSanJiGengGai = "false";
        XWTreeNode technicsnode = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
        if (technicsnode != null) {
            //------辅制工艺提交签审校验   add by liangbo --- start----
            XWTreeObject obj = technicsnode.getObject();
            Element technicsElement = obj.getTreeCellData();
            //校验SOP要求值是否全部填写
//            String msg = SopProcessUtil.checkYQZ(technicsElement);
//            if(msg != null && !msg.isEmpty()){
//                JOptionPane.showMessageDialog(this, msg, "提示", JOptionPane.OK_OPTION);
//                return;
//            }
            if(!checkBeforeSubmit(technicsElement, this)){
                return;
            }
        }

        //---------------------  end -------------------------
        if (hasGengGaiDan()) {
            JOptionPane.showMessageDialog(NewTechnicsPart.this, "已经提交更改签审任务不允许再提交签审！", "提示", 1);
            return;
        } else {
            if ("3".equals(flag)) {
                if (technicsnode != null) {
                    if (isSanJiGengGai(technicsnode)) {
                        isSanJiGengGai = "true";
                    }
                }
            }
        }


        if (!isWorking()) {
            JOptionPane.showMessageDialog(NewTechnicsPart.this, "只有正在工作状态下才能再提交签审！", "提示", 1);
            return;
        }

        XWTreeNode node = NewTechnicsPart.this.xwPartTreePanel.getSelectedTreeNode();
        if (node != null && !(node.getObject() instanceof ReportTechnicsTreeObject) && !"SOP".equals(EditorConfig.startType)) {
            if (!isCldeAndDyde()) {
                int opt = JOptionPane.showConfirmDialog(NewTechnicsPart.this, "您提交签审的工艺文件中没有进行材料定额或工艺定额，请确认是否提交签审？", "提示", JOptionPane.YES_NO_OPTION);
                if (opt != 0) {
                    return;
                }
            }
        }
        //校验参装件数量是否多装 Start
        if(node.getObject() instanceof TechnicsMessageTreeObject){
            XWTreeNode parent = (XWTreeNode) node.getParent();
            XWTreeObject parentObj = parent.getObject();
            Element techEle = node.getObject().getTreeCellData();
            if(!checkCanZhuang4Technics(parentObj, techEle, this)){
                return;
            }
        }
        //校验参装件数量是否多装 end
        if (node != null && (node.getObject() instanceof ReportTechnicsTreeObject)) {
            ReportTechnicsTreeObject obj = (ReportTechnicsTreeObject) node.getObject();
            if ("工艺文件目录".equals(obj.getType())) {
                Element element = obj.getTreeCellData();
                if(!check4Gongyimulu(element, this)) {
                    return;
                }
            }
        }

        submitFlag = flag;
        final VaActionProgressBar progressBar = new VaActionProgressBar(NewTechnicsPart.this, "提交签审", "正在提交签审,请等待...", "签审提交中");
        Thread thread = new Thread() {
            public void run() {
                XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
                if (node != null) {
                    XWTreeObject xo = node.getObject();
                    if ((xo instanceof XWTechnicsTreeObject)) {
                        boolean nptflag = needProcessTaskItem();
                        if (!NewTechnicsPart.isTemplateCapp && nptflag) {
                            CommitForSignedDialog dialog = null;
                            try {
                                dialog = new CommitForSignedDialog(NewTechnicsPart.this);
                            } catch (Exception e3) {
                                e3.printStackTrace();
                            }
                            String comment = dialog.showDialog();

                            if (comment != null) {
                                BianzhiOid = dialog.content;
                                BianzhiOid = BianzhiOid.split("ext.casc.process.ProcessTaskItem:")[1];
                            } else {
                                JOptionPane.showMessageDialog(NewTechnicsPart.this, "必须要选择相关的工艺任务才能提交签审！", "提示", 1);
                                progressBar.finish();
                                progressBar.setVisible(false);
                                return;
                            }
                            if (!CommitForSignedDialog.flag) {
                                return;
                            }
                        }
                        Element element = xo.getTreeCellData();
                        // List<Element> subPartOids = PbomUtil.getSubPartOids(
                        // xwPartTreePanel.get Tree(),
                        // element.attributeValue("partOid"));

                       /* if (!"".equals(TechnicsUtil.checkTechnics(NewTechnicsPart.this, element))) {
                            progressBar.finish();
                            progressBar.setVisible(false);
                            return;
                        }*/
                        logger.debug("开始校验工艺文件...");
                        long startTime4 = System.currentTimeMillis();
                        // 校验工艺文件
                        boolean checkOk = true;
						try {
                            checkOk = check(element, progressBar, NewTechnicsPart.this, NewTechnicsPart.this);
						} catch (RemoteException e3) {
							// TODO Auto-generated catch block
							e3.printStackTrace();
						} catch (WTException e3) {
							// TODO Auto-generated catch block
							e3.printStackTrace();
						} catch (InvocationTargetException e3) {
							// TODO Auto-generated catch block
							e3.printStackTrace();
						}
                        long endTime4 = System.currentTimeMillis();
                        logger.debug("校验工艺文件耗时："+(endTime4-startTime4)+" ms");
                        if(!checkOk) {
                        	progressBar.finish();
                            progressBar.setVisible(false);
                            return;
                        }

                        String technicsNumber = element.attributeValue("technicsNumber");

                        // 如果该工艺文件新建或修改后还未上载，则需要先上载
                        Vector<UploadTechnics> editTechnics = NewTechnicsPart.this.editTechnics;
                        if (editTechnics != null) {
                            for (UploadTechnics temp : editTechnics) {
                                if (temp.getTechnicsNumber().equals(technicsNumber)) {
                                	Document doc = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(technicsNumber);
                            		Element technicsElement = XmlUtility.getTechnicsElement(doc);
                                    boolean canTransPdf = TechnicsUtil.checkWordTransPDF(technicsElement);
                                    if (!canTransPdf) {
                                        progressBar.finish();
                                        progressBar.setVisible(false);
                                        JOptionPane.showMessageDialog(null, "Word转换PDF失败，请尝试通过以下方式解决!\n" + "1、请检查确认jacob插件是否安装;\n" + "2、若1已安装，请尝试升级本地Word至Word2010;\n" + "3、若以上都不行，请联系管理员！");
                                        return;
                                    }
                                    RemarkJDialog remarkJDialog = new RemarkJDialog("工艺上载", NewTechnicsPart.this);
                                    String remark = remarkJDialog.showDialog();

                                    if (remark == null) {
                                        progressBar.finish();
                                        progressBar.setVisible(false);
                                        return;
                                    }


                                    String technicsCategory = element.attributeValue("technicsCategory");
                                    String technicsName = element.attributeValue("technicsName");
                                    String version = element.attributeValue("version");
                                    String technicsType = element.attributeValue("technicsType"); // 工艺类型
                                    if ((version == null) || (version.equals(""))) {
                                        version = "1.0";
                                    }

                                    String pdfFile = null;
                                    String path = null;
                                    try {
                                        path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber, technicsName, technicsCategory);
                                    } catch (Exception e2) {
                                        // TODO Auto-generated catch block
                                        e2.printStackTrace();
                                    }
                                    pdfFile = path + File.separator + "PDFPreview.pdf";
                                    File floder1 = new File(pdfFile);
                                    if (floder1.exists()) {
                                        boolean flag = floder1.delete();
                                        if (!flag) {
                                            JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺名称为:" + technicsName + "的PDF文件正在使用，请先关闭再上载！");
                                            progressBar.finish();
                                            progressBar.setVisible(false);
                                            return;
                                        }
                                    }
//									String pdfFile = null;
//									try {
//										progressBar.setHeaderMessage("开始生成工艺文件PDF格式！");
//										path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber,technicsName, technicsCategory);
//										File floder = new File(path,"fbtemp");
//										PDFPreviewFactory.preview(path, false,null);
//										pdfFile = path + File.separator + "PDFPreview.pdf";
//										FileUtil.deleteFile(floder);
//										progressBar.setHeaderMessage("结束生成工艺文件PDF格式！");
//									} catch (Exception e) {
//										e.printStackTrace();
//									}

                                    try {
                                        progressBar.setHeaderMessage("开始获取上载流！");
                                        byte[] bytes;
                                        String technicsDirectory = "";
                                        if ("rework".equals(technicsCategory)) {
                                            bytes = FilesUtil.getReworkTechnicsByte(technicsNumber, technicsName);
                                            technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
                                        } else if ("temp".equals(technicsCategory)) {
                                            bytes = FilesUtil.getTempTechnicsByte(technicsNumber, technicsName);
                                            technicsDirectory = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
                                        } else {
                                            bytes = FilesUtil.getTechnicsByte(technicsNumber, progressBar);
                                            technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
                                        }
                                        progressBar.setHeaderMessage("结束获取上载流！");
                                        String technicsFolderName = technicsDirectory.substring(technicsDirectory.lastIndexOf("\\") + 1, technicsDirectory.length());
                                        String partNumber = element.attributeValue("partNumber");
                                        String partOid = element.attributeValue("partOid");
                                        String partName = element.attributeValue("partName");
                                        String partType = element.attributeValue("partType");
                                        String pplanNumber = element.attributeValue("pplanNumber");
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map.put("oid", partOid);
                                        map.put("partNumber", partNumber);
                                        map.put("partName", partName);
                                        map.put("partType", partType);
                                        map.put("technicsType", technicsType);
                                        map.put("technicsNumber", technicsNumber);
                                        map.put("technicsName", technicsName);
                                        map.put("pplanNumber", pplanNumber);
                                        if ("SOP标准操作规程".equals(technicsType)) {
                                            map.put("SopNumber", element.attributeValue("SopNumber"));
                                            map.put("SpecializedType", element.attributeValue("SpecializedType"));
                                            map.put("ProceduceName", element.attributeValue("ProceduceName"));
                                            map.put("OperationJob", element.attributeValue("OperationJob"));
                                            map.put("CustomArea", element.attributeValue("CustomArea"));
                                            map.put("SECRET", element.attributeValue("SECRET"));
                                            map.put("Term", element.attributeValue("Term"));
                                            map.put("ProfessionalCode", element.attributeValue("ProfessionalCode"));
                                            map.put("GONGXUJIANHAO", element.attributeValue("GONGXUJIANHAO"));
                                            map.put("department", element.attributeValue("department"));
                                        }
                                        String CINDEX = element.attributeValue("CINDEX");
                                        String MINDEX = element.attributeValue("MINDEX");
                                        String PINDEX = element.attributeValue("PINDEX");
                                        String PPLANTYPE = element.attributeValue("PPLANTYPE");
                                        String ZFFLAG = element.attributeValue("ZFFLAG");
                                        String DEPT = element.attributeValue("DEPT");
                                        String PHASE_CODE = element.attributeValue("PHASE_CODE");
                                        String KEYCOMPONENT = element.attributeValue("KEYCOMPONENT");
                                        String BATCH = element.attributeValue("PCNO");
                                        String BIAOSHI = element.attributeValue("BIAOSHI");
                                        String isCLDE = XmlUtility.isCLDE(element);
                                        map.put("CINDEX", CINDEX);
                                        map.put("MINDEX", MINDEX);
                                        map.put("PINDEX", PINDEX);
                                        map.put("PPLANTYPE", PPLANTYPE);
                                        map.put("ZFFLAG", ZFFLAG);
                                        map.put("DEPT", DEPT);
                                        map.put("PHASE_CODE", PHASE_CODE);
                                        map.put("KEYCOMPONENT", KEYCOMPONENT);
                                        map.put("BATCH", BATCH);
                                        map.put("BIAOSHI", BIAOSHI);
                                        map.put("isCLDE", isCLDE);
                                        String bzyjNum = element.attributeValue("bzyjNum");
                                        String bzyjName = element.attributeValue("bzyjName");
                                        String yyfl = element.attributeValue("yyfl");
                                        map.put("bzyjNum", bzyjNum);
                                        map.put("bzyjName", bzyjName);
                                        map.put("yyfl", yyfl);


                                        logger.debug("map=======" + map);
                                        progressBar.setHeaderMessage("开始上载！");
                                        HashMap<String, String> returnMap = TechnicsIntf.uploadTechnics(bytes, map, version, remark);

//										progressBar.setHeaderMessage("开始上载PDF工艺文件！");
//										File file = new File(pdfFile);
//										TechHelper.uploadAttachForDocument(technicsNumber, "PDFPreview.pdf", file);
//										progressBar.setHeaderMessage("结束上载PDF工艺文件！");
//
//										progressBar.setHeaderMessage("结束上载！");
                                        logger.debug("returnMap= " + returnMap);
                                        String success = returnMap.get("success");
                                        if ("success".equals(success)) {
                                            String newVersion = returnMap.get("version");
                                            String newLifecycle = returnMap.get("lifecycle");
                                            XmlUtility.setAttributeValue(element, "version", newVersion);
                                            XmlUtility.setAttributeValue(element, "lifecycle", newLifecycle);
                                            saveProcess(element);
											/*NewTechnicsPart.this.technicsTreePanel.expandAllNode(node);

											NewTechnicsPart.this.technicsTreePanel.getTree().setSelectionPath(new TreePath(node.getPath()));
											XWTreeNode rootNode = (XWTreeNode) xwPartTreePanel.getTree().getModel().getRoot();
											// updateTreeTechnics(rootNode, partOid, technicsName, technicsCategory, newVersion);
											rootNode.removeAllChildren();
											NewTechnicsPart.this.xwPartTreePanel.expandAllNode(rootNode);
											for (int i = 0; i < NewTechnicsPart.this.xwPartTreePanel.getTree().getRowCount(); i++) {
												NewTechnicsPart.this.xwPartTreePanel.getTree().expandRow(i);
												NewTechnicsPart.this.xwPartTreePanel.getTree().updateUI();
											}
											NewTechnicsPart.this.xwPartTreePanel.getTree().updateUI();

											technicsMasterJPanel.setUIValues(element);*/
                                            try {
                                                progressBar.setHeaderMessage("开始生成工艺文件PDF格式！");
                                                path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber, technicsName, technicsCategory);
                                                File floder = new File(path, "fbtemp");
                                                pages = PDFPreviewFactory.preview(path, false, null);
                                                pdfFile = path + File.separator + "PDFPreview.pdf";
                                                FileUtil.deleteFile(floder);
                                                progressBar.setHeaderMessage("结束生成工艺文件PDF格式！");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
//----------------------------

//--------------------------------------------------------再次上载工艺文件(begin)--------------------------------------
                                            //将页数pages写入XML
                                            XmlUtility.setAttributeValue(element, "pageSize", String.valueOf(pages));
                                            String rootpath = path + File.separator + technicsNumber + ".xml";
                                            File rootfile = new File(rootpath);
                                            XmlUtility.saveDocument(element.getDocument(), rootfile);
                                            //再次打包工艺文件夹并上传
                                            progressBar.setHeaderMessage("再次获取上载流！");
                                            if ("rework".equals(technicsCategory)) {
                                                bytes = FilesUtil.getReworkTechnicsByte(technicsNumber, technicsName);
                                                technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
                                            } else if ("temp".equals(technicsCategory)) {
                                                bytes = FilesUtil.getTempTechnicsByte(technicsNumber, technicsName);
                                                technicsDirectory = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
                                            } else {
                                                bytes = FilesUtil.getTechnicsByte(technicsNumber, progressBar);
                                                technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
                                            }
                                            progressBar.setHeaderMessage("再次获取上载流结束！");
                                            map.put("page", String.valueOf(pages));
                                            progressBar.setHeaderMessage("再次上载开始！");
                                            returnMap = TechnicsIntf.reUploadTechnics(bytes, map);
                                            progressBar.setHeaderMessage("再次上载结束！");

//--------------------------------------------------------再次上载工艺文件(end)--------------------------------------


                                            progressBar.setHeaderMessage("开始上载PDF工艺文件！");
                                            File file = new File(pdfFile);
                                            TechHelper.uploadAttachForDocument(technicsNumber, "PDFPreview.pdf", file);
                                            progressBar.setHeaderMessage("结束上载PDF工艺文件！");
                                            progressBar.setHeaderMessage("结束上载！");


                                            if (editTechnics != null) {
                                                for (UploadTechnics temp1 : editTechnics) {
                                                    if (temp1.getTechnicsNumber().equals(technicsNumber)) {
                                                        NewTechnicsPart.editTechnics.remove(temp1);
                                                        break;
                                                    }
                                                }
                                            }

                                            progressBar.setHeaderMessage("工艺上载成功！");
                                        } else {
                                            XmlUtility.setAttributeValue(element, "version", version);
                                            saveProcess(element);
                                            String errorMessage = returnMap.get("errorMessage");
                                            if ((errorMessage != null) && (!errorMessage.equals(""))) {
                                                progressBar.setHeaderMessage(errorMessage);
                                            } else {
                                                progressBar.setHeaderMessage("工艺上载过程中出现错误！");
                                            }
                                        }
                                    } catch (Exception e1) {
                                        progressBar.setHeaderMessage("工艺上载过程中出现错误！");
                                        e1.printStackTrace();
                                    }

                                    NewTechnicsPart.editTechnics.remove(temp);
                                    break;
                                }
                            }
                        }

                        Map<String, Object> inputMap = TechnicsUtil.generateSubmitMap(element, null);
                        String technicsCategory = element.attributeValue("technicsCategory");
                        String technicsName = element.attributeValue("technicsName");

                        String technicsType = element.attributeValue("technicsType");
                        String partOid = element.attributeValue("partOid");
                        String processTaskItemOid = element.attributeValue("processTaskItemOid");
                        inputMap.put("isReport", "false");//false标识一般类工艺
                        inputMap.put("technicsType", technicsType);
                        inputMap.put("technicsNumber", technicsNumber);
                        inputMap.put("partOid", partOid);
                        progressBar.setHeaderMessage("开始获取签审流！");
                        try {
                            byte[] bytes;
                            String technicsDirectory = "";
                            if ("rework".equals(technicsCategory)) {
                                bytes = FilesUtil.getReworkTechnicsByte(technicsNumber, technicsName);
                                technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
                                inputMap.put("taskOid", technicsTaskId);
                            } else if ("temp".equals(technicsCategory)) {
                                bytes = FilesUtil.getTempTechnicsByte(technicsNumber, technicsName);
                                technicsDirectory = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
                                inputMap.put("taskOid", technicsTaskId);
                            } else {
                                bytes = FilesUtil.getTechnicsByte(technicsNumber, progressBar);
                                technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
                            }
                            progressBar.setHeaderMessage("结束获取签审流！");
                            String technicsFolderName = technicsDirectory.substring(technicsDirectory.lastIndexOf("\\") + 1, technicsDirectory.length());
                            inputMap.put("technicsName", technicsFolderName);
                            inputMap.put("flag", submitFlag);
                            //inputMap.put("taskOid", processTaskItemOid);
                            inputMap.put("taskOid", BianzhiOid);
                            inputMap.put("isSanJiGengGai", isSanJiGengGai);
                            inputMap.put("startType", EditorConfig.startType);
                            progressBar.setHeaderMessage("开始提交签审！");
                            HashMap returnMap = TechnicsIntf.submitSigned(inputMap, bytes);
                            progressBar.setHeaderMessage("结束提交签审！");
                            String success = (String) returnMap.get("success");
                            if (success.equals("success")) {
                                //String newVersion = (String) returnMap.get("version");
                                //String lifecycle = (String) returnMap.get("lifecycle");
                                //XmlUtility.setAttributeValue(element, "version", newVersion);
                                //XmlUtility.setAttributeValue(element, "lifecycle", lifecycle);
								/*saveProcess(element);
								XWTreeNode rootNode = (XWTreeNode) xwPartTreePanel.getTree().getModel().getRoot();
								String oid = element.attributeValue("partOid");
								updateTreeTechnics(rootNode, oid, technicsName, technicsCategory, newVersion);
								NewTechnicsPart.this.xwPartTreePanel.expandAllNode(rootNode);
								NewTechnicsPart.this.technicsTreePanel.expandAllNode(node);
								NewTechnicsPart.this.technicsTreePanel.getTree().setSelectionPath(new TreePath(node.getPath()));
								NewTechnicsPart.this.xwPartTreePanel.getTree().updateUI();
								technicsMasterJPanel.setUIValues(element);
//								Vector<UploadTechnics> editTechnics = NewTechnicsPart.this.editTechnics;
//								if (editTechnics != null) {
//									for (UploadTechnics temp : editTechnics) {
//										if (temp.getTechnicsNumber().equals(technicsNumber)) {
//											editTechnics.remove(temp);
//											break;
//										}
//									}
//								}*/
                                JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺规程提交签审成功,在个人主页我的任务列表中可以收到任务！", "提示", 1);
                                progressBar.setHeaderMessage("工艺规程提交签审成功！");
                            } else {
                                String errorMessage = (String) returnMap.get("errorMessage");
                                progressBar.setHeaderMessage(errorMessage);
                                if ((errorMessage != null) && (!errorMessage.equals(""))) {
                                    JOptionPane.showMessageDialog(NewTechnicsPart.this, errorMessage, "提示", 1);
                                    progressBar.setHeaderMessage(errorMessage);
                                } else {
                                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺规程提交签审出现错误！", "提示", 1);
                                    progressBar.setHeaderMessage("工艺规程提交签审出现错误！");
                                }
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺规程提交签审出现错误！", "提示", 1);
                            progressBar.setHeaderMessage("工艺规程提交签审出现错误！");
                        }
                    }
                } else {
                    XWTreeNode n = NewTechnicsPart.this.xwPartTreePanel.getSelectedTreeNode();
                    if (n != null) {
                        XWTreeObject xo = n.getObject();
                        if (xo instanceof ReportTechnicsTreeObject) {
                            reportCommitForSignedThread(n, submitFlag, progressBar);
                        }
                    }
                }
                progressBar.finish();
                progressBar.setVisible(false);
            }
        };
        thread.start();
        progressBar.setVisible(true);
    }

    public static boolean checkBeforeSubmit(Element technicsElement,Component parentComponent){
        String pplanNumber = technicsElement.attributeValue("pplanNumber");
        String checkMaterialResult = TechnicsUtil.checkMaterial(technicsElement);
        if(checkMaterialResult != null && !checkMaterialResult.isEmpty()){
            JOptionPane.showMessageDialog(parentComponent, checkMaterialResult, "提示", JOptionPane.OK_OPTION);
            return false;
        }
        if (technicsElement.attributeValue("ZFFLAG") != null && technicsElement.attributeValue("ZFFLAG").equals("F")) {
            if(!"17".equals(technicsElement.attributeValue("PPLANID"))) {
                String fzTechnicsNumber = technicsElement.attributeValue("technicsNumber");
                String fzVersion = technicsElement.attributeValue("version");
                String version = fzVersion.substring(0, fzVersion.indexOf("."));
                String picihao = technicsElement.attributeValue("PCNO");
                boolean hasLink = false;
                try {
                    hasLink = TechnicsIntf.hasMainMakeTechnics(fzTechnicsNumber, version, picihao);
                    if (!hasLink) {
                        JOptionPane.showMessageDialog(parentComponent, "辅制工艺" + pplanNumber + "未关联主制工艺，不能提交签审！", "提示", JOptionPane.OK_OPTION);
                        return false;
                    }
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        //如果是主工艺
        if (technicsElement.attributeValue("ZFFLAG") != null && technicsElement.attributeValue("ZFFLAG").equals("Z")) {
            Element fzgy = technicsElement.element("FZGY");
            if(fzgy != null){
                List<Element> fzgyList = fzgy.elements("FZTECHNICS");
                if(fzgyList != null ){
                    try {
                        //校验该工艺是否下发了辅制工艺任务
                        int fzProcessTaskCount = TechnicsIntf.checkHasFzProcessTask(pplanNumber, ProcessConstants.TASK_TYPE_FZGYRW);
                        if (fzProcessTaskCount < fzgyList.size()) {
                            JOptionPane.showMessageDialog(parentComponent, "工艺" + pplanNumber + "已定义了辅制工艺信息，但未提交辅制工艺编制任务，请下发后再提交\r\n定义的辅制工艺数量 : " + fzgyList.size() + " ,已创建的辅制工艺任务数量 : " + fzProcessTaskCount, "提示", JOptionPane.OK_OPTION);
                            return false;
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

    /**
     * 判断修订的工艺文件是三级工艺签审，还是更改单修订的
     *
     * @param node
     * @return
     */
    private boolean isSanJiGengGai(XWTreeNode node) {
        XWTreeObject xo = node.getObject();
        System.out.println(xo);
        if ((xo instanceof XWTechnicsTreeObject)) {
            Element element = xo.getTreeCellData();
            String technicsNumber = element.attributeValue("technicsNumber");
            String version = element.attributeValue("technicsNumber");
            String ecnNo = (String) IntfUtil.getPeRemoteMethodInvoke("getChangeNoByTechnics", new Class[] { String.class }, new Object[] { technicsNumber });
            if ("".equals(ecnNo) && !version.startsWith("space")) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static boolean isSanJiGengGai(Element element) {
        String technicsNumber = element.attributeValue("technicsNumber");
        String version = element.attributeValue("technicsNumber");
        String ecnNo = (String) IntfUtil.getPeRemoteMethodInvoke("getChangeNoByTechnics", new Class[] { String.class }, new Object[] { technicsNumber });
        if ("".equals(ecnNo) && !version.startsWith("space")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkCanZhuang4Technics(XWTreeObject partObj, Element techEle, Component parentComponent) {
        Map<String, String> partMap = new HashMap<String, String>();
        if(partObj instanceof XWPartTreeObject) {
            Element partElenet = partObj.getTreeCellData();
            List<Element> childProducts = BomXMLUtil.getChildProducts(partElenet);
            for(Element element : childProducts) {
                if("自制件".equals(element.attributeValue("CTYPE"))) {
                    partMap.put(element.attributeValue("partNumber"), element.attributeValue("gysl"));
                }
            }
                /*
                try {
                    List<String> strings = TechnicsUtil.checkCzj(partElenet, techEle);
                    if(strings != null && strings.size() > 0){
                        JOptionPane.showMessageDialog(null, strings + "存在多装或者漏装，请参装完成后再提交签审！");
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            try {
                Map<String, PDFBuilder.CzjPart> czjPartMap = PDFBuilder.getAllCzjMap(techEle);
                for(Map.Entry<String, String> entry : partMap.entrySet()) {
                    String partNumber = entry.getKey();
                    if(czjPartMap.containsKey(partNumber)) {
                        double czjCount = czjPartMap.get(partNumber).getCount();
                        double partGysl = Double.valueOf(entry.getValue());
                        if(czjCount > partGysl) {
                            JOptionPane.showMessageDialog(parentComponent, partNumber + "参装数量不合理，请重新进行参装！");
                            return false;
                        }
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean check4Gongyimulu(Element element,Component parentComponent) {
        List<Element> dataList = element.elements("dataItemValue");
        Map<String, String> technicsMap = new HashMap<String, String>();
        for (Element data : dataList) {
            String technicsNumber = data.attributeValue("fileNumber");
            String version = data.attributeValue("version");
            technicsMap.put(technicsNumber, version);
            String lifeCycle = data.attributeValue("lifecycle");
            if (!"已批准".equals(lifeCycle)) {
                JOptionPane.showMessageDialog(parentComponent, "该工艺文件目录中存在未受控工艺，不能提交签审!", "提示", 1);
                return false;
            }
        }
        try {
            String message = TechnicsIntf.checkTechnicsIsLatest(technicsMap);
            if (!"".equals(message)) {
                int result = JOptionPane.showConfirmDialog(parentComponent, message + "是否继续提交签审？", "提示", JOptionPane.YES_NO_OPTION);
                if (result != JOptionPane.YES_OPTION) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 提交报表类工艺文件签审流程
     *
     * @param node        PBOM树当前选择的节点
     * @param flag        3-三级签审；5-五级签审
     * @param progressBar 滚动条
     */
    private void reportCommitForSignedThread(XWTreeNode node, String flag, VaActionProgressBar progressBar) {
        String technicsCategory = null;
        String technicsName = null;
        String technicsType = null;
        String partOid = null;
        String processTaskItemOid = null;
        if (node != null) {
            XWTreeObject xo = node.getObject();
            if ((xo instanceof ReportTechnicsTreeObject)) {
                BianzhiOid = "";
//			    CommitForSignedDialog dialog = null;
//                try {
//                    dialog = new CommitForSignedDialog(NewTechnicsPart.this);
//                } catch (WTException e3) {
//                    e3.printStackTrace();
//                }
//                String comment = dialog.showDialog();
//                if (comment!=null) {
//                      BianzhiOid=dialog.content;
//                      BianzhiOid = BianzhiOid.split("ext.casc.process.ProcessTaskItem:")[1];
//                }else{
//                    JOptionPane.showMessageDialog(NewTechnicsPart.this,"必须要选择相关的工艺任务才能提交签审！", "提示", 1);
//                    progressBar.finish();
//                    progressBar.setVisible(false);
//                    return;
//                }
//                  if (!CommitForSignedDialog.flag) {
//                        return;
//                    }
                Element element = xo.getTreeCellData();
                String technicsNumber = element.attributeValue("technicsNumber");
                //如果该工艺文件新建或修改后还未上载，则需要先上载
                technicsCategory = element.attributeValue("technicsCategory");
                technicsName = element.attributeValue("technicsName");
                String pdfFile = null;
                String path = null;
                try {
                    path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber, technicsName, technicsCategory);
                } catch (Exception e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
                pdfFile = path + File.separator + "PDFPreview.pdf";
                File floder1 = new File(pdfFile);
                if (floder1.exists()) {
                    boolean flag1 = floder1.delete();
                    if (!flag1) {
                        JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺名称为:" + technicsName + "的PDF文件正在使用，请先关闭再上载！");
                        progressBar.finish();
                        progressBar.setVisible(false);
                        return;
                    }
                }


                Vector<UploadTechnics> editTechnics = NewTechnicsPart.this.editTechnics;
                Vector<UploadTechnics> tempeditTechnics = new Vector<UploadTechnics>();
                tempeditTechnics.addAll(editTechnics);
                if (editTechnics != null) {
                    for (UploadTechnics temp : tempeditTechnics) {
                        if (temp.getTechnicsNumber().equals(technicsNumber)) {
                            //上载报表类工艺
                            uploadReportTechnics1(progressBar, element, "");
                        }
                    }
                }

                Map<String, Object> inputMap = TechnicsUtil.generateSubmitMap(element, null);


                technicsType = element.attributeValue("technicsType");
                partOid = element.attributeValue("partOid");
                processTaskItemOid = element.attributeValue("processTaskItemOid");
                inputMap.put("isReport", "true");//true标识报表类工艺
                inputMap.put("technicsType", technicsType);
                inputMap.put("technicsNumber", technicsNumber);
                inputMap.put("technicsCategory", "normal");
                inputMap.put("partOid", partOid);
                try {
                    progressBar.setHeaderMessage("开始获取签审流！");
                    byte[] bytes = FilesUtil.getTechnicsByte(technicsNumber, progressBar);
                    String technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
                    progressBar.setHeaderMessage("结束获取签审流！");
                    String technicsFolderName = technicsDirectory.substring(technicsDirectory.lastIndexOf("\\") + 1, technicsDirectory.length());
                    inputMap.put("technicsName", technicsFolderName);
                    inputMap.put("flag", submitFlag);
//					inputMap.put("taskOid", processTaskItemOid);
                    inputMap.put("taskOid", BianzhiOid);
                    progressBar.setHeaderMessage("开始提交签审！");
                    HashMap returnMap = TechnicsIntf.submitSigned(inputMap, bytes);
                    progressBar.setHeaderMessage("结束提交签审！");
                    String success = (String) returnMap.get("success");
                    if (success.equals("success")) {
                        String newVersion = (String) returnMap.get("version");
                        String lifecycle = (String) returnMap.get("lifecycle");
                        XmlUtility.setAttributeValue(element, "version", newVersion);
                        XmlUtility.setAttributeValue(element, "lifecycle", lifecycle);
                        saveProcess(element);
                        XWTreeNode rootNode = (XWTreeNode) xwPartTreePanel.getTree().getModel().getRoot();
                        String oid = element.attributeValue("partOid");
                        updateTreeTechnics(rootNode, oid, technicsName, technicsCategory, newVersion);
                        NewTechnicsPart.this.xwPartTreePanel.expandAllNode(rootNode);
                        NewTechnicsPart.this.xwPartTreePanel.getTree().updateUI();
                        try {
                            progressBar.setHeaderMessage("开始生成工艺文件PDF格式！");


                            File floder = new File(path, "fbtemp");
                            String path1 = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
                            pages = PDFPreviewFactory.previewForReport(path1, false);
                            pdfFile = path1 + File.separator + "PDFPreview.pdf";
                            FileUtil.deleteFile(floder);
                            progressBar.setHeaderMessage("结束生成工艺文件PDF格式！");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            progressBar.setHeaderMessage("开始上载PDF工艺文件！");
                            File file = new File(pdfFile);
                            TechHelper.uploadAttachForDocument(technicsNumber, "PDFPreview.pdf", file);
                            progressBar.setHeaderMessage("结束上载PDF工艺文件！");
                        } catch (Exception e) {
                            progressBar.setHeaderMessage("上载PDF工艺文件过程中出现错误！");
                            e.printStackTrace();
                        }

                        XmlUtility.setAttributeValue(element, "pageSize", String.valueOf(pages));
                        if (editTechnics != null) {
                            for (UploadTechnics temp : tempeditTechnics) {
                                if (temp.getTechnicsNumber().equals(technicsNumber)) {
                                    bytes = FilesUtil.getTechnicsByte(technicsNumber, progressBar);
                                    //再次上载报表类工艺
                                    HashMap map = new HashMap();
                                    map.put("page", pages);
                                    map.put("oid", element.attributeValue("partOid"));
                                    map.put("technicsNumber", technicsNumber);
                                    map.put("technicsName", element.attributeValue("technicsName"));
                                    map.put("technicsType", element.attributeValue("technicsType"));
//		                            map.put("bytes",bytes);
                                    // technicsType
                                    map.put("pplanNumber", element.attributeValue("pplanNumber"));
                                    progressBar.setHeaderMessage("再次上载开始！");
                                    TechnicsIntf.reUploadTechnics(bytes, map);
//		                            uploadReportTechnics(progressBar, element, "");
                                }
                            }
                        }
                        Vector<UploadTechnics> editTechnics1 = NewTechnicsPart.this.editTechnics;
                        if (editTechnics1 != null) {
                            for (UploadTechnics temp : editTechnics1) {
                                if (temp.getTechnicsNumber().equals(technicsNumber)) {
                                    NewTechnicsPart.editTechnics.remove(temp);
                                    break;
                                }
                            }
                        }


                        JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺规程提交签审成功,在个人主页我的任务列表中可以收到任务！", "提示", 1);
                        progressBar.setHeaderMessage("工艺规程提交签审成功！");
                    } else {
                        String errorMessage = (String) returnMap.get("errorMessage");
                        progressBar.setHeaderMessage(errorMessage);
                        if ((errorMessage != null) && (!errorMessage.equals(""))) {
                            JOptionPane.showMessageDialog(NewTechnicsPart.this, errorMessage, "提示", 1);
                            progressBar.setHeaderMessage(errorMessage);
                        } else {
                            JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺规程提交签审出现错误！", "提示", 1);
                            progressBar.setHeaderMessage("工艺规程提交签审出现错误！");
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺规程提交签审出现错误！", "提示", 1);
                    progressBar.setHeaderMessage("工艺规程提交签审出现错误！");
                }
            }
        }
    }

    public void commitForCmatSignedThread() {
//		if(!isSubmited()) {
//			JOptionPane.showMessageDialog(NewTechnicsPart.this,"该工艺文件已经提交签审，不能再提交！", "提示", 1);
//			return;
//		}
        final VaActionProgressBar progressBar = new VaActionProgressBar(NewTechnicsPart.this, "提交材料消耗工艺定额明细表签审", "正在提交材料消耗工艺定额明细表签审,请等待...", "材料消耗工艺定额明细表签审中");
        Thread thread = new Thread() {
            public void run() {
                XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
                if (node != null) {
                    XWTreeObject xo = node.getObject();
                    if ((xo instanceof XWTechnicsTreeObject)) {
                        Element element = xo.getTreeCellData();

//						if (!TechnicsUtil.checkTechnics(NewTechnicsPart.this, element)) {
//							progressBar.finish();
//							progressBar.setVisible(false);
//							return;
//						}

                        String technicsNumber = element.attributeValue("technicsNumber");

                        //如果该工艺文件新建或修改后还未上载，则需要先上载
                        Vector<UploadTechnics> editTechnics = NewTechnicsPart.this.editTechnics;
                        if (editTechnics != null) {
                            for (UploadTechnics temp : editTechnics) {
                                if (temp.getTechnicsNumber().equals(technicsNumber)) {
                                    RemarkJDialog remarkJDialog = new RemarkJDialog("工艺上载", NewTechnicsPart.this);
                                    String remark = remarkJDialog.showDialog();
                                    if (remark == null) {
                                        progressBar.finish();
                                        progressBar.setVisible(false);
                                        return;
                                    }

                                    String technicsCategory = element.attributeValue("technicsCategory");
                                    String technicsName = element.attributeValue("technicsName");
                                    String version = element.attributeValue("version");
                                    String technicsType = element.attributeValue("technicsType"); // 工艺类型
                                    if ((version == null) || (version.equals(""))) {
                                        version = "1.0";
                                    }

                                    String pdfFile = null;
                                    try {
                                        progressBar.setHeaderMessage("开始生成工艺文件PDF格式！");
                                        String path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber, technicsName, technicsCategory);
                                        File floder = new File(path, "fbtemp");
                                        PDFPreviewFactory.preview(path, false, null);
                                        pdfFile = path + File.separator + "PDFPreview.pdf";
                                        FileUtil.deleteFile(floder);
                                        progressBar.setHeaderMessage("结束生成工艺文件PDF格式！");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        progressBar.setHeaderMessage("开始获取上载流！");
                                        byte[] bytes;
                                        String technicsDirectory = "";
                                        if ("rework".equals(technicsCategory)) {
                                            bytes = FilesUtil.getReworkTechnicsByte(technicsNumber, technicsName);
                                            technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
                                        } else if ("temp".equals(technicsCategory)) {
                                            bytes = FilesUtil.getTempTechnicsByte(technicsNumber, technicsName);
                                            technicsDirectory = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
                                        } else {
                                            bytes = FilesUtil.getTechnicsByte(technicsNumber, progressBar);
                                            technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
                                        }
                                        progressBar.setHeaderMessage("结束获取上载流！");
                                        String technicsFolderName = technicsDirectory.substring(technicsDirectory.lastIndexOf("\\") + 1, technicsDirectory.length());
                                        String partNumber = element.attributeValue("partNumber");
                                        String partOid = element.attributeValue("partOid");
                                        String partName = element.attributeValue("partName");
                                        String partType = element.attributeValue("partType");
                                        String pplanNumber = element.attributeValue("pplanNumber");
                                        String BATCH = element.attributeValue("PCNO");
                                        String BIAOSHI = element.attributeValue("BIAOSHI");
                                        String isCLDE = XmlUtility.isCLDE(element);
                                        HashMap<String, String> map = new HashMap<String, String>();

                                        map.put("isCLDE", isCLDE);
                                        map.put("oid", partOid);
                                        map.put("partNumber", partNumber);
                                        map.put("partName", partName);
                                        map.put("partType", partType);
                                        map.put("technicsType", technicsType);
                                        map.put("technicsNumber", technicsNumber);
                                        map.put("technicsName", technicsName);
                                        map.put("pplanNumber", pplanNumber);
                                        map.put("BATCH", BATCH);
                                        map.put("BIAOSHI", BIAOSHI);

                                        String bzyjNum = element.attributeValue("bzyjNum");
                                        String bzyjName = element.attributeValue("bzyjName");
                                        String yyfl = element.attributeValue("yyfl");
                                        map.put("bzyjNum", bzyjNum);
                                        map.put("bzyjName", bzyjName);
                                        map.put("yyfl", yyfl);

                                        logger.debug("map=======" + map);
                                        progressBar.setHeaderMessage("开始上载！");
                                        HashMap<String, String> returnMap = TechnicsIntf.uploadTechnics(bytes, map, version, remark);

                                        progressBar.setHeaderMessage("开始上载PDF工艺文件！");
                                        File file = new File(pdfFile);
                                        TechHelper.uploadAttachForDocument(technicsNumber, "PDFPreview.pdf", file);
                                        progressBar.setHeaderMessage("结束上载PDF工艺文件！");

                                        progressBar.setHeaderMessage("结束上载！");
                                        logger.debug("returnMap= " + returnMap);
                                        String success = returnMap.get("success");
                                        if ("success".equals(success)) {
                                            String newVersion = returnMap.get("version");
                                            String newLifecycle = returnMap.get("lifecycle");
                                            XmlUtility.setAttributeValue(element, "version", newVersion);
                                            XmlUtility.setAttributeValue(element, "lifecycle", newLifecycle);
                                            saveProcess(element);
                                            NewTechnicsPart.this.technicsTreePanel.expandAllNode(node);

                                            NewTechnicsPart.this.technicsTreePanel.getTree().setSelectionPath(new TreePath(node.getPath()));
                                            XWTreeNode rootNode = (XWTreeNode) xwPartTreePanel.getTree().getModel().getRoot();
                                            // updateTreeTechnics(rootNode, partOid, technicsName, technicsCategory, newVersion);
                                            rootNode.removeAllChildren();
                                            NewTechnicsPart.this.xwPartTreePanel.expandAllNode(rootNode);
                                            for (int i = 0; i < NewTechnicsPart.this.xwPartTreePanel.getTree().getRowCount(); i++) {
                                                NewTechnicsPart.this.xwPartTreePanel.getTree().expandRow(i);
                                                NewTechnicsPart.this.xwPartTreePanel.getTree().updateUI();
                                            }
                                            NewTechnicsPart.this.xwPartTreePanel.getTree().updateUI();

                                            technicsMasterJPanel.setUIValues(element);

                                            progressBar.setHeaderMessage("工艺上载成功！");
                                        } else {
                                            XmlUtility.setAttributeValue(element, "version", version);
                                            saveProcess(element);
                                            String errorMessage = returnMap.get("errorMessage");
                                            if ((errorMessage != null) && (!errorMessage.equals(""))) {
                                                progressBar.setHeaderMessage(errorMessage);
                                            } else {
                                                progressBar.setHeaderMessage("工艺上载过程中出现错误！");
                                            }
                                        }
                                    } catch (Exception e1) {
                                        progressBar.setHeaderMessage("工艺上载过程中出现错误！");
                                        e1.printStackTrace();
                                    }

                                    NewTechnicsPart.editTechnics.remove(temp);
                                    break;
                                }
                            }
                        }

                        progressBar.setHeaderMessage("开始获取签审流程！");

                        String partUseCount = "1";
                        String cindex = "";
                        XWTreeNode treeNode = NewTechnicsPart.this.xwPartTreePanel.getSelectedTreeNode();
                        if (treeNode != null) {
                            XWTreeNode partNode = treeNode.getP();
                            if (partNode != null) {
                                XWPartTreeObject partObject = (XWPartTreeObject) partNode.getObject();
                                Element partEle = partObject.getTreeCellData();
                                partUseCount = partEle.attributeValue("useCount");
                                cindex = partEle.attributeValue("CINDEX");
                            }
                        }

                        List<CMatBean> list = TechnicsUtil.generateSubmitList(element, partUseCount, cindex);
                        progressBar.setHeaderMessage("结束获取签审流！");
                        try {
                            progressBar.setHeaderMessage("开始提交材料消耗工艺定额明细表签审！");
                            HashMap<String, String> returnMap = TechnicsIntf.submitCMatSigned(list, technicsNumber);
                            progressBar.setHeaderMessage("结束提交材料消耗工艺定额明细表签审！");
                            String success = (String) returnMap.get("success");
                            if (success.equals("success")) {
                                JOptionPane.showMessageDialog(NewTechnicsPart.this, "提交签审成功,在个人主页我的任务列表中可以收到任务！", "提示", 1);
                                progressBar.setHeaderMessage("提交材料消耗工艺定额明细表签审成功！");
                            } else {
                                String errorMessage = (String) returnMap.get("errorMessage");
                                progressBar.setHeaderMessage(errorMessage);
                                if ((errorMessage != null) && (!errorMessage.equals(""))) {
                                    progressBar.setHeaderMessage(errorMessage);
                                } else {
                                    progressBar.setHeaderMessage("提交材料消耗工艺定额明细表签审出现错误！");
                                }
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            progressBar.setHeaderMessage("提交材料消耗工艺定额明细表签审出现错误！");
                        }
                    }
                }
                progressBar.finish();
                progressBar.setVisible(false);
            }
        };
        thread.start();
        progressBar.setVisible(true);
    }

    public void commitForBatchCmatSignedThread(final Map<String, List<CMatBean>> dataMap, final Map<String, String> taskMap, final String workItemOid, final String partNumber) {
        int returnValue = 0;
        if ((editTechnics != null) && (editTechnics.size() > 0)) {
            CloseJDialog cj = new CloseJDialog(NewTechnicsPart.this);
            returnValue = cj.showDialog();
        }
        if (returnValue == 0) {
            final VaActionProgressBar progressBar = new VaActionProgressBar(NewTechnicsPart.this, "提交材料消耗工艺定额明细表签审", "正在提交材料消耗工艺定额明细表签审,请等待...", "材料消耗工艺定额明细表签审中");
            Thread thread = new Thread() {
                public void run() {
                    try {
                        progressBar.setHeaderMessage("开始提交材料消耗工艺定额明细表签审！");
                        HashMap<String, String> returnMap = TechnicsIntf.submitBatchCMatSigned(dataMap, taskMap, workItemOid,partNumber);
                        progressBar.setHeaderMessage("结束提交材料消耗工艺定额明细表签审！");
                        String success = (String) returnMap.get("success");
                        if (success.equals("success")) {
                            JOptionPane.showMessageDialog(NewTechnicsPart.this, "提交签审成功,在个人主页我的任务列表中可以收到任务！", "提示", 1);
                            progressBar.setHeaderMessage("提交材料消耗工艺定额明细表签审成功！");

                            xwPartTreePanel.getTree().updateUI();
                        } else {
                            String errorMessage = (String) returnMap.get("errorMessage");
                            progressBar.setHeaderMessage(errorMessage);
                            if ((errorMessage != null) && (!errorMessage.equals(""))) {
                                progressBar.setHeaderMessage(errorMessage);
                            } else {
                                progressBar.setHeaderMessage("提交材料消耗工艺定额明细表签审出现错误！");
                            }
                        }
                    } catch (Exception e1) {
                        progressBar.setHeaderMessage("提交材料消耗工艺定额明细表签审出现错误！");
                        e1.printStackTrace();
                    }

                    progressBar.finish();
                    progressBar.setVisible(false);
                }
            };

            thread.start();
            progressBar.setVisible(true);
        }
    }

    /**
     * 提交签审
     */
    public void commitForSigned() {
        XWTreeNode node = this.technicsTreePanel.getSelectedTreeNode();
        if (node != null) {
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWTechnicsTreeObject)) {
                Element element = xo.getTreeCellData();
                // List<Element> subPartOids = PbomUtil.getSubPartOids(
                // xwPartTreePanel.get Tree(),
                // element.attributeValue("partOid"));

                if (!"".equals(TechnicsUtil.checkTechnics(this, element))) {
                    return;
                }

                Map<String, Object> inputMap = TechnicsUtil.generateSubmitMap(element, null);
                String technicsCategory = element.attributeValue("technicsCategory");
                String technicsName = element.attributeValue("technicsName");
                String technicsNumber = element.attributeValue("technicsNumber");
                String technicsType = element.attributeValue("technicsType");
                inputMap.put("technicsType", technicsType);
                inputMap.put("technicsNumber", technicsNumber);
                try {
                    byte[] bytes;
                    String technicsDirectory = "";
                    if ("rework".equals(technicsCategory)) {
                        bytes = FilesUtil.getReworkTechnicsByte(technicsNumber, technicsName);
                        technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
                        inputMap.put("taskOid", technicsTaskId);
                    } else if ("temp".equals(technicsCategory)) {
                        bytes = FilesUtil.getTempTechnicsByte(technicsNumber, technicsName);
                        technicsDirectory = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
                        inputMap.put("taskOid", technicsTaskId);
                    } else {
                        bytes = FilesUtil.getTechnicsByte(technicsNumber);
                        technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
                    }
                    String technicsFolderName = technicsDirectory.substring(technicsDirectory.lastIndexOf("\\") + 1, technicsDirectory.length());
                    inputMap.put("technicsName", technicsFolderName);

                    HashMap returnMap = TechnicsIntf.submitSigned(inputMap, bytes);
                    String success = (String) returnMap.get("success");
                    if (success.equals("success")) {
                        String newVersion = (String) returnMap.get("version");
                        String lifecycle = (String) returnMap.get("lifecycle");
                        XmlUtility.setAttributeValue(element, "version", newVersion);
                        XmlUtility.setAttributeValue(element, "lifecycle", lifecycle);
                        saveProcess(element);
                        XWTreeNode rootNode = (XWTreeNode) xwPartTreePanel.getTree().getModel().getRoot();
                        String oid = element.attributeValue("partOid");
                        updateTreeTechnics(rootNode, oid, technicsName, technicsCategory, newVersion);
                        this.xwPartTreePanel.expandAllNode(rootNode);
                        this.technicsTreePanel.expandAllNode(node);
                        this.technicsTreePanel.getTree().setSelectionPath(new TreePath(node.getPath()));
                        this.xwPartTreePanel.getTree().updateUI();
                        technicsMasterJPanel.setUIValues(element);
                        Vector<UploadTechnics> editTechnics = this.editTechnics;
                        if (editTechnics != null) {
                            for (UploadTechnics temp : editTechnics) {
                                if (temp.getTechnicsNumber().equals(technicsNumber)) {
                                    this.editTechnics.remove(temp);
                                    break;
                                }
                            }
                        }
                        JOptionPane.showMessageDialog(this, "工艺规程提交签审成功！", "提示", 1);
                    } else {
                        String errorMessage = (String) returnMap.get("errorMessage");
                        if ((errorMessage != null) && (!errorMessage.equals(""))) {
                            JOptionPane.showMessageDialog(this, errorMessage, "提示", 1);
                        } else {
                            JOptionPane.showMessageDialog(this, "工艺规程提交签审出现错误！", "提示", 1);
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(this, "工艺规程提交签审出现错误！", "提示", 1);
                }
            }
        }
    }

    // TODO

    /**
     * 完成工艺任务
     */
    public void completeTechnicsTask() {
        XWTreeNode node = this.technicsTreePanel.getSelectedTreeNode();
        if (node != null) {
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWTechnicsTreeObject)) {
                Element element = xo.getTreeCellData();
                // List<Element> subPartOids = PbomUtil.getSubPartOids(
                // xwPartTreePanel.getTree(),
                // element.attributeValue("partOid"));
                if (!"".equals(TechnicsUtil.checkTechnics(this, element))) {
                    return;
                }
                List<Object> list = TechnicsUtil.completeTechnicsTask(workItemOid, this);
                if (list != null && list.size() == 2 && Boolean.TRUE.equals(list.get(0))) {
                    Map<String, Object> inputMap = (Map<String, Object>) list.get(1);
                    inputMap = TechnicsUtil.generateSubmitMap(element, inputMap);
                    inputMap.put("workItemOid", workItemOid);
                    String technicsCategory = element.attributeValue("technicsCategory");
                    String technicsName = element.attributeValue("technicsName");
                    String technicsNumber = element.attributeValue("technicsNumber");
                    String technicsType = element.attributeValue("technicsType");
                    try {
                        byte[] bytes;
                        String technicsDirectory = "";
                        if ("rework".equals(technicsCategory)) {
                            bytes = FilesUtil.getReworkTechnicsByte(technicsNumber, technicsName);
                            technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
                            inputMap.put("taskOid", technicsTaskId);
                        } else if ("temp".equals(technicsCategory)) {
                            bytes = FilesUtil.getTempTechnicsByte(technicsNumber, technicsName);
                            technicsDirectory = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
                            inputMap.put("taskOid", technicsTaskId);
                        } else {
                            bytes = FilesUtil.getTechnicsByte(technicsNumber);
                            technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
                        }
                        String technicsFolderName = technicsDirectory.substring(technicsDirectory.lastIndexOf("\\") + 1, technicsDirectory.length());
                        inputMap.put("technicsName", technicsFolderName);
                        inputMap.put("technicsNumber", technicsNumber);
                        inputMap.put("technicsType", technicsType);
                        logger.debug("inputMap= " + inputMap);
                        HashMap returnMap = TechnicsIntf.completeWorkItem(inputMap, bytes);
                        String success = (String) returnMap.get("success");
                        if (success.equals("success")) {
                            String newVersion = (String) returnMap.get("version");
                            String lifecycle = (String) returnMap.get("lifecycle");
                            XmlUtility.setAttributeValue(element, "version", newVersion);
                            XmlUtility.setAttributeValue(element, "lifecycle", lifecycle);
                            saveProcess(element);
                            XWTreeNode rootNode = (XWTreeNode) xwPartTreePanel.getTree().getModel().getRoot();
                            String oid = element.attributeValue("partOid");
                            updateTreeTechnics(rootNode, oid, technicsName, technicsCategory, newVersion);
                            this.xwPartTreePanel.expandAllNode(rootNode);
                            this.technicsTreePanel.expandAllNode(node);
                            this.technicsTreePanel.getTree().setSelectionPath(new TreePath(node.getPath()));
                            this.xwPartTreePanel.getTree().updateUI();
                            technicsMasterJPanel.setUIValues(element);
                            Vector<UploadTechnics> editTechnics = this.editTechnics;
                            if (editTechnics != null) {
                                for (UploadTechnics temp : editTechnics) {
                                    if (temp.getTechnicsNumber().equals(technicsNumber)) {
                                        this.editTechnics.remove(temp);
                                        break;
                                    }
                                }
                            }
                            JOptionPane.showMessageDialog(this, "完成工艺任务成功！", "提示", 1);
                        } else {
                            String errorMessage = (String) returnMap.get("errorMessage");
                            if ((errorMessage != null) && (!errorMessage.equals(""))) {
                                JOptionPane.showMessageDialog(this, errorMessage, "提示", 1);
                            } else {
                                JOptionPane.showMessageDialog(this, "完成工艺任务出现错误！", "提示", 1);
                            }

                        }

                    } catch (Exception e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(this, "完成工艺任务出现错误！", "提示", 1);
                    }
                }
            }
        }

    }

    public void showContent(int index) {
        if (index == 0) {
            this.tecnicsJTabbedPane.setSelectedIndex(0);
        } else if (index == 1) {
            this.tecnicsJTabbedPane.setSelectedIndex(1);
        }
    }

    public void showTree(int index) {
        if (index == 0) {
            this.treeJTabbedPane.setSelectedIndex(0);
        } else if (index == 1) {
            this.treeJTabbedPane.setSelectedIndex(1);
        }
    }

    public TechnicsTreePanel getTechnicsTreePanel() {
        return this.technicsTreePanel;
    }

    /**
     * 制作中间模型
     */
    public void makeMiddleMode() throws Exception {
        String toolPath = WorkSpaceUtil.mySettingMap.get("MidModelToolInfo=");
        if ((toolPath == null) || (toolPath.trim().equals(""))) {
            JOptionPane.showMessageDialog(this, "没有设置中间模型工具位置启动程序", "提示", 1);
            return;
        }
        logger.debug("零件工艺制作间件");
        XWTreeNode node = this.technicsTreePanel.getCurrentTechnicsNode();
        if (node != null) {
            Element ele = node.getObject().getTreeCellData();
            File directoryFile = new File(MIDDLE_MODE_PATH);
            if (directoryFile.exists()) {
                FilesUtil.delFolder(MIDDLE_MODE_PATH);
            }
            directoryFile.mkdirs();
            Map<String, String> paraMap = new HashMap<String, String>();
            String partOid = ele.attributeValue("partOid");
            paraMap.put("oid", partOid);
            paraMap.put("toolPath", toolPath);
            String filePath = ImageIntf.getMidModelCAD(paraMap, MIDDLE_MODE_PATH);
            if (filePath == null) {
                JOptionPane.showMessageDialog(this, "该零件没有prt图档", "提示", 1);
                return;
            }
            String path = filePath.substring(0, filePath.lastIndexOf(".")) + ".xml";
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            XmlUtil.writeDocument(ele.getDocument(), path);
            logger.debug("MIDDLE_MODE_PATH= " + MIDDLE_MODE_PATH);
            Open3DUtil.createMidModel(paraMap, filePath);
        }
    }

    public void addCartoon() throws Exception {
        logger.debug("装配工艺添加动画");
        XWTreeNode node = this.technicsTreePanel.getCurrentTechnicsNode();
        if (node != null) {
            Element ele = node.getObject().getTreeCellData();
            String partOid = ele.attributeValue("partOid");
            String partNumber = ele.attributeValue("partNumber");
            String techNumber = ele.attributeValue("technicsNumber");
            String techDirPath = WorkSpaceUtil.getTechnicsDirectory(techNumber);
            String techXMLPath = WorkSpaceUtil.getTechnicsPath(techNumber);
            String techType = ele.attributeValue("technicsType");
            if ((techType != null) && (techType.equals("装配工艺"))) {
                String address = WorkSpaceUtil.mySettingMap.get("AssemblageCartoonToolInfo=");
                if ((address == null) || (address.trim().equals(""))) {
                    JOptionPane.showMessageDialog(this, "没有设置装配动画工具位置启动程序", "提示", 1);
                    return;
                }
                Map<String, String> paraMap = new HashMap<String, String>();
                paraMap.put("oid", partOid);
                paraMap.put("partNumber", partNumber);
                paraMap.put("toolPath", address);
                paraMap.put("xmlPath", techXMLPath);
                paraMap.put("filePath", techDirPath);
                logger.debug("调用Open3DUtil.createAnimation3D方法，参数为====" + paraMap);
                Open3DUtil.createAnimation3D(paraMap);
            }
        }
    }

    public void insertProductStep(boolean bool) throws Exception {
        if (this.treeJTabbedPane.getSelectedIndex() == 1) {
            XWTreeNode node = this.technicsTreePanel.getSelectedTreeNode();
            if (node != null) {
                XWTreeObject xo = node.getObject();
                if ((xo instanceof XWStepTreeObject)) {
                    int index = this.tecnicsJTabbedPane.getSelectedIndex();
                    if (index == 1) {
                        this.tecnicsJTabbedPane.setSelectedIndex(0);
                        Element ele = xo.getTreeCellData();
                        String stepID = ele.attributeValue("bsoID");
                        XWTreeNode stepNode = this.technicsTreePanel.getStepNode(stepID);
                        if (stepNode == null) {
                            return;
                        }

                        node = stepNode;
                        xo = node.getObject();
                    } else {
                        judgeValueModified();
                    }
                    createTechnicsRoute();

                    XWTreeNode techNode = this.technicsTreePanel.getCurrentTechnicsNode();

                    XWTechnicsTreeObject xto = (XWTechnicsTreeObject) techNode.getObject();
                    Element techEle = xto.getTreeCellData();

                    XWStepTreeObject curStep = (XWStepTreeObject) xo;

                    boolean islast = false;
                    if (node.getNextSibling() == null) {
                        islast = true;
                    }
                    String stepNumber = curStep.getTreeCellData().attributeValue("stepNumber");
                    String stepName = curStep.getTreeCellData().attributeValue("stepName");
                    String bsoID = curStep.getTreeCellData().attributeValue("bsoID");
                    int number = XmlUtility.getSubFigure(stepNumber);
                    logger.debug("在工序:" + stepNumber + "下插入新工序:" + (number + 1));
                    Element next = XmlUtility.getStepByStepNumber(techEle, number + 1 + "");
                    if (next != null) {
                        JOptionPane.showMessageDialog(this, "当前工序下已不能进行插入工序操作！", "提示", 1);
                        return;
                    }

                    Element procedureEle = null;
                    if (!bool) {
                        procedureEle = XmlUtility.createProcedure();
                    } else {
                        procedureEle = insertProcedureTemplate(techEle);
                    }
                    if (procedureEle == null) {
                        return;
                    }
                    /**移除工序模板中通用特殊质量记录表  add by liangbo 20180403*/
                   // MPMParameterProcessor.removeComSpeElements(procedureEle);
                    /**移除参装件信息*/
                  //  XmlUtility.removeCzjElements(procedureEle);
                    if (islast)
                        XmlUtility.setAttributeValue(procedureEle, "stepNumber", number + 10 + "");
                    else
                        XmlUtility.setAttributeValue(procedureEle, "stepNumber", number + 1 + "");

                    String stepId = new UID().toString();
                    XmlUtility.setAttributeValue(procedureEle, "bsoID", stepId);

                    XmlUtility.setAttributeValue(procedureEle, "preStep", number + "_" + stepName);
                    XmlUtility.setAttributeValue(procedureEle, "preBsoID", bsoID);
                    List<Element> paceElements = procedureEle.selectNodes("paces/QMProcedureInfo");
                    for (Element paceElement : paceElements) {
                        String paceId = new UID().toString();
                        XmlUtility.setAttributeValue(paceElement, "bsoID", paceId);
                        //插入工序模板 工步端白羽文件复用
                        List<Element> schemaDatas = XmlUtility.getSchemaDatas(paceElement);
                        ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();
                        for(Element schemaData : schemaDatas) {
                            ArrayList<String> bylist = new ArrayList<String>();
                            String bynumber = schemaData.attributeValue("id");
                            String version = schemaData.attributeValue("version");
                            bylist.add(bynumber);
                            bylist.add(bynumber);
                            bylist.add(version);
                            bylist.add(version);
                            lists.add(bylist);
                        }
                        if(lists.size() > 0) {
                            ArrayList<ArrayList<String>> bys = ProcessParameterToWCIntf.quoteBaiyuTemplate(lists, techEle.attributeValue("technicsNumber"), stepId, paceId);
                            if(bys != null && bys.size() > 0){
                                Element schemaData = XmlUtility.getSchemaData(paceElement);
                                XmlUtility.removeAllChildElements(schemaData);
                                for (ArrayList<String> list : bys) {
                                    Element schemaEle = schemaData.addElement(XmlUtility.SCHEMA_TAG);
                                    XmlUtility.setAttributeValue(schemaEle,"order",list.get(0));
                                    XmlUtility.setAttributeValue(schemaEle,"id",list.get(1));
                                    XmlUtility.setAttributeValue(schemaEle,"name",list.get(2));
                                    XmlUtility.setAttributeValue(schemaEle,"version",list.get(3));
                                    XmlUtility.setAttributeValue(schemaEle,"creator",list.get(4));
                                    XmlUtility.setAttributeValue(schemaEle,"mofitier",list.get(5));
                                    XmlUtility.setAttributeValue(schemaEle,"createTime",list.get(6));
                                    XmlUtility.setAttributeValue(schemaEle,"mofityTime",list.get(7));
                                    XmlUtility.setAttributeValue(schemaEle,"docNumber",list.get(8));
                                    XmlUtility.setAttributeValue(schemaEle,"tableType",list.get(9));
                                    XmlUtility.setAttributeValue(schemaEle,"dept",list.get(10));
                                }
                            }
                        }
                    }
                    List userlist = UserUtil.getCurrentUserOid();
                    if ((userlist != null) && (userlist.size() == 3)) {
                        String creator = (String) userlist.get(0);
                        String creatorOid = (String) userlist.get(1);
                        logger.debug("设置当前工序责任人为======" + creatorOid);
                        XmlUtility.setAttributeValue(procedureEle, "responser", creatorOid);
                    }
                    String partOid = techEle.attributeValue("partOid");
                    if (partOid != null) {
                        String responserGroup = TechnicsIntf.getUsertechnicsGroupName(partOid);
                        logger.debug("当前工艺零件======" + partOid + "===责任组==" + responserGroup);
                        if (responserGroup == null)
                            responserGroup = "";
                        XmlUtility.setAttributeValue(procedureEle, "responserGroup", responserGroup);
                    }

                    XmlUtility.addProcedure(techEle, procedureEle);
                    XmlUtility.orderSteps(techEle);
                    this.technicsTreePanel.insertProcedureNode(node, procedureEle);
                    showData(this.technicsTreePanel.getSelectedTreeNode(), null);
                }
            }
        }
    }

    public void setCheckStep(boolean bool) throws Exception {
        if (this.treeJTabbedPane.getSelectedIndex() == 1) {
            XWTreeNode node = this.technicsTreePanel.getSelectedTreeNode();
            if (node != null) {
                XWTreeObject xo = node.getObject();
                if ((xo instanceof XWStepTreeObject)) {
                    int index = this.tecnicsJTabbedPane.getSelectedIndex();
                    if (index == 1) {
                        this.tecnicsJTabbedPane.setSelectedIndex(0);
                        Element ele = xo.getTreeCellData();
                        String stepID = ele.attributeValue("bsoID");
                        XWTreeNode stepNode = this.technicsTreePanel.getStepNode(stepID);
                        if (stepNode == null) {
                            return;
                        }

                        node = stepNode;
                        xo = node.getObject();
                    } else {
                        judgeValueModified();
                    }
                    createTechnicsRoute();

                    XWTreeNode techNode = this.technicsTreePanel.getCurrentTechnicsNode();

                    XWTechnicsTreeObject xto = (XWTechnicsTreeObject) techNode.getObject();
                    Element techEle = xto.getTreeCellData();

                    XWStepTreeObject curStep = (XWStepTreeObject) xo;

                    boolean islast = false;
                    if (node.getNextSibling() == null) {
                        islast = true;
                    }
                    String stepNumber = curStep.getTreeCellData().attributeValue("stepNumber");
                    int number = XmlUtility.getSubFigure(stepNumber);
                    logger.debug("在工序:" + stepNumber + "下插入新工序:" + (number + 1));
                    Element next = XmlUtility.getStepByStepNumber(techEle, number + 1 + "");
                    if (next != null) {
                        JOptionPane.showMessageDialog(this, "当前工序下已不能进行插入工序操作！", "提示", 1);
                        return;
                    }

                    Element procedureEle = null;
                    if (!bool) {
                        procedureEle = XmlUtility.createProcedure();
                    } else {
                        procedureEle = insertProcedureTemplate(techEle);
                    }
                    if (procedureEle == null) {
                        return;
                    }

                    //拷贝当前工序的属性
                    //procedureEle = xo.getTreeCellData().createCopy();

                    if (islast) {
                        XmlUtility.setAttributeValue(procedureEle, "stepNumber", number + 10 + "");
                    } else {
                        XmlUtility.setAttributeValue(procedureEle, "stepNumber", number + 1 + "");
                    }

                    //修改检验工序的属性
                    XmlUtility.setAttributeValue(procedureEle, "bsoID", new UID().toString());
                    XmlUtility.setAttributeValue(procedureEle, "stepName", "检验");
                    //XmlUtility.setAttributeValue(procedureEle, "workShop", "检");
                    //复制工序内容
                    XmlUtility.setProcedureContent(procedureEle, XmlUtility.getProcedureContent(xo.getTreeCellData()));

                    //修改当前工序的属性
                    XmlUtility.setAttributeValue(xo.getTreeCellData(), "nextBsoID", procedureEle.attributeValue("bsoID"));

                    List userlist = UserUtil.getCurrentUserOid();
                    if ((userlist != null) && (userlist.size() == 3)) {
                        String creator = (String) userlist.get(0);
                        String creatorOid = (String) userlist.get(1);
                        logger.debug("设置当前工序责任人为======" + creatorOid);
                        XmlUtility.setAttributeValue(procedureEle, "responser", creatorOid);
                    }
                    String partOid = techEle.attributeValue("partOid");
                    if (partOid != null) {
                        String responserGroup = TechnicsIntf.getUsertechnicsGroupName(partOid);
                        logger.debug("当前工艺零件======" + partOid + "===责任组==" + responserGroup);
                        if (responserGroup == null)
                            responserGroup = "";
                        XmlUtility.setAttributeValue(procedureEle, "responserGroup", responserGroup);
                    }

                    XmlUtility.addProcedure(techEle, procedureEle);
                    XmlUtility.orderSteps(techEle);
                    this.technicsTreePanel.insertProcedureNode(node, procedureEle);
                    showData(this.technicsTreePanel.getSelectedTreeNode(), null);
                }
            }
        }
    }

    public void setPreProdure() {
        if (this.treeJTabbedPane.getSelectedIndex() == 1) {
            SetPreProdureDialog preProdureDialog = new SetPreProdureDialog(this);
            preProdureDialog.showDialog();
        }
    }

    public void newFrockApply() {
        Document document = getCurrentTechnics();
        if (document == null)
            return;
        XWTreeNode node = this.technicsTreePanel.getCurrentTechnicsNode();

        Element techElement = null;
        String version = null;
        try {
            techElement = XmlUtility.getTechnicsElement(document);
            Object obj = this.getTechnicsStepJPanel().workShopBox.getSelectedItem();
            Map<String, String> map = new HashMap<String, String>();
            map.put("partOid", techElement.attributeValue("partOid"));
            map.put("partNumber", techElement.attributeValue("partNumber"));
            if (obj != null) {
                map.put("workshop", obj.toString());
            }
            map.put("parentPartNumber", techElement.attributeValue("parentPartNumber"));
            map.put("productNumberValue", techElement.attributeValue("productNumber"));
            FrockCardApplyDialog dialog = new FrockCardApplyDialog(map, this);
            Frock frock = dialog.showDialog();
            logger.debug("return frock= " + frock);
            if (frock != null) {
                addNewFrock(SwitchUtil.javaBeanToHashMap(frock));
                this.rtPanel.refreshFrock();
                JOptionPane.showMessageDialog(this, "工装申请卡创建成功！", "提示", 1);
            }
            // else {
            // JOptionPane.showMessageDialog(this, "您没有创建工装申请卡的权限！", "提示", 1);
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addNewFrock(Map map) {
        tecnicsJTabbedPane.setSelectedIndex(0);
        technicsStepJPanel.getToolLinkPanel().setOneRowTableValue(map, "frock");
        technicsStepJPanel.getTabbedPane().setSelectedIndex(1);
        technicsStepJPanel.getTabbedPane().updateUI();

        treeJTabbedPane.setSelectedComponent(rtPanel);
    }

    public void submitUniteTechnics() {
        logger.debug("提交合编工艺...................");
        Document document = getCurrentTechnics();
        if (document == null)
            return;
        XWTreeNode node = this.technicsTreePanel.getCurrentTechnicsNode();

        Element techElement = null;
        String version = null;
        try {
            techElement = XmlUtility.getTechnicsElement(document);
            String topPartOid = techElement.attributeValue("parentPartOid");
            String partOid = techElement.attributeValue("partOid");
            String technicsNumber = techElement.attributeValue("technicsNumber");
            String technicsName = WorkSpaceUtil.getTechnicsDirectory(techElement.attributeValue("technicsNumber"));
            version = techElement.attributeValue("version");
            if ((version == null) || (version.equals(""))) {
                version = "1.0";
            }

            int index = technicsName.lastIndexOf("\\");
            if (index >= 0) {
                technicsName = technicsName.substring(index + 1);
            }
            String unite = techElement.attributeValue("unite");

            byte[] technicsZip = FilesUtil.getTechnicsByte(technicsNumber);
            HashMap map = TechnicsIntf.uploadUniteTechnics(topPartOid, partOid, technicsName, unite, technicsZip, version);
            String success = (String) map.get("success");
            logger.debug("提交合编工艺返回结果..................." + success);
            if (success.equals("success")) {
                JOptionPane.showMessageDialog(this, "提交合编工艺成功！", "提示", 1);

                String newVersion = (String) map.get("version");
                String lifecycle = (String) map.get("lifecycle");
                XmlUtility.setAttributeValue(techElement, "version", newVersion);
                XmlUtility.setAttributeValue(techElement, "lifecycle", lifecycle);
                saveProcess(techElement);
                XWTreeNode rootNode = (XWTreeNode) xwPartTreePanel.getTree().getModel().getRoot();
                updateTreeTechnics(rootNode, techElement.attributeValue("partOid"), techElement.attributeValue("technicsName"), techElement.attributeValue("technicsCategory"), newVersion);
                xwPartTreePanel.expandAllNode(rootNode);
                if (node != null) {
                    this.technicsTreePanel.expandAllNode(node);
                    this.technicsTreePanel.getTree().setSelectionPath(new TreePath(node.getPath()));
                    this.xwPartTreePanel.getTree().updateUI();
                }
                technicsMasterJPanel.setUIValues(techElement);
                Vector<UploadTechnics> editTechnics = this.editTechnics;
                if (editTechnics != null) {
                    for (UploadTechnics temp : editTechnics) {
                        if (temp.getTechnicsNumber().equals(technicsNumber)) {
                            this.editTechnics.remove(temp);
                            break;
                        }
                    }
                }
            } else {
                String errorMessage = (String) map.get("errorMessage");
                if ((errorMessage != null) && (!errorMessage.equals(""))) {
                    JOptionPane.showMessageDialog(this, errorMessage, "提示", 1);
                } else {
                    JOptionPane.showMessageDialog(this, "提交合编工艺过程中出现错误！", "提示", 1);
                }

            }

        } catch (Exception e1) {

            JOptionPane.showMessageDialog(this, "提交合编工艺过程中出现错误！", "提示", 1);
            e1.printStackTrace();
        }
    }

    /**
     * 下载普通工艺
     *
     * @param partNumber
     */
    private void downloadTechnics(String partNumber, Element part) {
        if (childPartOid != null && childPartOid.trim().length() > 0 && partNumber != null && partNumber.trim().length() > 0) {
            String techXMLPath = null;
            try {
                String filepath = null;
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("oid", childPartOid);
                map.put("partNumber", partNumber);
                map.put("category", "normal");
                String reportTechnics1 = "";
                if (reportTechnics) {
                    reportTechnics1 = "report";
                }
                map.put("reportTechnics", reportTechnics1);
                // 当前可编辑的零部件需要把其所有的工艺文件下载下来.
                // NewTechnicsPart.childPartOid就是当前可编辑零部件的OID
                map.put("isEditable", "true");
                logger.debug("开始下载工艺数据...");
                long startTime4 = System.currentTimeMillis();
                logger.debug("=======map>>>" + map);
                List<Vector> list = TechnicsIntf.getTechnicsByPart(map);
                logger.debug("=======list>>>" + list);
                long endTime4 = System.currentTimeMillis();
                logger.debug("下载工艺数据耗时：" + (endTime4 - startTime4) + " ms");
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        Vector result = list.get(i);
                        if ((result != null) && (result.size() == 6)) {
                            String fileName = (String) result.get(0);
                            byte[] data = (byte[]) result.get(1);
                            if ((data == null) || (data.length <= 0)) {
                                return;
                            }
                            if (fileName.toLowerCase().endsWith(".zip")) {
                                fileName = fileName.substring(0, fileName.length() - 4);
                            }
                            String lifecycle = (String) result.get(2);
                            String version = (String) result.get(3);
                            tmpTechnicsNumber = WorkSpaceUtil.getTechnicsNumber(fileName);
                            File f = new File(WorkSpaceUtil.getCommonTechnicsRootPath() + "\\" + fileName);
                            if (f.exists()) {
                                if (SwingUtil.showConfirmDialog(result.get(5) + "数据文件在本地已存在，是否确定将服务器文件覆盖本地文件？", Constants.TIP, 2) != JOptionPane.YES_OPTION) {
                                    return;
                                }
                                WorkSpaceUtil.delete(f);
                            }
                            filepath = WorkSpaceUtil.createTechnicsDirectory(fileName);

                            logger.debug("开始解压工艺文件数据包,数据包大小="+data.length/1024+" KB");
                            long startTime = System.currentTimeMillis();
                            TechnicsReleaseUtil.unZip(data, filepath);
                            long endTime = System.currentTimeMillis();
                            logger.debug("解压工艺文件数据包耗时："+(endTime-startTime)+" ms");

                            String technicsNumber = WorkSpaceUtil.getTechnicsNumber(fileName);
                            Document doc = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(technicsNumber);
                            Element ele = XmlUtility.getTechnicsElement(doc);
                            XmlUtility.setAttributeValue(ele, "version", version);
                            XmlUtility.setAttributeValue(ele, "partOid", part.attributeValue("oid"));
                            XmlUtility.setAttributeValue(ele, "lifecycle", lifecycle);
                            firstSaveProcess(ele);
                            techXMLPath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
                            File file = new File(techXMLPath);
                            if (!file.exists()) {
                                JOptionPane.showMessageDialog(this, "下载工艺出现错误！", "提示", 1);
                                File technDIR = new File(filepath);
                                if ((technDIR != null) && (technDIR.exists()))
                                    WorkSpaceUtil.delete(technDIR);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (techXMLPath != null) {
                    File file = new File(techXMLPath);
                    if (!file.exists())
                        try {
                            WorkSpaceUtil.delete(file);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                }
            }
        }
    }

    /**
     * 下载返工工艺
     *
     * @param reworkTaskId 任务id
     */
    private void downloadTechnics(List<String> list, String category) {
        String techXMLPath = null;
        try {
            if (NewTechnicsPart.flag) {
                // list = new ArrayList<String>();
                // list.add("1252558");
                // list.add("AL2_907_1459");
                // list.add("1327898");
                // list.add("AL2_907_1460");
            }
            if (list != null && list.size() == 2) {
                String filepath = null;
                HashMap<String, String> map = new HashMap<String, String>();
                String partOid = list.get(0);
                map.put("oid", partOid);
                map.put("partNumber", list.get(1));
                map.put("category", category);
                //当前可编辑的零部件需要把其所有的工艺文件下载下来. NewTechnicsPart.childPartOid就是当前可编辑零部件的OID
                //	if(childPartOid.equals(partOid)) {
                map.put("isEditable", "true");
                //	} else {
                //		map.put("isEditable", "false");
                //	}
                String reportTechnics1 = "";
                if (reportTechnics) {
                    reportTechnics1 = "report";
                }
                map.put("reportTechnics", reportTechnics1);
                reworkPartNumber = list.get(1);
                List<Vector> technics = TechnicsIntf.getTechnicsByPart(map);
                for (Vector result : technics) {
                    if ((result != null) && (result.size() == 4)) {
                        String fileName = (String) result.get(0);
                        byte[] data = (byte[]) result.get(1);
                        if ((data == null) || (data.length <= 0)) {
                            return;
                        }
                        if (fileName.toLowerCase().endsWith(".zip")) {
                            fileName = fileName.substring(0, fileName.length() - 4);
                        }
                        String lifecycle = (String) result.get(2);
                        String version = (String) result.get(3);
                        tmpTechnicsNumber = WorkSpaceUtil.getTechnicsNumber(fileName);
                        File file = null;
                        if ("rework".equals(category)) {
                            file = new File(WorkSpaceUtil.getReworkTechnicsRootPath() + "\\" + fileName);
                        } else if ("temp".equals(category)) {
                            file = new File(WorkSpaceUtil.getTempTechnicsRootPath() + "\\" + fileName);
                        }
                        if (file.exists()) {
                            if (SwingUtil.showConfirmDialog("数据文件在本地已存在，是否确定将服务器文件覆盖本地文件？", Constants.TIP, 2) != JOptionPane.YES_OPTION) {
                                return;
                            }
                            WorkSpaceUtil.delete(file);
                        }
                        if ("rework".equals(category)) {
                            filepath = WorkSpaceUtil.createReworkTechnicsDirectory(fileName);
                        } else if ("temp".equals(category)) {
                            filepath = WorkSpaceUtil.createTempTechnicsDirectory(fileName);
                        }
                        TechnicsReleaseUtil.unZip(data, filepath);
                        String technicsNumber = WorkSpaceUtil.getTechnicsNumber(fileName);
                        Document doc = null;
                        if ("rework".equals(category)) {
                            doc = WorkSpaceUtil.getReWorkTechnicsDocumentByTechnicsPath(filepath);
                        } else if ("temp".equals(category)) {
                            doc = WorkSpaceUtil.getTempTechnicsDocumentByTechnicsPath(filepath);
                        }
                        Element element = XmlUtility.getTechnicsElement(doc);
                        XmlUtility.setAttributeValue(element, "partOid", partOid);
                        XmlUtility.setAttributeValue(element, "version", version);
                        XmlUtility.setAttributeValue(element, "lifecycle", lifecycle);
                        firstSaveProcess(element);
                        if ("rework".equals(category)) {
                            techXMLPath = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, XmlUtility.getAttributeValue(element, "technicsName"));
                        } else if ("temp".equals(category)) {
                            techXMLPath = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, XmlUtility.getAttributeValue(element, "technicsName"));
                        }

                        file = new File(techXMLPath);
                        if (!file.exists()) {
                            JOptionPane.showMessageDialog(this, "下载工艺出现错误！", "提示", 1);
                            File technDIR = new File(filepath);
                            if ((technDIR != null) && (technDIR.exists()))
                                WorkSpaceUtil.delete(technDIR);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (techXMLPath != null) {
                File file = new File(techXMLPath);
                logger.debug("下载后工艺文件路径=========" + techXMLPath + "===是否存在==" + file.exists());
                if (!file.exists())
                    try {
                        WorkSpaceUtil.delete(file);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
            }
        }
    }

    public void show3DPane(String actionID) {
        try {
            this.creopanel.setVisible(false);
            this.panel3D.setVisible(true);

            Document doc = getCurrentTechnics();
            if (doc != null) {
                Element techEle = XmlUtility.getTechnicsElement(doc);
                String techNumber = techEle.attributeValue("technicsNumber");
                String wrlFile = techEle.attributeValue("wrlFile");
                if ((wrlFile != null) && (wrlFile.trim().length() > 0)) {
                    String path = WorkSpaceUtil.getTechnicsDirectory(techNumber) + File.separator + wrlFile + ".wrl";
                    logger.debug("3D图形文件路径为=====" + path);
                    this.panel3D.setView(path);
                    this.panel3D.click(actionID);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show2DPane(Vector v) {
        this.panel3D.setVisible(false);
        this.creopanel.setVisible(true);

        this.creopanel.setView(v);
    }

    public String getCurrentUser() {
        List userlist = UserUtil.getCurrentUserOid();
        if ((userlist != null) && (userlist.size() == 3)) {
            creatorOid = (String) userlist.get(1);
        }

        return creatorOid;
    }

    public void reSettingData() {
        try {
            this.creopanel.setVisible(false);
            this.panel3D.setVisible(false);
            getQuickCreateProcedureJPanel().clear();
            getTechnicsRouteJPanel().clearAll();
            clearRightContent();
            this.technicsTreePanel.removeCurrentTechnicd();
            this.xwPartTreePanel.reExpandPBOM();
            this.tecnicsJTabbedPane.setSelectedIndex(0);
            this.treeJTabbedPane.setSelectedIndex(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResourceTreePanel getResourceTreePanel() {
        return this.rtPanel;
    }

    public MeasureTreePanel getMeasurePanel() {
        return measurePanel;
    }

    public KtTreePanel getKtTreePanel() {
        return this.ktPanel;
    }

    public EpTreePanel getEpTreePanel() {
        return this.eqPanel;
    }

    public SDashboardTreePanel getsDashboardPanel() {
        return this.sDashboardPanel;
    }

    public UnSDashboardTreePanel getUnSDashboardPanel() {
        return this.unSDashboardPanel;
    }

    public MtTreePanel getMTPanel() {
        return this.mtPanel;
    }

    public CsTreePanel getCSTreePanel() {
        return this.csTreePanel;
    }

    public PdNameTreePanel getPdNameTreePanel() {
        return this.namePanel;
    }

    public JTabbedPane getLeftTab() {
        return this.treeJTabbedPane;
    }

    public XWPartTreePanel getXWPartTreePanel() {
        return this.xwPartTreePanel;
    }

    public UniversalToolBar getUniversalToolBar() {
        return this.universalToolBar;
    }

    /**
     * 根据PBOM xml 更新工艺（下载工艺压缩包）
     *
     * @param bytes
     * @return
     */
    public Set<String> updateTechnics(byte[] bytes) {
        Set<String> oids = null;
        String techXMLPath = null;
        try {
            if (bytes != null) {
                String userOid = getCurrentUser();
                Document doc = BomXMLUtil.getDocument(bytes);
                if ((doc != null) && (userOid != null) && (userOid.trim().length() > 0)) {
                    HashMap allPartOid = new HashMap();
                    Element productElement = BomXMLUtil.getProduct(doc);
                    Element mainPart = BomXMLUtil.getMainPart(productElement);
                    //查询当前PBOM下所有的部件OID哪些是当前用户需要编制工艺的
                    List<String> allOid = new ArrayList<String>();
                    if (reportTechnics) {
                        BomXMLUtil.getAllPartOid(mainPart, allOid);
                    } else {
                        if (!"1".equals(EditorConfig.startType)) {
                            BomXMLUtil.getAllPartOid(mainPart, allOid);
                        } else {
                            BomXMLUtil.getTWOPartOid(mainPart, allOid);
                        }
                    }

                    if (childPartOid != null && !"".equals(childPartOid)) {
                        allOid.remove(childPartOid);
                    }
                    if (reportTechnics && !"1".equals(EditorConfig.startType)) {
                        BomXMLUtil.filterPBOM(mainPart, userOid, allPartOid);
                    } else {
                        if (!"1".equals(EditorConfig.startType)) {
                            BomXMLUtil.filterPBOM(mainPart, userOid, allPartOid);
                        } else {
                            BomXMLUtil.filterPBOM1(mainPart, userOid, allPartOid);
                        }
                    }
                    oids = allPartOid.keySet();
                    if ((allPartOid != null) && (allPartOid.size() > 0)) {
                        Set set = allPartOid.keySet();
                        if (set != null) {
                            Iterator it = set.iterator();
                            if (it != null) {
                                while (it.hasNext()) {
                                    String partOid = (String) it.next();
                                    if ((partOid != null) && (partOid.trim().length() > 0)) {

                                        if (!allOid.contains(partOid)) {
                                            continue;
                                        }

                                        String partNumber = (String) allPartOid.get(partOid);
                                        if (partNumber != null) {
                                            HashMap map = new HashMap();
                                            map.put("oid", partOid);
                                            map.put("partNumber", partNumber);
                                            map.put("category", "normal");

                                            //当前可编辑的零部件需要把其所有的工艺文件下载下来. NewTechnicsPart.childPartOid就是当前可编辑零部件的OID
                                            map.put("isEditable", "true");

                                            String reportTechnics1 = "";
                                            if (reportTechnics) {
                                                reportTechnics1 = "report";
                                            }
                                            map.put("reportTechnics", reportTechnics1);
                                            List<Vector> vector = TechnicsIntf.getTechnicsByPart(map);
                                            if (vector.size() > 0) {
                                                for (Vector result : vector) {
                                                    if ((result != null) && (result.size() == 6)) {
                                                        String technicsNumber = (String) result.get(4);
                                                        String filePath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
                                                        boolean flag = false;
                                                        if (filePath != null) {
//															if (SwingUtil.showConfirmDialog("1当前工艺数据已存在，是否覆盖？",
//																			Constants.TIP, 2) == JOptionPane.YES_OPTION) {
                                                            WorkSpaceUtil.delete(new File(filePath));
                                                            flag = true;
//															}
                                                        } else {
                                                            flag = true;
                                                        }
                                                        if (flag) {
                                                            String fileName = (String) result.get(0);
                                                            byte[] data = (byte[]) result.get(1);
                                                            if ((data != null) && (data.length > 0)) {
                                                                if (fileName.toLowerCase().endsWith(".zip")) {
                                                                    fileName = fileName.substring(0, fileName.length() - 4);
                                                                }
                                                                String lifecycle = (String) result.get(2);
                                                                String version = (String) result.get(3);
                                                                String filepath = WorkSpaceUtil.createTechnicsDirectory(fileName);
                                                                techXMLPath = filepath;

                                                                TechnicsReleaseUtil.unZip(data, filepath);
                                                                Document document = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(technicsNumber);
                                                                Element ele = XmlUtility.getTechnicsElement(document);
                                                                XmlUtility.setAttributeValue(ele, "version", version);
                                                                XmlUtility.setAttributeValue(ele, "lifecycle", lifecycle);
                                                                firstSaveProcess(ele);
                                                                String temp = WorkSpaceUtil.getTechnicsPath(technicsNumber);
                                                                if (temp != null && !"".equals(temp)) {
                                                                    File file = new File(temp);
                                                                    if (!file.exists()) {
                                                                        File technDIR = new File(techXMLPath);
                                                                        if ((technDIR != null) && (technDIR.exists()))
                                                                            WorkSpaceUtil.delete(technDIR);
                                                                    } else {
                                                                        techXMLPath = null;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (techXMLPath != null) {
                File technDIR = new File(techXMLPath);
                if ((technDIR != null) && (technDIR.exists()))
                    try {
                        WorkSpaceUtil.delete(technDIR);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
            }
        }
        return oids;
    }

    public String getKey(Map map, String value) {
        if ((map == null) || (value == null))
            return "NOKEY";
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            Object temp = map.get(key);
            if (temp.equals(value)) {
                return key.toString();
            }
        }
        return "NOKEY";
    }

    public void getOtherTechnics() {
        if (this.treeJTabbedPane.getSelectedIndex() == 0) {
            XWTreeNode node = this.xwPartTreePanel.getSelectedTreeNode();
            if (node != null) {
                if ((node.getObject() instanceof XWPartTreeObject)) {
                    Element partElement = node.getObject().getTreeCellData();
                    if (partElement != null) {
                        String partNumber = partElement.attributeValue("partNumber");
                        String partOid = partElement.attributeValue("oid");
                        if ((partOid != null) && (partOid.trim().length() > 0)) {
                            String techXMLPath = null;
                            try {
                                String filepath = null;
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("oid", partOid);
                                map.put("partNumber", partNumber);
                                map.put("category", "normal");

                                //当前可编辑的零部件需要把其所有的工艺文件下载下来. NewTechnicsPart.childPartOid就是当前可编辑零部件的OID
                                //		if(childPartOid.equals(partOid)) {
                                map.put("isEditable", "true");
                                //		} else {
                                //			map.put("isEditable", "false");
                                //		}

                                String reportTechnics1 = "";
                                if (reportTechnics) {
                                    reportTechnics1 = "report";
                                }
                                map.put("reportTechnics", reportTechnics1);
                                List<Vector> vector = TechnicsIntf.getTechnicsByPart(map);
                                if (vector.size() == 6) {
                                    Vector result = vector.get(0);
                                    if ((result != null) && (result.size() == 4)) {
                                        String fileName = (String) result.get(0);
                                        byte[] data = (byte[]) result.get(1);
                                        if ((data == null) || (data.length <= 0)) {
                                            return;
                                        }
                                        String lifecycle = (String) result.get(2);
                                        String version = (String) result.get(3);

                                        if (fileName.toLowerCase().endsWith(".zip")) {
                                            fileName = fileName.substring(0, fileName.length() - 4);
                                        }

                                        File f = new File(WorkSpaceUtil.getCommonTechnicsRootPath() + "\\" + fileName);
                                        if (f.exists()) {
                                            int yes = JOptionPane.showConfirmDialog(this, "当前工艺数据已存在，是否覆盖？", "提示", 1);
                                            if (yes == 1) {
                                                return;
                                            }
                                            WorkSpaceUtil.delete(f);
                                        }
                                        filepath = WorkSpaceUtil.createTechnicsDirectory(fileName);
                                        TechnicsReleaseUtil.unZip(data, filepath);
                                        String technicsNumber = WorkSpaceUtil.getTechnicsNumber(fileName);
                                        Document doc = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(technicsNumber);
                                        Element ele = XmlUtility.getTechnicsElement(doc);
                                        XmlUtility.setAttributeValue(ele, "version", version);
                                        XmlUtility.setAttributeValue(ele, "lifecycle", lifecycle);
                                        saveProcess(ele);
                                        techXMLPath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
                                        File file = new File(techXMLPath);
                                        if (!file.exists()) {
                                            JOptionPane.showMessageDialog(this, "下载工艺出现错误！", "提示", 1);
                                            File technDIR = new File(filepath);
                                            if ((technDIR != null) && (technDIR.exists()))
                                                WorkSpaceUtil.delete(technDIR);
                                        } else {
                                            UploadTechnics technics = ObjectTransfer.technicsElementToUploadTechnics(ele);
                                            downLoadTech.add(technics);
                                            // TODO XCZ
                                            // this.xwPartTreePanel.addTechnicsNode(node);
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(this, "该零部件尚未创建工艺规程！", "提示", 1);
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(this, "该零部件尚未创建工艺规程！", "提示", 1);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (techXMLPath != null) {
                                    File file = new File(techXMLPath);
                                    if (!file.exists())
                                        try {
                                            WorkSpaceUtil.delete(file);
                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                        }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public Element insertProcedureTemplate(Element techElement) {
        Element stepElement = null;
        if (techElement != null) {
            String technicsNumber = techElement.attributeValue("technicsNumber");
            String techFolderPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
            String type = getTechType(techElement);
            String path = null;
            for (int i = 0; i < technicsTypes[1].length; i++) {
                if (technicsTypes[1][i].equals(type)) {
                    type = technicsTypes[0][i];
                    path = WorkSpaceUtil.getProcedureTempletPath(type);
                }
            }

            // if (type.equals("装配工艺")) {
            // type = "assembleTemplate";
            // path = WorkSpaceUtil.getAssembleProcedureTempletPath();
            // } else {
            // type = "partTemplate";
            // path = WorkSpaceUtil.getPartProcedureTempletPath();
            // }
            StepTemplateSearchDialog dialog = new StepTemplateSearchDialog(WorkSpaceUtil.getStepRootPath(), type, this);
            Vector vec = dialog.showDialog();
            TreePath selectionPath = dialog.stTree.getSelectionPath();
            if(selectionPath==null) {
            	selectionPath=StepTemplateSearchDialog.currentTreePath;
            }
            
            Object lastPathComponent = selectionPath.getLastPathComponent();
            if (lastPathComponent instanceof TpNode) {
                TpNode tpNode = (TpNode) lastPathComponent;
                TpTreeNode tpTreeNode = (TpTreeNode) tpNode.getParent();
                String stepType = tpTreeNode.getStepType();
                String name = tpTreeNode.getName();
                for (int i = 0; i < technicsTypes[1].length; i++) {
                    if (technicsTypes[1][i].equals(name)) {
                        type = technicsTypes[0][i];
                        path = WorkSpaceUtil.getProcedureTempletPath(type);
                    }
                }
            }

            if ((vec != null) && (vec.size() > 0) && (path != null)) {
                String templateName = (String) vec.get(0);
                String filepath = path + "\\" + templateName;

                if (vec.size() > 1) {
                    File file = new File(filepath);
                    FilesUtil.delFolder(file.getPath());
                    file.mkdirs();
                    if (file.exists()) {
                        // File file = new File(
                        // System.getProperty("java.io.tmpdir")
                        // + File.separator + templateName);
                        // if (file.exists()) {
                        // FilesUtil.delFolder(file.getPath());
                        // }
                        // if (!file.exists()) {
                        // file.mkdirs();
                        // }
                        byte[] data = (byte[]) vec.get(1);
                        TechnicsReleaseUtil.unZip(data, file.getPath());
                        filepath = file.getPath();
                    }
                }
                String xmlName = filepath + "\\" + templateName + ".xml";
                File xmlFile = new File(xmlName);
                if (!xmlFile.exists()) {
                    JOptionPane.showMessageDialog(this, "本地模板文件丢失！", "提示", 1);
                    WorkSpaceUtil.delete(new File(filepath));
                } else {
                    Document procedureTemplateDoc = XmlUtility.getDocument(xmlFile);
                    if (procedureTemplateDoc != null) {
                        Element mainEle = procedureTemplateDoc.getRootElement();
                        if (mainEle != null) {
                            stepElement = (Element) mainEle.clone();
                        }
                    }
                    System.out.println("bsoID1= " + stepElement.attributeValue("bsoID"));
                    StepTemplateCopyHandler.generateStepFromTemplate(techFolderPath, filepath, stepElement);
                    System.out.println("bsoID2= " + stepElement.attributeValue("bsoID"));
                }
            }
        }
        return stepElement;
    }

    private void setTechnicsDisabledByLifecycle() {
        // Document tech = getCurrentTechnics();
        // if (tech != null)
        // try {
        // Element ele = XmlUtility.getTechnicsElement(tech);
        // String lifecycle = ele.attributeValue("lifecycle");
        // if ((lifecycle != null) && (!"".equals(lifecycle))
        // && (!lifecycle.equals("拟制"))
        // && (!lifecycle.equals("驳回")))
        // this.technicsCoWork.setEnabled(false);
        // this.submitUniteTechnics.setEnabled(false);
        // this.technicsConfirm.setEnabled(false);
        // this.technicsUpload.setEnabled(false);
        // this.technicsUpdate.setEnabled(false);
        // this.signed.setEnabled(false);
        // this.completeTask.setEnabled(false);
        // this.createStep.setEnabled(false);
        // this.paste.setEnabled(false);
        // } catch (Exception e1) {
        // e1.printStackTrace();
        // }
    }

    public boolean clearFittings(Vector fList, boolean isClearAll) throws Exception {
        if ((fList == null) || (fList.size() == 0))
            return false;
        DefaultTreeModel model = (DefaultTreeModel) this.technicsTreePanel.getTree().getModel();

        boolean bool = false;
        Document doc = getCurrentTechnics();
        if (doc != null) {
            if (isClearAll) {
                Element technics = XmlUtility.getTechnicsElement(doc);
                List steps = XmlUtility.getAllSteps(technics);
                if ((steps != null) && (steps.size() > 0)) {
                    for (int i = 0; i < steps.size(); i++) {
                        Element step = (Element) steps.get(i);
                        if (step != null) {
                            List paces = XmlUtility.getAllPaces(step);
                            if ((paces != null) && (paces.size() > 0)) {
                                for (int ii = 0; ii < paces.size(); ii++) {
                                    Element pace = (Element) paces.get(ii);
                                    XmlUtility.deleteOnePart(pace, fList);
                                }
                            }
                        }
                    }
                    saveProcess(technics);
                    bool = true;

                    XWTreeNode techNode = this.technicsTreePanel.getCurrentTechnicsNode();
                    if (techNode != null) {
                        for (int k = 0; k < techNode.getChildCount(); k++) {
                            XWTreeNode stepNode = (XWTreeNode) techNode.getChildAt(k);
                            Vector vec = this.technicsTreePanel.recordStepNodeState(stepNode);
                            stepNode.removeAllChildren();
                            stepNode.expandAll();
                            model.reload(stepNode);
                            this.technicsTreePanel.reExpandStepNode(stepNode, vec);
                        }
                    }
                }
            } else {
                Element selectPace = this.technicsStepJPanel.getPaceTable().getSelectedPaceElement();
                if (selectPace != null) {
                    XmlUtility.deleteOnePart(selectPace, fList);
                    saveProcess(selectPace);
                    bool = true;
                    XWTreeNode stepNode = this.technicsStepJPanel.getNode();

                    if (stepNode != null) {
                        Vector vec = this.technicsTreePanel.recordStepNodeState(stepNode);
                        stepNode.removeAllChildren();
                        stepNode.expandAll();
                        model.reload(stepNode);
                        this.technicsTreePanel.reExpandStepNode(stepNode, vec);
                    }
                }
            }
            this.technicsStepJPanel.getPaceTable().refreshPartDatas();
        }
        return bool;
    }

    class KeyInputListener implements ActionListener {
        KeyInputListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().compareTo("copy") == 0) {
                try {
                    NewTechnicsPart.this.copy();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "复制工序出现错误！", "提示", 1);
                }
            } else if (e.getActionCommand().compareTo("paste") == 0) {
                if (NewTechnicsPart.this.treeJTabbedPane.getSelectedIndex() != 1)
                    return;
                XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
                if (node != null) {
                    XWTreeObject xo = node.getObject();
                    if ((xo instanceof XWTechnicsTreeObject)) {
                        boolean sameType = xo.getTreeCellData().attributeValue("technicsType").equals(NewTechnicsPart.technicsType);
                        if (sameType)
                            try {
                                NewTechnicsPart.this.pasteProductTechnics();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                JOptionPane.showMessageDialog(NewTechnicsPart.this, "粘贴工艺出现错误！", "提示", 1);
                            }
                    }
                }
            } else if (e.getActionCommand().compareTo("delete") == 0) {
                try {
                    NewTechnicsPart.this.delete();
                } catch (Exception ee) {

                    ee.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "删除操作出现错误！", "提示", 1);
                }
            } else if (e.getActionCommand().compareTo("createTechnics") == 0) {
                try {
                    NewTechnicsPart.this.createTechnics();
                } catch (Exception ee) {
                    ee.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "创建工艺出现错误！", "提示", 1);
                }
            } else if (e.getActionCommand().compareTo("createStep") == 0) {
                try {
                    NewTechnicsPart.this.createProdcutStep();
                } catch (Exception ee) {
                    ee.printStackTrace();
                    JOptionPane.showMessageDialog(NewTechnicsPart.this, "创建工序出现错误！", "提示", 1);
                }
            }
        }
    }

    class MenuSelectedAdapter implements MenuListener {
        MenuSelectedAdapter() {
        }

        @Override
        public void menuSelected(MenuEvent e) {
            if (NewTechnicsPart.this.treeJTabbedPane.getSelectedIndex() == 0) {
                XWTreeNode node = NewTechnicsPart.this.xwPartTreePanel.getSelectedTreeNode();
                if (node != null) {
                    XWTreeObject xo = node.getObject();
                    if ((xo instanceof XWPartTreeObject)) {
                        NewTechnicsPart.this.createStep.setEnabled(false);
                        NewTechnicsPart.this.setPartSelectedStatus();
                        XWPartTreeObject po = (XWPartTreeObject) xo;
                        //add by machongqi 20150603
                        if (NewTechnicsPart.this.copyElement != null || NewTechnicsPart.this.copyTechElement != null) {
                            NewTechnicsPart.this.paste.setEnabled(true);
                        } else {
                            NewTechnicsPart.this.paste.setEnabled(false);
                        }
                        if (po.isAllowed()) {
                            NewTechnicsPart.this.createTechnics.setEnabled(true);
                        } else {
                            NewTechnicsPart.this.paste.setEnabled(false);
                            NewTechnicsPart.this.createTechnics.setEnabled(false);
                        }
                    } else if ((xo instanceof XWProductTreeObject)) {
                        NewTechnicsPart.this.setProductSelectedStatus();
                    } else if ((xo instanceof TechnicsMessageTreeObject)) {
                        Document technic = NewTechnicsPart.this.getCurrentTechnics();
                        Element element = XmlUtility.getTechnicsElement(technic);

                        String lifecycle1 = element.attributeValue("lifecycle");
                        if (lifecycle1 != null && (!"".equals(lifecycle1)) && !lifecycle1.equals("正在工作") && !lifecycle1.equals("修改中")) {
                            NewTechnicsPart.this.delete.setEnabled(false);
                        } else {
                            NewTechnicsPart.this.delete.setEnabled(true);
                        }
                        // XWTreeNode
                        // treenode=NewTechnicsPart.this.xwPartTreePanel.getSelectedTreeNode().getP();
                        // if(treenode!=null){
                        // XWTreeObject obj = treenode.getObject();
                        // if(obj instanceof XWPartTreeObject){
                        // XWPartTreeObject po = (XWPartTreeObject) obj;
                        // if(po.isAllowed()){
                        // NewTechnicsPart.this.delete.setEnabled(true);
                        // }else{
                        // NewTechnicsPart.this.delete.setEnabled(false);
                        // }
                        // }
                        //
                        // }
                        NewTechnicsPart.this.copy.setEnabled(true);
                        NewTechnicsPart.this.paste.setEnabled(false);
                        // add by machongqi 20150603 end
                        NewTechnicsPart.this.createTechnics.setEnabled(false);
                        NewTechnicsPart.this.createStep.setEnabled(false);

                        TechnicsMessageTreeObject tmto = (TechnicsMessageTreeObject) xo;
                        UploadTechnics technics = ObjectTransfer.technicsToUploadTechnics(tmto);
                        if (NewTechnicsPart.downLoadTech.contains(technics)) {
                            NewTechnicsPart.this.reviewTechnics.setEnabled(false);
                            NewTechnicsPart.this.pdfReviewTechnics.setEnabled(false);
                            NewTechnicsPart.this.technicsCoWork.setEnabled(false);
                            NewTechnicsPart.this.viewHistory.setEnabled(false);
                            NewTechnicsPart.this.technicsConfirm.setEnabled(false);
                            NewTechnicsPart.this.technicsUpload.setEnabled(false);
                            NewTechnicsPart.this.technicsUpdate.setEnabled(false);
                            NewTechnicsPart.this.signed.setEnabled(false);
                            NewTechnicsPart.this.signed2.setEnabled(false);
                            NewTechnicsPart.this.completeTask.setEnabled(false);
                            NewTechnicsPart.this.submitUniteTechnics.setEnabled(false);
                        } else {
                            NewTechnicsPart.this.reviewTechnics.setEnabled(true);
                            NewTechnicsPart.this.pdfReviewTechnics.setEnabled(true);
                            NewTechnicsPart.this.technicsCoWork.setEnabled(true);
                            NewTechnicsPart.this.viewHistory.setEnabled(true);
                            NewTechnicsPart.this.technicsConfirm.setEnabled(true);
                            NewTechnicsPart.this.technicsUpload.setEnabled(true);
                            NewTechnicsPart.this.technicsUpdate.setEnabled(true);
                            if ("3".equals(runType)) {
                                NewTechnicsPart.this.signed.setEnabled(false);
                                NewTechnicsPart.this.signed2.setEnabled(false);
                                NewTechnicsPart.this.completeTask.setEnabled(true);
                            } else {
                                NewTechnicsPart.this.signed.setEnabled(AclUtl.sign3Enable(element));
                                NewTechnicsPart.this.signed2.setEnabled(true);
                                NewTechnicsPart.this.completeTask.setEnabled(false);
                            }

                            XWTreeNode node1 = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
                            NewTechnicsPart.this.technicsConfirm.setEnabled(true);
                            NewTechnicsPart.this.technicsUpload.setEnabled(true);
                            NewTechnicsPart.this.technicsUpdate.setEnabled(true);
                            NewTechnicsPart.this.signed.setEnabled(AclUtl.sign3Enable(element));
                            NewTechnicsPart.this.signed2.setEnabled(true);
                            if (node1 != null) {
                                NewTechnicsPart.this.submitUniteTechnics.setEnabled(true);
                            }
                        }

                        XWTreeNode pNode = NewTechnicsPart.this.xwPartTreePanel.getSelectedTreeNode().getP();
                        if (pNode != null) {
                            XWTreeObject obj = pNode.getObject();
                            if (obj instanceof XWPartTreeObject) {
                                XWPartTreeObject pObj = (XWPartTreeObject) obj;
                                if (!pObj.isAllowed()) {
                                    NewTechnicsPart.this.reviewTechnics.setEnabled(false);
                                    NewTechnicsPart.this.pdfReviewTechnics.setEnabled(false);
                                    NewTechnicsPart.this.technicsCoWork.setEnabled(false);
                                    NewTechnicsPart.this.viewHistory.setEnabled(false);
                                    NewTechnicsPart.this.technicsConfirm.setEnabled(false);
                                    NewTechnicsPart.this.technicsUpload.setEnabled(false);
                                    NewTechnicsPart.this.technicsUpdate.setEnabled(false);
                                    NewTechnicsPart.this.signed.setEnabled(false);
                                    NewTechnicsPart.this.signed2.setEnabled(false);
                                    NewTechnicsPart.this.completeTask.setEnabled(false);
                                    NewTechnicsPart.this.submitUniteTechnics.setEnabled(false);
                                }
                            }
                        }
                        Document tech = NewTechnicsPart.this.getCurrentTechnics();
                        Element ele = XmlUtility.getTechnicsElement(tech);
                        String creator = ele.attributeValue("creator");
                        String lifecycle = ele.attributeValue("lifecycle");
                        if (((lifecycle != null) && (!"".equals(lifecycle)) && (!lifecycle.equals("正在工作")) && (!lifecycle.equals("修改中"))) || !creator.equals(currentUser)) {
                            NewTechnicsPart.this.reviewTechnics.setEnabled(false);
                            NewTechnicsPart.this.pdfReviewTechnics.setEnabled(false);
                            NewTechnicsPart.this.technicsCoWork.setEnabled(false);
                            NewTechnicsPart.this.viewHistory.setEnabled(false);
                            NewTechnicsPart.this.technicsConfirm.setEnabled(false);
                            NewTechnicsPart.this.technicsUpload.setEnabled(false);
                            NewTechnicsPart.this.technicsUpdate.setEnabled(false);
                            NewTechnicsPart.this.signed.setEnabled(false);
                            NewTechnicsPart.this.signed2.setEnabled(false);
                            NewTechnicsPart.this.completeTask.setEnabled(false);
                            NewTechnicsPart.this.submitUniteTechnics.setEnabled(false);
                        }

                        NewTechnicsPart.this.setTechnicsDisabledByLifecycle();
                    }
                    NewTechnicsPart.this.startPBOM.setEnabled(true);
                    if (xo instanceof ReportTechnicsTreeObject) {
                        NewTechnicsPart.this.signed.setEnabled(false);
                        if (NewTechnicsPart.reportTechnics) {
                            XWTreeNode treeNode = NewTechnicsPart.this.xwPartTreePanel.getSelectedTreeNode();
                            if (treeNode != null) {
                                Element ele = treeNode.getObject().getTreeCellData();
                                String lifecycle = ele.attributeValue("lifecycle");
                                if (lifecycle != null && (!"".equals(lifecycle)) && !lifecycle.equals("正在工作") && !lifecycle.equals("修改中")) {
                                    delete.setEnabled(false);
                                } else {
                                    delete.setEnabled(true);
                                }
                            }
                        } else {
                            delete.setEnabled(false);
                        }
                    }
                } else {
                    NewTechnicsPart.this.setNullSelectedStatus();
                }
//					NewTechnicsPart.this.copy.setEnabled(false);
//					NewTechnicsPart.this.paste.setEnabled(false);
//					NewTechnicsPart.this.delete.setEnabled(false);

            } else if (NewTechnicsPart.this.treeJTabbedPane.getSelectedIndex() == 1) {
                boolean bool = false;
                Document tech = NewTechnicsPart.this.getCurrentTechnics();
                Element ele = null;
                if (tech != null) {
                    try {
                        ele = XmlUtility.getTechnicsElement(tech);
                        String technicsNum = ele.attributeValue("technicsNumber");
                        if (NewTechnicsPart.downLoadTech.contains(technicsNum)) {
                            bool = true;
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
                if (node != null) {
                    XWTreeObject xo = node.getObject();
                    if ((xo instanceof XWTechnicsTreeObject)) {
                        if (bool) {
                            NewTechnicsPart.this.setTechnicsSelectedStatus();
                        } else {
                            NewTechnicsPart.this.setTechnicsSelectedStatus(xo);
                        }
                        if (tech != null) {
                            String lifecycle = ele.attributeValue("lifecycle");
                            String creator = ele.attributeValue("creator");
                            if (((lifecycle != null) && (!"".equals(lifecycle)) && (!lifecycle.equals("正在工作")) && (!lifecycle.equals("修改中"))) || (!creator.equals(currentUser))) {
                                NewTechnicsPart.this.technicsCoWork.setEnabled(false);
                                NewTechnicsPart.this.submitUniteTechnics.setEnabled(false);
                                NewTechnicsPart.this.technicsConfirm.setEnabled(false);
                                NewTechnicsPart.this.technicsUpload.setEnabled(false);
                                NewTechnicsPart.this.technicsUpdate.setEnabled(false);
                                NewTechnicsPart.this.technicsCoWork.setEnabled(false);

                                NewTechnicsPart.this.submitUniteTechnics.setEnabled(false);
                                NewTechnicsPart.this.technicsConfirm.setEnabled(false);
                                NewTechnicsPart.this.technicsUpload.setEnabled(false);
                                NewTechnicsPart.this.technicsUpdate.setEnabled(false);
                                NewTechnicsPart.this.signed.setEnabled(false);
                                NewTechnicsPart.this.signed2.setEnabled(false);
                                NewTechnicsPart.this.completeTask.setEnabled(false);
                                NewTechnicsPart.this.delete.setEnabled(false);
                                NewTechnicsPart.this.createStep.setEnabled(false);
                                NewTechnicsPart.this.paste.setEnabled(false);
                            } else if (lifecycle.equals("已批准")) {
                                NewTechnicsPart.this.delete.setEnabled(false);
                            } else {
                                NewTechnicsPart.this.delete.setEnabled(true);
                            }
                        } else {
                            XWTreeNode treenode = NewTechnicsPart.this.xwPartTreePanel.getSelectedTreeNode().getP();
                            if (treenode != null) {
                                XWTreeObject obj = treenode.getObject();
                                if (obj instanceof XWPartTreeObject) {
                                    XWPartTreeObject po = (XWPartTreeObject) obj;
                                    if (po.isAllowed()) {
                                        NewTechnicsPart.this.delete.setEnabled(true);
                                    } else {
                                        NewTechnicsPart.this.delete.setEnabled(false);
                                    }
                                }

                            }
                        }

                        XWTreeNode pNode = NewTechnicsPart.this.xwPartTreePanel.getSelectedTreeNode().getP();
                        if (pNode != null) {
                            XWTreeObject obj = pNode.getObject();
                            if (obj instanceof XWPartTreeObject) {
                                XWPartTreeObject pObj = (XWPartTreeObject) obj;
                                if (!pObj.isAllowed()) {
                                    NewTechnicsPart.this.reviewTechnics.setEnabled(false);
                                    NewTechnicsPart.this.pdfReviewTechnics.setEnabled(false);
                                    NewTechnicsPart.this.technicsCoWork.setEnabled(false);
                                    NewTechnicsPart.this.viewHistory.setEnabled(false);
                                    NewTechnicsPart.this.technicsConfirm.setEnabled(false);
                                    NewTechnicsPart.this.technicsUpload.setEnabled(false);
                                    NewTechnicsPart.this.technicsUpdate.setEnabled(false);
                                    NewTechnicsPart.this.signed.setEnabled(false);
                                    NewTechnicsPart.this.signed2.setEnabled(false);
                                    NewTechnicsPart.this.completeTask.setEnabled(false);
                                    NewTechnicsPart.this.submitUniteTechnics.setEnabled(false);
                                }
                            }
                        }
                        NewTechnicsPart.this.copy.setEnabled(true);
                        if (NewTechnicsPart.this.copyDatas != null && NewTechnicsPart.this.copyDatas.size() > 0) {
                            NewTechnicsPart.this.paste.setEnabled(true);
                        } else {
                            NewTechnicsPart.this.paste.setEnabled(false);
                        }
                    } else if ((xo instanceof XWStepTreeObject)) {
                        NewTechnicsPart.this.delete.setEnabled(true);
                        if (bool) {
                            NewTechnicsPart.this.setStepSelectedStatus();
                        } else {
                            NewTechnicsPart.this.setStepSelectedStatus(xo);
                        }
                        NewTechnicsPart.this.setStepSelectedStatus(xo);
                        String lifecycle = ele.attributeValue("lifecycle");
                        if ((lifecycle != null) && (!"".equals(lifecycle)) && (!lifecycle.equals("正在工作")) && (!lifecycle.equals("修改中"))) {
                            NewTechnicsPart.this.delete.setEnabled(false);
                        }
                        NewTechnicsPart.this.copy.setEnabled(true);
                        if (NewTechnicsPart.this.copyPace != null && NewTechnicsPart.this.copyPace.size() > 0) {
                            NewTechnicsPart.this.paste.setEnabled(true);
                        } else {
                            NewTechnicsPart.this.paste.setEnabled(false);
                        }
                    } else if (xo instanceof XWPaceTreeObject) {
                        NewTechnicsPart.this.copy.setEnabled(true);
                        NewTechnicsPart.this.delete.setEnabled(true);
                        NewTechnicsPart.this.paste.setEnabled(false);
                    } else if (xo instanceof XWPaceTreeObject) {
                        ele = xo.getTreeCellData();
                        XWTreeNode parent = (XWTreeNode) node.getParent().getParent();
                        XWTreeObject parentobj = parent.getObject();
                        String lifecycle = parentobj.getTreeCellData().attributeValue("lifecycle");
                        if ("已批准".equals(lifecycle)) {
                            NewTechnicsPart.this.delete.setEnabled(false);
                        } else {
                            NewTechnicsPart.this.delete.setEnabled(true);
                        }
                        NewTechnicsPart.this.copy.setEnabled(true);
                        NewTechnicsPart.this.paste.setEnabled(false);
                    } else if ((xo instanceof XWPartTreeObject)) {
                        NewTechnicsPart.this.setPartSelectedStatus();
                    } else if ((xo instanceof XWProductTreeObject)) {
                        NewTechnicsPart.this.setProductSelectedStatus();
                    } else {
                        NewTechnicsPart.this.reviewTechnics.setEnabled(false);
                        NewTechnicsPart.this.pdfReviewTechnics.setEnabled(false);
                        NewTechnicsPart.this.technicsCoWork.setEnabled(false);
                        NewTechnicsPart.this.viewHistory.setEnabled(false);
                        NewTechnicsPart.this.submitUniteTechnics.setEnabled(false);
                        NewTechnicsPart.this.technicsConfirm.setEnabled(false);
                        NewTechnicsPart.this.technicsUpload.setEnabled(false);
                        NewTechnicsPart.this.technicsUpdate.setEnabled(false);
                        NewTechnicsPart.this.startPBOM.setEnabled(false);
                        NewTechnicsPart.this.signed.setEnabled(false);
                        NewTechnicsPart.this.signed2.setEnabled(false);
                        NewTechnicsPart.this.completeTask.setEnabled(false);

                        NewTechnicsPart.this.setNullSelectedStatus();
                    }
                }
            }
        }

        @Override
        public void menuDeselected(MenuEvent e) {
        }

        @Override
        public void menuCanceled(MenuEvent e) {
        }
    }

    /**
     * @Description: 搜索工装申请卡
     */
    public void searchFrockCard() {
        FrockCardDetailDialog dialog = new FrockCardDetailDialog(null, false, this);
        dialog.showDialog();
    }

    /**
     * @Description: 上一个工序
     */
    public void previousStep() {
        JTree tree = NewTechnicsPart.this.technicsTreePanel.getTree();
        String currentStepNumber = technicsStepJPanel.technicsStepNumberField.getText();
        if (tree != null) {
            Object root = tree.getModel().getRoot();
            if (root != null) {
                logger.debug(root);
                XWTreeNode technicsNode = technicsTreePanel.getCurrentTechnicsNode();
                if (technicsNode != null) {
                    Enumeration children = technicsNode.children();
                    XWTreeNode leftNode = null;
                    while (children.hasMoreElements()) {
                        Object child = children.nextElement();
                        if (child instanceof XWTreeNode) {
                            XWTreeNode node = (XWTreeNode) child;
                            XWTreeObject obj = node.getObject();
                            if (obj instanceof XWStepTreeObject) {
                                XWStepTreeObject stepObj = (XWStepTreeObject) obj;
                                Element element = stepObj.getTreeCellData();
                                String stepNumber = element.attributeValue("stepNumber");
                                if (!stepNumber.equals(currentStepNumber)) {
                                    leftNode = node;
                                } else {
                                    if (leftNode != null) {
                                        tree.setSelectionPath(new TreePath(leftNode.getPath()));
                                        this.universalToolBar.setToolBarEnabled();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @Description: 下一个工序
     */
    public void nextStep() {
        JTree tree = NewTechnicsPart.this.technicsTreePanel.getTree();
        String currentStepNumber = technicsStepJPanel.technicsStepNumberField.getText();
        if (tree != null) {
            Object root = tree.getModel().getRoot();
            if (root != null) {
                logger.debug(root);
                XWTreeNode technicsNode = technicsTreePanel.getCurrentTechnicsNode();
                if (technicsNode != null) {
                    Enumeration children = technicsNode.children();
                    while (children.hasMoreElements()) {
                        Object child = children.nextElement();
                        if (child instanceof XWTreeNode) {
                            XWTreeNode node = (XWTreeNode) child;
                            XWTreeObject obj = node.getObject();
                            if (obj instanceof XWStepTreeObject) {
                                XWStepTreeObject stepObj = (XWStepTreeObject) obj;
                                Element element = stepObj.getTreeCellData();
                                String stepNumber = element.attributeValue("stepNumber");
                                if (stepNumber.equals(currentStepNumber)) {
                                    if (children.hasMoreElements()) {
                                        Object nextNode = children.nextElement();
                                        XWTreeNode nextStepNode = (XWTreeNode) nextNode;
                                        tree.setSelectionPath(new TreePath(nextStepNode.getPath()));
                                        this.universalToolBar.setToolBarEnabled();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 导入工艺模板
     */
    public void importTechnicsTemplate() {
        XWTreeNode node = getTechnicsTreePanel().getSelectedTreeNode();
        XWTreeObject obj = node.getObject();
        if (obj instanceof XWTechnicsTreeObject) {
            XWTechnicsTreeObject technicsObj = (XWTechnicsTreeObject) obj;
            Element techElement = technicsObj.getTreeCellData();
            try {
                String templatePath = WorkSpaceUtil.getTempletRootPath();
                String technicsType = techElement.attributeValue("technicsType");
                String s = null;// WorkSpaceUtil.ASSEMBLE_TEMPLATE;
                // if (technicsType.equals(WorkSpaceUtil.MACHINING_TYPE)) {
                // s = WorkSpaceUtil.MACHINING_TEMPLATE;
                // }else if(WorkSpaceUtil.PAINT_TYPE.equals(technicsType)){
                // s = WorkSpaceUtil.PAINT_TEMPLATE;
                // }else if(WorkSpaceUtil.MOUNT_TYPE.equals(technicsType)){
                // s = WorkSpaceUtil.MOUNT_TEMPLATE;
                // }

                for (int i = 0; i < technicsTypes[1].length; i++) {
                    if (technicsType.equals(technicsTypes[1][i])) {
                        s = technicsTypes[0][i];
                    }
                }

                System.out.println(technicsType);
                TemplateSearchDialog dialog = new TemplateSearchDialog(templatePath, s, this);
                Vector templates = dialog.showDialog();
                if (templates != null) {
                    Document document = null;
                    String templateName = null;
                    String filepath = null;
                    if (templates.size() == 1) {
                        templateName = (String) templates.get(0);
                        if (templateName.toLowerCase().endsWith(".zip")) {
                            templateName = templateName.substring(0, templateName.length() - 4);
                        }
                        String templateXMLPath = WorkSpaceUtil.getTempletRootPath() + File.separator + s + File.separator + templateName + File.separator;
                        filepath = templateXMLPath;
                        document = XmlUtil.getDocument(templateXMLPath + templateName + ".xml");
                    }
                    // PDS服务模板逻辑
                    else if (templates.size() == 2) {
                        templateName = (String) templates.get(0);
                        if (templateName.toLowerCase().endsWith(".xml")) {
                            templateName = templateName.substring(0, templateName.length() - 4);
                        }
                        System.out.println("templateName==333======" + templateName);
                        // String templateXMLPath = WorkSpaceUtil
                        // .getTempletRootPath()
                        // + File.separator
                        // + s
                        // + File.separator
                        // + templateName
                        // + File.separator;
                        byte[] data = (byte[]) templates.get(1);
                        String tempPath = WorkSpaceUtil.getTempRootPath();
                        String filePath = tempPath + "temp.zip";
                        File zipFile = new File(filePath);
                        if (!zipFile.exists()) {
                            zipFile.createNewFile();
                        }
                        FileUtil.writeBytes(filePath, data);
                        // File file = new File(templateXMLPath);
                        // if (file.exists()) {
                        // file.mkdirs();
                        // }
                        ApacheZipUtil.decompress(filePath, tempPath + templateName);
                        document = XmlUtil.getDocument(tempPath + templateName + File.separator + templateName + ".xml");
                        // filepath = templateXMLPath;
                        zipFile.delete();
                        FileUtil.deleteFile(new File(tempPath + templateName));
                    }
                    if (document != null) {
                        String technicsCategory = techElement.attributeValue("technicsCategory");
                        String technicsNumber = techElement.attributeValue("technicsNumber");
                        String technicsName = techElement.attributeValue("technicsName");
                        String technicsDirectory = "";
                        if ("rework".equals(technicsCategory)) {
                            technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
                        } else if ("temp".equals(technicsCategory)) {
                            technicsDirectory = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
                        } else {
                            technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
                        }
                        int selection = SwingUtil.showConfirmDialog("是否删除已有工序？", Constants.TIP, 2);
                        System.out.println(selection);
                        if (selection == 0 || selection == 2) {
                            Element element = techElement.element("steps");
                            if (selection == 0) {// 删除
                                List<Element> steps = element.elements();
                                if (steps != null && steps.size() != 0) {
                                    for (Element temp : steps) {
                                        element.remove(temp);
                                    }
                                }
                            }
                            Element rootElement = document.getRootElement().element("QMFawTechnicsInfo").element("steps");
                            List<Element> newSteps = rootElement.elements();
                            List list = element.elements();
                            if (newSteps != null && newSteps.size() != 0) {
                                for (Element temp : newSteps) {
                                    Element stepElement = (Element) temp.clone();
                                    XmlUtility.setAttributeValue(stepElement, "bsoID", new UID().toString());
                                    StepTemplateCopyHandler.generateStepFromTemplate(technicsDirectory, filepath, stepElement);
                                    list.add(stepElement);
                                }
                            }

                            XmlUtility.reSetStepNumbers(techElement);
                            saveProcess(techElement);
                            this.technicsTreePanel.refreshSelectNode(true);
                            this.technicsTreePanel.expandAllNode(node);
                            getQuickCreateProcedureJPanel().setTableValue(techElement);
                        }
                    } else {

                    }
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    public void setFittingToolEnable(boolean flag) {
        JPanel com = NewTechnicsPart.this.foregoingPanel;
        if (com != null) {
            if ((com instanceof TechnicsStepJPanel_XW)) {
                TechnicsStepJPanel_XW stepPanel = (TechnicsStepJPanel_XW) com;
                PaceTablePane paceTable = stepPanel.getPaceTable();
                paceTable.dynamicAssemblagePicture.setEnabled(flag);
            }
        }

    }

//	public static long getEbomOid(){
//		return ebomOid;
//	}

    /**
     * 增加工步参装件
     *
     * @param stepOid
     * @param paceOid
     * @param parts2
     * @throws Exception
     */
    public void addParticipateParts(String stepOid, String paceOid) throws Exception {
        XWTreeNode step = technicsTreePanel.getStepNode(stepOid);
        if (step != null) {
            TechnicsStepJPanel_XW stepPanel = getTechnicsStepJPanel();
            String bsoId = stepPanel.stepElement.attributeValue("bsoID");
            if (bsoId != null && bsoId.equals(stepOid)) {
                PaceTablePane paceTable = stepPanel.getPaceTable();
                paceTable.addPartValues(paceOid);
            }
        }
    }

    /**
     * 增加工序参装件
     *
     * @param stepOid
     * @param parts2
     * @throws Exception
     */
    public void addParticipateParts(String stepOid) throws Exception {
        XWTreeNode step = technicsTreePanel.getStepNode(stepOid);
        if (step != null) {
            TechnicsStepJPanel_XW stepPanel = getTechnicsStepJPanel();
            stepPanel.refreshPartDatas();
        }
    }

    /**
     * 删除PaceTablePane中工步的参装件信息
     *
     * @param stepOid
     * @param paceOid
     * @param parts
     */
    public void deleteParticipateParts(String stepOid, String paceOid, Vector<Map<String, String>> parts) {
        if (parts == null) {
            return;
        }
        XWTreeNode step = technicsTreePanel.getStepNode(stepOid);
        if (step != null) {
            TechnicsStepJPanel_XW stepPanel = getTechnicsStepJPanel();
            String bsoId = null;
            if (stepPanel.stepElement != null) {
                bsoId = stepPanel.stepElement.attributeValue("bsoID");
            }
            if (bsoId != null && bsoId.equals(stepOid)) {
                PaceTablePane paceTable = stepPanel.getPaceTable();
                paceTable.deletePartValues(paceOid, parts);
            } else {
                XWTreeObject obj = step.getObject();
                Element element = obj.getTreeCellData();
                List<Element> elements = element.element("paces").elements("QMProcedureInfo");
                if (elements != null && elements.size() != 0) {
                    for (Element paceElement : elements) {
                        String paceId = paceElement.attributeValue("bsoID");
                        if (paceId.equals(paceOid)) {
                            for (Map<String, String> temp : parts) {
                                String partNumber = temp.get("partNumber");
                                String occId = temp.get("occId");
                                Element part = paceElement.element("parts");
                                List<Element> partsElements = part.elements("QMPartInfo");
                                PbomUtil.deletePartbyOccId(partsElements, partNumber, occId);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 删除工序的参装件信息
     *
     * @param stepOid
     * @param parts
     */
    public void deleteParticipateParts(String stepOid, Vector<Map<String, String>> parts) {
        if (parts == null) {
            return;
        }
        XWTreeNode step = technicsTreePanel.getStepNode(stepOid);
        if (step != null) {
            TechnicsStepJPanel_XW stepPanel = getTechnicsStepJPanel();
            String bsoId = null;
            if (stepPanel.stepElement != null) {
                bsoId = stepPanel.stepElement.attributeValue("bsoID");
            }
            XWTreeObject obj = step.getObject();
            Element stepElement = obj.getTreeCellData();
            // String stepId = stepElement.attributeValue("bsoID");
            for (Map<String, String> temp : parts) {
                String partNumber = temp.get("partNumber");
                String occId = temp.get("occId");
                Element part = stepElement.element("parts");
                List<Element> partsElements = part.elements("QMPartInfo");
                PbomUtil.deletePartbyOccId(partsElements, partNumber, occId);
            }
            stepPanel.refreshPartDatas();
        }
    }

    @Override
    public void newActivation(String[] arg0) {
        if (arg0 != null) {
            for (String temp : arg0) {
                logger.debug(temp);
            }
        }
    }

    public Map<String, List<Map<String, String>>> getAttriMap() {
        return attriMap;
    }

    public static Map<String, List<List<String>>> getReportTechnicsAttriMap() {
        if (reportTechnicsAttriMap == null) {
            try {
                reportTechnicsAttriMap = TechnicsIntf.getReportMPMPPlanAttrByXML();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return reportTechnicsAttriMap;
    }


    /**
     * 粘贴工步
     *
     * @throws Exception
     * @author 王鑫磊、杨青
     * @校对 马崇奇
     * @date 2015-6-3
     */
    public void pastePace() throws Exception {
        XWTreeNode node = this.technicsTreePanel.getSelectedTreeNode();
        if (node != null) {
            judgeValueModified();
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWStepTreeObject)) {
                Element stepEle = xo.getTreeCellData();
                List userlist = UserUtil.getCurrentUserOid();
                String creatorOid = "";
                String responserGroup = "";
                if ((userlist != null) && (userlist.size() == 3)) {
                    String creator = (String) userlist.get(0);
                    creatorOid = (String) userlist.get(1);
                }
                Document doc = stepEle.getDocument();
                Element techElement = XmlUtility.getTechnicsElement(doc);
                String partOid = techElement.attributeValue("partOid");
                if (partOid != null) {
                    responserGroup = TechnicsIntf.getUsertechnicsGroupName(partOid);
                    if (responserGroup == null)
                        responserGroup = "";
                }
                logger.debug("设置当前工序责任人为======" + creatorOid);
                logger.debug("当前工艺零件======" + partOid + "===责任组==" + responserGroup);

                for (int i = 0; i < copyPace.size(); i++) {
                    Element temp = (Element) copyPace.get(i);
                    XmlUtility.setAttributeValue(temp, "responser", creatorOid);
                    XmlUtility.setAttributeValue(temp, "responserGroup", responserGroup);
                    Element ele = CopyUtil.copyPace((Element) temp.clone(), stepEle);
                    this.technicsStepJPanel.getPaceTable().pasteOneRowTableValue(ele);
                }
                /**粘贴工序时移除工序节点下质量记录表信息*/
                MPMParameterProcessor.removeComSpeElements(stepEle);
                XmlUtility.orderPaces(stepEle);
                saveProcess(stepEle);
                expand(node);
            } else {
                JOptionPane.showMessageDialog(this, "该操作需要针对工序进行，产品树中无选中工序节点！");
            }
        } else {
            JOptionPane.showMessageDialog(this, "该操作需要针对工序进行，产品树中无选中工序节点！");
        }
    }


    public static void loopNode(XWTreeNode root, List<XWTreeNode> targeNodes, String technumber, String techname) {
        XWTreeNode node = null;
        String number = "";
        String name = "";
        Enumeration childrens = root.children();
        while (childrens.hasMoreElements()) {
            node = (XWTreeNode) childrens.nextElement();
            if (node.getObject() instanceof TechnicsMessageTreeObject) {
                TechnicsMessageTreeObject treeObject = (TechnicsMessageTreeObject) node.getObject();
                number = treeObject.getTechNumber();
                name = treeObject.getTechName() + "(" + treeObject.getPplanNumber() + ")";
                if (technumber.equals(number) && techname.equals(name)) {
                    targeNodes.add(node);
                    return;
                }
            }
            loopNode(node, targeNodes, technumber, techname);
        }
    }

    public void sysTechnicNodeVesion(XWTreeNode node, String partNumber, String technicsNumber) {
        String technumber = "";
        String techname = "";
        String number = "";
        String name = "";
        String nas = null;
        // part树
        XWTreeNode childNode = NewTechnicsPart.this.xwPartTreePanel.getSelectedTreeNode();
        XWTreeNode targeNode = null;
        List<XWTreeNode> targeNodes = new ArrayList<XWTreeNode>();
        if (node.getObject() instanceof XWTechnicsTreeObject) {
            XWTechnicsTreeObject technicsObject = (XWTechnicsTreeObject) node.getObject();
            technumber = XmlUtility.getAttributeValue(technicsObject.getTreeCellData(), "technicsNumber");
            techname = XmlUtility.getAttributeValue(technicsObject.getTreeCellData(), "technicsName");
        }
        if (childNode.getObject() instanceof TechnicsMessageTreeObject) {
            TechnicsMessageTreeObject treeObject = (TechnicsMessageTreeObject) childNode.getObject();
            number = treeObject.getTechNumber();
            name = treeObject.getTechName() + "(" + treeObject.getPplanNumber() + ")";

        }
        if (!technumber.equals(number) || !techname.equals(name)) {
            XWTreeNode rootNode = NewTechnicsPart.this.xwPartTreePanel.cmNode;
            loopNode(rootNode, targeNodes, technumber, techname);
        }

        String tepath = WorkSpaceUtil.getTechnicsPath(technicsNumber);

        if (targeNodes.size() > 0) {
            targeNode = targeNodes.get(0);
        } else {
            if (childNode.getObject() instanceof TechnicsMessageTreeObject) {
                targeNode = childNode;
            }

        }
        if (targeNode != null) {
            XWTreeNode partNode = targeNode.getP();
            int loca = partNode.getIndex(targeNode);

            partNode.remove(targeNode);
            Document doucment = XmlUtil.getDocument(tepath);
            if (doucment != null) {
                Element technicsElement = doucment.getRootElement().element("QMFawTechnicsInfo");
                if (technicsElement != null) {
                    String ctype = technicsElement.attributeValue("technicsType");
                    String ctechnicsCategory = technicsElement.attributeValue("technicsCategory");
                    String ctechnicsVersion = technicsElement.attributeValue("version");
                    String cbatch = technicsElement.attributeValue("PCNO");
                    String cpplanNumber = technicsElement.attributeValue("pplanNumber");
                    String cisZhuZhi = technicsElement.attributeValue("ZFFLAG");
                    String cPPLANTYPE = technicsElement.attributeValue("PPLANTYPE");

                    TechnicsMessageTreeObject partObject = new TechnicsMessageTreeObject(partNumber, technicsNumber, technicsElement.attributeValue("pplanName"), ctype, ctechnicsVersion,
                            ctechnicsCategory, cbatch, cpplanNumber, cisZhuZhi, cPPLANTYPE);
                    XWTreeNode newNode = new XWTreeNode(partObject);
                    partNode.insert(newNode, loca);
                    newNode.setParent(partNode);
                    //partNode.add(newNode);
                    TreePath newpath = new TreePath(newNode.getPath());

                    NewTechnicsPart.this.xwPartTreePanel.getTree().scrollPathToVisible(newpath);
                    NewTechnicsPart.this.xwPartTreePanel.getTree().setSelectionPath(newpath);
                    NewTechnicsPart.this.xwPartTreePanel.getTree().updateUI();

                }
            }
        }
    }

    public NewTechnicsMasterJPanel_XW getNewTechnicsMasterJPanel_XW() {
        return technicsMasterJPanel;
    }

    public void createSOPTechnics() throws Exception {
        XWTreeNode node = this.xwPartTreePanel.getSelectedTreeNode();
        if (node != null) {
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWPartTreeObject)) {
                Element ele = xo.getTreeCellData();
                this.tecnicsJTabbedPane.setSelectedIndex(0);
                NewSOPTechnicsSettingJDialog dialog = new NewSOPTechnicsSettingJDialog(this, xo.getTreeCellData(), node, "SOPDoc");
                Document doc = dialog.getTechDocument();
                if (doc == null) {
                    return;
                }
                Element technicsElement = doc.getRootElement().element("QMFawTechnicsInfo");

                String fileCode = XmlUtility.getAttributeValue(technicsElement, "fileCode");
                String technicsNumber = XmlUtility.getAttributeValue(technicsElement, "technicsNumber");
                String technicsType = XmlUtility.getAttributeValue(technicsElement, "technicsType");

                Map<XWTreeNode, Document> connectObjects = dialog.getConnectObjects();
                if (connectObjects != null) {
                    Set<Entry<XWTreeNode, Document>> set = connectObjects.entrySet();
                    for (Entry<XWTreeNode, Document> entry : set) {
                        this.xwPartTreePanel.addTechnicsNode(technicsNumber, technicsType, entry.getKey());
                        loadTechnics(entry.getValue());
                    }
                }
                this.xwPartTreePanel.addTechnicsNode(technicsNumber, technicsType, node);
                loadTechnics(doc);
                // if (connectObjects == null) {
                this.treeJTabbedPane.setSelectedIndex(1);
                // } else {
                // this.treeJTabbedPane.setSelectedIndex(0);
                // }
                showData(this.technicsTreePanel.getSelectedTreeNode(), null);
                tecnicsJTabbedPane.setSelectedIndex(1);
                repaint();
            }
        }
    }

	/** 
	  * @Description: 外协工艺一键受控
	  * @date 2025年10月29日上午10:14:33
	  * @author Liluwen  
	  * @return 
	*/
	public void commitControlExternalProcess() {
		if (!isWorking()) {
            JOptionPane.showMessageDialog(NewTechnicsPart.this, "只有正在工作状态下才能一键受控！", "提示", 1);
            return;
        }
		XWTreeNode node = NewTechnicsPart.this.technicsTreePanel.getSelectedTreeNode();
		Element element=null;
		if (node != null) {
			XWTreeObject xo = node.getObject();
			if (xo instanceof XWTechnicsTreeObject) {
				element = xo.getTreeCellData();
				
			}
		} else {
			XWTreeNode node2 = NewTechnicsPart.this.xwPartTreePanel.getSelectedTreeNode();
			if (node2 != null) {
				XWTreeObject xo = node2.getObject();
				if (xo instanceof ReportTechnicsTreeObject) {
					element = xo.getTreeCellData();
				}
			}
		}
		
		if(element==null) {
			JOptionPane.showMessageDialog(NewTechnicsPart.this, "未找到当前工艺的工序，不能一键受控！", "提示", 1);
			return ;
		}
		String isTabular = element.attributeValue("isTabular");
		if(!ISTABULAR_OUTSOURCE.equals(isTabular)) {
			JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺文件形式为外协时才能一键受控！", "提示", 1);
			return ;
		}
//		boolean hasProcessSteps=checkHasProcessSteps(element);
//		if(!hasProcessSteps) {
//			JOptionPane.showMessageDialog(NewTechnicsPart.this, "未找到当前工艺的工序，不能一键受控！", "提示", 1);
//			return ;
//		}
		
		boolean hasProcessQuota=checkHasProcessQuota(element);
		if(!hasProcessQuota) {
			JOptionPane.showMessageDialog(NewTechnicsPart.this, "工艺规程中没有材料定额，不能一键受控！", "提示", 1);
			return ;
		}
		
		
		final VaActionProgressBar progressBar = new VaActionProgressBar(NewTechnicsPart.this, "工艺实例化", "工艺实例化处理中,请等待...", "处理中");
		String technicsNumber = element.attributeValue("technicsNumber");
		boolean executeResult=TechnicsIntf.setDocumentStatus(technicsNumber, STATUS_APPROVED);
		if(executeResult) {
			JOptionPane.showMessageDialog(NewTechnicsPart.this, "外协工艺一键受控成功！", "提示", 1);
		} else {
			JOptionPane.showMessageDialog(NewTechnicsPart.this, "外协工艺一键受控失败！", "提示", 1);
		}
		progressBar.finish();
        progressBar.setVisible(false);
		return;
	}

	/** 
	  * @Description: 检查当前工艺是否存在工序
	  * @date 2025年10月29日下午4:03:01
	  * @author Liluwen
	  * @param element
	  * @return  
	  * @return 
	*/
	private boolean checkHasProcessSteps(Element element) {
		Element steps=element.element("steps");
		if(steps!=null) {
			List list=steps.elements("QMProcedureInfo");
			if(list!=null&&list.size()>0) {
				return true;
			}
		}
		return false;
	}

	/** 
	  * @Description: 检查工艺规程中没有材料定额数据
	  * @date 2025年10月29日下午3:53:02
	  * @author Liluwen
	  * @param element
	  * @return  
	  * @return 
	*/
	private boolean checkHasProcessQuota(Element element) {
		Element CLDE=element.element("CLDE");
		List list=CLDE.elements();
		if(list==null) {
			return false;
		}
		for(int i=0;i<list.size();i++) {
			Object object=list.get(i);
			if(object instanceof Element){
				Element childElement=(Element)object;
				List grandChildElement=childElement.elements();
				if(grandChildElement!=null&&grandChildElement.size()>0) {
					return true;
				}
				
			}
		}
		return false;
	}
}
