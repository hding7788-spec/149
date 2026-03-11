package ext.casc.process;

import com.ptc.core.components.beans.TreeHandlerAdapter;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.constants.Constants;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;
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

import javax.servlet.http.HttpSession;
import java.util.*;

public class ProAssignTreeHandler2 extends TreeHandlerAdapter {

    private WTPart part;
    private List<String> oidList = new ArrayList<String>();
    private WTContainer parentContainer = null;

    public ProAssignTreeHandler2(WTPart part) {
        this.part = part;
        this.parentContainer = part.getContainer();
    }

    @Override
    public Map<Object, List> getNodes(List parents) throws WTException {
        NmCommandBean commandBean = getModelContext().getNmCommandBean();
        HttpSession session = commandBean.getRequest().getSession();
        List deleteList = (List)session.getAttribute("deleteObject");
        if (deleteList==null) {
            deleteList = new ArrayList();
        }
        Map<Object, List> result = new HashMap<Object, List>();
        Persistable[][][] all_children = WTPartHelper.service.getUsesWTParts(new WTArrayList(parents),
                getDefaultConfigSpec());
        WTContainer childContainer = null;
        for (ListIterator i = parents.listIterator(); i.hasNext();) {
            WTPart parent = (WTPart) i.next();
            if (deleteList.contains(parent)) {
                continue;
            }
            String parentOid = PersistenceHelper.getObjectIdentifier(parent).toString();
            if (!oidList.contains(parentOid)&&!deleteList.contains(parent)) {
                oidList.add(parentOid);
            }
            Persistable[][] branch = all_children[i.previousIndex()];
            if (branch == null) {
                continue;
            }
            List children = new ArrayList(branch.length);
            result.put(parent, children);
            if (!this.part.equals(parent)) {//新加的部件不需要带出子件
                continue;
            }
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
                    if (partType != null && partType.equals(Constants.TYPE_ZIZHIJIAN)) {//过滤不是自制件类型的零部件
//                        if (ProcessUtil.isExistProcessTask(childPart)) {//过滤已经分过工的零部件
//                            continue;
//                        }
                        childContainer = childPart.getContainer();
                        if (!parentContainer.getName().equals(childContainer.getName())) {//过滤掉借用件，即是跟父件不在同一产品库下的零部件
                            continue;
                        }
                        String childOid = PersistenceHelper.getObjectIdentifier(childPart).toString();
                        if (deleteList.contains(childPart)) {
                            continue;
                        }
                        if (!oidList.contains(childOid)&&!deleteList.contains(childPart)) {
                            oidList.add(childOid);
                        }
                        if(!deleteList.contains(childPart)&&!parents.contains(childPart)){
                            children.add(childPart);
                        }
                    }
                }

            }
        }
        session.setAttribute("oidList", oidList);
        deleteList.clear();
        session.removeAttribute("deleteObject");
        return result;
    }

    @Override
    public List<Object> getRootNodes() throws WTException {
        NmCommandBean commandBean = getModelContext().getNmCommandBean();
        HttpSession session = commandBean.getRequest().getSession();
        List<Object> list = new ArrayList<Object>();
        List deleteList = (List)session.getAttribute("deleteObject");
        if (deleteList==null) {
            deleteList = new ArrayList();
        }
        List<Object> rootObjects = (List)session.getAttribute("rootParts");
        if (rootObjects == null) {
            rootObjects = new ArrayList<Object>();
        }
        list.add(this.part);
        if(!rootObjects.contains(this.part)){
            rootObjects.add(this.part);
        }
        List<Object> list2 = (List)session.getAttribute("addObject");
        if (list2!=null&&!list2.isEmpty()) {
            for (Object object : list2) {
                if(!rootObjects.contains(object)){
                    list.add(object);
                    rootObjects.add(object);
                }
            }
            list2.clear();
            session.removeAttribute("addObject");
        }
        rootObjects.removeAll(deleteList);
        session.setAttribute("rootParts", rootObjects);
        return rootObjects;
    }

    protected ConfigSpec getDefaultConfigSpec() throws WTException {
        return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
    }

}