package com.glaway.mpm.pbombuilder.util;

import com.glaway.mpm.pbombuilder.bom.CmConnectFrame;
import com.glaway.mpm.pbombuilder.jws.CmContext;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmScrollPaneTree;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.PbomTreeUpdateAction;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;
import com.ptc.jws.JWSUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import wt.part.WTPart;

import javax.swing.*;
import java.io.*;
import java.util.*;
public class CmXmlUtil {
	private static String PARTS = "parts";
	private static String CHILDS = "childs";
	private static String QMPARTINFO = "QMPartInfo";
	private static String PRODUCT = "Product";
	private static List<CmTreeNode> ebomNodesList;
	private static Map<CmTreeNode,CmTreeNode> ebomNodesMap = new HashMap<CmTreeNode,CmTreeNode>();
	private List<CmTreeNode> planingList=new ArrayList<CmTreeNode>();
	private List<List<String>> packageList=new ArrayList<List<String>>();
	private static List<Map<String,Object>> byteslist = new ArrayList<Map<String,Object>>();

	/**
	 * 将一个DOM树转换成其对应的XML文件的内容字节流数组
	 *
	 * @param document
	 *            Document对象
	 * @return byte[] DOM对应的XML文件的内容字节流数组
	 */
	public byte[] generateDocumentByteArray(Document document) throws Exception {
		if (null == document)
			return null;
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLWriter writer = new XMLWriter(out, format);
		writer.write(document);
		writer.flush();
		byte[] array = out.toByteArray();
		writer.close();
		out.close();
		out = null;
		return array;
	}

	/**
	 * 通过一个本地的xml文件来构造一个DOM树并返回
	 *
	 * @param fileName
	 *            本地的xml文件名
	 * @return Document对象 本地的xml文件对应的DOM树
	 */
	public Document getDocument(String fileName) throws Exception {
		if (null == fileName || fileName.trim().equals(""))
			return null;
		return getDocument(new File(fileName));
	}

	/**
	 * 通过一个本地的xml文件来构造一个DOM树并返回
	 *
	 * @param file
	 *            本地的xml文件
	 * @return Document对象 本地的xml文件对应的DOM树
	 */
	public Document getDocument(File file) throws Exception {
		if (null == file || (!file.isFile()))
			return null;
		SAXReader saxReader = new SAXReader();
		saxReader.setEncoding("GBK");
		Document document = saxReader.read(file);
		return document;
	}

	/**
	 * 通过一个xml文件内容的字节流数组，解析成DOM树后，返回Document对象
	 *
	 * @param xmlContentArray
	 *            xml文件内容的字节流数组
	 * @return Document对象
	 */
	public  Document getDocument(byte[] xmlContentArray) throws Exception {
		SAXReader saxReader = new SAXReader();
		saxReader.setEncoding("GBK");
		Document document = null;
		ByteArrayInputStream inputStream = null;
		try {
			inputStream = new ByteArrayInputStream(xmlContentArray);
			document = saxReader.read(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (null != inputStream)
				inputStream.close();
			inputStream = null;
			saxReader = null;
		}
		return document;
	}

	/**
	 * 将一个DOM树的XML信息保存到本地硬盘上
	 *
	 * @param document
	 *            DOM树对应的Document对象
	 * @param file
	 *            XML文件对象
	 * @return void
	 */
	public  void saveDocument(Document document, File file) throws Exception {
		if (null == document || null == file)
			return;
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");
		XMLWriter writer = new XMLWriter(new FileWriter(file), format);
		writer.write(document);
		writer.close();
	}

	/**
	 * 将一个DOM树的XML信息保存到本地硬盘上
	 *
	 * @param document
	 *            DOM树对应的Document对象
	 * @param fileName
	 *            XML文件全路径名
	 * @return void
	 */
	public  void saveDocument(Document document, String fileName) throws Exception {
		if (null == document || null == fileName || fileName.trim().equals(""))
			return;
		saveDocument(document, new File(fileName));
	}

	/**
	 * 将节点信息添加到Document对象中，再转换成对象流中
	 *
	 * @author chenyunlong
	 * @date 2012-10-31
	 * @param 将节点信息添加到Document对象中
	 *            ，再转换成对象流中
	 */
	public List<Map<String,Object>> saveTreeToBytes(CmTreeNode node) {
		Document doc =  org.dom4j.DocumentHelper.createDocument();
		doc.setXMLEncoding("GBK");
		// 根节点
		Element product = DocumentHelper.createElement(PRODUCT);
		product.addAttribute("oid", CmConnectFrame.partOid);
		product.addAttribute("productNumber", CmConnectFrame.productName);
		product.addAttribute("productName", CmConnectFrame.productName);
		Element root = DocumentHelper.createElement(PARTS);
		product.add(root);
		doc.add(product);
		Element element = addElementAttribute(node);
		element.setAttributeValue("isHasXml", "true");
		createChildElement(node, element);
		root.add(element);
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");
		String xml = "";
		try {
			StringWriter w = new StringWriter();
			XMLWriter writer = new XMLWriter(w, format);
			writer.write(doc);
			writer.flush();
			xml = w.getBuffer().toString();
			// xml = new String(xml.getBytes(), "GBK");
		} catch (Exception e) {
			e.printStackTrace();
		}

//		return xml.getBytes();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partNumber", node.getPart().getPartNumber());
		map.put("oid", node.getPart().getOid()+"");
		map.put("bytes", xml.getBytes());
		byteslist.add(map);
		return byteslist;
	}

	/**
	 *
	 * 如果子节点下还有节点，那么先创建一个childs对象，然后再添加节点Element，循环
	 *
	 * @date 2012-10-31
	 * @param node
	 * @param root
	 */
	@SuppressWarnings("unchecked")
	public void createChildElement(CmTreeNode node, Element root) {
		if (node.children().hasMoreElements()) {
			List<CmTreeNode> list = new ArrayList<CmTreeNode>();
			Enumeration children = node.children();
			while (children.hasMoreElements()) {
				list.add((CmTreeNode) children.nextElement());
			}
			Element childs = DocumentHelper.createElement(CHILDS);
			for (CmTreeNode cmnode : list) {
				Element e1 = addElementAttribute(cmnode);

				if(cmnode.getPart().isHasXml()||cmnode.getPart().isChangeOfStructure()){
					e1.setAttributeValue("isHasXml", "true");
					saveBrotherElement(node.getListNode(), cmnode, e1);

					childs.add(e1);
//					if(checkNodeIsSaveXML(cmnode) || cmnode.getPart().isEditWithChild()){
						CmTreeNode xmlNode = CmCommonStringUtil.copyNewNode(cmnode);
						CmTreeNode brother = null;//位号为-1或者大于0的零件才有图，可以保存xml
//						if(Long.valueOf(xmlNode.getOccId()) > 0 || Long.valueOf(xmlNode.getOccId()) == -1){
//							brother = xmlNode;
//							brother.setListNode(null);
//						}else{
//								for(CmTreeNode bro:xmlNode.getListNode()){
//									if(Long.valueOf(bro.getOccId()) > 0 || Long.valueOf(bro.getOccId()) == -1){
//										brother = bro;
//										break;
//									}
//								}
//						}
						/*if(xmlNode.getListNode()!=null){
							for(CmTreeNode bro:xmlNode.getListNode()){
								brother = bro;
								break;

							}
						}else{
							brother = xmlNode;
							brother.setListNode(null);
						}*/
						brother = xmlNode;
						brother.setListNode(null);

						if(null != brother){
							String occpath = brother.getOccpath();
							brother.setOccId("-1");
							brother.setOccpath("-1");
							brother.getPart().setParentPartNumber("");
//							CmBizObjUtil.rebuildOccpath(brother);
							rebuildOccpathFromTreeToXML(brother,occpath);
							saveTreeToBytes(brother);
						}
//					}
				}else if (cmnode.children().hasMoreElements() && CmCommonNodeUtil.checkNodeHasStructure(cmnode)) {
					if (CmCommonStringUtil.isPackage(cmnode)) {
						saveBrotherElement(node.getListNode(), cmnode, e1);
						createPackageElement(cmnode, e1);
					} else if(CmCommonStringUtil.isPackageOfParent(cmnode)){
						saveBrotherElement(node.getListNode(), cmnode, e1);
						createChildElement(cmnode, e1);
					}
					else {
						createChildElement(cmnode, e1);
					}
					childs.add(e1);
				}else if("assistant".equals(cmnode.getPart().getPartType())){
					childs.add(e1);
				} else {
					saveBrotherElement(node.getListNode(), cmnode, e1);
					if (cmnode.children().hasMoreElements()) {
						Element ele = DocumentHelper.createElement(CHILDS);
						Enumeration childrens = cmnode.children();
						while (childrens.hasMoreElements()) {
							CmTreeNode assist = (CmTreeNode) childrens.nextElement();
							Element e2 = addElementAttribute(assist);
							ele.add(e2);
						}
						e1.add(ele);
					}
					childs.add(e1);
				}
			}
			root.add(childs);
		}
	}
	/**
	 * 创建打包节点的DOM对象信息
	 *
	 * @author chenyunlong
	 * @date 2013-4-10
	 * @param node
	 * @param element
	 *
	 */
	@SuppressWarnings("unchecked")
	public void createPackageElement(CmTreeNode node, Element element) {
		List<CmTreeNode> list = new ArrayList<CmTreeNode>();
		Enumeration children = node.children();
		while (children.hasMoreElements()) {
			list.add((CmTreeNode) children.nextElement());
		}
		Element childs = DocumentHelper.createElement(CHILDS);
		for (CmTreeNode cmnode : list) {
			Element e1 = addElementAttribute(cmnode);
			if(cmnode.getPart().isHasXml()|| cmnode.getPart().isChangeOfStructure()){
				e1.setAttributeValue("isHasXml", "true");
				saveBrotherElement(node.getListNode(), cmnode, e1);
				childs.add(e1);
//				if(checkNodeIsSaveXML(cmnode) || cmnode.getPart().isEditWithChild()){
					CmTreeNode xmlNode = CmCommonStringUtil.copyNewNode(cmnode);
					CmTreeNode brother = null;
//					if(Integer.valueOf(xmlNode.getOccId()) > 0 || Integer.valueOf(xmlNode.getOccId()) == -1){
//						brother = xmlNode;
//						brother.setListNode(null);
//					}else{
//						for(CmTreeNode bro:xmlNode.getListNode()){
//							if(Integer.valueOf(bro.getOccId()) > 0 || Integer.valueOf(bro.getOccId()) == -1){
//								brother = bro;
//								break;
//							}
//						}
//					}
					if(xmlNode.getListNode()!=null){
						for(CmTreeNode bro:xmlNode.getListNode()){
							brother = bro;
							break;

						}
					}else{
						brother = xmlNode;
						brother.setListNode(null);
					}
					if(null != brother){
						String occpath = brother.getOccpath();
						brother.setOccId("-1");
						brother.setOccpath("-1");
						brother.getPart().setParentPartNumber("");
//						CmBizObjUtil.rebuildOccpath(brother);
						rebuildOccpathFromTreeToXML(brother,occpath);
						saveTreeToBytes(brother);
					}
//				}
			}else if (cmnode.children().hasMoreElements() && CmCommonNodeUtil.checkNodeHasStructure(cmnode)) {
				//saveBrotherElement(node.getListNode(), cmnode, e1);
				if (CmCommonStringUtil.isPackage(cmnode)) {
					saveBrotherElement(node.getListNode(), cmnode, e1);
					createPackageElement(cmnode, e1);
				}else if(CmCommonStringUtil.isPackageOfParent(cmnode)){
					saveBrotherElement(node.getListNode(), cmnode, e1);
					createChildElement(cmnode, e1);
				}
				else {
					createChildElement(cmnode, e1);
				}
				childs.add(e1);
			}else if("assistant".equals(cmnode.getPart().getPartType())){
				childs.add(e1);
			}else{
				saveBrotherElement(node.getListNode(), cmnode, e1);
				if (cmnode.children().hasMoreElements()) {
					Element ele = DocumentHelper.createElement(CHILDS);
					Enumeration childrens = cmnode.children();
					while (childrens.hasMoreElements()) {
						CmTreeNode assist = (CmTreeNode) childrens.nextElement();
						Element e2 = addElementAttribute(assist);
						ele.add(e2);
					}
					e1.add(ele);
				}
				childs.add(e1);
			}
		}
		element.add(childs);
	}
	/**
	 * 将打包中父节点中的相同位置的兄弟节点的occid信息保存
	 *
	 * @author chenyunlong
	 * @date 2013-4-10
	 * @param list
	 * @param obj
	 * @param e
	 *
	 */
	@SuppressWarnings("unchecked")
	public void saveBrotherElement(List<CmTreeNode> list, CmTreeNode obj, Element e) {
		List<CmTreeNode> parentlis = new ArrayList<CmTreeNode>();
		getParentPackage((CmTreeNode)obj.getParent(),parentlis);
		saveBrotherElementOfCommonLevel(list,obj,e);
		if(parentlis.size()>1){
			int level = parentlis.size()-1;
			CmTreeNode objparent = (CmTreeNode) obj.getParent();
			for(int m=0;m<parentlis.size();m++){
				objparent = (CmTreeNode) objparent.getParent();
			}
			if(CmCommonNodeUtil.checkNodeIsCommon(objparent, parentlis.get(level-1))){
				if(CmCommonStringUtil.isPackage(objparent)){
					for(int i =0;i<objparent.getListNode().size();i++){
						saveBrotherElement(objparent.getListNode().get(i),e,obj,parentlis,true);
					}
				}
			}else{
				Enumeration objchildren = objparent.children();
				while(objchildren.hasMoreElements()){
					CmTreeNode child = (CmTreeNode) objchildren.nextElement();
					if(level >0 && CmCommonNodeUtil.checkNodeIsCommon(child, parentlis.get(level-1))){
						level--;
						if(level==0){
							if(CmCommonStringUtil.isPackage(child)){
								for(int i =0;i<child.getListNode().size();i++){
									saveBrotherElement(child.getListNode().get(i),e,obj,parentlis,true);
								}
							}
						}
					}
				}
			}
		}
		if(null !=parentlis && parentlis.size()>0){
			CmTreeNode parent = (CmTreeNode) parentlis.get(parentlis.size()-1);
			if(parentlis.size()>1){
				e.setAttributeValue("occId", e.attributeValue("occId") + "&" + parent.getPart().getPartNumber() + "&");
				e.setAttributeValue("occpath", e.attributeValue("occpath") + "&" + parent.getPart().getPartNumber() + "&");
				e.setAttributeValue("middleIndex", e.attributeValue("middleIndex") + "&" + parent.getPart().getPartNumber() + "&");
			}

			for (int j = 0; j < parent.getListNode().size(); j++) {
				if (parentlis.size() > 1) {
					saveBrotherElementWhthLevels(obj, parentlis.size() - 1, parent.getListNode().get(j), parentlis, e);
					if (j < parent.getListNode().size() - 1) {
						e.setAttributeValue("occId", e.attributeValue("occId") + "&" + parent.getListNode().get(j).getPart().getPartNumber() + "&");
						e.setAttributeValue("occpath", e.attributeValue("occpath") + "&" + parent.getListNode().get(j).getPart().getPartNumber() + "&");
						e.setAttributeValue("middleIndex", e.attributeValue("middleIndex") + "&" + parent.getListNode().get(j).getPart().getPartNumber() + "&");
					}
				} else {
					CmTreeNode common = null;
					common = getCommonNodeOfCommonLevel(parent.getListNode().get(j), (CmTreeNode) obj.getParent(), parentlis, parentlis.size(), 0, common);
					if (null != common) {
						Element come = null;
						Enumeration children = common.children();
						while (children.hasMoreElements()) {
							CmTreeNode child = (CmTreeNode) children.nextElement();
							if (CmCommonNodeUtil.checkNodeIsCommon(obj, child)) {
								come = addElementAttribute(child);
								saveBrotherElementOfCommonLevel(common.getListNode(), obj, come);
							}
						}
						e.setAttributeValue("occId", e.attributeValue("occId") + "&" + parent.getListNode().get(j).getPart().getPartNumber() + "&" + come.attributeValue("occId"));
						e.setAttributeValue("occpath", e.attributeValue("occpath") + "&" + parent.getListNode().get(j).getPart().getPartNumber() + "&" + come.attributeValue("occpath"));
						e.setAttributeValue("middleIndex", e.attributeValue("middleIndex") + "&" + parent.getListNode().get(j).getPart().getPartNumber() + "&" + come.attributeValue("middleIndex"));
					}
				}
			}
		}
	}

	public void saveBrotherElementWhthLevels(CmTreeNode obj,int level,CmTreeNode parent,List<CmTreeNode> list,Element e){
		Enumeration children = parent.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(level>=1)
			if(CmCommonNodeUtil.checkNodeIsCommon(child, list.get(level-1))){
				level--;
				if(level==0){
					saveBrotherElement(child,e,obj,list,false);
					if(CmCommonStringUtil.isPackage(child)){
						for(int i =0;i<child.getListNode().size();i++){
							saveBrotherElement(child.getListNode().get(i),e,obj,list,true);
						}
					}
				}
			}
		}
	}

	public void saveBrotherElement(CmTreeNode child,Element e,CmTreeNode obj,List<CmTreeNode> list,boolean flag){
		CmTreeNode common = null;
		common = getCommonNodeOfCommonLevel(child,(CmTreeNode)obj.getParent(),list,list.size()-1,0,common);
		Element come = null;
		Enumeration comchildren = common.children();
		while(comchildren.hasMoreElements()){
			CmTreeNode comchild = (CmTreeNode)comchildren.nextElement();
			if(CmCommonStringUtil.isCommon(obj,comchild)){
				come = addElementAttribute(comchild);
				saveBrotherElementOfCommonLevel(common.getListNode(),obj,come);
			}
		}
			if (flag) {
				e.setAttributeValue("occId", e.attributeValue("occId") + "&" + child.getPart().getPartNumber() + "&" + come.attributeValue("occId"));
				e.setAttributeValue("occpath", e.attributeValue("occpath") + "&" + child.getPart().getPartNumber() + "&" + come.attributeValue("occpath"));
				e.setAttributeValue("middleIndex", e.attributeValue("middleIndex") + "&" + child.getPart().getPartNumber() + "&" + come.attributeValue("middleIndex"));

			} else {
				e.setAttributeValue("occId", e.attributeValue("occId") + come.attributeValue("occId"));
				e.setAttributeValue("occpath", e.attributeValue("occpath") + come.attributeValue("occpath"));
				e.setAttributeValue("middleIndex", e.attributeValue("middleIndex") + come.attributeValue("middleIndex"));
			}
	}

	public void saveBrotherElementOfCommonLevel(List<CmTreeNode> list, CmTreeNode obj, Element e) {
		String occid = e.attributeValue("occId");
		String occpath = e.attributeValue("occpath");
		String middleIndex = e.attributeValue("middleIndex");

		if(null != list && list.size()>0){
			for (CmTreeNode cmnode : list) {
				CmTreeNode bro = getBrotherNodeFromParentPackage(cmnode, obj);
				if(null != bro){
					String broOccid = bro.getOccId();
					String broOccpath = bro.getOccpath();
					String broMiddleIndex = CmCommonStringUtil.isEmpty(bro.getPart().getMiddleIndex())?"0":bro.getPart().getMiddleIndex();
					if (!"assistant".equals(bro.getPart().getPartType()) && CmCommonStringUtil.isPackage(bro)) {
						for (CmTreeNode brother : bro.getListNode()) {
							broOccid += "," + brother.getOccId();
							broOccpath += "," + brother.getOccpath();
							if(CmCommonStringUtil.isEmpty(brother.getPart().getMiddleIndex())){
								broMiddleIndex += "," +0;
							}else{
								broMiddleIndex += "," + brother.getPart().getMiddleIndex();
							}
						}
					}
					occid += "&" + cmnode.getPart().getPartNumber() + "&"+broOccid;
					occpath += "&" + cmnode.getPart().getPartNumber() + "&" + broOccpath;
					middleIndex += "&" + cmnode.getPart().getPartNumber() + "&" + broMiddleIndex;
				}
			}
		}
			e.setAttributeValue("occId", occid);
			e.setAttributeValue("occpath", occpath);
			e.setAttributeValue("middleIndex", middleIndex);
	}

	/**
	 * 从打包的父节点中获取相同位置的兄弟节点
	 *
	 * @author chenyunlong
	 * @date 2013-4-10
	 * @param parent
	 * @param obj
	 * @return
	 *
	 */
	@SuppressWarnings("unchecked")
	public CmTreeNode getBrotherNodeFromParentPackage(CmTreeNode parent, CmTreeNode obj) {
		Enumeration children = parent.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if (CmCommonStringUtil.isCommon(child, obj) && isSamePositionWithCommonNOde(obj, child, true)) {
				return child;
			}
			getBrotherNodeFromParentPackage(child, obj);
		}
		return null;
	}

	/**判断两个相同的零件的位置是否完全一样
	 *
	 * @author chenyunlong
	 * @date  2013-4-10
	 * @param node
	 * @param brother
	 * @return
	 *
	 */
	public boolean isSamePositionWithCommonNOde(CmTreeNode node,CmTreeNode brother,boolean flag){
		CmTreeNode nodeP= (CmTreeNode)node.getParent();
		CmTreeNode brotherP= (CmTreeNode)brother.getParent();
		if(null !=brotherP && !CmCommonStringUtil.isCommon(nodeP,brotherP)){
			flag=false;
		}
		else if(null !=brotherP && CmCommonStringUtil.isCommon(nodeP,brotherP)){
			flag = isSamePositionWithCommonNOde(nodeP,brotherP,flag);
		}
		return flag;
	}

	/**
	 * 获取最近的打包父节点
	 * @author chenyunlong
	 * @date  2013-4-26
	 * @param node
	 * @param i
	 * @param list
	 * @return
	 *
	 */
	public List<CmTreeNode> getParentPackage(CmTreeNode node,List<CmTreeNode> list){
		CmTreeNode parent = (CmTreeNode) node.getParent();
		if(null == parent || null == parent.getParent()){
			return null;
		}else if(CmCommonStringUtil.isPackage(parent)){
			if(CmCommonStringUtil.checkNodeIsSame((CmTreeNode) node.getParent(), parent)){
				list.add(parent);
			}else{
				while(!CmCommonStringUtil.checkNodeIsSame((CmTreeNode) node.getParent(), parent)){
					list.add((CmTreeNode) node.getParent());
				}
			}
			getParentPackage(parent,list);
		}
		else{
			getParentPackage(parent,list);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public CmTreeNode getCommonNodeOfCommonLevel(CmTreeNode parent,CmTreeNode obj,List<CmTreeNode> list,int i,int j,CmTreeNode common){
		Enumeration children = parent.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(checkHasCommonParent(parent, list.get(i-1)) && CmCommonStringUtil.isCommon(child, obj)){
				common = child;
				break;
			}else if(CmCommonStringUtil.isCommon(child, list.get(j))){
				i--;
				j++;
				common = getCommonNodeOfCommonLevel(child,obj,list,i,j,common);
			}
			else if(CmCommonNodeUtil.checkNodeHasStructure(child)){
				common = getCommonNodeOfCommonLevel(child,obj,list,i,j,common);
			}
		}
		return common;
	}

	public boolean checkHasCommonParent(CmTreeNode parent,CmTreeNode packagenode){
		if(null==parent.getParent() && "PBOM".equals(parent.toString())){
			return false;
		}else if(CmCommonNodeUtil.checkNodeIsCommon(parent, packagenode)){
			return true;
		}else{
			return checkHasCommonParent((CmTreeNode)parent.getParent(),packagenode);
		}
	}

	@SuppressWarnings("unchecked")
	public CmTree getBytesToTree(byte[] bytes, CmTree ebomTree) {
		if(null != ebomTree){
			getEbomNodeList(ebomTree.getRoot());
		}
		CmTree cmTree = null;
		try {
			CmTreeNode root = new CmTreeNode("PBOM");
			//CmTreeNode childnode = buildTreeWithBytes(bytes);
			CmTreeNode childnode = buildTreeWithBytesFromCache(root, bytes);
//			CmBizObjUtil.addBytesToList(childnode, bytes);
			CmBizObjUtil.addBytesToList(childnode.getPart().getPartNumber(),childnode.getPart().getOid()+"", bytes);
			CmBizObjUtil.addChildNode(childnode);
			root.add(childnode);
			cmTree = new CmTree(root);
			CmCommonStringUtil.sortTheTreeNode(cmTree.getRoot());
			cmTree.updateUI();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(CmContext.getMainFrame(), "xml格式不正确！");
		}
		return cmTree;
	}

	public CmTreeNode buildTreeWithBytesFromCache(CmTreeNode node, byte[] bytes) {
		CmTreeNode cNode = null;
		CmTreeNode dNode = null;
		if (node != null && node.getPart() != null) {
			cNode = CmScrollPaneTree.xmlNodeMap.get(node.getPart().getPartNumber());
		}
		if (cNode == null) {
			cNode = buildTreeWithBytes(bytes);
			CmScrollPaneTree.xmlNodeMap.put(node.getPart().getPartNumber(), cNode);
		}

		try {
			dNode = (CmTreeNode) cNode.deepClone();
		} catch (OptionalDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dNode;
	}

	@SuppressWarnings("unchecked")
	public CmTreeNode buildTreeWithBytes(byte[] bytes){
		Document doc = null;
		CmTreeNode childnode = null;
		try {
			String xml = new String(bytes,"GBK");
			xml = xml.replace("&#1;","");
			xml = xml.replace("&#2;","");
			doc = DocumentHelper.parseText(xml);
			Element rootElement = doc.getRootElement();
			Element parts = getParts(rootElement);
			List<Element> list = parts.elements();
			for (Element e : list) {
				removeChildElement(e);
				String[] occId = e.attributeValue("occId").split(",");
				String[] occpath = e.attributeValue("occpath").split(",");
				String[] middleIndex = e.attributeValue("middleIndex").split(",");
				for (int i = 0; i < occId.length; i++) {
					childnode = addTreeNode(e, occId[i],occpath[i],middleIndex[i]);
					childnode.setOccpath(childnode.getOccId());
//					getMatrixFromEbom(childnode);
					if (!"".equals(childnode.toString())) {
						addChildNode(e, childnode);
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(CmContext.getMainFrame(), "xml格式不正确！");
		}
		return childnode;
	}

	@SuppressWarnings("unchecked")
	public Element getParts(Element doc) {
		Element parts = null;
		if (!doc.getQName().getQualifiedName().equals("parts")) {
			List<Element> list = doc.elements();
			for (Element e : list) {
				if (e.getQName().getQualifiedName().equals("parts")) {
					parts = e;
					break;
				} else {
					getParts(e);
				}
			}
		} else {
			parts = doc;
		}
		return parts;
	}


	/**
	 * 根据节点信息创建Element对象
	 *
	 * @param node
	 * @return
	 */
	public Element addElementAttribute(CmTreeNode node) {
		CmLightPart part = node.getPart();
		String occid = node.getOccId();
		String occpath = node.getOccpath();
		String middleIndex = CmCommonStringUtil.isEmpty(node.getPart().getMiddleIndex())?"0":node.getPart().getMiddleIndex();
		Element e = DocumentHelper.createElement(QMPARTINFO);
		e.addAttribute("oid", part.getOid() == 0 ? "" : String.valueOf(part.getOid()));
		if (!CmCommonStringUtil.isNewNode(part.getPartType())) {
			if (CmCommonStringUtil.isPackage(node)) {
				List<CmTreeNode> list = node.getListNode();
				for (CmTreeNode brotherNode : list) {
					occid += "," + brotherNode.getOccId();
					occpath += "," + brotherNode.getOccpath();
					if(CmCommonStringUtil.isEmpty(brotherNode.getPart().getMiddleIndex())){
						middleIndex += "," + 0;
					}
					else{
						middleIndex += "," + brotherNode.getPart().getMiddleIndex();
					}
				}
				e.addAttribute("useCount", 1 + list.size() + "");
			} else {
				e.addAttribute("useCount", "1");
			}
		} else if ("middle".equals(part.getPartType())) {
			if (CmCommonStringUtil.isPackage(node)) {
				List<CmTreeNode> list = node.getListNode();
				for (CmTreeNode brotherNode : list) {
					occid += "," + brotherNode.getOccId();
					occpath += "," + brotherNode.getOccpath();
					if(CmCommonStringUtil.isEmpty(brotherNode.getPart().getMiddleIndex())){
						middleIndex += "," + 0;
					}
					else{
						middleIndex += "," + brotherNode.getPart().getMiddleIndex();
					}
				}
				e.addAttribute("useCount", 1 + list.size() + "");
			} else {
				e.addAttribute("useCount", "1");
			}
		} else if("assistant".equals(part.getPartType())){
			e.addAttribute("useCount", "1");
		}
		e.addAttribute("productionQuantity", part.getProductionQuantity() + "");
		e.addAttribute("productionRatio", CmCommonStringUtil.emptyToString(part.getProductionRatio()));


		String suboccpath = CmCommonStringUtil.emptyToString(occpath);
		if(suboccpath.length()>CmConnectFrame.OCCPATH_MAX){
			String suboccId = CmCommonStringUtil.emptyToString(occid);
			e.addAttribute("occId",suboccId);
			e.addAttribute("occpath",suboccId);
		}else{
			e.addAttribute("occId", CmCommonStringUtil.emptyToString(occid));
			e.addAttribute("occpath", CmCommonStringUtil.emptyToString(occpath));
		}

		e.addAttribute("partNumber", CmCommonStringUtil.emptyToString(part.getPartNumber()));
		e.addAttribute("parentPartNumber", CmCommonStringUtil.emptyToString(part.getParentPartNumber()));
		e.addAttribute("partName", CmCommonStringUtil.emptyToString(part.getPartName()));
		e.addAttribute("partType", CmCommonStringUtil.emptyToString(part.getPartType()));
		e.addAttribute("dutu", CmCommonStringUtil.emptyToString(part.getDutu()));
		e.addAttribute("remark", CmCommonStringUtil.emptyToString(part.getRemark()));
		e.addAttribute("rate", CmCommonStringUtil.emptyToString(part.getRate()));
		e.addAttribute("isKey", part.isKey() + "");
		e.addAttribute("isSpecial", part.isSpecial() + "");
		e.addAttribute("spaceBorneTable", part.isSpaceBorneTable() + "");
		e.addAttribute("isEbomKey", part.isEbomKey() + "");
		e.addAttribute("materialType", CmCommonStringUtil.emptyToString(part.getMaterialType()));
		e.addAttribute("backupRate", CmCommonStringUtil.emptyToString(part.getBackupRate()));
		e.addAttribute("maxBackupCount", CmCommonStringUtil.emptyToString(part.getMaxBackupCount()));
		e.addAttribute("backupReason", CmCommonStringUtil.emptyToString(part.getBackupReason()));
		e.addAttribute("workShop", CmCommonStringUtil.emptyToString(part.getWorkShop()));
		e.addAttribute("outsourcingUnits", CmCommonStringUtil.emptyToString(part.getOutsourcingUnits()));
		e.addAttribute("materialNumber", CmCommonStringUtil.emptyToString(part.getMaterialNumber()));
		e.addAttribute("materialName", CmCommonStringUtil.emptyToString(part.getMaterialName()));
		e.addAttribute("materialBrand", CmCommonStringUtil.emptyToString(part.getMaterialBrand()));
		e.addAttribute("materialCrision", CmCommonStringUtil.emptyToString(part.getMaterialCrision()));
		e.addAttribute("responser", CmCommonStringUtil.emptyToString(part.getResponser()));
		e.addAttribute("responserGroup", CmCommonStringUtil.emptyToString(part.getResponserGroup()));
		e.addAttribute("lifecycle", CmCommonStringUtil.emptyToString(part.getLifecycle()));
		e.addAttribute("e_lifecycle", CmCommonStringUtil.emptyToString(part.getE_lifecycle()));
		e.addAttribute("e_version", CmCommonStringUtil.emptyToString(part.getE_version()));
		e.addAttribute("eu_number", CmCommonStringUtil.emptyToString(part.getEu_number()));
		e.addAttribute("eu_version", CmCommonStringUtil.emptyToString(part.getEu_version()));
		e.addAttribute("version", CmCommonStringUtil.emptyToString(part.getVersion()));
		e.addAttribute("isChange", part.isChange()+"");
		e.addAttribute("containerId", part.getContainerId() == 0 ? "" : String.valueOf(part.getContainerId()));
		e.addAttribute("middleIndex", middleIndex);
		e.addAttribute("isHasXml",  part.isHasXml()+"");
		e.addAttribute("modifyXmlPartNumber", CmCommonStringUtil.emptyToString(part.getModifyXmlPartNumber()));

		e.addAttribute("structureSaved",  part.isStructureSaved() + "");

		//TODO  添加812 属性 保存
		e.addAttribute("main_plant", CmCommonStringUtil.emptyToString(part.getFirstPlant())); //主车间
		e.addAttribute("secondPlant", CmCommonStringUtil.emptyToString(part.getSecondePlantStr()));//辅助车间
		e.addAttribute("XHPHCL", CmCommonStringUtil.emptyToString(part.getWzk().getInvtype()));
		e.addAttribute("GG", CmCommonStringUtil.emptyToString(part.getWzk().getInvspec()));
		e.addAttribute("ZGFBZHJSTJ", CmCommonStringUtil.emptyToString(part.getWzk().getDef2()));
		e.addAttribute("XXGF", CmCommonStringUtil.emptyToString(part.getWzk().getDef3()));
		e.addAttribute("JLDWMC", CmCommonStringUtil.emptyToString(part.getWzk().getMeasname()));
		e.addAttribute("ZLDJ", CmCommonStringUtil.emptyToString(part.getWzk().getDef4()));
		e.addAttribute("FZXS", CmCommonStringUtil.emptyToString(part.getWzk().getDef5()));
		e.addAttribute("SCCJ", CmCommonStringUtil.emptyToString(part.getWzk().getCustname()));
		e.addAttribute("XXGF", CmCommonStringUtil.emptyToString(part.getWzk().getDef3()));
		e.addAttribute("MPCC", CmCommonStringUtil.emptyToString(part.getWzk().getMpcc()));
		e.addAttribute("CLBM", CmCommonStringUtil.emptyToString(part.getWzk().getClcode()));
		e.addAttribute("CMAT", CmCommonStringUtil.emptyToString(part.getWzk().getClName()));
		e.addAttribute("WZBM", CmCommonStringUtil.emptyToString(part.getWzk().getInvcode()));
		e.addAttribute("WZMC", CmCommonStringUtil.emptyToString(part.getWzk().getInvname()));
		e.addAttribute("ZXSL", CmCommonStringUtil.emptyToString( part.getWzk().getZxsl()));
		e.addAttribute("WZLB", CmCommonStringUtil.emptyToString( part.getWzk().getWzlb()));

		e.addAttribute("CLFLBM", CmCommonStringUtil.emptyToString( part.getWzk().getInvclasscode())); //材料

		//begin 149
		e.addAttribute("CMAT", CmCommonStringUtil.emptyToString(part.getCmat()));
		e.addAttribute("PTC_MATERIAL_NAME", CmCommonStringUtil.emptyToString(part.getPtc_material_name()));
		e.addAttribute("CMAT_UP", CmCommonStringUtil.emptyToString(part.getCmat_up()));
		e.addAttribute("CMAT_DOWN", CmCommonStringUtil.emptyToString(part.getCmat_down()));
		e.addAttribute("SETMARK", CmCommonStringUtil.emptyToString(part.getSetmark()));
		e.addAttribute("ADJUSTABLE", CmCommonStringUtil.emptyToString(part.getAdjustable()));
		e.addAttribute("PHASE_CODE", CmCommonStringUtil.emptyToString(part.getPhase_code()));
		e.addAttribute("KEYCOMPONENT", CmCommonStringUtil.emptyToString(part.getKeycomponent()));
		e.addAttribute("CSIZE", CmCommonStringUtil.emptyToString(part.getCsize()));
		e.addAttribute("CTYPE", CmCommonStringUtil.emptyToString(part.getCtype()));
		e.addAttribute("SECRET", CmCommonStringUtil.emptyToString(part.getSecret()));
		e.addAttribute("ENDITEMIN", CmCommonStringUtil.emptyToString(part.getEnditemin()));
		e.addAttribute("MINDEX", CmCommonStringUtil.emptyToString(part.getMindex()));
		e.addAttribute("CINDEX", CmCommonStringUtil.emptyToString(part.getCindex()));
		e.addAttribute("PTC_COMMON_NAME", CmCommonStringUtil.emptyToString(part.getPtc_common_name()));
		e.addAttribute("ROUTING", CmCommonStringUtil.emptyToString( part.getRouting()));
		e.addAttribute("COMPANY", CmCommonStringUtil.emptyToString( part.getCompany()));
		e.addAttribute("PRODUCT_INDEX", CmCommonStringUtil.emptyToString(part.getProduct_index()));
		e.addAttribute("DESIGNER", CmCommonStringUtil.emptyToString(part.getDesigner()));
		e.addAttribute("ZZCJ", CmCommonStringUtil.emptyToString(part.getZzcj()));
		e.addAttribute("FZCJ", CmCommonStringUtil.emptyToString(part.getFzcj()));
		e.addAttribute("MATERIAL", CmCommonStringUtil.emptyToString(part.getMaterial()));
		e.addAttribute("PZGGBZH", CmCommonStringUtil.emptyToString(part.getPzggbzh()));
		e.addAttribute("JSTJBZH", CmCommonStringUtil.emptyToString(part.getJstjbzh()));
		e.addAttribute("JDDJ", CmCommonStringUtil.emptyToString(part.getJddj()));
		e.addAttribute("ZLDJ", CmCommonStringUtil.emptyToString(part.getZldj()));
		e.addAttribute("CLZT", CmCommonStringUtil.emptyToString(part.getClzt()));
		e.addAttribute("CLDW", CmCommonStringUtil.emptyToString(part.getCldw()));
		e.addAttribute("ZQCLMC", CmCommonStringUtil.emptyToString(part.getZqclmc()));
		e.addAttribute("JBCLMC", CmCommonStringUtil.emptyToString(part.getJbclmc()));
		e.addAttribute("ZQCLBZH", CmCommonStringUtil.emptyToString(part.getZqclbzh()));
		e.addAttribute("PINDEX", CmCommonStringUtil.emptyToString( part.getPindex()));
		e.addAttribute("CHBM", CmCommonStringUtil.emptyToString(part.getWzk().getInvcode()));
		e.addAttribute("CHMC", CmCommonStringUtil.emptyToString(part.getWzk().getInvname()));
		e.addAttribute("MTYPE", CmCommonStringUtil.emptyToString(part.getMtype()));
		e.addAttribute("XHPH", CmCommonStringUtil.emptyToString(part.getXhph()));
		e.addAttribute("JSTJ", CmCommonStringUtil.emptyToString(part.getJstj()));
		e.addAttribute("BATCH", CmCommonStringUtil.emptyToString(part.getBatch()));
		//end

		e.addAttribute("gysl", CmCommonStringUtil.emptyToString(part.getGysl()));
		e.addAttribute("usecount_805", CmCommonStringUtil.emptyToString(part.getUseCount_805()));

		//设计资源库应用改造新增属性
		//start
		e.addAttribute("SHORTNAME", CmCommonStringUtil.emptyToString(part.getShortname()));//物资简称
		e.addAttribute("YXJB", CmCommonStringUtil.emptyToString(part.getYxjb()));//编码优选级别
		e.addAttribute("BMZT", CmCommonStringUtil.emptyToString(part.getBmzt()));//编码状态
		e.addAttribute("BMLX", CmCommonStringUtil.emptyToString(part.getBmlx()));//编码类型
		e.addAttribute("STANDARDNUMBER", CmCommonStringUtil.emptyToString(part.getStandardnumber()));//标准号
		e.addAttribute("MECHANICALPROPERTYORHARDNESS", CmCommonStringUtil.emptyToString(part.getMechanicalpropertyorhardness()));//机械性能等级或硬度
		e.addAttribute("SURFACETREATMENT", CmCommonStringUtil.emptyToString(part.getSurfacetreatment()));//表面处理
		e.addAttribute("HEATTREATMENT", CmCommonStringUtil.emptyToString(part.getHeattreatment()));//热处理
		e.addAttribute("PRODUCTFORM", CmCommonStringUtil.emptyToString(part.getProductform()));//产品型式
		e.addAttribute("PRODUCTLEVEL", CmCommonStringUtil.emptyToString(part.getProductlevel()));//产品等级
		e.addAttribute("PLATECSCREWFORM", CmCommonStringUtil.emptyToString(part.getPlatecscrewform()));//板拧形式
		e.addAttribute("ISIMPORT", CmCommonStringUtil.emptyToString(part.getIsimport()));//是否进口
		e.addAttribute("SPECIALINSTRUCTION", CmCommonStringUtil.emptyToString(part.getSpecialinstruction()));//特殊说明
		e.addAttribute("MEASUREUNIT", CmCommonStringUtil.emptyToString(part.getMeasureunit()));//计量单位
		e.addAttribute("TYPE", CmCommonStringUtil.emptyToString(part.getType()));//型号
		e.addAttribute("TYPESTANDARD", CmCommonStringUtil.emptyToString(part.getTypestandard()));//型号规格
		e.addAttribute("QUALITYLEVEL", CmCommonStringUtil.emptyToString(part.getQualitylevel()));//质量等级
		e.addAttribute("TOTALSTANDARD", CmCommonStringUtil.emptyToString(part.getTotalstandard()));//总规范
		e.addAttribute("DETAILSTANDARD", CmCommonStringUtil.emptyToString(part.getDetailstandard()));//详细规范
		e.addAttribute("PACKAGINGFORM", CmCommonStringUtil.emptyToString(part.getPackagingform()));//封装形式
		e.addAttribute("OUTLINESIZE", CmCommonStringUtil.emptyToString(part.getOutlinesize()));//外形尺寸
		e.addAttribute("SPECIALCONDITION", CmCommonStringUtil.emptyToString(part.getSpecialcondition()));//专用条件
		e.addAttribute("EXTRACONDITION", CmCommonStringUtil.emptyToString(part.getExtracondition()));//附加协议
		e.addAttribute("MATTYPE", CmCommonStringUtil.emptyToString(part.getMattype()));//材料类型
		e.addAttribute("NUMBER", CmCommonStringUtil.emptyToString(part.getOutlinesize()));//材料编号
		e.addAttribute("MARKNUMBER", CmCommonStringUtil.emptyToString(part.getSpecialcondition()));//牌号
		e.addAttribute("SUPPLYSTATE", CmCommonStringUtil.emptyToString(part.getExtracondition()));//供应状态
		e.addAttribute("USESTANDARD", CmCommonStringUtil.emptyToString(part.getMattype()));//采用标准
		//end

		return e;
	}

	/**
	 *
	 * 将inputStream转换成byte数组
	 *
	 * @param is
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public String inputStreamToString(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = br.readLine()) != null) {
			buffer.append(line);
		}
		return buffer.toString();
	}

	@SuppressWarnings("unchecked")
	public boolean isHasChildNode(InputStream is) {
		Document doc = null;
		try {
			String xml = inputStreamToString(is);
			//System.out.println(xml);
			doc = DocumentHelper.parseText(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Element rootElement = doc.getRootElement();
		List<Element> list = rootElement.elements();
		return list.size() > 0 ? true : false;
	}

	public CmTreeNode addTreeNode(Element e, String occId,String occpath,String middleIndex) {
		CmLightPart part = new CmLightPart();
		String oid = e.attributeValue("oid");
		String partNumber = e.attributeValue("partNumber");
		//System.out.println("---addTreeNode----------partNumber:"+partNumber+"  occId:"+occId+"  occpath:"+occpath+"  middleIndex:"+middleIndex);
//		WTPart newPart  = null;
//		WTPart newePart  = null;
//		try {
//			 WTPartMaster master = 	WTPartUtil.getWTPartMasterByNumber(partNumber);
//			 newPart = WTPartUtil.getLatestManufacturingPartByMaster(master);
//			 newePart = WTPartUtil.getLatestDesinPartByMaster(master);
//		} catch (WTException e1) {
//			e1.printStackTrace();
//		}
		String partName = e.attributeValue("partName");
		if(CmScrollPaneTree.ebomMap.get(partNumber)!=null){
			String epartName = CmScrollPaneTree.ebomMap.get(partNumber).getName();
			if(epartName!=null &&!"".equals(epartName)&&!epartName.equals(partName)){
				partName = epartName;
			}
		}


		part.setOid(CmCommonStringUtil.emptyToLong(oid));
		part.setPartNumber(partNumber);
		part.setParentPartNumber(e.attributeValue("parentPartNumber"));
		part.setPartName(partName);
		part.setPartType(e.attributeValue("partType"));
		part.setDutu(e.attributeValue("dutu"));
		part.setRemark(e.attributeValue("remark"));
		part.setUseCount(Integer.parseInt(e.attributeValue("useCount")));

		//工艺数量

		String gysl = e.attributeValue("gysl");
//		String usecount = CmScrollPaneTree.ebomMap.get(partNumber).getUsecount();
		if(null == gysl || "".equals(gysl)) {
			//part.setGysl(e.attributeValue("useCount"));
			if(CmScrollPaneTree.ebomMap.get(partNumber)!=null){
				String usecount = CmScrollPaneTree.ebomMap.get(partNumber).getUsecount();
				if(null==usecount){
					usecount="";
				}
				part.setGysl(usecount);
			}else{
				part.setGysl(e.attributeValue("useCount"));
			}

		} else {
			part.setGysl(gysl);
		}
		String usecount_805=e.attributeValue("usecount_805");
		if(CmScrollPaneTree.ebomMap.get(partNumber)!=null){
		   String usecount = CmScrollPaneTree.ebomMap.get(partNumber).getUsecount();
		   if(null==usecount || "null".equals(usecount) || "".equals(usecount)){
			   part.setUseCount_805(usecount_805);
		   }else{
			   part.setUseCount_805(usecount);
		   }
		}else{
			part.setUseCount_805(usecount_805);
		}
//		if(null==usecount_805 || "null".equals(usecount_805) || "".equals(usecount_805)){
//			if(null!=CmScrollPaneTree.ebomMap.get(partNumber)){
//				String usecount = CmScrollPaneTree.ebomMap.get(partNumber).getUsecount();
//				if(null==usecount){
//					usecount="";
//				}
//				part.setUseCount_805(usecount);
//			}else{
//				part.setUseCount_805(e.attributeValue("useCount"));
//			}
//		}else{
//		    part.setUseCount_805(usecount_805);
//		}
		part.setProductionQuantity(Integer.parseInt(e.attributeValue("productionQuantity")));
		part.setProductionRatio(e.attributeValue("productionRatio"));
		part.setRate(e.attributeValue("rate"));
		part.setKey(Boolean.valueOf(e.attributeValue("isKey")));
		part.setSpecial(Boolean.valueOf(e.attributeValue("isSpecial")));
		part.setSpaceBorneTable(Boolean.valueOf(e.attributeValue("spaceBorneTable")));
		part.setEbomKey(Boolean.valueOf(e.attributeValue("isEbomKey")));
		part.setMaterialType(e.attributeValue("materialType"));
		part.setBackupRate(e.attributeValue("backupRate"));
		part.setMaxBackupCount(e.attributeValue("maxBackupCount"));
		part.setBackupReason(e.attributeValue("backupReason"));
		part.setWorkShop(e.attributeValue("workShop"));
		part.setOutsourcingUnits(e.attributeValue("outsourcingUnits"));
		part.setMaterialNumber(e.attributeValue("materialNumber"));
		part.setMaterialName(e.attributeValue("materialName"));
		part.setMaterialBrand(e.attributeValue("materialBrand"));
		part.setMaterialCrision(e.attributeValue("materialCrision"));
		part.setResponser(e.attributeValue("responser"));
		part.setResponserGroup(e.attributeValue("responserGroup"));
		part.setContainerId(CmCommonStringUtil.emptyToLong(e.attributeValue("containerId")));
		part.setE_version(e.attributeValue("e_version"));
		part.setEu_number(e.attributeValue("eu_number"));
		part.setEu_version(e.attributeValue("eu_version"));
		part.setVersion(e.attributeValue("version"));
		part.setChange(Boolean.valueOf(e.attributeValue("isChange")));
		part.setMiddleIndex(middleIndex);
		part.setHasXml(Boolean.valueOf(e.attributeValue("isHasXml")));
		part.setModifyXmlPartNumber(e.attributeValue("modifyXmlPartNumber"));

		part.setStructureSaved(Boolean.valueOf(e.attributeValue("structureSaved")));

		//TODO 812
		part.getWzk().setInvtype(e.attributeValue("XHPHCL"));
		part.getWzk().setInvspec(e.attributeValue("GG"));
		part.getWzk().setDef2(e.attributeValue("ZGFBZHJSTJ"));
		part.getWzk().setDef3(e.attributeValue("XXGF"));
		part.getWzk().setMeasname(e.attributeValue("JLDWMC"));
		part.getWzk().setDef4(e.attributeValue("ZLDJ"));
		part.getWzk().setDef5(e.attributeValue("FZXS"));
		part.getWzk().setCustname(e.attributeValue("SCCJ"));
		part.getWzk().setMpcc(e.attributeValue("MPCC"));
		part.getWzk().setClcode(e.attributeValue("CLBM"));
		part.getWzk().setClName(e.attributeValue("CMAT"));
		part.getWzk().setInvcode(e.attributeValue("WZBM"));
		part.getWzk().setInvname(e.attributeValue("WZMC"));
		part.getWzk().setZxsl(e.attributeValue("ZXSL"));
		part.getWzk().setWzlb(e.attributeValue("WZLB"));
		part.setFirstPlant(e.attributeValue("main_plant"));
		part.getWzk().setInvclasscode(e.attributeValue("CLFLBM"));

		//begin 149
		part.setCmat(e.attributeValue("CMAT"));
		part.setPtc_material_name(e.attributeValue("PTC_MATERIAL_NAME"));
		part.setCmat_up(e.attributeValue("CMAT_UP"));
		part.setCmat_down(e.attributeValue("CMAT_DOWN"));
		part.setSetmark(e.attributeValue("SETMARK"));
		part.setAdjustable(e.attributeValue("ADJUSTABLE"));
		part.setPhase_code(e.attributeValue("PHASE_CODE"));
		part.setKeycomponent(e.attributeValue("KEYCOMPONENT"));
		part.setCsize(e.attributeValue("CSIZE"));
		part.setCtype(e.attributeValue("CTYPE"));
		part.setSecret(e.attributeValue("SECRET"));
		part.setEnditemin(e.attributeValue("ENDITEMIN"));
		part.setMindex(e.attributeValue("MINDEX"));
		part.setCindex(e.attributeValue("CINDEX"));
		part.setPtc_common_name(e.attributeValue("PTC_COMMON_NAME"));
		part.setRouting(e.attributeValue("ROUTING"));
		part.setCompany(e.attributeValue("COMPANY"));
		part.setProduct_index(e.attributeValue("PRODUCT_INDEX"));
		part.setDesigner(e.attributeValue("DESIGNER"));
		part.setZzcj(e.attributeValue("ZZCJ"));
		part.setFzcj(e.attributeValue("FZCJ"));
		part.setMaterial(e.attributeValue("MATERIAL"));
		part.setPzggbzh(e.attributeValue("PZGGBZH"));
		part.setJstjbzh(e.attributeValue("JSTJBZH"));
		part.setJddj(e.attributeValue("JDDJ"));
		part.setZldj(e.attributeValue("ZLDJ"));
		part.setClzt(e.attributeValue("CLZT"));
		part.setCldw(e.attributeValue("CLDW"));
		part.setZqclmc(e.attributeValue("ZQCLMC"));
		part.setZqclbzh(e.attributeValue("ZQCLBZH"));
		part.setJbclmc(e.attributeValue("JBCLMC"));
		part.setPindex(e.attributeValue("PINDEX"));
		part.setMtype(e.attributeValue("MTYPE"));
		part.setXhph(e.attributeValue("XHPH"));
		part.setJstj(e.attributeValue("JSTJ"));
		part.setBatch(e.attributeValue("BATCH"));
		//end

		//设计资源库应用改造新增属性
		//start
		part.setShortname(e.attributeValue("SHORTNAME"));//物资简称
		part.setYxjb(e.attributeValue("YXJB"));//编码优选级别
		part.setBmzt(e.attributeValue("BMZT"));//编码状态
		part.setBmlx(e.attributeValue("BMLX"));//编码类型
		part.setStandardnumber(e.attributeValue("STANDARDNUMBER"));//标准号
		part.setMechanicalpropertyorhardness(e.attributeValue("MECHANICALPROPERTYORHARDNESS"));//机械性能等级或硬度
		part.setSurfacetreatment(e.attributeValue("SURFACETREATMENT"));//表面处理
		part.setHeattreatment(e.attributeValue("HEATTREATMENT"));//热处理
		part.setProductform(e.attributeValue("PRODUCTFORM"));//产品型式
		part.setProductlevel(e.attributeValue("PRODUCTLEVEL"));//产品等级
		part.setPlatecscrewform(e.attributeValue("PLATECSCREWFORM"));//板拧形式
		part.setIsimport(e.attributeValue("ISIMPORT"));//是否进口
		part.setSpecialinstruction(e.attributeValue("SPECIALINSTRUCTION"));//特殊说明
		part.setMeasureunit(e.attributeValue("MEASUREUNIT"));//计量单位
		part.setType(e.attributeValue("TYPE"));//型号
		part.setTypestandard(e.attributeValue("TYPESTANDARD"));//型号规格
		part.setQualitylevel(e.attributeValue("QUALITYLEVEL"));//质量等级
		part.setTotalstandard(e.attributeValue("TOTALSTANDARD"));//总规范
		part.setDetailstandard(e.attributeValue("DETAILSTANDARD"));//详细规范
		part.setPackagingform(e.attributeValue("PACKAGINGFORM"));//封装形式
		part.setOutlinesize(e.attributeValue("OUTLINESIZE"));//外形尺寸
		part.setSpecialcondition(e.attributeValue("SPECIALCONDITION"));//专用条件
		part.setExtracondition(e.attributeValue("EXTRACONDITION"));//附加协议
		part.setMattype(e.attributeValue("MATTYPE"));//材料类型
		part.setCmatnumber(e.attributeValue("NUMBER"));//材料编号
		part.setMarknumber(e.attributeValue("MARKNUMBER"));//牌号
		part.setSupplystate(e.attributeValue("SUPPLYSTATE"));//供应状态
		part.setUsestandard(e.attributeValue("USESTANDARD"));//采用标准
		//end
		String fzbm = e.attributeValue("FZCJ");

		HashMap<String,String> arrts = CmConnectFrame.partAttrsFromWNC.get(part.getPartNumber());
		if(arrts==null){
			try {
				WTPart p = (WTPart) CmSearchHelper.search(WTPart.class,part.getPartNumber());
				if(p!=null){
					arrts = new HashMap<String,String>();
					CmIBAHelper helper = new CmIBAHelper(p);
					part.setZzcj(helper.getIBAValue("ZZCJ"));
					arrts.put("ZZCJ", part.getZzcj());

					fzbm = helper.getIBAValue("FZCJ");
					part.setFzcj(fzbm);
					arrts.put("FZCJ", fzbm);

					String MTYPE = helper.getIBAValue("MTYPE");
					part.setMtype(MTYPE);
					arrts.put("MTYPE",part.getMtype());

					part.setPindex(helper.getIBAValue("PINDEX"));
					arrts.put("PINDEX", part.getPindex());

					part.setCindex(helper.getIBAValue("CINDEX"));
					arrts.put("CINDEX", part.getCindex());

					part.setMindex(helper.getIBAValue("MINDEX"));
					arrts.put("MINDEX", part.getMindex());

					part.setPhase_code(helper.getIBAValue("PHASE_CODE"));
					arrts.put("PHASE_CODE", part.getPhase_code());

					String ctype = helper.getIBAValue("CTYPE");
					arrts.put("CTYPE",ctype);
					if(MTYPE==null||"".equals(MTYPE)){
						if("标准件".equals(ctype)){
							part.setMtype("标准件");
						}
					}


					CmConnectFrame.partAttrsFromWNC.put(part.getPartNumber(), arrts);
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else{
			part.setZzcj(arrts.get("ZZCJ"));
			part.setFzcj(arrts.get("FZCJ"));
			fzbm = arrts.get("FZCJ");
			String MTYPE = arrts.get("MTYPE");
			part.setMtype(MTYPE);
			part.setPindex(arrts.get("PINDEX"));
			part.setCindex(arrts.get("CINDEX"));
			part.setMindex(arrts.get("MINDEX"));
			part.setPhase_code(arrts.get("PHASE_CODE"));

			if(MTYPE==null||"".equals(MTYPE)){
				String ctype = arrts.get("CTYPE");
				if("标准件".equals(ctype)){
					part.setMtype("标准件");
				}
			}
		}




		String[] plants = LoadConfig.getInstance().getMainPlant();

		Map<String,Boolean> plantMap = new HashMap<String,Boolean>();



//		if(newPart!=null){ //覆盖  xml 中的属性
//			CmIBAHelper  helper = new CmIBAHelper(newPart);
//
//			part.setPartNumber(e.attributeValue("partNumber"));
//			part.setPartName(e.attributeValue("partName"));
//
////			part.setE_version(e.attributeValue("e_version"));
////			part.setEu_number(e.attributeValue("eu_number"));
////			part.setEu_version(e.attributeValue("eu_version"));
//
//			try {
//				part.setOid(PersistenceHelper.getObjectIdentifier(newPart).getId());
//				part.setVersion(newPart.getVersionIdentifier().getValue() + "." + newPart.getIterationIdentifier().getValue());
//				part.setFzcj(fzbm);
//
//				//begin 149
//				part.setCmat(helper.getIBAValue(newPart,"CMAT"));
//				part.setPtc_material_name(helper.getIBAValue(newPart,"PTC_MATERIAL_NAME"));
//				part.setCmat_up(helper.getIBAValue(newPart,"CMAT_UP"));
//				part.setCmat_down(helper.getIBAValue(newPart,"CMAT_DOWN"));
//				part.setSetmark(helper.getIBAValue(newPart,"SETMARK"));
//				part.setPhase_code(helper.getIBAValue(newPart,"PHASE_CODE"));
//				part.setKeycomponent(helper.getIBAValue(newPart,"KEYCOMPONENT"));
//				part.setCsize(helper.getIBAValue(newPart,"CSIZE"));
//				part.setCtype(helper.getIBAValue(newPart,"CTYPE"));
//				part.setSecret(helper.getIBAValue(newPart,"SECRET"));
//				part.setEnditemin(helper.getIBAValue(newPart,"ENDITEMIN"));
//				part.setMindex(helper.getIBAValue(newPart,"MINDEX"));
//				part.setCindex(helper.getIBAValue(newPart,"CINDEX"));
//				part.setPtc_common_name(helper.getIBAValue(newPart,"PTC_COMMON_NAME"));
//				part.setRouting(helper.getIBAValue(newPart,"ROUTING"));
//				part.setCompany(helper.getIBAValue(newPart,"COMPANY"));
//				part.setProduct_index(helper.getIBAValue(newPart,"PRODUCT_INDEX"));
//				part.setDesigner(helper.getIBAValue(newPart,"DESIGNER"));
//				part.setZzcj(helper.getIBAValue(newPart,"ZZCJ"));
//				part.setFzcj(helper.getIBAValue(newPart,"FZCJ"));
//				part.setMaterial(helper.getIBAValue(newPart,"MATERIAL"));
//				part.setPzggbzh(helper.getIBAValue(newPart,"PZGGBZH"));
//				part.setJstjbzh(helper.getIBAValue(newPart,"JSTJBZH"));
//				part.setJddj(helper.getIBAValue(newPart,"JDDJ"));
//				part.setZldj(helper.getIBAValue(newPart,"ZLDJ"));
//				part.setClzt(helper.getIBAValue(newPart,"CLZT"));
//				part.setCldw(helper.getIBAValue(newPart,"CLDW"));
//				part.setZqclmc(helper.getIBAValue(newPart,"ZQCLMC"));
//				part.setZqclbzh(helper.getIBAValue(newPart,"ZQCLBZH"));
//				part.setJbclmc(helper.getIBAValue(newPart,"JBCLMC"));
//				part.setPindex(helper.getIBAValue(newPart,"PINDEX"));
//				part.setMtype(helper.getIBAValue(newPart,"MTYPE"));
//				part.setXhph(helper.getIBAValue(newPart,"XHPH"));
//				part.setJstj(helper.getIBAValue(newPart,"JSTJ"));
//				part.setBatch(helper.getIBAValue(newPart,"BATCH"));
//				//end
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//		}

		for(int i=0;i<plants.length;i++){
			plantMap.put(plants[i], false);
		}

		for(int i=0;i<plants.length;i++){
			if(null!=fzbm&&!"".equals(fzbm)){
				if(fzbm.contains("-")) {
					String [] fzbms = fzbm.split("-");
					if(fzbms!=null){
						for(int j=0;j<fzbms.length;j++){
							if(plants[i].equals(fzbms[j])){
								plantMap.put(plants[i], true);
							}
						}
					}
				} else {
					if(plants[i].equals(fzbm)){
						plantMap.put(plants[i], true);
					}
				}
			}
		}

		part.setSecondePlant(plantMap);
		CmTreeNode node = new CmTreeNode(CmCommonStringUtil.getTreeItem(part));
		node.setPart(part);
		node.setOccId(occId);
		node.setOccpath(occpath);
		setNewLifecycle(node);
		return node;
	}





	@SuppressWarnings("unchecked")
	public void addChildNode(Element e, CmTreeNode node) {
		// 第一层childs层
		List<Element> list = e.elements();
		for (Element e1 : list) {
			// 第二层QMPartInfo层
			List<Element> childList = e1.elements();
			for (Element e2 : childList) {
				String[] occId = null;
				String[] occpath = null;
				String[] middleIndex = null;
				String[] path = null;
				String[] id = null;
				String[] index = null;
				String occpathe = e2.attributeValue("occpath");
				String occide = e2.attributeValue("occId");
				String indexe = e2.attributeValue("middleIndex");
				if(!CmCommonStringUtil.isEmpty(occpathe)
						&& !"assistant".equals(e2.attributeValue("partType"))){
					for(int m=0;m<packageList.size();m++){
						String vm = packageList.get(m).get(1);
						vm = vm.replace("(", "\\(");
						vm = vm.replace(")", "\\)");
						path = occpathe.split("&" + vm +"&");
						id =   occide.split("&" + vm +"&");
						index =   indexe.split("&" + vm +"&");
						if(path.length == 1){
							occpathe = path[0];
							occide = id[0];
							indexe = index[0];
						}else{
							occpathe = path[Integer.valueOf(packageList.get(m).get(0))];
							occide = id[Integer.valueOf(packageList.get(m).get(0))];
							indexe = index[Integer.valueOf(packageList.get(m).get(0))];
						}
					}
				}
				occpath = occpathe.split(",");
				occId = occide.split(",");
				middleIndex = indexe.split(",");
				if (occId.length > 1 && e2.elements().size() > 0 && !checkisOnlyHasAssistantNode(e2)) {
					// 整件打包，或带有子节点的中间件，并且中间件数量不为1
					if(Integer.valueOf(e2.attributeValue("useCount"))>0){
						List<String> cmlist = new ArrayList<String>();
						cmlist.add(0+"");
						cmlist.add(e2.attributeValue("partNumber"));
						packageList.add(cmlist);
					}
					int  tempLength = occId.length;
					if(tempLength>occpath.length){
						tempLength = occpath.length;
					}
					addPackageChildNode(e2, node,tempLength ,false);
					if(Integer.valueOf(e2.attributeValue("useCount"))>0){
						packageList.remove(packageList.size()-1);
					}
				} else {
					if ("middle".equals(e2.attributeValue("partType"))) {
						int useCount = Integer.valueOf(e2.attributeValue("useCount"));
						if (e2.elements().size() > 0) {
							addPackageChildNode(e2, node, useCount,false);
						} else {
							for (int k = 0; k < useCount; k++) {
								CmTreeNode brother = addTreeNode(e2, e2.attributeValue("occId"),e2.attributeValue("occpath"),e2.attributeValue("middleIndex"));
								node.add(brother);
//								getMatrixFromEbom(brother);
								brother.getPart().setUseCount(1);
								System.out.println(brother);
							}
						}
					} else if ("assistant".equals(e2.attributeValue("partType"))) {
						CmTreeNode assist = addTreeNode(e2, e2.attributeValue("occId"),e2.attributeValue("occId"),e2.attributeValue("middleIndex"));
						node.add(assist);
//						getMatrixFromEbom(assist);
						System.out.println(assist);
					} else {
						String[] paths = null;
						String[] brothers = null;
						String[] middle = null;
						occpathe = e2.attributeValue("occpath");
						occide = e2.attributeValue("occId");
						indexe = e2.attributeValue("middleIndex");
						for(int m=0;m<packageList.size();m++){
							String vm = packageList.get(m).get(1);
							vm = vm.replace("(", "\\(");
							vm = vm.replace(")", "\\)");
							paths = occpathe.split("&" + vm +"&");
							brothers =   occide.split("&" + vm +"&");
							middle =   indexe.split("&" + vm +"&");
							if(paths.length == 1){
								occpathe = paths[0];
								occide = brothers[0];
								indexe = middle[0];
							}else{
								occpathe = paths[Integer.valueOf(packageList.get(m).get(0))];
								occide = brothers[Integer.valueOf(packageList.get(m).get(0))];
								indexe = middle[Integer.valueOf(packageList.get(m).get(0))];
							}
						}
						paths = occpathe.split(",");
						brothers = occide.split(",");
						middle = indexe.split(",");
						Set<String> hasPaths = new HashSet<String>();
						for (int j = 0; j < paths.length; j++) {
							String tempPath = paths[j];
							if(hasPaths.contains(paths[j])){
								tempPath = paths[j]+"_";
							}

							hasPaths.add(paths[j]);
							CmTreeNode brother = addTreeNode(e2, brothers[j],tempPath,middle[j]);
							node.add(brother);
//							getMatrixFromEbom(brother);
							System.out.println(brother);
							if (!isOnlyHasAssistantElements(e2)) {
								addChildNode(e2, brother);
							} else {
								addAssistOnCommonNode(e2, brother);
							}
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public boolean isOnlyHasAssistantElements(Element assist) {
		boolean flag = true;
		// 第一层childs层
		List<Element> list = assist.elements();
		for (Element e1 : list) {
			// 第二层QMPartInfo层
			List<Element> childList = e1.elements();
			for (Element e2 : childList) {
				if (!"assistant".equals(e2.attribute("partType"))) {
					flag = false;
				}
			}
		}
		return flag;
	}

	@SuppressWarnings("unchecked")
	public void addPackageChildNode(Element e, CmTreeNode node, int packageSize,boolean isPackageOfParent) {
		for (int i = 0; i < packageSize; i++) {
			for(int n=0;n<packageList.size();n++){
				if(CmCommonStringUtil.isEqual(e.attributeValue("partNumber"), packageList.get(n).get(1))){
					packageList.get(n).set(0, i+"");
				}
			}
			CmTreeNode childnode = null;
			if ("middle".equals(e.attributeValue("partType"))) {
				String[] occId = null;
				String[] occpath=null;
				String[] middleIndex =null;
				if(isPackageOfParent || e.attributeValue("occpath").split("&").length>1){
					String[] path = null;
					String[] id = null;
					String[] index = null;
					String occpathe = e.attributeValue("occpath");
					String occide = e.attributeValue("occId");
					String indexe = e.attributeValue("middleIndex");
					for(int m=0;m<packageList.size();m++){
						if(!CmCommonStringUtil.isEqual(e.attributeValue("partNumber"), packageList.get(m).get(1))){
							String vm = packageList.get(m).get(1);
							vm = vm.replace("(", "\\(");
							vm = vm.replace(")", "\\)");
							path = occpathe.split("&" + vm +"&");
							id =   occide.split("&" + vm +"&");
							index =   indexe.split("&" + vm +"&");
							if(path.length == 1){
								occpathe = path[0];
								occide = id[0];
								indexe = index[0];
							}else{
								occpathe = path[Integer.valueOf(packageList.get(m).get(0))];
								occide = id[Integer.valueOf(packageList.get(m).get(0))];
								indexe = index[Integer.valueOf(packageList.get(m).get(0))];
							}
						}
					}
					occpath = occpathe.split(",");
					occId = occide.split(",");
					middleIndex = indexe.split(",");
				}
				else{
					occId = e.attributeValue("occId").split(",");
					occpath = e.attributeValue("occpath").split(",");
					middleIndex = e.attributeValue("middleIndex").split(",");
				}
				childnode = addTreeNode(e, occId[i],occpath[i],middleIndex[i]);
				childnode.getPart().setUseCount(1);
			} else {
				String occpathe = e.attributeValue("occpath");
				String occide = e.attributeValue("occId");
				String indexe = e.attributeValue("middleIndex");
				String[] path = null;
				String[] id = null;
				String[] index = null;
				if(!CmCommonStringUtil.isEmpty(e.attributeValue("occpath"))
						&& !"assistant".equals(e.attributeValue("partType"))){
					for(int m=0;m<packageList.size();m++){
						String vm = packageList.get(m).get(1);
						vm = vm.replace("(", "\\(");
						vm = vm.replace(")", "\\)");
						path = occpathe.split("&" + vm +"&");
						id =   occide.split("&" + vm +"&");
						index =   indexe.split("&" + vm +"&");
						if(path.length == 1){
							occpathe = path[0];
							occide = id[0];
							indexe = index[0];
						}else{
							occpathe = path[Integer.valueOf(packageList.get(m).get(0))];
							occide = id[Integer.valueOf(packageList.get(m).get(0))];
							indexe = index[Integer.valueOf(packageList.get(m).get(0))];
						}
					}
				}
				String[] occId = occide.split(",");
				String[] occpath=occpathe.split(",");
				String[] middleIndex =indexe.split(",");

				childnode = addTreeNode(e, occId[i],occpath[i],middleIndex[i]);
			}
			node.add(childnode);
//			getMatrixFromEbom(childnode);
			System.out.println("++++++++"+childnode);
			// 第一层childs层
			List<Element> list = e.elements();
			for (Element e1 : list) {
				// 第二层QMPartInfo层
				List<Element> childList = e1.elements();
				for (Element e2 : childList) {
					String[] occId = null;
					String[] occpath = null;
					String[] middleIndex = null;
					String[] path = null;
					String[] id = null;
					String[] index = null;
					String occpathe = e2.attributeValue("occpath");
					String occide = e2.attributeValue("occId");
					String indexe = e2.attributeValue("middleIndex");
					if(!CmCommonStringUtil.isEmpty(occpathe)
							&& !"assistant".equals(e2.attributeValue("partType"))){
						for(int m=0;m<packageList.size();m++){
							String vm = packageList.get(m).get(1);
							vm = vm.replace("(", "\\(");
							vm = vm.replace(")", "\\)");
							path = occpathe.split("&" + vm +"&");
							id =   occide.split("&" + vm +"&");
							index =   indexe.split("&" + vm +"&");
							if(path.length == 1){
								occpathe = path[0];
								occide = id[0];
								indexe = index[0];
							}else{
								occpathe = path[Integer.valueOf(packageList.get(m).get(0))];
								occide = id[Integer.valueOf(packageList.get(m).get(0))];
								indexe = index[Integer.valueOf(packageList.get(m).get(0))];
							}
						}
					}
					occpath = occpathe.split(",");
					occId = occide.split(",");
					middleIndex = indexe.split(",");
					if (occId.length > 1 && e2.elements().size() > 0 && !checkisOnlyHasAssistantNode(e2)) {
						// 整件打包，或带有子节点的中间件，并且中间件数量不为1
						if(Integer.valueOf(e2.attributeValue("useCount"))>0){
							List<String> cmlist = new ArrayList<String>();
							cmlist.add(i+"");
							cmlist.add(e2.attributeValue("partNumber"));
							packageList.add(cmlist);
						}
						addPackageChildNode(e2, childnode, occId.length,true);
						if(Integer.valueOf(e2.attributeValue("useCount"))>0){
							packageList.remove(packageList.size()-1);
						}
					} else {
						if ("middle".equals(e2.attributeValue("partType"))) {
							int useCount = Integer.valueOf(e2.attributeValue("useCount"));
							if (e2.elements().size() > 0) {
								addPackageChildNode(e2, childnode, useCount,true);
							} else {
								for (int k = 0; k < useCount; k++) {
									CmTreeNode brother = addTreeNode(e2, e2.attributeValue("occId"),e2.attributeValue("occpath"),e2.attributeValue("middleIndex"));
									childnode.add(brother);
//									getMatrixFromEbom(brother);
									brother.getPart().setUseCount(1);
									System.out.println(brother);
								}
							}
						} else if ("assistant".equals(e2.attributeValue("partType"))) {
							CmTreeNode assist = addTreeNode(e2, occId[0],occpath[0],middleIndex[0]);
							childnode.add(assist);
//							getMatrixFromEbom(assist);
							System.out.println(assist);
						} else {
							String[] paths = null;
							String[] brothers = null;
							String[] middle = null;
							occpathe = e2.attributeValue("occpath");
							occide = e2.attributeValue("occId");
							indexe = e2.attributeValue("middleIndex");
							for(int m=0;m<packageList.size();m++){
								String vm = packageList.get(m).get(1);
								vm = vm.replace("(", "\\(");
								vm = vm.replace(")", "\\)");
								path = occpathe.split("&" + vm +"&");
								id =   occide.split("&" + vm +"&");
								index =   indexe.split("&" + vm +"&");
								if(path.length == 1){
									occpathe = path[0];
									occide = id[0];
									indexe = index[0];
								}else{
									occpathe = path[Integer.valueOf(packageList.get(m).get(0))];
									occide = id[Integer.valueOf(packageList.get(m).get(0))];
									indexe = index[Integer.valueOf(packageList.get(m).get(0))];
								}
							}
							paths = occpathe.split(",");
							brothers = occide.split(",");
							System.out.println("-------brothers---"+Arrays.toString(brothers));
							middle = indexe.split(",");
							for (int j = 0; j < brothers.length; j++) {
								CmTreeNode brother = addTreeNode(e2, brothers[j],paths[j],middle[j]);
								childnode.add(brother);
//								getMatrixFromEbom(brother);
								if (!isOnlyHasAssistantElements(e2)) {
									addChildNode(e2, brother);
								} else {
									addAssistOnCommonNode(e2, brother);
								}
							}
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void addAssistOnCommonNode(Element e, CmTreeNode common) {
		if ("assistant".equals(e.attribute("partType"))) {
			// 第一层childs层
			List<Element> list = e.elements();
			for (Element e1 : list) {
				// 第二层QMPartInfo层
				List<Element> childList = e1.elements();
				for (Element e2 : childList) {
					CmTreeNode assist = addTreeNode(e2, e.attributeValue("occId"),e.attributeValue("occpath"),e.attributeValue("middleIndex"));
					common.add(assist);
					//getMatrixFromEbom(assist);
					System.out.println(assist);
				}
			}
		}
	}

	/**
	 * 判断某节点在具有相同父节点中的兄弟节点是否存在相同零件
	 *
	 * @date 2012-11-21
	 * @param node
	 * @param list
	 * @return
	 *
	 */
	public boolean ishasNoBrotherNode(CmTreeNode node, List<CmTreeNode> list) {
		boolean flag = true;
		for (CmTreeNode cmnode : list) {
			if (CmCommonStringUtil.isCommonNode(node, cmnode) && !node.getOccId().equals(cmnode.getOccId())) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	/**
	 *
	 * 判断某节点在具有相同父节点中的兄弟节点的oocid集
	 *
	 * @date 2012-11-21
	 * @param node
	 * @param list
	 * @return
	 *
	 */
	public String getAllBrotherNodeOccid(CmTreeNode node, List<CmTreeNode> list) {
		String occid = node.getOccId();
		for (CmTreeNode cmnode : list) {
			if (CmCommonStringUtil.isCommonNode(node, cmnode) && !node.getOccId().equals(cmnode.getOccId())) {
				occid += "," + cmnode.getOccId();
			}
		}
		return occid;
	}

	@SuppressWarnings("unchecked")
	public static void getEbomNodeList(CmTreeNode root) {
		Enumeration children = root.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
//			if (null == ebomNodesList) {
//				ebomNodesList = new ArrayList<CmTreeNode>();
//			}
//			ebomNodesList.add(child);
			ebomNodesMap.put(child, child);
			if(CmCommonStringUtil.isPackage(child)){
				for(CmTreeNode node: child.getListNode()){
					ebomNodesMap.put(node, node);
				}
			}
			getEbomNodeList(child);
		}
	}

	public static void getMatrixFromEbom(CmTreeNode node) {
//		if(null != ebomNodesList && ebomNodesList.size()>0){
//			for (CmTreeNode ebom : ebomNodesList) {
//				if(ebom.getOccpath() != null) {
//					if (ebom.getOccpath().equals(node.getOccpath())) {
//						node.setMatrix(ebom.getMatrix());
//						if (node.getPart().getVersion().equals(ebom.getPart().getVersion())) {
//							node.getPart().setReversion(ebom.getPart().isReversion());
//						}
//					}
//				} else {
//					System.out.println(ebom.getPart().getPartNumber() + " 没有OCCPATH!");
//				}
//
//			}
//		}
		CmTreeNode ebom  = ebomNodesMap.get(node);
		if(ebom!=null){
			node.setMatrix(ebom.getMatrix());
			if (node.getPart().getVersion().equals(ebom.getPart().getVersion())) {
				node.getPart().setReversion(ebom.getPart().isReversion());
			}
		}
	}

	/**
	 * 读取XML的时候获取零件最新的状态
	 * @author chenyunlong
	 * @date  2013-4-16
	 * @param node
	 *
	 */
	public void setNewLifecycle(CmTreeNode node){
		CmCommonNodeUtil common = new CmCommonNodeUtil();
		CmTreeNode brother = common.getSameOidInList(node, planingList);
		if(null ==brother){
			try {
				WTPart wtPart = (WTPart) CmSearchHelper.search(WTPart.class, node.getPart().getOid());
				if(null == wtPart){
					/*JOptionPane.showMessageDialog(CmContext.getMainFrame(), "找不到节点:\n"+node.getPart().getPartNumber()+"\nPlanning视图！");
					if (CmContext.isJWS())
						JWSUtil.shutdown();
					System.exit(0);*/
				}else{
					node.getPart().setLifecycle(wtPart.getLifeCycleState().getDisplay(Locale.CHINA));
					node.getPart().setVersion(
							wtPart.getVersionIdentifier().getValue() + "." + wtPart.getIterationIdentifier().getValue());
				}
				if(CmCommonStringUtil.isHasFilingOfObj(node)){
					node.getPart().setChange(false);
				}
				planingList.add(node);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(CmContext.getMainFrame(), "找不到节点:\n"+node.getPart().getPartNumber()+"\nPlanning视图！");
				if (CmContext.isJWS())
					JWSUtil.shutdown();
				System.exit(0);
			}
		}
		else{
			node.getPart().setLifecycle(brother.getPart().getLifecycle());
			node.getPart().setVersion(brother.getPart().getVersion());
			if(CmCommonStringUtil.isHasFilingOfObj(node)){
				node.getPart().setChange(false);
			}
		}
	}

	/**
	 * 判断当前节点(非工艺中间件)下的子零件是否只有工艺辅件或没有子零件
	 * @date  2013-1-17
	 * @return
	 *
	 */
	@SuppressWarnings("unchecked")
	public boolean checkisOnlyHasAssistantNode(Element e){
		boolean flag=true;
		List<Element> list = e.elements();
		for (Element e1 : list) {
			// 第二层QMPartInfo层
			List<Element> childList = e1.elements();
			for (Element e2 : childList) {
				if(!"assistant".equals(e2.attributeValue("partType"))){
					flag = false;
				}
			}
		}
		return flag;
	}
	/**
	 * 将现在的EBOM树和xml进行比较，并将EBOM重构信息显示出
	 * @author chenyunlong
	 * @date  2013-5-13
	 * @param bytes
	 * @param ebomTree
	 * @return
	 *
	 */
	@SuppressWarnings("unchecked")
	public void compareEbomWithXML(byte[] bytes, CmTree ebomTree) {
		try {
			String xml = new String(bytes,"GBK");
			System.out.println(xml);
			Document doc = DocumentHelper.parseText(xml);
			List<Element> elelist = new ArrayList<Element>();
			Element rootElement = doc.getRootElement();
//			if("needCompare".equals(rootElement.attributeValue("isCompared"))){
				List<Element> list = rootElement.elements();
				for (Element e : list) {
					elelist.add(e);
					getSubChildElement(e,elelist);
				}
				compareEbomStructureWithXML(elelist,ebomTree.getRoot());
				CmCommonStringUtil.sortTheTreeNode(ebomTree.getRoot());
				ebomTree.updateUI();
//			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(CmContext.getMainFrame(), "xml格式不正确！");
		}
	}

	@SuppressWarnings("unchecked")
	public void getSubChildElement(Element subchilds,List<Element> elelist){
		List<Element> list = subchilds.elements();
		if(null != list && list.size()>0){
			for(Element e:list){
				// 第二层partInfo层
				List<Element> childList = e.elements();
				for(Element ce:childList){
					if(CmCommonNodeUtil.checkPartNumberHasStructure(ce.attributeValue("partNumber"))){
						elelist.add(ce);
					}
					if(null != ce.elements() && ce.elements().size()>0){
						getSubChildElement(ce,elelist);
					}
				}
			}
		}
	}
	/**
	 * EBOM变更类型  ：1-版本变化，2-新增，3-删除
	 * @author chenyunlong
	 * @date  2013-5-18
	 * @param elelist
	 * @param root
	 *
	 */
	public void compareEbomStructureWithXML(List<Element> elelist,CmTreeNode root){
		Enumeration children = root.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(CmCommonNodeUtil.checkNodeHasStructure(child)){
				List<Element> sublist = getSubElementsWithCommonParent(elelist,child);
				if(null == sublist){
					//当前组件全部新增
					child.getPart().setEchangeIndex(2);
					changeNodeState(child,2);
				}else if(sublist.size() ==0){
					updateNodeVersion(elelist,child,1);
					changeNodeState(child,2);
				}else{
					updateNodeVersion(elelist,child,1);
					List<CmTreeNode> subnodelist = CmCommonNodeUtil.getChildNodeFromParent(child);
					addEbomDeleteNode(subnodelist,sublist,child);
					for (Iterator<CmTreeNode> it = subnodelist.iterator(); it.hasNext();) {
						CmTreeNode enuchild = it.next();
						if(CmCommonNodeUtil.checkNodeHasStructure(enuchild)){
							List<Element> enusublist = getSubElementsWithCommonParent(elelist,enuchild);
							if(null == enusublist){
								enuchild.getPart().setEchangeIndex(2);
								changeNodeState(enuchild,2);
							}else if(enusublist.size() == 0){
								updateNodeVersion(elelist,enuchild,1);
								changeNodeState(enuchild,2);
							}else{
								updateNodeVersion(elelist,enuchild,1);
								List<CmTreeNode> eunchildlist = CmCommonNodeUtil.getChildNodeFromParent(enuchild);
								addEbomDeleteNode(eunchildlist,enusublist,enuchild);
								for(CmTreeNode enusub:eunchildlist){
									if(CmCommonNodeUtil.checkNodeHasStructure(enusub)){
										compareEbomStructureWithXML(elelist,enuchild);
									}
									else{
										checkEbomNodeIsNewOrUpdateVersion(enusublist,enusub);
									}
								}
							}
						}else{
							checkEbomNodeIsNewOrUpdateVersion(sublist,enuchild);
						}
					}
				}
			}
		}
	}
	/**
	 * 如果EBOM树上是结构，
	 * @author chenyunlong
	 * @date  2013-5-18
	 * @param elelist
	 * @param node
	 * @return
	 *
	 */
	@SuppressWarnings("unchecked")
	public List<Element> getSubElementsWithCommonParent(List<Element> elelist,CmTreeNode node){
		Element common = null;
		for(Element ele:elelist){
			if(CmCommonStringUtil.isEqual(ele.attributeValue("partNumber"), node.getPart().getPartNumber())){
				common = ele;
				break;
			}
		}
		if(null != common){
			List<Element> sublist = new ArrayList<Element>();
			List<Element> list = common.elements();
			for(Element e:list){
				// 第二层partInfo层
				List<Element> childList = e.elements();
				for(Element ce:childList){
					sublist.add(ce);
				}
			}
			return sublist;
		}else{
			return null;
		}
	}
	/**
	 * 改变子节点的状态
	 * @author chenyunlong
	 * @date  2013-5-18
	 * @param node
	 * @param state
	 *
	 */
	@SuppressWarnings("unchecked")
	public void changeNodeState(CmTreeNode node,int state){
		Enumeration children = node.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			child.getPart().setEchangeIndex(state);
			changeNodeState(child,state);
		}
	}

	public void updateNodeVersion(List<Element> list ,CmTreeNode node,int state){
		for(Element ele:list){
			if(CmCommonStringUtil.isEqual(ele.attributeValue("partNumber"), node.getPart().getPartNumber())){
				if(!CmCommonStringUtil.isEqual(ele.attributeValue("version"), node.getPart().getVersion())){
					node.getPart().setEchangeIndex(state);
				}

				//modify by LongXiuChuan 2015/8/12
				//使用数量变化
				String useCount = ele.attributeValue("useCount");
				String eleParentNumber = "";
				Element pEle = ele.getParent();
				if(pEle != null) {
					Element ppEle = pEle.getParent();
					if(ppEle != null) {
						eleParentNumber = ppEle.attributeValue("partNumber");
					}
				}
				if(useCount != null && !"".equals(useCount)) {
					if(!node.getParent().equals(node.getRoot())
							&& !CmCommonStringUtil.isEqual(useCount, String.valueOf(node.getPart().getUseCount()))){
						CmTreeNode pNode = (CmTreeNode)node.getParent();
						String pNumber = pNode.getPart().getPartNumber();
						if(pNumber != null && !"".equals(pNumber) && pNumber.equals(eleParentNumber)) {
							node.getPart().setEchangeIndex(state);
						}
					}
				}

				break;
			}
		}
	}

	/**创建EBOM变更过程中删除的节点
	 *
	 * @author chenyunlong
	 * @date  2013-5-20
	 * @param subnodelist
	 * @param sublist
	 * @param node
	 *
	 */
	public void addEbomDeleteNode(List<CmTreeNode> subnodelist,List<Element> sublist,CmTreeNode node){
		for(Iterator<Element> it = sublist.iterator(); it.hasNext();){
			Element ele = it.next();
			CmTreeNode same = null;
			for(CmTreeNode subnode:subnodelist){
				if(CmCommonStringUtil.isEqual(subnode.getPart().getPartNumber(), ele.attributeValue("partNumber"))){
					same = subnode;
					break;
				}
			}
			if(null == same){
				CmLightPart part = new CmLightPart();
				part.setPartNumber(ele.attributeValue("partNumber"));
				part.setPartName(ele.attributeValue("partName"));
				part.setVersion(ele.attributeValue("version"));
				CmTreeNode delnode = new CmTreeNode(CmCommonStringUtil.getTreeItem(part));
				delnode.setPart(part);
				delnode.getPart().setEchangeIndex(3);
				node.add(delnode);
				addSubDeleteNode(delnode,ele);
				it.remove();
			}
		}
	}
	/**
	 * 判断单个节点节点是否是新增或升版
	 * @author chenyunlong
	 * @date  2013-5-20
	 * @param sublist
	 * @param enuchild
	 *
	 */
	public void checkEbomNodeIsNewOrUpdateVersion(List<Element> sublist,CmTreeNode enuchild){
		Element common = null;
		for(Element se:sublist){
			if(CmCommonStringUtil.isEqual(se.attributeValue("partNumber"), enuchild.getPart().getPartNumber())){
				common = se;
				break;
			}
		}
		if(null == common){
			enuchild.getPart().setEchangeIndex(2);//新增的节点
		}else if(null != common && !CmCommonStringUtil.isEqual(common.attributeValue("version"), enuchild.getPart().getVersion())){
			enuchild.getPart().setEchangeIndex(1);//版本的升级
		}
	}

	public void addSubDeleteNode(CmTreeNode delnode,Element e){
		List<Element> sublist = e.elements();
		if(null != sublist && sublist.size()>0){
			for(Element sube:sublist){
				List<Element> partlist = sube.elements();
				for(Element parte:partlist){
					CmLightPart part = new CmLightPart();
					part.setPartNumber(parte.attributeValue("partNumber"));
					part.setPartName(parte.attributeValue("partName"));
					part.setVersion(parte.attributeValue("version"));
					CmTreeNode delete = new CmTreeNode(CmCommonStringUtil.getTreeItem(part));
					delete.setPart(part);
					delete.getPart().setEchangeIndex(3);
					delnode.add(delete);
					addSubDeleteNode(delete,parte);
				}
			}
		}

	}
	/**
	 * 判断某节点是否需要保存xml
	 * @author chenyunlong
	 * @date  2013-7-16
	 * @param node
	 * @return
	 *
	 */
	public boolean checkNodeIsSaveXML(CmTreeNode node){
		boolean flag = false;
		byte[] bytes = null;
		for(Map<String,Object> map:byteslist){
			if(CmCommonStringUtil.isEqual(String.valueOf(map.get("partNumber")), node.getPart().getPartNumber())){
				bytes = (byte[]) map.get("bytes");
			}

		}
		if(null == bytes || bytes.length == 0){
			flag = checkNodeIsChange(node);
		}
		return flag;
	}

	public boolean checkNodeIsChange(CmTreeNode node){
		boolean flag = false;
		if(node.getPart().isEdit()){
			flag = true;
		}else{
			int verIndex = PbomTreeUpdateAction.getNodeUpdateVersionIndex(node);
			if(verIndex>0){
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 当零组件有xml的时候，其下的结构的节点的occpath要重新组装，去掉最上层父节点的occpath部分
	 * @author chenyunlong
	 * @date  2013-8-8
	 * @param node
	 * @param parentOccpath
	 *
	 */
	public void rebuildOccpathFromTreeToXML(CmTreeNode node,String parentOccpath){
		Enumeration children = node.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(child.getPart().isHasXml()||child.getPart().isChangeOfStructure()) {
				continue;
			}
			if(!CmCommonStringUtil.isEmpty(child.getOccpath())
					&& child.getOccpath().indexOf(parentOccpath)==0){
				child.setOccpath(child.getOccpath().replace(parentOccpath+"+","-1+"));
			}
			rebuildOccpathFromTreeToXML(child,parentOccpath);
			if(CmCommonStringUtil.isPackage(child)){
				for(CmTreeNode brother:child.getListNode()){
					if(!CmCommonStringUtil.isEmpty(brother.getOccpath())
							&& brother.getOccpath().indexOf(parentOccpath)==0){
						brother.setOccpath(brother.getOccpath().replace(parentOccpath+"+","-1+"));
					}
					rebuildOccpathFromTreeToXML(brother,parentOccpath);
				}
			}
		}

	}


	/**
	 * 如果某节点在xml中记录没有xml，但是实际上有xml需要重新获取
	 * @author chenyunlong
	 * @date  2013-8-13
	 * @param ele
	 *
	 */
	public void removeChildElement(Element ele){
		List<Element> list = ele.elements();
		for (Element e1 : list) {
			// 第二层QMPartInfo层
			List<Element> childList = e1.elements();
			for (Element e2 : childList) {
				boolean isHasXml = Boolean.valueOf(e2.attributeValue("isHasXml"));
				if(!isHasXml){
					boolean flag = false;
					byte[] bytes = CmBizObjUtil.getNodeXML(e2.attributeValue("partNumber"));
					if(null != bytes){
						e2.setAttributeValue("isHasXml", "true");
						flag = true;
					}else{
						WTPart part = PBOMEditorToWCIntf.getPlanningPart(e2.attributeValue("partNumber"),e2.attributeValue("oid"), CmConnectFrame.planningOid,flag);
						if(null != part){
							bytes =PBOMEditorToWCIntf.getPBOMXml(String.valueOf(part.getPersistInfo().getObjectIdentifier().getId()));
						}
						if(null != bytes){
							e2.setAttributeValue("isHasXml", "true");
							CmBizObjUtil.addBytesToList(e2.attributeValue("partNumber"),e2.attributeValue("oid"), bytes);
							flag = true;
						}
						if(flag && e2.elements().size()==1){
							e2.elements().remove(0);
						}
					}
				}else{
					removeChildElement(e2);
				}
			}
		}
	}




}
