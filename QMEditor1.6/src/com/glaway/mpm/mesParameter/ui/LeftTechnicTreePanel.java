package com.glaway.mpm.mesParameter.ui;

import com.glaway.mpm.mesParameter.helper.MesParameterProcessor;
import com.glaway.mpm.mesParameter.model.GLZhuFuLink;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.*;
import com.glaway.mpm.visual.log.VaLogger;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.*;
import java.util.List;

public class LeftTechnicTreePanel extends JPanel implements TreeSelectionListener {

	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(NewTechnicsPart.class);

	private JScrollPane scrollPane;
	private JTree mesTree;
	private String productNumber;
	private String technicNumber;
	private String fzTechnicNumber;
	private String fzVersion;
	private String objType;
	private String objNumber;
	private String lukahao;
	private String gxPK;
	private String jianyanyuan;
	private String caozuoyuan;
	private String state;
	private Document document;
	private String isZF;
	private String bsoID;
	private String version;

	public LeftTechnicTreePanel() {
		initComponent();
	}

	private void initComponent() {
		scrollPane = new JScrollPane();
		scrollPane.setBorder(null);

		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
	}

	public void loadMesTree(Document document, String technicNumber, String fzTechnicNumber, String fzVersion, String produreNumber, String productNumber, String lukahao, String gxPK,
			String jianyanyuan, String caozuoyuan) {
		this.technicNumber = technicNumber;
		this.fzTechnicNumber = fzTechnicNumber;
		this.fzVersion = fzVersion;
		this.productNumber = productNumber;
		this.lukahao = lukahao;
		this.gxPK = gxPK;
		this.jianyanyuan = jianyanyuan;
		this.caozuoyuan = caozuoyuan;
		this.document = document;
		Element technicElement = XmlUtility.getTechnicsElement(document);
		this.version = technicElement.attributeValue("version");
		XWTreeNode technicTreeNode = null;
		if (technicNumber.contains("_ZF")) {
			isZF = "Y";
			XWMesTechnicsTreeObject technicTreeObject = new XWMesTechnicsTreeObject(document);
			technicTreeNode = new XWTreeNode(technicTreeObject);
			String number = technicNumber.substring(0, technicNumber.indexOf("_ZF"));
			List<XWTreeNode> newNodeList = createNewTreeNode(number, technicTreeNode.getAllSteps());
			List<String> displayNames = new ArrayList<String>();
			for (XWTreeNode newNode : newNodeList) {
				technicTreeNode.addChild(newNode.getObject());
				String displayName = newNode.getDisplayName();
				String zfType = newNode.getObject().getTreeCellData().getParent().getParent().attributeValue("ZFFLAG");
				String ppNum = displayName.split(" ")[1];
				if ("F".equals(zfType)) {
					ppNum = ppNum.substring(ppNum.lastIndexOf("F"), ppNum.length());
					if (!displayNames.contains(ppNum)) {
						displayNames.add(ppNum);
					}
				}
			}
			String ppNumber = "";
			for (String str : displayNames) {
				ppNumber = ppNumber + "、" + str;
			}
			technicTreeObject.setDisplayName(ppNumber);
			technicTreeNode.expandAllZFSteps(technicTreeNode);
		} else {
			isZF = "N";
			XWTechnicsTreeObject technicTreeObject = new XWTechnicsTreeObject(document);
			technicTreeNode = new XWTreeNode(technicTreeObject);
			List<XWTreeNode> nodeList = technicTreeNode.getAllSteps();
			for (XWTreeNode node : nodeList) {
				technicTreeNode.addChild(node.getObject());
			}
			technicTreeNode.expandAllSteps();
		}

		mesTree = new JTree(technicTreeNode);
		mesTree.setCellRenderer(new MESTypeCellRenderer());
		mesTree.setRootVisible(true);
		mesTree.updateUI();
		scrollPane.setViewportView(mesTree);

		mesTree.addTreeSelectionListener(this);
		setSelectedTreeNode(technicTreeNode, produreNumber, fzTechnicNumber);
		scrollPane.setHorizontalScrollBarPolicy(30);
		scrollPane.setVerticalScrollBarPolicy(20);
		scrollPane.getViewport().updateUI();
	}

	public void setSelectedTreeNode(XWTreeNode treeNode, String currentStepNumber, String currentFzTechnicNumber) {
		for (int i = 0; i < treeNode.getChildCount(); i++) {
			XWTreeNode childNode = (XWTreeNode) treeNode.getChildAt(i);
			String stepNumber = childNode.getDisplayName();
			stepNumber = stepNumber.substring(0, stepNumber.indexOf("_"));
			if (stepNumber != null && stepNumber.equals(currentStepNumber)) {
				mesTree.setSelectionRow(i + 1);
			}
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		Element technicElement = XmlUtility.getTechnicsElement(document);
		state = technicElement.attributeValue("lifecycle");
		XWTreeNode node = getSelectedTreeNode();
		Map<String, String> paramsMap = null;
		if (node != null) {
			XWTreeObject xo = node.getObject();
			if (xo != null) {
				if (xo instanceof XWMesStepTreeObject || xo instanceof XWStepTreeObject) {
					technicNumber = xo.getTreeCellData().getParent().getParent().attributeValue("technicsNumber");
					version = xo.getTreeCellData().getParent().getParent().attributeValue("version");
					objType = "工序";
					Element objElement = xo.getTreeCellData();
					objNumber = objElement.attributeValue("stepNumber");
					bsoID = objElement.attributeValue("bsoID");
					String produreName = objElement.attributeValue("stepName");
					String newNumber = node.getDisplayName();
					newNumber = newNumber.substring(0, newNumber.indexOf("_"));
					paramsMap = new HashMap<String, String>();
					paramsMap.put("technicsNumber", technicNumber);
					paramsMap.put("objType", objType);
					paramsMap.put("objNumber", objNumber);
					paramsMap.put("lukahao", lukahao);
					paramsMap.put("isZF", isZF);
					paramsMap.put("bsoId", bsoID);
					paramsMap.put("version", version);
					String defaultValues = MesParameterProcessor.getDefaultValues(paramsMap);
					String mainNumber = MesParameterMainFrame.getProdureNumber();
					if(!newNumber.equals(mainNumber)){
						MesParameterMainFrame.getMesParameterMainPanel().getSaveButton().setEnabled(false);
					}else{
						MesParameterMainFrame.getMesParameterMainPanel().getSaveButton().setEnabled(true);
					}
					MesParameterMainFrame.getMesParameterMainPanel().updateUI();
					MesParameterMainFrame.getMesParameterMainPanel().getProcessNumberBox().setEditText(defaultValues);
					MesParameterMainFrame.getMesParameterMainPanel().getProcessNumberBox().setDefaultValue(getDefaultValueList(defaultValues));
					MesParameterMainFrame.getMesParameterMainPanel().getProcedureNameField().setText(produreName);
					MesParameterMainFrame.getMesParameterMainPanel().getProcedureNumberField().setText(newNumber);
					MesParameterMainFrame.getMesParameterMainPanel().getRightTechnicInfoPanel().initUIValues();
				} else if (xo instanceof XWMesPaceTreeObject || xo instanceof XWPaceTreeObject) {
					technicNumber = xo.getTreeCellData().getParent().getParent().getParent().getParent().attributeValue("technicsNumber");
					version = xo.getTreeCellData().getParent().getParent().getParent().getParent().attributeValue("version");
					objType = "工步";
					Element objElement = xo.getTreeCellData();
					Element parentElement = objElement.getParent().getParent();
					String produreNumber = parentElement.attributeValue("stepNumber");
					String produreName = parentElement.attributeValue("stepName");
					String newNumber = node.getP().getDisplayName();
					newNumber = newNumber.substring(0, newNumber.indexOf("_"));
					objNumber = produreNumber + "-" + objElement.attributeValue("stepNumber");
					bsoID = parentElement.attributeValue("bsoID") + objElement.attributeValue("bsoID");
					paramsMap = new HashMap<String, String>();
					paramsMap.put("technicsNumber", technicNumber);
					paramsMap.put("objType", objType);
					paramsMap.put("objNumber", objNumber);
					paramsMap.put("lukahao", lukahao);
					paramsMap.put("isZF", isZF);
					paramsMap.put("bsoId", bsoID);
					paramsMap.put("version", version);
					String mainNumber = MesParameterMainFrame.getProdureNumber();
					if(!newNumber.equals(mainNumber)){
						MesParameterMainFrame.getMesParameterMainPanel().getSaveButton().setEnabled(false);
					}else{
						MesParameterMainFrame.getMesParameterMainPanel().getSaveButton().setEnabled(true);
					}
					String defaultValues = MesParameterProcessor.getDefaultValues(paramsMap);
					MesParameterMainFrame.getMesParameterMainPanel().getProcessNumberBox().setEditText(defaultValues);
					MesParameterMainFrame.getMesParameterMainPanel().getProcessNumberBox().setDefaultValue(getDefaultValueList(defaultValues));

					MesParameterMainFrame.getMesParameterMainPanel().getProcedureNameField().setText(produreName);
					MesParameterMainFrame.getMesParameterMainPanel().getProcedureNumberField().setText(newNumber);
					MesParameterMainFrame.getMesParameterMainPanel().getRightTechnicInfoPanel().initUIValues();
				}
			}
		}
	}

	public XWTreeNode getSelectedTreeNode() {
		if (this.mesTree == null)
			return null;
		if (this.mesTree.getSelectionCount() <= 0)
			return null;
		TreePath path = this.mesTree.getSelectionPath();
		Object obj = path.getLastPathComponent();
		if ((obj instanceof XWTreeNode)) {
			XWTreeNode node = (XWTreeNode) obj;
			return node;
		}
		return null;
	}

	/**
	 * 根据工艺文件编号，版本获得辅制工艺的工序节点集合
	 *
	 * @param technicNumber
	 * @param version
	 * @return
	 */
	public Map<String, XWTreeNode> getTechnicsNodes(String technicNumber, String version, Map<String, Document> fzDocumentMap) {
		Map<String, XWTreeNode> nodeMap = new HashMap<String, XWTreeNode>();
		XWMesTechnicsTreeObject technicTreeObject = new XWMesTechnicsTreeObject(fzDocumentMap.get(technicNumber));
		XWTreeNode technicTreeNode = new XWTreeNode(technicTreeObject);
		List<XWTreeNode> nodeList = technicTreeNode.getAllSteps();
		for (XWTreeNode node : nodeList) {
			String stepNumber = node.getDisplayName().substring(0, node.getDisplayName().indexOf("_"));
			nodeMap.put(stepNumber, node);
		}
		return nodeMap;
	}

	/**
	 * 生成主辅关联新的工艺树
	 *
	 * @param technicNumber
	 * @param nodeList
	 * @return
	 */
	public List<XWTreeNode> createNewTreeNode(String technicNumber, List<XWTreeNode> nodeList) {
		List<XWTreeNode> newNodeList = new ArrayList<XWTreeNode>();
		long searchZhufuLinkStart = System.currentTimeMillis();
		List<String[]> fzTechnicsList = MesParameterProcessor.getFzTechnicsNumberList(technicNumber);
		Map<String, Document> fzDocumentMap = new HashMap<String, Document>();
		for (String[] fzTechnics : fzTechnicsList) {
			String fzTechnicsNumber = fzTechnics[0];
			String fzTechnicsVersion = fzTechnics[1];
			Vector<Object> result = MesParameterProcessor.getTechnicDocumentByNumberAndVersion(fzTechnicsNumber, fzTechnicsVersion);
			if(result != null){
				Document doc = MesParameterProcessor.downloadData(result);
				fzDocumentMap.put(fzTechnicsNumber, doc);
			}
		}
		Map<String, List<GLZhuFuLink>> linksMap = (Map<String, List<GLZhuFuLink>>) MesParameterProcessor.getZFTechnics(technicNumber)[2];
		long searchZhufuLinkEnd = System.currentTimeMillis();
		System.out.println("SearchZhuFuLink   Time" + (searchZhufuLinkEnd - searchZhufuLinkStart) + "ms");
		int newNumber = 10;
		long createNodesStart = System.currentTimeMillis();
		for (XWTreeNode node : nodeList) {
			String displayName = node.getDisplayName();
			String stepNumber = displayName.substring(0, displayName.indexOf("_"));
			String stepName = displayName.substring(displayName.indexOf("_") + 1, displayName.length());
			List<GLZhuFuLink> mapValue = linksMap.get(stepNumber);
			if (mapValue != null && mapValue.size() > 0) {
				for (GLZhuFuLink link : mapValue) {
					String fzTechnicNumber = link.getFztechnicsnumber();
					String fzVersion = link.getFztechnicsversion();
					String fzStepNumber = link.getFzprocedurenumber();
					fzStepNumber = fzStepNumber.substring(0, fzStepNumber.indexOf("_"));
					Map<String, XWTreeNode> fzNodeMap = getTechnicsNodes(fzTechnicNumber, fzVersion, fzDocumentMap);
					XWTreeNode fzXWTreeNode = fzNodeMap.get(fzStepNumber);
					XWMesStepTreeObject stepObject = (XWMesStepTreeObject) fzXWTreeNode.getObject();
					stepObject.setDisplaName(String.valueOf(newNumber), fzStepNumber);
					newNodeList.add(fzXWTreeNode);
					newNumber += 10;
				}
			} else {
				XWMesStepTreeObject stepObject = (XWMesStepTreeObject) node.getObject();
				stepObject.setDisplaName(String.valueOf(newNumber), stepNumber);
				newNodeList.add(node);
				newNumber += 10;
			}
		}
		long createNodesEnd = System.currentTimeMillis();
		System.out.println("CreateZhuFuTree    Time：" + (createNodesEnd - createNodesStart) + "ms");
		return newNodeList;
	}
	public static List<String> getDefaultValueList(String defaultValue){
		List<String> defaultValueList = new ArrayList<String>();
		if(defaultValue != null){
			String[] values = defaultValue.split(",");
			for(String str : values){
				defaultValueList.add(str);
			}
		}
		return defaultValueList;
	}

	public String getTechnicNumber() {
		return technicNumber;
	}

	public String getObjType() {
		return objType;
	}

	public String getObjNumber() {
		return objNumber;
	}

	public String getState() {
		return state;
	}

	public JTree getMesTree() {
		return mesTree;
	}

	public Document getDocument() {
		return document;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public String getLukahao() {
		return lukahao;
	}

	public String getGxPK() {
		return gxPK;
	}

	public String getJianyanyuan() {
		return jianyanyuan;
	}

	public String getCaozuoyuan() {
		return caozuoyuan;
	}

	public String getIsZF() {
		return isZF;
	}

	public String getBsoID(){
		return bsoID;
	}

	public String getVersion(){
		return version;
	}

}
