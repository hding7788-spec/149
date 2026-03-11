package ext.casc.common.mvc.builder.analysis;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.change2.mvc.builders.tables.AbstractRelatedChangesTableBuilder;
import ext.casc.analysisActivity.bean.AnalysisToSourceLink;
import wt.change2.WTAnalysisActivity;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

import java.util.ArrayList;
import java.util.List;

@ComponentBuilder("ext.casc.common.mvc.builder.analysis.RelatedAnalysisFileBuilder")
public class RelatedAnalysisFileBuilder extends AbstractRelatedChangesTableBuilder {

    @Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1)
            throws Exception {
        List list = new ArrayList();
        NmCommandBean cb = ((JcaComponentParams) arg1).getHelperBean().getNmCommandBean();
        WTObject refObject = (WTObject) cb.getPageOid().getRefObject();
        if(refObject instanceof WTAnalysisActivity){
            WTAnalysisActivity analysisActivity = (WTAnalysisActivity)refObject;
            list = getObjByWTAnalysisActivity(analysisActivity);
        } else if(refObject instanceof WorkItem) {
            WorkItem workItem = (WorkItem) refObject;
            Persistable object = workItem.getPrimaryBusinessObject().getObject();
            if(object instanceof WTAnalysisActivity){
                WTAnalysisActivity analysisActivity = (WTAnalysisActivity) object;
                list = getObjByWTAnalysisActivity(analysisActivity);
            }
        }

        return list;
    }

    public static List getObjByWTAnalysisActivity(WTAnalysisActivity analysisActivity) throws WTException {
        List list = new ArrayList();
        QueryResult qr = PersistenceHelper.manager.navigate(analysisActivity, "sourceObject", AnalysisToSourceLink.class, false);
        while(qr.hasMoreElements()) {
            AnalysisToSourceLink link = (AnalysisToSourceLink) qr.nextElement();
            list.add(link.getSourceObject());
        }
        return list;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams arg0)
            throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig table = factory.newTableConfig();
        table.setLabel("关联影响源");
        table.setActionModel("add_analysis_file");

        table.setId("ext.casc.common.mvc.builder.analysis.RelatedAnalysisFileBuilder");
        table.setSelectable(true);
        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        table.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setWidth(100);
        table.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        nameConfig.setWidth(150);
        table.addComponent(nameConfig);

        ColumnConfig plNumberConfig = factory.newColumnConfig("plNumber", "偏离单编号", false);
        plNumberConfig.setWidth(150);
        plNumberConfig.setDataUtilityId("AnalysisDataUtility");
        table.addComponent(plNumberConfig);

        ColumnConfig modifierConfig = factory.newColumnConfig("iterationInfo.creator", false);
        modifierConfig.setLabel("修改者");
        modifierConfig.setWidth(100);
        table.addComponent(modifierConfig);

        ColumnConfig status = factory.newColumnConfig("state.state",true);
        status.setLabel("状态");
        status.setWidth(100);
        table.addComponent(status);

        return table;
    }

}
