package com.glaway.mpm.erp;

import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.XWTechnicsTreeObject;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.view.XWTreeObject;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;
/**
 * erp 物资库查询panel
 * @author Administrator
 *
 */
public class ErpWzkPanel extends CmAbstractPanel{

	private static final long serialVersionUID = 1L;

	private ZTableOp tableOp;

	private String[] tableHeader = null;

	private String[][] tableBody ;

	private int[] tableColWidth ;

	private JPanel topPanel;

	private JLabel databaseLable;
	private KVJComboBox databaseComboBox; //基础数据库
	private KVJComboBox wzlbComboBox; //物资类别
	private KVJComboBox wzlbComboBox2; //物资类别小类

	private Vector databaseModel ; //
	private Vector wzlbMode;
	private Vector<KVItem> wzlbMode2;

	private JLabel wzbmLable; //存货编码
	private JTextField wzbmText;

	private JLabel chmcLable; //存货名称
	private JTextField chmcText;

	private JLabel xhphclLable; //型号牌号
	private JTextField xhphclText;

	private JLabel ggLable; //规格
	private JTextField ggText;

	private JLabel jstjLable; // 技术条件
	private JTextField jstjText;

	private JLabel sccjLable; //生产厂家
	private JTextField sccjText;

	private JLabel zjldwLabel;//主计量单位
	private JTextField zjldwText;

	private JLabel fjtjLabel;//附加条件
	private JTextField fjtjText;

	private JLabel gyztrclLabel;//供应状态/热处理
	private JTextField gyztrclText;

	private JLabel cyfhLable; //常用符号
	private JComboBox cyfhBox;

	private JLabel cyfhmsLable;
	private String[] cyfhMode = null;

	private JLabel  jlzsLable; //记录总数量

	public  JTextArea wzkDescLable;

	private JCheckBox autoQuery;
    private XWTreeNode treeNode;
	//private JLabel cxLable;
	//private JTextField cxText;
	//private JButton cxButton;
	//private JLabel cxmsLable;

	private JButton queryButton;

	private long jlzs;

	private int opType;

	//"存货编码","存货名称","型号牌号","规格","技术条件","生产厂家","主计量单位","附加条件","供应状态/热处理"
	private String dbtype, wzlb, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw, fjtj, gyztrcl;

	private List<Wzk> dataList;

	public ErpWzkPanel(int opType,XWTreeNode node){
		this.opType = opType;
		treeNode=node;
		loadInitDatas();
		initDimension();
		initComponents();
		initActions();
		initLayout();
		init();
	}

	private void init() {
		XWTechnicsTreeObject object = (XWTechnicsTreeObject)treeNode.getObject();
		Element data = object.getTreeCellData();
//		System.out.println("=-==-=-=-=-=->>>>>>>>>>>>>>>>>>>>"+XmlUtility.getAttributeValue(data,"CMAT"));
		wzbmText.setText(XmlUtility.getAttributeValue(data,"CMAT"));
		chmcText.setText(XmlUtility.getAttributeValue(data,"PTC_MATERIAL_NAME"));
		xhphclText.setText(XmlUtility.getAttributeValue(data,"XHPH"));
		ggText.setText(XmlUtility.getAttributeValue(data,"PZGGBZH"));
		jstjText.setText(XmlUtility.getAttributeValue(data,"JSTJ"));
	}

	public ZTableOp getTableOp() {
		return tableOp;
	}


	@Override
	protected void initDimension() {

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
			}
		});

		wzlbComboBox2.addActionListener(actionListener);

		databaseComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dbtype =((KVItem)(databaseComboBox.getSelectedItem())).getKey();
				//2,wzlb 物资类别(01：元器件 02：标准紧固件 03：金属材料 04：非金属材料 05：复合材料 06：机电产品 07：火工品)
				wzlb =((KVItem)(wzlbComboBox.getSelectedItem())).getKey();
				String wzbm = wzbmText.getText();//3,wzbm 存货编码
				String wzmc = chmcText.getText();//4,wzmc 存货名称
				String xhphcl = xhphclText.getText();//5,xhphcl 型号牌号
				String gg = ggText.getText();//6,gg 规格
				String jstj = jstjText.getText();//7,jstj 技术条件
				String sccj = sccjText.getText();//8,gys 生产厂家
				String zjldw = zjldwText.getText();//9,zjldw 主计量单位
				String fjtj = fjtjText.getText();//10,fjtj 附加条件
				String gyztrcl = gyztrclText.getText();//11,供应状态/热处理

				dataList = ErpToWCIntf.queryWzk(dbtype, wzlb, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw, fjtj,gyztrcl);
				jlzsLable.setText("当前记录数："+dataList.size());
				refreshTable();
			}
		});

		queryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dbtype =((KVItem)(databaseComboBox.getSelectedItem())).getKey();
				//2,wzlb 物资类别(01：元器件 02：标准紧固件 03：金属材料 04：非金属材料 05：复合材料 06：机电产品 07：火工品)
				wzlb =((KVItem)(wzlbComboBox2.getSelectedItem())).getKey();
				String wzbm = wzbmText.getText();//3,wzbm 存货编码
				String wzmc = chmcText.getText();//4,wzmc 存货名称
				String xhphcl = xhphclText.getText();//5,xhphcl 型号牌号
				String gg = ggText.getText();//6,gg 规格
				String jstj = jstjText.getText();//7,jstj 技术条件
				String sccj = sccjText.getText();//8,gys 生产厂家
				String zjldw = zjldwText.getText();//9,zjldw 主计量单位
				String fjtj = fjtjText.getText();//10,fjtj 附加条件
				String gyztrcl = gyztrclText.getText();//11,供应状态/热处理

				dataList = ErpToWCIntf.queryWzk(dbtype, wzlb, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw, fjtj,gyztrcl);
				jlzsLable.setText("当前记录数："+dataList.size());
				refreshTable();
			}
		});

		queryButton.setVisible(false);

		autoQuery.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(((JCheckBox)e.getSource()).isSelected()){
					queryButton.setVisible(false);
					setAutoQueryListener(true);
				}else{
					queryButton.setVisible(true);
					setAutoQueryListener(false);
				}
			}
		});

		tableOp.getZTable().addMouseListener(new MouseAdapter() {
			int row;
			TableModel wltm;
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				row = tableOp.getZTable().getSelectedRow();
				wltm = tableOp.getTableModel();
				if(row==-1){
					return;
				}
				if(e.getClickCount()==1){
					StringBuffer sb = new StringBuffer();
					sb.append("存货编码:"+ emptyToString((String)wltm.getValueAt(row, 1))+";");
					sb.append("存货名称:"+ emptyToString((String)wltm.getValueAt(row, 2))+";");
					sb.append("型号牌号:"+ emptyToString((String)wltm.getValueAt(row, 3))+";");
					sb.append("规格:"+ emptyToString((String)wltm.getValueAt(row, 4))+";");
					sb.append("技术条件:"+ emptyToString((String)wltm.getValueAt(row, 5))+";");
					sb.append("生产厂家:"+ emptyToString((String)wltm.getValueAt(row, 6))+";");
					sb.append("主计量单位:"+ ((String)wltm.getValueAt(row, 7))+";");
					sb.append("附加条件:"+ emptyToString((String)wltm.getValueAt(row, 8))+";");
					sb.append("供应状态/热处理:"+ emptyToString((String)wltm.getValueAt(row, 9))+";");

					wzkDescLable.setText(sb.toString());
				}
			}
		});
		this.setAutoQueryListener(true);
	}

	@Override
	protected void initComponents() {
		wzkDescLable = new JTextArea("");
		wzkDescLable.setRows(5);
		wzkDescLable.setLineWrap(true);
		wzkDescLable.setEditable(false);

		topPanel = new JPanel();
		autoQuery = new JCheckBox("是否动态查询");
		autoQuery.setSelected(true);
		databaseLable = new JLabel("基础数据库");
		databaseLable.setForeground(Color.red);

		jlzsLable = new JLabel("当前记录数："+jlzs);
		jlzsLable.setForeground(Color.red);

		databaseComboBox = new KVJComboBox(databaseModel);
		databaseComboBox.setEnabled(false);
		wzlbComboBox = new KVJComboBox(wzlbMode);
		wzlbComboBox2 = new KVJComboBox(wzlbMode2);

		wzbmLable = new JLabel("存货编码");
		wzbmText = new JTextField();

		chmcLable = new JLabel("存货名称");
		chmcText = new JTextField();

		xhphclLable = new JLabel("型号牌号");
		xhphclText = new JTextField();

		ggLable = new JLabel("规格");
		ggText = new JTextField();

		sccjLable = new JLabel("生产厂家");
		sccjText = new JTextField();

		zjldwLabel = new JLabel("主计量单位");
		zjldwText = new JTextField();

		cyfhLable = new JLabel("常用符号");
		cyfhBox = new JComboBox(cyfhMode);
		cyfhBox.setEditable(true);
		cyfhmsLable = new JLabel("直接选择后复制...");
		cyfhmsLable.setForeground(Color.red);
		cyfhmsLable.setToolTipText("直接选择后复制,如果新增特殊符号,请与信息处联系");

		jstjLable = new JLabel("技术条件");
		jstjText = new JTextField();

		fjtjLabel = new JLabel("附加条件");
		fjtjText = new JTextField();

		gyztrclLabel = new JLabel("供应状态/热处理");
		gyztrclText = new JTextField();

		//cxLable = new JLabel("带@编码");
		//cxText = new JTextField();
		//cxmsLable = new JLabel("如果带@查询出错...");
		//cxmsLable.setToolTipText("如果带@查询出错,则说明该模板最新的BOM模板,请在以上文本框最后添加@在查询");
		//cxButton = new JButton("带@查询");

		queryButton = new JButton("查  询");

		tableOp =  new DefaultZTableFactory();
		tableOp.setTableInfors(tableHeader, tableBody,tableColWidth);
		tableOp.setTableStyle(tableOp.getZTable());
		tableOp.setColumnsHidden(tableOp.getZTable(), new int[]{10,11});
	}

	public void setInitValue(XWTreeNode node) {
		XWTreeObject obj = node.getObject();
		if (obj instanceof XWTechnicsTreeObject) {
			XWTechnicsTreeObject object = (XWTechnicsTreeObject) obj;
			Element partElement = object.getTreeCellData();
			//wzbmText.setText(partElement.attributeValue("CMAT"));
			//chmcText.setText(partElement.attributeValue("PTC_MATERIAL_NAME"));
			xhphclText.setText(partElement.attributeValue("CMAT"));
			ggText.setText(partElement.attributeValue("CSIZE"));
			jstjText.setText(partElement.attributeValue("JSTJBZH"));
		}
	}

	@Override
	protected void initLayout() {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx=1;
		topPanel.setLayout(new GridBagLayout());
		int width = 80;
		c.gridx = 0;
		c.gridy = 0;

		//基础数据库
		topPanel.add(databaseLable,c);
		c.gridx = 1;
		databaseComboBox.setPreferredSize(new Dimension(width, 25));
		topPanel.add(databaseComboBox,c);

		//物资类别
		c.gridx = 2;
		wzlbComboBox.setPreferredSize(new Dimension(width, 25));
		topPanel.add(wzlbComboBox,c);

		//存货类别小类
		c.gridx = 3;
		wzlbComboBox2.setPreferredSize(new Dimension(width, 25));
		topPanel.add(wzlbComboBox2,c);

		//是否动态查询复选框
		c.gridx = 4;
		topPanel.add(autoQuery);

		//查询按钮
		c.gridx = 5;
		c.gridwidth = 1;
		queryButton.setPreferredSize(new Dimension(width, 25));
		topPanel.add(queryButton,c);

		//存货编码
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		topPanel.add(wzbmLable,c);
		c.gridx = 1;
		wzbmText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(wzbmText,c);

		//存货名称
		c.gridx = 2;
		topPanel.add(chmcLable,c);
		c.gridx = 3;
		chmcText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(chmcText,c);

		//型号牌号
		c.gridx = 4;
		topPanel.add(xhphclLable,c);
		c.gridx = 5;
		xhphclText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(xhphclText,c);

		//规格
		c.gridx = 6;
		topPanel.add(ggLable,c);
		c.gridx = 7;
		ggText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(ggText,c);

		// 技术条件
		c.gridy = 2;
		c.gridx = 0;
		topPanel.add(jstjLable,c);
		c.gridx = 1;
		jstjText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(jstjText,c);

		//生产厂家
		c.gridx = 2;
		topPanel.add(sccjLable,c);
		c.gridx = 3;
		sccjText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(sccjText,c);

		//主计量单位
		c.gridx = 4;
		topPanel.add(zjldwLabel,c);
		c.gridx = 5;
		zjldwText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(zjldwText,c);

		//常用符号
		c.gridx = 6;
		topPanel.add(cyfhLable,c);
		c.gridx = 7;
		cyfhBox.setPreferredSize(new Dimension(width, 25));
		topPanel.add(cyfhBox,c);
		c.gridx = 8;
		cyfhmsLable.setPreferredSize(new Dimension(50, 25));
		topPanel.add(cyfhmsLable,c);

		//附加条件
		c.gridy = 3;
		c.gridx = 0;
		topPanel.add(fjtjLabel,c);
		c.gridx = 1;
		fjtjText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(fjtjText,c);

		//供应状态/热处理
		c.gridx = 2;
		topPanel.add(gyztrclLabel,c);
		c.gridx = 3;
		gyztrclText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(gyztrclText,c);

//		c.gridx = 4;
//		topPanel.add(cxLable,c);
//		c.gridx = 5;
//		cxText.setPreferredSize(new Dimension(width, 25));
//		topPanel.add(cxText,c);
//
//		c.gridx = 6;
//		//c.gridwidth = 2;
//		cxButton.setPreferredSize(new Dimension(width, 25));
//		topPanel.add(cxButton,c);
//
//		c.gridx = 7;
//		//c.gridwidth = 2;
//		cxmsLable.setPreferredSize(new Dimension(50, 25));
//		topPanel.add(cxmsLable,c);

		c.gridy = 5;
		c.gridx = 0;
		c.gridwidth = 8;
		jlzsLable.setPreferredSize(new Dimension(700, 30));
		jlzsLable.setFont(new Font("宋体",Font.PLAIN,20));
		topPanel.add(jlzsLable,c);

		c.gridy = 6;
		c.gridx = 0;
		c.gridwidth = 9;
		topPanel.add(wzkDescLable,c);

		this.setLayout(new BorderLayout());
		this.add(topPanel,BorderLayout.NORTH);

		JPanel wzkPanel = new JPanel();
		wzkPanel.setLayout(new BorderLayout());
		final JLabel wzkLable = new JLabel("存货库表");
		wzkLable.setFont(new Font("宋体",Font.PLAIN,20));
		wzkPanel.add(wzkLable,BorderLayout.NORTH);
		wzkPanel.add(new JScrollPane(tableOp.getZTable()),BorderLayout.CENTER);
		this.add(wzkPanel,BorderLayout.CENTER);
	}

	@Override
	protected void loadInitDatas() {
		dbtype = LoadErpConfig.getInstance().getDbTypes();
		wzlb = ((KVItem)LoadErpConfig.getInstance().getWztypeVectorByOpType2(opType,"01").get(0)).getKey();
		tableHeader =new String[]{"序号","存货编码","存货名称","型号牌号","规格","技术条件","生产厂家","主计量单位","附加条件","供应状态/热处理","物资类别","物资类别编码"
				,"质量等级","封装形式","精度等级","螺纹规格/公称尺寸","机械性能等级","点参考特选要求"};

		List<Wzk> wlList = ErpToWCIntf.queryWzk(dbtype, wzlb, null, null, null, null, null, null, null, null, null);
		tableBody = new String[wlList.size()][tableHeader.length];
		jlzs = wlList.size();
		Wzk wzk;
		for(int i=0;i<wlList.size();i++){
			wzk = wlList.get(i);
			tableBody[i][0] = (i+1)+"";
			tableBody[i][1] = wzk.getInvcode();
			tableBody[i][2] = wzk.getInvname();
			tableBody[i][3] = wzk.getInvtype();
			tableBody[i][4] = wzk.getInvspec();
			//tableBody[i][5] = wzk.getJsgfbz();
			tableBody[i][5] = wzk.getJstjname();
			tableBody[i][6] = wzk.getCustname();
			tableBody[i][7] = wzk.getMeasname();
			tableBody[i][8] = wzk.getFjtjname();
			tableBody[i][9] = wzk.getDef13();
			tableBody[i][10] = wzk.getWzlb();
			tableBody[i][11] = wzk.getInvclasscode();

			tableBody[i][12] = wzk.getZldj();
			tableBody[i][13] = wzk.getDef6();
			tableBody[i][14] = wzk.getDef8();
			tableBody[i][15] = wzk.getDef12();
			tableBody[i][16] = wzk.getDef14();
			tableBody[i][17] = wzk.getDef10();

		}
		int tw1 = 200;
		tableColWidth = new int[]{tw1-175,tw1-50,tw1-30,tw1-50,tw1-50,tw1+20,tw1+20,tw1-125,tw1+20,tw1+20
				,tw1+20,tw1+20,tw1+20,tw1+20,tw1+20,tw1+20,tw1+20,tw1+20};

		if(tableOp == null) {
			tableOp =  new DefaultZTableFactory();
			tableOp.setTableInfors(tableHeader, tableBody,tableColWidth);
			tableOp.setTableStyle(tableOp.getZTable());
		}

		for (int i = 1; i < tableHeader.length; i++) {
			tableOp.getZTable().getColumn(tableHeader[i]).setPreferredWidth(tableColWidth[i]);
		}

		tableOp.setOneColumnHidden(tableOp.getZTable(), 10);
		tableOp.setOneColumnHidden(tableOp.getZTable(), 11);

		databaseModel = LoadErpConfig.getInstance().getDbTypeVector();
		wzlbMode = LoadErpConfig.getInstance().getWztypeVectorByOpType(opType);
		wzlbMode2 = LoadErpConfig.getInstance().getWztypeVectorByOpType3("01");

		cyfhMode = LoadErpConfig.getInstance().getCyfhs();
	}

	private void queryWzk() {
		dataList = ErpToWCIntf.queryWzk(dbtype, wzlb, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw, fjtj, gyztrcl);
		jlzsLable.setText("当前记录数：" + dataList.size());
		this.refreshTable();
	}

	private void refreshTable() {
		if (dataList == null) {
			return;
		}
		Object[][] tableBody = new Object[dataList.size()][tableHeader.length];
		Wzk wzk;
		//"序号","存货编码","存货名称","型号牌号","规格","技术条件","生产厂家","主计量单位","附加条件","供应状态/热处理"
		for (int i = 0; i < dataList.size(); i++) {
			wzk = dataList.get(i);
			tableBody[i][0] = (i + 1) + "";
			tableBody[i][1] = wzk.getInvcode();
			tableBody[i][2] = wzk.getInvname();
			tableBody[i][3] = wzk.getInvtype();
			tableBody[i][4] = wzk.getInvspec();
			tableBody[i][5] = wzk.getJsgfbz();
			tableBody[i][6] = wzk.getCustname();
			tableBody[i][7] = wzk.getMeasname();
			tableBody[i][8] = wzk.getFjtjname();
			tableBody[i][9] = wzk.getDef13();
			tableBody[i][10] = wzk.getWzlb();
			tableBody[i][11] = wzk.getInvclasscode();
			tableBody[i][12] = wzk.getZldj();
			tableBody[i][13] = wzk.getDef6();
			tableBody[i][14] = wzk.getDef8();
			tableBody[i][15] = wzk.getDef12();
			tableBody[i][16] = wzk.getDef14();
			tableBody[i][17] = wzk.getDef10();
		}
		DefaultTableModel dtm = (DefaultTableModel) tableOp.getTableModel();
		dtm.setDataVector(tableBody, tableHeader);
		for (int i = 1; i < tableHeader.length; i++) {
			tableOp.getZTable().getColumn(tableHeader[i]).setPreferredWidth(tableColWidth[i]);
		}
		tableOp.setTableStyle(tableOp.getZTable());

		setHiddenColumn(10);
		setHiddenColumn(11);

		tableOp.getZTable().updateUI();
	}

	ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			//2,wzlb 物资类别(01：元器件 02：标准紧固件 03：金属材料 04：非金属材料 05：复合材料 06：机电产品 07：火工品)
			wzlb =((KVItem)(wzlbComboBox2.getSelectedItem())).getKey();
			String wzbm = wzbmText.getText();//3,wzbm 存货编码
			String wzmc = chmcText.getText();//4,wzmc 存货名称
			String xhphcl = xhphclText.getText();//5,xhphcl 型号牌号
			String gg = ggText.getText();//6,gg 规格
			String jstj = jstjText.getText();//7,jstj 技术条件
			String sccj = sccjText.getText();//8,gys 生产厂家
			String zjldw = zjldwText.getText();//9,zjldw 主计量单位
			String fjtj = fjtjText.getText();//10,fjtj 附加条件
			String gyztrcl = gyztrclText.getText();//11,供应状态/热处理

			//"存货编码","存货名称","型号牌号","规格","技术条件","生产厂家","主计量单位","附加条件","供应状态/热处理"

			//查询条件：
			//1,dbtype 数据库类型 (01：优选物资库 02：ERP物资库)

			dataList = ErpToWCIntf.queryWzk(dbtype, wzlb, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw, fjtj,gyztrcl);
			jlzsLable.setText("当前记录数："+dataList.size());
			refreshTable();
		}
	};

	DocumentListener wlbmDocumentListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			wzbm = wzbmText.getText();
			queryWzk();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			wzbm = wzbmText.getText();
			queryWzk();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			wzbm = wzbmText.getText();
			queryWzk();

		}
	};

	DocumentListener cpmcDocumentListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			wzmc = chmcText.getText();
			queryWzk();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			wzmc = chmcText.getText();
			queryWzk();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			wzmc = chmcText.getText();
			queryWzk();

		}
	};
	DocumentListener xhphclDocumentListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			xhphcl = xhphclText.getText();
			queryWzk();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			xhphcl = xhphclText.getText();
			queryWzk();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			xhphcl = xhphclText.getText();
			queryWzk();

		}
	};

	DocumentListener ggDocumentListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			gg = ggText.getText();
			queryWzk();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			gg = ggText.getText();
			queryWzk();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			gg = ggText.getText();
			queryWzk();

		}
	};

	DocumentListener jstjDocumentListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			jstj = jstjText.getText();
			queryWzk();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			jstj = jstjText.getText();
			queryWzk();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			jstj = jstjText.getText();
			queryWzk();

		}
	};

	DocumentListener sccjDocumentListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			sccj = sccjText.getText();
			queryWzk();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			sccj = sccjText.getText();
			queryWzk();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			sccj = sccjText.getText();
			queryWzk();

		}
	};

	DocumentListener zjldwDocumentListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			zjldw = zjldwText.getText();
			queryWzk();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			zjldw = zjldwText.getText();
			queryWzk();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			zjldw = zjldwText.getText();
			queryWzk();

		}
	};

	DocumentListener fjtjDocumentListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			fjtj = fjtjText.getText();
			queryWzk();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			fjtj = fjtjText.getText();
			queryWzk();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			fjtj = fjtjText.getText();
			queryWzk();

		}
	};

	DocumentListener gyztrclDocumentListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			gyztrcl = gyztrclText.getText();
			queryWzk();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			gyztrcl = gyztrclText.getText();
			queryWzk();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			gyztrcl = gyztrclText.getText();
			queryWzk();

		}
	};

	private void setAutoQueryListener(boolean isAutoQuery) {
		if (isAutoQuery) {
			wzbmText.getDocument().addDocumentListener(wlbmDocumentListener);
			chmcText.getDocument().addDocumentListener(cpmcDocumentListener);
			xhphclText.getDocument().addDocumentListener(xhphclDocumentListener);
			ggText.getDocument().addDocumentListener(ggDocumentListener);
			jstjText.getDocument().addDocumentListener(jstjDocumentListener);
			sccjText.getDocument().addDocumentListener(sccjDocumentListener);
			zjldwText.getDocument().addDocumentListener(zjldwDocumentListener);
			fjtjText.getDocument().addDocumentListener(fjtjDocumentListener);
			gyztrclText.getDocument().addDocumentListener(gyztrclDocumentListener);
		} else {
			wzbmText.getDocument().removeDocumentListener(wlbmDocumentListener);
			chmcText.getDocument().removeDocumentListener(cpmcDocumentListener);
			xhphclText.getDocument().removeDocumentListener(xhphclDocumentListener);
			ggText.getDocument().removeDocumentListener(ggDocumentListener);
			jstjText.getDocument().removeDocumentListener(jstjDocumentListener);
			sccjText.getDocument().removeDocumentListener(sccjDocumentListener);
			zjldwText.getDocument().removeDocumentListener(zjldwDocumentListener);
			fjtjText.getDocument().removeDocumentListener(fjtjDocumentListener);
			gyztrclText.getDocument().removeDocumentListener(gyztrclDocumentListener);
		}
	}

	public void setHiddenColumn(int columnIndex) {
		if (columnIndex >= 0 && columnIndex < tableOp.getZTable().getColumnCount()) {
			tableOp.getZTable().getTableHeader().getColumnModel().getColumn(columnIndex).setMaxWidth(0);
			tableOp.getZTable().getTableHeader().getColumnModel().getColumn(columnIndex).setMinWidth(0);
			tableOp.getZTable().getColumnModel().getColumn(columnIndex).setMaxWidth(0);
			tableOp.getZTable().getColumnModel().getColumn(columnIndex).setPreferredWidth(0);
			tableOp.getZTable().getColumnModel().getColumn(columnIndex).setWidth(0);
			tableOp.getZTable().getColumnModel().getColumn(columnIndex).setMinWidth(0);
		}
	}

	public static String emptyToString(String obj) {
		if (null == obj || "".equals(obj.trim())) {
			return "";
		} else {
			return obj;
		}
	}
}
