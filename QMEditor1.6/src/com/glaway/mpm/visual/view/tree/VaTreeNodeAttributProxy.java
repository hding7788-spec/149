package com.glaway.mpm.visual.view.tree;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.glaway.mpm.visual.bean.VaAttributRecord;
import com.glaway.mpm.visual.bean.VaXmlProxy;
import com.glaway.mpm.visual.util.VaXML;

/**
 * <br>
 * Created on 2012-10-29
 * 
 * @author chenyunlong
 */
public class VaTreeNodeAttributProxy implements VaXmlProxy {

	private static final long serialVersionUID = 4124734407627794005L;
	private Map<String, VaPartAttributNode> attributs = new HashMap<String, VaPartAttributNode>();
	/**
	 *XML节点属性集合顶级节点名
	 */
	public static String XMLTITLE = "TreeNode_Attribut";
	public static String key_symbol = "KEY";
	public static String key_split_symbol = "_";

	private VaTreeNode rootNode;
	private VaXML xml;

	public VaTreeNodeAttributProxy() {

	}

	public void addAttributNode(VaPartAttributNode parentNode, VaPartAttributNode currNode) {
		String key = createKey(parentNode, currNode);
		attributs.put(key, currNode);
	}

	public VaPartAttributNode getAttributNode(String key) {

		return attributs.get(key);
	}

	public Map<String, VaPartAttributNode> getAttributs() {
		return attributs;
	}

	public VaPartAttributNode getAttributNode(long parentOid, long currOid) {
		String key = createKey(parentOid, currOid);
		return getAttributNode(key);
	}

	public VaPartAttributNode getAttributNode(VaTreeNode parentNode, VaTreeNode currNode) {
		String key = createKey(parentNode, currNode);
		return getAttributNode(key);
	}

	public VaAttributRecord getAttribute(VaTreeNode parentNode, VaTreeNode currNode) {
		String key = createKey(parentNode, currNode);
		return getAttribute(key);
	}

	public VaAttributRecord getAttribute(long parentOid, long currOid) {
		String key = createKey(parentOid, currOid);
		return getAttribute(key);
	}

	public VaAttributRecord getAttribute(String key) {
		VaPartAttributNode node = getAttributNode(key);
		if (node == null)
			return null;
		return node.getAttribut();
	}

	public void deleteAttributNode(String key) {
		attributs.remove(key);
	}

	public void deleteAllAttribut() {
		attributs = new HashMap<String, VaPartAttributNode>();
	}

	public void deleteAttributNode(VaTreeNode parentNode, VaTreeNode currNode) {
//		log.debug("删除属性记录： parentNode" + parentNode + "   currNode:" + currNode);
		String key = createKey(parentNode, currNode);
//		log.debug("转换成key为:" + key);
//		log.debug("找到key为:" + key + "  的值为" + getAttributNode(key));
		deleteAttributNode(key);
	}

	public String createKey(VaTreeNode parentNode, VaTreeNode currNode) {
		return createKey(parentNode.getPart().getOid(), currNode.getPart().getOid());
	}

	public String createKey(long parentOid, long currOid) {
		return createKey(String.valueOf(parentOid), String.valueOf(currOid));
	}

	public String createKey(String parentOid, String currOid) {
		String key = "";
		key += parentOid + key_split_symbol + currOid;
		return key;
	}

	public void parse(VaTreeNode node, VaXML xml) {
		this.rootNode = node;
		this.xml = xml;

		if (xml == null)
			return;
		VaXML firstXML = xml.firstElem();
		if (firstXML == null)
			return;
		buildAttribut(firstXML);
	}

	private void buildAttribut(VaXML xmlelem) {
		if (xmlelem == null)
			return;

		String key = xmlelem.attrval(key_symbol);

		if (key == null || key.trim().length() == 0)
			return;

		VaTreeNode[] nodes = getOidFromKey(rootNode, key);
		VaTreeNode parentNode = nodes[0];
		VaTreeNode currNode = nodes[1];
		if (parentNode == null || currNode == null)
			return;

		VaPartAttributNode pan = VaPartAttributNode.load4XML(currNode, xmlelem);
		attributs.put(key, pan);
		buildAttribut(xmlelem.nextElem());

	}

	private VaTreeNode[] getOidFromKey(VaTreeNode root, String key) {
		String[] oidstr = key.split(key_split_symbol);
		String parentOid = oidstr[0];
		String curroid = oidstr[1];
		VaTreeNode parentNode = getNodeForOid(root, parentOid);
		VaTreeNode currNode = getNodeForOid(root, curroid);
		return new VaTreeNode[] { parentNode, currNode };
	}

	private VaTreeNode getNodeForOid(VaTreeNode node, String oid) {
		VaTreeNode ret = null;
		long oidl = Long.valueOf(oid);
		Enumeration<VaTreeNode> nodes = node.breadthFirstEnumeration();
		while (nodes.hasMoreElements()) {
			ret = nodes.nextElement();
			if (oidl != ret.getPart().getOid()) {
				continue;
			}
			return ret;
		}
		return ret;
	}

	public VaTreeNode getTreeNode() {
		return rootNode;
	}

	public void setNode(VaTreeNode node) {
		this.rootNode = node;
	}

	public VaXML toXML() {
		VaXML xml = new VaXML(XMLTITLE);
		Iterator<String> it = attributs.keySet().iterator();
		for (String key : attributs.keySet()) {
			VaPartAttributNode att = attributs.get(key);
			VaXML attXml = att.toXML();
			attXml.set(key_symbol, key);
			xml.append(attXml);
		}
		return xml;
	}
}
