<%@ page import="java.util.*"%>
<%@page import="ext.casc.workflow.TaskConfigrationHelper"%>
<%@ page import="com.ptc.netmarkets.util.table.NmHTMLTable,com.ptc.netmarkets.util.table.NmDefaultHTMLTable,com.ptc.netmarkets.util.misc.NmTableRenderer"%>

<%@ include file="/netmarkets/jsp/util/beginPopup.jspf"%>
<%
	Locale locale = WTContext.getContext().getLocale();
	String contextPath = request.getContextPath();
	String NAME = WTMessage.getLocalizedMessage("ext.ases.workflow.tree.resource.asesSignatureResource","NAME",null,locale);
	String SIGN_ADVISE = WTMessage.getLocalizedMessage("ext.ases.workflow.tree.resource.asesSignatureResource","SIGN_ADVISE",null,locale);
	String DATE = WTMessage.getLocalizedMessage("ext.ases.workflow.tree.resource.asesSignatureResource","DATE",null,locale);
	String COMPANY = WTMessage.getLocalizedMessage("ext.ases.workflow.tree.resource.asesSignatureResource","COMPANY",null,locale);
	String OKBUTTON = WTMessage.getLocalizedMessage("ext.ases.workflow.tree.resource.asesSignatureResource","OKBUTTON",null,locale);
	String NEWBUTTON = WTMessage.getLocalizedMessage("ext.ases.workflow.tree.resource.asesSignatureResource","NEWBUTTON",null,locale);
	String rili = new String("日历".getBytes("iso-8859-1"),"utf-8");
	String week = new String("一月#二月#三月#四月#五月#六月#七月#八月#九月#十月#十一月#十二月#".getBytes("iso-8859-1"),"utf-8");
	String month = new String("星期日#星期一#星期二#星期三#星期四#星期五#星期六#".getBytes("iso-8859-1"),"utf-8");
	String oid = request.getParameter("oid").trim();
%>
<script>
	function newline()
	{
	var table = document.getElementById("inputTable");
	var cp = document.getElementById("count");
	var node = table.childNodes[1];
	var tr = document.createElement("tr");

	var td = document.createElement("td");
	td.align="left";
	var input = document.createElement("input");
	input.name="name"+cp.value;
	input.size = 10;
	input.id=input.name;
	td.appendChild(input);
	tr.appendChild(td);

	td = document.createElement("td");
	td.align="left";
	input = document.createElement("input");
	input.name="company"+cp.value;
	input.id=input.name;
	td.appendChild(input);
	tr.appendChild(td);

	td = document.createElement("td");
	td.align="left";
	var inputd = document.createElement("input");
	inputd.name="date"+cp.value;
	inputd.size='8';
	inputd.id=inputd.name;
	td.appendChild(inputd);
	var but = document.createElement("input");
	but.type="button";
	but.value='...';
	if(window.attachEvent){
		but.attachEvent( "onclick",function(){calendar(inputd);});
	}
	td.appendChild(but);
	tr.appendChild(td);

	td = document.createElement("td");
	td.align="left";
	input = document.createElement("input");
	input.name="message"+cp.value;
	input.id=input.name;
	td.appendChild(input);
	tr.appendChild(td);

	node.appendChild(tr);
	cp.value = parseInt(cp.value)+1;
	}
	
	function getMessage()
	{
	var c = document.getElementById("count").value;
	var name = new Array;
	var kk = new Array;
	var mesg = new Array;
	var sb = new Array;
	for(var i=1;i<parseInt(c);i++)
	{
	sb[0]="name";
	sb[1]=i;
	name.push(document.getElementById(sb.join("")).value);
	sb[0]="company";
	name.push(document.getElementById(sb.join("")).value);
	sb[0]="date";
	name.push(document.getElementById(sb.join("")).value);
	kk.push(name.join("/"));
	name.clear();
	sb[0]="message";
	mesg.push(document.getElementById(sb.join("")).value);
	}
	opener.document.getElementById("<%=oid%>_advise").value=mesg.join(";");
	opener.document.getElementById("<%=oid%>_message").value=kk.join(";");
	window.close();
	}
  /*
	function calendar(loc)
	{
		initCal('<%=rili%>', '<BASE HREF=null><LINK REL=stylesheet HREF=<%=contextPath%>/netmarkets/css/nmstyles.css TYPE=text/css>', '<%=contextPath%>/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1931);
		setDateField(loc, 'yyyy-MM-dd', '<%=week%>', '#', '<%=month%>', '0');
		newCalendarWindow(event, '<%=contextPath%>/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240');
	}*/
	
	function calendar(loc)
	{
		initCal('<%=rili%>', '<LINK REL=stylesheet HREF=<%=contextPath%>/netmarkets/css/nmstyles.css TYPE=text/css>', '<%=contextPath%>/wtcore/js/com/ptc/core/ca/web/misc/trlUtils.js', 3, 2, 1, 'null', 'null', false, 100,2010);
		setDateField(loc, 'yyyy-MM-dd', '<%=week%>', '#', '<%=month%>', '0');
		newCalendarWindow(event, '<%=contextPath%>/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240');
	}


</script>
<table id='inputTable'> 
	<thead>
		<tr class="tableHeaderRow">
			<th align='left' class="tablecolumnheaderbg pointer" size="10"><span class="tablecolumnheaderfont"><%=NAME%></span></th>
			<th align='left' class="tablecolumnheaderbg pointer"><span class="tablecolumnheaderfont"><%=COMPANY%></span></th>
			<th align='left' class="tablecolumnheaderbg pointer"><span class="tablecolumnheaderfont"><%=DATE%></span></th>
			<th align='left' class="tablecolumnheaderbg pointer"><span class="tablecolumnheaderfont"><%=SIGN_ADVISE%></span></th>
		</tr>
</thead>
<tbody>
	</tbody>
</table>
<input type='button' name='button1' id='button1' value="<%=NEWBUTTON%>" onclick='newline()'>
<input type='hidden' id='count' value='1'>
<input type='button' value=<%=OKBUTTON%> onclick="getMessage()">

<script>
   var advise = opener.document.getElementById("<%=oid%>_advise").value;
   var message = opener.document.getElementById("<%=oid%>_message").value;
   newline();
	var c = 1;
	if (message != null && message !=""){
		var ads = message.split(";");
		var mes = advise.split(";");
		for(var i=0;i<ads.length;i++){
			var al = ads[i].split("/");
			document.getElementById("name"+c).value=al[0];	
			document.getElementById("company"+c).value=al[1];	
			document.getElementById("date"+c).value=al[2];	
			document.getElementById("message"+c).value=mes[i];
			c++;
			if (i<ads.length-1)
				newline();
		}
	}

</script>
<%--
   //CONTENT_AREA comment marks the begin and end of the content area for DHTML content switching
   //  (for example, switching the 3rd level nav content without refreshing the page)
--%><!--CONTENT_AREA--><%
      }
    }
    catch (Throwable t) {
		%><tags:renderError throwable="<%=t%>" pageModel="${jcaPageModel}"/><%
    }
    finally {
    	
    }
}%><!-- negate onunload cancel request, page load completed -->
<script type="text/javascript">
   pageLoadComplete=true;

</script>
