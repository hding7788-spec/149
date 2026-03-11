<%@ page language="java" session="true" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components"
	prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>

<link href="netmarkets/jsp/ext/numbergen/css/nmstyles.css" rel="stylesheet" type="text/css">

<%@ include file="/netmarkets/jsp/util/begin.jspf" %>

<script type="text/javascript">
	function selectTechnic(){
		PTC.jca.table.Utils.reload("technicActivityResult", {}, true);		
	}
</script>
	<table>
		<tr>
			<td>
				筛选：<select id="selecteid" name="selecteid" onchange="selectTechnic();">
					<option value="ALL">--筛选条件--</option>
					<option value="DISPATCH">已派工</option>
					<option value="UNDISPATCH">未派工</option>
				</select>
			</td>
		</tr>
		<tr>
			<td>
				<jca:renderPropertyPanel>
					<w:textBox propertyLabel="备注" id="comments" name="comments"/>
				</jca:renderPropertyPanel>
			</td>
			<td>
				<jca:renderPropertyPanel>
					<w:checkBox name="change" id="change" label="不更改" renderLabel="true" renderLabelOnRight="true" checked="false"/>
				</jca:renderPropertyPanel>
			</td>
		</tr>
	</table>
<div>
	<jsp:include page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.task.TechnicTaskActivityBuilder')}" flush="true" ></jsp:include>
</div>
		
<%@ include file="/netmarkets/jsp/util/end.jspf" %>