package com.glaway.mpm.qmIntf.template;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.glaway.mpm.model.TpType;
import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TemplateIntf;

public class StepTemplateUtil {

	private static VaLogger logger = VaLogger.getLogger(StepTemplateUtil.class);

	/**
	 * @Description:type(null:获取本地所有的模板)
	 */
	public static TpTreeNode getLocalTemplates(String filePath, String type) {
		TpTreeNode stTreeNode = new TpTreeNode("本地工序模板",TpTreeNode.STEP_TYPE_LOCAL);
		File templateDirectory = new File(filePath);
		if (templateDirectory.exists() && templateDirectory.isDirectory()) {
			File[] files = templateDirectory.listFiles();
			for (File directory : files) {
//				if (type != null && !directory.getName().endsWith(type)) {
//					continue;
//				}
				String name = typeSwitch(directory.getName());
				if (name == null) {
					continue;
				}
				TpTreeNode treeNode = new TpTreeNode(name,TpTreeNode.STEP_TYPE_LOCAL);
				for (File file : directory.listFiles()) {
					if (file.isDirectory()) {
						treeNode.add(new TpNode(null, null, file.getName()));
					}
				}
				stTreeNode.add(treeNode);
			}
		}

		return stTreeNode;
	}

	public static StepTree getTemplates(String filePath, String type) {
		TpTreeNode rootNode = new TpTreeNode("工序模板");

		rootNode.add(getLocalTemplates(filePath, type));
		List<TpType> tptypes= null;
		try{
			tptypes = TemplateIntf.getAllStepTemplates();
		}catch(Exception e){
			e.printStackTrace();
		}
		TpTreeNode publicNode = new TpTreeNode("公共工序模板",TpTreeNode.STEP_TYPE_PUBLIC);
		if(tptypes!=null){
			for (TpType tpType : tptypes) {
				TpTreeNode node = new TpTreeNode(tpType.getName(),TpTreeNode.STEP_TYPE_PUBLIC);
				publicNode.add(node);
				TpTreeXmlUtil.parseTyType(tpType, node, true);
			}
		}
		rootNode.add(publicNode);
		StepTree stepTree = new StepTree(rootNode);
		return stepTree;
	}

	public static String typeSwitch(String type) {
		String name = null;
//		if ("assembleTemplate".equals(type)) {
//			name = "装配工艺";
//		} else if ("partTemplate".equals(type)) {
//			name = "零件工艺";
//		} else {
//			logger.error("type error= " + type);
//		}

		String[][] technicsTypes = LoadConfig.getInstance().getTechnicsType();
		boolean flag = false;
		for(int i=0;i<technicsTypes[0].length;i++){
			if(technicsTypes[0][i].equals(type)){
				name = technicsTypes[1][i];
				flag = true;
				break;
			}
		}
		if(!flag){
			logger.error("工艺类型出错");
		}

		return name;
	}
}