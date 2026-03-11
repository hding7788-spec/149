package ext.casc.preview.mvc.builder;

import wt.util.WTException;

import com.ptc.jca.mvc.builders.DefaultInfoComponentBuilder;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentId;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.InfoComponentConfigFactory;
import com.ptc.mvc.components.InfoConfig;
import com.ptc.mvc.components.TypeBased;

import ext.casc.preview.Preview;

@ComponentBuilder(value = ComponentId.INFOPAGE_ID)
@TypeBased(value = "ext.casc.preview.Preview")
public class PreviewInfoPageBuilder extends DefaultInfoComponentBuilder {

    @Override
    protected InfoConfig buildInfoConfig(ComponentParams params) throws WTException {
        InfoComponentConfigFactory configFactory = getComponentConfigFactory();
        InfoConfig config = configFactory.newInfoConfig();
        config.setNavBarName("third_level_nav_preview");
        config.setTabSet("previewInfoPageTabSet");
        config.setType(Preview.class.getName());
        return config;
    }
}
