<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %><%


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
    System.out.println("chromePath : "+chromePath);
    try{
        Runtime.getRuntime().exec(new String[]{chromePath, "www.baidu.com"});
    }catch(Exception e){

    }

%>