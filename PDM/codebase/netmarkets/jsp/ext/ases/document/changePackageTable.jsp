<%@taglib uri="http://java.sun.com/jsp/jstl/core" 				 prefix="c"
%><%@taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"
%><%@taglib uri="http://www.ptc.com/windchill/taglib/fmt"        prefix="fmt"
%><%@ taglib uri="http://www.ptc.com/windchill/taglib/changeWizards" prefix="cwiz"
%><%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%><%@ include file="/netmarkets/jsp/util/begin.jspf"
%>
<jsp:include page="${mvc:getComponentURL('ext.casc.workflow.mvc.builder.ChangePackageTable')}" />
<%@ include file="/netmarkets/jsp/util/end.jspf"%>



