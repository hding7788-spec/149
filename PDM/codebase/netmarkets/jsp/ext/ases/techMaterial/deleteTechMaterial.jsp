<%@ page import="wt.part.WTPart" %>
<%@ page import="com.glaway.mpm.util.Util" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="org.apache.poi.hssf.usermodel.HSSFWorkbook" %>
<%@ page import="org.apache.poi.ss.usermodel.Workbook" %>
<%@ page import="org.apache.poi.xssf.usermodel.XSSFWorkbook" %>
<%@ page import="org.apache.poi.ss.usermodel.Sheet" %>
<%@ page import="org.apache.poi.ss.usermodel.Row" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="wt.query.QuerySpec" %>
<%@ page import="ext.ases.techMaterial.TechnicsMaterialEntries" %>
<%@ page import="wt.query.SearchCondition" %>
<%@ page import="wt.fc.QueryResult" %>
<%@ page import="wt.fc.PersistenceHelper" %>
<%@ page import="wt.pds.StatementSpec" %>
<%@page language="java" pageEncoding="UTF-8"
		contentType="text/html; charset=UTF-8"%>
<%
	String fileName = request.getParameter("file");
	String filePath = Util.getCodebasePath() + File.separatorChar + "temp" + File.separatorChar + fileName;
	File file = new File(filePath);
	if(file.exists()){
		int i = 0;
		InputStream is = new FileInputStream(file);
		Workbook workbook = null;
		if (file.getName().endsWith("xls")) {
			workbook = new HSSFWorkbook(is);
		} else if (file.getName().endsWith("xlsx")) {
			workbook = new XSSFWorkbook(is);
		}
		Sheet sheet = workbook.getSheetAt(0);
		int lastRowNum = sheet.getLastRowNum();
		for (int j = 1; j <= lastRowNum; j++) {
			Row row = sheet.getRow(j);
			String number = row.getCell(0).getStringCellValue().trim();
			if(StringUtils.isNotEmpty(number)){
				QuerySpec qs = new QuerySpec(TechnicsMaterialEntries.class);
				SearchCondition sc = new SearchCondition(TechnicsMaterialEntries.class, TechnicsMaterialEntries.NUMBER,
						SearchCondition.EQUAL, number);
				qs.appendWhere(sc, new int[] { 0 });
				QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qs);
				if(qResult.hasMoreElements()){
					TechnicsMaterialEntries entries = (TechnicsMaterialEntries) qResult.nextElement();
					PersistenceHelper.manager.delete(entries);
					i++;
				}
			}else {
				break;
			}
		}
		out.println("共删除" + i + "个工艺物资条目。");
	}else {
		out.println("文件不存在。");
	}
%>