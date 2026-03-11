package com.glaway.mpm.qmIntf.resourceTree;

import java.util.List;

import com.glaway.mpm.model.Tool;
import com.glaway.mpm.model.ToolType;

public class ResourceTreeXmlUtil {

	// public static ResourceTree generateToolTree(ToolType toolType) {
	// ResourceTreeNode root = new ResourceTreeNode("工艺资源树");
	// if (toolType != null) {
	// parseNode(root, toolType.getToolTypes());
	// }
	// ResourceTree toolTree = new ResourceTree(root);
	// return toolTree;
	// }

	public static void parseNode(ResourceTreeNode toolTreeNode,
			List<ToolType> toolType) {
		if (toolType != null && toolType.size() != 0) {
			for (ToolType type : toolType) {
				if (type.getTools() == null) {
					ResourceTreeNode node = new ResourceTreeNode(type.getName());
					toolTreeNode.add(node);
					parseNode(node, type.getToolTypes());
				} else {
					ResourceTreeNode treeNode = new ResourceTreeNode(
							type.getName());
					for (Tool tool : type.getTools()) {
						ResourceNode node = new ResourceNode(tool);
						treeNode.add(node);
					}
					toolTreeNode.add(treeNode);
				}
			}
		}
	}

	public static ResourceTree generateResourceTree() {
		return null;
	}
}