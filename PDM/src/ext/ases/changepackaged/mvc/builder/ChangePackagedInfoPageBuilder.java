package ext.ases.changepackaged.mvc.builder;

import wt.util.WTException;

import com.ptc.jca.mvc.builders.DefaultInfoComponentBuilder;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentId;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.InfoComponentConfigFactory;
import com.ptc.mvc.components.InfoConfig;
import com.ptc.mvc.components.TypeBased;

import ext.ases.changepackaged.ChangePackaged;

@ComponentBuilder(value = ComponentId.INFOPAGE_ID)
@TypeBased(value = "ext.ases.changepackaged.ChangePackaged")
public class ChangePackagedInfoPageBuilder extends DefaultInfoComponentBuilder {

    @Override
    protected InfoConfig buildInfoConfig(ComponentParams params) throws WTException {
        InfoComponentConfigFactory configFactory = getComponentConfigFactory();
        InfoConfig config = configFactory.newInfoConfig();
        config.setNavBarName("third_level_nav_changepackaged");
        config.setTabSet("changePackagedInfoPageTabSet");
        config.setType(ChangePackaged.class.getName());       
        return config;
    }
}
