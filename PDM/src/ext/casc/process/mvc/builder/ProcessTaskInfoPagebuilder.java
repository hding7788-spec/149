package ext.casc.process.mvc.builder;

import wt.util.WTException;

import com.ptc.mvc.components.AbstractInfoConfigBuilder;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentDataBuilder;
import com.ptc.mvc.components.ComponentId;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.InfoComponentConfigFactory;
import com.ptc.mvc.components.InfoConfig;
import com.ptc.mvc.components.TypeBased;

import ext.casc.process.ProcessTask;

@ComponentBuilder(value = ComponentId.INFOPAGE_ID)
@TypeBased(value = "ext.casc.process.ProcessTask")
public class ProcessTaskInfoPagebuilder extends AbstractInfoConfigBuilder implements ComponentDataBuilder {

    @Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
        return new ProcessTask();
    }

    @Override
    protected InfoConfig buildInfoConfig(ComponentParams arg0) throws WTException {
        InfoComponentConfigFactory factory = getComponentConfigFactory();
        InfoConfig result = factory.newInfoConfig();
        result.setView("/customProcess/processTaskInfoPage.jsp");
        result.setType(ProcessTask.class.getName());

        result.setNavBarName("custom_nav_actions");
        result.setTabSet("custom_tabset_actions");

        return result;
    }

}
