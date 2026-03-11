package ext.sast.catalog;

/**
 * 目录条目与编码的关联关系
 * @author Sean
 *
 */
public class GLCIPartLink {

	private String partNumber;
	
	private String catalogItemNumber;

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getCatalogItemNumber() {
		return catalogItemNumber;
	}

	public void setCatalogItemNumber(String catalogItemNumber) {
		this.catalogItemNumber = catalogItemNumber;
	}
}
