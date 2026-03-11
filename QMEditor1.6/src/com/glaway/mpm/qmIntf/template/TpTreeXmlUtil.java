package com.glaway.mpm.qmIntf.template;

import java.io.File;
import java.util.List;

import com.glaway.mpm.model.ProcessTemplate;
import com.glaway.mpm.model.TpType;
import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.wcIntf.TemplateIntf;

public class TpTreeXmlUtil {

	/***
	 * xml->TpTree obj->TpTree
	 *
	 * @param xml
	 * @return TpTree
	 */
	public static TpTree searchTemplates(String filePath, String type) {
		return addTpToTpTree(xmlToTpTree(filePath, type), TemplateIntf.getProcessTemplates(type), true, "工艺");
	}

	public static void parseTyType(TpType tpType, TpTreeNode tpTreeNode,
			boolean flag) {
		if (tpType != null) {
			List<TpType> types = tpType.getTpTypes();
			if (types != null && types.size() != 0) {
				for (TpType type : types) {
					TpTreeNode treeNode = new TpTreeNode(type.getName(),TpTreeNode.STEP_TYPE_PUBLIC);
					tpTreeNode.add(treeNode);
					parseTyType(type, treeNode, flag);
				}
			}
			if (flag) {
				List<ProcessTemplate> templates = tpType.getProcessTemplates();
				if (templates != null && templates.size() != 0) {
					for (ProcessTemplate template : templates) {
						TpNode node = new TpNode(template.getOid(),
								template.getNumber(), template.getName());
						tpTreeNode.add(node);
					}
				}
			}
		}
	}

	public static TpTree addTpToTpTree(TpTree tpTree, TpType tpType,
			boolean flag, String useType) {
		TpTreeNode root = tpTree.getRoot();
		TpTreeNode tpTreeNode = new TpTreeNode("公共" + useType + "模板库");
		root.add(tpTreeNode);

		TpTreeNode typeNode = new TpTreeNode(tpType.getName());
		tpTreeNode.add(typeNode);

		parseTyType(tpType, typeNode, flag);

		return tpTree;

	}

	/***
	 * xml->obj
	 *
	 * @param xml
	 * @return TpTreeNode
	 */

	public static TpTree xmlToTpTree(String filePath, String type) {

		TpTree tpTree = null;
		if (!filePath.equals(File.separator)) {
			filePath += File.separator;
		}
		File fileDirectory = new File(filePath);
		TpTreeNode rootNode = new TpTreeNode("tpTree");
		TpTreeNode node = new TpTreeNode("个人工艺模板库");
		rootNode.add(node);
		String [][] technicsTypes = LoadConfig.getInstance().getTechnicsType();
		if (fileDirectory.isDirectory()) {
			File tmpDir = null;
			for(int i=0;i<technicsTypes[0].length;i++){
				if(technicsTypes[0][i].equals(type)){
					tmpDir = new File(filePath + technicsTypes[1][i]);
				}
			}

//			if ("assembleTemplate".equals(type)) {
//				tmpDir = new File(filePath + "装配工艺");
//			} else if ("paintTemplate".equals(type)) {
//				tmpDir = new File(filePath + "油漆工艺");
//			} else if ("machiningTemplate".equals(type)) {
//				tmpDir = new File(filePath + "机加工艺");
//			} else if ("mountTemplate".equals(type)) {
//				tmpDir = new File(filePath + "装联工艺");
//			}

			if (type != null) {
				tmpDir = new File(filePath + type);
				if (!tmpDir.exists()) {
					tmpDir.mkdirs();
				}
			} else {
				tmpDir = new File(filePath);
				tmpDir.mkdirs();
			}

			for (File file : fileDirectory.listFiles()) {
				if (file.isDirectory()) {
					String name = file.getName();
					boolean flag = false;
					if (type != null) {
						if("Process_FJSJJGTemplate".equals(type)||"Process_MPTemplate".equals(type)){
							if ("Process_FJSJJGTemplate".equals(name)||"Process_MPTemplate".equals(name)) {

							}else{
								if (!name.equals(type)) {
									continue;
								}
							}
						}else{
							if (!name.equals(type)) {
								continue;
							}
						}

					}
					for(int i=0;i<technicsTypes[0].length;i++){
						if(technicsTypes[0][i].equals(name)){
							name = technicsTypes[1][i];
							flag = true;
						}
					}
//					if ("assembleTemplate".equals(name)) {
//						name = "装配工艺";
//						flag = true;
//					} else if ("paintTemplate".equals(name)) {
//						name = "油漆工艺";
//						flag = true;
//					} else if ("machiningTemplate".equals(name)) {
//						name = "机加工艺";
//						flag = true;
//					} else if ("mountTemplate".equals(name)) {
//						name = "装联工艺";
//						flag = true;
//					}

					if (flag) {
						TpTreeNode newChild = new TpTreeNode(name);
						node.add(newChild);
						for (File subFile : file.listFiles()) {
							if (subFile.isDirectory()) {
								String subFileName = subFile.getName();
								TpNode newSubChild = new TpNode("", "", subFileName);
								newChild.add(newSubChild);
							}
						}
					}
				}
			}
		} else {
			try {
				throw new Exception("本地工艺模板路径不正确");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		tpTree = new TpTree(rootNode);
		return tpTree;
	}
}