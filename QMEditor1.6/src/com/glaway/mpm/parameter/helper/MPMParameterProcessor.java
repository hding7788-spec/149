package com.glaway.mpm.parameter.helper;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.parameter.commonui.CommonTableModel;
import com.glaway.mpm.parameter.designui.NewCommonParamTablePanel;
import com.glaway.mpm.parameter.designui.NewSpecialParamTablePanel;
import com.glaway.mpm.parameter.model.GWParamTableTypeMaster;
import com.glaway.mpm.parameter.model.data.*;
import com.glaway.mpm.parameter.model.tree.*;
import com.glaway.mpm.parameter.service.ProcessParameterToWCIntf;
import com.glaway.mpm.parameter.ui.MPMParameterMainFrame;
import com.glaway.mpm.parameter.util.ZipUtil2;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.FilesUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.List;

public class MPMParameterProcessor {

	private static VaLogger logger = VaLogger.getLogger(MPMParameterProcessor.class.getName());

	/** 工艺类别 */
	private static List<CmTechnicsType> technicsTypes;
	/** 所有参数类型 */
	private static List<CmParameterType> parameterTypes;
	/** 所有参数记录表类型 */
	private static List<CmTechnicsType> paramTableTypes;
	/** 所有叶子节点参数类型 */
	private static List<CmParameterType> leafParameterTypes;

	/**
	 * 获取临时工作空间路径
	 *
	 * @return
	 */
	public static String getParamWorkSpace() {
		return MPMParameterMainFrame.getImageFolder();
	}

	/**
	 * 获取工艺类型下拉框组件
	 *
	 * @return
	 */
	public static JComboBox getTechnicsTypeComboBox() {
		if (technicsTypes == null) {
			technicsTypes = queryTechnicsTypes();
		}

		JComboBox technicsTypeComboBox = new JComboBox();
//		technicsTypeComboBox.addItem(" ");
//		for (CmTechnicsType cmTechnicsType : technicsTypes) {
//			technicsTypeComboBox.addItem(cmTechnicsType.getName());
//		}
		return technicsTypeComboBox;
	}

	/**
	 * 通过工艺类型OID获取工艺类型名称
	 *
	 * @param technicsTypeId
	 * @return
	 */
	public static String getTechnicsTypeNameById(String technicsTypeId) {
		if (technicsTypes == null) {
			technicsTypes = queryTechnicsTypes();
		}

		for (CmTechnicsType cmTechnicsType : technicsTypes) {
			if (String.valueOf(cmTechnicsType.getOid()).equals(technicsTypeId)) {
				return cmTechnicsType.getName();
			}
		}
		return "";
	}

	/**
	 * 通过工艺类型名称获取工艺类型的OID
	 *
	 * @param technicsTypeName
	 * @return
	 */
	public static String getTechnicsTypeIdByName(String technicsTypeName) {
		if (technicsTypes == null) {
			technicsTypes = queryTechnicsTypes();
		}

		for (CmTechnicsType cmTechnicsType : technicsTypes) {
			if (String.valueOf(cmTechnicsType.getName()).equals(technicsTypeName)) {
				return String.valueOf(cmTechnicsType.getOid());
			}
		}
		return "";
	}

	/**
	 * 加载参数类型树结构
	 */
	public static void loadQualityTree() {
		JTree qualityTree = MPMParameterMainFrame.getLeftPanel().getQualityTree();
		XWTreeNode root = (XWTreeNode) qualityTree.getModel().getRoot();
		DefaultTreeModel model = (DefaultTreeModel) qualityTree.getModel();
		Enumeration<?> enums = root.children();
		try {
			while (enums.hasMoreElements()) {
				XWTreeNode node = (XWTreeNode) enums.nextElement();
				if ("检验特性管理".equals(node.toString())) {
					node.removeAllChildren();
					model.reload(node);
					qualityTree.setShowsRootHandles(false);
					expandParameterType(qualityTree, node);
				} else if ("检验记录表管理".equals(node.toString())) {
					Enumeration<?> enums2 = node.children();
					while (enums2.hasMoreElements()) {
						XWTreeNode node2 = (XWTreeNode) enums2.nextElement();
						node2.removeAllChildren();
						if ("特殊检查项表记录".equals(node2.toString())) {
							expandParamTableType(qualityTree, node2);
						} else if ("通用检查项定义".equals(node2.toString())) {
							CmParamTableType paramTableType = (CmParamTableType) node2.getTreeObject().getTreeNode();
							paramTableType = getCommonParamTableType(paramTableType);
							node2.getTreeObject().setTreeNode(paramTableType);
						}
					}
				}
			}
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}

//		expandAllNode(root, qualityTree);

		TreePath p = new TreePath(root.getPath());
		qualityTree.expandPath(p);
		qualityTree.scrollPathToVisible(p);
		qualityTree.setSelectionPath(p);
		qualityTree.repaint();
	}

	private static void expandParameterType(JTree qualityTree, XWTreeNode node)
			throws RemoteException, InvocationTargetException {
		parameterTypes = ProcessParameterToWCIntf.loadParamTypeData();
		XWParameterTypeTreeObject parameterTypeTreeObject = null;
		for (CmParameterType cmParameterType : parameterTypes) {
			parameterTypeTreeObject = new XWParameterTypeTreeObject(cmParameterType);
			XWTreeNode treeNode = new XWTreeNode(parameterTypeTreeObject);
			node.add(treeNode);

			expandSubNode(treeNode);
		}
	}

	private static void expandParamTableType(JTree qualityTree, XWTreeNode node)
			throws RemoteException, InvocationTargetException {
		paramTableTypes = ProcessParameterToWCIntf.loadParamTableTypeData();
		XWTechnicsTypeTreeObject technicsTypeTreeObject = null;
		for (CmTechnicsType technicsType : paramTableTypes) {
			technicsTypeTreeObject = new XWTechnicsTypeTreeObject(technicsType);
			XWTreeNode treeNode = new XWTreeNode(technicsTypeTreeObject);
			node.add(treeNode);

			expandSubNode(treeNode);
		}
	}

	public static void expandAllNode(XWTreeNode node, JTree technicsTree) {
		if (node != null) {
			List<XWTreeNode> list = new ArrayList<XWTreeNode>();
			Enumeration<?> en = node.depthFirstEnumeration();
			while (en.hasMoreElements()) {
				Object temp = en.nextElement();
				if ((temp instanceof XWTreeNode)) {
					XWTreeNode treeNode = (XWTreeNode) temp;
					list.add(treeNode);
				}
			}

			for (int i = 0; i < list.size(); i++) {
				XWTreeNode no = list.get(i);
				TreePath p = new TreePath(no.getPath());
				technicsTree.expandPath(p);
			}
		}
	}

	public static void expandSubNode(XWTreeNode node) {
		if (node != null) {
			XWTreeObject treeObject = node.getTreeObject();
			if ((treeObject instanceof XWParameterTypeTreeObject)
					|| (treeObject instanceof XWTechnicsTypeTreeObject)
					|| (treeObject instanceof XWParamTableTypeTreeObject)) {
				node.expandNode();
				for (int i = 0; i < node.getChildCount(); i++) {
					XWTreeNode child = (XWTreeNode) node.getChildAt(i);
					expandSubNode(child);
				}
			}
		}
	}

	public static void setParameterTypeTableValues(XWTreeNode parentNode) {
//		MPMParameterMainFrame.getParameterTypeManagerPanel().getTablePanel().setTableValues(parentNode);
	}

	/**
	 * 获取叶子节点的参数类型
	 *
	 * @return
	 */
	public static List<CmParameterType> getAllLeafParameterTypes() {
		try {
			if (leafParameterTypes == null) {
				if (parameterTypes == null) {
					parameterTypes = ProcessParameterToWCIntf.loadParamTypeData();
				}

				leafParameterTypes = new ArrayList<CmParameterType>();
				for (CmParameterType parameterType : parameterTypes) {
					getAllLeafParameterTypes(parameterType, leafParameterTypes);
				}
			}
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return leafParameterTypes;
	}

	public static List<CmParameterType> getAllChildParameterTypes(String technicsType){
		try {
//			if (leafParameterTypes == null) {
				CmParameterType cmParameterType =ProcessParameterToWCIntf.loadParamTypeData(technicsType);
				leafParameterTypes = new ArrayList<CmParameterType>();
				getAllLeafParameterTypes(cmParameterType, leafParameterTypes);
//			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return leafParameterTypes;
	}
	private static void getAllLeafParameterTypes(CmParameterType parentType, List<CmParameterType> leafParameterTypes) {
		List<CmParameterType> childParameterTypes = parentType.getChildParameterTypes();
		/*if (childParameterTypes == null || childParameterTypes.isEmpty()) {
			parentType.setShortcut(CommonUtil.convertShortcut(parentType.getName()));
			leafParameterTypes.add(parentType);
		} else {
			for (CmParameterType cmParameterType : childParameterTypes) {
				getAllLeafParameterTypes(cmParameterType, leafParameterTypes);
			}
		}*/
		for (CmParameterType cmParameterType : childParameterTypes) {
			cmParameterType.setShortcut(CommonUtil.convertShortcut(cmParameterType.getName()));
			leafParameterTypes.add(cmParameterType);
			getAllLeafParameterTypes(cmParameterType, leafParameterTypes);
		}
	}

	public static CmParameterType getCmParameterTypeById(String id) {
		if (leafParameterTypes == null) {
			leafParameterTypes = getAllLeafParameterTypes();
		}
		for (CmParameterType parameterType : leafParameterTypes) {
			if(id.equals(String.valueOf(parameterType.getOid()))) {
				return parameterType;
			}
		}
		return null;
	}

	public static CmParameterType getCmParameterTypeByName(String name) {
		if (leafParameterTypes == null) {
			leafParameterTypes = getAllLeafParameterTypes();
		}
		for (CmParameterType parameterType : leafParameterTypes) {
			if(String.valueOf(parameterType.getName()).equals(name)) {
				return parameterType;
			}
		}
		return null;
	}

	/**
	 * 新建参数类型
	 *
	 * @param parameterType
	 * @return
	 */
	public static CmParameterType createParameterType(CmParameterType parameterType) {
		try {
			return ProcessParameterToWCIntf.createParameterType(parameterType);
		} catch (RemoteException e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, "新建参数类型失败，请联系管理员！");
		} catch (InvocationTargetException e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, "新建参数类型失败，请联系管理员！");
		}
		return null;
	}

	public static boolean hasChinaName(CmParameterType parameterType){
		try {
			return ProcessParameterToWCIntf.hasChinaName(parameterType);
		} catch (RemoteException e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, "该节点下已存在该类型！");
		} catch (InvocationTargetException e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, "该节点下已存在该类型！");
		}
		return false;
	}

	/**
	 * 新增参数表
	 *
	 * @param paramTableType
	 * @return
	 */
	public static CmParamTableType createParamTableType(CmParamTableType paramTableType) {
		try {
			return ProcessParameterToWCIntf.createParamTableType(paramTableType);
		} catch (RemoteException e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, "新建参数表格类型失败，请联系管理员！");
		} catch (InvocationTargetException e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, "新建参数表格类型，请联系管理员！");
		}
		return null;
	}

	/**
	 * 新建模板参数表
	 *
	 * @param cmTemplateParamTable
	 * @return
	 */
	public static CmTemplateParamTable createTemplateParamTable(CmTemplateParamTable cmTemplateParamTable) {
		try {
			return ProcessParameterToWCIntf.createTemplateParamTable(cmTemplateParamTable);
		} catch (RemoteException e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, "新建模板参数表格失败，请联系管理员！");
		} catch (InvocationTargetException e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, "新建参数表格失败，请联系管理员！");
		}
		return null;
	}

	/**
	 * 保存修改后的参数表
	 *
	 * @param paramTableType
	 * @return
	 */
	public static CmParamTableType saveParamTableType(CmParamTableType paramTableType) {
		try {
			return ProcessParameterToWCIntf.saveParamTableType(paramTableType);
		} catch (RemoteException e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, "保存检验记录表失败！错误信息如下：\r\n"+e.getLocalizedMessage());
		} catch (InvocationTargetException e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, "保存检验记录表失败！错误信息如下：\r\n"+e.getLocalizedMessage());
		}
		return null;
	}

	public static void refreshTreeNode(CmParameterType parameterType) {
		JTree tree = MPMParameterMainFrame.getLeftPanel().getQualityTree();
		XWTreeNode node = getParamManagerNode();
		loopTreeNode(node, parameterType);
		tree.updateUI();
	}

	@SuppressWarnings("unchecked")
	private static void loopTreeNode(XWTreeNode parentNode, CmParameterType parameterType) {
		Enumeration<XWTreeNode> childs = parentNode.children();
		XWTreeNode childNode = null;;
		while (childs.hasMoreElements()) {
			childNode = childs.nextElement();
			CmParameterType childParameterType = (CmParameterType) childNode.getTreeObject().getTreeNode();
			if (childParameterType.getEnName().equals(parameterType.getEnName())) {
				childNode.getTreeObject().setTreeNode(parameterType);
			} else {
				loopTreeNode(childNode, parameterType);
			}
		}
	}

	public static void addParameterTypeNode(JTree tree, XWTreeNode selTreeNode, CmParameterType parameterType) {
		XWParameterTypeTreeObject newParameterTypeTreeObject = new XWParameterTypeTreeObject(parameterType);
		XWTreeNode newTreeNode = new XWTreeNode(newParameterTypeTreeObject);
		selTreeNode.add(newTreeNode);

		expandAllNode(selTreeNode, tree);

		TreePath p = new TreePath(newTreeNode.getPath());
		tree.setSelectionPath(p);
		tree.updateUI();
	}

	public static List<CmTechnicsType> queryTechnicsTypes() {
		List<CmTechnicsType> list = null;
		try {
			list = ProcessParameterToWCIntf.queryTechnicsTypes();
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return list;
	}

	public static List<CmParamTableType> queryParamTableTypes(Map<String, String> map){
		List<CmParamTableType> list = null;
		try {
			list = ProcessParameterToWCIntf.queryParamTableTypes(map);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return list;
	}

	public static CmParamTableType queryCmParameterTableType(String gwkey) {
		try {
			return ProcessParameterToWCIntf.queryCmParameterTableType(gwkey);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return null;
	}

	public static List<CmTemplateParamTable> queryTemplateParamTable(String chinaName) {
		List<CmTemplateParamTable> list = null;
		try {
			list = ProcessParameterToWCIntf.queryTemplateParamTable(chinaName);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return list;
	}

	public static List<GWParamTableTypeMaster> queryAllParamTableTypeMasters() {
		List<GWParamTableTypeMaster> list = null;
		try {
			list = ProcessParameterToWCIntf.queryAllParamTableTypeMasters();
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return list;
	}

	public static Vector<Vector<String>> queryParamsByTableId(String tableName, String tableId, String paramTableTypeIid) {
		Vector<Vector<String>> parameters = null;;
		try {
			parameters = ProcessParameterToWCIntf.queryParamsByTableId(tableName, tableId, paramTableTypeIid);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return parameters;
	}

	public static Vector<Vector<String>> queryTemplateTableParamsByTableId(String tableName, String tableId, String paramTableTypeIid) {
		Vector<Vector<String>> parameters = null;;
		try {
			parameters = ProcessParameterToWCIntf.queryTemplateTableParamsByTableId(tableName, tableId, paramTableTypeIid);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return parameters;
	}

	/**
	 * 根据模板参数表的名称查询模板参数表对象
	 *
	 * @param chinaName
	 * @return
	 */
	public static CmTemplateParamTable queryCmTemplateParamTableByChinaName(String chinaName) {
		CmTemplateParamTable templateParamTable = null;
		try {
			templateParamTable = ProcessParameterToWCIntf.queryCmTemplateParamTableByChinaName(chinaName);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return templateParamTable;
	}

	public static void resetTemplateTableParamsTable(CmParameterTablePackage tablePackage) {
//		MPMParameterMainFrame.getTemplateTableManagerPanel().getParamsDefinitionTablePanel().resetTemplateTableParamsTable(tablePackage);
	}

	/**
	 * 将当前模板参数表中用到的特殊符号图片打成压缩包,并返回该压缩包的字节数组。
	 *
	 * @return
	 */
	public static byte[] getCurrentImageBytes() {
		byte[] bytes = null;

		String imageDir = getParamWorkSpace() + "/" + "content";
		String zipFilePath = getParamWorkSpace() + "/" + System.currentTimeMillis() + ".zip";
		boolean flag = ZipUtil2.compress(imageDir, zipFilePath);
		if (flag) {
			bytes = FileUtil.readFilePathToByte(zipFilePath);
		}

		return bytes;
	}

	/**
	 * 根据参数表类型转化为参数表数据包，该数据包中包含了生成JTable所需要的信息。
	 *
	 * @param paramTableType
	 * @return
	 */
	public static CmParameterTablePackage getParameterTablePackage(CmParamTableType paramTableType, String jianyanyuan, String caozuoyuan) {
		CmParameterTablePackage tablePackage = new CmParameterTablePackage();
		tablePackage.setParamTableType(paramTableType);

		List<CmParameterTableColumn> columns = paramTableType.getTableColumns();

		/** 表各列的对象类型 */
		Class<?>[] tableColumnClass = new Class<?>[columns.size()];
		/** 表各列的数据库数据类型 */
		String[] tableColumnDataType = new String[columns.size()];
		/** 表头 */
		String[] tableColumnName = new String[columns.size()];
		/** mes数据搜索表头 */
		String[] mesDataSearchTableColumnName = new String[columns.size()];
		/**不显示的检验列*/
		List<Integer> notShowColumns = new ArrayList<Integer>();
		/**不显示的数据搜索检验列*/
		List<Integer> notShowDataSearchColumns = new ArrayList<Integer>();
		/**可编辑的检验列*/
		List<Integer> editableColumnList = new ArrayList<Integer>();
		/**不显示的MES列*/
		List<Integer> notShowMesColumns = new ArrayList<Integer>();
		/**可编辑的MES列*/
		List<Integer> editableMesColumnList = new ArrayList<Integer>();
		/**双击操作的Boolean检验列*/
		List<Integer> doubleCilckBooleanColumns = new ArrayList<Integer>();
		/**双击操作的MESBoolean检验列*/
		List<Integer> doubleCilckMesBooleanColumns = new ArrayList<Integer>();
		/**双击操作的图片检验列*/
		List<Integer> doubleCilckPictureColumns = new ArrayList<Integer>();
		/**双击操作的MES图片检验列*/
		List<Integer> doubleCilckMesPictureColumns = new ArrayList<Integer>();
		/**记录列*/
		List<Integer> recordColumnsList = new ArrayList<Integer>();
		CmParameterTableColumn tableColumn = null;
		for (int i=0;i<columns.size();i++) {
			tableColumn = columns.get(i);
			tableColumnName[i] = tableColumn.getName();
			if("OBJNUMBER".equals(tableColumn.getName())){
				mesDataSearchTableColumnName[i] = "工序号";
			}else{
				mesDataSearchTableColumnName[i] = tableColumn.getName();
			}
			tableColumnDataType[i] = tableColumn.getDatatype();
			if(tableColumnDataType[i].equals("布尔型")){
				tableColumnClass[i] = Boolean.class;
			}else{
				tableColumnClass[i] = String.class;
			}
			if (!tableColumn.isShow()
					|| "冻结".equals(tableColumn.getStatus())
					|| "true".equals(tableColumn.getVisiless())) {
				notShowColumns.add(i);
			}
			if(!tableColumn.isShow()
					|| "冻结".equals(tableColumn.getStatus())
					|| "true".equals(tableColumn.getVisilessInMes())
					|| !isColumnHasValue(paramTableType, i)){
				notShowMesColumns.add(i);
			}
			if(!tableColumn.isShow()
					|| "冻结".equals(tableColumn.getStatus())
					|| "true".equals(tableColumn.getVisilessInMes())
					|| !isColumnHasValue(paramTableType, i)){
				if(!tableColumn.getName().equals("OBJNUMBER")){
					notShowDataSearchColumns.add(i);
				}
			}
			if (tableColumn.isEditable()
					&& !"图片".equals(tableColumn.getDatatype())
					&& "false".equals(tableColumn.getIsrecord())) {
				editableColumnList.add(i);
			}
			if("true".equals(tableColumn.getIsrecord())){
				recordColumnsList.add(i);
			}
			if("true".equals(tableColumn.getIsrecord())
					&& !"图片".equals(tableColumn.getDatatype())
					&& !tableColumn.getName().contains("检验人员")
					&& !tableColumn.getName().contains("操作人员")){
//				if(tableColumn.getName().contains("检验人员")){
//					if(jianyanyuan != null && jianyanyuan.length() > 0){
						editableMesColumnList.add(i);
//					}
//				}else if(tableColumn.getName().contains("操作人员")){
//					if(caozuoyuan != null && caozuoyuan.length() > 0){
//						editableMesColumnList.add(i);
//					}
//				}else{
//					editableMesColumnList.add(i);
//				}
			}
			if(tableColumn.isEditable()
				&& ("布尔型".equals(tableColumn.getDatatype()))
				&& "false".equals(tableColumn.getIsrecord())) {
					doubleCilckBooleanColumns.add(i);
				}
			if(tableColumn.isEditable()
				&& "布尔型".equals(tableColumn.getDatatype())
				&& "true".equals(tableColumn.getIsrecord())) {
					doubleCilckMesBooleanColumns.add(i);
				}
			if(tableColumn.isEditable()
					&& ("图片".equals(tableColumn.getDatatype()))
					&& "false".equals(tableColumn.getIsrecord())) {
						doubleCilckPictureColumns.add(i);
					}
				if(tableColumn.isEditable()
					&& "图片".equals(tableColumn.getDatatype())
					&& "true".equals(tableColumn.getIsrecord())) {
						doubleCilckMesPictureColumns.add(i);
					}

		}

		/** 可编辑的列 */
		int[] editableColumns = new int[editableColumnList.size()];
		for (int i=0;i<editableColumnList.size();i++) {
			editableColumns[i] = editableColumnList.get(i);
		}
		tablePackage.setEditableColumns(editableColumns);
		/** 可编辑的MES列 */
		int[] editableMesColumns = new int[editableMesColumnList.size()];
		for(int i = 0; i < editableMesColumnList.size(); i++){
			editableMesColumns[i] = editableMesColumnList.get(i);
		}
		tablePackage.setEditableMesColumns(editableMesColumns);

		/** 不需要显示的列 */
		int[] notShows = new int[notShowColumns.size()];
		for (int i=0;i<notShowColumns.size();i++) {
			notShows[i] = notShowColumns.get(i);
		}
		tablePackage.setNotShowColumns(notShows);
		/** 不需要显示的MES列 */
		int[] notShowMes = new int[notShowMesColumns.size()];
		for(int i = 0; i < notShowMesColumns.size(); i++){
			notShowMes[i] = notShowMesColumns.get(i);
		}
		tablePackage.setNotShowMesColumns(notShowMes);
		/** 不需要显示的MES数据搜索列 */
		int[] notShowMesDataSearch = new int[notShowDataSearchColumns.size()];
		for(int i = 0; i < notShowDataSearchColumns.size(); i++){
			notShowMesDataSearch[i] = notShowDataSearchColumns.get(i);
		}
		tablePackage.setNotShowDataSearchColumns(notShowMesDataSearch);

		tablePackage.setDoubleCilckBooleanColumns(doubleCilckBooleanColumns);
		tablePackage.setDoubleCilckMesBooleanColumns(doubleCilckMesBooleanColumns);
		tablePackage.setDoubleCilckPictureColumns(doubleCilckPictureColumns);
		tablePackage.setDoubleCilckMesPictureColumns(doubleCilckMesPictureColumns);
		tablePackage.setRecordColumns(recordColumnsList);

		tablePackage.setTableColumnClass(tableColumnClass);
		tablePackage.setTableColumnDataType(tableColumnDataType);
		tablePackage.setTableColumnName(tableColumnName);
		tablePackage.setMesDataSearchTableColumnName(mesDataSearchTableColumnName);

		return tablePackage;
	}

	public static boolean isColumnHasValue(CmParamTableType paramTableType, int column) {
		boolean flag = true;
		List<CmParameterTableColumn> columns = paramTableType.getTableColumns();
		Vector<Vector<Object>> values = paramTableType.getParameters();
		CmParameterTableColumn tableColumn = columns.get(column);
		String tableColumnDataType = tableColumn.getDatatype();
		if (!"true".equals(tableColumn.getIsrecord()) && values != null) {
			for (int i = 0; i < values.size(); i++) {
				String cellValue = String.valueOf(values.get(i).get(column)).trim();
				if ("布尔型".equals(tableColumnDataType)) {
					if ("false".equals(cellValue)) {
						flag = false;
					} else {
						return true;
					}
				} else if ("BLOB".equals(tableColumnDataType)) {
					cellValue = getBlobStr(cellValue);
					if ("".equals(cellValue)) {
						flag = false;
					} else {
						return true;
					}
				} else {
					if ("".equals(cellValue)) {
						flag = false;
					} else {
						return true;
					}
				}
			}
		}
		return flag;
	}

	/**
	 * 获取标准值列的列索引
	 *
	 * @param paramTableType
	 * @return
	 */
	public static int getStandradValueColumnIndex(CmParamTableType paramTableType) {
		if (paramTableType != null) {
			List<CmParameterTableColumn> columns = paramTableType.getTableColumns();
			if (columns != null && !columns.isEmpty()) {
//				for (CmParameterTableColumn cmParameterTableColumn : columns) {
//					if ("true".equals(cmParameterTableColumn.getIsstandardvalue())) {
//						return Integer.valueOf(cmParameterTableColumn.getOrderno())-1;
//					}
//				}
			}
		}
		return -1;
	}

	public static String deleteTemplateTableParamsByTableId(String tableName, String tableId, List<String> paramTypeIdList) throws Exception {
		return ProcessParameterToWCIntf.deleteTemplateTableParamsByTableId(tableName, tableId, paramTypeIdList);
	}

	public static void downloadImages(CmTemplateParamTable templateParamTable) {
		try {
			byte[] bytes = templateParamTable.getImagesByte();
			if (bytes != null && bytes.length > 0) {
				String zipFilePath = getParamWorkSpace() + File.separator + System.currentTimeMillis() + ".zip";
				File file = new File(zipFilePath);
				if (!file.exists()) {
					file.createNewFile();
				}
				FileUtil.writeBytes(zipFilePath, bytes);

				String imageDir = getParamWorkSpace() + File.separator + "content";

				//首先清空该路径下其他特殊符号图片
				FileUtil.delAllFile(imageDir);

				boolean flag = ZipUtil2.decompress(zipFilePath, imageDir);
				if (!flag) {
					JOptionPane.showMessageDialog(null, "下载特殊符号图片失败!");
				}
				FileUtil.deleteFile(file);
			}
		} catch (HeadlessException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public static void downloadImages(CmTemplateParamTable templateParamTable, String technicsPath) {
		try {
			byte[] bytes = templateParamTable.getImagesByte();
			if (bytes != null) {
				String zipFilePath = WorkSpaceUtil.getWorkSpace() + File.separator + System.currentTimeMillis() + ".zip";
				File file = new File(zipFilePath);
				if (!file.exists()) {
					file.createNewFile();
				}
				FileUtil.writeBytes(zipFilePath, bytes);

				String imageDir = technicsPath + File.separator + "content";

				boolean flag = ZipUtil2.decompress(zipFilePath, imageDir);
				if (!flag) {
					JOptionPane.showMessageDialog(null, "下载特殊符号图片失败!");
				}
				FileUtil.deleteFile(file);
			}
		} catch (HeadlessException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public static XWTreeNode getParamManagerNode() {
		JTree tree = MPMParameterMainFrame.getLeftPanel().getQualityTree();
		XWTreeNode root = (XWTreeNode) tree.getModel().getRoot();
		Enumeration<?> enums = root.children();
		while (enums.hasMoreElements()) {
			XWTreeNode node = (XWTreeNode) enums.nextElement();
			if ("检验特性管理".equals(node.toString())) {
				return node;
			}
		}
		return null;
	}

	public static void showInfomation(XWTreeNode treeNode) {
		if (treeNode == null)
			return ;

		MPMParameterMainFrame.getRightPanel().removeAll();
		XWTreeObject treeObject = treeNode.getTreeObject();
		if (treeObject instanceof XWParameterTypeTreeObject) {
			MPMParameterMainFrame.getRightPanel().add(MPMParameterMainFrame.getParameterTypeInfoPanel());
			MPMParameterMainFrame.getParameterTypeInfoPanel().setUIValues(treeNode);
		} else if (treeObject instanceof XWParamTableTypeTreeObject) {
			MPMParameterMainFrame.getRightPanel().add(MPMParameterMainFrame.getParamTableTypePanel());
			MPMParameterMainFrame.getParamTableTypePanel().setUIValues(treeNode);
		} else if (treeObject instanceof XWBaiyuParamTableTreeObject) {
			MPMParameterMainFrame.getRightPanel().add(MPMParameterMainFrame.getBaiyuParamTablePanel());
			MPMParameterMainFrame.getBaiyuParamTablePanel().setUIValues(treeNode);
		}
		MPMParameterMainFrame.getRightPanel().updateUI();
	}

	/**
	 * 保存修改后的特性值
	 *
	 * @param paramTableType
	 * @return
	 */
	public static CmParameterType saveParameterType(CmParameterType parameterType) {
		try {
			return ProcessParameterToWCIntf.saveParameterType(parameterType);
		} catch (RemoteException e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, "保存检验特性失败！错误信息如下：\r\n"+e.getLocalizedMessage());
		} catch (InvocationTargetException e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, "保存检验特性s失败！错误信息如下：\r\n"+e.getLocalizedMessage());
		}
		return null;
	}

	public static void addParamTableTypeNode(XWTreeNode selTreeNode, CmParamTableType paramTableType) {
		XWParamTableTypeTreeObject newParamTableTypeTreeObject = new XWParamTableTypeTreeObject(paramTableType);
		XWTreeNode newTreeNode = new XWTreeNode(newParamTableTypeTreeObject);
		selTreeNode.add(newTreeNode);

		JTree tree = MPMParameterMainFrame.getLeftPanel().getQualityTree();
		expandAllNode(selTreeNode, tree);

		TreePath p = new TreePath(newTreeNode.getPath());
		tree.setSelectionPath(p);
		tree.updateUI();
	}

	public static void refreshTreeNode(CmParamTableType paramTableType) {
		JTree tree = MPMParameterMainFrame.getLeftPanel().getQualityTree();
		XWTreeNode node = getParamTableManagerNode();
		loopTreeNode(node, paramTableType);
		tree.updateUI();
	}

	public static XWTreeNode getParamTableManagerNode() {
		JTree tree = MPMParameterMainFrame.getLeftPanel().getQualityTree();
		XWTreeNode root = (XWTreeNode) tree.getModel().getRoot();
		Enumeration<?> enums = root.children();
		while (enums.hasMoreElements()) {
			XWTreeNode node = (XWTreeNode) enums.nextElement();
			if ("检验记录表管理".equals(node.toString())) {
				return node;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static void loopTreeNode(XWTreeNode parentNode, CmParamTableType paramTableType) {
		Enumeration<XWTreeNode> childs = parentNode.children();
		XWTreeNode childNode = null;;
		while (childs.hasMoreElements()) {
			childNode = childs.nextElement();
			CmTreeNode treeNode = childNode.getTreeObject().getTreeNode();
			if (treeNode != null) {
				if (treeNode instanceof CmParamTableType) {
					CmParamTableType childParamTableType = (CmParamTableType) treeNode;
					if (childParamTableType.getEnName().equals(paramTableType.getEnName())) {
						childNode.getTreeObject().setTreeNode(paramTableType);
					} else {
						loopTreeNode(childNode, paramTableType);
					}
				} else {
					loopTreeNode(childNode, paramTableType);
				}
			}
		}
	}

	private static CmParamTableType getCommonParamTableType(CmParamTableType paramTableType) {
		try {
			return ProcessParameterToWCIntf.getCommonParamTableType(paramTableType);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static CmParamTableType getCommonParamTableType(String technicsNumber, String objType, String objNumber, boolean isApproved, String bsoID, String version) {
		try {
			return ProcessParameterToWCIntf.getCommonParamTableType(technicsNumber, objType, objNumber, isApproved, bsoID, version);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static CmParamTableType getCommonParamTableTypeForMes(String technicsNumber, String objType, String objNumber, boolean isApproved, String bsoID, String version) {
		try {
			return ProcessParameterToWCIntf.getCommonParamTableTypeForMes(technicsNumber, objType, objNumber, isApproved, bsoID, version);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void saveParameters(CmParamTableType paramTableType, String technicsNumber, String objType, String objNumber, String bsoID, String version) {
		try {
			ProcessParameterToWCIntf.saveParameters(paramTableType, technicsNumber, objType, objNumber, bsoID, version);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static List<CmParamTableType> getParamTableTypes(String technicsNumber, String objType, String objNumber, boolean isApproved, String bsoID, String version) {
		try {
			return ProcessParameterToWCIntf.getParamTableTypes(technicsNumber, objType, objNumber, isApproved, bsoID, version);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void setSpecialParamTableIndex(String tableOid, String technicsNumber, String objType, String objNumber, String index){
		try {
			ProcessParameterToWCIntf.setSpecialParamTableIndex(tableOid, technicsNumber, objType, objNumber, index);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<CmParamTableType> getParamTableTypesForMes(String technicsNumber, String objType, String objNumber, boolean isApproved, String bsoID, String version) {
		try {
			return ProcessParameterToWCIntf.getParamTableTypesForMes(technicsNumber, objType, objNumber, isApproved, bsoID, version);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void createObjToParamTableLink(String technicsNumber, String objType, String objNumber, String tableId, String tableIndex, String bsoID, String version) {
		try {
			ProcessParameterToWCIntf.createObjToParamTableLink(technicsNumber, objType, objNumber, tableId, tableIndex, bsoID, version);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void deleteObjToParamTableLink(String technicsNumber, String objType, String objNumber, String tableId, String bsoID, String version) {
		try {
			ProcessParameterToWCIntf.deleteObjToParamTableLink(technicsNumber, objType, objNumber, tableId, bsoID, version);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void deleteParameterType(CmParameterType parameterType) {
		try {
			ProcessParameterToWCIntf.deleteParameterType(parameterType);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void deleteTableParams(String tableName, String technicsNumber, String objType, String objNumber, String bsoID, String version) {
		try {
			ProcessParameterToWCIntf.deleteTableParams(tableName, technicsNumber, objType, objNumber, bsoID, version);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static String queryMaxEnname(){
		String maxEnname = "";
		try {
			maxEnname = ProcessParameterToWCIntf.queryMaxEnname();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return maxEnname;
	}
	public static String queryMaxNameFromTypeMaster(){
		String maxName = "";
		try {
			maxName = ProcessParameterToWCIntf.queryMaxNameFromTypeMaster();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return maxName;
	}
	public static Map<String, Map<String, byte[]>> getAllImages(){
		Map<String, Map<String, byte[]>> imageBytesList = null;
		try {
			imageBytesList = ProcessParameterToWCIntf.getAllImages();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return imageBytesList;
	}
	public static void downloadAllImages(){
		Map<String, Map<String, byte[]>> imageBytesMap = getAllImages();
		if(imageBytesMap != null && imageBytesMap.size() > 0){
			for (Map.Entry<String, Map<String, byte[]>> entry : imageBytesMap.entrySet()) {
				String date = entry.getKey();
				String imagePath = WorkSpaceUtil.getMesTempletRootPath() + File.separator + date;
				Map<String, byte[]> imageBytes = entry.getValue();
				for (Map.Entry<String, byte[]> imageByte : imageBytes.entrySet()) {
					String uuid = imageByte.getKey();
					byte[] bytes = imageByte.getValue();
					FilesUtil.getFile(bytes, imagePath, uuid);
				}
			}
		}
	}
	public static void deleteOldUUIDFiles(List<String> oldUUIDFileNameList){
		try {
			ProcessParameterToWCIntf.deleteOldUUIDFiles(oldUUIDFileNameList);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void exportQualityForm(Container component, String path){
		CommonTableModel tableModel = null;
		List<String> showColumnNames = null;
		List<String> notshowColumnNames = null;
		if(component instanceof NewCommonParamTablePanel){
			NewCommonParamTablePanel commonParamTable = (NewCommonParamTablePanel) component;
			tableModel = commonParamTable.getTableModel();
			String[] columnName = commonParamTable.getTableColumnName();
			int[] notShowColumns = commonParamTable.getNotShowColumns();
			showColumnNames = new ArrayList<String>();
			notshowColumnNames = new ArrayList<String>();
			for(int n = 0; n < notShowColumns.length; n++){
				notshowColumnNames.add(String.valueOf(notShowColumns[n]));
			}
			for(int i = 0; i < columnName.length; i++){
				if(!notshowColumnNames.contains(String.valueOf(i))){
					showColumnNames.add(columnName[i]);
				}
			}
		}
		if(component instanceof NewSpecialParamTablePanel){
			NewSpecialParamTablePanel specialParamTable = (NewSpecialParamTablePanel) component;
			tableModel = specialParamTable.getTableModel();
			String[] columnName = specialParamTable.getTableColumnName();
			int[] notShowColumns = specialParamTable.getNotShowColumns();
			showColumnNames = new ArrayList<String>();
			notshowColumnNames = new ArrayList<String>();
			for(int n = 0; n < notShowColumns.length; n++){
				notshowColumnNames.add(String.valueOf(notShowColumns[n]));
			}
			for(int i = 0; i < columnName.length; i++){
				if(!notshowColumnNames.contains(String.valueOf(i))){
					showColumnNames.add(columnName[i]);
				}
			}
		}

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFCellStyle style = workbook.createCellStyle(); // 样式对象
		style.setVerticalAlignment(VerticalAlignment.CENTER); // 使用枚举值
		style.setAlignment(HorizontalAlignment.CENTER);
		HSSFSheet sheet = workbook.createSheet("sheet1");
		Vector<Vector<Object>> dataVector = tableModel.getDataVector();
		HSSFRow row = sheet.createRow(0);
		for(int n = 0; n < showColumnNames.size(); n++){
			HSSFCell cell = row.createCell(n);
			cell.setCellValue(showColumnNames.get(n));
		}
		for(int i = 0; i < dataVector.size(); i++){
			row = sheet.createRow(i + 1);
			int count = 0;
			for(int j = 0; j < dataVector.get(i).size(); j++){
				if(!notshowColumnNames.contains(String.valueOf(j))){
					HSSFCell cell = row.createCell(count);
					String cellValue = String.valueOf(dataVector.get(i).get(j));
					if(cellValue.contains("已选图片数量") || cellValue.contains("img")){
						cellValue = "";
					}
					if(cellValue.contains("<html>") && cellValue.contains("<head>") && cellValue.contains("<body>")){
						cellValue = getBlobStr(cellValue);
					}
					if(cellValue.equals("true")){
						cellValue = "是";
					}
					if(cellValue.equals("false")){
						cellValue = "否";
					}
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(cellValue);
					cell.setCellStyle(style);
					count++;
				}
			}
		}
		FileOutputStream writeFile = null;
		try {
			writeFile = new FileOutputStream(path);
			workbook.write(writeFile);
			writeFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public static String getBlobStr(String str){
		str = str.replace("\r\n", "");
		str = str.replace("<html>", "");
		str = str.replace("<head>", "");
		str = str.replace("</head>", "");
		str = str.replace("<body>", "");
		str = str.replace("<p style='margin-top:5'>", "");
		str = str.replace("</p>", "");
		str = str.replace("</body>", "");
		str = str.replace("</html>", "");
		return str.trim();
	}
	public static void setTableHeaderColorAndAutoNextLine(JTable table, final int columnIndex, final Color c,final List<Integer> recordList){
		TableColumn column = table.getTableHeader().getColumnModel().getColumn(columnIndex);
		DefaultTableCellRenderer cellRender = new DefaultTableCellRenderer(){
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//				JComponent comp = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				JTableHeader header = table.getTableHeader();
		        setForeground(header.getForeground());
//		        setBackground(header.getBackground());
		        if(recordList != null && !recordList.contains(columnIndex)){
		        	setBackground(c);
		        }else{
		        	setBackground(header.getBackground());
		        }
		        setFont(header.getFont());
		        setOpaque(true);
//		        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		        setBorder(BorderFactory.createEtchedBorder());

		        // 得到列的宽度
		        TableColumnModel columnModel = table.getColumnModel();
		        int width = columnModel.getColumn(column).getWidth();
		        if(width != 0){
		        	value = getShowValue(value.toString(), width);
		        	setText(value.toString());
		        	setSize(new Dimension(width, this.getHeight()));

		        	setHorizontalAlignment(JLabel.CENTER);
		        }

		        return this;
			}
			 private Object getShowValue(String value, int colWidth) {
			        // 根据当前的字体和显示值得到需要显示的宽度
			        FontMetrics fm = this.getFontMetrics(this.getFont());
			        int width = fm.stringWidth(value.toString());
			        if (width < colWidth) {
			            return value;
			        }
			        StringBuffer sb = new StringBuffer("<html>");
			        char str;
			        int tempW = 0;
			        for (int i = 0; i < value.length(); i++) {
			            str = value.charAt(i);
			            tempW += fm.charWidth(str);
			            if (tempW > colWidth) {
			                sb.append("<br>");
			                tempW = 0;
			            }
			            sb.append(str);
			        }
			        sb.append("</html>");
			        return sb.toString();
			    }
		};
		column.setHeaderRenderer(cellRender);
	}
	public static void setTableHeaderColor(JTable table, final int columnIndex, final Color c,final List<Integer> recordList){
		TableColumn column = table.getTableHeader().getColumnModel().getColumn(columnIndex);
		DefaultTableCellRenderer cellRender = new DefaultTableCellRenderer(){
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				JComponent comp = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				JTableHeader header = table.getTableHeader();
				if(recordList != null && !recordList.contains(columnIndex)){
					comp.setBackground(c);
				}else{
					comp.setBackground(header.getBackground());
				}
				comp.setBorder(BorderFactory.createEtchedBorder());
				return comp;
			}
		};
		column.setHeaderRenderer(cellRender);
	}
	/**
	 * 移除工艺节点下的通用、特殊质量记录表节点 add by liangbo 20180403
	 * @param stepElement
	 */
	public static void removeComSpeElements(Element stepElement){
		Element stepComElement = stepElement.element("commonParamTables");
		if(stepComElement != null){
			stepElement.remove(stepComElement);
		}
		Element stepSpeElement = stepElement.element("specialParamTables");
		if(stepSpeElement != null){
			stepElement.remove(stepSpeElement);
		}
		Element pacesElements = stepElement.element("paces");
		List<Element> paceElements = pacesElements.elements("pace");
		if(paceElements != null && paceElements.size() > 0){
			for(Element paceElement : paceElements){
				Element paceComElement = paceElement.element("commonParamTables");
				if(paceComElement != null){
					paceElement.remove(paceComElement);
				}
				Element paceSpeElement = paceElement.element("specialParamTables");
				if(paceSpeElement != null){
					paceElement.remove(paceSpeElement);
				}

				Element schemaData = paceElement.element("schemaData");
				if(schemaData != null){
					paceElement.remove(schemaData);
				}
			}
		}
	}
}
