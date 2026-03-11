<%@page import="ext.casc.workflow.CwbmDBController"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.xyz.platform.drivers.xpdfj.PDFNormalizer.outputppXMLCell"%>
<%
/* String dizhi= request.getParameter("dizhi");
String xiaodui= request.getParameter("xiaodui");
String shenhezhe= request.getParameter("shenhezhe");
String neibuhuiqianzhe= request.getParameter("neibuhuiqianzhe");
String biaoshenzhe= request.getParameter("biaoshenzhe");
String pizhunzhe= request.getParameter("pizhunzhe");
String dayinzhe= request.getParameter("dayinzhe");
String gongshidiongeyuan= request.getParameter("gongshidiongeyuan");
String waibuhuiqianzhe= request.getParameter("waibuhuiqianzhe");
Map<String,String> map=new HashMap<String,String>();
map.put("xiaodui", xiaodui);
map.put("shenhezhe", shenhezhe);
map.put("neibuhuiqianzhe", neibuhuiqianzhe);
map.put("biaoshenzhe", biaoshenzhe);
map.put("pizhunzhe", pizhunzhe);
map.put("dayinzhe", dayinzhe);
map.put("gongshidiongeyuan", gongshidiongeyuan);
map.put("waibuhuiqianzhe", waibuhuiqianzhe);
CwbmDBController.setValuesIntoTeam(dizhi, map);*/
String dizhi= request.getParameter("dizhi");
Map<String,String> map=new HashMap<String,String>();
String nameValue= request.getParameter("nameValue");
String[] value= nameValue.split("---");
for(int i=0;i<value.length;i++){
    String[] canyuzhe=value[i].split("_");
    if(canyuzhe.length==1){
        map.put(canyuzhe[0], "");
    }else{
        map.put(canyuzhe[0], canyuzhe[1]);
    }
}

CwbmDBController.setValuesIntoTeam(dizhi, map);
%>
<head>
<script type="text/javascript">
window.onload=function(){
	window.close();
};
</script>
</head>