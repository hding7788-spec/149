package com.glaway.mpm.pbombuilder.tree.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import wt.part.WTPart;
import wt.util.WTException;

import com.glaway.mpm.pbom.table.DefaultZTableFactory;
import com.glaway.mpm.pbom.table.ZTableOp;
import com.glaway.mpm.pbombuilder.action.CmCommonPackageAction;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.panel.CmMPartMaster;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.tree.action.EbomTreeCancelAction;
import com.glaway.mpm.pbombuilder.tree.action.PbomTreeEditReportAction;
import com.glaway.mpm.pbombuilder.tree.dialog.erp.AbstractERPDialog;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;
import com.glaway.mpm.util.IBAHelper;

public class SelectPartDialog2 extends AbstractERPDialog {

	private static final long serialVersionUID = 1L;

	private final String[] YES_OR_NO = new String[] { "是", "否" };
	private Window owner;
	private CmTree tree;
	private CmTreeNode node;
	private String title;
	private WTPart parentPart;

	private JLabel numberLabel;
	private JTextField numberText;

	private JLabel nameLabel;
	private JTextField nameText;

	private JLabel viewLable;
	private JComboBox viewComboBox;
	private String[] viewArr = {"Manufacturing","Design"};

	private JButton queryButton;
	private JButton addButton;

	private ZTableOp partTableOp;
	private String[] partTableHeader = null;

	private String[][] partTableBody;

	private int[] partTableColWidth;
	private int[] partTableEditCols;

	public static void main(String[] args) {
		new SelectPartDialog(null, null, null, null);
	}

	public SelectPartDialog2(CmTree tree, Window owner, CmTreeNode node, String title) {
		super(owner);
		this.setTitle(title);
		this.tree = tree;
		this.node = node;
		// CmLightPart lightPart = node.getPart();
		// this.parentPart = CmBizObjUtil.getWTPartFromLightPart(lightPart);

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

	@Override
	protected void initActions() {
		queryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = nameText.getText();
				String number = numberText.getText();
				String view = viewComboBox.getSelectedItem().toString();
				if (CmCommonStringUtil.isEmpty(name) && CmCommonStringUtil.isEmpty(number)) {
					JOptionPane.showMessageDialog(owner, "部件编号和名称至少一个不为空");
					return;
				}
				if(number!=null)
					number = number.toUpperCase();
				List<WTPart> dataPart = PBOMEditorToWCIntf.queryPartByLikeNumberNameView(view, number, name);

				if (dataPart == null)
					return;
				Object[][] partTableBody = new Object[dataPart.size()][partTableHeader.length];
				WTPart part = null;
				for (int i = 0; i < dataPart.size(); i++) {
					part = dataPart.get(i);
					try {
						IBAHelper helper = new IBAHelper(part);
						partTableBody[i][0] = (i + 1) + "";
						partTableBody[i][1] = part.getNumber();
						partTableBody[i][2] = part.getName();
						partTableBody[i][3] = part.getViewName();
						partTableBody[i][4] = "1";
						partTableBody[i][5] = helper.getIBAValue("MTYPE");
						partTableBody[i][6] = helper.getIBAValue("SETMARK");
						partTableBody[i][7] = helper.getIBAValue("KEYCOMPONENT");
						partTableBody[i][8] = helper.getIBAValue("SECRET");
						partTableBody[i][9] = helper.getIBAValue("PHASE_CODE");
					} catch (WTException e1) {
						e1.printStackTrace();
					}
				}

				DefaultTableModel dtm = (DefaultTableModel) partTableOp.getTableModel();
				dtm.setDataVector(partTableBody, partTableHeader);
				for (int i = 0; i < partTableHeader.length; i++) {
					partTableOp.getZTable().getColumn(partTableHeader[i]).setPreferredWidth(partTableColWidth[i]);
				}
				partTableOp.setTableStyle(partTableOp.getZTable());
				partTableOp.setColumnsEditable(partTableEditCols);
				partTableOp.getZTable().updateUI();
			}
		});

		addButton.addActionListener(new AddlbjListener2(this,owner,partTableOp.getZTable(),node,tree));
	}

	class AddPartLabel extends JLabel {
		public AddPartLabel(String text) {
			setText(text);
			this.setFont(new Font("宋体", Font.PLAIN, 20));
		}
	}

	@Override
	protected void initComponents() {
		numberLabel = new AddPartLabel("  编号：");
		numberText = new JTextField();

		nameLabel = new AddPartLabel("  名称：");
		nameText = new JTextField();

		viewLable = new AddPartLabel("  视图：");
		//viewComboBox = new JComboBox(LoadConfig.getInstance().getPartOFView());
		viewComboBox = new JComboBox(viewArr);
		queryButton = new JButton("查 询");
		queryButton.setFont(new Font("宋体", Font.PLAIN, 20));
		addButton = new JButton("添 加");
		addButton.setFont(new Font("宋体", Font.PLAIN, 20));

		partTableOp = new DefaultZTableFactory();
		partTableOp.setColumnsEditable(partTableEditCols);
		partTableOp.setTableInfors(partTableHeader, partTableBody,partTableColWidth);
		partTableOp.setTableStyle(partTableOp.getZTable());
	}

	@Override
	protected void initLayout() {
		JPanel contentPanel = new JPanel();
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.ipadx = 10;
		contentPanel.setLayout(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;

		contentPanel.add(numberLabel, c);
		c.gridx = 1;
		numberText.setPreferredSize(new Dimension(180, 25));
		contentPanel.add(numberText, c);
		c.gridx = 2;
		contentPanel.add(nameLabel, c);
		c.gridx = 3;
		nameText.setPreferredSize(new Dimension(180, 25));
		contentPanel.add(nameText, c);
		c.gridx = 4;

		contentPanel.add(viewLable, c);
		c.gridx = 5;
		contentPanel.add(viewComboBox, c);
		c.gridx = 7;
		c.insets = new java.awt.Insets(0, 100, 0, 0);
		contentPanel.add(queryButton, c);

		JScrollPane jsp = new JScrollPane(partTableOp.getZTable());
		jsp.getViewport().setBackground(Color.WHITE);

		JPanel mainJPanel = new JPanel();
		mainJPanel.setLayout(new BorderLayout());

		JPanel bottomPanel = new JPanel();
		bottomPanel.add(addButton);

		mainJPanel.add(contentPanel, BorderLayout.NORTH);

		mainJPanel.add(jsp, BorderLayout.CENTER);

		mainJPanel.add(bottomPanel, BorderLayout.SOUTH);

		this.setContentPane(mainJPanel);
	}

	@Override
	protected void loadInitDatas() {
		partTableHeader = new String[] { "序号", "编码", "名称", "视图", "数量", "零部件类型",
				"成套件标识", "关重件标识", "密级", "当前阶段" };
		partTableBody = new String[0][partTableHeader.length];
		partTableColWidth = new int[] { 50, 200, 200, 100, 50, 100, 100, 100, 50, 100 };
		partTableEditCols = new int[] { 4 };
	}

	@Override
	protected void registerTaskExecutor() throws CmTaskException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void unregisterTaskExecutor() throws CmTaskException {
		// TODO Auto-generated method stub

	}
}

class AddlbjListener2 implements ActionListener {
	private JDialog dialog;
	private Window owner;
	private JTable table;
	WTPart part;
	CmTreeNode node;
	CmTree tree;
	public AddlbjListener2(JDialog dialog,Window owner,JTable table,CmTreeNode node,CmTree tree){
		this.dialog = dialog;
		this.owner = owner;
		this.table = table;
		CmLightPart lightPart = node.getPart();
		this.part = CmBizObjUtil.getWTPartFromLightPart(lightPart);
		this.tree = tree;
		this.node = node;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		int[] rows = table.getSelectedRows();
		TableModel tm = table.getModel();
		String number = null;
		String name = null;
		String usercount = null;
		for(int i=0;i<rows.length;i++){
			number = (String)tm.getValueAt(rows[i], 1);
			name = (String)tm.getValueAt(rows[i], 2);
			usercount = (String)tm.getValueAt(rows[i], 4);

			//检查该编号的零部件是否在所选零部件节点已经存在，如果存在了就不添加
			if(checkIsExsited(number)) {
				JOptionPane.showMessageDialog(dialog, number+",该编号的零部件已经存在你所选节点的下级节点中，不能添加！");
				continue;
			}

			WTPart wtpart = PBOMEditorToWCIntf.createPlanningPartRMI(part, number, name);

		    CmLightPart newLightPart = CmBizObjUtil.buildCmLightPartFromWTPart(wtpart);
		    newLightPart.setOperType("new");
		    newLightPart.setEdit(true);

		    CmMPartMaster master = new CmMPartMaster(newLightPart);
		    String item = wtpart.getNumber() + "(" + wtpart.getName()+ ") " ;
			CmTreeNode cmTreeNode = new CmTreeNode(item);
			cmTreeNode.setPart(newLightPart);
			cmTreeNode.setUserObject(master);
			String zxsl = usercount;

			int sl = 1;
			try {
				sl = Integer.parseInt(zxsl);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			}
			newLightPart.setUseCount(sl);
			CmCommonStringUtil.addCommonMiddleNodeWithCommonParent(tree.getRoot(),cmTreeNode,sl,node);
			EbomTreeCancelAction.addPbomTreeChange(cmTreeNode,null, "create",null);
			CmCommonStringUtil.addNodeToCheckList(cmTreeNode);
			CmCommonPackageAction common = new CmCommonPackageAction();
			common.packageOneNode(this.tree.getRoot(), cmTreeNode);

			PbomTreeEditReportAction.updatePbomTreeEditReport();
			BomTreeReportAction.updateBomReport();
		}
		CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
		tree.updateUI();
		dialog.dispose();
	}

	private boolean checkIsExsited(String number) {
		boolean b = false;
		Enumeration children = node.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(number.equals(child.getPart().getPartNumber())) {
				b = true;
				break;
			}
		}
		return b;
	}
}
