package com.glaway.mpm.dataPackage.helper;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.glaway.mpm.dataPackage.service.ProcessMesParameterToWCIntf;
import com.glaway.mpm.dataPackage.ui.DPMesParameterMainFrame;
import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.view.XWTreeNode;

public class DPMesParameterProcessor {

	private static VaLogger logger = VaLogger.getLogger(DPMesParameterProcessor.class.getName());


	/**
	 * 加载参数类型树结构
	 */
	public static void loadMesTree() {
		JTree mesTree = DPMesParameterMainFrame.getMesParameterMainPanel().getLeftTechnicTreePanel().getMesTree();
		XWTreeNode root = (XWTreeNode) mesTree.getModel().getRoot();
		DefaultTreeModel model = (DefaultTreeModel) mesTree.getModel();
		Enumeration<?> enums = root.children();
		while (enums.hasMoreElements()) {
			XWTreeNode node = (XWTreeNode) enums.nextElement();
				node.removeAllChildren();
				model.reload(node);
			}

		TreePath p = new TreePath(root.getPath());
		mesTree.expandPath(p);
		mesTree.scrollPathToVisible(p);
		mesTree.setSelectionPath(p);
		mesTree.repaint();
	}

	public static Vector<Object> getTechnicsByTechnicNumber(String technicNumber){
		Vector<Object> vector = null;
		try {
			vector =  ProcessMesParameterToWCIntf.getTechnicsByTechnicNumber(technicNumber);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return vector;
	}
	public static String getTechnicsNumberByProductNumber(String productNumber){
		String technicsNumber = "";
		try {
			technicsNumber =  ProcessMesParameterToWCIntf.getTechnicsNumberByProductNumber(productNumber);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return technicsNumber;
	}
	public static void saveMesParameters(CmParamTableType paramTableType, String productNumber, String technicsNumber, String objType, String objNumber, String lukahao, String gxPK) {
		try {
			ProcessMesParameterToWCIntf.saveMesParameters(paramTableType, productNumber, technicsNumber, objType, objNumber, lukahao, gxPK);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	public static boolean isMesDataExist(CmParamTableType paramTableType, String productNumber, String technicsNumber, String objType, String objNumber, String lukahao, String gxPK){
		try {
			return ProcessMesParameterToWCIntf.isMesDataExist(paramTableType, productNumber, technicsNumber, objType, objNumber, lukahao, gxPK);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}
	public static CmParamTableType getMESCommonParamTableType(String productNumber, String technicsNumber, String objType, String objNumber, boolean isApproved, String lukahao, String gxPK) {
		try {
			return ProcessMesParameterToWCIntf.getMESCommonParamTableType(productNumber, technicsNumber, objType, objNumber, isApproved, lukahao, gxPK);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static List<CmParamTableType> getMesParamTableTypes(String productNumber, String technicsNumber, String objType, String objNumber, boolean isApproved, String lukahao, String gxPK) {
		try {
			return ProcessMesParameterToWCIntf.getMesParamTableTypes(productNumber, technicsNumber, objType, objNumber, isApproved, lukahao, gxPK);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void updataTechnicsPrimary(String technicNumber, byte[] bytes){
		try {
			ProcessMesParameterToWCIntf.updataTechnicsPrimary(technicNumber, bytes);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String getZzTechnicsNumber(String technicsNumber){
		try {
			return ProcessMesParameterToWCIntf.getZzTechnicsNumber(technicsNumber);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
