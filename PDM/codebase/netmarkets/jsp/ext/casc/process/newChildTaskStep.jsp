<%@page import="wt.util.WTProperties"%><%@ taglib uri="http://www.ptc.com/windchill/taglib/components"  prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt"         prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc"         prefix="mvc"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"               prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers"    prefix="w"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ page import="ext.casc.process.ProcessConstants" %>
<%@ page import="ext.casc.constants.Constants" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Iterator" %>
<%@page language="java" pageEncoding="GBK" contentType="text/html; charset=GBK"%>

<table>
	<tr>
		<td>
			<b><%=ProcessConstants.RENWUMINGCHENG %></b>
		</td>
		<td>
			<input type="text" name="name" id="name"/>

		</td>
	</tr>
	<tr>
		<td>
			<b><%=ProcessConstants.ZHIXINGBUMEN %></b>
		</td>

		<td>
		<select name="zhixingbumen" id="zhixingbumen" style='width:150px'>
				<option value="" selected></options>
			    <%
                    for(String bumen:Constants.allChejian){%>
                      <option value=<%=bumen %>><%=bumen%></options>
                    <%}
			    %>
         </select>
		</td>
	</tr>
	<tr>
		<td>
			<b><%=ProcessConstants.JIHUAWANCHENGSHIJIAN %></b>
		</td>
		<td>
			<label for="endDate"><font style="font-weight: bold;"></font></label>
<input id="endDate" type="text" name="endDate" onBlur="validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" onkeypress="validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" value=""  size=10 maxlength=10/>
<A HREF="javascript:void(0)"  onClick="initCal('\u65e5\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/nmstyles.css TYPE=text/css>', '/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); setDateField(document.getElementsByName('endDate')[0], 'yyyy/MM/dd', '\u4e00\u6708#\u4e8c\u6708#\u4e09\u6708#\u56db\u6708#\u4e94\u6708#\u516d\u6708#\u4e03\u6708#\u516b\u6708#\u4e5d\u6708#\u5341\u6708#\u5341\u4e00\u6708#\u5341\u4e8c\u6708#', '#', '\u661f\u671f\u65e5#\u661f\u671f\u4e00#\u661f\u671f\u4e8c#\u661f\u671f\u4e09#\u661f\u671f\u56db#\u661f\u671f\u4e94#\u661f\u671f\u516d#', '0'); newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')">
<IMG name="calImg" SRC="/Windchill/netmarkets/images/calendar.gif" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd</font>
		</td>
	</tr>
	<tr>
		<td>
			<b>&nbsp;&nbsp;<%=ProcessConstants.RENWUYAOQIU %></b>
		</td>
		<td>
			<input type="text" name="renwuyaoqiu" id="renwuyaoqiu"/>
		</td>
	</tr>
</table>

