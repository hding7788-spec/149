package ext.sast.center.bean;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/3/13
 * @ Description：
 * @ Modified By：
 */

public class UserGroupLinkBean {
    private String div_iid;

    private String user_iid;

    public String getDiv_iid() {
        return div_iid;
    }

    public void setDiv_iid(String div_iid) {
        this.div_iid = div_iid;
    }

    public String getUser_iid() {
        return user_iid;
    }

    public void setUser_iid(String user_iid) {
        this.user_iid = user_iid;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((div_iid == null) ? 0 : div_iid.hashCode());
		result = prime * result + ((user_iid == null) ? 0 : user_iid.hashCode());
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
		UserGroupLinkBean other = (UserGroupLinkBean) obj;
		if (div_iid == null) {
			if (other.div_iid != null)
				return false;
		} else if (!div_iid.equals(other.div_iid))
			return false;
		if (user_iid == null) {
			if (other.user_iid != null)
				return false;
		} else if (!user_iid.equals(other.user_iid))
			return false;
		return true;
	}
    
    
}
