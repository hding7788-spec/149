package ext.casc.workflow.tree.mvc.builder;

import java.util.ArrayList;
import java.util.List;

import wt.fc.Persistable;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.preview.Preview;
import ext.casc.preview.PreviewUtil;

@ComponentBuilder("ext.casc.workflow.tree.mvc.builder.SetPreviewDataBuilder")
public class SetPreviewDataBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
        NmCommandBean commandBean = ((JcaComponentParams) arg1).getHelperBean().getNmCommandBean();
        WorkItem wi = (WorkItem) commandBean.getPageOid().getRefObject();
        Persistable pbo = (Persistable) wi.getPrimaryBusinessObject().getObject();
        List<Object> list = new ArrayList<Object>();
        if(pbo instanceof Preview) {
        	Preview pre = (Preview)pbo;
        	list.addAll(PreviewUtil.getAllMembers(pre));
        }
        return list;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();

        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setLabel("签审列表");
        tableConfig.setComponentMode(ComponentMode.VIEW);
        tableConfig.setConfigurable(false);
        tableConfig.setId("ext.casc.workflow.tree.mvc.builder.SetPreviewDataBuilder");
        tableConfig.setSelectable(true);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setInfoPageLink(false);
        numberConfig.setWidth(150);
        tableConfig.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        nameConfig.setWidth(150);
        tableConfig.addComponent(nameConfig);

        ColumnConfig huiqianresult = factory.newColumnConfig("sign_result", false);
        huiqianresult.setLabel("会签结论");
        huiqianresult.setDataUtilityId("PreviewDataUtility");
        huiqianresult.setWidth(100);
        tableConfig.addComponent(huiqianresult);

        ColumnConfig huiqianadvise = factory.newColumnConfig("sign_advise", false);
        huiqianadvise.setLabel("会签意见");
        huiqianadvise.setDataUtilityId("PreviewDataUtility");
        huiqianadvise.setWidth(200);
        tableConfig.addComponent(huiqianadvise);

        ColumnConfig sign_person = factory.newColumnConfig("sign_person", false);
        sign_person.setLabel("指派工艺师");
        sign_person.setDataUtilityId("PreviewDataUtility");
        sign_person.setWidth(200);
        tableConfig.addComponent(huiqianadvise);

        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
        versionConfig.setAutoSize(true);
        versionConfig.setWidth(50);
        tableConfig.addComponent(versionConfig);

        return tableConfig;
    }

}
