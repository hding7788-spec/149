package ext.casc.process;

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

@GenAsPersistable(superClass = Managed.class,
                interfaces = { WTContainedIdentified.class, Typed.class, OrganizationOwnedImpl.class,
                ElectronicallySignable.class, ContentHolder.class,
                IdentityAccessControlled.class, RecentlyVisited.class, Externalizable.class },
                properties = {
                //零部件名称
                @GeneratedProperty(name = "name", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(required = true)),
                //零部件编号
                @GeneratedProperty(name = "number", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true, columnName = "ProcessTaskNumber")),
                //零部件的版本
                @GeneratedProperty(name = "zhuti", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //零部件的版本
                @GeneratedProperty(name = "xiangmubu", type = String.class, supportedAPI = SupportedAPI.PUBLIC)},
                iconProperties = @IconProperties(standardIcon = "wtcore/images/projactivity.gif",
                openIcon = "wtcore/images/projactivity.gif"))
public class ProcessPlan extends _ProcessPlan {
    public static final long serialVersionUID = 1;

    public static ProcessPlan newProcessTask() throws WTException {
        ProcessPlan instance = new ProcessPlan();
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
