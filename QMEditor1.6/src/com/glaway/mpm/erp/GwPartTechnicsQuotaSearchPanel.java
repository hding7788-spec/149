package com.glaway.mpm.erp;

import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.XWTechnicsTreeObject;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.view.XWTreeObject;
import ext.ases.techMaterial.bean.*;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.List;
import java.util.*;

/**
 * erp 物资库查询panel
 * @author Administrator
 *
 */
public class GwPartTechnicsQuotaSearchPanel extends CmAbstractPanel implements DocumentListener {

	private static final long serialVersionUID = 1L;

	private ZTableOp tableOp;

	private String[] tableHeader = null;

	private String[][] tableBody ;

	private int[] tableColWidth ;

	private JPanel topPanel;
	private JPanel topTopPanel;

	private JPanel wzkPanel;

	private JLabel databaseLable;
	private KVJComboBox databaseComboBox; //基础数据库
	private KVJComboBox wzlbComboBox; //物资类别
	private KVJComboBox wzlbComboBox2; //物资类别小类

	private Vector databaseModel ; //
	private Vector wzlbMode;
	private Vector<KVItem> wzlbMode2;
	//设计编码
	private JLabel sjbmLable;
	private JTextField sjbmText;
	//物资编码
	private JLabel wzbmLable;
	private JTextField wzbmText;
	//物资名称
	private JLabel chmcLable;
	private JTextField wzmcText;
	//物资简称
	private JLabel wzjcLabel;
	private JTextField wzjcText;
	//编码优选级别
	private JLabel bmyxjbLabel;
	private JTextField bmyxjbText;
	//编码状态
	private JLabel bmztLabel;
	private JTextField bmztText;
	//编码类型
	private JLabel bmlxLabel;
	private JTextField bmlxText;
	//编码等级
	private JLabel bmdjLabel;
	private JTextField bmdjText;
	//换算率
	private JLabel hslLabel;
	private JTextField hslText;
	//系数
	private JLabel xsLabel;
	private JTextField xsText;
	//牌号
	private JLabel phLabel;
	private JTextField phText;
	//规格
	private JLabel ggLabel;
	private JTextField ggText;
	//采用标准
	private JLabel cybzLabel;
	private JTextField cybzText;
	//特殊说明
	private JLabel tssmLabel;
	private JTextField tssmText;
	//是否进口
	private JLabel sfjkLabel;
	private JTextField sfjkText;
	//型号规格
	private JLabel xhggLabel;
	private JTextField xhggText;
	//质量等级
	private JLabel zldjLabel;
	private JTextField zldjText;
	//总规范
	private JLabel zgfLabel;
	private JTextField zgfText;
	//详细规范
	private JLabel xxgfLabel;
	private JTextField  xxgfText;
	//型号
	private JLabel xhLabel;
	private JTextField  xhText;
	//封装形式
	private JLabel fzxsLabel;
	private JTextField  fzxsText;
	//外形尺寸
	private JLabel wxccLabel;
	private JTextField  wxccText;
	//专用条件
	private JLabel zytjLabel;
	private JTextField zytjText;
	//附加协议
	private JLabel fjxyLabel;
	private JTextField fjxyText;
	//抗辐射指标TID
	private JLabel kfszbtidLabel;
	private JTextField kfszbtidText;
	//抗辐射指标SEE
	private JLabel kfszbseeLabel;
	private JTextField kfszbseeText;
	//性能参数
	private JLabel xncsLabel;
	private JTextField xncsText;
	//是否静电敏感
	private JLabel sfjdmgLabel;
	private JTextField sfjdmgText;
	//静电敏感等级
	private JLabel jdmgdjLabel;
	private JTextField jdmgdjText;
	//湿敏等级
	private JLabel smdjLabel;
	private JTextField smdjText;
	//供应状态
	private JLabel gyztLabel;
	private JTextField gyztText;
	//精度
	private JLabel jdLabel;
	private JTextField jdText;
	//质量特征
	private JLabel zltzLabel;
	private JTextField zltzText;
	//品种规格标准
	private JLabel pzggbzLabel;
	private JTextField pzggbzText;
	//标准号
	private JLabel bzhLabel;
	private JTextField bzhText;
	//材料
	private JLabel clLabel;
	private JTextField clText;
	//机械性能等级或硬度
	private JLabel jxxndjLabel;
	private JTextField jxxndjText;
	//表面处理
	private JLabel bmclLabel;
	private JTextField bmclText;
	//热处理
	private JLabel rclLabel;
	private JTextField rclText;
	//产品型式
	private JLabel cpxsLabel;
	private JTextField cpxsText;
	//产品等级
	private JLabel cpdjLabel;
	private JTextField cpdjText;
	//板拧形式
	private JLabel nbxsLabel;
	private JTextField nbxsText;
	//产品代号
	private JLabel cpdhLabel;
	private JTextField cpdhText;
	//重量
	private JLabel zlLabel;
	private JTextField zlText;
	//贮存寿命
	private JLabel zcsmLabel;
	private JTextField zcsmText;
	//TNT
	private JLabel tntLabel;
	private JTextField tntText;

	//生产厂家
	private JLabel sccjLabel;
	private JTextField sccjText;
	//计量单位
	private JLabel jldwLabel;
	private JTextField jldwText;
	//工艺单位
	private JLabel gydwLabel;
	private JTextField gydwText;

	//记录总数量
	private JLabel  jlzsLable;

	public  JTextArea wzkDescLable;

	private JCheckBox autoQuery;
	private JCheckBox isTypeC;
	private XWTreeNode treeNode;

	private JButton queryButton;

	private long jlzs;

	private int opType;

	public LinkedHashMap<JTextField, String> componentMap ;
	public LinkedHashMap<JTextField, String> standardMap ;
	public LinkedHashMap<JTextField, String> materialMap ;
	public LinkedHashMap<JTextField, String> nonMaterialMap ;
	public LinkedHashMap<JTextField, String> compoundMap ;
	public LinkedHashMap<JTextField, String> jdclMap ;
	public LinkedHashMap<JTextField, String> hgpMap ;

	public GwPartTechQuotaDialog gwPartTechQuotaDialog;

	/**
	 *
	 * String转map
	 * @param str
	 * @return
	 */
	public static Map<String,String> getStringToMap(String str){
		//根据逗号截取字符串数组
		String[] str1 = str.split(",");
		//创建Map对象
		Map<String,String> map = new HashMap<String,String>();
		//循环加入map集合
		for (int i = 0; i < str1.length; i++) {
			//根据":"截取字符串数组
			String[] str2 = str1[i].split(":");
			//str2[0]为KEY,str2[1]为值
			map.put(str2[0],str2[1]);
		}
		return map;
	}

	//"物资类别","物资编码","物资名称","物资简称","编码优选级别","编码状态","编码类型","编码等级","换算率","系数","牌号","供应状态","采用标准","精度","质量特征","品种规格标准"
	private String wzlb, wzbm, wzmc, wzjc,bmyxjb,bmzt,bmlx,bmdj,hsl,xs,ph,gyzt,cybz,jd,zltz,pzggbz;

	private List<Wzk> dataList=new ArrayList<Wzk>();

	public GwPartTechnicsQuotaSearchPanel(GwPartTechQuotaDialog gwPartTechQuotaDialog,int opType, XWTreeNode node){
		this.opType = opType;
		this.gwPartTechQuotaDialog=gwPartTechQuotaDialog;
		treeNode=node;
		initTableInfo("component");
		loadInitDatas();
		initDimension();
		initComponents();
		initAttrCollect();
		initActions();
		initLayout("component");
		init();
	}

 public void initAttrCollect(){
	 componentMap= new LinkedHashMap<JTextField, String>();
	 componentMap.put(wzmcText,"WZMC");
	 componentMap.put(xhggText,"XHGG");
	 componentMap.put(zldjText,"ZLDJ");
	 componentMap.put(sccjText,"SCCJ");
	 componentMap.put(jldwText,"JLDW");
	 componentMap.put(zgfText,"ZGF");
	 componentMap.put(xxgfText,"XXGF");
	 componentMap.put(xhText,"XH");
	 componentMap.put(fzxsText,"FZXS");
	 componentMap.put(wxccText,"WXCC");
	 componentMap.put(zytjText,"ZYTJ");
	 componentMap.put(fjxyText,"FJXY");
	 componentMap.put(tssmText,"TSSM");
	 componentMap.put(sfjkText,"SFJK");
	 componentMap.put(kfszbtidText,"KFSZBTID");
	 componentMap.put(kfszbseeText,"KFSZBSEE");
	 componentMap.put(xncsText,"XNCS");
	 componentMap.put(sfjdmgText,"SFJDMG");
	 componentMap.put(jdmgdjText,"JDMGDJ");
	 componentMap.put(smdjText,"SMDJ");
	 componentMap.put(sjbmText,"SJBM");
	 componentMap.put(wzbmText,"WZBM");
	 componentMap.put(wzjcText,"WZJC");
	 componentMap.put(bmyxjbText,"BMYXJB");
	 componentMap.put(bmztText,"BMZT");
	 componentMap.put(bmlxText,"BMLX");
	 componentMap.put(bmdjText,"BMDJ");

	 standardMap = new LinkedHashMap<JTextField, String>();
	 standardMap.put(wzmcText,"WZMC");
	 standardMap.put(ggText,"GG");
	 standardMap.put(bzhText,"BZH");
	 standardMap.put(jxxndjText,"JXXNDJ");
	 standardMap.put(jldwText,"JLDW");
	 standardMap.put(clText,"CL");
	 standardMap.put(bmclText,"BMCL");
	 standardMap.put(rclText,"RCL");
	 standardMap.put(sccjText,"SCCJ");
	 standardMap.put(cpxsText,"CPXS");
	 standardMap.put(cpdjText,"CPDJ");
	 standardMap.put(nbxsText,"NBXS");
	 standardMap.put(tssmText,"TSSM");
	 standardMap.put(sfjkText,"SFJK");
	 standardMap.put(sjbmText,"SJBM");
	 standardMap.put(wzbmText,"WZBM");
	 standardMap.put(wzjcText,"WZJC");
	 standardMap.put(bmyxjbText,"BMYXJB");
	 standardMap.put(bmztText,"BMZT");
	 standardMap.put(bmlxText,"BMLX");
	 standardMap.put(bmdjText,"BMDJ");

	  materialMap= new LinkedHashMap<JTextField, String>();
	 materialMap.put(wzmcText,"WZMC");
	 materialMap.put(phText,"PH");
	 materialMap.put(ggText,"GG");
	 materialMap.put(gyztText,"GYZT");
	 materialMap.put(cybzText,"CYBZ");
	 materialMap.put(jldwText,"JLDW");
	 materialMap.put(gydwText,"GYDW");
	 materialMap.put(pzggbzText,"PZGGBZ");
	 materialMap.put(jdText,"JD");
	 materialMap.put(zltzText,"ZLTZ");
	 materialMap.put(sccjText,"SCCJ");
	 materialMap.put(wzjcText,"WZJC");
	 materialMap.put(tssmText,"TSSM");
	 materialMap.put(sfjkText,"SFJK");
	 materialMap.put(hslText,"HSL");
	 materialMap.put(xsText,"XS");
	 materialMap.put(sjbmText,"SJBM");
	 materialMap.put(wzbmText,"WZBM");
	 materialMap.put(bmyxjbText,"BMYXJB");
	 materialMap.put(bmztText,"BMZT");
	 materialMap.put(bmlxText,"BMLX");
	 materialMap.put(bmdjText,"BMDJ");

	 nonMaterialMap= new LinkedHashMap<JTextField, String>();
	 nonMaterialMap.put(wzmcText,"WZMC");
	 nonMaterialMap.put(phText,"PH");
	 nonMaterialMap.put(ggText,"GG");
	 nonMaterialMap.put(cybzText,"CYBZ");
	 nonMaterialMap.put(jldwText,"JLDW");
	 nonMaterialMap.put(gydwText,"GYDW");
	 nonMaterialMap.put(sccjText,"SCCJ");
	 nonMaterialMap.put(tssmText,"TSSM");
	 nonMaterialMap.put(sfjkText,"SFJK");
	 nonMaterialMap.put(wzjcText,"WZJC");
	 nonMaterialMap.put(hslText,"HSL");
	 nonMaterialMap.put(xsText,"XS");
	 nonMaterialMap.put(sjbmText,"SJBM");
	 nonMaterialMap.put(wzbmText,"WZBM");
	 nonMaterialMap.put(bmyxjbText,"BMYXJB");
	 nonMaterialMap.put(bmztText,"BMZT");
	 nonMaterialMap.put(bmlxText,"BMLX");
	 nonMaterialMap.put(bmdjText,"BMDJ");
	 compoundMap= new LinkedHashMap<JTextField, String>();
	 compoundMap.put(wzmcText,"WZMC");
	 compoundMap.put(phText,"PH");
	 compoundMap.put(ggText,"GG");
	 compoundMap.put(cybzText,"CYBZ");
	 compoundMap.put(jldwText,"JLDW");
	 compoundMap.put(sccjText,"SCCJ");
	 compoundMap.put(tssmText,"TSSM");
	 compoundMap.put(sfjkText,"SFJK");
	 compoundMap.put(wzjcText,"WZJC");
	 compoundMap.put(hslText,"HSL");
	 compoundMap.put(xsText,"XS");
	 compoundMap.put(sjbmText,"SJBM");
	 compoundMap.put(wzbmText,"WZBM");
	 compoundMap.put(bmyxjbText,"BMYXJB");
	 compoundMap.put(bmztText,"BMZT");
	 compoundMap.put(bmlxText,"BMLX");
	 compoundMap.put(bmdjText,"BMDJ");
	 //机电材料
	 jdclMap= new LinkedHashMap<JTextField, String>();
	 jdclMap.put(wzmcText,"WZMC");
	 jdclMap.put(phText,"PH");
	 jdclMap.put(bzhText,"BZH");
	 jdclMap.put(xhggText,"XHGG");
	 jdclMap.put(jldwText,"JLDW");
	 jdclMap.put(sccjText,"SCCJ");
	 jdclMap.put(sjbmText,"SJBM");
	 jdclMap.put(wzbmText,"WZBM");
	 jdclMap.put(bmdjText,"BMDJ");
	 jdclMap.put(sfjkText,"SFJK");
	 jdclMap.put(xncsText,"XNCS");
	 jdclMap.put(tssmText,"TSSM");
	 jdclMap.put(bmztText,"BMZT");

	 //火工品
	 hgpMap= new LinkedHashMap<JTextField, String>();
	 hgpMap.put(wzmcText,"WZMC");
	 hgpMap.put(bzhText,"BZH");
	 hgpMap.put(sccjText,"SCCJ");
	 hgpMap.put(sjbmText,"SJBM");
	 hgpMap.put(wzbmText,"WZBM");
	 hgpMap.put(jldwText,"JLDW");
	 hgpMap.put(bmdjText,"BMDJ");
	 hgpMap.put(cpdhText,"CPDH");
	 hgpMap.put(zlText,"ZL");
	 hgpMap.put(zcsmText,"ZCSM");
	 hgpMap.put(tntText,"TNT");
	 hgpMap.put(xncsText,"XNCS");
	 hgpMap.put(tssmText,"TSSM");
	 hgpMap.put(bmztText,"BMZT");

 }

	private void init() {
		XWTechnicsTreeObject object = (XWTechnicsTreeObject)treeNode.getObject();
		Element data = object.getTreeCellData();
		wzbmText.setText(XmlUtility.getAttributeValue(data,"CMAT"));
		wzmcText.setText(XmlUtility.getAttributeValue(data,"PTC_MATERIAL_NAME"));
	}

	public ZTableOp getTableOp() {
		return tableOp;
	}
	public void setTableOp(ZTableOp tableOp) {
		this.tableOp=tableOp;
	}
	public KVJComboBox getComboBox() {
		return wzlbComboBox;
	}

	@Override
	protected void initDimension() {

	}

	public void clearAllSearchCondition(){
		for(JTextField key:componentMap.keySet() ){
			key.setText("");
		}
		for(JTextField key:standardMap.keySet() ){
			key.setText("");
		}
		for(JTextField key:materialMap.keySet() ){
			key.setText("");
		}
		for(JTextField key:nonMaterialMap.keySet() ){
			key.setText("");
		}
		for(JTextField key:compoundMap.keySet() ){
			key.setText("");
		}
		for(JTextField key:jdclMap.keySet() ){
			key.setText("");
		}
		for(JTextField key:hgpMap.keySet() ){
			key.setText("");
		}
		isTypeC.setSelected(false);

	}

	@Override
	protected void initActions() {
		wzlbComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				wzlb =((KVItem)(wzlbComboBox.getSelectedItem())).getKey();
				wzlbMode2 = LoadErpConfig.getInstance().getWztypeVectorByOpType3(wzlb);
				wzlbComboBox2.removeActionListener(actionListener);
				wzlbComboBox2.update(wzlbMode2);
				wzlbComboBox2.addActionListener(actionListener);
				if(wzlbMode2 != null && !wzlbMode2.isEmpty()) {
					wzlbComboBox2.setSelectedIndex(0);
				}

				String value = ((KVItem) (wzlbComboBox.getSelectedItem())).getValue();
				if("元器件".equals(value)){
					initLayout("component");
				}else if("标准紧固件".equals(value)){
					initLayout("standard");
				}else if("金属材料".equals(value)){
					initLayout("materlal");
				}else if("非金属材料".equals(value)){
					initLayout("nonMaterial");
				}else if("复合材料".equals(value)){
					initLayout("compoundMaterial");
				} else if("机电材料".equals(value)){
					initLayout("jdcl");
				}else if("火工品".equals(value)){
					initLayout("hgp");
				}
				clearAllSearchCondition();
			}
		});


		wzlbComboBox2.addActionListener(actionListener);


		queryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				searchData();
				//refreshTable();
			}
		});

		queryButton.setVisible(true);

		autoQuery.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String value = ((KVItem) (wzlbComboBox.getSelectedItem())).getValue();
				if(((JCheckBox)e.getSource()).isSelected()){
					queryButton.setVisible(false);
					setAutoQueryListener(true);
				}else{
					queryButton.setVisible(true);
					setAutoQueryListener(false);
				}
			}
		});
		this.setAutoQueryListener(false);
	}

	protected void initComponents( ) {
		wzkDescLable = new JTextArea("");
		wzkDescLable.setRows(5);
		wzkDescLable.setLineWrap(true);
		wzkDescLable.setEditable(false);

		topPanel = new JPanel();
		topTopPanel = new JPanel();
		wzkPanel = new JPanel();
		autoQuery = new JCheckBox("是否动态查询");
		autoQuery.setSelected(false);
		isTypeC = new JCheckBox("是否包含C类");
		isTypeC.setSelected(true);
		databaseLable = new JLabel("基础数据库");
		databaseLable.setForeground(Color.red);

		jlzsLable = new JLabel("当前记录数："+jlzs);
		jlzsLable.setForeground(Color.red);

		databaseComboBox = new KVJComboBox(databaseModel);
		databaseComboBox.setEnabled(false);
		wzlbComboBox = new KVJComboBox(wzlbMode);
		wzlbComboBox2 = new KVJComboBox(wzlbMode2);

		sjbmLable = new JLabel("设计编码");
		sjbmText = new JTextField();

		wzbmLable = new JLabel("物资编码");
		wzbmText = new JTextField();

		chmcLable = new JLabel("物资名称");
		wzmcText = new JTextField();

		wzjcLabel = new JLabel("物资简称");
		wzjcText = new JTextField();

		bmyxjbLabel = new JLabel("编码优选级别");
		bmyxjbText = new JTextField();

		bmztLabel = new JLabel("编码状态");
		bmztText = new JTextField();

		bmlxLabel = new JLabel("编码类型");
		bmlxText = new JTextField();

		bmdjLabel = new JLabel("编码等级");
		bmdjText = new JTextField();

		hslLabel = new JLabel("换算率");
		hslText = new JTextField();

		xsLabel = new JLabel("系数");
		xsText = new JTextField();

		phLabel = new JLabel("牌号");
		phText = new JTextField();

		gyztLabel = new JLabel("供应状态");
		gyztText = new JTextField();

		cybzLabel = new JLabel("采用标准");
		cybzText = new JTextField();

		jdLabel = new JLabel("精度");
		jdText = new JTextField();

		zltzLabel = new JLabel("质量特征");
		zltzText = new JTextField();

		pzggbzLabel = new JLabel("品种规格标准");
		pzggbzText = new JTextField();

		tssmLabel = new JLabel("特殊说明");
		tssmText = new JTextField();

		sfjkLabel = new JLabel("是否进口");
		sfjkText = new JTextField();

		bzhLabel = new JLabel("标准号");
		bzhText = new JTextField();
		clLabel = new JLabel("材料");
		clText = new JTextField();
		jxxndjLabel = new JLabel("机械性能等级或硬度");
		jxxndjText = new JTextField();
		bmclLabel = new JLabel("表面处理");
		bmclText = new JTextField();
		rclLabel = new JLabel("热处理");
		rclText = new JTextField();
		cpxsLabel = new JLabel("产品型式");
		cpxsText = new JTextField();
		cpdjLabel = new JLabel("产品等级");
		cpdjText = new JTextField();
		nbxsLabel = new JLabel("板拧形式");
		nbxsText = new JTextField();
		xhggLabel = new JLabel("型号规格");
		xhggText = new JTextField();
		zldjLabel = new JLabel("质量等级");
		zldjText = new JTextField();
		zgfLabel = new JLabel("总规范");
		zgfText = new JTextField();
		xxgfLabel = new JLabel("详细规范");
		xxgfText = new JTextField();
		xhLabel = new JLabel("型号");
		xhText = new JTextField();
		fzxsLabel = new JLabel("封装形式");
		fzxsText = new JTextField();
		wxccLabel = new JLabel("外形尺寸");
		wxccText = new JTextField();
		zytjLabel = new JLabel("专用条件");
		zytjText = new JTextField();
		fjxyLabel = new JLabel("附加协议");
		fjxyText = new JTextField();
		kfszbtidLabel = new JLabel("抗辐射指标TID");
		kfszbtidText = new JTextField();
		kfszbseeLabel = new JLabel("抗辐射指标SEE");
		kfszbseeText = new JTextField();
		xncsLabel = new JLabel("性能参数");
		xncsText = new JTextField();
		sfjdmgLabel = new JLabel("是否静电敏感");
		sfjdmgText = new JTextField();
		jdmgdjLabel = new JLabel("静电敏感等级");
		jdmgdjText = new JTextField();
		smdjLabel = new JLabel("湿敏等级");
		smdjText = new JTextField();
		ggLabel = new JLabel("规格");
		ggText = new JTextField();
		sccjLabel = new JLabel("生产厂家");
		sccjText = new JTextField();
		jldwLabel = new JLabel("计量单位");
		jldwText = new JTextField();
		gydwLabel = new JLabel("工艺单位");
		gydwText = new JTextField();
		cpdhLabel = new JLabel("产品代号");
		cpdhText = new JTextField();
		zlLabel = new JLabel("重量");
		zlText = new JTextField();
		zcsmLabel = new JLabel("贮存寿命");
		zcsmText = new JTextField();
		tntLabel = new JLabel("TNT当量");
		tntText = new JTextField();

		queryButton = new JButton("查  询");
		tableOp =  new DefaultZTableFactory();
		tableOp.setTableInfors(tableHeader, tableBody,tableColWidth);
		tableOp.setTableStyle(tableOp.getZTable());
		tableOp.setColumnsHidden(tableOp.getZTable(), new int[]{0});
		//GwPartTechQuotaDialog.initWzkPanelZTableOp(tableOp);
	}

	@Override
	protected void initLayout() {

	}


	public void setInitValue(XWTreeNode node) {
		XWTreeObject obj = node.getObject();
		if (obj instanceof XWTechnicsTreeObject) {
			XWTechnicsTreeObject object = (XWTechnicsTreeObject) obj;
			Element partElement = object.getTreeCellData();
			//暂时无操作
		}
	}

	public void initLayout(String type) {
		this.removeAll();
		topTopPanel=new JPanel();
		topPanel=new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		topTopPanel.setLayout(new GridBagLayout());
		int width = 80;
		c.gridx = 0;
		c.gridy = 0;

		//基础数据库
		topTopPanel.add(databaseLable, c);
		c.gridx = 1;
		databaseComboBox.setPreferredSize(new Dimension(width, 25));
		topTopPanel.add(databaseComboBox, c);

		//物资类别
		c.gridx = 2;
		wzlbComboBox.setPreferredSize(new Dimension(width, 25));
		topTopPanel.add(wzlbComboBox, c);

		//存货类别小类
		c.gridx = 3;
		wzlbComboBox2.setPreferredSize(new Dimension(width, 25));
		topTopPanel.add(wzlbComboBox2, c);

		//是否包含C类复选框
		c.gridx = 4;
		topTopPanel.add(isTypeC);

		//是否动态查询复选框
		c.gridx = 5;
		topTopPanel.add(autoQuery);

		//查询按钮
		c.gridx = 6;
		c.gridwidth = 1;
		queryButton.setPreferredSize(new Dimension(width, 25));
		topTopPanel.add(queryButton, c);
		if ("component".equals(type)) {
			//物资编码
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = 1;
			topTopPanel.add(wzbmLable, c);
			c.gridx = 1;
			wzbmText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(wzbmText, c);

			//物资名称
			c.gridx = 2;
			topTopPanel.add(chmcLable, c);
			c.gridx = 3;
			wzmcText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(wzmcText, c);

			//物资简称
			c.gridx = 4;
			topTopPanel.add(wzjcLabel, c);
			c.gridx = 5;
			wzjcText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(wzjcText, c);

			//编码优选级别
			c.gridx = 6;
			topTopPanel.add(bmyxjbLabel, c);
			c.gridx = 7;
			bmyxjbText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmyxjbText, c);

			//编码状态
			c.gridx = 8;
			topTopPanel.add(bmztLabel, c);
			c.gridx = 9;
			bmztText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmztText, c);
			// 编码类型
			c.gridy = 2;
			c.gridx = 0;
			topTopPanel.add(bmlxLabel, c);
			c.gridx = 1;
			bmlxText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmlxText, c);

			//编码等级
			c.gridx = 2;
			topTopPanel.add(bmdjLabel, c);
			c.gridx = 3;
			bmdjText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmdjText, c);

			//总规范
			c.gridx = 4;
			topTopPanel.add(zgfLabel, c);
			c.gridx = 5;
			zgfText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(zgfText, c);
			//详细规范
			c.gridx = 6;
			topTopPanel.add(xxgfLabel, c);
			c.gridx = 7;
			xxgfText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(xxgfText, c);
			//型号
			c.gridx = 8;
			topTopPanel.add(xhLabel, c);
			c.gridx = 9;
			xhText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(xhText, c);
			c.gridy = 3;
			// 封装形式
			c.gridx = 0;
			topTopPanel.add(fzxsLabel, c);
			c.gridx = 1;
			fzxsText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(fzxsText, c);
			// 外形尺寸
			c.gridx = 2;
			topTopPanel.add(wxccLabel, c);
			c.gridx = 3;
			wxccText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(wxccText, c);
			// 专用条件
			c.gridx = 4;
			topTopPanel.add(zytjLabel, c);
			c.gridx = 5;
			zytjText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(zytjText, c);
			// 附加协议
			c.gridx = 6;
			topTopPanel.add(fjxyLabel, c);
			c.gridx = 7;
			fjxyText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(fjxyText, c);
			// 特殊说明
			c.gridx = 8;
			topTopPanel.add(tssmLabel, c);
			c.gridx = 9;
			tssmText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(tssmText, c);
			c.gridy = 4;
			// 是否进口
			c.gridx = 0;
			topTopPanel.add(sfjkLabel, c);
			c.gridx = 1;
			sfjkText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(sfjkText, c);
			// 抗辐射指标TID
			c.gridx = 2;
			topTopPanel.add(kfszbtidLabel, c);
			c.gridx = 3;
			kfszbtidText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(kfszbtidText, c);
			// 抗辐射指标SEE
			c.gridx = 4;
			topTopPanel.add(kfszbseeLabel, c);
			c.gridx = 5;
			kfszbseeText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(kfszbseeText, c);
			// 性能参数
			c.gridx = 6;
			topTopPanel.add(xncsLabel, c);
			c.gridx = 7;
			xncsText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(xncsText, c);
			// 是否静电敏感
			c.gridx = 8;
			topTopPanel.add(sfjdmgLabel, c);
			c.gridx = 9;
			sfjdmgText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(sfjdmgText, c);
			c.gridy = 5;
			// 静电敏感等级
			c.gridx = 0;
			topTopPanel.add(jdmgdjLabel, c);
			c.gridx = 1;
			jdmgdjText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(jdmgdjText, c);
			// 湿敏等级
			c.gridx = 2;
			topTopPanel.add(smdjLabel, c);
			c.gridx = 3;
			smdjText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(smdjText, c);
			// 生产厂家
			c.gridx = 4;
			topTopPanel.add(sccjLabel, c);
			c.gridx = 5;
			sccjText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(sccjText, c);
			// 计量单位
			c.gridx = 6;
			topTopPanel.add(jldwLabel, c);
			c.gridx = 7;
			jldwText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(jldwText, c);

			// 型号规格
			c.gridx = 8;
			topTopPanel.add(xhggLabel, c);
			c.gridx = 9;
			xhggText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(xhggText, c);

			c.gridy = 6;
			c.gridx = 0;
			c.gridwidth = 8;
			jlzsLable.setPreferredSize(new Dimension(700, 30));
			jlzsLable.setFont(new Font("宋体", Font.PLAIN, 20));
			topTopPanel.add(jlzsLable, c);
			c.gridy = 7;
			c.gridx = 0;
			c.gridwidth = 9;
			topTopPanel.add(wzkDescLable, c);
		}else if("standard".equals(type)) {
			//物资编码
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = 1;
			topTopPanel.add(wzbmLable, c);
			c.gridx = 1;
			wzbmText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(wzbmText, c);

			//物资名称
			c.gridx = 2;
			topTopPanel.add(chmcLable, c);
			c.gridx = 3;
			wzmcText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(wzmcText, c);

			//物资简称
			c.gridx = 4;
			topTopPanel.add(wzjcLabel, c);
			c.gridx = 5;
			wzjcText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(wzjcText, c);

			//编码优选级别
			c.gridx = 6;
			topTopPanel.add(bmyxjbLabel, c);
			c.gridx = 7;
			bmyxjbText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmyxjbText, c);

			//编码状态
			c.gridx = 8;
			topTopPanel.add(bmztLabel, c);
			c.gridx = 9;
			bmztText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmztText, c);

			// 编码类型
			c.gridy = 2;
			c.gridx = 0;
			topTopPanel.add(bmlxLabel, c);
			c.gridx = 1;
			bmlxText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmlxText, c);

			//编码等级
			c.gridx = 2;
			topTopPanel.add(bmdjLabel, c);
			c.gridx = 3;
			bmdjText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmdjText, c);

			//标准号
			c.gridx = 4;
			topTopPanel.add(bzhLabel, c);
			c.gridx = 5;
			bzhText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bzhText, c);

			//规格
			c.gridx = 6;
			topTopPanel.add(ggLabel, c);
			c.gridx = 7;
			ggText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(ggText, c);

			//材料
			c.gridx = 8;
			topTopPanel.add(clLabel, c);
			c.gridx = 9;
			clText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(clText, c);

			//机械性能等级或硬度
			c.gridy = 3;
			c.gridx = 0;
			topTopPanel.add(jxxndjLabel, c);
			c.gridx = 1;
			jxxndjText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(jxxndjText, c);

			//表面处理
			c.gridx = 2;
			topTopPanel.add(bmclLabel, c);
			c.gridx = 3;
			bmclText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmclText, c);

			//热处理
			c.gridx = 4;
			topTopPanel.add(rclLabel, c);
			c.gridx = 5;
			rclText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(rclText, c);

			//产品型式
			c.gridx = 6;
			topTopPanel.add(cpxsLabel, c);
			c.gridx = 7;
			cpxsText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(cpxsText, c);

			//产品等级
			c.gridx = 8;
			topTopPanel.add(cpdjLabel, c);
			c.gridx = 9;
			cpdjText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(cpdjText, c);

			//板拧形式
			c.gridy = 4;
			c.gridx = 0;
			topTopPanel.add(nbxsLabel, c);
			c.gridx = 1;
			nbxsText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(nbxsText, c);

			//特殊说明
			c.gridx = 2;
			topTopPanel.add(tssmLabel, c);
			c.gridx = 3;
			tssmText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(tssmText, c);
			//特殊说明
			c.gridx = 4;
			topTopPanel.add(sfjkLabel, c);
			c.gridx = 5;
			sfjkText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(sfjkText, c);

			// 生产厂家
			c.gridx = 6;
			topTopPanel.add(sccjLabel, c);
			c.gridx = 7;
			sccjText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(sccjText, c);

			c.gridy = 5;
			c.gridx = 0;
			c.gridwidth = 8;
			jlzsLable.setPreferredSize(new Dimension(700, 30));
			jlzsLable.setFont(new Font("宋体", Font.PLAIN, 20));
			topTopPanel.add(jlzsLable, c);
			c.gridy = 6;
			c.gridx = 0;
			c.gridwidth = 9;
			topTopPanel.add(wzkDescLable, c);
		}else if("materlal".equals(type)){
			//物资编码
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = 1;
			topTopPanel.add(wzbmLable, c);
			c.gridx = 1;
			wzbmText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(wzbmText, c);

			//物资名称
			c.gridx = 2;
			topTopPanel.add(chmcLable, c);
			c.gridx = 3;
			wzmcText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(wzmcText, c);

			//物资简称
			c.gridx = 4;
			topTopPanel.add(wzjcLabel, c);
			c.gridx = 5;
			wzjcText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(wzjcText, c);

			//编码优选级别
			c.gridx = 6;
			topTopPanel.add(bmyxjbLabel, c);
			c.gridx = 7;
			bmyxjbText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmyxjbText, c);

			//编码状态
			c.gridx = 8;
			topTopPanel.add(bmztLabel, c);
			c.gridx = 9;
			bmztText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmztText, c);

			// 编码类型
			c.gridy = 2;
			c.gridx = 0;
			topTopPanel.add(bmlxLabel, c);
			c.gridx = 1;
			bmlxText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmlxText, c);

			//编码等级
			c.gridx = 2;
			topTopPanel.add(bmdjLabel, c);
			c.gridx = 3;
			bmdjText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmdjText, c);

			//换算率
			c.gridx = 4;
			topTopPanel.add(hslLabel, c);
			c.gridx = 5;
			hslText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(hslText, c);

			//系数
			c.gridx = 6;
			topTopPanel.add(xsLabel, c);
			c.gridx = 7;
			xsText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(xsText, c);

			//牌号
			c.gridx = 8;
			topTopPanel.add(phLabel, c);
			c.gridx = 9;
			phText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(phText, c);

			//供应状态
			c.gridy = 3;
			c.gridx = 0;
			topTopPanel.add(gyztLabel, c);
			c.gridx = 1;
			gyztText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(gyztText, c);

			//采用标准
			c.gridx = 2;
			topTopPanel.add(cybzLabel, c);
			c.gridx = 3;
			cybzText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(cybzText, c);

			//精度
			c.gridx = 4;
			topTopPanel.add(jdLabel, c);
			c.gridx = 5;
			jdText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(jdText, c);

			//质量特征
			c.gridx = 6;
			topTopPanel.add(zltzLabel, c);
			c.gridx = 7;
			zltzText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(zltzText, c);

			//品种规格标准
			c.gridx = 8;
			topTopPanel.add(pzggbzLabel, c);
			c.gridx = 9;
			pzggbzText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(pzggbzText, c);

			//特殊说明
			c.gridy = 4;
			c.gridx = 0;
			topTopPanel.add(tssmLabel, c);
			c.gridx = 1;
			tssmText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(tssmText, c);

			//特殊说明
			c.gridx = 2;
			topTopPanel.add(sfjkLabel, c);
			c.gridx = 3;
			sfjkText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(sfjkText, c);

			// 生产厂家
			c.gridx = 4;
			topTopPanel.add(sccjLabel, c);
			c.gridx = 5;
			sccjText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(sccjText, c);
			// 规格
			c.gridx = 6;
			topTopPanel.add(ggLabel, c);
			c.gridx = 7;
			ggText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(ggText, c);
			// 计量单位
			c.gridx = 8;
			topTopPanel.add(jldwLabel, c);
			c.gridx = 9;
			jldwText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(jldwText, c);

			c.gridy = 5;
			c.gridx = 0;
			c.gridwidth = 8;
			jlzsLable.setPreferredSize(new Dimension(700, 30));
			jlzsLable.setFont(new Font("宋体", Font.PLAIN, 20));
			topTopPanel.add(jlzsLable, c);
			c.gridy = 6;
			c.gridx = 0;
			c.gridwidth = 9;
			topTopPanel.add(wzkDescLable, c);
		}else if("nonMaterial".equals(type)||"compoundMaterial".equals(type)){
			//物资编码
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = 1;
			topTopPanel.add(wzbmLable, c);
			c.gridx = 1;
			wzbmText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(wzbmText, c);

			//物资名称
			c.gridx = 2;
			topTopPanel.add(chmcLable, c);
			c.gridx = 3;
			wzmcText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(wzmcText, c);

			//物资简称
			c.gridx = 4;
			topTopPanel.add(wzjcLabel, c);
			c.gridx = 5;
			wzjcText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(wzjcText, c);

			//编码优选级别
			c.gridx = 6;
			topTopPanel.add(bmyxjbLabel, c);
			c.gridx = 7;
			bmyxjbText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmyxjbText, c);

			//编码状态
			c.gridx = 8;
			topTopPanel.add(bmztLabel, c);
			c.gridx = 9;
			bmztText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmztText, c);

			// 编码类型
			c.gridy = 2;
			c.gridx = 0;
			topTopPanel.add(bmlxLabel, c);
			c.gridx = 1;
			bmlxText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmlxText, c);

			//编码等级
			c.gridx = 2;
			topTopPanel.add(bmdjLabel, c);
			c.gridx = 3;
			bmdjText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmdjText, c);

			//换算率
			c.gridx = 4;
			topTopPanel.add(hslLabel, c);
			c.gridx = 5;
			hslText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(hslText, c);

			//系数
			c.gridx = 6;
			topTopPanel.add(xsLabel, c);
			c.gridx = 7;
			xsText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(xsText, c);

			//牌号
			c.gridx = 8;
			topTopPanel.add(phLabel, c);
			c.gridx = 9;
			phText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(phText, c);

			//规格
			c.gridy = 3;
			c.gridx = 0;
			topTopPanel.add(ggLabel, c);
			c.gridx = 1;
			ggText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(ggText, c);

			//采用标准
			c.gridx = 2;
			topTopPanel.add(cybzLabel, c);
			c.gridx = 3;
			cybzText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(cybzText, c);

			//特殊说明
			c.gridx = 4;
			topTopPanel.add(tssmLabel, c);
			c.gridx = 5;
			tssmText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(tssmText, c);

			//是否进口
			c.gridx = 6;
			topTopPanel.add(sfjkLabel, c);
			c.gridx = 7;
			sfjkText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(sfjkText, c);

			// 生产厂家
			c.gridx = 8;
			topTopPanel.add(sccjLabel, c);
			c.gridx = 9;
			sccjText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(sccjText, c);

			//计量单位
			c.gridy = 4;
			c.gridx = 0;
			topTopPanel.add(jldwLabel, c);
			c.gridx = 1;
			jldwText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(jldwText, c);


			c.gridy = 5;
			c.gridx = 0;
			c.gridwidth = 8;
			jlzsLable.setPreferredSize(new Dimension(700, 30));
			jlzsLable.setFont(new Font("宋体", Font.PLAIN, 20));
			topTopPanel.add(jlzsLable, c);
			c.gridy = 6;
			c.gridx = 0;
			c.gridwidth = 9;
			topTopPanel.add(wzkDescLable, c);
		}else if("jdcl".equals(type)){
			//物资编码
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = 1;
			topTopPanel.add(wzbmLable, c);
			c.gridx = 1;
			wzbmText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(wzbmText, c);

			//物资名称
			c.gridx = 2;
			topTopPanel.add(chmcLable, c);
			c.gridx = 3;
			wzmcText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(wzmcText, c);

			//标准号
			c.gridx = 4;
			topTopPanel.add(bzhLabel, c);
			c.gridx = 5;
			bzhText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bzhText, c);

			//牌号
			c.gridx = 6;
			topTopPanel.add(phLabel, c);
			c.gridx = 7;
			phText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(phText, c);

			//生产厂家
			c.gridx = 8;
			topTopPanel.add(sccjLabel, c);
			c.gridx = 9;
			sccjText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(sccjText, c);

			//计量单位
			c.gridy = 2;
			c.gridx = 0;
			topTopPanel.add(jldwLabel, c);
			c.gridx = 1;
			jldwText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(jldwText, c);

			//编码等级
			c.gridx = 2;
			topTopPanel.add(bmdjLabel, c);
			c.gridx = 3;
			bmdjText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmdjText, c);

			//型号规格
			c.gridx = 4;
			topTopPanel.add(xhggLabel, c);
			c.gridx = 5;
			xhggText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(xhggText, c);

			//是否进口
			c.gridx = 6;
			topTopPanel.add(sfjkLabel, c);
			c.gridx = 7;
			sfjkText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(sfjkText, c);

			//性能参数
			c.gridx = 8;
			topTopPanel.add(xncsLabel, c);
			c.gridx = 9;
			xncsText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(xncsText, c);

			//特殊说明
			c.gridy = 3;
			c.gridx = 0;
			topTopPanel.add(tssmLabel, c);
			c.gridx = 1;
			tssmText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(tssmText, c);

			//编码状态
			c.gridx = 2;
			topTopPanel.add(bmztLabel, c);
			c.gridx = 3;
			bmztText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmztText, c);

			c.gridy = 4;
			c.gridx = 0;
			c.gridwidth = 8;
			jlzsLable.setPreferredSize(new Dimension(700, 30));
			jlzsLable.setFont(new Font("宋体", Font.PLAIN, 20));
			topTopPanel.add(jlzsLable, c);
			c.gridy = 6;
			c.gridx = 0;
			c.gridwidth = 9;
			topTopPanel.add(wzkDescLable, c);
		}else if("hgp".equals(type)){
			//物资编码
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = 1;
			topTopPanel.add(wzbmLable, c);
			c.gridx = 1;
			wzbmText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(wzbmText, c);

			//物资名称
			c.gridx = 2;
			topTopPanel.add(chmcLable, c);
			c.gridx = 3;
			wzmcText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(wzmcText, c);

			//标准号
			c.gridx = 4;
			topTopPanel.add(bzhLabel, c);
			c.gridx = 5;
			bzhText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bzhText, c);

			//生产厂家
			c.gridx = 6;
			topTopPanel.add(sccjLabel, c);
			c.gridx = 7;
			sccjText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(sccjText, c);

			//编码等级
			c.gridx = 8;
			topTopPanel.add(bmdjLabel, c);
			c.gridx = 9;
			bmdjText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmdjText, c);

			//产品代号
			c.gridy = 2;
			c.gridx = 0;
			topTopPanel.add(cpdhLabel, c);
			c.gridx = 1;
			cpdhText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(cpdhText, c);

			//重量
			c.gridx = 2;
			topTopPanel.add(zlLabel, c);
			c.gridx = 3;
			zlText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(zlText, c);

			//贮存寿命
			c.gridx = 4;
			topTopPanel.add(zcsmLabel, c);
			c.gridx = 5;
			zcsmText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(zcsmText, c);

			//TNT
			c.gridx = 6;
			topTopPanel.add(tntLabel, c);
			c.gridx = 7;
			tntText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(tntText, c);

			//性能参数
			c.gridx = 8;
			topTopPanel.add(xncsLabel, c);
			c.gridx = 9;
			xncsText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(xncsText, c);

			//特殊说明
			c.gridy = 3;
			c.gridx = 0;
			topTopPanel.add(tssmLabel, c);
			c.gridx = 1;
			tssmText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(tssmText, c);

			//编码状态
			c.gridx = 2;
			topTopPanel.add(bmztLabel, c);
			c.gridx = 3;
			bmztText.setPreferredSize(new Dimension(width, 25));
			topTopPanel.add(bmztText, c);

			c.gridy = 4;
			c.gridx = 0;
			c.gridwidth = 8;
			jlzsLable.setPreferredSize(new Dimension(700, 30));
			jlzsLable.setFont(new Font("宋体", Font.PLAIN, 20));
			topTopPanel.add(jlzsLable, c);
			c.gridy = 7;
			c.gridx = 0;
			c.gridwidth = 9;
			topTopPanel.add(wzkDescLable, c);
		}
		topPanel.add(topTopPanel);
		this.setLayout(new BorderLayout());
		this.add(topPanel,BorderLayout.NORTH);
		wzkPanel=new JPanel();
		initTableInfo(type);
		tableOp.setTableInfors(tableHeader, tableBody,tableColWidth);
		tableOp.setColumnsHidden(tableOp.getZTable(),new int[]{0});
		wzkPanel.setLayout(new BorderLayout());
		final JLabel wzkLable = new JLabel("工艺物资条目");
		wzkLable.setFont(new Font("宋体",Font.PLAIN,20));
		wzkPanel.add(wzkLable,BorderLayout.NORTH);
		tableOp.getZTable().setPreferredScrollableViewportSize(new Dimension(850,300));
		wzkPanel.add(new JScrollPane(tableOp.getZTable()),BorderLayout.CENTER);
		this.add(wzkPanel,BorderLayout.CENTER);
		gwPartTechQuotaDialog.initWzkPanelZTableOp(tableOp);


	}


	protected void loadInitDatas( ) {
		Vector<KVItem> vector = new Vector<KVItem>();
		vector.addElement(new KVItem("工艺物资信息库", "工艺物资信息库"));
		databaseModel =vector ;
		wzlbMode = LoadErpConfig.getInstance().getGwWztypeVectorByOpType();
		wzlbMode2 = LoadErpConfig.getInstance().getWztypeVectorByOpType3("01");

	}
	private void 	initTableInfo(String type){
		if("component".equals(type)){
			tableHeader =new String[]{"物资条目oid","序号","物资名称","型号规格","质量等级","生产厂家","计量单位","总规范","详细规范","型号",
					"封装形式","外形尺寸","专用条件","附加协议","特殊说明","是否进口","抗辐射指标TID","抗辐射指标SEE","性能参数","是否静电敏感",
					"静电敏感等级","湿敏等级","设计编码","物资编码","物资简称","编码优选级别","编码状态","编码类型","编码等级",
					};
			int tw1 = 55;
			tableColWidth = new int[]{tw1-15,tw1-15,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,
					tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,
					tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1};
		}else if("standard".equals(type)){
			tableHeader =new String[]{"物资条目oid","序号","物资名称","规格","标准号","机械性能等级或硬度","计量单位","材料","表面处理","热处理",
					"生产厂家","产品型式","产品等级","板拧形式","特殊说明","是否进口","设计编码","物资编码","物资简称","编码优选级别",
					"编码状态","编码类型","编码等级",
					};
			int tw1 = 55;
			tableColWidth = new int[]{tw1-15,tw1-15,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,
					tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,
					tw1,tw1,tw1};
		}else if("materlal".equals(type)){
			tableHeader =new String[]{"物资条目oid","序号","物资名称","牌号","规格","供应状态","采用标准","计量单位","工艺单位","品种规格标准",
					"精度","质量特征","生产厂家","物资简称","特殊说明","是否进口","换算率","系数","设计编码","物资编码",
					"编码优选级别","编码状态","编码类型","编码等级"};
			int tw1 = 55;
			tableColWidth = new int[]{tw1-15,tw1-15,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,
					tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,
					tw1,tw1,tw1,tw1};
		}else if("nonMaterial".equals(type)){
			tableHeader =new String[]{"物资条目oid","序号","物资名称","牌号","规格","采用标准","计量单位","工艺单位","生产厂家","特殊说明",
					"是否进口","物资简称","换算率","系数","设计编码","物资编码","编码优选级别","编码状态","编码类型","编码等级"
					};
			int tw1 = 55;
			tableColWidth = new int[]{tw1-15,tw1-15,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,
					tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1};
		}else if("compoundMaterial".equals(type)){
			tableHeader =new String[]{"物资条目oid","序号","物资名称","牌号","规格","采用标准","计量单位","生产厂家","特殊说明","是否进口",
					"物资简称","换算率","系数","设计编码","物资编码","编码优选级别","编码状态","编码类型","编码等级"};
			int tw1 = 55;
			tableColWidth = new int[]{tw1-15,tw1-15,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,
					tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1};
		} else if("jdcl".equals(type)){
			tableHeader =new String[]{"物资条目oid","序号","物资名称","牌号","标准号","型号规格","计量单位","生产厂家","设计编码","物资编码","编码等级",
					"是否进口","性能参数","特殊说明","编码状态"};
			int tw1 = 160;
			tableColWidth = new int[]{tw1-145,tw1-145,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1};
		}else if("hgp".equals(type)){
			tableHeader =new String[]{"物资条目oid","序号","物资名称","标准号","生产厂家","设计编码","物资编码","计量单位","编码等级","产品代号",
					"重量","贮存寿命","TNT当量","性能参数","特殊说明","编码状态"};
			int tw1 = 160;
			tableColWidth = new int[]{tw1-145,tw1-145,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1};
		}
	}
	private void queryWzk() {
		//TODO
		jlzsLable.setText("当前记录数：" + dataList.size());
		this.refreshTable();
	}

	private void refreshTable() {
		//TODO
	}

	ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			//TODO
			jlzsLable.setText("当前记录数："+dataList.size());
			refreshTable();
		}
	};

	ActionListener isTypeCListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			searchData();
		}
	};




	private void setAutoQueryListener(boolean isAutoQuery) {
			if (isAutoQuery) {
				for (JTextField key:componentMap.keySet()){
					key.getDocument().addDocumentListener(this);
				}
				for (JTextField key:standardMap.keySet()){
					key.getDocument().addDocumentListener(this);
				}
				for (JTextField key:materialMap.keySet()){
					key.getDocument().addDocumentListener(this);
				}
				for (JTextField key:nonMaterialMap.keySet()){
					key.getDocument().addDocumentListener(this);
				}
				for (JTextField key:jdclMap.keySet()){
					key.getDocument().addDocumentListener(this);
				}
				for (JTextField key:hgpMap.keySet()){
					key.getDocument().addDocumentListener(this);
				}
				isTypeC.addActionListener(isTypeCListener);
			}else{
				for (JTextField key:componentMap.keySet()){
					key.getDocument().removeDocumentListener(this);
				}
				for (JTextField key:standardMap.keySet()){
					key.getDocument().removeDocumentListener(this);
				}
				for (JTextField key:materialMap.keySet()){
					key.getDocument().removeDocumentListener(this);
				}
				for (JTextField key:nonMaterialMap.keySet()){
					key.getDocument().removeDocumentListener(this);
				}
				for (JTextField key:jdclMap.keySet()){
					key.getDocument().removeDocumentListener(this);
				}
				for (JTextField key:hgpMap.keySet()){
					key.getDocument().removeDocumentListener(this);
				}
				isTypeC.removeActionListener(isTypeCListener);
			}


	}



	public static String emptyToString(String obj) {
		if (null == obj || "".equals(obj.trim())) {
			return "";
		} else {
			return obj;
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		searchData();

	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		searchData();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		searchData();
	}
	public void searchData(){
		String value = ((KVItem) (wzlbComboBox.getSelectedItem())).getValue();
		if("".equals(value)||"null".equals(value)){
			return;
		}
		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap.put("isC",isTypeC.isSelected()+"");
		if("元器件".equals(value)){
			for(JTextField key:componentMap.keySet()){
				String text = key.getText();
				if (!"".equals(text)&&!"null".equals(text)) {
					String column = componentMap.get(key);
					conditionMap.put(column,text);
				}
			}
		}else if("标准紧固件".equals(value)){
			for(JTextField key:standardMap.keySet()){
				String text = key.getText();
				if (!"".equals(text)&&!"null".equals(text)) {
					String column = standardMap.get(key);
					conditionMap.put(column,text);
				}
			}
		}else if("金属材料".equals(value)){
			for(JTextField key:materialMap.keySet()){
				String text = key.getText();
				if (!"".equals(text)&&!"null".equals(text)) {
					String column = materialMap.get(key);
					conditionMap.put(column,text);
				}
			}
		}else if("非金属材料".equals(value)){
			for(JTextField key:nonMaterialMap.keySet()){
				String text = key.getText();
				if (!"".equals(text)&&!"null".equals(text)) {
					String column = nonMaterialMap.get(key);
					conditionMap.put(column,text);
				}
			}
		}else if("复合材料".equals(value)){
			for(JTextField key:compoundMap.keySet()){
				String text = key.getText();
				if (!"".equals(text)&&!"null".equals(text)) {
					String column = compoundMap.get(key);
					conditionMap.put(column,text);
				}
			}
		}else if("机电材料".equals(value)){
			for(JTextField key:jdclMap.keySet()){
				String text = key.getText();
				if (!"".equals(text)&&!"null".equals(text)) {
					String column = jdclMap.get(key);
					conditionMap.put(column,text);
				}
			}
		}else if("火工品".equals(value)){
			for(JTextField key:hgpMap.keySet()){
				String text = key.getText();
				if (!"".equals(text)&&!"null".equals(text)) {
					String column = hgpMap.get(key);
					conditionMap.put(column,text);
				}
			}
		}
		List list = ErpToWCIntf.getTechMaterialInfo(value, conditionMap);
		jlzsLable.setText("当前记录数："+list.size());
		if (list != null) {
			addRowInfo(list);
		}
	}

	public void addRowInfo(List list){
		DefaultTableModel model = (DefaultTableModel) tableOp.getTableModel();
		model.setRowCount(0);
		for (int i = 0; i <list.size() ; i++) {
			Object obj = list.get(i);
			if(obj instanceof TMEEleComponentsPartLinkBean){
				TMEEleComponentsPartLinkBean bean=(TMEEleComponentsPartLinkBean) obj;
				String[] values = new String[componentMap.size()+2];
				values[0]=bean.getTechnicsmaterialentriesid();
				values[1]=String.valueOf(i+1);
				int intFlag=2;
				for (JTextField key:componentMap.keySet()) {
					String s = componentMap.get(key);
					Object getMethod = getGetMethod(bean, s);
					String value="";
					if (getMethod==null){
						value="";
					}else{
						value=String.valueOf(getMethod);
					}
					values[intFlag]=value;
					intFlag++;
				}
				model.addRow(values);

			}else if(obj instanceof TMEStandPartLinkBean){
				TMEStandPartLinkBean bean=(TMEStandPartLinkBean) obj;
				String[] values = new String[standardMap.size()+2];
				values[0]=bean.getTechnicsmaterialentriesid();
				values[1]=String.valueOf(i+1);
				int intFlag=2;
				for (JTextField key:standardMap.keySet()) {
					String s = standardMap.get(key);
					Object getMethod = getGetMethod(bean, s);
					String value="";
					if (getMethod==null){
						value="";
					}else{
						value=String.valueOf(getMethod);
					}
					values[intFlag]=value;
					intFlag++;
				}
				model.addRow(values);

			}else if(obj instanceof TMEMetallicPartLinkBean){
				TMEMetallicPartLinkBean bean=(TMEMetallicPartLinkBean) obj;
				String[] values = new String[materialMap.size()+2];
				values[0]=bean.getTechnicsmaterialentriesid();
				values[1]=String.valueOf(i+1);
				int intFlag=2;
				for (JTextField key:materialMap.keySet()) {
					String s = materialMap.get(key);
					Object getMethod = getGetMethod(bean, s);
					String value="";
					if (getMethod==null){
						value="";
					}else{
						value=String.valueOf(getMethod);
					}
					values[intFlag]=value;
					intFlag++;
				}
				model.addRow(values);

			}else if(obj instanceof TMENonMetallicPartLinkBean){
				TMENonMetallicPartLinkBean bean=(TMENonMetallicPartLinkBean) obj;
				String[] values = new String[nonMaterialMap.size()+2];
				values[0]=bean.getTechnicsmaterialentriesid();
				values[1]=String.valueOf(i+1);
				int intFlag=2;
				for (JTextField key:nonMaterialMap.keySet()) {
					String s = nonMaterialMap.get(key);
					Object getMethod = getGetMethod(bean, s);
					String value="";
					if (getMethod==null){
						value="";
					}else{
						value=String.valueOf(getMethod);
					}
					values[intFlag]=value;
					intFlag++;
				}
				model.addRow(values);
			}else if(obj instanceof TMECompoundMaterialPartLinkBean){
				TMECompoundMaterialPartLinkBean bean=(TMECompoundMaterialPartLinkBean) obj;
			String[] values = new String[compoundMap.size()+2];
				values[0]=bean.getTechnicsmaterialentriesid();
				values[1]=String.valueOf(i+1);
				int intFlag=2;
			for (JTextField key:compoundMap.keySet()) {
				String s = compoundMap.get(key);
				Object getMethod = getGetMethod(bean, s);
				String value="";
				if (getMethod==null){
					value="";
				}else{
					value=String.valueOf(getMethod);
				}
				values[intFlag]=value;
				intFlag++;
			}
			model.addRow(values);

		}else if(obj instanceof TMEEleMachinePartLinkBean){
				TMEEleMachinePartLinkBean bean=(TMEEleMachinePartLinkBean) obj;
				String[] values = new String[jdclMap.size()+2];
				values[0]=bean.getTechnicsmaterialentriesid();
				values[1]=String.valueOf(i+1);
				int intFlag=2;
				for (JTextField key:jdclMap.keySet()) {
					String s = jdclMap.get(key);
					Object getMethod = getGetMethod(bean, s);
					String value="";
					if (getMethod==null){
						value="";
					}else{
						value=String.valueOf(getMethod);
					}
					values[intFlag]=value;
					intFlag++;
				}
				model.addRow(values);

			}else if(obj instanceof TMEExpDevicePartLinkBean){
				TMEExpDevicePartLinkBean bean=(TMEExpDevicePartLinkBean) obj;
				String[] values = new String[hgpMap.size()+2];
				values[0]=bean.getTechnicsmaterialentriesid();
				values[1]=String.valueOf(i+1);
				int intFlag=2;
				for (JTextField key:hgpMap.keySet()) {
					String s = hgpMap.get(key);
					Object getMethod = getGetMethod(bean, s);
					String value="";
					if (getMethod==null){
						value="";
					}else{
						value=String.valueOf(getMethod);
					}
					values[intFlag]=value;
					intFlag++;
				}
				model.addRow(values);

			}
		}
	}

	/**
	 * 根据属性，获取get方法
	 *
	 * @param ob
	 *            对象
	 * @param name
	 *            属性名
	 * @return
	 * @throws Exception
	 */
	public  Object getGetMethod(Object ob, String name)  {
		try {
			Method[] m = ob.getClass().getMethods();
			for (int i = 0; i < m.length; i++) {
				if (("get" + name).toLowerCase().equals(m[i].getName().toLowerCase())) {
					return m[i].invoke(ob);
				}
			}
		}catch (Exception e){

		}
		return null;
	}

}
