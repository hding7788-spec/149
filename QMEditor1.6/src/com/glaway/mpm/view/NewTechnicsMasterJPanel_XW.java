package com.glaway.mpm.view;

import com.glaway.mpm.parameter.designui.BaiYuTestCheckFileTableJPanel;
import com.glaway.mpm.sjzyk.TechnicsDEForSjzykJPanel;
import com.glaway.mpm.sjzyk.TechnicsSjzykCLDEJPanel;
import com.glaway.mpm.sop.intf.SopIntf;
import com.glaway.mpm.sop.view.SopParametersJPanel;
import com.glaway.mpm.util.*;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ImageIntf;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.util.StringUtil;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.*;

public class NewTechnicsMasterJPanel_XW extends JPanel {

	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(NewTechnicsMasterJPanel_XW.class);
	private JTabbedPane technicsJTabbedPane = new JTabbedPane();
	private XWTreeNode node;
	private Element technicsElement;
	private JFrame frame;
	private JSplitPane pane = new JSplitPane();
	private JPanel basicPanel = new JPanel();
//	private JLabel label_1 = new JLabel("产品名称:");
//	private JLabel productNameJLabel = new JLabel("");
	private JLabel label_2 = new JLabel("父件编号:");
	private JLabel signNumberJLabel = new JLabel("");
	private JLabel label_3 = new JLabel("零部件名称:");
	private JLabel partNameLabel = new JLabel("");
	private JLabel label_4 = new JLabel("零部件编号:");
	private JLabel partNumberLabel = new JLabel("");
	private JLabel label19 = new JLabel("零部件EBOM版本:");
	private JLabel ebomVesionLabel = new JLabel("");
	private JLabel label20 = new JLabel("零部件PBOM版本:");
	private JLabel pbomVesionLabel = new JLabel("");
	private JLabel label_5 = new JLabel("版本:");
	private JLabel partVersionLabel = new JLabel("");
	private JLabel label_6 = new JLabel("零组件生产类型:");
	private JLabel mtypeLabel = new JLabel("");
	private JLabel label_10 = new JLabel("主制车间:");
	private JLabel backupRateLabel = new JLabel("");
	private JLabel label_11 = new JLabel("辅制车间:");
	private JLabel maxBackupCountLabel = new JLabel("");
	private JLabel batch = new JLabel("批次:");
	private JLabel batchV = new JLabel("");
	private JLabel product_index = new JLabel("工装代号:");
	private JLabel product_indexV = new JLabel("");

	private JPanel technicsPanel = new JPanel();
	private JLabel label_12 = new JLabel("工艺文件名称");
	private JTextField technicsNameJLabel = new JTextField();
	private JLabel label_13 = new JLabel("工艺文件编号");
	private JLabel technicsNumberJLabel = new JLabel();
	private JLabel label_14 = new JLabel("工艺版本");
	private JLabel technicsVersionJLabel = new JLabel();
	private JLabel label_15 = new JLabel("创建者");
	private JLabel creatorJLabel = new JLabel();
	private JLabel label_16 = new JLabel("修改者");
	private JLabel menderJLabel = new JLabel();
	private JLabel label_17 = new JLabel("修改时间");
	private JLabel creatTimeJLabel = new JLabel();
	private JLabel label_18 = new JLabel("修改时间");
	private JLabel modifyTimeJLabel = new JLabel();
	private JLabel label_19 = new JLabel("工艺状态");
	private JLabel technicsStateJLabel = new JLabel();

	private JLabel term_label = new JLabel("期限");
	private JLabel specializedType_label = new JLabel("专业类别");
	private JLabel procedureName_label = new JLabel("工序名称");
	private JLabel operationJob_label = new JLabel("操作岗位");
	private JLabel customArea_label = new JLabel("定制区域");
	private JLabel professionalCode_label = new JLabel("专业代号");
	private JLabel GONGXUJIANHAO_label = new JLabel("工序简号");
	private JLabel remark_label = new JLabel("其他说明");
	private JTextField term_value = new JTextField();
	private JComboBox term_value2 = new JComboBox();
	private JTextField specializedType_value = new JTextField();
	private JTextField procedureName_value = new JTextField();
	private JTextField professionalCode_value = new JTextField();
	private JTextField GONGXUJIANHAO_value = new JTextField();
	private JTextField remark_value = new JTextField();
	private JComboBox customArea_value = null;
	private JComboBox operationJob_value = null;

	private JLabel designerLabel = new JLabel("设计者");
	private JLabel designer = new JLabel();
	private JLabel reviewerLabel = new JLabel("审核者");
	private JLabel reviewer = new JLabel();
	private JLabel signerLabel = new JLabel("工艺会签者");
	private JLabel signer = new JLabel();

	private JLabel onBuildNumberLabel = new JLabel("令号");
	private JLabel onBuildNumber = new JLabel();

	private JLabel secretLabel = new JLabel("密级");
	private JLabel secretLevel = new JLabel();
	private JComboBox secretComboBox;
	private JLabel imageLabel = new JLabel();
	private Map<String, byte[]> imageCache = new HashMap<String, byte[]>();
	private Insets insets = new Insets(0, 0, 0, 0);
	private String[][] technicsTypes = LoadConfig.getInstance().getTechnicsType();

	private String technicsType;
	private JComboBox technicsTypeComboBox;

	JLabel mtslLabel = new JLabel("每台数量:");
	JLabel mjdeLabel = new JLabel("每件定额:");
	JLabel hzjsLabel = new JLabel("合制件数:");

	private JPanel materialPanel = new JPanel();

	private ShowMaterialsJPanel mJPanel;
	private ShowMaterialsJPanel cJPanel;
	private ShowMaterialsJPanel sopJPanel;

	private Map<String, String> ibasMap = new HashMap<String, String>();
	private List<String> ibaList = new ArrayList<String>();
	private Map<String, String> typesMap = new HashMap<String, String>();
	private Map<String, String> attrs = new HashMap<String, String>();
	private Map<String, List<List<String>>> xmlmap = new HashMap<String, List<List<String>>>();
	private Map<String, String> attrsMap = new HashMap<String, String>();
	private List<Component> list = new ArrayList<Component>();
	private List<Component> buttonList = new ArrayList<Component>();
	private int row = 31;
	private String[] pplantype_array = { "正式工艺文件", "临时工艺文件" };
	private String[] zfflag_array = { "Z", "F" };
	private String[] secret_array = { "公开", "内部" };
//	private String[] secret_array = { "公开", "内部", "秘密★10年", "机密★20年" };
	private String[] sop_secret_array = { "公开", "商密", "内部", "秘密", "机密" };
	private String[] department_array = { "1", "2", "3", "4", "5", "6", "7", "8", "项" };
	JTextField mindex_Value = new JTextField();
	JTextField pindex_value = new JTextField();
	JLabel secret_label = null;
	// JTextField keycomponent_value = new JTextField();
	JComboBox keycomponent_value = new JComboBox(new Object[] { "", "G", "Z", "N" });
	JTextField phase_code_value = new JTextField();
	JComboBox zfflag_value = new JComboBox(zfflag_array);
	final JComboBox pplantype_value = new JComboBox(pplantype_array);
	private JTextField pplanid_value = new JTextField();
	final JLabel tempno_label = new JLabel("临时工艺顺序号");
	final JTextField tempno_value = new JTextField();
	JLabel mindex_label = new JLabel("产品型号代号");
	JLabel pindex_label = null;
	JLabel partNumber_label = null;
	JLabel partName_label = null;
	JLabel keycomponent_label = null;
	JLabel phase_code_label = null;
	JLabel pplantype_label = null;
	JLabel zfflag_label = null;
	JLabel technicsTypeLabel = null;
	JLabel pplanForms_label = null;
	JLabel pplanid_label = null;
	private JLabel pcno_label = new JLabel("批次号");
	private JTextField pcno_value = new JTextField();
	private JComboBox pcno_box_value = new JComboBox();
	private JLabel dept_label = new JLabel("部门");
	private JLabel technicsEnglishName_label = new JLabel("工艺文件英文名");
	private JTextField technicsEnglishName_value = new JTextField();
	private JLabel imgVersion_label = new JLabel("图纸版本");
	private JTextField imgVersion_value = new JTextField();
	private JLabel vse_label = new JLabel("VSE");
	private JTextField vse_value = new JTextField();
	private JLabel cindex_label = new JLabel("图号");
	final JTextField cindex_value = new JTextField();
	private JLabel biaoshi_label = new JLabel("标识");
	final JTextField biaoshi_value = new JTextField();
	private JComboBox dept_value;
	JComboBox secret_value = new JComboBox();
	JTextField partNumber_value = new JTextField();
	JTextField partName_value = new JTextField();
	private static final String ID = "Rz";
	private boolean flag = false;
	private String technicsNoFlag = "";
	private Map<String, List<Component>> compMap = new HashMap<String, List<Component>>();
	// JButton okButton = new JButton("保    存");
	private Vector<String> batchs;
	private String[] pplanForms = { "非表格化", "表格化", "外协" };
	final JComboBox pplanForms_value = new JComboBox(pplanForms);

	final JLabel bzyjNum_label = new JLabel("编制依据编号");
	final JTextField bzyjNum_value = new JTextField();

	final JLabel bzyjName_label = new JLabel("编制依据名称");
	final JTextField bzyjName_value = new JTextField();

	final JLabel yyfl_label = new JLabel("原因分类");
	final JComboBox yyfl_value = new JComboBox(new Object[]{"设计","工艺","质量","器材","试验","其他"});

	final JLabel zwpt_label = new JLabel("配套本级图号");
	final JComboBox zwpt_value = new JComboBox(new Object[]{"否","是"});

	final JLabel scdyb_label = new JLabel("输出1");
	final JComboBox scdyb_value = new JComboBox(new Object[]{"是","否"});

	final JLabel scbyb_label = new JLabel("输出2");
	final JComboBox scbyb_value = new JComboBox(new Object[]{"是","否"});

	private BorrowThecnicsJPanel borrowThecnicsJPanel;
	private AdditionalTableJPanel additionalTableJPanel;
	private TechnicsStateTableJPanel technicsStateTableJPanel;
	private WaiXieTecDescirbeJPanel waiXieTecDescirbeJPanel;
	private TechnicsDescribeJPanel technicsDescribeJPanel;
	private FuZhiTechnicsJPanel fuZhiTechnicsJPanel;
//	private PeiTaoListTableJPanel peiTaoListTableJPanel;
	private SopParametersJPanel sopParametersJPanel;
	private BaiYuTestCheckFileTableJPanel baiYuTestCheckFileTableJPanel;

	List<String> operationJob_list = null;
	List<String> customArea_list = null;

	public NewTechnicsMasterJPanel_XW(JFrame parent) {
		NewTechnicsPart.startAnimFrame.setHeaderMessage("加载工艺基本信息");
		this.frame = parent;
		getPartImage();

		// 获取到所有的车间组，然后自动定位到当前用户所在的组
		List<String> allList = new ArrayList<String>();
		Map<String, String> workShop = ResourceIntf.getWorkShops();
		if (workShop != null && workShop.size() > 0) {
			Collection<String> coll = workShop.values();
			Iterator<String> it = coll.iterator();
			while (it.hasNext()) {
				String temp = (String) it.next();
				if (temp != null && temp.trim().length() > 0) {
					allList.add(temp);
				}
			}

			Collections.sort(allList);
		}
		dept_value = new JComboBox(allList.toArray());

		if (typesMap.isEmpty()) {
			try {
				typesMap = TechnicsIntf.getMPMPPlanSubTypes();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}
		}

		if (xmlmap.isEmpty()) {
			try {
				xmlmap = TechnicsIntf.getMPMPPlanTypeAttrByXML();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}
		}

		imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		imageLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1) {
					String url = ImageIntf.getCreoViewUrl(technicsElement.attributeValue("partOid"));
					CommonUtil.openURL(url);
				}
			}
		});

		setLayout(new GridBagLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			panel.add(basicPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			panel.add(imageLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0,
					0));

			add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

			basicPanel.setLayout(new GridBagLayout());
			panel.setBorder(new TitledBorder(null, "PBOM信息", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		}


		// 第1行
		// 产品名称
		/*label_1.setMaximumSize(new Dimension(130, 23));
		label_1.setMinimumSize(new Dimension(130, 23));
		label_1.setPreferredSize(new Dimension(130, 23));
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label_1, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));

		productNameJLabel.setMaximumSize(new Dimension(130, 23));
		productNameJLabel.setMinimumSize(new Dimension(130, 23));
		productNameJLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel
				.add(productNameJLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		*/
		// 父件编号
		label_2.setMaximumSize(new Dimension(130, 23));
		label_2.setMinimumSize(new Dimension(130, 23));
		label_2.setPreferredSize(new Dimension(130, 23));
		label_2.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label_2, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));

		signNumberJLabel.setMaximumSize(new Dimension(130, 23));
		signNumberJLabel.setMinimumSize(new Dimension(130, 23));
		signNumberJLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(signNumberJLabel, new GridBagConstraints(3, 0, 1, 1, 1.0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		// 第2行
		// 零部件名称
		label_3.setMaximumSize(new Dimension(130, 23));
		label_3.setMinimumSize(new Dimension(130, 23));
		label_3.setPreferredSize(new Dimension(130, 23));
		label_3.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label_3, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));

		partNameLabel.setMaximumSize(new Dimension(130, 23));
		partNameLabel.setMinimumSize(new Dimension(130, 23));
		partNameLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(partNameLabel, new GridBagConstraints(1, 1, 1, 1, 1.0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		// 零部件编号
		label_4.setMaximumSize(new Dimension(130, 23));
		label_4.setMinimumSize(new Dimension(130, 23));
		label_4.setPreferredSize(new Dimension(130, 23));
		label_4.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label_4, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));

		partNumberLabel.setMaximumSize(new Dimension(130, 23));
		partNumberLabel.setMinimumSize(new Dimension(130, 23));
		partNumberLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(partNumberLabel, new GridBagConstraints(3, 1, 1, 1, 1.0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		// 第3行
		// EBOM版本
		label19.setMaximumSize(new Dimension(130, 23));
		label19.setMinimumSize(new Dimension(130, 23));
		label19.setPreferredSize(new Dimension(130, 23));
		label19.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label19, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));

		ebomVesionLabel.setMaximumSize(new Dimension(130, 23));
		ebomVesionLabel.setMinimumSize(new Dimension(130, 23));
		ebomVesionLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(ebomVesionLabel, new GridBagConstraints(1, 2, 1, 1, 1.0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		// PBOM版本
		label20.setMaximumSize(new Dimension(130, 23));
		label20.setMinimumSize(new Dimension(130, 23));
		label20.setPreferredSize(new Dimension(130, 23));
		label20.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label20, new GridBagConstraints(2, 2, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));

		pbomVesionLabel.setMaximumSize(new Dimension(130, 23));
		pbomVesionLabel.setMinimumSize(new Dimension(130, 23));
		pbomVesionLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(pbomVesionLabel, new GridBagConstraints(3, 2, 1, 1, 1.0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		// 第4行
		// 零组件生产类型
		label_6.setMaximumSize(new Dimension(130, 23));
		label_6.setMinimumSize(new Dimension(130, 23));
		label_6.setPreferredSize(new Dimension(130, 23));
		label_6.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label_6, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));

		mtypeLabel.setMaximumSize(new Dimension(130, 23));
		mtypeLabel.setMinimumSize(new Dimension(130, 23));
		mtypeLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(mtypeLabel, new GridBagConstraints(1, 3, 1, 1, 1.0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		// 主制车间
		label_10.setMaximumSize(new Dimension(130, 23));
		label_10.setMinimumSize(new Dimension(130, 23));
		label_10.setPreferredSize(new Dimension(130, 23));
		label_10.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label_10, new GridBagConstraints(2, 3, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));

		backupRateLabel.setMaximumSize(new Dimension(130, 23));
		backupRateLabel.setMinimumSize(new Dimension(130, 23));
		backupRateLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(backupRateLabel, new GridBagConstraints(3, 3, 1, 1, 1.0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		// 第5行
		// 辅制车间
		label_11.setMaximumSize(new Dimension(130, 23));
		label_11.setMinimumSize(new Dimension(130, 23));
		label_11.setPreferredSize(new Dimension(130, 23));
		label_11.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label_11, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));

		maxBackupCountLabel.setMaximumSize(new Dimension(130, 23));
		maxBackupCountLabel.setMinimumSize(new Dimension(130, 23));
		maxBackupCountLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(maxBackupCountLabel, new GridBagConstraints(1, 4, 1, 1, 1.0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0,
				0));

		batch.setMaximumSize(new Dimension(130, 23));
		batch.setMinimumSize(new Dimension(130, 23));
		batch.setPreferredSize(new Dimension(130, 23));
		batch.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(batch, new GridBagConstraints(2, 4, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));

		batchV.setMaximumSize(new Dimension(130, 23));
		batchV.setMinimumSize(new Dimension(130, 23));
		batchV.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(batchV, new GridBagConstraints(3, 4, 1, 1, 1.0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		// 第6行
		product_index.setMaximumSize(new Dimension(130, 23));
		product_index.setMinimumSize(new Dimension(130, 23));
		product_index.setPreferredSize(new Dimension(130, 23));
		product_index.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(product_index, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5),
				0, 0));

		product_indexV.setMaximumSize(new Dimension(130, 23));
		product_indexV.setMinimumSize(new Dimension(130, 23));
		product_indexV.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(product_indexV, new GridBagConstraints(1, 0, 1, 1, 1.0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		technicsPanel.setLayout(new GridBagLayout());
		//technicsPanel.setBorder(new TitledBorder(null, "工艺信息",TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, null, null));

		// 第一行：工艺文件编号
		label_13.setPreferredSize(new Dimension(120, 23));
		label_13.setHorizontalAlignment(SwingConstants.CENTER);
		label_13.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(label_13, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		technicsNumberJLabel.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(technicsNumberJLabel, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第一行：工艺文件名称
		label_12.setPreferredSize(new Dimension(120, 23));
		label_12.setHorizontalAlignment(SwingConstants.CENTER);
		label_12.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(label_12, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		technicsNameJLabel.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(technicsNameJLabel, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第二行：工艺版本
		label_14.setPreferredSize(new Dimension(120, 23));
		label_14.setHorizontalAlignment(SwingConstants.CENTER);
		label_14.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(label_14, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		technicsVersionJLabel.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(technicsVersionJLabel, new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第二行：创建者
		label_15.setPreferredSize(new Dimension(120, 23));
		label_15.setHorizontalAlignment(SwingConstants.CENTER);
		label_15.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(label_15, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		creatorJLabel.setPreferredSize(new Dimension(100, 23));
		technicsPanel.add(creatorJLabel, new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第三行：修改时间
		label_17.setPreferredSize(new Dimension(120, 23));
		label_17.setHorizontalAlignment(SwingConstants.CENTER);
		label_17.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(label_17, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		creatTimeJLabel.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(creatTimeJLabel, new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第三行：工艺状态
		label_19.setPreferredSize(new Dimension(120, 23));
		label_19.setHorizontalAlignment(SwingConstants.CENTER);
		label_19.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(label_19, new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		technicsStateJLabel.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(technicsStateJLabel, new GridBagConstraints(1, 5, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第四行：产品型号代号
		mindex_label.setPreferredSize(new Dimension(120, 23));
		mindex_label.setHorizontalAlignment(SwingConstants.CENTER);
		mindex_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(mindex_label,new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		mindex_Value.setVisible(true);
		// mindex_Value.setEditable(false);
		mindex_Value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(mindex_Value, new GridBagConstraints(1, 6, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第四行：产品代号
		pindex_label = new JLabel("产品代号");
		pindex_label.setPreferredSize(new Dimension(120, 23));
		pindex_label.setHorizontalAlignment(SwingConstants.CENTER);
		pindex_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(pindex_label,new GridBagConstraints(0, 7, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		pindex_value.setVisible(true);
		pindex_value.setEditable(true);
		pindex_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(pindex_value, new GridBagConstraints(1, 7, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第五行：设计图样代号
		partNumber_label = new JLabel("设计图样代号");
		partNumber_label.setPreferredSize(new Dimension(120, 23));
		partNumber_label.setHorizontalAlignment(SwingConstants.CENTER);
		partNumber_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(partNumber_label, new GridBagConstraints(0, 8, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0),
				0, 0));

		partNumber_value.setVisible(true);
		partNumber_value.setEditable(false);
		partNumber_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(partNumber_value, new GridBagConstraints(1, 8, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第五行：设计图样名称
		partName_label = new JLabel("设计图样名称");
		partName_label.setPreferredSize(new Dimension(120, 23));
		partName_label.setHorizontalAlignment(SwingConstants.CENTER);
		partName_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(partName_label, new GridBagConstraints(0, 9, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0,
				0));

		partName_value.setVisible(true);
		partName_value.setEditable(false);
		partName_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(partName_value, new GridBagConstraints(1, 9, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第六行：关重件标记
		keycomponent_label = new JLabel("关重件标记");
		keycomponent_label.setPreferredSize(new Dimension(120, 23));
		keycomponent_label.setHorizontalAlignment(SwingConstants.CENTER);
		keycomponent_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(keycomponent_label, new GridBagConstraints(0, 10, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(5, 0, 5, 0), 0, 0));

		keycomponent_value.setVisible(true);
		keycomponent_value.setEditable(true);
		keycomponent_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(keycomponent_value, new GridBagConstraints(1, 10, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第六行：产品阶段标记
		phase_code_label = new JLabel("产品阶段标记");
		phase_code_label.setPreferredSize(new Dimension(120, 23));
		phase_code_label.setHorizontalAlignment(SwingConstants.CENTER);
		phase_code_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(phase_code_label, new GridBagConstraints(0, 11, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0),0, 0));

		phase_code_value.setVisible(true);
		phase_code_value.setEditable(false);
		phase_code_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(phase_code_value, new GridBagConstraints(1, 11, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第七行：工艺文件类别
		pplantype_label = new JLabel("工艺文件类别");
		pplantype_label.setPreferredSize(new Dimension(120, 23));
		pplantype_label.setHorizontalAlignment(SwingConstants.CENTER);
		pplantype_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(pplantype_label, new GridBagConstraints(0, 12, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0),
				0, 0));

		pplantype_value.setVisible(true);
		pplantype_value.setEnabled(false);
		pplantype_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(pplantype_value, new GridBagConstraints(1, 12, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第七行：临时工艺顺序号
		tempno_label.setVisible(false);
		tempno_label.setPreferredSize(new Dimension(120, 23));
		tempno_label.setHorizontalAlignment(SwingConstants.CENTER);
		tempno_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(tempno_label, new GridBagConstraints(0, 13, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0,
				0));

		tempno_value.setVisible(false);
		tempno_value.setEditable(false);
		tempno_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(tempno_value, new GridBagConstraints(1, 13, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第八行：主辅制类别
		zfflag_label = new JLabel("主辅制类别");
		zfflag_label.setPreferredSize(new Dimension(120, 23));
		zfflag_label.setHorizontalAlignment(SwingConstants.CENTER);
		zfflag_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(zfflag_label, new GridBagConstraints(0, 14, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0,
				0));

		zfflag_value.setEnabled(false);
		zfflag_value.setVisible(true);
		zfflag_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(zfflag_value, new GridBagConstraints(1, 14, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第八行：工艺类型
		technicsTypeLabel = new JLabel("工艺类型");
		technicsTypeLabel.setPreferredSize(new Dimension(120, 23));
		technicsTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		technicsTypeLabel.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(technicsTypeLabel, new GridBagConstraints(0, 15, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(5, 0, 5, 0), 0, 0));

		technicsTypeComboBox = new JComboBox(typesMap.values().toArray());
		technicsTypeComboBox.setVisible(true);
		technicsTypeComboBox.setEnabled(false);
		technicsTypeComboBox.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(technicsTypeComboBox, new GridBagConstraints(1, 15, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第九行：是否表格化
		pplanForms_label = new JLabel("工艺文件形式");
		pplanForms_label.setPreferredSize(new Dimension(120, 23));
		pplanForms_label.setHorizontalAlignment(SwingConstants.CENTER);
		pplanForms_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(pplanForms_label, new GridBagConstraints(0, 16, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0),
				0, 0));

		pplanForms_value.setVisible(true);
		// pplanForms_value.setEnabled(false);
		pplanForms_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(pplanForms_value, new GridBagConstraints(1, 16, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第九行：工艺特征编号
		pplanid_label = new JLabel("工艺特征编号");
		pplanid_label.setPreferredSize(new Dimension(120, 23));
		pplanid_label.setHorizontalAlignment(SwingConstants.CENTER);
		pplanid_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(pplanid_label, new GridBagConstraints(0, 17, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0,
				0));

		pplanid_value.setVisible(true);
		pplanid_value.setEditable(false);
		pplanid_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(pplanid_value, new GridBagConstraints(1, 17, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第十行：文件密级
		secret_label = new JLabel("文件密级");
		secret_label.setPreferredSize(new Dimension(120, 23));
		secret_label.setHorizontalAlignment(SwingConstants.CENTER);
		secret_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(secret_label, new GridBagConstraints(0, 18, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0,
				0));

		secret_value.setVisible(true);
		secret_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(secret_value, new GridBagConstraints(1, 18, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 期限
		term_label.setPreferredSize(new Dimension(120, 23));
		term_label.setHorizontalAlignment(SwingConstants.CENTER);
		term_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(term_label, new GridBagConstraints(0, 19, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
		term_value.setVisible(false);
		term_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(term_value, new GridBagConstraints(1, 19, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));
		term_value2.setVisible(false);
		term_value2.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(term_value2, new GridBagConstraints(1, 19, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));


		// 第十行：批次号
		pcno_label.setPreferredSize(new Dimension(120, 23));
		pcno_label.setHorizontalAlignment(SwingConstants.CENTER);
		pcno_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel
				.add(pcno_label, new GridBagConstraints(0, 20, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		// pcno_box_value.addItem("");
		pcno_value.setEditable(false);
		pcno_value.setVisible(true);
		pcno_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(pcno_value, new GridBagConstraints(1, 20, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第十一行：部门
		dept_label.setPreferredSize(new Dimension(120, 23));
		dept_label.setHorizontalAlignment(SwingConstants.CENTER);
		dept_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel
				.add(dept_label, new GridBagConstraints(0, 21, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		dept_value.setVisible(true);
		dept_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(dept_value, new GridBagConstraints(1, 21, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));


		// 第十二行：图号
		cindex_label.setPreferredSize(new Dimension(120, 23));
		cindex_label.setHorizontalAlignment(SwingConstants.CENTER);
		cindex_label.setVerticalAlignment(SwingConstants.TOP);


		technicsPanel.add(cindex_label, new GridBagConstraints(0, 22, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		cindex_value.setVisible(true);
		cindex_value.setEditable(true);
		cindex_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(cindex_value, new GridBagConstraints(1, 22, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));



		// 第十三行：工艺文件英文名
		technicsEnglishName_label.setPreferredSize(new Dimension(120, 23));
		technicsEnglishName_label.setHorizontalAlignment(SwingConstants.CENTER);
		technicsEnglishName_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(technicsEnglishName_label, new GridBagConstraints(0, 23, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		technicsEnglishName_value.setVisible(true);
		technicsEnglishName_value.setEditable(true);
		technicsEnglishName_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(technicsEnglishName_value,new GridBagConstraints(1, 23, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));
		// 第十四行：图纸版本
		imgVersion_label.setPreferredSize(new Dimension(120, 23));
		imgVersion_label.setHorizontalAlignment(SwingConstants.CENTER);
		imgVersion_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(imgVersion_label, new GridBagConstraints(0, 24, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0),
				0, 0));

		imgVersion_value.setVisible(true);
		imgVersion_value.setEditable(true);
		imgVersion_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(imgVersion_value, new GridBagConstraints(1, 24, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));
		// 第十五行：VSE
		vse_label.setPreferredSize(new Dimension(120, 23));
		vse_label.setHorizontalAlignment(SwingConstants.CENTER);
		vse_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(vse_label, new GridBagConstraints(0, 25, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		vse_value.setVisible(true);
		vse_value.setEditable(true);
		vse_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(vse_value, new GridBagConstraints(1, 25, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		/*
		 * sop相关属性 author chenjianhui 2019 11 19
		 */
		// 专业类别
		specializedType_label.setPreferredSize(new Dimension(130, 23));
		specializedType_label.setHorizontalAlignment(SwingConstants.CENTER);
		specializedType_label.setVerticalAlignment(SwingConstants.TOP);
		specializedType_label.setVisible(false);
		technicsPanel.add(specializedType_label, new GridBagConstraints(0, 26, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
		specializedType_value.setVisible(false);
		specializedType_value.setEditable(false);
		specializedType_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(specializedType_value, new GridBagConstraints(1, 26, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 工序名称
		procedureName_label.setPreferredSize(new Dimension(130, 23));
		procedureName_label.setHorizontalAlignment(SwingConstants.CENTER);
		procedureName_label.setVerticalAlignment(SwingConstants.TOP);
		procedureName_label.setVisible(false);
		technicsPanel.add(procedureName_label, new GridBagConstraints(0, 27, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
		procedureName_value.setVisible(false);
		procedureName_value.setEditable(false);
		procedureName_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(procedureName_value, new GridBagConstraints(1, 27, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 操作岗位
		operationJob_label.setPreferredSize(new Dimension(130, 23));
		operationJob_label.setHorizontalAlignment(SwingConstants.CENTER);
		operationJob_label.setVerticalAlignment(SwingConstants.TOP);
		operationJob_label.setVisible(false);
		technicsPanel.add(operationJob_label, new GridBagConstraints(0, 28, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
		operationJob_value = new JComboBox();
		operationJob_value.setVisible(false);
		operationJob_value.setEditable(false);
		operationJob_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(operationJob_value, new GridBagConstraints(1, 28, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 定制区域
		customArea_label.setPreferredSize(new Dimension(130, 23));
		customArea_label.setHorizontalAlignment(SwingConstants.CENTER);
		customArea_label.setVerticalAlignment(SwingConstants.TOP);
		customArea_label.setVisible(false);
		technicsPanel.add(customArea_label, new GridBagConstraints(0, 29, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(5, 0, 5, 0), 0, 0));
		customArea_value = new JComboBox();
		customArea_value.setVisible(false);
		customArea_value.setEditable(false);
		customArea_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(customArea_value, new GridBagConstraints(1, 29, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 专业代号
		professionalCode_label.setPreferredSize(new Dimension(130, 23));
		professionalCode_label.setHorizontalAlignment(SwingConstants.CENTER);
		professionalCode_label.setVerticalAlignment(SwingConstants.TOP);
		professionalCode_label.setVisible(false);
		technicsPanel.add(professionalCode_label, new GridBagConstraints(0, 30, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
		professionalCode_value.setVisible(false);
		professionalCode_value.setEditable(false);
		professionalCode_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(professionalCode_value, new GridBagConstraints(1, 30, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 第十一行：工序简号
		GONGXUJIANHAO_label.setPreferredSize(new Dimension(130, 23));
		GONGXUJIANHAO_label.setHorizontalAlignment(SwingConstants.CENTER);
		GONGXUJIANHAO_label.setVerticalAlignment(SwingConstants.TOP);
		GONGXUJIANHAO_label.setVisible(false);
		technicsPanel.add(GONGXUJIANHAO_label, new GridBagConstraints(0, 31, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
		GONGXUJIANHAO_value.setVisible(false);
		GONGXUJIANHAO_value.setEditable(false);
		GONGXUJIANHAO_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(GONGXUJIANHAO_value, new GridBagConstraints(1, 31, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		// 其他说明
		remark_label.setPreferredSize(new Dimension(130, 23));
		remark_label.setHorizontalAlignment(SwingConstants.CENTER);
		remark_label.setVerticalAlignment(SwingConstants.TOP);
		remark_label.setVisible(false);
		technicsPanel.add(remark_label, new GridBagConstraints(0, 32, 1, 1, 0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0,
				0));
		remark_value.setVisible(false);
		remark_value.setEditable(true);
		remark_value.setPreferredSize(new Dimension(200, 46));
		technicsPanel.add(remark_value, new GridBagConstraints(1, 32, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		biaoshi_label.setPreferredSize(new Dimension(120, 23));
		biaoshi_label.setHorizontalAlignment(SwingConstants.CENTER);
		biaoshi_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel
				.add(biaoshi_label, new GridBagConstraints(0, 47, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		biaoshi_value.setVisible(true);
		biaoshi_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(biaoshi_value, new GridBagConstraints(1, 47, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));


		bzyjNum_label.setPreferredSize(new Dimension(120, 23));
		bzyjNum_label.setHorizontalAlignment(SwingConstants.CENTER);
		bzyjNum_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel
				.add(bzyjNum_label, new GridBagConstraints(0, 48, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		bzyjNum_value.setVisible(true);
		bzyjNum_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(bzyjNum_value, new GridBagConstraints(1, 48, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));


		bzyjName_label.setPreferredSize(new Dimension(120, 23));
		bzyjName_label.setHorizontalAlignment(SwingConstants.CENTER);
		bzyjName_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel
				.add(bzyjName_label, new GridBagConstraints(0, 49, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		bzyjName_value.setVisible(true);
		bzyjName_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(bzyjName_value, new GridBagConstraints(1, 49, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));


		yyfl_label.setPreferredSize(new Dimension(120, 23));
		yyfl_label.setHorizontalAlignment(SwingConstants.CENTER);
		yyfl_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel
				.add(yyfl_label, new GridBagConstraints(0, 50, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		yyfl_value.setVisible(true);
		yyfl_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(yyfl_value, new GridBagConstraints(1, 50, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		zwpt_label.setPreferredSize(new Dimension(120, 23));
		zwpt_label.setHorizontalAlignment(SwingConstants.CENTER);
		zwpt_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(zwpt_label, new GridBagConstraints(0, 51, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		zwpt_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(zwpt_value, new GridBagConstraints(1, 51, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));

		scdyb_label.setPreferredSize(new Dimension(120, 23));
		scdyb_label.setHorizontalAlignment(SwingConstants.CENTER);
		scdyb_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(scdyb_label, new GridBagConstraints(0, 52, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		scdyb_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(scdyb_value, new GridBagConstraints(1, 52, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));


		scbyb_label.setPreferredSize(new Dimension(120, 23));
		scbyb_label.setHorizontalAlignment(SwingConstants.CENTER);
		scbyb_label.setVerticalAlignment(SwingConstants.TOP);
		technicsPanel.add(scbyb_label, new GridBagConstraints(0, 53, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

		scbyb_value.setPreferredSize(new Dimension(200, 23));
		technicsPanel.add(scbyb_value, new GridBagConstraints(1, 53, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));


		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		panel1.setLayout(new GridBagLayout());
		panel2.setLayout(new GridBagLayout());

		pane.add(new JScrollPane(panel1, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), JSplitPane.TOP);

		panel1.add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		pane.add(new JScrollPane(panel2, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
				JSplitPane.BOTTOM);

		panel2.add(technicsJTabbedPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
				0), 0, 0));

		panel2.add(new JPanel(), new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 0;
		c.gridheight = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 0, 0);
		gridbag.setConstraints(pane.getTopComponent(), c);
		gridbag.setConstraints(pane.getBottomComponent(), c);

		setLayout(new GridBagLayout());
		add(pane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		pane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		pane.setMinimumSize(new Dimension(100, 226));
		pane.setContinuousLayout(true);
		pane.setOneTouchExpandable(true);
		pane.setDividerSize(10);
		pane.setDividerLocation(250);

		// TODO 材料信息
		// JScrollPane jsp = new JScrollPane();
		// jsp.setViewportView(materialPanel);
		// jsp.setBorder(BorderFactory.createTitledBorder("材料信息"));
		// jsp.setPreferredSize(new Dimension(600, 200));
		// materialPanel.setLayout(new GridBagLayout());
		if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			cJPanel = new ShowMaterialsJPanel((NewTechnicsPart) frame);
			panel1.add(cJPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));

			mJPanel = new ShowMaterialsJPanel((NewTechnicsPart) frame);
			panel1.add(mJPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
		}

		sopJPanel = new ShowMaterialsJPanel((NewTechnicsPart) frame);
		panel1.add(sopJPanel, new GridBagConstraints(0, 3, 1, 1, 1.0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));

		technicsJTabbedPane.add("工艺文件信息", technicsPanel);
        if("SOP".equals(com.glaway.mpm.EditorConfig.startType)) {
            sopParametersJPanel = new SopParametersJPanel((NewTechnicsPart) frame);
            technicsJTabbedPane.add("参数项目", sopParametersJPanel);
            technicsDescribeJPanel = new TechnicsDescribeJPanel((NewTechnicsPart) frame);
            technicsJTabbedPane.add("工艺说明", technicsDescribeJPanel);
        }

		borrowThecnicsJPanel = new BorrowThecnicsJPanel(frame);
		technicsJTabbedPane.add("典型/通用工艺、标准", borrowThecnicsJPanel);

		// technicsJTabbedPane.add("工艺报表", new AdditionalTableJPanel(frame));
		additionalTableJPanel = new AdditionalTableJPanel(frame);
		technicsJTabbedPane.add("工艺附表", additionalTableJPanel);

		// technicsJTabbedPane.add("材料定额信息", new TechnicsCLDEJPanel(frame));
		// technicsJTabbedPane.add("工艺定额信息", new TechnicsDEJPanel(frame));
		technicsJTabbedPane.add("零件工艺定额信息", new TechnicsCLDEJPanel(frame));
		technicsJTabbedPane.add("装配工艺定额信息", new TechnicsDEJPanel(frame));
		// technicsJTabbedPane.add("主要材料(装配工艺定额)", new
		// TechnicsZYCLDEJPanel(frame));
		technicsJTabbedPane.add("零件工艺定额信息(设计资源库)", new TechnicsSjzykCLDEJPanel(frame));
		technicsJTabbedPane.add("装配工艺定额信息(设计资源库)", new TechnicsDEForSjzykJPanel(frame));

		technicsStateTableJPanel = new TechnicsStateTableJPanel((NewTechnicsPart) frame);
		technicsJTabbedPane.add("工艺状态表", technicsStateTableJPanel);

		if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			NewAttachJPanel attachJPanel = new NewAttachJPanel((NewTechnicsPart)frame);
			attachJPanel.setSize(200, 100);
			technicsJTabbedPane.add("附件", attachJPanel);
		}
		 if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)) {
	            technicsDescribeJPanel = new TechnicsDescribeJPanel((NewTechnicsPart) frame);
	            technicsJTabbedPane.add("工艺说明", technicsDescribeJPanel);
	        }


		waiXieTecDescirbeJPanel = new WaiXieTecDescirbeJPanel((NewTechnicsPart) frame);
		waiXieTecDescirbeJPanel.setUIEnabled(false);
		fuZhiTechnicsJPanel = new FuZhiTechnicsJPanel(frame);
//		peiTaoListTableJPanel = new PeiTaoListTableJPanel((NewTechnicsPart) frame);
		baiYuTestCheckFileTableJPanel = new BaiYuTestCheckFileTableJPanel(frame,frame);
		String startType =com.glaway.mpm.EditorConfig.startType;
		if (!"SOP".equals(startType)) {
			technicsJTabbedPane.add("外协工艺说明", waiXieTecDescirbeJPanel);
			technicsJTabbedPane.add("辅制工艺信息", fuZhiTechnicsJPanel);
//			technicsJTabbedPane.add("配套明细表", peiTaoListTableJPanel);
			technicsJTabbedPane.add("白羽检验记录表", baiYuTestCheckFileTableJPanel);
		}

		technicsJTabbedPane.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				// int index = technicsJTabbedPane.getSelectedIndex();
				JPanel tabbedPane = (JPanel) technicsJTabbedPane.getSelectedComponent();
				refreshTabPanel(tabbedPane);
			}
		});
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成加载工艺基本信息");
	}

	private void refreshTabPanel(JPanel tabbedPane) {
		// 读取最新工艺文件XML
		String techPath = WorkSpaceUtil.getTechnicsDirectory(technicsElement.attributeValue("technicsNumber"));
		String xmlFilePath = techPath + File.separator + technicsElement.attributeValue("technicsNumber") + ".xml";
		technicsElement = XmlUtility.getTechnicsElement(XmlUtility.getDocument(xmlFilePath));

		secret_value.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox source = ((JComboBox) e.getSource());
				String secreted = (String) source.getItemAt(source.getSelectedIndex());
				String techPath = "";
				if(technicsElement!=null){
					techPath =WorkSpaceUtil.getTechnicsDirectory(technicsElement.attributeValue("technicsNumber"));
					String xmlFilePath = techPath + File.separator + technicsElement.attributeValue("technicsNumber") + ".xml";
					technicsElement = XmlUtility.getTechnicsElement(XmlUtility.getDocument(xmlFilePath));
					if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
						if ("秘密".equals(secreted)) {
							term_value.setVisible(false);
							term_value.setText(null);
							term_value2.removeAllItems();
							term_value2.setVisible(true);
							String term = technicsElement.attributeValue("Term").trim();
							term_value2.addItem("");
							for (int i = 0; i < 10; i++) {
								String t = i + 1 + "";
								term_value2.addItem(t);
								if ((t).equals(term)) {
									term_value2.setSelectedItem(t);
								}
							}
						} else if ("机密".equals(secreted)) {
							term_value.setVisible(false);
							term_value.setText(null);
							term_value2.removeAllItems();
							term_value2.setVisible(true);
							String term = technicsElement.attributeValue("Term").trim();
							term_value2.addItem("");
							for (int i = 0; i < 20; i++) {
								String t = i + 1 + "";
								term_value2.addItem(t);
								if ((t).equals(term)) {
									term_value2.setSelectedItem(t);
								}
							}
						} else {
							term_value2.setVisible(false);
							term_value2.removeAllItems();
							term_value.setVisible(true);
							term_value.setText(technicsElement.attributeValue("Term"));
						}
					}
				}

			}
		});

		String name = tabbedPane.getName();
		if ("AdditionalTable".equals(name) && tabbedPane instanceof AdditionalTableJPanel) {
			AdditionalTableJPanel panel = (AdditionalTableJPanel) tabbedPane;
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			List<Element> attachElement = XmlUtility.getTechnicsAdditionTables(latestEle);
			Vector<Element> attachElements = new Vector<Element>();
			if (attachElement != null)
				for (Element ee : attachElement) {
					attachElements.add(ee);
				}
			logger.debug(attachElements.size() + " attachElements===" + attachElements.size());
			panel.setTableValues(attachElements);
		} else if ("BorrowThecnics".equals(name) && tabbedPane instanceof BorrowThecnicsJPanel) {
			BorrowThecnicsJPanel panel = (BorrowThecnicsJPanel) tabbedPane;
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			List<Element> attachElement = XmlUtility.getBorrowTechnics(latestEle);
			Vector<Element> attachElements = new Vector<Element>();
			if (attachElement != null)
				for (Element ee : attachElement) {
					attachElements.add(ee);
				}
			logger.debug(attachElements.size() + " attachElements===" + attachElements.size());
			panel.setTableValues(attachElements);
		} else if ("clde".equals(name) && tabbedPane instanceof TechnicsCLDEJPanel) {
			TechnicsCLDEJPanel cldePanel = (TechnicsCLDEJPanel) tabbedPane;
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			cldePanel.setTableValues(latestEle);
		} else if (tabbedPane instanceof TechnicsZYCLDEJPanel) {
			TechnicsZYCLDEJPanel zycldejPanel = (TechnicsZYCLDEJPanel) tabbedPane;
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			zycldejPanel.setTableValues(latestEle);
		} else if ("TechnicsStateTable".equals(name) && tabbedPane instanceof TechnicsStateTableJPanel) {
			TechnicsStateTableJPanel panel = (TechnicsStateTableJPanel) tabbedPane;
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			List<Element> attachElement = XmlUtility.getTechnicsStateTables(latestEle);
			Vector<Element> attachElements = new Vector<Element>();
			if (attachElement != null)
				for (Element ee : attachElement) {
					attachElements.add(ee);
				}
			logger.debug(attachElements.size() + " TechnicsStateTable==attachElements===" + attachElements.size());
			panel.setTableValues(attachElements);
		} else if ("TechnicsDescribe".equals(name) && tabbedPane instanceof TechnicsDescribeJPanel) {
			TechnicsDescribeJPanel panel = (TechnicsDescribeJPanel) tabbedPane;
			// edit by hding 20150615
			// String mtype = technicsElement.attributeValue("MTYPE");
			// if(!"带料委外件".equals(mtype) && !"不带料委外件".equals(mtype)) {
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			String content = XmlUtility.getTechnicsDescribe(latestEle);
			panel.setContent(content);
			// } else {
			// panel.setUIEnabled(false);
			// }
		} else if ("WaiXieTecDescirbe".equals(name) && tabbedPane instanceof WaiXieTecDescirbeJPanel) {
			WaiXieTecDescirbeJPanel panel = (WaiXieTecDescirbeJPanel) tabbedPane;
			panel.clear();
			String mtype = technicsElement.attributeValue("isTabular");
			if ("外协".equals(mtype)) {
				panel.setUIEnabled(true);
				Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
				panel.loadData(latestEle);
			} else {
				panel.setUIEnabled(false);
			}
		} else if ("gyde".equals(name) && tabbedPane instanceof TechnicsDEJPanel) {
			TechnicsDEJPanel cldePanel = (TechnicsDEJPanel) tabbedPane;
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			cldePanel.setTableValues(latestEle);
		} else if ("FuZhiTechnicsJPanel".equals(name) && tabbedPane instanceof FuZhiTechnicsJPanel) {
			FuZhiTechnicsJPanel fuZhiTechnicsJPanel = (FuZhiTechnicsJPanel) tabbedPane;
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			fuZhiTechnicsJPanel.setTableValues(latestEle);
		}/* else if ("PeiTaoListTableJPanel".equals(name) && tabbedPane instanceof PeiTaoListTableJPanel) {
			PeiTaoListTableJPanel peiTaoListTableJPanel = (PeiTaoListTableJPanel) tabbedPane;
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			peiTaoListTableJPanel.setTableValues(latestEle);
		}*/ else if ("sjzykgyde".equals(name) && tabbedPane instanceof TechnicsDEForSjzykJPanel) {
			TechnicsDEForSjzykJPanel cldePanel = (TechnicsDEForSjzykJPanel) tabbedPane;
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			cldePanel.setTableValues(latestEle);
		} else if ("sjzykclde".equals(name) && tabbedPane instanceof TechnicsSjzykCLDEJPanel) {
			TechnicsSjzykCLDEJPanel cldePanel = (TechnicsSjzykCLDEJPanel) tabbedPane;
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			cldePanel.setTableValues(latestEle);
		} else if (tabbedPane instanceof SopParametersJPanel) {
			SopParametersJPanel sopParametersJPanel = (SopParametersJPanel) tabbedPane;
			sopParametersJPanel.loadInitData(technicsElement);
		} else if (tabbedPane instanceof NewAttachJPanel) {
			NewAttachJPanel attachJPanel = (NewAttachJPanel) tabbedPane;
			attachJPanel.loadInitData(technicsElement);
		} else if ("CheckFileTable".equals(name) && tabbedPane instanceof BaiYuTestCheckFileTableJPanel) {
			BaiYuTestCheckFileTableJPanel baiYuTestCheckFileTableJPanel = (BaiYuTestCheckFileTableJPanel) tabbedPane;
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			List<Element> schemaDatas = XmlUtility.getSchemaDatas(latestEle);
			Vector<Element> schemaData = new Vector<Element>();
			if (schemaDatas != null && !schemaDatas.isEmpty()){
				for (Element schema : schemaDatas) {
					schemaData.add(schema);
				}
			}
			baiYuTestCheckFileTableJPanel.setUIValues(schemaData);
		}
	}

	private void showButton() {
		JPanel buttonPanel = new JPanel();
		list.add(buttonPanel);
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_5.anchor = GridBagConstraints.WEST;
		gridBagConstraints_5.weightx = 1.0;
		gridBagConstraints_5.gridx = 0;

		if (compMap.containsKey(technicsType)) {
			List<Component> compList = compMap.get(technicsType);
			row = (compList.size() / 4) + 11;
		}
		if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			row = 31;
		}else{
			row = 21;
		}
		gridBagConstraints_5.gridy = row;
		gridBagConstraints_5.gridwidth = 6;
		gridBagConstraints_5.insets = new Insets(20, 0, 5, 240);
		technicsPanel.add(buttonPanel, gridBagConstraints_5);

		JButton okButton = new JButton("保 存");
		buttonList.add(okButton);
		okButton.setVisible(true);
		buttonPanel.setLayout(new GridBagLayout());
		// buttonPanel.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0,
		// 0,
		// GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
		// new Insets(0, 0, 0, 0), 0, 0));
		okButton.setPreferredSize(new Dimension(70, 23));
		okButton.setMinimumSize(new Dimension(70, 23));
		okButton.setMaximumSize(new Dimension(70, 23));
		buttonPanel.add(okButton, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		// buttonPanel.add(okButton, new GridBagConstraints(0, 22, 1, 1, 1.0, 0,
		// GridBagConstraints.WEST, GridBagConstraints.NONE,
		// new Insets(0, 0, 0, 0), 0, 0));
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int flag = JOptionPane.showConfirmDialog(frame, "是否确定保存？", "确定", JOptionPane.YES_NO_OPTION);
				if (flag == JOptionPane.YES_OPTION) {
					if (frame instanceof NewTechnicsPart) {
						try {
							XWTreeObject treeObject = node.getObject();
							Element data = treeObject.getTreeCellData();
							if ("SOP标准操作规程".equals(technicsElement.attributeValue("technicsType"))) {
								NewTechnicsMasterJPanel_XW.this.setSOPElementValue(data);
							} else {
								NewTechnicsMasterJPanel_XW.this.setElementValue(data);
							}
							((NewTechnicsPart) frame).saveProcess(data);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		});
	}

	private void showIBAAttributes(final Element techElement) {
		hideIBAAttributes();
		this.technicsType = techElement.attributeValue("technicsType");

		mindex_Value.setText(techElement.attributeValue("MINDEX"));
		pindex_value.setText(techElement.attributeValue("PINDEX"));
		partNumber_value.setText(techElement.attributeValue("partNumber"));
		partName_value.setText(techElement.attributeValue("partName"));
		// keycomponent_value.setText(techElement.attributeValue("KEYCOMPONENT"));
		keycomponent_value.setSelectedItem(techElement.attributeValue("KEYCOMPONENT"));
		phase_code_value.setText(techElement.attributeValue("PHASE_CODE"));
		pplantype_value.setSelectedItem(techElement.attributeValue("PPLANTYPE"));
		cindex_value.setText(techElement.attributeValue("CINDEX"));
		biaoshi_value.setText(techElement.attributeValue("BIAOSHI"));
		/** 工艺文件英文名、图纸版本、VSE add by liangbo 20170508 start */
		technicsEnglishName_value.setText(techElement.attributeValue("technicsEnglishName"));
		imgVersion_value.setText(techElement.attributeValue("imageVersion"));
		vse_value.setText(techElement.attributeValue("VSE"));
		dept_value.setSelectedItem(techElement.attributeValue("DEPT"));


		if (!techElement.attributeValue("technicsType").contains("英文")) {
			technicsEnglishName_label.setVisible(false);
			technicsEnglishName_value.setVisible(false);
			imgVersion_label.setVisible(false);
			imgVersion_value.setVisible(false);
			vse_label.setVisible(false);
			vse_value.setVisible(false);
		} else {
			technicsEnglishName_label.setVisible(true);
			technicsEnglishName_value.setVisible(true);
			imgVersion_label.setVisible(true);
			imgVersion_value.setVisible(true);
			vse_label.setVisible(true);
			vse_value.setVisible(true);
		}
		/** end */
		if ("临时工艺文件".equals(techElement.attributeValue("PPLANTYPE"))) {
			tempno_label.setVisible(true);
			tempno_value.setVisible(true);
			tempno_value.setEditable(false);
			tempno_value.setText(techElement.attributeValue("TEMPNO"));

			bzyjNum_label.setVisible(true);
			bzyjNum_value.setVisible(true);
			bzyjNum_value.setText(techElement.attributeValue("bzyjNum"));

			bzyjName_label.setVisible(true);
			bzyjName_value.setVisible(true);
			bzyjName_value.setText(techElement.attributeValue("bzyjName"));


			yyfl_label.setVisible(true);
			yyfl_value.setVisible(true);
			yyfl_value.setSelectedItem(techElement.attributeValue("yyfl"));
		}
		zfflag_value.setSelectedItem(techElement.attributeValue("ZFFLAG"));
		technicsTypeComboBox.setSelectedItem(techElement.attributeValue("technicsType"));

		zwpt_value.setSelectedItem(StringUtil.isEmpty(techElement.attributeValue("zwptFlag")) ? "否" : techElement.attributeValue("zwptFlag"));
		scdyb_value.setSelectedItem(StringUtil.isEmpty(techElement.attributeValue("printDanYuanFlag")) ? "是" : techElement.attributeValue("printDanYuanFlag"));
		scbyb_value.setSelectedItem(StringUtil.isEmpty(techElement.attributeValue("printBaiYuFlag")) ? "是" : techElement.attributeValue("printBaiYuFlag"));

		String isTabular = techElement.attributeValue("isTabular");
		if (isTabular != null && !"".equals(isTabular)) {
			if (isTabular.equals("表格化") || isTabular.equals("非表格化") || isTabular.equals("外协")) {
				pplanForms_value.setSelectedItem(isTabular);
			} else {
				pplanForms_value.setSelectedIndex(0);
			}
		} else {
			pplanForms_value.setSelectedIndex(0);
		}

		pplanid_value.setText(techElement.attributeValue("PPLANID"));
		secret_value.setSelectedItem(techElement.attributeValue("SECRET"));
		String secret = techElement.attributeValue("SECRET");
		if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			term_value.setText(null);
			term_value2.removeAllItems();
			if ("公开".equals(secret) || "商密".equals(secret) || "内部".equals(secret)) {
				term_value.setVisible(true);
				term_value.setText(techElement.attributeValue("Term"));
			} else if ("秘密".equals(secret)) {
				term_value2.setVisible(true);
				String term = techElement.attributeValue("Term").trim();
				for (int i = 0; i < 10; i++) {
					String t = i + 1 + "";
					term_value2.addItem(t);
					if ((t).equals(term)) {
						term_value2.setSelectedItem(t);
					}
				}
			} else if ("机密".equals(secret)) {
				term_value2.setVisible(true);
				String term = techElement.attributeValue("Term").trim();
				for (int i = 0; i < 20; i++) {
					String t = i + 1 + "";
					term_value2.addItem(t);
					if ((t).equals(term)) {
						term_value2.setSelectedItem(t);
					}
				}
			}

		}

		if ("SOP标准操作规程".equals(technicsElement.attributeValue("technicsType"))) {
			specializedType_value.setText(techElement.attributeValue("SpecializedType"));
			procedureName_value.setText(techElement.attributeValue("ProceduceName"));
			professionalCode_value.setText(techElement.attributeValue("ProfessionalCode"));
			operationJob_value.setSelectedItem(techElement.attributeValue("OperationJob"));
			customArea_value.setSelectedItem(techElement.attributeValue("CustomArea"));
			GONGXUJIANHAO_value.setText(techElement.attributeValue("GONGXUJIANHAO"));
			remark_value.setText(techElement.attributeValue("SopRemark"));
			//term_value.setText(techElement.attributeValue("Term"));
			dept_value.setSelectedItem(techElement.attributeValue("ZZCJ"));
		}

		// 获取当前产品的批次号
		if (batchs == null) {
			String productName = techElement.attributeValue("productName");
			try {
				batchs = TechnicsIntf.getBatchsByProductName(productName);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (batchs == null) {
				batchs = new Vector<String>();
				batchs.add("");
			}
		}
		pcno_box_value.removeAllItems();
		for (String str : batchs) {
			pcno_box_value.addItem(str);
		}
		// pcno_box_value = new JComboBox(batchs);
		String pc = techElement.attributeValue("PCNO");
		if (pc != null && batchs.contains(pc)) {
			pcno_box_value.setSelectedItem(pc);
		} else {
			pcno_box_value.setSelectedIndex(0);
		}

		pcno_value.setText(techElement.attributeValue("PCNO"));

		// 从保存的缓存变量里面获取需要显示的界面组件
		if (compMap.containsKey(technicsType)) {
			setIBAAttributes(compMap.get(technicsType), techElement, technicsType);
			return;
		}

		row = 31;
		List<List<String>> allAttributes = xmlmap.get(technicsType);
		System.out.println("-------------technicsType-----" + technicsType);
		System.out.println("-------------allAttributes-----" + allAttributes);
		if (allAttributes == null) {
			allAttributes = new ArrayList<List<String>>();
		}

		// 获取零部件的IBA属性值
		//QMEditor-TODO:获取的参数并未用到，注解掉了，LB/20191225
		/*if (ibasMap == null || ibasMap.isEmpty()) {
			try {
				ibasMap = TechnicsIntf.getPartIBAValuesByNumber2(techElement.attributeValue("partNumber"), allAttributes);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/

		List<Component> compList = new ArrayList<Component>();
		if (allAttributes != null && !allAttributes.isEmpty()) {
			JTextField textField = null;
			JComboBox comboBox = null;
			JLabel label = null;
			int col = 0;
			for (List<String> ibaList : allAttributes) {
				if (ibaList != null && !ibaList.isEmpty()) {
					if ("TOTALAREA".equals(ibaList.get(0)) || "TLXHLJS".equals(ibaList.get(0))) {
						continue;
					}

					boolean isEditor = Boolean.valueOf(ibaList.get(6));
//					String partAttr = ibaList.get(5);
//					String partAttrValue = "";
//					if (partAttr != null && !"".equals(partAttr)) {
//						partAttrValue = ibasMap.get(partAttr);
//					}

					label = new JLabel(ibaList.get(1));
					label.setVisible(true);
					label.setName(ibaList.get(1));
					list.add(label);
					compList.add(label);
					if ("MJJS".equals(ibaList.get(0))) {
						JPanel p1 = new JPanel();
						p1.setName("p1");
						list.add(p1);
						compList.add(p1);
						p1.setVisible(true);
						FlowLayout flowLayout = new FlowLayout();
						flowLayout.setAlignment(FlowLayout.LEFT);
						p1.setLayout(flowLayout);

						p1.add(label);
						JTextField MJJS = new JTextField();
						MJJS.setName("MJJS");
						MJJS.setVisible(true);
						MJJS.setPreferredSize(new Dimension(60, 23));
						MJJS.setText(techElement.attributeValue("MJJS"));
						p1.add(MJJS);
						list.add(MJJS);
						compList.add(MJJS);
						attrsMap.put("MJJS", ibaList.get(1));
						label = new JLabel("件面积Cr2");
						label.setName("件面积Cr2");
						list.add(label);
						compList.add(label);
						label.setVisible(true);
						p1.add(label);
						JTextField TOTALAREA = new JTextField();
						TOTALAREA.setName("TOTALAREA");
						TOTALAREA.setVisible(true);
						TOTALAREA.setPreferredSize(new Dimension(120, 23));
						TOTALAREA.setText(techElement.attributeValue("TOTALAREA"));
						p1.add(TOTALAREA);
						list.add(TOTALAREA);
						compList.add(TOTALAREA);
						attrsMap.put("TOTALAREA", "件面积Cr2");
						technicsPanel.add(p1, new GridBagConstraints(0, ++row, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5,
								0, 0), 0, 0));

						col = 0;
					} else if ("MJTLXHL".equals(ibaList.get(0))) {
						JPanel p2 = new JPanel();
						p2.setName("p2");
						list.add(p2);
						compList.add(p2);
						p2.setVisible(true);
						FlowLayout flowLayout = new FlowLayout();
						flowLayout.setAlignment(FlowLayout.RIGHT);
						p2.setLayout(flowLayout);

						p2.add(label);
						JTextField YGDJKZLJS = new JTextField();
						YGDJKZLJS.setName("MJTLXHL");
						list.add(YGDJKZLJS);
						compList.add(YGDJKZLJS);
						attrsMap.put("MJTLXHL", ibaList.get(1));
						YGDJKZLJS.setVisible(true);
						YGDJKZLJS.setPreferredSize(new Dimension(60, 23));
						YGDJKZLJS.setText(techElement.attributeValue("MJTLXHL"));
						p2.add(YGDJKZLJS);

						label = new JLabel("件涂料消耗量kg");
						label.setName("件涂料消耗量kg");
						list.add(label);
						compList.add(label);
						label.setVisible(true);
						p2.add(label);
						JTextField TLXHLJS = new JTextField();
						TLXHLJS.setName("TLXHLJS");
						TLXHLJS.setVisible(true);
						TLXHLJS.setPreferredSize(new Dimension(120, 23));
						TLXHLJS.setText(techElement.attributeValue("TLXHLJS"));
						p2.add(TLXHLJS);
						list.add(TLXHLJS);
						compList.add(TLXHLJS);
						attrsMap.put("TLXHLJS", "件涂料消耗量kg");
						technicsPanel.add(p2, new GridBagConstraints(0, ++row, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 5,
								0, 0), 0, 0));

						col = 0;
					} else {
						attrsMap.put(ibaList.get(0), ibaList.get(1));
						label.setPreferredSize(new Dimension(120, 23));
						label.setHorizontalAlignment(SwingConstants.RIGHT);
						if (col == 2) {
							technicsPanel.add(label, new GridBagConstraints(col++ + 1, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
									new Insets(5, 5, 5, 5), 0, 0));
						} else {
							technicsPanel.add(label, new GridBagConstraints(col++, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
									new Insets(5, 5, 5, 5), 0, 0));
						}

						if ("string".equals(ibaList.get(4))) {
							textField = new JTextField();
							textField.setName(ibaList.get(0));
							textField.setVisible(true);
							textField.setEditable(isEditor);
							list.add(textField);
							compList.add(textField);
							textField.setText(techElement.attributeValue(ibaList.get(0)));
							textField.setPreferredSize(new Dimension(200, 23));
							if (col == 3) {
								technicsPanel.add(textField, new GridBagConstraints(col++ + 1, row, 2, 1, 1.0, 0, GridBagConstraints.WEST,
										GridBagConstraints.NONE, insets, 0, 0));
							} else {
								technicsPanel.add(textField, new GridBagConstraints(col++, row, 2, 1, 1.0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
										insets, 0, 0));
							}
						} else if ("set".equals(ibaList.get(4))) {
							String[] vls = ibaList.get(3).split("\\|");
							Vector<String> v = new Vector<String>();
							for (String string : vls) {
								v.add(string);
							}
							comboBox = new JComboBox(v);
							comboBox.setName(ibaList.get(0));
							comboBox.setVisible(true);
							list.add(comboBox);
							compList.add(comboBox);
							comboBox.setEnabled(isEditor);
							comboBox.setPreferredSize(new Dimension(200, 23));
							comboBox.setSelectedItem(techElement.attributeValue(ibaList.get(0)));
							if (col == 3) {
								technicsPanel.add(comboBox, new GridBagConstraints(col++ + 1, row++, 2, 1, 1.0, 0, GridBagConstraints.WEST,
										GridBagConstraints.NONE, insets, 0, 0));
							} else {
								technicsPanel.add(comboBox, new GridBagConstraints(col++, row++, 2, 1, 1.0, 0, GridBagConstraints.WEST,
										GridBagConstraints.NONE, insets, 0, 0));
							}
						}
					}

					if (col == 4) {
						row++;
						col = 0;
					}
				}
			}
			compMap.put(technicsType, compList);
		}
	}

	private void setIBAAttributes(List<Component> compList, Element stepElement, String typeName) {
		for (Component component : compList) {
			String name = component.getName();
			if (name == null) {
				continue;
			}
			list.add(component);
			component.setVisible(true);
			if (component instanceof JTextField) {
				((JTextField) component).setText(stepElement.attributeValue(name));
			} else if (component instanceof JComboBox) {
				((JComboBox) component).setSelectedItem(stepElement.attributeValue(name));
			} else if (component instanceof JLabel) {
				((JLabel) component).setText(name);
			}
		}
	}

	private void hideIBAAttributes() {
		for (Component component : list) {
			component.setVisible(false);
		}
		list.clear();
	}

	private void setTechnicsElementAttributeValue(Element techElement) {
		// XMLUtil.setAttributeValue(techElement,"bsoID","");
		XmlUtility.setAttributeValue(techElement, "creator", creatorJLabel.getText());
		XmlUtility.setAttributeValue(techElement, "modifyTime", creatTimeJLabel.getText());
		XmlUtility.setAttributeValue(techElement, "technicsName", technicsNameJLabel.getText());
		XmlUtility.setAttributeValue(techElement, "technicsNumber", technicsNumberJLabel.getText());
		XmlUtility.setAttributeValue(techElement, "technicsType", technicsElement.attributeValue("technicsType"));
//		XmlUtility.setAttributeValue(techElement, "productName", productNameJLabel.getText());
		XmlUtility.setAttributeValue(techElement, "parentPartNumber", signNumberJLabel.getText());
		XmlUtility.setAttributeValue(techElement, "partName", partNameLabel.getText());
		XmlUtility.setAttributeValue(techElement, "partNumber", partNumberLabel.getText());
		XmlUtility.setAttributeValue(techElement, "maxBackupCount", maxBackupCountLabel.getText());
		XmlUtility.setAttributeValue(techElement, "backupRate", backupRateLabel.getText());
		XmlUtility.setAttributeValue(techElement, "version", technicsVersionJLabel.getText());
		XmlUtility.setAttributeValue(techElement, "lifecycle", technicsStateJLabel.getText());
		XmlUtility.setAttributeValue(techElement, "designer", designer.getText());
		XmlUtility.setAttributeValue(techElement, "reviewer", reviewer.getText());
		XmlUtility.setAttributeValue(techElement, "signer", signer.getText());
		XmlUtility.setAttributeValue(techElement, "secretLevel", secretLevel.getText());

		if ("rework".equals(techElement.attributeValue("technicsCategory"))) {
			onBuildNumberLabel.setText("返工令号");
		} else {
			onBuildNumberLabel.setVisible(false);
			onBuildNumber.setVisible(false);
		}
		XmlUtility.setAttributeValue(techElement, "onBuildNumber", onBuildNumber.getText());

		XmlUtility.setAttributeValue(techElement, "e_version", ebomVesionLabel.getText());
		XmlUtility.setAttributeValue(techElement, "partVersion", pbomVesionLabel.getText());

		XmlUtility.setAttributeValue(techElement, "MTYPE", mtypeLabel.getText());

		setElementValue(techElement);
	}

	public void setUIValues(Element techElement) {
		technicsElement = techElement;
//		productNameJLabel.setText(techElement.attributeValue("productName"));

		creatorJLabel.setText(techElement.attributeValue("creatorDisplay"));
		creatTimeJLabel.setText(techElement.attributeValue("modifyTime"));
		technicsNameJLabel.setText(techElement.attributeValue("pplanName"));
		technicsNumberJLabel.setText(techElement.attributeValue("pplanNumber"));
		// technicsTypeComboBox.setSelectedItem(techElement.attributeValue("technicsType"));
		partVersionLabel.setText(techElement.attributeValue("partVersion"));
		signNumberJLabel.setText(techElement.attributeValue("parentPartNumber"));
		partNameLabel.setText(techElement.attributeValue("partName"));
		partNumberLabel.setText(techElement.attributeValue("partNumber"));
		maxBackupCountLabel.setText(techElement.attributeValue("FZCJ"));
		backupRateLabel.setText(techElement.attributeValue("ZZCJ"));
		technicsVersionJLabel.setText(techElement.attributeValue("version"));
		technicsStateJLabel.setText(techElement.attributeValue("lifecycle"));

		if ("SOP标准操作规程".equals(technicsElement.attributeValue("technicsType"))) {
			mindex_label.setVisible(false);
			mindex_Value.setVisible(false);
			pindex_label.setVisible(false);
			pindex_value.setVisible(false);
			partNumber_label.setVisible(false);
			partNumber_value.setVisible(false);
			partName_label.setVisible(false);
			partName_value.setVisible(false);
			keycomponent_label.setVisible(false);
			keycomponent_value.setVisible(false);
			phase_code_label.setVisible(false);
			phase_code_value.setVisible(false);
			pplantype_label.setVisible(false);
			pplantype_value.setVisible(false);
			zfflag_label.setVisible(false);
			zfflag_value.setVisible(false);
			technicsTypeLabel.setVisible(false);
			technicsTypeComboBox.setVisible(false);
			pplanForms_label.setVisible(false);
			pplanForms_value.setVisible(false);
			pplanid_label.setVisible(false);
			pplanid_value.setVisible(false);
			pcno_label.setVisible(false);
			pcno_value.setVisible(false);
			cindex_label.setVisible(false);
			cindex_value.setVisible(false);
			biaoshi_value.setVisible(false);

			operationJob_value.removeAllItems();
			operationJob_list = new ArrayList<String>();
			operationJob_list = SopIntf.getSopResourceByType(SopConstants.SOP_TYPE_OPERATIONJOB);
			for (String oj : operationJob_list) {
				operationJob_value.addItem(oj);
			}
			customArea_value.removeAllItems();
			customArea_list = new ArrayList<String>();
			customArea_list = SopIntf.getSopResourceByType(SopConstants.SOP_TYPE_CUSTOMAREA);
			for (String ca : customArea_list) {
				customArea_value.addItem(ca);
			}

			// 窗体赋值
			specializedType_value.setText(techElement.attributeValue("SpecializedType"));
			specializedType_label.setVisible(true);
			specializedType_value.setVisible(true);

			procedureName_value.setText(techElement.attributeValue("ProceduceName"));
			procedureName_label.setVisible(true);
			procedureName_value.setVisible(true);

			String OperationJob = techElement.attributeValue("OperationJob");
			if (operationJob_list.contains(OperationJob)) {
				operationJob_value.setSelectedItem(OperationJob);
			}
			operationJob_label.setVisible(true);
			operationJob_value.setVisible(true);

			String CustomArea = techElement.attributeValue("CustomArea");
			if (customArea_list.contains(CustomArea)) {
				customArea_value.setSelectedItem(OperationJob);
			}
			customArea_label.setVisible(true);
			customArea_value.setVisible(true);


			professionalCode_value.setText(techElement.attributeValue("ProfessionalCode"));
			professionalCode_label.setVisible(true);
			professionalCode_value.setVisible(true);

			GONGXUJIANHAO_value.setText(techElement.attributeValue("GONGXUJIANHAO"));
			GONGXUJIANHAO_label.setVisible(true);
			GONGXUJIANHAO_value.setVisible(true);

			remark_value.setText(techElement.attributeValue("SopRemark"));
			remark_label.setVisible(true);
			remark_value.setVisible(true);

			secret_value.removeAllItems();
			secret_label.setVisible(true);
			secret_value.setVisible(true);
			for (int i = 0; i < sop_secret_array.length; i++) {
				secret_value.addItem(sop_secret_array[i]);
			}

//			term_value.setText(techElement.attributeValue("Term"));
			term_label.setVisible(true);
//			term_value.setVisible(true);

		} else {
			specializedType_label.setVisible(false);
			specializedType_value.setVisible(false);

			procedureName_label.setVisible(false);
			procedureName_value.setVisible(false);

			operationJob_label.setVisible(false);
			operationJob_value.setVisible(false);

			customArea_label.setVisible(false);
			customArea_value.setVisible(false);

			professionalCode_label.setVisible(false);
			professionalCode_value.setVisible(false);

			GONGXUJIANHAO_label.setVisible(false);
			GONGXUJIANHAO_value.setVisible(false);

			remark_label.setVisible(false);
			remark_value.setVisible(false);

			secret_value.removeAllItems();
			secret_label.setVisible(true);
			secret_value.setVisible(true);
			for (int i = 0; i < secret_array.length; i++) {
				secret_value.addItem(secret_array[i]);
			}

			term_label.setVisible(false);
			term_value.setVisible(false);
			term_value2.setVisible(false);

			//加载白羽检验记录表
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			List<Element> schemaDatas = XmlUtility.getSchemaDatas(latestEle);
			Vector<Element> schemaData = new Vector<Element>();
			if (schemaDatas != null && !schemaDatas.isEmpty()){
				for (Element schema : schemaDatas) {
					schemaData.add(schema);
				}
			}
			baiYuTestCheckFileTableJPanel.setUIValues(schemaData);
		}

		// designer.setText(techElement.attributeValue("designer"));
		// reviewer.setText(techElement.attributeValue("reviewer"));
		// signer.setText(techElement.attributeValue("signer"));
		secretLevel.setText(techElement.attributeValue("secretLevel"));
		onBuildNumber.setText(techElement.attributeValue("onBuildNumber"));

		ebomVesionLabel.setText(techElement.attributeValue("e_version"));
		pbomVesionLabel.setText(techElement.attributeValue("partVersion"));

		/*
		 * materialName.setText(techElement.attributeValue("PTC_MATERIAL_NAME"));
		 * materialCode.setText(techElement.attributeValue("CMAT"));
		 * materialInvtype.setText(techElement.attributeValue("XHPHCL"));
		 * materialSpecification.setText(techElement.attributeValue("CSIZE"));
		 * materialStandards.setText(techElement.attributeValue("JSTJBZH"));
		 *
		 * CMAT_UP.setText(techElement.attributeValue("CMAT_UP"));
		 * CMAT_DOWN.setText(techElement.attributeValue("CMAT_DOWN"));
		 * PZGGBZH.setText(techElement.attributeValue("PZGGBZH"));
		 * JDDJ.setText(techElement.attributeValue("JDDJ"));
		 * CLZT.setText(techElement.attributeValue("CLZT"));
		 * ZLDJ.setText(techElement.attributeValue("ZLDJ"));
		 * CLDW.setText(techElement.attributeValue("ZQCLBZH"));
		 * ZQCLBZH.setText(techElement.attributeValue("ZQCLBZH"));
		 * ZQCLMC.setText(techElement.attributeValue("ZQCLMC"));
		 * XHPH.setText(techElement.attributeValue("XHPH"));
		 * JSTJ.setText(techElement.attributeValue("JSTJ"));
		 * JBCLMC.setText(techElement.attributeValue("JBCLMC"));
		 */

		mtypeLabel.setText(techElement.attributeValue("MTYPE"));

		/*
		 * //设计资源库应用改造新增属性 //start
		 * wzjc.setText(techElement.attributeValue("SHORTNAME"));//物资简称
		 * bzh.setText(techElement.attributeValue("STANDARDNUMBER"));//标准号
		 * gg.setText(techElement.attributeValue("STANDARD"));//规格
		 * cl.setText(techElement.attributeValue("MATERIAL"));//材料
		 * jxxndj.setText
		 * (techElement.attributeValue("MECHANICALPROPERTYORHARDNESS"
		 * ));//机械性能等级或硬度
		 * bmcl.setText(techElement.attributeValue("SURFACETREATMENT"));//表面处理
		 * rcl.setText(techElement.attributeValue("HEATTREATMENT"));//热处理
		 * cpxs.setText(techElement.attributeValue("PRODUCTFORM"));//产品型式
		 * cpdj.setText(techElement.attributeValue("PRODUCTLEVEL"));//产品等级
		 * bnxs.setText(techElement.attributeValue("PLATECSCREWFORM"));//板拧形式
		 * sfjk.setText(techElement.attributeValue("ISIMPORT"));//是否进口
		 * tssm.setText(techElement.attributeValue("SPECIALINSTRUCTION"));//特殊说明
		 * jldw.setText(techElement.attributeValue("MEASUREUNIT"));//计量单位
		 * xh.setText(techElement.attributeValue("TYPE"));//型号
		 * xhgg.setText(techElement.attributeValue("TYPESTANDARD"));//型号规格
		 * zldj.setText(techElement.attributeValue("QUALITYLEVEL"));//质量等级
		 * zgf.setText(techElement.attributeValue("TOTALSTANDARD"));//总规范
		 * xxgf.setText(techElement.attributeValue("DETAILSTANDARD"));//详细规范
		 * fzxs.setText(techElement.attributeValue("PACKAGINGFORM"));//封装形式
		 * wxcc.setText(techElement.attributeValue("OUTLINESIZE"));//外形尺寸
		 * zytj.setText(techElement.attributeValue("SPECIALCONDITION"));//专用条件
		 * fjxy.setText(techElement.attributeValue("EXTRACONDITION"));//附加协议
		 * //end
		 */

		if ("SOP".equals(com.glaway.mpm.EditorConfig.startType)) {
			sopJPanel.setValues(techElement, false, true);

		} else {
			cJPanel.setValues(techElement, false, false);
			mJPanel.setValues(techElement, true, false);
		}

		batchV.setText(techElement.attributeValue("BATCH"));
		product_indexV.setText(techElement.attributeValue("PRODUCT_INDEX"));

		// if(!technicsNoFlag.equals(techElement.attributeValue("technicsNumber")))
		// {
		// begin 以下是IBA属性显示
		showIBAAttributes(techElement);
		// end IBA属性显示结束

		showButton();

		flag = true;
		technicsNoFlag = techElement.attributeValue("technicsNumber");
		// }

		getPartImage();
		JPanel tablePanel = (JPanel) technicsJTabbedPane.getSelectedComponent();
		XWTreeNode treeNode = ((NewTechnicsPart) frame).getXWPartTreePanel().getSelectedTreeNode();
		if (treeNode != null) {
			XWTreeNode partTreeNode = treeNode.getP();
			if (partTreeNode != null) {
				XWTreeObject treeObj = partTreeNode.getObject();
				if (treeObj instanceof XWPartTreeObject) {
					XWPartTreeObject partObj = (XWPartTreeObject) treeObj;
					Element partEle = partObj.getTreeCellData();
					String mtype = partEle.attributeValue("MTYPE");
					if ("带料委外件".equals(mtype) || "不带料委外件".equals(mtype)) {
						if (technicsJTabbedPane.getComponentCount() == 7) {
							technicsJTabbedPane.add("外协工艺说明", waiXieTecDescirbeJPanel);
						}
					} else {
						if (technicsJTabbedPane.getComponentCount() == 8) {
							technicsJTabbedPane.remove(waiXieTecDescirbeJPanel);
						}
					}
				}
			}
		}
		refreshTabPanel(tablePanel);
		this.additionalTableJPanel.setTabTitle();
	}

	public void setUIEnabled(boolean flag) {
		secret_value.setEnabled(flag);
		pcno_value.setEnabled(false);
		// pcno_box_value.setEnabled(false);
		dept_value.setEnabled(flag);
		pplanForms_value.setEnabled(flag);
		technicsNameJLabel.setEditable(flag);
		mindex_Value.setEditable(flag);

		borrowThecnicsJPanel.setUIEnabled(flag);
		if (NewTechnicsPart.currentUser.equals("wcadmin") || NewTechnicsPart.currentUser.equals("Administrator")) {

			borrowThecnicsJPanel.setUIEnabled(true);
		}
		additionalTableJPanel.setUIEnabled(flag);
		technicsStateTableJPanel.setUIEnabled(flag);
		// waiXieTecDescirbeJPanel.setUIEnabled(flag);
		technicsDescribeJPanel.setUIEnabled(flag);
		fuZhiTechnicsJPanel.setUIEnabled(flag);
		if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			sopParametersJPanel.setUIEnabled(flag);
			borrowThecnicsJPanel.setUIEnabled(flag);
		}
		for (Component component : list) {
			component.setEnabled(flag);
		}

		for (Component button : buttonList) {
			button.setEnabled(flag);
		}
		baiYuTestCheckFileTableJPanel.setUIEnabled(flag);
	}

	private void setElementValue(Element techElement) {
		Element ibaElement = XmlUtility.getTechnicsIBAAttriElement(techElement);
		// XmlUtility.deleteAllChildElements(ibaElement);

		// XmlUtility.setAttributeValue(techElement, "TEMPNO",
		// tempno_value.getText());
		// XmlUtility.updateElementByKey(ibaElement, "临时工艺顺序号",
		// tempno_value.getText());
		// ibaElement.add(this.createTechnicsIBAAttriElement("TEMPNO",tempno_value.getText()));

		// XmlUtility.setAttributeValue(techElement, "ZFFLAG",
		// zfflag_value.getSelectedItem().toString());
		// XmlUtility.updateElementByKey(ibaElement, "主辅制类别",
		// zfflag_value.getSelectedItem().toString());
		// ibaElement.add(this.createTechnicsIBAAttriElement("ZFFLAG",zfflag_value.getSelectedItem().toString()));

		XmlUtility.setAttributeValue(techElement, "MINDEX", mindex_Value.getText());// 产品型号代号

		XmlUtility.setAttributeValue(techElement, "CINDEX", cindex_value.getText());// 图号，代号
		XmlUtility.setAttributeValue(techElement, "BIAOSHI", biaoshi_value.getText());// 标识
		/** 工艺文件英文名、图纸版本、VSE add by liangbo 20170508 */
		XmlUtility.setAttributeValue(techElement, "technicsEnglishName", technicsEnglishName_value.getText());// 工艺文件英文名
		XmlUtility.setAttributeValue(techElement, "imageVersion", imgVersion_value.getText());// 图纸版本
		XmlUtility.setAttributeValue(techElement, "VSE", vse_value.getText());// VSE

		XmlUtility.setAttributeValue(techElement, "PINDEX", pindex_value.getText());// 图号，代号

		XmlUtility.setAttributeValue(techElement, "pplanName", technicsNameJLabel.getText());
		String technicsName = techElement.attributeValue("technicsName");
		technicsName = technicsNameJLabel.getText() + technicsName.substring(technicsName.indexOf("("), technicsName.length());
		XmlUtility.setAttributeValue(techElement, "technicsName", technicsName);

		XmlUtility.setAttributeValue(techElement, "isTabular", pplanForms_value.getSelectedItem().toString());

		XmlUtility.setAttributeValue(techElement, "SECRET", secret_value.getSelectedItem().toString());
		XmlUtility.updateElementByKey(ibaElement, "文件密级", secret_value.getSelectedItem().toString());
		// ibaElement.add(this.createTechnicsIBAAttriElement("SECRET",secret_value.getSelectedItem().toString()));

		XmlUtility.setAttributeValue(techElement, "PCNO", pcno_value.getText());
		XmlUtility.updateElementByKey(ibaElement, "批次号", pcno_value.getText());
		// ibaElement.add(this.createTechnicsIBAAttriElement("PCNO",pcno_value.getText()));

		XmlUtility.setAttributeValue(techElement, "KEYCOMPONENT", keycomponent_value.getSelectedItem().toString());
		XmlUtility.updateElementByKey(ibaElement, "关重件标识", keycomponent_value.getSelectedItem().toString());

		XmlUtility.setAttributeValue(techElement, "DEPT", dept_value.getSelectedItem().toString());
		XmlUtility.updateElementByKey(ibaElement, "部门", dept_value.getSelectedItem().toString());

		XmlUtility.setAttributeValue(techElement, "bzyjNum", bzyjNum_value.getText());
		XmlUtility.setAttributeValue(techElement, "bzyjName", bzyjName_value.getText());

		if(yyfl_value.getSelectedItem()!=null){
			XmlUtility.setAttributeValue(techElement, "yyfl", yyfl_value.getSelectedItem().toString());
		}

		XmlUtility.setAttributeValue(techElement, "zwptFlag", zwpt_value.getSelectedItem().toString());
		XmlUtility.setAttributeValue(techElement, "printDanYuanFlag", scdyb_value.getSelectedItem().toString());
		XmlUtility.setAttributeValue(techElement, "printBaiYuFlag", scbyb_value.getSelectedItem().toString());

		String processTaskItemOid = techElement.attributeValue("processTaskItemOid");
		if (processTaskItemOid == null || "".equals(processTaskItemOid)) {
			XmlUtility.setAttributeValue(techElement, "processTaskItemOid", ((NewTechnicsPart) this.frame).workItemOid);// 纪录工艺任务活动的OID
		}

		String setVal = "";
		String areaKey = "";
		String areaValue = "";
		String tlxhlKey = "";
		String tlxhlValue = "";
		for (Component component : list) {
			if ("null".equals(component.getName()) || component.getName() == null) {
				continue;
			}
			System.out.println("--------component----" + component.getName());
			if (component instanceof JTextField) {
				setVal = ((JTextField) component).getText();
			} else if (component instanceof JComboBox) {
				setVal = ((JComboBox) component).getSelectedItem().toString();
			}
			XmlUtility.setAttributeValue(techElement, component.getName(), setVal);

			if ("MJJS".equals(component.getName())) {
				areaKey = attrsMap.get(component.getName()) + "(" + setVal + ")";
				continue;
			} else if ("TOTALAREA".equals(component.getName())) {
				areaKey = areaKey + attrsMap.get(component.getName());
				areaValue = setVal;
				continue;
			} else if ("MJTLXHL".equals(component.getName())) {
				tlxhlKey = attrsMap.get(component.getName()) + "(" + setVal + ")";
				continue;
			} else if ("TLXHLJS".equals(component.getName())) {
				tlxhlKey = tlxhlKey + attrsMap.get(component.getName());
				tlxhlValue = setVal;
				continue;
			}
			System.out.println("--------ibaElement----" + ibaElement);
			System.out.println("--------setVal----" + setVal);
			XmlUtility.updateElementByKey(ibaElement, attrsMap.get(component.getName()), setVal);
			// ibaElement.add(this.createTechnicsIBAAttriElement(attrsMap.get(component.getName()),setVal));
		}
		if (areaKey != null && !"".equals(areaKey)) {
			XmlUtility.updateElementByKey(ibaElement, areaKey, areaValue);
			XmlUtility.updateElementByKey(ibaElement, tlxhlKey, tlxhlValue);
		}
	}

	private void setSOPElementValue(Element techElement) {
		Element ibaElement = XmlUtility.getTechnicsIBAAttriElement(techElement);

		String secret = secret_value.getSelectedItem().toString();
		if("秘密".equals(secret) || "机密".equals(secret)){
			if(term_value2.getSelectedItem().toString()==null || "".equals(term_value2.getSelectedItem().toString())){
				JOptionPane.showMessageDialog(frame, "SOP文件期限不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
				return;
			}else{
				XmlUtility.setAttributeValue(techElement, "SECRET", secret_value.getSelectedItem().toString());
				XmlUtility.setAttributeValue(techElement, "secretLevel", secret_value.getSelectedItem().toString());
				XmlUtility.updateElementByKey(ibaElement, "密级", secret_value.getSelectedItem().toString());

				XmlUtility.setAttributeValue(techElement, "Term", term_value2.getSelectedItem().toString());
				XmlUtility.updateElementByKey(ibaElement, "期限", term_value2.getSelectedItem().toString());
			}
		}else{
			XmlUtility.setAttributeValue(techElement, "SECRET", secret_value.getSelectedItem().toString());
			XmlUtility.setAttributeValue(techElement, "secretLevel", secret_value.getSelectedItem().toString());
			XmlUtility.updateElementByKey(ibaElement, "密级", secret_value.getSelectedItem().toString());

			XmlUtility.setAttributeValue(techElement, "Term", term_value.getText());
			XmlUtility.updateElementByKey(ibaElement, "期限", term_value.getText());
		}

		XmlUtility.setAttributeValue(techElement, "OperationJob", operationJob_value.getSelectedItem().toString());
		XmlUtility.updateElementByKey(ibaElement, "操作岗位", operationJob_value.getSelectedItem().toString());

		XmlUtility.setAttributeValue(techElement, "CustomArea", customArea_value.getSelectedItem().toString());
		XmlUtility.updateElementByKey(ibaElement, "定制区域", customArea_value.getSelectedItem().toString());

		XmlUtility.setAttributeValue(techElement, "SopRemark", remark_value.getText());
		XmlUtility.updateElementByKey(ibaElement, "备注", remark_value.getText());

		XmlUtility.setAttributeValue(techElement, "department", dept_value.getSelectedItem().toString());
		XmlUtility.setAttributeValue(techElement, "ZZCJ", dept_value.getSelectedItem().toString());

		String processTaskItemOid = techElement.attributeValue("processTaskItemOid");
		if (processTaskItemOid == null || "".equals(processTaskItemOid)) {
			XmlUtility.setAttributeValue(techElement, "processTaskItemOid", ((NewTechnicsPart) this.frame).workItemOid);// 纪录工艺任务活动的OID
		}

		String setVal = "";
		String areaKey = "";
		String areaValue = "";
		String tlxhlKey = "";
		String tlxhlValue = "";
		for (Component component : list) {
			if ("null".equals(component.getName()) || component.getName() == null) {
				continue;
			}
			System.out.println("--------component----" + component.getName());
			if (component instanceof JTextField) {
				setVal = ((JTextField) component).getText();
			} else if (component instanceof JComboBox) {
				setVal = ((JComboBox) component).getSelectedItem().toString();
			}
			XmlUtility.setAttributeValue(techElement, component.getName(), setVal);

			if ("MJJS".equals(component.getName())) {
				areaKey = attrsMap.get(component.getName()) + "(" + setVal + ")";
				continue;
			} else if ("TOTALAREA".equals(component.getName())) {
				areaKey = areaKey + attrsMap.get(component.getName());
				areaValue = setVal;
				continue;
			} else if ("MJTLXHL".equals(component.getName())) {
				tlxhlKey = attrsMap.get(component.getName()) + "(" + setVal + ")";
				continue;
			} else if ("TLXHLJS".equals(component.getName())) {
				tlxhlKey = tlxhlKey + attrsMap.get(component.getName());
				tlxhlValue = setVal;
				continue;
			}
			System.out.println("--------ibaElement----" + ibaElement);
			System.out.println("--------setVal----" + setVal);
			XmlUtility.updateElementByKey(ibaElement, attrsMap.get(component.getName()), setVal);
			// ibaElement.add(this.createTechnicsIBAAttriElement(attrsMap.get(component.getName()),setVal));
		}
		if (areaKey != null && !"".equals(areaKey)) {
			XmlUtility.updateElementByKey(ibaElement, areaKey, areaValue);
			XmlUtility.updateElementByKey(ibaElement, tlxhlKey, tlxhlValue);
		}
	}

	private Element createTechnicsIBAAttriElement(String key, String value) {
		Element element = DocumentHelper.createElement("attribute");
		XmlUtility.setAttributeValue(element, "key", key);
		XmlUtility.setAttributeValue(element, "value", value);

		return element;
	}

	public Element getElement() {
		if (technicsElement != null) {
			Element ele = (Element) technicsElement.clone();
			List list = ele.element(XmlUtility.STEP_TAG).elements();
			if (list != null && list.size() > 0)
				list.clear();
			return ele;
		}
		Element techElement = XmlUtility.createTechnics();
		setTechnicsElementAttributeValue(techElement);
		return techElement;
	}

	public void setBorrowTechnicsTableValue(Element stepElement) {
		// 典型/通用工艺、标准 add by liangbo
		List<Element> borrowThecnicsElement = XmlUtility.getBorrowTechnics(stepElement);
		Vector<Element> borrowThecnicsElements = new Vector<Element>();
		if (borrowThecnicsElement != null)
			for (Element ee : borrowThecnicsElement) {
				borrowThecnicsElements.add(ee);
			}
		borrowThecnicsJPanel.setTableValues(borrowThecnicsElements);
	}

	public void clearUI() {
		node = null;
		technicsElement = null;
//		productNameJLabel.setText("");
		signNumberJLabel.setText("");
		partNameLabel.setText("");
		partNumberLabel.setText("");
		technicsNumberJLabel.setText("");
		technicsNameJLabel.setText("");
		technicsStateJLabel.setText("");

		designer.setText("");
		reviewer.setText("");
		signer.setText("");
		secretLevel.setText("");
		onBuildNumber.setText("");

		partVersionLabel.setText("");
		mtypeLabel.setText("");
		backupRateLabel.setText("");
		maxBackupCountLabel.setText("");
		ebomVesionLabel.setText("");
		pbomVesionLabel.setText("");

		technicsVersionJLabel.setText("");
		creatorJLabel.setText("");
		menderJLabel.setText("");
		creatTimeJLabel.setText("");
		modifyTimeJLabel.setText("");
		imageLabel.setIcon(new ImageIcon());

		/*
		 * materialName.setText(""); materialCode.setText("");
		 * materialInvtype.setText(""); materialSpecification.setText("");
		 * materialStandards.setText(""); CMAT_UP.setText("");
		 * CMAT_DOWN.setText(""); PZGGBZH.setText(""); JDDJ.setText("");
		 * CLZT.setText(""); ZLDJ.setText(""); CLDW.setText("");
		 * ZQCLBZH.setText(""); ZQCLMC.setText(""); XHPH.setText("");
		 * JSTJ.setText(""); JBCLMC.setText("");
		 */

		mindex_Value.setText("");
		pindex_value.setText("");
		partNumber_value.setText("");
		partName_value.setText("");
		// keycomponent_value.setText("");
		keycomponent_value.setSelectedIndex(0);
		phase_code_value.setText("");
		pplantype_value.setSelectedIndex(0);
		tempno_value.setText("");
		zfflag_value.setSelectedIndex(0);
		technicsTypeComboBox.setSelectedIndex(0);
		pplanForms_value.setSelectedIndex(0);
		pplanid_value.setText("");
		if(secret_value.getItemCount()!=0){
			secret_value.setSelectedIndex(0);
		}
		pcno_value.setText("");
		// pcno_box_value.setSelectedIndex(0);
		dept_value.setSelectedItem("");

		for (Component com : list) {
			if (com instanceof JTextField) {
				((JTextField) com).setText("");
			} else if (com instanceof JComboBox) {
				((JComboBox) com).setSelectedIndex(0);
			}
		}
	}

	public void setNode(XWTreeNode node) {
		this.node = node;
	}

	public XWTreeNode getNode() {
		return node;
	}

	public void setCreatTimeJLabel(String s) {
		creatTimeJLabel.setText(s);
	}

	public void getPartImage() {
		if (technicsElement == null) {
			return;
		}
		final String oid = technicsElement.attributeValue("partOid");
		byte[] bytes = imageCache.get(oid);
		if (bytes == null) {
			bytes = SwingUtil.getIcon(oid);
			imageCache.put(oid, bytes);
		}

		if (bytes != null) {
			imageLabel.setIcon(new ImageIcon(bytes));
		} else {
			imageLabel.setIcon(new ImageIcon());
		}
	}

	public void initIBAList() {
		ibaList.add("PHASE_CODE");
		ibaList.add("MINDEX");
		ibaList.add("PINDEX");
		ibaList.add("KEYCOMPONENT");
	}

	public JTabbedPane getTabbedPane() {
		return technicsJTabbedPane;
	}

	public void firstSetTitle(Element data) {
		int row = 0;
		if (data != null) {
			Element e = data.element("additiontables");
			if (e != null) {
				List<Element> s = e.elements("additionaltable");
				row = s.size();
			}
		}
		this.additionalTableJPanel.firstSetTitle(row);
	}

}