package ext.casc.util;

import ext.casc.folder.AuthoriserPermissionUtil;
import ext.casc.workflow.WorkflowHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.query.QuerySpec;
import wt.util.WTProperties;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

public class ExportProductMembers {
    public static void export() {
        try{
            Map<String,String>  resultMap = new HashMap<String, String>();
            QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            while (qr.hasMoreElements()) {
                PDMLinkProduct product = (PDMLinkProduct) qr.nextElement();
                ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(product);
                Vector vc = containerTeam.getMembers();
                List<WTUser> users = new ArrayList<WTUser>();
                for (int i = 0; i < vc.size(); i++) {
                    WTPrincipalReference principalRef = (WTPrincipalReference) vc
                            .get(i);
                    Persistable persistable = principalRef.getObject();
                    if (persistable instanceof WTUser) {
                        WTUser user = (WTUser) persistable;
                        users.add(user);
                    }else if (persistable instanceof WTGroup) {
                        WTGroup group = (WTGroup) persistable;
                        WorkflowHelper.getUserFromWTGroup(group,users);
                    }
                }
                for(WTUser u:users){
                    String keyName = u.getFullName()+"("+u.getName()+")";
                    String productName = resultMap.get(keyName);
                    if(productName!=null&&!"".equals(productName)){
                        resultMap.put(keyName,productName+";"+product.getName());
                    }else{
                        resultMap.put(keyName,product.getName());
                    }
                }
            }
            WTProperties wtp = WTProperties.getLocalProperties();
            String temp = wtp.getProperty("wt.temp");
            ArrayList<String> titles = new  ArrayList<String> ();
            titles.add("用户");titles.add("产品库");
            ArrayList<ArrayList<String>> values = buildValues(resultMap);
            ExcelFileGenerator gen = new ExcelFileGenerator(titles,values);
            File file = new File(temp + File.separator + "ExportProductMembers.xls");
            gen.expordExcel(new FileOutputStream(file));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static ArrayList<ArrayList<String>> buildValues(Map<String, String> resultMap) {
        ArrayList<ArrayList<String>> allList = new ArrayList<ArrayList<String>>();
        Set<Map.Entry<String,String>>  set = resultMap.entrySet();
        for (Map.Entry<String,String> entry:set){
            ArrayList<String> list = new ArrayList<String>();
            list.add(entry.getKey());
            list.add(entry.getValue());
            allList.add(list);
        }
        return allList;
    }


}
