package ext.ases.techMaterial;

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
		@GeneratedProperty(name = "number", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true,columnName = "TechnicsMaterialEntriesNumber")),
		@GeneratedProperty(name = "name", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties( columnName = "TechnicsMaterialEntriesName")),
		@GeneratedProperty(name = "description", type = java.lang.String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(upperLimit = 2000)) }, iconProperties = @IconProperties(standardIcon = "netmarkets/images/actionmenus.gif", 
		openIcon = "netmarkets/images/actionmenus.gif"))
public class TechnicsMaterialEntries extends _TechnicsMaterialEntries{
	public static final long serialVersionUID = 1;

	public static TechnicsMaterialEntries newTechnicsMaterial() throws WTException {
		TechnicsMaterialEntries instance = new TechnicsMaterialEntries();
		instance.initialize();
		return instance;
	}

	@Override
	public IdentificationObject getIdentificationObject() throws WTException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getFlexTypeIdPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeDefinitionInfo getTypeDefinitionInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue(String var1, String var2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public WTOrganization getOrganization() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOrganization(WTOrganization var1)
			throws WTPropertyVetoException {
		// TODO Auto-generated method stub
		
	}

}
