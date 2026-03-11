package com.glaway.mpm.print.util;

import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import com.glaway.mpm.parameter.model.data.CmTechnicsType;
import com.glaway.mpm.parameter.service.ProcessParameterToWCIntf;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.util.FileUtil;

public class FilePrintUtil {
	private static final Logger log = Logger.getLogger(FilePrintUtil.class);
	public static String getPrintType(String type) {
		Map<String, String> map = LoadPrintConfigurations.getInstance().getPrintType();
		return map.get(type);
	}

	public static String mergePdf(List<String> pdfFileList) throws Exception {
		String mergePdfPath = FileUtil.getTmpPath(PrintConstants.FOLDER_PDFTEMP) + File.separator + "mergePdf.pdf";
		PDFMergerUtility mergerUtility = new PDFMergerUtility();
		for (String pdfPath : pdfFileList) {
			mergerUtility.addSource(pdfPath);
		}
		mergerUtility.setDestinationFileName(mergePdfPath);
		mergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
		return mergePdfPath;
	}
	public static String mergePdf(List<String> pdfFileList,String fileName) throws Exception {
		String mergePdfPath = FileUtil.getTmpPath(PrintConstants.FOLDER_PDFTEMP) + File.separator + fileName;
		PDFMergerUtility mergerUtility = new PDFMergerUtility();
		for (String pdfPath : pdfFileList) {
			mergerUtility.addSource(pdfPath);
		}
		mergerUtility.setDestinationFileName(mergePdfPath);
		mergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
		return mergePdfPath;
	}
	public static void print(String filePath) throws PrinterException, IOException {
		/*PrinterJob job = PrinterJob.getPrinterJob();
		job.setJobName(filePath);
		boolean flag = job.printDialog();
		if (flag) {
			PrintService printService = job.getPrintService();
			String[] args = new String[] {"-silentPrint", job.getJobName(), "-printerName", printService.getName()};
			PrintPDF.main(args);
		}*/

		Runtime.getRuntime().exec("cmd.exe /C start acrobat /P " + filePath);
	}
	public static Vector<String> getAllMPMSkill() {
		List<CmTechnicsType> list = null;
		Vector<String> vector = new Vector<String>();
		try {
			list = ProcessParameterToWCIntf.queryTechnicsTypes();
			if(list != null){
				for (CmTechnicsType cmTechnicsType : list) {
					vector.add(cmTechnicsType.getName());
				}
			}
		} catch (RemoteException e) {
			log.error(e);
		} catch (InvocationTargetException e) {
			log.error(e);
		}
		return vector;
	}



}
