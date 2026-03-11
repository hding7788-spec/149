package ext.casc.tools;

import ext.casc.analysisActivity.helper.AnalysisUtil;
import ext.casc.workflow.PrintHelper;
import wt.change2.WTAnalysisActivity;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.vc.config.LatestConfigSpec;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;

import java.util.*;

/**
 * @program: SAST-149-PDM
 * @description: 删除无人处理的发送给复材的更改影响分析
 * @author: cjh
 * @create: 2025-12-15 10:00:00
 */
public class DeleteFuCaiAnalysisTool implements RemoteAccess {

    public static void main(String[] args) {
        if(!RemoteMethodServer.ServerFlag) {
            try {
                RemoteMethodServer server = RemoteMethodServer.getDefault();
                server.setUserName("wcadmin");
                server.setPassword("Admin@149.941");
                String method = "";
                Class<?>[] types = null;
                Object[] vals = null;
                method = "doDelete";
                types = new Class<?>[]{};
                vals = new Object[]{};
                if(types != null && vals != null) {
                    server.invoke(method, DeleteFuCaiAnalysisTool.class.getName(), null, types, vals);
                } else {
                    doDelete();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void doDelete() {
        System.out.println("----start-------");
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            int count = 0;
            QuerySpec qs = new QuerySpec(WTAnalysisActivity.class);
            qs.appendWhere(new SearchCondition(WTAnalysisActivity.class, WTAnalysisActivity.NAME, SearchCondition.LIKE, "%发复材%"), new int[]{0});
            qs = new LatestConfigSpec().appendSearchCriteria(qs);
            QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
            System.out.println("共有" + qr.size() + "条包含发复材更改影响分析");
            while(qr.hasMoreElements()) {
                WTAnalysisActivity analysisActivity = (WTAnalysisActivity) qr.nextElement();
                if(analysisActivity != null) {
                    QueryResult result = WfEngineHelper.service.getAssociatedProcesses(analysisActivity, null, null);
                    if(result.hasMoreElements()) {
                        WfProcess process = (WfProcess) result.nextElement();
                        if(process.getName().contains("更改影响分析执行流程")) {
                            List<WfAssignedActivity> activityList = new ArrayList<WfAssignedActivity>();
                            activityList = PrintHelper.getActivities(process, activityList);
                            Iterator iterator = activityList.iterator();
                            while(iterator.hasNext()) {
                                WfAssignedActivity wfAssignedActivity = (WfAssignedActivity) iterator.next();
                                String wfactivityName = wfAssignedActivity.getName();
                                if(wfactivityName.equals("更改影响分析")) {
                                    if("正在运行".equals(wfAssignedActivity.getState().getDisplay(Locale.CHINA))) {
                                        WfEngineHelper.service.terminateObjectsRunningWorkflows(analysisActivity);
                                        PersistenceHelper.manager.delete(analysisActivity);
                                        AnalysisUtil.deleteAnalysisEntry(analysisActivity.getNumber());
                                        count++;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("删除的更改影响分析数量：" + count);
            System.out.println("----end-------");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    }
}