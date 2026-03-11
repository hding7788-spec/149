package com.glaway.mpm.pbombuilder.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.tree.TreePath;

import wt.part.WTPart;

import com.glaway.mpm.pbom.db.ERPService;
import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.wcInterface.ErpToWCIntf;

public class ImportPbomUtil {
	private CmTree tree;
	private List<CmTreeNode> deleteContainNodeList = new ArrayList<CmTreeNode>();
	private List<CmTreeNode> selNodeList = new ArrayList<CmTreeNode>();

	private List<CmTreeNode> realContainNodeList = new ArrayList<CmTreeNode>();

	private List<CmTreeNode> allSelNodeList = new ArrayList<CmTreeNode>();

	public ImportPbomUtil(CmTree tree){
		this.tree = tree;
		this.initData();
	}

	public List<Wzk> genImportWzkList(){
		List<Wzk> wzkList = new ArrayList<Wzk>();
		CmTreeNode node = this.findLatestNode();
		CmTreeNode pNode = (CmTreeNode)node.getParent();
		if(!this.isContainAllSelNode(pNode)){
			pNode = (CmTreeNode)pNode.getParent();
		}
		this.findImportWzk(pNode, wzkList);
		return wzkList;
	}





	private void initData(){
		TreePath[] paths = tree.getSelectionPaths();
		for (TreePath path : paths) {
			CmTreeNode selNode = (CmTreeNode) path.getLastPathComponent();
			deleteContainNodeList.add(selNode);
			selNodeList.add(selNode);
		}
		this.initRealContainNodeList();

	}

	private void initRealContainNodeList(){
		for(CmTreeNode node: selNodeList){
			this.addShowParent(node);
		}
	}

	private void addShowParent(CmTreeNode node){
		realContainNodeList.add(node);
		CmTreeNode pnode = (CmTreeNode)node.getParent();
		if("PBOM".equals(pnode)){
			return;
		}else{
			if(pnode==null)return;
			CmLightPart part = pnode.getPart();
			if(!CmCommonStringUtil.isEmpty(part.getWzk().getInvcode())&&!CmCommonStringUtil.isEmpty(part.getWzk().getClcode())){
				//System.out.println("pnode="+ pnode + " Invcode="+part.getWzk().getInvcode() + " Clcode="+part.getWzk().getClcode());
				realContainNodeList.add(pnode);
			}
			addShowParent(pnode);
		}
	}

	private CmTreeNode findLatestNode(){
		TreePath[] paths = tree.getSelectionPaths();
		List<Integer> sortList = new ArrayList<Integer>();
		Map<Integer,CmTreeNode> mapNodes = new TreeMap<Integer,CmTreeNode>();
		for (TreePath path : paths) {
			CmTreeNode selNode = (CmTreeNode) path.getLastPathComponent();
			sortList.add(selNode.getLevel());
			mapNodes.put(selNode.getLevel(), selNode);
		}
		Collections.sort(sortList);
		return mapNodes.get(sortList.get(0));
	}

	private boolean isContainAllSelNode(CmTreeNode node){
		deleteSelNode(node);
		return deleteContainNodeList.isEmpty();
	}

	private void deleteSelNode(CmTreeNode node){
		Enumeration<CmTreeNode> children = node.children();
		while(children.hasMoreElements()){
			CmTreeNode cnode =  children.nextElement();
			if(deleteContainNodeList.contains(cnode)){
				deleteContainNodeList.remove(cnode);
			}
			deleteSelNode(cnode);
		}
	}

	private void findImportWzk(CmTreeNode pNode,List<Wzk> wzkList){
		Enumeration<CmTreeNode> children = pNode.children();
		List<Wzk> tempList = new ArrayList<Wzk>();
		int i = 0;

		List<CmTreeNode> leafNodeList = new ArrayList<CmTreeNode>();
		List<CmTreeNode> noNodeList = new ArrayList<CmTreeNode>();

		while(children.hasMoreElements()){
			CmTreeNode cnode =  children.nextElement();
			if(cnode.isLeaf()){
				leafNodeList.add(cnode);
			}else{
				noNodeList.add(cnode);
			}
		}
		List<CmTreeNode> allNodeList = new ArrayList<CmTreeNode>();
		allNodeList.addAll(noNodeList);
		allNodeList.addAll(leafNodeList);

		for(CmTreeNode cnode:allNodeList){
			CmLightPart  ppart = pNode.getPart();

			WTPart cwtpart = CmBizObjUtil.getWTPartFromLightPart(cnode.getPart());
			WTPart pwtpart = CmBizObjUtil.getWTPartFromLightPart(pNode.getPart());
			CmIBAHelper chelper = new CmIBAHelper(cwtpart);
			CmIBAHelper phelper = new CmIBAHelper(pwtpart);
			CmLightPart  cPart  = cnode.getPart();
			Wzk cWzk = cPart.getWzk();
			String yzjd = null;
			if(realContainNodeList.contains(cnode)){
				Wzk pWzk = ppart.getWzk();
				Wzk showWzk = new Wzk();
				if(i==0){
					showWzk.setGsdm("100101");
					showWzk.setGcbm("1");
					yzjd = phelper.getIBAValue("document_phase");
					showWzk.setCpbm(ErpUtil.getPhase(yzjd)+"#"+ppart.getPartNumber());
					showWzk.setVersion(ppart.getVersion());
					showWzk.setSjth(ppart.getPartNumber());
					showWzk.setSjthflbm(pWzk.getClflbm());
					showWzk.setSjthmc(ppart.getPartName());
				}
				if(pwtpart!=null)
				showWzk.setSjthssxh(pwtpart.getContainerName());
				showWzk.setSjthjldwmc(pWzk.getMeasname());
				showWzk.setBtbz("");
				showWzk.setSfmr("");
				showWzk.setFxsl(ppart.getUseCount()+"");
				if(cwtpart!=null)
				showWzk.setSsxh(cwtpart.getContainerName());

				if(CmCommonStringUtil.isEmpty(cWzk.getClcode())){
					yzjd = chelper.getIBAValue("document_phase");
					showWzk.setYzjd(ErpUtil.getPhase(yzjd));
					showWzk.setInvcode(cWzk.getInvcode() );
					showWzk.setZxsl(cWzk.getZxsl());
					String invcode = cWzk.getInvcode();
					Wzk dbWzk = ErpToWCIntf.getWzkByInvcode("02", invcode);
					if(dbWzk!=null){
						showWzk.setClmc(dbWzk.getInvname());
						showWzk.setInvtype(dbWzk.getInvtype());
						showWzk.setInvspec(dbWzk.getInvspec());
						showWzk.setDef2(dbWzk.getDef2());
						showWzk.setDef3(dbWzk.getDef3());
						showWzk.setMeasname(dbWzk.getMeasname());
						showWzk.setDef4(dbWzk.getDef4());
					}


				}else{
					yzjd = chelper.getIBAValue("document_phase");
					showWzk.setYzjd(ErpUtil.getPhase(yzjd));
					showWzk.setInvcode(ErpUtil.getPhase(yzjd)+"#"+cPart.getPartNumber() );
					showWzk.setZxsl(cWzk.getZxsl());
					showWzk.setTh(cPart.getPartNumber());
					showWzk.setThmc(cPart.getPartNumber());
				}

				Wzk wzk = cnode.getPart().getWzk();
				wzkList.add(showWzk);
				if(!CmCommonStringUtil.isEmpty(wzk.getClcode())){
					wzk.setGsdm("100101");
					wzk.setGcbm("1");
					wzk.setVersion(cPart.getVersion());
					wzk.setSjth(cPart.getPartNumber());
					wzk.setSjthflbm(cWzk.getClflbm());
					wzk.setSjthmc(cPart.getPartName());
					if(pwtpart!=null)
						wzk.setSjthssxh(cwtpart.getContainerName());
					wzk.setSjthjldwmc(cWzk.getMeasname());
//					showWzk.setBtbz("");
					wzk.setSfmr("");
					wzk.setZxsl(String.valueOf(cWzk.getZxsl()));
					yzjd = chelper.getIBAValue("document_phase");
					wzk.setCpbm(yzjd+"");
					wzk.setYzjd(ErpUtil.getPhase(yzjd));
					wzk.setClflbm(cWzk.getClflbm());
					wzk.setZxsl(cWzk.getClflbm());
					wzk.setTh(cPart.getPartNumber());
					wzk.setThmc(cPart.getPartNumber());
					tempList.add(wzk);
				}


				findImportWzk(cnode,wzkList);
				i++;
			}

		}
		wzkList.addAll(tempList);
	}


	/**
	 * 生成导出的物资库列表 （新）
	 * @return
	 */
	public List<Wzk> genImportWzkListNew(){
		List<Wzk> wzkList = new ArrayList<Wzk>();
		TreePath[] paths = tree.getSelectionPaths();
		for (TreePath path : paths) {
			CmTreeNode selNode = (CmTreeNode) path.getLastPathComponent();
			allSelNodeList.add(selNode);
		}
		for(CmTreeNode cNode:allSelNodeList){
			CmTreeNode pNode = (CmTreeNode)cNode.getParent();
			if(allSelNodeList.contains(pNode)){ //父节点选中
				CmLightPart cpart = cNode.getPart();
				CmLightPart ppart = pNode.getPart();
				WTPart cwtpart = CmBizObjUtil.getWTPartFromLightPart(cNode.getPart());
				WTPart pwtpart = CmBizObjUtil.getWTPartFromLightPart(pNode.getPart());

				String invcode = cpart.getWzk().getInvcode();
				String Clcode = cpart.getWzk().getClcode();
				//System.out.println("invcode="+invcode + ",   Clcode="+Clcode);

				CmIBAHelper phelper = new CmIBAHelper(pwtpart);
				CmIBAHelper chelper = new CmIBAHelper(cwtpart);
				if(!CmCommonStringUtil.isEmpty(Clcode)){

					Wzk wzk = new Wzk();
					wzk.setGsdm("100101");
					wzk.setGcbm("1");
					String yzjd = phelper.getIBAValue("document_phase");
					wzk.setCpbm(ErpUtil.getPhase(yzjd)+"#"+ppart.getPartNumber());
					wzk.setVersion(ppart.getVersion());
					wzk.setSjth(ppart.getPartNumber());
					wzk.setSjthflbm(ppart.getWzk().getClflbm());
					wzk.setSjthmc(ppart.getPartName());
					wzk.setSjthmc(ppart.getPartName());
					wzk.setSjthssxh(pwtpart.getContainerName());
					wzk.setSjthjldwmc(ppart.getWzk().getMeasname());
					wzk.setBtbz("");
					wzk.setSfmr("");
					wzk.setFxsl(ppart.getUseCount()+"");
					if(cwtpart!=null)
						wzk.setSsxh(cwtpart.getContainerName());
					yzjd = chelper.getIBAValue("document_phase");
					wzk.setYzjd(ErpUtil.getPhase(yzjd));
					wzk.setInvcode(ErpUtil.getPhase(yzjd)+"#"+cwtpart.getNumber() );
					wzk.setClflbm(cpart.getWzk().getInvclasscode());
					wzk.setZxsl(cpart.getUseCount()+"");
					wzk.setTh(cwtpart.getNumber());
					wzk.setThmc(cwtpart.getName());
					wzkList.add(wzk);
					Wzk clWzk = new Wzk();
					clWzk.setGsdm("100101");
					clWzk.setGcbm("1");
					clWzk.setCpbm(ErpUtil.getPhase(yzjd)+"#"+cpart.getPartNumber());
					clWzk.setVersion(cpart.getVersion());
					clWzk.setSjth(cpart.getPartNumber());
					clWzk.setSjthflbm(cpart.getWzk().getClflbm());
					clWzk.setSjthmc(cpart.getPartName());
					clWzk.setSjthssxh(pwtpart.getContainerName());
					clWzk.setSjthjldwmc(cpart.getWzk().getMeasname());
					clWzk.setSsxh(cwtpart.getContainerName());
					clWzk.setBtbz("");
					clWzk.setSfmr("");
					clWzk.setInvcode(Clcode);
					clWzk.setClflbm(cpart.getWzk().getInvclasscode());
					clWzk.setZxsl(cpart.getUseCount()+"");
					clWzk.setClmc(cpart.getWzk().getClName());
					clWzk.setInvtype(cpart.getWzk().getInvtype());
					clWzk.setDef2(cpart.getWzk().getDef2());
					clWzk.setDef3(cpart.getWzk().getDef3());
					clWzk.setMeasname(cpart.getWzk().getMeasname());
					clWzk.setDef4(cpart.getWzk().getDef4());
					wzkList.add(clWzk);


				}else if(!CmCommonStringUtil.isEmpty( invcode)){

					Wzk dbWzk = ErpToWCIntf.getWzkByInvcode("02", invcode);

					Wzk wzk = new Wzk();
					wzk.setGsdm("100101");
					wzk.setGcbm("1");
					String yzjd = phelper.getIBAValue("document_phase");
					wzk.setCpbm(ErpUtil.getPhase(yzjd)+"#"+ppart.getPartNumber());
					wzk.setVersion(ppart.getVersion());
					wzk.setSjth(ppart.getPartNumber());
					wzk.setSjthflbm(ppart.getWzk().getClflbm());
					wzk.setSjthmc(ppart.getPartName());
					wzk.setSjthmc(ppart.getPartName());
					wzk.setSjthssxh(pwtpart.getContainerName());
					wzk.setSjthjldwmc(ppart.getWzk().getMeasname());
					wzk.setBtbz("");
					wzk.setSfmr("");
					wzk.setFxsl(ppart.getUseCount()+"");
					if(cwtpart!=null)
						wzk.setSsxh(pwtpart.getContainerName());

					wzk.setInvcode(invcode );
					wzk.setClflbm(dbWzk.getInvclasscode());
					wzk.setZxsl(cpart.getUseCount()+"");
					wzk.setClmc(dbWzk.getInvname());
					wzk.setInvtype(dbWzk.getInvtype());
					wzk.setDef2(dbWzk.getDef2());
					wzk.setDef3(dbWzk.getDef3());
					wzk.setMeasname(dbWzk.getMeasname());
					wzk.setDef4(dbWzk.getDef4());
					wzkList.add(wzk);
				}

			}else{ //父节点没选中
				CmLightPart cpart = cNode.getPart();
				WTPart cwtpart = CmBizObjUtil.getWTPartFromLightPart(cNode.getPart());
				String invcode = cpart.getWzk().getInvcode();
				String Clcode = cpart.getWzk().getClcode();

				CmLightPart ppart = pNode.getPart();
				WTPart pwtpart = CmBizObjUtil.getWTPartFromLightPart(pNode.getPart());

				String clcode = cpart.getWzk().getClcode();
				System.out.println("invcode="+invcode + ",   Clcode="+Clcode);

				CmIBAHelper phelper = new CmIBAHelper(pwtpart);
				CmIBAHelper chelper = new CmIBAHelper(cwtpart);

				System.out.println("invcode="+invcode + ",   Clcode="+Clcode);
				if(!CmCommonStringUtil.isEmpty(Clcode)){

					Wzk wzk = new Wzk();
//					wzk.setGsdm("100101");
//					wzk.setGcbm("1");
					String yzjd = phelper.getIBAValue("document_phase");
//					wzk.setCpbm(ErpUtil.getPhase(yzjd)+"#"+ppart.getPartNumber());
//					wzk.setVersion(ppart.getVersion());
//					wzk.setSjth(ppart.getPartNumber());
//					wzk.setSjthflbm(ppart.getWzk().getClflbm());
//					wzk.setSjthmc(ppart.getPartName());
//					wzk.setSjthmc(ppart.getPartName());
//					wzk.setSjthssxh(pwtpart.getContainerName());
//					wzk.setSjthjldwmc(ppart.getWzk().getMeasname());
//					wzk.setBtbz("");
//					wzk.setSfmr("");
//					wzk.setFxsl(ppart.getUseCount()+"");
					if(cwtpart!=null)
						wzk.setSsxh(cwtpart.getContainerName());
					yzjd = chelper.getIBAValue("document_phase");
					wzk.setYzjd(ErpUtil.getPhase(yzjd));
					wzk.setInvcode(ErpUtil.getPhase(yzjd)+"#"+cwtpart.getNumber() );
					wzk.setClflbm(cpart.getWzk().getInvclasscode());
					wzk.setZxsl(cpart.getUseCount()+"");
					wzk.setTh(cwtpart.getNumber());
					wzk.setThmc(cwtpart.getName());
					wzkList.add(wzk);
					Wzk clWzk = new Wzk();
					clWzk.setGsdm("100101");
					clWzk.setGcbm("1");
					clWzk.setCpbm(ErpUtil.getPhase(yzjd)+"#"+cpart.getPartNumber());
					clWzk.setVersion(cpart.getVersion());
					clWzk.setSjth(cpart.getPartNumber());
					clWzk.setSjthflbm(cpart.getWzk().getClflbm());
					clWzk.setSjthmc(cpart.getPartName());
					clWzk.setSjthssxh(pwtpart.getContainerName());
					clWzk.setSsxh(cwtpart.getContainerName());
					clWzk.setSjthjldwmc(cpart.getWzk().getMeasname());
					clWzk.setBtbz("");
					clWzk.setSfmr("");
					clWzk.setInvcode(Clcode);
					clWzk.setClflbm(cpart.getWzk().getInvclasscode());
					clWzk.setZxsl(cpart.getUseCount()+"");
					clWzk.setClmc(cpart.getWzk().getClName());
					clWzk.setInvtype(cpart.getWzk().getInvtype());
					clWzk.setDef2(cpart.getWzk().getDef2());
					clWzk.setDef3(cpart.getWzk().getDef3());
					clWzk.setMeasname(cpart.getWzk().getMeasname());
					clWzk.setDef4(cpart.getWzk().getDef4());
					wzkList.add(clWzk);


				}else if(!CmCommonStringUtil.isEmpty( invcode)){

					Wzk dbWzk = ErpToWCIntf.getWzkByInvcode("02", invcode);

					Wzk wzk = new Wzk();
//					wzk.setGsdm("100101");
//					wzk.setGcbm("1");
//					String yzjd = phelper.getIBAValue("document_phase");
//					wzk.setCpbm(ErpUtil.getPhase(yzjd)+"#"+ppart.getPartNumber());
//					wzk.setVersion(ppart.getVersion());
//					wzk.setSjth(ppart.getPartNumber());
//					wzk.setSjthflbm(ppart.getWzk().getClflbm());
//					wzk.setSjthmc(ppart.getPartName());
//					wzk.setSjthmc(ppart.getPartName());
//					wzk.setSjthssxh(pwtpart.getContainerName());
//					wzk.setSjthjldwmc(ppart.getWzk().getMeasname());
//					wzk.setBtbz("");
//					wzk.setSfmr("");
//					wzk.setFxsl(ppart.getUseCount()+"");
					if(cwtpart!=null)
						wzk.setSsxh(pwtpart.getContainerName());

					wzk.setInvcode(invcode );
					wzk.setClflbm(dbWzk.getInvclasscode());
					wzk.setZxsl(cpart.getUseCount()+"");
					wzk.setClmc(dbWzk.getInvname());
					wzk.setInvtype(dbWzk.getInvtype());
					wzk.setDef2(dbWzk.getDef2());
					wzk.setDef3(dbWzk.getDef3());
					wzk.setMeasname(dbWzk.getMeasname());
					wzk.setDef4(dbWzk.getDef4());
					wzkList.add(wzk);
				}

			}

		}


		return wzkList;
	}


}
