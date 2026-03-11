package ext.ases.changerequest;

import java.io.Externalizable;

import wt.access.IdentityAccessControlled;
import wt.content.ContentHolder;
import wt.enterprise.Managed;
import wt.fc.IdentificationObject;
import wt.inf.container.WTContainedIdentified;
import wt.org.OrganizationOwnedImpl;
import wt.org.WTOrganization;
import wt.org.electronicIdentity.ElectronicallySignable;
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

@GenAsPersistable(superClass = Managed.class, interfaces = {
        WTContainedIdentified.class, Typed.class, OrganizationOwnedImpl.class,
        ElectronicallySignable.class, ContentHolder.class,
        IdentityAccessControlled.class, RecentlyVisited.class,
        Externalizable.class }, properties = {
        @GeneratedProperty(name = "name", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(required = true)),
        @GeneratedProperty(name = "number", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true, columnName = "ChangeRequest")),
        @GeneratedProperty(name = "description", type = java.lang.String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(upperLimit = 2000)),
        @GeneratedProperty(name = "avidmtype", type = java.lang.String.class, supportedAPI = SupportedAPI.PUBLIC),
        @GeneratedProperty(name = "requesttype", type = java.lang.String.class, supportedAPI = SupportedAPI.PUBLIC),
        @GeneratedProperty(name = "requestproprity", type = java.lang.String.class, supportedAPI = SupportedAPI.PUBLIC),
        @GeneratedProperty(name = "template", type = java.lang.String.class, supportedAPI = SupportedAPI.PUBLIC),
        @GeneratedProperty(name = "solution", type = java.lang.String.class, supportedAPI = SupportedAPI.PUBLIC),
        @GeneratedProperty(name = "remark", type = java.lang.String.class, supportedAPI = SupportedAPI.PUBLIC),
        @GeneratedProperty(name = "cost", type = java.lang.String.class, supportedAPI = SupportedAPI.PUBLIC),
        @GeneratedProperty(name = "implement", type = java.lang.String.class, supportedAPI = SupportedAPI.PUBLIC) },

        iconProperties = @IconProperties(standardIcon = "netmarkets/images/chgreqst.gif", openIcon = "netmarkets/images/chgreqst.gif"))
public class ChangeRequest extends _ChangeRequest {
    public static final long serialVersionUID = 1;

    public static ChangeRequest newChangeRequest() throws WTException {
        ChangeRequest instance = new ChangeRequest();
        instance.initialize();
        return instance;
    }

    public IdentificationObject getIdentificationObject() throws WTException {

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
