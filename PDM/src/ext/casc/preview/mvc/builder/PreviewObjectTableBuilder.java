package ext.casc.preview.mvc.builder;

import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.mvc.components.*;
import ext.casc.preview.PreviewTreeHandler;
import wt.util.WTException;

public class PreviewObjectTableBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig componentconfig,
			ComponentParams componentparams) throws Exception {
		// TODO Auto-generated method stub
		return new PreviewTreeHandler();
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params)
			throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TreeConfig treeConfig = factory.newTreeConfig();
        treeConfig.setLabel("预审数据列表");
        treeConfig.setComponentMode(ComponentMode.VIEW);
        treeConfig.setConfigurable(false);
        treeConfig.setId("ext.casc.preview.mvc.builder.PreviewObjectTableBuilder");
        treeConfig.setExpansionLevel("full");


        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setInfoPageLink(false);
        numberConfig.setWidth(200);
        treeConfig.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("Name", false);
        nameConfig.setWidth(200);
        treeConfig.addComponent(nameConfig);

        ColumnConfig versionConfig = factory.newColumnConfig("ObjVer", false);
        versionConfig.setWidth(100);
        versionConfig.setLabel("版本");
        treeConfig.addComponent(versionConfig);

        ColumnConfig modelMaturityConfig = factory.newColumnConfig("modelMaturity", false);
        modelMaturityConfig.setWidth(100);
        modelMaturityConfig.setLabel("模型成熟度");
        treeConfig.addComponent(modelMaturityConfig);

        ColumnConfig maturityReasonConfig = factory.newColumnConfig("maturityReason", false);
        maturityReasonConfig.setWidth(200);
        maturityReasonConfig.setLabel("成熟度变化原因");
        treeConfig.addComponent(maturityReasonConfig);

        ColumnConfig typeConfig = factory.newColumnConfig("ObjType", false);
        typeConfig.setLabel("类型");
        typeConfig.setWidth(100);
        treeConfig.addComponent(typeConfig);

		return treeConfig;
	}

}
