package com.glaway.mpm.pbombuilder.tree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.vecmath.Matrix4d;

import wt.part.WTPart;
import wt.part.WTPartUsageLink;

import com.glaway.mpm.pbombuilder.action.CmEPViewStructureGenerator;
import com.glaway.mpm.pbombuilder.bom.CmConnectFrame;
import com.glaway.mpm.pbombuilder.bom.WTPartUtil;
import com.glaway.mpm.pbombuilder.data.CmMPartInstance;
import com.glaway.mpm.pbombuilder.data.CmNode;
import com.glaway.mpm.pbombuilder.data.CmPartUsesOcc;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.panel.CmMPartMaster;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmInstanceIdentifier;
import com.glaway.mpm.pbombuilder.util.CmMathUtil;
import com.glaway.mpm.pbombuilder.util.CmSearchHelper;
import com.glaway.mpm.pbombuilder.util.CmXML;
import com.glaway.mpm.pbombuilder.util.CmXML.CmXmlProperty;
import com.ptc.pview.pvkapp.ComponentInstance;
import com.ptc.pview.pvkapp.ComponentNode;
import com.ptc.pview.pvkapp.ShapeInstance;

public class CmTreeNode extends DefaultMutableTreeNode implements Serializable {
	private static final long serialVersionUID = 610285217272571232L;
	private static final CmLogger log = CmLogger.getLogger(CmTreeNode.class.getName());

	private CmLightPart part;

	private Matrix4d matrix;
	private Matrix4d relativeMatrix;
	private String occId;
	private String occpath;
	private CmInstanceIdentifier instanceIdentifier;

	private boolean isSelected;
	private boolean marked;

	// ProductView objects -> start
	private ComponentNode _pviewComponentNode;
	private ComponentInstance _pviewComponentInstance;
	private ShapeInstance _pviewShapeInstance;
	private Collection<String[]> _pviewInstanceProperties;

	private Object cadObject;
	private boolean isInWorkspace;
	private boolean isLockedForCad;
	private boolean isCreoClick;//是点击图形还是点击树
	private boolean ispackage;//是否有打包的结构
	private String status;//节点状态：新增、删除、更新
	private String fktGrp;
	private String[] infoTxt = { "", "" };
	private Matrix4d relative_matrix = null;
	private boolean locationChange;
	private Vector bboxes = null;
	private Matrix4d basematrix = null;
	private String diode = "";
	private int _index;
	private List<CmTreeNode> children;
	private List<CmTreeNode> listNode;
	// ProductView objects -> end

	protected String nodeType;

	public static final String AO = "AO";
	public static final String OPS = "OPS";
	public static final String CPS = "CPS";
	public static final String OP = "OP";
	public static final String CP = "CP";
	public static final String PART = "PART";
	public static final String GY = "GY";
	public static final String CONSULT = "CONSULT";

	private List<CmLightPart> pathFromCI;
	private CmTree cmTree;
	private String rootPath = "ERROR";
	public static CmTree staticCmTree;
	private List<String> childOccpathList;//记录   保存可视化3D后的节点下的子节点的occpath信息
	private boolean isOut;

	private boolean isNewTopNode;  //是否是添加的顶级节点  --- 添加的顶级节点可删除、其子节点不可删除
	private boolean isChangeCountNode; //是否修改数量节点  --  节点不可删除

	public CmTreeNode(Object useObject) {
		super(useObject);
		// (String) useObject;
		if (useObject instanceof CmNode) {
			this.part = ((CmNode) useObject).getPart();
		} else {
			rootPath = (String) useObject;
		}

		this.instanceIdentifier = new CmInstanceIdentifier();
		this.matrix = CmPartUsesOcc.STD_MATRIX4D;
		this.relativeMatrix = CmPartUsesOcc.STD_MATRIX4D;
		this.occId = String.valueOf(System.nanoTime());
		this.isSelected = false;
		this.marked = false;
		this.nodeType = PART;
	}

	public boolean isOut() {
		return isOut;
	}

	public void setOut(boolean isOut) {
		this.isOut = isOut;
	}

	public String toString() {
		if (userObject == null) {
			return null;
		} else {
			String nodename=userObject.toString();
			if (!CmCommonStringUtil.isEmpty(this.getPart().getVersion())){
				nodename=nodename.substring(0, nodename.lastIndexOf(")")+2);
				if(!CmCommonStringUtil.isEmpty(this.getPart().getVersion())){
					nodename+=this.getPart().getVersion();
				}
			}
			if(!CmCommonStringUtil.isEmpty(this.getPart().getPartType()) && "assistant".equals(this.getPart().getPartType())){
				if(this.getPart().getProductionQuantity()==0){
					return nodename + ",×" + this.getPart().getProductionRatio();
				}
				else{
					return nodename + ",×" + this.getPart().getProductionQuantity();
				}
			}
			else if (null != this.listNode && this.listNode.size() > 0) {
//				return nodename + ",×" + (this.listNode.size() + 1);
				String gysl = this.getPart().getGysl();
				if(gysl != null && !"".equals(gysl)) {
					return nodename + ",×" + (this.listNode.size() + 1)+"("+gysl+")";
				} else {
//					return nodename + ",×" + (this.listNode.size() + 1)+"("+(this.listNode.size() + 1)+")";
					return nodename + ",×" + (this.listNode.size() + 1);
				}
			} else if (this.getPart().getGysl() != null && !"".equals(this.getPart().getGysl())){
				return nodename + "(" + this.getPart().getGysl() + ")";
			} else {
				int index = nodename.indexOf(",×");
				if (index > 0) {
					return nodename.substring(0, index);
				} else {
					return nodename;
				}
			}
		}
	}

	public CmTree buildEbomTree() throws Exception {

		WTPart part = (WTPart) CmSearchHelper.search(WTPart.class, Long.valueOf(CmConnectFrame.partOid));
		List<WTPart> list = WTPartUtil.getChildPart(part);
		CmTreeNode node = new CmTreeNode(rootPath);
		CmTreeNode root = getCmRootNode(part);
		node.add(root);
		buildPbomTreeNode(root, list);
		cmTree = new CmTree(node);
		cmTree.updateUI();
		staticCmTree = cmTree;
		return cmTree;
	}

	public CmTreeNode buildPbomTreeNode(CmTreeNode node, List<WTPart> list) throws Exception {
		for (WTPart wtPart : list) {
			CmLightPart cmLightPart = new CmLightPart();
			String item = getCmTreeItem(wtPart, cmLightPart, true);
			CmTreeNode cmTreeNode = new CmTreeNode(item);
			buildPbomTreeNode(cmTreeNode, WTPartUtil.getChildPart(wtPart));
			cmTreeNode.setPart(cmLightPart);
			node.add(cmTreeNode);
		}
		return node;
	}

	public CmTreeNode getCmRootNode(WTPart part) {
		CmLightPart cmLightPart = new CmLightPart();
		String item = getCmTreeItem(part, cmLightPart, false);
		CmTreeNode tree = new CmTreeNode(item);
		tree.setPart(cmLightPart);
		return tree;
	}

	public String getCmTreeItem(WTPart part, CmLightPart cmLightPart, boolean flag) {
		String item = part.getNumber() + "," + part.getName();
		if (flag) {
			item += ",×" + 2;
		}
		cmLightPart.setPartName(part.getName());
		cmLightPart.setPartNumber(part.getNumber());
		cmLightPart.setPartType("normal");
		cmLightPart.setUseCount(2);
		cmLightPart.setVersion(part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue());
		return item;
	}

	public CmTreeNode getRootNode(WTPart part, WTPartUsageLink link) {
		CmLightPart cmLightPart = new CmLightPart();
		String item = getTreeItem(part, link, cmLightPart);
		CmTreeNode tree = new CmTreeNode(item);
		tree.setPart(cmLightPart);
		return tree;
	}

	public String getTreeItem(WTPart part, WTPartUsageLink link, CmLightPart cmLightPart) {
		String item = part.getNumber() + "," + part.getName() + ",×" + (int) link.getQuantity().getAmount();
		cmLightPart.setPartName(part.getName());
		cmLightPart.setPartNumber(part.getNumber());
		cmLightPart.setPartType("normal");
		cmLightPart.setUseCount((int) link.getQuantity().getAmount());
		cmLightPart.setVersion(part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue());
		return item;
	}

	public List<CmLightPart> getPathFromCI() {
		return pathFromCI;
	}

	public void setPathFromCI(List<CmLightPart> pathFromCI) {
		this.pathFromCI = pathFromCI;
	}

	public CmLightPart getPart() {
		if (getUserObject() instanceof CmNode) {
			CmLightPart lightPart = ((CmNode) getUserObject()).getPart();
			return lightPart == null ? CmLightPart.EMPTY_PART : lightPart;
		}
		return part == null ? CmLightPart.EMPTY_PART : part;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public List<CmTreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<CmTreeNode> children) {
		this.children = children;
	}

	public List<CmTreeNode> getListNode() {
		return listNode;
	}

	public void setListNode(List<CmTreeNode> listNode) {
		this.listNode = listNode;
	}

	@SuppressWarnings("unchecked")
	public void setSelected(boolean isSelected) {
		setSelectedWithoutPropagate(isSelected);

		Enumeration allChilds = breadthFirstEnumeration();
		while (allChilds.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) allChilds.nextElement();
			batchChildrenNodesSelected(child,isSelected);
		}

		if (isSelected) {
			CmTreeNode father = (CmTreeNode) getParent();
			while (father != null && !father.isSelected()) {
				father.setSelectedWithoutPropagate(isSelected);
				father = (CmTreeNode) father.getParent();
			}
		} else {
			// 如果其它兄弟节点未选中，则需要取消选中父节点的
			unselectFatherIfNoSelectedSiblings(this);
		}
		setSelectedOfPackage(this,isSelected);
	}

	public void batchChildrenNodesSelected(CmTreeNode node,boolean isSelected){
		node.setSelectedWithoutPropagate(isSelected);
		Enumeration children = node.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			batchChildrenNodesSelected(child,isSelected);
		}
		//打包后的兄弟节点
		if(CmCommonStringUtil.isPackage(node)){
			for(CmTreeNode brother:node.getListNode()){
				batchChildrenNodesSelected(brother,isSelected);
			}
		}
	}

	public void setSelectedOfPackage(CmTreeNode node,boolean isSelected){
		if(null !=node.getParent() && null != node.getParent().getParent()){
			//第一步：找到最上层的父节点
			CmTreeNode packageNode = getParentPackageNode(node);
			if(null != packageNode){
				for(CmTreeNode cmnode:packageNode.getListNode()){
					//第二步：找到对应的节点
					CmTreeNode child = CmCommonStringUtil.getBrotherPackageNode(node, cmnode,packageNode.getLevel());
					setSelected(child,isSelected);
					if(CmCommonStringUtil.isPackage(child)){
						for(CmTreeNode brother:child.getListNode()){
							setSelected(brother,isSelected);
						}
					}
				}
			}
		}
	}

	public void setSelected(CmTreeNode node,boolean flag){
		setSelectedWithoutPropagate(isSelected);

		Enumeration allChilds = node.breadthFirstEnumeration();
		while (allChilds.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) allChilds.nextElement();
			batchChildrenNodesSelected(child,isSelected);
		}

		if (isSelected) {
			CmTreeNode father = (CmTreeNode)node.getParent();
			while (father != null && !father.isSelected()) {
				father.setSelectedWithoutPropagate(isSelected);
				father = (CmTreeNode) father.getParent();
			}
		} else {
			// 如果其它兄弟节点未选中，则需要取消选中父节点的
			unselectFatherIfNoSelectedSiblings(node);
		}
		setSelectedOfPackage(node,isSelected);
	}

	public CmTreeNode getParentPackageNode(CmTreeNode node){
		CmTreeNode parent = (CmTreeNode) node.getParent();
		if(null == parent.getParent().getParent()){
			return null;
		}
		else if(CmCommonStringUtil.isPackage(parent)){
			return parent;
		}
		else{
			getParentPackageNode(parent);
		}
		return null;
	}

	private void unselectFatherIfNoSelectedSiblings(CmTreeNode node) {
		CmTreeNode siblNode = (CmTreeNode) node.getPreviousSibling();
		while (siblNode != null && !siblNode.isSelected()) {
			siblNode = (CmTreeNode) siblNode.getPreviousSibling();
		}
		if (siblNode == null) {// no selected sibl node found!
			siblNode = (CmTreeNode) node.getNextSibling();
			while (siblNode != null && !siblNode.isSelected()) {
				siblNode = (CmTreeNode) siblNode.getNextSibling();
			}
		}
		if (siblNode == null) {// no selected sibl node found!
			CmTreeNode father = (CmTreeNode) node.getParent();
			if (father != null) {
				father.setSelectedWithoutPropagate(false);
				unselectFatherIfNoSelectedSiblings(father);
			}
		}
	}

	private void setSelectedWithoutPropagate(boolean isSelected) {
		this.isSelected = isSelected;

		try {
			if (!isSelected) {
				CmEPViewStructureGenerator.desactivate(get_pviewShapeInstance());
			} else {
				CmEPViewStructureGenerator.activate(get_pviewShapeInstance());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	public CmInstanceIdentifier getInstanceIdentifier() {
		return instanceIdentifier;
	}

	public void setInstanceIdentifier(CmInstanceIdentifier instanceIdentifier) {
		this.instanceIdentifier = instanceIdentifier;
	}

	public Matrix4d getMatrix() {
		return matrix;
	}

	public void setMatrix(Matrix4d matrix) {
		this.matrix = matrix;
	}

	public Matrix4d getRelativeMatrix() {
		return relativeMatrix;
	}

	public void setRelativeMatrix(Matrix4d relativeMatrix) {
		this.relativeMatrix = relativeMatrix;
	}

	public String getOccId() {
		return occId;
	}

	public void setOccId(String occId) {
		this.occId = occId;
	}

	public ComponentInstance get_pviewComponentInstance() {
		return _pviewComponentInstance;
	}

	public void set_pviewComponentInstance(ComponentInstance componentInstance) {
		_pviewComponentInstance = componentInstance;
	}

	public ComponentNode get_pviewComponentNode() {
		return _pviewComponentNode;
	}

	public void set_pviewComponentNode(ComponentNode componentNode) {
		_pviewComponentNode = componentNode;
	}

	public ShapeInstance get_pviewShapeInstance() {
		return _pviewShapeInstance;
	}

	public void set_pviewShapeInstance(ShapeInstance shapeInstance) {
		_pviewShapeInstance = shapeInstance;
	}

	public boolean isIspackage() {
		return ispackage;
	}

	public void setIspackage(boolean ispackage) {
		this.ispackage = ispackage;
	}

	/***
	 * obj->xml & xml->obj
	 *
	 * @param xml
	 * @return
	 */
	public CmXML write2XML() {
		CmXML xml = new CmXML("Node");
		Vector<CmXmlProperty> propertys = new Vector<CmXmlProperty>();
		propertys.add(new CmXmlProperty("occId", getOccId()));
		propertys.add(new CmXmlProperty("nodeType", getNodeType()));

		if (getPart() == null)
			part = CmLightPart.newLightPart("", "", "", 0l);
		long oid = getPart().getOid();
		String oidStr = String.valueOf(oid);
		propertys.add(new CmXmlProperty("oid", oidStr));
		propertys.add(new CmXmlProperty("name", getPart().getPartName()));
		propertys.add(new CmXmlProperty("number", getPart().getPartNumber()));
		propertys.add(new CmXmlProperty("version", getPart().getVersion()));
		long masterOid = getPart().getMasterOid();
		String masterOidStr = String.valueOf(masterOid);
		propertys.add(new CmXmlProperty("masterOid", masterOidStr));

		if (getUserObject() instanceof CmMPartMaster) {
			CmMPartMaster master = (CmMPartMaster) getUserObject();
			propertys.add(new CmXmlProperty("quantity", String.valueOf(master.getQuantity())));
		}
		// 此node来自于EBOM ci中的路径
		xml.append(writePathFromCI());

		if (getInstanceIdentifier() != null)
			xml.append(getInstanceIdentifier().toXML());
		xml.setAttrs(propertys);
		if (matrix != null)
			xml.set("matrix", CmMathUtil.getLocationAsString(matrix));
		if (relativeMatrix != null)
			xml.set("relativeMatrix", CmMathUtil.getLocationAsString(relativeMatrix));

		writeChildNode2XML(xml);

		return xml;
	}

	protected CmXML writePathFromCI() {
		CmXML xml = new CmXML("PathFromCI");
		if (pathFromCI != null) {
			for (CmLightPart lightPart : pathFromCI) {
				if (lightPart != null) {
					CmXML partXml = new CmXML("PathPart");
					partXml.set("oid", lightPart.getOid());
					partXml.set("name", lightPart.getPartName());
					partXml.set("number", lightPart.getPartNumber());
					partXml.set("oversion", lightPart.getVersion());
					xml.append(partXml);
				}
			}
		}
		return xml;
	}

	public static List<CmLightPart> loadPathFromCI4XML(CmXML xml) {
		List<CmLightPart> pathPart = new Vector<CmLightPart>();
		CmXML pathXML = xml.loc(".PathFromCI");
		if (pathXML != null)
			loadPathPart4XML(pathXML.firstElem(), pathPart);
		return pathPart;
	}

	public static void loadPathPart4XML(CmXML xml, List<CmLightPart> pathPart) {
		if (xml != null && "PathPart".equals(xml.getName())) {
			CmLightPart lightPart = loadPart4XML(xml);
			pathPart.add(lightPart);
			loadPathPart4XML(xml.nextElem(), pathPart);
		}
	}

	private void writeChildNode2XML(CmXML nodeXml) {
		Enumeration<CmTreeNode> children = children();
		if (children.hasMoreElements()) {
			CmXML childrenXML = new CmXML("children");
			nodeXml.append(childrenXML);
			while (children.hasMoreElements()) {
				CmTreeNode child = children.nextElement();
				childrenXML.append(child.write2XML());
			}
		}
	}

	/***
	 * obj->xml xml->obj
	 *
	 * @param xml
	 * @return
	 */
	public static CmTreeNode load4XML(CmXML xml) {
		CmXML nodeXML = xml == null ? null : xml.loc(".Node");
		if (nodeXML == null)
			return null;

		return loadNode4XML(nodeXML);
	}

	private static CmTreeNode loadNode4XML(CmXML xml) {
		// 不仅有CmMPartInstance，也可能是CmMPartMaster
		CmTreeNode node = null;

		CmLightPart lightPart = loadPart4XML(xml);
		if (xml.attrvalnum("quantity") > 0) {
			node = new CmTreeNode(new CmMPartMaster(lightPart, xml.attrvalnum("quantity")));
		} else {
			node = new CmTreeNode(new CmMPartInstance(lightPart));
		}

		node.setPathFromCI(loadPathFromCI4XML(xml));
		node.instanceIdentifier.loadFromXML(xml.loc(".CmInstanceIdentifier"));

		String matrix = xml.attrval("matrix");
		if (matrix != null)
			node.matrix = CmMathUtil.getMatrix4dFromString(matrix);

		String relativeMatrix = xml.attrval("relativeMatrix");
		if (relativeMatrix != null)
			node.relativeMatrix = CmMathUtil.getMatrix4dFromString(relativeMatrix);

		String occId = xml.attrval("occId");
		node.setOccId(occId);

		String nodeType = xml.attrval("nodeType");
		node.setNodeType(nodeType);

		CmXML childrenXML = xml.loc(".children");
		if (childrenXML != null)
			loadChildNode4XML(childrenXML, node);

		return node;
	}

	private static void loadChildNode4XML(CmXML xml, CmTreeNode parentNode) {
		if (xml == null)
			return;

		CmXML nextXML = xml.firstElem();
		while (nextXML != null) {
			CmTreeNode node = loadNode4XML(nextXML);
			if (node != null)
				parentNode.add(node);
			nextXML = nextXML.nextElem();
		}
	}

	private static CmLightPart loadPart4XML(CmXML nodeXML) {
		if (nodeXML == null)
			return null;

		String oid = nodeXML.attrval("oid");
		long oidl = Long.valueOf(oid);
		CmLightPart lightPart = null;
		if (oidl > 0) {
			WTPart part;
			try {
				part = (WTPart) CmSearchHelper.search(WTPart.class, oidl);
				lightPart = CmBizObjUtil.buildCmLightPartFromWTPart(part);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (lightPart == null) {
			String name = nodeXML.attrval("name");
			String number = nodeXML.attrval("number");
			String version = nodeXML.attrval("version");

			lightPart = CmLightPart.newLightPart(number, name, version, 0l);
		}

		return lightPart;
	}

	public void set_cadObject(Object object) {
		cadObject = object;
	}

	public Object get_cadObject() {
		return cadObject;
	}

	public boolean get_wsInfo() {
		return isInWorkspace;
	}

	public void set_wsInfo(boolean isInWS) {
		isInWorkspace = isInWS;
	}

	public void set_LockedForCAD(boolean locked) {
		isLockedForCad = locked;
	}

	public boolean get_LockedForCAD() {
		return isLockedForCad;
	}

	public String getFktGrp() {
		return fktGrp;
	}

	public void setFktGrp(String _fktGrp) {
		this.fktGrp = _fktGrp;
	}

	public String[] getInfoTxt() {
		return infoTxt;
	}

	public void setInfoTxt(String[] infoTxt) {
		this.infoTxt = infoTxt;
	}

	public Matrix4d getRelative_matrix() {
		return relative_matrix;
	}

	public void setRelative_matrix(Matrix4d a_Relative_matrix) {
		relative_matrix = a_Relative_matrix;
	}

	public boolean getLocChange() {
		return locationChange;
	}

	public void setLocChange(boolean locChange) {
		locationChange = locChange;
	}

	public Vector getBboxes() {
		return bboxes;

	}

	public void setBboxes(Vector a_Bboxes) {
		bboxes = a_Bboxes;

	}

	public void setBaseMatrix(Matrix4d a_Matrix) {
		basematrix = a_Matrix;
	}

	public boolean isCreoClick() {
		return isCreoClick;
	}

	public void setCreoClick(boolean isCreoClick) {
		this.isCreoClick = isCreoClick;
	}

	public Matrix4d getBaseMatrix() {
		return basematrix;
	}

	public String getDiode() {
		return diode;
	}

	public void setPart(CmLightPart part) {
		this.part = part;
	}

	public void setDiode(String a_Diode) {
		diode = a_Diode;
		try {
			if (this.get_pviewShapeInstance() != null) {
				// CmAOTechnicsMarkMPViewGenerator.applyColorOnPView(this,
				// a_Diode);
			}
			if (this.get_cadObject() != null) {
				// CadAdapter.applyColor(this, a_Diode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int get_index() {
		return _index;
	}

	public void set_index(int _index) {
		this._index = _index;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getOccpath() {
		return occpath;
	}

	public void setOccpath(String occpath) {
		this.occpath = occpath;
	}

	public Collection<String[]> get_pviewInstanceProperties() {
		return _pviewInstanceProperties;
	}

	public void set_pviewInstanceProperties(Collection<String[]> _pviewInstanceProperties) {
		this._pviewInstanceProperties = _pviewInstanceProperties;
	}
	public List<String> getChildOccpathList() {
		return childOccpathList;
	}

	public void setChildOccpathList(List<String> childOccpathList) {
		this.childOccpathList = childOccpathList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
//		result = prime * result + ((occId == null) ? 0 : occId.hashCode());
		result = prime * result + ((occpath == null) ? 0 : occpath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CmTreeNode other = (CmTreeNode) obj;
//		if (occId == null) {
//			if (other.occId != null)
//				return false;
//		} else if (!occId.equals(other.occId))
//			return false;
		if (occpath == null) {
			if (other.occpath != null)
				return false;
		} else if (!occpath.equals(other.occpath))
			return false;
		return true;
	}

	public boolean isNewTopNode() {
		return isNewTopNode;
	}

	public void setNewTopNode(boolean isNewTopNode) {
		this.isNewTopNode = isNewTopNode;
	}

	public boolean isChangeCountNode() {
		return isChangeCountNode;
	}

	public void setChangeCountNode(boolean isChangeCountNode) {
		this.isChangeCountNode = isChangeCountNode;
	}

	public Object deepClone() throws IOException, OptionalDataException,
			ClassNotFoundException {// 将对象写到流里
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo = new ObjectOutputStream(bo);
		oo.writeObject(this);// 从流里读出来
		ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
		ObjectInputStream oi = new ObjectInputStream(bi);
		Object obj = oi.readObject();
		oi.close();
		oo.close();
		return obj;
	}
}
