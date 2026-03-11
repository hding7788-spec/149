package ext.casc.product.mvc.builder;

import java.util.ArrayList;
import java.util.List;

import wt.pdmlink.PDMLinkProduct;
import wt.util.WTException;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;

@ComponentBuilder("ext.casc.product.mvc.builder.ProductRoleListBuilder")
public class ProductRoleListBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        NmCommandBean commandbean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
        List seleted = commandbean.getSelectedContextsForPopup();
        List<PDMLinkProduct> list = new ArrayList<PDMLinkProduct>();
        if (seleted != null && !seleted.isEmpty()) {
            for (Object object : seleted) {
                if (object instanceof NmContext) {
                    NmContext nmContext = (NmContext) object;
                    NmOid nmOid = nmContext.getTargetOid();
                    PDMLinkProduct product = (PDMLinkProduct) nmOid.getRefObject();
                    list.add(product);
                }
            }
        }
        commandbean.getRequest().getSession().setAttribute("productList", list);
        return list;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setLabel("角色");
        tableConfig.setActionModel("productTeam_manageRole_tableRole_actions");
        ((JcaTableConfig)tableConfig).setDescriptorProperty("ptype", "gridfileinputhandler");
        tableConfig.setSelectable(true);
        tableConfig.setId("ProductRoleListBuilder");
        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        tableConfig.addComponent(icon);

        ColumnConfig nameColumnConfig = factory.newColumnConfig("name", true);
        nameColumnConfig.setAutoSize(true);
        nameColumnConfig.setDataUtilityId("RoleDataUtility");
        tableConfig.addComponent(nameColumnConfig);

        ColumnConfig currentUsersColumnConfig = factory.newColumnConfig("currentRoles", true);
        currentUsersColumnConfig.setAutoSize(true);
        currentUsersColumnConfig.setLabel("角色");
        currentUsersColumnConfig.setDataUtilityId("RoleDataUtility");
        tableConfig.addComponent(currentUsersColumnConfig);

        return tableConfig;
    }

}
