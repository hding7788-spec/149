package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

public class DashboardType implements Serializable, Comparable<DashboardType> {
	private static final long serialVersionUID = 1L;
	/**
	 * @Fields name :设备类型的名称
	 */
	private String name;
	/**
	 * @Fields commonStrings : 设备集合
	 */
	private List<Dashboard> dashboards;

	private List<DashboardType> dashboardTypes;

	private String typePath;

	public DashboardType(String name) {
		super();
		this.name = name;
	}

	public DashboardType(String name, List<Dashboard> dashboards,
			List<DashboardType> dashboardTypes) {
		super();
		this.name = name;
		this.dashboards = dashboards;
		this.dashboardTypes = dashboardTypes;
	}

	public String getTypePath() {
		return typePath;
	}

	public void setTypePath(String typePath) {
		this.typePath = typePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Dashboard> getDashboards() {
		return dashboards;
	}

	public void setDashboards(List<Dashboard> dashboards) {
		this.dashboards = dashboards;
	}

	public List<DashboardType> getDashboardTypes() {
		return dashboardTypes;
	}

	public void setDashboardTypes(List<DashboardType> dashboardTypes) {
		this.dashboardTypes = dashboardTypes;
	}

	public int compareTo(DashboardType o) {
		return Collator.getInstance(Locale.CHINA).compare(this.name,
				o.getName());
	}

}
