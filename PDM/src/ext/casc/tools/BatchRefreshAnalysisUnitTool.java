package ext.casc.tools;

import cn.hutool.core.util.StrUtil;
import ext.casc.analysisActivity.bean.AnalysisObjEntry;
import ext.casc.util.WTUserUtil;
import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;
import wt.fc.ReferenceFactory;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTUser;
import wt.session.SessionServerHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: SAST-149-PDM
 * @description: 刷新影响分析负责人所在部门工具
 * @author: cjh
 * @create: 2025-9-8 16:35:02
 */
public class BatchRefreshAnalysisUnitTool implements RemoteAccess {

    public static void main(String[] args) {
        if(!RemoteMethodServer.ServerFlag) {
            try {
                RemoteMethodServer server = RemoteMethodServer.getDefault();
                server.setUserName("wcadmin");
                server.setPassword("Admin@149.941");
                String method = "";
                Class<?>[] types = null;
                Object[] vals = null;
                method = "doRefresh";
                types = new Class<?>[]{};
                vals = new Object[]{};
                if(types != null && vals != null) {
                    server.invoke(method, BatchRefreshAnalysisUnitTool.class.getName(), null, types, vals);
                } else {
                    doRefresh();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void doRefresh() {
        System.out.println("----start-------");
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            Map<String, String> units = new HashMap<>();
            CmQuerySpec qs = new CmQuerySpec(AnalysisObjEntry.class);
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while(qr.hasNext()) {
                AnalysisObjEntry entry = (AnalysisObjEntry) qr.next();
                String responser = entry.getResponser();
                if(StrUtil.isNotEmpty(responser)) {
                    if(units.containsKey(responser)) {
                        String newUnit = units.get(responser);
                        if(!newUnit.equals(entry.getUnit())) {
                            entry.setUnit(newUnit);
                            CmPersistenceHelper.manager.update(entry);
                        }
                    }else {
                        ReferenceFactory rf = new ReferenceFactory();
                        WTUser user = null;
                        try {
                            user = (WTUser) rf.getReference(responser).getObject();
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        if(user != null) {
                            String newUnit = WTUserUtil.getDeptShortName(user);
                            if(!newUnit.equals(entry.getUnit())) {
                                entry.setUnit(newUnit);
                                CmPersistenceHelper.manager.update(entry);
                            }
                            units.put(responser, newUnit);
                        }
                    }
                }
            }
            System.out.println("----end-------");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    }
}