package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

public class UnSDashboardType implements Serializable, Comparable<UnSDashboardType> {
	private static final long serialVersionUID = 1L;
	/**
	 * @Fields name :设备类型的名称
	 */
	private String name;
	/**
	 * @Fields commonStrings : 设备集合
	 */
	private List<UnSDashboard> dashboards;

	private List<UnSDashboardType> dashboardTypes;

	private String typePath;

	public UnSDashboardType(String name) {
		super();
		this.name = name;
	}

	public UnSDashboardType(String name, List<UnSDashboard> dashboards,
			List<UnSDashboardType> dashboardTypes) {
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

	public List<UnSDashboard> getDashboards() {
		return dashboards;
	}

	public void setDashboards(List<UnSDashboard> dashboards) {
		this.dashboards = dashboards;
	}

	public List<UnSDashboardType> getDashboardTypes() {
		return dashboardTypes;
	}

	public void setDashboardTypes(List<UnSDashboardType> dashboardTypes) {
		this.dashboardTypes = dashboardTypes;
	}

	public int compareTo(UnSDashboardType o) {
		return Collator.getInstance(Locale.CHINA).compare(this.name,
				o.getName());
	}

}
