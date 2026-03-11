package ext.ases.changerequest;

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
                @GeneratedProperty(name = "implement", type = String.class, supportedAPI = SupportedAPI.PRIVATE),
                @GeneratedProperty(name = "theChangeRequest", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                @GeneratedProperty(name = "theRevisionControlled", type = String.class, supportedAPI = SupportedAPI.PUBLIC)

        },
        roleA = @GeneratedRole(name = "theChangeRequest", type = ChangeRequest.class),
        roleB = @GeneratedRole(name = "theRevisionControlled", type = RevisionControlled.class))
public class ChangeRequestAffectLink extends _ChangeRequestAffectLink {
    static final long serialVersionUID = 1L;

    public static ChangeRequestAffectLink newChangeRequestAffectLink(ChangeRequest theChangeRequest,
            RevisionControlled theRevisionControlled)
             throws WTException {

        ChangeRequestAffectLink instance = new ChangeRequestAffectLink();
        instance.initialize(theChangeRequest, theRevisionControlled);
        return instance;
    }
}
