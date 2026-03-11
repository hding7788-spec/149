package ext.sast.center.productModel.bean;

import java.io.Serializable;

public class ReceivedModelTypeInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String parent_modelType_id;
	private String parent_modelType_name;
	private String model_type_id;
	private String model_type_name;
	
	private String innerId;
	private String sast_modeltypeid;
	private String sast_modeltypename;
	private String local_modeltypeid;
	private String local_modeltypename;
	
	
	public String getParent_modelType_id() {
		return parent_modelType_id;
	}
	public void setParent_modelType_id(String parent_modelType_id) {
		this.parent_modelType_id = parent_modelType_id;
	}
	public String getParent_modelType_name() {
		return parent_modelType_name;
	}
	public void setParent_modelType_name(String parent_modelType_name) {
		this.parent_modelType_name = parent_modelType_name;
	}
	public String getModel_type_id() {
		return model_type_id;
	}
	public void setModel_type_id(String model_type_id) {
		this.model_type_id = model_type_id;
	}
	public String getModel_type_name() {
		return model_type_name;
	}
	public void setModel_type_name(String model_type_name) {
		this.model_type_name = model_type_name;
	}
	public String getInnerId() {
		return innerId;
	}
	public void setInnerId(String innerId) {
		this.innerId = innerId;
	}
	public String getSast_modeltypeid() {
		return sast_modeltypeid;
	}
	public void setSast_modeltypeid(String sast_modeltypeid) {
		this.sast_modeltypeid = sast_modeltypeid;
	}
	public String getSast_modeltypename() {
		return sast_modeltypename;
	}
	public void setSast_modeltypename(String sast_modeltypename) {
		this.sast_modeltypename = sast_modeltypename;
	}
	public String getLocal_modeltypeid() {
		return local_modeltypeid;
	}
	public void setLocal_modeltypeid(String local_modeltypeid) {
		this.local_modeltypeid = local_modeltypeid;
	}
	public String getLocal_modeltypename() {
		return local_modeltypename;
	}
	public void setLocal_modeltypename(String local_modeltypename) {
		this.local_modeltypename = local_modeltypename;
	}
	
	
	
}
