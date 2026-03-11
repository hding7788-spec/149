package com.glaway.mpm.print.helper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTable;

import com.glaway.mpm.model.data.CmAttachment;
import com.glaway.mpm.model.data.CmBaseline;
import com.glaway.mpm.model.data.CmUser;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.data.CmPrintQueryBean;
import com.glaway.mpm.print.data.CmQrCode;
import com.glaway.mpm.print.qrCode.QRCode;
import com.glaway.mpm.print.ui.MPMPrintFileFrame;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.FileUtil;

/**
 * 打印模块外部访问类
 *
 */
public class MPMPrintHelper {

	public static List<CmPrintInfoBean> loadData(String oid, String type,String dept) {
		List<CmPrintInfoBean> printInfoBeanList = new ArrayList<CmPrintInfoBean>();
		String category = MPMPrintFileFrame.getCategory();
		//文件打印申请 ,预览打印 列表加载
		if(type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST) ||
				type.equals(PrintConstants.TITLE_MAINPANEL_YLDYSQ)){
			printInfoBeanList = MPMPrintProcessor.loadPrintApplication(oid);
		}
		//执行打印申请
		else if(type.equals(PrintConstants.TITLE_MAINPANEL_ZXDYSQ)){
			printInfoBeanList = MPMPrintProcessor.loadPrintBarCode(oid,category,dept);
		}
		//领取纸质文件
		else if(type.equals(PrintConstants.TITLE_MAINPANEL_LQZZWJ)){
			printInfoBeanList = MPMPrintProcessor.loadPaperFile(oid,category);
		}
		//加盖印章确认
		else if(type.equals(PrintConstants.TITLE_MAINPANEL_JGYZQR)){
			printInfoBeanList = MPMPrintProcessor.loadSealPlus(oid,category);
		}
		//修改加盖印章
		else if(type.equals(PrintConstants.TITLE_MAINPANEL_JGYZGL) && !"".equals(oid) && oid != null){
			printInfoBeanList = MPMPrintProcessor.loadSealPlus(oid,null);
		}
		MPMPrintFileFrame.getFileMainPanel().setUIValues(printInfoBeanList);
		return printInfoBeanList;
	}

	public static List<CmBaseline> getBaseline(String oid, String fileType) {
		return MPMPrintProcessor.getBaseline(oid, fileType);
	}

	public static Vector<String> getDistributeDept() {
		return MPMPrintProcessor.getDistributeDept();
	}

	public static Vector<String> getOutsideDept() {
		return MPMPrintProcessor.getOutsideDept();
	}

	public static List<CmPrintInfoBean> getCmPrintInfoFormFileListTable(JTable table){
		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		for (int row = 0; row < table.getRowCount(); row++) {
			String oid = CommonUtil.objectToString(table.getValueAt(row, 0));
			String fileNumber = CommonUtil.objectToString(table.getValueAt(row, 3));
			String fileName = CommonUtil.objectToString(table.getValueAt(row, 4));
			String pindex = CommonUtil.objectToString(table.getValueAt(row, 5));
			String version = CommonUtil.objectToString(table.getValueAt(row, 6));
			String phaseCode = CommonUtil.objectToString(table.getValueAt(row, 7));
			String fileType = CommonUtil.objectToString(table.getValueAt(row, 8));
			String baseline = CommonUtil.objectToString(table.getValueAt(row, 9));
			String pageCount = CommonUtil.objectToString(table.getValueAt(row, 10));
			String isBlueCard = CommonUtil.objectToString(table.getValueAt(row, 11));
			String ecnNumber = CommonUtil.objectToString(table.getValueAt(row, 12));
			String secret = CommonUtil.objectToString(table.getValueAt(row, 13));
			String distributeDeptAndCount = CommonUtil.objectToString(table.getValueAt(row, 14));
			String temporarySeal = CommonUtil.objectToString(table.getValueAt(row, 15));
			String printDescription = CommonUtil.objectToString(table.getValueAt(row, 16));

			CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
			cmPrintInfoBean.setOid(oid);
			cmPrintInfoBean.setFileNumber(fileNumber);
			cmPrintInfoBean.setFileName(fileName);
			cmPrintInfoBean.setPindex(pindex);
			cmPrintInfoBean.setVersion(version);
			cmPrintInfoBean.setPhaseCode(phaseCode);
			cmPrintInfoBean.setFileType(fileType);
			cmPrintInfoBean.setBaseline(baseline);
			cmPrintInfoBean.setPageCount(pageCount);
			cmPrintInfoBean.setBlueCard(Boolean.parseBoolean(isBlueCard));
			cmPrintInfoBean.setEcnNumber(ecnNumber);
			cmPrintInfoBean.setSecret(secret);
			cmPrintInfoBean.setDistributeDeptAndCount(distributeDeptAndCount);
			cmPrintInfoBean.setTemporarySeal(temporarySeal);
			cmPrintInfoBean.setPrintDescription(printDescription);
			cmPrintInfoBean.setCmUser(MPMPrintFileFrame.getCurrentUser());

			//生成二维码
			CmQrCode cmQrCode = generateQrCode(cmPrintInfoBean, true);
			cmPrintInfoBean.setCmQrCode(cmQrCode);
			list.add(cmPrintInfoBean);
		}
		return list;
	}

	public static List<CmPrintInfoBean> getCmPrintInfoFromSelfPrint(JTable table){
		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		for (int row = 0; row < table.getRowCount(); row++) {
			boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
			if (isSelect) {
				String oid = CommonUtil.objectToString(table.getValueAt(row, 0));
				String fileNumber = CommonUtil.objectToString(table.getValueAt(row, 3));
				String fileName = CommonUtil.objectToString(table.getValueAt(row, 4));
				String pindex = CommonUtil.objectToString(table.getValueAt(row, 5));
				String version = CommonUtil.objectToString(table.getValueAt(row, 6));
				String phaseCode = CommonUtil.objectToString(table.getValueAt(row, 7));
				String fileType = CommonUtil.objectToString(table.getValueAt(row, 8));
				String pageCount = CommonUtil.objectToString(table.getValueAt(row, 9));
				String secret = CommonUtil.objectToString(table.getValueAt(row, 10));
				String temporarySeal = CommonUtil.objectToString(table.getValueAt(row, 11));
				String barCode = CommonUtil.objectToString(table.getValueAt(row, 12));

				CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
				cmPrintInfoBean.setOid(oid);
				cmPrintInfoBean.setFileNumber(fileNumber);
				cmPrintInfoBean.setFileName(fileName);
				cmPrintInfoBean.setPindex(pindex);
				cmPrintInfoBean.setVersion(version);
				cmPrintInfoBean.setPhaseCode(phaseCode);
				cmPrintInfoBean.setFileType(fileType);
				cmPrintInfoBean.setPageCount(pageCount);
				cmPrintInfoBean.setSecret(secret);
				cmPrintInfoBean.setTemporarySeal(temporarySeal);
				cmPrintInfoBean.setCmUser(MPMPrintFileFrame.getCurrentUser());

				//生成二维码
				if ("".equals(barCode)) {
					CmQrCode cmQrCode = generateQrCode(cmPrintInfoBean, true);
					cmPrintInfoBean.setCmQrCode(cmQrCode);
					cmPrintInfoBean.setQrCode(cmQrCode.getNumber());
				} else {
					cmPrintInfoBean.setQrCode(barCode);
					CmQrCode cmQrCode = generateQrCode(cmPrintInfoBean, false);
					cmPrintInfoBean.setCmQrCode(cmQrCode);
				}
				list.add(cmPrintInfoBean);
			}
		}
		return list;
	}

	public static List<CmPrintInfoBean> getCmPrintInfoFromTHQR(JTable table){
		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		for (int row = 0; row < table.getRowCount(); row++) {
			String oid = CommonUtil.objectToString(table.getValueAt(row, 0));
			String barCode = CommonUtil.objectToString(table.getValueAt(row, 8));
			String receiverCount = CommonUtil.objectToString(table.getValueAt(row, 11));
			String recoverDept = CommonUtil.objectToString(table.getValueAt(row, 12));

			if (!receiverCount.equals("0")) {
				CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
				cmPrintInfoBean.setOid(oid);
				cmPrintInfoBean.setQrCode(barCode);
				cmPrintInfoBean.setReceiveCount(receiverCount);
				cmPrintInfoBean.setRecoverDept(recoverDept);
				cmPrintInfoBean.setCmUser(MPMPrintFileFrame.getCurrentUser());

				list.add(cmPrintInfoBean);
			}
		}
		return list;
	}

	public static String createGwPrintApplyRecords(List<CmPrintInfoBean> list){
		return MPMPrintProcessor.createGwPrintApplyRecords(list);
	}

	public static CmQrCode generateQrCode(CmPrintInfoBean cmPrintInfoBean, boolean isNew){
		CmQrCode cmQrCode = new CmQrCode();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = dateFormat.format(new Date());
		String[] dates = date.split("-");
		String year = dates[0];
		String month = dates[1];
		String day = dates[2];
		String number = "";
		if (isNew) {
			number = getPrintObjNumber(PrintConstants.NUMBER_OBJTYPE_QRCODE, year + month + day);
			cmPrintInfoBean.setQrCode(number);
		} else {
			number = cmPrintInfoBean.getQrCode();
		}
		cmQrCode.setNumber(number);
		cmQrCode.setFileType(cmPrintInfoBean.getFileType());
		cmQrCode.setFileNumber(cmPrintInfoBean.getFileNumber());
		cmQrCode.setFileName(cmPrintInfoBean.getFileName());
		cmQrCode.setEcnNumber(cmPrintInfoBean.getEcnNumber());
		cmQrCode.setPageCount(cmPrintInfoBean.getPageCount());
		cmQrCode.setPindex(cmPrintInfoBean.getPindex());
		cmQrCode.setCreator(cmPrintInfoBean.getFileCreator());
		cmQrCode.setPrinter(cmPrintInfoBean.getPrinter());
		cmQrCode.setApplyDate(cmPrintInfoBean.getApplyDate());
		cmQrCode.setDistributeDept(cmPrintInfoBean.getDistributeDeptAndCount());
		cmQrCode.setBaseline(cmPrintInfoBean.getBaseline());
		cmQrCode.setPrintDescription(cmPrintInfoBean.getPrintDescription());

		//生成图片     路径待修改
		String qrCodePath = FileUtil.makeTmpDir(PrintConstants.FOLDER_PRINT_QRCODE);
		String imagePath = qrCodePath + File.separator + number + ".jpg";
		QRCode.encoderQRCode("number:" + cmQrCode.getNumber(), imagePath, "png");

		CmAttachment cmAttachment = new CmAttachment();
		cmAttachment.setAttachmentType(PrintConstants.NUMBER_OBJTYPE_QRCODE);
		File file = new File(imagePath);
		cmAttachment.setBytes(FileUtil.readFilePathToByte(file));
		cmAttachment.setFileName(number + ".jpg");
		cmQrCode.setAttachment(cmAttachment);
		return cmQrCode;
	}

	public static String getPrintObjNumber(String objType, String preFix){
		return MPMPrintProcessor.getPrintObjNumber(objType, preFix);
	}

	public static String updatePrintState(String qrCodeNumber, long userOid) {
		return MPMPrintProcessor.updatePrintState(qrCodeNumber, userOid);
	}

	public static String updateRejectState(List<String[]> list) {
		return MPMPrintProcessor.updateRejectState(list);
	}

	public static List<CmAttachment> getPdfFiles(List<String> list, String oid) {
		return MPMPrintProcessor.getPdfFiles(list, oid);
	}

	public static List<CmAttachment> printPDFAndReturn(List<CmPrintInfoBean> list) {
		return MPMPrintProcessor.printPDFAndReturn(list);
	}

	public static void setTableQrCodeNumber(JTable table, List<CmPrintInfoBean> list) {
		if (list != null) {
			for (CmPrintInfoBean infoBean : list) {
				for (int row = 0; row < table.getRowCount(); row++) {
					String oid = CommonUtil.objectToString(table.getValueAt(row, 0));
					if (infoBean.getOid().equals(oid)) {
						table.setValueAt(infoBean.getQrCode(), row, 12);
						break;
					}
				}
			}
			table.updateUI();
		}
	}

	public static List<CmPrintInfoBean> addPrintFiles(CmPrintQueryBean cmPrintQueryBean) {
		return MPMPrintProcessor.addPrintFiles(cmPrintQueryBean);
	}

	public static List<CmPrintInfoBean> addFilesOnBom(CmPrintQueryBean cmPrintQueryBean){
		return MPMPrintProcessor.addFilesOnBom(cmPrintQueryBean);
	}

	public static List<CmPrintInfoBean> printFilesMgt(CmPrintQueryBean cmPrintQueryBean, boolean isZxdy) {
		return MPMPrintProcessor.printFilesMgt(cmPrintQueryBean, isZxdy);
	}

	public static Map<String, String> getReceiptPerson(String userName) {
		return MPMPrintProcessor.getReceiptPerson(userName);
	}

	public static String isCanGet(String barCode, String receiptDept) {
		return MPMPrintProcessor.isCanGet(barCode, receiptDept);
	}

	public static List<CmPrintInfoBean> getReceiptFile(String barCode, String receiptDept) {
		return MPMPrintProcessor.getReceiptFile(barCode, receiptDept);
	}

	public static String updateReceiptInfo(List<CmPrintInfoBean> list) {
		return MPMPrintProcessor.updateReceiptInfo(list);
	}

	public static List<CmPrintInfoBean> ylqFileAddQuery(CmPrintQueryBean cmPrintQueryBean) {
		return MPMPrintProcessor.ylqFileAddQuery(cmPrintQueryBean);
	}

	public static String isCanRecover(String barCode, String recoverDept) {
		return MPMPrintProcessor.isCanRecover(barCode, recoverDept);
	}

	public static List<CmPrintInfoBean> getRecoverFile(String barCode, String recoverDept) {
		return MPMPrintProcessor.getRecoverFile(barCode, recoverDept);
	}

	public static String createGwPrintRecoverRecords(List<CmPrintInfoBean> list, String oid){
		return MPMPrintProcessor.createGwPrintRecoverRecords(list, oid);
	}

	public static String updateRecoverInfo(List<CmPrintInfoBean> list) {
		return MPMPrintProcessor.updateRecoverInfo(list);
	}

	public static Map<String, String> queryUserInfoByCode(String userCode){
		return MPMPrintProcessor.queryUserInfoByCode(userCode);
	}

	public static boolean verifyVersionDataFormat(String version){
		boolean flag = true;
		if(!version.equals("")){
			flag = CommonUtil.isVersionFormat(version);
		}
		return flag;
	}

	public static boolean verifyAllVersionDataFormat(String version){
		boolean flag = true;
		if(!version.equals("")){
			flag = CommonUtil.isVersion(version);
		}
		return flag;
	}

	public static String cancelPrintState(String qrCodeNumber) {
		return MPMPrintProcessor.cancelPrintState(qrCodeNumber);
	}

	public static List<CmPrintInfoBean> getDistributeAndRecoverInfo(String qrCodeNumber) {
		return MPMPrintProcessor.getDistributeAndRecoverInfo(qrCodeNumber);
	}

	public static Map<String, byte[]> getPdfFileForLookUp(List<CmPrintInfoBean> list) {
		return MPMPrintProcessor.getPdfFileForLookUp(list);
	}

	public static Map<String, byte[]> checkIsHasPrint(List<CmPrintInfoBean> list) {
		return MPMPrintProcessor.checkIsHasPrint(list);
	}

	//20170309_jiangyixing
	public static List<CmPrintInfoBean> getCmPrintInfoFromForLookUp(JTable table){
		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		for (int row = 0; row < table.getRowCount(); row++) {
			boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
			if (isSelect) {
				String oid = CommonUtil.objectToString(table.getValueAt(row, 0));
				if(!oid.startsWith("OR:wt.doc.WTDocument:")){
					CmPrintInfoBean oidBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
					oid = oidBean.getDocVR();
				}
//				String fileType = CommonUtil.objectToString(((CmPrintInfoBean) table.getValueAt(row, 8)).getFileType());
				CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
				cmPrintInfoBean.setOid(oid);
//				cmPrintInfoBean.setFileType(fileType);
//				cmPrintInfoBean.setCmUser(MPMPrintFileFrame.getCurrentUser());

				list.add(cmPrintInfoBean);
			}
		}
		return list;
	}

	public static List<CmPrintInfoBean> printFilesTYDYGL() {
		return MPMPrintProcessor.printFilesTYDYGL();
	}

	public static List<CmPrintInfoBean> queryBaselines(CmPrintQueryBean printQueryBean) {
		return MPMPrintProcessor.queryBaselines(printQueryBean);
	}

	/**
	 * 打印申请获取bean
	 * @param table
	 * @param category
	 * @return
	 */
	public static List<CmPrintInfoBean> getDataFormFileListTable(JTable table, String category) {
		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		CmUser user = MPMPrintFileFrame.getCurrentUser();
		String userName = user.getFullName();
		Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String applyDate = sdf.format(d);
		for (int row = 0; row < table.getRowCount(); row++) {
			CmPrintInfoBean oldBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
			String oid = CommonUtil.objectToString(table.getValueAt(row, 0));
			String fileNumber = CommonUtil.objectToString(table.getValueAt(row, 2));
			String fileName = CommonUtil.objectToString(table.getValueAt(row, 3));
			String version = CommonUtil.objectToString(table.getValueAt(row, 4));
			String phaseCode = CommonUtil.objectToString(table.getValueAt(row, 5));
			String secret = CommonUtil.objectToString(table.getValueAt(row, 6));
			String temporarySeal = CommonUtil.objectToString(table.getValueAt(row, 7));
			String distributeDeptAndCount = CommonUtil.objectToString(table.getValueAt(row, 8));

			String containerName = CommonUtil.objectToString(table.getValueAt(row, 9));
			String fileState = CommonUtil.objectToString(table.getValueAt(row, 10));
			String lifeCycle = CommonUtil.objectToString(table.getValueAt(row, 11));

			String fileType = CommonUtil.objectToString(oldBean.getFileType());
			String pbooid = CommonUtil.objectToString(table.getValueAt(row, 12));
			String technicsNumber = CommonUtil.objectToString(oldBean.getTechnicsNumber());
			String mainTechnicsName = CommonUtil.objectToString(oldBean.getMainTechnics());
			boolean processfile = oldBean.isProcessfile();
			boolean fromBOM = oldBean.isAddFormBOM();
			String processNumber = "";
			if("".equals(CommonUtil.objectToString(oldBean.getProcessNumber()))){
				processNumber = fileNumber;
			}else{
				processNumber = CommonUtil.objectToString(oldBean.getProcessNumber());
			}
			CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
			cmPrintInfoBean.setOid(oid);
			cmPrintInfoBean.setFileNumber(fileNumber);
			cmPrintInfoBean.setFileName(fileName);
			cmPrintInfoBean.setVersion(version);
			cmPrintInfoBean.setPhaseCode(phaseCode);
			cmPrintInfoBean.setSecret(secret);
			cmPrintInfoBean.setDistributeDeptAndCount(distributeDeptAndCount);
			cmPrintInfoBean.setTemporarySeal(temporarySeal);
			cmPrintInfoBean.setApplier(userName);
			cmPrintInfoBean.setApplyDate(applyDate);
			cmPrintInfoBean.setFileType(fileType);
			cmPrintInfoBean.setTechnicsNumber(technicsNumber);
			cmPrintInfoBean.setDocVR(oldBean.getDocVR());
			cmPrintInfoBean.setContainerName(containerName);
			cmPrintInfoBean.setFileState(fileState);
			cmPrintInfoBean.setLifeCycle(lifeCycle);
			cmPrintInfoBean.setPrintpath(oldBean.getPrintpath());
			cmPrintInfoBean.setProcessfile(processfile);
			cmPrintInfoBean.setAddFormBOM(fromBOM);
			cmPrintInfoBean.setPbooid(pbooid);
			cmPrintInfoBean.setProcessNumber(processNumber);
			cmPrintInfoBean.setMainTechnics(mainTechnicsName);
			if("WL".equals(category)){
				cmPrintInfoBean.setOutDept(oldBean.getOutDept());
				cmPrintInfoBean.setDocVR("WL");
			}else if("ZZ".equals(category)){
				cmPrintInfoBean.setOutDept("ZZ");
				cmPrintInfoBean.setDocVR("ZZ");
			}else if("".equals(category) || category == null){
				cmPrintInfoBean.setOutDept("NULL");
			}
//			cmPrintInfoBean.setPbooid(pbooid);
//			cmPrintInfoBean.setCmUser(MPMPrintFileFrame.getCurrentUser());
			list.add(cmPrintInfoBean);
		}
		return list;
	}
}
