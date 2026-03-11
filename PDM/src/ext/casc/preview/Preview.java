package ext.casc.preview;

import java.io.Externalizable;

import wt.access.IdentityAccessControlled;
import wt.content.FormatContentHolder;
import wt.enterprise.Managed;
import wt.fc.IdentificationObject;
import wt.inf.container.WTContainedIdentified;
import wt.org.OrganizationOwnedImpl;
import wt.org.WTOrganization;
import wt.recent.RecentlyVisited;
import wt.type.TypeDefinitionInfo;
import wt.type.Typed;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.IconProperties;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.SupportedAPI;

@GenAsPersistable(superClass = Managed.class,
                  interfaces = { WTContainedIdentified.class, Typed.class, OrganizationOwnedImpl.class, FormatContentHolder.class,
                          IdentityAccessControlled.class, RecentlyVisited.class, Externalizable.class },
                  properties = {
                          @GeneratedProperty(name = "name", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(required = true)),
                          @GeneratedProperty(name = "number", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true, columnName = "PreviewNumber")),
                          @GeneratedProperty(name = "description", type = java.lang.String.class, supportedAPI = SupportedAPI.PUBLIC),
                          @GeneratedProperty(name = "designer", type = String.class,supportedAPI = SupportedAPI.PUBLIC),
                          @GeneratedProperty(name = "designCompany", type = String.class,supportedAPI = SupportedAPI.PUBLIC)},
                          iconProperties = @IconProperties(standardIcon = "",
                                  openIcon = ""))
public class Preview extends _Preview {
    public static final long serialVersionUID = 1;

    public static Preview newPreview()
            throws WTException {
        Preview instance = new Preview();
        instance.initialize();
        return instance;
    }

    public IdentificationObject getIdentificationObject()
            throws WTException {

        return null;
    }

    public String getFlexTypeIdPath() {

        return null;
    }

    public Object getValue() {

        return null;
    }

    public void setValue(String key, String value) {
    }

    public TypeDefinitionInfo getTypeDefinitionInfo() {
        return null;
    }

    public void setOrganization(WTOrganization a_Organization)
            throws WTPropertyVetoException {
    }

    public WTOrganization getOrganization() {

        return null;
    }

}
