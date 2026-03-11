package ext.casc.common.mvc.builder.document;

import com.glaway.mpm.util.EPMDocUtil;
import com.ptc.core.components.descriptor.DescriptorConstants.TableTreeProperties;
import com.ptc.jca.mvc.components.JcaTreeConfig;
import com.ptc.mvc.components.*;
import com.ptc.mvc.components.ds.DataSourceMode;
import wt.epm.EPMDocument;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.List;

@ComponentBuilder("ext.casc.common.mvc.builder.document.EPMStructureTreeBuilder")
public class EPMStructureTreeBuilder extends AbstractComponentConfigBuilder implements TreeDataBuilderAsync {

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams componentParams) throws WTException {
		ComponentConfigFactory configFactory = getComponentConfigFactory();
		JcaTreeConfig treeConfig = (JcaTreeConfig) configFactory.newTreeConfig();
		treeConfig.setDataSourceMode(DataSourceMode.ASYNCHRONOUS);
		treeConfig.setLabel("模型结构树");
		treeConfig.setExpansionLevel(TableTreeProperties.NO_EXPAND);
		treeConfig.setSelectable(false);
		treeConfig.setNodeColumn("number");

		ColumnConfig number = configFactory.newColumnConfig("number", true);
		number.setWidth(300);
		treeConfig.addComponent(number);

		ColumnConfig name = configFactory.newColumnConfig("name", true);
		name.setWidth(300);
		treeConfig.addComponent(name);

		ColumnConfig maturity = configFactory.newColumnConfig("maturity", true);
		maturity.setWidth(300);
		maturity.setDataUtilityId("PreviewDataUtility");
		treeConfig.addComponent(maturity);

		ColumnConfig version = configFactory.newColumnConfig("version", true);
		treeConfig.addComponent(version);

		return treeConfig;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void buildNodeData(Object node, ComponentResultProcessor resultProcessor) throws Exception {
		if(node == TreeNode.RootNode) {
			resultProcessor.setPresorted(false);
			Object object = resultProcessor.getParams().getContextObject();
			List<Object> list = new ArrayList<Object>();
			if(object != null && object instanceof EPMDocument) {
				EPMDocument epmDocument = (EPMDocument) object;
				list.add(epmDocument);
			}
			resultProcessor.addElements(list);
		} else {
			List<EPMDocument> list = new ArrayList<EPMDocument>();
			if(node != null && node instanceof EPMDocument) {
				list = EPMDocUtil.getMemberEPMDoc((EPMDocument) node);
			}
			resultProcessor.addElements(list);
		}
	}
}
