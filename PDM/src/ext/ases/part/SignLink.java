package ext.ases.part;

import java.io.Externalizable;

import wt.fc.ObjectToObjectLink;
import wt.fc.WTObject;
import wt.util.WTException;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.SupportedAPI;

@GenAsBinaryLink(
        superClass = ObjectToObjectLink.class, 
        interfaces = Externalizable.class, 
        properties = {
                @GeneratedProperty(name = "signObject", type = String.class,supportedAPI = SupportedAPI.PUBLIC),
                @GeneratedProperty(name = "theSignature", type = String.class,supportedAPI = SupportedAPI.PUBLIC)
        },
        roleA = @GeneratedRole(name = "signObject", type = WTObject.class),
        roleB = @GeneratedRole(name = "theSignature", type = ASESHuiqianSignature.class))
public class SignLink extends _SignLink{
    public static final long serialVersionUID = 1;

    public static SignLink newSignLink(WTObject signObject,ASESHuiqianSignature theSignature) throws WTException {
        SignLink instance = new SignLink();
        instance.initialize(signObject,theSignature);
        return instance;
    }
}
