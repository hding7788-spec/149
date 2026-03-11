package com.glaway.mpm.qmIntf.template;

import java.io.File;

import com.glaway.mpm.visual.log.VaLogger;

public class PaceTemplateUtil {
	private static VaLogger logger = VaLogger.getLogger(PaceTemplateUtil.class);
	public static TpTreeNode getLocalTemplates(String filePath) {
		TpTreeNode stTreeNode = new TpTreeNode("本地工步模板",TpTreeNode.STEP_TYPE_LOCAL);
		File templateDirectory = new File(filePath);
		if (templateDirectory.exists() && templateDirectory.isDirectory()) {
			File[] files = templateDirectory.listFiles();
			TpTreeNode treeNode = new TpTreeNode("工步",TpTreeNode.STEP_TYPE_LOCAL);
			for (File directory : files) {
				for (File file : directory.listFiles()) {
					treeNode.add(new TpNode(null, null, file.getName()));
				}
				stTreeNode.add(treeNode);
			}
		}
		return stTreeNode;
	}
	public static StepTree getTemplates(String filePath) {
		TpTreeNode rootNode = new TpTreeNode("工步模板");
		rootNode.add(getLocalTemplates(filePath));
		StepTree stepTree = new StepTree(rootNode);
		return stepTree;
	}
}
