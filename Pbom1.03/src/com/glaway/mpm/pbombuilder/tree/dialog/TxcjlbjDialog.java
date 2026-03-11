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
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.tree.action.PbomTreeEditReportAction;
import com.glaway.mpm.pbombuilder.tree.dialog.erp.AbstractERPDialog;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.ErpUtil;
import com.glaway.mpm.pbombuilder.util.ExtCommonDellFunction;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

/**
 * 填写创建零部件
 * @author Administrator
 *
 */
public class TxcjlbjDialog extends AbstractERPDialog {

	private static final long serialVersionUID = 1L;

	private ErpWzkPanel wzkPanel = new ErpWzkPanel(ErpUtil.ERP_OP_TYPE_WLBM,1) ;

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
	private Window owner;
	private CmTreeNode node;
	private CmTree tree;
	private WTPart parentPart;

	public TxcjlbjDialog(CmTree tree,Window owner,CmTreeNode node,String title) {
		super(owner);
		this.setTitle(title);
		this.tree = tree;

		this.node = node;
		CmLightPart lightPart = node.getPart();
		this.parentPart = CmBizObjUtil.getWTPartFromLightPart(lightPart);

		loadInitDatas();
		initDimension();
		initComponents();

		initActions();
		initLayout();
		this.setResizable(true);
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
					rows[1] = parentPart.getNumber();
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

		saveButton.addActionListener(new SaveCjlbjListener(this,owner,partTableOp.getTableModel(),node,tree));
	}

	@Override
	protected void initComponents() {

		//将零部件的材料信息设置到查询物资库的条件里
		this.wzkPanel.setInitValue(node);

		partTableOp =  new DefaultZTableFactory();
		partTableOp.setColumnsEditable(partTableEditCols);
		partTableOp.setTableInfors(partTableHeader, partTableBody,partTableColWidth);
		partTableOp.setTableStyle(partTableOp.getZTable());
		partTableOp.setColumnsHidden(partTableOp.getZTable(), new int[]{});

		saveButton = new JButton("保 存");
		deleteButton = new JButton("删除选择行");
		deleteAllButton = new JButton("删除所有行");

		centerPanel = new JPanel();
		bottomPanel = new JPanel();
	}

	protected void loadInitDatas() {
		partTableHeader = new String[]{"序号","上级图号","图号","存货编码","存货名称","使用数量","型号牌号","规格","技术条件","生产厂家","主计量单位","附加条件","螺纹规格/公称尺寸","机械性能等级","质量等级","封装形式","精度等级","物资类别","物资类别编码"};
		partTableBody = new String[0][partTableHeader.length];
		int tw1 = 200;
		partTableColWidth = new int[]{tw1-150,tw1,tw1+30,tw1,tw1+50,tw1-175,tw1+20,tw1,tw1,tw1-20,tw1-20,tw1-20,tw1-20,tw1,tw1,tw1,tw1,tw1,tw1};
		partTableEditCols =new int[]{5};
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

	class SaveCjlbjListener implements ActionListener {
		private JDialog dialog;
		private Window owner;
		private TableModel tm;
		private WTPart part;
		private CmTreeNode node;
		private CmTree tree;
		public SaveCjlbjListener(JDialog dialog,Window owner,TableModel tm,CmTreeNode node,CmTree tree){
			this.dialog = dialog;
			this.owner = owner;
			this.tm = tm;
			CmLightPart lightPart = node.getPart();
			this.part = CmBizObjUtil.getWTPartFromLightPart(lightPart);
			this.tree = tree;
			this.node = node;
		}

		public void actionPerformed(ActionEvent e) {
			List<Wzk> dataList = new ArrayList<Wzk>();

			int rows = tm.getRowCount();
			Wzk wzk = null;
			for(int i=0;i<rows;i++){
				wzk = new Wzk();
				wzk.setSjth((String)tm.getValueAt(i, 1));
				if(tm.getValueAt(i, 2)==null){
					continue;
				}
				wzk.setPpart(part);
				wzk.setTh((String)tm.getValueAt(i, 2));
				wzk.setClcode("");
				wzk.setClName("");
				wzk.setInvcode((String)tm.getValueAt(i, 3));//存货编码
				wzk.setInvname((String)tm.getValueAt(i, 4));//存货名称
				wzk.setZxsl((String)tm.getValueAt(i, 5));//使用数量
				wzk.setInvtype(CmCommonStringUtil.emptyToString((String)tm.getValueAt(i, 6)));//型号牌号
				wzk.setInvspec(CmCommonStringUtil.emptyToString((String)tm.getValueAt(i, 7)));//规格
				wzk.setDef1(CmCommonStringUtil.emptyToString((String)tm.getValueAt(i, 8)));//技术条件
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
				wzk.setWzlb((String)tm.getValueAt(i, 17));
				wzk.setInvclasscode((String)tm.getValueAt(i, 18));
				wzk.setOpType(ErpUtil.ERP_OP_TYPE_WLBM);

				dataList.add(wzk);
			}

			String msg = checkData(dataList);
			if(!"".equals(msg)) {
				//JOptionPane.showMessageDialog(owner, "创建失败，编码为："+msg+" 系统中已经存在！");
				int flag = JOptionPane.showConfirmDialog(owner, "编码 [ "+msg+" ] 系统中已经存在，确认是否要添加？","确认", JOptionPane.OK_CANCEL_OPTION);
				if(flag != 0){
					return;
				}
			}

			int flag = JOptionPane.showConfirmDialog(owner, "确定保存所选标准件/元器件吗？","确认", JOptionPane.OK_CANCEL_OPTION);

			if(flag==0){
				List<Wzk> partList = null;
				ErpResult result = PBOMEditorToWCIntf.saveErpCjPart2Wc(dataList);
				if(!"".equals(result.getMsg())){
					JOptionPane.showMessageDialog(owner, result.getMsg());
					return;
				}
				partList = result.getDataList();
				if(partList!=null)
				for(Wzk partwzk:partList){
				    CmLightPart newLightPart = CmBizObjUtil.buildCmLightPartFromWTPart(partwzk.getPart());
				    newLightPart.setWzk(partwzk);
				    newLightPart.setOperType("new");
				    //newLightPart.setEdit(true);
				    newLightPart.setPartType("NS");//表示新建的标准件
				    newLightPart.setLifecycle("已批准");

//				    CmMPartMaster master = new CmMPartMaster(newLightPart);
				    String item = partwzk.getPart().getNumber() + "(" + partwzk.getPart().getName()+ ") " ;
					CmTreeNode cmTreeNode = new CmTreeNode(item);
					cmTreeNode.setPart(newLightPart);
//					cmTreeNode.setUserObject(master);
					String zxsl = partwzk.getZxsl();

					int sl = 1;
					try {
						sl = Integer.parseInt(zxsl);
					} catch (NumberFormatException e1) {
						e1.printStackTrace();
					}
					newLightPart.setUseCount(sl);
//					CmCommonStringUtil.addCommonMiddleNodeWithCommonParent(tree.getRoot(),cmTreeNode,sl,node);
//					EbomTreeCancelAction.addPbomTreeChange(cmTreeNode,null, "create",null);
//					CmCommonStringUtil.addNodeToCheckList(cmTreeNode);
//					CmCommonPackageAction common = new CmCommonPackageAction();
//					common.packageOneNode(this.tree.getRoot(), cmTreeNode);
//
//					PbomTreeEditReportAction.updatePbomTreeEditReport();
//					BomTreeReportAction.updateBomReport();

					try {
//						ExtCommonDellFunction.addExistPartNodeToCommonNode(tree.getRoot(), cmTreeNode, node, sl, tree);
						List<CmTreeNode> retList = ExtCommonDellFunction.addNewPartToCommonNode(tree.getRoot(), cmTreeNode, node, sl, false);
						CmCommonStringUtil.setIsNewTop(retList);
						ExtCommonDellFunction.changeCommonNodeUseCount(tree.getRoot(), cmTreeNode, node, sl, false, tree);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					ExtCommonDellFunction.packageAllNodeContanChildernNode(node, tree);
//					CmCommonStringUtil.addNodeToCheckList(node);
					PbomTreeEditReportAction.updatePbomTreeEditReport();
					BomTreeReportAction.updateBomReport();
				}
				CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
				tree.updateUI();
				dialog.dispose();
			}
		}

		private String checkData(List<Wzk> dataList) {
			StringBuffer sb = new StringBuffer();
			for (Wzk wzk : dataList) {
				String number = wzk.getInvcode();
				long id = PBOMEditorToWCIntf.queryLatestPartIdByNumberRMI(number);
				if(id != -1) {
					sb.append(number).append(",");
				}
			}

			return sb.toString();
		}
	}

}