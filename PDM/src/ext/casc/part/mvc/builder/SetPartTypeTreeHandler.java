package ext.casc.part.mvc.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import wt.enterprise.Master;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.PersistenceException;
import wt.util.WTException;
import wt.vc.Iterated;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;

import com.ptc.core.components.beans.TreeHandlerAdapter;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.util.WCUtil;

public class SetPartTypeTreeHandler extends TreeHandlerAdapter {

    private Object note;
    private List<String> oidList = new ArrayList<String>();

    public SetPartTypeTreeHandler(Object object) {
        this.note = object;
    }

    @Override
    public Map<Object, List> getNodes(List arg0) throws WTException {
        NmCommandBean commandBean = getModelContext().getNmCommandBean();
        Map<Object, List> map = new HashMap<Object, List>();
        for (int i = 0; i < arg0.size(); i++) {
            Object node = arg0.get(i);
            WTPart parent = (WTPart) node;
            parent = WCUtil.getLatestPartByView((Master)parent.getMaster(), "Manufacturing");
            if (parent == null) {
                continue;
            }
            List subNodeList = new ArrayList();
            String parentOid = PersistenceHelper.getObjectIdentifier(parent).toString();
            if (!oidList.contains(parentOid)) {
                oidList.add(parentOid);
            }
            
            QueryResult qr = PersistenceHelper.manager.navigate(parent, WTPartUsageLink.USES_ROLE, WTPartUsageLink.class, false);
            while (qr.hasMoreElements()) {
                WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
                WTPartMaster master = link.getUses();
                WTPart child = (WTPart) getIteratedByMaster(master);
                child = WCUtil.getLatestPartByView((Master)child.getMaster(), "Manufacturing");
                if (child == null) {
                    continue;
                }
                String childOid = PersistenceHelper.getObjectIdentifier(child).toString();
                if (!oidList.contains(childOid)) {
                    oidList.add(childOid);
                }
                subNodeList.add(child);
            }
            map.put(node, subNodeList);
        }
        HttpSession session = commandBean.getRequest().getSession();
        session.setAttribute("oidList", oidList);
        return map;
    }

    @Override
    public List<Object> getRootNodes() throws WTException {
        List<Object> list = new ArrayList<Object>();
        list.add(this.note);
        return list;
    }

    public static Iterated getIteratedByMaster(Mastered mst) {
        Iterated itr = null;
        if (mst != null) {
            QueryResult qr;
            try {
                qr = VersionControlHelper.service.allIterationsOf(mst);
                if (qr.hasMoreElements()) {
                    itr = (Iterated) qr.nextElement();
                }
            } catch (PersistenceException e) {
                e.printStackTrace();
            } catch (WTException e) {
                e.printStackTrace();
            }
        }
        return itr;
    }

}
