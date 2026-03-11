package com.glaway.mpm.dataPackage.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.dom4j.Document;
import org.dom4j.Element;

import com.glaway.mpm.mesParameter.helper.MesParameterProcessor;
import com.glaway.mpm.mesParameter.model.GLZhuFuLink;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.MESTypeCellRenderer;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWMesPaceTreeObject;
import com.glaway.mpm.view.XWMesStepTreeObject;
import com.glaway.mpm.view.XWMesTechnicsTreeObject;
import com.glaway.mpm.view.XWPaceTreeObject;
import com.glaway.mpm.view.XWStepTreeObject;
import com.glaway.mpm.view.XWTechnicsTreeObject;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.view.XWTreeObject;
import com.glaway.mpm.visual.log.VaLogger;

public class DPLeftTechnicTreePanel extends JPanel implements TreeSelectionListener{

	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(NewTechnicsPart.class);

	private JScrollPane scrollPane;
	private JTree mesTree;
	private String productNumber;
	private String technicNumber;
	private String objType;
	private String objNumber;
	private String lukahao;
	private String gxPK;
	private String jianyanyuan;
	private String caozuoyuan;
	private String state;
	private Document document;

	public DPLeftTechnicTreePanel(){
		initComponent();
	}

	private void initComponent(){
		scrollPane = new JScrollPane();
		scrollPane.setBorder(null);

		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
	}

	public void loadMesTree(Document document, String technicNumber, String produreNumber, String productNumber, String lukahao, String gxPK, String jianyanyuan, String caozuoyuan){
		this.technicNumber = technicNumber;
		this.productNumber = productNumber;
		this.lukahao = lukahao;
		this.gxPK = gxPK;
		this.jianyanyuan = jianyanyuan;
		this.caozuoyuan = caozuoyuan;
		this.document = document;
		XWTreeNode technicTreeNode = null;
		if(technicNumber.contains("_ZF")){
			XWMesTechnicsTreeObject technicTreeObject = new XWMesTechnicsTreeObject(document);
			technicTreeNode = new XWTreeNode(technicTreeObject);
			String number = technicNumber.substring(0, technicNumber.indexOf("_ZF"));
			List<XWTreeNode> newNodeList = createNewTreeNode(number, technicTreeNode.getAllSteps());
			List<String> displayNames = new ArrayList<String>();
 			for(XWTreeNode newNode : newNodeList){
 				technicTreeNode.addChild(newNode.getObject());
 				String displayName = newNode.getDisplayName();
 				String zfType = newNode.getObject().getTreeCellData().getParent().getParent().attributeValue("ZFFLAG");
 				String ppNum = displayName.split(" ")[1];
 				if("F".equals(zfType)){
 					ppNum = ppNum.substring(ppNum.lastIndexOf("F"), ppNum.length());
 					if(!displayNames.contains(ppNum)){
 						displayNames.add(ppNum);
 					}
 				}
			}
 			String ppNumber = "";
 			for(String str : displayNames){
 				ppNumber = ppNumber + "、" + str;
 			}
 			technicTreeObject.setDisplayName(ppNumber);
			technicTreeNode.expandAllZFSteps(technicTreeNode);
		}else{
			XWTechnicsTreeObject technicTreeObject = new XWTechnicsTreeObject(document);
			technicTreeNode = new XWTreeNode(technicTreeObject);
			List<XWTreeNode> nodeList = technicTreeNode.getAllSteps();
			for(XWTreeNode node : nodeList){
				technicTreeNode.addChild(node.getObject());
			}
			technicTreeNode.expandAllSteps();
		}

		mesTree = new JTree(technicTreeNode);
		mesTree.setCellRenderer(new MESTypeCellRenderer());
		mesTree.setRootVisible(true);
		scrollPane.setViewportView(mesTree);

		mesTree.addTreeSelectionListener(this);
		setSelectedTreeNode(technicTreeNode, produreNumber);
		scrollPane.setHorizontalScrollBarPolicy(30);
		scrollPane.setVerticalScrollBarPolicy(20);
		scrollPane.getViewport().updateUI();
	}
	public void setSelectedTreeNode(XWTreeNode treeNode,String currentStepNumber){
		for(int i = 0; i < treeNode.getChildCount(); i++){
			XWTreeNode childNode = (XWTreeNode) treeNode.getChildAt(i);
			String stepNumber = childNode.getObject().getTreeCellData().attributeValue("stepNumber");
			if(stepNumber.equals(currentStepNumber)){
				mesTree.setSelectionRow(i+1);
			}
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		Element technicElement = XmlUtility.getTechnicsElement(document);
		state = technicElement.attributeValue("lifecycle");
		XWTreeNode node = getSelectedTreeNode();
		if(node != null){
			XWTreeObject xo = node.getObject();
			if(xo != null){
				if(xo instanceof XWMesStepTreeObject || xo instanceof XWStepTreeObject){
					technicNumber = xo.getTreeCellData().getParent().getParent().attributeValue("technicsNumber");
					objType = "工序";
					Element objElement = xo.getTreeCellData();
					objNumber = objElement.attributeValue("stepNumber");
					String produreName = objElement.attributeValue("stepName");
					String newNumber = node.getDisplayName();
					newNumber = newNumber.substring(0, newNumber.indexOf("_"));
					System.out.println("objNumber:" + objNumber + ",technicNumber:" + technicNumber + ",objType:" + objType + ",state:" + state);
					DPMesParameterMainFrame.getMesParameterMainPanel().getProcedureNameField().setText(produreName);
					DPMesParameterMainFrame.getMesParameterMainPanel().getProcedureNumberField().setText(newNumber);
					DPMesParameterMainFrame.getMesParameterMainPanel().getRightTechnicInfoPanel().initUIValues();
				}else if(xo instanceof XWMesPaceTreeObject || xo instanceof XWPaceTreeObject){
					technicNumber = xo.getTreeCellData().getParent().getParent().getParent().getParent().attributeValue("technicsNumber");
					objType = "工步";
					Element objElement = xo.getTreeCellData();
					Element parentElement = objElement.getParent().getParent();
					String produreNumber = parentElement.attributeValue("stepNumber");
					String produreName = parentElement.attributeValue("stepName");
					objNumber = produreNumber + "-" + objElement.attributeValue("stepNumber");
					DPMesParameterMainFrame.getMesParameterMainPanel().getProcedureNameField().setText(produreName);
					DPMesParameterMainFrame.getMesParameterMainPanel().getProcedureNumberField().setText(produreNumber);
					System.out.println("objNumber:" + objNumber + "technicNumber:" + technicNumber + "objType:" + objType + ",state:" + state);
					DPMesParameterMainFrame.getMesParameterMainPanel().getRightTechnicInfoPanel().initUIValues();
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
	 * 生成主辅关联新的工艺树
	 * @param technicNumber
	 * @param nodeList
	 * @return
	 */
	public List<XWTreeNode> createNewTreeNode(String technicNumber, List<XWTreeNode> nodeList){
		List<XWTreeNode> newNodeList = new ArrayList<XWTreeNode>();
		Map<String, List<GLZhuFuLink>> linksMap = (Map<String, List<GLZhuFuLink>>) MesParameterProcessor.getZFTechnics(technicNumber)[2];
		int newNumber = 10;
		for(XWTreeNode node : nodeList){
			String displayName = node.getDisplayName();
			String stepNumber = displayName.substring(0, displayName.indexOf("_"));
			String stepName = displayName.substring(displayName.indexOf("_") + 1, displayName.length());
			List<GLZhuFuLink> mapValue = linksMap.get(stepNumber);
			if(mapValue != null && mapValue.size() > 0){
				for(GLZhuFuLink link : mapValue){
					String fzTechnicNumber = link.getFztechnicsnumber();
					String fzVersion = link.getFztechnicsversion();
					String fzStepNumber = link.getFzprocedurenumber();
					fzStepNumber = fzStepNumber.substring(0, fzStepNumber.indexOf("_"));
					Map<String, XWTreeNode> fzNodeMap = getTechnicsNodes(fzTechnicNumber, fzVersion);
					XWTreeNode fzXWTreeNode = fzNodeMap.get(fzStepNumber);
					XWMesStepTreeObject stepObject = (XWMesStepTreeObject) fzXWTreeNode.getObject();
					stepObject.setDisplaName(String.valueOf(newNumber), fzStepNumber);
					newNodeList.add(fzXWTreeNode);
					newNumber += 10;
				}
			}else{
				XWMesStepTreeObject stepObject = (XWMesStepTreeObject) node.getObject();
				stepObject.setDisplaName(String.valueOf(newNumber), stepNumber);
				newNodeList.add(node);
				newNumber += 10;
			}
		}
		return newNodeList;
	}
	/**
	 * 根据工艺文件编号，版本获得辅制工艺的工序节点集合
	 * @param technicNumber
	 * @param version
	 * @return
	 */
	public Map<String, XWTreeNode> getTechnicsNodes(String technicNumber, String version){
		Map<String, XWTreeNode> nodeMap = new HashMap<String, XWTreeNode>();
		Vector<Object> result = MesParameterProcessor.getTechnicDocumentByNumberAndVersion(technicNumber, version);
		Document doc = MesParameterProcessor.downloadData(result);
		XWMesTechnicsTreeObject technicTreeObject = new XWMesTechnicsTreeObject(doc);
		XWTreeNode technicTreeNode = new XWTreeNode(technicTreeObject);
		List<XWTreeNode> nodeList =  technicTreeNode.getAllSteps();
		for(XWTreeNode node : nodeList){
			String stepNumber = node.getDisplayName().substring(0, node.getDisplayName().indexOf("_"));
			nodeMap.put(stepNumber, node);
		}
		return nodeMap;
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

}
