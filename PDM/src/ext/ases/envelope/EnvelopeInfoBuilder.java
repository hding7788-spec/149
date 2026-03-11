package ext.ases.envelope;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import wt.log4j.LogR;
import wt.util.WTException;

import com.ptc.jca.mvc.builders.DefaultInfoComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.InfoComponentConfigFactory;
import com.ptc.mvc.components.InfoConfig;
import com.ptc.mvc.components.TypeBased;

public class EnvelopeInfoBuilder extends DefaultInfoComponentBuilder{
    private static final Logger log = LogR.getLogger(EnvelopeInfoBuilder.class.getName());
    @Override
    protected InfoConfig buildInfoConfig(ComponentParams arg0) throws WTException {
        log.debug("Info page config begin");
        InfoComponentConfigFactory infocomponentconfigfactory = getComponentConfigFactory();
        InfoConfig infoconfig = infocomponentconfigfactory.newInfoConfig();
        List list = infocomponentconfigfactory.getStandardStatusConfigs();
        ComponentConfig componentconfig;
        for(Iterator iterator = list.iterator(); iterator.hasNext(); infoconfig.addComponent(componentconfig))
            componentconfig = (ComponentConfig)iterator.next();
        infoconfig.setNavBarName("third_level_nav_envelope");
        infoconfig.setTabSet("envelopeInfoPageTabSet");
        infoconfig.setType(ProcessEnvelope.class.getName());
        return infoconfig;
    }

}
