package com.glaway.mpm.visual.view.pview;

import java.util.Collection;
import com.ptc.pview.pvkapp.PropertiesVisitorImpl;

public class VaPropertiesVisitorImpl extends PropertiesVisitorImpl {
	private Collection<String[]> properties;
	
	public Collection<String[]> getProperties() {
		return properties;
	}
	
	public void setProperties(Collection<String[]> properties) {
		this.properties = properties;
	}
}