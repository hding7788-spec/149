<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
    
    <!-- task actions section -->

    <tr></tr>   

    <tr>
    <tr><td colspan=3><tags:customSetUpParticipants/></td></tr>
    </tr>

    <tr></tr>

    <tr>
        <tags:taskPanel propertyModel="${propertyModel}" attrs="workitem_comment"/>
    </tr>
    
   <tr>
    <td></td>
    <td valign="top"><FONT class=tabledatafont>
        <tags:taskPanelValue propertyModel="${propertyModel}" attrs="workitem_automatefasttrack"/>
   </td>
   </tr> 
   
    <!-- automatefasttrack is not needed here, because it is be printed out inside the rountingchoices request below -->
    <tr>
        <td>&nbsp;</td>
        <td valign="top" align="left">
            <FONT class=tabledatafont>
            <tags:taskPanelValue propertyModel="${propertyModel}" attrs="workitem_routingchoices"/></FONT>
        </td>
    </tr>
    <tr>
            <td>&nbsp;</td>
            <td valign="top" align="left">
                <FONT class=tabledatafont>
                <tags:taskPanelValue propertyModel="${propertyModel}" attrs="workitem_votingoptions"/> </FONT>
            </td>
    </tr>