package com.glaway.mpm.pbombuilder.data;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.glaway.mpm.pbombuilder.tree.CmPartAttributNode;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmXML;

/**
 * <br>
 * Created on 2012-10-29
 * 
 * @author chenyunlong
 */
public class CmTreeNodeAttributProxy implements CmXmlProxy {

	private static final long serialVersionUID = 4124734407627794005L;
	private Map<String, CmPartAttributNode> attributs = new HashMap<String, CmPartAttributNode>();
	/**
	 *XML节点属性集合顶级节点名
	 */
	public static String XMLTITLE = "TreeNode_Attribut";
	public static String key_symbol = "KEY";
	public static String key_split_symbol = "_";

	private CmTreeNode rootNode;
	private CmXML xml;

	public CmTreeNodeAttributProxy() {

	}

	public void addAttributNode(CmPartAttributNode parentNode, CmPartAttributNode currNode) {
		String key = createKey(parentNode, currNode);
		attributs.put(key, currNode);
	}

	public CmPartAttributNode getAttributNode(String key) {

		return attributs.get(key);
	}

	public Map<String, CmPartAttributNode> getAttributs() {
		return attributs;
	}

	public CmPartAttributNode getAttributNode(long parentOid, long currOid) {
		String key = createKey(parentOid, currOid);
		return getAttributNode(key);
	}

	public CmPartAttributNode getAttributNode(CmTreeNode parentNode, CmTreeNode currNode) {
		String key = createKey(parentNode, currNode);
		return getAttributNode(key);
	}

	public CmAttributRecord getAttribute(CmTreeNode parentNode, CmTreeNode currNode) {
		String key = createKey(parentNode, currNode);
		return getAttribute(key);
	}

	public CmAttributRecord getAttribute(long parentOid, long currOid) {
		String key = createKey(parentOid, currOid);
		return getAttribute(key);
	}

	public CmAttributRecord getAttribute(String key) {
		CmPartAttributNode node = getAttributNode(key);
		if (node == null)
			return null;
		return node.getAttribut();
	}

	public void deleteAttributNode(String key) {
		attributs.remove(key);
	}

	public void deleteAllAttribut() {
		attributs = new HashMap<String, CmPartAttributNode>();
	}

	public void deleteAttributNode(CmTreeNode parentNode, CmTreeNode currNode) {
//		log.debug("删除属性记录： parentNode" + parentNode + "   currNode:" + currNode);
		String key = createKey(parentNode, currNode);
//		log.debug("转换成key为:" + key);
//		log.debug("找到key为:" + key + "  的值为" + getAttributNode(key));
		deleteAttributNode(key);
	}

	public String createKey(CmTreeNode parentNode, CmTreeNode currNode) {
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

	public void parse(CmTreeNode node, CmXML xml) {
		this.rootNode = node;
		this.xml = xml;

		if (xml == null)
			return;
		CmXML firstXML = xml.firstElem();
		if (firstXML == null)
			return;
		buildAttribut(firstXML);
	}

	private void buildAttribut(CmXML xmlelem) {
		if (xmlelem == null)
			return;

		String key = xmlelem.attrval(key_symbol);

		if (key == null || key.trim().length() == 0)
			return;

		CmTreeNode[] nodes = getOidFromKey(rootNode, key);
		CmTreeNode parentNode = nodes[0];
		CmTreeNode currNode = nodes[1];
		if (parentNode == null || currNode == null)
			return;

		CmPartAttributNode pan = CmPartAttributNode.load4XML(currNode, xmlelem);
		attributs.put(key, pan);
		buildAttribut(xmlelem.nextElem());

	}

	private CmTreeNode[] getOidFromKey(CmTreeNode root, String key) {
		String[] oidstr = key.split(key_split_symbol);
		String parentOid = oidstr[0];
		String curroid = oidstr[1];
		CmTreeNode parentNode = getNodeForOid(root, parentOid);
		CmTreeNode currNode = getNodeForOid(root, curroid);
		return new CmTreeNode[] { parentNode, currNode };
	}

	private CmTreeNode getNodeForOid(CmTreeNode node, String oid) {
		CmTreeNode ret = null;
		long oidl = Long.valueOf(oid);
		Enumeration<CmTreeNode> nodes = node.breadthFirstEnumeration();
		while (nodes.hasMoreElements()) {
			ret = nodes.nextElement();
			if (oidl != ret.getPart().getOid()) {
				continue;
			}
			return ret;
		}
		return ret;
	}

	public CmTreeNode getTreeNode() {
		return rootNode;
	}

	public void setNode(CmTreeNode node) {
		this.rootNode = node;
	}

	public CmXML toXML() {
		CmXML xml = new CmXML(XMLTITLE);
		Iterator<String> it = attributs.keySet().iterator();
		for (String key : attributs.keySet()) {
			CmPartAttributNode att = attributs.get(key);
			CmXML attXml = att.toXML();
			attXml.set(key_symbol, key);
			xml.append(attXml);
		}
		return xml;
	}
}
