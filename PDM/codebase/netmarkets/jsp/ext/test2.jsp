<%@page import="ext.casc.process.ProcessConstants" %>
<%@page import="ext.casc.process.ProcessTask" %>
<%@page import="ext.casc.process.ProcessTaskItem" %>
<%@page import="ext.casc.process.util.ProcessUtil" %>
<%@page import="wt.fc.PersistenceHelper" %>
<%@page import="wt.fc.QueryResult" %>
<%@page import="wt.pds.StatementSpec" %>
<%@page import="wt.query.QuerySpec" %>
<%@ page import="wt.query.SearchCondition" %>
<%
    System.out.println("--------RefreshProcessTaskState--------START---------");
    ProcessTask processTask;
    ProcessTaskItem tempTaskItem;
    String beforeState;
    int i = 0;
    QuerySpec qSpec = new QuerySpec(ProcessTask.class);
    qSpec.appendWhere(new SearchCondition(ProcessTask.class, ProcessTask.TASK_STATE, SearchCondition.EQUAL, ProcessConstants.TASK_STATE_JINGXINZHONG));
    QueryResult pts = PersistenceHelper.manager.find((StatementSpec) qSpec);
    while (pts.hasMoreElements()) {
        processTask = (ProcessTask) pts.nextElement();
        QueryResult qResult = ProcessUtil.getAllProcessTaskItemByPTask(processTask.getPersistInfo().getObjectIdentifier().getId());
        boolean flag = false;
        while (qResult.hasMoreElements()) {
            flag = true;
            tempTaskItem = (ProcessTaskItem) qResult.nextElement();
            if (ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN.equals(tempTaskItem.getTaskItemState())) {
                flag = false;
                break;
            }
        }
        if (flag) {
            i++;
            beforeState = processTask.getTaskState();
            processTask.setTaskState(ProcessConstants.TASK_STATE_YIWANGONG);
            System.out.println("----" + i + "--  ProcessTask: " + processTask.getNumber() + " stateChanged , beforeTaskState: " + beforeState);
            PersistenceHelper.manager.save(processTask);
        }
    }
    System.out.println("--------RefreshProcessTaskState--------END---------");

%>