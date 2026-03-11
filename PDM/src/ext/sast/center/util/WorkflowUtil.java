package ext.sast.center.util;

import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;

import java.net.URLEncoder;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/4/11
 * @ Description：
 * @ Modified By：
 */
public class WorkflowUtil {

    /**
     * 获取 会签人会签单位url
     * @param ref
     * @return
     * @throws WTException
     */
    public static String getSignUserAndUnitUrl (ObjectReference ref) throws Exception {
        WfActivity activity = (WfActivity) ref.getObject();
        WfProcess wfp = activity.getParentProcess();
        String processName = wfp.getTemplate().getName();
        processName = URLEncoder.encode(processName,"UTF-8");
        String link = "<input hidden=\"true\" type=\"text\" id=\"userAndUnit\" readonly/><a href=\"#\" onclick=\"window.open('netmarkets/jsp/ext/sast/center/workflow/setSignUserAndUnit.jsp?processName="+processName+"','设置电子会签人员','height=600, width=600, top=150, left=300')\">" + "设置" + "</a>";
        return link;
    }

    public static WfProcess getProcess(Persistable pbo) {
        WfProcess process = null;
        try {
            QueryResult queryResult = WfEngineHelper.service.getAssociatedProcesses(pbo, null, null);
            if (queryResult.hasMoreElements()) {
                process = (WfProcess) queryResult.nextElement();
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
        return process;
    }
}
