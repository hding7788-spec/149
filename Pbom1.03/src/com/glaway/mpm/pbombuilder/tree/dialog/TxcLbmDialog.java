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

import wt.part.WTPart;

import com.glaway.mpm.pbom.db.ErpResult;
import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.pbom.table.DefaultZTableFactory;
import com.glaway.mpm.pbom.table.ZTableOp;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.panel.ErpWzkPanel;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.erp.AbstractERPDialog;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.ErpUtil;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

/**
 * 填写材料编码
 * @author Administrator
 *
 */
public class TxcLbmDialog extends AbstractERPDialog {

	private static final long serialVersionUID = 1L;

	private ErpWzkPanel wzkPanel = new ErpWzkPanel(ErpUtil.ERP_OP_TYPE_CLBM,5) ;

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
	private CmTree tree;

	public TxcLbmDialog(Window owner,List<CmTreeNode> selPbomNodes,String title, CmTree tree) {
		super(owner);
		this.setTitle(title);
		this.selPbomNodes = selPbomNodes;
		this.tree = tree;
		loadInitDatas();
		initDimension();
		initComponents();

		initActions();
		initLayout();
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
					int prow = partTableOp.getZTable().getSelectedRow();
					if(prow==-1){
						JOptionPane.showMessageDialog(owner, "请选择part表，然后在设置值");
						return;
					}
						parttm = partTableOp.getTableModel();
						parttm.setValueAt(wztm.getValueAt(row, 1), prow, 2);
						parttm.setValueAt(wztm.getValueAt(row, 2), prow, 3);
						parttm.setValueAt(wztm.getValueAt(row, 3), prow, 5);
						parttm.setValueAt(wztm.getValueAt(row, 4), prow, 6);
						parttm.setValueAt(wztm.getValueAt(row, 5), prow, 7);
						parttm.setValueAt(wztm.getValueAt(row, 6), prow, 8);
						parttm.setValueAt(wztm.getValueAt(row, 7), prow, 9);
						parttm.setValueAt(wztm.getValueAt(row, 8), prow, 10);
						parttm.setValueAt(wztm.getValueAt(row, 9), prow, 11);
						parttm.setValueAt(wztm.getValueAt(row, 10), prow, 12);
						parttm.setValueAt(wztm.getValueAt(row, 11), prow, 13);
						parttm.setValueAt(wztm.getValueAt(row, 12), prow, 14);
						parttm.setValueAt(wztm.getValueAt(row, 13), prow, 15);
						parttm.setValueAt(wztm.getValueAt(row, 14), prow, 16);
						parttm.setValueAt(wztm.getValueAt(row, 15), prow, 17);
						parttm.setValueAt(wztm.getValueAt(row, 16), prow, 18);
						parttm.setValueAt(wztm.getValueAt(row, 17), prow, 19);
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

		saveButton.addActionListener(new SaveClbmListener(this,owner,partTableOp.getTableModel(),tree,selPbomNodes));

	}

	@Override
	protected void initComponents() {


		partTableOp =  new DefaultZTableFactory();
		partTableOp.setColumnsEditable(partTableEditCols);
		partTableOp.setTableInfors(partTableHeader, partTableBody,partTableColWidth);
		partTableOp.setTableStyle(partTableOp.getZTable());
		partTableOp.setColumnsHidden(partTableOp.getZTable(), new int[]{18});

		saveButton = new JButton("保 存");
		deleteButton = new JButton("删 除选择行");
		deleteAllButton = new JButton("删除所有行");

		centerPanel = new JPanel();


		bottomPanel = new JPanel();



	}
	protected void loadInitDatas() {
		partTableHeader = new String[]{"序号","图号","物资编码","物资名称","毛坯尺寸","型号/牌号/材料","规格","总规范/标准代号/技术条件","详细规范","国产/进口","供应商","计量单位","质量等级","封装形式","尺寸","专用条件","附加协议","电参数特选要求","物资类别","材料分类编码"};
		partTableBody = new String[selPbomNodes.size()][partTableHeader.length];
		CmTreeNode node = null;
		CmLightPart lightPart = null;
		WTPart part  = null;
		for(int i=0;i<selPbomNodes.size();i++){
			node = selPbomNodes.get(i);
		    lightPart = node.getPart();
		    part = CmBizObjUtil.getWTPartFromLightPart(lightPart);
			partTableBody[i][0]=(i+1)+"";
			Wzk initWzk = lightPart.getWzk();
			partTableBody[i][1]= part.getNumber();
			partTableBody[i][2]= initWzk.getClcode();
			partTableBody[i][3]= initWzk.getClName();
			partTableBody[i][4]= initWzk.getMpcc();
			partTableBody[i][5] = initWzk.getInvtype();
			partTableBody[i][6] = initWzk.getInvspec();
			partTableBody[i][7] = initWzk.getDef2();
			partTableBody[i][8] = initWzk.getDef3();
			partTableBody[i][9] = initWzk.getIsJinKou();
			partTableBody[i][10] = initWzk.getCustname();
			partTableBody[i][11] = initWzk.getMeasname();
			partTableBody[i][12] = initWzk.getDef4();
			partTableBody[i][13] = initWzk.getDef5();
			partTableBody[i][14] = initWzk.getDef6();
			partTableBody[i][15] = initWzk.getDef7();
			partTableBody[i][16] = initWzk.getDef8();
			partTableBody[i][17] = initWzk.getDef9();
			partTableBody[i][18] = initWzk.getWzlb();
			partTableBody[i][19] = initWzk.getInvclasscode();


		}
		int tw1 = 200;
		partTableColWidth = new int[]{tw1-100,tw1,tw1+30,tw1,tw1+50,tw1+20,tw1,tw1,tw1-20,tw1-20,tw1-20,tw1-20,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1,tw1};
		partTableEditCols =new int[]{4};

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

	final class SaveClbmListener implements ActionListener{
		private JDialog dialog;
		private Window owner;
		private TableModel tm;
		CmTree tree;
		List<CmTreeNode> updateNodes;
		public  SaveClbmListener(JDialog dialog,Window owner,TableModel tm,CmTree tree,List<CmTreeNode> updateNodes){
			this.dialog = dialog;
			this.owner = owner;
			this.tm = tm;
			this.tree = tree;
			this.updateNodes = updateNodes;
		}
		public void actionPerformed(ActionEvent e) {
			int rows = tm.getRowCount();
			String th = null;
			Wzk wzk = null;
			CmTreeNode node = null;
			List<Wzk> wzkList = new ArrayList<Wzk>();
			int flag = JOptionPane.showConfirmDialog(owner, "确定保存吗？","确认", JOptionPane.OK_CANCEL_OPTION);
			if(flag==JOptionPane.YES_OPTION){
				for(int i=0;i<rows;i++){
					th = (String)tm.getValueAt(i, 1);
					for(int j=0;j<updateNodes.size();j++){
					    node = updateNodes.get(j);
						CmTreeNode pnode = (CmTreeNode)node.getParent();
						WTPart part = CmBizObjUtil.getWTPartFromLightPart(node.getPart());
						WTPart ppart = CmBizObjUtil.getWTPartFromLightPart(pnode.getPart());
						if(part.getNumber().equals(th)){
							wzk = node.getPart().getWzk();
							wzk.setPart(part);
							wzk.setPpart(ppart);
							break;
						}
					}
					String isJinKou = "N";
					if("进口".equals((String)tm.getValueAt(i, 9))){
						isJinKou = "Y";
					}
//					node.getPart().setEdit(true);
					//缓存中
					wzk.setTh(th);
					wzk.setClcode((String)tm.getValueAt(i, 2));
					wzk.setClName((String)tm.getValueAt(i, 3));
					wzk.setInvcode("");
					wzk.setInvname("");
					wzk.setMpcc((String)tm.getValueAt(i, 4));
					wzk.setInvtype((String)tm.getValueAt(i, 5));
					wzk.setInvspec((String)tm.getValueAt(i, 6));
					wzk.setDef2((String)tm.getValueAt(i, 7));
					wzk.setDef3((String)tm.getValueAt(i, 8));
					wzk.setDef1(isJinKou);
					wzk.setCustname((String)tm.getValueAt(i, 10));
					wzk.setMeasname((String)tm.getValueAt(i, 11));
					wzk.setDef4((String)tm.getValueAt(i, 12));
					wzk.setDef5((String)tm.getValueAt(i, 13));
					wzk.setDef6((String)tm.getValueAt(i, 14));
					wzk.setDef7((String)tm.getValueAt(i, 15));
					wzk.setDef8((String)tm.getValueAt(i, 16));
					wzk.setDef9((String)tm.getValueAt(i, 17));
					wzk.setWzlb((String)tm.getValueAt(i, 18));
					wzk.setInvclasscode((String)tm.getValueAt(i, 19));
					wzk.setOpType(ErpUtil.ERP_OP_TYPE_CLBM);
					wzkList.add(wzk);
				}
				ErpResult result = PBOMEditorToWCIntf.saveErpClAttributeToWc(wzkList);
				JOptionPane.showMessageDialog(owner, result.getMsg());
				dialog.dispose();
			}
		}


	}

}