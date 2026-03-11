package com.glaway.mpm.intf;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import wt.method.RemoteAccess;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.mesParameter.MesParameterProcessor;
import com.glaway.mpm.mesParameter.MesParameterServiceImp;
import com.glaway.mpm.mesParameter.model.Bdpsndoc;
import com.glaway.mpm.mesParameter.model.MesBfcccpbh;
import com.glaway.mpm.parameter.model.data.CmParamTableType;

public class ProcessMESParameterToWCIntfRMI implements RemoteAccess{

	private static VaLogger logger = VaLogger.getLogger(ProcessMESParameterToWCIntfRMI.class.getName());

	private static MesParameterServiceImp mesParameterServiceImp = new MesParameterServiceImp();

	public static Vector<Object> getTechnicsByTechnicNumber(String technicNumber){
		return mesParameterServiceImp.getTechnicsByTechnicNumber(technicNumber);
	}

	public static void saveMesParameters(CmParamTableType paramTableType, Map<String,String> paramsMap) {
		MesParameterProcessor.saveMesParameters(paramTableType, paramsMap);
	}

	public static CmParamTableType getCommonParamTableType(CmParamTableType paramTableType) {
		return mesParameterServiceImp.getCommonParamTableType(paramTableType);
	}
	public static CmParamTableType getMESCommonParamTableType(boolean isApproved, Map<String, String> paramsMap) {
		return MesParameterProcessor.getMESCommonParamTableType(isApproved, paramsMap);
	}
	public static CmParamTableType getDataPackageCommonParamTableType(String productNumber, String technicsNumber, String objType, String objNumber, boolean isApproved, String lukahao, String gxPK) {
		return MesParameterProcessor.getDataPackageCommonParamTableType(productNumber, technicsNumber, objType, objNumber, isApproved, lukahao, gxPK);
	}
	public static boolean isMesDataExist(CmParamTableType paramTableType, Map<String, String> paramsMap){
		return MesParameterProcessor.isMesDataExist(paramTableType, paramsMap);
	}
	public static boolean isDataPackageMesDataExist(CmParamTableType paramTableType, String productNumber, String technicsNumber, String objType, String objNumber, String lukahao, String gxPK){
		return MesParameterProcessor.isDataPackageMesDataExist(paramTableType, productNumber, technicsNumber, objType, objNumber, lukahao, gxPK);
	}
	public static List<CmParamTableType> getMesParamTableTypes(boolean isApproved, Map<String, String> paramsMap) {
		return MesParameterProcessor.getMesParamTableTypes(isApproved, paramsMap);
	}
	public static void updataTechnicsPrimary(String technicNumber, byte[] bytes){
		MesParameterProcessor.updataTechnicsPrimary(technicNumber, bytes);
	}
	public static String getTechnicsNumberByProductNumber(String productNumber){
		return MesParameterProcessor.getTechnicsNumberByProductNumber(productNumber);
	}
	public static List<Bdpsndoc> getCampPersonInfo(String name, String number){
		return MesParameterProcessor.getCampPersonInfo(name, number);
	}
	public static Object[] getZFTechnics(String technicNumber){
		return MesParameterProcessor.getZFTechnics(technicNumber);
	}
	public static List<String[]> getFzTechnicsNumberList(String zzTechnicsNumber){
		return MesParameterProcessor.getFzTechnicsNumberList(zzTechnicsNumber);
	}
	public static Vector<Object> getTechnicDocumentByNumberAndVersion(String technicNumber, String version){
		return MesParameterProcessor.getTechnicDocumentByNumberAndVersion(technicNumber, version);
	}
	public static String getZzTechnicsNumber(String technicsNumber){
		return MesParameterProcessor.getZzTechnicsNumber(technicsNumber);
	}
	public static List<MesBfcccpbh> getProcessNumberValues(String lukahao){
		return MesParameterProcessor.getProcessNumberValues(lukahao);
	}
	public static String getDefaultValues(Map<String, String> paramsMap){
		return MesParameterProcessor.getDefaultValues(paramsMap);
	}
	public static Map<String, String> getProcessNumberGroup(Map<String, String> paramsMap){
		return MesParameterProcessor.getProcessNumberGroup(paramsMap);
	}

}
