package ext.casc.analysisActivity.bean;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.SupportedAPI;
import wt.change2.WTAnalysisActivity;
import wt.fc.ObjectToObjectLink;
import wt.fc.WTObject;
import wt.util.WTException;

import java.io.Externalizable;

@GenAsBinaryLink(
        superClass = ObjectToObjectLink.class,
        interfaces = Externalizable.class,
        properties = {
                @GeneratedProperty(name = "plNumber", type = String.class, supportedAPI = SupportedAPI.PRIVATE)
        },
        roleA = @GeneratedRole(name = "analysisActivity", type = WTAnalysisActivity.class),
        roleB = @GeneratedRole(name = "sourceObject", type = WTObject.class))
public class AnalysisToSourceLink extends _AnalysisToSourceLink {
    static final long serialVersionUID = 1L;

    public static AnalysisToSourceLink newAnalysisToSourceLink(WTAnalysisActivity analysisActivity, WTObject sourceObject)
            throws WTException {

        AnalysisToSourceLink instance = new AnalysisToSourceLink();
        instance.initialize(analysisActivity, sourceObject);
        return instance;
    }
}
