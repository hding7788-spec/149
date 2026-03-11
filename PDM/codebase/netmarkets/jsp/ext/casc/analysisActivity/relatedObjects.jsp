<%@taglib uri="http://java.sun.com/jsp/jstl/core" 				 prefix="c"

%><%@taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"

%><%@taglib uri="http://www.ptc.com/windchill/taglib/fmt"        prefix="fmt"

%><%@ taglib uri="http://www.ptc.com/windchill/taglib/changeWizards" prefix="cwiz"

%><%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"
%>

<jsp:include page="${mvc:getComponentURL('ext.casc.common.mvc.builder.analysis.RelatedAnalysisFileBuilder')}" />

<BR>

<jsp:include page="/netmarkets/jsp/ext/casc/analysisActivity/showAnalysisDetail.jsp" flush="true" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>







