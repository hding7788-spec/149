package ext.casc.consCheckRecords;

import java.io.Serializable;

public class ProConfigBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String gwKeyId;
	private String value;
	private String type1;

	//com.ptc.netmarkets.model.NmSimpleOid

	public ProConfigBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getGwKeyId() {
		return gwKeyId;
	}

	public void setGwKeyId(String gwKeyId) {
		this.gwKeyId = gwKeyId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType1() {
		return type1;
	}

	public void setType1(String type1) {
		this.type1 = type1;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public ProConfigBean(String gwKeyId, String value, String type1) {
		super();
		this.gwKeyId = gwKeyId;
		this.value = value;
		this.type1 = type1;
	}

	@Override
	public String toString() {
		return "ProConfigBean [gwKeyId=" + gwKeyId + ", value=" + value + ", type1=" + type1 + "]";
	}

}
