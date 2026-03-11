package ext.casc.dataUtility;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.AbstractAttributeDataUtility;
import com.ptc.core.components.rendering.guicomponents.AttributeGuiComponent;
import com.ptc.core.components.rendering.guicomponents.TextDisplayComponent;
import com.ptc.netmarkets.util.misc.NmActionServiceHelper;
import com.ptc.windchill.enterprise.work.assignmentslist.server.CachedAttrForAssignments;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import wt.access.NotAuthorizedException;
import wt.change2.WTAnalysisActivity;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistentReference;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.httpgw.URLFactory;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.workflow.engine.WfState;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

/**
 * <pre>
 * 功能描述：主页中的任务辅助类
 * 使用方法：
 * 修改记录:（修改时间、修改人、修改内容、修改原因）
 * </pre>
 *
 * @author LongXiuChuan 2013-5-3
 * @since 1.0
 */
public class AssignmentUtility extends AbstractAttributeDataUtility {

    public static String CONST_EXT_PLAN_ASSIGNMENTS_SUBJECT_NAME = "EXT_PLAN_ASSIGNMENT_SUBJECT";

    CachedAttrForAssignments cachedAttrForAssignments;

    public AssignmentUtility() {

    }

    public void setModelData(String component_id, List Object, ModelContext mc) throws WTException {

        cachedAttrForAssignments = CachedAttrForAssignments.getInstance();
    }

    @Override
    public AttributeGuiComponent createSingleValueDisplayComponent(String component_id,
            Object datum, Object rawVal, ModelContext mc) throws WTException {
        AttributeGuiComponent guiComp = null;
        guiComp = handleAssignmentSubject(component_id, datum);
        return guiComp;
    }

    /**
     * <pre>
     * 功能描述: 产生“名称”列的显示内容
     * 使用方法：
     * 修改记录:（修改时间、修改人、修改内容、修改原因）
     * </pre>
     *
     * @param component_id "名称"列的id
     * @param datum
     *            对象
     * @return 组件对象
     * @throws WTException 异常
     * @author LongXiuChuan 2013-5-3
     */
    private AttributeGuiComponent handleAssignmentSubject(String component_id, Object datum)
            throws WTException {

    	if("BIANZHIZHE".equals(component_id)){
            String bzz = "";
    		if (datum instanceof WorkItem) {
                WorkItem currentWorkItem = (WorkItem) datum;
                if(currentWorkItem!=null){
                    PersistentReference pboR = currentWorkItem.getPrimaryBusinessObject();
                    if(pboR!=null&&!"null".equals(pboR)){
                        try{
                            Object pbo =  pboR.getObject();
                            if(pbo!=null){
                                if(pbo instanceof WTDocument){
                                    WTDocument doc = (WTDocument)pbo;
                                    if(doc!=null){
                                        bzz = doc.getCreatorFullName();
                                    }
                                }else if(pbo instanceof WTChangeOrder2){
                                    WTChangeOrder2 doc = (WTChangeOrder2)pbo;
                                    if(doc!=null){
                                        bzz = doc.getCreatorFullName();
                                    }
                                }else if(pbo instanceof ProcessEnvelope){
                                    ProcessEnvelope doc = (ProcessEnvelope)pbo;
                                    if(doc!=null){
                                        bzz = doc.getCreatorFullName();
                                    }
                                }
                            }
                        }catch (Exception e){
                            bzz = "";
                        }
                    }
                }
            }
    		 TextDisplayComponent guiComp = new TextDisplayComponent(component_id);
             guiComp.setRichText(false);
             guiComp.setEnabled(true);
             guiComp.setReadOnly(true);
             guiComp.setValue(bzz);
             return guiComp;
    	}

        AttributeGuiComponent displayComp = new TextDisplayComponent("");
        try {
            if (datum instanceof WorkItem) {
                WorkItem currentWorkItem = (WorkItem) datum;
                displayComp = getWorkItemSubjectDisplayName(component_id, currentWorkItem);
            }
        } catch (Exception e) {
            if (e instanceof NotAuthorizedException)
                throw new WTRuntimeException(e);
        }

        return displayComp;

    }

    /**
     * <pre>
     * 功能描述: 获得列显示内容
     * 使用方法：
     * 修改记录:（修改时间、修改人、修改内容、修改原因）
     * </pre>
     *
     * @param component_id "名称"列id
     * @param currentWorkItem 活动
     * @return 组件对象
     * @throws WTException 异常
     * @author LongXiuChuan 2013-5-3
     */
    public AttributeGuiComponent getWorkItemSubjectDisplayName(String component_id,
            WorkItem currentWorkItem) throws WTException {

        boolean enforce = SessionServerHelper.manager.isAccessEnforced();
        String label = "";
        String objURL = "";
        WfAssignedActivity wa = null;
        try {
            SessionServerHelper.manager.setAccessEnforced(true);
            wa = (WfAssignedActivity) currentWorkItem.getSource().getObject();
            label = wa.getName();
            objURL = getURL(currentWorkItem);
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enforce);
        }
        TextDisplayComponent guiComp = new TextDisplayComponent(component_id);
        label = makeShowLabel(label, wa, objURL);
        guiComp.setRichText(true);
        guiComp.setEnabled(true);
        guiComp.setReadOnly(true);
        guiComp.setValue(label);
        guiComp.setPlainTextValue("1");//因为是富文本，为了让程序认为没有换行，达到不显示更多链接的目的，写死一个没有换行的值
        return guiComp;
    }

    /**
     * 获取对象url
     *
     * @create date: 2013-5-3
     * @methodName: getURL
     * @return: String
     * @param obj
     * @return
     * @throws WTException
     */
    public static String getURL(Object obj) throws WTException {

        ReferenceFactory referencefactory = new ReferenceFactory();
        String s1 = referencefactory.getReferenceString((Persistable) obj);
        URLFactory urlfactory = new URLFactory();
        HashMap hashmap = new HashMap();
        hashmap.put("oid", s1);
        String s2 = NmActionServiceHelper.service.getAction("object", "view").getUrl();
        return urlfactory.getHREF(s2, hashmap, true);
    }

    /**
     * <pre>
     * 功能描述: 根据任务时间显示不同的颜色,如果活动不是待办的活动的话，则不用高亮显示
     * 使用方法：
     * 修改记录:（修改时间、修改人、修改内容、修改原因）
     * </pre>
     *
     * @param label 显示数据
     * @author LongXiuChuan 2013-5-3
     * @param wa 活动
     * @return 显示的名称及活动
     * @throws WTException
     */
    private String makeShowLabel(String label, WfAssignedActivity wa, String objURL) throws WTException {
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        WfState state = wa.getState();
        WTReference  ref = wa.getParentProcess().getBusinessObjectReference(new ReferenceFactory());
        String pboNumber = "";
        if(ref!=null){
        	  Object pbo = ref.getObject();
        	  if(pbo!=null){
        		  if(pbo instanceof WTDocument){
        			  pboNumber = "_"+((WTDocument)pbo).getNumber();
        		  }else if(pbo instanceof WTChangeOrder2){
        			  pboNumber = "_"+((WTChangeOrder2)pbo).getNumber();
        		  }else if(pbo instanceof ProcessEnvelope){
        			  pboNumber = "_"+((ProcessEnvelope)pbo).getNumber();
        		  }else if(pbo instanceof ChangePackaged){
        			  pboNumber = "_"+((ChangePackaged)pbo).getNumber();
        		  }else if(pbo instanceof WTAnalysisActivity) {
                      String actName = ((WTAnalysisActivity) pbo).getName();
                      try {
                          actName = actName.substring(7, actName.length() - 1);
                      } catch(Exception e) {
                          e.printStackTrace();
                      }
                      pboNumber = "_" + actName;
                  }
              }
        }

        String templateName = "(" +wa.getParentProcess().getTemplate().getName()+pboNumber+")";
        label = label+ "  " +templateName;
        wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        if (WfState.CLOSED_COMPLETED_EXECUTED.equals(state)) {
            return "<a href='" + objURL+ "'>" + label+ "</a>";
        }
        if (isPassDays(wa.getStartTime(), 10)) {
            return "<a href='javascript:void(0)' onclick='window.open(\""+objURL+"\")'><font color=\"red\">" + label + "</font></a>";

        } else if (isPassDays(wa.getStartTime(), 5)) {
            return "<a href='javascript:void(0)' onclick='window.open(\""+objURL+"\")'><font color='#FFCC00'>" + label + "</font></a>";
        } else {
            return "<a href='" + objURL + "'>" + label + "</a>";
        }


    }

    /**
     * <pre>
     * 功能描述: 和当前时间相差的天数
     * 使用方法：
     * 修改记录:（修改时间、修改人、修改内容、修改原因）
     * </pre>
     *
     * @param startTime 开始时间
     * @param days 天数
     * @return 超过：true；没有超过:false;
     * @author LongXiuChuan 2013-5-3
     */
    private boolean isPassDays(Timestamp startTime, int days) {
        long day = 1 * 24 * 60 * 60 * 1000;
        long s = startTime.getTime();
        long e = System.currentTimeMillis();
        if ((e - s) > day * days) {
            return true;
        } else {
            return false;
        }
    }
}
