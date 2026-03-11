<%@page import="ext.casc.workflow.CwbmDBController"%>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<% String txt=request.getParameter("txt");
List<Map<String, Object>> list=CwbmDBController.getTempletaByName(txt);

String xiaodui="";
String shenhezhe="";
String neibuhuiqianzhe= "";
String biaoshenzhe= "";
String pizhunzhe= "";
String dayinzhe= "";
String gongshidiongeyuan= "";
String waibuhuiqianzhe="";
for(int i=0;i<list.size();i++){
  String roleName= (String)list.get(i).get("ROLE_NAME");
  if(roleName.equals("xiaodui")){
      xiaodui=(String)list.get(i).get("USER");
  }else if(roleName.equals("shenhezhe")){
      shenhezhe=(String)list.get(i).get("USER");
  }else if(roleName.equals("neibuhuiqianzhe")){
      neibuhuiqianzhe=(String)list.get(i).get("USER");
  }else if(roleName.equals("biaoshenzhe")){
      biaoshenzhe=(String)list.get(i).get("USER");
  }else if(roleName.equals("pizhunzhe")){
      pizhunzhe=(String)list.get(i).get("USER");
  }else if(roleName.equals("dayinzhe")){
      dayinzhe=(String)list.get(i).get("USER");
  }else if(roleName.equals("gongshidiongeyuan")){
      gongshidiongeyuan=(String)list.get(i).get("USER");
  }else if(roleName.equals("waibuhuiqianzhe")){
      waibuhuiqianzhe=(String)list.get(i).get("USER");
  }

}
%>


<input type="hidden" id="xiaodui" name="xiaodui" value="<%=xiaodui%>">
<input type="hidden" id="shenhezhe" name="shenhezhe" value="<%=shenhezhe%>">
<input type="hidden" id="neibuhuiqianzhe" name="neibuhuiqianzhe" value="<%=neibuhuiqianzhe%>">
<input type="hidden" id="biaoshenzhe" name="biaoshenzhe" value="<%=biaoshenzhe%>">
<input type="hidden" id="pizhunzhe" name="pizhunzhe" value="<%=pizhunzhe%>">
<input type="hidden" id="dayinzhe" name="dayinzhe" value="<%=dayinzhe%>">
<input type="hidden" id="gongshidiongeyuan" name="gongshidiongeyuan" value="<%=gongshidiongeyuan%>">
<input type="hidden" id="waibuhuiqianzhe" name="waibuhuiqianzhe" value="<%=waibuhuiqianzhe%>">
<script type="text/javascript">
window.onload=function close(){
	window.opener.document.getElementById("xiaodui").value=document.getElementById("xiaodui").value;
	window.opener.document.getElementById("shenhezhe").value=document.getElementById("shenhezhe").value;
	window.opener.document.getElementById("neibuhuiqianzhe").value=document.getElementById("neibuhuiqianzhe").value;
	window.opener.document.getElementById("biaoshenzhe").value=document.getElementById("biaoshenzhe").value;
	window.opener.document.getElementById("pizhunzhe").value=document.getElementById("pizhunzhe").value;
	window.opener.document.getElementById("dayinzhe").value=document.getElementById("dayinzhe").value;
	window.opener.document.getElementById("gongshidiongeyuan").value=document.getElementById("gongshidiongeyuan").value;
	window.opener.document.getElementById("waibuhuiqianzhe").value=document.getElementById("waibuhuiqianzhe").value;
	window.close();
};
</script>


