package ext.casc.model;

public class MemberModel
{
  private String name;
  private String projectreportname;
  private String dn;
  private String uid;

  public String getUid() {
	return uid;
}

public void setUid(String uid) {
	this.uid = uid;
}

public String getDN()
  {
    return dn;
  }

  public void setDN(String dn)
  {
    this.dn = dn;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getProjectReportName()
  {
    return projectreportname;
  }
  
  public void setProjectReportName(String name)
  {
    this.projectreportname = name;
  }
  public String toString()
  {
    return "Member{name=" + name + ", dn=" + dn + "}";
  }
}
