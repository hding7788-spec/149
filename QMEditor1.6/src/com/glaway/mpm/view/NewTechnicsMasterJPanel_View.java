package com.glaway.mpm.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.dom4j.Element;

import com.glaway.mpm.sjzyk.TechnicsDEForSjzykJPanel;
import com.glaway.mpm.sjzyk.TechnicsSjzykCLDEJPanel;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class NewTechnicsMasterJPanel_View extends JPanel {

	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(NewTechnicsMasterJPanel_View.class);
	private XWTreeNode node;
	private Element technicsElement;

	private JSplitPane pane = new JSplitPane();
	private JPanel basicPanel = new JPanel();
	private JLabel label_1 = new JLabel("产品名称:");
	private JLabel productNameJLabel = new JLabel("");

	private JLabel label_2 = new JLabel("父件编号:");
	private JLabel signNumberJLabel = new JLabel("");

	private JLabel label_3 = new JLabel("零部件名称:");
	private JLabel partNameLabel = new JLabel("");

	private JLabel label_4 = new JLabel("零部件编号:");
	private JLabel partNumberLabel = new JLabel("");

	private JLabel label_22 = new JLabel("零部件版本:");
	private JLabel numberVersionLabel = new JLabel("");

	private JLabel label19 = new JLabel("零部件EBOM版本:");
	private JLabel ebomVesionLabel = new JLabel("");

	private JLabel label20 = new JLabel("零部件PBOM版本");
	private JLabel pbomVesionLabel = new JLabel("");
	private JLabel label_5 = new JLabel("零部件版本:");
	private JLabel partVersionLabel = new JLabel("");
	private JLabel label_6 = new JLabel("零组件生产类型:");
	private JLabel mtypeLabel = new JLabel("");
	private JLabel label_8 = new JLabel("主制单位:");
	private JLabel mainWorkShopLabel = new JLabel("");
	private JLabel label_9 = new JLabel("零部件分类:");
	private JLabel materialTypeLabel = new JLabel("");
	private JLabel label_10 = new JLabel("主制车间:");
	private JLabel backupRateLabel = new JLabel("");

	private JLabel label_11 = new JLabel("辅制车间:");
	private JLabel maxBackupCountLabel = new JLabel("");
	private JPanel technicsPanel = new JPanel();
	private JLabel label_12 = new JLabel("工艺名称:");
	private JLabel technicsNameJLabel = new JLabel();
	private JLabel label_13 = new JLabel("工艺编号:");
	private JLabel technicsNumberJLabel = new JLabel();
	private JLabel label_14 = new JLabel("工艺版本:");
	private JLabel technicsVersionJLabel = new JLabel();
	private JLabel label_15 = new JLabel("创建者:");
	private JLabel creatorJLabel = new JLabel();
	private JLabel label_16 = new JLabel("修改者:");
	private JLabel menderJLabel = new JLabel();
	private JLabel label_17 = new JLabel("修改时间:");
	private JLabel creatTimeJLabel = new JLabel();
	private JLabel label_18 = new JLabel("修改时间:");
	private JLabel modifyTimeJLabel = new JLabel();
	private JLabel label_19 = new JLabel("工艺状态:");
	private JLabel technicsStateJLabel = new JLabel();

	private JLabel designerLabel = new JLabel("设计者");
	private JLabel designer = new JLabel();
	private JLabel reviewerLabel = new JLabel("审核者:");
	private JLabel reviewer = new JLabel();

	private JLabel signerLabel = new JLabel("工艺会签者:");
	private JLabel signer = new JLabel();

	private JLabel onBuildNumberLabel = new JLabel("令号:");
	private JLabel onBuildNumber = new JLabel();

	private JLabel secretLabel = new JLabel("密级:");
	private JLabel secretLevel = new JLabel("内部");

	private JLabel label_20 = new JLabel("PBOM状态:");
	private JLabel pbomStateJLabel = new JLabel();
	private JLabel imageLabel = new JLabel();
	private Map<String, byte[]> imageCache = new HashMap<String, byte[]>();
	private Insets insets = new Insets(5, 10, 5, 5);

	private Map<String,String> ibasMap = new HashMap<String,String>();
	private List<String> ibaList = new ArrayList<String>();
	private Map<String,String> typesMap = new HashMap<String,String>();
	private Map<String,String> attrs = new HashMap<String,String>();
	private Map<String,List<List<String>>> xmlmap = new HashMap<String, List<List<String>>>();
	private Map<String,String> attrsMap = new HashMap<String,String>();
	private List<Component> list = new ArrayList<Component>();
	private int row = 31;
	private String[] pplantype_array = {"正式工艺文件","临时工艺文件"};
	private String[] zfflag_array = {"Z","F"};
	private String[] secret_array = {"公开","内部"};
//	private String[] secret_array = {"公开","内部","秘密★10年","机密★20年"};
	private String[] department_array = {"1","2","3","4","5","6","7","8","项"};
	JTextField mindex_Value = new JTextField();
	JTextField pindex_value = new JTextField();
	JTextField keycomponent_value = new JTextField();
	JTextField phase_code_value = new JTextField();
	JComboBox zfflag_value = new JComboBox(zfflag_array);
	final JComboBox pplantype_value = new JComboBox(pplantype_array);
	private JTextField pplanid_value = new JTextField();
	final JLabel tempno_label = new JLabel("临时工艺顺序号");
	final JTextField tempno_value = new JTextField();
	JLabel mindex_label = new JLabel("产品型号代号");
	private JLabel pcno_label = new JLabel("批次号");
	private JTextField pcno_value = new JTextField();
	private JLabel dept_label = new JLabel("部门");
	private JLabel technicsEnglishName_label = new JLabel("工艺文件英文名");
	private JTextField technicsEnglishName_value = new JTextField();
	private JLabel imgVersion_label = new JLabel("图纸版本");
	private JTextField imgVersion_value = new JTextField();
	private JLabel vse_label = new JLabel("VSE");
	private JTextField vse_value = new JTextField();
	private JComboBox dept_value ;
	JComboBox secret_value = new JComboBox(secret_array);
	JTextField partNumber_value = new JTextField();
	JTextField partName_value = new JTextField();
	private static final String ID = "Rz";
	private boolean flag = false;
	private String technicsNoFlag = "";
	private JComboBox  technicsTypeComboBox ;
	private JTabbedPane technicsJTabbedPane = new JTabbedPane();

	/*
	private JPanel materialPanel = new JPanel();
	private JLabel materialNameLabel = new JLabel("材料名称:");
	private JLabel materialCodeLabel = new JLabel("材料编码:");
	private JLabel materialSpecificationLabel = new JLabel("材料规格:");
	private JLabel materialStandardsLabel = new JLabel("材料标准:");
	private JLabel materialInvtypeLabel = new JLabel("材料牌号:");

	private JLabel materialName = new JLabel("");
	private JLabel materialCode = new JLabel("");
	private JLabel materialSpecification = new JLabel("");
	private JLabel materialStandards = new JLabel("");
	private JLabel materialInvtype = new JLabel("");
	*/

	private NewTechnicsPart frame;
	private String technicsType;

	private WaiXieTecDescirbeJPanel waiXieTecDescirbeJPanel;
	private TechnicsDescribeJPanel technicsDescribeJPanel;
	private FuZhiTechnicsJPanel fuZhiTechnicsJPanel;
	private TechnicsStateTableJPanel technicsStateTableJPanel;
	private PeiTaoListTableJPanel peiTaoListTableJPanel;

	private ShowMaterialsJPanel cJPanel;
	private ShowMaterialsJPanel mJPanel;
	private ShowMaterialsJPanel sopJPanel;

	public NewTechnicsMasterJPanel_View(NewTechnicsPart frame) {
		getPartImage();
		// imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		// imageLabel.addMouseListener(new MouseAdapter() {
		// @Override
		// public void mouseClicked(MouseEvent e) {
		// if (e.getButton() == 1) {
		// String url = ProcessEditorToWCIntf
		// .getCreoViewUrl(technicsElement
		// .attributeValue("partOid"));
		// CommonUtil.openURL(url);
		// }
		// }
		// });
		this.frame = frame;

		//获取到所有的车间组，然后自动定位到当前用户所在的组
        List<String> allList = new ArrayList<String>();
        Map<String,String> workShop = ResourceIntf.getWorkShops();
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

		try {
			typesMap = TechnicsIntf.getMPMPPlanSubTypes();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			xmlmap = TechnicsIntf.getMPMPPlanTypeAttrByXML();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("-------------xmlmap--------"+xmlmap);

		setLayout(new GridBagLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.add(basicPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));

		panel.add(imageLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));

		panel.add(new JPanel(), new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(
						5, 5, 5, 5), 0, 0));

		panel.add(new JPanel(), new GridBagConstraints(3, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(
						5, 5, 5, 5), 0, 0));

		panel.add(new JPanel(), new GridBagConstraints(4, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(
						5, 5, 5, 5), 0, 0));

		add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));

		add(new JPanel(), new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(
						5, 5, 5, 5), 0, 0));

		basicPanel.setLayout(new GridBagLayout());
		panel.setBorder(new TitledBorder(null, "基本信息",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));

		//产品名称
		label_1.setMaximumSize(new Dimension(130, 23));
		label_1.setMinimumSize(new Dimension(130, 23));
		label_1.setPreferredSize(new Dimension(130, 23));
		label_1.setHorizontalAlignment(SwingConstants.LEFT);
		basicPanel.add(label_1, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		productNameJLabel.setMaximumSize(new Dimension(130, 23));
		productNameJLabel.setMinimumSize(new Dimension(130, 23));
		productNameJLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(productNameJLabel, new GridBagConstraints(1, 0, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		label_2.setMaximumSize(new Dimension(130, 23));
		label_2.setMinimumSize(new Dimension(130, 23));
		label_2.setPreferredSize(new Dimension(130, 23));
		label_2.setHorizontalAlignment(SwingConstants.LEFT);
		basicPanel.add(label_2, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		signNumberJLabel.setMaximumSize(new Dimension(130, 23));
		signNumberJLabel.setMinimumSize(new Dimension(130, 23));
		signNumberJLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(signNumberJLabel, new GridBagConstraints(3, 0, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		label_3.setMaximumSize(new Dimension(130, 23));
		label_3.setMinimumSize(new Dimension(130, 23));
		label_3.setPreferredSize(new Dimension(130, 23));
		label_3.setHorizontalAlignment(SwingConstants.LEFT);
		basicPanel.add(label_3, new GridBagConstraints(0, 1, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		partNameLabel.setMaximumSize(new Dimension(130, 23));
		partNameLabel.setMinimumSize(new Dimension(130, 23));
		partNameLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(partNameLabel, new GridBagConstraints(1, 1, 1, 1, 1.0,
				0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				insets, 0, 0));

		label_4.setMaximumSize(new Dimension(130, 23));
		label_4.setMinimumSize(new Dimension(130, 23));
		label_4.setPreferredSize(new Dimension(130, 23));
		label_4.setHorizontalAlignment(SwingConstants.LEFT);
		basicPanel.add(label_4, new GridBagConstraints(2, 1, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		partNumberLabel.setMaximumSize(new Dimension(130, 23));
		partNumberLabel.setMinimumSize(new Dimension(130, 23));
		partNumberLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(partNumberLabel, new GridBagConstraints(3, 1, 1, 1, 1.0,
				0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				insets, 0, 0));

		label_22.setMaximumSize(new Dimension(130, 23));
		label_22.setMinimumSize(new Dimension(130, 23));
		label_22.setPreferredSize(new Dimension(130, 23));
		label_22.setHorizontalAlignment(SwingConstants.LEFT);
		basicPanel.add(label_22, new GridBagConstraints(0, 2, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		numberVersionLabel.setMaximumSize(new Dimension(130, 23));
		numberVersionLabel.setMinimumSize(new Dimension(130, 23));
		numberVersionLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(numberVersionLabel, new GridBagConstraints(1, 2, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		label19.setMaximumSize(new Dimension(130, 23));
		label19.setMinimumSize(new Dimension(130, 23));
		label19.setPreferredSize(new Dimension(130, 23));
		label19.setHorizontalAlignment(SwingConstants.LEFT);
		basicPanel.add(label19, new GridBagConstraints(2, 2, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		ebomVesionLabel.setMaximumSize(new Dimension(130, 23));
		ebomVesionLabel.setMinimumSize(new Dimension(130, 23));
		ebomVesionLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(ebomVesionLabel, new GridBagConstraints(3, 2, 1, 1, 1.0,
				0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				insets, 0, 0));

		label20.setMaximumSize(new Dimension(130, 23));
		label20.setMinimumSize(new Dimension(130, 23));
		label20.setPreferredSize(new Dimension(130, 23));
		label20.setHorizontalAlignment(SwingConstants.LEFT);
		basicPanel.add(label20, new GridBagConstraints(0, 3, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		pbomVesionLabel.setMaximumSize(new Dimension(130, 23));
		pbomVesionLabel.setMinimumSize(new Dimension(130, 23));
		pbomVesionLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(pbomVesionLabel, new GridBagConstraints(1, 3, 1, 1, 1.0,
				0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				insets, 0, 0));

		label_6.setMaximumSize(new Dimension(130, 23));
		label_6.setMinimumSize(new Dimension(130, 23));
		label_6.setPreferredSize(new Dimension(130, 23));
		label_6.setHorizontalAlignment(SwingConstants.LEFT);
		basicPanel.add(label_6, new GridBagConstraints(2, 3, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		mtypeLabel.setMaximumSize(new Dimension(130, 23));
		mtypeLabel.setMinimumSize(new Dimension(130, 23));
		mtypeLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(mtypeLabel, new GridBagConstraints(3, 3, 1, 1, 1.0, 0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				insets, 0, 0));

		label_20.setMaximumSize(new Dimension(130, 23));
		label_20.setMinimumSize(new Dimension(130, 23));
		label_20.setPreferredSize(new Dimension(130, 23));
		label_20.setHorizontalAlignment(SwingConstants.LEFT);
		basicPanel.add(label_20, new GridBagConstraints(0, 4, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		pbomStateJLabel.setMaximumSize(new Dimension(130, 23));
		pbomStateJLabel.setMinimumSize(new Dimension(130, 23));
		pbomStateJLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(pbomStateJLabel, new GridBagConstraints(1, 4, 1, 1, 1.0,
				0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				insets, 0, 0));

		label_8.setMaximumSize(new Dimension(130, 23));
		label_8.setMinimumSize(new Dimension(130, 23));
		label_8.setPreferredSize(new Dimension(130, 23));
		label_8.setHorizontalAlignment(SwingConstants.LEFT);
		basicPanel.add(label_8, new GridBagConstraints(2, 4, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		mainWorkShopLabel.setMaximumSize(new Dimension(130, 23));
		mainWorkShopLabel.setMinimumSize(new Dimension(130, 23));
		mainWorkShopLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(mainWorkShopLabel, new GridBagConstraints(3, 4, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		label_9.setMaximumSize(new Dimension(130, 23));
		label_9.setMinimumSize(new Dimension(130, 23));
		label_9.setPreferredSize(new Dimension(130, 23));
		label_9.setHorizontalAlignment(SwingConstants.LEFT);
		basicPanel.add(label_9, new GridBagConstraints(0, 5, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		materialTypeLabel.setMaximumSize(new Dimension(130, 23));
		materialTypeLabel.setMinimumSize(new Dimension(130, 23));
		materialTypeLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(materialTypeLabel, new GridBagConstraints(1, 5, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

//		label_9.setMaximumSize(new Dimension(130, 23));
//		label_9.setMinimumSize(new Dimension(130, 23));
//		label_9.setPreferredSize(new Dimension(130, 23));
//		label_9.setHorizontalAlignment(SwingConstants.LEFT);
//		basicPanel.add(label_9, new GridBagConstraints(0, 6, 1, 1, 0, 0,
//				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
//				new Insets(5, 5, 5, 10), 0, 0));
//
//		materialTypeLabel.setMaximumSize(new Dimension(130, 23));
//		materialTypeLabel.setMinimumSize(new Dimension(130, 23));
//		materialTypeLabel.setPreferredSize(new Dimension(130, 23));
//		basicPanel.add(materialTypeLabel, new GridBagConstraints(1, 6, 1, 1,
//				1.0, 0, GridBagConstraints.NORTHWEST,
//				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		label_10.setMaximumSize(new Dimension(130, 23));
		label_10.setMinimumSize(new Dimension(130, 23));
		label_10.setPreferredSize(new Dimension(130, 23));
		label_10.setHorizontalAlignment(SwingConstants.LEFT);
		basicPanel.add(label_10, new GridBagConstraints(2, 5, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		backupRateLabel.setMaximumSize(new Dimension(130, 23));
		backupRateLabel.setMinimumSize(new Dimension(130, 23));
		backupRateLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(backupRateLabel, new GridBagConstraints(3, 5, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		label_11.setMaximumSize(new Dimension(130, 23));
		label_11.setMinimumSize(new Dimension(130, 23));
		label_11.setPreferredSize(new Dimension(130, 23));
		label_11.setHorizontalAlignment(SwingConstants.LEFT);
		basicPanel.add(label_11, new GridBagConstraints(0, 6, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		maxBackupCountLabel.setMaximumSize(new Dimension(130, 23));
		maxBackupCountLabel.setMinimumSize(new Dimension(130, 23));
		maxBackupCountLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(maxBackupCountLabel, new GridBagConstraints(1, 6, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		technicsPanel.setLayout(new GridBagLayout());
		technicsPanel.setBorder(new TitledBorder(null, "工艺信息",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));

		technicsPanel.setLayout(new GridBagLayout());
//		technicsPanel.setBorder(new TitledBorder(null, "工艺信息",
//				TitledBorder.DEFAULT_JUSTIFICATION,
//				TitledBorder.DEFAULT_POSITION, null, null));


		//第一行：工艺文件名称
		label_12.setMaximumSize(new Dimension(130, 23));
		label_12.setMinimumSize(new Dimension(130, 23));
		label_12.setPreferredSize(new Dimension(130, 23));
		label_12.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(label_12, new GridBagConstraints(0, 1, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		technicsNameJLabel.setMaximumSize(new Dimension(130, 23));
		technicsNameJLabel.setMinimumSize(new Dimension(130, 23));
		technicsNameJLabel.setPreferredSize(new Dimension(130, 23));
		technicsPanel.add(technicsNameJLabel, new GridBagConstraints(1, 1, 1,
				1, 1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第一行：工艺文件编号
		label_13.setMaximumSize(new Dimension(130, 23));
		label_13.setMinimumSize(new Dimension(130, 23));
		label_13.setPreferredSize(new Dimension(130, 23));
		label_13.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(label_13, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		technicsNumberJLabel.setMaximumSize(new Dimension(130, 23));
		technicsNumberJLabel.setMinimumSize(new Dimension(130, 23));
		technicsNumberJLabel.setPreferredSize(new Dimension(130, 23));
		technicsPanel.add(technicsNumberJLabel, new GridBagConstraints(1, 0, 1,
				1, 1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第二行：工艺版本
		label_14.setMaximumSize(new Dimension(130, 23));
		label_14.setMinimumSize(new Dimension(130, 23));
		label_14.setPreferredSize(new Dimension(130, 23));
		label_14.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(label_14, new GridBagConstraints(0, 2, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		technicsVersionJLabel.setMaximumSize(new Dimension(130, 23));
		technicsVersionJLabel.setMinimumSize(new Dimension(130, 23));
		technicsVersionJLabel.setPreferredSize(new Dimension(130, 23));
		technicsPanel.add(technicsVersionJLabel, new GridBagConstraints(1,2 ,
				3, 1, 1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第二行：创建者
		label_15.setMaximumSize(new Dimension(130, 23));
		label_15.setMinimumSize(new Dimension(130, 23));
		label_15.setPreferredSize(new Dimension(130, 23));
		label_15.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(label_15, new GridBagConstraints(0, 3, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		creatorJLabel.setMaximumSize(new Dimension(130, 23));
		creatorJLabel.setMinimumSize(new Dimension(130, 23));
		creatorJLabel.setPreferredSize(new Dimension(130, 23));
		technicsPanel.add(creatorJLabel, new GridBagConstraints(1, 3, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第三行：修改时间
		label_17.setMaximumSize(new Dimension(130, 23));
		label_17.setMinimumSize(new Dimension(130, 23));
		label_17.setPreferredSize(new Dimension(130, 23));
		label_17.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(label_17, new GridBagConstraints(0, 4, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		creatTimeJLabel.setMaximumSize(new Dimension(130, 23));
		creatTimeJLabel.setMinimumSize(new Dimension(130, 23));
		creatTimeJLabel.setPreferredSize(new Dimension(130, 23));
		technicsPanel.add(creatTimeJLabel, new GridBagConstraints(1, 4, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第三行：工艺状态
		label_19.setMaximumSize(new Dimension(130, 23));
		label_19.setMinimumSize(new Dimension(130, 23));
		label_19.setPreferredSize(new Dimension(130, 23));
		label_19.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(label_19, new GridBagConstraints(0, 5, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		technicsStateJLabel.setMaximumSize(new Dimension(130, 23));
		technicsStateJLabel.setMinimumSize(new Dimension(130, 23));
		technicsStateJLabel.setPreferredSize(new Dimension(130, 23));
		technicsPanel.add(technicsStateJLabel, new GridBagConstraints(1, 5, 1,
				1, 1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第四行：产品型号代号
		mindex_label.setMaximumSize(new Dimension(130, 23));
		mindex_label.setMinimumSize(new Dimension(130, 23));
		mindex_label.setPreferredSize(new Dimension(130, 23));
		mindex_label.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(mindex_label, new GridBagConstraints(0, 6, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));

		mindex_Value.setVisible(true);
		mindex_Value.setEditable(false);
		mindex_Value.setMaximumSize(new Dimension(130, 23));
		mindex_Value.setMinimumSize(new Dimension(130, 23));
		mindex_Value.setPreferredSize(new Dimension(300, 23));
		technicsPanel.add(mindex_Value, new GridBagConstraints(1, 6, 1, 1, 1.0, 0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第四行：产品代号
		final JLabel pindex_label = new JLabel("产品代号");
		pindex_label.setMaximumSize(new Dimension(130, 23));
		pindex_label.setMinimumSize(new Dimension(130, 23));
		pindex_label.setPreferredSize(new Dimension(130, 23));
		pindex_label.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(pindex_label, new GridBagConstraints(0, 7, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));

		pindex_value.setVisible(true);
		pindex_value.setEditable(false);
		pindex_value.setMaximumSize(new Dimension(130, 23));
		pindex_value.setMinimumSize(new Dimension(130, 23));
		pindex_value.setPreferredSize(new Dimension(300, 23));
		technicsPanel.add(pindex_value, new GridBagConstraints(1, 7, 1, 1, 1.0, 0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第五行：设计图样代号
		final JLabel partNumber_label = new JLabel("设计图样代号");
		partNumber_label.setMaximumSize(new Dimension(130, 23));
		partNumber_label.setMinimumSize(new Dimension(130, 23));
		partNumber_label.setPreferredSize(new Dimension(130, 23));
		partNumber_label.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(partNumber_label, new GridBagConstraints(0, 8, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));

		partNumber_value.setVisible(true);
		partNumber_value.setEditable(false);
		partNumber_value.setMaximumSize(new Dimension(120, 23));
		partNumber_value.setMinimumSize(new Dimension(120, 23));
		partNumber_value.setPreferredSize(new Dimension(300, 23));
		technicsPanel.add(partNumber_value, new GridBagConstraints(1, 8, 1, 1, 1.0, 0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第五行：设计图样名称
		final JLabel partName_label = new JLabel("设计图样名称");
		partName_label.setMaximumSize(new Dimension(130, 23));
		partName_label.setMinimumSize(new Dimension(130, 23));
		partName_label.setPreferredSize(new Dimension(130, 23));
		partName_label.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(partName_label, new GridBagConstraints(0, 9, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));

		partName_value.setVisible(true);
		partName_value.setEditable(false);
		partName_value.setMaximumSize(new Dimension(120, 23));
		partName_value.setMinimumSize(new Dimension(120, 23));
		partName_value.setPreferredSize(new Dimension(300, 23));
		technicsPanel.add(partName_value, new GridBagConstraints(1, 9, 1, 1, 1.0, 0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第六行：关重件标记
		final JLabel keycomponent_label = new JLabel("关重件标记");
		keycomponent_label.setMaximumSize(new Dimension(130, 23));
		keycomponent_label.setMinimumSize(new Dimension(130, 23));
		keycomponent_label.setPreferredSize(new Dimension(130, 23));
		keycomponent_label.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(keycomponent_label, new GridBagConstraints(0, 10, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));

		keycomponent_value.setVisible(true);
		keycomponent_value.setEditable(false);
		keycomponent_value.setMaximumSize(new Dimension(120, 23));
		keycomponent_value.setMinimumSize(new Dimension(120, 23));
		keycomponent_value.setPreferredSize(new Dimension(300, 23));
		technicsPanel.add(keycomponent_value, new GridBagConstraints(1, 10, 1, 1, 1.0, 0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第六行：产品阶段标记
		final JLabel phase_code_label = new JLabel("产品阶段标记");
		phase_code_label.setMaximumSize(new Dimension(130, 23));
		phase_code_label.setMinimumSize(new Dimension(130, 23));
		phase_code_label.setPreferredSize(new Dimension(130, 23));
		phase_code_label.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(phase_code_label, new GridBagConstraints(0, 11, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));

		phase_code_value.setVisible(true);
		phase_code_value.setEditable(false);
		phase_code_value.setMaximumSize(new Dimension(120, 23));
		phase_code_value.setMinimumSize(new Dimension(120, 23));
		phase_code_value.setPreferredSize(new Dimension(300, 23));
		technicsPanel.add(phase_code_value, new GridBagConstraints(1, 11, 1, 1, 1.0, 0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第七行：工艺文件类别
		final JLabel pplantype_label = new JLabel("工艺文件类别");
		pplantype_label.setMaximumSize(new Dimension(130, 23));
		pplantype_label.setMinimumSize(new Dimension(130, 23));
		pplantype_label.setPreferredSize(new Dimension(130, 23));
		pplantype_label.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(pplantype_label, new GridBagConstraints(0, 12, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));

		pplantype_value.setVisible(true);
		pplantype_value.setEnabled(false);
		pplantype_value.setPreferredSize(new Dimension(300, 23));
		technicsPanel.add(pplantype_value, new GridBagConstraints(1, 12, 1, 1, 1.0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第七行：临时工艺顺序号
		tempno_label.setVisible(false);
		tempno_label.setMaximumSize(new Dimension(130, 23));
		tempno_label.setMinimumSize(new Dimension(130, 23));
		tempno_label.setPreferredSize(new Dimension(130, 23));
		tempno_label.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(tempno_label, new GridBagConstraints(0, 13, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));

		tempno_value.setVisible(false);
		tempno_value.setMaximumSize(new Dimension(120, 23));
		tempno_value.setMinimumSize(new Dimension(120, 23));
		tempno_value.setPreferredSize(new Dimension(300, 23));
		technicsPanel.add(tempno_value, new GridBagConstraints(1, 13, 1, 1, 1.0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第八行：主辅制类别
		JLabel zfflag_label = new JLabel("主辅制类别");
		zfflag_label.setMaximumSize(new Dimension(130, 23));
		zfflag_label.setMinimumSize(new Dimension(130, 23));
		zfflag_label.setPreferredSize(new Dimension(130, 23));
		zfflag_label.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(zfflag_label, new GridBagConstraints(0, 14, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));

		zfflag_value.setEnabled(false);
		zfflag_value.setVisible(true);
		zfflag_value.setPreferredSize(new Dimension(300, 23));
		technicsPanel.add(zfflag_value, new GridBagConstraints(1, 14, 1, 1, 1.0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第八行：工艺类型
		JLabel  technicsTypeLabel = new JLabel("工艺类型:");
		technicsTypeLabel.setMinimumSize(new Dimension(130, 23));
		technicsTypeLabel.setPreferredSize(new Dimension(130, 23));
		technicsTypeLabel.setPreferredSize(new Dimension(130, 23));
		technicsTypeLabel.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(technicsTypeLabel, new GridBagConstraints(0, 15, 1, 1, 0,
				0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));

		technicsTypeComboBox = new JComboBox(typesMap.values().toArray());
		technicsTypeComboBox.setVisible(true);
		technicsTypeComboBox.setEnabled(false);
		technicsPanel.add(technicsTypeComboBox, new GridBagConstraints(1, 15, 1, 1, 1.0,
				0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第九行：工艺特征编号
		JLabel pplanid_label = new JLabel("工艺特征编号");
		pplanid_label.setMaximumSize(new Dimension(130, 23));
		pplanid_label.setMinimumSize(new Dimension(130, 23));
		pplanid_label.setPreferredSize(new Dimension(130, 23));
		pplanid_label.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(pplanid_label, new GridBagConstraints(0, 16, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));

		pplanid_value.setVisible(true);
		pplanid_value.setMaximumSize(new Dimension(120, 23));
		pplanid_value.setMinimumSize(new Dimension(120, 23));
		pplanid_value.setPreferredSize(new Dimension(300, 23));
		pplanid_value.setEditable(false);
		technicsPanel.add(pplanid_value, new GridBagConstraints(1, 16, 1, 1, 1.0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第九行：文件密级
		JLabel secret_label = new JLabel("文件密级");
		secret_label.setMaximumSize(new Dimension(130, 23));
		secret_label.setMinimumSize(new Dimension(130, 23));
		secret_label.setPreferredSize(new Dimension(130, 23));
		secret_label.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(secret_label, new GridBagConstraints(0, 17, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));

		secret_value.setEnabled(false);
		secret_value.setVisible(true);
		secret_value.setMaximumSize(new Dimension(120, 23));
		secret_value.setMinimumSize(new Dimension(120, 23));
		secret_value.setPreferredSize(new Dimension(300, 23));
		technicsPanel.add(secret_value, new GridBagConstraints(1, 17, 1, 1, 1.0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第十行：批次号
		pcno_label.setMaximumSize(new Dimension(130, 23));
		pcno_label.setMinimumSize(new Dimension(130, 23));
		pcno_label.setPreferredSize(new Dimension(130, 23));
		pcno_label.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(pcno_label, new GridBagConstraints(0, 18, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));

		pcno_value.setEditable(false);
		pcno_value.setVisible(true);
		pcno_value.setMaximumSize(new Dimension(120, 23));
		pcno_value.setMinimumSize(new Dimension(120, 23));
		pcno_value.setPreferredSize(new Dimension(300, 23));
		technicsPanel.add(pcno_value, new GridBagConstraints(1, 18, 1, 1, 1.0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第十行：部门
		dept_label.setMaximumSize(new Dimension(130, 23));
		dept_label.setMinimumSize(new Dimension(130, 23));
		dept_label.setPreferredSize(new Dimension(130, 23));
		dept_label.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(dept_label, new GridBagConstraints(0, 19, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));

		dept_value.setEnabled(false);
		dept_value.setVisible(true);
		dept_value.setMaximumSize(new Dimension(120, 23));
		dept_value.setMinimumSize(new Dimension(120, 23));
		dept_value.setPreferredSize(new Dimension(300, 23));
		technicsPanel.add(dept_value, new GridBagConstraints(1, 19, 1, 1, 1.0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//工艺文件英文名
		technicsEnglishName_label.setMaximumSize(new Dimension(130, 23));
		technicsEnglishName_label.setMinimumSize(new Dimension(130, 23));
		technicsEnglishName_label.setPreferredSize(new Dimension(130, 23));
		technicsEnglishName_label.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(technicsEnglishName_label, new GridBagConstraints(0, 20, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));

		technicsEnglishName_value.setEnabled(false);
		technicsEnglishName_value.setVisible(true);
		technicsEnglishName_value.setMaximumSize(new Dimension(120, 23));
		technicsEnglishName_value.setMinimumSize(new Dimension(120, 23));
		technicsEnglishName_value.setPreferredSize(new Dimension(300, 23));
		technicsPanel.add(technicsEnglishName_value, new GridBagConstraints(1, 20, 1, 1, 1.0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		//图纸版本
		imgVersion_label.setMaximumSize(new Dimension(130, 23));
		imgVersion_label.setMinimumSize(new Dimension(130, 23));
		imgVersion_label.setPreferredSize(new Dimension(130, 23));
		imgVersion_label.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(imgVersion_label, new GridBagConstraints(0, 21, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));

		imgVersion_value.setEnabled(false);
		imgVersion_value.setVisible(true);
		imgVersion_value.setMaximumSize(new Dimension(120, 23));
		imgVersion_value.setMinimumSize(new Dimension(120, 23));
		imgVersion_value.setPreferredSize(new Dimension(300, 23));
		technicsPanel.add(imgVersion_value, new GridBagConstraints(1, 21, 1, 1, 1.0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		//VSE
		vse_label.setMaximumSize(new Dimension(130, 23));
		vse_label.setMinimumSize(new Dimension(130, 23));
		vse_label.setPreferredSize(new Dimension(130, 23));
		vse_label.setHorizontalAlignment(SwingConstants.LEFT);
		technicsPanel.add(vse_label, new GridBagConstraints(0, 22, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));

		vse_value.setEnabled(false);
		vse_value.setVisible(true);
		vse_value.setMaximumSize(new Dimension(120, 23));
		vse_value.setMinimumSize(new Dimension(120, 23));
		vse_value.setPreferredSize(new Dimension(300, 23));
		technicsPanel.add(vse_value, new GridBagConstraints(1, 22, 1, 1, 1.0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//工艺说明
//		JLabel technicsDescLabel = new JLabel("工艺说明");
//		technicsDescLabel.setMinimumSize(new Dimension(130, 23));
//		technicsDescLabel.setPreferredSize(new Dimension(130, 23));
//		technicsPanel.add(technicsDescLabel, new GridBagConstraints(2, 5, 1, 1, 1.0,
//				0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
//				new Insets(5, 5, 5, 10), 0, 0));
//		technicsDescFilePath = new JTextField();
//		JButton editTechincsDescButton = new JButton("编辑工艺说明");
//		editTechincsDescButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				try {
//					String dir = WorkSpaceUtil.getTechnicsDirectory(technicsNumberJLabel.getText());
//					String path = dir+File.separator+"工艺说明.doc";
//					Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler "+path);
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//			}
//		});
//		technicsPanel.add(editTechincsDescButton, new GridBagConstraints(3, 5, 1, 1, 1.0,
//				0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
//				insets, 0, 0));

//		technicsDescText = new JTextArea();
//		technicsDescText.setRows(4);
//		JScrollPane js = new JScrollPane(technicsDescText);
//		js.setVisible(false);
//		technicsPanel.add(js, new GridBagConstraints(0, row, 4, 1, 1.0,
//				0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
//				insets, 0, 0));


		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		panel1.setLayout(new GridBagLayout());
		panel2.setLayout(new GridBagLayout());

		pane.add(new JScrollPane(panel1,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
				JSplitPane.TOP);

		panel1.add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));

		pane.add(new JScrollPane(panel2,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
				JSplitPane.BOTTOM);

		panel2.add(technicsJTabbedPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));

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
		add(pane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		pane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		pane.setMinimumSize(new Dimension(100, 226));
		pane.setContinuousLayout(true);
		pane.setOneTouchExpandable(true);
		pane.setDividerSize(10);
		pane.setDividerLocation(350);

		//TODO 材料信息
		cJPanel = new ShowMaterialsJPanel((NewTechnicsPart)frame);
		panel1.add(cJPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(0, 5, 0, 5), 0, 0));

		mJPanel = new ShowMaterialsJPanel((NewTechnicsPart)frame);
		panel1.add(mJPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(0, 5, 0, 5), 0, 0));
		sopJPanel = new ShowMaterialsJPanel((NewTechnicsPart)frame);
		panel1.add(sopJPanel, new GridBagConstraints(0, 3, 1, 1, 1.0, 0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(0, 5, 0, 5), 0, 0));

		/*
		materialPanel.setLayout(new GridBagLayout());

		//材料编码
		materialCodeLabel.setMaximumSize(new Dimension(130, 23));
		materialCodeLabel.setMinimumSize(new Dimension(130, 23));
		materialCodeLabel.setPreferredSize(new Dimension(130, 23));
		materialCodeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(materialCodeLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		materialCode.setMaximumSize(new Dimension(130, 23));
		materialCode.setMinimumSize(new Dimension(130, 23));
		materialCode.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(materialCode, new GridBagConstraints(1, 0, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//材料名称
		materialNameLabel.setMaximumSize(new Dimension(130, 23));
		materialNameLabel.setMinimumSize(new Dimension(130, 23));
		materialNameLabel.setPreferredSize(new Dimension(130, 23));
		materialNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(materialNameLabel, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		materialName.setMaximumSize(new Dimension(130, 23));
		materialName.setMinimumSize(new Dimension(130, 23));
		materialName.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(materialName, new GridBagConstraints(3, 0, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//材料规格
		materialSpecificationLabel.setMaximumSize(new Dimension(130, 23));
		materialSpecificationLabel.setMinimumSize(new Dimension(130, 23));
		materialSpecificationLabel.setPreferredSize(new Dimension(130, 23));
		materialSpecificationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(materialSpecificationLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		materialSpecification.setMaximumSize(new Dimension(130, 23));
		materialSpecification.setMinimumSize(new Dimension(130, 23));
		materialSpecification.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(materialSpecification, new GridBagConstraints(1, 1, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//材料标准
		materialStandardsLabel.setMaximumSize(new Dimension(130, 23));
		materialStandardsLabel.setMinimumSize(new Dimension(130, 23));
		materialStandardsLabel.setPreferredSize(new Dimension(130, 23));
		materialStandardsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(materialStandardsLabel, new GridBagConstraints(2, 1, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		materialStandards.setMaximumSize(new Dimension(130, 23));
		materialStandards.setMinimumSize(new Dimension(130, 23));
		materialStandards.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(materialStandards, new GridBagConstraints(3, 1, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//材料牌号
		materialInvtypeLabel.setMaximumSize(new Dimension(130, 23));
		materialInvtypeLabel.setMinimumSize(new Dimension(130, 23));
		materialInvtypeLabel.setPreferredSize(new Dimension(130, 23));
		materialInvtypeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(materialInvtypeLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 10), 0, 0));

		materialInvtype.setMaximumSize(new Dimension(130, 23));
		materialInvtype.setMinimumSize(new Dimension(130, 23));
		materialInvtype.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(materialInvtype, new GridBagConstraints(1, 2, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		*/

		technicsJTabbedPane.add("工艺文件信息", technicsPanel);
		technicsJTabbedPane.add("典型/通用工艺、标准", new BorrowThecnicsJPanel(frame));
//		technicsJTabbedPane.add("工艺报表", new AdditionalTableJPanel(frame));
		technicsJTabbedPane.add("工艺附表", new AdditionalTableJPanel(frame));
//		technicsJTabbedPane.add("材料定额信息", new TechnicsCLDEJPanel(frame));
//		technicsJTabbedPane.add("工艺定额信息", new TechnicsDEJPanel(frame));
		technicsJTabbedPane.add("零件工艺定额信息", new TechnicsCLDEJPanel(frame));
		technicsJTabbedPane.add("装配工艺定额信息", new TechnicsDEJPanel(frame));
//		technicsJTabbedPane.add("主要材料(装配工艺定额)", new TechnicsZYCLDEJPanel(frame));
		technicsJTabbedPane.add("零件工艺定额信息(设计资源库)", new TechnicsSjzykCLDEJPanel(frame));
		technicsJTabbedPane.add("装配工艺定额信息(设计资源库)", new TechnicsDEForSjzykJPanel(frame));

		technicsStateTableJPanel = new TechnicsStateTableJPanel((NewTechnicsPart)frame);
		technicsStateTableJPanel.setUIEnabled(false);
		technicsJTabbedPane.add("工艺状态表", technicsStateTableJPanel);

		technicsDescribeJPanel = new TechnicsDescribeJPanel((NewTechnicsPart)frame);
		technicsDescribeJPanel.setUIEnabled(false);
		technicsJTabbedPane.add("工艺说明", technicsDescribeJPanel);

		waiXieTecDescirbeJPanel = new WaiXieTecDescirbeJPanel((NewTechnicsPart)frame);
		waiXieTecDescirbeJPanel.setUIEnabled(false);
		technicsJTabbedPane.add("外协工艺说明", waiXieTecDescirbeJPanel);

		fuZhiTechnicsJPanel = new FuZhiTechnicsJPanel(frame);
		fuZhiTechnicsJPanel.setUIEnabled(false);
		technicsJTabbedPane.add("辅制工艺信息", fuZhiTechnicsJPanel);

		peiTaoListTableJPanel = new PeiTaoListTableJPanel((NewTechnicsPart)frame);
		technicsJTabbedPane.add("配套明细表", peiTaoListTableJPanel);

		technicsJTabbedPane.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
//				 int index = technicsJTabbedPane.getSelectedIndex();
				 JPanel tabbedPane = (JPanel) technicsJTabbedPane.getSelectedComponent();
				 refreshTabPanel(tabbedPane );
			}
		});
	}

	public void setUIValues(Element techElement) {
		technicsElement = techElement;

		if ("rework".equals(techElement.attributeValue("technicsCategory"))) {
			onBuildNumberLabel.setText("返工令号");
		} else {
			onBuildNumberLabel.setVisible(false);
			onBuildNumber.setVisible(false);
		}
		productNameJLabel.setText(techElement.attributeValue("productName"));

		creatorJLabel.setText(techElement.attributeValue("creator"));
		creatTimeJLabel.setText(techElement.attributeValue("modifyTime"));
		technicsNameJLabel.setText(techElement.attributeValue("technicsName"));
		technicsNumberJLabel.setText(techElement
				.attributeValue("technicsNumber"));
		mainWorkShopLabel.setText(techElement.attributeValue("workShop"));
		partVersionLabel.setText(techElement.attributeValue("partVersion"));
		materialTypeLabel.setText(techElement.attributeValue("materialType"));// 物料类型
		signNumberJLabel.setText(techElement.attributeValue("parentPartNumber"));
		partNameLabel.setText(techElement.attributeValue("partName"));
		partNumberLabel.setText(techElement.attributeValue("partNumber"));
		maxBackupCountLabel.setText(techElement.attributeValue("FZCJ"));
		backupRateLabel.setText(techElement.attributeValue("ZZCJ"));
		technicsVersionJLabel.setText(techElement.attributeValue("version"));
		technicsStateJLabel.setText(techElement.attributeValue("lifecycle"));

		designer.setText(techElement.attributeValue("designer"));
		reviewer.setText(techElement.attributeValue("reviewer"));
		signer.setText(techElement.attributeValue("signer"));
		onBuildNumber.setText(techElement.attributeValue("onBuildNumber"));
		secretLevel.setText(techElement.attributeValue("secretLevel"));

		pbomStateJLabel.setText(techElement.attributeValue("pbomLifecycle"));

		numberVersionLabel.setText(techElement.attributeValue("eu_version"));
		ebomVesionLabel.setText(techElement.attributeValue("e_version"));
		pbomVesionLabel.setText(techElement.attributeValue("partVersion"));

		mtypeLabel.setText(techElement.attributeValue("MTYPE"));

		if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			sopJPanel.setValues(techElement, false, true);
		}else{
			cJPanel.setValues(techElement, false, false);
			mJPanel.setValues(techElement, true, false);
		}

		//begin 以下是IBA属性显示
		showIBAAttributes(techElement);
		//end  IBA属性显示结束

		getPartImage();
	}

	private void showIBAAttributes(Element techElement){
		hideIBAAttributes();
		this.technicsType = techElement.attributeValue("technicsType");

		mindex_Value.setText(techElement.attributeValue("MINDEX"));
		pindex_value.setText(techElement.attributeValue("PINDEX"));
		partNumber_value.setText(techElement.attributeValue("partNumber"));
		partName_value.setText(techElement.attributeValue("partName"));
		keycomponent_value.setText(techElement.attributeValue("KEYCOMPONENT"));
		phase_code_value.setText(techElement.attributeValue("PHASE_CODE"));
		pplantype_value.setSelectedItem(techElement.attributeValue("PPLANTYPE"));
		if("临时工艺文件".equals(techElement.attributeValue("PPLANTYPE"))) {
			tempno_label.setVisible(true);
			tempno_value.setVisible(true);
			tempno_value.setEditable(false);
			tempno_value.setText(techElement.attributeValue("TEMPNO"));
		}
		zfflag_value.setSelectedItem(techElement.attributeValue("ZFFLAG"));
		technicsTypeComboBox.setSelectedItem(techElement.attributeValue("technicsType"));
		pplanid_value.setText(techElement.attributeValue("PPLANID"));
		secret_value.setSelectedItem(techElement.attributeValue("SECRET"));
		pcno_value.setText(techElement.attributeValue("PCNO"));
		dept_value.setSelectedItem(techElement.attributeValue("DEPT"));

		row = 31;
		List<List<String>> allAttributes = xmlmap.get(technicsType);

		//获取零部件的IBA属性值
		//QMEditor-TODO:获取的参数并未用到，注解掉了，LB/20191225
		/*if(ibasMap.isEmpty()) {
			try {
				ibasMap = TechnicsIntf.getPartIBAValuesByNumber2(techElement.attributeValue("partNumber"), allAttributes);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("---------ibasMap-------"+ibasMap);*/

		if(allAttributes != null && !allAttributes.isEmpty()) {
			JTextField textField = null;
			JComboBox comboBox = null;
			JLabel label = null;
			int col = 0;
			for (List<String> ibaList : allAttributes) {
				if(ibaList != null && !ibaList.isEmpty()) {
					if("TOTALAREA".equals(ibaList.get(0)) || "TLXHLJS".equals(ibaList.get(0))) {
						continue;
					}

					boolean isEditor = Boolean.valueOf(ibaList.get(6));
//					String partAttr = ibaList.get(5);
//					String partAttrValue = "";
//					if(partAttr != null && !"".equals(partAttr)) {
//						partAttrValue = ibasMap.get(partAttr);
//					}

					label = new JLabel(ibaList.get(1));
					label.setVisible(true);
					list.add(label);

					if("MJJS".equals(ibaList.get(0))){
						JPanel p1 = new JPanel();
						p1.setVisible(true);
						FlowLayout flowLayout = new FlowLayout();
						flowLayout.setAlignment(FlowLayout.LEFT);
						p1.setLayout(flowLayout);

						p1.add(label);
						JTextField MJJS = new JTextField();
						MJJS.setName("MJJS");
						MJJS.setVisible(true);
						MJJS.setPreferredSize(new Dimension(60,23));
						MJJS.setText(techElement.attributeValue("MJJS"));
						p1.add(MJJS);
						list.add(MJJS);
						MJJS.setEditable(false);
						attrsMap.put("MJJS", ibaList.get(1));
						label = new JLabel("件面积Cr2");
						list.add(label);
						label.setVisible(true);
						p1.add(label);
						JTextField TOTALAREA = new JTextField();
						TOTALAREA.setName("TOTALAREA");
						TOTALAREA.setVisible(true);
						TOTALAREA.setPreferredSize(new Dimension(120,23));
						TOTALAREA.setText(techElement.attributeValue("TOTALAREA"));
						p1.add(TOTALAREA);
						list.add(TOTALAREA);
						TOTALAREA.setEditable(false);
						attrsMap.put("TOTALAREA", "件面积Cr2");
						technicsPanel.add(p1, new GridBagConstraints(0, ++row, 2, 1, 0, 0,
								GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

						col = 0;
					} else if("MJTLXHL".equals(ibaList.get(0))) {
						JPanel p2 = new JPanel();
						list.add(p2);
						FlowLayout flowLayout = new FlowLayout();
						flowLayout.setAlignment(FlowLayout.LEFT);
						p2.setLayout(flowLayout);

						p2.add(label);
						JTextField YGDJKZLJS = new JTextField();
						YGDJKZLJS.setName("MJTLXHL");
						list.add(YGDJKZLJS);
						attrsMap.put("MJTLXHL", ibaList.get(1));
						YGDJKZLJS.setVisible(true);
						YGDJKZLJS.setPreferredSize(new Dimension(60,23));
						YGDJKZLJS.setText(techElement.attributeValue("MJTLXHL"));
						p2.add(YGDJKZLJS);
						YGDJKZLJS.setEditable(false);
						list.add(textField);
						label = new JLabel("件涂料消耗量kg");
						list.add(label);
						label.setVisible(true);
						p2.add(label);
						JTextField TLXHLJS = new JTextField();
						TLXHLJS.setName("TLXHLJS");
						TLXHLJS.setVisible(true);
						TLXHLJS.setPreferredSize(new Dimension(120,23));
						TLXHLJS.setText(techElement.attributeValue("TLXHLJS"));
						p2.add(TLXHLJS);
						TLXHLJS.setEditable(false);
						list.add(TLXHLJS);
						attrsMap.put("TLXHLJS", "件涂料消耗量kg");
						technicsPanel.add(p2, new GridBagConstraints(0, ++row, 2, 1, 0, 0,
								GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

						col = 0;
					} else {
						attrsMap.put(ibaList.get(0), ibaList.get(1));
						label.setMinimumSize(new Dimension(130, 23));
						label.setPreferredSize(new Dimension(130, 23));
						label.setHorizontalAlignment(SwingConstants.LEFT);
						technicsPanel.add(label, new GridBagConstraints(col++, row, 1, 1, 0, 0,
								GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));

						if("string".equals(ibaList.get(4))){
							textField = new JTextField();
							textField.setName(ibaList.get(0));
							textField.setVisible(true);
							textField.setEditable(isEditor);
							list.add(textField);
							textField.setText(techElement.attributeValue(ibaList.get(0)));
							textField.setMaximumSize(new Dimension(130, 23));
							textField.setMinimumSize(new Dimension(130, 23));
							textField.setPreferredSize(new Dimension(130, 23));
							textField.setEditable(false);
							technicsPanel.add(textField, new GridBagConstraints(col++, row, 1, 1, 1.0, 0,
									GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
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
							comboBox.setEnabled(false);
							comboBox.setSelectedItem(techElement.attributeValue(ibaList.get(0)));
							technicsPanel.add(comboBox, new GridBagConstraints(col++, row++, 1, 1, 1.0, 0,
									GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
						}
					}

					if(col == 4) {
						row++;
						col = 0;
					}
				}
			}
		}
	}

	private void hideIBAAttributes(){
		for(Component component:list) {
			component.setVisible(false);
		}
		list.clear();
	}

	public void clearUI() {
		node = null;
		technicsElement = null;
		productNameJLabel.setText("");
		signNumberJLabel.setText("");
		partNameLabel.setText("");
		partNumberLabel.setText("");
		technicsNumberJLabel.setText("");
		technicsNameJLabel.setText("");
		partVersionLabel.setText("");
		mtypeLabel.setText("");
		mainWorkShopLabel.setText("");
		materialTypeLabel.setText("");
		backupRateLabel.setText("");
		maxBackupCountLabel.setText("");
		numberVersionLabel.setText("");
		ebomVesionLabel.setText("");
		pbomVesionLabel.setText("");
		technicsStateJLabel.setText("");

		designer.setText("");
		reviewer.setText("");
		signer.setText("");
		onBuildNumber.setText("");
		secretLevel.setText("");

		pbomStateJLabel.setText("");

		technicsVersionJLabel.setText("");
		creatorJLabel.setText("");
		menderJLabel.setText("");
		creatTimeJLabel.setText("");
		modifyTimeJLabel.setText("");
		imageLabel.setIcon(new ImageIcon());
	}

	private void refreshTabPanel( JPanel tabbedPane ){
		String name = tabbedPane.getName();
		if("AdditionalTable".equals(name) && tabbedPane instanceof AdditionalTableJPanel){
			AdditionalTableJPanel panel = (AdditionalTableJPanel)tabbedPane;
			List<Element> attachElement = XmlUtility.getTechnicsAdditionTables(technicsElement);
			Vector<Element> attachElements = new Vector<Element>();
			if(attachElement!=null)
				for (Element ee: attachElement) {
					attachElements.add(ee);
				}
			logger.debug(attachElements.size() + " attachElements==="+ attachElements.size());
			panel.setTableValues(attachElements);
			panel.setUIEnabled(false);
		} else if("BorrowThecnics".equals(name) && tabbedPane instanceof BorrowThecnicsJPanel){
			BorrowThecnicsJPanel panel = (BorrowThecnicsJPanel)tabbedPane;
			List<Element> attachElement = XmlUtility.getBorrowTechnics(technicsElement);
			Vector<Element> attachElements = new Vector<Element>();
			if(attachElement!=null)
				for (Element ee: attachElement) {
					attachElements.add(ee);
				}
			logger.debug(attachElements.size() + " attachElements==="+ attachElements.size());
			panel.setTableValues(attachElements);
			panel.setUIEnabled(false);
		} else if("TechnicsStateTable".equals(name) && tabbedPane instanceof TechnicsStateTableJPanel){
			TechnicsStateTableJPanel panel = (TechnicsStateTableJPanel)tabbedPane;
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			List<Element> attachElement = XmlUtility.getTechnicsStateTables(latestEle);
			Vector<Element> attachElements = new Vector<Element>();
			if(attachElement!=null)
				for (Element ee: attachElement) {
					attachElements.add(ee);
				}
			logger.debug(attachElements.size() + " TechnicsStateTable==attachElements==="+ attachElements.size());
			panel.setTableValues(attachElements);
		} else if("TechnicsDescribe".equals(name) && tabbedPane instanceof TechnicsDescribeJPanel){
			TechnicsDescribeJPanel panel = (TechnicsDescribeJPanel)tabbedPane;
			//edit by hding 20150615
			//String mtype = technicsElement.attributeValue("MTYPE");
			//if(!"带料委外件".equals(mtype) && !"不带料委外件".equals(mtype)) {
				Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
				String content = XmlUtility.getTechnicsDescribe(latestEle);
				panel.setContent(content);
			//} else {
			//	panel.setUIEnabled(false);
			//}
		} else if("WaiXieTecDescirbe".equals(name) && tabbedPane instanceof WaiXieTecDescirbeJPanel){
			WaiXieTecDescirbeJPanel panel = (WaiXieTecDescirbeJPanel)tabbedPane;
			String mtype = technicsElement.attributeValue("isTabular");
			if("外协".equals(mtype)) {
				Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
				panel.loadData(latestEle);
			} else {
				panel.setUIEnabled(false);
			}
		} else if("clde".equals(name) && tabbedPane instanceof TechnicsCLDEJPanel){
			TechnicsCLDEJPanel cldePanel = (TechnicsCLDEJPanel)tabbedPane;
			cldePanel.setTableValues(technicsElement);
		} else if("gyde".equals(name) && tabbedPane instanceof TechnicsDEJPanel){
			TechnicsDEJPanel cldePanel = (TechnicsDEJPanel)tabbedPane;
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			cldePanel.setTableValues(latestEle);
		} else if(tabbedPane instanceof TechnicsZYCLDEJPanel){
			TechnicsZYCLDEJPanel zycldejPanel = (TechnicsZYCLDEJPanel) tabbedPane;
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			zycldejPanel.setTableValues(latestEle);
		} else if("PeiTaoListTableJPanel".equals(name) && tabbedPane instanceof PeiTaoListTableJPanel){
			PeiTaoListTableJPanel peiTaoListTableJPanel = (PeiTaoListTableJPanel)tabbedPane;
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			peiTaoListTableJPanel.setTableValues(latestEle);
			peiTaoListTableJPanel.setUIEnabled(false);
		} else if("sjzykgyde".equals(name) && tabbedPane instanceof TechnicsDEForSjzykJPanel){
			TechnicsDEForSjzykJPanel cldePanel = (TechnicsDEForSjzykJPanel)tabbedPane;
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			cldePanel.setTableValues(latestEle);
		} else if("sjzykclde".equals(name) && tabbedPane instanceof TechnicsSjzykCLDEJPanel){
			TechnicsSjzykCLDEJPanel cldePanel = (TechnicsSjzykCLDEJPanel)tabbedPane;
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			cldePanel.setTableValues(latestEle);
		} else if("FuZhiTechnicsJPanel".equals(name) && tabbedPane instanceof FuZhiTechnicsJPanel){
			FuZhiTechnicsJPanel fuZhiTechnicsJPanel = (FuZhiTechnicsJPanel)tabbedPane;
			Element latestEle = XmlUtility.getTechnicsElement(technicsElement.getDocument());
			fuZhiTechnicsJPanel.setTableValues(latestEle);
		}
	}

	public void setNode(XWTreeNode node) {
		this.node = node;
	}

	public XWTreeNode getNode() {
		return node;
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
}