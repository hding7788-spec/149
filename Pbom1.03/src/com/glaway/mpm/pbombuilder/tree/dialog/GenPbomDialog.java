package com.glaway.mpm.pbombuilder.tree.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.pbom.table.DefaultZTableFactory;
import com.glaway.mpm.pbom.table.ZTableOp;
import com.glaway.mpm.pbombuilder.bom.CMGenPbomConnectFrame;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.panel.ErpWzkPanel;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.erp.AbstractERPDialog;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.ErpUtil;
import com.glaway.mpm.pbombuilder.wcInterface.ErpToWCIntf;

/**
 * 填写材料编码
 * @author Administrator
 *
 */
public class GenPbomDialog extends AbstractERPDialog {

	private static final long serialVersionUID = 1L;

	private ErpWzkPanel wzkPanel = new ErpWzkPanel(ErpUtil.ERP_OP_TYPE_GENBOM,4) ;

	private ZTableOp partTableOp;
	private String[] partTableHeader = null;
	private String[][] partTableBody ;
	private int[] partTableColWidth ;
	private int[] partTableEditCols;

	private JButton saveButton;
	private JButton deleteButton;
	private JButton deleteAllButton;


	private JPanel centerPanel;

	private JPanel bottomPanel;

	private List<CmTreeNode> selPbomNodes;

	private Window owner;


	public GenPbomDialog(Window owner,String title) {
		this.setTitle(title);
		this.owner = owner;
		loadInitDatas();
		initDimension();
		initComponents();

		initActions();
		initLayout();
		this.setResizable(true);
		this.setModal(true);

		this.setVisible(true);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());
	}

	protected void initActions(){
		wzkPanel.getTableOp().getZTable().addMouseListener(new MouseAdapter() {
			int row = -1;
			TableModel  wztm,parttm;
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				wztm = wzkPanel.getTableOp().getTableModel();
				parttm = partTableOp.getTableModel();
				row = wzkPanel.getTableOp().getZTable().getSelectedRow();
				if(row==-1){
					return;
				}
				if(e.getClickCount()==2){
					parttm = partTableOp.getTableModel();
					int pcunt = parttm.getRowCount();
					String wzbm = (String)wztm.getValueAt(row, 1);
					boolean isExist = false;
					for(int i=0;i<pcunt;i++){
						if(wzbm.equals((String)parttm.getValueAt(i, 2))){
							isExist = true;
							break;
						}
					}
					if(isExist){
						JOptionPane.showMessageDialog(owner, "【"+wzbm+"】物资编码已经使用");
						return ;
					}
					Object [] rows = new Object[partTableHeader.length];
					rows[0] = (parttm.getRowCount()+1)+"";
					rows[1] = "";
					rows[2] = wztm.getValueAt(row, 1);
					rows[3] = wztm.getValueAt(row, 1);
					rows[4] = wztm.getValueAt(row, 2);
					rows[5] = 1+"";
					rows[6] = wztm.getValueAt(row, 3);
					rows[7] = wztm.getValueAt(row, 4);
					rows[8] = wztm.getValueAt(row, 5);
					rows[9] = wztm.getValueAt(row, 6);
					rows[10] = wztm.getValueAt(row, 7);
					rows[11] = wztm.getValueAt(row, 8);
					rows[12] = wztm.getValueAt(row, 9);

					rows[13] = wztm.getValueAt(row, 10);
					rows[14] = wztm.getValueAt(row, 11);
					rows[15] = wztm.getValueAt(row, 12);
					rows[16] = wztm.getValueAt(row, 13);
					rows[17] = wztm.getValueAt(row, 14);
					rows[18] = wztm.getValueAt(row, 15);
					partTableOp.addOneRow(rows);




				}
			}

		});
		deleteButton.addActionListener(new ActionListener() {
			JTable ptable = partTableOp.getZTable();
			DefaultTableModel dtm = (DefaultTableModel)partTableOp.getTableModel();
			@Override
			public void actionPerformed(ActionEvent e) {
				int isDelete = JOptionPane.showConfirmDialog(owner, "确定要删除所选行吗？","确定",JOptionPane.YES_NO_OPTION);
				if(isDelete==JOptionPane.YES_OPTION){
					int numrow = ptable.getSelectedRows().length;
					for (int i = 0; i < numrow; i++) {
						dtm.removeRow(ptable.getSelectedRow());
					}
				}

			}
		});


		deleteAllButton.addActionListener(new ActionListener() {
			DefaultTableModel dtm = (DefaultTableModel)partTableOp.getTableModel();
			@Override
			public void actionPerformed(ActionEvent e) {
				int isDelete = JOptionPane.showConfirmDialog(owner, "确定要删除所选行吗？","确定",JOptionPane.YES_NO_OPTION);
				if(isDelete==JOptionPane.YES_OPTION){
					   int rowcount = dtm.getRowCount() - 1;
					   while(rowcount>=0){
						   dtm.removeRow(rowcount);
						   dtm.setRowCount(rowcount);
						   rowcount = dtm.getRowCount() - 1;
					   }


				}

			}
		});

		saveButton.addActionListener(new SaveGenPbomListener(owner,partTableOp.getZTable()));

	}

	@Override
	protected void initComponents() {
		partTableOp =  new DefaultZTableFactory();
		partTableOp.setColumnsEditable(partTableEditCols);
		partTableOp.setTableInfors(partTableHeader, partTableBody,partTableColWidth);
		partTableOp.setTableStyle(partTableOp.getZTable());
//		partTableOp.setColumnsHidden(partTableOp.getZTable(), new int[]{18});

		saveButton = new JButton("保 存");
		deleteButton = new JButton("删 除选择行");
		deleteAllButton = new JButton("删除所有行");

		centerPanel = new JPanel();


		bottomPanel = new JPanel();



	}
	protected void loadInitDatas() {
		partTableHeader = new String[]{"序号","上级图号","图号","物资编码","产品名称","子项数量","型号/牌号/材料","规格","总规范/标准代号/技术条件","详细规范","国产/进口","供应商","计量单位","质量等级","封装形式","尺寸","专用条件","附加协议","电参数特选要求",
				"公司编码","工厂编码","产品编码","版本号","父项数量","上级图号分类编码","上级图号名称","上级图号所属型号","上级图号研制阶段","上级图号计量单位名称",
				"表头备注","是否默认","材料分类编码","图号名称","所属型号","研制阶段","单机工艺定额","下料尺寸",
				"毛坯可制件数","表体备注","密度","物资自由项1","物资自由项2","物资自由项3" ,"材料分类编码"};
		List<Wzk> dataList = ErpToWCIntf.queryExcelPbom(CMGenPbomConnectFrame.docId);
		int cnt = 0;
		if(dataList!=null){
			cnt = dataList.size();
		}
		partTableBody = new String[cnt][partTableHeader.length];
		Wzk w;
		for(int i=0;i<cnt;i++){
			w = dataList.get(i);
			partTableBody[i][0]=(i+1)+"";
			partTableBody[i][1]= w.getSjth();
			partTableBody[i][2]= w.getTh();
			partTableBody[i][3]= w.getInvcode();
			partTableBody[i][4]= w.getInvname();
			partTableBody[i][5]= w.getZxsl();
			partTableBody[i][6] = w.getInvtype();
			partTableBody[i][7] = w.getInvspec();
			partTableBody[i][8] = w.getDef2();
			partTableBody[i][9] = w.getDef3();
			partTableBody[i][10] = w.getIsJinKou();
			partTableBody[i][11] = w.getCustname();
			partTableBody[i][12] = w.getMeasname();
			partTableBody[i][13] = w.getDef4();
			partTableBody[i][14] = w.getDef5();
			partTableBody[i][15] = w.getDef6();
			partTableBody[i][16] = w.getDef7();
			partTableBody[i][17] = w.getDef8();
			partTableBody[i][18] = w.getDef9();
			//其他隐藏字段
			partTableBody[i][19] = w.getGsdm();
			partTableBody[i][20] = w.getGcbm();
			partTableBody[i][21] = w.getCpbm();
			partTableBody[i][22] = w.getVersion();
			partTableBody[i][23] = w.getFxsl();
			partTableBody[i][24] = w.getSjthflbm();
			partTableBody[i][25] = w.getSjthmc();
			partTableBody[i][26] = w.getSjthssxh();
			partTableBody[i][27] = w.getSjthyzjd();
			partTableBody[i][28] = w.getSjthjldwmc();
			partTableBody[i][29] = w.getBtbz();
			partTableBody[i][30] = w.getSfmr();
			partTableBody[i][31] = w.getClflbm();
			partTableBody[i][32] = w.getThmc();
			partTableBody[i][33] = w.getSsxh();
			partTableBody[i][34] = w.getYzjd();
			partTableBody[i][35] = w.getDjgyde();
			partTableBody[i][36] = w.getMpcc();
			partTableBody[i][37] = w.getMpkzjs();
			partTableBody[i][38] = w.getBtibz();
			partTableBody[i][39] = w.getMd();
			partTableBody[i][40] = w.getWzzyx1();
			partTableBody[i][41] = w.getWzzyx2();
			partTableBody[i][42] = w.getWzzyx3();
			partTableBody[i][43] = w.getInvclasscode();


		}


		int tw1 = 200;
		partTableColWidth = new int[]{tw1-100,tw1,tw1+30,tw1,tw1+50,tw1+20,tw1,tw1,tw1-20,tw1-20,tw1-20,tw1-20,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1};
		partTableEditCols =new int[]{1,5};


	}
	@Override
	protected void initLayout() {
		JScrollPane wztmsp = new JScrollPane(partTableOp.getZTable());
		wztmsp.getViewport().setBackground(Color.WHITE);
		BoxLayout box = new BoxLayout (centerPanel,BoxLayout.Y_AXIS) ;
		centerPanel.setLayout(box);
		centerPanel.add(wzkPanel);
		final JLabel lbjLable = new JLabel("零部件表");
		lbjLable.setFont(new Font("宋体",Font.PLAIN,20));
		JPanel partPanel = new JPanel();
		partPanel.setLayout(new BorderLayout());
		partPanel.add(lbjLable,BorderLayout.NORTH);
		partPanel.add(wztmsp,BorderLayout.CENTER);
		centerPanel.add(partPanel);
		bottomPanel.add(saveButton);
		bottomPanel.add(deleteButton);
		bottomPanel.add(deleteAllButton);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
//		mainPanel.add(wzkPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel,BorderLayout.CENTER);
		mainPanel.add(bottomPanel,BorderLayout.SOUTH);

		this.setContentPane(mainPanel);



	}

	@Override
	protected void registerTaskExecutor() throws CmTaskException {

	}

	@Override
	protected void unregisterTaskExecutor() throws CmTaskException {

	}

	class SaveGenPbomListener implements ActionListener {
		private Window owner;
		private JTable table;

		public SaveGenPbomListener(Window owner,JTable table){
			this.table = table;
		}
		public void actionPerformed(ActionEvent e) {
			int flag = JOptionPane.showConfirmDialog(owner, "确定保存吗？","确定",JOptionPane.YES_NO_OPTION);
			if(flag == 0){
				TableModel tm = table.getModel();
				int rows = tm.getRowCount();
				Wzk wzk = null;
				List<Wzk> dataList = new ArrayList<Wzk>();
				for(int i=0;i<rows;i++){
					Wzk zwzk = new Wzk();
					wzk = new Wzk();
					String sjth = (String)tm.getValueAt(i, 1);
					if(!CmCommonStringUtil.isEmpty(sjth)){
						zwzk.setSjth(sjth);

					}else{

					}
					wzk.setSjth(sjth);
					wzk.setTh((String)tm.getValueAt(i, 2));
					wzk.setInvcode((String)tm.getValueAt(i, 3));
					wzk.setInvname((String)tm.getValueAt(i, 4));
					wzk.setZxsl((String)tm.getValueAt(i, 5));
					wzk.setInvtype((String)tm.getValueAt(i, 6));
					wzk.setInvspec((String)tm.getValueAt(i, 7));
					wzk.setDef2((String)tm.getValueAt(i, 8));
					wzk.setDef3((String)tm.getValueAt(i, 9));
					String isJinKou = "N";
					if("进口".equals((String)tm.getValueAt(i, 10))){
						isJinKou = "Y";
					}
					wzk.setDef1(isJinKou);
					wzk.setCustname((String)tm.getValueAt(i, 11));
					wzk.setMeasname((String)tm.getValueAt(i, 12));
					wzk.setDef4((String)tm.getValueAt(i, 13));
					wzk.setDef5((String)tm.getValueAt(i, 14));
					wzk.setDef6((String)tm.getValueAt(i, 15));
					wzk.setDef7((String)tm.getValueAt(i, 16));
					wzk.setDef8((String)tm.getValueAt(i, 17));
					wzk.setDef9((String)tm.getValueAt(i, 18));

					wzk.setGsdm((String)tm.getValueAt(i, 19));
					wzk.setGcbm((String)tm.getValueAt(i, 20));
					wzk.setCpbm((String)tm.getValueAt(i, 21));
					wzk.setVersion((String)tm.getValueAt(i, 22));
					wzk.setFxsl((String)tm.getValueAt(i, 23));

					wzk.setSjthflbm((String)tm.getValueAt(i, 24));
					wzk.setSjthmc((String)tm.getValueAt(i, 25));
					wzk.setSjthssxh((String)tm.getValueAt(i, 26));
					wzk.setSjthyzjd((String)tm.getValueAt(i, 27));
					wzk.setSjthjldwmc((String)tm.getValueAt(i, 28));
					wzk.setBtbz((String)tm.getValueAt(i, 29));
					wzk.setSfmr((String)tm.getValueAt(i, 30));
					wzk.setClflbm((String)tm.getValueAt(i, 31));
					wzk.setThmc((String)tm.getValueAt(i, 32));
					wzk.setSsxh((String)tm.getValueAt(i, 33));
					wzk.setYzjd((String)tm.getValueAt(i, 34));
					wzk.setDjgyde((String)tm.getValueAt(i, 35));

					wzk.setMpcc((String)tm.getValueAt(i, 36));
					wzk.setMpkzjs((String)tm.getValueAt(i, 37));
					wzk.setBtibz((String)tm.getValueAt(i, 38));
					wzk.setMd((String)tm.getValueAt(i, 39));
					wzk.setWzzyx1((String)tm.getValueAt(i, 40));
					wzk.setWzzyx2((String)tm.getValueAt(i, 41));
					wzk.setWzzyx3((String)tm.getValueAt(i, 42));
					wzk.setClmc((String)tm.getValueAt(i, 4));
					wzk.setInvclasscode((String)tm.getValueAt(i, 43));
					dataList.add(wzk);
//					if(!CmCommonStringUtil.isEmpty(sjth)){
//						dataList.add(zwzk);
//					}
				}

				String docNumber = null;
				 String docId = null;
				if(CMGenPbomConnectFrame.docId==null){
					 docNumber = JOptionPane.showInputDialog(owner, "请输入文档编号！");
					 if(ErpToWCIntf.isExistDocument(docNumber)){
						 JOptionPane.showMessageDialog(owner, "编号【"+docNumber+"】文档已经存在！");
					 }else{
						 docId =  ErpToWCIntf.genDocSavePbomExcel(CMGenPbomConnectFrame.containerId, docNumber, dataList);
					 }

				}else{
					docId =  ErpToWCIntf.editDocSavePbomExcel(CMGenPbomConnectFrame.docId, dataList);

				}
				CMGenPbomConnectFrame.docId = docId;
			}
		}
	}

	class EditPbomAttribute extends MouseAdapter{
			private Window owner;
			private JTable table;
			public EditPbomAttribute(Window owner,JTable table){
				this.table = table;
				this.owner = owner;
			}
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(e.getClickCount()==2){
					int row = table.getSelectedRow();
					if(row!=-1){
						 	String name = (String)table.getValueAt(row, 2);
							EditPbomDialog dialog = new EditPbomDialog(owner,table,row,"编辑【"+name+"】PBOM属性");

						}
					}
			}

	}

}