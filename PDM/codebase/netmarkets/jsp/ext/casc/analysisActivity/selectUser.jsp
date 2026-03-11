<%@ page contentType="text/html;charset=utf-8" %>

<%
    String oid = request.getParameter("oid");
    String type = request.getParameter("type");
    String title = request.getParameter("title");
%>

<BODY>
<TABLE border=0 width="100%">
    <TBODY>
    <TR>
        <TD class=tabledatafont noWrap align="center">
            <BR>查找用户：
            <INPUT id=searchUserKey name=searchUserKey size=15 type=text>
            <INPUT onclick=searchUsers() value=&nbsp;查找&nbsp; type=button>
        </TD>
    </TR>
    <TR>
        <TD class=tabledatafont align="center">
            请选择要添加的用户：<BR>
            <SELECT id=users multiple size=18 style="width: 300px" name=null___Users___combobox>
                <OPTION size=18 value="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp</OPTION>
            </SELECT>
        </TD>
    </TR>
    <TR/>
    <TR>
        <td align="center">
            <input type="button" value="确定" onclick="setUsers()">
        </td>
    </TR>
    </TBODY>
</TABLE>
</BODY>
<script type="text/javascript">
    var searchUserKey = document.getElementById("searchUserKey");
    searchUserKey.onkeyup = function (event) {
        var e = event || window.event;
        if (e && e.keyCode == 13) {
            searchUsers();
        }
    };

    function searchUsers() {
        var userName = document.getElementById("searchUserKey").value;
        if (userName == "" && ('<%=type%>' != 'product' || '<%=title%>' != '选择计调员')) {
            alert("请输入用户名，如zhangsan");
            return;
        } else {
            var xmlHttpRequest;
            if (window.XMLHttpRequest) { // Mozilla, Safari,...
                xmlHttpRequest = new XMLHttpRequest();
                if (xmlHttpRequest.overrideMimeType) {
                    xmlHttpRequest.overrideMimeType('text/xml');
                }
            } else if (window.ActiveXObject) { // IE
                try {
                    xmlHttpRequest = new ActiveXObject("Msxml2.XMLHTTP");
                } catch (e) {
                    try {
                        xmlHttpRequest = new ActiveXObject("Microsoft.XMLHTTP");
                    } catch (e) {
                        alert("不支持AJAX!");
                        return;
                    }
                }
            }

            xmlHttpRequest.onreadystatechange = function () {
                if (xmlHttpRequest.readyState == 4) {
                    if (xmlHttpRequest.status == 200) {
                        var userNameList = xmlHttpRequest.responseText.replace(/(^\s*)|(\s*$)/g, "");
                        var userNameArray = userNameList.split("`");
                        if (userNameArray.length < 2) {
                            var userList = document.getElementById("users");
                            userList.options.length = 0;
                            return;
                        }
                        processUsers(userNameArray);
                    } else {
                        alert("远程调用出错!");
                    }
                }
            }
            var url = encodeURI("searchUser.jsp?userName=" + userName + "&type=<%=type%>&title=<%=title%>");
            xmlHttpRequest.open("GET", url, true);
            xmlHttpRequest.setRequestHeader("Content-Type", "text/html;charset=UTF-8");
            xmlHttpRequest.setRequestHeader("If-Modified-Since", "0");
            xmlHttpRequest.send(null);
        }
    }

    function processUsers(userNameArray) {
        var userList = document.getElementById("users");
        userList.options.length = 0;
        for (var i = 0; i < userNameArray.length - 1; i++) {
            var str = userNameArray[i];
            var names = str.split('$');
            userList.options.add(new Option(names[0], names[1]));
        }
        userList.selectedIndex = 0;
    }

    function setUsers() {
        var userList = document.getElementById("users");
        var selectedIndex = userList.selectedIndex;
        var userName = userList.options[selectedIndex].text;
        var userOid = userList.options[selectedIndex].value;
        if ('<%=title%>' == '选择责任人') {
            window.opener.document.getElementById('<%=oid%>_responser_<%=type%>').value = userName;
            window.opener.document.getElementById('<%=oid%>_responser_value_<%=type%>').value = userOid;
            if (window.opener && window.opener.setUserValue) {
                window.opener.setUserValue(userName, userOid, '<%=type%>', '<%=title%>');
            }
        } else if ('<%=title%>' == '选择计调员') {
            window.opener.document.getElementById('<%=oid%>_jidiaoyuan_<%=type%>').value = userName;
            window.opener.document.getElementById('<%=oid%>_jidiaoyuan_value_<%=type%>').value = userOid;
            if (window.opener && window.opener.setUserValue) {
                window.opener.setUserValue(userName, userOid, '<%=type%>', '<%=title%>');
            }
        }
        window.close();
    }

    function init() {
        if ('<%=type%>' == 'product' && '<%=title%>' == '选择计调员') {
            searchUsers();
        }
    }
    init();
</script>