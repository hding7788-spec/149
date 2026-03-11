package ext.casc.purge.mvc.builder;

import javax.servlet.http.HttpSession;

import wt.fc.QueryResult;
import wt.inf.container.WTContained;
import wt.pdmlink.PDMLinkProduct;
import wt.util.WTException;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.purge.DataPurger;

@ComponentBuilder("ext.casc.purge.mvc.builder.DeleteInvalidEPMWorkspaceDataBuilder")
public class DeleteInvalidEPMWorkspaceDataBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        PDMLinkProduct product = null;
        NmCommandBean nmcommandbean = ((JcaComponentParams)params).getHelperBean().getNmCommandBean();
        WTContained contained = nmcommandbean.getContainer();
        QueryResult qResult = DataPurger.resultForPurgeEpmWorkspace(contained);
        QueryResult qResult2 = DataPurger.resultWorkspace(contained);
        HttpSession session = nmcommandbean.getRequest().getSession();
        session.setAttribute("workspResult", qResult);
        session.setAttribute("workspResult2", qResult2);
        return qResult;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig table = factory.newTableConfig();
        table.setLabel("从工作空间删除的CAD文档");
        ColumnConfig icon = factory.newColumnConfig("type_icon",false);
        table.addComponent(icon);
        table.addComponent(factory.newColumnConfig("number", true));
        table.addComponent(factory.newColumnConfig("name", true));
        table.addComponent(factory.newColumnConfig("version", false));    
        table.addComponent(factory.newColumnConfig("creator", false));
        
        return table;
    }

}
