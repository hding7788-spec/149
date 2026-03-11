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
<%
	String oid = (String)request.getParameter("oid");
	List<String> oidList = new ArrayList<String>();
	oidList = ReleaseHelper.getPrintApplyRelatedDoc(oid);
	ReferenceFactory rf = new ReferenceFactory();
	String fN = null;
	String number = null;

	WTObject obj = (WTObject)rf.getReference(oid).getObject();

	String fileNameTemp = "";
	if(obj instanceof Preview){
		Preview preview = (Preview)obj;
		fileNameTemp = ZipFileHelper.service.downloadPrimaryContent(preview,preview.getNumber());

	}
	//System.out.println("fileNameTemp=="+fileNameTemp);
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
			out.print("javascript:close()");
		}
	} else {
		out.print("<script>window.close()</script>");
	}
%>
