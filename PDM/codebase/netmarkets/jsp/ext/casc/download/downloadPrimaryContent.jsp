<%@page import="java.util.List"%>
<%@page import="wt.epm.EPMFamily"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.BufferedInputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="ext.casc.util.Tools"%>
<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean,com.ptc.netmarkets.model.NmOid"%>
<%@page import="ext.casc.zipfile.ZipFileHelper, com.jspsmart.upload.SmartUpload, java.io.File"%>

<jsp:useBean id="cnmcontext" class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request">
 <jsp:setProperty name="nmcontext" property="portlet" param="portlet" />
</jsp:useBean>
<jsp:useBean id="curlFactoryBean" class="com.ptc.netmarkets.util.beans.NmURLFactoryBean" scope="request" />
<jsp:useBean id="csessionBean" class="com.ptc.netmarkets.util.beans.NmSessionBean" scope="session" />

<%
	NmCommandBean cb = new NmCommandBean();
	cb.setRequest(request);
	cb.setCompContext(cnmcontext.getContext().toString());
	cb.setUrlFactoryBean(curlFactoryBean);
	cb.setSessionBean(csessionBean);
	cb.setOut(out);

	NmOid nmOid = cb.getActionOid();
    Object object = nmOid.getRefObject();
    if(object != null){
        if(object instanceof EPMDocument){
            EPMDocument epmDocument = (EPMDocument)object;
			System.out.println(epmDocument.isGeneric());
			System.out.println(epmDocument.isInstance());
            if (epmDocument.isGeneric()||epmDocument.isInstance()) {
				EPMFamily family = EPMFamily.getEPMFamily(epmDocument);
				if (family != null) {
					List members = family.getFamilyMembers();
					for (int i = 0; i < members.size(); i++) {
						EPMDocument member = (EPMDocument)members.get(i);
						System.out.println(member.getCADName());

						String number = epmDocument.getNumber();
			            String fileNameTemp =  ZipFileHelper.service.downloadPrimaryContent(member,member.getNumber());
						System.out.println(fileNameTemp);

			        	if (fileNameTemp != null&&!"".equals(fileNameTemp)){
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
					}
				}
			}

        }
    }

%>
