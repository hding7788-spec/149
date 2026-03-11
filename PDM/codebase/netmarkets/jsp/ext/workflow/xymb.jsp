<%@page import="ext.casc.constants.Constants" %>
<%@page import="ext.casc.workflow.CwbmDBController" %>
<%@ page language="java" import="wt.httpgw.URLFactory" pageEncoding="utf-8" %>
<%@page import="java.util.Map" %>
<%
    String dizhi = request.getParameter("dizhi");
/* List<Map<String, Object>> list;
 list=CwbmDBController.selectTemplate(dizhi);
Vector vectorList=new Vector();
List list2=new ArrayList();
if(list!=null){
    for(int i=0;i<list.size();i++){
        Map map=(Map) list.get(i);
        list2.add(map);
      Object templateName= map.get("TEMPLATE_NAME");
      if(!vectorList.contains(templateName)){
       vectorList.add(templateName);
      }
    }
} */
    Map<String, String> map;
    map = CwbmDBController.selectTemplate(dizhi);


    URLFactory fac = new URLFactory();
    String herf = fac.getBaseHREF();

%>
<form id="form" method="post" action="<%=herf%>app/netmarkets/jsp/ext/workflow/saveToTeam.jsp">
    <table style='height:150px;width:350px;text-align:center'>
        <tr>
            <td><%=Constants.MUBANMINGCHENG%>
            </td>
            <td><select id="name" onchange=change1() style="width:250px;">
                <option></option>
                <%for (String s : map.keySet()) { %>
                <option value="<%=map.get(s)%>"><%=s.substring(s.indexOf("@_@") + 3)%>
                </option>
                <%} %>
            </select></td>
        </tr>
    </table>
    <tr>
        <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
        <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
        <td>
            <input id="ok" type="button" name="ok" onclick="sure()" value="<%=Constants.JSP_DISPLAY_OK%>"></td>
        <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
        <td><input id="cancel" type="button" name="cancel" value="<%=Constants.JSP_DISPLAY_CANCEL%>" onclick="can()">
        </td>
    </tr>
    <input type="hidden" id="nameValue" name="nameValue" value="">
    <input type="hidden" id="dizhi" name="dizhi" value="<%=dizhi%>">

</form>
<script type="text/javascript">
    function change1() {
        <%-- var iWidth=300; //弹出窗口的宽度;
        var iHeight=100; //弹出窗口的高度;
        var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
        var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
        var obj = document.getElementById("name");
        var txt = obj.options[obj.selectedIndex].text;
        window.open("<%=herf%>app/netmarkets/jsp/ext/workflow/findbm.jsp?txt="+txt+"","","height="+iHeight+", width="+iWidth+", top="+iTop+", left="+iLeft); --%>
        var obj = document.getElementById("name");
        var txt = obj.options[obj.selectedIndex].value;
        document.getElementById("nameValue").value = txt;
    }

    function sure() {
        //document.getElementById("form").submit();
        debugger;
        var uservalue = document.getElementById("nameValue").value;
        var roleUsers = uservalue.split('---');
        for (var i = 0; i < roleUsers.length; i++) {
            var users = roleUsers[i].split('_');
            var role = users[0];
            var user = users[1];
            var fullName = users[2];

            if (role == "xiaodui") {
                role = "校对者";
            } else if (role == "pizhunzhe") {
                role = "批准者";
            } else if (role == "dayinzhe") {
                role = "打印者";
            } else if (role == "shenhezhe") {
                role = "审核者";
            } else if (role == "neibuhuiqianzhe") {
                role = "内部会签者";
            } else if (role == "biaoshenzhe") {
                role = "标审者";
            } else if (role == "gongshidiongeyuan") {
                role = "工时定额员";
            } else if (role == "waibuhuiqianzhe") {
                role = "外部会签者";
            }
            if (user != "null" && role) {

                if (fullName.indexOf("&&") >= 0) {
                    var names = fullName.split("&&");
                    var userSp = user.split("&&");
                    for (var j = 0; j < names.length; j++) {
                        if (name[j] != "null" && names[j] != "" ) {
                            window.opener.tryAppendUser(role, userSp[j], names[j]);
                        }
                    }
                } else {
                    window.opener.tryAppendUser(role, user, fullName);
                }
            }


        }
        //window.opener.location.reload();
        window.close();
    }

    function can() {
        window.close();
    }
</script>