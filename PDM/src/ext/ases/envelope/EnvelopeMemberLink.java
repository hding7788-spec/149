package ext.ases.envelope;

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
                @GeneratedProperty(name = "theProcessEnvelope", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                @GeneratedProperty(name = "theRevisionControlled", type = String.class, supportedAPI = SupportedAPI.PUBLIC)

        },
        roleA = @GeneratedRole(name = "theProcessEnvelope", type = ProcessEnvelope.class),
        roleB = @GeneratedRole(name = "theRevisionControlled", type = RevisionControlled.class))
public class EnvelopeMemberLink extends _EnvelopeMemberLink {
    static final long serialVersionUID = 1L;

    public static EnvelopeMemberLink newEnvelopeMemberLink(ProcessEnvelope theProcessEnvelope,
            RevisionControlled theRevisionControlled)
            throws WTException {

        EnvelopeMemberLink instance = new EnvelopeMemberLink();
        instance.initialize(theProcessEnvelope, theRevisionControlled);
        return instance;
    }
}
