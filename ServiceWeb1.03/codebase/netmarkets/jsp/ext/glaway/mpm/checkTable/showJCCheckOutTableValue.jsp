<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="wt.vc.VersionControlHelper"%>
<%@page import="wt.workflow.work.WorkItem"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="wt.org.WTPrincipalReference"%>
<%@page import="wt.preference.PreferenceHelper"%>
<%@page import="wt.httpgw.LanguagePreference"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="wt.inf.container.WTContainer"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.util.*"%>
<%@page import="java.util.*"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.glaway.mpm.print.util.PrintUtil"%>
<%@page import="java.io.File"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="com.glaway.mpm.util.SWXMLUtil"%>
<%@page import="org.dom4j.Element"%>
<%@page import="org.dom4j.Document"%>
<%@page import="org.dom4j.Node"%>
<%@page import="org.dom4j.io.SAXReader"%>
<%@page import="com.glaway.mpm.processplan.checkouttable.*"%>
<%@page import="com.glaway.mpm.processplan.checkouttable.checkbean.*"%>

<%
	String technicsNumber = request.getParameter("technicsNumber");
	if(technicsNumber.contains(" ")){
		technicsNumber = technicsNumber.substring(0,technicsNumber.indexOf(" "));
	}
// 	CheckOutUtil.getTechincsXMLPath(technicsNumber);
	String tableName = request.getParameter("tableName");
    String xmlPath = CheckOutUtil.getXmlPath(technicsNumber);
    System.out.println("------------------------------------------xmlPath---------------------------------"+xmlPath);
    String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	String technicsUrl = basePath+"temp"+"/"+"checkouttable"+"/"+technicsNumber;
	System.out.println("------------------------------------------technicsUrl---------------------------------"+technicsUrl);
    List<ProjectNameBean> projectNameBeanList = CheckOutUtil.getXMLCheckOutTbaleList(xmlPath,tableName,"检测类",technicsUrl);
//     CheckOutUtil.getXMLCheckOutTbaleList(xmlPath);
//     SAXReader sax = new SAXReader();
//     Document document = sax.read(new File(xmlPath));
//     Element root = document.getRootElement();
%>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>查看检验汇总表</title>
		<style type="text/css">
			body{
				margin: 0px;
				width: 2500px;
				min-width: 100px;
				max-width: 2500px;
				height: 100%;
			}
			.bttr{
				color:#2222fF;
				background-color: #cFcFcF;
			}
			.bttd{
				background-color: #ccdFf1;
			}
		</style>
    </head>

    <body>
	    <div id = "checkResource" style="margin-top:10px;overflow:hidden">
	    	<h2 style="color: #ff0000;">检测类表单</h2>
	    	<hr/>
		    <div>
		    	<h3>套表名：<%=tableName %></h3>
				<table id = "table1_content" border="1"  width="2500px" margin-top: 20px">
					<tr class="bttr">
               			<th width="4%">项目名</th>
               			<th width="4%">工序号</th>
               			<th width="4%">工步号</th>
               			<th width="6%">单元表表名</th>
               			<th width="4%">检测项</th>
               			<th width="16%">公称值</th>
               			<th width="5%">上偏差</th>
               			<th width="5%">下偏差</th>
               			<th width="16%">实测值</th>
               			<th width="8%">签署信息</th>
               			<th width="16%">检验方法描述</th>
               			<th width="16%">检验内容描述</th>
               		</tr>
               		<%
	               		for(int i=0;i<projectNameBeanList.size();i++){
	           				ProjectNameBean projectNameBean = projectNameBeanList.get(i);
	           				String projectName = projectNameBean.getProjectName();
	           				List<StepNOBean> stepNOBeanList = projectNameBean.getStepNOBeanlist();
	           				int projectTableUnitCount = projectNameBean.getTableUnitCount();
	           				for(int j=0;j<stepNOBeanList.size();j++){
	           					StepNOBean stepNOBean = stepNOBeanList.get(j);
	           					String stepNo = stepNOBean.getStepNo();
	           					List<PaceNoBean> paceNoBeanList =  stepNOBean.getPaceNoBeanlist();
	           					int stepNoTableUnitCount = stepNOBean.getTableUnitCount();
	           					for(int k=0;k<paceNoBeanList.size();k++){
	           						PaceNoBean paceNoBean = paceNoBeanList.get(k);
	           						String peceNo = paceNoBean.getPeceNo();
	           						List<CheckOutTableListBean> checkOutTableListBeanlist = paceNoBean.getCheckOutTableListBeanlist();
	           						int peceNoTableUnitCount = paceNoBean.getTableUnitCount();
	           						for(int g=0;g<checkOutTableListBeanlist.size();g++){
	           							CheckOutTableListBean checkOutTableListBean = checkOutTableListBeanlist.get(g);
	           							String unitName = checkOutTableListBean.getUnitName();
	           							List<CheckOutTableBean> checkOutTableBeanList = checkOutTableListBean.getCheckOutTableBeanList();
	           							int checkOutTableCount = checkOutTableListBean.getTableUnitCount();
               							for(int h=0;h<checkOutTableBeanList.size();h++){
               								CheckOutTableBean checkOutTableBean = checkOutTableBeanList.get(h);
               								String jlx =  checkOutTableBean.getJlxValue();
               								String yqz =  checkOutTableBean.getYqzValue();
               								String spc =  checkOutTableBean.getSpcValue();
               								String xpc =  checkOutTableBean.getXpcValue();

               								String scz =  checkOutTableBean.getSczValue();
               								String cmc =  checkOutTableBean.getCheckmethodContent();
               								String cc =  checkOutTableBean.getCheckcontentContent();


               		%>
						               		<tr>
						               			<%
						               				if(j==0&&k==0&&g==0&&h==0){
						               			%>
						               			<td class="bttr" rowspan="<%=projectTableUnitCount%>"><%=projectName %></td>
						               			<%
						               				}
						               			%>
						               			<%
						               				if(k==0&&g==0&&h==0){
						               			%>
						               			<td class="bttr" rowspan="<%=stepNoTableUnitCount%>"><%=stepNo %></td>
						               			<%
						               				}
						               			%>
						               			<%
						               				if(g==0&&h==0){
						               			%>
						               			<td class="bttr" rowspan="<%=peceNoTableUnitCount%>"><%=peceNo %></td>
						               			<%
						               				}
						               			%>
						               			<%
						               				if(h==0){
						               			%>
						               			<td class="bttd" rowspan="<%=checkOutTableCount%>"><%=unitName %></td>
						               			<%
						               				}
						               			%>
						               			<td class="bttd"><%=jlx %></td>
						               			<td class="bttd"><%=yqz %></td>
						               			<td class="bttd"><%=spc %></td>
						               			<td class="bttd"><%=xpc %></td>
						               			<td class="bttd"><%=scz %></td>

						               			<%
						               				if(g==0&&h==0){
						               			%>
						               			<td class="bttd" rowspan="<%=peceNoTableUnitCount%>"></td>
						               			<%
						               				}
						               			%>
						               			<%
						               				if(g==0&&h==0){
						               			%>
						               			<td class="bttd" rowspan="<%=peceNoTableUnitCount%>"><%=cmc %></td>
						               			<%
						               				}
						               			%>
						               			<%
						               				if(g==0&&h==0){
						               			%>
						               			<td class="bttd" rowspan="<%=peceNoTableUnitCount%>"><%=cc %></td>
						               			<%
						               				}
						               			%>
						               		</tr>
               		<%					}
               						}
               					}
               				}
               		    }
               		%>
               	</table>
		    </div>
    	</div>
    </body>
</html>




