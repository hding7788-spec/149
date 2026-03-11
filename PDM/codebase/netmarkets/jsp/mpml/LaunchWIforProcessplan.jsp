<%@include file="/netmarkets/jsp/mpml/wiImports.jspf"%>
<%
    String oid = request.getParameter("oid");
    String containerOid = request.getParameter("ContainerOid");
	String type = "ProcessPlan";
    NmOid nmoid = new NmOid(oid);
    Object obj = nmoid.getRef();
	if(obj instanceof MPMOperation){
		type = "Operation";
	}
    NavigationCriteria nc=null;
    ArrayList<String> list = new ArrayList<String>();
    list.add(obj.toString());
    String remoteAdd = request.getRemoteAddr();
    String sid = request.getSession().getId();
    ClientNavigationCriteriaBean clientInfoBean = new ClientNavigationCriteriaBean(MPMLConstants.PP_APP_ID, remoteAdd, sid);

    String ncid = request.getParameter("ncid");
    //In case ncid is not availabel in URl, get default navigation criteria from default ncid
    String default_ncid =ncid;
    if(ncid == null || ncid ==""){
              default_ncid = new ClientNavigationCriteriaServiceImpl().getDefaultNavigationCriteriaID(list, clientInfoBean, null);
           }
           
   //out.println(">>>>>>>>>>>default_ncid:"+default_ncid);        
           
    // make the base url
    WTContext context = WTContext.getContext();
    StringBuilder base_url = new StringBuilder(256);
    base_url.append(context.getCodeBase());
    base_url.append(NetmarketURL.buildMvcURL("netmarkets/jsp/mpml/launchWorkInstruction.jsp?"));
    String baseUrl = base_url.toString();
	//out.println(">>>>>>>>>>>>>>>>>>>baseUrl:"+baseUrl);
%>

<script language="JavaScript" type="text/javascript">

function getNCIDfromUrl()
{
	//alert("window.opener.location:"+window.opener);
 //modified by tom 2011/10/19 start
 var urlString;
 if(window.opener){
  urlString = window.opener.location.href;
 } else {
	urlString = window.location.href;
 }
  //modified by tom 2011/10/19 start end
  var ncidVal = "";
  if (window.opener && !window.opener.closed) 
  {
	  urlString = window.opener.location.href;
  }
  else
  {
	  return ncidVal;
  }
   
  // strip off the basic url
  var splitByQuestionMark = urlString.split("?");
  if(splitByQuestionMark.length > 1)
  {
	  urlString = splitByQuestionMark[1];
  }
 
  // get parameters as key,value pairs
  var paramPairs = urlString.split("&");
  for (i = 0; i < paramPairs.length; i++)
     {
          var paramPair = paramPairs[i].split("=");
          var paramID = paramPair[0];        
          if(paramID == "ncid"){
          	ncidVal = paramID;
		return paramPair[1];
	    }
      }
   return ncidVal;
}

var ncid = getNCIDfromUrl();

if(ncid == null || ncid == ""){
ncid='<%=default_ncid%>';
}

var baseURL='<%=baseUrl%>';

var urlForLaunchWI = baseURL+"container="+'<%=type%>'+"&"+"oid="+'<%=oid%>'+"&ContainerOid="+'<%=containerOid%>'+"&ncid="+ncid+"&wizardActionClass="+"com.ptc.windchill.mpml.forms.WILauncherWithRelatedPartsFormProcessor";
//alert(">>>>>>>>>>>>>>>>>>urlForLaunchWI:"+urlForLaunchWI);
window.location.replace(urlForLaunchWI);

</script>