package ext.casc.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.http.HttpSession;

import wt.enterprise.Master;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.collections.WTArrayList;
import wt.inf.container.WTContainer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.util.WTException;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;

import com.ptc.core.components.beans.TreeHandlerAdapter;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.constants.Constants;
import ext.casc.process.util.ProcessUtil;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;

public class ChangeProAssignTreeHandler extends TreeHandlerAdapter {

    private WTPart part;
    private List<String> oidList = new ArrayList<String>();
    private WTContainer parentContainer = null;
    
    public ChangeProAssignTreeHandler(WTPart part) {
        this.part = part;
        this.parentContainer = part.getContainer();
    }

    @Override
    public Map<Object, List> getNodes(List parents) throws WTException {
        Map<Object, List> result = new HashMap<Object, List>();
        Persistable[][][] all_children = WTPartHelper.service.getUsesWTParts(new WTArrayList(parents),
                getDefaultConfigSpec());
        WTContainer childContainer = null;
        for (ListIterator i = parents.listIterator(); i.hasNext();) {
            WTPart parent = (WTPart) i.next();
            String parentOid = PersistenceHelper.getObjectIdentifier(parent).toString();
            if (!oidList.contains(parentOid)) {
                oidList.add(parentOid);
            }
            Persistable[][] branch = all_children[i.previousIndex()];
            if (branch == null) {
                continue;
            }
            List children = new ArrayList(branch.length);
            result.put(parent, children);
            for (Persistable[] child : branch) {
                Persistable per = child[1];
                if (per instanceof WTPart) {
                    WTPart childPart = (WTPart)per;
                    childPart = WCUtil.getLatestPartByView((Master)childPart.getMaster(), "Manufacturing");
                    if (childPart == null){
                        continue;
                    }
                    IBAUtility ibaUtility = new IBAUtility(childPart);
                    String partType = ibaUtility.getIBAValue("MTYPE");
                    System.out.println("--------partType:"+partType);
                    if (partType != null && partType.equals(Constants.TYPE_ZIZHIJIAN)) {//过滤不是自制件类型的零部件
                        if (ProcessUtil.isExistProcessTask(childPart)) {//过滤已经分过工的零部件
                            continue;
                        }
                        childContainer = childPart.getContainer();
                        if (!parentContainer.getName().equals(childContainer.getName())) {//过滤掉借用件，即是跟父件不在同一产品库下的零部件
                            continue;
                        }
                        String childOid = PersistenceHelper.getObjectIdentifier(childPart).toString();
                        if (!oidList.contains(childOid)) {
                            oidList.add(childOid);
                        }
                        children.add(childPart);
                    }
                }
                
            }
        }
        NmCommandBean commandBean = getModelContext().getNmCommandBean();
        HttpSession session = commandBean.getRequest().getSession();
        session.setAttribute("oidList", oidList);
        return result;
    }

    @Override
    public List<Object> getRootNodes() throws WTException {
        List<Object> list = new ArrayList<Object>();
        list.add(this.part);
        return list;
    }

    protected ConfigSpec getDefaultConfigSpec() throws WTException {
        return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
    }

}
