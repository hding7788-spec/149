package ext.ases.changepackaged;

import java.io.Externalizable;

import wt.enterprise.RevisionControlled;
import wt.util.WTException;
import wt.vc.ObjectToVersionLink;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.SupportedAPI;

@GenAsBinaryLink(
        superClass = ObjectToVersionLink.class,
        interfaces = Externalizable.class,
        properties = {
                @GeneratedProperty(name = "description", type = String.class, supportedAPI = SupportedAPI.PRIVATE),
                @GeneratedProperty(name = "implementadvise", type = String.class, supportedAPI = SupportedAPI.PRIVATE),
                @GeneratedProperty(name = "theChangePackaged", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                @GeneratedProperty(name = "theRevisionControlled", type = String.class, supportedAPI = SupportedAPI.PUBLIC)

        },
        roleA = @GeneratedRole(name = "theProcessEnvelope", type = ChangePackaged.class),
        roleB = @GeneratedRole(name = "theRevisionControlled", type = RevisionControlled.class))
public class ChangePackagedResultLink extends _ChangePackagedResultLink {
    static final long serialVersionUID = 1L;

    public static ChangePackagedResultLink newChangePackagedResultLink(ChangePackaged theChangePackaged,
            RevisionControlled theRevisionControlled)
             throws WTException {

        ChangePackagedResultLink instance = new ChangePackagedResultLink();
        instance.initialize(theChangePackaged, theRevisionControlled);
        return instance;
    }
}
