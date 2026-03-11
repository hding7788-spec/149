package ext.casc.workflow;

import com.ptc.core.components.rendering.GuiComponent;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.TextDisplayComponent;
import com.ptc.netmarkets.model.NmException;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.wp.WorkPackage;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.access.AccessAdminUtil;
import ext.casc.constants.Constants;
import ext.casc.process.util.ProcessUtil;
import ext.casc.sop.util.SopWorkflowUtil;
import wt.change2.ChangeHelper2;
import wt.change2.WTAnalysisActivity;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.*;
import wt.part.WTPart;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.util.WTException;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessTemplate;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfBlock;
import wt.workflow.engine.WfContainer;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkflowHelper;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.*;

@SuppressWarnings("unchecked")
public class CmWfTaskProcessorCommands implements RemoteAccess {

    public static final String KEY_WF_AUGMENT_ROLES_FORM = "CM_CUSTOM_WF_AUGMENT_ROLE_FORM";
    public static final String KEY_WF_AUGMENT_GROUPS_FORM = "CM_CUSTOM_WF_AUGMENT_GROUP_FORM";
    public static final String KEY_WF_CURRENT_USER = "CM_CUSTOM_WF_CURRENT_USER";
    private static final String CLASSNAME = CmWfTaskProcessorCommands.class.getName();
    private static final String END_LINE = "　　　　　　　　　　　　";

    public static GuiComponent getJSActions(NmCommandBean cb) {

        TextDisplayComponent ret = new TextDisplayComponent("");
        ret.setCheckXSS(false);
        StringBuffer buffer = new StringBuffer('\n');

        buffer.append("<!-- 流程角色参与者选择支持 -->").append('\n');
        buffer.append("<script type=\"text/javascript\">").append('\n');

        buffer.append("    var count = 1;").append('\n');

        buffer.append("    function getForm() {").append('\n');
        buffer.append("        return document.mainform;").append('\n');
        buffer.append("    }").append('\n');

        buffer.append("    // 添加选中的用户到指定流程角色").append('\n');
        buffer.append("    function appendUsers(role) {").append('\n');
        buffer.append("        //var roleElement = document.getElementsByName(role);");
        buffer.append("        //if(roleElement[0].childNodes.length==1){alert('只能选择一个'+role);return;}");
        buffer.append("        var form = getForm();").append('\n');
        buffer.append("        var selectUnit = document.getElementById(\"CustActVarselectUnitValueCustActVar\");").append('\n');
        buffer.append("        if(selectUnit&&selectUnit.value!=\"\"&&role=='外部会签者'){alert('已经设置电子会签单位，不能再设置外部会签者');return; }").append('\n');
        buffer.append("        var section = findRoleSection(role);").append('\n');
        buffer.append("        if(section.childNodes.length==1&&role!='打印者'&&role!='内部会签者'){alert('只能选择一个'+role);return;}");
        buffer.append("        if (section == null)").append('\n');
        buffer.append("            return;").append('\n');
        buffer.append("        var index = $(\"groups\").selectedIndex;").append('\n');
        buffer.append("        if (index < 0) {").append('\n');
        buffer.append("            alert(\"请选择一个用户分组!\");").append('\n');
        buffer.append("            return;").append('\n');
        buffer.append("        }").append('\n');
        buffer.append("        var group_options = $(\"groups\").options;").append('\n');
        buffer.append("        var option = group_options[index].innerHTML;").append('\n');
        buffer.append("        if(role=='指派工艺组长者'&&option.indexOf('主任工艺师')==-1){alert('指派工艺组长者只能从主任工艺师中选择！');return;}");
        buffer.append("        if(role=='工时审核员'&&option.indexOf('工时审核员')==-1){alert('工时审核员只能从工时审核员组中选择！');return;}");
        buffer.append("        // 读取选定用户,逐个添加").append('\n');
        buffer.append("        var users = $(\"users\");").append('\n');
        buffer.append("        if (users.selectedIndex < 0) {").append('\n');
        buffer.append("            alert(\"请选择一个或多个用户!\");").append('\n');
        buffer.append("            return;").append('\n');
        buffer.append("        }").append('\n');
        buffer.append("        var options = users.options;").append('\n');
        buffer.append("        for (var i = 0; i < options.length; i++) {").append('\n');
        buffer.append("            if (options[i].selected && options[i].value.length != 0) {").append('\n');
        buffer.append("                var userOid = options[i].value;").append('\n');
        buffer.append("                var fullName = options[i].text;").append('\n');
        buffer.append("                tryAppendUser(role, userOid, fullName);").append('\n');
        buffer.append("            }").append('\n');
        buffer.append("        }").append('\n');
        buffer.append("    }").append('\n');

        buffer.append("    // 移除点击的用户").append('\n');
        buffer.append("    function removeUser(spanId) {").append('\n');
        buffer.append("        var userSpan = $(spanId);").append('\n');
        buffer.append("        if (userSpan != null) {").append('\n');
        buffer.append("            userSpan.parentNode.removeChild(userSpan);").append('\n');
        buffer.append("        }").append('\n');
        buffer.append("    }").append('\n');

        buffer.append("    // 找到角色的HTML定义区域").append('\n');
        buffer.append("    function findRoleSection(role) {").append('\n');
        buffer.append("        var roleListAreaValue = document.getElementById(\"roleListArea\");").append('\n');
        buffer.append("        // 查找流程角色的定义区域").append('\n');
        buffer.append("        var roleSections = roleListAreaValue.getElementsByTagName(\"div\");").append('\n');
        buffer.append("        var section = null;").append('\n');
        buffer.append("        for (var i = 0; i < roleSections.length; i++) {").append('\n');
        buffer.append("            if (role == roleSections.item(i).getAttribute(\"name\")) {").append('\n');
        buffer.append("                section = roleSections.item(i);").append('\n');
        buffer.append("                break;").append('\n');
        buffer.append("            }").append('\n');
        buffer.append("        }").append('\n');
        buffer.append("        if (section == null)").append('\n');
        buffer.append("            alert(\"未找到指定的角色<\" + role + \">!\");").append('\n');
        buffer.append("        return section;").append('\n');
        buffer.append("    }").append('\n');

        buffer.append("    // 尝试添加一个用户到指定角色, 如已存在则不再重复添加").append('\n');
        buffer.append("    function tryAppendUser(role, userOid, fullName) {").append('\n');
        buffer.append("        // 查找角色定义区域").append('\n');
        buffer.append("        var section = findRoleSection(role);").append('\n');
        buffer.append("        if (section == null)").append('\n');
        buffer.append("            return;").append('\n');
        buffer.append("        // 查找用户定义区域").append('\n');
        buffer.append("        var eid = \"角色.\" + role + \".\" + userOid + \".\" + fullName;").append('\n');
        buffer.append("        var userSpan = $(eid);").append('\n');
        buffer.append("        if (userSpan != null)").append('\n');
        buffer.append("            return; // 该用户/角色已存在").append('\n');
        buffer.append("        // 创建用户定义区域").append('\n');
        buffer.append("        userSpan = document.createElement(\"span\");").append('\n');
        buffer.append("        userSpan.id = eid;").append('\n');
        buffer.append("        // 移除用户的链接").append('\n');
        buffer.append("        var userLink = document.createElement(\"a\");").append('\n');
        buffer.append("        userLink.href = \"javascript:removeUser('\" + eid + \"')\";").append('\n');
        buffer.append("        userLink.innerHTML = fullName;").append('\n');
        buffer.append("        userLink.title = \"移除参与者: \" + fullName;").append('\n');
        buffer.append("        // 定义用户的表单域").append('\n');
        buffer.append("        var userField = document.createElement(\"input\");").append('\n');
        buffer.append("        userField.type = \"hidden\";").append('\n');
        buffer.append("        userField.autocomplete = \"off\";").append('\n');
        buffer.append("        userField.name = \"角色.\" + role;").append('\n');
        buffer.append("        userField.value = userOid;").append('\n');
        buffer.append("        // 构造用户定义区域").append('\n');
        buffer.append("        userSpan.appendChild(document.createTextNode(\" \")); // 1个空格").append('\n');
        buffer.append("        userSpan.appendChild(userLink);").append('\n');
        buffer.append("        userSpan.appendChild(userField);").append('\n');

        buffer.append("        if (count>20){").append('\n');
        buffer.append("        var appendBr = document.createElement(\"<br>\")").append('\n');
        buffer.append("        userSpan.appendChild(appendBr);").append('\n');
        buffer.append("        count = 0").append('\n');
        buffer.append("        }").append('\n');

        buffer.append("        // 将用户添加到角色区域").append('\n');
        buffer.append("        section.appendChild(userSpan);").append('\n');
        buffer.append("        count++");
        buffer.append("    }").append('\n');

        buffer.append("    // 根据用户选中的分组(包括查询结果), 显示该分组中的用户").append('\n');
        buffer.append("    function listGroupUsers() {").append('\n');
        buffer.append("        var form = getForm();").append('\n');
        buffer.append("        var groups = $(\"groups\");").append('\n');
        buffer.append("        var index = groups.selectedIndex;").append('\n');
        buffer.append("        if (index < 0)").append('\n');
        buffer.append("            return;").append('\n');
        buffer.append("        var userList = groups.options[index].value;").append('\n');
        buffer.append("        var userArray = userList.split(\";\");").append('\n');
        buffer.append("        var users = $(\"users\");").append('\n');
        buffer.append("        users.options.length = 0;").append('\n');
        buffer.append("        for (var i = 0; i < userArray.length; i++) {").append('\n');
        buffer.append("            var userItems = userArray[i].split(\",\");").append('\n');
        buffer.append("            if (userItems.length >= 2) {").append('\n');
        buffer.append("                var option = document.createElement(\"option\");").append('\n');
        buffer.append("                option.text = userItems[1]; // fullName").append('\n');
        buffer.append("                option.value = userItems[0]; // userOid").append('\n');
        buffer.append("                users.options.add(option);").append('\n');
        buffer.append("            }").append('\n');
        buffer.append("        }").append('\n');
        buffer.append("        var option = document.createElement(\"option\");").append('\n');
        buffer.append("        option.text = \"").append(END_LINE).append("\";").append('\n');
        buffer.append("        option.value = \"\";").append('\n');
        buffer.append("        users.options.add(option);").append('\n');
        buffer.append("    }    ").append('\n');

        buffer.append("    // 完成任务按钮动作").append('\n');
        buffer.append("    function completeTask() {").append('\n');
        buffer.append("        var isNeedCheckRoles = true;").append('\n');
        buffer.append("        var completehidden = document.getElementsByName(\"completehidden\")[0];").append('\n');
        buffer.append("          //completehidden.disabled='true';").append('\n');
        buffer.append("        // 检查必须定义的角色").append('\n');
        buffer.append("        var form = getForm();").append('\n');
        buffer.append("        if(isNeedCheckRoles){").append('\n');
        buffer.append("        var neededRoles = document.getElementsByName(\"neededrole\");").append('\n');
        buffer.append("        for (var i = 0; i < neededRoles.length; i++) {").append('\n');
        buffer.append("            var role = neededRoles.item(i).value;").append('\n');
        buffer.append("            var roleSection = findRoleSection(role);").append('\n');
        buffer.append("            var cntChildSpan = 0;").append('\n');
        buffer.append("            if (roleSection != null) {").append('\n');
        buffer.append("                // firefox 有innerHTML作为其childNode").append('\n');
        buffer.append("                for (var j = 0; j < roleSection.childNodes.length; j++) {").append('\n');
        buffer.append("                    var child = roleSection.childNodes.item(j);").append('\n');
        buffer.append("                    var nodeType = child.nodeType;").append('\n');
        buffer.append("                    if (nodeType == 1) { // 1: Element node 3: Text node").append('\n');
        buffer.append("                        cntChildSpan++;").append('\n');
        buffer.append("                        break;").append('\n');
        buffer.append("                    }").append('\n');
        buffer.append("                }").append('\n');
        buffer.append("            }").append('\n');

        buffer.append("            var isZHQianshen =  document.getElementById('routingChoice_终止签审'); ").append('\n');
        buffer.append("            var isQXQianshen =  document.getElementById('routingChoice_取消签审'); ").append('\n');
        buffer.append("            var isYiJianShouKong =  document.getElementById('routingChoice_一键受控'); ").append('\n');
        buffer.append("            var isWuXuDaYin1 =  document.getElementById('routingChoice_无需打印'); ").append('\n');
        buffer.append("            var isWuXuDaYin2 =  document.getElementById('routingChoice_无需打印_设计更改单偏离单不允许选无需打印'); ").append('\n');

        buffer.append("            if((isZHQianshen == null || !isZHQianshen) && (isQXQianshen == null || !isQXQianshen) && (isWuXuDaYin1 == null || !isWuXuDaYin1) && (isWuXuDaYin2 == null || !isWuXuDaYin2) && (isYiJianShouKong == null || !isYiJianShouKong)){").append('\n');//终止签审或取消签审时不需要校验是否设置了参与者
        buffer.append("            if (roleSection != null && cntChildSpan < 1) {").append('\n');
        buffer.append("            if ((role != \"主任工艺师\")) {").append('\n');
        buffer.append("                alert(\"请定义角色<\" + role + \">的参与者\");").append('\n');
        buffer.append("                //completehidden.disabled='';").append('\n');
        buffer.append("                return;").append('\n');
        buffer.append("            }").append('\n');
        buffer.append("            }").append('\n');
        buffer.append("            }").append('\n');

        buffer.append("            else if (isYiJianShouKong != null && isYiJianShouKong.checked) {").append('\n');
        buffer.append("            if ((role == \"主任工艺师\") && (roleSection == null || cntChildSpan < 1)) {").append('\n');
        buffer.append("                alert(\"请定义角色<\" + role + \">的参与者\");").append('\n');
        buffer.append("                    return;").append('\n');
//        buffer.append("                }").append('\n');
        buffer.append("            }").append('\n');
        buffer.append("            }").append('\n');

        buffer.append("            else{").append('\n');

        buffer.append("            if ((isZHQianshen == null || !isZHQianshen.checked)&& (isWuXuDaYin1 == null || !isWuXuDaYin1.checked)&& (isWuXuDaYin2 == null || !isWuXuDaYin2.checked)&&(isYiJianShouKong == null || !isYiJianShouKong.checked) && roleSection != null && cntChildSpan < 1) {").append('\n');
        buffer.append("            if ((role != \"主任工艺师\")) {").append('\n');
        buffer.append("                alert(\"请定义角色<\" + role + \">的参与者\");").append('\n');
        buffer.append("                //completehidden.disabled='';").append('\n');
        buffer.append("                return;").append('\n');
        buffer.append("            }").append('\n');
        buffer.append("            }").append('\n');
        buffer.append("            }").append('\n');
        buffer.append("        }").append('\n');

        buffer.append("        moveRoleFields();").append('\n');
        buffer.append("        }").append('\n');
        buffer.append("        completehidden.click();").append('\n');
        buffer.append("        return;").append('\n');
        buffer.append("    }").append('\n');

        buffer.append("    // 替换原有的任务完成按钮").append('\n');
        buffer.append("    function replaceTaskCompleteButton() {").append('\n');
        buffer.append("        var completeBtn = document.getElementsByName(\"complete\")[0];").append('\n');
        buffer.append("        var completehidden = document.getElementsByName(\"completehidden\")[0];").append('\n');
        buffer.append("        if (completeBtn&&completehidden) {").append('\n');
        buffer.append("            completehidden.onclick = completeBtn.onclick;").append('\n');
        buffer.append("            completeBtn.onclick = completeTask;").append('\n');
        buffer.append("        }").append('\n');
        buffer.append("    }").append('\n');

        buffer.append("    // 将角色参与者信息移动为form下的一级对象").append('\n');
        buffer.append("    function moveRoleFields() {").append('\n');
        buffer.append("        // firefox 不能正确post角色参与者,需要将嵌入在span里面的input转移到form下第一级").append('\n');
        buffer.append("        if (navigator.appName.indexOf(\"Netscape\") < 0)").append('\n');
        buffer.append("            return;").append('\n');
        buffer.append("").append('\n');
        buffer.append("        var form = getForm();").append('\n');
        buffer.append("        var inputs = document.getElementsByTagName(\"input\");").append('\n');
        buffer.append("        var nodes = new Array();").append('\n');
        buffer.append("        var cnt = 0;").append('\n');
        buffer.append("        for (var i = 0; i < inputs.length; i++) {").append('\n');
        buffer.append("            var e = inputs.item(i);").append('\n');
        buffer.append("            var p = e.parentNode;").append('\n');
        buffer.append(
                "            if (p.tagName.toUpperCase() == \"SPAN\" && e.type.toUpperCase() == \"HIDDEN\" && e.name.indexOf(\"角色.\") == 0) {")
                .append('\n');
        buffer.append("                nodes[nodes.length] = e;").append('\n');
        buffer.append("                cnt++;").append('\n');
        buffer.append("            }").append('\n');
        buffer.append("        }").append('\n');
        buffer.append("").append('\n');
        buffer.append("        for (var i = 0; i < cnt; i++) {").append('\n');
        buffer.append("            var node = nodes[i];").append('\n');
        buffer.append("            var moved = node.parentNode.removeChild(node);").append('\n');
        buffer.append("            window.getMainForm().appendChild(moved);").append('\n');
        buffer.append("        }").append('\n');
        buffer.append("    }").append('\n');
        buffer.append("</script>").append('\n');

        ret.setValue(buffer.toString());
        return ret;
    }

    public static GuiComponent initJSAction(NmCommandBean cb) {
        TextDisplayComponent ret = new TextDisplayComponent("");
        ret.setCheckXSS(false);
        try {
            ret.setHiddenId(true);
            String me = SessionHelper.getPrincipal().getName();
            ret.addHiddenField(KEY_WF_CURRENT_USER, me);
        } catch (WTException wte) {
        }
        StringBuffer buffer = new StringBuffer('\n');

        buffer.append("<script type=\"text/javascript\">").append('\n');

        buffer.append("    // 替换原有的任务完成按钮").append('\n');
        buffer.append("    function replaceTaskCompleteButton2() {").append('\n');
        buffer.append("        var completeBtn = document.getElementsByName(\"complete\")[0];").append('\n');
        buffer.append("        if (completeBtn) {").append('\n');
        buffer.append("            completeBtn.oldOnClick = completeBtn.onclick;").append('\n');
        buffer.append("            completeBtn.onclick = completeTask2;").append('\n');
        buffer.append("        }").append('\n');
        buffer.append("    }").append('\n');

        buffer.append("    function setupGroupAndUsers() {").append('\n');
        buffer.append("        var option = document.createElement(\"option\");").append('\n');
        buffer.append("        option.text = \"").append(END_LINE).append("\";").append('\n');
        buffer.append("        option.value = \"\";").append('\n');
        buffer.append("        if ($(\"groups\") && $(\"groups\").options)").append('\n');
        buffer.append("            $(\"groups\").options.add(option);").append('\n');

        buffer.append("        option = document.createElement(\"option\");").append('\n');
        buffer.append("        option.text = \"").append(END_LINE).append("\";").append('\n');
        buffer.append("        option.value = \"\";").append('\n');
        buffer.append("        if ($(\"users\") && $(\"users\").options)").append('\n');
        buffer.append("            $(\"users\").options.add(option);").append('\n');
        buffer.append("        if ($(\"groups\") && $(\"groups\").options) {").append('\n');
        buffer.append("            $(\"groups\").selectedIndex = 0;").append('\n');
        buffer.append("            if (typeof listGroupUsers == 'function')").append('\n');
        buffer.append("                listGroupUsers();").append('\n');
        buffer.append("        }").append('\n');
        buffer.append("    }").append('\n');
        buffer.append("    setupGroupAndUsers();").append('\n');
        buffer.append("    if (typeof replaceTaskCompleteButton == 'function')").append('\n');
        buffer.append("        replaceTaskCompleteButton();").append('\n');
        buffer.append("    else {").append('\n');
        buffer.append("        replaceTaskCompleteButton2();").append('\n');
        buffer.append("    }").append('\n');
        buffer.append("    if (typeof searchUsers == 'function')").append('\n');
        buffer.append("        searchUsers();").append('\n');
        buffer.append("</script>").append('\n');

        ret.setValue(buffer.toString());
        return ret;
    }

    public static GuiComponent getClearCommentsJSAction() {
        TextDisplayComponent ret = new TextDisplayComponent("");
        ret.setCheckXSS(false);
        StringBuffer buffer = new StringBuffer('\n');

        buffer.append("<script type=\"text/javascript\">").append('\n');
        buffer.append("    var cm_comments = $('CustActVarcommentsCustActVar');").append('\n');
        buffer.append("    if (cm_comments)").append('\n');
        buffer.append("        cm_comments.value = '';").append('\n');
        buffer.append("</script>").append('\n');

        ret.setValue(buffer.toString());
        return ret;
    }

    public static GuiComponent getContainerTeamRoles(NmCommandBean cb) throws RemoteException,
            InvocationTargetException, WTException {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "getContainerTeamRoles";
            return (GuiComponent) RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null,
                    new Class[] { NmCommandBean.class }, new Object[] { cb });
        }
        boolean falg = SessionServerHelper.manager.setAccessEnforced(false);
        ArrayList<String> internalValues = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();
        ArrayList<String> selectedValues = new ArrayList<String>();

        ComboBox ret = new ComboBox(internalValues, values, selectedValues);
        ret.setName("Groups");
        ret.setId("groups");
        ret.setSize(21);
        ret.addJsAction("onclick", "javascript:listGroupUsers()");

        WorkItem workItem = getWorkItem(cb);
        Persistable pbo = workItem.getPrimaryBusinessObject().getObject();
        WTContained contained = null;
        if (pbo instanceof WTChangeOrder2) {
            QueryResult qr = ChangeHelper2.service.getChangeablesBefore((WTChangeOrder2) pbo);
            boolean checkedOut = false;
            while (qr.hasMoreElements()) {
                Object o = qr.nextElement();
                if (o instanceof WTDocument) {
                    WTDocument document = (WTDocument) o;
                    contained = document.getContainer();
                } else if (o instanceof EPMDocument) {
                    EPMDocument epmDocument = (EPMDocument) o;
                    contained = epmDocument.getContainer();
                } else if (o instanceof WTPart) {
                    WTPart part = (WTPart) o;
                    contained = part.getContainer();
                } else if (o instanceof ProcessEnvelope) {
                    ProcessEnvelope processEnvelope = (ProcessEnvelope) o;
                    contained = processEnvelope.getContainer();
                } else if (o instanceof MPMProcessPlan) {
                    MPMProcessPlan processPlan = (MPMProcessPlan) o;
                    contained = processPlan.getContainer();
                }
                if (o instanceof Workable && WorkInProgressHelper.isCheckedOut((Workable) o)) {
                    checkedOut = true;
                    break;
                }
            }
            if (checkedOut) {
                throw new NmException("ext.casc.workflow.workflowResource", "workflow.workitem.docIsCheckout", null);
            }
        }
        if (pbo instanceof WTDocument) {
            WTDocument document = (WTDocument) pbo;
            contained = document.getContainer();
        } else if (pbo instanceof WTPart) {
            WTPart part = (WTPart) pbo;
            contained = part.getContainer();
        } else if (pbo instanceof EPMDocument) {
            EPMDocument epmDocument = (EPMDocument) pbo;
            contained = epmDocument.getContainer();
        } else if (pbo instanceof ManagedBaseline) {
            ManagedBaseline mbl = (ManagedBaseline) pbo;
            contained = mbl.getContainer();
        } else if (pbo instanceof WorkPackage) {
            WorkPackage wp = (WorkPackage) pbo;
            contained = wp.getContainer();
        } else if (pbo instanceof ProcessEnvelope) {
            ProcessEnvelope processEnvelope = (ProcessEnvelope) pbo;
            contained = processEnvelope.getContainer();
        } else if (pbo instanceof MPMProcessPlan) {
            MPMProcessPlan processPlan = (MPMProcessPlan) pbo;
            contained = processPlan.getContainer();
        }else if (pbo instanceof ChangePackaged) {
            ChangePackaged changePackaged = (ChangePackaged)pbo;
            contained = changePackaged.getContainer();
        } else if(pbo instanceof WTChangeRequest2) {
            WTChangeRequest2 request2 = (WTChangeRequest2) pbo;
            contained = request2.getContainer();
        } else if(pbo instanceof WTAnalysisActivity) {
            WTAnalysisActivity analysisActivity = (WTAnalysisActivity) pbo;
            contained = analysisActivity.getContainer();
        }

        loopRole(pbo, contained, internalValues, values,workItem);
        SessionServerHelper.manager.setAccessEnforced(falg);
        return ret;
    }

    private static void loopRole(Persistable pbo, WTContained contained, ArrayList<String> internalValues,
            ArrayList<String> values,WorkItem workItem)
            throws WTException {
        ReferenceFactory rf = new ReferenceFactory();
        StringBuffer optionValue = null;
        String black = "    ";
        ContainerTeamManaged teamManaged = (ContainerTeamManaged) contained;
        if(teamManaged==null) return ;
        ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(teamManaged);

        OrgContainer orgCon = ProcessUtil.getOrgContainer();
        ContainerTeam shareContainerTeam = ContainerTeamHelper.service.getSharedTeamByName(orgCon, "产品共享团队");
        Vector<Role> vector = containerTeam.getRoles();
        Iterator<Role> iterator = vector.iterator();
        Set<String> tempRoles = new HashSet<String>();
        Role role = null;
        List<WTUser> users = null;

        WfActivity activity = (WfActivity) workItem.getSource().getObject();
        if("设置分发部门和份数".equals(activity.getName())){//
            Role tempRole = Role.toRole("CHEJIANZHUREN");//
            ArrayList<WTPrincipalReference> allUser = containerTeam.getAllPrincipalsForTarget(tempRole);
            optionValue = new StringBuffer();
            users = new ArrayList<WTUser>();
            for (WTPrincipalReference ref : allUser) {
                Persistable per = ref.getObject();
                if (per instanceof WTUser) {
                    WTUser user = (WTUser) per;
                    users.add(user);
                }
                if (per instanceof WTGroup) {
                    getUserFromWTGroup((WTGroup) per, users);
                }
            }
            Set<String > tempusers = new HashSet<String>();
            for(WTUser user:users){
                String uoid = rf.getReferenceString(user);
                if(!tempusers.contains(uoid)) {
                    tempusers.add(uoid);
                    if (uoid != null) {
                        if (optionValue.length() > 0) {
                            optionValue.append(';');
                        }
                        String fullName = user.getFullName();
                        if (fullName.contains(",")) {
                            fullName = fullName.replaceAll(",", "");
                        }
                        String userStr = user.getName() + "(" + fullName + ")";
                        optionValue.append(uoid).append(',').append(userStr);
                    }
                }
            }
            values.add(black + tempRole.getDisplay(Locale.CHINA));
            internalValues.add(optionValue.toString());
            return ;
        } else if("工时定额编制".equals(activity.getName())) {
            users = new ArrayList<WTUser>();
            optionValue = new StringBuffer();
            WTGroup group = AccessAdminUtil.getGroupByName("执行经理");
            if(group != null) {
                getUserFromWTGroup(group, users);
            }
            Set<String > tempusers = new HashSet<String>();
            for(WTUser user:users){
                String uoid = rf.getReferenceString(user);
                if(!tempusers.contains(uoid)) {
                    tempusers.add(uoid);
                    if (uoid != null) {
                        if (optionValue.length() > 0) {
                            optionValue.append(';');
                        }
                        String fullName = user.getFullName();
                        if (fullName.contains(",")) {
                            fullName = fullName.replaceAll(",", "");
                        }
                        String userStr = user.getName() + "(" + fullName + ")";
                        optionValue.append(uoid).append(',').append(userStr);
                    }
                }
            }
            values.add(black + "工时审核员");
            internalValues.add(optionValue.toString());
            return ;
        }


        while (iterator.hasNext()) {
            role = iterator.next();

            // 过滤角色
            String name = role.getDisplay(Locale.CHINA);
            // System.out.println("----------role name:" + role.getDisplay());
            if (name.equals(Constants.ROLE_DISABLE_CAI) || name.equals(Constants.ROLE_DISABLE_CAII)
                    || name.equals(Constants.ROLE_DISABLE_CAIII)
                    || name.equals(Constants.ROLE_DISABLE_CHANGEREQUESTREVIEWBOARD)
                    || name.equals(Constants.ROLE_DISABLE_COLLABORATIONMANAGER)
                    || name.equals(Constants.ROLE_DISABLE_GUEST)
                    || name.equals(Constants.ROLE_DISABLE_OPTIONADMINISTRATOR)
                    || name.equals(Constants.ROLE_DISABLE_PACKAGECREATOR)
                    || name.equals(Constants.ROLE_DISABLE_PROMOTIONAPPROVERS)
                    || name.equals(Constants.ROLE_DISABLE_PROMOTIONREVIEWERS)
                    || name.equals(Constants.ROLE_DISABLE_VARIANCEAPPROVERS)) {
                continue;
            }

            tempRoles.add(name);

            List<String > tempusers = new ArrayList<String>();

            ArrayList<WTPrincipalReference> allUser = containerTeam.getAllPrincipalsForTarget(role);
            ArrayList<WTPrincipalReference> allShareUser = new  ArrayList<WTPrincipalReference>();
            if(shareContainerTeam!=null){
            	 allShareUser = shareContainerTeam.getAllPrincipalsForTarget(role);
            }

            optionValue = new StringBuffer();
            users = new ArrayList<WTUser>();
            for (WTPrincipalReference ref : allUser) {
                Persistable per = ref.getObject();
                if (per instanceof WTUser) {
                    WTUser user = (WTUser) per;
                    String uoid = rf.getReferenceString(user);
                    tempusers.add(uoid);
                    if (uoid != null) {
                        if (optionValue.length() > 0) {
                            optionValue.append(';');
                        }
                        String fullName = user.getFullName();
                        if (fullName.contains(",")) {
                            fullName = fullName.replaceAll(",", "");
                        }
                        String userStr = user.getName()+"("+fullName+")";
                        optionValue.append(uoid).append(',').append(userStr);
                    }
                }
                if (per instanceof WTGroup) {
                    getUserFromWTGroup((WTGroup) per, users);
                }
            }

            //本地团队有得角色，共享团队也有得角色相应的成员
            if(allShareUser!=null&&!allShareUser.isEmpty()){
            	for (WTPrincipalReference ref : allShareUser) {
                    Persistable per = ref.getObject();
                    if (per instanceof WTUser) {
                        WTUser user = (WTUser) per;
                        String uoid = rf.getReferenceString(user);
                        if(!tempusers.contains(uoid)){
	                        if (uoid != null) {
	                            if (optionValue.length() > 0) {
	                                optionValue.append(';');
	                            }
	                            String fullName = user.getFullName();
	                            if (fullName.contains(",")) {
	                                fullName = fullName.replaceAll(",", "");
	                            }
	                            String userStr = user.getName()+"("+fullName+")";
	                            optionValue.append(uoid).append(',').append(userStr);
	                        }
                        }
                    }
                    if (per instanceof WTGroup) {
                        getUserFromWTGroup((WTGroup) per, users);
                    }
                }
            }
            for(WTUser user:users){
                String uoid = rf.getReferenceString(user);
                if(!tempusers.contains(uoid)){
                    if (uoid != null) {
                        if (optionValue.length() > 0) {
                            optionValue.append(';');
                        }
                        String fullName = user.getFullName();
                        if (fullName.contains(",")) {
                            fullName = fullName.replaceAll(",", "");
                        }
                        String userStr = user.getName()+"("+fullName+")";
                        optionValue.append(uoid).append(',').append(userStr);
                    }
                }
           }
            values.add(black + role.getDisplay(Locale.CHINA));
            internalValues.add(optionValue.toString());

        }

       //本地团队中没有的角色，共享团队有的角色和成员
        Vector<Role> sharevector = shareContainerTeam.getRoles();
        Iterator<Role> shareiterator = sharevector.iterator();
        while (shareiterator.hasNext()) {
            role = shareiterator.next();
            // 过滤角色
            String name = role.getDisplay(Locale.CHINA);
            // System.out.println("----------role name:" + role.getDisplay());
            if (name.equals(Constants.ROLE_DISABLE_CAI) || name.equals(Constants.ROLE_DISABLE_CAII)
                    || name.equals(Constants.ROLE_DISABLE_CAIII)
                    || name.equals(Constants.ROLE_DISABLE_CHANGEREQUESTREVIEWBOARD)
                    || name.equals(Constants.ROLE_DISABLE_COLLABORATIONMANAGER)
                    || name.equals(Constants.ROLE_DISABLE_GUEST)
                    || name.equals(Constants.ROLE_DISABLE_OPTIONADMINISTRATOR)
                    || name.equals(Constants.ROLE_DISABLE_PACKAGECREATOR)
                    || name.equals(Constants.ROLE_DISABLE_PROMOTIONAPPROVERS)
                    || name.equals(Constants.ROLE_DISABLE_PROMOTIONREVIEWERS)
                    || name.equals(Constants.ROLE_DISABLE_VARIANCEAPPROVERS)) {
                continue;
            }

           if(!tempRoles.contains(name)){
	            List<String > tempusers = new ArrayList<String>();
	            ArrayList<WTPrincipalReference> allShareUser = shareContainerTeam.getAllPrincipalsForTarget(role);
	            optionValue = new StringBuffer();
	            users = new ArrayList<WTUser>();
	            for (WTPrincipalReference ref : allShareUser) {
	                Persistable per = ref.getObject();
	                if (per instanceof WTUser) {
	                    WTUser user = (WTUser) per;
	                    String uoid = rf.getReferenceString(user);
	                    tempusers.add(uoid);
	                    if (uoid != null) {
	                        if (optionValue.length() > 0) {
	                            optionValue.append(';');
	                        }
	                        String fullName = user.getFullName();
	                        if (fullName.contains(",")) {
	                            fullName = fullName.replaceAll(",", "");
	                        }
	                        String userStr = user.getName()+"("+fullName+")";
	                        optionValue.append(uoid).append(',').append(userStr);
	                    }
	                }
	                if (per instanceof WTGroup) {
	                    getUserFromWTGroup((WTGroup) per, users);
	                }
	            }
	            for(WTUser user:users){
	                String uoid = rf.getReferenceString(user);
	                if(!tempusers.contains(uoid)){
	                    if (uoid != null) {
	                        if (optionValue.length() > 0) {
	                            optionValue.append(';');
	                        }
	                        String fullName = user.getFullName();
	                        if (fullName.contains(",")) {
	                            fullName = fullName.replaceAll(",", "");
	                        }
	                        String userStr = user.getName()+"("+fullName+")";
	                        optionValue.append(uoid).append(',').append(userStr);
	                    }
	                }
	           }
	            values.add(black + role.getDisplay(Locale.CHINA));
	            internalValues.add(optionValue.toString());

	        }
        }

    }

    /**
     * 循环组并且获取组里面的用户
     *
     * @param group
     * @param list
     * @throws WTException
     */
    public static void getUserFromWTGroup(WTGroup group, List list) throws WTException {
        if (group == null || list == null) {
            return;
        }
        Enumeration member = group.members();
        while (member.hasMoreElements()) {
            WTPrincipal principal = (WTPrincipal) member.nextElement();
            if (principal instanceof WTUser) {
                list.add((WTUser) principal);
            } else if (principal instanceof WTGroup) {
                getUserFromWTGroup((WTGroup) principal, list);
            }
        }
    }

    /**
     * 当任务是定义流程角色参与者时, 显示供选择的分组信息 用户分组select名称为groups, 用户名称select名称为users<br>
     * groups.option.text: 分组名称/产品容器角色名称 + "搜索到的用户"<br>
     * groups.option.value: 该分组内的用户, e.g. "u1,用户名1;u2,用户名2;u3,用户名3"<br>
     * users.option.text: user's fullName/principal name<br>
     * users.option.value: principal object id<br>
     *
     * @throws
     * @throws Exception
     * @throws
     */
    public static GuiComponent getGroups(NmCommandBean cb) throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "getGroups";
            return (GuiComponent) RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null,
                    new Class[] { NmCommandBean.class }, new Object[] { cb });
        }

        ArrayList<String> internalValues = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();
        ArrayList<String> selectedValues = new ArrayList<String>();

        ComboBox ret = new ComboBox(internalValues, values, selectedValues);
        ret.setName("Groups");
        ret.setId("groups");
        ret.setSize(21);
        ret.addJsAction("onclick", "javascript:listGroupUsers()");

        try {
            Vector lvlgrp1 = new Vector();
            HashMap lvl2map = new HashMap();
            HashMap lvl3map = new HashMap();
            HashMap groupUsers = new HashMap();
            List<WTUser> allUsers = null;
            boolean accessEnforced = SessionServerHelper.manager.setAccessEnforced(false);
            try {
                getGroupsAndUsers(cb, lvlgrp1, lvl2map, lvl3map, groupUsers);

                allUsers = getAllUsersFromProductTeam(cb);

                // 过滤不是本产品团队下的user
                Iterator iterator = groupUsers.keySet().iterator();
                List<WTUser> removeUsers = null;
                while (iterator.hasNext()) {
                    Object object = iterator.next();
                    ArrayList<WTUser> users = (ArrayList<WTUser>) groupUsers.get(object);
                    removeUsers = new ArrayList<WTUser>();
                    for (WTUser wtUser : users) {
                        if (!allUsers.contains(wtUser)) {
                            removeUsers.add(wtUser);
                            // users.remove(wtUser);
                        }
                    }
                    for (WTUser wtUser : removeUsers) {
                        users.remove(wtUser);
                    }

                }
                // end
            } finally {
                SessionServerHelper.manager.setAccessEnforced(accessEnforced);
            }

            // groups go here
            ReferenceFactory rf = new ReferenceFactory();
            for (int i = 0; i < lvlgrp1.size(); i++) {

                WTGroup group = (WTGroup) lvlgrp1.get(i);
                int j = 0;
                LoopGroup(cb, group, internalValues, values, 1, allUsers);

                /**
                 * ArrayList users = (ArrayList) groupUsers.get(lvlgrp1.get(i));
                 * ArrayList lvl2grp = (ArrayList) lvl2map.get(lvlgrp1.get(i));
                 * if (isEmptyGroup(lvl2grp, users)) {
                 * continue;
                 * }
                 * Collections.sort(users, CmComparators.GB_WTUSER_COMPARATOR);
                 * StringBuffer optionValue = new StringBuffer();
                 * for (int j = 0; users != null && j < users.size(); j++) {
                 * WTUser u = (WTUser) users.get(j);
                 * String uoid = rf.getReferenceString(u);
                 * if (uoid != null) {
                 * if (optionValue.length() > 0) {
                 * optionValue.append(';');
                 * }
                 * optionValue.append(uoid).append(',').append(u.getFullName());
                 * }
                 * }
                 * values.add((String) lvlgrp1.get(i));
                 * internalValues.add(optionValue.toString());
                 *
                 * if (lvl2grp == null) {
                 * continue;
                 * }
                 * for (int k = 0; k < lvl2grp.size(); k++) {
                 * users = (ArrayList) groupUsers.get(lvl2grp.get(k));
                 * ArrayList lvl3grp = (ArrayList) lvl3map.get(lvl2grp.get(k));
                 * if (isEmptyGroup(lvl3grp, users)) {
                 * continue;
                 * }
                 * Collections.sort(users, CmComparators.GB_WTUSER_COMPARATOR);
                 * optionValue = new StringBuffer();
                 * for (int j = 0; users != null && j < users.size(); j++) {
                 * WTUser u = (WTUser) users.get(j);
                 * String uoid = rf.getReferenceString(u);
                 * if (uoid != null) {
                 * if (optionValue.length() > 0) {
                 * optionValue.append(';');
                 * }
                 * optionValue.append(uoid).append(',').append(u.getFullName());
                 * }
                 * }
                 * values.add("　　　　" + lvl2grp.get(k));
                 * internalValues.add(optionValue.toString());
                 *
                 * if (lvl3grp == null) {
                 * continue;
                 * }
                 * for (int m = 0; m < lvl3grp.size(); m++) {
                 * users = (ArrayList) groupUsers.get(lvl3grp.get(m));
                 * if (isEmptyGroup(null, users)) {
                 * continue;
                 * }
                 * Collections.sort(users, CmComparators.GB_WTUSER_COMPARATOR);
                 * optionValue = new StringBuffer();
                 * for (int j = 0; users != null && j < users.size(); j++) {
                 * WTUser u = (WTUser) users.get(j);
                 * String uoid = rf.getReferenceString(u);
                 * if (uoid != null) {
                 * if (optionValue.length() > 0) {
                 * optionValue.append(';');
                 * }
                 * optionValue.append(uoid).append(',').append(u.getFullName());
                 * }
                 * }
                 * values.add("　　　　　　　　" + lvl3grp.get(m));
                 * internalValues.add(optionValue.toString());
                 * }
                 * }
                 **/
            }
            Collections.reverse(values);
            Collections.reverse(internalValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    private static void LoopGroup(NmCommandBean cb, WTGroup group, ArrayList<String> internalValues,
            ArrayList<String> values, int level, List<WTUser> allUsers)
            throws WTException {
        Enumeration member = OrganizationServicesHelper.manager.members(group, false);
        ReferenceFactory rf = new ReferenceFactory();
        StringBuffer optionValue = new StringBuffer();
        String black = "    ";
        int j = 0;
        List users = new ArrayList();
        while (member.hasMoreElements()) {
            WTPrincipal principal = (WTPrincipal) member.nextElement();
            users.add(principal);
        }
        if ("模具中心资料管理员".equals(group.getName()) || "研发部资料管理员".equals(group.getName())) {
            List<WTUser> zLGLYUsers = getUsersForZLGLY(cb);
            users.addAll(zLGLYUsers);
        }
        for (int i = 0; i < users.size(); i++) {
            WTPrincipal principal = (WTPrincipal) users.get(i);
            if (principal instanceof WTUser) {
                WTUser user = (WTUser) principal;
                if (!allUsers.contains(user)) {
                    continue;
                }
                String uoid = rf.getReferenceString(user);
                if (uoid != null) {
                    if (optionValue.length() > 0) {
                        optionValue.append(';');
                    }
                    optionValue.append(uoid).append(',').append(user.getFullName());
                }
            } else if (principal instanceof WTGroup) {
                level += 1;
                LoopGroup(cb, (WTGroup) principal, internalValues, values, level, allUsers);
                level -= 1;
            }
        }
        for (int i = 3; i <= level; i++) {
            black = black + black + "|--";
        }
        if (level == 2) {
            black = black + "|--";
        }
        if (level == 1) {
            black = "";
        }
        values.add(black + group.getName());
        internalValues.add(optionValue.toString());
    }

    public static GuiComponent getUsers(NmCommandBean cb) throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "getUsers";
            return (GuiComponent) RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null,
                    new Class[] { NmCommandBean.class }, new Object[] { cb });
        }

        ArrayList<String> internalValues = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();
        ArrayList<String> selectedValues = new ArrayList<String>();

        ComboBox ret = new ComboBox(internalValues, values, selectedValues);
        ret.setMultiSelect(cb.getMap().get("UsersMultiSelect") != null);
        ret.setName("Users");
        ret.setId("users");
        ret.setSize(21);

        return ret;
    }

    /**
     * 当任务时定义流程角色参与者时, 显示各角色的参与者, 每个参与者为一个连接, 点击移除该参与者
     *
     * 查看活动变量<b>"设定角色"</b>, 为逗号分开的角色列表.升级者和提交者被自动排除<br>
     * 如果已定义: 选择指定的流程角色的参与者<br>
     * 如果未定义: 选择全部流程角色的参与者<br>
     *
     * 如果定义了变量<b>"必需角色"</b>则该角色必须选择参与者
     *
     * @param pp
     *            *
     * @param locale
     *            *
     * @param os
     *            *
     * @throws Exception
     */
    public static GuiComponent getRolesAndParticipators(NmCommandBean cb) throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "getRolesAndParticipators";
            return (GuiComponent) RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null,
                    new Class[] { NmCommandBean.class }, new Object[] { cb });
        }
        GUIComponentArray ret = new GUIComponentArray();
        ret.addHiddenField(KEY_WF_AUGMENT_ROLES_FORM, "true");
        boolean accessEnforced = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            WorkItem workItem = getWorkItem(cb);

            Vector rolesDefinedVec = new Vector();

            WfActivity wfAct = (WfActivity) workItem.getSource().getObject();
            Object pbo = wfAct.getParentProcess().getContext().getValue("primaryBusinessObject");
            String wfName = "";
            List<Role> roleList = new ArrayList<Role>();
            WfContainer wfcont = (WfContainer) wfAct.getParentProcessRef().getObject();
            WfProcess process = null;
            if (wfcont instanceof WfBlock) {
                WfBlock wfBlock = (WfBlock) wfcont;
                process = wfBlock.getParentProcess();
            } else {
                process = (WfProcess) wfcont;
            }

            wfName = process.getName();
            WfProcessTemplate wfprocesstemplate = (WfProcessTemplate) process.getTemplate().getObject();
            Vector vector = WfDefinerHelper.service.getProcessRoles(wfprocesstemplate);
            for (int i = 0; i < vector.size(); i++) {
                Object objRole = vector.get(i);
                if (objRole instanceof Role) {
                    Role role = (Role) objRole;
                    roleList.add(role);
                }
            }
            WTContained contained = null;
            Team team = null;
            WTChangeOrder2 changeOrder2 = null;
            boolean isSop = false;
            if (pbo instanceof WTChangeOrder2) {
                changeOrder2 = (WTChangeOrder2) pbo;
                isSop = SopWorkflowUtil.isSop(changeOrder2);
                team = (Team) changeOrder2.getTeamId().getObject();
                Vector vecRole = team.getRoles();
                for (int i = 0; i < vecRole.size(); i++) {
                    Role wfRole = (Role) vecRole.get(i);
                    // 过滤掉系统默认角色
                    String roleValue = wfRole.getStringValue();
                    if (!(roleValue.endsWith(Constants.CHANGE_ADMINISTRATOR_I)
                            || roleValue.endsWith(Constants.CHANGE_ADMINISTRATOR_II)
                            || roleValue.endsWith(Constants.CHANGE_REQUEST_REVIEW_BOARD)
                            || roleValue.endsWith(Constants.ECR_AUTHOR) || roleValue.endsWith(Constants.PR_AUTHOR)
                            || roleValue.endsWith(Constants.VARIANCE_AUTHOR) || roleValue.endsWith(Constants.REVIEWER))) {
                        // rolesDefinedVec.add(wfRole);
                    }
                }

                QueryResult qr = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
                if (qr.hasMoreElements()) {
                    contained = (WTContained) qr.nextElement();
                }

                // 获取流程primaryBusinessObject的团队
            } else if (pbo instanceof TeamManaged) {
                team = (Team) process.getTeamId().getObject();
            }
            HashMap rolePrincipalListMap = null;
            Vector allRoles = null;
            if (team == null) {
                ContainerTeam conteam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) contained
                        .getContainer());
                rolePrincipalListMap = (HashMap) conteam.getRolePrincipalMap();
                allRoles = conteam.getRoles();
            } else {
                rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
                allRoles = rolesDefinedVec;
            }
            // 获取团队角色的中英文对照表
            HashMap rolesValue = new HashMap();
            HashMap rolesDisp = new HashMap();
            for (int i = 0; allRoles != null && i < allRoles.size(); i++) {
                Role role = (Role) allRoles.get(i);
                rolesValue.put(role.toString(), role);
                rolesDisp.put(role.getDisplay(Locale.CHINA), role);
            }
            HashMap rolesValueAll = new HashMap();
            HashMap rolesDispAll = new HashMap();
            Role[] roleArray = Role.getRoleSet();
            for (int i = 0; i < roleArray.length; i++) {
                Role role = roleArray[i];
                rolesValueAll.put(role.toString(), role);
                rolesDispAll.put(role.getDisplay(Locale.CHINA), role);
            }

            // 如果没定义要选择参与者的角色, 默认全部有权限的角色
            if (rolesDefinedVec.size() <= 0) {
                HashMap permissionMap = WorkflowHelper.service.getPermissionMap(workItem);
                //兼容错误数据
                WfAssignedActivity wa = (WfAssignedActivity) workItem.getSource().getObject();
                if(wa != null && wa.getName().indexOf("工时定额编制") > -1 && (permissionMap == null || permissionMap.size() == 0)) {
                    permissionMap = new HashMap();
                    Role role = Role.toRole("GONGSHISHENHEYUAN");
                    permissionMap.put(role, true);
                }
                Collection col = permissionMap == null ? new Vector() : permissionMap.keySet();
                rolesDefinedVec.addAll(col);
            }

            //添加型号结算负责人
            if(!wfprocesstemplate.getName().equals(Constants.WFN_PROCESS_DOCUMENT)){
            	 rolesDefinedVec.add(Role.toRole("XINGHAOJIESUANFUZEREN"));
            }

            TextDisplayComponent textDisplayComp = new TextDisplayComponent("");
            textDisplayComp.setCheckXSS(false);
            StringBuffer buffer = new StringBuffer();
            buffer.append("<div id='roleListArea'>\n")
                    .append("<table border=0 width=100% cellpadding=3 cellspacing=3>");

            // 按照流程节点顺序给显示的角色排序
            List<Role> sortedRole = new ArrayList<Role>(rolesDefinedVec);
            List<Role> sortedRole2 = new ArrayList<Role>();
            List<String> list = standardSortRoles();
            for (String roleName : list) {
                for (int i = 0; i < rolesDefinedVec.size(); i++) {
                    Role temRole = (Role) rolesDefinedVec.get(i);
                    if (temRole!=null && roleName.equals(temRole.getDisplay(Locale.CHINA))) {
                        sortedRole2.add(temRole);
                        sortedRole.remove(temRole);
                    }
                }
            }
            sortedRole2.addAll(sortedRole2.size(), sortedRole);
            rolesDefinedVec = new Vector(sortedRole2);

            // 显示每个要定义的角色的参与者列表
            for (int i = 0; i < rolesDefinedVec.size(); i++) {
                Role role = (Role) rolesDefinedVec.get(i);
                if (!roleList.contains(role)) {
                    continue;
                }

                // 输出角色定义区域
                String roleDisp = role.getDisplay(Locale.CHINA);
                if(isSop && ("工时定额员".equals(roleDisp) || "外部会签者".equals(roleDisp))){
                    continue;
                }
                String roleDisp1 = roleDisp;
                // 内部会签者和外部会签者 可选
                // 参与人 可选
                if (Constants.ROLE_NEIBUHUIQIANZHE.equals(roleDisp) || Constants.ROLE_WAIBUHUIQIANZHE.equals(roleDisp)
                        || Constants.ROLE_ZHIPAIGONGYIYUANZHE.equals(roleDisp)
                        || Constants.ROLE_CANYUREN.equals(roleDisp)||"主管领导".equals(roleDisp)|| Constants.ROLE_DAYINZHE.equals(roleDisp)) {
                    buffer.append("<input autocomplete='off' type=hidden name=noneededrole value='").append(roleDisp).append("'>");
                    roleDisp1 = roleDisp1 + ": ";
                }else if (wfName.indexOf("三维工艺签审流程")>-1&&Constants.ROLE_BIAOSHENZHE.equals(roleDisp) ) {
                    buffer.append("<input autocomplete='off' type=hidden name=noneededrole value='").append(roleDisp).append("'>");
                    roleDisp1 = roleDisp1 + ": ";
                } else if (wfName.indexOf("文档签审流程")>-1){
                    if (Constants.ROLE_BIAOSHENZHE.equals(roleDisp) || Constants.ROLE_ZHIPAIGONGYIZUZHANGZHE.equals(roleDisp) || Constants.ROLE_XINXIHUASHUJVYUAN.equals(roleDisp)) {
                        buffer.append("<input autocomplete='off' type=hidden name=noneededrole value='").append(roleDisp).append("'>");
                        roleDisp1 = roleDisp1 + ": ";
                    } else {
                        buffer.append("<input autocomplete='off' type=hidden name=neededrole value='").append(roleDisp).append("'>");
                        roleDisp1 = "*&nbsp;" + roleDisp1 + ": ";
                    }
                } else if (wfName.indexOf("材料定额流程")>-1){
                    if (Constants.ROLE_ZHURENGONGYISHI.equals(roleDisp)) {
                        buffer.append("<input autocomplete='off' type=hidden name=neededrole value='").append(roleDisp).append("'>");
                        roleDisp1 = roleDisp1 + ": ";
                    } else {
                        buffer.append("<input autocomplete='off' type=hidden name=neededrole value='").append(roleDisp).append("'>");
                        roleDisp1 = "*&nbsp;" + roleDisp1 + ": ";
                    }
                } else {
                    buffer.append("<input autocomplete='off' type=hidden name=neededrole value='").append(roleDisp).append("'>");
                    roleDisp1 = "*&nbsp;" + roleDisp1 + ": ";
                }
                buffer.append("<tr><td class=tabledatafont>").append(
                        "<input type=button value='添加 =>' onclick=\"appendUsers('" + roleDisp + "')\"></td>");
                buffer.append("<td align=right nowrap><FONT class=tabledatafont> <b>" + roleDisp1 + "</b></FONT></td>");
                buffer.append("<td align=left width=100% class=tabledatafont nowrap>");
                buffer.append("<div name='" + roleDisp + "'>");

                // 显示参与者列表
                ReferenceFactory rf = new ReferenceFactory();
                ArrayList principalList = (ArrayList) rolePrincipalListMap.get(role);

                for (int j = 0; principalList != null && j < principalList.size(); j++) {
                    WTPrincipal principal = (WTPrincipal) ((WTPrincipalReference) principalList.get(j)).getPrincipal();
                    String userOid = rf.getReferenceString(principal);
                    String name = principal instanceof WTUser ? ((WTUser) principal).getFullName() : principal.getName();
                    String desc = name + "(" + principal.getName() + ")";
                    // 构造用户定义区域
                    String spanId = "角色." + roleDisp + "." + userOid + "." + name;
                    String span = "<span id='" + spanId + "'>\n" + "<a href=\"javascript:removeUser('" + spanId
                            + "')\" " + "title='移除参与者: " + desc + "'>" + name + "</a>" + "<input autocomplete='off' type=hidden name='角色."
                            + roleDisp + "' value='" + userOid + "'>" + "</span>";
                    buffer.append(span);
                }

                buffer.append("</div></td></tr>");
            }
            buffer.append("</table></div>");
            textDisplayComp.setValue(buffer.toString());
            ret.addGUIComponent(textDisplayComp);
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessEnforced);
        }
        return ret;
    }

    /**
     * 当任务是用于发布文件时，用户选择需要发布的用户
     *
     * add by LongXiuChuan
     *
     * @param cb
     * @return
     * @throws Exception
     */
    public static GuiComponent getGroupsAndParticipators(NmCommandBean cb) throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "getGroupsAndParticipators";
            return (GuiComponent) RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null,
                    new Class[] { NmCommandBean.class }, new Object[] { cb });
        }

        GUIComponentArray ret = new GUIComponentArray();
        ret.addHiddenField(KEY_WF_AUGMENT_GROUPS_FORM, "true");
        boolean accessEnforced = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            WorkItem workItem = getWorkItem(cb);

            Vector rolesDefinedVec = new Vector();

            WfActivity wfAct = (WfActivity) workItem.getSource().getObject();
            Object pbo = wfAct.getParentProcess().getContext().getValue("primaryBusinessObject");

            WTContained contained = null;
            Team team = null;

            if (pbo instanceof WTChangeOrder2) {
                WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
                QueryResult qResult = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
                Enumeration enumeration = qResult.getEnumeration();
                Object obj = null;
                WTDocument doc = null;
                while (enumeration.hasMoreElements()) {
                    obj = enumeration.nextElement();
                    if (obj instanceof WTDocument) {
                        doc = (WTDocument) obj;
                    }
                }
            }

            // HashMap rolePrincipalListMap=null;
            // Vector allRoles =null;
            // if(team==null){
            // ContainerTeam conteam=
            // ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged)contained.getContainer());
            // rolePrincipalListMap=(HashMap)conteam.getRolePrincipalMap();
            // allRoles = conteam.getRoles();
            // }else{
            // rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
            // // allRoles = TeamHelper.service.findRoles(team);
            // allRoles=rolesDefinedVec;
            // }
            //
            // Debug.P("=========allRoles.size==========="+allRoles.size());
            // // 获取团队角色的中英文对照表
            // HashMap rolesValue = new HashMap();
            // HashMap rolesDisp = new HashMap();
            // for (int i = 0; allRoles != null && i < allRoles.size(); i++) {
            // Role role = (Role) allRoles.get(i);
            // rolesValue.put(role.toString(), role);
            // rolesDisp.put(role.getDisplay(Locale.SIMPLIFIED_CHINESE), role);
            // }
            // HashMap rolesValueAll = new HashMap();
            // HashMap rolesDispAll = new HashMap();
            // Role[] roleArray = Role.getRoleSet();
            // for (int i = 0; i < roleArray.length; i++) {
            // Role role = roleArray[i];
            // rolesValueAll.put(role.toString(), role);
            // rolesDispAll.put(role.getDisplay(Locale.SIMPLIFIED_CHINESE), role);
            // }

            // 如果没定义要选择参与者的角色, 默认全部有权限的角色
            // if (rolesDefinedVec.size() <= 0) {
            // HashMap permissionMap = WorkflowHelper.service.getPermissionMap(workItem);
            // Collection col = permissionMap == null ? new Vector() : permissionMap.keySet();
            // rolesDefinedVec.addAll(col);
            // }
            TextDisplayComponent textDisplayComp = new TextDisplayComponent("");
            textDisplayComp.setCheckXSS(false);
            StringBuffer buffer = new StringBuffer();
            buffer.append("<div id='roleListArea'>\n")
                    .append("<table border=0 width=100% cellpadding=3 cellspacing=3>");

            // 输出角色定义区域
            // String roleDisp = role.getDisplay(cb.getLocale());
            String roleDisp = "userlists";
            // String roleDisp1 = roleDisp;
            buffer.append("<input autocomplete='off' type=hidden name=neededrole value='").append(roleDisp).append("'>");
            // roleDisp1 = "*&nbsp;" + roleDisp1 + ": ";
            buffer.append("<tr><td class=tabledatafont>").append(
                    "<input type=button value='添加 =>' onclick=\"appendUsers('" + roleDisp + "')\"></td>");
            buffer.append("<td align=right nowrap><FONT class=tabledatafont> <b>" + "接收者:" + "</b></FONT></td>");
            buffer.append("<td align=left width=100% class=tabledatafont nowrap>");
            buffer.append("<div name='" + roleDisp + "'>");

            // 显示参与者列表
            // ReferenceFactory rf = new ReferenceFactory();
            // ArrayList principalList = (ArrayList) rolePrincipalListMap.get(role);
            // for (int j = 0; principalList != null && j < principalList.size(); j++) {
            // WTPrincipal principal = (WTPrincipal) ((WTPrincipalReference) principalList.get(j)).getPrincipal();
            // String userOid = rf.getReferenceString(principal);
            // String name = principal instanceof WTUser ? ((WTUser) principal).getFullName() : principal.getName();
            // String desc = name + "(" + principal.getName() + ")";
            //
            // // 构造用户定义区域
            // String spanId = "角色." + roleDisp + "." + userOid + "." + name;
            // String span = "<span id='" + spanId + "'>\n" + "<a href=\"javascript:removeUser('" + spanId + "')\" " +
            // "title='移除参与者: " + desc + "'>" + name + "</a>" + "<input type=hidden name='角色." + roleDisp + "' value='"
            // + userOid + "'>" + "</span>";
            // buffer.append(span);
            // }
            buffer.append("</div></td></tr>");
            buffer.append("</table></div>");
            textDisplayComp.setValue(buffer.toString());
            ret.addGUIComponent(textDisplayComp);
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessEnforced);
        }
        return ret;
    }

    public static GuiComponent getRolesAndParticipators(NmCommandBean cb, String roles, boolean displayRoleName)
            throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "getRolesAndParticipators";
            return (GuiComponent) RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null,
                    new Class[] { NmCommandBean.class, String.class, Boolean.TYPE },
                    new Object[] { cb, roles, displayRoleName });
        }

        GUIComponentArray ret = new GUIComponentArray();
        ret.addHiddenField(KEY_WF_AUGMENT_ROLES_FORM, "true");
        boolean accessEnforced = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            Object pbo = getContextObj(cb);
            if (pbo != null && pbo instanceof WorkItem) {
                WorkItem workItem = (WorkItem) pbo;
                WfActivity wfAct = (WfActivity) workItem.getSource().getObject();
                pbo = wfAct.getContext().getValue("primaryBusinessObject");
            }
            if (pbo == null || !(pbo instanceof TeamManaged)) {
                return ret; // 获取流程primaryBusinessObject的团队
            }
            Team team = TeamHelper.service.getTeam((TeamManaged) pbo);
            HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);

            // 获取团队角色的中英文对照表
            HashMap rolesValue = new HashMap();
            HashMap rolesDisp = new HashMap();
            Vector allRoles = TeamHelper.service.findRoles(team);
            for (int i = 0; allRoles != null && i < allRoles.size(); i++) {
                Role role = (Role) allRoles.get(i);
                rolesValue.put(role.toString(), role);
                rolesDisp.put(role.getDisplay(Locale.CHINA), role);
            }
            HashMap rolesValueAll = new HashMap();
            HashMap rolesDispAll = new HashMap();
            Role[] roleArray = Role.getRoleSet();
            for (int i = 0; i < roleArray.length; i++) {
                Role role = roleArray[i];
                rolesValueAll.put(role.toString(), role);
                rolesDispAll.put(role.getDisplay(Locale.CHINA), role);
            }

            // 获取要选择参与者的角色定义
            Vector<Role> rolesDefinedVec = new Vector<Role>();

            Object rolesDefined = roles;
            if (rolesDefined != null) {
                String[] roleDefineds = String.valueOf(rolesDefined).split(",");
                for (int i = 0; i < roleDefineds.length; i++) {
                    String roleStr = roleDefineds[i].trim();
                    if (roleStr.length() > 0) {
                        Role role = (Role) rolesDisp.get(roleStr); // 是否角色中文名
                        if (role == null) {
                            role = (Role) rolesValue.get(roleStr); // 是否角色英文KEY
                        }
                        if (role == null) {
                            role = (Role) rolesDispAll.get(roleStr); // 是否不在已定义团队内的角色中文名
                        }
                        if (role == null) {
                            role = (Role) rolesValueAll.get(roleStr); // 是否不在已定义团队内的角色KEY
                        }
                        if (role != null && !rolesDefinedVec.contains(role)) {
                            rolesDefinedVec.add(role);
                        }
                    }
                }
            }

            // 如果没定义要选择参与者的角色, 默认全部有权限的角色
            if (rolesDefinedVec.size() <= 0) {
                rolesDefinedVec.addAll(team.getRoles());
            }
            TextDisplayComponent textDisplayComp = new TextDisplayComponent("");
            textDisplayComp.setCheckXSS(false);
            StringBuffer buffer = new StringBuffer();
            buffer.append("<div id='roleListArea'>\n")
                    .append("<table border=0 width=100% cellpadding=3 cellspacing=3>");

            // 显示每个要定义的角色的参与者列表
            for (int i = 0; i < rolesDefinedVec.size(); i++) {
                Role role = (Role) rolesDefinedVec.get(i);

                // 不选择"提交者"和"升级者"
                if (role.equals(Role.PROMOTER) || role.equals(Role.SUBMITTER) || role.equals(Role.toRole("ASSIGNEE"))) {
                    continue;
                }

                // 输出角色定义区域
                String roleDisp = role.getDisplay(Locale.CHINA);
                buffer.append("<input autocomplete='off' type=hidden name=neededrole value='").append(roleDisp).append("'>");
                buffer.append("<input autocomplete='off' type=hidden name=UPDATE_TEAM_ROLES value='").append(role.toString()).append("'>");
                String roleDisp1 = displayRoleName ? "*&nbsp;" + roleDisp + ": " : "";

                buffer.append("<tr><td class=tabledatafont>").append(
                        "<input type=button value='设置 =>' onclick=\"appendUsers('" + roleDisp + "')\"></td>");
                buffer.append("<td align=right nowrap><FONT class=tabledatafont> <b>" + roleDisp1 + "</b></FONT></td>");
                buffer.append("<td align=left width=100% class=tabledatafont>");
                buffer.append("<div name='" + roleDisp + "' nowrap>");

                // 显示参与者列表
                ReferenceFactory rf = new ReferenceFactory();
                ArrayList principalList = (ArrayList) rolePrincipalListMap.get(role);
                for (int j = 0; principalList != null && j < principalList.size(); j++) {
                    WTPrincipal principal = (WTPrincipal) ((WTPrincipalReference) principalList.get(j)).getPrincipal();
                    String userOid = rf.getReferenceString(principal);
                    String name = principal instanceof WTUser ? ((WTUser) principal).getFullName() : principal
                            .getName();
                    String desc = name + "(" + principal.getName() + ")";

                    // 构造用户定义区域
                    String spanId = "角色." + roleDisp + "." + userOid + "." + name;
                    String span = "<span id='" + spanId + "'>\n" + "<a href=\"javascript:removeUser('" + spanId
                            + "')\" " + "title='移除参与者: " + desc + "'>" + name + "</a>" + "<input autocomplete='off' type=hidden name='角色."
                            + roleDisp + "' value='" + userOid + "'>" + "</span>";
                    buffer.append(span);
                }

                buffer.append("</div></td></tr>");
            }
            buffer.append("</table></div>");
            textDisplayComp.setValue(buffer.toString());
            ret.addGUIComponent(textDisplayComp);
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessEnforced);
        }
        return ret;
    }

    /**
     * 当任务时定义流程角色参与者时, 获取分组列表和分组用户列表
     *
     * @param groups
     *            分组名列表
     * @param groupUsers
     *            分组名 --- 分组用户列表(ArrayList) map
     */
    public static void getGroupsAndUsers(NmCommandBean cb, Vector lvlgrp1, HashMap lvl2map, HashMap lvl3map,
            HashMap groupUsers) throws Exception {
        Object pbo = getContextObj(cb);
        if (pbo != null && pbo instanceof WorkItem) {
            WorkItem workItem = (WorkItem) pbo;
            WfActivity wfAct = (WfActivity) workItem.getSource().getObject();
            pbo = wfAct.getContext().getValue("primaryBusinessObject");
        }
        if (pbo == null) {
            return;
        }
        HashMap hmap = CmGroupUserCache.getGroupUser();
        ArrayList toplist = (ArrayList) hmap.get(CmGroupUserCache.KEY_LEVEL1);
        HashMap submap = (HashMap) hmap.get(CmGroupUserCache.KEY_LEVEL2);
        HashMap lastmap = (HashMap) hmap.get(CmGroupUserCache.KEY_LEVEL3);
        HashMap usrmap = (HashMap) hmap.get(CmGroupUserCache.KEY_USRLIB);

        lvlgrp1.addAll(toplist);
        lvl2map.putAll(submap);
        lvl3map.putAll(lastmap);
        groupUsers.putAll(usrmap);
    }

    private static Object getContextObj(NmCommandBean cb) throws WTException {
        Object ret = null;

        NmOid oid = cb.getPageOid() == null ? cb.getPrimaryOid() == null ? null : cb.getPrimaryOid() : cb.getPageOid();
        if (oid != null) {
            ret = oid.getRef();
        }
        return ret;
    }

    public static WorkItem getWorkItem(NmCommandBean cb) throws WTException {
        WorkItem ret = null;

        NmOid oid = cb.getPageOid() == null ? cb.getPrimaryOid() == null ? null : cb.getPrimaryOid() : cb.getPageOid();
        if (oid != null && oid.getRef() instanceof WorkItem) {
            ret = (WorkItem) oid.getRef();
        }

        return ret;
    }

    private static boolean isEmptyGroup(ArrayList subGroups, ArrayList groupUsers) {
        if (subGroups == null || subGroups.isEmpty()) {
            return groupUsers == null || groupUsers.isEmpty();
        }
        return false;
    }

    private static List<WTUser> getUsersForZLGLY(NmCommandBean cb) throws WTException {
        Object pbo = getContextObj(cb);
        List<WTUser> allUsers = new ArrayList<WTUser>();
        if (pbo != null && pbo instanceof WorkItem) {
            WorkItem workItem = (WorkItem) pbo;
            WfActivity wfAct = (WfActivity) workItem.getSource().getObject();
            Object pbo2 = wfAct.getParentProcess().getContext().getValue("primaryBusinessObject");
            WTContainer wfcont = null;
            if (pbo2 instanceof WTChangeOrder2) {
                WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo2;
                QueryResult qr = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
                while (qr.hasMoreElements()) {
                    Object o = qr.nextElement();
                    if (o instanceof WTDocument) {
                        WTDocument document = (WTDocument) o;
                        wfcont = document.getContainer();
                    } else if (o instanceof EPMDocument) {
                        EPMDocument epmdoc = (EPMDocument) o;
                        wfcont = epmdoc.getContainer();
                    } else if (o instanceof WTPart) {
                        WTPart part = (WTPart) o;
                        wfcont = part.getContainer();
                    }
                }
            }
            if (pbo2 instanceof WTDocument) {
                WTDocument document = (WTDocument) pbo2;
                wfcont = document.getContainer();
            } else if (pbo2 instanceof EPMDocument) {
                EPMDocument epmdoc = (EPMDocument) pbo2;
                wfcont = epmdoc.getContainer();
            } else if (pbo2 instanceof WTPart) {
                WTPart part = (WTPart) pbo2;
                wfcont = part.getContainer();
            }
            ContainerTeam conteam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wfcont);
            HashMap map = conteam.getAllMembers();

            Iterator iterator = map.keySet().iterator();
            WTUser user = null;
            WTPrincipalReference reference = null;
            WTPrincipal principal = null;
            Role role = Role.toRole("资料管理员");
            while (iterator.hasNext()) {
                reference = (WTPrincipalReference) iterator.next();

                List list = (List) map.get(reference);
                if ("资料管理员".equals(((Role) list.get(0)).getDisplay(Locale.CHINA))) {
                    principal = reference.getPrincipal();
                    if (principal instanceof WTUser) {
                        user = (WTUser) principal;
                        allUsers.add(user);
                    }
                }
            }
        }
        return allUsers;
    }

    /**
     * find all users from the product team
     *
     * add by LongXiuChuan
     *
     * @param cb
     * @return List<WTUser>
     * @throws WTException
     */
    private static List<WTUser> getAllUsersFromProductTeam(NmCommandBean cb) throws WTException {
        Object pbo = getContextObj(cb);
        List<WTUser> allUsers = new ArrayList<WTUser>();
        if (pbo != null && pbo instanceof WorkItem) {
            WorkItem workItem = (WorkItem) pbo;
            WfActivity wfAct = (WfActivity) workItem.getSource().getObject();
            Object pbo2 = wfAct.getParentProcess().getContext().getValue("primaryBusinessObject");
            WTContainer wfcont = null;
            if (pbo2 instanceof WTChangeOrder2) {
                WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo2;
                QueryResult qr = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
                while (qr.hasMoreElements()) {
                    Object o = qr.nextElement();
                    if (o instanceof WTDocument) {
                        WTDocument document = (WTDocument) o;
                        wfcont = document.getContainer();
                    } else if (o instanceof EPMDocument) {
                        EPMDocument epmdoc = (EPMDocument) o;
                        wfcont = epmdoc.getContainer();
                    } else if (o instanceof WTPart) {
                        WTPart part = (WTPart) o;
                        wfcont = part.getContainer();
                    } else if (o instanceof ManagedBaseline) {
                        ManagedBaseline bl = (ManagedBaseline) o;
                        wfcont = bl.getContainer();
                    } else if (o instanceof ProcessEnvelope) {
                        ProcessEnvelope processEnvelope = (ProcessEnvelope) o;
                        wfcont = processEnvelope.getContainer();
                    } else if (o instanceof MPMProcessPlan) {
                        MPMProcessPlan processPlan = (MPMProcessPlan) o;
                        wfcont = processPlan.getContainer();
                    }
                }
            }
            if (pbo2 instanceof WTDocument) {
                WTDocument document = (WTDocument) pbo2;
                wfcont = document.getContainer();
            } else if (pbo2 instanceof EPMDocument) {
                EPMDocument epmdoc = (EPMDocument) pbo2;
                wfcont = epmdoc.getContainer();
            } else if (pbo2 instanceof WTPart) {
                WTPart part = (WTPart) pbo2;
                wfcont = part.getContainer();
            } else if (pbo2 instanceof ManagedBaseline) {
                ManagedBaseline bl = (ManagedBaseline) pbo2;
                wfcont = bl.getContainer();
            } else if (pbo2 instanceof ProcessEnvelope) {
                ProcessEnvelope processEnvelope = (ProcessEnvelope) pbo2;
                wfcont = processEnvelope.getContainer();
            } else if (pbo2 instanceof MPMProcessPlan) {
                MPMProcessPlan processPlan = (MPMProcessPlan) pbo2;
                wfcont = processPlan.getContainer();
            }
            ContainerTeam conteam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wfcont);
            HashMap map = conteam.getAllMembers();

            Iterator iterator = map.keySet().iterator();
            WTUser user = null;
            WTPrincipalReference reference = null;
            WTPrincipal principal = null;
            while (iterator.hasNext()) {
                reference = (WTPrincipalReference) iterator.next();
                principal = reference.getPrincipal();
                if (principal instanceof WTUser) {
                    user = (WTUser) principal;
                    allUsers.add(user);
                }
            }
        }
        return allUsers;
    }

    private static List<String> standardSortRoles() {
        List<String> list = new ArrayList<String>();

        //工装申请单 签审流程
        list.add("技术部门");
        list.add("主任工艺师");
        list.add("工装管理员");
        list.add("工装设计师");
        list.add("档案会签");
        list.add("主任工艺师会签");
        list.add("项目调度员会签");
        list.add("工装管理员归档");

        list.add("校对者");
        list.add("审核者");
        list.add("内部会签者");
        list.add("外部会签者");
        list.add("指派工艺组长者");
        list.add("型号结算负责人");
        list.add("标审者");
        list.add("复审者");
        list.add("批准者");
        list.add("打印者");
        return list;
    }
}
