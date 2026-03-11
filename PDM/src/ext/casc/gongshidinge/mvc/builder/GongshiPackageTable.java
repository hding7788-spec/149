package ext.casc.gongshidinge.mvc.builder;

import com.glaway.mpm.util.MPMProcessPlanUtil;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.ptc.ViewWIHelper;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ComponentBuilder("ext.casc.gongshidinge.mvc.builder.GongshiPackageTable")
public class GongshiPackageTable extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1)
            throws Exception {
        NmCommandBean cb = ((JcaComponentParams) arg1).getHelperBean().getNmCommandBean();
        Persistable p = (Persistable) cb.getPageOid().getRefObject();
        Set<ProcessEnvelope> pes = new HashSet<>();
        if(p instanceof WTDocument) {
            MPMProcessPlan plan = MPMProcessPlanUtil.getProcessPlanByWTDocument((WTDocument) p);
            if(plan != null) {
                List<MPMOperationUsageLink> links = ViewWIHelper.getMPMOperationUsageLinkByMpmPr(plan);
                for(MPMOperationUsageLink link : links) {
                    if(link.getRoleBObject() != null && link.getRoleBObject() instanceof MPMOperationMaster) {
                        MPMOperationMaster master = (MPMOperationMaster) link.getRoleBObject();
                        MPMOperation operation = ViewWIHelper.getMpmOperation(master.getNumber());
                        QueryResult qr = PersistenceHelper.manager.navigate(operation, "theProcessEnvelope", EnvelopeMemberLink.class, false);
                        while(qr.hasMoreElements()) {
                            EnvelopeMemberLink memberLink = (EnvelopeMemberLink) qr.nextElement();
                            ProcessEnvelope pe = memberLink.getProcessEnvelope();
                            pes.add(pe);
                        }
                    }
                }
            }
        }
        return pes;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams arg0)
            throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig table = factory.newTableConfig();
        table.setLabel("关联工时定额签审包");

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        table.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", true);
        numberConfig.setInfoPageLink(true);
        table.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", true);
        table.addComponent(nameConfig);

        ColumnConfig creatorConfig = factory.newColumnConfig("Creator", true);
        table.addComponent(creatorConfig);

        ColumnConfig createTimeConfig = factory.newColumnConfig("CreateTimestamp", true);
        createTimeConfig.setLabel("创建时间");
        table.addComponent(createTimeConfig);

        return table;
    }

}
