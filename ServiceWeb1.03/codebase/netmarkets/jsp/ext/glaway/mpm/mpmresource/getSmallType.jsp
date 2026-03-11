<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.ptc.core.meta.common.TypeIdentifier"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.glaway.mpm.util.TypeUtil"%>
<%@page import="wt.type.TypedUtility"%>
<%@page import="java.util.Locale"%>
<%
	String typeName=request.getParameter("typeName");
    List<TypeIdentifier> list =null;
    Map<String,String> map=new HashMap<String,String>();
    if(!"".equals(typeName)){
        list = TypeUtil.getChildTypes(typeName); 
	    if(list!=null&&list.size()!=0){
	        for(TypeIdentifier identifier : list){
	               map.put(identifier.getTypename(),TypedUtility.getLocalizedTypeName(identifier,Locale.CHINA));
	        }
	    }
	}
	out.print(map);
%>






