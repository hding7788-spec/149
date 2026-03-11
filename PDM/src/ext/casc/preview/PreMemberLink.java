package ext.casc.preview;

import java.io.Externalizable;

import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.SupportedAPI;

@GenAsBinaryLink(
        superClass = ObjectToObjectLink.class,
        interfaces = Externalizable.class,
        properties = {
                @GeneratedProperty(name = "description", type = String.class, supportedAPI = SupportedAPI.PRIVATE),
                @GeneratedProperty(name = "implement", type = String.class, supportedAPI = SupportedAPI.PRIVATE)

        },
        roleA = @GeneratedRole(name = "thePreview", type = Preview.class),
        roleB = @GeneratedRole(name = "thePreviewObj", type = PreviewObject.class))
public class PreMemberLink extends _PreMemberLink {
    static final long serialVersionUID = 1L;

    public static PreMemberLink newPreMemberLink(Preview thePreview,
    		PreviewObject thePreviewObj)
             throws WTException {

        PreMemberLink instance = new PreMemberLink();
        instance.initialize(thePreview, thePreviewObj);
        return instance;
    }
}
