package ext.casc.workflow.mvc.builder;

import java.util.ArrayList;
import java.util.List;

import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ases.envelope.EnvelopeMemberLink;

@ComponentBuilder("ext.casc.workflow.mvc.builder.EnvelopPackageTable")
public class EnvelopPackageTable extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1)
			throws Exception {
		NmCommandBean cb = ((JcaComponentParams) arg1).getHelperBean().getNmCommandBean();
		Object p = cb.getPageOid().getRefObject();
		QueryResult qr = PersistenceHelper.manager.navigate((Persistable)p, "theProcessEnvelope",
	                EnvelopeMemberLink.class, false);

		Persistable obj = null;
        List envelopes = new ArrayList();
        for (; qr.hasMoreElements(); envelopes.add(obj)) {
            obj = ((EnvelopeMemberLink) qr.nextElement()).getProcessEnvelope();
        }

        return envelopes;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0)
			throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig table = factory.newTableConfig();
        table.setLabel("关联批量签审单");

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        table.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setInfoPageLink(true);
        table.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        table.addComponent(nameConfig);

        return table;
	}

}
