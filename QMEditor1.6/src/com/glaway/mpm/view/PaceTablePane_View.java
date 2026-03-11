package com.glaway.mpm.view;

import com.faw_qm.speChar.speChar.SpeClassUtil;
import com.faw_qm.speChar.speChar.SpeIcon;
import com.faw_qm.speChar.view.MyUtilities;
import com.faw_qm.speChar.view.SpeCharPanel;
import com.glaway.mpm.pdf.HtmlGenerator;
import com.glaway.mpm.qmIntf.commonString.CsSearchDialog;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.JavaUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.speciaword.common.CommonHelper;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.server.UID;
import java.util.List;
import java.util.*;

public class PaceTablePane_View extends NewLinkJPanel {
	private boolean bool = false;
	private NewTechnicsHistoryView frame;
	private TechnicsStepJPanel_View stepPanel;
	private boolean isEdit = true;

	private Element paces = null;

	private HashMap pacesCashe = new HashMap();

	protected JButton dynamicAssemblagePicture = new IconButton("/images/button_pic_review.png", "装配工具");

	private static final Font useFont = new Font("Dialog", 0, 12);
	private static JButton btn = new JButton();
	private static final FontMetrics metrics = btn.getFontMetrics(useFont);

	private ContentEditor ce = new ContentEditor(new JTextField());

	private String imageFolder;

	public PaceTablePane_View(NewTechnicsHistoryView fra, TechnicsStepJPanel_View stepPanel) {
		super(stepPanel);
		this.frame = fra;
		this.stepPanel = stepPanel;

		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.setCellSelectionEnabled(true);

		this.panel.add(this.dynamicAssemblagePicture, new GridBagConstraints(1,
				6, 1, 1, 1.0D, 0.0D, 10, 0, new Insets(5, 5, 0, 5), 0, 0));

		// 第一列：控制点(G)
		this.table.getColumnModel().getColumn(0)
				.setCellRenderer(new IsKeyRenderer());
		this.table.getColumnModel().getColumn(0)
				.setCellEditor(new IsKeyEditor(new JCheckBox()));

		// 第二列：工步号
		this.table.getColumnModel().getColumn(1).setCellRenderer(new LabelRender());
		this.table.getColumnModel().getColumn(1).setCellEditor(new StepNumberEditor(new JTextField()));

		// 第三列：工步内容
		// this.table.getColumnModel().getColumn(2).setCellRenderer(new
		// PaceContentRenderer());
		// this.table.getColumnModel().getColumn(2).setCellEditor(this.ce);
		this.table.getColumnModel().getColumn(2)
				.setCellRenderer(new SWRenderer());
		this.table.getColumnModel().getColumn(2).setCellEditor(new SWEditor());

		// 第四列：设备
		this.table.getColumnModel().getColumn(3)
				.setCellRenderer(new PaceContentRenderer());

		// 第五列：工装及工具
		this.table.getColumnModel().getColumn(4)
				.setCellRenderer(new PaceContentRenderer());

		// 第六列：参装件
		this.table.getColumnModel().getColumn(5)
				.setCellRenderer(new PaceContentRenderer());

		// 第七列：工艺辅料
		this.table.getColumnModel().getColumn(6)
				.setCellRenderer(new PaceContentRenderer());

		// 第八列：刀具
		this.table.getColumnModel().getColumn(7)
				.setCellRenderer(new PaceContentRenderer());

		// 第九列：标准仪器仪表
		this.table.getColumnModel().getColumn(8)
				.setCellRenderer(new PaceContentRenderer());

		// 第十列：非标准仪器仪表
		this.table.getColumnModel().getColumn(9)
				.setCellRenderer(new PaceContentRenderer());

		// 第十一列：量具
		this.table.getColumnModel().getColumn(10)
				.setCellRenderer(new PaceContentRenderer());

		//第十二列：程序号
		this.table.getColumnModel().getColumn(11).setCellRenderer(new LabelRender());
		this.table.getColumnModel().getColumn(11).setCellEditor(new StepProgramNoEditor(new JTextField()));

		this.table.getTableHeader().setReorderingAllowed(false);
		this.table.setRowHeight(25);

		this.table.getTableHeader().setReorderingAllowed(false);
		this.table.setRowHeight(25);

		// 隐藏列
		setHiddenColumn(12);

		JTableHeader header = this.table.getTableHeader();
		header.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				PaceTablePane_View.this.table.setRowHeight(25);
			}
		});
		init();
	}

	protected void addModelColumn() {
		this.tableModel.addColumn("控制点(G)");
		this.tableModel.addColumn("工步号");
		this.tableModel.addColumn("工步内容");
		this.tableModel.addColumn("设备");
		this.tableModel.addColumn("工装及工具");
		this.tableModel.addColumn("参装件");
		this.tableModel.addColumn("工艺辅料");
		this.tableModel.addColumn("刀具");
		this.tableModel.addColumn("标准仪器仪表");
		this.tableModel.addColumn("非标准仪器仪表");
		this.tableModel.addColumn("量具");
		this.tableModel.addColumn("程序号");
		this.tableModel.addColumn("bsoID");
	}

	public Vector<Element> getElements() {
		Vector result = new Vector();
		if (this.table.getRowCount() > 0) {
			for (int i = 0; i < this.table.getRowCount(); i++) {
				String num = (String) this.table.getValueAt(i, 9);
				Element ele = (Element) this.pacesCashe.get(num);
				if (ele != null)
					result.add(ele.clone());
			}
		}
		List list = this.paces.elements();
		if (list != null)
			list.clear();
		for (int i = 0; i < result.size(); i++) {
			Element ele = (Element) result.get(i);
			this.paces.add(ele);
		}
		return result;
	}

	private void init() {
		this.table.getColumnModel().getColumn(0).setPreferredWidth(50);
		this.table.getColumnModel().getColumn(0).setMinWidth(50);
		this.table.getColumnModel().getColumn(0).setMaxWidth(50);

		this.table.getColumnModel().getColumn(1).setPreferredWidth(50);
		this.table.getColumnModel().getColumn(1).setMinWidth(50);
		this.table.getColumnModel().getColumn(1).setMaxWidth(50);

		this.table.getColumnModel().getColumn(2).setPreferredWidth(300);
		this.table.getColumnModel().getColumn(2).setMinWidth(300);
		this.table.getColumnModel().getColumn(2).setMaxWidth(2147483647);

		this.table.getColumnModel().getColumn(3).setPreferredWidth(80);
		this.table.getColumnModel().getColumn(3).setMinWidth(80);
		this.table.getColumnModel().getColumn(3).setMaxWidth(2147483647);

		this.table.getColumnModel().getColumn(4).setPreferredWidth(80);
		this.table.getColumnModel().getColumn(4).setMinWidth(80);
		this.table.getColumnModel().getColumn(4).setMaxWidth(2147483647);

		this.table.getColumnModel().getColumn(5).setPreferredWidth(50);
		this.table.getColumnModel().getColumn(5).setMinWidth(50);
		this.table.getColumnModel().getColumn(5).setMaxWidth(2147483647);

		this.table.getColumnModel().getColumn(6).setPreferredWidth(50);
		this.table.getColumnModel().getColumn(6).setMinWidth(50);
		this.table.getColumnModel().getColumn(6).setMaxWidth(2147483647);

		this.table.getColumnModel().getColumn(7).setPreferredWidth(50);
		this.table.getColumnModel().getColumn(7).setMinWidth(50);
		this.table.getColumnModel().getColumn(7).setMaxWidth(2147483647);

		this.table.getColumnModel().getColumn(8).setPreferredWidth(50);
		this.table.getColumnModel().getColumn(8).setMinWidth(50);
		this.table.getColumnModel().getColumn(8).setMaxWidth(2147483647);

		this.table.getColumnModel().getColumn(9).setPreferredWidth(50);
		this.table.getColumnModel().getColumn(9).setMinWidth(50);
		this.table.getColumnModel().getColumn(9).setMaxWidth(2147483647);

		this.table.getColumnModel().getColumn(10).setPreferredWidth(50);
		this.table.getColumnModel().getColumn(10).setMinWidth(50);
		this.table.getColumnModel().getColumn(10).setMaxWidth(2147483647);

		this.table.getColumnModel().getColumn(11).setPreferredWidth(50);
		this.table.getColumnModel().getColumn(11).setMinWidth(50);
		this.table.getColumnModel().getColumn(11).setMaxWidth(2147483647);
	}

	public String getAllEquips(Element pace) {
		String equips = "";
		Vector vec = new Vector();
		List l1 = XmlUtility.getEquips(pace).elements();
		if ((l1 != null) && (l1.size() > 0)) {
			for (int i = 0; i < l1.size(); i++) {
				Element eq = (Element) l1.get(i);
				String te = XmlUtility.getAttributeValue(eq, "eqName");
				if (!vec.contains(te))
					vec.add(te);
			}
		}
		for (int i = 0; i < vec.size(); i++) {
			String te = (String) vec.get(i);
			if (equips.trim().length() > 0)
				equips = equips + "\n";
			equips = equips + te;
		}
		return equips;
	}

	public String getAllMeasures(Element pace) {
		String equips = "";
		Vector vec = new Vector();
		List l1 = XmlUtility.getMeasures(pace).elements();
		if ((l1 != null) && (l1.size() > 0)) {
			for (int i = 0; i < l1.size(); i++) {
				Element eq = (Element) l1.get(i);
				String te1 = XmlUtility.getAttributeValue(eq, "number");
				String te2 = XmlUtility.getAttributeValue(eq, "name");
				String te = te1 + " " + te2;
				if (!vec.contains(te))
					vec.add(te);
			}
		}
		for (int i = 0; i < vec.size(); i++) {
			String te = (String) vec.get(i);
			if (equips.trim().length() > 0)
				equips = equips + "\n";
			equips = equips + te;
		}
		return equips;
	}

	public String getAllDashboards(Element pace) {
		String equips = "";
		Vector vec = new Vector();
		List l1 = XmlUtility.getSDashboards(pace).elements();
		if ((l1 != null) && (l1.size() > 0)) {
			for (int i = 0; i < l1.size(); i++) {
				Element eq = (Element) l1.get(i);
				String te1 = XmlUtility.getAttributeValue(eq, "number");
				String te2 = XmlUtility.getAttributeValue(eq, "name");
				String te = te1 + " " + te2;
				if (!vec.contains(te))
					vec.add(te);
			}
		}
		for (int i = 0; i < vec.size(); i++) {
			String te = (String) vec.get(i);
			if (equips.trim().length() > 0)
				equips = equips + "\n";
			equips = equips + te;
		}
		return equips;
	}

	public String getAllUnSDashboards(Element pace) {
		String equips = "";
		Vector vec = new Vector();
		List l1 = XmlUtility.getUnsdashboards(pace).elements();
		if ((l1 != null) && (l1.size() > 0)) {
			for (int i = 0; i < l1.size(); i++) {
				Element eq = (Element) l1.get(i);
				String te1 = XmlUtility.getAttributeValue(eq, "number");
				String te2 = XmlUtility.getAttributeValue(eq, "name");
				String te = te1 + " " + te2;
				if (!vec.contains(te))
					vec.add(te);
			}
		}
		for (int i = 0; i < vec.size(); i++) {
			String te = (String) vec.get(i);
			if (equips.trim().length() > 0)
				equips = equips + "\n";
			equips = equips + te;
		}
		return equips;
	}

	public String getAllTools(Element pace) {
		String tools = "";
		Vector vec = new Vector();
		List l1 = XmlUtility.getTools(pace).elements();
		if ((l1 != null) && (l1.size() > 0)) {
			for (int i = 0; i < l1.size(); i++) {
				Element tool = (Element) l1.get(i);
				String te = XmlUtility.getAttributeValue(tool, "toolName");
				if (!vec.contains(te))
					vec.add(te);
			}
		}
		for (int i = 0; i < vec.size(); i++) {
			String te = (String) vec.get(i);
			if (tools.trim().length() > 0)
				tools = tools + "\n";
			tools = tools + te;
		}
		return tools;
	}

	public String getAllKnifeTools(Element pace) {
		String tools = "";
		Vector vec = new Vector();
		List l1 = XmlUtility.getKnifeTools(pace).elements();
		if ((l1 != null) && (l1.size() > 0)) {
			for (int i = 0; i < l1.size(); i++) {
				Element tool = (Element) l1.get(i);
				String te1 = XmlUtility.getAttributeValue(tool, "toolNum");
				String te2 = XmlUtility.getAttributeValue(tool, "toolName");
				String te = te1 + " " + te2;
				if (!vec.contains(te))
					vec.add(te);
			}
		}
		for (int i = 0; i < vec.size(); i++) {
			String te = (String) vec.get(i);
			if (tools.trim().length() > 0)
				tools = tools + "\n";
			tools = tools + te;
		}
		return tools;
	}

	public String getAllMaterials(Element pace) {
		String materials = "";
		Vector vec = new Vector();
		List l1 = XmlUtility.getMaterials(pace).elements();
		if ((l1 != null) && (l1.size() > 0)) {
			for (int i = 0; i < l1.size(); i++) {
				Element mal = (Element) l1.get(i);
				String te = XmlUtility.getAttributeValue(mal, "materialName");
				if (!vec.contains(te))
					vec.add(te);
			}
		}
		for (int i = 0; i < vec.size(); i++) {
			String te = (String) vec.get(i);
			if (materials.trim().length() > 0)
				materials = materials + "\n";
			materials = materials + te;
		}
		return materials;
	}

	public String getAllParts(Element pace) {
		String part = "";
		Vector vec = new Vector();
		List l1 = XmlUtility.getParts(pace).elements();
		if ((l1 != null) && (l1.size() > 0)) {
			for (int i = 0; i < l1.size(); i++) {
				Element pt = (Element) l1.get(i);
				String te = XmlUtility.getAttributeValue(pt, "partNumber");
				if (!vec.contains(te))
					vec.add(te);
			}
		}
		for (int i = 0; i < vec.size(); i++) {
			String te = (String) vec.get(i);
			if (part.trim().length() > 0)
				part = part + "\n";
			part = part + te;
		}
		return part;
	}

	public void setOneRowTableValue(Element element) {
		if ((element != null) && (element.getName().equals("QMProcedureInfo"))) {
			String isKey = XmlUtility.getAttributeValue(element, "isKey");
			String paceNum = XmlUtility.getAttributeValue(element, "stepNumber");
			String progamrNo = XmlUtility.getAttributeValue(element, "programNo");
			if(progamrNo == null) {
				progamrNo = "";
			}
			String paceContent = XmlUtility.getProcedureContent(element);
			String shop = XmlUtility.getAttributeValue(element, "workShop");
			String type = XmlUtility.getAttributeValue(element, "workType");
			String bsoID = XmlUtility.getAttributeValue(element, "bsoID");
			if ((bsoID == null) || (bsoID.trim().length() == 0)) {
				bsoID = new UID().toString();
				XmlUtility.setAttributeValue(element, "bsoID", bsoID);
			}
			this.pacesCashe.put(bsoID, element);

			String equips = getAllEquips(element);
			String tools = getAllTools(element);
			String knifeTools = getAllKnifeTools(element);
			String materials = getAllMaterials(element);
			String parts = getAllParts(element);
			String dashboards = getAllDashboards(element);
			String unsdashboards = getAllUnSDashboards(element);
			String measures = getAllMeasures(element);

			super.addProcess();
			int i = this.tableModel.getRowCount();
			i--;
			this.table.setRowHeight(i, 25);
			this.tableModel.setValueAt(isKey, i, 0);
			this.tableModel.setValueAt(paceNum, i, 1);
			this.tableModel.setValueAt(paceContent, i, 2);
			this.tableModel.setValueAt(equips, i, 3);
			this.tableModel.setValueAt(tools, i, 4);
			this.tableModel.setValueAt(parts, i, 5);
			this.tableModel.setValueAt(materials, i, 6);
			this.tableModel.setValueAt(knifeTools, i, 7);
			this.tableModel.setValueAt(dashboards, i, 8);
			this.tableModel.setValueAt(unsdashboards, i, 9);
			this.tableModel.setValueAt(measures, i, 10);
			this.tableModel.setValueAt(progamrNo, i, 11);
			this.tableModel.setValueAt(bsoID, i, 12);
		}
	}

	public void setOneRowTableValue(Element element, int row) {
		if ((row < 0) || (row >= this.table.getRowCount()))
			return;
		if ((element != null) && (element.getName().equals("QMProcedureInfo"))) {
			String isKey = XmlUtility.getAttributeValue(element, "isKey");
			String paceNum = XmlUtility.getAttributeValue(element, "stepNumber");
			String progamrNo = XmlUtility.getAttributeValue(element, "programNo");
			if(progamrNo == null) {
				progamrNo = "";
			}
			String paceContent = XmlUtility.getProcedureContent(element);
			String shop = XmlUtility.getAttributeValue(element, "workShop");
			String type = XmlUtility.getAttributeValue(element, "workType");
			String bsoID = XmlUtility.getAttributeValue(element, "bsoID");
			if ((bsoID == null) || (bsoID.trim().length() == 0)) {
				bsoID = new UID().toString();
				XmlUtility.setAttributeValue(element, "bsoID", bsoID);
			}
			this.pacesCashe.put(bsoID, element);

			String equips = getAllEquips(element);
			String tools = getAllTools(element);
			String knifeTools = getAllKnifeTools(element);
			String materials = getAllMaterials(element);
			String parts = getAllParts(element);
			String dashboards = getAllDashboards(element);
			String unsdashboards = getAllUnSDashboards(element);
			String measures = getAllMeasures(element);

			this.tableModel.setValueAt(isKey, row, 0);
			this.tableModel.setValueAt(paceNum, row, 1);
			this.tableModel.setValueAt(paceContent, row, 2);
			this.tableModel.setValueAt(equips, row, 3);
			this.tableModel.setValueAt(tools, row, 4);
			this.tableModel.setValueAt(parts, row, 5);
			this.tableModel.setValueAt(materials, row, 6);
			this.tableModel.setValueAt(knifeTools, row, 7);
			this.tableModel.setValueAt(dashboards, row, 8);
			this.tableModel.setValueAt(unsdashboards, row, 9);
			this.tableModel.setValueAt(measures, row, 10);
			this.tableModel.setValueAt(progamrNo, row, 11);
			this.tableModel.setValueAt(bsoID, row, 12);
		}
	}

	public void setTableValues(Vector<Element> vec) {
		this.pacesCashe.clear();
		clearTable();
		for (int i = 0; i < vec.size(); i++) {
			Element element = (Element) vec.get(i);
			setOneRowTableValue(element);
		}
	}

	public void setTableValues(Vector<Element> vec, int selectIndex) {
		setTableValues(vec);
		this.table.setRowSelectionInterval(selectIndex, selectIndex);
	}

	public void displayPaceDatas() {
		int select = this.table.getSelectedRow();
		System.out.println("当前选中行为=========" + select);
		if (this.paces != null) {
			List list = this.paces.elements();
			if ((list != null) && (list.size() > 0)) {
				// int num = 1;
				// for (int i = 0; i < list.size(); i++) {
				// Element pace = (Element) list.get(i);
				// String temp = XmlUtility.getAttributeValue(pace,
				// "stepNumber");
				// if ((temp != null) && (!temp.equals("检"))
				// && (!temp.equals("A")) && (!temp.equals("B"))) {
				// XmlUtility.setAttributeValue(pace, "stepNumber", "-"
				// + num);
				// num++;
				// }
				// }

				Vector vec = new Vector();
				vec.addAll(list);
				setTableValues(vec);
			} else {
				clearTable();
			}
		}
		if ((select >= 0) && (select < this.table.getRowCount())) {
			this.table.setRowSelectionInterval(select, select);
		}
		updateUI();
	}

	public int getPaceNum() {
		Vector vec = getElements();
		if (vec.size() == 0)
			return -1;
		int count = vec.size();
		return (count + 1) * -1;
	}

	public void paint(Graphics g) {
		if (!this.bool) {
			this.table.setRowHeight(25);
		}
		super.paint(g);
	}

	public void setPaces(Element paces) {
		try {
			this.paces = paces;
			if (this.frame != null) {
				Document doc = this.frame.getDocument();
				if (doc != null) {
					Element tech = XmlUtility.getTechnicsElement(doc);
					String technicsType = tech.attributeValue("technicsType");
					if ((technicsType != null) && (technicsType.equals("零件工艺"))) {
						this.table.getTableHeader().getColumnModel().getColumn(7).setMaxWidth(0);
						this.table.getTableHeader().getColumnModel().getColumn(7).setMinWidth(0);
						this.table.getColumnModel().getColumn(7).setMaxWidth(0);
						this.table.getColumnModel().getColumn(7).setPreferredWidth(0);
						this.table.getColumnModel().getColumn(7).setWidth(0);
						this.table.getColumnModel().getColumn(7).setMinWidth(0);

						this.dynamicAssemblagePicture.setVisible(false);
					} else {
						this.table.getTableHeader().getColumnModel().getColumn(7).setMaxWidth(2147483647);
						this.table.getTableHeader().getColumnModel().getColumn(7).setMinWidth(50);
						this.table.getColumnModel().getColumn(7).setMaxWidth(2147483647);
						this.table.getColumnModel().getColumn(7).setPreferredWidth(50);
						this.table.getColumnModel().getColumn(7).setWidth(50);
						this.table.getColumnModel().getColumn(7).setMinWidth(50);

						this.dynamicAssemblagePicture.setVisible(true);
					}
				}
			}
		} catch (Exception localException) {
		}
	}

	protected void removeProcess() {
		int[] rows = this.table.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			String num = (String) this.tableModel.getValueAt(rows[i], 9);
			Element ele = (Element) this.pacesCashe.get(num);
			if (ele != null) {
				this.pacesCashe.remove(num);
				if (ele.getParent() != null) {
					Element parent = ele.getParent();
					parent.remove(ele);
				}
			}
		}
		super.removeProcess();
	}

	public Element getPaces() {
		return this.paces;
	}

	public void addProcess() {
		Element ele = XmlUtility.createProcedure();
		XmlUtility.setAttributeValue(ele, "stepNumber", "-1");
		XmlUtility.setAttributeValue(ele, "bsoID", new UID().toString());
		if (ele != null) {
			getElements();
			int select = this.table.getSelectedRow();
			List list = this.paces.elements();
			if ((select == list.size() - 1) || (select == -1)) {
				list.add(ele);
			} else {
				list.add(select + 1, ele);
			}

			// int num = 1;
			// for (int i = 0; i < list.size(); i++) {
			// Element pace = (Element) list.get(i);
			// String temp = XmlUtility.getAttributeValue(pace, "stepNumber");
			// if ((temp != null) && (!temp.equals("检"))
			// && (!temp.equals("A")) && (!temp.equals("B"))) {
			// XmlUtility.setAttributeValue(pace, "stepNumber", "-" + num);
			// num++;
			// }
			// }

			Vector vec = new Vector(list);
			setTableValues(vec, select + 1);
		}
		updateUI();
	}

	public void addProcess(Element ele) {
		if (ele != null) {
			getElements();
			int select = this.table.getSelectedRow();
			System.out.println("当前选中行===========" + select);
			List list = this.paces.elements();
			System.out.println("当前工步数量===========" + list.size());
			if ((select == list.size() - 1) || (select == -1)) {
				list.add(ele);
			} else {
				list.add(select + 1, ele);
			}

			int num = 1;
			for (int i = 0; i < list.size(); i++) {
				Element pace = (Element) list.get(i);
				String temp = XmlUtility.getAttributeValue(pace, "stepNumber");
				if ((temp != null) && (temp.startsWith("-"))) {
					XmlUtility.setAttributeValue(pace, "stepNumber", "-" + num);
					num++;
				}
			}

			Vector vec = new Vector(list);
			setTableValues(vec, select + 1);
		}
		updateUI();
	}

	public void setUIEnabled(boolean b) {
		this.isEdit = b;
		super.setUIEnabled(b);
		this.dynamicAssemblagePicture.setEnabled(b);
		// this.toolJButton.setEnabled(b);
		// this.pasteJButton.setEnabled(b);
	}

	public void update(Observable o, Object arg) {
	}

	class ContentEditor extends DefaultCellEditor {
		private int startLocation = -1;
		private int endLocation = -1;
		private SpeCharPanel textPane = new SpeCharPanel(null, true, true);

		private JTable editingTable = null;
		private int editingRow = -1;
		private int editingColumn = -1;

		private boolean auto = false;

		private boolean bool = false;

		ContentEditor(JTextField textField) {
			super(textField);

			JMenuItem terminologyItem = new JMenuItem("插入工艺常用语");
			this.textPane.addItem(terminologyItem);
			terminologyItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						String commonString = null;
						String terminologyXMLPath = WorkSpaceUtil.getPersonalTerminologyDirectory();
						CsSearchDialog dia = new CsSearchDialog(terminologyXMLPath, frame);
						commonString = dia.showDialog();
						System.out.println("调用信维接口返回值为=====commonString====" + commonString);
						if (commonString == null) {
							commonString = "";
						}
						PaceTablePane_View.ContentEditor.this.textPane.insertString(commonString);
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				}
			});
			this.textPane.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					System.out.println("textPane==SIZE==="
							+ PaceTablePane_View.ContentEditor.this.textPane.getBounds());
				}
			});
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			this.auto = false;
			table.setRowHeight(25);
			this.editingTable = table;
			this.editingRow = row;
			this.editingColumn = column;
			this.textPane.setText("");

			if ((value != null) && ((value instanceof String))) {
				String txt = value.toString();
				this.textPane.setText(txt);
			}
			this.auto = true;
			return this.textPane;
		}

		public Object getCellEditorValue() {
			String content = this.textPane.getText();
			this.startLocation = this.textPane.getSelectionStart();
			this.endLocation = this.textPane.getSelectionEnd();
			if (this.editingTable != null) {
				if ((this.editingRow >= 0)
						&& (this.editingRow < this.editingTable.getRowCount())
						&& (this.editingColumn >= 0)
						&& (this.editingColumn < this.editingTable
								.getColumnCount())) {
					this.editingTable.setRowHeight(25);
					if (content != null) {
						this.editingTable.setValueAt(content, this.editingRow,
								this.editingColumn);
						String bsoID = (String) this.editingTable.getValueAt(
								this.editingRow, 9);
						if (bsoID != null) {
							Element pace = (Element) PaceTablePane_View.this.pacesCashe
									.get(bsoID);
							if (pace != null) {
								XmlUtility.setProcedureContent(pace, content);
							}
						}
					}
				}
			}
			return content;
		}

		public int getSelectionStart() {
			return this.startLocation;
		}

		public int getSelectionEnd() {
			return this.endLocation;
		}

		public int getClickCountToStart() {
			return 1;
		}
	}

	class IsKeyEditor extends DefaultCellEditor {
		JPanel panel = null;
		JCheckBox checkBox = null;
		private JTable editingTable = null;
		private int editingRow = -1;
		private int editingColumn = -1;

		IsKeyEditor(JCheckBox box) {
			super(box);
			this.checkBox = box;
			this.panel = new JPanel();
			this.panel.setLayout(new GridBagLayout());
			this.panel.add(this.checkBox, new GridBagConstraints(0, 0, 1, 1,
					0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			this.editingTable = table;
			this.editingRow = row;
			this.editingColumn = column;
			this.checkBox.setSelected(false);
			if ((value != null) && ((value instanceof String))) {
				if (value.toString().equalsIgnoreCase("true")) {
					this.checkBox.setSelected(true);
				}
			}
			return this.panel;
		}

		public Object getCellEditorValue() {
			boolean bool = this.checkBox.isSelected();

			if (this.editingTable != null) {
				if ((this.editingRow >= 0)
						&& (this.editingRow < this.editingTable.getRowCount())
						&& (this.editingColumn >= 0)
						&& (this.editingColumn < this.editingTable
								.getColumnCount())) {
					this.editingTable.setValueAt(String.valueOf(bool),
							this.editingRow, this.editingColumn);
					String bsoID = (String) this.editingTable.getValueAt(
							this.editingRow, 9);
					if (bsoID != null) {
						Element pace = (Element) PaceTablePane_View.this.pacesCashe
								.get(bsoID);
						if (pace != null) {
							XmlUtility.setAttributeValue(pace, "isKey",
									String.valueOf(bool));
						}
					}
				}
			}
			return String.valueOf(bool);
		}

		public int getClickCountToStart() {
			return 1;
		}
	}

	class PaceContentRenderer implements TableCellRenderer {
		private boolean bool = false;

		PaceContentRenderer() {
		}

		public Component getTableCellRendererComponent(JTable arg0,
				Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
			int width = arg0.getColumnModel().getColumn(arg5).getWidth();
			int height = arg0.getRowHeight(arg4);
			int h = height;
			SpeCharPanel speCharPanel = new SpeCharPanel(null, true, true);

			if ((arg1 != null) && ((arg1 instanceof String))) {
				String txt = arg1.toString();
				speCharPanel.setText(txt);
				if (txt.length() > 0) {
					ArrayList textList = new ArrayList();
					ArrayList LineList = SpeClassUtil.splitContent(
							arg1.toString(), PaceTablePane_View.useFont);
					ArrayList lineEnds = MyUtilities.splitString(LineList,
							width, PaceTablePane_View.metrics);
					ArrayList result = new ArrayList();
					if ((lineEnds == null) || (lineEnds.size() == 0)) {
						textList.add("");
					}
					ArrayList list = MyUtilities.getLines(LineList, lineEnds);

					int maxWidth = width;
					if (list != null) {
						for (int i = 0; i < list.size(); i++) {
							ArrayList al = (ArrayList) list.get(i);
							int w = 0;
							for (int m = 0; m < al.size(); m++) {
								Object o = al.get(m);
								if ((o instanceof String)) {
									w += PaceTablePane_View.metrics
											.stringWidth(o.toString());
								}
								if ((o instanceof SpeIcon)) {
									SpeIcon icon = (SpeIcon) o;
									w += icon.getIconWidth();
								}
							}
							if (w > maxWidth) {
								maxWidth = w;
							}
						}
						if (width < maxWidth) {
							JTableHeader header = arg0.getTableHeader();
							TableColumn column = arg0.getColumnModel()
									.getColumn(arg5);
							header.setResizingColumn(column);
							column.setMinWidth(maxWidth);
							column.setPreferredWidth(maxWidth);
							column.setMaxWidth(maxWidth);
						}
					}
					int rows = list.size();
					h = 25 * rows;
					if (h < height)
						h = height;
					if (height != h) {
						arg0.setRowHeight(arg4, h);
					}
				}
			}
			if (arg2) {
				speCharPanel.setForeground(PaceTablePane_View.this.table
						.getSelectionForeground());
				speCharPanel.setBackground(PaceTablePane_View.this.table
						.getSelectionBackground());
			} else {
				speCharPanel.setForeground(PaceTablePane_View.this.table
						.getForeground());
				speCharPanel.setBackground(PaceTablePane_View.this.table
						.getBackground());
			}
			return speCharPanel;
		}
	}

	// ====meixin====
	class SWEditor extends DefaultCellEditor {

		private static final long serialVersionUID = 1L;

		final SpecialWordPanel panle = new SpecialWordPanel(frame, getImageFolder());
		private JTable editingTable = null;
		private int editingRow = -1;
		private int editingColumn = -1;

		public SWEditor() {
			super(new JTextField());

			editorComponent = panle;

			delegate = new DefaultCellEditor.EditorDelegate() {
				@Override
				public Object getCellEditorValue() {
					return panle.getText();
				}

				@Override
				public void setValue(Object value) {
					panle.setText((value != null) ? CommonHelper
							.replaceReadSeperator(value.toString(),getImageFolder()) : "");
				}
			};
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			panle.setTechnicsPath(getImageFolder());
			this.editingTable = table;
			this.editingRow = row;
			this.editingColumn = column;
			System.out.println("------"+getImageFolder());
			String text = CommonHelper.replaceReadSeperator(JavaUtil.convertNull(value), getImageFolder());
			panle.setText(text);
			return this.panle;
		}

		@Override
		public Object getCellEditorValue() {
			String content = panle.getText();
			this.editingTable.setValueAt(content, this.editingRow,
					this.editingColumn);
			String bsoID = (String) this.editingTable.getValueAt(
					this.editingRow, 13);
			if (bsoID != null) {
				Element pace = (Element) PaceTablePane_View.this.pacesCashe.get(bsoID);
				if (pace != null) {
					if (content != null) {
						content = content.replaceAll(CommonUtil.SPECIAL_SPACE, "");
					}
					content = HtmlGenerator.removeImageTags(content);
					String text = CommonHelper.replaceSaveSeperator(content,getImageFolder());
					XmlUtility.setProcedureContent(pace, text);
				}
			}
			return panle.getText();
		}

		public void insertText(String str) {
			if (str != null && editorComponent != null) {
				((SpecialWordPanel) editorComponent).insertText(str);
			}
		}

	}

	class SWRenderer extends SpecialWordPanel implements TableCellRenderer {

		private static final long serialVersionUID = 1L;
		private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();
		@SuppressWarnings("unchecked")
		private final Map cellSizes = new HashMap();

		public SWRenderer() {
			super(frame, getImageFolder());
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table,
				Object obj, boolean isSelected, boolean hasFocus,
				final int row, final int column) {
			adaptee.getTableCellRendererComponent(table, obj, isSelected,
					hasFocus, row, column);
			// setForeground(adaptee.getForeground());
			// setBackground(adaptee.getBackground());
			setBorder(null);
			setFont(adaptee.getFont());
			setText(adaptee.getText());

			TableColumnModel columnModel = table.getColumnModel();
			setSize(columnModel.getColumn(column).getWidth(), 100000);
			int height_wanted = (int) getPreferredSize().getHeight();
			addSize(table, row, column, height_wanted);
			height_wanted = findTotalMaximumRowSize(table, row);
			if (height_wanted != table.getRowHeight(row)) {
				// if(table.getRowCount() > 1){
				table.setRowHeight(row, height_wanted);
				// }
			}
			// this.addCheckDocumentChangeListener(new
			// CheckDocumentChangeInterface() {
			// @Override
			// public void documentContentChange(String content) {
			// int height_wanted = (int) getPreferredSize().getHeight();
			// addSize(table, row, column, height_wanted);
			// height_wanted = findTotalMaximumRowSize(table, row);
			// if (height_wanted != table.getRowHeight(row)) {
			// // if(table.getRowCount() > 1){
			// table.setRowHeight(row, height_wanted);
			// // }
			// }
			// }
			// });

			if (isSelected) {
				setBackgroundColor(table.getSelectionBackground());
			} else {
				setBackgroundColor(Color.WHITE);
			}

			return this;
		}

		@SuppressWarnings("unchecked")
		private void addSize(JTable table, int row, int column, int height) {
			Map rows = (Map) cellSizes.get(table);
			if (rows == null) {
				cellSizes.put(table, rows = new HashMap());
			}
			Map rowheights = (Map) rows.get(new Integer(row));
			if (rowheights == null) {
				rows.put(new Integer(row), rowheights = new HashMap());
			}
			rowheights.put(new Integer(column), new Integer(height));
		}

		@SuppressWarnings("unchecked")
		private int findTotalMaximumRowSize(JTable table, int row) {
			int maximum_height = 0;
			Enumeration columns = table.getColumnModel().getColumns();
			while (columns.hasMoreElements()) {
				TableColumn tc = (TableColumn) columns.nextElement();
				TableCellRenderer cellRenderer = tc.getCellRenderer();
				if (cellRenderer instanceof SWRenderer) {
					SWRenderer tar = (SWRenderer) cellRenderer;
					maximum_height = Math.max(maximum_height,
							tar.findMaximumRowSize(table, row));
				}
			}

			return maximum_height;
		}

		@SuppressWarnings("unchecked")
		private int findMaximumRowSize(JTable table, int row) {
			Map rows = (Map) cellSizes.get(table);
			if (rows == null)
				return 0;
			Map rowheights = (Map) rows.get(new Integer(row));
			if (rowheights == null)
				return 0;
			int maximum_height = 0;
			for (Iterator it = rowheights.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				int cellHeight = ((Integer) entry.getValue()).intValue();
				maximum_height = Math.max(maximum_height, cellHeight);
			}
			return maximum_height;
		}

	}

	class StepProgramNoEditor extends DefaultCellEditor {
		private JTextField stepProgramNoField = null;
		private JTable editingTable = null;
		private int editingRow = -1;
		private int editingColumn = -1;

		StepProgramNoEditor(JTextField textField) {
			super(textField);
			this.stepProgramNoField = textField;
			this.stepProgramNoField.setHorizontalAlignment(0);
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			this.editingTable = table;
			this.editingRow = row;
			this.editingColumn = column;
			this.stepProgramNoField.setText("");

			if ((value != null) && ((value instanceof String))) {
				this.stepProgramNoField.setText(value.toString());
			}
			return this.stepProgramNoField;
		}

		public Object getCellEditorValue() {
			String text = this.stepProgramNoField.getText();
			if (this.editingTable != null) {
				if ((this.editingRow >= 0)
						&& (this.editingRow < this.editingTable.getRowCount())
						&& (this.editingColumn >= 0)
						&& (this.editingColumn < this.editingTable.getColumnCount())) {
					this.editingTable.setValueAt(text, this.editingRow, this.editingColumn);
					for (int i = 0; i < this.editingTable.getRowCount(); i++) {
						String temp = (String) this.editingTable.getValueAt(i, 11);
						String paceID = (String) this.editingTable.getValueAt(i, 12);
						if ((temp != null) && (paceID != null)
								&& (PaceTablePane_View.this.pacesCashe.get(paceID) != null)) {
							Element pace = (Element) PaceTablePane_View.this.pacesCashe.get(paceID);
							XmlUtility.setAttributeValue(pace, "programNo", temp);
						}
					}
				}
			}

			return text;
		}
	}

	class StepNumberEditor extends DefaultCellEditor {
		private JTable editingTable = null;
		private int editingRow = -1;
		private int editingColumn = -1;
		private JTextField stepNumberField = null;

		StepNumberEditor(JTextField textField) {
			super(textField);
			this.stepNumberField = textField;
			this.stepNumberField.setHorizontalAlignment(0);
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			this.editingTable = table;
			this.editingRow = row;
			this.editingColumn = column;
			this.stepNumberField.setText("");
			if ((value != null) && ((value instanceof String))) {
				this.stepNumberField.setText(value.toString());
			}
			return this.stepNumberField;
		}

		public boolean check(String text, int row) {
			boolean flag = false;
			if (text != null) {
				if ((text.length() > 1) && (!text.startsWith("-"))) {
					flag = true;
				}
				if ((text.equals("A")) || (text.equals("B"))) {
					if (this.editingTable != null) {
						if ((this.editingRow >= 0)
								&& (this.editingRow < this.editingTable
										.getRowCount())
								&& (this.editingColumn >= 0)
								&& (this.editingColumn < this.editingTable
										.getColumnCount())) {
							for (int i = 0; i < this.editingTable.getRowCount(); i++) {
								if (i != row) {
									String temp = (String) this.editingTable
											.getValueAt(i, 1);
									if ((temp != null) && (temp.equals(text))) {
										flag = true;
										break;
									}
								}
							}
						}
					}
				}
			}
			return flag;
		}

		public Object getCellEditorValue() {
			String text = this.stepNumberField.getText();
			if (text == null)
				text = "";
			text = text.replaceAll("　", "").trim();
			if (text.trim().length() > 0) {
				text = XmlUtility.toSemiangle(text);
			}
			if (this.editingTable != null) {
				if ((this.editingRow >= 0)
						&& (this.editingRow < this.editingTable.getRowCount())
						&& (this.editingColumn >= 0)
						&& (this.editingColumn < this.editingTable
								.getColumnCount())) {
					if (check(text, this.editingRow)) {
						JOptionPane.showMessageDialog(
								PaceTablePane_View.this.frame,
								"工步号重复或不合法，请重新输入！", "提示", 1);
						text = "0";
					}
					this.editingTable.setValueAt(text, this.editingRow,
							this.editingColumn);

					int max = 1;
					for (int i = 0; i < this.editingTable.getRowCount(); i++) {
						String temp = (String) this.editingTable.getValueAt(i,
								1);
						String paceID = (String) this.editingTable.getValueAt(
								i, 9);
						if ((temp != null)
								&& (paceID != null)
								&& (PaceTablePane_View.this.pacesCashe
										.get(paceID) != null)) {
							Element pace = (Element) PaceTablePane_View.this.pacesCashe
									.get(paceID);
							// if ((!temp.equals("A")) && (!temp.equals("B"))) {
							// if (temp.equals("检")) {
							// XmlUtility.setAttributeValue(pace,
							// "procedureType", "procedureType");
							// } else {
							// String num = String.valueOf(max++);
							// this.editingTable.setValueAt("-" + num, i,
							// 1);
							// temp = "-" + num;
							// }
							// }
							XmlUtility.setAttributeValue(pace, "stepNumber",
									temp);
							if (i == this.editingRow) {
								text = temp;
							}
						}
					}
				}
			}
			return text;
		}

		public int getClickCountToStart() {
			return 1;
		}
	}

	class WorkSpaceEditor extends DefaultCellEditor {
		JPanel panel = null;
		JComboBox combo = null;
		private JTable editingTable = null;
		private Map paceWorkType = null;
		private int editingRow = -1;
		private int editingColumn = -1;

		WorkSpaceEditor(JComboBox box) {
			super(box);
			this.combo = box;
			this.panel = new JPanel();
			this.panel.setLayout(new GridBagLayout());
			this.panel.add(this.combo, new GridBagConstraints(0, 0, 1, 1, 0.0D,
					0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
		}

		public String getKey(Map map, String value) {
			if ((map == null) || (value == null))
				return "NOKEY";
			Iterator it = map.keySet().iterator();
			while (it.hasNext()) {
				Object key = it.next();
				Object temp = map.get(key);
				if (temp.equals(value)) {
					return key.toString();
				}
			}
			return "NOKEY";
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			this.editingTable = table;
			this.editingRow = row;
			this.editingColumn = column;
			int width = table.getColumnModel().getColumn(column).getWidth();
			this.combo.setSelectedItem("");
			this.combo.setMaximumSize(new Dimension(width, 24));
			this.combo.setMinimumSize(new Dimension(width, 24));
			this.combo.setPreferredSize(new Dimension(width, 24));
			return this.panel;
		}

		public Object getCellEditorValue() {
			Object result = this.combo.getSelectedItem();
			if (result == null)
				result = "";
			if (this.editingTable != null) {
				if ((this.editingRow >= 0)
						&& (this.editingRow < this.editingTable.getRowCount())
						&& (this.editingColumn >= 0)
						&& (this.editingColumn < this.editingTable
								.getColumnCount())) {
					this.editingTable.setValueAt(result, this.editingRow,
							this.editingColumn);
					String bsoID = (String) this.editingTable.getValueAt(
							this.editingRow, 9);
					if (bsoID != null) {
						Element pace = (Element) PaceTablePane_View.this.pacesCashe
								.get(bsoID);
						if (pace != null) {
							XmlUtility.setAttributeValue(pace, "workType",
									result.toString());
							if (this.paceWorkType != null) {
								XmlUtility.setAttributeValue(
										pace,
										"workTypeID",
										getKey(this.paceWorkType,
												result.toString()));
							}
						}
					}
				}
			}
			return result;
		}

		public int getClickCountToStart() {
			return 1;
		}
	}

	public String getImageFolder() {
		return imageFolder;
	}

	public void setImageFolder(String imageFolder) {
		this.imageFolder = imageFolder;
	}
}
