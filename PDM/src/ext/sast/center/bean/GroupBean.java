package ext.sast.center.bean;

/**
 * @ Author ：LB. @ Date ：Created in 2019/3/13 @ Description： @ Modified By：
 */
public class GroupBean {

	private String iid;

	private String id;

	private String name;

	private String parent_iid;

	public String getIid() {
		return iid;
	}

	public void setIid(String iid) {
		this.iid = iid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParent_iid() {
		return parent_iid;
	}

	public void setParent_iid(String parent_iid) {
		this.parent_iid = parent_iid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((iid == null) ? 0 : iid.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parent_iid == null) ? 0 : parent_iid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupBean other = (GroupBean) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (iid == null) {
			if (other.iid != null)
				return false;
		} else if (!iid.equals(other.iid))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parent_iid == null) {
			if (other.parent_iid != null)
				return false;
		} else if (!parent_iid.equals(other.parent_iid))
			return false;
		return true;
	}
	
}
