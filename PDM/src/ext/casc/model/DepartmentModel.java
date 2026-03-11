package ext.casc.model;

import java.util.ArrayList;
import java.util.List;

public class DepartmentModel {
	private String name;

	private String dn;
	private String cn;

	private List childDeparts = new ArrayList();

	private List childMembers = new ArrayList();

	public String getDN() {
		return dn;
	}

	public void setDN(String dn) {
		this.dn = dn;
	}
	
	public String getCN() {
		return cn;
	}

	public void setCN(String cn) {
		this.cn = cn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return "Department{name=" + name + ", dn=" + dn + "}";
	}

	public List getChildDeparts() {
		return childDeparts;
	}

	public void setChildDeparts(List childDeparts) {
		this.childDeparts = childDeparts;
	}

	public List getChildMembers() {
		return childMembers;
	}

	public void setChildMembers(List childMembers) {
		this.childMembers = childMembers;
	}
}
