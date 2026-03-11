package com.glaway.mpm.view;

import com.glaway.mpm.util.BomXMLUtil;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.wcIntf.ImageIntf;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class PartMasterJPanel extends JPanel {
	private JFrame frame;
	private XWTreeNode node;
	private Element partElement;

	private JPanel basicPanel = new JPanel();
	private JPanel materialPanel = new JPanel();
//	private JLabel label_1 = new JLabel("产品名称:");
//	private JLabel productNameJLabel = new JLabel("");
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
	private JLabel label20 = new JLabel("零部件PBOM版本:");
	private JLabel pbomVesionLabel = new JLabel("");
	// private JLabel label_5 = new JLabel("零部件版本:");
	// private JLabel partVersionLabel = new JLabel("");
	private JLabel label_6 = new JLabel("是否关键工艺:");
	private JLabel isKeyLabel = new JLabel("");
	private JLabel label_7 = new JLabel("是否特殊件:");
	private JLabel isSpecialKeyLabel = new JLabel("");
	private JLabel label_8 = new JLabel("主制单位:");
	private JLabel mainWorkShopLabel = new JLabel("");
	private JLabel label_9 = new JLabel("零组件生产类型:");
	private JLabel materialTypeLabel = new JLabel("");
	private JLabel label_10 = new JLabel("备份比例:");
	private JLabel backupRateLabel = new JLabel("");
	private JLabel label_11 = new JLabel("最大备份数量:");
	private JLabel maxBackupCountLabel = new JLabel("");
	private JLabel label_21 = new JLabel("备份说明:");
	private JLabel backUpDescriptionLabel = new JLabel("");

	//主制车间,辅制车间,批次,工装代号
	//zzjc,fzcj,batch,product_index
	private JLabel zzjc = new JLabel("主制车间:");
	private JLabel zzjcV = new JLabel("");
	private JLabel fzcj = new JLabel("辅制车间:");
	private JLabel fzcjV = new JLabel("");
	private JLabel batch = new JLabel("批次:");
	private JLabel batchV = new JLabel("");
	private JLabel product_index = new JLabel("工装代号:");
	private JLabel product_indexV = new JLabel("");

	private JLabel remarkLabel = new JLabel("备注:");
	private JLabel remark = new JLabel("");

	private JLabel imageLabel = new JLabel();
	private Insets insets = new Insets(0, 0, 0, 5);
	private Map<String, byte[]> imageCache = new HashMap<String, byte[]>();

	/*
	private JLabel materialNameLabel = new JLabel("材料名称:");
	private JLabel materialCodeLabel = new JLabel("材料编码:");
	private JLabel materialSpecificationLabel = new JLabel("材料规格:");
	private JLabel materialStandardsLabel = new JLabel("材料标准:");
	private JLabel materialInvtypeLabel = new JLabel("材料牌号:");
	private JLabel CMAT_UPLabel = new JLabel("材料上标:");
	private JLabel CMAT_DOWNLabel = new JLabel("材料下标:");
	private JLabel PZGGBZHLabel = new JLabel("品种规格标准号:");
	private JLabel JDDJLabel = new JLabel("精度等级:");
	private JLabel CLZTLabel = new JLabel("材料状态:");
	private JLabel ZLDJLabel = new JLabel("质量等级:");
	private JLabel CLDWLabel = new JLabel("材料单位:");
	private JLabel ZQCLBZHLabel = new JLabel("增强材料标准号:");
	private JLabel ZQCLMCLabel = new JLabel("增强材料名称:");
	private JLabel XHPHLabel = new JLabel("型号牌号:");
	private JLabel JSTJLabel = new JLabel("技术条件:");
	private JLabel JBCLMCLabel = new JLabel("基体材料名称:");

	private JTextField materialName = new JTextField("");
	private JTextField materialCode = new JTextField("");
	private JTextField materialSpecification = new JTextField("");
	private JTextField materialStandards = new JTextField("");
	private JTextField materialInvtype = new JTextField("");
	private JTextField CMAT_UP = new JTextField("");
	private JTextField CMAT_DOWN = new JTextField("");
	private JTextField PZGGBZH = new JTextField("");
	private JTextField JDDJ = new JTextField("");
	private JTextField CLZT = new JTextField("");
	private JTextField ZLDJ = new JTextField("");
	private JTextField CLDW = new JTextField("");
	private JTextField ZQCLBZH = new JTextField("");
	private JTextField ZQCLMC = new JTextField("");
	private JTextField XHPH = new JTextField("");
	private JTextField JSTJ = new JTextField("");
	private JTextField JBCLMC = new JTextField("");

	//设计资源库应用改造新增属性
	//start
	private JLabel wzjcLabel = new JLabel("物资简称:");
	private JTextField wzjc = new JTextField("");
	private JLabel bzhLabel = new JLabel("标准号:");
	private JTextField bzh = new JTextField("");
	private JLabel ggLabel = new JLabel("规格:");
	private JTextField gg = new JTextField("");
	private JLabel clLabel = new JLabel("材料:");
	private JTextField cl = new JTextField("");
	private JLabel jxxndjLabel = new JLabel("机械性能等级或硬度:");
	private JTextField jxxndj = new JTextField("");
	private JLabel bmclLabel = new JLabel("表面处理:");
	private JTextField bmcl = new JTextField("");
	private JLabel rclLabel = new JLabel("热处理:");
	private JTextField rcl = new JTextField("");
	private JLabel cpxsLabel = new JLabel("产品型式:");
	private JTextField cpxs = new JTextField("");
	private JLabel cpdjLabel = new JLabel("产品等级:");
	private JTextField cpdj = new JTextField("");
	private JLabel bnxsLabel = new JLabel("板拧形式:");
	private JTextField bnxs = new JTextField("");
	private JLabel sfjkLabel = new JLabel("是否进口:");
	private JTextField sfjk = new JTextField("");
	private JLabel jldwLabel = new JLabel("计量单位:");
	private JTextField jldw = new JTextField("");
	private JLabel xhLabel = new JLabel("型号:");
	private JTextField xh = new JTextField("");
	private JLabel xhggLabel = new JLabel("型号规格:");
	private JTextField xhgg = new JTextField("");
	private JLabel zldjLabel = new JLabel("质量等级:");
	private JTextField zldj = new JTextField("");
	private JLabel zgfLabel = new JLabel("总规范:");
	private JTextField zgf = new JTextField("");
	private JLabel xxgfLabel = new JLabel("详细规范:");
	private JTextField xxgf = new JTextField("");
	private JLabel fzxsLabel = new JLabel("封装形式:");
	private JTextField fzxs = new JTextField("");
	private JLabel wxccLabel = new JLabel("外形尺寸:");
	private JTextField wxcc = new JTextField("");
	private JLabel zytjLabel = new JLabel("专用条件:");
	private JTextField zytj = new JTextField("");
	private JLabel fjxyLabel = new JLabel("附加协议:");
	private JTextField fjxy = new JTextField("");
	private JLabel tssmLabel = new JLabel("特殊说明:");
	private JTextField tssm = new JTextField("");
	//end
	*/

	private ShowMaterialsJPanel mJPanel;
	private ShowMaterialsJPanel cJPanel;
	private ShowMaterialsJPanel sopJPanel;

	public PartMasterJPanel(JFrame parent) {
		NewTechnicsPart.startAnimFrame.setHeaderMessage("初始化零部件信息显示面板");
		frame = parent;
		getPartImage();
		imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		imageLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1) {
					String url = ImageIntf.getCreoViewUrl(partElement.attributeValue("oid"));
					CommonUtil.openURL(url);
				}
			}
		});
		setLayout(new GridBagLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.add(basicPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(imageLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
					GridBagConstraints.NORTH, GridBagConstraints.BOTH,
					new Insets(0, 5, 0, 5), 0, 0));

			basicPanel.setLayout(new GridBagLayout());
			panel.setBorder(new TitledBorder(null, "PBOM信息", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, null));
		}

		//第1行
		//产品名称
		/*label_1.setMaximumSize(new Dimension(130, 23));
		label_1.setMinimumSize(new Dimension(130, 23));
		label_1.setPreferredSize(new Dimension(130, 23));
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label_1, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 5), 0, 0));

		productNameJLabel.setMaximumSize(new Dimension(130, 23));
		productNameJLabel.setMinimumSize(new Dimension(130, 23));
		productNameJLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(productNameJLabel, new GridBagConstraints(1, 0, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));*/

		//父件编号
		label_2.setMaximumSize(new Dimension(130, 23));
		label_2.setMinimumSize(new Dimension(130, 23));
		label_2.setPreferredSize(new Dimension(130, 23));
		label_2.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label_2, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 5), 0, 0));

		signNumberJLabel.setMaximumSize(new Dimension(130, 23));
		signNumberJLabel.setMinimumSize(new Dimension(130, 23));
		signNumberJLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(signNumberJLabel, new GridBagConstraints(3, 0, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第2行
		//零部件名称
		label_3.setMaximumSize(new Dimension(130, 23));
		label_3.setMinimumSize(new Dimension(130, 23));
		label_3.setPreferredSize(new Dimension(130, 23));
		label_3.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label_3, new GridBagConstraints(0, 1, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 5), 0, 0));

		partNameLabel.setMaximumSize(new Dimension(130, 23));
		partNameLabel.setMinimumSize(new Dimension(130, 23));
		partNameLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(partNameLabel, new GridBagConstraints(1, 1, 1, 1, 1.0,
				0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				insets, 0, 0));

		//零部件编号
		label_4.setMaximumSize(new Dimension(130, 23));
		label_4.setMinimumSize(new Dimension(130, 23));
		label_4.setPreferredSize(new Dimension(130, 23));
		label_4.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label_4, new GridBagConstraints(2, 1, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 5), 0, 0));

		partNumberLabel.setMaximumSize(new Dimension(130, 23));
		partNumberLabel.setMinimumSize(new Dimension(130, 23));
		partNumberLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(partNumberLabel, new GridBagConstraints(3, 1, 1, 1, 1.0,
				0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				insets, 0, 0));

		//第3行
		//零部件EBOM版本
		label19.setMaximumSize(new Dimension(130, 23));
		label19.setMinimumSize(new Dimension(130, 23));
		label19.setPreferredSize(new Dimension(130, 23));
		label19.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label19, new GridBagConstraints(0, 2, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 5), 0, 0));

		ebomVesionLabel.setMaximumSize(new Dimension(130, 23));
		ebomVesionLabel.setMinimumSize(new Dimension(130, 23));
		ebomVesionLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(ebomVesionLabel, new GridBagConstraints(1, 2, 1, 1, 1.0,
				0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				insets, 0, 0));

		//零部件PBOM版本
		label20.setMaximumSize(new Dimension(130, 23));
		label20.setMinimumSize(new Dimension(130, 23));
		label20.setPreferredSize(new Dimension(130, 23));
		label20.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label20, new GridBagConstraints(2, 2, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 5), 0, 0));

		pbomVesionLabel.setMaximumSize(new Dimension(130, 23));
		pbomVesionLabel.setMinimumSize(new Dimension(130, 23));
		pbomVesionLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(pbomVesionLabel, new GridBagConstraints(3, 2, 1, 1, 1.0,
				0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				insets, 0, 0));

		//第4行
		//物料类型
		label_9.setMaximumSize(new Dimension(130, 23));
		label_9.setMinimumSize(new Dimension(130, 23));
		label_9.setPreferredSize(new Dimension(130, 23));
		label_9.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label_9, new GridBagConstraints(0, 3, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 5), 0, 0));

		materialTypeLabel.setMaximumSize(new Dimension(130, 23));
		materialTypeLabel.setMinimumSize(new Dimension(130, 23));
		materialTypeLabel.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(materialTypeLabel, new GridBagConstraints(1, 3, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//zzjc,fzcj,batch,product_index
		zzjc.setMaximumSize(new Dimension(130, 23));
		zzjc.setMinimumSize(new Dimension(130, 23));
		zzjc.setPreferredSize(new Dimension(130, 23));
		zzjc.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(zzjc, new GridBagConstraints(2, 3, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 5), 0, 0));

		zzjcV.setMaximumSize(new Dimension(130, 23));
		zzjcV.setMinimumSize(new Dimension(130, 23));
		zzjcV.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(zzjcV, new GridBagConstraints(3, 3, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第5行
		fzcj.setMaximumSize(new Dimension(130, 23));
		fzcj.setMinimumSize(new Dimension(130, 23));
		fzcj.setPreferredSize(new Dimension(130, 23));
		fzcj.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(fzcj, new GridBagConstraints(0, 4, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 5), 0, 0));

		fzcjV.setMaximumSize(new Dimension(130, 23));
		fzcjV.setMinimumSize(new Dimension(130, 23));
		fzcjV.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(fzcjV, new GridBagConstraints(1, 4, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		batch.setMaximumSize(new Dimension(130, 23));
		batch.setMinimumSize(new Dimension(130, 23));
		batch.setPreferredSize(new Dimension(130, 23));
		batch.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(batch, new GridBagConstraints(2, 4, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 5), 0, 0));

		batchV.setMaximumSize(new Dimension(130, 23));
		batchV.setMinimumSize(new Dimension(130, 23));
		batchV.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(batchV, new GridBagConstraints(3, 4, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//第6行
		product_index.setMaximumSize(new Dimension(130, 23));
		product_index.setMinimumSize(new Dimension(130, 23));
		product_index.setPreferredSize(new Dimension(130, 23));
		product_index.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(product_index, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 5), 0, 0));

		product_indexV.setMaximumSize(new Dimension(130, 23));
		product_indexV.setMinimumSize(new Dimension(130, 23));
		product_indexV.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(product_indexV, new GridBagConstraints(1, 0, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));


		/*
		materialPanel.setLayout(new GridBagLayout());
		//materialPanel.setBorder(new TitledBorder(null, "材料信息",TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, null, null));

		//材料编码
		materialCodeLabel.setMaximumSize(new Dimension(130, 23));
		materialCodeLabel.setMinimumSize(new Dimension(130, 23));
		materialCodeLabel.setPreferredSize(new Dimension(130, 23));
		materialCodeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(materialCodeLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		materialCode.setEditable(false);
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
				new Insets(5, 5, 5, 5), 0, 0));

		materialName.setEditable(false);
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
		materialPanel.add(materialSpecificationLabel, new GridBagConstraints(4, 0, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		materialSpecification.setEditable(false);
		materialSpecification.setMaximumSize(new Dimension(130, 23));
		materialSpecification.setMinimumSize(new Dimension(130, 23));
		materialSpecification.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(materialSpecification, new GridBagConstraints(5, 0, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//材料标准
		materialStandardsLabel.setMaximumSize(new Dimension(130, 23));
		materialStandardsLabel.setMinimumSize(new Dimension(130, 23));
		materialStandardsLabel.setPreferredSize(new Dimension(130, 23));
		materialStandardsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(materialStandardsLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		materialStandards.setEditable(false);
		materialStandards.setMaximumSize(new Dimension(130, 23));
		materialStandards.setMinimumSize(new Dimension(130, 23));
		materialStandards.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(materialStandards, new GridBagConstraints(1, 1, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//材料牌号
		materialInvtypeLabel.setMaximumSize(new Dimension(130, 23));
		materialInvtypeLabel.setMinimumSize(new Dimension(130, 23));
		materialInvtypeLabel.setPreferredSize(new Dimension(130, 23));
		materialInvtypeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(materialInvtypeLabel, new GridBagConstraints(2, 1, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		materialInvtype.setEditable(false);
		materialInvtype.setMaximumSize(new Dimension(130, 23));
		materialInvtype.setMinimumSize(new Dimension(130, 23));
		materialInvtype.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(materialInvtype, new GridBagConstraints(3, 1, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//材料上标
		CMAT_UPLabel.setMaximumSize(new Dimension(130, 23));
		CMAT_UPLabel.setMinimumSize(new Dimension(130, 23));
		CMAT_UPLabel.setPreferredSize(new Dimension(130, 23));
		CMAT_UPLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(CMAT_UPLabel, new GridBagConstraints(4, 1, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		CMAT_UP.setEditable(false);
		CMAT_UP.setMaximumSize(new Dimension(130, 23));
		CMAT_UP.setMinimumSize(new Dimension(130, 23));
		CMAT_UP.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(CMAT_UP, new GridBagConstraints(5, 1, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//材料下标
		CMAT_DOWNLabel.setMaximumSize(new Dimension(130, 23));
		CMAT_DOWNLabel.setMinimumSize(new Dimension(130, 23));
		CMAT_DOWNLabel.setPreferredSize(new Dimension(130, 23));
		CMAT_DOWNLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(CMAT_DOWNLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		CMAT_DOWN.setEditable(false);
		CMAT_DOWN.setMaximumSize(new Dimension(130, 23));
		CMAT_DOWN.setMinimumSize(new Dimension(130, 23));
		CMAT_DOWN.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(CMAT_DOWN, new GridBagConstraints(1, 2, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//品种规格标准号
		PZGGBZHLabel.setMaximumSize(new Dimension(130, 23));
		PZGGBZHLabel.setMinimumSize(new Dimension(130, 23));
		PZGGBZHLabel.setPreferredSize(new Dimension(130, 23));
		PZGGBZHLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(PZGGBZHLabel, new GridBagConstraints(2, 2, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		PZGGBZH.setEditable(false);
		PZGGBZH.setMaximumSize(new Dimension(130, 23));
		PZGGBZH.setMinimumSize(new Dimension(130, 23));
		PZGGBZH.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(PZGGBZH, new GridBagConstraints(3, 2, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//精度等级
		JDDJLabel.setMaximumSize(new Dimension(130, 23));
		JDDJLabel.setMinimumSize(new Dimension(130, 23));
		JDDJLabel.setPreferredSize(new Dimension(130, 23));
		JDDJLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(JDDJLabel, new GridBagConstraints(4, 2, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		JDDJ.setEditable(false);
		JDDJ.setMaximumSize(new Dimension(130, 23));
		JDDJ.setMinimumSize(new Dimension(130, 23));
		JDDJ.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(JDDJ, new GridBagConstraints(5, 2, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//材料状态
		CLZTLabel.setMaximumSize(new Dimension(130, 23));
		CLZTLabel.setMinimumSize(new Dimension(130, 23));
		CLZTLabel.setPreferredSize(new Dimension(130, 23));
		CLZTLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(CLZTLabel, new GridBagConstraints(0, 3, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		CLZT.setEditable(false);
		CLZT.setMaximumSize(new Dimension(130, 23));
		CLZT.setMinimumSize(new Dimension(130, 23));
		CLZT.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(CLZT, new GridBagConstraints(1, 3, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//质量等级
		ZLDJLabel.setMaximumSize(new Dimension(130, 23));
		ZLDJLabel.setMinimumSize(new Dimension(130, 23));
		ZLDJLabel.setPreferredSize(new Dimension(130, 23));
		ZLDJLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(ZLDJLabel, new GridBagConstraints(2, 3, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		ZLDJ.setEditable(false);
		ZLDJ.setMaximumSize(new Dimension(130, 23));
		ZLDJ.setMinimumSize(new Dimension(130, 23));
		ZLDJ.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(ZLDJ, new GridBagConstraints(3, 3, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//材料单位
		CLDWLabel.setMaximumSize(new Dimension(130, 23));
		CLDWLabel.setMinimumSize(new Dimension(130, 23));
		CLDWLabel.setPreferredSize(new Dimension(130, 23));
		CLDWLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(CLDWLabel, new GridBagConstraints(4, 3, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		CLDW.setEditable(false);
		CLDW.setMaximumSize(new Dimension(130, 23));
		CLDW.setMinimumSize(new Dimension(130, 23));
		CLDW.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(CLDW, new GridBagConstraints(5, 3, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//增强材料标准号
		ZQCLBZHLabel.setMaximumSize(new Dimension(130, 23));
		ZQCLBZHLabel.setMinimumSize(new Dimension(130, 23));
		ZQCLBZHLabel.setPreferredSize(new Dimension(130, 23));
		ZQCLBZHLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(ZQCLBZHLabel, new GridBagConstraints(0, 4, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		ZQCLBZH.setEditable(false);
		ZQCLBZH.setMaximumSize(new Dimension(130, 23));
		ZQCLBZH.setMinimumSize(new Dimension(130, 23));
		ZQCLBZH.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(ZQCLBZH, new GridBagConstraints(1, 4, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//增强材料名称
		ZQCLMCLabel.setMaximumSize(new Dimension(130, 23));
		ZQCLMCLabel.setMinimumSize(new Dimension(130, 23));
		ZQCLMCLabel.setPreferredSize(new Dimension(130, 23));
		ZQCLMCLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(ZQCLMCLabel, new GridBagConstraints(2, 4, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		ZQCLMC.setEditable(false);
		ZQCLMC.setMaximumSize(new Dimension(130, 23));
		ZQCLMC.setMinimumSize(new Dimension(130, 23));
		ZQCLMC.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(ZQCLMC, new GridBagConstraints(3, 4, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//材料牌号
		XHPHLabel.setMaximumSize(new Dimension(130, 23));
		XHPHLabel.setMinimumSize(new Dimension(130, 23));
		XHPHLabel.setPreferredSize(new Dimension(130, 23));
		XHPHLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(XHPHLabel, new GridBagConstraints(4, 4, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		XHPH.setEditable(false);
		XHPH.setMaximumSize(new Dimension(130, 23));
		XHPH.setMinimumSize(new Dimension(130, 23));
		XHPH.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(XHPH, new GridBagConstraints(5, 4, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//材料牌号
		JSTJLabel.setMaximumSize(new Dimension(130, 23));
		JSTJLabel.setMinimumSize(new Dimension(130, 23));
		JSTJLabel.setPreferredSize(new Dimension(130, 23));
		JSTJLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(JSTJLabel, new GridBagConstraints(0, 5, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		JSTJ.setEditable(false);
		JSTJ.setMaximumSize(new Dimension(130, 23));
		JSTJ.setMinimumSize(new Dimension(130, 23));
		JSTJ.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(JSTJ, new GridBagConstraints(1, 5, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//基体材料名称
		JBCLMCLabel.setMaximumSize(new Dimension(130, 23));
		JBCLMCLabel.setMinimumSize(new Dimension(130, 23));
		JBCLMCLabel.setPreferredSize(new Dimension(130, 23));
		JBCLMCLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(JBCLMCLabel, new GridBagConstraints(2, 5, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		JBCLMC.setEditable(false);
		JBCLMC.setMaximumSize(new Dimension(130, 23));
		JBCLMC.setMinimumSize(new Dimension(130, 23));
		JBCLMC.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(JBCLMC, new GridBagConstraints(3, 5, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//物资简称
		wzjcLabel.setMaximumSize(new Dimension(130, 23));
		wzjcLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(wzjcLabel, new GridBagConstraints(0, 6, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		wzjc.setEditable(false);
		wzjc.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(wzjc, new GridBagConstraints(1, 6, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//标准号
		bzhLabel.setMaximumSize(new Dimension(130, 23));
		bzhLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(bzhLabel, new GridBagConstraints(2, 6, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		bzh.setEditable(false);
		bzh.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(bzh, new GridBagConstraints(3, 6, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//规格
		ggLabel.setMaximumSize(new Dimension(130, 23));
		ggLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(ggLabel, new GridBagConstraints(4, 6, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		gg.setEditable(false);
		gg.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(gg, new GridBagConstraints(5, 6, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//材料
		clLabel.setMaximumSize(new Dimension(130, 23));
		clLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(clLabel, new GridBagConstraints(0, 7, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		cl.setEditable(false);
		cl.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(cl, new GridBagConstraints(1, 7, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//机械性能等级或硬度
		jxxndjLabel.setMaximumSize(new Dimension(130, 23));
		jxxndjLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(jxxndjLabel, new GridBagConstraints(2, 7, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		jxxndj.setEditable(false);
		jxxndj.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(jxxndj, new GridBagConstraints(3, 7, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//表面处理
		bmclLabel.setMaximumSize(new Dimension(130, 23));
		bmclLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(bmclLabel, new GridBagConstraints(4, 7, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		bmcl.setEditable(false);
		bmcl.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(bmcl, new GridBagConstraints(5, 7, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//热处理
		rclLabel.setMaximumSize(new Dimension(130, 23));
		rclLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(rclLabel, new GridBagConstraints(0, 8, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		rcl.setEditable(false);
		rcl.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(rcl, new GridBagConstraints(1, 8, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//产品型式
		cpxsLabel.setMaximumSize(new Dimension(130, 23));
		cpxsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(cpxsLabel, new GridBagConstraints(2, 8, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		cpxs.setEditable(false);
		cpxs.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(cpxs, new GridBagConstraints(3, 8, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//产品等级
		cpdjLabel.setMaximumSize(new Dimension(130, 23));
		cpdjLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(cpdjLabel, new GridBagConstraints(4, 8, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		cpdj.setEditable(false);
		cpdj.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(cpdj, new GridBagConstraints(5, 8, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//板拧形式
		bnxsLabel.setMaximumSize(new Dimension(130, 23));
		bnxsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(bnxsLabel, new GridBagConstraints(0, 9, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		bnxs.setEditable(false);
		bnxs.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(bnxs, new GridBagConstraints(1, 9, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//是否进口
		sfjkLabel.setMaximumSize(new Dimension(130, 23));
		sfjkLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(sfjkLabel, new GridBagConstraints(2, 9, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		sfjk.setEditable(false);
		sfjk.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(sfjk, new GridBagConstraints(3, 9, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//计量单位
		jldwLabel.setMaximumSize(new Dimension(130, 23));
		jldwLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(jldwLabel, new GridBagConstraints(4, 9, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		jldw.setEditable(false);
		jldw.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(jldw, new GridBagConstraints(5, 9, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//特殊说明
		tssmLabel.setMaximumSize(new Dimension(130, 23));
		tssmLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(tssmLabel, new GridBagConstraints(0, 10, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		tssm.setEditable(false);
		tssm.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(tssm, new GridBagConstraints(1, 10, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//型号
		xhLabel.setMaximumSize(new Dimension(130, 23));
		xhLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(xhLabel, new GridBagConstraints(2, 10, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		xh.setEditable(false);
		xh.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(xh, new GridBagConstraints(3, 10, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//型号规格
		xhggLabel.setMaximumSize(new Dimension(130, 23));
		xhggLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(xhggLabel, new GridBagConstraints(4, 10, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		xhgg.setEditable(false);
		xhgg.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(xhgg, new GridBagConstraints(5, 10, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//质量等级
		zldjLabel.setMaximumSize(new Dimension(130, 23));
		zldjLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(zldjLabel, new GridBagConstraints(0, 11, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		zldj.setEditable(false);
		zldj.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(zldj, new GridBagConstraints(1, 11, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//总规范
		zgfLabel.setMaximumSize(new Dimension(130, 23));
		zgfLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(zgfLabel, new GridBagConstraints(2, 11, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		zgf.setEditable(false);
		zgf.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(zgf, new GridBagConstraints(3, 11, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//详细规范
		xxgfLabel.setMaximumSize(new Dimension(130, 23));
		xxgfLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(xxgfLabel, new GridBagConstraints(4, 11, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		xxgf.setEditable(false);
		xxgf.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(xxgf, new GridBagConstraints(5, 11, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//封装形式
		fzxsLabel.setMaximumSize(new Dimension(130, 23));
		fzxsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(fzxsLabel, new GridBagConstraints(0, 12, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		fzxs.setEditable(false);
		fzxs.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(fzxs, new GridBagConstraints(1, 12, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//外形尺寸
		wxccLabel.setMaximumSize(new Dimension(130, 23));
		wxccLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(wxccLabel, new GridBagConstraints(2, 12, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		wxcc.setEditable(false);
		wxcc.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(wxcc, new GridBagConstraints(3, 12, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//专用条件
		zytjLabel.setMaximumSize(new Dimension(130, 23));
		zytjLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(zytjLabel, new GridBagConstraints(4, 12, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		zytj.setEditable(false);
		zytj.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(zytj, new GridBagConstraints(5, 12, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		//附加协议
		fjxyLabel.setMaximumSize(new Dimension(130, 23));
		fjxyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		materialPanel.add(fjxyLabel, new GridBagConstraints(0, 13, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		fjxy.setEditable(false);
		fjxy.setPreferredSize(new Dimension(130, 23));
		materialPanel.add(fjxy, new GridBagConstraints(1, 13, 1, 1,
				1.0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		JScrollPane jsp = new JScrollPane();
		jsp.setViewportView(materialPanel);
		jsp.setBorder(BorderFactory.createTitledBorder("材料信息"));
		jsp.setPreferredSize(new Dimension(600, 200));
		*/

		cJPanel = new ShowMaterialsJPanel((NewTechnicsPart)frame);
		add(cJPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 5, 0, 5), 0, 0));

		mJPanel = new ShowMaterialsJPanel((NewTechnicsPart)frame);
		add(mJPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 5, 0, 5), 0, 0));
		sopJPanel = new ShowMaterialsJPanel((NewTechnicsPart)frame);
		add(sopJPanel, new GridBagConstraints(0, 3, 1, 1, 1.0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 5, 0, 5), 0, 0));
	}

	/**
	 * 根据传入的零部件信息标签设置该标签下的各个属性
	 *
	 * @param partElement
	 */
	private void setPartElementAttributeValue(Element partElement) {
		Element productElement = partElement.getDocument().getRootElement();
//		XmlUtility.setAttributeValue(productElement, "productName", productNameJLabel.getText());
		try {
			Element rootPart = BomXMLUtil.getMainPart(BomXMLUtil.getProduct(partElement.getDocument()));
			XmlUtility.setAttributeValue(rootPart, "partNumber", signNumberJLabel.getText());
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame, "获得产品信息时出现错误！");
		}
		XmlUtility.setAttributeValue(partElement, "partName", partNameLabel.getText());
		XmlUtility.setAttributeValue(partElement, "partNumber", partNumberLabel.getText());
		XmlUtility.setAttributeValue(partElement, "eu_version", numberVersionLabel.getText());
		XmlUtility.setAttributeValue(partElement, "e_version", ebomVesionLabel.getText());
		XmlUtility.setAttributeValue(partElement, "version", pbomVesionLabel.getText());
		if (isKeyLabel.getText().equals("是"))
			XmlUtility.setAttributeValue(partElement, "isKey", "true");
		else
			XmlUtility.setAttributeValue(partElement, "isKey", "false");
		if (isSpecialKeyLabel.getText().equals("是"))
			XmlUtility.setAttributeValue(partElement, "isSpecial", "true");
		else
			XmlUtility.setAttributeValue(partElement, "isSpecial", "false");
		XmlUtility.setAttributeValue(partElement, "workShop", mainWorkShopLabel.getText());
		XmlUtility.setAttributeValue(partElement, "materialType", materialTypeLabel.getText());
		XmlUtility.setAttributeValue(partElement, "backupRate", backupRateLabel.getText());
		XmlUtility.setAttributeValue(partElement, "maxBackupCount", maxBackupCountLabel.getText());
		XmlUtility.setAttributeValue(partElement, "backupReason", backUpDescriptionLabel.getText());
		XmlUtility.setAttributeValue(partElement, "remark", remark.getText());

	}

	/**
	 * 根据传入的零部件信息为界面的各个组件赋值
	 *
	 * @param partElement
	 */
	public void setUIValues(Element partElement) {
		this.partElement = partElement;
		Element productElement = partElement.getDocument().getRootElement();
//		productNameJLabel.setText(productElement.attributeValue("productName"));

		try {
			Element rootPart = BomXMLUtil.getMainPart(BomXMLUtil
					.getProduct(partElement.getDocument()));
			signNumberJLabel.setText(rootPart.attributeValue("partNumber"));
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame, "获得产品信息时出现错误！");
		}
		//TODO 149材料属性
		String clbm = partElement.attributeValue("CLBM");
		String wzbm = partElement.attributeValue("WZBM");

		if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
		    cJPanel.setVisible(false);
            mJPanel.setVisible(false);
            sopJPanel.setValues(partElement, false, true);
		}else{
            cJPanel.setValues(partElement, false, false);
            mJPanel.setValues(partElement, true, false);
		}


		//zzjc,fzcj,batch,product_index
		zzjcV.setText(partElement.attributeValue("ZZCJ"));
		fzcjV.setText(partElement.attributeValue("FZCJ"));
		batchV.setText(partElement.attributeValue("BATCH"));
		product_indexV.setText(partElement.attributeValue("PRODUCT_INDEX"));

		/*
		materialCode.setText(partElement.attributeValue("CMAT"));
		materialName.setText(partElement.attributeValue("PTC_MATERIAL_NAME"));
		materialInvtype.setText(partElement.attributeValue("XHPHCL"));
		materialSpecification.setText(partElement.attributeValue("CSIZE"));
		materialStandards.setText(partElement.attributeValue("JSTJBZH"));

		CMAT_UP.setText(partElement.attributeValue("CMAT_UP"));
		CMAT_DOWN.setText(partElement.attributeValue("CMAT_DOWN"));
		PZGGBZH.setText(partElement.attributeValue("PZGGBZH"));
		JDDJ.setText(partElement.attributeValue("JDDJ"));
		CLZT.setText(partElement.attributeValue("CLZT"));
		ZLDJ.setText(partElement.attributeValue("ZLDJ"));
		CLDW.setText(partElement.attributeValue("ZQCLBZH"));
		ZQCLBZH.setText(partElement.attributeValue("ZQCLBZH"));
		ZQCLMC.setText(partElement.attributeValue("ZQCLMC"));
		XHPH.setText(partElement.attributeValue("XHPH"));
		JSTJ.setText(partElement.attributeValue("JSTJ"));
		JBCLMC.setText(partElement.attributeValue("JBCLMC"));

		//设计资源库应用改造新增属性
		//start
		wzjc.setText(partElement.attributeValue("SHORTNAME"));//物资简称
		bzh.setText(partElement.attributeValue("STANDARDNUMBER"));//标准号
		gg.setText(partElement.attributeValue("STANDARD"));//规格
		cl.setText(partElement.attributeValue("MATERIAL"));//材料
		jxxndj.setText(partElement.attributeValue("MECHANICALPROPERTYORHARDNESS"));//机械性能等级或硬度
		bmcl.setText(partElement.attributeValue("SURFACETREATMENT"));//表面处理
		rcl.setText(partElement.attributeValue("HEATTREATMENT"));//热处理
		cpxs.setText(partElement.attributeValue("PRODUCTFORM"));//产品型式
		cpdj.setText(partElement.attributeValue("PRODUCTLEVEL"));//产品等级
		bnxs.setText(partElement.attributeValue("PLATECSCREWFORM"));//板拧形式
		sfjk.setText(partElement.attributeValue("ISIMPORT"));//是否进口
		tssm.setText(partElement.attributeValue("SPECIALINSTRUCTION"));//特殊说明
		jldw.setText(partElement.attributeValue("MEASUREUNIT"));//计量单位
		xh.setText(partElement.attributeValue("TYPE"));//型号
		xhgg.setText(partElement.attributeValue("TYPESTANDARD"));//型号规格
		zldj.setText(partElement.attributeValue("QUALITYLEVEL"));//质量等级
		zgf.setText(partElement.attributeValue("TOTALSTANDARD"));//总规范
		xxgf.setText(partElement.attributeValue("DETAILSTANDARD"));//详细规范
		fzxs.setText(partElement.attributeValue("PACKAGINGFORM"));//封装形式
		wxcc.setText(partElement.attributeValue("OUTLINESIZE"));//外形尺寸
		zytj.setText(partElement.attributeValue("SPECIALCONDITION"));//专用条件
		fjxy.setText(partElement.attributeValue("EXTRACONDITION"));//附加协议
		//end

		*/

		partNameLabel.setText(partElement.attributeValue("partName"));
		partNumberLabel.setText(partElement.attributeValue("partNumber"));

		numberVersionLabel.setText(partElement.attributeValue("eu_version"));
		ebomVesionLabel.setText(partElement.attributeValue("e_version"));
		pbomVesionLabel.setText(partElement.attributeValue("version"));

		String isKey = partElement.attributeValue("isKey");
		if (isKey != null && isKey.equals("true")) {
			isKeyLabel.setText("是");
		} else {
			isKeyLabel.setText("否");
		}
		String isSpecial = partElement.attributeValue("isSpecial");
		if (isSpecial != null && isSpecial.equals("true")) {
			isSpecialKeyLabel.setText("是");
		} else {
			isSpecialKeyLabel.setText("否");
		}

		mainWorkShopLabel.setText(partElement.attributeValue("workShop"));
		materialTypeLabel.setText(partElement.attributeValue("MTYPE"));
		backupRateLabel.setText(partElement.attributeValue("backupRate"));
		maxBackupCountLabel.setText(partElement.attributeValue("maxBackupCount"));
		backUpDescriptionLabel.setText(partElement.attributeValue("backupReason"));
		remark.setText(partElement.attributeValue("remark"));

		getPartImage();
	}

	public Element getElement() {
		if (partElement != null) {
			Element ele = (Element) partElement.clone();
			return ele;
		}
		Element partElement = XmlUtility.createPart();
		setPartElementAttributeValue(partElement);
		return partElement;
	}

	public void clearUI() {
		node = null;
		partElement = null;
//		productNameJLabel.setText("");
		signNumberJLabel.setText("");
		partNameLabel.setText("");
		partNumberLabel.setText("");
		// partVersionLabel.setText("");
		isKeyLabel.setText("");
		isSpecialKeyLabel.setText("");
		mainWorkShopLabel.setText("");
		materialTypeLabel.setText("");
		backupRateLabel.setText("");
		maxBackupCountLabel.setText("");
		backUpDescriptionLabel.setText("");
		remark.setText("");
		numberVersionLabel.setText("");
		ebomVesionLabel.setText("");
		pbomVesionLabel.setText("");
		imageLabel.setIcon(new ImageIcon());
	}

	public void setNode(XWTreeNode node) {
		this.node = node;
	}

	public XWTreeNode getNode() {
		return node;
	}

	public void getPartImage() {
		if (partElement == null) {
			return;
		}
		final String oid = partElement.attributeValue("oid");
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