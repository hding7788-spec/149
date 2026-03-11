<%@ page import="com.ptc.netmarkets.model.NmOid"%>
<%@ page import="com.ptc.netmarkets.user.NmUser"%>
<%@ page import="com.ptc.netmarkets.role.NmRole"%>
<%@ page import="com.ptc.netmarkets.util.beans.*"%>
<%@ page import="com.ptc.netmarkets.project.NmProjectCommands"%>
<%@ page import="com.ptc.windchill.enterprise.team.commands.TeamCommands"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Locale"%>
<%@ page import="java.util.ResourceBundle"%>
<%@ page import="com.ptc.netmarkets.project.projectResource"%>
<%@ page import="wt.util.WTMessage"%>
<%@ page import="wt.util.HTMLEncoder"%>
<%@ page import="wt.inf.container.OrgContainer"%>
<%@ page import="ext.casc.process.util.ProcessUtil"%>
<%@ page import="com.ptc.windchill.principal.org.OrganizationCommands"%>
<%! private static final String RESOURCE = "com.ptc.netmarkets.project.projectResource";%>
<jsp:useBean id="nmcontext" class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request"/>
<jsp:useBean id="localeBean" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>
<jsp:useBean id="sessionBean" class="com.ptc.netmarkets.util.beans.NmSessionBean" scope="session"/>
<jsp:useBean id="linkBean" class="com.ptc.netmarkets.util.beans.NmLinkBean" scope="request"/>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<%//
   Locale locale = localeBean.getLocale();
   ResourceBundle stepRb = ResourceBundle.getBundle(RESOURCE, locale);

   NmCommandBean cb = new NmCommandBean();
   cb.setCompContext(nmcontext.getContext().toString());
   cb.setRequest(request);
   OrgContainer org = ProcessUtil.getOrgContainer();
   //NmOid projectOid = cb.getPrimaryOid();
   String spaces = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
   spaces = spaces + spaces + spaces + spaces;
%>

<table>
  <tr>
    <td align="left">
      <font class="wizardbuttonfont">
         <%=stepRb.getString(projectResource.SELECT_ROLE_COLON)%>
      </font>
    </td>
  </tr>
  <tr>
    <td align="left" valign="bottom" nowrap>
      <select name="orgRoles" size="15" multiple><%
        ArrayList roles = OrganizationCommands.listOrgRoles(org);
        int size = roles.size();
        System.out.println("---------org:"+org);
        System.out.println("---------roles:"+size);
        for (int i = 0; i < size; i++) {
           NmRole role = (NmRole) roles.get(i);
           String roleid = "";
           if (linkBean.isTrail())
           {
             roleid = "id=\""+linkBean.getLinkID("role",role.getName()) + "\"";
           }
%>
           <option <%=roleid%> value="<%=role.getInternalName()%>"> <%=role.getName()%><%
        }
        %><option value="spacesKeyValue"> <%=spaces%>
      </select>
      &nbsp;&nbsp;&nbsp;
      <% String clearButton = WTMessage.getLocalizedMessage(RESOURCE, projectResource.CLEAR_SELECTION, null, locale); %>
      <input type="button" id="PJL_addRoleProj_clear" value="<%=HTMLEncoder.encodeAndFormatForHTMLContent(clearButton)%>" onclick="return clearSelects();"></td>      
  </tr>
  <tr>
    <td align="left">
      <br>
      <font class="wizardbuttonfont">
         <%=stepRb.getString(projectResource.ADD_PROJECT_ROLE)%>
      </font>
    </td>
  </tr>
  <tr>
    <td align="left">
      <textarea name="projectRoles" rows="4" cols="36" wrap onKeypress="return validate(event)"></textarea>
    </td>
  </tr>
</table>

<SCRIPT LANGUAGE="JavaScript">
function clearSelects() {
   var orgList = window.document.forms.mainform.orgRoles.selectedIndex = -1;
}

function validate(event) {
   //- We need to use the charCode event property of the keypress instead
   //  of keyCode - doing so allow this code to disregard non-character
   //  keypresses such as arrow keys, home, end, etc.  It's not appropriate
   //  to use keyCode, and then only charCode if keyCode is zero or undefined.
   //  keyCode is zero when a character key is pressed; but shares some code
   //  values with the printable characters - eg. 37 is the left arrow keyCode
   //  but also the % charCode, likewise the Home key is value 36, as is the $
   //  character.  The Home key should be allowed for use in the text box; but
   //  the $ char should not.
   //
   var myCode = event.charCode;
   var undefined;
   var msg;
   
   // - set returnValue to true by default
   event.returnValue = true;

   //- characters not allowed:  *&|().=+<>#;!$"@\/^'
   if ((myCode >= 33 && myCode <= 36) || myCode == 38 ||
       (myCode >= 40 && myCode <= 43) ||
        myCode == 46 || myCode == 47 ||
       (myCode >= 59 && myCode <= 62) ||
        myCode == 64 || myCode == 92 || 
        myCode == 94 || myCode == 124 ||
        myCode == 39) {

      event.returnValue = false;
      
      // Note that the "\" character needs to be escaped
      // Note that the ' character needs to be escaped
      var msgInserts = new Array('*&|().=+<>#;\'!$"\\\@/^');
      JCAAlert("com.ptc.netmarkets.project.projectResource.INVALID_CHARACTERS_IN_ROLE_NAME", "", msgInserts);
   }
   else if (myCode == 126) {
      // Don't allow two consecutive tildes
      // charsEntered will be the value of the textarea *before* this current character
      var charsEntered = window.document.forms.mainform.projectRoles.value;
      var tildeIndex = charsEntered.lastIndexOf("~");
      if (charsEntered.length > 0 && tildeIndex == (charsEntered.length - 1)) {
         event.returnValue = false;
         JCAAlert("com.ptc.netmarkets.project.projectResource.MULTIPLE_CONSECUTIVE_TILDE_IN_ROLE_NAME");
      }
   }
   return event.returnValue;
}

if (is_nav4) {
    document.captureEvents(Event.KEYPRESS);
    document.onkeypress = validate;
}
</SCRIPT>
