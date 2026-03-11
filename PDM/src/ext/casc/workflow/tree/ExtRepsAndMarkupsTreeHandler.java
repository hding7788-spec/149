package ext.casc.workflow.tree;

import com.ptc.core.components.beans.TreeHandlerAdapter;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.model.NmSimpleOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.wvs.repsAndMarkups.utils.RepsAndMarkupsHelper;
import com.ptc.wvs.common.util.MarkupFolder;
import com.ptc.wvs.common.util.ViewableMap;
import com.ptc.wvs.server.util.PublishUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.List;
import java.util.Map;
import java.util.Map;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.util.WTException;
import wt.viewmarkup.LifeCycleManagedWtMarkUp;
import wt.viewmarkup.MarkUp;
import wt.viewmarkup.Viewable;
import wt.wvs.WVSLogger;

public class ExtRepsAndMarkupsTreeHandler extends TreeHandlerAdapter
{
  ViewableMap mViewableMap = new ViewableMap();
  private static final WVSLogger mLogger = WVSLogger.getLogger(ExtRepsAndMarkupsTreeHandler.class, "wt.wvs.ui.repsandmarkups");

  public List<Object> getRootNodes()
    throws WTException
  {
    ArrayList localArrayList = new ArrayList();

    NmCommandBean localNmCommandBean = getModelContext().getNmCommandBean();
    Object localObject1 = localNmCommandBean.getPrimaryOid().getRefObject();

    if(localObject1==null){
        Map map = localNmCommandBean.getRequestData().getParameterMap();
        Object o = map.get("oid");//读取任务页面的流程oid
        ReferenceFactory rf = new ReferenceFactory();
        if(o instanceof String[]){
        	String[] ss = (String[])o;
        	localObject1 = rf.getReference(ss[0]).getObject();

        }
        if(o instanceof String){
        	String ss = (String)o;
        	localObject1 = rf.getReference(ss).getObject();
        }

    }
    if (PublishUtils.isRepTableStructureRowVisible(localObject1))
    {
      localArrayList.add(localObject1);
    }
    Object localObject2;
    if ((localObject1 instanceof Representable))
    {
      localObject2 = (Representable)localObject1;
      Object localObject3;
      if ((localObject2 instanceof WTPart))
      {
    	QueryResult localQueryResult = PublishUtils.findDescribedBy((WTPart)localObject2);
        Object localObject4;
        if (localQueryResult != null) {
          while (localQueryResult.hasMoreElements())
          {
            localObject3 = PublishUtils.getRepresentations((Persistable)localQueryResult.nextElement());
            if (localObject3 != null) {
              while (((QueryResult)localObject3).hasMoreElements())
              {
                localObject4 = (Representation)((QueryResult)localObject3).nextElement();

                localArrayList.add(localObject4);
              }
            }
          }
        }

        localObject3 = PublishUtils.findEPMDocument((WTPart)localObject2);
        if (localObject3 != null) {
          localObject4 = PublishUtils.getRepresentations((Persistable)localObject3, false);
          if (localObject4 != null) {
            while (((QueryResult)localObject4).hasMoreElements())
            {
              Representation localRepresentation = (Representation)((QueryResult)localObject4).nextElement();

              localArrayList.add(localRepresentation);
            }
          }
        }
      }
      QueryResult localQueryResult = PublishUtils.getRepresentations((Persistable)localObject2);

      if (localQueryResult != null) {
        while (localQueryResult.hasMoreElements())
        {
          localObject3 = (Representation)localQueryResult.nextElement();
          localArrayList.add(localObject3);
        }

      }

    }

    if (((localObject1 instanceof Viewable)) && (!(localObject1 instanceof Representable)))
    {
      localObject2 = this.mViewableMap.getChildren((Viewable)localObject1);
      if ((localObject2 != null) && (((List)localObject2).size() > 0))
      {
        localArrayList.addAll((Collection)localObject2);
      }
    }

    return (List<Object>)(List<Object>)(List<Object>)localArrayList;
  }

  public Map<Object, List> getNodes(List paramList)
    throws WTException
  {
    mLogger.debug("Entering in getNodes method for RepsAndMarkupTreeHandler");
    HashMap localHashMap = new HashMap();

    boolean bool = mLogger.isDebugEnabled();

    for (Iterator localIterator1 = paramList.iterator(); localIterator1.hasNext(); ) { Object localObject1 = localIterator1.next();

      List localList = null;
      Object localObject2;
      if ((localObject1 instanceof Viewable))
      {
        localList = this.mViewableMap.getChildren((Viewable)localObject1);
      }
      else if ((localObject1 instanceof MarkupFolder))
      {
        localList = this.mViewableMap.getChildren((MarkupFolder)localObject1);
      }
      else if ((localObject1 instanceof NmSimpleOid))
      {
        localObject2 = MarkupFolder.newInstance((NmSimpleOid)localObject1);
        localList = this.mViewableMap.getChildren((MarkupFolder)localObject2);
      }

      if ((localList != null) && (localList.size() > 0))
      {
        localObject2 = new ArrayList();
        for (Iterator localIterator2 = localList.iterator(); localIterator2.hasNext(); ) { Object localObject3 = localIterator2.next();
          if ((localObject3 instanceof MarkUp)) {
            MarkUp localMarkUp = (MarkUp)localObject3;

            int i = 0;
            if ((i == 0) && ((localMarkUp instanceof LifeCycleManagedWtMarkUp))) {
              if (bool) mLogger.debug("This markup is not added to the tree because it is a LifeCycleManagedWtMarkUp: " + localMarkUp.getName() + "   " + PublishUtils.getRefFromObject(localMarkUp));
            }
            else
              ((List)localObject2).add(localObject3);
          }
          else
          {
            ((List)localObject2).add(localObject3);
          }
        }
        localHashMap.put(localObject1, localObject2);
      }
    }

    return (Map<Object, List>)localHashMap;
  }

  public boolean isExpandNeeded(Object paramObject, int paramInt)
    throws WTException
  {
    Object localObject1;
    Object localObject3;
    if ((paramObject instanceof Representation))
    {
      localObject1 = (Representation)paramObject;

      Object localObject2 = getModelContext().getNmCommandBean().getPrimaryOid().getRefObject();

      localObject3 = RepsAndMarkupsHelper.getDescribedByEPMDocument((Representable)localObject2, (Representation)localObject1);
      if ((localObject3 == null) && (((Representation)localObject1).getDefaultRepresentation().booleanValue()))
      {
        return true;
      }
    }
    else if ((paramObject instanceof Representable))
    {
      localObject1 = (Representable)paramObject;

      int i = 0;
      localObject3 = PublishUtils.getRepresentations((Persistable)localObject1);
      while ((((QueryResult)localObject3).hasMoreElements()) && (i == 0))
      {
        Representation localRepresentation = (Representation)((QueryResult)localObject3).nextElement();
        if (localRepresentation.getDefaultRepresentation().booleanValue())
        {
          i = 1;
        }
      }

      if (i == 0)
      {
        return true;
      }

    }

    return super.isExpandNeeded(paramObject, paramInt);
  }
}