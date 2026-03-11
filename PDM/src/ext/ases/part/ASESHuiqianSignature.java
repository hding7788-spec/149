package ext.ases.part;

import java.io.Externalizable;

import wt.fc.WTObject;
import wt.util.WTException;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.SupportedAPI;

@GenAsPersistable(
        superClass = WTObject.class, 
        interfaces = Externalizable.class , 
        properties = {
        @GeneratedProperty(name = "activity", type = String.class,supportedAPI = SupportedAPI.PUBLIC),
        @GeneratedProperty(name = "conclusion", type = String.class,supportedAPI = SupportedAPI.PUBLIC),
        @GeneratedProperty(name = "signature", type = String.class,supportedAPI = SupportedAPI.PUBLIC),
        @GeneratedProperty(name = "opinion", type = String.class,supportedAPI = SupportedAPI.PUBLIC),
        @GeneratedProperty(name = "updatestate", type = String.class,supportedAPI = SupportedAPI.PUBLIC),
        @GeneratedProperty(name = "implementadvise", type = String.class,supportedAPI = SupportedAPI.PUBLIC)
        })
public class ASESHuiqianSignature extends _ASESHuiqianSignature {
    public static final long serialVersionUID = 1;

    public static ASESHuiqianSignature newASESHuiqianSignature() throws WTException {
        ASESHuiqianSignature instance = new ASESHuiqianSignature();
        instance.initialize();
        return instance;
    }
}
