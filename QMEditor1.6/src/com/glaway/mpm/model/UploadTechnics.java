package com.glaway.mpm.model;

/**
 * @author xuehu 工艺批量上载时的对象
 */
public class UploadTechnics {
	private String partNumber;
	private String technicsNumber;
	private String technicsName;
	private String technicsCategory;

	public String getTechnicsNumber() {
		return technicsNumber;
	}

	public void setTechnicsNumber(String technicsNumber) {
		this.technicsNumber = technicsNumber;
	}

	public String getTechnicsName() {
		return technicsName;
	}

	public void setTechnicsName(String technicsName) {
		this.technicsName = technicsName;
	}

	public String getTechnicsCategory() {
		return technicsCategory;
	}

	public void setTechnicsCategory(String technicsCategory) {
		this.technicsCategory = technicsCategory;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof UploadTechnics)) {
			return false;
		}
		UploadTechnics technics = (UploadTechnics) obj;
		if (technicsName == null || technicsNumber == null) {
			return false;
		}
		return technicsNumber.equals(technics.getTechnicsNumber())
				&& technicsName.equals(technics.getTechnicsName());
	}
}
