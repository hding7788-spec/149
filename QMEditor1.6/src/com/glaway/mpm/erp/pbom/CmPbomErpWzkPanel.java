package com.glaway.mpm.erp.pbom;

import com.glaway.mpm.erp.*;
import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.wcIntf.ErpToWCIntf;

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
public class CmPbomErpWzkPanel extends  CmAbstractPanel{

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

	private JLabel zjldwLable; //主计量单位
	private JTextField zjldwText;

	private JLabel zldjLable;// 质量等级
	private JTextField zldjText;

	private JLabel sccjLable; //生产厂家
	private JTextField sccjText;

	private JLabel cyfhLable; //常用符号
	private JComboBox cyfhBox;

	private JLabel cyfhmsLable;
	private String[] cyfhMode = null;

	private JLabel jstjLable; // 技术条件
	private JTextField jstjText;

	private JLabel fjtjLabel;//附加条件
	private JTextField fjtjText;

	private JLabel lwgggcccLabel;//螺纹规格/公称尺寸
	private JTextField lwgggcccText;

	private JLabel jxxndjLabel;//机械性能等级
	private JTextField jxxndjText;

	private JLabel fzxsLabel;//封装形式
	private JTextField fzxsText;

	private JLabel jddjLabel;//精度等级
	private JTextField jddjText;

	private JLabel  jlzsLable; //记录总数量

	public  JTextArea wzkDescLable;

	private JCheckBox autoQuery;

	//private JLabel cxLable;
	//private JTextField cxText;
	//private JButton cxButton;
	//private JLabel cxmsLable;

	private JButton queryButton;

	private long jlzs;

	private int opType;
	private int flag;

	private String dbtype, wzlb, wzbm, wzmc, xhphcl, gg, jstj, zjldw, zldj, sccj,fjtj,lwgggccc,jxxndj,fzxs,jddj;

	private List<Wzk> dataList;

	public CmPbomErpWzkPanel(int opType,int flag){
		this.opType = opType;
		this.flag = flag;
		loadInitDatas();
		initDimension();
		initComponents();
		initActions();
		initLayout();
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
				wzlbComboBox2.setSelectedIndex(0);
			}
		});

		wzlbComboBox2.addActionListener(actionListener);

		databaseComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dbtype =((KVItem)(databaseComboBox.getSelectedItem())).getKey();//1,dbtype 数据库类型 (01：优选物资库 02：ERP物资库)
				String wzbm = wzbmText.getText();//3,wzbm 存货编码
				String wzmc = chmcText.getText();//4,wzmc 存货名称
				String xhphcl = xhphclText.getText();//5,xhphcl 型号牌号
				String gg = ggText.getText();//6,gg 规格
				String jstj = jstjText.getText();//7,jstj 技术条件
				String sccj = sccjText.getText();//8,gys 生产厂家
				String zjldw = zjldwText.getText();//9,zjldw 主计量单位
				String fjtj = fjtjText.getText();//10,fjtj 附加条件
				String lwgggccc = lwgggcccText.getText();//11,lwgggccc 螺纹规格/公称尺寸
				String jxxndj = jxxndjText.getText();//12,jxxndj 机械性能等级
				String zldj = zldjText.getText();//13,zldj 质量等级
				String fzxs = fzxsText.getText();//14,fzxs 封装形式
				String jddj = jddjText.getText();//15,jddj 精度等级

				//查询条件：
				//2,wzlb 物资类别(01：元器件 02：标准紧固件 03：金属材料 04：非金属材料 05：复合材料 06：机电产品 07：火工品)

				//"存货编码","存货名称","型号牌号","规格","技术条件","生产厂家","主计量单位","附加条件","螺纹规格/公称尺寸","机械性能等级","质量等级","封装形式","精度等级"
				dataList = ErpToWCIntf.queryWzk(dbtype, wzlb, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw,fjtj,lwgggccc,jxxndj, zldj,fzxs,jddj);
				jlzsLable.setText("当前记录数："+dataList.size());
				refreshTable();
			}
		});

		queryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dbtype =((KVItem)(databaseComboBox.getSelectedItem())).getKey();//1,dbtype 数据库类型 (01：优选物资库 02：ERP物资库)
				String wzbm = wzbmText.getText();//3,wzbm 存货编码
				String wzmc = chmcText.getText();//4,wzmc 存货名称
				String xhphcl = xhphclText.getText();//5,xhphcl 型号牌号
				String gg = ggText.getText();//6,gg 规格
				String jstj = jstjText.getText();//7,jstj 技术条件
				String sccj = sccjText.getText();//8,gys 生产厂家
				String zjldw = zjldwText.getText();//9,zjldw 主计量单位
				String fjtj = fjtjText.getText();//10,fjtj 附加条件
				String lwgggccc = lwgggcccText.getText();//11,lwgggccc 螺纹规格/公称尺寸
				String jxxndj = jxxndjText.getText();//12,jxxndj 机械性能等级
				String zldj = zldjText.getText();//13,zldj 质量等级
				String fzxs = fzxsText.getText();//14,fzxs 封装形式
				String jddj = jddjText.getText();//15,jddj 精度等级

				dataList = ErpToWCIntf.queryWzk(dbtype, wzlb, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw,fjtj,lwgggccc,jxxndj, zldj,fzxs,jddj);
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
					sb.append("物资编码:"+ emptyToString((String)wltm.getValueAt(row, 1))+"  ;");
					sb.append("物资名称:"+ emptyToString((String)wltm.getValueAt(row, 2))+"  ;");
					sb.append("型号牌号:"+ emptyToString((String)wltm.getValueAt(row, 3))+"  ;");
					sb.append("规格:"+ emptyToString((String)wltm.getValueAt(row, 4))+"  ;");
					sb.append("技术条件:"+ emptyToString((String)wltm.getValueAt(row, 5))+"  ;");
					sb.append("生产厂家:"+ emptyToString((String)wltm.getValueAt(row, 6))+"  ;");
					sb.append("主计量单位:"+ ((String)wltm.getValueAt(row, 7))+"  ;");
					sb.append("附加条件:"+ emptyToString((String)wltm.getValueAt(row, 8))+"  ;");
					sb.append("螺纹规格/公称尺寸:"+ emptyToString((String)wltm.getValueAt(row, 9))+"  ;");
					sb.append("机械性能等级:"+ emptyToString((String)wltm.getValueAt(row, 10))+"  ;");
					sb.append("质量等级:"+ emptyToString((String)wltm.getValueAt(row, 11))+"  ;");
					sb.append("封装形式:"+ emptyToString((String)wltm.getValueAt(row, 12))+"  ;");
					sb.append("精度等级:"+ emptyToString((String)wltm.getValueAt(row, 13))+"  ;");
					sb.append("产品代号:"+ emptyToString((String)wltm.getValueAt(row, 16))+"  ;");
					sb.append("电参数特选要求:"+ emptyToString((String)wltm.getValueAt(row, 17)).replaceAll("\r|\n", "")+"  ;");
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

		databaseComboBox = new KVJComboBox(databaseModel);//数据库
		databaseComboBox.setEnabled(false);
		wzlbComboBox = new KVJComboBox(wzlbMode);//存货类型

		wzlbComboBox2 = new KVJComboBox(wzlbMode2);

		wzbmLable = new JLabel("存货编码");
		wzbmText = new JTextField();

		chmcLable = new JLabel("存货名称");
		chmcText = new JTextField();

		xhphclLable = new JLabel("型号牌号");
		xhphclText = new JTextField();

		ggLable = new JLabel("规格");
		ggText = new JTextField();

		zjldwLable = new JLabel("主计量单位");
		zjldwText = new JTextField();

		zldjLable = new JLabel("质量等级");
		zldjText = new JTextField();

		sccjLable = new JLabel("生产厂家");
		sccjText = new JTextField();

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

		lwgggcccLabel = new JLabel("螺纹规格/公称尺寸");
		lwgggcccText = new JTextField();

		jxxndjLabel = new JLabel("机械性能等级");
		jxxndjText = new JTextField();

		fzxsLabel = new JLabel("封装形式");
		fzxsText = new JTextField();

		jddjLabel = new JLabel("精度等级");
		jddjText = new JTextField();

		//cxLable = new JLabel("带@编码");
		//cxText = new JTextField();
		//cxmsLable = new JLabel("如果带@查询出错...");
		//cxmsLable.setToolTipText("如果带@查询出错,则说明该模板最新的BOM模板,请在以上文本框最后添加@在查询");
		//cxButton = new JButton("带@查询");

		queryButton = new JButton("查  询");

		tableOp =  new DefaultZTableFactory();
		tableOp.setTableInfors(tableHeader, tableBody,tableColWidth);
		tableOp.setTableStyle(tableOp.getZTable());
		tableOp.setColumnsHidden(tableOp.getZTable(), new int[]{14,15});

	}

	public void setInitValue(XWTreeNode node) {
//		CmLightPart part = node.getPart();
//		if(this.flag != 1) {
//			wzbmText.setText(part.getPartNumber());
//			chmcText.setText(part.getPartName());
//			xhphclText.setText(part.getXhph());
//			ggText.setText(part.getCsize());
//			jstjText.setText(part.getJstj());
//		}
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

		topPanel.add(databaseLable,c);
		c.gridx = 1;
		databaseComboBox.setPreferredSize(new Dimension(width, 25));
		topPanel.add(databaseComboBox,c);

		c.gridx = 2;
		wzlbComboBox.setPreferredSize(new Dimension(width, 25));
		topPanel.add(wzlbComboBox,c);

		c.gridx = 3;
		wzlbComboBox2.setPreferredSize(new Dimension(width, 25));
		topPanel.add(wzlbComboBox2,c);

		c.gridx = 4;
		topPanel.add(autoQuery);
		c.gridx = 7;
		c.gridwidth = 2;
		queryButton.setPreferredSize(new Dimension(width, 25));
		topPanel.add(queryButton,c);

		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		topPanel.add(wzbmLable,c);

		c.gridx = 1;
		wzbmText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(wzbmText,c);

		c.gridx = 2;
		topPanel.add(chmcLable,c);
		c.gridx = 3;
		chmcText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(chmcText,c);

		c.gridx = 4;
		topPanel.add(xhphclLable,c);
		c.gridx = 5;
		xhphclText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(xhphclText,c);

		c.gridx = 6;
		topPanel.add(ggLable,c);
		c.gridx = 7;
		ggText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(ggText,c);

		c.gridy = 2;
		c.gridx = 0;
		topPanel.add(zjldwLable,c);
		c.gridx = 1;
		zjldwText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(zjldwText,c);

		c.gridx = 2;
		topPanel.add(zldjLable,c);
		c.gridx = 3;
		zldjText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(zldjText,c);

		c.gridx = 4;
		topPanel.add(sccjLable,c);
		c.gridx = 5;
		sccjText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(sccjText,c);

		c.gridx = 6;
		topPanel.add(cyfhLable,c);
		c.gridx = 7;
		cyfhBox.setPreferredSize(new Dimension(width, 25));
		topPanel.add(cyfhBox,c);
		c.gridx = 8;
		cyfhmsLable.setPreferredSize(new Dimension(50, 25));
		topPanel.add(cyfhmsLable,c);


		c.gridy = 3;
		c.gridx = 0;
		topPanel.add(jstjLable,c);
		c.gridx = 1;
		jstjText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(jstjText,c);

		//附加条件
		c.gridy = 3;
		c.gridx = 2;
		topPanel.add(fjtjLabel,c);
		c.gridx = 3;
		fjtjText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(fjtjText,c);

		//螺纹规格/公称尺寸
		c.gridy = 3;
		c.gridx = 4;
		topPanel.add(lwgggcccLabel,c);
		c.gridx = 5;
		lwgggcccText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(lwgggcccText,c);

		//机械性能等级
		c.gridy = 3;
		c.gridx = 6;
		topPanel.add(jxxndjLabel,c);
		c.gridx = 7;
		jxxndjText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(jxxndjText,c);

		//封装形式
		c.gridy = 4;
		c.gridx = 0;
		topPanel.add(fzxsLabel,c);
		c.gridx = 1;
		fzxsText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(fzxsText,c);

		//精度等级
		c.gridy = 4;
		c.gridx = 2;
		topPanel.add(jddjLabel,c);
		c.gridx = 3;
		jddjText.setPreferredSize(new Dimension(width, 25));
		topPanel.add(jddjText,c);

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
		tableHeader =new String[]{"序号","存货编码","存货名称","型号牌号","规格","技术条件","生产厂家","主计量单位","附加条件","螺纹规格/公称尺寸","机械性能等级","质量等级","封装形式","精度等级","物资类别","物资类别编码","产品代号","电参数特选要求"};

		List<Wzk> wlList = ErpToWCIntf.queryWzk(dbtype, wzlb, null, null, null, null, null, null, null, null, null, null, null, null, null);
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
			tableBody[i][5] = wzk.getJstjname();
			tableBody[i][6] = wzk.getCustname();
			tableBody[i][7] = wzk.getMeasname();
			tableBody[i][8] = wzk.getFjtjname();
			tableBody[i][9] = wzk.getDef12();
			tableBody[i][10] = wzk.getDef14();
			tableBody[i][11] = wzk.getZldj();
			tableBody[i][12] = wzk.getDef6();
			tableBody[i][13] = wzk.getDef8();
			tableBody[i][14] = wzk.getWzlb();
			tableBody[i][15] = wzk.getInvclasscode();
			tableBody[i][16] = wzk.getDef5();
			tableBody[i][17] = wzk.getDef10();

		}
		int tw1 = 200;
		tableColWidth = new int[]{tw1-175,tw1-50,tw1-30,tw1-50,tw1+60,tw1+20,tw1+20,tw1+20,tw1+20,tw1+20,tw1+20,tw1+20,tw1+20,tw1+20,tw1,tw1,tw1,tw1};
		databaseModel = LoadErpConfig.getInstance().getDbTypeVector();
		wzlbMode = LoadErpConfig.getInstance().getWztypeVectorByOpType2(opType);
		wzlbMode2 = LoadErpConfig.getInstance().getWztypeVectorByOpType3("01");
		cyfhMode = LoadErpConfig.getInstance().getCyfhs();
	}

	private void queryWzk() {
		dataList = ErpToWCIntf.queryWzk(dbtype, wzlb, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw,fjtj,lwgggccc,jxxndj, zldj,fzxs,jddj);
		jlzsLable.setText("当前记录数：" + dataList.size());
		this.refreshTable();
	}

	private void refreshTable() {
		if (dataList == null) {
			return;
		}
		Object[][] tableBody = new Object[dataList.size()][tableHeader.length];
		Wzk wzk;
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
			tableBody[i][9] = wzk.getDef12();
			tableBody[i][10] = wzk.getDef14();
			tableBody[i][11] = wzk.getZldj();
			tableBody[i][12] = wzk.getDef6();
			tableBody[i][13] = wzk.getDef8();
			tableBody[i][14] = wzk.getWzlb();
			tableBody[i][15] = wzk.getInvclasscode();
			tableBody[i][16] = wzk.getDef5();
			tableBody[i][17] = wzk.getDef10();
		}
		DefaultTableModel dtm = (DefaultTableModel) tableOp.getTableModel();
		dtm.setDataVector(tableBody, tableHeader);
		for (int i = 1; i < tableHeader.length; i++) {
			tableOp.getZTable().getColumn(tableHeader[i])
					.setPreferredWidth(tableColWidth[i]);
		}
		tableOp.setTableStyle(tableOp.getZTable());
		tableOp.getZTable().updateUI();

	}
	ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			wzlb =((KVItem)(wzlbComboBox2.getSelectedItem())).getKey();
			dbtype =((KVItem)(databaseComboBox.getSelectedItem())).getKey();//1,dbtype 数据库类型 (01：优选物资库 02：ERP物资库)
			String wzbm = wzbmText.getText();//3,wzbm 存货编码
			String wzmc = chmcText.getText();//4,wzmc 存货名称
			String xhphcl = xhphclText.getText();//5,xhphcl 型号牌号
			String gg = ggText.getText();//6,gg 规格
			String jstj = jstjText.getText();//7,jstj 技术条件
			String sccj = sccjText.getText();//8,gys 生产厂家
			String zjldw = zjldwText.getText();//9,zjldw 主计量单位
			String fjtj = fjtjText.getText();//10,fjtj 附加条件
			String lwgggccc = lwgggcccText.getText();//11,lwgggccc 螺纹规格/公称尺寸
			String jxxndj = jxxndjText.getText();//12,jxxndj 机械性能等级
			String zldj = zldjText.getText();//13,zldj 质量等级
			String fzxs = fzxsText.getText();//14,fzxs 封装形式
			String jddj = jddjText.getText();//15,jddj 精度等级
			dataList = ErpToWCIntf.queryWzk(dbtype, wzlb, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw,fjtj,lwgggccc,jxxndj, zldj,fzxs,jddj);
			jlzsLable.setText("当前记录数："+dataList.size());
			refreshTable();
		}
	};


	//存货编码监听
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

	//存货名称监听
	DocumentListener chmcDocumentListener = new DocumentListener() {

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

	//型号牌号监听
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

	//规格
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

	//技术条件
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

	//主计量单位监听
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

	//质量等级监听
	DocumentListener zldjDocumentListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			zldj = zldjText.getText();
			queryWzk();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			zldj = zldjText.getText();
			queryWzk();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			zldj = zldjText.getText();
			queryWzk();

		}
	};

	//生产厂家监听
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

	//附加条件
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

	//螺纹规格/公称尺寸
	DocumentListener lwgggcccDocumentListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			lwgggccc = lwgggcccText.getText();
			queryWzk();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			lwgggccc = lwgggcccText.getText();
			queryWzk();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			lwgggccc = lwgggcccText.getText();
			queryWzk();
		}
	};

	//机械性能等级
	DocumentListener jxxndjDocumentListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			jxxndj = jxxndjText.getText();
			queryWzk();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			jxxndj = jxxndjText.getText();
			queryWzk();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			jxxndj = jxxndjText.getText();
			queryWzk();

		}
	};

	//封装形式监听
	DocumentListener fzxsDocumentListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			fzxs = fzxsText.getText();
			queryWzk();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			fzxs = fzxsText.getText();
			queryWzk();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			fzxs = fzxsText.getText();
			queryWzk();

		}
	};

	//精度等级监听
	DocumentListener jddjDocumentListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			jddj = jddjText.getText();
			queryWzk();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			jddj = jddjText.getText();
			queryWzk();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			jddj = jddjText.getText();
			queryWzk();

		}
	};

	private void setAutoQueryListener(boolean isAutoQuery) {
		if (isAutoQuery) {
			wzbmText.getDocument().addDocumentListener(wlbmDocumentListener);// 存货编码
			chmcText.getDocument().addDocumentListener(chmcDocumentListener);// 存货名称
			xhphclText.getDocument().addDocumentListener(xhphclDocumentListener);// 型号牌号
			ggText.getDocument().addDocumentListener(ggDocumentListener);// 规格
			jstjText.getDocument().addDocumentListener(jstjDocumentListener);// 技术条件
			zjldwText.getDocument().addDocumentListener(zjldwDocumentListener);// 主计量单位
			zldjText.getDocument().addDocumentListener(zldjDocumentListener);// 质量等级
			sccjText.getDocument().addDocumentListener(sccjDocumentListener);// 生产厂家
			fjtjText.getDocument().addDocumentListener(fjtjDocumentListener);// 附加条件
			lwgggcccText.getDocument().addDocumentListener(lwgggcccDocumentListener);// 螺纹规格/公称尺寸
			jxxndjText.getDocument().addDocumentListener(jxxndjDocumentListener);// 机械性能等级
			fzxsText.getDocument().addDocumentListener(fzxsDocumentListener);// 封装形式
			jddjText.getDocument().addDocumentListener(jddjDocumentListener);// 精度等级

		} else {
			wzbmText.getDocument().removeDocumentListener(wlbmDocumentListener);// 存货编码
			chmcText.getDocument().removeDocumentListener(chmcDocumentListener);// 存货名称
			xhphclText.getDocument().removeDocumentListener(xhphclDocumentListener);// 型号牌号
			ggText.getDocument().removeDocumentListener(ggDocumentListener);// 规格
			jstjText.getDocument().removeDocumentListener(jstjDocumentListener);// 技术条件
			zjldwText.getDocument().removeDocumentListener(zjldwDocumentListener);// 主计量单位
			zldjText.getDocument().removeDocumentListener(zldjDocumentListener);// 质量等级
			sccjText.getDocument().removeDocumentListener(sccjDocumentListener);// 生产厂家
			fjtjText.getDocument().removeDocumentListener(fjtjDocumentListener);// 附加条件
			lwgggcccText.getDocument().removeDocumentListener(lwgggcccDocumentListener);// 螺纹规格/公称尺寸
			jxxndjText.getDocument().removeDocumentListener(jxxndjDocumentListener);// 机械性能等级
			fzxsText.getDocument().removeDocumentListener(fzxsDocumentListener);// 封装形式
			jddjText.getDocument().removeDocumentListener(jddjDocumentListener);// 精度等级
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
