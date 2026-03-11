package ext.casc.workflow;

public class SelectRoleBean {
	private String TEMPLATE_NAME;
	private String ROLE_NAME ;
	private String USER ;
	private String CONCAT_ID ;
	private String ROLE_FULL_NAME ;

	public String getROLE_FULL_NAME() {
		return ROLE_FULL_NAME;
	}
	public void setROLE_FULL_NAME(String rOLE_FULL_NAME) {
		ROLE_FULL_NAME = rOLE_FULL_NAME;
	}
	public String getTEMPLATE_NAME() {
		return TEMPLATE_NAME;
	}
	public void setTEMPLATE_NAME(String tEMPLATE_NAME) {
		TEMPLATE_NAME = tEMPLATE_NAME;
	}
	public String getROLE_NAME() {
		return ROLE_NAME;
	}
	public void setROLE_NAME(String rOLE_NAME) {
		ROLE_NAME = rOLE_NAME;
	}
	public String getUSER() {
		return USER;
	}
	public void setUSER(String uSER) {
		USER = uSER;
	}
	public String getCONCAT_ID() {
		return CONCAT_ID;
	}
	public void setCONCAT_ID(String cONCAT_ID) {
		CONCAT_ID = cONCAT_ID;
	}

}
