<%@ page import="wt.type.TypeDefinitionReference" %>
<%@ page import="wt.type.ClientTypedUtility" %>
<%@ page import="wt.query.QuerySpec" %>
<%@ page import="wt.doc.WTDocument" %>
<%@ page import="wt.query.SearchCondition" %>
<%@ page import="wt.fc.QueryResult" %>
<%@ page import="wt.fc.PersistenceHelper" %>
<%@ page import="wt.vc.config.LatestConfigSpec" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="com.itextpdf.text.pdf.PdfStamper" %>
<%@ page import="com.itextpdf.text.pdf.PdfReader" %>
<%@ page import="wt.pom.Transaction" %>
<%@ page import="wt.util.WTProperties" %>
<%@ page import="java.io.File" %>
<%@ page import="com.itextpdf.text.Image" %>
<%@ page import="com.itextpdf.text.pdf.BaseFont" %>
<%@ page import="java.io.FileOutputStream" %>
<%@ page import="com.itextpdf.text.pdf.PdfContentByte" %>
<%@ page import="com.itextpdf.text.Element" %>
<%@ page import="wt.util.WTException" %>
<%@ page import="java.rmi.RemoteException" %>
<%@ page import="wt.query.ClassAttribute" %>
<%@ page import="wt.query.ArrayExpression" %>
<%@ page import="com.glaway.mpm.util.*" %>
<%@ page import="wt.iba.value.IBAHolder" %>
<%@ page import="org.apache.commons.io.IOUtils" %>
<%@ page import="ext.casc.doc.RePdfVersionProcessor" %>
<%@page language="java" pageEncoding="UTF-8"
		contentType="text/html; charset=UTF-8"%>
<%
	String oid = request.getParameter("oid");
	String[] split = oid.split(",");
	long[] oids = new long[split.length];
	for(int i = 0; i < split.length; i++) {
		oids[i] = Long.parseLong(split[i]);
	}
	try {
		TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference("casc.sast.149.TECHNOLOGY_AGREEMENT");
		long typeId = 0;
		if (tdr != null) {
			typeId = tdr.getKey().getBranchId();
		}
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendWhere(new SearchCondition(WTDocument.class, "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeId),new int[]{0});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(new ClassAttribute(WTDocument.class, "containerReference.key.id"), SearchCondition.IN, new ArrayExpression(oids)));
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, "RZ/ZY-JX-10004"), new int[]{0});
		QueryResult qr = PersistenceHelper.manager.find(qs);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			WTDocument document = (WTDocument) qr.nextElement();
			IBAHelper ibaHelper = new IBAHelper((IBAHolder) document.getContainer());
			String xhjh = ibaHelper.getIBAValue("XHJH");
			if(xhjh == null || "".equals(xhjh)){
				continue;
			}
			String number = document.getNumber();
			if(number.indexOf("-JX") > -1){
				number = "RZ/" + xhjh + number.substring(number.lastIndexOf("-JX"));
			}else{
				continue;
			}
			InputStream input = null;
			FileInputStream fis = null;
			PdfStamper stamper = null;
			PdfReader reader = null;
			String tempSub = "";
			Transaction trans = new Transaction();
			try {
				trans.start();
				WTProperties pro = WTProperties.getLocalProperties();
				String tec_temp_dir = pro.getProperty("wt.temp");
				boolean isHasPrintFile = false;
				tempSub = tec_temp_dir + File.separator + java.util.UUID.randomUUID();
				File file = new File(tempSub);
				if (!file.exists()) {
					file.mkdirs();
				}
				String printPdfName = WTPartUtil.downloadPrintPdf(document, tempSub);
				if(!"".equals(printPdfName) && !"null".equals(printPdfName)){
					isHasPrintFile = true;
				}
				String printPdfPath = tempSub + File.separator + printPdfName;
				File printPdfFile = new File(printPdfPath);

				Image image = Image.getInstance(PropertiesUtil.getLocalCodeBase() + File.separator + "reJsxyNumber.png");
				int i = 442;
				int j = 789;
				int e = 200;
				int f = 21;
				image.scaleToFit(e, f);
				image.setAbsolutePosition(i, j);
				BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
				float axisX = 480;
				float axisY = 795;
				float fontSize = 10;
				if(isHasPrintFile){
					input = new FileInputStream(printPdfFile);
					reader = new PdfReader(input);
					stamper = new PdfStamper(reader, new FileOutputStream(printPdfFile));
					int pages = reader.getNumberOfPages();
					for(int i1 = 1; i1 <= pages; i1++) {
						if(i1 == 2){
							image.scaleToFit(180, 20);
							image.setAbsolutePosition(450, 758);
							axisX = 485;
							axisY = 765;
						}
						PdfContentByte under = stamper.getOverContent(i1);
						under.addImage(image);
						under.beginText();
						under.setFontAndSize(bf, fontSize);
						under.showTextAligned(Element.ALIGN_CENTER, number, axisX, axisY, 0);
						under.endText();
						under.closePathStroke();
					}
				}
				if(stamper!=null){
					stamper.close();
				}
				if(reader!=null){
					reader.close();
				}
				if(input!=null){
					input.close();
				}
				trans.commit();
				trans = null;

				if(isHasPrintFile){
					fis = new FileInputStream(printPdfFile);
					byte[] printpdfbytes = IOUtils.toByteArray(fis);
					WTDocumentUtil.uploadPrintAttachForDocument(document, printPdfName, printpdfbytes);
					fis.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (trans != null) {
					trans.rollback();
				}
				if(!"".equals(tempSub)){
					RePdfVersionProcessor.deleteFiles(tempSub);
				}
			}
		}
	} catch(WTException e) {
		throw new RuntimeException(e);
	} catch(RemoteException e) {
		throw new RuntimeException(e);
	}
%>