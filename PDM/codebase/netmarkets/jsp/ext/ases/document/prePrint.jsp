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
	java.util.List,
	ext.casc.preview.Preview
"%>
<div>Please waiting ...</div>
<%
	String oid = (String)request.getParameter("oid");
	List<String> oidList = new ArrayList<String>();
	oidList = ReleaseHelper.getPrintApplyRelatedDoc(oid);
	ReferenceFactory rf = new ReferenceFactory();
	String fN = null;
	String number = null;
	String fileNameTemp = "";
	WTObject obj = (WTObject)rf.getReference(oid).getObject();
	//if(obj instanceof WTDocument){
		//WTDocument document = (WTDocument)obj;
		String processOid = (String)request.getParameter("processOid");
		wt.workflow.engine.WfProcess process = (wt.workflow.engine.WfProcess)rf.getReference(processOid).getObject();
		obj = (WTObject)(process.getContext().getValue("primaryBusinessObject"));
		String returnMsg= "";
		if(obj instanceof wt.doc.WTDocument){
			returnMsg = ext.casc.workflow.WorkflowHelper.checkPublishFile(obj);
		}else if(obj instanceof wt.change2.WTChangeOrder2){
			//ext.casc.workflow.WorkflowHelper.publishPdf(obj);
			//java.lang.Thread thread = new java.lang.Thread();
			//thread.sleep(1000*30);
			//returnMsg = ext.casc.workflow.WorkflowHelper.setPDFToECN(obj);
			//if(returnMsg!= null && !"".equals(returnMsg)){
				//thread.sleep(1000*30);
				//returnMsg = ext.casc.workflow.WorkflowHelper.setPDFToECN(obj);
			//}
		}
		if(returnMsg!= null && !"".equals(returnMsg)){
			%>
			<script>
			alert('<%=returnMsg%>');
			window.close();
			</script>
			<%
			return;
		 //throw new wt.util.WTException(returnMsg);
		}
		ext.casc.fileprint.FilePrintUtil.writePreReviewtoPDF2(obj, rf.getReference(processOid).getObject());
		fileNameTemp = ext.casc.fileprint.FilePrintUtil.downloadAttachmentForPrint(obj);
	//}
	System.out.println("fileNameTemp=="+fileNameTemp);
	if (fileNameTemp != null && !"".equals(fileNameTemp)) {
		if (fileNameTemp != null) {
			/*SmartUpload su = new SmartUpload();
			su.initialize(pageContext);
			su.setContentDisposition(null);
			su.downloadFile(fileNameTemp,"application/zip");
			File f = new File(fileNameTemp);
			f.delete();*/
			File f = new File(fileNameTemp);
			InputStream is = new BufferedInputStream(
					new FileInputStream(f));
			byte[] buffer = new byte[8192];
			int length = 0;
			OutputStream os = null;
			os = response.getOutputStream();
			fileNameTemp = fileNameTemp.replaceAll(" ", "");
			String fileName = new String(Tools
					.getFileName(fileNameTemp).getBytes("gbk"),
					"iso-8859-1");
			response.setHeader("Content-Disposition",
					"attachment;filename=" + fileName);
			response.setContentType("application/zip");
			response.setContentLength((int) f.length());
			while ((length = is.read(buffer)) >= 0) {
				os.write(buffer, 0, length);
			}
			f.delete();
			is.close();
			out.clear();
			out = pageContext.pushBody();
			out.print("<script>window.close()</script>");
		}
	} else {
		out.print("<script>window.close()</script>");
	}
%>
