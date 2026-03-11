package ext.casc.preview;

import com.ptc.windchill.annotations.metadata.*;
import wt.fc.WTObject;
import wt.util.WTException;

import java.io.Externalizable;

@GenAsPersistable(
        superClass = WTObject.class,
        interfaces = Externalizable.class ,
        properties = {
        @GeneratedProperty(name = "name", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(required = true)),
        @GeneratedProperty(name = "number", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true, columnName = "PreOjbNumber")),
        @GeneratedProperty(name = "objType", type = String.class,supportedAPI = SupportedAPI.PUBLIC),
        @GeneratedProperty(name = "objVer", type = String.class,supportedAPI = SupportedAPI.PUBLIC),
        @GeneratedProperty(name = "modelMaturity", type = String.class,supportedAPI = SupportedAPI.PUBLIC),
        @GeneratedProperty(name = "maturityReason", type = String.class,supportedAPI = SupportedAPI.PUBLIC),
        @GeneratedProperty(name = "description", type = String.class,supportedAPI = SupportedAPI.PUBLIC),
        @GeneratedProperty(name = "designer", type = String.class,supportedAPI = SupportedAPI.PUBLIC),
        @GeneratedProperty(name = "designCompany", type = String.class,supportedAPI = SupportedAPI.PUBLIC)
        })
public class PreviewObject extends _PreviewObject {
    public static final long serialVersionUID = 1;

    public static PreviewObject newPreviewObject() throws WTException {
        PreviewObject instance = new PreviewObject();
        instance.initialize();
        return instance;
    }
}
