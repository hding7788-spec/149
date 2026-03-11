package com.glaway.mpm.visual.view.tree;

import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpPaceNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpPartNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpStepNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpTecnicsNode;

import javax.swing.tree.TreeNode;
import java.util.Enumeration;

public class VaTreeNodeModelComparator extends VaTreeNodeComparator {

	public int compare(VaTreeNode o1, VaTreeNode o2) {

		//QMEditor-TODO:参装件联动
		//1 ZPBOM
		if(o1.getParent()!= null && o1.getParent().getParent() != null && "ZPBOM".equals(o1.getParent().getParent().toString())){
			//1.1 工艺树
			if(o2 instanceof DpPartNode){
				if(o2.getOccId().equals(o1.getOccId())){
					return EQUAL;
				}
			//1.2 FPBOM
			}else if(o2 instanceof DpStepNode) {
				DpStepNode dpStepNode = (DpStepNode) o2;
				Enumeration<VaTreeNode> stepChildren = dpStepNode.children();
				while (stepChildren.hasMoreElements()) {
					VaTreeNode childNode = stepChildren.nextElement();
					if (childNode instanceof DpPaceNode) {
						DpPaceNode dpPaceNode = (DpPaceNode) childNode;
						Enumeration<VaTreeNode> paceChildren = dpPaceNode.children();
						while (paceChildren.hasMoreElements()) {
							VaTreeNode vaTreeNode = paceChildren.nextElement();
							if (vaTreeNode instanceof DpPartNode) {
								DpPartNode dpPartNode = (DpPartNode) vaTreeNode;
								if (dpPartNode.getOccId().equals(o1.getOccId())) {
									return EQUAL;
								}
							}
						}
					} else if (childNode instanceof DpPartNode) {
						DpPartNode dpPartNode = (DpPartNode) childNode;
						if (dpPartNode.getOccId().equals(o1.getOccId())) {
							return EQUAL;
						}
					}
				}
			} else if(o2.getParent() != null && "FPBOM".equals(o2.getParent().toString())){


			//1.3 ZPBOM
			}else if(o2.getParent()!= null && o2.getParent().getParent() != null && "ZPBOM".equals(o2.getParent().getParent().toString())){
				if(o2.getOccId().equals(o1.getOccId())){
					return EQUAL;
				}
			}
		//2 工艺树
		}else if(o1.getParent() != null && (o1.getParent() instanceof DpStepNode || o1.getParent() instanceof DpPaceNode ||o1.getParent() instanceof DpTecnicsNode)){
			//2.1 ZPBOM
			if(o2.getParent()!= null && o2.getParent().getParent() != null && "ZPBOM".equals(o2.getParent().getParent().toString()) && o1 instanceof DpPartNode){
				if(o1.getOccId().equals(o2.getOccId())){
					return EQUAL;
				}
			//2.2 FPBOM
			}else if(o2.getParent() != null && "FPBOM".equals(o2.getParent().toString()) && o1 instanceof DpPartNode){
				//2.2.1 工艺树打包状态
				if(o1.getOccId().contains(",")){
					//2.2.1.1 FPBOM打包状态
					if(o2.getOccId().contains(",")){
						String[] occids = o1.getOccId().split(",");
						boolean flag = false;
						for(String occid : occids){
							if(o2.getOccId().contains(occid)){
								flag = true;
							}else{
								flag = false;
							}
						}
						if(flag){
							return EQUAL;
						}
					//2.2.1.2 FPBOM拆包状态
					}else{
						if(o1.getOccId().contains(o2.getOccId())){
							return EQUAL;
						}
					}
				//2.2.2 工艺树拆包状态
				}else{
					if(o2.getOccId().equals(o1.getOccId())){

						return EQUAL;
					}
				}
			//2.3 工艺树
			}else if(o2.getParent() != null && (o2.getParent() instanceof DpStepNode || o2.getParent() instanceof DpPaceNode ||o2.getParent() instanceof DpTecnicsNode)){
				if(o2.getOccId().equals(o1.getOccId())){
					if(o1 instanceof DpPartNode && o2 instanceof DpPartNode){
						String zcmark1 = ((DpPartNode) o1).getZcmark();
						String zcmark2 = ((DpPartNode) o2).getZcmark();
						TreeNode parent1 = o1.getParent();
						TreeNode parent2 = o2.getParent();
						String oid1 = "1";
						if (parent1 instanceof DpStepNode) {
							DpStepNode stepNode = (DpStepNode) parent1;
							oid1 = stepNode.getStep().getOid();
						} else if (parent1 instanceof DpPaceNode) {
							DpPaceNode paceNode = (DpPaceNode) parent1;
							oid1 = paceNode.getPace().getOid();
						}
						String oid2 = "2";
						if (parent2 instanceof DpStepNode) {
							DpStepNode stepNode = (DpStepNode) parent2;
							oid2 = stepNode.getStep().getOid();
						} else if (parent2 instanceof DpPaceNode) {
							DpPaceNode paceNode = (DpPaceNode) parent2;
							oid2 = paceNode.getPace().getOid();
						}
						if (oid1.equals(oid2) && zcmark1.equals(zcmark2)) {
							return EQUAL;
						}
					}else{
						return EQUAL;
					}
				}
			}
		//3 FPBOM
		}else if(o1.getParent() != null && "FPBOM".equals(o1.getParent().toString())){
			//3.1 工艺树
			if(o2 instanceof DpPartNode){

				if (o1.getOccId().equals(o2.getOccId())) {
					return EQUAL;
				}

			}else if(o2 instanceof DpStepNode) {
				DpStepNode dpStepNode = (DpStepNode) o2;
				Enumeration<VaTreeNode> stepChildren = dpStepNode.children();
				while (stepChildren.hasMoreElements()) {
					VaTreeNode childNode = stepChildren.nextElement();
					if (childNode instanceof DpPaceNode) {
						DpPaceNode dpPaceNode = (DpPaceNode) childNode;
						Enumeration<VaTreeNode> paceChildren = dpPaceNode.children();
						while (paceChildren.hasMoreElements()) {
							VaTreeNode vaTreeNode = paceChildren.nextElement();
							if (vaTreeNode instanceof DpPartNode) {
								DpPartNode dpPartNode = (DpPartNode) vaTreeNode;
								if (dpPartNode.getOccId().equals(o1.getOccId())) {
									return EQUAL;
								}
							}
						}
					} else if (childNode instanceof DpPartNode) {
						DpPartNode dpPartNode = (DpPartNode) childNode;
						if (dpPartNode.getOccId().equals(o1.getOccId())) {
							return EQUAL;
						}
					}
				}

			}
			//3.2 ZPBOM
			else if(o2.getParent()!= null && o2.getParent().getParent() != null && "ZPBOM".equals(o2.getParent().getParent().toString())){


			//3.3 FPBOM
			}else if(o2.getParent() != null && "FPBOM".equals(o2.getParent().toString())){
				if(o2.getOccId().equals(o1.getOccId())){
					return EQUAL;
				}
			}
		}

//		if (o1.getPart() != null && o2.getPart() != null) {
//			String o1Num = o1.getPart().getNumber();
//			String o2Num = o2.getPart().getNumber();
//
//			if (o1Num != null && o2Num != null && o1Num.equals(o2Num)) {
//				if (o1.getOccId().equals(o2.getOccId())) {
//					return EQUAL;
//				}
//
//				if(o1.getOccId().split(",").length > 1){
//					if (o1.getOccId().contains(o2.getOccId())) {
//						return EQUAL;
//					}
//				}
//				if(o2.getOccId().split(",").length > 1){
//					if (o2.getOccId().contains(o1.getOccId())) {
//						return EQUAL;
//					}
//				}
//			}
//		}

		return UNDECIDABLE;
	}
}
