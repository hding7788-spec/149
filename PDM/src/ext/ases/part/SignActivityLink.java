package ext.ases.part;

import java.io.Externalizable;

import wt.fc.ObjectToObjectLink;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.SupportedAPI;

@GenAsBinaryLink(
        superClass = ObjectToObjectLink.class, 
        interfaces = Externalizable.class, 
        properties = {
                @GeneratedProperty(name = "theSignature", type = String.class,supportedAPI = SupportedAPI.PUBLIC),
                @GeneratedProperty(name = "theActivity", type = String.class,supportedAPI = SupportedAPI.PUBLIC)
        },
        roleA = @GeneratedRole(name = "theActivity", type = WfActivity.class),
        roleB = @GeneratedRole(name = "theSignature", type = ASESHuiqianSignature.class))
public class SignActivityLink extends _SignActivityLink{
    public static final long serialVersionUID = 1;

    public static SignActivityLink newSignActivityLink(WfActivity theActivity,ASESHuiqianSignature theSignature) throws WTException {
        SignActivityLink instance = new SignActivityLink();
        instance.initialize(theActivity,theSignature);
        return instance;
    }
}
