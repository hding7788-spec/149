package ext.casc.product.mvc.builder;

import wt.util.WTException;

import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;

import ext.casc.util.WCUtil;

@ComponentBuilder("ext.casc.product.mvc.builder.ProductListBuilder")
public class ProductListBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {

        return WCUtil.getProductList();
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setLabel("产品");
        tableConfig.setSelectable(true);
        tableConfig.setActionModel("productTeam_manage_table_actions");
        ((JcaTableConfig)tableConfig).setDescriptorProperty("ptype", "gridfileinputhandler");
        ColumnConfig icon = factory.newColumnConfig("type_icon",false);
        tableConfig.addComponent(icon);

        ColumnConfig nameColumnConfig = factory.newColumnConfig("name", true);
        nameColumnConfig.setAutoSize(true);
        nameColumnConfig.setInfoPageLink(true);
        tableConfig.addComponent(nameColumnConfig);

        ColumnConfig ownerColumnConfig = factory.newColumnConfig("owner", true);
        ownerColumnConfig.setAutoSize(true);
        tableConfig.addComponent(ownerColumnConfig);

        ColumnConfig creatorColumnConfig = factory.newColumnConfig("creator", true);
        creatorColumnConfig.setAutoSize(true);
        tableConfig.addComponent(creatorColumnConfig);

        ColumnConfig createDataColumnConfig = factory.newColumnConfig("thePersistInfo.createStamp", true);
        createDataColumnConfig.setAutoSize(true);
        tableConfig.addComponent(createDataColumnConfig);

        ColumnConfig createxhlxColumnConfig = factory.newColumnConfig("IBA|XHLX", true);
        createxhlxColumnConfig.setLabel("型号类型");
        createxhlxColumnConfig.setAutoSize(true);
        tableConfig.addComponent(createxhlxColumnConfig);

        return tableConfig;
    }

}
