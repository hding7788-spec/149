package com.glaway.mpm.qmIntf.frock;

import java.util.List;

import com.glaway.mpm.model.FkType;
import com.glaway.mpm.model.Frock;

public class FkTreeXmlUtil {

	public static FkTree generateFkTree(FkType fkType) {
		FkTreeNode root = new FkTreeNode("工装");
		if (fkType != null) {
			parseNode(root, fkType.getFkTypes());
		}
		FkTree fkTree = new FkTree(root);
		return fkTree;
	}

	public static void parseNode(FkTreeNode fkTreeNode, List<FkType> fkType) {
		if (fkType != null && fkType.size() != 0) {
			for (FkType type : fkType) {
				if (type.getFrocks() == null) {
					FkTreeNode node = new FkTreeNode(type.getName());
					fkTreeNode.add(node);
					parseNode(node, type.getFkTypes());
				} else {
					FkTreeNode treeNode = new FkTreeNode(type.getName());
					for (Frock frock : type.getFrocks()) {
						FkNode node = new FkNode(frock);
						treeNode.add(node);
					}
					fkTreeNode.add(treeNode);
				}
			}
		}
	}
}