package com.glaway.mpm.print.helper;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JTable;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.model.data.CmAttachment;
import com.glaway.mpm.model.data.CmBaseline;
import com.glaway.mpm.model.data.CmUser;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmDistributionBean;
import com.glaway.mpm.print.data.CmImportBean;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.data.CmPrintQueryBean;
import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.data.CmSealBean;
import com.glaway.mpm.print.service.PrintToWCIntf;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.wcIntf.UserIntf;

/**
 * 打印模块内部处理类
 * @author Administrator
 *
 */
public class MPMPrintProcessor {

	private static final VaLogger logger = VaLogger.getLogger(MPMPrintProcessor.class.getClass());

	/**
	 * 获取文件关联的所有基线
	 * @param oid
	 * @param fileType
	 * @return
	 */
	public static List<CmBaseline> getBaseline(String oid, String fileType) {
		try {
			return PrintToWCIntf.getBaseline(oid, fileType);
		} catch (RemoteException e) {
			logger.error("服务器异常，请联系系统管理员！", e);
		} catch (InvocationTargetException e) {
			logger.error("服务器异常，请联系系统管理员！", e);
		}
		return new ArrayList<CmBaseline>();
	}

	/**
	 * 获取所有分发部门
	 * @return
	 */
	public static Vector<String> getDistributeDept() {
		try {
			return PrintToWCIntf.getDistributeDept();
		} catch (RemoteException e) {
			logger.error("服务器异常，请联系系统管理员！", e);
		} catch (InvocationTargetException e) {
			logger.error("服务器异常，请联系系统管理员！", e);
		}
		return new Vector<String>();
	}

	/**
	 * 获取所有所外部门
	 * @return
	 */
	public static Vector<String> getOutsideDept() {
		try {
			return PrintToWCIntf.getOutsideDept();
		} catch (RemoteException e) {
			logger.error("服务器异常，请联系系统管理员！", e);
		} catch (InvocationTargetException e) {
			logger.error("服务器异常，请联系系统管理员！", e);
		}
		return new Vector<String>();
	}

	/**
	 * 创建打印申请单
	 * @param list
	 * @return
	 */
	public static String createGwPrintApplyRecords(List<CmPrintInfoBean> list){
		String result = "";
		try {
			result = PrintToWCIntf.createGwPrintApplyRecords(list);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return result;
	}

	/**
	 * 获取打印文件编号（目前只应用于二维码编号获取）
	 * @param objType
	 * @param preFix
	 * @return
	 */
	public static String getPrintObjNumber(String objType, String preFix){
		String number = "";
		try {
			number = PrintToWCIntf.getPrintObjNumber(objType, preFix);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return number;
	}

	/**
	 * 获取当前会话用户信息
	 *
	 * @return CmUser
	 */
	public static CmUser getCurrentUser() {
		CmUser user = null;
		try {
			user = UserIntf.getCurrentUser();
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return user;
	}

	/**
	 * 保存申请记录
	 * @param list
	 * @param pboOid
	 * @return
	 */
	public static String saveGwPrintApplyRecords(List<CmPrintInfoBean> list, String pboOid){
		String result = "";
		try {
			result = PrintToWCIntf.saveGwPrintApplyRecords(list, pboOid);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return result;
	}

	public static String updatePrintState(String qrCodeNumber, long userOid) {
		String result = "";
		try {
			result = PrintToWCIntf.updatePrintState(qrCodeNumber, userOid);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return result;
	}

	public static String updateRejectState(List<String[]> list) {
		String result = "";
		try {
			result = PrintToWCIntf.updateRejectState(list);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return result;
	}

	public static List<CmAttachment> getPdfFiles(List<String> list, String oid) {
		try {
			return  PrintToWCIntf.getPdfFiles(list, oid);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return new ArrayList<CmAttachment>();
	}

	public static List<CmAttachment> printPDFAndReturn(List<CmPrintInfoBean> list) {
		try {
			return  PrintToWCIntf.printPDFAndReturn(list);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return new ArrayList<CmAttachment>();
	}

	public static List<CmPrintInfoBean> addPrintFiles(CmPrintQueryBean cmPrintQueryBean) {
		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		try {
			list = PrintToWCIntf.addPrintFiles(cmPrintQueryBean);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return list;
	}

	public static List<CmPrintInfoBean> addPrintApplicationFiles(CmPrintQueryBean cmPrintQueryBean) {
		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		try {
			list = PrintToWCIntf.addPrintApplicationFiles(cmPrintQueryBean);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return list;
	}

	public static List<CmPrintInfoBean> addFilesOnBom(CmPrintQueryBean cmPrintQueryBean) {
		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		try {
			list = PrintToWCIntf.addFilesOnBom(cmPrintQueryBean);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return list;
	}

	public static List<CmPrintInfoBean> addBomFiles(CmPrintInfoBean cmPrintInfoBean) {
		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		try {
			list = PrintToWCIntf.addBomFiles(cmPrintInfoBean);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return list;
	}
	
	public static List<String> getAllChildPartOid(CmPrintInfoBean cmPrintInfoBean) {
		List<String> list = null;
		try {
			list = PrintToWCIntf.getAllChildPartOid(cmPrintInfoBean);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static List<CmPrintInfoBean> queryBomFilesByPart(String partOid, String mainTechnics) {
		List<CmPrintInfoBean> list = null;
		try {
			list = PrintToWCIntf.queryBomFilesByPart(partOid, mainTechnics);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static Map<String, String> getReceiptPerson(String userName) {
		Map<String, String> map = null;
		try {
			map = PrintToWCIntf.getReceiptPerson(userName);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return map;
	}

	public static List<CmPrintInfoBean> printFilesMgt(CmPrintQueryBean cmPrintQueryBean, boolean isZxdy) {
		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		try {
			list = PrintToWCIntf.printFilesMgt(cmPrintQueryBean, isZxdy);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return list;
	}

	public static String isCanGet(String barCode, String receiptDept) {
		String result = "";
		try {
			result = PrintToWCIntf.isCanGet(barCode, receiptDept);
		} catch (RemoteException e) {
			result = "服务器异常，请联系系统管理员！";
			logger.error(e);
		} catch (InvocationTargetException e) {
			result = "服务器异常，请联系系统管理员！";
			logger.error(e);
		}
		return result;
	}

	public static List<CmPrintInfoBean> getReceiptFile(String barCode, String receiptDept) {
		try {
			return PrintToWCIntf.getReceiptFile(barCode, receiptDept);
		} catch (RemoteException e) {
			logger.error("服务器异常，请联系系统管理员！", e);
		} catch (InvocationTargetException e) {
			logger.error("服务器异常，请联系系统管理员！", e);
		}
		return new ArrayList<CmPrintInfoBean>();
	}

	public static String updateReceiptInfo(List<CmPrintInfoBean> list) {
		String result = "";
		try {
			result = PrintToWCIntf.updateReceiptInfo(list);
		} catch (RemoteException e) {
			result = "服务器异常，请联系系统管理员！";
			logger.error(e);
		} catch (InvocationTargetException e) {
			result = "服务器异常，请联系系统管理员！";
			logger.error(e);
		}
		return result;
	}

	public static List<CmPrintInfoBean> ylqFileAddQuery(CmPrintQueryBean cmPrintQueryBean) {
		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		try {
			list = PrintToWCIntf.ylqFileAddQuery(cmPrintQueryBean);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return list;
	}

	public static String isCanRecover(String barCode, String recoverDept) {
		String result = "";
		try {
			result = PrintToWCIntf.isCanRecover(barCode, recoverDept);
		} catch (RemoteException e) {
			result = "服务器异常，请联系系统管理员！";
			logger.error(e);
		} catch (InvocationTargetException e) {
			result = "服务器异常，请联系系统管理员！";
			logger.error(e);
		}
		return result;
	}

	public static List<CmPrintInfoBean> getRecoverFile(String barCode, String recoverDept) {
		try {
			return PrintToWCIntf.getRecoverFile(barCode, recoverDept);
		} catch (RemoteException e) {
			logger.error("服务器异常，请联系系统管理员！", e);
		} catch (InvocationTargetException e) {
			logger.error("服务器异常，请联系系统管理员！", e);
		}
		return new ArrayList<CmPrintInfoBean>();
	}

	public static String createGwPrintRecoverRecords(List<CmPrintInfoBean> list, String oid){
		String result = "";
		try {
			result = PrintToWCIntf.createGwPrintRecoverRecords(list, oid);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return result;
	}

	public static String updateRecoverInfo(List<CmPrintInfoBean> list) {
		String result = "";
		try {
			result = PrintToWCIntf.updateRecoverInfo(list);
		} catch (RemoteException e) {
			result = "服务器异常，请联系系统管理员！";
			logger.error(e);
		} catch (InvocationTargetException e) {
			result = "服务器异常，请联系系统管理员！";
			logger.error(e);
		}
		return result;
	}

	public static Map<String, String> queryUserInfoByCode(String userCode){
		Map<String, String> map = null;
		try {
			map = PrintToWCIntf.queryUserInfoByCode(userCode);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return map;
	}

	public static String cancelPrintState(String qrCodeNumber) {
		String result = "";
		try {
			result = PrintToWCIntf.cancelPrintState(qrCodeNumber);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return result;
	}

	public static List<CmPrintInfoBean> getDistributeAndRecoverInfo(String qrCodeNumber){
		List<CmPrintInfoBean> cmPrintInfoBeans = null;
		try {
			cmPrintInfoBeans = PrintToWCIntf.getDistributeAndRecoverInfo(qrCodeNumber);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cmPrintInfoBeans;
	}

	public static Map<String, byte[]> getPdfFileForLookUp(List<CmPrintInfoBean> list) {
		Map<String, byte[]> map = null;
		try {
			map = PrintToWCIntf.getPdfFileForLookUp(list);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return map;
	}
	public static Map<String, byte[]> checkIsHasPrint(List<CmPrintInfoBean> list) {
		Map<String, byte[]> map = null;
		try {
			map = PrintToWCIntf.checkIsHasPrint(list);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 自行打印查询添加控制
	 * @return
	 */
	public static List<Integer> checkReferCondition(CmPrintQueryBean cmPrintQueryBean,JTable table,String [] fyleTypes){
		List<Integer> list = new ArrayList<Integer>();
		for (int row = 0; row < table.getRowCount(); row++) {
			boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(cmPrintQueryBean.getPrintDateCheck()));
			boolean isEqually = false;
			do{
				int conn = 0;
				/*
				 * 编号
				 */
				if(!"".equals(cmPrintQueryBean.getFileNumber().trim())){
					if(CommonUtil.objectToString(table.getValueAt(row,2)).indexOf(cmPrintQueryBean.getFileNumber().trim())>=0){
						isEqually = true;
					}else{
						isEqually = false;
						break;
					}
				}else{
					conn++;
				}
				/*
				 *名称
				 */
				if(!"".equals(cmPrintQueryBean.getFileName().trim())){
					if(CommonUtil.objectToString(table.getValueAt(row,3)).indexOf(cmPrintQueryBean.getFileName().trim())>=0){
						isEqually = true;
					}else{
						isEqually = false;
						break;
					}
				}else{
					conn++;
				}
				/*
				 * 版本
				 */
				if(!"".equals(cmPrintQueryBean.getVersion().trim())){
					if(CommonUtil.objectToString(table.getValueAt(row, 5)).indexOf(cmPrintQueryBean.getVersion().trim())>=0){
						isEqually = true;
					}else{
						isEqually = false;
						break;
					}
				}else{
					conn++;
				}
				/*
				 * 阶段标记
				 */
				if(!"".equals(cmPrintQueryBean.getPhaseCode().trim())){
					if(cmPrintQueryBean.getPhaseCode().trim().equals(CommonUtil.objectToString(table.getValueAt(row, 6)))){
						isEqually = true;
					}else{
						isEqually = false;
						break;
					}
				}else{
					conn++;
				}
				if(!"".equals(cmPrintQueryBean.getFileType().trim())){
					String fileType = cmPrintQueryBean.getFileType().trim();
					if(fileType.equals(PrintConstants.FILETYPE_QTBG)){
						for (int i = 0; i < fyleTypes.length; i++) {
							String fyletypes = fyleTypes[i];
							if(fyletypes.equals(CommonUtil.objectToString(table.getValueAt(row, 7)))){
								isEqually = true;
								break;
							}else{
								isEqually = false;
							}
						}
						if(!isEqually){
							break;
						}
					}else{
						if(fileType.equals(CommonUtil.objectToString(table.getValueAt(row, 7)))){
							isEqually = true;
						}else{
							isEqually = false;
							break;
						}
					}
				}else{
					conn++;
				}
				if(!"".equals(cmPrintQueryBean.getFileState().trim())){
					if(cmPrintQueryBean.getFileState().trim().equals(CommonUtil.objectToString(table.getValueAt(row, 12)))){
						isEqually = true;
					}else{
						isEqually = false;
						break;
					}
				}else{
					conn++;
				}
				if(isSelect){
					try {
						Date statedate = new SimpleDateFormat("yyyy-MM-dd").parse(cmPrintQueryBean.getPrintBeginDate());
						Date overdate = new SimpleDateFormat("yyyy-MM-dd").parse(cmPrintQueryBean.getPrintOverDate());
						if("".equals(CommonUtil.objectToString(table.getValueAt(row, 14)).trim())){
							isEqually = false;
							break;
						}else{
							Date printdate = new SimpleDateFormat("yyyy/MM/dd").parse(CommonUtil.objectToString(table.getValueAt(row, 14)).trim());
							if(printdate.after(statedate)&&printdate.before(overdate)||printdate.equals(statedate)||printdate.equals(overdate)){
								isEqually = true;
							}else{
								isEqually = false;
								break;
							}
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					conn++;
				}
				if(conn==7){
					isEqually = true;
				}
				if(isEqually){
					break;
				}
			}while(isEqually);
			if (!isEqually) {
				list.add(row);
			}
		}
		return list;
	}
	//20170316——jiangyixing
	public static List<CmPrintInfoBean> printFilesTYDYGL() {
		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		try {
			list = PrintToWCIntf.printFilesTYDYGL();
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return list;
	}

	protected static List<CmPrintInfoBean> queryBaselines(CmPrintQueryBean printQueryBean) {
		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		try {
			list = PrintToWCIntf.queryBaselines(printQueryBean);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return list;
	}
	//20170401——jiangyixing
	public static boolean checkComplete(String qrCodeNumber) {
		boolean flag = true;
		String state = "";
		try {
			state = PrintToWCIntf.checkComplete(qrCodeNumber);
			if(state==null||"已批准".equals(state)){
				flag = false;
			}
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return flag;
	}
	public static Vector<String> getAllMPMSkill(){
		Vector<String> vector = new Vector<String>();
		try {
			vector = PrintToWCIntf.getAllMPMSkill();
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		if(vector == null){
			vector = new Vector<String>();
		}
		return vector;
	}
	public static Vector<String> getProductMindex(){
		Vector<String> vector = new Vector<String>();
		try {
			vector = PrintToWCIntf.getProductMindex();
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return vector;
	}

	public static List<CmPrintInfoBean> loadPrintApplication(String oid) {
		try {
			return PrintToWCIntf.loadPrintApplication(oid);
		} catch (RemoteException e) {
			logger.error("服务器异常，请联系系统管理员！", e);
		} catch (InvocationTargetException e) {
			logger.error("服务器异常，请联系系统管理员！", e);
		}
		return new ArrayList<CmPrintInfoBean>();
	}

	public static void setPrintStatus(List<String> list, String status, String printer, String printDate) {
		PrintToWCIntf.setPrintStatus(list,status,printer,printDate);
	}

	public static List<CmPrintInfoBean> loadPaperFile(String oid, String category) {
		try {
			return PrintToWCIntf.loadPaperFile(oid, category);
		} catch (RemoteException e) {
			logger.error("服务器异常，请联系系统管理员！", e);
		} catch (InvocationTargetException e) {
			logger.error("服务器异常，请联系系统管理员！", e);
		}
		return new ArrayList<CmPrintInfoBean>();
	}


	public static List<CmPrintInfoBean> searchSealPlus(CmPrintQueryBean cmPrintQueryBean, String category) {
		return PrintToWCIntf.searchSealPlus(cmPrintQueryBean, category);
	}

	public static String updatePrintAddSeal(ArrayList<CmSealBean> list, String oid) {
		return PrintToWCIntf.updatePrintAddSeal(list, oid);
	}

	public static List<CmPrintInfoBean> loadSealPlus(String oid, String category) {
		return PrintToWCIntf.loadSealPlus(oid,category);
	}

	public static List<CmPrintInfoBean> getQRbarcode(String oid) {
		return PrintToWCIntf.getQRbarcode(oid);
	}

	public static List<CmPrintInfoBean> addFileOnProcessDirectory(CmPrintQueryBean cmPrintQueryBean) {
		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		try {
			list = PrintToWCIntf.addFileOnProcessDirectory(cmPrintQueryBean);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return list;
	}

	public static List<CmPrintInfoBean> getProcessFiles(CmPrintInfoBean cmPrintInfoBean) {
//		CmProcessFile cmProcessFile = new CmProcessFile();
		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		try {
			list = PrintToWCIntf.getProcessFiles(cmPrintInfoBean);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return list;
	}

	public static List<String> setFileStatus(List<String> list, CmDistributionBean cmDistributionBean) {
		try {
			list = PrintToWCIntf.setFileStatus(list,cmDistributionBean);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return list;
	}

	public static List<CmPrintInfoBean> searchOutFile(CmPrintQueryBean cmPrintQueryBean) {
		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		try {
			list = PrintToWCIntf.searchOutFile(cmPrintQueryBean);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
		return list;
	}

	public static void deleteOutFileByID(List<CmPrintInfoBean> beanList) {
		try {
			PrintToWCIntf.deleteOutFileByID(beanList);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}
	}

	public static void saveChangeNoticeInfo(CmPrintQueryBean cmPrintQueryBean, String id, boolean isModify) {
		try {
			PrintToWCIntf.saveChangeNoticeInfo(cmPrintQueryBean,id,isModify);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		}

		// TODO Auto-generated method stub

	}

	public static List<String> getAddBatch(String oid) {
		List<String> list = null;
		try {
			list = PrintToWCIntf.getAddBatch(oid);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<CmPrintInfoBean> getReceiveData(String userName, String oid, String category) {
		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		try {
			list = PrintToWCIntf.getReceiveData(userName, oid, category);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取部门 add by zhuhao
	 * @return
	 */
	public static String[] getAllDept() {
		try {
			return PrintToWCIntf.getAllDept();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static CmDistributionBean getReceiveMessage(String name) {
		try {
			return PrintToWCIntf.getReceiveMessage(name);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getCategory(String oid) {
		try {
			return PrintToWCIntf.getCategory(oid);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] getImageByte(String qrName) {
		try {
			return PrintToWCIntf.getImageByte(qrName);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static List<CmPrintInfoBean> loadPrintBarCode(String oid, String category,String dept) {
		try {
			return PrintToWCIntf.loadPrintBarCode(oid,category, dept);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setPrintStatus(List<String> idList) {
		try {
			PrintToWCIntf.setPrintStatus(idList);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static List<CmPrintInfoBean> saveImportInfo(List<CmImportBean> beanList) {
		try {
			return PrintToWCIntf.saveImportInfo(beanList);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<String> getTechnicsNumberByBOM(String value) {
		try {
			return PrintToWCIntf.getTechnicsNumberByBOM(value);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static List<CmPrintRecordInfoBean> queryPrintInfo(List<String> technicsList) {
		try {
			return PrintToWCIntf.queryPrintInfo(technicsList);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<String> getTechnicsNumberByProcessDirectory(String value) {
		try {
			return PrintToWCIntf.getTechnicsNumberByProcessDirectory(value);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<String> queryBarTableID(List<String> strList) {
		try {
			return PrintToWCIntf.queryBarTableID(strList);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<CmPrintRecordInfoBean> getQueryRecoverTableIsDelay(List<String> list) {
		try {
			return PrintToWCIntf.queryRecoverTableIsDelay(list);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<CmPrintRecordInfoBean> queryRecoverValueByUser(List<String> strList, String userName) {
		try {
			return PrintToWCIntf.queryRecoverValueByUser(strList, userName);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setProcessState(String pboOid, String state) {
		try {
			PrintToWCIntf.setProcessState(pboOid, state);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void setChangeFileState(List<String> strList) {
		try {
			PrintToWCIntf.setChangeFileState(strList);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	public static void saveLosePboNumber(String pboNumber, List<CmPrintRecordInfoBean> listBean) {
		try {
			PrintToWCIntf.saveLosePboNumber(pboNumber, listBean);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	public static String queryLosePboNumber(String barTableID) {
		try {
			return PrintToWCIntf.queryLosePboNumber(barTableID);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String querydelayPboNumber(String barTableID) {
		try {
			return PrintToWCIntf.querydelayPboNumber(barTableID);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void saveAddSealPlus(String id, String allBatch) {
		try {
			PrintToWCIntf.saveAddSealPlus(id, allBatch);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static String getUserNameBySign(String scanInput) {
		try {
			return PrintToWCIntf.getUserNameBySign(scanInput);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static List<CmPrintInfoBean> searchPrintInfo(CmPrintQueryBean cmPrintQueryBean) {
		try {
			return PrintToWCIntf.searchPrintInfo(cmPrintQueryBean);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String startPrintOffSet(List<CmPrintInfoBean> list) {
		try {
			return PrintToWCIntf.startPrintOffSet(list);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static boolean checkContainerRole(String containerName) {
		try {
			return PrintToWCIntf.checkContainerRole(containerName);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean checkRepeatOutFile(CmPrintQueryBean cmPrintQueryBean, String category) {
		try {
			return PrintToWCIntf.checkRepeatOutFile(cmPrintQueryBean, category);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean checkRepeatInputFile(String fileNumber, String version, String category) {
		try {
			return PrintToWCIntf.checkRepeatInputFile(fileNumber, version, category);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static String getPartType(String partNumber) {
		String result = "";
		try {
			result = PrintToWCIntf.getPartType(partNumber);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, List<String>> getPrinterByDepts(String[] deptSeal) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		try {
			result = PrintToWCIntf.getPrinterByDepts(deptSeal);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String createPrintTransferProcess(List<CmPrintRecordInfoBean> list) {
		String info = "";
		try {
			info = PrintToWCIntf.createPrintTransferProcess(list);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return info;
	}

	public static List<CmPrintRecordInfoBean> loadTransferData(String oid) {
		try {
			return PrintToWCIntf.loadTransferData(oid);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}
