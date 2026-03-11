package ext.casc.consCheckRecords;

import java.io.Serializable;

public class TableConfigBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String gwKeyId;
	private String name;

	public TableConfigBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getGwKeyId() {
		return gwKeyId;
	}

	public void setGwKeyId(String gwKeyId) {
		this.gwKeyId = gwKeyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public TableConfigBean(String gwKeyId, String name) {
		super();
		this.gwKeyId = gwKeyId;
		this.name = name;
	}

	@Override
	public String toString() {
		return "TableConfigBean [gwKeyId=" + gwKeyId + ", name=" + name + "]";
	}

}
