package com.glaway.mpm.view;

import com.glaway.mpm.EditorConfig;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpPartNode;
import com.glaway.mpm.qmIntf.participatePart.ParticipatePartAddDialog;
import com.glaway.mpm.util.BomXMLUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.visual.bean.VaEPartInstance;
import com.glaway.mpm.visual.bean.VaLightPart;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.*;

public class NewPartJPanel extends NewLinkJPanel {
	private JFrame parentFrame;
	private NewTechnicsPart frame;
	String title = "";

	private JButton toolJButton = new IconButton("/images/button_pic_review.png", "装配工具");
	private JButton zpButton = new IconButton("/images/button_pic_review.png", "装配定额参装");
	private JButton pbomButton = new IconButton("/images/button_pic_review.png", "PBOM参装");
	private JButton refreshButton = new IconButton("/images/refresh.png", "刷新");

	public NewPartJPanel(JFrame parentpanel, JFrame frame) {
		super(parentpanel);
		this.parentFrame = parentpanel;
		if(frame instanceof NewTechnicsPart){
			this.frame = (NewTechnicsPart) frame;
		}
		initOneself();
		super.setHiddenColumn(7);
		super.setHiddenColumn(8);
	}

	public NewPartJPanel(JPanel parentpanel, JFrame frame) {
		super(parentpanel);
		if(frame instanceof NewTechnicsPart){
			this.parentFrame = frame;
			this.frame = (NewTechnicsPart) frame;
		}
		initOneself();
		super.setHiddenColumn(7);
		super.setHiddenColumn(8);
	}

	protected void initOneself() {
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(0).setMinWidth(200);
		table.getColumnModel().getColumn(0).setMaxWidth(Integer.MAX_VALUE);

		zpButton.setMaximumSize(new Dimension(130, 23));
		zpButton.setMinimumSize(new Dimension(130, 23));
		zpButton.setPreferredSize(new Dimension(130, 23));
		zpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zpProcess();
			}
		});
		pbomButton.setMaximumSize(new Dimension(130, 23));
		pbomButton.setMinimumSize(new Dimension(130, 23));
		pbomButton.setPreferredSize(new Dimension(130, 23));
		pbomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pbomProcess();
			}
		});
		refreshButton.setMaximumSize(new Dimension(130, 23));
		refreshButton.setMinimumSize(new Dimension(130, 23));
		refreshButton.setPreferredSize(new Dimension(130, 23));
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
		removeJButton.setMaximumSize(new Dimension(130, 23));
		removeJButton.setMinimumSize(new Dimension(130, 23));
		removeJButton.setPreferredSize(new Dimension(130, 23));
		removeJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remove();
			}
		});
		panel.add(zpButton, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 0, 5), 0, 0));
		panel.add(pbomButton, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 0, 5), 0, 0));
		panel.add(refreshButton, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 0, 5), 0, 0));

		toolJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("启动可视化装配工具");
				try {
					if (frame != null && frame instanceof NewTechnicsPart) {
						NewTechnicsPart np = (NewTechnicsPart) frame;

						Document doc = np.getCurrentTechnics();
						if (doc != null) {
							Element tech = XmlUtility.getTechnicsElement(doc);
							String oid = tech.attributeValue("parentPartOid");
							String partNumber = tech.attributeValue("parentPartNumber");
							String path = WorkSpaceUtil.getTechnicsPath(tech.attributeValue("technicsNumber"));
							String occId = tech.attributeValue("occId");
							String partName = tech.attributeValue("partName");
							String material = tech.attributeValue("material");
							String dutu = tech.attributeValue("dutu");
							String remark = tech.attributeValue("remark");
							String useCount = tech.attributeValue("useCount");
							Map map = new HashMap();
							map.put("poid", oid);
							map.put("partNumber", partNumber);
							map.put("xmlPath", path);
							map.put("occId", occId);
							map.put("partName", partName);
							map.put("material", material);
							map.put("dutu", dutu);
							map.put("remark", remark);
							map.put("useCount", useCount);
							System.out.println("启动可视化装配工具,复制粘贴零部件========" + map);
							ParticipatePartAddDialog.showDialog(map, np);
							// VaStarter.startAssembler(partNumber);
						}
					}
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		});

		addJButton.setVisible(false);
		upJButton.setVisible(false);
		downJButton.setVisible(false);
//		if(EditorConfig.isZS && !EditorConfig.ACL_POSITIVE.contains(NewTechnicsPart.currentUser)){
			zpButton.setVisible(true);
			pbomButton.setVisible(true);
			refreshButton.setVisible(true);
			removeJButton.setVisible(true);
			upJButton.setVisible(false);
			downJButton.setVisible(false);
//		}
	}

	public void setTabTitle() {
		int i = tableModel.getRowCount();
		if (i > 0) {
			if (parentPanel instanceof TechnicsStepJPanel_XW) {
				JTabbedPane tabbedPane = ((TechnicsStepJPanel_XW) parentPanel).getTabbedPane();
				if(tabbedPane.getTabCount()>16){
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(16, "参装件" + "(" + i + ")");
				}
			} else if (parentPanel instanceof TechnicsPaceJDialog) {
				if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(3, "参装件" + "(" + i + ")");
				}
			} else if (parentPanel instanceof TechnicsStepJPanel_View) {
					((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(3, "参装件" + "(" + i + ")");
			}
		} else {
			if(parentPanel instanceof TechnicsStepJPanel_XW) {
				JTabbedPane tabbedPane = ((TechnicsStepJPanel_XW) parentPanel).getTabbedPane();
				if(tabbedPane.getTabCount()>16){
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(16, "参装件");
				}
			} else if(parentPanel instanceof TechnicsPaceJDialog) {
				if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)) {
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(3, "参装件");
				}
			} else if(parentPanel instanceof TechnicsStepJPanel_View) {
				((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(3, "参装件");
			}
		}
	}

	protected void addModelColumn() {
		tableModel.addColumn("零部件编号");
		tableModel.addColumn("零部件名称");
		tableModel.addColumn("型号牌号");
		tableModel.addColumn("规格");
		tableModel.addColumn("技术条件");
		tableModel.addColumn("备注");
		tableModel.addColumn("使用数量");
		tableModel.addColumn("oid");
		tableModel.addColumn("occId");
	}

	public Vector<Element> getElements() {
		stopTableCellEditing();
		Vector<Element> elements = new Vector<Element>();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if (isRowNull(i))
				continue;
			Element element = XmlUtility.createPart();
			XmlUtility.setAttributeValue(element, "partNumber",
					(String) tableModel.getValueAt(i, 0));
			XmlUtility.setAttributeValue(element, "partName",
					(String) tableModel.getValueAt(i, 1));
			XmlUtility.setAttributeValue(element, "XHPH",
					(String) tableModel.getValueAt(i, 2));
			XmlUtility.setAttributeValue(element, "GG",
					(String) tableModel.getValueAt(i, 3));
			XmlUtility.setAttributeValue(element, "JSTJ",
					(String) tableModel.getValueAt(i, 4));
			XmlUtility.setAttributeValue(element, "remark",
					(String) tableModel.getValueAt(i, 5));
			XmlUtility.setAttributeValue(element, "useCount",
					(String) tableModel.getValueAt(i, 6));
			XmlUtility.setAttributeValue(element, "oid",
					(String) tableModel.getValueAt(i, 7));
			XmlUtility.setAttributeValue(element, "occId",
					(String) tableModel.getValueAt(i, 8));
			elements.add(element);
		}
		return elements;
	}

	public void setTableValues(Vector<Element> vec) {
		clearTable();
		for (int i = 0; i < vec.size(); i++) {
			Element element = vec.get(i);
			setOneRowTableValue(element);
		}
		setTabTitle();
	}

	public void setOneRowTableValue(Element element) {
		if (element != null && element.getName().equals(XmlUtility.PART_TAG)
				&& isAdd(element)) {
			addProcess();
			int i = tableModel.getRowCount();
			i--;
			tableModel.setValueAt(element.attributeValue("partNumber"), i, 0);
			tableModel.setValueAt(element.attributeValue("partName"), i, 1);
			tableModel.setValueAt(element.attributeValue("XHPH"), i, 2);
			tableModel.setValueAt(element.attributeValue("GG"), i, 3);
			tableModel.setValueAt(element.attributeValue("JSTJ"), i, 4);
			tableModel.setValueAt(element.attributeValue("remark"), i, 5);
			tableModel.setValueAt(element.attributeValue("useCount"), i, 6);
			tableModel.setValueAt(element.attributeValue("oid"), i, 7);
			tableModel.setValueAt(element.attributeValue("occId"), i, 8);
		}
	}

	public boolean isAdd(Element partElement) {
		for (int i = 0; i < table.getRowCount(); i++) {
			String number = String.valueOf(table.getValueAt(i, 0));
			if (partElement.attributeValue("partNumber").equals(number))
				return false;
		}
		return true;
	}

	public void setUIEnabled(boolean b) {
		super.setUIEnabled(b);
		toolJButton.setEnabled(b);
		zpButton.setEnabled(b);
		pbomButton.setEnabled(b);
		refreshButton.setEnabled(b);
	}

	// 参装件通过复制粘贴的方式来添加，所以此方法不必实现
	public void update(Observable o, Object arg) {

	}

	private void zpProcess(){
		if (parentFrame instanceof NewTechnicsPart){
			TechnicsStepJPanel_XW stepPanel = frame.getTechnicsStepJPanel();
			Element stepElement = stepPanel.getStepElement();
			List<Element> paces = XmlUtility.getAllPaces(stepElement);
			if(paces != null && paces.size() > 0) {
				JOptionPane.showMessageDialog(null, "该工序下有工步，无法进行参装！", "提示", JOptionPane.OK_OPTION);
				return;
			}
		}

		org.dom4j.Document doc = frame.getCurrentTechnics();
		Element techElement = XmlUtility.getTechnicsElement(doc);
		String technicsNumber = techElement.attributeValue("technicsNumber");
		title = "【" + technicsNumber + "】" + "装配工艺定额";

		//判断是否已打开一个定额窗口
		boolean isHas = GwAssembQuotaDialog.isHas();
		if(isHas){
			return;
		}

		final VaActionProgressBar progressBar = new VaActionProgressBar(frame, "装配工艺定额", "正在加载数据,请等待...", "数据加载中");
		Thread thread = new Thread() {
			public void run() {
				GwAssembQuotaDialog.getInstance(parentFrame,frame,title,frame.getTechnicsTreePanel().getCurrentTechnicsNode());
				progressBar.setHeaderMessage("数据加载完成！");
				progressBar.finish();
				progressBar.setVisible(false);
			}
		};
		thread.start();
		progressBar.setVisible(true);
	}

	private void pbomProcess(){
		if (parentFrame instanceof NewTechnicsPart){
			TechnicsStepJPanel_XW stepPanel = frame.getTechnicsStepJPanel();
			Element stepElement = stepPanel.getStepElement();
			List<Element> paces = XmlUtility.getAllPaces(stepElement);
			if(paces != null && paces.size() > 0) {
				JOptionPane.showMessageDialog(null, "该工序下有工步，无法进行参装！", "提示", JOptionPane.OK_OPTION);
				return;
			}
		}

		GwPbomQuotaDialog dia = new GwPbomQuotaDialog(parentFrame,this);
		Vector<VaTreeNode> vector = dia.showDialog();
		if(vector != null) {
			addData(vector);
		}
	}

	private void refresh(){
		//刷新参装件列表
		if(parentFrame instanceof TechnicsPaceJDialog){
			TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentFrame;
			paceJDialog.refreshPartDatas();
			PaceTablePane paceTablePane = paceJDialog.getPaceTablePane();
			if(paceTablePane != null){
				String bsoID = paceJDialog.getPaceElement().attributeValue("bsoID");
				paceTablePane.addPartValues(bsoID);
			}
		}else if (parentFrame instanceof NewTechnicsPart){
			TechnicsStepJPanel_XW stepPanel = frame.getTechnicsStepJPanel();
			stepPanel.refreshPartDatas();
		}
	}

	private void remove(){
		int[] rows = table.getSelectedRows();
		for(int i = 0; i < rows.length; i++) {
			String partNumber = (String) tableModel.getValueAt(rows[i], 0);
			Element parentEle = null;
			Element techEle = null;
			if(parentFrame instanceof TechnicsPaceJDialog){
				TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentFrame;
				parentEle = paceJDialog.paceElement;
				techEle = paceJDialog.getStepElement().getParent().getParent();
			}else if (parentFrame instanceof NewTechnicsPart){
				TechnicsStepJPanel_XW stepPanel = frame.getTechnicsStepJPanel();
				parentEle = stepPanel.getStepElement();
				techEle = parentEle.getParent().getParent();
			}
			Element parts = XmlUtility.getParts(parentEle);
			if(parts != null) {
				List<Element> elements = parts.elements();
				if(elements != null && elements.size()>0) {
					for(Element element : elements) {
						String number = element.attributeValue("partNumber");
						if(partNumber.equals(number)){
							elements.remove(element);
							break;
						}
					}
				}
			}
			frame.saveProcess(techEle);

			tableModel.removeRow(rows[i]);
			for(int j = i + 1; j < rows.length; j++) {
				rows[j] = rows[j] - 1;
			}
		}
		table.updateUI();
		setTabTitle();
	}

	private void addData(Vector<VaTreeNode> vector) {
		Element parentEle = null;
		Element techEle = null;
		if(parentFrame instanceof TechnicsPaceJDialog){
			TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentFrame;
			parentEle = paceJDialog.paceElement;
			techEle = paceJDialog.getStepElement().getParent().getParent();
		}else if (parentFrame instanceof NewTechnicsPart){
			TechnicsStepJPanel_XW stepPanel = frame.getTechnicsStepJPanel();
			parentEle = stepPanel.getStepElement();
			techEle = parentEle.getParent().getParent();
		}
		for(VaTreeNode vaTreeNode : vector) {
			DpPartNode dpPartNode = covertTreeNodeToPartNode(vaTreeNode);
			Map<String, String> partMap = generalNodeMap(vaTreeNode,dpPartNode);
			Element part = BomXMLUtil.generatePartData(partMap, null);
			String partNumber = part.attributeValue("partNumber");
			double newSl = Double.valueOf(part.attributeValue("useCount"));
			Element parts = XmlUtility.getParts(parentEle);
			if(parts != null) {
				List<Element> elements = parts.elements();
				if(elements != null && elements.size()>0) {
					for(Element element : elements) {
						String number = element.attributeValue("partNumber");
						if(partNumber.equals(number)){
							double useCount = Double.valueOf(element.attributeValue("useCount"));
							newSl = useCount + newSl;
							part.setAttributeValue("useCount",String.valueOf(newSl));
							elements.remove(element);
							break;
						}
					}
				}
			}
			parentEle.element("parts").add(part);
		}
		frame.saveProcess(techEle);

		//刷新参装件列表
		refresh();
	}

	public JFrame getParentFrame() {
		return parentFrame;
	}

	public NewTechnicsPart getFrame() {
		return frame;
	}

	/**
	 * 将null或者字符串null转为string的空值
	 *
	 * @param obj
	 * @return
	 */
	public String convertToString(Object obj) {
		if (obj == null) {
			return "";
		} else {
			return String.valueOf(obj);
		}
	}

	private DpPartNode covertTreeNodeToPartNode(VaTreeNode pauseNode) {
		VaLightPart vaLightPart = (VaLightPart) pauseNode.getPart().clone();
		VaTreeNode treenode = new VaTreeNode(new VaEPartInstance(vaLightPart));
		DpPartNode partNode = new DpPartNode(treenode.getUserObject());
		partNode.set_cadObject(pauseNode.get_cadObject());
		partNode.set_index(pauseNode.get_index());
		partNode.set_LockedForCAD(pauseNode.get_LockedForCAD());
		partNode.set_pviewComponentInstance(pauseNode.get_pviewComponentInstance());
		partNode.set_pviewComponentNode(pauseNode.get_pviewComponentNode());
		partNode.set_pviewShapeInstance(pauseNode.get_pviewShapeInstance());
		partNode.set_wsInfo(pauseNode.get_wsInfo());
		partNode.setAllowsChildren(pauseNode.getAllowsChildren());
		partNode.setBaseMatrix(pauseNode.getBaseMatrix());
		partNode.setBboxes(pauseNode.getBboxes());
		partNode.setDiode(pauseNode.getDiode());
		partNode.setFktGrp(pauseNode.getFktGrp());
		partNode.setInfoTxt(pauseNode.getInfoTxt());
		partNode.setInstanceIdentifier(pauseNode.getInstanceIdentifier());
		partNode.setLocChange(pauseNode.getLocChange());
		partNode.setMatrix(pauseNode.getMatrix());
		partNode.setNodeType(pauseNode.getNodeType());
		partNode.setOccId(pauseNode.getOccId());
		partNode.setPathFromCI(pauseNode.getPathFromCI());
		partNode.setRelative_matrix(pauseNode.getRelative_matrix());
		partNode.setRelativeMatrix(pauseNode.getRelativeMatrix());
		partNode.setDataType(pauseNode.getDataType());
		partNode.setBzh(pauseNode.getBzh());
		partNode.getPart().setRootType("TECHNICS");
		return partNode;
	}

	private Map<String, String> generalNodeMap(VaTreeNode vaTreeNode, DpPartNode dpPartNode) {
		Map<String, String> partMap = new HashMap<String, String>();
		partMap.put("partNumber", dpPartNode.getPart().getNumber());
		partMap.put("oid", String.valueOf(dpPartNode.getPart().getOid()));
		partMap.put("occId", dpPartNode.getOccId());
		partMap.put("occpath", dpPartNode.getOccpath());
		partMap.put("partName", dpPartNode.getPart().getName());
		partMap.put("materialNumber",vaTreeNode.getPart().getMaterialNumber());//材料编号
		partMap.put("materialName",vaTreeNode.getPart().getMaterialName());//材料名称
		partMap.put("materialBrand",vaTreeNode.getPart().getMaterialBrand());//材料牌号
		partMap.put("materialCrision",vaTreeNode.getPart().getMaterialCrision());//材料标准号
		partMap.put("material", "");
		partMap.put("dutu", "");
		partMap.put("remark", "");
		partMap.put("useCount", String.valueOf(dpPartNode.getPart().getAmount()));

		partMap.put("MTYPE", dpPartNode.getPart().getMtype());
		String dataType = vaTreeNode.getDataType();
		String wh = vaTreeNode.getWh();
		if(dataType!=null && "元器件".equals(dataType) && wh!=null){
			partMap.put("wh", vaTreeNode.getWh());
		}
		String replaceableParts = vaTreeNode.getReplaceableParts();
		if(replaceableParts!=null){
			partMap.put("replaceableParts", vaTreeNode.getReplaceableParts());
		}
		String adjustable = vaTreeNode.getPart().getAdjustable();
		if(adjustable != null) {
			partMap.put("adjustable", adjustable);
		}
		partMap.put("CSIZE", dpPartNode.getPart().getCsize());
		partMap.put("XHPH", dpPartNode.getPart().getXhph());
		partMap.put("JSTJ", dpPartNode.getPart().getJstj());
		partMap.put("GG", dpPartNode.getPart().getGg());
		partMap.put("DW", vaTreeNode.getDw());
		partMap.put("DW2", vaTreeNode.getDw2());
		partMap.put("bzh", dpPartNode.getBzh());
		partMap.put("dataType", dpPartNode.getDataType());
//		partMap.put("HASEPM", vaTreeNode.isHasEpmDoc() + "");
		if(vaTreeNode.getFlag().contains("SJZYK")){
			partMap.put("materialGg", vaTreeNode.getPart().getGg());//材料规格
			partMap.put("materialLb", vaTreeNode.getPart().getDataType());//材料类别
			partMap.put("materialPh", vaTreeNode.getPart().getCl());//材料牌号
			partMap.put("materialBzh", vaTreeNode.getPart().getBzh());//材料标准号
		}else{
			partMap.put("materialGg", vaTreeNode.getPart().getGg());//材料编号
			partMap.put("materialLb", vaTreeNode.getPart().getDataType());//材料名称
			partMap.put("materialPh", vaTreeNode.getPart().getXhph());//材料牌号
			partMap.put("materialBzh", vaTreeNode.getPart().getJstj());//材料标准号
		}

		if ("C".equals(dpPartNode.getPart().getZcmark())) {
			partMap.put("ZCMARK", "C");
		} else if ("Z".equals(dpPartNode.getPart().getZcmark())) {
			partMap.put("ZCMARK", "Z");
		}
		return partMap;
	}
}