package com.glaway.mpm.pbombuilder.tree.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.pbom.table.DefaultZTableFactory;
import com.glaway.mpm.pbom.table.ZTableOp;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.panel.ErpWzkPanel;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.tree.action.PbomTreeEditReportAction;
import com.glaway.mpm.pbombuilder.tree.dialog.erp.AbstractERPDialog;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.ErpUtil;


/**
 * 填写物料编码
 * @author Administrator
 *
 */
public class TxwLbmDialog extends AbstractERPDialog {

	private static final long serialVersionUID = 1L;

	private ErpWzkPanel wzkPanel = new ErpWzkPanel(ErpUtil.ERP_OP_TYPE_WLBM,0) ;

	private ZTableOp partTableOp;
	private String[] partTableHeader = null;
	private String[][] partTableBody ;
	private int[] partTableColWidth ;
	private int[] partTableEditCols;
	private int[] partTableHideCols;
	private JButton saveButton;
	private JButton deleteButton;
	private JButton deleteAllButton;
	private JPanel centerPanel;
	private JPanel bottomPanel;
	private List<CmTreeNode> selPbomNodes;

	private Window owner;
	private CmTree tree;
	private CmTreeNode node;


	public TxwLbmDialog(CmTree tree,Window owner,List selPbomNodes,String title,CmTreeNode node) {
		super(owner);
		this.setTitle(title);
		this.tree = tree;
		this.selPbomNodes = selPbomNodes;
		this.owner = owner;
		this.node = node;

		loadInitDatas();

		initDimension();
		initComponents();

		initActions();
		initLayout();
		this.setModal(false);
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
					parttm.setValueAt(wztm.getValueAt(row, 3), prow, 4);
					parttm.setValueAt(wztm.getValueAt(row, 4), prow, 5);
					parttm.setValueAt(wztm.getValueAt(row, 5), prow, 6);
					parttm.setValueAt(wztm.getValueAt(row, 6), prow, 7);
					parttm.setValueAt(wztm.getValueAt(row, 7), prow, 8);
					parttm.setValueAt(wztm.getValueAt(row, 8), prow, 9);
					parttm.setValueAt(wztm.getValueAt(row, 9), prow, 10);
					parttm.setValueAt(wztm.getValueAt(row, 10), prow, 11);
					parttm.setValueAt(wztm.getValueAt(row, 11), prow, 12);
					parttm.setValueAt(wztm.getValueAt(row, 12), prow, 13);
					parttm.setValueAt(wztm.getValueAt(row, 13), prow, 14);
					parttm.setValueAt(wztm.getValueAt(row, 14), prow, 15);
					parttm.setValueAt(wztm.getValueAt(row, 15), prow, 16);
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

		saveButton.addActionListener(new SaveWlbmListener(this,owner,partTableOp.getTableModel(),tree,selPbomNodes));
	}

	@Override
	protected void initComponents() {

		//将零部件的材料信息设置到查询物资库的条件里
		this.wzkPanel.setInitValue(node);

		partTableOp =  new DefaultZTableFactory();
		partTableOp.setColumnsEditable(partTableEditCols);
		partTableOp.setTableInfors(partTableHeader, partTableBody,partTableColWidth);
		partTableOp.setTableStyle(partTableOp.getZTable());
		partTableOp.setColumnsHidden(partTableOp.getZTable(), partTableHideCols);

		saveButton = new JButton("保 存");
		deleteButton = new JButton("删除选择行");
		deleteAllButton = new JButton("删除所有行");

		centerPanel = new JPanel();
		bottomPanel = new JPanel();
	}

	protected void loadInitDatas() {
		partTableHeader = new String[]{"序号","图号","存货编码","存货名称","型号牌号","规格","技术条件","生产厂家","主计量单位","附加条件","螺纹规格/公称尺寸","机械性能等级","质量等级","封装形式","精度等级","物资类别","物资类别编码"};
		partTableBody = new String[selPbomNodes.size()][partTableHeader.length];
		CmTreeNode node = null;
		CmLightPart lightPart = null;
		WTPart part  = null;
		String invcode = null;
		for(int i=0;i<selPbomNodes.size();i++){
			node = selPbomNodes.get(i);
		    lightPart = node.getPart();
		    //long oid = node.getPart().getOid();
		    //String parentNumber = node.getPart().getParentPartNumber();
		    //invcode = lightPart.getWzk().getInvcode();
		    //invcode = PBOMEditorToWCIntf.getPartLinkIBAValueByPartOid(parentNumber,oid, "CHBM");
		    part = CmBizObjUtil.getWTPartFromLightPart(lightPart);
			partTableBody[i][0]=(i+1)+"";
//			Wzk initWzk = null;
//			if(invcode != null && !"".equals(invcode)){
//				initWzk = ErpToWCIntf.getWzkByInvcode(LoadConfig.getInstance().getQueryWzkDdtype(), invcode);
//			}
			partTableBody[i][0]=(i+1)+"";
			partTableBody[i][1]= part.getNumber();
//			if(initWzk == null){
				partTableBody[i][2] = "";
				partTableBody[i][3] = "";
				partTableBody[i][4] = "";
				partTableBody[i][5] = "";
				partTableBody[i][6] = "";
				partTableBody[i][7] = "";
				partTableBody[i][8] = "";
				partTableBody[i][9] = "";
				partTableBody[i][10] ="";
				partTableBody[i][11] = "";
				partTableBody[i][12] = "";
				partTableBody[i][13] = "";
				partTableBody[i][14] = "";
				partTableBody[i][15] = "";
				partTableBody[i][16] = "";
//			}else{
//				partTableBody[i][2] = initWzk.getInvcode();//存货编码
//				partTableBody[i][3] = initWzk.getInvname();//存货名称
//				partTableBody[i][4] = initWzk.getInvtype();//型号牌号
//				partTableBody[i][5] = initWzk.getInvspec();//规格
//				partTableBody[i][6] = initWzk.getJstjname();//技术条件
//				partTableBody[i][7] = initWzk.getCustname();//生产厂家
//				partTableBody[i][8] = initWzk.getMeasname();//主计量单位
//				partTableBody[i][9] = initWzk.getFjtjname();//附加条件
//				partTableBody[i][10] = initWzk.getDef12();//螺纹规格/公称尺寸
//				partTableBody[i][11] = initWzk.getDef14();//机械性能等级
//				partTableBody[i][12] = initWzk.getZldj();//质量等级
//				partTableBody[i][13] = initWzk.getDef6();//封装形式
//				partTableBody[i][14] = initWzk.getDef8();//精度等级
//				partTableBody[i][15] = initWzk.getWzlb();//物资类别
//				partTableBody[i][16] = initWzk.getInvclasscode();//物资类别编码
//			}
		}
		int tw1 = 200;
		partTableColWidth = new int[]{tw1-175,tw1,tw1+30,tw1,tw1+50,tw1+20,tw1,tw1,tw1-20,tw1-20,tw1-20,tw1-20,tw1,tw1,tw1,tw1,tw1};
		partTableEditCols =new int[]{};
		partTableHideCols = new int[]{};

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
	protected void registerTaskExecutor() throws CmTaskException {}

	@Override
	protected void unregisterTaskExecutor() throws CmTaskException {}

	final class SaveWlbmListener implements ActionListener{
		private JDialog dialog;
		private Window owner;
		private TableModel tm;
		CmTree tree;
		List<CmTreeNode> updateNodes;

		public  SaveWlbmListener(JDialog dialog,Window owner,TableModel tm,CmTree tree,List<CmTreeNode> updateNodes){
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
			int flag = JOptionPane.showConfirmDialog(owner, "确定保存吗？","确认", JOptionPane.OK_CANCEL_OPTION);
			if(flag==JOptionPane.YES_OPTION){
				for(int i=0;i<rows;i++){
					long partOid = 0;
					String parentNumber = "";
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
							partOid = node.getPart().getOid();
							parentNumber = node.getPart().getParentPartNumber();
							break;
						}
					}
					node.getPart().setEdit(true);
					//缓存中
					wzk.setTh(th);
					wzk.setClcode("");
					wzk.setClName("");
					wzk.setInvcode((String)tm.getValueAt(i, 2));//存货编码
					wzk.setInvname("");
					wzk.setInvtype("");//型号牌号
					wzk.setInvspec("");//规格
					wzk.setDef1("");//技术条件
					wzk.setMpcc("");
					wzk.setDef2("");
					wzk.setDef3("");
					wzk.setCustname("");
					wzk.setMeasname("");
					wzk.setDef4("");
					wzk.setDef5("");
					wzk.setDef6("");
					wzk.setDef7("");
					wzk.setDef8("");
					wzk.setDef9("");
					wzk.setWzlb((String)tm.getValueAt(i, 15));
					wzk.setInvclasscode((String)tm.getValueAt(i, 16));
					wzk.setOpType(ErpUtil.ERP_OP_TYPE_WLBM);

					Map<String, String> ibaMap = new HashMap<String, String>();
					//ibaMap.put("CHBM", (String)tm.getValueAt(i, 2));//存货编码
					ibaMap.put("XHPH", CmCommonStringUtil.emptyToString((String)tm.getValueAt(i, 4)));//型号牌号
					ibaMap.put("CSIZE", CmCommonStringUtil.emptyToString((String)tm.getValueAt(i, 5)));//规格
					ibaMap.put("JSTJ", CmCommonStringUtil.emptyToString((String)tm.getValueAt(i, 6)));//技术条件

					node.getPart().setXhph(CmCommonStringUtil.emptyToString((String)tm.getValueAt(i, 4)));
					node.getPart().setCsize(CmCommonStringUtil.emptyToString((String)tm.getValueAt(i, 5)));
					node.getPart().setJstj(CmCommonStringUtil.emptyToString((String)tm.getValueAt(i, 6)));
					node.getPart().setChbm(CmCommonStringUtil.emptyToString((String)tm.getValueAt(i, 2)));

					for (CmTreeNode cmnode : CmCommonStringUtil.getBomNodeList(tree)) {
						if (CmCommonStringUtil.isCommon(node, cmnode)) {
							cmnode.getPart().setXhph(CmCommonStringUtil.emptyToString((String)tm.getValueAt(i, 4)));
							cmnode.getPart().setCsize(CmCommonStringUtil.emptyToString((String)tm.getValueAt(i, 5)));
							cmnode.getPart().setJstj(CmCommonStringUtil.emptyToString((String)tm.getValueAt(i, 6)));
							cmnode.getPart().setChbm(CmCommonStringUtil.emptyToString((String)tm.getValueAt(i, 2)));
							cmnode.getPart().setWzk(wzk);
						}
					}

					if(partOid != 0 && ibaMap.get("CHBM") != null && !"".equals(ibaMap.get("CHBM"))) {
						//PBOMEditorToWCIntf.savePartIBAValue(partOid, ibaMap);
					}

					Map<String, String> ibaMap2 = new HashMap<String, String>();
					ibaMap2.put("CHBM", (String)tm.getValueAt(i, 2));//存货编码
					if(parentNumber != null && !"".equals(parentNumber)
							&& partOid != 0 && ibaMap2.get("CHBM") != null && !"".equals(ibaMap2.get("CHBM"))) {
						//PBOMEditorToWCIntf.saveCHBMIBAValueForPartLink(parentNumber, partOid, ibaMap2);
					}

				}
				tree.updateUI();
				dialog.dispose();
			}
		}
	}

	/**
	 * 同步更新零件的其他兄弟节点
	 * @date  2013-1-29
	 * @param node
	 *
	 */
	public void updateBrotherNodesOfPbomTree(CmTreeNode root,CmTreeNode node, boolean isUpdateUI) {
		for (CmTreeNode cmnode : CmCommonStringUtil.getBomNodeList(tree)) {
			System.out.println("-------------"+cmnode.getPart().getPartNumber());
			if (CmCommonStringUtil.isCommon(node, cmnode)) {
				System.out.println("-------------ok"+node.getPart().getChbm());
				cmnode.getPart().setXhph(node.getPart().getXhph());
				cmnode.getPart().setCsize(node.getPart().getCsize());
				cmnode.getPart().setJstj(node.getPart().getJstj());
				cmnode.getPart().setChbm(node.getPart().getChbm());
				CmCommonStringUtil.checkPbomTreeNodeIsEdit(cmnode);
			}
		}
		PbomTreeEditReportAction.updatePbomTreeEditReport();
		BomTreeReportAction.updateBomReport();
		if (isUpdateUI) {
			tree.updateUI();
		}
	}


}