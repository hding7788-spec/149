package ext.casc.common.mvc.builder.analysis;

import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import wt.change2.ChangeHelper2;
import wt.change2.ChangeRequestIfc;
import wt.change2.WTChangeProposal;
import wt.change2.WTChangeRequest2;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.List;

@ComponentBuilder("ext.casc.common.mvc.builder.analysis.RelatedAnalysisActivityBuilder")
public class RelatedAnalysisActivityBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1)
            throws Exception {
        List aa = new ArrayList();
        NmCommandBean cb = ((JcaComponentParams) arg1).getHelperBean().getNmCommandBean();
        Persistable p = (Persistable) cb.getPageOid().getRefObject();
        if(p instanceof WTChangeRequest2){
            QueryResult qr = ChangeHelper2.service.getChangeProposals((ChangeRequestIfc) p);
            while(qr.hasMoreElements()) {
                WTChangeProposal cp = (WTChangeProposal) qr.nextElement();
                QueryResult result = ChangeHelper2.service.getLatestAnalysisActivity(cp);
                while(result.hasMoreElements()) {
                    aa.add(result.nextElement());
                }
            }
        }
        return aa;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams arg0)
            throws WTException {
        ComponentConfigFactory factory = this.getComponentConfigFactory();
        TableConfig table = factory.newTableConfig();
        table.setId("analysisActivity_analysisActivities_table");
        table.setType("wt.change2.AnalysisActivity");
        table.setLabel("关联更改影响分析");
        table.setSelectable(false);
        table.setHelpContext("change_analysisActivityTable_info");
        table.addComponent(factory.newColumnConfig("type_icon", false));
        ColumnConfig number = factory.newColumnConfig();
        number.setId("number");
        number.setDefaultSort(true);
        table.addComponent(number);
        table.addComponent(factory.newColumnConfig("orgid", false));
        table.addComponent(factory.newColumnConfig("revision", false));
        table.addComponent(factory.newColumnConfig("infoPageAction", false));
        ColumnConfig nmActions = factory.newColumnConfig();
        nmActions.setId("nmActions");
        nmActions.setComponentMode(ComponentMode.VIEW);
        nmActions.setSortable(false);
        nmActions.setActionModel("analysis_activity_row_actions");
        table.addComponent(nmActions);
        table.addComponent(factory.newColumnConfig("name", false));
        table.addComponent(factory.newColumnConfig("lifeCycleState", false));
        table.addComponent(factory.newColumnConfig("thePersistInfo.modifyStamp", false));
        table.addComponent(factory.newColumnConfig("modifier.name","修改者",false));

        return table;
    }

}
