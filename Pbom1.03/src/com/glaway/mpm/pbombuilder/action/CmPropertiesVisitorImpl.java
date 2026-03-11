package com.glaway.mpm.pbombuilder.action;

import java.util.Collection;
import com.ptc.pview.pvkapp.PropertiesVisitorImpl;

public class CmPropertiesVisitorImpl extends PropertiesVisitorImpl {
	private Collection<String[]> properties;
	
	public Collection<String[]> getProperties() {
		return properties;
	}
	
	public void setProperties(Collection<String[]> properties) {
		this.properties = properties;
	}
}