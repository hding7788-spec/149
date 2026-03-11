package com.glaway.mpm.qmIntf.tool;

import java.util.List;

import com.glaway.mpm.model.Tool;
import com.glaway.mpm.model.ToolType;

public class ToolTreeXmlUtil {

	public static ToolTree generateToolTree(ToolType toolType) {
		ToolTreeNode root = new ToolTreeNode("工具");
		if (toolType != null) {
			parseNode(root, toolType.getToolTypes());
		}
		ToolTree toolTree = new ToolTree(root);
		return toolTree;
	}

	public static void parseNode(ToolTreeNode toolTreeNode, List<ToolType> toolType) {
		if (toolType != null && toolType.size() != 0) {
			for (ToolType type : toolType) {
				if (type.getTools() == null) {
					ToolTreeNode node = new ToolTreeNode(type.getName());
					toolTreeNode.add(node);
					parseNode(node, type.getToolTypes());
				} else {
					ToolTreeNode treeNode = new ToolTreeNode(type.getName());
					for (Tool tool : type.getTools()) {
						ToolNode node = new ToolNode(tool);
						treeNode.add(node);
					}
					toolTreeNode.add(treeNode);
				}
			}
		}
	}
}