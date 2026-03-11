<%@include file="imports.jspf"%>
<%@include file="/netmarkets/jsp/mpml/wiImports.jspf"%>
<%@page import="ext.ptc.workinstruction.WorkInstructionHelper,ext.ptc.ViewWIHelper"%>
<%@page import="com.ptc.netmarkets.workinstructions.WorkInstructionsUtilities"%>
<%@page import="com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper" %>

<html>
   <head>
      <%@ include file="/wtcore/jsp/wvs/dialog.jspf" %>
      <meta http-equiv="Content-Style-Type" content="text/css">
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <title><fmt:message key="mpml.headerWorkInstruction.title"/></title>       
      <link rel=stylesheet href="../../css/workInstructionStyles.css" type="text/css">    
     <!-- script type="text/javascript" src="flowplayer-3.2.4.min.js"></script --->	  
      <SCRIPT type="text/javascript">
    	
    	function generateReport(form){
    			var myind = form.paperSizeToPrint.selectedIndex
    			if(!myind==0){
        			var popupWin = window.open(form.paperSizeToPrint.options[myind].value,"test");										
					popupWin.focus(); 					
        			}	
    			}    	
		
		</SCRIPT>
		
   <script type="text/javascript" src="js/resizeImage.js"></script>	
   <script language="JavaScript">

 function openNewWindow(url) 
 {
	 var width  = 600;
	 var height = 700;
	 var left   = (screen.width  - width)/2;
	 var top    = (screen.height - height)/2;
	 var params = 'width='+width+',height='+height;
	 params += ',top='+top+',left='+left;
	 params += ',directories=no';
	 params += ',location=no';
	 params += ',menubar=no';
	 params += ',resizable=no';
	 params += ',scrollbars=no';
	 params += ',status=no';
	 params += ',toolbar=no'; 
	 popupWin = window.open(url,'CenteredPopupWindow', params);
 }
   </script>
   
   </head>
   <body onload="resizeImage()">
<%@include file="commonWeb.jspf"%>
<%@include file="extractAndProcessUIData.jspf"%>
      
<%
IeService ieObj2 = (IeService) request.getAttribute("ieObjFromCore");
String pplanObid2 =  ieObj2.getAttributeValue("PROCESS_PLAN", 0, "obid");
String oidPPlan2= WorkInstructionsUtilities.getOid(pplanObid2);
MPMProcessPlan plan2 = (MPMProcessPlan)WorkInstructionsUtilities.getObject(oidPPlan2);
String objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(plan2).toString();
%>
      
        <%for(int currentOPIndex=0;currentOPIndex<totalOp;currentOPIndex++) 
          {%>
			<%@include file="extractAndProcessUIDataForOperations.jspf"%>

       		<% do{  
					String urlPrint = url.replaceFirst("coreWorkInstruction.jsp", "coreWorkInstructionPrintable.jsp");
		       		%><BR><BR>
		       		<%if(!isPrintChoicePrinted){ 
		       		String help_button_url = WorkInstructionsUtilities.getHelpLink();%>

    	<%isPrintChoicePrinted=true;} %>
		<%if(currentOPIndex==0){%>
					<TABLE  border=0 width="100%">
							<% if(ppOid != null){ 
								if(objectType.indexOf("Process_MP")>0){%>
								<TR><TD valign="top" colspan="2" ><%@ include file="headerWorkInstruction.jspf"%></TD></TR>
								<%}else{ %>
								<TR><TD valign="top" colspan="2" ><%@ include file="headerWorkInstruction2.jspf"%></TD></TR>
								<%} %>
								<TR><TD valign="top" colspan="2" ><%@ include file="headerWorkInstructionSign.jspf"%></TD></TR>
							<% }else{%>
								<TR><TD valign="top" colspan="2" > <%@ include file="headerOpWorkInstruction.jspf"%></TD></TR>
							<%}%>
					</TABLE>
		<%}%>
		       		<TABLE border=0 width="100%">
		       			<TR>
		       				<TD>
			                 	<TABLE >
			                     	<%if(isStandard)
				                      {  
			                     	     Enumeration enuObj =  ieObj.getElements("CURRENT_OPERATION"); 
			                     	     Element objElem= ( Element )enuObj.nextElement();%> 
			                     	     <TR>
			                     	     	<TD  colspan="2">
			                     	     		<FONT>
				                     	     		<A HREF="<%=configParamObject.getUrlStandardProcedure(WorkInstructionsUtilities.getOid(objElem))%>" target="_blank" >
				                     	     			<fmt:message key="mpml.operationDetailWorkInstruction.standardProcedure"/>: <ie:getValue name="com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink.operationLabel" groupIn="CURRENT_OPERATION"/> <ie:getValue name="name" groupIn="CURRENT_OPERATION"/> 
				                     	     		</A>
			                     	     		</FONT>
			                     	      	</TD>
			                     	    </TR>
			                  	</TABLE>
		                	</TD>
		                </TR>
		             </TABLE>
		
		             <% break; // Standard link, end the loop.
		                  	      } // close if isStandard %>

					<TABLE border=1 width="100%">
						<%if(objectType.indexOf("Process_MP")>0){ %>
                 	      <TR><TD valign="top" colspan="2"><%@ include file="operationHeaderWorkInstruction.jspf"%></TD></TR>
                 	   <%}else{ %>
                 	      <TR><TD valign="top" colspan="2"><%@ include file="operationHeaderWorkInstruction2.jspf"%></TD></TR>
                 	   <%} %>
                 	      
		                <TR><TD>
							<TABLE border="0" width="100%">							   
		                 	   <TR>
		                 	   <%boolean detailPrinted = false;
							  //System.out.println("illustrations-----------size="+illustrations.size());			  
							  							  
		                 	   if ( illustrations != null && illustrations.size() > 0 )
		                 	     { 
								 
								 //System.out.println("illustration...............:"+illustrations.get(0)[0]);
								 //System.out.println("illustration...............:"+illustrations.get(0)[1]);
		                 		   for(int img=0;img<illustrations.size();img++){
		                 	       imageName = ((String[]) illustrations.get(img))[0]; 
		                 	       edrURL = ((String[]) illustrations.get(img))[1];
		                 	       illustName = ((String[]) illustrations.get(img))[2];
									
									//System.out.println("imageName-------------:"+imageName);
									//System.out.println("edrURL-------------:"+edrURL);
									//System.out.println("illustName-------------:"+illustName);
		                 	     //}
		                         //if (count < 1)
		                        // { // show the DETAIL header for an illustration for the FIRST illustration to display, 
		                           // skip (do not show this header) for other illustrations in the SAME operation.
		                           // This header shows the information about the work center, process, etc.%>
		                           
		                       <!--  %}%-->
		                         <TD valign="top" width="50%" align="center">                           
		                       <%if (imageName != null && !imageName.equals(""))
		                         { // Show image =flower.jpg&
								  String urlprefix = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/netmarkets/";
								  String fileExtName = "";
								  
								  if(imageName.contains("originalFileName")){
									fileExtName = imageName.split("originalFileName")[1].split("&")[0].split("[.]")[1];
								  } else {
									String[] tempAry = imageName.split("[?]")[0].split("[/]");
									fileExtName = tempAry[tempAry.length-1].split("[.]")[1];
								  }
								 //System.out.println("fileExtName:::::::::::"+fileExtName);						
								 if(fileExtName!=null && fileExtName.toUpperCase().contains("FLV")){
								 String fileName=(new ext.ptc.workinstruction.WorkInstructionHelper(edrURL)).download();
								 //String fName = WorkInstructionHelper.getContentData(edrURL);
								 String fName = WorkInstructionHelper.getContentData2(illustName);
								 //System.out.println("fName-----------------:"+fName);
								 fileName = fileName.split("[?]")[0];
								 String flvUrl = urlprefix+fileName;
								 
								 %> 
								 <object type="application/x-shockwave-flash" width="560" height="360"
									wmode="transparent" data="../flvplayer.swf?file=flv/<%=fName%>"> 
									<param name="movie" value="../flvplayer.swf?file=flv/<%=fName%>"/>
									<param name="allowFullScreen" value="true" />
									<param name="wmode" value="transparent"/>
								</object>
								 
								<%} else if(fileExtName!=null && fileExtName.contains("htm")){
									String fileName=(new ext.ptc.workinstruction.WorkInstructionHelper(edrURL)).download();
									fileName = fileName.split("[?]")[0];
									String htmUrl = "../../"+fileName;
									%>
									<iframe align="left" width="100%" hight="400" src="<%=htmUrl%>"></iframe>
									<!--<A HREF="<%=htmUrl%>"><jsp:include page="<%=htmUrl%>" flush="true"></jsp:include></A>-->
								<%}else {%>
									<A HREF="<%=edrURL%>"><IMG class="image" src="<%=imageName%>"  BORDER="0" TITLE="<%=illustName%>"/></A>
									<!--<%@ include file="integrateCreoView.jspf"%> -->
								<%}%>
		                         	
		                       <%}%></TD>
		                       <%
		                       String opObid3 = ieObj.getAttributeValue("CURRENT_OPERATION", 0, "obid");
		                       String oidOP3 = WorkInstructionsUtilities.getOid(opObid3);
		                       MPMOperation opera3 = (MPMOperation) WorkInstructionsUtilities.getObject(oidOP3);
		                       boolean detailPrinted2 = ViewWIHelper.detailPrintedFlag(opera3);
		                       //!detailPrinted
		                       if (objectType.indexOf("Process_AP")>0) 
		                       {  // show the details for an operation (ie, parts, documents, resources) only for the 
		                          // first illustration to display withing one operation. For the rest of the illustrations under
		                          // the same operation, do not show this detail%>
		                           <TD valign="top" width="50%" > <jsp:include page="operationDetailWI2.jsp"/></TD></TR>
		                    	<%detailPrinted=true;}
		                       else{%>
		                    	<TD valign="top" width="50%" ></TD>
		                    	<%}		                    		
		                       count = img;
		                       	}//end of image for 
		                       	%>
								</TR>
		                       	</TABLE>
		                </TD></TR>
		            </TABLE >
		                       <%}else if(!detailPrinted){%>		                    
		                       <TR>
		                       		<TD valign="top" width="50%" ></TD>
		                       </TR>
		                       <%} %>
		                    </TABLE>
		                </TD></TR>
		            </TABLE>
		                    <% if ( illustrations != null && (illustrations.size() == count + 1) )
		                    { %> 
		                    </TABLE>
		                </TD></TR>
		            </TABLE> 
		                    	<%break; // no more illustrations to display. End the while loop.
		                    }
		                    if( (illustrations == null) || (illustrations != null) && (illustrations.size()==0))
		                    { %> 
		                    </TABLE>
		                </TD></TR>
		            </TABLE> 
		            			<%break; 
		                    }
		                    count++;
		                    isOpContinue = true;
		        } while(true);       	  
    	
    	}//end of regular WI %>    	

        <%  
        com.infoengine.object.factory.Group masterOperationGroup = ieObj.getGroup ("MASTER_OPERATIONS");
        com.infoengine.object.factory.Group processPlanGroup = ieObj.getGroup ("PROCESS_PLAN"); 
        if ((processPlanGroup!=null) && ((masterOperationGroup == null) || ((masterOperationGroup != null) && (masterOperationGroup.getElementCount() < 1))))
        {%>
        	<TABLE border=0>
                <TR><TD valign="top" colspan="2" ><%@ include file="headerWorkInstruction.jspf"%></TD></TR>
	                <BR><BR>
	                <TABLE >
	                	<TR><TD>
	            		<TR><TD valign="middle" align="center" colspan="3" > <fmt:message key="mpml.coreWorkInstruction.operationsFound"/> 
	            		</TD></TR> 
	            	</TABLE>
            	</TD></TR> 
           	</TABLE>
      <%}%>                                   
      <%  // clean the Virtual Data Base
       ieObj.removeAllGroups();%>
    </body>
</html>