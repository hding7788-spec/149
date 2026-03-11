package com.glaway.mpm.visual.view.pview;

import com.ptc.pview.dg.Location;
import com.ptc.pview.pvkapp.Instance;

public class VaPviewInstance {

	private String		idPath;
	private String		name;
	private Location	location;
	private String		olFileSource;

	public String getOlFileSource() {
		return olFileSource;
	}

	public void setOlFileSource(String olFileSource) {
		this.olFileSource = olFileSource;
	}

	private Instance	instance;

	public String getIdPath() {
		return idPath;
	}

	public void setIdPath(String idPath) {
		this.idPath = idPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

}
