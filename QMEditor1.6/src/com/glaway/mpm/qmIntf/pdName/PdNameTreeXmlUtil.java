package com.glaway.mpm.qmIntf.pdName;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import com.glaway.mpm.model.PdName;
import com.glaway.mpm.model.PdNameType;
import com.glaway.mpm.model.ShopType;
import com.glaway.mpm.model.Skill;
import com.glaway.mpm.model.WorkShop;
import com.glaway.mpm.qmIntf.resourceTree.PdNameInfoPanel;
import com.glaway.mpm.resource.ResourceCache;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class PdNameTreeXmlUtil {

	public static PdNameTree generatePdNameTree(PdNameInfoPanel pdNameInfoPanel,NewTechnicsPart frame) {
		WorkShop rootObject = new WorkShop();
		rootObject.setName("工序名称");
		WorkShopNode root = new WorkShopNode(rootObject);
		System.out.println("------PdNameTreeXmlUtil.generatePdNameTree()----ResourceCache.workShops:"+ResourceCache.workShops);
		if(ResourceCache.workShops.isEmpty()) {
			ResourceIntf.getInitData();
		}
		List<WorkShop> workShops = ResourceCache.workShops;

		if (workShops != null && workShops.size() != 0) {
			for (WorkShop workShop : workShops) {
				WorkShopNode workShopNode = new WorkShopNode(workShop);
				List<PdName> pdNames = workShop.getPdNames();
				if (pdNames != null) {
					for (PdName pdName : pdNames) {
						PdNameNode pdNameNode = new PdNameNode(pdName);
						List<ShopType> shopTypes = pdName.getShopTypes();
						if (shopTypes != null) {
							for (ShopType shopType : shopTypes) {
								ShopTypeNode shopTypeNode = new ShopTypeNode(
										shopType);
								pdNameNode.add(shopTypeNode);
							}
						}
						workShopNode.add(pdNameNode);
					}
				}
				root.add(workShopNode);
			}
		}

		PdNameTree pdNameTree = new PdNameTree(root,pdNameInfoPanel,frame);
		return pdNameTree;
	}

	public static PdNameTree generateSkillTree(PdNameInfoPanel pdNameInfoPanel,NewTechnicsPart frame){
		WorkShop rootObject = new WorkShop();
		rootObject.setName("工种与工序");
		WorkShopNode root = new WorkShopNode(rootObject);

		WorkShop skillRootObject = new WorkShop();
		skillRootObject.setName("工种");
		WorkShopNode skillRoot = new WorkShopNode(skillRootObject);
		root.add(skillRoot);
		WorkShop stepRootObject = new WorkShop();
		stepRootObject.setName("工序名称");
		WorkShopNode stepRoot = new WorkShopNode(stepRootObject);

		root.add(stepRoot);

		if(ResourceCache.workShops.isEmpty()) {
			ResourceIntf.getInitData();
		}
		List<WorkShop> workShops = ResourceCache.workShops;
		if (workShops != null && workShops.size() != 0) {
			for (WorkShop workShop : workShops) {
				WorkShopNode workShopNode = new WorkShopNode(workShop);
				List<Skill> skillList = workShop.getSkillList();
				if (skillList != null) {
					for (Skill skill : skillList) {
						SkillNode skillNode = new SkillNode(skill);
						workShopNode.add(skillNode);
					}
				}
				skillRoot.add(workShopNode);
			}
		}

		PdNameType pdNameType = ResourceIntf.get812AllPdNameType();
		parseRootNode(stepRoot,pdNameType);
		PdNameTree pdNameTree = new PdNameTree(root,pdNameInfoPanel,frame);
		return pdNameTree;
	}

	public static PdNameTree generateSkillTreeRoot(PdNameInfoPanel pdNameInfoPanel,NewTechnicsPart frame){
		WorkShop rootObject = new WorkShop();
		rootObject.setName("工序名称树");
		WorkShopNode root = new WorkShopNode(rootObject);

		WorkShop stepRootObject = new WorkShop();
		stepRootObject.setName("工序名称");
		WorkShopNode stepRoot = new WorkShopNode(stepRootObject);

		WorkShop stepRootObject2 = new WorkShop();
		stepRootObject2.setName("工序名称2");
		WorkShopNode stepRoot2 = new WorkShopNode(stepRootObject2);

		stepRoot.add(stepRoot2);
		root.add(stepRoot);

		PdNameTree pdNameTree = new PdNameTree(root,pdNameInfoPanel,frame);
		return pdNameTree;
	}

	public static void parseRootNode(DefaultMutableTreeNode pdNameTreeNode,PdNameType pdNameType){
		if (pdNameType.getPdNameList() != null) {
			for (PdName equipment : pdNameType.getPdNameList()) {
				PdNameNode node1 = new PdNameNode(equipment);
				pdNameTreeNode.add(node1);
			}
		}
		parseNode(pdNameTreeNode,pdNameType.getPdNameTypeList());
	}

	public static void parseNode(DefaultMutableTreeNode pdNameTreeNode, List<PdNameType> pdNameType) {
		if (pdNameType != null && pdNameType.size() != 0) {
			for (PdNameType type : pdNameType) {
				List<PdNameType> eptype = type.getPdNameTypeList();
				PdNameTypeNode node = new PdNameTypeNode(type.getName());
				pdNameTreeNode.add(node);
				parseNode(node,eptype);
				if (type.getPdNameList() != null) {
					for (PdName equipment : type.getPdNameList()) {
						PdNameNode node1 = new PdNameNode(equipment);
						node.add(node1);
					}
					pdNameTreeNode.add(node);
				}
			}
		}
	}
}