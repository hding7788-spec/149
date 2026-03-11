package ext.ases.envelope;

import java.io.Externalizable;

import wt.enterprise.RevisionControlled;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.SupportedAPI;

@GenAsBinaryLink(
        superClass = ObjectToObjectLink.class,
        interfaces = Externalizable.class,
        properties = { @GeneratedProperty(name = "topObject", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                @GeneratedProperty(name = "topForEnvelope", type = String.class, supportedAPI = SupportedAPI.PUBLIC)

        },
        roleA = @GeneratedRole(name = "topForEnvelope", type = ProcessEnvelope.class),
        roleB = @GeneratedRole(name = "topObject", type = RevisionControlled.class))
public class EnvelopeTopObjLink extends _EnvelopeTopObjLink{
    static final long serialVersionUID = 1L;

    public static EnvelopeTopObjLink newEnvelopeTopObjLink(ProcessEnvelope topForEnvelope, RevisionControlled topObject)
            throws WTException {

        EnvelopeTopObjLink instance = new EnvelopeTopObjLink();
        instance.initialize(topForEnvelope, topObject);
        return instance;
    }
}
