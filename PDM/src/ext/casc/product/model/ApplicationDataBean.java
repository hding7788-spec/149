package ext.casc.product.model;

import wt.content.ApplicationData;

import java.io.Serializable;

/**
 * @describe in order to resolve EPMDocument's primary content has no name
 *           problem
 * @author Pan,JianTao
 * @date 2012-6-26
 * 
 */
public class ApplicationDataBean implements Serializable {

	private static final long serialVersionUID = 1L;
	ApplicationData ad;
	String name;
	String folderPath;

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public ApplicationData getAd() {
		return ad;
	}

	public void setAd(ApplicationData ad) {
		this.ad = ad;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
