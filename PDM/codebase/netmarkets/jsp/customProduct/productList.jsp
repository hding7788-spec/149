<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>

<script type="text/javascript">
Ext.override(Ext.ux.grid.BufferView,{cacheSize:1000});
</script>

<jsp:include page="${mvc:getComponentURL('ext.casc.product.mvc.builder.ProductListBuilder')}" />



<%@ include file="/netmarkets/jsp/util/end.jspf"%>