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

import ext.casc.process.ProcessTaskItem;

@ComponentBuilder(value = ComponentId.INFOPAGE_ID)
@TypeBased(value = "ext.casc.process.ProcessTaskItem")
public class ProcessTaskItemInfoBuilder extends AbstractInfoConfigBuilder implements ComponentDataBuilder {

    @Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
        return new ProcessTaskItem();
    }

    @Override
    protected InfoConfig buildInfoConfig(ComponentParams arg0) throws WTException {
        InfoComponentConfigFactory factory = getComponentConfigFactory();
        InfoConfig result = factory.newInfoConfig();
        result.setView("/customProcess/processTaskItemInfoPage.jsp");
        result.setType(ProcessTaskItem.class.getName());

        return result;
    }

}
