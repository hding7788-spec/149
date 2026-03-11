package com.glaway.mpm.mesParameter.helper;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.dom4j.Document;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.mesParameter.model.Bdpsndoc;
import com.glaway.mpm.mesParameter.model.MesBfcccpbh;
import com.glaway.mpm.mesParameter.service.ProcessMesParameterToWCIntf;
import com.glaway.mpm.mesParameter.ui.MesParameterMainFrame;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.util.TechnicsReleaseUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.view.XWTreeNode;

public class MesParameterProcessor {

	private static VaLogger logger = VaLogger.getLogger(MesParameterProcessor.class.getName());


	/**
	 * 加载参数类型树结构
	 */
	public static void loadMesTree() {
		JTree mesTree = MesParameterMainFrame.getMesParameterMainPanel().getLeftTechnicTreePanel().getMesTree();
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
	public static void saveMesParameters(CmParamTableType paramTableType, Map<String,String> paramsMap) {
		try {
			ProcessMesParameterToWCIntf.saveMesParameters(paramTableType, paramsMap);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static boolean isMesDataExist(CmParamTableType paramTableType, Map<String, String> paramsMap){
		try {
			return ProcessMesParameterToWCIntf.isMesDataExist(paramTableType, paramsMap);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}
	public static boolean isDataPackageMesDataExist(CmParamTableType paramTableType, String productNumber, String technicsNumber, String objType, String objNumber, String lukahao, String gxPK){
		try {
			return ProcessMesParameterToWCIntf.isDataPackageMesDataExist(paramTableType, productNumber, technicsNumber, objType, objNumber, lukahao, gxPK);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}
	public static CmParamTableType getMESCommonParamTableType(boolean isApproved, Map<String, String> paramsMap) {
		try {
			return ProcessMesParameterToWCIntf.getMESCommonParamTableType(isApproved, paramsMap);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static CmParamTableType getDataPackageCommonParamTableType(String productNumber, String technicsNumber, String objType, String objNumber, boolean isApproved, String lukahao, String gxPK) {
		try {
			return ProcessMesParameterToWCIntf.getDataPackageCommonParamTableType(productNumber, technicsNumber, objType, objNumber, isApproved, lukahao, gxPK);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static List<CmParamTableType> getMesParamTableTypes(boolean isApproved, Map<String, String> paramsMap) {
		try {
			return ProcessMesParameterToWCIntf.getMesParamTableTypes(isApproved, paramsMap);
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
	public static List<Bdpsndoc> getCampPersonInfo(String name, String number){
		try {
			return ProcessMesParameterToWCIntf.getCampPersonInfo(name, number);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static Object[] getZFTechnics(String technicNumber){
		try {
			return ProcessMesParameterToWCIntf.getZFTechnics(technicNumber);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static List<String[]> getFzTechnicsNumberList(String zzTechnicsNumber){

		try {
			return ProcessMesParameterToWCIntf.getFzTechnicsNumberList(zzTechnicsNumber);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static Vector<Object> getTechnicDocumentByNumberAndVersion(String technicNumber, String version){
		try {
			return ProcessMesParameterToWCIntf.getTechnicDocumentByNumberAndVersion(technicNumber, version);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static Document downloadData(Vector<Object> result){
		String fileName = (String) result.get(0);
		String filepath = "";
		Document doc = null;
		try {
			byte[] data = (byte[]) result.get(1);
			if ((data == null) || (data.length <= 0)) {

			}
			if (fileName.toLowerCase().endsWith(".zip")) {
				fileName = fileName.substring(0, fileName.length() - 4);
			}
			File f = new File(WorkSpaceUtil.getMesTempletRootPath() + "\\" + fileName);
			if (f.exists()) {
				WorkSpaceUtil.delete(f);
			}
				filepath = WorkSpaceUtil.createMesTechnicsDirectory(fileName);
				logger.debug("filepath=======" + filepath);
				TechnicsReleaseUtil.unZip(data, filepath);
				String technicsNumber = WorkSpaceUtil.getTechnicsNumber(fileName);
				doc = WorkSpaceUtil.getMesTechnicsDocumentByTechnicsNumber(technicsNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return doc;
	}
	public static List<MesBfcccpbh> getProcessNumberValues(String lukahao){
		try {
			return ProcessMesParameterToWCIntf.getProcessNumberValues(lukahao);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static String getDefaultValues(Map<String, String> paramsMap){
		try {
			return ProcessMesParameterToWCIntf.getDefaultValues(paramsMap);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static Map<String, String> getProcessNumberGroup(Map<String, String> paramsMap){
		try {
			return ProcessMesParameterToWCIntf.getProcessNumberGroup(paramsMap);
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
