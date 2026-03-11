package ext.casc.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import wt.enterprise.Master;
import wt.fc.Persistable;
import wt.fc.collections.WTArrayList;
import wt.inf.container.WTContainer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;

import com.ptc.core.components.beans.TreeHandlerAdapter;

import ext.casc.constants.Constants;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;

public class ProAssignTreeHandler extends TreeHandlerAdapter {

    private WTPart part;
    private WTContainer parentContainer = null;

    public ProAssignTreeHandler(WTPart part) {
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
//            if (!this.part.equals(parent)) {//新加的部件不需要带出子件
//                continue;
//            }
            Persistable[][] branch = all_children[i.previousIndex()];
            if (branch == null) {
                continue;
            }
            List children = new ArrayList(branch.length);
            result.put(parent, children);

            for (Persistable[] child : branch) {
                Persistable per = child[1];

                if(!(per instanceof WTPart) && !(per instanceof WTPartMaster)) {
                	continue;
                }
                WTPart childPart = null;
                if (per instanceof WTPart) {
                    childPart = (WTPart)per;
                    childPart = WCUtil.getLatestPartByView((Master)childPart.getMaster(), "Manufacturing");
                } else if(per instanceof WTPartMaster) {
                	childPart = WCUtil.getLatestPartByView((Master)per, "Manufacturing");
                }

                if (childPart == null){
                    continue;
                }

                IBAUtility ibaUtility = new IBAUtility(childPart);
                String partType = ibaUtility.getIBAValue("MTYPE");

                //过滤不是自制件、外配套件、带料委外件、不带料委外件类型的零部件
                if (Constants.TYPE_ZIZHIJIAN.equals(partType)
                		||Constants.TYPE_WAIPEITAOJIAN.equals(partType)
                		||Constants.TYPE_DAILIAOWEIWAIJIAN.equals(partType)
                		||Constants.TYPE_BUDAILIAOWEIWAIJIAN.equals(partType)) {
//                        if (ProcessUtil.isExistProcessTask(childPart)) {//过滤已经分过工的零部件
//                            continue;
//                        }
                    childContainer = childPart.getContainer();
                    if (!parentContainer.getName().equals(childContainer.getName())) {//过滤掉借用件，即是跟父件不在同一产品库下的零部件
                        continue;
                    }
                    children.add(childPart);
                }
            }
        }
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
