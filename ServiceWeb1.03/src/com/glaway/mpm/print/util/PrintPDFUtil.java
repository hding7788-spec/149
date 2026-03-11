package com.glaway.mpm.print.util;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.representation.Representable;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;

import com.glaway.mpm.constants.ProcessPlanConstants;
import com.glaway.mpm.model.data.CmAttachment;
import com.glaway.mpm.parameter.util.PersistableUtil;
import com.glaway.mpm.print.GWPrintApplyRecordManager;
import com.glaway.mpm.print.PrintRecordHelper;
import com.glaway.mpm.print.constants.PrintServerConstants;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.data.CmQrCode;
import com.glaway.mpm.print.image.FileImageCreator;
import com.glaway.mpm.print.image.SimpleDrawer;
import com.glaway.mpm.print.model.GWPrintApplyRecord;
import com.glaway.mpm.print.sign.Position;
import com.glaway.mpm.util.DateUtil;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMUtil;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import ext.casc.util.CommonUtil;
import ext.casc.util.IBAUtility;
import ext.casc.util.PropertiesReader;
import ext.casc.util.WCUtil;

public class PrintPDFUtil {

	private static final String filePrintProperties = "d800.conf.filePrintPdfSignatureConfig";
	static PropertiesReader signtemplateReader = new PropertiesReader(filePrintProperties, "UTF-8", "ISO-8859-1");
	private static String WTHOME = "";
	private static String FONT = "";

	static {
		 try {
			WTHOME = WTProperties.getLocalProperties().getProperty("wt.home", "");
			FONT = WTHOME + File.separator
					+ "codebase" + File.separator + "ext" + File.separator
					+ "a800" + File.separator + "fileprint" + File.separator
					+ "simsun.ttc,1";
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得配置文件中配置的坐标
	 * @throws IOException
	 */
	public static List<Position> getConfigPositions(Persistable per, String distributeDeptAndCount, String baseline, CmQrCode qrCode, String folderName, String temporarySeal,String fileType,boolean isSelfPrint) throws WTException, IOException {
		List<Position> positions = null;
		if (per != null) {
			positions = new ArrayList<Position>();
			if (per instanceof MPMProcessPlan) {
				generateProcessPlanConfigs(per, positions, qrCode, distributeDeptAndCount, baseline, folderName, temporarySeal, isSelfPrint);
			} else if (per instanceof WTDocument) {
				if(PrintServerConstants.FILETYPE_GYTZD.equals(fileType)
						||PrintServerConstants.FILETYPE_GYZFA.equals(fileType)
						||PrintServerConstants.FILETYPE_QTBG.equals(fileType)
						||PrintServerConstants.FILETYPE_BZHDG.equals(fileType)
						||PrintServerConstants.FILETYPE_BZHSCBG.equals(fileType)
						||PrintServerConstants.FILETYPE_BZHZHYQ.equals(fileType)){
					generateFilePrintApplyConfigs(per, positions, qrCode, distributeDeptAndCount, baseline, folderName, temporarySeal,fileType,isSelfPrint);
				}
				/*preStr = "DOCUMENT";
				WTDocument doc = (WTDocument) per;
				pindex = IBAHelper.getIBAValue(doc, ProcessPlanConstants.MBA_PINDEX);*/
			} else if (per instanceof WTChangeOrder2) {
				generateChangeConfigs(per, positions, qrCode, distributeDeptAndCount, baseline, folderName, temporarySeal, isSelfPrint);
			}
		} else {
			positions = new ArrayList<Position>(0);
		}

		return positions;
	}

	public static void signature(InputStream istream, String targetPdfPath,
			List<Position> positions, int fromPage, int toPage) {
		OutputStream ostream = null;
		PdfReader reader = null;
		PdfStamper stamper = null;
		Image image = null;
		Image fmImage = null;
		Image seal = null;
		Image fmSeal = null;
		try {
			ostream = new FileOutputStream(targetPdfPath);
			// 创建pdf读入流
			reader = new PdfReader(istream);
			// 根据pdfreader创建pdfStamper,生成新的pdf
			stamper = new PdfStamper(reader, ostream);
			BaseFont bfont = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			Font font = new Font(bfont, 18);
			font.setStyle(Font.BOLD);
			int pageNum = reader.getNumberOfPages();

			// 起始页不小于1，不大于总页数
			fromPage = fromPage <= 0 ? 1 : fromPage;
			fromPage = fromPage > pageNum ? pageNum : fromPage;

			// 结束页不小于起始页，不大于总页数，如果不大于0，则签署所有页
			toPage = toPage <= 0 ? pageNum : toPage;
			toPage = toPage < fromPage ? fromPage : toPage;
			toPage = toPage > pageNum ? pageNum : toPage;

			for (int i = fromPage; i <= toPage; i++) {
				// 获得pdfstamper在当前页的上层打印内容，即当前写入内容会覆盖在原有的pdf内容之上.
				PdfContentByte content = stamper.getOverContent(i);
				for (Position position : positions) {
					String text = position.getText();
					String qrCodeInfoFM = position.getQrCodeInfoFMText();
					String qrCodeInfoContent = position.getQrCodeInfoContentText();
					image = position.getImage();
					fmImage = position.getFmImage();
					seal = position.getSeal();
					fmSeal = position.getFmSeal();
					if (i == 1 && text != null) {
						// 写入文本
						content.beginText();
						// 设置字体和大小
						if (position.getFontColor() != null && position.getFontSize() != 0 && position.getFontStyle() != 0) {
							bfont = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
							content.setFontAndSize(bfont, position.getFontSize());
							content.setColorFill(position.getFontColor());
							content.setTextMatrix(1.5f, 0f, 0f, 1.5f, position.getWidth(), position.getHeight());
						} else {
							content.setFontAndSize(font.getBaseFont(), 10);
							// 设置字体的输出位置
							content.setTextMatrix(position.getWidth(), position.getHeight());
						}
						// 要输出的text
						content.showText(text);
						content.endText();
						content.closePathStroke();
					} else if (i == 1 && qrCodeInfoFM != null) {
						// 写入文本
						content.beginText();
						// 设置字体和大小
						if (position.getFontColor() != null && position.getFontSize() != 0 && position.getFontStyle() != 0) {
							bfont = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
							content.setFontAndSize(bfont, position.getFontSize());
							content.setColorFill(position.getFontColor());
							content.setTextMatrix(1.5f, 0f, 0f, 1.5f, position.getWidth(), position.getHeight());
						} else {
							content.setFontAndSize(font.getBaseFont(), 10);
							// 设置字体的输出位置
							content.setTextMatrix(position.getWidth(), position.getHeight());
						}
						// 要输出的text
						content.showText(qrCodeInfoFM);
						content.endText();
						content.closePathStroke();
					} else if (i == 1 && fmImage != null) {
						content.addImage(fmImage, true);
						fmImage = null;
					} else if (i != 1 && image != null) {
						content.addImage(image, true);
						image = null;
					} else if (i == 1 && fmSeal != null) {
						content.addImage(fmSeal);
						fmSeal = null;
					} else if (i != 1 && seal != null) {
						content.addImage(seal);
						seal = null;
					} else if (i != 1 && qrCodeInfoContent != null) {
						// 写入文本
						content.beginText();
						// 设置字体和大小
						if (position.getFontColor() != null && position.getFontSize() != 0 && position.getFontStyle() != 0) {
							bfont = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
							content.setFontAndSize(bfont, position.getFontSize());
							content.setColorFill(position.getFontColor());
							content.setTextMatrix(1.5f, 0f, 0f, 1.5f, position.getWidth(), position.getHeight());
						} else {
							content.setFontAndSize(font.getBaseFont(), 10);
							// 设置字体的输出位置
							content.setTextMatrix(position.getWidth(), position.getHeight());
						}
						// 要输出的text
						content.showText(qrCodeInfoContent);
						content.endText();
						content.closePathStroke();
					}

				}

			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (DocumentException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (stamper != null) {
					stamper.close();
				}
				if (ostream != null) {
					ostream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void generatePosition(String coords, String offsetStr, List<Position> positions, String text) throws WTException {
		String[] coordinates = coords.split(",");
		float axisX =  Float.parseFloat(coordinates[0]);
		float axisY =  Float.parseFloat(coordinates[1]);
		float offset =  Float.parseFloat(offsetStr);
		if (text != null && !"".equals(text)) {
			String[] textArr = text.split(",");
			for (int i = 0; i < textArr.length; i++) {
				if (i > 0) {
					axisY = axisY - offset;
				}
				Position position = new Position(axisX, axisY, textArr[i].trim());
				positions.add(position);
			}
		}
	}

	private static void generateChangePosition(String coords, String offsetStr, List<Position> positions, String text) throws WTException {
		String[] coordinates = coords.split(",");
		float axisX =  Float.parseFloat(coordinates[0]);
		float axisY =  Float.parseFloat(coordinates[1]);
		float offset =  Float.parseFloat(offsetStr);
		if (text != null && !"".equals(text)) {
			String[] textArr = text.split(",");
			for (int i = 0; i < textArr.length; i++) {
				if (i != 0 && i % 5 == 0) {
					axisX =  Float.parseFloat(coordinates[0]);
					axisY =  axisY - 20;
				} else {
					if (i > 0) {
						axisX = axisX + offset;
					}
				}
				Position position = new Position(axisX, axisY, textArr[i].trim());
				positions.add(position);
			}
		}
	}
	/*
	 * --2017.02.14---*
	 * 工艺通知单将分部门信息打印在PDF上
	 */
	private static void generateGYTZDPosition(String coords, String offsetStr, List<Position> positions, String text) throws WTException {
		String[] coordinates = coords.split(",");
		float axisX =  Float.parseFloat(coordinates[0]);
		float axisY =  Float.parseFloat(coordinates[1]);
		float offset =  Float.parseFloat(offsetStr);
		if (text != null && !"".equals(text)) {
			String[] textArr = text.split(",");
			int cont = textArr.length/5+1;
			int co = 0;
			for(int j = 0;j<cont;j++){
				String deptText = "";
				if(j==0&&cont>1){
					co = 5;
				}else{
					co = textArr.length%5;
				}
				for (int i = j*5; i < co+j*5; i++) {
					if((i+1)%5==0||i==(textArr.length-1)){
						deptText = deptText+textArr[i].trim();
					}else{
						deptText = deptText+textArr[i].trim()+", ";
					}
					Position position = new Position(axisX, axisY, deptText);
					positions.add(position);
				}
				axisY =  axisY - 20;
			}
		}
	}

	private static void generateBaselinePosition(String coords, String offsetStr, List<Position> positions, String text, String pindex, String folderName) throws WTException, IOException {
		String[] coordinates = coords.split(",");
		float axisX =  Float.parseFloat(coordinates[0]);
		float axisY =  Float.parseFloat(coordinates[1]);
		float offset =  Float.parseFloat(offsetStr);
		if (text != null && !"".equals(text)) {
			String[] textArr = text.split(",");
			for (int i = 0; i < textArr.length; i++) {
				if (i > 0) {
					axisY = axisY - offset;
				}

				String imgPath = generateBaselineImage(" " + pindex + textArr[i].trim() + " ", folderName);
				Position position = new Position();
				position.setFMImage(imgPath, 60, axisX, axisY);
				positions.add(position);
			}
		}
	}

	private static void generateBaselineChangePosition(String coords, String offsetStr, List<Position> positions, String text, String pindex, String folderName) throws WTException, IOException {
		String[] coordinates = coords.split(",");
		float axisX =  Float.parseFloat(coordinates[0]);
		float axisY =  Float.parseFloat(coordinates[1]);
		float offset = Float.parseFloat(offsetStr);
		if (text != null && !"".equals(text)) {
			String[] textArr = text.split(",");
			for (int i = 0; i < textArr.length; i++) {
				if (i != 0 && i % 3 == 0) {
					axisX =  Float.parseFloat(coordinates[0]);
					axisY =  axisY - 35;
				} else {
					if (i > 0) {
						axisX = axisX + offset;
					}
				}

				String imgPath = generateBaselineImage(" " + pindex + textArr[i].trim() + " ", folderName);
				Position position = new Position();
				position.setFMImage(imgPath, 60, axisX, axisY);
				positions.add(position);
			}
		}
	}

	private static void generateImagePosition(String coords, List<Position> positions, CmQrCode qrCode, String folderName) throws WTException {
		String[] coordinates = coords.split(",");
		float axisX =  Float.parseFloat(coordinates[0]);
		float axisY =  Float.parseFloat(coordinates[1]);
		Position position = new Position();
		String qrCodePath = FileUtil.getWncTmpPath() + File.separator + PrintServerConstants.FOLDER_PRINT + File.separator + folderName + File.separator + PrintServerConstants.FOLDER_PRINT_QRCODE + File.separator + qrCode.getNumber() + PrintServerConstants.JPG;
		position.setImage(qrCodePath, axisX, axisY);
		positions.add(position);
	}

	private static void generateFMImagePosition(String coords, List<Position> positions, CmQrCode qrCode, String folderName) throws WTException {
		String[] coordinates = coords.split(",");
		float axisX =  Float.parseFloat(coordinates[0]);
		float axisY =  Float.parseFloat(coordinates[1]);
		Position position = new Position();
		String qrCodePath = FileUtil.getWncTmpPath() + File.separator + PrintServerConstants.FOLDER_PRINT + File.separator + folderName + File.separator + PrintServerConstants.FOLDER_PRINT_QRCODE + File.separator + qrCode.getNumber() + PrintServerConstants.JPG;
		position.setFMImage(qrCodePath, axisX, axisY);
		positions.add(position);
	}

	private static void generateSealPosition(String coords, List<Position> positions, String sealPath, String folderName) throws WTException {
		String[] coordinates = coords.split(",");
		float axisX =  Float.parseFloat(coordinates[0]);
		float axisY =  Float.parseFloat(coordinates[1]);
		Position position = new Position();
		position.setFmSeal(sealPath, 35, axisX, axisY);
		positions.add(position);
	}

	private static void generateSealContentPosition(String coords, List<Position> positions, String sealPath, String folderName) throws WTException {
		String[] coordinates = coords.split(",");
		float axisX =  Float.parseFloat(coordinates[0]);
		float axisY =  Float.parseFloat(coordinates[1]);
		Position position = new Position();
		position.setSeal(sealPath, 35, axisX, axisY);
		positions.add(position);
	}

	private static void generateQRCodeInfoPosition(String coords, String offsetStr, List<Position> positions, String text, boolean isFm) throws WTException {
		String[] coordinates = coords.split(",");
		float axisX = Float.parseFloat(coordinates[0]);
		float axisY = Float.parseFloat(coordinates[1]);
		float offset = Float.parseFloat(offsetStr);
		if (text != null && !"".equals(text)) {
			String[] textArr = text.split(",");
			for (int i = 0; i < textArr.length; i++) {
				if (i > 0) {
					axisY = axisY - offset;
				}
				Position position = new Position(axisX, axisY, textArr[i].trim(), isFm);
				positions.add(position);
			}
		}
	}

	public static List<CmAttachment> printPDFAndReturn(List<CmPrintInfoBean> list) throws Exception {
		List<CmAttachment> attachlist = new ArrayList<CmAttachment>();
		if (list != null) {
			String tempId = String.valueOf(System.currentTimeMillis());
			String signPdfPath = FileUtil.makeWncTmpDir(PrintServerConstants.FOLDER_PRINT + File.separator + String.valueOf(tempId) + File.separator + PrintServerConstants.FOLDER_SIGNPDF);
			String printQRCodePath = FileUtil.makeWncTmpDir(PrintServerConstants.FOLDER_PRINT + File.separator + String.valueOf(tempId) + File.separator + PrintServerConstants.FOLDER_PRINT_QRCODE);
			String printPDFPath = FileUtil.makeWncTmpDir(PrintServerConstants.FOLDER_PRINT + File.separator + String.valueOf(tempId) + File.separator + PrintServerConstants.FOLDER_PRINT_PDF);
			for (CmPrintInfoBean infoBean : list) {
				GWPrintApplyRecord gwPrintApplyRecord = GWPrintApplyRecordManager.queryGWPrintApplyRecordByQRCode(infoBean.getQrCode());
				if (gwPrintApplyRecord == null)
					PrintRecordHelper.service.createGwPrintApplyRecord(infoBean);

				CmQrCode qrCode = infoBean.getCmQrCode();
				CmAttachment qrCodeAttach = qrCode.getAttachment();
				String qrCodeFilePath = printQRCodePath + File.separator + qrCodeAttach.getFileName();
				FileUtil.writeBytes(qrCodeFilePath, qrCodeAttach.getBytes());

				Persistable per = PersistableUtil.getPersistable(infoBean.getOid());
				ApplicationData appData = null;
				if (per instanceof MPMProcessPlan
						|| per instanceof WTDocument) {
					appData = WCUtil.getRepresentation((Representable) per);
				}

				if (appData == null) {
					appData =  PrintUtil.getPDFFile((ContentHolder) per);
				}

				if (appData != null) {
					byte[] bytes = CommonUtil.applicationDataToByte(appData);
					InputStream istream = ContentServerHelper.service.findContentStream(appData);
					String pdfFilePath = printPDFPath + File.separator + appData.getFileName();
					FileUtil.writeBytes(pdfFilePath, bytes);

					String temporarySeal = CommonUtil.objectToString(infoBean.getTemporarySeal());
					List<Position> positions = getConfigPositions(per, "", "", qrCode, tempId, temporarySeal,infoBean.getFileType(), true);
					String targetPdfPath = signPdfPath + File.separator + per.getPersistInfo().getObjectIdentifier().getId() + "_sign.pdf";
					PrintPDFUtil.signature(istream, targetPdfPath, positions, 0, 0);

					File pdfSignFile = new File(targetPdfPath);
					if (pdfSignFile.exists()) {
						CmAttachment attachment = new CmAttachment();
						attachment.setFileName(pdfSignFile.getName());
						attachment.setBytes(FileUtil.readFilePathToByte(pdfSignFile));

						attachlist.add(attachment);
					}
				}
			}
			String filePath = FileUtil.getWncTmpDir(PrintServerConstants.FOLDER_PRINT + File.separator + String.valueOf(tempId));
			FileUtil.deleteSubFile(filePath);
		}
		return attachlist;
	}

	public static void generateProcessPlanConfigs(Persistable per, List<Position> positions, CmQrCode qrCode, String distributeDeptAndCount, String baseline, String folderName, String temporarySeal, boolean isSelfPrint) throws WTException, IOException {
		String preStr = "PROCESSPLAN";
		String processCategory = CommonUtil.objectToString(MBAUtil.getValue(per, ProcessPlanConstants.MBA_PROCESSCATEGORY));
		if (processCategory.equals(ProcessPlanConstants.DOCTYPE_GYZTB)) {
			preStr = "GYZTB";
		}
		String pindex = CommonUtil.objectToString(MBAUtil.getValue(per, ProcessPlanConstants.MBA_PINDEX));

		String deptCoords = signtemplateReader.getValue(preStr + "_Dept");
		String deptOffSetCoords = signtemplateReader.getValue(preStr + "_Dept_yOffSet");
		String baselineCoords = signtemplateReader.getValue(preStr + "_Baseline");
		String baselineOffSetCoords = signtemplateReader.getValue(preStr + "_Baseline_yOffSet");
		String qrCodeFMCoords = signtemplateReader.getValue(preStr + "_QRCode_FM");
		String qrCodeContentCoords = signtemplateReader.getValue(preStr + "_QRCode_Content");
		String sealCoords = signtemplateReader.getValue(preStr + "_Seal_FM");
		String sealContentCoords = signtemplateReader.getValue(preStr + "_Seal_Content");
		String qrCodeTextFMCoords = signtemplateReader.getValue(preStr + "_QRCodeText_FM");
		String qrCodeTextContentCoords = signtemplateReader.getValue(preStr + "_QRCodeText_Content");

		String isCappImport = CommonUtil.objectToString(MBAUtil.getValue(per, ProcessPlanConstants.MBA_ISCAPPIMPORT));
		if ("是".equals(isCappImport)) {
			deptCoords = "";
			deptOffSetCoords = "";
			qrCodeFMCoords = signtemplateReader.getValue(preStr + "_CAPP_QRCode_FM");
			qrCodeTextFMCoords = signtemplateReader.getValue(preStr + "_CAPP_QRCodeText_FM");
		}

		//分发部门及份数
		if ((deptCoords != null && deptCoords.length() > 0) && (deptOffSetCoords != null && deptOffSetCoords.length() > 0)) {
			generatePosition(deptCoords, deptOffSetCoords, positions, distributeDeptAndCount);
		}

		//技术状态标识
		if ((baselineCoords != null && baselineCoords.length() > 0) && (baselineOffSetCoords != null && baselineOffSetCoords.length() > 0)) {
			generateBaselinePosition(baselineCoords, baselineOffSetCoords, positions, baseline, pindex, folderName);
		}

		//二维码
		if ((qrCodeFMCoords != null && qrCodeFMCoords.length() > 0) && (qrCodeContentCoords != null && qrCodeContentCoords.length() > 0) && qrCode != null) {
			generateFMImagePosition(qrCodeFMCoords, positions, qrCode, folderName);
			generateImagePosition(qrCodeContentCoords, positions, qrCode, folderName);
		}

		//二维码文字信息
		if ((qrCodeTextFMCoords != null && qrCodeTextFMCoords.length() > 0) && (qrCodeTextContentCoords != null && qrCodeTextContentCoords.length() > 0)) {
			String dept = "信息档案处";
			if (isSelfPrint) {
				dept = getGroupsByUser(SessionHelper.getPrincipal());
			}
			String date = DateUtil.getTodayDate("yyyy/MM/dd");
			generateQRCodeInfoPosition(qrCodeTextFMCoords, "18", positions, dept + "," + date, true);
			generateQRCodeInfoPosition(qrCodeTextContentCoords, "18", positions, dept + "," + date, false);
		}

		//临时章
		if (sealCoords != null && sealCoords.length() > 0) {
			String sealPath = "";
			if (temporarySeal.equals(PrintServerConstants.SEAL_SHIYAN)) {
				sealPath = WTHOME + File.separator + "codebase" + File.separator + "ext" + File.separator + "d800" + File.separator + "technicpdf" + File.separator + "print" + File.separator + "sign" + File.separator + "shiyan.png";
			} else if (temporarySeal.equals(PrintServerConstants.SEAL_FANXIU)) {
				sealPath = WTHOME + File.separator + "codebase" + File.separator + "ext" + File.separator + "d800" + File.separator + "technicpdf" + File.separator + "print" + File.separator + "sign" + File.separator + "fan.png";
			} else {
				if (isSelfPrint) {
					sealPath = WTHOME + File.separator + "codebase" + File.separator + "ext" + File.separator + "d800" + File.separator + "technicpdf" + File.separator + "print" + File.separator + "sign" + File.separator + "margin.png";
				}
			}
			if (!sealPath.equals("")) {
				generateSealPosition(sealCoords, positions, sealPath, folderName);
				generateSealContentPosition(sealContentCoords, positions, sealPath, folderName);
			}
		}
	}

	public static void generateChangeConfigs(Persistable per, List<Position> positions, CmQrCode qrCode, String distributeDeptAndCount, String baseline, String folderName, String temporarySeal, boolean isSelfPrint) throws WTException, IOException {
		String preStr = "PROCESSECFORM";
		WTChangeOrder2 ecn = (WTChangeOrder2) per;
		String softType = IBAUtility.getSoftType(ecn);
		if (ProcessPlanConstants.SOFT_INSTANCEPROCESSECFORM.equals(softType)) {
			preStr = "INSTANCEPROCESSECFORM";
		}
		String pindex = IBAHelper.getIBAValue(ecn, ProcessPlanConstants.MBA_PINDEX);

		String deptCoords = signtemplateReader.getValue(preStr + "_Dept");
		String deptOffSetCoords = signtemplateReader.getValue(preStr + "_Dept_xOffSet");
		String baselineCoords = signtemplateReader.getValue(preStr + "_Baseline");
		String baselineOffSetCoords = signtemplateReader.getValue(preStr + "_Baseline_xOffSet");
		String qrCodeFMCoords = signtemplateReader.getValue(preStr + "_QRCode_FM");
		//String sealCoords = signtemplateReader.getValue(preStr + "_Seal_FM");
		String qrCodeTextFMCoords = signtemplateReader.getValue(preStr + "_QRCodeText_FM");

		//分发部门及份数
		if ((deptCoords != null && deptCoords.length() > 0) && (deptOffSetCoords != null && deptOffSetCoords.length() > 0)) {
			generateChangePosition(deptCoords, deptOffSetCoords, positions, distributeDeptAndCount);
		}

		//技术状态标识
		if ((baselineCoords != null && baselineCoords.length() > 0) && (baselineOffSetCoords != null && baselineOffSetCoords.length() > 0)) {
			generateBaselineChangePosition(baselineCoords, baselineOffSetCoords, positions, baseline, pindex, folderName);
		}

		//二维码
		if ((qrCodeFMCoords != null && qrCodeFMCoords.length() > 0) && qrCode != null) {
			generateFMImagePosition(qrCodeFMCoords, positions, qrCode, folderName);
		}

		//二维码文字信息
		if ((qrCodeTextFMCoords != null && qrCodeTextFMCoords.length() > 0)) {
			String dept = "信息档案处";
			if (isSelfPrint) {
				dept = getGroupsByUser(SessionHelper.getPrincipal());
			}
			String date = DateUtil.getTodayDate("yyyy/MM/dd");
			generateQRCodeInfoPosition(qrCodeTextFMCoords, "18", positions, dept + "," + date, true);
		}

		/*//临时章
		if (sealCoords != null && sealCoords.length() > 0) {
			String sealPath = "";
			if (temporarySeal.equals(PrintServerConstants.SEAL_SHIYAN)) {
				sealPath = WTHOME + File.separator + "codebase" + File.separator + "ext" + File.separator + "d800" + File.separator + "technicpdf" + File.separator + "print" + File.separator + "sign" + File.separator + "shiyan.png";
			} else if (temporarySeal.equals(PrintServerConstants.SEAL_FANXIU)) {
				sealPath = WTHOME + File.separator + "codebase" + File.separator + "ext" + File.separator + "d800" + File.separator + "technicpdf" + File.separator + "print" + File.separator + "sign" + File.separator + "fan.png";
			} else {
				if (isSelfPrint) {
					sealPath = WTHOME + File.separator + "codebase" + File.separator + "ext" + File.separator + "d800" + File.separator + "technicpdf" + File.separator + "print" + File.separator + "sign" + File.separator + "margin.png";
				}
			}
			if (!sealPath.equals("")) {
				generateSealPosition(sealCoords, positions, sealPath, folderName);
			}
		}*/
	}
	//--------#2017.02.13#----------//
	/**
	 * 打印工艺通知单时获取坐标
	 *
	 */
	public static void generateFilePrintApplyConfigs(Persistable per, List<Position> positions, CmQrCode qrCode, String distributeDeptAndCount, String baseline, String folderName, String temporarySeal,String fileType, boolean isSelfPrint) throws WTException, IOException {
		String preStr = "";
		if(PrintServerConstants.FILETYPE_GYTZD.equals(fileType)){
			preStr = "DOCUMENT_PROCESS_NOTICE";
			String deptCoords = signtemplateReader.getValue(preStr + "_Dept");
			String deptOffSetCoords = signtemplateReader.getValue(preStr + "_Dept_xOffSet");
			//分发部门及份数
			if ((deptCoords != null && deptCoords.length() > 0) && (deptOffSetCoords != null && deptOffSetCoords.length() > 0)) {
				generateGYTZDPosition(deptCoords, deptOffSetCoords, positions, distributeDeptAndCount);
			}
		}else if(PrintServerConstants.FILETYPE_GYZFA.equals(fileType)){
			preStr = "DOCUMENT_PROCESS_PROGRAM";
		}else if(PrintServerConstants.FILETYPE_QTBG.equals(fileType)
				||PrintServerConstants.FILETYPE_BZHDG.equals(fileType)
				||PrintServerConstants.FILETYPE_BZHSCBG.equals(fileType)
				||PrintServerConstants.FILETYPE_BZHZHYQ.equals(fileType)){
			preStr = "QTBG";
		}

		String qrCodeFMCoords = signtemplateReader.getValue(preStr + "_QRCode_FM");
		String qrCodeTextFMCoords = signtemplateReader.getValue(preStr + "_QRCodeText_FM");

		//二维码
		if ((qrCodeFMCoords != null && qrCodeFMCoords.length() > 0) && qrCode != null) {
			generateFMImagePosition(qrCodeFMCoords, positions, qrCode, folderName);
		}
		//二维码文字信息
		if ((qrCodeTextFMCoords != null && qrCodeTextFMCoords.length() > 0)) {
			String dept = "信息档案处";
			if (isSelfPrint) {
				dept = getGroupsByUser(SessionHelper.getPrincipal());
			}
			String date = DateUtil.getTodayDate("yyyy/MM/dd");
			generateQRCodeInfoPosition(qrCodeTextFMCoords, "18", positions, dept + "," + date, true);
		}
	}

	public static String generateBaselineImage(String text, String folderName) throws IOException {
		java.awt.Font font = new java.awt.Font("宋体", Font.BOLD, 30);
		FontMetrics metrics = new FontMetrics(font) {
		};
		Rectangle2D bounds = metrics.getStringBounds(text, null);
		//System.out.println(bounds.getWidth());
		//int width = 22 * text.length();//每个字符22
		int width = (int) (bounds.getWidth() + 2);
		int height = 50;//每个字符5

		String imgDir = FileUtil.getWncTmpDir(PrintServerConstants.FOLDER_PRINT + File.separator + folderName + File.separator + PrintServerConstants.FOLDER_PRINT_IMAGE);
		FileUtil.mkDirs(imgDir);
		String imgName = String.valueOf(System.nanoTime());
		String imgPath = imgDir + File.separator + imgName + PrintServerConstants.JPG;
		FileImageCreator creator = new FileImageCreator(new SimpleDrawer(), imgPath);
		creator.setWidth(width); //图片宽度
		creator.setHeight(height); //图片高度
		creator.setFontSize(30); //字体大小
		creator.setFontName("宋体");//字体
		creator.setBgColor(Color.WHITE);//背景颜
		creator.setFontColor(Color.RED);//字体颜色
		creator.setFontBold(Font.BOLD);//是否加粗
		creator.setRectColor(Color.RED);//外框颜色
		creator.setBorderWidth(5.0f);//外框粗细

		creator.generateImage(text);
		return imgPath;
	}

	/**
	 * 根据用户获取其所属组
	 * @return
	 * @throws WTException
	 */
	public static String getGroupsByUser(WTPrincipal principal) throws WTException {
		WTGroup wtGroup = null;
    	QueryResult qResult = MPMUtil.queryGroup();
		while (qResult.hasMoreElements()) {
			wtGroup = (WTGroup) qResult.nextElement();
			if(wtGroup.isMember(principal) && wtGroup.getName().startsWith("部门_")) {
				String groupName = wtGroup.getName();
				groupName = groupName.split("_")[1];
				return groupName;
			}
		}
		return "";
    }
}
