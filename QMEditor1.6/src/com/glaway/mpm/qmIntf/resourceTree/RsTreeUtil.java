package com.glaway.mpm.qmIntf.resourceTree;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import com.glaway.mpm.model.FkType;
import com.glaway.mpm.model.KtType;
import com.glaway.mpm.model.Tool;
import com.glaway.mpm.model.ToolType;
import com.glaway.mpm.qmIntf.measure.MeasureInfoPanel;
import com.glaway.mpm.qmIntf.measure.MeasureTree;
import com.glaway.mpm.qmIntf.measure.MeasureTreeNode;
import com.glaway.mpm.qmIntf.resourceTree.model.FkTreeNode;
import com.glaway.mpm.qmIntf.resourceTree.model.KtTreeNode;
import com.glaway.mpm.qmIntf.resourceTree.model.ToolNode;
import com.glaway.mpm.qmIntf.resourceTree.model.ToolTreeNode;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class RsTreeUtil {

	public static final Object[] BLANKARRAY = new Object[] {};

	public static KtTree generateKtTree(RsInfoPanel rsInfoPanel,NewTechnicsPart frame) {
		KtTree ktTree = new KtTree(null,rsInfoPanel,frame);
		KtTreeNode root = new KtTreeNode("刀具树");
		KtTreeNode ktTreeNode = new KtTreeNode("刀具");
		root.add(ktTreeNode);
		KtType ktType = ResourceIntf.getAllKnifeTools();
		if (ktType != null) {
			RsTreeUtil.getData(KtType.class, ktType, ktTreeNode);
		}
		ktTree.setRoot(root);
		ktTree.setRootVisible(false);
		return ktTree;
	}

	public static KtTree generateKtTreeRoot(RsInfoPanel rsInfoPanel,NewTechnicsPart frame) {
		KtTree ktTree = new KtTree(null,rsInfoPanel,frame);
		KtTreeNode root = new KtTreeNode("刀具树");
		KtTreeNode ktTreeNode = new KtTreeNode("刀具");
		KtTreeNode ktTreeNode2 = new KtTreeNode("刀具2");
		ktTreeNode.add(ktTreeNode2);
		root.add(ktTreeNode);
		ktTree.setRoot(root);
		ktTree.setRootVisible(false);
		return ktTree;
	}

	public static ResourceTree generateResourceTree(RsInfoPanel rsInfoPanel, NewTechnicsPart frame) {
		ResourceTree rsTree = new ResourceTree(null, rsInfoPanel, frame);
		ResourceTreeNode root = new ResourceTreeNode("工装树");

		FkTreeNode fkTreeNode = new FkTreeNode("工装");
		root.add(fkTreeNode);
		FkType fkType = ResourceIntf.getAllFrocks();
		if (fkType != null) {
			RsTreeUtil.getData(FkType.class, fkType, fkTreeNode);
		}
		rsTree.setRoot(root);
		rsTree.setRootVisible(false);
		return rsTree;
	}

	public static ResourceTree generateResourceTreeRoot(RsInfoPanel rsInfoPanel, NewTechnicsPart frame) {
		ResourceTree rsTree = new ResourceTree(null, rsInfoPanel, frame);
		ResourceTreeNode root = new ResourceTreeNode("工装树");

		FkTreeNode fkTreeNode = new FkTreeNode("工装");
		FkTreeNode fkTreeNode2 = new FkTreeNode("工装2");
		fkTreeNode.add(fkTreeNode2);
		root.add(fkTreeNode);
		rsTree.setRoot(root);
		rsTree.setRootVisible(false);
		return rsTree;
	}

	public static MeasureTree generateMeasureTree(MeasureInfoPanel rsInfoPanel, NewTechnicsPart frame) {
		MeasureTree rsTree = new MeasureTree(null, rsInfoPanel, frame);
		MeasureTreeNode root = new MeasureTreeNode("量具树");

		ToolType measureType = ResourceIntf.getAllMeasures();
		ToolTreeNode measureTreeNode = new ToolTreeNode("量具");
		root.add(generateToolNode(measureType, measureTreeNode));

		rsTree.setRoot(root);
		rsTree.setRootVisible(false);
		return rsTree;
	}

	public static MeasureTree generateMeasureTreeRoot(MeasureInfoPanel rsInfoPanel, NewTechnicsPart frame) {
		MeasureTree rsTree = new MeasureTree(null, rsInfoPanel, frame);
		MeasureTreeNode root = new MeasureTreeNode("量具树");

		ToolTreeNode measureTreeNode = new ToolTreeNode("量具");
		ToolTreeNode measureTreeNode2 = new ToolTreeNode("量具2");
		measureTreeNode.add(measureTreeNode2);
		root.add(measureTreeNode);

		rsTree.setRoot(root);
		rsTree.setRootVisible(false);
		return rsTree;
	}

	public static void getData(Class<?> clazz, Object object,
			DefaultMutableTreeNode treeNode) {
		List<?> types = null;
		List<?> objects = null;
		String prefixName = null;
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			String fieldTypeName = field.getType().getName();
			String fieldName = field.getName();
			if (fieldTypeName.equals("java.util.List")) {
				if (fieldName.endsWith("Types")) {
					prefixName = firstToUpperCaseSubString(fieldName, "Types");
					types = (List<?>) callGetMethod(clazz, fieldName, object);
				} else {
					objects = (List<?>) callGetMethod(clazz, fieldName, object);
				}
			}
		}
		generatorTree(clazz, types, objects, prefixName, treeNode);
	}

	public static ToolTreeNode generateToolNode(ToolType toolType,
			ToolTreeNode toolTreeNode) {
		if (toolType != null) {
			parseNode(toolTreeNode, toolType.getToolTypes());
			if(toolType.getTools()!=null)
			for (Tool tool : toolType.getTools()) {
				ToolNode node = new ToolNode(tool);
				toolTreeNode.add(node);
			}
		}
		return toolTreeNode;
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

	public static void generatorTree(Class<?> clazz, List<?> types,
			List<?> objects, String prefixName, DefaultMutableTreeNode treeNode) {
		if (types != null) {
			for (int i = 0; i < types.size(); i++) {
				DefaultMutableTreeNode subTreeNode = (DefaultMutableTreeNode) newInstanceByClassName(
						prefixName + "TreeNode",
						new Class[] { String.class },
						new Object[] { callGetMethod(clazz, "name", types.get(i)) });
				getData(clazz, types.get(i), subTreeNode);
				treeNode.add(subTreeNode);
			}
		}

		if (objects != null) {
			for (int i = 0; i < objects.size(); i++) {
				DefaultMutableTreeNode subTreeNode = (DefaultMutableTreeNode) newInstanceByClassName(
						prefixName + "Node",
						new Class[] { objects.get(i) .getClass() },
						new Object[] { objects.get(i) });
				treeNode.add(subTreeNode);
			}
		}
	}

	public static String firstToUpperCaseSubString(String str, String toString) {
		if (str == null || str.equals("")) {
			return "";
		}
		return str.substring(0, 1).toUpperCase()
				+ str.substring(1, str.lastIndexOf(toString));
	}

	public static String firstToUpperCase(String str) {
		if (str == null || str.equals("")) {
			return "";
		}
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	public static Object newInstanceByClassName(String className,
			Class<?>[] clazzs, Object[] objects) {
		try {
			Class<?> clazz = Class.forName("com.glaway.mpm.qmIntf.resourceTree.model." + className);
			Constructor<?> constructor = clazz.getDeclaredConstructor(clazzs);
			return constructor.newInstance(objects);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String generateGetMethodName(String field) {
		return "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
	}

	public static Object callGetMethod(Class<?> clazz, String fieldName,
			Object object) {
		Object obj = null;
		try {
			String methodName = generateGetMethodName(fieldName);
			Method method = clazz.getDeclaredMethod(methodName, new Class[] {});
			obj = method.invoke(object, BLANKARRAY);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return obj;
	}
}
