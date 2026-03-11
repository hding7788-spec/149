package com.glaway.mpm.erp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.glaway.mpm.util.*;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.glaway.mpm.model.UploadTechnics;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTechnicsTreeObject;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.view.XWTreeObject;


/**
 * 原材料定额
 * @author Administrator
 *
 */
public class TxwLbmDialog extends AbstractERPDialog {

	private static final long serialVersionUID = 1L;

	private ZTableOp partTableOp;
	private String[] partTableHeader = null;
	private String[][] partTableBody ;
	private int[] partTableColWidth ;
	private int[] partTableEditCols;
	private int[] partTableHideCols;
	private JButton saveButton;
	private JButton deleteButton;
	private JButton deleteAllButton;
	private JButton moveUpButton;
	private JButton moveDownButton;
	private JPanel centerPanel;
	private JPanel bottomPanel;
	private Window owner;
	private XWTreeNode node;
	private NewTechnicsPart frame;
	private ErpWzkPanelEx wzkPanel;

	public TxwLbmDialog(NewTechnicsPart frame,String title,XWTreeNode node) {
		super(frame);
		this.setTitle(title);
		this.frame = frame;
		this.node = node;
		wzkPanel = new ErpWzkPanelEx(22,node);
		loadInitDatas();
		initDimension();
		initComponents();

		loadOldDatas();

		initActions();
		initLayout();
		this.setModal(false);
		this.setVisible(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());
	}

	protected void initActions(){
		wzkPanel.getTableOp().getZTable().addMouseListener(new MouseAdapter() {
			int row = -1;
			TableModel wztm,parttm;
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
						if(wzbm.equals((String)parttm.getValueAt(i, 1))){
							isExist = true;
							break;
						}
					}
					if(isExist){
						JOptionPane.showMessageDialog(frame, "【"+wzbm+"】物资编码已经使用");
						return ;
					}
					Object [] rows = new Object[partTableHeader.length];
					rows[0] = (parttm.getRowCount()+1)+"";
					rows[1] = wztm.getValueAt(row, 1);
					rows[2] = wztm.getValueAt(row, 2);
					rows[3] = "";
					rows[4] = 1;
					rows[6] = wztm.getValueAt(row, 3);
					rows[7] = wztm.getValueAt(row, 4);
					rows[8] = wztm.getValueAt(row, 5);
					rows[9] = wztm.getValueAt(row, 6);
					rows[10] = wztm.getValueAt(row, 7);
					rows[11] = wztm.getValueAt(row, 8);
					rows[12] = wztm.getValueAt(row, 9);

					rows[13] = wztm.getValueAt(row, 12);
					rows[14] = wztm.getValueAt(row, 13);
					rows[15] = wztm.getValueAt(row, 14);
					rows[16] = wztm.getValueAt(row, 15);
					rows[17] = wztm.getValueAt(row, 16);
					rows[18] = wztm.getValueAt(row, 17);

					partTableOp.addOneRow(rows);
				}
			}
		});
		deleteButton.addActionListener(new ActionListener() {
			JTable ptable = partTableOp.getZTable();
			DefaultTableModel dtm = (DefaultTableModel)partTableOp.getTableModel();
			@Override
			public void actionPerformed(ActionEvent e) {
				int isDelete = JOptionPane.showConfirmDialog(frame, "确定要删除所选行吗？","确定",JOptionPane.YES_NO_OPTION);
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
				int isDelete = JOptionPane.showConfirmDialog(frame, "确定要删除所选行吗？","确定",JOptionPane.YES_NO_OPTION);
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

		moveUpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTableUtil.changeRowValue(true,partTableOp.getZTable());
			}
		});

		moveDownButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTableUtil.changeRowValue(false,partTableOp.getZTable());
			}
		});

		saveButton.addActionListener(new SaveWlbmListener(this,frame,partTableOp.getTableModel()));
	}

	@Override
	protected void initComponents() {

		//将零部件的材料属性设置到物资库查询条件里
		wzkPanel.setInitValue(this.node);

		partTableOp =  new DefaultZTableFactory();
		partTableOp.setColumnsEditable(partTableEditCols);
		partTableOp.setTableInfors(partTableHeader, partTableBody,partTableColWidth);
		partTableOp.setTableStyle(partTableOp.getZTable());
		partTableOp.setColumnsHidden(partTableOp.getZTable(), partTableHideCols);

		TableColumn tableColumn = partTableOp.getZTable().getColumn("*单位");
        tableColumn.setCellEditor(new DefaultCellEditor(CommonUtil.getDWJComboBox()));

		saveButton = new JButton("保 存");
		deleteButton = new JButton("删除选择行");
		deleteAllButton = new JButton("删除所有行");
		moveUpButton = new JButton("上 移");
		moveDownButton = new JButton("下 移");

		centerPanel = new JPanel();
		bottomPanel = new JPanel();
	}

	protected void loadInitDatas() {
		partTableHeader = new String[]{"序号","存货编码","存货名称","*下料尺寸","*可制件数","*单位","型号牌号","规格","技术条件","生产厂家","主计量单位","附加条件","供应状态/热处理"
				,"质量等级","封装形式","精度等级","螺纹规格/公称尺寸","机械性能等级","电参数特选要求","备注"};
		partTableBody = new String[0][partTableHeader.length];

		int tw1 = 200;
		partTableColWidth = new int[]{tw1-175,tw1,tw1+30,tw1-150,tw1-150,tw1-150,tw1-20,tw1-20,tw1-20,tw1-20
				,tw1-150,tw1,tw1,tw1+100,tw1+100,tw1+100,tw1+100,tw1+100,tw1+100,tw1+100};
		partTableEditCols =new int[]{3,4,5,13,19};
		partTableHideCols = new int[]{};
	}

	private void loadOldDatas() {
		if (node != null) {
			XWTreeObject xo = node.getObject();
			if ((xo instanceof XWTechnicsTreeObject)) {
				Element element = xo.getTreeCellData();
				Element clde = XmlUtility.getTechnicsCLDEElement(element);
				if(clde == null) {
					return ;
				}
				List<Element> list = XmlUtility.getTechnicsYCLDE(clde);
				if(list != null && !list.isEmpty()) {
					TableModel parttm = partTableOp.getTableModel();
					int n = parttm.getRowCount()+1;
					for (Element ele : list) {
						Object [] rows = new Object[partTableHeader.length];
						rows[0] = n++;
						rows[1] = objectToString(ele.attributeValue("chbm"));
						rows[2] = objectToString(ele.attributeValue("chmc"));
						rows[3] = objectToString(ele.attributeValue("xlcc"));
						rows[4] = objectToString(ele.attributeValue("kzjs"));
						rows[5] = objectToString(ele.attributeValue("dw"));
						rows[6] = objectToString(ele.attributeValue("xhph"));
						rows[7] = objectToString(ele.attributeValue("gg"));
						rows[8] = objectToString(ele.attributeValue("jstj"));
						rows[9] = objectToString(ele.attributeValue("sccj"));
						rows[10] = objectToString(ele.attributeValue("zjldw"));
						rows[11] = objectToString(ele.attributeValue("fjtj"));
						rows[12] = objectToString(ele.attributeValue("gyztrcl"));
						rows[13] = objectToString(ele.attributeValue("zldj"));
						rows[14] = objectToString(ele.attributeValue("fzxs"));
						rows[15] = objectToString(ele.attributeValue("jddj"));
						rows[16] = objectToString(ele.attributeValue("lwgg"));
						rows[17] = objectToString(ele.attributeValue("jxxndj"));
						rows[18] = objectToString(ele.attributeValue("dcstxyq"));
						rows[19] = objectToString(ele.attributeValue("comment"));
						partTableOp.addOneRow(rows);
					}
				}
			}
		}
	}

	@Override
	protected void initLayout() {
		JScrollPane wztmsp = new JScrollPane(partTableOp.getZTable());
		wztmsp.getViewport().setBackground(Color.WHITE);
		BoxLayout box = new BoxLayout (centerPanel,BoxLayout.Y_AXIS) ;
		centerPanel.setLayout(box);
		centerPanel.add(wzkPanel);
		final JLabel lbjLable = new JLabel("材料列表");
		lbjLable.setFont(new Font("宋体",Font.PLAIN,20));
		JPanel partPanel = new JPanel();
		partPanel.setLayout(new BorderLayout());
		partPanel.add(lbjLable,BorderLayout.NORTH);
		partPanel.add(wztmsp,BorderLayout.CENTER);
		centerPanel.add(partPanel);
		bottomPanel.add(saveButton);
		bottomPanel.add(deleteButton);
		bottomPanel.add(deleteAllButton);
//		bottomPanel.add(moveUpButton);
//		bottomPanel.add(moveDownButton);


		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
//		mainPanel.add(wzkPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel,BorderLayout.CENTER);
		mainPanel.add(bottomPanel,BorderLayout.SOUTH);

		this.setContentPane(mainPanel);
	}

	final class SaveWlbmListener implements ActionListener{
		private JDialog dialog;
		private NewTechnicsPart frame;
		private TableModel tm;

		public  SaveWlbmListener(JDialog dialog,NewTechnicsPart frame,TableModel tm){
			this.dialog = dialog;
			this.frame = frame;
			this.tm = tm;
		}

		public void actionPerformed(ActionEvent e) {
			int rows = tm.getRowCount();

			if(!checkValue(tm)) {
				JOptionPane.showMessageDialog(frame, "下料尺寸,可制件数或单位不能为空！");
				return;
			}

//			if(!checkIsNumeric()) {
//				JOptionPane.showMessageDialog(owner, "可制件数只能填写0-9的数字！");
//				return;
//			}
			for (int i = 0; i < rows; i++) {
				String num = objectToString(tm.getValueAt(i, 4));
				String unit = objectToString(tm.getValueAt(i, 5));
				if(!CommonUtil.checkGysl(num,unit)){
					JOptionPane.showMessageDialog(owner, "第" + (i+1) + "行可制件数填写不规范：\r\n单位为“个”，“只”，“件”的数量只能为整数，其他的只能为最多三位小数");
					return;
				}
			}

			int flag = JOptionPane.showConfirmDialog(frame, "确定保存吗？","确认", JOptionPane.OK_CANCEL_OPTION);
			if(flag==JOptionPane.YES_OPTION){
				if (node != null) {
					XWTreeObject xo = node.getObject();
					if ((xo instanceof XWTechnicsTreeObject)) {
						Element element = xo.getTreeCellData();
						Element clde = XmlUtility.getTechnicsCLDEElement(element);
						Element yclde = XmlUtility.getChildElements(clde, "YCLDE");
						if(yclde == null) {
							yclde = DocumentHelper.createElement("YCLDE");
						} else {
							XmlUtility.deleteAllChildElements(yclde);
						}
						for(int i=0;i<rows;i++){
							Element ycldeRecord = createTechnicsYCLDEElement(i, tm);
							yclde.add(ycldeRecord);
						}
						String techPath = WorkSpaceUtil.getTechnicsDirectory(element.attributeValue("technicsNumber"));
						String xmlFilePath = techPath + File.separator + element.attributeValue("technicsNumber")+".xml";
//						System.out.println("---------xmlFilePath-----"+xmlFilePath);
						try {
							//clde.add(yclde);
							XmlUtility.saveDocument(element.getDocument(), xmlFilePath);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						UploadTechnics technics = ObjectTransfer.technicsElementToUploadTechnics(element);
						if (!frame.editTechnics.contains(technics)) {
							frame.editTechnics.add(technics);
						}
					}
				}
				dialog.dispose();
			}
		}

		private boolean checkIsNumeric() {
			int rows = tm.getRowCount();
			for(int i=0;i<rows;i++){
				String num = objectToString(tm.getValueAt(i, 4));
				boolean b = CommonUtil.isDouble(num);
				if(!b) {
					return false;
				}
			}
			return true;
		}
	}

	private Element createTechnicsYCLDEElement(int i,TableModel tm) {
		Element element = DocumentHelper.createElement("ycldeRecord");
		XmlUtility.setAttributeValue(element, "chbm", objectToString(tm.getValueAt(i, 1)));
		XmlUtility.setAttributeValue(element, "chmc", objectToString(tm.getValueAt(i, 2)));
		XmlUtility.setAttributeValue(element, "xlcc", objectToString(tm.getValueAt(i, 3)));
		XmlUtility.setAttributeValue(element, "kzjs", objectToString(tm.getValueAt(i, 4)));
		XmlUtility.setAttributeValue(element, "dw", objectToString(tm.getValueAt(i, 5)));
		XmlUtility.setAttributeValue(element, "xhph", objectToString(tm.getValueAt(i, 6)));
		XmlUtility.setAttributeValue(element, "gg", objectToString(tm.getValueAt(i, 7)));
		XmlUtility.setAttributeValue(element, "jstj", objectToString(tm.getValueAt(i, 8)));
		XmlUtility.setAttributeValue(element, "sccj", objectToString(tm.getValueAt(i, 9)));
		XmlUtility.setAttributeValue(element, "zjldw", objectToString(tm.getValueAt(i, 10)));
		XmlUtility.setAttributeValue(element, "fjtj", objectToString(tm.getValueAt(i, 11)));
		XmlUtility.setAttributeValue(element, "gyztrcl", objectToString(tm.getValueAt(i, 12)));
		XmlUtility.setAttributeValue(element, "zldj", objectToString(tm.getValueAt(i, 13)));
		XmlUtility.setAttributeValue(element, "fzxs", objectToString(tm.getValueAt(i, 14)));
		XmlUtility.setAttributeValue(element, "jddj", objectToString(tm.getValueAt(i, 15)));
		XmlUtility.setAttributeValue(element, "lwgg", objectToString(tm.getValueAt(i, 16)));
		XmlUtility.setAttributeValue(element, "jxxndj", objectToString(tm.getValueAt(i, 17)));
		XmlUtility.setAttributeValue(element, "dcstxyq", objectToString(tm.getValueAt(i, 18)));
		XmlUtility.setAttributeValue(element, "comment", objectToString(tm.getValueAt(i, 19)));
		XmlUtility.setAttributeValue(element, "dataFrom", "erp");//数据来源
		return element;
	}

	private boolean checkValue(TableModel tm) {
		int rows = tm.getRowCount();
		for(int i=0;i<rows;i++){
			String xlcc = objectToString(tm.getValueAt(i, 3));
			if(xlcc == null || "".equals(xlcc)) {
				return false;
			}
			String kzjs = objectToString(tm.getValueAt(i, 4));
			if(kzjs == null || "".equals(kzjs)) {
				return false;
			}
			String dw = objectToString(tm.getValueAt(i, 5));
			if(dw == null || "".equals(dw)) {
				return false;
			}
		}
		return true;
	}

	public static String objectToString(Object obj) {
		if (null == obj || "".equals(obj)) {
			return "";
		} else {
			return String.valueOf(obj);
		}
	}
}