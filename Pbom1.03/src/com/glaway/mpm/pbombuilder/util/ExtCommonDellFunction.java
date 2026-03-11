package com.glaway.mpm.pbombuilder.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import wt.fc.PersistenceHelper;
import wt.part.WTPart;
import wt.util.WTException;

import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.pbombuilder.action.CmCommonPackageAction;
import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmScrollPaneTree;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.wcInterface.ErpToWCIntf;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

public class ExtCommonDellFunction {

	public static List<String> getUp_Nodes(CmTreeNode cuNode,CmTreeNode root){
		List<String> list = new ArrayList<String>();
		List<CmTreeNode> temp = new ArrayList<CmTreeNode>();

		 Enumeration en =   root.children();

		 while(en.hasMoreElements()){
			 CmTreeNode node = (CmTreeNode)en.nextElement();
			 if(CmCommonStringUtil.isCommon(cuNode, node)){
				 continue;
			 }else{
				 getUp_Nodes(cuNode,node,list,temp);
			 }
		 }

		 List<String> tempList = PBOMEditorToWCIntf.getUp_Parts(cuNode.getPart().getPartNumber(),LoadConfig.getInstance().getPbomView());
		 for(String str:tempList){
			 if(!list.contains(str)){
				 list.add(str);
			 }
		 }
		 return list;
	}

	public static void getUp_Nodes(CmTreeNode cuNode,CmTreeNode node,List<String> list,List<CmTreeNode> temp){
		Enumeration en = node.children();
		temp.add(node);
		while(en.hasMoreElements()){
			CmTreeNode cNode = (CmTreeNode)en.nextElement();
			if(!CmCommonStringUtil.isCommon(cuNode, cNode)){
				for(CmTreeNode teNode:temp){
					if(!list.contains(teNode.getPart().getPartNumber())){
						list.add(teNode.getPart().getPartNumber());
					}
				}
				continue;
			}else{
				getUp_Nodes(cuNode,cNode,list,temp);
			}
		}
	}

	public static void addExistPartNodeToCommonNode(CmTreeNode root,CmTreeNode node,CmTreeNode pNode,int useCount,CmTree tree) throws Exception{
		CmTreeNode commNode = getOnlyOneCommonNode(root,node);
		List<CmTreeNode> pNodeList = new ArrayList<CmTreeNode>();
		getAllCommonCmTreeNode(root,pNode,pNodeList);
		if(commNode!=null){
			if(CmCommonStringUtil.isPackage(commNode)){
				List<CmTreeNode>temp = commNode.getListNode();
				if(temp!=null&&!temp.isEmpty()){
					commNode = temp.get(0);
				}
			}
			String oldValue = commNode.getPart().getOperType();
			commNode.getPart().setOperType("new");
			List<String> tlist = new ArrayList<String>();
			tlist.add(useCount+"");
			tlist.add("");
			addNodeContainChidernToAssignNode(pNodeList,commNode,tlist,tree);
			commNode.getPart().setOperType(oldValue);
		}else{
			HashMap<CmTreeNode ,List<String>> nodeList = getChildernCmTreeNodeFromPDMSystem(node);
			WTPart part = (WTPart) CmSearchHelper.search(WTPart.class, node.getPart().getOid());
			CmTreeNode node1 = converWTPartToCmTreeNode(part);
			node1.getPart().setOperType("new");
//			node1.getPart().setWzk(node.getPart().getWzk());
			node1.setPart(node.getPart());
			List<String> tlist = new ArrayList<String>();
			tlist.add(useCount+"");
			tlist.add("");
			addNodeNotContainChildernToAssignNode(pNodeList,node1,tlist);
			addExistNodesToAssignNode(nodeList, node1, root, tree);



		}
		addAssignNodeContChildernScrollPanelPbomList(pNodeList, node);



		for(CmTreeNode temp: pNodeList){
			if(CmCommonStringUtil.isPackage(temp)){
				for(CmTreeNode n:temp.getListNode()){
					Enumeration<CmTreeNode> en = n.children();
					while(en.hasMoreElements()){
						CmTreeNode n1 = en.nextElement();
						if(CmCommonStringUtil.isCommonNode(node, n1)){
							CmCommonStringUtil.setIsNewTop(n1);
						}

					}
				}
				Enumeration<CmTreeNode> en = temp.children();
				while(en.hasMoreElements()){
					CmTreeNode n1 = en.nextElement();
					if(CmCommonStringUtil.isCommonNode(node, n1)){
						CmCommonStringUtil.setIsNewTop(n1);
					}
				}
			}else{
				Enumeration<CmTreeNode> en = temp.children();
				while(en.hasMoreElements()){
					CmTreeNode n1 = en.nextElement();
					if(CmCommonStringUtil.isCommonNode(node, n1)){
						CmCommonStringUtil.setIsNewTop(n1);
					}
				}
			}


			CmCommonStringUtil.checkNodeIsChangeOfStructure(temp, false);
		}

		packageAllNodeContanChildernNode(pNode,tree);

	}

	private static CmTreeNode getOnlyOneCommonNode(CmTreeNode root,CmTreeNode node){
		Enumeration en = root.children();
		while(en.hasMoreElements()){
			CmTreeNode cNode = (CmTreeNode) en.nextElement();
			if(CmCommonStringUtil.isCommon(cNode, node)){
				return cNode;
			}else{
				CmTreeNode ccNode = getOnlyOneCommonNode(cNode, node);
				if(ccNode!=null){
					return ccNode;
				}
			}
		}
		return null;
	}

	public static void getAllCommonCmTreeNode(CmTreeNode root,CmTreeNode node,List<CmTreeNode> list){
		Enumeration en = root.children();
		while(en.hasMoreElements()){
			CmTreeNode temp = (CmTreeNode) en.nextElement();
			if(CmCommonStringUtil.isCommon(temp, node)){
				list.add(temp);
				if(CmCommonStringUtil.isPackage(temp)){
					for(CmTreeNode bNode:temp.getListNode()){
						list.add(bNode);
					}
				}
				continue;
			}else{
				getAllCommonCmTreeNode(temp,node,list);
				if(CmCommonStringUtil.isPackage(temp)){
					for(CmTreeNode bNode:temp.getListNode()){
						getAllCommonCmTreeNode(bNode,node,list);
					}
				}
			}
		}
	}

	public static void addNodeContainChidernToAssignNode(List<CmTreeNode> tempList,CmTreeNode node,List<String> list,CmTree tree){
		int useCount = Integer.parseInt(list.get(0));
		String wzbm = list.get(1);
		Wzk wzk = null;
		if(wzbm!=null&&!"".equals(wzbm)){
			//wzk = ErpToWCIntf.getWzkByInvcode("01", wzbm);
		}
		List<CmTreeNode > temp = new ArrayList<CmTreeNode>();
		for(CmTreeNode pNode:tempList){
			for(int i=0;i<useCount;i++ ){
				CmTreeNode middle = copCmTreeNode(node);
				if(!temp.contains(middle)){
					temp.add(middle);
				}
				middle.getPart().setMiddleIndex(i+"");
				if(wzk!=null){
					middle.getPart().setWzk(wzk);
				}
				pNode.add(middle);
				middle.setOccId(String.valueOf(System.nanoTime()));
				CmCommonStringUtil.addOccpathOfNewNode(middle);
				if(CmCommonStringUtil.isPackage(node)){
					if(node.getListNode()!=null){
						for(CmTreeNode bNode:node.getListNode()){
							CmTreeNode bmiddle = copCmTreeNode(bNode);
							if(!temp.contains(bmiddle)){
								temp.add(bmiddle);
							}
							bmiddle.getPart().setMiddleIndex(CmCommonStringUtil.emptyToNumber(pNode.getPart().getMiddleIndex()) +"10"+i);
							pNode.add(bmiddle);
							bmiddle.setOccId(String.valueOf(System.nanoTime()));
							CmCommonStringUtil.addOccpathOfNewNode(bmiddle);
						}
					}
				}
			}
		}

		 Enumeration  en = node.children();
		 while(en.hasMoreElements()){
			 CmTreeNode cNode = (CmTreeNode) en.nextElement();
			 List<String> tlist = new ArrayList<String>();
			 tlist.add(1+"");
			 tlist.add("");
			 addNodeContainChidernToAssignNode(temp, cNode, tlist, tree);
		 }

	}

	public static CmTreeNode copCmTreeNode(CmTreeNode node){
		CmTreeNode newnode = new CmTreeNode(CmCommonStringUtil.getTreeItem(node.getPart()));
		newnode.setMatrix(node.getMatrix());
		newnode.setOccId(node.getOccId());
		newnode.setOccpath(node.getOccpath());
		newnode.setSelected(node.isSelected());
		newnode.setPart(CmCommonStringUtil.copyCmlightPart(node.getPart()));
		newnode.setNewTopNode(node.isNewTopNode());
		newnode.setChangeCountNode(node.isChangeCountNode());
		return newnode;
	}

	public static HashMap<CmTreeNode ,List<String>> getChildernCmTreeNodeFromPDMSystem(CmTreeNode node) throws WTException{
		HashMap<CmTreeNode ,List<String>> nodeList = new HashMap<CmTreeNode ,List<String>>();
		HashMap<WTPart ,List<String>> partList = PBOMEditorToWCIntf.getDownPartOnlyStepFromPdmSystem(String.valueOf(node.getPart().getOid()))     ;
		Set<WTPart> set = partList.keySet();
		for(WTPart part :set){
			CmTreeNode newNode = converWTPartToCmTreeNode(part);
			CmLightPart newLightPart = CmBizObjUtil.buildCmLightPartFromWTPart(part);
			newNode.setPart(newLightPart);
			nodeList.put(newNode, partList.get(part));
		}
		return nodeList;
	}

	public static CmTreeNode converWTPartToCmTreeNode(WTPart part)throws WTException{
		CmLightPart newpart = CmLightPart.newLightPart(part.getNumber(), part.getName(), "", 0L);
		String item = newpart.getPartNumber()+"("+newpart.getPartName()+")";
		CmTreeNode node = new CmTreeNode(item);
		node.setPart(newpart);
		node.getPart().setOid(Long.valueOf(part.getPersistInfo().getObjectIdentifier().getId()));
		node.getPart().setPartType(PBOMEditorToWCIntf.getTypeName(part));
		long containerId = PersistenceHelper.getObjectIdentifier(part.getContainer()).getId();
		node.getPart().setContainerId(containerId);
		node.getPart().setLifecycle(part.getLifeCycleState().getDisplay(Locale.CHINA));
		node.getPart().setVersion(part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue());
		node.setOccId(System.nanoTime()+"");
		node.setOccpath(System.nanoTime()+"");
		//-----------------------

		CmIBAHelper helper = new CmIBAHelper(part);
		newpart.setMaterialType(helper.getIBAValue("CTYPE"));
		newpart.setFirstPlant(helper.getIBAValue("ZZBM"));

		String fzbm = helper.getIBAValue("FZBM");
		String[] plants = LoadConfig.getInstance().getMainPlant();

		Map<String,Boolean> plantMap = new HashMap<String,Boolean>();

		for(int i=0;i<plants.length;i++){
			if(null!=fzbm&&!"".equals(fzbm)){
				String [] fzbms = fzbm.split("-");
				if(fzbms!=null){
					for(int j=0;j<fzbms.length;j++){
						if(plants[i].equals(fzbms[j])){
							plantMap.put(plants[i], true);
							break;
						}
					}
				}
			}else{
				plantMap.put(plants[i], false);
			}
		}
		newpart.setSecondePlant(plantMap);

		String clbm = helper.getIBAValue("CLBM");
		if(clbm!=null&&!"".equals(clbm)){
			Wzk wzk = ErpToWCIntf.getWzkByInvcode("01", clbm);
			newpart.setWzk(wzk);
		}


		return node;
	}

	public static List<CmTreeNode> addNodeNotContainChildernToAssignNode(List<CmTreeNode> nodeList,CmTreeNode node,List<String> tlist){
		int useCount = Integer.parseInt(tlist.get(0));
		String wzbm = tlist.get(1);
		Wzk wzk = null;
		if(wzbm!=null&&!"".equals(wzbm)){
			wzk = ErpToWCIntf.getWzkByInvcode("01", wzbm);

		}
		List<CmTreeNode> list  = new ArrayList<CmTreeNode>();
		List<String> occIdList = new ArrayList<String>();
		for(CmTreeNode temp:nodeList){
			for(int i=0;i<useCount;i++){
				int size = occIdList.size();
				if(size==i){
					occIdList.add(String.valueOf(System.nanoTime()));
				}

				CmTreeNode middle = CmCommonStringUtil.copyCmTreeNode(node);
				middle.getPart().setMiddleIndex(i+"");
				if(wzk!=null){
					middle.getPart().setWzk(wzk);
				}
				temp.add(middle);
				middle.setOccId(occIdList.get(i));
				CmCommonStringUtil.addOccpathOfNewNode(middle);
				list.add(middle);
			}
		}
		return list;
	}

	private static void addExistNodesToAssignNode(HashMap<CmTreeNode,List<String>> cNodeList,CmTreeNode pNode,CmTreeNode root,CmTree tree)throws WTException{
		Set<CmTreeNode> set = cNodeList.keySet();
		for(CmTreeNode cNode:set){
			CmTreeNode commNode = getOnlyOneCommonNode(root, cNode);
			if(commNode!=null){
				if(CmCommonStringUtil.isPackage(commNode)){
					List<CmTreeNode> temp = commNode.getListNode();
					if(temp!=null){
						if(temp.size()>1){
							commNode = temp.get(0);
						}
					}
				}
				List<CmTreeNode> pNodeList = new ArrayList<CmTreeNode>();
				pNodeList.add(pNode);
				addNodeContainChidernToAssignNode(pNodeList, commNode, cNodeList.get(cNode), tree);

			}else{
				List<CmTreeNode> temp = new ArrayList<CmTreeNode>();
				getAllCommonCmTreeNode(root, pNode, temp);
				addNodeNotContainChildernToAssignNode(temp, cNode, cNodeList.get(cNode));
				HashMap<CmTreeNode,List<String>> nodeList = getChildernCmTreeNodeFromPDMSystem(cNode);
				addExistNodesToAssignNode(nodeList, cNode, root, tree);
			}
		}
	}


	public static void addAssignNodeContChildernScrollPanelPbomList(List<CmTreeNode> nodeList,CmTreeNode needAddNode){
		if(CmScrollPaneTree.pbomMap.values() == null){
			CmScrollPaneTree.pbomMap = new HashMap<CmTreeNode,CmTreeNode>();
		}

		if(needAddNode!=null){
			for(CmTreeNode node : nodeList){
				Enumeration en = node.children();
				List<CmTreeNode> cNodeList = new ArrayList<CmTreeNode>();
				while(en.hasMoreElements()){
					CmTreeNode cNode = (CmTreeNode) en.nextElement();
					if(CmCommonStringUtil.isCommon(cNode, needAddNode)){
						cNodeList.add(cNode);
					}
				}
				if(cNodeList.size()>0){
					addAssignNodeContChildernScrollPanelPbomList(cNodeList, null);
				}
			}
		}else{
			for(CmTreeNode node:nodeList){
				CmCommonStringUtil.copyNodeToList(node, (CmTreeNode)node.getParent(), CmScrollPaneTree.pbomMap, true, false, false);
				Enumeration en = node.children();
				List<CmTreeNode> cNodeList = new ArrayList<CmTreeNode>();
				while(en.hasMoreElements()){
					CmTreeNode cNode  = (CmTreeNode)en.nextElement();
					cNodeList.add(cNode);
				}
				if(cNodeList.size()>0){
					addAssignNodeContChildernScrollPanelPbomList(cNodeList, null);
				}
			}
		}
	}

	public static void packageAllNodeContanChildernNode(CmTreeNode node,CmTree tree){
		List<CmTreeNode> nodeList = new ArrayList<CmTreeNode>();
		getAllNodeContanChildernNodeNotBrother(node,nodeList);
		for(int i= nodeList.size()-1;i>-1;i--){
			CmTreeNode pNode = nodeList.get(i);
			packageAssignNode(tree, pNode, false);
		}
		CmTree cmtree = null;

		if("PBOM".equals(tree.getRoot().toString())){
			cmtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree();
		}else{
			cmtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree();
		}


		CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
		if(cmtree!=null){
			CmCommonStringUtil.sortTheTreeNode(cmtree.getRoot());
		}



	}

	public static void packageAssignNode(CmTree tree,CmTreeNode currNode,boolean needTreeNode){
		CmTree cmtree = null;

		CmCommonPackageAction common = new CmCommonPackageAction();
		if("PBOM".equals(tree.getRoot().toString())){
			cmtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree();
		}else{
			cmtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree();
		}

		common.packageOneNode(tree.getRoot(), currNode);
		common.packageOneNode(cmtree.getRoot(), currNode);

		if(needTreeNode){
			CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
			CmCommonStringUtil.sortTheTreeNode(cmtree.getRoot());
		}
	}

	public static void getAllNodeContanChildernNodeNotBrother(CmTreeNode node,List<CmTreeNode> nodeList){
		Enumeration en = node.children();
		while(en.hasMoreElements()){
			CmTreeNode cNode = (CmTreeNode) en.nextElement();
			getAllNodeContanChildernNodeNotBrother(cNode, nodeList);

		}
		if(!nodeList.contains(node)){
			boolean hasBrother = false;
			for(CmTreeNode lNode:nodeList){
				if(CmCommonStringUtil.isCommon(lNode, node)){
					hasBrother = true;
					break;
				}
			}
			if(!hasBrother){
				nodeList.add(node);
			}
		}
	}


	public static List<CmTreeNode> addNewPartToCommonNode(CmTreeNode root,CmTreeNode node,CmTreeNode pNode,int useCount,boolean needCancel){
		List<CmTreeNode> list = new ArrayList<CmTreeNode>();
		getAllCommonCmTreeNode(root, pNode, list);
		List<String> tlist = new ArrayList<String>();
		tlist.add(useCount+"");
		tlist.add("");
		List<CmTreeNode> list1 = addNodeNotContainChildernToAssignNode(list, node, tlist);
		addAssignNodeContChildernScrollPanelPbomList(list, node);
		for(CmTreeNode temp:list){
			CmCommonStringUtil.checkNodeIsChangeOfStructure(temp, false);
		}

		return list1;
	}

	public static void updateTreeUI(CmTree tree,boolean neddUpdateOtherTree){
		if(neddUpdateOtherTree){
			CmTree cmtree = null;

			CmCommonPackageAction common = new CmCommonPackageAction();
			if("PBOM".equals(tree.getRoot().toString())){
				cmtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree();
			}else{
				cmtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree();
			}

			if(cmtree!=null	){
				cmtree.updateUI();
			}
		}
		tree.updateUI();
	}

	public static void changeCommonNodeUseCount(CmTreeNode root,CmTreeNode node,CmTreeNode pNode,int useCount,boolean needCancal,CmTree tree){
		int hasCount = 0;
		boolean needAddPbomList = false;
		List<CmTreeNode> list = new ArrayList<CmTreeNode>();
		getAllCommonCmTreeNode(root, pNode, list);
		for(CmTreeNode temp:list){
			Enumeration<CmTreeNode> en = temp.children();
			List<CmTreeNode> remNode = new ArrayList<CmTreeNode>();
			while(en.hasMoreElements()){
				CmTreeNode cNode = en.nextElement();
				if(CmCommonStringUtil.isCommon(cNode, node)){
					cNode.getPart().setOperType("new");
					remNode.add(cNode);
				}
			}
			List<CmTreeNode> temp1 = new ArrayList<CmTreeNode>();
			for(CmTreeNode cNode:remNode){
				if(CmCommonStringUtil.isPackage(cNode)){
					for(CmTreeNode bNode:cNode.getListNode()){
						bNode.getPart().setOperType("new");
						temp.add(bNode);
						temp1.add(bNode);
					}
					cNode.setListNode(new ArrayList<CmTreeNode>());
				}
			}
			remNode.addAll(temp1);
			hasCount = remNode.size();
			if(useCount==0){
				for(int i=useCount;i<useCount;i++){
					CmTreeNode tNode = remNode.get(i);
					Long deleteParentInfo = 0L;
					if(temp!=null){
						CmLightPart ppart = temp.getPart();
						if(ppart!=null){
							deleteParentInfo = ppart.getOid();
							if(CmCommonStringUtil.isPackage(tNode)){
								for(CmTreeNode bNode:tNode.getListNode()){
									bNode.getPart().setOperType("delete");
									CmCommonStringUtil.updateNodeOperTypeExpand(bNode, "delete");
								}
							}
							tNode.getPart().setOperType("delete");
							CmCommonStringUtil.updateNodeOperTypeExpand(tNode, "delete");
							tNode.removeFromParent();
						}
					}
				}
			}else{
				if(hasCount<useCount){
					List<CmTreeNode> tempList = new ArrayList<CmTreeNode>();
					tempList.add(temp);
					node.getPart().setOperType("new");
					node.setChangeCountNode(true);
					List<String> tlist = new ArrayList<String>();
					tlist.add((useCount-hasCount)+"");
					tlist.add("");
					addNodeContainChidernToAssignNode(tempList, node, tlist, tree);
					needAddPbomList = true;
				}else if(hasCount>useCount){
					for(int i=useCount;i<hasCount;i++){
						CmTreeNode tNode = remNode.get(i);
						if(CmCommonStringUtil.isPackage(tNode)){
							for(CmTreeNode pnode:tNode.getListNode()){
								pnode.getPart().setOperType("new");
							}
						}
						tNode.getPart().setOperType("new");
						tNode.removeFromParent();
					}
				}
			}
			CmCommonStringUtil.checkNodeIsChangeOfStructure(temp, false);
			if(needAddPbomList){
				addAssignNodeContChildernScrollPanelPbomList(list, node);
			}

			if(CmCommonStringUtil.isPackage(temp)){
				for(CmTreeNode n:temp.getListNode()){
					Enumeration<CmTreeNode> en1 = n.children();
					while(en1.hasMoreElements()){
						CmTreeNode n1 = en1.nextElement();
						if(CmCommonStringUtil.isCommonNode(node, n1)){
							CmCommonStringUtil.setIsChangeCountNode(n1);
						}
					}
				}
				Enumeration<CmTreeNode> en2 = temp.children();
				while(en2.hasMoreElements()){
					CmTreeNode n1 = en2.nextElement();
					if(CmCommonStringUtil.isCommonNode(node, n1)){
						CmCommonStringUtil.setIsChangeCountNode(n1);
					}
				}
			}else{
				Enumeration<CmTreeNode> en3 = temp.children();
				while(en3.hasMoreElements()){
					CmTreeNode n1 = en3.nextElement();
					if(CmCommonStringUtil.isCommonNode(node, n1)){
						CmCommonStringUtil.setIsChangeCountNode(n1);
					}
				}
			}

			if(needCancal){
//					EbomTreeCancelAction.
			}
			ExtCommonDellFunction.packageAllNodeContanChildernNode(pNode, tree);
		}
	}
}
