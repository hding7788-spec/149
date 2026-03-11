<%@page import="wt.part.WTPart"%>
<%@page import="ext.casc.util.WCUtil"%>
<%@page import="com.ptc.windchill.mpml.resource.MPMProcessMaterial"%>
<%@page import="java.util.Locale"%>
<%@include file="imports.jspf"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set,wt.part.WTPartMaster"%>
<%@page import="com.infoengine.modeler.data.ForEach"%>
<%@page import="com.ptc.netmarkets.workinstructions.WorkInstructionsUtilities"%>
<%@page import="ext.casc.util.IBAUtility,ext.ptc.ViewWIHelper,java.util.List,java.util.Map"%>
<%@page import="com.ptc.windchill.mpml.processplan.operation.MPMOperation"%>
<%@page import="com.ptc.windchill.mpml.processplan.operation.MPMOperationToPartLink" %>
<%@page import="com.ptc.windchill.mpml.processplan.operation.MPMOperationToConsumableLink" %>
<%@page import="com.ptc.windchill.mpml.resource.MPMProcessMaterialMaster" %>
<%@page import="com.ptc.windchill.mpml.processplan.operation.MPMOperationToOperatedPartLink" %>
<%@page import="com.ptc.windchill.mpml.resource.MPMTooling" %>
<%@page import="com.ptc.windchill.mpml.resource.MPMToolingMaster"%>

<style>  
.CMtdPPTableHeader{background-color: #FED293; font-weight: bold; font-size: 8pt; text-align:left; vertical-align: left}
.CMtdPPTableNormal{background-color: #FFFFFF; font-weight: normal; font-size: 8pt; text-align:left; vertical-align: left}
.CMtdPPTableNormal2{background-color: #F0F0F0; font-weight: normal; font-size: 8pt; text-align:left; vertical-align: left}
</style>


<%
String type = "类型";
type = new String(type.getBytes("iso-8859-1"), "GBK");
String partType = "类别";
partType = new String(partType.getBytes("iso-8859-1"), "GBK");
String daihao = "代号";
daihao = new String(daihao.getBytes("iso-8859-1"), "GBK");
String number = "编号";
number = new String(number.getBytes("iso-8859-1"), "GBK");
String name = "名称";
name = new String(name.getBytes("iso-8859-1"), "GBK");
String amount = "数量";
amount = new String(amount.getBytes("iso-8859-1"), "GBK");
String paihao = "牌号";
paihao = new String(paihao.getBytes("iso-8859-1"), "GBK");
String csize = "规格";
csize = new String(csize.getBytes("iso-8859-1"), "GBK");
String jstj = "技术条件";
jstj = new String(jstj.getBytes("iso-8859-1"), "GBK");
String danwei = "计量单位";
danwei = new String(danwei.getBytes("iso-8859-1"), "GBK");

IeService ieObj = (IeService) request.getAttribute("ieObjFromCore");
String opObid4 = ieObj.getAttributeValue("CURRENT_OPERATION", 0, "obid");
String oidOP4 = WorkInstructionsUtilities.getOid(opObid4);
MPMOperation opera4 = (MPMOperation) WorkInstructionsUtilities.getObject(oidOP4);
//获取使用过的部件
List<MPMOperationToPartLink> list = ViewWIHelper.getPartUsesLinkByMPMOPer(opera4);
//获取非操作耗材部件
List<MPMOperationToConsumableLink> materialList = ViewWIHelper.getMaterialByMPMOper(opera4);
//获取资源分配的资源
List<MPMOperationToOperatedPartLink> operatedList = ViewWIHelper.getOperatedPartUsesLinkByMPMOPer(opera4);
%>

<table border=0 width="100%">
	<tr>
		<td class="CMtdPPTableHeader" width="10%" align="left"><%=type %></td>
		<td class="CMtdPPTableHeader" width="10%" align="left"><%=daihao %></td>
		<td class="CMtdPPTableHeader" width="20%" align="left"><%=name %></td>
		<td class="CMtdPPTableHeader" width="10%" align="left"><%=danwei %></td>
		<td class="CMtdPPTableHeader" width="10%" align="left"><%=amount %></td>
		<td class="CMtdPPTableHeader" width="10%" align="left"><%=partType %></td>
	</tr>
	<%
	int count = 0;
	IBAUtility ibaUtility = null;
	for(int i=0;i<list.size();i++){//部件
	    MPMOperationToPartLink link = list.get(i);
        WTPartMaster partMaster = (WTPartMaster)link.getRoleBObject();
        WTPart part = WCUtil.getPartByNumber(partMaster.getNumber());
        ibaUtility = new IBAUtility(part);
        String daihaoValue = ibaUtility.getIBAValue("CINDEX");
        if(daihaoValue==null){
            daihaoValue = "";
	    }
        String partTypeValue = ibaUtility.getIBAValue("CTYPE");
        if(partTypeValue==null){
            partTypeValue = "";
	    }
        String typeValue = new String("部件".getBytes("iso-8859-1"), "GBK");
	    
        count++;
		String col = "CMtdPPTableNormal2";
		if(count%2==0){
		    col = "CMtdPPTableNormal";
		}
	%>
	<tr >
		<td class="<%=col %>" ><%=typeValue %></td>
		<td class="<%=col %>" ><%=daihaoValue %></td>
		<td class="<%=col %>" ><%=partMaster.getName() %></td>
		<td class="<%=col %>" ><%=link.getQuantity().getUnit().getDisplay(Locale.CHINA) %></td>
		<td class="<%=col %>" ><%=link.getQuantity().getAmount() %></td>
		<td class="<%=col %>" ><%=partTypeValue %></td>
	</tr>
	<%}%>
</table>	
<br>
<table border=0 width="100%">
	<tr>
		<td class="CMtdPPTableHeader" width="10%" align="left"><%=type %></td>
		<td class="CMtdPPTableHeader" width="10%" align="left"><%=daihao %></td>
		<td class="CMtdPPTableHeader" width="20%" align="left"><%=name %></td>
		<td class="CMtdPPTableHeader" width="10%" align="left"><%=danwei %></td>
		<td class="CMtdPPTableHeader" width="10%" align="left"><%=amount %></td>
		<td class="CMtdPPTableHeader" width="10%" align="left"><%=partType %></td>
	</tr>
	<%
	int count2 = 0;
	for(int i=0;i<operatedList.size();i++){//非操作耗材部件
	    MPMOperationToOperatedPartLink link = operatedList.get(i);
	    WTPartMaster master = (WTPartMaster)link.getRoleBObject();
	    WTPart part = WCUtil.getPartByNumber(master.getNumber());
        ibaUtility = new IBAUtility(part);
        String daihaoValue = ibaUtility.getIBAValue("CINDEX");
        if(daihaoValue==null){
            daihaoValue = "";
	    }
        String partTypeValue = ibaUtility.getIBAValue("CTYPE");
        if(partTypeValue==null){
            partTypeValue = "";
	    }
	    String typeValue = new String("工装".getBytes("iso-8859-1"), "GBK");
	    
	    count2++;
		String col = "CMtdPPTableNormal2";
		if(count2%2==0){
		    col = "CMtdPPTableNormal";
		}
	%>
	<tr >
		<td class="<%=col %>" ><%=typeValue %></td>
		<td class="<%=col %>" ><%=daihaoValue %></td>
		<td class="<%=col %>" ><%=master.getName() %></td>
		<td class="<%=col %>" ><%=link.getQuantity().getUnit().getDisplay(Locale.CHINA) %></td>
		<td class="<%=col %>" ><%=link.getQuantity().getAmount() %></td>
		<td class="<%=col %>" ><%=partTypeValue %></td>
	</tr>
	<%}%>
</table>	
<br>
<table border=0 width="100%">
	<tr>
		<td class="CMtdPPTableHeader" width="10%" align="left"><%=type %></td>
		<td class="CMtdPPTableHeader" width="20%" align="left"><%=daihao %></td>
		<td class="CMtdPPTableHeader" width="20%" align="left"><%=name %></td>
		<td class="CMtdPPTableHeader" width="10%" align="left"><%=paihao %></td>
		<td class="CMtdPPTableHeader" width="10%" align="left"><%=csize %></td>
		<td class="CMtdPPTableHeader" width="10%" align="left"><%=jstj %></td>
		<td class="CMtdPPTableHeader" width="10%" align="left"><%=danwei %></td>
		<td class="CMtdPPTableHeader" width="10%" align="left"><%=amount %></td>
	</tr>
	<%
	int count3 = 0;
	for(int i=0;i<materialList.size();i++){//使用的资源
	    MPMOperationToConsumableLink link = materialList.get(i);
		Object obj = link.getRoleBObject();
		String typeValue = "";
		String numberValue = "";
		String nameValue = "";
		String paihaoValue = "";
		String csizeValue = "";
		String jstjValue = "";
		String danweiValue = "";
		String amountValue = "";
		
		if(!(obj instanceof MPMProcessMaterialMaster)){
		    continue;
		}
		MPMProcessMaterialMaster mmaster = (MPMProcessMaterialMaster)obj;
	    MPMProcessMaterial material = WCUtil.getMpmProcessMaterialByNumber(mmaster.getNumber());
	    ibaUtility = new IBAUtility(material);
	    typeValue = new String("辅助材料".getBytes("iso-8859-1"), "GBK");
	    numberValue = mmaster.getNumber();
	    nameValue = mmaster.getName();
	    paihaoValue = ibaUtility.getIBAValue("PAIHAO");
	    if(paihaoValue==null){
	        paihaoValue = "";
	    }
	    csizeValue = ibaUtility.getIBAValue("CSIZE");
	    if(csizeValue==null){
	        csizeValue = "";
	    }
	    jstjValue = ibaUtility.getIBAValue("JSTJ");
	    if(jstjValue==null){
	        jstjValue = "";
	    }
	    danweiValue = link.getQuantity().getUnit().getDisplay(Locale.CHINA);
	    amountValue = String.valueOf(link.getQuantity().getAmount());
	    
	    count3++;
		String col = "CMtdPPTableNormal2";
		if(count3%2==0){
		    col = "CMtdPPTableNormal";
		}
	%>
	<tr >
		<td class="<%=col %>" ><%=typeValue %></td>
		<td class="<%=col %>" ><%=numberValue %></td>
		<td class="<%=col %>" ><%=nameValue %></td>
		<td class="<%=col %>" ><%=paihaoValue %></td>
		<td class="<%=col %>" ><%=csizeValue %></td>
		<td class="<%=col %>" ><%=jstjValue %></td>
		<td class="<%=col %>" ><%=danweiValue %></td>
		<td class="<%=col %>" ><%=amountValue %></td>
	</tr>
	<%}%>
</table>
<br>
<table border=0 width="100%">
	<tr>
		<td class="CMtdPPTableHeader" width="10%" align="left"><%=type %></td>
		<td class="CMtdPPTableHeader" width="20%" align="left"><%=number %></td>
		<td class="CMtdPPTableHeader" width="20%" align="left"><%=name %></td>
		<td class="CMtdPPTableHeader" width="10%" align="left"><%=csize %></td>
		<td class="CMtdPPTableHeader" width="10%" align="left"><%=amount %></td>
	</tr>
	<%
	int count4 = 0;
	for(int i=0;i<materialList.size();i++){//使用的资源
	    MPMOperationToConsumableLink link = materialList.get(i);
		Object obj = link.getRoleBObject();
		String typeValue = "";
		String numberValue = "";
		String nameValue = "";
		String paihaoValue = "";
		String csizeValue = "";
		String jstjValue = "";
		String danweiValue = "";
		String amountValue = "";
		
		if(!(obj instanceof MPMToolingMaster)){
		    continue;
		}
		MPMToolingMaster tMaster = (MPMToolingMaster)obj;
	    MPMTooling tooling = WCUtil.getMpmMPMToolingByNumber(tMaster.getNumber());
	    ibaUtility = new IBAUtility(tooling);
	    typeValue = new String("标准刀量具".getBytes("iso-8859-1"), "GBK");
	    numberValue = tMaster.getNumber();
	    nameValue = tMaster.getName();
	    paihaoValue = ibaUtility.getIBAValue("PAIHAO");
	    if(paihaoValue==null){
	        paihaoValue = "";
	    }
	    csizeValue = ibaUtility.getIBAValue("CSIZE");
	    if(csizeValue==null){
	        csizeValue = "";
	    }
	    jstjValue = ibaUtility.getIBAValue("JSTJ");
	    if(jstjValue==null){
	        jstjValue = "";
	    }
	    danweiValue = link.getQuantity().getUnit().getDisplay(Locale.CHINA);
	    amountValue = String.valueOf(link.getQuantity().getAmount());
	    
	    count4++;
		String col = "CMtdPPTableNormal2";
		if(count4%2==0){
		    col = "CMtdPPTableNormal";
		}
	%>
	<tr >
		<td class="<%=col %>" ><%=typeValue %></td>
		<td class="<%=col %>" ><%=numberValue %></td>
		<td class="<%=col %>" ><%=nameValue %></td>
		<td class="<%=col %>" ><%=csizeValue %></td>
		<td class="<%=col %>" ><%=amountValue %></td>
	</tr>
	<%}%>
</table>