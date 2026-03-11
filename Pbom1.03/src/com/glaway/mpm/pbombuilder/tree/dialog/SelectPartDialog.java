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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import wt.fc.PersistenceHelper;
import wt.part.WTPart;
import wt.util.WTException;

import com.glaway.mpm.pbom.table.DefaultZTableFactory;
import com.glaway.mpm.pbom.table.ZTableOp;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.tree.action.PbomTreeEditReportAction;
import com.glaway.mpm.pbombuilder.tree.dialog.erp.AbstractERPDialog;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmSearchHelper;
import com.glaway.mpm.pbombuilder.util.ExtCommonDellFunction;
import com.glaway.mpm.pbombuilder.util.InputLimited;
import com.glaway.mpm.pbombuilder.util.LoadConfig;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;
import com.glaway.mpm.util.IBAHelper;

public class SelectPartDialog extends AbstractERPDialog {

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

	private JLabel cntLabel;
	private final JTextField cntText = new JTextField();

	private JButton queryButton;
	private JButton addButton;

	private ZTableOp partTableOp;
	private String[] partTableHeader = null;

	private String[][] partTableBody;

	private int[] partTableColWidth;
	private int[] partTableEditCols;
	private InputLimited limit;

	public static void main(String[] args) {
		new SelectPartDialog(null, null, null, null);
	}

	public SelectPartDialog(CmTree tree, Window owner, CmTreeNode node,
			String title) {
		super(owner);
		this.setTitle(title);
		this.tree = tree;
		this.node = node;

		loadInitDatas();
		initDimension();
		initComponents();

		initActions();
		initLayout();
		this.setResizable(true);
		this.setModal(true);
		//this.setSize(600, 300);
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
				String view = LoadConfig.getInstance().getPbomView();
				if (CmCommonStringUtil.isEmpty(name) && CmCommonStringUtil.isEmpty(number)) {
					JOptionPane.showMessageDialog(owner, "部件编号和名称至少一个不为空");
					return;
				}
				if (number != null)
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
						partTableBody[i][10] = PersistenceHelper.getObjectIdentifier(part).getId() + "";
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
				partTableOp.setColumnsHidden(partTableOp.getZTable(), new int[] { 4, 10 });
				partTableOp.getZTable().updateUI();
			}
		});

		addButton.addActionListener(new AddlbjListener(this, owner, partTableOp.getZTable(), node, tree));

		partTableOp.getZTable().addMouseListener(new AddlbjListener(this, owner, partTableOp.getZTable(), node, tree));
	}

	class AddPartLabel extends JLabel {
		public AddPartLabel(String text) {
			setText(text);
			// this.setFont(new Font("宋体", Font.PLAIN, 20));
		}
	}

	public String getCnt() {
		return cntText.getText();
	}

	@Override
	protected void initComponents() {
		numberLabel = new AddPartLabel("    编号:");
		numberText = new JTextField();

		nameLabel = new AddPartLabel("    名称:");
		nameText = new JTextField();

		viewLable = new AddPartLabel("视图:");
		viewComboBox = new JComboBox(LoadConfig.getInstance().getPartOFView());

		cntLabel = new AddPartLabel("    数量:");

		limit = new InputLimited(5, true);
		cntText.setDocument(limit);
		cntText.setText("1");

		queryButton = new JButton("查 询");
		queryButton.setFont(new Font("宋体", Font.PLAIN, 20));
		addButton = new JButton("添 加");
		addButton.setFont(new Font("宋体", Font.PLAIN, 20));

		partTableOp = new DefaultZTableFactory();
		partTableOp.setColumnsEditable(partTableEditCols);

		partTableOp.setTableInfors(partTableHeader, partTableBody, partTableColWidth);
		partTableOp.setTableStyle(partTableOp.getZTable());
		partTableOp.setColumnsHidden(partTableOp.getZTable(), new int[] { 4, 10 });
		partTableOp.getZTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		cntText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!CmCommonStringUtil.isEmpty(cntText.getText()) && cntText.getText().startsWith("0")) {
					cntText.setText("1");
				} else if (!CmCommonStringUtil.isEmpty(cntText.getText())) {
					cntText.setText(cntText.getText());
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {

			}
		});
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
		numberText.setPreferredSize(new Dimension(180, 15));
		contentPanel.add(numberText, c);
		c.gridx = 2;
		contentPanel.add(nameLabel, c);
		c.gridx = 3;
		nameText.setPreferredSize(new Dimension(180, 15));
		contentPanel.add(nameText, c);
		// c.gridx = 4;
		//
		// contentPanel.add(viewLable, c);
		// c.gridx = 5;
		// contentPanel.add(viewComboBox, c);
		c.gridx = 6;

		contentPanel.add(cntLabel, c);
		c.gridx = 7;
		contentPanel.add(cntText, c);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(queryButton);
		buttonPanel.add(addButton);
		c.gridx = 9;
		c.insets = new java.awt.Insets(0, 50, 0, 0);
		contentPanel.add(buttonPanel, c);

		JScrollPane jsp = new JScrollPane(partTableOp.getZTable());
		jsp.getViewport().setBackground(Color.WHITE);

		JPanel mainJPanel = new JPanel();
		mainJPanel.setLayout(new BorderLayout());

		// JPanel bottomPanel = new JPanel();
		// bottomPanel.add(addButton);

		mainJPanel.add(contentPanel, BorderLayout.NORTH);

		mainJPanel.add(jsp, BorderLayout.CENTER);

		// mainJPanel.add(bottomPanel, BorderLayout.SOUTH);

		this.setContentPane(mainJPanel);
	}

	@Override
	protected void loadInitDatas() {
		partTableHeader = new String[] { "序号", "编码", "名称", "视图", "数量", "零部件类型",
				"成套件标识", "关重件标识", "密级", "当前阶段", "oid" };
		partTableBody = new String[0][partTableHeader.length];
		partTableColWidth = new int[] { 50, 200, 200, 100, 50, 100, 100, 100, 50, 100, 50 };
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

class AddlbjListener extends MouseAdapter implements ActionListener {
	private JDialog dialog;
	private Window owner;
	private JTable table;
	WTPart part;
	CmTreeNode node;
	CmTree tree;

	public AddlbjListener(JDialog dialog, Window owner, JTable table,
			CmTreeNode node, CmTree tree) {
		this.dialog = dialog;
		this.owner = owner;
		this.table = table;
		CmLightPart lightPart = node.getPart();
		this.part = CmBizObjUtil.getWTPartFromLightPart(lightPart);
		this.tree = tree;
		this.node = node;
	}

	public void actionPerformed(ActionEvent e) {
		addSelectPart();
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			addSelectPart();
		}
	}

	public void addSelectPart() {
		String usecount = ((SelectPartDialog) dialog).getCnt();

		if (CmCommonStringUtil.isEmpty(usecount)) {
			JOptionPane.showMessageDialog(dialog, "数量不能为空！");
			return;
		}

		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(dialog, "请在表格中选中需要添加的零部件！");
			return;
		}
		TableModel tm = table.getModel();
		String number = null;
		String name = null;

		String oid = null;
		int sl = 1;
		oid = (String) tm.getValueAt(row, 10);
		number = (String) tm.getValueAt(row, 1);
		name = (String) tm.getValueAt(row, 2);
		WTPart wtpart = null;
		try {
			wtpart = (WTPart) CmSearchHelper.search(WTPart.class, Long.valueOf(oid));
		} catch (NumberFormatException e2) {
			e2.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		try {
			sl = Integer.parseInt(usecount);
		} catch (Exception e2) {
			sl = 0;
			JOptionPane.showMessageDialog(owner, "请输入一个正确的数值！");
			return;
		}

		if (node.getPart().getPartNumber().equals(number)) {
			JOptionPane.showMessageDialog(owner, "添加失败，所选部件与节点为同一个部件！");
			return;
		}

		List<String> upNodeList = ExtCommonDellFunction.getUp_Nodes(node, tree.getRoot());

		if (upNodeList.contains(number)) {
			JOptionPane.showMessageDialog(owner, "添加失败，所选部件是添加到部件的上级！");
			return;
		}
		Enumeration<CmTreeNode> cen = node.children();
		while (cen.hasMoreElements()) {
			if (cen.nextElement().getPart().getPartNumber().equals(number)) {
				JOptionPane.showMessageDialog(owner, "添加失败，子节点中已经存在此节点！");
				return;
			}
		}

		wtpart = PBOMEditorToWCIntf.getLatestPart(number, "Manufacturing");
		if(wtpart == null) {
			wtpart = PBOMEditorToWCIntf.createPlanningPartRMI(wtpart, number, name);
		}

		CmLightPart newLightPart = CmBizObjUtil.buildCmLightPartFromWTPart(wtpart);
		String version = wtpart.getVersionIdentifier().getValue()+"."+wtpart.getIterationIdentifier().getValue();
		String item = newLightPart.getPartNumber() + "(" + newLightPart.getPartName() + ") "+version;
		newLightPart.setUseCount(sl);
		newLightPart.setOperType("new");
		CmTreeNode cmTreeNode = new CmTreeNode(item);
		cmTreeNode.setPart(newLightPart);
		try {
			ExtCommonDellFunction.addExistPartNodeToCommonNode(tree.getRoot(), cmTreeNode, node, sl, tree);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		ExtCommonDellFunction.packageAllNodeContanChildernNode(node, tree);
		CmCommonStringUtil.addNodeToCheckList(node);
		PbomTreeEditReportAction.updatePbomTreeEditReport();
		BomTreeReportAction.updateBomReport();

		CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
		ExtCommonDellFunction.updateTreeUI(tree, true);
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
