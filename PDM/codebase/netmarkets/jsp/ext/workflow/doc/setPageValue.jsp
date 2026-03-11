<%@page import="ext.casc.workflow.WorkflowHelper"%>
<%@page import="ext.casc.ixb.ReleaseDataAdvisBackHelper"%>
<%@page import="ext.casc.part.SignatureHelper"%>
<%
    String oid = request.getParameter("oid");
	String isbohui = request.getParameter("isbohui");
	String datas = request.getParameter("value");
	String signCommentValues = request.getParameter("");
	String value1 = java.net.URLDecoder.decode(datas,"UTF-8");

	if(signCommentValues!=null&&!"".equals(signCommentValues)){
		String value2 = java.net.URLDecoder.decode(signCommentValues,"UTF-8");
		if(!"".equals(value2)){
			SignatureHelper.setSignature(oid,value2);
		}
	}

	System.out.println("-------->>>>>oid:"+oid);
	System.out.println("------->>>>>value1:"+value1);
	String departValue  = value1;
	if(value1.contains(";;;;")){
		String[] values = value1.split(";;;;");
		departValue = values[0];
		if(values.length>1){
			String outSignInfo = values[1];
			if(!"".equals(outSignInfo)){
				ReleaseDataAdvisBackHelper.setSelSignValue2(oid,outSignInfo);
			}
		}
	}
	if(!"".equals(departValue)){
		ReleaseDataAdvisBackHelper.setSelDepartValue2(oid,departValue);
	}

	if("true".equals(isbohui)){
		WorkflowHelper.bohuiWorkItem(oid);
	}


%>