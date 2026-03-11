package ext.sast.supply;

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
                @GeneratedProperty(name = "glSupply", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                @GeneratedProperty(name = "theRevisionControlled", type = String.class, supportedAPI = SupportedAPI.PUBLIC)

        },
        roleA = @GeneratedRole(name = "glSupply", type = GLSupply.class),
        roleB = @GeneratedRole(name = "theRevisionControlled", type = RevisionControlled.class))
public class GLSupplyDescLink extends _GLSupplyDescLink {
    static final long serialVersionUID = 1L;

    public static GLSupplyDescLink newGLSupplyDescLink(GLSupply glSupply,
            RevisionControlled theRevisionControlled)
            throws WTException {
    	GLSupplyDescLink instance = new GLSupplyDescLink();
        instance.initialize(glSupply, theRevisionControlled);
        return instance;
    }
}
