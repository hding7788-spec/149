<%@page import="wt.part.WTPart"%>
<%@page import="wt.workflow.engine.WfProcess"%>
<%@page import="ext.casc.util.Tools"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.BufferedInputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="ext.casc.release.ReleaseHelper,
	ext.casc.zipfile.ZipFileHelper,
	ext.ases.envelope.ProcessEnvelope,
	ext.ases.changepackaged.ChangePackaged,
	wt.change2.WTChangeOrder2,
	wt.doc.WTDocument,
	com.jspsmart.upload.SmartUpload,
	java.io.File,
	wt.util.WTException,
	wt.fc.ReferenceFactory,
	wt.fc.WTObject,
	java.util.ArrayList,
	java.util.List
"%>
<%
	
	String oid = (String)request.getParameter("oid");	 
	String processOid = (String)request.getParameter("processOid");
	List<String> oidList = new ArrayList<String>();
	ReferenceFactory rf = new ReferenceFactory();
	
	WTPart part = (WTPart)rf.getReference(oid).getObject();
	String fN = part.getName();
	String number = part.getNumber();
	WfProcess process = (WfProcess)rf.getReference(processOid).getObject();
	String printFiles = process.getContext().getValue("printFiles").toString();
	if(printFiles!=null&&!"".equals(printFiles)){
		if(printFiles.contains(";")){
			String [] printFs = printFiles.split(";");
			for(String s :printFs){
				oidList.add(s);
			}
		}else {
			oidList.add(printFiles);
		}
	}
	
	if (oidList.size() > 0){
		String fileNameTemp =  ZipFileHelper.service.zipPrimaryFile(oidList,number+"_"+fN);
		if (fileNameTemp != null){
			/*SmartUpload su = new SmartUpload();
			su.initialize(pageContext);
			su.setContentDisposition(null);
			su.downloadFile(fileNameTemp,"application/zip");
			File f = new File(fileNameTemp);
			f.delete();*/
			File f = new File(fileNameTemp);
			InputStream is = new BufferedInputStream(new FileInputStream(f));
			byte[] buffer = new byte[8192];
			int length = 0;
			OutputStream os = null;
			os = response.getOutputStream();
			fileNameTemp =fileNameTemp.replaceAll(" ","");
			String fileName = new String(Tools.getFileName(fileNameTemp).getBytes("gbk"),"iso-8859-1");
			response.setHeader("Content-Disposition", "attachment;filename="+fileName);
			response.setContentType("application/zip");
			response.setContentLength((int) f.length());
			while ((length = is.read(buffer)) >= 0) {
				os.write(buffer, 0, length);
			}
			f.delete();
			is.close();
			out.clear();
			out=pageContext.pushBody();
			out.print("javascript:close()");
		}	
	}else{
		out.print("<script>window.close()</script>"); 
	}
		
%>
