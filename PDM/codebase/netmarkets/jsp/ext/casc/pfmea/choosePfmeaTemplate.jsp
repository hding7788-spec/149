<%@ page import="java.util.Map" %>
<%@ page import="java.util.Iterator" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@taglib prefix="wctags" tagdir="/WEB-INF/tags"%>
<%
    String chromePath = "";
    // 获取环境变量中chrome的位置，若不存在，则使用默认位置
    Map tempMap = System.getenv();
    for(Iterator itr = tempMap.keySet().iterator(); itr.hasNext();){
        String value = (String)tempMap.get((String)itr.next());
        System.out.println("value = " + value);
        if(value.contains("chrome.exe")){
            chromePath = value;
            break;
        }
    }
    System.out.println("chromePath ===" + chromePath);
%>
<jca:wizard title="选择PFMEA模板" buttonList="WizardButtonClose">
    <jca:wizardStep action="choosePfmeaTemplate_step" type="pfmea" label="PFMEA模板选择"/>
</jca:wizard>
<%@include file="/netmarkets/jsp/util/end.jspf"%>
<script type="text/javascript">
    PTC.onReady(myfun);

    function openPfmeaHome() {
        //alert("111");
        debugger;
        /*
        创建ActiveXObject实例，只在IE下有效，才可以创建
        */
        //var objShell= new ActiveXObject("WScript.Shell");
        /*
        命令参数说明
        cmd.exe /c dir 是执行完dir命令后关闭命令窗口。
        cmd.exe /k dir 是执行完dir命令后不关闭命令窗口。
        cmd.exe /c start dir 会打开一个新窗口后执行dir指令，原窗口会关闭。
        cmd.exe /k start dir 会打开一个新窗口后执行dir指令，原窗口不会关闭。
        这里的dir是start chrome www.baidu.com//用谷歌浏览器打开百度
        */
        //objShell.Run("cmd.exe /c start chrome www.baidu.com",0,true);
        //window.open("http://pdm.149.sast.casc");

        // Ext.Ajax.request({
        //     url : "netmarkets/jsp/ext/casc/pfmea/openChrome.jsp" ,
        //     method: 'POST'
        // });
    }
</script>