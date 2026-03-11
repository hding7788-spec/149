package ext.ases.changerequest.mvc.builder;

import wt.util.WTException;

import com.ptc.jca.mvc.builders.DefaultInfoComponentBuilder;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentId;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.InfoComponentConfigFactory;
import com.ptc.mvc.components.InfoConfig;
import com.ptc.mvc.components.TypeBased;

import ext.ases.changerequest.ChangeRequest;

@ComponentBuilder(value = ComponentId.INFOPAGE_ID)
@TypeBased(value = "ext.ases.changerequest.ChangeRequest")
public class ChangeRequestInfoPageBuilder extends DefaultInfoComponentBuilder {

    @Override
    protected InfoConfig buildInfoConfig(ComponentParams params) throws WTException {
        InfoComponentConfigFactory configFactory = getComponentConfigFactory();
        InfoConfig config = configFactory.newInfoConfig();
        config.setNavBarName("third_level_nav_changerequest");
        config.setTabSet("changeRequestInfoPageTabSet");
        config.setType(ChangeRequest.class.getName());       
        return config;
    }
}
