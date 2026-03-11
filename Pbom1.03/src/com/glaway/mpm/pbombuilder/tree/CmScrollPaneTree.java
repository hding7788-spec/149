package com.glaway.mpm.pbombuilder.tree;

import com.glaway.mpm.pbombuilder.bom.CmCIMerger;
import com.glaway.mpm.pbombuilder.bom.CmConnectDialog;
import com.glaway.mpm.pbombuilder.bom.CmConnectFrame;
import com.glaway.mpm.pbombuilder.jws.CmContext;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmSearchHelper;
import com.glaway.mpm.pbombuilder.util.CmXmlUtil;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;
import com.ptc.jws.JWSUtil;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import wt.fc.PersistenceHelper;
import wt.part.WTPart;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * <br>
 * Created on 2012-10-23
 *
 * @author chenyunlong
 */
public class CmScrollPaneTree extends JScrollPane {
	private static final long serialVersionUID = 5106882626216586255L;
	public static CmTree ebomTree;
	public static Map<String,InforObject> ebomMap;
	private Image image;
	public static Map<String,String> versionInfor;
	public static Map<String,String> changeGysl = new HashMap<String,String>();
//	public static List<CmTreeNode> pbomlist=null;

	//记录节点最初信息（保存后至修改时间段）
	//public static List<CmTreeNode> pbomlist = new ArrayList<CmTreeNode>();
	public static HashMap<CmTreeNode,CmTreeNode> pbomMap = new HashMap<CmTreeNode,CmTreeNode>();
	public static HashMap<String,CmLightPart> partMap = new HashMap<String,CmLightPart>();
	public static HashMap<String,CmTreeNode> nodeMap = new HashMap<String,CmTreeNode>();
	public static HashMap<CmTreeNode,List<CmTreeNode>> structureList = new HashMap<CmTreeNode,List<CmTreeNode>>();

	public static HashMap<CmTreeNode,CmTreeNode> initPbomMap = null;
	public static HashMap<String,CmTreeNode> xmlNodeMap = new HashMap<String,CmTreeNode>();

	// 记录修订版本节点信息
//	public static HashMap<CmTreeNode, List<CmTreeNode>> updateVerssionList = new HashMap<CmTreeNode, List<CmTreeNode>>();

	public CmScrollPaneTree(CmTreeNode root) {
		super();

		setOpaque(true);
		getViewport().setOpaque(false);
		try {
			DocumentHelper.parseText("<?xml--------?>");
		} catch (DocumentException e1) {
		}

		try {
			if ("EBOM".equals(root.toString())) {
				CmConnectFrame.startAnimFrame.setHeaderMessage("加载EBOM树");
				long start = System.currentTimeMillis();
				ebomTree = new CmTree(root);
				ebomMap= new HashMap<String,InforObject>();
				versionInfor=new HashMap<String,String>();
				CmCIMerger merger = new CmCIMerger(ebomTree, root, CmContext.getMainFrame());
				WTPart part = (WTPart) CmSearchHelper.search(WTPart.class,Long.valueOf(CmConnectFrame.partOid));
				CmLightPart lightpart = CmBizObjUtil.buildCmLightPartFromWTPart(part);
				InforObject ob =new InforObject();
				ob.setNumber(lightpart.getPartNumber());
				ob.setName(lightpart.getPartName());
				ob.setUsecount(String.valueOf(lightpart.getUseCount()));
				ebomMap.put(lightpart.getPartNumber(), ob);
				versionInfor.put(lightpart.getPartNumber(), lightpart.getVersion());
				List<CmLightPart> parts = new ArrayList<CmLightPart>();
				parts.add(lightpart);
				merger.doMerger(parts);
				setViewportView(ebomTree);
				long end = System.currentTimeMillis();
				System.out.println(">>>>>>>>>>加载EBOM树用时："+(end-start));
				CmConnectFrame.startAnimFrame.setHeaderMessage("完成加载EBOM树");
			} else {
				CmConnectFrame.startAnimFrame.setHeaderMessage("加载MBOM树");
				long start = System.currentTimeMillis();
				CmBizObjUtil biz = new CmBizObjUtil();
				biz.resetComponentId();
				WTPart part = null;
				WTPart ebomPart  = null;
				if(null != ebomTree){
					CmTreeNode firstEbomNode = (CmTreeNode)ebomTree.getRoot().children().nextElement();
					part = CmCommonStringUtil.isHasPlanning(firstEbomNode, false);
					ebomPart  = (WTPart) CmSearchHelper.search(WTPart.class,Long.valueOf(CmConnectFrame.partOid));
				} else {
					ebomPart  = (WTPart) CmSearchHelper.search(WTPart.class,Long.valueOf(CmConnectDialog.partOid));
					part = PBOMEditorToWCIntf.getPlanningPart(ebomPart.getNumber(), String.valueOf(PersistenceHelper.getObjectIdentifier(ebomPart).getId()), CmConnectDialog.planningOid,false);
				}
				byte[] bytes = null;
				if(null != part){
					bytes =PBOMEditorToWCIntf.getPBOMXml(String.valueOf(part.getPersistInfo().getObjectIdentifier().getId()));
				}
				CmTree pbomTree=null;
				if (null == bytes || bytes.length == 0) {
					CmTreeNode node=CmBizObjUtil.convertEBomNode2MBomNode((CmTreeNode)ebomTree.getRoot().children().nextElement(),true);
					CmTreeNode unplanningNode = null;
					unplanningNode = CmCommonStringUtil.getPbomPlanning(node,unplanningNode);
					if(null != unplanningNode&&(!unplanningNode.getPart().getPartNumber().contains("CDB800-30"))){
						JOptionPane.showMessageDialog(ebomTree.getRootPane(), "下列EBOM树上节点:\n"+unplanningNode.getPart().getPartNumber()+"\n创建Planning视图失败，请联系管理员！");
						if (CmContext.isJWS())
							JWSUtil.shutdown();
						System.exit(0);

					}else{
						root.add(node);
						pbomTree=new CmTree(root);
						setViewportView(pbomTree);
					}
				} else {
					CmXmlUtil util = new CmXmlUtil();
				//	CmConnectFrame.startAnimFrame.setHeaderMessage("compareEbomWithXML begin");
					/*if(null != ebombytes && ebombytes.length > 0){
						util.compareEbomWithXML(ebombytes, ebomTree);
					}*/
				//	CmConnectFrame.startAnimFrame.setHeaderMessage("compareEbomWithXML end");
					pbomTree=util.getBytesToTree(bytes,ebomTree);
					setViewportView(pbomTree);
				}
				ebomTree=null;
				getPbomNodeList(pbomTree);
				biz.clearData();

				reSetPbomTree(pbomTree);
				long end = System.currentTimeMillis();
				System.out.println(">>>>>>>>>>加载MBOM树用时："+(end-start));
				CmConnectFrame.startAnimFrame.setHeaderMessage("完成加载MBOM树");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void reSetPbomTree(CmTree pbomTree) {
		CmTreeNode root = pbomTree.getRoot();
		Enumeration children = root.children();
		while (children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode)children.nextElement();
			retSetNode(child);
		}

	}

	private void retSetNode(CmTreeNode child) {
		setNewPros(child);
		if(!"PBOM".equals(child.getParent().toString())){
			String gysl = getGYSLFromWNC(child);
			if(gysl!=null&&!"".equals(gysl)){
				child.getPart().setGysl(gysl);
			}else{
				//如果远程link无工艺数量，则使用数量
				//String tmpGysl  = child.getPart().getGysl();
				//if(tmpGysl==null||"".equals(tmpGysl)||"1".equals(tmpGysl)){
				 child.getPart().setGysl(child.getPart().getUseCount()+"");
				//}
			}
		}
		Enumeration children = child.children();
		while (children.hasMoreElements()){
			CmTreeNode c = (CmTreeNode)children.nextElement();
			retSetNode(c);
		}
	}
	/**
	 * @param child
	 */
	private void setNewPros(CmTreeNode child) {
		String partNumber = child.getPart().getPartNumber();
		Map<String,String> pros = null;
		if( CmConnectFrame.partProsFromWNC.containsKey(partNumber)){
			//System.out.println("缓存获取到属性："+partNumber+"="+CmConnectFrame.partProsFromWNC.get(partNumber));
			pros = CmConnectFrame.partProsFromWNC.get(partNumber);

		}else{
			pros = PBOMEditorToWCIntf.getProsFromWNC(partNumber);
			//System.out.println("远程获取到属性："+partNumber+"="+pros);
			CmConnectFrame.partProsFromWNC.put(partNumber,pros);
		}
		if(pros!=null){
			String oid = pros.get("oid");
			if(oid!=null&&!"".equals(oid)){
				/*if(child.getPart().getOid()==Long.parseLong(oid)){
					System.out.println(child.getPart().getPartNumber()+"的OID相同取消属性重置");
					return;
				}*/
				String version1 = child.getPart().getVersion();
				String version2 = pros.get("version");
				if(version2!=null&&!"".equals(version2)&&version1!=null&&!"".equals(version1)){
					String[] v1= version1.split("\\.");
					String[] v2= version2.split("\\.");
					if(v1[0].equals(v2[0])){
						//System.out.println(child.getPart().getPartNumber()+"的大版本相同设置属性重置");
						child.getPart().setOid(Long.parseLong(oid));
						String version = pros.get("version");
						if(version!=null&&!"".equals(version)){
							child.getPart().setVersion(version);
						}
						String PINDEX = pros.get("PINDEX");
						if(PINDEX!=null&&!"".equals(PINDEX)){
							child.getPart().setPindex(PINDEX);
						}
						String MINDEX = pros.get("MINDEX");
						if(MINDEX!=null&&!"".equals(MINDEX)){
							child.getPart().setMindex(MINDEX);
						}
						String CINDEX = pros.get("CINDEX");
						if(CINDEX!=null&&!"".equals(CINDEX)){
							child.getPart().setCindex(CINDEX);
						}
						String PHASE_CODE = pros.get("PHASE_CODE");
						if(PHASE_CODE!=null&&!"".equals(PHASE_CODE)){
							child.getPart().setPhase_code(PHASE_CODE);
						}
						String BATCH = pros.get("BATCH");
						if(BATCH!=null&&!"".equals(BATCH)){
							child.getPart().setBatch(BATCH);
						}
					}
				}


			}

		}

	}

	private String getGYSLFromWNC(CmTreeNode child) {
		String partNumber = child.getPart().getPartNumber();
		TreeNode parent = child.getParent();
		if(parent!=null && parent instanceof CmTreeNode){
			CmTreeNode p = (CmTreeNode)parent;
			String parentPartNumber = p.getPart().getPartNumber();
			if(parentPartNumber==null||"".equals(parentPartNumber)){
				return null;
			}

			//String version = e.attributeValue("version");
			String key = parentPartNumber+"->"+partNumber;
			String gysl = null;

			if( CmConnectFrame.partLinkGYSLFromWNC.containsKey(key)){
				//System.out.println("缓存获取到工艺数量："+key+"="+CmConnectFrame.partLinkGYSLFromWNC.get(key));
				return CmConnectFrame.partLinkGYSLFromWNC.get(key);
			}


			gysl = PBOMEditorToWCIntf.getGYSLFromWNC(parentPartNumber,partNumber);
			if(gysl!=null&&!"".equals(gysl)){
				CmConnectFrame.partLinkGYSLFromWNC.put(key,gysl);
			}

			//System.out.println("远程获取到工艺数量："+key+"="+gysl);
			return gysl;
		}
		return null;

	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Color.WHITE);

		if (image != null) {
			int height = image.getHeight(this);
			int width = image.getWidth(this);

			if (height != -1 && height > getHeight())
				height = getHeight();

			if (width != -1 && width > getWidth())
				width = getWidth();

			int x = (int) (((double) (getWidth() - width)) / 2.0);
			int y = (int) (((double) (getHeight() - height)) / 2.0);

			g.drawImage(image, x, y, width, height, this);
		}
	}

	public CmTree getTree() {
		return (CmTree) getViewport().getView();
	}

//	public void getPbomNodeList(CmTree pbomTree){
//		if(null==pbomlist){
//			pbomlist=new ArrayList<CmTreeNode>();
//		}
//		getPbomTreeNodeList((CmTreeNode)pbomTree.getRoot().children().nextElement(),pbomlist);
//	}

	public void getPbomNodeList(CmTree pbomTree) {
		if (null == pbomMap) {
			pbomMap = new HashMap<CmTreeNode,CmTreeNode>();
		}

		getPbomTreeNodeList((CmTreeNode) pbomTree.getRoot().children()
				.nextElement(), pbomMap);

//		initPbomMap = new HashMap<CmTreeNode, CmTreeNode>(pbomMap);
	}

//	@SuppressWarnings("unchecked")
//	public List<CmTreeNode> getPbomTreeNodeList(CmTreeNode node, List<CmTreeNode> list) {
//		CmCommonStringUtil.copyNodeToList(node,(CmTreeNode)node.getParent(), list,false);
//		Enumeration children = node.children();
//		while (children.hasMoreElements()) {
//			CmTreeNode child = (CmTreeNode) children.nextElement();
//			if(CmCommonStringUtil.isPackage(child)){
//				for(CmTreeNode brother:child.getListNode()){
//					getPbomTreeNodeList(brother, list);
//				}
//			}
//			getPbomTreeNodeList(child, list);
//		}
//		return list;
//	}

	public HashMap<CmTreeNode,CmTreeNode> getPbomTreeNodeList(CmTreeNode node,
			HashMap<CmTreeNode,CmTreeNode> list) {
		CmCommonStringUtil.copyNodeToList(node, (CmTreeNode) node.getParent(),
				list, false, true,true);

		CmXmlUtil.getMatrixFromEbom(node);
		Enumeration children = node.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if (CmCommonStringUtil.isPackage(child)) {
				for (CmTreeNode brother : child.getListNode()) {
					getPbomTreeNodeList(brother, list);
				}
			}
			getPbomTreeNodeList(child, list);
		}
		return list;
	}


}